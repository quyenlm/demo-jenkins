
package phn.nts.ams.fe.web.action.account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.IMessageManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.WhiteLabelConfigInfo;
import phn.nts.ams.fe.model.AccountAjaxModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.security.RoseIndiaCaptcha;

import com.nts.common.exchange.bean.RateBandInfo;
import com.nts.common.exchange.dealing.FxFrontRateInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @description Account Ajax Action
 * @version TDSBO1.0
 * @CrBy QuyTM
 * @CrDate Aug 4, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class AccountAJAXAction extends ActionSupport implements ModelDriven<AccountAjaxModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(AccountAJAXAction.class);
	AccountAjaxModel model = new AccountAjaxModel();	
	IAccountManager accountManager;
	IBalanceManager balanceManager;
	IIBManager ibManager;
	IMessageManager iMessageManager;
	public AccountAjaxModel getModel() {
		return model;
	}
	public String executeGetCustomerServicesInfo() {
		CustomerServicesInfo customerServicesInfo = accountManager.getCustomerServiceInfo(model.getCustomerServicesId());
		model.setCustomerServicesInfo(customerServicesInfo);
		return SUCCESS;
		
	}
	/**
	 * @param iMessageManager the iMessageManager to set
	 */
	public void setiMessageManager(IMessageManager iMessageManager) {
		this.iMessageManager = iMessageManager;
	}
	
	/**
	 * executeGetCustomerInfo　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quy.To.Minh
	 * @CrDate Nov 29, 2012
	 */
	public String GetCustomerInfo() {
		try{
			String customerId = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {
					customerId = frontUserOnline.getUserId();
				}
			}
			String clientCustomerId = model.getCustomerId() == null ? "" : model.getCustomerId().trim();
			if(customerId != null && clientCustomerId != null && !StringUtils.isBlank(clientCustomerId)) {
				CustomerInfo customerInfo = accountManager.getCustomerInfo(customerId, clientCustomerId);
				if(customerInfo == null) {
					model.setErrorMsg(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
				} else {
					model.setCustomerInfo(customerInfo);
					Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + frontUserOnline.getWlCode());
					String maxDeposit  = "";
					String minDeposit  = "";		
					String maxWithdrawal = "";
					String minWithdrawal = "";
					String key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT;
					maxDeposit = mapConfiguration.get(key);
					maxDeposit = model.formatNumber(MathUtil.parseBigDecimal(maxDeposit), customerInfo.getCurrencyCode());

					key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT;
					minDeposit = mapConfiguration.get(key);	
					minDeposit = model.formatNumber(MathUtil.parseBigDecimal(minDeposit), customerInfo.getCurrencyCode());
					
					// remove read cache for wl config
									
					key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_WITHDRAWAL_AMOUNT;
					WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
					if(whiteLabelConfigInfo != null) {
						maxWithdrawal = whiteLabelConfigInfo.getConfigValue();
						maxWithdrawal = model.formatNumber(MathUtil.parseBigDecimal(maxWithdrawal), customerInfo.getCurrencyCode());
					}
					
//					maxWithdrawal = mapConfiguration.get(key);	
//					maxWithdrawal = model.formatNumber(MathUtil.parseBigDecimal(maxWithdrawal), customerInfo.getCurrencyCode());
					
					key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_WITHDRAWAL_AMOUNT;
					whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
					if(whiteLabelConfigInfo != null) {
						minWithdrawal = whiteLabelConfigInfo.getConfigValue();
						minWithdrawal = model.formatNumber(MathUtil.parseBigDecimal(minWithdrawal), customerInfo.getCurrencyCode());
					}
					
//					minWithdrawal = mapConfiguration.get(key);	
//					minWithdrawal = model.formatNumber(MathUtil.parseBigDecimal(minWithdrawal), customerInfo.getCurrencyCode());
					model.setMaxAmount(maxDeposit);
					model.setMinAmount(minDeposit);
					model.setMaxWithdrawal(maxWithdrawal);
					model.setMinWithdrawal(minWithdrawal);
					
					
				}			
			} else {
				model.setErrorMsg(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.blank.customerId", clientCustomerId));
			}
		} catch (Exception ex){
			logger.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	public String GetServiceType() {
		String customerId = "";
		String maxDeposit  = "";
		String minDeposit  = "";			
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {
					customerId = frontUserOnline.getUserId();
					model.setBaseCurrencyCode(frontUserOnline.getCurrencyCode());
				}
			}
			Integer serviceTypeId = MathUtil.parseInteger(model.getServiceTypeId());
			if(IConstants.SERVICES_TYPE.AMS.equals(serviceTypeId)) {
				model.setCurrencyCode(frontUserOnline.getCurrencyCode());
			} else {
				CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, serviceTypeId);
				if(customerServiceInfo != null) {					
					model.setCurrencyCode(customerServiceInfo.getCurrencyCode());
				} else {
					model.setCurrencyCode(frontUserOnline.getCurrencyCode());
				}
			}
			Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + frontUserOnline.getWlCode());
			
			String key = model.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT;
			maxDeposit = mapConfiguration.get(key);
			maxDeposit = model.formatNumber(MathUtil.parseBigDecimal(maxDeposit), model.getCurrencyCode());
