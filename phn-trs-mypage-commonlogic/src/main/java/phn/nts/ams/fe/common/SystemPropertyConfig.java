package phn.nts.ams.fe.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;
import phn.nts.ams.utils.Helper;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;


public class SystemPropertyConfig implements TextProvider, LocaleProvider {
	private static Logit LOG = Logit.getInstance(SystemPropertyConfig.class);
	private final transient TextProvider textProvider = new TextProviderFactory().createInstance(getClass(), this);
	private static SystemPropertyConfig instance;
	private static Properties errMsgs;
	private static List<String> listCustomerTestInternalSc;
	private static Properties propsConfig;
	private static final String CONFIGPATH = "configs.properties";
	private static SysAppDate amsAppDate;
	
	public static final int CONFIG_MODE_AMS_API = 1; //Config for AmsApi
	public static final int CONFIG_MODE_FE = 2; //Config for BE
	private static int configMode = CONFIG_MODE_FE;
	
	public static SystemPropertyConfig getInstance() {
		if(instance == null) {
			instance = new SystemPropertyConfig();			
		}
		return instance;
	}
	static {
        try {
            propsConfig = Helpers.getProperties(CONFIGPATH);                      
        } catch(Exception e) {
        	LOG.warn("Could not load configuration file from: " + CONFIGPATH, e);
        }
    }
	@Override
	public Locale getLocale() {
//		ActionContext ctx = ActionContext.getContext();
//        if (ctx != null) {        	
//            return ctx.getLocale();
//        } else {
//        	LOG.info("Action context not initialized");
//            return null;
//        }
		String language = StringUtil.toLowerCase(IConstants.Language.ENGLISH);
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {
				language = StringUtil.toLowerCase(frontUserOnline.getLanguage());
			}
		}
		
		return new Locale(language);
		
	}
	
	/**
	 * 　
	 * get map with propertyKey param
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String propertyKey) {
		String value = "";
		Map<String, String> map = SystemProperty.getInstance().getMapByKey(propertyKey);
		
		if(configMode == CONFIG_MODE_FE) {
			Map<String, String> newMapKey = (Map<String, String>) ObjectCopy.copy(map);
			if(newMapKey != null && newMapKey.size() > 0) {
				for (String key : newMapKey.keySet()) {
					value = newMapKey.get(key);
					newMapKey.put(key, getText(value));				
				}
			}
			return newMapKey;
		}
		
		return map;
	}
	
	public Map<Integer, String> getMapContent(String propertyKey) {
		String value = "";
		Map<String, String> map = SystemProperty.getInstance().getMapByKey(propertyKey);
		Map<Integer, String> newMapKey = new TreeMap<Integer, String>();
		for (String key : map.keySet()) {
			Integer id = MathUtil.parseInteger(key);
			value = map.get(key);
			newMapKey.put(id, getText(value));
		}
		
		return newMapKey;
	}
	public static String getConfigProperties(String key){
        return propsConfig.getProperty(key);
	}
	@Override
	public String getText(String arg0) {		
		try {
			if(configMode == CONFIG_MODE_FE)
				return textProvider.getText(arg0);
			return String.valueOf(errMsgs.get(arg0));					
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
//			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public String getText(String arg0, String arg1) {
		return textProvider.getText(arg0, arg1);
	}

	@Override
	public String getText(String arg0, List<?> arg1) {
		return textProvider.getText(arg0, arg1);
	}

	@Override
	public String getText(String arg0, String[] arg1) {
		return textProvider.getText(arg0, arg1);
	}

	@Override
	public String getText(String arg0, String arg1, String arg2) {
		return textProvider.getText(arg0, arg1, arg2);
	}

	@Override
	public String getText(String arg0, String arg1, List<?> arg2) {
		return textProvider.getText(arg0, arg1, arg2);
	}

	@Override
	public String getText(String arg0, String arg1, String[] arg2) {
		return textProvider.getText(arg0, arg1, arg2);
	}

	@Override
	public String getText(String arg0, String arg1, List<?> arg2,
			ValueStack arg3) {
		return textProvider.getText(arg0, arg1, arg2, arg3);
	}

	@Override
	public String getText(String arg0, String arg1, String[] arg2,
			ValueStack arg3) {
		return textProvider.getText(arg0, arg1, arg2, arg3);
	}

	@Override
	public ResourceBundle getTexts() {
		return textProvider.getTexts();
	}

	@Override
	public ResourceBundle getTexts(String arg0) {
		return textProvider.getTexts(arg0);
	}

	@Override
	public boolean hasKey(String arg0) {
		return textProvider.hasKey(arg0);
	}
	public static Properties getErrMsgs() {
		return errMsgs;
	}
	public static void setErrMsgs(Properties errMsgs) {
		SystemPropertyConfig.errMsgs = errMsgs;
	}

	public static SysAppDate getAmsAppDate() {
		return amsAppDate;
	}

	public static void setAmsAppDate(SysAppDate amsAppDate) {
		SystemPropertyConfig.amsAppDate = amsAppDate;
	}

	public static void setConfigMode(int configMode) {
		SystemPropertyConfig.configMode = configMode;
	}

	public static List<String> getListCustomerTestInternalSc() {
		return listCustomerTestInternalSc;
	}

	public static void loadListCustomerTestInternalSc() {
		List<String> listCustomer = new ArrayList<String>();
		if (!StringUtil.isEmpty(getConfigProperties("list.test.internal.sc.customer"))) {
			String[] arrCustomer = getConfigProperties("list.test.internal.sc.customer").trim().split(",");
			listCustomer = Arrays.asList(arrCustomer); 
			Helper.listTrim(listCustomer);
		}
		SystemPropertyConfig.listCustomerTestInternalSc = listCustomer;
	}
	
}
