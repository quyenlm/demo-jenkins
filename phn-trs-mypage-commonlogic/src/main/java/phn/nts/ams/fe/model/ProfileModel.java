package phn.nts.ams.fe.model;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.ReportHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsSysBank;
import phn.com.nts.db.entity.AmsSysBankBranch;
import phn.com.nts.db.entity.AmsSysZipcode;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BrokerSettingInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustReportHistoryInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerScInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;

public class ProfileModel extends BaseSocialModel {
	private static final long serialVersionUID = 1L;
	CustomerEwalletInfo customerEwalletInfo;
	CustomerEwalletInfo oldCustomerEwalletInfo;
	
	BankTransferInfo newBankTransferInfo;
	BankTransferInfo oldBankTransferInfo;
	
	CreditCardInfo newCreditCardInfo;
	CreditCardInfo oldCreditCardInfo;
	
	List<CustomerEwalletInfo> listNeteller;
	List<CustomerEwalletInfo> listPayza;
	List<CreditCardInfo> listCreditCardInfo;
	List<BankTransferInfo> listBankTransferInfo;
	List<CustomerEwalletInfo> listLiberty;
	Integer paymentMethod;
	String paymentMethodName;
	CustomerInfo customerInfo;
	String ewalletaccount;
	Integer netellerType;
	Integer libertyType;
	Integer payzaType;
	Integer creditType;
	Integer bankType;
	Integer customerCcId;
	String cardNumber;
	String ewalletEmail;
	String bank;
	String accNo;
	String countryName;

	private String errorMsgSizeLimit;
	
	//[NTS1.0-anhndn]Jan 19, 2013A - Start 
	private List<File> listCreditFileUpload;
	private List<String> listCreditFileUploadFileName;
	//[NTS1.0-anhndn]Jan 19, 2013A - End

	//[NTS1.0-anhndn]Jan 22, 2013A - Start 
	private List<File> listLibertyFileUpload;
	private List<String> listLibertyFileUploadFileName;
	//[NTS1.0-anhndn]Jan 22, 2013A - End
	
	private LinkedHashMap<String, String> listCountry;
	
	private Map<String, String> mapPaymentMethod;
	private Map<String, String> mapCardType;
	private List<String> listDay;	
	private Map<String,String> listMonth;
	private List<String> listYear;
	private Map<String, String> mapLeverage;
	private Map<String, String> mapLanguage;
	private Map<String, String> mapGender;
	//count
	private int openCount = 0;
	private int closeCount = 0;
	//count
	private int isOpenPassword = 0;
	
	//[NTS1.0-Quan.Le.Minh]Jan 19, 2013A - Start 
	private List<File> uploadPhones;
	private List<File> uploadPassports;
	private List<File> uploadAddresses;
	private List<File> uploadSignatures;
	
	private List<String> uploadPhonesFileName;
	private List<String> uploadPassportsFileName;
	private List<String> uploadAddressesFileName;
	private List<String> uploadSignaturesFileName;
	//[NTS1.0-Quan.Le.Minh]Jan 19, 2013A - End
	
	private Map<String,String> mapServiceType;
	private Map<String,String> mapAccountKind;
	private Map<String,String> mapBrokerCd;
	private Map<Integer,String> mapServerAddress;
	private Map<String,String> mapMt4Account;
//	private Map<String,Integer> mapNumberOfEnabledBrokerCd;
	private CustomerScInfo customerScInfo;
	private File uploadedAvatar;
	private String uploadedAvatarFileName;
	private List<BrokerSettingInfo> listTrsServiceInfo;
	private List<BrokerSettingInfo> listOtherBrokerServiceInfo;
	private Integer writeMyBoardFlg;
	private BrokerSettingInfo newBrokerSettingInfo;
	private String imageName;
	private Integer enableFlg;
	private String brokerCd;
	private Integer scCustServiceId;
	private String expiredDate;
	private String password;
	private String jsonResult;	
	private String brokerName;
	private String accountId;

