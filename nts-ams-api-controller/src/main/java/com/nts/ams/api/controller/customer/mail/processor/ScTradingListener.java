package com.nts.ams.api.controller.customer.mail.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nts.ams.api.controller.service.AmsApiControllerMng;

import cn.nextop.social.api.admin.proxy.model.trading.Execution;
import cn.nextop.social.api.admin.proxy.service.trading.TradingListener;

/**
 * @description
 * @version NTS
 * @author TheLN
 * @CrDate Mar 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */

public class ScTradingListener implements TradingListener {
	private static final Logger logger = LoggerFactory.getLogger(ScTradingListener.class);
	
	@Override
	public void onExecution(Execution execution) {
		logger.info("onExecution: {}", execution);
		try {
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onMessage(execution);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
