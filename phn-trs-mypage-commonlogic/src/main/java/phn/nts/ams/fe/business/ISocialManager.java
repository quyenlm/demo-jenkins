package phn.nts.ams.fe.business;



import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;
import phn.com.nts.ams.web.condition.RankingSearchCondition;
import phn.com.nts.ams.web.condition.RankingTraderInfo;
import phn.com.nts.ams.web.condition.SummaryTradingInfo;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.ScOrderInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.domain.CopierCustomerModel;
import phn.com.nts.db.domain.FollowPartCustomer;
import phn.com.nts.db.domain.LeaderBoardCustomer;
import phn.com.nts.db.domain.TraderServiceInfo;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.db.entity.ScCustomerCopy;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.db.entity.ScOrder;
import phn.com.nts.db.entity.ScSummaryTradingCust;
import phn.com.trs.util.enums.ConfirmAgreementResult;
import phn.nts.ams.fe.domain.CopyFollowInfo;
import phn.nts.ams.fe.domain.CountryInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.FollowListModel;
import phn.nts.ams.fe.domain.InvalidActionException;
import phn.nts.ams.fe.domain.LiveRateInfo;
import phn.nts.ams.fe.domain.ScCustomerServiceInfo;
import phn.nts.ams.fe.domain.ScSummaryTradingCustInfo;
import phn.nts.ams.fe.model.ChangeFundsModel;
import phn.nts.ams.fe.model.CustomerGuidelineModel;
import phn.nts.ams.fe.model.TraderModel;

import com.nts.common.exchange.ams.bean.AmsCustomerNews;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;

/**
 * @author dev15
 *
 */
/**
 * @author dev15
 *
 */
/**
 * @author dev15
 *
 */
public interface ISocialManager {

	List<FollowPartCustomer> getListFollower(String customer_id);

	List<LeaderBoardCustomer> getLeaderBoard(String customerId);

	void loadChangeFundsInfo(ChangeFundsModel changeFundsModel);
	boolean checkRemainAmountAfterInvestment(String currentCustomerId, Integer copyId,
			BigDecimal investmentAmount);
	void updateInvestmentAmount(ChangeFundsModel changeFundsModel);
	void stopCopyProcessing(TraderModel model) throws InvalidActionException;
	//String isCopy(String customerId, String otherCustomerAccountId, String otherCustomerBrokerCd);
	public List<FxSymbol> getListSymbolCurrency(String customerId);
	/*
	 * This is used to get signal information for customer
	 */
	public ScCustomer getSignalProviderInformation(String userName);
	/*
	 * This is used to get copy trade information for customer
	 * 
	 */
	public ScSummaryTradingCust getCopyTradeInformation(String customerId);
    void loadCopyFollowInfo(CopyFollowInfo copyFollowInfo, String currentCustomerId, String guestCustomerId, String accountId, String brokerCd, boolean getAllCopierNo, Integer mode);
    CustomerInfo getCustomerFromId(String customerId, String frontUserId);
    //SearchResult <CustomerInfo> getCopierList(String customer_id,PagingInfo paging);    

    Integer stopCopy(String currentCustomerId, String stopCustomerId, String accountId, String brokerCd) throws InvalidActionException;

    Integer stopFollow(String currentCustomerId, String stopCustomerId) throws InvalidActionException;

    Integer followCustomer(String currentCustomerId, String followCustomerId) throws InvalidActionException;

    Integer copyTradeCustomer(String currentCustomerId, String copyCustomerId, String accountId, String brokerCd, CopyFollowInfo copyInfo, String wlCode) throws InvalidActionException;

	boolean checkCustomerExistence(String customerId);

	void loadCopyListInfo(TraderModel model);

	void loadLiveRateSymbolsInfo(LiveRateInfo liveRateInfo);
	List<RankingTraderInfo> getRankingData(RankingSearchCondition condition, PagingInfo pagingInfo, String wlCode);

	List<TraderServiceInfo> getListAccountOfTrader(String customerId);

	CopierCustomerModel getCopierAccountInfo(ScCustomerCopy copier);

    void loadFollowListInfo(FollowListModel followListModel, String currentCustomerId, String customerId, PagingInfo pagingInfo);
	
	
//	SearchResult<CustomerInfo> getFollowerList1(String customerId,PagingInfo pagingInfo);
    /**
     * This is used to get "Copy Trade " Account Information for Performance and statistic Block
     * @param customerId
     * @return
     */
    ScCustomerService getCopyTradeAccountInformation(String customerId);
    
    /**
     * This is used to get "Signal Provider " Account Information for Performance and statistic Block
     * @param customerId
     * @return
     */
    ScCustomerService getSignalProviderAccInformation(String customerId);
    

    /**
     * This is used to get list Symbol of account by Account_Id
     * @param customerId
     * @return
     */
    public List<FxSymbol> getListSymbolByAccountId(String accountId);
    
    /**
     * This is used to get list order by trading account Id
     * @param accountId
     * @return
     */
    public List<ScOrder> findOrderByTradingAccount(String accountId);

