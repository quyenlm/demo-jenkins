package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class WithdrawalInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5116827020261070676L;
	private String withdrawalId;
	private String gwRefId;
	private Integer ewalletTypte;
	private String ewalletAccNo;
	private String ewalletEmail;
	private String ewalletApiPassword;
	private String ewalletSecureId;
	private Integer ccType;
	private String ccHolderName;
	private String ccNo;
	private String ccExpiredDate;
	private String ccCvv;
	private String ccIssuer;
	private String ccZipCode;
	private String ccState;
	private String ccCity;
	private String ccAddress;
	private String ccPhone;
	private String ccEmail;
	private Integer countryId;
	private String beneficiaryBankName;
	private String beneficiaryBankAddress;
	private String beneficiarySwiftCode;
	private String beneficiaryBranchName;
	private String beneficiaryAccountNo;
	private String beneficiaryAccountName;
	private Integer ewalletId;
	private String customerId;
	private String password;
	private String currencyCode;
	private String currencyCodeFX;
	private String currencyCodeBO;
	private Integer status;
	private Integer withdrawalMethod;
	private Double withdrawalAmount;
	private String withdrawalAcceptDate;
	private Timestamp withdrawalAcceptDatetime;
	private String withdrawalCompletedDate;
	private Timestamp withdrawalCompletedDatetime;
	private String remark;
	private String errorCode;
	private String csvFilename;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private Integer withdrawalType;
	private Integer serviceType;
	private String regCustomerId;	
	private Integer netellerInfo;
	private Integer payzaInfo;
	private Integer bankTransferInfo;
	private String serviceTypeName;
	private String methodName;
	private String countryName;
	private String rateSymbol;
	private String convertedRate;
	private String convertedAmount;
	private BigDecimal maxAmount;
	private BigDecimal minAmount;
	//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
	private Double withdrawalFee;
	private Double receivedAmount;
	//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
	private String exchangerId;
	/**
	 * @return the withdrawalId
	 */
	public String getWithdrawalId() {
		return withdrawalId;
	}
	/**
	 * @param withdrawalId the withdrawalId to set
	 */
	public void setWithdrawalId(String withdrawalId) {
		this.withdrawalId = withdrawalId;
	}
	/**
	 * @return the gwRefId
	 */
	public String getGwRefId() {
		return gwRefId;
	}
	/**
	 * @param gwRefId the gwRefId to set
	 */
	public void setGwRefId(String gwRefId) {
		this.gwRefId = gwRefId;
	}
	/**
	 * @return the ewalletTypte
	 */
	public Integer getEwalletTypte() {
		return ewalletTypte;
	}
	/**
	 * @param ewalletTypte the ewalletTypte to set
	 */
	public void setEwalletTypte(Integer ewalletTypte) {
		this.ewalletTypte = ewalletTypte;
	}
	/**
	 * @return the ewalletAccNo
	 */
	public String getEwalletAccNo() {
		return ewalletAccNo;
	}
	/**
	 * @param ewalletAccNo the ewalletAccNo to set
	 */
	public void setEwalletAccNo(String ewalletAccNo) {
		this.ewalletAccNo = ewalletAccNo;
	}
	/**
	 * @return the ewalletEmail
	 */
	public String getEwalletEmail() {
		return ewalletEmail;
	}
	/**
	 * @param ewalletEmail the ewalletEmail to set
	 */
	public void setEwalletEmail(String ewalletEmail) {
		this.ewalletEmail = ewalletEmail;
	}
	/**
	 * @return the ewalletApiPassword
	 */
	public String getEwalletApiPassword() {
		return ewalletApiPassword;
	}
	/**
	 * @param ewalletApiPassword the ewalletApiPassword to set
	 */
	public void setEwalletApiPassword(String ewalletApiPassword) {
		this.ewalletApiPassword = ewalletApiPassword;
	}
	/**
	 * @return the ewalletSecureId
	 */
	public String getEwalletSecureId() {
		return ewalletSecureId;
	}
	/**
	 * @param ewalletSecureId the ewalletSecureId to set
	 */
	public void setEwalletSecureId(String ewalletSecureId) {
		this.ewalletSecureId = ewalletSecureId;
	}
	/**
	 * @return the ccType
	 */
	public Integer getCcType() {
		return ccType;
	}
	/**
	 * @param ccType the ccType to set
	 */
	public void setCcType(Integer ccType) {
		this.ccType = ccType;
	}
	/**
	 * @return the ccHolderName
	 */
	public String getCcHolderName() {
		return ccHolderName;
	}
	/**
	 * @param ccHolderName the ccHolderName to set
	 */
	public void setCcHolderName(String ccHolderName) {
		this.ccHolderName = ccHolderName;
	}
	/**
	 * @return the ccNo
	 */
	public String getCcNo() {
		return ccNo;
	}
	/**
	 * @param ccNo the ccNo to set
	 */
	public void setCcNo(String ccNo) {
		this.ccNo = ccNo;
	}
	/**
	 * @return the ccExpiredDate
	 */
	public String getCcExpiredDate() {
		return ccExpiredDate;
	}
	/**
	 * @param ccExpiredDate the ccExpiredDate to set
	 */
	public void setCcExpiredDate(String ccExpiredDate) {
		this.ccExpiredDate = ccExpiredDate;
	}
	/**
	 * @return the ccCvv
	 */
	public String getCcCvv() {
		return ccCvv;
	}
	/**
	 * @param ccCvv the ccCvv to set
	 */
	public void setCcCvv(String ccCvv) {
		this.ccCvv = ccCvv;
	}
	/**
	 * @return the ccIssuer
	 */
	public String getCcIssuer() {
		return ccIssuer;
	}
	/**
	 * @param ccIssuer the ccIssuer to set
	 */
	public void setCcIssuer(String ccIssuer) {
		this.ccIssuer = ccIssuer;
	}
	/**
	 * @return the ccZipCode
	 */
	public String getCcZipCode() {
		return ccZipCode;
	}
	/**
	 * @param ccZipCode the ccZipCode to set
	 */
	public void setCcZipCode(String ccZipCode) {
		this.ccZipCode = ccZipCode;
	}
	/**
	 * @return the ccState
	 */
	public String getCcState() {
		return ccState;
	}
	/**
	 * @param ccState the ccState to set
	 */
	public void setCcState(String ccState) {
		this.ccState = ccState;
	}
	/**
	 * @return the ccCity
	 */
	public String getCcCity() {
		return ccCity;
	}
	/**
	 * @param ccCity the ccCity to set
	 */
	public void setCcCity(String ccCity) {
		this.ccCity = ccCity;
	}
	/**
	 * @return the ccAddress
	 */
	public String getCcAddress() {
		return ccAddress;
	}
	/**
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	/**
	 * @return the ccPhone
	 */
	public String getCcPhone() {
		return ccPhone;
	}
	/**
	 * @param ccPhone the ccPhone to set
	 */
	public void setCcPhone(String ccPhone) {
		this.ccPhone = ccPhone;
	}
	/**
	 * @return the ccEmail
	 */
	public String getCcEmail() {
		return ccEmail;
	}
	/**
	 * @param ccEmail the ccEmail to set
	 */
	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}
	/**
	 * @return the countryId
	 */
	public Integer getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	/**
	 * @return the beneficiaryBankName
	 */
	public String getBeneficiaryBankName() {
		return beneficiaryBankName;
	}
	/**
	 * @param beneficiaryBankName the beneficiaryBankName to set
	 */
	public void setBeneficiaryBankName(String beneficiaryBankName) {
		this.beneficiaryBankName = beneficiaryBankName;
	}
	/**
	 * @return the beneficiaryBankAddress
	 */
	public String getBeneficiaryBankAddress() {
		return beneficiaryBankAddress;
	}
	/**
	 * @param beneficiaryBankAddress the beneficiaryBankAddress to set
	 */
	public void setBeneficiaryBankAddress(String beneficiaryBankAddress) {
		this.beneficiaryBankAddress = beneficiaryBankAddress;
	}
	/**
	 * @return the beneficiarySwiftCode
	 */
	public String getBeneficiarySwiftCode() {
		return beneficiarySwiftCode;
	}
	/**
	 * @param beneficiarySwiftCode the beneficiarySwiftCode to set
	 */
	public void setBeneficiarySwiftCode(String beneficiarySwiftCode) {
		this.beneficiarySwiftCode = beneficiarySwiftCode;
	}
	/**
	 * @return the beneficiaryBranchName
	 */
	public String getBeneficiaryBranchName() {
		return beneficiaryBranchName;
	}
	/**
	 * @param beneficiaryBranchName the beneficiaryBranchName to set
	 */
	public void setBeneficiaryBranchName(String beneficiaryBranchName) {
		this.beneficiaryBranchName = beneficiaryBranchName;
	}
	/**
	 * @return the beneficiaryAccountNo
	 */
	public String getBeneficiaryAccountNo() {
		return beneficiaryAccountNo;
	}
	/**
	 * @param beneficiaryAccountNo the beneficiaryAccountNo to set
	 */
	public void setBeneficiaryAccountNo(String beneficiaryAccountNo) {
		this.beneficiaryAccountNo = beneficiaryAccountNo;
	}
	/**
	 * @return the beneficiaryAccountName
	 */
	public String getBeneficiaryAccountName() {
		return beneficiaryAccountName;
	}
	/**
	 * @param beneficiaryAccountName the beneficiaryAccountName to set
	 */
	public void setBeneficiaryAccountName(String beneficiaryAccountName) {
		this.beneficiaryAccountName = beneficiaryAccountName;
	}
	/**
	 * @return the ewalletId
	 */
	public Integer getEwalletId() {
		return ewalletId;
	}
	/**
	 * @param ewalletId the ewalletId to set
	 */
	public void setEwalletId(Integer ewalletId) {
		this.ewalletId = ewalletId;
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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the withdrawalMethod
	 */
	public Integer getWithdrawalMethod() {
		return withdrawalMethod;
	}
	/**
	 * @param withdrawalMethod the withdrawalMethod to set
	 */
	public void setWithdrawalMethod(Integer withdrawalMethod) {
		this.withdrawalMethod = withdrawalMethod;
	}
	/**
	 * @return the withdrawalAmount
	 */
	public Double getWithdrawalAmount() {
		return withdrawalAmount;
	}
	/**
	 * @param withdrawalAmount the withdrawalAmount to set
	 */
	public void setWithdrawalAmount(Double withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}
	/**
	 * @return the withdrawalAcceptDate
	 */
	public String getWithdrawalAcceptDate() {
		return withdrawalAcceptDate;
	}
	/**
	 * @param withdrawalAcceptDate the withdrawalAcceptDate to set
	 */
	public void setWithdrawalAcceptDate(String withdrawalAcceptDate) {
		this.withdrawalAcceptDate = withdrawalAcceptDate;
	}
	/**
	 * @return the withdrawalAcceptDatetime
	 */
	public Timestamp getWithdrawalAcceptDatetime() {
		return withdrawalAcceptDatetime;
	}
	/**
	 * @param withdrawalAcceptDatetime the withdrawalAcceptDatetime to set
	 */
	public void setWithdrawalAcceptDatetime(Timestamp withdrawalAcceptDatetime) {
		this.withdrawalAcceptDatetime = withdrawalAcceptDatetime;
	}
	/**
	 * @return the withdrawalCompletedDate
	 */
	public String getWithdrawalCompletedDate() {
		return withdrawalCompletedDate;
	}
	/**
	 * @param withdrawalCompletedDate the withdrawalCompletedDate to set
	 */
	public void setWithdrawalCompletedDate(String withdrawalCompletedDate) {
		this.withdrawalCompletedDate = withdrawalCompletedDate;
	}
	/**
	 * @return the withdrawalCompletedDatetime
	 */
	public Timestamp getWithdrawalCompletedDatetime() {
		return withdrawalCompletedDatetime;
	}
	/**
	 * @param withdrawalCompletedDatetime the withdrawalCompletedDatetime to set
	 */
	public void setWithdrawalCompletedDatetime(Timestamp withdrawalCompletedDatetime) {
		this.withdrawalCompletedDatetime = withdrawalCompletedDatetime;
	}
	/**
	 * @return the note
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param note the note to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the csvFilename
	 */
	public String getCsvFilename() {
		return csvFilename;
	}
	/**
	 * @param csvFilename the csvFilename to set
	 */
	public void setCsvFilename(String csvFilename) {
		this.csvFilename = csvFilename;
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
	 * @return the withdrawalType
	 */
	public Integer getWithdrawalType() {
		return withdrawalType;
	}
	/**
	 * @param withdrawalType the withdrawalType to set
	 */
	public void setWithdrawalType(Integer withdrawalType) {
		this.withdrawalType = withdrawalType;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the regCustomerId
	 */
	public String getRegCustomerId() {
		return regCustomerId;
	}
	/**
	 * @param regCustomerId the regCustomerId to set
	 */
	public void setRegCustomerId(String regCustomerId) {
		this.regCustomerId = regCustomerId;
	}
	/**
	 * @return the netellerInfo
	 */
	public Integer getNetellerInfo() {
		return netellerInfo;
	}
	/**
	 * @param netellerInfo the netellerInfo to set
	 */
	public void setNetellerInfo(Integer netellerInfo) {
		this.netellerInfo = netellerInfo;
	}
	/**
	 * @return the payzaInfo
	 */
	public Integer getPayzaInfo() {
		return payzaInfo;
	}
	/**
	 * @param payzaInfo the payzaInfo to set
	 */
	public void setPayzaInfo(Integer payzaInfo) {
		this.payzaInfo = payzaInfo;
	}
	/**
	 * @return the bankTransferInfo
	 */
	public Integer getBankTransferInfo() {
		return bankTransferInfo;
	}
	/**
	 * @param bankTransferInfo the bankTransferInfo to set
	 */
	public void setBankTransferInfo(Integer bankTransferInfo) {
		this.bankTransferInfo = bankTransferInfo;
	}
	/**
	 * @return the serviceTypeName
	 */
	public String getServiceTypeName() {
		return serviceTypeName;
	}
	/**
	 * @param serviceTypeName the serviceTypeName to set
	 */
	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
	 * @return the currencyCodeFX
	 */
	public String getCurrencyCodeFX() {
		return currencyCodeFX;
	}
	/**
	 * @param currencyCodeFX the currencyCodeFX to set
	 */
	public void setCurrencyCodeFX(String currencyCodeFX) {
		this.currencyCodeFX = currencyCodeFX;
	}
	/**
	 * @return the currencyCodeBO
	 */
	public String getCurrencyCodeBO() {
		return currencyCodeBO;
	}
	/**
	 * @param currencyCodeBO the currencyCodeBO to set
	 */
	public void setCurrencyCodeBO(String currencyCodeBO) {
		this.currencyCodeBO = currencyCodeBO;
	}
	/**
	 * @return the rateSymbol
	 */
	public String getRateSymbol() {
		return rateSymbol;
	}
	/**
	 * @param rateSymbol the rateSymbol to set
	 */
	public void setRateSymbol(String rateSymbol) {
		this.rateSymbol = rateSymbol;
	}
	/**
	 * @return the convertedRate
	 */
	public String getConvertedRate() {
		return convertedRate;
	}
	/**
	 * @param convertedRate the convertedRate to set
	 */
	public void setConvertedRate(String convertedRate) {
		this.convertedRate = convertedRate;
	}
	/**
	 * @return the convertedAmount
	 */
	public String getConvertedAmount() {
		return convertedAmount;
	}
	/**
	 * @param convertedAmount the convertedAmount to set
	 */
	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	/**
	 * @return the maxAmount
	 */
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return the minAmount
	 */
	public BigDecimal getMinAmount() {
		return minAmount;
	}
	/**
	 * @param minAmount the minAmount to set
	 */
	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}
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
	 * @return the withdrawalFee
	 */
	public Double getWithdrawalFee() {
		return withdrawalFee;
	}
	/**
	 * @param withdrawalFee the withdrawalFee to set
	 */
	public void setWithdrawalFee(Double withdrawalFee) {
		this.withdrawalFee = withdrawalFee;
	}
	/**
	 * @return the receivedAmount
	 */
	public Double getReceivedAmount() {
		return receivedAmount;
	}
	/**
	 * @param receivedAmount the receivedAmount to set
	 */
	public void setReceivedAmount(Double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

		
}
