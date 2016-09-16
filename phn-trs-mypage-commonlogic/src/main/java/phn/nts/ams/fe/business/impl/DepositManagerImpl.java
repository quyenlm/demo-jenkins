package phn.nts.ams.fe.business.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCashflowDAO;
import phn.com.nts.db.dao.IAmsCustomerCreditcardDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerEwalletDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsDepositDAO;
import phn.com.nts.db.dao.IAmsDepositRefDAO;
import phn.com.nts.db.dao.IAmsErrorCodeDAO;
import phn.com.nts.db.dao.IAmsExchangerDAO;
import phn.com.nts.db.dao.IAmsExchangerSymbolDAO;
import phn.com.nts.db.dao.IAmsPaymentgwDAO;
import phn.com.nts.db.dao.IAmsPromotionDAO;
import phn.com.nts.db.dao.IAmsSysCountryDAO;
import phn.com.nts.db.dao.IAmsTransferMoneyDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWhitelabelDAO;
import phn.com.nts.db.dao.IFxSummaryRateDAO;
import phn.com.nts.db.dao.IFxSymbolDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashBalanceId;
import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerCreditcard;
import phn.com.nts.db.entity.AmsCustomerEwallet;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.db.entity.AmsErrorCode;
import phn.com.nts.db.entity.AmsExchanger;
import phn.com.nts.db.entity.AmsExchangerSymbol;
import phn.com.nts.db.entity.AmsGroup;
import phn.com.nts.db.entity.AmsPaymentgw;
import phn.com.nts.db.entity.AmsPromotion;
import phn.com.nts.db.entity.AmsPromotionCustomer;
import phn.com.nts.db.entity.AmsSubGroup;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsTransferMoney;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.FxSummaryRate;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.libertyreserve.api.ApiAgent.HistoryItem;
import phn.com.nts.libertyreserve.api.LibertyTransferUtil;
import phn.com.nts.netpay.NetPay;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Cryptography;
import phn.com.nts.util.security.Security;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.common.AbstractManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BjpDepositInfo;
import phn.nts.ams.fe.domain.CountryInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.DepositInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.NetellerResponseInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.PayzaResponseInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.SymbolInfo;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.domain.converter.ExchangerSymbolConverter;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.payment.LibertyContext;
import phn.nts.ams.fe.payment.NetellerContext;
import phn.nts.ams.fe.payment.PayonlineSystemContext;
import phn.nts.ams.fe.payment.PayzaContext;
import phn.nts.ams.fe.promotion.IPromotionManager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.MailService;

import com.nts.common.exchange.bean.BalanceUpdateInfo;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.phn.mt.common.entity.FundRecord;

/**
 * @description
 * @version NTS1.0
 * @CrBy HuyenMT
 * @CrDate Jul 24, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DepositManagerImpl extends AbstractManager implements IDepositManager{
	private static final Logit log = Logit.getInstance(DepositManagerImpl.class);
	private final String BANK_TRANSFER_COMPLETED = "banktranser_completed";
	private final String ERROR 	= "error";	
	public static final String SUCCESS = "success";
	private IAmsDepositDAO<AmsDeposit> iAmsDepositDAO ;
	private IAmsDepositRefDAO<AmsDepositRef> iAmsDepositRefDAO ;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private IAmsCashflowDAO<AmsCashflow> iAmsCashFlowDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IAmsWhitelabelDAO<AmsWhitelabel> iAmsWhitelabelDAO;
	private IAmsCustomerEwalletDAO<AmsCustomerEwallet> iAmsCustomerEwalletDAO;
	private IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> iAmsCustomerCreditCardDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO;
	private IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO;
	private MailService mailService;
	private IFxSymbolDAO<FxSymbol> iFxSymbolDAO;
	private IPromotionManager iPromotionManager;
	private IAmsPromotionDAO<AmsPromotion> amsPromotionDAO;
	private IAmsExchangerDAO<AmsExchanger> amsExchangerDAO;
	
	private IFxSummaryRateDAO<FxSummaryRate> summaryRateDAO;
	
	private IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO;
	private IBalanceManager balanceManager;
	
	private IAccountManager accountManager;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
	private IAmsPaymentgwDAO<AmsPaymentgw> amsPaymentGwDAO;
	private IAmsErrorCodeDAO<AmsErrorCode> amsErrorCodeDAO;
	private IJmsContextSender jmsContextSender;
	
//	private DepositModel depositModel = new DepositModel();
    private IDepositManager depositManager = null;
    private IExchangerManager exchangerManager = null;
    private String msgCode;
    private String rawUrl;
    private String result;
    
	public String depositBankTransfer(DepositInfo depositInfo) {
		String result = BANK_TRANSFER_COMPLETED;
		log.info("[Start] log information of deposit bank transfer");
		try {
			//save deposit
			AmsDeposit amsDeposit = new AmsDeposit();		
			String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
			if(depositInfo != null){
				log.info("deposit amount = " + depositInfo.getAmount());
				log.info("deposit method = " + depositInfo.getMethod());
				log.info("deposit code   = " + depositInfo.getCurrencyCode());

				SysAppDate amsAppDate = null;
				List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
				if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
					amsAppDate = listAmsAppDate.get(0);
				}

				BigDecimal amount = MathUtil.parseBigDecimal(depositInfo.getAmount());
				Integer scale = new Integer(0);
				Integer rounding = new Integer(0);
				CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + depositInfo.getCurrencyCode());
				if(currencyInfo != null) {
					scale = currencyInfo.getCurrencyDecimal();
					rounding = currencyInfo.getCurrencyRound();
				}
				amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
				amsDeposit.setDepositId(depositId);
				AmsCustomer amsCustomer = new AmsCustomer(); 
				amsCustomer.setCustomerId(depositInfo.getCustomerId());	
				amsDeposit.setAmsCustomer(amsCustomer);
				amsDeposit.setServiceType(depositInfo.getServiceType());
				amsDeposit.setRemark(depositInfo.getRemark());
				amsDeposit.setDepositAmount(amount.doubleValue());			
				amsDeposit.setDepositMethod(Integer.valueOf(depositInfo.getMethod()));
				amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.REQUESTING);
				amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
				amsDeposit.setCurrencyCode(depositInfo.getCurrencyCode());		
				if(amsAppDate != null) {
					amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
				} 
				amsDeposit.setDepositAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
//				amsDeposit.setConfirmDate(new java.sql.Timestamp(System.currentTimeMillis()));
				//amsDeposit.setDepositCompletedDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDeposit.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDeposit.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsDeposit.setDepositRoute(depositInfo.getDepositRoute());
				amsDeposit.setRegCustomerId(depositInfo.getRegCustomerId());
				AmsDepositRef amsDepositRef = new AmsDepositRef();
				amsDepositRef.setDepositId(depositId);
				amsDepositRef.setAmsDeposit(amsDeposit);				
				amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsDepositRef.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDepositRef.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
				
				amsDeposit.setAmsDepositRef(amsDepositRef);
				getiAmsDepositDAO().save(amsDeposit);		
				getiAmsDepositRefDAO().save(amsDepositRef);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = ERROR;
		}
		return result;
	}
	
	private BigDecimal convertRate(BankTransferInfo bankTransferInfo, String customerId) {
		BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
//		Integer symbolRound = new Integer(0);
//		Integer symbolDecimal = new Integer(0);				
		String currencyCodeTargetServiceType = "";
		AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, bankTransferInfo.getServiceType());
		if(toAmsCustomerService != null) {				
			// get subgroup of customer services for get currency code
			AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
			if(amsSubGroup != null) {
				currencyCodeTargetServiceType = amsSubGroup.getCurrencyCode();
				if(bankTransferInfo.getBaseCurrency().equals(currencyCodeTargetServiceType)) {
					convertRate = MathUtil.parseBigDecimal(1);
				} else {
					// set symbolName = fromCurrency + toCurrency
//					String symbolName = amsSubGroup.getCurrencyCode() + bankTransferInfo.getBaseCurrency();
//					Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//					RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//					if(rateInfo != null) {
//						// get symbol rounding from table AMS_SYS_SYMBOL
//						FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//						if(amsSysSymbol != null) {
//							symbolRound = amsSysSymbol.getSymbolRound();
//							symbolDecimal = amsSysSymbol.getSymbolDecimal();
//						}
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//						convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);							
//					} else {	
//						symbolName = bankTransferInfo.getBaseCurrency() + amsSubGroup.getCurrencyCode();
//						rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);								
//						} else {
//							log.warn("Cannot find rate of symbolName " + symbolName);
////							return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//							return null;
//						}
//					}
					
					RateInfo rateInfo = getRateInfo(amsSubGroup.getCurrencyCode(), bankTransferInfo.getBaseCurrency());
					if(rateInfo == null) {
						return null;
					}
					convertRate = rateInfo.getRate();
				}
			}
		}

		return convertRate;
	}

	/**
	 * depositBankTransfer　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	public Integer depositBankTransfer(BankTransferInfo bankTransferInfo) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		log.info("[Start] log information of deposit bank transfer");
		try {
			// get convert rate from AMS and target service type			
			String customerId = bankTransferInfo.getCustomerId();	
//			
			BigDecimal convertRate = convertRate(bankTransferInfo, customerId);
			if (convertRate == null) {
				return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
			}

			//save deposit
			result = saveDeposit(bankTransferInfo, convertRate);

			if (result == null) {
				return IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		}

		return result;
	}

	/**
	 * saveDeposit　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private Integer saveDeposit(BankTransferInfo bankTransferInfo, BigDecimal convertRate) {
		AmsDeposit amsDeposit = new AmsDeposit();		
		String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
		Integer result = null;
		if(bankTransferInfo != null){
			log.info("deposit amount = " + bankTransferInfo.getAmount());
			log.info("deposit method = " + bankTransferInfo.getPaymentMethod());
			log.info("deposit code   = " + bankTransferInfo.getCurrencyCode());

			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}				
			amsDeposit.setDepositId(depositId);
			BigDecimal amount = bankTransferInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + bankTransferInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			AmsCustomer amsCustomer = new AmsCustomer(); 
			amsCustomer.setCustomerId(bankTransferInfo.getCustomerId());	
			amsDeposit.setAmsCustomer(amsCustomer);
			amsDeposit.setServiceType(bankTransferInfo.getServiceType());				
			amsDeposit.setDepositAmount(amount.doubleValue());			
			amsDeposit.setDepositMethod(Integer.valueOf(bankTransferInfo.getPaymentMethod()));
			amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.REQUESTING);
			amsDeposit.setRate(convertRate.doubleValue());
			amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
			amsDeposit.setCurrencyCode(bankTransferInfo.getCurrencyCode());		
			if(amsAppDate != null) {
				amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsDeposit.setDepositAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
//			amsDeposit.setConfirmDate(null); //HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
			//amsDeposit.setDepositCompletedDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsDeposit.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsDeposit.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis())); 
			amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsDeposit.setDepositRoute(bankTransferInfo.getDeviceType());
			amsDeposit.setRegCustomerId(bankTransferInfo.getRegCustomerId());
			AmsDepositRef amsDepositRef = new AmsDepositRef();
			amsDepositRef.setDepositId(depositId);
			amsDepositRef.setAmsDeposit(amsDeposit);				
			amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsDepositRef.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsDepositRef.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsDepositRef.setAmsDeposit(amsDeposit);
			amsDeposit.setAmsDepositRef(amsDepositRef);
			getiAmsDepositDAO().save(amsDeposit);	
			getiAmsDepositRefDAO().save(amsDepositRef); //HuyenMT save ams deposit ref for Bug #5549 	Bug-0.2.1(AMSBO)
			result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
		}

		return result;
	}

	/**
	 * Deposit with method is exchanger　
	 * 
	 * @param exchangerInfo
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 25, 2012
	 */
	public Integer depositExchanger(ExchangerInfo exchangerInfo) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		log.info("[Start] log information of deposit bank transfer");
		try {
			String customerId = exchangerInfo.getCustomerId();
			BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
						
			//save deposit
			AmsDeposit amsDeposit = new AmsDeposit();		
			String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
			if(exchangerInfo != null){
				log.info("deposit amount = " + exchangerInfo.getAmount());
				log.info("deposit method = " + exchangerInfo.getPaymentMethod());
				log.info("deposit code   = " + exchangerInfo.getCurrencyCode());

				SysAppDate amsAppDate = null;
				List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
				if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
					amsAppDate = listAmsAppDate.get(0);
				}
				
				BigDecimal amount =  exchangerInfo.getAmount();
				Integer scale = new Integer(0);
				Integer rounding = new Integer(0);
				CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + exchangerInfo.getCurrencyCode());
				if(currencyInfo != null) {
					scale = currencyInfo.getCurrencyDecimal();
					rounding = currencyInfo.getCurrencyRound();
				}
				amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
				
				amsDeposit.setDepositId(depositId);
//				AmsCustomer amsCustomer = new AmsCustomer(); 
//				amsCustomer.setCustomerId(customerId);	
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				amsDeposit.setAmsCustomer(amsCustomer);
				amsDeposit.setServiceType(IConstants.SERVICES_TYPE.AMS);				
				amsDeposit.setDepositAmount(amount.doubleValue());			
				amsDeposit.setDepositMethod(exchangerInfo.getPaymentMethod());
				amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.REQUESTING);
				amsDeposit.setRate(convertRate.doubleValue());
				amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
				amsDeposit.setCurrencyCode(exchangerInfo.getCurrencyCode());		
				if(amsAppDate != null) {
					amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
				} 
				amsDeposit.setDepositAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDeposit.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDeposit.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis())); 
				amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsDeposit.setDepositRoute(exchangerInfo.getDeviceType());
