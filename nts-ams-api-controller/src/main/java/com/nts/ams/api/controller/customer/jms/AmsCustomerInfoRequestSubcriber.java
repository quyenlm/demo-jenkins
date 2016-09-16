package com.nts.ams.api.controller.customer.jms;

import javax.jms.BytesMessage;

import phn.com.nts.util.log.Logit;

import com.nts.ams.api.controller.customer.bean.AmsBoAdditionalInfoUpdateRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerAgreementNewsWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerBoTestUpdateRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerCloseSocialRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerModifySocialRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsUpdateRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerPaymentUpdateRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerRegisterSocialRequestWraper;
import com.nts.ams.api.controller.customer.bean.AmsCustomerReportsRequestWraper;
import com.nts.ams.api.controller.customer.bean.CustomerBalanceRequestWraper;
import com.nts.ams.api.controller.customer.bean.CustomerInfoRequestWraper;
import com.nts.ams.api.controller.customer.bean.CustomerInfoUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.service.SecurityMananger;
import com.nts.ams.api.controller.util.Helper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerAgreementNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

/**
 * @description AmsCustomerInfoRequest Subcriber
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerInfoRequestSubcriber {
	private static final Logit log = Logit.getInstance(AmsCustomerInfoRequestSubcriber.class);
	
	public void onMessage(Object msg) {
		try {
			byte[] data = null;
			BytesMessage bytesMessage = null;
			RpcMessage rpcRequest = null;
			
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
					rpcResponseBuilder.setResult(com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result.SERVICE_NOT_AVAIABLE);
					rpcResponseBuilder.setPayloadClass(rpcRequest.getPayloadClass());
					writeRejectMsg(rpcResponseBuilder.build(), rpcRequest.getPayloadClass());
					return;
				}
				
				//Handle
				if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST)) {
					//CUSTOMER_INFO
					AmsApiControllerMng.getAmsProfileProcessor().onMessage(
							new CustomerInfoRequestWraper(AmsCustomerInfoRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_REQUEST)) {
					//CUSTOMER_BALANCE
					AmsApiControllerMng.getAmsCustomerBalanceProcessor().onMessage(
							new CustomerBalanceRequestWraper(AmsCustomerBalanceRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_REQUEST)) {
					//CUSTOMER_INFO_UPDATE
					AmsApiControllerMng.getAmsCustomerInfoUpdateProcessor().onMessage(
							new CustomerInfoUpdateRequestWraper(AmsCustomerInfoUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_REQUEST)) {
			    	//CUSTOMER_NEWS
			    	AmsApiControllerMng.getAmsCustomerNewsProcessor().onMessage(
			    			new AmsCustomerNewsRequestWraper(AmsCustomerNewsRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_AGREEMENT_NEWS_REQUEST)) {
			    	//AMS_CUSTOMER_AGREEMENT_NEWS_REQUEST
			    	AmsApiControllerMng.getAmsCustomerAgreementNewsProcessor().onMessage(
			    			new AmsCustomerAgreementNewsWraper(AmsCustomerAgreementNewsRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_UPDATE_REQUEST)) {
			    	//CUSTOMER_NEWS_UPDATE
			    	AmsApiControllerMng.getAmsCustomerNewsUpdateProcessor().onMessage(
			    			new AmsCustomerNewsUpdateRequestWraper(AmsCustomerNewsUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_UPDATE_REQUEST)) {
			    	//CUSTOMER_PAYMENT_UPDATE
			    	AmsApiControllerMng.getAmsCustomerPaymentUpdateProcessor().onMessage(
			    			new AmsCustomerPaymentUpdateRequestWraper(AmsCustomerPaymentUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BO_TEST_UPDATE_REQUEST)) {
			    	//CUSTOMER_BO_TEST_UPDATE
			    	AmsApiControllerMng.getAmsCustomerBoTestUpdateProcessor().onMessage(
			    			new AmsCustomerBoTestUpdateRequestWraper(AmsCustomerBoTestUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_BO_ADDITIONAL_INFO_UPDATE_REQUEST)) {
			    	//AMS_BO_ADDITIONAL_INFO_UPDATE
			    	AmsApiControllerMng.getAmsBoAdditionalInfoUpdateProcessor().onMessage(
			    			new AmsBoAdditionalInfoUpdateRequestWraper(AmsBoAdditionalInfoUpdateRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_REQUEST)) {
			    	//AMS_CUSTOMER_REPORTS_REQUEST
			    	AmsApiControllerMng.getAmsCustomerReportsProcessor().onMessage(
			    			new AmsCustomerReportsRequestWraper(AmsCustomerReportsRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
			    } else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REGISTER_SOCIAL_REQUEST)) {
			    	//AMS_CUSTOMER_REGISTER_SOCIAL_REQUEST
					AmsApiControllerMng.getAmsCustomerRegisterSocialProcessor().onMessage(
							new AmsCustomerRegisterSocialRequestWraper(AmsCustomerRegisterSocialRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_MODIFY_SOCIAL_REQUEST)) {
					//AMS_CUSTOMER_MODIFY_SOCIAL_REQUEST
					AmsApiControllerMng.getAmsCustomerModifySocialProcessor().onMessage(
							new AmsCustomerModifySocialRequestWraper(AmsCustomerModifySocialRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				} else if(rpcRequest.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_CLOSE_SOCIAL_REQUEST)) {
					//AMS_CUSTOMER_CLOSE_SOCIAL_REQUEST
					AmsApiControllerMng.getAmsCustomerCloseSocialProcessor().onMessage(
							new AmsCustomerCloseSocialRequestWraper(AmsCustomerCloseSocialRequest.parseFrom(rpcRequest.getPayloadData()), rpcResponseBuilder));
					
				}
				else
					log.warn("Request is not AmsMsgType");
			} else
				log.warn("Request has invalid ProtoVersion: " + rpcRequest.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	private void writeRejectMsg(RpcAms.RpcMessage rejectMsg, String requestType) {
		if(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_REQUEST.equals(requestType))
			AmsApiControllerMng.getAmsCustomerReportResponsePublisher().publish(rejectMsg);
		
		else if(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REGISTER_SOCIAL_REQUEST.equals(requestType)
				|| ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_MODIFY_SOCIAL_REQUEST.equals(requestType))
			AmsApiControllerMng.getAmsCustomerBEResponsePublisher().publish(rejectMsg);
		else
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(rejectMsg);
	}
}
