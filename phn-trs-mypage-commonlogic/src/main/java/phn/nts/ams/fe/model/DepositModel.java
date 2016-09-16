package phn.nts.ams.fe.model;

import java.util.List;
import java.util.Map;

import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.util.common.IConstants;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BjpInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.DepositInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.RateInfo;

public class DepositModel extends BaseSocialModel {
	private static final long serialVersionUID = 5779534964261998901L;
	private DepositInfo depositInfo;
	private List<AmsDeposit> listAmsDeposit;
	private BalanceInfo balanceInfo;	
	private Map<String, String> mapDepositMethod;
	private Map<String, String> mapServiceType;
	private Map<String, String> mapCreditCardPayment;
	private Map<String, String> mapCountry;
	private Map<String, String> mapExchanger;
	
	// option for payment method
	private String rdNetteller;
	private String rdPayza;
	private String rdAllcharge;
	private Integer paymentGateway;	
	private String rdLiberty;
	private String baseCurrencyCode; // currency code of AMS
	
	private List<CustomerEwalletInfo> listNeteller;
	private List<CustomerEwalletInfo> listLiberty;
	private List<CustomerEwalletInfo> listPayza;
	private List<CreditCardInfo> listCreditInfo;
	private List<ExchangerInfo> listExchanger;
	
