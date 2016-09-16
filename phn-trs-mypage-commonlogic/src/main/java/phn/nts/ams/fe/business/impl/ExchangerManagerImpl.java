package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import phn.com.nts.ams.web.condition.ExchangerTransactionSearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.*;
import phn.com.nts.db.entity.*;
import phn.com.nts.util.common.*;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CashBalanceInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.converter.ExchangerSymbolConverter;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.nts.components.mail.bean.AmsMailTemplateInfo;

/**
 * @description ExchangerManagerImpl
 * @version NTS1.0
 * @author Nguyen.Manh.Thang
 * @CrDate Sep 18, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class ExchangerManagerImpl implements IExchangerManager {

	private static final Logit log = Logit.getInstance(ExchangerManagerImpl.class);
	private IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO;
	private IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO;
	private IAmsExchangerDAO<AmsExchanger> amsExchangerDAO;
	private IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> amsViewFeSearchHistoryDAO;
	private IAmsViewFeExchangerHistoryDAO<AmsViewFeExchangerHistory> amsFeExchangerHistoryDAO;
	private IAmsDepositDAO<AmsDeposit> depositDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IAmsCashflowDAO<AmsCashflow> cashflowDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> cashBalanceDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> transferMoneyDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> customerServiceDAO;
	private IAmsWithdrawalDAO<AmsWithdrawal> withdrawalDAO;
	private IAmsCustomerDAO<AmsCustomer> customerDAO;
    private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
    private IJmsContextSender jmsContextSender;
	/**
	 * get balance by id's customer
	 * 
	 * @param String
	 *            customerId
	 * @return CashBalanceInfo
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Sep 18, 2012
	 */
	@Override
	public CashBalanceInfo getBalanceByCustomerId(String customerId) {
		List<AmsCashBalance> list = amsCashBalanceDAO.findByProperty("amsCustomer.customerId", customerId);
		if (list != null && list.size() > 0) {
			AmsCashBalance amsCashBalance = list.get(0);
			CashBalanceInfo info = new CashBalanceInfo();
			info.setCashBalance(amsCashBalance.getCashBalance());
			info.setCurrencyCode(amsCashBalance.getId().getCurrencyCode());
			return info;
		}
		return null;
	}

	/**
	 * get exchanger symbol by id's exchanger
	 * 
	 * @param String
	 *            exchangerId
	 * @return ExchangerSymbolInfo
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Sep 18, 2012
	 */
	@Override
	public List<ExchangerSymbolInfo> getExchangerSymbolByExchangerId (String exchangerId) {
		List<AmsExchangerSymbol> list = amsExchangerSymbolDAO.getExchangerSymbolByExchangerId(exchangerId);
		List<ExchangerSymbolInfo> result = new ArrayList<ExchangerSymbolInfo>();
		if (list != null && list.size() > 0) {
			for (AmsExchangerSymbol symbol : list) {
				result.add(ExchangerSymbolConverter.toInfo(symbol));
			}
			return result;
		}
		return null;
	}

	@Override
	public List<ExchangerSymbolInfo> getExchangerSymbolHistoryByExchangerId(String exchangerId, PagingInfo pagingInfo) {
		List<AmsExchangerSymbol> list = amsExchangerSymbolDAO.getExchangerSymbolHistoryByExchangerId(exchangerId, pagingInfo);
		List<ExchangerSymbolInfo> result = new ArrayList<ExchangerSymbolInfo>();
		if (list != null && list.size() > 0) {
			for (AmsExchangerSymbol symbol : list) {
				result.add(ExchangerSymbolConverter.toInfo(symbol));
			}
			return result;
		} 
		return null;
	}
	
	public Double getAmountAvailable(String currencyCode, String customerId){
		// ******** AMS Account ************
		AmsCashBalanceId amsbalanceId = new AmsCashBalanceId(currencyCode, customerId, IConstants.SERVICES_TYPE.AMS);
		AmsCashBalance amsBalance = cashBalanceDAO.findById(AmsCashBalance.class, amsbalanceId);
		// Balance
		if (amsBalance != null) {
			// Amount Request transfer & withdrawal
			double amountRequestTransferWithdrawal = withdrawalDAO.sumRequestTransferWithdrawal(customerId);
			// Amount available for transfer & withdrawal
			Double amountAvailable = amsBalance.getCashBalance() - amountRequestTransferWithdrawal;
			if(amountAvailable <= 0){
				amountAvailable = 0D;
			}
			return amountAvailable;
		}
		return 0D;
	}
	
	
	/**
	 * update current rate
	 * 
	 * @param ExchangerSymbolInfo
	 *            info
	 * @return boolean
	 * @throws
	 * @author Nguyen.Manh.Thang
	 * @CrDate Sep 18, 2012
	 */
	@Override
	public boolean updateCurrentRate(List<ExchangerSymbolInfo> listInfos) {
		try {
			for (ExchangerSymbolInfo info : listInfos) {
				AmsExchangerSymbol amsExchangerSymbol = amsExchangerSymbolDAO.findById(AmsExchangerSymbol.class, info.getExchangerSymbolId());
				updateRateInfo(amsExchangerSymbol,info);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public void updateRateInfo(AmsExchangerSymbol amsExchangerSymbol, ExchangerSymbolInfo info ) {
		if (!amsExchangerSymbol.getBuyRate().equals(MathUtil.parseDouble(info.getBuyRate())) || 
			!amsExchangerSymbol.getSellRate().equals(MathUtil.parseDouble(info.getSellRate()))) {
			try {
							
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				//update exchange current rate is inactive
				amsExchangerSymbol.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
				amsExchangerSymbol.setUpdateDate(currentTime);
				amsExchangerSymbolDAO.merge(amsExchangerSymbol);
				//insert new record
				AmsExchangerSymbol newExchangerSymbol = new AmsExchangerSymbol();
				//active flag
				newExchangerSymbol.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				//input date
				newExchangerSymbol.setInputDate(currentTime);
				//update date
//				newExchangerSymbol.setUpdateDate(getBusinessDate().getFrontDatetime());
				newExchangerSymbol.setUpdateDate(currentTime);
				//buy rate
				newExchangerSymbol.setBuyRate(Double.parseDouble(info.getBuyRate()));
				//sell rate
				newExchangerSymbol.setSellRate(Double.parseDouble(info.getSellRate()));
				//fxsymbol
				FxSymbol fxSymbol = amsExchangerSymbol.getFxSymbol();
				newExchangerSymbol.setFxSymbol(fxSymbol);
				//exchanger
				AmsExchanger amsExchanger = amsExchangerSymbol.getAmsExchanger();				
				newExchangerSymbol.setAmsExchanger(amsExchanger);
				amsExchangerSymbolDAO.save(newExchangerSymbol);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	public List<ExchangerTransactionSearchCondition> getExchangerHistory(ExchangerTransactionSearchCondition condition, PagingInfo pagingInfo) {
		List<AmsViewFeExchangerHistory> amsViewFeSearchHistories = amsFeExchangerHistoryDAO.getExchangerHistory(condition, pagingInfo);
		if (amsViewFeSearchHistories != null && amsViewFeSearchHistories.size() > 0) {
			List<ExchangerTransactionSearchCondition> list = new ArrayList<ExchangerTransactionSearchCondition>();
			for (AmsViewFeExchangerHistory exchangerHistory : amsViewFeSearchHistories) {
				ExchangerTransactionSearchCondition info = new ExchangerTransactionSearchCondition();
				info.setTransactionId(exchangerHistory.getTransactionId());
				info.setTypeStr(exchangerHistory.getType());
				info.setCustomerId(exchangerHistory.getCustomerId());
				info.setCustomerName(exchangerHistory.getCustomerName());
				if(!StringUtil.isEmpty(exchangerHistory.getCurrencyCode())){
					String pattern = MasterDataManagerImpl.getInstance().getPattern(exchangerHistory.getCurrencyCode());
					info.setAmount(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(exchangerHistory.getCurrencyCode() , exchangerHistory.getAmount().doubleValue()), pattern));
				}
				
				info.setAmountD(exchangerHistory.getAmount().toString());
				info.setCurrencyCode(exchangerHistory.getCurrencyCode());
				info.setRate(exchangerHistory.getRate());
				info.setStatus(exchangerHistory.getStatus());
				info.setRequestTime(DateUtil.toString(exchangerHistory.getRequestTime(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
				info.setRemark(exchangerHistory.getRemark());
				info.setBeneficiaryAccountName(exchangerHistory.getBeneficiaryAccountName());
				info.setBeneficiaryAccountNo(exchangerHistory.getBeneficiaryAccountNo());
				info.setBeneficiaryBankAddress(exchangerHistory.getBeneficiaryBankAddress());
				info.setBeneficiaryBankName(exchangerHistory.getBeneficiaryBankName());
				info.setBeneficiaryBranchName(exchangerHistory.getBeneficiaryBranchName());
				info.setBeneficiarySwiftCode(exchangerHistory.getBeneficiarySwiftCode());
				BigDecimal amount = exchangerHistory.getAmount() == null ? MathUtil.parseBigDecimal(0) : exchangerHistory.getAmount();
				BigDecimal rate = exchangerHistory.getRate() == null ? MathUtil.parseBigDecimal(0) : exchangerHistory.getRate();
				BigDecimal total = amount.multiply(rate);
				String pattern = MasterDataManagerImpl.getInstance().getPattern(exchangerHistory.getCurrencyCode());
				info.setTotal(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(exchangerHistory.getCurrencyCode() , total.doubleValue()), pattern));
				list.add(info);
			}
			return list;
		}
		return null;
	}

	/**
	 * Update deposit status
	 * 
	 * @param AmsDeposit
	 *            deposit
	 * @param DepositHistoryInfo
	 *            info
	 * @paramString frontDate
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Aug 14, 2012
	 */
	private void updateDepposit(AmsDeposit deposit, ExchangerTransactionSearchCondition info) {
		try {
			Timestamp current = new Timestamp(System.currentTimeMillis());
			deposit.setStatus(info.getStatus());
			deposit.setChangeStatusNote(info.getRemark());
			deposit.setUpdateDate(current);
			if (info.getStatus().intValue() == IConstants.DEPOSIT_STATUS.FINISHED.intValue()) {
				deposit.setDepositCompletedDatetime(new Timestamp(new Date().getTime()));
				deposit.setDepositCompletedDate(getBusinessDate().getId().getFrontDate());
			}

			log.info("Start Updating " + deposit.getDepositId() + "Status: " + deposit.getStatus());
			System.out.println("Start Updating " + deposit.getDepositId() + "Status: " + deposit.getStatus());
			depositDAO.merge(deposit);

			System.out.println("Finish Updating " + deposit.getDepositId());
			log.info("Finish Updating " + deposit.getDepositId());
		} catch (Exception e) {
			log.error(e.getMessage());
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
	 * insert data to ams money transfer
	 * 
	 * @param Integer
	 *            serviceType
	 * @param AmsCustomer
	 *            amsCustomer
	 * @param Double
	 *            amount
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Aug 14, 2012
	 */
	/**
	 * @param sourceType
	 */
	private AmsTransferMoney insertMoneyTransfer(AmsCustomer amsCustomer, Double amount, Double rate, String currencyCode, String accountFrom, String accountTo, String sourceId, Integer sourceType) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		AmsTransferMoney transferMoney = new AmsTransferMoney();
		String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
		transferMoney.setTransferMoneyId(transferMoneyId);
		transferMoney.setTransferFrom(IConstants.SERVICES_TYPE.AMS);
		transferMoney.setTransferTo(IConstants.SERVICES_TYPE.AMS);
		transferMoney.setAmsCustomer(amsCustomer);
		transferMoney.setTransferMoney(amount);
		transferMoney.setCurrencyCode(currencyCode);
		transferMoney.setActiveFlg(1);
		transferMoney.setAccountFrom(accountFrom);
		transferMoney.setAccountTo(accountTo);
		transferMoney.setSourceId(sourceId);
		transferMoney.setSourceType(sourceType);
		transferMoney.setInputDate(currentTime);
		transferMoney.setUpdateDate(currentTime);
		transferMoney.setStatus(IConstants.TRANSFER_MONEY_STATUS.FINISH);
		transferMoney.setTranferAcceptDate(getBusinessDate().getId().getFrontDate());
		transferMoney.setTranferAcceptDateTime(currentTime);
		transferMoney.setTranferCompleteDateTime(new Timestamp(new Date().getTime()));
		transferMoney.setTranferCompleteDate(getBusinessDate().getId().getFrontDate());
		transferMoneyDAO.save(transferMoney);
		return transferMoney;
	}

	public SysAppDate getBusinessDate() {
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if (listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		return amsAppDate;
	}

	/**
	 * insert data to ams cashflow
	 * 
	 * @param amsCustomer
	 * @param cashBalance
	 * @param frontDate
	 * @param balance
	 * @param amount
	 * @param deposit
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Aug 14, 2012
	 */
	private void insertCashflow(AmsCustomer amsCustomer, AmsCashBalance cashBalance, Double balance, Double amount, double rate, String sourceId, Integer serviceType, String currencyCode) {
		AmsCashflow cashFlow = new AmsCashflow();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String cashflowId = generateUniqueId(IConstants.UNIQUE_CONTEXT.CASHFLOW_CONTEXT);
		cashFlow.setCashflowId(cashflowId);
		cashFlow.setAmsCustomer(amsCustomer);
		//cashFlow.setAmsCashBalance(cashBalance);
		cashFlow.setServiceType(serviceType);
		cashFlow.setCurrencyCode(currencyCode);
		cashFlow.setEventDatetime(currentTime);
		cashFlow.setEventDate(getBusinessDate().getId().getFrontDate());
		cashFlow.setValueDate(null);
		cashFlow.setCashflowType(IConstants.CASHFLOW_TYPE.TRANSFER);
		cashFlow.setCashflowAmount(amount);
		cashFlow.setCashBalance(balance);
		cashFlow.setRate(1D);
		cashFlow.setSourceType(IConstants.SOURCE_TYPE.TRANFER_MONEY);
		cashFlow.setSourceId(sourceId);
		cashFlow.setActiveFlg(1);
		cashFlow.setInputDate(currentTime);
		cashFlow.setUpdateDate(currentTime);
		cashflowDAO.save(cashFlow);
	}

	/**
	 * update info withdraw table
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Aug 24, 2012
	 */
	private void updateWithdraw(AmsWithdrawal withdraw, ExchangerTransactionSearchCondition info) {
		Timestamp current = new Timestamp(System.currentTimeMillis());
		withdraw.setStatus(info.getStatus());
		withdraw.setChangeStatusNote(info.getRemark());
//		withdraw.setWithdrawalFee(new Double(0));
		withdraw.setUpdateDate(current);
		if (info.getStatus().intValue() == IConstants.DEPOSIT_STATUS.FINISHED.intValue()) {
			withdraw.setWithdrawalCompletedDatetime(new Timestamp(new Date().getTime()));
			withdraw.setWithdrawalCompletedDate(getBusinessDate().getId().getFrontDate());
		}
		withdrawalDAO.merge(withdraw);
	}
	
	
	/**
	 * Update exchanger status
	 * 
	 * @param list ExchangerTransactionSearchCondition
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 20, 2012
	 */
	public int updateExchanger(List<ExchangerTransactionSearchCondition> list){
		for (ExchangerTransactionSearchCondition exchangerTransactionSearchCondition : list) {
			try{
				if(!updateOneRecord(exchangerTransactionSearchCondition)){
					return 0;
				}
			}catch (Exception e){
				return 0;
			}
		}
		return 1;
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
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

	/**
	 * Do bussiness for each record of exchanger　
	 * 
	 * @param info
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 19, 2012
	 */
	public boolean updateOneRecord(ExchangerTransactionSearchCondition info) {
		try {
		String exchangerCustomerId = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {		
				 exchangerCustomerId = frontUserOnline.getUserId();
			}
		}
		int nextStatus = info.getStatus();
	
		if (IConstants.EXCHANGER_TYPE.DEPOSIT.equals(info.getTypeStr())) {
			AmsDeposit deposit = getDepositDAO().findById(AmsDeposit.class, info.getTransactionId());
			AmsCustomer amsCustomer = deposit.getAmsCustomer();
			double amount = deposit.getDepositAmount();
			double rate = deposit.getRate();
			String currencyCode = deposit.getCurrencyCode();

			// If status is changed into [IN_PROGRESS]
			if (nextStatus == IConstants.DEPOSIT_STATUS.IN_PROGRESS || nextStatus == IConstants.DEPOSIT_STATUS.REQUESTING) {
				updateDepposit(deposit, info);
				
				// If status is changed into [Inprogress]
				if (nextStatus == IConstants.DEPOSIT_STATUS.IN_PROGRESS) {
					// send mail to customer
					sendMailDepositExchangerInprogress(deposit);
				}
			} else if (nextStatus == IConstants.DEPOSIT_STATUS.CANCEL
					&& (deposit.getStatus().intValue() == IConstants.DEPOSIT_STATUS.REQUESTING.intValue() || deposit.getStatus().intValue() == IConstants.DEPOSIT_STATUS.IN_PROGRESS)) {
				// If status is changed into [Cancel]
				updateDepposit(deposit, info);
				// sendmailCancel(info, deposit);
				sendmailDepositCancel(deposit);
				// If status is changed into [Finished]
			} else if (nextStatus == IConstants.DEPOSIT_STATUS.FINISHED.intValue() && deposit.getStatus().intValue() == IConstants.DEPOSIT_STATUS.IN_PROGRESS.intValue()) {
				// Step1: Insert into table AMS_TRANFER_MONEY
				AmsTransferMoney transferMoney = insertMoneyTransfer(amsCustomer, amount, rate, currencyCode, exchangerCustomerId, info.getCustomerId(), deposit.getDepositId(), IConstants.SOURCE_TYPE.DEPOSIT_ID);
				// Step2: Insert 2 record in to table AMS_CASHFLOW
				AmsCashBalanceId balanceIdIdExchanger = new AmsCashBalanceId(currencyCode, exchangerCustomerId, IConstants.SERVICES_TYPE.AMS);
				AmsCashBalance amsCashBalanceExchanger = cashBalanceDAO.findById(AmsCashBalance.class, balanceIdIdExchanger);
				insertCashflow(amsCustomer, amsCashBalanceExchanger, amsCashBalanceExchanger.getCashBalance() - amount, amount, deposit.getRate(), transferMoney.getTransferMoneyId(), IConstants.SERVICES_TYPE.AMS, currencyCode);

				AmsCashBalanceId balanceIdIdCustomer = new AmsCashBalanceId(currencyCode, amsCustomer.getCustomerId(), IConstants.SERVICES_TYPE.AMS);
				AmsCashBalance amsCashBalanceCustomer = cashBalanceDAO.findById(AmsCashBalance.class, balanceIdIdCustomer);
				insertCashflow(amsCustomer, amsCashBalanceCustomer, amsCashBalanceCustomer.getCashBalance() + amount, amount, deposit.getRate(), transferMoney.getTransferMoneyId(), IConstants.SERVICES_TYPE.AMS, currencyCode);

				// Step3: Update CASH_BALANCE in table AMS_CASH_BALANCE
				// --------------------Deduct AMS balance of
				// Exchanger----------------------------
				amsCashBalanceExchanger.setCashBalance(amsCashBalanceExchanger.getCashBalance() - amount);
				amsCashBalanceExchanger.setNetDepositAmount(amsCashBalanceExchanger.getNetDepositAmount() - amount);				
				amsCashBalanceDAO.merge(amsCashBalanceExchanger);
				// --------------------Plus AMS balance of
				// Customer----------------------------
				amsCashBalanceCustomer.setCashBalance(amsCashBalanceCustomer.getCashBalance() + amount);
				amsCashBalanceCustomer.setNetDepositAmount(amsCashBalanceCustomer.getNetDepositAmount() + amount);
				amsCashBalanceDAO.merge(amsCashBalanceCustomer);
				// Step4: Update table AMS_DEPOSIT
				updateDepposit(deposit, info);
				// Step5: Send mail to customer
                updateRemarkCustomerSurvey(info.getCustomerId(), IConstants.PAYMENT_METHOD.EXCHANGER);
				sendmailDepositSuccess(deposit);
			}
		} else if (IConstants.EXCHANGER_TYPE.WITHDRAWAL.equals(info.getTypeStr())) {
			AmsWithdrawal withdrawal = withdrawalDAO.findById(AmsWithdrawal.class, info.getTransactionId());
			AmsCustomer amsCustomer = withdrawal.getAmsCustomer();
			double amount = withdrawal.getWithdrawalAmount();
			double fee = withdrawal.getWithdrawalFee();
			//
			String currencyCode = withdrawal.getCurrencyCode();
			// If status is changed into [IN_PROGRESS]
			if (nextStatus == IConstants.DEPOSIT_STATUS.IN_PROGRESS || nextStatus == IConstants.DEPOSIT_STATUS.REQUESTING) {
				updateWithdraw(withdrawal, info);
				
				// If status is changed into [Inprogress]
				if (nextStatus == IConstants.DEPOSIT_STATUS.IN_PROGRESS) {
					sendMailWithdrawExchangerInprogress(withdrawal);
				}
			} else if ((nextStatus == IConstants.DEPOSIT_STATUS.CANCEL)
					&& (withdrawal.getStatus().intValue() == IConstants.DEPOSIT_STATUS.REQUESTING.intValue() || withdrawal.getStatus().intValue() == IConstants.DEPOSIT_STATUS.IN_PROGRESS)) {
				// If status is changed into [Cancel]
				updateWithdraw(withdrawal, info);
				// sendmailCancel(info, deposit);
				sendmailWithdrawalCancel(withdrawal);
			}
			// If status is changed into [Finished]
		 else if (nextStatus == IConstants.DEPOSIT_STATUS.FINISHED.intValue() && withdrawal.getStatus().intValue() == IConstants.DEPOSIT_STATUS.IN_PROGRESS.intValue()) {
			// Step1: Insert into table AMS_TRANFER_MONEY
			 AmsTransferMoney transferMoney = insertMoneyTransfer(amsCustomer, amount, 0D, currencyCode, info.getCustomerId(), exchangerCustomerId, withdrawal.getWithdrawalId(), IConstants.SOURCE_TYPE.WITHDRAWAL_ID);
				// Step2: Insert 2 record in to table AMS_CASHFLOW
				AmsCashBalanceId balanceIdIdExchanger = new AmsCashBalanceId(currencyCode, exchangerCustomerId, IConstants.SERVICES_TYPE.AMS);
				AmsCashBalance amsCashBalanceExchanger = cashBalanceDAO.findById(AmsCashBalance.class, balanceIdIdExchanger);
				insertCashflow(amsCustomer, amsCashBalanceExchanger, amsCashBalanceExchanger.getCashBalance() + amount, amount , 0D, transferMoney.getTransferMoneyId(), IConstants.SERVICES_TYPE.AMS, currencyCode);

				AmsCashBalanceId balanceIdIdCustomer = new AmsCashBalanceId(currencyCode, amsCustomer.getCustomerId(), IConstants.SERVICES_TYPE.AMS);
				AmsCashBalance amsCashBalanceCustomer = cashBalanceDAO.findById(AmsCashBalance.class, balanceIdIdCustomer);
				insertCashflow(amsCustomer, amsCashBalanceCustomer, amsCashBalanceCustomer.getCashBalance() - amount, amount , 0D, transferMoney.getTransferMoneyId(), IConstants.SERVICES_TYPE.AMS, currencyCode);

				// Step3: Update CASH_BALANCE in table AMS_CASH_BALANCE
				// --------------------Deduct AMS balance of
				// Exchanger----------------------------
				amsCashBalanceExchanger.setCashBalance(amsCashBalanceExchanger.getCashBalance() + amount);
				amsCashBalanceDAO.merge(amsCashBalanceExchanger);
				// --------------------Plus AMS balance of
				// Customer----------------------------
				log.info("deduct fee of customer when withdrawal with fee = " + fee);
				amount = amount + fee;
				log.info("deduct balance of customerId = " + amsCustomer.getCustomerId() + ", amount = " + amount);
				amsCashBalanceCustomer.setCashBalance(amsCashBalanceCustomer.getCashBalance() - amount);
				amsCashBalanceDAO.merge(amsCashBalanceCustomer);
				// Step4: Update table AMS_DEPOSIT
				updateWithdraw(withdrawal, info);
				 // Step5: Send mail to customer
				sendmailWithdrawalSuccess(withdrawal);
		 }
		}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * send mail inform withdraw is in progress
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 27, 2012
	 */
	private void sendMailWithdrawExchangerInprogress(AmsWithdrawal withdrawal) {
		AmsCustomer amsCustomer = withdrawal.getAmsCustomer();
		String language = StringUtil.isEmpty(amsCustomer.getDisplayLanguage()) ? IConstants.Language.ENGLISH : amsCustomer.getDisplayLanguage();
		
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_WITHDRAWAL_EX_INPROGRESS + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_WITHDRAWAL_EX_INPROGRESS).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setWithdrawalAmount(String.valueOf(withdrawal.getWithdrawalAmount()));
		SysCurrency sysCurrency = amsCustomer.getSysCurrency();
		amsMailTemplateInfo.setWithdrawalCurrency(sysCurrency == null ? "" : sysCurrency.getCurrencyCode());
		amsMailTemplateInfo.setWithdrawalId(withdrawal.getWithdrawalId());
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setWithdrawalMethod(SystemPropertyConfig.getInstance().getText("withdrawal_transfer_method.exchanger"));
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		amsMailTemplateInfo.setWithdrawalDate(DateUtil.toString(withdrawal.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																												
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
			log.info("end send mail withdrawal exchanger inprogress");
	}

	/**
	 * send mail inform deposit is in progress
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 27, 2012
	 */
	private void sendMailDepositExchangerInprogress(AmsDeposit deposit) {
		AmsCustomer amsCustomer = deposit.getAmsCustomer();
		String language = StringUtil.isEmpty(amsCustomer.getDisplayLanguage()) ? IConstants.Language.ENGLISH : amsCustomer.getDisplayLanguage();
		
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_EX_INPROGRESS + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_EX_INPROGRESS).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(String.valueOf(deposit.getDepositAmount()));
		SysCurrency sysCurrency = amsCustomer.getSysCurrency();
		amsMailTemplateInfo.setDepositCurrency(sysCurrency == null ? "" : sysCurrency.getCurrencyCode());
		amsMailTemplateInfo.setDepositId(deposit.getDepositId());
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositMethod(SystemPropertyConfig.getInstance().getText("deposit_transfer_method_exchanger"));
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(deposit.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		
//			Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
//			amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(IConstants.PAYMENT_METHOD.PAYZA)));
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																												
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("end send mail inform deposit exchanger is inprogress");
	}

	/**
	   * 　send mail to customer when deposit update is failed
	 * 
	 * @param  AmsDeposit deposit
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 21, 2012
	 */
	private void sendmailDepositCancel(AmsDeposit deposit) {
		AmsCustomer amsCustomer = deposit.getAmsCustomer();
		String lang = amsCustomer.getDisplayLanguage();
		if(StringUtil.isEmpty(lang)){
			lang = IConstants.Language.ENGLISH;
		}

		AmsMailTemplateInfo mail = new AmsMailTemplateInfo();
		mail.setFullName(amsCustomer.getFullName());
		mail.setDepositId(deposit.getDepositId());
		mail.setDepositCancelReason(deposit.getChangeStatusNote());
		String currency = deposit.getCurrencyCode();
		mail.setDepositAmount(FormatHelper.formatDecimal(MathUtil.parseBigDecimal(deposit.getDepositAmount()), IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL) );
		mail.setLoginId(amsCustomer.getLoginId());
		mail.setDepositCancelReason(deposit.getChangeStatusNote());
		mail.setDepositCurrency(currency);
		mail.setDepositDate(DateUtil.toString(deposit.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		mail.setDepositMethod(SystemPropertyConfig.getInstance().getText("nts.ams.fe.label.exchanger"));
		// send to
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		mail.setTo(to);
		// common info
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_CANCEL + "_" + lang);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_CANCEL).append("_").append(lang).toString();
		mail.setMailCode(mailCode);
		mail.setSubject(mailCode);
		mail.setWlCode(deposit.getAmsCustomer().getWlCode());
		//mail.setSubject(SysPropertyConfig.getInstance().getLanguage());
		log.info("Sending mail to: " +amsCustomer.getMailMain());
//		phn.nts.ams.fe.common.JMSSendClient.getInstance().sendMail(mail);
		jmsContextSender.sendMail(mail, false);
		log.info("Finish send mail to: " + amsCustomer.getMailMain());
	}

	/**
	   * 　send mail to customer when deposit update is success
	 * 
	 * @param DepositHistoryInfo info, AmsDeposit deposit
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 21, 2012
	 */
	private void sendmailDepositSuccess(AmsDeposit deposit) {
		AmsCustomer amsCustomer = deposit.getAmsCustomer();
		String lang = amsCustomer.getDisplayLanguage();
		if(StringUtil.isEmpty(lang)){
			lang = IConstants.Language.ENGLISH;
		}
		AmsMailTemplateInfo mail = new AmsMailTemplateInfo();
		mail.setDepositAmount(FormatHelper.formatDecimal(MathUtil.parseBigDecimal(deposit.getDepositAmount()), IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL));
		mail.setDepositCurrency(deposit.getCurrencyCode());
		mail.setDepositDate(DateUtil.toString(deposit.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		mail.setFullName(amsCustomer.getFullName());
		mail.setDepositId(deposit.getDepositId());
		mail.setLoginId(amsCustomer.getLoginId());
		mail.setDepositMethod(SystemPropertyConfig.getInstance().getText("nts.ams.fe.label.exchanger"));
		log.info("Sending mail to: " +amsCustomer.getMailMain());
		// send to
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		mail.setTo(to);

		// common info
//		String key = IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS + "_" + lang;
//		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS).append("_").append(amsCustomer.getDisplayLanguage()).toString();
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEP_SUCCESS).append("_").append(lang).toString();
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(key);
//		if (mailTemplateInfo == null) {
//			log.error("sendmailDepositSuccess not exist MailTemplateInfo key = " + key);
//			return;
//		}

		mail.setMailCode(mailCode);
		mail.setSubject(mailCode);
		mail.setWlCode(deposit.getAmsCustomer().getWlCode());
		// send to queue
//		phn.nts.ams.fe.common.JMSSendClient.getInstance().sendMail(mail);
		jmsContextSender.sendMail(mail, false);
		log.info("Finish send mail to: " + amsCustomer.getMailMain());
	}
	
	/**
	 * send mail to customer if status withdraw failure　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 21, 2012
	 */
	private void sendmailWithdrawalCancel(AmsWithdrawal withdraw) {
		try {
			AmsCustomer amsCustomer = withdraw.getAmsCustomer();
			String lang = amsCustomer.getDisplayLanguage();
			if(StringUtil.isEmpty(lang)){
				lang = IConstants.Language.ENGLISH;
			}
			AmsMailTemplateInfo mail = new AmsMailTemplateInfo();
			mail.setWithdrawalAmount(String.valueOf(withdraw.getWithdrawalAmount()));
			mail.setWithdrawalCurrency(withdraw.getCurrencyCode());
			mail.setWithdrawalDate(DateUtil.toString(withdraw.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
			mail.setFullName(amsCustomer.getFullName());
			mail.setWithdrawalId(withdraw.getWithdrawalId());
			mail.setWithdrawalMethod(SystemPropertyConfig.getInstance().getText("nts.ams.fe.label.exchanger"));
			mail.setWithdrawalCancelReason(withdraw.getChangeStatusNote() == null ? "" : withdraw.getChangeStatusNote());
			mail.setLoginId(withdraw.getAmsCustomer().getLoginId());
			// send to
			HashMap<String, String> to = new HashMap<String, String>();
			to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
			mail.setTo(to);

//			MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_WDR_CANCEL+ "_" + lang);
			String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_WDR_CANCEL).append("_").append(lang).toString();
			// common info
			mail.setMailCode(mailCode);
			mail.setSubject(mailCode);
			mail.setWlCode(withdraw.getAmsCustomer().getWlCode());

			// send to queue
//			JMSSendClient.getInstance().sendMail(mail);
			jmsContextSender.sendMail(mail, false);
		} catch (Exception e) {
			log.error("Send mail cancel error " + e);
		}
	}

	/**
	 * send mail to customer if status withdraw success　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Tran.Duc.Nam
	 * @CrDate Sep 21, 2012
	 */
	private void sendmailWithdrawalSuccess(AmsWithdrawal withdraw) {
		try {
			AmsCustomer amsCustomer = withdraw.getAmsCustomer();
			String lang = amsCustomer.getDisplayLanguage();
			if(StringUtil.isEmpty(lang)){
				lang = IConstants.Language.ENGLISH;
			}
			
			AmsMailTemplateInfo mail = new AmsMailTemplateInfo();
			mail.setWithdrawalAmount(String.valueOf(withdraw.getWithdrawalAmount()));
			mail.setWithdrawalCurrency(withdraw.getCurrencyCode());
			mail.setWithdrawalDate(DateUtil.toString(withdraw.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
			mail.setFullName(amsCustomer.getFullName());
			mail.setWithdrawalId(withdraw.getWithdrawalId());
			mail.setWithdrawalMethod(SystemPropertyConfig.getInstance().getText("nts.ams.fe.label.exchanger"));
			mail.setFullName(amsCustomer.getFullName());
			mail.setLoginId(withdraw.getAmsCustomer().getLoginId());
			
			// send to
			HashMap<String, String> to = new HashMap<String, String>();
			to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
			mail.setTo(to);
			
//			String key = new StringBuffer(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE).append(IConstants.MAIL_TEMPLATE.AMS_WDR_SUCCESS).append("_").append(lang).toString();
//			MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(key);
			String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_WDR_SUCCESS).append("_").append(lang).toString();
//			if (mailTemplateInfo == null) {
//				log.error("Not exist mail template: key = " + key);
//			}
			// common info
			mail.setMailCode(mailCode);
			mail.setSubject(mailCode);
			mail.setWlCode(withdraw.getAmsCustomer().getWlCode());
			
			// send to queue
//			JMSSendClient.getInstance().sendMail(mail);
			jmsContextSender.sendMail(mail, false);
		} catch (Exception e) {
			log.error("Send mail error " + e);
		}
	}
	
	
	

	/*********************** setter - getter ***************************/
	public IAmsCashBalanceDAO<AmsCashBalance> getAmsCashBalanceDAO() {
		return amsCashBalanceDAO;
	}

	public void setAmsCashBalanceDAO(IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO) {
		this.amsCashBalanceDAO = amsCashBalanceDAO;
	}

	public IAmsExchangerSymbolDAO<AmsExchangerSymbol> getAmsExchangerSymbolDAO() {
		return amsExchangerSymbolDAO;
	}

	public void setAmsExchangerSymbolDAO(IAmsExchangerSymbolDAO<AmsExchangerSymbol> amsExchangerSymbolDAO) {
		this.amsExchangerSymbolDAO = amsExchangerSymbolDAO;
	}

	public IAmsExchangerDAO<AmsExchanger> getAmsExchangerDAO() {
		return amsExchangerDAO;
	}

	public void setAmsExchangerDAO(IAmsExchangerDAO<AmsExchanger> amsExchangerDAO) {
		this.amsExchangerDAO = amsExchangerDAO;
	}

	public IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> getAmsViewFeSearchHistoryDAO() {
		return amsViewFeSearchHistoryDAO;
	}

	public void setAmsViewFeSearchHistoryDAO(IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> amsViewFeSearchHistoryDAO) {
		this.amsViewFeSearchHistoryDAO = amsViewFeSearchHistoryDAO;
	}

	public IAmsViewFeExchangerHistoryDAO<AmsViewFeExchangerHistory> getAmsFeExchangerHistoryDAO() {
		return amsFeExchangerHistoryDAO;
	}

	public void setAmsFeExchangerHistoryDAO(IAmsViewFeExchangerHistoryDAO<AmsViewFeExchangerHistory> amsFeExchangerHistoryDAO) {
		this.amsFeExchangerHistoryDAO = amsFeExchangerHistoryDAO;
	}

	public IAmsDepositDAO<AmsDeposit> getDepositDAO() {
		return depositDAO;
	}

	public void setDepositDAO(IAmsDepositDAO<AmsDeposit> depositDAO) {
		this.depositDAO = depositDAO;
	}

	public ISysUniqueidCounterDAO<SysUniqueidCounter> getiSysUniqueidCounterDAO() {
		return iSysUniqueidCounterDAO;
	}

	public void setiSysUniqueidCounterDAO(ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO) {
		this.iSysUniqueidCounterDAO = iSysUniqueidCounterDAO;
	}

	public ISysAppDateDAO<SysAppDate> getiSysAppDateDAO() {
		return iSysAppDateDAO;
	}

	public void setiSysAppDateDAO(ISysAppDateDAO<SysAppDate> iSysAppDateDAO) {
		this.iSysAppDateDAO = iSysAppDateDAO;
	}

	public IAmsCashflowDAO<AmsCashflow> getCashflowDAO() {
		return cashflowDAO;
	}

	public void setCashflowDAO(IAmsCashflowDAO<AmsCashflow> cashflowDAO) {
		this.cashflowDAO = cashflowDAO;
	}

	public IAmsCashBalanceDAO<AmsCashBalance> getCashBalanceDAO() {
		return cashBalanceDAO;
	}

	public void setCashBalanceDAO(IAmsCashBalanceDAO<AmsCashBalance> cashBalanceDAO) {
		this.cashBalanceDAO = cashBalanceDAO;
	}

	public IAmsTransferMoneyDAO<AmsTransferMoney> getTransferMoneyDAO() {
		return transferMoneyDAO;
	}

	public void setTransferMoneyDAO(IAmsTransferMoneyDAO<AmsTransferMoney> transferMoneyDAO) {
		this.transferMoneyDAO = transferMoneyDAO;
	}

	public IAmsCustomerServiceDAO<AmsCustomerService> getCustomerServiceDAO() {
		return customerServiceDAO;
	}

	public void setCustomerServiceDAO(IAmsCustomerServiceDAO<AmsCustomerService> customerServiceDAO) {
		this.customerServiceDAO = customerServiceDAO;
	}

	public IAmsWithdrawalDAO<AmsWithdrawal> getWithdrawalDAO() {
		return withdrawalDAO;
	}

	public void setWithdrawalDAO(IAmsWithdrawalDAO<AmsWithdrawal> withdrawalDAO) {
		this.withdrawalDAO = withdrawalDAO;
	}

    public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
        return amsCustomerSurveyDAO;
    }

    public void setAmsCustomerSurveyDAO(IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
        this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
    }

    /* (non-Javadoc)
      * @see phn.nts.ams.fe.business.IExchangerManager#getExchangerHistory(java.lang.String)
      */
	@Override
	public ExchangerTransactionSearchCondition getExchangerHistory(String transctionId) {
		AmsViewFeExchangerHistory exchangerHistory = amsFeExchangerHistoryDAO.findById(AmsViewFeExchangerHistory.class, transctionId);
		if(exchangerHistory != null){
			ExchangerTransactionSearchCondition info = new ExchangerTransactionSearchCondition();
			info.setBeneficiaryAccountName(exchangerHistory.getBeneficiaryAccountName());
			info.setBeneficiaryAccountNo(exchangerHistory.getBeneficiaryAccountNo());
			info.setBeneficiaryBankAddress(exchangerHistory.getBeneficiaryBankAddress());
			info.setBeneficiaryBankName(exchangerHistory.getBeneficiaryBankName());
			info.setBeneficiaryBranchName(exchangerHistory.getBeneficiaryBranchName());
			info.setBeneficiarySwiftCode(exchangerHistory.getBeneficiarySwiftCode());
			return info;
		}
		return null;
	}

	@Override
	public Map<String, String> getMapExchanger(String wlCode, String currencyCode, String customerId) {
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

	public IAmsCustomerDAO<AmsCustomer> getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(IAmsCustomerDAO<AmsCustomer> customerDAO) {
		this.customerDAO = customerDAO;
	}

	/**
	 * @return the jmsContextSender
	 */
	public IJmsContextSender getJmsContextSender() {
		return jmsContextSender;
	}

	/**
	 * @param jmsContextSender the jmsContextSender to set
	 */
	public void setJmsContextSender(IJmsContextSender jmsContextSender) {
		this.jmsContextSender = jmsContextSender;
	}
}
