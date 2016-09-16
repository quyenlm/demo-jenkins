package phn.nts.ams.fe.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import phn.com.nts.ams.web.condition.AmsFeIBKickBackHistoryCondition;
import phn.com.nts.db.entity.AmsIbKickback;
import phn.nts.ams.fe.domain.*;


public class IBModel extends BaseSocialModel {
	private static final long serialVersionUID = 1L;
	private String currencyCode;
	private String amount;
	private String clientCustomerId;	
	private Integer type;
	private String transactionType;
	private Integer serviceType;
	private Integer serviceTypeDeposit;
	private Integer serviceTypeWithdrawal;
	private String serviceName;
	private String remark;
	private String customerName;
	private String emailAddress;
	private WithdrawalInfo withdrawalInfo;
	private DepositInfo depositInfo;
	private IbTransferMoneyInfo transferInfo;
	private CustomerInfo customerInfo;
	private List<IbClientCustomer> listIbClientCustomers;
	private List<AmsIbKickback> kickbackList;
	private String errorMsg;
	private AmsFeIBKickBackHistoryCondition amsFeIBKickBackHistoryCondition;
	private IbInfo ibInfo;	
	private CustomerServicesInfo customerServicesInfo;
	private BigDecimal totalKickBack;
	private BigDecimal totalVolumn;
	private List<String> listSymbol;
	private String sServiceType;
	private Map<String, String> mapCurrency;
	private Map<Integer, String> mapServiceType;
	private Map<Integer, String> mapMethod;
	private List<String> listCurrencyCode;

	private Map<String, String> mapServiceDeposit;
	private Map<String, String> mapServiceWithdrawal;
	private Map<String, String> mapTransferFrom; 
	private Map<String, String> mapTransferTo;
	private Map<Integer, String> mapAllServiceType;
    private Map<Integer, CountryInfo> mapCountry;
	
	private String pattern;

	/**
	 * @return the ibInfo
	 */
	public IbInfo getIbInfo() {
		return ibInfo;
	}

