package phn.nts.trader.fe.business;

import phn.com.nts.ams.web.condition.InviteCustomerSearchCondition;
import phn.com.nts.ams.web.condition.InviteKickbackHistoryCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.domain.InviteCustomerInfo;
import phn.com.nts.db.domain.InviteKickbackHistoryInfo;
import phn.nts.trader.fe.domain.IBInfo;

import java.util.List;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/11/13 1:29 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public interface IIBManager extends phn.nts.ams.fe.business.IIBManager {
    List<InviteCustomerInfo> getInviteCustomerDetails(InviteCustomerSearchCondition condition, PagingInfo pagingInfo);

    List<InviteKickbackHistoryInfo> getInviteKickbackHistoryDetails(InviteKickbackHistoryCondition condition, PagingInfo pagingInfo);

    IBInfo getIbMgmtInfo(String customerId);
}
