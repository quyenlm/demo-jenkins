package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IWithdrawalManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalCancelRequestWraper;

/**
 * @description AmsWithdrawalCancel Processor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsWithdrawalCancelProcessor extends AbstractApiProcessor<AmsWithdrawalCancelRequestWraper>{
    private IWithdrawalManager withdrawalManager;
	
	@Override
	public void onMessage(AmsWithdrawalCancelRequestWraper wraper) throws InterruptedException {
		AmsWithdrawalCancelTask task = new AmsWithdrawalCancelTask(wraper);
		task.setWithdrawalManager(getWithdrawalManager());
		getExecutorService().submit(task);
	}
	
	public void onComplete(AmsWithdrawalCancelRequestWraper wraper) {
		super.onComplete(wraper);
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}
}