	/**
	 * @param ibInfo the ibInfo to set
	 */
	public void setIbInfo(IbInfo ibInfo) {
		this.ibInfo = ibInfo;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the clientCustomerId
	 */
	public String getClientCustomerId() {
		return clientCustomerId;
	}

	/**
	 * @param clientCustomerId the clientCustomerId to set
	 */
	public void setClientCustomerId(String clientCustomerId) {
		this.clientCustomerId = clientCustomerId;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the serviceType
	 */
//	public Integer getServiceType() {
//		return serviceType;
//	}
//
//	/**
//	 * @param serviceType the serviceType to set
//	 */
//	public void setServiceType(Integer serviceType) {
//		this.serviceType = serviceType;
//	}

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
	


	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}


	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<IbClientCustomer> getListIbClientCustomers() {
		return listIbClientCustomers;
	}

	public void setListIbClientCustomers(
			List<IbClientCustomer> listIbClientCustomers) {
		this.listIbClientCustomers = listIbClientCustomers;
	}

	/**
	 * @return the amsFeIBKickBackHistoryCondition
	 */
	public AmsFeIBKickBackHistoryCondition getAmsFeIBKickBackHistoryCondition() {
		return amsFeIBKickBackHistoryCondition;
	}

	/**
	 * @param amsFeIBKickBackHistoryCondition the amsFeIBKickBackHistoryCondition to set
	 */
	public void setAmsFeIBKickBackHistoryCondition(
			AmsFeIBKickBackHistoryCondition amsFeIBKickBackHistoryCondition) {
		this.amsFeIBKickBackHistoryCondition = amsFeIBKickBackHistoryCondition;
	}

	/**
	 * @return the kickbackList
	 */
	public List<AmsIbKickback> getKickbackList() {
		return kickbackList;
	}

	/**
	 * @param kickbackList the kickbackList to set
	 */
	public void setKickbackList(List<AmsIbKickback> kickbackList) {
		this.kickbackList = kickbackList;
	}

	/**
	 * @return the customerServicesInfo
	 */
	public CustomerServicesInfo getCustomerServicesInfo() {
		return customerServicesInfo;
	}

	/**
	 * @param customerServicesInfo the customerServicesInfo to set
	 */
	public void setCustomerServicesInfo(CustomerServicesInfo customerServicesInfo) {
		this.customerServicesInfo = customerServicesInfo;
	}

	/**
	 * @return the totalVolumn
	 */
	public BigDecimal getTotalVolumn() {
		return totalVolumn;
	}

	/**
	 * @param totalVolumn the totalVolumn to set
	 */
	public void setTotalVolumn(BigDecimal totalVolumn) {
		this.totalVolumn = totalVolumn;
	}

	/**
	 * @return the totalKickBack
	 */
	public BigDecimal getTotalKickBack() {
		return totalKickBack;
	}

	/**
	 * @param totalKickBack the totalKickBack to set
	 */
	public void setTotalKickBack(BigDecimal totalKickBack) {
		this.totalKickBack = totalKickBack;
	}

	

	public String getsServiceType() {
		return sServiceType;
	}

	public void setsServiceType(String sServiceType) {
		this.sServiceType = sServiceType;
	}

	/**
	 * @return the listSymbol
	 */
	public List<String> getListSymbol() {
		return listSymbol;
	}

	/**
	 * @param listSymbol the listSymbol to set
	 */
	public void setListSymbol(List<String> listSymbol) {
		this.listSymbol = listSymbol;
	}

	/**
	 * @return the mapCurrency
	 */
	public Map<String, String> getMapCurrency() {
		return mapCurrency;
	}

	/**
	 * @param mapCurrency the mapCurrency to set
	 */
	public void setMapCurrency(Map<String, String> mapCurrency) {
		this.mapCurrency = mapCurrency;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the mapServiceType
	 */
	public Map<Integer, String> getMapServiceType() {
		return mapServiceType;
	}

	/**
	 * @param mapServiceType the mapServiceType to set
	 */
	public void setMapServiceType(Map<Integer, String> mapServiceType) {
		this.mapServiceType = mapServiceType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the listCurrencyCode
	 */
	public List<String> getListCurrencyCode() {
		return listCurrencyCode;
	}

	/**
	 * @param listCurrencyCode the listCurrencyCode to set
	 */
	public void setListCurrencyCode(List<String> listCurrencyCode) {
		this.listCurrencyCode = listCurrencyCode;
	}

	public Map<Integer, String> getMapMethod() {
		return mapMethod == null ? new TreeMap<Integer, String>() : mapMethod;
	}

	public void setMapMethod(Map<Integer, String> mapMethod) {
		this.mapMethod = mapMethod;
	}

	public Map<String, String> getMapTransferTo() {
		return mapTransferTo;
	}

	public void setMapTransferTo(Map<String, String> mapTransferTo) {
		this.mapTransferTo = mapTransferTo;
	}

	public Map<String, String> getMapTransferFrom() {
		return mapTransferFrom;
	}

	public void setMapTransferFrom(Map<String, String> mapTransferFrom) {
		this.mapTransferFrom = mapTransferFrom;
	}

	public Map<String, String> getMapServiceDeposit() {
		return mapServiceDeposit;
	}

	public void setMapServiceDeposit(Map<String, String> mapServiceDeposit) {
		this.mapServiceDeposit = mapServiceDeposit;
	}

	public Map<String, String> getMapServiceWithdrawal() {
		return mapServiceWithdrawal;
	}

	public void setMapServiceWithdrawal(Map<String, String> mapServiceWithdrawal) {
		this.mapServiceWithdrawal = mapServiceWithdrawal;
	}

	public Integer getServiceTypeWithdrawal() {
		return serviceTypeWithdrawal;
	}

	public void setServiceTypeWithdrawal(Integer serviceTypeWithdrawal) {
		this.serviceTypeWithdrawal = serviceTypeWithdrawal;
	}

	public Integer getServiceTypeDeposit() {
		return serviceTypeDeposit;
	}

	public void setServiceTypeDeposit(Integer serviceTypeDeposit) {
		this.serviceTypeDeposit = serviceTypeDeposit;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public void setTransferInfo(IbTransferMoneyInfo transferInfo) {
		this.transferInfo = transferInfo;
	}

	public IbTransferMoneyInfo getTransferInfo() {
		return this.transferInfo;
	}

	public Map<Integer, String> getMapAllServiceType() {
		return mapAllServiceType;
	}

	public void setMapAllServiceType(Map<Integer, String> mapAllServiceType) {
		this.mapAllServiceType = mapAllServiceType;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


    public Map<Integer, CountryInfo> getMapCountry() {
        return mapCountry;
    }

    public void setMapCountry(Map<Integer, CountryInfo> mapCountry) {
        this.mapCountry = mapCountry;
    }
}
