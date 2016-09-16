package phn.nts.ams.fe.common.memcached;

import org.apache.commons.beanutils.BeanUtils;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.CustomerInfo;

import com.nts.common.exchange.dealing.FxFrontRateInfo;
import com.nts.dealing.inmem.memcached.DataProvider;

public class SocialMemcached {
	private static Logit LOG = Logit.getInstance(SocialMemcached.class);
	private static SocialMemcached instance;	
	public static SocialMemcached getInstance() {
		if(instance == null) {
			instance = new SocialMemcached();
		}
		return instance;
	}
	
	public synchronized FxFrontRateInfo getFrontRateInfo(String symbolCd) {
		FxFrontRateInfo fxFrontRateInfo = null;
		try {
			fxFrontRateInfo = DataProvider.getFrontRate(symbolCd);
		} catch(Exception ex) {
			LOG.error(ex.getMessage());
		}
		return fxFrontRateInfo;
	}
	
	public synchronized void saveFrontRateInfo(FxFrontRateInfo fxFrontRateInfo) {
		try {
			DataProvider.saveFrontRate(fxFrontRateInfo);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public synchronized CustomerInfo getCustomerInfo (String customerId) {
		CustomerInfo cusInfo = null;
		com.nts.common.exchange.social.CustomerInfo cusInfoMem = null;
		try {
			cusInfoMem = DataProvider.getCustomerInfo(customerId);
			if(cusInfoMem != null){
				cusInfo = new CustomerInfo();
				BeanUtils.copyProperties(cusInfo, cusInfoMem);
			}
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return cusInfo;
	}
	
	public synchronized void saveCustomerInfo(CustomerInfo customerInfo){
		try{
			com.nts.common.exchange.social.CustomerInfo cusInfoMem = new com.nts.common.exchange.social.CustomerInfo();
			if(customerInfo != null){
				BeanUtils.copyProperties(cusInfoMem, customerInfo);
			}
			DataProvider.saveCustomerInfo(cusInfoMem);
		}catch(Exception ex){
			LOG.error(ex.getMessage());
		}
	}
}
