package phn.nts.ams.fe.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import org.springframework.beans.BeanUtils;
import phn.com.nts.db.dao.IAmsMailTemplateDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWhitelabelDAO;
import phn.com.nts.db.dao.IFxSymbolDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysCurrencyDAO;
import phn.com.nts.db.dao.ISysPropertyDAO;
import phn.com.nts.db.dao.impl.AmsPaymentgwWlDAO;
import phn.com.nts.db.domain.AmsPaymentgwWlInfo;
import phn.com.nts.db.entity.AmsMailTemplate;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWhitelabelConfigId;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.db.entity.SysProperty;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.ISystemPropertyManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.MailTemplateInfo;
import phn.nts.ams.fe.domain.SymbolInfo;
import phn.nts.ams.utils.Helper;

public class SystemPropertyManagerImpl implements ISystemPropertyManager {
	private static Logit LOG = Logit.getInstance(SystemPropertyManagerImpl.class);
	private ISysPropertyDAO<SysProperty> sysPropertyDAO;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO;
	private IAmsWhitelabelDAO<AmsWhitelabel> amsWhiteLabelDAO;
	private ISysCurrencyDAO<SysCurrency> sysCurrencyDAO;
	private IAmsMailTemplateDAO<AmsMailTemplate> amsMailTemplateDAO;
	private IFxSymbolDAO<FxSymbol> fxSymbolDAO;
	private AmsPaymentgwWlDAO amsPaymentgwWlDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private static List<String> eaGroupNameList;
	/**
	 * @return the sysPropertyDAO
	 */
	public ISysPropertyDAO<SysProperty> getSysPropertyDAO() {
		return sysPropertyDAO;
	}
	/**
	 * @param sysPropertyDAO the sysPropertyDAO to set
	 */
	public void setSysPropertyDAO(ISysPropertyDAO<SysProperty> sysPropertyDAO) {
		this.sysPropertyDAO = sysPropertyDAO;
	}
	public void loadData() {
		//load sys app date
		loadSysAppDate();
		
		// load sys property
		loadProperties();
		// load whitelabel config
		loadWhitelabelConfig();
		// load currency
		loadCurrency();
		// load mail template
		loadMailTemplate();
		// load symbol
		loadFxSymbol();
		// load symbol currency
		loadSymbolCurrency();
		// load default url
		loadServerConfiguration();

		// load jms configuration
		loadJmsConfiguration();
		// load Ea group name list
		loadEaGroupNameList();
		LOG.info("EaGroupNameList: " + getEaGroupNameList());
		
		LOG.info("Loaded SystemProperty: " + SystemProperty.getInstance().getMap());
	}
	
