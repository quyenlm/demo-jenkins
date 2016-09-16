package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class RateInfo implements Serializable {
	private BigDecimal rate;
	private String symbolName;
	private Integer rateType;
	private BigDecimal totalAfterConvert;
	private String totalExchangeRate;
	private String exchangeRate;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal convertRate;
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
	 * @return the rateType
	 */
	public Integer getRateType() {
		return rateType;
	}
	/**
	 * @param rateType the rateType to set
	 */
	public void setRateType(Integer rateType) {
		this.rateType = rateType;
	}
	/**
	 * @return the totalAfterConvert
	 */
	public BigDecimal getTotalAfterConvert() {
		return totalAfterConvert;
	}
	/**
	 * @param totalAfterConvert the totalAfterConvert to set
	 */
	public void setTotalAfterConvert(BigDecimal totalAfterConvert) {
		this.totalAfterConvert = totalAfterConvert;
	}
	/**
	 * @return the exchangeRate
	 */
	public String getExchangeRate() {
		return exchangeRate;
	}
	/**
	 * @param exchangeRate the exchangeRate to set
	 */
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	/**
	 * @return the totalExchangeRate
	 */
	public String getTotalExchangeRate() {
		return totalExchangeRate;
	}
	/**
	 * @param totalExchangeRate the totalExchangeRate to set
	 */
	public void setTotalExchangeRate(String totalExchangeRate) {
		this.totalExchangeRate = totalExchangeRate;
	}
	/**
	 * @return the bid
	 */
	public BigDecimal getBid() {
		return bid;
	}
	/**
	 * @param bid the bid to set
	 */
	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}
	/**
	 * @return the ask
	 */
	public BigDecimal getAsk() {
		return ask;
	}
	/**
	 * @param ask the ask to set
	 */
	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}
	/**
	 * @return the convertRate
	 */
	public BigDecimal getConvertRate() {
		return convertRate;
	}
	/**
	 * @param convertRate the convertRate to set
	 */
	public void setConvertRate(BigDecimal convertRate) {
		this.convertRate = convertRate;
	}
	
}
