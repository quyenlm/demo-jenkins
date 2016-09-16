package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.utils.Helper;
import cn.nextop.social.api.admin.proxy.glossary.OpenAccountResult;

import com.nts.ams.api.controller.customer.bean.AmsCustomerRegisterSocialRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerRegisterSocialTask implements Runnable {
	private Logit log = Logit.getInstance(AmsCustomerRegisterSocialTask.class);
	private IProfileManager profileManager = null;
	private AmsCustomerRegisterSocialRequestWraper wraper;
	
	public AmsCustomerRegisterSocialTask(AmsCustomerRegisterSocialRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		RpcMessage response = null;
		
		try {
			boolean result = false;
			
			AmsCustomerRegisterSocialRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerRegisterSocialRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			if (validateRegisterSC(request.getLoginId())) {
				OpenAccountResult openAccountResult = profileManager.openSocialAccount(request);
				log.info("open SocialAccount Result: " + openAccountResult);
				
				if (openAccountResult == OpenAccountResult.SUCCESS)
					result = true;
			} else {
				result = true;
				log.info("Customer has loginId: " + request.getLoginId() + " not in list test account, response result = true");
			}
			
			response = createRpcMessage(result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = createRpcMessage(false);
		} finally {
			AmsApiControllerMng.getAmsCustomerBEResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerRegisterSocialRequest, requestId: " + wraper.getResponseBuilder().getId());
			
			AmsApiControllerMng.getAmsCustomerRegisterSocialProcessor().onComplete(wraper);
		}
	}
	
	public RpcMessage createRpcMessage(boolean result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REGISTER_SOCIAL_RESPONSE);
		
		AmsCustomerRegisterSocialResponse.Builder amsCustomerInfoResponse = AmsCustomerRegisterSocialResponse.newBuilder();
		amsCustomerInfoResponse.setResult(result ? AmsCustomerRegisterSocialResponse.Result.SUCCESS : AmsCustomerRegisterSocialResponse.Result.FAIL);
		response.setPayloadData(amsCustomerInfoResponse.build().toByteString());
		response.setResult(result ? Result.SUCCESS : Result.FAILED);
		
		return response.build();
	}
	
	public boolean validateRegisterSC(final String loginId) {
		if (SystemPropertyConfig.getListCustomerTestInternalSc().isEmpty())
			return true;
		
		AmsCustomer amsCustomer = profileManager.getAmsCustomerByLoginId(loginId);
		if (amsCustomer != null && Helper.isTestInternalScCustomer(amsCustomer.getCustomerId()))
			return true;
		
		return false;
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}