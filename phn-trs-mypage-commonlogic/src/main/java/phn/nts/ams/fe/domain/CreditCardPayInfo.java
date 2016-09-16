package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;


public class CreditCardPayInfo implements Serializable{

	/**
	 * default serial version.
	 */
	private static final long serialVersionUID = 1L;
	private Integer creditcardId;
	private Integer cardType;
	private String cardTypeStr;
	private String cardHolderName;
	private String cardNumber;
	private String expiryDate;
	private String cvvNumber;
	private Integer customerId;
	private String firstName;
	private String lastName;
	private String countryCode;
	private String zipcode;
	private String state;
	private String city;
	private String street;
	private Integer countryId;
	private Timestamp regDate;
	private String email;
	private String phone;
	
	public Integer getCreditcardId() {
		return creditcardId;
	}
	public void setCreditcardId(Integer creditcardId) {
		this.creditcardId = creditcardId;
	}
	public Integer getCardType() {
		return cardType;
	}
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	public String getCardTypeStr() {
		return cardTypeStr;
	}
	public void setCardTypeStr(String cardTypeStr) {
		this.cardTypeStr = cardTypeStr;
	}
	public String getCardHolderName() {
		return cardHolderName;
	}
	public void setCardHolderName(String ownerName) {
		this.cardHolderName = ownerName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCvvNumber() {
		return cvvNumber;
	}
	public void setCvvNumber(String cvvNumber) {
		this.cvvNumber = cvvNumber;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
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
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}
	public String getDisp3Digit(){
		String disp3Digit = "";
		if(this.cardNumber!=null&&cardNumber.length()>4){
			disp3Digit = cardNumber.substring(cardNumber.length()-4);
		}
		return disp3Digit;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
