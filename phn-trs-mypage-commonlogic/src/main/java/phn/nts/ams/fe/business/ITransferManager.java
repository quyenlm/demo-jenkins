package phn.nts.ams.fe.business;

import java.math.BigDecimal;

import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.model.TransferModel;

public interface ITransferManager {
	public CustomerInfo getCustomerInfo(String customerId);
	
	public Integer registerTransferMoney(TransferMoneyInfo transferMoneyInfo, String currencyCode);
	public Integer transferMoney(TransferMoneyInfo transferMoneyInfo) throws Exception;
	
	/**
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jan 21, 2016
	 * @MdDate
	 * @deprecated Not safe transaction. AMS_TRANFER_MONEY record will loss if exception occur when commit transaction
	 */
	public Integer transferMoney(TransferMoneyInfo transferMoneyInfo, String currencyCode);
	
	public CurrencyInfo getCurrencyInfo(String currencyCode);

    public BigDecimal getConvertRateOnFrontRate(String fromCurrency, String toCurrency);

    public BigDecimal getConvertRateOnFrontRate(String fromCurrency, String toCurrency, int defaultScale);

	public void loadAdditionalFxData(TransferModel transferModel);
	
	public Integer changeDissolveStatusAkazan(String akazanStatus,String customerId);
	
	public void sendMailDissolveStatusAkazan(String customerId, Integer serviceType);
	
	public boolean checkTransferFlagOnMasterData(String configKey, String wlCode);
	
	public boolean validateBoCustomerStatus(String customerId);
	
}
