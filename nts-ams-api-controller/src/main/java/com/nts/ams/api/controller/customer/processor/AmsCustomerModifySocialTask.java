package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.utils.Helper;
import cn.nextop.social.api.admin.proxy.glossary.ModifyAccountResult;
import com.nts.ams.api.controller.customer.bean.AmsCustomerModifySocialRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerModifySocialTask implements Runnable {
	private Logit log = Logit.getInstance(AmsCustomerModifySocialTask.class);
	private IProfileManager profileManager = null;
	private AmsCustomerModifySocialRequestWraper wraper;
	
	public AmsCustomerModifySocialTask(AmsCustomerModifySocialRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		RpcMessage response = null;
		try {
			boolean result = false;
			
			AmsCustomerModifySocialRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerModifySocialRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			if (validateModifySC(request.getCustomerServiceId())) {
				ModifyAccountResult modifyAccountResult = profileManager.modifySocialAccount(request);
				log.info("modify SocialAccount Result: " + modifyAccountResult);
				
				if (modifyAccountResult == ModifyAccountResult.SUCCESS)
					result = true;
				
			} else {
				result = true;
				log.info("[AmsCustomerModifySocial] Customer has customerServiceId: " + request.getCustomerServiceId() + " not in list test account, response result = true");
			}

			//Response to client
			response = createRpcMessage(result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = createRpcMessage(false);
		} finally {
			AmsApiControllerMng.getAmsCustomerBEResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerModifySocialRequest, requestId: " + wraper.getResponseBuilder().getId());
			
			AmsApiControllerMng.getAmsCustomerModifySocialProcessor().onComplete(wraper);
		}
	}
	
	public RpcMessage createRpcMessage(boolean result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_MODIFY_SOCIAL_RESPONSE);
		
		AmsCustomerModifySocialResponse.Builder amsCustomerInfoResponse = AmsCustomerModifySocialResponse.newBuilder();
		amsCustomerInfoResponse.setResult(result ? AmsCustomerModifySocialResponse.Result.SUCCESS : AmsCustomerModifySocialResponse.Result.FAIL);
		response.setPayloadData(amsCustomerInfoResponse.build().toByteString());
		response.setResult(result ? Result.SUCCESS : Result.FAILED);
		
		return response.build();
	}
	
	public boolean validateModifySC(final String customerServiceId) {
		if (SystemPropertyConfig.getListCustomerTestInternalSc().isEmpty())
			return true;
		
		String customerId = profileManager.getCustomerIdByCustomerServiceId(customerServiceId);
		if (customerId != null && Helper.isTestInternalScCustomer(customerId))
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