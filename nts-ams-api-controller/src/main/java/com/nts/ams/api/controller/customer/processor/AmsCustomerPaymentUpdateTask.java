package com.nts.ams.api.controller.customer.processor;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.domain.CustomerInfo;

import com.nts.ams.api.controller.customer.bean.AmsCustomerPaymentUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ActionType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerBankInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerPaymentInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.PaymentMethod;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description Handle AmsCustomerBalanceRequest
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentUpdateTask extends Thread {
	private static Logit log = Logit.getInstance(AmsCustomerPaymentUpdateTask.class);
	private AmsCustomerPaymentUpdateRequestWraper wraper;
	protected IProfileManager profileManager;
	public AmsCustomerPaymentUpdateTask(AmsCustomerPaymentUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerPaymentUpdateRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerPaymentUpdateTask, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			String result = "";
			if(request.getActionType().equals(ActionType.INSERT) || request.getActionType().equals(ActionType.UPDATE)){
				result = insertOrUpdate(request.toBuilder());
			}else if(request.getActionType().equals(ActionType.DELETE)){
				result = deletePayment(request.toBuilder());
			}
			RpcMessage response = createRpcMessage(AmsCustomerPaymentUpdateRequest.newBuilder(), result);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerPaymentUpdateTask, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			RpcMessage response = createRpcMessage(AmsCustomerPaymentUpdateRequest.newBuilder(), "FAIL");
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
		} finally {
			AmsApiControllerMng.getAmsCustomerPaymentUpdateProcessor().onComplete(wraper);
		}
	}

	public RpcMessage createRpcMessage(AmsCustomerPaymentUpdateRequest.Builder amsCustomerPaymentUpdateResponse, String result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_UPDATE_RESPONSE);
		
		if(!StringUtils.isBlank(result) && result.equals(IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS)){
			response.setResult(Result.SUCCESS);
		}else{
			response.setResult(Result.FAILED);
			response.setMessageCode(result);
		}
		
		return response.build();
	}
	
	public String insertOrUpdate(AmsCustomerPaymentUpdateRequest.Builder request) {
		try{
			String customerId = request.getCustomerId();
			AmsCustomerPaymentInfo.Builder customerPaymentInfo = request.getPaymentInfo().toBuilder();
			
			if(customerPaymentInfo == null) {
				customerPaymentInfo = AmsCustomerPaymentInfo.newBuilder();
			} 
			
			AmsCustomerBankInfo.Builder amsCustomerBankInfo = customerPaymentInfo.getCustomerBankInfo().toBuilder();
			
			CustomerInfo customerInfo = profileManager.getCustomerInfo(customerId);
			if(customerInfo == null){
				log.info("Not get customer info with customerId : "+ customerId);
				return "CUSTOMER_NOT_EXISTS";
			}
					
//			List<BankTransferInfo> bankTransferInfoLst = getProfileManager().getBankInfo(customerId);
			
			customerPaymentInfo.setCustomerId(customerId);
			String validateResult = validateBankTransferInfo(amsCustomerBankInfo);
			
			if(!StringUtil.isEmpty(validateResult)){
				log.info("Validate fail: " + validateResult);
				return validateResult;
			}
			
			return getProfileManager().addBankTransfer(customerPaymentInfo.getCustomerBankInfoBuilder());
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
	}
	
	public String deletePayment(AmsCustomerPaymentUpdateRequest.Builder request) {
		try{
			AmsCustomerPaymentInfo.Builder customerPaymentInfo = request.getPaymentInfo().toBuilder();
			AmsCustomerBankInfo.Builder amsCustomerBankInfo = customerPaymentInfo.getCustomerBankInfo().toBuilder();
			
			if(StringUtils.isBlank(amsCustomerBankInfo.getCustomerBankId())){
				log.info("Delete fail cauby customerBankId wrong " + amsCustomerBankInfo.getCustomerBankId());
				return "CustomerBankId_Wrong";
			}
			
			log.info("Delete bankInfo with CusbankId : "+ amsCustomerBankInfo.getCustomerBankId());
			String ewalletType = String.valueOf(customerPaymentInfo.getPaymentMethod().getNumber());
			if(ewalletType !=null){
				if (ewalletType.equals(String.valueOf(PaymentMethod.PAYMENT_BANK_TRANFER_VALUE))) {
					return profileManager.deleteBankTransfer(amsCustomerBankInfo.getCustomerBankId());
				}
			}
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
	}
	
	private String validateBankTransferInfo(AmsCustomerBankInfo.Builder amsCustomerBankInfo) {
		if (StringUtils.isEmpty(amsCustomerBankInfo.getBankName())) {
			return "MSG_NAB001";
		} 
		if (StringUtils.isEmpty(amsCustomerBankInfo.getBranchName())) {
			return "MSG_NAB001";
		} 
		if (StringUtils.isEmpty(amsCustomerBankInfo.getAccountNo())) {
			return "MSG_NAB001";
		}else{
			if (amsCustomerBankInfo.getAccountNo().length() != 7) {
				return "MSG_NAB007";
			}
		}
		return "";
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	
}