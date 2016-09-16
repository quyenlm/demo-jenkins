package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsTransferMoneyDAO;
import phn.com.nts.db.dao.IAmsWithdrawalDAO;
import phn.com.nts.db.dao.IScInvestmentDAO;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashBalanceId;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsTransferMoney;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.db.entity.ScInvestment;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.common.AbstractManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CashBalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.SymbolInfo;
import phn.nts.ams.fe.jms.managers.BoManager;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.ntd.NTDManager;
import phn.nts.ams.fe.social.SCManager;
import phn.nts.ams.utils.Helper;

public class BalanceManagerImpl extends AbstractManager implements IBalanceManager {
	private static final Logit log = Logit.getInstance(BalanceManagerImpl.class);
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDao;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDao;
	private IAccountManager iAccountManager;
	private IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO;
	private IScInvestmentDAO<ScInvestment> iScInvestmentDAO;
	private BoManager boManager;

	
	public BoManager getBoManager() {
		return boManager;
	}

	public void setBoManager(BoManager boManager) {
		this.boManager = boManager;
	}

	public IScInvestmentDAO<ScInvestment> getiScInvestmentDAO() {
		return iScInvestmentDAO;
	}

	public void setiScInvestmentDAO(IScInvestmentDAO<ScInvestment> iScInvestmentDAO) {
		this.iScInvestmentDAO = iScInvestmentDAO;
	}

	/**
	 * @return the iAmsCashBalanceDAO
	 */
	public IAmsCashBalanceDAO<AmsCashBalance> getiAmsCashBalanceDAO() {
		return iAmsCashBalanceDAO;
	}

	/**
	 * @param iAmsCashBalanceDAO the iAmsCashBalanceDAO to set
	 */
	public void setiAmsCashBalanceDAO(
			IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO) {
		this.iAmsCashBalanceDAO = iAmsCashBalanceDAO;
	}
	
	public CashBalanceInfo getCashBalanceInfo(String customerId, String currencyCode, Integer serviceType) {
		CashBalanceInfo cashBalanceInfo = null;
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(currencyCode);
		amsCashBalanceId.setCustomerId(customerId);
		amsCashBalanceId.setServiceType(serviceType);		
		AmsCashBalance amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalance != null) {
			cashBalanceInfo = new CashBalanceInfo();
			BeanUtils.copyProperties(amsCashBalance, cashBalanceInfo);
			cashBalanceInfo.setCustomerId(customerId);
			cashBalanceInfo.setCurrencyCode(currencyCode);
			cashBalanceInfo.setServiceType(serviceType);
		
		}
		return cashBalanceInfo;
	}
	
	public BalanceInfo getBalanceInfo(String customerId, String customerServiceId, Integer serviceType, String currencyCode) {
		BalanceInfo balanceInfo = new BalanceInfo();;
		AmsCashBalance amsCashBalance = null;
		AmsCashBalanceId amsCashBalanceId = null;
		if(IConstants.SERVICES_TYPE.FX.equals(serviceType)) {				
			balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceId);
			//[NTS1.0-le.xuan.tuong]Nov 9, 2012A - Start Round down balance
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				int scale = currencyInfo.getCurrencyDecimal();
				int rounding = BigDecimal.ROUND_DOWN;
				BigDecimal amountAvailable = MathUtil.rounding(balanceInfo.getAmountAvailable() == null ? 0D : balanceInfo.getAmountAvailable(), scale, rounding);
				balanceInfo.setAmountAvailable(amountAvailable.doubleValue());
			}
			//[NTS1.0-le.xuan.tuong]Nov 9, 2012A - End
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(serviceType)){
				balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceId);
				
			BigDecimal totalInvestmentAmount = iScInvestmentDAO.getScTotalInvestmentAmount(customerId);
			if(totalInvestmentAmount == null){
				totalInvestmentAmount = new BigDecimal(0);
			}
			balanceInfo.setTotalInvestment(totalInvestmentAmount.doubleValue());
			
