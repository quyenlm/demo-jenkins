package phn.nts.ams.fe.web.action.contest;

import com.opensymphony.xwork2.ActionContext;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.entity.AmsDmc;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IDemoContestManager;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.DemoContestInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.DemoContestModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

import java.util.Date;
import java.util.Map;

/**
 * @author Quan.Le.Minh
 * @version NTS1.0
 * @description DemoContestAction
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestAction extends BaseSocialAction<DemoContestModel> {
    private static final long serialVersionUID = 4222625508456512850L;
    private static Logit log = Logit.getInstance(DemoContestAction.class);

    private DemoContestModel model = new DemoContestModel();
    private IDemoContestManager demoContestManager;
    private String result;
    private String msgCode;

    /**
     * Display detail of contest
     *
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 8, 2013
     */
    public String index() {
        log.info("[start]DemoContestAction.index()");

        String contestCd = "";
        String wlCode = "";
        String language = "";
        String contestId = httpRequest.getParameter("contestId");
        if (!StringUtil.isEmpty(contestId)) {
            try {
                Integer id = MathUtil.parseInteger(contestId);
                if (id == null) {
                    id = model.getContestId();
                }
                if (id != null) {
                    FrontUserOnline frontUserOnline = null;
                    FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
                    if (frontUserDetails != null) {
                        frontUserOnline = frontUserDetails.getFrontUserOnline();
                    }
                    wlCode = frontUserOnline.getWlCode();
                    language = frontUserOnline.getLanguage();
                    if (!demoContestManager.canViewContest(id, wlCode, language)) {
                        model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.notFound"));
                        return ERROR;
                    }
                    AmsDmc demoContest = demoContestManager.getDemoContestDetail(id);
                    if (demoContest == null) {
                        model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.notFound"));
                        setMsgCode(IConstants.DEMO_CONTEST.MSG_GET_DETAILS_FAIL);
                        return ERROR;
                    }

                    // Set contest content for display
                    model.setContestTitle(demoContest.getContestTitle());

                    model.setImage2(demoContest.getImage2());
                    model.setContestContent(demoContest.getContestContent());
                    model.setRegisterStartDate(demoContest.getRegisterStartDatetime());
                    model.setRegisterEndDate(demoContest.getRegisterEndDatetime());
                    model.setTradingStartDate(demoContest.getTradingStartDatetime());
                    model.setTradingEndDate(demoContest.getTradingEndDatetime());
                    model.setAwardStartDate(demoContest.getAwardStartDatetime());
                    model.setAwardEndDate(demoContest.getAwardEndDatetime());
                    model.setPrize(demoContest.getPrize());
                    model.setNotes(demoContest.getNote());
                    model.setDmcRule(demoContest.getDmcRules());
                    model.setDmcFAQs(demoContest.getDmcFaqs());
                    model.setTerms(demoContest.getTerms());
                    model.setContestCd(demoContest.getContestCd());
                    model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));

                    // Display title of Join Button
/*					FrontUserOnline frontUserOnline = null;
					FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
					if(frontUserDetails != null) {
						frontUserOnline = frontUserDetails.getFrontUserOnline();
					}*/
                    String customerId = frontUserOnline.getUserId();
                    contestCd = demoContest.getContestCd();
                    Date today = new Date();
                    DemoContestAccountInfo dmcAccountInfo = demoContestManager.joinedContest(customerId, contestCd, frontUserOnline.getCurrencyCode());
                    if (dmcAccountInfo != null) {
                        model.setDmcAccountInfo(dmcAccountInfo);
                    }
                    if (demoContest.getTradingEndDatetime().after(today)
                            && today.after(demoContest.getTradingStartDatetime())) {
                        /* Trading */
                        model.setJoinButtonTitle(getText("nts.ams.fe.label.democontest.detail_ranking.trading"));
                        // Disable Join button
                        model.setDisableJoinButton(true);
                        model.setShowJoinButton(true);
                    } else if (demoContest.getAwardEndDatetime().after(today)
                            && today.after(demoContest.getAwardStartDatetime())) {
                        /* Awarding */
                        model.setJoinButtonTitle(getText("nts.ams.fe.label.democontest.detail_ranking.awarding"));
                        // Disable Join button
                        model.setDisableJoinButton(true);
                        model.setShowJoinButton(true);
                    } else if (demoContest.getRegisterEndDatetime().after(today)
                            && today.after(demoContest.getRegisterStartDatetime())) {
                        /* Registering */
                        if (dmcAccountInfo != null) {
                            //model.setDmcAccountInfo(dmcAccountInfo);
                            model.setJoinButtonTitle(getText("nts.ams.fe.label.democontest.detail_ranking.joined"));
                            // Disable Join button
                            model.setDisableJoinButton(true);
                            model.setShowJoinButton(true);
                        } else {
                            model.setJoinButtonTitle(getText("nts.ams.fe.label.democontest.detail_ranking.join"));
                            // Enable Join button
                            model.setDisableJoinButton(false);
                            model.setShowJoinButton(true);
                        }

                    } else {
                        // Do not show Join button
                        model.setShowJoinButton(false);
                    }
                    // Get search condition
                    String participant = model.getFindParticipant();
                    String currencyCode = frontUserOnline.getCurrencyCode();
                    // Get paging info
                    PagingInfo pagingInfo = model.getPagingInfo();
                    if (pagingInfo == null) {
                        pagingInfo = new PagingInfo();
                    }

                    getDemoContestAccountList(id, contestCd, participant, currencyCode, pagingInfo);
                }
            } catch (Exception e) {
                log.error("DemoContestAction.index()");
                log.error(e.getMessage(), e);
            }
        }

        log.info("[end]DemoContestAction.index()");
        return SUCCESS;
    }

    /**
     * Get contest list
     *
     * @param
     * @return
     * @throws
     * @author anh.nguyen.ngoc
     * @CrDate Jan 4, 2013
     */
    @SuppressWarnings("unchecked")
    public String getContestList() {
    	try{
    		FrontUserOnline frontUserOnline = null;
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            if (frontUserDetails != null) {
                frontUserOnline = frontUserDetails.getFrontUserOnline();

            }
            String customerLanguage = frontUserOnline.getLanguage();
            //[NTS1.0-Quan.Le.Minh]Jan 8, 2013A - Start
            String wlCode = frontUserOnline.getWlCode();

            /*Get error message*/
            if (result != null) {
                getMsgCode(result);
            }
            //[NTS1.0-Quan.Le.Minh]Jan 8, 2013A - End

            @SuppressWarnings("rawtypes")
            Map session = ActionContext.getContext().getSession();

            // get paging
            PagingInfo pagingInfo = model.getPagingInfo();
            if (pagingInfo == null) {
                pagingInfo = new PagingInfo();
            }
            session.put("paging", pagingInfo);

            //[NTS1.0-Quan.Le.Minh]Jan 8, 2013M - Start
            SearchResult<DemoContestInfo> result = demoContestManager.getContestList(customerLanguage, wlCode, pagingInfo);
            //[NTS1.0-Quan.Le.Minh]Jan 8, 2013M - End
            model.setPagingInfo(result.getPagingInfo());
            model.setContestList(result);
    	} catch (Exception ex){
    		log.error(ex.getMessage(), ex);
    	}
        return SUCCESS;
    }

    /**
     * Get data for creating demo account
     *
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 8, 2013
     */
    public String searchForGenerate() {
        try {
            DemoContestAccountInfo result = demoContestManager.getInfoForGenerateAccount(model.getContestId());
            model.setDmcAccountInfo(result);
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));
            //[NTS1.0-Quan.Le.Minh]Jan 12, 2013A - Start
            /*Set CustomerId*/
            FrontUserDetails useretails = FrontUserOnlineContext.getFrontUserOnline();
            if (useretails == null) {
                return null;
            }
            FrontUserOnline userOnline = useretails.getFrontUserOnline();
            if (userOnline == null) {
                return null;
            }
            result.setCustomerId(userOnline.getUserId());
            //[NTS1.0-Quan.Le.Minh]Jan 12, 2013A - End
        } catch (Exception e) {
            log.error(e);
        }
        return SUCCESS;
    }

    /**
     * Save information of acocount into Database
     *
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 8, 2013
     */
    public String generateAccountConfirm() {
        try {
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));

            if (model.getDmcAccountInfo() == null) {
                model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.empty"));
                return INPUT;
            }

            if (StringUtil.isEmpty(model.getDmcAccountInfo().getNickname())) {
                model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.empty"));
                return INPUT;
            }

            if (checkExistNickname()) {
                model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.exist"));
                return INPUT;
            }

            Integer registerAccountResult = demoContestManager.saveDmcAccount(model.getDmcAccountInfo(), model.getContestId(), model.getContestCd());
            if (!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
                model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.failed"));
                return ERROR;
            }

            /*Set success message*/
            model.setSuccessMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.create.success"));

            return SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.failed"));
            return ERROR;
        }
    }

    /**
     * Check existing of nickname
     *
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 8, 2013
     */
    private boolean checkExistNickname() {
        boolean result = demoContestManager.checkExistNickname(model.getDmcAccountInfo().getNickname(), model.getContestId());
        return result;
    }

    /**
     * getDemoContestAccountList
     *
     * @param
     * @return
     * @throws
     * @author anh.nguyen.ngoc
     * @CrDate Jan 7, 2013
     */
    private void getDemoContestAccountList(Integer contestId, String contestCd, String participant, String currencyCode, PagingInfo pagingInfo) {
        SearchResult<DemoContestAccountInfo> result = demoContestManager.findDmcAccountByCondition(contestId, contestCd, participant, currencyCode, pagingInfo);
        if (result != null && result.size() != 0) {
            model.setDmcAccountList(result);
            model.setPagingInfo(result.getPagingInfo());
        } else {
            String msg = getText("nts.ams.fe.label.democontest.info_message.no_participant_found");
            model.setInfoMessage(msg);
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @author Quan.Le.Minh
     * @CrDate Jan 9, 2013
     */
    private void getMsgCode(String msgCode) {
        if (msgCode != null) {
            if (msgCode.equalsIgnoreCase(IConstants.DEMO_CONTEST.MSG_GET_DETAILS_FAIL)) {
                model.setErrorMessage(getText("nts.ams.fe.label.democontest.generate_demo_account.message.notFound"));
            }
        }
    }

    @Override
    public DemoContestModel getModel() {
        return model;
    }

    public IDemoContestManager getDemoContestManager() {
        return demoContestManager;
    }

    public void setDemoContestManager(IDemoContestManager demoContestManager) {
        this.demoContestManager = demoContestManager;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

}
