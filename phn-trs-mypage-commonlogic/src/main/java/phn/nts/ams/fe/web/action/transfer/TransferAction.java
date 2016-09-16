package phn.nts.ams.fe.web.action.transfer;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.model.TransferModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

public class TransferAction extends BaseSocialAction<TransferModel> {

    private static final long serialVersionUID = 7177815412233147074L;
    private static Logit log = Logit.getInstance(TransferAction.class);
    private List<String> messageList = new LinkedList<String>();
    private String msgCode;
    private String result;
    private TransferModel transferModel = new TransferModel();
    private IAccountManager accountManager;
    private ITransferManager transferManager;
//    private IBalanceManager balanceManager;
    private IWithdrawalManager withdrawalManager;
    private static Properties propsConfig;
    private static final String CONFIGPATH = "configs.properties";
    private static final String TEST_URL = "testbo.url";

    public void prepare() throws Exception {
    	super.prepare();
//    	if (this.transferModel.getMapServiceType() == null || this.transferModel.getMapServiceType().isEmpty()) {
    		log.info("[start]get list servicetype from system property");
            Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);

            Map<String, String> newMapServiceType = new ConcurrentHashMap<String, String>(mapServiceType);
            newMapServiceType = Collections.synchronizedMap(newMapServiceType);
            List<Integer> listServiceInfo = getTempListServiceType(mapServiceType.keySet());

            for (String key : newMapServiceType.keySet()) {
                Integer keyType = MathUtil.parseInteger(key);
                if (keyType != null) {
                    if (listServiceInfo.contains(keyType)) {
                        newMapServiceType.remove(key);
                    }
                }
            }

//            this.transferModel.setMapServiceType(new TreeMap<String, String>(newMapServiceType));
//            this.transferModel.setMapToServiceType(getMapToServiceType(IConstants.SERVICES_TYPE.AMS));

            log.info("[end]get list servicetype from system property");
//    	}
    }
    
    public TransferModel getModel() {
        return transferModel;
    }

    /**
     * @return the transferManager
     */
    public ITransferManager getTransferManager() {
        return transferManager;
    }

    /**
     * @param transferManager the transferManager to set
     */
    public void setTransferManager(ITransferManager transferManager) {
        this.transferManager = transferManager;
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

    public String index() {
    	log.info("[Start function] index - TransferAction: "+System.currentTimeMillis());
        try {
            if (result != null) {
                getMsgCode(result);
            }
            String transferFrom = httpRequest.getParameter("transferFrom");
            String transferTo = httpRequest.getParameter("transferTo");
            TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
            if (!StringUtil.isEmpty(transferFrom) && MathUtil.parseInteger(transferFrom) != null) {
                transferMoneyInfo.setTransferFrom(MathUtil.parseInteger(transferFrom));
            }
            if (!StringUtil.isEmpty(transferTo) && MathUtil.parseInteger(transferFrom) != null) {
                transferMoneyInfo.setTransferTo(MathUtil.parseInteger(transferTo));
                Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);
                transferMoneyInfo.setTransferToName(mapServiceType.get(StringUtil.toString(transferMoneyInfo.getTransferTo())));
                transferModel.setTransferMoneyInfo(transferMoneyInfo);
            }

            setRawUrl(IConstants.FrontEndActions.TRANSFER_INDEX);
            readCustomerInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[Finish function] index - TransferAction: "+System.currentTimeMillis());
        return SUCCESS;
    }

    private void readCustomerInfo() {
    	log.info("[Start function] readCustomerInfo - TransferAction: "+System.currentTimeMillis());
        try {
//            log.info("[start]get list servicetype from system property");
//            Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);
//
//
//            Map<String, String> newMapServiceType = new ConcurrentHashMap<String, String>(mapServiceType);
//            newMapServiceType = Collections.synchronizedMap(newMapServiceType);
//            List<Integer> listServiceInfo = getTempListServiceType(mapServiceType.keySet());
//
//            for (String key : newMapServiceType.keySet()) {
//                Integer keyType = MathUtil.parseInteger(key);
//                if (keyType != null) {
//                    if (listServiceInfo.contains(keyType)) {
//                        newMapServiceType.remove(key);
//                    }
                    
                    //[NTS1.0-le.hong.ha]May 6, 2013A - Start 
                    //temporary not use BO
                    //remove by master data instead of this code
                    // sysProperty key = "DepositServiceType", type = 4 -> activeFlag = 0
                    //if(keyType.equals(IConstants.SERVICES_TYPE.BO)){
                    //	newMapServiceType.remove(key);
                    //}
					//[NTS1.0-le.hong.ha]May 6, 2013A - End
//                }
//            }
//
//            transferModel.setMapServiceType(new TreeMap<String, String>(newMapServiceType));
//            transferModel.setMapToServiceType(getMapToServiceType(IConstants.SERVICES_TYPE.AMS));

            /*TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
               if(transferMoneyInfo == null){
                   transferMoneyInfo =  new TransferMoneyInfo();
               }
               Map<String, String> mapToServiceType = new HashMap<String, String>();
               if(IConstants.SERVICES_TYPE.AMS.equals(transferModel.getTransferFrom())) {//transferMoneyInfo.getTransferFrom()
                   for(String key : mapServiceType.keySet()) {
                       String value = mapServiceType.get(key);
                       if(!IConstants.SERVICES_TYPE.AMS.equals(MathUtil.parseInt(key, 0))) {
                           mapToServiceType.put(key, value);
                       }
                   }

                } else {
                   String value = mapServiceType.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS));
                   mapToServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), value);
               }
               transferModel.setMapToServiceType(mapToServiceType);*/
//            log.info("[end]get list servicetype from system property");

            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    String customerId = frontUserOnline.getUserId();
                    String currency = frontUserOnline.getCurrencyCode(); // for ams account
                    //String currencyCodeFX = "";
                    //String currencyCodeBO = "";
                    log.info("customerID" + customerId);
                    log.info("login ID " + frontUserOnline.getLoginId());
                    log.info("currency Code " + currency);
                    transferModel.setCurrencyCode(currency);
                    log.info("customer ID" + customerId);
                    log.info("login ID" + frontUserOnline.getLoginId());
                    CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, IConstants.SERVICES_TYPE.FX);
                    if (customerServiceInfo != null) {
                        transferModel.setToAccountCurrencyCode(customerServiceInfo.getCurrencyCode()); //default service type of destination account transfer
                        //currencyCodeFX = customerServiceInfo.getCurrencyCode();
                    }
                    //customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, IConstants.SERVICES_TYPE.BO);
                    //if (customerServiceInfo != null) {
                        //currencyCodeBO = customerServiceInfo.getCurrencyCode();
                    //}
                    
                    loadAccountBalanceInfo();
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[Finish function] readCustomerInfo - TransferAction: "+System.currentTimeMillis());
    }

    private void loadAccountBalanceInfo(){
    	log.info("[Start function] loadAccountBalanceInfo - TransferAction: "+System.currentTimeMillis());
    	FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
    	FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
    	String customerId = frontUserOnline.getUserId();
    	String currency = frontUserOnline.getCurrencyCode();
    	
    	// get balance of AMS
    	BalanceInfo balanceAmsInfo = transferModel.getBalanceAmsInfo();
    	if(balanceAmsInfo == null){
    		balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currency);
    		transferModel.setBalanceAmsInfo(balanceAmsInfo);
    	}
    	
    	// get balance of FX
    	BalanceInfo balanceFxInfo = transferModel.getBalanceFxInfo();
    	if(balanceFxInfo == null){
    		balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currency);
//    		balanceFxInfo.setRoundedMarginLevel(roundingMarginLevel(balanceFxInfo, currency));
    		transferModel.setBalanceFxInfo(balanceFxInfo);
    	} else {
//    		balanceFxInfo.setRoundedMarginLevel(roundingMarginLevel(balanceFxInfo, currency));
    	}
        
        // get balance of BO
    	BalanceInfo balanceBoInfo = transferModel.getBalanceBoInfo();
    	if(balanceBoInfo == null){
    		balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currency);
    		transferModel.setBalanceBoInfo(balanceBoInfo);
    	}
        
        // get balance of CopyTrade
    	BalanceInfo balanceCopyTradeInfo = transferModel.getBalanceScInfo();
    	if(balanceCopyTradeInfo == null){
    		balanceCopyTradeInfo = transferModel.getBalanceScInfo();
    		if(balanceCopyTradeInfo == null){
    			balanceCopyTradeInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currency);
    		}
