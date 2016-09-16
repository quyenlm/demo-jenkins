package com.nts.ams.api.controller.transfer.jms;

import javax.jms.BytesMessage;

import phn.com.nts.util.log.Logit;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.service.SecurityMananger;
import com.nts.ams.api.controller.transfer.bean.AmsCustomerPaymentInfoRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsDepositRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsDepositUpdateRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsTransferRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsTransferSocialRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalCancelRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalRequestWraper;
import com.nts.ams.api.controller.util.Helper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalCancelRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

/**
 * @description AmsTransactionInfo Subcriber
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransactionInfoSubcriber {
	private static final Logit log = Logit.getInstance(AmsTransactionInfoSubcriber.class);
	
	public void onMessage(Object msg) {
		try {
			byte[] data = null;
			BytesMessage bytesMessage = null;
			RpcMessage rpcRequest = null;
		    
		    rpcRequest = null;
		    bytesMessage = (BytesMessage) msg;
		    data = new byte[(int) bytesMessage.getBodyLength()];
		    bytesMessage.readBytes(data);
		    rpcRequest = RpcAms.RpcMessage.parseFrom(data);
		    
		    log.info("Received Request from AMS:\n" + rpcRequest);
		    
		    if(!AmsApiControllerMng.isStarted()) {
		    	log.warn("AmsApiController NOT STARTED!");
		    	return;
		    }
		    
		    if(Helper.checkProtoVersion(rpcRequest.getVersion())) {
				//Prepare response message
				RpcAms.RpcMessage.Builder rpcResponseBuilder = RpcAms.RpcMessage.newBuilder();
				rpcResponseBuilder.setId(rpcRequest.getId());
				rpcResponseBuilder.setVersion(rpcRequest.getVersion());
				
				if(!SecurityMananger.getInstance().isServiceAvaiable()) {
					log.info("There are too much request. Reject requestId: " + rpcRequest.getId());
					
					//there are too much request, reject this request
					rpcResponseBuilder.setPayloadClass(rpcRequest.getPayloadClass());
					rpcResponseBuilder.setResult(com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result.SERVICE_NOT_AVAIABLE);
					writeRejectMsg(rpcResponseBuilder.build());
					return;
				}
					
				//Handle
				if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_REQUEST)) {
					AmsApiControllerMng.getAmsCustomerPaymentInfoProcessor().onMessage(
							new AmsCustomerPaymentInfoRequestWraper(AmsCustomerPaymentInfoRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_REQUEST)) {
					AmsApiControllerMng.getAmsSerializeTransactionMananger().onMessage(
							new AmsDepositRequestWraper(AmsDepositRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDRAWAL_REQUEST)) {
					AmsApiControllerMng.getAmsSerializeTransactionMananger().onMessage(
							new AmsWithdrawalRequestWraper(AmsWithdrawalRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDRAWAL_CANCEL_REQUEST)) {
					AmsApiControllerMng.getAmsWithdrawalCancelProcessor().onMessage(
							new AmsWithdrawalCancelRequestWraper(AmsWithdrawalCancelRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_REQUEST)) {
					AmsApiControllerMng.getAmsSerializeTransactionMananger().onMessage(
							new AmsTransferRequestWraper(AmsTransferRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_UPDATE_REQUEST)) {
					AmsApiControllerMng.getAmsSerializeTransactionMananger().onMessage(
							new AmsDepositUpdateRequestWraper(AmsDepositUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_SOCIAL_REQUEST)) {
					AmsApiControllerMng.getAmsTransferSocialAgentProcessor().onMessage(
							new AmsTransferSocialRequestWraper(AmsTransferSocialRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				}
				else
					log.warn("Request is not AmsMsgType");
			} else
				log.warn("Request has invalid ProtoVersion: " + rpcRequest.getVersion());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void writeRejectMsg(RpcAms.RpcMessage rejectMsg) {
		AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(rejectMsg);
	}
}
