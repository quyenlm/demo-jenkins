package phn.nts.ams.fe.domain;

import java.sql.Timestamp;

public class CustomerBankInfo extends PaymentMethodInfo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer customerBankId;
	private String customerId;
	private Integer countryId = -1;
	private String bankName;
	private String bankAddress;
	private String swiftCode;
	private String branchName;
	private String accountNo;
	private String accountName;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;	
	private String countryName; 
	private Integer bankAccClass;
	private String accountTypeStr;
	private String bankNameKana;
	private String branchNameKana;
	private String accountNameKana;
	private String bankCode;
	private String branchCode;
	
	private boolean selected;

	public Integer getCustomerBankId() {
		return customerBankId;
	}

	public void setCustomerBankId(Integer customerBankId) {
		this.customerBankId = customerBankId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	
	public String getAccountTypeStr() {
		return accountTypeStr;
	}

	public void setAccountTypeStr(String accountTypeStr) {
		this.accountTypeStr = accountTypeStr;
	}

	public Integer getBankAccClass() {
		return bankAccClass;
	}

	public void setBankAccClass(Integer bankAccClass) {
		this.bankAccClass = bankAccClass;
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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Override
	public String toString() {
		return "CustomerBankInfo [customerBankId=" + customerBankId
				+ ", customerId=" + customerId + ", countryId=" + countryId
				+ ", bankName=" + bankName + ", bankAddress=" + bankAddress
				+ ", swiftCode=" + swiftCode + ", branchName=" + branchName
				+ ", accountNo=" + accountNo + ", accountName=" + accountName
				+ ", activeFlg=" + activeFlg + ", countryName=" + countryName
				+ ", bankAccClass=" + bankAccClass + ", accountTypeStr="
				+ accountTypeStr + ", bankNameKana=" + bankNameKana
				+ ", branchNameKana=" + branchNameKana + ", accountNameKana="
				+ accountNameKana + ", bankCode=" + bankCode + ", branchCode="
				+ branchCode + "]";
	}
}
