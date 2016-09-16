package com.nts.ams.api.controller.customer.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.customer.bean.CustomerInfoUpdateRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description AmsCustomerInfoUpdate Processor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerInfoUpdateProcessor extends AbstractApiProcessor<CustomerInfoUpdateRequestWraper>{
	private static Logit log = Logit.getInstance(AmsCustomerInfoUpdateProcessor.class);
	
	//Key = customerId
	private static ConcurrentHashMap<String, LinkedBlockingQueue<CustomerInfoUpdateRequestWraper>> processor = new ConcurrentHashMap<String, LinkedBlockingQueue<CustomerInfoUpdateRequestWraper>>();
	private IAccountManager accountManager = null;
	private IProfileManager profileManager = null;
	
	@Override
	public void onMessage(CustomerInfoUpdateRequestWraper wraper) throws InterruptedException {
		synchronized(processor) {
			String customerId = wraper.getRequest().getCustomerInfo().getCustomerId();
			
			LinkedBlockingQueue<CustomerInfoUpdateRequestWraper> listRequest = processor.get(customerId);
			if(listRequest == null) {
				listRequest = new LinkedBlockingQueue<CustomerInfoUpdateRequestWraper>();
				processor.put(customerId, listRequest);
			}
			listRequest.put(wraper);
			
			if(listRequest.size() == 1) {
				//Start handle current wraper
				CustomerInfoUpdateRequestWraper currentWraper = listRequest.peek();
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
	public void onComplete(CustomerInfoUpdateRequestWraper wraper) {
		super.onComplete(wraper);
		
		try {
			synchronized(processor) {
				String customerId = wraper.getRequest().getCustomerInfo().getCustomerId();
				LinkedBlockingQueue<CustomerInfoUpdateRequestWraper> listRequest = processor.get(customerId);
				if(listRequest != null) {
					if(listRequest.size() > 1) {
						//Remove First wraper from queue
						CustomerInfoUpdateRequestWraper firstWraper = listRequest.poll();
						log.info("removed Wraper from queue: " + firstWraper);
						
						//Start handle next wraper
						CustomerInfoUpdateRequestWraper nextWraper = listRequest.peek();
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
	private void startHandleRequest(CustomerInfoUpdateRequestWraper wraper) {
		AmsCustomerInfoUpdateTask task = new AmsCustomerInfoUpdateTask(wraper);
		task.setAccountManager(getAccountManager());
		task.setProfileManager(getProfileManager());
		task.setExecutorService(getExecutorService());
		getExecutorService().submit(task);
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}