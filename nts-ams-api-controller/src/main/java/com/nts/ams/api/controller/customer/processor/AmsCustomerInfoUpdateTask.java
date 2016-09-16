package com.nts.ams.api.controller.customer.processor;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.IConstants.CORPORATION_TYPE;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Security;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.TrsUtil;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.model.ProfileModel;
import phn.nts.ams.fe.mt4.MT4Manager;

import com.nts.ams.api.controller.common.Constant;
import com.nts.ams.api.controller.customer.bean.CustomerInfoUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;
import com.phn.mt.common.constant.IConstant;
import com.phn.mt.common.entity.UserRecord;

/**
 * @description AmsCustomerInfoUpdate Task
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 10, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerInfoUpdateTask implements Runnable {
	private Logit log = Logit.getInstance(AmsCustomerInfoUpdateTask.class);
	private CustomerInfoUpdateRequestWraper wraper;
	private IAccountManager accountManager = null;
	private IProfileManager profileManager = null;
	
	private ProfileModel model = new ProfileModel();
	private CustomerInfo currentCustomerInfo;
	private ExecutorService executorService;
	private String requestId;
	
	public AmsCustomerInfoUpdateTask(CustomerInfoUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerInfoUpdateRequest request = wraper.getRequest();
			
			requestId = wraper.getResponseBuilder().getId();
			AmsCustomerInfoUpdateRequest requestToLog = request;
			if(requestToLog.hasCustomerInfo() && requestToLog.getCustomerInfo().hasNewPassword()) {
				//hide password from log
				AmsCustomerInfo.Builder cusBuilder = requestToLog.getCustomerInfo().toBuilder();
				cusBuilder.setNewPassword("******");
				cusBuilder.setPassword("******");
				requestToLog = requestToLog.toBuilder().setCustomerInfo(cusBuilder).build();
			}
			
			log.info("[start] handle AmsCustomerInfoUpdateRequest, requestId: " + wraper.getResponseBuilder().getId() +  ", " + requestToLog);
			
			
			AmsCustomerInfo amsCustomerInfo = request.getCustomerInfo();
			String customerId = amsCustomerInfo.getCustomerId();
			
			Result result = Result.FAILED;
			switch (request.getActionType()) {
			case INSERT:
				break;
			case UPDATE:
				if(!StringUtil.isEmpty(customerId)) {
					currentCustomerInfo = profileManager.getCustomerInfo(customerId);
					
					if(currentCustomerInfo != null) {
						CustomerInfo oldCustomerInfo = (CustomerInfo) ObjectCopy.copy(currentCustomerInfo);
						CustomerInfo newCustomerInfo = Converter.convertCustomerInfo(request.getCustomerInfo(), oldCustomerInfo);
						log.info("Converted CustomerInfo: " + newCustomerInfo);
						
						model.setCurrentCusInfo(newCustomerInfo);
						model.setCustomerInfo(newCustomerInfo);

						//Check for changeValue
						scanForChangeCustomerInfo(currentCustomerInfo, model.getCustomerInfo()); //TRSSC-1197, scan first then validate
						//Update changeValue
						if(validateProfileInfo(model)) {
							result = updateProfile(model);
						}
					} else {
						//CustomerId does not exist
						log.info("Customer does not exist, customerId: " + customerId);
					}
				} else
					log.info("CustomerId is empty");
				break;
			case DELETE:
				break;
			default:
				break;
			}
			
			//Get lastest info of CustomerInfo to response to Client
			currentCustomerInfo = profileManager.getCustomerInfo(customerId);
			amsCustomerInfo = Converter.convertCustomerInfo(currentCustomerInfo);
			
			//Response to client	
			RpcMessage response = createRpcMessage(amsCustomerInfo, result, model.getErrorMessage());
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			
			log.info("[end] handle AmsCustomerInfoUpdateRequest, requestId: " + wraper.getResponseBuilder().getId() 
					+ ", ErrorMessage: " + model.getErrorMessage() + " - " + AmsApiControllerMng.getMsg(model.getErrorMessage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerInfoUpdateProcessor().onComplete(wraper);
		}
	}
	
	/**
	 * Update Profile　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 16, 2015
	 * @MdDate
	 */
	public Result updateProfile(ProfileModel model) {
		try {
			log.info("start update profile, ProfileModel: " + model);
			CustomerInfo customerInfo = model.getCustomerInfo();
			String customerId = customerInfo.getCustomerId();
			
			//Check update Password
			if(customerInfo.isChangePass()) {
				customerInfo.setChangePasswordFlag(true);
				// Now password and MT4 password is the same
				model.setIsChangeMt4Pass(IConstants.ENABLE_FLG.ENABLE.toString());
				model.setMt4NewPass(customerInfo.getNewPassword());
				model.setMt4InvestorNewPass(customerInfo.getNewPassword());
			}
			
			//SYNC Password to MT4
			String isChangeMt4Pass = model.getIsChangeMt4Pass();
			if(IConstants.ENABLE_FLG.ENABLE.toString().equals(isChangeMt4Pass) ){
				log.info("[start] sync password to MT4");
				//For TRS have 3 service type Social, FX, Demo therefor update three MT4 account
				CustomerServicesInfo customerServiceDemoMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.DEMO_FXCD);
				//CustomerServicesInfo customerServiceMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.FX);
				CustomerServicesInfo customerServiceSocialMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
				
				String password = model.getMt4NewPass();
				String investorPass = model.getMt4InvestorNewPass();
				Integer resultMt4Demo = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				
				// Update account demo
				if(customerServiceDemoMt4 != null){
					log.info("[start] sync password to MT4 Demo");
					String loginIdDemo = customerServiceDemoMt4.getCustomerServiceId();
					UserRecord userRecord = new UserRecord();
					userRecord.setLogin(MathUtil.parseInt(loginIdDemo));
					userRecord.setPassword(password);
					userRecord.setPasswordInvestor(investorPass);
					userRecord.setEnable(UserRecord.NO_UPDATE);
					userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
					userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
					resultMt4Demo = MT4Manager.getInstance().updateDemoAccountMt4(userRecord);
					log.info("[end] sync password to MT4 Demo, Result: " + resultMt4Demo);
				}
				
				// Update account FX
				Integer resultMt4 = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				//Remove MT4 FX account in social system
//				if(customerServiceMt4 != null){
//					log.info("[start] sync password to MT4 FX");
//					String loginId = customerServiceMt4.getCustomerServiceId();
//					resultMt4 = MT4Manager.getInstance().changePassword(MathUtil.parseInt(loginId), password, investorPass);
//					log.info("[end] sync password to MT4 FX, Result: " + resultMt4);
//				}
					
				// Update account Social
				Integer resultMt4Social = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				if(customerServiceSocialMt4 != null){
					log.info("[start] sync password to MT4 Social");
					String loginId = customerServiceSocialMt4.getCustomerServiceId();
					resultMt4Social = MT4Manager.getInstance().changePassword(MathUtil.parseInt(loginId), password, investorPass);
					log.info("[end] sync password to MT4 Social, Result: " + resultMt4Social);
				}
				
				if(!resultMt4.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS) || !resultMt4Demo.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS) || !resultMt4Social.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS)){
					model.setErrorMessage("MSG_TRS_NAB_0010");
					log.info("[end] sync password to MT4, Result: FAIL");
					return Result.FAILED;
				}
				log.info("[end] sync password to MT4, Result: OK");
				//Need CustomerAuthen cache
				model.getCurrentCusInfo().setNeedReloadCache(true);
			}

			
			//Update Profile to DB
			log.info("[start] updateProfile, customerInfo: " + customerInfo);
			Integer updateResult = getProfileManager().updateProfile(customerInfo);
			log.info("[end] updateProfile, updateResult: " + updateResult);
			
			//Check Update result
			if(IConstant.ACCOUNT_UPDATE_SUCCESS != updateResult) {
				model.setErrorMessage("MSG_TRS_NAB_0010");
				log.info("Update Result: " + updateResult);
				return Result.FAILED;
			}
			
			// Synchronize customer information to Salesforce and send mail to CS and Customer
			SyncDataAndSendMailSubTask subtask = new SyncDataAndSendMailSubTask(customerInfo, requestId);
			getExecutorService().submit(subtask);
			
			//Set messageCode
			model.setErrorMessage(getSuccessMessageCode(customerInfo));
			return Result.SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.setErrorMessage("MSG_TRS_NAB_0010");
			return Result.FAILED;
		} finally {
			try {
				//Reload Customer Authenticate to cache
				if(model.getCustomerInfo().isNeedReloadCache()) {
					log.info("Customer has changed pass or loginId, must remove CustomerAuthen from Redis");
					AmsApiControllerMng.getDataCache().removeAmsCustomerAuthenInfo(model.getCustomerInfo().getOldLoginId());
//					AmsApiControllerMng.getDataCache().getAmsCustomerAuthenInfo(model.getCustomerInfo().getLoginId());
				}
			} catch (Exception e) {
				log.error("Fail reload CustomerAuthen to Redis", e);
			}
		}
	}

	/**
	 * Validate Profile Info　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 16, 2015
	 * @MdDate
	 */
	private boolean validateProfileInfo(ProfileModel model) {
		CustomerInfo customerInfo = model.getCustomerInfo();
		
		//Check pass
		if(customerInfo.isChangePass()) {
			String newPassword = customerInfo.getNewPassword();
			String comfirmedPassword = customerInfo.getComfirmedPassword();
			String indentifyPassword = customerInfo.getIdentifyPassword();
			String md5IndentifyPassword = null;
			try{
				md5IndentifyPassword = Security.MD5(indentifyPassword);
				customerInfo.setMd5IndentifyPassword(md5IndentifyPassword);
			}catch(Exception ex) {
				log.error(ex.getMessage(),ex);
			}
			
			if(!validatePassword(newPassword, comfirmedPassword, indentifyPassword, md5IndentifyPassword)) {
				log.info("validate Password fail");
				return false;
			}
		}
		
		String tel1 = customerInfo.getTel1();
		String tel2 = customerInfo.getTel2();
		String additionalMail = getAdditionalMail(customerInfo);
		 
		String mailMain = customerInfo.getMailMain();
		if(!validateBaseCustomerInfo(customerInfo, tel1, tel2, additionalMail, mailMain)) {
			log.info("Validate for Base CustomerInfo fail");
			return false;
		}
		
		//Check Purpose Flg
		boolean purposeShortTermFlg = customerInfo.isPurposeShortTermFlg();
		boolean purposeLongTermFlg = customerInfo.isPurposeLongTermFlg();
		boolean purposeExchangeFlg = customerInfo.isPurposeExchangeFlg();
		boolean purposeSwapFlg = customerInfo.isPurposeSwapFlg();
		boolean purposeHedgeAssetFlg = customerInfo.isPurposeHedgeAssetFlg();
		boolean purposeHighIntFlg = customerInfo.isPurposeHighIntFlg();
		boolean purposeEconomicFlg = customerInfo.isPurposeEconomicFlg();
		
		if(!purposeShortTermFlg && !purposeLongTermFlg && !purposeExchangeFlg && !purposeSwapFlg && !purposeHedgeAssetFlg
				&&! purposeHighIntFlg && !purposeEconomicFlg) {
			model.setErrorMessage("MSG_SC_013");
			log.info("PurposeFlag must be set at least one");
			return false;
		}
		
		//Check info
		if(CORPORATION_TYPE.INDIVIDUAL.equals(customerInfo.getCorporationType())){
			//Validate Individual info
			if(!validateIndividualInfo(customerInfo)) {
				log.info("Validate for Individual fail");
				return false;
			}
		} else {
			//Validate Corporation info
			if(!validateCorporationInfo(customerInfo)) {
				log.info("Validate for Corporation fail");
				return false;
			}
		}
			
		if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
			//Individual Name
			String firstName = !StringUtil.isEmpty(customerInfo.getFirstName()) ? customerInfo.getFirstName() : "";
			String lastName = !StringUtil.isEmpty(customerInfo.getLastName()) ? customerInfo.getLastName() : "";
			customerInfo.setFullName(firstName + "　" + lastName);
			
			//Individual Address
			String houseNumber = (currentCustomerInfo.getHouseNumber() != null && !currentCustomerInfo.getHouseNumber().equals("null")) ? currentCustomerInfo.getHouseNumber() + "" : "";
			customerInfo.setAddress(customerInfo.getPrefecture() + customerInfo.getCity() + customerInfo.getSection() + customerInfo.getBuildingName() + houseNumber);
		} else {
			//Corp Name
			String repFirstName = !StringUtil.isEmpty(customerInfo.getCorpRepFirstname()) ? customerInfo.getCorpRepFirstname() : "";
			String repLastName = !StringUtil.isEmpty(customerInfo.getCorpRepLastname()) ? customerInfo.getCorpRepLastname() : "";
			customerInfo.setCorpRepFullname(repFirstName + "　" + repLastName);
			
			//Corp Address
			String houseNumber = (currentCustomerInfo.getHouseNumber() != null && !currentCustomerInfo.getHouseNumber().equals("null")) ? currentCustomerInfo.getHouseNumber() +"" : "";
			String buildingName = customerInfo.getBuildingName() == null ? "" : customerInfo.getBuildingName();
			customerInfo.setAddress(customerInfo.getPrefecture() + customerInfo.getCity() + customerInfo.getSection() + buildingName + houseNumber);
			
			//Set CorpPicAddress
			buildingName = customerInfo.getCorpPicBuildingName() == null ? "" : customerInfo.getCorpPicBuildingName();
			customerInfo.setCorpPicAddress(customerInfo.getCorpPicPrefecture() + customerInfo.getCorpPicCity() + customerInfo.getCorpPicSection() + buildingName + houseNumber);
		}
		
		// Add for #20778
		if(customerInfo.getCorporationType().equals(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER)){
            standardizeDataBeneficOwner(customerInfo);
		}
		
		return true;
	}

    private void standardizeDataBeneficOwner(CustomerInfo customerInfo) {
        if (customerInfo.getBeneficOwnerFlg() != null && customerInfo.getBeneficOwnerFlg() == 0) {
            setEmptyForBeneficOwner1(customerInfo);
            setEmptyForBeneficOwner2(customerInfo);
            setEmptyForBeneficOwner3(customerInfo);
        }

        if (customerInfo.getBeneficOwnerFlg2() != null && customerInfo.getBeneficOwnerFlg2() == 0) {
            setEmptyForBeneficOwner2(customerInfo);
            setEmptyForBeneficOwner3(customerInfo);
        }

        if (customerInfo.getBeneficOwnerFlg3() != null && customerInfo.getBeneficOwnerFlg3() == 0) {
            setEmptyForBeneficOwner3(customerInfo);
        }
    }

    private void setEmptyForBeneficOwner3(CustomerInfo customerInfo) {
        customerInfo.setBeneficOwnerFlg3(0);
        customerInfo.setBeneficOwnerFullname3("");
        customerInfo.setBeneficOwnerFullnameKana3("");
        customerInfo.setBeneficOwnerEstablishDate3("");
        customerInfo.setBeneficOwnerZipcode3("");
        customerInfo.setBeneficOwnerPrefecture3("");
        customerInfo.setBeneficOwnerSection3("");
        customerInfo.setBeneficOwnerCity3("");
        customerInfo.setBeneficOwnerBuildingName3("");
        customerInfo.setBeneficOwnerTel3("");
        customerInfo.setBeneficOwnerFirstname3("");
        customerInfo.setBeneficOwnerLastname3("");
        customerInfo.setBeneficOwnerFirstnameKana3("");
        customerInfo.setBeneficOwnerLastnameKana3("");
    }

    private void setEmptyForBeneficOwner2(CustomerInfo customerInfo) {
        customerInfo.setBeneficOwnerFlg2(0);
        customerInfo.setBeneficOwnerFullname2("");
        customerInfo.setBeneficOwnerFullnameKana2("");
        customerInfo.setBeneficOwnerEstablishDate2("");
        customerInfo.setBeneficOwnerZipcode2("");
        customerInfo.setBeneficOwnerPrefecture2("");
        customerInfo.setBeneficOwnerSection2("");
        customerInfo.setBeneficOwnerCity2("");
        customerInfo.setBeneficOwnerBuildingName2("");
        customerInfo.setBeneficOwnerTel2("");
        customerInfo.setBeneficOwnerFirstname2("");
        customerInfo.setBeneficOwnerLastname2("");
        customerInfo.setBeneficOwnerFirstnameKana2("");
        customerInfo.setBeneficOwnerLastnameKana2("");
    }

    private void setEmptyForBeneficOwner1(CustomerInfo customerInfo) {
        customerInfo.setBeneficOwnerFlg(0);
        customerInfo.setBeneficOwnerFullname("");
        customerInfo.setBeneficOwnerFullnameKana("");
        customerInfo.setBeneficOwnerEstablishDate("");
        customerInfo.setBeneficOwnerZipcode("");
        customerInfo.setBeneficOwnerPrefecture("");
        customerInfo.setBeneficOwnerSection("");
        customerInfo.setBeneficOwnerCity("");
        customerInfo.setBeneficOwnerBuildingName("");
        customerInfo.setBeneficOwnerTel("");
        customerInfo.setBeneficOwnerFirstname("");
        customerInfo.setBeneficOwnerLastname("");
        customerInfo.setBeneficOwnerFirstnameKana("");
        customerInfo.setBeneficOwnerLastnameKana("");
    }

    private boolean validateIndividualInfo(CustomerInfo customerInfo) {
		log.info("Validate for Individual");
		Pattern pattern = Pattern.compile(Constant.REGEX_SPECIAL);
		
		// For INDIVIDUAL
		if(customerInfo.isChangeCustomerName()){
			String firstName = customerInfo.getFirstName();
			String lastName = customerInfo.getLastName();
			String firstNameKana = customerInfo.getFirstNameKana();
			String lastNameKana = customerInfo.getLastNameKana();
			
			if(!validateCorpRefName(pattern, firstName, lastName, firstNameKana, lastNameKana))
				return false;
		}
		
		if(customerInfo.isChangeAddress()){
			String zipCode = customerInfo.getZipcode();
			String preficture = customerInfo.getPrefecture();
			String city = customerInfo.getCity();
			String section = customerInfo.getSection();
			
			if(!validateAddress(zipCode, preficture, city, section)){
				return false;
			}
		}
		
		return true;
	} 
	
	private boolean validateCorporationInfo(CustomerInfo customerInfo) {
		log.info("Validate for Corporation");
		Pattern pattern = Pattern.compile(Constant.REGEX_SPECIAL);
		
		// For corporation
		if(customerInfo.isChangeCorpName()){
			String fullName = customerInfo.getCorpFullname();
			String fullNameKana = customerInfo.getCorpFullnameKana();
			if(StringUtil.isEmpty(fullName)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("CorpFullname is empty");
				return false;
			}
			
			if(StringUtil.isEmpty(fullNameKana)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("FullNameKana is empty");
				return false;
			}
			
			if(pattern.matcher(fullName).find()) {
				model.setErrorMessage("MSG_SC_065");
				log.info("Invalid CorpFullname: " + fullName);
				return false;
			}
			
			if(pattern.matcher(fullNameKana).find()) {
				model.setErrorMessage("MSG_SC_065");
				log.info("Invalid CorpFullnameKana: " + fullNameKana);
				return false;
			}
		}
		
		if(customerInfo.isChangeCorpAddress()){
			String zipCode = customerInfo.getZipcode();
			String preficture = customerInfo.getPrefecture();
			String city = customerInfo.getCity();
			String section = customerInfo.getSection();
			
			if(!validateAddress(zipCode, preficture, city, section)){
				return false;
			}			
		}
		
		//Set address
		String houseNumber = (customerInfo.getHouseNumber() != null && !customerInfo.getHouseNumber().equals("null")) ? customerInfo.getHouseNumber()+"" : "";
		customerInfo.setAddress(customerInfo.getPrefecture() + " " + customerInfo.getCity() + " " + customerInfo.getSection() + " " + customerInfo.getBuildingName() + " " + houseNumber);
		
		if(customerInfo.isChangeCorpRepName()){
			String corpRepFirstname = customerInfo.getCorpRepFirstname();
			String corpRepLastname = customerInfo.getCorpRepLastname();
			String corpRepFirstnameKana = customerInfo.getCorpRepFirstnameKana();
			String corpRepLastnameKana = customerInfo.getCorpRepLastnameKana();
			
			if(!validateCorpRepName(pattern, corpRepFirstname, corpRepLastname, corpRepFirstnameKana, corpRepLastnameKana)){
				return false;
			}
		}
		
		if(customerInfo.isChangeCorpOwnerName()){
			if(!validateCorpOwnerName(customerInfo, pattern)) {
				return false;
			}
		}
		
		if(customerInfo.isChangeCorpRefName()){
			String corpPicLastname = customerInfo.getCorpPicLastname();
			String corpPicFirstname = customerInfo.getCorpPicFirstname();
			String corpPicLastnameKana = customerInfo.getCorpPicLastnameKana();
			String corpPicFirstnameKana = customerInfo.getCorpPicFirstnameKana();
			
			if(!validateCorpRefName(pattern, corpPicLastname, corpPicFirstname, corpPicLastnameKana, corpPicFirstnameKana)){
				return false;
			}
		}
		
		if(customerInfo.isChangeCorpRefAddress()){
			String corpPicZipcode = customerInfo.getCorpPicZipcode();
			String preficture = customerInfo.getCorpPicPrefecture();
			String corpPicCity = customerInfo.getCorpPicCity();
			String corpPicSection = customerInfo.getCorpPicSection();
			
			if(!validateAddress(corpPicZipcode, preficture,	corpPicCity, corpPicSection)){
				return false;
			}
		}
		
		
		
		String corpPicTel = customerInfo.getCorpPicTel();
		if(StringUtil.isEmpty(corpPicTel)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpPicTel is empty");
			return false;
		}
		
		return true;
	} 
	
	private boolean validateBaseCustomerInfo(CustomerInfo customerInfo,	String tel1, String tel2, String additionalMail, String mailMain) {
		log.info("Validate BaseCustomerInfo, customerId: " + customerInfo.getCustomerId() + ", tel1: " + tel1 + ", tel2: " + tel2 
				+ ", additionalMail: " +  additionalMail + ", mailMain: " + mailMain);
		if(StringUtil.isEmpty(tel1)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("Tel1 is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(mailMain)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("MailMain is empty");
			return false;
		}
		
		if (!TrsUtil.isEmail(mailMain)) {
			model.setErrorMessage("NAB007");
			log.info("Invalid MailMain: " + mailMain);
			return false;
		}
		
		// Check loginId existed 
		if(StringUtil.isDifferent(mailMain, currentCustomerInfo.getMailMain()) 
				&& profileManager.mailExisted(customerInfo.getCustomerId(), mailMain)) {
			model.setErrorMessage("MSG_NAB033");
			log.info("MailMain exist: " + mailMain);
			return false;
		}
		
		if(!StringUtil.isEmpty(tel1) && !tel1.matches("^\\d+$")) {
			model.setErrorMessage("nts.ams.fe.label.customer_information.phone.invalid");
			log.info("Invalid Tel1: " + tel1);
			return false;
		}
		
		if(!StringUtil.isEmpty(tel2) && !tel2.matches("^\\d+$")) {
			model.setErrorMessage("nts.ams.fe.label.customer_information.phone.invalid");
			log.info("Invalid Tel2: " + tel2);
			return false;
		}
		
		if(!StringUtil.isEmpty(tel1) && (tel1.length() < 7 || tel1.length() > 11)) {
			model.setErrorMessage("MSG_TRS_NAF_0038");
			log.info("Invalid Tel1: " + tel1);
			return false;
		}
		
		if(!StringUtil.isEmpty(tel2) && (tel2.length() < 7 || tel2.length() > 11)) {
			model.setErrorMessage("MSG_TRS_NAF_0038");
			log.info("Invalid Tel2: " + tel2);
			return false;
		}
		
		if (!StringUtil.isEmpty(additionalMail) && !TrsUtil.isEmail(additionalMail)) {
			model.setErrorMessage("NAB007");
			log.info("Invalid additionalMail: " + additionalMail);
			return false;
		}
		
		if(StringUtil.isDifferent(additionalMail, getAdditionalMail(currentCustomerInfo)) 
				&& !StringUtil.isEmpty(additionalMail) && profileManager.mailExisted(customerInfo.getCustomerId(), additionalMail)){				
			model.setErrorMessage("MSG_NAB033");
			log.info("AdditionalMail exist: " + additionalMail);
			return false;
		}
		
		if(!StringUtil.isEmpty(additionalMail) && mailMain.equalsIgnoreCase(additionalMail)){
			model.setErrorMessage("MSG_NAB033");
			log.info("MailMain equals AdditionalMail: " + additionalMail);
			return false;
		}

		if(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER == customerInfo.getCorporationType().intValue()){
			String mailMobile = customerInfo.getCorpPicMailMobile();
			if(!StringUtil.isEmpty(mailMobile) && mailMain.equalsIgnoreCase(mailMobile)){
				model.setErrorMessage("MSG_NAB033");
				log.info("Invalid AdditionalMail: " + mailMobile);
				return false;
			}
		}
		return true;
	}
	
	private boolean validateCorpRefName(Pattern pattern, String corpPicLastname,
			String corpPicFirstname, String corpPicLastnameKana, String corpPicFirstnameKana) {
		log.info("Validate CorpRefName, pattern: " + pattern + ", corpPicLastname: " + corpPicLastname + ", corpPicFirstname: " + corpPicFirstname 
				+ ", corpPicLastnameKana: " + corpPicLastnameKana + ", corpPicFirstnameKana: " + corpPicFirstnameKana);
		
		if(StringUtil.isEmpty(corpPicLastname)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpPicLastname is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpPicFirstname)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpPicFirstname is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpPicLastnameKana)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpPicLastnameKana is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpPicFirstnameKana)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpPicFirstnameKana is empty");
			return false;
		}
		
		if(pattern.matcher(corpPicLastname).find()) {
			model.setErrorMessage("MSG_SC_065");
			log.info("Invalid CorpPicLastname: " + corpPicLastname);
			return false;
		}
		
		if(pattern.matcher(corpPicFirstname).find()) {
			model.setErrorMessage("MSG_SC_065");
			log.info("Invalid CorpPicFirstname: " + corpPicFirstname);
			return false;
		}
		
		if(pattern.matcher(corpPicLastnameKana).find()) {
			model.setErrorMessage("MSG_SC_065");
			log.info("Invalid CorpPicLastnameKana: " + corpPicLastnameKana);
			return false;
		}
		
		if(pattern.matcher(corpPicFirstnameKana).find()) {
			model.setErrorMessage("MSG_SC_065");
			log.info("Invalid CorpPicFirstnameKana: " + corpPicFirstnameKana);
			return false;
		}
		
		return true;
	}
	
	private boolean validateAddress(String zipCode, String preficture, String city, String section) {
		log.info("Validate Address, zipCode: " + zipCode + ", preficture: " + preficture + ", city: " + city + ", section: " + section);
		
		if(StringUtil.isEmpty(zipCode)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("ZipCode is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(preficture) || preficture.equals("-1")) {
			model.setErrorMessage("MSG_SC_013");
			log.info("Preficture is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(city)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("City is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(section)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("Section is empty");
			return false;
		}
		
		if(!zipCode.matches("^\\d+$")) {
			model.setErrorMessage("MSG_NAB200");
			log.info("Invalid zipcode: " + zipCode);
			return false;
		}
		
		return true;
	}
	
	private boolean validateCorpRepName(Pattern pattern, String corpRepFirstname,
			String corpRepLastname, String corpRepFirstnameKana, String corpRepLastnameKana) {
		log.info("validateCorpRepName, pattern: " + pattern + ", corpRepFirstname: " + corpRepFirstname + ", corpRepLastname: " + corpRepLastname
				+ ", corpRepFirstnameKana: " + corpRepFirstnameKana + ", corpRepLastnameKana: " + corpRepLastnameKana);
		
		if(StringUtil.isEmpty(corpRepFirstname)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpRepFirstname is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepLastname)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpRepLastname is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepFirstnameKana)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpRepFirstnameKana is empty");
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepLastnameKana)) {
			model.setErrorMessage("MSG_SC_013");
			log.info("CorpRepLastnameKana is empty");			
			return false;
		}

		if(pattern.matcher(corpRepFirstname).find()) {
			model.setErrorMessage("MSG_NAB053");
			log.info("Invalid CorpRepFirstname: " + corpRepFirstname);
			return false;
		}

		if(pattern.matcher(corpRepLastname).find()) {
			model.setErrorMessage("MSG_NAB053");
			log.info("Invalid CorpRepLastname: " + corpRepLastname);
			return false;
		}
		
		if(pattern.matcher(corpRepFirstnameKana).find()) {
			model.setErrorMessage("MSG_NAB053");
			log.info("Invalid CorpRepFirstnameKana: " + corpRepFirstnameKana);
			return false;
		}

		if(pattern.matcher(corpRepLastnameKana).find()) {
			model.setErrorMessage("MSG_NAB053");
			log.info("Invalid CorpRepLastnameKana: " + corpRepLastnameKana);
			return false;
		}
		
		return true;
	}

	private boolean validateCorpOwnerName(CustomerInfo customerInfo, Pattern pattern) {
		if(customerInfo.getBeneficOwnerFlg() == 1) {
			log.info("Validate CorpOwnerName, customerInfo: " + customerInfo + ", pattern: " + pattern);
			
			String beneficOwnerFullname = customerInfo.getBeneficOwnerFullname();
			String beneficOwnerFullnameKana = customerInfo.getBeneficOwnerFullnameKana();
			
			if(StringUtil.isEmpty(beneficOwnerFullname)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerFullname is empty");
				return false;
			}
			
			if(StringUtil.isEmpty(beneficOwnerFullnameKana)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerFullnameKana is empty");
				return false;
			}
		
			if(pattern.matcher(beneficOwnerFullname).find()) {
				model.setErrorMessage("MSG_SC_065");
				log.info("Invalid BeneficOwnerFullname: " + beneficOwnerFullname);
				return false;
			}
		
			if(pattern.matcher(beneficOwnerFullnameKana).find()) {
				model.setErrorMessage("MSG_NAB053");
				log.info("Invalid BeneficOwnerFullnameKana: " + beneficOwnerFullnameKana);
				return false;
			}
			
			String beneficOwnerZipcode = customerInfo.getBeneficOwnerZipcode();
			String beneficOwnerPrefecture = customerInfo.getBeneficOwnerPrefecture();
			String beneficOwnerCity = customerInfo.getBeneficOwnerCity();
			String beneficOwnerSection = customerInfo.getBeneficOwnerSection();
			String beneficOwnerTel = customerInfo.getBeneficOwnerTel();
			
			if(StringUtil.isEmpty(beneficOwnerZipcode)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerZipcode is empty");
				return false;
			}
			
			if(StringUtil.isEmpty(beneficOwnerPrefecture) || beneficOwnerPrefecture.equals("-1")) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerPrefecture is empty");
				return false;
			}
			
			if(StringUtil.isEmpty(beneficOwnerCity)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerCity is empty");
				return false;
			}
			
			if(StringUtil.isEmpty(beneficOwnerSection)) {
				model.setErrorMessage("MSG_SC_013");
				log.info("BeneficOwnerSection is empty");
				return false;
			}
			
			if(!beneficOwnerZipcode.matches("^\\d+$")) {
				model.setErrorMessage("MSG_NAB200");
				log.info("Invalid BeneficOwnerZipcode: " + beneficOwnerZipcode);
				return false;
			}
			
			if(!StringUtil.isEmpty(beneficOwnerTel) && (beneficOwnerTel.length() < 7 || beneficOwnerTel.length() > 11)) {
				model.setErrorMessage("MSG_TRS_NAF_0038");
				log.info("Invalid beneficOwnerTel: " + beneficOwnerTel);
				return false;
			}
		}
		return true;
	}
	
	private boolean validatePassword(String newPassword, String comfirmedPassword,
			String indentifyPassword, String md5IndentifyPassword) {
		
		// check if loginpass is blank
		if(StringUtil.isEmpty(indentifyPassword)){				
			model.setErrorMessage("MSG_SC_013");
			log.info("IndentifyPassword is empty");
			return false;
		}
		
		
		if(currentCustomerInfo != null) {
			// if password is no change, set change password flag = false
			if(!currentCustomerInfo.getLoginPass().equals(md5IndentifyPassword)) {
				model.setErrorMessage("MSG_NAB083");
				log.info("IndentifyPassword is wrong!");
				return false;
			}
		}
		
		// check if new input password is blank
		if(StringUtil.isEmpty(newPassword)){				
			model.setErrorMessage("MSG_SC_013");
			log.info("NewPassword is empty!");
			return false;
		} else if(!validateComplexPassword(newPassword)) {
			model.setErrorMessage("MSG_SC_079");
			log.info("NewPassword is NOT enough Complex!");
			return false;
		}
		
		return true;
	}
	
	private boolean validateComplexPassword(String password) {
		password = password.trim();
		if (StringUtils.isBlank(password))
			return false;
		
		if (password.length() < 6 || password.length() > 12)
			return false;
		
		/*
		 * add more condition for validation password
		 */
		// End adding condition
		
		int digitCnt = 0;
		int charCount = 0;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if ('0' <= c && c <= '9')
				digitCnt++;
			else if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'))
				charCount++;
		}
		
		if (digitCnt < 1 || charCount < 1){
			return false;
		}
		
		
		return true;
	}
	
	/**
	 * Scan For Change information　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 24, 2015
	 * @MdDate
	 */
	public void scanForChangeCustomerInfo(CustomerInfo curCus, CustomerInfo newCus) {
		log.info("[start] scan for changed CustomerInfo, OldCustomerInfo: " + curCus);
		if(curCus == null || newCus == null)
			return;
		
		//isChangePass
		if(!StringUtil.isEmpty(newCus.getNewPassword())) {
			newCus.setChangePass(true);
		}
		
		//isChangeMailMain
		if(StringUtil.isDifferent(curCus.getMailMain(), newCus.getMailMain())) {
			newCus.setChangeMailMain(true);
		}
				
		//isChangeCustomerName
		if(StringUtil.isDifferent(curCus.getFirstName(), newCus.getFirstName())
				|| StringUtil.isDifferent(curCus.getLastName(), newCus.getLastName())
				|| StringUtil.isDifferent(curCus.getFirstNameKana(), newCus.getFirstNameKana()) 
				|| StringUtil.isDifferent(curCus.getLastNameKana(), newCus.getLastNameKana()))
			newCus.setChangeCustomerName(true);
		
		//isChangeAddress - isChangeCorpAddress
		if(StringUtil.isDifferent(curCus.getZipcode(), newCus.getZipcode())
				|| StringUtil.isDifferent(curCus.getPrefecture(), newCus.getPrefecture())
				|| StringUtil.isDifferent(curCus.getCity(), newCus.getCity()) 
				|| StringUtil.isDifferent(curCus.getSection(), newCus.getSection())
				|| StringUtil.isDifferent(curCus.getBuildingName(), newCus.getBuildingName())) {
			newCus.setChangeAddress(true);
			newCus.setChangeCorpAddress(true);
		}
		
		//isChangeCorpName
		if(StringUtil.isDifferent(curCus.getCorpFullname(), newCus.getCorpFullname())
				|| StringUtil.isDifferent(curCus.getCorpFullnameKana(), newCus.getCorpFullnameKana())) {
			newCus.setChangeCorpName(true);
		}

		//isChangeCorpOwnerName
		//[TRSGAP-116-quyen.le.manh]Jul 25, 2016A - Start Change the information of BeneficOwner immediately
		if(newCus.getCorporationType().equals(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER)){
			boolean isBeneficOwner1Change = checkBeneficOwner1Change(curCus, newCus);
			boolean isBeneficOwner2Change = checkBeneficOwner2Change(curCus, newCus);
			boolean isBeneficOwner3Change = checkBeneficOwner3Change(curCus, newCus);

			if (isBeneficOwner1Change || isBeneficOwner2Change || isBeneficOwner3Change) {
				newCus.setChangeCorpOwnerName(true);
			}
		}
		//[TRSGAP-116-quyen.le.manh]Jul 25, 2016A - End
		
		//isChangeCorpRefName
		if(StringUtil.isDifferent(curCus.getCorpPicLastname(), newCus.getCorpPicLastname())
				|| StringUtil.isDifferent(curCus.getCorpPicFirstname(), newCus.getCorpPicFirstname())
				|| StringUtil.isDifferent(curCus.getCorpPicLastnameKana(), newCus.getCorpPicLastnameKana())
				|| StringUtil.isDifferent(curCus.getCorpPicFirstnameKana(), newCus.getCorpPicFirstnameKana())) {
			newCus.setChangeCorpRefName(true);
		}
		
		//isChangeCorpRefAddress
		if(StringUtil.isDifferent(curCus.getCorpPicZipcode(), newCus.getCorpPicZipcode())
				|| StringUtil.isDifferent(curCus.getCorpPicPrefecture(), newCus.getCorpPicPrefecture())
				|| StringUtil.isDifferent(curCus.getCorpPicCity(), newCus.getCorpPicCity())
				|| StringUtil.isDifferent(curCus.getCorpPicSection(), newCus.getCorpPicSection())
				|| StringUtil.isDifferent(curCus.getCorpPicBuildingName(), newCus.getCorpPicBuildingName())){ //TRSSC-1197
			newCus.setChangeCorpRefAddress(true);
		}
		
		//isChangeCorpRepName
		if(StringUtil.isDifferent(curCus.getCorpRepFirstname(), newCus.getCorpRepFirstname())
				|| StringUtil.isDifferent(curCus.getCorpRepLastname(), newCus.getCorpRepLastname())
				|| StringUtil.isDifferent(curCus.getCorpRepFirstnameKana(), newCus.getCorpRepFirstnameKana())
				|| StringUtil.isDifferent(curCus.getCorpRepLastnameKana(), newCus.getCorpRepLastnameKana())
				//|| StringUtil.isDifferent(curCus.getCorpRepFullname(), newCus.getCorpRepFullname()) //TRSGAP-1281 no need check this condition to avoid difference white space with BE
			    //|| StringUtil.isDifferent(curCus.getCorpRepFullnameKana(), newCus.getCorpRepFullnameKana())
			    ) {
			newCus.setChangeCorpRepName(true);
		}
		
		log.info("[end] scan for changed CustomerInfo " + newCus);
	}

	private boolean checkBeneficOwner1Change(CustomerInfo curCus, CustomerInfo newCus) {
		log.info("[Start] checkBeneficOwner1Change");
		if (newCus.getBeneficOwnerFlg() != null && newCus.getBeneficOwnerFlg() == 1) {
			if (StringUtil.isDifferent(curCus.getBeneficOwnerFullname(), newCus.getBeneficOwnerFullname())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerFullnameKana(), newCus.getBeneficOwnerFullnameKana())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerZipcode(), newCus.getBeneficOwnerZipcode())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerPrefecture(), newCus.getBeneficOwnerPrefecture())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerCity(), newCus.getBeneficOwnerCity())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerSection(), newCus.getBeneficOwnerSection())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerTel(), newCus.getBeneficOwnerTel())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerBuildingName(), newCus.getBeneficOwnerBuildingName())) {
				log.info("[End] checkBeneficOwner1Change, result: true");
				return true;
			}
		}
		log.info("[End] checkBeneficOwner1Change, result: false");
		return false;
	}

	private boolean checkBeneficOwner2Change(CustomerInfo curCus, CustomerInfo newCus) {
		log.info("[Start] checkBeneficOwner2Change");
		if (newCus.getBeneficOwnerFlg2() != null && newCus.getBeneficOwnerFlg2() == 1) {
			if (StringUtil.isDifferent(curCus.getBeneficOwnerFullname2(), newCus.getBeneficOwnerFullname2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerFullnameKana2(), newCus.getBeneficOwnerFullnameKana2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerZipcode2(), newCus.getBeneficOwnerZipcode2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerPrefecture2(), newCus.getBeneficOwnerPrefecture2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerCity2(), newCus.getBeneficOwnerCity2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerSection2(), newCus.getBeneficOwnerSection2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerTel2(), newCus.getBeneficOwnerTel2())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerBuildingName2(), newCus.getBeneficOwnerBuildingName2())) {
				log.info("[End] checkBeneficOwner2Change, result: true");
				return true;
			}
		}
		log.info("[End] checkBeneficOwner2Change, result: false");
		return false;
	}

	private boolean checkBeneficOwner3Change(CustomerInfo curCus, CustomerInfo newCus) {
		log.info("[Start] checkBeneficOwner3Change");
		if (newCus.getBeneficOwnerFlg3() != null && newCus.getBeneficOwnerFlg3() == 1) {
			if (StringUtil.isDifferent(curCus.getBeneficOwnerFullname3(), newCus.getBeneficOwnerFullname3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerFullnameKana3(), newCus.getBeneficOwnerFullnameKana3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerZipcode3(), newCus.getBeneficOwnerZipcode3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerPrefecture3(), newCus.getBeneficOwnerPrefecture3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerCity3(), newCus.getBeneficOwnerCity3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerSection3(), newCus.getBeneficOwnerSection3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerTel3(), newCus.getBeneficOwnerTel3())
					|| StringUtil.isDifferent(curCus.getBeneficOwnerBuildingName3(), newCus.getBeneficOwnerBuildingName3())) {
				log.info("[End] checkBeneficOwner3Change, result: true");
				return true;
			}
		}
		log.info("[End] checkBeneficOwner3Change, result: false");
		return false;
	}

	/**
	 * Get message when update profile success　
	 **/
	public String getSuccessMessageCode(CustomerInfo customer) {
		//Ref https://nextop-asia.atlassian.net/browse/TRSBO-4590
		
		if(CORPORATION_TYPE.INDIVIDUAL.equals(customer.getCorporationType())){
			if(customer.hasDocumentUpload())
				return "MSG_TRS_NAF_0089";
			else if(customer.isChangeCustomerName() || customer.isChangeAddress())
				return "MSG_TRS_NAF_0043";
			return "MSG_TRS_NAF_0080";
		} else {
			//CORPORATION
			if(customer.hasDocumentUpload())
				return "MSG_TRS_NAF_0090";
			else if(customer.isChangeCorpName() || customer.isChangeCorpAddress() 
					|| customer.isChangeCorpRepName() || customer.isChangeCorpRefName() 
					|| customer.isChangeCorpRefAddress())
				return "MSG_TRS_NAF_0045";
			else			
				return "MSG_TRS_NAF_0080";
		}
	}
	
	private class SyncDataAndSendMailSubTask implements Runnable {
		private CustomerInfo customerInfo;
		private String requestId;
		public SyncDataAndSendMailSubTask(CustomerInfo customerInfo, String requestId) {
			this.customerInfo = customerInfo;
			this.requestId = requestId;
		}
		
		@Override
		public void run() {
			try {
				//Send mail to CS & Customer
				if (customerInfo.isChangeCustomerName() || customerInfo.isChangeAddress() || customerInfo.isChangeCorpName() || customerInfo.isChangeCorpAddress()
						|| customerInfo.isChangeCorpRefName() || customerInfo.isChangeCorpRefAddress() || customerInfo.isChangeCorpRepName()) {
					// Send mail inform to CS
					getProfileManager().sendmailChangeInfoToCS(customerInfo);
					// Send mail to customer
					getProfileManager().sendmailChangeInfoToCustomer(customerInfo);
					// Send mail notify CS for change file upload, send Old Name (TRSBO-4182)
					
					//[TRSPT-7454-ThinhPH]Jan 18, 2016M - Start : remove send email to CS when user upload document at simple mypage
//					if(customerInfo.hasDocumentUpload())
//						getProfileManager().sendmailNotifyDocToCS(currentCustomerInfo);
					//[TRSPT-7454-ThinhPH]Jan 18, 2016M - End
				} else
					log.info("No Name/Address changed. Do not have to send mail to CS and Customer");
			} catch (Exception e) {
				log.error("[requestId: " + requestId +"] " + e.getMessage(), e);
			}
			
			try {
				log.info("[start] syncCustomerInfoToBo");
				getAccountManager().syncCustomerInfoToBo(customerInfo.getCustomerId());
				log.info("[end] syncCustomerInfoToBo");
			} catch (Exception e) {
				log.error("[requestId: " + requestId +"] " + e.getMessage(), e);
			}
			
			try {
				// Synchronize customer information to Salesforce
				log.info("[requestId: " + requestId + "] [start] syncCustomerInfoToSalesForce, CustomerId: " + customerInfo.getCustomerId());
				getProfileManager().syncCustomerInfoToSaleFace(customerInfo);
				log.info("[requestId: " + requestId + "] [end] syncCustomerInfoToSalesForce, CustomerId: " + customerInfo.getCustomerId());
			} catch (Exception e) {
				log.error("[requestId: " + requestId +"] " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Create RpcMessage to response to client　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public RpcMessage createRpcMessage(AmsCustomerInfo customerInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_RESPONSE);
		if(errCode != null)
			response.setMessageCode(errCode);
		
		if(customerInfo != null) {
			AmsCustomerInfoUpdateResponse.Builder customerInfoResponse = AmsCustomerInfoUpdateResponse.newBuilder();
			customerInfoResponse.setCustomerInfo(customerInfo);
			response.setPayloadData(customerInfoResponse.build().toByteString());
		}
		
		response.setResult(result);
		
		return response.build();
	}
	
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	private String getAdditionalMail(CustomerInfo customerInfo) {
		String additionalMail = null;
		if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER) {
			additionalMail = customerInfo.getMailAddtional();
		} else {
			additionalMail = customerInfo.getCorpPicMailMobile();
		}
		
		return additionalMail;
	}
}