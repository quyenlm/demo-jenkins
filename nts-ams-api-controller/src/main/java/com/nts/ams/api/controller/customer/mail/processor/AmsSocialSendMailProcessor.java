/**
 * 
 */
package com.nts.ams.api.controller.customer.mail.processor;

import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IProfileManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 15, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsSocialSendMailProcessor extends AbstractApiProcessor<Object> {
	private IProfileManager profileManager;
	private IAccountManager accountManager;	
	
	@Override
	public void onMessage(Object wraper) throws Exception {
		// submit request to handle
		AmsSocialSendMailTask newsTask = new AmsSocialSendMailTask(wraper);
		newsTask.setAccountManager(accountManager);
		newsTask.setProfileManager(profileManager);
		getExecutorService().submit(newsTask);
	}

	@Override
	public void onComplete(Object wraper) {
		super.onComplete(wraper);
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

}