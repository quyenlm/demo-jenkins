/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerPaymentUpdateRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description Manage AmsCustomerBalanceRequest
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentUpdateProcessor extends AbstractApiProcessor<AmsCustomerPaymentUpdateRequestWraper> {
	protected IProfileManager profileManager;
	@Override
	public void onMessage(AmsCustomerPaymentUpdateRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerPaymentUpdateTask newsTask = new AmsCustomerPaymentUpdateTask(wraper);
		newsTask.setProfileManager(getProfileManager());
		getExecutorService().submit(newsTask);
	}

	@Override
	public void onComplete(AmsCustomerPaymentUpdateRequestWraper wraper) {
		super.onComplete(wraper);
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}