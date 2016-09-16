package phn.nts.ams.fe.model;

import phn.com.nts.ams.web.condition.RankingSearchCondition;
import phn.com.nts.ams.web.condition.RankingTraderInfo;
import phn.nts.ams.fe.common.CustomerRankingCache;
import phn.nts.ams.fe.domain.CountryInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/7/13 10:23 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class RankingModel extends BaseSocialModel {

    private String userName;
    private String winRatio;
    private String minTrades;
    private String minWinRatio;
    private String maxWinRatio;
    private String returnRatio;
    private String minReturn;
    private String maxReturn;
    private String pips;
    private String averagePips;
    private String maxDrawdown;
    private String minMaxDrawdown;
    private String maxMaxDrawdown;
    private String averageMargin;
    private String minAvgMargin;
    private String maxAvgMargin;
    private List<String> listServiceType;
    private Boolean last30Days;
    private String period;
    private List<RankingTraderInfo> rankingDetails;
    private RankingSearchCondition condition;
    private Integer startRankNumber;
    private Map<String, CountryInfo> mapCountry;

    public Integer getCopierNo(String accountId, String brokerCd){
        return CustomerRankingCache.getInstance().getCopierNo(accountId, brokerCd);
    }

    public Integer getFollowerNo(String customerId){
        return CustomerRankingCache.getInstance().getFollowerNo(customerId);
    }

    private Map<String, String> mapServiceTypes = new TreeMap<String, String>();
    
    public Map<String, String> getMapServiceTypes() {
		return mapServiceTypes;
	}

	public void setMapServiceTypes(Map<String, String> mapServiceTypes) {
		this.mapServiceTypes = mapServiceTypes;
	}

	public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(String winRatio) {
        this.winRatio = winRatio;
    }

    public String getMinTrades() {
        return minTrades;
    }

    public void setMinTrades(String minTrades) {
        this.minTrades = minTrades;
    }

    public String getMinWinRatio() {
        return minWinRatio;
    }

    public void setMinWinRatio(String minWinRatio) {
        this.minWinRatio = minWinRatio;
    }

    public String getMaxWinRatio() {
        return maxWinRatio;
    }

    public void setMaxWinRatio(String maxWinRatio) {
        this.maxWinRatio = maxWinRatio;
    }

    public String getReturnRatio() {
		return returnRatio;
	}

	public void setReturnRatio(String returnRatio) {
		this.returnRatio = returnRatio;
	}

	public String getMinReturn() {
		return minReturn;
	}

	public void setMinReturn(String minReturn) {
		this.minReturn = minReturn;
	}

	public String getMaxReturn() {
		return maxReturn;
	}

	public void setMaxReturn(String maxReturn) {
		this.maxReturn = maxReturn;
	}

	public String getPips() {
		return pips;
	}

	public void setPips(String pips) {
		this.pips = pips;
	}

	public String getAveragePips() {
		return averagePips;
	}

	public void setAveragePips(String averagePips) {
		this.averagePips = averagePips;
	}

	public String getMaxDrawdown() {
		return maxDrawdown;
	}

	public void setMaxDrawdown(String maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
	}

	public String getMinMaxDrawdown() {
		return minMaxDrawdown;
	}

	public void setMinMaxDrawdown(String minMaxDrawdown) {
		this.minMaxDrawdown = minMaxDrawdown;
	}

	public String getMaxMaxDrawdown() {
		return maxMaxDrawdown;
	}

	public void setMaxMaxDrawdown(String maxMaxDrawdown) {
		this.maxMaxDrawdown = maxMaxDrawdown;
	}

	public String getAverageMargin() {
		return averageMargin;
	}

	public void setAverageMargin(String marginRatio) {
		this.averageMargin = marginRatio;
	}

	public String getMinAvgMargin() {
		return minAvgMargin;
	}

	public void setMinAvgMargin(String minAvgMargin) {
		this.minAvgMargin = minAvgMargin;
	}

	public String getMaxAvgMargin() {
		return maxAvgMargin;
	}

	public void setMaxAvgMargin(String maxAvgMargin) {
		this.maxAvgMargin = maxAvgMargin;
	}

	public List<String> getListServiceType() {
		return listServiceType;
	}

	public void setListServiceType(List<String> listServiceType) {
		this.listServiceType = listServiceType;
	}

	public Boolean getLast30Days() {
		return last30Days;
	}

	public void setLast30Days(Boolean last30Days) {
		this.last30Days = last30Days;
	}

	public List<RankingTraderInfo> getRankingDetails() {
        return rankingDetails;
    }

    public void setRankingDetails(List<RankingTraderInfo> rankingDetails) {
        this.rankingDetails = rankingDetails;
    }

    public RankingSearchCondition getCondition() {
        return condition;
    }

    public void setCondition(RankingSearchCondition condition) {
        this.condition = condition;
    }

    public Integer getStartRankNumber() {
        return startRankNumber;
    }

    public void setStartRankNumber(Integer startRankNumber) {
        this.startRankNumber = startRankNumber;
    }

    public Map<String, CountryInfo> getMapCountry() {
        return mapCountry;
    }

    public void setMapCountry(Map<String, CountryInfo> mapCountry) {
        this.mapCountry = mapCountry;
    }

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
