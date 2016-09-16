package phn.nts.ams.fe.model;

import java.math.BigDecimal;

public class ChangeFundsModel {
	private String customerId;
	private String avatarTimestamp;
	private String nickName;
	private String avatar;
	private String customerDescription;
	private String amount;
	private Integer loadChangeFundInfoResult ;
	private Integer changeFundResult;
	private String message;
	private String baseCurrency;
	private BigDecimal currentInvestment;
	private BigDecimal availableInvestment;
	private Integer changeFundCopyId;
	private Integer scCustomerServiceId;
	private Integer serviceType;
	private Integer accountKind;
	private String countryCode;
	private String countryName;
	
	
	public String getAvatarTimestamp() {
		return avatarTimestamp;
	}

	public void setAvatarTimestamp(String avatarTimestamp) {
		this.avatarTimestamp = avatarTimestamp;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getAccountKind() {
		return accountKind;
	}

	public void setAccountKind(Integer accountKind) {
		this.accountKind = accountKind;
	}

	public Integer getScCustomerServiceId() {
		return scCustomerServiceId;
	}

	public void setScCustomerServiceId(Integer scCustomerServiceId) {
		this.scCustomerServiceId = scCustomerServiceId;
	}

	public Integer getChangeFundCopyId() {
		return changeFundCopyId;
	}

	public void setChangeFundCopyId(Integer changeFundCopyId) {
		this.changeFundCopyId = changeFundCopyId;
	}

	public BigDecimal getCurrentInvestment() {
		return currentInvestment;
	}

	public void setCurrentInvestment(BigDecimal currentInvestment) {
		this.currentInvestment = currentInvestment;
	}

	public BigDecimal getAvailableInvestment() {
		return availableInvestment;
	}

	public void setAvailableInvestment(BigDecimal availableInvestment) {
		this.availableInvestment = availableInvestment;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Integer getChangeFundResult() {
		return changeFundResult;
	}

	public void setChangeFundResult(Integer changeFundResult) {
		this.changeFundResult = changeFundResult;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getLoadChangeFundInfoResult() {
		return loadChangeFundInfoResult;
	}

	public void setLoadChangeFundInfoResult(Integer loadChangeFundInfoResult) {
		this.loadChangeFundInfoResult = loadChangeFundInfoResult;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCustomerDescription() {
		return customerDescription;
	}

	public void setCustomerDescription(String customerDescription) {
		this.customerDescription = customerDescription;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
