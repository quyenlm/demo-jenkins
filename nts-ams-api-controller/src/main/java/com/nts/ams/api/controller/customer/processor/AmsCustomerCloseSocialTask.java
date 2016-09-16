package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.utils.Helper;
import cn.nextop.social.api.admin.proxy.glossary.CloseAccountResult;

import com.nts.ams.api.controller.customer.bean.AmsCustomerCloseSocialRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerCloseSocialTask implements Runnable {
	private Logit log = Logit.getInstance(AmsCustomerCloseSocialTask.class);
	private IProfileManager profileManager = null;
	private AmsCustomerCloseSocialRequestWraper wraper;
	
	public AmsCustomerCloseSocialTask(AmsCustomerCloseSocialRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		RpcMessage response = null;
		try {
			boolean result = false;
			
			AmsCustomerCloseSocialRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerCloseSocialRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			if (Helper.validateRequestToSC(request.getCustomerId())) {
				CloseAccountResult closeAccountResult = profileManager.closeSocialAccount(request.getCustomerId());
				log.info("close SocialAccount Result: " + closeAccountResult);
				
				if (closeAccountResult == CloseAccountResult.SUCCESS)
					result = true;
				
			} else {
				result = true;
				log.info("CustomerId: " + request.getCustomerId() + " not in list test account, response result = true");
			}
			
			response = createRpcMessage(result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = createRpcMessage(false);
		} finally {
			AmsApiControllerMng.getAmsCustomerBEResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerCloseSocialRequest, requestId: " + wraper.getResponseBuilder().getId());
			
			AmsApiControllerMng.getAmsCustomerCloseSocialProcessor().onComplete(wraper);
		}
	}
	
	public RpcMessage createRpcMessage(boolean result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_CLOSE_SOCIAL_RESPONSE);
		
		AmsCustomerCloseSocialResponse.Builder amsCustomerCloseSocialResponse = AmsCustomerCloseSocialResponse.newBuilder();
		amsCustomerCloseSocialResponse.setResult(result ? AmsCustomerCloseSocialResponse.Result.SUCCESS : AmsCustomerCloseSocialResponse.Result.FAIL);
		response.setPayloadData(amsCustomerCloseSocialResponse.build().toByteString());
		response.setResult(result ? Result.SUCCESS : Result.FAILED);
		
		return response.build();
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}