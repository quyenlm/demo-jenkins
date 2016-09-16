package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsDepositRequestWraper;

/**
 * @description AmsDeposit Processor
 * @version NTS
 * @author THINHPH
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsDepositProcessor extends AbstractApiProcessor<AmsDepositRequestWraper>{
	private IDepositManager depositManager;
	private IAccountManager accountManager;
	
	@Override
	public void onMessage(AmsDepositRequestWraper wraper) throws InterruptedException {
		AmsDepositTask task = new AmsDepositTask(wraper);
		task.setDepositManager(depositManager);
		task.setAccountManager(accountManager);
		getExecutorService().submit(task);
	}
	
	public void onComplete(AmsDepositRequestWraper wraper) {
		super.onComplete(wraper);
		AmsApiControllerMng.getAmsSerializeTransactionMananger().onComplete(wraper);
	}

	public IDepositManager getDepositManager() {
		return depositManager;
	}

	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

}