package com.nts.ams.api.controller.transfer.processor;

import java.math.BigDecimal;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.social.SCManager;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferResponse;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferStatus;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsTransferSocialRequestWraper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferSocialResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;
import phn.nts.ams.utils.Helper;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 8, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransferSocialAgentTask implements Runnable {
	private Logit log = Logit.getInstance(AmsTransferSocialAgentTask.class);
	private IAccountManager accountManager;
	private ITransferManager transferManager;
	private AmsTransferSocialRequestWraper wraper;
	private static final int SC_TIME_OUT = 2;  
	
	public AmsTransferSocialAgentTask(AmsTransferSocialRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		RpcMessage response = null;
		
		try {
			boolean rpcResult = false;
			int socialResponseResult = SC_TIME_OUT; // default value is 2 - timeout
			
			AmsTransferSocialRequest request = wraper.getRequest();
			log.info("[start] handle AmsTransferSocialRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			BigDecimal amount = new BigDecimal(request.getAmount());
			TransferStatus status = null;
			Long cashflowId = null;
			Long sourceId = null;
			long responseCashflowId = 0L;
			
			if(request.hasCashflowId())
				cashflowId = request.getCashflowId();
			if(request.hasStatus())
				status = IConstants.TRANSFER_STATUS.SUCCESS.equals(request.getStatus()) ? TransferStatus.SUCCESS: TransferStatus.FAIL;
			if(request.hasSourceId())
				sourceId = request.getSourceId();
				
			//Validate data
			validateParam(amount, request.getCustomerServiceId());
			
			CustomerServicesInfo serviceAccount = accountManager.getCustomerServiceInfo(request.getCustomerServiceId());
			if(serviceAccount == null)
				throw new Exception("Not exist CustomerServiceId: " + request.getCustomerServiceId());
			
			String customerId = accountManager.getCustomerIdByCustomerService(request.getCustomerServiceId());
			
			//Execute transfer
			// 1. Register transfer to SocialApi
			log.info("[start] register transfer to SocialApi, transferMoneyId: " + request.getId()
					+ ", amount: " + request.getAmount() + ", subGroupCode: " + serviceAccount.getSubGroupCode() + ", groupName: " + serviceAccount.getGroupName());
			boolean isEaAccount = Helper.isEaGroupName(serviceAccount.getGroupName());
			TransferResponse transferResponse = SCManager.getInstance().transfer(request.getId(),
					request.getCashflowType(), amount, Integer.valueOf(request.getCustomerServiceId()), customerId, status, cashflowId, isEaAccount, sourceId);
			
			log.info("[end] register transfer to SocialApi, transferMoneyId: " + request.getId()
					+ ", amount: " + request.getAmount() + ", transferResponse: " + transferResponse);
			
			if (transferResponse != null) {
				if (transferResponse.getResult() == 1) {
					rpcResult = true;
				}
				socialResponseResult = transferResponse.getResult();
				responseCashflowId = transferResponse.getCashflowId();
			}
			
			response = createRpcMessage(rpcResult, socialResponseResult, responseCashflowId);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = createRpcMessage(false, SC_TIME_OUT, 0);
		} finally {
			AmsApiControllerMng.getAmsCustomerBEResponsePublisher().publish(response);
			log.info("[end] handle AmsTransferSocialRequest, requestId: " + wraper.getResponseBuilder().getId());
			
			AmsApiControllerMng.getAmsTransferSocialAgentProcessor().onComplete(wraper);
		}
	}
	
	public RpcMessage createRpcMessage(boolean rpcResult, int socialResponseResult, long cashflowId) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_SOCIAL_RESPONSE);
		
		AmsTransferSocialResponse.Builder amsTransferSocialResponse = AmsTransferSocialResponse.newBuilder();
		amsTransferSocialResponse.setResult(AmsTransferSocialResponse.Result.valueOf(socialResponseResult));
		if (cashflowId != 0)
			amsTransferSocialResponse.setCashflowId(cashflowId);
		
		response.setPayloadData(amsTransferSocialResponse.build().toByteString());
		response.setResult(rpcResult ? Result.SUCCESS : Result.FAILED);
		
		return response.build();
	}
	
	private void validateParam(BigDecimal amount, String customerServiceId) throws Exception {
		if(amount == null || BigDecimal.ZERO.compareTo(amount) == 0)
			throw new Exception("Amount is invalid: " + amount);
		if(StringUtil.isEmpty(customerServiceId))
			throw new Exception("CustomerServiceId is invalid: " + customerServiceId);
	}
	
	public ITransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(ITransferManager transferManager) {
		this.transferManager = transferManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
}