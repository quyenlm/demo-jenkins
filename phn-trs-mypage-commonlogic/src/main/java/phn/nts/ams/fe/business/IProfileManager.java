package phn.nts.ams.fe.business;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.ReportHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsSysBank;
import phn.com.nts.db.entity.AmsSysBankBranch;
import phn.com.nts.db.entity.AmsSysZipcode;
import phn.com.nts.db.entity.BoCustomer;
import phn.com.nts.util.file.FileUploadInfo;
import phn.com.trs.util.enums.Result;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BrokerSettingInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustReportHistoryInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerScInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.DocumentInfo;
import cn.nextop.social.api.admin.proxy.glossary.CloseAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.ModifyAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.OpenAccountResult;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerBankInfo;


public interface IProfileManager {

	public String addEwallet(CustomerEwalletInfo customerEwalletInfo, Integer eWalletType, List<FileUploadInfo> listFileUploadInfo, String wlCode, Integer subGroupId, String publicKey);
	public String updateEwallet(String customerId, CustomerEwalletInfo newCustomerEwalletInfo,CustomerEwalletInfo customerEwalletInfo,Integer ewalletType, List<FileUploadInfo> listFileUploadInfo, String wlCode, Integer subGroupId, String publicKey);
	public String deleteEwallet(String customerId,String ewalletAccNo, Integer ewalletType);
	
	public String addBankTransfer(AmsCustomerBankInfo.Builder bankTransferInfo);
	public String updateBankTransfer(BankTransferInfo newBankTransferInfo, BankTransferInfo oldBankTransferInfo);
	public String deleteBankTransfer(String customerBankId);
	
	public String addCreditCard(CreditCardInfo creditCardInfo, List<FileUploadInfo> listFileUploadInfo, String wlCode, Integer subGroupId, String publicKey);
	public String updateCreditCard(CreditCardInfo newCreditCardInfo, List<FileUploadInfo> listFileUploadInfo, String wlCode, Integer subGroupId, String publicKey);
	public String deleteCreditcard(Integer creditId);
	
//	/public boolean checkExistedMailPayza(String email);
	public List<CustomerEwalletInfo> getEwalletList(String customerId, Integer paymentType, String publicKey);
	public CustomerInfo getCustomerInfoByNtdCustomerId(String ntdCustomerId);
	public CustomerInfo getCustomerInfo(String customerId);
	public Boolean updateProfileInfo(CustomerInfo customerInfo);
	public LinkedHashMap<String, String> getListCountry();
	public Integer updateProfile(CustomerInfo customerInfo);
	public List<CreditCardInfo> getCreditCardList(String customerId, String publicKey);
	public List<BankTransferInfo> getBankInfo(String customerId);
	public CustomerEwalletInfo getEwalletInfo(String customerId, String ewalletID, Integer ewalletType, String publicKey);
	//public CreditCardInfo getCreditCard(String customerId, Integer cardType, String cardNo);
	public BankTransferInfo getBankInfo(String customerId, String bankName, String accNo);
	public CreditCardInfo getCreditCardbyID(Integer customerCCID, String publicKey);
	public String getCountryName(Integer countryId) ;
	public CustomerServicesInfo getCustomerService(String customerId,Integer serviceType);
	public List<CustomerServicesInfo> getCustomerService (String customerId);
	
	public String getAmsGroup(Integer groupId);
	public String getIbLink(String customerId);
	
	public boolean updateCustomerServiceStatus(String customerId);
	public Integer uploadFiles(List<FileUploadInfo> filesInfo, String customerId, String wlCode, Integer subGroupId);
	public boolean updateCustomerDocStatus(String customerId, Integer docType);
	public List<DocumentInfo> getEwalletDocUrl(Integer ewalletId);
	public List<DocumentInfo> getCcDocUrl(Integer ccId);
	public CustomerInfo getDocUrls(String customerId);
	public CustomerScInfo getCustomerScInfo(String customerId);
	