	private CustomerServicesInfo socialMt4Info;
	private CustomerServicesInfo normalMt4Info;
	private String boAccountId;
	private String mt4Account;
	private String mt4NewPass;
	private String mt4ConfirmPass;
	private String mt4InvestorNewPass;
	private String mt4InvestorConfirmPass;
	private String isChangeMt4Pass;
	
	private String successMsg;
	private String errorMsg;
	
	//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
	private Map<String, String> mapFinancilAssets;
	private Map<String, String> mapPrefecture;
	private AmsSysZipcode amsSysZipcode;
	private String zipCode;
	private Map<String, String> mapAccountType;
	private PagingInfo pagingInfo;
	private String searchBankName;
	private List<AmsSysBank> listBank;
	private List<AmsSysBankBranch> listBankBranch;
	private BankTransferInfo bankCondition;
	
	private ReportHistorySearchCondition custReportSearchCondition;
	SearchResult<CustReportHistoryInfo> listCustReportHistoryInfo;
	Map<String, String> mapReportType;
	private InputStream downloadFile;
	private String downloadFileName;
	//[TRS-BO tan.pham.duy]
	private Map<String,String> mapType;
	private Map<Integer,String> mapRadio;
	
	private Map<String, String> days;
	private Map<String, String> months;
	private Map<String, String> years;
	//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
	
	private Integer x;
	private Integer y;
	private Integer w;
	private Integer h;
	private boolean allowCrop;
	private String isCompleted;

	
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public Integer getH() {
		return h;
	}

	public void setH(Integer h) {
		this.h = h;
	}

	public boolean isAllowCrop() {
		return allowCrop;
	}

	public void setAllowCrop(boolean allowCrop) {
		this.allowCrop = allowCrop;
	}

	public String getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(String isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<String> getUploadPhonesFileName() {
		return uploadPhonesFileName;
	}

	public Map<String, String> getDays() {
		return days;
	}

	public void setDays(Map<String, String> days) {
		this.days = days;
	}

	public Map<String, String> getMonths() {
		return months;
	}

	public void setMonths(Map<String, String> months) {
		this.months = months;
	}

	public Map<String, String> getYears() {
		return years;
	}

	public void setYears(Map<String, String> years) {
		this.years = years;
	}

	public InputStream getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(InputStream downloadFile) {
		this.downloadFile = downloadFile;
	}

	public String getDownloadFileName() {
		return downloadFileName;
	}

	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}

	public Map<String, String> getMapReportType() {
		return mapReportType;
	}

	public void setMapReportType(Map<String, String> mapReportType) {
		this.mapReportType = mapReportType;
	}

	public SearchResult<CustReportHistoryInfo> getListCustReportHistoryInfo() {
		return listCustReportHistoryInfo;
	}

	public void setListCustReportHistoryInfo(SearchResult<CustReportHistoryInfo> listCustReportHistoryInfo) {
		this.listCustReportHistoryInfo = listCustReportHistoryInfo;
	}

	public ReportHistorySearchCondition getCustReportSearchCondition() {
		return custReportSearchCondition;
	}

	public void setCustReportSearchCondition(ReportHistorySearchCondition custReportSearchCondition) {
		this.custReportSearchCondition = custReportSearchCondition;
	}

	public List<AmsSysBankBranch> getListBankBranch() {
		return listBankBranch;
	}

	public void setListBankBranch(List<AmsSysBankBranch> listBankBranch) {
		this.listBankBranch = listBankBranch;
	}

	public BankTransferInfo getBankCondition() {
		return bankCondition;
	}

	public void setBankCondition(BankTransferInfo bankCondition) {
		this.bankCondition = bankCondition;
	}

	public List<AmsSysBank> getListBank() {
		return listBank;
	}

	public void setListBank(List<AmsSysBank> listBank) {
		this.listBank = listBank;
	}

	public String getSearchBankName() {
		return searchBankName;
	}

	public void setSearchBankName(String searchBankName) {
		this.searchBankName = searchBankName;
	}