	private void loadSysAppDate() {
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
			if(amsAppDate != null) {
				LOG.info("Loaded SysAppDate: " + amsAppDate.getId().getFrontDate());
				SystemPropertyConfig.setAmsAppDate(amsAppDate);					
			}
		}
	}
	
	/**
	 * 　
	 * load currency configuration
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 10, 2012
	 * @MdDate
	 */
	public void loadCurrency() {
		List<SysCurrency> listSysCurrency = getSysCurrencyDAO().findAll();
		CurrencyInfo currencyInfo = null;
		if(listSysCurrency != null && listSysCurrency.size() > 0) {
			for(SysCurrency sysCurrency : listSysCurrency) {
				currencyInfo = new CurrencyInfo();
				BeanUtils.copyProperties(sysCurrency, currencyInfo);
				FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyInfo.getCurrencyCode(), currencyInfo);
			}
		}
	}
	/**
	 * 　
	 * load symbol for currency
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Oct 18, 2012
	 * @MdDate
	 */
	public void loadSymbolCurrency() {
		Map<String, String> mapSymbolCurrency = new HashMap<String, String>();
		mapSymbolCurrency.put(IConstants.CURRENCY_CODE.USD, IConstants.SYMBOL_FOR_CURRENCY.USD);
		mapSymbolCurrency.put(IConstants.CURRENCY_CODE.GBP, IConstants.SYMBOL_FOR_CURRENCY.GBP);
		mapSymbolCurrency.put(IConstants.CURRENCY_CODE.JPY, IConstants.SYMBOL_FOR_CURRENCY.JPY);
		mapSymbolCurrency.put(IConstants.CURRENCY_CODE.EUR, IConstants.SYMBOL_FOR_CURRENCY.EUR);
		FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYMBOL_CURRENCY, mapSymbolCurrency);
	}
	/**
	 * 　
	 * load mail template
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 21, 2012
	 * @MdDate
	 */
	public void loadMailTemplate() {
		List<AmsMailTemplate> listAmsMailTemplate = getAmsMailTemplateDAO().findAll();
		MailTemplateInfo mailTemplateInfo = null;
		if(listAmsMailTemplate != null && listAmsMailTemplate.size() > 0) {
			for(AmsMailTemplate amsMailTemplate : listAmsMailTemplate) {
				mailTemplateInfo = new MailTemplateInfo();
				BeanUtils.copyProperties(amsMailTemplate, mailTemplateInfo);
				AmsWhitelabel amsWhitelabel = amsMailTemplate.getAmsWhitelabel();
				if(amsWhitelabel != null) {
					mailTemplateInfo.setWlCode(amsWhitelabel.getWlCode());
				}
				FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + mailTemplateInfo.getMailCode(), mailTemplateInfo);
			}
		}
	}
	/**
	 * 　
	 * load fx symbol
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Oct 1, 2012
	 * @MdDate
	 */
	public void loadFxSymbol() {
		LOG.info("[start] load fx symbol");
		List<FxSymbol> listFxSymbol = getFxSymbolDAO().findAll();
		SymbolInfo symbolInfo = null;
		FxSymbol fxSymbol = null;
		if(listFxSymbol != null && listFxSymbol.size() > 0) {
			for(int i = 0; i < listFxSymbol.size(); i ++ ) {
				fxSymbol = listFxSymbol.get(i);
				symbolInfo = new SymbolInfo();
				BeanUtils.copyProperties(fxSymbol, symbolInfo);
				FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolInfo.getSymbolCd(), symbolInfo);
			}
			
		}
		LOG.info("[end] load fx symbol");
	}
	/**
	 * 　
	 * load white label config
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 10, 2012
	 * @MdDate
	 */
	public void loadWhitelabelConfig() {
		LOG.info("[start] load white label config");
		
		//Get list whitelabel
		List<AmsWhitelabel> listAmsWhiteLabel = getAmsWhiteLabelDAO().findAll();
		if(listAmsWhiteLabel != null && listAmsWhiteLabel.size() > 0) {
			for(int i = 0 ; i < listAmsWhiteLabel.size(); i ++ ) {
				
				AmsWhitelabel amsWhiteLabel = listAmsWhiteLabel.get(i);
				//Get config of each whitelabel
				List<AmsWhitelabelConfig> listAmsWhiteLabelConfig = getAmsWhitelabelConfigDAO().findByProperty("id.wlCode", amsWhiteLabel.getWlCode());
				
				Map<String, String> mapWhiteLabelConfig = null;
				if(listAmsWhiteLabelConfig != null && listAmsWhiteLabelConfig.size() > 0) {		
					mapWhiteLabelConfig = new HashMap<String, String>();
					
					for(AmsWhitelabelConfig amsWhiteLabelConfig : listAmsWhiteLabelConfig) {						
						AmsWhitelabelConfigId whiteLabelConfigId = amsWhiteLabelConfig.getId();				
						mapWhiteLabelConfig.put(whiteLabelConfigId.getConfigKey(), amsWhiteLabelConfig.getConfigValue());						
					}					
				}
				LOG.info("Loaded WL_CONFIG: " + IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + amsWhiteLabel.getWlCode() + ": " + mapWhiteLabelConfig);
				SystemProperty.getInstance().getMap().put(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + amsWhiteLabel.getWlCode(), mapWhiteLabelConfig);
				
				//Get list payment method for wlcode
				getListPaymentMethod(amsWhiteLabel.getWlCode());
			}
		}
		
		LOG.info("[end] load white label config");
	}
	/**
	 * 　
	 * load system property on SYS_PROPERTY
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 25, 2012
	 * @MdDate
	 */
	public void loadProperties() {
		LOG.info("[start] load system property");
		List<String> listKey = sysPropertyDAO.getListPropertyKey();
		Map<String, String> map = null;
		
		if(listKey != null && listKey.size() > 0) {
			for(String key : listKey) {
				map = new TreeMap<String, String>();
				List<SysProperty> listAmsSysProperty = sysPropertyDAO.findByProperty("id.propertyKey", key);
				if(listAmsSysProperty != null && listAmsSysProperty.size() > 0) {
					for(SysProperty amsSysProperty : listAmsSysProperty) {
						map.put(amsSysProperty.getId().getPropType(), amsSysProperty.getPropertyValue());
					}
				}
				put(SystemProperty.getInstance().getMap(), IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + key, map);
				
				if(IConstants.SYS_PROPERTY.PAYMENT_INFORMATION_METHOD.equals(key) && listAmsSysProperty != null && listAmsSysProperty.size() > 0) {
					//Cache PAYMENT_INFORMATION_METHOD to FrontEndContext
					FrontEndContext.getInstance().putMapConfiguration(IConstants.SYS_PROPERTY.PAYMENT_INFORMATION_METHOD, listAmsSysProperty);
				}
			}
		}		
		
		Map<String, String> mapMT4Configuration = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_CONFIGURATION);
		FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION, mapMT4Configuration);
		LOG.info("[end] load system property");
	}
	private void put(Map<String, Map<String, String>> map, String key, Map<String, String> mapValue) {
		map.put(key, mapValue);
	}
	/**
	 * @return the amsWhitelabelConfigDAO
	 */
	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getAmsWhitelabelConfigDAO() {
		return amsWhitelabelConfigDAO;
	}
	/**
	 * @param amsWhitelabelConfigDAO the amsWhitelabelConfigDAO to set
	 */
	public void setAmsWhitelabelConfigDAO(
			IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO) {
		this.amsWhitelabelConfigDAO = amsWhitelabelConfigDAO;
	}
	/**
	 * @return the amsWhiteLabelDAO
	 */
	public IAmsWhitelabelDAO<AmsWhitelabel> getAmsWhiteLabelDAO() {
		return amsWhiteLabelDAO;
	}
	/**
	 * @param amsWhiteLabelDAO the amsWhiteLabelDAO to set
	 */
	public void setAmsWhiteLabelDAO(
			IAmsWhitelabelDAO<AmsWhitelabel> amsWhiteLabelDAO) {
		this.amsWhiteLabelDAO = amsWhiteLabelDAO;
	}
	/**
	 * @return the sysCurrencyDAO
	 */
	public ISysCurrencyDAO<SysCurrency> getSysCurrencyDAO() {
		return sysCurrencyDAO;
	}
	/**
	 * @param sysCurrencyDAO the sysCurrencyDAO to set
	 */
	public void setSysCurrencyDAO(ISysCurrencyDAO<SysCurrency> sysCurrencyDAO) {
		this.sysCurrencyDAO = sysCurrencyDAO;
	}
	/**
	 * @return the amsMailTemplateDAO
	 */
	public IAmsMailTemplateDAO<AmsMailTemplate> getAmsMailTemplateDAO() {
		return amsMailTemplateDAO;
	}
	private void loadJmsConfiguration() {
		try {
			Properties props = Helpers.getProperties("jndi.properties");
			if(props != null) {
				for(Entry<Object, Object> entry : props.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					FrontEndContext.getInstance().putJmsName(key, value);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	/**
	 * @param amsMailTemplateDAO the amsMailTemplateDAO to set
	 */
	public void setAmsMailTemplateDAO(
			IAmsMailTemplateDAO<AmsMailTemplate> amsMailTemplateDAO) {
		this.amsMailTemplateDAO = amsMailTemplateDAO;
	}
	/**
	 * @return the fxSymbolDAO
	 */
	public IFxSymbolDAO<FxSymbol> getFxSymbolDAO() {
		return fxSymbolDAO;
	}
	/**
	 * @param fxSymbolDAO the fxSymbolDAO to set
	 */
	public void setFxSymbolDAO(IFxSymbolDAO<FxSymbol> fxSymbolDAO) {
		this.fxSymbolDAO = fxSymbolDAO;
	}
	private void loadServerConfiguration() {
		try {
			Properties props = Helpers.getProperties("configs.properties");
			if(props != null) {
				String defaultTargetUrl = props.getProperty("default-target-url");
				if(!StringUtil.isEmpty(defaultTargetUrl)) {
					FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "default-target-url", defaultTargetUrl);
				}
				String privateKey = props.getProperty("secret.key");
				if(!StringUtil.isEmpty(privateKey)) {
					FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key", privateKey);
				}

                String clusterServerId = props.getProperty(ITrsConstants.CONFIG_KEY.CLUSTER_SERVER_ID);
                if(!StringUtil.isEmpty(clusterServerId)) {
                    FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + ITrsConstants.CONFIG_KEY.CLUSTER_SERVER_ID, clusterServerId.trim());
                }

				// Avatar crop
				Integer avatarMode = MathUtil.parseInt(props.getProperty(ITrsConstants.AVATAR_MODE.AVATAR_MODE));
                if(avatarMode == null) {
                	avatarMode = IConstants.AVATAR_MODE.NORMAL;
                }
                FrontEndContext.getInstance().putMapConfiguration(ITrsConstants.AVATAR_MODE.AVATAR_MODE, avatarMode);
                
                String openBOAccountFlg = props.getProperty(ITrsConstants.SYSTEM_CONFIG_KEY.OPENBOACCOUNTFLG);
                if(!StringUtil.isEmpty(openBOAccountFlg)){
                	FrontEndContext.getInstance().putMapConfiguration(ITrsConstants.SYSTEM_CONFIG_KEY.OPENBOACCOUNTFLG,  MathUtil.parseInt(openBOAccountFlg));
                }
                
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public List<AmsPaymentgwWlInfo> getListPaymentMethod(String wlCode) {
		List<AmsPaymentgwWlInfo> listPaymentMethod = getAmsPaymentgwWlDAO().findListPaymentMethodGwByWl(wlCode);
		
		if(listPaymentMethod != null && listPaymentMethod.size() > 0) {
			//Key = PAYMENT_GW_WL_WLCODE, Value = List
			FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.PAYMENT_GW_WL + wlCode, listPaymentMethod);
		}
		return listPaymentMethod;
	}
	
	public void loadEaGroupNameList() {
		List<String> listNames = new ArrayList<String>();
		String demoGroupNames = SystemProperty.getInstance().getMap().get(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + "TRS").get("DEMO_GROUP_LIST");
		if (!StringUtil.isEmpty(demoGroupNames)) {
			String[] arrNames = demoGroupNames.trim().split(",");
			listNames = Arrays.asList(arrNames); 
			Helper.listTrim(listNames);
		}
		setEaGroupNameList(listNames);
	}
	
	public static List<String> getEaGroupNameList() {
		return eaGroupNameList;
	}
	public static void setEaGroupNameList(List<String> eaGroupNameList) {
		SystemPropertyManagerImpl.eaGroupNameList = eaGroupNameList;
	}
	public AmsPaymentgwWlDAO getAmsPaymentgwWlDAO() {
		return amsPaymentgwWlDAO;
	}
	public void setAmsPaymentgwWlDAO(AmsPaymentgwWlDAO amsPaymentgwWlDAO) {
		this.amsPaymentgwWlDAO = amsPaymentgwWlDAO;
	}

	public ISysAppDateDAO<SysAppDate> getSysAppDateDAO() {
		return iSysAppDateDAO;
	}
	
	public void setSysAppDateDAO(ISysAppDateDAO<SysAppDate> iSysAppDateDAO) {
		this.iSysAppDateDAO = iSysAppDateDAO;
	}
}
