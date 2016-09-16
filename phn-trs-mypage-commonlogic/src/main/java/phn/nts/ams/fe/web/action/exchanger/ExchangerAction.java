package phn.nts.ams.fe.web.action.exchanger;

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import phn.com.nts.ams.web.condition.ExchangerTransactionSearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.util.common.*;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.ExchangerModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.CsvWriter;
import phn.nts.social.fe.web.action.BaseSocialAction;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Nguyen.Manh.Thang
 * @version NTS1.0
 * @description
 * @CrDate Sep 18, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class ExchangerAction extends BaseSocialAction<ExchangerModel> {
    /**
     *
     */
    private static final long serialVersionUID = -126146137526919945L;
    private static Logit log = Logit.getInstance(ExchangerAction.class);
    private ExchangerModel model = new ExchangerModel();
    private IExchangerManager exchangerManager;
//    private IBalanceManager balanceManager;
    private String result;
    private String msgCode;
    private String filePath;
    private String fileName;
    private static final String CONFIGPATH = "configs.properties";

    /**
     * Search data
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public String index() {
        getListforExchangerScreen();
        try {
            if (SUCCESS.equals(httpRequest.getParameter(SUCCESS))) {
                String msg = getText("MSG_NAB003", new String[]{getText("nts.ams.fe.msg.update")});
                model.setSuccessMessage(msg);
            } else if (ERROR.equals(httpRequest.getParameter(ERROR))) {
                String msg = getText("MSG_NAB044", new String[]{getText("nts.ams.fe.msg.update")});
                model.setErrorMessage(msg);
            }

            PagingInfo pagingInfo = model.getPagingInfo();
            if (pagingInfo == null) {
                pagingInfo = new PagingInfo();
                model.setPagingInfo(pagingInfo);
            }
            String customerId = "";
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    customerId = frontUserOnline.getUserId();
                }
            }
            setRawUrl(IConstants.FrontEndActions.EXCHANGER_INDEX);
            // add more conditions
            ExchangerTransactionSearchCondition condition = model.getExchangerTransactionSearchCondition();
            if (condition == null) {
                condition = new ExchangerTransactionSearchCondition();

            }
            condition.setExchangerCustomerId(customerId);
            if (httpRequest.getParameter("fromDate") != null) {
                condition.setFromDate(httpRequest.getParameter("fromDate"));
            }
            if (httpRequest.getParameter("toDate") != null) {
                condition.setToDate(httpRequest.getParameter("toDate"));
            }
            if (httpRequest.getParameter("customerId") != null) {
                condition.setCustomerId(httpRequest.getParameter("customerId"));
            }
            if (httpRequest.getParameter("customerName") != null) {
                condition.setCustomerName(httpRequest.getParameter("customerName"));
            }
            if (httpRequest.getParameter("type") != null) {
                condition.setType(httpRequest.getParameter("type"));
            }
            if (httpRequest.getParameter("status") != null) {
                condition.setStatus(Integer.valueOf(httpRequest.getParameter("status")));
            }

            model.setExchangerTransactionSearchCondition(condition);
            if (!validateSearch()) {
                return SUCCESS;
            }
            @SuppressWarnings("rawtypes")
            Map session = ActionContext.getContext().getSession();
            session.put("exchangerCondition", condition);

            String customerName = condition.getCustomerName();
            if (!StringUtil.isEmpty(customerName)) {
                condition.setCustomerName(customerName.trim());
            }
            String customerID = condition.getCustomerId();
            if (!StringUtil.isEmpty(customerID)) {
                condition.setCustomerId(customerID.trim());
            }
            //search
            List<ExchangerTransactionSearchCondition> listExchangerTransactionSearchConditions = exchangerManager.getExchangerHistory(condition, pagingInfo);

            if (listExchangerTransactionSearchConditions != null && listExchangerTransactionSearchConditions.size() > 0) {
                model.setListExchangerTransactionSearchConditions(listExchangerTransactionSearchConditions);
            } else {
                model.setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
                model.setListExchangerTransactionSearchConditions(null);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


        return SUCCESS;
    }

    /**
     * Validate search condition
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public boolean validateSearch() {
        ExchangerTransactionSearchCondition condition = model.getExchangerTransactionSearchCondition();
        String from = condition.getFromDate();
        String to = condition.getToDate();
        if (!StringUtil.isEmpty(from) && !StringUtil.isEmpty(to)) {
            Date dateFrom = DateUtil.toDate(from, DateUtil.PATTERN_MMDDYYYY);
            Date dateTo = DateUtil.toDate(to, DateUtil.PATTERN_MMDDYYYY);
            if (dateFrom.after(dateTo)) {
                String msg = getText("MSG_NAB012", new String[]{getText("nts.ams.fe.label.exchanger.fromdate"), getText("nts.ams.fe.label.exchanger.toDate")});
                model.setErrorMessage(msg);
                return false;
            }
        }
        return true;
    }

    public boolean validateUpdate() {
        double totalDepositAmount = 0;
        List<ExchangerTransactionSearchCondition> historyInfos = model.getListExchangerTransactionSearchConditions();
        for (ExchangerTransactionSearchCondition exchangerTransactionSearchCondition : historyInfos) {
            if (exchangerTransactionSearchCondition.getTypeStr().equals(IConstants.EXCHANGER_TYPE.DEPOSIT)
                    && exchangerTransactionSearchCondition.getStatus().intValue() == IConstants.DEPOSIT_STATUS.FINISHED.intValue()) {
                totalDepositAmount = totalDepositAmount + Double.valueOf(exchangerTransactionSearchCondition.getAmountD());
            } else if (exchangerTransactionSearchCondition.getTypeStr().equals(IConstants.EXCHANGER_TYPE.WITHDRAWAL)
                    && exchangerTransactionSearchCondition.getStatus().intValue() == IConstants.DEPOSIT_STATUS.FINISHED.intValue()) {
                double amountAvailabe = exchangerManager.getAmountAvailable(exchangerTransactionSearchCondition.getCurrencyCode(), exchangerTransactionSearchCondition.getCustomerId());
                if (amountAvailabe < 0) {
                    String msg = getText("MSG_NAB094", new String[]{exchangerTransactionSearchCondition.getTransactionId()});
                    model.setErrorMessage(msg);
                    return false;
                }
//			if(amountAvailabe < Double.valueOf(exchangerTransactionSearchCondition.getAmountD())){
//				String msg = getText("MSG_NAB094", new String[]{exchangerTransactionSearchCondition.getTransactionId()});
//				model.setErrorMessage(msg);
//				return false;
//			}
            }
        }
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        String customerId = "";
        String currencyCode = "";
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                customerId = frontUserOnline.getUserId();
                currencyCode = frontUserOnline.getCurrencyCode();
            }
        }
        double amountAvailabe = exchangerManager.getAmountAvailable(currencyCode, customerId);
        if (amountAvailabe < totalDepositAmount) {
            String pattern = MasterDataManagerImpl.getInstance().getPattern(currencyCode);
            String msg = getText("MSG_NAB093", new String[]{FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(currencyCode, amountAvailabe), pattern) + "",
                    currencyCode, FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(currencyCode, totalDepositAmount), pattern) + "", currencyCode});
            model.setErrorMessage(msg);
            return false;
        }

        return true;
    }

    /**
     * Redirect to confirm screen
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public String confirmUpdate() {
        getListforExchangerScreen();

        try {
            List<ExchangerTransactionSearchCondition> historyInfos = model.getListExchangerTransactionSearchConditions();
            List<ExchangerTransactionSearchCondition> newHistoryInfos = new ArrayList<ExchangerTransactionSearchCondition>();
            boolean checked = false;
            for (ExchangerTransactionSearchCondition exchangerTransactionSearchCondition : historyInfos) {
                if (exchangerTransactionSearchCondition.isSelected()) {
                    checked = true;
                    newHistoryInfos.add(exchangerTransactionSearchCondition);
                }
            }
            model.setListExchangerTransactionSearchConditions(newHistoryInfos);
            if (!checked) {
                String msg = getText("MSG_NAB018");
                model.setErrorMessage(msg);
                index();
                return ERROR;
            } else {
                if (!validateUpdate()) {
                    index();
                    return ERROR;
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SUCCESS;
    }

    /**
     * Update status for all selected exchanger
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public String update() {
        getListforExchangerScreen();
        try {
            List<ExchangerTransactionSearchCondition> exchangers = model.getListExchangerTransactionSearchConditions();
            if (!validateUpdate()) {
                return INPUT;
            }
            int status = exchangerManager.updateExchanger(exchangers);
            if (status == 1) {
                return SUCCESS;
            } else {
                return ERROR;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR;
        }


    }


    /**
     * Get Bank information
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public String getBankInfo() {
        String transactionId = httpRequest.getParameter("transactionId");
        if (!StringUtil.isEmail(transactionId)) {
            ExchangerTransactionSearchCondition info = exchangerManager.getExchangerHistory(transactionId);
            model.setBankInfo(info);
        }
        return SUCCESS;
    }


    /**
     * get Lists for history screen
     */
    public void getListforExchangerScreen() {
        try {
            // get List for status
            Map<Integer, String> mapStatus = new HashMap<Integer, String>();
            mapStatus.put(IConstants.DEPOSIT_STATUS.REQUESTING, getText("nts.ams.fe.label.exchanger.status.requesting"));
            mapStatus.put(IConstants.DEPOSIT_STATUS.IN_PROGRESS, getText("nts.ams.fe.label.exchanger.status.inprogress"));
            mapStatus.put(IConstants.DEPOSIT_STATUS.FINISHED, getText("nts.ams.fe.label.exchanger.status.finish"));
            mapStatus.put(IConstants.DEPOSIT_STATUS.CANCEL, getText("nts.ams.fe.label.exchanger.status.cancel"));

            model.setMapStatus(mapStatus);

            Map<String, String> mapType = new HashMap<String, String>();
            mapType.put(IConstants.EXCHANGER_TYPE.DEPOSIT, getText("nts.ams.fe.label.deposit.deposit"));
            mapType.put(IConstants.EXCHANGER_TYPE.WITHDRAWAL, getText("nts.ams.fe.label.deposit.withdrawal"));
            model.setMapType(mapType);
            Map<Integer, String> mapStatusRequest = new HashMap<Integer, String>();
            mapStatusRequest.put(IConstants.DEPOSIT_STATUS.REQUESTING, getText("nts.ams.fe.label.exchanger.status.requesting"));
            mapStatusRequest.put(IConstants.DEPOSIT_STATUS.IN_PROGRESS, getText("nts.ams.fe.label.exchanger.status.inprogress"));
            mapStatusRequest.put(IConstants.DEPOSIT_STATUS.CANCEL, getText("nts.ams.fe.label.exchanger.status.cancel"));
            model.setMapStatusRequest(mapStatusRequest);

            Map<Integer, String> mapStatusInprogress = new HashMap<Integer, String>();
            mapStatusInprogress.put(IConstants.DEPOSIT_STATUS.IN_PROGRESS, getText("nts.ams.fe.label.exchanger.status.inprogress"));
            mapStatusInprogress.put(IConstants.DEPOSIT_STATUS.FINISHED, getText("nts.ams.fe.label.exchanger.status.finish"));
            mapStatusInprogress.put(IConstants.DEPOSIT_STATUS.CANCEL, getText("nts.ams.fe.label.exchanger.status.cancel"));
            model.setMapStatusInprogress(mapStatusInprogress);

            Map<String, String> mapStatusAll = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.STATUS_TRANSFER);
            Map<Integer, String> mapStatusAllInt = new HashMap<Integer, String>();
            if (mapStatusAll != null && mapStatusAll.size() > 0) {
                String value = "";
                for (String key : mapStatusAll.keySet()) {
                    value = mapStatusAll.get(key);
                    mapStatusAllInt.put(Integer.valueOf(key), getText(value));
                }
            }
            model.setMapStatusAll(mapStatusAllInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }

    }


    /**
     * begin to load data of exchanger
     *
     * @param
     * @return String
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 17, 2012
     */
    public String setRate() {
        if (result != null) {
            getMsgCode(result);
        }
        String exchangerId = "";
        String currencyCode = "";
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                currencyCode = frontUserOnline.getCurrencyCode();
                String userId = frontUserOnline.getUserId();
                // get ExchangerID
                ExchangerInfo exchangerInfo = frontUserOnline.getExchangerInfo();
                if (exchangerInfo != null) {
                    exchangerId = exchangerInfo.getExchangerId();
                }
                //set cashBalanceInfo
                BalanceInfo balanceInfo = balanceManager.getBalanceInfo(userId, IConstants.SERVICES_TYPE.AMS, currencyCode);
                model.setBalanceInfo(balanceInfo);
                //set exchange rate
                //data of set rate
                List<ExchangerSymbolInfo> listExchangerSymbol = exchangerManager.getExchangerSymbolByExchangerId(exchangerId);
                model.setListExchangerSymbol(listExchangerSymbol);
                //data of set rate history
                PagingInfo pagingInfo = model.getPagingInfo();
                if (pagingInfo == null) {
                    pagingInfo = new PagingInfo();
                    model.setPagingInfo(pagingInfo);
                }
                List<ExchangerSymbolInfo> listExchangerHistory = exchangerManager.getExchangerSymbolHistoryByExchangerId(exchangerId, pagingInfo);
                if (listExchangerHistory != null && listExchangerHistory.size() > 0) {
                    model.setListExchangerSymbolHistory(listExchangerHistory);
                } else {
                    //not found
                    model.setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
                }
            }
        }
        return SUCCESS;
    }

    /**
     * go to confirm page
     *
     * @param
     * @return String
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 18, 2012
     */
    public String confirmSetRate() {
    	try{
    		List<ExchangerSymbolInfo> listExchangerSymbol = null;
            //validate form
            validateForm();
            //check fields have error or not
            if (hasFieldErrors()) {
                reloadData();
                return ERROR;
            } else {
                FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
                String exchangerId = "";
                if (frontUserDetails != null) {
                    FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                    if (frontUserOnline != null) {
                        // get ExchangerID
                        ExchangerInfo exchangerInfo = frontUserOnline.getExchangerInfo();
                        if (exchangerInfo != null) {
                            exchangerId = exchangerInfo.getExchangerId();
                        }
                    }
                }
                listExchangerSymbol = exchangerManager.getExchangerSymbolByExchangerId(exchangerId);
            }
            // read user information
            readExchangerUserInformation();
            //list 1
            List<ExchangerSymbolInfo> exchangeSymbol = model.getListExchangerSymbol();
            //list2
            //listExchangerSymbol//
            int size = exchangeSymbol.size();

            List<ExchangerSymbolInfo> listTemp = new ArrayList<ExchangerSymbolInfo>();
            if (listExchangerSymbol != null || listExchangerSymbol.size() > 0) {
                for (int i = 0; i < size; i++) {
                    //if list2 remove not difference
                    ExchangerSymbolInfo source = listExchangerSymbol.get(i);
                    ExchangerSymbolInfo screen = exchangeSymbol.get(i);
                    if (compare(source, screen)) {
                        listTemp.add(screen);
                    }
                }
            }

            //remove item not change from input screen
            if (listTemp.size() > 0) {
                exchangeSymbol.removeAll(listTemp);
            }

            //if nothing any change - reload screen
            if (exchangeSymbol.size() <= 0) {
                setRate();
                return ERROR;
            }

            model.setListExchangerSymbol(exchangeSymbol);
    	} catch (Exception ex){
    		log.error(ex.getMessage(), ex);
    	}
        return SUCCESS;
    }

    private void reloadData() {
        String exchangerId = "";
        String currencyCode = "";
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                currencyCode = frontUserOnline.getCurrencyCode();
                String userId = frontUserOnline.getUserId();

                //set cashBalanceInfo
                BalanceInfo balanceInfo = balanceManager.getBalanceInfo(userId, IConstants.SERVICES_TYPE.AMS, currencyCode);
                model.setBalanceInfo(balanceInfo);

                // get ExchangerID
                ExchangerInfo exchangerInfo = frontUserOnline.getExchangerInfo();
                if (exchangerInfo != null) {
                    exchangerId = exchangerInfo.getExchangerId();
                }

                PagingInfo pagingInfo = model.getPagingInfo();
                if (pagingInfo == null) {
                    pagingInfo = new PagingInfo();
                    model.setPagingInfo(pagingInfo);
                }

                List<ExchangerSymbolInfo> listExchangerHistory = exchangerManager.getExchangerSymbolHistoryByExchangerId(exchangerId, pagingInfo);
                if (listExchangerHistory != null && listExchangerHistory.size() > 0) {
                    model.setListExchangerSymbolHistory(listExchangerHistory);
                } else {
                    //not found
                    model.setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
                }
            }
        }
    }

    public boolean compare(ExchangerSymbolInfo source, ExchangerSymbolInfo srceen) {
        boolean result = true;
        if (source.getExchangerSymbolId().equals(srceen.getExchangerSymbolId()) && source.getExchangerId().equals(srceen.getExchangerId())) {
            if (!source.getSellRate().equals(srceen.getSellRate()) || !source.getBuyRate().equals(srceen.getBuyRate())) {
                result = false;
            }
        }
        return result;
    }

    public void readExchangerUserInformation() {
        // get balance info
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
            	BalanceInfo balanceInfo = model.getBalanceAmsInfo();
            	model.setBalanceInfo(balanceInfo);
//                BalanceInfo balanceInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.AMS, frontUserOnline.getCurrencyCode());
//                model.setBalanceInfo(balanceInfo);
            }
        }
    }

    /**
     * process update current rate
     *
     * @param
     * @return
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 18, 2012
     */
    public String updateSetRate() {
        boolean updateResult = false;
        updateResult = exchangerManager.updateCurrentRate(model.getListExchangerSymbol());
        if (updateResult) {
            //Show message when update rate successfully
            setMsgCode(IConstants.EXCHANGER_MSG_CODE.MSG_EXCHANGER_SUCCESS);
        } else {
            //update rate failure
            setMsgCode(IConstants.EXCHANGER_MSG_CODE.MSG_DEPOSIT_FAILURE);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * get message code
     *
     * @param String msgCode
     * @return
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 18, 2012
     */
    private void getMsgCode(String msgCode) {
        if (msgCode != null) {
            if (msgCode.equals(IConstants.EXCHANGER_MSG_CODE.MSG_EXCHANGER_SUCCESS)) {
                model.setSuccessMessage(getText("nts.ams.fe.message.exchanger.MSG_NAB088"));
            }
        }
    }

    /**
     * validate form
     *
     * @param
     * @return
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 18, 2012
     */
    private void validateForm() {
        List<ExchangerSymbolInfo> listExchangerSymbol = model.getListExchangerSymbol();
        if (listExchangerSymbol != null && listExchangerSymbol.size() > 0) {
            for (ExchangerSymbolInfo info : listExchangerSymbol) {
                externalValidateForm(info.getSellRate(), info.getBuyRate());
            }
        }
    }

    /**
     * external validate form
     *
     * @param
     * @return
     * @throws
     * @author Nguyen.Manh.Thang
     * @CrDate Sep 19, 2012
     */
    private void externalValidateForm(String strSellRate, String strBuyRate) {

        BigDecimal sellRate = MathUtil.parseBigDecimal(strSellRate, null);
        BigDecimal buyRate = MathUtil.parseBigDecimal(strBuyRate, null);

        //check sell rate is empty or not
        String msg = "";
        if (strSellRate == null || StringUtils.isBlank(strSellRate)) {
            msg = getText("MSG_NAF001", new String[]{getText("nts.ams.fe.label.exchanger.sellRate")});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
            //check buy rate is empty or not
        } else if (strBuyRate == null || StringUtils.isBlank(strBuyRate)) {
            msg = getText("MSG_NAF001", new String[]{getText("nts.ams.fe.label.exchanger.buyRate")});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
        }

        if (sellRate == null || sellRate.doubleValue() < 0) {
            msg = getText("MSG_NAB053", new String[]{getText("nts.ams.fe.label.exchanger.sellRate")});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
        } else if (buyRate == null || sellRate.doubleValue() < 0) {
            msg = getText("MSG_NAB053", new String[]{getText("nts.ams.fe.label.exchanger.buyRate")});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
        }

        if (sellRate.doubleValue() >= 1000000) {
            msg = getText("MSG_NAB012", new String[]{getText("nts.ams.fe.label.exchanger.sellRate"), "999999.999999"});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
        }

        if (buyRate.doubleValue() >= 1000000) {
            msg = getText("MSG_NAB012", new String[]{getText("nts.ams.fe.label.exchanger.buyRate"), "999999.999999"});
            model.setErrorMessage(msg);
            addFieldError("errorMessage", msg);
            return;
        }
    }

    /**
     * Export excel file
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    public String exportCsv() {
        getListforExchangerScreen();
        try {
            generateFileName();
            String userId = "";
            CsvWriter csv = new CsvWriter(getFilePath(), ',', Charset.forName("UTF-8"));
            writeHeaders(csv);

            Map<String, String> mapStatusAll = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.STATUS_TRANSFER);

            int pageIndex = PagingInfo.DEFAULT_INDEX;


            while (true) {
                // Paging
                PagingInfo pagingInfo = model.getPagingInfo();
                pagingInfo.setIndexPage(pageIndex);
                @SuppressWarnings("rawtypes")
                Map session = ActionContext.getContext().getSession();
                ExchangerTransactionSearchCondition condition = (ExchangerTransactionSearchCondition) session.get("exchangerCondition");
                // Search results
                List<ExchangerTransactionSearchCondition> listExchangers = exchangerManager.getExchangerHistory(condition, pagingInfo);

                // Data
                if (listExchangers != null) {
                    for (ExchangerTransactionSearchCondition exchanger : listExchangers) {
                        writeRecord(csv, exchanger, mapStatusAll);
                    }
                }

                // Check next page
                if (pagingInfo.getIndexPage() < pagingInfo.getTotalPage()) {
                    pageIndex = pagingInfo.getIndexPage() + 1;
                } else {
                    break;
                }
            }
            csv.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    private void generateFileName() throws FileNotFoundException {
        Calendar c = Calendar.getInstance();
        String timetamp = DateUtil.toString(c.getTime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT);
        String timetampSecond = String.format("%02d%02d%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        setFileName("ExchangerApproveHistory_" + timetamp + "_" + timetampSecond + ".csv");
        Properties properties = Helpers.getProperties(CONFIGPATH);
        setFilePath(properties.getProperty("pathFileCsvOutput") + "/" + getFileName());
    }

    private void writeHeaders(CsvWriter csv) throws IOException {
        // ID
        csv.write("TransactionId");
        csv.write("CustomerId");
        csv.write("CustomerName");
        csv.write("Type");
        csv.write("Amount");
        csv.write("Currency");
        csv.write("Rate");
        csv.write("Status");
        csv.write("Remark");
        csv.write("Request Time");
        csv.write("Bank Name");
        csv.write("Bank address");
        csv.write("Swift code");
        csv.write("Branch name");
        csv.write("Account No");
        csv.write("Account Name");

        // End record
        csv.endRecord();
    }

    /**
     * Write data for each  row
     *
     * @param
     * @return
     * @throws
     * @author Tran.Duc.Nam
     * @CrDate Sep 20, 2012
     */
    private void writeRecord(CsvWriter csv, ExchangerTransactionSearchCondition record, Map<String, String> map) throws IOException {
        if (!StringUtil.isEmpty(record.getTransactionId())) {
            csv.write(record.getTransactionId());
        } else
            csv.write("");

        if (!StringUtil.isEmpty(record.getCustomerId())) {
            csv.write(record.getCustomerId());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getCustomerName())) {
            csv.write(record.getCustomerName());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getTypeStr())) {
            csv.write(record.getTypeStr());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getAmount())) {
            csv.write(record.getAmount());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getCurrencyCode())) {
            csv.write(record.getCurrencyCode());
        } else
            csv.write("");
        if (record.getRate() != null) {
            csv.write(record.getRate());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(getStatusString(map, record.getStatus().toString()))) {
            csv.write(getStatusString(map, record.getStatus().toString()));
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getRemark())) {
            csv.write(record.getRemark());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getRequestTime())) {
            csv.write(record.getRequestTime());
        } else
            csv.write("");

        if (!StringUtil.isEmpty(record.getBeneficiaryBankName())) {
            csv.write(record.getBeneficiaryBankName());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getBeneficiaryBankAddress())) {
            csv.write(record.getBeneficiaryBankAddress());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getBeneficiarySwiftCode())) {
            csv.write(record.getBeneficiarySwiftCode());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getBeneficiaryBranchName())) {
            csv.write(record.getBeneficiaryBranchName());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getBeneficiaryAccountNo())) {
            csv.write(record.getBeneficiaryAccountNo());
        } else
            csv.write("");
        if (!StringUtil.isEmpty(record.getBeneficiaryAccountName())) {
            csv.write(record.getBeneficiaryAccountName());
        } else
            csv.write("");
        // End record
        csv.endRecord();
    }

    public InputStream getCsvFile() {
        try {
            InputStream is = new FileInputStream(new File(getFilePath()));
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


    /**
     * ************************ setter - getter ****************************
     */

    public IExchangerManager getExchangerManager() {
        return exchangerManager;
    }

    public void setExchangerManager(IExchangerManager exchangerManager) {
        this.exchangerManager = exchangerManager;
    }

    public ExchangerModel getModel() {
        return model;
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

    /**
     * @param balanceManager the balanceManager to set
     */
    public void setBalanceManager(IBalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
