package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class IbInfo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String customerId;	
	private Integer ibType;
	private String ibLink;
	private Long accountTotal;
	private String currencyCode;
	
	private Double kickbackTotal;
	private Integer ibClientactiveFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String wlCode;
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
	 * @return the ibType
	 */
	public Integer getIbType() {
		return ibType;
	}
	/**
	 * @param ibType the ibType to set
	 */
	public void setIbType(Integer ibType) {
		this.ibType = ibType;
	}
	/**
	 * @return the ibLink
	 */
	public String getIbLink() {
		return ibLink;
	}
	/**
	 * @param ibLink the ibLink to set
	 */
	public void setIbLink(String ibLink) {
		this.ibLink = ibLink;
	}
	/**
	 * @return the ibClientactiveFlg
	 */
	public Integer getIbClientactiveFlg() {
		return ibClientactiveFlg;
	}
	/**
	 * @param ibClientactiveFlg the ibClientactiveFlg to set
	 */
	public void setIbClientactiveFlg(Integer ibClientactiveFlg) {
		this.ibClientactiveFlg = ibClientactiveFlg;
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
	 * @return the accountTotal
	 */
	public Long getAccountTotal() {
		return accountTotal;
	}
	/**
	 * @param accountTotal the accountTotal to set
	 */
	public void setAccountTotal(Long accountTotal) {
		this.accountTotal = accountTotal;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public Double getKickbackTotal() {
		return kickbackTotal;
	}
	public void setKickbackTotal(Double kickbackTotal) {
		this.kickbackTotal = kickbackTotal;
	}
}
