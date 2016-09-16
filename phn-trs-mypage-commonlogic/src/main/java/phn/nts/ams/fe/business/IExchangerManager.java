package phn.nts.ams.fe.business;

import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.ExchangerTransactionSearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsExchangerSymbol;
import phn.nts.ams.fe.domain.CashBalanceInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;

public interface IExchangerManager {
	
	public CashBalanceInfo getBalanceByCustomerId(String customerId);
	
	public List<ExchangerSymbolInfo> getExchangerSymbolByExchangerId(String exchangerId); 
	
	public boolean updateCurrentRate(List<ExchangerSymbolInfo> listInfos);
	
	public List<ExchangerTransactionSearchCondition> getExchangerHistory(ExchangerTransactionSearchCondition condition, PagingInfo pagingInfo);
	
	public int updateExchanger(List<ExchangerTransactionSearchCondition> list);
		
	public List<ExchangerSymbolInfo> getExchangerSymbolHistoryByExchangerId(String exchangerId, PagingInfo pagingInfo);
	
	public ExchangerTransactionSearchCondition getExchangerHistory(String transctionId);
	
	public Map<String, String> getMapExchanger(String wlCode, String currencyCode, String customerId);
	
	public Double getAmountAvailable(String currencyCode, String customerId);
	
	
}
