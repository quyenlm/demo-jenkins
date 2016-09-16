
package phn.nts.ams.fe.model;

import java.util.Map;

import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.RateInfo;

/**
 * @description Account Ajax Model
 * @version TDSBO1.0
 * @CrBy Administrator
 * @CrDate Aug 4, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class AccountAjaxModel extends BaseModel {
	private String customerServicesId;	
	private String customerId;
	private String errorMsg;
	private String serviceTypeId;
	//[NatureForex1.0-HuyenMT]Aug 31, 2012A - Start 
	private String accountServiceTypeId;
	private String accountCurrencyCode;
	private Map<String, String> mapFromServiceType;	
	private Map<String, String> mapToServiceType;
	private String captchaResponse;
	private Integer decimalFormatCurrencyCode;
	//[NatureForex1.0-HuyenMT]Aug 31, 2012A - End
	private String currencyCode;
	private String baseCurrencyCode;
	private String counterCurrencyCode;
	private RateInfo rateInfo;
	private String maxAmount;
	private String minAmount;
	private String maxWithdrawal;
	private String minWithdrawal;
	private BalanceInfo balanceInfo;
	private String balance;
	private String amountAvailable;
	private String messageId;
	private String dateTime;
	
	// [NTS1.0-DuyenNT]Mar 4, 2013 - Start
	private BalanceInfo balanceAmsInfo = null;
	private BalanceInfo balanceBoInfo = null;
	private BalanceInfo balanceFxInfo = null;
	private BalanceInfo balanceScInfo = null;
	private Double total;
	// [NTS1.0-DuyenNT]Mar 4, 2013 - End
	
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the customerServicesId
	 */
	public String getCustomerServicesId() {
		return customerServicesId;
	}

	/**
	 * @param customerServicesId the customerServicesId to set
	 */
	public void setCustomerServicesId(String customerServicesId) {
		this.customerServicesId = customerServicesId;
	}

	private CustomerServicesInfo customerServicesInfo;
	private CustomerInfo customerInfo;

	/**
	 * @return the customerInfo
	 */
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
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

	/**
	 * @return the serviceTypeId
	 */
	public String getServiceTypeId() {
		return serviceTypeId;
	}

	/**
	 * @param serviceTypeId the serviceTypeId to set
	 */
	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
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

	/**
	 * @return the counterCurrencyCode
	 */
	public String getCounterCurrencyCode() {
		return counterCurrencyCode;
	}

	/**
	 * @param counterCurrencyCode the counterCurrencyCode to set
	 */
	public void setCounterCurrencyCode(String counterCurrencyCode) {
		this.counterCurrencyCode = counterCurrencyCode;
	}

	/**
	 * @return the maxAmount
	 */
	public String getMaxAmount() {
		return maxAmount;
	}

	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * @return the minAmount
	 */
	public String getMinAmount() {
		return minAmount;
	}

	/**
	 * @param minAmount the minAmount to set
	 */
	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
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
	 * @return the accountServiceTypeId
	 */
	public String getAccountServiceTypeId() {
		return accountServiceTypeId;
	}

	/**
	 * @param accountServiceTypeId the accountServiceTypeId to set
	 */
	public void setAccountServiceTypeId(String accountServiceTypeId) {
		this.accountServiceTypeId = accountServiceTypeId;
	}

	/**
	 * @return the accountCurrencyCode
	 */
	public String getAccountCurrencyCode() {
		return accountCurrencyCode;
	}

	/**
	 * @param accountCurrencyCode the accountCurrencyCode to set
	 */
	public void setAccountCurrencyCode(String accountCurrencyCode) {
		this.accountCurrencyCode = accountCurrencyCode;
	}

	/**
	 * @return the mapFromServiceType
	 */
	public Map<String, String> getMapFromServiceType() {
		return mapFromServiceType;
	}

	/**
	 * @param mapFromServiceType the mapFromServiceType to set
	 */
	public void setMapFromServiceType(Map<String, String> mapFromServiceType) {
		this.mapFromServiceType = mapFromServiceType;
	}

	/**
	 * @return the mapToServiceType
	 */
	public Map<String, String> getMapToServiceType() {
		return mapToServiceType;
	}

	/**
	 * @param mapToServiceType the mapToServiceType to set
	 */
	public void setMapToServiceType(Map<String, String> mapToServiceType) {
		this.mapToServiceType = mapToServiceType;
	}

	/**
	 * @return the balance
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(String balance) {
		this.balance = balance;
	}


	/**
	 * @return the amountAvailable
	 */
	public String getAmountAvailable() {
		return amountAvailable;
	}

	/**
	 * @param amountAvailable the amountAvailable to set
	 */
	public void setAmountAvailable(String amountAvailable) {
		this.amountAvailable = amountAvailable;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the maxWithdrawal
	 */
	public String getMaxWithdrawal() {
		return maxWithdrawal;
	}

	/**
	 * @param maxWithdrawal the maxWithdrawal to set
	 */
	public void setMaxWithdrawal(String maxWithdrawal) {
		this.maxWithdrawal = maxWithdrawal;
	}

	/**
	 * @return the minWithdrawal
	 */
	public String getMinWithdrawal() {
		return minWithdrawal;
	}

	/**
	 * @param minWithdrawal the minWithdrawal to set
	 */
	public void setMinWithdrawal(String minWithdrawal) {
		this.minWithdrawal = minWithdrawal;
	}
	/**
	 * @return the captchaResponse
	 */
	public String getCaptchaResponse() {
		return captchaResponse;
	}

	/**
	 * @param captchaResponse the captchaResponse to set
	 */
	public void setCaptchaResponse(String captchaResponse) {
		this.captchaResponse = captchaResponse;
	}

	/**
	 * @return the decimalFormatCurrencyCode
	 */
	public Integer getDecimalFormatCurrencyCode() {
		return decimalFormatCurrencyCode;
	}

	/**
	 * @param decimalFormatCurrencyCode the decimalFormatCurrencyCode to set
	 */
	public void setDecimalFormatCurrencyCode(Integer decimalFormatCurrencyCode) {
		this.decimalFormatCurrencyCode = decimalFormatCurrencyCode;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
	 * @return the balanceScInfo
	 */
	public BalanceInfo getBalanceScInfo() {
		return balanceScInfo;
	}

	/**
	 * @param balanceScInfo the balanceScInfo to set
	 */
	public void setBalanceScInfo(BalanceInfo balanceScInfo) {
		this.balanceScInfo = balanceScInfo;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}


}