//				amsDeposit.setRegCustomerId(exchangerInfo.getRegCustomerId());
				AmsDepositRef amsDepositRef = new AmsDepositRef();
				amsDepositRef.setDepositId(depositId);
				amsDepositRef.setAmsDeposit(amsDeposit);				
				amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsDepositRef.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDepositRef.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsDepositRef.setAmsDeposit(amsDeposit);
				amsDeposit.setAmsDepositRef(amsDepositRef);
				
				// Set Exchanger ID
				amsDepositRef.setExchangerId(exchangerInfo.getExchangerId());
				// Set rate sell
				ExchangerSymbolInfo exchangerSymbolInfo = getExchangerSymbol(exchangerInfo.getExchangerId());
				if(exchangerInfo != null){
					amsDepositRef.setRate(MathUtil.parseDouble(exchangerSymbolInfo.getSellRate()));
				}
				getiAmsDepositDAO().save(amsDeposit);	
				getiAmsDepositRefDAO().save(amsDepositRef); 
				result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
				
				// send mail exchanger
				sendMailExchanger(amsDeposit);
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		}
		return result;
	}
	
	/**
	 * send mail exchanger
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 27, 2012
	 */
	private void sendMailExchanger(AmsDeposit deposit) {
		log.info("[start] send mail exchanger");
		AmsCustomer amsCustomer = deposit.getAmsCustomer();
		String language = StringUtil.isEmpty(amsCustomer.getDisplayLanguage()) ? IConstants.Language.ENGLISH : amsCustomer.getDisplayLanguage();
		
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_EXCHANGER + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_EXCHANGER).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(String.valueOf(deposit.getDepositAmount()));
		SysCurrency sysCurrency = amsCustomer.getSysCurrency();
		amsMailTemplateInfo.setDepositCurrency(sysCurrency == null ? "" : sysCurrency.getCurrencyCode());
		amsMailTemplateInfo.setDepositId(deposit.getDepositId());
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositMethod(SystemPropertyConfig.getInstance().getText("deposit_transfer_method_exchanger"));
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		Date date = DateUtil.toDate(deposit.getDepositAcceptDate(), DateUtil.PATTERN_YYYYMMDD_BLANK);
		String acceptDate = DateUtil.toString(date, DateUtil.PATTERN_YYMMDD);
		amsMailTemplateInfo.setDepositDate(acceptDate);

		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																												
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail exchanger");
	}

	public List<AmsDeposit> getListAmsDeposit(){
		List<AmsDeposit> listAmsDeposit = new ArrayList<AmsDeposit>();
		listAmsDeposit = getiAmsDepositDAO().findAll();
		return listAmsDeposit;
	}

	private synchronized String generateUniqueId(String contextID) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}		
		String uniqueId = getiSysUniqueidCounterDAO().generateUniqueId(contextID);		
		return uniqueId;
	}

	@Override
	public List<AmsDeposit> getListDepositHistory(String customerId,PagingInfo pagingInfo) {
		List<AmsDeposit> listAmsDeposits = null;
		try{
			listAmsDeposits = getiAmsDepositDAO().getDepositHistory(customerId, pagingInfo);
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}	
		return listAmsDeposits;
	}
	
	/**
	 *  get bank info
	 * 
	 * @param
	 * @return
	 * @auth  HuyenMT
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String getBankInfo(String wlCode){
		AmsWhitelabel amsWhitelabel;
		String bankInfo = "";
		try {
			amsWhitelabel = new AmsWhitelabel();
			amsWhitelabel =  getiAmsWhitelabelDAO().findById(AmsWhitelabel.class, wlCode);
			bankInfo = amsWhitelabel.getBankInfo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}		
		return bankInfo; 
	}
	
	/**
	 * 　
	 * get list customer ewallet info 
	 * @param
	 * @return
	 * @auth QUyTM
	 * @CrDate Aug 20, 2012
	 * @MdDate
	 */
	public Map<Integer, List<CustomerEwalletInfo>> getListCustomerEwalletInfo(String customerId) {
		Map<Integer, List<CustomerEwalletInfo>> mapListCustomerEwalletInfo = new HashMap<Integer, List<CustomerEwalletInfo>>();
		List<CustomerEwalletInfo> listCustomerEwalletInfo = null;
		CustomerEwalletInfo customerEwalletInfo = null;
		// get list ewallet of neteller
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.NETELLER);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				
				
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.NETELLER, listCustomerEwalletInfo);
		}
		// get list ewallet of payza
		listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.PAYZA);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.PAYZA, listCustomerEwalletInfo);
		}
		// get list ewallet of payza
		listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.LIBERTY);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.LIBERTY, listCustomerEwalletInfo);
		}
		//[NTS1.0-Nguyen.Manh.Thang]Oct 23, 2012M - End	
		return mapListCustomerEwalletInfo;
	}
	/**
	 * 　
	 * get list customer ewallet info 
	 * @param
	 * @return
	 * @auth QUyTM
	 * @CrDate Aug 20, 2012
	 * @MdDate
	 */
	public Map<Integer, List<CustomerEwalletInfo>> getListCustomerEwalletInfo(String customerId, String publicKey) {
		String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key"); 
		Map<Integer, List<CustomerEwalletInfo>> mapListCustomerEwalletInfo = new HashMap<Integer, List<CustomerEwalletInfo>>();
		List<CustomerEwalletInfo> listCustomerEwalletInfo = null;
		CustomerEwalletInfo customerEwalletInfo = null;
		// get list ewallet of neteller
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.NETELLER);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				log.info("[start] decrypt data with private key and public key");
				decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
				log.info("[end] decrypt data with private key and public key");
				
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.NETELLER, listCustomerEwalletInfo);
		}
		// get list ewallet of payza
		listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.PAYZA);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				log.info("[start] decrypt data with private key and public key");
				decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
				log.info("[end] decrypt data with private key and public key");
				
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.PAYZA, listCustomerEwalletInfo);
		}
		// get list ewallet of payza
		listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, IConstants.EWALLET_TYPE.LIBERTY);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			listCustomerEwalletInfo = new ArrayList<CustomerEwalletInfo>();
			for(AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				log.info("[start] decrypt data with private key and public key");
				decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
				log.info("[end] decrypt data with private key and public key");
				
				
				listCustomerEwalletInfo.add(customerEwalletInfo);
			}
			mapListCustomerEwalletInfo.put(IConstants.EWALLET_TYPE.LIBERTY, listCustomerEwalletInfo);
		}
		//[NTS1.0-Nguyen.Manh.Thang]Oct 23, 2012M - End	
		return mapListCustomerEwalletInfo;
	}
	/**
	 * getNetellerInfo　
	 * get neteller Info
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public CustomerEwalletInfo getNetellerInfo(String customerId, String accountId, String publicKey) {
		CustomerEwalletInfo customerEwalletInfo = null;
		AmsCustomerEwallet amsCustomerEwallet = null;
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getAmsCustomerEwallet(customerId, IConstants.EWALLET_TYPE.NETELLER, accountId, null);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			amsCustomerEwallet = listAmsCustomerEwallet.get(0);
			if(amsCustomerEwallet != null) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				log.info("[start] decrypt data with private key and public key");
				decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
				log.info("[end] decrypt data with private key and public key");
			}
		}
		return customerEwalletInfo;
	}
	
	public CustomerEwalletInfo getNettellerInfo(Integer ewalletId, String publicKey) {
		CustomerEwalletInfo customerEwalletInfo = null;
		AmsCustomerEwallet amsCustomerEwallet = getiAmsCustomerEwalletDAO().findById(AmsCustomerEwallet.class, ewalletId);
		if(amsCustomerEwallet != null) {
			customerEwalletInfo = new CustomerEwalletInfo();
			BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key"); 
			log.info("[start] decrypt data with private key and public key");
			decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
		}
		return customerEwalletInfo;
	}

	private static String hideString(String str) {
		String digit4 = null;
		
		if (str != null && str.length() > 4) {
			int length = str.length();
			digit4 = str.substring(length - 4);
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < length - 4; i++) {
				bf.append("*");
			}
			return bf.append(digit4).toString();
		} else {
			digit4 = str == null ? "" : str;
			return digit4;
		}
		
	}

	/**
	 * 　
	 * get payza info
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public CustomerEwalletInfo getPayzaInfo(String customerId, String email, String publicKey) {
		CustomerEwalletInfo customerEwalletInfo = null;
		AmsCustomerEwallet amsCustomerEwallet = null;
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getAmsCustomerEwallet(customerId, IConstants.EWALLET_TYPE.PAYZA, null, email);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			amsCustomerEwallet = listAmsCustomerEwallet.get(0);
			if(amsCustomerEwallet != null) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
				
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				log.info("[start] decrypt data with private key and public key");
				decryptCustomerEwalletInfo(customerEwalletInfo, privateKey, publicKey);
				log.info("[end] decrypt data with private key and public key");
			}
		}
		return customerEwalletInfo;
	}
	/**
	 * 　
	 * get list customer credit card on TABLE AMS_CUSTOMER_CREDIT_CARD
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public List<CreditCardInfo> getListCustomerCreditCardInfo(String customerId) {
		List<CreditCardInfo> listCustomerCreditCardInfo = null;
		CreditCardInfo customerCreditInfo = null;
		Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
		List<AmsCustomerCreditcard> listAmsCustomerCreditCard = getiAmsCustomerCreditCardDAO().findByCusIdAndStatus(customerId, IConstants.DOC_VERIFY_STATUS.VERIFIED);
		if(listAmsCustomerCreditCard != null && listAmsCustomerCreditCard.size() > 0) {
			listCustomerCreditCardInfo = new ArrayList<CreditCardInfo>();
			for(AmsCustomerCreditcard amsCustomerCreditCard : listAmsCustomerCreditCard) {
				customerCreditInfo = new CreditCardInfo();
				BeanUtils.copyProperties(amsCustomerCreditCard, customerCreditInfo);
				customerCreditInfo.setCcTypeName(mapCardType.get(String.valueOf(amsCustomerCreditCard.getCcType())));
				customerCreditInfo.setExpiredDate(amsCustomerCreditCard.getExpiredDate());
				customerCreditInfo.setCcHolderName(amsCustomerCreditCard.getCcHolderName());
				String cardNo = hideString(amsCustomerCreditCard.getCcNo());
				
				customerCreditInfo.setCcNoDisp(cardNo);
				AmsSysCountry country = amsCustomerCreditCard.getAmsSysCountry();
				if (country != null) {
					customerCreditInfo.setCountryName(country.getCountryName());
					customerCreditInfo.setCountryId(country.getCountryId());
				}
				listCustomerCreditCardInfo.add(customerCreditInfo);
			}
		}
		return listCustomerCreditCardInfo;
	}
	
	public List<CreditCardInfo> getListCustomerCreditCardInfo(String customerId, String publicKey) {
		List<CreditCardInfo> listCustomerCreditCardInfo = null;
		CreditCardInfo customerCreditInfo = null;
		Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
		List<AmsCustomerCreditcard> listAmsCustomerCreditCard = getiAmsCustomerCreditCardDAO().findByCusIdAndStatus(customerId, IConstants.DOC_VERIFY_STATUS.VERIFIED);
		if(listAmsCustomerCreditCard != null && listAmsCustomerCreditCard.size() > 0) {
			listCustomerCreditCardInfo = new ArrayList<CreditCardInfo>();
			for(AmsCustomerCreditcard amsCustomerCreditCard : listAmsCustomerCreditCard) {
				customerCreditInfo = new CreditCardInfo();
				BeanUtils.copyProperties(amsCustomerCreditCard, customerCreditInfo);
				customerCreditInfo.setCcTypeName(mapCardType.get(String.valueOf(amsCustomerCreditCard.getCcType())));
				customerCreditInfo.setExpiredDate(amsCustomerCreditCard.getExpiredDate());
				customerCreditInfo.setCcHolderName(amsCustomerCreditCard.getCcHolderName());
				String cardNo = hideString(amsCustomerCreditCard.getCcNo());
				
				customerCreditInfo.setCcNoDisp(cardNo);
				AmsSysCountry country = amsCustomerCreditCard.getAmsSysCountry();
				if (country != null) {
					customerCreditInfo.setCountryName(country.getCountryName());
					customerCreditInfo.setCountryId(country.getCountryId());
				}
				log.info("[start] decrypt customer credit card");
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				decryptCustomerCreditCardInfo(customerCreditInfo, privateKey, publicKey);
				log.info("[end] decrypt customer credit card");
				listCustomerCreditCardInfo.add(customerCreditInfo);
			}
		}
		return listCustomerCreditCardInfo;
	}
	
	/**
	 * getCreditCardInfoByCcId　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Jan 21, 2013
	 */
	public CreditCardInfo getCreditCardInfoByCcId(Integer ccId, String publicKey) {
		
		CreditCardInfo customerCreditInfo = null;
		Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
		AmsCustomerCreditcard amsCustomerCreditCard = getiAmsCustomerCreditCardDAO().findById(AmsCustomerCreditcard.class, ccId);
		String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
		customerCreditInfo = new CreditCardInfo();
		BeanUtils.copyProperties(amsCustomerCreditCard, customerCreditInfo);
		customerCreditInfo.setCcTypeName(mapCardType.get(String.valueOf(amsCustomerCreditCard.getCcType())));
		customerCreditInfo.setCcNoDisp(hideString(amsCustomerCreditCard.getCcNo()));
		AmsSysCountry country = amsCustomerCreditCard.getAmsSysCountry();
		if (country != null) {
			customerCreditInfo.setCountryName(country.getCountryName());
			customerCreditInfo.setCountryId(country.getCountryId());
		}
		customerCreditInfo.setCcFirstName(amsCustomerCreditCard.getCcFirstName());
		customerCreditInfo.setCcLastName(amsCustomerCreditCard.getCcLastName());
		log.info("[start] decrypt credit card information");
		decryptCustomerCreditCardInfo(customerCreditInfo, privateKey, publicKey);
		log.info("[end] decrypt credit card information");
		return customerCreditInfo;
	}
	/**
	 * 　
	 * deposit via neteller
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public Integer depositNeteller(NetellerInfo netellerInfo, String publicKey) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;		
		try {			
			String subjectMail = "";
//			String templateMail = "";
			String customerServiceId = "";
			String customerId = netellerInfo.getCustomerId();	
			BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
			// get convert rate from AMS and target service type
			BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
			Integer symbolRound = new Integer(0);
			Integer symbolDecimal = new Integer(0);	
			Integer rateType = new Integer(-1);
			BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
			BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
			//BigDecimal cashFlowAmount = MathUtil.parseBigDecimal(0);
			String currencyCodeTargetServiceType = "";
			AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, netellerInfo.getServiceType());
			if(toAmsCustomerService != null) {
				customerServiceId = toAmsCustomerService.getCustomerServiceId();
				// get subgroup of customer services for get currency code
				AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
				if(amsSubGroup != null) {
					currencyCodeTargetServiceType = amsSubGroup.getCurrencyCode();
					if(netellerInfo.getBaseCurrency().equals(currencyCodeTargetServiceType)) {
						convertRate = MathUtil.parseBigDecimal(1);
					} else {
						// set symbolName = fromCurrency + toCurrency
						convertRate = getConvertRateOnFrontRate(amsSubGroup.getCurrencyCode(), netellerInfo.getBaseCurrency(), IConstants.FRONT_OTHER.SCALE_ALL);
//						String symbolName = amsSubGroup.getCurrencyCode() + netellerInfo.getBaseCurrency();
//						Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//						RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//							rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//						} else {	
//							symbolName = netellerInfo.getBaseCurrency() + amsSubGroup.getCurrencyCode();
//							rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//							if(rateInfo != null) {
//								// get symbol rounding from table AMS_SYS_SYMBOL
//								FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//								if(amsSysSymbol != null) {
//									symbolRound = amsSysSymbol.getSymbolRound();
//									symbolDecimal = amsSysSymbol.getSymbolDecimal();
//								}
//								log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//								convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//								log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//								rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//							} else {
//								log.warn("Cannot find rate of symbolName " + symbolName);
//								return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//							}
//							
//						}
						if(convertRate == null || convertRate.compareTo(new BigDecimal("0")) <= 0) {
							return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
						}
					}
				}
			}
			
			// end get rate
			Map<String, String> mapNeteller = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + netellerInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.NETELLER_CONFIG);		
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			// Create deposit record
			AmsDeposit amsDeposit = new AmsDeposit();
			String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
			log.info("[start] insert deposit for depositId " + depositId);
			amsDeposit.setDepositId(depositId);
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, netellerInfo.getCustomerId());
			if(amsCustomer == null) {
				log.warn("depositNeteller Cannot find customerInfo with Id: " + netellerInfo.getCustomerId());
				return IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
			}						
			amsDeposit.setAmsCustomer(amsCustomer);		
			amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsDeposit.setCurrencyCode(netellerInfo.getCurrencyCode());
			amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
			amsDeposit.setServiceType(netellerInfo.getServiceType());
			amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
			amsDeposit.setDepositAmount(netellerInfo.getAmount().doubleValue());
			amsDeposit.setRate(convertRate.doubleValue());
			amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.NETELLER);
			amsDeposit.setDepositRoute(netellerInfo.getDeviceType());
			if(amsAppDate != null) {
				amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
			} else {
				log.warn("depositNeteller Cannot find config for Bussiness Date");
				return IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
			}
			amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
			log.info("[==============> CurrentTimestamp " + new Timestamp(System.currentTimeMillis()));
			amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));
			amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis())); 
//			amsDeposit.setConfirmDate(null);//HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
			AmsDepositRef amsDepositRef = new AmsDepositRef();
			amsDepositRef.setDepositId(depositId);
			amsDepositRef.setEwalletType(netellerInfo.getPaymentMethod());
			amsDepositRef.setEwalletAccNo(netellerInfo.getAccountId());
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012D - Start close for Bug #6669
			/*amsDepositRef.setEwalletSecureId(netellerInfo.getSecureId());*/
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012D - End
//			try {
//				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key"); 
//				String encryptSecureId = Cryptography.encrypt(netellerInfo.getSecureId(), privateKey, publicKey);
//				amsDepositRef.setEwalletSecureId(encryptSecureId);
//			} catch (Exception e) {
//				log.error(e.getMessage(), e);
//				String md5SecureId = Security.MD5(netellerInfo.getSecureId());
//				amsDepositRef.setEwalletSecureId(md5SecureId);
//			}
			String md5SecureId = Security.MD5(netellerInfo.getSecureId());
			amsDepositRef.setEwalletSecureId(md5SecureId);
			
			amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
			amsDepositRef.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);			
			amsDepositRef.setAmsDeposit(amsDeposit);
			amsDeposit.setAmsDepositRef(amsDepositRef);
			log.info("depositNeteller depositInfo: depositId: " + depositId + ", amount: " + netellerInfo.getAmount() + ", customerId: " + netellerInfo.getCustomerId() + ", currencyCode: " + netellerInfo.getCurrencyCode());
			
			Map<String, String> mapSubjectMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + netellerInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);							
			try {
				// save deposit
				getiAmsDepositDAO().save(amsDeposit);
				
				log.info("[end] insert deposit for depositId " + depositId);
				NetellerResponseInfo netellerResponseInfo = NetellerContext.getInstance().getNetellerResponseInfo(mapNeteller, netellerInfo);			
				amsDepositRef.setGwRefId(netellerResponseInfo.getTransactionId());
				getiAmsDepositRefDAO().save(amsDepositRef);							
				if (IConstants.NETELLER_RESPONSE.APPROVAL_YES.equalsIgnoreCase(netellerResponseInfo.getApproval())) {
					log.debug("depositNeteller Deposit to Neteller payment gateway success");
					amsDeposit.setDepositCompletedDate(amsAppDate.getId().getFrontDate());
					amsDeposit.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
					log.info("[==============> CurrencyTimestamp " + new Timestamp(System.currentTimeMillis()));
					amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.FINISHED);				
					getiAmsDepositDAO().attachDirty(amsDeposit);
					
					// send mail to customer about deposit successful
//					DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
					String language = amsCustomer.getDisplayLanguage();
					if(language == null || StringUtils.isBlank(language)) {
						language = IConstants.Language.ENGLISH;
					}
//					MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS + "_" + language);
					sendmailDepositSuccess(amsCustomer, depositId, netellerInfo.getAmount(), netellerInfo.getCurrencyCode(), language, IConstants.PAYMENT_METHOD.NETELLER);

					if(IConstants.RATE_TYPE.BID.equals(rateType)) {
						convertedAmount = convertRate.multiply(netellerInfo.getAmount());
					} else if(IConstants.RATE_TYPE.ASK.equals(rateType)) {
						convertedAmount = netellerInfo.getAmount().multiply(MathUtil.parseBigDecimal(1).divide(convertRate, IConstants.FRONT_OTHER.SCALE_ALL, BigDecimal.ROUND_DOWN));
						convertedAmount = convertedAmount.divide(MathUtil.parseBigDecimal(1), symbolDecimal, symbolRound);
					} else {
						convertedAmount = netellerInfo.getAmount();
					}
					
					// update cash balance // base currency is currency of AMS
					AmsCashBalance amsCashBalance = updateAmsCashBalance(customerId, netellerInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.FALSE); // deductFlag = false
					Double balance = amsCashBalance.getCashBalance();
					// insert data into table CASH_FLOW
					
					insertCashFlow(depositId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL, convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.DEPOSIT_ID, netellerInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue());
					// end insert cash flow
					
					// check service type 
					// if service type != AMS, insert into table AMS_TRANSFER_MONEY
					if(!IConstants.SERVICES_TYPE.AMS.equals(netellerInfo.getServiceType())) {
						AmsTransferMoney amsTransferMoney = null;
						
						Boolean hasTransfer = false;
						
											
						log.info("Transfer money from AMS " + netellerInfo.getAmount() + " -> " + netellerInfo.getServiceType() + " with Amount = " + convertedAmount);
						// insert AMS_TRANSFER MONEY 
						
//						TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
//						String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
//						transferMoneyInfo.setTransferMoneyId(transferMoneyId);
//						transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
//						transferMoneyInfo.setCustomerId(customerId);
//						transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
//						transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
//						transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
//						transferMoneyInfo.setTransferTo(netellerInfo.getServiceType());
//						transferMoneyInfo.setTransferMoney(netellerInfo.getAmount().doubleValue());
////					transferMoneyInfo.setWlRefId(netellerResponseInfo.getTransactionId());
//						transferMoneyInfo.setRate(convertRate);
//						transferMoneyInfo.setCurrencyCode(netellerInfo.getCurrencyCode());
//						transferMoneyInfo.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
//						transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
//						transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
//						transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
//						transferMoneyInfo.setSourceId(depositId);
//						transferMoneyInfo.setDestinationAmount(netellerInfo.getAmount().doubleValue());
//						transferMoneyInfo.setDestinationCurrencyCode(netellerInfo.getCurrencyCode());
						
						TransferMoneyInfo transferMoneyInfo = getTransferMoneyInfo(customerId, netellerInfo.getServiceType(), netellerInfo.getAmount().doubleValue(), convertRate, netellerInfo.getCurrencyCode(), amsAppDate.getId().getFrontDate(), depositId);
						
						amsTransferMoney = insertTransferMoney(transferMoneyInfo);
						// checking service type 
						if(IConstants.SERVICES_TYPE.FX.equals(netellerInfo.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(netellerInfo.getServiceType())) {
							log.info("depositNeteller serviceType = " + netellerInfo.getServiceType() + " with customerId: " + netellerInfo.getCustomerId() + ", customerServiceId: " + customerServiceId);
							// if service type == FX then update balance on MT4					
							Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
							String description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETELLER);
							Integer resultUpdateBalance = MT4Manager.getInstance().depositBalance(customerServiceId, netellerInfo.getAmount().doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
							if(resultUpdateBalance.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)) {
								amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
								amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
								amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
								
//								log.info("[start] update net deposit for customerId = " + amsCustomer.getCustomerId());
//								updateNetDeposit(netellerInfo.getServiceType(), netellerInfo.getCustomerId(), netellerInfo.getCurrencyCode(), netellerInfo.getAmount().doubleValue());
//								log.info("[end] update net deposit for customerId = " + amsCustomer.getCustomerId());
								
								log.info("update balance to mt4 account " + customerId +  " successful");
								log.info("[start] checking NET_DEPOSIT of account " + customerId);
								BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
								AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
								amsCashBalanceFxId.setCurrencyCode(netellerInfo.getCurrencyCode());
								amsCashBalanceFxId.setCustomerId(customerId);
								amsCashBalanceFxId.setServiceType(netellerInfo.getServiceType());
								AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
								if(amsCashBalanceFx != null) {
									netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
								}
								log.info("[end] checking NET_DEPOSIT of account " + customerId + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
								BigDecimal totalNetDepositAmount = netDepositAmount.add(netellerInfo.getAmount());
								Integer scale = new Integer(0);
								Integer rounding = new Integer(0);
								CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + netellerInfo.getCurrencyCode());
								if(currencyInfo != null) {
									scale = currencyInfo.getCurrencyDecimal();
									rounding = currencyInfo.getCurrencyRound();
								}	
								AmsPromotion amsPromotion = null;
								totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
								log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
								if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
									log.info("[start] check promotion for depositId: " + depositId);
									//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, netellerInfo.getWlCode());
									amsPromotion = amsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, netellerInfo.getServiceType(), netellerInfo.getSubGroupId());
									
									if(amsPromotion != null) {
										log.info("currently, depositId: " + depositId + " will be received promotion");
										log.info("process promotion for customerId: " + netellerInfo.getCustomerId() + " with kind = " + amsPromotion.getKind());
										//bonusAmount = getiPromotionManager().getBonusAmount(netellerInfo.getAmount(), netellerInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, netellerInfo.getWlCode());
										//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start 
										BigDecimal baseAmount = getBonusByNetDeposit(netellerInfo.getAmount(), netDepositAmount, amsPromotion.getKind());
										if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
											bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, netellerInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, netellerInfo.getServiceType(), netellerInfo.getSubGroupId());
											//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
										} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
											bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, netellerInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, netellerInfo.getServiceType(), netellerInfo.getSubGroupId(), netDepositAmount, customerId, true);
										}
										
										if(bonusAmount != null && bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
											log.info("promotionTitle: " + amsPromotion.getPromotionTitle() + "depositId: " + depositId + " will be recieved " + bonusAmount + " " + netellerInfo.getCurrencyCode());
											log.info("send request deposit to customerId: " + netellerInfo.getCustomerId() + ", amount: " + bonusAmount);
											description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETELLER);
											Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
											if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
												log.info("update credit with amount: " + bonusAmount + " is successful");
												getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), bonusAmount.doubleValue(), depositId, netellerInfo.getCurrencyCode());
												//send mail bonus
												sendmailDepositBonus(amsDeposit, amsCustomer, bonusAmount, language);

												amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
												amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
											}
										} else {
											log.info("bonus amount is 0, amount: " + netellerInfo.getAmount() + ", currencyCode: " + netellerInfo.getCurrencyCode() + ", promotionId: " + amsPromotion.getPromotionId());
										}
									} else {
										log.info("cannot find promotion for depositId: " + depositId);
									}
								}
								
								//[NTS1.0-Administrator]Sep 27, 2012A - Start - checking promotion for losscut 
								//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, netellerInfo.getWlCode());
								amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, netellerInfo.getServiceType(), netellerInfo.getSubGroupId());
								if(amsPromotion != null) {									
									log.info("Find promotion for losscut");
									log.info("[start] process promotion losscut for customerId: " + netellerInfo.getCustomerId());
									log.info("Checking losscutflag of customer " + netellerInfo.getCustomerId() + " and customer service id " + customerServiceId);
									if(toAmsCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(toAmsCustomerService.getLosscutFlg())) {
										// get lastest deposit of this customer
										log.info("customerId " + customerServiceId + " is losscut");
										log.info("[start] get deposit lastest for customerId " + netellerInfo.getCustomerId());
//										String losscutDatetime = DateUtil.toString(toAmsCustomerService.getLosscutDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DB);
										AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(netellerInfo.getCustomerId(), toAmsCustomerService.getLosscutDatetime());
										if(amsDepositLastest != null) {
											log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
											AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(customerId, amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
											log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
											if(amsPromotionCustomer != null) {
												log.info("CustomerID: " + customerId + " recieved promotion losscut");
												toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
												getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
											} else {
												log.info("Lastest deposit of customerServiceId = " + customerServiceId + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
												log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceId );
												//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, netellerInfo.getWlCode());
												BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, netellerInfo.getServiceType(), netellerInfo.getSubGroupId());
												log.info("customerServiceId " + customerServiceId + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
												if(losscutAmountBonus != null && losscutAmountBonus.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
													log.info("[start] send request deposit to credit for customerServiceId " + customerServiceId);	
													description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
													Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
													if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
														log.info("CurrencyCode of customerId " + amsDepositLastest.getCurrencyCode());
														if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
															log.info("Because currencyCode of customerId " + customerServiceId + " is japan so system will not send mail to this account");
														} else {
															if(IConstants.Language.JAPANESE.equals(language)) {
																language = IConstants.Language.ENGLISH;
															}
															sendmailLosscut(amsDepositLastest, amsCustomer, losscutAmountBonus, language, netellerInfo.getCurrencyCode());
														}
														
														log.info("customer service id " + customerServiceId + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
														log.info("[start] Insert data into PROMOTION CUSTOMER");
														getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), netellerInfo.getCurrencyCode());													
														log.info("[end] Insert data into PROMOTION CUSTOMER");
													} else {
														log.warn("Cannot plus credit for customer service id " + customerServiceId + " because returnCode = " + resultUpdateCredit);
													}
												}
												
												log.info("[start] update losscutFlag of customerServiceId " + customerServiceId);
												toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
												getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
												log.info("[end] update losscutFlag of customerServiceId " + customerServiceId);
												log.info("[end] send request deposit to credit for customerServiceId " + customerServiceId);
												log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceId );
											}
											
											
										} else {
											log.info("customerId " + customerServiceId + " no longer deposit into system");
											toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
											getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
										}
										log.info("[end] get deposit lastest for customerId " + netellerInfo.getCustomerId());
									} else {
										log.info("CustomerID: " + netellerInfo.getCustomerId() + " has losscutFlag = false");
									}
									
									log.info("[end] process promotion losscut for customerId: " + netellerInfo.getCustomerId());
								} else {
									log.info("Cannot find promotion for losscut");
								}
								getiAmsTransferMoneyDAO().merge(amsTransferMoney);
								//[NTS1.0-Administrator]Sep 27, 2012A - End
								hasTransfer = true;																			
							} else {
								result = IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR;
								amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
								getiAmsTransferMoneyDAO().merge(amsTransferMoney);
								
								//  send mail to OM via occur error on MT4						
								log.warn("depositNeteller [start] Send mail to OM about occur MT4 Error" );
								Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
								
								String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
								String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
								if(omDescription == null) {
									omDescription = "";
								}
								String errorOn = "depositNeteller function depositNeteller";													
								String template = IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;

								subjectMail = SystemPropertyConfig.getInstance().getText(mapSubjectMail.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL));
								try {
									mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
								} catch (Exception e) {
									log.error("[sendMailOM] can not send mail");
								}
								log.error("depositNeteller [end] Send mail to OM about occur MT4 Error" );
							}
							
						} else {
							amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
							amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
							amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
							getiAmsTransferMoneyDAO().merge(amsTransferMoney);
							hasTransfer = true;							
						}
						if(hasTransfer) {
							if(IConstants.SERVICES_TYPE.BO.equals(netellerInfo.getServiceType())) {
								// if servicetype = BO then send topic refresh balance 
								log.info("[start] send topic update balance info for BO");
								log.info("[start] send topic for Refresh Balance Info of BO with ");
								try {									
									BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, netellerInfo.getAmount(), 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
									jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
								} catch(Exception ex) {
									log.error(ex.getMessage(), ex);
								}		
								log.info("[end] send topic for Refresh Balance Info of BO");
								log.info("[end] send topic update balance info for BO");
							}
							// deduct ams balance for insert cashflow follow 7.2.3 NAF401
							Double amsBalance = balance - convertedAmount.doubleValue();
							
							// save cashflow for deduct ams balance
							insertCashFlow(transferMoneyInfo.getTransferMoneyId(), customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 0 - convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, netellerInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, amsBalance, convertRate.doubleValue());
							// save cashflow for plus fx balance
							if(IConstants.SERVICES_TYPE.FX.equals(netellerInfo.getServiceType())) {
								// get balanceInfo from MT4
//								BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
								BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, netellerInfo.getCurrencyCode());
								balance = new Double(0);
								if(balanceInfo != null) {
									balance = balanceInfo.getBalance();
								}
							} else {
								// get balanceInfo from other balance
								AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
								amsCashBalanceId.setCurrencyCode(netellerInfo.getCurrencyCode());
								amsCashBalanceId.setCustomerId(customerId);
								amsCashBalanceId.setServiceType(netellerInfo.getServiceType());
								AmsCashBalance amsCashBalanceServiceType = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId); 
								balance = new Double(0);
								if(amsCashBalanceServiceType != null) {
									balance = amsCashBalanceServiceType.getCashBalance();
								}
								balance = balance + netellerInfo.getAmount().doubleValue();
							}
							
							insertCashFlow(transferMoneyInfo.getTransferMoneyId(), customerId, customerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, netellerInfo.getAmount().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, netellerInfo.getCurrencyCode(), netellerInfo.getServiceType(), balance, convertRate.doubleValue());
							// update balance for deduct balance of AMS
							updateAmsCashBalance(customerId, netellerInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE); // deduct Flag = true mean subtract balance of customer
							//[TDSBO1.0-Administrator]Oct 31, 2012M - Start - Apply deposit bonus that are based on NET_DEPOSIT for all services 
//							if(!IConstants.SERVICES_TYPE.FX.equals(netellerInfo.getServiceType())) {
								// update balance for other servicetype except AMS
							updateAmsCashBalance(customerId, netellerInfo.getCurrencyCode(), netellerInfo.getServiceType(), netellerInfo.getAmount().doubleValue(), bonusAmount.doubleValue(), Boolean.FALSE); // deduct Flag = false mean plus balance of customer
//							}
							//[NTS1.0-Administrator]Mar 11, 2013A - Start - update AMS_CUSTOMER_SURVEY
							
						}
						
					} else {
						// do nothing
					}
