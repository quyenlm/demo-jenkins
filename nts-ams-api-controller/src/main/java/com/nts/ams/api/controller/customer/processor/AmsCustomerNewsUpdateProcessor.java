/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerNewsUpdateRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description Manage AmsCustomerNewsUpdate Processor
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerNewsUpdateProcessor extends AbstractApiProcessor<AmsCustomerNewsUpdateRequestWraper> {
	protected ISocialManager socialManager;

	@Override
	public void onMessage(AmsCustomerNewsUpdateRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerNewsUpdateTask newsTask = new AmsCustomerNewsUpdateTask(wraper);
		newsTask.setSocialManager(getSocialManager());
		getExecutorService().submit(newsTask);
	}

	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}

	@Override
	public void onComplete(AmsCustomerNewsUpdateRequestWraper wraper) {
		super.onComplete(wraper);
	}
	
}