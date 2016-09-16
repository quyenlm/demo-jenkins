package com.nts.ams.api.controller.customer.mail.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.nextop.social.api.admin.proxy.model.customer.Customer;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccount;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerConfig;
import cn.nextop.social.api.admin.proxy.service.customer.CustomerListener;

import com.nts.ams.api.controller.customer.bean.ScCustomerEvent;
import com.nts.ams.api.controller.service.AmsApiControllerMng;

/**
 * @description
 * @version NTS
 * @author TheLN
 * @CrDate Mar 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScCustomerListener implements CustomerListener {	
	private static final Logger logger = LoggerFactory.getLogger(ScCustomerListener.class);
	
	@Override																			
	public void onModifyCustomerConfig(List<Integer> copierIds, CustomerConfig customerConfig) {
		logger.info("onModifyCustomerConfig, copierIds: {}, customerConfig: {}", new Object[]{copierIds, customerConfig});
		try {
			ScCustomerEvent customerEvent = new ScCustomerEvent();
			customerEvent.setCopierIds(copierIds);
			customerEvent.setCustomerConfig(customerConfig);
			
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onMessage(customerEvent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void onModifyCustomer(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifyAccount(CustomerAccount account) {
		// TODO Auto-generated method stub
		
	}
	
}
