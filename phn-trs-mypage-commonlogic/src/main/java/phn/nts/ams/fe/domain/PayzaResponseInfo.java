package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PayzaResponseInfo implements Serializable{
	
	private String referenceNumber;
	private String description;
	private Integer returnCode;
	private String testMode;
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the returnCode
	 */
	public Integer getReturnCode() {
		return returnCode;
	}
	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}
	/**
	 * @return the testMode
	 */
	public String getTestMode() {
		return testMode;
	}
	/**
	 * @param testMode the testMode to set
	 */
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	
}
