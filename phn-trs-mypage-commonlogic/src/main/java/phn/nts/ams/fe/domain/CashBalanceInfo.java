package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class CashBalanceInfo implements Serializable {
	private String currencyCode;
	private String customerId;
	private Integer serviceType;	
	private Double cashBalance;
	private Double previousBalance;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
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
	 * @return the cashBalance
	 */
	public Double getCashBalance() {
		return cashBalance;
	}
	/**
	 * @param cashBalance the cashBalance to set
	 */
	public void setCashBalance(Double cashBalance) {
		this.cashBalance = cashBalance;
	}
	/**
	 * @return the previousBalance
	 */
	public Double getPreviousBalance() {
		return previousBalance;
	}
	/**
	 * @param previousBalance the previousBalance to set
	 */
	public void setPreviousBalance(Double previousBalance) {
		this.previousBalance = previousBalance;
	}
	/**
	 * @return the activeFlag
	 */
	public Integer getActiveFlag() {
		return activeFlag;
	}
	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
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
