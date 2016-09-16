/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerModifySocialRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerModifySocialProcessor extends AbstractApiProcessor<AmsCustomerModifySocialRequestWraper>  {
	private IProfileManager profileManager;
	
	@Override
	public void onMessage(AmsCustomerModifySocialRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerModifySocialTask orderTask = new AmsCustomerModifySocialTask(wraper);
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
	public void onComplete(AmsCustomerModifySocialRequestWraper wraper) {
		super.onComplete(wraper);
	}
}