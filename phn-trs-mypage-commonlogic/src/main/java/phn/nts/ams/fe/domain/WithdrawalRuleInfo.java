package phn.nts.ams.fe.domain;

import java.math.BigDecimal;
import java.util.Map;

public class WithdrawalRuleInfo {
	private boolean showTransactionWithCc;
	private boolean showTransactionWithOtherPaymentMethod;
	private BigDecimal withdrawFeeCc;
	private BigDecimal receivedAmountCc;
	private Integer paymentMethod;
	private BigDecimal withdrawFeeOtherMethod;
	private BigDecimal receivedAmountOtherMethod;
	private BigDecimal withdrawAmount2;
	private Map<String, String> mapWithdrawMethods;
	private BigDecimal minWithdrawAmount;
	private BigDecimal maxWithdrawAmount;
	private Integer serviceType;
	private String paymentMethodName;
	private BigDecimal libertyReferenceFee;
	
	public int getNumberOfTransactions(){
		int numberOfTransactions = 0;
		if(showTransactionWithCc) numberOfTransactions ++;
		if(showTransactionWithOtherPaymentMethod) numberOfTransactions ++;
		return numberOfTransactions;
	}
	
	public BigDecimal getLibertyReferenceFee() {
		return libertyReferenceFee;
	}

	public void setLibertyReferenceFee(BigDecimal libertyReferenceFee) {
		this.libertyReferenceFee = libertyReferenceFee;
	}

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public BigDecimal getMinWithdrawAmount() {
		return minWithdrawAmount;
	}

	public void setMinWithdrawAmount(BigDecimal minWithdrawAmount) {
		this.minWithdrawAmount = minWithdrawAmount;
	}

	public BigDecimal getMaxWithdrawAmount() {
		return maxWithdrawAmount;
	}

	public void setMaxWithdrawAmount(BigDecimal maxWithdrawAmount) {
		this.maxWithdrawAmount = maxWithdrawAmount;
	}

	public BigDecimal getTotalReceivedAmount() {
		if(receivedAmountCc == null) receivedAmountCc = BigDecimal.ZERO;
		if(receivedAmountOtherMethod == null) receivedAmountOtherMethod = BigDecimal.ZERO;
		return receivedAmountCc.add(receivedAmountOtherMethod);
	}
	
	public BigDecimal getWithdrawAmount2() {
		return withdrawAmount2;
	}

	public void setWithdrawAmount2(BigDecimal withdrawAmount2) {
		this.withdrawAmount2 = withdrawAmount2;
	}

	public Map<String, String> getMapWithdrawMethods() {
		return mapWithdrawMethods;
	}

	public void setMapWithdrawMethods(Map<String, String> mapWithdrawMethods) {
		this.mapWithdrawMethods = mapWithdrawMethods;
	}

	public boolean isShowTransactionWithCc() {
		return showTransactionWithCc;
	}

	public void setShowTransactionWithCc(boolean showTransactionWithCc) {
		this.showTransactionWithCc = showTransactionWithCc;
	}

	public boolean isShowTransactionWithOtherPaymentMethod() {
		return showTransactionWithOtherPaymentMethod;
	}

	public void setShowTransactionWithOtherPaymentMethod(
			boolean showTransactionWithOtherPaymentMethod) {
		this.showTransactionWithOtherPaymentMethod = showTransactionWithOtherPaymentMethod;
	}

	public BigDecimal getWithdrawFeeCc() {
		return withdrawFeeCc;
	}
	public void setWithdrawFeeCc(BigDecimal withdrawFeeCc) {
		this.withdrawFeeCc = withdrawFeeCc;
	}
	public BigDecimal getReceivedAmountCc() {
		return receivedAmountCc;
	}
	public void setReceivedAmountCc(BigDecimal receivedAmountCc) {
		this.receivedAmountCc = receivedAmountCc;
	}
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public BigDecimal getWithdrawFeeOtherMethod() {
		return withdrawFeeOtherMethod;
	}
	public void setWithdrawFeeOtherMethod(BigDecimal withdrawFeeOtherMethod) {
		this.withdrawFeeOtherMethod = withdrawFeeOtherMethod;
	}
	public BigDecimal getReceivedAmountOtherMethod() {
		return receivedAmountOtherMethod;
	}
	public void setReceivedAmountOtherMethod(BigDecimal receivedAmountOtherMethod) {
		this.receivedAmountOtherMethod = receivedAmountOtherMethod;
	}

	@Override
	public String toString() {
		return "WithdrawalRuleInfo [showTransactionWithCc="
				+ showTransactionWithCc
				+ ", showTransactionWithOtherPaymentMethod="
				+ showTransactionWithOtherPaymentMethod + ", withdrawFeeCc="
				+ withdrawFeeCc + ", receivedAmountCc=" + receivedAmountCc
				+ ", paymentMethod=" + paymentMethod
				+ ", withdrawFeeOtherMethod=" + withdrawFeeOtherMethod
				+ ", receivedAmountOtherMethod=" + receivedAmountOtherMethod
				+ ", withdrawAmount2=" + withdrawAmount2
				+ ", mapWithdrawMethods=" + mapWithdrawMethods
				+ ", minWithdrawAmount=" + minWithdrawAmount
				+ ", maxWithdrawAmount=" + maxWithdrawAmount + ", serviceType="
				+ serviceType + ", paymentMethodName=" + paymentMethodName
				+ ", libertyReferenceFee=" + libertyReferenceFee + "]";
	}
}
