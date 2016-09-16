package phn.nts.ams.fe.domain;

import java.math.BigDecimal;
import java.util.Date;

public class IBCustomerInfo {
	String customerId = null;
	String customerName = null;
	BigDecimal kickbackAmount;
	Date kickbackDate;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getKickbackAmount() {
		return kickbackAmount;
	}
	public void setKickbackAmount(BigDecimal kickbackAmount) {
		this.kickbackAmount = kickbackAmount;
	}
	public Date getKickbackDate() {
		return kickbackDate;
	}
	public void setKickbackDate(Date kickbackDate) {
		this.kickbackDate = kickbackDate;
	}
	
}
