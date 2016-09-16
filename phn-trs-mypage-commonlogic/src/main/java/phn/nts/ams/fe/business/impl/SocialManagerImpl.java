package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.BeanUtils;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;
import phn.com.nts.ams.web.condition.RankingSearchCondition;
import phn.com.nts.ams.web.condition.RankingTraderInfo;
import phn.com.nts.ams.web.condition.SummaryTradingInfo;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.ScOrderInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsCustomerTraceDAO;
import phn.com.nts.db.dao.IAmsMessageDAO;
import phn.com.nts.db.dao.IAmsMessageReadTraceDAO;
import phn.com.nts.db.dao.IAmsSysCountryDAO;
import phn.com.nts.db.dao.IAmsWhitelabelCalendarDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IFxSymbolDAO;
import phn.com.nts.db.dao.IScCustomerCopyDAO;
import phn.com.nts.db.dao.IScCustomerDAO;
import phn.com.nts.db.dao.IScCustomerFollowDAO;
import phn.com.nts.db.dao.IScCustomerServiceDAO;
import phn.com.nts.db.dao.IScFeedDAO;
import phn.com.nts.db.dao.IScInvestmentCashflowDAO;
import phn.com.nts.db.dao.IScInvestmentDAO;
import phn.com.nts.db.dao.IScOrderDAO;
import phn.com.nts.db.dao.IScSummaryTradingCustDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysPropertyDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.dao.impl.ScOrderDAO;
import phn.com.nts.db.domain.AccountSummaryTradingInfo;
import phn.com.nts.db.domain.ChartSymbol;
import phn.com.nts.db.domain.CopiedScCustomer;
import phn.com.nts.db.domain.CopierCustomerInfo;
import phn.com.nts.db.domain.CopierCustomerModel;
import phn.com.nts.db.domain.FollowPartCustomer;
import phn.com.nts.db.domain.LeaderBoardCustomer;
import phn.com.nts.db.domain.OpenCopyOrder;
import phn.com.nts.db.domain.SymbolBidAskValue;
import phn.com.nts.db.domain.TraderServiceInfo;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsCustomerTrace;
import phn.com.nts.db.entity.AmsGroup;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.AmsMessageReadTrace;
import phn.com.nts.db.entity.AmsMessageReadTraceId;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsWhitelabelCalendar;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.db.entity.ScCustomerCopy;
import phn.com.nts.db.entity.ScCustomerFollow;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.db.entity.ScFeed;
import phn.com.nts.db.entity.ScInvestment;
import phn.com.nts.db.entity.ScInvestmentCashflow;
import phn.com.nts.db.entity.ScOrder;
import phn.com.nts.db.entity.ScSummaryTradingCust;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.db.entity.SysProperty;
import phn.com.nts.db.entity.SysPropertyId;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.ConfirmAgreementResult;
import phn.nts.ams.fe.business.ISocialManager;
import phn.nts.ams.fe.common.AbstractManager;
import phn.nts.ams.fe.common.CustomerRankingCache;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CopyFollowInfo;
import phn.nts.ams.fe.domain.CopyListModel;
import phn.nts.ams.fe.domain.CountryInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.FollowListModel;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.InvalidActionException;
import phn.nts.ams.fe.domain.LiveRateInfo;
import phn.nts.ams.fe.domain.ScCustomerServiceInfo;
import phn.nts.ams.fe.domain.ScSummaryTradingCustIdInfo;
import phn.nts.ams.fe.domain.ScSummaryTradingCustInfo;
import phn.nts.ams.fe.domain.SymbolBidAskInfo;
import phn.nts.ams.fe.domain.converter.CustomerInfoConverter;
import phn.nts.ams.fe.jms.IJmsSender;
import phn.nts.ams.fe.model.ChangeFundsModel;
import phn.nts.ams.fe.model.CustomerGuidelineModel;
import phn.nts.ams.fe.model.TraderModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.nts.common.exchange.ams.bean.AmsCustomerNews;
import com.nts.common.exchange.bean.RateBandInfo;
import com.nts.common.exchange.dealing.FxFrontRateInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ConfirmFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReadFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.social.ScCustomerInfo;
import com.nts.common.exchange.social.api.ScSignalRelationInfo;


public class SocialManagerImpl extends AbstractManager implements ISocialManager {

    private static final Logit LOG = Logit.getInstance(SocialManagerImpl.class);
    //[NTS1.0-SonPH]Mar 01, 2013A - Start
    private List<CustomerInfo> listFollower;
    private IScInvestmentDAO<ScInvestment> scInvestmentDAO;
    private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhiteLabelConfigDAO;
    private IScInvestmentCashflowDAO<ScInvestmentCashflow> scInvestmentCashFlowDAO;
    private IScCustomerDAO<ScCustomer> scCustomerDAO;
    private IScOrderDAO<ScOrderDAO> scOrderDAO;
    private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
    private ISysAppDateDAO<SysAppDate> sysAppDateDAO;
    private IFxSymbolDAO<FxSymbol> fxSymbolDAO;
    private IScSummaryTradingCustDAO<ScSummaryTradingCust> scSummaryTradingCustDAO;
    private IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO;
    private IScCustomerCopyDAO<ScCustomerCopy> scCustomerCopyDAO;
    private IScCustomerFollowDAO<ScCustomerFollow> scCustomerFollowDAO;
    private IAmsCustomerDAO<AmsCustomer> amsCustomerDAO;
    private ISysUniqueidCounterDAO<SysUniqueidCounter> sysUniqueIdCounterDAO;
    private ISysPropertyDAO<SysProperty> sysPropertyDAO;
    private IScFeedDAO<ScFeed> scFeedDao;
    private IAmsSysCountryDAO<AmsSysCountry> amsSysCountryDAO;
    private IAmsWhitelabelCalendarDAO<AmsWhitelabelCalendar> amsWhitelabelCalendarDAO;
    private IAmsCustomerServiceDAO<AmsCustomerService> amsCustomerServiceDAO;
    private IAmsMessageDAO<AmsMessage> amsMessageDAO;
    private IAmsMessageReadTraceDAO<AmsMessageReadTrace> amsMessageReadTraceDAO;
    private IAmsCustomerTraceDAO<AmsCustomerTrace> amsCustomerTraceDAO;
    private IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO;
    private IScOrderDAO<ScOrderDAO> historyScOrderDAO;
    private IScSummaryTradingCustDAO<ScSummaryTradingCust> historyScSummaryTradingCustDAO;
    
//    private static Map<Integer, Entry<String,SearchResult<RankingTraderInfo>>> cachedRankingData = new HashMap<Integer, Map.Entry<String,SearchResult<RankingTraderInfo>>>();
    private IJmsSender jmsRealSender;
    private IJmsSender jmsDemoSender;
    	public IJmsSender getJmsRealSender() {
		return jmsRealSender;
	}

	public void setJmsRealSender(IJmsSender jmsRealSender) {
		this.jmsRealSender = jmsRealSender;
	}

	public IJmsSender getJmsDemoSender() {
		return jmsDemoSender;
	}

	public void setJmsDemoSender(IJmsSender jmsDemoSender) {
		this.jmsDemoSender = jmsDemoSender;
	}
	
    public IScOrderDAO<ScOrderDAO> getHistoryScOrderDAO() {
		return historyScOrderDAO;
	}

	public void setHistoryScOrderDAO(IScOrderDAO<ScOrderDAO> historyScOrderDAO) {
		this.historyScOrderDAO = historyScOrderDAO;
	}

	public IScSummaryTradingCustDAO<ScSummaryTradingCust> getHistoryScSummaryTradingCustDAO() {
		return historyScSummaryTradingCustDAO;
	}

	public void setHistoryScSummaryTradingCustDAO(
			IScSummaryTradingCustDAO<ScSummaryTradingCust> historyScSummaryTradingCustDAO) {
		this.historyScSummaryTradingCustDAO = historyScSummaryTradingCustDAO;
	}

	public IAmsCashBalanceDAO<AmsCashBalance> getAmsCashBalanceDAO() {
		return amsCashBalanceDAO;
	}

	public IAmsCustomerTraceDAO<AmsCustomerTrace> getAmsCustomerTraceDAO() {
		return amsCustomerTraceDAO;
	}

	public void setAmsCustomerTraceDAO(IAmsCustomerTraceDAO<AmsCustomerTrace> amsCustomerTraceDAO) {
		this.amsCustomerTraceDAO = amsCustomerTraceDAO;
	}
	private static Map<Integer, Entry<String,SearchResult<RankingTraderInfo>>> cachedRankingData = new HashMap<Integer, Map.Entry<String,SearchResult<RankingTraderInfo>>>();

	public IAmsMessageReadTraceDAO<AmsMessageReadTrace> getAmsMessageReadTraceDAO() {
		return amsMessageReadTraceDAO;
	}

	public void setAmsMessageReadTraceDAO(IAmsMessageReadTraceDAO<AmsMessageReadTrace> amsMessageReadTraceDAO) {
		this.amsMessageReadTraceDAO = amsMessageReadTraceDAO;
	}

	public IAmsMessageDAO<AmsMessage> getAmsMessageDAO() {
		return amsMessageDAO;
	}

	public void setAmsMessageDAO(IAmsMessageDAO<AmsMessage> amsMessageDAO) {
		this.amsMessageDAO = amsMessageDAO;
	}

	public IAmsCustomerServiceDAO<AmsCustomerService> getAmsCustomerServiceDAO() {
        return amsCustomerServiceDAO;
    }

    public void setAmsCustomerServiceDAO(IAmsCustomerServiceDAO<AmsCustomerService> amsCustomerServiceDAO) {
        this.amsCustomerServiceDAO = amsCustomerServiceDAO;
    }

