package phn.nts.ams.fe.model;

import phn.com.nts.db.common.ScOrderInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.domain.CopierCustomerModel;
import phn.com.nts.db.domain.LeaderBoardCustomer;
import phn.com.nts.db.entity.*;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LiveRateInfo;
import phn.nts.ams.fe.domain.ScSummaryTradingCustInfo;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;


public class SocialModel extends BaseSocialModel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<LeaderBoardCustomer> listLeaderBoard;
	private List<Integer> listCheckCopy;
	private List<FxSymbol> listSymbolSettings;
	private FrontUserOnline userOnline;
	private String q;
	private boolean isOwner;
	private ScCustomer signalProviderInformation;
	private ScSummaryTradingCust copyTradeInformation;
	private LiveRateInfo liveRateInfo = new LiveRateInfo();
//	private int mode ;
	private String id;
	private String brandMode;
	private ScCustomerService accountInformation;
	private String accountId;
	private List<ScSummaryTradingCustInfo> summaryTrading;
	private String brokerCd;
	private String customerId;	
	private int typeView;
	private String selectedCustomerId;
	private String followFlg;
	//private String fromDate;
    private Integer dayDiff;
	private SearchResult<CustomerInfo> followerList;
	private List<CopierCustomerModel> listAccountInfo;
	private String accountKind;
	private String serviceType;
	private CustomerFollowerModel customerFollowerModel;
	private CustomerCopierModel customerCopierModel;
	private int enableFeedBoard;
	private String totalTraded;
	private String serverTime;
	private String defaultLanguage;
	private List<ScOrderInfo> listOrder;
	private int pageOrder;
	private int indexPaging;
	private String userName;
	//add 17/03/2013
	private String usernameFirstLogin;
	private InputStream inputStream;
	
	//[NTS1.0-le.hong.ha]Jun 11, 2013A - Start 
	//For change agreement
	private Integer changeAgreement;
	private List<AmsMessage> amsMessages;
	private String messageIds;
	//[NTS1.0-le.hong.ha]Jun 11, 2013A - End

	private String marketWatchSymbols;
    private String marketWatchOriginalSymbols;
    private String marketWatchOriginalSymbolDigits;
    private String marketWatchOriginalSymbolVolumeBuy;
    private String marketWatchOriginalSymbolVolumeSell;
    private String sortSymbolBy;
    private BigDecimal totalReturn;
    private String frontDate;
    private Integer isSignalProvider;
    private Integer messageType;
    private String spreadBid;
	private String spreadAsk;
	
	public List<AmsMessage> getAmsMessages() {
		return amsMessages;
	}

	public String getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(String messageIds) {
		this.messageIds = messageIds;
	}

	public void setAmsMessages(List<AmsMessage> amsMessages) {
		this.amsMessages = amsMessages;
	}

	public Integer getChangeAgreement() {
		return changeAgreement;
	}

	public void setChangeAgreement(Integer changeAgreement) {
		this.changeAgreement = changeAgreement;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPageOrder() {
		return pageOrder;
	}

	public void setPageOrder(int pageOrder) {
		this.pageOrder = pageOrder;
	}

	public int getIndexPaging() {
		return indexPaging;
	}

	public void setIndexPaging(int indexPaging) {
		this.indexPaging = indexPaging;
	}

	
	public List<ScOrderInfo> getListOrder() {
		return listOrder;
	}

	public void setListOrder(List<ScOrderInfo> listOrder) {
		this.listOrder = listOrder;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	public String getTotalTraded() {
		return totalTraded;
	}

	public void setTotalTraded(String totalTraded) {
		this.totalTraded = totalTraded;
	}

	public int getEnableFeedBoard() {
		return enableFeedBoard;
	}

	public void setEnableFeedBoard(int enableFeedBoard) {
		this.enableFeedBoard = enableFeedBoard;
	}

	public int getTypeView() {
		return typeView;
	}

	public void setTypeView(int typeView) {
		this.typeView = typeView;
	}

	/*public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}*/

	public String getBrokerCd() {
		return brokerCd;
	}

	public void setBrokerCd(String brokerCd) {
		this.brokerCd = brokerCd;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	
	public List<ScSummaryTradingCustInfo> getSummaryTrading() {
		return summaryTrading;
	}

	public void setSummaryTrading(List<ScSummaryTradingCustInfo> summaryTrading) {
		this.summaryTrading = summaryTrading;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public ScCustomerService getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(ScCustomerService accountInformation) {
		this.accountInformation = accountInformation;
	}

	public String getBrandMode() {
		return brandMode;
	}

	public void setBrandMode(String brandMode) {
		this.brandMode = brandMode;
	}

	public LiveRateInfo getLiveRateInfo() {
		return liveRateInfo;
	}

	public void setLiveRateInfo(LiveRateInfo liveRateInfo) {
		this.liveRateInfo = liveRateInfo;
	}

	public ScSummaryTradingCust getCopyTradeInformation() {
		return copyTradeInformation;
	}

	public void setCopyTradeInformation(ScSummaryTradingCust copyTradeInformation) {
		this.copyTradeInformation = copyTradeInformation;
	}

	public ScCustomer getSignalProviderInformation() {
		return signalProviderInformation;
	}

	public void setSignalProviderInformation(ScCustomer signalProviderInformation) {
		this.signalProviderInformation = signalProviderInformation;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public boolean isOwner() {
		isOwner = q == null ? true: false;
		return isOwner;
	}


	public void setOwner(boolean isOwn) {
		this.isOwner = isOwn;
	}


	public FrontUserOnline getUserOnline() {
		return userOnline;
	}


	public void setUserOnline(FrontUserOnline userOnline) {
		this.userOnline = userOnline;
	}
	
	public List<FxSymbol> getListSymbolSettings() {
		return listSymbolSettings;
	}


	public void setListSymbolSettings(List<FxSymbol> listSymbolSettings) {
		this.listSymbolSettings = listSymbolSettings;
	}


	public List<LeaderBoardCustomer> getListLeaderBoard() {
		return listLeaderBoard;
	}

	public void setListLeaderBoard(List<LeaderBoardCustomer> listLeaderBoard) {
		this.listLeaderBoard = listLeaderBoard;
	}

	public List<Integer> getListCheckCopy() {
		return listCheckCopy;
	}

	public void setListCheckCopy(List<Integer> listCheckCopy) {
		this.listCheckCopy = listCheckCopy;
	}

	

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	
	public String getSelectedCustomerId() {
		return selectedCustomerId;
	}

	public void setSelectedCustomerId(String selectedCustomerId) {
		this.selectedCustomerId = selectedCustomerId;
	}

	public String getFollowFlg() {
		return followFlg;
	}

	public void setFollowFlg(String followFlg) {
		this.followFlg = followFlg;
	}


	public SearchResult<CustomerInfo> getFollowerList() {
		return followerList;
	}

	public void setFollowerList(SearchResult<CustomerInfo> followerList) {
		this.followerList = followerList;
	}


	public List<CopierCustomerModel> getListAccountInfo() {
		return listAccountInfo;
	}

	public void setListAccountInfo(List<CopierCustomerModel> listAccountInfo) {
		this.listAccountInfo = listAccountInfo;
	}

	public String getAccountKind() {
		return accountKind;
	}

	public void setAccountKind(String accountKind) {
		this.accountKind = accountKind;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public CustomerFollowerModel getCustomerFollowerModel() {
		return customerFollowerModel;
	}

	public void setCustomerFollowerModel(CustomerFollowerModel customerFollowerModel) {
		this.customerFollowerModel = customerFollowerModel;
	}

	public CustomerCopierModel getCustomerCopierModel() {
		return customerCopierModel;
	}

	public void setCustomerCopierModel(CustomerCopierModel customerCopierModel) {
		this.customerCopierModel = customerCopierModel;
	}

	public String getUsernameFirstLogin() {
		return usernameFirstLogin;
	}

	public void setUsernameFirstLogin(String usernameFirstLogin) {
		this.usernameFirstLogin = usernameFirstLogin;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	


    public String getMarketWatchSymbols() {
        return marketWatchSymbols;
    }

    public void setMarketWatchSymbols(String marketWatchSymbols) {
        this.marketWatchSymbols = marketWatchSymbols;
    }

    public String getMarketWatchOriginalSymbols() {
        return marketWatchOriginalSymbols;
    }

    public void setMarketWatchOriginalSymbols(String marketWatchOriginalSymbols) {
        this.marketWatchOriginalSymbols = marketWatchOriginalSymbols;
    }

    public String getMarketWatchOriginalSymbolDigits() {
        return marketWatchOriginalSymbolDigits;
    }

    public void setMarketWatchOriginalSymbolDigits(String marketWatchOriginalSymbolDigits) {
        this.marketWatchOriginalSymbolDigits = marketWatchOriginalSymbolDigits;
    }

    public String getMarketWatchOriginalSymbolVolumeBuy() {
        return marketWatchOriginalSymbolVolumeBuy;
    }

    public void setMarketWatchOriginalSymbolVolumeBuy(String marketWatchOriginalSymbolVolumeBuy) {
        this.marketWatchOriginalSymbolVolumeBuy = marketWatchOriginalSymbolVolumeBuy;
    }

    public String getMarketWatchOriginalSymbolVolumeSell() {
        return marketWatchOriginalSymbolVolumeSell;
    }

    public void setMarketWatchOriginalSymbolVolumeSell(String marketWatchOriginalSymbolVolumeSell) {
        this.marketWatchOriginalSymbolVolumeSell = marketWatchOriginalSymbolVolumeSell;
    }

    public String getSortSymbolBy() {
        return sortSymbolBy;
    }

    public void setSortSymbolBy(String sortSymbolBy) {
        this.sortSymbolBy = sortSymbolBy;
    }

	public BigDecimal getTotalReturn() {
		return totalReturn;
	}

	public void setTotalReturn(BigDecimal totalReturn) {
		this.totalReturn = totalReturn;
	}

	public String getFrontDate() {
		return frontDate;
	}

	public void setFrontDate(String frontDate) {
		this.frontDate = frontDate;
	}

	public Integer getIsSignalProvider() {
		return isSignalProvider;
	}

	public void setIsSignalProvider(Integer isSignalProvider) {
		this.isSignalProvider = isSignalProvider;
	}

    public Integer getDayDiff() {
        return dayDiff;
    }

    public void setDayDiff(Integer dayDiff) {
        this.dayDiff = dayDiff;
    }

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public String getSpreadBid() {
		return spreadBid;
	}

	public void setSpreadBid(String spreadBid) {
		this.spreadBid = spreadBid;
	}

	public String getSpreadAsk() {
		return spreadAsk;
	}

	public void setSpreadAsk(String spreadAsk) {
		this.spreadAsk = spreadAsk;
	}

}

