package com.nts.ams.api.controller.customer.processor;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.IReportManager;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.ntd.NTDManager;
import phn.nts.ams.utils.Helper;

import com.nts.ams.api.controller.customer.bean.AmsCustomerReportsRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsCustomerReportsTask
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jan 6, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerReportsTask implements Runnable {
	private static final Logit log = Logit.getInstance(AmsCustomerReportsTask.class);
	
	private AmsCustomerReportsRequestWraper wraper;
	private IProfileManager profileManager;
	private IReportManager reportManager;
	
	private static final Integer TRADING_TYPE_AMS = 1;
	private static final Integer TRADING_TYPE_BO = 2;
	
	public AmsCustomerReportsTask(AmsCustomerReportsRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerReportsRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerReportsRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			AmsCustomerReportsResponse amsCustomerReportsResponse = null;
			
			if (request.hasServiceType() && ServiceType.NTD_FX.equals(request.getServiceType())) {

				amsCustomerReportsResponse = getCustomerReportNtdFx(request);

			} else if (request.hasServiceType() && ServiceType.BO.equals(request.getServiceType())) {
				
				amsCustomerReportsResponse = getCustomerReportBo(request);
				
			} else if (request.hasServiceType() && ServiceType.AMS.equals(request.getServiceType())) {
				
				amsCustomerReportsResponse = getCustomerReportAms(request);
				
			} else if (request.hasServiceType() && ServiceType.SC.equals(request.getServiceType())) {
				
				if (Helper.validateRequestToSC(request.getCustomerId())) {
					amsCustomerReportsResponse = getCustomerReportSc(request);
				} else {
					log.info("CustomerId not in test account list, get report as Ams, customerId: " + request.getCustomerId() + ", serviceType: " + request.getServiceType());
					amsCustomerReportsResponse = getCustomerReportAms(request);
				}
				
			} else {
				log.warn("Not support get customerReport for ServiceType: " + request.getServiceType());
			}

			//Response to client
			RpcMessage response  = createRpcMessage(amsCustomerReportsResponse);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerReportsRequest, requestId: " + wraper.getResponseBuilder().getId());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerReportsProcessor().onComplete(wraper);
		}
	}

	private AmsCustomerReportsResponse getCustomerReportNtdFx(AmsCustomerReportsRequest request) {
		AmsCustomerReportsResponse amsCustomerReportsResponse = null;
		//Get by serviceType
		CustomerServicesInfo serviceInfo = profileManager.getCustomerService(request.getCustomerId(), request.getServiceType().getNumber());
		if(serviceInfo != null) {
			log.warn("get customerReport for customerId: " + request.getCustomerId() + ", ntdAccountId: " + serviceInfo.getNtdAccountId());
			amsCustomerReportsResponse = NTDManager.getInstance().getCustomerReports(serviceInfo.getNtdAccountId(), request);
			if(amsCustomerReportsResponse != null) {
				log.info("received customerReport for customerId: " + amsCustomerReportsResponse.getCustomerId() 
						+ ", size: " + amsCustomerReportsResponse.getCustomerReportsCount() 
						+ ", totalRecords: " + amsCustomerReportsResponse.getTotalRecords());
			}
		} else {
			log.warn("Not found customerService: " + request.getServiceType() + " of customerId: " + request.getCustomerId());
		}
		return amsCustomerReportsResponse;
	}
	
	private AmsCustomerReportsResponse getCustomerReportBo(AmsCustomerReportsRequest request) {
		return reportManager.getCustomerReportBoOrAms(request, TRADING_TYPE_BO);
	}
	
	private AmsCustomerReportsResponse getCustomerReportAms(AmsCustomerReportsRequest request) {
		return reportManager.getCustomerReportBoOrAms(request, TRADING_TYPE_AMS);
	}

	private AmsCustomerReportsResponse getCustomerReportSc(AmsCustomerReportsRequest request) {
		return reportManager.getCustomerReportSc(request);
	}

	public RpcMessage createRpcMessage(AmsCustomerReportsResponse amsCustomerReportsResponse) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_RESPONSE);
		
		if(amsCustomerReportsResponse != null) {
			response.setPayloadData(amsCustomerReportsResponse.toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(Result.RECORD_NOT_FOUND);
		}
		
		return response.build();
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	public void setReportManager(IReportManager reportManager) {
		this.reportManager = reportManager;
	}
}