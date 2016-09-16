package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class WhiteLabelConfigInfo implements Serializable {
	private String configKey;
	private String wlCode;	
	private String configValue;
	private String note;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String configType;
	/**
	 * @return the configKey
	 */
	public String getConfigKey() {
		return configKey;
	}
	/**
	 * @param configKey the configKey to set
	 */
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
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
	 * @return the configValue
	 */
	public String getConfigValue() {
		return configValue;
	}
	/**
	 * @param configValue the configValue to set
	 */
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
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
	 * @return the configType
	 */
	public String getConfigType() {
		return configType;
	}
	/**
	 * @param configType the configType to set
	 */
	public void setConfigType(String configType) {
		this.configType = configType;
	}

}
