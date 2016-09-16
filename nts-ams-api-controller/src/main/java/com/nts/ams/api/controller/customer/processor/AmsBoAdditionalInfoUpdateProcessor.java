package com.nts.ams.api.controller.customer.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.ITestBoManager;

import com.nts.ams.api.controller.customer.bean.AmsBoAdditionalInfoUpdateRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description AmsCustomerInfoUpdate Processor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsBoAdditionalInfoUpdateProcessor extends AbstractApiProcessor<AmsBoAdditionalInfoUpdateRequestWraper>{
	private static Logit log = Logit.getInstance(AmsBoAdditionalInfoUpdateProcessor.class);
	
	//Key = customerId
	private static ConcurrentHashMap<String, LinkedBlockingQueue<AmsBoAdditionalInfoUpdateRequestWraper>> processor = new ConcurrentHashMap<String, LinkedBlockingQueue<AmsBoAdditionalInfoUpdateRequestWraper>>();
	private ITestBoManager testBoManager = null;
	
	@Override
	public void onMessage(AmsBoAdditionalInfoUpdateRequestWraper wraper) throws InterruptedException {
		synchronized(processor) {
			String customerId = wraper.getRequest().getCustomerId();
			
			LinkedBlockingQueue<AmsBoAdditionalInfoUpdateRequestWraper> listRequest = processor.get(customerId);
			if(listRequest == null) {
				listRequest = new LinkedBlockingQueue<AmsBoAdditionalInfoUpdateRequestWraper>();
				processor.put(customerId, listRequest);
			}
			listRequest.put(wraper);
			
			if(listRequest.size() == 1) {
				//Start handle current wraper
				AmsBoAdditionalInfoUpdateRequestWraper currentWraper = listRequest.peek();
				startHandleRequest(currentWraper);
			}
		}
	}
	
	/**
	 * Remove first request and start handle next request (if has)
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 8, 2015
	 * @MdDate
	 */
	public void onComplete(AmsBoAdditionalInfoUpdateRequestWraper wraper) {
		super.onComplete(wraper);
		
		try {
			synchronized(processor) {
				String customerId = wraper.getRequest().getCustomerId();
				LinkedBlockingQueue<AmsBoAdditionalInfoUpdateRequestWraper> listRequest = processor.get(customerId);
				if(listRequest != null) {
					if(listRequest.size() > 1) {
						//Remove First wraper from queue
						AmsBoAdditionalInfoUpdateRequestWraper firstWraper = listRequest.poll();
						log.info("removed Wraper from queue: " + firstWraper);
						
						//Start handle next wraper
						AmsBoAdditionalInfoUpdateRequestWraper nextWraper = listRequest.peek();
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
	 * Start Handle Request　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 8, 2015
	 * @MdDate
	 */
	private void startHandleRequest(AmsBoAdditionalInfoUpdateRequestWraper wraper) {
		AmsBoAdditionalInfoUpdateRequestTask task = new AmsBoAdditionalInfoUpdateRequestTask(wraper);
		task.setTestBoManager(testBoManager);
		task.setExecutorService(getExecutorService());
		getExecutorService().submit(task);
	}

	public ITestBoManager getTestBoManager() {
		return testBoManager;
	}

	public void setTestBoManager(ITestBoManager testBoManager) {
		this.testBoManager = testBoManager;
	}
}