//					log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
//					AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
//					if(amsCustomerSurvey != null) {
//						if(amsCustomerSurvey.getNetDepositCc() == null) amsCustomerSurvey.setNetDepositCc(new Double("0"));
//						amsCustomerSurvey.setNetDepositCc(amsCustomerSurvey.getNetDepositCc() + amsDeposit.getDepositAmount());
//						amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
//						amsCustomerSurveyDAO.merge(amsCustomerSurvey);
//					}
//					log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
					
					log.info("[start] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
					updateRemarkCustomerSurvey(customerId, amsDeposit.getDepositMethod());
					log.info("[end] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
//					getiAmsDepositDAO().attachDirty(amsDeposit);
					//  send mail to customer about deposit success
					result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
				} else {					
					//result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
					result = IConstants.DEPOSIT_UPDATE_RESULT.CANCEL;
					log.info("depositNeteller Deposit fail because payment gateway not accept");				
					// update status deposit 
					log.info("depositNeteller [start] update status to fail ");
					amsDeposit.setErrorCode(netellerResponseInfo.getErrorCode());
					amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.CANCEL);				
					getiAmsDepositDAO().attachDirty(amsDeposit);
					log.info("depositNeteller [end] update status to failure ");
					// send mail to customer about deposit is failure
					String language = IConstants.Language.ENGLISH;
					if(amsCustomer.getDisplayLanguage() != null && !StringUtils.isBlank(amsCustomer.getDisplayLanguage())) {
						language = amsCustomer.getDisplayLanguage();
					}					
					sendmailDepositCancel(amsDeposit, amsCustomer, netellerInfo.getAmount().doubleValue(), netellerResponseInfo.getErrorMessage(), language,
							IConstants.PAYMENT_METHOD.NETELLER);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
				e.printStackTrace();
				log.error("Error on deposit by neteller", e);
				log.error("depositNeteller [start] Send mail to OM about occur MT4 Error" );
				Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
				
				String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
				String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
				if(omDescription == null) {
					omDescription = "";
				}
				String errorOn = "function depositNeteller";										
				String template = IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
				Map<String, String> mapMailSubject = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + netellerInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);
				subjectMail = mapMailSubject.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
				mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
				log.error("depositNeteller [end] Send mail to OM about occur MT4 Error" );
			}
		} catch(Exception ex) {
			result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
			log.error(ex.getMessage(), ex);
		}
		
		return result;
	}

	/**
	 * sendmailDepositCancel　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailDepositCancel(AmsDeposit amsDeposit, AmsCustomer amsCustomer, Double depositAmount, String depositCancelReason, String language, Integer paymentMethod) {
		log.info("[start] send mail deposit cancel for deposit id " + amsDeposit.getDepositId());
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_CANCEL + "_" + language); //  mail template lack DEPOSIT
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_CANCEL).append("_").append(language).toString();
		DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(formater.format(depositAmount));													
		amsMailTemplateInfo.setDepositCurrency(amsDeposit.getCurrencyCode());		
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(amsDeposit.getDepositAcceptDatetime(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		amsMailTemplateInfo.setDepositId(amsDeposit.getDepositId());
		Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
		amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(paymentMethod)));
		amsMailTemplateInfo.setDepositCancelReason(depositCancelReason);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
															
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail deposit cancel for deposit id " + amsDeposit.getDepositId());
	}

	/**
	 * sendmailLosscut　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailLosscut(AmsDeposit amsDepositLastest, AmsCustomer amsCustomer, BigDecimal losscutAmountBonus, String language, String currencyCode) {
		log.info("DepositManagerImpl.sendmailLosscut() start send mail losscut");
		
		String depositId = amsDepositLastest.getDepositId();
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_INSUARANCE_LOSSCUT + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_INSUARANCE_LOSSCUT).append("_").append(language).toString();
			AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(balanceManager.formatNumber(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode()));
		amsMailTemplateInfo.setDepositCurrency(balanceManager.getCurrencyCode(amsDepositLastest.getCurrencyCode(), language));
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(amsDepositLastest.getDepositCompletedDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DDMMYYYY));																																
		amsMailTemplateInfo.setBonusAmount(balanceManager.formatNumber(losscutAmountBonus, currencyCode));
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositId(depositId);
		Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
		amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(IConstants.PAYMENT_METHOD.NETELLER)));
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																												
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("DepositManagerImpl.sendmailLosscut() end send mail losscut");
	}

	/**
	 * sendmailDepositBonus　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailDepositBonus(AmsDeposit amsDeposit, AmsCustomer amsCustomer, BigDecimal bonusAmount, String language) {
		String depositId = amsDeposit.getDepositId();
		log.info("[start] send mail deposit bonus for deposit id " + depositId);											
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_BONUS).append("_").append(language).toString();
		
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();

		amsMailTemplateInfo.setBonusAmount(balanceManager.formatNumber(bonusAmount, amsDeposit.getCurrencyCode()));
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setBonusCurrency(balanceManager.getCurrencyCode(amsDeposit.getCurrencyCode(), language));
		amsMailTemplateInfo.setDepositCurrency(amsDeposit.getCurrencyCode());													
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
															
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);

		log.info("[end] send mail deposit bonus for deposit id " + depositId);
	}
	public void insertCashFlow(String transactionId, String customerId, String customerServiceId, SysAppDate amsAppDate, Integer cashFlowType, Double amount, Integer sourceType, String currencyCode, Integer serviceType, Double balance, Double convertRate) {
		AmsCashflow amsCashFlow = new AmsCashflow();
		String cashFlowId = generateUniqueId(IConstants.UNIQUE_CONTEXT.CASHFLOW_CONTEXT);
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setCashflowId(cashFlowId);		
		amsCashFlow.setCashBalance(balance);		
//		amsCashFlow.setAmsCashBalance(amsCashBalance);
		amsCashFlow.setEventDatetime(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setEventDate(amsAppDate.getId().getFrontDate());
		amsCashFlow.setValueDate(amsAppDate.getId().getFrontDate());
		amsCashFlow.setCashflowType(cashFlowType);
		amsCashFlow.setCashflowAmount(amount);
		amsCashFlow.setServiceType(serviceType);
		amsCashFlow.setCurrencyCode(currencyCode);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(customerId);
		amsCashFlow.setAmsCustomer(amsCustomer);
		amsCashFlow.setRate(convertRate);
		//[NTS1.0-Mai.Thu.Huyen]Oct 17, 2012A - Start comment, and set null for Bug #6862
		/*amsCashFlow.setTax(new Double(IConstants.FRONT_OTHER.ONE));*/
		//[NTS1.0-Mai.Thu.Huyen]Oct 17, 2012A - End
		amsCashFlow.setTax(null);
		amsCashFlow.setSourceType(sourceType);
		amsCashFlow.setSourceId(transactionId);
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setUpdateDate(new Timestamp(System.currentTimeMillis()));		
		amsCashFlow.setCustomerServiceId(customerServiceId);
		//amsCashFlow.setServiceType(serviceType);
		getiAmsCashFlowDAO().save(amsCashFlow);			
	}
	/**
	 * 　insert cashflow with tax and rate 
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Sep 4, 2012
	 * @MdDate
	 */
	public void insertCashFlow(String transactionId, String customerId, String customerServiceId, SysAppDate amsAppDate, Integer cashFlowType, Double amount, Integer sourceType, Integer serviceType, Double balance, Double rate, String currencyCode) {		
		AmsCashflow amsCashFlow = new AmsCashflow();
		String cashFlowId = generateUniqueId(IConstants.UNIQUE_CONTEXT.CASHFLOW_CONTEXT);
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setCashflowId(cashFlowId);		
		amsCashFlow.setCashBalance(balance);		
		amsCashFlow.setCurrencyCode(currencyCode);
		amsCashFlow.setServiceType(serviceType);
		amsCashFlow.setEventDatetime(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setEventDate(amsAppDate.getId().getFrontDate());
		amsCashFlow.setValueDate(amsAppDate.getId().getFrontDate());
		amsCashFlow.setCashflowType(cashFlowType);
		amsCashFlow.setCashflowAmount(amount);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(customerId);
		amsCashFlow.setAmsCustomer(amsCustomer);
		amsCashFlow.setRate(rate);
		amsCashFlow.setTax(null);
		amsCashFlow.setSourceType(sourceType);
		amsCashFlow.setSourceId(transactionId);
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setCustomerServiceId(customerServiceId);
		getiAmsCashFlowDAO().save(amsCashFlow);				
	}
	
	
	/**
	 * 　
	 * update ams cash balance
	 * @param PlusType: if true Balance = Balance + amount else balance = amount
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate Nov 07, 2012: do not update CashBalance, CreditAmount in case FX service
	 */
	public AmsCashBalance updateAmsCashBalance(String customerId, String currencyCode, Integer serviceType, Double amount, Double creditAmount, Boolean deductFlag) {
		
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(currencyCode);
		amsCashBalanceId.setCustomerId(customerId);
		amsCashBalanceId.setServiceType(serviceType);
		AmsCashBalance amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalance != null) {
			amsCashBalance.setPreviousBalance(amsCashBalance.getCashBalance());
			if(!deductFlag) {
				// do not update CashBalance, CreditAmount in case FX service
				//[NTS1.0-le.hong.ha]May 7, 2013M - Start 
				// For TRS do update CashBalance, CreditAmount in case FX and CopyTrad service
				//if (serviceType.intValue() != IConstants.SERVICES_TYPE.FX.intValue() || serviceType.intValue() != IConstants.SERVICES_TYPE.COPY_TRADE.intValue()) {
					amsCashBalance.setCashBalance(amsCashBalance.getCashBalance() + amount);
					amsCashBalance.setCreditAmount(amsCashBalance.getCreditAmount() + creditAmount);
				//}
				//[NTS1.0-le.hong.ha]May 7, 2013M - End
				amsCashBalance.setNetDepositAmount(amsCashBalance.getNetDepositAmount() + amount);
			} else {
				// do not update CashBalance, CreditAmount in case FX service
				//[NTS1.0-le.hong.ha]May 7, 2013M - Start 
				// For TRS do update CashBalance, CreditAmount in case FX and CopyTrad service
				//if (serviceType.intValue() != IConstants.SERVICES_TYPE.FX.intValue() || serviceType.intValue() != IConstants.SERVICES_TYPE.COPY_TRADE.intValue()) {
					amsCashBalance.setCashBalance(amsCashBalance.getCashBalance() - amount);
					amsCashBalance.setCreditAmount(amsCashBalance.getCreditAmount() - creditAmount);
				//}
				//[NTS1.0-le.hong.ha]May 7, 2013M - End
				amsCashBalance.setNetDepositAmount(amsCashBalance.getNetDepositAmount() - amount);
			}			
			amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			getiAmsCashBalanceDAO().merge(amsCashBalance);
		} else {
			log.warn("Cannot find cash balance for customerId: " + customerId + ", currencyCode: " + currencyCode);
		}
		return amsCashBalance;
	}
	public CustomerInfo getCustomerInfo(String customerId) {
		CustomerInfo customerInfo = null;
		AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, customerId);
		if(amsCustomer != null) {			
			customerInfo = new CustomerInfo();
			BeanUtils.copyProperties(amsCustomer, customerInfo);
			SysCurrency amsSysCurrency = amsCustomer.getSysCurrency();
			if(amsSysCurrency != null) {
				customerInfo.setCurrencyCode(amsSysCurrency.getCurrencyCode());					
			}
			AmsGroup amsGroup = amsCustomer.getAmsGroup();
			if(amsGroup != null) {
				customerInfo.setGroupId(amsGroup.getGroupId());
				customerInfo.setGroupName(amsGroup.getGroupName());
			}
			AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
			if(amsSysCountry != null) {
				customerInfo.setCountryId(amsSysCountry.getCountryId());
			}
											
		}
		return customerInfo;
	}
//	private AmsTransferMoney insertTransferMoney(TransferMoneyInfo transferMoneyInfo ) {
//		AmsTransferMoney amsTransferMoney = new AmsTransferMoney();
//		
//		BeanUtils.copyProperties(transferMoneyInfo, amsTransferMoney);
//		if(amsTransferMoney != null) {
//			AmsCustomer amsCustomer = new AmsCustomer();
//			amsCustomer.setCustomerId(transferMoneyInfo.getCustomerId());
//			amsTransferMoney.setAmsCustomer(amsCustomer);
//			getiAmsTransferMoneyDAO().save(amsTransferMoney);
//		}
//		return amsTransferMoney;
//	}
	
	private AmsTransferMoney insertTransferMoney(TransferMoneyInfo transferMoneyInfo) {
		log.info("[insertTransferMoney] start ");
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		AmsTransferMoney transferMoney = new AmsTransferMoney();
		String transferMoneyId = transferMoneyInfo.getTransferMoneyId();
		transferMoney.setTransferMoneyId(transferMoneyId);
		transferMoney.setTransferFrom(transferMoneyInfo.getTransferFrom());
		transferMoney.setTransferTo(transferMoneyInfo.getTransferTo());
		
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(transferMoneyInfo.getCustomerId());
		transferMoney.setAmsCustomer(amsCustomer);
		
		transferMoney.setTransferMoney(transferMoneyInfo.getTransferMoney());
		transferMoney.setRate(transferMoneyInfo.getRate() == null ? 1D : transferMoneyInfo.getRate().doubleValue());
		transferMoney.setCurrencyCode(transferMoneyInfo.getCurrencyCode());
		transferMoney.setTranferAcceptDate(transferMoneyInfo.getTranferAcceptDate());
		transferMoney.setTranferAcceptDateTime(currentTime);
		transferMoney.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		transferMoney.setInputDate(currentTime);
		transferMoney.setUpdateDate(currentTime);
		transferMoney.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
		transferMoney.setDestinationAmount(transferMoneyInfo.getDestinationAmount());
		transferMoney.setDestinationCurrencyCode(transferMoneyInfo.getDestinationCurrencyCode());
//		transferMoney.setTranferCompleteDate(transferMoneyInfo.gettranfer);
//		transferMoney.setTranferCompleteDateTime(currentTime);
		iAmsTransferMoneyDAO.save(transferMoney);
		// write log
		log.info("[insertTransferMoney] end ");
		return transferMoney;
	}

	/**
	 * 　
	 * deposit on payza 
	 * @param PayzaInfo information of payza account
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public Integer depositPayza(PayzaInfo payzaInfo) {
		Map<String, String> mapPayzaConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + payzaInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.PAYZA_CONFIG);
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		String payzalUrl = mapPayzaConfig.get(IConstants.PAYZA_CONFIGURATION.PAYMENT_GATEWAY_PAYZA);
		String payzaMerchantUser = mapPayzaConfig.get(IConstants.PAYZA_CONFIGURATION.PAYZA_MERCHANT_USER);
				
		//[NatureForex1.0-Administrator]Jun 20, 2012A - Start - encode for parameter 	
		String emailAddress = null;
		String apiPassword = null;
		try {
			emailAddress = URLEncoder.encode(payzaInfo.getEmailAddress(), IConstants.UTF8);
			apiPassword = URLEncoder.encode(payzaInfo.getApiPassword(), IConstants.UTF8);
			payzaMerchantUser = URLEncoder.encode(payzaMerchantUser, IConstants.UTF8);				
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Integer purchaseType = 1;
		String params = "USER=" + emailAddress + "&PASSWORD=" + apiPassword + "&AMOUNT=" + payzaInfo.getAmount() + "&CURRENCY=" + payzaInfo.getCurrencyCode() + "&RECEIVEREMAIL=" + payzaMerchantUser + "&PURCHASETYPE=" + purchaseType;
		log.info("Parammeters of payza - Email Address : " + emailAddress + ", is " + params);
		// get convert rate from AMS and target service type
		BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
		Integer symbolRound = new Integer(0);
		Integer symbolDecimal = new Integer(0);
		BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
		Integer rateType = new Integer(-1);
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		String currencyCodeService = ""; 
		String customerServiceId = ""; 
		String customerId = payzaInfo.getCustomerId();
		AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, payzaInfo.getServiceType());
		if(toAmsCustomerService != null) {
			customerServiceId = toAmsCustomerService.getCustomerServiceId();
			// get subgroup of customer services for get currency code
			AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
			if(amsSubGroup != null) {
				currencyCodeService = amsSubGroup.getCurrencyCode();
				if(payzaInfo.getBaseCurrency().equals(currencyCodeService)) {
					convertRate = MathUtil.parseBigDecimal(1);
				} else {
					//[NTS1.0-Administrator]Apr 9, 2013D - Start - remove getting rate from DB 
					// set symbolName = fromCurrency + toCurrency
//					String symbolName = amsSubGroup.getCurrencyCode() + payzaInfo.getBaseCurrency();
//					Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//					RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//					if(rateInfo != null) {
//						// get symbol rounding from table AMS_SYS_SYMBOL
//						FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//						if(amsSysSymbol != null) {
//							symbolRound = amsSysSymbol.getSymbolRound();
//							symbolDecimal = amsSysSymbol.getSymbolDecimal();
//						}
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//						convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//						rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//					} else {	
//						symbolName = payzaInfo.getBaseCurrency() + amsSubGroup.getCurrencyCode();																		
//						rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//							rateType = IConstants.RATE_TYPE.BID; // if ask counter currency / base currency
//						} else {
//							log.warn("Cannot find rate of symbolName " + symbolName);
//							return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//						}
//						
//					}
					//[NTS1.0-Administrator]Apr 9, 2013D - End
					
					
					RateInfo rateInfo = getRateInfo(amsSubGroup.getCurrencyCode(), payzaInfo.getBaseCurrency());
					if(rateInfo == null) {
						return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
					}
					rateType = rateInfo.getRateType();
					convertRate = rateInfo.getRate();
				}
			}
		}
			
		//save information into amsdeposit		
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		AmsDeposit amsDeposit = new AmsDeposit();
		String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
		amsDeposit.setDepositId(depositId);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, payzaInfo.getCustomerId());
		if(amsCustomer == null) {
			log.warn("depositNeteller Cannot find customerInfo with Id: " + payzaInfo.getCustomerId());
			return IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		}		
		amsDeposit.setAmsCustomer(amsCustomer);
		amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDeposit.setCurrencyCode(payzaInfo.getCurrencyCode());
		amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
		amsDeposit.setServiceType(payzaInfo.getServiceType());
		amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
		amsDeposit.setRate(convertRate.doubleValue());
		amsDeposit.setDepositAmount(payzaInfo.getAmount().doubleValue());
		amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.PAYZA);
		amsDeposit.setDepositRoute(payzaInfo.getDeviceType());
		if(amsAppDate != null) {
			amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
		} else {
			log.warn("depositPayza Cannot find config for Bussiness Date");			
		}
		amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
		amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));		
		amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
//		amsDeposit.setConfirmDate(null);//HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
		AmsDepositRef amsDepositRef = new AmsDepositRef();
		amsDepositRef.setDepositId(depositId);
		amsDepositRef.setEwalletType(payzaInfo.getPaymentMethod());
		amsDepositRef.setEwalletEmail(payzaInfo.getEmailAddress());
		amsDepositRef.setEwalletSecureId(payzaInfo.getApiPassword());
		amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDeposit.setAmsDepositRef(amsDepositRef);
		amsDepositRef.setAmsDeposit(amsDeposit);
		String subjectMail = "";
//		String templateMail  = "";		
		Map<String, String> mapSubjectMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + payzaInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);							
		try {
			// save deposit
			log.info("==============>Deposit depositPayza <================ ");
			log.info("Email address" + emailAddress + ", Password: " + apiPassword + ", depositId: " + depositId + ", Status of transaction" + IConstants.DEPOSIT_STATUS.IN_PROGRESS);
			getiAmsDepositDAO().save(amsDeposit);
			PayzaResponseInfo payzaResponseInfo = PayzaContext.getInstance().getPayzaResponse(payzalUrl, params);
			if(payzaResponseInfo != null) {
				log.info("depositPayza Response info of email " + emailAddress  + ", reference number: " + payzaResponseInfo.getReferenceNumber() + ", return code: " + payzaResponseInfo.getReturnCode() + ", descriptio: " + payzaResponseInfo.getDescription());
				if(IConstants.PAYMENT_PAYZA_ERROR_CODE.SUCCESS.equals(payzaResponseInfo.getReturnCode()) && !StringUtils.isBlank(payzaResponseInfo.getReferenceNumber())) { 
					// success					
					log.debug("depositPayza Deposit to Neteller payment gateway success");
					amsDepositRef.setGwRefId(payzaResponseInfo.getReferenceNumber());
					amsDeposit.setAmsDepositRef(amsDepositRef);
					amsDeposit.setDepositCompletedDate(amsAppDate.getId().getFrontDate());
					amsDeposit.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));					
					amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.FINISHED);				
					getiAmsDepositDAO().attachDirty(amsDeposit);
					getiAmsDepositRefDAO().save(amsDepositRef);
//					DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
					// send mail to customer about deposit successful
					String language = amsCustomer.getDisplayLanguage();
					if(language == null || StringUtils.isBlank(language)) {
						language = IConstants.Language.ENGLISH;
					}
					
					sendmailDepositSuccess(amsCustomer, depositId, payzaInfo.getAmount(), payzaInfo.getCurrencyCode(), language, IConstants.PAYMENT_METHOD.PAYZA);
					
					// insert data into table CASH_FLOW					
					if(IConstants.RATE_TYPE.BID.equals(rateType)) {
						convertedAmount = convertRate.multiply(payzaInfo.getAmount());
					} else if(IConstants.RATE_TYPE.ASK.equals(rateType)) {
						convertedAmount = payzaInfo.getAmount().multiply(MathUtil.parseBigDecimal(1).divide(convertRate, IConstants.FRONT_OTHER.SCALE_ALL, BigDecimal.ROUND_DOWN));
						convertedAmount = convertedAmount.divide(MathUtil.parseBigDecimal(1), symbolDecimal, symbolRound);
					} else {
						convertedAmount = payzaInfo.getAmount();
					}
					// update cash balance 
					AmsCashBalance amsCashBalance = updateAmsCashBalance(customerId, payzaInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.FALSE);
					Double balance = amsCashBalance.getCashBalance();
					insertCashFlow(depositId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL, convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.DEPOSIT_ID, payzaInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue());

					// check service type 
					// if service type != AMS, insert into table AMS_TRANSFER_MONEY
					if(!IConstants.SERVICES_TYPE.AMS.equals(payzaInfo.getServiceType())) {
						Boolean hasTransfer = false;
						AmsTransferMoney amsTransferMoney = null;
						// insert AMS_TRANSFER MONEY
						
						log.info("Transfer money from AMS " + payzaInfo.getAmount() + " -> " + payzaInfo.getServiceType() + " with Amount = " + convertedAmount);
						TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
						String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
						transferMoneyInfo.setTransferMoneyId(transferMoneyId);
						transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
						transferMoneyInfo.setCustomerId(customerId);
						transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
						transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
						transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
						transferMoneyInfo.setTransferTo(payzaInfo.getServiceType());
						transferMoneyInfo.setTransferMoney(payzaInfo.getAmount().doubleValue()); // will confirmed BA
//						transferMoneyInfo.setWlRefId(payzaResponseInfo.getReferenceNumber());
						transferMoneyInfo.setRate(convertRate);
						transferMoneyInfo.setCurrencyCode(payzaInfo.getCurrencyCode());
						transferMoneyInfo.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
						transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
						transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
						transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
						transferMoneyInfo.setSourceId(depositId);
						transferMoneyInfo.setDestinationAmount(payzaInfo.getAmount().doubleValue());
						transferMoneyInfo.setDestinationCurrencyCode(payzaInfo.getCurrencyCode());
						amsTransferMoney = insertTransferMoney(transferMoneyInfo);
						// checking service type 
						if(IConstants.SERVICES_TYPE.FX.equals(payzaInfo.getServiceType())) {
							log.info("depositNeteller serviceType = FX with customerId: " + payzaInfo.getCustomerId());
							// if service type == FX then update balance on MT4					
							Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
							String description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETELLER);
							Integer resultUpdateBalance = MT4Manager.getInstance().depositBalance(customerServiceId, payzaInfo.getAmount().doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
							if(resultUpdateBalance.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)) {
								//  wait for BA via promotion
								amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
								amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
								amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
								
//								log.info("[start] update net deposit for customerId = " + amsCustomer.getCustomerId());
//								updateNetDeposit(payzaInfo.getServiceType(), payzaInfo.getCustomerId(), payzaInfo.getCurrencyCode(), payzaInfo.getAmount().doubleValue());
//								log.info("[end] update net deposit for customerId = " + amsCustomer.getCustomerId());
								
								log.info("update balance to mt4 account " + customerId +  " successful");
								log.info("[start] checking NET_DEPOSIT of account " + customerId);
								BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
								AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
								amsCashBalanceFxId.setCurrencyCode(amsDeposit.getCurrencyCode());
								amsCashBalanceFxId.setCustomerId(customerId);
								amsCashBalanceFxId.setServiceType(IConstants.SERVICES_TYPE.FX);
								AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
								if(amsCashBalanceFx != null) {
									netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
								}
								log.info("[end] checking NET_DEPOSIT of account " + customerId + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
								BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
								Integer scale = new Integer(0);
								Integer rounding = new Integer(0);
								CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + amsDeposit.getCurrencyCode());
								if(currencyInfo != null) {
									scale = currencyInfo.getCurrencyDecimal();
									rounding = currencyInfo.getCurrencyRound();
								}	
								AmsPromotion amsPromotion = null;
								totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
								log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
								if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
									log.info("[start] check promotion for depositId: " + depositId);
									//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, payzaInfo.getWlCode());
									amsPromotion = amsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, payzaInfo.getSubGroupId());
									
									if(amsPromotion != null) {
										log.info("currently, depositId: " + depositId + " will be received promotion");
										log.info("process promotion for customerId: " + payzaInfo.getCustomerId());
										//bonusAmount = getiPromotionManager().getBonusAmount(payzaInfo.getAmount(), payzaInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, payzaInfo.getWlCode());
										//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
										BigDecimal baseAmount = getBonusByNetDeposit(payzaInfo.getAmount(), netDepositAmount, amsPromotion.getKind());
										
										if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
											bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, payzaInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, payzaInfo.getSubGroupId());
											//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
										} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
											bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, payzaInfo.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, payzaInfo.getSubGroupId(), netDepositAmount, customerId, true);
										}
										
										//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
										if(bonusAmount != null && bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
											log.info("depositId: " + depositId + " will be recieved " + bonusAmount + " " + payzaInfo.getCurrencyCode());
											log.info("send request deposit to customerId: " + payzaInfo.getCustomerId() + ", amount: " + bonusAmount);
											description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETELLER);
											Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
											if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
												log.info("update credit with amount: " + bonusAmount + " is successful");
												getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), bonusAmount.doubleValue(), depositId, payzaInfo.getCurrencyCode());
												//send mail bonus
												log.info("[start] Send mail to customer about bonus amount " + depositId);
												
												sendmailDepositBonus(amsDeposit, amsCustomer, bonusAmount, language);
												log.info("[end] Send mail to customer about bonus amount" + depositId);
											}
											amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
											amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
										}
									} else {
										log.info("cannot find promotion for depositId: " + depositId);
									}	
								}

								//[NTS1.0-Administrator]Sep 27, 2012A - Start - checking promotion for losscut 
								//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, payzaInfo.getWlCode());
								amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, IConstants.SERVICES_TYPE.FX, payzaInfo.getSubGroupId());
								if(amsPromotion != null) {									
									log.info("Find promotion for losscut");
									log.info("[start] process promotion losscut for customerId: " + payzaInfo.getCustomerId());
									log.info("Checking losscutflag of customer " + payzaInfo.getCustomerId() + " and customer service id " + customerServiceId);
									if(toAmsCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(toAmsCustomerService.getLosscutFlg())) {
										// get lastest deposit of this customer
										log.info("customerId " + customerServiceId + " is losscut");
										log.info("[start] get deposit lastest for customerId " + payzaInfo.getCustomerId());
//										String losscutDatetime = DateUtil.toString(toAmsCustomerService.getLosscutDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DB);
										AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(payzaInfo.getCustomerId(), toAmsCustomerService.getLosscutDatetime());
										if(amsDepositLastest != null) {
											log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
											AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(customerId, amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
											log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
											if(amsPromotionCustomer != null) {
												log.info("CustomerID: " + customerId + " recieved promotion losscut");
												toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
												getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
											} else {
												log.info("Lastest deposit of customerServiceId = " + customerServiceId + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
												log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceId );
												//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, payzaInfo.getWlCode());
												BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, IConstants.SERVICES_TYPE.FX, payzaInfo.getSubGroupId());
												log.info("customerServiceId " + customerServiceId + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
												if(losscutAmountBonus != null && losscutAmountBonus.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
													log.info("[start] send request deposit to credit for customerServiceId " + customerServiceId);											
													description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
													Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
													if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
														log.info("CurrencyCode of customerId " + amsDepositLastest.getCurrencyCode());
														if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
															log.info("Because currencyCode of customerId " + customerServiceId + " is japan so system will not send mail to this account");
														} else {
															if(IConstants.Language.JAPANESE.equals(language)) {
																language = IConstants.Language.ENGLISH;
															}
															sendmailLosscut(amsDepositLastest, amsCustomer, losscutAmountBonus, language, payzaInfo.getCurrencyCode()); 
														}
														
														log.info("customer service id " + customerServiceId + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
														log.info("[start] Insert data into PROMOTION CUSTOMER");
														getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), payzaInfo.getCurrencyCode());													
														log.info("[end] Insert data into PROMOTION CUSTOMER");
													} else {
														log.warn("Cannot plus credit for customer service id " + customerServiceId + " because returnCode = " + resultUpdateCredit);
													}
												}
												
												log.info("[start] update losscutFlag of customerServiceId " + customerServiceId);
												toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
												getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
												log.info("[end] update losscutFlag of customerServiceId " + customerServiceId);
												log.info("[end] send request deposit to credit for customerServiceId " + customerServiceId);
												log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceId );
											}
											
											
										} else {
											log.info("customerId " + customerServiceId + " no longer deposit into system");
											toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
											getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
										}
										log.info("[end] get deposit lastest for customerId " + payzaInfo.getCustomerId());
									}
									
									log.info("[end] process promotion losscut for customerId: " + payzaInfo.getCustomerId());
								} else {
									log.info("Cannot find promotion for losscut");
								}
								//[NTS1.0-Administrator]Sep 27, 2012A - End
								getiAmsTransferMoneyDAO().merge(amsTransferMoney);
								hasTransfer = true;
							} else {
								result = IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR;
								amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
								getiAmsTransferMoneyDAO().merge(amsTransferMoney);
								
								//  send mail to OM via occur error on MT4						
								log.warn("depositNeteller [start] Send mail to OM about occur MT4 Error" );
								Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
								
								String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
								String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
								if(omDescription == null) {
									omDescription = "";
								}
								String errorOn = "depositNeteller function depositNeteller";													
								String template = IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
								
								subjectMail = mapSubjectMail.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
								mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
								log.warn("depositNeteller [end] Send mail to OM about occur MT4 Error" );
							}
							
						} else {
							amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
							amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
							amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
							getiAmsTransferMoneyDAO().merge(amsTransferMoney);
							hasTransfer = true;
						}
						if(hasTransfer) {
							
							log.info("[start] send topic for Refresh Balance Info of BO");
							try {									
								BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, payzaInfo.getAmount(), 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
								jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
							} catch(Exception ex) {
								log.error(ex.getMessage(), ex);
							}		
							log.info("[end] send topic for Refresh Balance Info of BO");
							
							// deduct ams balance for insert cashflow follow 7.2.3 NAF401
							Double amsBalance = balance - convertedAmount.doubleValue();
							
							// save cashflow for deduct ams balance
							insertCashFlow(transferMoneyId, null, customerId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 0 - convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, payzaInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, amsBalance, convertRate.doubleValue());
							// save cashflow for plus fx balance
							if(IConstants.SERVICES_TYPE.FX.equals(payzaInfo.getServiceType())) {
								// get balanceInfo from MT4
//								BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
								BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, payzaInfo.getCurrencyCode());
								balance = new Double(0);
								if(balanceInfo != null) {
									balance = balanceInfo.getBalance();
								}
							} else {
								// get balanceInfo from other balance
								AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
								amsCashBalanceId.setCurrencyCode(payzaInfo.getCurrencyCode());
								amsCashBalanceId.setCustomerId(customerId);
								amsCashBalanceId.setServiceType(payzaInfo.getServiceType());						
								AmsCashBalance amsCashBalanceServiceType = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId); 
								balance = new Double(0);
								if(amsCashBalanceServiceType != null) {
									balance = amsCashBalanceServiceType.getCashBalance();
								}
								balance = balance + payzaInfo.getAmount().doubleValue();
							}
							// update balance for destination service type 
							// last balance = balance + transfer money
							// cashflow amount = transfer money						
//							balance = balance + payzaInfo.getAmount().doubleValue();						
							// start insert cashflow for other servicetype except AMS
							log.info("insert cash flow for transfer money to service type for customerId: " + customerId + ", currencyCodeService: " + currencyCodeService);
							
							insertCashFlow(transferMoneyId, customerId, customerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, payzaInfo.getAmount().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, payzaInfo.getCurrencyCode(), payzaInfo.getServiceType(), balance, convertRate.doubleValue());
							// update balance for AMS
							updateAmsCashBalance(customerId, payzaInfo.getBaseCurrency(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE); // deduct Flag = TRUE mean subtract balance of customer 
							//[TDSBO1.0-Administrator]Oct 31, 2012A - Start - Apply deposit bonus that are based on NET_DEPOSIT for all services 
							// update balance for other servicetype except AMS
//							if(!IConstants.SERVICES_TYPE.FX.equals(payzaInfo.getServiceType())) {
							updateAmsCashBalance(customerId, payzaInfo.getCurrencyCode(), payzaInfo.getServiceType(), payzaInfo.getAmount().doubleValue(), bonusAmount.doubleValue(), Boolean.FALSE);
//							}
							
							//[TDSBO1.0-Administrator]Oct 31, 2012A - End
							
								
						}
						
					} else {
						// do nothing						
					}	
					log.info("[start] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
					updateRemarkCustomerSurvey(customerId, amsDeposit.getDepositMethod());
					log.info("[end] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
					result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
				} else {
					// failure
					//result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
					result = IConstants.DEPOSIT_UPDATE_RESULT.CANCEL;
					log.info("depositPayza Deposit fail because payment gateway not accept");				
					// update status deposit 
					log.info("depositPayza [start] update status to fail ");
					amsDeposit.setErrorCode(StringUtil.toString(payzaResponseInfo.getReturnCode()));
					amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.CANCEL);				
					getiAmsDepositDAO().attachDirty(amsDeposit);
					log.info("depositPayza [end] update status to fail ");
					log.info("[start] send mail deposit bonus for deposit id " + depositId);		
					String language = IConstants.Language.ENGLISH;
					if(amsCustomer.getDisplayLanguage() != null && !StringUtils.isBlank(amsCustomer.getDisplayLanguage())) {
						language = amsCustomer.getDisplayLanguage();
					}
					
					String reason = new StringBuffer(payzaResponseInfo.getDescription() == null ? "" : payzaResponseInfo.getDescription()).append(" : ").append(payzaResponseInfo.getReturnCode() == null ? "" : payzaResponseInfo.getReturnCode()).toString();
					sendmailDepositCancel(amsDeposit, amsCustomer, payzaInfo.getAmount().doubleValue(), reason, language,
							IConstants.PAYMENT_METHOD.PAYZA);
				}
			}
			
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
			ex.printStackTrace();
			log.error("depositPayza Error on deposit by neteller", ex);
			log.error("depositPayza [start] Send mail to OM about occur MT4 Error" );
			Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
			
			String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
			String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
			if(omDescription == null) {
				omDescription = "";
			}
			String errorOn = "function depositPayza ";									
			String template = IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
			Map<String, String> mapMailSubject = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + payzaInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);
			subjectMail = mapMailSubject.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
			mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
			log.error("depositPayza [end] Send mail to OM about occur MT4 Error" );
		}				
		
		
		
		
		return result;
	}
	
	/**
	 * sendmailDepositSuccess　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailDepositSuccess(AmsCustomer amsCustomer, String depositId, BigDecimal depositAmount, String currencyCode, String language, Integer method) {
		log.info("[start] send mail about deposit successful");
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(balanceManager.formatNumber(depositAmount, currencyCode));
		amsMailTemplateInfo.setDepositCurrency(balanceManager.getCurrencyCode(currencyCode, language));
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_MMDDYYYY));
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositId(depositId);
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
		amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(method)));
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about deposit successful");
	}

	/**
	 * deposit on liberty
	 * @param libertyInfo
	 * @return
	 * @auth Tran.Duc.Nam
	 * @CrDate Oct 23, 2012
	 * @MdDate
	 */
	public String depositLiberty(LibertyInfo libertyInfo) {
		log.info("[start] depositLiberty");
//		String url = "";
		//save information into amsdeposit		
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		// get convert rate from AMS and target service type
		BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
		
//		Integer symbolRound = new Integer(0);
//		Integer symbolDecimal = new Integer(0);	
//		Integer rateType = new Integer(-1);		
//		String customerServiceId = "";
		
		String customerId = libertyInfo.getCustomerId();	
		//BigDecimal cashFlowAmount = MathUtil.parseBigDecimal(0);
		String currencyCodeTargetServiceType = "";
		AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, libertyInfo.getServiceType());
		if(toAmsCustomerService != null) {

			// get subgroup of customer services for get currency code
			AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
			if(amsSubGroup != null) {
				currencyCodeTargetServiceType = amsSubGroup.getCurrencyCode();
				if(libertyInfo.getBaseCurrency().equals(currencyCodeTargetServiceType)) {
					convertRate = MathUtil.parseBigDecimal(1);
				} else {
					// set symbolName = fromCurrency + toCurrency
					convertRate = getConvertRateOnFrontRate(amsSubGroup.getCurrencyCode(), libertyInfo.getBaseCurrency(), IConstants.FRONT_OTHER.SCALE_ALL);
					
//					String symbolName = amsSubGroup.getCurrencyCode() + libertyInfo.getBaseCurrency();
//					Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//					RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//					if(rateInfo != null) {
//						// get symbol rounding from table AMS_SYS_SYMBOL
//						FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//						if(amsSysSymbol != null) {
//							symbolRound = amsSysSymbol.getSymbolRound();
//							symbolDecimal = amsSysSymbol.getSymbolDecimal();
//						}
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//						convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
////						rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//					} else {	
//						symbolName = libertyInfo.getBaseCurrency() + amsSubGroup.getCurrencyCode();
//						rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
////							rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//						} else {
//							log.warn("Cannot find rate of symbolName " + symbolName);
//							//return IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE;
//						}
//						
//					}
					if(convertRate == null || convertRate.compareTo(new BigDecimal("0")) <= 0) {
						return IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE;
					}
					
				}
			}
		}

		log.info("[start] insert ams deposit");
		AmsDeposit amsDeposit = new AmsDeposit();
		String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
		amsDeposit.setDepositId(depositId);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(libertyInfo.getCustomerId());
		amsDeposit.setAmsCustomer(amsCustomer);
		amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDeposit.setCurrencyCode(libertyInfo.getCurrencyCode());
		amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
		amsDeposit.setServiceType(libertyInfo.getServiceType());
		amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
		amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.LIBERTY);
		amsDeposit.setDepositRoute(libertyInfo.getDeviceType());
		amsDeposit.setRate(convertRate.doubleValue());
		amsDeposit.setDepositAmount(libertyInfo.getAmount().doubleValue());
		if(amsAppDate != null) {
			amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
		} else {
			log.warn("Cannot find config for Bussiness Date");			
		}
		amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
		amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));		
		amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
