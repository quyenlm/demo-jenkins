package phn.nts.ams.fe.web.action.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;

import phn.com.nts.ams.web.condition.ReportHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsSysBank;
import phn.com.nts.db.entity.AmsSysBankBranch;
import phn.com.nts.db.entity.AmsSysZipcode;
import phn.com.nts.util.common.ComparatorUtil;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.file.FileLoaderUtil;
import phn.com.nts.util.file.FileUploadInfo;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Cryptography;
import phn.com.nts.util.security.CryptographyException;
import phn.com.nts.util.security.Security;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.TrsUtil;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BrokerSettingInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustReportHistoryInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerScInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.DocumentInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.ProfileModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.DownloadUtil;
import phn.nts.social.fe.web.action.BaseSocialAction;

import com.opensymphony.xwork2.ActionContext;
import com.phn.mt.common.constant.IConstant;
import com.phn.mt.common.entity.UserRecord;

public class ProfileAction extends BaseSocialAction<ProfileModel> {
	
	private static final long serialVersionUID = 1L;
	private IProfileManager profileManager = null;
	private IAccountManager accountManager = null;
	private ProfileModel model = new ProfileModel();
	private static final Logit log = Logit.getInstance(ProfileAction.class);
	private String result;
	private String msgCode;
	private String type;
	private String paymentId;
	private Integer cardType;
	private String cardNo;
	private String bankName;
	private String accNumber;
	//HungPV
	private String validateExitsEmail=null;
	
	private boolean changeCustomerName;
	private boolean changeAddress;
	private boolean changeCorpName;
	private boolean changeCorpAddress;
	private boolean changeCorpRefName;
	private boolean changeCorpRefAddress;
	private boolean changeCorpRepName;
	private boolean changeCorpOwnerName;

	
	public boolean isChangeCorpOwnerName() {
		return changeCorpOwnerName;
	}
	public void setChangeCorpOwnerName(boolean changeCorpOwnerName) {
		this.changeCorpOwnerName = changeCorpOwnerName;
	}

	private Integer customerCcId;

	public boolean isChangeCustomerName() {
		return changeCustomerName;
	}
	public void setChangeCustomerName(boolean changeCustomerName) {
		this.changeCustomerName = changeCustomerName;
	}
	public boolean isChangeAddress() {
		return changeAddress;
	}
	public void setChangeAddress(boolean changeAddress) {
		this.changeAddress = changeAddress;
	}
	public boolean isChangeCorpName() {
		return changeCorpName;
	}
	public void setChangeCorpName(boolean changeCorpName) {
		this.changeCorpName = changeCorpName;
	}
	public boolean isChangeCorpAddress() {
		return changeCorpAddress;
	}
	public void setChangeCorpAddress(boolean changeCorpAddress) {
		this.changeCorpAddress = changeCorpAddress;
	}
	public boolean isChangeCorpRefName() {
		return changeCorpRefName;
	}
	public void setChangeCorpRefName(boolean changeCorpRefName) {
		this.changeCorpRefName = changeCorpRefName;
	}
	public boolean isChangeCorpRefAddress() {
		return changeCorpRefAddress;
	}
	public void setChangeCorpRefAddress(boolean changeCorpRefAddress) {
		this.changeCorpRefAddress = changeCorpRefAddress;
	}
	public boolean isChangeCorpRepName() {
		return changeCorpRepName;
	}
	public void setChangeCorpRepName(boolean changeCorpRepName) {
		this.changeCorpRepName = changeCorpRepName;
	}

	//[NTS1.0-Quan.Le.Minh]Jan 21, 2013A - Start
	private static final int ONE = 1;
	private static final String MONTH_BEGGINER = "0";
	//[NTS1.0-Quan.Le.Minh]Jan 21, 2013A - End
	
	private InputStream inputStream;
	private static final String URL_AVATAR_FOLDER = "url.avatar.folder";
	private static Properties propsConfig;
	private static final String APP_PROPS_FILE = "configs.properties";
	
	static {
        try {
            propsConfig = Helpers.getProperties(APP_PROPS_FILE);                      
        } catch(Exception e) {
            log.warn("Could not load configuration file from: " + APP_PROPS_FILE, e);
        }
    }
	
	public ProfileModel getModel() {
		return model;
	}
	public IProfileManager getProfileManager() {
		return profileManager;
	}
	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	// get User Account
	public String index() {
		log.info("USER ACCOUNT INFO : START");
		try {
			init();
		
			if(!StringUtil.isEmpty(result)) {			
				getMsgCode(result);
			}
			setRawUrl(IConstants.FrontEndActions.PROFILE_INDEX);
			CustomerInfo customerInfo = null;
			CustomerServicesInfo socialMt4Info = null;
			CustomerServicesInfo normalMt4Info = null;
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					customerInfo = profileManager.getCustomerInfo(customerId);
					customerInfo.setDisplayLanguageName(model.getMapLanguage().get(StringUtil.toUpperCase(customerInfo.getDisplayLanguage())));
					log.info("get info of mt4 account : BEGIN"); 
//					socialMt4Info = getProfileManager().getCustomerService(customerId, IConstants.SERVICES_TYPE.COPY_TRADE);
//					model.setSocialMt4Info(socialMt4Info);
//					normalMt4Info = getProfileManager().getCustomerService(customerId, IConstants.SERVICES_TYPE.FX);
//					model.setNormalMt4Info(normalMt4Info);
//					String boAccountId = getProfileManager().getCustomerService(customerId, IConstants.SERVICES_TYPE.BO).getCustomerServiceId();
//					model.setBoAccountId(boAccountId);
					List<CustomerServicesInfo> listCustomerServicesInfos = getProfileManager().getCustomerService(customerId);
					if(listCustomerServicesInfos != null && listCustomerServicesInfos.size() > 0){
						for(CustomerServicesInfo cusInfo : listCustomerServicesInfos){
							if(cusInfo.getServiceType().equals(IConstants.SERVICES_TYPE.COPY_TRADE)){
								model.setSocialMt4Info(cusInfo);
							}else if (cusInfo.getServiceType().equals(IConstants.SERVICES_TYPE.FX)){
								//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
								Date accDate = DateUtil.toDate(cusInfo.getAccountOpenDate(), DateUtil.PATTERN_YYMMDD_BLANK);
								String accDateStr = DateUtil.toString(accDate, DateUtil.PATTERN_YYMMDD);
								customerInfo.setAccountOpenDate(accDateStr);
								//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
								model.setNormalMt4Info(cusInfo);
							}else if (cusInfo.getServiceType().equals(IConstants.SERVICES_TYPE.BO)){
								model.setBoAccountId(cusInfo.getCustomerServiceId());
							}
						}
					}
					log.info("get info of mt4 account : END");
					
					customerInfo.setCurrencyCode(frontUserOnline.getCurrencyCode());
//					CustomerServicesInfo customerServicesInfo = accountManager.getCustomerServiceInfo(customerId,IConstants.SERVICES_TYPE.FX);
//					customerInfo.setMt4Id(customerServicesInfo.getCustomerServiceId());				
//					customerInfo.setIbLink(profileManager.getIbLink(customerInfo.getCustomerId()));
//					customerInfo.setLeverage(customerServicesInfo.getLeverage());
					
					//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
					customerInfo.setFinancilAssetsName(model.getMapFinancilAssets().get(customerInfo.getFinancilAssets() + ""));
					//customerInfo.setPurposeShortTermFlgName(model.getMapPurposeShortTerm().get(customerInfo.getPurposeShortTermFlg()));
					customerInfo.setPurposeShortTermFlgName(getText("nts.ams.fe.label.customer_information.purepose1"));
					customerInfo.setPurposeLongTermFlgName(getText("nts.ams.fe.label.customer_information.purepose2"));
					customerInfo.setPurposeExchangeFlgName(getText("nts.ams.fe.label.customer_information.purepose3"));
					customerInfo.setPurposeSwapFlgName(getText("nts.ams.fe.label.customer_information.purepose4"));
					customerInfo.setPurposeHedgeAssetFlgName(getText("nts.ams.fe.label.customer_information.purepose5"));
					customerInfo.setPurposeHighIntFlgName(getText("nts.ams.fe.label.customer_information.purepose6"));
					customerInfo.setPurposeEconomicFlgName(getText("nts.ams.fe.label.customer_information.purepose7"));
					customerInfo.setPurposeOtherName(getText("nts.ams.fe.label.customer_information.purepose8"));
					List<String> listPurposes = new ArrayList<String>();	
					if(customerInfo.isPurposeShortTermFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose1"));
					}
					if(customerInfo.isPurposeLongTermFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose2"));
					}
					if(customerInfo.isPurposeExchangeFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose3"));
					}
					if(customerInfo.isPurposeSwapFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose4"));
					}
					if(customerInfo.isPurposeHedgeAssetFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose5"));
					}
					if(customerInfo.isPurposeHighIntFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose6"));
					}
					if(customerInfo.isPurposeEconomicFlg() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose7"));
					}
					if(customerInfo.isPurposeOther() == true){
						listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose8"));
						listPurposes.add(customerInfo.getPurposeOtherComment());
					}
					customerInfo.setListPurposes(listPurposes);
					//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
				}
				model.setCustomerInfo(customerInfo);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}		
		log.info("USER ACCOUNT INFO : END");
		return SUCCESS;
	}
	
	public String uploadProfile() {	
		return SUCCESS;
	}
	
	/**
	 * initialize info for basic information
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Feb 28, 2013
	 */
	public boolean initBasicInformation(){
		log.info("[start] initBasicInformation");
		model.setListDay(Utilities.getListCalendar(IConstants.DATE_CALENDAR.DAY, true));
		model.setListMonth(getMapMonth());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		model.setListYear(Utilities.getListCalendar(IConstants.CALENDAR.FROM_YEAR,IConstants.CALENDAR.TO_YEAR,false));
		Map<String, String> mapGender = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.GENDER);
		if(mapGender != null){
			model.setMapGender(mapGender);
		}
		log.info("[end] initBasicInformation");
		return true;
	}
	
	/**
	 * get basic Information
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Feb 26, 2013
	 */
	public String getBasicInformation() {
		log.info("BASIC INFORMATION : START");
		try {
			initBasicInformation();
			
			if(!StringUtil.isEmpty(result)) {			
				getMsgCode(result);
			}
			CustomerInfo customerInfo = null;
			CustomerScInfo customerScInfo = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					customerInfo = getProfileManager().getCustomerInfo(customerId);
					String birthday = customerInfo.getBirthday();
					Date dBirthday;
					if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
						birthday = customerInfo.getBirthday();
						dBirthday = DateUtil.toDate(birthday, IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT);
					}else{
						birthday = customerInfo.getCorpEstablishDate();
						dBirthday = DateUtil.toDate(birthday, DateUtil.PATTERN_YYYYMM_BLANK);
					}
					if(dBirthday != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(dBirthday);
						Integer day = cal.get(Calendar.DAY_OF_MONTH);
						Integer month = cal.get(Calendar.MONTH) + 1;
						Integer year = cal.get(Calendar.YEAR);
						customerInfo.setDay(StringUtil.toString(day));
						customerInfo.setMonth(StringUtil.toString(month));
						customerInfo.setYear(StringUtil.toString(year));
//						customerInfo.setBirthday(DateUtil.toString(dBirthday, getText("nts.ams.fe.label.date.pattern")));
						String language = frontUserOnline.getLanguage();
						if(StringUtil.isEmpty(language)){
							language = "JA";
						}
						model.setUserLanguage(language);
						if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
							customerInfo.setBirthday(formatDate(new Timestamp(dBirthday.getTime()), getText("nts.ams.fe.label.date.pattern")));
						}else{
							customerInfo.setBirthday(formatDate(new Timestamp(dBirthday.getTime()), getText("nts.ams.fe.label.date.pattern.corp")));
						}
					}
					customerScInfo = getProfileManager().getCustomerScInfo(customerId);
					
					// escapse to prevent input html tag or script in decription
					String description2 = customerScInfo.getDescription();
//					if (!StringUtil.isEmpty(description2)) {
//						description2 = URLDecoder.decode(description2, "UTF-8");
//					}
					customerScInfo.setDescriptionTextArea(description2);
					customerScInfo.setDescription(description2);
				}
				model.setCustomerInfo(customerInfo);
				model.setCustomerScInfo(customerScInfo);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}		
		log.info("BASIC INFORMATION : END");
		return SUCCESS;
	}
	
	/**
	 * init info for Broker Setting
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 6, 2013
	 */
	public void initBrokerSetting(){
		Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SC_SERVICE_TYPE);
		Map<String, String> mapAccountKind = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.ACCOUNT_KIND);
		Map<String, String> mapBrokerCd = getMapBrokerCd();
		Map<Integer, String> mapServerAddress = new HashMap<Integer, String>();
	/*	Map<String,Integer> mapNumberOfEnabledBrokerCd = new HashMap<String, Integer>();
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if(frontUserOnline != null) {
				String customerId = frontUserOnline.getUserId();
				mapNumberOfEnabledBrokerCd = profileManager.getMapNumberByEnabledBrokerCd(customerId);
				if(mapNumberOfEnabledBrokerCd != null){
					model.setMapNumberOfEnabledBrokerCd(mapNumberOfEnabledBrokerCd);
				}
			}
		}*/
		
		if(mapServiceType != null){
			model.setMapServiceType(mapServiceType);
		}
		if(mapAccountKind != null){
			model.setMapAccountKind(mapAccountKind);
		}
		if(mapBrokerCd != null){
			model.setMapBrokerCd(mapBrokerCd);
		}
//		if(model.getBrokerId() != null && !model.getBrokerId().equals(IConstants.FRONT_OTHER.COMBO_TOP)){
//			model.setServerAddress(profileManager.getServerAddressByBrokerId(model.getBrokerId()));
//		}
		if(model.getBrokerCd() != null && !model.getBrokerCd().equals(IConstants.FRONT_OTHER.COMBO_INDEX)){
			model.setMapServerAddress(profileManager.getServerAddressByBrokerCd(model.getBrokerCd()));
		}else{
			model.setMapServerAddress(mapServerAddress);
		}
	}
	
	public Map<String,String> getMapBrokerCd(){
		Map<String,String> mapBrokerCd = new HashMap<String, String>();;
		mapBrokerCd = getProfileManager().getMapBrokerCd();
		return mapBrokerCd;
	}
	
	/**
	 * get map server address by broker name
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 8, 2013
	 */
	public String getServerAddressByBrokerCd(){		
		String brokerCd  = model.getBrokerCd();
		Map<Integer,String> serverAddress = new HashMap<Integer, String>();
		if(brokerCd != null){
			serverAddress = profileManager.getServerAddressByBrokerCd(brokerCd);
		}		
		model.setMapServerAddress(serverAddress);
		return SUCCESS;
	}
	
	public String getBrokerSetting(){
		log.info("BROKER SETTING : START");
		try {
			initBrokerSetting();
			if(result != null) {			
				getMsgCode(result);
			}
			List<BrokerSettingInfo> listBrokerSettingInfos = new ArrayList<BrokerSettingInfo>();
			List<BrokerSettingInfo> listTrsInfo = new ArrayList<BrokerSettingInfo>();
			List<BrokerSettingInfo> listOtherBrokerInfo = new ArrayList<BrokerSettingInfo>();
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					listBrokerSettingInfos = getProfileManager().getBrokerSettingInfo(customerId);
					for(BrokerSettingInfo cus: listBrokerSettingInfos){
						//[NTS1.0-le.hong.ha]May 7, 2013M - Start 
						//Edit to TRS broker
						if(ITrsConstants.BROKER_TYPE.TRS_BROKER.equals(cus.getBrokerCd())){
							listTrsInfo.add(cus);
						}else{
							listOtherBrokerInfo.add(cus);
						}
						//[NTS1.0-le.hong.ha]May 7, 2013M - End
					}
				}
				if(listTrsInfo != null && listTrsInfo.size() != 0){
					model.setListTrsServiceInfo(listTrsInfo);
				}
				
				//[NTS1.0-le.hong.ha]May 31, 2013D - Start 
				// TRS not use
				//if(listOtherBrokerInfo != null && listOtherBrokerInfo.size() != 0){
				//	model.setListOtherBrokerServiceInfo(listOtherBrokerInfo);
				//}
				//[NTS1.0-le.hong.ha]May 31, 2013D - End

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}		
		log.info("BROKER SETTING : END");
		return SUCCESS;
	}
	
