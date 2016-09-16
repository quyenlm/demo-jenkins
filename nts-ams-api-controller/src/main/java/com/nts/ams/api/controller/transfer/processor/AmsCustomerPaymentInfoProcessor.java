package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.IWithdrawalManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.transfer.bean.AmsCustomerPaymentInfoRequestWraper;

/**
 * @description
 * @version NTS
 * @author THINHPH
 * @CrDate Jul 21, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentInfoProcessor extends AbstractApiProcessor<AmsCustomerPaymentInfoRequestWraper>{
	private IProfileManager profileManager;
	private IWithdrawalManager withdrawalManager;
	
	@Override
	public void onMessage(AmsCustomerPaymentInfoRequestWraper wraper) throws InterruptedException {
		AmsCustomerPaymentInfoTask task = new AmsCustomerPaymentInfoTask(wraper);
		task.setWithdrawalManager(getWithdrawalManager());
		task.setProfileManager(profileManager);
		getExecutorService().submit(task);
	}
	
	public void onComplete(AmsCustomerPaymentInfoRequestWraper wraper) {
		super.onComplete(wraper);
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}
}