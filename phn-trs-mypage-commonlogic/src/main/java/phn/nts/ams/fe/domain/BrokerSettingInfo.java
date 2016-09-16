package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class BrokerSettingInfo implements Serializable{
private static final long serialVersionUID = 1L;
	
	private Integer scCustServiceId;
    private String serviceType;
	private String brokerCd;
	private String brokerName;
	private String brokerId;
	private String serverAddress;
	private String accountId;
	private String accountPassword;
	private Integer accountType;
	private Integer accountKind;
	private Integer enableFlg;
	private String signalExpiredDatetime;
	private Integer edited;
	private String baseCurrency;
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
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
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
	public String getSignalExpiredDatetime() {
		return signalExpiredDatetime;
	}
	/**
	 * @param signalExpiredDatetime the signalExpiredDatetime to set
	 */
	public void setSignalExpiredDatetime(String signalExpiredDatetime) {
		this.signalExpiredDatetime = signalExpiredDatetime;
	}
	/**
	 * @return the brokerName
	 */
	public String getBrokerName() {
		return brokerName;
	}
	/**
	 * @param brokerName the brokerName to set
	 */
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	/**
	 * @return the edited
	 */
	public Integer getEdited() {
		return edited;
	}
	/**
	 * @param edited the edited to set
	 */
	public void setEdited(Integer edited) {
		this.edited = edited;
	}
	/**
	 * @return the brokerId
	 */
	public String getBrokerId() {
		return brokerId;
	}
	/**
	 * @param brokerId the brokerId to set
	 */
	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
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
	
}
