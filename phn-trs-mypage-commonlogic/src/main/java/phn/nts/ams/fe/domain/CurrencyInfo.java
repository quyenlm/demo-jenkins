package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class CurrencyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4815026786124606435L;
	private String currencyCode;
	private String currencyName;
	private Integer currencyDecimal;
	private Integer currencyRound;
	private Integer displaySortOrder;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private List<String> customerId;
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
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}
	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	/**
	 * @return the currencyDecimal
	 */
	public Integer getCurrencyDecimal() {
		return currencyDecimal;
	}
	/**
	 * @param currencyDecimal the currencyDecimal to set
	 */
	public void setCurrencyDecimal(Integer currencyDecimal) {
		this.currencyDecimal = currencyDecimal;
	}
	/**
	 * @return the currencyRound
	 */
	public Integer getCurrencyRound() {
		return currencyRound;
	}
	/**
	 * @param currencyRound the currencyRound to set
	 */
	public void setCurrencyRound(Integer currencyRound) {
		this.currencyRound = currencyRound;
	}
	/**
	 * @return the displaySortOrder
	 */
	public Integer getDisplaySortOrder() {
		return displaySortOrder;
	}
	/**
	 * @param displaySortOrder the displaySortOrder to set
	 */
	public void setDisplaySortOrder(Integer displaySortOrder) {
		this.displaySortOrder = displaySortOrder;
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
	/**
	 * @return the customerId
	 */
	public List<String> getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(List<String> customerId) {
		this.customerId = customerId;
	}
	
	@Override
	public String toString() {
		return "CurrencyInfo [currencyCode=" + currencyCode + ", currencyName="
				+ currencyName + ", currencyDecimal=" + currencyDecimal
				+ ", currencyRound=" + currencyRound + ", displaySortOrder="
				+ displaySortOrder + ", activeFlag=" + activeFlag
				+ ", customerId=" + customerId + "]";
	}
}