//    		balanceCopyTradeInfo.setRoundedMarginLevel(roundingMarginLevel(balanceCopyTradeInfo, currency));
    		transferModel.setBalanceScInfo(balanceCopyTradeInfo);
        } else {
//        	balanceCopyTradeInfo.setRoundedMarginLevel(roundingMarginLevel(balanceCopyTradeInfo, currency));
        }       
    	log.info("[Finish function] loadAccountBalanceInfo - TransferAction: "+System.currentTimeMillis());
    }
    
//    private Double roundingMarginLevel(BalanceInfo balanceInfo, String currencyCode){
//    	log.info("[Start function] roundingMarginLevel");
//    	CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
//		String MT4Rounding = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.MT4_ROUNDING).get(ITrsConstants.SYS_PROPERTY.MT4_ROUNDING);
//		BigDecimal roundedMarginLevel = BigDecimal.ZERO;
//		if(currencyInfo != null && !StringUtil.isEmpty(MT4Rounding)) {
//			int scale = currencyInfo.getCurrencyDecimal();
//			int rounding = Integer.valueOf(MT4Rounding);
//			BigDecimal roundedEquity = MathUtil.rounding(balanceInfo.getEquity(), scale, rounding);
//			BigDecimal roundedMargin = MathUtil.rounding(balanceInfo.getMargin(), scale, rounding);
//			if(roundedMargin.compareTo(BigDecimal.ZERO) != 0){
//				roundedMarginLevel = roundedEquity.multiply(new BigDecimal(100)).divide(roundedMargin, 2, rounding);
//			}
//		}
//		log.info("[End function] roundingMarginLevel " + roundedMarginLevel);
//		return roundedMarginLevel.doubleValue();
//    }
    
    private void validateTransfer() {
    	log.info("[Start function] validateTransfer - TransferAction: "+System.currentTimeMillis());
        clearFieldErrors();
        try {
            String customerId = "";
            String currencyCode = "";
            BigDecimal amountAvailabaleTransfer = MathUtil.parseBigDecimal(0);
            BalanceInfo balanceAmsInfo = null;
            BalanceInfo balanceBoInfo = null;
            BalanceInfo balanceFxInfo = null;
            BalanceInfo balanceCopyTradeInfo = null;
            BigDecimal minTransferAmount = null;
            String configValue = "";
            String wlCode = "";
            
            Integer transferFrom = transferModel.getTransferMoneyInfo().getTransferFrom();
            Integer transferTo = transferModel.getTransferMoneyInfo().getTransferTo();
            if(transferFrom == null || transferTo == null){
            	addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.lacking_account"));
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.lacking_account"));
            	return;
            }
            if(transferFrom.equals(transferTo)){
            	addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.same_account"));
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.same_account"));
            	return;
            }
            //check tranferFlg on MasterData
            boolean checkAllowTransferFlg = transferManager.checkTransferFlagOnMasterData(ITrsConstants.CONFIG_KEY.ALLOW_TRANSFER_FLG, ITrsConstants.WL_CODE.TRS);
            if(!checkAllowTransferFlg) {
            	addFieldError("errorMessage", getText("MSG_NAB020"));
                transferModel.setErrorMessage(getText("MSG_NAB020"));
            	return;
            }
            
            Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);
            transferModel.getTransferMoneyInfo().setTransferToName(mapServiceType.get(StringUtil.toString(transferModel.getTransferMoneyInfo().getTransferTo())));
            //check customer can transfer or not
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    currencyCode = frontUserOnline.getCurrencyCode();
                    log.info("customer ID" + frontUserOnline.getUserId());
                    log.info("login ID" + frontUserOnline.getLoginId());
                    wlCode = frontUserOnline.getWlCode();
                    //check send money flag with fromAccount
                    CustomerServicesInfo customerServiceInfoFromAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), transferModel.getTransferMoneyInfo().getTransferFrom());
                    if (customerServiceInfoFromAccount != null) {
                        customerId = customerServiceInfoFromAccount.getCustomerId();
                        if (customerServiceInfoFromAccount.getAllowSendmoneyFlg() == null || customerServiceInfoFromAccount.getAllowSendmoneyFlg() == 0) { // not allow transfer
                            addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB030"));
                            transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB030"));
                            return;
                        }
                    } else {
                        //if from account is AMS
                        customerId = frontUserOnline.getUserId();
                    }

                    if (IConstants.SERVICES_TYPE.AMS.equals(transferModel.getTransferMoneyInfo().getTransferFrom())) {
                        // get balance of AMS
                    	balanceAmsInfo = transferModel.getBalanceAmsInfo();
                    	if(balanceAmsInfo == null){
                    		balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currencyCode);
                        }
                        if (balanceAmsInfo != null) {
                            CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceAmsInfo.getCurrencyCode());
                            if (currencyInfo != null) {
                                amountAvailabaleTransfer = MathUtil.rounding(balanceAmsInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
                            } else {
                                amountAvailabaleTransfer = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
                            }
//							amountAvailabaleTransfer = MathUtil.parseBigDecimal(balanceAmsInfo.getAmountAvailable());
                        } else {
                            addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            return;
                        }
                    } else if (IConstants.SERVICES_TYPE.BO.equals(transferModel.getTransferMoneyInfo().getTransferFrom())) {
                        // get balance of BO
                    	balanceBoInfo = transferModel.getBalanceBoInfo();
                    	if(balanceBoInfo == null){
                    		balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currencyCode);
                        }
                        if (balanceBoInfo != null) {
                            CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceBoInfo.getCurrencyCode());
                            if (currencyInfo != null) {
                                amountAvailabaleTransfer = MathUtil.rounding(balanceBoInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
                            } else {
                                amountAvailabaleTransfer = MathUtil.parseBigDecimal(balanceBoInfo.getAmountAvailable());
                            }

                        } else {
                            addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            return;

                        }
                    } else if (IConstants.SERVICES_TYPE.FX.equals(transferModel.getTransferMoneyInfo().getTransferFrom())) {
                        // get balance of FX
                    	balanceFxInfo = transferModel.getBalanceFxInfo();
                    	if(balanceFxInfo == null){
                    		balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currencyCode);
                        }
                        if (balanceFxInfo != null) {
                            CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceFxInfo.getCurrencyCode());
                            if (currencyInfo != null) {
                                amountAvailabaleTransfer = MathUtil.rounding(balanceFxInfo.getAmountAvailable() == null ? 0D : balanceFxInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
                            } else {
                                amountAvailabaleTransfer = MathUtil.parseBigDecimal(balanceFxInfo.getAmountAvailable());
                            }


                        } else {
                            addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            return;
                        }
                    } else if (IConstants.SERVICES_TYPE.COPY_TRADE.equals(transferModel.getTransferMoneyInfo().getTransferFrom())) {
                    	balanceCopyTradeInfo = transferModel.getBalanceScInfo();
                    	if(balanceCopyTradeInfo == null){
                    		balanceCopyTradeInfo = transferModel.getBalanceScInfo();
                    		if(balanceCopyTradeInfo == null){
                        		balanceCopyTradeInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currencyCode);
                        	}
                    	}
                    	                        
                        if (balanceCopyTradeInfo != null) {
                            CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceCopyTradeInfo.getCurrencyCode());
                            if (currencyInfo != null) {
                                amountAvailabaleTransfer = MathUtil.rounding(balanceCopyTradeInfo.getAmountAvailable() == null ? 0D : balanceCopyTradeInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
                            } else {
                                amountAvailabaleTransfer = MathUtil.parseBigDecimal(balanceCopyTradeInfo.getAmountAvailable());
                            }
                        } else {
                            addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.cannot.get.balance"));
                            return;
                        }
                    }
                }
            }
            //required amount display message MSG_NAB058
            String transferAmountStr = transferModel.getAmount().trim().replaceAll("[,]", "");
            BigDecimal amount = MathUtil.parseBigDecimal(transferAmountStr, null);
            if (StringUtil.isEmpty(transferAmountStr)) {
                List<String> listContent = new ArrayList<String>();
                listContent.add(getText("nts.ams.fe.transfer.label.amount"));
                transferModel.setErrorMessage(getText("MSG_NAF001", listContent));
                addFieldError("errorMessage", getText("MSG_NAF001", listContent));
                return;
            }
            if (transferAmountStr.length() > ITrsConstants.MAX_LENGTH_TRANSFER_AMOUNT) {
                List<String> listContent = new ArrayList<String>();
                listContent.add(getText("nts.ams.fe.transfer.label.amount"));
                listContent.add(ITrsConstants.MAX_LENGTH_TRANSFER_AMOUNT.toString());
                transferModel.setErrorMessage(getText("MSG_NAB008", listContent));
                addFieldError("errorMessage", getText("MSG_NAB008", listContent));
                return;
            }
            if (amount == null) {
                addFieldError("errorMessage", getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));
                transferModel.setErrorMessage(getText("nts.ams.fe.error.message.amount.required.MSG_NAB057"));
                return;
            }/* else if (amount.compareTo(MathUtil.parseBigDecimal(0)) <= 0) {
                addFieldError("errorMessage", getText("MSG_NAB095"));
                transferModel.setErrorMessage(getText("MSG_NAB095"));
                return;
            }*/
            
            //validate with min transfer amount
            Map<String, String> mapWlConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
            configValue = mapWlConfig.get(transferModel.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_TRANSFER_AMOUNT);
            minTransferAmount = MathUtil.parseBigDecimal(configValue);
            
            String fromCurrencyCode = transferModel.getCurrencyCode();
            String toCurrencyCode = transferModel.getToAccountCurrencyCode();

            
            if(IConstants.CURRENCY_CODE.JPY.equalsIgnoreCase(toCurrencyCode)){
            	amount = amount.divide(MathUtil.parseBigDecimal(1), 0, RoundingMode.HALF_UP);
            }else{
            	amount = amount.divide(MathUtil.parseBigDecimal(1), 2, RoundingMode.HALF_UP);
            }
            
            transferModel.setAmount(String.valueOf(amount));
            BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
            BigDecimal convertRate = balanceManager.getConvertRateOnFrontRate(fromCurrencyCode , toCurrencyCode, IConstants.FRONT_OTHER.SCALE_ALL);
            if(convertRate == null || convertRate.equals(BigDecimal.ZERO)){
            	addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.convert_rate_not_available"));
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.convert_rate_not_available"));
                return;
            }
            CurrencyInfo fromCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + fromCurrencyCode);
            convertedAmount = amount.divide(convertRate, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
			transferModel.setConvertedAmount(String.valueOf(convertedAmount));
			
            
           
           /* if (transferModel.getConvertedAmount() != null && transferModel.getConvertedAmount() != "") {
                convertedAmount = MathUtil.parseBigDecimal(transferModel.getConvertedAmount());
            }*/
            log.info("start compare converted amount transfer with MIN_TRANSFER_AMOUNT value=" + minTransferAmount.doubleValue() + "with currency code =" + transferModel.getCurrencyCode());
            //if(convertedAmount.compareTo(minTransferAmount) < 0){
            
          
            if ((fromCurrencyCode.equalsIgnoreCase(transferModel.getToAccountCurrencyCode()) && amount.compareTo(minTransferAmount) < 0)
                    || (!fromCurrencyCode.equalsIgnoreCase(transferModel.getToAccountCurrencyCode()) && convertedAmount.compareTo(minTransferAmount) < 0)) {
                List<String> listContent = new ArrayList<String>();
                listContent.add(StringUtil.toString(minTransferAmount));
                transferModel.setErrorMessage(getText("MSG_NAB095", listContent));
                addFieldError("errorMessage", getText("MSG_NAB095", listContent));
                log.info("amount = " + convertedAmount.doubleValue() + " is smaller than min transfer amount=" + minTransferAmount.doubleValue());
                return;

            }
            log.info("start compare converted amount transfer with MIN_TRANSFER_AMOUNT value=" + minTransferAmount.doubleValue() + "with currency code =" + transferModel.getCurrencyCode());
            //recheck amount available withdrawl display MSG_NAB029
            /*if(amount.compareTo(amountAvailabaleTransfer) > 0){
                   addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB029"));
                   transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB029"));
                   return;
               }*/
            log.info("start compare Converted Amount=" + convertedAmount.doubleValue() + " with Amount Available Transfer=" + amountAvailabaleTransfer);
            if (convertedAmount.compareTo(amountAvailabaleTransfer) > 0) {
                addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB029"));
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB029"));
                log.info("Converted Amount= " + convertedAmount.doubleValue() + " is larger than Amount Available Transfer=" + amountAvailabaleTransfer);
                return;
            }
            log.info("end compare Converted Amount=" + convertedAmount.doubleValue() + " with Amount Available Transfer=" + amountAvailabaleTransfer);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("[Finish function] validateTransfer - TransferAction: "+System.currentTimeMillis());

    }

    public String transferConfirmed() {
    	log.info("[Start function] transferConfirmed - TransferAction : "+System.currentTimeMillis());
        setRawUrl(IConstants.FrontEndActions.TRANSFER_CONFIRMED);
        try {
            readCustomerInfo();
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
            if (transferMoneyInfo == null) {
                transferMoneyInfo = new TransferMoneyInfo();
            }
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    getCurrencyCodeOfFromAndToAccount(frontUserOnline, transferMoneyInfo.getTransferFrom(), transferMoneyInfo.getTransferTo());
                    transferMoneyInfo.setCustomerId(frontUserOnline.getUserId());
                }
            }
            validateTransfer();
            //[NTS1.0-Quan.Le.Minh]Jan 25, 2013A - Start
            if (!isAllowTransferMoneyByAccountStatus()) {
                return ERROR;
            }
            //[NTS1.0-Quan.Le.Minh]Jan 25, 2013A - End
            if (messageList != null && messageList.size() > 0) {
                setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
                return ERROR;
            }
            if (hasFieldErrors()) {
                setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
                return ERROR;
            }
            Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);
            String transferAmountStr = transferModel.getAmount().trim().replaceAll("[,]", "");
            transferMoneyInfo.setTransferMoney(MathUtil.parseDouble(transferAmountStr));
            transferMoneyInfo.setTransferFromName(mapServiceType.get(StringUtil.toString(transferMoneyInfo.getTransferFrom())));
            transferMoneyInfo.setTransferToName(mapServiceType.get(StringUtil.toString(transferMoneyInfo.getTransferTo())));
            transferMoneyInfo.setConvertedAmount(MathUtil.parseDouble(transferModel.getConvertedAmount()));
            transferMoneyInfo.setToCurrencyCode(transferModel.getToAccountCurrencyCode());
            transferMoneyInfo.setFromCurrencyCode(transferModel.getCurrencyCode());
            
            transferModel.setTransferMoneyInfo(transferMoneyInfo);
            
