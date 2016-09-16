package phn.nts.ams.fe.domain;

import java.io.Serializable;

/**
 * @description information of Payza 
 * @version NTS - AMS
 * @CrBy HuyenMT
 * @CrDate Jun 20, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class PayzaInfo extends PaymentMethodInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8615514154427098665L;
	private String emailAddress;
	private String apiPassword;	
	
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
		
	/**
	 * @return the apiPassword
	 */
	public String getApiPassword() {
		return apiPassword;
	}
	/**
	 * @param apiPassword the apiPassword to set
	 */
	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}
	

}
