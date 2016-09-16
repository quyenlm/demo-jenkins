package phn.nts.ams.fe.domain;

import java.io.Serializable;

public class IbTransferMoneyInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ibId;
	private String customerId;
	private Integer serviceTypeFrom;
	private Integer serviceTypeTo;
	private String currencyFrom;
	private String currencyTo;
	private String amountTransfer;
	private String note;
	private String convertedAmount;
	private String convertedRate;
	private String convertedCurrency;
	
//	private String convertedAmountDisp;
	private String amountDisp;
//	private String convertedRateDisp;
	
	private Integer type;
//	private String serviceTypeFromDisp;
//	private String serviceTypeToDisp;
	private String customerName;
	private String customerEmail;
	
	public String getIbId() {
		return ibId;
	}
	public void setIbId(String ibId) {
		this.ibId = ibId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getServiceTypeFrom() {
		return serviceTypeFrom;
	}
	public void setServiceTypeFrom(Integer serviceTypeFrom) {
		this.serviceTypeFrom = serviceTypeFrom;
	}
	public Integer getServiceTypeTo() {
		return serviceTypeTo;
	}
	public void setServiceTypeTo(Integer serviceTypeTo) {
		this.serviceTypeTo = serviceTypeTo;
	}
	public String getCurrencyFrom() {
		return currencyFrom;
	}
	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}
	public String getCurrencyTo() {
		return currencyTo;
	}
	public void setCurrencyTo(String currencyTo) {
		this.currencyTo = currencyTo;
	}
	public String getAmountTransfer() {
		return amountTransfer;
	}
	public void setAmountTransfer(String amountTransfer) {
		this.amountTransfer = amountTransfer;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getConvertedAmount() {
		return convertedAmount;
	}
	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	public String getConvertedRate() {
		return convertedRate;
	}
	public void setConvertedRate(String convertedRate) {
		this.convertedRate = convertedRate;
	}
	public String getConvertedCurrency() {
		return convertedCurrency;
	}
	public void setConvertedCurrency(String convertedCurrency) {
		this.convertedCurrency = convertedCurrency;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public String getAmountDisp() {
		return amountDisp;
	}
	public void setAmountDisp(String amountDisp) {
		this.amountDisp = amountDisp;
	}
}
