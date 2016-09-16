package phn.nts.ams.fe.domain;

import java.io.Serializable;

/**
 * @description information of Liberty 
 * @version NTS - AMS
 * @CrBy NamTD
 * @CrDate Jun 20, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class LibertyInfo extends PaymentMethodInfo implements Serializable{
	private static final long serialVersionUID = -955458245113296027L;
	/**
	 * 
	 */
	private String accountNumber;
	private String apiName;
	private String securityWord;
	private String comment;
	
	//[NTS1.0-anhndn]Feb 27, 2013A - Start 
	private String accessMethod;
	//[NTS1.0-anhndn]Feb 27, 2013A - End

	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getSecurityWord() {
		return securityWord;
	}
	public void setSecurityWord(String securityWord) {
		this.securityWord = securityWord;
	}
	public String getAccessMethod() {
		return accessMethod;
	}
	public void setAccessMethod(String accessMethod) {
		this.accessMethod = accessMethod;
	}
	

}
