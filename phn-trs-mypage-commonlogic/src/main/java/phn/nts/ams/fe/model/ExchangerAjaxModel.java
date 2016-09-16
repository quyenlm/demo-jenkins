
package phn.nts.ams.fe.model;

import java.math.BigDecimal;

public class ExchangerAjaxModel extends BaseModel {
	
	private String exchangerId;
	private String rate;
	private BigDecimal amount;
	private String convertedAmount;
	private String bankInfo;
	private String displayRateFormat;
	private String displayWithdrawalAmount;
	private String displayConvertedAmount;
	private String amountStr;
	
	public String getExchangerId() {
		return exchangerId;
	}

	public void setExchangerId(String exchangerId) {
		this.exchangerId = exchangerId;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getConvertedAmount() {
		return convertedAmount;
	}

	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}

	public String getBankInfo() {
		return bankInfo;
	}

	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}

	/**
	 * @return the displayRateFormat
	 */
	public String getDisplayRateFormat() {
		return displayRateFormat;
	}

	/**
	 * @param displayRateFormat the displayRateFormat to set
	 */
	public void setDisplayRateFormat(String displayRateFormat) {
		this.displayRateFormat = displayRateFormat;
	}

	public String getDisplayWithdrawalAmount() {
		return displayWithdrawalAmount;
	}

	public void setDisplayWithdrawalAmount(String displayWithdrawalAmount) {
		this.displayWithdrawalAmount = displayWithdrawalAmount;
	}

	public String getDisplayConvertedAmount() {
		return displayConvertedAmount;
	}

	public void setDisplayConvertedAmount(String displayConvertedAmount) {
		this.displayConvertedAmount = displayConvertedAmount;
	}

	public String getAmountStr() {
		return amountStr;
	}

	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
	}	
}
