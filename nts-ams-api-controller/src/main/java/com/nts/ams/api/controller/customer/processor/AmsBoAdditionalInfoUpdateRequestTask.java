package com.nts.ams.api.controller.customer.processor;

import java.util.concurrent.ExecutorService;

import phn.com.components.trs.api.CRMIntegrationAPI;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.ITestBoManager;

import com.nts.ams.api.controller.customer.bean.AmsBoAdditionalInfoUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsBoAdditionalInfoUpdateRequest Task
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 10, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsBoAdditionalInfoUpdateRequestTask implements Runnable {
	private Logit log = Logit.getInstance(AmsBoAdditionalInfoUpdateRequestTask.class);
	private AmsBoAdditionalInfoUpdateRequestWraper wraper;
	private ITestBoManager testBoManager = null;
	
	private ExecutorService executorService;
	private String requestId;
	
	public AmsBoAdditionalInfoUpdateRequestTask(AmsBoAdditionalInfoUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsBoAdditionalInfoUpdateRequest request = wraper.getRequest();
			
			requestId = wraper.getResponseBuilder().getId();
			log.info("[start] handle AmsBoAdditionalInfoUpdateRequest, requestId: " + wraper.getResponseBuilder().getId() +  ", " + request);
			
			//Update to DB
			AmsCustomerService amsCustomerService = testBoManager.updateAmsBoAdditionalInfo(request);
			
			//Sync to SaleForce
			if(amsCustomerService != null) {
				executorService.submit(new SyncSaleForceSubTask(amsCustomerService, requestId));
			}
			
			//Response to client	
			RpcMessage response = createRpcMessage(amsCustomerService);
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			
			log.info("[end] handle AmsBoAdditionalInfoUpdateRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsBoAdditionalInfoUpdateProcessor().onComplete(wraper);
		}
	}
	
	private class SyncSaleForceSubTask implements Runnable {
		AmsCustomerService amsCustomerService;
		private String requestId;
		public SyncSaleForceSubTask(AmsCustomerService amsCustomerService, String requestId) {
			this.amsCustomerService = amsCustomerService;
			this.requestId = requestId;
		}
		
		@Override
		public void run() {
			try {
				// Synchronize customer information to Salesforce
				log.info("[requestId: " + requestId + "] [start] syncBoTestStatusToSalesForce, CustomerServiceId: " 
						+ amsCustomerService.getCustomerServiceId());

				//update sale force
				boolean flag = CRMIntegrationAPI.syncBoTestStatusSF(amsCustomerService, amsCustomerService.getTestStatus());
				
				log.info("[requestId: " + requestId + "] [end] syncBoTestStatusToSalesForce, CustomerServiceId: " 
						+ amsCustomerService.getCustomerServiceId() + (flag ? ": SUCCESS" : "FAIL"));
			} catch (Exception e) {
				log.error("[requestId: " + requestId +"] " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Create RpcMessage to response to client　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public RpcMessage createRpcMessage(AmsCustomerService amsCustomerService) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_BO_ADDITIONAL_INFO_UPDATE_RESPONSE);
		
		if(amsCustomerService != null) {
			AmsBoAdditionalInfoUpdateResponse.Builder customerInfoResponse = AmsBoAdditionalInfoUpdateResponse.newBuilder();
			customerInfoResponse.setCustomerId(amsCustomerService.getCustomerServiceId());
			customerInfoResponse.setBoStatus(String.valueOf(amsCustomerService.getCustomerServiceStatus()));
			customerInfoResponse.setBoTestStatus(String.valueOf(amsCustomerService.getTestStatus()));
			
			response.setPayloadData(customerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(Result.FAILED);
			response.setMessageCode("MSG_TRS_NAF_0084");
		}
		
		return response.build();
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public ITestBoManager getTestBoManager() {
		return testBoManager;
	}

	public void setTestBoManager(ITestBoManager testBoManager) {
		this.testBoManager = testBoManager;
	}
}