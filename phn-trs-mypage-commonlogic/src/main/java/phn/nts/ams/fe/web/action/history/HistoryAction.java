package phn.nts.ams.fe.web.action.history;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.ams.web.condition.AmsFeHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.FormatHelper;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.business.IHistoryManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.HistoryModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.CsvWriter;
import phn.nts.social.fe.web.action.BaseSocialAction;

import com.opensymphony.xwork2.ActionContext;

public class HistoryAction extends BaseSocialAction<HistoryModel> {
    private static final long serialVersionUID = 1L;
    private static Logit log = Logit.getInstance(HistoryAction.class);
    private HistoryModel model = new HistoryModel();
    private IHistoryManager historyManager = null;
    private IAccountManager accountManager = null;
    private IExchangerManager exchangerManager;
    private IIBManager ibManager = null;
    private String msgCode = null;
    private String result = null;
    private String fromDate = null;
    private String toDate = null;
    private String fileName;
    private String filePath;
    private static final String CONFIGPATH = "configs.properties";
    
    private static final String HISTORY_TRANSFER_NUMBER_KEY = "nts.ams.history.header.transfer.number";
    private static final String HISTORY_ACCOUNT_NUMBER_KEY = "nts.ams.history.header.account.number";
    private static final String HISTORY_FULLNAME_KEY = "nts.ams.history.header.fullname";
    private static final String HISTORY_CLASSIFICATION_KEY = "nts.ams.history.header.classification";
    private static final String HISTORY_METHOD_KEY = "nts.ams.history.header.method";
    private static final String HISTORY_AMOUNT_KEY = "nts.ams.history.header.money.amount";
    private static final String HISTORY_DEPOSIT_WITHDRAWAL_KEY = "nts.ams.history.header.currency.deposit.withdrawal";
    private static final String HISTORY_RELIABILITY_KEY = "nts.ams.history.header.reliability.by.date";
    private static final String HISTORY_WITHDRAWAL_BASE_KEY = "nts.ams.history.header.withdrawal.base";
    private static final String HISTORY_TRANSFER_DESTINATION_KEY = "nts.ams.history.header.transfer.destination";
    private static final String HISTORY_STATUS_KEY = "nts.ams.history.header.status";

    public HistoryModel getModel() {
        return model;
    }

