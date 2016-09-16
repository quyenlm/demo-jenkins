package phn.nts.social.fe.web.action;

import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;
import phn.com.nts.ams.web.condition.SummaryTradingInfo;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.ScOrderInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.domain.CopierCustomerModel;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.db.entity.ScCustomerCopy;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.ISocialManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.InvalidActionException;
import phn.nts.ams.fe.domain.LeaderBoardInfo;
import phn.nts.ams.fe.domain.LiveRateInfo;
import phn.nts.ams.fe.domain.ScCustomerServiceInfo;
import phn.nts.ams.fe.domain.ScSummaryTradingCustInfo;
import phn.nts.ams.fe.model.CustomerCopierModel;
import phn.nts.ams.fe.model.CustomerFollowerModel;
import phn.nts.ams.fe.model.SocialModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.social.ScSymbolMarketWatchInfo;
import com.nts.dealing.inmem.memcached.DataProvider;
import com.opensymphony.xwork2.ActionContext;

@SuppressWarnings("deprecation")
public class SocialAction extends BaseSocialAction<SocialModel> {
	
	private static Logit LOG = Logit.getInstance(SocialAction.class);
	
	private SocialModel model = new SocialModel();
	private String dispatch;
	private String selectedCustomerId;
	private static HashMap<String,FxSymbol> allSymbol;
	private Double amount;
	private String orderTicket;
	private String validateOrder;
	
	public String getValidateOrder() {
		return validateOrder;
	}

