package com.nts.ams.api.controller.customer.processor;

import java.util.ArrayList;
import java.util.List;

import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;
import phn.nts.ams.fe.domain.CustomerInfo;
import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.SourceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description Handle AmsCustomerNews Task
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerNewsTask extends Thread {
	private static Logit log = Logit.getInstance(AmsCustomerNewsTask.class);
	private AmsCustomerNewsRequestWraper wraper;
	private ISocialManager socialManager;
	private IProfileManager profileManager = null;
	private List<AmsMessage> amsMessageLst = new ArrayList<AmsMessage>();
	public AmsCustomerNewsTask(AmsCustomerNewsRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerNewsRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerNewsRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			Integer readFlag = null;
			Integer serviceType = null;
			Integer confirmFlg = null;
			Integer messageKind = null;
			Integer pageSize = null;
			Integer offset = null;
			
			if(request.hasReadFlg())
				readFlag = request.getReadFlg().getNumber();
			if(request.hasServiceType())
				serviceType = request.getServiceType().getNumber();
			if(request.hasConfirmFlg())
				confirmFlg = request.getConfirmFlg().getNumber();
			if(request.hasMessageKind())
				messageKind = request.getMessageKind().getNumber();
			if(request.hasOffset())
				offset = request.getOffset();
			if(request.hasPageSize())
				pageSize = request.getPageSize();
			
			AmsCustomerNewsResponse.Builder amsCustomerNewsResponse = AmsCustomerNewsResponse.newBuilder();
			
			String customerId = null;
			
			//Get News/Agreement for NTD by NtdCustomerId -> get customerId by NtdCustomerId
			if(request.hasSourceType() && request.getSourceType() == SourceType.NTD_FX_MOBILE) {
				log.info("Get CustomerId by NtdCustomerId: " + request.getCustomerId());
				CustomerInfo customerInfo = profileManager.getCustomerInfoByNtdCustomerId(request.getCustomerId());
				if(customerInfo == null) {
					log.warn("Not found any CustomerId has NtdCustomerId: " + request.getCustomerId());
				} else {
					customerId = customerInfo.getCustomerId();
					log.info("Got CustomerId " + customerId + " mapping with NtdCustomerId: " + request.getCustomerId());
				}
			} else
				customerId = request.getCustomerId();
			
			RpcMessage response = null;
			
			if(customerId != null) {
				//Load news from DB
				amsMessageLst = getSocialManager().getAgreementInfo(amsCustomerNewsResponse, customerId, request.getMessageType().getNumber(), 
						offset, pageSize, readFlag, serviceType, confirmFlg, messageKind);
				log.info("Total News/Agreement size: " + amsCustomerNewsResponse.getTotalRecords());
				
				if(amsMessageLst != null) {
					for (AmsMessage amsMessage : amsMessageLst) {
						AmsNewsInfo amsNewsInfo = Converter.convertAmsMessage(amsMessage);
						amsCustomerNewsResponse.addNewsInfo(amsNewsInfo);
						log.debug(String.valueOf(amsNewsInfo));
					}
				} else
					log.info("Not found News/Agreement");
				
				response = createRpcMessage(amsCustomerNewsResponse, true);
			} else
				response = createRpcMessage(amsCustomerNewsResponse, false);
			
			//Response to client
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerNewsRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			RpcMessage response = createRpcMessage(AmsCustomerNewsResponse.newBuilder(), false);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
		} finally {
			AmsApiControllerMng.getAmsCustomerNewsProcessor().onComplete(wraper);
		}
	}

	public RpcMessage createRpcMessage(AmsCustomerNewsResponse.Builder amsCustomerNewsResponse, boolean result) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_RESPONSE);
		
		//Set mypage Url
		if(AmsApiControllerMng.getConfiguration().getMypageUrl() != null)
			amsCustomerNewsResponse.setMypageUrl(AmsApiControllerMng.getConfiguration().getMypageUrl());
				
		response.setPayloadData(amsCustomerNewsResponse.build().toByteString());
		
		if(result){
			response.setResult(Result.SUCCESS);
		}else{
			response.setResult(Result.RECORD_NOT_FOUND);
		}
		
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