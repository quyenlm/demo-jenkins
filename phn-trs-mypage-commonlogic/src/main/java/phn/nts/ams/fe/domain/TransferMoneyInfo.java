package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransferMoneyInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6526715081075348295L;
	private String transferMoneyId;
	private String customerId;
	private Integer transferFrom;
	private Integer transferTo;
	private String transferFromName;
	private String transferToName;
	private Double transferMoney;
	private BigDecimal rate;
	private String currencyCode;
	private Double convertedAmount;
	private String wlRefId;
	private String tranferAcceptDate;
	private Timestamp tranferAcceptDateTime;
	private Integer status;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String fromCurrencyCode;
	private String toCurrencyCode;
	private String wlCode;
	private Double destinationAmount;
	private String destinationCurrencyCode;
	private String sourceId;
	private Integer sourceType;
	private Double creditAmount;
	private String tranferCompleteDate;
	private Timestamp tranferCompleteDateTime;
	private BigDecimal netDepositAmount;
	private String remark;
	
	private CustomerServicesInfo amsServicesInfo;
	private CustomerServicesInfo fromServicesInfo;
	private CustomerServicesInfo toServicesInfo;
	
	public BigDecimal getNetDepositAmount() {
		return netDepositAmount;
	}
	public void setNetDepositAmount(BigDecimal netDepositAmount) {
		this.netDepositAmount = netDepositAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return the wlCode
	 */
	public String getWlCode() {
		return wlCode;
	}
	/**
	 * @param wlCode the wlCode to set
	 */
	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}
	/**
	 * @return the transferMoneyId
	 */
	public String getTransferMoneyId() {
		return transferMoneyId;
	}
	/**
	 * @param transferMoneyId the transferMoneyId to set
	 */
	public void setTransferMoneyId(String transferMoneyId) {
		this.transferMoneyId = transferMoneyId;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the transferFrom
	 */
	public Integer getTransferFrom() {
		return transferFrom;
	}
	/**
	 * @param transferFrom the transferFrom to set
	 */
	public void setTransferFrom(Integer transferFrom) {
		this.transferFrom = transferFrom;
	}
	/**
	 * @return the transferTo
	 */
	public Integer getTransferTo() {
		return transferTo;
	}
	/**
	 * @param transferTo the transferTo to set
	 */
	public void setTransferTo(Integer transferTo) {
		this.transferTo = transferTo;
	}
	
	/**
	 * @return the transferMoney
	 */
	public Double getTransferMoney() {
		return transferMoney;
	}
	/**
	 * @param transferMoney the transferMoney to set
	 */
	public void setTransferMoney(Double transferMoney) {
		this.transferMoney = transferMoney;
	}
	/**
	 * @return the wlRefId
	 */
	public String getWlRefId() {
		return wlRefId;
	}
	/**
	 * @param wlRefId the wlRefId to set
	 */
	public void setWlRefId(String wlRefId) {
		this.wlRefId = wlRefId;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the activeFlag
	 */
	public Integer getActiveFlg() {
		return activeFlg;
	}
	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlg(Integer activeFlg) {
		this.activeFlg = activeFlg;
	}
	/**
	 * @return the inputDate
	 */
	public Timestamp getInputDate() {
		return inputDate;
	}
	/**
	 * @param inputDate the inputDate to set
	 */
	public void setInputDate(Timestamp inputDate) {
		this.inputDate = inputDate;
	}
	/**
	 * @return the updateDate
	 */
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * @return the tranferAcceptDate
	 */
	public String getTranferAcceptDate() {
		return tranferAcceptDate;
	}
	/**
	 * @param tranferAcceptDate the tranferAcceptDate to set
	 */
	public void setTranferAcceptDate(String tranferAcceptDate) {
		this.tranferAcceptDate = tranferAcceptDate;
	}
	/**
	 * @return the tranferAcceptDateTime
	 */
	public Timestamp getTranferAcceptDateTime() {
		return tranferAcceptDateTime;
	}
	/**
	 * @param tranferAcceptDateTime the tranferAcceptDateTime to set
	 */
	public void setTranferAcceptDateTime(Timestamp tranferAcceptDateTime) {
		this.tranferAcceptDateTime = tranferAcceptDateTime;
	}
	/**
	 * @return the fromCurrencyCode
	 */
	public String getFromCurrencyCode() {
		return fromCurrencyCode;
	}
	/**
	 * @param fromCurrencyCode the fromCurrencyCode to set
	 */
	public void setFromCurrencyCode(String fromCurrencyCode) {
		this.fromCurrencyCode = fromCurrencyCode;
	}
	/**
	 * @return the toCurrencyCode
	 */
	public String getToCurrencyCode() {
		return toCurrencyCode;
	}
	/**
	 * @param toCurrencyCode the toCurrencyCode to set
	 */
	public void setToCurrencyCode(String toCurrencyCode) {
		this.toCurrencyCode = toCurrencyCode;
	}
	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
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
	 * @return the transferFromName
	 */
	public String getTransferFromName() {
		return transferFromName;
	}
	/**
	 * @param transferFromName the transferFromName to set
	 */
	public void setTransferFromName(String transferFromName) {
		this.transferFromName = transferFromName;
	}
	/**
	 * @return the transferToName
	 */
	public String getTransferToName() {
		return transferToName;
	}
	/**
	 * @param transferToName the transferToName to set
	 */
	public void setTransferToName(String transferToName) {
		this.transferToName = transferToName;
	}
	/**
	 * @return the convertedAmount
	 */
	public Double getConvertedAmount() {
		return convertedAmount;
	}
	/**
	 * @param convertedAmount the convertedAmount to set
	 */
	public void setConvertedAmount(Double convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	/**
	 * @return the destinationAmount
	 */
	public Double getDestinationAmount() {
		return destinationAmount;
	}
	/**
	 * @param destinationAmount the destinationAmount to set
	 */
	public void setDestinationAmount(Double destinationAmount) {
		this.destinationAmount = destinationAmount;
	}
	/**
	 * @return the destinationCurrencyCode
	 */
	public String getDestinationCurrencyCode() {
		return destinationCurrencyCode;
	}
	/**
	 * @param destinationCurrencyCode the destinationCurrencyCode to set
	 */
	public void setDestinationCurrencyCode(String destinationCurrencyCode) {
		this.destinationCurrencyCode = destinationCurrencyCode;
	}
	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}
	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	/**
	 * @return the sourceType
	 */
	public Integer getSourceType() {
		return sourceType;
	}
	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 * @return the creditAmount
	 */
	public Double getCreditAmount() {
		return creditAmount;
	}
	/**
	 * @param creditAmount the creditAmount to set
	 */
	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}
	/**
	 * @return the tranferCompleteDate
	 */
	public String getTranferCompleteDate() {
		return tranferCompleteDate;
	}
	/**
	 * @param tranferCompleteDate the tranferCompleteDate to set
	 */
	public void setTranferCompleteDate(String tranferCompleteDate) {
		this.tranferCompleteDate = tranferCompleteDate;
	}
	/**
	 * @return the tranferCompleteDateTime
	 */
	public Timestamp getTranferCompleteDateTime() {
		return tranferCompleteDateTime;
	}
	/**
	 * @param tranferCompleteDateTime the tranferCompleteDateTime to set
	 */
	public void setTranferCompleteDateTime(Timestamp tranferCompleteDateTime) {
		this.tranferCompleteDateTime = tranferCompleteDateTime;
	}
	
	@Override
	public String toString() {
		return "TransferMoneyInfo [transferMoneyId=" + transferMoneyId
				+ ", customerId=" + customerId + ", transferFrom="
				+ transferFrom + ", transferTo=" + transferTo
				+ ", transferFromName=" + transferFromName
				+ ", transferToName=" + transferToName + ", transferMoney="
				+ transferMoney + ", rate=" + rate + ", currencyCode="
				+ currencyCode + ", convertedAmount=" + convertedAmount
				+ ", wlRefId=" + wlRefId + ", tranferAcceptDate="
				+ tranferAcceptDate + ", tranferAcceptDateTime="
				+ tranferAcceptDateTime + ", status=" + status + ", activeFlg="
				+ activeFlg + ", inputDate=" + inputDate + ", updateDate="
				+ updateDate + ", fromCurrencyCode=" + fromCurrencyCode
				+ ", toCurrencyCode=" + toCurrencyCode + ", wlCode=" + wlCode
				+ ", destinationAmount=" + destinationAmount
				+ ", destinationCurrencyCode=" + destinationCurrencyCode
				+ ", sourceId=" + sourceId + ", sourceType=" + sourceType
				+ ", creditAmount=" + creditAmount + ", tranferCompleteDate="
				+ tranferCompleteDate + ", tranferCompleteDateTime="
				+ tranferCompleteDateTime + ", netDepositAmount="
				+ netDepositAmount + ", remark=" + remark + "]";
	}
	public CustomerServicesInfo getFromServicesInfo() {
		return fromServicesInfo;
	}
	public void setFromServicesInfo(CustomerServicesInfo fromServicesInfo) {
		this.fromServicesInfo = fromServicesInfo;
	}
	public CustomerServicesInfo getToServicesInfo() {
		return toServicesInfo;
	}
	public void setToServicesInfo(CustomerServicesInfo toServicesInfo) {
		this.toServicesInfo = toServicesInfo;
	}
	public CustomerServicesInfo getAmsServicesInfo() {
		return amsServicesInfo;
	}
	public void setAmsServicesInfo(CustomerServicesInfo amsServicesInfo) {
		this.amsServicesInfo = amsServicesInfo;
	}
}
