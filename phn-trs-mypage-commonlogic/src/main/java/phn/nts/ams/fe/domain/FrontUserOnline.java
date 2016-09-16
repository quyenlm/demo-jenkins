package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.db.entity.AmsGroup;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.util.common.IConstants;
import phn.com.trs.util.common.ITrsConstants;

public class FrontUserOnline implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userId;
	private String loginId;
	private String userRole;
	private String userName;
	private String password;
	private String currencyCode;
	private String wlCode;
	private AmsGroup userGroup;
	private Integer ibType;
	private String ibLink;
	private Integer countryId;
	private String countryName;
	private String countryCode;
	private String ipAddress;
	private String useDevice;
	private String userAgent;
	private Integer deviceType;	
	private ExchangerInfo exchangerInfo;
	private boolean isExchanger;
	private Boolean haveAgreementFlg;
	private String language;
	private String fullName;
	private String ticketId;
	private String mt4Id;
	private String description;
	private List<ScCustomerServiceInfo> listScCustomerServiceInfo;
	private List<CustomerServicesInfo> listCustomerServiceInfo;
	private Map<Integer, Boolean> mapCustomerService = new HashMap<Integer, Boolean>();
	private String subGroupCd;
	private String publicKey;
	private Integer avatarMode;
	private List<AmsMessage> agreements;
	private AmsMessage normalNewsMessage;
	private Integer finalcialSelfAssets;
	private Integer serviceBo;
	private String myPageUrl;
	private String ntdAccountId;
	
	
	public String getNtdAccountId() {
		return ntdAccountId;
	}
	public void setNtdAccountId(String ntdAccountId) {
		this.ntdAccountId = ntdAccountId;
	}
	public Integer getAvatarMode() {
		return avatarMode;
	}
	public void setAvatarMode(Integer avatarMode) {
		this.avatarMode = avatarMode;
	}
	public String getSubGroupCd() {
		return subGroupCd;
	}
	public void setSubGroupCd(String subGroupCd) {
		this.subGroupCd = subGroupCd;
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
	 * @return the userRole
	 */
	public String getUserRole() {
		return userRole;
	}
	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the userGroup
	 */
	public AmsGroup getUserGroup() {
		return userGroup;
	}
	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(AmsGroup userGroup) {
		this.userGroup = userGroup;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
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
	 * @return the ibType
	 */
	public Integer getIbType() {
		return ibType;
	}
	/**
	 * @param ibType the ibType to set
	 */
	public void setIbType(Integer ibType) {
		this.ibType = ibType;
	}
	/**
	 * @return the ibLink
	 */
	public String getIbLink() {
		return ibLink;
	}
	/**
	 * @param ibLink the ibLink to set
	 */
	public void setIbLink(String ibLink) {
		this.ibLink = ibLink;
	}
	/**
	 * @return the countryId
	 */
	public Integer getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}	
	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}
	/**
	 * @param userAgent the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	/**
	 * @return the useDevice
	 */
	public String getUseDevice() {
		return useDevice;
	}
	/**
	 * @param useDevice the useDevice to set
	 */
	public void setUseDevice(String useDevice) {
		this.useDevice = useDevice;
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
	 * @return the listCustomerServiceInfo
	 */
	public List<CustomerServicesInfo> getListCustomerServiceInfo() {
		return listCustomerServiceInfo;
	}
	/**
	 * @param listCustomerServiceInfo the listCustomerServiceInfo to set
	 */
	public void setListCustomerServiceInfo(List<CustomerServicesInfo> listCustomerServiceInfo) {
		this.listCustomerServiceInfo = listCustomerServiceInfo;
	}
	/**
	 * @return the mapCustomerService
	 */
	public Map<Integer, Boolean> getMapCustomerService() {
		return mapCustomerService;
	}
	/**
	 * @param mapCustomerService the mapCustomerService to set
	 */
	public void setMapCustomerService(Map<Integer, Boolean> mapCustomerService) {
		this.mapCustomerService = mapCustomerService;
	}

	/**
	 * @return the exchangerInfo
	 */
	public ExchangerInfo getExchangerInfo() {
		return exchangerInfo;
	}
	/**
	 * @param exchangerInfo the exchangerInfo to set
	 */
	public void setExchangerInfo(ExchangerInfo exchangerInfo) {
		this.exchangerInfo = exchangerInfo;
	}
	/**
	 * @return the isExchanger
	 */
	public boolean getIsExchanger() {
		return isExchanger;
	}
	/**
	 * @param isExchanger the isExchanger to set
	 */
	public void setIsExchanger(boolean isExchanger) {
		this.isExchanger = isExchanger;
	}
	public String getLanguage() {
		return language == null ? IConstants.Language.ENGLISH : language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the listScCustomerServiceInfo
	 */
	public List<ScCustomerServiceInfo> getListScCustomerServiceInfo() {
		return listScCustomerServiceInfo;
	}
	/**
	 * @param listScCustomerServiceInfo the listScCustomerServiceInfo to set
	 */
	public void setListScCustomerServiceInfo(
			List<ScCustomerServiceInfo> listScCustomerServiceInfo) {
		this.listScCustomerServiceInfo = listScCustomerServiceInfo;
	}
	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}
	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	/**
	 * @return the mt4Id
	 */
	public String getMt4Id() {
		return mt4Id;
	}
	/**
	 * @param mt4Id the mt4Id to set
	 */
	public void setMt4Id(String mt4Id) {
		this.mt4Id = mt4Id;
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
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}
	/**
	 * @param publicKey the publicKey to set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public List<AmsMessage> getAgreements() {
		return agreements;
	}
	public void setAgreements(List<AmsMessage> agreements) {
		this.agreements = agreements;
	}
	public Integer getFinalcialSelfAssets() {
		return finalcialSelfAssets;
	}
	public void setFinalcialSelfAssets(Integer finalcialSelfAssets) {
		this.finalcialSelfAssets = finalcialSelfAssets;
	}
	public Integer getServiceBo() {
		return serviceBo;
	}
	public void setServiceBo(Integer serviceBo) {
		this.serviceBo = serviceBo;
	}
	public AmsMessage getNormalNewsMessage() {
		return normalNewsMessage;
	}
	public void setNormalNewsMessage(AmsMessage normalNewsMessage) {
		this.normalNewsMessage = normalNewsMessage;
	}
	public Boolean getHaveAgreementFlg() {
		return haveAgreementFlg;
	}
	public void setHaveAgreementFlg(Boolean haveAgreementFlg) {
		this.haveAgreementFlg = haveAgreementFlg;
	}
	public void setExchanger(boolean isExchanger) {
		this.isExchanger = isExchanger;
	}
	public String getMyPageUrl() {
		return myPageUrl;
	}
	public void setMyPageUrl(String myPageUrl) {
		this.myPageUrl = myPageUrl;
	}
	public  Integer getSubgroupIdByServiceType(Integer serviceType){
		Integer result = null;
		for (CustomerServicesInfo customerServicesInfo : this.listCustomerServiceInfo) {
			if(customerServicesInfo.getServiceType().equals(serviceType)){
				result = customerServicesInfo.getSubGroupId();
				break;
			}
		}
		return result;
		
	}
	
	public List<Integer> getListServiceType(){
		List<Integer> result = new ArrayList<Integer>();
		result.add(ITrsConstants.SERVICES_TYPE.AMS);
		for (CustomerServicesInfo customerServicesInfo : this.listCustomerServiceInfo) {
				result.add(customerServicesInfo.getServiceType());
		}
		
		return result;
	}
	
	
	
}
