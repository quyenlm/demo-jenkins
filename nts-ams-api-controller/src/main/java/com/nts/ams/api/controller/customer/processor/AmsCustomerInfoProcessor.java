/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.customer.bean.CustomerInfoRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description Manage CustomerInfoRequest
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerInfoProcessor extends AbstractApiProcessor<CustomerInfoRequestWraper>  {
	private IProfileManager profileManager;
	
	@Override
	public void onMessage(CustomerInfoRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerInfoTask orderTask = new AmsCustomerInfoTask(wraper);
		orderTask.setProfileManager(getProfileManager());
		getExecutorService().submit(orderTask);
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	@Override
	public void onComplete(CustomerInfoRequestWraper wraper) {
		super.onComplete(wraper);
	}
}