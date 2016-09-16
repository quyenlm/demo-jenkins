package phn.nts.ams.fe.web.action;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class WebAction extends ActionSupport implements SessionAware, ServletResponseAware, ServletRequestAware {

    private static final long serialVersionUID = 1L;
    private static final Logit m_log = Logit.getInstance(WebAction.class);
    protected static final String KEY_LANGUAGE = "SESSION_KEY_LANGUAGE";
    protected Map<String, Object> session;
    protected HttpServletResponse httpResponse;
    protected HttpServletRequest httpRequest;
    protected String sessionLanguageKey;
    private String language;
    private String rawUrl;
    private String currencyCode;
    private String ticketId;
    private String boUrl;
    private Integer mode = IConstants.SOCIAL_MODES.OWNER_MODE;
    private static Properties propsConfig;
    private static final String CONFIGPATH = "configs.properties";
    private static final String WEBTRADE_URL = "webtrade.url";
    

    /**
     *
     * get session from cache
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Jul 18, 2012
     * @MdDate
     */
    public void setSession(Map<String, Object> session) {
        this.session = session;
//        language = getSessionLocale().toString();
    }

    /**
     *
     * setting language
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Jul 18, 2012
     * @MdDate
     */
    public void settingLocale() {
        session.put(KEY_LANGUAGE, language);
        // put into cookies

    }

    public String getUserLanguage() {
        if (session == null) {
            return null;
        } else {
            return (String) session.get(KEY_LANGUAGE);
        }
    }


//    @Override
//    public Locale getLocale() {
////        return getSessionLocale();
//    	return new Locale("js","JP");
//    }

    /**
     * @return the rawUrl
     */
    public String getRawUrl() {
        return rawUrl;
    }

    /**
     * @param rawUrl the rawUrl to set
     */
    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @param currencyCode the currencyCode to set
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void readUserInformation() {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        FrontUserOnline frontUserOnline = null;
        if (frontUserDetails != null) {
            frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                setCurrencyCode(frontUserOnline.getCurrencyCode());  
                setTicketId(frontUserOnline.getTicketId());
            }
        }
    }

    public String getCurrentCustomerId() {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            return frontUserOnline.getUserId();
        }
        return null;
    }
    
    public String getRequestUsername() {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            return frontUserOnline.getUserName();
        }
        return null;
    }

    public String getWlCode() {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            return frontUserOnline.getWlCode();
        }
        return null;
    }

    /**
     *
     * set locale
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Jul 18, 2012
     * @MdDate
     */
    public String locale() {
        if (this.language == null) {
            this.language = StringUtil.toLowerCase(IConstants.Language.ENGLISH);
        }
        settingLocale();
        if (getRawUrl() == null) {
            setRawUrl(IConstants.FrontEndActions.ACCOUNT_HOME);
        }
        return "redirect";
    }

    /**
     *
     * set user language with param is language (JA, EN, VI v.v...)
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Oct 2, 2012
     * @MdDate
     */
    public void setUserLanguage(String language) {
        if (language == null) {
            this.language = StringUtil.toLowerCase(IConstants.Language.ENGLISH);
        } else {
            this.language = StringUtil.toLowerCase(language);
        }
        settingLocale();
        // save into cookie
        saveLocaleToCookie();
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            if (frontUserOnline != null) {
                frontUserOnline.setLanguage(language);
            }
        }
    }

    /**
     *
     * get session locale
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Jul 18, 2012
     * @MdDate
     */
    public Locale getSessionLocale() {
        Locale locale = null;
//        sessionLanguageKey = (String) session.get(KEY_LANGUAGE);
//        if (sessionLanguageKey == null) {
//            locale = new Locale(StringUtil.toLowerCase(IConstants.Language.ENGLISH));
//        } else {
//            locale = new Locale(StringUtil.toLowerCase(sessionLanguageKey));
//        }
        FrontUserDetails frontUserDetail = FrontUserOnlineContext.getFrontUserOnline();
        if(frontUserDetail != null) {
        	FrontUserOnline frontUserOnline = frontUserDetail.getFrontUserOnline();
        	if(frontUserOnline != null) {
        		locale = new Locale(StringUtil.toLowerCase(frontUserOnline.getLanguage()));
        	}
        }
        
        if(locale == null){
        	Object requestLocale = session.get("request_locale");
        	if(requestLocale != null){
        		locale = new Locale(StringUtil.toLowerCase(requestLocale.toString()));
        	}
        }
        
        if(locale == null) {
        	m_log.info("get default language");
    		String requestLocale = getCookies(IConstants.COOKIES.REQUEST_LOCALE);
    		m_log.info("request locale is " + requestLocale);
    		if(StringUtil.isEmpty(requestLocale)) {
    			requestLocale = IConstants.Language.ENGLISH.toLowerCase();
    		}
    		session.put("request_locale", requestLocale);
    		locale = new Locale(StringUtil.toLowerCase(requestLocale));
        }
  		
//		ActionContext ctx = ActionContext.getContext();
//		if(ctx != null) {
//			ctx.setLocale(locale);
//		}
        return locale;
    }

    /**
     *
     * save locale to cookies
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Oct 2, 2012
     * @MdDate
     */
    protected void saveLocaleToCookie() {
        Cookie cookie = new Cookie(IConstants.COOKIES.USER_LANGUAGE, language);
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setPath("/");
        httpResponse.addCookie(cookie);
    }
    protected String getCookies(String key) {
    	Cookie[] cookies = httpRequest.getCookies();
    	if(cookies != null && cookies.length > 0) {
    		m_log.info("key for request = " + key);
    		for(Cookie cookie : cookies) {
    			m_log.info("Key: " + cookie.getName() + " = " + cookie.getValue());
    			if(key.equals(cookie.getName())) {
    				return cookie.getValue();
    			}
    		}
    	}
    	return null;
    }
    //@Override
    public void setServletResponse(HttpServletResponse response) {
        this.httpResponse = response;
    }

    //@Override
    public void setServletRequest(HttpServletRequest request) {
        this.httpRequest = request;

    }

    public void prepare() throws Exception {
        try {
            if (httpRequest != null) {
                String id = httpRequest.getParameter("id");
                String currentCustomerId = getCurrentCustomerId();
                if (StringUtil.isEmpty(currentCustomerId)) {
                    mode = IConstants.SOCIAL_MODES.GUEST_MODE;
                } else if (StringUtil.isEmpty(id) || id.equals(currentCustomerId)) {
                    mode = IConstants.SOCIAL_MODES.OWNER_MODE;
                } else {
                    mode = IConstants.SOCIAL_MODES.GUEST_MODE;
                }
        		propsConfig = Helpers.getProperties(CONFIGPATH);
        		String webTradeUrl = propsConfig.getProperty(WEBTRADE_URL);
        		boUrl = webTradeUrl;
                StringBuffer buffer = new StringBuffer();
                buffer.append("AFTER WebAction PREPARE {id = ").append(id).append(", mode = ").append(mode).append(", currentCustomerId = ").append(currentCustomerId).append("}");
                m_log.info(buffer.toString());
            } else {
                m_log.warn("httpRequest is null");
            }
        } catch (Exception ex) {
            m_log.error(ex.getMessage(), ex);
        }
    }

    /**
     * @return the mode
     */
    public Integer getMode() {
        return mode;
    }

	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getBoUrl() {
		return boUrl;
	}

	public void setBoUrl(String boUrl) {
		this.boUrl = boUrl;
	}

	
}