//			if(totalInvestmentAmount.doubleValue() < 0) {
//				totalInvestmentAmount = BigDecimal.ZERO;
//			}
//			Double remainAmountAfterInvestment = balanceInfo.getAmountAvailable() - totalInvestmentAmount.doubleValue();
//			if(remainAmountAfterInvestment < 0){
//				remainAmountAfterInvestment = new Double(0);
//			}
//			balanceInfo.setAmountAvailable(remainAmountAfterInvestment);
			
		} else {				
			amsCashBalanceId = new AmsCashBalanceId();
			amsCashBalanceId.setCurrencyCode(currencyCode);
			amsCashBalanceId.setCustomerId(customerId);
			amsCashBalanceId.setServiceType(serviceType);
			amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
			if(amsCashBalance != null ) {
				balanceInfo = new BalanceInfo();
				balanceInfo.setBalance(amsCashBalance.getCashBalance());		
				// get transfer withdrawal amount
				Double transferRequestAmount = getiAmsTransferMoneyDAO().sumInprogressTransactionAmount(customerId, serviceType);
				Double availableAmount = amsCashBalance.getCashBalance() - transferRequestAmount;
				balanceInfo.setAmountAvailable(availableAmount);
				balanceInfo.setRequestingAmount(transferRequestAmount);
			}									
		}
		balanceInfo.setAccountId(customerServiceId);
		balanceInfo.setCurrencyCode(currencyCode);
		return balanceInfo;
	}
	
	public BalanceInfo getBalanceInfo(String customerId, Integer serviceType, String baseCurrency) {
		BalanceInfo balanceInfo = null;
		AmsCashBalance amsCashBalance = null;
		AmsCashBalanceId amsCashBalanceId = null;
		if(IConstants.SERVICES_TYPE.FX.equals(serviceType)) {
			log.info("Service type is FX -> return null");
			return null;
		}
		if(IConstants.SERVICES_TYPE.AMS.equals(serviceType)) {
			balanceInfo = new BalanceInfo();
			// get balance of AMS
			balanceInfo.setAccountId(customerId);
			balanceInfo.setCurrencyCode(baseCurrency);
			amsCashBalance = null;
			amsCashBalanceId = new AmsCashBalanceId();
			amsCashBalanceId.setCurrencyCode(baseCurrency);
			amsCashBalanceId.setCustomerId(customerId);
			amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.AMS);
			amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
			
			if(amsCashBalance != null) {
				balanceInfo.setBalance(amsCashBalance.getCashBalance());
			} else {
				log.warn("AMS_CASHBALANCE is null with customer_id = " + customerId + ", serviceType = " + serviceType + ", baseCurrency = " + baseCurrency);
				balanceInfo.setResult(AccountBalanceResult.INVALID_ACCOUNT_ID);
				return balanceInfo;
			}
			
			// get transfer withdrawal amount
			Double transferWithdrawalAmount = getiAmsWithdrawalDAO().sumRequestTransferWithdrawal(customerId);				
			Double availableAmount = amsCashBalance.getCashBalance() - transferWithdrawalAmount;
			if(availableAmount < 0)
				availableAmount = new Double(0);
			balanceInfo.setAmountAvailable(availableAmount);
			balanceInfo.setRequestingAmount(transferWithdrawalAmount);
			balanceInfo.setResult(AccountBalanceResult.SUCCESS);
		} else {
			CustomerServicesInfo customerServiceInfo = getiAccountManager().getCustomerServiceInfo(customerId, serviceType);
			
			if(customerServiceInfo != null) {
				balanceInfo = new BalanceInfo();				
				String customerServiceId = customerServiceInfo.getCustomerServiceId();
				String currencyCode = customerServiceInfo.getCurrencyCode();

				if (!Helper.isNormalAccount(customerServiceInfo.getCustomerServiceStatus()) ) {
					log.info("customerServiceId [" + customerServiceId + "] has status [" + customerServiceInfo.getCustomerServiceStatus() + "] service have status NOT IN (open_completed, open_completed_deposited, open_completed_traded) -> return empty immediately");
					balanceInfo.setAccountId(customerId);
					balanceInfo.setCurrencyCode(currencyCode);
					balanceInfo.setServiceType(serviceType);
					balanceInfo.setResult(AccountBalanceResult.INVALID_ACCOUNT_ID);
					return balanceInfo;
				}
				
				/*if(IConstants.SERVICES_TYPE.FX.equals(serviceType)) {
					//Get FX balance
					log.info("Get balance of fx account customerServiceId : " + customerServiceInfo.getCustomerServiceId());
					
					balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceInfo.getCustomerServiceId());
					//[NTS1.0-le.xuan.tuong]Nov 9, 2012A - Start Round down balance
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + customerServiceInfo.getCurrencyCode());
					if(currencyInfo != null) {
						int scale = currencyInfo.getCurrencyDecimal();
						int rounding = BigDecimal.ROUND_DOWN;
						BigDecimal amountAvailable = MathUtil.rounding(balanceInfo.getAmountAvailable() == null ? 0D : balanceInfo.getAmountAvailable(), scale, rounding);
						balanceInfo.setAmountAvailable(amountAvailable.doubleValue());
					}
					//[NTS1.0-le.xuan.tuong]Nov 9, 2012A - End
				} else */
				if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(serviceType)){
					//Get SC balance
					log.info("Get balance of copy trade account customerServiceId : " + customerServiceInfo.getCustomerServiceId());

					boolean isEaAccount = Helper.isEaGroupName(customerServiceInfo.getGroupName());
					if (isEaAccount || !Helper.validateRequestToSC(customerId)) {
						log.info("customerServiceId: " + customerServiceInfo.getCustomerServiceId() + " is EA or not in list test, get balance from MT4, " +
								"isEASocialAccount = " + isEaAccount + ", validateRequestToSC = " + Helper.validateRequestToSC(customerId));

	 					balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceInfo.getCustomerServiceId());
						BigDecimal totalInvestmentAmount = iScInvestmentDAO.getScTotalInvestmentAmount(customerId);

						Double balance = balanceInfo.getBalance();
						Double freeMargin = balanceInfo.getFreemargin();
						Double credit = balanceInfo.getCredit();
						
						if(totalInvestmentAmount == null){
							totalInvestmentAmount = new BigDecimal(0);
						}
						balanceInfo.setTotalInvestment(totalInvestmentAmount.doubleValue());
						
						if(balance <= 0 || (freeMargin - credit - totalInvestmentAmount.doubleValue()) <= 0 || (freeMargin - credit) <= 0){
							balanceInfo.setAmountAvailable(0D);
						}else{
							balanceInfo.setAmountAvailable(Math.min(balance, Math.min(freeMargin - credit, freeMargin - credit - totalInvestmentAmount.doubleValue())));
						}
					} else {
						log.info("customerServiceId: " + customerServiceInfo.getCustomerServiceId() + " NOT is EA, get balance from SocialApi");
						balanceInfo = SCManager.getInstance().getBalanceInfo(Integer.valueOf(customerServiceInfo.getCustomerServiceId()));
					}
				} else if(IConstants.SERVICES_TYPE.BO.equals(serviceType)) {
					// Get BO balance
					log.info("Get balance of bo account customerServiceId : " + customerServiceInfo.getCustomerServiceId());
					Double transferRequestAmount = getiAmsTransferMoneyDAO().sumInprogressTransactionAmount(customerId, serviceType);
					
					BigDecimal boBalance = boManager.getBalance(customerServiceId);
					if(boBalance==null) {
						boBalance = new BigDecimal(0);
						balanceInfo.setResult(AccountBalanceResult.TIME_OUT);
					} else
						balanceInfo.setResult(AccountBalanceResult.SUCCESS);
					Double availableAmount = boBalance.doubleValue() - transferRequestAmount;
					balanceInfo.setBalance(boBalance.doubleValue());
					balanceInfo.setAmountAvailable(availableAmount);
					balanceInfo.setRequestingAmount(transferRequestAmount);
				} else  {
					//Get NTD balance
					log.info("Get balance of NTD account customerServiceId: " + customerServiceInfo.getCustomerServiceId() + ", NtdAccountId: " + customerServiceInfo.getNtdAccountId());
					BalanceInfo ntdBalanceInfo = NTDManager.getInstance().getBalanceInfo(customerServiceInfo.getNtdAccountId());
					if(ntdBalanceInfo != null)
						balanceInfo = ntdBalanceInfo;
				}
				balanceInfo.setAccountId(customerId);
				balanceInfo.setCurrencyCode(currencyCode);
				balanceInfo.setServiceType(serviceType);
			}
		}
			
		return balanceInfo;
	}

	/**
	 * @return the iAmsCustomerDao
	 */
	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDao() {
		return iAmsCustomerDao;
	}

	/**
	 * @param iAmsCustomerDao the iAmsCustomerDao to set
	 */
	public void setiAmsCustomerDao(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDao) {
		this.iAmsCustomerDao = iAmsCustomerDao;
	}

	/**
	 * @return the iAmsCustomerServiceDao
	 */
	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDao() {
		return iAmsCustomerServiceDao;
	}

	/**
	 * @param iAmsCustomerServiceDao the iAmsCustomerServiceDao to set
	 */
	public void setiAmsCustomerServiceDao(
			IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDao) {
		this.iAmsCustomerServiceDao = iAmsCustomerServiceDao;
	}

	/**
	 * @return the iAccountManager
	 */
	public IAccountManager getiAccountManager() {
		return iAccountManager;
	}

	/**
	 * @param iAccountManager the iAccountManager to set
	 */
	public void setiAccountManager(IAccountManager iAccountManager) {
		this.iAccountManager = iAccountManager;
	}

	/**
	 * @return the iAmsWithdrawalDAO
	 */
	public IAmsWithdrawalDAO<AmsWithdrawal> getiAmsWithdrawalDAO() {
		return iAmsWithdrawalDAO;
	}

	/**
	 * @param iAmsWithdrawalDAO the iAmsWithdrawalDAO to set
	 */
	public void setiAmsWithdrawalDAO(
			IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO) {
		this.iAmsWithdrawalDAO = iAmsWithdrawalDAO;
	}

	/**
	 * @return the iAmsTransferMoneyDAO
	 */
	public IAmsTransferMoneyDAO<AmsTransferMoney> getiAmsTransferMoneyDAO() {
		return iAmsTransferMoneyDAO;
	}

	/**
	 * @param iAmsTransferMoneyDAO the iAmsTransferMoneyDAO to set
	 */
	public void setiAmsTransferMoneyDAO(
			IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO) {
		this.iAmsTransferMoneyDAO = iAmsTransferMoneyDAO;
	}	
	/**
	 * 　
	 * format number 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 11, 2012
	 * @MdDate
	 */
	@SuppressWarnings("unchecked")
	public String formatNumber(BigDecimal number, String currencyCode, String language) {
		String result = "";		
		if(number != null) {
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			Map<String, String> mapSymbolCurrency = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYMBOL_CURRENCY);
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
			if(IConstants.Language.JAPANESE.equals(language)) {
				String symbol = mapSymbolCurrency.get(currencyCode);
				result = symbol + result;
			} else {
				result += " " + currencyCode;
			}
		}
		
		
		return result;
	}
	/**
	 * 　
	 * format number 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 11, 2012
	 * @MdDate
	 */
	public String formatNumber(BigDecimal number, String currencyCode) {
		String result = "";		
		if(number != null) {
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
		}
		
		
		return result;
	}
	public String getCurrencyCode(String currencyCode, String language) {
		//[NTS1.0-le.xuan.tuong]Nov 8, 2012D - Do not symbol of currency
		/*Map<String, String> mapSymbolCurrency = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYMBOL_CURRENCY);			
		if(IConstants.Language.JAPANESE.equals(language)) {
			currencyCode = mapSymbolCurrency.get(currencyCode);			
		}*/
		return currencyCode;
	}
	
	/**
	 * get Balance after converting rate
	 * 
	 * @param double amount
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 5, 2013
	 */
	public BigDecimal getBalanceWithConvertRate(Double amount, String currencyCode, String baseCurrency){
		BigDecimal balance = new BigDecimal(0);
		if(amount != null){
			balance = BigDecimal.valueOf(amount);
		}
		
		BigDecimal convertRate = getConvertRateOnFrontRate(currencyCode, baseCurrency);
		balance = balance.multiply(convertRate);
		return balance;
	}
	public RateInfo getRateInfo(String fromCurrencyCode, String toCurrencyCode) {
		RateInfo rateInfo = new RateInfo();
		BigDecimal frontRate = new BigDecimal("0");
		String symbolCd = fromCurrencyCode + toCurrencyCode;
		SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
		if(symbolInfo != null) {
			int scale = symbolInfo.getSymbolDecimal();
        	int rounding = symbolInfo.getSymbolRound();
        	frontRate = getMidRate(symbolCd, scale, rounding);
        	if(frontRate != null) {
        		rateInfo.setSymbolName(symbolCd);
        		rateInfo.setRate(frontRate);
        		rateInfo.setRateType(IConstants.RATE_TYPE.BID);
        		return rateInfo;
        	} 
        	symbolCd = toCurrencyCode + fromCurrencyCode;
        	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
        	if(symbolInfo != null) {
        		scale = symbolInfo.getSymbolDecimal();
            	rounding = symbolInfo.getSymbolRound();
            	frontRate = getMidRate(symbolCd, scale, rounding);
            	if(frontRate != null) {
            		rateInfo.setSymbolName(symbolCd);
            		rateInfo.setRate(frontRate);
            		rateInfo.setRateType(IConstants.RATE_TYPE.ASK);
            		return rateInfo;
            	}
        	}
		} else {
			symbolCd = toCurrencyCode + fromCurrencyCode;
        	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
        	if(symbolInfo != null) {
        		int scale = symbolInfo.getSymbolDecimal();
            	int rounding = symbolInfo.getSymbolRound();
            	frontRate = getMidRate(symbolCd, scale, rounding);
            	if(frontRate != null) {
            		rateInfo.setSymbolName(symbolCd);
            		rateInfo.setRate(frontRate);
            		rateInfo.setRateType(IConstants.RATE_TYPE.ASK);
            		return rateInfo;
            	}
            	symbolCd = fromCurrencyCode + toCurrencyCode;
            	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
            	if(symbolInfo != null) {
            		scale = symbolInfo.getSymbolDecimal();
                	rounding = symbolInfo.getSymbolRound();
                	frontRate = getMidRate(symbolCd, scale, rounding);
                	if(frontRate != null) {
                		rateInfo.setSymbolName(symbolCd);
                		rateInfo.setRate(frontRate);
                		rateInfo.setRateType(IConstants.RATE_TYPE.BID);
                		return rateInfo;
                	}
            	}
        	}
		}
		return rateInfo;
	}
	
}
