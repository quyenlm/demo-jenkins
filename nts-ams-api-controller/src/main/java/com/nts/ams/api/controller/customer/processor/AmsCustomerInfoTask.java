package com.nts.ams.api.controller.customer.processor;

import java.util.List;

import phn.com.nts.db.entity.BoCustomer;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;

import com.nts.ams.api.controller.customer.bean.CustomerInfoRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoTestStatus;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.CustomerServiceStatus;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.FirstLoginStatus;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description Handle AmsCustomerInfoRequest
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerInfoTask implements Runnable {
	private Logit log = Logit.getInstance(AmsCustomerInfoTask.class);
	private IProfileManager profileManager = null;
	private CustomerInfoRequestWraper wraper;
	
	public AmsCustomerInfoTask(CustomerInfoRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerInfoRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerInfoRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			AmsCustomerInfo amsCustomerInfo = null;
			if(validateRequest(request) == Result.SUCCESS){
				CustomerInfo customerInfo = null;
				
				//Get data from DB
				if(request.hasCustomerId())
					customerInfo = getProfileManager().getCustomerInfo(request.getCustomerId());
				else
					customerInfo = getProfileManager().getCustomerInfoByLoginId(request.getLoginId());
				
				log.info("Got CustomerInfo from DB: " + customerInfo);
				
				if(customerInfo != null) {
					//Convert to AmsCustomerInfo
					amsCustomerInfo = Converter.convertCustomerInfo(customerInfo);
					AmsCustomerInfo.Builder amsCustomerInfoBuilder = amsCustomerInfo.toBuilder();
					
					if(request.hasServiceType()) {
						//Get by serviceType
						CustomerServicesInfo serviceInfo = getProfileManager().getCustomerService(customerInfo.getCustomerId(), request.getServiceType().getNumber());
						
						if(serviceInfo != null) {
							ServiceTypeInfo.Builder serviceTypeInfoBuilder  = Converter.convertCustomerServicesInfo(serviceInfo).toBuilder();
							
							if(ServiceType.BO.equals(request.getServiceType())) {
								//Get BO addition info
								BoCustomer boCustomer = getProfileManager().getBoCustomer(customerInfo.getCustomerId());
								
								if(boCustomer != null) {
									if(!CustomerServiceStatus.BEFORE_REGISTER.equals(serviceTypeInfoBuilder.getCustomerServiceStatus()) 
											&&  boCustomer.getBoTestStatus() != null)
										serviceTypeInfoBuilder.setBoTestStatus(BoTestStatus.valueOf(boCustomer.getBoTestStatus()));
										
									if(boCustomer.getFirstLoginFlg() != null)
										serviceTypeInfoBuilder.setFirstLogin(FirstLoginStatus.valueOf(boCustomer.getFirstLoginFlg()));
									else
										serviceTypeInfoBuilder.setFirstLogin(FirstLoginStatus.STATUS_FASLE);
								} else
									log.warn("Not found boCustomer, customerId: " + customerInfo.getCustomerId());
							}
							
							amsCustomerInfoBuilder.addServiceTypeInfo(serviceTypeInfoBuilder);
						} else
							log.warn("Not found customerService: " + request.getServiceType() + ", customerId: " + customerInfo.getCustomerId());
						
					} else {
						//Get all Customser Service
						List<CustomerServicesInfo> listServiceType = getProfileManager().getCustomerService(request.getCustomerId());
						for (CustomerServicesInfo serviceTypeInfo : listServiceType) {
							amsCustomerInfoBuilder.addServiceTypeInfo(Converter.convertCustomerServicesInfo(serviceTypeInfo));
						}
					}
					
					//Set MyPage/Social avata
					if(AmsApiControllerMng.getConfiguration().getMypageAvataUrl() != null)
						amsCustomerInfoBuilder.setAvatar(String.format(AmsApiControllerMng.getConfiguration().getMypageAvataUrl(), amsCustomerInfoBuilder.getCustomerId()));
					
					//Build amsCustomerInfo
					amsCustomerInfo = amsCustomerInfoBuilder.build();
					log.info("Converted AmsCustomerInfo: " + amsCustomerInfo);
				} else
					log.warn("Not found customerId: " + request.getCustomerId());
			} else
				log.warn("Invalid Request, can not get CustomerInfo from DB");
			
			//Response to client
			RpcMessage response = createRpcMessage(amsCustomerInfo);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerInfoRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerInfoProcessor().onComplete(wraper);
		}
	}
	
	public Result validateRequest(AmsCustomerInfoRequest request) {
		if(request == null)
			return Result.INTERNAL_ERROR;
		
		if(!request.hasCustomerId() && !request.hasLoginId()) {
			log.info("Must be set CustomerId or LoginId");
			return Result.RECORD_NOT_FOUND;
		}
		return Result.SUCCESS;
	}
	
	public RpcMessage createRpcMessage(AmsCustomerInfo amsCustomerInfo) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_RESPONSE);
		
		if(amsCustomerInfo != null) {
			AmsCustomerInfoResponse.Builder amsCustomerInfoResponse = AmsCustomerInfoResponse.newBuilder();
			amsCustomerInfoResponse.setCustomerInfo(amsCustomerInfo);
			response.setPayloadData(amsCustomerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(Result.RECORD_NOT_FOUND);
		}
		
		return response.build();
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}