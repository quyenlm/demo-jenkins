package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ScCustomerServiceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer scCustServiceId;
    private String subGroupCd;
    private Integer serviceType;
    private BigDecimal leverage;
	private String customerId;
	private String baseCurrency;
	private String brokerCd;
	private String serverAddress;
	private String accountId;
	private String accountPassword;
	private Integer accountType;
	private Integer accountKind;
	private Integer enableFlg;
	private Timestamp signalExpiredDatetime;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	/**
	 * @return the scCustServiceId
	 */
	public Integer getScCustServiceId() {
		return scCustServiceId;
	}
	/**
	 * @param scCustServiceId the scCustServiceId to set
	 */
	public void setScCustServiceId(Integer scCustServiceId) {
		this.scCustServiceId = scCustServiceId;
	}
	/**
	 * @return the subGroupCd
	 */
	public String getSubGroupCd() {
		return subGroupCd;
	}
	/**
	 * @param subGroupCd the subGroupCd to set
	 */
	public void setSubGroupCd(String subGroupCd) {
		this.subGroupCd = subGroupCd;
	}
	/**
	 * @return the serviceType
	 */
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
	 * @return the leverage
	 */
	public BigDecimal getLeverage() {
		return leverage;
	}
	/**
	 * @param leverage the leverage to set
	 */
	public void setLeverage(BigDecimal leverage) {
		this.leverage = leverage;
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
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}
	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	/**
	 * @return the brokerCd
	 */
	public String getBrokerCd() {
		return brokerCd;
	}
	/**
	 * @param brokerCd the brokerCd to set
	 */
	public void setBrokerCd(String brokerCd) {
		this.brokerCd = brokerCd;
	}
	/**
	 * @return the serverAddress
	 */
	public String getServerAddress() {
		return serverAddress;
	}
	/**
	 * @param serverAddress the serverAddress to set
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
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
	 * @return the accountPassword
	 */
	public String getAccountPassword() {
		return accountPassword;
	}
	/**
	 * @param accountPassword the accountPassword to set
	 */
	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}
	/**
	 * @return the accountType
	 */
	public Integer getAccountType() {
		return accountType;
	}
	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
	/**
	 * @return the accountKind
	 */
	public Integer getAccountKind() {
		return accountKind;
	}
	/**
	 * @param accountKind the accountKind to set
	 */
	public void setAccountKind(Integer accountKind) {
		this.accountKind = accountKind;
	}
	/**
	 * @return the enableFlg
	 */
	public Integer getEnableFlg() {
		return enableFlg;
	}
	/**
	 * @param enableFlg the enableFlg to set
	 */
	public void setEnableFlg(Integer enableFlg) {
		this.enableFlg = enableFlg;
	}
	/**
	 * @return the signalExpiredDatetime
	 */
	public Timestamp getSignalExpiredDatetime() {
		return signalExpiredDatetime;
	}
	/**
	 * @param signalExpiredDatetime the signalExpiredDatetime to set
	 */
	public void setSignalExpiredDatetime(Timestamp signalExpiredDatetime) {
		this.signalExpiredDatetime = signalExpiredDatetime;
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
