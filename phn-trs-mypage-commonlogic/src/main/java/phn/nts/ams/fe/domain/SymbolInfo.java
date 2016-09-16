package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class SymbolInfo implements Serializable {
	 private String symbolCd;
     private String symbolName;
     private Integer symbolType;
     private Integer symbolDecimal;
     private Integer symbolRound;
     private String contractCurrencyCode;
     private String counterCurrencyCode;
     private Short displayOrder;
     private BigDecimal spread;
     private String originalSymbolCd;
     private Timestamp startTime;
     private Timestamp endTime;
     private Long unitLot;
     private Long unitLotDl;
     private Integer dlDigit;
     private BigDecimal pipSize;
     private BigDecimal minHedgeAmt;
     private Integer activeFlg;
     private Timestamp inputDate;
     private Timestamp updateDate;
     
	/**
	 * @return the symbolCd
	 */
	public String getSymbolCd() {
		return symbolCd;
	}
	/**
	 * @param symbolCd the symbolCd to set
	 */
	public void setSymbolCd(String symbolCd) {
		this.symbolCd = symbolCd;
	}
	/**
	 * @return the symbolName
	 */
	public String getSymbolName() {
		return symbolName;
	}
	/**
	 * @param symbolName the symbolName to set
	 */
	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}
	/**
	 * @return the symbolType
	 */
	public Integer getSymbolType() {
		return symbolType;
	}
	/**
	 * @param symbolType the symbolType to set
	 */
	public void setSymbolType(Integer symbolType) {
		this.symbolType = symbolType;
	}
	/**
	 * @return the symbolDecimal
	 */
	public Integer getSymbolDecimal() {
		return symbolDecimal;
	}
	/**
	 * @param symbolDecimal the symbolDecimal to set
	 */
	public void setSymbolDecimal(Integer symbolDecimal) {
		this.symbolDecimal = symbolDecimal;
	}
	/**
	 * @return the symbolRound
	 */
	public Integer getSymbolRound() {
		return symbolRound;
	}
	/**
	 * @param symbolRound the symbolRound to set
	 */
	public void setSymbolRound(Integer symbolRound) {
		this.symbolRound = symbolRound;
	}
	/**
	 * @return the contractCurrencyCode
	 */
	public String getContractCurrencyCode() {
		return contractCurrencyCode;
	}
	/**
	 * @param contractCurrencyCode the contractCurrencyCode to set
	 */
	public void setContractCurrencyCode(String contractCurrencyCode) {
		this.contractCurrencyCode = contractCurrencyCode;
	}
	/**
	 * @return the counterCurrencyCode
	 */
	public String getCounterCurrencyCode() {
		return counterCurrencyCode;
	}
	/**
	 * @param counterCurrencyCode the counterCurrencyCode to set
	 */
	public void setCounterCurrencyCode(String counterCurrencyCode) {
		this.counterCurrencyCode = counterCurrencyCode;
	}
	/**
	 * @return the displayOrder
	 */
	public Short getDisplayOrder() {
		return displayOrder;
	}
	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(Short displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	/**
	 * @return the activeFlg
	 */
	public Integer getActiveFlg() {
		return activeFlg;
	}
	/**
	 * @param activeFlg the activeFlg to set
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
	 * @return the originalSymbolCd
	 */
	public String getOriginalSymbolCd() {
		return originalSymbolCd;
	}
	/**
	 * @param originalSymbolCd the originalSymbolCd to set
	 */
	public void setOriginalSymbolCd(String originalSymbolCd) {
		this.originalSymbolCd = originalSymbolCd;
	}
	public BigDecimal getSpread() {
		return spread;
	}
	public void setSpread(BigDecimal spread) {
		this.spread = spread;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public Long getUnitLot() {
		return unitLot;
	}
	public void setUnitLot(Long unitLot) {
		this.unitLot = unitLot;
	}
	public Long getUnitLotDl() {
		return unitLotDl;
	}
	public void setUnitLotDl(Long unitLotDl) {
		this.unitLotDl = unitLotDl;
	}
	public Integer getDlDigit() {
		return dlDigit;
	}
	public void setDlDigit(Integer dlDigit) {
		this.dlDigit = dlDigit;
	}
	public BigDecimal getPipSize() {
		return pipSize;
	}
	public void setPipSize(BigDecimal pipSize) {
		this.pipSize = pipSize;
	}
	public BigDecimal getMinHedgeAmt() {
		return minHedgeAmt;
	}
	public void setMinHedgeAmt(BigDecimal minHedgeAmt) {
		this.minHedgeAmt = minHedgeAmt;
	}
    
}
