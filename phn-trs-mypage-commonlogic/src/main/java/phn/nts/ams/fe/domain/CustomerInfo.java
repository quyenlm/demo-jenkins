package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class CustomerInfo implements Serializable {

	private static final long serialVersionUID = 8992324302273961478L;
	private String ntdCustomerId;
	private String customerId;
	//TRSSC-226 begin - the.le.ngoc
	private String customerServiceId;
	private String guruCustomerId;
	private String guruNickName;
	private String eventDateTime;
	private String orderDate;
	private String symbol;
	private String volume;
	private String orderId;
	private String orderType;
	private String tradeType;
	private String executionPrice;
	private String lastTradeTime;
	//TRSSC-226 end
	private Integer groupId;
	private String groupName;
	private String currencyCode;
	private Integer countryId;	
	private String loginId;
	private String loginPass;
	private String mailActiveCode;
	private Integer leverage;
	private Integer corporationType=0;
	private String fullName;
	private String firstName;
	private String firstNameKana;
	private String lastName;
	private String lastNameKana;
	private String birthday;
	private Integer sex;
	private String mailMain;
    //[NTS1.0-Dai.Nguyen.Van]Mar 1, 2013 - Start
    private String confirmMailMain;
    private String phoneCode;
    private Integer phoneType;
    private boolean fxBoFlag;
    private boolean demoFxFlag;
    //[NTS1.0-Dai.Nguyen.Van]Mar 1, 2013 - End
	private String mailAddtional;
	private String mailMobile;
	private String zipcode;
	private String prefecture;
	private String city;
	private String section;
	private String buildingName;
	private String houseNumber;
	private String address;
	private String tel1;
	private String tel2;
	private String fax;
	private Integer jobType;
	private Integer industryType;
	private String jobPlaceName;
	private String companyName;
	private String companyTel;
	private String jobZipcode;
	private String jobPrefecture;
	private String jobWard;
	private String jobBuilding;
	private String corpFullname;
	private String corpFullnameKana;
	private Integer corpIndustryType;
	private String corpJobDetail;
	private String corpEstablishDate;
	private String corpSettleDate;
	private String corpHomepage;
	private String corpRep;
	private String corpRepFirstname;
	private String corpRepLastname;
	private String corpRepFirstnameKana;
	private String corpRepLastnameKana;
	private String corpPicDep;
	private String corpPicPosition;
	private String corpPicFirstname;
	private String corpPicFirstnameKana;
	private String corpPicLastname;
	private String corpPicLastnameKana;
	private Integer corpPicSex;
	private String corpPicTel;
	private String corpPicMobile;
	private String corpPicMailPc;
	private String corpPicMailMobile;
	private Integer bankAccClass;
	private String bankAccNumber;
	private Integer documentSendStatus;
	private String documentPostDate;
	private String documentAcceptDate;
	private Integer accountOpenStatus;
	private Integer allowChangePassFlg;
	private Integer allowNewOrderFlg;
	private String note;
	private String wlCode;
	private String wlCustomerId;
	private Integer confirm1Flg;
	private Integer confirm2Flg;
	private Integer confirm3Flg;
	private Integer confirm4Flg;
	private Integer accountClass;
	private Integer activeFlag;
	private Timestamp inputDate;
	private Timestamp updateDate;
	private String serviceType;
	private String countryName;
	private String countryCode;
	private Boolean changePasswordFlag;
	//[NatureForex1.0-HuyenMT]Aug 22, 2012A - Start 
	private Integer allowWithdrawalFlg;
	//[NatureForex1.0-HuyenMT]Aug 22, 2012A - End
	private String comfirmedPassword;
	private String newPassword;
	private String identifyPassword;
	private String md5IndentifyPassword;
	private String mt4Id;
	private String ibLink;
	private String day;
	private String month;
	private String year;
	private String displayLanguage;
	private String displayLanguageName;
	
	private String accountApplicationDate;
	/** ams currency */
	private String currencyAms;
	private String currencyFx;
	private String currencyBo;
    private String currencyCopyTrade;
	
	//[NTS1.0-Quan.Le.Minh]Jan 21, 2013A - Start 
	private Integer verifyPhoneStatus;
	private Integer verifyPassportStatus;
	private Integer verifyAddressStatus;
	private Integer verifySignatureStatus;
	
	private List<DocumentInfo> passportDocs;
	private List<DocumentInfo> addressDocs;
	private List<DocumentInfo> signatureDocs;
	private String description;
	//[NTS1.0-Quan.Le.Minh]Jan 21, 2013A - End
	
	//[NTS1.0-SonPH]Mar 01, 2013A - Start
	private String username;
	private String returnRate;
	private Integer copierNo;
	private Integer FollowerNo;
	private String brokerCd;
	private String accountId;
   //[NTS1.0-SonPH]Mar 01, 2013A - End
	
	
	//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
	private String accountOpenDate;
	// survey
	private String financilAssetsName;
	private Integer financilAssets;

	private boolean purposeShortTermFlg;
	private boolean purposeLongTermFlg;
	private boolean purposeExchangeFlg;
	private boolean purposeSwapFlg;
	private boolean purposeHedgeAssetFlg;
	private boolean purposeHighIntFlg;
	private boolean purposeEconomicFlg;
	private boolean purposeOther;
	private String purposeShortTermFlgName;
	private String purposeLongTermFlgName;
	private String purposeExchangeFlgName;
	private String purposeSwapFlgName;
	private String purposeHedgeAssetFlgName;
	private String purposeHighIntFlgName;
	private String purposeEconomicFlgName;
	private String purposeOtherName;
	private String purposeOtherComment;
	private Integer beneficOwnerFlg;
	private String beneficOwnerFullname;
	private String beneficOwnerFullnameKana;
	private String beneficOwnerFirstnameKana;
	private String beneficOwnerLastnameKana;
	private String beneficOwnerFirstname;
	private String beneficOwnerLastname;
	private String beneficOwnerEstablishDate;
	private String beneficOwnerZipcode;
	private String beneficOwnerPrefecture;
	private String beneficOwnerCity;
	private String beneficOwnerSection;
	private String beneficOwnerBuildingName;
	private String beneficOwnerTel;
	private String beneficOwnerEstablishDateYear;
	private String beneficOwnerEstablishDateMonth;
	private String beneficOwnerEstablishDateDay;

	private Integer beneficOwnerFlg2;
	private String beneficOwnerFullname2;
	private String beneficOwnerFullnameKana2;
	private String beneficOwnerFirstnameKana2;
	private String beneficOwnerLastnameKana2;
	private String beneficOwnerFirstname2;
	private String beneficOwnerLastname2;
	private String beneficOwnerEstablishDate2;
	private String beneficOwnerZipcode2;
	private String beneficOwnerPrefecture2;
	private String beneficOwnerCity2;
	private String beneficOwnerSection2;
	private String beneficOwnerBuildingName2;
	private String beneficOwnerTel2;
	private String beneficOwnerEstablishDateYear2;
	private String beneficOwnerEstablishDateMonth2;
	private String beneficOwnerEstablishDateDay2;

	private Integer beneficOwnerFlg3;
	private String beneficOwnerFullname3;
	private String beneficOwnerFullnameKana3;
	private String beneficOwnerFirstnameKana3;
	private String beneficOwnerLastnameKana3;
	private String beneficOwnerFirstname3;
	private String beneficOwnerLastname3;
	private String beneficOwnerEstablishDate3;
	private String beneficOwnerZipcode3;
	private String beneficOwnerPrefecture3;
	private String beneficOwnerCity3;
	private String beneficOwnerSection3;
	private String beneficOwnerBuildingName3;
	private String beneficOwnerTel3;
	private String beneficOwnerEstablishDateYear3;
	private String beneficOwnerEstablishDateMonth3;
	private String beneficOwnerEstablishDateDay3;


	private String corpPicZipcode;
	private String corpPicPrefecture;
	private String corpPicCity;
	private String corpPicSection;
	private String corpPicBuildingName;
	private String corpPicAddress;
	private String corpRepFullname;
	private String corpRepFullnameKana;

	private boolean changeCustomerName;
	private boolean changeAddress;
	private boolean changeCorpName;
	private boolean changeCorpAddress;
	private boolean changeCorpOwnerName;
	private boolean changeCorpRefName;
	private boolean changeCorpRefAddress;
	private boolean changePurePose;
	private boolean changeCorpRepName;

	private boolean changePass;
	private boolean changeMailMain = false;
	
	private String virtualBankAccNo;
	private String virtualBankName;
	private String virtualBranchName;

	private List<String> listPurposes;

	private String virtualAccType;
	private String virtualAccName;
	private String virtualAccNameKana;
	//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
	
	private boolean boPurposeShortTermFlg;
	private boolean boPurposeDispAssetMngFlg;
	private boolean boPurposeHedgeFlg;
	private Integer boPurposeHedgeType;
	private Integer boPurposeHedgeAmount;
	private BigDecimal boMaxLossAmount;
	private boolean hasDocumentUpload;
	private Integer enableMt4Fx = 0;
	
	private boolean needReloadCache = false;
	private boolean eaAccount;
	private String oldLoginId;
	
	public  Integer getEnableMt4Fx() {
		return enableMt4Fx;
	}

	public void setEnableMt4Fx(Integer enableMt4Fx) {
		this.enableMt4Fx = enableMt4Fx;
	}

	public boolean isBoPurposeShortTermFlg() {
		return boPurposeShortTermFlg;
	}

	public void setBoPurposeShortTermFlg(boolean boPurposeShortTermFlg) {
		this.boPurposeShortTermFlg = boPurposeShortTermFlg;
	}

	public boolean isBoPurposeDispAssetMngFlg() {
		return boPurposeDispAssetMngFlg;
	}

	public void setBoPurposeDispAssetMngFlg(boolean boPurposeDispAssetMngFlg) {
		this.boPurposeDispAssetMngFlg = boPurposeDispAssetMngFlg;
	}

	public boolean isBoPurposeHedgeFlg() {
		return boPurposeHedgeFlg;
	}

	public void setBoPurposeHedgeFlg(boolean boPurposeHedgeFlg) {
		this.boPurposeHedgeFlg = boPurposeHedgeFlg;
	}

	public Integer getBoPurposeHedgeType() {
		return boPurposeHedgeType;
	}

	public void setBoPurposeHedgeType(Integer boPurposeHedgeType) {
		this.boPurposeHedgeType = boPurposeHedgeType;
	}

	public Integer getBoPurposeHedgeAmount() {
		return boPurposeHedgeAmount;
	}

	public void setBoPurposeHedgeAmount(Integer boPurposeHedgeAmount) {
		this.boPurposeHedgeAmount = boPurposeHedgeAmount;
	}

	public BigDecimal getBoMaxLossAmount() {
		return boMaxLossAmount;
	}

	public void setBoMaxLossAmount(BigDecimal boMaxLossAmount) {
		this.boMaxLossAmount = boMaxLossAmount;
	}

	public String getBeneficOwnerFirstname() {
			return beneficOwnerFirstname;
	}

	public String getBeneficOwnerEstablishDateYear() {
		return beneficOwnerEstablishDateYear;
	}

	public void setBeneficOwnerEstablishDateYear(String beneficOwnerEstablishDateYear) {
		this.beneficOwnerEstablishDateYear = beneficOwnerEstablishDateYear;
	}

	public String getBeneficOwnerEstablishDateMonth() {
		return beneficOwnerEstablishDateMonth;
	}

	public void setBeneficOwnerEstablishDateMonth(String beneficOwnerEstablishDateMonth) {
		this.beneficOwnerEstablishDateMonth = beneficOwnerEstablishDateMonth;
	}

	public String getBeneficOwnerEstablishDateDay() {
		return beneficOwnerEstablishDateDay;
	}

	public void setBeneficOwnerEstablishDateDay(String beneficOwnerEstablishDateDay) {
		this.beneficOwnerEstablishDateDay = beneficOwnerEstablishDateDay;
	}

	public Integer getBeneficOwnerFlg() {
		return beneficOwnerFlg;
	}

	public void setBeneficOwnerFlg(Integer beneficOwnerFlg) {
		this.beneficOwnerFlg = beneficOwnerFlg;
	}

	public String getBeneficOwnerEstablishDate() {
		return beneficOwnerEstablishDate;
	}

	public void setBeneficOwnerEstablishDate(String beneficOwnerEstablishDate) {
		this.beneficOwnerEstablishDate = beneficOwnerEstablishDate;
	}

	public String getBeneficOwnerZipcode() {
		return beneficOwnerZipcode;
	}

	public void setBeneficOwnerZipcode(String beneficOwnerZipcode) {
		this.beneficOwnerZipcode = beneficOwnerZipcode;
	}

	public String getBeneficOwnerPrefecture() {
		return beneficOwnerPrefecture;
	}

	public void setBeneficOwnerPrefecture(String beneficOwnerPrefecture) {
		this.beneficOwnerPrefecture = beneficOwnerPrefecture;
	}

	public String getBeneficOwnerCity() {
		return beneficOwnerCity;
	}

	public void setBeneficOwnerCity(String beneficOwnerCity) {
		this.beneficOwnerCity = beneficOwnerCity;
	}

	public String getBeneficOwnerSection() {
		return beneficOwnerSection;
	}

	public void setBeneficOwnerSection(String beneficOwnerSection) {
		this.beneficOwnerSection = beneficOwnerSection;
	}

	public String getBeneficOwnerBuildingName() {
		return beneficOwnerBuildingName;
	}

	public void setBeneficOwnerBuildingName(String beneficOwnerBuildingName) {
		this.beneficOwnerBuildingName = beneficOwnerBuildingName;
	}

	public String getBeneficOwnerTel() {
		return beneficOwnerTel;
	}

	public void setBeneficOwnerTel(String beneficOwnerTel) {
		this.beneficOwnerTel = beneficOwnerTel;
	}

	public String getVirtualBranchName() {
		return virtualBranchName;
	}

	public void setVirtualBranchName(String virtualBranchName) {
		this.virtualBranchName = virtualBranchName;
	}

	public String getVirtualAccNameKana() {
		return virtualAccNameKana;
	}

	public void setVirtualAccNameKana(String virtualAccNameKana) {
		this.virtualAccNameKana = virtualAccNameKana;
	}

	public boolean isChangeCorpRepName() {
		return changeCorpRepName;
	}

	public void setChangeCorpRepName(boolean changeCorpRepName) {
		this.changeCorpRepName = changeCorpRepName;
	}

	public String getCorpRepFullnameKana() {
		return corpRepFullnameKana;
	}

	public void setCorpRepFullnameKana(String corpRepFullnameKana) {
		this.corpRepFullnameKana = corpRepFullnameKana;
	}

	public String getCorpRepFullname() {
		return corpRepFullname;
	}

	public void setCorpRepFullname(String corpRepFullname) {
		this.corpRepFullname = corpRepFullname;
	}

	public boolean isChangePurePose() {
		return changePurePose;
	}

	public void setChangePurePose(boolean changePurePose) {
		this.changePurePose = changePurePose;
	}

	public String getVirtualAccType() {
		return virtualAccType;
	}

	public void setVirtualAccType(String virtualAccType) {
		this.virtualAccType = virtualAccType;
	}

	public String getVirtualAccName() {
		return virtualAccName;
	}

	public void setVirtualAccName(String virtualAccName) {
		this.virtualAccName = virtualAccName;
	}

	public String getVirtualBankName() {
		return virtualBankName;
	}



	public void setVirtualBankName(String virtualBankName) {
		this.virtualBankName = virtualBankName;
	}



	public String getVirtualBankAccNo() {
		return virtualBankAccNo;
	}

	public void setVirtualBankAccNo(String virtualBankAccNo) {
		this.virtualBankAccNo = virtualBankAccNo;
	}

	public String getPurposeLongTermFlgName() {
		return purposeLongTermFlgName;
	}

	public void setPurposeLongTermFlgName(String purposeLongTermFlgName) {
		this.purposeLongTermFlgName = purposeLongTermFlgName;
	}

	public String getPurposeExchangeFlgName() {
		return purposeExchangeFlgName;
	}

	public void setPurposeExchangeFlgName(String purposeExchangeFlgName) {
		this.purposeExchangeFlgName = purposeExchangeFlgName;
	}

	public String getPurposeSwapFlgName() {
		return purposeSwapFlgName;
	}

	public void setPurposeSwapFlgName(String purposeSwapFlgName) {
		this.purposeSwapFlgName = purposeSwapFlgName;
	}



	public String getPurposeHedgeAssetFlgName() {
		return purposeHedgeAssetFlgName;
	}



	public void setPurposeHedgeAssetFlgName(String purposeHedgeAssetFlgName) {
		this.purposeHedgeAssetFlgName = purposeHedgeAssetFlgName;
	}



	public String getPurposeHighIntFlgName() {
		return purposeHighIntFlgName;
	}



	public void setPurposeHighIntFlgName(String purposeHighIntFlgName) {
		this.purposeHighIntFlgName = purposeHighIntFlgName;
	}



	public String getPurposeEconomicFlgName() {
		return purposeEconomicFlgName;
	}



	public void setPurposeEconomicFlgName(String purposeEconomicFlgName) {
		this.purposeEconomicFlgName = purposeEconomicFlgName;
	}



	public String getPurposeOtherName() {
		return purposeOtherName;
	}



	public void setPurposeOtherName(String purposeOtherName) {
		this.purposeOtherName = purposeOtherName;
	}



	public List<String> getListPurposes() {
		return listPurposes;
	}



	public void setListPurposes(List<String> listPurposes) {
		this.listPurposes = listPurposes;
	}




	public boolean isPurposeLongTermFlg() {
		return purposeLongTermFlg;
	}

	public void setPurposeLongTermFlg(boolean purposeLongTermFlg) {
		this.purposeLongTermFlg = purposeLongTermFlg;
	}

	public boolean isPurposeExchangeFlg() {
		return purposeExchangeFlg;
	}

	public void setPurposeExchangeFlg(boolean purposeExchangeFlg) {
		this.purposeExchangeFlg = purposeExchangeFlg;
	}

	public boolean isPurposeSwapFlg() {
		return purposeSwapFlg;
	}

	public void setPurposeSwapFlg(boolean purposeSwapFlg) {
		this.purposeSwapFlg = purposeSwapFlg;
	}

	public boolean isPurposeHedgeAssetFlg() {
		return purposeHedgeAssetFlg;
	}

	public void setPurposeHedgeAssetFlg(boolean purposeHedgeAssetFlg) {
		this.purposeHedgeAssetFlg = purposeHedgeAssetFlg;
	}

	public boolean isPurposeHighIntFlg() {
		return purposeHighIntFlg;
	}

	public void setPurposeHighIntFlg(boolean purposeHighIntFlg) {
		this.purposeHighIntFlg = purposeHighIntFlg;
	}

	public boolean isPurposeEconomicFlg() {
		return purposeEconomicFlg;
	}

	public void setPurposeEconomicFlg(boolean purposeEconomicFlg) {
		this.purposeEconomicFlg = purposeEconomicFlg;
	}

	public boolean isPurposeOther() {
		return purposeOther;
	}

	public void setPurposeOther(boolean purposeOther) {
		this.purposeOther = purposeOther;
	}

	public void setPurposeShortTermFlg(boolean purposeShortTermFlg) {
		this.purposeShortTermFlg = purposeShortTermFlg;
	}

	public String getPurposeOtherComment() {
		return purposeOtherComment;
	}
	public void setPurposeOtherComment(String purposeOtherComment) {
		this.purposeOtherComment = purposeOtherComment;
	}
	public String getCorpPicAddress() {
		return corpPicAddress;
	}
	public void setCorpPicAddress(String corpPicAddress) {
		this.corpPicAddress = corpPicAddress;
	}
	public boolean isChangeCustomerName() {
		return changeCustomerName;
	}
	public void setChangeCustomerName(boolean changeCustomerName) {
		this.changeCustomerName = changeCustomerName;
	}
	public boolean isChangeAddress() {
		return changeAddress;
	}
	public void setChangeAddress(boolean changeAddress) {
		this.changeAddress = changeAddress;
	}
	public boolean isChangeCorpName() {
		return changeCorpName;
	}
	public void setChangeCorpName(boolean changeCorpName) {
		this.changeCorpName = changeCorpName;
	}
	public boolean isChangeCorpAddress() {
		return changeCorpAddress;
	}
	public void setChangeCorpAddress(boolean changeCorpAddress) {
		this.changeCorpAddress = changeCorpAddress;
	}
	public boolean isChangeCorpOwnerName() {
		return changeCorpOwnerName;
	}
	public void setChangeCorpOwnerName(boolean changeCorpOwnerName) {
		this.changeCorpOwnerName = changeCorpOwnerName;
	}
	public boolean isChangeCorpRefName() {
		return changeCorpRefName;
	}
	public void setChangeCorpRefName(boolean changeCorpRefName) {
		this.changeCorpRefName = changeCorpRefName;
	}
	public boolean isChangeCorpRefAddress() {
		return changeCorpRefAddress;
	}
	public void setChangeCorpRefAddress(boolean changeCorpRefAddress) {
		this.changeCorpRefAddress = changeCorpRefAddress;
	}
	public String getCorpPicZipcode() {
		return corpPicZipcode;
	}
	public void setCorpPicZipcode(String corpPicZipcode) {
		this.corpPicZipcode = corpPicZipcode;
	}
	public String getCorpPicPrefecture() {
		return corpPicPrefecture;
	}
	public void setCorpPicPrefecture(String corpPicPrefecture) {
		this.corpPicPrefecture = corpPicPrefecture;
	}
	public String getCorpPicCity() {
		return corpPicCity;
	}
	public void setCorpPicCity(String corpPicCity) {
		this.corpPicCity = corpPicCity;
	}
	public String getCorpPicSection() {
		return corpPicSection;
	}
	public void setCorpPicSection(String corpPicSection) {
		this.corpPicSection = corpPicSection;
	}
	public String getCorpPicBuildingName() {
		return corpPicBuildingName;
	}
	public void setCorpPicBuildingName(String corpPicBuildingName) {
		this.corpPicBuildingName = corpPicBuildingName;
	}
		public void setBeneficOwnerFirstname(String beneficOwnerFirstname) {
			this.beneficOwnerFirstname = beneficOwnerFirstname;
		}
		public String getBeneficOwnerLastname() {
			return beneficOwnerLastname;
		}
		public void setBeneficOwnerLastname(String beneficOwnerLastname) {
			this.beneficOwnerLastname = beneficOwnerLastname;
		}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	
	public String getNtdCustomerId() {
		return ntdCustomerId;
	}

	public void setNtdCustomerId(String ntdCustomerId) {
		this.ntdCustomerId = ntdCustomerId;
	}
	public String getCustomerServiceId() {
		return customerServiceId;
	}

	public void setCustomerServiceId(String customerServiceId) {
		this.customerServiceId = customerServiceId;
	}

	public String getGuruCustomerId() {
		return guruCustomerId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setGuruCustomerId(String guruCustomerId) {
		this.guruCustomerId = guruCustomerId;
	}
	
	public String getGuruNickName() {
		return guruNickName;
	}

	public void setGuruNickName(String guruNickName) {
		this.guruNickName = guruNickName;
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}
	
	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getVolume() {
		return volume;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	
	public String getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(String executionPrice) {
		this.executionPrice = executionPrice;
	}

	public String getLastTradeTime() {
		return lastTradeTime;
	}

	public void setLastTradeTime(String lastTradeTime) {
		this.lastTradeTime = lastTradeTime;
	}

	public String getBeneficOwnerFullname() {
		return beneficOwnerFullname;
	}
	public void setBeneficOwnerFullname(String beneficOwnerFullname) {
		this.beneficOwnerFullname = beneficOwnerFullname;
	}
	public String getBeneficOwnerFullnameKana() {
		return beneficOwnerFullnameKana;
	}
	public void setBeneficOwnerFullnameKana(String beneficOwnerFullnameKana) {
		this.beneficOwnerFullnameKana = beneficOwnerFullnameKana;
	}
	public String getBeneficOwnerFirstnameKana() {
		return beneficOwnerFirstnameKana;
	}
	public void setBeneficOwnerFirstnameKana(String beneficOwnerFirstnameKana) {
		this.beneficOwnerFirstnameKana = beneficOwnerFirstnameKana;
	}
	public String getBeneficOwnerLastnameKana() {
		return beneficOwnerLastnameKana;
	}
	public void setBeneficOwnerLastnameKana(String beneficOwnerLastnameKana) {
		this.beneficOwnerLastnameKana = beneficOwnerLastnameKana;
	}
	public String getFinancilAssetsName() {
		return financilAssetsName;
	}
	public void setFinancilAssetsName(String financilAssetsName) {
		this.financilAssetsName = financilAssetsName;
	}
	public Integer getFinancilAssets() {
		return financilAssets;
	}
	public void setFinancilAssets(Integer financilAssets) {
		this.financilAssets = financilAssets;
	}

	public boolean isPurposeShortTermFlg() {
		return purposeShortTermFlg;
	}

	public String getPurposeShortTermFlgName() {
		return purposeShortTermFlgName;
	}
	public void setPurposeShortTermFlgName(String purposeShortTermFlgName) {
		this.purposeShortTermFlgName = purposeShortTermFlgName;
	}
	public String getAccountOpenDate() {
		return accountOpenDate;
	}
	public void setAccountOpenDate(String accountOpenDate) {
		this.accountOpenDate = accountOpenDate;
	}
	public String getReturnRate() {
		return returnRate;
	}
	public void setReturnRate(String returnRate) {
		this.returnRate = returnRate;
	}
	public Integer getCopierNo() {
		return copierNo;
	}
	public void setCopierNo(Integer copierNo) {
		this.copierNo = copierNo;
	}
	public Integer getFollowerNo() {
		return FollowerNo;
	}
	public void setFollowerNo(Integer followerNo) {
		FollowerNo = followerNo;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the groupId
	 */
	public Integer getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	 * @return the loginPass
	 */
	public String getLoginPass() {
		return loginPass;
	}
	/**
	 * @param loginPass the loginPass to set
	 */
	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}
	/**
	 * @return the mailActiveCode
	 */
	public String getMailActiveCode() {
		return mailActiveCode;
	}
	/**
	 * @param mailActiveCode the mailActiveCode to set
	 */
	public void setMailActiveCode(String mailActiveCode) {
		this.mailActiveCode = mailActiveCode;
	}
	/**
	 * @return the leverageId
	 */
	public Integer getLeverage() {
		return leverage;
	}
	/**
	 * @param leverageId the leverageId to set
	 */
	public void setLeverage(Integer leverage) {
		this.leverage = leverage;
	}
	/**
	 * @return the corporationType
	 */
	public Integer getCorporationType() {
		return corporationType;
	}
	/**
	 * @param corporationType the corporationType to set
	 */
	public void setCorporationType(Integer corporationType) {
		this.corporationType = corporationType;
	}
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the firstNameKana
	 */
	public String getFirstNameKana() {
		return firstNameKana;
	}
	/**
	 * @param firstNameKana the firstNameKana to set
	 */
	public void setFirstNameKana(String firstNameKana) {
		this.firstNameKana = firstNameKana;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the lastNameKana
	 */
	public String getLastNameKana() {
		return lastNameKana;
	}
	/**
	 * @param lastNameKana the lastNameKana to set
	 */
	public void setLastNameKana(String lastNameKana) {
		this.lastNameKana = lastNameKana;
	}
	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}
	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	/**
	 * @return the sex
	 */
	public Integer getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	/**
	 * @return the mailMain
	 */
	public String getMailMain() {
		return mailMain;
	}
	/**
	 * @param mailMain the mailMain to set
	 */
	public void setMailMain(String mailMain) {
		this.mailMain = mailMain;
	}
	/**
	 * @return the mailAddtional
	 */
	public String getMailAddtional() {
		return mailAddtional;
	}
	/**
	 * @param mailAddtional the mailAddtional to set
	 */
	public void setMailAddtional(String mailAddtional) {
		this.mailAddtional = mailAddtional;
	}
	/**
	 * @return the mailMobile
	 */
	public String getMailMobile() {
		return mailMobile;
	}
	/**
	 * @param mailMobile the mailMobile to set
	 */
	public void setMailMobile(String mailMobile) {
		this.mailMobile = mailMobile;
	}
	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}
	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	/**
	 * @return the prefecture
	 */
	public String getPrefecture() {
		return prefecture;
	}
	/**
	 * @param prefecture the prefecture to set
	 */
	public void setPrefecture(String prefecture) {
		this.prefecture = prefecture;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the section
	 */
	public String getSection() {
		return section;
	}
	/**
	 * @param section the section to set
	 */
	public void setSection(String section) {
		this.section = section;
	}
	/**
	 * @return the buildingName
	 */
	public String getBuildingName() {
		return buildingName;
	}
	/**
	 * @param buildingName the buildingName to set
	 */
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	/**
	 * @return the houseNumber
	 */
	public String getHouseNumber() {
		return houseNumber;
	}
	/**
	 * @param houseNumber the houseNumber to set
	 */
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the tel1
	 */
	public String getTel1() {
		return tel1;
	}
	/**
	 * @param tel1 the tel1 to set
	 */
	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}
	/**
	 * @return the tel2
	 */
	public String getTel2() {
		return tel2;
	}
	/**
	 * @param tel2 the tel2 to set
	 */
	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}
	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}
	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}
	/**
	 * @return the jobType
	 */
	public Integer getJobType() {
		return jobType;
	}
	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}
	/**
	 * @return the industryType
	 */
	public Integer getIndustryType() {
		return industryType;
	}
	/**
	 * @param industryType the industryType to set
	 */
	public void setIndustryType(Integer industryType) {
		this.industryType = industryType;
	}
	/**
	 * @return the jobPlaceName
	 */
	public String getJobPlaceName() {
		return jobPlaceName;
	}
	/**
	 * @param jobPlaceName the jobPlaceName to set
	 */
	public void setJobPlaceName(String jobPlaceName) {
		this.jobPlaceName = jobPlaceName;
	}
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return the companyTel
	 */
	public String getCompanyTel() {
		return companyTel;
	}
	/**
	 * @param companyTel the companyTel to set
	 */
	public void setCompanyTel(String companyTel) {
		this.companyTel = companyTel;
	}
	/**
	 * @return the jobZipcode
	 */
	public String getJobZipcode() {
		return jobZipcode;
	}
	/**
	 * @param jobZipcode the jobZipcode to set
	 */
	public void setJobZipcode(String jobZipcode) {
		this.jobZipcode = jobZipcode;
	}
	/**
	 * @return the jobPrefecture
	 */
	public String getJobPrefecture() {
		return jobPrefecture;
	}
	/**
	 * @param jobPrefecture the jobPrefecture to set
	 */
	public void setJobPrefecture(String jobPrefecture) {
		this.jobPrefecture = jobPrefecture;
	}
	/**
	 * @return the jobWard
	 */
	public String getJobWard() {
		return jobWard;
	}
	/**
	 * @param jobWard the jobWard to set
	 */
	public void setJobWard(String jobWard) {
		this.jobWard = jobWard;
	}
	/**
	 * @return the jobBuilding
	 */
	public String getJobBuilding() {
		return jobBuilding;
	}
	/**
	 * @param jobBuilding the jobBuilding to set
	 */
	public void setJobBuilding(String jobBuilding) {
		this.jobBuilding = jobBuilding;
	}
	/**
	 * @return the corpFullname
	 */
	public String getCorpFullname() {
		return corpFullname;
	}
	/**
	 * @param corpFullname the corpFullname to set
	 */
	public void setCorpFullname(String corpFullname) {
		this.corpFullname = corpFullname;
	}
	/**
	 * @return the corpFullnameKana
	 */
	public String getCorpFullnameKana() {
		return corpFullnameKana;
	}
	/**
	 * @param corpFullnameKana the corpFullnameKana to set
	 */
	public void setCorpFullnameKana(String corpFullnameKana) {
		this.corpFullnameKana = corpFullnameKana;
	}
	/**
	 * @return the corpIndustryType
	 */
	public Integer getCorpIndustryType() {
		return corpIndustryType;
	}
	/**
	 * @param corpIndustryType the corpIndustryType to set
	 */
	public void setCorpIndustryType(Integer corpIndustryType) {
		this.corpIndustryType = corpIndustryType;
	}
	/**
	 * @return the corpJobDetail
	 */
	public String getCorpJobDetail() {
		return corpJobDetail;
	}
	/**
	 * @param corpJobDetail the corpJobDetail to set
	 */
	public void setCorpJobDetail(String corpJobDetail) {
		this.corpJobDetail = corpJobDetail;
	}
	/**
	 * @return the corpEstablishDate
	 */
	public String getCorpEstablishDate() {
		return corpEstablishDate;
	}
	/**
	 * @param corpEstablishDate the corpEstablishDate to set
	 */
	public void setCorpEstablishDate(String corpEstablishDate) {
		this.corpEstablishDate = corpEstablishDate;
	}
	/**
	 * @return the corpSettleDate
	 */
	public String getCorpSettleDate() {
		return corpSettleDate;
	}
	/**
	 * @param corpSettleDate the corpSettleDate to set
	 */
	public void setCorpSettleDate(String corpSettleDate) {
		this.corpSettleDate = corpSettleDate;
	}
	/**
	 * @return the corpHomepage
	 */
	public String getCorpHomepage() {
		return corpHomepage;
	}
	/**
	 * @param corpHomepage the corpHomepage to set
	 */
	public void setCorpHomepage(String corpHomepage) {
		this.corpHomepage = corpHomepage;
	}
	/**
	 * @return the corpRep
	 */
	public String getCorpRep() {
		return corpRep;
	}
	/**
	 * @param corpRep the corpRep to set
	 */
	public void setCorpRep(String corpRep) {
		this.corpRep = corpRep;
	}
	/**
	 * @return the corpRepFirstname
	 */
	public String getCorpRepFirstname() {
		return corpRepFirstname;
	}
	/**
	 * @param corpRepFirstname the corpRepFirstname to set
	 */
	public void setCorpRepFirstname(String corpRepFirstname) {
		this.corpRepFirstname = corpRepFirstname;
	}
	/**
	 * @return the corpRepLastname
	 */
	public String getCorpRepLastname() {
		return corpRepLastname;
	}
	/**
	 * @param corpRepLastname the corpRepLastname to set
	 */
	public void setCorpRepLastname(String corpRepLastname) {
		this.corpRepLastname = corpRepLastname;
	}
	/**
	 * @return the corpRepFirstnameKana
	 */
	public String getCorpRepFirstnameKana() {
		return corpRepFirstnameKana;
	}
	/**
	 * @param corpRepFirstnameKana the corpRepFirstnameKana to set
	 */
	public void setCorpRepFirstnameKana(String corpRepFirstnameKana) {
		this.corpRepFirstnameKana = corpRepFirstnameKana;
	}
	/**
	 * @return the corpRepLastnameKana
	 */
	public String getCorpRepLastnameKana() {
		return corpRepLastnameKana;
	}
	/**
	 * @param corpRepLastnameKana the corpRepLastnameKana to set
	 */
	public void setCorpRepLastnameKana(String corpRepLastnameKana) {
		this.corpRepLastnameKana = corpRepLastnameKana;
	}
	/**
	 * @return the corpPicDep
	 */
	public String getCorpPicDep() {
		return corpPicDep;
	}
	/**
	 * @param corpPicDep the corpPicDep to set
	 */
	public void setCorpPicDep(String corpPicDep) {
		this.corpPicDep = corpPicDep;
	}
	/**
	 * @return the corpPicPosition
	 */
	public String getCorpPicPosition() {
		return corpPicPosition;
	}
	/**
	 * @param corpPicPosition the corpPicPosition to set
	 */
	public void setCorpPicPosition(String corpPicPosition) {
		this.corpPicPosition = corpPicPosition;
	}
	/**
	 * @return the corpPicFirstname
	 */
	public String getCorpPicFirstname() {
		return corpPicFirstname;
	}
	/**
	 * @param corpPicFirstname the corpPicFirstname to set
	 */
	public void setCorpPicFirstname(String corpPicFirstname) {
		this.corpPicFirstname = corpPicFirstname;
	}
	/**
	 * @return the corpPicFirstnameKana
	 */
	public String getCorpPicFirstnameKana() {
		return corpPicFirstnameKana;
	}
	/**
	 * @param corpPicFirstnameKana the corpPicFirstnameKana to set
	 */
	public void setCorpPicFirstnameKana(String corpPicFirstnameKana) {
		this.corpPicFirstnameKana = corpPicFirstnameKana;
	}
	/**
	 * @return the corpPicLastname
	 */
	public String getCorpPicLastname() {
		return corpPicLastname;
	}
	/**
	 * @param corpPicLastname the corpPicLastname to set
	 */
	public void setCorpPicLastname(String corpPicLastname) {
		this.corpPicLastname = corpPicLastname;
	}
	/**
	 * @return the corpPicLastnameKana
	 */
	public String getCorpPicLastnameKana() {
		return corpPicLastnameKana;
	}
	/**
	 * @param corpPicLastnameKana the corpPicLastnameKana to set
	 */
	public void setCorpPicLastnameKana(String corpPicLastnameKana) {
		this.corpPicLastnameKana = corpPicLastnameKana;
	}
	/**
	 * @return the corpPicSex
	 */
	public Integer getCorpPicSex() {
		return corpPicSex;
	}
	/**
	 * @param corpPicSex the corpPicSex to set
	 */
	public void setCorpPicSex(Integer corpPicSex) {
		this.corpPicSex = corpPicSex;
	}
	/**
	 * @return the corpPicTel
	 */
	public String getCorpPicTel() {
		return corpPicTel;
	}
	/**
	 * @param corpPicTel the corpPicTel to set
	 */
	public void setCorpPicTel(String corpPicTel) {
		this.corpPicTel = corpPicTel;
	}
	/**
	 * @return the corpPicMobile
	 */
	public String getCorpPicMobile() {
		return corpPicMobile;
	}
	/**
	 * @param corpPicMobile the corpPicMobile to set
	 */
	public void setCorpPicMobile(String corpPicMobile) {
		this.corpPicMobile = corpPicMobile;
	}
	/**
	 * @return the corpPicMailPc
	 */
	public String getCorpPicMailPc() {
		return corpPicMailPc;
	}
	/**
	 * @param corpPicMailPc the corpPicMailPc to set
	 */
	public void setCorpPicMailPc(String corpPicMailPc) {
		this.corpPicMailPc = corpPicMailPc;
	}
	/**
	 * @return the corpPicMailMobile
	 */
	public String getCorpPicMailMobile() {
		return corpPicMailMobile;
	}
	/**
	 * @param corpPicMailMobile the corpPicMailMobile to set
	 */
	public void setCorpPicMailMobile(String corpPicMailMobile) {
		this.corpPicMailMobile = corpPicMailMobile;
	}
	/**
	 * @return the bankAccClass
	 */
	public Integer getBankAccClass() {
		return bankAccClass;
	}
	/**
	 * @param bankAccClass the bankAccClass to set
	 */
	public void setBankAccClass(Integer bankAccClass) {
		this.bankAccClass = bankAccClass;
	}
	/**
	 * @return the bankAccNumber
	 */
	public String getBankAccNumber() {
		return bankAccNumber;
	}
	/**
	 * @param bankAccNumber the bankAccNumber to set
	 */
	public void setBankAccNumber(String bankAccNumber) {
		this.bankAccNumber = bankAccNumber;
	}
	/**
	 * @return the documentSendStatus
	 */
	public Integer getDocumentSendStatus() {
		return documentSendStatus;
	}
	/**
	 * @param documentSendStatus the documentSendStatus to set
	 */
	public void setDocumentSendStatus(Integer documentSendStatus) {
		this.documentSendStatus = documentSendStatus;
	}
	/**
	 * @return the documentPostDate
	 */
	public String getDocumentPostDate() {
		return documentPostDate;
	}
	/**
	 * @param documentPostDate the documentPostDate to set
	 */
	public void setDocumentPostDate(String documentPostDate) {
		this.documentPostDate = documentPostDate;
	}
	/**
	 * @return the documentAcceptDate
	 */
	public String getDocumentAcceptDate() {
		return documentAcceptDate;
	}
	/**
	 * @param documentAcceptDate the documentAcceptDate to set
	 */
	public void setDocumentAcceptDate(String documentAcceptDate) {
		this.documentAcceptDate = documentAcceptDate;
	}
	/**
	 * @return the accountOpenStatus
	 */
	public Integer getAccountOpenStatus() {
		return accountOpenStatus;
	}
	/**
	 * @param accountOpenStatus the accountOpenStatus to set
	 */
	public void setAccountOpenStatus(Integer accountOpenStatus) {
		this.accountOpenStatus = accountOpenStatus;
	}
	/**
	 * @return the allowChangePassFlg
	 */
	public Integer getAllowChangePassFlg() {
		return allowChangePassFlg;
	}
	/**
	 * @param allowChangePassFlg the allowChangePassFlg to set
	 */
	public void setAllowChangePassFlg(Integer allowChangePassFlg) {
		this.allowChangePassFlg = allowChangePassFlg;
	}
	/**
	 * @return the allowNewOrderFlg
	 */
	public Integer getAllowNewOrderFlg() {
		return allowNewOrderFlg;
	}
	/**
	 * @param allowNewOrderFlg the allowNewOrderFlg to set
	 */
	public void setAllowNewOrderFlg(Integer allowNewOrderFlg) {
		this.allowNewOrderFlg = allowNewOrderFlg;
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
	 * @return the wlCustomerId
	 */
	public String getWlCustomerId() {
		return wlCustomerId;
	}
	/**
	 * @param wlCustomerId the wlCustomerId to set
	 */
	public void setWlCustomerId(String wlCustomerId) {
		this.wlCustomerId = wlCustomerId;
	}
	/**
	 * @return the confirm1Flg
	 */
	public Integer getConfirm1Flg() {
		return confirm1Flg;
	}
	/**
	 * @param confirm1Flg the confirm1Flg to set
	 */
	public void setConfirm1Flg(Integer confirm1Flg) {
		this.confirm1Flg = confirm1Flg;
	}
	/**
	 * @return the confirm2Flg
	 */
	public Integer getConfirm2Flg() {
		return confirm2Flg;
	}
	/**
	 * @param confirm2Flg the confirm2Flg to set
	 */
	public void setConfirm2Flg(Integer confirm2Flg) {
		this.confirm2Flg = confirm2Flg;
	}
	/**
	 * @return the confirm3Flg
	 */
	public Integer getConfirm3Flg() {
		return confirm3Flg;
	}
	/**
	 * @param confirm3Flg the confirm3Flg to set
	 */
	public void setConfirm3Flg(Integer confirm3Flg) {
		this.confirm3Flg = confirm3Flg;
	}
	/**
	 * @return the confirm4Flg
	 */
	public Integer getConfirm4Flg() {
		return confirm4Flg;
	}
	/**
	 * @param confirm4Flg the confirm4Flg to set
	 */
	public void setConfirm4Flg(Integer confirm4Flg) {
		this.confirm4Flg = confirm4Flg;
	}
	/**
	 * @return the accountClass
	 */
	public Integer getAccountClass() {
		return accountClass;
	}
	/**
	 * @param accountClass the accountClass to set
	 */
	public void setAccountClass(Integer accountClass) {
		this.accountClass = accountClass;
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
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return the changePasswordFlag
	 */
	public Boolean getChangePasswordFlag() {
		return changePasswordFlag == null ? false : changePasswordFlag;
	}
	/**
	 * @param changePasswordFlag the changePasswordFlag to set
	 */
	public void setChangePasswordFlag(Boolean changePasswordFlag) {
		this.changePasswordFlag = changePasswordFlag;
	}
	/**
	 * @return the allowWithdrawalFlg
	 */
	public Integer getAllowWithdrawalFlg() {
		return allowWithdrawalFlg;
	}
	/**
	 * @param allowWithdrawalFlg the allowWithdrawalFlg to set
	 */
	public void setAllowWithdrawalFlg(Integer allowWithdrawalFlg) {
		this.allowWithdrawalFlg = allowWithdrawalFlg;
	}
	public String getComfirmedPassword() {
		return comfirmedPassword;
	}
	public void setComfirmedPassword(String comfirmedPassword) {
		this.comfirmedPassword = comfirmedPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getIdentifyPassword() {
		return identifyPassword;
	}
	public void setIdentifyPassword(String identifyPassword) {
		this.identifyPassword = identifyPassword;
	}
	public String getMt4Id() {
		return mt4Id;
	}
	public void setMt4Id(String mt4Id) {
		this.mt4Id = mt4Id;
	}
	public String getIbLink() {
		return ibLink;
	}
	public void setIbLink(String ibLink) {
		this.ibLink = ibLink;
	}
	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return the displayLanguage
	 */
	public String getDisplayLanguage() {
		return displayLanguage;
	}
	/**
	 * @param displayLanguage the displayLanguage to set
	 */
	public void setDisplayLanguage(String displayLanguage) {
		this.displayLanguage = displayLanguage;
	}
	/**
	 * @return the displayLanguageName
	 */
	public String getDisplayLanguageName() {
		return displayLanguageName;
	}
	/**
	 * @param displayLanguageName the displayLanguageName to set
	 */
	public void setDisplayLanguageName(String displayLanguageName) {
		this.displayLanguageName = displayLanguageName;
	}
	public String getCurrencyAms() {
		return currencyAms;
	}
	public void setCurrencyAms(String currencyAms) {
		this.currencyAms = currencyAms;
	}
	public String getCurrencyFx() {
		return currencyFx;
	}
	public void setCurrencyFx(String currencyFx) {
		this.currencyFx = currencyFx;
	}
	public String getCurrencyBo() {
		return currencyBo;
	}
	public void setCurrencyBo(String currencyBo) {
		this.currencyBo = currencyBo;
	}

    public String getCurrencyCopyTrade() {
        return currencyCopyTrade;
    }

    public void setCurrencyCopyTrade(String currencyCopyTrade) {
        this.currencyCopyTrade = currencyCopyTrade;
    }

    public Integer getVerifyPhoneStatus() {
		return verifyPhoneStatus;
	}
	public void setVerifyPhoneStatus(Integer verifyPhoneStatus) {
		this.verifyPhoneStatus = verifyPhoneStatus;
	}
	public Integer getVerifyPassportStatus() {
		return verifyPassportStatus;
	}
	public void setVerifyPassportStatus(Integer verifyPassportStatus) {
		this.verifyPassportStatus = verifyPassportStatus;
	}
	public Integer getVerifyAddressStatus() {
		return verifyAddressStatus;
	}
	public void setVerifyAddressStatus(Integer verifyAddressStatus) {
		this.verifyAddressStatus = verifyAddressStatus;
	}
	public Integer getVerifySignatureStatus() {
		return verifySignatureStatus;
	}
	public void setVerifySignatureStatus(Integer verifySignatureStatus) {
		this.verifySignatureStatus = verifySignatureStatus;
	}
	public List<DocumentInfo> getPassportDocs() {
		return passportDocs;
	}
	public void setPassportDocs(List<DocumentInfo> passportDocs) {
		this.passportDocs = passportDocs;
	}
	public List<DocumentInfo> getAddressDocs() {
		return addressDocs;
	}
	public void setAddressDocs(List<DocumentInfo> addressDocs) {
		this.addressDocs = addressDocs;
	}
	public List<DocumentInfo> getSignatureDocs() {
		return signatureDocs;
	}
	public void setSignatureDocs(List<DocumentInfo> signatureDocs) {
		this.signatureDocs = signatureDocs;
	}

    public String getConfirmMailMain() {
        return confirmMailMain;
    }

    public void setConfirmMailMain(String confirmMailMain) {
        this.confirmMailMain = confirmMailMain;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public Integer getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(Integer phoneType) {
        this.phoneType = phoneType;
    }

    public boolean isFxBoFlag() {
        return fxBoFlag;
    }

    public void setFxBoFlag(boolean fxBoFlag) {
        this.fxBoFlag = fxBoFlag;
    }

    public boolean isDemoFxFlag() {
        return demoFxFlag;
    }

    public void setDemoFxFlag(boolean demoFxFlag) {
        this.demoFxFlag = demoFxFlag;
    }
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @return the brokerCd
	 */
	public String getBrokerCd() {
		return brokerCd;
	}
	/**
	 * @param brokerCd the brokerCd to set
	 */
	public void setBrokerCd(String brokerCd) {
		this.brokerCd = brokerCd;
	}
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
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
	 * @return the md5IndentifyPassword
	 */
	public String getMd5IndentifyPassword() {
		return md5IndentifyPassword;
	}

	/**
	 * @param md5IndentifyPassword the md5IndentifyPassword to set
	 */
	public void setMd5IndentifyPassword(String md5IndentifyPassword) {
		this.md5IndentifyPassword = md5IndentifyPassword;
	}

	public boolean isChangePass() {
		return changePass;
	}

	public void setChangePass(boolean changePass) {
		this.changePass = changePass;
	}

	public boolean isChangeMailMain() {
		return changeMailMain;
	}

	public void setChangeMailMain(boolean changeMailMain) {
		this.changeMailMain = changeMailMain;
	}

	public String getAccountApplicationDate() {
		return accountApplicationDate;
	}

	public void setAccountApplicationDate(String accountApplicationDate) {
		this.accountApplicationDate = accountApplicationDate;
	}

	public boolean hasDocumentUpload() {
		return hasDocumentUpload;
	}

	public void setHasDocumentUpload(boolean hasDocumentUpload) {
		this.hasDocumentUpload = hasDocumentUpload;
	}

	public boolean isNeedReloadCache() {
		return needReloadCache;
	}

	public void setNeedReloadCache(boolean needReloadCache) {
		this.needReloadCache = needReloadCache;
	}
	
	public String getOldLoginId() {
		return oldLoginId;
	}

	public void setOldLoginId(String oldLoginId) {
		this.oldLoginId = oldLoginId;
	}

	public Integer getBeneficOwnerFlg2() {
		return beneficOwnerFlg2;
	}

	public void setBeneficOwnerFlg2(Integer beneficOwnerFlg2) {
		this.beneficOwnerFlg2 = beneficOwnerFlg2;
	}

	public String getBeneficOwnerFullname2() {
		return beneficOwnerFullname2;
	}

	public void setBeneficOwnerFullname2(String beneficOwnerFullname2) {
		this.beneficOwnerFullname2 = beneficOwnerFullname2;
	}

	public String getBeneficOwnerFullnameKana2() {
		return beneficOwnerFullnameKana2;
	}

	public void setBeneficOwnerFullnameKana2(String beneficOwnerFullnameKana2) {
		this.beneficOwnerFullnameKana2 = beneficOwnerFullnameKana2;
	}

	public String getBeneficOwnerFirstnameKana2() {
		return beneficOwnerFirstnameKana2;
	}

	public void setBeneficOwnerFirstnameKana2(String beneficOwnerFirstnameKana2) {
		this.beneficOwnerFirstnameKana2 = beneficOwnerFirstnameKana2;
	}

	public String getBeneficOwnerLastnameKana2() {
		return beneficOwnerLastnameKana2;
	}

	public void setBeneficOwnerLastnameKana2(String beneficOwnerLastnameKana2) {
		this.beneficOwnerLastnameKana2 = beneficOwnerLastnameKana2;
	}

	public String getBeneficOwnerFirstname2() {
		return beneficOwnerFirstname2;
	}

	public void setBeneficOwnerFirstname2(String beneficOwnerFirstname2) {
		this.beneficOwnerFirstname2 = beneficOwnerFirstname2;
	}

	public String getBeneficOwnerLastname2() {
		return beneficOwnerLastname2;
	}

	public void setBeneficOwnerLastname2(String beneficOwnerLastname2) {
		this.beneficOwnerLastname2 = beneficOwnerLastname2;
	}

	public String getBeneficOwnerEstablishDate2() {
		return beneficOwnerEstablishDate2;
	}

	public void setBeneficOwnerEstablishDate2(String beneficOwnerEstablishDate2) {
		this.beneficOwnerEstablishDate2 = beneficOwnerEstablishDate2;
	}

	public String getBeneficOwnerZipcode2() {
		return beneficOwnerZipcode2;
	}

	public void setBeneficOwnerZipcode2(String beneficOwnerZipcode2) {
		this.beneficOwnerZipcode2 = beneficOwnerZipcode2;
	}

	public String getBeneficOwnerPrefecture2() {
		return beneficOwnerPrefecture2;
	}

	public void setBeneficOwnerPrefecture2(String beneficOwnerPrefecture2) {
		this.beneficOwnerPrefecture2 = beneficOwnerPrefecture2;
	}

	public String getBeneficOwnerCity2() {
		return beneficOwnerCity2;
	}

	public void setBeneficOwnerCity2(String beneficOwnerCity2) {
		this.beneficOwnerCity2 = beneficOwnerCity2;
	}

	public String getBeneficOwnerSection2() {
		return beneficOwnerSection2;
	}

	public void setBeneficOwnerSection2(String beneficOwnerSection2) {
		this.beneficOwnerSection2 = beneficOwnerSection2;
	}

	public String getBeneficOwnerBuildingName2() {
		return beneficOwnerBuildingName2;
	}

	public void setBeneficOwnerBuildingName2(String beneficOwnerBuildingName2) {
		this.beneficOwnerBuildingName2 = beneficOwnerBuildingName2;
	}

	public String getBeneficOwnerTel2() {
		return beneficOwnerTel2;
	}

	public void setBeneficOwnerTel2(String beneficOwnerTel2) {
		this.beneficOwnerTel2 = beneficOwnerTel2;
	}

	public String getBeneficOwnerEstablishDateYear2() {
		return beneficOwnerEstablishDateYear2;
	}

	public void setBeneficOwnerEstablishDateYear2(String beneficOwnerEstablishDateYear2) {
		this.beneficOwnerEstablishDateYear2 = beneficOwnerEstablishDateYear2;
	}

	public String getBeneficOwnerEstablishDateMonth2() {
		return beneficOwnerEstablishDateMonth2;
	}

	public void setBeneficOwnerEstablishDateMonth2(String beneficOwnerEstablishDateMonth2) {
		this.beneficOwnerEstablishDateMonth2 = beneficOwnerEstablishDateMonth2;
	}

	public String getBeneficOwnerEstablishDateDay2() {
		return beneficOwnerEstablishDateDay2;
	}

	public void setBeneficOwnerEstablishDateDay2(String beneficOwnerEstablishDateDay2) {
		this.beneficOwnerEstablishDateDay2 = beneficOwnerEstablishDateDay2;
	}

	public Integer getBeneficOwnerFlg3() {
		return beneficOwnerFlg3;
	}

	public void setBeneficOwnerFlg3(Integer beneficOwnerFlg3) {
		this.beneficOwnerFlg3 = beneficOwnerFlg3;
	}

	public String getBeneficOwnerFullname3() {
		return beneficOwnerFullname3;
	}

	public void setBeneficOwnerFullname3(String beneficOwnerFullname3) {
		this.beneficOwnerFullname3 = beneficOwnerFullname3;
	}

	public String getBeneficOwnerFullnameKana3() {
		return beneficOwnerFullnameKana3;
	}

	public void setBeneficOwnerFullnameKana3(String beneficOwnerFullnameKana3) {
		this.beneficOwnerFullnameKana3 = beneficOwnerFullnameKana3;
	}

	public String getBeneficOwnerFirstnameKana3() {
		return beneficOwnerFirstnameKana3;
	}

	public void setBeneficOwnerFirstnameKana3(String beneficOwnerFirstnameKana3) {
		this.beneficOwnerFirstnameKana3 = beneficOwnerFirstnameKana3;
	}

	public String getBeneficOwnerLastnameKana3() {
		return beneficOwnerLastnameKana3;
	}

	public void setBeneficOwnerLastnameKana3(String beneficOwnerLastnameKana3) {
		this.beneficOwnerLastnameKana3 = beneficOwnerLastnameKana3;
	}

	public String getBeneficOwnerFirstname3() {
		return beneficOwnerFirstname3;
	}

	public void setBeneficOwnerFirstname3(String beneficOwnerFirstname3) {
		this.beneficOwnerFirstname3 = beneficOwnerFirstname3;
	}

	public String getBeneficOwnerLastname3() {
		return beneficOwnerLastname3;
	}

	public void setBeneficOwnerLastname3(String beneficOwnerLastname3) {
		this.beneficOwnerLastname3 = beneficOwnerLastname3;
	}

	public String getBeneficOwnerEstablishDate3() {
		return beneficOwnerEstablishDate3;
	}

	public void setBeneficOwnerEstablishDate3(String beneficOwnerEstablishDate3) {
		this.beneficOwnerEstablishDate3 = beneficOwnerEstablishDate3;
	}

	public String getBeneficOwnerZipcode3() {
		return beneficOwnerZipcode3;
	}

	public void setBeneficOwnerZipcode3(String beneficOwnerZipcode3) {
		this.beneficOwnerZipcode3 = beneficOwnerZipcode3;
	}

	public String getBeneficOwnerPrefecture3() {
		return beneficOwnerPrefecture3;
	}

	public void setBeneficOwnerPrefecture3(String beneficOwnerPrefecture3) {
		this.beneficOwnerPrefecture3 = beneficOwnerPrefecture3;
	}

	public String getBeneficOwnerCity3() {
		return beneficOwnerCity3;
	}

	public void setBeneficOwnerCity3(String beneficOwnerCity3) {
		this.beneficOwnerCity3 = beneficOwnerCity3;
	}

	public String getBeneficOwnerSection3() {
		return beneficOwnerSection3;
	}

	public void setBeneficOwnerSection3(String beneficOwnerSection3) {
		this.beneficOwnerSection3 = beneficOwnerSection3;
	}

	public String getBeneficOwnerBuildingName3() {
		return beneficOwnerBuildingName3;
	}

	public void setBeneficOwnerBuildingName3(String beneficOwnerBuildingName3) {
		this.beneficOwnerBuildingName3 = beneficOwnerBuildingName3;
	}

	public String getBeneficOwnerTel3() {
		return beneficOwnerTel3;
	}

	public void setBeneficOwnerTel3(String beneficOwnerTel3) {
		this.beneficOwnerTel3 = beneficOwnerTel3;
	}

	public String getBeneficOwnerEstablishDateYear3() {
		return beneficOwnerEstablishDateYear3;
	}

	public void setBeneficOwnerEstablishDateYear3(String beneficOwnerEstablishDateYear3) {
		this.beneficOwnerEstablishDateYear3 = beneficOwnerEstablishDateYear3;
	}

	public String getBeneficOwnerEstablishDateMonth3() {
		return beneficOwnerEstablishDateMonth3;
	}

	public void setBeneficOwnerEstablishDateMonth3(String beneficOwnerEstablishDateMonth3) {
		this.beneficOwnerEstablishDateMonth3 = beneficOwnerEstablishDateMonth3;
	}

	public String getBeneficOwnerEstablishDateDay3() {
		return beneficOwnerEstablishDateDay3;
	}

	public void setBeneficOwnerEstablishDateDay3(String beneficOwnerEstablishDateDay3) {
		this.beneficOwnerEstablishDateDay3 = beneficOwnerEstablishDateDay3;
	}

	public boolean isEaAccount() {
		return eaAccount;
	}

	public void setEaAccount(boolean eaAccount) {
		this.eaAccount = eaAccount;
	}

	@Override
	public String toString() {
		return "CustomerInfo{" +
				"ntdCustomerId='" + ntdCustomerId + '\'' +
				", customerId='" + customerId + '\'' +
				", customerServiceId='" + customerServiceId + '\'' +
				", guruCustomerId='" + guruCustomerId + '\'' +
				", guruNickName='" + guruNickName + '\'' +
				", eventDateTime='" + eventDateTime + '\'' +
				", orderDate='" + orderDate + '\'' +
				", symbol='" + symbol + '\'' +
				", volume='" + volume + '\'' +
				", orderId='" + orderId + '\'' +
				", orderType='" + orderType + '\'' +
				", tradeType='" + tradeType + '\'' +
				", executionPrice='" + executionPrice + '\'' +
				", lastTradeTime='" + lastTradeTime + '\'' +
				", groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", currencyCode='" + currencyCode + '\'' +
				", countryId=" + countryId +
				", loginId='" + loginId + '\'' +
				", loginPass='" + loginPass + '\'' +
				", mailActiveCode='" + mailActiveCode + '\'' +
				", leverage=" + leverage +
				", corporationType=" + corporationType +
				", fullName='" + fullName + '\'' +
				", firstName='" + firstName + '\'' +
				", firstNameKana='" + firstNameKana + '\'' +
				", lastName='" + lastName + '\'' +
				", lastNameKana='" + lastNameKana + '\'' +
				", birthday='" + birthday + '\'' +
				", sex=" + sex +
				", mailMain='" + mailMain + '\'' +
				", confirmMailMain='" + confirmMailMain + '\'' +
				", phoneCode='" + phoneCode + '\'' +
				", phoneType=" + phoneType +
				", fxBoFlag=" + fxBoFlag +
				", demoFxFlag=" + demoFxFlag +
				", mailAddtional='" + mailAddtional + '\'' +
				", mailMobile='" + mailMobile + '\'' +
				", zipcode='" + zipcode + '\'' +
				", prefecture='" + prefecture + '\'' +
				", city='" + city + '\'' +
				", section='" + section + '\'' +
				", buildingName='" + buildingName + '\'' +
				", houseNumber='" + houseNumber + '\'' +
				", address='" + address + '\'' +
				", tel1='" + tel1 + '\'' +
				", tel2='" + tel2 + '\'' +
				", fax='" + fax + '\'' +
				", jobType=" + jobType +
				", industryType=" + industryType +
				", jobPlaceName='" + jobPlaceName + '\'' +
				", companyName='" + companyName + '\'' +
				", companyTel='" + companyTel + '\'' +
				", jobZipcode='" + jobZipcode + '\'' +
				", jobPrefecture='" + jobPrefecture + '\'' +
				", jobWard='" + jobWard + '\'' +
				", jobBuilding='" + jobBuilding + '\'' +
				", corpFullname='" + corpFullname + '\'' +
				", corpFullnameKana='" + corpFullnameKana + '\'' +
				", corpIndustryType=" + corpIndustryType +
				", corpJobDetail='" + corpJobDetail + '\'' +
				", corpEstablishDate='" + corpEstablishDate + '\'' +
				", corpSettleDate='" + corpSettleDate + '\'' +
				", corpHomepage='" + corpHomepage + '\'' +
				", corpRep='" + corpRep + '\'' +
				", corpRepFirstname='" + corpRepFirstname + '\'' +
				", corpRepLastname='" + corpRepLastname + '\'' +
				", corpRepFirstnameKana='" + corpRepFirstnameKana + '\'' +
				", corpRepLastnameKana='" + corpRepLastnameKana + '\'' +
				", corpPicDep='" + corpPicDep + '\'' +
				", corpPicPosition='" + corpPicPosition + '\'' +
				", corpPicFirstname='" + corpPicFirstname + '\'' +
				", corpPicFirstnameKana='" + corpPicFirstnameKana + '\'' +
				", corpPicLastname='" + corpPicLastname + '\'' +
				", corpPicLastnameKana='" + corpPicLastnameKana + '\'' +
				", corpPicSex=" + corpPicSex +
				", corpPicTel='" + corpPicTel + '\'' +
				", corpPicMobile='" + corpPicMobile + '\'' +
				", corpPicMailPc='" + corpPicMailPc + '\'' +
				", corpPicMailMobile='" + corpPicMailMobile + '\'' +
				", bankAccClass=" + bankAccClass +
				", bankAccNumber='" + bankAccNumber + '\'' +
				", documentSendStatus=" + documentSendStatus +
				", documentPostDate='" + documentPostDate + '\'' +
				", documentAcceptDate='" + documentAcceptDate + '\'' +
				", accountOpenStatus=" + accountOpenStatus +
				", allowChangePassFlg=" + allowChangePassFlg +
				", allowNewOrderFlg=" + allowNewOrderFlg +
				", note='" + note + '\'' +
				", wlCode='" + wlCode + '\'' +
				", wlCustomerId='" + wlCustomerId + '\'' +
				", confirm1Flg=" + confirm1Flg +
				", confirm2Flg=" + confirm2Flg +
				", confirm3Flg=" + confirm3Flg +
				", confirm4Flg=" + confirm4Flg +
				", accountClass=" + accountClass +
				", activeFlag=" + activeFlag +
				", inputDate=" + inputDate +
				", updateDate=" + updateDate +
				", serviceType='" + serviceType + '\'' +
				", countryName='" + countryName + '\'' +
				", countryCode='" + countryCode + '\'' +
				", changePasswordFlag=" + changePasswordFlag +
				", allowWithdrawalFlg=" + allowWithdrawalFlg +
				", comfirmedPassword='" + "******" + '\'' +
				", newPassword='" + "******" + '\'' +
				", identifyPassword='" + "******" + '\'' +
				", md5IndentifyPassword='" + "******" + '\'' +
				", mt4Id='" + mt4Id + '\'' +
				", ibLink='" + ibLink + '\'' +
				", day='" + day + '\'' +
				", month='" + month + '\'' +
				", year='" + year + '\'' +
				", displayLanguage='" + displayLanguage + '\'' +
				", displayLanguageName='" + displayLanguageName + '\'' +
				", accountApplicationDate='" + accountApplicationDate + '\'' +
				", currencyAms='" + currencyAms + '\'' +
				", currencyFx='" + currencyFx + '\'' +
				", currencyBo='" + currencyBo + '\'' +
				", currencyCopyTrade='" + currencyCopyTrade + '\'' +
				", verifyPhoneStatus=" + verifyPhoneStatus +
				", verifyPassportStatus=" + verifyPassportStatus +
				", verifyAddressStatus=" + verifyAddressStatus +
				", verifySignatureStatus=" + verifySignatureStatus +
				", passportDocs=" + passportDocs +
				", addressDocs=" + addressDocs +
				", signatureDocs=" + signatureDocs +
				", description='" + description + '\'' +
				", username='" + username + '\'' +
				", returnRate='" + returnRate + '\'' +
				", copierNo=" + copierNo +
				", FollowerNo=" + FollowerNo +
				", brokerCd='" + brokerCd + '\'' +
				", accountId='" + accountId + '\'' +
				", accountOpenDate='" + accountOpenDate + '\'' +
				", financilAssetsName='" + financilAssetsName + '\'' +
				", financilAssets=" + financilAssets +
				", purposeShortTermFlg=" + purposeShortTermFlg +
				", purposeLongTermFlg=" + purposeLongTermFlg +
				", purposeExchangeFlg=" + purposeExchangeFlg +
				", purposeSwapFlg=" + purposeSwapFlg +
				", purposeHedgeAssetFlg=" + purposeHedgeAssetFlg +
				", purposeHighIntFlg=" + purposeHighIntFlg +
				", purposeEconomicFlg=" + purposeEconomicFlg +
				", purposeOther=" + purposeOther +
				", purposeShortTermFlgName='" + purposeShortTermFlgName + '\'' +
				", purposeLongTermFlgName='" + purposeLongTermFlgName + '\'' +
				", purposeExchangeFlgName='" + purposeExchangeFlgName + '\'' +
				", purposeSwapFlgName='" + purposeSwapFlgName + '\'' +
				", purposeHedgeAssetFlgName='" + purposeHedgeAssetFlgName + '\'' +
				", purposeHighIntFlgName='" + purposeHighIntFlgName + '\'' +
				", purposeEconomicFlgName='" + purposeEconomicFlgName + '\'' +
				", purposeOtherName='" + purposeOtherName + '\'' +
				", purposeOtherComment='" + purposeOtherComment + '\'' +
				", beneficOwnerFlg=" + beneficOwnerFlg +
				", beneficOwnerFullname='" + beneficOwnerFullname + '\'' +
				", beneficOwnerFullnameKana='" + beneficOwnerFullnameKana + '\'' +
				", beneficOwnerFirstnameKana='" + beneficOwnerFirstnameKana + '\'' +
				", beneficOwnerLastnameKana='" + beneficOwnerLastnameKana + '\'' +
				", beneficOwnerFirstname='" + beneficOwnerFirstname + '\'' +
				", beneficOwnerLastname='" + beneficOwnerLastname + '\'' +
				", beneficOwnerEstablishDate='" + beneficOwnerEstablishDate + '\'' +
				", beneficOwnerZipcode='" + beneficOwnerZipcode + '\'' +
				", beneficOwnerPrefecture='" + beneficOwnerPrefecture + '\'' +
				", beneficOwnerCity='" + beneficOwnerCity + '\'' +
				", beneficOwnerSection='" + beneficOwnerSection + '\'' +
				", beneficOwnerBuildingName='" + beneficOwnerBuildingName + '\'' +
				", beneficOwnerTel='" + beneficOwnerTel + '\'' +
				", beneficOwnerEstablishDateYear='" + beneficOwnerEstablishDateYear + '\'' +
				", beneficOwnerEstablishDateMonth='" + beneficOwnerEstablishDateMonth + '\'' +
				", beneficOwnerEstablishDateDay='" + beneficOwnerEstablishDateDay + '\'' +
				", beneficOwnerFlg2=" + beneficOwnerFlg2 +
				", beneficOwnerFullname2='" + beneficOwnerFullname2 + '\'' +
				", beneficOwnerFullnameKana2='" + beneficOwnerFullnameKana2 + '\'' +
				", beneficOwnerFirstnameKana2='" + beneficOwnerFirstnameKana2 + '\'' +
				", beneficOwnerLastnameKana2='" + beneficOwnerLastnameKana2 + '\'' +
				", beneficOwnerFirstname2='" + beneficOwnerFirstname2 + '\'' +
				", beneficOwnerLastname2='" + beneficOwnerLastname2 + '\'' +
				", beneficOwnerEstablishDate2='" + beneficOwnerEstablishDate2 + '\'' +
				", beneficOwnerZipcode2='" + beneficOwnerZipcode2 + '\'' +
				", beneficOwnerPrefecture2='" + beneficOwnerPrefecture2 + '\'' +
				", beneficOwnerCity2='" + beneficOwnerCity2 + '\'' +
				", beneficOwnerSection2='" + beneficOwnerSection2 + '\'' +
				", beneficOwnerBuildingName2='" + beneficOwnerBuildingName2 + '\'' +
				", beneficOwnerTel2='" + beneficOwnerTel2 + '\'' +
				", beneficOwnerEstablishDateYear2='" + beneficOwnerEstablishDateYear2 + '\'' +
				", beneficOwnerEstablishDateMonth2='" + beneficOwnerEstablishDateMonth2 + '\'' +
				", beneficOwnerEstablishDateDay2='" + beneficOwnerEstablishDateDay2 + '\'' +
				", beneficOwnerFlg3=" + beneficOwnerFlg3 +
				", beneficOwnerFullname3='" + beneficOwnerFullname3 + '\'' +
				", beneficOwnerFullnameKana3='" + beneficOwnerFullnameKana3 + '\'' +
				", beneficOwnerFirstnameKana3='" + beneficOwnerFirstnameKana3 + '\'' +
				", beneficOwnerLastnameKana3='" + beneficOwnerLastnameKana3 + '\'' +
				", beneficOwnerFirstname3='" + beneficOwnerFirstname3 + '\'' +
				", beneficOwnerLastname3='" + beneficOwnerLastname3 + '\'' +
				", beneficOwnerEstablishDate3='" + beneficOwnerEstablishDate3 + '\'' +
				", beneficOwnerZipcode3='" + beneficOwnerZipcode3 + '\'' +
				", beneficOwnerPrefecture3='" + beneficOwnerPrefecture3 + '\'' +
				", beneficOwnerCity3='" + beneficOwnerCity3 + '\'' +
				", beneficOwnerSection3='" + beneficOwnerSection3 + '\'' +
				", beneficOwnerBuildingName3='" + beneficOwnerBuildingName3 + '\'' +
				", beneficOwnerTel3='" + beneficOwnerTel3 + '\'' +
				", beneficOwnerEstablishDateYear3='" + beneficOwnerEstablishDateYear3 + '\'' +
				", beneficOwnerEstablishDateMonth3='" + beneficOwnerEstablishDateMonth3 + '\'' +
				", beneficOwnerEstablishDateDay3='" + beneficOwnerEstablishDateDay3 + '\'' +
				", corpPicZipcode='" + corpPicZipcode + '\'' +
				", corpPicPrefecture='" + corpPicPrefecture + '\'' +
				", corpPicCity='" + corpPicCity + '\'' +
				", corpPicSection='" + corpPicSection + '\'' +
				", corpPicBuildingName='" + corpPicBuildingName + '\'' +
				", corpPicAddress='" + corpPicAddress + '\'' +
				", corpRepFullname='" + corpRepFullname + '\'' +
				", corpRepFullnameKana='" + corpRepFullnameKana + '\'' +
				", changeCustomerName=" + changeCustomerName +
				", changeAddress=" + changeAddress +
				", changeCorpName=" + changeCorpName +
				", changeCorpAddress=" + changeCorpAddress +
				", changeCorpOwnerName=" + changeCorpOwnerName +
				", changeCorpRefName=" + changeCorpRefName +
				", changeCorpRefAddress=" + changeCorpRefAddress +
				", changePurePose=" + changePurePose +
				", changeCorpRepName=" + changeCorpRepName +
				", changePass=" + changePass +
				", changeMailMain=" + changeMailMain +
				", virtualBankAccNo='" + virtualBankAccNo + '\'' +
				", virtualBankName='" + virtualBankName + '\'' +
				", virtualBranchName='" + virtualBranchName + '\'' +
				", listPurposes=" + listPurposes +
				", virtualAccType='" + virtualAccType + '\'' +
				", virtualAccName='" + virtualAccName + '\'' +
				", virtualAccNameKana='" + virtualAccNameKana + '\'' +
				", boPurposeShortTermFlg=" + boPurposeShortTermFlg +
				", boPurposeDispAssetMngFlg=" + boPurposeDispAssetMngFlg +
				", boPurposeHedgeFlg=" + boPurposeHedgeFlg +
				", boPurposeHedgeType=" + boPurposeHedgeType +
				", boPurposeHedgeAmount=" + boPurposeHedgeAmount +
				", boMaxLossAmount=" + boMaxLossAmount +
				", hasDocumentUpload=" + hasDocumentUpload +
				", enableMt4Fx=" + enableMt4Fx +
				", needReloadCache=" + needReloadCache +
				", eaAccount=" + eaAccount +
				", oldLoginId='" + oldLoginId + '\'' +
				'}';
	}
}
