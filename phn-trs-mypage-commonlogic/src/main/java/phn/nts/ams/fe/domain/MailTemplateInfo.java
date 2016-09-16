package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import phn.com.nts.db.entity.AmsWhitelabel;

public class MailTemplateInfo implements Serializable {
	private Integer mailTemplateId;
	private String wlCode;
	private String mailCode;
	private Integer mailGroup;
	private Integer mailType;
	private String mailSubject;
	private String mailContent;
	private String mailCc;
	private String mailBcc;
	private Integer sendFlg;
	private Integer activeFlg;
	private Timestamp inputDate;
	private Timestamp updateDate;
	
	private Integer mailKind = 1;
	private Integer revisionNo;

	

	public Integer getMailTemplateId() {
		return this.mailTemplateId;
	}

	public void setMailTemplateId(Integer mailTemplateId) {
		this.mailTemplateId = mailTemplateId;
	}

	

	public String getMailCode() {
		return this.mailCode;
	}

	public void setMailCode(String mailCode) {
		this.mailCode = mailCode;
	}

	public Integer getMailGroup() {
		return this.mailGroup;
	}

	public void setMailGroup(Integer mailGroup) {
		this.mailGroup = mailGroup;
	}

	public Integer getMailType() {
		return this.mailType;
	}

	public void setMailType(Integer mailType) {
		this.mailType = mailType;
	}

	public String getMailSubject() {
		return this.mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailContent() {
		return this.mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailCc() {
		return this.mailCc;
	}

	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
	}

	public String getMailBcc() {
		return this.mailBcc;
	}

	public void setMailBcc(String mailBcc) {
		this.mailBcc = mailBcc;
	}

	public Integer getSendFlg() {
		return this.sendFlg;
	}

	public void setSendFlg(Integer sendFlg) {
		this.sendFlg = sendFlg;
	}

	public Integer getActiveFlg() {
		return this.activeFlg;
	}

	public void setActiveFlg(Integer activeFlg) {
		this.activeFlg = activeFlg;
	}

	public Timestamp getInputDate() {
		return this.inputDate;
	}

	public void setInputDate(Timestamp inputDate) {
		this.inputDate = inputDate;
	}

	public Timestamp getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getMailKind() {
		return mailKind;
	}

	public void setMailKind(Integer mailKind) {
		this.mailKind = mailKind;
	}

	public Integer getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(Integer revisionNo) {
		this.revisionNo = revisionNo;
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
}
