package phn.nts.ams.fe.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 15/11/2013 10:03 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScSummaryTradingCustInfo {

    private ScSummaryTradingCustIdInfo id = new ScSummaryTradingCustIdInfo();
    private ScCustomerInfo customer = new ScCustomerInfo();
    private String currencyCode;
    private Integer summaryType;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitTransCount;
    private BigDecimal lossProfit;
    private BigDecimal lossProfitTransCount;
    private BigDecimal swap;
    private BigDecimal totalComSignal;
    private BigDecimal totalComCopy;
    private BigDecimal totalComBroker;
    private BigDecimal totalInvest;
    private BigDecimal totalDeposit;
    private BigDecimal totalPl;
    private BigDecimal totalReturn;
    private BigDecimal totalGain;
    private BigDecimal totalTradeBuy;
    private BigDecimal totalTradeSell;
    private BigDecimal winRatio;
    private BigDecimal totalBuyAmount;
    private BigDecimal totalSellAmount;
    private BigDecimal totalPipGain;
    private BigDecimal totalPipLoss;
    private Integer activeFlg;
    private Timestamp inputDate;
    private Timestamp updateDate;

    private BigDecimal bestTrade;
    private BigDecimal bestTradePips;
    private BigDecimal worstTrade;
    private BigDecimal worstTradePips;
    private BigDecimal avgDuration;
    private BigDecimal totalWithdraw;
    
    private BigDecimal totalBuyLot;
    private BigDecimal totalSellLot;

    public ScSummaryTradingCustIdInfo getId() {
        return id;
    }

    public void setId(ScSummaryTradingCustIdInfo id) {
        this.id = id;
    }

    public ScCustomerInfo getCustomer() {
        return customer;
    }

    public void setCustomer(ScCustomerInfo customer) {
        this.customer = customer;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Integer getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(Integer summaryType) {
        this.summaryType = summaryType;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getGrossProfitTransCount() {
        return grossProfitTransCount;
    }

    public void setGrossProfitTransCount(BigDecimal grossProfitTransCount) {
        this.grossProfitTransCount = grossProfitTransCount;
    }

    public BigDecimal getLossProfit() {
        return lossProfit;
    }

    public void setLossProfit(BigDecimal lossProfit) {
        this.lossProfit = lossProfit;
    }

    public BigDecimal getLossProfitTransCount() {
        return lossProfitTransCount;
    }

    public void setLossProfitTransCount(BigDecimal lossProfitTransCount) {
        this.lossProfitTransCount = lossProfitTransCount;
    }

    public BigDecimal getSwap() {
        return swap;
    }

    public void setSwap(BigDecimal swap) {
        this.swap = swap;
    }

    public BigDecimal getTotalComSignal() {
        return totalComSignal;
    }

    public void setTotalComSignal(BigDecimal totalComSignal) {
        this.totalComSignal = totalComSignal;
    }

    public BigDecimal getTotalComCopy() {
        return totalComCopy;
    }

    public void setTotalComCopy(BigDecimal totalComCopy) {
        this.totalComCopy = totalComCopy;
    }

    public BigDecimal getTotalComBroker() {
        return totalComBroker;
    }

    public void setTotalComBroker(BigDecimal totalComBroker) {
        this.totalComBroker = totalComBroker;
    }

    public BigDecimal getTotalInvest() {
        return totalInvest;
    }

    public void setTotalInvest(BigDecimal totalInvest) {
        this.totalInvest = totalInvest;
    }

    public BigDecimal getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(BigDecimal totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public BigDecimal getTotalPl() {
        return totalPl;
    }

    public void setTotalPl(BigDecimal totalPl) {
        this.totalPl = totalPl;
    }

    public BigDecimal getTotalReturn() {
        return totalReturn;
    }

    public void setTotalReturn(BigDecimal totalReturn) {
        this.totalReturn = totalReturn;
    }

    public BigDecimal getTotalGain() {
        return totalGain;
    }

    public void setTotalGain(BigDecimal totalGain) {
        this.totalGain = totalGain;
    }

    public BigDecimal getTotalTradeBuy() {
        return totalTradeBuy;
    }

    public void setTotalTradeBuy(BigDecimal totalTradeBuy) {
        this.totalTradeBuy = totalTradeBuy;
    }

    public BigDecimal getTotalTradeSell() {
        return totalTradeSell;
    }

    public void setTotalTradeSell(BigDecimal totalTradeSell) {
        this.totalTradeSell = totalTradeSell;
    }

    public BigDecimal getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(BigDecimal winRatio) {
        this.winRatio = winRatio;
    }

    public BigDecimal getTotalBuyAmount() {
        return totalBuyAmount;
    }

    public void setTotalBuyAmount(BigDecimal totalBuyAmount) {
        this.totalBuyAmount = totalBuyAmount;
    }

    public BigDecimal getTotalSellAmount() {
        return totalSellAmount;
    }

    public void setTotalSellAmount(BigDecimal totalSellAmount) {
        this.totalSellAmount = totalSellAmount;
    }

    public BigDecimal getTotalPipGain() {
        return totalPipGain;
    }

    public void setTotalPipGain(BigDecimal totalPipGain) {
        this.totalPipGain = totalPipGain;
    }

    public BigDecimal getTotalPipLoss() {
        return totalPipLoss;
    }

    public void setTotalPipLoss(BigDecimal totalPipLoss) {
        this.totalPipLoss = totalPipLoss;
    }

    public Integer getActiveFlg() {
        return activeFlg;
    }

    public void setActiveFlg(Integer activeFlg) {
        this.activeFlg = activeFlg;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public BigDecimal getBestTrade() {
        return bestTrade;
    }

    public void setBestTrade(BigDecimal bestTrade) {
        this.bestTrade = bestTrade;
    }

    public BigDecimal getBestTradePips() {
        return bestTradePips;
    }

    public void setBestTradePips(BigDecimal bestTradePips) {
        this.bestTradePips = bestTradePips;
    }

    public BigDecimal getWorstTrade() {
        return worstTrade;
    }

    public void setWorstTrade(BigDecimal worstTrade) {
        this.worstTrade = worstTrade;
    }

    public BigDecimal getWorstTradePips() {
        return worstTradePips;
    }

    public void setWorstTradePips(BigDecimal worstTradePips) {
        this.worstTradePips = worstTradePips;
    }

    public BigDecimal getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(BigDecimal avgDuration) {
        this.avgDuration = avgDuration;
    }

    public BigDecimal getTotalWithdraw() {
        return totalWithdraw;
    }

    public void setTotalWithdraw(BigDecimal totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public BigDecimal getTotalBuyLot() {
		return totalBuyLot;
	}

	public void setTotalBuyLot(BigDecimal totalBuyLot) {
		this.totalBuyLot = totalBuyLot;
	}

	public BigDecimal getTotalSellLot() {
		return totalSellLot;
	}

	public void setTotalSellLot(BigDecimal totalSellLot) {
		this.totalSellLot = totalSellLot;
	}

	public void convertToGuessMode(){
        this.summaryType = null;
        this.grossProfit = null;
        this.grossProfitTransCount = null;
        this.lossProfit = null;
        this.totalBuyAmount = null;
        this.totalSellAmount = null;
        this.lossProfitTransCount = null;
        this.totalComSignal = null;
        this.totalComCopy = null;
        this.totalComBroker = null;
        this.totalInvest = null;
        this.totalDeposit = null;
        this.inputDate = null;
        this.updateDate = null;
        this.totalPl = null;
        this.totalBuyLot = null;
        this.totalSellLot = null;
    }
}
