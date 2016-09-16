/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerRegisterSocialRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerRegisterSocialProcessor extends AbstractApiProcessor<AmsCustomerRegisterSocialRequestWraper>  {
	private IProfileManager profileManager;
	
	@Override
	public void onMessage(AmsCustomerRegisterSocialRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerRegisterSocialTask orderTask = new AmsCustomerRegisterSocialTask(wraper);
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
	public void onComplete(AmsCustomerRegisterSocialRequestWraper wraper) {
		super.onComplete(wraper);
	}
}