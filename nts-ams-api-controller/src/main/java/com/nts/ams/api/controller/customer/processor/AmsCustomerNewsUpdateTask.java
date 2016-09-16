package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.util.log.Logit;
import phn.com.trs.util.enums.ConfirmAgreementResult;
import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description Handle AmsCustomerNewsUpdate Task
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerNewsUpdateTask extends Thread {
	private static Logit log = Logit.getInstance(AmsCustomerNewsUpdateTask.class);
	private AmsCustomerNewsUpdateRequestWraper wraper;
	protected ISocialManager socialManager;
	
	public AmsCustomerNewsUpdateTask(AmsCustomerNewsUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerNewsUpdateRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerNewsUpdateRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			ConfirmAgreementResult result = getSocialManager().agreementConfirm(request.toBuilder());
			if(ConfirmAgreementResult.CONFIRMED.equals(result)) {
				log.info("Agreement CONFIRMED, reload AmsCustomerNews to Redis");
				//Remove agreement cache of customer
				AmsApiControllerMng.getDataCache().removeAmsCustomerNews(request.getCustomerId());
				AmsApiControllerMng.getDataCache().getAmsCustomerNews(request.getCustomerId());
			}
			
			//Response to client
			RpcMessage response = createRpcMessage(AmsCustomerNewsUpdateResponse.newBuilder(), !ConfirmAgreementResult.FAILED.equals(result));
			
			log.info("Response to client: " + response);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerNewsUpdateRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			RpcMessage response = createRpcMessage(AmsCustomerNewsUpdateResponse.newBuilder(), false);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
		}  finally {
			AmsApiControllerMng.getAmsCustomerNewsUpdateProcessor().onComplete(wraper);
		}
	}

	public RpcMessage createRpcMessage(AmsCustomerNewsUpdateResponse.Builder amsCustomerNewsResponse, boolean result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_UPDATE_RESPONSE);
		response.setPayloadData(amsCustomerNewsResponse.build().toByteString());
		if(result){
			response.setResult(Result.SUCCESS);
		}else{
			response.setResult(Result.FAILED);
		}
		
		return response.build();
	}

	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}
	
}