/*	public String updateBrokerInfo(){
		List<BrokerSettingInfo> listNfBrokerSettingInfos = model.getListNfxServiceInfo();
		List<BrokerSettingInfo> listOtherBrokerSettingInfos = model.getListOtherBrokerServiceInfo();
		List<BrokerSettingInfo> listEditedBroker = new ArrayList<BrokerSettingInfo>();
		for(BrokerSettingInfo brokerSettingInfo : listNfBrokerSettingInfos){
			if(brokerSettingInfo.getEdited().equals(1)){
				listEditedBroker.add(brokerSettingInfo);
			}
		}
		
		for(BrokerSettingInfo brokerSettingInfo : listNfBrokerSettingInfos){
			if(brokerSettingInfo.getEdited().equals(1)){
				listEditedBroker.add(brokerSettingInfo);
			}
		}
		
		if(!profileManager.updateBrokerInfo(listEditedBroker)){
			return ERROR;
		}
		return SUCCESS;
	}*/
	
	public String updateBrokerInfo(){
		try{
			Integer scCustServiceId = model.getScCustServiceId();
			String expiredDate = model.getExpiredDate();
			Date expriredDay = DateUtil.toDate(expiredDate,DateUtil.PATTERN_YYMMDD);
			String password = model.getPassword();
			Calendar cal = Calendar.getInstance();  
			Date curDate = cal.getTime();  
			if(expriredDay.before(curDate)){
				model.setErrorMsg(getText("MSG_SC_051"));
				addFieldError("errorMessage",getText("MSG_SC_051"));
				return SUCCESS;
			}
			
			cal.add(Calendar.DATE, 7); // add 7 days  
			curDate = cal.getTime();
			if(expriredDay.after(curDate)){
				model.setErrorMsg(getText("MSG_SC_051"));
				addFieldError("errorMessage",getText("MSG_SC_051"));
				return SUCCESS;
			}
			if(!profileManager.updateBrokerInfo(scCustServiceId, expiredDate, password)){
				model.setErrorMsg(getText("nts.ams.fe.message.profile.update.failure"));
				addFieldError("errorMessage",getText("MSG_NAB044"));
				return SUCCESS;
			}else{
				model.setSuccessMsg(getText("nts.ams.fe.message.profile.update.success"));
				return SUCCESS;
			}
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unused")
	public String updateStatusForBroker(){
        try{
            Integer scCustServiceId = model.getScCustServiceId();
            Integer enableFlg = model.getEnableFlg();
            String accountId = model.getAccountId();
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if(frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if(frontUserOnline != null) {
                    String customerId = frontUserOnline.getUserId();
                    if(scCustServiceId != null && enableFlg != null){
                        List<String> listContent = new ArrayList<String>();
                        if(enableFlg.equals(IConstants.ENABLE_FLG.ENABLE)){
                            listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.enable"));
                        }else{
                            listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.disable"));
                        }
                        if(!getProfileManager().updateEnableFlgOfBroker(scCustServiceId, enableFlg)){
                            model.setErrorMsg(getText("MSG_SC_053",listContent));
                            addFieldError("errorMessage",getText("MSG_SC_053",listContent));
                            if(enableFlg.equals(IConstants.ENABLE_FLG.ENABLE)){
                                setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_ENABLE_UNSUCCESS);
                            }else{
                                setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_DISABLE_UNSUCCESS);
                            }
                            return SUCCESS;
                        }else{
                            model.setSuccessMsg(getText("MSG_SC_052",listContent));
                            if(enableFlg.equals(IConstants.ENABLE_FLG.ENABLE)){
                                setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_ENABLE_SUCCESS);
                            }else{
                                setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_DISABLE_SUCCESS);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return ERROR;
        }

        return SUCCESS;
	}
	@SuppressWarnings("unused")
	public String validateStatusForBroker(){
		try{
			Integer enable = model.getEnableFlg();
			Integer scCustServiceId = model.getScCustServiceId();
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					if(enable.equals(IConstants.ENABLE_FLG.ENABLE)){
						Integer numberOfEnabledAccount = profileManager.getNumberOfEnabledAccount(customerId);						
						Integer numberOfEnabledOther = profileManager.getNumberOfEnabledOtherBroker(customerId);						
						if(numberOfEnabledAccount >= 5){
							model.setErrorMsg(getText("MSG_SC_038"));
							return ERROR;
						}
						if(numberOfEnabledOther >= 1){
							model.setErrorMsg(getText("MSG_SC_048"));
							return ERROR;
						}
					}else{
						Integer noOfOrder = profileManager.getNumberOfScOrder(StringUtil.toString(scCustServiceId));
						if(!noOfOrder.equals(IConstants.FRONT_OTHER.NOT_FOUND_NUMBER)){
							model.setErrorMsg(getText("MSG_SC_054"));
							return SUCCESS;
						}
					}
				}
			}
			model.setJsonResult("true");
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	/**
	 * delete a broker account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 11, 2013
	 */
	public String deleteBroker(){
		try{
			Integer scCustServiceId = model.getScCustServiceId();
			Integer noOfOrder = profileManager.getNumberOfScOrder(StringUtil.toString(scCustServiceId));
			if(noOfOrder != IConstants.FRONT_OTHER.NOT_FOUND_NUMBER){
				model.setErrorMsg(getText("MSG_SC_054"));
				getBrokerSetting();
				return SUCCESS;
			}
			if(profileManager.deleteBroker(scCustServiceId)){
				model.setSuccessMessage(getText("message.delete.success"));
			}else{
				model.setErrorMessage(getText("message.delete.unsuccess"));
			}
			getBrokerSetting();
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	public String createNewBroker(){
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
	 				String customerId = frontUserOnline.getUserId();
//					String baseCurrency;
					BrokerSettingInfo newBroker = model.getNewBrokerSettingInfo();
					if(validateNewBroker(newBroker)){
						Date expriredDay = DateUtil.toDate(newBroker.getSignalExpiredDatetime(),DateUtil.PATTERN_YYMMDD);
						Calendar cal = Calendar.getInstance();  
						Date curDate = cal.getTime();  
						if(expriredDay.before(curDate)){
							model.setErrorMessage(getText("MSG_SC_051"));
							addFieldError("errorMessage",getText("MSG_SC_051"));
							return SUCCESS;
						}
						
						cal.add(Calendar.DATE, 7); // add 7 days  
						curDate = cal.getTime();
						if(expriredDay.after(curDate)){
							model.setErrorMessage(getText("MSG_SC_073"));
							addFieldError("errorMessage",getText("MSG_SC_073"));
							getBrokerSetting();
							return SUCCESS;
						}
						if(!profileManager.checkExistAccountIdAndBrokerId(customerId, newBroker)){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.account_id"));
							model.setErrorMessage(getText("MSG_SC_020", listContent));
							addFieldError("errorMessage", getText("MSG_SC_020", listContent));
							getBrokerSetting();
							return ERROR;
						}
						if(profileManager.insertNewAccountBroker(customerId, newBroker)){
							model.setSuccessMessage(getText("MSG_SC_072"));
							addFieldError("errorMessage",getText("MSG_SC_072"));
							return getBrokerSetting();
						}
					}else{
						getBrokerSetting();
						return ERROR;
					}
				}
			}
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	private boolean validateNewBroker(BrokerSettingInfo newBroker){
		List<String> listContent = new ArrayList<String>();
		if(newBroker.getBrokerCd() == null || newBroker.getBrokerCd().equals(IConstants.FRONT_OTHER.COMBO_INDEX)){
			listContent.add("Broker Name");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getBrokerId() == null || newBroker.getBrokerId().equals(IConstants.FRONT_OTHER.COMBO_INDEX)){
			listContent.add("Server Address");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getAccountKind() == null || newBroker.getAccountKind().equals(IConstants.FRONT_OTHER.COMBO_INDEX)){
			listContent.add("Account Kind");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getAccountId() == null){
			listContent.add("Account Id");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getAccountPassword() == null){
			listContent.add("Password");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getBaseCurrency() == null || newBroker.getAccountKind().equals(IConstants.FRONT_OTHER.COMBO_INDEX)){
			listContent.add("Base currency");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		if(newBroker.getSignalExpiredDatetime() == null){
			listContent.add("Expired Date");
			model.setErrorMessage(getText("MSG_SC_013",listContent));
			return false;
		}
		return true;
	}
	
	public String getPrivacySetting() {	
		log.info("PRIVACY SETTING : START");
		try {
			if(result != null) {			
				getMsgCode(result);
			}
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					Integer writeMyBoardFlg = getProfileManager().getCustomerScInfo(customerId).getWriteMyBoardFlg();
					model.setWriteMyBoardFlg(writeMyBoardFlg);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}	
		log.info("PRIVACY SETTING : END");
		return SUCCESS;
	}
	
	public String updatePrivacySetting(){
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					Integer writeMyBoardFlag = model.getWriteMyBoardFlg();
					if(writeMyBoardFlag != null){
						if(!getProfileManager().updatePrivacySetting(writeMyBoardFlag, customerId)){
							model.setErrorMsg(getText("nts.ams.fe.message.profile.update.failure"));
							addFieldError("errorMessage",getText("MSG_NAB044"));
							return SUCCESS;
						}else{
							// Halh edit to change msg
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.socialtrading.scfe018.privacy_setting"));
							model.setSuccessMsg(getText("MSG_NAB003", listContent));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}
	
	public String getVerification() {
		log.info("VERIFICATION : START");
		if(result != null) {			
			getMsgCode(result);
		}
		CustomerInfo customerInfo = null;
		
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if(frontUserOnline != null) {
				String customerId = frontUserOnline.getUserId();
				customerInfo = getProfileManager().getCustomerInfo(customerId);
				CustomerInfo docInfo = profileManager.getDocUrls(frontUserOnline.getUserId());
				customerInfo.setPassportDocs(docInfo.getPassportDocs());
				customerInfo.setAddressDocs(docInfo.getAddressDocs());
				customerInfo.setSignatureDocs(docInfo.getSignatureDocs());
				model.setCustomerInfo(customerInfo);
			}
		}
		log.info("VERIFICATION : END");
		return SUCCESS;
	}
	
	public String bankInformation(){
		log.info("PAYMENT INFORMATION : START");
		try {
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			if(result != null) {			
				getMsgCode(result);
			}
			getListInfo();
			
			//[NTS1.0-le.hong.ha]Jun 17, 2013A - Start 
			if(result == null){
				// Set default value 
				model.setPaymentMethod(ITrsConstants.PAYMENT_METHOD.VIRTUAL_ACCOUNT);
			}
			//[NTS1.0-le.hong.ha]Jun 17, 2013A - End
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			CustomerInfo customerInfo = null;
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					customerInfo = profileManager.getCustomerInfo(customerId);
					model.setCustomerInfo(customerInfo);
					
					//[NTS1.0-anhndn]Jan 25, 2013A - Start 
					initListMonthYear();
					//[NTS1.0-anhndn]Jan 25, 2013A - End
					
					model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
					model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
					model.setListCreditCardInfo(getCreditCardList(customerId));
					model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
					model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
				}
			}	
			
			//[NTS1.0-le.hong.ha]Apr 26, 2013A - Start 
//			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
//			if(result != null) {			
//				getMsgCode(result);
//			}
//			getListInfo();
//			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//			CustomerInfo customerInfo = null;
//			if(frontUserDetails != null) {
//				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
//				if(frontUserOnline != null) {
//					String customerId = frontUserOnline.getUserId();
//					customerInfo = profileManager.getCustomerInfo(customerId);
//					model.setCustomerInfo(customerInfo);
//				}
//			}	
			//[NTS1.0-le.hong.ha]Apr 26, 2013A - End
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
		log.info("PAYMENT INFORMATION : END");
		return SUCCESS;
	}
	
	
	private void initListMonthYear() {
		model.setListMonth(getMapMonth());
		Integer years = new Integer(0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		years = cal.get(Calendar.YEAR);
		model.setListYear(Utilities.getListCalendarToFuture(years, new Integer(20)));
	}
	
	/**
	 * Add Neteller account
	 */
	public String addNeteller() {
		try{
			getListInfo();
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			model.setPaymentMethod(IConstants.EWALLET_TYPE.NETELLER);
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			CustomerEwalletInfo customerEwalletInfo = model.getCustomerEwalletInfo();
			if(customerEwalletInfo == null) {
				customerEwalletInfo = new CustomerEwalletInfo();
			}
			
			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			model.setCustomerInfo(customerInfo);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			model.setListCreditCardInfo(getCreditCardList(customerId));
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			
			validateNetellerInfo(customerEwalletInfo);
			if (hasFieldErrors()) {	
//				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
				return INPUT;
			}

			customerEwalletInfo.setCustomerId(customerId);
			List<FileUploadInfo> listFileUploadInfo = null;
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String wlCode = frontUserOnline.getWlCode();
			String publicKey = frontUserOnline.getPublicKey();
			String result = getProfileManager().addEwallet(customerEwalletInfo,IConstants.EWALLET_TYPE.NETELLER, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(customerEwalletInfo.getEwalletAccNo());
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
				return INPUT;
			}
			else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent));
				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
				return INPUT;
			}else{
				//reset field after add new neteller account
				customerEwalletInfo.setEwalletAccNo("");
				customerEwalletInfo.setEwalletSecureId("");
			}
			model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
			model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.paymentmethod.add.success", listContent));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB002);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB002 + "&paymentMethod="+IConstants.EWALLET_TYPE.NETELLER);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	/**
	 * add liberty account　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Oct 25, 2012
	 */
	public String addLiberty() {
		try{
			getListInfo();
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			model.setPaymentMethod(IConstants.EWALLET_TYPE.LIBERTY);
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;			
			}
			CustomerEwalletInfo customerEwalletInfo = model.getCustomerEwalletInfo();
			if(customerEwalletInfo == null) {
				customerEwalletInfo = new CustomerEwalletInfo();
			}
			
			String customerId = frontUserOnline.getUserId();
			customerEwalletInfo.setCustomerId(customerId);

			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			model.setCustomerInfo(customerInfo);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			model.setListCreditCardInfo(getCreditCardList(customerId));
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			
			validateLibertyInfo(customerEwalletInfo);
			if (hasFieldErrors()) {
				return INPUT;
			}
			
			List<File> listFileUpload = model.getListLibertyFileUpload();
			List<String> listFileUploadFileNames = model.getListLibertyFileUploadFileName();
			List<FileUploadInfo> listFileUploadInfo = null;
			
			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			if (listFileUpload == null) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043", listContent)+ ": " + getText("nts.ams.fe.label.deposit.credit.card.verification_document") + " " + getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.is.require"));
				return INPUT;
			}
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			listFileUploadInfo = getFileUploadInfo(listFileUpload, listFileUploadFileNames, customerId, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.LIBERTY_RESERVE, IConstants.UPLOAD_DOCUMENT.DOC_KIND.WITHDRAW);
			
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String wlCode = frontUserOnline.getWlCode();
			String publicKey = frontUserOnline.getPublicKey();
			String result = getProfileManager().addEwallet(customerEwalletInfo,IConstants.EWALLET_TYPE.LIBERTY, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				return INPUT;
			}
			else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent));
				return INPUT;
			}
			else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent) + ": " + getText("MSG_NAF108"));
				return INPUT;
			}
			else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent) + ": " + getText("MSG_NAF109"));
				return INPUT;
			}
			else {
				//reset field after add new liberty account
				customerEwalletInfo.setEwalletAccNo("");
				customerEwalletInfo.setEwalletApiName("");
				customerEwalletInfo.setEwalletSecureWord("");
			}
			model.setListLiberty(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.LIBERTY));
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
			model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.paymentmethod.add.success", listContent));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.LIBERTY);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	/**
	 * Update Neteller Account
	 */
	public String updatePayment() {
		try{
			//[NTS1.0-Quan.Le.Minh]Feb 19, 2013A - Start 
			setRawUrl(IConstants.FrontEndActions.PROFILE_UPDATE_PAYMENT);
			if(result != null){
				getMsgCode(result);
			}
			//[NTS1.0-Quan.Le.Minh]Feb 19, 2013A - End
			// List of payment method
			Map<String, String> mapPaymentMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);
			model.setMapPaymentMethod(mapPaymentMethod);
			
			//[NTS1.0-le.hong.ha]May 14, 2013A - Start 
			Map<String, String> mapAccountType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.TRS_ACCOUNT_TYPE);
			model.setMapAccountType(mapAccountType);
			//[NTS1.0-le.hong.ha]May 14, 2013A - End
			
			// List of type of card
			Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
			model.setMapCardType(mapCardType);
			// get List country
			model.setListCountry(profileManager.getListCountry());
			String customerId = null;
			String publicKey = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					publicKey = frontUserOnline.getPublicKey();
				}
			}
			Integer method = MathUtil.parseInteger(type);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			if(method != null) {
				if(IConstants.EWALLET_TYPE.NETELLER.equals(method)) {
					model.setPaymentMethod(IConstants.EWALLET_TYPE.NETELLER);
					model.setPaymentMethodName(mapPaymentMethod.get(StringUtil.toString(IConstants.EWALLET_TYPE.NETELLER)));
						if(customerId != null) {	
							CustomerEwalletInfo customerEwalletInfo = profileManager.getEwalletInfo(customerId,paymentId,IConstants.EWALLET_TYPE.NETELLER,publicKey);
							model.setCustomerEwalletInfo(customerEwalletInfo);
							model.setOldCustomerEwalletInfo(customerEwalletInfo);
							model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
						}
				} else if (IConstants.EWALLET_TYPE.PAYZA.equals(method)) {
					model.setPaymentMethod(IConstants.EWALLET_TYPE.PAYZA);
					model.setPaymentMethodName(mapPaymentMethod.get(StringUtil.toString(IConstants.EWALLET_TYPE.PAYZA)));
						if(customerId != null) {					
							CustomerEwalletInfo customerEwalletInfo = profileManager.getEwalletInfo(customerId,paymentId, IConstants.EWALLET_TYPE.PAYZA,publicKey);
							model.setCustomerEwalletInfo(customerEwalletInfo);
							model.setOldCustomerEwalletInfo(customerEwalletInfo);
							model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
						}
				} else if (IConstants.EWALLET_TYPE.BANK_TRANSFER.equals(method)) {
					model.setPaymentMethod(IConstants.EWALLET_TYPE.BANK_TRANSFER);
					model.setPaymentMethodName(mapPaymentMethod.get(StringUtil.toString(IConstants.EWALLET_TYPE.BANK_TRANSFER)));
					if(customerId != null) {					
						BankTransferInfo bankTransferInfo = profileManager.getBankInfo(customerId, bankName, accNumber);
						model.setNewBankTransferInfo(bankTransferInfo);
						model.setOldBankTransferInfo(bankTransferInfo);
						model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
					}
				} else if (IConstants.EWALLET_TYPE.CREDIT_DEBIT.equals(method)) {
					model.setPaymentMethod(IConstants.EWALLET_TYPE.CREDIT_DEBIT);
					model.setPaymentMethodName(mapPaymentMethod.get(StringUtil.toString(IConstants.EWALLET_TYPE.CREDIT_DEBIT)));
					
					if(customerId != null) {		
						customerCcId = model.getCustomerCcId();
						CreditCardInfo creditCardInfo = profileManager.getCreditCardbyID(customerCcId,publicKey);
						//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - Start 
						List<DocumentInfo> docs = profileManager.getCcDocUrl(creditCardInfo.getCustomerCcId());
						if(docs != null && !docs.isEmpty()){
							creditCardInfo.setDocInfos(docs);
						}
						//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - End
						////[NTS1.0-anhndn]Jan 25, 2013A - Start 
						String expiredDate = creditCardInfo.getExpiredDate();
						String expiredYear = expiredDate.substring(0, 4);
						String expiredMonth = expiredDate.substring(4);
						if (expiredMonth.startsWith("0")) {
							expiredMonth = expiredMonth.replace("0", "");
						}
						creditCardInfo.setExpiredYear(expiredYear);
						creditCardInfo.setExpiredMonth(expiredMonth);
						
//						String ccNoDisp = hideString(creditCardInfo.getCcNo());
						creditCardInfo.setCcNoDisp("*****" + (creditCardInfo.getCcNoLastDigit()));
						
						creditCardInfo.setCcTypeName(getText(creditCardInfo.getCcTypeName()));
						creditCardInfo.setCountryName(model.getListCountry().get(creditCardInfo.getCountryId().toString()));
						
						//[NTS1.0-anhndn]Jan 25, 2013A - End
						model.setNewCreditCardInfo(creditCardInfo);
						model.setListCreditCardInfo(getCreditCardList(customerId));
						
					}
				} if(IConstants.EWALLET_TYPE.LIBERTY.equals(method)) {
					//[NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - Start 
					model.setPaymentMethod(IConstants.EWALLET_TYPE.LIBERTY);
					model.setPaymentMethodName(mapPaymentMethod.get(StringUtil.toString(IConstants.EWALLET_TYPE.LIBERTY)));
					if(customerId != null) {	
						CustomerEwalletInfo customerEwalletInfo = profileManager.getEwalletInfo(customerId,paymentId,IConstants.EWALLET_TYPE.LIBERTY,publicKey);
						//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - Start 
						List<DocumentInfo> docs = profileManager.getEwalletDocUrl(customerEwalletInfo.getEwalletId());
						if(docs != null && !docs.isEmpty()){
							customerEwalletInfo.setDocInfos(docs);
						}
						//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - End
						model.setCustomerEwalletInfo(customerEwalletInfo);
						model.setOldCustomerEwalletInfo(customerEwalletInfo);
						model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
					}
					//[NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - End
				}
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		
		return SUCCESS;
	}
	
	/**
	 * convert card number into format *****
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 22, 2013
	 */
	 private String hideString(String str) {
			String digit4 = null;
			if (str != null && str.length() > 4) {
				digit4 = str.substring(str.length() - 4);
			} else {
				digit4 = str == null ? "" : str;
			}
			
			return "*****" + digit4;
		}
	
	/**
	 * Update payment submit
	 */
	
	public String updateNetellerSubmit() {
		try{
			getListInfo();
			initListMonthYear();
			String customerId = null;
			String publicKey = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					publicKey = frontUserOnline.getPublicKey();
				}
			}
			model.setPaymentMethod(IConstants.EWALLET_TYPE.NETELLER);
			CustomerEwalletInfo newCustomerEwalletInfo = model.getCustomerEwalletInfo();
			CustomerEwalletInfo oldCustomerEwalletInfo = model.getOldCustomerEwalletInfo();
			validateNetellerInfo(newCustomerEwalletInfo);
			if (hasFieldErrors()) {	
				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.INPUT_ERROR + "&paymentMethod="+IConstants.EWALLET_TYPE.NETELLER);
				return INPUT;
			}
			String wlCode = frontUserOnline.getWlCode();
			List<FileUploadInfo> listFileUploadInfo = null;
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String updateResult = profileManager.updateEwallet(customerId,newCustomerEwalletInfo,oldCustomerEwalletInfo,IConstants.PAYMENT_METHOD.NETELLER, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				//setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
				//setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.NETELLER);
				return INPUT;
			} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)){
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB046);
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB046 + "&paymentMethod="+IConstants.EWALLET_TYPE.NETELLER);
				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.NETELLER));
				return INPUT;
			}
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.NETELLER);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		
		return SUCCESS;
	}
	
	public String updatePayzaSubmit() {
		try{
			getListInfo();
			initListMonthYear();
			String customerId = null;
			String publicKey = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					publicKey = frontUserOnline.getPublicKey();
				}
			}
			model.setPaymentMethod(IConstants.EWALLET_TYPE.PAYZA);
			CustomerEwalletInfo newCustomerEwalletInfo = model.getCustomerEwalletInfo();
			CustomerEwalletInfo oldCustomerEwalletInfo = model.getOldCustomerEwalletInfo();
			validatePayzaInfo(newCustomerEwalletInfo);
			if (hasFieldErrors()) {	
				model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.INPUT_ERROR + "&paymentMethod="+IConstants.EWALLET_TYPE.PAYZA);
				return INPUT;
			}
			String wlCode = frontUserOnline.getWlCode();
			List<FileUploadInfo> listFileUploadInfo = null;
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String updateResult = profileManager.updateEwallet(customerId,newCustomerEwalletInfo,oldCustomerEwalletInfo,IConstants.PAYMENT_METHOD.PAYZA, listFileUploadInfo, wlCode, fxSubGroupId, publicKey);
			if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
				model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.PAYZA);
				return INPUT;
			} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)){
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB046);
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB046 + "&paymentMethod="+IConstants.EWALLET_TYPE.PAYZA);
				model.setListNeteller(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.PAYZA));
				return INPUT;
			}
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.PAYZA);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		
		return SUCCESS;
	}
	
	/**
	 * update customer credit card information　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 22, 2013
	 */
	public String updateCreditSubmit() {
		getListInfo();
		initListMonthYear();
		String customerId = null;
		String publicKey = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = null;
		if(frontUserDetails != null) {
			frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {
				customerId = frontUserOnline.getUserId();
				publicKey = frontUserOnline.getPublicKey();
			}
		}
		try{
			model.setPaymentMethod(IConstants.EWALLET_TYPE.CREDIT_DEBIT);
			CreditCardInfo newCreditCardInfo = model.getNewCreditCardInfo();
			newCreditCardInfo.setCustomerId(customerId);
			validateCreditDebit(newCreditCardInfo);
			if (hasFieldErrors()) {
				model.setCustomerInfo(profileManager.getCustomerInfo(customerId));
				model.setListCreditCardInfo(getCreditCardList(customerId));
				initListMonthYear();
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
				return INPUT;
			}
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			String expiredMonth = newCreditCardInfo.getExpiredMonth();
			if (expiredMonth.length() == ONE) {
				expiredMonth = MONTH_BEGGINER + expiredMonth;
			}
			String expiredYear = newCreditCardInfo.getExpiredYear();
			if (expiredMonth != null && expiredYear != null) {
				String expiredDate = new StringBuffer(expiredYear).append(expiredMonth).toString();
				newCreditCardInfo.setExpiredDate(expiredDate);
			}
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			String wlCode = frontUserOnline.getWlCode();
			List<File> listFileUpload = model.getListCreditFileUpload();
			List<String> listFileUploadFileNames = model.getListCreditFileUploadFileName();
			List<FileUploadInfo> listFileUploadInfo = null;
			if (listFileUpload != null && listFileUploadFileNames != null) {
				listFileUploadInfo = getFileUploadInfo(listFileUpload, listFileUploadFileNames, customerId, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.CREDIT_CARD, IConstants.UPLOAD_DOCUMENT.DOC_KIND.DEPOSIT);
			}
			
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String updateResult = profileManager.updateCreditCard(newCreditCardInfo, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
				model.setListCreditCardInfo(getCreditCardList(customerId));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
				return INPUT;
			}
			else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent) + ": " + getText("MSG_NAF108"));