//		amsDeposit.setConfirmDate(null);//HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
		AmsDepositRef amsDepositRef = new AmsDepositRef();
		amsDepositRef.setDepositId(depositId);
		amsDepositRef.setEwalletType(IConstants.PAYMENT_METHOD.LIBERTY);
		amsDepositRef.setEwalletAccNo(libertyInfo.getAccountNumber());
		amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDepositRef.setEwalletApiName(libertyInfo.getApiName());
		amsDepositRef.setEwalletSecureWord(libertyInfo.getSecurityWord());
		amsDeposit.setAmsDepositRef(amsDepositRef);				
		log.info("==============>Deposit Liberty<================ ");
		log.info("customerId " + libertyInfo.getCustomerId() + ", Amount: " + libertyInfo.getAmount() + ", depositId: " + depositId + ", Status of transaction" + IConstants.DEPOSIT_STATUS.IN_PROGRESS);
		// save deposit into db
		getiAmsDepositDAO().save(amsDeposit);
		log.info("[start] insert ams deposit");

		amsDepositRef.setAmsDeposit(amsDeposit);
		getiAmsDepositRefDAO().save(amsDepositRef);

		log.info("[start] depositLiberty");
		return depositId;
	}

	/**
	 * deposit through liberty SCI (shopping cart interface)　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 26, 2013
	 */
	public String depositLibertySCI(LibertyInfo libertyInfo) {
		log.info("[start] depositLibertySCI");
		String url = "";
		String depositId = depositLiberty(libertyInfo);
		
		Map<String, String> mapPayonlineConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + libertyInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.LIBERTY_CONFIG);
		Map<String, String> mapFrontEnd = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + libertyInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_END);
		//get url to redirect
		if (depositId != null) {
			url = LibertyContext.getInstance().getLibertySystem(depositId, libertyInfo, mapPayonlineConfig, mapFrontEnd, libertyInfo.getCurrencyCode());
		}

		log.info("[end] depositLibertySCI");
		return url;
	}
	
	/**
	 * update deposit ref　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguye.ngoc
	 * @CrDate Feb 27, 2013
	 */
	public Integer updateDepositRef(String depositId, String ewalletAccountNumber) {
		log.info("[start] updateDepositRef");
		try {
			AmsDepositRef amsDepositRef = getiAmsDepositRefDAO().findById(AmsDepositRef.class, depositId);
			if (amsDepositRef == null) {
				return IConstants.DEPOSIT_STATUS.FAILURE;
			}
			amsDepositRef.setEwalletAccNo(ewalletAccountNumber);
			getiAmsDepositRefDAO().merge(amsDepositRef);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		log.info("[end] updateDepositRef");
		return IConstants.DEPOSIT_STATUS.FINISHED;
	}
	
	public String depositNetpay(CreditCardInfo creditCardInfo) {
		log.info("[start] depositNetpay");
		//save information into amsdeposit		
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		// get convert rate from AMS and target service type
		BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
		
//		Integer symbolRound = new Integer(0);
//		Integer symbolDecimal = new Integer(0);	
		
		String customerId = creditCardInfo.getCustomerId();	
		//BigDecimal cashFlowAmount = MathUtil.parseBigDecimal(0);
		String currencyCodeTargetServiceType = "";
		AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, creditCardInfo.getServiceType());
		String currencyCode = creditCardInfo.getCurrencyCode();

		Integer cardType = creditCardInfo.getCcType();
		if (cardType != null && cardType.intValue() != IConstants.CREDIT_CARD_PAYMENT.CARD_TYPE.CUP) {
			if(toAmsCustomerService != null) {
				// get subgroup of customer services for get currency code
				AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
				if(amsSubGroup != null) {
					currencyCodeTargetServiceType = amsSubGroup.getCurrencyCode();
					if(currencyCode != null && currencyCode.equals(currencyCodeTargetServiceType)) {
						convertRate = MathUtil.parseBigDecimal(1);
					} else {
						// set symbolName = fromCurrency + toCurrency
//						String symbolName = amsSubGroup.getCurrencyCode() + currencyCode;
//						Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
						
						convertRate = getConvertRateOnFrontRate(amsSubGroup.getCurrencyCode(), currencyCode, IConstants.FRONT_OTHER.SCALE_ALL);
						
//						RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//						} else {	
//							symbolName = currencyCode + amsSubGroup.getCurrencyCode();
//							rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//							if(rateInfo != null) {
//								// get symbol rounding from table AMS_SYS_SYMBOL
//								FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//								if(amsSysSymbol != null) {
//									symbolRound = amsSysSymbol.getSymbolRound();
//									symbolDecimal = amsSysSymbol.getSymbolDecimal();
//								}
//								log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//								convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//								log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//							} else {
//								log.warn("Cannot find rate of symbolName " + symbolName);
//							}
//						}
					}
						
				}
			}
		} else {
			// in case deposit by CUP
			RateInfo rateInfoCny = getLastestRate(currencyCode + IConstants.CURRENCY_CODE.CNY);
			
			convertRate = rateInfoCny.getRate();
		}

		return insertDeposit(creditCardInfo, amsAppDate, convertRate, currencyCode);
	}

	/**
	 * insertDeposit　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 7, 2013
	 */
	private String insertDeposit(CreditCardInfo creditCardInfo, SysAppDate amsAppDate, BigDecimal convertRate, String currencyCode) {
		AmsDeposit amsDeposit = new AmsDeposit();
		String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
		amsDeposit.setDepositId(depositId);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(creditCardInfo.getCustomerId());
		amsDeposit.setAmsCustomer(amsCustomer);
		amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDeposit.setCurrencyCode(currencyCode);
		amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
		amsDeposit.setServiceType(creditCardInfo.getServiceType());
		amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
		amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
		amsDeposit.setDepositGateway(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY_VALUE);
		amsDeposit.setDepositRoute(creditCardInfo.getDeviceType());
		amsDeposit.setRate(convertRate.doubleValue());
		amsDeposit.setDepositAmount(creditCardInfo.getAmount().doubleValue());
		if(amsAppDate != null) {
			amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
		} else {
			log.warn("Cannot find config for Bussiness Date");			
		}
		amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
		amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));		
		amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		AmsDepositRef amsDepositRef = new AmsDepositRef();
		amsDepositRef.setDepositId(depositId);
		amsDepositRef.setCcAddress(creditCardInfo.getAddress());
		amsDepositRef.setCcCity(creditCardInfo.getCity());
		amsDepositRef.setCcCvv(creditCardInfo.getCcCvv());
		amsDepositRef.setCcEmail(creditCardInfo.getEmail());
		amsDepositRef.setCcExpiredDate(creditCardInfo.getExpiredDate());
		amsDepositRef.setCcHolderName(creditCardInfo.getCcHolderName());
		amsDepositRef.setCcNo(creditCardInfo.getCcNo());
		amsDepositRef.setCcPhone(creditCardInfo.getPhone());
		amsDepositRef.setCcState(creditCardInfo.getState());
		amsDepositRef.setCcType(creditCardInfo.getCcType());
		amsDepositRef.setCcZipCode(creditCardInfo.getZipCode());
		amsDepositRef.setEwalletType(IConstants.PAYMENT_METHOD.CREDIT_CARD);
		amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDepositRef.setCountryId(creditCardInfo.getCountryId());
		amsDeposit.setAmsDepositRef(amsDepositRef);				
		try {
			log.info("==============>Deposit Payonline System<================ ");
			log.info("customerId " + creditCardInfo.getCustomerId() + ", Amount: " + creditCardInfo.getAmount() + ", depositId: " + depositId + ", Status of transaction" + IConstants.DEPOSIT_STATUS.IN_PROGRESS);
			// save deposit into db
			getiAmsDepositDAO().save(amsDeposit);
			amsDepositRef.setAmsDeposit(amsDeposit);
			getiAmsDepositRefDAO().save(amsDepositRef);
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}

		log.info("[end] depositNetpay");
		return depositId;
	}

	public Integer depositLibertySuccess(String depositId, String customerId, LibertyInfo libertyInfo){
		log.info("[start] depositLibertySuccess");
		Integer result = 0;
		try{
		// Call Liberty transfer API
		Map<String, String> mapPayonlineConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + libertyInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.LIBERTY_CONFIG);
		String merchantId = mapPayonlineConfig.get(IConstants.LIBERTY_CONFIGURATION.MERCHANT_ID);
		
		TimeZone defaultTimeZone = TimeZone.getDefault();
		HistoryItem historyItem = LibertyTransferUtil.transfer(libertyInfo.getAccountNumber(), libertyInfo.getApiName(), libertyInfo.getSecurityWord(), merchantId, libertyInfo.getAmount(), libertyInfo.getBaseCurrency());

		// roll back default time zone (Get before run liberty) 
		TimeZone.setDefault(defaultTimeZone);
		if(historyItem != null){
			//result = depositUpdateStatus(depositId, customerId, libertyInfo.getBaseCurrency(), libertyInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.FINISHED, null, libertyInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY);
			result = depositUpdateStatus(depositId, customerId, libertyInfo.getBaseCurrency(), libertyInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.FINISHED, null, libertyInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, IConstants.PAYMENT_METHOD.LIBERTY);
			AmsDepositRef amsDepositRef = getiAmsDepositRefDAO().findById(AmsDepositRef.class, depositId);
			amsDepositRef.setGwRefId(Long.toString(historyItem.getBatch()));
			getiAmsDepositRefDAO().merge(amsDepositRef);
		}else {
			//result = depositUpdateStatus(depositId, customerId, libertyInfo.getBaseCurrency(), libertyInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.CANCEL, null, libertyInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY);
			result = depositUpdateStatus(depositId, customerId, libertyInfo.getBaseCurrency(), libertyInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.CANCEL, null, libertyInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, IConstants.PAYMENT_METHOD.LIBERTY);
		}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		log.info("[end] depositLibertySuccess");
		return result;
	}

	/**
	 * depositNetpaySilentPost　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Feb 20, 2013
	 */
	public Integer depositNetpaySilentPost(String depositId, String customerId, CreditCardInfo creditCardInfo) {
		log.info("[start] depositNetpaySilentPost");
		Integer result = 0;
		try{
			String cardNum = creditCardInfo.getCcNo();
			String expiredDate = creditCardInfo.getExpiredDate();
			
			if (expiredDate == null || expiredDate.length() < 4) {
				result = depositUpdateStatus(depositId, customerId, creditCardInfo.getCurrencyCode(), creditCardInfo.getCurrencyCode(), IConstants.DEPOSIT_STATUS.CANCEL, null, creditCardInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETPAY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETPAY, IConstants.PAYMENT_METHOD.CREDIT_CARD);
				return result;
			}

			String expiredYear = expiredDate.substring(0, 4);
			String expiredMonth = expiredDate.substring(4);
			if (expiredMonth.startsWith("0")) {
				expiredMonth = expiredMonth.replace("0", "");
			}
			String holderName = creditCardInfo.getCcHolderName();
			int typeCredit = IConstants.NETPAY.TYPE_CREDIT.DEBIT;
			int currency = IConstants.NETPAY.CURRENCY.USD;
			if(creditCardInfo.getCurrencyCode() != null){
				currency = getCurrencyCodeInt(creditCardInfo.getCurrencyCode());
			}
			String cvv2 = creditCardInfo.getCcCvv();
			String cardHolderMail = creditCardInfo.getEmail();
			String phoneNumber = creditCardInfo.getPhone();

			String amount = creditCardInfo.getAmount().toString();
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			String ipAddress = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					ipAddress = frontUserOnline.getIpAddress();
				} else {
					ipAddress = "";
				}
			}

			int cardType = creditCardInfo.getCcType();

			String billingAddress1 = creditCardInfo.getAddress() == null ? "" : creditCardInfo.getAddress();
			String billingCity = creditCardInfo.getCity() == null ? "" : creditCardInfo.getCity() ;
			String billingZipCode = creditCardInfo.getZipCode() == null ? "" : creditCardInfo.getZipCode();
			String billingState = creditCardInfo.getState() == null ? "" : creditCardInfo.getState();
			String billingCountry = "";
			Integer countryId = creditCardInfo.getCountryId();
			if (countryId != null) {
				Map<Integer, String> mapCountry = MasterDataManagerImpl.getInstance().getMapCountryCode();
				billingCountry = mapCountry == null ? "" : mapCountry.get(countryId);
			}
			String personalNo = creditCardInfo.getCcDriverNo();

			Map<String, String> mapNetpayConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + creditCardInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.NETPAY_CONFIG);

			// Call Liberty transfer API
			String reply =  NetPay.getInstance().silentPost(mapNetpayConfig, cardType, cardNum, expiredMonth, expiredYear, holderName, typeCredit, amount, currency, cvv2, cardHolderMail, personalNo, phoneNumber, ipAddress, billingAddress1, billingCity, billingZipCode, billingState, billingCountry);
			
			log.info("[end] depositNetpaySilentPost");
			if (IConstants.NETPAY.ERROR_CODE.SUCCESS.equalsIgnoreCase(reply)) {
//				result = depositUpdateStatus(depositId, customerId, creditCardInfo.getCurrencyCode(), creditCardInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.FINISHED, null, creditCardInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, creditCardInfo.getSubGroupId(), IConstants.PAYMENT_METHOD.LIBERTY);
				return 0;
			} else {
//				result = depositUpdateStatus(depositId, customerId, creditCardInfo.getBaseCurrency(), creditCardInfo.getBaseCurrency(), IConstants.DEPOSIT_STATUS.CANCEL, null, creditCardInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, creditCardInfo.getSubGroupId(), IConstants.PAYMENT_METHOD.LIBERTY);
				result = depositUpdateStatus(depositId, customerId, creditCardInfo.getCurrencyCode(), creditCardInfo.getCurrencyCode(), IConstants.DEPOSIT_STATUS.CANCEL, null, creditCardInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETPAY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETPAY, IConstants.PAYMENT_METHOD.CREDIT_CARD);
				return result;
			}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			result = depositUpdateStatus(depositId, customerId, creditCardInfo.getCurrencyCode(), creditCardInfo.getCurrencyCode(), IConstants.DEPOSIT_STATUS.CANCEL, null, creditCardInfo.getWlCode(), null, IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETPAY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETPAY, IConstants.PAYMENT_METHOD.CREDIT_CARD);
			return result;
		}	
	}

	/**
	 * Convert currency code from String to Int　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 20, 2013
	 */
	private int getCurrencyCodeInt(String currencyCode){
		int currencyInt = 1;
		if(IConstants.NETPAY.TRANS_CURRENCY.AUD.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.AUD;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.CAD.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.CAD;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.CHF.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.CHF;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.CNY.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.CNY;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.DKK.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.DKK;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.EUR.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.EUR;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.GBP.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.GBP;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.HUF.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.HUF;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.ILS.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.ILS;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.INR.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.INR;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.JPY.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.JPY;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.MXN.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.MXN;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.NOK.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.NOK;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.NZD.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.NZD;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.PLN.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.PLN;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.RUB.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.RUB;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.SEK.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.SEK;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.TRY.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.TRY;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.USD.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.USD;
		} else if (IConstants.NETPAY.TRANS_CURRENCY.ZAR.equalsIgnoreCase(currencyCode)){
			currencyInt = IConstants.NETPAY.CURRENCY.ZAR;
		}
		
		return currencyInt;
	}

	/**
	 * 　
	 * deposit on payonline system 
	 * @param amount money will deposit to MT4
	 * @param creditCardInfo money will deposit to payonline system
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 23, 2012
	 * @MdDate
	 */
	public String depositPayonlineSystem(CreditCardInfo creditCardInfo, BigDecimal amount, String publicKey) {
		String url = "";
		//save information into amsdeposit		
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		// get convert rate from AMS and target service type
		BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
//		Integer symbolRound = new Integer(0);
//		Integer symbolDecimal = new Integer(0);	
//		Integer rateType = new Integer(-1);
//		String customerServiceId = "";
		String customerId = creditCardInfo.getCustomerId();	
		//BigDecimal cashFlowAmount = MathUtil.parseBigDecimal(0);
		String currencyCodeTargetServiceType = "";
		AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, creditCardInfo.getServiceType());
		if(toAmsCustomerService != null) {
//			customerServiceId = toAmsCustomerService.getCustomerServiceId();
			// get subgroup of customer services for get currency code
			AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
			if(amsSubGroup != null) {
				currencyCodeTargetServiceType = amsSubGroup.getCurrencyCode();
				if(creditCardInfo.getBaseCurrency().equals(currencyCodeTargetServiceType)) {
					convertRate = MathUtil.parseBigDecimal(1);
				} else {
					// set symbolName = fromCurrency + toCurrency
					convertRate = getConvertRateOnFrontRate(amsSubGroup.getCurrencyCode(), creditCardInfo.getBaseCurrency(), IConstants.FRONT_OTHER.SCALE_ALL);
//					String symbolName = amsSubGroup.getCurrencyCode() + creditCardInfo.getBaseCurrency();
//					Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//					RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//					if(rateInfo != null) {
//						// get symbol rounding from table AMS_SYS_SYMBOL
//						FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//						if(amsSysSymbol != null) {
//							symbolRound = amsSysSymbol.getSymbolRound();
//							symbolDecimal = amsSysSymbol.getSymbolDecimal();
//						}
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//						convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//						log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
////						rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//					} else {	
//						symbolName = creditCardInfo.getBaseCurrency() + amsSubGroup.getCurrencyCode();
//						rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//						if(rateInfo != null) {
//							// get symbol rounding from table AMS_SYS_SYMBOL
//							FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//							if(amsSysSymbol != null) {
//								symbolRound = amsSysSymbol.getSymbolRound();
//								symbolDecimal = amsSysSymbol.getSymbolDecimal();
//							}
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//							convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//							log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
////							rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//						} else {
//							log.warn("Cannot find rate of symbolName " + symbolName);
//							return IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE;
//						}
//						
//					}
					
					if(convertRate == null || convertRate.compareTo(new BigDecimal("0")) <= 0) {
						return IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE;
					}
				}
			}
		}
					
		AmsDeposit amsDeposit = new AmsDeposit();
		String depositId = generateUniqueId(IConstants.UNIQUE_CONTEXT.DEPOSIT_CONTEXT);
		amsDeposit.setDepositId(depositId);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(creditCardInfo.getCustomerId());
		amsDeposit.setAmsCustomer(amsCustomer);
		amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDeposit.setCurrencyCode(creditCardInfo.getCurrencyCode());
		amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
		amsDeposit.setServiceType(creditCardInfo.getServiceType());
		amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
		amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
		amsDeposit.setDepositGateway(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.PAYONLINE_VALUE);
		amsDeposit.setDepositRoute(creditCardInfo.getDeviceType());
		amsDeposit.setRate(convertRate.doubleValue());
		amsDeposit.setDepositAmount(amount.doubleValue());
		if(amsAppDate != null) {
			amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
		} else {
			log.warn("Cannot find config for Bussiness Date");			
		}
		amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
		amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));		
		amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		
