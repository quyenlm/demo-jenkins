/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IBalanceManager;

import com.nts.ams.api.controller.customer.bean.CustomerBalanceRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description Manage AmsCustomerBalanceRequest
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerBalanceProcessor extends AbstractApiProcessor<CustomerBalanceRequestWraper> {
	protected IBalanceManager balanceManager;

	@Override
	public void onMessage(CustomerBalanceRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerBalanceTask orderTask = new AmsCustomerBalanceTask(wraper);
		orderTask.setBalanceManager(getBalanceManager());
		orderTask.setExecutorService(getExecutorService());
		getExecutorService().submit(orderTask);
	}
	
	public void setBalanceManager(IBalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    public IBalanceManager getBalanceManager() {
        return balanceManager;
    }

	@Override
	public void onComplete(CustomerBalanceRequestWraper wraper) {
		super.onComplete(wraper);
	}
}