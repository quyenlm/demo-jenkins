package com.nts.ams.api.controller.transfer.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.WhiteLabelConfigInfo;
import phn.nts.ams.fe.domain.WithdrawalRuleInfo;
import phn.nts.ams.fe.model.WithdrawalModel;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalRequestWraper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsWithdrawalTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsWithdrawal Task
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsWithdrawalTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsWithdrawalTask.class);
	
	private AmsWithdrawalRequestWraper wraper;
	private WithdrawalModel withdrawalModel = new WithdrawalModel();
	private IAccountManager accountManager = null;
	private IBalanceManager balanceManager;
    private IIBManager ibManager;
    private IWithdrawalManager withdrawalManager;
    private IDepositManager depositManager;
    
	public AmsWithdrawalTask(AmsWithdrawalRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsWithdrawalRequest request = wraper.getRequest();
			log.info("[start] handle AmsWithdrawalRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			Result result = Result.FAILED;
			
			//Get customerInfo from DB
			CustomerInfo customerInfo = accountManager.getCustomerInfo(request.getWithdrawalInfo().getCustomerId());
			log.info("Loaded CustomerInfo: " + customerInfo);
			
			if(customerInfo != null) {
				withdrawalModel.setCurrentCusInfo(customerInfo);
				withdrawalModel.setCurrentCustomerId(customerInfo.getCustomerId());
				withdrawalModel.setCurrencyCode(customerInfo.getCurrencyCode());
				withdrawalModel.setAmount(request.getWithdrawalInfo().getWithdrawalAmount());
				withdrawalModel.setWithdrawalFee(request.getWithdrawalInfo().getWithdrawalFee());
				withdrawalModel.setFromServiceType(request.getWithdrawalInfo().getServiceType().getNumber());
				withdrawalModel.setRdBankwire(request.getWithdrawalInfo().getCustomerBankId());
				
				result = handleWithdrawal(withdrawalModel);
			} else {
				log.warn("Not found CustomerInfo, CustomerId: " + request.getWithdrawalInfo().getCustomerId());
				withdrawalModel.setErrorMessage("MSG_NAB019");
			}
			
			//Reset WithdrawalId
			AmsWithdrawalTransactionInfo withdrawalInfo = null;
			if(result == Result.SUCCESS) {
				AmsWithdrawalTransactionInfo.Builder withdrawalBuilder = request.getWithdrawalInfo().toBuilder();
				withdrawalBuilder.setWithdrawalId(withdrawalModel.getWithdrawalId());
				withdrawalInfo = withdrawalBuilder.build();
			} else
				withdrawalInfo = request.getWithdrawalInfo();
			
			//Response to client
			RpcMessage response = createRpcMessage(withdrawalInfo, result, withdrawalModel.getErrorMessage());
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response);
			
			String errCode = withdrawalModel.getErrorMessage();
			log.info("[end] handle AmsWithdrawalRequest, requestId: " + wraper.getResponseBuilder().getId() 
					+ ", ErrorMessage: " + errCode + " - " + AmsApiControllerMng.getMsg(errCode));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsWithdrawalProcessor().onComplete(wraper);
		}
	}
	
	/**
	 * Handle Withdrawal　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 16, 2015
	 * @MdDate
	 */
	public Result handleWithdrawal(WithdrawalModel withdrawalModel){
		Result result = null;
		try {
			//Load withdrawal Rule
			loadMinMaxWithdrawalAmount(withdrawalModel);
			
			//Validate WithdrawAmount & Permission
			if(!validateInputAmountAndWithdrawPermission(withdrawalModel)) {
				log.warn("Validate Input Amount And Withdraw Permission fail, ErrorCode: " + withdrawalModel.getErrorMessage() + ", ErrorMsg: " + AmsApiControllerMng.getMsg(withdrawalModel.getErrorMessage() ));
				result = Result.FAILED;
				return result;
			}
			
			//Validate rule
			withdrawalManager.loadWithdrawalRuleInfo(withdrawalModel);
			if(withdrawalModel.getWithdrawalRuleInfo().isShowTransactionWithOtherPaymentMethod()){
				readPaymentInformation(withdrawalModel);
			}
			
			//Validate WithdrawAmount vs FeeAndBalance
			if(!validateWithdrawAmountAgainstFeeAndBalance(withdrawalModel)){
				log.warn("Validate Withdraw Amount Against Fee And Balance fail, ErrorCode: " + withdrawalModel.getErrorMessage() + ", ErrorMsg: " + AmsApiControllerMng.getMsg(withdrawalModel.getErrorMessage() ));
				result = Result.FAILED;
				return result;
			}
			
			log.info("[start] withdraw Money");
			withdrawalManager.withdrawMoney(withdrawalModel);
			log.info("[end] withdraw Money");
			
			withdrawalModel.setErrorMessage("withdrawal_success");
			result = Result.SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = Result.INTERNAL_ERROR;
		}
		return result;
	}
	
	/**
	 * Load MinMax Withdrawal Amount　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	private void loadMinMaxWithdrawalAmount(WithdrawalModel withdrawalModel){
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode();
		String wlCode = withdrawalModel.getCurrentCusInfo().getWlCode();
		
		String maxWithdrawal = "";
		String minWithdrawal = "";
		
		WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_WITHDRAWAL_AMOUNT, wlCode);
		if(whiteLabelConfigInfo != null) {
			maxWithdrawal = whiteLabelConfigInfo.getConfigValue();
		}
		
		whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_WITHDRAWAL_AMOUNT, wlCode);
		if(whiteLabelConfigInfo != null) {
			minWithdrawal = whiteLabelConfigInfo.getConfigValue();
		}
		
		WithdrawalRuleInfo withdrawalRuleInfo = new WithdrawalRuleInfo();
		withdrawalRuleInfo.setMaxWithdrawAmount(MathUtil.parseBigDecimal(maxWithdrawal));
		withdrawalRuleInfo.setMinWithdrawAmount(MathUtil.parseBigDecimal(minWithdrawal));
		withdrawalModel.setWithdrawalRuleInfo(withdrawalRuleInfo);
		log.info("Loaded WithdrawalRuleInfo: " + withdrawalRuleInfo);
	}
	
	/**
	 * Read PaymentInformation　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	private void readPaymentInformation(WithdrawalModel withdrawalModel) {
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		String customerId = withdrawalModel.getCurrentCusInfo().getCustomerId();
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode();
		String wlCode = withdrawalModel.getCurrentCusInfo().getWlCode();
		String publicKey= "";
		
		AmsCustomer cusInfo = accountManager.getAmsCustomer(customerId);
		publicKey = cusInfo.getPublicKey();		
		
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
		
		log.info("Loaded PaymentInformation: " + withdrawalRuleInfo);
	}
	
	/**
	 * Validate Input Amount And Withdraw Permission
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	private boolean validateInputAmountAndWithdrawPermission(WithdrawalModel withdrawalModel){
		log.info("[start] validate Input Amount And Withdraw Permission");
		String customerId = withdrawalModel.getCurrentCusInfo().getCustomerId();
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode();
		String wlCode = withdrawalModel.getCurrentCusInfo().getWlCode();
		int serviceType = withdrawalModel.getFromServiceType();
		
		//Check AllowWithdrawalFlg & AmountAvaiable of from service
		CustomerInfo custInfo = withdrawalModel.getCurrentCusInfo();
		BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, serviceType, currencyCode);
		withdrawalModel.setBalanceAmsInfo(balanceInfo);
		
		Double amountAvaiable = withdrawalModel.getBalanceAmsInfo().getAmountAvailable();
		if(custInfo != null){
			if(custInfo.getAllowWithdrawalFlg() == null || custInfo.getAllowWithdrawalFlg().compareTo(IConstants.ALLOW_FLG.INALLOW) == 0 || amountAvaiable < 0){
				withdrawalModel.setErrorMessage("MSG_NAB021");
				log.info("Invalid amountAvaiable: " + amountAvaiable + ", AllowWithdrawalFlg: " + custInfo.getAllowWithdrawalFlg());
				return false;
			}
		}
		
		//Check CASH_BALANCE of customer for akazan flow 
		Integer countAkazan = withdrawalManager.getCountAkazan(customerId);
		if(countAkazan > 0){
			withdrawalModel.setErrorMessage("MSG_NAB021");
			log.info("Invalid CountAkazan (> 0): " + countAkazan);
			return false;
		}
		
		//Check negative cash balance
		Integer countAkazan1 = withdrawalManager.getCountAkazanNegativeBlance(customerId);
		if(countAkazan1 > 0) {
			withdrawalModel.setErrorMessage("MSG_NAB021");
			log.info("Invalid CountAkazanNegativeBlance (> 0): " + countAkazan1);
			return false;
		}
		
		if(!withdrawalManager.checkWithDrawalLimitPerDay(withdrawalModel)){
			withdrawalModel.setErrorMessage("MSG_NAF0003");
			log.info("Account reach WithDrawalLimitPerDay");
			return false;
		}
		
		//Check valid amount
		if(StringUtil.isEmpty(withdrawalModel.getAmount())){
			withdrawalModel.setErrorMessage("MSG_NAF001");
			log.info("Amount is empty");
			return false;
		}
		
		BigDecimal amount = MathUtil.parseBigDecimal(withdrawalModel.getAmount(), null);
		if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
			withdrawalModel.setErrorMessage("MSG_NAB057");
			log.info("Invalid Amount: " + withdrawalModel.getAmount());
			return false;
		}
		
		//Check min/max amount
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		BigDecimal withdrawalAmountMax = withdrawalRuleInfo.getMaxWithdrawAmount();
		BigDecimal withdrawalAmountMin = withdrawalRuleInfo.getMinWithdrawAmount();
		
		if(amount.compareTo(withdrawalAmountMax) > 0) {
			withdrawalModel.setErrorMessage("MSG_NAB026");
			log.info("Invalid Amount: " + withdrawalModel.getAmount() + " bigger maxWithdrawAmount: " + withdrawalAmountMax);
			return false;
		}
		
		
		/*if((amount.compareTo(withdrawalAmountMin) <= 0) && amountAvaiable.compareTo(withdrawalAmountMin.doubleValue()) >= 0) {//modify by HungLV fix bugs #16192
			log.warn("Invalid Amount: " + withdrawalModel.getAmount() + " smaller minWithdrawalAmount: " + withdrawalAmountMin + " have to check input amount.");
			
			if (amount.compareTo(new BigDecimal(amountAvaiable.toString())) != 0) {
				log.warn("Input amount != avaiable amount, can't withdrawal");
				withdrawalModel.setErrorMessage("MSG_NAB027");
				return false;
			}
		}*/
		
		//[TRSM1-3900-TheLN]May 17, 2016A - Start - Remove update ntd balance on AMS side when transfer
		if (new BigDecimal(amountAvaiable.toString()).compareTo(withdrawalAmountMin) <= 0 && amount.compareTo(new BigDecimal(amountAvaiable.toString())) !=0) {
			withdrawalModel.setErrorMessage("MSG_NAB027");
			log.warn("Invalid - amountAvaiable.compareTo(withdrawalAmountMin) <= 0 && amount.compareTo(amountAvaiable) !=0 ");
			return false;
	    } else if (new BigDecimal(amountAvaiable.toString()).compareTo(withdrawalAmountMin) > 0 && amount.compareTo(withdrawalAmountMin) < 0) {
	    	log.warn("Invalid - amountAvaiable.compareTo(withdrawalAmountMin) > 0 && amount.compareTo(withdrawalAmountMin) < 0 ");
	    	withdrawalModel.setErrorMessage("MSG_NAB027");
			return false;
		}
		//[TRSM1-3900-TheLN]May 17, 2016A - End
		
		Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
		String key = IConstants.WHITE_LABEL_CONFIG.WITHDRAWAL_NUMBER_PERDAY;
		Integer withdrawalAmountNumber =  MathUtil.parseInteger(mapConfiguration.get(key));
		Integer countWithdrawal = withdrawalManager.summaryOfWithDrawalPerday(customerId, IConstants.APP_DATE.FRONT_DATE);
		if(withdrawalAmountNumber.compareTo(countWithdrawal) <= 0) {
			withdrawalModel.setErrorMessage("MSG_NAB029");
			log.info("Account withdrawal " + countWithdrawal + " time todays, reach Max WithDrawalPerday: " + withdrawalAmountNumber);
			return false;
		}

		BalanceInfo balanceAmsInfo = withdrawalModel.getBalanceAmsInfo();
		amount = MathUtil.parseBigDecimal(withdrawalModel.getAmount(), null);
		BigDecimal availableAmount = MathUtil.parseBigDecimal(0);
		
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		
		try {
			if(balanceAmsInfo != null) {
				if(currencyInfo != null) {
					availableAmount = MathUtil.rounding(balanceAmsInfo.getAmountAvailable(), scale, rounding);
				} else {
					availableAmount = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
				}
			} else {
				withdrawalModel.setErrorMessage("MSG_NAB022");
				log.info("Invalid AvailableAmount: " + availableAmount);
				return false;						
			}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		log.info("Start compare withdrawalAmount = " + amount.doubleValue() + " with AvailableAmount = " + availableAmount.doubleValue());
		if(amount.compareTo(availableAmount) > 0){
			withdrawalModel.setErrorMessage("MSG_NAB022");
			log.info("withdrawalAmount = " + amount.doubleValue() + " larger AvailableAmount = " + availableAmount.doubleValue());
			return false;
		}
		
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		withdrawalModel.setAmount(StringUtil.toString(amount));
		
		log.info("[end] validate Input Amount And Withdraw Permission");
		return true;
	}
	
	/**
	 * Validate Withdraw Amount Against Fee And Balance　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	private boolean validateWithdrawAmountAgainstFeeAndBalance(WithdrawalModel withdrawalModel){
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode(); 
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		
		if(withdrawalRuleInfo.isShowTransactionWithCc()){
			BigDecimal withdrawAmount = MathUtil.parseBigDecimal(withdrawalModel.getAmount());
			BigDecimal withdrawalCcFee = withdrawalRuleInfo.getWithdrawFeeCc();
			
			if(withdrawAmount.compareTo(withdrawalCcFee) <= 0){
				withdrawalModel.setErrorMessage("MSG_NAB101");
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
				withdrawalModel.setErrorMessage("MSG_NAB101");
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
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							}
						} else {						
							// if customer don't input email account
							withdrawalModel.setErrorMessage("MSG_NAF001");
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
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else {
								if(!StringUtil.isEmail(payzaInfo.getEmailAddress())) {
									withdrawalModel.setErrorMessage("NAB007");
									return false;
								}
							}
						} else {						
							// if customer don't input email account
							withdrawalModel.setErrorMessage("MSG_NAF001");
							return false;						
						}
					}
				}
			} else if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == withdrawMethod){
				if(rdBankwire == null){
					// later
				}else{
					if(IConstants.DEPOSIT_CHOOSE_RADIO.BANKWIRE.equals(rdBankwire)){
						withdrawalModel.setErrorMessage("MSG_NAF001");
						return false;
					}
				}
			} else if(IConstants.PAYMENT_METHOD.EXCHANGER == withdrawMethod){
				ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
				if(rdExchanger == null){
					// later
				} else {
					if(StringUtil.isEmpty(exchangerInfo.getExchangerId())){
						withdrawalModel.setErrorMessage("MSG_NAF001");
						return false;
					}
					
					if(IConstants.DEPOSIT_CHOOSE_RADIO.EXCHANGER.equals(rdExchanger)){
						
						if (exchangerInfo != null) {
							if (StringUtils.isEmpty(exchangerInfo.getExchangerId())) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else if (StringUtils.isBlank(exchangerInfo.getBankName())) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getBranchName())) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getAccountNo())) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else if(StringUtils.isBlank(exchangerInfo.getAccountName())) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} else if(exchangerInfo.getCountryId() == -1) {
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							} 
						}
					}
				}
			} else if(IConstants.PAYMENT_METHOD.LIBERTY == withdrawMethod) {
				if(rdLiberty == null) {
					//  do late
					withdrawalModel.setErrorMessage("NAB001");
					return false;
				} else {
					if(IConstants.DEPOSIT_CHOOSE_RADIO.LIBERTY.equals(rdLiberty)) {
						LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
						if(libertyInfo != null) {
							if(libertyInfo.getAccountNumber() == null || StringUtils.isBlank(libertyInfo.getAccountNumber())) {
								// if customer don't input email account
								withdrawalModel.setErrorMessage("MSG_NAF001");
								return false;
							}
						} else {						
							// if customer don't input email account
							withdrawalModel.setErrorMessage("MSG_NAF001");
							return false;
							
						}
					}
				}
			}
		}
		return true;
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
	public RpcMessage createRpcMessage(AmsWithdrawalTransactionInfo withdrawalInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDAWAL_RESPONSE);
		
		if(result == Result.SUCCESS && withdrawalInfo != null) {
			AmsWithdrawalResponse.Builder customerInfoResponse = AmsWithdrawalResponse.newBuilder();
			customerInfoResponse.setWithdrawalInfo(withdrawalInfo);
			response.setPayloadData(customerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
			response.setMessageCode(errCode);
		} else {
			response.setResult(result);
			response.setMessageCode(errCode);
		}
		
		return response.build();
	}
	
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public WithdrawalModel getWithdrawalModel() {
		return withdrawalModel;
	}

	public void setWithdrawalModel(WithdrawalModel withdrawalModel) {
		this.withdrawalModel = withdrawalModel;
	}

	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	public IIBManager getIbManager() {
		return ibManager;
	}

	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}

	public IDepositManager getDepositManager() {
		return depositManager;
	}

	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}
}