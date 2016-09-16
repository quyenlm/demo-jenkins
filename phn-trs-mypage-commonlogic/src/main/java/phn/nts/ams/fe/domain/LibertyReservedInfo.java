package phn.nts.ams.fe.domain;

public class LibertyReservedInfo extends PaymentMethodInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accountNumber;
	private String secureId;

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
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
