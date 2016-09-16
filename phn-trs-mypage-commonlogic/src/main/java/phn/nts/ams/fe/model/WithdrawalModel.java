package phn.nts.ams.fe.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.util.common.StringUtil;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.WithdrawalInfo;
import phn.nts.ams.fe.domain.WithdrawalRuleInfo;

public class WithdrawalModel extends BaseSocialModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<String, String> listCountry;
	private List<WithdrawalInfo> listAmsWithdrawal;
	private List<String> errorMessages;
	private BalanceInfo balanceAmsInfo;
	private BalanceInfo balanceFxInfo;
	private BalanceInfo balanceBoInfo;
	private BalanceInfo balanceCopyTradeInfo;
	private BalanceInfo balanceFxNtdInfo;
	
	private WithdrawalInfo withdrawalInfo;
	private CustomerEwalletInfo customerEwalletInfo;
	private BigDecimal cashBalance;
	private String amount;
	private String withdrawalId;
	private Map<String, String> mapPaymentMethod;
	private Map<String, String> mapServiceType;
	private Map<String, String> mapExchanger;
	//[NatureForex1.0-Mai.Thu.Huyen]Oct 15, 2012A - Start 
	private Map<String, String> mapCreditCardPayment;
	private String withdrawalFee;
	private String receivedAmount;
	private Integer paymentGateway;
	//[NatureForex1.0-Mai.Thu.Huyen]Oct 15, 2012A - End
	private List<CustomerBankInfo> listCustomerBankInfo;
	private List<CustomerEwalletInfo> listPaypal;
	private List<CustomerEwalletInfo> listNetteller;
	private List<CustomerEwalletInfo> listPayza;
	private List<CustomerEwalletInfo> listLiberty;
	private ExchangerSymbolInfo exchangerSymbolInfo;
	private ExchangerInfo exchangerInfo;
	private CreditCardInfo creditCardInfo;
	
	private String rdNeteller;
	private String rdPayza;
	private String rdBankwire;
	private String rdExchanger;
	private String rdLiberty;
	
	private NetellerInfo netellerInfo;
	private PayzaInfo payzaInfo;
	private CustomerBankInfo customerBankInfo;
	private LibertyInfo libertyInfo;//libertyReservedInfo
	private RateInfo rateInfo;	
	private Integer withdrawalRuleCase;
	private WithdrawalRuleInfo withdrawalRuleInfo;
	private Integer fromServiceType;

	public WithdrawalRuleInfo getWithdrawalRuleInfo() {
		return withdrawalRuleInfo;
	}

	public void setWithdrawalRuleInfo(WithdrawalRuleInfo withdrawalRuleInfo) {
		this.withdrawalRuleInfo = withdrawalRuleInfo;
	}

	public Integer getWithdrawalRuleCase() {
		return withdrawalRuleCase;
	}

	public void setWithdrawalRuleCase(Integer withdrawalRuleCase) {
		this.withdrawalRuleCase = withdrawalRuleCase;
	}

	public BalanceInfo getBalanceCopyTradeInfo() {
		return balanceCopyTradeInfo;
	}

	public void setBalanceCopyTradeInfo(BalanceInfo balanceCopyTradeInfo) {
		this.balanceCopyTradeInfo = balanceCopyTradeInfo;
	}

	/**
	 * @return the listCountry
	 */
	public LinkedHashMap<String, String> getListCountry() {
		return listCountry;
	}

	/**
	 * @param listCountry the listCountry to set
	 */
	public void setListCountry(LinkedHashMap<String, String> listCountry) {
		this.listCountry = listCountry;
	}

	
	/**
	 * @return the errorMessages
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * @param errorMessages the errorMessages to set
	 */
	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	

	/**
	 * @return the withdrawalInfo
	 */
	public WithdrawalInfo getWithdrawalInfo() {
		return withdrawalInfo;
	}

	/**
	 * @param withdrawalInfo the withdrawalInfo to set
	 */
	public void setWithdrawalInfo(WithdrawalInfo withdrawalInfo) {
		this.withdrawalInfo = withdrawalInfo;
	}

	/**
	 * @return the cashBalance
	 */
	public BigDecimal getCashBalance() {
		return cashBalance;
	}

	/**
	 * @param cashBalance the cashBalance to set
	 */
	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		if(!StringUtil.isEmpty(amount))
			amount = amount.replace(",", "");
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the withdrawalId
	 */
	public String getWithdrawalId() {
		return withdrawalId;
	}

	/**
	 * @param withdrawalId the withdrawalId to set
	 */
	public void setWithdrawalId(String withdrawalId) {
		this.withdrawalId = withdrawalId;
	}

	/**
	 * @return the mapPaymentMethod
	 */
	public Map<String, String> getMapPaymentMethod() {
		return mapPaymentMethod;
	}

	/**
	 * @param mapPaymentMethod the mapPaymentMethod to set
	 */
	public void setMapPaymentMethod(Map<String, String> mapPaymentMethod) {
		this.mapPaymentMethod = mapPaymentMethod;
	}


	/**
	 * @return the customerEwalletInfo
	 */
	public CustomerEwalletInfo getCustomerEwalletInfo() {
		return customerEwalletInfo;
	}

	/**
	 * @param customerEwalletInfo the customerEwalletInfo to set
	 */
	public void setCustomerEwalletInfo(CustomerEwalletInfo customerEwalletInfo) {
		this.customerEwalletInfo = customerEwalletInfo;
	}

	
	

	/**
	 * @return the rdPayza
	 */
	public String getRdPayza() {
		return rdPayza;
	}

	/**
	 * @param rdPayza the rdPayza to set
	 */
	public void setRdPayza(String rdPayza) {
		this.rdPayza = rdPayza;
	}

	/**
	 * @return the rdBankwire
	 */
	public String getRdBankwire() {
		return rdBankwire;
	}

	/**
	 * @param rdBankwire the rdBankwire to set
	 */
	public void setRdBankwire(String rdBankwire) {
		this.rdBankwire = rdBankwire;
	}

	/**
	 * @return the netellerInfo
	 */
	public NetellerInfo getNetellerInfo() {
		return netellerInfo;
	}

	/**
	 * @param netellerInfo the netellerInfo to set
	 */
	public void setNetellerInfo(NetellerInfo netellerInfo) {
		this.netellerInfo = netellerInfo;
	}

	/**
	 * @return the payzaInfo
	 */
	public PayzaInfo getPayzaInfo() {
		return payzaInfo;
	}

	/**
	 * @param payzaInfo the payzaInfo to set
	 */
	public void setPayzaInfo(PayzaInfo payzaInfo) {
		this.payzaInfo = payzaInfo;
	}

	/**
	 * @return the mapServiceType
	 */
	public Map<String, String> getMapServiceType() {
		return mapServiceType;
	}

	/**
	 * @param mapServiceType the mapServiceType to set
	 */
	public void setMapServiceType(Map<String, String> mapServiceType) {
		this.mapServiceType = mapServiceType;
	}

	/**
	 * @return the listPaypal
	 */
	public List<CustomerEwalletInfo> getListPaypal() {
		return listPaypal;
	}

	/**
	 * @param listPaypal the listPaypal to set
	 */
	public void setListPaypal(List<CustomerEwalletInfo> listPaypal) {
		this.listPaypal = listPaypal;
	}

	/**
	 * @return the listNetteller
	 */
	public List<CustomerEwalletInfo> getListNetteller() {
		return listNetteller;
	}

	/**
	 * @param listNetteller the listNetteller to set
	 */
	public void setListNetteller(List<CustomerEwalletInfo> listNetteller) {
		this.listNetteller = listNetteller;
	}

	/**
	 * @return the listPayza
	 */
	public List<CustomerEwalletInfo> getListPayza() {
		return listPayza;
	}

	/**
	 * @param listPayza the listPayza to set
	 */
	public void setListPayza(List<CustomerEwalletInfo> listPayza) {
		this.listPayza = listPayza;
	}

	/**
	 * @return the listAmsWithdrawal
	 */
	public List<WithdrawalInfo> getListAmsWithdrawal() {
		return listAmsWithdrawal;
	}

	/**
	 * @param listAmsWithdrawal the listAmsWithdrawal to set
	 */
	public void setListAmsWithdrawal(List<WithdrawalInfo> listAmsWithdrawal) {
		this.listAmsWithdrawal = listAmsWithdrawal;
	}

	/**
	 * @return the listCustomerBankInfo
	 */
	public List<CustomerBankInfo> getListCustomerBankInfo() {
		return listCustomerBankInfo;
	}

	/**
	 * @param listCustomerBankInfo the listCustomerBankInfo to set
	 */
	public void setListCustomerBankInfo(List<CustomerBankInfo> listCustomerBankInfo) {
		this.listCustomerBankInfo = listCustomerBankInfo;
	}

	/**
	 * @return the customerBankInfo
	 */
	public CustomerBankInfo getCustomerBankInfo() {
		return customerBankInfo;
	}

	/**
	 * @param customerBankInfo the customerBankInfo to set
	 */
	public void setCustomerBankInfo(CustomerBankInfo customerBankInfo) {
		this.customerBankInfo = customerBankInfo;
	}

	/**
	 * @return the rdNeteller
	 */
	public String getRdNeteller() {
		return rdNeteller;
	}

	/**
	 * @param rdNeteller the rdNeteller to set
	 */
	public void setRdNeteller(String rdNeteller) {
		this.rdNeteller = rdNeteller;
	}

	/**
	 * @return the rateInfo
	 */
	public RateInfo getRateInfo() {
		return rateInfo;
	}

	/**
	 * @param rateInfo the rateInfo to set
	 */
	public void setRateInfo(RateInfo rateInfo) {
		this.rateInfo = rateInfo;
	}

	/**
	 * @return the balanceAmsInfo
	 */
	public BalanceInfo getBalanceAmsInfo() {
		return balanceAmsInfo;
	}

	/**
	 * @param balanceAmsInfo the balanceAmsInfo to set
	 */
	public void setBalanceAmsInfo(BalanceInfo balanceAmsInfo) {
		this.balanceAmsInfo = balanceAmsInfo;
	}

	/**
	 * @return the balanceFxInfo
	 */
	public BalanceInfo getBalanceFxInfo() {
		return balanceFxInfo;
	}

	/**
	 * @param balanceFxInfo the balanceFxInfo to set
	 */
	public void setBalanceFxInfo(BalanceInfo balanceFxInfo) {
		this.balanceFxInfo = balanceFxInfo;
	}

	/**
	 * @return the balanceBoInfo
	 */
	public BalanceInfo getBalanceBoInfo() {
		return balanceBoInfo;
	}

	/**
	 * @param balanceBoInfo the balanceBoInfo to set
	 */
	public void setBalanceBoInfo(BalanceInfo balanceBoInfo) {
		this.balanceBoInfo = balanceBoInfo;
	}

	public Map<String, String> getMapExchanger() {
		return mapExchanger;
	}

	public void setMapExchanger(Map<String, String> mapExchanger) {
		this.mapExchanger = mapExchanger;
	}

	/**
	 * @return the exchangerSymbolInfo
	 */
	public ExchangerSymbolInfo getExchangerSymbolInfo() {
		return exchangerSymbolInfo;
	}

	/**
	 * @param exchangerSymbolInfo the exchangerSymbolInfo to set
	 */
	public void setExchangerSymbolInfo(ExchangerSymbolInfo exchangerSymbolInfo) {
		this.exchangerSymbolInfo = exchangerSymbolInfo;
	}

	/**
	 * @return the exchangerInfo
	 */
	public ExchangerInfo getExchangerInfo() {
		return exchangerInfo;
	}

	/**
	 * @param exchangerInfo the exchangerInfo to set
	 */
	public void setExchangerInfo(ExchangerInfo exchangerInfo) {
		this.exchangerInfo = exchangerInfo;
	}

	/**
	 * @return the rdExchanger
	 */
	public String getRdExchanger() {
		return rdExchanger;
	}

	/**
	 * @param rdExchanger the rdExchanger to set
	 */
	public void setRdExchanger(String rdExchanger) {
		this.rdExchanger = rdExchanger;
	}

	public CreditCardInfo getCreditCardInfo() {
		return creditCardInfo;
	}

	public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
		this.creditCardInfo = creditCardInfo;
	}

	/**
	 * @return the mapCreditCardPayment
	 */
	public Map<String, String> getMapCreditCardPayment() {
		return mapCreditCardPayment;
	}

	/**
	 * @param mapCreditCardPayment the mapCreditCardPayment to set
	 */
	public void setMapCreditCardPayment(Map<String, String> mapCreditCardPayment) {
		this.mapCreditCardPayment = mapCreditCardPayment;
	}

	/**
	 * @return the withdrawalFee
	 */
	public String getWithdrawalFee() {
		return withdrawalFee;
	}

	/**
	 * @param withdrawalFee the withdrawalFee to set
	 */
	public void setWithdrawalFee(String withdrawalFee) {
		this.withdrawalFee = withdrawalFee;
	}

	/**
	 * @return the receivedAmount
	 */
	public String getReceivedAmount() {
		return receivedAmount;
	}

	/**
	 * @param receivedAmount the receivedAmount to set
	 */
	public void setReceivedAmount(String receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	/**
	 * @return the paymentGateway
	 */
	public Integer getPaymentGateway() {
		return paymentGateway;
	}

	/**
	 * @param paymentGateway the paymentGateway to set
	 */
	public void setPaymentGateway(Integer paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	/**
	 * @return the rdLiberty
	 */
	public String getRdLiberty() {
		return rdLiberty;
	}

	/**
	 * @param rdLiberty the rdLiberty to set
	 */
	public void setRdLiberty(String rdLiberty) {
		this.rdLiberty = rdLiberty;
	}


	/**
	 * @return the listLiberty
	 */
	public List<CustomerEwalletInfo> getListLiberty() {
		return listLiberty;
	}

	/**
	 * @param listLiberty the listLiberty to set
	 */
	public void setListLiberty(List<CustomerEwalletInfo> listLiberty) {
		this.listLiberty = listLiberty;
	}

	public LibertyInfo getLibertyInfo() {
		return libertyInfo;
	}

	public void setLibertyInfo(LibertyInfo libertyInfo) {
		this.libertyInfo = libertyInfo;
	}

	public BalanceInfo getBalanceFxNtdInfo() {
		return balanceFxNtdInfo;
	}

	public void setBalanceFxNtdInfo(BalanceInfo balanceFxNtdInfo) {
		this.balanceFxNtdInfo = balanceFxNtdInfo;
	}

	public Integer getFromServiceType() {
		return fromServiceType;
	}

	public void setFromServiceType(Integer fromServiceType) {
		this.fromServiceType = fromServiceType;
	}
}