	public void setValidateOrder(String validateOrder) {
		this.validateOrder = validateOrder;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getOrderTicket() {
		return orderTicket;
	}

	public void setOrderTicket(String orderTicket) {
		this.orderTicket = orderTicket;
	}

	public String getSelectedCustomerId() {
		return selectedCustomerId;
	}

	public void setSelectedCustomerId(String selectedCustomerId) {
		this.selectedCustomerId = selectedCustomerId;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public SocialModel getModel() {
		return model;
	}
	
	public String loadLiveRateSymbolsInfo(){
		LiveRateInfo liveRateInfo = model.getLiveRateInfo();
		if(liveRateInfo == null){
			liveRateInfo = new LiveRateInfo();
		}
		socialManager.loadLiveRateSymbolsInfo(liveRateInfo);
		return SUCCESS;
	}

	private static String ticketIdCheck = null;
	@SuppressWarnings("unused")
	public String home() {
		LOG.info("home");
		this.model.setBrandMode("Home");
		try {
            profileManager.ensureAvatarCreated(getCurrentCustomerId(), httpRequest.getSession().getServletContext().getRealPath("/images/user-pict.png"));
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				this.setSubGroupCd(frontUserOnline);
				this.model.setUserOnline(frontUserOnline);
				model.setDefaultLanguage(frontUserOnline.getLanguage());
				// Set Username for check firstLogin before show change agreement.
				model.setUsernameFirstLogin(frontUserOnline.getUserName());
				//[NTS1.0-le.hong.ha]Jun 11, 2013A - Start 
				LOG.debug("oldTicketId: " + ticketIdCheck + " newTicketId: " + frontUserOnline.getTicketId());
				if(ticketIdCheck == null || !ticketIdCheck.equals(frontUserOnline.getTicketId())){
					//Get agreement
					//Get subgroupId for servicetypes
//					List<CustomerServicesInfo> serviceInfos = frontUserOnline.getListCustomerServiceInfo();
//					Integer fxSubgroupId = null;
//					Integer boSubgroupId = null;
//					Integer socialSubgroupId = null;
//					for (CustomerServicesInfo customerServicesInfo : serviceInfos) {
//						if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.FX)){
//							fxSubgroupId = customerServicesInfo.getSubGroupId();
//						}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.BO)){
//							boSubgroupId = customerServicesInfo.getSubGroupId();
//						}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE)){
//							socialSubgroupId = customerServicesInfo.getSubGroupId();
//						}
//					}
//					List<AmsMessage> agreements = socialManager.getAgreementInfo(frontUserOnline.getUserId(),fxSubgroupId,boSubgroupId,socialSubgroupId);
					List<AmsMessage> agreements = frontUserOnline.getAgreements();
					if(agreements != null && agreements.size() > 0){
						model.setAmsMessages(agreements);
						model.setChangeAgreement(1);
						return "agree";
					}else{
						model.setChangeAgreement(0);
					}
				}else{
					model.setChangeAgreement(-1);
				}
				//[NTS1.0-le.hong.ha]Jun 11, 2013A - End
			} else {
				HttpServletRequest request = ServletActionContext.getRequest();
				String referal = request.getHeader("Referer");
				LOG.info("---Referer ="+referal);
				dispatch=phn.nts.ams.fe.util.AppConfiguration.getLogoutUrl()+"?SS=0";
				return "guest";
			}
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		
		return SUCCESS;
	}
	
	/**
	 * Agree agreement
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 11, 2013
	 */
	/*public String agreeConfirm(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest){
		try {
			if(amsCustomerNewsUpdateRequest.getNewsInfo().getMessageType() == null){
				throw new Exception("Invalid message type is null --> please check message type input");
			}
//			List<AmsMessage> agreements = getListAgreement(amsCustomerNewsUpdateRequest);
			if(amsCustomerNewsUpdateRequest.getNewsInfo().getReadFlg().getNumber() == 0){
				socialManager.disagreeConfirm(amsCustomerNewsUpdateRequest.getCustomerId(), agreements);
			}else if(amsCustomerNewsUpdateRequest.getNewsInfo().getReadFlg().getNumber() == 1){
				socialManager.agreeConfirm(amsCustomerNewsUpdateRequest);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ERROR;
		}
		
		return SUCCESS;
	}*/
	
	/*private List<AmsMessage> getListAgreement(AmsCustomerNewsUpdateRequest.Builder amsCustomerNewsUpdateRequest) throws Exception{
		List<AmsMessage> result = new ArrayList<AmsMessage>();
		
		if(ITrsConstants.MESSAGE_TYPE.NOTIFICATION_NEWS.compareTo(amsCustomerNewsUpdateRequest.getNewsInfo().getMessageType().getNumber()) == 0){
			result = socialManager.getAgreementInfo(amsCustomerNewsUpdateRequest.getCustomerId(), ITrsConstants.MESSAGE_TYPE.NOTIFICATION_NEWS);
		}else if(ITrsConstants.MESSAGE_TYPE.NORMAL_NEWS.compareTo(amsCustomerNewsUpdateRequest.getNewsInfo().getMessageType().getNumber()) == 0){
			AmsMessage amsMessage = new AmsMessage();
			amsMessage.setMessageId(Integer.valueOf(amsCustomerNewsUpdateRequest.getNewsInfo().getMessageId()));
			amsMessage.setServiceType(amsCustomerNewsUpdateRequest.getNewsInfo().getServiceType().getNumber());
			result.add(amsMessage);
		}
		return result;
	}*/
	
	/*private void updateCacheAgree(Integer messageType){
		
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		
		if(ITrsConstants.MESSAGE_TYPE.NOTIFICATION_NEWS == messageType.intValue()){
			
			frontUserOnline.setAgreements(null);
			
		}else if(ITrsConstants.MESSAGE_TYPE.NORMAL_NEWS == messageType.intValue()){
			
			AmsMessage normalNewsMessage = null;
			List<AmsMessage> normalMessages = socialManager.getAgreementInfo(frontUserOnline.getUserId(),ITrsConstants.MESSAGE_TYPE.NORMAL_NEWS, frontUserOnline.getSubgroupIdByServiceType(ITrsConstants.SERVICES_TYPE.FX) ,
					 frontUserOnline.getSubgroupIdByServiceType(ITrsConstants.SERVICES_TYPE.BO),frontUserOnline.getSubgroupIdByServiceType(ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE));
			
			if(!CollectionUtils.isEmpty(normalMessages)){
				normalNewsMessage = normalMessages.get(0);
			}
			frontUserOnline.setNormalNewsMessage(normalNewsMessage);
			
		}
	}*/
	
	
	/**
	 * disagree agreement
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jun 11, 2013
	 */
	/*public String disagreeConfirm(){
		try {
			LOG.info("Disagree confirm");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				String customerId = frontUserOnline.getUserId();
				
				// Get to update confirm for all current active agreement (bug TRSP-445) 
				List<CustomerServicesInfo> serviceInfos = frontUserOnline.getListCustomerServiceInfo();
				Integer fxSubgroupId = null;
				Integer boSubgroupId = null;
				Integer socialSubgroupId = null;
				for (CustomerServicesInfo customerServicesInfo : serviceInfos) {
					if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.FX)){
						fxSubgroupId = customerServicesInfo.getSubGroupId();
					}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.BO)){
						boSubgroupId = customerServicesInfo.getSubGroupId();
					}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE)){
						socialSubgroupId = customerServicesInfo.getSubGroupId();
					}
				}
				List<AmsMessage> agreements = socialManager.getAllAgreementInfo(frontUserOnline.getUserId(),fxSubgroupId,boSubgroupId,socialSubgroupId);
				int result = socialManager.disagreeConfirm(customerId, agreements);
				if(result == 1){
					LOG.info("Agree confirm success");
				}else{
					LOG.info("Agree confirm failed");
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ERROR;
		}
		
		return SUCCESS;
	}*/
	
	public String firstLogin(){
		try{
			if(model.getUsernameFirstLogin()!= null) {
				LOG.info("[First Login] Username is : "+model.getUsernameFirstLogin());
				ScCustomer sc = socialManager.findUserByUserName(model.getUsernameFirstLogin());
				if(sc == null) {
					//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - Start
//					sc = socialManager.findUserByCustomerId(model.getCustomerId());
					String customerId = FrontUserOnlineContext.getUserId();
					sc = socialManager.findUserByCustomerId(customerId);
					sc.setUserName(model.getUsernameFirstLogin());
					sc.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - End
					
					LOG.info("[Start]Save username to SC_Customer table ");
					if(!socialManager.save(sc)){
						model.setInputStream(new StringBufferInputStream("error"));
						LOG.info("[End]Save username to SC_Customer table is FAIL ");
					}else {
						LOG.info("[End]Save username to SC_Customer table is SUCCESS ");
						FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
						if (frontUserDetails != null) {
							FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
							frontUserOnline.setUserName(model.getUsernameFirstLogin());
							this.setSubGroupCd(frontUserOnline);
							this.model.setUserOnline(frontUserOnline);
						}
						LOG.info("[Start]Save username to memcached ");
						//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - Start
//						CustomerInfo customerInfo = SocialMemcached.getInstance().getCustomerInfo(model.getCustomerId());
						CustomerInfo customerInfo = SocialMemcached.getInstance().getCustomerInfo(customerId);
						//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - End
						customerInfo.setUsername(model.getUsernameFirstLogin());
						SocialMemcached.getInstance().saveCustomerInfo(customerInfo);
						LOG.info("[End]Save username to memcached ");
					}
					model.setInputStream(new StringBufferInputStream("notExisted"));
					//set first flag log in
					//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - Start
//					boolean rs = socialManager.updateFirsLoginFlag(model.getCustomerId());
					boolean rs = socialManager.updateFirsLoginFlag(customerId);
					//[TRS-chien.nghe.xuan]Feb 10, 2015M - TRSPT-3879 - End
					if(rs==false){
						LOG.error("ERROR CAN NOT UPDATE FIRST_LOGIN_FLG==1 for AMS_CUSTOMER_SURVEY");
					}
				}else {
					model.setInputStream(new StringBufferInputStream("existed"));
					LOG.warn("[First Login] USERNAME is Existed on SC_CUSTOMER");
				}
				
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		
		return SUCCESS;
	}
	private void setSubGroupCd(FrontUserOnline frontUserOnline) {
		// set subgroupcd; TODO: refactor
		for (ScCustomerServiceInfo service : frontUserOnline.getListScCustomerServiceInfo()) {
			
			if (service.getAccountId() != null && service.getAccountId().equals(model.getAccountId())) {
				LOG.info("ServiceType: " + service.getServiceType());
				LOG.info("Subgroup Cd: " + service.getSubGroupCd());
				frontUserOnline.setSubGroupCd(service.getSubGroupCd());
				break;
			}
		}
	}

//	public void getUserInformation() {
//		initialUserInformation();
//	}
//	
//	public void initialUserInformation() {
//		CustomerInfo customerInfo = socialManager.getCustomerFromId(model.getId(), FrontUserOnlineContext.getUserId());
//		model.setCustomerInfo(customerInfo);		
//		model.setMode(getMode());
//	}	
	
	public String followProcessing(){
		if(model.getLeaderBoardInfo() == null) model.setLeaderBoardInfo(new LeaderBoardInfo());		
		try{
			String loginCustomerId = getCurrentCustomerId();	
			String selectedCustomerId = model.getSelectedCustomerId();			
			if("Unfollow".equals(dispatch)){
				 socialManager.stopFollow(loginCustomerId, selectedCustomerId);          
	             model.getLeaderBoardInfo().setAjaxMsg(getText("MSG_SC_043"));             
	             model.getLeaderBoardInfo().setAjaxSuccess(true);             
			}		
			if("Follow".equals(dispatch)){
				socialManager.followCustomer(loginCustomerId, selectedCustomerId);
	            model.getLeaderBoardInfo().setAjaxMsg(getText("MSG_SC_041"));
	            model.getLeaderBoardInfo().setAjaxSuccess(true);            
			}		
		}
		catch (InvalidActionException e){
//			e.printStackTrace();
			LOG.warn(e.getMessage(), e);
			model.getLeaderBoardInfo().setAjaxMsg(getText(e.getMessageCode(), e.getArgs()));
		}
		catch(Exception e){
			LOG.error(e.getMessage(), e);
			if("unfollow".equals(dispatch)){
                model.getLeaderBoardInfo().setAjaxMsg(getText("MSG_SC_044"));
            } else if("follow".equals(dispatch)){
                model.getLeaderBoardInfo().setAjaxMsg(getText("MSG_SC_042"));
            }
		}
		
		return SUCCESS;
	}
	
	public String marketWatch() {
		try{
			model.setBrandMode("MarketWatch");
			initMarketWatchSymbol();
			String serverTime = getMarketWatchServerTime();
			model.setServerTime(serverTime);
			String spreadBid = SystemPropertyConfig.getInstance().getConfigProperties("spread.bid");
			String spreadAsk = SystemPropertyConfig.getInstance().getConfigProperties("spread.ask");
			model.setSpreadBid(spreadBid == null ? "0" : spreadBid);
			model.setSpreadAsk(spreadAsk == null ? "0" : spreadAsk);
			if(model.getMode() == 1){			
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					this.setSubGroupCd(frontUserOnline);
					this.model.setUserOnline(frontUserOnline);
					model.setDefaultLanguage(frontUserOnline.getLanguage());
					//HungPV: check confirm flag then show popup on screen markatWatch
					LOG.debug("oldTicketId: " + ticketIdCheck + " newTicketId: " + frontUserOnline.getTicketId());
					if(ticketIdCheck == null || !ticketIdCheck.equals(frontUserOnline.getTicketId())){				
						List<CustomerServicesInfo> serviceInfos = frontUserOnline.getListCustomerServiceInfo();
						Integer fxSubgroupId = null;
						Integer boSubgroupId = null;
						Integer socialSubgroupId = null;
						for (CustomerServicesInfo customerServicesInfo : serviceInfos) {
							if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.FX)){
								fxSubgroupId = customerServicesInfo.getSubGroupId();
							}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.BO)){
								boSubgroupId = customerServicesInfo.getSubGroupId();
							}else if(customerServicesInfo.getServiceType().equals(ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE)){
								socialSubgroupId = customerServicesInfo.getSubGroupId();
							}
						}
						/*List<AmsMessage> agreements = socialManager.getAgreementInfo(frontUserOnline.getUserId(),fxSubgroupId,boSubgroupId,socialSubgroupId);
						if(agreements != null && agreements.size() > 0){
							model.setAmsMessages(agreements);
							model.setChangeAgreement(1);
						}else{
							model.setChangeAgreement(0);
						}*/
					}
				}
				else
				{
					model.setChangeAgreement(0);
				}
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	private void initMarketWatchSymbol() {
        String SORT_BY_ALPHABET = "1";
        String SORT_BY_PRIORITY = "2";
        String SORT_BY_CUSTOM = "3";
        String marketWatchOriginalSymbols = "AUDJPY,AUDUSD,CADJPY,CHFJPY,EURAUD,EURCHF,EURGBP,EURJPY,EURUSD,GBPAUD,GBPCHF,GBPJPY,GBPUSD,NZDJPY,NZDUSD,USDCHF,USDJPY,ZARJPY";
        String marketWatchSymbols = "AUDJPYs,AUDUSDs,CADJPYs,CHFJPYs,EURAUDs,EURCHFs,EURGBPs,EURJPYs,EURUSDs,GBPAUDs,GBPCHFs,GBPJPYs,GBPUSDs,NZDJPYs,NZDUSDs,USDCHFs,USDJPYs,ZARJPYs";
        String marketWatchOriginalSymbolDigits = "3,5,3,3,5,5,5,3,5,5,5,3,5,3,5,5,3,3";
        String marketWatchOriginalSymbolVolumeBuy =  ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
        String marketWatchOriginalSymbolVolumeSell = ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
        try{
            List<ScSymbolMarketWatchInfo> data = DataProvider.getScSymbolMarketWatchInfo(StringUtil.isEmpty(model.getSortSymbolBy()) ? Integer.parseInt(SORT_BY_CUSTOM) : SORT_BY_PRIORITY.equals(model.getSortSymbolBy()) ? Integer.parseInt(SORT_BY_PRIORITY) : Integer.parseInt(SORT_BY_ALPHABET));
            if(data != null && data.size() > 0){
                StringBuilder originalSymbols = new StringBuilder("");
                StringBuilder symbols = new StringBuilder("");
                StringBuilder originalSymbolDigits = new StringBuilder("");
                StringBuilder originalSymbolVolumeBuy = new StringBuilder("");
                StringBuilder originalSymbolVolumeSell = new StringBuilder("");
                for(ScSymbolMarketWatchInfo info : data){
                    originalSymbols.append(",").append(info.getSymbolCd());
                    symbols.append(",").append(info.getSymbolCd());
                    symbols.append("s");
                    originalSymbolDigits.append(",").append(info.getDigitDecimal());
                    originalSymbolVolumeBuy.append(";").append(formatNumber(info.getVolumeBuy()));
                    originalSymbolVolumeSell.append(";").append(formatNumber(info.getVolumeSell()));
                }
                marketWatchOriginalSymbols = originalSymbols.toString().substring(1);
                System.out.println("##############"+symbols);
                System.out.println("##############"+symbols.toString().substring(1));
                marketWatchSymbols = symbols.toString().substring(1);
                marketWatchOriginalSymbolDigits = originalSymbolDigits.toString().substring(1);
                marketWatchOriginalSymbolVolumeBuy = originalSymbolVolumeBuy.toString().substring(1);
                marketWatchOriginalSymbolVolumeSell = originalSymbolVolumeSell.toString().substring(1);
            }
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        }

        model.setMarketWatchOriginalSymbols(marketWatchOriginalSymbols);

        System.out.println("##############"+marketWatchSymbols);
        model.setMarketWatchSymbols(marketWatchSymbols);
        model.setMarketWatchOriginalSymbolDigits(marketWatchOriginalSymbolDigits);
        model.setMarketWatchOriginalSymbolVolumeBuy(marketWatchOriginalSymbolVolumeBuy);
        model.setMarketWatchOriginalSymbolVolumeSell(marketWatchOriginalSymbolVolumeSell);
    }

    public String formatNumber(BigDecimal number) {
        String result = "";
        try{
        	if(number != null) {
                String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
                DecimalFormat formatter = new DecimalFormat(pattern);
                result = formatter.format(number);
            }
        } catch (Exception ex){
        	LOG.error(ex.getMessage(), ex);
        }
        return result;
    }
	/*
	 * To show Information : 
	 * 	+ get Signal Provider Account for all mode
	 *  + get Copy Trade Account for Owner Mode
	 * We used parameter q for user 
	 */
	public String chatBoard(){
		try{
			this.setUserInfo();
			this.model.setBrandMode("ChatBoard");
			// check signal ON/OFF
			if(!StringUtil.isEmpty(model.getAccountId())){
				Integer signal = socialManager.isEnabledFeedBoard(model.getAccountId(), model.getBrokerCd());
				if(signal.equals(IConstants.ACTIVE_FLG.ACTIVE)){
					if(model.getMode().equals(IConstants.SOCIAL_MODES.OWNER_MODE)){
						FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
						if (frontUserDetails != null) {
							FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
							this.setSubGroupCd(frontUserOnline);
							this.model.setUserOnline(frontUserOnline);
							model.setDefaultLanguage(frontUserOnline.getLanguage());
						}
					}
					model.setEnableFeedBoard(1);
				} else {
					model.setEnableFeedBoard(0);
				}
			} else {
				model.setEnableFeedBoard(0);
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		
//		if(model.getMode() == 2){
//			if(!StringUtil.isEmpty(model.getId())){
//				model.setEnableFeedBoard(socialManager.isEnabledFeedBoard(model.getId()));
//			}else{
//				model.setEnableFeedBoard(0);
//			}
//		}else{
//			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//			if (frontUserDetails != null) {
//				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
//				this.setSubGroupCd(frontUserOnline);
//				this.model.setUserOnline(frontUserOnline);
//				model.setDefaultLanguage(frontUserOnline.getLanguage());
//			}
//			model.setEnableFeedBoard(1);
//		}
		return SUCCESS;
	}
	
	private void setUserInfo () {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			this.setSubGroupCd(frontUserOnline);
			this.model.setUserOnline(frontUserOnline);
		}
	}
	
//	public String listCopier() {
//		if(model.isOwner()){
//			model.setMode(0);
//		}else{
//			model.setMode(1);
//		}
//		return SUCCESS;
//	}
	
	public String tradingActivity(){
		try{
			model.setBrandMode("TradingActivity");
			this.getListAllSymbol();
			model.setServerTime(getServerTime());
			if( model.getMode() == 1){
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					this.setSubGroupCd(frontUserOnline);
					this.model.setUserOnline(frontUserOnline);
					model.setDefaultLanguage(frontUserOnline.getLanguage());
				}
			}
			String accountId = model.getAccountId();
			String brokerCd = model.getBrokerCd();
			String customerId = model.getId();
			model.setIsSignalProvider(IConstants.ACTIVE_FLG.INACTIVE);
			if( !StringUtil.isEmpty(accountId) && !StringUtil.isEmpty(brokerCd) && !StringUtil.isEmpty(customerId)){
				ScCustomerService sc = socialManager.getCustomerServiceInfo(accountId, customerId, brokerCd);
					/*
					 * Account_type == 1 || Account_type == 3 -> Copy trade account
					 */
				if(sc != null){
					model.setTypeView(sc.getAccountType());
					model.setIsSignalProvider(sc.getActiveFlg());
				}
			}else{
				model.setTypeView(0);
			}		
			this.setUserInfo();	
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	

	public String followCopy(){
		return SUCCESS;
	}

	public String performanceTrader(){
		try{
			model.setBrandMode("Performance");
			getListAllSymbol();
			if( model.getMode() == 1){
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					setSubGroupCd(frontUserOnline);
					model.setUserOnline(frontUserOnline);
					model.setDefaultLanguage(frontUserOnline.getLanguage());
				}
			}
			this.setUserInfo();
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	/**
	 * This is used to get account Information type "Copy Trade"
	 * @return
	 */
	public String getCopyTradeInformation(){
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		model.setAccountInformation(socialManager.getCopyTradeAccountInformation(frontUserOnline.getMt4Id()));
		return SUCCESS;
	}
	/**
	 * This is used to get account Information type "Copy Trade"
	 * @return
	 */
	public String getSignalProviderInformation(){
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
		model.setAccountInformation(socialManager.getSignalProviderAccInformation(frontUserOnline.getMt4Id()));
		return SUCCESS;
	}
	
	/**
	 * This is used to get list Symbol with Account Id
	 * @return
	 */
	public String getListSymbol(){
		String accountId = model.getAccountId();
		if(!StringUtil.isEmpty(accountId)){
			model.setListSymbolSettings(socialManager.getListSymbolByAccountId(accountId));
			return SUCCESS;
		}else{
			return ERROR;
		}
		
	}
	/**
	 * This is used to calculate trading Statistic info for account Id
	 * @return
	 */
	public String getTradingStatisticInfo(){
		try{
			String accountId = model.getAccountId();
			if(!StringUtil.isEmpty(accountId)){
				List<FxSymbol> listSymbol = new ArrayList<FxSymbol>();
				/*
				 * Get List Order With Account Id
				 */
//				List<phn.com.nts.db.entity.ScOrder> listOrder = socialManager.findOrderByTradingAccount(accountId);
//				if( listOrder != null && listOrder.size() > 0){
//						for(int j = 0; j < listOrder.size(); ++ j){
//							phn.com.nts.db.entity.ScOrder orderTemp = listOrder.get(j);
//							int index = this.getFxSymbolBySymbolCd(orderTemp.getSymbolCd(), listSymbol);
//							FxSymbol fx = null;
//							if( index == -1){
//								fx = new FxSymbol();
//								if(orderTemp.getOrderSide() == -1){
//									fx.setUnitLotDl(0L);
//									fx.setUnitLot(1L);
//								}else{
//									fx.setUnitLot(0L);
//									fx.setUnitLotDl(1L);
//								}
//								fx.setSymbolCd(orderTemp.getSymbolCd());
//								listSymbol.add(fx);
//							}else{
//								if(orderTemp.getOrderSide() == -1){
//									Long sell =  listSymbol.get(index).getUnitLot() + 1;
//									listSymbol.get(index).setUnitLot(sell);
//								}else{
//									Long buy = listSymbol.get(index).getUnitLotDl() + 1;
//									listSymbol.get(index).setUnitLotDl(buy);
//								}
//								
//							}
//					}
//				}
//				if( listSymbol != null && listSymbol.size() > 0){
//					int length = listSymbol.size();
//					for(int j = 0 ;j < length; ++ j){
//						String symbol = listSymbol.get(j).getSymbolCd();
//						String orginalSymbol = allSymbol.get(symbol).getOriginalSymbolCd();
//						if(!StringUtil.isEmpty(orginalSymbol)){
//							listSymbol.get(j).setSymbolCd(orginalSymbol);
//						}
//					}
//				}
				listSymbol = socialManager.getTradingStatisticInfo(accountId);
				model.setListSymbolSettings(listSymbol);
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	/**
	 * This is used to get Summary Trading Customer by ajax request ( accountId,brokerId,customerId)
	 * @return
	 */
	public String getAccountSummaryTrading(){
		try{
			String accountId = model.getAccountId();
			String brokerCd = model.getBrokerCd();
			String customerId = model.getCustomerId();
			//String fromDate = model.getFromDate();
            Integer dayDiff = model.getDayDiff();
            if( !StringUtil.isEmpty(accountId) && !StringUtil.isEmpty(brokerCd) && !StringUtil.isEmpty(customerId) && dayDiff != null){
                String businessDate = socialManager.getFrontDate();
                String fromDate = computePreviousRelativeDate(businessDate, dayDiff);
				List<ScSummaryTradingCustInfo> sc = socialManager.getAccountSummaryTrading(accountId, customerId, brokerCd,fromDate);
				if(!model.isOwner()){
					// That is used to convert to null with the private field
					if(sc != null && sc.size() > 0){
						int leng = sc.size();
						for( int i = 0 ; i < leng ; ++ i){
							sc.get(i).convertToGuessMode();
						}
					}
				}
				model.setSummaryTrading(sc);
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}

    /**
	 * This is used to load Customer Service Info get params by Ajax request
	 * @return
	 */
	public String getCustomerServiceInfo(){
		String accountId = model.getAccountId();
		String brokerCd = model.getBrokerCd();
		String customerId = model.getCustomerId();
		if( !StringUtil.isEmpty(accountId) && !StringUtil.isEmpty(brokerCd) && !StringUtil.isEmpty(customerId)){
			model.setAccountInformation(socialManager.getCustomerServiceInfo(accountId, customerId, brokerCd));
		}
		return SUCCESS;
	}
	/**
	 * This is used to load customer Service Info and summary trading info of customer  get params by Ajax request
	 * @return
	 */
	public String getAccountInformation(){
		try{
			String accountId = model.getAccountId();
			String brokerCd = model.getBrokerCd();
			String customerId = model.getCustomerId();
			//String fromDate = model.getFromDate();
            Integer dayDiff = model.getDayDiff();
			String brandMode = model.getBrandMode();
            String businessDate = socialManager.getFrontDate();
            String fromDate = computePreviousRelativeDate(businessDate, dayDiff);
			if( !StringUtil.isEmpty(accountId) && !StringUtil.isEmpty(brokerCd) && !StringUtil.isEmpty(customerId)){
				List<ScSummaryTradingCustInfo> sc = socialManager.getAccountSummaryTrading(accountId, customerId, brokerCd,fromDate);
				if(model.getMode() == 2){
					// That is used to convert to null with the private field
					if(sc != null && sc.size() > 0){
						int leng = sc.size();
						for( int i = 0 ; i < leng ; ++ i){
							sc.get(i).convertToGuessMode();
						}
					}
				} else {
	                if(sc != null && sc.size() > 0){
	                    for(ScSummaryTradingCustInfo summaryTrading : sc){
	                        summaryTrading.setBestTrade(summaryTrading.getBestTrade() == null || summaryTrading.getBestTrade().compareTo(new BigDecimal(0)) <= 0 ? new BigDecimal(0) : summaryTrading.getBestTrade().setScale(1, RoundingMode.DOWN));
	                        summaryTrading.setWorstTrade(summaryTrading.getWorstTrade() == null || summaryTrading.getWorstTrade().compareTo(new BigDecimal(0)) <= 0 ? summaryTrading.getWorstTrade().setScale(1, RoundingMode.DOWN) : new BigDecimal(0));
	                        if(summaryTrading.getTotalPipGain() != null) summaryTrading.getTotalPipGain().setScale(1, RoundingMode.DOWN);
	                        if(summaryTrading.getTotalPipLoss() != null) summaryTrading.getTotalPipLoss().setScale(1, RoundingMode.DOWN);
	                        if(summaryTrading.getTotalReturn() != null) summaryTrading.getTotalReturn().setScale(2, RoundingMode.DOWN);
	                        if(summaryTrading.getBestTradePips() != null) summaryTrading.getBestTradePips().setScale(1, RoundingMode.DOWN);
	                        if(summaryTrading.getWorstTradePips() != null) summaryTrading.getWorstTradePips().setScale(1, RoundingMode.DOWN);
	                    }
	                }
	            }
				model.setSummaryTrading(sc);
				model.setAccountInformation(socialManager.getCustomerServiceInfo(accountId, customerId, brokerCd));
				if(ITrsConstants.BRAND_MODE.PERFORMANCE.equals(brandMode)){
					FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		            FrontUserOnline frontUserOnline = null;
		            String currencyCode = null;
		            if (frontUserDetails != null) {
		                frontUserOnline = frontUserDetails.getFrontUserOnline();
		                if (frontUserOnline != null) {
		                    currencyCode = frontUserOnline.getCurrencyCode();
		                }
		            }
					Integer rounding = BigDecimal.ROUND_DOWN;
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
					if(currencyInfo != null) {
						rounding = currencyInfo.getCurrencyRound();
					}
					SummaryTradingInfo tradingInfo = socialManager.getTradingInfoByAccount(accountId, brokerCd);
					BigDecimal totalReturn = BigDecimal.ZERO;
					if(tradingInfo != null && tradingInfo.getAvgBalance().compareTo(new Double(0)) != 0){
						totalReturn = tradingInfo.getTotalPL().multiply(new BigDecimal(100)).divide(new BigDecimal(tradingInfo.getAvgBalance()), 2, rounding);
					}
					model.setTotalReturn(totalReturn);
				}
				String dateTrade = socialManager.calculateToTalTraded(customerId);
				if(!StringUtil.isEmpty(dateTrade)){
					model.setTotalTraded(dateTrade);
				}
				model.setFrontDate(businessDate);
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}

    private String computePreviousRelativeDate(String businessDate, Integer dayDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.toDate(businessDate, DateUtil.PATTERN_YYMMDD_BLANK));
        calendar.add(Calendar.DAY_OF_YEAR, -dayDiff);
        return DateUtil.toString(calendar, DateUtil.PATTERN_YYMMDD_BLANK);
    }

    public String listFollower(){
		try{		
			int currentMode=getMode();
			String customerId = "";
			if (currentMode == IConstants.SOCIAL_MODES.GUEST_MODE){
				customerId = model.getId();
			}
			else {
				customerId = getCurrentCustomerId();
			}
							
			//Get Follower number of userLogin
			/*CustomerInfo customer = socialManager.getCustomerFromId(customerId , null);
			CustomerFollowerModel customerFollowerModel = new CustomerFollowerModel();
			customerFollowerModel.setCustomerId(customerId);
			customerFollowerModel.setUsername(customer.getUsername());*/
			
			ScCustomer customerInfo = socialManager.getScCustomerFromId(customerId);
			CustomerFollowerModel customerFollowerModel = new CustomerFollowerModel();
			customerFollowerModel.setCustomerId(customerId);
			customerFollowerModel.setUsername(customerInfo.getUserName());			
//			if (customerInfo.getFollowerNo()==null){
//				customerFollowerModel.setFollowerNo(0);	
//			}
//			else{
			//[NTS1.0-le.hong.ha]Aug 8, 2013M - Start 
			// fix bug #19257
			//customerFollowerModel.setFollowerNo(socialManager.countFollower(customerId));	
			//[NTS1.0-le.hong.ha]Aug 8, 2013M - End
//			}
																				
			@SuppressWarnings("rawtypes")
			Map session = ActionContext.getContext().getSession();
			// get paging
			PagingInfo pagingInfo = model.getPagingInfo();
			if (pagingInfo == null) {
				pagingInfo = new PagingInfo();
				model.setPagingInfo(pagingInfo);
			}
			pagingInfo.setOffset(IConstants.PAGING.COPY_LIST);
			session.put("paging", pagingInfo);		
			SearchResult <CustomerInfo> followerList = socialManager.getFollowerList(customerId,pagingInfo);
			//[NTS1.0-le.hong.ha]Aug 8, 2013M - Start 
			// fix bug #19257
			//customerFollowerModel.setFollowerNo(socialManager.countFollower(customerId));
			customerFollowerModel.setFollowerNo(new Integer(followerList.getPagingInfo().getTotal()+""));	
			model.setCustomerFollowerModel(customerFollowerModel);
			//[NTS1.0-le.hong.ha]Aug 8, 2013M - End
			model.setPagingInfo(followerList.getPagingInfo());
			model.setFollowerList(followerList);		
		}
		catch (Exception e){
			LOG.error("Error occured when loading LIST FOLLOWER: "+e.getMessage(), e);
		}			
			
		return SUCCESS;
	}

	public String copierList(){
		try{			
			int currentMode=getMode();
			String customerId = "";
			if (currentMode == IConstants.SOCIAL_MODES.GUEST_MODE){
				customerId = model.getId();
			}
			else {
				customerId = getCurrentCustomerId();
			}
			
			String accountId = ""; 
			//model.getAccountId();			
			String brokerCd = "";
			//model.getBrokerCd();
			String serviceType = "";
			String accountKind = "";
			//Get Account Information		
//			accountId = model.getAccountId();
//			brokerCd = model.getBrokerCd();
			
			CopyTradeItemInfo account = model.getSelectedAccount();
			if (account!=null){
				accountId = account.getAccountId();
				brokerCd = account.getBrokerCd();
				//accountKind = mappingAccountKind(account.getAccountKind());				
				//serviceType = mappingServiceType(account.getServiceType(), brokerCd);			
			}
			else {
				accountId = model.getAccountId();
				brokerCd = model.getBrokerCd();
				//accountKind = model.getAccountKind();
				//serviceType = model.getServiceType();
			}
			model.setAccountId(accountId);
			model.setBrokerCd(brokerCd);
			//model.setAccountKind(accountKind);
			//model.setServiceType(serviceType);
			
			@SuppressWarnings("rawtypes")
			Map session = ActionContext.getContext().getSession();
			// get paging
			PagingInfo pagingInfo = model.getPagingInfo();
			if (pagingInfo == null) {
				pagingInfo = new PagingInfo();
			}
			session.put("paging", pagingInfo);	
			
			//case user do not have any account & brokerCd
			if (accountId.equals("")||brokerCd.equals("")){				
				CustomerCopierModel customerCopierModel = new CustomerCopierModel();
				//CustomerInfo customer = socialManager.getCustomerFromId(customerId , null);
				
				ScCustomer customerInfo = socialManager.getScCustomerFromId(customerId);				
				customerCopierModel.setCopierNo(0);
				customerCopierModel.setUsername(customerInfo.getUserName());
				model.setCustomerCopierModel(customerCopierModel);	
			}
			//case user have accountId and brokerCd
			else
			{
				//display customer info
				SearchResult <ScCustomerCopy> listOfCopier = socialManager.getListOfCopierByAccount(accountId, brokerCd, pagingInfo);
				Integer numberOfCopier = listOfCopier.size();
				CustomerCopierModel customerCopierModel = new CustomerCopierModel();
				//CustomerInfo customer = socialManager.getCustomerFromId(customerId , null);
				ScCustomer customerInfo = socialManager.getScCustomerFromId(customerId);	
				//[NTS1.0-le.hong.ha]Aug 8, 2013M - Start 
				// Fix bug #19257
				// Get copiers not depend on paging
				//customerCopierModel.setCopierNo(socialManager.countCopier(accountId, brokerCd));
				customerCopierModel.setCopierNo(new Integer(listOfCopier.getPagingInfo().getTotal()+""));
				//customerCopierModel.setCopierNo(numberOfCopier);
				//[NTS1.0-le.hong.ha]Aug 8, 2013M - End
				customerCopierModel.setUsername(customerInfo.getUserName());
				model.setCustomerCopierModel(customerCopierModel);			
				SearchResult <CopierCustomerModel> listAccountInfo = new SearchResult<CopierCustomerModel>();
				listAccountInfo.setPagingInfo(listOfCopier.getPagingInfo());
				for (ScCustomerCopy copier:listOfCopier){				
					listAccountInfo.add(socialManager.getCopierAccountInfo(copier));	
				}
				//set to list Account Info
				model.setPagingInfo(listAccountInfo.getPagingInfo());
				model.setListAccountInfo(listAccountInfo);
			}
			
		
		}
		catch (Exception e){
			LOG.error("Error occured when loading COPIER LIST: "+e.getMessage(), e);
		}
		return SUCCESS;
	}
	private String getServerTime(){
		String result = null;
		  Date dNow = new Date( );
	      SimpleDateFormat ft =  new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
	      result = ft.format(dNow);
		return result;
	}
	
	private String getMarketWatchServerTime(){
		String result = null;
		  Date dNow = new Date( );
	      SimpleDateFormat ft =  new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
	      result = ft.format(dNow);
		return result;
	}
	private int getFxSymbolBySymbolCd(String symbolCd,List<FxSymbol> list){
		int result = -1;
		if(list.size() == 0){
			return -1;
		}else{
			int length = list.size();
			for (int i = 0; i < length; i ++){
				if(list.get(i).getSymbolCd().equalsIgnoreCase(symbolCd)){
					result = i;
					break;
				}
			}
			return result;
		}
	}
	/**
	 * This function is used to get list order of user
	 */
	public String getListOrderByAccountId(){
		try {
			String accountId =  model.getAccountId();
			int page = model.getPageOrder();
			int index = model.getIndexPaging();
			List<ScOrderInfo> list = null;
			if( !StringUtil.isEmpty(accountId) && page > 0 && index > 0 ){
				list = socialManager.getListOrderByAccountId(accountId,page,index);
				if(list != null && list.size() > 0){
					int length = list.size();
					
					for( int i = 0; i < length; ++i){
						ScOrderInfo order = list.get(i);
						BigDecimal pips = BigDecimal.ZERO;
						if(allSymbol != null){
							FxSymbol symbol = allSymbol.get(order.getSymbolCd());
							if(symbol != null && symbol.getPipSize() != null && order.getOrderClosePrice() != null && order.getOrderOpenPrice() != null && symbol.getPipSize().floatValue() != 0){
								int rateScale = symbol.getSymbolCd().toLowerCase().indexOf(IConstants.CURRENCY_CODE.JPY.toLowerCase()) == -1 ? 5 : 3;
								BigDecimal closePrice = order.getOrderClosePrice().divide(BigDecimal.ONE, rateScale, BigDecimal.ROUND_HALF_UP);
								BigDecimal openPrice = order.getOrderOpenPrice().divide(BigDecimal.ONE, rateScale, BigDecimal.ROUND_HALF_UP);
								pips = closePrice.subtract(openPrice).divide(symbol.getPipSize(), 1, BigDecimal.ROUND_DOWN);
								if(IConstants.ORDER_SIDE.SELL == order.getOrderSide().intValue()){
									pips = pips.negate();
								}
//								pips = (order.getOrderClosePrice().floatValue() - order.getOrderOpenPrice().floatValue())/symbol.getPipSize().floatValue();
							}
						}
//						order.setTax(pips.divide(BigDecimal.ONE, 1, BigDecimal.ROUND_DOWN));
						order.setTax(pips);
					}
				}
			}
			model.setListOrder(list);
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}
		
		return SUCCESS;
	}
	private void getListAllSymbol(){
		allSymbol = socialManager.findAllFxSymbol();
	};
	public String handleDirectionUser(){
		String userName = model.getUserName();
		String result = ERROR;
		try{
			if( !StringUtil.isEmpty(userName)){
				// Fix Bug #19905
				try {
					userName = java.net.URLDecoder.decode (userName, "utf-8");
				} catch (UnsupportedEncodingException e) {
					LOG.warn("handleDirectionUser: can not decode username");
				}
				ScCustomer customer = socialManager.findUserByUserName(userName);
				if( customer != null){
					model.setCustomerId(customer.getCustomerId());
					result=  SUCCESS;
				}
			}
		} catch (Exception ex){
			LOG.error(ex.getMessage(), ex);
		}
		
		return result;
	}
	/**
	 * @return
	 */
	public String validateOrder(){
        try{
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            String customerId = model.getCurrentCustomerId();
            LOG.info("[Start] VALIDATE ORDER for user: " + customerId + " with amount=" + amount + " & orderTicket=" + orderTicket);
            if (frontUserDetails != null) {
                FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
                frontUserOnline.getUserId();
                String result = socialManager.validateOrder(amount, orderTicket, customerId);
                LOG.info("VALIDATE result: " + result);
                setValidateOrder(result);
            }
            LOG.info("[end] VALIDATE ORDER for user: " + customerId + " with amount=" + amount + " & orderTicket=" + orderTicket);
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        }
		return SUCCESS;
	}

    public String handle404() {
    	SecurityContext securityContext = (SecurityContext) this.session.get("SPRING_SECURITY_CONTEXT");
    	Authentication auth = null;
    	FrontUserDetails frontUser = null;
    	FrontUserOnline frontUserOnline = null;
    	if (securityContext != null) {
    		auth = securityContext.getAuthentication();
    	}
    	
    	if (auth != null) {
			frontUser = (FrontUserDetails) auth.getPrincipal();
		}
    	
    	if (frontUser != null) {
    		frontUserOnline = frontUser.getFrontUserOnline();
    	}
    	if (frontUserOnline == null) {
    		return NONE;
    	} else {
    		return LOGIN;
    	}
    }
	
	public ISocialManager getSocialManager() {
		return socialManager;
	}

	public void setSocialManager(ISocialManager socialManager) {
		this.socialManager = socialManager;
	}

	/**
	 * @return the allSymbol
	 */
	public static HashMap<String, FxSymbol> getAllSymbol() {
		return allSymbol;
	}

	/**
	 * @param allSymbol the allSymbol to set
	 */
	public static void setAllSymbol(HashMap<String, FxSymbol> allSymbol) {
		SocialAction.allSymbol = allSymbol;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(SocialModel model) {
		this.model = model;
	};
}