//				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
				model.setListCreditCardInfo(getCreditCardList(customerId));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
				return INPUT;
			}
			else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent) + ": " + getText("MSG_NAF109"));
//				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
				model.setListCreditCardInfo(getCreditCardList(customerId));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
				return INPUT;
			}
			model.setListCreditCardInfo(getCreditCardList(customerId));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return SUCCESS;
	}
	
	
	public String updateBankInfoSubmit() {
		try{
			getListInfo();
			initListMonthYear();
			String customerId = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
				}
			}
			model.setPaymentMethod(IConstants.EWALLET_TYPE.BANK_TRANSFER);
			BankTransferInfo newBankTransferInfo = model.getNewBankTransferInfo();
			BankTransferInfo oldBankTransferInfo = model.getOldBankTransferInfo();
			newBankTransferInfo.setCustomerId(customerId);
			validateBankTransferInfo(newBankTransferInfo);
			if (hasFieldErrors()) {		
				//[NTS1.0-le.hong.ha]May 11, 2013M - Start 
				//setRawUrl("/profile/bankInformation?result=" + "updatefail" + "&paymentMethod="+IConstants.EWALLET_TYPE.BANK_TRANSFER);
				return INPUT;
				//[NTS1.0-le.hong.ha]May 11, 2013M - End
			}
			if( newBankTransferInfo != null && oldBankTransferInfo != null) {
					String updateResult = profileManager.updateBankTransfer(newBankTransferInfo, oldBankTransferInfo);
					if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.bankaccount"));
						model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
						model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
						return INPUT;
					} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB044);
						model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
						setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.BANK_TRANSFER);
						return INPUT;
					}
			}
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.BANK_TRANSFER + "&errorMessage=" + (StringUtil.isEmpty(model.getErrorMessage())?"":model.getErrorMessage()));
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		
		return SUCCESS;
	}
	
	/**
	  * 　 update liberty 
	 * 
	 * @param
	 * @return String
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Oct 26, 2012
	 */
	public String updateLibertySubmit() {
		try{
			getListInfo();
			initListMonthYear();
			String customerId = null;
			String publicKey = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					publicKey = frontUserOnline.getPublicKey();
				}
			}
			model.setPaymentMethod(IConstants.EWALLET_TYPE.LIBERTY);
			CustomerEwalletInfo newCustomerEwalletInfo = model.getCustomerEwalletInfo();
			CustomerEwalletInfo oldCustomerEwalletInfo = model.getOldCustomerEwalletInfo();
			validateLibertyInfo(newCustomerEwalletInfo);
			if (hasFieldErrors()) {
				model.setListLiberty(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.LIBERTY));
				return INPUT;
			}
			String wlCode = frontUserOnline.getWlCode();
			List<File> listFileUpload = model.getListLibertyFileUpload();
			List<String> listFileUploadFileNames = model.getListLibertyFileUploadFileName();
			List<FileUploadInfo> listFileUploadInfo = null;
			if (listFileUpload != null && listFileUploadFileNames != null) {
				listFileUploadInfo = getFileUploadInfo(listFileUpload, listFileUploadFileNames, customerId, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.LIBERTY_RESERVE, IConstants.UPLOAD_DOCUMENT.DOC_KIND.WITHDRAW);
			}
			
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String updateResult = profileManager.updateEwallet(customerId,newCustomerEwalletInfo,oldCustomerEwalletInfo,IConstants.PAYMENT_METHOD.LIBERTY, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				return INPUT;
			} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)){
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB046);
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB046 + "&paymentMethod="+IConstants.EWALLET_TYPE.LIBERTY);
				model.setListLiberty(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.LIBERTY));
				return INPUT;
			} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED)){
				model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent) + ": " + getText("MSG_NAF108"));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.LIBERTY);
				return INPUT;
			} else if(updateResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED)){
				model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent) + ": " + getText("MSG_NAF109"));
				setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB044 + "&paymentMethod="+IConstants.EWALLET_TYPE.LIBERTY);
				return INPUT;
			}
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.LIBERTY);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		
		return SUCCESS;
	}
	
	/**
	 * Delete payment
	 */
	/*public String deletePayment() {
		try{
			getListInfo();
			String customerId = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
				}
			}
			Integer netellerType = model.getNetellerType();
			Integer payzaType = model.getPayzaType();
			Integer creditType = model.getCreditType();
			Integer bankType = model.getBankType();
			Integer libertyType = model.getLibertyType();
			Integer ewalletType = null;
			if(netellerType!=null) ewalletType =netellerType;
			else if(payzaType != null)ewalletType =payzaType;
			else if (creditType !=null)ewalletType =creditType;
			else if(bankType!=null)ewalletType =bankType;
			else if (libertyType != null) {
				ewalletType = libertyType;
			}
			if(ewalletType !=null){
				// if method is Neteller
				if(ewalletType.equals(IConstants.PAYMENT_METHOD.NETELLER)) {
					String ewalletAccNo = model.getEwalletaccount();
					String deleteResult = profileManager.deleteEwallet(customerId,ewalletAccNo,ewalletType);
					if(deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB045" + "&paymentMethod="+ewalletType);
						setMsgCode("MSG_NAB045");
						return INPUT;
					} else if (deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB018" + "&paymentMethod="+ewalletType);
						return INPUT;
					}
					setRawUrl("/profile/bankInformation?result=" + "MSG_NAB004" + "&paymentMethod="+ewalletType);
					return SUCCESS;
				
				// if method is payza
				} else if (ewalletType.equals(IConstants.PAYMENT_METHOD.PAYZA)) {
					String ewalletEmail = model.getEwalletEmail();
					String deleteResult = profileManager.deleteEwallet(customerId,ewalletEmail,ewalletType);
					if(deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB045_payza" + "&paymentMethod="+ewalletType);
						setMsgCode("MSG_NAB045");
						return INPUT;
					} else if (deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB018_payza" + "&paymentMethod="+ewalletType);
						return INPUT;
					}
					setRawUrl("/profile/bankInformation?result=" + "MSG_NAB004_payza" + "&paymentMethod="+ewalletType);
					return SUCCESS;
					
				// if method is credit debit card	
				} else if (ewalletType.equals(IConstants.PAYMENT_METHOD.CREDIT_CARD)) {
					Integer cardId = model.getCustomerCcId();
					String deleteResult = profileManager.deleteCreditcard(cardId);
					if(deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB045_credit" + "&paymentMethod="+ewalletType);
						setMsgCode("MSG_NAB045");
						return INPUT;
					} else if (deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB018_credit" + "&paymentMethod="+ewalletType);
						return INPUT;
					}
					setRawUrl("/profile/bankInformation?result=" + "MSG_NAB004_credit" + "&paymentMethod="+ewalletType);
					return SUCCESS;
				// if method is bank Info	
				} else if (ewalletType.equals(IConstants.PAYMENT_METHOD.BANK_TRANSFER)) {
//					String bankName = model.getBank();
					String accNo = model.getAccNo();
					String deleteResult = profileManager.deleteBankTransfer(customerId,accNo);
					if(deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB045_bank" + "&paymentMethod="+ewalletType);
						setMsgCode("MSG_NAB045");
						return INPUT;
					} else if (deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB018_bank" + "&paymentMethod="+ewalletType);
						return INPUT;
					}
					setRawUrl("/profile/bankInformation?result=" + "MSG_NAB004_bank" + "&paymentMethod="+ewalletType);
					return SUCCESS;
				} else if(ewalletType.equals(IConstants.PAYMENT_METHOD.LIBERTY)) { //in-case liberty
					//[NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - Start 
					String ewalletAccNo = model.getEwalletaccount();
					String deleteResult = profileManager.deleteEwallet(customerId,ewalletAccNo,ewalletType);
					if(deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB045_LIBERTY" + "&paymentMethod="+ewalletType);
						setMsgCode("MSG_NAB045");
						return INPUT;
					} else if (deleteResult.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST)) {
						setRawUrl("/profile/bankInformation?result=" + "MSG_NAB018_LIBERTY" + "&paymentMethod="+ewalletType);
						return INPUT;
					}
					setRawUrl("/profile/bankInformation?result=" + "MSG_NAB004_LIBERTY" + "&paymentMethod="+ewalletType);
					return SUCCESS;
					//[NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - End
				}
			}
			setRawUrl("/profile/bankInformation");
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return INPUT;
	}
	*/
	/**
	 * Add Bank Transfer
	 * @return
	 */
	/*public String addBankTransfer() {
		try{
			getListInfo();
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			model.setPaymentMethod(IConstants.EWALLET_TYPE.BANK_TRANSFER);
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails == null) {
				return ERROR;
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			
			String customerId = frontUserOnline.getUserId();
			BankTransferInfo bankTransferInfo = model.getNewBankTransferInfo();
			if(bankTransferInfo == null) {
				bankTransferInfo = new BankTransferInfo();
			} 
			
			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			model.setCustomerInfo(customerInfo);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			model.setListCreditCardInfo(getCreditCardList(customerId));
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			//[NTS1.0-anhndn]Jan 26, 2013A - End
					
			bankTransferInfo.setCustomerId(customerId);
			validateBankTransferInfo(bankTransferInfo);
			if (hasFieldErrors()) {	
//				model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
				return INPUT;
			}
			
			String result = profileManager.addBankTransfer(bankTransferInfo);
			if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.bankaccount"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
				return INPUT;
			} else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.bankaccount"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent));
				model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
				return INPUT;
			}
			
			//[NTS1.0-le.hong.ha]May 22, 2013M - Start 
			// reset info
			model.setNewBankTransferInfo(new BankTransferInfo());
			//[NTS1.0-le.hong.ha]May 22, 2013M - End
			
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.bankaccount"));
			model.setSuccessMessage(getText("MSG_NAB002", listContent));
//			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
//			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.BANK_TRANSFER);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}*/

	/**
	 * Add Payza Account 
	 * @return String
	 */
	
	public String addPayza() {
		try{
			getListInfo();
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			model.setPaymentMethod(IConstants.EWALLET_TYPE.PAYZA);
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;			
			}
			String customerId = frontUserOnline.getUserId();
			
			CustomerEwalletInfo customerEwalletInfo = model.getCustomerEwalletInfo();
			if(customerEwalletInfo == null) {
				customerEwalletInfo = new CustomerEwalletInfo();
			}
			
			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			model.setCustomerInfo(customerInfo);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			model.setListCreditCardInfo(getCreditCardList(customerId));
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			
			validatePayzaInfo(customerEwalletInfo);
			if (hasFieldErrors()) {	
//				model.setListPayza(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.PAYZA));
				return INPUT;
			}
			
			customerEwalletInfo.setCustomerId(customerId);

			List<FileUploadInfo> listFileUploadInfo = null;
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String wlCode = frontUserOnline.getWlCode();
			String publicKey = frontUserOnline.getPublicKey();
			String result = getProfileManager().addEwallet(customerEwalletInfo,IConstants.EWALLET_TYPE.PAYZA, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				model.setListPayza(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.PAYZA));
				return INPUT;
			}
			else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent));
				model.setListPayza(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.PAYZA));
				return INPUT;
			}
		
			model.setListPayza(getEwalletList(frontUserOnline.getUserId(),IConstants.EWALLET_TYPE.PAYZA));
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
			model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.paymentmethod.add.success", listContent));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.PAYZA);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}

	/**
	 * Add Credit/debit card
	 * @return String
	 */
	public String addCreditCard() {
		try{
			getListInfo();
			setRawUrl(IConstants.FrontEndActions.PROFILE_BANK_INFORMATION);
			Map<String, String> mapPaymentMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);
			model.setMapPaymentMethod(mapPaymentMethod);
			model.setPaymentMethod(IConstants.EWALLET_TYPE.CREDIT_DEBIT);
			Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
			model.setMapCardType(mapCardType);
			model.setListCountry(profileManager.getListCountry());
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			String wlCode = frontUserOnline.getWlCode();	
			String publicKey = frontUserOnline.getPublicKey();
			CreditCardInfo creditCardInfo = model.getNewCreditCardInfo();
			if(creditCardInfo == null) {
				creditCardInfo = new CreditCardInfo();
			}
			creditCardInfo.setCustomerId(customerId);
			String ccNo = creditCardInfo.getCcNo();
			String ccNoLastDigit = null;
			if(ccNo.length() > 4){
				ccNoLastDigit = ccNo.substring(ccNo.length()-4);
			}else {
				ccNoLastDigit = ccNo;
			}
			creditCardInfo.setCcNoLastDigit(ccNoLastDigit);

			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			model.setCustomerInfo(customerInfo);
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			initListMonthYear();
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			model.setListNeteller(getEwalletList(customerId,IConstants.EWALLET_TYPE.NETELLER));
			model.setListPayza(getEwalletList(customerId,IConstants.EWALLET_TYPE.PAYZA));
			model.setListCreditCardInfo(getCreditCardList(customerId));
			model.setListBankTransferInfo(profileManager.getBankInfo(frontUserOnline.getUserId()));
			model.setListLiberty(getEwalletList(customerId,IConstants.EWALLET_TYPE.LIBERTY));
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			
			validateCreditDebit(creditCardInfo);
			if (hasFieldErrors()) {	
				return INPUT;
			}
			
			//[NTS1.0-anhndn]Jan 25, 2013A - Start 
			String expiredMonth = creditCardInfo.getExpiredMonth();
			if (expiredMonth.length() == ONE) {
				expiredMonth = MONTH_BEGGINER + expiredMonth;
			}
			String expiredYear = creditCardInfo.getExpiredYear();
			if (expiredMonth != null && expiredYear != null) {
				String expiredDate = new StringBuffer(expiredYear).append(expiredMonth).toString();
				creditCardInfo.setExpiredDate(expiredDate);
			}
			//[NTS1.0-anhndn]Jan 25, 2013A - End
			
			List<File> listFileUpload = model.getListCreditFileUpload();
			List<String> listFileUploadFileNames = model.getListCreditFileUploadFileName();
			List<FileUploadInfo> listFileUploadInfo = null;
			
			//[NTS1.0-anhndn]Jan 26, 2013A - Start 
			if (listFileUpload == null) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent) + ": " + getText("nts.ams.fe.label.deposit.credit.card.verification_document") + " " + getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.is.require"));
				return INPUT;
			}
			//[NTS1.0-anhndn]Jan 26, 2013A - End
			
			listFileUploadInfo = getFileUploadInfo(listFileUpload, listFileUploadFileNames, customerId, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.CREDIT_CARD, IConstants.UPLOAD_DOCUMENT.DOC_KIND.DEPOSIT);

			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			String result = getProfileManager().addCreditCard(creditCardInfo, listFileUploadInfo, wlCode, fxSubGroupId,publicKey);
			if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				model.setListCreditCardInfo(getCreditCardList(frontUserOnline.getUserId()));
				return INPUT;
			} else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent));
				return INPUT;
			} else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent) + ": " + getText("MSG_NAF108"));
				return INPUT;
			} else if(result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB043",listContent) + ": " + getText("MSG_NAF109"));
				return INPUT;
			}
			model.setListCreditCardInfo(getCreditCardList(frontUserOnline.getUserId()));
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
			model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.paymentmethod.add.success", listContent));
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB003);
			setRawUrl("/profile/bankInformation?result=" + IConstants.BANK_INFO_MSGCODE.MSG_NAB003 + "&paymentMethod="+IConstants.EWALLET_TYPE.CREDIT_DEBIT);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	/**
	 * get file upload info　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 22, 2013
	 */
	private List<FileUploadInfo> getFileUploadInfo(List<File> listFileUpload, List<String> listFileUploadFileName, String customerId, Integer docType, Integer docKind) {
		String rootPath = httpRequest.getSession().getServletContext().getRealPath("/");
		List<FileUploadInfo> listFileUploadInfo = new ArrayList<FileUploadInfo>();
		FileUploadInfo fileUploadInfo = null;
		for (File f : listFileUpload) {
			fileUploadInfo = new FileUploadInfo();
			fileUploadInfo.setFile(f);
			fileUploadInfo.setCustomerId(customerId);
			fileUploadInfo.setDocType(docType);
			fileUploadInfo.setDocKind(docKind);
			fileUploadInfo.setFileName(listFileUploadFileName.get(listFileUpload.indexOf(f)));
			fileUploadInfo.setDocFileType(FileLoaderUtil.getFileType(listFileUploadFileName.get(listFileUpload.indexOf(f))));
			fileUploadInfo.setRootPath(rootPath);
			listFileUploadInfo.add(fileUploadInfo);
		}
		return listFileUploadInfo;
	}
	
	/**
	 * upload verification documents for credit card　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 19, 2013
	 */
