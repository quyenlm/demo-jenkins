package phn.nts.ams.fe.domain;

public class NetellerResponseInfo {
	private String approval;
	private String errorMessage;
	private String errorCode;
	private String transactionId;
	/**
	 * @return the approval
	 */
	public String getApproval() {
		return approval;
	}
	/**
	 * @param approval the approval to set
	 */
	public void setApproval(String approval) {
		this.approval = approval;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
}
