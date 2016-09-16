package phn.nts.ams.fe.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.log.Logit;


/**
 * 
 * @author Nguyen Xuan Bach
 *
 */
public class AppConfiguration {
	private static final Logit log = Logit.getInstance(AppConfiguration.class);
	private static Properties propsConfig;
	private static final String APP_PROPS_FILE = "configs.properties";
	private static final int PORT_DEFAULT = 587;	
	public static final String MAIL_HOST = "global.mail.host";
	public static final String MAIL_USERNAME = "global.mail.username";
	public static final String MAIL_PASSWORD = "global.mail.password";
	public static final String MAIL_PORT = "global.mail.port";
	public static final String MAIL_ADMIN_SENDER = "global.mail.admin.sender";
	public static final String MAIL_ADMIN_RECEIVER = "global.mail.admin.receiver";
	public static final String MAIL_OM_SENDER = "global.mail.om.receiver";
	public static final String MAIL_ACCOUNTING="mail.accounting";
	public static final String HOMEPAGE_URL="global.homepage.url";
	public static final String WEB_XML_TIMEOUT="web.xml.TimeOut";
	public static final String LOGOUT_URL="logOut.url";
	
	private AppConfiguration(){}
    
    static {
        try {
            propsConfig = Helpers.getProperties(APP_PROPS_FILE);                      
        } catch(Exception e) {
            log.warn("Could not load configuration file from: " + APP_PROPS_FILE, e);
        }
    }
  
    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        String value = propsConfig.getProperty(key);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }
   
	public static List<String> getList(String key) {
    	String value = propsConfig.getProperty(key);
    	if (value == null || value.length() == 0) {
    		return new ArrayList<String>();
    	}
    	String[] list = value.split(";");
        
    	return Arrays.asList(list);
	}

	public static String getMailHost() {
		return getString(MAIL_HOST);
	}

	public static String getMailUsername() {
		return getString(MAIL_USERNAME);
	}

	public static String getMailPassword() {
		return getString(MAIL_PASSWORD);
	}

	public static String getMailAdminSender() {
		return getString(MAIL_ADMIN_SENDER);
	}
	public static String getMailAccounting() {
		return getString(MAIL_ACCOUNTING);
	}
	public static String getMailOMReceiver() {
		return getString(MAIL_OM_SENDER);
	}
	public static String getHomepageUrl() {
		return getString(HOMEPAGE_URL);
	}
	public static String getWebTimeOut() {
		return getString(WEB_XML_TIMEOUT);
	}
	public static String getLogoutUrl() {
		return getString(LOGOUT_URL);
	}
	
	public static int getMailPort() {
		String port = getString(MAIL_PORT);
		int portMail = -1;
		try {
			portMail = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			portMail = PORT_DEFAULT;
		}
		return portMail;
	}

	public static List<String> getListMailAdminReceiver() {
		return getList(MAIL_ADMIN_RECEIVER);
	}
}