//	public String uploadCreditDocument() {
//		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//		FrontUserOnline frontUserOnline = null;
//		if(frontUserDetails == null) {
//			return ERROR;
//		}
//		frontUserOnline = frontUserDetails.getFrontUserOnline();
//		if (frontUserOnline == null) {
//			return ERROR;
//		}
//		String wlCode = frontUserOnline.getWlCode();
//		String customerId = frontUserOnline.getUserId();
//		Integer fxSubGroupId = new Integer(0);
//		List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
//		for (CustomerServicesInfo info : listServices) {
//			if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
//				fxSubGroupId = info.getSubGroupId();
//			}
//		}
//		List<File> listFileUpload = model.getListCreditFileUpload();
//		List<String> listFileUploadFileNames = model.getListCreditFileUploadFileNames();
//		List<FileUploadInfo> listFileUploadInfo = new ArrayList<FileUploadInfo>();
//		FileUploadInfo fileUploadInfo = null;
//		for (File f : listFileUpload) {
//			fileUploadInfo = new FileUploadInfo();
//			fileUploadInfo.setFile(f);
//			fileUploadInfo.setCustomerId(customerId);
//			fileUploadInfo.setDocType(IConstants.UPLOAD_DOCUMENT.DOC_TYPE.CREDIT_CARD);
//			fileUploadInfo.setDocKind(IConstants.UPLOAD_DOCUMENT.DOC_KIND.DEPOSIT);
//			fileUploadInfo.setFileName(listFileUploadFileNames.get(listFileUpload.indexOf(f)));
//			fileUploadInfo.setDocFileType(getFileType(listFileUploadFileNames.get(listFileUpload.indexOf(f))));
//			listFileUploadInfo.add(fileUploadInfo);
//		}
//		
//		Integer customerCCId = model.getCustomerCcId();
//		profileManager.uploadCreditCardDocument(listFileUploadInfo, customerId, customerCCId, wlCode, fxSubGroupId);
//		
//		return SUCCESS;
//	}
	
	/**
	 * upload document for verification of liberty reverse　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 22, 2013
	 */
//	public String uploadLibertyDocument() {
//		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//		FrontUserOnline frontUserOnline = null;
//		if(frontUserDetails == null) {
//			return ERROR;
//		}
//		frontUserOnline = frontUserDetails.getFrontUserOnline();
//		if (frontUserOnline == null) {
//			return ERROR;
//		}
//		String wlCode = frontUserOnline.getWlCode();
//		String customerId = frontUserOnline.getUserId();
//		Integer fxSubGroupId = new Integer(0);
//		List<CustomerServicesInfo> listServices = frontUserOnline.getListCustomerServiceInfo();
//		for (CustomerServicesInfo info : listServices) {
//			if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
//				fxSubGroupId = info.getSubGroupId();
//			}
//		}
//		List<File> listFileUpload = model.getListLibertyFileUpload();
//		List<String> listFileUploadFileNames = model.getListLibertyFileUploadFileNames();
//		List<FileUploadInfo> listFileUploadInfo = new ArrayList<FileUploadInfo>();
//		FileUploadInfo fileUploadInfo = null;
//		for (File f : listFileUpload) {
//			fileUploadInfo = new FileUploadInfo();
//			fileUploadInfo.setFile(f);
//			fileUploadInfo.setCustomerId(customerId);
//			fileUploadInfo.setDocType(IConstants.UPLOAD_DOCUMENT.DOC_TYPE.LIBERTY_RESERVE);
//			fileUploadInfo.setDocKind(IConstants.UPLOAD_DOCUMENT.DOC_KIND.WITHDRAW);
//			fileUploadInfo.setFileName(listFileUploadFileNames.get(listFileUpload.indexOf(f)));
//			fileUploadInfo.setDocFileType(getFileType(listFileUploadFileNames.get(listFileUpload.indexOf(f))));
//			listFileUploadInfo.add(fileUploadInfo);
//		}
//		
//		Integer customerCCId = model.getCustomerCcId();
//		profileManager.uploadCreditCardDocument(listFileUploadInfo, customerId, customerCCId, wlCode, fxSubGroupId);
//		
//		return SUCCESS;
//	}
	
	/**
	 * validate liberty　
	 * 
	 * @param info CustomerEwalletInfos
	 * @return
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Oct 25, 2012
	 */
	private void validateLibertyInfo(CustomerEwalletInfo info) {
		hasFieldErrors();
		final String PATTERN = "^[A-Za-z][0-9]{7}$";
		Pattern pattern = Pattern.compile(PATTERN);
		if (info != null) {
			String accountNumber = info.getEwalletAccNo().trim();
//			String apiName = info.getEwalletApiName().trim();
//			String securityWord = info.getEwalletSecureWord().trim();
			if (accountNumber == null || StringUtils.isBlank(accountNumber)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty.accountNumber"));
				model.setErrorMessage(getText("MSG_NAF001", listContent));
				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
				return;
			} else {
				Matcher match = pattern.matcher(accountNumber);
				if (!match.matches()) { 
					List<String> listContent = new ArrayList<String>();
					listContent.add(getText("nts.ams.fe.label.bank_information.liberty.accountNumber"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB007", listContent));
					addFieldError("errorMessage", getText("nts.ams.fe.message.bank_information.MSG_NAB007", listContent));
					return;
				}
			}
			
//			if (apiName == null || StringUtils.isBlank(apiName)) {
//				List<String> listContent = new ArrayList<String>();
//				listContent.add(getText("nts.ams.fe.label.bank_information.liberty.apiName"));
//				model.setErrorMessage(getText("MSG_NAF001", listContent));
//				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
//				return;
//			}
//			
//			if (securityWord == null || StringUtils.isBlank(securityWord)) {
//				List<String> listContent = new ArrayList<String>();
//				listContent.add(getText("nts.ams.fe.label.bank_information.liberty.securityWord"));
//				model.setErrorMessage(getText("MSG_NAF001", listContent));
//				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
//				return;
//			}
		}
	}
	
	// Validate Neteller
	private void validateNetellerInfo(CustomerEwalletInfo info) {
		hasFieldErrors();
		if(info != null) {
			String accountId = info.getEwalletAccNo().trim();
			String secureId = info.getEwalletSecureId().trim();
			if (accountId == null || StringUtils.isBlank(accountId)) {				
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.accountId"));
				model.setErrorMessage(getText("MSG_NAF001", listContent));
				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
				return;
			}
			if(accountId.length()<12){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.accountId"));
				model.setErrorMessage(getText("MSG_SC_074", listContent));
				addFieldError("errorMessage", getText("MSG_SC_074", listContent));
				return;
			}
//			}else if(MathUtil.parseLong(accountId) == null){
//				List<String> listContent = new ArrayList<String>();
//				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.accountId"));
//				model.setErrorMessage(getText("MSG_NAB053", listContent));
//				addFieldError("errorMessage", getText("MSG_NAB053", listContent));
//				return;
//			}
			if (secureId == null || StringUtils.isBlank(secureId)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.secureId"));
				model.setErrorMessage(getText("MSG_NAF001", listContent));
				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
				return;
			} 
			if(secureId.length() < 6){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.secureId"));
				model.setErrorMessage(getText("MSG_SC_075", listContent));
				addFieldError("errorMessage", getText("MSG_SC_075", listContent));
				return;
			}
//			}else if(MathUtil.parseLong(secureId) == null){
//				List<String> listContent = new ArrayList<String>();
//				listContent.add(getText("nts.ams.fe.label.bank_information.neteller.secureId"));
//				model.setErrorMessage(getText("MSG_NAB053", listContent));
//				addFieldError("errorMessage", getText("MSG_NAB053", listContent));
//				return;
//			}
		}
	}
		
	// validate BankTransfer Information
	private void validateBankTransferInfo(BankTransferInfo bankTransferInfo) {
		hasFieldErrors();
		String accNumber = null;
//		String beneficiaryName = null;
		
		//[NTS1.0-le.hong.ha]May 13, 2013M - Start 
		//Not use
//		String swifcode = null;
//		String bankAddress = null;
//		String countryId = null;
		//[NTS1.0-le.hong.ha]May 13, 2013M - End
		
		String branchName = null;
		if(bankTransferInfo != null) {
			bankName = bankTransferInfo.getBankName();
			branchName = bankTransferInfo.getBranchName();
			accNumber = bankTransferInfo.getAccountNumber();
//			beneficiaryName = bankTransferInfo.getBeneficiaryName();
//			swifcode = bankTransferInfo.getSwiftCode();
//			bankAddress = bankTransferInfo.getBankAddress();
//			countryId = bankTransferInfo.getCountryId();
		}
		if (StringUtils.isBlank(bankName)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.bank"));
			model.setErrorMessage(getText("MSG_NAB001", listContent));
			addFieldError("errorMessage", getText("MSG_NAB001", listContent));
			return;
		} 
		if (StringUtils.isBlank(branchName)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.branch"));
			model.setErrorMessage(getText("MSG_NAB001", listContent));
			addFieldError("errorMessage", getText("MSG_NAB001", listContent));
			return;
		} 
		if (StringUtils.isBlank(accNumber)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.accountNumber"));
			model.setErrorMessage(getText("MSG_NAB001",listContent));
			addFieldError("errorMessage",getText("MSG_NAB001",listContent));
			return;
		}else{
			if (accNumber.length() != 7) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer.accountNumber"));
				// nts.ams.fe.label.bankinfo.bank_acc_no_is_7
				model.setErrorMessage(getText("MSG_NAB007",listContent));
				addFieldError("errorMessage",getText("MSG_NAB007",listContent));
				return;
			}
		}
		
	}
	
	// validate Payza information
	private void validatePayzaInfo(CustomerEwalletInfo info) {
		hasFieldErrors();
		if(info != null) {
			String email = info.getEwalletEmail();
			String apiPassword = info.getEwalletApiPassword();
			if (email == null || StringUtils.isBlank(email)) {
				model.setErrorMessage(getText("nts.ams.fe.label.bank_information.payza.email") + getText("MSG_NAF001"));
				addFieldError("errorMessage", getText("nts.ams.fe.label.bank_information.payza.email") + getText("MSG_NAF001"));
				return;
			} else {
				if(!StringUtil.isEmail(email)) {
					List<Object> listContent = new ArrayList<Object>();
					listContent.add(getText("nts.ams.fe.label.bank_information.payza.email"));
					model.setErrorMessage(getText("MSG_NAB053", listContent));
					addFieldError("errorMessage",  getText("MSG_NAB053", listContent));
					return;					
				}
			}
			if (apiPassword == null || StringUtils.isBlank(apiPassword)) {
				model.setErrorMessage(getText("nts.ams.fe.label.bank_information.payza.apiPassword") + getText("MSG_NAF001"));
				addFieldError("errorMessage", getText("nts.ams.fe.label.bank_information.neteller.secureId") + getText("MSG_NAF001"));
				return;
			}
		}

			
	}

	
	/**
	 * Get CustomerEwallet list for displaying on the table
	 * @param customerEwalletinfo
	 * @return
	 */

	public List<CustomerEwalletInfo> getEwalletList(String customerId, Integer ewalletType) {
		List<CustomerEwalletInfo> listCustomerEwalletInfo = null;
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					String publicKey = frontUserOnline.getPublicKey();
					listCustomerEwalletInfo = getProfileManager().getEwalletList(customerId, ewalletType,publicKey);
					if(listCustomerEwalletInfo != null && listCustomerEwalletInfo.size() > 0) {
						if(ewalletType == IConstants.EWALLET_TYPE.NETELLER) {
							for(int i=0;i<listCustomerEwalletInfo.size();i++) {
								listCustomerEwalletInfo.get(i).setEwalletSecureId(model.formatPassword(listCustomerEwalletInfo.get(i).getEwalletSecureId()));
							}
						} else if (ewalletType == IConstants.EWALLET_TYPE.PAYZA) {
							for(int i=0;i<listCustomerEwalletInfo.size();i++) {
								listCustomerEwalletInfo.get(i).setEwalletApiPassword(model.displayPassword(listCustomerEwalletInfo.get(i).getEwalletApiPassword()));
							}
						} else if (ewalletType == IConstants.EWALLET_TYPE.LIBERTY) {
							for(int i=0;i<listCustomerEwalletInfo.size();i++) {
								listCustomerEwalletInfo.get(i).setEwalletSecureWord(model.formatPassword(listCustomerEwalletInfo.get(i).getEwalletSecureWord()));
							}
						} 
					}
				}
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listCustomerEwalletInfo;
	}
	
	public String getLast3Digit(String secureID){
		String last3Digit = "";
		try {
			if (secureID != null) {
				last3Digit = secureID.substring(secureID.length()-2);
			}
		} catch (Exception e) {
		}
		return last3Digit;
	}
	
	/**
	 * 　
	 * update profile information
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 11, 2012
	 * @MdDate
	 */
	
	public String updateProfile() {
		try {
			String wlCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null) {
					wlCode = frontUserOnline.getWlCode();
				}
			}
			model.setListCountry(profileManager.getListCountry());
			CustomerInfo customerInfo = model.getCustomerInfo();
			customerInfo.setCustomerId(getCurrentCustomerId());
			
			Map<String, String> mapLanguage = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.LANGUAGE);
			customerInfo.setDisplayLanguageName(mapLanguage.get(StringUtil.toUpperCase(customerInfo.getDisplayLanguage())));
			String mt4Account = model.getMt4Account();
			String mt4Password = model.getMt4NewPass();
			String mt4ConfirmPassword = model.getMt4ConfirmPass();
			String mt4NewInvestorPassword = model.getMt4InvestorNewPass();
			String mt4ConfirmInvestorPassword = model.getMt4InvestorConfirmPass();
			
			//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
			Map<String, String> mapFinancilAssets = SystemPropertyConfig.getInstance().getMap(ITrsConstants.SYS_PROPERTY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.CURRENT_ASSET);
			model.setMapFinancilAssets(mapFinancilAssets);

			Map<String, String> mapPrefectures = this.getPrefecturesMap();
			model.setMapPrefecture(mapPrefectures);
			
			customerInfo.setFinancilAssetsName(model.getMapFinancilAssets().get(customerInfo.getFinancilAssets() + ""));
			
			if(customerInfo.getCorporationType() == 0){
				String houseNumber = (customerInfo.getHouseNumber() != null && !customerInfo.getHouseNumber().equals("null")) ? customerInfo.getHouseNumber()+"" : "";
				customerInfo.setAddress(customerInfo.getPrefecture() + " " + customerInfo.getCity() + " " + customerInfo.getSection() + " " + customerInfo.getBuildingName() + " " + houseNumber);
			}else{
				String houseNumber = (customerInfo.getHouseNumber() != null && !customerInfo.getHouseNumber().equals("null")) ? customerInfo.getHouseNumber()+"" : "";
				customerInfo.setAddress(customerInfo.getPrefecture() + " " + customerInfo.getCity() + " " + customerInfo.getSection() + " " + customerInfo.getBuildingName() + " " + houseNumber);
				customerInfo.setCorpPicAddress(customerInfo.getCorpPicPrefecture() + " " + customerInfo.getCorpPicCity() + " " + customerInfo.getCorpPicSection() + " " + customerInfo.getCorpPicBuildingName() + " " + houseNumber);
			}
			
			
			if(customerInfo.getCorporationType() == 0){
				String firstName = StringUtil.isEmpty(customerInfo.getFirstName()) ? "" : customerInfo.getFirstName();
				String lastName = StringUtil.isEmpty(customerInfo.getLastName()) ? "" : customerInfo.getLastName();
				customerInfo.setFullName(firstName + " " + lastName);
			}
			
			List<String> listPurposes = new ArrayList<String>();	
			if(customerInfo.isPurposeShortTermFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose1"));
			}
			if(customerInfo.isPurposeLongTermFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose2"));
			}
			if(customerInfo.isPurposeExchangeFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose3"));
			}
			if(customerInfo.isPurposeSwapFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose4"));
			}
			if(customerInfo.isPurposeHedgeAssetFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose5"));
			}
			if(customerInfo.isPurposeHighIntFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose6"));
			}
			if(customerInfo.isPurposeEconomicFlg() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose7"));
			}
			if(customerInfo.isPurposeOther() == true){
				listPurposes.add(getText("nts.ams.fe.label.customer_information.purepose8"));
				listPurposes.add(customerInfo.getPurposeOtherComment());
			}
			customerInfo.setListPurposes(listPurposes);
			initYearMonthDay();
			//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
			
			validateProfileForm();
			if(hasFieldErrors()) {		
				model.setListDay(Utilities.getListCalendar(IConstants.DATE_CALENDAR.DAY, true));
				model.setListMonth(getMapMonth());
				Integer years = new Integer(0);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				years = cal.get(Calendar.YEAR);
				model.setListYear(Utilities.getListCalendar(IConstants.CALENDAR.FROM_YEAR,IConstants.CALENDAR.TO_YEAR,false));
				
				model.setListCountry(profileManager.getListCountry());
				
				Map<String, String> mapLeverage = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_LEVERAGE);
				ComparatorUtil comparatorUtil = new ComparatorUtil(mapLeverage);
				TreeMap<String, String> mapLeverageSorted = new TreeMap<String, String>(comparatorUtil);	
				mapLeverageSorted.putAll(mapLeverage);
				model.setMapLeverage(mapLeverageSorted);
				model.setMapLanguage(mapLanguage);
				model.setIsOpenPassword(0);
				model.setIsChangeMt4Pass("0");
				model.setMt4Account(mt4Account);
				model.setMt4NewPass(mt4Password);
				model.setMt4ConfirmPass(mt4ConfirmPassword);
				model.setMt4InvestorNewPass(mt4NewInvestorPassword);
				model.setMt4InvestorConfirmPass(mt4ConfirmInvestorPassword);
				Map<String, String> mapMt4Account = new HashMap<String, String>();
				mapMt4Account =	SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_ACCOUNT);
				model.setMapMt4Account(mapMt4Account);
				return ERROR;
			}	
		} catch (Exception e) {
			log.error(e.getMessage(),e);		
			model.setListCountry(profileManager.getListCountry());
			model.setIsOpenPassword(0);
			return ERROR;
		}
		return SUCCESS;
	}

	private void initYearMonthDay(){
		Map<String, String> days = new LinkedHashMap<String, String>();
		Map<String, String> months = new LinkedHashMap<String, String>();
		Map<String, String> years = new LinkedHashMap<String, String>();
		days.put("-1", getText("nts.ams.fe.label.customer_information.beneficOwner.foundation.day"));
		months.put("-1", getText("nts.ams.fe.label.customer_information.beneficOwner.foundation.month"));
		years.put("-1", getText("nts.ams.fe.label.customer_information.beneficOwner.foundation.year"));
		for (int i = 1; i <= 31; i++) {
			String d = "";
			if(i < 10){
				d = "0" + i;
			}else{
				d = i+"";
			}
			days.put(d, d);
			
			if(i <= 12){
				months.put(d, d);
			}
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int curYear = cal.get(Calendar.YEAR);
		for (int i = 1900; i <= curYear; i++) {
			years.put(i+"", i+"");
		}
		model.setYears(years);
		model.setMonths(months);
		model.setDays(days);
	}
	
	/**
	 * 
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Dec 10, 2012
	 */
	private Map<String, String> getMapMonth() {
		@SuppressWarnings("unchecked")
		Map<String, String> mapMonth = new LinkedMap();
		mapMonth.put("1", getText("month_in_year.jan"));
		mapMonth.put("2", getText("month_in_year.feb"));
		mapMonth.put("3", getText("month_in_year.mar"));
		mapMonth.put("4", getText("month_in_year.apr"));
		mapMonth.put("5", getText("month_in_year.may"));
		mapMonth.put("6", getText("month_in_year.jun"));
		mapMonth.put("7", getText("month_in_year.jul"));
		mapMonth.put("8", getText("month_in_year.aug"));
		mapMonth.put("9", getText("month_in_year.sep"));
		mapMonth.put("10", getText("month_in_year.oct"));
		mapMonth.put("11", getText("month_in_year.nov"));
		mapMonth.put("12", getText("month_in_year.dec"));
		return mapMonth;
	}
	/**
	 * 　
	 * comfirm update profile
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 11, 2012
	 * @MdDate
	 */
	
	public String updateProfileConfirm() {
		try {
			Map<Integer, Boolean> mapCustomerServices = null;
			model.setListCountry(profileManager.getListCountry());
			CustomerInfo customerInfo = model.getCustomerInfo();
			
			// Set full Name
			if(customerInfo.isChangeCustomerName()){
				String firstName = !StringUtil.isEmpty(customerInfo.getFirstName()) ? customerInfo.getFirstName() : "";
				String lastName = !StringUtil.isEmpty(customerInfo.getLastName()) ? customerInfo.getLastName() : "";
				customerInfo.setFullName(firstName + "　" + lastName);
			}
			if(customerInfo.isChangeCorpRepName()){
				String repFirstName = !StringUtil.isEmpty(customerInfo.getCorpRepFirstname()) ? customerInfo.getCorpRepFirstname() : "";
				String repLastName = !StringUtil.isEmpty(customerInfo.getCorpRepLastname()) ? customerInfo.getCorpRepLastname() : "";
				customerInfo.setCorpRepFullname(repFirstName + "　" + repLastName);		
			}
			
			// Add for #20778
			if(customerInfo.getCorporationType().equals(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER)){
				if(customerInfo.getBeneficOwnerFlg() != 1){
					customerInfo.setBeneficOwnerFullname("");
					customerInfo.setBeneficOwnerFullnameKana("");
					customerInfo.setBeneficOwnerFirstname("");
					customerInfo.setBeneficOwnerFirstnameKana("");
					customerInfo.setBeneficOwnerZipcode("");
					customerInfo.setBeneficOwnerPrefecture("");
					customerInfo.setBeneficOwnerCity("");
					customerInfo.setBeneficOwnerSection("");
					customerInfo.setBeneficOwnerBuildingName("");
					customerInfo.setBeneficOwnerEstablishDate("");
					customerInfo.setBeneficOwnerTel("");
				}else{
					customerInfo.setBeneficOwnerEstablishDate(customerInfo.getBeneficOwnerEstablishDateYear() + customerInfo.getBeneficOwnerEstablishDateMonth() + customerInfo.getBeneficOwnerEstablishDateDay());
				}
			}
				
			
			int ischangePass = model.getIsOpenPassword();
			String customerId = null;
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null) {
					customerId = frontUserOnline.getUserId();
					mapCustomerServices = frontUserOnline.getMapCustomerService();
					customerInfo.setWlCode(frontUserOnline.getWlCode());
				}
			}
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
			if(customerServiceInfo == null) {
				setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_FAIL);	
				log.error("Cannot find customerService info of customerId: " + customerId);
				return ERROR;
			}
			Boolean isOpenFx = mapCustomerServices.containsKey(IConstants.SERVICES_TYPE.FX);
			if(ischangePass !=0 && ischangePass%2!=0) {
				// check current passowrd
				String md5Password = customerInfo.getMd5IndentifyPassword();
				CustomerInfo amsCustomerInfo = accountManager.getCustomerInfo(customerId);
				if(amsCustomerInfo != null) {
					if(!amsCustomerInfo.getLoginPass().equals(md5Password)) {
						setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_INVALID_PASSWORD);
						return ERROR;
					} 
				}
				customerInfo.setChangePasswordFlag(true);
				//[NTS1.0-le.hong.ha]Apr 26, 2013A - Start 
				// Now password and MT4 password is the same
				model.setIsChangeMt4Pass(IConstants.ENABLE_FLG.ENABLE.toString());
				model.setMt4NewPass(customerInfo.getNewPassword());
				model.setMt4InvestorNewPass(customerInfo.getNewPassword());
				//[NTS1.0-le.hong.ha]Apr 26, 2013A - End
			}
			
