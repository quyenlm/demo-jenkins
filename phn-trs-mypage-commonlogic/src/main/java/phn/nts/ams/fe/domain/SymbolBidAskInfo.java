package phn.nts.ams.fe.domain;

import java.math.BigDecimal;

public class SymbolBidAskInfo {
	private String symbol;
	private BigDecimal bidRatio;
	private BigDecimal askRatio;
	private BigDecimal bidValue;
	private BigDecimal askValue;
	
	public BigDecimal getBidValue() {
		return bidValue;
	}
	public void setBidValue(BigDecimal bidValue) {
		this.bidValue = bidValue;
	}
	public BigDecimal getAskValue() {
		return askValue;
	}
	public void setAskValue(BigDecimal askValue) {
		this.askValue = askValue;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getBidRatio() {
		return bidRatio;
	}
	public void setBidRatio(BigDecimal bidRatio) {
		this.bidRatio = bidRatio;
	}
	public BigDecimal getAskRatio() {
		return askRatio;
	}
	public void setAskRatio(BigDecimal askRatio) {
		this.askRatio = askRatio;
	}
	
}
