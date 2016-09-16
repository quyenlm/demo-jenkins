package phn.nts.trader.fe.business.impl;

import phn.com.nts.ams.web.condition.InviteCustomerSearchCondition;
import phn.com.nts.ams.web.condition.InviteKickbackHistoryCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.impl.AmsIbKickbackDAO;
import phn.com.nts.db.domain.InviteCustomerInfo;
import phn.com.nts.db.domain.InviteKickbackHistoryInfo;
import phn.com.nts.db.entity.AmsIb;
import phn.com.nts.db.entity.AmsIbKickback;
import phn.com.nts.util.common.IConstants;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.trader.fe.business.IIBManager;
import phn.nts.trader.fe.domain.IBInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/11/13 1:30 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class IBManagerImpl extends phn.nts.ams.fe.business.impl.IBManagerImpl implements IIBManager {

    @Override
    public List<InviteCustomerInfo> getInviteCustomerDetails(InviteCustomerSearchCondition condition, PagingInfo pagingInfo) {
        return getiAmsIbKickbackDAO().getInviteCustomerDetails(condition, pagingInfo);
    }

    @Override
    public List<InviteKickbackHistoryInfo> getInviteKickbackHistoryDetails(InviteKickbackHistoryCondition condition, PagingInfo pagingInfo) {
        return getiAmsIbKickbackDAO().getInviteKickbackHistoryDetails(condition, pagingInfo);
    }

    @Override
    public IBInfo getIbMgmtInfo(String customerId) {
//        IBInfo ibInfo = null;
//        AmsIb amsIb = getiAmsIbDAO().findById(AmsIb.class, customerId);
//        if(amsIb != null) {
//            ibInfo = new IBInfo();
//            BeanUtils.copyProperties(amsIb, ibInfo);
//        }
//        if(ibInfo != null) {
//            Long accountTotal = getIBAccountTotal(customerId);
//            ibInfo.setAccountTotal(accountTotal);
//            Double kickbackTotal = getiAmsIbKickbackDAO().getKickbackTotalWithStatus345(customerId);
//            ibInfo.setKickbackTotal(kickbackTotal);
//        }
    	IBInfo ibInfo = new IBInfo();
    	List<AmsIbKickback> ibKickBacks = getiAmsIbKickbackDAO().findByProperty(AmsIbKickbackDAO.CUSTOMER_ID, customerId);
    	Set<String> setCustomerIds = new HashSet<String>();
    	Double kickBackAmount = new Double(0);
    	for(AmsIbKickback ibKickBack : ibKickBacks){
    		if(IConstants.ACTIVE_FLG.INACTIVE.equals(ibKickBack.getActiveFlg()) 
    				|| ibKickBack.getKickbackAmount() <= 0 
    				|| !ITrsConstants.IB_APPROVE_STATUS.APPROVED.equals(ibKickBack.getApproveStatus())
    				|| ITrsConstants.STATUS_TYPE.TRADE_COMPLATED != ibKickBack.getKickbackType()){
    			continue;
    		}
    		
    		setCustomerIds.add(ibKickBack.getIbCustomerId());
    		kickBackAmount += ibKickBack.getKickbackAmount();
    		
    	}

        AmsIb amsIb = getiAmsIbDAO().findById(AmsIb.class, customerId);
        ibInfo.setIbLink(amsIb == null ? null : amsIb.getIbLink());
    	
    	ibInfo.setCustomerId(customerId);
    	ibInfo.setKickbackTotal(kickBackAmount);
    	ibInfo.setAccountTotal(Long.valueOf(setCustomerIds.size()));
    	
        return ibInfo;
    }
}
