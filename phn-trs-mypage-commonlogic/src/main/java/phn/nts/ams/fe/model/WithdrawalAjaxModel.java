
package phn.nts.ams.fe.model;

public class WithdrawalAjaxModel extends BaseSocialModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2131403621042958143L;
	private String withdrawalFee;
	private String formatWithdrawlFee;
	private String withdrawalMethod;
	private String withdrawalAmount;
	private String currencyCode;
	private String formatReceivedAmount;
	private String receivedAmount;
	private String message;
	/**
	 * @return the withdrawalFee
	 */
	public String getWithdrawalFee() {
		return withdrawalFee;
	}
	/**
	 * @param withdrawalFee the withdrawalFee to set
	 */
	public void setWithdrawalFee(String withdrawalFee) {
		this.withdrawalFee = withdrawalFee;
	}
	/**
	 * @return the withdrawalMethod
	 */
	public String getWithdrawalMethod() {
		return withdrawalMethod;
	}
	/**
	 * @param withdrawalMethod the withdrawalMethod to set
	 */
	public void setWithdrawalMethod(String withdrawalMethod) {
		this.withdrawalMethod = withdrawalMethod;
	}
	/**
	 * @return the withdrawalAmount
	 */
	public String getWithdrawalAmount() {
		return withdrawalAmount;
	}
	/**
	 * @param withdrawalAmount the withdrawalAmount to set
	 */
	public void setWithdrawalAmount(String withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
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
	 * @return the formatWithdrawlFee
	 */
	public String getFormatWithdrawlFee() {
		return formatWithdrawlFee;
	}
	/**
	 * @param formatWithdrawlFee the formatWithdrawlFee to set
	 */
	public void setFormatWithdrawlFee(String formatWithdrawlFee) {
		this.formatWithdrawlFee = formatWithdrawlFee;
	}
	/**
	 * @return the formatReceivedAmount
	 */
	public String getFormatReceivedAmount() {
		return formatReceivedAmount;
	}
	/**
	 * @param formatReceivedAmount the formatReceivedAmount to set
	 */
	public void setFormatReceivedAmount(String formatReceivedAmount) {
		this.formatReceivedAmount = formatReceivedAmount;
	}
	/**
	 * @return the receivedAmount
	 */
	public String getReceivedAmount() {
		return receivedAmount;
	}
	/**
	 * @param receivedAmount the receivedAmount to set
	 */
	public void setReceivedAmount(String receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
}
