package phn.nts.ams.fe.business;

import java.util.List;

import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsDepositTransactionInfo;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.nts.ams.fe.domain.BjpInfo;
import phn.nts.ams.fe.domain.BoRegisInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.MessageInfo;
import phn.nts.ams.fe.model.WebUserDetails;
import phn.nts.ams.fe.security.FrontUserDetails;


public interface IAccountManager {
	public FrontUserOnline getFrontUserOnline(String loginId);
	public FrontUserDetails getUserDetail(String loginId);
	public WebUserDetails getWebUserDetails(String loginId);	
	public CustomerServicesInfo getCustomerServiceInfo(String customerServiceId);
	public CustomerInfo getCustomerInfo(String customerId, String clientCustomerId);
	public CustomerServicesInfo getCustomerServiceInfo(String customerId, Integer serviceType);
	public CustomerInfo getCustomerInfoByEmail(String email);
	public CustomerInfo getCustomerInfo(String customerId);	
	public void updateAmsCustomerServices(CustomerServicesInfo customerServiceInfo) throws Exception ;
	public List<CustomerServicesInfo> getListCustomerServiceInfo(String customerId);
	public Integer getSubGroupId(String customerId, int serviceType);
	Integer getSubGroupIdFX(String customerId);
	public List<MessageInfo> getMessageList(String customerId);
	public List<MessageInfo> getMessageList(String customerId, PagingInfo pagingInfo);
	public Boolean registerCustomerService(CustomerInfo customerInfo, Integer serviceType);
	public Boolean resetPassword(String email);
	public Boolean resetPasswordExtend(String email);
	public Boolean verifyPassword(String mailActiveCode, String email);
	public Boolean isEmailExisting(String email);
	public Boolean checkUserExisting(String email,String birthday, Integer corpType);
	public List<CustomerServicesInfo> getListCustomerServiceByServiceTypes(String customerId, List<Integer> serviceTypes);

    boolean isExistMail(String email);
    boolean checkVerifyUser(String email, String birthday,String mailActiveCode);
    boolean updatePassword(String loginId,String  newPass,String activeCode);
    boolean isExistPhone(String phoneNumber);
    String saveBjpDeposit(AmsDeposit amsDeposit,AmsDepositRef amsDepositRef, AmsDepositTransactionInfo amsDepositTransactionInfo);
    public BjpInfo updateBjpInfo(BjpInfo bjpInfo);
    String getBjpCertificationKey(String wlcode);
	public List<Integer> getListServiceTypeStatusCancel(String customerId);
	public boolean updateCustomer(String customerId,BoRegisInfo info);
	public AmsCustomer getAmsCustomer(String customerId);
	public AmsCustomer getAmsCustomerByCustomerService(String customerServiceId);
	public String getCustomerIdByCustomerService(String customerServiceId);
	public boolean syncCustomerInfoToBo(String customerId);
	public boolean checkEaAccount(String customerId);
}