//			 TODO fix for testing 
			Integer updateResult = IConstant.ACCOUNT_UPDATE_SUCCESS; //profileManager.updateProfile(customerInfo, customerServiceInfo, isOpenFx);

//			updateResult = IConstant.ACCOUNT_UPDATE_SUCCESS;
			log.warn("=========> update result = " + updateResult + "===========<");
			if(IConstant.ACCOUNT_UPDATE_SUCCESS != updateResult) {
				setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_FAIL);	
				model.setListCountry(profileManager.getListCountry());
				return ERROR;
			}
			
			//[NTS1.0-le.hong.ha]Apr 23, 2013A - Start 
			if (customerInfo.isChangeCustomerName() || customerInfo.isChangeAddress() || customerInfo.isChangeCorpName() || customerInfo.isChangeCorpAddress() || customerInfo.isChangeCorpOwnerName()
					|| customerInfo.isChangeCorpRefName() || customerInfo.isChangeCorpRefAddress() || customerInfo.isChangeCorpRepName()) {
				// Send mail inform to CS
				profileManager.sendmailChangeInfoToCS(customerInfo);
				// Send mail to customer
				profileManager.sendmailChangeInfoToCustomer(customerInfo);
			}
			//[NTS1.0-le.hong.ha]Apr 23, 2013A - End
			
			//[NTS1.0-le.hong.ha]May 10, 2013A - Start 
			// Synchronize customer information to SaleFoce
			profileManager.syncCustomerInfoToSaleFace(customerInfo);
			//[NTS1.0-le.hong.ha]May 10, 2013A - End
			
			String countryCode = profileManager.getCountryCodeFromCountryId(customerInfo.getCountryId());
			if(countryCode != null){
				frontUserOnline.setCountryCode(countryCode);
			}
			String isChangeMt4Pass = model.getIsChangeMt4Pass();
			if(isChangeMt4Pass.equals(IConstants.ENABLE_FLG.ENABLE.toString()) ){
				//Halh change
				//For TRS have 3 service type Social, FX, Demo therefor update three MT4 account
				CustomerServicesInfo customerServiceDemoMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.DEMO_FXCD);
				CustomerServicesInfo customerServiceMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.FX);
				CustomerServicesInfo customerServiceSocialMt4 = accountManager.getCustomerServiceInfo(customerId, ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
				
				String password = model.getMt4NewPass();
				String investorPass = model.getMt4InvestorNewPass();
				Integer resultMt4Demo = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				// Update account demo
				if(customerServiceDemoMt4 != null){
					String loginIdDemo = customerServiceMt4.getCustomerServiceId();
					UserRecord userRecord = new UserRecord();
					userRecord.setLogin(MathUtil.parseInt(loginIdDemo));
					userRecord.setPassword(password);
					userRecord.setPasswordInvestor(investorPass);
					userRecord.setEnable(UserRecord.NO_UPDATE);
					userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
					userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
					resultMt4Demo = MT4Manager.getInstance().updateDemoAccountMt4(userRecord);
				}
				
				// Update account FX
				Integer resultMt4 = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				if(customerServiceMt4 != null){
					String loginId = customerServiceMt4.getCustomerServiceId();
					resultMt4 = MT4Manager.getInstance().changePassword(MathUtil.parseInt(loginId), password, investorPass);
				}
				
				// Update account Social
				Integer resultMt4Social = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				if(customerServiceSocialMt4 != null){
					String loginId = customerServiceSocialMt4.getCustomerServiceId();
					resultMt4Social = MT4Manager.getInstance().changePassword(MathUtil.parseInt(loginId), password, investorPass);
				}
				
				if(!resultMt4.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS) || !resultMt4Demo.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS) || !resultMt4Social.equals(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS)){
					setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_FAIL);	
					model.setListCountry(profileManager.getListCountry());
					return ERROR;
				}
			}
			
			changeCustomerName = customerInfo.isChangeCustomerName();
			changeAddress = customerInfo.isChangeAddress();
			changeCorpName = customerInfo.isChangeCorpName();
			changeCorpAddress = customerInfo.isChangeCorpAddress();
			changeCorpRefName = customerInfo.isChangeCorpRefName();
			changeCorpRefAddress = customerInfo.isChangeCorpRefAddress();
			changeCorpRepName = customerInfo.isChangeCorpRepName();
			
			// Check change name or adress
			if(customerInfo.isChangeCorpOwnerName() && customerInfo.getBeneficOwnerFlg() == 1){
				CustomerInfo oldCustomerInfo = profileManager.getCustomerInfo(customerId);
				String oldName = oldCustomerInfo.getBeneficOwnerFullname() != null ? oldCustomerInfo.getBeneficOwnerFullname() : "";
				String newName = customerInfo.getBeneficOwnerFullname() != null ? customerInfo.getBeneficOwnerFullname() : "";
				
				String oldNameKana = oldCustomerInfo.getBeneficOwnerFullnameKana() != null ? oldCustomerInfo.getBeneficOwnerFullnameKana() : "";
				String newNameKana = customerInfo.getBeneficOwnerFullnameKana() != null ? customerInfo.getBeneficOwnerFullnameKana() : "";
				
				String oldZipcode = oldCustomerInfo.getBeneficOwnerZipcode() != null ? oldCustomerInfo.getBeneficOwnerZipcode() : "";
				String newZipcode = customerInfo.getBeneficOwnerZipcode() != null ? customerInfo.getBeneficOwnerZipcode() : "";
				
				String oldPreficture = oldCustomerInfo.getBeneficOwnerPrefecture() != null ? oldCustomerInfo.getBeneficOwnerPrefecture() : "";
				String newPreficture = customerInfo.getBeneficOwnerPrefecture() != null ? customerInfo.getBeneficOwnerPrefecture() : "";
				
				String oldCity = oldCustomerInfo.getBeneficOwnerCity() != null ? oldCustomerInfo.getBeneficOwnerCity() : "";
				String newCity = customerInfo.getBeneficOwnerCity() != null ? customerInfo.getBeneficOwnerCity() : "";
				
				String oldSection = oldCustomerInfo.getBeneficOwnerSection() != null ? oldCustomerInfo.getBeneficOwnerSection() : "";
				String newSection = customerInfo.getBeneficOwnerSection() != null ? customerInfo.getBeneficOwnerSection() : "";
				
				if(!oldName.equals(newName) || !oldNameKana.equals(newNameKana) || !oldZipcode.equals(newZipcode) || !oldPreficture.equals(newPreficture) || !oldCity.equals(newCity) || !oldSection.equals(newSection)){
					changeCorpOwnerName = true;
				}else{
					changeCorpOwnerName = false;
				}
			}else{
				changeCorpOwnerName = false;
			}
			
			log.info("Set message update success for updateProfile with customerId: " + customerId);
			customerInfo = getProfileManager().getCustomerInfo(customerId);
			SocialMemcached.getInstance().saveCustomerInfo(customerInfo);
			setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_SUCCESS);
			
			// change language 
			setUserLanguage(customerInfo.getDisplayLanguage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);	
			model.setListCountry(profileManager.getListCountry());
			setMsgCode(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_FAIL);	
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the msgCode
	 */
	public String getMsgCode() {
		return msgCode;
	}

	/**
	 * @param msgCode the msgCode to set
	 */
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}	
	private void getMsgCode(String msgCode) {
		if(msgCode != null) {
			if(msgCode.equalsIgnoreCase(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_SUCCESS)) {
				//[NTS1.0-le.hong.ha]Jun 6, 2013A - Start 
				// Check what user updated and set corresponseding success message
				List<String> listContent = new ArrayList<String>();
				if(changeCustomerName || changeAddress){
					model.setSuccessMessage(getText("MSG_TRS_NAF_0043"));
				}else if(changeCorpName || changeCorpAddress || changeCorpRepName || changeCorpRefName){
					listContent.add(getText("corp_name_address_change"));
					model.setSuccessMessage(getText("MSG_TRS_NAF_0045", listContent));
				}else if(changeCorpOwnerName){
					listContent.add(getText("corp_name_address_change_bene"));
					model.setSuccessMessage(getText("MSG_TRS_NAF_0045", listContent));
				}else{
					listContent.add(getText("registration.information"));
					model.setSuccessMessage(getText("MSG_NAB003",listContent));
				}
				//[NTS1.0-le.hong.ha]Jun 6, 2013A - End
			} 
			if(msgCode.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS)) {
				List<String> listContent = new ArrayList<String>();
				if(model.getPaymentMethod()==IConstants.EWALLET_TYPE.NETELLER) {
					listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.paymentmethod.add.success", listContent));
				}
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.INPUT_ERROR)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("MSG_NAB053", listContent));
			}
			
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_UPDATE_FAIL)) {
				model.setErrorMessage(getText("nts.ams.fe.message.profile.update.failure"));
			}
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_INVALID_PASSWORD)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.current_password"));
				model.setErrorMessage(getText("MSG_SC_047",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB053_BIRHDAY)) {
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(getText("nts.socialtrading.scfe018.basicinfo.label.birthday"));
				model.setErrorMessage(getText("MSG_NAB053", listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_SUCCESS)) {
				model.setSuccessMessage(getText("nts.ams.fe.message.profile.update.success"));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_FAILURE)) {
				model.setErrorMessage(getText("nts.ams.fe.message.profile.update.failure"));
				addFieldError("errorMessage",getText("MSG_NAB044"));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB045)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB045",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB018)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB018",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB004)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB004", listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB045_PAYZA)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB045",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB018_PAYZA)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB018",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB004_PAYZA)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB004", listContent));
			}
			
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB045_CREDIT)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB045",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB018_CREDIT)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB018",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB004_CREDIT)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB004", listContent));
			}
			
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB045_BANK)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB045",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB018_BANK)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB018",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB004_BANK)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB004", listContent));
			}
			
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB045_LIBERTY)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB045",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB018_LIBERTY)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB018",listContent));
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB004_LIBERTY)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
				model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB004", listContent));
			}
			
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB044)) {
				List<String> listContent = new ArrayList<String>();
				if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.NETELLER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.PAYZA)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.CREDIT_CARD)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.BANK_TRANSFER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB044", listContent));
				}
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB046)) {
				List<String> listContent = new ArrayList<String>();
				if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.NETELLER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011",listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.PAYZA)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
					model.setErrorMessage(getText("nts.ams.fe.message.bank_information.credit.MSG_NAB011", listContent));
				}
			}
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB002)) {
				List<String> listContent = new ArrayList<String>();
				if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.NETELLER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
					model.setSuccessMessage(getText("MSG_NAB002", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.PAYZA)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
					model.setSuccessMessage(getText("MSG_NAB002", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.CREDIT_CARD)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
					model.setSuccessMessage(getText("MSG_NAB002", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.BANK_TRANSFER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
					model.setSuccessMessage(getText("MSG_NAB002", listContent));
				} else if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.LIBERTY)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
					model.setSuccessMessage(getText("MSG_NAB002", listContent));
				}
			}
			
			if(msgCode.equals(IConstants.BANK_INFO_MSGCODE.MSG_NAB003)) {
				List<String> listContent = new ArrayList<String>();
				if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.NETELLER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.neteller"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB003", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.PAYZA)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.payza"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB003", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.CREDIT_CARD)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.creditCard"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB003", listContent));
				} else if (model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.BANK_TRANSFER)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB003", listContent));
				} else if(model.getPaymentMethod().equals(IConstants.PAYMENT_METHOD.LIBERTY)) {
					listContent.add(getText("nts.ams.fe.label.bank_information.liberty"));
					model.setSuccessMessage(getText("nts.ams.fe.message.bank_information.MSG_NAB003", listContent));
				}
			}
			
			if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_008)) {
				model.setErrorMessage(getText("MSG_SC_008"));
			} else if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_009)) {
				model.setErrorMessage(getText("MSG_SC_009"));
			} else if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_NAF109)) {
				model.setErrorMsgSizeLimit(getText("MSG_NAF109"));
			} else if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_NAF108)) {
				model.setErrorMessage(getText("MSG_NAF108"));
			} else if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_SUCCESS)) {
				model.setSuccessMessage(getText("nts.ams.fe.label.customer_information.verify.messages.upload.success"));
			} else if(msgCode.equals(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR)) {
				model.setErrorMessage(getText("nts.ams.fe.label.customer_information.verify.messages.upload.fail"));
			} else if(msgCode.equals(IConstants.VERIFY_ACCOUNT_MSG_CODE.MSG_DOWNLOAD_FILE_NOT_FOUND)){
				model.setErrorMessage(getText("nts.ams.fe.label.customer_information.download.fail"));
			} else if(msgCode.equals(ITrsConstants.UPLOAD_DOCUMENT.MSG_CODE.FILE_REQUIRED)){
				model.setErrorMessage(getText("avartar.file.not.input"));
			}
			
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_ENABLE_SUCCESS)){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.enable"));
				model.setSuccessMessage(getText("MSG_SC_052",listContent));
			}
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_DISABLE_SUCCESS)){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.disable"));
				model.setSuccessMessage(getText("MSG_SC_052",listContent));
			}
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_ENABLE_UNSUCCESS)){
				//List<String> listContent = new ArrayList<String>();
				//listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.enable"));
				model.setErrorMessage(getText("MSG_SC_053"));
			}
			if(msgCode.equals(IConstants.PROFILE_MSG_CODE.MSG_DISABLE_UNSUCCESS)){
				//List<String> listContent = new ArrayList<String>();
				//listContent.add(getText("nts.socialtrading.scfe018.brokersetting.label.disable"));
				model.setErrorMessage(getText("MSG_SC_053"));
			}
		}
	}	
	
	private void validateProfileForm(){
		clearFieldErrors();
		CustomerInfo customerInfo = model.getCustomerInfo();
		if(customerInfo!=null){
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
			
			//[NTS1.0-le.hong.ha]Apr 18, 2013A - Start 
			String tel1 = customerInfo.getTel1();
			String tel2 = customerInfo.getTel2();
			String additionalMail;
			if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
				additionalMail = customerInfo.getMailAddtional();
			}else{
				additionalMail = customerInfo.getCorpPicMailMobile();
			}
			 
			String mailMain = customerInfo.getMailMain();
			if(!validateBaseCustomerInfo(customerInfo, tel1, tel2, additionalMail, mailMain)){
				return;
			}
			
			boolean purposeShortTermFlg = customerInfo.isPurposeShortTermFlg();
			boolean purposeLongTermFlg = customerInfo.isPurposeLongTermFlg();
			boolean purposeExchangeFlg = customerInfo.isPurposeExchangeFlg();
			boolean purposeSwapFlg = customerInfo.isPurposeSwapFlg();
			boolean purposeHedgeAssetFlg = customerInfo.isPurposeHedgeAssetFlg();
			boolean purposeHighIntFlg = customerInfo.isPurposeHighIntFlg();
			boolean purposeEconomicFlg = customerInfo.isPurposeEconomicFlg();
			boolean purposeOther = customerInfo.isPurposeOther();
			if(!purposeShortTermFlg&&!purposeLongTermFlg&&!purposeExchangeFlg&&!purposeSwapFlg&&!purposeHedgeAssetFlg&&!purposeHighIntFlg&&!purposeEconomicFlg&&!purposeOther){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.purposeShortTermFlg"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return;
			}
			if(customerInfo.isPurposeOther()){
				if(StringUtil.isEmpty(customerInfo.getPurposeOtherComment())) {
					List<String> listContent = new ArrayList<String>();
					listContent.add(getText("nts.ams.fe.label.customer_information.purposeShortTermFlg"));
					model.setErrorMessage(getText("MSG_SC_013", listContent));
					addFieldError("errorMessage", getText("MSG_SC_013", listContent));
					return;
				}
			}
			
			// for check name pattern = /[\s~`!#$%\^&*+=@\-\[\]\\';,/{}|\\":<>\?]/g;
			//"[~`!#$%\\^&*+=@\\-\\[\\]\\';,/{}|:\\?\"<>]";
			String specialRegex = "[~`!#$%\\^&*+=@\\-\\[\\]\\';,/{}|:\\?\"<>]";
			Pattern pattern = Pattern.compile(specialRegex);
			if(customerInfo.getCorporationType() == 0){
				if(customerInfo.isChangeCustomerName()){
					String firstName = customerInfo.getFirstName();
					String lastName = customerInfo.getLastName();
					String firstNameKana = customerInfo.getFirstNameKana();
					String lastNameKana = customerInfo.getLastNameKana();
					
					validateCorpRefName(pattern, firstName, lastName,
							firstNameKana, lastNameKana);
				}
				if(customerInfo.isChangeAddress()){
					String zipCode = customerInfo.getZipcode();
					String preficture = customerInfo.getPrefecture();
					String city = customerInfo.getCity();
					String section = customerInfo.getSection();
					
					if(!validateAddress(zipCode, preficture, city, section)){
						return;
					}
				}
			}else{
				// For corporation
				if(customerInfo.isChangeCorpName()){
					String fullName = customerInfo.getCorpFullname();
					String fullNameKana = customerInfo.getCorpFullnameKana();
					if(StringUtil.isEmpty(fullName)) {
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.customer_information.name"));
						model.setErrorMessage(getText("MSG_SC_013", listContent));
						addFieldError("errorMessage", getText("MSG_SC_013", listContent));
						return;
					}
					if(StringUtil.isEmpty(fullNameKana)) {
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
						model.setErrorMessage(getText("MSG_SC_013", listContent));
						addFieldError("errorMessage", getText("MSG_SC_013", listContent));
						return;
					}
					if(pattern.matcher(fullName).find()) {
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.customer_information.name"));
						model.setErrorMessage(getText("MSG_SC_065", listContent));
						addFieldError("errorMessage", getText("MSG_SC_065", listContent));
						return;
					}
					if(pattern.matcher(fullNameKana).find()) {
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
						model.setErrorMessage(getText("MSG_SC_065", listContent));
						addFieldError("errorMessage", getText("MSG_SC_065", listContent));
						return;
					}
				}
				if(customerInfo.isChangeCorpAddress()){
					String zipCode = customerInfo.getZipcode();
					String preficture = customerInfo.getPrefecture();
					String city = customerInfo.getCity();
					String section = customerInfo.getSection();
					//String buildingName = customerInfo.getBuildingName();
					
					if(!validateAddress(zipCode, preficture, city, section)){
						return;
					}
				}
				
				if(customerInfo.isChangeCorpRepName()){
					String corpRepFirstname = customerInfo.getCorpRepFirstname();
					String corpRepLastname = customerInfo.getCorpRepLastname();
					String corpRepFirstnameKana = customerInfo.getCorpRepFirstnameKana();
					String corpRepLastnameKana = customerInfo.getCorpRepLastnameKana();
					
					if(!validateCorpRepName(pattern, corpRepFirstname, corpRepLastname, corpRepFirstnameKana, corpRepLastnameKana)){
						return;
					}
				}
				
				if(customerInfo.isChangeCorpOwnerName()){
					if(!validateCorpOwnerName(customerInfo, pattern)) {
						return;
					}
				}
				
				if(customerInfo.isChangeCorpRefName()){
					String corpPicLastname = customerInfo.getCorpPicLastname();
					String corpPicFirstname = customerInfo.getCorpPicFirstname();
					String corpPicLastnameKana = customerInfo.getCorpPicLastnameKana();
					String corpPicFirstnameKana = customerInfo.getCorpPicFirstnameKana();
					
					if(!validateCorpRefName(pattern, corpPicLastname, corpPicFirstname, corpPicLastnameKana, corpPicFirstnameKana)){
						return;
					}
				}
				if(customerInfo.isChangeCorpRefAddress()){
					String corpPicZipcode = customerInfo.getCorpPicZipcode();
					String preficture = customerInfo.getCorpPicPrefecture();
					String corpPicCity = customerInfo.getCorpPicCity();
					String corpPicSection = customerInfo.getCorpPicSection();
					String corpPicBuildingName = customerInfo.getCorpPicBuildingName();
					
					if(!validateAddress(corpPicZipcode, preficture,	corpPicCity, corpPicSection)){
						return;
					}
				}
				
				String corpPicTel = customerInfo.getCorpPicTel();
				if(StringUtil.isEmpty(corpPicTel)) {
					List<String> listContent = new ArrayList<String>();
					listContent.add(getText("nts.ams.fe.label.customer_information.corp_pic_tel"));
					model.setErrorMessage(getText("MSG_SC_013", listContent));
					addFieldError("errorMessage", getText("MSG_SC_013", listContent));
					return;
				}
			}
			
			//[NTS1.0-le.hong.ha]Apr 18, 2013A - End
			int ischangePass = model.getIsOpenPassword();
			if(ischangePass !=0 && ischangePass%2!=0) {
				if(!validatePassword(newPassword, comfirmedPassword, indentifyPassword, md5IndentifyPassword)) {
					return;
				}
			}
		}
	}
	private boolean validateBaseCustomerInfo(CustomerInfo customerInfo,	String tel1, String tel2, String additionalMail, String mailMain) {
		if(StringUtil.isEmpty(tel1)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.tel1"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(mailMain)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.mailMain"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if (!TrsUtil.isEmail(mailMain)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.mailMain"));				
			listContent.add("xxx@xxx.xxx");
			model.setErrorMessage(getText("global.message.NAB007", listContent));
			addFieldError("errorMessage", getText("global.message.NAB007", listContent));
			return false;
		}
		// HungPV Check loginId existed 
		if(profileManager.mailExisted(getCurrentCustomerId(), mailMain)){		
			model.setErrorMessage(getText("MSG_NAB033"));
			addFieldError("errorMessage", getText("MSG_NAB033"));				
			setValidateExitsEmail("exitsEmail");
			return false;
		}		
		
		if(!StringUtil.isEmpty(tel1) && !tel1.matches("^\\d+$")) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.tel1"));
			model.setErrorMessage(getText("nts.ams.fe.label.customer_information.phone.invalid", listContent));
			addFieldError("errorMessage", getText("nts.ams.fe.label.customer_information.phone.invalid", listContent));
			return false;
		}
		if(!StringUtil.isEmpty(tel2) && !tel2.matches("^\\d+$")) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.tel2"));
			model.setErrorMessage(getText("nts.ams.fe.label.customer_information.phone.invalid", listContent));
			addFieldError("errorMessage", getText("nts.ams.fe.label.customer_information.phone.invalid", listContent));
			return false;
		}
		
		if(!StringUtil.isEmpty(tel1) && (tel1.length() < 7 || tel1.length() > 11)) {
			model.setErrorMessage(getText("MSG_TRS_NAF_0038"));
			addFieldError("errorMessage", getText("MSG_TRS_NAF_0038"));
			return false;
		}
		if(!StringUtil.isEmpty(tel2) && (tel2.length() < 7 || tel2.length() > 11)) {
			model.setErrorMessage(getText("MSG_TRS_NAF_0038"));
			addFieldError("errorMessage", getText("MSG_TRS_NAF_0038"));
			return false;
		}
		
		if (!StringUtil.isEmpty(additionalMail) && !TrsUtil.isEmail(additionalMail)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.mailAddition"));
			listContent.add("xxx@xxx.xxx");
			model.setErrorMessage(getText("global.message.NAB007", listContent));
			addFieldError("errorMessage", getText("global.message.NAB007", listContent));
			return false;
		}
		if(!StringUtil.isEmpty(additionalMail) && profileManager.mailExisted(getCurrentCustomerId(), additionalMail)){				
			model.setErrorMessage(getText("MSG_NAB033"));
			addFieldError("errorMessage", getText("MSG_NAB033"));					
			setValidateExitsEmail("exitsEmail");
			return false;
		}
		
		if(!StringUtil.isEmpty(additionalMail) && mailMain.equalsIgnoreCase(additionalMail)){
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.mailMain"));
			listContent.add(getText("nts.ams.fe.label.customer_information.mailAddition"));
			model.setErrorMessage(getText("MSG_NAB033", listContent));
			addFieldError("errorMessage", getText("MSG_NAB033", listContent));	
			//setValidExitsAdditonalEmail("exitsadditionalMail");
			setValidateExitsEmail("exitsEmail");
			return false;
		}

		if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER){
			String mailMobile = customerInfo.getCorpPicMailMobile();
			if(!StringUtil.isEmpty(mailMobile) && mailMain.equalsIgnoreCase(mailMobile)){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.mailMain"));
				listContent.add(getText("nts.ams.fe.label.customer_information.mailMobile"));
				model.setErrorMessage(getText("MSG_NAB033", listContent));
				addFieldError("errorMessage", getText("MSG_NAB033", listContent));
				setValidateExitsEmail("exitsEmail");					
				return false;
			}
		}
		return true;
	}
	private boolean validatePassword(String newPassword, String comfirmedPassword,
			String indentifyPassword, String md5IndentifyPassword) {
		// check if loginpass is blank
		if(StringUtil.isEmpty(indentifyPassword)){				
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.current_password"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		CustomerInfo amsCustomerInfo = accountManager.getCustomerInfo(getCurrentCustomerId());
		if(amsCustomerInfo != null) {
			// if password is no change, set change password flag = false
			if(!amsCustomerInfo.getLoginPass().equals(md5IndentifyPassword)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.current_password"));
				addFieldError("errorMessage", getText("MSG_SC_047"));
				model.setErrorMessage(getText("MSG_SC_047",listContent));
				return false;
			} 
		}
		// check if new input password is blank
		if(StringUtil.isEmpty(newPassword)){				
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.new_password"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}else {
			if(!validateComplexPassword(newPassword)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.newpassword"));
				addFieldError("errorMessage", getText("MSG_SC_079"));
				model.setErrorMessage(getText("MSG_SC_079",listContent));				
				return false;
			}
			// check if confirmed password is blank
			if(StringUtil.isEmpty(comfirmedPassword)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.retype_password"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			if(!validateComplexPassword(comfirmedPassword)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.socialtrading.scfe018.useraccount.label.retype_password"));
				addFieldError("errorMessage", getText("MSG_SC_079"));
				model.setErrorMessage(getText("MSG_SC_079",listContent));				
				return false;
			}
			// check if the confirmed password is not equal to new password
			if(!newPassword.equals(comfirmedPassword)) {
				addFieldError("errorMessage", getText("MSG_SC_005"));
				model.setErrorMessage( getText("MSG_SC_005"));				
				return false;
			}
		}
		return true;
	}
	private boolean validateCorpRefName(Pattern pattern, String corpPicLastname,
			String corpPicFirstname, String corpPicLastnameKana,
			String corpPicFirstnameKana) {
		if(StringUtil.isEmpty(corpPicLastname)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(corpPicFirstname)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(corpPicLastnameKana)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname.kana"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(corpPicFirstnameKana)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(pattern.matcher(corpPicLastname).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname"));
			model.setErrorMessage(getText("MSG_SC_065", listContent));
			addFieldError("errorMessage", getText("MSG_SC_065", listContent));
			return false;
		}
		if(pattern.matcher(corpPicFirstname).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name"));
			model.setErrorMessage(getText("MSG_SC_065", listContent));
			addFieldError("errorMessage", getText("MSG_SC_065", listContent));
			return false;
		}
		if(pattern.matcher(corpPicLastnameKana).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname.kana"));
			model.setErrorMessage(getText("MSG_SC_065", listContent));
			addFieldError("errorMessage", getText("MSG_SC_065", listContent));
			return false;
		}
		if(pattern.matcher(corpPicFirstnameKana).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
			model.setErrorMessage(getText("MSG_SC_065", listContent));
			addFieldError("errorMessage", getText("MSG_SC_065", listContent));
			return false;
		}
		return true;
	}
	private boolean validateCorpOwnerName(CustomerInfo customerInfo, Pattern pattern) {
		if(customerInfo.getBeneficOwnerFlg() == 1){
			String beneficOwnerFullname = customerInfo.getBeneficOwnerFullname();
			String beneficOwnerFullnameKana = customerInfo.getBeneficOwnerFullnameKana();
			
			if(StringUtil.isEmpty(beneficOwnerFullname)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.name"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			
			if(StringUtil.isEmpty(beneficOwnerFullnameKana)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
		
			if(pattern.matcher(beneficOwnerFullname).find()) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.name"));
				model.setErrorMessage(getText("MSG_SC_065", listContent));
				addFieldError("errorMessage", getText("MSG_SC_065", listContent));
				return false;
			}
		
			if(pattern.matcher(beneficOwnerFullnameKana).find()) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
				model.setErrorMessage(getText("MSG_NAB053", listContent));
				addFieldError("errorMessage", getText("MSG_NAB053", listContent));
				return false;
			}
			
			String beneficOwnerZipcode = customerInfo.getBeneficOwnerZipcode();
			String beneficOwnerPrefecture = customerInfo.getBeneficOwnerPrefecture();
			String beneficOwnerCity = customerInfo.getBeneficOwnerCity();
			String beneficOwnerSection = customerInfo.getBeneficOwnerSection();
			String beneficOwnerTel = customerInfo.getBeneficOwnerTel();
			
			if(StringUtil.isEmpty(beneficOwnerZipcode)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.beneficOwner.postcode"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			if(StringUtil.isEmpty(beneficOwnerPrefecture) || beneficOwnerPrefecture.equals("-1")) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.beneficOwner.prefectural"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			if(StringUtil.isEmpty(beneficOwnerCity)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.beneficOwner.municipal"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			if(StringUtil.isEmpty(beneficOwnerSection)) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.beneficOwner.address"));
				model.setErrorMessage(getText("MSG_SC_013", listContent));
				addFieldError("errorMessage", getText("MSG_SC_013", listContent));
				return false;
			}
			if(!beneficOwnerZipcode.matches("^\\d+$")) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.customer_information.beneficOwner.postcode"));
				model.setErrorMessage(getText("nts.ams.fe.label.customer_information.postcode.invalid", listContent));
				addFieldError("errorMessage", getText("nts.ams.fe.label.customer_information.postcode.invalid", listContent));
				return false;
			}
			
			if(!StringUtil.isEmpty(beneficOwnerTel) && (beneficOwnerTel.length() < 7 || beneficOwnerTel.length() > 11)) {
				model.setErrorMessage(getText("MSG_TRS_NAF_0038"));
				addFieldError("errorMessage", getText("MSG_TRS_NAF_0038"));
				return false;
			}
			return true;
		}
		return true;
	}
	private boolean validateAddress(String zipCode, String preficture,
			String city, String section) {
		if(StringUtil.isEmpty(zipCode)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.postcode"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(preficture) || preficture.equals("-1")) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.prefectural"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(city)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.city"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(StringUtil.isEmpty(section)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.section"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		if(!zipCode.matches("^\\d+$")) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.postcode"));
			model.setErrorMessage(getText("nts.ams.fe.label.customer_information.postcode.invalid", listContent));
			addFieldError("errorMessage", getText("nts.ams.fe.label.customer_information.postcode.invalid", listContent));
			return false;
		}
		return true;
	}
	private boolean validateCorpRepName(Pattern pattern, String corpRepFirstname,
			String corpRepLastname, String corpRepFirstnameKana,
			String corpRepLastnameKana) {
		if(StringUtil.isEmpty(corpRepFirstname)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepLastname)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.ref"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepFirstnameKana)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname.kana"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		
		if(StringUtil.isEmpty(corpRepLastnameKana)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
			model.setErrorMessage(getText("MSG_SC_013", listContent));
			addFieldError("errorMessage", getText("MSG_SC_013", listContent));
			return false;
		}
		

		if(pattern.matcher(corpRepFirstname).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname"));
			model.setErrorMessage(getText("MSG_NAB053", listContent));
			addFieldError("errorMessage", getText("MSG_NAB053", listContent));
			return false;
		}

		if(pattern.matcher(corpRepLastname).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.ref"));
			model.setErrorMessage(getText("MSG_NAB053", listContent));
			addFieldError("errorMessage", getText("MSG_NAB053", listContent));
			return false;
		}
		
		if(pattern.matcher(corpRepFirstnameKana).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.surname.kana"));
			model.setErrorMessage(getText("MSG_NAB053", listContent));
			addFieldError("errorMessage", getText("MSG_NAB053", listContent));
			return false;
		}

		if(pattern.matcher(corpRepLastnameKana).find()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.name.kana"));
			model.setErrorMessage(getText("MSG_NAB053", listContent));
			addFieldError("errorMessage", getText("MSG_NAB053", listContent));
			return false;
		}
		return true;
	}

	/**
	 * @param accountManager the accountManager to set
	 */
	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	public void validateCreditDebit(CreditCardInfo creditCardInfo) {
		hasFieldErrors();
		String cvvNumber = creditCardInfo.getCcCvv();
		if (cvvNumber == null || StringUtils.isBlank(cvvNumber)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.creditCard.cvvNumber"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage",getText("MSG_NAF001",listContent));
			return;
		}
		
		String personalNumber = creditCardInfo.getCcDriverNo();
		if (personalNumber == null || StringUtils.isBlank(personalNumber)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.creditCard.personalNum"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage", getText("MSG_NAF001",listContent));
			return;
		}
		
		String address = creditCardInfo.getAddress();
		String city = creditCardInfo.getCity();
		String state = creditCardInfo.getState();
		String zipCode = creditCardInfo.getZipCode();
		Integer countryId = creditCardInfo.getCountryId();
		if (address == null || StringUtils.isBlank(address)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.customer_information.address"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage", getText("MSG_NAF001",listContent));
			return;
		}
		if (city == null || StringUtils.isBlank(city)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.deposit.credit.card.city"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage", getText("MSG_NAF001",listContent));
			return;
		}
		if (state == null || StringUtils.isBlank(state)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.deposit.credit.card.state"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage", getText("MSG_NAF001",listContent));
			return;
		}
		if (zipCode == null || StringUtils.isBlank(zipCode)) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.deposit.credit.card.zipcode"));
			model.setErrorMessage(getText("MSG_NAF001",listContent));
			addFieldError("errorMessage", getText("MSG_NAF001",listContent));
			return;
		}
		if(countryId == null || countryId.equals(new Integer(-1))) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.bank_information.creditCard.countryCode"));
			model.setErrorMessage( getText("MSG_NAF001", listContent));
			addFieldError("errorMessage", getText("MSG_NAF001", listContent));
			return;
		}
	}
	
	/**
	 * Get Credit Customer list for displaying on the table
	 * @param customerId String
	 * @return
	 */


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	// desc
	class MyComparator implements Comparator<String>{
        public int compare(String o1,String o2)
        {
        	if(o1 == null || o2 == null){
        		return 0;
        	}else{
        		return o2.compareTo(o1);
        	}
        }
    }
	public void getListInfo() {
		// List of payment method
		Map<String, String> mapPaymentMethodTem = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_INFORMATION_METHOD);
		List<String> sortedKeys=new ArrayList<String>(mapPaymentMethodTem.keySet());
		Collections.sort(sortedKeys, new MyComparator());
		Map<String, String> mapPaymentMethod = new LinkedHashMap<String, String>();
		for (String key : sortedKeys) {
			mapPaymentMethod.put(key, mapPaymentMethodTem.get(key));
		}
		
		//[NTS1.0-le.hong.ha]May 4, 2013A - Start 
		//Remove method which not no longer use
		//Update master data needn't use this method
