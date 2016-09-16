package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class NetellerPayInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer paymentgwId;
	private Integer customerID;
	private String beneficiaryName;
	private String accountID;
	private String secureID;
	private Timestamp regDate;
	
	
	public String getAccountID() {
		return accountID;
	}
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}
	public String getSecureID() {
		return secureID;
	}
	public void setSecureID(String secureID) {
		this.secureID = secureID;
	}
	public Integer getCustomerID() {
		return customerID;
	}
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}
	public Integer getPaymentgwId() {
		return paymentgwId;
	}
	public void setPaymentgwId(Integer paymentgwId) {
		this.paymentgwId = paymentgwId;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}
	public String getLast3Digit(){
		String last3Digit = "";
		try {
			if (secureID != null) {
				last3Digit = secureID.substring(secureID.length()-3);
			}
		} catch (Exception e) {
		}
		return last3Digit;
	}
	
}
