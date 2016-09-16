package phn.nts.ams.fe.domain;

import java.math.BigDecimal;

import phn.com.nts.util.log.Logit;



public class AllChargeResponseInfo {
	public static final String TYPE = "Type";
	public static final String RET_CODE="retCode";
	public static final String TRANSACTION_ID = "TransactionID";
	public static final String AMOUNT="Amount";
	public static final String INSTALLMENTS= "Installments";
	public static final String FIRSTNAME = "FirstName";
	public static final String SHIPPING_ADDRESS = "ShippingAddress";
	public static final String CITY = "City";
	public static final String ZIP = "Zip";
	public static final String PHONE = "Phone";
	public static final String LASTNAME = "LastName";
	public static final String EMAIL = "Email";
	public static final String MERCHANTDATA = "MerchantData";			
	
	private static final Logit LOG = Logit.getInstance(AllChargeResponseInfo.class);
	private Integer returnCode;
	private String syncType;
	private BigDecimal amount;
	private String installments;
	private String firstName;
	private String lastName;
	private String shippingAddress;
	private String city;
	private String zip;
	private String phone;
	private String email;
	private String transactionId;
	private String merchantData;
	/**
	 * @return the returnCode
	 */
	public Integer getReturnCode() {
		return returnCode;
	}
	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}
	/**
	 * @return the syncType
	 */
	public String getSyncType() {
		return syncType;
	}
	/**
	 * @param syncType the syncType to set
	 */
	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the installments
	 */
	public String getInstallments() {
		return installments;
	}
	/**
	 * @param installments the installments to set
	 */
	public void setInstallments(String installments) {
		this.installments = installments;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the shippingAddress
	 */
	public String getShippingAddress() {
		return shippingAddress;
	}
	/**
	 * @param shippingAddress the shippingAddress to set
	 */
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	/**
	 * @return the merchantData
	 */
	public String getMerchantData() {
		return merchantData;
	}
	/**
	 * @param merchantData the merchantData to set
	 */
	public void setMerchantData(String merchantData) {
		this.merchantData = merchantData;
	}
	
	public void writeLog() {
		LOG.info("Type=" + syncType + ", retCode=" + returnCode + ", TransactionID=" + transactionId +  ", Amount=" + amount + ", Installments=" + installments + ", FirstName="  + firstName + ", LastName=" + lastName + ", ShippingAddress=" + shippingAddress + ", City=" + city + ", Zip=" + zip + ", Phone=" + phone + ", Email=" + email + ", MerchantData=" + merchantData);
	}
}	
