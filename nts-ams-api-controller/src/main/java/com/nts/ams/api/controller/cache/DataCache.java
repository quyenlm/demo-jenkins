package com.nts.ams.api.controller.cache;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;

import com.nts.ams.api.controller.setting.Configuration;
import com.nts.ams.inmem.impl.AmsAgreementNewsCache;
import com.nts.ams.inmem.impl.AmsCustomerAuthenCache;
import com.nts.ams.inmem.impl.AmsCustomerNewsCache;
import com.nts.common.Constant;
import com.nts.common.exchange.ams.bean.AgreementNews;
import com.nts.common.exchange.ams.bean.AgreementNews.NewsBean;
import com.nts.common.exchange.ams.bean.AmsCustomerAuthenInfo;
import com.nts.common.exchange.ams.bean.AmsCustomerNews;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Apr 12, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class DataCache {
	private static Logit log = Logit.getInstance(DataCache.class);
	private ISocialManager socialManager;
	private IProfileManager profileManager;
	private Configuration configuration;
	
	private AmsCustomerNewsCache amsCustomerNewsCache;
	private AmsCustomerAuthenCache amsCustomerAuthenCache;
	private AmsAgreementNewsCache amsAgreementNewsCache;
	
	public synchronized void init() {
		log.info("Starting connection to Redis");
		amsCustomerNewsCache = new AmsCustomerNewsCache();
		amsCustomerAuthenCache = new AmsCustomerAuthenCache();
		amsAgreementNewsCache = new AmsAgreementNewsCache();
		log.info("Started connection to Redis");
	}
	
	private long lastCacheAgreementNews = 0; //Time get agreement from Redis
	private long updateAgreementNewsDate = 0; //Time save agreement to Redis
	private ConcurrentMap<Integer, AgreementNews.NewsBean> cacheNewsBean = new ConcurrentHashMap<Integer, AgreementNews.NewsBean>();
	
	public Map<Integer, AgreementNews.NewsBean> getAgreementNewsCache() {
		cacheAgreementNews();
		return cacheNewsBean;
	}
	
	/**
	 * Load all news from Redis and cache to local　
	 * 
	 * @param
	 * @return
	 * @CrDate Apr 15, 2016
	 * @MdDate
	 */
	private void cacheAgreementNews() {
		try {
			if(isNeedReloadAgreementNews()) {
				
				synchronized (cacheNewsBean) {
					if(isNeedReloadAgreementNews()) {
						//Find news from cache
						AgreementNews agreementNews = amsAgreementNewsCache.findById(Constant.AMS_AGREEMENT_NEW_KEY_DEFAULT);
						if(agreementNews != null) {
							updateAgreementNewsDate = agreementNews.getUpdateDate().getTime();
							lastCacheAgreementNews = System.currentTimeMillis();
							
							cacheNewsBean.clear();
							for (NewsBean newsBean : agreementNews.getLstAllNews()) {
								cacheNewsBean.put(newsBean.getMessageId(), newsBean);
							}
						} else
							log.error("NOT found AMS_AGREEMENT_NEWS from Redis. Please check!");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private boolean isNeedReloadAgreementNews() {
		return System.currentTimeMillis() - updateAgreementNewsDate > configuration.getMaxTimeCacheAgreement() //24 hours
			 && System.currentTimeMillis() - lastCacheAgreementNews > configuration.getMinTimeCacheAgreement(); //5s
	}
	
	public AmsCustomerNews getAmsCustomerNews(String customerId) {
		AmsCustomerNews result = new AmsCustomerNews();
		try {
			//Find news from cache
			AmsCustomerNews amsCustomerNews = amsCustomerNewsCache.findById(customerId);
			
			if (amsCustomerNews == null) {
				log.info("Can not get AmsCustomerNews on Redis, get data from DB...");
				amsCustomerNews = getSocialManager().getAgreementInfo(customerId);
				log.info("Loaded AmsCustomerNews from DB: " + amsCustomerNews);
				
				if(amsCustomerNews != null){
					amsCustomerNewsCache.saveOrUpdate(amsCustomerNews);
					log.info("Inserted AmsCustomerNews info to Redis: SUCCESS");
				} else{
					log.warn("Could not find AmsCustomerNews for customerId: " + customerId + " from DB & Redis");
				}
			}
			
			if(amsCustomerNews != null) {
				//Check StartDate/ExpiredDate Of Agreement
				result.setCustomerId(amsCustomerNews.getCustomerId());
				
				Map<Integer, AgreementNews.NewsBean> cacheNews = getAgreementNewsCache();
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				
				//ReAgreement
				for (Integer msgId : amsCustomerNews.getLstReAgreement()) {
					NewsBean newsBean = cacheNews.get(msgId);
					if(newsBean != null && DateUtil.compare(newsBean.getStartDate(), currentTime) <= 0)
						result.getLstReAgreement().add(msgId);
						
				}
				
				//Agreement
				for (Integer msgId : amsCustomerNews.getLstAgreement()) {
					NewsBean newsBean = cacheNews.get(msgId);
					if(newsBean != null && DateUtil.compare(newsBean.getStartDate(), currentTime) <= 0) {
						
						if((newsBean.getExpireDate() == null && newsBean.getEndDate() == null)
								|| (newsBean.getExpireDate() == null && DateUtil.compare(newsBean.getEndDate(), currentTime) > 0)
								|| (DateUtil.compare(newsBean.getExpireDate(), currentTime) > 0))
							result.getLstAgreement().add(msgId);
					}						
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}
	
	public boolean removeAmsCustomerNews(String customerId) {
		return amsCustomerNewsCache.deleteById(customerId);
	}
	
	public AmsCustomerAuthenInfo getAmsCustomerAuthenInfo(String loginId) {
		AmsCustomerAuthenInfo authenInfo = null;
		try {
			//Find news from cache
			authenInfo = amsCustomerAuthenCache.findById(loginId);
			
//			if (authenInfo == null) {
//				log.info("Can not get AmsCustomerAuthenInfo with loginId: " + loginId + " on Redis, get data from DB...");
//				//Load data from DB
//				AmsCustomer amsCustomer = profileManager.getAmsCustomerByLoginId(loginId);
//				if(amsCustomer == null)
//					log.warn("Not found AmsCustomer from DB, loginId: " + loginId);
//				else {
//					authenInfo = new AmsCustomerAuthenInfo();
//					authenInfo.setCustomerId(amsCustomer.getCustomerId());
//					authenInfo.setLoginId(amsCustomer.getLoginId());
//					authenInfo.setLoginPass(amsCustomer.getLoginPass());
//					authenInfo.setAllowLoginFlg(amsCustomer.getAllowLoginFlg());
//					authenInfo.setNtdCustomerId(amsCustomer.getNtdCustomerId());
//					authenInfo.setUpdateDate(amsCustomer.getUpdateDate());
//					authenInfo.setActiveFlg(amsCustomer.getActiveFlg());
//					log.info("Loaded AmsCustomerAuthenInfo from DB: " + authenInfo);
//				}
//				
//				if(authenInfo != null){
//					amsCustomerAuthenCache.saveOrUpdate(authenInfo);
//					log.info("Inserted AmsCustomerAuthenInfo info to Redis: SUCCESS");
//				} else{
//					log.warn("Could not find AmsCustomerAuthenInfo for loginId: " + loginId + " from DB & Redis");
//				}
//			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return authenInfo;
	}
	
	public boolean removeAmsCustomerAuthenInfo(String loginId) {
		log.info("remove loginId: " + loginId + " from AmsCustomerAuthenInfo Redis");
		return amsCustomerAuthenCache.deleteById(loginId);
	}
	
	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}
	
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
