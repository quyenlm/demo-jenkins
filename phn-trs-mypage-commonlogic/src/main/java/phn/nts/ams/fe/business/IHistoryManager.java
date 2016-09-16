package phn.nts.ams.fe.business;

import java.util.List;

import phn.com.nts.ams.web.condition.AmsFeHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsViewFeSearchHistory;
import phn.com.nts.db.entity.AmsWithdrawal;


public interface IHistoryManager {
	public List<AmsFeHistorySearchCondition> getListAmsFeSearchHistory(AmsFeHistorySearchCondition condition, PagingInfo pagingInfo, String customerId);
	public AmsDeposit getDeposit(String depositId);
	public void updateAmsDepositStatus(AmsDeposit amsDeposit);
	public void updateAmsWithdrawalStatus(AmsWithdrawal amsWithdrawal);
	public AmsWithdrawal getAmsWithdrawal(String withdrawalId);
	public boolean isIBClientUser(String customerId, String ibCustomerId);

    void updateBackNetDepositCc(String customerId, Double withdrawalAmount);
}
