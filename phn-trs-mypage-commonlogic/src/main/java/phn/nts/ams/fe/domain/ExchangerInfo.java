package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class ExchangerInfo extends PaymentMethodInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String exchangerId;
	private String customerId;
	private String bankInfo;
	private String exchangerName;
	private String rate;
	private String convertedAmount;
	private String amountStr;
	private String displayAmount;
	
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	
	//use for withdrawal
	private Integer customerBankId;
	private Integer countryId = -1;
	private String bankName;
	private String bankAddress;
	private String swiftCode;
	private String branchName;
	private String accountNo;
	private String accountName;
	private String countryName; 
	private String bankNameKana;
	private String branchNameKana;
	private String accountNameKana;
	//end
	
	/**
	 * @return the exchangerId
	 */
	public String getExchangerId() {
		return exchangerId;
	}
	/**
	 * @param exchangerId the exchangerId to set
	 */
	public void setExchangerId(String exchangerId) {
		this.exchangerId = exchangerId;
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
	 * @return the bankInfo
	 */
	public String getBankInfo() {
		return bankInfo;
	}
	/**
	 * @param bankInfo the bankInfo to set
	 */
	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}
	/**
	 * @return the exchangerName
	 */
	public String getExchangerName() {
		return exchangerName;
	}
	/**
	 * @param exchangerName the exchangerName to set
	 */
	public void setExchangerName(String exchangerName) {
		this.exchangerName = exchangerName;
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
	public Integer getActiveFlg() {
		return activeFlg;
	}
	public void setActiveFlg(Integer activeFlg) {
		this.activeFlg = activeFlg;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	
	public String getConvertedAmount() {
		return convertedAmount;
	}
	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAddress() {
		return bankAddress;
	}
	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the customerBankId
	 */
	public Integer getCustomerBankId() {
		return customerBankId;
	}
	/**
	 * @param customerBankId the customerBankId to set
	 */
	public void setCustomerBankId(Integer customerBankId) {
		this.customerBankId = customerBankId;
	}
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return the displayAmount
	 */
	public String getDisplayAmount() {
		return displayAmount;
	}
	/**
	 * @param displayAmount the displayAmount to set
	 */
	public void setDisplayAmount(String displayAmount) {
		this.displayAmount = displayAmount;
	}
	/**
	 * @return the amountStr
	 */
	public String getAmountStr() {
		return amountStr;
	}
	/**
	 * @param amountStr the amountStr to set
	 */
	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
	}
	public String getBankNameKana() {
		return bankNameKana;
	}
	public void setBankNameKana(String bankNameKana) {
		this.bankNameKana = bankNameKana;
	}
	public String getBranchNameKana() {
		return branchNameKana;
	}
	public void setBranchNameKana(String branchNameKana) {
		this.branchNameKana = branchNameKana;
	}
	public String getAccountNameKana() {
		return accountNameKana;
	}
	public void setAccountNameKana(String accountNameKana) {
		this.accountNameKana = accountNameKana;
	}
	
}