//		amsDeposit.setConfirmDate(null);//HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
		
		String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
		String encryptCcNo = "";
		String encryptCcCvv = "";
		try {
			encryptCcNo = Cryptography.encrypt(creditCardInfo.getCcNo(), privateKey, publicKey);
			encryptCcCvv = Cryptography.encrypt(creditCardInfo.getCcCvv(), privateKey, publicKey);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		AmsDepositRef amsDepositRef = new AmsDepositRef();
		amsDepositRef.setDepositId(depositId);
		amsDepositRef.setCcAddress(creditCardInfo.getAddress());
		amsDepositRef.setCcCity(creditCardInfo.getCity());
		amsDepositRef.setCcCvv(encryptCcCvv);
		amsDepositRef.setCcEmail(creditCardInfo.getEmail());
		amsDepositRef.setCcExpiredDate(creditCardInfo.getExpiredDate());
		amsDepositRef.setCcHolderName(creditCardInfo.getCcHolderName());
		amsDepositRef.setCcNo(encryptCcNo);
		amsDepositRef.setCcPhone(creditCardInfo.getPhone());
		amsDepositRef.setCcState(creditCardInfo.getState());
		amsDepositRef.setCcType(creditCardInfo.getCcType());
		amsDepositRef.setCcZipCode(creditCardInfo.getZipCode());
		amsDepositRef.setEwalletType(IConstants.PAYMENT_METHOD.CREDIT_CARD);
		amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsDepositRef.setCountryId(creditCardInfo.getCountryId());
		amsDeposit.setAmsDepositRef(amsDepositRef);				
		try {
			log.info("==============>Deposit Payonline System<================ ");
			log.info("customerId " + creditCardInfo.getCustomerId() + ", Amount: " + creditCardInfo.getAmount() + ", depositId: " + depositId + ", Status of transaction" + IConstants.DEPOSIT_STATUS.IN_PROGRESS);
			// save deposit into db
			getiAmsDepositDAO().save(amsDeposit);
			amsDepositRef.setAmsDeposit(amsDeposit);
			getiAmsDepositRefDAO().save(amsDepositRef);
			
			Map<String, String> mapPayonlineConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + creditCardInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.PAY_ONLINE_CONFIG);
			Map<String, String> mapFrontEnd = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + creditCardInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_END);
			// get url that will redirect to
			url = PayonlineSystemContext.getInstance().getPayonlineSystem(depositId, creditCardInfo, mapPayonlineConfig, mapFrontEnd);
			
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}			
		return url;
		
	}
	public Integer depositPayonlineSuccess(String transactionId, String depositId, String customerId, String wlCode, String currencyCode, Integer status) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		Integer subGroupId = null;
		AmsDeposit amsDeposit = getiAmsDepositDAO().findById(AmsDeposit.class, depositId);
		if(amsDeposit != null) {
			if(IConstants.DEPOSIT_STATUS.IN_PROGRESS.equals(amsDeposit.getStatus())) {
				String subjectMail = "";				
				Map<String, String> mapSubjectMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);				
				AmsCustomer amsCustomer = amsDeposit.getAmsCustomer();
				if(amsCustomer != null) {										
					if(customerId.equals(amsCustomer.getCustomerId())) {
						//
						if(IConstants.DEPOSIT_STATUS.IN_PROGRESS.equals(amsDeposit.getStatus())) {
							subGroupId = accountManager.getSubGroupId(customerId, amsDeposit.getServiceType());
							SysAppDate amsAppDate = null;
							List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
							if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
								amsAppDate = listAmsAppDate.get(0);
							}
							log.info("payonlineSuccess depositId: " + amsDeposit.getDepositId() + " is inprogress");
							log.info("payonlineSuccess [Start] Log for TransactionId "+depositId);			
							log.info("payonlineSuccess amsDeposit.getDepositId() " + amsDeposit.getDepositId());
							log.info("payonlineSuccess amsDeposit.getCustomerId() " + customerId);				
							log.info("payonlineSuccess amsDeposit.getPayMethod()" + amsDeposit.getDepositMethod());
							log.info("payonlineSuccess amsDeposit.getDepositAmount()" + amsDeposit.getDepositAmount());
							log.info("payonlineSuccess amsDeposit.getDepositAcceptDate()" + amsDeposit.getDepositAcceptDate());
							log.info("payonlineSuccess customerInfo.getBaseCurrency()" + currencyCode);
							log.info("payonlineSuccess [End] TransactionId "+depositId);
							log.info("payonlineSuccess [start] update status for " + depositId);
							amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
							AmsDepositRef amsDepositRef = amsDeposit.getAmsDepositRef();
							if(amsDepositRef != null) {
								amsDepositRef.setGwRefId(transactionId);
							}				
							amsDeposit.setAmsDepositRef(amsDepositRef);		
							amsDeposit.setDepositCompletedDate(amsAppDate.getId().getFrontDate());
							amsDeposit.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
							amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.FINISHED);				
							getiAmsDepositDAO().attachDirty(amsDeposit);
							getiAmsDepositRefDAO().attachDirty(amsDepositRef);
							// get convert rate from AMS and target service type
							BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
							Integer symbolRound = new Integer(0);
							Integer symbolDecimal = new Integer(0);
							Integer rateType = new Integer(-1);
							String currencyCodeService = "";
							String customerServiceId = "";		
							BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
							AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, amsDeposit.getServiceType());
							if(toAmsCustomerService != null) {
								customerServiceId = toAmsCustomerService.getCustomerServiceId();
								AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
								if(amsSubGroup != null) {
									currencyCodeService = amsSubGroup.getCurrencyCode();
									if(currencyCode.equals(currencyCodeService)) {
										convertRate = MathUtil.parseBigDecimal(1);
									} else {
										
										//[NTS1.0-Administrator]Apr 9, 2013D - Start - remove get rate from DB 
										// set symbolName = fromCurrency + toCurrency
//										String symbolName =  currencyCodeService + currencyCode;
//										Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//										RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//										if(rateInfo != null) {
//											// get symbol rounding from table AMS_SYS_SYMBOL
//											FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//											if(amsSysSymbol != null) {
//												symbolRound = amsSysSymbol.getSymbolRound();
//												symbolDecimal = amsSysSymbol.getSymbolDecimal();
//											}
//											log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//											convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//											log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//											rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//										} else {	
//											 symbolName = currencyCode + currencyCodeService;
//											rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//											if(rateInfo != null) {
//												// get symbol rounding from table AMS_SYS_SYMBOL
//												FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//												if(amsSysSymbol != null) {
//													symbolRound = amsSysSymbol.getSymbolRound();
//													symbolDecimal = amsSysSymbol.getSymbolDecimal();
//												}
//												log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//												convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//												log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//												rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//											} else {
//												log.warn("Cannot find rate of symbolName " + symbolName);
//												return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//											}
//										}
										//[NTS1.0-Administrator]Apr 9, 2013D - End
									
										RateInfo rateInfo = getRateInfo(currencyCodeService, currencyCode);
										if(rateInfo == null) {
											return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
										}
										rateType = rateInfo.getRateType();
										convertRate = rateInfo.getRate();
									}
								}
							}
							if(IConstants.RATE_TYPE.BID.equals(rateType)) {
								convertedAmount = convertRate.multiply(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
							} else if(IConstants.RATE_TYPE.ASK.equals(rateType)) {
//								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, symbolDecimal, symbolRound));
								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, IConstants.FRONT_OTHER.SCALE_ALL, BigDecimal.ROUND_DOWN));
								convertedAmount = convertedAmount.divide(MathUtil.parseBigDecimal(1), symbolDecimal, symbolRound);
							} else {
								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount());
							}
							//[NatureForex1.0-HuyenMT]Sep 7, 2012A - Start 
							amsDeposit.setRate(convertRate.doubleValue()); //HuyenMT set rate for Bug #5549 	Bug-0.2.1(AMSBO)
							getiAmsDepositDAO().attachDirty(amsDeposit); //update ams deposit
							//[NatureForex1.0-HuyenMT]Sep 7, 2012A - End
							
							// update cash balance 
							AmsCashBalance amsCashBalance = updateAmsCashBalance(customerId, currencyCode, IConstants.SERVICES_TYPE.AMS, amsDeposit.getDepositAmount(), bonusAmsAmount.doubleValue(), Boolean.FALSE); // deduct Flag = false mean plus balance of customer
							Double balance = amsCashBalance.getCashBalance();
							insertCashFlow(depositId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL, amsDeposit.getDepositAmount(), IConstants.SOURCE_TYPE.DEPOSIT_ID, currencyCode, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue());
							// send mail to customer about deposit successful
							String language = amsCustomer.getDisplayLanguage();
							if(language == null || StringUtils.isBlank(language)) {
								language = IConstants.Language.ENGLISH;
							}

							sendmailDepositSuccess(amsCustomer, depositId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), currencyCodeService, language, IConstants.PAYMENT_METHOD.CREDIT_CARD);
										
							// check service type 
							// if service type != AMS, insert into table AMS_TRANSFER_MONEY
							if(!IConstants.SERVICES_TYPE.AMS.equals(amsDeposit.getServiceType())) {
								AmsTransferMoney amsTransferMoney = null;
								
								log.info("Transfer money from AMS " + amsDeposit.getDepositAmount() + " -> " + amsDeposit.getServiceType() + " with Amount = " + convertedAmount);
								// insert AMS_TRANSFER MONEY
								TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
								String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
								transferMoneyInfo.setTransferMoneyId(transferMoneyId);
								transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
								transferMoneyInfo.setCustomerId(customerId);
								transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
								transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
								transferMoneyInfo.setTransferTo(amsDeposit.getServiceType());
								transferMoneyInfo.setTransferMoney(amsDeposit.getDepositAmount());
//								transferMoneyInfo.setWlRefId(depositId);
								transferMoneyInfo.setRate(convertRate);
								transferMoneyInfo.setCurrencyCode(amsDeposit.getCurrencyCode());
								transferMoneyInfo.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
								transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setSourceId(depositId);
								transferMoneyInfo.setDestinationAmount(amsDeposit.getDepositAmount());
								transferMoneyInfo.setDestinationCurrencyCode(amsDeposit.getCurrencyCode());
								amsTransferMoney = insertTransferMoney(transferMoneyInfo);
								// checking service type 
								Boolean hasTransfer = false;
								if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
									log.info("serviceType = FX with customerId: " + customerId + ", customerServiceId: " + customerServiceId);
									// if service type == FX then update balance on MT4					
									Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
									String description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_PAYONLINE);
									Integer resultUpdateBalance = MT4Manager.getInstance().depositBalance(customerServiceId, amsDeposit.getDepositAmount(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
									if(resultUpdateBalance.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)) {
										//  wait for BA via promotion
										amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
										amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
										amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
										
//										log.info("[start] update net deposit for customerId = " + amsCustomer.getCustomerId());
//										updateNetDeposit(amsDeposit.getServiceType(), amsCustomer.getCustomerId(), amsDeposit.getCurrencyCode(), amsDeposit.getDepositAmount());
//										log.info("[end] update net deposit for customerId = " + amsCustomer.getCustomerId());
										
										
										log.info("update balance to mt4 account " + customerId +  " successful");
										log.info("[start] checking NET_DEPOSIT of account " + customerId);
										BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
										AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
										amsCashBalanceFxId.setCurrencyCode(amsDeposit.getCurrencyCode());
										amsCashBalanceFxId.setCustomerId(customerId);
										amsCashBalanceFxId.setServiceType(amsDeposit.getServiceType());
										AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
										if(amsCashBalanceFx != null) {
											netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
										}
										log.info("[end] checking NET_DEPOSIT of account " + customerId + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
										BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
										Integer scale = new Integer(0);
										Integer rounding = new Integer(0);
										CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + amsDeposit.getCurrencyCode());
										if(currencyInfo != null) {
											scale = currencyInfo.getCurrencyDecimal();
											rounding = currencyInfo.getCurrencyRound();
										}	
										AmsPromotion amsPromotion = null;
										totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
										log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
										if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
											log.info("[start] check promotion for depositId: " + depositId);
											//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
											amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
											if(amsPromotion != null) {
												log.info("currently, depositId: " + depositId + " will be received promotion");
												log.info("process promotion for customerId: " + amsCustomer.getCustomerId());
												//bonusAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
												//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
												BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), netDepositAmount, amsPromotion.getKind());
												
												if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
													bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
													//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
												} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
													bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId, netDepositAmount, customerId, true);
												}
												
												//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
												if(bonusAmount != null && bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
													log.info("promotionTitle: " + amsPromotion.getPromotionTitle() + "depositId: " + depositId + " will be recieved " + bonusAmount + " " + amsDeposit.getCurrencyCode());
													log.info("send request deposit to customerId: " + amsCustomer.getCustomerId() + ", amount: " + bonusAmount);
													description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_PAYONLINE);
													Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
													if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
														log.info("update credit with amount: " + bonusAmount + " is successful");
														getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), bonusAmount.doubleValue(), depositId, amsDeposit.getCurrencyCode());
														//send mail bonus
														sendmailDepositBonus(amsDeposit, amsCustomer, bonusAmount, language);

														amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
														amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
													}
												} else {
													log.info("bonus amount is 0, amount: " + amsDeposit.getDepositAmount() + ", currencyCode: " + amsDeposit.getCurrencyCode() + ", promotionId: " + amsPromotion.getPromotionId());
												}
												
											} else {
												log.info("cannot find promotion for depositId: " + depositId);
											}
										}
										
										
										//[NTS1.0-Administrator]Sep 27, 2012A - Start - checking promotion for losscut 
										//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, amsCustomer.getWlCode());
										amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
										if(amsPromotion != null) {									
											log.info("Find promotion for losscut");
											log.info("[start] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
											log.info("Checking losscutflag of customer " + amsCustomer.getCustomerId() + " and customer service id " + customerServiceId);
											if(toAmsCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(toAmsCustomerService.getLosscutFlg())) {
												// get lastest deposit of this customer
												log.info("customerId " + customerServiceId + " is losscut");
												log.info("[start] get deposit lastest for customerId " + amsCustomer.getCustomerId());
//												String losscutDatetime = DateUtil.toString(toAmsCustomerService.getLosscutDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DB);
												AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(amsCustomer.getCustomerId(), toAmsCustomerService.getLosscutDatetime());
												if(amsDepositLastest != null) {
													log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
													AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(customerId, amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
													log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
													if(amsPromotionCustomer != null) {
														log.info("CustomerID: " + customerId + " recieved promotion losscut");
														toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
														getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
													} else {
														log.info("Lastest deposit of customerServiceId = " + customerServiceId + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
														log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceId );
														//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, amsCustomer.getWlCode());
														BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
														log.info("customerServiceId " + customerServiceId + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
														if(losscutAmountBonus != null && losscutAmountBonus.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
															log.info("[start] send request deposit to credit for customerServiceId " + customerServiceId);
															description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
															Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
															if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
																log.info("CurrencyCode of customerId " + amsDepositLastest.getCurrencyCode());
																if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
																	log.info("Because currencyCode of customerId " + customerServiceId + " is japan so system will not send mail to this account");
																} else {
																	if(IConstants.Language.JAPANESE.equals(language)) {
																		language = IConstants.Language.ENGLISH;
																	}
																	sendmailLosscut(amsDepositLastest, amsCustomer, losscutAmountBonus, language, currencyCodeService);
																}
																
																log.info("customer service id " + customerServiceId + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
																log.info("[start] Insert data into PROMOTION CUSTOMER");
																getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), amsDeposit.getCurrencyCode());													
																log.info("[end] Insert data into PROMOTION CUSTOMER");
															} else {
																log.warn("Cannot plus credit for customer service id " + customerServiceId + " because returnCode = " + resultUpdateCredit);
															}
														}
														
														log.info("[start] update losscutFlag of customerServiceId " + customerServiceId);
														toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
														getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
														log.info("[end] update losscutFlag of customerServiceId " + customerServiceId);
														log.info("[end] send request deposit to credit for customerServiceId " + customerServiceId);
														log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceId );
													}
													
													
												} else {
													log.info("customerId " + customerServiceId + " no longer deposit into system");
													toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
													getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
												}
												log.info("[end] get deposit lastest for customerId " + amsCustomer.getCustomerId());
											}
											
											log.info("[end] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
										} else {
											log.info("Cannot find promotion for losscut");
										}
										//[NTS1.0-Administrator]Sep 27, 2012A - End
										getiAmsTransferMoneyDAO().merge(amsTransferMoney);
										hasTransfer = true;
									} else {
										result = IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR;
										amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
										getiAmsTransferMoneyDAO().merge(amsTransferMoney);
										
										//  send mail to OM via occur error on MT4						
										log.warn("[start] Send mail to OM about occur MT4 Error" );
										Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
										
										String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
										String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
										if(omDescription == null) {
											omDescription = "";
										}
										String errorOn = "function depositNeteller";													
										String template =IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
										
										subjectMail = mapSubjectMail.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
										mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
										log.warn("[end] Send mail to OM about occur MT4 Error" );
									}
									
								} else {
									amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
									amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
									amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
									getiAmsTransferMoneyDAO().merge(amsTransferMoney);
									hasTransfer = true;
								}
								if(hasTransfer) {
									log.info("[start] send topic for Refresh Balance Info of BO");
									try {									
										BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
										jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
									} catch(Exception ex) {
										log.error(ex.getMessage(), ex);
									}		
									log.info("[end] send topic for Refresh Balance Info of BO");
									
									// deduct ams balance for insert cashflow follow 7.2.3 NAF401
									Double amsBalance = balance - convertedAmount.doubleValue();
									
									// save cashflow for deduct ams balance
									insertCashFlow(transferMoneyId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 0 - convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCode, IConstants.SERVICES_TYPE.AMS, amsBalance, convertRate.doubleValue());
									// save cashflow for plus fx balance
									if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
										// get balanceInfo from MT4
//										BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
										BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, amsDeposit.getServiceType(), currencyCode);
										balance = new Double(0);
										if(balanceInfo != null) {
											balance = balanceInfo.getBalance();
										}
									} else {
										// get balanceInfo from other balance
										AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
										amsCashBalanceId.setCurrencyCode(currencyCode);
										amsCashBalanceId.setCustomerId(customerId);
										amsCashBalanceId.setServiceType(amsDeposit.getServiceType());
										AmsCashBalance amsCashBalanceServiceType = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId); 
										balance = new Double(0);
										if(amsCashBalanceServiceType != null) {
											balance = amsCashBalanceServiceType.getCashBalance();
										}
										balance = balance + amsDeposit.getDepositAmount();
									}
									
									insertCashFlow(transferMoneyId, customerId, customerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, amsDeposit.getDepositAmount(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCodeService, amsDeposit.getServiceType(), balance, convertRate.doubleValue());
									// update balance for AMS
									updateAmsCashBalance(customerId, currencyCode, IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE);
									//[TDSBO1.0-Administrator]Oct 31, 2012A - Start - Apply deposit bonus that are based on NET_DEPOSIT 
//									if(!IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType())) {
										// update balance for other servicetype except AMS
									updateAmsCashBalance(customerId, amsDeposit.getCurrencyCode(), amsDeposit.getServiceType(), amsDeposit.getDepositAmount(), bonusAmount.doubleValue(), Boolean.FALSE);
//									}
									//[TDSBO1.0-Administrator]Oct 31, 2012A - End
									
									
								}
								//[NTS1.0-Administrator]Mar 11, 2013A - Start - update AMS_CUSTOMER_SURVEY
								
								result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;																	
								
							} else {
								// do nothing
							}
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
							AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
							if(amsCustomerSurvey != null) {
								if(amsCustomerSurvey.getNetDepositCc() == null) amsCustomerSurvey.setNetDepositCc(new Double("0"));
								amsCustomerSurvey.setNetDepositCc(amsCustomerSurvey.getNetDepositCc() + amsDeposit.getDepositAmount());
								amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
								amsCustomerSurveyDAO.merge(amsCustomerSurvey);
							}
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
							
							log.info("[start] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
							updateRemarkCustomerSurvey(customerId, amsDeposit.getDepositMethod());
							log.info("[end] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
							result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
							log.info("[end] update status for " + depositId);
						} else {
							log.info("depositId: " + amsDeposit.getDepositId() + " is " + amsDeposit.getStatus());
							log.info("depositId has been changed status to " + amsDeposit.getStatus());
							result = IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED;						
						}
					} else {
						log.warn("deposit Id: " + depositId + " didn't create by " + customerId);
					}
				}
				
			} else {
				log.info("depositId: " + depositId + " is processed with status " + amsDeposit.getStatus());
				result = IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED;		
				
			}
			
		} else {
			log.warn("cannot find depositInfo with depositId: " + depositId);
			result = IConstants.DEPOSIT_UPDATE_RESULT.NOT_AVAILABLE;					
		}
		return result;
		
	}

	public Integer depositNetpaySuccess(String transactionId, String depositId, String customerId, String wlCode, String currencyCode, Integer status) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		AmsDeposit amsDeposit = getiAmsDepositDAO().findById(AmsDeposit.class, depositId);
		if(amsDeposit != null) {
			Integer subGroupId = null;
			if(IConstants.DEPOSIT_STATUS.IN_PROGRESS.equals(amsDeposit.getStatus())) {
				String subjectMail = "";				
				Map<String, String> mapSubjectMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);				
				AmsCustomer amsCustomer = amsDeposit.getAmsCustomer();
				if(amsCustomer != null) {										
					if(customerId.equals(amsCustomer.getCustomerId())) {
						//
						if(IConstants.DEPOSIT_STATUS.IN_PROGRESS.equals(amsDeposit.getStatus())) {
							subGroupId = accountManager.getSubGroupId(customerId, amsDeposit.getServiceType());
							SysAppDate amsAppDate = null;
							List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
							if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
								amsAppDate = listAmsAppDate.get(0);
							}
							log.info("depositNetpaySuccess depositId: " + amsDeposit.getDepositId() + " is inprogress");
							log.info("depositNetpaySuccess [Start] Log for TransactionId "+depositId);			
							log.info("depositNetpaySuccess amsDeposit.getDepositId() " + amsDeposit.getDepositId());
							log.info("depositNetpaySuccess amsDeposit.getCustomerId() " + customerId);				
							log.info("depositNetpaySuccess amsDeposit.getPayMethod()" + amsDeposit.getDepositMethod());
							log.info("depositNetpaySuccess amsDeposit.getDepositAmount()" + amsDeposit.getDepositAmount());
							log.info("depositNetpaySuccess amsDeposit.getDepositAcceptDate()" + amsDeposit.getDepositAcceptDate());
							log.info("depositNetpaySuccess customerInfo.getBaseCurrency()" + currencyCode);
							log.info("depositNetpaySuccess [End] TransactionId "+depositId);
							log.info("depositNetpaySuccess [start] update status for " + depositId);
							amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
							AmsDepositRef amsDepositRef = amsDeposit.getAmsDepositRef();
							if(amsDepositRef != null) {
								amsDepositRef.setGwRefId(transactionId);
							}				
							amsDeposit.setAmsDepositRef(amsDepositRef);		
							amsDeposit.setDepositCompletedDate(amsAppDate.getId().getFrontDate());
							amsDeposit.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
							amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.FINISHED);				
							getiAmsDepositDAO().attachDirty(amsDeposit);
							getiAmsDepositRefDAO().attachDirty(amsDepositRef);
							// get convert rate from AMS and target service type
							BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
							Integer symbolRound = new Integer(0);
							Integer symbolDecimal = new Integer(0);
							Integer rateType = new Integer(-1);
							String currencyCodeService = "";
							String customerServiceId = "";		
							BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
							AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, amsDeposit.getServiceType());
							if(toAmsCustomerService != null) {
								customerServiceId = toAmsCustomerService.getCustomerServiceId();
								AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
								if(amsSubGroup != null) {
									currencyCodeService = amsSubGroup.getCurrencyCode();
									if(currencyCode.equals(currencyCodeService)) {
										convertRate = MathUtil.parseBigDecimal(1);
									} else {
										//[NTS1.0-Administrator]Apr 9, 2013D - Start - remove get rate from DB 
										// set symbolName = fromCurrency + toCurrency
//										String symbolName =  currencyCodeService + currencyCode;
//										Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//										RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//										if(rateInfo != null) {
//											// get symbol rounding from table AMS_SYS_SYMBOL
//											FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//											if(amsSysSymbol != null) {
//												symbolRound = amsSysSymbol.getSymbolRound();
//												symbolDecimal = amsSysSymbol.getSymbolDecimal();
//											}
//											log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//											convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//											log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//											rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//										} else {	
//											 symbolName = currencyCode + currencyCodeService;
//											rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//											if(rateInfo != null) {
//												// get symbol rounding from table AMS_SYS_SYMBOL
//												FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//												if(amsSysSymbol != null) {
//													symbolRound = amsSysSymbol.getSymbolRound();
//													symbolDecimal = amsSysSymbol.getSymbolDecimal();
//												}
//												log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk());
//												convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//												log.info("rate of symbol: " + symbolName + ", bid: " + rateInfo.getBid() + ", ask: " + rateInfo.getAsk() + ", convertrate = " + convertRate);
//												rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//											} else {
//												log.warn("Cannot find rate of symbolName " + symbolName);
//												return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//											}
//											
//										}
										//[NTS1.0-Administrator]Apr 9, 2013D - End
										
										RateInfo rateInfo = getRateInfo(currencyCodeService, currencyCode);
										if(rateInfo == null) {
											return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
										}
										rateType = rateInfo.getRateType();
										convertRate = rateInfo.getRate();
									}
								}
							}
							if(IConstants.RATE_TYPE.BID.equals(rateType)) {
								convertedAmount = convertRate.multiply(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
							} else if(IConstants.RATE_TYPE.ASK.equals(rateType)) {
//								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, symbolDecimal, symbolRound));
								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, IConstants.FRONT_OTHER.SCALE_ALL, BigDecimal.ROUND_DOWN));
								convertedAmount = convertedAmount.divide(MathUtil.parseBigDecimal(1), symbolDecimal, symbolRound);
							} else {
								convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount());
							}
							//[NatureForex1.0-HuyenMT]Sep 7, 2012A - Start 
							amsDeposit.setRate(convertRate.doubleValue()); //HuyenMT set rate for Bug #5549 	Bug-0.2.1(AMSBO)
							getiAmsDepositDAO().attachDirty(amsDeposit); //update ams deposit
							//[NatureForex1.0-HuyenMT]Sep 7, 2012A - End
							
							// update cash balance 
							AmsCashBalance amsCashBalance = updateAmsCashBalance(customerId, currencyCode, IConstants.SERVICES_TYPE.AMS, amsDeposit.getDepositAmount(), bonusAmsAmount.doubleValue(), Boolean.FALSE); // deduct Flag = false mean plus balance of customer
							Double balance = amsCashBalance.getCashBalance();
							insertCashFlow(depositId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL, amsDeposit.getDepositAmount(), IConstants.SOURCE_TYPE.DEPOSIT_ID, currencyCode, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue());
							// send mail to customer about deposit successful
							String language = amsCustomer.getDisplayLanguage();
							if(language == null || StringUtils.isBlank(language)) {
								language = IConstants.Language.ENGLISH;
							}

							sendmailDepositSuccess(amsCustomer, depositId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), currencyCodeService, language, IConstants.PAYMENT_METHOD.CREDIT_CARD);
										
							// check service type 
							// if service type != AMS, insert into table AMS_TRANSFER_MONEY
							if(!IConstants.SERVICES_TYPE.AMS.equals(amsDeposit.getServiceType())) {
								AmsTransferMoney amsTransferMoney = null;
								
								log.info("Transfer money from AMS " + amsDeposit.getDepositAmount() + " -> " + amsDeposit.getServiceType() + " with Amount = " + convertedAmount);
								// insert AMS_TRANSFER MONEY
								TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
								String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
								transferMoneyInfo.setTransferMoneyId(transferMoneyId);
								transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
								transferMoneyInfo.setCustomerId(customerId);
								transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
								transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
								transferMoneyInfo.setTransferTo(amsDeposit.getServiceType());
								transferMoneyInfo.setTransferMoney(amsDeposit.getDepositAmount());
//								transferMoneyInfo.setWlRefId(depositId);
								transferMoneyInfo.setRate(convertRate);
								transferMoneyInfo.setCurrencyCode(amsDeposit.getCurrencyCode());
								transferMoneyInfo.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
								transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
								transferMoneyInfo.setSourceId(depositId);
								transferMoneyInfo.setDestinationAmount(amsDeposit.getDepositAmount());
								transferMoneyInfo.setDestinationCurrencyCode(amsDeposit.getCurrencyCode());
								amsTransferMoney = insertTransferMoney(transferMoneyInfo);
								// checking service type 
								Boolean hasTransfer = false;
								if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
									log.info("serviceType = FX with customerId: " + customerId + ", customerServiceId: " + customerServiceId);
									// if service type == FX then update balance on MT4					
									Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
									String description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_NETPAY);
									Integer resultUpdateBalance = MT4Manager.getInstance().depositBalance(customerServiceId, amsDeposit.getDepositAmount(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
									if(resultUpdateBalance.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)) {
										//  wait for BA via promotion
										amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
										amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
										amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
										
//										log.info("[start] update net deposit for customerId = " + amsCustomer.getCustomerId());
//										updateNetDeposit(amsDeposit.getServiceType(), amsCustomer.getCustomerId(), amsDeposit.getCurrencyCode(), amsDeposit.getDepositAmount());
//										log.info("[end] update net deposit for customerId = " + amsCustomer.getCustomerId());
										
										
										log.info("update balance to mt4 account " + customerId +  " successful");
										log.info("[start] checking NET_DEPOSIT of account " + customerId);
										BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
										AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
										amsCashBalanceFxId.setCurrencyCode(amsDeposit.getCurrencyCode());
										amsCashBalanceFxId.setCustomerId(customerId);
										amsCashBalanceFxId.setServiceType(amsDeposit.getServiceType());
										AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
										if(amsCashBalanceFx != null) {
											netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
										}
										log.info("[end] checking NET_DEPOSIT of account " + customerId + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
										BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
										Integer scale = new Integer(0);
										Integer rounding = new Integer(0);
										CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + amsDeposit.getCurrencyCode());
										if(currencyInfo != null) {
											scale = currencyInfo.getCurrencyDecimal();
											rounding = currencyInfo.getCurrencyRound();
										}	
										AmsPromotion amsPromotion = null;
										totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
										log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
										if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
											log.info("[start] check promotion for depositId: " + depositId);
											//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
											amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
											if(amsPromotion != null) {
												log.info("currently, depositId: " + depositId + " will be received promotion");
												log.info("process promotion for customerId: " + amsCustomer.getCustomerId());
												//bonusAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
												//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
												BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), netDepositAmount, amsPromotion.getKind());
												if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
													bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
													//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
												} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
													bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId, netDepositAmount, customerId, true);
												}
												
												//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
												if(bonusAmount != null && bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
													log.info("promotionTitle: " + amsPromotion.getPromotionTitle() + "depositId: " + depositId + " will be recieved " + bonusAmount + " " + amsDeposit.getCurrencyCode());
													log.info("send request deposit to customerId: " + amsCustomer.getCustomerId() + ", amount: " + bonusAmount);
													description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_NETPAY);
													Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
													if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
														log.info("update credit with amount: " + bonusAmount + " is successful");
														getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), bonusAmount.doubleValue(), depositId, amsDeposit.getCurrencyCode());
														//send mail bonus
														sendmailDepositBonus(amsDeposit, amsCustomer, bonusAmount, language);

														amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
														amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
													}
												} else {
													log.info("bonus amount is 0, amount: " + amsDeposit.getDepositAmount() + ", currencyCode: " + amsDeposit.getCurrencyCode() + ", promotionId: " + amsPromotion.getPromotionId());
												}
												
											} else {
												log.info("cannot find promotion for depositId: " + depositId);
											}
										}
										
										
										//[NTS1.0-Administrator]Sep 27, 2012A - Start - checking promotion for losscut 
										//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, amsCustomer.getWlCode());
										amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
										if(amsPromotion != null) {									
											log.info("Find promotion for losscut");
											log.info("[start] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
											log.info("Checking losscutflag of customer " + amsCustomer.getCustomerId() + " and customer service id " + customerServiceId);
											if(toAmsCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(toAmsCustomerService.getLosscutFlg())) {
												// get lastest deposit of this customer
												log.info("customerId " + customerServiceId + " is losscut");
												log.info("[start] get deposit lastest for customerId " + amsCustomer.getCustomerId());
//												String losscutDatetime = DateUtil.toString(toAmsCustomerService.getLosscutDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DB);
												AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(amsCustomer.getCustomerId(), toAmsCustomerService.getLosscutDatetime());
												if(amsDepositLastest != null) {
													log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
													AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(customerId, amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
													log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
													if(amsPromotionCustomer != null) {
														log.info("CustomerID: " + customerId + " recieved promotion losscut");
														toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
														getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
													} else {
														log.info("Lastest deposit of customerServiceId = " + customerServiceId + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
														log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceId );
														//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, amsCustomer.getWlCode());
														BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
														log.info("customerServiceId " + customerServiceId + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
														if(losscutAmountBonus != null && losscutAmountBonus.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
															log.info("[start] send request deposit to credit for customerServiceId " + customerServiceId);
															description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
															Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
															if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
																log.info("CurrencyCode of customerId " + amsDepositLastest.getCurrencyCode());
																if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
																	log.info("Because currencyCode of customerId " + customerServiceId + " is japan so system will not send mail to this account");
																} else {
																	if(IConstants.Language.JAPANESE.equals(language)) {
																		language = IConstants.Language.ENGLISH;
																	}
																	sendmailLosscut(amsDepositLastest, amsCustomer, losscutAmountBonus, language, currencyCodeService);
																}
																
																log.info("customer service id " + customerServiceId + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
																log.info("[start] Insert data into PROMOTION CUSTOMER");
																getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), amsDeposit.getCurrencyCode());													
																log.info("[end] Insert data into PROMOTION CUSTOMER");
															} else {
																log.warn("Cannot plus credit for customer service id " + customerServiceId + " because returnCode = " + resultUpdateCredit);
															}
														}
														
														log.info("[start] update losscutFlag of customerServiceId " + customerServiceId);
														toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
														getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
														log.info("[end] update losscutFlag of customerServiceId " + customerServiceId);
														log.info("[end] send request deposit to credit for customerServiceId " + customerServiceId);
														log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceId );
													}
													
													
												} else {
													log.info("customerId " + customerServiceId + " no longer deposit into system");
													toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
													getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
												}
												log.info("[end] get deposit lastest for customerId " + amsCustomer.getCustomerId());
											}
											
											log.info("[end] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
										} else {
											log.info("Cannot find promotion for losscut");
										}
										//[NTS1.0-Administrator]Sep 27, 2012A - End
										getiAmsTransferMoneyDAO().merge(amsTransferMoney);
										hasTransfer = true;
									} else {
										result = IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR;
										amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
										getiAmsTransferMoneyDAO().merge(amsTransferMoney);
										
										//  send mail to OM via occur error on MT4						
										log.warn("[start] Send mail to OM about occur MT4 Error" );
										Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
										
										String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
										String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
										if(omDescription == null) {
											omDescription = "";
										}
										String errorOn = "function depositNeteller";													
										String template =IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
										
										subjectMail = mapSubjectMail.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
										mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
										log.warn("[end] Send mail to OM about occur MT4 Error" );
									}
									
								} else {
									amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
									amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
									amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
									getiAmsTransferMoneyDAO().merge(amsTransferMoney);
									hasTransfer = true;
								}
								if(hasTransfer) {
									log.info("[start] send topic for Refresh Balance Info of BO");
									try {									
										BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
										jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
									} catch(Exception ex) {
										log.error(ex.getMessage(), ex);
									}		
									log.info("[end] send topic for Refresh Balance Info of BO");
									
									// deduct ams balance for insert cashflow follow 7.2.3 NAF401
									Double amsBalance = balance - convertedAmount.doubleValue();
									
									// save cashflow for deduct ams balance
									insertCashFlow(transferMoneyId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 0 - convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCode, IConstants.SERVICES_TYPE.AMS, amsBalance, convertRate.doubleValue());
									// save cashflow for plus fx balance
									if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
										// get balanceInfo from MT4
//										BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
										BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, amsDeposit.getServiceType(), currencyCode);
										balance = new Double(0);
										if(balanceInfo != null) {
											balance = balanceInfo.getBalance();
										}
									} else {
										// get balanceInfo from other balance
										AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
										amsCashBalanceId.setCurrencyCode(currencyCode);
										amsCashBalanceId.setCustomerId(customerId);
										amsCashBalanceId.setServiceType(amsDeposit.getServiceType());
										AmsCashBalance amsCashBalanceServiceType = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId); 
										balance = new Double(0);
										if(amsCashBalanceServiceType != null) {
											balance = amsCashBalanceServiceType.getCashBalance();
										}
										balance = balance + amsDeposit.getDepositAmount();
									}
									
									insertCashFlow(transferMoneyId, customerId, customerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, amsDeposit.getDepositAmount(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCodeService, amsDeposit.getServiceType(), balance, convertRate.doubleValue());
									// update balance for AMS
									updateAmsCashBalance(customerId, currencyCode, IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE);
									//[TDSBO1.0-Administrator]Oct 31, 2012A - Start - Apply deposit bonus that are based on NET_DEPOSIT 
//									if(!IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType())) {
										// update balance for other servicetype except AMS
									updateAmsCashBalance(customerId, amsDeposit.getCurrencyCode(), amsDeposit.getServiceType(), amsDeposit.getDepositAmount(), bonusAmount.doubleValue(), Boolean.FALSE);
//									}
									//[TDSBO1.0-Administrator]Oct 31, 2012A - End
									//[NTS1.0-Administrator]Mar 11, 2013A - Start - update AMS_CUSTOMER_SURVEY
									
									
									
								}
								
								result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;																	
								
							} else {
								// do nothing
							}
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
							AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
							if(amsCustomerSurvey != null) {
								if(amsCustomerSurvey.getNetDepositCc() == null) amsCustomerSurvey.setNetDepositCc(new Double("0"));
								amsCustomerSurvey.setNetDepositCc(amsCustomerSurvey.getNetDepositCc() + amsDeposit.getDepositAmount());
								amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
								amsCustomerSurveyDAO.merge(amsCustomerSurvey);
							}
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
							
							
							log.info("[start] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
							updateRemarkCustomerSurvey(customerId, amsDeposit.getDepositMethod());
							log.info("[end] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
							result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
							log.info("[end] update status for " + depositId);
						} else {
							log.info("depositId: " + amsDeposit.getDepositId() + " is " + amsDeposit.getStatus());
							log.info("depositId has been changed status to " + amsDeposit.getStatus());
							result = IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED;						
						}
					} else {
						log.warn("deposit Id: " + depositId + " didn't create by " + customerId);
					}
				}
				
			} else {
				log.info("depositId: " + depositId + " is processed with status " + amsDeposit.getStatus());
				result = IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED;		
				
			}
			
		} else {
			log.warn("cannot find depositInfo with depositId: " + depositId);
			result = IConstants.DEPOSIT_UPDATE_RESULT.NOT_AVAILABLE;					
		}
		return result;
		
	}

	/**
	 * 　
	 * update deposit status
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 24, 2012
	 * @MdDate
	 */
	public Integer depositUpdateStatus(String depositId, String customerId, String baseCurrency, String currencyCode, Integer status, String reason, String wlCode, String errorCode, String fundKeyBalDescription, String fundKeyCrdDescription, Integer paymentMethod) {
		Integer result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
//		try {
		Integer subGroupId = null;
			BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
			BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
			AmsDeposit amsDeposit = getiAmsDepositDAO().findById(AmsDeposit.class, depositId);
			if(amsDeposit != null) {
				String subjectMail = "";
//				String templateMail = "";
				Map<String, String> mapSubjectMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.FRONT_SUBJECT_MAIL);				
				if(IConstants.DEPOSIT_STATUS.IN_PROGRESS.equals(amsDeposit.getStatus())) {
					subGroupId = accountManager.getSubGroupId(customerId, amsDeposit.getServiceType());
					log.info("depositId: " + amsDeposit.getDepositId() + " is inprogress");
					log.info("[Start] Log for TransactionId "+depositId);			
					log.info("amsDeposit.getDepositId() " + amsDeposit.getDepositId());
					log.info("amsDeposit.getCustomerId() " + customerId);				
					log.info("amsDeposit.getPayMethod()" + amsDeposit.getDepositMethod());
					log.info("amsDeposit.getDepositAmount()" + amsDeposit.getDepositAmount());
					log.info("amsDeposit.getDepositAcceptDate()" + amsDeposit.getDepositAcceptDate());
					log.info("customerInfo.getBaseCurrency()" + baseCurrency);
					log.info("[End] TransactionId "+depositId);
					log.info("[start] update status for " + depositId);
					log.info("start update deposit with depositId:" + depositId);
					SysAppDate amsAppDate = null;
					List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
					if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
						amsAppDate = listAmsAppDate.get(0);
					}
					if(IConstants.DEPOSIT_STATUS.FAILURE.equals(status)) {
						amsDeposit.setErrorCode(errorCode);
					} else if(IConstants.DEPOSIT_STATUS.FINISHED.equals(status)) {
						
						amsDeposit.setDepositCompletedDate(amsAppDate.getId().getFrontDate());
						amsDeposit.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
					}
					amsDeposit.setStatus(status);
					AmsDepositRef amsDepositRef = amsDeposit.getAmsDepositRef();
					if(amsDepositRef != null) {
						amsDepositRef.setGwRefId(depositId);
						getiAmsDepositRefDAO().merge(amsDepositRef);
					}
					
					getiAmsDepositDAO().merge(amsDeposit);
					log.info("end update deposit with depositId:" + depositId);					
					// send mail to customer
					AmsCustomer amsCustomer = amsDeposit.getAmsCustomer();
					if(IConstants.DEPOSIT_STATUS.FINISHED.equals(status)) {
						// get convert rate from AMS and target service type
						BigDecimal convertRate = MathUtil.parseBigDecimal(1); // default = 1
						Integer symbolRound = new Integer(0);
						Integer symbolDecimal = new Integer(0);
						String currencyCodeService = "";
						BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
						Integer rateType = new Integer(-1);
						String customerServiceId = "";
						
						AmsCustomerService toAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, amsDeposit.getServiceType());
						if(toAmsCustomerService != null) {
							customerServiceId = toAmsCustomerService.getCustomerServiceId();
							// get subgroup of customer services for get currency code
							AmsSubGroup amsSubGroup = toAmsCustomerService.getAmsSubGroup();
							if(amsSubGroup != null) {
								currencyCodeService = amsSubGroup.getCurrencyCode();
								if(baseCurrency.equals(currencyCodeService)) {
									convertRate = MathUtil.parseBigDecimal(1);
								} else {
									//[NTS1.0-Administrator]Apr 9, 2013D - Start - remove getting rate from DB 
									// set symbolName = fromCurrency + toCurrency
//									String symbolName = currencyCodeService + baseCurrency;
//									Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);									
//									RateInfo rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//									if(rateInfo != null) {
//										// get symbol rounding from table AMS_SYS_SYMBOL
//										FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//										if(amsSysSymbol != null) {
//											symbolRound = amsSysSymbol.getSymbolRound();
//											symbolDecimal = amsSysSymbol.getSymbolDecimal();
//										}
//										convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//										rateType = IConstants.RATE_TYPE.BID; // if bid counter currency / base currency
//									} else {	
//										symbolName = baseCurrency + currencyCodeService;
//										rateInfo = MT4Manager.getInstance().getRateBySymbol(symbolName, mapConfig);
//										if(rateInfo != null) {
//											// get symbol rounding from table AMS_SYS_SYMBOL
//											FxSymbol amsSysSymbol = getiFxSymbolDAO().findById(FxSymbol.class, symbolName);
//											if(amsSysSymbol != null) {
//												symbolRound = amsSysSymbol.getSymbolRound();
//												symbolDecimal = amsSysSymbol.getSymbolDecimal();
//											}
//											convertRate = (rateInfo.getBid().add(rateInfo.getAsk())).divide(MathUtil.parseBigDecimal(2), symbolDecimal, symbolRound);
//											rateType = IConstants.RATE_TYPE.ASK; // if ask counter currency / base currency
//										} else {
//											log.error("Cannot find rate of symbolName " + symbolName);
//											return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
//										}
//										
//									}
									//[NTS1.0-Administrator]Apr 9, 2013D - End
									
									RateInfo rateInfo = getRateInfo(currencyCodeService, currencyCode);
									if(rateInfo == null) {
										return IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE;
									}
									rateType = rateInfo.getRateType();
									convertRate = rateInfo.getRate();
								}
							}
						}
						
						if(IConstants.RATE_TYPE.BID.equals(rateType)) {
							convertedAmount = convertRate.multiply(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
						} else if(IConstants.RATE_TYPE.ASK.equals(rateType)) {
//							convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, symbolDecimal, symbolRound));
							convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()).multiply(MathUtil.parseBigDecimal(1).divide(convertRate, IConstants.FRONT_OTHER.SCALE_ALL, BigDecimal.ROUND_DOWN));
							convertedAmount = convertedAmount.divide(MathUtil.parseBigDecimal(1), symbolDecimal, symbolRound);
						} else {
							convertedAmount = MathUtil.parseBigDecimal(amsDeposit.getDepositAmount());
						}
						
						
						// update cash balance 
						AmsCashBalance amsCashBalance = updateAmsCashBalance(customerId, baseCurrency, IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.FALSE); // deduct Flag = false
						Double balance = amsCashBalance.getCashBalance();
						insertCashFlow(depositId, customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL, convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.DEPOSIT_ID, baseCurrency, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue());

						// send mail to customer about deposit successful
						String language = amsCustomer.getDisplayLanguage();
						if(language == null || StringUtils.isBlank(language)) {
							language = IConstants.Language.ENGLISH;
						}
						sendmailDepositSuccess(amsCustomer, depositId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), currencyCodeService, language, paymentMethod);

						// check service type 
						// if service type != AMS, insert into table AMS_TRANSFER_MONEY
						if(!IConstants.SERVICES_TYPE.AMS.equals(amsDeposit.getServiceType())) {
							AmsTransferMoney amsTransferMoney = null;
							Boolean hasTransfer = false;
							log.info("Transfer money from AMS " + amsDeposit.getDepositAmount() + " -> " + amsDeposit.getServiceType() + " with Amount = " + convertedAmount);
							// insert AMS_TRANSFER MONEY
//							TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
//							String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
//							transferMoneyInfo.setTransferMoneyId(transferMoneyId);
//							transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
//							transferMoneyInfo.setCustomerId(customerId);
//							transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
//							transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
//							transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
//							transferMoneyInfo.setTransferTo(amsDeposit.getServiceType());
//							transferMoneyInfo.setTransferMoney(amsDeposit.getDepositAmount());
////							transferMoneyInfo.setWlRefId(depositId);
//							transferMoneyInfo.setRate(convertRate);
//							transferMoneyInfo.setCurrencyCode(amsDeposit.getCurrencyCode());
//							transferMoneyInfo.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
//							transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
//							transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
//							transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
//							transferMoneyInfo.setSourceId(depositId);
//							transferMoneyInfo.setDestinationAmount(amsDeposit.getDepositAmount());
//							transferMoneyInfo.setDestinationCurrencyCode(amsDeposit.getCurrencyCode());
							TransferMoneyInfo transferMoneyInfo = getTransferMoneyInfo(customerId, amsDeposit.getServiceType(), amsDeposit.getDepositAmount(), convertRate, amsDeposit.getCurrencyCode(), amsAppDate.getId().getFrontDate(), depositId);
							amsTransferMoney = insertTransferMoney(transferMoneyInfo);
							// checking service type 
							if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
								String description = "";
								log.info("deposit serviceType = FX with customerId: " + customerId + ", customerServiceId: " + customerServiceId);
								// if service type == FX then update balance on MT4					
								Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);								
