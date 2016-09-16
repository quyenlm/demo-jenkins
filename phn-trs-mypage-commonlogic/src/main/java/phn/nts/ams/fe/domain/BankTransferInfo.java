package phn.nts.ams.fe.domain;

import java.util.Date;

public class BankTransferInfo extends PaymentMethodInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3453086995095524073L;
	private String bankCode;
	private String bankName;
	private String branchCode;
	private String branchName;
	private String accountNumber;
	private String beneficiaryName;
	private String swiftCode;
	private String bankAddress;
	private String countryId;	
	private Date regDate;	
	private Integer bankAccClass;
	private String bankNameFullSize;
	private String bankNameHalfSize;
	private String branchNameFullSize;
	private String branchNameHalfSize;
	
	
	public String getBankNameFullSize() {
		return bankNameFullSize;
	}
	public void setBankNameFullSize(String bankNameFullSize) {
		this.bankNameFullSize = bankNameFullSize;
	}
	public String getBankNameHalfSize() {
		return bankNameHalfSize;
	}
	public void setBankNameHalfSize(String bankNameHalfSize) {
		this.bankNameHalfSize = bankNameHalfSize;
	}
	public String getBranchNameFullSize() {
		return branchNameFullSize;
	}
	public void setBranchNameFullSize(String branchNameFullSize) {
		this.branchNameFullSize = branchNameFullSize;
	}
	public String getBranchNameHalfSize() {
		return branchNameHalfSize;
	}
	public void setBranchNameHalfSize(String branchNameHalfSize) {
		this.branchNameHalfSize = branchNameHalfSize;
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
	public Integer getBankAccClass() {
		return bankAccClass;
	}
	public void setBankAccClass(Integer bankAccClass) {
		this.bankAccClass = bankAccClass;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getBankAddress() {
		return bankAddress;
	}
	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	
	/**
	 * @return the countryId
	 */
	public String getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}	
	/**
	 * @return the beneficiaryName
	 */
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	/**
	 * @param beneficiaryName the beneficiaryName to set
	 */
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	
	
}	
