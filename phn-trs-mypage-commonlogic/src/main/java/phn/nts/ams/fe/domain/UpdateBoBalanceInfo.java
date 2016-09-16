package phn.nts.ams.fe.domain;

import java.math.BigDecimal;

public class UpdateBoBalanceInfo {
	public UpdateBoBalanceInfo(Integer result, BigDecimal cashBalance){
		this.result = result;
		this.cashBalance = cashBalance;
	}
	
	private BigDecimal cashBalance;
	private Integer result;
	public BigDecimal getCashBalance() {
		return cashBalance;
	}
	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
}