    public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}

	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}

	public IScCustomerCopyDAO<ScCustomerCopy> getScCustomerCopyDAO() {
        return scCustomerCopyDAO;
    }

    public IScFeedDAO<ScFeed> getScFeedDao() {
        return scFeedDao;
    }

    public void setScFeedDao(IScFeedDAO<ScFeed> scFeedDao) {
        this.scFeedDao = scFeedDao;
    }

    public void setScCustomerCopyDAO(
            IScCustomerCopyDAO<ScCustomerCopy> scCustomerCopyDAO) {
        this.scCustomerCopyDAO = scCustomerCopyDAO;
    }


    public ISysPropertyDAO<SysProperty> getSysPropertyDAO() {
        return sysPropertyDAO;
    }


    public void setSysPropertyDAO(ISysPropertyDAO<SysProperty> sysPropertyDAO) {
        this.sysPropertyDAO = sysPropertyDAO;
    }


    public ISysUniqueidCounterDAO<SysUniqueidCounter> getSysUniqueIdCounterDAO() {
        return sysUniqueIdCounterDAO;
    }


    public void setSysUniqueIdCounterDAO(
            ISysUniqueidCounterDAO<SysUniqueidCounter> sysUniqueIdCounterDAO) {
        this.sysUniqueIdCounterDAO = sysUniqueIdCounterDAO;
    }


    public IAmsCustomerDAO<AmsCustomer> getAmsCustomerDAO() {
        return amsCustomerDAO;
    }


    public void setAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> amsCustomerDAO) {
        this.amsCustomerDAO = amsCustomerDAO;
    }


    public IScCustomerServiceDAO<ScCustomerService> getScCustomerServiceDAO() {
        return scCustomerServiceDAO;
    }


    public void setScSummaryTradingCustDAO(
            IScSummaryTradingCustDAO<ScSummaryTradingCust> scSummaryTradingCustDAO) {
        this.scSummaryTradingCustDAO = scSummaryTradingCustDAO;
    }


    public void setScCustomerServiceDAO(
            IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO) {
        this.scCustomerServiceDAO = scCustomerServiceDAO;
    }


    public IScSummaryTradingCustDAO<ScSummaryTradingCust> getScSummaryTradingCustDAO() {
        return scSummaryTradingCustDAO;
    }


    public List<CustomerInfo> getListFollower() {
        return listFollower;
    }


    public IScInvestmentDAO<ScInvestment> getScInvestmentDAO() {
        return scInvestmentDAO;
    }


    public void setScInvestmentDAO(IScInvestmentDAO<ScInvestment> scInvestmentDAO) {
        this.scInvestmentDAO = scInvestmentDAO;
    }


    public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getAmsWhiteLabelConfigDAO() {
        return amsWhiteLabelConfigDAO;
    }


    public void setAmsWhiteLabelConfigDAO(
            IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhiteLabelConfigDAO) {
        this.amsWhiteLabelConfigDAO = amsWhiteLabelConfigDAO;
    }


    public IScInvestmentCashflowDAO<ScInvestmentCashflow> getScInvestmentCashFlowDAO() {
        return scInvestmentCashFlowDAO;
    }


    public void setScInvestmentCashFlowDAO(
            IScInvestmentCashflowDAO<ScInvestmentCashflow> scInvestmentCashFlowDAO) {
        this.scInvestmentCashFlowDAO = scInvestmentCashFlowDAO;
    }


    public void setScOrderDAO(IScOrderDAO<ScOrderDAO> scOrderDAO) {
        this.scOrderDAO = scOrderDAO;
    }


    public void setSysAppDateDAO(ISysAppDateDAO<SysAppDate> sysAppDateDAO) {
        this.sysAppDateDAO = sysAppDateDAO;
    }


    public void setListFollower(List<CustomerInfo> listFollower) {
        this.listFollower = listFollower;
    }


    public IScOrderDAO<ScOrderDAO> getScOrderDAO() {
        return scOrderDAO;
    }

    public ISysAppDateDAO<SysAppDate> getSysAppDateDAO() {
        return sysAppDateDAO;
    }
    
	public void setAmsCashBalanceDAO(
			IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO) {
		this.amsCashBalanceDAO = amsCashBalanceDAO;
	}

	@Override
    public List<FollowPartCustomer> getListFollower(String customer_id) {
        // TODO Auto-generated method stub
        List<FollowPartCustomer> listFollower = new ArrayList<FollowPartCustomer>();       
        try {
        	listFollower = scCustomerDAO.getListCustomerFollower(customer_id);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        }
        return listFollower;
    }

    @Override
    public List<LeaderBoardCustomer> getLeaderBoard(String customerId) {
        // TODO Auto-generated method stub
        String previousBusinessDay = getPreviousBusinessDate(getWlCode());         
        List<LeaderBoardCustomer> listTopCustomer = new ArrayList<LeaderBoardCustomer>();	
        List<RankingTraderInfo> listTrader = null;
        
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setOffset(ITrsConstants.PAGING.SC_RANKING_PAGE_SIZE);

        Object o = historyScSummaryTradingCustDAO.getLastRankingBusinessDay(previousBusinessDay, IConstants.RANKING_SUMMARY_TYPE.DAILY);
        String rankingSearchDate = o == null ? previousBusinessDay : o.toString();
                
        Entry<String, SearchResult<RankingTraderInfo>> data = cachedRankingData.get(IConstants.RANKING_SUMMARY_TYPE.DAILY);
        if(data != null && data.getKey().equals(rankingSearchDate)){
        	listTrader = data.getValue();
        	pagingInfo.setTotal(data.getValue().getPagingInfo().getTotal());
        } else{
        	RankingSearchCondition condition = RankingSearchCondition.getDefaultSearchCondition();
            condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.DAILY);
        	SearchResult<RankingTraderInfo> result = new SearchResult<RankingTraderInfo>();
        	result.addAll(historyScSummaryTradingCustDAO.getRankingData(condition, pagingInfo, rankingSearchDate));
        	result.setPagingInfo(pagingInfo);
            cachedRankingData.put(condition.getSummaryType(), new AbstractMap.SimpleEntry<String, SearchResult<RankingTraderInfo>>(rankingSearchDate, result));
            listTrader = result;
        }
        
        listTopCustomer = convertTraderToCustomer(listTrader.subList(0, IConstants.LEADERBOARD.MAX_NUMBER_OF_TRADER_TO_DISPLAY), customerId);
        return listTopCustomer;
    }
    
    public String getWlCode() {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        if (frontUserDetails != null) {
            FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
            return frontUserOnline.getWlCode();
        }
        return null;
    }

    @Override
    public void loadCopyListInfo(TraderModel model) {
    	CopyListModel copyListModel = model.getCopyListModel();
    	PagingInfo pagingInfo = model.getPagingInfo();
    
    	
        String currentCustomerId = model.getId();
        //String currentAccountId = model.getAccountId();
        Integer currentMode = model.getMode();
        
        if(StringUtil.isEmpty(currentCustomerId)){
        	currentCustomerId = model.getCurrentCustomerId();
        	currentMode = IConstants.SOCIAL_MODES.OWNER_MODE;
        }
        
        String currentAccountId = "";
        List<ScCustomerService> services = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        if(services != null && !services.isEmpty()){
        	currentAccountId = services.get(0).getAccountId();
        }
        
        
		String currencyCode = "";
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = null;
		if (frontUserDetails != null)
			frontUserOnline = frontUserDetails.getFrontUserOnline();
		if (frontUserOnline != null && IConstants.SOCIAL_MODES.OWNER_MODE.equals(currentMode)) {
			currencyCode = frontUserOnline.getCurrencyCode();
		} else {
			CustomerInfo customerInfo = getCustomerInfo(currentCustomerId);
			if (customerInfo != null) {
				currencyCode = customerInfo.getCurrencyCode();
				frontUserOnline= new FrontUserOnline();
				frontUserOnline.setWlCode(customerInfo.getWlCode());
			}
		}

        if(!StringUtil.isEmpty(currentCustomerId)){
	        ScCustomer currentScCustomer = scCustomerDAO.findById(ScCustomer.class, currentCustomerId);
	        if(currentScCustomer != null){
	        	copyListModel.setCurrentUserName(currentScCustomer.getUserName());
	        }
        }
        
        String previousBusinessDate = getPreviousBusinessDate(frontUserOnline.getWlCode());
        List<CopiedScCustomer> copiedCustomerInChart = scCustomerDAO.loadCopiedCustomers(currentCustomerId, currentAccountId, previousBusinessDate, IConstants.COPY_LIST.FOR_CHART, currentMode);
        copyListModel.setCopiedCustomersInChart(copiedCustomerInChart);
        List<CopiedScCustomer> copiedCustomers = scCustomerDAO.loadCopiedCustomers(currentCustomerId, currentAccountId,previousBusinessDate, pagingInfo, currentMode);
        copyListModel.setCopiedCustomers(copiedCustomers);

        int numberOfGuru = copiedCustomerInChart.size();
        if (numberOfGuru == IConstants.COPY_LIST.MAX_ITEM_ON_CHART.intValue()) {
            numberOfGuru = scCustomerDAO.loadCopiedCustomers(currentCustomerId, currentAccountId, previousBusinessDate, IConstants.COPY_LIST.NOT_FOR_CHART, currentMode).size();
        }

        copyListModel.setNumberOfGuru(numberOfGuru);

        BigDecimal maxValue = new BigDecimal("0");
        BigDecimal minValue = new BigDecimal("0");
        int totalNameCharNum  = 0;
        for (CopiedScCustomer scCustomer : copiedCustomerInChart) {
        	if(IConstants.SOCIAL_MODES.OWNER_MODE.equals(currentMode)){
        		BigDecimal copyAmount = scCustomer.getCopyAmount();
        		if(copyAmount == null){
        			copyAmount = BigDecimal.ZERO;
        			scCustomer.setCopyAmount(copyAmount);
        		}
        		
                BigDecimal profitAmount = scCustomer.getProfit();
                if(profitAmount == null){
                	profitAmount = BigDecimal.ZERO;
                	scCustomer.setProfit(profitAmount);
                }
                
                if (maxValue.compareTo(copyAmount) < 0) {
                    maxValue = copyAmount;
                }
                if (maxValue.compareTo(profitAmount) < 0) {
                    maxValue = profitAmount;
                }
                if (minValue.compareTo(copyAmount) > 0) {
                    minValue = copyAmount;
                }
                if (minValue.compareTo(profitAmount) > 0) {
                    minValue = profitAmount;
                }
        	}else{//Guest mode
        		BigDecimal returnValue = scCustomer.getReturnValue();
        		if(returnValue == null){
        			returnValue = BigDecimal.ZERO;
        			scCustomer.setReturnValue(returnValue);
        		}
        		
        		if(minValue.compareTo(returnValue) > 0){
        			minValue = returnValue;
        		}
        		if(maxValue.compareTo(returnValue) < 0){
        			maxValue = returnValue;
        		}
        	}
            
        	totalNameCharNum += StringUtil.isEmpty(scCustomer.getName()) ? 0 : scCustomer.getName().length();
        }
        
        if(totalNameCharNum >= 80){
        	for(CopiedScCustomer scCustomer : copiedCustomerInChart){
        		String name = scCustomer.getName();
        		if(StringUtil.isEmpty(name) || name.length() <= 8) continue;
        		scCustomer.setName(name.substring(0, 5)+"...");
        	}
        }

        BigDecimal maxRangeValue = maxValue.compareTo(new BigDecimal(0)) >= 0 ? maxValue : maxValue.negate();
        BigDecimal minRangeValue = minValue.compareTo(new BigDecimal(0)) >= 0 ? minValue : minValue.negate();
        BigDecimal maxRangeNumberOfTicks = new BigDecimal(0);
        BigDecimal minRangeNumberOfTicks = new BigDecimal(0);
        BigDecimal numberOfSteps = new BigDecimal(0);

        BigDecimal max;
        if(maxValue.multiply(minValue).compareTo(BigDecimal.ZERO) >= 0){
        	max = maxRangeValue.compareTo(minRangeValue) >= 0 ? maxRangeValue : minRangeValue;
        }else{
        	max = maxRangeValue.add(minRangeValue);
        }
        BigDecimal distance = max.divide(new BigDecimal(IConstants.COPY_LIST.CHART_STEP), 0, RoundingMode.DOWN);
        if(distance == null || distance.compareTo(BigDecimal.ZERO) == 0){
        	distance = max;
        }
        
        if(distance != null && distance.compareTo(BigDecimal.ZERO) != 0){
        	BigDecimal[] maxDivideAndRemainder = maxRangeValue.divideAndRemainder(distance);

            maxRangeNumberOfTicks = maxRangeNumberOfTicks.add(maxDivideAndRemainder[0]);
            if (maxDivideAndRemainder[1].intValue() != 0) {
                maxRangeNumberOfTicks = maxRangeNumberOfTicks.add(new BigDecimal(1));
            }

            BigDecimal[] minDivideAndRemainder = minRangeValue.divideAndRemainder(distance);

            minRangeNumberOfTicks = minRangeNumberOfTicks.add(minDivideAndRemainder[0]);
            if (minDivideAndRemainder[1].intValue() != 0) {
                minRangeNumberOfTicks = minRangeNumberOfTicks.add(new BigDecimal(1));
            }

            BigDecimal productValue = maxValue.multiply(minValue);
            
            if (productValue.compareTo(new BigDecimal(0)) >= 0) {
                numberOfSteps = maxRangeNumberOfTicks.compareTo(minRangeNumberOfTicks) >= 0 ? maxRangeNumberOfTicks : minRangeNumberOfTicks;
                if (maxValue.compareTo(BigDecimal.ZERO) > 0 || minValue.compareTo(BigDecimal.ZERO) > 0) {
                    maxRangeValue = numberOfSteps.multiply(distance);
                    minRangeValue = new BigDecimal(0);
                } else {
                    maxRangeValue = new BigDecimal(0);
                    minRangeValue = numberOfSteps.multiply(distance).negate();
                }
            } else {
                numberOfSteps = maxRangeNumberOfTicks.add(minRangeNumberOfTicks);
                maxRangeValue = maxRangeNumberOfTicks.multiply(distance);
                minRangeValue = minRangeNumberOfTicks.multiply(distance).negate();
            }
        }
        
        if(maxRangeValue.compareTo(MathUtil.parseBigDecimal(1)) < 0){
        	maxRangeValue = MathUtil.parseBigDecimal(1);
        }
        if(minRangeValue.compareTo(MathUtil.parseBigDecimal(0)) < 0 && minRangeValue.compareTo(MathUtil.parseBigDecimal(-1)) > 0){
        	minRangeValue = MathUtil.parseBigDecimal(-1);
        }
        copyListModel.getChartInfo().setMaxRangeValue(maxRangeValue);
        copyListModel.getChartInfo().setMinRangeValue(minRangeValue);
        copyListModel.getChartInfo().setNumberOfTicks(numberOfSteps.add(new BigDecimal(1)));
        copyListModel.getChartInfo().setCurrencyCode(currencyCode);
    }

    @Override
    public List<RankingTraderInfo> getRankingData(RankingSearchCondition condition, PagingInfo pagingInfo, String wlCode) {
        String previousBusinessDay = getLastestUpdatedBusinessDate(wlCode);
        if(condition.getPeriod()!=null){
        	if(condition.getPeriod().equals(ITrsConstants.RANKING_PERIOD.PERIOD_1)){	    		
	    		condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.WEEKLY);
	    	} else if(condition.getPeriod().equals(ITrsConstants.RANKING_PERIOD.PERIOD_2)){
	    		condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.MONTHLY);
	    	} else if(condition.getPeriod().equals(ITrsConstants.RANKING_PERIOD.PERIOD_3)){
	    		condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.THREE_MONTHS);
	    	} else if(condition.getPeriod().equals(ITrsConstants.RANKING_PERIOD.PERIOD_4)){
	    		condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.SIX_MONTHS);
	    	}else if(condition.getPeriod().equals(ITrsConstants.RANKING_PERIOD.PERIOD_5)){
	    		condition.setSummaryType(IConstants.RANKING_SUMMARY_TYPE.ONE_YEAR);
	    	}
        }

        Object o = historyScSummaryTradingCustDAO.getLastRankingBusinessDay(previousBusinessDay, condition.getSummaryType());
        String rankingSearchDate = o == null ? previousBusinessDay : o.toString();
        condition.setFrontDate(rankingSearchDate);

        if(pagingInfo.getIndexPage() == 1 && RankingSearchCondition.isDefaultSearchOption(condition)){
            Integer mapKey = condition.getSummaryType();
            Entry<String, SearchResult<RankingTraderInfo>> data = cachedRankingData.get(mapKey);
            if(data != null && data.getKey().equals(rankingSearchDate)){
            	pagingInfo.setTotal(data.getValue().getPagingInfo().getTotal());
                return data.getValue();
            } else{
            	SearchResult<RankingTraderInfo> result = new SearchResult<RankingTraderInfo>();
            	result.addAll(historyScSummaryTradingCustDAO.getRankingData(condition, pagingInfo, rankingSearchDate));
            	result.setPagingInfo(pagingInfo);
                cachedRankingData.put(mapKey, new AbstractMap.SimpleEntry<String, SearchResult<RankingTraderInfo>>(rankingSearchDate, result));
                return result;
            }
        }
        return historyScSummaryTradingCustDAO.getRankingData(condition, pagingInfo, rankingSearchDate);
    
    }
    
    public List<String> getListGroupDemo(String wlCode){
    	AmsWhitelabelConfig amsWhitelabelConfig = amsWhiteLabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.WHILELABEL_CONFIG_KEY.TEST_SUB_GROUP_LIST, wlCode);
    	String demoGroups = amsWhitelabelConfig.getConfigValue();
    	String[] dms = demoGroups.split(",");
    	return Arrays.asList(dms);
    }
    
    public List<String> getSubGroupCdsByAccountId(String accountId, String brokerCd) {
    	List<String> subGroupCdsList = new ArrayList<String>();
    	List<ScCustomerService> listCust = this.scCustomerServiceDAO.findByAccountIdBrokerCdEnableFlg(accountId, brokerCd, IConstants.ENABLE_FLG.ENABLE);
    	if (listCust != null) {
    		for (ScCustomerService service : listCust) {
        		String subGroupCd = service.getSubGroupCd();
        		if (!StringUtil.isEmpty(subGroupCd)) {
        			subGroupCdsList.add(subGroupCd);
        		}
    		}
    	}
    	return subGroupCdsList;
    }
    
    public Map<String, String> getMapSubGroupCdsByCustomerId(String customerId) {
    	Map<String, String> subGroupCdsMap = new HashMap<String, String>();
    	List<ScCustomerService> listCust = this.scCustomerServiceDAO.findByCustomerIdEnableFlg(customerId, IConstants.ENABLE_FLG.ENABLE);
    	if (listCust != null) {
    		for (ScCustomerService service : listCust) {
        		String subGroupCd = service.getSubGroupCd();
        		if (!StringUtil.isEmpty(subGroupCd)) {
        			subGroupCdsMap.put(service.getAccountId(), subGroupCd);
        		}
    		}
    	}
    	
    	return subGroupCdsMap;
    }
    
    private String getLastestUpdatedBusinessDate(String wlCode){
    	SysAppDate currentBusinessDate = sysAppDateDAO.getCurrentBusinessDay();
        String lastestUpdatedBusinessDate = "";
        if(currentBusinessDate != null){
        	lastestUpdatedBusinessDate = currentBusinessDate.toString();
        	String today = DateUtil.toString(Calendar.getInstance(), IConstants.DATE_TIME_FORMAT.DATE_yyyyMMdd);
        	if(StringUtil.isEmpty(wlCode)) wlCode = ITrsConstants.TRS_CONSTANT.TRS_WL_CODE;
        	boolean isBusinessDate = amsWhitelabelCalendarDAO.isBusinessDay(today, wlCode);
        	if(isBusinessDate){
            	Object obj = amsWhitelabelCalendarDAO.getPreviousBussinessDay(currentBusinessDate.getId().getFrontDate(), wlCode, 1);
            	lastestUpdatedBusinessDate = obj == null ? "" : obj.toString();
        	}
        }
        
		return lastestUpdatedBusinessDate;
    }
    
	private String getPreviousBusinessDate(String wlCode) {
		SysAppDate currentBusinessDate = sysAppDateDAO.getCurrentBusinessDay();
        String previousBusinessDay = "";
        
        if(currentBusinessDate != null){
        	wlCode=  ITrsConstants.TRS_CONSTANT.TRS_WL_CODE;
        	Object obj = amsWhitelabelCalendarDAO.getPreviousBussinessDay(currentBusinessDate.getId().getFrontDate(), wlCode, 1);
            previousBusinessDay = obj == null ? "" : obj.toString();
        }
		return previousBusinessDay;
	}

    
    
    @Override
    public void loadFollowListInfo(FollowListModel followListModel, String currentCustomerId, String customerId, PagingInfo pagingInfo) {
        ScCustomer customer = scCustomerDAO.findById(ScCustomer.class, customerId);
        if (customer != null) {
            followListModel.setCurrentUserName(customer.getUserName());
            followListModel.setFollowerNo(scCustomerFollowDAO.findByCustomerId(customerId).size());
        }
        followListModel.setFollowDetails(scCustomerFollowDAO.loadFollowListInfo(currentCustomerId, customerId, pagingInfo));
    }

    @Override
    public void loadChangeFundsInfo(ChangeFundsModel changeFundsModel) {
        String customerId = changeFundsModel.getCustomerId();
        Integer copyId = changeFundsModel.getChangeFundCopyId();
        Integer scCustomerServiceId = changeFundsModel.getScCustomerServiceId();
        
        ScCustomer customerSc = scCustomerDAO.findById(ScCustomer.class, customerId);
        if (customerSc != null && IConstants.ACTIVE_FLG.ACTIVE.equals(customerSc.getActiveFlg())) {
            changeFundsModel.setNickName(customerSc.getUserName());
            changeFundsModel.setCustomerDescription(customerSc.getDescription());
        }

        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        FrontUserOnline frontUserOnline = null;
        String currentCustomerId = "";
        String currencyCode = "";
        if(frontUserDetails != null){
        	frontUserOnline = frontUserDetails.getFrontUserOnline();
        	if(frontUserOnline != null){
        		 currentCustomerId = frontUserOnline.getUserId();
        	     currencyCode = frontUserOnline.getCurrencyCode();
        	}
        }

        ScCustomerService scCustomerService = scCustomerServiceDAO.findById(ScCustomerService.class, scCustomerServiceId);
        if(scCustomerService != null && IConstants.ACTIVE_FLG.ACTIVE.equals(scCustomerService.getActiveFlg())){
        	Integer accountKind = scCustomerService.getAccountKind();
        	Integer serviceType = scCustomerService.getServiceType();
        	changeFundsModel.setAccountKind(accountKind);
        	changeFundsModel.setServiceType(serviceType);
        }
        
        String countryCode = "";
        String countryName = "";
        AmsCustomer amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
        if(amsCustomer != null){
        	AmsSysCountry sysCountry = amsCustomer.getAmsSysCountry();
        	if(sysCountry != null){
        		countryCode = sysCountry.getCountryCode();
        		countryName = sysCountry.getCountryName();
        	}
        }
        changeFundsModel.setCountryCode(countryCode);
        changeFundsModel.setCountryName(countryName);
        
        BigDecimal currentInvestment = BigDecimal.ZERO;
        List<ScInvestment> investments = scInvestmentDAO.findByCopyId(copyId);
        if(investments != null && !investments.isEmpty()){
        	ScInvestment investment = investments.get(0);
        	currentInvestment = investment.getCurrentInvestAmount();
        }
        currentInvestment = roundAmountByCurrency(currentInvestment, currencyCode);
        changeFundsModel.setCurrentInvestment(currentInvestment);

        BigDecimal availableInvestmentAmount = BigDecimal.ZERO;
        List<ScCustomerService> customerServices = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        if(customerServices != null && !customerServices.isEmpty()){
        	String accountId = customerServices.get(0).getAccountId();
        	availableInvestmentAmount = getRemainAmountAfterInvestment(currentCustomerId, accountId);
        }
        
        //BigDecimal availabelInvestmentAmount = getAvailableInvestmentAmount(currentCustomerId, customerId);
        
        availableInvestmentAmount = roundAmountByCurrency(availableInvestmentAmount, currencyCode);
        changeFundsModel.setAvailableInvestment(availableInvestmentAmount);
    }
    
    private BigDecimal roundAmountByCurrency(BigDecimal amount, String currencyCode){
    	BigDecimal roundValue  =amount;
    	CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		if(currencyInfo != null) {
			Integer scale = currencyInfo.getCurrencyDecimal();
			Integer rounding = currencyInfo.getCurrencyRound();
			roundValue = amount.divide(BigDecimal.ONE, scale, rounding);
		}
		return roundValue;
    }

    private BigDecimal getAvailableInvestmentAmount(String currentCustomerId, String customerId) {
        BigDecimal currentInvestmentAmount = getCurrentInvestmentAmount(currentCustomerId, customerId);

        BigDecimal reserveOrderAmount = historyScOrderDAO.getReserveOrderAmount(currentCustomerId, customerId);
        if (reserveOrderAmount == null) reserveOrderAmount = new BigDecimal(0);

        BigDecimal unrelizedPL = getUnrelizedPLOfAllOpenCopyOrders(currentCustomerId, customerId);

        return currentInvestmentAmount.add(unrelizedPL).subtract(reserveOrderAmount);
    }


    private BigDecimal getUnrelizedPLOfAllOpenCopyOrders(String currentCustomerId, String customerId) {
        List<OpenCopyOrder> openCopyOrders = historyScOrderDAO.getOpenCopyOrder(currentCustomerId, customerId);
        if (openCopyOrders == null) {
            return new BigDecimal(0);
        }

        BigDecimal totalUnrelizedPl = new BigDecimal(0);
        for (OpenCopyOrder openCopyOrder : openCopyOrders) {
            Integer orderSide = openCopyOrder.getOrderSide();
            BigDecimal executionPrice = openCopyOrder.getExecutionPrice();
            BigDecimal orderAmount = openCopyOrder.getOrderAmount();
            String symbol = openCopyOrder.getSymbol();

            BigDecimal unrelizedPl = new BigDecimal(0);
            if (IConstants.ORDER_SIDE.BUY == orderSide.intValue()) {
                FxFrontRateInfo rateInfo = SocialMemcached.getInstance().getFrontRateInfo(symbol);
                List<RateBandInfo> rateBandInfos = rateInfo.getBidBandInfoList();
                BigDecimal bidRate;
                if (rateBandInfos != null && !rateBandInfos.isEmpty()) {
                    bidRate = rateBandInfos.get(0).getRate();
                } else {
                    bidRate = new BigDecimal(0);
                }

                unrelizedPl = bidRate.subtract(executionPrice).multiply(orderAmount);
            } else if (IConstants.ORDER_SIDE.SELL == orderSide.intValue()) {
                FxFrontRateInfo rateInfo = SocialMemcached.getInstance().getFrontRateInfo(symbol);
                List<RateBandInfo> rateBandInfos = rateInfo.getAskBandInfoList();
                BigDecimal askRate;
                if (rateBandInfos != null && !rateBandInfos.isEmpty()) {
                    askRate = rateBandInfos.get(0).getRate();
                } else {
                    askRate = new BigDecimal(0);
                }
                unrelizedPl = executionPrice.subtract(askRate).abs().multiply(orderAmount);

            }

            totalUnrelizedPl = totalUnrelizedPl.add(unrelizedPl);
        }

        return totalUnrelizedPl;
    }


    private BigDecimal getCurrentInvestmentAmount(String currentCustomerId, String customerId) {
        BigDecimal currentInvestmentAmount = new BigDecimal(0);
        List<ScInvestment> scInvestments = scInvestmentDAO.findByCurrentAndCopyCustomerId(currentCustomerId, customerId);
        if (scInvestments != null && !scInvestments.isEmpty()) {
            currentInvestmentAmount = scInvestments.get(0).getCurrentInvestAmount();
        }
        return currentInvestmentAmount;
    }

    @Override
    public BigDecimal getMinInvestmentAmount(String baseCurrency) {
        /*AmsWhitelabelConfig whiteLabelConfig = amsWhiteLabelConfigDAO.getAmsWhiteLabelConfig(configKey, wlCode);
        if (whiteLabelConfig == null) {
            return new BigDecimal(0);
        }
        return new BigDecimal(whiteLabelConfig.getConfigValue());*/
    	String key = IConstants.COPY_LIST.MIN_INVEST_AMOUNT;
    	
    	SysPropertyId sysPropId = new SysPropertyId();
    	sysPropId.setPropertyKey(key);
    	sysPropId.setPropType(baseCurrency);
    	SysProperty sysProperty =  sysPropertyDAO.findById(SysProperty.class, sysPropId);
    	if(sysProperty == null || IConstants.ACTIVE_FLG.INACTIVE.equals(sysProperty.getActiveFlg())){
    		return MathUtil.parseBigDecimal(0);
    	}
    	return MathUtil.parseBigDecimal(sysProperty.getPropertyValue());
    }

    @Override
    public boolean checkRemainAmountAfterInvestment(String customerId, Integer copyId, BigDecimal inputInvestmentAmount) {
        List<ScCustomerService> customerServices = scCustomerServiceDAO.findByCustomerIdAndServiceType(customerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        BigDecimal remainAmountAfterInvestment = customerServices.size() == 0 ? new BigDecimal("0") : getRemainAmountAfterInvestment(customerId, customerServices.get(0).getAccountId());

        BigDecimal currentInvestAmt = new BigDecimal("0");
        
        List<ScInvestment> scInvestments = scInvestmentDAO.findByCopyId(copyId);
        if (scInvestments != null && !scInvestments.isEmpty()) {
            ScInvestment investment = scInvestments.get(0);
            currentInvestAmt = investment.getCurrentInvestAmount();
        }

        BigDecimal amt = inputInvestmentAmount.subtract(currentInvestAmt);
        if (remainAmountAfterInvestment.compareTo(amt) < 0) {
            return false;
        }
        return true;
    }

    private BigDecimal getRemainAmountAfterInvestment(String customerId, String accountId) {
        BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(String.valueOf(accountId));

        BigDecimal totalInvestmentAmount = scInvestmentDAO.getScTotalInvestmentAmount(String.valueOf(customerId));
        if (totalInvestmentAmount == null) {
            totalInvestmentAmount = new BigDecimal(0);
        }
        balanceInfo.setTotalInvestment(totalInvestmentAmount.doubleValue());
        
//      Double amt1 = balanceInfo.getFreemargin() - balanceInfo.getCredit();
//      Double minAmt = (balanceInfo.getBalance() < amt1) ? balanceInfo.getBalance() : amt1;
        
        Double amountAvailable = balanceInfo.getAmountAvailable();

        Double remainAmountAfterInvestment = amountAvailable - totalInvestmentAmount.doubleValue();
        if (remainAmountAfterInvestment < 0) {
            remainAmountAfterInvestment = new Double(0);
        }
        return MathUtil.parseBigDecimal(remainAmountAfterInvestment);// BigDecimal("100000");
    }
    
    @Override
	public void updateCopyRate(ChangeFundsModel changeFundsModel) {
    	FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
    	FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
    	
		String currentCustomerId = frontUserOnline.getUserId();
		Integer copyId = changeFundsModel.getChangeFundCopyId();
		
		ScCustomerCopy scCustomerCopy = scCustomerCopyDAO.findById(ScCustomerCopy.class, copyId);
		if(scCustomerCopy != null && IConstants.ACTIVE_FLG.ACTIVE.equals(scCustomerCopy.getActiveFlg())){
			String copyCustomerId  = scCustomerCopy.getCopier().getCustomerId();
			BigDecimal investAmount = MathUtil.parseBigDecimal(changeFundsModel.getAmount());
			String copyAccountId = scCustomerCopy.getCopyAccountId();
			String copyBrokerCd = scCustomerCopy.getCopyBrokerCd();
					
			BigDecimal equityPercentage = computeEquityPercentage(currentCustomerId, copyCustomerId, investAmount, copyAccountId, copyBrokerCd);
			scCustomerCopy.setCopyRate(equityPercentage);
			scCustomerCopy.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			scCustomerCopyDAO.merge(scCustomerCopy);
		}
		
	}
	
	
    @Override
    public void updateInvestmentAmount(ChangeFundsModel changeFundsModel) {
        FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
        FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();

        String currentCustomerId = frontUserOnline.getUserId();
        //String customerId = String.valueOf(changeFundsModel.getCustomerId());
        Integer copyId = changeFundsModel.getChangeFundCopyId();
        BigDecimal investmentAmt = MathUtil.parseBigDecimal(changeFundsModel.getAmount());
        List<ScInvestment> scInvestments = scInvestmentDAO.findByCopyId(copyId);
        if (scInvestments != null) {
            for (ScInvestment scInvestment : scInvestments) {
                BigDecimal currentInvestmentAmt = scInvestment.getCurrentInvestAmount();
                BigDecimal amt = investmentAmt.subtract(currentInvestmentAmt);
                BigDecimal originalInvestmentAmt = scInvestment.getOriginalInvestAmount();
                scInvestment.setOriginalInvestAmount(originalInvestmentAmt.add(amt));
                scInvestment.setCurrentInvestAmount(investmentAmt);
                scInvestment.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                scInvestmentDAO.merge(scInvestment);

                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                ScInvestmentCashflow scInvestmentCashFlow = new ScInvestmentCashflow();
                //String investCashflowId = generateUniqueId(IConstants.UNIQUE_CONTEXT.INVEST_CASH_FLOW_CONTEXT);
                //scInvestmentCashFlow.setInvestCashflowId(investCashflowId);
                scInvestmentCashFlow.setScInvestmentId(scInvestment.getScInvestmentId());
                scInvestmentCashFlow.setCustomerId(currentCustomerId);
                scInvestmentCashFlow.setEventDatetime(currentTimestamp);
                SysAppDate currentBusinessDay = sysAppDateDAO.getCurrentBusinessDay();
                String currentBusinessDate = currentBusinessDay.getId().getFrontDate();
                scInvestmentCashFlow.setEventDate(currentBusinessDate);
                scInvestmentCashFlow.setValueDate(currentBusinessDate);
                scInvestmentCashFlow.setCashflowType(IConstants.CASHFLOW_TYPE.TRANSFER);
                scInvestmentCashFlow.setCashflowAmount(investmentAmt.subtract(currentInvestmentAmt));
                scInvestmentCashFlow.setCashBalance(investmentAmt);
                String currencyCode = frontUserOnline.getCurrencyCode();
                scInvestmentCashFlow.setCurrencyCode(currencyCode);
                scInvestmentCashFlow.setRate(new BigDecimal(1));
                scInvestmentCashFlow.setTax(new BigDecimal(0));
                scInvestmentCashFlow.setSourceId(null);
                scInvestmentCashFlow.setServiceType(null);
                scInvestmentCashFlow.setActiveFlg(new Integer(1));
                scInvestmentCashFlow.setInputDate(currentTimestamp);
                scInvestmentCashFlow.setUpdateDate(currentTimestamp);

                scInvestmentCashFlowDAO.save(scInvestmentCashFlow);
            }
        }
    }

    @Override
    public void stopCopyProcessing(TraderModel model) throws InvalidActionException {
    	CopyListModel copyListModel = model.getCopyListModel();
        
    	String stopCopyCustomerId = copyListModel.getStopCopyCustomerId();
        String stopAccountId = copyListModel.getStopCopyAccountId();
        
        String brokerCd = copyListModel.getStopCopyBrokerCd();
        String currentCustomerId = model.getCurrentCustomerId();
        String currentAccountId = model.getAccountId();
        
        stopCopy(currentCustomerId, stopCopyCustomerId, stopAccountId, brokerCd);
        
        FrontUserDetails frontUserDetails  = FrontUserOnlineContext.getFrontUserOnline();
        FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
        
        String previousBusinessDate = getPreviousBusinessDate(frontUserOnline.getWlCode());
        List<CopiedScCustomer> lst = scCustomerDAO.loadCopiedCustomers(currentCustomerId, currentAccountId, previousBusinessDate, IConstants.COPY_LIST.NOT_FOR_CHART, model.getMode());
        copyListModel.setCopyListCount(lst.size());
    }

    private synchronized String generateUniqueId(String contextID) {
        if (contextID == null || contextID.trim().equals("")) {
            return null;
        }
        String uniqueId = sysUniqueIdCounterDAO.generateUniqueId(contextID);
        return uniqueId;
    }

    @Override
    public Integer stopCopy(String currentCustomerId, String stopCustomerId, String accountId, String brokerCd) throws InvalidActionException {
        List<ScCustomerCopy> alreadyCopied = scCustomerCopyDAO.findByRelationship(stopCustomerId, currentCustomerId, accountId, brokerCd);
        if(alreadyCopied.size() == 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_070);
        }

        List<ScCustomerCopy> copyConnections = scCustomerCopyDAO.findByRelationship(stopCustomerId, currentCustomerId, accountId, brokerCd);
        List<ScCustomerService> services = scCustomerServiceDAO.findByCondition(stopCustomerId, IConstants.ENABLE_FLG.ENABLE, accountId, brokerCd);
        ScCustomerService scCustomerService = services.size() == 0 ? null : services.get(0);
        if (copyConnections != null && copyConnections.size() > 0) {
            for (ScCustomerCopy copy : copyConnections) {
                copy.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
                copy.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                scCustomerCopyDAO.merge(copy);
                
                Integer copyId=  copy.getCopyId();
                updateScInvestment(copyId);
            }

            if (scCustomerService != null) {
                Integer copierNo = scCustomerService.getCopierNo();
                scCustomerService.setCopierNo(copierNo - 1);
                scCustomerServiceDAO.merge(scCustomerService);
            }
        }
        ScSignalRelationInfo jmsFeed = new ScSignalRelationInfo();
        jmsFeed.setActionTime(new Timestamp(System.currentTimeMillis()));
        jmsFeed.setActionType(IConstants.SC_ACTION_TYPE.UNCOPY);
        jmsFeed.setCustomerId(currentCustomerId);
        jmsFeed.setTargetCustomerId(stopCustomerId);

//        JMSContext.getInstance().send(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        CustomerRankingCache.getInstance().decreaseCopierNo(jmsRealSender, accountId, brokerCd);
        return scCustomerService == null ? 0 : scCustomerService.getCopierNo();
    }

    private void updateScInvestment(Integer copyId) {
    	List<ScInvestment> scInvestments = scInvestmentDAO.findByCopyId(copyId);
        if (scInvestments != null) {
            for (ScInvestment scInvest : scInvestments) {
            	if(IConstants.ACTIVE_FLG.INACTIVE.equals(scInvest.getActiveFlg())){
            		continue;
            	}
                scInvest.setEnableFlg(IConstants.ACTIVE_FLG.INACTIVE);
                scInvestmentDAO.merge(scInvest);
            }
        }
	}

	@Override
    public Integer stopFollow(String currentCustomerId, String stopCustomerId) throws InvalidActionException {
        List<ScCustomerFollow> alreadyFollowed = scCustomerFollowDAO.findByRelationship(stopCustomerId, currentCustomerId);
        if(alreadyFollowed.size() == 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_068);
        }

        List<ScCustomerCopy> copyConns = scCustomerCopyDAO.findByRelationship(stopCustomerId, currentCustomerId);
        if (copyConns.size() > 0) throw new InvalidActionException(InvalidActionException.MSG_SC_019);
        List<ScCustomerFollow> cusConnections = scCustomerFollowDAO.findByRelationship(stopCustomerId, currentCustomerId);
        if (cusConnections != null) {
            for (ScCustomerFollow cusCon : cusConnections) {
                cusCon.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
                cusCon.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                scCustomerFollowDAO.merge(cusCon);
            }
        }

        ScCustomer scCustomer = scCustomerDAO.findById(ScCustomer.class, stopCustomerId);
        if (scCustomer != null && IConstants.ACTIVE_FLG.ACTIVE.equals(scCustomer.getActiveFlg())) {
            Integer followNo = scCustomer.getFollowerNo();
            scCustomer.setFollowerNo(followNo == null ? 0 : followNo - 1);
            scCustomerDAO.merge(scCustomer);
        }
        ScSignalRelationInfo jmsFeed = new ScSignalRelationInfo();
        jmsFeed.setActionTime(new Timestamp(System.currentTimeMillis()));
        jmsFeed.setActionType(IConstants.SC_ACTION_TYPE.UNFOLLOW);
        jmsFeed.setCustomerId(currentCustomerId);
        jmsFeed.setTargetCustomerId(stopCustomerId);