//          Integer fromServiceType = transferMoneyInfo.getTransferFrom();
//          Integer toServiceType = transferMoneyInfo.getTransferTo();
//          if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType) || IConstants.SERVICES_TYPE.FX.equals(toServiceType)){
//            	transferManager.loadAdditionalFxData(transferModel);
//          }

            log.info("[Finish function] transferConfirmed - TransferAction : "+System.currentTimeMillis());
            return INPUT;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR;
        }
    }

    public String transferSubmit() {
        // reload map
    	log.info("[Start function] transferSubmit - TransferAction : "+System.currentTimeMillis());
        readCustomerInfo();
        FrontUserDetails userDetail = FrontUserOnlineContext.getFrontUserOnline();
        TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
        if (transferMoneyInfo == null) {
            transferMoneyInfo = new TransferMoneyInfo();
        }
        if (userDetail != null) {
            FrontUserOnline frontUserOnline = userDetail.getFrontUserOnline();
            if (frontUserOnline != null) {
                getCurrencyCodeOfFromAndToAccount(frontUserOnline, transferMoneyInfo.getTransferFrom(), transferMoneyInfo.getTransferTo());
            }
        }
        //check send money flag with fromAccount
        if (userDetail != null) {
            FrontUserOnline frontUserOnline = userDetail.getFrontUserOnline();
            if (frontUserOnline != null) {
                log.info("customer ID" + frontUserOnline.getUserId());
                log.info("login ID" + frontUserOnline.getLoginId());
                //check send money flag with fromAccount
                CustomerServicesInfo customerServiceInfoFromAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), transferMoneyInfo.getTransferFrom());
                if (customerServiceInfoFromAccount != null) {
                    if (customerServiceInfoFromAccount.getAllowSendmoneyFlg() == null || customerServiceInfoFromAccount.getAllowSendmoneyFlg().intValue() == 0) { // not allow transfer
                        addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB030"));
                        transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB030"));
                        return ERROR;
                    }
                }
            }
        }
        
        validateTransfer();
        if (hasFieldErrors()) {
            return ERROR;
        }

        log.info("[START] sending transfer request to server");
        try {
            //TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
//			readCustomerInfo();			
            Integer result = IConstants.TRANSFER_STATUS.FAIL;
            /*if (transferMoneyInfo == null) {
                transferModel.setErrorMessage("");
                setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
                log.error("Transfer Money Info is null");
                return ERROR;
            }*/
            if (!IConstants.SERVICES_TYPE.AMS.equals(transferMoneyInfo.getTransferTo())) {
                if (!isAllowSendingMoney(transferMoneyInfo.getTransferTo())) {
                    transferModel.setErrorMessage(getText("MSG_NAB091_TRANFER"));

                    setMsgCode(ITrsConstants.MSG_NAB091_TRANFER);

                    return ERROR;
                }
            }
            
            if (!isAllowTransferMoneyByAccountStatus()) {
                return ERROR;
            }
            //String amount = transferModel.getAmount().trim();
            //String convertedAmount = transferModel.getConvertedAmount();

            //transferMoneyInfo.setTransferMoney(MathUtil.parseDouble(transferModel.formatNumber(MathUtil.parseBigDecimal(amount), transferMoneyInfo.getToCurrencyCode())));
            //transferMoneyInfo.setConvertedAmount(MathUtil.parseDouble(transferModel.formatNumber(MathUtil.parseBigDecimal(convertedAmount), transferMoneyInfo.getFromCurrencyCode())));
            String transferAmountStr = transferModel.getAmount().trim().replaceAll("[,]", "");
            transferMoneyInfo.setTransferMoney(MathUtil.parseDouble(transferAmountStr));
            transferMoneyInfo.setConvertedAmount(MathUtil.parseDouble(transferModel.getConvertedAmount()));
            log.info("amount for transfer " + transferMoneyInfo.getTransferMoney() + ", convertedAmount " + transferMoneyInfo.getConvertedAmount());
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    log.info("customerID" + frontUserOnline.getUserId() + "loginID=" + frontUserOnline.getLoginId() + "currencyCode= " + frontUserOnline.getCurrencyCode() + "wlCode=" + frontUserOnline.getWlCode());
                    transferMoneyInfo.setCustomerId(frontUserOnline.getUserId());
                    transferMoneyInfo.setWlCode(frontUserOnline.getWlCode());
                    
                    //Set from/to service info
                    for(CustomerServicesInfo customerServicesInfo : frontUserOnline.getListCustomerServiceInfo()) {
        				if(customerServicesInfo.getServiceType() == transferMoneyInfo.getTransferFrom())
        					transferMoneyInfo.setFromServicesInfo(customerServicesInfo);
        				else if(customerServicesInfo.getServiceType() == transferMoneyInfo.getTransferTo())
        					transferMoneyInfo.setToServicesInfo(customerServicesInfo);
        			}
                    
                    result = transferManager.transferMoney(transferMoneyInfo, frontUserOnline.getCurrencyCode());
                }
            }
            //if success display MSG_NAB014 The transaction of transfer is executed successfully
            if (IConstants.TRANSFER_STATUS.SUCCESS.equals(result)) {
            	if (frontUserDetails != null) {
            		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            		if (frontUserOnline != null) {
            			//Dissolve status akazan of customer
            			Integer resultMt4 = transferManager.changeDissolveStatusAkazan(ITrsConstants.AKAZAN_STATUS.RE, frontUserOnline.getUserId());
            			if(IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(resultMt4)) {
            				log.info("Change MT4.Accounts.Status = 'RE' of Account = "  + frontUserOnline.getUserId() + " is SUCCESS");
            				//send email
            				transferManager.sendMailDissolveStatusAkazan(frontUserOnline.getUserId(),ITrsConstants.SERVICES_TYPE.FX);
            				transferManager.sendMailDissolveStatusAkazan(frontUserOnline.getUserId(),ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
            			} else {
            				log.info("Change MT4.Accounts.Status = 'RE' of Account = "  + frontUserOnline.getUserId() + " is FAIL or this account NOT is akazan");
            			}
            			
            		}
            	}
                setMsgCode(IConstants.TRANSFER_MSG_CODE.MSG_NAB014);
            } else if (IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE.equals(result)) { //cannot get rate
                setMsgCode(IConstants.TRANSFER_MSG_CODE.MSG_NAB066);
            } else if (IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) { //warning can not transfer
                setMsgCode(IConstants.TRANSFER_MSG_CODE.MSG_NAB029);
            } else {//if fail display MSG_NAB015 Transaction failed. Please contact to system admin
                setMsgCode(IConstants.TRANSFER_MSG_CODE.MSG_NAB015);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR;
        }
        log.info("[Finish function] transferSubmit - TransferAction : "+System.currentTimeMillis());
        return SUCCESS;
    }

    /**
     * @return the messageList
     */
    public List<String> getMessageList() {
        return messageList;
    }

    /**
     * @param messageList the messageList to set
     */
    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }


    /**
     * @param msgCode the msgCode to set
     */
    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    private void getMsgCode(String msgCode) {
        if (msgCode != null) {
            if (msgCode.equals(IConstants.TRANSFER_MSG_CODE.MSG_NAB014)) {
                transferModel.setSuccessMessage(getText("nts.ams.fe.transfer.message.MSG_NAB014"));
            } else if (msgCode.equals(IConstants.TRANSFER_MSG_CODE.MSG_NAB015)) {
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB015"));
            } else if (msgCode.equals(IConstants.TRANSFER_MSG_CODE.MSG_NAB066)) {
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB066"));
            } else if (msgCode.equals(IConstants.TRANSFER_MSG_CODE.MSG_NAB029)) {
                transferModel.setErrorMessage(getText("nts.ams.fe.transfer.message.MSG_NAB029"));
            } else if (msgCode.equals(IConstants.TRANSFER_MSG_CODE.MSG_NAB091)) {
//				addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB091", transferModel.getListContents()));
                transferModel.setErrorMessage(getText("MSG_NAB091"));
            }else if (msgCode.equals(ITrsConstants.MSG_NAB091_TRANFER)) {
//				addFieldError("errorMessage", getText("nts.ams.fe.transfer.message.MSG_NAB091", transferModel.getListContents()));
                transferModel.setErrorMessage(getText("MSG_NAB091_TRANFER"));
            }
        }
    }

    /**
     * @return the msgCode
     */
    public String getMsgCode() {
        return msgCode;
    }

    public String reloadMapToServiceType() {
    	log.info("[Start function] reloadMapToServiceType - TransferAction: "+System.currentTimeMillis());
        String method = httpRequest.getParameter("method");
        Integer medthodSelected = Integer.parseInt(method);
//        transferModel.setMapToServiceType(getMapToServiceType(medthodSelected));
        log.info("[Finish function] reloadMapToServiceType - TransferAction: "+System.currentTimeMillis());
        return SUCCESS;
    }

    /**
     * get map serviceType of To account
     *
     * @param
     * @return
     * @auth Mai.Thu.Huyen
     * @CrDate Sep 22, 2012
     * @MdDate
     */
    private Map<String, String> getMapToServiceType(Integer fromServiceType) {
    	log.info("[Start function] getMapToServiceType - TransferAction: "+System.currentTimeMillis());
        Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
        Map<String, String> mapServiceType = new LinkedHashMap<String, String>();

        if (IConstants.SERVICES_TYPE.AMS.equals(fromServiceType)) {
            for (Entry<String, String> entryServiceType : mapServiceTypeName.entrySet()) {
                if (IConstants.SERVICES_TYPE.AMS.toString().equals(entryServiceType.getKey())) {
                    continue;
                }
                
                //[NTS1.0-le.hong.ha]May 6, 2013A - Start 
                //Temporary not use BO
                // sysProperty key = "ServiceType", type = 4 -> activeFlag = 0
                //if(IConstants.SERVICES_TYPE.BO.toString().equals(entryServiceType.getKey())){
                //	continue;
                //}
				//[NTS1.0-le.hong.ha]May 6, 2013A - End
                
                if (isAllowSendingMoney(MathUtil.parseInteger(entryServiceType.getKey()))) {
                    mapServiceType.put(entryServiceType.getKey(), entryServiceType.getValue());
                }
            }
        } else {
            for (Entry<String, String> entryServiceType : mapServiceTypeName.entrySet()) {
                if (fromServiceType.toString().equals(entryServiceType.getKey())) {
                    continue;
                }
                
                if (!IConstants.SERVICES_TYPE.AMS.toString().equals(entryServiceType.getKey())
                        && !isAllowSendingMoney(MathUtil.parseInteger(entryServiceType.getKey()))) {
                    continue;
                }
                
                //[NTS1.0-le.hong.ha]May 6, 2013A - Start 
                //Temporary not use BO
                // sysProperty key = "ServiceType", type = 4 -> activeFlag = 0
                //if(IConstants.SERVICES_TYPE.BO.toString().equals(entryServiceType.getKey())){
                //	continue;
                //}
				//[NTS1.0-le.hong.ha]May 6, 2013A - End
                
                mapServiceType.put(entryServiceType.getKey(), entryServiceType.getValue());
            }
        }
        /*
          if(IConstants.SERVICES_TYPE.AMS.equals(fromServiceType)){
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.FX)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
              }
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.BO)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
              }
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.COPY_TRADE)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
              }
          }else if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType)) {
              mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));

              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.BO)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
              }
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.COPY_TRADE)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
              }
          }else if(IConstants.SERVICES_TYPE.BO.equals(fromServiceType)){
              mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.FX)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
              }
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.COPY_TRADE)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
              }
          }else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromServiceType)){
              mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.FX)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
              }
              if(isAllowSendingMoney(IConstants.SERVICES_TYPE.BO)){
                  mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
              }
          }*/
        log.info("[Finish function] getMapToServiceType - TransferAction  : "+System.currentTimeMillis());
        return mapServiceType;
    }

    private void getCurrencyCodeOfFromAndToAccount(FrontUserOnline frontUserOnline, Integer serviceTypeId, Integer accountServiceTypeId) {
    	log.info("[Start function] getCurrencyCodeOfFromAndToAccount : "+System.currentTimeMillis());
    	try {
            String customerId = frontUserOnline.getUserId();
            CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, serviceTypeId);
            if (customerServiceInfo != null) {
                transferModel.setCurrencyCode(customerServiceInfo.getCurrencyCode());
            } else {
                transferModel.setCurrencyCode(frontUserOnline.getCurrencyCode());
            }
            customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, accountServiceTypeId);
            if (customerServiceInfo != null) {
                transferModel.setToAccountCurrencyCode(customerServiceInfo.getCurrencyCode());
            } else {
                transferModel.setToAccountCurrencyCode(frontUserOnline.getCurrencyCode());
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    	log.info("[Finish function] getCurrencyCodeOfFromAndToAccount  - TransferAction: "+System.currentTimeMillis());
    }

    /**
     * 　check allowSendingFlg in CustomerServiceInfo
     *
     * @param
     * @return
     * @auth Mai.Thu.Huyen
     * @CrDate Sep 22, 2012
     * @MdDate
     */
    private boolean isAllowSendingMoney(Integer serviceType) {
    	log.info("[Start function] isAllowSendingMoney  - TransferAction : "+System.currentTimeMillis());
        boolean flg = true;
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                CustomerServicesInfo customerServiceInfoToAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), serviceType);
                if (customerServiceInfoToAccount != null) {
                    if (IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER.equals(customerServiceInfoToAccount.getCustomerServiceStatus()) || IConstants.CUSTOMER_SERVIVES_STATUS.CANCEL.equals(customerServiceInfoToAccount.getCustomerServiceStatus())) {
                        flg = false;
                    }
                }
            }
        }
        log.info("[Finish function] isAllowSendingMoney  - TransferAction : "+System.currentTimeMillis());
        return flg;
    }


    /**
     * Check account status
     *
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 25, 2013
     */
    private boolean isAllowTransferMoneyByAccountStatus() {
    	log.info("[Start function] isAllowTransferMoneyByAccountStatus  - TransferAction : "+System.currentTimeMillis());
        boolean flg = false;
        try {
			propsConfig = Helpers.getProperties(CONFIGPATH);
		} catch (FileNotFoundException e) {
			log.error("cannot read config.properties");
		} 
		String testUrl = propsConfig.getProperty(TEST_URL);
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
                Integer serviceType = transferMoneyInfo.getTransferTo();
                if (!IConstants.SERVICES_TYPE.AMS.equals(serviceType)) {
                    CustomerServicesInfo customerServiceInfoToAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), serviceType);
                    if (customerServiceInfoToAccount != null) {
                        Integer serviceStatus = customerServiceInfoToAccount.getCustomerServiceStatus();
                        if (!IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(serviceStatus) &&
                                !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(serviceStatus) &&
                                !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(serviceStatus)) {
                        	
                        	if(ITrsConstants.ACCOUNT_OPEN_STATUS.WAITING_ADD_ACCOUNT == serviceStatus.intValue()){
                        		if(!transferManager.validateBoCustomerStatus(frontUserOnline.getUserId())){
                        			transferModel.setErrorMessage(getText("nts.ams.fe.error.message.MSG_TRS_NAF_0069",new String[]{testUrl}));
                        			addFieldError("errorMessage", getText("nts.ams.fe.error.message.MSG_TRS_NAF_0069", new String[]{testUrl}));
                        		}	else {
                        			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
                        			List<Object> listContent = new ArrayList<Object>();
    	                            listContent.add(mapServiceType.get(StringUtil.toString(serviceType)));
    	                            transferModel.setErrorMessage(getText("MSG_NAB091_TRANFER"));
    	                            addFieldError("errorMessage", getText("MSG_NAB091_TRANFER"));
                        		}
                        	}else{
	                            Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
	                            List<Object> listContent = new ArrayList<Object>();
	                            listContent.add(mapServiceType.get(StringUtil.toString(serviceType)));
	                            transferModel.setErrorMessage(getText("MSG_NAB091_TRANFER"));
	                            addFieldError("errorMessage", getText("MSG_NAB091_TRANFER"));
	                            flg = false;
                        	}
                        } else {
                            flg = true;
                        }
                    }
                } else {
                	//[NTS1.0-le.hong.ha]May 7, 2013A - Start 
                	// Check accountOpenStatus for AMS
                	CustomerInfo customerInfo = accountManager.getCustomerInfo(frontUserOnline.getUserId());
                	Integer accountOpenStatus = customerInfo.getAccountOpenStatus();
                	if (ITrsConstants.OPEN_STATUS.NOT_ACTIVE == accountOpenStatus) {
                        Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
                        List<Object> listContent = new ArrayList<Object>();
                        listContent.add(mapServiceType.get(StringUtil.toString(serviceType)));
                        transferModel.setErrorMessage(getText("MSG_NAB091_TRANFER"));
                        addFieldError("errorMessage", getText("MSG_NAB091_TRANFER"));
                        flg = false;
                    } else {
                        flg = true;
                    }
					//[NTS1.0-le.hong.ha]May 7, 2013A - End
                }
            }
        }
        log.info("[Finish function] isAllowTransferMoneyByAccountStatus  - TransferAction : "+System.currentTimeMillis());
        return flg;
    }

    /**
     *
     *
     * @param
     * @return
     * @auth Mai.Thu.Huyen
     * @CrDate Sep 22, 2012
     * @MdDate
     */
    private List<Integer> getTempListServiceType(Set<String> listServiceTypeIn) {
    	log.info("[Start function] getTempListServiceType  - TransferAction : "+System.currentTimeMillis());
        List<Integer> listIn = new ArrayList<Integer>();
        for (String s : listServiceTypeIn) {
            Integer key = MathUtil.parseInteger(s);
            if (key != null) {
                listIn.add(key);
            }
        }
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        List<CustomerServicesInfo> listInfo = null;
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                listInfo = accountManager.getListCustomerServiceByServiceTypes(frontUserOnline.getUserId(), listIn);
            }
        }
        List<Integer> listServiceType = null;
        if (listInfo != null) {
            listServiceType = new ArrayList<Integer>();
            for (CustomerServicesInfo info : listInfo) {
                listServiceType.add(info.getServiceType());
            }
        }
        log.info("[Finsh function] getTempListServiceType  - TransferAction : "+System.currentTimeMillis());
        return listServiceType;
    }
}
