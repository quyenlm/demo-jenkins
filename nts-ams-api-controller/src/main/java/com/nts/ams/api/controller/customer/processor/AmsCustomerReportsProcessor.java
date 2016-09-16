/**
 * 
 */
package com.nts.ams.api.controller.customer.processor;

import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.IReportManager;

import com.nts.ams.api.controller.customer.bean.AmsCustomerReportsRequestWraper;
import com.nts.ams.api.controller.processor.AbstractApiProcessor;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jan 7, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerReportsProcessor extends AbstractApiProcessor<AmsCustomerReportsRequestWraper>  {
	private IProfileManager profileManager;
	private IReportManager reportManager;
	
	@Override
	public void onMessage(AmsCustomerReportsRequestWraper wraper) throws Exception {
		//submit request to handle
		AmsCustomerReportsTask task = new AmsCustomerReportsTask(wraper);
		task.setProfileManager(getProfileManager());
		task.setReportManager(reportManager);
		getExecutorService().submit(task);
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	public IReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(IReportManager reportManager) {
		this.reportManager = reportManager;
	}

	@Override
	public void onComplete(AmsCustomerReportsRequestWraper wraper) {
		super.onComplete(wraper);
	}
}