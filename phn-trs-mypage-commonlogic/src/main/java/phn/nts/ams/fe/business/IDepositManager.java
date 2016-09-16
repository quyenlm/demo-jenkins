package phn.nts.ams.fe.business;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.SysAppDate;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BjpDepositInfo;
import phn.nts.ams.fe.domain.CountryInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.DepositInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.RateInfo;

public interface IDepositManager {
	public String depositBankTransfer(DepositInfo depositInfo);

	public List<AmsDeposit> getListDepositHistory(String customerId, PagingInfo pagingInfo);

	public String getBankInfo(String wlCode);

	public Map<Integer, List<CustomerEwalletInfo>> getListCustomerEwalletInfo(String customerId);

	public List<CreditCardInfo> getListCustomerCreditCardInfo(String customerId);

	public CreditCardInfo getCreditCardInfoByCcId(Integer ccId, String publicKey);

	public Integer depositPayonlineSuccess(String transactionId, String depositId, String customerId, String wlCode, String currencyCode, Integer status);

	public Integer depositNetpaySuccess(String transactionId, String depositId, String customerId, String wlCode, String currencyCode, Integer status);

	public Integer depositUpdateStatus(String depositId, String customerId, String baseCurrency, String currencyCode, Integer status, String reason, String wlCode, String errorCode, String fundKeyBalDescription, String fundKeyCrdDescription, Integer paymentMethod);

	public Integer depositNeteller(NetellerInfo netellerInfo, String publicKey);

	public Integer depositPayza(PayzaInfo payzaInfo);

	public String depositNetpay(CreditCardInfo creditCardInfo);

	public CustomerEwalletInfo getNetellerInfo(String customerId, String accountId, String publicKey);

	public CustomerEwalletInfo getPayzaInfo(String customerId, String email, String publicKey);

	public Integer depositBankTransfer(BankTransferInfo bankTransferInfo);

	public String depositPayonlineSystem(CreditCardInfo creditCardInfo, BigDecimal amount, String publicKey);

	public LinkedHashMap<String, String> getListCountry();

	public Map<Integer, CountryInfo> getListCountryInfo();

	public CreditCardInfo getCustomerCreditInfo(Integer customerCreditId);

	public AmsCashBalance updateAmsCashBalance(String customerId, String currencyCode, Integer serviceType, Double amount, Double creditAmount, Boolean deductFlag);

	public void insertCashFlow(String transactionId, String customerId, String customerServiceId, SysAppDate amsAppDate, Integer cashFlowType, Double amount, Integer sourceType, String currencyCode, Integer serviceType, Double balance, Double convertRate);

	public void insertCashFlow(String transactionId, String customerId, String customerServiceId, SysAppDate amsAppDate, Integer cashFlowType, Double amount, Integer sourceType, Integer serviceType, Double balance, Double rate, String currencyCode);

	public List<ExchangerInfo> getListExchangers(String wlCode, String currencyCode, String customerId);

	public ExchangerSymbolInfo getExchangerSymbol(String exchangerId);

	public ExchangerInfo getExchanger(String exchangerId);

	public Integer depositExchanger(ExchangerInfo exchangerInfo);

	public String depositLiberty(LibertyInfo libertyInfo);

	public Integer depositLibertySuccess(String depositId, String customerId, LibertyInfo libertyInfo);

	public Integer depositNetpaySilentPost(String depositId, String customerId, CreditCardInfo creditCardInfo);
	
	//[NTS1.0-anhndn]Feb 26, 2013A - Start 
	public String depositLibertySCI(LibertyInfo libertyInfo);
	public Integer updateDepositRef(String depositId, String ewalletAccountNumber);
	//[NTS1.0-anhndn]Feb 26, 2013A - End
	
	public RateInfo getLastestRate(String currencyPair);
	public BigDecimal getFrontRate(String fromCurrencyCode, String toCurrencyCode);
	
	public BigDecimal getConvertRate(String fromCurrencyCode, String toCurrencyCode);
	
	public RateInfo getRateInfo(String fromCurrencyCode, String toCurrencyCode);
	public Map<String, String> getMapPaymentGW(Integer cardType);
	
	public Map<Integer, List<CustomerEwalletInfo>> getListCustomerEwalletInfo(String customerId, String publicKey);
	
	public List<CreditCardInfo> getListCustomerCreditCardInfo(String customerId, String publicKey);
	
	public CustomerEwalletInfo getNettellerInfo(Integer ewalletId, String publicKey);
	
	public BigDecimal rounding(BigDecimal amount, String currencyCode);
	public AmsWhitelabelConfig getAmsWhitelabelConfig(String configKey, String wlCode);
	

	//tungpv  add start
	public AmsCashflow insertBjpCashFlow(AmsCashflow amsCashflow);

	public AmsCashBalance updateBjpCashBalance(String customerId,String  currencyCode,Integer serviceType,Double amount, Double tranFee);
	public void updateBjpUploadDeposit(String depositId, String errorCode,String csvFileName) ;
	public void updateBjpDeposit(String depositId,String tranResonCode,Double depositFee);
	public void updateBjpDepositRef(String rEMARKS_3, String tRAN_ID,String tRAN_DIGEST);
	public void updateBjpDepositFail(AmsDeposit dep,Integer status,String errorCode);
	public void updateBjpUploadDepositFail(String depositId, String errorCode, String csvFileName);
	public AmsDeposit getBjpDeposit(String depositId);
	public boolean sendMailBjpDeposit(AmsDeposit deposit,int mode,String methodName) throws Exception;
	public boolean sendMailBjpWarning(List<BjpDepositInfo> listDepositWarning,String fileName) throws Exception;
	public AmsDepositRef keshicomiUpdateDepositRef(AmsDepositRef amsDepositRef);
	
	//tungpv  add end

	public void sendMailAbnormalUpdateDepositStatus(String customerId, String customerName, String depositId, String currentSts, String receivedSts);
	
	public AmsDepositRef getDepositRefById(String refId);
}
