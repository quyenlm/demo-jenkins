package phn.nts.ams.fe.business;

import java.math.BigDecimal;

import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CashBalanceInfo;
import phn.nts.ams.fe.domain.RateInfo;

public interface IBalanceManager {
	public CashBalanceInfo getCashBalanceInfo(String customerId, String currencyCode, Integer serviceType);
	public BalanceInfo getBalanceInfo(String customerId, Integer serviceType, String baseCurrency);
	public BalanceInfo getBalanceInfo(String customerId, String customerServiceId, Integer serviceType, String currencyCode);
	public String formatNumber(BigDecimal number, String currencyCode, String language);
	public String formatNumber(BigDecimal number, String currencyCode);
	public String getCurrencyCode(String currencyCode, String language);
	public BigDecimal getBalanceWithConvertRate(Double amount, String currencyCode, String baseCurrency);
	public BigDecimal getConvertRateOnFrontRate(String fromCurrency, String toCurrency, int defaultScale);
	public RateInfo getRateInfo(String fromCurrencyCode, String toCurrencyCode);
}
