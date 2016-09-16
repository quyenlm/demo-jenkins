package phn.nts.ams.fe.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.db.entity.AmsMessage;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BoRegisInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.MessageInfo;


public class AccountModel extends BaseSocialModel {
	private BalanceInfo balanceAmsInfo = null;
	private BalanceInfo balanceBoInfo = null;
	private BalanceInfo balanceFxInfo = null;
	private BalanceInfo balanceScInfo = null;
	private Double total;
	private String serviceType;	
	private String serviceTypeId;
	private String agree;
	private String currencyCode = null;	
	private List<MessageInfo> listMessage = null;
	private CustomerInfo customerInfo;
	private Map<Integer, String> mapCorporationType=new HashMap<Integer, String>();
	private String verifyMessage;
	private int uploadDoc;
	private String newPassWord;
	private String newPassWordConfirm;
	private String userLoginId;
	private String verifyCode;
	private String service;
	private String url;
	private String homepageDocUrl;
	private Map<Integer, String> mapPurposeBoHedgeType;
	private Map<Integer, String> mapPurposeBoHedgeAmount;
	private Map<Integer,String> mapPurposeBo;
	private BoRegisInfo boRegisInfo;
	private List<Integer> serviceTypeChecked;
	private AmsMessage normalNewsMessage;
	private String myPageUrl;
	
	
	public AmsMessage getNormalNewsMessage() {
		return normalNewsMessage;
	}

	public void setNormalNewsMessage(AmsMessage normalNewsMessage) {
		this.normalNewsMessage = normalNewsMessage;
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
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the serviceTypeId
	 */
	public String getServiceTypeId() {
		return serviceTypeId;
	}

	/**
	 * @param serviceTypeId the serviceTypeId to set
	 */
	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	/**
	 * @return the agree
	 */
	public String getAgree() {
		return agree;
	}

	/**
	 * @param agree the agree to set
	 */
	public void setAgree(String agree) {
		this.agree = agree;
	}

	/**
	 * @return the balanceAmsInfo
	 */
	public BalanceInfo getBalanceAmsInfo() {
		return balanceAmsInfo;
	}

	/**
	 * @param balanceAmsInfo the balanceAmsInfo to set
	 */
	public void setBalanceAmsInfo(BalanceInfo balanceAmsInfo) {
		this.balanceAmsInfo = balanceAmsInfo;
	}

	

	/**
	 * @return the balanceFxInfo
	 */
	public BalanceInfo getBalanceFxInfo() {
		return balanceFxInfo;
	}

	/**
	 * @param balanceFxInfo the balanceFxInfo to set
	 */
	public void setBalanceFxInfo(BalanceInfo balanceFxInfo) {
		this.balanceFxInfo = balanceFxInfo;
	}
	
	public List<MessageInfo> getListMessage() {
		return listMessage;
	}

	public void setListMessage(List<MessageInfo> listMessage) {
		this.listMessage = listMessage;
	}

	/**
	 * @return the balanceBoInfo
	 */
	public BalanceInfo getBalanceBoInfo() {
		return balanceBoInfo;
	}

	/**
	 * @param balanceBoInfo the balanceBoInfo to set
	 */
	public void setBalanceBoInfo(BalanceInfo balanceBoInfo) {
		this.balanceBoInfo = balanceBoInfo;
	}

	/**
	 * @return the customerInfo
	 */
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	public String getVerifyMessage() {
		return verifyMessage;
	}

	public void setVerifyMessage(String verifyMessage) {
		this.verifyMessage = verifyMessage;
	}

	public int getUploadDoc() {
		return uploadDoc;
	}

	public void setUploadDoc(int uploadDoc) {
		this.uploadDoc = uploadDoc;
	}

	/**
	 * @return the balanceScInfo
	 */
	public BalanceInfo getBalanceScInfo() {
		return balanceScInfo;
	}

	/**
	 * @param balanceScInfo the balanceScInfo to set
	 */
	public void setBalanceScInfo(BalanceInfo balanceScInfo) {
		this.balanceScInfo = balanceScInfo;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * @return the mapCorporationType
	 */
	public Map<Integer, String> getMapCorporationType() {
		return mapCorporationType;
	}

	/**
	 * @param mapCorporationType the mapCorporationType to set
	 */
	public void setMapCorporationType(Map<Integer, String> mapCorporationType) {
		this.mapCorporationType = mapCorporationType;
	}

	/**
	 * @return the newPassWord
	 */
	public String getNewPassWord() {
		return newPassWord;
	}

	/**
	 * @param newPassWord the newPassWord to set
	 */
	public void setNewPassWord(String newPassWord) {
		this.newPassWord = newPassWord;
	}

	/**
	 * @return the newPassWordConfirm
	 */
	public String getNewPassWordConfirm() {
		return newPassWordConfirm;
	}

	/**
	 * @param newPassWordConfirm the newPassWordConfirm to set
	 */
	public void setNewPassWordConfirm(String newPassWordConfirm) {
		this.newPassWordConfirm = newPassWordConfirm;
	}

	/**
	 * @return the userLoginId
	 */
	public String getUserLoginId() {
		return userLoginId;
	}

	/**
	 * @param userLoginId the userLoginId to set
	 */
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	/**
	 * @return the verifyCode
	 */
	public String getVerifyCode() {
		return verifyCode;
	}

	/**
	 * @param verifyCode the verifyCode to set
	 */
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHomepageDocUrl() {
		return homepageDocUrl;
	}

	public void setHomepageDocUrl(String homepageDocUrl) {
		this.homepageDocUrl = homepageDocUrl;
	}

	public Map<Integer, String> getMapPurposeBoHedgeType() {
		return mapPurposeBoHedgeType;
	}

	public void setMapPurposeBoHedgeType(Map<Integer, String> mapPurposeBoHedgeType) {
		this.mapPurposeBoHedgeType = mapPurposeBoHedgeType;
	}

	public Map<Integer, String> getMapPurposeBoHedgeAmount() {
		return mapPurposeBoHedgeAmount;
	}

	public void setMapPurposeBoHedgeAmount(
			Map<Integer, String> mapPurposeBoHedgeAmount) {
		this.mapPurposeBoHedgeAmount = mapPurposeBoHedgeAmount;
	}

	public Map<Integer, String> getMapPurposeBo() {
		return mapPurposeBo;
	}

	public void setMapPurposeBo(Map<Integer, String> mapPurposeBo) {
		this.mapPurposeBo = mapPurposeBo;
	}

	public BoRegisInfo getBoRegisInfo() {
		return boRegisInfo;
	}

	public void setBoRegisInfo(BoRegisInfo boRegisInfo) {
		this.boRegisInfo = boRegisInfo;
	}

	public List<Integer> getServiceTypeChecked() {
		return serviceTypeChecked;
	}

	public void setServiceTypeChecked(List<Integer> serviceTypeChecked) {
		this.serviceTypeChecked = serviceTypeChecked;
	}

	public String getMyPageUrl() {
		return myPageUrl;
	}

	public void setMyPageUrl(String myPageUrl) {
		this.myPageUrl = myPageUrl;
	}
	
}
