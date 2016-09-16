package phn.nts.ams.fe.domain;

import java.sql.Timestamp;

public class KickbackInfo {
	private String customerId;
	private String fullName;
	private Integer serviceType;
	private String orderId;
	private String orderCustomerId;
	private Timestamp orderDatetime;
	private String orderSymbolCd;
	private Double orderVolume;
	private Integer kickbackType;
	private Double kickbackAmount;
	private String kickbackCurrencyCode;
	private Double kickbackConvertRate;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderCustomerId() {
		return orderCustomerId;
	}
	public void setOrderCustomerId(String orderCustomerId) {
		this.orderCustomerId = orderCustomerId;
	}
	public Timestamp getOrderDatetime() {
		return orderDatetime;
	}
	public void setOrderDatetime(Timestamp orderDatetime) {
		this.orderDatetime = orderDatetime;
	}
	public String getOrderSymbolCd() {
		return orderSymbolCd;
	}
	public void setOrderSymbolCd(String orderSymbolCd) {
		this.orderSymbolCd = orderSymbolCd;
	}
	public Double getOrderVolume() {
		return orderVolume;
	}
	public void setOrderVolume(Double orderVolume) {
		this.orderVolume = orderVolume;
	}
	public Integer getKickbackType() {
		return kickbackType;
	}
	public void setKickbackType(Integer kickbackType) {
		this.kickbackType = kickbackType;
	}
	public Double getKickbackAmount() {
		return kickbackAmount;
	}
	public void setKickbackAmount(Double kickbackAmount) {
		this.kickbackAmount = kickbackAmount;
	}
	public String getKickbackCurrencyCode() {
		return kickbackCurrencyCode;
	}
	public void setKickbackCurrencyCode(String kickbackCurrencyCode) {
		this.kickbackCurrencyCode = kickbackCurrencyCode;
	}
	public Double getKickbackConvertRate() {
		return kickbackConvertRate;
	}
	public void setKickbackConvertRate(Double kickbackConvertRate) {
		this.kickbackConvertRate = kickbackConvertRate;
	}
	public Integer getActiveFlg() {
		return activeFlg;
	}
	public void setActiveFlg(Integer activeFlg) {
		this.activeFlg = activeFlg;
	}
	public Timestamp getInputDate() {
		return inputDate;
	}
	public void setInputDate(Timestamp inputDate) {
		this.inputDate = inputDate;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
