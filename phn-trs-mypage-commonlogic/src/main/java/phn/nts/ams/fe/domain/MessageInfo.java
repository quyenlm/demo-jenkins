package phn.nts.ams.fe.domain;

import java.sql.Timestamp;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsWhitelabel;

public class MessageInfo implements java.io.Serializable{
	private static final long serialVersionUID = -5058249627995110429L;
	private Integer messageId;
	private AmsWhitelabel amsWhitelabel;
	private AmsCustomer amsCustomer;
	private Integer serviceType;
	private Integer messageCategory;
	private Integer messageType;
	private String messageTitle;
	private String information;
	private Integer activeFlag;
	private Integer customerDeleteFlg;
	private Integer readingManageFlg;
	private Timestamp startDate;
	private Timestamp endDate;
	private Integer forceDisplayFlg;
	private Timestamp confirmTerm;
	private Integer actionBuyNewFlg;
	private Integer actionWithdrawalFlg;
	private Integer actionLoginFlg;
	private Integer displayObjectType;
	private Integer groupId;
	private Integer customerStatus;
	private Integer statusAfterDay;
	private Integer statusDaily;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private Boolean readFlg;
	private Boolean allowDeleteFlag;
	private String serviceName;
	
	
	public Integer getMessageId() {
		return messageId;
	}
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	public AmsWhitelabel getAmsWhitelabel() {
		return amsWhitelabel;
	}
	public void setAmsWhitelabel(AmsWhitelabel amsWhitelabel) {
		this.amsWhitelabel = amsWhitelabel;
	}
	public AmsCustomer getAmsCustomer() {
		return amsCustomer;
	}
	public void setAmsCustomer(AmsCustomer amsCustomer) {
		this.amsCustomer = amsCustomer;
	}
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	public Integer getMessageCategory() {
		return messageCategory;
	}
	public void setMessageCategory(Integer messageCategory) {
		this.messageCategory = messageCategory;
	}
	public Integer getMessageType() {
		return messageType;
	}
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public Integer getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}
	public Integer getCustomerDeleteFlg() {
		return customerDeleteFlg;
	}
	public void setCustomerDeleteFlg(Integer customerDeleteFlg) {
		this.customerDeleteFlg = customerDeleteFlg;
	}
	public Integer getReadingManageFlg() {
		return readingManageFlg;
	}
	public void setReadingManageFlg(Integer readingManageFlg) {
		this.readingManageFlg = readingManageFlg;
	}
	public Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}
	public Timestamp getEndDate() {
		return endDate;
	}
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}
	public Integer getForceDisplayFlg() {
		return forceDisplayFlg;
	}
	public void setForceDisplayFlg(Integer forceDisplayFlg) {
		this.forceDisplayFlg = forceDisplayFlg;
	}
	public Timestamp getConfirmTerm() {
		return confirmTerm;
	}
	public void setConfirmTerm(Timestamp confirmTerm) {
		this.confirmTerm = confirmTerm;
	}
	public Integer getActionBuyNewFlg() {
		return actionBuyNewFlg;
	}
	public void setActionBuyNewFlg(Integer actionBuyNewFlg) {
		this.actionBuyNewFlg = actionBuyNewFlg;
	}
	public Integer getActionWithdrawalFlg() {
		return actionWithdrawalFlg;
	}
	public void setActionWithdrawalFlg(Integer actionWithdrawalFlg) {
		this.actionWithdrawalFlg = actionWithdrawalFlg;
	}
	public Integer getActionLoginFlg() {
		return actionLoginFlg;
	}
	public void setActionLoginFlg(Integer actionLoginFlg) {
		this.actionLoginFlg = actionLoginFlg;
	}
	public Integer getDisplayObjectType() {
		return displayObjectType;
	}
	public void setDisplayObjectType(Integer displayObjectType) {
		this.displayObjectType = displayObjectType;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(Integer customerStatus) {
		this.customerStatus = customerStatus;
	}
	public Integer getStatusAfterDay() {
		return statusAfterDay;
	}
	public void setStatusAfterDay(Integer statusAfterDay) {
		this.statusAfterDay = statusAfterDay;
	}
	public Integer getStatusDaily() {
		return statusDaily;
	}
	public void setStatusDaily(Integer statusDaily) {
		this.statusDaily = statusDaily;
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
	/**
	 * @return the readFlg
	 */
	public Boolean getReadFlg() {
		return readFlg;
	}
	/**
	 * @param readFlg the readFlg to set
	 */
	public void setReadFlg(Boolean readFlg) {
		this.readFlg = readFlg;
	}	
	/**
	 * @return the allowDeleteFlag
	 */
	public Boolean getAllowDeleteFlag() {
		return allowDeleteFlag;
	}
	/**
	 * @param allowDeleteFlag the allowDeleteFlag to set
	 */
	public void setAllowDeleteFlag(Boolean allowDeleteFlag) {
		this.allowDeleteFlag = allowDeleteFlag;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}
