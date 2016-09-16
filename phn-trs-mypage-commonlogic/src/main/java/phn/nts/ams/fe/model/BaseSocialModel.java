package phn.nts.ams.fe.model;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;
import phn.com.nts.db.domain.FollowPartCustomer;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CopyFollowInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.LeaderBoardInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/12/13 9:02 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class BaseSocialModel extends BaseModel {
    //The identifier of requested SC_CUSTOMER
    private String id;
    //The identifier of requested MT4 account
    private String accountId;
    //The requested broker code
    private String brokerCd;
    //The identifier of logged in SC_CUSTOMER
    private String currentCustomerId;
    private String requestUsername;    
    //Common data
    private List<CopyTradeItemInfo> copyDetails;
    private CopyTradeItemInfo selectedAccount;
    private boolean guestMode;
    private String brokerRegisterLink;
    private CustomerGuidelineModel customerGuidelineModel;
    //The data for SCF002_CopyFollowPart
    protected CopyFollowInfo copyFollowInfo = new CopyFollowInfo();
	private CustomerInfo currentCusInfo = new CustomerInfo();
	private BalanceInfo balanceAmsInfo = null;
	private BalanceInfo balanceBoInfo = null;
	private BalanceInfo balanceFxInfo = null;
	private BalanceInfo balanceScInfo = null;
	private Double total;
	private String currencyCode;
	private String baseScale;
	private String baseRounding;
	private String userLanguage;
	//The data for SCFE007_FollowPart
	private List<FollowPartCustomer> listFollower;
	//The data for SCFE009_LeaderBoard
	private LeaderBoardInfo leaderBoardInfo;
	private boolean showCopyFollow;
	private String title;

    public String getAvatarTimestamp(String customerId){
        return FrontEndContext.getInstance().getAvatarTimestamp(customerId);
    }

    public String getAvatarDimension(String customerId){
        return FrontEndContext.getInstance().getAvatarDimension(customerId);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBrokerCd() {
        return brokerCd;
    }

    public void setBrokerCd(String brokerCd) {
        this.brokerCd = brokerCd;
    }

    public CopyFollowInfo getCopyFollowInfo() {
        return copyFollowInfo;
    }

    public void setCopyFollowInfo(CopyFollowInfo copyFollowInfo) {
        this.copyFollowInfo = copyFollowInfo;
    }

    public String getCurrentCustomerId() {
        return currentCustomerId;
    }

    public void setCurrentCustomerId(String currentCustomerId) {
        this.currentCustomerId = currentCustomerId;
    }

    public List<CopyTradeItemInfo> getCopyDetails() {
        return copyDetails;
    }

    public void setCopyDetails(List<CopyTradeItemInfo> copyDetails) {
        this.copyDetails = copyDetails;
    }

    public CopyTradeItemInfo getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(CopyTradeItemInfo selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public boolean isGuestMode() {
        return guestMode;
    }

    public void setGuestMode(boolean guestMode) {
        this.guestMode = guestMode;
    }
    
	/**
	 * @return the balanceAmsInfo
	 */
	public BalanceInfo getBalanceAmsInfo() {
		return balanceAmsInfo;
	}

	/**
	 * @param balanceAmsInfo the balanceAmsInfo to set
	 */
	public void setBalanceAmsInfo(BalanceInfo balanceAmsInfo) {
		this.balanceAmsInfo = balanceAmsInfo;
	}

	/**
	 * @return the balanceBoInfo
	 */
	public BalanceInfo getBalanceBoInfo() {
		return balanceBoInfo;
	}

	/**
	 * @param balanceBoInfo the balanceBoInfo to set
	 */
	public void setBalanceBoInfo(BalanceInfo balanceBoInfo) {
		this.balanceBoInfo = balanceBoInfo;
	}

	/**
	 * @return the balanceFxInfo
	 */
	public BalanceInfo getBalanceFxInfo() {
		return balanceFxInfo;
	}

	/**
	 * @param balanceFxInfo the balanceFxInfo to set
	 */
	public void setBalanceFxInfo(BalanceInfo balanceFxInfo) {
		this.balanceFxInfo = balanceFxInfo;
	}

	/**
	 * @return the balanceScInfo
	 */
	public BalanceInfo getBalanceScInfo() {
		return balanceScInfo;
	}

	/**
	 * @param balanceScInfo the balanceScInfo to set
	 */
	public void setBalanceScInfo(BalanceInfo balanceScInfo) {
		this.balanceScInfo = balanceScInfo;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
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

	/**
	 * @return the userLanguage
	 */
	public String getUserLanguage() {
		return userLanguage;
	}

	/**
	 * @param userLanguage the userLanguage to set
	 */
	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}



	public LeaderBoardInfo getLeaderBoardInfo() {
		return leaderBoardInfo;
	}

	public void setLeaderBoardInfo(LeaderBoardInfo leaderBoardInfo) {
		this.leaderBoardInfo = leaderBoardInfo;
	}

    public String getBrokerRegisterLink() {
        return brokerRegisterLink;
    }

    public void setBrokerRegisterLink(String brokerRegisterLink) {
        this.brokerRegisterLink = brokerRegisterLink;
    }

	/**
	 * @return the currentCusInfo
	 */
	public CustomerInfo getCurrentCusInfo() {
		return currentCusInfo;
	}

	/**
	 * @param currentCusInfo the currentCusInfo to set
	 */
	public void setCurrentCusInfo(CustomerInfo currentCusInfo) {
		this.currentCusInfo = currentCusInfo;
	}

	/**
	 * @return the requestUsername
	 */
	public String getRequestUsername() {
		return requestUsername;
	}

	/**
	 * @param requestUsername the requestUsername to set
	 */
	public void setRequestUsername(String requestUsername) {
		this.requestUsername = requestUsername;
	}

	public List<FollowPartCustomer> getListFollower() {
		return listFollower;
	}

	public void setListFollower(List<FollowPartCustomer> listFollower) {
		this.listFollower = listFollower;
	}

	/**
	 * @return the baseScale
	 */
	public String getBaseScale() {
		return baseScale;
	}

	/**
	 * @param baseScale the baseScale to set
	 */
	public void setBaseScale(String baseScale) {
		this.baseScale = baseScale;
	}

	/**
	 * @return the baseRounding
	 */
	public String getBaseRounding() {
		return baseRounding;
	}

	/**
	 * @param baseRounding the baseRounding to set
	 */
	public void setBaseRounding(String baseRounding) {
		this.baseRounding = baseRounding;
	}

    public CustomerGuidelineModel getCustomerGuidelineModel() {
        return customerGuidelineModel;
    }

    public void setCustomerGuidelineModel(CustomerGuidelineModel customerGuidelineModel) {
        this.customerGuidelineModel = customerGuidelineModel;
    }
    public BigDecimal roundDownBigDecimal(BigDecimal value, int scale){
    	if(value == null ) value = BigDecimal.ZERO;
    	return value.divide(BigDecimal.ONE, scale, RoundingMode.DOWN);
    }

	public boolean isShowCopyFollow() {
		return showCopyFollow;
	}

	public void setShowCopyFollow(boolean showCopyFollow) {
		this.showCopyFollow = showCopyFollow;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    
}
