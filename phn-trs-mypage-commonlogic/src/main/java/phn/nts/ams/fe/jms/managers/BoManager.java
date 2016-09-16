package phn.nts.ams.fe.jms.managers;

import java.math.BigDecimal;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.messages.AmsFrontAdminMessageSubcriber;
import phn.nts.ams.fe.domain.UpdateBoBalanceInfo;
import phn.nts.ams.fe.jms.IJmsSender;

import com.phn.bo.admin.message.AdminAccountDetailsUpdate;
import com.phn.bo.admin.message.AdminBalanceInfo;
import com.phn.bo.admin.message.AdminBalanceUpdate;
import com.phn.bo.exchange.bean.AccountInfo;

public class BoManager {
	
	private IJmsSender jmsBoSender;
	private static Logit LOG = Logit.getInstance(BoManager.class);
	
	public BigDecimal getBalance(String customerServiceId){
		
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setCustomerServiceId(customerServiceId);
		String sequenceId = Utilities.generateRandomPassword(32);
		
		AdminBalanceInfo info = new AdminBalanceInfo(ITrsConstants.JMS_CONSTANTS.TARGET, AdminBalanceInfo.COMMAND_GET_ACCOUNT_INFO);
		info.setM_Id(sequenceId);
		info.setBalanceInfo(accountInfo);
		
		LOG.info("[start]Send BO get balance request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
		jmsBoSender.sendQueue(ITrsConstants.ACTIVEMQ.QUEUE_BO_ACCOUNT_REQUEST, info, false);
		LOG.info("[end]Send BO get balance request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
		
		AdminBalanceInfo response = null;
		AccountInfo result = null;
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				response = (AdminBalanceInfo) obj;
				result = response.getBalanceInfo();
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		if(result != null){
			LOG.info("Received BO balance info - sequenceId: "+sequenceId + ", customerServiceId: "+customerServiceId);
			LOG.info(writeLogBoBalanceInfo(response));
			return result.getCashBalance();
		}else{
			LOG.warn("Do not receive BO balance info - sequenceId: "+sequenceId+",  customerServiceId: "+customerServiceId);
			return BigDecimal.ZERO;
		}
	}
	
	public UpdateBoBalanceInfo deposit(String customerServiceId, BigDecimal amount, BigDecimal convertRate, String casflowSourceId,Integer casflowSourceType,Integer casflowType){
		return updateBalance(customerServiceId, amount, convertRate, casflowSourceId, casflowSourceType, casflowType);
	}
	
	public UpdateBoBalanceInfo withdraw(String customerServiceId, BigDecimal amount, BigDecimal convertRate, String casflowSourceId,Integer casflowSourceType,Integer casflowType){
		return updateBalance(customerServiceId, amount.negate(), convertRate, casflowSourceId, casflowSourceType, casflowType);
	}
	
	private UpdateBoBalanceInfo updateBalance(String customerServiceId, BigDecimal amount, BigDecimal convertRate, String casflowSourceId,Integer casflowSourceType,Integer casflowType){
		
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setCustomerServiceId(customerServiceId);
		accountInfo.setAmount(amount);
		accountInfo.setConvertRate(convertRate);
		
		AdminBalanceUpdate updateInfo = new AdminBalanceUpdate(ITrsConstants.JMS_CONSTANTS.TARGET, AdminBalanceUpdate.COMMAND_UPDATE_ACCOUNT_INFO);
		String sequenceId = Utilities.generateRandomPassword(32);
		updateInfo.setM_Id(sequenceId);
		updateInfo.setBalanceInfo(accountInfo);
		updateInfo.setCashFlowSourceId(casflowSourceId);
		updateInfo.setCashFlowSourceType(casflowSourceType);
		updateInfo.setCashFlowType(casflowType);
		
		LOG.info("[start]Send BO balance update request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId+", amount: "+amount +", convertRate: "+convertRate+ ", casflowsourceid: " + casflowSourceId + ", casflowSourceType: " + casflowSourceType + ", casflowType: " + casflowType);
		jmsBoSender.sendQueue(ITrsConstants.ACTIVEMQ.QUEUE_BO_ACCOUNT_REQUEST, updateInfo, false);
		LOG.info("[end]Send BO balance update request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId+", amount: "+amount + ", convertRate: "+convertRate+ ", casflowsourceid: " + casflowSourceId + ", casflowSourceType: " + casflowSourceType + ", casflowType: " + casflowType);
		
		AdminBalanceUpdate response = null;
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				response = (AdminBalanceUpdate) obj;
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		if(response != null){
			LOG.info("Received response for BO balance info update request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
			LOG.info(writeLogBoBalanceInfo(response));
			return new UpdateBoBalanceInfo(response.getResult(), response.getBalanceInfo().getCashBalance());
		}else{
			LOG.warn("Do not receive response for BO balance info update request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
			return new UpdateBoBalanceInfo(IConstants.COMMON_RESULT.NO_RESPONSE, BigDecimal.ZERO);
		}
	}
	
	private String writeLogBoBalanceInfo(AdminBalanceInfo response) {
		StringBuffer logInfo = new StringBuffer();
		logInfo.append("m_id : "+response.getM_Id()).append("\n");
		logInfo.append("result : "+response.getResult()).append("\n");
		AccountInfo accountInfo = response.getBalanceInfo();
		if(accountInfo == null){
			logInfo.append("account info: null").append("\n");
		}else{
			logInfo.append("currency code : "+accountInfo.getCurrencyCode()).append("\n");
			logInfo.append("customer id : "+ accountInfo.getCustomerId()).append("\n");
			logInfo.append("customer service id : "+accountInfo.getCustomerServiceId()).append("\n");
			logInfo.append("full name : "+accountInfo.getFullName()).append("\n");
			logInfo.append("leverage : "+accountInfo.getLeverage()).append("\n");
			logInfo.append("subgroup code : "+accountInfo.getSubGroupCode()).append("\n");
			logInfo.append("amount : "+accountInfo.getAmount()).append("\n");
			logInfo.append("cash balance : "+accountInfo.getCashBalance()).append("\n");
			logInfo.append("account info : "+accountInfo.getEquity()).append("\n");
		}
		return logInfo.toString();
	}

	private String writeLogBoBalanceInfo(AdminBalanceUpdate response) {
		StringBuffer logInfo = new StringBuffer();
		logInfo.append("m_id : "+response.getM_Id()).append("\n");
		logInfo.append("result : "+response.getResult()).append("\n");
		AccountInfo accountInfo = response.getBalanceInfo();
		if(accountInfo == null){
			logInfo.append("account info: null").append("\n");
		}else{
			logInfo.append("currency code : "+accountInfo.getCurrencyCode()).append("\n");
			logInfo.append("customer id : "+ accountInfo.getCustomerId()).append("\n");
			logInfo.append("customer service id : "+accountInfo.getCustomerServiceId()).append("\n");
			logInfo.append("full name : "+accountInfo.getFullName()).append("\n");
			logInfo.append("leverage : "+accountInfo.getLeverage()).append("\n");
			logInfo.append("subgroup code : "+accountInfo.getSubGroupCode()).append("\n");
			logInfo.append("amount : "+accountInfo.getAmount()).append("\n");
			logInfo.append("cash balance : "+accountInfo.getCashBalance()).append("\n");
			logInfo.append("account info : "+accountInfo.getEquity()).append("\n");
		}
		return logInfo.toString();
	}
	
	public AdminAccountDetailsUpdate updateBoDetail(AccountInfo accountInfo) throws Exception {
		LOG.info("[start] updateBoDetail, AccountInfo: " + accountInfo);
		
		AdminAccountDetailsUpdate updateInfo = new AdminAccountDetailsUpdate(ITrsConstants.JMS_CONSTANTS.TARGET, AdminBalanceUpdate.COMMAND_UPDATE_ACCOUNT_INFO);
		String sequenceID = Utilities.generateRandomPassword(32);
		updateInfo.setM_Id(sequenceID);
		updateInfo.setAccountDetails(accountInfo);
		
		LOG.info("Send UpdateBoDetail request - sequenceID: " + sequenceID + ", customerServiceId: " + accountInfo.getCustomerServiceId());
		jmsBoSender.sendQueue(ITrsConstants.ACTIVEMQ.QUEUE_BO_ACCOUNT_REQUEST, updateInfo, false);
		
		AdminAccountDetailsUpdate response = null;
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceID);
			if(obj != null){
				response = (AdminAccountDetailsUpdate) obj;
				LOG.info("Result = " + response.getResult());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("[end] updateBoDetail");
		return response;
	}
	
	public boolean notifyChangeAgreement(String customerServiceId){
		
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setCustomerServiceId(customerServiceId);
		String sequenceId = Utilities.generateRandomPassword(32);
		accountInfo.setAgreementFlg(ITrsConstants.NEWS_AGREEMENT.AGREE_ALL_NEW);
		
		AdminAccountDetailsUpdate info = new AdminAccountDetailsUpdate(ITrsConstants.JMS_CONSTANTS.TARGET, AdminBalanceInfo.COMMAND_UPPDATE_AGREEMENT);
		info.setM_Id(sequenceId);
		info.setAccountDetails(accountInfo);
		
		LOG.info("[start]Send BO update agreement flag request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
		jmsBoSender.sendQueue(ITrsConstants.ACTIVEMQ.QUEUE_BO_ACCOUNT_REQUEST, info, false);
		LOG.info("[end]Send BO update agreement flag request - sequenceId: "+sequenceId+", customerServiceId: "+customerServiceId);
		
		AdminAccountDetailsUpdate response = null;
		AccountInfo result = null;
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				response = (AdminAccountDetailsUpdate) obj;
				result = response.getAccountDetails();
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		if(result != null){
			return true;
		}else{
			return false;
		}
	}

	public IJmsSender getJmsBoSender() {
		return jmsBoSender;
	}

	public void setJmsBoSender(IJmsSender jmsBoSender) {
		this.jmsBoSender = jmsBoSender;
	}
}
