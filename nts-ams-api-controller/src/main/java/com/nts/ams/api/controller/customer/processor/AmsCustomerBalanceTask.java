package com.nts.ams.api.controller.customer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.domain.BalanceInfo;

import com.nts.ams.api.controller.customer.bean.CustomerBalanceRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsBalanceInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description Handle AmsCustomerBalanceRequest
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerBalanceTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsCustomerBalanceTask.class);
	private CustomerBalanceRequestWraper wraper;
	private IBalanceManager balanceManager;
	private ExecutorService executorService;
	
	private CountDownLatch doneSignal;
	private List<AmsBalanceInfo> listBalanceInfo = new ArrayList<AmsBalanceInfo>();
	
	public AmsCustomerBalanceTask(CustomerBalanceRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerBalanceRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerBalanceRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			if(request.getServiceTypeInfoCount() > 0) {
				doneSignal = new CountDownLatch(request.getServiceTypeInfoCount());
				
				//Send to get balance
				for(ServiceTypeInfo service : request.getServiceTypeInfoList()) {
					GetBalanceSubTask subTask = new GetBalanceSubTask(request.getCustomerId(), service, wraper.getResponseBuilder().getId());
					executorService.submit(subTask);				
				}
				
				//Wait for all Balance response
				doneSignal.await(AmsApiControllerMng.getConfiguration().getBalanaceTimeOut(), TimeUnit.MILLISECONDS);
			}
			
			//Response to client
			RpcMessage response = null;
			synchronized (listBalanceInfo) {
				response = createRpcMessage(listBalanceInfo);
			}
			
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			log.info("[end] handle AmsCustomerBalanceRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerBalanceProcessor().onComplete(wraper);
		}
	}

	/**
	 * @description GetBalanceSubTask - Get balance for once service
	 * @version NTS
	 * @author quyen.le.manh
	 * @CrDate Jul 21, 2015
	 * @Copyright Nextop Asia Limited. All rights reserved.
	 */
	private class GetBalanceSubTask implements Runnable {
		private ServiceTypeInfo service;
		private String customerId;
		private String requestId;
		
		public GetBalanceSubTask(String customerId, ServiceTypeInfo service, String requestId) {
			this.customerId = customerId;
			this.service = service;
			this.requestId = requestId;
		}
		
		@Override
		public void run() {
			try {
				log.info("[requestId: " + requestId + "] [start] Get BalanceInfo, ServiceType: " + service.getServiceType()  + ", CustomerServiceId: " + service.getCustomerServiceId());
				BalanceInfo balanceInfo = getBalanceManager().getBalanceInfo(customerId, service.getServiceType().getNumber(), service.getCurrencyCode());
				log.info("[requestId: " + requestId + "] [end] Get BalanceInfo, ServiceType: " + service.getServiceType() + ", BalanceInfo: " + balanceInfo);
				
				AmsBalanceInfo amsBalanceInfo = null;
				if(balanceInfo != null) {
					//convert to AmsBalanceInfo
					amsBalanceInfo = Converter.convertBalanceInfo(balanceInfo, service.getServiceType().getNumber());
				} else {
					amsBalanceInfo = AmsBalanceInfo.newBuilder().setCustomerId(customerId)
																.setServiceType(String.valueOf(service.getServiceType().getNumber()))
																.setResult(AmsBalanceInfo.Result.TIMED_OUT).build();
				}
				
				log.info("[requestId: " + requestId +"] Converted AmsBalanceInfo: " + amsBalanceInfo);
				
				synchronized (listBalanceInfo) {
					listBalanceInfo.add(amsBalanceInfo);
				}
			} catch (Exception e) {
				log.error("[requestId: " + requestId +"] " + e.getMessage(), e);
			} finally {
				doneSignal.countDown();
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

	public RpcMessage createRpcMessage(List<AmsBalanceInfo> listBalanceInfo) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_RESPONSE);
		
		if(listBalanceInfo != null && listBalanceInfo.size() > 0) {
			AmsCustomerBalanceResponse.Builder balanceResponse = AmsCustomerBalanceResponse.newBuilder();
			balanceResponse.addAllBalanceInfo(listBalanceInfo);
			response.setPayloadData(balanceResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(Result.RECORD_NOT_FOUND);
		}
		
		return response.build();
	}
	
	public void setBalanceManager(IBalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    public IBalanceManager getBalanceManager() {
        return balanceManager;
    }

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}