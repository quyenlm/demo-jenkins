package com.nts.ams.api.controller.transfer.processor;

import java.sql.Timestamp;

import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IWithdrawalManager;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalCancelRequestWraper;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsWithdrawalTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalCancelRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalCancelResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsWithdrawalCancel Task
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsWithdrawalCancelTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsWithdrawalCancelTask.class);
	
	private AmsWithdrawalCancelRequestWraper wraper;
    private IWithdrawalManager withdrawalManager;
    private AmsWithdrawal amsWithdrawal;
    private String msgCode;
	public AmsWithdrawalCancelTask(AmsWithdrawalCancelRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsWithdrawalCancelRequest request = wraper.getRequest();
			log.info("[start] handle AmsWithdrawalCancelRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			Result result = withdrawalCancel(request.getCustomerId(), request.getWithdrawalId());
			
			AmsWithdrawalTransactionInfo withdrawalInfo = null;
			if(result == Result.SUCCESS && amsWithdrawal!= null)
				withdrawalInfo = Converter.convertWithdrawalInfo(amsWithdrawal);
			//Response to client
			RpcMessage response = createRpcMessage(withdrawalInfo, result, msgCode);
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response);
			log.info("[end] handle AmsWithdrawalCancelRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsWithdrawalCancelProcessor().onComplete(wraper);
		}
	}
	
	public Result withdrawalCancel(String customerId, String withdrawalId){
		log.info("[start] cancel withdrawal transaction, customerId: " + customerId + ", withdrawalId: " + withdrawalId);
		Result result = Result.FAILED;
		if(StringUtil.isEmpty(customerId) || StringUtil.isEmpty(withdrawalId))
			msgCode = IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANNOT_CANCEL;
		else {
			try {
				amsWithdrawal =  withdrawalManager.getAmsWithdrawal(withdrawalId);
				log.info("Loaded AmsWithdrawal: " + amsWithdrawal);
				if(amsWithdrawal.getStatus().equals(IConstants.STATUS_WITHDRAW.REQUESTING)){
					amsWithdrawal.setRegCustomerId(customerId);
					amsWithdrawal.setStatus(IConstants.STATUS_WITHDRAW.CANCEL);
					amsWithdrawal.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					withdrawalManager.updateStatusofWithdrawal(amsWithdrawal, customerId);
					msgCode = IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANCEL;
					result = Result.SUCCESS;
				} else {
					msgCode = IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANNOT_CANCEL;
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				msgCode = IConstants.WITHDRAWAL_MSG_CODE.MSG_WITHDRAWAL_CANNOT_CANCEL;
			}
		}
		log.info("[end] cancel withdrawal transaction");
		return result;
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
	public RpcMessage createRpcMessage(AmsWithdrawalTransactionInfo withdrawalInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDAWAL_RESPONSE);
		
		if(result == Result.SUCCESS && withdrawalInfo != null) {
			AmsWithdrawalCancelResponse.Builder customerInfoResponse = AmsWithdrawalCancelResponse.newBuilder();
			customerInfoResponse.setWithdrawalInfo(withdrawalInfo);
			response.setPayloadData(customerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
			response.setMessageCode(errCode);
		} else {
			response.setResult(result);
			response.setMessageCode(errCode);
		}
		
		return response.build();
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}
}