package phn.nts.ams.fe.business.impl;

import java.sql.Timestamp;

import org.springframework.beans.BeanUtils;

import phn.com.nts.db.dao.IAmsMessageDAO;
import phn.com.nts.db.dao.IAmsMessageReadTraceDAO;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.AmsMessageReadTrace;
import phn.com.nts.db.entity.AmsMessageReadTraceId;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IMessageManager;
import phn.nts.ams.fe.domain.MessageInfo;

public class MessageManagerImpl implements IMessageManager {
	private static final Logit LOG = Logit.getInstance(MessageManagerImpl.class);
	private IAmsMessageDAO<AmsMessage> iAmsMessageDAO;
	private IAmsMessageReadTraceDAO<AmsMessageReadTrace> iAmsMessageReadTraceDAO;
	
	/**
	 * @return the iAmsMessageDAO
	 */
	public IAmsMessageDAO<AmsMessage> getiAmsMessageDAO() {
		return iAmsMessageDAO;
	}
	/**
	 * @param iAmsMessageDAO the iAmsMessageDAO to set
	 */
	public void setiAmsMessageDAO(IAmsMessageDAO<AmsMessage> iAmsMessageDAO) {
		this.iAmsMessageDAO = iAmsMessageDAO;
	}
	/**
	 * @return the iAmsMessageReadTraceDAO
	 */
	public IAmsMessageReadTraceDAO<AmsMessageReadTrace> getiAmsMessageReadTraceDAO() {
		return iAmsMessageReadTraceDAO;
	}
	/**
	 * @param iAmsMessageReadTraceDAO the iAmsMessageReadTraceDAO to set
	 */
	public void setiAmsMessageReadTraceDAO(
			IAmsMessageReadTraceDAO<AmsMessageReadTrace> iAmsMessageReadTraceDAO) {
		this.iAmsMessageReadTraceDAO = iAmsMessageReadTraceDAO;
	}
	
	public MessageInfo getMessageInfo(String customerId, Integer messageId) {
		MessageInfo messageInfo = null;
		AmsMessage amsMessage = getiAmsMessageDAO().getAmsMessage(customerId, messageId);
		if(amsMessage != null) {
			messageInfo = new MessageInfo();
			BeanUtils.copyProperties(amsMessage, messageInfo);
		}
		return messageInfo;
	}
	public MessageInfo getMessageInfo(Integer messageId) {
		MessageInfo messageInfo = null;
		AmsMessage amsMessage = getiAmsMessageDAO().findById(AmsMessage.class, messageId);
		if(amsMessage != null) {
			messageInfo = new MessageInfo();
			BeanUtils.copyProperties(amsMessage, messageInfo);
		}
		return messageInfo;
	}
	public void insertAmsMessageReadTrace(String customerId, Integer messageId) {
		MessageInfo messageInfo = getMessageInfo(messageId);
		if(messageInfo != null) {
			AmsMessageReadTrace amsMessageReadTrace = getiAmsMessageReadTraceDAO().getAmsMessageReadTrace(customerId, messageId);
			if(amsMessageReadTrace == null) {
				LOG.info("messageId " + messageId + " has not read by " + customerId);
				amsMessageReadTrace = new AmsMessageReadTrace();
				AmsMessageReadTraceId amsMessageReadTraceId = new AmsMessageReadTraceId();
				amsMessageReadTraceId.setCustomerId(customerId);
				amsMessageReadTraceId.setMessageId(messageId);
				amsMessageReadTrace.setId(amsMessageReadTraceId);
				amsMessageReadTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				AmsCustomer amsCustomer = new AmsCustomer();
				amsCustomer.setCustomerId(customerId);
				amsMessageReadTrace.setAmsCustomer(amsCustomer);
				AmsMessage amsMessage = new AmsMessage();
				amsMessage.setMessageId(messageId);
				amsMessageReadTrace.setAmsMessage(amsMessage);
				amsMessageReadTrace.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsMessageReadTrace.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsMessageReadTrace.setReadFlg(IConstants.ACTIVE_FLG.ACTIVE);
				getiAmsMessageReadTraceDAO().save(amsMessageReadTrace);
			} else {
				LOG.info("messageId " + messageId + " has read by " + customerId);
			}
			
		} else {
			LOG.info("Cannot find message with customerId " + customerId + " and MessageId " + messageId);
		}
	}
	/**
	 * 　
	 * delete message
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 18, 2012
	 * @MdDate
	 */
	public Boolean deleteMessage(String customerId, Integer messageId) {
		AmsMessage amsMessage = getiAmsMessageDAO().getAmsMessage(customerId, messageId);
		if(amsMessage == null) {
			LOG.info("message " + messageId + " isn't of customer " + customerId);
			return false;
		}
		if(!IConstants.ACTIVE_FLG.ACTIVE.equals(amsMessage.getCustomerDeleteFlg())) {
			LOG.info("message " + messageId + " cannot be deleted by " + customerId);
			return false;
		}
		LOG.info("[start] Delete message Id " + messageId + ", update reading manager flag to 0");
		amsMessage.setReadingManageFlg(IConstants.ACTIVE_FLG.INACTIVE);
		getiAmsMessageDAO().merge(amsMessage);
		LOG.info("[end] Delete message Id " + messageId);
		return true;
	}
	
}
