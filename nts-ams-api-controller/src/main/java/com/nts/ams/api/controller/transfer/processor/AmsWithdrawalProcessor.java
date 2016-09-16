package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.IWithdrawalManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsWithdrawalRequestWraper;

/**
 * @description AmsCustomerInfoUpdate Processor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsWithdrawalProcessor extends AbstractApiProcessor<AmsWithdrawalRequestWraper>{
	//Key = customerId
	private IAccountManager accountManager = null;
	private IBalanceManager balanceManager;
    private IIBManager ibManager;
    private IWithdrawalManager withdrawalManager;
    private IDepositManager depositManager;
	
	@Override
	public void onMessage(AmsWithdrawalRequestWraper wraper) throws InterruptedException {
		AmsWithdrawalTask task = new AmsWithdrawalTask(wraper);
		task.setAccountManager(getAccountManager());
		task.setBalanceManager(getBalanceManager());
		task.setIbManager(getIbManager());
		task.setWithdrawalManager(getWithdrawalManager());
		task.setDepositManager(getDepositManager());
		getExecutorService().submit(task);
	}
	
	/**
	 * Remove first request and start handle next request (if has)
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 8, 2015
	 * @MdDate
	 */
	public void onComplete(AmsWithdrawalRequestWraper wraper) {
		super.onComplete(wraper);
		AmsApiControllerMng.getAmsSerializeTransactionMananger().onComplete(wraper);
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	public IIBManager getIbManager() {
		return ibManager;
	}

	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}

	public IDepositManager getDepositManager() {
		return depositManager;
	}

	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}
}