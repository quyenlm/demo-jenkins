package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerAgreementNewsWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.ams.bean.AmsCustomerNews;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerAgreementNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerAgreementNewsResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Apr 12, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerAgreementNewsTask extends Thread {
	private static Logit log = Logit.getInstance(AmsCustomerAgreementNewsTask.class);
	private AmsCustomerAgreementNewsWraper wraper;
	private ISocialManager socialManager;
	private IProfileManager profileManager = null;
	public AmsCustomerAgreementNewsTask(AmsCustomerAgreementNewsWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerAgreementNewsRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerAgreementNewsRequest " + request);
			
			RpcMessage response = null;
			
			if(!StringUtil.isEmpty(request.getCustomerId())) {
				//Load news from Cache
				AmsCustomerNews agreementNews = AmsApiControllerMng.getDataCache().getAmsCustomerNews(request.getCustomerId());
				log.info("Loaded AmsCustomerNews: " + agreementNews);
				
				response = createRpcMessage(agreementNews);
			} else {
				log.warn("Invalid customerId: " + request.getCustomerId());
				response = createRpcMessage(null);
			}
			
			//Response to client
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerAgreementNewsRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			RpcMessage response = createRpcMessage(null);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
		} finally {
			AmsApiControllerMng.getAmsCustomerAgreementNewsProcessor().onComplete(wraper);
		}
	}

	public RpcMessage createRpcMessage(AmsCustomerNews agreementNews) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_AGREEMENT_NEWS_RESPONSE);
		
		if(agreementNews != null) {
			AmsCustomerAgreementNewsResponse.Builder amsCustomerNewsResponse = AmsCustomerAgreementNewsResponse.newBuilder();
			amsCustomerNewsResponse.setCustomerId(agreementNews.getCustomerId());
			amsCustomerNewsResponse.setAgreementNews(agreementNews.getLstAgreement() != null ? agreementNews.getLstAgreement().size() : 0 );
			amsCustomerNewsResponse.setReAgreementNews(agreementNews.getLstReAgreement() != null ? agreementNews.getLstReAgreement().size() : 0 );
			
			response.setPayloadData(amsCustomerNewsResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else
			response.setResult(Result.RECORD_NOT_FOUND);
		
		return response.build();
	}

	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
}