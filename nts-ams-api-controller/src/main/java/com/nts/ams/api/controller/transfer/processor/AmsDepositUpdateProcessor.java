package com.nts.ams.api.controller.transfer.processor;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsDepositUpdateRequestWraper;

/**
 * @description AmsDepositUpdate Processor
 * @version NTS
 * @author THINHPH
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsDepositUpdateProcessor extends AbstractApiProcessor<AmsDepositUpdateRequestWraper>{
	//Key = customerId
	private IDepositManager depositManager = null;
	private IAccountManager accountManager = null;
	
	@Override
	public void onMessage(AmsDepositUpdateRequestWraper wraper) throws InterruptedException {
		AmsDepositUpdateTask task = new AmsDepositUpdateTask(wraper);
		task.setAccountManager(getAccountManager());
		task.setDepositManager(getDepositManager());
		getExecutorService().submit(task);
	}
	
	public void onComplete(AmsDepositUpdateRequestWraper wraper) {
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