package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCashflowDAO;
import phn.com.nts.db.dao.IAmsCustomerAkazanDAO;
import phn.com.nts.db.dao.IAmsCustomerBankDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerEwalletDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsDepositDAO;
import phn.com.nts.db.dao.IAmsExchangerDAO;
import phn.com.nts.db.dao.IAmsExchangerSymbolDAO;
import phn.com.nts.db.dao.IAmsPaymentGwPrioDAO;
import phn.com.nts.db.dao.IAmsPaymentgwBalanceDAO;
import phn.com.nts.db.dao.IAmsPaymentgwWlDAO;
import phn.com.nts.db.dao.IAmsSysCountryDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWithdrawalDAO;
import phn.com.nts.db.dao.IAmsWithdrawalFeeDAO;
import phn.com.nts.db.dao.IAmsWithdrawalRefDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysOperationDAO;
import phn.com.nts.db.dao.ISysOperationLogDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashBalanceId;
import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerAkazan;
import phn.com.nts.db.entity.AmsCustomerBank;
import phn.com.nts.db.entity.AmsCustomerEwallet;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsExchanger;
import phn.com.nts.db.entity.AmsExchangerSymbol;
import phn.com.nts.db.entity.AmsPaymentGwPrio;
import phn.com.nts.db.entity.AmsPaymentgwBalance;
import phn.com.nts.db.entity.AmsPaymentgwBalanceId;
import phn.com.nts.db.entity.AmsPaymentgwWl;
import phn.com.nts.db.entity.AmsPaymentgwWlId;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.db.entity.AmsWithdrawalFee;
import phn.com.nts.db.entity.AmsWithdrawalFeeId;
import phn.com.nts.db.entity.AmsWithdrawalRef;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysOperation;
import phn.com.nts.db.entity.SysOperationLog;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Cryptography;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.WithdrawalInfo;
import phn.nts.ams.fe.domain.WithdrawalRuleInfo;
import phn.nts.ams.fe.domain.converter.ExchangerSymbolConverter;
import phn.nts.ams.fe.model.WithdrawalModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

public class WithdrawalManagerImpl implements IWithdrawalManager {
	private static final Logit log = Logit.getInstance(WithdrawalManagerImpl.class);
	private IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO;
	private IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO;
	private IAmsCustomerAkazanDAO<AmsCustomerAkazan> iAmsCustomerAkazanDAO;
	private IAmsWithdrawalRefDAO<AmsWithdrawalRef> iAmsWithdrawalRefDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private IAmsCashflowDAO<AmsCashflow> iAmsCashFlowDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	//private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO;
	private ISysOperationDAO<SysOperation> iSysOperationDAO;
	private ISysOperationLogDAO<SysOperationLog> iSysOperationLogDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IAmsCustomerEwalletDAO<AmsCustomerEwallet> iAmsCustomerEwalletDAO;
	private IAmsCustomerBankDAO<AmsCustomerBank> iAmsCustomerBankDAO;
	private IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO;
	private IBalanceManager balanceManager;
	private IAmsPaymentgwBalanceDAO<AmsPaymentgwBalance> iAmsPaymentgwBalanceDAO;
	private IAmsPaymentgwWlDAO<AmsPaymentgwWl> iAmsPaymentgwWlDAO;
	private IAmsWithdrawalFeeDAO<AmsWithdrawalFee> iAmsWithdrawalFeeDAO;
	private IAmsDepositDAO<AmsDeposit> amsDepositDAO;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
	private IAmsPaymentGwPrioDAO<AmsPaymentGwPrio> amsPaymentGWPrioDAO;
	private IAmsExchangerDAO<AmsExchanger> amsExchangerDAO;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO;
	