	private NetellerInfo netellerInfo;
	private PayzaInfo payzaInfo;
	private BankTransferInfo bankTransferInfo;
	private CreditCardInfo creditCardInfo;
	private ExchangerInfo exchangerInfo;
	private RateInfo rateInfo;
	private RateInfo rateInfoCny;
	private LibertyInfo libertyInfo;
	//[NTS1.0-Quan.Le.Minh]Feb 20, 2013A - Start 
	private Map<String, String> mapPaymentSystem;
	//[NTS1.0-Quan.Le.Minh]Feb 20, 2013A - End
	private Map<String, String> mapCardType;
	//[NTS1.0-anhndn]Feb 27, 2013A - Start 
	private Map<String, String> mapLibertyAccess;
	private String libertyAccessMethod;
	public final String defaulLibertyAccess = IConstants.LIBERTY_PAYMENT.SCI;
	//[NTS1.0-anhndn]Feb 27, 2013A - End
	private Map<String, BalanceInfo> mapBalanceInfo;
	private Map<String, String> mapBjpBank;
	private String bjpBankCode="0008";
	private String bjpBankAmount;
	private Integer bjpMinAmount;
	private Integer bjpMaxAmount;
	private BjpInfo bjpInfo;
	/**
	 * @return the depositInfo
	 */
	public DepositInfo getDepositInfo() {
		return depositInfo;
	}
	/**
	 * @param depositInfo the depositInfo to set
	 */
	public void setDepositInfo(DepositInfo depositInfo) {
		this.depositInfo = depositInfo;
	}
	/**
	 * @return the listAmsDeposit
	 */
	public List<AmsDeposit> getListAmsDeposit() {
		return listAmsDeposit;
	}
	/**
	 * @param listAmsDeposit the listAmsDeposit to set
	 */
	public void setListAmsDeposit(List<AmsDeposit> listAmsDeposit) {
		this.listAmsDeposit = listAmsDeposit;
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
	/**
	 * @return the mapDepositMethod
	 */
	public Map<String, String> getMapDepositMethod() {
		return mapDepositMethod;
	}
	/**
	 * @param mapDepositMethod the mapDepositMethod to set
	 */
	public void setMapDepositMethod(Map<String, String> mapDepositMethod) {
		this.mapDepositMethod = mapDepositMethod;
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
	 * @return the rdNetteller
	 */
	public String getRdNetteller() {
		return rdNetteller;
	}
	/**
	 * @param rdNetteller the rdNetteller to set
	 */
	public void setRdNetteller(String rdNetteller) {
		this.rdNetteller = rdNetteller;
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
	 * @return the listNeteller
	 */
	public List<CustomerEwalletInfo> getListNeteller() {
		return listNeteller;
	}
	/**
	 * @param listNeteller the listNeteller to set
	 */
	public void setListNeteller(List<CustomerEwalletInfo> listNeteller) {
		this.listNeteller = listNeteller;
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
	 * @return the bankTransferInfo
	 */
	public BankTransferInfo getBankTransferInfo() {
		return bankTransferInfo;
	}
	/**
	 * @param bankTransferInfo the bankTransferInfo to set
	 */
	public void setBankTransferInfo(BankTransferInfo bankTransferInfo) {
		this.bankTransferInfo = bankTransferInfo;
	}
	/**
	 * @return the creditCardInfo
	 */
	public CreditCardInfo getCreditCardInfo() {
		return creditCardInfo;
	}
	/**
	 * @param creditCardInfo the creditCardInfo to set
	 */
	public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
		this.creditCardInfo = creditCardInfo;
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
	 * @return the rdAllcharge
	 */
	public String getRdAllcharge() {
		return rdAllcharge;
	}
	/**
	 * @param rdAllcharge the rdAllcharge to set
	 */
	public void setRdAllcharge(String rdAllcharge) {
		this.rdAllcharge = rdAllcharge;
	}
	/**
	 * @return the mapCountry
	 */
	public Map<String, String> getMapCountry() {
		return mapCountry;
	}
	/**
	 * @param mapCountry the mapCountry to set
	 */
	public void setMapCountry(Map<String, String> mapCountry) {
		this.mapCountry = mapCountry;
	}
	/**
	 * @return the listCreditInfo
	 */
	public List<CreditCardInfo> getListCreditInfo() {
		return listCreditInfo;
	}
	/**
	 * @param listCreditInfo the listCreditInfo to set
	 */
	public void setListCreditInfo(List<CreditCardInfo> listCreditInfo) {
		this.listCreditInfo = listCreditInfo;
	}
	/**
	 * @return the baseCurrencyCode
	 */
	public String getBaseCurrencyCode() {
		return baseCurrencyCode;
	}
	/**
	 * @param baseCurrencyCode the baseCurrencyCode to set
	 */
	public void setBaseCurrencyCode(String baseCurrencyCode) {
		this.baseCurrencyCode = baseCurrencyCode;
	}
	public List<ExchangerInfo> getListExchanger() {
		return listExchanger;
	}
	public void setListExchanger(List<ExchangerInfo> listExchanger) {
		this.listExchanger = listExchanger;
	}
	public ExchangerInfo getExchangerInfo() {
		return exchangerInfo;
	}
	public void setExchangerInfo(ExchangerInfo exchangerInfo) {
		this.exchangerInfo = exchangerInfo;
	}
	/**
	 * @return the mapExchanger
	 */
	public Map<String, String> getMapExchanger() {
		return mapExchanger;
	}
	/**
	 * @param mapExchanger the mapExchanger to set
	 */
	public void setMapExchanger(Map<String, String> mapExchanger) {
		this.mapExchanger = mapExchanger;
	}
	public LibertyInfo getLibertyInfo() {
		return libertyInfo;
	}
	public void setLibertyInfo(LibertyInfo libertyInfo) {
		this.libertyInfo = libertyInfo;
	}
	public List<CustomerEwalletInfo> getListLiberty() {
		return listLiberty;
	}
	public void setListLiberty(List<CustomerEwalletInfo> listLiberty) {
		this.listLiberty = listLiberty;
	}
	public String getRdLiberty() {
		return rdLiberty;
	}
	public void setRdLiberty(String rdLiberty) {
		this.rdLiberty = rdLiberty;
	}
	public Map<String, String> getMapPaymentSystem() {
		return mapPaymentSystem;
	}
	public void setMapPaymentSystem(Map<String, String> mapPaymentSystem) {
		this.mapPaymentSystem = mapPaymentSystem;
	}
	public Map<String, String> getMapLibertyAccess() {
		return mapLibertyAccess;
	}
	public void setMapLibertyAccess(Map<String, String> mapLibertyAccess) {
		this.mapLibertyAccess = mapLibertyAccess;
	}
	public String getLibertyAccessMethod() {
		return libertyAccessMethod;
	}
	public void setLibertyAccessMethod(String libertyAccessMethod) {
		this.libertyAccessMethod = libertyAccessMethod;
	}
	public RateInfo getRateInfoCny() {
		return rateInfoCny;
	}
	public void setRateInfoCny(RateInfo rateInfoCny) {
		this.rateInfoCny = rateInfoCny;
	}
	/**
	 * @return the mapBalanceInfo
	 */
	public Map<String, BalanceInfo> getMapBalanceInfo() {
		return mapBalanceInfo;
	}
	/**
	 * @param mapBalanceInfo the mapBalanceInfo to set
	 */
	public void setMapBalanceInfo(Map<String, BalanceInfo> mapBalanceInfo) {
		this.mapBalanceInfo = mapBalanceInfo;
	}
	/**
	 * @return the mapCardType
	 */
	public Map<String, String> getMapCardType() {
		return mapCardType;
	}
	/**
	 * @param mapCardType the mapCardType to set
	 */
	public void setMapCardType(Map<String, String> mapCardType) {
		this.mapCardType = mapCardType;
	}
	public Map<String, String> getMapBjpBank() {
		return mapBjpBank;
	}
	public void setMapBjpBank(Map<String, String> mapBjpBank) {
		this.mapBjpBank = mapBjpBank;
	}
	public String getBjpBankCode() {
		return bjpBankCode;
	}
	public void setBjpBankCode(String bjpBankCode) {
		this.bjpBankCode = bjpBankCode;
	}
	public String getBjpBankAmount() {
		return bjpBankAmount;
	}
	public void setBjpBankAmount(String bjpBankAmount) {
		this.bjpBankAmount = bjpBankAmount;
	}
	public Integer getBjpMinAmount() {
		return bjpMinAmount;
	}
	public void setBjpMinAmount(Integer bjpMinAmount) {
		this.bjpMinAmount = bjpMinAmount;
	}
	public BjpInfo getBjpInfo() {
		return bjpInfo;
	}
	public void setBjpInfo(BjpInfo bjpInfo) {
		this.bjpInfo = bjpInfo;
	}
	/**
	 * @return the bjpMaxAmount
	 */
	public Integer getBjpMaxAmount() {
		return bjpMaxAmount;
	}
	/**
	 * @param bjpMaxAmount the bjpMaxAmount to set
	 */
	public void setBjpMaxAmount(Integer bjpMaxAmount) {
		this.bjpMaxAmount = bjpMaxAmount;
	}

}
