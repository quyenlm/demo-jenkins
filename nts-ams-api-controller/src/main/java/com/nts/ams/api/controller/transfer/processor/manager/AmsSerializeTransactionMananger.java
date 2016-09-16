package com.nts.ams.api.controller.transfer.processor.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;

import com.nts.ams.api.controller.common.bean.RequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsDepositRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsDepositUpdateRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsTransferRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalCancelRequestWraper;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalRequestWraper;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 22, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsSerializeTransactionMananger extends Thread {
	private static Logit log = Logit.getInstance(AmsSerializeTransactionMananger.class);
	
	//Key = customerId
	private static ConcurrentHashMap<String, LinkedBlockingQueue<RequestWraper<?>>> processor = new ConcurrentHashMap<String, LinkedBlockingQueue<RequestWraper<?>>>();
	private static LinkedBlockingQueue<RequestWraper<?>> queueMsg = new LinkedBlockingQueue<RequestWraper<?>>();
	
	public void run() {
		while(true) {
			try {
				RequestWraper<?> request = queueMsg.poll(100, TimeUnit.MICROSECONDS);
				if(request != null)
					deliver(request);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public void onMessage(RequestWraper<?> wraper) {
		if(wraper == null)
			return;
		try {
			queueMsg.put(wraper);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void deliver(RequestWraper<?> wraper) throws InterruptedException {
		String customerId = wraper.getCustomerId();
		if(StringUtil.isEmpty(customerId)) {
			log.warn("CustomerId is empty, wraper: " + wraper);
			return;
		}
		
		synchronized(processor) {
			LinkedBlockingQueue<RequestWraper<?>> listRequest = processor.get(customerId);
			if(listRequest == null) {
				listRequest = new LinkedBlockingQueue<RequestWraper<?>>();
				processor.put(customerId, listRequest);
			}
			listRequest.put(wraper);
			
			if(listRequest.size() == 1) {
				//Start handle current wraper
				RequestWraper<?> currentWraper = listRequest.peek();
				startHandleRequest(currentWraper);
			}
		}
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 22, 2015
	 * @MdDate
	 */
	public void onComplete(RequestWraper<?> wraper) {
		try {
			String customerId = wraper.getCustomerId();
			
			synchronized(processor) {
				LinkedBlockingQueue<RequestWraper<?>> listRequest = processor.get(customerId);
				if(listRequest != null) {
					if(listRequest.size() > 1) {
						//Remove First wraper from queue
						Object firstWraper = listRequest.poll();
						log.info("removed Wraper from queue: " + firstWraper);
						
						//Start handle next wraper
						RequestWraper<?> nextWraper = listRequest.peek();
						startHandleRequest(nextWraper);
					} else {
						//Remove Request queue of customerId
						processor.remove(customerId);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 22, 2015
	 * @MdDate
	 */
	private void startHandleRequest(RequestWraper<?> wraper) {
		if(wraper == null)
			return;
		try {
			if (wraper instanceof AmsTransferRequestWraper) {
				//Transfer
				AmsApiControllerMng.getAmsTransferProcessor().onMessage((AmsTransferRequestWraper) wraper);
			} else if (wraper instanceof AmsDepositRequestWraper) {
				//Deposit
				AmsApiControllerMng.getAmsDepositProcessor().onMessage((AmsDepositRequestWraper) wraper);
			} else if (wraper instanceof AmsDepositUpdateRequestWraper) {
				//Deposit update
				AmsApiControllerMng.getAmsDepositUpdateProcessor().onMessage((AmsDepositUpdateRequestWraper) wraper);
			} else if (wraper instanceof AmsWithdrawalCancelRequestWraper) {
				//Deposit cancel
				AmsApiControllerMng.getAmsWithdrawalCancelProcessor().onMessage((AmsWithdrawalCancelRequestWraper) wraper);
			} else if (wraper instanceof AmsWithdrawalRequestWraper) {
				//Withdrawal
				AmsApiControllerMng.getAmsWithdrawalProcessor().onMessage((AmsWithdrawalRequestWraper) wraper);
			} else
				log.warn("Not Supported handle: " + wraper);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
}