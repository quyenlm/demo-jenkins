package com.nts.ams.api.controller.customer.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ITestBoManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerBoTestUpdateRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 27, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerBoTestUpdateProcessor extends AbstractApiProcessor<AmsCustomerBoTestUpdateRequestWraper>{
	private static Logit log = Logit.getInstance(AmsCustomerBoTestUpdateProcessor.class);
	
	//Key = customerId
	private static ConcurrentHashMap<String, LinkedBlockingQueue<AmsCustomerBoTestUpdateRequestWraper>> processor = new ConcurrentHashMap<String, LinkedBlockingQueue<AmsCustomerBoTestUpdateRequestWraper>>();
	private IAccountManager accountManager = null;
	private ITestBoManager testBoManager = null;
	
	@Override
	public void onMessage(AmsCustomerBoTestUpdateRequestWraper wraper) throws InterruptedException {
		synchronized(processor) {
			String customerId = wraper.getRequest().getCustomerId();
			
			LinkedBlockingQueue<AmsCustomerBoTestUpdateRequestWraper> listRequest = processor.get(customerId);
			if(listRequest == null) {
				listRequest = new LinkedBlockingQueue<AmsCustomerBoTestUpdateRequestWraper>();
				processor.put(customerId, listRequest);
			}
			listRequest.put(wraper);
			
			if(listRequest.size() == 1) {
				//Start handle current wraper
				AmsCustomerBoTestUpdateRequestWraper currentWraper = listRequest.peek();
				startHandleRequest(currentWraper);
			}
		}
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 27, 2015
	 * @MdDate
	 */
	public void onComplete(AmsCustomerBoTestUpdateRequestWraper wraper) {
		super.onComplete(wraper);
		
		try {
			synchronized(processor) {
				String customerId = wraper.getRequest().getCustomerId();
				LinkedBlockingQueue<AmsCustomerBoTestUpdateRequestWraper> listRequest = processor.get(customerId);
				if(listRequest != null) {
					if(listRequest.size() > 1) {
						//Remove First wraper from queue
						AmsCustomerBoTestUpdateRequestWraper firstWraper = listRequest.poll();
						log.info("removed Wraper from queue: " + firstWraper);
						
						//Start handle next wraper
						AmsCustomerBoTestUpdateRequestWraper nextWraper = listRequest.peek();
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
	 * @CrDate Jul 27, 2015
	 * @MdDate
	 */
	private void startHandleRequest(AmsCustomerBoTestUpdateRequestWraper wraper) {
		AmsCustomerBoTestUpdateTask task = new AmsCustomerBoTestUpdateTask(wraper);
		task.setAccountManager(getAccountManager());
		task.setTestBoManager(getTestBoManager());
		task.setExecutorService(getExecutorService());
		getExecutorService().submit(task);
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public ITestBoManager getTestBoManager() {
		return testBoManager;
	}

	public void setTestBoManager(ITestBoManager testBoManager) {
		this.testBoManager = testBoManager;
	}
}