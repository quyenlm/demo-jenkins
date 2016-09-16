package phn.nts.ams.fe.domain;


public class NetellerInfo extends PaymentMethodInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -149714673577152459L;
	private String accountId;
	private String secureId;
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the secureId
	 */
	public String getSecureId() {
		return secureId;
	}
	/**
	 * @param secureId the secureId to set
	 */
	public void setSecureId(String secureId) {
		this.secureId = secureId;
	}	
	
	
	
	
	
}