//			WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());
//			if(whiteLabelConfigInfo != null) {
//				maxDeposit = whiteLabelConfigInfo.getConfigValue();					
//			}
			key = model.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT;
			minDeposit = mapConfiguration.get(key);	
			minDeposit = model.formatNumber(MathUtil.parseBigDecimal(minDeposit), model.getCurrencyCode());
//			whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, frontUserOnline.getWlCode());						
//			if(whiteLabelConfigInfo != null) {
//				minDeposit = whiteLabelConfigInfo.getConfigValue();					
//			}			
			
			System.out.println("MIN: " + minDeposit + "MAX: " + maxDeposit);
			model.setMaxAmount(maxDeposit);
			model.setMinAmount(minDeposit);
			// get balance of customer by service Type
			BalanceInfo balanceInfo = null;
			balanceInfo = balanceManager.getBalanceInfo(customerId, serviceTypeId, model.getBaseCurrencyCode());
			if(balanceInfo != null) {
				model.setBalance(model.formatNumber(MathUtil.parseBigDecimal(balanceInfo.getBalance()), balanceInfo.getCurrencyCode()));
				model.setAmountAvailable(model.formatNumber(MathUtil.parseBigDecimal(balanceInfo.getAmountAvailable()), balanceInfo.getCurrencyCode()));
			}
			model.setBalanceInfo(balanceInfo);	
			
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		return SUCCESS;
	}
	public String GetRate() {
		String counterCurrencyCode = model.getCounterCurrencyCode();
		String baseCurrencyCode = model.getBaseCurrencyCode();
//		Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
//		RateInfo rateInfo = MT4Manager.getInstance().getRate(counterCurrencyCode, baseCurrencyCode, mapConfig);
		RateInfo rateInfo = balanceManager.getRateInfo(counterCurrencyCode, baseCurrencyCode);
		model.setRateInfo(rateInfo);
		return SUCCESS;
	}
	/**
	 * @param accountManager the accountManager to set
	 */
	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	/**
	 * @param ibManager the ibManager to set
	 */
	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 31, 2012
	 * @MdDate
	 */
	public String GetFromServiceTypeToServiceType() {
		String customerId = "";
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {
					customerId = frontUserOnline.getUserId();
					model.setCurrencyCode(frontUserOnline.getCurrencyCode());
					model.setAccountCurrencyCode(frontUserOnline.getCurrencyCode());
				}
			}
			Integer serviceTypeId = MathUtil.parseInteger(model.getServiceTypeId());
			Integer accountServiceTypeId = MathUtil.parseInteger(model.getAccountServiceTypeId());
			List<CustomerServicesInfo> listCustomerServiceInfo = frontUserOnline.getListCustomerServiceInfo();
			if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
				for(CustomerServicesInfo customerServiceInfo : listCustomerServiceInfo) {
					if(customerServiceInfo.getServiceType().equals(serviceTypeId)) {
						model.setCurrencyCode(customerServiceInfo.getCurrencyCode());						
					}
					if(customerServiceInfo.getServiceType().equals(accountServiceTypeId)) {
						model.setAccountCurrencyCode(customerServiceInfo.getCurrencyCode());
					}
				}
			}