//								description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_ALLCHARGE);
								description = mapFundDescription.get(fundKeyBalDescription);
								Integer resultUpdateBalance = MT4Manager.getInstance().depositBalance(customerServiceId, amsDeposit.getDepositAmount(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
								if(resultUpdateBalance.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)) {
									//  wait for BA via promotion
									amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
									amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
									amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
									
//									log.info("[start] update net deposit for customerId = " + amsCustomer.getCustomerId());
//									updateNetDeposit(amsDeposit.getServiceType(), amsCustomer.getCustomerId(), amsDeposit.getCurrencyCode(), amsDeposit.getDepositAmount());
//									log.info("[end] update net deposit for customerId = " + amsCustomer.getCustomerId());
									
									log.info("update balance to mt4 account " + customerId +  " successful");
									log.info("[start] checking NET_DEPOSIT of account " + customerId);
									BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
									AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
									amsCashBalanceFxId.setCurrencyCode(amsDeposit.getCurrencyCode());
									amsCashBalanceFxId.setCustomerId(customerId);
									amsCashBalanceFxId.setServiceType(amsDeposit.getServiceType());
									AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
									if(amsCashBalanceFx != null) {
										netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
									}
									log.info("[end] checking NET_DEPOSIT of account " + customerId + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
									BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()));
									Integer scale = new Integer(0);
									Integer rounding = new Integer(0);
									CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + amsDeposit.getCurrencyCode());
									if(currencyInfo != null) {
										scale = currencyInfo.getCurrencyDecimal();
										rounding = currencyInfo.getCurrencyRound();
									}	
									AmsPromotion amsPromotion = null;
									totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
									log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
									if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
										log.info("[start] check promotion for depositId: " + depositId);
										//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
										amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
										if(amsPromotion != null) {
											log.info("currently, depositId: " + depositId + " will be received promotion");
											log.info("process promotion for customerId: " + amsCustomer.getCustomerId());
											//bonusAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsCustomer.getWlCode());
											//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start 
											BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), netDepositAmount, amsPromotion.getKind());
											if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
												bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId);
												//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
											} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
												bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, amsDeposit.getCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, amsDeposit.getServiceType(), subGroupId, netDepositAmount, customerId, true);
											}
											
											//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
											if(bonusAmount != null && bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
												log.info("promotionTitle: " + amsPromotion.getPromotionTitle() + "depositId: " + depositId + " will be recieved " + bonusAmount + " " + amsDeposit.getCurrencyCode());
												log.info("send request deposit to customerId: " + amsCustomer.getCustomerId() + ", amount: " + bonusAmount);
												description = mapFundDescription.get(fundKeyCrdDescription);
												Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
												if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
													log.info("update credit with amount: " + bonusAmount + " is successful");
													getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), bonusAmount.doubleValue(), depositId, amsDeposit.getCurrencyCode());
													//send mail bonus
													sendmailDepositBonus(amsDeposit, amsCustomer, bonusAmount, language);
													amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
													amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
												}
											} else {
												log.info("bonus amount is 0, amount: " + amsDeposit.getDepositAmount() + ", currencyCode: " + amsDeposit.getCurrencyCode() + ", promotionId: " + amsPromotion.getPromotionId());
											}
										} else {
											log.info("cannot find promotion for depositId: " + depositId);
										}
									}
									
									
									//[NTS1.0-Administrator]Sep 27, 2012A - Start - checking promotion for losscut 
									//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, wlCode);
									amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
									if(amsPromotion != null) {									
										log.info("Find promotion for losscut");
										log.info("[start] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
										log.info("Checking losscutflag of customer " + amsCustomer.getCustomerId() + " and customer service id " + customerServiceId);
										if(toAmsCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(toAmsCustomerService.getLosscutFlg())) {
											// get lastest deposit of this customer
											log.info("customerId " + customerServiceId + " is losscut");
											log.info("[start] get deposit lastest for customerId " + amsCustomer.getCustomerId());
//											String losscutDatetime = DateUtil.toString(toAmsCustomerService.getLosscutDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DB);
											AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(amsCustomer.getCustomerId(), toAmsCustomerService.getLosscutDatetime());
											if(amsDepositLastest != null) {
												log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
												AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(customerId, amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
												log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
												if(amsPromotionCustomer != null) {
													log.info("CustomerID: " + customerId + " recieved promotion losscut");
													toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
													getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
												} else {
													log.info("Lastest deposit of customerServiceId = " + customerServiceId + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
													log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceId );
													//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, wlCode);
													BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, amsDeposit.getServiceType(), subGroupId);
													log.info("customerServiceId " + customerServiceId + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
													if(losscutAmountBonus != null && losscutAmountBonus.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
														log.info("[start] send request deposit to credit for customerServiceId " + customerServiceId);											
														description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
														Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceId, losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
														if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
															log.info("CurrencyCode of customerId " + amsDepositLastest.getCurrencyCode());
															if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
																log.info("Because currencyCode of customerId " + customerServiceId + " is japan so system will not send mail to this account");
															} else {
																if(IConstants.Language.JAPANESE.equals(language)) {
																	language = IConstants.Language.ENGLISH;
																}
																sendmailLosscut(amsDepositLastest, amsCustomer, losscutAmountBonus, language, currencyCodeService);
															}

															log.info("customer service id " + customerServiceId + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
															log.info("[start] Insert data into PROMOTION CUSTOMER");
															getiPromotionManager().saveAmsPromotionCustomer(customerId, amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), amsDeposit.getCurrencyCode());													
															log.info("[end] Insert data into PROMOTION CUSTOMER");
															
														} else {
															log.warn("Cannot plus credit for customer service id " + customerServiceId + " because returnCode = " + resultUpdateCredit);
														}
													}
													
													log.info("[start] update losscutFlag of customerServiceId " + customerServiceId);
													toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
													getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
													log.info("[end] update losscutFlag of customerServiceId " + customerServiceId);
													log.info("[end] send request deposit to credit for customerServiceId " + customerServiceId);
													log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceId );
												}
												
												
											} else {
												log.info("customerId " + customerServiceId + " no longer deposit into system");
												toAmsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
												getiAmsCustomerServiceDAO().merge(toAmsCustomerService);
											}
											log.info("[end] get deposit lastest for customerId " + amsCustomer.getCustomerId());
										}
										
										log.info("[end] process promotion losscut for customerId: " + amsCustomer.getCustomerId());
									} else {
										log.info("Cannot find promotion for losscut");
									}
									//[NTS1.0-Administrator]Sep 27, 2012A - End
									getiAmsTransferMoneyDAO().merge(amsTransferMoney);
									hasTransfer = true;
								} else {
									result = IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR;
									amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
									getiAmsTransferMoneyDAO().merge(amsTransferMoney);
									
									//  send mail to OM via occur error on MT4						
									log.warn("depositNeteller [start] Send mail to OM about occur MT4 Error" );
									Map<String, String> mapOmDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MAIL_OM_DESCRIPTION);
									
									String timeError = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME);
									String omDescription = mapOmDescription.get(IConstants.MAIL_OM_DESCRIPTION.DEPOSIT_MT4);
									if(omDescription == null) {
										omDescription = "";
									}
									String errorOn = "depositNeteller function depositNeteller";													
									String template = IConstants.AMS_MAIL_TEMPLATE.ERROR_MAIL;
									
									subjectMail = mapSubjectMail.get(IConstants.FRONT_SUBJECT_MAIL.ERROR_MAIL);
									mailService.sendMailOM(timeError, errorOn, omDescription, subjectMail, template);
									log.warn("depositNeteller [end] Send mail to OM about occur MT4 Error" );
									return IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
								}
								
							} else {
								amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
								amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
								amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
								getiAmsTransferMoneyDAO().merge(amsTransferMoney);	
								hasTransfer = true;
							}
							if(hasTransfer) {
								
								log.info("[start] send topic for Refresh Balance Info of BO");
								try {									
									BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
									jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
								} catch(Exception ex) {
									log.error(ex.getMessage(), ex);
								}
								log.info("[end] send topic for Refresh Balance Info of BO");
								
								// deduct ams balance for insert cashflow follow 7.2.3 NAF401
								Double amsBalance = balance - convertedAmount.doubleValue();								
								// save cashflow for deduct ams balance
								insertCashFlow(transferMoneyInfo.getTransferMoneyId(), customerId, null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 0 - convertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCodeService, IConstants.SERVICES_TYPE.AMS, amsBalance, convertRate.doubleValue());
								// save cashflow for plus fx balance
								if(IConstants.SERVICES_TYPE.FX.equals(amsDeposit.getServiceType()) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsDeposit.getServiceType())) {
									// get balanceInfo from MT4
//									BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
									BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, amsDeposit.getServiceType(), currencyCodeService);
									balance = new Double(0);
									if(balanceInfo != null) {
										balance = balanceInfo.getBalance();
									}
								} else {
									// get balanceInfo from other balance
									AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
									amsCashBalanceId.setCurrencyCode(currencyCodeService);
									amsCashBalanceId.setCustomerId(customerId);
									amsCashBalanceId.setServiceType(amsDeposit.getServiceType());
									AmsCashBalance amsCashBalanceServiceType = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId); 
									balance = new Double(0);
									if(amsCashBalanceServiceType != null) {
										balance = amsCashBalanceServiceType.getCashBalance();
									}
									balance = balance + amsDeposit.getDepositAmount();
								}
								
								insertCashFlow(transferMoneyInfo.getTransferMoneyId(), customerId, customerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, amsDeposit.getDepositAmount(), IConstants.SOURCE_TYPE.TRANFER_MONEY, currencyCodeService, amsDeposit.getServiceType(), balance, convertRate.doubleValue());
								// update balance for AMS
								updateAmsCashBalance(customerId, baseCurrency, IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE);
								updateAmsCashBalance(customerId, currencyCode, amsDeposit.getServiceType(), amsDeposit.getDepositAmount(), bonusAmount.doubleValue(), Boolean.FALSE);
