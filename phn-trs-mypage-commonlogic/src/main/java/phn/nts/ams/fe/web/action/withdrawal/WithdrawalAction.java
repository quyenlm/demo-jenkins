package phn.nts.ams.fe.web.action.withdrawal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.WhiteLabelConfigInfo;
import phn.nts.ams.fe.domain.WithdrawalInfo;
import phn.nts.ams.fe.domain.WithdrawalRuleInfo;
import phn.nts.ams.fe.model.WithdrawalModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

public class WithdrawalAction extends BaseSocialAction<WithdrawalModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2817409452184908544L;
	private WithdrawalModel withdrawalModel = new WithdrawalModel();	
	private IWithdrawalManager withdrawalManager;
	private static Logit log = Logit.getInstance(WithdrawalAction.class);
	private List<String> messageList = new LinkedList<String>();
	private String msgCode;
	private String result;
	private IAccountManager accountManager;
	private IIBManager ibManager;
//	private IBalanceManager balanceManager;
	private IDepositManager depositManager;
	private IExchangerManager exchangerManager;
	
	
	
	@Override
	public WithdrawalModel getModel() {
		return withdrawalModel;
	}
	
	public String index(){
		try{
			if(result != null) {
				getMsgCode(result);
			}
			loadWithdrawAccountInfos();
			loadMinMaxWithdrawalAmount();
			loadListOfCountries();
			getWithdrawalHistory();
			return SUCCESS;
		}catch (Exception e) {
			withdrawalModel.setErrorMessage("An error occured");
			log.error(e.getMessage(), e);
			return INPUT;
		}
	}
	
	public String withdrawalMethodShow(){
		try {
			loadWithdrawAccountInfos();
			loadMinMaxWithdrawalAmount();
			loadAvailablePaymentInfo();
			loadListOfCountries();
			getWithdrawalHistory();
			
			if(!validateInputAmountAndWithdrawPermission()){
				return INPUT;
			}
			
			withdrawalManager.loadWithdrawalRuleInfo(withdrawalModel);
			
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			withdrawalModel.setErrorMessage("An error occured when performing action to show withdrawal methods");
			return INPUT;
		}
	}
	
	public String withdrawalConfirmed(){
		try{
			loadWithdrawAccountInfos();
			loadMinMaxWithdrawalAmount();
			loadAvailablePaymentInfo();
			loadListOfCountries();
			getWithdrawalHistory();
			
			if(!validateInputAmountAndWithdrawPermission()){
				return INPUT;
			}
			
			withdrawalManager.loadWithdrawalRuleInfo(withdrawalModel);
			//hunglv_add
			if(!checkRdBankWire()){
				return INPUT;
			}
			//end
			if(withdrawalModel.getWithdrawalRuleInfo().isShowTransactionWithOtherPaymentMethod()){
				readPaymentInformation();
			}
			
			if(!validateWithdrawAmountAgainstFeeAndBalance()){
				return INPUT;
			}
			
			return SUCCESS;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			withdrawalModel.setErrorMessage("An error occured");
			return ERROR;
		}
	}
	
	public String withdrawalSubmit(){
		try {
			log.info("[Start] withdrawal submit");
			loadWithdrawAccountInfos();
			loadMinMaxWithdrawalAmount();
			loadListOfCountries();
			getWithdrawalHistory();
			
			if(!validateInputAmountAndWithdrawPermission()){
				return INPUT;
			}
			
			withdrawalManager.loadWithdrawalRuleInfo(withdrawalModel);
			if(withdrawalModel.getWithdrawalRuleInfo().isShowTransactionWithOtherPaymentMethod()){
				readPaymentInformation();
			}	
			
			if(!validateWithdrawAmountAgainstFeeAndBalance()){
				return INPUT;
			}
			
			withdrawalManager.withdrawMoney(withdrawalModel);
			
			setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
			log.info("[End] withdrawal submit");

			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ERROR;
		}
	}
	
	private boolean validateWithdrawAmountAgainstFeeAndBalance(){
		String currencyCode = null;
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		currencyCode = frontUserOnline.getCurrencyCode();
		
		if(withdrawalRuleInfo.isShowTransactionWithCc()){
			BigDecimal withdrawAmount = MathUtil.parseBigDecimal(withdrawalModel.getAmount());
			BigDecimal withdrawalCcFee = withdrawalRuleInfo.getWithdrawFeeCc();
			
			if(withdrawAmount.compareTo(withdrawalCcFee) <= 0){
				List<String> listContent = new ArrayList<String>();
				listContent.add(StringUtil.toString(withdrawalCcFee));
				listContent.add(currencyCode);
				withdrawalModel.setErrorMessage(getText("MSG_NAB101", listContent));
				addFieldError("errorMessage", getText("MSG_NAB101", listContent));
				log.info("input amount "+ withdrawAmount + "is smaller than withdrawal fee" + withdrawalCcFee);
				return false;
			}
		}
		
		if(withdrawalRuleInfo.isShowTransactionWithOtherPaymentMethod()){
			BigDecimal withdrawAmount2 = withdrawalRuleInfo.getWithdrawAmount2();
			BigDecimal withdrawalFee = withdrawalRuleInfo.getWithdrawFeeOtherMethod();
			
			if(withdrawAmount2.compareTo(MathUtil.parseBigDecimal(withdrawalFee)) <= 0){
				List<String> listContent = new ArrayList<String>();
				listContent.add(StringUtil.toString(withdrawalFee));
				listContent.add(currencyCode);
				withdrawalModel.setErrorMessage(getText("MSG_NAB101", listContent));
				addFieldError("errorMessage", getText("MSG_NAB101", listContent));
				log.info("input amount "+ withdrawAmount2 + "is smaller than withdrawal fee" + withdrawalFee);
				return false;
			}
		
			String rdNeteller = withdrawalModel.getRdNeteller();
			String rdPayza = withdrawalModel.getRdPayza();
			String rdBankwire = withdrawalModel.getRdBankwire();
			String rdExchanger = withdrawalModel.getRdExchanger();
			String rdLiberty = withdrawalModel.getRdLiberty();
			Integer withdrawMethod = withdrawalRuleInfo.getPaymentMethod();
			
			if(IConstants.PAYMENT_METHOD.NETELLER == withdrawMethod) {
				if(rdNeteller == null) {
					//  do late
				} else {
					if(IConstants.DEPOSIT_CHOOSE_RADIO.NETELLER.equals(rdNeteller)) {
						NetellerInfo netellerInfo = withdrawalModel.getNetellerInfo();
						if(netellerInfo != null) {
							if(netellerInfo.getAccountId() == null || StringUtils.isBlank(netellerInfo.getAccountId())) {
								// if customer don't input email account
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
								return false;
							}
						} else {						
								// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
							
						}
					}
				}
			} else if(IConstants.PAYMENT_METHOD.PAYZA == withdrawMethod) {
				if(rdPayza == null) {
					//  do late
				} else {
					if(IConstants.DEPOSIT_CHOOSE_RADIO.PAYZA.equals(rdPayza)) {
						PayzaInfo payzaInfo = withdrawalModel.getPayzaInfo();
						if(payzaInfo != null) {
							if(payzaInfo.getEmailAddress() == null || StringUtils.isBlank(payzaInfo.getEmailAddress())) {
								// if customer don't input email account
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							} else {
								if(!StringUtil.isEmail(payzaInfo.getEmailAddress())) {
									List<Object> listMsg = new ArrayList<Object>();
									listMsg.add(getText("nts.ams.fe.label.deposit.payza.email"));
									listMsg.add(getText("global.message.NAB007_2"));
									withdrawalModel.setErrorMessage(getText("global.message.NAB007", listMsg));
									addFieldError("errorMessage", getText("global.message.NAB007", listMsg));
									return false;
								}
							}
						} else {						
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;						
						}
					}
				}
			} else if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == withdrawMethod){
				if(rdBankwire == null){
					// later
				}else{
					if(IConstants.DEPOSIT_CHOOSE_RADIO.BANKWIRE.equals(rdBankwire)){
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.bank_information.bankTransfer"));
						withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
						addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
						return false;
						/*CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
						customerBankInfo.setAccountName(frontUserDetails.getFrontUserOnline().getFullName());
						if(customerBankInfo != null){
							if(StringUtils.isBlank(customerBankInfo.getBankName())){
								
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							}else if(StringUtils.isBlank(customerBankInfo.getBranchName())){
								
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.branchName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							}else if(StringUtils.isBlank(customerBankInfo.getAccountNo())){
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.accountNumber"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return false;
							}else if(StringUtils.isBlank(customerBankInfo.getSwiftCode())){
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.swiftCode"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return false;
							} else if(StringUtils.isBlank(customerBankInfo.getAccountName())){
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.beneficiaryName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));							
								return false;
							}else if(StringUtils.isBlank(customerBankInfo.getBankAddress())){
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankAddress"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return false;
							}else if(customerBankInfo.getCountryId() == -1){
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.country"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							}
						}*/
					}
				}
			} else if(IConstants.PAYMENT_METHOD.EXCHANGER == withdrawMethod){
				ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
				if(rdExchanger == null){
					// later
				}else{
					if(StringUtil.isEmpty(exchangerInfo.getExchangerId())){
						String msg =  getText("MSG_NAF001", new String[]{getText("nts.ams.fe.label.exchanger")});
						withdrawalModel.setErrorMessage(msg);
						addFieldError("errorMessage", msg);
						return false;
					}
					
					if(IConstants.DEPOSIT_CHOOSE_RADIO.EXCHANGER.equals(rdExchanger)){
						
						if (exchangerInfo != null) {
							if (StringUtils.isEmpty(exchangerInfo.getExchangerId())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.exchanger"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							} else if (StringUtils.isBlank(exchangerInfo.getBankName())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));							
								
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getBranchName())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.branchName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getAccountNo())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.accountNumber"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getAccountName())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.beneficiaryName"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return false;
							} else if(exchangerInfo.getCountryId() == -1) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.error.message.withdrawal.country"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return false;
							} 
						}
					}
				}
			} else if(IConstants.PAYMENT_METHOD.LIBERTY == withdrawMethod) {
				if(rdLiberty == null) {
					//  do late
					String msg = getText("deposit_transfer_method_liberty") + getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.validate.NAB001");
					addFieldError("errorMessage", msg);			
					withdrawalModel.setErrorMessage(msg);
					return false;
				} else {
					if(IConstants.DEPOSIT_CHOOSE_RADIO.LIBERTY.equals(rdLiberty)) {
						LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
						if(libertyInfo != null) {
							if(libertyInfo.getAccountNumber() == null || StringUtils.isBlank(libertyInfo.getAccountNumber())) {
								// if customer don't input email account
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.withdrawal.liberty.accountNumber"));
								withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
								return false;
							}
						} else {						
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.withdrawal.liberty.accountNumber"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
							
						}
					}
				}
			}
		}
		withdrawalModel.setErrorMessages(messageList);
		log.info("[START] validate withdrawal request on client side");
		return true;
	}
	
	private void loadListOfCountries(){
		withdrawalModel.setListCountry(withdrawalManager.getListCountry());
	}
	
	private void loadAvailablePaymentInfo(){
		readDefaultBankInfo();
		readBankInformation();
		readExchangerInfo();
		readEwalletInformation();
	}
	
	private void loadMinMaxWithdrawalAmount(){
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String currencyCode = frontUserOnline.getCurrencyCode();
		String wlCode = frontUserOnline.getWlCode();
		
		String maxWithdrawal = "";
		String minWithdrawal = "";
		
		String key = currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_WITHDRAWAL_AMOUNT;
		WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, wlCode);
		if(whiteLabelConfigInfo != null) {
			maxWithdrawal = whiteLabelConfigInfo.getConfigValue();
		}
		
		key = currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_WITHDRAWAL_AMOUNT;						
		whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
		if(whiteLabelConfigInfo != null) {
			minWithdrawal = whiteLabelConfigInfo.getConfigValue();
		}
		
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		if(withdrawalRuleInfo == null){
			withdrawalRuleInfo = new WithdrawalRuleInfo();
			withdrawalModel.setWithdrawalRuleInfo(withdrawalRuleInfo);
		}
		withdrawalRuleInfo.setMaxWithdrawAmount(MathUtil.parseBigDecimal(maxWithdrawal));
		withdrawalRuleInfo.setMinWithdrawAmount(MathUtil.parseBigDecimal(minWithdrawal));
	}
	
	private void loadWithdrawAccountInfos(){
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String customerId = frontUserOnline.getUserId();
		String currencyCode = frontUserOnline.getCurrencyCode();
		
		BalanceInfo balanceAmsInfo = withdrawalModel.getBalanceAmsInfo();
		List<Integer> listCustomerStatusCancel = accountManager.getListServiceTypeStatusCancel(customerId);
		if(balanceAmsInfo == null){
			balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currencyCode);
			balanceAmsInfo.setCurrencyCode(currencyCode);
			withdrawalModel.setBalanceAmsInfo(balanceAmsInfo);
		}
		
		BalanceInfo balanceFxInfo = withdrawalModel.getBalanceFxInfo();
		if(listCustomerStatusCancel.contains(IConstants.SERVICES_TYPE.FX))
			withdrawalModel.setBalanceFxInfo(null);
		else if(balanceFxInfo == null){
			balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currencyCode);
			balanceFxInfo.setCurrencyCode(currencyCode);
			withdrawalModel.setBalanceFxInfo(balanceFxInfo);
		}
		
		
		BalanceInfo balanceBoInfo = withdrawalModel.getBalanceBoInfo();
		if(listCustomerStatusCancel.contains(IConstants.SERVICES_TYPE.BO))
			withdrawalModel.setBalanceBoInfo(null);
		else if(balanceBoInfo == null){
			balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currencyCode);
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
			balanceBoInfo.setCurrencyCode(customerServiceInfo.getCurrencyCode());
			withdrawalModel.setBalanceBoInfo(balanceBoInfo);			
		}
		
		BalanceInfo balanceCopyTradeInfo = withdrawalModel.getBalanceCopyTradeInfo();
		if(listCustomerStatusCancel.contains(IConstants.SERVICES_TYPE.COPY_TRADE))
			withdrawalModel.setBalanceCopyTradeInfo(null);
		else if(balanceCopyTradeInfo == null){
			balanceCopyTradeInfo  = withdrawalModel.getBalanceScInfo();
			if(balanceCopyTradeInfo == null){
				balanceCopyTradeInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currencyCode);
				balanceCopyTradeInfo.setCurrencyCode(currencyCode);
			}			
			withdrawalModel.setBalanceCopyTradeInfo(balanceCopyTradeInfo);
		}
	}
	
	private void readDefaultBankInfo() {
		CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
		if(customerBankInfo == null){
			customerBankInfo = new CustomerBankInfo();
			withdrawalModel.setCustomerBankInfo(customerBankInfo);
		}
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		customerBankInfo.setAccountName(frontUserOnline.getFullName());
		Integer countryId = customerBankInfo.getCountryId();
		if(countryId == null || countryId.compareTo(IConstants.FRONT_OTHER.COMBO_TOP) == 0){
			countryId = frontUserOnline.getCountryId();
			customerBankInfo.setCountryId(countryId);
		}
	}

	/**
	   * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Sep 20, 2012
	 */
	public void readExchangerInfo() {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {
				//get map exchanger
				Map<String, String> mapExchanger = exchangerManager.getMapExchanger(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
				if (mapExchanger == null) {
					mapExchanger = new TreeMap<String, String>();
				}
				withdrawalModel.setMapExchanger(mapExchanger);
			} else {
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
			}
		}		
	}
	
	public String index1(){
		try{
			if(result != null) {
				getMsgCode(result);
			}
			setRawUrl(IConstants.FrontEndActions.WITHDRAWAL_INDEX);		
			withdrawalModel.setListCountry(withdrawalManager.getListCountry());		
			readCustomerInfo();
			readEwalletInformation();
			readDefaultBankInfo();
			readBankInformation();
			getWithdrawalHistory();	
//			readExchangerInfo();
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return INPUT;
	}
	
	private void readCustomerInfo(){
		log.info("[Start] read info of ");
		try {
			log.info("[start]get list withdrawal method from system property");
			
			Map<String, String> mapWithdrawalMethod = /*(Map<String, String>) ObjectCopy.copy(*/SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY +  IConstants.SYS_PROPERTY.WITHDRAWAL_METHOD);
			log.info("[end]get list withdrawal method from system property");
			log.info("[start]get list servicetype from system property");
			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);					
			withdrawalModel.setMapServiceType(mapServiceType);
			log.info("[end]get list servicetype from system property");
			
			log.info("[start]get list credit card payment from system property");
			Map<String, String> mapCreditCardPayment = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CREDIT_CARD_PAYMENT);					
			withdrawalModel.setMapCreditCardPayment(mapCreditCardPayment);
			log.info("[end]get list credit card payment from system property");
			
			CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
			if(customerBankInfo == null) {
				customerBankInfo = new CustomerBankInfo();
			}
			WithdrawalInfo withdrawalInfo = withdrawalModel.getWithdrawalInfo();
			if(withdrawalInfo == null){
				withdrawalInfo = new WithdrawalInfo();
			}else{
				withdrawalInfo.setMethodName(mapWithdrawalMethod.get(StringUtil.toString(withdrawalInfo.getWithdrawalMethod())));				
				withdrawalInfo.setServiceTypeName(mapServiceType.get(StringUtil.toString(withdrawalInfo.getServiceType())));				
				withdrawalModel.setWithdrawalInfo(withdrawalInfo);
			}
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);	
			RateInfo rateInfo1 = null;
			String symbolNameFx = null;
			String symbolNameBO = null;
			String maxWithdrawal  = "";
			String minWithdrawal  = "";
			
			
			
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					//get list exchanger
					Map<String, String> mapExchanger = exchangerManager.getMapExchanger(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
					if (mapExchanger == null || mapExchanger.size() <=0) {
						mapExchanger = new HashMap<String, String>();
						withdrawalModel.setMapExchanger(mapExchanger);
						mapWithdrawalMethod.remove(String.valueOf(IConstants.PAYMENT_METHOD.EXCHANGER));
					} else {
						withdrawalModel.setMapExchanger(mapExchanger);
					}
					
					if(!frontUserOnline.getCurrencyCode().equals(IConstants.CURRENCY_CODE.USD) && !frontUserOnline.getCurrencyCode().equals(IConstants.CURRENCY_CODE.EUR)){
						mapWithdrawalMethod.remove(String.valueOf(IConstants.PAYMENT_METHOD.LIBERTY));
					}
					
					//set payment method
					withdrawalModel.setMapPaymentMethod(mapWithdrawalMethod);
					
					log.info("customer ID" + frontUserOnline.getUserId());
					log.info("login ID" + frontUserOnline.getLoginId());
					customerBankInfo.setCustomerId(frontUserOnline.getUserId());
					customerBankInfo.setCurrencyCode(frontUserOnline.getCurrencyCode());
					
					customerBankInfo.setLoginId(frontUserOnline.getLoginId());				
					withdrawalModel.setCustomerBankInfo(customerBankInfo);
					
					withdrawalInfo.setCurrencyCode(frontUserOnline.getCurrencyCode());
					withdrawalInfo.setCurrencyCodeFX(frontUserOnline.getCurrencyCode()); //find currency code of FX
					withdrawalInfo.setCustomerId(frontUserOnline.getUserId());
					withdrawalModel.setWithdrawalInfo(withdrawalInfo);
					// get balance of AMS
					BalanceInfo balanceAmsInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.AMS, frontUserOnline.getCurrencyCode());
					// get balance of Fx
					BalanceInfo balanceFxInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.FX, frontUserOnline.getCurrencyCode());
					// get balance of Bo
					BalanceInfo balanceBoInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO, frontUserOnline.getCurrencyCode());
					// get balance of CopyTrade
					BalanceInfo balanceCopyTradeInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.COPY_TRADE, frontUserOnline.getCurrencyCode());
					
					withdrawalModel.setBalanceAmsInfo(balanceAmsInfo);
					withdrawalModel.setBalanceBoInfo(balanceBoInfo);
					withdrawalModel.setBalanceFxInfo(balanceFxInfo);
					withdrawalModel.setBalanceCopyTradeInfo(balanceCopyTradeInfo);
					
					log.info("[start] find rate and base currency of fx account");
					if(withdrawalInfo.getCurrencyCodeFX()!=null && (!withdrawalInfo.getCurrencyCode().equals(withdrawalInfo.getCurrencyCodeFX()))){
						//find currency code of FX
						symbolNameFx = withdrawalInfo.getCurrencyCodeFX() + withdrawalInfo.getCurrencyCode();
						log.info("basecurrency of fx " + withdrawalInfo.getCurrencyCodeFX() + "symbol Name : " + symbolNameFx);
						RateInfo rateInfo = depositManager.getRateInfo(withdrawalInfo.getCurrencyCodeFX(), withdrawalInfo.getCurrencyCode());
						if(rateInfo != null) {
							withdrawalInfo.setConvertedRate(StringUtil.toString(rateInfo.getRate()));
							log.info("converted rate" + withdrawalInfo.getConvertedRate());
						}
						
//						rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolNameFx, mapConfig);
//						if(rateInfo !=null){
//							withdrawalInfo.setRateSymbol(rateInfo.getSymbolName());
//							BigDecimal convertedRate = rateInfo.getBid();
//							convertedRate = convertedRate.add(rateInfo.getAsk()).divide(MathUtil.parseBigDecimal(2));
//							withdrawalInfo.setConvertedRate(StringUtil.toString(convertedRate));
//							log.info("converted rate" + withdrawalInfo.getConvertedRate());
//							
//						}else{
//							symbolNameFx = withdrawalInfo.getCurrencyCode() + withdrawalInfo.getCurrencyCodeFX();
//							rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolNameFx, mapConfig);
//							withdrawalInfo.setRateSymbol(rateInfo.getSymbolName());
//							
//							BigDecimal convertedRate = rateInfo.getBid();
//							convertedRate = convertedRate.add(rateInfo.getAsk()).multiply(MathUtil.parseBigDecimal(0.5));
//							withdrawalInfo.setConvertedRate(StringUtil.toString(convertedRate));
//							log.info("converted rate" + withdrawalInfo.getConvertedRate());
//						}
						
						withdrawalModel.setWithdrawalInfo(withdrawalInfo);
					}
					
					log.info("[end] find rate and base currency of fx account");
					//find currency code of BO
					log.info("[start] find rate and base currency of bo account");
					CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
					if(customerServiceInfo != null){
						withdrawalInfo.setCurrencyCodeBO(customerServiceInfo.getCurrencyCode());
						if(withdrawalInfo.getCurrencyCodeBO()!=null && (!withdrawalInfo.getCurrencyCode().equals(withdrawalInfo.getCurrencyCodeBO()))){
							symbolNameBO = withdrawalInfo.getCurrencyCodeBO() + withdrawalInfo.getCurrencyCode();
							log.info("base currency of BO" + withdrawalInfo.getCurrencyCodeBO() + "symbolName" + symbolNameBO);
							
//							rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolNameBO, mapConfig);
//							if(rateInfo !=null){
//								withdrawalInfo.setRateSymbol(rateInfo.getSymbolName());
//								BigDecimal convertedRate = rateInfo.getBid();
//								convertedRate = convertedRate.add(rateInfo.getAsk()).divide(MathUtil.parseBigDecimal(2));
//								withdrawalInfo.setConvertedRate(StringUtil.toString(convertedRate));
//								
//								log.info("converted rate" + withdrawalInfo.getConvertedRate());
//							}else{
//								symbolNameBO = withdrawalInfo.getCurrencyCode() + withdrawalInfo.getCurrencyCodeBO();
//								rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolNameBO, mapConfig);
//								withdrawalInfo.setRateSymbol(rateInfo.getSymbolName());
//								BigDecimal convertedRate = rateInfo.getBid();
//								convertedRate = convertedRate.add(rateInfo.getAsk()).multiply(MathUtil.parseBigDecimal(0.5));
//								withdrawalInfo.setConvertedRate(StringUtil.toString(convertedRate));
//								log.info("converted rate" + withdrawalInfo.getConvertedRate());
//							}
							
							RateInfo rateInfo = depositManager.getRateInfo(withdrawalInfo.getCurrencyCodeBO(), withdrawalInfo.getCurrencyCode());
							if(rateInfo != null) {
								withdrawalInfo.setConvertedRate(StringUtil.toString(rateInfo.getRate()));
								log.info("converted rate" + withdrawalInfo.getConvertedRate());
							}
						}
						String key = frontUserOnline.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_WITHDRAWAL_AMOUNT;
//						Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + frontUserOnline.getWlCode());						
//						maxWithdrawal = mapConfiguration.get(key);
						WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
						if(whiteLabelConfigInfo != null) {
							maxWithdrawal = whiteLabelConfigInfo.getConfigValue();
						}
						
						key = frontUserOnline.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_WITHDRAWAL_AMOUNT;						
//						minWithdrawal = mapConfiguration.get(key);
						whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
						if(whiteLabelConfigInfo != null) {
							minWithdrawal = whiteLabelConfigInfo.getConfigValue();
						}
						withdrawalInfo.setMaxAmount(MathUtil.parseBigDecimal(maxWithdrawal));
						withdrawalInfo.setMinAmount(MathUtil.parseBigDecimal(minWithdrawal));
						withdrawalModel.setWithdrawalInfo(withdrawalInfo);
					}
					log.info("[end] find rate and base currency of BO account");
				}				
				withdrawalModel.setWithdrawalInfo(withdrawalInfo);
				//find converted rate
				
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] read info");
	}
	
	private void getWithdrawalHistory(){
		log.info("[start] get history of:");
		String status = IConstants.STATUS_WITHDRAW.IN_PROGRESS + ","  + IConstants.STATUS_WITHDRAW.REQUESTING + "," + IConstants.STATUS_WITHDRAW.FAIL;
		try{
			PagingInfo pagingInfo = withdrawalModel.getPagingInfo();
			if(pagingInfo == null) {
				pagingInfo = new PagingInfo();			
			}	
			withdrawalModel.setPagingInfo(pagingInfo);
			List<WithdrawalInfo> listWithdrawalInfo = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					log.info("CustomerId = " + frontUserOnline.getUserId());
					listWithdrawalInfo = withdrawalManager.getWithdrawlHistory(frontUserOnline.getUserId(), pagingInfo, status);
					
				} else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
				}
			}		
			
			if(listWithdrawalInfo != null) {
				withdrawalModel.setListAmsWithdrawal(listWithdrawalInfo);
			}						
					
		} catch(Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	public String withdrawalConfirmed1(){
		try {
			setRawUrl(IConstants.FrontEndActions.WITHDRAWAL_CONFIRMED);
			withdrawalModel.setListCountry(withdrawalManager.getListCountry());
//			readExchangerInfo();
			readCustomerInfo();
			readPaymentInformation();
			readEwalletInformation();
			readBankInformation();
			getWithdrawalHistory();
			
			if(!validateWithdrawalForm()){
				return ERROR;
			}
			if (messageList != null &&  messageList.size() > 0) {
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}
			if(hasFieldErrors()){
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}
			WithdrawalInfo withdrawalInfo = withdrawalModel.getWithdrawalInfo();
			if(withdrawalInfo == null) {
				withdrawalInfo = new WithdrawalInfo();
			}
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(withdrawalModel.getCustomerBankInfo().getCustomerId(), IConstants.SERVICES_TYPE.FX);
			if(customerServiceInfo != null) {
				log.info("customer service id" + customerServiceInfo.getCustomerServiceId());
				withdrawalInfo.setCustomerId(customerServiceInfo.getCustomerServiceId());
				CustomerInfo customerInfo = accountManager.getCustomerInfo(customerServiceInfo.getCustomerId());
				if(customerInfo != null) {
					log.info("password" + customerInfo.getLoginPass());
					withdrawalInfo.setPassword(customerInfo.getLoginPass());
				}				
			} else {
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}			
			CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
			if(customerBankInfo == null) {
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}			
			withdrawalInfo.setActiveFlag(IConstants.ACTIVE_FLG.ACTIVE);
			withdrawalInfo.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			withdrawalInfo.setCurrencyCode(customerBankInfo.getCurrencyCode());
			withdrawalInfo.setWithdrawalAmount(MathUtil.parseDouble(withdrawalModel.getAmount()));
			withdrawalInfo.setRemark("");
			withdrawalInfo.setBeneficiaryBankName(customerBankInfo.getBankName());
			withdrawalInfo.setBeneficiaryAccountNo(customerBankInfo.getAccountNo());
			withdrawalInfo.setBeneficiaryBranchName(customerBankInfo.getBranchName());
			if(!withdrawalInfo.getWithdrawalMethod().equals(IConstants.WITHDRAW_METHOD.EXCHANGER)){
				withdrawalInfo.setBeneficiaryBankAddress(customerBankInfo.getBankAddress());
				withdrawalInfo.setBeneficiarySwiftCode(customerBankInfo.getSwiftCode());
			}
			withdrawalInfo.setBeneficiaryAccountName(customerBankInfo.getAccountName());
			withdrawalInfo.setCountryId(customerBankInfo.getCountryId());		
			/*//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
			withdrawalInfo.setWithdrawalFee(MathUtil.parseDouble(withdrawalModel.getWithdrawalFee()));
			withdrawalInfo.setReceivedAmount(MathUtil.parseDouble(withdrawalModel.getReceivedAmount()));			
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
			 */			
			withdrawalModel.setWithdrawalInfo(withdrawalInfo);
			return INPUT;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ERROR;
		}
	}
	public String withdrawalSubmit1(){
		log.info("[Start] withdrawal");
		withdrawalModel.setListCountry(withdrawalManager.getListCountry());
		try {
			readCustomerInfo();
			readEwalletInformation();
			readBankInformation();
			
			if(!validateWithdrawalForm()){
				return ERROR;
			}
			//[NTS1.0-le.xuan.tuong]Nov 8, 2012A - Start 
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				
				CustomerInfo custInfo = withdrawalManager.getCustomerInfo(frontUserOnline.getUserId());
				//check even if the customer has authority to withdraw or not - display message NAB021
				if(custInfo !=null){
					if(custInfo.getAllowWithdrawalFlg() == null || custInfo.getAllowWithdrawalFlg() == 0){
						addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));			
						withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
						return ERROR;
					}
				}
			}
			//[NTS1.0-le.xuan.tuong]Nov 8, 2012A - End
			WithdrawalInfo withdrawalInfo = withdrawalModel.getWithdrawalInfo();
			CustomerInfo customerInfo = new CustomerInfo();
			String wlCode = null;
			if(withdrawalInfo == null) {
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);							
				log.error("Withdrawal Info is null");
				return ERROR;				
			}
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(withdrawalModel.getCustomerBankInfo().getCustomerId(), IConstants.SERVICES_TYPE.FX);
			if(customerServiceInfo != null) {
				log.info("customer service id" + customerServiceInfo.getCustomerServiceId());
				withdrawalInfo.setCustomerId(customerServiceInfo.getCustomerId());
				customerInfo = accountManager.getCustomerInfo(customerServiceInfo.getCustomerId());
				if(customerInfo != null) {
					log.info("password" + customerInfo.getLoginPass());
					withdrawalInfo.setPassword(customerInfo.getLoginPass());
					wlCode = customerInfo.getWlCode();
				}				
			} else {
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}			
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + withdrawalInfo.getCurrencyCode());
			
			Integer withdrawalMethod = withdrawalInfo.getWithdrawalMethod();
			Integer serviceType 	 = withdrawalInfo.getServiceType(); 
			Integer result = 0;
			BigDecimal roundWithdrawalfee = MathUtil.parseBigDecimal(0);
			BigDecimal roundReceivedAmount = MathUtil.parseBigDecimal(0);
			Double withdrawalFee;
			switch (withdrawalMethod) {
			case IConstants.PAYMENT_METHOD.BANK_TRANSFER:
				Double receivedAmount = 0D;
				CustomerBankInfo bankwireInfo = withdrawalModel.getCustomerBankInfo();		
				if(serviceType != null){
					bankwireInfo.setServiceType(serviceType);
				}
				withdrawalFee = withdrawalManager.getWithdrawalFee(bankwireInfo.getAmount().doubleValue(), bankwireInfo.getCurrencyCode(), IConstants.PAYMENT_GATEWAY_ID.BANK_TRANSFER, wlCode);				
				//add for radio bankwire
				receivedAmount = bankwireInfo.getAmount().doubleValue() - withdrawalFee.doubleValue();

				roundWithdrawalfee = MathUtil.rounding(withdrawalFee, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				bankwireInfo.setWithdrawalFee(roundWithdrawalfee.doubleValue());
				
				roundReceivedAmount = MathUtil.rounding(receivedAmount, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				bankwireInfo.setReceivedAmount(roundReceivedAmount.doubleValue());
				
				result = withdrawalManager.withdrawalBankTransfer(bankwireInfo);
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					log.info("[End] withdrawal");	
					return SUCCESS;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				} else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}
			case IConstants.PAYMENT_METHOD.EXCHANGER:
				//add for radio bankwire
				ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();		
				if(serviceType != null){
					exchangerInfo.setServiceType(serviceType);
				}
				result = withdrawalManager.withdrawalExchanger(exchangerInfo);						
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					log.info("[End] withdrawal");	
					return SUCCESS;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				} else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}
			case IConstants.PAYMENT_METHOD.CREDIT_CARD:
				WithdrawalInfo info = withdrawalModel.getWithdrawalInfo();
				info.setWithdrawalAmount(new Double(withdrawalModel.getAmount()));
				
				withdrawalFee = withdrawalManager.getWithdrawalFee(info.getWithdrawalAmount().doubleValue(), info.getCurrencyCode(), IConstants.PAYMENT_GATEWAY_ID.CREDIT_CARD, wlCode);				
				//add for radio bankwire
				receivedAmount = info.getWithdrawalAmount().doubleValue() - withdrawalFee.doubleValue();							
				
				roundWithdrawalfee = MathUtil.rounding(withdrawalFee, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				roundReceivedAmount = MathUtil.rounding(receivedAmount, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				
				info.setWithdrawalFee(roundWithdrawalfee.doubleValue());
				
				
				info.setReceivedAmount(roundReceivedAmount.doubleValue());
				
				result = withdrawalManager.withdrawalCreditCard(info);						
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					
					log.info("[End] withdrawal");	
					return SUCCESS;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				}  else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}	
				
			case IConstants.PAYMENT_METHOD.NETELLER:
				// coding for netteller
				NetellerInfo netellerInfo = withdrawalModel.getNetellerInfo();
				if(serviceType != null){
					netellerInfo.setServiceType(serviceType);
				}
				//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start calculate withdrawal fee
				withdrawalFee = withdrawalManager.getWithdrawalFee(netellerInfo.getAmount().doubleValue(), netellerInfo.getCurrencyCode(), IConstants.PAYMENT_GATEWAY_ID.NETELLER, wlCode);				
				//add for radio bankwire
				receivedAmount = netellerInfo.getAmount().doubleValue() - withdrawalFee.doubleValue();
				
				roundWithdrawalfee = MathUtil.rounding(withdrawalFee, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				roundReceivedAmount = MathUtil.rounding(receivedAmount, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
											
				netellerInfo.setWithdrawalFee(roundWithdrawalfee.doubleValue());
				netellerInfo.setReceivedAmount(roundReceivedAmount.doubleValue());
				
				//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
				result = withdrawalManager.withdrawalNeteller(netellerInfo);				
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					log.info("[End] withdrawal");	
					return SUCCESS;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				}  else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}
			case IConstants.PAYMENT_METHOD.LIBERTY:
				// coding for netteller
				LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
				if(serviceType != null){
					libertyInfo.setServiceType(serviceType);
				}
				//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start calculate withdrawal fee
				withdrawalFee = withdrawalManager.getWithdrawalFee(libertyInfo.getAmount().doubleValue(), libertyInfo.getCurrencyCode(), IConstants.PAYMENT_GATEWAY_ID.LIBERTY, wlCode);				
				//add for radio bankwire
				receivedAmount = libertyInfo.getAmount().doubleValue() - withdrawalFee.doubleValue();
				
				roundWithdrawalfee = MathUtil.rounding(withdrawalFee, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				roundReceivedAmount = MathUtil.rounding(receivedAmount, currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
											
				libertyInfo.setWithdrawalFee(roundWithdrawalfee.doubleValue());
				libertyInfo.setReceivedAmount(roundReceivedAmount.doubleValue());
				
				//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
				result = withdrawalManager.withdrawalLiberty(libertyInfo);				
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					log.info("[End] withdrawal");	
					return SUCCESS;
				} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				}  else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}	
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 16, 2012D - Start  disable function withdrawl via payza
			/*case IConstants.PAYMENT_METHOD.PAYZA:
			// coding for rdPayza
			//String rdPayza = withdrawalModel.getRdPayza();
			PayzaInfo payzaInfo = withdrawalModel.getPayzaInfo();
			if(serviceType !=null){
				payzaInfo.setServiceType(serviceType);
			}
			if(payzaInfo !=null){
				result = withdrawalManager.withdrawalPayza(payzaInfo);
				if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {					
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
					log.info("[End] withdrawal");	
					return SUCCESS;
				}  else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
					withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
					getWithdrawalHistory();
					return ERROR;
				} else {
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
					setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
					getWithdrawalHistory();
					return ERROR;
				}
			}
			
			return SUCCESS;*/
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 16, 2012D - End
			}
		} catch (Exception e) {
			withdrawalModel.setListCountry(withdrawalManager.getListCountry());		
			readExchangerInfo();
			log.error(e.toString(), e);
			return ERROR;
		}
		log.info("[End] withdrawal");
		return SUCCESS;
	}
	public String withdrawalCancel(){
		log.info("[Start] cancel a withdrawal transaction");
		try {
			String withdrawalId = withdrawalModel.getWithdrawalId();
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();
			String customerId = getCurrentCustomerId();
			
			if(withdrawalId != null){
				log.info("withdrawal Id" + withdrawalId);
				amsWithdrawal =  withdrawalManager.getAmsWithdrawal(withdrawalId);				
				if(amsWithdrawal.getStatus().equals(IConstants.STATUS_WITHDRAW.REQUESTING)){
					amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.CANCEL);
					amsWithdrawal.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					withdrawalManager.updateStatusofWithdrawal(amsWithdrawal, customerId);
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANCEL);
				}else {
					getWithdrawalHistory();
					setMsgCode(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANNOT_CANCEL);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ERROR;
		}
		log.info("[End] cancel a withdrawal transaction");
		return SUCCESS;
	}
	
	private boolean validateInputAmountAndWithdrawPermission(){
		clearFieldErrors();
		CustomerInfo custInfo = new CustomerInfo();
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String wlCode = frontUserOnline.getWlCode();
		String customerId = frontUserOnline.getUserId();
		
		String currencyCode = frontUserOnline.getCurrencyCode();
		Double cashBalance = withdrawalManager.getCashBalance(customerId, currencyCode, 0);
		if(cashBalance !=null && cashBalance < 0){
			addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			return false;
		}
		if(frontUserDetails != null) {
			if(frontUserOnline != null) {
				log.info("customer ID" + frontUserOnline.getUserId());
				log.info("login ID" + frontUserOnline.getLoginId());
				custInfo = withdrawalManager.getCustomerInfo(customerId);
			}
		}
		Double amountAvaiable = withdrawalModel.getBalanceAmsInfo().getAmountAvailable();
		if(custInfo !=null){
			if(custInfo.getAllowWithdrawalFlg() == null || custInfo.getAllowWithdrawalFlg().compareTo(IConstants.ALLOW_FLG.INALLOW) == 0 || amountAvaiable <= 0){
				addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
				return false;
			}
		}
		//Check CASH_BALANCE of customer for akazan flow 
		Integer countAkazan = withdrawalManager.getCountAkazan(customerId);
		if(countAkazan > 0){
			addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			return false;
		}
		//Check negative cash balance
		Integer countAkazan1 = withdrawalManager.getCountAkazanNegativeBlance(customerId);
		if(countAkazan1 > 0) {
			addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAB021"));
			return false;
		}
		//Check Withdrawal limitation as 1 time per day
		if(!withdrawalManager.checkWithDrawalLimitPerDay()){
			addFieldError("errorMessage", getText("nts.ams.fe.message.withdrawal.MSG_NAF0003"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.withdrawal.MSG_NAF0003"));
			return false;
		}
		BigDecimal amount = MathUtil.parseBigDecimal(withdrawalModel.getAmount(), null);
		if(StringUtil.isEmpty(withdrawalModel.getAmount())){
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.withdrawal.amount"));
			withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
			addFieldError("errorMessage", getText("MSG_NAF001", listContent));
			return false;
		}
		
		if(amount == null){
			addFieldError("errorMessage", getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));			
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));
			return false;
		}
		
		if(amount != null && amount.compareTo(BigDecimal.ZERO) <= 0){
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.withdrawal.amount"));
			addFieldError("errorMessage", getText("nts.ams.fe.error.message.amount.required.MSG_NAB013",listContent));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.error.message.amount.required.MSG_NAB013",listContent));
			return false;
		}
		
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		BigDecimal withdrawalAmountMax = withdrawalRuleInfo.getMaxWithdrawAmount();
		BigDecimal withdrawalAmountMin = withdrawalRuleInfo.getMinWithdrawAmount();
		
		if(amount.compareTo(withdrawalAmountMax) > 0) {
			messageList.add(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));
			return false;
		}
		
		if((amount.compareTo(withdrawalAmountMin) < 0) && amountAvaiable.compareTo(withdrawalAmountMin.doubleValue()) > 0) {//modify by HungLV fix bugs #16192
			messageList.add(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));
			return false;
		}
		
		if(amountAvaiable.compareTo(withdrawalAmountMin.doubleValue()) < 0){//add by HungLV 21/05/2013 fix bugs #16192
			withdrawalModel.setAmount(String.valueOf(amountAvaiable));
		}
		Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
		String key = IConstants.WHITE_LABEL_CONFIG.WITHDRAWAL_NUMBER_PERDAY;
		Integer withdrawalAmountNumber =  MathUtil.parseInteger(mapConfiguration.get(key));
		/*String customerId = null;
		CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.FX);
		if(customerServiceInfo != null) {
			customerId = customerServiceInfo.getCustomerId();
		}*/
		Integer countWithdrawal = withdrawalManager.summaryOfWithDrawalPerday(customerId, IConstants.APP_DATE.FRONT_DATE);
		if(withdrawalAmountNumber.compareTo(countWithdrawal) <= 0) {
			messageList.add(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));
			return false;
		}
		
		

		//balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currencyCode);
		//balanceAmsInfo.setCurrencyCode(currencyCode);
		BalanceInfo balanceAmsInfo = withdrawalModel.getBalanceAmsInfo();
		//withdrawalModel.setBalanceAmsInfo(balanceAmsInfo);
		
		amount = MathUtil.parseBigDecimal(withdrawalModel.getAmount(), null);
		BigDecimal withdrawableAmount = MathUtil.parseBigDecimal(0);
		
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		
		try {
			if(balanceAmsInfo != null) {
//				CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceAmsInfo.getCurrencyCode());
				if(currencyInfo != null) {
					withdrawableAmount = MathUtil.rounding(balanceAmsInfo.getAmountAvailable(), scale, rounding);
				} else {
					withdrawableAmount = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
				}
			}else{
				addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
				withdrawalModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
				return false;						
			}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		log.info("start compare amount= " + amount.doubleValue() + "with Withdrawal Amount= " + withdrawableAmount.doubleValue());
		if(amount.compareTo(withdrawableAmount) > 0){
			messageList.add(getText("MSG_NAB022"));
			addFieldError("errorMessage", getText("MSG_NAB022"));
			withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
			return false;
		}
			
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		withdrawalModel.setAmount(StringUtil.toString(amount));
		return true;
	}
	
	
	
	private boolean validateWithdrawalForm(){		
		log.info("[START] validate withdrawal request on client side");
		
		if(!validateInputAmountAndWithdrawPermission()){
			return false;
		}
		
		String customerId = null;
		String currencyCode = null;
		String wlCode = "";
		WithdrawalInfo withdrawalInfo = withdrawalModel.getWithdrawalInfo();
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		BalanceInfo balanceAmsInfo = null;
		BalanceInfo balanceBoInfo = null;
		BalanceInfo balanceFxInfo = null;
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if(frontUserOnline != null) {
				currencyCode = frontUserOnline.getCurrencyCode();
				log.info("customer ID" + frontUserOnline.getUserId());
				log.info("login ID" + frontUserOnline.getLoginId());
				CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.FX);
				if(customerServiceInfo != null) {
					customerId = customerServiceInfo.getCustomerId();
				}
				// get balance of AMS
				balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currencyCode);
				// get balance of FX
				balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currencyCode);
				// get balance of BO
				balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currencyCode);
				
				wlCode = frontUserOnline.getWlCode();
			}			
		}
		
		if(withdrawalInfo == null) {
			addFieldError("errorMessage", getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));			
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));
			return false;
		}
		
		BigDecimal withdrawAmount2 = withdrawalRuleInfo.getWithdrawAmount2();
		Integer paymentgwId = null;
		Integer method = withdrawalInfo.getWithdrawalMethod();
		if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == method.intValue()) {
			paymentgwId = IConstants.PAYMENT_GATEWAY_ID.BANK_TRANSFER;
		} else if(IConstants.PAYMENT_METHOD.CREDIT_CARD == method.intValue()) {
			paymentgwId = IConstants.PAYMENT_GATEWAY_ID.CREDIT_CARD;
		} else if(IConstants.PAYMENT_METHOD.NETELLER == method.intValue()) {
			paymentgwId = IConstants.PAYMENT_GATEWAY_ID.NETELLER;
		}
		if(paymentgwId != null) {
			Double withdrawalFee = withdrawalManager.getWithdrawalFee(withdrawAmount2.doubleValue(), currencyCode, paymentgwId, wlCode);
			withdrawalInfo.setWithdrawalFee(withdrawalFee);
			Double receivedAmount = withdrawAmount2.doubleValue() - withdrawalFee;
			withdrawalInfo.setReceivedAmount(receivedAmount);
			if(withdrawalFee != null){
				if(withdrawAmount2.compareTo(MathUtil.parseBigDecimal(withdrawalFee)) <= 0){
					List<String> listContent = new ArrayList<String>();
					listContent.add(StringUtil.toString(withdrawalFee));
					listContent.add(currencyCode);
					withdrawalModel.setErrorMessage(getText("MSG_NAB101", listContent));
					addFieldError("errorMessage", getText("MSG_NAB101", listContent));
					log.info("input amount "+ withdrawAmount2 + "is smaller than withdrawal fee" + withdrawalFee);
					return false;
				}
			}
		}
		
		BigDecimal amount = MathUtil.parseBigDecimal(withdrawalModel.getAmount(), null);
		BigDecimal withdrawableAmount = MathUtil.parseBigDecimal(0);
		
		try {
			if(IConstants.SERVICES_TYPE.AMS.equals(withdrawalInfo.getServiceType())){
				if(balanceAmsInfo != null) {
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceAmsInfo.getCurrencyCode());
					if(currencyInfo != null) {
						withdrawableAmount = MathUtil.rounding(balanceAmsInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
					} else {
						withdrawableAmount = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
					}
				}else{
					addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					return false;						
				}				
			} else if(IConstants.SERVICES_TYPE.BO.equals(withdrawalInfo.getServiceType())){
				if(balanceBoInfo != null) {
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceBoInfo.getCurrencyCode());
					if(currencyInfo != null) {
						withdrawableAmount = MathUtil.rounding(balanceBoInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
					} else {
						withdrawableAmount = MathUtil.parseBigDecimal(balanceBoInfo.getAmountAvailable());
					}
				}else{
					addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					return false;
				}
			} else if(IConstants.SERVICES_TYPE.FX.equals(withdrawalInfo.getServiceType())){
				if(balanceFxInfo != null) {
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceFxInfo.getCurrencyCode());
					if(currencyInfo != null) {
						withdrawableAmount = MathUtil.rounding(balanceFxInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
					} else {
						withdrawableAmount = MathUtil.parseBigDecimal(balanceFxInfo.getAmountAvailable());
					}
				}else{
					addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					withdrawalModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
					return false;
				}
			}
							
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		log.info("start compare amount= " + amount.doubleValue() + "with Withdrawal Amount= " + withdrawableAmount.doubleValue());
		if(amount.compareTo(withdrawableAmount) > 0){
			messageList.add(getText("MSG_NAB022"));
			addFieldError("errorMessage", getText("MSG_NAB022"));
			withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
			return false;
		}
			
		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		withdrawalModel.setAmount(StringUtil.toString(amount));

		String rdNeteller = withdrawalModel.getRdNeteller();
		String rdPayza = withdrawalModel.getRdPayza();
		String rdBankwire = withdrawalModel.getRdBankwire();
		String rdExchanger = withdrawalModel.getRdExchanger();
		String rdLiberty = withdrawalModel.getRdLiberty();
		if(IConstants.PAYMENT_METHOD.NETELLER == withdrawalInfo.getWithdrawalMethod()) {
			if(rdNeteller == null) {
				//  do late
			} else {
				if(IConstants.DEPOSIT_CHOOSE_RADIO.NETELLER.equals(rdNeteller)) {
					NetellerInfo netellerInfo = withdrawalModel.getNetellerInfo();
					if(netellerInfo != null) {
						if(netellerInfo.getAccountId() == null || StringUtils.isBlank(netellerInfo.getAccountId())) {
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
							return false;
						}
					} else {						
							// if customer don't input email account
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
						withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
						addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
						return false;
						
					}
				}
			}
		} else if(IConstants.PAYMENT_METHOD.PAYZA == withdrawalInfo.getWithdrawalMethod()) {
			if(rdPayza == null) {
				//  do late
			} else {
				if(IConstants.DEPOSIT_CHOOSE_RADIO.PAYZA.equals(rdPayza)) {
					PayzaInfo payzaInfo = withdrawalModel.getPayzaInfo();
					if(payzaInfo != null) {
						if(payzaInfo.getEmailAddress() == null || StringUtils.isBlank(payzaInfo.getEmailAddress())) {
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						} else {
							if(!StringUtil.isEmail(payzaInfo.getEmailAddress())) {
								List<Object> listMsg = new ArrayList<Object>();
								listMsg.add(getText("nts.ams.fe.label.deposit.payza.email"));
								listMsg.add(getText("global.message.NAB007_2"));
								withdrawalModel.setErrorMessage(getText("global.message.NAB007", listMsg));
								addFieldError("errorMessage", getText("global.message.NAB007", listMsg));
								return false;
							}
						}
					} else {						
						// if customer don't input email account
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
						withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
						addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
						return false;						
					}
				}
			}
		} else if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == withdrawalInfo.getWithdrawalMethod()){
			if(rdBankwire == null){
				// later
			}else{
				if(IConstants.DEPOSIT_CHOOSE_RADIO.BANKWIRE.equals(rdBankwire)){
					CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
					customerBankInfo.setAccountName(frontUserDetails.getFrontUserOnline().getFullName());
					if(customerBankInfo != null){
						if(StringUtils.isBlank(customerBankInfo.getBankName())){
							
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						}else if(StringUtils.isBlank(customerBankInfo.getBranchName())){
							
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.branchName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						}else if(StringUtils.isBlank(customerBankInfo.getAccountNo())){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.accountNumber"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;
						}else if(StringUtils.isBlank(customerBankInfo.getSwiftCode())){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.swiftCode"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;
						} else if(StringUtils.isBlank(customerBankInfo.getAccountName())){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.beneficiaryName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));							
							return false;
						}else if(StringUtils.isBlank(customerBankInfo.getBankAddress())){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankAddress"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;
						}else if(customerBankInfo.getCountryId() == -1){
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.country"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						}
					}
				}
			}
		} else if(IConstants.PAYMENT_METHOD.EXCHANGER == withdrawalInfo.getWithdrawalMethod()){
			ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
			if(rdExchanger == null){
				// later
			}else{
				
				if(StringUtil.isEmpty(exchangerInfo.getExchangerId())){
					String msg =  getText("MSG_NAF001", new String[]{getText("nts.ams.fe.label.exchanger")});
					withdrawalModel.setErrorMessage(msg);
					addFieldError("errorMessage", msg);
					return false;
				}
				
				if(IConstants.DEPOSIT_CHOOSE_RADIO.EXCHANGER.equals(rdExchanger)){
					
					if (exchangerInfo != null) {
						if (StringUtils.isEmpty(exchangerInfo.getExchangerId())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.exchanger"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						} else if (StringUtils.isBlank(exchangerInfo.getBankName())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));							
							
							return false;
						} else if(StringUtils.isBlank(exchangerInfo.getBranchName())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.branchName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						} else if(StringUtils.isBlank(exchangerInfo.getAccountNo())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.accountNumber"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							
							return false;
						/*} else if(StringUtils.isBlank(exchangerInfo.getSwiftCode())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.swiftCode"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;*/
						} else if(StringUtils.isBlank(exchangerInfo.getAccountName())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.beneficiaryName"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;
						/*} else if(StringUtils.isBlank(exchangerInfo.getBankAddress())) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.bankAddress"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;*/
						} else if(exchangerInfo.getCountryId() == -1) {
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.error.message.withdrawal.country"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return false;
						} 
					}
				}
			}
		} else if(IConstants.PAYMENT_METHOD.LIBERTY == withdrawalInfo.getWithdrawalMethod()) {
			if(rdLiberty == null) {
				//  do late
				String msg = getText("deposit_transfer_method_liberty") + getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.validate.NAB001");
				addFieldError("errorMessage", msg);			
				withdrawalModel.setErrorMessage(msg);
				return false;
			} else {
				if(IConstants.DEPOSIT_CHOOSE_RADIO.LIBERTY.equals(rdLiberty)) {
					LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
					if(libertyInfo != null) {
						if(libertyInfo.getAccountNumber() == null || StringUtils.isBlank(libertyInfo.getAccountNumber())) {
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.withdrawal.liberty.accountNumber"));
							withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
							return false;
						}
					} else {						
							// if customer don't input email account
						List<String> listContent = new ArrayList<String>();
						listContent.add(getText("nts.ams.fe.label.withdrawal.liberty.accountNumber"));
						withdrawalModel.setErrorMessage(getText("MSG_NAF001", listContent));
						addFieldError("errorMessage", getText("MSG_NAF001", listContent));
						
						return false;
						
					}
				}
			}
		}
		withdrawalModel.setErrorMessages(messageList);
		log.info("[START] validate withdrawal request on client side");
		return true;
	}	
	
	/**
	 * @return the withdrawalManager
	 */
	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	/**
	 * @param withdrawalManager the withdrawalManager to set
	 */
	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}

	/**
	 * @return the accountManager
	 */
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	/**
	 * @param accountManager the accountManager to set
	 */
	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	private void getMsgCode(String msgCode) {
		if(msgCode != null) {
			if(msgCode.equals(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_SUCCESS)) {
				withdrawalModel.setSuccessMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.withdrawal.success"));
			}
			if(msgCode.equals(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANCEL)) {
				withdrawalModel.setSuccessMessage(getText("nts.ams.fe.message.withdrawal.cancel.MSG_NAB040"));
			}			
			if(msgCode.equals(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANNOT_CANCEL)){
				withdrawalModel.setErrorMessage(getText("MSG_NAB039"));
			}
			if(msgCode.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY)){
				withdrawalModel.setErrorMessage(getText("MSG_NAB022"));
			}
			
		}
	}
	
	private void readPaymentInformation() {
		//WithdrawalInfo withdrawalInfo = withdrawalModel.getWithdrawalInfo();
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		String customerId = "";
		String wlCode = "";
		String currencyCode = "";
		String publicKey= "";
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if(frontUserOnline != null) {
				customerId = frontUserOnline.getUserId();
				wlCode = frontUserOnline.getWlCode();
				currencyCode = frontUserOnline.getCurrencyCode();
				publicKey = frontUserOnline.getPublicKey();
			}
		}
		if(IConstants.PAYMENT_METHOD.NETELLER == withdrawalRuleInfo.getPaymentMethod()) {
			// if method is neteller
			String rndNeteller = withdrawalModel.getRdNeteller();
			if(IConstants.DEPOSIT_CHOOSE_RADIO.NETELLER.equals(rndNeteller)) {
				// if customer input new account
				NetellerInfo netellerInfo = withdrawalModel.getNetellerInfo();
				if(netellerInfo != null) {
					netellerInfo.setWlCode(wlCode);
					netellerInfo.setCustomerId(customerId);
					netellerInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					netellerInfo.setCurrencyCode(currencyCode);
					netellerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					netellerInfo.setServiceType(withdrawalRuleInfo.getServiceType());			
					withdrawalModel.setNetellerInfo(netellerInfo);
					withdrawalModel.setRdNeteller(rndNeteller);
				}
			} else {
				// if customer choose on payment information
				String accountId = rndNeteller;
				CustomerEwalletInfo customerEwalletInfo = depositManager.getNetellerInfo(customerId, accountId, publicKey);
				if(customerEwalletInfo != null) {
					NetellerInfo netellerInfo = new NetellerInfo();
					netellerInfo.setAccountId(accountId);
					netellerInfo.setSecureId(customerEwalletInfo.getEwalletSecureId());
					netellerInfo.setWlCode(wlCode);
					netellerInfo.setCustomerId(customerId);
					netellerInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					netellerInfo.setCurrencyCode(currencyCode);
					netellerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					netellerInfo.setServiceType(withdrawalRuleInfo.getServiceType());			
					withdrawalModel.setNetellerInfo(netellerInfo);
					withdrawalModel.setRdNeteller(accountId);
				}
			}
		} else if(IConstants.PAYMENT_METHOD.PAYZA == withdrawalRuleInfo.getPaymentMethod()) {
			// if method is payza
			String rndPayza = withdrawalModel.getRdPayza();
			if(IConstants.DEPOSIT_CHOOSE_RADIO.PAYZA.equals(rndPayza)) {
				// if customer input new email and api password
				PayzaInfo payzaInfo = withdrawalModel.getPayzaInfo();
				if(payzaInfo != null) {
					payzaInfo.setWlCode(wlCode);
					payzaInfo.setCustomerId(customerId);
					payzaInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					payzaInfo.setCurrencyCode(currencyCode);
					payzaInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.PAYZA);
					payzaInfo.setServiceType(withdrawalRuleInfo.getServiceType());
					payzaInfo.setCustomerId(customerId);
					withdrawalModel.setPayzaInfo(payzaInfo);
					withdrawalModel.setRdPayza(rndPayza);
				}
			} else {
				String email = rndPayza;
				CustomerEwalletInfo customerEwalletInfo = depositManager.getPayzaInfo(customerId, email, publicKey);
				if(customerEwalletInfo != null) {
					PayzaInfo payzaInfo = new PayzaInfo();
					payzaInfo.setEmailAddress(email);
					payzaInfo.setApiPassword(customerEwalletInfo.getEwalletApiPassword());
					payzaInfo.setWlCode(wlCode);
					payzaInfo.setCustomerId(customerId);
					payzaInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					payzaInfo.setCurrencyCode(currencyCode);
					payzaInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					payzaInfo.setServiceType(withdrawalRuleInfo.getServiceType());
					payzaInfo.setCustomerId(customerId);
					withdrawalModel.setPayzaInfo(payzaInfo);
					withdrawalModel.setRdPayza(email);
				}
			}
			
		}else if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == withdrawalRuleInfo.getPaymentMethod()){
			String rndBankwire = withdrawalModel.getRdBankwire();
			if(IConstants.DEPOSIT_CHOOSE_RADIO.BANKWIRE.equals(rndBankwire)){
				CustomerBankInfo customerBankInfo = withdrawalModel.getCustomerBankInfo();
				String countryName = null;
				if(customerBankInfo !=null){
					countryName = withdrawalManager.getCountryName(customerBankInfo.getCountryId());
					customerBankInfo.setCountryName(countryName);
					customerBankInfo.setServiceType(withdrawalRuleInfo.getServiceType());
					customerBankInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
					customerBankInfo.setCustomerId(customerId);
					customerBankInfo.setWlCode(wlCode);
					customerBankInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					customerBankInfo.setCurrencyCode(currencyCode);
					withdrawalModel.setCustomerBankInfo(customerBankInfo);
					withdrawalModel.setRdBankwire(rndBankwire);
				}
			}else{
				String customerBankId = rndBankwire;
				CustomerBankInfo customerBankInfo = withdrawalManager.getCustomerBankInfo(customerBankId);
				//customerBankInfo.setServiceType(withdrawalInfo.getServiceType());
				customerBankInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
				customerBankInfo.setCustomerId(customerId);
				customerBankInfo.setWlCode(wlCode);
				customerBankInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
				customerBankInfo.setCurrencyCode(currencyCode);
				
				withdrawalModel.setCustomerBankInfo(customerBankInfo);
				withdrawalModel.setRdBankwire(rndBankwire);
			}
		} else if(IConstants.PAYMENT_METHOD.EXCHANGER == withdrawalRuleInfo.getPaymentMethod()){
			String rndExchanger = withdrawalModel.getRdExchanger();
			if(IConstants.DEPOSIT_CHOOSE_RADIO.EXCHANGER.equals(rndExchanger)){
				ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
				
				String countryName = null;
				if(exchangerInfo !=null){
					countryName = withdrawalManager.getCountryName(exchangerInfo.getCountryId());
					exchangerInfo.setCountryName(countryName);
					exchangerInfo.setServiceType(withdrawalRuleInfo.getServiceType());
					exchangerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
					exchangerInfo.setCustomerId(customerId);
					exchangerInfo.setWlCode(wlCode);
					exchangerInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					exchangerInfo.setCurrencyCode(currencyCode);
					withdrawalModel.setExchangerInfo(exchangerInfo);
					withdrawalModel.setRdExchanger(rndExchanger);
				}
			}else{
				String customerBankId = rndExchanger;
				CustomerBankInfo customerBankInfo = withdrawalManager.getCustomerBankInfo(customerBankId);
				ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
				if(customerBankInfo != null) {
					exchangerInfo.setCountryName(customerBankInfo.getCountryName());
					exchangerInfo.setServiceType(customerBankInfo.getServiceType());
					exchangerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.EXCHANGER);
					exchangerInfo.setCustomerId(customerId);
					exchangerInfo.setWlCode(wlCode);
					exchangerInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					exchangerInfo.setCurrencyCode(currencyCode);
					//bank
					exchangerInfo.setBankName(customerBankInfo.getBankName());
					exchangerInfo.setBranchName(customerBankInfo.getBranchName());
					exchangerInfo.setAccountNo(customerBankInfo.getAccountNo());
					exchangerInfo.setAccountName(customerBankInfo.getAccountName());
					exchangerInfo.setSwiftCode(customerBankInfo.getSwiftCode());
					exchangerInfo.setBankAddress(customerBankInfo.getBankAddress());
					exchangerInfo.setBankNameKana(customerBankInfo.getBankNameKana());
					exchangerInfo.setBranchNameKana(customerBankInfo.getBranchNameKana());
					exchangerInfo.setAccountNameKana(customerBankInfo.getAccountNameKana());
				}
				withdrawalModel.setExchangerInfo(exchangerInfo);
				withdrawalModel.setRdExchanger(rndExchanger);
			}
		} else if (IConstants.PAYMENT_METHOD.LIBERTY == withdrawalRuleInfo.getPaymentMethod()) {
			String rndLiberty = withdrawalModel.getRdLiberty();
			if(IConstants.DEPOSIT_CHOOSE_RADIO.LIBERTY.equals(rndLiberty)) {
				// if customer input new account
				LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
				if(libertyInfo != null) {
					libertyInfo.setWlCode(wlCode);
					libertyInfo.setCustomerId(customerId);
					libertyInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					libertyInfo.setCurrencyCode(currencyCode);
					libertyInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.LIBERTY);
					libertyInfo.setServiceType(withdrawalRuleInfo.getServiceType());			
					withdrawalModel.setLibertyInfo(libertyInfo);
					withdrawalModel.setRdNeteller(rndLiberty);
				}
			} else {
				// if customer choose on payment information
				String accountId = rndLiberty;
				CustomerEwalletInfo customerEwalletInfo = withdrawalManager.getLibertyInfo(customerId, accountId, publicKey);
				if(customerEwalletInfo != null) {
					LibertyInfo libertyInfo = new LibertyInfo();
					libertyInfo.setAccountNumber(accountId);
					libertyInfo.setSecurityWord(customerEwalletInfo.getEwalletSecureWord());
					libertyInfo.setApiName(customerEwalletInfo.getEwalletApiName());
					libertyInfo.setWlCode(wlCode);
					libertyInfo.setCustomerId(customerId);
					libertyInfo.setAmount(MathUtil.parseBigDecimal(withdrawalModel.getAmount()));
					libertyInfo.setCurrencyCode(currencyCode);
					libertyInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.LIBERTY);
					libertyInfo.setServiceType(withdrawalRuleInfo.getServiceType());			
					withdrawalModel.setLibertyInfo(libertyInfo);
					withdrawalModel.setRdLiberty(accountId);
				}
			}
		}
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
	 * @return the ibManager
	 */
	public IIBManager getIbManager() {
		return ibManager;
	}

	/**
	 * @param ibManager the ibManager to set
	 */
	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}

	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	/**
	 * @return the depositManager
	 */
	public IDepositManager getDepositManager() {
		return depositManager;
	}

	/**
	 * @param depositManager the depositManager to set
	 */
	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}
	private void readEwalletInformation() {
		try {
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					String publicKey = frontUserOnline.getPublicKey();
					Map<Integer, List<CustomerEwalletInfo>> mapListCustomerEwalletInfo = depositManager.getListCustomerEwalletInfo(customerId, publicKey);
					if(mapListCustomerEwalletInfo != null && mapListCustomerEwalletInfo.size() > 0) {
						// get list neteller on db
						List<CustomerEwalletInfo> listCustomerEwalletInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.NETELLER);
						if(listCustomerEwalletInfo == null){
							listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
						}
						withdrawalModel.setListNetteller(listCustomerEwalletInfo);
						// get list payza on db
						listCustomerEwalletInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.PAYZA);
						withdrawalModel.setListPayza(listCustomerEwalletInfo);
						
						listCustomerEwalletInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.LIBERTY);
						withdrawalModel.setListLiberty(listCustomerEwalletInfo);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		
	}	
	private void readBankInformation(){
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					List<CustomerBankInfo> listCustomerBankInfo = withdrawalManager.getListCustomerBankInfo(customerId);
					if(listCustomerBankInfo != null && listCustomerBankInfo.size() > 0){
						for(CustomerBankInfo cib:listCustomerBankInfo){
							if(cib.getBankAccClass().equals(1)){
								cib.setAccountTypeStr(getText("nts.socialtrading.moneytransaction.withdrawal.label.normal"));
							}
							if(cib.getBankAccClass().equals(2)){
								cib.setAccountTypeStr(getText("nts.socialtrading.moneytransaction.withdrawal.label.current"));
							}
							if(cib.getBankAccClass().equals(3)){
								cib.setAccountTypeStr(getText("nts.socialtrading.moneytransaction.withdrawal.label.savings"));
							}
						}
					}
					if(listCustomerBankInfo != null){
						withdrawalModel.setListCustomerBankInfo(listCustomerBankInfo);				
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public boolean checkRdBankWire(){
		if(withdrawalModel.getRdBankwire() == null){
			withdrawalModel.setErrorMessage(getText("nts.ams.fe.label.bank_information.bankTransfer.required.register"));
			addFieldError("errorMessage", getText("nts.ams.fe.label.bank_information.bankTransfer.required.register"));
			return false;
		}
		return true;
	}
	
	public IExchangerManager getExchangerManager() {
		return exchangerManager;
	}

	public void setExchangerManager(IExchangerManager exchangerManager) {
		this.exchangerManager = exchangerManager;
	}
		
}