//        JMSContext.getInstance().send(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        CustomerRankingCache.getInstance().decreaseFollowerNo(jmsRealSender, stopCustomerId);
        return scCustomer == null ? 0 : scCustomer.getFollowerNo();
    }

    @Override
    public Integer followCustomer(String currentCustomerId, String followCustomerId) throws InvalidActionException {
        List<ScCustomerFollow> alreadyFollowed = scCustomerFollowDAO.findByRelationship(followCustomerId, currentCustomerId);
        if(alreadyFollowed.size() > 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_067);
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ScCustomerFollow connection = new ScCustomerFollow();
        connection.setUpdateDate(timestamp);
        connection.setCustomer(scCustomerDAO.findById(ScCustomer.class, currentCustomerId));
        connection.setFollower(scCustomerDAO.findById(ScCustomer.class, followCustomerId));
        connection.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
        connection.setFollowDatetime(timestamp);
        connection.setInputDate(timestamp);
        scCustomerFollowDAO.save(connection);

        ScCustomer scCustomer = scCustomerDAO.findById(ScCustomer.class, followCustomerId);
        if (scCustomer != null && IConstants.ACTIVE_FLG.ACTIVE.equals(scCustomer.getActiveFlg())) {
            Integer followNo = scCustomer.getFollowerNo();
            scCustomer.setFollowerNo(followNo == null ? 1 : followNo + 1);
            scCustomerDAO.merge(scCustomer);
        }
        ScSignalRelationInfo jmsFeed = new ScSignalRelationInfo();
        jmsFeed.setActionTime(timestamp);
        jmsFeed.setActionType(IConstants.SC_ACTION_TYPE.FOLLOW);
        jmsFeed.setCustomerId(currentCustomerId);
        jmsFeed.setTargetCustomerId(followCustomerId);
//        JMSContext.getInstance().send(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);

        CustomerRankingCache.getInstance().increaseFollowerNo(jmsRealSender, followCustomerId);
        return scCustomer == null ? 0 : scCustomer.getFollowerNo();
    }

    @Override
    public Integer copyTradeCustomer(String currentCustomerId, String copyCustomerId, String accountId, String brokerCd, CopyFollowInfo copyInfo, String wlCode) throws InvalidActionException {
        List<ScCustomerCopy> alreadyCopied = scCustomerCopyDAO.findByRelationship(copyCustomerId, currentCustomerId, accountId, brokerCd);
        if(alreadyCopied.size() > 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_069);
        }
        List<ScCustomerService> customerServices = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        //start check copy loop
        if(customerServices.size() > 0){
            List<ScCustomerCopy> copied = scCustomerCopyDAO.findByRelationship(currentCustomerId, copyCustomerId, customerServices.get(0).getAccountId(), customerServices.get(0).getBrokerCd());
            if(copied.size() > 0){
                List<ScCustomerService> guruServiceList = scCustomerServiceDAO.findByCustomerIdAndServiceType(copyCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
                boolean isValid = true;
                for(ScCustomerService service : guruServiceList){
                    //invalid if logged in user is copying from COPY TRADE account of guru
                    if(service.getAccountId().equals(accountId) && service.getBrokerCd().equals(brokerCd)){
                        isValid = false;
                        break;
                    }
                }
                if(!isValid) throw new InvalidActionException(InvalidActionException.MSG_SC_046);
            }
        }
        //end check copy loop
        ScCustomer customerGuru = scCustomerDAO.findById(ScCustomer.class, copyCustomerId);
        ScCustomer loggedInCustomer = scCustomerDAO.findById(ScCustomer.class, currentCustomerId);
        String systemBaseCurrency = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.BASE_CURRENCY).get(ITrsConstants.SYS_PROPERTY.BASE_CURRENCY); 
        String baseCurrency = customerServices.size() > 0 ? customerServices.get(0).getBaseCurrency() : systemBaseCurrency;
        String minAmountStr = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + "MIN_INVEST_AMOUNT").get(baseCurrency);
        BigDecimal minInvestAmount = MathUtil.parseBigDecimal(minAmountStr, null);
        if (minInvestAmount != null && minInvestAmount.compareTo(copyInfo.getInvestAmount()) > 0) {
            throw new InvalidActionException(InvalidActionException.MSG_SC_022, new String[]{minAmountStr});
        }
        BigDecimal remainAmount = customerServices.size() == 0 ? new BigDecimal("0") : getRemainAmountAfterInvestment(currentCustomerId, customerServices.get(0).getAccountId());
        if(remainAmount.compareTo(copyInfo.getInvestAmount()) < 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_023);
        }
        Integer maxCopierNo = MathUtil.parseInteger(SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + "MAX_COPIER_NO").get("1"), null);
        List<ScCustomerService> services = scCustomerServiceDAO.findByCondition(copyCustomerId, IConstants.ENABLE_FLG.ENABLE, accountId, brokerCd);
        ScCustomerService scCustomerServiceGuru = services.size() == 0 ? null : services.get(0);
        if (maxCopierNo != null && maxCopierNo < getCopierNo(copyCustomerId, true, null, null) + 1) {
            throw new InvalidActionException(InvalidActionException.MSG_SC_025);
        }
        BigDecimal minEquity = MathUtil.parseBigDecimal(SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + "MINIMUM_EQUITY").get("1"), null);
        BigDecimal traderEquity = scCustomerServiceGuru == null ? new BigDecimal("0") : scCustomerServiceGuru.getEquity();
        BigDecimal convertRate = getConvertRateOnFrontRate(scCustomerServiceGuru.getBaseCurrency(), systemBaseCurrency);
        LOG.info("copyTradeCustomer: convertRate from " + scCustomerServiceGuru.getBaseCurrency() + " to " + systemBaseCurrency + ": " + convertRate);
        if(traderEquity == null || traderEquity.multiply(convertRate).compareTo(minEquity) < 0){
            throw new InvalidActionException(InvalidActionException.MSG_SC_062);
        }
        //Do copy
        if (customerServices.size() > 0) {
            List<ScCustomerFollow> alreadyFollowed = scCustomerFollowDAO.findByRelationship(copyCustomerId, currentCustomerId);
            if(alreadyFollowed.size() == 0){
                followCustomer(currentCustomerId, copyCustomerId);
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SysAppDate businessDate = sysAppDateDAO.getCurrentBusinessDay();
            ScCustomerService service = customerServices.get(0);
            ScCustomerCopy customerCopy = new ScCustomerCopy();
            customerCopy.setCustomer(loggedInCustomer);
            customerCopy.setCopier(customerGuru);
            customerCopy.setScCustServiceId(service.getScCustServiceId());
            String scBrokerCd = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.BROKER_OWNER).get("1");
            customerCopy.setBrokerCd(scBrokerCd);
            customerCopy.setAccountId(service.getAccountId());
            customerCopy.setLeverage(service.getLeverage());
            customerCopy.setCurrencyCd(service.getBaseCurrency());
            customerCopy.setCopyCustServiceId(services.size() > 0 ? services.get(0).getScCustServiceId() : null);
            customerCopy.setCopyAccountId(accountId);
            customerCopy.setCopyBrokerCd(brokerCd);
            //customerCopy.setCopyRate(copyInfo.getEquityPercentage());
            customerCopy.setCopyRate(computeEquityPercentage(currentCustomerId, copyCustomerId, copyInfo.getInvestAmount(), accountId, brokerCd));
            customerCopy.setCopyCurrencyCd(scCustomerServiceGuru == null ? null : scCustomerServiceGuru.getBaseCurrency());
            customerCopy.setCopyDatetime(timestamp);
            customerCopy.setInputDate(timestamp);
            customerCopy.setUpdateDate(timestamp);
            customerCopy.setLockFlg(0);
            customerCopy.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
            scCustomerCopyDAO.save(customerCopy);

            ScInvestment investment = new ScInvestment();
            investment.setCopyCustomerId(copyCustomerId);
            investment.setCopyId(customerCopy.getCopyId());
            investment.setCustomerId(currentCustomerId);
            BigDecimal zero = new BigDecimal("0");
            investment.setCopyAmount(zero);
            investment.setProfit(zero);
            investment.setCurrencyCode(baseCurrency);
            investment.setOriginalInvestAmount(copyInfo.getInvestAmount());
            investment.setCurrentInvestAmount(copyInfo.getInvestAmount());
            investment.setInputDate(timestamp);
            investment.setUpdateDate(timestamp);
            investment.setEnableFlg(IConstants.ENABLE_FLG.ENABLE);
            investment.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
            scInvestmentDAO.save(investment);

            ScInvestmentCashflow cashflow = new ScInvestmentCashflow();
            //cashflow.setInvestCashflowId(generateUniqueId(IConstants.UNIQUE_CONTEXT.INVEST_CASH_FLOW_CONTEXT));
            cashflow.setScInvestmentId(investment.getScInvestmentId());
            cashflow.setCustomerId(currentCustomerId);
            cashflow.setEventDatetime(timestamp);
            cashflow.setEventDate(businessDate.getId().getFrontDate());
            cashflow.setValueDate(businessDate.getId().getFrontDate());
            cashflow.setCashflowType(IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER);
            cashflow.setCashflowAmount(copyInfo.getInvestAmount());
            cashflow.setCashBalance(copyInfo.getInvestAmount());
            cashflow.setCurrencyCode(baseCurrency);
            cashflow.setRate(new BigDecimal("1"));
            cashflow.setTax(zero);
            cashflow.setInputDate(timestamp);
            cashflow.setUpdateDate(timestamp);
            cashflow.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
            scInvestmentCashFlowDAO.save(cashflow);

            scCustomerServiceGuru.setCopierNo(scCustomerServiceGuru.getCopierNo() == null ? 1 : scCustomerServiceGuru.getCopierNo() + 1);
            scCustomerServiceDAO.merge(scCustomerServiceGuru);

            ScSignalRelationInfo jmsFeed = new ScSignalRelationInfo();
            jmsFeed.setActionTime(timestamp);
            jmsFeed.setActionType(IConstants.SC_ACTION_TYPE.COPY);
            jmsFeed.setCustomerId(currentCustomerId);
            jmsFeed.setTargetCustomerId(copyCustomerId);
//            JMSContext.getInstance().send(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
            jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SOCIAL_FEED_AGENT_REQUEST, jmsFeed, false);
        }
        CustomerRankingCache.getInstance().increaseCopierNo(jmsRealSender, accountId, brokerCd);
        return scCustomerServiceGuru == null ? 0 : scCustomerServiceGuru.getCopierNo();
    }


    @Override
    /*
          * Call FxSymbolDao to get list symbol user
          * @see phn.nts.ams.fe.business.ISocialManager#getListSymbolSetting()
          */
    public List<FxSymbol> getListSymbolCurrency(String customerId) {
        List<FxSymbol> listSymbol = null;
        try {
            listSymbol = fxSymbolDAO.getListSymbolCurrency(customerId);
        } catch (Exception e) {
            LOG.error("Exeception in get list symbol setting");
        }
        return listSymbol;
    }


    @Override
    public void loadCopyFollowInfo(CopyFollowInfo result, String currentCustomerId, String guestCustomerId, String accountId, String brokerCd, boolean getAllCopierNo, Integer mode) {
        if (IConstants.SOCIAL_MODES.GUEST_MODE.equals(mode)) {
            if(StringUtil.isEmpty(currentCustomerId)){
                result.setFollowFlg(IConstants.ACTIVE_FLG.INACTIVE);
            } else {
                List<ScCustomerFollow> followConnections = scCustomerFollowDAO.findByRelationship(guestCustomerId, currentCustomerId);
                result.setFollowFlg(followConnections.size() == 0 ? IConstants.ACTIVE_FLG.INACTIVE : IConstants.ACTIVE_FLG.ACTIVE);
                List<ScCustomerService> services = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
                if(services.size() > 0){
                    result.setSocialBaseCurrency(services.get(0).getBaseCurrency());
                    BigDecimal remainAmount = getRemainAmountAfterInvestment(currentCustomerId, services.get(0).getAccountId());
                    CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + services.get(0).getBaseCurrency());
                    if(currencyInfo != null){
                        remainAmount = remainAmount.setScale(currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
                    }
                    result.setAvailableInvestAmount(remainAmount);
                }
            }
        }

        ScCustomer customer = scCustomerDAO.findById(ScCustomer.class, guestCustomerId);
        if(customer != null) {
        	//result.setFollowers(customer.getFollowerNo());
            result.setUserName(customer.getUserName());
        }
        
        //[NTS1.0-le.hong.ha]Aug 7, 2013A - Start 
        // update fix bug: #19257
		String customerId = "";
		// owner mode
		if (getAllCopierNo && currentCustomerId != null){
			customerId = currentCustomerId;
		}
		// guest mode
		else {
			customerId = guestCustomerId;
		}
		Integer followers = scCustomerFollowDAO.getNoFollowers(customerId);
		result.setFollowers(followers);
    	Integer copiers = scCustomerCopyDAO.countCopierNumber(accountId, brokerCd);
    	result.setCopiers(copiers);
		//[NTS1.0-le.hong.ha]Aug 7, 2013A - End
    	
        //Integer copierNo = getCopierNo(guestCustomerId, getAllCopierNo, accountId, brokerCd);
        //result.setCopiers(copierNo);
    }
    
    public Integer countFollower(String customerId){
    	return scCustomerFollowDAO.getNoFollowers(customerId);
    }

    public Integer countCopier(String accountId, String brokerCd){
    	return scCustomerCopyDAO.countCopierNumber(accountId, brokerCd);
    }
    
    private Integer getCopierNo(String customerId, boolean getAllCopierNo, String accountId, String brokerCd) {
        List<ScCustomerService> services = getAllCopierNo ? scCustomerServiceDAO.findByCustomerIdEnableFlg(customerId, IConstants.ENABLE_FLG.ENABLE) :
                scCustomerServiceDAO.findByCondition(customerId, IConstants.ENABLE_FLG.ENABLE, accountId, brokerCd);
        Integer copierNo = 0;
        for(ScCustomerService item : services){
            copierNo += item.getCopierNo() == null ? 0 : item.getCopierNo();
        }
   
        return copierNo;
    }

