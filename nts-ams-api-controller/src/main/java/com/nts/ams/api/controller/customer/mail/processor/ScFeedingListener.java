package com.nts.ams.api.controller.customer.mail.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nts.ams.api.controller.customer.bean.ScHiberanteAccountEvent;
import com.nts.ams.api.controller.service.AmsApiControllerMng;

import cn.nextop.social.api.admin.proxy.model.feeding.SocialRelation;
import cn.nextop.social.api.admin.proxy.model.feeding.SocialTradePropagation;
import cn.nextop.social.api.admin.proxy.service.feeding.FeedingListener;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 15, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScFeedingListener implements FeedingListener {
	private static final Logger logger = LoggerFactory.getLogger(ScFeedingListener.class);

	@Override
	public void onRelation(SocialRelation releation) {
		logger.info("onRelation: {}", releation);
		
		try {
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onMessage(releation);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void onTradePropagation(SocialTradePropagation p) {
		logger.info("onTradePropagation: {}", p);
		
		try {
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onMessage(p);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void onHiberanteAccount(int accountId, int guruAccountId, long lastTradeTime) {
		logger.info("onHiberanteAccount, accountId: {}, guruAccountId: {}, lastTradeTime: {}", new Object[]{accountId, guruAccountId, lastTradeTime});
		try {
			ScHiberanteAccountEvent hiberante = new ScHiberanteAccountEvent();
			hiberante.setAccountId(accountId);
			hiberante.setGuruAccountId(guruAccountId);
			hiberante.setLastTradeTime(lastTradeTime);
			
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onMessage(hiberante);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
