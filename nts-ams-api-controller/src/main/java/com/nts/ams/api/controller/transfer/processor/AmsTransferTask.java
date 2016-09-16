package com.nts.ams.api.controller.transfer.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.com.trs.util.enums.AmsTransferStatus;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.model.TransferModel;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsTransferRequestWraper;
import com.nts.common.Constant.AllowSendMoneyFlag;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsTranferMoneyInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsWithdrawal Task
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransferTask implements Runnable {
	private Logit log = Logit.getInstance(AmsTransferTask.class);
	private AmsTransferRequestWraper wraper;
	private TransferModel transferModel = new TransferModel();
	
	private IBalanceManager balanceManager;
	private IAccountManager accountManager = null;
    private ITransferManager transferManager;
    private IProfileManager profileManager = null;
    private ExecutorService executorService;
    
	public AmsTransferTask(AmsTransferRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsTransferRequest request = wraper.getRequest();
			log.info("[start] handle AmsTransferRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			//Prepare model data
			AmsTranferMoneyInfo transferInfo = request.getTransferInfo();
			transferModel.setCurrentCustomerId(transferInfo.getCustomerId());
			transferModel.setWlCode(transferInfo.getWlCode());
			transferModel.setCurrencyCode(transferInfo.getCurrencyCode());
			transferModel.setToAccountCurrencyCode(transferInfo.getDestinationCurrencyCode());
			transferModel.setAmount(transferInfo.getTranferMoney());
			
			//Prepare transferMoneyInfo
			TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
			transferMoneyInfo.setTransferFrom(transferInfo.getTranferFrom().getNumber());
			transferMoneyInfo.setTransferTo(transferInfo.getTranferTo().getNumber());
			transferMoneyInfo.setCustomerId(transferInfo.getCustomerId());
			transferMoneyInfo.setWlCode(transferInfo.getWlCode());
			transferMoneyInfo.setCurrencyCode(transferInfo.getCurrencyCode());
			transferMoneyInfo.setFromCurrencyCode(transferInfo.getDestinationCurrencyCode());
			transferMoneyInfo.setToCurrencyCode(transferInfo.getDestinationCurrencyCode());
			//[TRSGAP-1400-quyen.le.manh]Jul 28, 2016A - Start - Add Remark for Transfer flow
			if(transferInfo.hasRemark())
				transferMoneyInfo.setRemark(transferInfo.getRemark());
			//[TRSGAP-1400-quyen.le.manh]Jul 28, 2016A - End
			transferModel.setTransferMoneyInfo(transferMoneyInfo);
			
			log.info("TransferMoneyInfo: " + transferMoneyInfo);
			log.info("TransferModel: " + transferModel);
			
			//Handle
			Result result = handleTransferInfo(transferModel);
			
			//Response to client
			RpcMessage response = createRpcMessage(request.getTransferInfo(), result, transferModel.getErrorMessage());
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response);
			
			String errCode = transferModel.getErrorMessage();
			log.info("[end] handle AmsTransferRequest, requestId: " + wraper.getResponseBuilder().getId() 
					+ ", ErrorMessage: " + errCode + " - " + AmsApiControllerMng.getMsg(errCode));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsTransferProcessor().onComplete(wraper);
		}
	}
	
	/**
	 * Handle TransferInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 14, 2015
	 * @MdDate
	 */
	public Result handleTransferInfo(TransferModel transferModel) {
    	final String customerId = transferModel.getCurrentCustomerId();
        
    	//Check validate
        if(!validateTransfer(transferModel)) {
        	return Result.FAILED;
        }
        
        try {
        	//Start transfer
            log.info("[start] transfer money");
            
            TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
            transferMoneyInfo.setTransferMoney(MathUtil.parseDouble( transferModel.getAmount()));
            transferMoneyInfo.setConvertedAmount(MathUtil.parseDouble(transferModel.getConvertedAmount()));
            log.info("Amount for transfer: " + transferMoneyInfo.getTransferMoney() + ", convertedAmount: " + transferMoneyInfo.getConvertedAmount());
            
            Integer transferResult = IConstants.TRANSFER_STATUS.FAIL;
            if(ServiceType.AMS_VALUE == transferMoneyInfo.getTransferTo().intValue() || ServiceType.AMS_VALUE == transferMoneyInfo.getTransferFrom().intValue()) {
            	//Transfer direct with AMS
            	transferResult = getTransferManager().registerTransferMoney(transferMoneyInfo, transferMoneyInfo.getCurrencyCode());
            	if(transferResult == IConstants.TRANSFER_STATUS.SUCCESS) {
            		transferResult = getTransferManager().transferMoney(transferMoneyInfo);
            	}
            } else {
            	log.info("Transfer from " + transferMoneyInfo.getTransferFrom() + " to " + transferMoneyInfo.getTransferTo() + ": must use Ams");
            	
            	//Transfer from Source Account to AMS
            	TransferMoneyInfo transferToAms = (TransferMoneyInfo) ObjectCopy.copy(transferMoneyInfo);
            	transferToAms.setToServicesInfo(transferToAms.getAmsServicesInfo());
            	transferToAms.setToCurrencyCode(transferToAms.getCurrencyCode());
            	transferToAms.setTransferTo(ServiceType.AMS_VALUE);
            	
            	log.info("[start] transfer from Source to Ams, TransferMoneyInfo: " + transferToAms);
            	transferResult = getTransferManager().registerTransferMoney(transferToAms, transferToAms.getCurrencyCode());
            	if(transferResult == IConstants.TRANSFER_STATUS.SUCCESS) {
            		transferResult = getTransferManager().transferMoney(transferToAms);
            	}
            	log.info("[end] transfer from Source to Ams, transferResult: " + transferResult + " - " + AmsTransferStatus.valueOf(transferResult));
            	
            	if(transferResult == IConstants.TRANSFER_STATUS.SUCCESS) {
            		TransferMoneyInfo transferToDesc = (TransferMoneyInfo) ObjectCopy.copy(transferMoneyInfo);
            		transferToDesc.setFromServicesInfo(transferToDesc.getAmsServicesInfo());
            		transferToDesc.setFromCurrencyCode(transferToDesc.getCurrencyCode());
            		transferToDesc.setTransferFrom(ServiceType.AMS_VALUE);
                	
            		log.info("[start] transfer from Ams to Destination, TransferMoneyInfo: " + transferToDesc);
	            	//Transfer from AMS to Destination Account
            		Integer transferResult2 = getTransferManager().registerTransferMoney(transferToDesc, transferToDesc.getCurrencyCode());
                	if(transferResult2 == IConstants.TRANSFER_STATUS.SUCCESS) {
                		transferResult2 = getTransferManager().transferMoney(transferToDesc);
                	}
                	
	            	log.info("[end] transfer from Ams to Destination, transferResult: " + transferResult2 + " - " + AmsTransferStatus.valueOf(transferResult2));
	            	
	            	if(!IConstants.TRANSFER_STATUS.SUCCESS.equals(transferResult2))
	            		transferResult = IConstants.TRANSFER_STATUS.SUCCESS_APART;
            	}
            }
            
            log.info("[end] transfer money, result: " + transferResult + " - " + AmsTransferStatus.valueOf(transferResult));
            
            //if success display MSG_NAB014 The transaction of transfer is executed successfully
            if (IConstants.TRANSFER_STATUS.SUCCESS.equals(transferResult)) {
            	log.info("Transfer SUCCESS, checking Akazan status");
            	
            	//Dissolve status akazan of customer
    			int resultDissolve = getTransferManager().changeDissolveStatusAkazan(ITrsConstants.AKAZAN_STATUS.RE, customerId);
  
    			//[TRSGAP-1207-quyen.le.manh]Jul 22, 2016A - Start Sync to SalesForce
    			if(IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(resultDissolve)) {
    				getExecutorService().submit(new Runnable() {
						@Override
						public void run() {
							getProfileManager().syncTradingInfoToSalesForce(customerId);
						}
					});
    			}
				//[TRSGAP-1207-quyen.le.manh]Jul 22, 2016A - End
    			
    			
    			//[TRSM1-3488-quyen.le.manh]Apr 14, 2016M - Start Update transfer money for Akazan flow
//    			if(IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(resultMt4)) {
//    				log.info("Change Accounts.Status = 'RE' of Account = "  + customerId + " is SUCCESS");
//    				//send email
//    				getTransferManager().sendMailDissolveStatusAkazan(customerId, ITrsConstants.SERVICES_TYPE.FX);
//    				getTransferManager().sendMailDissolveStatusAkazan(customerId, ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
//    			} else {
//    				log.info("Change Accounts.Status = 'RE' of Account = "  + customerId + " is FAIL or this account NOT is akazan");
//    			}
    			//[TRSM1-3488-quyen.le.manh]Apr 14, 2016M - End
    			
            	transferModel.setErrorMessage(IConstants.TRANSFER_MSG_CODE.MSG_NAB014);
            	return Result.SUCCESS;
            } else if (IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE.equals(transferResult)) { //cannot get rate
            	transferModel.setErrorMessage(IConstants.TRANSFER_MSG_CODE.MSG_NAB066);
            	return Result.FAILED;
            } else if (IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(transferResult)) { //warning can not transfer
            	transferModel.setErrorMessage(IConstants.TRANSFER_MSG_CODE.MSG_NAB029);
            	return Result.FAILED;
            } else if (IConstants.TRANSFER_STATUS.SUCCESS_APART.equals(transferResult)) { //Transaction successful from Source account to  General account. However Transaction failed from General account to Destination account. Please contact to system admin
            	transferModel.setErrorMessage(IConstants.TRANSFER_MSG_CODE.MSG_NAB114);
            	return Result.FAILED;
            } else {
            	//if fail display MSG_NAB015 Transaction failed. Please contact to system admin
            	transferModel.setErrorMessage(IConstants.TRANSFER_MSG_CODE.MSG_NAB015);
            	return Result.FAILED;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.FAILED;
        }
    }
    
	private boolean validateTransfer(TransferModel transferModel) {
		log.info("[start] validate transfer information");
		
		//Validate input
    	if(!validateInput(transferModel)) {
    		log.info("Validate Input fail, ErrorMessage: " + transferModel.getErrorMessage());
    		return false;
    	} else {
    		//Set transfer ServiceInfo
    		TransferMoneyInfo transferInfo = transferModel.getTransferMoneyInfo();
    		CustomerServicesInfo fromServicesInfo = new CustomerServicesInfo();
			fromServicesInfo.setCustomerId(transferInfo.getCustomerId());
			fromServicesInfo.setCurrencyCode(transferInfo.getCurrencyCode());
			fromServicesInfo.setServiceType(transferInfo.getTransferFrom());
			transferInfo.setFromServicesInfo(fromServicesInfo);
			
			CustomerServicesInfo toServicesInfo = new CustomerServicesInfo();
			toServicesInfo.setCustomerId(transferInfo.getCustomerId());
			toServicesInfo.setCurrencyCode(transferInfo.getDestinationCurrencyCode());
			toServicesInfo.setServiceType(transferInfo.getTransferTo());
			transferInfo.setToServicesInfo(toServicesInfo);
    	}
    	
    	//Check AMS is active
    	if(!validateAmsAccount(transferModel)) {
    		log.info("Validate Ams Account fail, ErrorMessage: " + transferModel.getErrorMessage());
    		return false;
    	}
    	
    	//Check balance and transfer amount
        if(!validateBalanceAndAmount(transferModel)) {
        	log.info("Validate Balance fail, ErrorMessage: " + transferModel.getErrorMessage());
        	return false;
        }
        
        //Check From Account is active
        if(!IConstants.SERVICES_TYPE.AMS.equals(transferModel.getTransferMoneyInfo().getTransferFrom())) {
        	if(!validateFromAccount(transferModel)) {
	        	log.info("Validate Source Account fail, ErrorMessage: " + transferModel.getErrorMessage());
	        	return false;
        	}
        }
        
        //Check To Account is active 
        if (!IConstants.SERVICES_TYPE.AMS.equals(transferModel.getTransferMoneyInfo().getTransferTo())) {
    		if(!validateToAccount(transferModel)) {
    			log.info("Validate Destination Account fail, ErrorMessage: " + transferModel.getErrorMessage());
    			return false;
    		}
        }
        
        //Set tranfer Name
        Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);
        transferModel.getTransferMoneyInfo().setTransferToName(mapServiceType.get(StringUtil.toString(transferModel.getTransferMoneyInfo().getTransferTo())));
        
        log.info("[end] validate transfer information");
        return true;
    }
	
	/**
	 * Validate Input param　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 16, 2015
	 * @MdDate
	 */
    private boolean validateInput(TransferModel transferModel) {
    	log.info("[start] validate input information");
    	
    	//Check valid transferFrom & transferTo
        Integer transferFrom = transferModel.getTransferMoneyInfo().getTransferFrom();
        Integer transferTo = transferModel.getTransferMoneyInfo().getTransferTo();
        
        if(transferFrom == null || transferTo == null){
            transferModel.setErrorMessage("nts.ams.fe.transfer.message.lacking_account");
            log.info(AmsApiControllerMng.getMsg("nts.ams.fe.transfer.message.lacking_account"));
        	return false;
        }
        
        if(transferFrom.equals(transferTo)){
            transferModel.setErrorMessage("nts.ams.fe.transfer.message.same_account");
            log.info(AmsApiControllerMng.getMsg("nts.ams.fe.transfer.message.same_account"));
            return false;
        }
        
        //Check input amount and re-set valid amount
        String transferAmountStr = transferModel.getAmount().trim().replaceAll("[,]", "");
        if (StringUtil.isEmpty(transferAmountStr)) {
            transferModel.setErrorMessage("MSG_NAF001");
            log.info("TransferAmount is empty");
            return false;
        } else if (transferAmountStr.length() > ITrsConstants.MAX_LENGTH_TRANSFER_AMOUNT) {
            transferModel.setErrorMessage("MSG_NAB008");
            log.info("TransferAmount > MAX_LENGTH_TRANSFER_AMOUNT " + ITrsConstants.MAX_LENGTH_TRANSFER_AMOUNT);
            return false;
        }
        transferModel.setAmount(transferAmountStr);
        
        //check tranferFlg on MasterData
        String wlCode = transferModel.getTransferMoneyInfo().getWlCode();
        boolean checkAllowTransferFlg = getTransferManager().checkTransferFlagOnMasterData(ITrsConstants.CONFIG_KEY.ALLOW_TRANSFER_FLG, wlCode);
        if(!checkAllowTransferFlg) {
            transferModel.setErrorMessage("MSG_NAB020");
            log.info(wlCode + " NOT ALLOW_TRANSFER_FLG");
            return false;
        }
        
        log.info("[end] validate input information");
        return true;
    }
    
	/**
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 16, 2015
	 * @MdDate
	 */
    private boolean validateToAccount(TransferModel transferModel) {
    	String customerId = transferModel.getCurrentCustomerId();
        Integer toService = transferModel.getTransferMoneyInfo().getTransferTo();
    	log.info("[start] Validate Destination Account, customerId: " + customerId + ", serviceType: " + toService);
    	
        boolean flg = false;
        
        if (!IConstants.SERVICES_TYPE.AMS.equals(toService)) {
            CustomerServicesInfo toServiceAccount = accountManager.getCustomerServiceInfo(customerId, toService);
            if (toServiceAccount != null) {
            	transferModel.getTransferMoneyInfo().setToServicesInfo(toServiceAccount);
            	
                Integer serviceStatus = toServiceAccount.getCustomerServiceStatus();
                
                //Check serviceStatus
                if (IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER.equals(serviceStatus) || IConstants.CUSTOMER_SERVIVES_STATUS.CANCEL.equals(serviceStatus)) {
                	transferModel.setErrorMessage("MSG_NAB091_TRANFER");
                	log.info("Destination account is not active; serviceStatus: " + serviceStatus + ", customerId: " + customerId + ", serviceType: " + toService);	
                } else if (!IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(serviceStatus) &&
                        !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(serviceStatus) &&
                        !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(serviceStatus)) {
                	
                	if(ITrsConstants.ACCOUNT_OPEN_STATUS.WAITING_ADD_ACCOUNT == serviceStatus.intValue()){
                		//Check status for BO, only BO has WAITING_ADD_ACCOUNT STATUS
                		if(!getTransferManager().validateBoCustomerStatus(customerId)){
                			transferModel.setErrorMessage("MSG_TRS_NAF_0069");
                			log.info("BoCustomer Account NOT pass on the knowledge verification test, serviceStatus: " + serviceStatus + ", customerId: " + customerId + ", serviceType: " + toService); 
                		} else {
                            transferModel.setErrorMessage("MSG_NAB091_TRANFER");
                            log.info("BoCustomer Account is not active, serviceStatus: " + serviceStatus + ", customerId: " + customerId + ", serviceType: " + toService);
                		}
                	} else {
                        transferModel.setErrorMessage("MSG_NAB091_TRANFER");
                        log.info("Destination Account is not active, serviceStatus: " + serviceStatus + ", customerId: " + customerId + ", serviceType: " + toService);
                	}
				} else {
					//Check AllowSendMoneyFlag

					//[TRSGAP-2100-cuong.bui.manh]Sep 13, 2016M - Start
					Integer allowFlg = toServiceAccount.getAllowSendmoneyFlg();
					log.info("Destination Account, CustomerId = " + customerId + ", ServiceType = " + toServiceAccount + ", AllowSendmoneyFlg: " + toServiceAccount.getAllowSendmoneyFlg());

					if (toService.intValue() == ServiceType.SC_VALUE) {
						if (allowFlg == null || allowFlg.intValue() == 0) {
							// not allow transfer
							transferModel.setErrorMessage("MSG_NAB030");
							log.info("Not AllowSendmoney to destination sc account, CustomerId = " + customerId + ", ServiceType = "
									+ toServiceAccount + ", AllowSendmoneyFlg: " + toServiceAccount.getAllowSendmoneyFlg());
						} else {
							flg = true;
						}
					} else if (toService.intValue() == ServiceType.NTD_FX_VALUE) {
						//NTD
						if (allowFlg.intValue() == AllowSendMoneyFlag.ALLOW_ALL_VALUE || allowFlg.intValue() == AllowSendMoneyFlag.ALLOW_DEPOSIT_ONLY_VALUE) {
							flg = true;
							log.info("Destination Account is OK, customerId: " + customerId + ", serviceType: " + toService);
						} else {
							transferModel.setErrorMessage("MSG_NAB030");
							log.info("Not AllowSendmoney to destination ntd account, CustomerId = " + customerId + ", ServiceType = "
									+ toService + ", AllowSendmoneyFlg: " + toServiceAccount.getAllowSendmoneyFlg());
						}
					} else {
						// BO
						flg = true;
					}
					//[TRSGAP-2100-cuong.bui.manh]Sep 13, 2016M - End
				}
			} else
            	log.info("Not found Destination Account, customerId: " + customerId + ", serviceType: " + toService);
        }
        
        log.info("[end] Validate Destination Account, customerId: " + customerId + ", serviceType: " + toService);
        return flg;
    }
	
    private boolean validateFromAccount(TransferModel transferModel) {
    	boolean flag = false;
    	String customerId = transferModel.getCurrentCustomerId();
    	Integer fromService = transferModel.getTransferMoneyInfo().getTransferFrom();
    	
    	log.info("[start] validate Source Account, CustomerId = " + customerId + ", ServiceType = " + fromService);
    	
    	//check send money flag with fromAccount
        CustomerServicesInfo fromServiceAccount = accountManager.getCustomerServiceInfo(customerId, fromService);
        
        if (fromServiceAccount != null) {
        	transferModel.getTransferMoneyInfo().setFromServicesInfo(fromServiceAccount);
        	Integer allowFlg = fromServiceAccount.getAllowSendmoneyFlg();
        	log.info("Source Account, CustomerId = " + customerId + ", ServiceType = " + fromService + ", AllowSendmoneyFlg: " + fromServiceAccount.getAllowSendmoneyFlg());
        	
            if (allowFlg == null || allowFlg.intValue() == 0) {
            	// not allow transfer
                transferModel.setErrorMessage("MSG_NAB030");
                log.info("Not AllowSendmoney with Source Account, CustomerId = " + customerId + ", ServiceType = " 
                		+ fromService + ", AllowSendmoneyFlg: " + fromServiceAccount.getAllowSendmoneyFlg());
            } else if(fromService.intValue() == ServiceType.NTD_FX_VALUE) {
            	//NTD
            	if(allowFlg.intValue() == AllowSendMoneyFlag.ALLOW_ALL_VALUE || allowFlg.intValue() == AllowSendMoneyFlag.ALLOW_WITHDRAWL_ONLY_VALUE)
            		flag = true;
            	else {
            		transferModel.setErrorMessage("MSG_NAB030");
            		log.info("Not AllowSendmoney with Source Account, CustomerId = " + customerId + ", ServiceType = " 
                    		+ fromService + ", AllowSendmoneyFlg: " + fromServiceAccount.getAllowSendmoneyFlg());
            	}
            } else {
            	//BO/SOCIAL
            	flag = true;
            }
        } else
        	log.info("Not found Source Account, CustomerId = " + customerId + ", ServiceType = " + fromService);
        
        log.info("[end] validate Source Account");
        return flag;
    }
    
    private boolean validateAmsAccount(TransferModel transferModel) {
    	String customerId = transferModel.getCurrentCustomerId();
    	log.info("[start] Validate Ams Account, customerId: " + customerId);
        boolean flg = false;
        
    	// Check accountOpenStatus for AMS
    	CustomerInfo customerInfo = accountManager.getCustomerInfo(customerId);
    	if(customerInfo == null) {
    		transferModel.setErrorMessage("MSG_NAB019");
    		log.info("Customer does not exist, customerId: " + customerId);
    		flg = false;
    	} else if (ITrsConstants.OPEN_STATUS.NOT_ACTIVE == customerInfo.getAccountOpenStatus()) {
            transferModel.setErrorMessage("MSG_NAB091_TRANFER");
            log.info("Ams Account is not active, customerId: " + customerId);
            flg = false;
        } else {
        	//Set Ams Service info
        	CustomerServicesInfo amsServicesInfo = new CustomerServicesInfo();
			amsServicesInfo.setCustomerId(customerId);
			amsServicesInfo.setCurrencyCode(customerInfo.getCurrencyCode());
			amsServicesInfo.setServiceType(IConstants.SERVICES_TYPE.AMS);
			transferModel.getTransferMoneyInfo().setAmsServicesInfo(amsServicesInfo);
            flg = true;
        }
        
        log.info("[end] Validate Ams Account");
        return flg;
    }
    
    private boolean validateBalanceAndAmount(TransferModel transferModel) {
    	log.info("[start] validate Balance And Amount");
    	
    	String customerId = transferModel.getCurrentCustomerId();
    	String wlCode = transferModel.getWlCode();
    	
    	//Get & check balance of From Account
    	BalanceInfo fromBalanceInfo = getBalanceManager().getBalanceInfo(customerId, transferModel.getTransferMoneyInfo().getTransferFrom(), transferModel.getCurrencyCode());
    	BigDecimal amountAvailabaleTransfer = MathUtil.parseBigDecimal(0);
        if (fromBalanceInfo != null) {
        	//RESTRICTED social account
        	if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(transferModel.getTransferMoneyInfo().getTransferFrom()) 
        			&& AccountBalanceResult.RESTRICTED.equals(fromBalanceInfo.getResult())) {
        		transferModel.setErrorMessage("MSG_TRS_NAB_0061");
        		return false;
        	}
        	
        	if(!AccountBalanceResult.SUCCESS.equals(fromBalanceInfo.getResult())) {
        		transferModel.setErrorMessage("MSG_NAB005");
        		return false;
        	}
        		
        	transferModel.setBalanceAmsInfo(fromBalanceInfo);
        	log.info("Got Source BalanceInfo: " + fromBalanceInfo);
        	
            CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + fromBalanceInfo.getCurrencyCode());
            if (currencyInfo != null) {
                amountAvailabaleTransfer = MathUtil.rounding(fromBalanceInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
            } else {
                amountAvailabaleTransfer = MathUtil.parseBigDecimal(fromBalanceInfo.getAmountAvailable());
            }
            log.info("Amount Available Transfer: " + amountAvailabaleTransfer);
        } else {
            transferModel.setErrorMessage("nts.ams.fe.transfer.message.cannot.get.balance");
            log.warn("Not found Source BalanceInfo of CustomerId: " + customerId + ", ServiceType: " + transferModel.getTransferMoneyInfo().getTransferFrom());
            return false;
        }
        
        //Get & check balance of From Account (SC account)
        if (IConstants.SERVICES_TYPE.COPY_TRADE.equals(transferModel.getTransferMoneyInfo().getTransferTo())) {
        	BalanceInfo toBalanceInfo = getBalanceManager().getBalanceInfo(customerId, transferModel.getTransferMoneyInfo().getTransferTo(), transferModel.getCurrencyCode());
        	if (toBalanceInfo == null) {
        		transferModel.setErrorMessage("nts.ams.fe.transfer.message.cannot.get.balance");
                log.warn("Not found To BalanceInfo of CustomerId: " + customerId + ", ServiceType: " + transferModel.getTransferMoneyInfo().getTransferTo());
                return false;
        	} else if (AccountBalanceResult.RESTRICTED.equals(toBalanceInfo.getResult())) {
        		transferModel.setErrorMessage("MSG_TRS_NAB_0061");
        		return false;
        	}
        }
        
        //Check Min/Max amount can transfer
        String fromCurrencyCode = transferModel.getCurrencyCode();
        String toCurrencyCode = transferModel.getToAccountCurrencyCode();
        
        BigDecimal amount = MathUtil.parseBigDecimal(transferModel.getAmount(), null);                        
        if (amount == null) {
            transferModel.setErrorMessage("MSG_NAB057");
            log.info("Invalid Transfer Amount = " + amount);
            return false;
        }
        
        if(IConstants.CURRENCY_CODE.JPY.equalsIgnoreCase(toCurrencyCode)){
        	amount = amount.divide(MathUtil.parseBigDecimal(1), 0, RoundingMode.HALF_UP);
        } else{
        	amount = amount.divide(MathUtil.parseBigDecimal(1), 2, RoundingMode.HALF_UP);
        }
        transferModel.setAmount(String.valueOf(amount));
        log.info("Set TransferAmount after rounding = " + amount);
        
        BigDecimal convertedAmount = MathUtil.parseBigDecimal(0);
        BigDecimal convertRate = getBalanceManager().getConvertRateOnFrontRate(fromCurrencyCode , toCurrencyCode, IConstants.FRONT_OTHER.SCALE_ALL);
        if(convertRate == null || convertRate.equals(BigDecimal.ZERO)){
            transferModel.setErrorMessage("nts.ams.fe.transfer.message.convert_rate_not_available");
            log.info("Invalid ConvertRate = " + convertRate);
            return false;
        }
        
        CurrencyInfo fromCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + fromCurrencyCode);
        convertedAmount = amount.divide(convertRate, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
		transferModel.setConvertedAmount(String.valueOf(convertedAmount));
       
		//validate with min transfer amount
        Map<String, String> mapWlConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
        String configValue = mapWlConfig.get(transferModel.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_TRANSFER_AMOUNT);
        BigDecimal minTransferAmount = MathUtil.parseBigDecimal(configValue);
        
        log.info("Compare converted amount transfer with MIN_TRANSFER_AMOUNT value = " + minTransferAmount.doubleValue() + " with currency code = " + transferModel.getCurrencyCode());
        if ((fromCurrencyCode.equalsIgnoreCase(transferModel.getToAccountCurrencyCode()) && amount.compareTo(minTransferAmount) < 0)
                || (!fromCurrencyCode.equalsIgnoreCase(transferModel.getToAccountCurrencyCode()) && convertedAmount.compareTo(minTransferAmount) < 0)) {
            transferModel.setErrorMessage("MSG_NAB095");
            log.info("Amount = " + convertedAmount.doubleValue() + " is smaller than min transfer amount = " + minTransferAmount.doubleValue());
            return false;
        }
        log.info("end compare converted amount transfer with MIN_TRANSFER_AMOUNT");
        
        log.info("start compare Converted Amount = " + convertedAmount.doubleValue() + " with Amount Available Transfer = " + amountAvailabaleTransfer);
        if (convertedAmount.compareTo(amountAvailabaleTransfer) > 0) {
            transferModel.setErrorMessage("MSG_NAB029");
            log.info("Converted Amount = " + convertedAmount.doubleValue() + " is larger than Amount Available Transfer = " + amountAvailabaleTransfer);
            return false;
        }
//        log.info("end compare Converted Amount = " + convertedAmount.doubleValue() + " with Amount Available Transfer = " + amountAvailabaleTransfer);
		log.info("[end] validate Balance And Amount: OK");
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
	public RpcMessage createRpcMessage(AmsTranferMoneyInfo transferInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_RESPONSE);
		if(errCode != null)
			response.setMessageCode(errCode);
		
		if(result == Result.SUCCESS && transferInfo != null) {
			AmsTransferResponse.Builder customerInfoResponse = AmsTransferResponse.newBuilder();
			customerInfoResponse.setTransferInfo(transferInfo);
			response.setPayloadData(customerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(result);
		}
		
		return response.build();
	}
    
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	public ITransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(ITransferManager transferManager) {
		this.transferManager = transferManager;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}