	public PagingInfo getPagingInfo() {
		return pagingInfo;
	}

	public void setPagingInfo(PagingInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	public Map<String, String> getMapAccountType() {
		return mapAccountType;
	}

	public void setMapAccountType(Map<String, String> mapAccountType) {
		this.mapAccountType = mapAccountType;
	}

	public AmsSysZipcode getAmsSysZipcode() {
		return amsSysZipcode;
	}

	public void setAmsSysZipcode(AmsSysZipcode amsSysZipcode) {
		this.amsSysZipcode = amsSysZipcode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Map<String, String> getMapPrefecture() {
		return mapPrefecture;
	}

	public void setMapPrefecture(Map<String, String> mapPrefecture) {
		this.mapPrefecture = mapPrefecture;
	}

	public Map<String, String> getMapFinancilAssets() {
		return mapFinancilAssets;
	}

	public void setMapFinancilAssets(Map<String, String> mapFinancilAssets) {
		this.mapFinancilAssets = mapFinancilAssets;
	}

	public void setUploadPhonesFileName(List<String> uploadPhonesFileName) {
		this.uploadPhonesFileName = uploadPhonesFileName;
	}

	public List<String> getUploadPassportsFileName() {
		return uploadPassportsFileName;
	}

	public void setUploadPassportsFileName(List<String> uploadPassportsFileName) {
		this.uploadPassportsFileName = uploadPassportsFileName;
	}

	public List<String> getUploadAddressesFileName() {
		return uploadAddressesFileName;
	}

	public void setUploadAddressesFileName(List<String> uploadAddressesFileName) {
		this.uploadAddressesFileName = uploadAddressesFileName;
	}

	public List<String> getUploadSignaturesFileName() {
		return uploadSignaturesFileName;
	}

	public void setUploadSignaturesFileName(List<String> uploadSignaturesFileName) {
		this.uploadSignaturesFileName = uploadSignaturesFileName;
	}

	public BankTransferInfo getNewBankTransferInfo() {
		return newBankTransferInfo;
	}

	public void setNewBankTransferInfo(BankTransferInfo newBankTransferInfo) {
		this.newBankTransferInfo = newBankTransferInfo;
	}

	public BankTransferInfo getOldBankTransferInfo() {
		return oldBankTransferInfo;
	}

	public void setOldBankTransferInfo(BankTransferInfo oldBankTransferInfo) {
		this.oldBankTransferInfo = oldBankTransferInfo;
	}


	public CreditCardInfo getNewCreditCardInfo() {
		return newCreditCardInfo;
	}

	public void setNewCreditCardInfo(CreditCardInfo newCreditCardInfo) {
		this.newCreditCardInfo = newCreditCardInfo;
	}

	public CreditCardInfo getOldCreditCardInfo() {
		return oldCreditCardInfo;
	}

	public void setOldCreditCardInfo(CreditCardInfo oldCreditCardInfo) {
		this.oldCreditCardInfo = oldCreditCardInfo;
	}

	public Integer getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	/**
	 * @return the listCountry
	 */
	public LinkedHashMap<String, String> getListCountry() {
		return listCountry;
	}

	/**
	 * @param listCountry the listCountry to set
	 */
	public void setListCountry(LinkedHashMap<String, String> listCountry) {
		this.listCountry = listCountry;
	}


	public Map<String, String> getMapPaymentMethod() {
		return mapPaymentMethod;
	}

	public void setMapPaymentMethod(Map<String, String> mapPaymentMethod) {
		this.mapPaymentMethod = mapPaymentMethod;
	}

	public CustomerEwalletInfo getCustomerEwalletInfo() {
		return customerEwalletInfo;
	}

	public void setCustomerEwalletInfo(CustomerEwalletInfo customerEwalletInfo) {
		this.customerEwalletInfo = customerEwalletInfo;
	}

	public List<CustomerEwalletInfo> getListNeteller() {
		return listNeteller;
	}

	public void setListNeteller(List<CustomerEwalletInfo> listNeteller) {
		this.listNeteller = listNeteller;
	}

	public List<CustomerEwalletInfo> getListPayza() {
		return listPayza;
	}

	public void setListPayza(List<CustomerEwalletInfo> listPayza) {
		this.listPayza = listPayza;
	}

	public Map<String, String> getMapCardType() {
		return mapCardType;
	}

	public void setMapCardType(Map<String, String> mapCardType) {
		this.mapCardType = mapCardType;
	}

	public List<CreditCardInfo> getListCreditCardInfo() {
		return listCreditCardInfo;
	}

	public void setListCreditCardInfo(List<CreditCardInfo> listCreditCardInfo) {
		this.listCreditCardInfo = listCreditCardInfo;
	}

	public List<BankTransferInfo> getListBankTransferInfo() {
		return listBankTransferInfo;
	}

	public void setListBankTransferInfo(List<BankTransferInfo> listBankTransferInfo) {
		this.listBankTransferInfo = listBankTransferInfo;
	}


	public String getEwalletaccount() {
		return ewalletaccount;
	}

	public void setEwalletaccount(String ewalletaccount) {
		this.ewalletaccount = ewalletaccount;
	}


	public CustomerEwalletInfo getOldCustomerEwalletInfo() {
		return oldCustomerEwalletInfo;
	}

	public void setOldCustomerEwalletInfo(CustomerEwalletInfo oldCustomerEwalletInfo) {
		this.oldCustomerEwalletInfo = oldCustomerEwalletInfo;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getEwalletEmail() {
		return ewalletEmail;
	}

	public void setEwalletEmail(String ewalletEmail) {
		this.ewalletEmail = ewalletEmail;
	}


	
	public String getBank() {
		return bank;
	}

	public void setBank(String bankName) {
		this.bank = bankName;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public Integer getBankType() {
		return bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	public Integer getCreditType() {
		return creditType;
	}

	public void setCreditType(Integer creditType) {
		this.creditType = creditType;
	}

	public Integer getPayzaType() {
		return payzaType;
	}

	public void setPayzaType(Integer payzaType) {
		this.payzaType = payzaType;
	}

	public Integer getNetellerType() {
		return netellerType;
	}

	public void setNetellerType(Integer netellerType) {
		this.netellerType = netellerType;
	}

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public Integer getCustomerCcId() {
		return customerCcId;
	}

	public void setCustomerCcId(Integer customerCcId) {
		this.customerCcId = customerCcId;
	}

	public int getOpenCount() {
		return openCount;
	}

	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}

	public int getCloseCount() {
		return closeCount;
	}

	public void setCloseCount(int closeCount) {
		this.closeCount = closeCount;
	}

	public int getIsOpenPassword() {
		return isOpenPassword;
	}

	public void setIsOpenPassword(int isOpenPassword) {
		this.isOpenPassword = isOpenPassword;
	}

	
	/**
	 * @return the listDay
	 */
	public List<String> getListDay() {
		return listDay;
	}

	/**
	 * @param listDay the listDay to set
	 */
	public void setListDay(List<String> listDay) {
		this.listDay = listDay;
	}


	/**
	 * @return the listYear
	 */
	public List<String> getListYear() {
		return listYear;
	}

	/**
	 * @param listYear the listYear to set
	 */
	public void setListYear(List<String> listYear) {
		this.listYear = listYear;
	}

	/**
	 * @return the mapLeverage
	 */
	public Map<String, String> getMapLeverage() {
		return mapLeverage;
	}

	/**
	 * @param mapLeverage the mapLeverage to set
	 */
	public void setMapLeverage(Map<String, String> mapLeverage) {
		this.mapLeverage = mapLeverage;
	}

	/**
	 * @return the mapLanguage
	 */
	public Map<String, String> getMapLanguage() {
		return mapLanguage;
	}

	/**
	 * @param mapLanguage the mapLanguage to set
	 */
	public void setMapLanguage(Map<String, String> mapLanguage) {
		this.mapLanguage = mapLanguage;
	}

	public List<CustomerEwalletInfo> getListLiberty() {
		return listLiberty;
	}

	public void setListLiberty(List<CustomerEwalletInfo> listLiberty) {
		this.listLiberty = listLiberty;
	}

	/**
	 * @return the libertyType
	 */
	public Integer getLibertyType() {
		return libertyType;
	}

	/**
	 * @param libertyType the libertyType to set
	 */
	public void setLibertyType(Integer libertyType) {
		this.libertyType = libertyType;
	}

	public Map<String,String> getListMonth() {
		return listMonth;
	}

	public void setListMonth(Map<String,String> listMonth) {
		this.listMonth = listMonth;
	}

	public List<File> getUploadPhones() {
		return uploadPhones;
	}

	public void setUploadPhones(List<File> uploadPhones) {
		this.uploadPhones = uploadPhones;
	}

	public List<File> getUploadPassports() {
		return uploadPassports;
	}

	public void setUploadPassports(List<File> uploadPassports) {
		this.uploadPassports = uploadPassports;
	}

	public List<File> getUploadAddresses() {
		return uploadAddresses;
	}

	public void setUploadAddresses(List<File> uploadAddresses) {
		this.uploadAddresses = uploadAddresses;
	}

	public List<File> getUploadSignatures() {
		return uploadSignatures;
	}

	public void setUploadSignatures(List<File> uploadSignatures) {
		this.uploadSignatures = uploadSignatures;
	}
	
	public List<File> getListCreditFileUpload() {
		return listCreditFileUpload;
	}

	public void setListCreditFileUpload(List<File> listCreditFileUpload) {
		this.listCreditFileUpload = listCreditFileUpload;
	}

	public List<String> getListCreditFileUploadFileName() {
		return listCreditFileUploadFileName;
	}

	public void setListCreditFileUploadFileName(
			List<String> listCreditFileUploadFileName) {
		this.listCreditFileUploadFileName = listCreditFileUploadFileName;
	}

	public List<File> getListLibertyFileUpload() {
		return listLibertyFileUpload;
	}

	public void setListLibertyFileUpload(List<File> listLibertyFileUpload) {
		this.listLibertyFileUpload = listLibertyFileUpload;
	}

	public List<String> getListLibertyFileUploadFileName() {
		return listLibertyFileUploadFileName;
	}

	public void setListLibertyFileUploadFileName(
			List<String> listLibertyFileUploadFileName) {
		this.listLibertyFileUploadFileName = listLibertyFileUploadFileName;
	}

	public String getErrorMsgSizeLimit() {
		return errorMsgSizeLimit;
	}

	public void setErrorMsgSizeLimit(String errorMsgSizeLimit) {
		this.errorMsgSizeLimit = errorMsgSizeLimit;
	}

	public Map<String, String> getMapGender() {
		return mapGender;
	}

	public void setMapGender(Map<String, String> mapGender) {
		this.mapGender = mapGender;
	}

	public CustomerScInfo getCustomerScInfo() {
		return customerScInfo;
	}

	public void setCustomerScInfo(CustomerScInfo customerScInfo) {
		this.customerScInfo = customerScInfo;
	}

	public File getUploadedAvatar() {
		return uploadedAvatar;
	}

	public void setUploadedAvatar(File uploadedAvatar) {
		this.uploadedAvatar = uploadedAvatar;
	}

	public String getUploadedAvatarFileName() {
		return uploadedAvatarFileName;
	}

	public void setUploadedAvatarFileName(String uploadedAvatarFileName) {
		this.uploadedAvatarFileName = uploadedAvatarFileName;
	}

	/**
	 * @return the listOtherBrokerServiceInfo
	 */
	public List<BrokerSettingInfo> getListOtherBrokerServiceInfo() {
		return listOtherBrokerServiceInfo;
	}

	public List<BrokerSettingInfo> getListTrsServiceInfo() {
		return listTrsServiceInfo;
	}

	public void setListTrsServiceInfo(List<BrokerSettingInfo> listTrsServiceInfo) {
		this.listTrsServiceInfo = listTrsServiceInfo;
	}

	/**
	 * @param listOtherBrokerServiceInfo the listOtherBrokerServiceInfo to set
	 */
	public void setListOtherBrokerServiceInfo(
			List<BrokerSettingInfo> listOtherBrokerServiceInfo) {
		this.listOtherBrokerServiceInfo = listOtherBrokerServiceInfo;
	}

	/**
	 * @return the writeMyBoardFlg
	 */
	public Integer getWriteMyBoardFlg() {
		return writeMyBoardFlg;
	}

	/**
	 * @param writeMyBoardFlg the writeMyBoardFlg to set
	 */
	public void setWriteMyBoardFlg(Integer writeMyBoardFlg) {
		this.writeMyBoardFlg = writeMyBoardFlg;
	}

	/**
	 * @return the mapServiceType
	 */
	public Map<String, String> getMapServiceType() {
		return mapServiceType;
	}

	/**
	 * @param mapServiceType the mapServiceType to set
	 */
	public void setMapServiceType(Map<String, String> mapServiceType) {
		this.mapServiceType = mapServiceType;
	}

	/**
	 * @return the newBrokerSettingInfo
	 */
	public BrokerSettingInfo getNewBrokerSettingInfo() {
		return newBrokerSettingInfo;
	}

	/**
	 * @param newBrokerSettingInfo the newBrokerSettingInfo to set
	 */
	public void setNewBrokerSettingInfo(BrokerSettingInfo newBrokerSettingInfo) {
		this.newBrokerSettingInfo = newBrokerSettingInfo;
	}

	/**
	 * @return the mapAccountKind
	 */
	public Map<String, String> getMapAccountKind() {
		return mapAccountKind;
	}

	/**
	 * @param mapAccountKind the mapAccountKind to set
	 */
	public void setMapAccountKind(Map<String, String> mapAccountKind) {
		this.mapAccountKind = mapAccountKind;
	}

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
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
	 * @return the jsonResult
	 */
	public String getJsonResult() {
		return jsonResult;
	}

	/**
	 * @param jsonResult the jsonResult to set
	 */
	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
	}

	/**
	 * @return the scCustServiceId
	 */
	public Integer getScCustServiceId() {
		return scCustServiceId;
	}

	/**
	 * @param scCustServiceId the scCustServiceId to set
	 */
	public void setScCustServiceId(Integer scCustServiceId) {
		this.scCustServiceId = scCustServiceId;
	}

	/**
	 * @return the enableFlg
	 */
	public Integer getEnableFlg() {
		return enableFlg;
	}

	/**
	 * @param enableFlg the enableFlg to set
	 */
	public void setEnableFlg(Integer enableFlg) {
		this.enableFlg = enableFlg;
	}

	/**
	 * @return the expiredDate
	 */
	public String getExpiredDate() {
		return expiredDate;
	}

	/**
	 * @param expiredDate the expiredDate to set
	 */
	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
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
	 * @return the successMsg
	 */
	public String getSuccessMsg() {
		return successMsg;
	}

	/**
	 * @param successMsg the successMsg to set
	 */
	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the mapServerAddress
	 */
	public Map<Integer, String> getMapServerAddress() {
		return mapServerAddress;
	}

	/**
	 * @param mapServerAddress the mapServerAddress to set
	 */
	public void setMapServerAddress(Map<Integer, String> mapServerAddress) {
		this.mapServerAddress = mapServerAddress;
	}

	/**
	 * @return the brokerName
	 */
	public String getBrokerName() {
		return brokerName;
	}

	/**
	 * @param brokerName the brokerName to set
	 */
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
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
	 * @return the mapMt4Account
	 */
	public Map<String, String> getMapMt4Account() {
		return mapMt4Account;
	}

	/**
	 * @param mapMt4Account the mapMt4Account to set
	 */
	public void setMapMt4Account(Map<String, String> mapMt4Account) {
		this.mapMt4Account = mapMt4Account;
	}

	/**
	 * @return the socialMt4Info
	 */
	public CustomerServicesInfo getSocialMt4Info() {
		return socialMt4Info;
	}

	/**
	 * @param socialMt4Info the socialMt4Info to set
	 */
	public void setSocialMt4Info(CustomerServicesInfo socialMt4Info) {
		this.socialMt4Info = socialMt4Info;
	}

	/**
	 * @return the normalMt4Info
	 */
	public CustomerServicesInfo getNormalMt4Info() {
		return normalMt4Info;
	}

	/**
	 * @param normalMt4Info the normalMt4Info to set
	 */
	public void setNormalMt4Info(CustomerServicesInfo normalMt4Info) {
		this.normalMt4Info = normalMt4Info;
	}

	/**
	 * @return the boAccountId
	 */
	public String getBoAccountId() {
		return boAccountId;
	}

	/**
	 * @param boAccountId the boAccountId to set
	 */
	public void setBoAccountId(String boAccountId) {
		this.boAccountId = boAccountId;
	}

	/**
	 * @return the mt4NewPass
	 */
	public String getMt4NewPass() {
		return mt4NewPass;
	}

	/**
	 * @param mt4NewPass the mt4NewPass to set
	 */
	public void setMt4NewPass(String mt4NewPass) {
		this.mt4NewPass = mt4NewPass;
	}

	/**
	 * @return the mt4ConfirmPass
	 */
	public String getMt4ConfirmPass() {
		return mt4ConfirmPass;
	}

	/**
	 * @param mt4ConfirmPass the mt4ConfirmPass to set
	 */
	public void setMt4ConfirmPass(String mt4ConfirmPass) {
		this.mt4ConfirmPass = mt4ConfirmPass;
	}

	/**
	 * @return the mt4InvestorNewPass
	 */
	public String getMt4InvestorNewPass() {
		return mt4InvestorNewPass;
	}

	/**
	 * @param mt4InvestorNewPass the mt4InvestorNewPass to set
	 */
	public void setMt4InvestorNewPass(String mt4InvestorNewPass) {
		this.mt4InvestorNewPass = mt4InvestorNewPass;
	}

	/**
	 * @return the mt4InvestorConfirmPass
	 */
	public String getMt4InvestorConfirmPass() {
		return mt4InvestorConfirmPass;
	}

	/**
	 * @param mt4InvestorConfirmPass the mt4InvestorConfirmPass to set
	 */
	public void setMt4InvestorConfirmPass(String mt4InvestorConfirmPass) {
		this.mt4InvestorConfirmPass = mt4InvestorConfirmPass;
	}

	/**
	 * @return the isChangeMt4Pass
	 */
	public String getIsChangeMt4Pass() {
		return isChangeMt4Pass;
	}

	/**
	 * @param isChangeMt4Pass the isChangeMt4Pass to set
	 */
	public void setIsChangeMt4Pass(String isChangeMt4Pass) {
		this.isChangeMt4Pass = isChangeMt4Pass;
	}

	/**
	 * @return the mt4Account
	 */
	public String getMt4Account() {
		return mt4Account;
	}

	/**
	 * @param mt4Account the mt4Account to set
	 */
	public void setMt4Account(String mt4Account) {
		this.mt4Account = mt4Account;
	}

	/**
	 * @return the mapBrokerCd
	 */
	public Map<String, String> getMapBrokerCd() {
		return mapBrokerCd;
	}

	/**
	 * @param mapBrokerCd the mapBrokerCd to set
	 */
	public void setMapBrokerCd(Map<String, String> mapBrokerCd) {
		this.mapBrokerCd = mapBrokerCd;
	}

	/**
	 * @return the serialversionuid
	 */
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Map<String, String> getMapType() {
		return mapType;
	}

	public void setMapType(Map<String, String> mapType) {
		this.mapType = mapType;
	}

	public Map<Integer, String> getMapRadio() {
		return mapRadio;
	}

	public void setMapRadio(Map<Integer, String> mapRadio) {
		this.mapRadio = mapRadio;
	}
	
}
