package phn.nts.ams.fe.model;

import phn.nts.ams.fe.domain.TransferMoneyInfo;


public class TransferModel extends BaseSocialModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private TransferMoneyInfo transferMoneyInfo;
	private String currencyCode;
	private String wlCode;
	private String toAccountCurrencyCode;
	private String amount;
	private String convertedAmount;

	/**
	 * @return the transferMoneyInfo
	 */
	public TransferMoneyInfo getTransferMoneyInfo() {
		return transferMoneyInfo;
	}

	/**
	 * @param transferMoneyInfo the transferMoneyInfo to set
	 */
	public void setTransferMoneyInfo(TransferMoneyInfo transferMoneyInfo) {
		this.transferMoneyInfo = transferMoneyInfo;
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
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the convertedAmount
	 */
	public String getConvertedAmount() {
		return convertedAmount;
	}

	/**
	 * @param convertedAmount the convertedAmount to set
	 */
	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}

	
	/**
	 * @return the toAccountCurrencyCode
	 */
	public String getToAccountCurrencyCode() {
		return toAccountCurrencyCode;
	}

	/**
	 * @param toAccountCurrencyCode the toAccountCurrencyCode to set
	 */
	public void setToAccountCurrencyCode(String toAccountCurrencyCode) {
		this.toAccountCurrencyCode = toAccountCurrencyCode;
	}

	public String getWlCode() {
		return wlCode;
	}

	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}

	@Override
	public String toString() {
		return "TransferModel [transferMoneyInfo=" + transferMoneyInfo
				+ ", currencyCode=" + currencyCode + ", wlCode=" + wlCode
				+ ", toAccountCurrencyCode=" + toAccountCurrencyCode
				+ ", amount=" + amount + ", convertedAmount=" + convertedAmount
				+ "]";
	}
}