	public IAmsExchangerDAO<AmsExchanger> getAmsExchangerDAO() {
		return amsExchangerDAO;
	}
	public void setAmsExchangerDAO(IAmsExchangerDAO<AmsExchanger> amsExchangerDAO) {
		this.amsExchangerDAO = amsExchangerDAO;
	}
	public IAmsPaymentGwPrioDAO<AmsPaymentGwPrio> getAmsPaymentGWPrioDAO() {
		return amsPaymentGWPrioDAO;
	}
	public void setAmsPaymentGWPrioDAO(
			IAmsPaymentGwPrioDAO<AmsPaymentGwPrio> amsPaymentGWPrioDAO) {
		this.amsPaymentGWPrioDAO = amsPaymentGWPrioDAO;
	}
	public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}
	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}
	public IAmsDepositDAO<AmsDeposit> getAmsDepositDAO() {
		return amsDepositDAO;
	}
	public void setAmsDepositDAO(IAmsDepositDAO<AmsDeposit> amsDepositDAO) {
		this.amsDepositDAO = amsDepositDAO;
	}
	@Override
	public LinkedHashMap<String, String> getListCountry() {
		log.info("[Start] get list country in system");
		LinkedHashMap<String, String> listCountry = null;
		try {
			List<AmsSysCountry> listCountries = new ArrayList<AmsSysCountry>();			
			//listCountries = getiAmsSysCountryDAO().findByActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			listCountries = getiAmsSysCountryDAO().findByActiveFlgOrderByName(IConstants.ACTIVE_FLG.ACTIVE);
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
	/**
	 * @return the iAmsSysCountryDAO
	 */
	public IAmsSysCountryDAO<AmsSysCountry> getiAmsSysCountryDAO() {
		return iAmsSysCountryDAO;
	}
	/**
	 * @param iAmsSysCountryDAO the iAmsSysCountryDAO to set
	 */
	public void setiAmsSysCountryDAO(IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO) {
		this.iAmsSysCountryDAO = iAmsSysCountryDAO;
	}
	@Override
	public List<WithdrawalInfo> getWithdrawlHistory(String customerId,PagingInfo pagingInfo, String status) {
		Map<String, String> mapWithdrawalMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.WITHDRAWAL_METHOD);
		Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		List<AmsWithdrawal> listAmsWithdrawal = null;
		List<WithdrawalInfo> listWithdrawalInfo = new ArrayList<WithdrawalInfo>();
		WithdrawalInfo withdrawalInfo = null;
		Integer serviceType = 0;
		
		try{
			
			listAmsWithdrawal = getiAmsWithdrawalDAO().getWithdrawalHistory(customerId, pagingInfo, status);
			for (AmsWithdrawal amsWithdrawal : listAmsWithdrawal) {
				withdrawalInfo = new WithdrawalInfo();				
				BeanUtils.copyProperties(amsWithdrawal, withdrawalInfo);
				withdrawalInfo.setMethodName(mapWithdrawalMethod.get(StringUtil.toString(withdrawalInfo.getWithdrawalMethod())));
				serviceType = amsWithdrawal.getServiceType();
				if(IConstants.SERVICES_TYPE.AMS.equals(serviceType)){
					withdrawalInfo.setServiceTypeName(mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
				}else if(IConstants.SERVICES_TYPE.BO.equals(serviceType)){
					withdrawalInfo.setServiceTypeName(mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));					
				}else if(IConstants.SERVICES_TYPE.FX.equals(serviceType)){
					withdrawalInfo.setServiceTypeName(mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
				}
				listWithdrawalInfo.add(withdrawalInfo);
				
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}	
		return listWithdrawalInfo;
		
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
	public void setiAmsWithdrawalDAO(IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO) {
		this.iAmsWithdrawalDAO = iAmsWithdrawalDAO;
	}	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 15, 2012
	 * @MdDate
	 */
	/*public Integer withdrawalBankTransfer(WithdrawalInfo withdrawalInfo, String customerServiceId) {
		log.info("[Start] withdrawal banktransfer");
		Integer resultForUpdate = IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NULL;		
		try {
			if(withdrawalInfo != null) {
				//save AmsWithdrawal
				resultForUpdate = updateBalanceFE(withdrawalInfo, customerServiceId);		
				//save
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);			
		}
		log.info("[End] withdrawal banktransfer");
		return resultForUpdate;
	}*/
	/**
	 * 　 withdrawal via Bank transfer info
	 * 
	 * @param 
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public Integer withdrawalBankTransfer(CustomerBankInfo bankwireInfo) {
		log.info("[START] withdrawl transaction via bank transfer");
		//save AmsWithdrawal 
		try {		
			BigDecimal amount =  bankwireInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + bankwireInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			log.info("format amount" + amount.doubleValue());
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(bankwireInfo.getCustomerId(), bankwireInfo.getCurrencyCode(), amount, bankwireInfo.getServiceType())) {
				log.info("not enough money to withdrawal");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}	
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(bankwireInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(bankwireInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(bankwireInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 16, 2012D - Start 
			//amsWithdrawal.setWithdrawalAmount(amount.doubleValue());
			amsWithdrawal.setWithdrawalAmount(bankwireInfo.getReceivedAmount());
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 16, 2012D - End
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
//			amsWithdrawal.setRegCustomerId(bankwireInfo.getCustomerId());
			amsWithdrawal.setServiceType(bankwireInfo.getServiceType());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
			amsWithdrawal.setWithdrawalFee(bankwireInfo.getWithdrawalFee());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
			
			//save ref
			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			AmsCustomerBank amsCustomerBank = null;
			if(bankwireInfo.getCustomerBankId() !=null){
				amsCustomerBank = getiAmsCustomerBankDAO().findById(AmsCustomerBank.class, bankwireInfo.getCustomerBankId());
				if(amsCustomerBank!=null){
					amsWithdrawalRef.setAmsCustomerBank(amsCustomerBank);
					amsWithdrawalRef.setCustomerBankId(bankwireInfo.getCustomerBankId());
				}
			}/*else{
				amsCustomerBank = new AmsCustomerBank();
				amsCustomerBank.setCustomerId(bankwireInfo.getCustomerId());
				amsCustomerBank.setCountryId(bankwireInfo.getCountryId());
				amsCustomerBank.setBankName(bankwireInfo.getBankName());
				amsCustomerBank.setBankAddress(bankwireInfo.getBankAddress());
				amsCustomerBank.setSwiftCode(bankwireInfo.getSwiftCode());
				amsCustomerBank.setBranchName(bankwireInfo.getBranchName());
				amsCustomerBank.setAccountNo(bankwireInfo.getAccountNo());
				amsCustomerBank.setAccountName(bankwireInfo.getAccountName());
				amsCustomerBank.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerBank.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
				amsCustomerBank.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
				getiAmsCustomerBankDAO().save(amsCustomerBank);
				amsWithdrawalRef.setAmsCustomerBank(amsCustomerBank);
			}
			*/
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawalRef.setBeneficiaryAccountName(bankwireInfo.getAccountName());
			/*amsWithdrawalRef.setBeneficiaryBankAddress(bankwireInfo.getBankAddress());*/
			amsWithdrawalRef.setBeneficiaryAccountNo(bankwireInfo.getAccountNo());
			amsWithdrawalRef.setBeneficiaryBankName(bankwireInfo.getBankName());
			amsWithdrawalRef.setBeneficiaryBranchName(bankwireInfo.getBranchName());
			/*amsWithdrawalRef.setBeneficiarySwiftCode(bankwireInfo.getSwiftCode());
			amsWithdrawalRef.setCountryId(bankwireInfo.getCountryId());*/
			
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);			
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount + "register successfully!");
			//end save withdrawal ref
			log.info("[END] withdrawl transaction via bank transfer");
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
	
	public Integer withdrawalExchanger(ExchangerInfo exchangerInfo) {
		log.info("[START] withdrawal transaction via exchanger");
		try {			
			BigDecimal amount = exchangerInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + exchangerInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(exchangerInfo.getCustomerId(), exchangerInfo.getCurrencyCode(), amount, exchangerInfo.getServiceType())) {
				log.info("not enough money to withdraw");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}	
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			//save AmsWithdrawal 
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(exchangerInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(exchangerInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(exchangerInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalAmount(amount.doubleValue());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.EXCHANGER);
			amsWithdrawal.setRegCustomerId(exchangerInfo.getCustomerId());
			amsWithdrawal.setServiceType(exchangerInfo.getServiceType());
			amsWithdrawal.setWithdrawalFee(new Double(0));
			//save ref
			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			AmsCustomerBank amsCustomerBank = null;
			if(exchangerInfo.getCustomerBankId() !=null){
				amsCustomerBank = getiAmsCustomerBankDAO().findById(AmsCustomerBank.class, exchangerInfo.getCustomerBankId());
				if(amsCustomerBank!=null){
					amsWithdrawalRef.setAmsCustomerBank(amsCustomerBank);
					amsWithdrawalRef.setCustomerBankId(exchangerInfo.getCustomerBankId());
				}
			}
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawalRef.setBeneficiaryAccountName(exchangerInfo.getAccountName());
			/*amsWithdrawalRef.setBeneficiaryBankAddress(exchangerInfo.getBankAddress());*/
			amsWithdrawalRef.setBeneficiaryAccountNo(exchangerInfo.getAccountNo());
			amsWithdrawalRef.setBeneficiaryBankName(exchangerInfo.getBankName());
			amsWithdrawalRef.setBeneficiaryBranchName(exchangerInfo.getBranchName());
			/*amsWithdrawalRef.setBeneficiarySwiftCode(exchangerInfo.getSwiftCode());
			amsWithdrawalRef.setCountryId(exchangerInfo.getCountryId());*/
			//set exchanger ID
			amsWithdrawalRef.setExchangerId(exchangerInfo.getExchangerId());
			//set rate
			ExchangerSymbolInfo exchangerSymbolInfo = getExchangerSymbol(exchangerInfo.getExchangerId());
			if(exchangerInfo != null){
				amsWithdrawalRef.setRate(MathUtil.parseDouble(exchangerSymbolInfo.getBuyRate()));
			}
			
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);			
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount.doubleValue() + "register successfully!");
			//end save withdrawal ref
			log.info("[END] withdrawal transaction via exchanger");
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
	
	public Integer withdrawalCreditCard(WithdrawalInfo withdrawalInfo){
		try {			
			BigDecimal amount = MathUtil.parseBigDecimal(withdrawalInfo.getWithdrawalAmount());
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + withdrawalInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(withdrawalInfo.getCustomerId(), withdrawalInfo.getCurrencyCode(), amount, withdrawalInfo.getServiceType())) {
				log.info("not enough money to withdrawal");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}	

			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(withdrawalInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(withdrawalInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(withdrawalInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			/*amsWithdrawal.setWithdrawalAmount(amount.doubleValue());*/
			amsWithdrawal.setWithdrawalAmount(withdrawalInfo.getReceivedAmount());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
//			amsWithdrawal.setRegCustomerId(withdrawalInfo.getCustomerId());
			amsWithdrawal.setServiceType(withdrawalInfo.getServiceType());
			amsWithdrawal.setWithdrawalFee(withdrawalInfo.getWithdrawalFee());
			
			//save ref
			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawalRef.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);			
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount.doubleValue() + "register successfully!");
			//end save withdrawal ref
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
	
	/**
	 * 　withdrawal via netteller
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public Integer withdrawalNeteller(NetellerInfo netellerInfo){
		try {			
			BigDecimal amount = netellerInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + netellerInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(netellerInfo.getCustomerId(), netellerInfo.getCurrencyCode(), amount, netellerInfo.getServiceType())) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(netellerInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(netellerInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(netellerInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			/*amsWithdrawal.setWithdrawalAmount(amount.doubleValue());*/
			amsWithdrawal.setWithdrawalAmount(netellerInfo.getReceivedAmount());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.NETELLER);
//			amsWithdrawal.setRegCustomerId(netellerInfo.getCustomerId());
			amsWithdrawal.setServiceType(netellerInfo.getServiceType());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
			amsWithdrawal.setWithdrawalFee(netellerInfo.getWithdrawalFee());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
			//save ref
			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);			
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawalRef.setEwalletAccNo(netellerInfo.getAccountId());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.NETELLER);
			amsWithdrawalRef.setCountryId(null);
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			
			
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount.doubleValue() + "register successfully!");
			//end save withdrawal ref
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
	/**
	 *  withdrawal via Payza
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public Integer withdrawalPayza(PayzaInfo payzaInfo){
		try {			
			BigDecimal amount = payzaInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + payzaInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(payzaInfo.getCustomerId(), payzaInfo.getCurrencyCode(),amount, payzaInfo.getServiceType())) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			//save AmsWithdrawal
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(payzaInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(payzaInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(payzaInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalAmount(amount.doubleValue());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.PAYZA);
//			amsWithdrawal.setRegCustomerId(payzaInfo.getCustomerId());
			amsWithdrawal.setServiceType(payzaInfo.getServiceType());

			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawalRef.setEwalletEmail(payzaInfo.getEmailAddress());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.PAYZA);
			amsWithdrawalRef.setCountryId(null);
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);
			//save ref					

			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
//	private synchronized String generateId(String contextID) {
//		if (contextID == null || contextID.trim().equals("")) {
//			return null;
//		}		
//		String strDate = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT);
//		String id = strDate;
//		Long counter = getiSysUniqueidCounterDAO().generateId(contextID);
//		String countString = counter.toString();
//		id += contextID;
//		String[] magicZeros = { "", // 0
//				"0", // 1
//				"00", // 2
//				"000", // 3
//				"0000", // 4
//				"00000", // 5
//				"000000", // 6
//				"0000000", // 7
//				"00000000", // 8
//				"000000000" // 9
//		};
//		
//		id += magicZeros[9 - countString.length()];
//		id += countString;
//
//		return id;
//	}
	private synchronized String generateUniqueId(String contextID) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}		
		String uniqueId = getiSysUniqueidCounterDAO().generateUniqueId(contextID);		
		return uniqueId;
	}
	/**
	 * @return the iAmsCustomerDAO
	 */
	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}
	/**
	 * @param iAmsCustomerDAO the iAmsCustomerDAO to set
	 */
	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}
	/**
	 * @return the iAmsWithdrawalRefDAO
	 */
	public IAmsWithdrawalRefDAO<AmsWithdrawalRef> getiAmsWithdrawalRefDAO() {
		return iAmsWithdrawalRefDAO;
	}
	/**
	 * @param iAmsWithdrawalRefDAO the iAmsWithdrawalRefDAO to set
	 */
	public void setiAmsWithdrawalRefDAO(
			IAmsWithdrawalRefDAO<AmsWithdrawalRef> iAmsWithdrawalRefDAO) {
		this.iAmsWithdrawalRefDAO = iAmsWithdrawalRefDAO;
	}
	
	public Integer withdrawalIBBankTransfer(WithdrawalInfo withdrawalInfo, String customerId) {		
		Integer resultForUpdate = IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NULL;
		try {
			if(withdrawalInfo!=null){
				resultForUpdate = updateBalanceFE(withdrawalInfo, customerId);											
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());			
		}
		
		return resultForUpdate;
		
	}
	public synchronized Integer updateBalanceFE(WithdrawalInfo withdrawalInfo, String customerServiceId) {
		log.info("<><><><><><><><><> Start updateBalance() function <><><><><><><><><>");
		try {			
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			BigDecimal amount =  MathUtil.parseBigDecimal(withdrawalInfo.getWithdrawalAmount());
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + withdrawalInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			log.info("format amount: "  + amount);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(withdrawalInfo.getCustomerId(), withdrawalInfo.getCurrencyCode(), amount, withdrawalInfo.getServiceType())) {
				log.info("not enough money to withdrawal");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}	
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(withdrawalInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(withdrawalInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(withdrawalInfo.getStatus());
			amsWithdrawal.setCurrencyCode(withdrawalInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
//			amsWithdrawal.setWithdrawalCompletedDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalAmount(amount.doubleValue());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark(withdrawalInfo.getRemark());
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
			amsWithdrawal.setRegCustomerId(withdrawalInfo.getRegCustomerId());
			amsWithdrawal.setWithdrawalFee(new Double(0));
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount + "register successfully!");
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			e.printStackTrace();		
			log.info("<><><><><><><><><> End updateBalance() function with [ERROR] <><><><><><><><><>");
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
		
	}
	/*public synchronized Integer updateBalance(WithdrawalInfo withdrawalInfo) {
		log.info("<><><><><><><><><> Start updateBalance() function <><><><><><><><><>");		
		AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
		String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
		if(withdrawalId != null){
			amsWithdrawal.setWithdrawalId(withdrawalId);
		}
		String customerId = withdrawalInfo.getCustomerId();
		Double volumeMt4 = 0 - withdrawalInfo.getWithdrawalAmount();
		// =========================================
		// Get & check customer's balance.
		BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerId);
		int balanceError = 0;
		if(balanceInfo != null) {
			Double freeMargin = balanceInfo.getFreemargin();
			if(freeMargin < withdrawalInfo.getWithdrawalAmount()) {
				log.info("Customer with ID: " + customerId
						+ " has input a withdraw amount bigger than his(her) balance! ");
				balanceError = 1;
			}
		} else {
			log.info("Cannot get balance for customer with ID: "
					+ customerId + ". Caught an unexpected error!");
			balanceError = 2;
		}		
		try {			
			if (balanceError != 0) {				
				if (balanceError == 1) {
					return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
				} else {
					return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_GET_BALANCE_FAILED;
				}
			}
			// if [OK] then send FundRecord object to ActiveMQ
			java.util.Date date = Calendar.getInstance().getTime();
			
			String sequenceID = MathUtil.generateRandomPassword(32);
			log.info("sequenceID: " + sequenceID);
			FundRecord fundRecord = new FundRecord();
			fundRecord.setActiveFlg(true);
			fundRecord.setCustomerId(MathUtil.parseInt(customerId, 0));		
			if(fundRecord.getCustomerId() == 0) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_INVALID_CUSTOMER_ID; 
			}
			
			fundRecord.setRequestDate(date.getTime());
			fundRecord.setVolume(volumeMt4);
			fundRecord.setSequenceID(sequenceID);
			fundRecord.setFundType(FundRecord.BALANCE);
			

			WithDrawMessage withDrawMessage = WithDrawMessage.getInstance(sequenceID);
			JMSSendClient.getInstance().sendWithdrawRequest(fundRecord);			
			
			int result = -1;
			int tryNumber = 0;
			boolean done = false;
			FundRecord receiveFundRrd = null;
			
			while (!done) {
				if (withDrawMessage.getFlag(sequenceID)) {
					receiveFundRrd = withDrawMessage.getFundRecord(sequenceID);
					if (receiveFundRrd != null) {
						result = receiveFundRrd.getResult();
						log.info("receiveFundRrd.getCustomerId(): "
								+ receiveFundRrd.getCustomerId());
						log.info("receiveFundRrd.getVolume(): "
								+ receiveFundRrd.getVolume());
						log.info("receiveFundRrd.getResult(): " + result);
						log.info("receiveFundRrd.getSequenceID(): "
								+ receiveFundRrd.getSequenceID());
						log.info("Message: "
								+ CommonError.getErrorMessage(result));
					}
					done = true;
				}
				
				Thread.sleep(1000);
				tryNumber++;
				if (tryNumber >= 30) {done = true;}
			}
			withDrawMessage.removeFundRecord(sequenceID);
			AmsCustomer amsCustomer = null; 
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if(frontUserOnline != null) {
					if(getiAmsCustomerDAO() != null){
						log.info("loginId: "  + frontUserOnline.getLoginId());
						amsCustomer =  getiAmsCustomerDAO().getAmsCustomer(frontUserOnline.getLoginId());"mai.thi.thu.huyen@gmail.com"
						amsWithdrawal.setCurrencyCode(frontUserOnline.getCurrencyCode());
					}					
				}
			}		
			if(amsCustomer != null){
				amsWithdrawal.setAmsCustomer(amsCustomer);			
			}else{
				amsWithdrawal.setAmsCustomer(new AmsCustomer());
			}
			amsWithdrawal.setServiceType(withdrawalInfo.getServiceType());
			
			amsWithdrawal.setStatus(withdrawalInfo.getStatus());
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalAmount(withdrawalInfo.getWithdrawalAmount());
							
			
			if(receiveFundRrd == null) {
													
				//save withdrawal		
				amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.FAIL);
				getiAmsWithdrawalDAO().save(amsWithdrawal);
				
				log.info("Received a null FundRecord!");
				log.info("<><><><><><><><><> End updateBalance() function with [ERROR] <><><><><><><><><>");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT;
			}
			
			if (result == IResultConstant.Withdraw.SUCCESSFUL) {
				
				getiAmsWithdrawalDAO().save(amsWithdrawal);
//				// save cash flow
//				AmsCashflow amsCashFlow = new AmsCashflow();
//				String cashFlowId = generateID(Constants.UNIQUE_CONTEXT.CASHFLOW_CONEXT);
//				amsCustomer = new AmsCustomer();
//				amsCustomer.setCustomerId(withdrawalInfo.getCustomerId());
//				amsCashFlow.setAmsCustomer(amsCustomer);
//				amsCashFlow.setCashflowId(cashFlowId);
//				amsCashFlow.setActiveFlg(Constants.ACTIVE_FLG.ACTIVE);
//				AmsCashBalance amsCashBalance = new AmsCashBalance();
//				AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
//				amsCashBalanceId.setCurrencyCode(withdrawalInfo.getCurrencyCode());
//				amsCashBalanceId.setCustomerId(withdrawalInfo.getCustomerId());
//				amsCashBalance.setId(amsCashBalanceId);
//				amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
//				if(amsCashBalance != null) {
//					// save case balance				
//					amsCashBalance.setPreviousBalance(amsCashBalance.getCashBalance());				
//					cashBalance = amsCashBalance.getCashBalance();
//					cashBalance = cashBalance + withdrawalInfo.getWithdrawalAmount();
//					amsCashFlow.setCashBalance(cashBalance);
//					amsCashBalance.setCashBalance(cashBalance);
//					getiAmsCashBalanceDAO().attachDirty(amsCashBalance);
//				} else {
//					log.error("Cannot find cashbalance for customerId: " + withdrawalInfo.getCustomerId());
//					return Constants.WITHDRAW_MT4_STATUS.WITHDRAW_FAIL;
//				}
//				amsCashFlow.setCashflowAmount(withdrawalInfo.getWithdrawalAmount());
//				amsCashFlow.setCashflowType(Constants.CASH_FLOW_TYPE.DEPOSIT_WITHDRAWAL);
//				amsCashFlow.setEventDate(DateUtil.toString(new Date(), Constants.DATE_TIME_FORMAT.DATE_TIME_EVENT));
//				amsCashFlow.setEventDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
//				amsCashFlow.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
//				amsCashFlow.setSourceType(Constants.SOURCE_TYPE.DEPOSIT_ID);
//				amsCashFlow.setSourceId(withdrawalId);
//				amsCashFlow.setValueDate(DateUtil.toString(new Date(), Constants.DATE_TIME_FORMAT.DATE_TIME_EVENT));
//				getiAmsCashFlowDAO().save(amsCashFlow);
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
			} else {
				// Delete if occured errors.
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_FAIL;
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			e.printStackTrace();		
			
			log.info("<><><><><><><><><> End updateBalance() function with [ERROR] <><><><><><><><><>");
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
		
	}*/
	
	/**
	 * 　get cash balance
	 * 
	 * @param customerID
	 * @param currencyCode
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 15, 2012
	 * @MdDate
	 */
	public Double getCashBalance(String customerId, String currencyCode, Integer serviceType) {
		log.info("[start] get cash balance of customerId: " + customerId + ", currencyCode: " + currencyCode + ", serviceType: " + serviceType);
		Double cashBalanceAmount = null;
		try {
			log.info("customer: " + customerId);
			log.info("currency code" + currencyCode);
			AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
			amsCashBalanceId.setCustomerId(customerId);
			amsCashBalanceId.setCurrencyCode(currencyCode);
			amsCashBalanceId.setServiceType(serviceType);
			AmsCashBalance amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
			if(amsCashBalance != null) {
				cashBalanceAmount = amsCashBalance.getCashBalance(); 
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[end] get cash balance of customerId: " + customerId + ", cashBalanceAmount: " + cashBalanceAmount);
		return cashBalanceAmount;
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
	/*public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getiAmsWhitelabelConfigDAO() {
		return iAmsWhitelabelConfigDAO;
	}*/
	/**
	 * @param iAmsWhitelabelConfigDAO the iAmsWhitelabelConfigDAO to set
	 */
	/*public void setiAmsWhitelabelConfigDAO(IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO) {
		this.iAmsWhitelabelConfigDAO = iAmsWhitelabelConfigDAO;
	}*/
	/**
	 * 　
	 * get withdrawal amount
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 9, 2012
	 * @MdDate
	 */
	public BigDecimal getTotalWithdrawalAmount(String customerId, String type) {
		BigDecimal amount = MathUtil.parseBigDecimal(0);
		Double total = getiAmsWithdrawalDAO().getTotalWithdrawalAmount(customerId, type);
		if(total == null) {
			total = new Double(0);
		}
		amount = MathUtil.parseBigDecimal(total);
		return amount;
	}
	/**
	 * 　
	 * get counter withdrawal
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 9, 2012
	 * @MdDate
	 */
	public Integer summaryOfWithDrawalPerday(String customerId, String acceptDate) {
		Double counter = new Double(0);
		try {
			counter = getiAmsWithdrawalDAO().summaryOfWithDrawalPerday(customerId, acceptDate);
			if(counter == null) {
				counter = new Double(0);
			}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		return counter.intValue();
	}
	public AmsWithdrawal getAmsWithdrawal(String withdrawalId){
		return getiAmsWithdrawalDAO().findById(AmsWithdrawal.class, withdrawalId);
	}
	/**
	 *  update status of withdrawal when user clicks cancel hyperlink
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public void updateStatusofWithdrawal(AmsWithdrawal amsWithdrawal, String customerId){
		log.info("[start] update status of cancel withdrawal transaction");
		try {
			getiAmsWithdrawalDAO().attachDirty(amsWithdrawal);
			updateBackNetDepositCc(customerId, amsWithdrawal.getWithdrawalAmount());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		log.info("[end] update status of cancel withdrawal transaction");
	}
	 public void updateBackNetDepositCc(String customerId, Double withdrawalAmount) {
        AmsCustomerSurvey survey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
        if(survey != null){
            survey.setNetDepositCc((survey.getNetDepositCc() == null ? 0 : survey.getNetDepositCc()) + withdrawalAmount);
            amsCustomerSurveyDAO.merge(survey);
        }
    }
	public Double getTotalWithdrawalAmount(String customerId, Integer status, Integer serviceType) {
		Double total = new Double(0);
		total = getiAmsWithdrawalDAO().getTotalWithdrawal(customerId, status, serviceType);
		return total;
	}
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public List<CustomerBankInfo> getListCustomerBankInfo(String customerId){
		List<CustomerBankInfo> listCustomerBankInfo = new ArrayList<CustomerBankInfo>();
		List<AmsCustomerBank> listAmsCustomerBank = new ArrayList<AmsCustomerBank>();
		CustomerBankInfo customerBankInfo = null;
		try {
			listAmsCustomerBank = getiAmsCustomerBankDAO().findByCustomerId(customerId);
			
			//[TRSPT-8605-cuong.bui.manh]Apr 29, 2016M - Start: Get lastest bank and send to client.
			for (AmsCustomerBank amsCustomerBank : listAmsCustomerBank) {
				customerBankInfo = new CustomerBankInfo();				
				BeanUtils.copyProperties(amsCustomerBank, customerBankInfo);
				listCustomerBankInfo.add(customerBankInfo);
			}
			//[TRSPT-8605-cuong.bui.manh]Apr 29, 2016M - End

			log.info("Size listCustomerBankInfo: " + listCustomerBankInfo.size());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return listCustomerBankInfo; 
	}
	
	/**
	 *  CustomerBankInfo
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public CustomerBankInfo  getCustomerBankInfo(String customerBankId){
		CustomerBankInfo customerBankInfo = null;
		try {
			customerBankInfo = new CustomerBankInfo();			
			AmsCustomerBank amsCustomerBank = getiAmsCustomerBankDAO().findById(AmsCustomerBank.class, MathUtil.parseInteger(customerBankId));
			if(amsCustomerBank != null){
				BeanUtils.copyProperties(amsCustomerBank, customerBankInfo);
				/*AmsSysCountry amsSysCountry = getiAmsSysCountryDAO().findById(AmsSysCountry.class, customerBankInfo.getCountryId());
				customerBankInfo.setCountryName(amsSysCountry.getCountryName());*/
			}
		} catch (BeansException e) {
			log.error(e.getMessage(), e);
		}
		
		
		return customerBankInfo;
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public List<AmsCustomerEwallet> getListAmsCustomerEwallet(String customerId, Integer ewalletType) {
		List<AmsCustomerEwallet> listAmsCustomerEwallet = new ArrayList<AmsCustomerEwallet>();
		try {
			listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, ewalletType);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return listAmsCustomerEwallet;
	}
		
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public List<CustomerEwalletInfo> getCustomerEwalletInfo(String customerId, Integer ewalletType){
		log.info("[start] get ewallet infor of customer " + customerId); 
		List<AmsCustomerEwallet> listAmsCustomerEwallet = new ArrayList<AmsCustomerEwallet>();
		List<CustomerEwalletInfo> listCustomerEwalletInfos  = new ArrayList<CustomerEwalletInfo>();
		try {
			listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId, ewalletType);
			if(listAmsCustomerEwallet != null){
				for (AmsCustomerEwallet amsCustomerEwallet : listAmsCustomerEwallet) {
					CustomerEwalletInfo customerEwalletInfo = new CustomerEwalletInfo();
					BeanUtils.copyProperties(customerEwalletInfo, amsCustomerEwallet);
					listCustomerEwalletInfos.add(customerEwalletInfo);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[end] get ewallet infor of customer " + customerId);
		return listCustomerEwalletInfos;
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
	 * @return the iAmsCustomerBankDAO
	 */
	public IAmsCustomerBankDAO<AmsCustomerBank> getiAmsCustomerBankDAO() {
		return iAmsCustomerBankDAO;
	}
	/**
	 * @param iAmsCustomerBankDAO the iAmsCustomerBankDAO to set
	 */
	public void setiAmsCustomerBankDAO(IAmsCustomerBankDAO<AmsCustomerBank> iAmsCustomerBankDAO) {
		this.iAmsCustomerBankDAO = iAmsCustomerBankDAO;
	}
	
	public CustomerInfo getCustomerInfo(String customerId){
		CustomerInfo customerInfo = null;
		try {
			customerInfo = new CustomerInfo();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, customerId);
			BeanUtils.copyProperties(amsCustomer, customerInfo);
		} catch (BeansException e) {
			log.error(e.getMessage(), e);
		}
		
		return customerInfo;
	}
	/**
	 * 　find countryName
	 * 
	 * @param
	 * @return
	 * @auth HuyenMt
	 * @CrDate Aug 25, 2012
	 * @MdDate
	 */
	public String getCountryName(Integer countryId){
		log.info("[Start] get countryName");
		String countryName = null;
		try {
			AmsSysCountry amsSysCountry =  getiAmsSysCountryDAO().findById(AmsSysCountry.class, countryId);
			if(amsSysCountry!=null){
				countryName = amsSysCountry.getCountryName();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] get countryName");
		return countryName;
	}
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Oct 3, 2012
	 * @MdDate
	 */
	public boolean validateAmountWithdrawal(String customerId, String currencyCode, BigDecimal withdrawalAmount, Integer serviceType){		
		log.info("[START] validate withdrawal amount = "+ withdrawalAmount.doubleValue() +"to withdrawal");
		log.info("customerId =" + customerId + "currencyCode = "  + currencyCode + "withdrawl from account (seviceType) = " + serviceType); 
		boolean flag = false;
		try {			
			BigDecimal amountAvailabaleTransferAms = MathUtil.parseBigDecimal(0);
			//BigDecimal amountAvailabaleTransferFx = MathUtil.parseBigDecimal(0);
			//BigDecimal amountAvailabaleTransferBo = MathUtil.parseBigDecimal(0);
			BalanceInfo balanceAmsInfo = null;
			//BalanceInfo balanceBoInfo = null;
			//BalanceInfo balanceFxInfo = null;
			
			//get balance of ams
			balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currencyCode);
			// get balance of FX
			//balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currencyCode);					
			// get balance of BO
			//balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currencyCode);					
			
			if(IConstants.SERVICES_TYPE.AMS.equals(serviceType)){
				if(balanceAmsInfo != null) {
					log.info("Withdrawal Amount available of AMS account = " + balanceAmsInfo.getAmountAvailable());
					//amountAvailabaleTransferAms = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceAmsInfo.getCurrencyCode());
					if(currencyInfo != null) {
						amountAvailabaleTransferAms = MathUtil.rounding(balanceAmsInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
					} else {
						amountAvailabaleTransferAms = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
					}
					
					if(withdrawalAmount.compareTo(amountAvailabaleTransferAms) > 0){
						flag = true;
					}
				}else{
					log.info("validate from SERVER: cannot get balance of ams account");
					flag = true;
				}					
			}/* else if(IConstants.SERVICES_TYPE.BO.equals(serviceType)){
				if(balanceAmsInfo != null) {			
					log.info("Withdrawal Amount available  of BO account = " + balanceBoInfo.getAmountAvailable());
					amountAvailabaleTransferBo = MathUtil.parseBigDecimal(balanceBoInfo.getAmountAvailable());
					if(withdrawalAmount.compareTo(amountAvailabaleTransferBo) > 0){
						log.info("withdrawal amount =" + withdrawalAmount.doubleValue() + " is larger than BO amount available transfer =" + amountAvailabaleTransferBo);
						flag = true;
					}
				}
			} else if(IConstants.SERVICES_TYPE.FX.equals(serviceType)){
				if(balanceAmsInfo != null) {
					log.info("Withdrawal Amount available of FX account = " + balanceFxInfo.getAmountAvailable());
					amountAvailabaleTransferFx = MathUtil.parseBigDecimal(balanceFxInfo.getAmountAvailable());
					if(withdrawalAmount.compareTo(amountAvailabaleTransferFx) > 0){
						log.info("withdrawal amount=" + withdrawalAmount.doubleValue() +" is larger than FX amount available transfer ="+amountAvailabaleTransferFx);
						flag = true;
					}
				}
			}*/
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		log.info("[START] validate withdrawal amount = "+ withdrawalAmount.doubleValue() +"to withdrawal");
		return flag;
	}
	public ExchangerSymbolInfo getExchangerSymbol(String exchangerId){
		AmsExchangerSymbol amsExchangerSymbol = amsExchangerSymbolDAO.getExchangerSymbolByExchangerId(exchangerId).get(0);
		return ExchangerSymbolConverter.toInfo(amsExchangerSymbol);
		
	}
	
	/**
	 * 　get withdrawal fee
	 * 
	 * @param amount
	 * @param currencyCode
	 * @param paymentgwId
	 * @param wlCode
	 * @return
	 * @throws
	 * @author Mai.Thu.Huyen
	 * @CrDate Oct 15, 2012
	 */
	public Double getWithdrawalFee(Double amount, String currencyCode, Integer paymentgwId, String wlCode){
		log.info("[Start] getting withdrawal fee with amount= " + amount + " currencyCode=" + currencyCode + " paymentgwID=" + paymentgwId + " wlCode= " + wlCode);
		Double withdrawalFee = new Double(0); 
		try {
			AmsPaymentgwBalanceId amsPaymentgwBalanceId = new AmsPaymentgwBalanceId();
			amsPaymentgwBalanceId.setPaymentgwId(paymentgwId);
			amsPaymentgwBalanceId.setCurrencyCode(currencyCode);
			amsPaymentgwBalanceId.setWlCode(wlCode);
			
			AmsPaymentgwWlId amsPaymentgwWlId = new AmsPaymentgwWlId();
			amsPaymentgwWlId.setPaymentgwId(paymentgwId);
			amsPaymentgwWlId.setWlCode(wlCode);
			
			AmsWithdrawalFeeId amsWithdrawalFeeId = new AmsWithdrawalFeeId();
			amsWithdrawalFeeId.setPaymentgwId(paymentgwId);
			amsWithdrawalFeeId.setCurrencyCode(currencyCode);
			amsWithdrawalFeeId.setWlCode(wlCode);
			
			AmsPaymentgwBalance amsPaymentgwBalance = getiAmsPaymentgwBalanceDAO().findById(AmsPaymentgwBalance.class, amsPaymentgwBalanceId);
			AmsPaymentgwWl amsPaymentgwWl = getiAmsPaymentgwWlDAO().findById(AmsPaymentgwWl.class, amsPaymentgwWlId);
			AmsWithdrawalFee amsWithdrawalFee = getiAmsWithdrawalFeeDAO().findById(AmsWithdrawalFee.class, amsWithdrawalFeeId);
			Integer feeType = new Integer(0);
			Double feePercent = new Double(0);
			Double feeAmount = new Double(0);
			Double maxFee   = new Double(0);
			Double minFee = new Double(0);
			
			
			if(amsWithdrawalFee != null){
				if(!amsWithdrawalFee.getActiveFlg().equals(IConstants.ACTIVE_FLG.ACTIVE)){
					withdrawalFee = new Double(0);
					return withdrawalFee;
				}else if(amsWithdrawalFee.getActiveFlg().equals(IConstants.ACTIVE_FLG.ACTIVE)){
					feeAmount = amsWithdrawalFee.getFeeAmount();
					feePercent = amsWithdrawalFee.getFeePercent();
					maxFee  = amsWithdrawalFee.getMaxFee();
					minFee = amsWithdrawalFee.getMinFee();
				}
			}
			if(amsPaymentgwWl != null){
				if(!amsPaymentgwWl.getActiveFlg().equals(IConstants.ACTIVE_FLG.ACTIVE)){
					withdrawalFee = new Double(0);
					return withdrawalFee;
					
				}else if(amsPaymentgwWl.getActiveFlg().equals(IConstants.ACTIVE_FLG.ACTIVE)){
					feeType = amsPaymentgwWl.getFeeType();
				}
			}
			if(amsPaymentgwBalance != null){
				if(amsPaymentgwBalance.getWithdrawalFeeFlg().equals(IConstants.WITHDRAWAL_FEE_FLG.FEE)){ //confirmed later
					if(feeType.equals(IConstants.TRANSACTION_FEE_TYPE.FEE_TYPE_PERCENT)){
						withdrawalFee = amount * feePercent / 100;
						if(withdrawalFee.compareTo(minFee) < 0){
							withdrawalFee = minFee;
						}else if(withdrawalFee.compareTo(maxFee) > 0){
							withdrawalFee = maxFee;
						}
						log.info("Fee type: = " + feeType + " PERCENT and withdrawalFee= " + withdrawalFee);
					}else if(feeType.equals(IConstants.TRANSACTION_FEE_TYPE.FEE_TYPE_AMOUNT)){						
						withdrawalFee = feeAmount;
						log.info("Fee type: = " + feeType + " AMOUNT and withdrawalFee= " + withdrawalFee);
					}
				}else if(amsPaymentgwBalance.getWithdrawalFeeFlg().equals(IConstants.WITHDRAWAL_FEE_FLG.NO_FEE)){
					withdrawalFee = new Double(0);
					return withdrawalFee;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[end] getting withdrawal fee with amount= " + amount + " currencyCode=" + currencyCode + " paymentgwID=" + paymentgwId + " wlCode= " + wlCode);
		return withdrawalFee;
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Oct 23, 2012
	 */
	public Integer withdrawalLiberty(LibertyInfo libertyInfo){
		try {			
			BigDecimal amount = libertyInfo.getAmount();
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + libertyInfo.getCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - Start 
			if (validateAmountWithdrawal(libertyInfo.getCustomerId(), libertyInfo.getCurrencyCode(), amount, libertyInfo.getServiceType())) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 3, 2012A - End
			AmsWithdrawal amsWithdrawal = new AmsWithdrawal();		
			AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
			String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
			if(withdrawalId != null){
				amsWithdrawal.setWithdrawalId(withdrawalId);
			}
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(libertyInfo.getCustomerId());
			amsWithdrawal.setAmsCustomer(amsCustomer);
			amsWithdrawal.setServiceType(libertyInfo.getServiceType());
			amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);// 1 withdrawal 2 agency payment, 1 for FE and both for BE
			amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
			amsWithdrawal.setCurrencyCode(libertyInfo.getCurrencyCode());
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
			} 
			amsWithdrawal.setWithdrawalAcceptDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setWithdrawalCompletedDatetime(null);
			amsWithdrawal.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsWithdrawal.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			/*amsWithdrawal.setWithdrawalAmount(amount.doubleValue());*/
			amsWithdrawal.setWithdrawalAmount(libertyInfo.getReceivedAmount());
			amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsWithdrawal.setRemark("");
			amsWithdrawal.setWithdrawalMethod(IConstants.PAYMENT_METHOD.LIBERTY);
//			amsWithdrawal.setRegCustomerId(libertyInfo.getCustomerId());
			amsWithdrawal.setServiceType(libertyInfo.getServiceType());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
			amsWithdrawal.setWithdrawalFee(libertyInfo.getWithdrawalFee());
			//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
			//save ref
			amsWithdrawalRef.setWithdrawalId(withdrawalId);
			amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);			
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			amsWithdrawalRef.setEwalletAccNo(libertyInfo.getAccountNumber() == null ? "" : libertyInfo.getAccountNumber().trim());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.LIBERTY);
			amsWithdrawalRef.setCountryId(null);
			amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
			
			
			amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
			getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);
			getiAmsWithdrawalDAO().save(amsWithdrawal);
			log.info("withdrawal with money= " + amount.doubleValue() + "register successfully!");
			//end save withdrawal ref
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
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
	 * @return the iSysOperationDAO
	 */
	public ISysOperationDAO<SysOperation> getiSysOperationDAO() {
		return iSysOperationDAO;
	}
	/**
	 * @param iSysOperationDAO the iSysOperationDAO to set
	 */
	public void setiSysOperationDAO(ISysOperationDAO<SysOperation> iSysOperationDAO) {
		this.iSysOperationDAO = iSysOperationDAO;
	}
	/**
	 * @return the iSysOperationLogDAO
	 */
	public ISysOperationLogDAO<SysOperationLog> getiSysOperationLogDAO() {
		return iSysOperationLogDAO;
	}
	/**
	 * @param iSysOperationLogDAO the iSysOperationLogDAO to set
	 */
	public void setiSysOperationLogDAO(
			ISysOperationLogDAO<SysOperationLog> iSysOperationLogDAO) {
		this.iSysOperationLogDAO = iSysOperationLogDAO;
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
	 * @return the amsExchangerDAO
	 */
	public IAmsExchangerSymbolDAO<AmsExchangerSymbol> getAmsExchangerSymbolDAO() {
		return amsExchangerSymbolDAO;
	}
	public void setAmsExchangerSymbolDAO(
			IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO) {
		this.amsExchangerSymbolDAO = amsExchangerSymbolDAO;
	}
	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}
	/**
	 * @return the iAmsPaymentgwBalanceDAO
	 */
	public IAmsPaymentgwBalanceDAO<AmsPaymentgwBalance> getiAmsPaymentgwBalanceDAO() {
		return iAmsPaymentgwBalanceDAO;
	}
	/**
	 * @param iAmsPaymentgwBalanceDAO the iAmsPaymentgwBalanceDAO to set
	 */
	public void setiAmsPaymentgwBalanceDAO(
			IAmsPaymentgwBalanceDAO<AmsPaymentgwBalance> iAmsPaymentgwBalanceDAO) {
		this.iAmsPaymentgwBalanceDAO = iAmsPaymentgwBalanceDAO;
	}
	/**
	 * @return the iAmsPaymentgwWlDAO
	 */
	public IAmsPaymentgwWlDAO<AmsPaymentgwWl> getiAmsPaymentgwWlDAO() {
		return iAmsPaymentgwWlDAO;
	}
	/**
	 * @param iAmsPaymentgwWlDAO the iAmsPaymentgwWlDAO to set
	 */
	public void setiAmsPaymentgwWlDAO(
			IAmsPaymentgwWlDAO<AmsPaymentgwWl> iAmsPaymentgwWlDAO) {
		this.iAmsPaymentgwWlDAO = iAmsPaymentgwWlDAO;
	}
	/**
	 * @return the iAmsWithdrawalFeeDAO
	 */
	public IAmsWithdrawalFeeDAO<AmsWithdrawalFee> getiAmsWithdrawalFeeDAO() {
		return iAmsWithdrawalFeeDAO;
	}
	/**
	 * @param iAmsWithdrawalFeeDAO the iAmsWithdrawalFeeDAO to set
	 */
	public void setiAmsWithdrawalFeeDAO(
			IAmsWithdrawalFeeDAO<AmsWithdrawalFee> iAmsWithdrawalFeeDAO) {
		this.iAmsWithdrawalFeeDAO = iAmsWithdrawalFeeDAO;
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Oct 25, 2012
	 */
	@Override
	public CustomerEwalletInfo getLibertyInfo(String customerId, String accountId, String publicKey) {
		CustomerEwalletInfo customerEwalletInfo = null;
		AmsCustomerEwallet amsCustomerEwallet = null;
		List<AmsCustomerEwallet> listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getAmsCustomerEwallet(customerId, IConstants.EWALLET_TYPE.LIBERTY, accountId, null);
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
	
	private void decryptCustomerEwalletInfo(CustomerEwalletInfo customerEwalletInfo, String privateKey, String publicKey) {
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
	}
	
	@Override
	public void checkWithdrawalRules(WithdrawalModel withdrawalModel) {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String currentCustomerId = frontUserOnline.getUserId();
		
		List<Integer> listDeposits = amsDepositDAO.findDepositMethodForCustomer(currentCustomerId, IConstants.DEPOSIT_STATUS.FINISHED);
		
		Map<String, String> mapWithdrawalMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY +  IConstants.SYS_PROPERTY.WITHDRAWAL_METHOD);
		
		Map<String, String> mapPaymentMethods = new LinkedHashMap<String, String>();
		Integer withdrawalCase = findWithdrawalRuleCase(listDeposits);
		withdrawalModel.setWithdrawalRuleCase(withdrawalCase);
		
		if(IConstants.WITHDRAWAL_RULE_CASE.SINGLE_METHOD_OR_WITHOUT_BANKWIRE_CREDITCARD.equals(withdrawalCase)){
			for(Integer depositMethod: listDeposits){
				String depositMethodKey = String.valueOf(depositMethod);
				mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
			}
			withdrawalModel.setMapPaymentMethod(mapPaymentMethods);
		}else if(IConstants.WITHDRAWAL_RULE_CASE.NO_BANKWIRE_BUT_CREDITCARD.equals(withdrawalCase)){
			BigDecimal netCCAmount = getNetDepositOfCreditCard(currentCustomerId);
			
			if(netCCAmount.compareTo(new BigDecimal(0)) > 0){
				//Display transaction 1
				String depositMethodKey = String.valueOf(IConstants.DEPOSIT_METHOD.CREDIT_CARD);
				mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
			}else{
				//Display transaction 2
				for(Integer depositMethod : listDeposits){
					String depositMethodKey = String.valueOf(depositMethod);
					if(depositMethod == null){
						continue;
					}
					if(IConstants.DEPOSIT_METHOD.CREDIT_CARD.equals(depositMethod)){
						continue;
					}
					mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
				}
			}
			withdrawalModel.setMapPaymentMethod(mapPaymentMethods);
		}else if(IConstants.WITHDRAWAL_RULE_CASE.BOTH_BANKWIRE_AND_CREDITCARD.equals(withdrawalCase)){
			BigDecimal netCCAmount = getNetDepositOfCreditCard(currentCustomerId);
			if(netCCAmount.compareTo(new BigDecimal(0)) > 0){
				//Transaction 1
				String depositMethodKey = String.valueOf(IConstants.DEPOSIT_METHOD.CREDIT_CARD);
				mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
			}else {
				//Transaction 2
				String amountStr = withdrawalModel.getAmount();
				BigDecimal restAmount;
				if(StringUtil.isEmpty(amountStr)){
					restAmount = new BigDecimal(0);
				}else{
					restAmount = new BigDecimal(amountStr);
				}
				
				BigDecimal minAmount = new BigDecimal(0);
				Map<String, String> mapMinWithdrawalRule = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MIN_WITHDRAWAL_RULE);
				
				String baseCurrency = frontUserOnline.getCurrencyCode();
				if(IConstants.CURRENCY_CODE.EUR.equalsIgnoreCase(baseCurrency)
						|| IConstants.CURRENCY_CODE.GBP.equalsIgnoreCase(baseCurrency)
						|| IConstants.CURRENCY_CODE.USD.equalsIgnoreCase(baseCurrency)){
						
					minAmount = new BigDecimal(mapMinWithdrawalRule.get(IConstants.MIN_WITHDRAWAL_RULE.MIN_BANKWIRE));
				}else if(IConstants.CURRENCY_CODE.JPY.equalsIgnoreCase(baseCurrency)){
					minAmount = new BigDecimal(mapMinWithdrawalRule.get(IConstants.MIN_WITHDRAWAL_RULE.MIN_JPYBANK));
				}
				
				
				if(restAmount.compareTo(minAmount) > 0){
					String depositMethodKey = String.valueOf(IConstants.DEPOSIT_METHOD.BANK_WIRE);
					mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
				}else{
					for(Integer depositMethod : listDeposits){
						String depositMethodKey = String.valueOf(depositMethod);
						if(depositMethod == null){
							continue;
						}
						if(IConstants.DEPOSIT_METHOD.CREDIT_CARD.equals(depositMethod)){
							continue;
						}else if(IConstants.DEPOSIT_METHOD.BANK_WIRE.equals(depositMethod)){
							continue;
						}
						mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
					}
				}
			}
			withdrawalModel.setMapPaymentMethod(mapPaymentMethods);
		}else if(IConstants.WITHDRAWAL_RULE_CASE.NO_CREDITCARD_BUT_BANKWIRE.equals(withdrawalCase)){
			BigDecimal amount = new BigDecimal(withdrawalModel.getAmount());
			
			BigDecimal minAmount = new BigDecimal(0);
			Map<String, String> mapMinWithdrawalRule = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MIN_WITHDRAWAL_RULE);
			String baseCurrency = frontUserOnline.getCurrencyCode();
			if(IConstants.CURRENCY_CODE.EUR.equalsIgnoreCase(baseCurrency)
					|| IConstants.CURRENCY_CODE.GBP.equalsIgnoreCase(baseCurrency)
					|| IConstants.CURRENCY_CODE.USD.equalsIgnoreCase(baseCurrency)){
					
				minAmount = new BigDecimal(mapMinWithdrawalRule.get(IConstants.MIN_WITHDRAWAL_RULE.MIN_BANKWIRE));
			}else if(IConstants.CURRENCY_CODE.JPY.equalsIgnoreCase(baseCurrency)){
				minAmount = new BigDecimal(mapMinWithdrawalRule.get(IConstants.MIN_WITHDRAWAL_RULE.MIN_JPYBANK));
			}
			
			withdrawalModel.setMapPaymentMethod(mapPaymentMethods);
			if(amount.compareTo(minAmount) > 0){
				String depositMethodKey = String.valueOf(IConstants.DEPOSIT_METHOD.BANK_WIRE);
				mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
			}else{
				for(Integer depositMethod : listDeposits){
					String depositMethodKey = String.valueOf(depositMethod);
					if(depositMethod == null){
						continue;
					}
					if(IConstants.DEPOSIT_METHOD.CREDIT_CARD.equals(depositMethod)){
						continue;
					}else if(IConstants.DEPOSIT_METHOD.BANK_WIRE.equals(depositMethod)){
						continue;
					}
					mapPaymentMethods.put(depositMethodKey, mapWithdrawalMethod.get(depositMethodKey));
				}
			}
		}
	}
	
	private BigDecimal getNetDepositOfCreditCard(String currentCustomerId) {
		AmsCustomerSurvey customerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, currentCustomerId);
		if(customerSurvey == null){
			return new BigDecimal(0);
		}
		return new BigDecimal(customerSurvey.getNetDepositCc());
	}
	private Integer findDepositMethodInList(List<Integer> listDeposits, Integer paymentType) {
		for(Integer depositMethod : listDeposits){
			if(depositMethod == null){
				continue;
			}
			if(depositMethod.equals(paymentType)){
				return depositMethod;
			}
		}
		return null;
	}
	private Integer findWithdrawalRuleCase(List<Integer> listDeposits){
		int depositMethodSize = listDeposits.size();
		Integer bankWireMethod = findDepositMethodInList(listDeposits, IConstants.DEPOSIT_METHOD.BANK_WIRE);
		Integer creditCardMethod = findDepositMethodInList(listDeposits, IConstants.DEPOSIT_METHOD.CREDIT_CARD);
		
		if(depositMethodSize == 1 || (depositMethodSize > 1 && bankWireMethod == null && creditCardMethod == null)){
			return IConstants.WITHDRAWAL_RULE_CASE.SINGLE_METHOD_OR_WITHOUT_BANKWIRE_CREDITCARD;
		}else if(depositMethodSize > 1 && bankWireMethod == null && creditCardMethod != null){
			return IConstants.WITHDRAWAL_RULE_CASE.NO_BANKWIRE_BUT_CREDITCARD;
		}else if(depositMethodSize > 1 && bankWireMethod != null && creditCardMethod != null){
			return IConstants.WITHDRAWAL_RULE_CASE.BOTH_BANKWIRE_AND_CREDITCARD;
		}else if(depositMethodSize > 1 && bankWireMethod != null && creditCardMethod == null){
			return IConstants.WITHDRAWAL_RULE_CASE.NO_CREDITCARD_BUT_BANKWIRE;
		}else{
			return IConstants.WITHDRAWAL_RULE_CASE.NORMAL_CASE;
		}
	}
	@Override
	public void updateAmsCustomerSurvey(String currentCustomerId, BigDecimal withdrawalAmount) {
		AmsCustomerSurvey customerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, currentCustomerId);
		if(customerSurvey != null && customerSurvey.getActiveFlg().equals(IConstants.ACTIVE_FLG.ACTIVE)){
			BigDecimal currentNetDepositCC = new BigDecimal(customerSurvey.getNetDepositCc());
			BigDecimal newNetDepositCC = currentNetDepositCC.subtract(withdrawalAmount);
			customerSurvey.setNetDepositCc(newNetDepositCC.doubleValue());
			amsCustomerSurveyDAO.merge(customerSurvey);
		}
		
	}
	@Override
	public void loadWithdrawalRuleInfo(WithdrawalModel withdrawalModel) {
		String customerId = withdrawalModel.getCurrentCusInfo().getCustomerId();
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode();
		String wlCode = withdrawalModel.getCurrentCusInfo().getWlCode();
		
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		if(withdrawalRuleInfo == null){
			withdrawalRuleInfo = new WithdrawalRuleInfo();
			withdrawalModel.setWithdrawalRuleInfo(withdrawalRuleInfo);
		}
		withdrawalRuleInfo.setServiceType(IConstants.SERVICES_TYPE.AMS);
		
		BigDecimal inputAmount = MathUtil.parseBigDecimal(withdrawalModel.getAmount());
		
		AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
		if(amsCustomerSurvey == null || IConstants.ACTIVE_FLG.INACTIVE.equals(amsCustomerSurvey.getActiveFlg())){
			return;
		}
		
		String depositMethodStr = amsCustomerSurvey.getPaymentMethodDepMark();
		List<Integer> withdrawMethods = null;
		List<Integer> listDepositMethods = new ArrayList<Integer>();
		if(!StringUtil.isEmpty(depositMethodStr)){
			String [] depositMethods = depositMethodStr.split(",");
			if(depositMethods != null){
				for(String depMethod : depositMethods){
					Integer method = MathUtil.parseInteger(depMethod);
					if(method == null) continue;
					listDepositMethods.add(method);
				}
			}
		}
		Map<String, String> mapPaymentMethods = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY +  IConstants.SYS_PROPERTY.WITHDRAWAL_METHOD);
		Map<String, String> mapWithdrawMethods = new HashMap<String, String>();
		
		BigDecimal netDepositCc = MathUtil.parseBigDecimal(amsCustomerSurvey.getNetDepositCc());
		BigDecimal withdrawAmount2 =BigDecimal.ZERO;
		
		if(listDepositMethods == null || listDepositMethods.size() == 0) {
			log.info("customerId = " + customerId + " never deposit before");
			log.info("[start] Check if customer can deposit/withdraw by Exchanger --> customer can choose Exchanger, BW for withdraw if not, customer can choose bankwire only");
			withdrawAmount2 = inputAmount;
			withdrawMethods = new ArrayList<Integer>();
			Map<String, String> mapExchanger = getMapExchanger(wlCode, currencyCode, customerId);
			if(mapExchanger != null && mapExchanger.size() > 0) {
				withdrawMethods.add(IConstants.WITHDRAW_METHOD.EXCHANGER);
				
			}
//			List<AmsExchanger> listAmsExchanger = getAmsExchangerDAO().getAmsExchanger(wlCode, currencyCode, customerId);
//			if(listAmsExchanger != null && listAmsExchanger.size() > 0) {
//				withdrawMethods.add(IConstants.WITHDRAW_METHOD.EXCHANGER);
//			}
			withdrawMethods.add(IConstants.WITHDRAW_METHOD.BANKTRANFER);
			
//			Map<String, String> mapPaymentMethods = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY +  IConstants.SYS_PROPERTY.WITHDRAWAL_METHOD);
//			Map<String, String> mapWithdrawMethods = new HashMap<String, String>();
			
			for(Integer method : withdrawMethods){
				String key = String.valueOf(method);
				String value = mapPaymentMethods.get(key);
				if(value != null){
					mapWithdrawMethods.put(key, value);
				}
			}
			withdrawalRuleInfo.setMapWithdrawMethods(mapWithdrawMethods);
			withdrawalRuleInfo.setShowTransactionWithOtherPaymentMethod(true);
			
			log.info("[end] Check if customer can deposit/withdraw by Exchanger --> customer can choose Exchanger, BW for withdraw if not, customer can choose bankwire only");
		} else { 
			AmsWhitelabelConfig amsWLC = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.CREDIT_CARD_WDR_RULE_FLG, wlCode);
			String configVal = null;
			if(amsWLC!=null){
				configVal = amsWLC.getConfigValue();
			}
			if(configVal != null && Integer.parseInt(configVal) == 1) {
				if(!listDepositMethods.contains(IConstants.WITHDRAW_METHOD.CREDIT) || BigDecimal.ZERO.compareTo(netDepositCc) >= 0){ //Not show credit card option
					withdrawalRuleInfo.setShowTransactionWithCc(false);
					withdrawalRuleInfo.setShowTransactionWithOtherPaymentMethod(true);
					withdrawAmount2 = inputAmount;
				}else{
					BigDecimal creditCardWithdrawFee = MathUtil.parseBigDecimal(getWithdrawalFee(inputAmount.doubleValue(), currencyCode, IConstants.PAYMENT_GATEWAY_ID.CREDIT_CARD, wlCode));
					withdrawalRuleInfo.setWithdrawFeeCc(creditCardWithdrawFee);
					BigDecimal maxWithdrawByCc = netDepositCc.add(creditCardWithdrawFee);
					
					if(inputAmount.compareTo(maxWithdrawByCc) <= 0){ // only show credit card option
						withdrawalRuleInfo.setShowTransactionWithCc(true);
						withdrawalRuleInfo.setReceivedAmountCc(inputAmount.subtract(creditCardWithdrawFee));
						withdrawalRuleInfo.setShowTransactionWithOtherPaymentMethod(false);
					}else{ // show both credit card and other payment method
						withdrawalRuleInfo.setShowTransactionWithCc(true);
						withdrawalRuleInfo.setReceivedAmountCc(netDepositCc);
						withdrawalRuleInfo.setShowTransactionWithOtherPaymentMethod(true);
						withdrawAmount2 = inputAmount.subtract(maxWithdrawByCc);
					}
				}
			}
			if(configVal != null && Integer.parseInt(configVal) == 0) {
				
				withdrawalRuleInfo.setShowTransactionWithCc(false);
				withdrawalRuleInfo.setShowTransactionWithOtherPaymentMethod(true);
				withdrawAmount2 = inputAmount;
			}
			
			if(!StringUtil.isEmpty(depositMethodStr)){
				String [] depositMethods = depositMethodStr.split(",");
				withdrawMethods = amsPaymentGWPrioDAO.findListWithdrawMethod(withdrawAmount2, depositMethods, currencyCode, wlCode);
			}
			
			if(withdrawMethods == null || withdrawMethods.isEmpty()){
				/*withdrawMethods = new ArrayList<Integer>();
				Map<String, String> mapExchanger = getMapExchanger(wlCode, currencyCode, customerId);
				if(mapExchanger != null && mapExchanger.size() > 0) {
					withdrawMethods.add(IConstants.WITHDRAW_METHOD.EXCHANGER);
				}
				withdrawMethods.add(IConstants.WITHDRAW_METHOD.BANKTRANFER);*/
				
				withdrawMethods = getListDefaultWithdrawalMetthod(ITrsConstants.DEFAULT_WITHDRAW_METHODS,wlCode);
			}
			
			withdrawMethods.remove(IConstants.WITHDRAW_METHOD.CREDIT);
			
			
			
			for(Integer method : withdrawMethods){
				String key = String.valueOf(method);
				String value = mapPaymentMethods.get(key);
				if(value != null){
					mapWithdrawMethods.put(key, value);
				}
			}
			withdrawalRuleInfo.setMapWithdrawMethods(mapWithdrawMethods);
			
			
			
		} 
		
		if(withdrawalRuleInfo.isShowTransactionWithOtherPaymentMethod()){
			withdrawalRuleInfo.setWithdrawAmount2(withdrawAmount2);
			BigDecimal otherWithdrawFee = BigDecimal.ZERO;
			Integer paymentMethod = withdrawalRuleInfo.getPaymentMethod();
			
			if(paymentMethod == null){
				paymentMethod = withdrawMethods.get(0);
				withdrawalRuleInfo.setPaymentMethod(paymentMethod);
			}
			String paymentMethodName = mapPaymentMethods.get(String.valueOf(paymentMethod));
			withdrawalRuleInfo.setPaymentMethodName(paymentMethodName);
			
			int gwId = getGwId(withdrawalRuleInfo.getPaymentMethod());
			if(gwId != IConstants.PAYMENT_GATEWAY_ID.NA){
				otherWithdrawFee = MathUtil.parseBigDecimal(getWithdrawalFee(withdrawAmount2.doubleValue(), currencyCode, gwId, wlCode));
			}
	
			withdrawalRuleInfo.setWithdrawFeeOtherMethod(otherWithdrawFee);
			withdrawalRuleInfo.setReceivedAmountOtherMethod(withdrawAmount2.subtract(otherWithdrawFee));
			
			if(IConstants.WITHDRAW_METHOD.LIBERTY.equals(paymentMethod)){
				
				BigDecimal minFeeLiberty = MathUtil.parseBigDecimal("0.01");
				BigDecimal maxFeeLiberty = MathUtil.parseBigDecimal("2.99");
				BigDecimal feeLiberty = MathUtil.parseBigDecimal("0.01");
				
				BigDecimal libertyReferenceFee = minFeeLiberty;
				libertyReferenceFee  = feeLiberty.multiply(withdrawAmount2);
				if(libertyReferenceFee.compareTo(minFeeLiberty) < 0){
					libertyReferenceFee = minFeeLiberty;
				}else if(libertyReferenceFee.compareTo(maxFeeLiberty) > 0){
					libertyReferenceFee = maxFeeLiberty;
				}
				
				withdrawalRuleInfo.setLibertyReferenceFee(libertyReferenceFee);
			}	
		}
		
	}
	
	private Map<String, String> getMapExchanger(String wlCode, String currencyCode, String customerId) {
		Map<String, String> mapData = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.EXCHANGER + wlCode + currencyCode + customerId);
		if(mapData != null) {
			return mapData;
		}
		mapData = new TreeMap<String, String>();
		List<AmsExchanger> resultDao = amsExchangerDAO.getAmsExchanger(wlCode, currencyCode, customerId);
		if (resultDao != null && resultDao.size() > 0) {
			for (AmsExchanger amsExchanger : resultDao) {
				mapData.put(amsExchanger.getExchangerId() , amsExchanger.getExchangerName());
			}
		}
		FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.EXCHANGER + wlCode + currencyCode + customerId, mapData);
		return mapData;
	}
	
	private List<Integer> getListDefaultWithdrawalMetthod(String configKey,String wlCode) {
		List<Integer> listDefaultWithdrawalMetthod = new ArrayList<Integer>();
		
		AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(configKey, wlCode);
		
		String configValues = amsWhitelabelConfig.getConfigValue();
		
		if(!StringUtil.isEmpty(configValues)) {
				String [] configValue = configValues.split(",");
				for(int ih = 0;ih < configValue.length ;ih++) {
					listDefaultWithdrawalMetthod.add(Integer.parseInt(configValue[ih]));
				}
		}
		return listDefaultWithdrawalMetthod;
	}
	
	private int getGwId(Integer paymentMethod) {
		if(paymentMethod == IConstants.PAYMENT_METHOD.BANK_TRANSFER) return IConstants.PAYMENT_GATEWAY_ID.BANK_TRANSFER;
		if(paymentMethod == IConstants.PAYMENT_METHOD.CREDIT_CARD) return IConstants.PAYMENT_GATEWAY_ID.CREDIT_CARD;
		if(paymentMethod == IConstants.PAYMENT_METHOD.LIBERTY) return IConstants.PAYMENT_GATEWAY_ID.LIBERTY;
		if(paymentMethod == IConstants.PAYMENT_METHOD.NETELLER) return IConstants.PAYMENT_GATEWAY_ID.NETELLER;
		if(paymentMethod == IConstants.PAYMENT_METHOD.EXCHANGER) return IConstants.PAYMENT_GATEWAY_ID.EXCHANGER;
		
		return IConstants.PAYMENT_GATEWAY_ID.NA;
	}
	
	@Override
	public void withdrawMoney(WithdrawalModel withdrawalModel) {
		String customerId = withdrawalModel.getCurrentCusInfo().getCustomerId();
		String currencyCode = withdrawalModel.getCurrentCusInfo().getCurrencyCode();
		
		WithdrawalRuleInfo withdrawalRuleInfo = withdrawalModel.getWithdrawalRuleInfo();
		if(withdrawalRuleInfo.isShowTransactionWithCc()){
			Integer withdrawalMethod = IConstants.WITHDRAW_METHOD.CREDIT;
			BigDecimal receivedAmount = withdrawalRuleInfo.getReceivedAmountCc();
			BigDecimal fee = withdrawalRuleInfo.getWithdrawFeeCc();
			insertAmsWithdrawInfo( customerId, currencyCode, withdrawalMethod, receivedAmount, fee, withdrawalModel);
			
			AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
			if(amsCustomerSurvey != null && IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getActiveFlg())){
				BigDecimal netDepositCc = MathUtil.parseBigDecimal(amsCustomerSurvey.getNetDepositCc());
				BigDecimal receivedAmountCc = withdrawalRuleInfo.getReceivedAmountCc();
				netDepositCc = netDepositCc.subtract(receivedAmountCc);
				amsCustomerSurvey.setNetDepositCc(netDepositCc.doubleValue());
				amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerSurveyDAO.merge(amsCustomerSurvey);
			}
		}
		
		if(withdrawalRuleInfo.isShowTransactionWithOtherPaymentMethod()){
			Integer withdrawalMethod = withdrawalRuleInfo.getPaymentMethod();
			BigDecimal receivedAmount = withdrawalRuleInfo.getReceivedAmountOtherMethod();
			BigDecimal fee = withdrawalRuleInfo.getWithdrawFeeOtherMethod();
			String withdrawalId = insertAmsWithdrawInfo( customerId, currencyCode, withdrawalMethod, receivedAmount, fee, withdrawalModel);
			withdrawalModel.setWithdrawalId(withdrawalId);
		}
	}
	
	public String insertAmsWithdrawInfo(String customerId, String currencyCode, Integer withdrawalMethod, BigDecimal receivedAmount, BigDecimal fee, WithdrawalModel withdrawalModel){
		String withdrawalId = generateUniqueId(IConstants.UNIQUE_CONTEXT.WITHDRAWAL_CONTEXT);
		String amount = withdrawalModel.getAmount();
		
		AmsWithdrawal amsWithdrawal = new AmsWithdrawal();
		amsWithdrawal.setWithdrawalId(withdrawalId);
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(customerId);
		amsWithdrawal.setAmsCustomer(amsCustomer);
		amsWithdrawal.setWithdrawalType(IConstants.WITHDRAWAL_TYPE.WITHDRAWAL);
		amsWithdrawal.setWithdrawalMethod(withdrawalMethod);
		amsWithdrawal.setCurrencyCode(currencyCode);
		amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
		amsWithdrawal.setServiceType(IConstants.SERVICES_TYPE.AMS);
		amsWithdrawal.setWithdrawalAmount(receivedAmount.doubleValue());
		amsWithdrawal.setWithdrawalFee(fee.doubleValue());
		amsWithdrawal.setWithdrawalAgencyType(ITrsConstants.WITHDRAWAL_AGENCY_TYPE.MANUAL);
		amsWithdrawal.setRemark("");//hunglv fix bug 15485
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		if(amsAppDate != null) {
			amsWithdrawal.setWithdrawalAcceptDate(amsAppDate.getId().getFrontDate());
		} 
		amsWithdrawal.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		amsWithdrawal.setWithdrawalAcceptDatetime(currentTime);
		amsWithdrawal.setInputDate(currentTime);
		amsWithdrawal.setUpdateDate(currentTime);
		AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
		amsWithdrawalRef.setWithdrawalId(withdrawalId);
		amsWithdrawalRef.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsWithdrawalRef.setAmsWithdrawal(amsWithdrawal);
		putAmsWithdrawalRefInfo(amsWithdrawalRef, withdrawalMethod, withdrawalModel);
		amsWithdrawalRef.setInputDate(currentTime);
		amsWithdrawalRef.setUpdateDate(currentTime);
		
		getiAmsWithdrawalRefDAO().save(amsWithdrawalRef);
		amsWithdrawal.setAmsWithdrawalRef(amsWithdrawalRef);
		getiAmsWithdrawalDAO().save(amsWithdrawal);
		return withdrawalId;
	}
	
	private void putAmsWithdrawalRefInfo(AmsWithdrawalRef amsWithdrawalRef, Integer withdrawalMethod,
										WithdrawalModel withdrawalModel) {
		if(IConstants.WITHDRAW_METHOD.BANKTRANFER.equals(withdrawalMethod)){
			CustomerBankInfo bankwireInfo = withdrawalModel.getCustomerBankInfo();
			AmsCustomerBank amsCustomerBank = null;
			if(bankwireInfo.getCustomerBankId() !=null){
				amsCustomerBank = getiAmsCustomerBankDAO().findById(AmsCustomerBank.class, bankwireInfo.getCustomerBankId());
				if(amsCustomerBank!=null){
					amsWithdrawalRef.setAmsCustomerBank(amsCustomerBank);
					amsWithdrawalRef.setCustomerBankId(bankwireInfo.getCustomerBankId());
				}
			}
			amsWithdrawalRef.setBeneficiaryAccountName(bankwireInfo.getAccountName());
			/*amsWithdrawalRef.setBeneficiaryBankAddress(bankwireInfo.getBankAddress());*/
			amsWithdrawalRef.setBeneficiaryAccountNo(bankwireInfo.getAccountNo());
			amsWithdrawalRef.setBeneficiaryBankName(bankwireInfo.getBankName());
			amsWithdrawalRef.setBeneficiaryBranchName(bankwireInfo.getBranchName());
			amsWithdrawalRef.setBeneficiaryBankNameKana(bankwireInfo.getBankNameKana());
			amsWithdrawalRef.setBeneficiaryBranchNameKana(bankwireInfo.getBranchNameKana());
			amsWithdrawalRef.setBeneficiaryAccountNameKana(bankwireInfo.getAccountNameKana());
			//[TRSPT-8457-TheLN]Apr 18, 2016A - Start 
			amsWithdrawalRef.setBeneficiaryBranchCode(bankwireInfo.getBranchCode());
			amsWithdrawalRef.setBeneficiaryBankCode(bankwireInfo.getBankCode());
			amsWithdrawalRef.setBeneficiaryBankAccClass(bankwireInfo.getBankAccClass());
			amsWithdrawalRef.setBeneficiarySwiftCode(bankwireInfo.getSwiftCode());
			amsWithdrawalRef.setBeneficiaryBankAddress(bankwireInfo.getBankAddress());
			amsWithdrawalRef.setCountryId(bankwireInfo.getCountryId());
			//[TRSPT-8457-TheLN]Apr 18, 2016A - End
			
			/*amsWithdrawalRef.setBeneficiarySwiftCode(bankwireInfo.getSwiftCode());
			amsWithdrawalRef.setCountryId(bankwireInfo.getCountryId());*/
			
		}else if(IConstants.WITHDRAW_METHOD.LIBERTY.equals(withdrawalMethod)){
			LibertyInfo libertyInfo = withdrawalModel.getLibertyInfo();
			amsWithdrawalRef.setEwalletAccNo(libertyInfo.getAccountNumber() == null ? "" : libertyInfo.getAccountNumber().trim());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.LIBERTY);
			amsWithdrawalRef.setCountryId(null);
			
		}else if(IConstants.WITHDRAW_METHOD.EXCHANGER.equals(withdrawalMethod)){
			ExchangerInfo exchangerInfo = withdrawalModel.getExchangerInfo();
			AmsCustomerBank amsCustomerBank = null;
			if(exchangerInfo.getCustomerBankId() !=null){
				amsCustomerBank = getiAmsCustomerBankDAO().findById(AmsCustomerBank.class, exchangerInfo.getCustomerBankId());
				if(amsCustomerBank!=null){
					amsWithdrawalRef.setAmsCustomerBank(amsCustomerBank);
				}
			}
			amsWithdrawalRef.setBeneficiaryAccountName(exchangerInfo.getAccountName());
			amsWithdrawalRef.setBeneficiaryAccountNo(exchangerInfo.getAccountNo());
			amsWithdrawalRef.setBeneficiaryBankName(exchangerInfo.getBankName());
			amsWithdrawalRef.setBeneficiaryBranchName(exchangerInfo.getBranchName());
			amsWithdrawalRef.setCountryId(exchangerInfo.getCountryId());
			amsWithdrawalRef.setExchangerId(exchangerInfo.getExchangerId());
			amsWithdrawalRef.setBeneficiaryBankNameKana(exchangerInfo.getBankNameKana());
			amsWithdrawalRef.setBeneficiaryBranchNameKana(exchangerInfo.getBranchNameKana());
			amsWithdrawalRef.setBeneficiaryAccountNameKana(exchangerInfo.getAccountNameKana());
			ExchangerSymbolInfo exchangerSymbolInfo = getExchangerSymbol(exchangerInfo.getExchangerId());
			if(exchangerInfo != null){
				amsWithdrawalRef.setRate(MathUtil.parseDouble(exchangerSymbolInfo.getBuyRate()));
			}
			
		}else if(IConstants.WITHDRAW_METHOD.NETELLER.equals(withdrawalMethod)){
			NetellerInfo netellerInfo = withdrawalModel.getNetellerInfo();
			amsWithdrawalRef.setEwalletAccNo(netellerInfo.getAccountId());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.NETELLER);
			amsWithdrawalRef.setCountryId(null);
			
		}else if(IConstants.WITHDRAW_METHOD.PAYZA.equals(withdrawalMethod)){
			PayzaInfo payzaInfo = withdrawalModel.getPayzaInfo();
			amsWithdrawalRef.setEwalletEmail(payzaInfo.getEmailAddress());
			amsWithdrawalRef.setEwalletType(IConstants.EWALLET_TYPE.PAYZA);
			amsWithdrawalRef.setCountryId(null);
		}
	}
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HungLV
	 * @CrDate June 15, 2013
	 * @MdDate
	 */
	public boolean checkWithDrawalLimitPerDay(){
		Long withdrawalTime = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		String customerId = frontUserOnline.getUserId();
		String wlCode = frontUserOnline.getWlCode();
		AmsWhitelabelConfig amsWLC = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.WITHDRAWAL_LIMIT_COUNT, wlCode);
		if(amsWLC.getActiveFlg().equals(1)){
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				withdrawalTime = getiAmsWithdrawalDAO().getWithDrawalTimesPerDay(customerId, amsAppDate.getId().getFrontDate());
			}
			if(withdrawalTime != null && withdrawalTime < Long.valueOf(amsWLC.getConfigValue())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check WithDrawal Limit PerDay
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	public boolean checkWithDrawalLimitPerDay(WithdrawalModel withdrawalModel){
		Long withdrawalTime = null;
		String customerId = withdrawalModel.getCurrentCusInfo().getCustomerId();
		String wlCode = withdrawalModel.getCurrentCusInfo().getWlCode();
		
		AmsWhitelabelConfig amsWLC = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.WITHDRAWAL_LIMIT_COUNT, wlCode);
		if(amsWLC.getActiveFlg().equals(1)){
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
			}
			if(amsAppDate != null) {
				withdrawalTime = getiAmsWithdrawalDAO().getWithDrawalTimesPerDay(customerId, amsAppDate.getId().getFrontDate());
			}
			if(withdrawalTime != null && withdrawalTime < Long.valueOf(amsWLC.getConfigValue())){
				return true;
			}
		}
		return false;
	}
	
	public Integer getCountAkazan(String customerId){
		Integer countAkazan = new Integer(0);
		List<AmsCustomerAkazan> lstAkazan = iAmsCustomerAkazanDAO.findByCustomerId(customerId);
		if(lstAkazan.size() > 0){
			countAkazan = new Integer(1);
		}
		return countAkazan;
	}
	
	public Integer getCountAkazanNegativeBlance(String customerId) {
		Integer countAkazan = new Integer(0);
		List<AmsCashBalance> lstCashBalance = iAmsCashBalanceDAO.checkNegativeBalance(customerId);
		if(lstCashBalance.size() > 0){
			countAkazan = new Integer(1);
		}
		return countAkazan;
	}
	
	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getAmsWhitelabelConfigDAO() {
		return amsWhitelabelConfigDAO;
	}
	public void setAmsWhitelabelConfigDAO(
			IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO) {
		this.amsWhitelabelConfigDAO = amsWhitelabelConfigDAO;
	}
	public IAmsCustomerAkazanDAO<AmsCustomerAkazan> getiAmsCustomerAkazanDAO() {
		return iAmsCustomerAkazanDAO;
	}
	public void setiAmsCustomerAkazanDAO(IAmsCustomerAkazanDAO<AmsCustomerAkazan> iAmsCustomerAkazanDAO) {
		this.iAmsCustomerAkazanDAO = iAmsCustomerAkazanDAO;
	}
}
