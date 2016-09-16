package phn.nts.social.fe.web.action;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ISocialManager;
import phn.nts.ams.fe.domain.*;
import phn.nts.ams.fe.model.ChangeFundsModel;
import phn.nts.ams.fe.model.TraderModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

public class TraderAction extends BaseSocialAction<TraderModel> {
	
	private static Logit LOG = Logit.getInstance(TraderAction.class);
	private static final long serialVersionUID = 5827828549969392634L;
	private IAccountManager accountManager;
    private String dispatch;
	
	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}

	private TraderModel model = new TraderModel();
	
	public TraderModel getModel() {
		return model;
	}
	
	public String copyList(){
		try{
			
			PagingInfo pagingInfo = model.getPagingInfo();
			if(pagingInfo == null) {
				pagingInfo = new PagingInfo();
				model.setPagingInfo(pagingInfo);
			}
			pagingInfo.setOffset(IConstants.PAGING.COPY_LIST);
			
			socialManager.loadCopyListInfo(model);
 			return SUCCESS;
		}catch (Exception e) {
			LOG.error("Error occured when navigating to copy list: "+e.getMessage(), e);
			return INPUT;
		}
	}

    public String followList(){
        try{
            FollowListModel followListModel = model.getFollowListModel();

            PagingInfo pagingInfo = model.getPagingInfo();
            if(pagingInfo == null) {
                pagingInfo = new PagingInfo();
                model.setPagingInfo(pagingInfo);
            }
            pagingInfo.setOffset(IConstants.PAGING.SC_FOLLOW_LIST_PAGE_SIZE);
            followListModel.setGuestMode(getMode().equals(IConstants.SOCIAL_MODES.GUEST_MODE));
            String customerId = getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE) ? getCurrentCustomerId() : model.getId();

            socialManager.loadFollowListInfo(followListModel, getCurrentCustomerId(), customerId, pagingInfo);
            return SUCCESS;
        }catch (Exception e) {
            LOG.error("Error occurred while processing follow list page: " + e.getMessage(), e);
            return INPUT;
        }
    }
	
    public String copyFollowActionProcessing(){
        if(model.getCopyFollowInfo() == null) model.setCopyFollowInfo(new CopyFollowInfo());
        try{
            String customerId = model.getCopyFollowInfo().getCustomerId() == null ? model.getId() : model.getCopyFollowInfo().getCustomerId();
            if("computeEquity".equals(dispatch)){
                BigDecimal investAmount = MathUtil.parseBigDecimal(model.getCopyFollowInfo().getInvestAmountStr());
                if(investAmount != null && investAmount.compareTo(new BigDecimal("0")) > 0){
                    model.getCopyFollowInfo().setEquityPercentage(socialManager.computeEquityPercentage(getCurrentCustomerId(), customerId, investAmount, model.getAccountId(), model.getBrokerCd()));
                    model.getCopyFollowInfo().setAjaxSuccess(true);
                }
            } else if("computeInvestAmount".equals(dispatch)){
                BigDecimal equityPercentage = MathUtil.parseBigDecimal(model.getCopyFollowInfo().getEquityPercentageStr());
                if(equityPercentage != null && equityPercentage.compareTo(new BigDecimal("0")) > 0){
                    model.getCopyFollowInfo().setInvestAmount(socialManager.computeInvestAmount(getCurrentCustomerId(), customerId, equityPercentage, model.getAccountId(), model.getBrokerCd()));
                    model.getCopyFollowInfo().setAjaxSuccess(true);
                }
            } else {
                if(getCurrentCustomerId() == null || getCurrentCustomerId().equals(customerId)) throw new InvalidActionException("TIME_OUT");
                if("unfollow".equals(dispatch)){
                    Integer newFollowerNo = socialManager.stopFollow(getCurrentCustomerId(), customerId);
                    if(model.getCopyFollowInfo().isFullRender()) loadSCFE002CopyFollowPartData();
                    else{
                        model.setGuestMode(getMode().equals(IConstants.SOCIAL_MODES.GUEST_MODE));
                        model.getCopyFollowInfo().setFollowers(newFollowerNo);
                    }
                    model.getCopyFollowInfo().setAjaxMsg(getText("MSG_SC_043"));
                    model.getCopyFollowInfo().setAjaxSuccess(true);
                } else if("follow".equals(dispatch)){
                    Integer newFollowerNo = socialManager.followCustomer(getCurrentCustomerId(), customerId);
                    if(model.getCopyFollowInfo().isFullRender()) loadSCFE002CopyFollowPartData();
                    else {
                        model.setGuestMode(getMode().equals(IConstants.SOCIAL_MODES.GUEST_MODE));
                        model.getCopyFollowInfo().setFollowers(newFollowerNo);
                    }
                    model.getCopyFollowInfo().setAjaxMsg(getText("MSG_SC_041"));
                    model.getCopyFollowInfo().setAjaxSuccess(true);
                } else if("uncopy".equals(dispatch)){
                    socialManager.stopCopy(getCurrentCustomerId(), customerId, model.getAccountId(), model.getBrokerCd());
                    if(model.getCopyFollowInfo().isFullRender()) loadSCFE002CopyFollowPartData();
                    model.getCopyFollowInfo().setAjaxMsg(getText("MSG_SC_039"));
                    model.getCopyFollowInfo().setAjaxSuccess(true);
                } else if("copy".equals(dispatch)){
                    CopyFollowInfo copyInfo = model.getCopyFollowInfo();
                    if(validateCopyTradeData(copyInfo)){
                        socialManager.copyTradeCustomer(getCurrentCustomerId(), customerId, model.getAccountId(), model.getBrokerCd(), copyInfo, getWlCode());
                        if(model.getCopyFollowInfo().isFullRender()) loadSCFE002CopyFollowPartData();
                        model.getCopyFollowInfo().setAjaxMsg(getText("MSG_SC_045"));
                        model.getCopyFollowInfo().setAjaxSuccess(true);
                    } else {
                        copyInfo.setAjaxSuccess(false);
                    }
                }
            }
        }catch (InvalidActionException e){
        	model.getCopyFollowInfo().setAjaxSuccess(false);
        	if("TIME_OUT".equals(e.getMessageCode())){
        		model.getCopyFollowInfo().setAjaxMsg(getErrorMessage(dispatch));
        	} else {
        		model.getCopyFollowInfo().setAjaxMsg(getText(e.getMessageCode(), e.getArgs()));
        	}
        }
        catch (Exception e){
            model.getCopyFollowInfo().setAjaxSuccess(false);
            model.getCopyFollowInfo().setAjaxMsg(getErrorMessage(dispatch));
            LOG.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    private String getErrorMessage(String dispatch){
    	if("unfollow".equals(dispatch)){
    		return getText("MSG_SC_044");
        } else if("follow".equals(dispatch)){
        	return getText("MSG_SC_042");
        } else if("copy".equals(dispatch)){
            return getText("MSG_SC_046");
        } else if("uncopy".equals(dispatch)){
            return getText("MSG_SC_040");
        }
    	return null;
    }
    
    private boolean validateCopyTradeData(CopyFollowInfo copyInfo) {
        BigDecimal zero = new BigDecimal("0");
        if(StringUtil.isEmpty(copyInfo.getInvestAmountStr()) || MathUtil.parseBigDecimal(copyInfo.getInvestAmountStr(), null) == null || zero.compareTo(MathUtil.parseBigDecimal(copyInfo.getInvestAmountStr(), null)) > 0){
            copyInfo.setAjaxMsg(getText("MSG_SC_047", new String[] {getText("nts.socialtrading.copytrade.copylist.message.copy_amount")}));
            return false;
        }
        /*if(StringUtil.isEmpty(copyInfo.getEquityPercentageStr()) || MathUtil.parseBigDecimal(copyInfo.getEquityPercentageStr(), null) == null || zero.compareTo(MathUtil.parseBigDecimal(copyInfo.getEquityPercentageStr(), null)) > 0){
            copyInfo.setAjaxMsg(getText("MSG_SC_047", new String[] {"Equity"}));
            return false;
        }*/
        //copyInfo.setEquityPercentage(MathUtil.parseBigDecimal(copyInfo.getEquityPercentageStr()));
        copyInfo.setInvestAmount(MathUtil.parseBigDecimal(copyInfo.getInvestAmountStr()));
        return true;
    }

    public String loadChangeFundsInfo(){
		ChangeFundsModel changeFundsModel = model.getChangeFundModel();
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails!=null){
				socialManager.loadChangeFundsInfo(changeFundsModel);
				String avatarTimestamp = model.getAvatarTimestamp(changeFundsModel.getCustomerId());
				changeFundsModel.setAvatarTimestamp(avatarTimestamp);
				CustomerInfo customerInfo = accountManager.getCustomerInfo(getCurrentCustomerId());
				changeFundsModel.setBaseCurrency(customerInfo.getCurrencyCode());
				changeFundsModel.setLoadChangeFundInfoResult(IConstants.COPY_LIST_PROCESSING_RESULT.SUCCESS);
				return SUCCESS;
			}else{
				HttpServletRequest request = ServletActionContext.getRequest();
				String referal = request.getHeader("Referer");
				LOG.info("---Referer ="+referal);
				dispatch=phn.nts.ams.fe.util.AppConfiguration.getLogoutUrl()+"?SS=0";
				return "guest";
			}
		}catch (Exception e) {
			changeFundsModel.setLoadChangeFundInfoResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
			LOG.error("Error occured when loading change funds info: "+e.getMessage(), e);
			return INPUT;
		}
		
	}
	
	public String changeFundsProcessing(){
		ChangeFundsModel changeFundsModel = model.getChangeFundModel();
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails!=null){
				String currentCustomerId = getCurrentCustomerId();
				String investAmountStr = changeFundsModel.getAmount();
				BigDecimal investmentAmount = MathUtil.parseBigDecimal(investAmountStr);
				
				if(investmentAmount == null || investmentAmount.equals(new BigDecimal("0"))){
					changeFundsModel.setChangeFundResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
					changeFundsModel.setMessage(getText("MSG_SC_013", new String[]{getText("nts.socialtrading.copytrade.copylist.message.investment_amount")}));
					return INPUT;
				}
				
				CustomerInfo customerInfo = accountManager.getCustomerInfo(currentCustomerId);
				
				String baseCurrency ="USD";
				if(customerInfo != null){
					baseCurrency = customerInfo.getCurrencyCode();
				}
				
				BigDecimal minInvestmentAmt = socialManager.getMinInvestmentAmount(baseCurrency);
				if(investmentAmount.compareTo(minInvestmentAmt) <0){
					changeFundsModel.setChangeFundResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
					changeFundsModel.setMessage(getText("MSG_SC_022", new String[]{String.valueOf(minInvestmentAmt)}));
					return INPUT;
				}
				
				boolean remainAmtCheck =  socialManager.checkRemainAmountAfterInvestment(currentCustomerId,changeFundsModel.getChangeFundCopyId(), investmentAmount);
				if(!remainAmtCheck){
					changeFundsModel.setChangeFundResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
					changeFundsModel.setMessage(getText("MSG_SC_024"));
					return INPUT;
				}
				
				socialManager.updateInvestmentAmount(changeFundsModel);
				socialManager.updateCopyRate(changeFundsModel);
				changeFundsModel.setChangeFundResult(IConstants.COPY_LIST_PROCESSING_RESULT.SUCCESS);
				changeFundsModel.setMessage(getText("MSG_SC_051"));
				return SUCCESS;
			}else{
				HttpServletRequest request = ServletActionContext.getRequest();
				String referal = request.getHeader("Referer");
				LOG.info("---Referer ="+referal);
				dispatch=phn.nts.ams.fe.util.AppConfiguration.getLogoutUrl()+"?SS=0";
				return "guest";
			}
		}catch (Exception e) {
			changeFundsModel.setChangeFundResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
			changeFundsModel.setMessage(getText("nts.socialtrading.copytrade.copylist.label.error_change_fund"));
			LOG.error("Error occured when changing fund processing", e);
			return INPUT;
		}
	}
	
	public String stopCopy(){
		CopyListModel copyListModel = model.getCopyListModel();
		try{
			socialManager.stopCopyProcessing(model);
			
			copyListModel.setStopCopyResult(IConstants.COPY_LIST_PROCESSING_RESULT.SUCCESS);
			copyListModel.setMessage(getText("MSG_SC_039"));
			return SUCCESS;
		} catch (Exception e) {
			copyListModel.setStopCopyResult(IConstants.COPY_LIST_PROCESSING_RESULT.FAIL);
			copyListModel.setMessage(getText("MSG_SC_040"));
			LOG.error("Error occured when stoping copy "+e.getMessage(), e);
			return INPUT;
		}
	}


	
	
    public String getDispatch() {
        return dispatch;
    }

    public void setDispatch(String dispatch) {
        this.dispatch = dispatch;
    }
    
	public static String[] cutString(final String s){
		if (s.contains("-")) {			
			String[] parts = s.split("-");
			return parts;
		} else {
		    throw new IllegalArgumentException("String " + s + " does not contain -");
		}
		
	}


}


