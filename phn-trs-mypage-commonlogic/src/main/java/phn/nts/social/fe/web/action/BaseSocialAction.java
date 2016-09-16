package phn.nts.social.fe.web.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;
import phn.com.nts.db.domain.FollowPartCustomer;
import phn.com.nts.db.domain.LeaderBoardCustomer;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.ISocialManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SocialBootstrap;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LeaderBoardInfo;
import phn.nts.ams.fe.domain.ScCustomerServiceInfo;
import phn.nts.ams.fe.model.BaseSocialModel;
import phn.nts.ams.fe.model.CustomerGuidelineModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.web.action.WebAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/12/13 8:56 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public abstract class BaseSocialAction<T extends BaseSocialModel> extends WebAction implements ModelDriven<T>, Preparable {
	private static final long serialVersionUID = 1L;
	private static final Logit m_log = Logit.getInstance(BaseSocialAction.class);
    protected ISocialManager socialManager;
    protected IBalanceManager balanceManager;
    protected IProfileManager profileManager;

    protected final String PARAMETER_ID = "id";
    protected final String PARAMETER_ACCOUNT_ID = "accountId";
    protected final String PARAMETER_BROKER_CD = "brokerCd";
    
    public static final String NOT_LOGGED_IN_HOMEPAGE = "/social/marketWatch";
    public static final String LOGGED_IN_HOMEPAGE = "/social/home";

//    private final String DEFAULT_DESCIPTION = "Proud to be a part of NatureForex Social Trading!";
    @Override
    public void prepare() throws Exception {
        super.prepare();
        try {
        	m_log.info("[Start function] prepare "+System.currentTimeMillis());
        	getModel().setCurrentCustomerId(getCurrentCustomerId());
        	getModel().setRequestUsername(getRequestUsername());
    		readUserInformation();
            SocialBootstrap bootstrap = SocialBootstrap.getInstance();
            String layout = bootstrap.getLayoutByServletPath(httpRequest.getServletPath());
            if(!StringUtil.isEmpty(layout)){
            	String account_Id = httpRequest.getParameter(PARAMETER_ACCOUNT_ID);
                String broker_Cd = httpRequest.getParameter(PARAMETER_BROKER_CD);
                String id = httpRequest.getParameter(PARAMETER_ID);
                
                HttpSession httpSession = httpRequest.getSession();
                CopyTradeItemInfo abc = (CopyTradeItemInfo)httpSession.getAttribute("abc");
                if(abc == null){
                	abc = new CopyTradeItemInfo();
                }
                if(account_Id != null && broker_Cd != null) {
                	abc.setAccountId(account_Id);
                	abc.setBrokerCd(broker_Cd);
                	abc.setCustomerId(id);
                } 
                httpSession.setAttribute("abc", abc);
                loadCommonData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE001_USER_INFORMATION)) loadSCFE001UserInformationData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE005_USER_PORTFOLIO)) loadSCFE005UserPortfolioData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE002_COPY_FOLLOW_PART)) loadSCFE002CopyFollowPartData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE007_FOLLOW_PART)) loadSCFE007FollowPartData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE009_LEADER_BOARD)) loadSCFE009LeaderBoardData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE010_LIVE_RATE)) loadSCFE010LiveRateData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE012_PRIVATE_MESSAGE)) loadSCFE012PrivateMessageData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE013_NOTICE)) loadSCFE012NoticeData();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE014_BACKGROUND_SETTING)) loadSCFE014BackgroundSettingData();     
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE005_USER_PORTFOLIO)) loadUserBalanceInfo();
                if(bootstrap.containsComponent(layout, SocialBootstrap.COMPONENT.SCFE025_CUSTOMER_GUIDELINE)) loadCustomerGuidelineData();
                loadPageTitle();
            }
            m_log.info("[Finish function] prepare "+System.currentTimeMillis());
        } catch(Exception ex) {
        	LOG.error(ex.getMessage());
        }
        
    }

    private void loadCustomerGuidelineData() {
    	m_log.info("[Start function] loadCustomerGuidelineData "+System.currentTimeMillis());
        if(!StringUtil.isEmpty(getModel().getCurrentCustomerId()) && getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE) && httpRequest.getServletPath().equals(SocialBootstrap.SERVLET_PATH.SOCIAL_HOME)){
            getModel().setCustomerGuidelineModel(new CustomerGuidelineModel());
            socialManager.loadCustomerGuidelineData(getModel().getCurrentCustomerId(), getModel().getCustomerGuidelineModel());
        }
        m_log.info("[Finish function] loadCustomerGuidelineData "+System.currentTimeMillis());
    }

    private void loadCommonData() throws IOException {
    	m_log.info("[Start function] loadCommonData "+System.currentTimeMillis());
    	HttpSession httpSession = httpRequest.getSession();
    	CopyTradeItemInfo abc = (CopyTradeItemInfo)httpSession.getAttribute("abc");
    	
        String id = httpRequest.getParameter(PARAMETER_ID);
        if (getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE)) {
        	// check accountId & Id belongs to one user
        	String accountId = httpRequest.getParameter(PARAMETER_ACCOUNT_ID);
    		if(!StringUtil.isEmpty(accountId) && !StringUtil.isEmpty(id)){
				if(accountId.length() < 2 || !accountId.substring(0, accountId.length()-2).equals(id)){
					httpResponse.sendRedirect(httpRequest.getContextPath() + LOGGED_IN_HOMEPAGE);
				}
    		}
            getModel().setId(getCurrentCustomerId());
        } else {
            if(getCurrentCustomerId() == null && StringUtil.isEmpty(id) && SocialBootstrap.getInstance().isRequireParamId(httpRequest.getServletPath())){
                String redirectLink = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + "BROKER_OWNER_LINK").get("1");
                httpResponse.sendRedirect(redirectLink);
            }
            // check access permission and redirect to the right place 
            if (SocialBootstrap.getInstance().isRequireParamId(httpRequest.getServletPath())) {
            	boolean isValidUrl = true;
//            	1. If not pass id, redirect to home or login page
            	if(StringUtil.isEmpty(id)){
            		isValidUrl = false;
            	} else {
//            	2. Check valid id or account id parameter
            		// Check valid customerId
            		if(!socialManager.isExistedCustomerId(id)){
            			isValidUrl = false;
            		} else {
            			String accountId = httpRequest.getParameter(PARAMETER_ACCOUNT_ID);
                    	
                    	if (!StringUtil.isEmpty(accountId)) {
                    		// check accountId & Id belongs to one user
                    		if(accountId.length() < 2 || !accountId.substring(0, accountId.length()-2).equals(id)){
                    			isValidUrl = false;
                    		}else if(!socialManager.isExistedAccountId(accountId)){ // Check valid accountId
                    			isValidUrl = false;
                    		}
                    	}
            		}
            		
            	}
            	
            	if (!isValidUrl) {
        			// if account is not auspice, redirect to the right view
        			if (StringUtil.isEmpty(getCurrentCustomerId())) {
        				httpResponse.sendRedirect(httpRequest.getContextPath() + NOT_LOGGED_IN_HOMEPAGE);
        			} else {
        				httpResponse.sendRedirect(httpRequest.getContextPath() + LOGGED_IN_HOMEPAGE);
        			}
        		}
            }
            
            getModel().setId(id);
        }
        
        getModel().setBrokerRegisterLink(SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.BROKER_REGISTER_LINK).get("1"));
        getModel().setGuestMode(getMode().equals(IConstants.SOCIAL_MODES.GUEST_MODE));
       /* final String accountId = httpRequest.getParameter(PARAMETER_ACCOUNT_ID);
        final String brokerCd = httpRequest.getParameter(PARAMETER_BROKER_CD);*/
        final String accountId = abc.getAccountId();//modify to fix bug TRSP-504
        final String brokerCd = abc.getBrokerCd();
        getModel().setCopyDetails(socialManager.getCopyItemDetails(getCurrentCustomerId(), getModel().getId()));
        Collections.sort(getModel().getCopyDetails(), new Comparator<CopyTradeItemInfo>() {
            @Override
            public int compare(CopyTradeItemInfo o1, CopyTradeItemInfo o2) {
            	if(o1 != null && o2 != null) {
            		if (o1.getAccountId().equals(accountId) && o1.getBrokerCd().equals(brokerCd)) return -1;
                    if (o2.getAccountId().equals(accountId) && o1.getBrokerCd().equals(brokerCd)) return 1;
                    if (IConstants.SERVICES_TYPE.SOCIAL_FX.equals(o1.getServiceType())) return -1;
                    if (IConstants.SERVICES_TYPE.SOCIAL_FX.equals(o2.getServiceType())) return 1;
            	}
                return 0;
            }
        });
        if(getModel().getCopyDetails().size() > 0){
            getModel().setSelectedAccount(getModel().getCopyDetails().get(0));
            getModel().setAccountId(getModel().getSelectedAccount().getAccountId());
            getModel().setBrokerCd(getModel().getSelectedAccount().getBrokerCd());
        } else if(!StringUtil.isEmpty(id)){
            ScCustomerServiceInfo service = socialManager.getCustomerService(id, IConstants.SERVICES_TYPE.SOCIAL_FX);
            if(service != null){
                getModel().setAccountId(service.getAccountId());
                getModel().setBrokerCd(service.getBrokerCd());
            }
        }

        if(getModel().getSelectedAccount() != null && IConstants.ACTIVE_FLG.ACTIVE.equals(getModel().getSelectedAccount().getEnableFlg())){
        	getModel().setShowCopyFollow(true);
        }
        m_log.info("[Finish function] loadCommonData "+System.currentTimeMillis());
    }

    protected void loadSCFE007FollowPartData() {
    	m_log.info("[Start function] loadSCFE007FollowPartData "+System.currentTimeMillis());
    	try{
    		int currentMode = getMode();
    		String customerId = "";
    		if (currentMode == IConstants.SOCIAL_MODES.GUEST_MODE){
    			customerId = getModel().getId();
    		}
    		else {
    			customerId = getCurrentCustomerId();
    		}
    		getModel().setId(customerId);
    		//List<CustomerInfo> listFollower = new ArrayList<CustomerInfo>();
    		List<FollowPartCustomer> listFollower = new ArrayList<FollowPartCustomer>();
    		listFollower = socialManager.getListFollower(customerId);
    		getModel().setListFollower(listFollower);
    	}    	
    	catch(Exception e){
    		LOG.error("Error occured when loading FOLLOWPART: "+e.getMessage());
    	}
    	m_log.info("[Finish function] loadSCFE007FollowPartData "+System.currentTimeMillis());
    }

    protected void loadSCFE014BackgroundSettingData() {

    }

    protected void loadSCFE012NoticeData() {

    }

    protected void loadSCFE012PrivateMessageData() {

    }

    protected void loadSCFE010LiveRateData() {

    }

    protected void loadSCFE009LeaderBoardData() {
    	m_log.info("[Start function] loadSCFE009LeaderBoardData "+System.currentTimeMillis());
    	try{
    		String customerId = getCurrentCustomerId();
    		List<LeaderBoardCustomer> listLeaderBoardCustomer = new ArrayList<LeaderBoardCustomer>();
    		listLeaderBoardCustomer = socialManager.getLeaderBoard(customerId);
    		LeaderBoardInfo leaderBoardInfo = new LeaderBoardInfo();
    		for (LeaderBoardCustomer topCustomer:listLeaderBoardCustomer){
    			if (topCustomer.getCustomerId().equals(customerId)) 
    				topCustomer.setDisable(1);
    			else topCustomer.setDisable(0);
    		}
    		leaderBoardInfo.setListLeaderBoardCustomer(listLeaderBoardCustomer);
    		getModel().setLeaderBoardInfo(leaderBoardInfo);	
    	}
    	catch(Exception e){
    		LOG.error("Error occured when loading LEADERBOARD: "+e.getMessage());			
    	}
    	m_log.info("[Finish function] loadSCFE009LeaderBoardData "+System.currentTimeMillis());
    }

    protected void loadSCFE005UserPortfolioData() {
    	try{
    		m_log.info("[Start function] loadSCFE005UserPortfolioData "+System.currentTimeMillis());
    		if(getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE)){
	    		final String customerId = getModel().getCurrentCustomerId();
	    		if(StringUtil.isEmpty(customerId)) return;
	    		
				final String currency = getCurrencyCode();
				final String ticketId = getTicketId();
				final String sequenceId = ticketId + customerId;	
			
			
				m_log.info("[start] init thread for getting ams balance");
				Thread threadBalance = new Thread(new Runnable() {
					@Override
					public void run() {
						m_log.info("[start] get ams balance");
						// get balance of AMS
						BalanceInfo balanceAmsInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.AMS, currency);
						Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(sequenceId);
						if(mapBalanceInfo != null) 
							mapBalanceInfo.put(IConstants.SERVICES_TYPE.AMS, balanceAmsInfo);
						m_log.info("[end] get ams balance");
					}
				});
				threadBalance.start();
				m_log.info("[end] init thread for getting ams balance");
				m_log.info("[start] init thread for getting ams balance");
				threadBalance = new Thread(new Runnable() {
					@Override
					public void run() {
						// get balance of FX
						BalanceInfo balanceFxInfo =  getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.FX, currency);
						Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(sequenceId);
						if(mapBalanceInfo != null) 
							mapBalanceInfo.put(IConstants.SERVICES_TYPE.FX, balanceFxInfo);
					}
				});
				threadBalance.start();
				m_log.info("[end] init thread for getting fx balance");
				
				m_log.info("[start] init thread for getting bo balance");
				threadBalance = new Thread(new Runnable() {
					@Override
					public void run() {
						// get balance of BO
						BalanceInfo balanceFxInfo =  getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.BO, currency);
						Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(sequenceId);
						if(mapBalanceInfo != null) 
							mapBalanceInfo.put(IConstants.SERVICES_TYPE.BO, balanceFxInfo);
					}
				});
				threadBalance.start();
				m_log.info("[end] init thread for getting bo balance");
				
				m_log.info("[start] init thread for getting copy trade balance");
				threadBalance = new Thread(new Runnable() {
					@Override
					public void run() {
						// get balance of Copy_trade
						BalanceInfo balanceScInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currency);
						Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(sequenceId);
						if(mapBalanceInfo != null) 
							mapBalanceInfo.put(IConstants.SERVICES_TYPE.COPY_TRADE, balanceScInfo);
					}
				});
				threadBalance.start();
				m_log.info("[end] init thread for getting copy trade balance");
			} else {
	    		final String customerId = getModel().getId();
	    		if(StringUtil.isEmpty(customerId)) return;
	    		
	    		m_log.info("loadSCFE005UserPortfolioData for guest: " + customerId);
	    		AmsCustomer amsCustomer = socialManager.findCustomerById(customerId);
	    		if(amsCustomer != null){
					final String currency = amsCustomer.getSysCurrency().getCurrencyCode();
					String randomText = Utilities.generateRandomPassword(8);
					final String ticketId = amsCustomer.getLoginId() + "_" + randomText;
					final String sequenceId = ticketId + customerId;
					setTicketId(ticketId);
					setCurrencyCode(currency);
					
					m_log.info("[start] init thread for getting copy trade balance");
					Thread threadBalance = new Thread(new Runnable() {
						@Override
						public void run() {
							// get balance of Copy_trade
							BalanceInfo balanceScInfo = getBalanceManager().getBalanceInfo(customerId, IConstants.SERVICES_TYPE.COPY_TRADE, currency);
							Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(sequenceId);
							if(mapBalanceInfo != null) 
								mapBalanceInfo.put(IConstants.SERVICES_TYPE.COPY_TRADE, balanceScInfo);
						}
					});
					threadBalance.start();
					m_log.info("[end] init thread for getting copy trade balance");
	    		}
	
			}
			
			
			m_log.info("[end] user port folio data");
		}catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
    	m_log.info("[Finish function] loadSCFE005UserPortfolioData "+System.currentTimeMillis());
    }
    private BalanceInfo getBalanceInfo(String ticketId, Integer serviceType) {
    	m_log.info("[Start function] getBalanceInfo - BaseSocialAction "+System.currentTimeMillis());
    	int interval = 10;
    	int counter = 1000;
    	BalanceInfo balanceInfo = null;
    	int i = 0;
    	while(balanceInfo == null) {
    		i++;
    		if(i >= counter) {
    			return null;
    		}
    		Map<Integer, BalanceInfo> mapBalanceInfo = FrontEndContext.getInstance().getMapBalanceInfo(ticketId);				
    		if(mapBalanceInfo == null) {
    			return null;
    		}
    		balanceInfo = (BalanceInfo) ObjectCopy.copy(mapBalanceInfo.get(serviceType));
//    		mapBalanceInfo.remove(serviceType);
    		try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
    	}
    	m_log.info("[Finish function] getBalanceInfo - BaseSocialAction "+System.currentTimeMillis());
    	return balanceInfo;
    			
    }
    public void loadUserBalanceInfo() {
    	m_log.info("[Start function] loadUserBalanceInfo - BaseSocialAction "+System.currentTimeMillis());
    	if(getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE)){
    		String customerId = getModel().getCurrentCustomerId();
        	String ticketId = getTicketId();
        	String sequenceId = ticketId + customerId;
    		String currency = getCurrencyCode();
        	Double total = new Double(0);
        	
        	getModel().setBalanceAmsInfo( new BalanceInfo());
        	getModel().setBalanceFxInfo( new BalanceInfo());
        	getModel().setBalanceScInfo( new BalanceInfo());
        	
        	if(!StringUtil.isEmpty(customerId)){
        		BalanceInfo balanceAmsInfo = getBalanceInfo(sequenceId, IConstants.SERVICES_TYPE.AMS);
        		if(balanceAmsInfo != null){
        			getModel().setBalanceAmsInfo(balanceAmsInfo);
        			if(balanceAmsInfo.getBalance() != null){
        				total = total + balanceAmsInfo.getBalance() ;
        			}
        		} 
        		
        		BalanceInfo balanceFxInfo = getBalanceInfo(sequenceId, IConstants.SERVICES_TYPE.FX);
        		if(balanceFxInfo != null){
        			getModel().setBalanceFxInfo(balanceFxInfo);
        			if(balanceFxInfo.getBalance() != null){
        				total = total + balanceFxInfo.getBalance() ;
        			}
        		}
        		
        		BalanceInfo balanceScInfo = getBalanceInfo(sequenceId, IConstants.SERVICES_TYPE.COPY_TRADE);
        		if(balanceScInfo != null){
        			getModel().setBalanceScInfo(balanceScInfo);
        			if(balanceScInfo.getBalance() != null){
        				total = total + balanceScInfo.getBalance() ;
        			}
        		}
        		
        		BalanceInfo balanceBoInfo = getBalanceInfo(sequenceId, IConstants.SERVICES_TYPE.BO);
        		if(balanceBoInfo != null){
        			getModel().setBalanceBoInfo(balanceBoInfo);
        			if(balanceBoInfo.getBalance() != null){
        				total = total + balanceBoInfo.getBalance() ;
        			}
        		}
        		
        	}
    		
    		getModel().setTotal(total);
    		getModel().setCurrencyCode(currency);
    		Integer scale = new Integer(0);
    		Integer rounding = new Integer(0);
    		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currency);
    		if(currencyInfo != null) {
    			scale = currencyInfo.getCurrencyDecimal();
    			rounding = currencyInfo.getCurrencyRound();
    		}
    		getModel().setBaseScale(StringUtil.toString(scale));
    		getModel().setBaseRounding(StringUtil.toString(rounding));
    		
    		FrontEndContext.getInstance().removeBalanceInfo(sequenceId);
    	} else {
    		String customerId = getModel().getId();
        	String ticketId = getTicketId();
        	String sequenceId = ticketId + customerId;
    		
        	m_log.info("loadUserBalanceInfo for guest customer: " + customerId);
    		if(!StringUtil.isEmpty(customerId)){
    			BalanceInfo balanceScInfo = getBalanceInfo(sequenceId, IConstants.SERVICES_TYPE.COPY_TRADE);
        		if(balanceScInfo != null){
        			getModel().setBalanceScInfo(balanceScInfo);
        		}
        		FrontEndContext.getInstance().removeBalanceInfo(sequenceId);
    		}
    	}
    	
		m_log.info("[Finish function] loadUserBalanceInfo - BaseSocialAction "+System.currentTimeMillis());
    }
    protected void loadSCFE001UserInformationData() {
    	m_log.info("[Start function] loadSCFE001UserInformationData - BaseSocialAction "+System.currentTimeMillis());
    	try{
    		String id = getModel().getId();
    		CustomerInfo cusInfo = SocialMemcached.getInstance().getCustomerInfo(id);
    		if(cusInfo == null){
    			cusInfo = getProfileManager().getCustomerInfo(id);
//    			if(StringUtil.isEmpty(cusInfo.getDescription())){
//    				cusInfo.setDescription(DEFAULT_DESCIPTION);
//    			}
    			SocialMemcached.getInstance().saveCustomerInfo(cusInfo);
    		}
			getModel().setCurrentCusInfo(cusInfo);		
			getModel().setMode(getMode());
    	}catch(Exception ex){
    		LOG.error(ex.getMessage(), ex);
    	}
    	m_log.info("[Start function] loadSCFE001UserInformationData - BaseSocialAction "+System.currentTimeMillis());
    }

    protected void loadSCFE002CopyFollowPartData(){
    	m_log.info("[Start function] loadSCFE002CopyFollowPartData - BaseSocialAction "+System.currentTimeMillis());
        try{
            getModel().getCopyFollowInfo().setShowAccountBox(!httpRequest.getServletPath().equals(SocialBootstrap.SERVLET_PATH.SOCIAL_HOME) || getMode().equals(IConstants.SOCIAL_MODES.GUEST_MODE));
            if(getModel().getCopyDetails() == null){
                loadCommonData();
            }
            if(getModel().isShowCopyFollow()){
            	socialManager.loadCopyFollowInfo(getModel().getCopyFollowInfo(), getCurrentCustomerId(), getModel().getId(), getModel().getAccountId(), getModel().getBrokerCd(), httpRequest.getServletPath().equals(SocialBootstrap.SERVLET_PATH.SOCIAL_HOME) && getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE), getMode());
            }
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        }
        m_log.info("[Finish function] loadSCFE002CopyFollowPartData - BaseSocialAction "+System.currentTimeMillis());
    }

    public String formatDate(Timestamp timestamp,String pattern){
    	try{
    		SimpleDateFormat formatter = null ;
    		formatter = new SimpleDateFormat(pattern, new Locale(getModel().getUserLanguage()));
    		return formatter.format(timestamp);
	    }catch(Exception ex){
	    	LOG.error(ex.getMessage(), ex);
	    	return null;
	    }
    }
    
    public boolean isCustomerSocialUserNameEmpty(){
    	FrontUserDetails frontUserDetail = FrontUserOnlineContext.getFrontUserOnline();
    	if(frontUserDetail == null) return false;
    	FrontUserOnline frontUserOnline = frontUserDetail.getFrontUserOnline();
    	return frontUserOnline != null && StringUtil.isEmpty(frontUserOnline.getUserName());
    }
    
    private void loadPageTitle(){
    	String titleKey = SocialBootstrap.getInstance().getTitleKeyByServletPath(httpRequest.getServletPath());
    	if(StringUtil.isEmpty(titleKey)){
    		titleKey = SocialBootstrap.TITLE_KEY.PROFILE;
    	} 
    	if (titleKey.equals(SocialBootstrap.TITLE_KEY.NAME)){
    		getModel().setTitle(getModel().getCurrentCusInfo().getUsername() + getText(titleKey));
    	} else {
    		getModel().setTitle(getText(titleKey));
    	}
    }
    
    public Map<String,String> getTextMap(Map<String, String> map) {
    	Map<String, String> textMap = new LinkedHashMap<String, String>();
    	if(map!= null && map.size() > 0) {
    		for(String key : map.keySet()) {
    			textMap.put(key, getText(map.get(key)));
    		}
    	}
    	return textMap;
    }
    
    public Map<Integer,String> getTextMapIn(Map<Integer, String> map) {
    	Map<Integer, String> textMap = new LinkedHashMap<Integer, String>();
    	if(map!= null && map.size() > 0) {
    		for(Integer key : map.keySet()) {
    			textMap.put(key, getText(map.get(key)));
    		}
    	}
    	return textMap;
    }
    
    public ISocialManager getSocialManager() {
        return socialManager;
    }

    public void setSocialManager(ISocialManager socialManager) {
        this.socialManager = socialManager;
    }

	/**
	 * @return the balanceManager
	 */
	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	/**
	 * @return the profileManager
	 */
	public IProfileManager getProfileManager() {
		return profileManager;
	}

	/**
	 * @param profileManager the profileManager to set
	 */
	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
}
