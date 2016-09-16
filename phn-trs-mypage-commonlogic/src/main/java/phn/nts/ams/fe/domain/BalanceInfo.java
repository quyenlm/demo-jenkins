package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.util.Date;

import phn.com.trs.util.enums.AccountBalanceResult;

public class BalanceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer balanceId;	
	private Boolean activeFlg;
	private Date updateDate;
	private Integer brokerId;
	private String description;
	
	private String accountId;
	private Integer currencyId;
	private Integer serviceType;
	
	private Double credit;
	private Double balance;
	
	private Double equity;
	private Double freemargin;
	private Double marginShort;
	private Double margin;
	private Double requiredMargin;
	private Double marginLevel;
	private Double unrealizedPl;
	private Integer leverage;
	
	private Double amountAvailable;
	private Double requestingAmount;
	private String currencyCode;
	private String currencyDecimal;
	private String currencyRound;
	
	private Double totalInvestment;
	private Double roundedMarginLevel;
	
	private AccountBalanceResult result;
	
	public Double getTotalInvestment() {
		return totalInvestment == null ? new Double(0) : totalInvestment;
	}
	public void setTotalInvestment(Double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}
	public Integer getBalanceId() {
		return balanceId;
	}
	public void setBalanceId(Integer balanceId) {
		this.balanceId = balanceId;
	}	
	public Boolean getActiveFlg() {
		return activeFlg;
	}
	public void setActiveFlg(Boolean activeFlg) {
		this.activeFlg = activeFlg;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getBrokerId() {
		return brokerId;
	}
	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	
	public Double getCredit() {
		return credit == null ? new Double(0) : credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}
	public Double getBalance() {
		return balance == null ? new Double(0) : balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getEquity() {
		return equity == null ? new Double(0) : equity;
	}
	public void setEquity(Double equity) {
		this.equity = equity;
	}
	public Double getFreemargin() {
		return freemargin == null ? new Double(0) : freemargin;
	}
	public void setFreemargin(Double freemargin) {
		this.freemargin = freemargin;
	}
	public Double getMargin() {
		return margin == null ? new Double(0) : margin;
	}
	public void setMargin(Double margin) {
		this.margin = margin;
	}	
	
	public Double getRequiredMargin() {
		return requiredMargin;
	}
	public void setRequiredMargin(Double requiredMargin) {
		this.requiredMargin = requiredMargin;
	}
	
	/**
	 * @return the marginLevel
	 */
	public Double getMarginLevel() {
		return marginLevel == null ? new Double(0) : marginLevel;
	}
	/**
	 * @param marginLevel the marginLevel to set
	 */
	public void setMarginLevel(Double marginLevel) {
		this.marginLevel = marginLevel;
	}
	
	public Double getMarginShort() {
		return marginShort == null ? new Double(0) : marginShort;
	}
	public void setMarginShort(Double marginShort) {
		this.marginShort = marginShort;
	}
		
	/**
	 * @return the amountAvailable
	 */
	public Double getAmountAvailable() {
		return amountAvailable == null ? new Double(0) : amountAvailable;
	}
	/**
	 * @param amountAvailable the amountAvailable to set
	 */
	public void setAmountAvailable(Double amountAvailable) {
		this.amountAvailable = amountAvailable;
	}
	
	/**
	 * @return the requestingAmount
	 */
	public Double getRequestingAmount() {
		return requestingAmount == null ? new Double(0) : requestingAmount;
	}
	/**
	 * @param requestingAmount the requestingAmount to set
	 */
	public void setRequestingAmount(Double requestingAmount) {
		this.requestingAmount = requestingAmount;
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
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the currencyDecimal
	 */
	public String getCurrencyDecimal() {
		return currencyDecimal;
	}
	/**
	 * @param currencyDecimal the currencyDecimal to set
	 */
	public void setCurrencyDecimal(String currencyDecimal) {
		this.currencyDecimal = currencyDecimal;
	}
	/**
	 * @return the currencyRound
	 */
	public String getCurrencyRound() {
		return currencyRound;
	}
	/**
	 * @param currencyRound the currencyRound to set
	 */
	public void setCurrencyRound(String currencyRound) {
		this.currencyRound = currencyRound;
	}
	/**
	 * @return the leverage
	 */
	public Integer getLeverage() {
		return leverage;
	}
	/**
	 * @param leverage the leverage to set
	 */
	public void setLeverage(Integer leverage) {
		this.leverage = leverage;
	}
	/**
	 * @return the roundedMarginLevel
	 */
	public Double getRoundedMarginLevel() {
		return roundedMarginLevel;
	}
	/**
	 * @param roundedMarginLevel the roundedMarginLevel to set
	 */
	public void setRoundedMarginLevel(Double roundedMarginLevel) {
		this.roundedMarginLevel = roundedMarginLevel;
	}
	
	public Double getUnrealizedPl() {
		return unrealizedPl;
	}
	public void setUnrealizedPl(Double unrealizedPl) {
		this.unrealizedPl = unrealizedPl;
	}
	public AccountBalanceResult getResult() {
		return result;
	}
	public void setResult(AccountBalanceResult result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "BalanceInfo [balanceId=" + balanceId + ", activeFlg="
				+ activeFlg + ", updateDate=" + updateDate + ", brokerId="
				+ brokerId + ", description=" + description + ", accountId="
				+ accountId + ", currencyId=" + currencyId + ", serviceType="
				+ serviceType + ", credit=" + credit + ", balance=" + balance
				+ ", equity=" + equity + ", freemargin=" + freemargin
				+ ", marginShort=" + marginShort + ", margin=" + margin
				+ ", requiredMargin=" + requiredMargin + ", marginLevel="
				+ marginLevel + ", unrealizedPl=" + unrealizedPl
				+ ", leverage=" + leverage + ", amountAvailable="
				+ amountAvailable + ", requestingAmount=" + requestingAmount
				+ ", currencyCode=" + currencyCode + ", currencyDecimal="
				+ currencyDecimal + ", currencyRound=" + currencyRound
				+ ", totalInvestment=" + totalInvestment
				+ ", roundedMarginLevel=" + roundedMarginLevel + ", result="
				+ result + "]";
	}
	
		
}