    public IHistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(IHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public void getFromDate(String fromDate) {
        if (fromDate != null) {
            model.getAmsFeHistorySearchCondition().setFromDate(fromDate);
        }
    }

    public void getToDate(String toDate) {
        if (toDate != null) {
            model.getAmsFeHistorySearchCondition().setToDate(toDate);
        }
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String index() {
        String userId = "";
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                userId = frontUserOnline.getUserId();

            }
        }
        if (result != null) {
            getTransactionHistory(userId, fromDate, toDate);
            getMsgCode(result);
        }

        try {
            PagingInfo pagingInfo = model.getPagingInfo();
            if (pagingInfo == null) {
                pagingInfo = new PagingInfo();
            }
            pagingInfo.setOffset(10);
            setRawUrl(IConstants.FrontEndActions.HISTORY_INDEX);
            AmsFeHistorySearchCondition amsFeHistorySearchCondition = model.getAmsFeHistorySearchCondition();
            if (amsFeHistorySearchCondition == null) {
                amsFeHistorySearchCondition = new AmsFeHistorySearchCondition();

                List<String> listCustomerId = new ArrayList<String>();
                listCustomerId.add(userId);
                List<String> listCustomerInfo = ibManager.getListClientCustomerInfo(userId);
                if (listCustomerInfo != null && listCustomerInfo.size() > 0) {
                    for (String customerId : listCustomerInfo) {
                        listCustomerId.add(customerId);
                    }
                }
                amsFeHistorySearchCondition.setListCustomerId(listCustomerId);
                amsFeHistorySearchCondition.setCustomerId(userId);
                model.setAmsFeHistorySearchCondition(amsFeHistorySearchCondition);
            }
            // add more conditions
            getListforHistoryScreen();
            //search
            List<AmsFeHistorySearchCondition> listAmsFeSearchHistory = historyManager.getListAmsFeSearchHistory(amsFeHistorySearchCondition, pagingInfo, userId);
            @SuppressWarnings("rawtypes")
            Map session = ActionContext.getContext().getSession();
            session.put("amsFeHistorySearchCondition", amsFeHistorySearchCondition);
            if (listAmsFeSearchHistory != null && listAmsFeSearchHistory.size() > 0) {
                model.setListAmsTransactionHistory(listAmsFeSearchHistory);
            } else {
                model.setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
            }
            model.setPagingInfo(pagingInfo);
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));
            model.setUserLanguage(getLanguage());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return SUCCESS;
    }

    public String search() {
        try {
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));
            model.setUserLanguage(getLanguage());
            String customerId = null;
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    customerId = frontUserOnline.getUserId();
                }
            }
            getListforHistoryScreen();
            PagingInfo pagingInfo = model.getPagingInfo();
            if (pagingInfo == null) {
                pagingInfo = new PagingInfo();
            }
            //pagingInfo.setOffset(10);
            AmsFeHistorySearchCondition condition = model.getAmsFeHistorySearchCondition();
            if (condition == null) {
                condition = new AmsFeHistorySearchCondition();
                condition = new AmsFeHistorySearchCondition();
//				condition.setToDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_DB));
//				condition.setFromDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_DB));
                model.setAmsFeHistorySearchCondition(condition);
            }
            validateHistory(condition, customerId);
            if (hasFieldErrors()) {
                getListforHistoryScreen();
                return INPUT;
            }

            if (customerId != null) {
                condition.setCustomerId(customerId);
            } else {
                model.setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
                return INPUT;
            }
            List<String> listCustomerId = new ArrayList<String>();
            listCustomerId.add(customerId);
            List<String> listCustomerInfo = ibManager.getListClientCustomerInfo(customerId);
            if (listCustomerInfo != null && listCustomerInfo.size() > 0) {
                for (String userId : listCustomerInfo) {
                    listCustomerId.add(userId);
                }
            }
            condition.setListCustomerId(listCustomerId);
            Date dFromDate = DateUtil.toDate(condition.getFromDate(), IConstants.DATE_TIME_FORMAT.DATE_DB);
            if (dFromDate != null) {
                condition.setFromDate(DateUtil.toString(dFromDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
            }
            Date dToDate = DateUtil.toDate(condition.getToDate(), IConstants.DATE_TIME_FORMAT.DATE_DB);
            if (dToDate != null) {
                condition.setToDate(DateUtil.toString(dToDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
            }
            //List<AmsFeSearchHistory> listAmsFeSearchHistory = historyManager.getListAmsFeSearchHistory(condition, pagingInfo);
            List<AmsFeHistorySearchCondition> listAmsFeSearchHistory = historyManager.getListAmsFeSearchHistory(condition, pagingInfo, customerId);
            @SuppressWarnings("rawtypes")
            Map session = ActionContext.getContext().getSession();
            session.put("amsFeHistorySearchCondition", condition);


            if (dFromDate != null) {
                condition.setFromDate(DateUtil.toString(dFromDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
            }
            if (dToDate != null) {
                condition.setToDate(DateUtil.toString(dToDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
            }

            if (listAmsFeSearchHistory != null) {
                model.setListAmsTransactionHistory(listAmsFeSearchHistory);
            } else {
                model.setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
            }


        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ERROR;
        }

        return SUCCESS;
    }
    
    //[NTS1.0-le.hong.ha]May 28, 2013A - Start 
    public String getTransacLang(String transType, String inOut){
    	if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.DEPOSIT) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)){
    		return getText("transaction_method.Deposit.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.WITHDRAWAL) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)){
    		return getText("transaction_method.Withdraw.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.CASHBACK) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)){
    		return getText("transaction_method.Cashback.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.CASHBACK) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)) {
    		return getText("transaction_method.Cashback.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.INVITE_FEE) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)) {
    		return getText("transaction_method.Invite_Fee.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.INVITE_FEE) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)) {
    		return getText("transaction_method.Invite_Fee.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.SWAP) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)) {
    		return getText("transaction_method.Swap.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.SWAP) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)) {
    		return getText("transaction_method.Swap.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.PL) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)) {
    		return getText("transaction_method.PL.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.PL) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)) {
    		return getText("transaction_method.PL.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.SOCIAL_FEE) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.IN)) {
    		return getText("transaction_method.Scial_Fee.In");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.SOCIAL_FEE) && inOut.equals(ITrsConstants.TRANSACTION_IN_OUT.OUT)) {
    		return getText("transaction_method.Scial_Fee.Out");
    	}else if(transType.equals(ITrsConstants.TRANSACTION_TYPE_NAME.TRANSFER)){
    		return getText("transaction_method.transfer");
    	}else{
    		return getText("transaction_method.Withdraw.Out");
    	}
    }

    public void validateHistory(AmsFeHistorySearchCondition amsFeHistorySearchCondition, String customerId) {
        hasFieldErrors();
        if (amsFeHistorySearchCondition != null) {

            String fromDate = amsFeHistorySearchCondition.getFromDate();
            String toDate = amsFeHistorySearchCondition.getToDate();
            Integer customerType = amsFeHistorySearchCondition.getCustomerType();
            String ibCustomerId = amsFeHistorySearchCondition.getIbcustomerId();

            if (toDate != null && fromDate != null) {
                Date fromDate_date = DateUtil.toDate(fromDate, IConstants.DATE_TIME_FORMAT.DATE_DB);
                Date toDate_date = DateUtil.toDate(toDate, IConstants.DATE_TIME_FORMAT.DATE_DB);
                if (fromDate_date != null && toDate_date != null) {
                    if (fromDate_date.after(toDate_date)) {
                    	List<String> listContent = new ArrayList<String>();
                        listContent.add(getText("nts.ams.fe.label.history.startDate"));
                        listContent.add(getText("nts.ams.fe.label.history.toDate"));
                        model.setErrorMessage(getText("MSG_NAB012",listContent));
                        addFieldError("errorMessage", getText("MSG_NAB012",listContent));
                        return;
                    }
                }
            }
            if (customerType != null) {
                if (customerType.equals(IConstants.CUSTOMER_TYPE.IB_CUSTOMER)) {
                    // check if customerId is blank or not
                    if (ibCustomerId == null || StringUtils.isBlank(ibCustomerId)) {
                        List<String> listContent = new ArrayList<String>();
                        listContent.add(getText("nts.ams.fe.label.history.customerId"));
                        model.setErrorMessage(getText("MSG_NAF001", listContent));
                        addFieldError("errorMessage", getText("MSG_NAF001", listContent));
                        return;
                    }
                    // check if customerID inputed is IB client of current user or not
                    if (!(historyManager.isIBClientUser(customerId, ibCustomerId))) {
                        model.setErrorMessage(getText("nts.ams.fe.message.history.notibclient"));
                        addFieldError("errorMessage", getText("nts.ams.fe.message.history.notibclient"));
                        return;
                    }
                }
            }

        }
    }

    private void getMsgCode(String msgCode) {
        if (msgCode != null) {
            if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_CANCEL)) {
                model.setSuccessMessage(getText("nts.ams.fe.message.deposit.cancel"));
            }
            if (msgCode.equals(IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANCEL)) {
                model.setSuccessMessage(getText("nts.ams.fe.message.withdrawal.cancel.MSG_NAB040"));
            }
            if (msgCode.equals(ERROR)) {
                model.setErrorMessage(getText("MSG_NAB039"));
            }
        }
    }

    public String transactionCancel() {
        log.info("[Start] cancel a transaction in history screen");
        try {
            String transactionId = model.getTransactionId();
            String transactionType = model.getTransactionType();
            String fromDate = model.getAmsFeHistorySearchCondition().getFromDate();
            String toDate = model.getAmsFeHistorySearchCondition().getToDate();
            if (transactionType.equalsIgnoreCase(IConstants.STRING_TRANSACTION_TYPE.DEPOSIT)) {
                AmsDeposit amsDeposit = new AmsDeposit();
                if (transactionId != null) {
                    log.info("Transaction Id" + transactionId);
                    amsDeposit = historyManager.getDeposit(transactionId);
                    if (amsDeposit.getStatus().equals(IConstants.STATUS_DEPOSIT.REQUESTING)) {
                        amsDeposit.setStatus(IConstants.STATUS_DEPOSIT.CANCEL);
                        amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                        historyManager.updateAmsDepositStatus(amsDeposit);
                        setRawUrl("/history/index?result=" + IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_CANCEL + "&fromDate=" + fromDate + "&toDate=" + toDate);
                    } else {
                        setRawUrl("/history/index?result=" + ERROR + "&fromDate=" + fromDate + "&toDate=" + toDate);
                    }
                }

            } else if (transactionType.equalsIgnoreCase(IConstants.STRING_TRANSACTION_TYPE.WITHDRAWAL)) {
                AmsWithdrawal amsWithdrawal = new AmsWithdrawal();
                if (transactionId != null) {
                    log.info("Transaction Id" + transactionId);
                    amsWithdrawal = historyManager.getAmsWithdrawal(transactionId);
                    if (IConstants.STATUS_WITHDRAW.REQUESTING.equals(amsWithdrawal.getStatus())) {
                        amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.CANCEL);
                        amsWithdrawal.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                        historyManager.updateAmsWithdrawalStatus(amsWithdrawal);
                        setRawUrl("/history/index?result=" + IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANCEL + "&fromDate=" + fromDate + "&toDate=" + toDate);
                    } else {
                        setRawUrl("/history/index?result=" + ERROR + "&fromDate=" + fromDate + "&toDate=" + toDate);
                    }
                }

            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ERROR;
        }
        log.info("[End] cancel a transaction in history screen");

        return SUCCESS;
    }

    /**
     * get list of transaction after changing to cancel status
     */
    public void getTransactionHistory(String userId, String fromDate, String toDate) {
        getListforHistoryScreen();
        PagingInfo pagingInfo = model.getPagingInfo();
        if (pagingInfo == null) {
            pagingInfo = new PagingInfo();
        }
        AmsFeHistorySearchCondition condition = model.getAmsFeHistorySearchCondition();
        if (condition == null) {
            condition = new AmsFeHistorySearchCondition();
        }
        condition.setCustomerId(userId);
        Date dFromDate = DateUtil.toDate(fromDate, IConstants.DATE_TIME_FORMAT.DATE_DB);
        if (dFromDate != null) {
            condition.setFromDate(DateUtil.toString(dFromDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
        }
        Date dToDate = DateUtil.toDate(toDate, IConstants.DATE_TIME_FORMAT.DATE_DB);
        if (dToDate != null) {
            condition.setToDate(DateUtil.toString(dToDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
        }
        List<AmsFeHistorySearchCondition> listAmsFeSearchHistory = historyManager.getListAmsFeSearchHistory(condition, pagingInfo, userId);
        if (listAmsFeSearchHistory != null) {
            model.setListAmsTransactionHistory(listAmsFeSearchHistory);
        }
        if (dFromDate != null) {
            condition.setFromDate(DateUtil.toString(dFromDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
        }
        if (dToDate != null) {
            condition.setToDate(DateUtil.toString(dToDate, IConstants.DATE_TIME_FORMAT.DATE_DB));
        }
    }

    /**
     * get Lists for history screen
     */
    public void getListforHistoryScreen() {
        try {
            // get List for payment method
            Map<String, String> mapPaymentMethod = (Map<String, String>) ObjectCopy.copy(SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD));

            //[NTS1.0-le.hong.ha]May 15, 2013D - Start 
//            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//            if (frontUserDetails != null) {
//                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
//                if (frontUserOnline != null) {
//                    Map<String, String> mapExchanger = exchangerManager.getMapExchanger(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
//                    if (mapExchanger == null || mapExchanger.size() <= 0) {
//                        mapPaymentMethod.remove(String.valueOf(IConstants.PAYMENT_METHOD.EXCHANGER));
//                    }
//                }
//            }
			//[NTS1.0-le.hong.ha]May 15, 2013D - End
            

            model.setMapPaymentMethod(mapPaymentMethod);
            // get List for transaction Method
            Map<String, String> mapTransactionMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.TRANSACTION_METHOD);
            model.setMapTransactionMethod(mapTransactionMethod);
            // get List for status
            Map<String, String> mapDepositStatus = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_STATUS);
            model.setMapDepositStatus(mapDepositStatus);
            Map<String, String> mapCustomerType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CUSTOMER_TYPE);
            model.setMapCustomerType(mapCustomerType);
            Map<String, String> mapCashBackStatus = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.STATUS_CASHBACK);
            model.setMapCashBackStatus(mapCashBackStatus);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

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
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String exportCsv() {
        try {
            generateFileName();
            String userId = "";
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    userId = frontUserOnline.getUserId();
                }
            }
           
            @SuppressWarnings("rawtypes")
            Map session = ActionContext.getContext().getSession();
            AmsFeHistorySearchCondition searchInfo = (AmsFeHistorySearchCondition) session.get("amsFeHistorySearchCondition");
            List<String> listCustomerId = new ArrayList<String>();
            listCustomerId.add(userId);
            List<String> listCustomerInfo = ibManager.getListClientCustomerInfo(userId);
            if (listCustomerInfo != null && listCustomerInfo.size() > 0) {
                for (String customerId : listCustomerInfo) {
                    listCustomerId.add(customerId);
                }
            }
            searchInfo.setListCustomerId(listCustomerId);

            CsvWriter csv = new CsvWriter(filePath, ',', Charset.forName("Shift-JIS"));
            writeHeaders(csv);

            int pageIndex = PagingInfo.DEFAULT_INDEX;

            while (true) {
                PagingInfo pagingInfo = model.getPagingInfo();
                pagingInfo.setIndexPage(pageIndex);
                Map<String, String> mapStatusAll = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.STATUS_TRANSFER);
                List<AmsFeHistorySearchCondition> listAmsFeSearchHistory = historyManager.getListAmsFeSearchHistory(searchInfo, pagingInfo, userId);

                if (listAmsFeSearchHistory != null) {
                    for (AmsFeHistorySearchCondition amsSearchCondition : listAmsFeSearchHistory) {
                        writeRecord(csv, amsSearchCondition, mapStatusAll);
                    }
                }

                if (pagingInfo.getIndexPage() < pagingInfo.getTotalPage()) {
                    pageIndex = pagingInfo.getIndexPage() + 1;
                } else {
                    break;
                }
            }
            csv.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    private void generateFileName() throws FileNotFoundException {
        Calendar c = Calendar.getInstance();
        String timetamp = DateUtil.toString(c.getTime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT);
        String timetampSecond = String.format("%02d%02d%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        fileName = "transaction_history_" + timetamp + "_" + timetampSecond + ".csv";
        Properties properties = Helpers.getProperties(CONFIGPATH);
        filePath = properties.getProperty("pathFileCsvOutput") + "/" + fileName;
    }

    private void writeHeaders(CsvWriter csv) throws IOException {
        csv.write(getText(HISTORY_TRANSFER_NUMBER_KEY));
        csv.write(getText(HISTORY_ACCOUNT_NUMBER_KEY));
        csv.write(getText(HISTORY_FULLNAME_KEY));
        csv.write(getText(HISTORY_CLASSIFICATION_KEY));
        csv.write(getText(HISTORY_METHOD_KEY));
        csv.write(getText(HISTORY_AMOUNT_KEY));
        csv.write(getText(HISTORY_DEPOSIT_WITHDRAWAL_KEY));
        csv.write(getText(HISTORY_RELIABILITY_KEY));
        csv.write(getText(HISTORY_WITHDRAWAL_BASE_KEY));
        csv.write(getText(HISTORY_TRANSFER_DESTINATION_KEY));
        csv.write(getText(HISTORY_STATUS_KEY));

        csv.endRecord();
    }

    private void writeRecord(CsvWriter csv, AmsFeHistorySearchCondition record, Map<String, String> map) throws IOException {
        String fromPattern = "";
        String fromCurrencyCode = record.getFromCurrencyCode();
      
        csv.write(record.getTransactionId());
        csv.write(record.getCustomerId());
        csv.write(record.getCustomerName());
        csv.write(getTransacLang(record.getType(), record.getInOut()));
        csv.write(record.getMethodName());
    
        if (fromCurrencyCode != null) {
            fromPattern = MasterDataManagerImpl.getInstance().getPattern(fromCurrencyCode);
            String amountFrom = FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(fromCurrencyCode, record.getAmountFrom().doubleValue()), fromPattern);
            csv.write(amountFrom);
            csv.write(getText("currencycode."+fromCurrencyCode));
        } else {
            csv.write("");
            csv.write("");
        }
      
        String dateTimeFormatted = DateUtil.toString(record.getAcceptTime(), getText("nts.ams.fe.label.date.full.pattern"));
      
        csv.write(dateTimeFormatted == null ? "" : dateTimeFormatted);
      
        csv.write(record.getSource());
        csv.write(record.getDestination());
      
        csv.write(showStatusString(record.getType(), record.getStatus()));
      
        csv.endRecord();
    }

    /**
     * Hard code status following hard code in JSP page
     * (to fix a bug about status when export csv file)
     *
     * @param
     * @return
     * @throws
     * @author le.xuan.tuong
     * @CrDate Nov 6, 2012
     */
    public String showStatusString(String type, Integer status) {
        if (StringUtil.isEmpty(type) || status == null) {
            return "";
        }
        String result = "";
        if (ITrsConstants.TRANSACTION_TYPE_NAME.TRANSFER.equalsIgnoreCase(type)) {
            if (status == 3 || status.intValue() == 9999)
                result = getText("transfer_status.inprogress"); //"InProgress";
            else if (status == 1)
                result = getText("transfer_status.success"); //"Success";
            else if (status == 0)
                result = getText("transfer_status.cancel"); //"Cancel";
        } else if (ITrsConstants.TRANSACTION_TYPE_NAME.PL.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.SWAP.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.CASHBACK.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.DEPOSIT.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.WITHDRAWAL.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.SOCIAL_FEE.equalsIgnoreCase(type)
        	 		|| ITrsConstants.TRANSACTION_TYPE_NAME.INVITE_FEE.equalsIgnoreCase(type)) {
            if (status == 5)
                result = getText("transfer_status.Requesting");//"Requesting";
            else if (status == 6 || status == 2)
                result = getText("transfer_status.inprogress");//"InProgress";
            else if (status == 4 || status == 7)
                result = getText("transfer_status.cancel");//"Cancel";
            else if (status == 1 || status == 3)
                result = getText("transfer_status.success");//"Success";
        }

        return result;
    }

    public InputStream getCsvFile() {
        try {
            InputStream is = new FileInputStream(new File(filePath));
            return is;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStatusString(Map<String, String> map, String status) {
        for (String key : map.keySet()) {
            if (status.equals(key)) {
                return map.get(key);
            }
        }
        return "";
    }

    public IExchangerManager getExchangerManager() {
        return exchangerManager;
    }

    public void setExchangerManager(IExchangerManager exchangerManager) {
        this.exchangerManager = exchangerManager;
    }

}
