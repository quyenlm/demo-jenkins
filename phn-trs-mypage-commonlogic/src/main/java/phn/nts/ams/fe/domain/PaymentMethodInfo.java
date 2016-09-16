package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentMethodInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6389338849276214527L;
	private String currencyCode;
	private String baseCurrency;
	private BigDecimal amount;
	private Integer serviceType;
	private String customerId;
	private Integer paymentMethod;
	private String wlCode;
	private String loginId;
	private String userId;
	private Integer deviceType;
	private String regCustomerId;
	private Integer paymentGateway;
	private String paymentGatewayName;
	// allcharge
	private String merchantId;
	private String successPage;
	private String failurePage;
	private Integer itemType;
	private String userDesc;
	private String settleImmediate;
	private String resultPageMethod;
	private String transactionId;
	private String paymentUrl;
	//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - Start 
	private Double withdrawalFee;
	private Double receivedAmount;
	//[NTS1.0-Mai.Thu.Huyen]Oct 16, 2012A - End
	
	private Integer subGroupId;
	/**
	 * @return the paymentUrl
	 */
	public String getPaymentUrl() {
		return paymentUrl;
	}
	/**
	 * @param paymentUrl the paymentUrl to set
	 */
	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	 * @return the paymentMethod
	 */
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	/**
	 * @return the wlCode
	 */
	public String getWlCode() {
		return wlCode;
	}
	/**
	 * @param wlCode the wlCode to set
	 */
	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the deviceType
	 */
	public Integer getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
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
	 * @return the paymentGatewayName
	 */
	public String getPaymentGatewayName() {
		return paymentGatewayName;
	}
	/**
	 * @param paymentGatewayName the paymentGatewayName to set
	 */
	public void setPaymentGatewayName(String paymentGatewayName) {
		this.paymentGatewayName = paymentGatewayName;
	}
	
	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}
	/**
	 * @param merchantId the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	/**
	 * @return the successPage
	 */
	public String getSuccessPage() {
		return successPage;
	}
	/**
	 * @param successPage the successPage to set
	 */
	public void setSuccessPage(String successPage) {
		this.successPage = successPage;
	}
	/**
	 * @return the failurePage
	 */
	public String getFailurePage() {
		return failurePage;
	}
	/**
	 * @param failurePage the failurePage to set
	 */
	public void setFailurePage(String failurePage) {
		this.failurePage = failurePage;
	}
	/**
	 * @return the itemType
	 */
	public Integer getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
	/**
	 * @return the userDesc
	 */
	public String getUserDesc() {
		return userDesc;
	}
	/**
	 * @param userDesc the userDesc to set
	 */
	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}
	/**
	 * @return the settleImmediate
	 */
	public String getSettleImmediate() {
		return settleImmediate;
	}
	/**
	 * @param settleImmediate the settleImmediate to set
	 */
	public void setSettleImmediate(String settleImmediate) {
		this.settleImmediate = settleImmediate;
	}
	/**
	 * @return the resultPageMethod
	 */
	public String getResultPageMethod() {
		return resultPageMethod;
	}
	/**
	 * @param resultPageMethod the resultPageMethod to set
	 */
	public void setResultPageMethod(String resultPageMethod) {
		this.resultPageMethod = resultPageMethod;
	}
	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}
	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	/**
	 * @return the withdrawalFee
	 */
	public Double getWithdrawalFee() {
		return withdrawalFee;
	}
	/**
	 * @param withdrawalFee the withdrawalFee to set
	 */
	public void setWithdrawalFee(Double withdrawalFee) {
		this.withdrawalFee = withdrawalFee;
	}
	/**
	 * @return the receivedAmount
	 */
	public Double getReceivedAmount() {
		return receivedAmount;
	}
	/**
	 * @param receivedAmount the receivedAmount to set
	 */
	public void setReceivedAmount(Double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	public Integer getSubGroupId() {
		return subGroupId;
	}
	public void setSubGroupId(Integer subGroupId) {
		this.subGroupId = subGroupId;
	}
	
	
	
}
