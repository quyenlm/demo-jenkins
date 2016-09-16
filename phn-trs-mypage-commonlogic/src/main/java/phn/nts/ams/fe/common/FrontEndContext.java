package phn.nts.ams.fe.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import phn.com.nts.db.domain.AmsPaymentgwWlInfo;
import phn.com.nts.db.entity.SysProperty;
import phn.com.nts.util.common.IConstants;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.domain.BalanceInfo;

public class FrontEndContext {
//	private static Logit LOG = Logit.getInstance(FrontEndContext.class);
	private static FrontEndContext instance;
	private static Map<String, Map<String, String>> context = new HashMap<String, Map<String,String>>();
	private static Map<String, Object> mapConfiguration = null;
	private static ConcurrentMap<String, Map<Integer, BalanceInfo>> mapBalanceContextInfo = new ConcurrentHashMap<String, Map<Integer, BalanceInfo>>();
	private static Map<String, String> mapJmsName = new HashMap<String, String>();
	private static Properties pros;
	public static FrontEndContext getInstance() {
		if(instance == null) {
			instance = new FrontEndContext();
		}
		return instance;
	}

    public String getAvatarTimestamp(String customerId){
        Object result = mapConfiguration.get(IConstants.SYSTEM_CONFIG_KEY.AVATAR_TIMESTAMP + customerId);
        return result == null ? "?" : "?ts=" + result.toString();
    }

    public void setAvatarTimestamp(String customerId){
        synchronized (mapConfiguration){
            mapConfiguration.put(IConstants.SYSTEM_CONFIG_KEY.AVATAR_TIMESTAMP + customerId, System.currentTimeMillis());
        }
    }
    
    
	public FrontEndContext() {
		if(mapConfiguration == null) {
			mapConfiguration = new HashMap<String, Object>();
		}
	}
	
	public Map<String, String> getContext(String key) {
		return context.get(key);
	}
	public void putContext(String key, Map<String, String> value) {
		context.put(key, value);
	}
	public void putMapConfiguration(String key, Object value) {
		mapConfiguration.put(key, value);
	}
	public Object getMapConfiguration(String key) {
		return mapConfiguration.get(key);
	}
	public Map<Integer, BalanceInfo> getMapBalanceInfo(String customerId) {
		if(customerId == null) {
			return null;
		}
		if(!mapBalanceContextInfo.containsKey(customerId)) {
			Map<Integer, BalanceInfo> mapBalanceInfo = new HashMap<Integer, BalanceInfo>();
			mapBalanceContextInfo.put(customerId, mapBalanceInfo);
		}
		return mapBalanceContextInfo.get(customerId);
	}
	public void addBalanceInfo(String customerId, Integer serviceType, BalanceInfo balanceInfo) {
		if(!mapBalanceContextInfo.containsKey(customerId)) {
			Map<Integer, BalanceInfo> mapBalanceInfo = new HashMap<Integer, BalanceInfo>();
			mapBalanceContextInfo.put(customerId, mapBalanceInfo);
		}
		Map<Integer, BalanceInfo> mapBalanceInfo = mapBalanceContextInfo.get(customerId);
		mapBalanceInfo.put(serviceType, balanceInfo);
		mapBalanceContextInfo.put(customerId, mapBalanceInfo);
	}
	public void removeBalanceInfo(String customerId) {
		mapBalanceContextInfo.remove(customerId);
	}
	
	public String getAvatarDimension(String customerId){
        Object result = mapConfiguration.get("AVATAR_DIMENSION" + customerId);
        return result == null ? "" : result.toString();
	}

    public void setAvatarDimension(String customerId, Integer width, Integer height){
        synchronized (mapConfiguration){
            mapConfiguration.put("AVATAR_DIMENSION" + customerId, "width:" + phn.com.nts.util.common.StringUtil.toString(width) + "px;height:" + phn.com.nts.util.common.StringUtil.toString(height) + "px");
        }
    }
	
	public Integer getAvatarMode() {
		return (Integer) mapConfiguration.get(ITrsConstants.AVATAR_MODE.AVATAR_MODE);
	}
	
	public String getJmsName(String key) {
		return mapJmsName.get(key);
	}
	public void putJmsName(String key, String value) {
		mapJmsName.put(key, value);
	}
	
	public Integer getOpenBOAccountFlg(){
		return (Integer) mapConfiguration.get(ITrsConstants.SYSTEM_CONFIG_KEY.OPENBOACCOUNTFLG);
	}
	
	@SuppressWarnings("unchecked")
	public List<AmsPaymentgwWlInfo> getListPaymentGwWlInfo(String wlCode) {
		List<AmsPaymentgwWlInfo> listPaymentMethod = (List<AmsPaymentgwWlInfo>) mapConfiguration.get(IConstants.SYSTEM_CONFIG_KEY.PAYMENT_GW_WL + wlCode);
		return listPaymentMethod;
	}
	
	public List<SysProperty> getPaymentInformationMethod() {
		@SuppressWarnings("unchecked")
		List<SysProperty> listAmsSysProperty = (List<SysProperty>) mapConfiguration.get(IConstants.SYS_PROPERTY.PAYMENT_INFORMATION_METHOD);
		return listAmsSysProperty;
	}

	public static Properties getPros() {
		return pros;
	}

	public static void setPros(Properties pros) {
		FrontEndContext.pros = pros;
	}
}
