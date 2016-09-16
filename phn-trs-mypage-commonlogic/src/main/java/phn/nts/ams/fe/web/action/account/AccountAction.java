package phn.nts.ams.fe.web.action.account;


import com.opensymphony.xwork2.ActionContext;
import com.phn.mt.common.util.DateUtil;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWhitelabelConfigId;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.*;
import phn.nts.ams.fe.model.AccountModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.security.RoseIndiaCaptcha;
import phn.nts.social.fe.web.action.BaseSocialAction;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class AccountAction extends BaseSocialAction<AccountModel> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logit log = Logit.getInstance(AccountAction.class);
    private AccountModel model = new AccountModel();
    private IAccountManager accountManager = null;
//    private IBalanceManager balanceManager = null;
    private String result;
    private String msgCode;
    private static Properties propsConfig;
    private static final String CONFIGPATH = "configs.properties";
    private static final String HOMEPAGE_URL = "hompagedocument.url";

    /**
     * @param accountManager the accountManager to set
     */
    public void setAccountManager(IAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public IAccountManager getAccountManager() {
        return accountManager;
    }

    public AccountModel getModel() {
        return model;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    public String sigin() {
        // read user language on cookies
        readUserLanguage();
        if (result != null) {
            getMsgCode(result);
        }
        return SUCCESS;
    }

    /**
     *
     * read user language on cookie
     *
     * @param
     * @return
     * @auth QuyTM
     * @CrDate Oct 2, 2012
     * @MdDate
     */
    public void readUserLanguage() {
        String language = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(IConstants.COOKIES.USER_LANGUAGE)) {
                    language = Utilities.trim(c.getValue());
                }
            }
        }
        if (language != null && !StringUtils.isBlank(language)) {
            setUserLanguage(language);
        }
    }

    public String index() {
        setRawUrl(IConstants.FrontEndActions.ACCOUNT_HOME);
        try {
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            FrontUserOnline frontUserOnline = null;
            String wlCode = "";
            if (frontUserDetails != null) {
                frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    String customerId = frontUserOnline.getUserId();
                    String currency = frontUserOnline.getCurrencyCode();
                    BalanceInfo balanceAmsInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currency);
                    BalanceInfo balanceFxInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currency);
                    BalanceInfo balanceBoInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currency);

                    model.setBalanceAmsInfo(balanceAmsInfo);
                    model.setBalanceBoInfo(balanceBoInfo);
                    model.setBalanceFxInfo(balanceFxInfo);
                    model.setCurrencyCode(currency);
                    wlCode = frontUserOnline.getWlCode();
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return SUCCESS;
    }

    public String home() {
        try {
            PagingInfo pagingInfo = model.getPagingInfo();
            if (pagingInfo == null) {
                pagingInfo = new PagingInfo();
                model.setPagingInfo(pagingInfo);
            }
            // get balance on MT4
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            FrontUserOnline frontUserOnline = null;
            if (frontUserDetails != null) {
                frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    // start setting locale for user login
                    Map<String, String> mapFrontEndConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + frontUserOnline.getWlCode() + "_" + IConstants.SYS_PROPERTY.FRONT_END);
                    Integer totalRecordOnPage = MathUtil.parseInteger(mapFrontEndConfiguration.get(IConstants.FRONT_END_CONFIG.MESSAGE_TOTAL_RECORD_ON_PAGE));
                    if (totalRecordOnPage == null) {
                        totalRecordOnPage = IConstants.FRONT_OTHER.MESSAGE_TOTAL_RECORD_ON_PAGE;
                    }
                    pagingInfo.setOffset(totalRecordOnPage);
                    String language = IConstants.Language.ENGLISH;
                    if (frontUserOnline.getLanguage() != null && !StringUtils.isBlank(frontUserOnline.getLanguage())) {
                        language = frontUserOnline.getLanguage();
                    }
                    setUserLanguage(language);


                    // end setting locale for user login

                    String customerId = frontUserOnline.getUserId();
                    String currency = frontUserOnline.getCurrencyCode();
                    // get balance of AMS
                    BalanceInfo balanceAmsInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currency);
                    // get balance of FX
                    BalanceInfo balanceFxInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currency);
                    // get balance of BO
                    BalanceInfo balanceBoInfo = balanceManager.getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currency);

                    model.setBalanceAmsInfo(balanceAmsInfo);
                    model.setBalanceBoInfo(balanceBoInfo);
                    model.setBalanceFxInfo(balanceFxInfo);
                    model.setCurrencyCode(currency);

                    // get List of news
                    List<MessageInfo> listMessageInfo = accountManager.getMessageList(customerId, pagingInfo);
                    if (listMessageInfo != null && listMessageInfo.size() > 0) {
                        model.setListMessage(listMessageInfo);
                        // End getting messages
                    }

                    //[NTS1.0-Quan.Le.Minh]Jan 22, 2013A - Start
                    if (frontUserDetails.isFromSigninPage()) {
                        frontUserDetails.setFromSigninPage(false);
                        CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(customerId, IConstants.SERVICES_TYPE.FX);
                        Integer serviceStatus = customerServiceInfo.getCustomerServiceStatus();
                        if (serviceStatus.equals(IConstants.CUSTOMER_SERVIVES_STATUS.CERTIFICATED_DOCSWAITING) ||
                                serviceStatus.equals(IConstants.CUSTOMER_SERVIVES_STATUS.BACK_TO_INSPECT)) {
                            model.setVerifyMessage(getText("nts.ams.label.home_screen.message.verify"));
                            model.setUploadDoc(1); /*Upload docs waiting*/
                        }
                        if (serviceStatus.equals(IConstants.CUSTOMER_SERVIVES_STATUS.ACCOUNT_OPEN_REQUESTING)) {
                            model.setVerifyMessage(getText("nts.ams.label.home_screen.message.verifying"));
                            model.setUploadDoc(2); /*Uploaded docs*/
                        }
                    } else {
                        model.setUploadDoc(3); /*Not from Sign in page*/
                    }
                    //[NTS1.0-Quan.Le.Minh]Jan 22, 2013A - End
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return SUCCESS;
    }

    public String accountRegistration() {
        Integer serviceTypeId = MathUtil.parseInteger(model.getServiceTypeId());
        if (serviceTypeId == null) {
            return ERROR;
        }
        Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
        String serviceType = mapServiceType.get(model.getServiceTypeId());
        model.setServiceType(serviceType);
        return INPUT;
    }


    public String accountRegistrationSubmit() {
        try {
            String serviceTypeId = model.getServiceTypeId();
            Integer customerServiceType = MathUtil.parseInteger(serviceTypeId);
            if (customerServiceType != null) {
                // if customer checked checkbox -> start process openaccount
                String customerId = "";
                String wlCode = "";
                String currencyCode = "";

                FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
                FrontUserOnline frontUserOnline = null;
                if (frontUserDetails != null) {
                    frontUserOnline = frontUserDetails.getFrontUserOnline();
                    if (frontUserOnline != null) {
                        customerId = frontUserOnline.getUserId();
                        wlCode = frontUserOnline.getWlCode();
                        currencyCode = frontUserOnline.getCurrencyCode();
                    }
                }
                CustomerInfo customerInfo = accountManager.getCustomerInfo(customerId);
                if (customerInfo != null) {
                    customerInfo.setWlCode(wlCode);
                    customerInfo.setCurrencyAms(currencyCode);
                    // update MT4
                    Boolean result = false;
                    if (IConstants.FRONT_OTHER.FX_BO_SERVICE.equals(customerServiceType)) {
                        // register customer service with type = fx and bo
                        result = accountManager.registerCustomerService(customerInfo, IConstants.FRONT_OTHER.FX_BO_SERVICE);
//						result = accountManager.registerCustomerService(customerInfo, IConstants.SERVICES_TYPE.BO);
                        if (result) {
                            Map<Integer, Boolean> mapCustomerService = frontUserOnline.getMapCustomerService();
                            mapCustomerService.put(IConstants.SERVICES_TYPE.FX, Boolean.TRUE);
                            mapCustomerService.put(IConstants.SERVICES_TYPE.BO, Boolean.TRUE);
                        } else {
                            model.setErrorMessage(getText("nts.ams.fe.message.profile.create.account.unsuccess"));
                            return ERROR;
                        }

                    } else {
                        result = accountManager.registerCustomerService(customerInfo, customerServiceType);
                        if (result) {
                            Map<Integer, Boolean> mapCustomerService = frontUserOnline.getMapCustomerService();
                            mapCustomerService.put(customerServiceType, Boolean.TRUE);
                        } else {
                            model.setErrorMessage(getText("nts.ams.fe.message.profile.create.demo.unsuccess"));
                            return ERROR;
                        }
                    }
                } else {
                    log.warn("Cannot find customer for customerId: " + customerId);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ERROR;
        }

        return SUCCESS;
    }

    public String openAccountSuccess() {

        return INPUT;
    }

    /**
     * @param balanceManager the balanceManager to set
     */
    public void setBalanceManager(IBalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    public IBalanceManager getBalanceManager() {
        return balanceManager;
    }

    /**
     * 　redirect to view forgot password page
     *
     * @param
     * @return
     * @auth HuyenMT
     * @CrDate Sep 20, 2012
     * @MdDate
     */
    public String viewForgotPassword() {
    	Locale locale = new Locale("ja");
		ActionContext.getContext().setLocale(locale);
        CustomerInfo customerInfo = model.getCustomerInfo();
        Map<Integer, String> mapCorporationType=new HashMap<Integer, String>();
        if(httpRequest.getAttribute("service")!=null){
        	model.setService(httpRequest.getAttribute("service").toString());
        }
        mapCorporationType.put(1, getText("nts.ams.fe.label.corporation.corp"));
        mapCorporationType.put(0, getText("nts.ams.fe.label.corporation.indv"));
//        mapCorporationType.put(1, "法人");
//        mapCorporationType.put(0, "個人");
        model.setMapCorporationType(mapCorporationType);
        if (customerInfo == null) {
            customerInfo = new CustomerInfo();
            model.setCustomerInfo(customerInfo);
         }
        return SUCCESS;
    }

    /**
     *
     *
     * @param
     * @return
     * @auth HuyenMT
     * @CrDate Sep 20, 2012
     * @MdDate
     */
    public String resetPasswordOld() {
        try {
            HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(org.apache.struts2.StrutsStatics.HTTP_REQUEST);
            javax.servlet.http.HttpSession session = request.getSession();
            String c = (String) session.getAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
            String parm = request.getParameter("j_captcha_response");

            CustomerInfo customerInfo = model.getCustomerInfo();
            if (customerInfo == null) {
                customerInfo = new CustomerInfo();
            }
            String email = customerInfo.getLoginId();
            //validate email
            if (!(StringUtil.isEmail(email))) {
                List<Object> listMsg = new ArrayList<Object>();
                listMsg.add(getText("nts.ams.fe.label.account.email"));
                //listMsg.add(getText("global.message.NAB007_2"));
                model.setErrorMessage(getText("MSG_NAB053", listMsg));
                addFieldError("errorMessage", getText("MSG_NAB053", listMsg));
                session.removeAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
                return INPUT;
            }
            //compare verification code
            if (!parm.equals(c)) {
                String strError = getText("nts.ams.fe.label.account.invalid.verification.code");
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                session.removeAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
                return INPUT;
            }
            session.removeAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
            if (hasFieldErrors()) {
                return INPUT;
            }
            //check email existed
            Boolean isEmailExisted = accountManager.isEmailExisting(email);
            if (!isEmailExisted) {
                String strError = getText("nts.ams.fe.label.account.not.existed.email");
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                session.removeAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
                return INPUT;
            }
            Boolean result = accountManager.resetPassword(email);
            if (!result) {
                String strError = getText("nts.ams.fe.label.account.reset.password.error");
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                session.removeAttribute(RoseIndiaCaptcha.CAPTCHA_KEY);
                return INPUT;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return INPUT;
        }
        return SUCCESS;
    }
    /**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 17, 2013
	 */
    public String resetPassword() {
        try {
        	Locale locale = new Locale("ja");
			ActionContext.getContext().setLocale(locale);
        	Map<Integer, String> mapCorporationType=new HashMap<Integer, String>();
            mapCorporationType.put(1, getText("nts.ams.fe.label.corporation.corp"));
            mapCorporationType.put(0, getText("nts.ams.fe.label.corporation.indv"));
//            mapCorporationType.put(1, "法人");
//            mapCorporationType.put(0, "個人");
            model.setMapCorporationType(mapCorporationType);
            CustomerInfo customerInfo = model.getCustomerInfo();
            if (customerInfo == null) {
                customerInfo = new CustomerInfo();
            }
            String email = customerInfo.getLoginId();
            String birthday = customerInfo.getBirthday();
            Integer corpType = customerInfo.getCorporationType();
            //verify login start
            if(email==null||email==""||email.trim()==""||"".equalsIgnoreCase(email.trim())){
            	String strError = getText("MSG_NAB001",getText("nts.ams.message.login.id").split(","));
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return INPUT;
            }
            //verify birthday start
            if(birthday==null||birthday==""||birthday.trim()==""||"".equalsIgnoreCase(birthday.trim())){
            	String strError="";
            	if(corpType.intValue()==0){
            		strError = getText("MSG_NAB001", getText("nts.ams.message.birth.date").split(","));
            	}else{
            		strError = getText("MSG_NAB001", getText("nts.ams.message.established.date").split(","));
            	}
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return INPUT;
            }
            //validate email
//            if (!(StringUtil.isEmail(email))) {
//                List<Object> listMsg = new ArrayList<Object>();
//                listMsg.add("ログインID");
//                model.setErrorMessage(getText("MSG_NAB053", listMsg));
//                addFieldError("errorMessage", getText("MSG_NAB053", listMsg));
//                return INPUT;
//            }
			try {
				if (corpType.intValue() == 0) {
					Date date = phn.com.nts.util.common.DateUtil.toDate(birthday, phn.com.nts.util.common.DateUtil.PATTERN_YYMMDD);
					if (date == null) {
						String strError = getText("MSG_NAB001", getText("nts.ams.message.birth.date").split(","));
		                model.setErrorMessage(strError);
		                addFieldError("errorMessage", strError);
		                return INPUT;
					}
				} else {
					Date date = phn.com.nts.util.common.DateUtil.toDate(birthday,"yyyy/MM");
					if (date == null) {
						String strError = getText("MSG_NAB001", getText("nts.ams.message.established.date").split(","));
		                model.setErrorMessage(strError);
		                addFieldError("errorMessage", strError);
		                return INPUT;
					}

				}
			}catch(Exception ex){
				String strError="";
				if (corpType.intValue() == 0){
					strError = getText("MSG_NAB001", getText("nts.ams.message.birth.date").split(","));
				}else{
					strError = getText("MSG_NAB001", getText("nts.ams.message.established.date").split(","));
				}
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return INPUT;
        	}
          //verify birthday end
            if (hasFieldErrors()) {
                return INPUT;
            }
            //check email existed
            Boolean isEmailExisted = accountManager.checkUserExisting(email, birthday,corpType);
            if (!isEmailExisted) {
            	String strError="";
            	if(corpType.intValue()==0){
            		strError = getText("MSG_TRS_NAF_0042",getText("MSG_TRS_NAF_0042_indv").split(","));
            	}else{
            		strError = getText("MSG_TRS_NAF_0042",getText("MSG_TRS_NAF_0042_corp").split(","));
            	}
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return INPUT;
            }
            Boolean result = accountManager.resetPasswordExtend(email);
            if (!result) {
                String strError = getText("nts.ams.fe.label.account.reset.password.error");
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return INPUT;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return INPUT;
        }
        return SUCCESS;
    }

    /**
     *
     *
     * @param
     * @return
     * @auth HuyenMT
     * @CrDate Sep 21, 2012
     * @MdDate
     */
    public String verifyLinkOld() {
        try {
            log.info("[Start] get information from reset password link");
            String mailActiveCode = httpRequest.getParameter("mailactivecode");
            String email = httpRequest.getParameter("emailaddr");
            log.info("mail active code" + mailActiveCode + "email " + email);
            log.info("[End] get information from reset password link");
            Boolean result = accountManager.verifyPassword(mailActiveCode, email);
            if (!result) {
                setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
                return ERROR;
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ERROR;

        }
        return SUCCESS;
    }
    /**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 17, 2013
	 */
    public String verifyLink() {
        try {
            log.info("[Start] get information from reset password link");
            String mailActiveCode = httpRequest.getParameter("mailactivecode");
            String email = httpRequest.getParameter("emailaddr");
            String birthday = httpRequest.getParameter("birthday");
            boolean validUser = accountManager.checkVerifyUser(email, birthday, mailActiveCode);
           if(validUser==false){
            	String strError="";
            	String[] param = new String[1];
//            	param[0]=httpRequest.getRequestURI();
//            	param[0]=httpRequest.getRequestURL().toString();
            	param[0]="URL";
           		strError = getText("MSG_TRS_NAF_0042",param);
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return ERROR;
            }else{
            	model.setUserLoginId(email);
            	model.setVerifyCode(mailActiveCode);
            }
//            Boolean result = accountManager.verifyPassword(mailActiveCode, email);
//            if (!result) {
//                setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
//                return ERROR;
//            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ERROR;

        }
        return SUCCESS;
    }
    
    public String changePassword() {
        try {
            log.info("[Start] get information from reset password link");
            String loginId = model.getUserLoginId();
            String newPass = model.getNewPassWord();
            String newPassConfirm = model.getNewPassWordConfirm();
            String activeCode = model.getVerifyCode();
            if(newPass==null||newPassConfirm==null){
            	String strError="";
           		strError = getText("MSG_NAB001",getText("nts.ams.fe.label.account.reset.password").split(","));
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return ERROR;
            }
            if(newPass==""||newPassConfirm==""){
            	String strError="";
           		strError = getText("MSG_NAB001",getText("nts.ams.fe.label.account.reset.password").split(","));
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return ERROR;
            }
            
            if(newPass.length()<6||newPass.length()>12){
            	String strError="";
           		strError = getText("MSG_SC_079",getText("nts.ams.fe.label.account.reset.password").split(","));
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return ERROR;
            }
            if(!newPass.equalsIgnoreCase(newPassConfirm)){
            	String strError="";
            	strError = getText("MSG_SC_079",getText("nts.ams.fe.label.account.reset.password").split(","));
                model.setErrorMessage(strError);
                addFieldError("errorMessage", strError);
                return ERROR;
            }
            Boolean result = accountManager.updatePassword(loginId, newPass,activeCode);
            if (!result) {
            	model.setErrorMessage( getText("MSG_NAB111"));
                return INPUT;
            }
        } catch (Exception e) {
        	model.setErrorMessage( getText("MSG_NAB111"));
            log.info(e.getMessage(), e);
            return ERROR;

        }
        return SUCCESS;
    }
    /**
     * @param msgCode the msgCode to set
     */
    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    /**
     * getMsgCode
     *
     * @param
     * @return
     * @auth Mai.Thu.Huyen
     * @CrDate Oct 12, 2012
     * @MdDate
     */
    private void getMsgCode(String msgCode) {
        if (msgCode != null) {
            if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS)) {
                model.setSuccessMessage(getText("nts.ams.fe.label.account.reset.password.error"));
            }
        }
    }
    
	/**
	 * get Balance 
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 25, 2013
	 */
	public String getBalance(){
		try{
			Integer baseServiceType = MathUtil.parseInteger(model.getServiceType());
			String customerId = getModel().getCurrentCustomerId();
			String currency = getCurrencyCode();
			Double total = new Double(0);
			
			if(baseServiceType != null){
				if(baseServiceType.equals(IConstants.SERVICES_TYPE.ALL)){
					loadSCFE005UserPortfolioData();
					loadUserBalanceInfo();
				}else{
					BalanceInfo balanceInfo = getBalanceManager().getBalanceInfo(customerId, baseServiceType , currency);
					if(balanceInfo != null){
						if(baseServiceType.equals(IConstants.SERVICES_TYPE.BO) ){
//							BalanceInfo balanceBoInfo = new BalanceInfo();
		//					if(balanceInfo.getBalance() != null){
		//						BigDecimal balance = getBalanceManager().getBalanceWithConvertRate(balanceInfo.getBalance(),balanceInfo.getCurrencyCode(),currency);
		//						Double boBalance = Double.parseDouble(balance.toString());
		//					}
							getModel().setBalanceBoInfo(balanceInfo);
						}
						
						if(baseServiceType.equals(IConstants.SERVICES_TYPE.AMS)){
							getModel().setBalanceAmsInfo(balanceInfo);
						}
						
						if(baseServiceType.equals(IConstants.SERVICES_TYPE.FX)){
							getModel().setBalanceFxInfo(balanceInfo);
						}else{
							getModel().setBalanceFxInfo( new BalanceInfo());
						} 
						
						if(baseServiceType.equals(IConstants.SERVICES_TYPE.COPY_TRADE)){							
							getModel().setBalanceScInfo(balanceInfo);
						}else{
							getModel().setBalanceScInfo( new BalanceInfo());
						}
					}
				}
			}
		}catch(Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	public String agree(){
		log.info("[start] load news agree screen");
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline!=null){
				getModel().setMyPageUrl(frontUserOnline.getMyPageUrl());
			}
		}else{
			log.warn("Can not get user online information");
		}
		log.info("[end] load news agree screen");
		return SUCCESS;
	}
	
	public String agreeNormalNews(){
		log.info("Start open screen normal news agree");
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		AmsMessage amsMessage = frontUserDetails.getFrontUserOnline().getNormalNewsMessage();
		if(amsMessage == null){
			log.error(" Error screen normal new WITH REASON ---> amsMessage is null ---> SOLUTION redirect social/home ");
			return "home";
		}else{
			getModel().setNormalNewsMessage(amsMessage);
		}
		
		return SUCCESS;
	}
	
	public void initMapDataBoRegistration(){
		/*PURPOSE BO Hedge Type*/
		Map<Integer, String> mapPurposeBoHedgeType = MasterDataManagerImpl.getInstance().getImmutableData().getMapPurposeBoHedgeType();
		model.setMapPurposeBoHedgeType(getTextMapIn(mapPurposeBoHedgeType));
		/*PURPOSE BO Hedge Amount*/
		Map<Integer, String> mapPurposeBoHedgeAmount = MasterDataManagerImpl.getInstance().getImmutableData().getMapPurposeBoHedgeAmount();
		
		model.setMapPurposeBoHedgeAmount(getTextMapIn(getMap(mapPurposeBoHedgeAmount)));
		/*PURPOSE BO*/
		Map<Integer, String> mapPurposeBo = MasterDataManagerImpl.getInstance().getImmutableData().getMapPurposeBo();
		model.setMapPurposeBo(getTextMapIn(mapPurposeBo));
	}
	
	private Map<Integer, String> getMap(Map<Integer, String> mapPurposeBoHedgeAmount){
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		Map<Integer, String> map = new HashMap<Integer, String>();
		if(frontUserDetails!=null){
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			Integer selfAsset = frontUserOnline.getFinalcialSelfAssets();
			for (Map.Entry<Integer, String> entry : mapPurposeBoHedgeAmount.entrySet())
			{
			   if(entry.getKey() <= selfAsset)
				   map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}
	
	public String initBoRegistration(){
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline!=null){
				CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
				if(customerServiceInfoAccount != null && customerServiceInfoAccount.getCustomerServiceStatus() != ITrsConstants.ACCOUNT_OPEN_STATUS.BEFORE_REGISTER){
					return "home";
				}
			}
			initMapDataBoRegistration();
		}
		catch(Exception ex){
			LOG.error("Failed in initBoRegistration(): ", ex);
		}
		return SUCCESS;
	}
	
	public String registrationConfirm(){
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails!=null){
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				initMapDataBoRegistration();
				BoRegisInfo regisInfo = model.getBoRegisInfo();
				if(!validateBoRegis(frontUserOnline.getWlCode(),frontUserOnline.getFinalcialSelfAssets())){
					return ERROR;
				}
				List<Integer> serviceTypeChecked = regisInfo.getListPurposeBo();
				model.setServiceTypeChecked(serviceTypeChecked);
			}	
		}
		catch(Exception ex){
			LOG.error("Fail in action registrationConfirm(): ", ex);
		}
	
		return SUCCESS;
	}
	
	public String registrationSave(){
		try{
			initMapDataBoRegistration();
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails!=null){
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					if(!validateBoRegis(frontUserOnline.getWlCode(),frontUserOnline.getFinalcialSelfAssets())){
						return ERROR;
					}else{
						BoRegisInfo regisInfo = model.getBoRegisInfo();
						boolean flag = accountManager.updateCustomer(frontUserOnline.getUserId(), regisInfo);
						if(!flag){
							return ERROR;
						}
					}
				}
			}else{
				return ERROR;
			}
		}
		catch(Exception ex){
			LOG.error("Exception in registrationSave(): ", ex);
		}
		return SUCCESS;
	}
	
	public boolean validateBoRegis(String wlCode,Integer finalcialAssets){
		BoRegisInfo info = model.getBoRegisInfo();
		String msg;
		List<Integer> listPurposeBo = info.getListPurposeBo();
		if (listPurposeBo == null || listPurposeBo.size() == 0) {
			msg = getText("MSG_NAF001", new String[] { getText("bo_registration.purpose_of_BO") });
			model.setErrorMessage(msg);
			return false;
		}
		for (Integer i : listPurposeBo) {
			if (ITrsConstants.REGISTER_CUSTOMER.PURPOSE_BO.HEDGE.equals(i)) {
				if (info.getPurposeBoHedgeType() == null || info.getPurposeBoHedgeType() == -1) {
					msg = getText("MSG_NAF001", new String[] { getText("bo_registration.hedge.type") });
					model.setErrorMessage(msg);
					return false;
				}
				if (info.getPurposeBoHedgeAmount() == null || info.getPurposeBoHedgeAmount() == -1) {
					msg = getText("MSG_NAF001", new String[] { getText("bo_registration.hedge.amount") });
					model.setErrorMessage(msg);
					return false;
				}
			}
		}
		
		/*MAX LOSS AMOUNT BO*/
		
		
		
		String maxLossStr = info.getMaxLossAmountBo();
		if (StringUtil.isEmpty(maxLossStr)) {
			msg = getText("MSG_NAF001", new String[]{getText("bo_registration.max_loss_mount_bo")});
			model.setErrorMessage(msg);
			return false;
		}
		
		BigDecimal maxLossAmount = MathUtil.parseBigDecimal(maxLossStr);
		if (maxLossAmount == null) {
			msg = getText("MSG_NAF013", new String[]{getText("bo_registration.max_loss_mount_bo")});
			model.setErrorMessage(msg);
			return false;
		}
		
		BigDecimal defaultMaxLossAmount = getDefaultBoMaxLossAmount(finalcialAssets,wlCode);
		if (defaultMaxLossAmount == null) {
			return false;
		}
		
		if (maxLossAmount.compareTo(defaultMaxLossAmount) == 1) {
			if (ITrsConstants.REGISTER_CUSTOMER.ANNUAL_INCOME_SELF_ASSET.MONEY30UNDER == info.getPurposeBoHedgeAmount().intValue()) {
				msg = getText("MSG_TRS_NAF_0060", new String[]{String.valueOf(defaultMaxLossAmount)});
			} else {
				BigDecimal defaultMaxLossAmountMoney30Under = getDefaultBoMaxLossAmount(ITrsConstants.REGISTER_CUSTOMER.ANNUAL_INCOME_SELF_ASSET.MONEY30UNDER,wlCode);
				if (defaultMaxLossAmountMoney30Under == null) {
					model.setErrorMessage(getText("MSG_NAF005"));
					return false;
				}
				msg = getText("MSG_TRS_NAF_0061", new String[]{String.valueOf(defaultMaxLossAmountMoney30Under), String.valueOf(defaultMaxLossAmount)});
			}
			model.setErrorMessage(msg);
			return false;
		}
		return true;
	}
	
	private BigDecimal getDefaultBoMaxLossAmount(Integer financialAssets,String wlCode) {
		StringBuffer configKey = new StringBuffer().append(ITrsConstants.WHILELABEL_CONFIG_KEY.BO_MAX_LOSS_AMOUNT).append(financialAssets);
		AmsWhitelabelConfigId id = new AmsWhitelabelConfigId(configKey.toString(), wlCode);
		AmsWhitelabelConfig config = MasterDataManagerImpl.getInstance().getMapWhiteLabelConfig().get(id);
		if (config == null) {
			log.warn("Cannot find AmsWhitelabelConfig with ConfigKey = " + configKey);
			model.setErrorMessage(getText("MSG_NAB005"));
			return null;
		}
		BigDecimal defaultMaxLossAmount = MathUtil.parseBigDecimal(config.getConfigValue());
		if (defaultMaxLossAmount == null) {
			log.warn("ConfigValue of AmsWhitelabelConfig with ConfigKey = " + configKey + " is invalid = " + config.getConfigValue());
			model.setErrorMessage(getText("MSG_NAB005"));
			return null;
		}
		return defaultMaxLossAmount;
	}
}

