package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class CustomerServicesInfo implements Serializable {
	private String customerServiceId;	
	private String customerId;
	private Integer groupId; // GroupId of table AMS_CUSTOMER
	private String groupName; // GroupName	 of table AMS_CUSTOMER
	private Integer subGroupId; // Group of table AMS_CUSTOMER_SERVICE
	private String subGroupName; // GroupName of AMS_CUSTOMER_SERVICE
	private Integer serviceType;
	private Integer customerServiceStatus;
	private Integer allowTransactFlg;
	private Integer allowSendmoneyFlg;
	private Integer allowLoginFlg;
	private Integer agreementFlg;
	private String accountApplicationDate;
	private String accountOpenDate;
	private String accountOpenFinishDate;
	private String accountCancelDate;
	private String accountStatusChangeDate;
	private String firstDepositDate;
	private Timestamp accountStatusChangeDatetime;
	private String agentCustomerServiceId;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private Integer leverage;
	private String wlCode;
	private String currencyCode;
	private Integer losscutFlg;
	private Timestamp losscutDatetime;
	private String ntdAccountId;
	
	private String subGroupCode;
	/**
	 * @return the customerServiceId
	 */
	public String getCustomerServiceId() {
		return customerServiceId;
	}
	/**
	 * @param customerServiceId the customerServiceId to set
	 */
	public void setCustomerServiceId(String customerServiceId) {
		this.customerServiceId = customerServiceId;
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
	
	public Integer getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	/**
	 * @return the customerServiceStatus
	 */
	public Integer getCustomerServiceStatus() {
		return customerServiceStatus;
	}
	/**
	 * @param customerServiceStatus the customerServiceStatus to set
	 */
	public void setCustomerServiceStatus(Integer customerServiceStatus) {
		this.customerServiceStatus = customerServiceStatus;
	}
	/**
	 * @return the allowTransactFlg
	 */
	public Integer getAllowTransactFlg() {
		return allowTransactFlg;
	}
	/**
	 * @param allowTransactFlg the allowTransactFlg to set
	 */
	public void setAllowTransactFlg(Integer allowTransactFlg) {
		this.allowTransactFlg = allowTransactFlg;
	}
	/**
	 * @return the allowSendmoneyFlg
	 */
	public Integer getAllowSendmoneyFlg() {
		return allowSendmoneyFlg;
	}
	/**
	 * @param allowSendmoneyFlg the allowSendmoneyFlg to set
	 */
	public void setAllowSendmoneyFlg(Integer allowSendmoneyFlg) {
		this.allowSendmoneyFlg = allowSendmoneyFlg;
	}
	/**
	 * @return the allowLoginFlg
	 */
	public Integer getAllowLoginFlg() {
		return allowLoginFlg;
	}
	/**
	 * @param allowLoginFlg the allowLoginFlg to set
	 */
	public void setAllowLoginFlg(Integer allowLoginFlg) {
		this.allowLoginFlg = allowLoginFlg;
	}
	/**
	 * @return the agreementFlg
	 */
	public Integer getAgreementFlg() {
		return agreementFlg;
	}
	/**
	 * @param agreementFlg the agreementFlg to set
	 */
	public void setAgreementFlg(Integer agreementFlg) {
		this.agreementFlg = agreementFlg;
	}
	/**
	 * @return the accountApplicationDate
	 */
	public String getAccountApplicationDate() {
		return accountApplicationDate;
	}
	/**
	 * @param accountApplicationDate the accountApplicationDate to set
	 */
	public void setAccountApplicationDate(String accountApplicationDate) {
		this.accountApplicationDate = accountApplicationDate;
	}
	/**
	 * @return the accountOpenDate
	 */
	public String getAccountOpenDate() {
		return accountOpenDate;
	}
	/**
	 * @param accountOpenDate the accountOpenDate to set
	 */
	public void setAccountOpenDate(String accountOpenDate) {
		this.accountOpenDate = accountOpenDate;
	}
	/**
	 * @return the accountOpenFinishDate
	 */
	public String getAccountOpenFinishDate() {
		return accountOpenFinishDate;
	}
	/**
	 * @param accountOpenFinishDate the accountOpenFinishDate to set
	 */
	public void setAccountOpenFinishDate(String accountOpenFinishDate) {
		this.accountOpenFinishDate = accountOpenFinishDate;
	}
	/**
	 * @return the accountCancelDate
	 */
	public String getAccountCancelDate() {
		return accountCancelDate;
	}
	/**
	 * @param accountCancelDate the accountCancelDate to set
	 */
	public void setAccountCancelDate(String accountCancelDate) {
		this.accountCancelDate = accountCancelDate;
	}
	/**
	 * @return the accountStatusChangeDate
	 */
	public String getAccountStatusChangeDate() {
		return accountStatusChangeDate;
	}
	/**
	 * @param accountStatusChangeDate the accountStatusChangeDate to set
	 */
	public void setAccountStatusChangeDate(String accountStatusChangeDate) {
		this.accountStatusChangeDate = accountStatusChangeDate;
	}
	
	/**
	 * @return the agentCustomerServiceId
	 */
	public String getAgentCustomerServiceId() {
		return agentCustomerServiceId;
	}
	/**
	 * @param agentCustomerServiceId the agentCustomerServiceId to set
	 */
	public void setAgentCustomerServiceId(String agentCustomerServiceId) {
		this.agentCustomerServiceId = agentCustomerServiceId;
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
	 * @return the accountStatusChangeDatetime
	 */
	public Timestamp getAccountStatusChangeDatetime() {
		return accountStatusChangeDatetime;
	}
	/**
	 * @param accountStatusChangeDatetime the accountStatusChangeDatetime to set
	 */
	public void setAccountStatusChangeDatetime(Timestamp accountStatusChangeDatetime) {
		this.accountStatusChangeDatetime = accountStatusChangeDatetime;
	}
	/**
	 * @return the wlCode
	 */
	public String getWlCode() {
		return wlCode;
	}
	/**
	 * @param wlCode the wlCode to set
	 */
	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}
	/**
	 * @return the subGroupId
	 */
	public Integer getSubGroupId() {
		return subGroupId;
	}
	/**
	 * @param subGroupId the subGroupId to set
	 */
	public void setSubGroupId(Integer subGroupId) {
		this.subGroupId = subGroupId;
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
	public String getFirstDepositDate() {
		return firstDepositDate;
	}
	public void setFirstDepositDate(String firstDepositDate) {
		this.firstDepositDate = firstDepositDate;
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
	 * @return the losscutFlg
	 */
	public Integer getLosscutFlg() {
		return losscutFlg;
	}
	/**
	 * @param losscutFlg the losscutFlg to set
	 */
	public void setLosscutFlg(Integer losscutFlg) {
		this.losscutFlg = losscutFlg;
	}
	/**
	 * @return the losscutDatetime
	 */
	public Timestamp getLosscutDatetime() {
		return losscutDatetime;
	}
	/**
	 * @param losscutDatetime the losscutDatetime to set
	 */
	public void setLosscutDatetime(Timestamp losscutDatetime) {
		this.losscutDatetime = losscutDatetime;
	}
	/**
	 * @return the subGroupCode
	 */
	public String getSubGroupCode() {
		return subGroupCode;
	}
	/**
	 * @param subGroupCode the subGroupCode to set
	 */
	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}
	public String getNtdAccountId() {
		return ntdAccountId;
	}
	public void setNtdAccountId(String ntdAccountId) {
		this.ntdAccountId = ntdAccountId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSubGroupName() {
		return subGroupName;
	}

	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}

	@Override
	public String toString() {
		return "CustomerServicesInfo{" +
				"customerServiceId='" + customerServiceId + '\'' +
				", customerId='" + customerId + '\'' +
				", groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", subGroupId=" + subGroupId +
				", subGroupName='" + subGroupName + '\'' +
				", serviceType=" + serviceType +
				", customerServiceStatus=" + customerServiceStatus +
				", allowTransactFlg=" + allowTransactFlg +
				", allowSendmoneyFlg=" + allowSendmoneyFlg +
				", allowLoginFlg=" + allowLoginFlg +
				", agreementFlg=" + agreementFlg +
				", accountApplicationDate='" + accountApplicationDate + '\'' +
				", accountOpenDate='" + accountOpenDate + '\'' +
				", accountOpenFinishDate='" + accountOpenFinishDate + '\'' +
				", accountCancelDate='" + accountCancelDate + '\'' +
				", accountStatusChangeDate='" + accountStatusChangeDate + '\'' +
				", firstDepositDate='" + firstDepositDate + '\'' +
				", accountStatusChangeDatetime=" + accountStatusChangeDatetime +
				", agentCustomerServiceId='" + agentCustomerServiceId + '\'' +
				", activeFlg=" + activeFlg +
				", inputDate=" + inputDate +
				", updateDate=" + updateDate +
				", leverage=" + leverage +
				", wlCode='" + wlCode + '\'' +
				", currencyCode='" + currencyCode + '\'' +
				", losscutFlg=" + losscutFlg +
				", losscutDatetime=" + losscutDatetime +
				", ntdAccountId='" + ntdAccountId + '\'' +
				", subGroupCode='" + subGroupCode + '\'' +
				'}';
	}
}