//		if(mapPaymentMethod != null){
//			for (Iterator<Map.Entry<String, String>> it = mapPaymentMethod.entrySet().iterator(); it.hasNext();) {
//				Map.Entry<String, String> entry = it.next();
//				if (!entry.getKey().equals(ITrsConstants.PAYMENT_METHOD.BANK_TRANFER + "") && !entry.getKey().equals(ITrsConstants.PAYMENT_METHOD.VIRTUAL_ACCOUNT + "")) {
//					it.remove();
//				}
//			}
//		}
		
		Map<String, String> mapAccountType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.TRS_ACCOUNT_TYPE);
		model.setMapAccountType(mapAccountType);
		//[NTS1.0-le.hong.ha]May 4, 2013A - End
		
		model.setMapPaymentMethod(mapPaymentMethod);
		// List of type of card
		Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
		model.setMapCardType(mapCardType);
		// get List country
		model.setListCountry(profileManager.getListCountry());
	}
	public Integer getCardType() {
		return cardType;
	}
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}
	/**
	 * @return the customerCcId
	 */
	public Integer getCustomerCcId() {
		return customerCcId;
	}
	/**
	 * @param customerCcId the customerCcId to set
	 */
	public void setCustomerCcId(Integer customerCcId) {
		this.customerCcId = customerCcId;
	}
	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	/**
	 * @param inputStream the inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public List<CreditCardInfo> getCreditCardList(String customerId) {
		List<CreditCardInfo> listCreditCard = null;
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					String publicKey = frontUserOnline.getPublicKey();
					listCreditCard = getProfileManager().getCreditCardList(customerId,publicKey);
					if(listCreditCard != null && listCreditCard.size() >0 ) {
						for(int i=0;i<listCreditCard.size();i++) {
							listCreditCard.get(i).setCcTypeName(getText(listCreditCard.get(i).getCcTypeName()));
							listCreditCard.get(i).setCcNoDisp("*****" + (listCreditCard.get(i).getCcNoLastDigit()));
						}
					}	
				}
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listCreditCard;
	}
	
	/**
	 * Initiate data of controls　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 19, 2013
	 */
	private boolean init(){
		/*Select Box*/
		model.setListCountry(profileManager.getListCountry());
		Map<String, String> mapLeverage = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_LEVERAGE);
		ComparatorUtil comparatorUtil = new ComparatorUtil(mapLeverage);
		TreeMap<String, String> mapLeverageSorted = new TreeMap<String, String>(comparatorUtil);	
		mapLeverageSorted.putAll(mapLeverage);
		model.setMapLeverage(mapLeverageSorted);
		
		FrontUserDetails useretails = FrontUserOnlineContext.getFrontUserOnline();
		if(useretails == null) {
			return false;
		}
		FrontUserOnline userOnline = useretails.getFrontUserOnline();
		if (userOnline == null) {
			return false;
		}
		Map<String, String> mapLanguage = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + userOnline.getWlCode() + "_" + IConstants.SYS_PROPERTY.LANGUAGE);
		model.setMapLanguage(mapLanguage);
		
		Map<String, String> mapMt4Account = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_ACCOUNT);
		model.setMapMt4Account(mapMt4Account);
		
		//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
		Map<String, String> mapFinancilAssets = SystemPropertyConfig.getInstance().getMap(ITrsConstants.SYS_PROPERTY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.CURRENT_ASSET);
		model.setMapFinancilAssets(mapFinancilAssets);
		
		Map<String, String> mapPrefectures = this.getPrefecturesMap();
		model.setMapPrefecture(mapPrefectures);

		initYearMonthDay();
		//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
		return true;
	}

	/**
	 * Upload File Passport　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 19, 2013
	 */
	public String uploadFilePassport(){
		try {
			List<File> passportUploads = model.getUploadPassports();
			List<String> passportUploadsName = model.getUploadPassportsFileName();
			
			if(SUCCESS.equals(uploadFile(passportUploads, passportUploadsName, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.PASSPORT))){
				if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(updateDocStatus(IConstants.UPLOAD_DOCUMENT.DOC_TYPE.PASSPORT))){
					return ERROR; 
				}
			}else{
				return ERROR;
			}
			
			//index();
			return SUCCESS;
			
		}catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			return SUCCESS;
		}
	}
	
	/**
	 * Upload File Address　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 19, 2013
	 */
	public String uploadFileAddress(){
		List<File> addressUploads = model.getUploadAddresses();
		List<String> addressUploadsName = model.getUploadAddressesFileName();
		
		/*return uploadFile(addressUploads, addressUploadsName, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.ADDRESS);*/
		
		if(SUCCESS.equals(uploadFile(addressUploads, addressUploadsName, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.ADDRESS))){
			if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(updateDocStatus(IConstants.UPLOAD_DOCUMENT.DOC_TYPE.ADDRESS))){
				return ERROR; 
			}
		}else{
			return ERROR;
		}

		//index();
		return SUCCESS;
	}
	
	/**
	 * Upload File Sign　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 19, 2013
	 */
	public String uploadFileSign(){
		List<File> signatureUploads = model.getUploadSignatures();
		List<String> signatureUploadsName = model.getUploadSignaturesFileName();
		
		if(SUCCESS.equals(uploadFile(signatureUploads, signatureUploadsName, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.SIGNATURE))){
			if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(updateDocStatus(IConstants.UPLOAD_DOCUMENT.DOC_TYPE.SIGNATURE))){
				return ERROR; 
			}
		}else{
			return ERROR;
		}

		//index();
		return SUCCESS;
		
		/*return uploadFile(signatureUploads, signatureUploadsName, IConstants.UPLOAD_DOCUMENT.DOC_TYPE.SIGNATURE);*/
	}
	
	/**
	 * uploadFile　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 22, 2013
	 */
	private String uploadFile(List<File> filesUpload, List<String> filesUploadName, Integer docType){
		Integer result = new Integer(0);
		try{
			String rootPath = httpRequest.getSession().getServletContext().getRealPath("/");
			FrontUserDetails useretails = FrontUserOnlineContext.getFrontUserOnline();
			if(useretails == null) {
				return ERROR;
			}
			FrontUserOnline userOnline = useretails.getFrontUserOnline();
			if (userOnline == null) {
				return ERROR;
			}
			
			//init();
			
			Integer fxSubGroupId = new Integer(0);
			List<CustomerServicesInfo> listServices = userOnline.getListCustomerServiceInfo();
			for (CustomerServicesInfo info : listServices) {
				if (IConstants.SERVICES_TYPE.FX.equals(info.getServiceType())) {
					fxSubGroupId = info.getSubGroupId();
				}
			}
			
			/*Files upload*/
			if(filesUpload != null && filesUploadName != null){
				List<FileUploadInfo> filesUploadInfo = new ArrayList<FileUploadInfo>();
				for (File srcFile : filesUpload) {
					FileUploadInfo uploadInfo = new FileUploadInfo();
					uploadInfo.setCustomerId(userOnline.getUserId());
					uploadInfo.setFileName(filesUploadName.get(filesUpload.indexOf(srcFile)));
					uploadInfo.setDocFileType(FileLoaderUtil.getFileType(filesUploadName.get(filesUpload.indexOf(srcFile))));
					uploadInfo.setDocKind(IConstants.UPLOAD_DOCUMENT.DOC_KIND.REGISTER);
					uploadInfo.setDocType(docType);
					uploadInfo.setFile(srcFile);
					uploadInfo.setRootPath(rootPath);
					filesUploadInfo.add(uploadInfo);
				}
				result = getProfileManager().uploadFiles(filesUploadInfo, userOnline.getUserId(), userOnline.getWlCode(), fxSubGroupId);
			}
			
			/*EXTENSION NOT ALLOWED*/
			if(result.equals(IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.EXTENSION_NOT_ALLOWED)){
				//model.setErrorMessage(getText("MSG_NAF108"));
				setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_NAF108);
				return ERROR;
			}
			/*SIZE LIMIT EXCEEDED*/
			if(result.equals(IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.SIZE_LIMIT_EXCEEDED)){
				//model.setErrorMessage(getText("MSG_NAF109"));
				setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_NAF109);
				return ERROR;
			}
			/*UPLOAD FILE OR INSERT DATABASE FAIL*/
			if(result.equals(IConstants.UPLOAD_DOCUMENT.FAIL)){
				//model.setErrorMessage(getText("nts.ams.fe.label.customer_information.verify.messages.upload.fail"));
				setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR);
				return ERROR;
			}
			
			/*Update Customer Service*/
			if(!getProfileManager().updateCustomerServiceStatus(userOnline.getUserId())){
				//model.setErrorMessage(getText("nts.ams.fe.label.customer_information.verify.messages.upload.fail"));
				setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR);
				return ERROR;
			}
			
			/*Reupload files
			if(result.equals(IConstants.UPLOAD_DOCUMENT.REUPLOAD_SUCCESS)){
				model.setSuccessMessage(getText("nts.ams.fe.label.customer_information.verify.messages.reupload.success"));
				return SUCCESS;
			}
			if(result.equals(IConstants.UPLOAD_DOCUMENT.REUPLOAD_FAIL)){
				model.setErrorMessage(getText("nts.ams.fe.label.customer_information.verify.messages.reupload.fail"));
				return ERROR;
			}*/
			
			//model.setSuccessMessage(getText("nts.ams.fe.label.customer_information.verify.messages.upload.success"));
			setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_SUCCESS);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	/**
	 * updateDocStatus　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 24, 2013
	 */
	private Integer updateDocStatus(Integer docType){
		Integer result = new Integer(0);
		FrontUserDetails useretails = FrontUserOnlineContext.getFrontUserOnline();
		if(useretails == null) {
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		FrontUserOnline userOnline = useretails.getFrontUserOnline();
		if (userOnline == null) {
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		
		if(getProfileManager().updateCustomerDocStatus(userOnline.getUserId(), docType)){
			result = IConstants.UPLOAD_DOCUMENT.SUCCESS;
		}else{
			result = IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		
		return result;
	}
	
	public void validate(){
		if(hasErrors()){
			setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_NAF109);
		}
	}
	
	/**
	 * Download file from server
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	public String download(){
		String fileName = httpRequest.getParameter("docUrl");
		try {
			// [start] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
			fileName = decryptedString(fileName);
			// [end] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971

			FileInputStream fileInputStream = new FileInputStream(new File(fileName));
			DownloadUtil.download(fileName, fileInputStream);
		    return NONE;
	    } catch(Exception ex) {
	    	log.error(ex.getMessage(), ex);
	    	setMsgCode(IConstants.VERIFY_ACCOUNT_MSG_CODE.MSG_DOWNLOAD_FILE_NOT_FOUND);
	    	StringBuilder url = new StringBuilder(getRawUrl());
	    	url.append("?result=").append(IConstants.VERIFY_ACCOUNT_MSG_CODE.MSG_DOWNLOAD_FILE_NOT_FOUND);
	    	
	    	Integer method = MathUtil.parseInteger(type);
			
			if(method != null) {
				if (IConstants.EWALLET_TYPE.CREDIT_DEBIT.equals(method)) {
					url.append("&type=").append(IConstants.EWALLET_TYPE.CREDIT_DEBIT).append("&customerCcId=").append(customerCcId);
				} else if(IConstants.EWALLET_TYPE.LIBERTY.equals(method)) {
					url.append("&type=").append(IConstants.EWALLET_TYPE.LIBERTY).append("&paymentId=").append(paymentId);
				}
			}

			setRawUrl(url.toString());
	    }
        return SUCCESS;
	}
	
	/**
	 * Download Profile docs　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	public String downloadVerifyProfileDoc(){
		setRawUrl(IConstants.FrontEndActions.PROFILE_INDEX);
		return download();
	}
	
	/**
	 * Download Payment docs　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	public String downloadVerifyPaymentDoc(){
		setRawUrl(IConstants.FrontEndActions.PROFILE_UPDATE_PAYMENT);
		return download();
	}
	
	/**
	 * upload Avatar
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 1, 2013
	 */
	public String uploadAvatar(){
		try {
			FileUploadInfo fileUploadInfo = new FileUploadInfo();
			String rootPath = httpRequest.getSession().getServletContext().getRealPath("/");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			String wlCode = frontUserOnline.getWlCode();	
			
			if(model.getUploadedAvatar() == null){
				setMsgCode(ITrsConstants.UPLOAD_DOCUMENT.MSG_CODE.FILE_REQUIRED);
				return ERROR;
			}
			
			fileUploadInfo.setFile(model.getUploadedAvatar());
			fileUploadInfo.setCustomerId(customerId);
			fileUploadInfo.setFileName(model.getUploadedAvatarFileName());
			fileUploadInfo.setRootPath(rootPath);
			
			Integer result = profileManager.uploadAvatar(fileUploadInfo,wlCode);
			if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(result)){
				if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_008);
				} else if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_009);
				} else {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR);
				}
				return ERROR;
			}
			setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_SUCCESS);
            FrontEndContext.getInstance().setAvatarTimestamp(customerId);
			//index();
			return SUCCESS;
			
		}catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			return SUCCESS;
		}
	}
	
	public String uploadCropAvatar(){
		try {
			log.info("[start] uploadCropAvatar for file :" + model.getUploadedAvatarFileName());
			FileUploadInfo fileUploadInfo = new FileUploadInfo();
			String rootPath = httpRequest.getSession().getServletContext().getRealPath("/");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			String wlCode = frontUserOnline.getWlCode();	
			
			fileUploadInfo.setFile(model.getUploadedAvatar());
			fileUploadInfo.setCustomerId(customerId);
			fileUploadInfo.setFileTempName(model.getUploadedAvatarFileName());
			fileUploadInfo.setFileName(model.getUploadedAvatarFileName());
			fileUploadInfo.setRootPath(rootPath);
//			fileUploadInfo.setX(model.getX());
//			fileUploadInfo.setY(model.getY());
//			fileUploadInfo.setW(model.getW());
//			fileUploadInfo.setH(model.getH());
			
			Integer result = profileManager.uploadCropAvatar(fileUploadInfo,wlCode);
			if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(result)){
				if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_008);
				} else if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_009);
				} else {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR);
				}
				return ERROR;
			}
			setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_SUCCESS);
            FrontEndContext.getInstance().setAvatarTimestamp(customerId);
            FrontEndContext.getInstance().setAvatarDimension(customerId, fileUploadInfo.getW(), fileUploadInfo.getH());
			//index();            
            log.info("[end] uploadCropAvatar for file :" + model.getUploadedAvatarFileName());
			return "crop";
			
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return SUCCESS;
		}
	}
	
	public String uploadCropAvatarSubmit(){
		try {
			log.info("[start] uploadCropAvatarSubmit for file :" + model.getUploadedAvatarFileName() + " x:" + model.getX() + " y: " + model.getY() + " w:" + model.getW() + " h:" + model.getH());
			FileUploadInfo fileUploadInfo = new FileUploadInfo();
			String rootPath = httpRequest.getSession().getServletContext().getRealPath("/");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			String wlCode = frontUserOnline.getWlCode();	
			
			fileUploadInfo.setFile(model.getUploadedAvatar());
			fileUploadInfo.setCustomerId(customerId);
			fileUploadInfo.setFileTempName(model.getUploadedAvatarFileName());
			fileUploadInfo.setFileName(model.getUploadedAvatarFileName());
			fileUploadInfo.setRootPath(rootPath);
			fileUploadInfo.setX(model.getX());
			fileUploadInfo.setY(model.getY());
			fileUploadInfo.setW(model.getW());
			fileUploadInfo.setH(model.getH());
			
			Integer result = profileManager.uploadCropAvatarSubmit(fileUploadInfo,wlCode);
			if(!IConstants.UPLOAD_DOCUMENT.SUCCESS.equals(result)){
				if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_008);
				} else if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED.equals(result)) {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_SC_009);
				} else {
					setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_ERROR);
				}
				return ERROR;
			}
			setMsgCode(IConstants.UPLOAD_DOCUMENT.MSG_CODE.MSG_UPLOAD_SUCCESS);
            FrontEndContext.getInstance().setAvatarTimestamp(customerId);
			//index();       
            log.info("[end] uploadCropAvatarSubmit for file :" + model.getUploadedAvatarFileName());
			return SUCCESS;
			
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return SUCCESS;
		}
	}
	
	
	/**
	 * update Basic Info
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 1, 2013
	 */
	public String updateBasicInfo(){
		try{
			CustomerInfo customerInfo = model.getCustomerInfo();
			CustomerScInfo customerScInfo = model.getCustomerScInfo();
			
			// Escape HTML tag
			String description1 = customerScInfo.getDescription();
//			String desc= "";
			if(description1 != null){
				if (description1.length() > 500) {
					description1 = description1.substring(0, 500);
				}
//				desc = new String(description1);
//				description1 = URLEncoder.encode(description1,"UTF-8");
			}
	
			customerScInfo.setDescription(description1);
			if(!validateUpdateBasicInfo()) {
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_NAB053_BIRHDAY);
				return SUCCESS;
			}
			if(profileManager.updateBasicInfoOfScCustomer(customerInfo, customerScInfo)){
				customerInfo = getProfileManager().getCustomerInfo(getCurrentCustomerId());
				customerInfo.setDescription(description1);
				SocialMemcached.getInstance().saveCustomerInfo(customerInfo);
				setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_SUCCESS);
				return SUCCESS;
		}else{
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_FAILURE);
			return SUCCESS;
		}
		} catch (Exception e) {
			setMsgCode(IConstants.BANK_INFO_MSGCODE.MSG_FAILURE);
			return SUCCESS;
		}
	}
	private boolean validateUpdateBasicInfo() {
		boolean result = true;
		CustomerInfo customerInfo = model.getCustomerInfo();
		CustomerScInfo customerScInfo = model.getCustomerScInfo();
		String birthday = customerInfo.getYear() + "/" + customerInfo.getMonth() + "/" + customerInfo.getDay();
		Date dBirthday = DateUtil.toDate(birthday, IConstants.DATE_TIME_FORMAT.DATE_DB);
		if(dBirthday == null) {
			return false;
		}
		return result;
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
//		if(!password.matches("[a-zA-Z]*[0-9]*")) {
//			return false;
//		}
		return true;
	}
    
    public String searchZipCode(){
    	String zipCode = model.getZipCode();
    	if(zipCode != null){
    		AmsSysZipcode amsSysZipcode = profileManager.getAddressByZipCode(zipCode);
        	model.setAmsSysZipcode(amsSysZipcode);
    	}
    	return SUCCESS;
    }
    
    //Halh
    public String displaySearchBankPopup(){
		System.out.println("search bank");
		return SUCCESS;
	}

	public String  displaySearchBranchPopup(){
		try {
			HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			String bankCode = request.getParameter("bankCode");
			BankTransferInfo bankCondition = model.getNewBankTransferInfo();
			if(bankCondition == null){
				bankCondition= new BankTransferInfo();
			}
			bankCondition.setBankCode(bankCode);
			model.setBankCondition(bankCondition);
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
	}

	public String searchBankFromPopup(){
		try {
			HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			//request.setCharacterEncoding("Shift-JIS");
			//String bankCode = request.getParameter("bankCode");
			String bankName = request.getParameter("bankName");
			String bankNameFullSize = request.getParameter("bankNameFullSize");
			String bankNameHalfSize = request.getParameter("bankNameHalfSize");
			
			if(bankName != null){
				bankName = java.net.URLDecoder.decode (bankName, "utf-8");
			}
			if(bankNameFullSize != null){
				bankNameFullSize = java.net.URLDecoder.decode (bankNameFullSize, "utf-8");
			}
			if(bankNameHalfSize != null){
				bankNameHalfSize = java.net.URLDecoder.decode (bankNameHalfSize, "utf-8");
			} 
			
			PagingInfo paging = model.getPagingInfo();
			if(paging==null){
				paging= new PagingInfo();
			}
			// Set paging default is 10 respective with combobox
			if(paging.getOffset() == 100){
				paging.setOffset(10);
			}
			//paging.setOffset(ITrsConstants.PAGING.POP_UP_PAGING_OFFSET);
			BankTransferInfo condition = model.getBankCondition();
			if(condition != null && condition.getBankName() != null){
				bankName = condition.getBankName();
				bankNameFullSize = condition.getBankNameFullSize();
				bankNameHalfSize = condition.getBankNameHalfSize();
			}else{
				condition = new BankTransferInfo();
				condition.setBankName(bankName);
				model.setBankCondition(condition);
			}
			SearchResult<AmsSysBank> listBank = profileManager.findListBank(bankName, bankNameFullSize, bankNameHalfSize, paging);
			model.setListBank(listBank);
			model.setPagingInfo(listBank.getPagingInfo());
			if(listBank == null || listBank.size() <= 0){
				model.setErrorMessage(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.MSG_NAB010"));
			}
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
	}

	public String  searchBranchFromPopup(){
		try {
			HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			String bankCode = request.getParameter("bankCode");
			String branchName = request.getParameter("branchName");
			String branchNameFullSize = request.getParameter("branchNameFullSize");
			String branchNameHalfSize = request.getParameter("branchNameHalfSize");
			
			if(bankCode != null){
				bankCode = java.net.URLDecoder.decode (bankCode, "utf-8");
			}
			if(branchName != null){
				branchName = java.net.URLDecoder.decode (branchName, "utf-8");
			}
			if(branchNameFullSize != null){
				branchNameFullSize = java.net.URLDecoder.decode (branchNameFullSize, "utf-8");
			}
			if(branchNameHalfSize != null){
				branchNameHalfSize = java.net.URLDecoder.decode (branchNameHalfSize, "utf-8");
			}
			
			PagingInfo paging = model.getPagingInfo();
			if(paging==null){
				paging= new PagingInfo();
			}
			// Set paging default is 10 respective with combobox
			if(paging.getOffset() == 100){
				paging.setOffset(10);
			}
			//paging.setOffset(ITrsConstants.PAGING.POP_UP_PAGING_OFFSET);
			BankTransferInfo condition = model.getBankCondition();
			if(condition != null){
				bankCode = condition.getBankCode() != null ? condition.getBankCode() : "";
				branchName = condition.getBranchName() != null ? condition.getBranchName() : "";
				branchNameFullSize = condition.getBranchNameFullSize() != null ? condition.getBranchNameFullSize() : "";
				branchNameHalfSize = condition.getBranchNameHalfSize() != null ? condition.getBranchNameHalfSize() : "";
			}else{
				condition = new BankTransferInfo();
				condition.setBankCode(bankCode);
				condition.setBranchName(branchName);
				model.setBankCondition(condition);
			}
			
			SearchResult<AmsSysBankBranch> listBankBranch = profileManager.findListBankBranch(bankCode, branchName, branchNameFullSize, branchNameHalfSize, paging);
			model.setListBankBranch(listBankBranch);
			model.setPagingInfo(listBankBranch.getPagingInfo());
			if(listBankBranch == null || listBankBranch.size() <= 0){
				model.setErrorMessage(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.MSG_NAB010"));
			}
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
	}
    
    public String displaySearchZipcodePopup(){
//		HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
//		String zipcode = request.getParameter("zipcode");
//		CustomerSearchCondition condition = model.getCondition();
//		if(condition==null){
//			condition= new CustomerSearchCondition();
//		}
//		condition.setZipcode(zipcode);
//		model.setCondition(condition);
//		if(StringUtils.isBlank(zipcode)){
//			model.setErrorMessage(getText("global.message.condition.error"));
//			model.addErrorContent(getText("BFE_MSG033"));
//			return SUCCESS;
//		}
//		BoSysZipcode postCode = manager.findZipCodeById(zipcode);
//		if(postCode==null){
//			model.setErrorMessage(getText("global.message.condition.error"));
//			model.addErrorContent(getText("BFE_MSG034"));
//			return SUCCESS;
//		}
//		HashMap<String, String> mapZipcode= new HashMap<String, String>();
//		mapZipcode.put(postCode.getZipcode(),postCode.getSection()+postCode.getWard()+ postCode.getPrefecture());
//		model.setMapZipcode(mapZipcode);
//		model.setPostCode(postCode);
		return SUCCESS;
	}
    
    public String loadZipcode(){
//		String zipcode= model.getCondition().getZipcode();
//		if(StringUtils.isBlank(zipcode)){
//			model.setErrorMessage(getText("global.message.condition.error"));
//			model.addErrorContent(getText("BFE_MSG033"));
//			return SUCCESS;
//		}
//		BoSysZipcode postCode = manager.findZipCodeById(zipcode);
//		if(postCode==null){
//			model.setErrorMessage(getText("global.message.condition.error"));
//			model.addErrorContent(getText("BFE_MSG034"));
//			return SUCCESS;
//		}
//		HashMap<String, String> mapZipcode= new HashMap<String, String>();
//		mapZipcode.put(postCode.getZipcode(),postCode.getSection()+postCode.getWard()+ postCode.getPrefecture());
//		model.setMapZipcode(mapZipcode);
//		model.setPostCode(postCode);
		return SUCCESS;
	}
    
    /**
	 * Customer report history
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jul 30, 2013
	 */
    public String customerReportHistory(){
    	try {
			initcusReportHistory();
    		return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
    }
	private void initcusReportHistory() {
		ReportHistorySearchCondition condition = model.getCustReportSearchCondition();
		FrontUserOnline frontUserOnline = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails!=null){
			frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return;
			}
		}
		if(condition == null){
			condition = new ReportHistorySearchCondition();
			
			String frontDate = profileManager.getCurrentBusinessDay();
			Date currentBussDate = DateUtil.toDate(frontDate, DateUtil.PATTERN_YYMMDD_BLANK);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentBussDate);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date previousCurrDate = cal.getTime();
			String fromDate = DateUtil.toString(previousCurrDate, DateUtil.PATTERN_YYMMDD);
			String toDate = DateUtil.toString(currentBussDate, DateUtil.PATTERN_YYMMDD);
			condition.setReportDateFrom(fromDate);
			condition.setReportDateTo(toDate);
			model.setCustReportSearchCondition(condition);
		}
		Map<String, String> mapReportType = new LinkedHashMap<String, String>();
		mapReportType.put("ALL", getText("nts.ams.fe.label.customer_report_history.all"));
		mapReportType.put("D", getText("nts.ams.fe.label.customer_report_history.daily"));
		mapReportType.put("M", getText("nts.ams.fe.label.customer_report_history.monthly"));
		mapReportType.put("Y", getText("nts.ams.fe.label.customer_report_history.yearly"));
		model.setMapReportType(mapReportType);
		
		//[TRS-BO tan.pham.duy]
		Map<String,String> mapType = new LinkedHashMap<String, String>();
		mapType.put("ALL", getText("nts.ams.fe.label.customer_report_history.all"));
		mapType.put(ITrsConstants.REPORT_HISTORY.SERVICE_TYPE_STR.SERVICE_TYPE_FX, getText("nts.ams.fe.label.customer_report_history.type.fx"));
		if(FrontEndContext.getInstance().getOpenBOAccountFlg()!=null && FrontEndContext.getInstance().getOpenBOAccountFlg() == 0){
			Integer serviceBo = frontUserOnline.getServiceBo();
			if(serviceBo > 1){
				mapType.put(ITrsConstants.REPORT_HISTORY.SERVICE_TYPE_STR.SERVICE_TYPE_BO, getText("nts.ams.fe.label.customer_report_history.type.bo"));
			}
		}else{
			mapType.put(ITrsConstants.REPORT_HISTORY.SERVICE_TYPE_STR.SERVICE_TYPE_BO, getText("nts.ams.fe.label.customer_report_history.type.bo"));
		}
		model.setMapType(mapType);
		Map<Integer,String> mapRadio = new LinkedHashMap<Integer, String>();
		mapRadio.put(ITrsConstants.REPORT_HISTORY.RADIO.YES, getText("nts.ams.fe.label.customer_report_history.radio.yes"));
		mapRadio.put(ITrsConstants.REPORT_HISTORY.RADIO.NO, getText("nts.ams.fe.label.customer_report_history.radio.no"));
		model.setMapRadio(mapRadio);
	}
	
	/**
	 * Search customer report history
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jul 30, 2013
	 */
	public String searchCustReportHistory(){
		try {
			initcusReportHistory();
			ReportHistorySearchCondition condition = model.getCustReportSearchCondition();
			
			// formate date before search
			ReportHistorySearchCondition newCondition = new ReportHistorySearchCondition();
			
			// avoid place holder of IE version <= 9
			if(condition.getReportDateFrom() != null && condition.getReportDateFrom().equalsIgnoreCase(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.fromDate"))){
				condition.setReportDateFrom("");
			}
			if(condition.getReportDateTo() != null && condition.getReportDateTo().equalsIgnoreCase(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.toDate"))){
				condition.setReportDateTo("");
			}
			
			// Validate
			Date fromDate = null;
			Date toDate = null;
			if(!StringUtil.isEmpty(condition.getReportDateFrom())){
				try {
					//fromDate = DateUtil.toDate(condition.getReportDateFrom(), DateUtil.PATTERN_YYMMDD);
					DateFormat formatter = new SimpleDateFormat(DateUtil.PATTERN_YYMMDD);
					formatter.setLenient(false);
					fromDate = formatter.parse(condition.getReportDateFrom());
				} catch (Exception e) {
					List<String> listContent = new ArrayList<String>();
					listContent.add(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.fromDate"));
					model.setErrorMessage(getText("MSG_NAB007", listContent));
					return ERROR;
				}
			}
			
			if(!StringUtil.isEmpty(condition.getReportDateTo())){
				try {
					//toDate = DateUtil.toDate(condition.getReportDateTo(), DateUtil.PATTERN_YYMMDD);
					DateFormat formatter = new SimpleDateFormat(DateUtil.PATTERN_YYMMDD);
					formatter.setLenient(false);
					toDate = formatter.parse(condition.getReportDateTo());
				} catch (Exception e) {
					List<String> listContent = new ArrayList<String>();
					listContent.add(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.toDate"));
					model.setErrorMessage(getText("MSG_NAB007", listContent));
					return ERROR;
				}
			}
			
			if(fromDate != null && toDate != null && (fromDate.getTime() > toDate.getTime())){
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.fromDate"));
				listContent.add(getText("nts.ams.fe.label.bank_information.searchCustReportHistory.toDate"));
				model.setErrorMessage(getText("MSG_NAB012", listContent));
				return ERROR;
			}
			
			newCondition.setReportDateFrom(DateUtil.toString(fromDate, DateUtil.PATTERN_YYMMDD_BLANK));
			newCondition.setReportDateTo(DateUtil.toString(toDate, DateUtil.PATTERN_YYMMDD_BLANK));
			newCondition.setReportType(condition.getReportType());
			newCondition.setServiceType(condition.getServiceType());
			newCondition.setUnread(condition.getUnread());
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			newCondition.setCustomerId(frontUserOnline.getUserId());
			newCondition.setWlCode(frontUserOnline.getWlCode());
			PagingInfo paging = model.getPagingInfo();
			if(paging==null){
				paging= new PagingInfo();
			}
			// Set paging default is 10 respective with combobox
			if(paging.getOffset() == 100){
				paging.setOffset(10);
			}

			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			SearchResult<CustReportHistoryInfo> listCustReportHistoryInfo = profileManager.searchCustReportHistory(newCondition, paging, privateKey, frontUserOnline.getPublicKey());
			if(listCustReportHistoryInfo == null || listCustReportHistoryInfo.size() <= 0){
				model.setErrorMessage(getText("MSG_NAB010"));
			}
			model.setListCustReportHistoryInfo(listCustReportHistoryInfo);
			model.setPagingInfo(listCustReportHistoryInfo.getPagingInfo());
    		return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
	}
	
	/**
	 * download customer report history
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jul 30, 2013
	 */
	public String downloadCustReport(){
		try {
			initcusReportHistory();
			HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			String link = request.getParameter("link");
			if(link != null){
				// [start] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
				link = decryptedString(link);
				// [end] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
			}
			String from = request.getParameter("from");
			if(from != null){
				from = java.net.URLDecoder.decode (from, "utf-8");
			}
			String to = request.getParameter("to");
			if(to != null){
				to = java.net.URLDecoder.decode (to, "utf-8");
			}
			String type = request.getParameter("type");
			if(type != null){
				type = java.net.URLDecoder.decode (type, "utf-8");
			}
			String reportId = request.getParameter("reportId");
			if(reportId != null){
				reportId = java.net.URLDecoder.decode (reportId, "utf-8");
			}
			
			ReportHistorySearchCondition oldCondition = model.getCustReportSearchCondition();
			oldCondition.setReportDateFrom(from);
			oldCondition.setReportDateTo(to);
			oldCondition.setReportType(type);
			model.setCustReportSearchCondition(oldCondition);
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails == null) {
				return ERROR;	
			}
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline == null) {
				return ERROR;
			}
			String customerId = frontUserOnline.getUserId();
			
			// Check customerId exitst on link
			if(link.indexOf(customerId) == -1){
				ReportHistorySearchCondition newCondition = new ReportHistorySearchCondition();
				if(from != null){
					from = from.replaceAll("/", "");
				}
				if(to != null){
					to = to.replaceAll("/", "");
				}
				newCondition.setReportDateFrom(from);
				newCondition.setReportDateTo(to);
				newCondition.setReportType(type);
				newCondition.setCustomerId(frontUserOnline.getUserId());
				newCondition.setWlCode(frontUserOnline.getWlCode());
				PagingInfo paging = model.getPagingInfo();
				if(paging==null){
					paging= new PagingInfo();
				}
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				SearchResult<CustReportHistoryInfo> listCustReportHistoryInfo = profileManager.searchCustReportHistory(newCondition, paging, privateKey, frontUserOnline.getPublicKey());
				model.setListCustReportHistoryInfo(listCustReportHistoryInfo);
				return ERROR;
			}
			profileManager.updateWhiteLabelReport(reportId);
			link += ITrsConstants.AFS.FILE_TYPE.PDF;
			File reportFile = new File(link);
			InputStream reportFileInput = new FileInputStream(reportFile);
			model.setDownloadFile(reportFileInput);
			model.setDownloadFileName(reportFile.getName());
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ERROR;
		}
	}
	
	private Map<String, String> getPrefecturesMap() {
		// Get prefectures map
		Map<String, String> prefecturesMap = new LinkedHashMap<String, String>();
		Map<String, String> mapPrefs = SystemPropertyConfig.getInstance().getMap(ITrsConstants.SYS_PROPERTY.SYS_PROPERTY 
				+ ITrsConstants.SYS_PROPERTY.TRS_PREFECTURES);
		
		String selectPrefecture = mapPrefs.get("10");
		for (String pref : mapPrefs.values()) {
			if (selectPrefecture.equals(pref)) {
				prefecturesMap.put("-1", pref);
			} else {
				prefecturesMap.put(pref.trim(), pref);
			}
		}
		
		if (prefecturesMap.isEmpty()) {
			log.warn("The prefectures map is empty!");
		}
		
		return prefecturesMap;
	}
	
	public String getValidateExitsEmail() {
		return validateExitsEmail;
	}
	public void setValidateExitsEmail(String validateExitsEmail) {
		this.validateExitsEmail = validateExitsEmail;
	}
	
	/**
	 * decrypt string 
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Dev-DuyenNT
	 * @CrDate Apr 1, 2014
	 */
	private String decryptedString (String encrytedStr) throws CryptographyException {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String publicKey = frontUserOnline.getPublicKey();
		String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
		return Cryptography.decrypt(encrytedStr, privateKey, publicKey);
	}
}
