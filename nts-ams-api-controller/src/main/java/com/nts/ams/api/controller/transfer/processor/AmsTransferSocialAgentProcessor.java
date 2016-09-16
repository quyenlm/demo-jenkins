/**
 * 
 */
package com.nts.ams.api.controller.transfer.processor;

import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ITransferManager;

import com.nts.ams.api.controller.processor.AbstractApiProcessor;
import com.nts.ams.api.controller.transfer.bean.AmsTransferSocialRequestWraper;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 8, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransferSocialAgentProcessor extends AbstractApiProcessor<AmsTransferSocialRequestWraper>  {
	private ITransferManager transferManager;
	private IAccountManager accountManager;
	
	@Override
	public void onMessage(AmsTransferSocialRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsTransferSocialAgentTask task = new AmsTransferSocialAgentTask(wraper);
		task.setTransferManager(getTransferManager());
		task.setAccountManager(getAccountManager());
		getExecutorService().submit(task);
	}
	
	public ITransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(ITransferManager transferManager) {
		this.transferManager = transferManager;
	}
	
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	@Override
	public void onComplete(AmsTransferSocialRequestWraper wraper) {
		super.onComplete(wraper);
	}
}