	SearchResult<CustomerInfo> getFollowerList(String customerId , PagingInfo pagingInfo);
    
    /**
     * This is used to get Trading Summary Info
     * @param accountId
     * @param customerId
     * @param brokerCd
     * @return
     */
    public List<ScSummaryTradingCustInfo> getAccountSummaryTrading(String accountId, String customerId, String brokerCd,String fromDate);
    /**
     * This is used to get customer Service Info
     * @param accountId
     * @param customerId
     * @param brokerCd
     * @return
     */
    public ScCustomerService getCustomerServiceInfo(String accountId,String customerId, String brokerCd);

	SearchResult<ScCustomerCopy> getListOfCopierByAccount(String accountId , String brokerCd , PagingInfo pageInfo);

    BigDecimal computeEquityPercentage(String currentCustomerId, String copyCustomerId, BigDecimal investAmount, String copyAccountId, String copyBrokerCd);

    BigDecimal computeInvestAmount(String currentCustomerId, String copyCustomerId, BigDecimal equityPercentage, String copyAccountId, String copyBrokerCd);

    List<CopyTradeItemInfo> getCopyItemDetails(String currentCustomerId, String customerId);


    Map<String,CountryInfo> getListCountryInfo();

	BigDecimal getMinInvestmentAmount(String baseCurrency);
	public Integer isEnabledFeedBoard(String accountId, String brokerCd);

	ScCustomer getScCustomerFromId(String customerId);

    ScCustomerServiceInfo getCustomerService(String customerId, Integer serviceType);
    String calculateToTalTraded(String customerId);

	void updateCopyRate(ChangeFundsModel changeFundsModel);
	
	List<ScOrderInfo> getListOrderByAccountId(String accountId,int page, int index);
	
	/**
	 * This is used to find All list active symbol
	 * @return
	 */
	HashMap<String,FxSymbol> findAllFxSymbol();

    void loadCustomerGuidelineData(String customerId, CustomerGuidelineModel guidelineModel);
    /**
     * This is used to find user by user Name
     * @param userName
     * @return
     */
    ScCustomer findUserByUserName(String userName); 
    /**
     * This is used to find user by customerId
     * @param sc
     * @return
     */
    ScCustomer findUserByCustomerId(String customerId); 
    /**
     * This is used to save ScCustomer
     * @param sc
     * @return
     */
    boolean save(ScCustomer sc);
    /**
     * This is used to update first_login_flag in ams_customer_survey
     * @param sc
     * @return
     */
    boolean updateFirsLoginFlag(String customerId);

    public List<AmsMessage> getAgreementInfo(String customerId, Integer messageType, Integer offSet, Integer pageSize, 
			Integer readFlg, Integer serviceType) throws Exception;
    
    public List<AmsMessage> getAgreementInfo(AmsCustomerNewsResponse.Builder amsCustomerNewsResponse, String customerId, Integer messageType, Integer offSet, Integer pageSize, 
			Integer readFlg, Integer serviceType, Integer confirmFlg, Integer messageKind) throws Exception;
    
    public AmsCustomerNews getAgreementInfo(String customerId) throws Exception;
    
    public int getAgreementInfoCount(String customerId, Integer messageType, Integer readFlg, Integer serviceType)  throws Exception;

    public void agreeConfirm(String customerId, AmsNewsInfo amsCustomerNewsUpdateRequest) throws Exception;

	public int disagreeConfirm(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest);
	
	public Integer countFollower(String customerId);
	
	public Integer countCopier(String accountId, String brokerCd);
	
	public List<AmsMessage> getAllAgreementInfo(String customerId, Integer fxSubgroupId, Integer boSubgroupId, Integer socialSubgroupId);
	
	public List<String> getListGroupDemo(String wlCode);
	String validateOrder(Double amount,String orderTicket,String customerServiceId);
	
	/**
	 * Gets the list of sub group cds of ScCustomerService by accountId.
	 *
	 * @param accountId the account id
	 * @param brokerCd the broker cd
	 * @return the sub group cd
	 */
	public List<String> getSubGroupCdsByAccountId(String accountId, String brokerCd);
	
	/**
	 * Gets the map of sub group cds by customer id.
	 *
	 * @param customerId the customer id
	 * @return the sub group cds by customer id
	 */
	public Map<String, String> getMapSubGroupCdsByCustomerId(String customerId);
	
	public AmsCustomer findCustomerById(String customerId);
	
	public SummaryTradingInfo getTradingInfoByAccount(String accountId, String brokerCd);
	
	public String getFrontDate();
	
	public boolean isExistedAccountId(String accountId);
	
	public boolean isExistedCustomerId(String id);
	
	public List<FxSymbol> getTradingStatisticInfo(String accountId);
	
	public ConfirmAgreementResult agreementConfirm(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest);
	
	public void agreementRead(String customerId, AmsNewsInfo amsCustomerNewsUpdateRequest) throws Exception;
	public void saveAmsMsgReadTraceList(String customerId, Integer messageType) throws Exception;
	
	
	
}