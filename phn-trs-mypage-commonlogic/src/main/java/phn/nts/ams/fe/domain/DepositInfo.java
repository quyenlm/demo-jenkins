package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DepositInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String amount;
	private Integer method;
	private String currencyCode;
	private String loginId;
	private String customerId;
	private String customerName;
	private String emailAddress;
	private Integer serviceType;
	private String remark;
	private BigDecimal maxAmount;
	private BigDecimal minAmount;
	private String bankInfo;
	private String regCustomerId;
	private Integer depositRoute;
	private String methodName;
	private String serviceTypeName;

	private String paymentGatewayName;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Integer getMethod() {
		return method;
	}
	public void setMethod(Integer method) {
		this.method = method;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the serviceType
	 */
	public Integer getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
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
	 * @return the maxAmount
	 */
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return the minAmount
	 */
	public BigDecimal getMinAmount() {
		return minAmount;
	}
	/**
	 * @param minAmount the minAmount to set
	 */
	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}
	/**
	 * @return the bankInfo
	 */
	public String getBankInfo() {
		return bankInfo;
	}
	/**
	 * @param bankInfo the bankInfo to set
	 */
	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}
	/**
	 * @return the regCustomerId
	 */
	public String getRegCustomerId() {
		return regCustomerId;
	}
	/**
	 * @param regCustomerId the regCustomerId to set
	 */
	public void setRegCustomerId(String regCustomerId) {
		this.regCustomerId = regCustomerId;
	}
	/**
	 * @return the depositRoute
	 */
	public Integer getDepositRoute() {
		return depositRoute;
	}
	/**
	 * @param depositRoute the depositRoute to set
	 */
	public void setDepositRoute(Integer depositRoute) {
		this.depositRoute = depositRoute;
	}
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	/**
	 * @return the serviceTypeName
	 */
	public String getServiceTypeName() {
		return serviceTypeName;
	}
	/**
	 * @param serviceTypeName the serviceTypeName to set
	 */
	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}
	public String getPaymentGatewayName() {
		return paymentGatewayName;
	}
	public void setPaymentGatewayName(String paymentGatewayName) {
		this.paymentGatewayName = paymentGatewayName;
	}
	
	

}
