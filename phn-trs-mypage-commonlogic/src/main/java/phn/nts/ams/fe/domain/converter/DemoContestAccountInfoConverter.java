package phn.nts.ams.fe.domain.converter;

import phn.com.nts.db.entity.AmsDmcCustomerContest;
import phn.com.nts.util.common.FormatHelper;
import phn.com.nts.util.common.IConstants;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;

/**
 * @description DemoContestAccountInfoConverter
 * @version NTS1.0
 * @author anh.nguyen.ngoc
 * @CrDate Jan 7, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestAccountInfoConverter {
	
	public static DemoContestAccountInfo toInfo(AmsDmcCustomerContest entity, String currencyCode) {
		DemoContestAccountInfo info = new DemoContestAccountInfo();
		
		if (entity.getRank().equals(IConstants.DEMO_CONTEST.DEFAULT_RANK)) {
			info.setRank("-");
		}
		else {
			info.setRank(Integer.toString(entity.getRank()));
		}
		info.setAccountId(entity.getDmcMt4Id());
		info.setNickname(entity.getNickname());
		double gain = (entity.getBalance() - entity.getAmsDmc().getDeposit())/entity.getAmsDmc().getDeposit();
		info.setGain(FormatHelper.formatString(gain, "#.##%"));
		String pattern = MasterDataManagerImpl.getInstance().getPattern(currencyCode);
		info.setBalance(FormatHelper.formatString(entity.getBalance(), pattern));
		info.setTradingVolume(Double.toString(entity.getTradingAmount()));
		info.setStatus(entity.getStatus());
		//[NTS1.0-Quan.Le.Minh]Jan 17, 2013A - Start 
		info.setLoginId(entity.getDmcMt4Id());
		info.setPassword(entity.getDmcMt4Pass());
		//[NTS1.0-Quan.Le.Minh]Jan 17, 2013A - End
		return info;
	}
}
