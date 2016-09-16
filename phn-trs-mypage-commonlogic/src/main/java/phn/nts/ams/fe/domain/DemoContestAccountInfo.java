package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description
 * @version NTS1.0
 * @author Quan.Le.Minh
 * @CrDate Jan 5, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestAccountInfo implements Serializable {
	private static final long serialVersionUID = -4875677049257949833L;
	private String deposit;
	private Timestamp tradingStartDatetime;
	private Timestamp tradingEndDatetime;
	private Integer leverage;
	private String currencyCode;
	private String nickname;
	
	private String loginId;
	private String customerId;
	private Integer contestId;
	private String password;
	private String subGroupName;
	private Integer subGroupId;
	
	//[NTS1.0-anhndn]Jan 7, 2013A - Start 
	private String rank;
	private String accountId;
	private String gain;
	private String balance;
	private String tradingVolume;
	private Integer status;
	//[NTS1.0-anhndn]Jan 7, 2013A - End
	
	public Integer getContestId() {
		return contestId;
	}
	public String getDeposit() {
		return deposit;
	}
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public Timestamp getTradingStartDatetime() {
		return tradingStartDatetime;
	}
	public void setTradingStartDatetime(Timestamp tradingStartDatetime) {
		this.tradingStartDatetime = tradingStartDatetime;
	}
	public Timestamp getTradingEndDatetime() {
		return tradingEndDatetime;
	}
	public void setTradingEndDatetime(Timestamp tradingEndDatetime) {
		this.tradingEndDatetime = tradingEndDatetime;
	}
	public Integer getLeverage() {
		return leverage;
	}
	public void setLeverage(Integer leverage) {
		this.leverage = leverage;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setContestId(Integer contestId) {
		this.contestId = contestId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public Integer getSubGroupId() {
		return subGroupId;
	}
	public void setSubGroupId(Integer subGroupId) {
		this.subGroupId = subGroupId;
	}
	public String getSubGroupName() {
		return subGroupName;
	}
	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}
	
	//[NTS1.0-anhndn]Jan 7, 2013A - Start 
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getGain() {
		return gain;
	}
	public void setGain(String gain) {
		this.gain = gain;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getTradingVolume() {
		return tradingVolume;
	}
	public void setTradingVolume(String tradingVolume) {
		this.tradingVolume = tradingVolume;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	//[NTS1.0-anhndn]Jan 7, 2013A - End
	
}
