package phn.nts.ams.fe.domain;

import java.sql.Timestamp;
import java.util.List;

import phn.com.nts.util.common.Utilities;

public class CreditCardInfo extends PaymentMethodInfo {
	private static final long serialVersionUID = 1L;
	private Integer customerCcId;
	private String customerId;
	private Integer countryId;
	private String countryCode;
	private String countryName;
	
	private Integer ccType;
	private String ccFirstName;
	private String ccLastName;
	private String ccHolderName;
	private String ccNo;
	private String ccDriverNo;
	private String ccNoDisp;
	private String expiredDate;
	private String ccCvv;
	private String ccIssuer;
	private String zipCode;
	private String state;
	private String city;
	private String address;
	private String phone;
	private String email;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String ccTypeName;
	
	//[NTS1.0-anhndn]Jan 21, 2013A - Start 
	private Integer docVerifyStatus;
	//[NTS1.0-anhndn]Jan 21, 2013A - End
	
	//[NTS1.0-anhndn]Jan 25, 2013A - Start 
	private String expiredYear;
	private String expiredMonth;
	//[NTS1.0-anhndn]Jan 25, 2013A - End
	//[NTS1.0-Quan.Le.Minh]Feb 19, 2013A - Start 
	List<DocumentInfo> docInfos;
	//[NTS1.0-Quan.Le.Minh]Feb 19, 2013A - End
	
	private String ccNoLastDigit;
	
	public Integer getCustomerCcId() {
		return customerCcId;
	}
	public List<DocumentInfo> getDocInfos() {
		return docInfos;
	}
	public void setDocInfos(List<DocumentInfo> docInfos) {
		this.docInfos = docInfos;
	}
	public void setCustomerCcId(Integer customerCcId) {
		this.customerCcId = customerCcId;
	}
	public Integer getCcType() {
		return ccType;
	}
	public void setCcType(Integer ccType) {
		this.ccType = ccType;
	}
	public String getCcHolderName() {
		return ccHolderName;
	}
	public void setCcHolderName(String ccHolderName) {
		this.ccHolderName = ccHolderName;
	}
	public String getCcNo() {
		return ccNo;
	}
	public void setCcNo(String ccNo) {
		this.ccNo = ccNo;
	}
	public String getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getCcCvv() {
		return ccCvv;
	}
	public void setCcCvv(String ccCvv) {
		this.ccCvv = ccCvv;
	}
	public String getCcIssuer() {
		return ccIssuer;
	}
	public void setCcIssuer(String ccIssuer) {
		this.ccIssuer = ccIssuer;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return Utilities.trim(email);
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
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
	public String getCcTypeName() {
		return ccTypeName;
	}
	public void setCcTypeName(String ccTypeName) {
		this.ccTypeName = ccTypeName;
	}
	public String getCcFirstName() {
		return Utilities.trim(ccFirstName);
	}
	public void setCcFirstName(String ccFirstName) {
		this.ccFirstName = ccFirstName;
	}
	public String getCcLastName() {		
		return Utilities.trim(ccLastName);
	}
	public void setCcLastName(String ccLastName) {
		this.ccLastName = ccLastName;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
	
	//[NTS1.0-anhndn]Jan 21, 2013A - Start 
	public Integer getDocVerifyStatus() {
		return docVerifyStatus;
	}
	public void setDocVerifyStatus(Integer docVerifyStatus) {
		this.docVerifyStatus = docVerifyStatus;
	}
	//[NTS1.0-anhndn]Jan 21, 2013A - End 
	
	public String getExpiredYear() {
		return expiredYear;
	}
	public void setExpiredYear(String expiredYear) {
		this.expiredYear = expiredYear;
	}
	public String getExpiredMonth() {
		return expiredMonth;
	}
	public void setExpiredMonth(String expiredMonth) {
		this.expiredMonth = expiredMonth;
	}
	public String getCcNoDisp() {
		return ccNoDisp;
	}
	public void setCcNoDisp(String ccNoDisp) {
		this.ccNoDisp = ccNoDisp;
	}
	public String getCcDriverNo() {
		return ccDriverNo;
	}
	public void setCcDriverNo(String ccDriverNo) {
		this.ccDriverNo = ccDriverNo;
	}
	/**
	 * @return the ccNoLastDigit
	 */
	public String getCcNoLastDigit() {
		return ccNoLastDigit;
	}
	/**
	 * @param ccNoLastDigit the ccNoLastDigit to set
	 */
	public void setCcNoLastDigit(String ccNoLastDigit) {
		this.ccNoLastDigit = ccNoLastDigit;
	}
	
}