//    @Override
//    public String isCopy(String customerId, String otherCustomerAccountId, String otherCustomerBrokerCd) {
//        // TODO Auto-generated method stub
//        return scCustomerCopyDAO.isCopy(customerId, otherCustomerAccountId, otherCustomerBrokerCd);
//    }

    public IScCustomerDAO<ScCustomer> getScCustomerDAO() {
        return scCustomerDAO;
    }

    public void setScCustomerDAO(IScCustomerDAO<ScCustomer> scCustomerDAO) {
        this.scCustomerDAO = scCustomerDAO;
    }

    public IFxSymbolDAO<FxSymbol> getFxSymbolDAO() {
        return fxSymbolDAO;
    }

    public void setFxSymbolDAO(IFxSymbolDAO<FxSymbol> fxSymbolDAO) {
        this.fxSymbolDAO = fxSymbolDAO;
    }

    @Override
    public boolean checkCustomerExistence(String customerId) {
        ScCustomer scCustomers = scCustomerDAO.findById(ScCustomer.class, customerId);
        if (scCustomers != null)
            return true;
        return false;
    }


    @Override
    public ScCustomer getSignalProviderInformation(String usedId) {
        ScCustomer result = null;
        try {
            List<ScCustomer> list = new ArrayList<ScCustomer>();
            list = scCustomerDAO.findCustomerById(usedId);
            if (list != null && list.size() > 0) {
                result = list.get(0);
            }
        } catch (RuntimeException e) {
            LOG.error("Error when get signal provider information for user name :" + usedId);
        }
        return result;
    }

    @Override
    public ScSummaryTradingCust getCopyTradeInformation(String customerId) {
        ScSummaryTradingCust result = null;
        ScCustomerService service = null;
        try {
            List<ScCustomerService> listService = new ArrayList<ScCustomerService>();
            listService = scCustomerServiceDAO.getListScCustomerService(customerId);
            if (listService != null && listService.size() > 0) {
                service = listService.get(0);
                if (!service.getAccountId().isEmpty()) {
                    List<ScSummaryTradingCust> listSum = new ArrayList<ScSummaryTradingCust>();
                    listSum = historyScSummaryTradingCustDAO.findSummaryByCustomerId(service.getAccountId());
                    result = listSum.get(0);
                }
            }
        } catch (RuntimeException e) {
            LOG.error("Error when get copy trade information for customer with Id :" + customerId);
        }
        return result;
    }


    @Override
    public CustomerInfo getCustomerFromId(String customerId, String frontUserId) {
        CustomerInfo sc = new CustomerInfo();
        if (customerId != null) {
            AmsCustomer amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
            if (amsCustomer != null) {
                AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
                if (amsSysCountry != null) {
                    sc.setCountryName(amsSysCountry.getCountryName());
                    sc.setCountryCode(amsSysCountry.getCountryCode());
                }
            } else {
                amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, frontUserId);
                if (amsCustomer != null) {
                    AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
                    if (amsSysCountry != null) {
                        sc.setCountryName(amsSysCountry.getCountryName());
                        sc.setCountryCode(amsSysCountry.getCountryCode());
                    }
                }
            }
        } else {
            AmsCustomer amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, frontUserId);
            if (amsCustomer != null) {
                AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
                if (amsSysCountry != null) {
                    sc.setCountryName(amsSysCountry.getCountryName());
                    sc.setCountryCode(amsSysCountry.getCountryCode());
                }
            }
        }


        ScCustomer customer = scCustomerDAO.getScCustomer(customerId);

        if (customer != null) {
            sc.setCustomerId(customer.getCustomerId());
            if (customer.getSignalTotalReturn()!=null){
            	sc.setReturnRate(customer.getSignalTotalReturn().toString());	
            }
            else {
            	sc.setReturnRate(" ");
            }
            
            sc.setUsername(customer.getUserName());
            //sc.setCopierNo(customer.getCopierNo());
            sc.setFollowerNo(customer.getFollowerNo());
            sc.setDescription(customer.getDescription());
        } else {
            if (frontUserId != null) {
                customer = scCustomerDAO.getScCustomer(frontUserId);
                if (customer != null) {
                    sc.setCustomerId(customer.getCustomerId());
                    sc.setReturnRate(customer.getSignalTotalReturn().toString());
                    sc.setUsername(customer.getUserName());
                    //sc.setCopierNo(customer.getCopierNo());
                    sc.setFollowerNo(customer.getFollowerNo());
                    sc.setDescription(customer.getDescription());
                }
            }
        }
        return sc;
    }

    @Override
    public ScCustomer getScCustomerFromId(String customerId){
    	return scCustomerDAO.getScCustomer(customerId);
    }

    @Override
    public ScCustomerServiceInfo getCustomerService(String customerId, Integer serviceType) {
        List<ScCustomerService> serviceList = scCustomerServiceDAO.findByCustomerIdAndServiceType(customerId, serviceType);
        if(serviceList.size() > 0){
            ScCustomerServiceInfo info = new ScCustomerServiceInfo();
            BeanUtils.copyProperties(serviceList.get(0), info);
            return info;
        } else {
            return null;
        }
    }

    /*@Override
    public SearchResult<CustomerInfo> getCopierList(String customer_id, PagingInfo paging) {

        SearchResult<ScCustomer> listCopiers = scCustomerDAO.getCopierList(customer_id, paging);
        if (listCopiers == null) {
            return null;
        }
        SearchResult<CustomerInfo> result = new SearchResult<CustomerInfo>();
        result.setPagingInfo(listCopiers.getPagingInfo());
        for (ScCustomer customer : listCopiers) {
            result.add(CustomerInfoConverter.toCustomerInfo(customer));            
        }
        return result;
    }*/


    /**
     * @return the scCustomerFollowDAO
     */
    public IScCustomerFollowDAO<ScCustomerFollow> getScCustomerFollowDAO() {
        return scCustomerFollowDAO;
    }


    /**
     * @param scCustomerFollowDAO the scCustomerFollowDAO to set
     */
    public void setScCustomerFollowDAO(IScCustomerFollowDAO<ScCustomerFollow> scCustomerFollowDAO) {
        this.scCustomerFollowDAO = scCustomerFollowDAO;
    }

    public CustomerInfo getCustomerInfo(String customerId) {
        CustomerInfo customerInfo = null;
        AmsCustomer amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
        if (amsCustomer != null) {
            customerInfo = new CustomerInfo();
            BeanUtils.copyProperties(amsCustomer, customerInfo);
            SysCurrency amsSysCurrency = amsCustomer.getSysCurrency();
            if (amsSysCurrency != null) {
                customerInfo.setCurrencyCode(amsSysCurrency.getCurrencyCode());
            }
            AmsGroup amsGroup = amsCustomer.getAmsGroup();
            if (amsGroup != null) {
                customerInfo.setGroupId(amsGroup.getGroupId());
                customerInfo.setGroupName(amsGroup.getGroupName());
            }
            AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
            if (amsSysCountry != null) {
                customerInfo.setCountryId(amsSysCountry.getCountryId());
            }

        }
        return customerInfo;
    }


    @Override
    public void loadLiveRateSymbolsInfo(LiveRateInfo liveRateInfo) {
        String symbolStr = "";
        List<SysProperty> sysProperties = sysPropertyDAO.findByPropertyKey(IConstants.SYS_PROPERTY.SC_SYMBOL_RATE);
        for (SysProperty prop : sysProperties) {
            if (IConstants.ACTIVE_FLG.ACTIVE.equals(prop.getActiveFlg())) {
                symbolStr = prop.getPropertyValue();
                break;
            }
        }

        liveRateInfo.setSymbols(symbolStr);

        String[] symbols = symbolStr.split(",");
        List<SymbolBidAskValue> listSymbolBidAskValues = historyScOrderDAO.getSymbolBidAskValues(symbols);
        List<SymbolBidAskInfo> listSymbolBidAskInfo = new ArrayList<SymbolBidAskInfo>();
        liveRateInfo.setSymbolBidAskInfo(listSymbolBidAskInfo);

        BigDecimal maxValue = null;
        for (SymbolBidAskValue bidAskValue : listSymbolBidAskValues) {
            BigDecimal bidValue = bidAskValue.getBidValue();
            BigDecimal askValue = bidAskValue.getAskValue();

            if (maxValue == null || maxValue.compareTo(bidValue) < 0) {
                maxValue = bidValue;
            }
            if (maxValue == null || maxValue.compareTo(askValue) < 0) {
                maxValue = askValue;
            }
        }

        for (String symbol : symbols) {
            SymbolBidAskValue symbolBidAskValue = getSymbolBidAskValueInList(symbol, listSymbolBidAskValues);
            SymbolBidAskInfo symbolBidAskInfo = new SymbolBidAskInfo();
            BigDecimal askValue, bidValue, bidRatio, askRatio;
            if (symbolBidAskValue != null) {
                askValue = symbolBidAskValue.getAskValue();
                bidValue = symbolBidAskValue.getBidValue();
                bidRatio = bidValue.multiply(new BigDecimal(100)).divide(maxValue, 0, RoundingMode.DOWN);
                askRatio = askValue.multiply(new BigDecimal(100)).divide(maxValue, 0, RoundingMode.DOWN);
            } else {
                askValue = new BigDecimal(0);
                bidValue = new BigDecimal(0);
                askRatio = new BigDecimal(0);
                bidRatio = new BigDecimal(0);
            }
            symbolBidAskInfo.setSymbol(symbol);
            symbolBidAskInfo.setAskValue(askValue);
            symbolBidAskInfo.setBidValue(bidValue);
            symbolBidAskInfo.setAskRatio(askRatio);
            symbolBidAskInfo.setBidRatio(bidRatio);
            listSymbolBidAskInfo.add(symbolBidAskInfo);
        }
    }


    private SymbolBidAskValue getSymbolBidAskValueInList(String symbol, List<SymbolBidAskValue> listSymbolBidAskValues) {
        for (SymbolBidAskValue symbolBidAskValue : listSymbolBidAskValues) {
            if (symbol.equalsIgnoreCase(symbolBidAskValue.getSymbol())) {
                return symbolBidAskValue;
            }
        }
        return null;
    }


    @Override
    public List<TraderServiceInfo> getListAccountOfTrader(String customerId) {
        return scCustomerServiceDAO.getListAccountOfTrader(customerId);
    }





    @Override
    public CopierCustomerModel getCopierAccountInfo(ScCustomerCopy copier) {
        // TODO Auto-generated method stub
      //  List<ScSummaryTradingCust> listSummary = new ArrayList<ScSummaryTradingCust>();
    	CopierCustomerModel copierCustomerModel = new CopierCustomerModel();
        List <CopierCustomerInfo> listCopierCustomerInfo = new ArrayList<CopierCustomerInfo>();
        String customerId = copier.getCustomer().getCustomerId();
        String accountId = copier.getAccountId();
        String brokerCd = copier.getBrokerCd();
        String username = copier.getCustomer().getUserName(); 
        String totalReturn = " ";        
        String previousBusinessDay = getPreviousBusinessDate(getWlCode());
        listCopierCustomerInfo = scCustomerDAO.getListCustomerCopier(customerId, accountId, brokerCd , previousBusinessDay);
    	if (listCopierCustomerInfo.size() > 0){
    	  Integer accountKind = listCopierCustomerInfo.get(0).getAccountKind();
    	  Integer serviceType = listCopierCustomerInfo.get(0).getServiceType();
    	  if (listCopierCustomerInfo.get(0).getTotalReturn()!=null){
    		  totalReturn = listCopierCustomerInfo.get(0).getTotalReturn().toString();
    	  }
   	  
    	  copierCustomerModel = new CopierCustomerModel(customerId, username, accountId, brokerCd, totalReturn, serviceType, accountKind);    	
    	}
    	//CopierCustomerInfo copierCustomerInfo =  new CopierCustomerInfo(customerId, username, accountId, totalReturn);    	
        return copierCustomerModel;
    }



	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.ISocialManager#getCopyTradeInformation(java.lang.String)
	 */
	public ScCustomerService getCopyTradeAccountInformation(String customerId) {
		ScCustomerService result = null;
		try{
			List<ScCustomerService> list = scCustomerServiceDAO.getCopyTradeAccountInformation(customerId);
			if( list != null && list.size() > 0){
				result = list.get(0);
			}
			
		}catch(RuntimeException e){
			LOG.error("Error when get account type information : "+ customerId);
		}
		return result;
	}


    @Override
	public ScCustomerService getSignalProviderAccInformation(String customerId) {
		ScCustomerService result = null;
		try{
			List<ScCustomerService> list = scCustomerServiceDAO.getSignalProviderAccInformation(customerId);
			if( list != null && list.size() > 0){
				result = list.get(0);
			}
			
		}catch(RuntimeException e){
			LOG.error("Error when get account type information : "+ customerId);
		}
		return result;
	}

	@Override
	public List<FxSymbol> getListSymbolByAccountId(String accountId) {
		List<FxSymbol> listSymbol = null;
		try{
			 listSymbol = fxSymbolDAO.getListSymbolByAccountId(accountId);
		}catch(Exception e){
			LOG.error("Exeception in get list symbol setting by account Id : "+ accountId);
		}
		return listSymbol;
	}



	@Override
	public List<ScOrder> findOrderByTradingAccount(String accountId) {
		List<phn.com.nts.db.entity.ScOrder> result = null;
		try{
			result = historyScOrderDAO.findOrderByTradingAccount(accountId);
		}catch(RuntimeException e){
			LOG.error("Error when find Order By Trading Account : "+accountId);
		}
		return result;
	}
	

	@Override
	public SearchResult<CustomerInfo> getFollowerList(String customerId,
			PagingInfo pagingInfo) {
		// TODO Auto-generated method stub
		SearchResult<CustomerInfo> listFollower = new SearchResult<CustomerInfo>();
		SearchResult<ScCustomer> lstSc = new SearchResult<ScCustomer>();
		lstSc = scCustomerDAO.getFollowerList (customerId , pagingInfo);
		listFollower.setPagingInfo(lstSc.getPagingInfo());
		for (ScCustomer sc:lstSc) {
			listFollower.add(CustomerInfoConverter.toCustomerInfo(sc));			
		}
		return listFollower;
	}

	@Override
	public List<ScSummaryTradingCustInfo> getAccountSummaryTrading(String accountId,String customerId, String brokerCd,String fromDate) {
		List<ScSummaryTradingCustInfo> result = new ArrayList<ScSummaryTradingCustInfo>();
//		try{
//            List<ScSummaryTradingCust> data =  scSummaryTradingCustDAO.getAccountSummaryTrading(accountId, customerId, brokerCd,fromDate);
//            for(ScSummaryTradingCust item : data){
//                ScSummaryTradingCustInfo info = new ScSummaryTradingCustInfo();
//                BeanUtils.copyProperties(item, info, new String[]{"customer", "id", "totalReturn"});
//                BeanUtils.copyProperties(item.getCustomer(), info.getCustomer());
//                BeanUtils.copyProperties(item.getId(), info.getId());
//                // get data from sc_summary_tradings
//                ScSummaryTrading tradingInfo = scSummaryTradingCustDAO.getSummaryTradingInfo(IConstants.RANKING_SUMMARY_TYPE.DAILY, accountId , brokerCd, item.getId().getFrontDate());
//                if(tradingInfo != null){
//                	info.setTotalReturn(tradingInfo.getTotalReturn());
//                }else {
//                	info.setTotalReturn(BigDecimal.ZERO);
//                }
//                result.add(info);
//            }
//		}catch(RuntimeException e){
//			LOG.error("Error when get Summary Trading Info : "+accountId + " " + e.getMessage(), e);
//		}
		
		List<AccountSummaryTradingInfo> data = historyScSummaryTradingCustDAO.getListAccountSummaryTrading(accountId, customerId, brokerCd,fromDate);
		if(data!=null && data.size() > 0){
			for(AccountSummaryTradingInfo accountSummaryTradingInfo : data){
				ScSummaryTradingCustInfo info = new ScSummaryTradingCustInfo();
				ScSummaryTradingCustIdInfo id = new ScSummaryTradingCustIdInfo();
				id.setAccountId(accountSummaryTradingInfo.getAccountId());
				id.setBrokerCd(accountSummaryTradingInfo.getBrokerCd());
				id.setFrontDate(accountSummaryTradingInfo.getFrontDate());
				info.setId(id);
				phn.nts.ams.fe.domain.ScCustomerInfo customerInfo = new phn.nts.ams.fe.domain.ScCustomerInfo();
				customerInfo.setCustomerId(accountSummaryTradingInfo.getCustomerId());
				info.setCustomer(customerInfo);
				BeanUtils.copyProperties(accountSummaryTradingInfo, info);
				result.add(info);
			}
		}
		return result;
	}

	public SummaryTradingInfo getTradingInfoByAccount(String accountId, String brokerCd){
		return historyScSummaryTradingCustDAO.getTradingInfoByAccount(accountId, brokerCd);
	}
	
	@Override
	public ScCustomerService getCustomerServiceInfo(String accountId,String customerId, String brokerCd) {
		ScCustomerService result = null;
		try{
			List<ScCustomerService> list =  scCustomerServiceDAO.getCustomerServiceInfo(accountId, customerId, brokerCd);
			if( list != null && list.size() > 0){
				result = list.get(0);
			}
		}catch(RuntimeException e){
			LOG.error("Error when get Summary Trading Info : "+accountId);
		}
		return result;
	}

	@Override
	public SearchResult<ScCustomerCopy> getListOfCopierByAccount(String accountId, String brokerCd, PagingInfo pageInfo) {
		// TODO Auto-generated method stub
		return scCustomerCopyDAO.getListOfCopierByAccount(accountId, brokerCd , pageInfo);
		
	}

    @Override
    public BigDecimal computeEquityPercentage(String currentCustomerId, String copyCustomerId, BigDecimal investAmount, String copyAccountId, String copyBrokerCd) {
    	List<ScCustomerService> customerServices = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        List<ScCustomerService> copyCustomerServices = scCustomerServiceDAO.findByAccountIdBrokerCd(copyAccountId, copyBrokerCd);
        BigDecimal convertRate = new BigDecimal("1");
        if(customerServices.size() > 0 && copyCustomerServices.size() > 0){
            convertRate = getConvertRateOnFrontRate(customerServices.get(0).getBaseCurrency(), copyCustomerServices.get(0).getBaseCurrency());
            LOG.info("computeEquityPercentage.getConvertRateOnFrontRate(" + customerServices.get(0).getBaseCurrency() + ", " + copyCustomerServices.get(0).getBaseCurrency() + ") = " + convertRate);
        }
        BigDecimal rs=new BigDecimal(0);
        List<ScCustomerService> services = scCustomerServiceDAO.findByAccountIdBrokerCdEnableFlg(copyAccountId, copyBrokerCd, IConstants.ENABLE_FLG.ENABLE);
        BigDecimal equity = services.size() == 0 ? new BigDecimal("0") : services.get(0).getEquity();
        if(equity == null || equity.compareTo(new BigDecimal("0")) == 0){ 
        	LOG.info("equity = "+equity);	
        	return new BigDecimal("0");
        }
    	try {
    		
	        Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG +ITrsConstants.BACKEND_ROLE.WL_CODE_TRS );
	        String mode = mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.COPY_RATE_MODE);
	        if("1".equalsIgnoreCase(mode)){
//	        	rs = new BigDecimal("100").multiply(investAmount).multiply(convertRate).divide(equity, 2, RoundingMode.HALF_UP);
	        	rs = new BigDecimal("100").multiply(investAmount).multiply(convertRate).divide(equity, 2, RoundingMode.DOWN);
	        	LOG.info("computeEquityPercentage = "+rs);	
	        	return rs;
	        }
	
	        LOG.info("GURU MT4Manager.getInstance().getBalanceInfo(copyAccountId) ="+copyAccountId);
			BalanceInfo bi = MT4Manager.getInstance().getBalanceInfo(copyAccountId);
			if(bi!=null){
				LOG.info("bi.getBalance() ="+bi.getBalance());
				LOG.info("bi.getCredit() ="+bi.getCredit());
				LOG.info("bi.getLeverage() ="+bi.getLeverage());
				LOG.info("investAmount ="+investAmount);
				LOG.info("convertRate ="+convertRate);
				LOG.info("customerServices.get(0).getLeverage() ="+customerServices.get(0).getLeverage());
				LOG.info("formular =   (investAmount.divide(new BigDecimal(bi.getBalance() + bi.getCredit()).multiply(convertRate))).multiply((customerServices.get(0).getLeverage()==null?new BigDecimal(0):customerServices.get(0).getLeverage()).divide(new BigDecimal(bi.getLeverage() == null ? 1 : bi.getLeverage())));");
//				rs= (investAmount.divide(new BigDecimal(bi.getBalance() + bi.getCredit()).multiply(convertRate), 2, RoundingMode.HALF_UP)).multiply((customerServices.get(0).getLeverage()==null?new BigDecimal(0):customerServices.get(0).getLeverage()).divide(new BigDecimal(bi.getLeverage() == null ? 1 : bi.getLeverage()),2, RoundingMode.HALF_UP));
//				rs= (investAmount.divide(new BigDecimal(bi.getBalance() + bi.getCredit()).multiply(convertRate))).multiply((customerServices.get(0).getLeverage()==null?new BigDecimal(0):customerServices.get(0).getLeverage()).divide(new BigDecimal(bi.getLeverage() == null ? 1 : bi.getLeverage())));
				BigDecimal up = (new BigDecimal(100)).multiply(investAmount.multiply(customerServices.get(0).getLeverage()==null?new BigDecimal(0):customerServices.get(0).getLeverage()));
				BigDecimal bl=new BigDecimal(bi.getBalance() + bi.getCredit()).multiply(convertRate).multiply(new BigDecimal(bi.getLeverage() == null ? 1 : bi.getLeverage()));
				rs = up.divide(bl,2,RoundingMode.DOWN);
				LOG.info("computeEquityPercentage = "+rs);	
				return rs;
			}else{
				LOG.info("get BalanceInfo = marginlevel of MT4 null ");	
				return new BigDecimal("0");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return new BigDecimal("0");
		}
        }

    @Override
    public BigDecimal computeInvestAmount(String currentCustomerId, String copyCustomerId, BigDecimal equityPercentage, String copyAccountId, String copyBrokerCd) {
        List<ScCustomerService> customerServices = scCustomerServiceDAO.findByCustomerIdAndServiceType(currentCustomerId, IConstants.SERVICES_TYPE.SOCIAL_FX);
        List<ScCustomerService> copyCustomerServices = scCustomerServiceDAO.findByAccountIdBrokerCd(copyAccountId, copyBrokerCd);
        BigDecimal convertRate = new BigDecimal("1");
        if(customerServices.size() > 0 && copyCustomerServices.size() > 0){
            convertRate = getConvertRateOnFrontRate(customerServices.get(0).getBaseCurrency(), copyCustomerServices.get(0).getBaseCurrency());
            LOG.info("computeInvestAmount.getConvertRateOnFrontRate(" + customerServices.get(0).getBaseCurrency() + ", " + copyCustomerServices.get(0).getBaseCurrency() + ") = " + convertRate);
        }
        List<ScCustomerService> services = scCustomerServiceDAO.findByAccountIdBrokerCdEnableFlg(copyAccountId, copyBrokerCd, IConstants.ENABLE_FLG.ENABLE);
        BigDecimal equity = services.size() == 0 ? new BigDecimal("0") : services.get(0).getEquity();
        if(equity == null || equity.compareTo(new BigDecimal("0")) == 0) return new BigDecimal("0");
        return equityPercentage.divide(new BigDecimal("100")).multiply(equity).divide(convertRate, 2, RoundingMode.HALF_UP);
    }

    @Override
    public List<CopyTradeItemInfo> getCopyItemDetails(String currentCustomerId, String customerId) {
    	String previousBizDate = getPreviousBusinessDate(ITrsConstants.BACKEND_ROLE.WL_CODE_TRS);
        return scCustomerCopyDAO.getCopyItemDetails(currentCustomerId, customerId, previousBizDate);
    }

    @Override
    public Map<String, CountryInfo> getListCountryInfo() {
        Map<String, CountryInfo> listCountry = new HashMap<String, CountryInfo>();;
        List<AmsSysCountry> listCountries = amsSysCountryDAO.findByActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
        if(listCountries != null && listCountries.size() > 0) {
            for(AmsSysCountry amsSysCountry : listCountries) {
                CountryInfo countryInfo = new CountryInfo();
                BeanUtils.copyProperties(amsSysCountry, countryInfo);
                listCountry.put(amsSysCountry.getCountryId().toString(), countryInfo);
            }
        }

        return listCountry;
    }

    public IAmsSysCountryDAO<AmsSysCountry> getAmsSysCountryDAO() {
        return amsSysCountryDAO;
    }

    public void setAmsSysCountryDAO(IAmsSysCountryDAO<AmsSysCountry> amsSysCountryDAO) {
        this.amsSysCountryDAO = amsSysCountryDAO;
    }

	@Override
	public Integer isEnabledFeedBoard(String accountId, String brokerCd) {
		Integer result = 0;
		try{
//			ScCustomer customer = scCustomerDAO.getScCustomer(customerId);
//			if( customer != null){
//				result = customer.getWriteMyBoardFlg();
//			}
			// check signal ON/OFF
			ScCustomerService customerService = null;
			List<ScCustomerService> list = scCustomerServiceDAO.findByAccountIdBrokerCdEnableFlg(accountId, brokerCd, IConstants.ACTIVE_FLG.ACTIVE);
			if(list.size()>0){
				customerService = list.get(0);
			}
			if(customerService != null){
				result = customerService.getEnableFlg();
			}
		}catch(RuntimeException e){
			LOG.error("Error when execute isEnabledFeedBoard :" + e.getMessage(), e);
		}
		return result;
	}

	@Override
	public String calculateToTalTraded(String customerId) {
		String result = null;
		try{
			List<AmsCustomerSurvey> list = amsCustomerSurveyDAO.findById(customerId);
			if(list != null && list.size() > 0){
				result = list.get(0).getFirstExecutionDate();
			}
		}catch(RuntimeException e){
			LOG.error("calculate Total traded : "+ e);
		}
		return result;
	}

	@Override
	public List<ScOrderInfo> getListOrderByAccountId(String accountId,int page,int index) {
		List<ScOrderInfo> result = null;
		try{
			result = historyScOrderDAO.getListOrderByAccountId(accountId, page, index);
		}catch(RuntimeException e){
			LOG.error("Error when get List Order By Account Id :"+ e);
		}
		return result;
	}

	@Override
	public HashMap<String,FxSymbol> findAllFxSymbol() {
		HashMap<String,FxSymbol> result = null;
		List<FxSymbol> list = null;
		try{
			list = fxSymbolDAO.findAll();
			if(list!= null && list.size() >  0){
				result = new HashMap<String, FxSymbol>();
				int length = list.size();
				for(int i = 0 ; i < length; ++i){
					result.put(list.get(i).getSymbolCd(),list.get(i));
				}
			}
		}catch(RuntimeException e ){
			LOG.error("Error when get all Fx Symbol :" + e);
		}
		return result;
	}

    @Override
    public void loadCustomerGuidelineData(String customerId, CustomerGuidelineModel guidelineModel) {
        List<AmsCustomerService> services = amsCustomerServiceDAO.getListCustomerServicesInfo(customerId, IConstants.SERVICES_TYPE.FX);
        guidelineModel.setCompleted(false);
        if(services.size() > 0){
            Integer customerServiceStatus = services.get(0).getCustomerServiceStatus();
            if(IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER.equals(customerServiceStatus) || IConstants.CUSTOMER_SERVIVES_STATUS.CERTIFICATED_DOCSWAITING.equals(customerServiceStatus) || IConstants.CUSTOMER_SERVIVES_STATUS.ACCOUNT_OPEN_REQUESTING.equals(customerServiceStatus)){
                guidelineModel.setStep(1);
            } else if(IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(customerServiceStatus)){
                guidelineModel.setStep(2);
            } else {
                List<ScCustomerFollow> followList = scCustomerFollowDAO.findByCustomerId(customerId);
                if(followList.size() == 0) guidelineModel.setStep(3);
                else guidelineModel.setCompleted(true);
            }
        }
    }


    public IAmsWhitelabelCalendarDAO<AmsWhitelabelCalendar> getAmsWhitelabelCalendarDAO() {
        return amsWhitelabelCalendarDAO;
    }

    public void setAmsWhitelabelCalendarDAO(IAmsWhitelabelCalendarDAO<AmsWhitelabelCalendar> amsWhitelabelCalendarDAO) {
        this.amsWhitelabelCalendarDAO = amsWhitelabelCalendarDAO;
    }

	@Override
	public ScCustomer findUserByUserName(String userName) {
		ScCustomer result = null;
		try{
			List<ScCustomer> list = scCustomerDAO.findByUserName(userName);
			if(list != null && list.size() > 0){
				result = list.get(0);
			}
		}catch(RuntimeException e){
			LOG.error("ERROR when get customer by user name :"+e);
		}
		return result;
	}
	public ScCustomer findUserByCustomerId(String customerId) {
		ScCustomer result = null;
		try{
			List<ScCustomer> list = scCustomerDAO.findCustomerById(customerId);
			if(list != null && list.size() > 0){
				result = list.get(0);
			}
		}catch(RuntimeException e){
			LOG.error("ERROR when get customer by user name :"+e);
		}
		return result;
	}
	public boolean save(ScCustomer sc){
		try {
			scCustomerDAO.merge(sc);
			ScCustomerInfo scinfo= new ScCustomerInfo();
			scinfo.setCustomerId(sc.getCustomerId());
			scinfo.setUserName(sc.getUserName());
//			JMSContext jms = JMSContext.getInstance();
//			
//			jms.send("queue.ScTcFeedRequest", scinfo);
			 jmsRealSender.sendQueue("queue.ScTcFeedRequest", scinfo,false);
			return true;
		} catch (RuntimeException e) {
			LOG.error("ERROR when save ScCustomer :"+e);
			return false;
		}
	}
	public boolean updateFirsLoginFlag(String customerId){
		AmsCustomerSurvey acs = null;
		try{
			List<AmsCustomerSurvey> lst = scCustomerDAO.findAmsCustomerSurveyById(customerId);
			if(lst==null||lst.size()==0){
				return false;
			}
			acs = lst.get(0);
			acs.setFirstLoginFlg(1);
			amsCustomerSurveyDAO.attachDirty(acs);
		
			return true;
		} catch (RuntimeException e) {
//			e.printStackTrace();
			LOG.error("ERROR when save AMS_CUSTOMER_SURVEY :"+e.getMessage(), e);
			return false;
		}
	}
	/**
	 * Get lastest agreement information
	 * 
	 * @param customerId 
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 11, 2013
	 */
	@Override
	public List<AmsMessage> getAgreementInfo(String customerId, Integer messageType, Integer offSet, Integer pageSize, 
			Integer readFlg, Integer serviceType) throws Exception{
		Integer fxSubgroupId = 0;
		Integer boSubgroupId = 0;
		Integer socialSubgroupId = 0;
		Integer ntdFXSubgroupId = 0;
			
		//Get subgroup of other service
		LOG.info("Get List Agreement with customerId="+customerId+", messageType="+messageType);
		List<AmsCustomerService> amsCustomerServiceLst = new ArrayList<AmsCustomerService>();
		amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
		
		for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
			if(amsCustomerService.getServiceType().compareTo(ServiceType.BO_VALUE) == 0){
				boSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
			}else if(amsCustomerService.getServiceType().compareTo(ServiceType.FX_VALUE) == 0){
				fxSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
			}else if(amsCustomerService.getServiceType().compareTo(ServiceType.SC_VALUE) == 0){
				socialSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
			}else if(amsCustomerService.getServiceType().compareTo(ServiceType.NTD_FX_VALUE) == 0){
				ntdFXSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
			}
		}
		
		//1.2. Get News notification
		if(MessageType.REGULAR_NEWS_VALUE == messageType){
			return amsMessageDAO.getRegularNewsMessage(customerId, messageType, fxSubgroupId, boSubgroupId, socialSubgroupId, 
					ntdFXSubgroupId, offSet, pageSize, readFlg, serviceType, false);
		}else if (MessageType.RE_AGREEMENT_VALUE == messageType){
			return amsMessageDAO.getAgreementMessage(customerId, messageType);
		}
		return null;
	}
	
	@Override
	public List<AmsMessage> getAgreementInfo(AmsCustomerNewsResponse.Builder amsCustomerNewsResponse, String customerId, Integer messageType, Integer offSet, Integer pageSize, 
			Integer readFlg, Integer serviceType, Integer confirmFlg, Integer messageKind) throws Exception{
		List<AmsMessage> lstAmsMessage = null;
		Integer amsSubgroupId = 0;
		boolean checkStartDate = true; //Need check start Date
		boolean isGetLast = true; //Need get last re-agreement news
		
		//Get amsSubgroupId
		AmsCustomer amsCustomer = amsCustomerDAO.getCustomerInfo(customerId);
		if(amsCustomer != null && amsCustomer.getAmsGroup() != null)
			amsSubgroupId = amsCustomer.getAmsGroup().getGroupId();
			
		//Get subgroup of other service
		List<AmsCustomerService> amsCustomerServiceLst = new ArrayList<AmsCustomerService>();
		amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
		
		Map<String, Integer> mapSubGroupId = new HashMap<String, Integer>();
		mapSubGroupId.put("amsSubgroupId", amsSubgroupId);
		
		for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
			mapSubGroupId.put(amsCustomerService.getCustomerServiceId(), amsCustomerService.getAmsSubGroup().getSubGroupId());
		}
		
		int totalRecord = 0;
		
		//1.2. Get News notification
		if(MessageType.REGULAR_NEWS_VALUE == messageType){
			totalRecord = amsMessageDAO.getRegularNewsMessageCount(customerId, messageType, mapSubGroupId, readFlg, serviceType, messageKind);
			
			lstAmsMessage = amsMessageDAO.getRegularNewsMessage(customerId, messageType, mapSubGroupId, offSet, 
					pageSize, readFlg, serviceType, false, messageKind);
			
		} else if (MessageType.RE_AGREEMENT_VALUE == messageType){
			lstAmsMessage = amsMessageDAO.getAgreementMessage(customerId, messageType, readFlg, confirmFlg, isGetLast, checkStartDate);
			totalRecord = lstAmsMessage != null ? lstAmsMessage.size() : 0;
			
		} else if (MessageType.AGREEMENT_NEWS_VALUE == messageType){
			lstAmsMessage = amsMessageDAO.getAgreementNewsMessage(customerId, messageType, mapSubGroupId, offSet, 
					pageSize, readFlg, serviceType, false, messageKind, confirmFlg, checkStartDate);
			totalRecord = lstAmsMessage != null ? lstAmsMessage.size() : 0;
			
		} else if (MessageType.REGULAR_AND_READ_AGREEMENT_NEWS_VALUE == messageType){
			lstAmsMessage = amsMessageDAO.getRegularNewsAndAgreementReadOrExpiredMessage(customerId, MessageType.REGULAR_AND_READ_AGREEMENT_NEWS_VALUE, mapSubGroupId, offSet, 
					pageSize, readFlg, serviceType, false, messageKind, confirmFlg);
					
			totalRecord = amsMessageDAO.getRegularNewsAndAgreementReadOrExpiredMessageCount(customerId, 
					MessageType.REGULAR_AND_READ_AGREEMENT_NEWS_VALUE, mapSubGroupId, readFlg, serviceType, messageKind, confirmFlg);
			
		} else if (MessageType.RE_AGREEMENT_AND_AGREEMENT_NEWS_VALUE == messageType){
			List<AmsMessage> reagreementNews = amsMessageDAO.getAgreementMessage(customerId, MessageType.RE_AGREEMENT_VALUE, readFlg, confirmFlg, isGetLast, checkStartDate);
			
			List<AmsMessage> agreementNews = amsMessageDAO.getAgreementNewsMessage(customerId, MessageType.AGREEMENT_NEWS_VALUE, mapSubGroupId, null, 
					null, readFlg, serviceType, false, messageKind, confirmFlg, checkStartDate);
			LOG.info("Loaded reAgreementNews: " + reagreementNews.size() + ", agreementNews: " + agreementNews.size());
			
			reagreementNews.addAll(agreementNews);
			lstAmsMessage = reagreementNews;
			totalRecord = lstAmsMessage != null ? lstAmsMessage.size() : 0;
		}
		
		amsCustomerNewsResponse.setTotalRecords(totalRecord);
		return lstAmsMessage;
	}
	
	
	public AmsCustomerNews getAgreementInfo(String customerId) throws Exception{
		Integer readFlg = ReadFlag.UNREAD.getNumber();
		Integer confirmFlg = ConfirmFlag.UNCONFIRMED.getNumber();
		Integer serviceType = null;
		Integer messageKind = null;
		boolean checkStartDate = false; //Load all, no need check start date
		boolean isGetLast = false; //Need get all re-agreement news
		
		Integer amsSubgroupId = 0;
		//Get amsSubgroupId
		AmsCustomer amsCustomer = amsCustomerDAO.getCustomerInfo(customerId);
		if(amsCustomer != null && amsCustomer.getAmsGroup() != null)
			amsSubgroupId = amsCustomer.getAmsGroup().getGroupId();
			
		//Get subgroup of other service
		List<AmsCustomerService> amsCustomerServiceLst = new ArrayList<AmsCustomerService>();
		amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
		
		Map<String, Integer> mapSubGroupId = new HashMap<String, Integer>();
		mapSubGroupId.put("amsSubgroupId", amsSubgroupId);
		
		for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
			mapSubGroupId.put(amsCustomerService.getCustomerServiceId(), amsCustomerService.getAmsSubGroup().getSubGroupId());
		}
		
		//Get Agreement/ReAgreement news from DB
		AmsCustomerNews agreementNews = new AmsCustomerNews();
		agreementNews.setCustomerId(customerId);
		
		//RE_AGREEMENT
		List<AmsMessage> reAgreementNewsLst = amsMessageDAO.getAgreementMessage(customerId, MessageType.RE_AGREEMENT_VALUE, readFlg, confirmFlg, isGetLast, checkStartDate);
		if(reAgreementNewsLst != null && reAgreementNewsLst.size() > 0) {
			for (AmsMessage amsMessage : reAgreementNewsLst) {
				agreementNews.getLstReAgreement().add(amsMessage.getMessageId());
			}
		}
		
		//AGREEMENT_NEWS
		List<AmsMessage> agreementNewsLst = amsMessageDAO.getAgreementNewsMessage(customerId, MessageType.AGREEMENT_NEWS_VALUE, mapSubGroupId, null,
				null, readFlg, serviceType, false, messageKind, confirmFlg, checkStartDate);
		if(agreementNewsLst != null && agreementNewsLst.size() > 0) {
			for (AmsMessage amsMessage : agreementNewsLst) {
				agreementNews.getLstAgreement().add(amsMessage.getMessageId());
			}
		}
		
		return agreementNews;
	}
	
	@Override
	public int getAgreementInfoCount(String customerId, Integer messageType, Integer readFlg, Integer serviceType) throws Exception{
		
		//1.2. Get News notification
		if(MessageType.REGULAR_NEWS_VALUE == messageType) {
			Integer fxSubgroupId = 0;
			Integer boSubgroupId = 0;
			Integer socialSubgroupId = 0;
			Integer ntdFXSubgroupId = 0;
			LOG.info("Get List Agreement with customerId = " + customerId + ", messageType = " + messageType);
			List<AmsCustomerService> amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
			
			//1.1. Get FX sub group ID, BO sub group ID , Social sub group ID of [CustomerId]
			for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
				if(amsCustomerService.getServiceType().compareTo(ServiceType.BO_VALUE) == 0){
					boSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
				}else if(amsCustomerService.getServiceType().compareTo(ServiceType.FX_VALUE) == 0){
					fxSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
				}else if(amsCustomerService.getServiceType().compareTo(ServiceType.SC_VALUE) == 0){
					socialSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
				}else if(amsCustomerService.getServiceType().compareTo(ServiceType.NTD_FX_VALUE) == 0){
					ntdFXSubgroupId = amsCustomerService.getAmsSubGroup().getSubGroupId();
				}
			}
			
			return amsMessageDAO.getRegularNewsMessage(customerId, messageType, fxSubgroupId, boSubgroupId, socialSubgroupId, 
					ntdFXSubgroupId, null, null, readFlg, serviceType, true).size();
		}
		
		return 0;
	}
	
	
	/**
	 * Get all agreement information
	 * 
	 * @param customerId 
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 11, 2013
	 */
	@Override
	public List<AmsMessage> getAllAgreementInfo(String customerId, Integer fxSubgroupId, Integer boSubgroupId, Integer socialSubgroupId) {
		return amsMessageDAO.getAllMessage(customerId,fxSubgroupId,boSubgroupId,socialSubgroupId);
	}

	
	@Override
	public ConfirmAgreementResult agreementConfirm(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest){
		ConfirmAgreementResult result = ConfirmAgreementResult.READED;
		try {
			if(amsCustomerNewsUpdateRequest.getNewsInfoList() == null || amsCustomerNewsUpdateRequest.getNewsInfoCount() < 1){
				throw new Exception("Invalid message type is null --> please check message type input");
			}
				
			for (AmsNewsInfo amsNewsInfo : amsCustomerNewsUpdateRequest.getNewsInfoList()) {
				if(amsNewsInfo.hasConfirmFlg() && ConfirmFlag.CONFIRMED == amsNewsInfo.getConfirmFlg() && 
						(MessageType.RE_AGREEMENT == amsNewsInfo.getMessageType() 
						|| MessageType.AGREEMENT_NEWS == amsNewsInfo.getMessageType())) {
					
					//1. If ([MessageType] = MessageType.RE_AGREEMENT OR MessageType.AGREEMENT_NEWS) AND [ConfirmFlg] = 1
					agreeConfirm(amsCustomerNewsUpdateRequest.getCustomerId(), amsNewsInfo);
					result = ConfirmAgreementResult.CONFIRMED;
				} else if(amsNewsInfo.hasReadFlg() && ReadFlag.READ == amsNewsInfo.getReadFlg()
						&& amsNewsInfo.hasConfirmFlg() && ConfirmFlag.UNCONFIRMED == amsNewsInfo.getConfirmFlg()
						&& (MessageType.REGULAR_NEWS == amsNewsInfo.getMessageType() || MessageType.AGREEMENT_NEWS == amsNewsInfo.getMessageType())) {
					
					//2. If ([MessageType] = MesageType.REGULAR_NEWS OR MesageType.AGREEMENT_NEWS) AND [ReadFlg] = 1
					agreementRead(amsCustomerNewsUpdateRequest.getCustomerId(), amsNewsInfo);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			result = ConfirmAgreementResult.FAILED;
		}
		return result;
	}
	
	/*private List<AmsMessage> getListAgreement(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest) throws Exception{
		List<AmsMessage> result = new ArrayList<AmsMessage>();
		AmsNewsInfo amsNewsInfo = amsCustomerNewsUpdateRequest.getNewsInfo();
		if(MessageType.AGREEMENT_NEWS.getNumber() == amsCustomerNewsUpdateRequest.getNewsInfo().getMessageType().getNumber()){
			result = getAgreementInfo(amsCustomerNewsUpdateRequest.getCustomerId(), amsNewsInfo.getMessageType().getNumber(),
					null, null, amsNewsInfo.getReadFlg().getNumber(), amsNewsInfo.getServiceType().getNumber());
		}else if(MessageType.REGULAR_NEWS.getNumber() == amsCustomerNewsUpdateRequest.getNewsInfo().getMessageType().getNumber()){
			AmsMessage amsMessage = new AmsMessage();
			amsMessage.setMessageId(Integer.valueOf(amsCustomerNewsUpdateRequest.getNewsInfo().getMessageId()));
			amsMessage.setServiceType(amsCustomerNewsUpdateRequest.getNewsInfo().getServiceType().getNumber());
			result.add(amsMessage);
		}
		return result;
	}*/
	
	/**
	 * Agree agreementã€€
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 12, 2013
	 */
	@Override
	public void agreeConfirm(String customerId, AmsNewsInfo amsNewInfo) throws Exception {
		if(amsNewInfo == null){
			LOG.info("amsNewInfo is null");
			return;
		}
		
		if(!StringUtils.isNotBlank(customerId)){
			LOG.info("Customer is null");
			return;
		}
		
		String messageId = amsNewInfo.getMessageId();
		if(!StringUtils.isNotBlank(messageId)){
			LOG.info("messageId is null");
			return;
		}
		
		if(!MessageType.RE_AGREEMENT.equals(amsNewInfo.getMessageType())){
			//1. Update AMS_MESSAGE_READ_TRACE
			AmsMessageReadTraceId id = new AmsMessageReadTraceId();
			id.setCustomerId(customerId);
			id.setMessageId(Integer.valueOf(messageId));
			AmsMessageReadTrace messageReadTrace = amsMessageReadTraceDAO.findById(AmsMessageReadTrace.class, id);
			if(messageReadTrace != null){
				LOG.info(String.format("[start] update AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
				messageReadTrace.setReadFlg(1);
				messageReadTrace.setConfirmFlg(1);
				messageReadTrace.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsMessageReadTraceDAO.merge(messageReadTrace);
				LOG.info(String.format("[end] update AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
			}else{
				LOG.info(String.format("[start] insert AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
				messageReadTrace = new AmsMessageReadTrace();
				messageReadTrace.setId(id);
				messageReadTrace.setReadFlg(1);
				messageReadTrace.setConfirmFlg(1);
				messageReadTrace.setActiveFlg(1);
				messageReadTrace.setInputDate(new Timestamp(System.currentTimeMillis()));
				messageReadTrace.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsMessageReadTraceDAO.attachDirty(messageReadTrace);
				LOG.info(String.format("[end] insert AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
			}
		} else{
			//update ams_read_trace
			Integer updateCount = amsMessageReadTraceDAO.updateAmsReadTraceForReAgreement(customerId, amsNewInfo.getMessageType().getNumber());
			LOG.info("Update AmsMsgReadTrace success " + updateCount);
			if(updateCount < 1){
				saveAmsMsgReadTraceList(customerId, amsNewInfo.getMessageType().getNumber());
			}
		}
		
		//Update AMS_CUSTOMER_SERVICE for customerId
		if(MessageType.RE_AGREEMENT.equals(amsNewInfo.getMessageType()) || MessageType.AGREEMENT_NEWS.equals(amsNewInfo.getMessageType())){
			List<AmsCustomerService> list = amsCustomerServiceDAO.findByCustomerId(customerId);
			LOG.info("START:: UPDATE agreement of All Service = 1");
			for (AmsCustomerService amsCustomerService : list) {
				amsCustomerService.setAgreementFlg(1);
				amsCustomerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerServiceDAO.merge(amsCustomerService);		
			}
			LOG.info("END:: UPDATE agreement of All Service = 1");
			
			// Update AmsCustomerTrace
			List<AmsCustomerService> amsCustomerServiceLst = new ArrayList<AmsCustomerService>();
			amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
			List<Integer> serviceTypes = new ArrayList<Integer>();
			serviceTypes.add(IConstants.SERVICES_TYPE.AMS);
			for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
				serviceTypes.add(amsCustomerService.getServiceType());
			}
			
			AmsMessage amsMessage = amsMessageDAO.findById(AmsMessage.class, Integer.valueOf(messageId));
			AmsCustomer customer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
			
			for (Integer serviceType : serviceTypes) {
				LOG.info("START UPDATE AmsCustomerTrace WITH SERVICE_TYPE = "+serviceType);
				AmsCustomerTrace customerTrace = new AmsCustomerTrace();
				customerTrace.setServiceType(serviceType);
				if(MessageType.RE_AGREEMENT == amsNewInfo.getMessageType())
					customerTrace.setReason("Accept Re-Agreement");
				else
					customerTrace.setReason("Accept Agreement News");
				customerTrace.setValue1("AGREEMENT_FLG=0");
				customerTrace.setValue2("AGREEMENT_FLG=1");
				customerTrace.setNote1(amsMessage.getMessageTitle());
				customerTrace.setNote2(DateUtil.toString(amsMessage.getStartDate(), DateUtil.PATTERN_YYYMMDD_HHMMSS_FULL));
				customerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
				customerTrace.setAmsCustomer(customer);
				customerTrace.setActiveFlg(1);
				
				amsCustomerTraceDAO.attachDirty(customerTrace);
			}
			LOG.info(" END UPDATE AmsCustomerTrace ");
		}  
	}
	
	@Override
	public void saveAmsMsgReadTraceList(String customerId, Integer messageType) throws Exception {
		AmsMessageReadTrace messageReadTrace;
		AmsMessageReadTraceId id;
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		List<AmsMessageReadTraceId> amsMsgReadList = amsMessageReadTraceDAO.getListAmsReadTraceForReAgreement(customerId, messageType);
		for (AmsMessageReadTraceId traceId : amsMsgReadList) {
			id = new AmsMessageReadTraceId();
			id.setCustomerId(customerId);
			id.setMessageId(Integer.valueOf(traceId.getMessageId()));
			
			messageReadTrace = new AmsMessageReadTrace();
			messageReadTrace.setId(id);
			messageReadTrace.setReadFlg(1);
			messageReadTrace.setConfirmFlg(1);
			messageReadTrace.setActiveFlg(1);
			messageReadTrace.setInputDate(currentTime);
			messageReadTrace.setUpdateDate(currentTime);
			amsMessageReadTraceDAO.attachDirty(messageReadTrace);
		}
	}
	
	@Override
	public void agreementRead(String customerId, AmsNewsInfo amsNewInfo) throws Exception {
		if(amsNewInfo == null){
			LOG.info("amsNewInfo is null");
			return;
		}
		
		if(!StringUtils.isNotBlank(customerId)){
			LOG.info("Customer is null");
			return;
		}
		
		String messageId = amsNewInfo.getMessageId();
		if(!StringUtils.isNotBlank(messageId)){
			LOG.info("messageId is null");
			return;
		}
		
		//1. Update AMS_MESSAGE_READ_TRACE
		AmsMessageReadTraceId id = new AmsMessageReadTraceId();
		id.setCustomerId(customerId);
		id.setMessageId(Integer.valueOf(messageId));
		
		AmsMessageReadTrace messageReadTrace = amsMessageReadTraceDAO.findById(AmsMessageReadTrace.class, id);
		if(messageReadTrace != null){
			LOG.info(String.format("[start] update AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
			messageReadTrace.setReadFlg(1);
			messageReadTrace.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			amsMessageReadTraceDAO.merge(messageReadTrace);
			LOG.info(String.format("[end] update AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
		}else{
			LOG.info(String.format("[start] insert AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
			messageReadTrace = new AmsMessageReadTrace();
			messageReadTrace.setId(id);
			messageReadTrace.setReadFlg(1);
			messageReadTrace.setActiveFlg(1);
			messageReadTrace.setInputDate(new Timestamp(System.currentTimeMillis()));
			messageReadTrace.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			amsMessageReadTraceDAO.attachDirty(messageReadTrace);
			LOG.info(String.format("[end] insert AmsMessageReadTrace, customerId: %s, messageId: %s", customerId, messageId));
		}
	}
	
	/**
	 * Disagree agreementã€€
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 12, 2013
	 */
	@Override
	public int disagreeConfirm(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest) {
		String customerId = amsCustomerNewsUpdateRequest.getCustomerId();
		if(!StringUtils.isNotBlank(customerId)){
			LOG.info("customerId is null");
			return 0;
		}
		
		try {
			List<AmsCustomerService> amsCustomerServiceLst = new ArrayList<AmsCustomerService>();
			amsCustomerServiceLst = amsCustomerServiceDAO.getListCustomerServices(customerId);
			List<Integer> serviceTypes = new ArrayList<Integer>();
			for (AmsCustomerService amsCustomerService : amsCustomerServiceLst) {
				serviceTypes.add(amsCustomerService.getServiceType());
			}
			
			AmsCustomer customer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
			for (int serviceId : serviceTypes) {
				AmsCustomerTrace customerTrace = new AmsCustomerTrace();
				customerTrace.setServiceType(serviceId);
				customerTrace.setReason("Disagree new agreement");
				customerTrace.setNote1("Customer press Esc in keyboard or Close window/browser");
				customerTrace.setValue1("AGREEMENT_FLG=0");
				customerTrace.setValue2("AGREEMENT_FLG=0");
				customerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
				customerTrace.setAmsCustomer(customer);
				customerTrace.setActiveFlg(1);
				amsCustomerTraceDAO.attachDirty(customerTrace);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
		return 1;
	}
	
	public List<LeaderBoardCustomer> convertTraderToCustomer(List<RankingTraderInfo> info, String customerId){
		List<LeaderBoardCustomer> listCustomer = new ArrayList<LeaderBoardCustomer>();
		LeaderBoardCustomer cusItem = null;
		// [TRS2.0-Dev-DuyenNT]Apr 1, 2014 - Fix bug: SQL Injection - Start JIRA #TRSPT-968
		List<String> listFollow = new ArrayList<String>();
		for(RankingTraderInfo infoItem : info){
			cusItem = new LeaderBoardCustomer();
			cusItem.setAccountId(infoItem.getAccountId());
			cusItem.setBrokerCd(infoItem.getBrokerCd());
			cusItem.setCustomerId(infoItem.getCustomerId());
			cusItem.setReturnValue(infoItem.getReturnValue());
			cusItem.setUsername(infoItem.getUserName());
			cusItem.setFollowFlg(0);
			cusItem.setDisable(0);
			listCustomer.add(cusItem);
			listFollow.add(infoItem.getCustomerId());
		}

		List<String> listFollowingCus = scCustomerDAO.getListFollowingCus(customerId, listFollow);
		// [TRS2.0-Dev-DuyenNT]Apr 1, 2014 - Fix bug: SQL Injection - End
		for(LeaderBoardCustomer cusItem2 : listCustomer){
			if(listFollowingCus.contains(cusItem2.getCustomerId())){
				cusItem2.setFollowFlg(1);
			}
		}
		
		return listCustomer;
	}
	public String validateOrder(Double amount, String orderTicket,String customerId){
        LOG.info("[start] validateOrder for customer: " + customerId + " with amount=" + amount + " orderTicket=" + orderTicket);
        String result = "";
		AmsCustomerService service = amsCustomerServiceDAO.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.COPY_TRADE);
		if(service == null) {
            LOG.info("Can't find copy trade account of customer: " + customerId);
            result = "fail";
        } else {
            List<AmsCashBalance> acb = amsCashBalanceDAO.findByCustomerServiceId(service.getCustomerServiceId());
            for(AmsCashBalance balance : acb){
                LOG.info((balance.getAmsCustomerService() == null ? "" : ("ServiceId=" +  balance.getAmsCustomerService().getCustomerServiceId() + " Service Type=" + balance.getAmsCustomerService().getServiceType())) + " BALANCE " + balance.getCashBalance());
            }
            if(acb.get(0).getCashBalance() == null || acb.get(0).getCashBalance() <= 0){
                result = "MSG_SC_062";
            } else{
                int sco = historyScOrderDAO.getOrderClosed(orderTicket);
                LOG.info("Customer=" + customerId + " with orderTicket=" + orderTicket + " has closed " + sco + " orders");
                if(sco != 0){
                    result = "MSG_SC_082";
                }
            }
        }
        LOG.info("[end] validateOrder for customer: " + customerId + " with amount=" + amount + " orderTicket=" + orderTicket);
		return result;
	}
	
	public AmsCustomer findCustomerById(String customerId) {
		AmsCustomer amsCustomer = null;
		try{
			amsCustomer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
		}catch(Exception e){
			LOG.error(e.getMessage(), e);
		}
		return amsCustomer;
	}
	
	
	@Override
	public String getFrontDate() {
		SysAppDate currentBusinessDate = sysAppDateDAO.getCurrentBusinessDay();
		return currentBusinessDate.getId().getFrontDate();
	}
	
	@Override
	public boolean isExistedAccountId(String accountId){
		List<ScCustomerService> listServices = scCustomerServiceDAO.findByAccountIdEnableFlg(accountId, IConstants.ENABLE_FLG.ENABLE);
		if(listServices.size() > 0){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isExistedCustomerId(String id){
		List<ScCustomerService> listServices = scCustomerServiceDAO.findByCustomerIdEnableFlg(id, IConstants.ENABLE_FLG.ENABLE);
		if(listServices.size() > 0){
			return true;
		} else {
			return false;
		}
	}
	
	public List<FxSymbol> getTradingStatisticInfo(String accountId){
		List<FxSymbol> listSymbol = new ArrayList<FxSymbol>();
		List<ChartSymbol> listChartInfo = historyScOrderDAO.getOrderbyTradingAccount(accountId);
		if(listChartInfo != null && listChartInfo.size() > 0) {
			for(ChartSymbol chartInfo : listChartInfo){
				FxSymbol symbol = new FxSymbol();
				BeanUtils.copyProperties(chartInfo, symbol);
				listSymbol.add(symbol);
			}
		}
		return listSymbol;
	}
}