//			if(model.getCurrencyCode() == null) {
//				
//			}
//			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, serviceTypeId);
//			if(customerServiceInfo != null) {
//				model.setCurrencyCode(customerServiceInfo.getCurrencyCode());
//			} else {
//				model.setCurrencyCode(frontUserOnline.getCurrencyCode());
//			}
//			customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, accountServiceTypeId);
//			if(customerServiceInfo != null) {
//				model.setAccountCurrencyCode(customerServiceInfo.getCurrencyCode());
//			} else {
//				model.setAccountCurrencyCode(frontUserOnline.getCurrencyCode());
//			}		
			
			String currencyCode = model.getCurrencyCode();
			String accountCurrencyCode = model.getAccountCurrencyCode();
			
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 1, 2012A - Start			
			CurrencyInfo currencyCodeInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyCodeInfo != null) {				
				model.setDecimalFormatCurrencyCode(currencyCodeInfo.getCurrencyDecimal());
			}			
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 1, 2012A - End
			Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
			/*RateInfo rateInfo = MT4Manager.getInstance().getRate(accountCurrencyCode, currencyCode, mapConfig);*/
			
			//[SocialTrading] - [start] get mid rate from memcache
			RateInfo rateInfo = new RateInfo();
			
			rateInfo.setRateType(IConstants.RATE_TYPE.MID);
			String symbol = currencyCode + accountCurrencyCode;
			rateInfo.setSymbolName(symbol);
			if(currencyCode.equalsIgnoreCase(accountCurrencyCode)){
				rateInfo.setRate(MathUtil.parseBigDecimal(1));
				model.setRateInfo(rateInfo);		
				//[NTS1.0-Quan.Le.Minh]Jan 14, 2013A - Start 
				model.setDateTime(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_MMDDYYYY));
				//[NTS1.0-Quan.Le.Minh]Jan 14, 2013A - End
			}else{
				BigDecimal converRate = balanceManager.getConvertRateOnFrontRate(currencyCode, accountCurrencyCode, 5); // default 5
				if(converRate != null){
					rateInfo.setRate(converRate);
					
					model.setRateInfo(rateInfo);		
					//[NTS1.0-Quan.Le.Minh]Jan 14, 2013A - Start 
					model.setDateTime(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_MMDDYYYY));
					//[NTS1.0-Quan.Le.Minh]Jan 14, 2013A - End
				}else{
					model.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB066"));
				}
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			model.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB066"));
		}
		
		return SUCCESS;
	}

	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}
	
	public String SetReadFlag() {
		String customerId = "";
		Integer messageId = MathUtil.parseInteger(model.getMessageId());
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = null;
		if(frontUserDetails != null) {
			frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if (frontUserOnline!= null) {
				customerId = frontUserOnline.getUserId();
			}
		}
		// get message detail
		iMessageManager.insertAmsMessageReadTrace(customerId, messageId);
		
		return SUCCESS;
	}
	//public String executeDeleteMessage() {
	public String DeleteMessage() {
		String customerId = "";
		Integer messageId = MathUtil.parseInteger(model.getMessageId());
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = null;
		if(frontUserDetails != null) {
			frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if (frontUserOnline!= null) {
				customerId = frontUserOnline.getUserId();
			}
		}
		Boolean result = iMessageManager.deleteMessage(customerId, messageId);
		if(result) {
			// if successful
			logger.info("Delete message " + messageId + " successful");
			model.setErrorMsg(IConstants.MESSAGE_MSG_CODE.MSG_UPDATE_SUCCESS);
		} else {
			model.setErrorMsg(IConstants.MESSAGE_MSG_CODE.MSG_CANNOT_UPDATE);
		}
		return SUCCESS;
	}
	//public String executeCheckCaptchaLogin() throws Exception {
	public String CheckCaptchaLogin() throws Exception {
		try {	
			HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(org.apache.struts2.StrutsStatics.HTTP_REQUEST);
			javax.servlet.http.HttpSession session = request.getSession();			
			String capchaCode = (String) session.getAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);			
			String captchaResponse = getModel().getCaptchaResponse();			
			model.setErrorMessage(null);
			if (captchaResponse != null && !captchaResponse.equals(capchaCode)) {
				String strError = getText("nts.ams.fe.label.account.invalid.verification.code");
				model.setErrorMessage(strError);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return SUCCESS;
	}	
//	public String executeGetUserPortfolio() {
	public String GetUserPortfolio() {				
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {
					String customerId = frontUserOnline.getUserId();
					String currency = frontUserOnline.getCurrencyCode();
					Double total = new Double(0);
					
					// get balance of AMS
					BalanceInfo balanceAmsInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currency);
					// get balance of FX
					BalanceInfo balanceFxInfo =  getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currency);
					// get balance of BO 
					BalanceInfo balanceBoInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currency);
					// get balance of Copy_trade
					BalanceInfo balanceScInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currency);
					
					if(balanceAmsInfo != null){
						if(balanceBoInfo.getBalance() != null){
							BigDecimal balance = getBalanceManager().getBalanceWithConvertRate(balanceBoInfo.getBalance(),balanceBoInfo.getCurrencyCode(),currency);
							Double boBalance = Double.parseDouble(balance.toString());
							total = total + boBalance;
						}
						model.setBalanceAmsInfo(balanceAmsInfo);
					}else 
						model.setBalanceAmsInfo( new BalanceInfo());
					
					if(balanceBoInfo != null){
						model.setBalanceBoInfo(balanceBoInfo);
						if(balanceBoInfo.getBalance() != null){
							total = total + balanceBoInfo.getBalance() ;
						}
					}else 
						model.setBalanceBoInfo( new BalanceInfo());
					
					if(balanceFxInfo != null){
						model.setBalanceFxInfo(balanceFxInfo);
						if(balanceFxInfo.getBalance() != null){
							total = total + balanceFxInfo.getBalance() ;
						}
					}else 
						model.setBalanceFxInfo( new BalanceInfo());
					
					if(balanceScInfo != null){
						model.setBalanceScInfo(balanceScInfo);
						if(balanceScInfo.getBalance() != null){
							total = total + balanceScInfo.getBalance() ;
						}
					}else 
						model.setBalanceScInfo( new BalanceInfo());
					
					model.setTotal(total);
					model.setCurrencyCode(currency);
				}
			}
		}catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	
		return SUCCESS;
	}
	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}
}