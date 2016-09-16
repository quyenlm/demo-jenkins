package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class CustomerEwalletInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 373825877613843406L;
	private Integer ewalletId;
	private Integer ewalletType;
	private String ewalletAccNo;
	private String ewalletEmail;
	private String ewalletApiPassword;
	private String ewalletSecureId;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String customerId;
	private String ewalletApiName;
	private String ewalletSecureWord;
	
	//[NTS1.0-anhndn]Jan 22, 2013A - Start 
	Integer docVerifyStatus;
	//[NTS1.0-anhndn]Jan 22, 2013A - End
	//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - Start 
	List<DocumentInfo> docInfos;
	//[NTS1.0-Quan.Le.Minh]Feb 18, 2013A - End
	
	/**
	 * @return the ewalletId
	 */
	public Integer getEwalletId() {
		return ewalletId;
	}
	public List<DocumentInfo> getDocInfos() {
		return docInfos;
	}
	public void setDocInfos(List<DocumentInfo> docInfos) {
		this.docInfos = docInfos;
	}
	/**
	 * @param ewalletId the ewalletId to set
	 */
	public void setEwalletId(Integer ewalletId) {
		this.ewalletId = ewalletId;
	}
	/**
	 * @return the ewalletType
	 */
	public Integer getEwalletType() {
		return ewalletType;
	}
	/**
	 * @param ewalletType the ewalletType to set
	 */
	public void setEwalletType(Integer ewalletType) {
		this.ewalletType = ewalletType;
	}
	/**
	 * @return the ewalletAccNo
	 */
	public String getEwalletAccNo() {
		return ewalletAccNo;
	}
	/**
	 * @param ewalletAccNo the ewalletAccNo to set
	 */
	public void setEwalletAccNo(String ewalletAccNo) {
		this.ewalletAccNo = ewalletAccNo;
	}
	/**
	 * @return the ewalletEmail
	 */
	public String getEwalletEmail() {
		return ewalletEmail;
	}
	/**
	 * @param ewalletEmail the ewalletEmail to set
	 */
	public void setEwalletEmail(String ewalletEmail) {
		this.ewalletEmail = ewalletEmail;
	}
	/**
	 * @return the ewalletApiPassword
	 */
	public String getEwalletApiPassword() {
		return ewalletApiPassword;
	}
	/**
	 * @param ewalletApiPassword the ewalletApiPassword to set
	 */
	public void setEwalletApiPassword(String ewalletApiPassword) {
		this.ewalletApiPassword = ewalletApiPassword;
	}
	/**
	 * @return the ewalletSecureId
	 */
	public String getEwalletSecureId() {
		return ewalletSecureId;
	}
	/**
	 * @param ewalletSecureId the ewalletSecureId to set
	 */
	public void setEwalletSecureId(String ewalletSecureId) {
		this.ewalletSecureId = ewalletSecureId;
	}
	/**
	 * @return the activeFlag
	 */
	public Integer getActiveFlag() {
		return activeFlag;
	}
	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}
	/**
	 * @return the inputDate
	 */
	public Timestamp getInputDate() {
		return inputDate;
	}
	/**
	 * @param inputDate the inputDate to set
	 */
	public void setInputDate(Timestamp inputDate) {
		this.inputDate = inputDate;
	}
	/**
	 * @return the updateDate
	 */
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
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
	public String getEwalletApiName() {
		return ewalletApiName;
	}
	public void setEwalletApiName(String ewalletApiName) {
		this.ewalletApiName = ewalletApiName;
	}
	public String getEwalletSecureWord() {
		return ewalletSecureWord;
	}
	public void setEwalletSecureWord(String ewalletSecureWord) {
		this.ewalletSecureWord = ewalletSecureWord;
	}
	public Integer getDocVerifyStatus() {
		return docVerifyStatus;
	}
	public void setDocVerifyStatus(Integer docVerifyStatus) {
		this.docVerifyStatus = docVerifyStatus;
	}
	
	
}
