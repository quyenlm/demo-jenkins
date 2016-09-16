package phn.nts.ams.fe.domain;

import java.math.BigDecimal;

public class CopyChartInfo {
	private BigDecimal maxRangeValue = new BigDecimal(0);
	private BigDecimal minRangeValue = new BigDecimal(0);
	private BigDecimal numberOfTicks = new BigDecimal(0);
	private String currencyCode;
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public BigDecimal getNumberOfTicks() {
		return numberOfTicks;
	}
	public void setNumberOfTicks(BigDecimal numberOfTicks) {
		this.numberOfTicks = numberOfTicks;
	}
	public BigDecimal getMaxRangeValue() {
		return maxRangeValue;
	}
	public void setMaxRangeValue(BigDecimal maxRangeValue) {
		this.maxRangeValue = maxRangeValue;
	}
	public BigDecimal getMinRangeValue() {
		return minRangeValue;
	}
	public void setMinRangeValue(BigDecimal minRangeValue) {
		this.minRangeValue = minRangeValue;
	}

	 
}
