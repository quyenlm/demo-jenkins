package phn.nts.ams.fe.domain;

import java.util.List;

public class LiveRateInfo {
	private String symbols;
	private List<SymbolBidAskInfo> symbolBidAskInfo;
	
	
	public List<SymbolBidAskInfo> getSymbolBidAskInfo() {
		return symbolBidAskInfo;
	}

	public void setSymbolBidAskInfo(List<SymbolBidAskInfo> symbolBidAskInfo) {
		this.symbolBidAskInfo = symbolBidAskInfo;
	}

	public String getSymbols() {
		return symbols;
	}

	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}
	
}
