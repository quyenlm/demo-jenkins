package phn.nts.ams.fe.business;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsDmc;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.DemoContestInfo;

/**
 * @description 
 * @version NTS1.0
 * @author Quan.Le.Minh
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public interface IDemoContestManager {
	public AmsDmc getDemoContestDetail(Integer contestId);
	public DemoContestAccountInfo joinedContest(String customerId, String contestCd, String currencyCode);
	public SearchResult<DemoContestInfo> getContestList(String customerId, String wlCode, PagingInfo paging);
	public DemoContestAccountInfo getInfoForGenerateAccount(Integer contestId);
	public boolean checkExistNickname(String nickname, Integer contestId);
	public Integer saveDmcAccount(DemoContestAccountInfo dmcAccountInfo, Integer contestId, String contestCd);
	public String getMailByCustomerId(String customerId);
	public String generateKey(String prefix, String contextId, int leng);
	
	//[NTS1.0-anhndn]Jan 7, 2013A - Start 
	public SearchResult<DemoContestAccountInfo> findDmcAccountByCondition(Integer contestId, String contestCd, String participant, String currencyCode, PagingInfo pagingInfo);
	//[NTS1.0-anhndn]Jan 7, 2013A - End
	public boolean canViewContest(Integer contestId, String wlCode, String language);
}
