package phn.nts.ams.fe.domain.converter;

import java.sql.Timestamp;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDmc;
import phn.com.nts.db.entity.AmsDmcCustomerContest;
import phn.com.nts.db.entity.AmsDmcCustomerContestId;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.IConstants.DEMO_CONTEST;
import phn.com.nts.util.common.MathUtil;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.DemoContestInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

/**
 * @description DemoContestInfoConverter
 * @version NTS1.0
 * @author anh.nguyen.ngoc
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestInfoConverter {

	public static DemoContestInfo toInfo(AmsDmc entity) {
		DemoContestInfo info = new DemoContestInfo();
		info.setContestId(entity.getContestId());
		info.setContestTitle(entity.getContestTitle());
		info.setShortContent(entity.getShortContent());
		info.setImage1(entity.getImage1());
		return info;
	}
	
	/**
	 * Convert AmsDmc entity to DemoContestAccountInfo　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 5, 2013
	 */
	public static DemoContestAccountInfo toContestAccountInfo(AmsDmc amsDmc){
		DemoContestAccountInfo info = new DemoContestAccountInfo();
		info.setContestId(amsDmc.getContestId());
		info.setSubGroupId(amsDmc.getAmsSubGroup().getSubGroupId());
		info.setSubGroupName(amsDmc.getAmsSubGroup().getSubGroupCode());
		info.setDeposit(amsDmc.getDeposit() == null ? "" : String.valueOf(amsDmc.getDeposit()));
		info.setTradingStartDatetime(amsDmc.getTradingStartDatetime());
		info.setTradingEndDatetime(amsDmc.getTradingEndDatetime());
		info.setLeverage(amsDmc.getAmsSubGroup().getLeverage());
		info.setCurrencyCode(amsDmc.getAmsSubGroup().getCurrencyCode());
		return info;
	}
	
	public static AmsDmcCustomerContest toDmcCustomerContestEntity(DemoContestAccountInfo info){
		Timestamp currennt = new Timestamp(System.currentTimeMillis());
		Double deposit = MathUtil.parseDouble(info.getDeposit());

		AmsDmcCustomerContest entity = new AmsDmcCustomerContest();
		AmsDmcCustomerContestId id = new AmsDmcCustomerContestId(info.getContestId(), info.getCustomerId());
		entity.setId(id);

		AmsCustomer customer = new AmsCustomer();
		customer.setCustomerId(info.getCustomerId());
		entity.setAmsCustomer(customer);
		AmsDmc dmc = new AmsDmc();
		dmc.setContestId(info.getContestId());
		entity.setAmsDmc(dmc);
		entity.setNickname(info.getNickname());
		entity.setBalance(deposit);
		entity.setRank(DEMO_CONTEST.DEFAULT_RANK);
		
		FrontUserDetails useretails = FrontUserOnlineContext.getFrontUserOnline();
		if(useretails == null) {
			return null;
		}
		FrontUserOnline userOnline = useretails.getFrontUserOnline();
		if (userOnline == null) {
			return null;
		}
		
//		String loginId = userOnline.getLoginId();
		entity.setDmcMt4Id(info.getLoginId());
		entity.setDmcMt4Pass(info.getPassword());
		entity.setRegistDatetime(currennt);
		entity.setStatus(DEMO_CONTEST.STATUS_ACCEPT);
		entity.setPrevRank(DEMO_CONTEST.DEFAULT_PRERANK);
		entity.setPrevBalance(deposit);
		entity.setTradingAmount(DEMO_CONTEST.DEFAULT_TRADING_AMOUNT);
		entity.setUpdateUser(null);
		entity.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		entity.setInputDate(currennt);
		entity.setUpdateDate(currennt);
		
		return entity;
	}
}