	public Integer uploadAvatar(FileUploadInfo fileUploadInfo, String wlCode);
	public boolean updateBasicInfoOfScCustomer(CustomerInfo customerInfo, CustomerScInfo customerScInfo);
    public List<BrokerSettingInfo> getBrokerSettingInfo(String customerId);
    public boolean updatePrivacySetting(Integer writeMyBoardFlg, String customerId);
    public Map<String,String> getMapBrokerCd();
    public Integer getNumberOfEnabledAccount(String customerId);
    public Integer getNumberOfEnabledOtherBroker(String customerId);
    public boolean updateEnableFlgOfBroker(Integer scCustServiceId,Integer enableFlg);
    public boolean updateBrokerInfo(Integer scCustServiceId, String expiredDate, String password);
    public Map<Integer,String> getServerAddressByBrokerCd(final String brokerCd);
    public boolean checkExistAccountIdAndBrokerId(String customerId, BrokerSettingInfo brokerSettingInfo);
    public boolean insertNewAccountBroker(String customerId, BrokerSettingInfo brokerSettingInfo);
    public boolean deleteBroker(Integer scCustServiceId);
    
    public String getCurrentBusinessDay();
    public String getCountryCodeFromCountryId(Integer countryId);
    public Integer getNumberOfScOrder(String customerServiceId);
    public void sendmailChangeMt4Pass(String customerId,String customerServiceId, String newMasterPassword,String newInvestorPassword);
	public Map<String, String> getMapPrefecture();
	public AmsSysZipcode getAddressByZipCode(String zipCode);
	public void sendmailChangeInfoToCS(CustomerInfo customerInfo);
	public void sendmailChangeInfoToCustomer(CustomerInfo customerInfo);
	public String getBankNameByVirtualBank(String virtualBankAccNo);
	public void syncCustomerInfoToSaleFace(CustomerInfo customerInfo);
	public Result syncTradingInfoToSalesForce(String customerId);
	public boolean checkExistedBankAccNumber(BankTransferInfo bankTransferInfo);
	public SearchResult<AmsSysBank> findListBank(String bankName, String bankNameFullSize, String bankNameHalfSize, PagingInfo paging);
	public SearchResult<AmsSysBankBranch> findListBankBranch(String bankCode, String branchName, String branchNameFullSize, String branchNameHalfSize, PagingInfo paging);
	public SearchResult<CustReportHistoryInfo> searchCustReportHistory(ReportHistorySearchCondition condition, PagingInfo paging, String privateKey, String publicKey) throws UnsupportedEncodingException ;
	public Integer uploadCropAvatarSubmit(FileUploadInfo fileUploadInfo, String wlCode);
	public Integer uploadCropAvatar(FileUploadInfo fileUploadInfo, String wlCode);
	public boolean mailIndivExisted(String customerId, String mailMain);
	public boolean mailCorpExisted(String customerId, String mailMain);
	public boolean additionalMailExisted(String customerId, String mailMain);
	public boolean mailExisted(String customerId, String mail);
    public void ensureAvatarCreated(String customerId, String defaultImagePath);
    public void updateWhiteLabelReport(String reportId);
    
    public AmsCustomer getAmsCustomer(String customerId);
    public AmsCustomer getAmsCustomerByLoginId(String loginId);
    public String getCustomerIdByCustomerServiceId(String customerServiceId);
    
    public BoCustomer getBoCustomer(String customerId);
    public CustomerInfo getCustomerInfoByLoginId(String loginId);
    public void sendmailNotifyDocToCS(CustomerInfo customerInfo);
   
    //Social system
	public OpenAccountResult openSocialAccount(AmsCustomerRegisterSocialRequest request);
	public ModifyAccountResult modifySocialAccount(AmsCustomerModifySocialRequest request);
	public CloseAccountResult closeSocialAccount(String customerId);
	public void sendmailSocial(CustomerInfo customerInfo, String mailCode);
}