//								}
								//[NTS1.0-Administrator]Mar 11, 2013A - Start - update AMS_CUSTOMER_SURVEY
								
								
							}
							
							result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
						} else {
							// do nothing
							result = IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS;
						}
						if(IConstants.PAYMENT_METHOD.LIBERTY != amsDeposit.getDepositMethod() && IConstants.PAYMENT_METHOD.NETELLER != amsDeposit.getDepositMethod()) {
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
							AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
							if(amsCustomerSurvey != null) {
								if(amsCustomerSurvey.getNetDepositCc() == null) amsCustomerSurvey.setNetDepositCc(new Double("0"));
								amsCustomerSurvey.setNetDepositCc(amsCustomerSurvey.getNetDepositCc() + amsDeposit.getDepositAmount());
								amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
								amsCustomerSurveyDAO.merge(amsCustomerSurvey);
							}
							log.info("[start] update AMS_CUSTOMER_SURVEY with customerId = " + customerId);
						}
						
						
						log.info("[start] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
						updateRemarkCustomerSurvey(customerId, amsDeposit.getDepositMethod());
						log.info("[end] update AMS_CUSTOMER_SURVEY for REMARK PAYMENT METHOD");
						
						log.info("depositNetpaySuccess [end] update status for " + depositId);
						
					} else {
//						DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
						if(IConstants.STATUS_DEPOSIT.CANCEL.equals(status)) {
							result = IConstants.DEPOSIT_UPDATE_RESULT.CANCEL;
							String language = IConstants.Language.ENGLISH;
							if(amsCustomer.getDisplayLanguage() != null && !StringUtils.isBlank(amsCustomer.getDisplayLanguage())) {
								language = amsCustomer.getDisplayLanguage();
							}

							sendmailDepositCancel(amsDeposit, amsCustomer, amsDeposit.getDepositAmount(), reason, language,
									paymentMethod);
						} else if(IConstants.STATUS_DEPOSIT.FAIL.equals(status)) {
							String language = IConstants.Language.ENGLISH;
							if(amsCustomer.getDisplayLanguage() != null && !StringUtils.isBlank(amsCustomer.getDisplayLanguage())) {
								language = amsCustomer.getDisplayLanguage();
							}							
							sendmailDepositFail(amsDeposit, amsCustomer, reason, language, paymentMethod);

							result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
						}
					}
				} else {
					log.info("depositId has been changed status to " + amsDeposit.getStatus());
					result = IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED;
				}
				
				
			} else {
				log.warn("cannot find depositInfo with depositId: " + depositId);
				result = IConstants.DEPOSIT_UPDATE_RESULT.NOT_AVAILABLE;
			}
			
//		} catch(Exception ex) {
//			result = IConstants.DEPOSIT_UPDATE_RESULT.FAILURE;
//			log.error(ex.getMessage(), ex);
//		}		
		return result;
	}
	
	/**
	 * sendmailDepositFail　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailDepositFail(AmsDeposit amsDeposit, AmsCustomer amsCustomer, String reason, String language, Integer paymentMethod) {
		log.info("[start] send mail deposit cancel for deposit id " + amsDeposit.getDepositId());
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_FAIL + "_" + language); //  mail template lack DEPOSIT
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_FAIL).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();								
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositId(amsDeposit.getDepositId());
		amsMailTemplateInfo.setDepositCancelReason(reason);
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		amsMailTemplateInfo.setDepositAmount(balanceManager.formatNumber(MathUtil.parseBigDecimal(amsDeposit.getDepositAmount()), amsDeposit.getCurrencyCode()));
		amsMailTemplateInfo.setDepositCurrency(balanceManager.getCurrencyCode(amsDeposit.getCurrencyCode(), language));
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(amsDeposit.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));							
		Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
		amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(paymentMethod)));							
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																			
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail deposit cancel for deposit id " + amsDeposit.getDepositId());
	}

	/**
	 * insertDeposit　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Feb 20, 2013
	 */
