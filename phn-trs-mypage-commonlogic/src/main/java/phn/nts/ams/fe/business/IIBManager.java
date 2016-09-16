package phn.nts.ams.fe.business;

import java.util.List;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsIbKickback;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.IbClientCustomer;
import phn.nts.ams.fe.domain.IbInfo;
import phn.nts.ams.fe.domain.WhiteLabelConfigInfo;


public interface IIBManager {
	public WhiteLabelConfigInfo getWhiteLabelConfigInfo(String key, String wlCode);
	public List<IbClientCustomer> getListIbCustomer (String customerId,String currentUserId, String customerName,PagingInfo info);
	public IbInfo getIbInfo(String customerId);
	public Long getIBAccountTotal(String customerId);
	public Double getKickbackTotal(String customerId);
	public List<AmsIbKickback> searchIbKickBackHistory (String customerId, String orderCustomerId, String orderId, String orderSymbolCd, String fromDate, String toDate, PagingInfo pagingInfo);
	public Integer registerIBCustomer(CustomerInfo customerInfo,String wlCode,String currentUserId,String rootPath);
	public Integer registerMT4Account(CustomerInfo customerInfo,String loginId,String wlCode, String agentServiceId);
	public void sendMail(String mt4Id,String mt4Password,String email,String subject,String template);
	public Double getTotalAmountTransferMoney(Integer transferFrom, Integer status);
	public List<AmsCustomerService> getListAmsCustomerService(String customerId);
	public List<String> getListClientCustomerInfo(String customerId);
	public List<String> getListSymbol();
	public List<String> getListWhiteLabelConfigInfo(String configType, String wlCode);
	public boolean isIbClient(String customerId, String clientCustomerId);
}
