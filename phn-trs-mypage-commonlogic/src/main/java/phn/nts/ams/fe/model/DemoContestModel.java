package phn.nts.ams.fe.model;

import java.sql.Timestamp;
import java.util.List;

import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsDmc;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.DemoContestInfo;

/**
 * @description DemoContestModel
 * @version NTS1.0
 * @author Quan.Le.Minh
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestModel extends BaseSocialModel {
	private static final long serialVersionUID = -3437980525278936229L;

	private Integer contestId;
	private String contestTitle;
	private String image2;
	private String contestContent;
	private Timestamp registerStartDate;
	private Timestamp registerEndDate;
	private Timestamp tradingStartDate;
	private Timestamp tradingEndDate;
	private Timestamp awardStartDate;
	private Timestamp awardEndDate;
	private String prize;
	private String dmcRule;
	private String notes;
	private String terms;
	private String dmcFAQs;
	private String contestCd;
	
	public String getContestCd() {
		return contestCd;
	}
	public void setContestCd(String contestCd) {
		this.contestCd = contestCd;
	}
	private String joinButtonTitle;
	private boolean showJoinButton;
	private boolean disableJoinButton;
	
	private String pattern;
	
	private List<AmsDmc> listAmsDmc;
	//[NTS1.0-anhndn]Jan 4, 2013A - Start 
	private SearchResult<DemoContestInfo> contestList;
	//[NTS1.0-anhndn]Jan 4, 2013A - End
	private DemoContestAccountInfo dmcAccountInfo;
	private String findParticipant;
	
	//[NTS1.0-anhndn]Jan 7, 2013A - Start 
	private SearchResult<DemoContestAccountInfo> dmcAccountList;
	//[NTS1.0-anhndn]Jan 7, 2013A - End
	
	public String getContestTitle() {
		return contestTitle;
	}
	public void setContestTitle(String contestTitle) {
		this.contestTitle = contestTitle;
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image) {
		this.image2 = image;
	}
	public String getContestContent() {
		return contestContent;
	}
	public void setContestContent(String contestContent) {
		this.contestContent = contestContent;
	}
	public Timestamp getRegisterStartDate() {
		return registerStartDate;
	}
	public void setRegisterStartDate(Timestamp registerStartDate) {
		this.registerStartDate = registerStartDate;
	}
	public Timestamp getRegisterEndDate() {
		return registerEndDate;
	}
	public void setRegisterEndDate(Timestamp registerEndDate) {
		this.registerEndDate = registerEndDate;
	}
	public Timestamp getTradingStartDate() {
		return tradingStartDate;
	}
	public void setTradingStartDate(Timestamp tradingStartDate) {
		this.tradingStartDate = tradingStartDate;
	}
	public Timestamp getTradingEndDate() {
		return tradingEndDate;
	}
	public void setTradingEndDate(Timestamp tradingEndDate) {
		this.tradingEndDate = tradingEndDate;
	}
	public Timestamp getAwardStartDate() {
		return awardStartDate;
	}
	public void setAwardStartDate(Timestamp awardStartDate) {
		this.awardStartDate = awardStartDate;
	}
	public Timestamp getAwardEndDate() {
		return awardEndDate;
	}
	public void setAwardEndDate(Timestamp awardEndDate) {
		this.awardEndDate = awardEndDate;
	}
	public String getPrize() {
		return prize;
	}
	public void setPrize(String prize) {
		this.prize = prize;
	}
	public String getDmcRule() {
		return dmcRule;
	}
	public void setDmcRule(String dmcRule) {
		this.dmcRule = dmcRule;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public String getDmcFAQs() {
		return dmcFAQs;
	}
	public void setDmcFAQs(String dmcFAQs) {
		this.dmcFAQs = dmcFAQs;
	}
	public String getFindParticipant() {
		return findParticipant;
	}
	public void setFindParticipant(String findParticipant) {
		this.findParticipant = findParticipant;
	}
	public List<AmsDmc> getListAmsDmc() {
		return listAmsDmc;
	}
	public void setListAmsDmc(List<AmsDmc> listAmsDmc) {
		this.listAmsDmc = listAmsDmc;
	}
	public String getJoinButtonTitle() {
		return joinButtonTitle;
	}
	public void setJoinButtonTitle(String joinButtonTitle) {
		this.joinButtonTitle = joinButtonTitle;
	}
	
	//[NTS1.0-anhndn]Jan 4, 2013A - Start 
	public SearchResult<DemoContestInfo> getContestList() {
		return contestList;
	}
	public void setContestList(SearchResult<DemoContestInfo> contestList) {
		this.contestList = contestList;
	}
	//[NTS1.0-anhndn]Jan 4, 2013A - End
	public boolean isShowJoinButton() {
		return showJoinButton;
	}
	public void setShowJoinButton(boolean showJoinButton) {
		this.showJoinButton = showJoinButton;
	}
	public boolean isDisableJoinButton() {
		return disableJoinButton;
	}
	public void setDisableJoinButton(boolean disableJoinButton) {
		this.disableJoinButton = disableJoinButton;
	}
	public Integer getContestId() {
		return contestId;
	}
	public void setContestId(Integer contestId) {
		this.contestId = contestId;
	}
	public DemoContestAccountInfo getDmcAccountInfo() {
		return dmcAccountInfo;
	}
	public void setDmcAccountInfo(DemoContestAccountInfo dmcAccountInfo) {
		this.dmcAccountInfo = dmcAccountInfo;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	//[NTS1.0-anhndn]Jan 7, 2013A - Start 
	public SearchResult<DemoContestAccountInfo> getDmcAccountList() {
		return dmcAccountList;
	}
	public void setDmcAccountList(
			SearchResult<DemoContestAccountInfo> dmcAccountList) {
		this.dmcAccountList = dmcAccountList;
	}
	//[NTS1.0-anhndn]Jan 7, 2013A - End
	
}
