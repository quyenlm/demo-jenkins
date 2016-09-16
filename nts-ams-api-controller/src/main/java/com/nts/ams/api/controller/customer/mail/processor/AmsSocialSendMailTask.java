package com.nts.ams.api.controller.customer.mail.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.social.SCManager;
import com.nts.ams.api.controller.common.Constant;
import com.nts.ams.api.controller.customer.bean.ScCustomerEvent;
import com.nts.ams.api.controller.customer.bean.ScHiberanteAccountEvent;
import cn.nextop.social.api.admin.proxy.glossary.ExecutionStatus;
import cn.nextop.social.api.admin.proxy.glossary.RecoverStatus;
import cn.nextop.social.api.admin.proxy.glossary.RelationRoute;
import cn.nextop.social.api.admin.proxy.glossary.Side;
import cn.nextop.social.api.admin.proxy.glossary.SignalStatus;
import cn.nextop.social.api.admin.proxy.glossary.TradeType;
import cn.nextop.social.api.admin.proxy.model.customer.Customer;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccount;
import cn.nextop.social.api.admin.proxy.model.feeding.SocialRelation;
import cn.nextop.social.api.admin.proxy.model.feeding.SocialTradePropagation;
import cn.nextop.social.api.admin.proxy.model.trading.Execution;
import phn.com.trs.util.common.ITrsConstants;
import com.nts.ams.api.controller.service.AmsApiControllerMng;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 15, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsSocialSendMailTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsSocialSendMailTask.class);
	private Object event;
	private IAccountManager accountManager;
	private IProfileManager profileManager;
	
	public AmsSocialSendMailTask(Object event) {
		this.event = event;
	}

	@Override
	public void run() {
		try {
			log.info("[start] handle AmsSocialSendMailTask, event: " + event);
			
			if(event instanceof SocialRelation)
				onRelation((SocialRelation)event);
			else if(event instanceof SocialTradePropagation)
				onTradePropagation((SocialTradePropagation)event);
			else if(event instanceof ScHiberanteAccountEvent)
				onHiberanteAccount((ScHiberanteAccountEvent) event);
			else if(event instanceof Execution)
				onExecution((Execution) event);
			else if(event instanceof ScCustomerEvent)
				onModifyCustomerConfig((ScCustomerEvent) event);
			
			log.info("[end] handle AmsSocialSendMailTask, event: " + event);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsSocialSendMailProcessor().onComplete(event);
		}
	}
	
	private void onRelation(SocialRelation releation) {
		if (releation.getRoute() == null || (!RelationRoute.SUSPEND_ACCOUNT.equals(releation.getRoute()) 
					&& !RelationRoute.CLOSE_ACCOUNT.equals(releation.getRoute())))
			return;
		
		if (Constant.NOT_SEND_MAIL.equalsIgnoreCase(AmsApiControllerMng.getConfiguration().getSendAmsOffSignalNotificationMail()) 
				&& RelationRoute.SUSPEND_ACCOUNT.equals(releation.getRoute())) {
			log.info("Not send mail AMS_OFF_SIGNAL_NOTIFICATION_JA, because: config api.sc.sendOffSignalNotificationMail = 0");
			return;
		}
		
		int copierServiceId = releation.getTarget();
		int guruAccountId = releation.getSource();
		String eventDateTime = DateUtil.toString(new Date(releation.getRelationDatetime()), DateUtil.PATTERN_SLASH_YYYYMMDD_HH_COLON_MM_SS);
	
		CustomerAccount copierAccount = SCManager.getInstance().getAccount(copierServiceId);
		if (copierAccount == null) {
			log.warn("[onRelation] NOT exist copierAccountId: " + copierServiceId + " on Social System");
			return;
		}
		
		String guruCustomerId = accountManager.getCustomerIdByCustomerService(String.valueOf(guruAccountId));
		if (guruCustomerId == null) {
			log.warn("[onRelation] NOT exist guruAccountId: " + guruAccountId + " on AMS System");
			return;
		}
		
		Customer guruCustomerInfo = SCManager.getInstance().getCustomer(Integer.parseInt(guruCustomerId)); 
		if (guruCustomerInfo == null) {
			log.warn("[onRelation] NOT exist guruCustomerId: " + guruCustomerId + " on Social System");
			return;
		}
		
		if (copierAccount.getMailConfig() == null) {
			log.warn("[onRelation]  MailConfig of copierAccountId: " + copierServiceId + " is null");
			return;
		}
		
		AmsCustomer copierCustomer = accountManager.getAmsCustomerByCustomerService(String.valueOf(copierServiceId));
		if (copierCustomer != null) {
			if (StringUtil.isEmpty(copierCustomer.getMailMain()) 
					&& StringUtil.isEmpty(copierCustomer.getMailAddtional())
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile())) {
				log.warn("[onRelation] copierServiceId: " + copierServiceId + " null email in AMS System");
				return;
			}
			CustomerInfo customerInfo = new CustomerInfo();
			String guruNickName = guruCustomerInfo.getNickName();
			customerInfo.setGuruNickName(guruNickName);
			customerInfo.setGuruCustomerId(guruCustomerId);
			customerInfo.setCustomerServiceId(String.valueOf(copierServiceId));
			customerInfo.setFullName(copierCustomer.getFullName());
			customerInfo.setEventDateTime(eventDateTime);
			customerInfo.setWlCode(copierCustomer.getWlCode());
			
			if (!copierAccount.getMailConfig().isSignalOffPcMailEnabled() && !copierAccount.getMailConfig().isSignalOffMobileMailEnabled()) {
				log.warn("Not send mail onRelation to customer: " + copierCustomer.getCustomerId() + " because isSignalOffPcMailEnabled && isSignalOffMobileMailEnabled = false");
				return;
			}
			//Prep mail info
			if (copierCustomer.getCorporationType() == 0) {
				if (!StringUtil.isEmpty(copierCustomer.getMailMain()) && copierAccount.getMailConfig().isSignalOffPcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getMailMain());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getMailAddtional()) && copierAccount.getMailConfig().isSignalOffMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getMailAddtional());
				}
			} else {  //CorporationType = 1
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailPc()) && copierAccount.getMailConfig().isSignalOffPcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getCorpPicMailPc());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile()) && copierAccount.getMailConfig().isSignalOffMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getCorpPicMailMobile());
				}
			}
			
			String mailCode="";
			if (RelationRoute.SUSPEND_ACCOUNT.equals(releation.getRoute())) {
				mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_OFF_SIGNAL_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();																																											
			} else if(RelationRoute.CLOSE_ACCOUNT.equals(releation.getRoute())) {
				mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_STOP_ACCOUNT_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();
			}
			profileManager.sendmailSocial(customerInfo, mailCode);
			
		} else
			log.warn("[onRelation]  NOT exist customerId: " + copierServiceId + " in AMS");
	}
	
	/**
	 * TODO　
	 * 
	 * @param
	 * @return
	 * @author TheLN
	 * @CrDate Mar 16, 2016
	 * @MdDate
	 */
	private void onTradePropagation(SocialTradePropagation p) {
		if (p.getPropagateResult() == null || p.getPropagateResult().isSuccessful())
			return;
		int copierServiceId = p.getAccountId();	
		int guruAccountId = p.getGuruAccountId();
		
		CustomerAccount copierAccount = SCManager.getInstance().getAccount(copierServiceId);
		if (copierAccount == null) {
			log.warn("[onTradePropagation] NOT exist copierAccountId: " + copierServiceId + " on Social System");
			return;
		}
		
		String guruCustomerId = accountManager.getCustomerIdByCustomerService(String.valueOf(guruAccountId));
		if (guruCustomerId == null) {
			log.warn("[onTradePropagation] NOT exist guruAccountId: " + guruAccountId + " on AMS System");
			return;
		}
		
		Customer guruCustomerInfo = SCManager.getInstance().getCustomer(Integer.parseInt(guruCustomerId)); 
		if (guruCustomerInfo == null) {
			log.warn("[onTradePropagation] NOT exist guruCustomerId: " + guruCustomerId + " on Social System");
			return;
		}
		
		if (copierAccount.getMailConfig() == null) {
			log.warn("[onTradePropagation]  MailConfig of copierAccountId: " + copierServiceId + " is null");
			return;
		}
		AmsCustomer copierCustomer = accountManager.getAmsCustomerByCustomerService(String.valueOf(copierServiceId));
		
		if (copierCustomer != null) {
			if (StringUtil.isEmpty(copierCustomer.getMailMain()) 
					&& StringUtil.isEmpty(copierCustomer.getMailAddtional()) 
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailPc()) 
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile())) {
				log.warn("[onTradePropagation] copierServiceId: " + copierServiceId + " null email in AMS System");
				return;
			}
			String guruNickName = guruCustomerInfo.getNickName();
			String orderDate=DateUtil.toString(p.getExecutionDatetime(), DateUtil.PATTERN_SLASH_YYYYMMDD_HH_COLON_MM_SS);
			String tradeType= "";
			if (TradeType.OPEN.equals(p.getTradeType()))
				tradeType = Constant.TRADE_TYPE_OPEN;
			else if (TradeType.SETTLE.equals(p.getTradeType()))
				tradeType = Constant.TRADE_TYPE_CLOSE;
			
			String orderType= "";
			if (Side.BUY.equals(p.getSide()))
				orderType = Constant.ORDER_TYPE_BUY;
			else if (Side.SELL.equals(p.getSide()))
				orderType = Constant.ORDER_TYPE_SELL;
			
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo.setGuruNickName(guruNickName);
			customerInfo.setGuruCustomerId(guruCustomerId);
			customerInfo.setCustomerServiceId(String.valueOf(copierServiceId));
			customerInfo.setFullName(copierCustomer.getFullName());
			customerInfo.setSymbol(p.getSymbol());
			customerInfo.setVolume(String.valueOf(p.getExecutionVolume()));
			customerInfo.setOrderType(orderType);
			customerInfo.setOrderDate(orderDate);
			customerInfo.setTradeType(tradeType);
			customerInfo.setWlCode(copierCustomer.getWlCode());
			
			if (!copierAccount.getMailConfig().isTradePcMailEnabled() && !copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
				log.warn("Not send mail onTradePropagation to customer: " + copierCustomer.getCustomerId() + " because isTradePcMailEnabled = false && isTradeMobileMailEnabled = false");
				return;
			}
	
			//Prep mail info
			if (copierCustomer.getCorporationType() == 0) {
				if (!StringUtil.isEmpty(copierCustomer.getMailMain()) && copierAccount.getMailConfig().isTradePcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getMailMain());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getMailAddtional()) && copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getMailAddtional());
				}
			} else {  //CorporationType = 1
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailPc()) && copierAccount.getMailConfig().isTradePcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getCorpPicMailPc());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile()) && copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getCorpPicMailMobile());
				}
			}
			
			String mailCode="";
			if (SignalStatus.EXPIRED.equals(p.getSignalStatus())) {
				mailCode=ITrsConstants.MAIL_TEMPLATE.AMS_GURU_SIGNAL_DELAY_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();
			}
			else if (SignalStatus.POSTPONED.equals(p.getSignalStatus()) || SignalStatus.RECOVERED.equals(p.getSignalStatus()) 
					&& RecoverStatus.ACCEPTED.equals(p.getRecoverStatus())) {
				mailCode=ITrsConstants.MAIL_TEMPLATE.AMS_ORDER_FAIL_LOSS_SIGNAL + "_" + copierCustomer.getDisplayLanguage();
			}
			profileManager.sendmailSocial(customerInfo, mailCode);
		}
		else
			log.warn("[onTradePropagation]  NOT exist customerId: " + copierServiceId + " in AMS");
	}

	private void onHiberanteAccount(ScHiberanteAccountEvent h) {
		int copierServiceId = h.getAccountId();	 
		int guruAccountId = h.getGuruAccountId();
	
		String guruCustomerId = accountManager.getCustomerIdByCustomerService(String.valueOf(guruAccountId));
		if (guruCustomerId == null) {
			log.warn("[onHiberanteAccount] NOT exist guruAccountId: " + guruAccountId + " on AMS System");
			return;
		}
		
		Customer guruCustomerInfo = SCManager.getInstance().getCustomer(Integer.parseInt(guruCustomerId)); 
		if (guruCustomerInfo == null) {
			log.warn("[onHiberanteAccount] NOT exist guruCustomerId: " + guruCustomerId + " on Social System");
			return;
		}
		
		AmsCustomer copierCustomer = accountManager.getAmsCustomerByCustomerService(String.valueOf(copierServiceId));
		if (copierCustomer != null) {
			if (StringUtil.isEmpty(copierCustomer.getMailMain()) && StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())) {
				log.warn("[onHiberanteAccount] copierServiceId: " + copierServiceId + " null email in AMS System");
				return;
			}
			
			String guruNickName = guruCustomerInfo.getNickName();
			String lastTradeTime = DateUtil.toString(new Date(h.getLastTradeTime()), DateUtil.PATTERN_SLASH_YYYYMMDD_HH_COLON_MM_SS);
			
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo.setGuruNickName(guruNickName);
			customerInfo.setGuruCustomerId(guruCustomerId);
			customerInfo.setCustomerServiceId(String.valueOf(copierServiceId));
			customerInfo.setFullName(copierCustomer.getFullName());
			customerInfo.setLastTradeTime(lastTradeTime);
			customerInfo.setWlCode(copierCustomer.getWlCode());
			
			//Prep mail info
			if (copierCustomer.getCorporationType() == 0  &&  !StringUtil.isEmpty(copierCustomer.getMailMain())) {
				customerInfo.setMailMain(copierCustomer.getMailMain());
			} else if (copierCustomer.getCorporationType() == 1 && !StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())) {  
				customerInfo.setMailMain(copierCustomer.getCorpPicMailPc());	
			}
			
			String mailCode=ITrsConstants.MAIL_TEMPLATE.AMS_NOT_TRADE_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();
			profileManager.sendmailSocial(customerInfo, mailCode);
		}
		else
			log.warn("[onHiberanteAccount]  NOT exist customerserviceId: " + copierServiceId + " in AMS system");
	}
	
	private void onExecution(Execution execution){
		int copierServiceId = execution.getAccountId(); 
		int guruAccountId = execution.getGuruAccountId();
		
		CustomerAccount copierAccount = SCManager.getInstance().getAccount(copierServiceId);
		if (copierAccount == null) {
			log.warn("[onExecution] NOT exist copierAccountId: " + copierServiceId + " on Social System");
			return;
		}
		
		String guruCustomerId = accountManager.getCustomerIdByCustomerService(String.valueOf(guruAccountId));
		if (guruCustomerId == null) {
			log.warn("[onExecution] NOT exist guruAccountId: " + guruAccountId + " on AMS System");
			return;
		}
		
		Customer guruCustomerInfo = SCManager.getInstance().getCustomer(Integer.parseInt(guruCustomerId)); 
		if (guruCustomerInfo == null) {
			log.warn("[onExecution] NOT exist guruCustomerId: " + guruCustomerId + " on Social System");
			return;
		}
		
		if (copierAccount.getMailConfig() == null) {
			log.warn("[onExecution]  MailConfig of copierAccountId: " + copierServiceId + " is null");
			return;
		}
		
		AmsCustomer copierCustomer = accountManager.getAmsCustomerByCustomerService(String.valueOf(copierServiceId));
		if (copierCustomer != null) {
			if (StringUtil.isEmpty(copierCustomer.getMailMain()) 
					&& StringUtil.isEmpty(copierCustomer.getMailAddtional())
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())
					&& StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile())) {
				log.warn("[onExecution] copierServiceId: " + copierServiceId + " null email in AMS System");
				return;
			}
			String guruNickName = guruCustomerInfo.getNickName();
			String orderDate = DateUtil.toString(execution.getExecutionDatetime(), DateUtil.PATTERN_SLASH_YYYYMMDD_HH_COLON_MM_SS);
			
			String tradeType= "";
			if (TradeType.OPEN.equals(execution.getTradeType()))
				tradeType = Constant.TRADE_TYPE_OPEN;
			else if (TradeType.SETTLE.equals(execution.getTradeType()))
				tradeType =  Constant.TRADE_TYPE_CLOSE;
			String orderType= "";
			if (Side.BUY.equals(execution.getSide()))
				orderType = Constant.ORDER_TYPE_BUY;
			else if (Side.SELL.equals(execution.getSide()))
				orderType = Constant.ORDER_TYPE_SELL;
			
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo.setGuruNickName(guruNickName);
			customerInfo.setGuruCustomerId(guruCustomerId);
			customerInfo.setCustomerServiceId(String.valueOf(copierServiceId));
			customerInfo.setFullName(copierCustomer.getFullName());
			customerInfo.setSymbol(execution.getSymbol());
			customerInfo.setVolume(String.valueOf(execution.getExecutionVolume()));
			customerInfo.setOrderType(orderType);
			customerInfo.setOrderDate(orderDate);
			customerInfo.setTradeType(tradeType);
			customerInfo.setWlCode(copierCustomer.getWlCode());
			
			if (!copierAccount.getMailConfig().isTradePcMailEnabled() && !copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
				log.warn("Not send mail onExecution to customer: " + copierCustomer.getCustomerId() + " because isTradePcMailEnabled = false && isTradeMobileMailEnabled = false");
				return;
			}
			//Prep mail info
			if (copierCustomer.getCorporationType() == 0) {
				if (!StringUtil.isEmpty(copierCustomer.getMailMain()) && copierAccount.getMailConfig().isTradePcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getMailMain());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getMailAddtional()) && copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getMailAddtional());
				}
			} else {  //CorporationType = 1
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailPc()) && copierAccount.getMailConfig().isTradePcMailEnabled()) {
					customerInfo.setMailMain(copierCustomer.getCorpPicMailPc());
				}
				
				if (!StringUtil.isEmpty(copierCustomer.getCorpPicMailMobile()) && copierAccount.getMailConfig().isTradeMobileMailEnabled()) {
					customerInfo.setMailMobile(copierCustomer.getCorpPicMailMobile());
				}
			}
		
			String mailCode= "";
			if (ExecutionStatus.EXECUTED.equals(execution.getStatus())) {
				mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_ORDER_SUCCESS_NOTIFY + "_" + copierCustomer.getDisplayLanguage();
				customerInfo.setOrderId(String.valueOf(execution.getOrderId()));
				customerInfo.setExecutionPrice(String.valueOf(execution.getExecutionPrice()));
			}
			else if (ExecutionStatus.FAILURE.equals(execution.getStatus()) && RecoverStatus.ACCEPTED.equals(execution.getRecoverStatus())) {
				mailCode=ITrsConstants.MAIL_TEMPLATE.AMS_ORDER_FAIL_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();
			}
			profileManager.sendmailSocial(customerInfo, mailCode);
		}
		else
			log.warn("[onExecution]  NOT exist customerId: " + copierServiceId + " in AMS");
	}

	private void onModifyCustomerConfig(ScCustomerEvent customerEvent) {
		List<Integer> copierIds = new ArrayList<Integer>(customerEvent.getCopierIds());
		int guruCustomerId = customerEvent.getCustomerConfig().getCustomerId();
	
		Customer guruCustomerInfo = SCManager.getInstance().getCustomer(guruCustomerId);
		
		if (guruCustomerInfo == null) {
			log.warn("[onModifyCustomerConfig] NOT exist guruCustomerId: " + guruCustomerId + " on SC system");
			return;
		}
		
		List<CustomerAccount> listCopierAccount = SCManager.getInstance().getListAccount(copierIds);
		if (listCopierAccount == null) {
			log.warn("[onModifyCustomerConfig] NOT exist copierIds: " + copierIds + " on SC system");
			return;
		}
		
		for (CustomerAccount copierAccount : listCopierAccount) {
			AmsCustomer copierCustomer = accountManager.getAmsCustomerByCustomerService(String.valueOf(copierAccount.getAccountId()));
			
			if (copierCustomer != null) {
				if (StringUtil.isEmpty(copierCustomer.getMailMain()) && StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())) {
					log.warn("[onModifyCustomerConfig] copierServiceId: " + copierAccount.getAccountId() + " null email in AMS System");
					continue;
				}
				CustomerInfo customerInfo = new CustomerInfo();
				customerInfo.setGuruNickName(guruCustomerInfo.getNickName());
				customerInfo.setFullName(copierCustomer.getFullName());
				
				String currentTime = DateUtil.toString(new Date(), DateUtil.PATTERN_MM_DD);
				String eventDateTime=currentTime.substring(0, 2) + Constant.CURRENT_TIME_MONTH + currentTime.substring(2,4) + Constant.CURRENT_TIME_DAY;
				customerInfo.setEventDateTime(eventDateTime);
				
				customerInfo.setWlCode(copierCustomer.getWlCode());
				
				//Prep mail info
				if (copierCustomer.getCorporationType() == 0 && !StringUtil.isEmpty(copierCustomer.getMailMain())) {
					customerInfo.setMailMain(copierCustomer.getMailMain());
				} else if (copierCustomer.getCorporationType() == 1 && !StringUtil.isEmpty(copierCustomer.getCorpPicMailPc())) {
					customerInfo.setMailMain(copierCustomer.getCorpPicMailPc());
				}
				
				String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_GURU_CUSTOMER_CONFIG_NOTIFICATION + "_" + copierCustomer.getDisplayLanguage();
				profileManager.sendmailSocial(customerInfo, mailCode);
			}
			else
				log.warn("[onModifyCustomerConfig] NOT exist copierServiceId: " + copierAccount.getAccountId() + " in AMS system");
			
		}
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
}