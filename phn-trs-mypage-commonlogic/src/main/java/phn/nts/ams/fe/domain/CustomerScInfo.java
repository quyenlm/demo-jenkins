package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CustomerScInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String customerId;
	private String userName;
	private String description;
	private String descriptionTextArea;
	private Integer copierNo;
	private Integer followerNo;
	private Integer leverage;
	private Integer userType;
	private Integer sendMessageFlg;
	private Integer writeMyBoardFlg;
	private Integer notificationFlg;
	private BigDecimal signalTotalReturn;
	private BigDecimal signalTotalPips;
	private BigDecimal signalTotalTrade;
	private BigDecimal signalWinRatio;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	
	
	public String getDescriptionTextArea() {
		return descriptionTextArea;
	}
	public void setDescriptionTextArea(String descriptionTextArea) {
		this.descriptionTextArea = descriptionTextArea;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the copierNo
	 */
	public Integer getCopierNo() {
		return copierNo;
	}
	/**
	 * @param copierNo the copierNo to set
	 */
	public void setCopierNo(Integer copierNo) {
		this.copierNo = copierNo;
	}
	/**
	 * @return the followerNo
	 */
	public Integer getFollowerNo() {
		return followerNo;
	}
	/**
	 * @param followerNo the followerNo to set
	 */
	public void setFollowerNo(Integer followerNo) {
		this.followerNo = followerNo;
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
	 * @return the userType
	 */
	public Integer getUserType() {
		return userType;
	}
	/**
	 * @param userType the userType to set
	 */
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	/**
	 * @return the sendMessageFlg
	 */
	public Integer getSendMessageFlg() {
		return sendMessageFlg;
	}
	/**
	 * @param sendMessageFlg the sendMessageFlg to set
	 */
	public void setSendMessageFlg(Integer sendMessageFlg) {
		this.sendMessageFlg = sendMessageFlg;
	}
	/**
	 * @return the writeMyBoardFlg
	 */
	public Integer getWriteMyBoardFlg() {
		return writeMyBoardFlg;
	}
	/**
	 * @param writeMyBoardFlg the writeMyBoardFlg to set
	 */
	public void setWriteMyBoardFlg(Integer writeMyBoardFlg) {
		this.writeMyBoardFlg = writeMyBoardFlg;
	}
	/**
	 * @return the notificationFlg
	 */
	public Integer getNotificationFlg() {
		return notificationFlg;
	}
	/**
	 * @param notificationFlg the notificationFlg to set
	 */
	public void setNotificationFlg(Integer notificationFlg) {
		this.notificationFlg = notificationFlg;
	}
	/**
	 * @return the signalTotalReturn
	 */
	public BigDecimal getSignalTotalReturn() {
		return signalTotalReturn;
	}
	/**
	 * @param signalTotalReturn the signalTotalReturn to set
	 */
	public void setSignalTotalReturn(BigDecimal signalTotalReturn) {
		this.signalTotalReturn = signalTotalReturn;
	}
	/**
	 * @return the signalTotalPips
	 */
	public BigDecimal getSignalTotalPips() {
		return signalTotalPips;
	}
	/**
	 * @param signalTotalPips the signalTotalPips to set
	 */
	public void setSignalTotalPips(BigDecimal signalTotalPips) {
		this.signalTotalPips = signalTotalPips;
	}
	/**
	 * @return the signalTotalTrade
	 */
	public BigDecimal getSignalTotalTrade() {
		return signalTotalTrade;
	}
	/**
	 * @param signalTotalTrade the signalTotalTrade to set
	 */
	public void setSignalTotalTrade(BigDecimal signalTotalTrade) {
		this.signalTotalTrade = signalTotalTrade;
	}
	/**
	 * @return the signalWinRatio
	 */
	public BigDecimal getSignalWinRatio() {
		return signalWinRatio;
	}
	/**
	 * @param signalWinRatio the signalWinRatio to set
	 */
	public void setSignalWinRatio(BigDecimal signalWinRatio) {
		this.signalWinRatio = signalWinRatio;
	}
	/**
	 * @return the activeFlg
	 */
	public Integer getActiveFlg() {
		return activeFlg;
	}
	/**
	 * @param activeFlg the activeFlg to set
	 */
	public void setActiveFlg(Integer activeFlg) {
		this.activeFlg = activeFlg;
	}
	/**
	 * @return the inputDate
	 */
	public Timestamp getInputDate() {
		return inputDate;
	}
	/**
	 * @param inputDate the inputDate to set
	 */
	public void setInputDate(Timestamp inputDate) {
		this.inputDate = inputDate;
	}
	/**
	 * @return the updateDate
	 */
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
