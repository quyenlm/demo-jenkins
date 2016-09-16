/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description Manage AmsCustomerNews Processor
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerNewsProcessor extends AbstractApiProcessor<AmsCustomerNewsRequestWraper> {
	private ISocialManager socialManager;
	private IProfileManager profileManager = null;
	
	@Override
	public void onMessage(AmsCustomerNewsRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerNewsTask newsTask = new AmsCustomerNewsTask(wraper);
		newsTask.setSocialManager(getSocialManager());
		newsTask.setProfileManager(getProfileManager());
		getExecutorService().submit(newsTask);
	}

	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	@Override
	public void onComplete(AmsCustomerNewsRequestWraper wraper) {
		super.onComplete(wraper);
	}

}