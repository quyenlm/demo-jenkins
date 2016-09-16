package phn.nts.ams.fe.model;
import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.ExchangerTransactionSearchCondition;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;

public class ExchangerModel extends BaseSocialModel {
	
	private BalanceInfo balanceInfo;
	private ExchangerSymbolInfo exchangerSymbolInfo;
	private List<ExchangerSymbolInfo> listExchangerSymbol;
	private List<ExchangerSymbolInfo> listExchangerSymbolHistory;
	private Map<Integer, String> mapStatus;
	private Map<Integer, String> mapStatusAll;
	private Map<String, String> mapType;
	private ExchangerTransactionSearchCondition exchangerTransactionSearchCondition; 
	private List<ExchangerTransactionSearchCondition> listExchangerTransactionSearchConditions;
	private Map<Integer, String> mapStatusRequest;
	private Map<Integer, String> mapStatusInprogress;
	private ExchangerTransactionSearchCondition bankInfo;
	
	

	public ExchangerSymbolInfo getExchangerSymbolInfo() {
		return exchangerSymbolInfo;
	}

	public void setExchangerSymbolInfo(ExchangerSymbolInfo exchangerSymbolInfo) {
		this.exchangerSymbolInfo = exchangerSymbolInfo;
	}

	/**
	 * @return the balanceInfo
	 */
	public BalanceInfo getBalanceInfo() {
		return balanceInfo;
	}

	/**
	 * @param balanceInfo the balanceInfo to set
	 */
	public void setBalanceInfo(BalanceInfo balanceInfo) {
		this.balanceInfo = balanceInfo;
	}
	public Map<Integer, String> getMapStatus() {
		return mapStatus;
	}

	public void setMapStatus(Map<Integer, String> mapStatus) {
		this.mapStatus = mapStatus;
	}

	public Map<String, String> getMapType() {
		return mapType;
	}

	public void setMapType(Map<String, String> mapType) {
		this.mapType = mapType;
	}

	public ExchangerTransactionSearchCondition getExchangerTransactionSearchCondition() {
		return exchangerTransactionSearchCondition;
	}

	public void setExchangerTransactionSearchCondition(ExchangerTransactionSearchCondition exchangerTransactionSearchCondition) {
		this.exchangerTransactionSearchCondition = exchangerTransactionSearchCondition;
	}

	public List<ExchangerTransactionSearchCondition> getListExchangerTransactionSearchConditions() {
		return listExchangerTransactionSearchConditions;
	}

	public void setListExchangerTransactionSearchConditions(List<ExchangerTransactionSearchCondition> listExchangerTransactionSearchConditions) {
		this.listExchangerTransactionSearchConditions = listExchangerTransactionSearchConditions;
	}

	public Map<Integer, String> getMapStatusRequest() {
		return mapStatusRequest;
	}

	public void setMapStatusRequest(Map<Integer, String> mapStatusRequest) {
		this.mapStatusRequest = mapStatusRequest;
	}

	public Map<Integer, String> getMapStatusInprogress() {
		return mapStatusInprogress;
	}

	public void setMapStatusInprogress(Map<Integer, String> mapStatusInprogress) {
		this.mapStatusInprogress = mapStatusInprogress;
	}

	public Map<Integer, String> getMapStatusAll() {
		return mapStatusAll;
	}

	public void setMapStatusAll(Map<Integer, String> mapStatusAll) {
		this.mapStatusAll = mapStatusAll;
	}

	public List<ExchangerSymbolInfo> getListExchangerSymbol() {
		return listExchangerSymbol;
	}

	public void setListExchangerSymbol(List<ExchangerSymbolInfo> listExchangerSymbol) {
		this.listExchangerSymbol = listExchangerSymbol;
	}

	public List<ExchangerSymbolInfo> getListExchangerSymbolHistory() {
		return listExchangerSymbolHistory;
	}

	public void setListExchangerSymbolHistory(
			List<ExchangerSymbolInfo> listExchangerSymbolHistory) {
		this.listExchangerSymbolHistory = listExchangerSymbolHistory;
	}

	public ExchangerTransactionSearchCondition getBankInfo() {
		return bankInfo;
	}

	public void setBankInfo(ExchangerTransactionSearchCondition bankInfo) {
		this.bankInfo = bankInfo;
	}


}
