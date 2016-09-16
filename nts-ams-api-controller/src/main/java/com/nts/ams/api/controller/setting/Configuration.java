package com.nts.ams.api.controller.setting;

import java.util.HashMap;
import java.util.Map;
import phn.com.nts.util.common.StringUtil;
import com.nts.ams.api.controller.common.AmsApiMode;


/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class Configuration {
	private Map<String, String> amsApiModes = new HashMap<String, String>();
	private int maxRequestOnTime = 1000;
	
	private String amsApiProtoVersion = "123456";
	private int profileUpdatePoolSize = 50;
	private int balanacePoolSize = 50;
	private int profilePoolSize = 50;
	private int agreementPoolSize = 50;
	private int transferPoolSize = 50;
	private int depositPoolSize = 50;
	private int withdrawalPoolSize = 50;
	private long stopServiceTimeOut = 1000L;
	private long balanaceTimeOut = 5000L;
	private String mypageUrl = "";
	private String mypageAvataUrl = "";
	
	private long maxTimeCacheAgreement = 24*60*60*1000; //24h = 86400000 miliseconds cache local, after must reload from Redis
	private long minTimeCacheAgreement = 5000; //min time miliseconds to reload Agreement from Redis
	
	private String sendAmsOffSignalNotificationMail = "1";
	
	public String getAmsApiProtoVersion() {
		return amsApiProtoVersion;
	}
	public void setAmsApiProtoVersion(String amsApiProtoVersion) {
		this.amsApiProtoVersion = amsApiProtoVersion;
	}
	public int getProfileUpdatePoolSize() {
		return profileUpdatePoolSize;
	}
	public void setProfileUpdatePoolSize(int profileUpdatePoolSize) {
		this.profileUpdatePoolSize = profileUpdatePoolSize;
	}
	public int getBalanacePoolSize() {
		return balanacePoolSize;
	}
	public void setBalanacePoolSize(int balanacePoolSize) {
		this.balanacePoolSize = balanacePoolSize;
	}
	public int getProfilePoolSize() {
		return profilePoolSize;
	}
	public void setProfilePoolSize(int profilePoolSize) {
		this.profilePoolSize = profilePoolSize;
	}
	public int getAgreementPoolSize() {
		return agreementPoolSize;
	}
	public void setAgreementPoolSize(int agreementPoolSize) {
		this.agreementPoolSize = agreementPoolSize;
	}
	public int getTransferPoolSize() {
		return transferPoolSize;
	}
	public void setTransferPoolSize(int transferPoolSize) {
		this.transferPoolSize = transferPoolSize;
	}
	public int getDepositPoolSize() {
		return depositPoolSize;
	}
	public void setDepositPoolSize(int depositPoolSize) {
		this.depositPoolSize = depositPoolSize;
	}
	public int getWithdrawalPoolSize() {
		return withdrawalPoolSize;
	}
	public void setWithdrawalPoolSize(int withdrawalPoolSize) {
		this.withdrawalPoolSize = withdrawalPoolSize;
	}
	
	public long getStopServiceTimeOut() {
		return stopServiceTimeOut;
	}
	public void setStopServiceTimeOut(long stopServiceTimeOut) {
		this.stopServiceTimeOut = stopServiceTimeOut;
	}
	public long getBalanaceTimeOut() {
		return balanaceTimeOut;
	}
	public void setBalanaceTimeOut(long balanaceTimeOut) {
		this.balanaceTimeOut = balanaceTimeOut;
	}
	
	public String getMypageUrl() {
		return mypageUrl;
	}
	public void setMypageUrl(String mypageUrl) {
		this.mypageUrl = mypageUrl;
	}
	
	public String getMypageAvataUrl() {
		return mypageAvataUrl;
	}
	public void setMypageAvataUrl(String mypageAvataUrl) {
		this.mypageAvataUrl = mypageAvataUrl;
	}
	
	public String getAmsApiMode() throws Exception {
		String modeName = "";
		
		for (String mode : amsApiModes.values()) {
			modeName += AmsApiMode.valueOf(Integer.valueOf(mode)) + ", ";
		}
		
		return modeName;
	}
	
	public void setAmsApiMode(String amsApiMode) throws Exception {
		if(amsApiMode == null)
			throw new Exception("amsApiMode must be set!");
		
		if(amsApiMode.trim().length() == 0)
			throw new Exception("amsApiMode can not be empty!");
		
		String[] temps = amsApiMode.trim().split(",");
		
		for (String mode : temps) {
			amsApiModes.put(mode, mode);
		}
	}
	
	public boolean hasAmsApiMode(AmsApiMode amsApiMode) {
		return amsApiModes.containsKey(String.valueOf(amsApiMode.getNumber()));
	}
	
	public int getMaxRequestOnTime() {
		return maxRequestOnTime;
	}
	public void setMaxRequestOnTime(int maxRequestOnTime) {
		this.maxRequestOnTime = maxRequestOnTime;
	}
	
	public long getMaxTimeCacheAgreement() {
		return maxTimeCacheAgreement;
	}
	public void setMaxTimeCacheAgreement(long maxTimeCacheAgreement) {
		this.maxTimeCacheAgreement = maxTimeCacheAgreement;
	}
	public long getMinTimeCacheAgreement() {
		return minTimeCacheAgreement;
	}
	public void setMinTimeCacheAgreement(long minTimeCacheAgreement) {
		this.minTimeCacheAgreement = minTimeCacheAgreement;
	}
	public String getSendAmsOffSignalNotificationMail() {
		return sendAmsOffSignalNotificationMail;
	}
	public void setSendAmsOffSignalNotificationMail(String sendAmsOffSignalNotificationMail) {
		if (!StringUtil.isEmpty(sendAmsOffSignalNotificationMail))
			this.sendAmsOffSignalNotificationMail = sendAmsOffSignalNotificationMail.trim();
	}
	
	@Override
	public String toString() {
		return "Configuration [amsApiModes=" + amsApiModes + ", maxRequestOnTime=" + maxRequestOnTime
				+ ", amsApiProtoVersion=" + amsApiProtoVersion + ", profileUpdatePoolSize=" + profileUpdatePoolSize
				+ ", balanacePoolSize=" + balanacePoolSize + ", profilePoolSize=" + profilePoolSize
				+ ", agreementPoolSize=" + agreementPoolSize + ", transferPoolSize=" + transferPoolSize
				+ ", depositPoolSize=" + depositPoolSize + ", withdrawalPoolSize=" + withdrawalPoolSize
				+ ", stopServiceTimeOut=" + stopServiceTimeOut + ", balanaceTimeOut=" + balanaceTimeOut + ", mypageUrl="
				+ mypageUrl + ", mypageAvataUrl=" + mypageAvataUrl + ", maxTimeCacheAgreement=" + maxTimeCacheAgreement
				+ ", minTimeCacheAgreement=" + minTimeCacheAgreement + ", sendAmsOffSignalNotificationMail="
				+ sendAmsOffSignalNotificationMail + "]";
	}
	
}
