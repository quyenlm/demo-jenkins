/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerAgreementNewsWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Apr 12, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerAgreementNewsProcessor extends AbstractApiProcessor<AmsCustomerAgreementNewsWraper> {
	private ISocialManager socialManager;
	private IProfileManager profileManager = null;
	
	@Override
	public void onMessage(AmsCustomerAgreementNewsWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerAgreementNewsTask newsTask = new AmsCustomerAgreementNewsTask(wraper);
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
	public void onComplete(AmsCustomerAgreementNewsWraper wraper) {
		super.onComplete(wraper);
	}

}