//	private void insertDeposit(CreditCardInfo creditCardInfo, SysAppDate amsAppDate, String depositId, BigDecimal convertRate) {
//		AmsDeposit amsDeposit = new AmsDeposit();			
//		amsDeposit.setDepositId(depositId);
//		AmsCustomer amsCustomer = new AmsCustomer();
//		amsCustomer.setCustomerId(creditCardInfo.getCustomerId());
//		amsDeposit.setAmsCustomer(amsCustomer);
//		amsDeposit.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
//		amsDeposit.setCurrencyCode(creditCardInfo.getCurrencyCode());
//		amsDeposit.setDepositType(IConstants.DEPOSIT_TYPE.DEPOSIT);
//		amsDeposit.setServiceType(creditCardInfo.getServiceType());
//		amsDeposit.setStatus(IConstants.DEPOSIT_STATUS.IN_PROGRESS);
//		amsDeposit.setDepositMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
//		amsDeposit.setDepositGateway(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY_VALUE);
//		amsDeposit.setDepositRoute(creditCardInfo.getDeviceType());
//		amsDeposit.setDepositAmount(creditCardInfo.getAmount().doubleValue());
//		amsDeposit.setRate(convertRate == null ? 1 : convertRate.doubleValue());
//		amsDeposit.setDepositRoute(creditCardInfo.getDeviceType());
//		if(amsAppDate != null) {
//			amsDeposit.setDepositAcceptDate(amsAppDate.getId().getFrontDate());
//		} else {
//			log.error("Cannot find config for Bussiness Date");			
//		}
//		amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
//		amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));		
//		amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
////		amsDeposit.setConfirmDate(null);//HuyenMT set null for Bug #5549 	Bug-0.2.1(AMSBO)
//		AmsDepositRef amsDepositRef = new AmsDepositRef();
//		amsDepositRef.setDepositId(depositId);
//		amsDepositRef.setCcAddress(creditCardInfo.getAddress());
//		amsDepositRef.setCcCity(creditCardInfo.getCity());
//		amsDepositRef.setCcCvv(creditCardInfo.getCcCvv());
//		amsDepositRef.setCcEmail(creditCardInfo.getEmail());
//		amsDepositRef.setCcExpiredDate(creditCardInfo.getExpiredDate());
//		amsDepositRef.setCcHolderName(creditCardInfo.getCcHolderName());
//		amsDepositRef.setCcNo(creditCardInfo.getCcNo());
//		amsDepositRef.setCcPhone(creditCardInfo.getPhone());
//		amsDepositRef.setCcState(creditCardInfo.getState());
//		amsDepositRef.setCcType(creditCardInfo.getCcType());
//		amsDepositRef.setCcZipCode(creditCardInfo.getZipCode());
//		amsDepositRef.setCountryId(creditCardInfo.getCountryId());
//		amsDepositRef.setInputDate(new Timestamp(System.currentTimeMillis()));
//		amsDepositRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
//		amsDepositRef.setAmsDeposit(amsDeposit);
//		amsDeposit.setAmsDepositRef(amsDepositRef);
//
//		getiAmsDepositDAO().save(amsDeposit);
//		getiAmsDepositRefDAO().save(amsDepositRef);
//	}
	
	/**
	 * 　
	 * get list country
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 24, 2012
	 * @MdDate
	 */
	public LinkedHashMap<String, String> getListCountry() {
		log.info("[Start] get list country in system");
		LinkedHashMap<String, String> listCountry = null;
		try {
			List<AmsSysCountry> listCountries = new ArrayList<AmsSysCountry>();
			listCountries = getiAmsSysCountryDAO().findByActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			listCountry = new LinkedHashMap<String, String>();
			if(listCountries != null && listCountries.size() > 0) {
				for (AmsSysCountry amsSysCountry : listCountries) {
					listCountry.put(amsSysCountry.getCountryId().toString(), amsSysCountry.getCountryName());
				}	
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] get list country in system");
		return listCountry;
	}
	public Map<Integer, CountryInfo> getListCountryInfo() {
		Map<Integer, CountryInfo> listCountry = null;
		CountryInfo countryInfo = null;
		List<AmsSysCountry> listCountries = getiAmsSysCountryDAO().findByActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		if(listCountries != null && listCountries.size() > 0) {
			listCountry = new HashMap<Integer, CountryInfo>();
			for(AmsSysCountry amsSysCountry : listCountries) {
				countryInfo = new CountryInfo();
				BeanUtils.copyProperties(amsSysCountry, countryInfo);
				listCountry.put(amsSysCountry.getCountryId(), countryInfo);
			}
		}
		
		return listCountry;
	}
	/**
	 * 　
	 * get customer credit info
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 23, 2012
	 * @MdDate
	 */
	public CreditCardInfo getCustomerCreditInfo(Integer customerCreditId) {
		CreditCardInfo creditCardInfo = null;
		AmsCustomerCreditcard amsCustomerCreditCard = getiAmsCustomerCreditCardDAO().findById(AmsCustomerCreditcard.class, customerCreditId);
		if(amsCustomerCreditCard != null) {
			creditCardInfo = new CreditCardInfo();
			BeanUtils.copyProperties(amsCustomerCreditCard, creditCardInfo);
			AmsSysCountry amsSysCountry = amsCustomerCreditCard.getAmsSysCountry();
			if(amsSysCountry != null) {
				creditCardInfo.setCountryCode(amsSysCountry.getCountryCode());
				creditCardInfo.setCountryId(amsSysCountry.getCountryId());
				creditCardInfo.setCountryName(amsSysCountry.getCountryName());
			}
		}
		return creditCardInfo;
	}
	
	
	public List<ExchangerInfo> getListExchangers(String wlCode, String currencyCode, String customerId){
		List<AmsExchanger> listExchangers = amsExchangerDAO.getAmsExchanger(wlCode, currencyCode, customerId);
		if(listExchangers != null && listExchangers.size() > 0){
			List<ExchangerInfo> exchangerInfos = new ArrayList<ExchangerInfo>();
			for (AmsExchanger amsExchanger : listExchangers) {
				ExchangerInfo exchangerInfo = new ExchangerInfo();
				exchangerInfo.setExchangerId(amsExchanger.getExchangerId());
				exchangerInfo.setExchangerName(amsExchanger.getExchangerName());
				exchangerInfo.setBankInfo(amsExchanger.getBankInfo());
				exchangerInfos.add(exchangerInfo);
			}
			return exchangerInfos;
		}
		return null;
	}
	
	public ExchangerInfo getExchanger(String exchangerId){
		AmsExchanger  exchanger = amsExchangerDAO.findById(AmsExchanger.class, exchangerId);
		if(exchanger != null ){
				ExchangerInfo exchangerInfo = new ExchangerInfo();
				exchangerInfo.setExchangerId(exchanger.getExchangerId());
				exchangerInfo.setExchangerName(exchanger.getAmsCustomer().getFullName());
				exchangerInfo.setBankInfo(exchanger.getBankInfo());
			return exchangerInfo;
		}
		return null;
	}
	
	public ExchangerSymbolInfo getExchangerSymbol(String exchangerId){
		ExchangerSymbolInfo exchangerSymbolInfo = null;
//		AmsExchangerSymbol amsExchangerSymbol = amsExchangerSymbolDAO.getExchangerSymbolByExchangerId( exchangerId).get(0);
		List<AmsExchangerSymbol> listAmsExchangerSymbol = amsExchangerSymbolDAO.getExchangerSymbolByExchangerId(exchangerId);
		if(listAmsExchangerSymbol != null && listAmsExchangerSymbol.size() > 0) {
			AmsExchangerSymbol amsExchangerSymbol = listAmsExchangerSymbol.get(0);
			exchangerSymbolInfo = ExchangerSymbolConverter.toInfo(amsExchangerSymbol); 
		}
		return exchangerSymbolInfo;
		
	}
	
	public RateInfo getLastestRate(String currencyPair) {
		List<FxSummaryRate> rates = summaryRateDAO.getListFxSummaryByCurrencyPair(currencyPair);
		if (rates == null || rates.size() == 0) {
			return null;
		}
		
		FxSummaryRate rate = rates.get(0);
		
		RateInfo info = new RateInfo();
		info.setRate(rate.getClosePrice());
		
		return info;
	}
	

	/**
	 * Calculate credit amount by net deposit
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 24, 2013
	 */
	private BigDecimal getBonusByNetDeposit(BigDecimal depositAmount, BigDecimal netDepositAmount, Integer promotionKind){
		BigDecimal baseAmount = MathUtil.parseBigDecimal(0);
		if(promotionKind == null){
			baseAmount = depositAmount;
		}else{
			if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(promotionKind)){
				baseAmount = depositAmount;
			}
			if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(promotionKind)){
				baseAmount = netDepositAmount.add(depositAmount);
			}
		}
		return baseAmount;
	}
	
	/**
	 * @return the iPromotionManager
	 */
	public IPromotionManager getiPromotionManager() {
		return iPromotionManager;
	}
	/**
	 * @param iPromotionManager the iPromotionManager to set
	 */
	public void setiPromotionManager(IPromotionManager iPromotionManager) {
		this.iPromotionManager = iPromotionManager;
	}
	/**
	 * @return the iSysUniqueidCounterDAO
	 */
	public ISysUniqueidCounterDAO<SysUniqueidCounter> getiSysUniqueidCounterDAO() {
		return iSysUniqueidCounterDAO;
	}
	/**
	 * @param iSysUniqueidCounterDAO the iSysUniqueidCounterDAO to set
	 */
	public void setiSysUniqueidCounterDAO(
			ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO) {
		this.iSysUniqueidCounterDAO = iSysUniqueidCounterDAO;
	}
	/**
	 * @return the iSysAppDateDAO
	 */
	public ISysAppDateDAO<SysAppDate> getiSysAppDateDAO() {
		return iSysAppDateDAO;
	}
	/**
	 * @param iSysAppDateDAO the iSysAppDateDAO to set
	 */
	public void setiSysAppDateDAO(ISysAppDateDAO<SysAppDate> iSysAppDateDAO) {
		this.iSysAppDateDAO = iSysAppDateDAO;
	}
	/**
	 * @return the iFxSymbolDAO
	 */
	public IFxSymbolDAO<FxSymbol> getiFxSymbolDAO() {
		return iFxSymbolDAO;
	}
	/**
	 * @param iFxSymbolDAO the iFxSymbolDAO to set
	 */
	public void setiFxSymbolDAO(IFxSymbolDAO<FxSymbol> iFxSymbolDAO) {
		this.iFxSymbolDAO = iFxSymbolDAO;
	}
	public IAmsExchangerDAO<AmsExchanger> getAmsExchangerDAO() {
		return amsExchangerDAO;
	}
	public void setAmsExchangerDAO(IAmsExchangerDAO<AmsExchanger> amsExchangerDAO) {
		this.amsExchangerDAO = amsExchangerDAO;
	}
	public IAmsExchangerSymbolDAO<AmsExchangerSymbol> getAmsExchangerSymbolDAO() {
		return amsExchangerSymbolDAO;
	}
	public void setAmsExchangerSymbolDAO(IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO) {
		this.amsExchangerSymbolDAO = amsExchangerSymbolDAO;
	}

	/**
	 * @return the balanceManager
	 */
	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}
	
	
	public CustomerEwalletInfo getLibertyInfo(String customerId, String accountId) {
		CustomerEwalletInfo customerEwalletInfo = null;
		AmsCustomerEwallet amsCustomerEwallet = null;
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getAmsCustomerEwallet(customerId, IConstants.EWALLET_TYPE.LIBERTY, accountId, null);
		if(listAmsCustomerEwallet != null && listAmsCustomerEwallet.size() > 0) {
			amsCustomerEwallet = listAmsCustomerEwallet.get(0);
			if(amsCustomerEwallet != null) {
				customerEwalletInfo = new CustomerEwalletInfo();
				BeanUtils.copyProperties(amsCustomerEwallet, customerEwalletInfo);
			}
		}
		return customerEwalletInfo;
	}

	public IAmsPromotionDAO<AmsPromotion> getAmsPromotionDAO() {
		return amsPromotionDAO;
	}

	public void setAmsPromotionDAO(IAmsPromotionDAO<AmsPromotion> amsPromotionDAO) {
		this.amsPromotionDAO = amsPromotionDAO;
	}
	
	/**
	 * @return the iAmsDepositRefDAO
	 */
	public IAmsDepositRefDAO<AmsDepositRef> getiAmsDepositRefDAO() {
		return iAmsDepositRefDAO;
	}

	/**
	 * @param iAmsDepositRefDAO the iAmsDepositRefDAO to set
	 */
	public void setiAmsDepositRefDAO(IAmsDepositRefDAO<AmsDepositRef> iAmsDepositRefDAO) {
		this.iAmsDepositRefDAO = iAmsDepositRefDAO;
	}

	/**
	 * @return the iAmsCashFlowDAO
	 */
	public IAmsCashflowDAO<AmsCashflow> getiAmsCashFlowDAO() {
		return iAmsCashFlowDAO;
	}

	/**
	 * @param iAmsCashFlowDAO the iAmsCashFlowDAO to set
	 */
	public void setiAmsCashFlowDAO(IAmsCashflowDAO<AmsCashflow> iAmsCashFlowDAO) {
		this.iAmsCashFlowDAO = iAmsCashFlowDAO;
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

	/**
	 * @return the iAmsWhitelabelConfigDAO
	 */
	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getiAmsWhitelabelConfigDAO() {
		return iAmsWhitelabelConfigDAO;
	}

	/**
	 * @param iAmsWhitelabelConfigDAO the iAmsWhitelabelConfigDAO to set
	 */
	public void setiAmsWhitelabelConfigDAO(IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO) {
		this.iAmsWhitelabelConfigDAO = iAmsWhitelabelConfigDAO;
	}
	
	

	/**
	 * @return the iAmsWhitelabelDAO
	 */
	public IAmsWhitelabelDAO<AmsWhitelabel> getiAmsWhitelabelDAO() {
		return iAmsWhitelabelDAO;
	}

	/**
	 * @param iAmsWhitelabelDAO the iAmsWhitelabelDAO to set
	 */
	public void setiAmsWhitelabelDAO(IAmsWhitelabelDAO<AmsWhitelabel> iAmsWhitelabelDAO) {
		this.iAmsWhitelabelDAO = iAmsWhitelabelDAO;
	}
	public IAmsDepositDAO<AmsDeposit> getiAmsDepositDAO() {
		return iAmsDepositDAO;
	}

	public void setiAmsDepositDAO(IAmsDepositDAO<AmsDeposit> iAmsDepositDAO) {
		this.iAmsDepositDAO = iAmsDepositDAO;
	}

	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}

	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}
	
	
	/**
	 * @return the iAmsCustomerEwalletDAO
	 */
	public IAmsCustomerEwalletDAO<AmsCustomerEwallet> getiAmsCustomerEwalletDAO() {
		return iAmsCustomerEwalletDAO;
	}

	/**
	 * @param iAmsCustomerEwalletDAO the iAmsCustomerEwalletDAO to set
	 */
	public void setiAmsCustomerEwalletDAO(
			IAmsCustomerEwalletDAO<AmsCustomerEwallet> iAmsCustomerEwalletDAO) {
		this.iAmsCustomerEwalletDAO = iAmsCustomerEwalletDAO;
	}
	/**
	 * @return the iAmsCustomerCreditCardDAO
	 */
	public IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> getiAmsCustomerCreditCardDAO() {
		return iAmsCustomerCreditCardDAO;
	}

	/**
	 * @param iAmsCustomerCreditCardDAO the iAmsCustomerCreditCardDAO to set
	 */
	public void setiAmsCustomerCreditCardDAO(
			IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> iAmsCustomerCreditCardDAO) {
		this.iAmsCustomerCreditCardDAO = iAmsCustomerCreditCardDAO;
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
	 * @return the mailService
	 */
	public MailService getMailService() {
		return mailService;
	}

	/**
	 * @param mailService the mailService to set
	 */
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	/**
	 * @return the iAmsSysCountryDAO
	 */
	public IAmsSysCountryDAO<AmsSysCountry> getiAmsSysCountryDAO() {
		return iAmsSysCountryDAO;
	}
	/**
	 * @param iAmsSysCountryDAO the iAmsSysCountryDAO to set
	 */
	public void setiAmsSysCountryDAO(
			IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO) {
		this.iAmsSysCountryDAO = iAmsSysCountryDAO;
	}
	/**
	 * @return the iAmsCustomerServiceDAO
	 */
	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDAO() {
		return iAmsCustomerServiceDAO;
	}
	/**
	 * @param iAmsCustomerServiceDAO the iAmsCustomerServiceDAO to set
	 */
	public void setiAmsCustomerServiceDAO(
			IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO) {
		this.iAmsCustomerServiceDAO = iAmsCustomerServiceDAO;
	}

	public IFxSummaryRateDAO<FxSummaryRate> getSummaryRateDAO() {
		return summaryRateDAO;
	}

	public void setSummaryRateDAO(IFxSummaryRateDAO<FxSummaryRate> summaryRateDAO) {
		this.summaryRateDAO = summaryRateDAO;
	}
	public TransferMoneyInfo getTransferMoneyInfo(String customerId, Integer serviceType, Double amount, BigDecimal rate, String currencyCode, String appDate, String depositId) {
		TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
		String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
		transferMoneyInfo.setTransferMoneyId(transferMoneyId);
		transferMoneyInfo.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		transferMoneyInfo.setCustomerId(customerId);
		transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
		transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
		transferMoneyInfo.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
		transferMoneyInfo.setTransferTo(serviceType);
		transferMoneyInfo.setTransferMoney(amount);
//		transferMoneyInfo.setWlRefId(netellerResponseInfo.getTransactionId());
		transferMoneyInfo.setRate(rate);
		transferMoneyInfo.setCurrencyCode(currencyCode);
		transferMoneyInfo.setTranferAcceptDate(appDate);
		transferMoneyInfo.setTranferAcceptDateTime(new Timestamp(System.currentTimeMillis()));
		transferMoneyInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		transferMoneyInfo.setInputDate(new Timestamp(System.currentTimeMillis()));
		transferMoneyInfo.setSourceId(depositId);
		transferMoneyInfo.setDestinationAmount(amount.doubleValue());
		transferMoneyInfo.setDestinationCurrencyCode(currencyCode);
		return transferMoneyInfo;
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

	/**
	 * @return the amsCustomerSurveyDAO
	 */
	public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}

	/**
	 * @param amsCustomerSurveyDAO the amsCustomerSurveyDAO to set
	 */
	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}

	public BigDecimal getFrontRate(String fromCurrencyCode, String toCurrencyCode) {
		return this.getFrontRate(fromCurrencyCode, toCurrencyCode);
	}

	public RateInfo getRateInfo(String fromCurrencyCode, String toCurrencyCode) {
		RateInfo rateInfo = new RateInfo();
		BigDecimal frontRate = new BigDecimal("0");
		if(fromCurrencyCode == null || toCurrencyCode == null) {
			return null;
		}
		if(fromCurrencyCode.equals(toCurrencyCode)) {
			rateInfo.setRate(new BigDecimal("1"));
			rateInfo.setRateType(IConstants.RATE_TYPE.BID);
			return rateInfo;
		}
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
	public Map<String, String> getMapPaymentGW(Integer cardType) {
		if(cardType == null) return null;
		Map<String, String> mapPayment = new HashMap<String, String>();
		List<AmsPaymentgw> listAmsPaymentgws = amsPaymentGwDAO.getListAmsPaymentGw(IConstants.ACTIVE_FLG.ACTIVE, cardType);
		if(listAmsPaymentgws != null && listAmsPaymentgws.size() > 0) {
			for(AmsPaymentgw item : listAmsPaymentgws) {
				mapPayment.put(StringUtil.toString(item.getPaymentgwId()), item.getPaymentgwName());				
			}
		}
		return mapPayment;
	}

	/**
	 * @return the amsPaymentGwDAO
	 */
	public IAmsPaymentgwDAO<AmsPaymentgw> getAmsPaymentGwDAO() {
		return amsPaymentGwDAO;
	}

	/**
	 * @param amsPaymentGwDAO the amsPaymentGwDAO to set
	 */
	public void setAmsPaymentGwDAO(IAmsPaymentgwDAO<AmsPaymentgw> amsPaymentGwDAO) {
		this.amsPaymentGwDAO = amsPaymentGwDAO;
	}
	public void updateRemarkCustomerSurvey(String customerId, Integer paymentMethod) {
		try{
            AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
            if(amsCustomerSurvey != null) {
                String paymentMethodDepMark = amsCustomerSurvey.getPaymentMethodDepMark();
                if(!StringUtil.isEmpty(paymentMethodDepMark)) {
                    String[] arrPaymentMethod = paymentMethodDepMark.split(",");
                    if(arrPaymentMethod != null) {
                        boolean isExist = Utilities.isExistArray(arrPaymentMethod, StringUtil.toString(paymentMethod));
                        if(!isExist) {
                            paymentMethodDepMark += "," + StringUtil.toString(paymentMethod);
                            amsCustomerSurvey.setPaymentMethodDepMark(paymentMethodDepMark);
                            amsCustomerSurveyDAO.merge(amsCustomerSurvey);
                        }
                    }
                } else {
                    amsCustomerSurvey.setPaymentMethodDepMark(StringUtil.toString(paymentMethod));
                    amsCustomerSurveyDAO.merge(amsCustomerSurvey);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
	}
	private void decryptCustomerEwalletInfo(CustomerEwalletInfo customerEwalletInfo, String privateKey, String publicKey) {
//		try {
//			if(!StringUtil.isEmpty(customerEwalletInfo.getEwalletAccNo())) {
//				String decryptData = Cryptography.decrypt(customerEwalletInfo.getEwalletAccNo(), privateKey, publicKey);
//				customerEwalletInfo.setEwalletAccNo(decryptData);
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
		try {
			if(!StringUtil.isEmpty(customerEwalletInfo.getEwalletApiName())) {
				String decryptData = Cryptography.decrypt(customerEwalletInfo.getEwalletApiName(), privateKey, publicKey);
				customerEwalletInfo.setEwalletApiName(decryptData);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		try {
			if(!StringUtil.isEmpty(customerEwalletInfo.getEwalletApiPassword())) {
				String decryptData = Cryptography.decrypt(customerEwalletInfo.getEwalletApiPassword(), privateKey, publicKey);
				customerEwalletInfo.setEwalletApiPassword(decryptData);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		try {
			if(!StringUtil.isEmpty(customerEwalletInfo.getEwalletSecureId())) {
				String decryptData = Cryptography.decrypt(customerEwalletInfo.getEwalletSecureId(), privateKey, publicKey);
				customerEwalletInfo.setEwalletSecureId(decryptData);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
//		try {
//			if(!StringUtil.isEmpty(customerEwalletInfo.getEwalletSecureWord())) {
//				String decryptData = Cryptography.decrypt(customerEwalletInfo.getEwalletSecureWord(), privateKey, publicKey);
//				customerEwalletInfo.setEwalletSecureWord(decryptData);
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
	}
	private void decryptCustomerCreditCardInfo(CreditCardInfo creditCardInfo, String privateKey, String publicKey) {
		try {
			if(!StringUtil.isEmpty(creditCardInfo.getCcCvv())) {
				String decryptData = Cryptography.decrypt(creditCardInfo.getCcCvv(), privateKey, publicKey);
				creditCardInfo.setCcCvv(decryptData);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		try {
			if(!StringUtil.isEmpty(creditCardInfo.getCcNo())) {
				String decryptData = Cryptography.decrypt(creditCardInfo.getCcNo(), privateKey, publicKey);
				creditCardInfo.setCcNo(decryptData);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	public BigDecimal rounding(BigDecimal amount, String currencyCode) {
		return super.rounding(amount, currencyCode);
	}
	@SuppressWarnings("unused")
	private void updateNetDeposit(Integer serviceType, String customerId, String currencyCode, Double amount) {
		try {
			AmsCashBalanceId id = new AmsCashBalanceId();
			id.setCurrencyCode(currencyCode);
			id.setCustomerId(customerId);
			id.setServiceType(serviceType);
			AmsCashBalance amsCashBalance = iAmsCashBalanceDAO.findById(AmsCashBalance.class, id);
			if(amsCashBalance != null) {
				amsCashBalance.setNetDepositAmount(amsCashBalance.getNetDepositAmount() + amount.doubleValue());
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				iAmsCashBalanceDAO.merge(amsCashBalance);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public AmsWhitelabelConfig getAmsWhitelabelConfig(String configKey, String wlCode) {
		return iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(configKey, wlCode);
	}

	@Override
	public AmsCashflow insertBjpCashFlow(AmsCashflow amsCashFlow) {
		String cashFlowId = generateUniqueId(IConstants.UNIQUE_CONTEXT.CASHFLOW_CONTEXT);
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setCashflowId(cashFlowId);		
		amsCashFlow.setEventDatetime(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setEventDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
		amsCashFlow.setValueDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
		Double balance = iAmsCashBalanceDAO.getBalance(amsCashFlow.getAmsCustomer().getCustomerId(),amsCashFlow.getCurrencyCode(), 0) ;
		amsCashFlow.setCashBalance(balance+amsCashFlow.getCashflowAmount());
		amsCashFlow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCashFlow.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsCashFlow.setUpdateDate(new Timestamp(System.currentTimeMillis()));		
		iAmsCashFlowDAO.save(amsCashFlow);	
		return amsCashFlow;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AmsCashBalance updateBjpCashBalance(String customerId,
			String currencyCode, Integer serviceType, Double amount, Double tranFee) {
		List<AmsCashBalance> list = iAmsCashBalanceDAO.findByCustomerId(customerId);
		AmsCashBalance amsCashBalance=null;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			AmsCashBalance amsCashBalance1 = (AmsCashBalance) iterator.next();
			if(amsCashBalance1.getAmsCustomerService()==null||amsCashBalance1.getAmsCustomerService().getServiceType()==0){
				amsCashBalance=amsCashBalance1;
				break;
			}
		}
		if(amsCashBalance!=null){
			Double balance = amsCashBalance.getCashBalance();
			amsCashBalance.setPreviousBalance(balance);
			amsCashBalance.setCashBalance(balance+amount- Math.abs(tranFee));
			amsCashBalance.setNetDepositAmount(amsCashBalance.getNetDepositAmount()+amount);
			amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			iAmsCashBalanceDAO.attachDirty(amsCashBalance);
		}
		return amsCashBalance;
	}
	@Override
	public void updateBjpUploadDeposit(String depositId, String errorCode,String csvFileName) {
		AmsDeposit dep = iAmsDepositDAO.findById(AmsDeposit.class, depositId);
		if(dep!=null){
		dep.setStatus(1);
		dep.setErrorCode(errorCode);
		dep.setCsvFilename(csvFileName);
		dep.setDepositCompletedDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
		dep.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
		dep.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		dep.setDepositCompletedDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
		dep.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
		dep.setConfirmDate(new Timestamp(System.currentTimeMillis()));
		List<AmsErrorCode> lErr = amsErrorCodeDAO.findByErrorCode(errorCode);
		if(lErr!=null&&lErr.size()>0){
			dep.setRemark(amsErrorCodeDAO.findByErrorCode(errorCode).get(0).getErrorMessage());
		}else{
			dep.setRemark(errorCode);
		}
		iAmsDepositDAO.attachClean(dep);
		}
	}
	@Override
	public void updateBjpDeposit(String depositId, String tranResonCode,Double depositFee) {
		AmsDeposit dep = iAmsDepositDAO.findById(AmsDeposit.class, depositId);
		if(dep!=null){
			dep.setStatus(1);
			dep.setErrorCode(tranResonCode);
			dep.setDepositCompletedDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
			dep.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
			dep.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			dep.setDepositFee(depositFee);
			dep.setDepositCompletedDate(iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate());
			dep.setDepositCompletedDatetime(new Timestamp(System.currentTimeMillis()));
			iAmsDepositDAO.attachClean(dep);
		}
	}

	@Override
	public void updateBjpDepositRef(String depositId, String tranId,String tranDigest) {
		AmsDepositRef depRef = iAmsDepositRefDAO.findById(AmsDepositRef.class, depositId);
		if(depRef!=null){
			depRef.setGwRefId(tranId);
			if(tranDigest!=null&&tranDigest.trim()!=""){
				depRef.setEwalletId(Integer.parseInt(tranDigest));
			}
			depRef.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			iAmsDepositRefDAO.attachDirty(depRef);
		}
	}

	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IDepositManager#updateBjpDepositFail(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateBjpDepositFail(AmsDeposit dep, Integer status, String errorCode) {
		try {
			if(dep!=null){
				dep.setStatus(status);
				dep.setErrorCode(errorCode);
				List<AmsErrorCode> lErr = amsErrorCodeDAO.findByErrorCode(errorCode);
				if(lErr!=null&&lErr.size()>0){
					dep.setRemark(amsErrorCodeDAO.findByErrorCode(errorCode).get(0).getErrorMessage());
				}else{
					dep.setRemark(errorCode);
				}
				dep.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				dep.setConfirmDate(new Timestamp(System.currentTimeMillis()));
				iAmsDepositDAO.merge(dep);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
	
	@Override
	public void updateBjpUploadDepositFail(String depositId, String errorCode, String csvFileName) {
		try {
			AmsDeposit dep = iAmsDepositDAO.findById(AmsDeposit.class, depositId);
			if(dep!=null){
				if(dep.getStatus().compareTo(BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS) == 0){
					if(BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equalsIgnoreCase(errorCode)){
						dep.setStatus(BJP_CONFIG.DEPOSIT_STATUS_FAIL);
					}else if(BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equalsIgnoreCase(errorCode)){
						dep.setStatus(BJP_CONFIG.DEPOSIT_STATUS_SUCCESS);
					}
				}
				dep.setErrorCode(errorCode);
				List<AmsErrorCode> lErr = amsErrorCodeDAO.findByErrorCode(errorCode);
				if(lErr!=null&&lErr.size()>0){
					dep.setRemark(amsErrorCodeDAO.findByErrorCode(errorCode).get(0).getErrorMessage());
				}else{
					dep.setRemark(errorCode);
				}
				dep.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				dep.setConfirmDate(new Timestamp(System.currentTimeMillis()));
				dep.setConfirmOperationId(null);
				dep.setCsvFilename(csvFileName);
				iAmsDepositDAO.attachDirty(dep);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}

	/**
	 * @return the amsErrorCodeDAO
	 */
	public IAmsErrorCodeDAO<AmsErrorCode> getAmsErrorCodeDAO() {
		return amsErrorCodeDAO;
	}
	/**
	 * @return the jmsContextSender
	 */
	public IJmsContextSender getJmsContextSender() {
		return jmsContextSender;
	}
	public void setJmsContextSender(IJmsContextSender jmsContextSender) {
		this.jmsContextSender = jmsContextSender;
	}
	/**
	 * @param amsErrorCodeDAO the amsErrorCodeDAO to set
	 */
	public void setAmsErrorCodeDAO(IAmsErrorCodeDAO<AmsErrorCode> amsErrorCodeDAO) {
		this.amsErrorCodeDAO = amsErrorCodeDAO;
	}

	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IDepositManager#getBjpDepositFail(java.lang.String)
	 */
	@Override
	public AmsDeposit getBjpDeposit(String depositId) {
		return iAmsDepositDAO.findById(AmsDeposit.class, depositId);
	
	}

	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IDepositManager#sendMailBjpDeposit(phn.com.nts.db.entity.AmsDeposit)
	 */
	@Override
	public boolean sendMailBjpDeposit(AmsDeposit amsDeposit,int mode,String methodName)  throws Exception{
		try {
			log.info("[start] send mail bjp deposit deposit id " + amsDeposit.getDepositId());
			String language="_JA";
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_FAIL + "_" + language); //  mail template lack DEPOSIT
			AmsCustomer amsCustomer = amsDeposit.getAmsCustomer();
			String mailCode= "";
			AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
			amsMailTemplateInfo.setWlCode(ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
			DecimalFormat df = new DecimalFormat("###,###");
			amsMailTemplateInfo.setDepositAmount(  df.format(amsDeposit.getDepositAmount()));
			amsMailTemplateInfo.setCurrency(amsDeposit.getCurrencyCode());
			amsMailTemplateInfo.setDepositCurrency(amsDeposit.getCurrencyCode());
			amsMailTemplateInfo.setDepositId(amsDeposit.getDepositId());
			amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
			amsMailTemplateInfo.setDepositDate(amsDeposit.getDepositAcceptDate());
			amsMailTemplateInfo.setDepositCancelReason(amsDeposit.getRemark());
			amsMailTemplateInfo.setDepositMethod(methodName);
			amsMailTemplateInfo.setRemark(amsDeposit.getRemark());
			if(mode==BJP_CONFIG.DEPOSIT_STATUS_SUCCESS){
				mailCode= IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS+language;
			}else if(mode==BJP_CONFIG.DEPOSIT_STATUS_CANCEL){
				mailCode= IConstants.MAIL_TEMPLATE.AMS_DEP_CANCEL+language;
			}else if(mode==BJP_CONFIG.DEPOSIT_STATUS_FAIL){
				mailCode= IConstants.MAIL_TEMPLATE.AMS_DEP_FAIL+language;
			}
			HashMap<String, String> to = new HashMap<String, String>();
			to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
			amsMailTemplateInfo.setTo(to);																			
			amsMailTemplateInfo.setMailCode(mailCode);
			amsMailTemplateInfo.setSubject(mailCode);
			amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
			jmsContextSender.sendMail(amsMailTemplateInfo,false);
			log.info("[end] send mail bjp deposit for deposit id " + amsDeposit.getDepositId());
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IDepositManager#sendMailBjpWarning(phn.com.nts.db.entity.AmsDeposit, phn.com.nts.db.entity.AmsCustomer, phn.nts.ams.fe.domain.BjpDepositInfo)
	 */
	@Override
	public boolean sendMailBjpWarning(List<BjpDepositInfo> listDepositWarning,String fileName) throws Exception {
		try {
			log.info("[start] send mail bjp deposit warning to OM ");
			String language="_JA";
			String mailCode= ITrsConstants.MAIL_TEMPLATE.AMS_KESHIKOMI_RESULT+language;
			HashMap<String, String> to = new HashMap<String, String>();
			AmsWhitelabelConfig mailAdminConfig = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_ADMIN, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			if(mailAdminConfig != null){
				 to.put("Admin", mailAdminConfig.getConfigValue());
			}
//			to.put("Admin", AppConfiguration.getMailAdminSender());
			AmsWhitelabelConfig mailAccounting = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_ACCOUNTANT, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			if(mailAccounting != null){
				 to.put("Accounting", mailAccounting.getConfigValue());
			}
//			to.put("Accounting", AppConfiguration.getMailAccounting());
			HashMap<String, String> from = new HashMap<String, String>();
			AmsWhitelabelConfig amsWhitelabelConfig1 = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			String mailFrom = "";
			if(amsWhitelabelConfig1 != null){
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			TrsMailTemplateInfo amsMailTemplateInfo = new TrsMailTemplateInfo();
//			AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
			amsMailTemplateInfo.setWlCode(ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			amsMailTemplateInfo.setTo(to);																			
			amsMailTemplateInfo.setMailCode(mailCode);
			amsMailTemplateInfo.setSubject(mailCode);
			amsMailTemplateInfo.setFrom(from);
			amsMailTemplateInfo.setLogFileName(fileName);
			amsMailTemplateInfo.setDepositMethod("BJP deposit");
			StringBuilder remark= new StringBuilder("<br/>");
			if(listDepositWarning!=null)
			for(int i=0;i<listDepositWarning.size();i++){
//				remark.append("DEPOSIT_ACCEPT_DATE=");
				remark.append(listDepositWarning.get(i).getDEPOSIT_ACCEPT_DATE());
//				remark.append("|DEPOSIT_AMOUNT=");
				remark.append(listDepositWarning.get(i).getDEPOSIT_AMOUNT());
//				remark.append("|ACCOUNT_NAME_KANA=");
				remark.append(listDepositWarning.get(i).getACCOUNT_NAME_KANA());
//				remark.append("|BANK_NAME_KANA=");
				remark.append(listDepositWarning.get(i).getBANK_NAME_KANA());
//				remark.append("|BRANCH_NAME_KANA=");
				remark.append(listDepositWarning.get(i).getBRANCH_NAME_KANA());
//				remark.append("|REMARK=");
				remark.append(listDepositWarning.get(i).getREMARK());
//				remark.append("|CHECK_MEIGI_STAT=");
				remark.append(listDepositWarning.get(i).getCHECK_MEIGI_STAT());
//				remark.append("|DEPOSIT_ID=");
				remark.append(listDepositWarning.get(i).getDEPOSIT_ID());
				remark.append("<br/>");
				
			}
			amsMailTemplateInfo.setRemark(remark.toString());
			jmsContextSender.sendMail(amsMailTemplateInfo,false);
			log.info("[end] send mail bjp deposit warning to OM ");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("KESHIKOMI"+e.getMessage());
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IDepositManager#keshicomiUpdateDepositRef(phn.com.nts.db.entity.AmsDepositRef)
	 */
	@Override
	public AmsDepositRef keshicomiUpdateDepositRef(AmsDepositRef amsDepositRef) {
		try {
			log.info("[start] updateDepositRef");
			getiAmsDepositRefDAO().attachDirty(amsDepositRef);
			log.info("[end] updateDepositRef");
			return amsDepositRef;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void sendMailAbnormalUpdateDepositStatus(String customerId, String customerName, String depositId, String currentSts, String receivedSts) {
		try {
			HashMap<String, String> to = new HashMap<String, String>();
			AmsWhitelabelConfig mailAdminConfig = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_ADMIN, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			if(mailAdminConfig != null){
				 to.put("Admin", mailAdminConfig.getConfigValue());
			}
			AmsWhitelabelConfig mailAccounting = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_ACCOUNTANT, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			if(mailAccounting != null){
				 to.put("Accounting", mailAccounting.getConfigValue());
			}
			AmsWhitelabelConfig mailOm = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_OM, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			if(mailOm != null){
				 to.put("OM", mailOm.getConfigValue());
			}
			HashMap<String, String> from = new HashMap<String, String>();
			AmsWhitelabelConfig amsWhitelabelConfig1 = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			String mailFrom = "";
			if(amsWhitelabelConfig1 != null){
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			
			String language="_JA";
			String mailCode= ITrsConstants.MAIL_TEMPLATE.AMS_DEP_NO_CHANGED+language;
			
			TrsMailTemplateInfo amsMailTemplateInfo = new TrsMailTemplateInfo();
			amsMailTemplateInfo.setWlCode(ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
			amsMailTemplateInfo.setTo(to);																			
			amsMailTemplateInfo.setMailCode(mailCode);
			amsMailTemplateInfo.setSubject(mailCode);
			amsMailTemplateInfo.setFrom(from);
			amsMailTemplateInfo.setFullName(customerName);
			amsMailTemplateInfo.setCustomerId(customerId);
			amsMailTemplateInfo.setDepositId(depositId);
			StringBuffer remark = new StringBuffer();
			remark.append("current status: "+currentSts);
			remark.append(",").append("received status: "+receivedSts);
			amsMailTemplateInfo.setRemark(remark.toString());
			amsMailTemplateInfo.setDepositDate(DateUtil.toString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD_WITH_SLASH));
			
			log.info("[start] sending email for abnormal deposit status update");
			jmsContextSender.sendMail(amsMailTemplateInfo,false);
			log.info("[end] sending email for abnormal deposit status update");
		} catch (Exception e) {
			log.warn("Fail when sending email for abnormal deposit status update", e);
		}
		
	}
	
	/**
	 * Get DepositRef By depositRefId
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Aug 11, 2015
	 * @MdDate
	 */
	public AmsDepositRef getDepositRefById(String depositRefId) {
		log.info("[start] get AmsDepositRef By Id: " + depositRefId);
		
		AmsDepositRef amsDepositRef = getiAmsDepositRefDAO().findById(AmsDepositRef.class, depositRefId);
		
		log.info("[end] get AmsDepositRef By Id: " + depositRefId + ", amsDepositRef: " + amsDepositRef);
		return amsDepositRef;
	}
	
}
