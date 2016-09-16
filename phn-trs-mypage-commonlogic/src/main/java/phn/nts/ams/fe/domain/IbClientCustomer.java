package phn.nts.ams.fe.domain;

import java.sql.Timestamp;

public class IbClientCustomer {
	private String customerId;
	private String clientCustomerId;
	private Timestamp clientOpenDate;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private Double total;
	private Timestamp kickbackDate;
	private String fullName;
	private String currencyCode;
	private String superCurrencyCode;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getClientCustomerId() {
		return clientCustomerId;
	}
	public void setClientCustomerId(String clientCustomerId) {
		this.clientCustomerId = clientCustomerId;
	}
	public Timestamp getClientOpenDate() {
		return clientOpenDate;
	}
	public void setClientOpenDate(Timestamp clientOpenDate) {
		this.clientOpenDate = clientOpenDate;
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
	
	public Timestamp getKickbackDate() {
		return kickbackDate;
	}
	public void setKickbackDate(Timestamp kickbackDate) {
		this.kickbackDate = kickbackDate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
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
	public String getSuperCurrencyCode() {
		return superCurrencyCode;
	}
	public void setSuperCurrencyCode(String superCurrencyCode) {
		this.superCurrencyCode = superCurrencyCode;
	}
	
	
}
