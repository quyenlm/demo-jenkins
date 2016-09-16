package phn.nts.ams.fe.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/13/13 9:37 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class SocialBootstrap {

    private final static SocialBootstrap instance = new SocialBootstrap();
    private Map<String, String> servletLayout = new HashMap<String, String>();
    private Map<String, String> pageTitles = new HashMap<String, String>();
    private Map<String, List<String>> layoutComponents = new HashMap<String, List<String>>();
    private List<String> requireIdPages = new ArrayList<String>();

    public interface TITLE_KEY{
    	final String PROFILE = "nts.ams.fe.label.title.account";
    	final String RESET_PASSWORD = "nts.ams.fe.label.title.reset.password";
    	final String MARKET = "nts.ams.fe.label.title.market";
    	final String RANKING = "nts.ams.fe.label.title.ranking";
    	final String MYPAGE = "nts.ams.fe.label.title.mypage";
    	final String NAME = "nts.ams.fe.label.title.name";
    }
    public interface LAYOUT{
        final String LAYOUT_SIMPLE = "SIMPLE";
        final String LAYOUT_INNER = "INNER";
        final String LAYOUT_SOCIAL_DEFAULT = "SOCIAL_DEFAULT";
        final String LAYOUT_DEFAULT = "DEFAULT";
        final String LAYOUT_SOCIAL_PROFILE = "LAYOUT_SOCIAL_PROFILE";
    }

    public interface COMPONENT{
        final String SCFE002_COPY_FOLLOW_PART = "SCFE002_COPY_FOLLOW_PART";
        final String SCFE001_USER_INFORMATION = "SCFE001_USER_INFORMATION";
        final String SCFE009_LEADER_BOARD = "SCFE009_LEADER_BOARD";
        final String SCFE010_LIVE_RATE = "SCFE010_LIVE_RATE";
        final String SCFE012_PRIVATE_MESSAGE = "SCFE012_PRIVATE_MESSAGE";
        final String SCFE013_NOTICE = "SCFE013_NOTICE";
        final String SCFE014_BACKGROUND_SETTING = "SCFE014_BACKGROUND_SETTING";
        final String SCFE005_USER_PORTFOLIO = "SCFE005_USER_PORTFOLIO";
        final String SCFE007_FOLLOW_PART = "SCFE007_FOLLOW_PART";
        final String SCFE025_CUSTOMER_GUIDELINE = "SCFE025_CUSTOMER_GUIDELINE";
    }

    public interface SERVLET_PATH{
        final String SOCIAL_HOME                                  = "/social/home";
        final String SOCIAL_MARKET_WATCH                          = "/social/marketWatch";
        final String SOCIAL_RANKING                               = "/social/ranking";
        final String SOCIAL_CHAT_BOARD                            = "/social/chatBoard";
        final String SOCIAL_CHAT_TRADING_ACTIVITY                 = "/social/tradingActivity";
        final String SOCIAL_CHAT_PERFORMANCE_STATS                = "/social/performanceStats";
        final String SOCIAL_FOLLOW_LIST                           = "/social/followList";
        final String SOCIAL_COPY_LIST                             = "/social/copyList";
        final String SOCIAL_FOLLOWERS                             = "/social/followerList";
        final String SOCIAL_COPIERS                               = "/social/copierList";
        final String PROFILE_BASIC_INFORMATION 					  = "/profile/basicInformation";
        final String PROFILE_INDEX                                = "/profile/index";
        final String PROFILE_UPDATE_USER                          = "/profile/updateProfile";
        final String PROFILE_UPDATE_USER_CONFIRM                  = "/profile/updateProfileConfirm";
        final String PROFILE_BROKER_SETTING                       = "/profile/brokerSetting";
        final String PROFILE_CREATE_NEW_BROKER                    = "/profile/createNewBroker";
        final String PROFILE_PRIVACY_SETTING                      = "/profile/privacySetting";
        final String PROFILE_VERIFICATION                         = "/profile/verification";
        final String PROFILE_BANK_INFORMATION                     = "/profile/bankInformation";
        final String PROFILE_ADD_BANK_TRANSFER                    = "/profile/addBankTransfer";
        final String PROFILE_ADD_PAYZA			                  = "/profile/addPayza";
        final String PROFILE_ADD_NETELLER		                  = "/profile/addNeteller";
        final String PROFILE_ADD_CREDIT_CARD	                  = "/profile/addCreditCard";
        final String PROFILE_ADD_LIBERTY		                  = "/profile/addLiberty";
        final String PROFILE_UPDATE_PAYMENT		                  = "/profile/updatePayment";
        final String PROFILE_UPDATE_NETELLER_SUBMIT               = "/profile/updateNetellerSubmit";
        final String PROFILE_UPDATE_PAYZA_SUBMIT	              = "/profile/updatePayzaSubmit";
        final String PROFILE_UPDATE_CREDIT_SUBMIT	              = "/profile/updateCreditSubmit";
        final String PROFILE_UPDATE_BANK_INFO_SUBMIT              = "/profile/updateBankInfoSubmit";
        final String PROFILE_UPDATE_LIBERTY_SUBMIT                = "/profile/updateLibertySubmit";
        final String PROFILE_DELETE_PAYMENT			              = "/profile/deletePayment";
        final String PROFILE_CUSTOMER_REPORT_HISTORY			  = "/profile/customerReportHistory";
        final String PROFILE_SEARCH_CUSTOMER_REPORT_HISTORY		  = "/profile/searchCustReportHistory";
        final String DEPOSIT_INDEX                                = "/deposit/index";
        final String DEPOSIT_CONFIRMATION                         = "/deposit/depositConfirmed";
        final String WITHDRAWAL_INDEX                             = "/withdrawal/index";
        final String WITHDRAWAL_CONFIRMATION                      = "/withdrawal/withdrawalConfirmed";
        final String WITHDRAWAL_SUBMIT							  = "/withdrawal/withdrawalSubmit";
        final String WITHDRAWAL_METHOD_SHOW						  = "/withdrawal/withdrawalMethodShow";
        final String WITHDRAWAL_CANCEL							  = "/withdrawal/withdrawalCancel";
        final String EXCHANGER_INDEX 							  = "/exchanger/index";
        final String EXCHANGER_CONFIRMATION						  = "/exchanger/confirm-update";
        final String TRANSFER_INDEX                               = "/transfer/index";
        final String TRANSFER_CONFIRMATION                        = "/transfer/transferConfirmed";
        final String TRANSFER_FINISH		                      = "/transfer/index.action";
        final String HISTORY_INDEX                                = "/history/index";
        final String HISTORY_SEARCH                               = "/history/search";
        final String IB_MANAGEMENT_INDEX                          = "/ibManagement/index";
        final String IB_MANAGEMENT_CUSTOMER_INFORMATION           = "/ibManagement/customerInformation";
        final String IB_MANAGEMENT_REGISTER_CUSTOMER_INFORMATION  = "/ibManagement/registerCustomerInformation";
        final String IB_MANAGEMENT_CONFIRM_CUSTOMER_INFORMATION   = "/ibManagement/confirmCustomerInformation";
        final String IB_MANAGEMENT_DEPOSIT_WITHDRAWAL             = "/ibManagement/depositWithdrawal";
        final String IB_MANAGEMENT_KICKBACK_HISTORY               = "/ibManagement/kickbackHistory";
        final String DEMO_CONTEST_CONTEST_LIST                    = "/demoContest/contest-list";
        final String IB_MANAGEMENT_DEPOSIT_WITHDRAWAL_CONFIRM     = "/ibManagement/depositWithdrawalConfirm";
        final String ACCOUNT_REGISTRATION						  = "/account/accountRegistration";
        final String ACCOUNT_REGISTRATION_SUBMIT    			  = "/account/accountRegistrationSubmit";
        final String OPEN_ACCOUNT_SUCCESS					      = "/account/openAccountSuccess";
        final String INVITE_CUSTOMER_SEARCH					      = "/ibManagement/inviteCustomerSearch";
        final String KICKBACK_HISTORY_SEARCH					  = "/ibManagement/kickbackHistorySearch";
    }

    private SocialBootstrap(){
        //Layout Simple
        List<String> layoutSimple = new ArrayList<String>();
        layoutComponents.put(LAYOUT.LAYOUT_SIMPLE, layoutSimple);

        //Layout Inner
        List<String> layoutInner = new ArrayList<String>();
        layoutInner.add(COMPONENT.SCFE001_USER_INFORMATION);
        layoutInner.add(COMPONENT.SCFE002_COPY_FOLLOW_PART);
        layoutInner.add(COMPONENT.SCFE005_USER_PORTFOLIO);
        layoutInner.add(COMPONENT.SCFE007_FOLLOW_PART);
        layoutComponents.put(LAYOUT.LAYOUT_INNER, layoutInner);

        //Layout Social Default
        List<String> layoutSocialDefault = new ArrayList<String>();
        layoutComponents.put(LAYOUT.LAYOUT_SOCIAL_DEFAULT, layoutSocialDefault);


        //Layout default
        List<String> layoutDefault = new ArrayList<String>();
        layoutDefault.add(COMPONENT.SCFE002_COPY_FOLLOW_PART);
        layoutDefault.add(COMPONENT.SCFE001_USER_INFORMATION);
        layoutDefault.add(COMPONENT.SCFE005_USER_PORTFOLIO);
        layoutDefault.add(COMPONENT.SCFE007_FOLLOW_PART);
        layoutDefault.add(COMPONENT.SCFE009_LEADER_BOARD);
        layoutDefault.add(COMPONENT.SCFE010_LIVE_RATE);
        layoutDefault.add(COMPONENT.SCFE025_CUSTOMER_GUIDELINE);
        layoutComponents.put(LAYOUT.LAYOUT_DEFAULT, layoutDefault);

        //Layout social profile
        List<String> layoutSocialProfile = new ArrayList<String>();
        layoutSocialProfile.add(COMPONENT.SCFE001_USER_INFORMATION);
        layoutSocialProfile.add(COMPONENT.SCFE002_COPY_FOLLOW_PART);
        layoutSocialProfile.add(COMPONENT.SCFE005_USER_PORTFOLIO);
        layoutComponents.put(LAYOUT.LAYOUT_SOCIAL_PROFILE, layoutSocialProfile);

        //config servlet -> layout relationship
        servletLayout.put(SERVLET_PATH.SOCIAL_HOME, LAYOUT.LAYOUT_DEFAULT);
        servletLayout.put(SERVLET_PATH.SOCIAL_MARKET_WATCH, LAYOUT.LAYOUT_SOCIAL_DEFAULT);
        servletLayout.put(SERVLET_PATH.SOCIAL_RANKING, LAYOUT.LAYOUT_SIMPLE);
        servletLayout.put(SERVLET_PATH.SOCIAL_CHAT_BOARD, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_CHAT_TRADING_ACTIVITY, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_CHAT_PERFORMANCE_STATS, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_FOLLOW_LIST, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_COPY_LIST, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_COPIERS, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.SOCIAL_FOLLOWERS, LAYOUT.LAYOUT_INNER);
        servletLayout.put(SERVLET_PATH.PROFILE_BASIC_INFORMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_USER, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_USER_CONFIRM, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_BROKER_SETTING, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_CREATE_NEW_BROKER, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_PRIVACY_SETTING, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_VERIFICATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_BANK_INFORMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_ADD_BANK_TRANSFER, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_ADD_PAYZA, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_ADD_NETELLER, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_ADD_CREDIT_CARD, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_ADD_LIBERTY, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_PAYMENT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_NETELLER_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_PAYZA_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_CREDIT_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_BANK_INFO_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_UPDATE_LIBERTY_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_DELETE_PAYMENT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_CUSTOMER_REPORT_HISTORY, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.PROFILE_SEARCH_CUSTOMER_REPORT_HISTORY, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.DEPOSIT_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.DEPOSIT_CONFIRMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.WITHDRAWAL_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.WITHDRAWAL_CONFIRMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.WITHDRAWAL_CANCEL, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.WITHDRAWAL_METHOD_SHOW, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.WITHDRAWAL_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.EXCHANGER_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.EXCHANGER_CONFIRMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.TRANSFER_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.TRANSFER_CONFIRMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.TRANSFER_FINISH, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.HISTORY_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.HISTORY_SEARCH, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_INDEX, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_CUSTOMER_INFORMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_REGISTER_CUSTOMER_INFORMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_CONFIRM_CUSTOMER_INFORMATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_DEPOSIT_WITHDRAWAL, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_KICKBACK_HISTORY, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.DEMO_CONTEST_CONTEST_LIST, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.IB_MANAGEMENT_DEPOSIT_WITHDRAWAL_CONFIRM, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.ACCOUNT_REGISTRATION, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.ACCOUNT_REGISTRATION_SUBMIT, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.OPEN_ACCOUNT_SUCCESS, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.INVITE_CUSTOMER_SEARCH, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        servletLayout.put(SERVLET_PATH.KICKBACK_HISTORY_SEARCH, LAYOUT.LAYOUT_SOCIAL_PROFILE);
        //Register pages that requires parameter id
        requireIdPages.add(SERVLET_PATH.SOCIAL_CHAT_BOARD);
        requireIdPages.add(SERVLET_PATH.SOCIAL_CHAT_PERFORMANCE_STATS);
        requireIdPages.add(SERVLET_PATH.SOCIAL_CHAT_TRADING_ACTIVITY);
        requireIdPages.add(SERVLET_PATH.SOCIAL_COPIERS);
        requireIdPages.add(SERVLET_PATH.SOCIAL_COPY_LIST);
        requireIdPages.add(SERVLET_PATH.SOCIAL_FOLLOW_LIST);
        requireIdPages.add(SERVLET_PATH.SOCIAL_FOLLOWERS);
        //Register page title
        pageTitles.put(SERVLET_PATH.SOCIAL_HOME, TITLE_KEY.MYPAGE);
        pageTitles.put(SERVLET_PATH.SOCIAL_MARKET_WATCH, TITLE_KEY.MARKET);
        pageTitles.put(SERVLET_PATH.SOCIAL_RANKING, TITLE_KEY.RANKING);
        pageTitles.put(SERVLET_PATH.SOCIAL_CHAT_BOARD, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_CHAT_TRADING_ACTIVITY, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_CHAT_PERFORMANCE_STATS, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_FOLLOW_LIST, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_COPY_LIST, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_COPIERS, TITLE_KEY.NAME);
        pageTitles.put(SERVLET_PATH.SOCIAL_FOLLOWERS, TITLE_KEY.NAME);
    }

    public static SocialBootstrap getInstance(){
        return instance;
    }

    public String getLayoutByServletPath(String servletPath){
        return servletLayout.get(servletPath);
    }

    public boolean containsComponent(String layout, String component){
        List<String> components = layoutComponents.get(layout);
        return components != null && components.contains(component);
    }

    public boolean isRequireParamId(String servletPath){
        return requireIdPages.contains(servletPath);
    }

	public String getTitleKeyByServletPath(String servletPath){
		return pageTitles.get(servletPath);
	}
    
}
