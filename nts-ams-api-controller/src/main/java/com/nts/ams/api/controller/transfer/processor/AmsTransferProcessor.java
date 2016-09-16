package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ITransferManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsTransferRequestWraper;

/**
 * @description AmsCustomerInfoUpdate Processor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransferProcessor extends AbstractApiProcessor<AmsTransferRequestWraper>{
	private IBalanceManager balanceManager;
	private IAccountManager accountManager = null;
    private ITransferManager transferManager;
    private IProfileManager profileManager = null;
	
	@Override
	public void onMessage(AmsTransferRequestWraper wraper) throws InterruptedException {
		AmsTransferTask task = new AmsTransferTask(wraper);
		task.setTransferManager(getTransferManager());
		task.setBalanceManager(getBalanceManager());
		task.setAccountManager(getAccountManager());
		task.setProfileManager(getProfileManager());
		task.setExecutorService(getExecutorService());
		getExecutorService().submit(task);
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
	public void onComplete(AmsTransferRequestWraper wraper) {
		super.onComplete(wraper);
		AmsApiControllerMng.getAmsSerializeTransactionMananger().onComplete(wraper);
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public ITransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(ITransferManager transferManager) {
		this.transferManager = transferManager;
	}

	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}