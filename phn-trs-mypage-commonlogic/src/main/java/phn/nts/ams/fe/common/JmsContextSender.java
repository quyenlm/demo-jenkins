package phn.nts.ams.fe.common;


import java.util.ArrayList;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.jms.IJmsSender;

import com.nts.common.exchange.bean.BalanceUpdateInfo;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.nts.components.mail.bean.AmsScMailTemplateInfo;
import com.nts.components.mail.bean.MailBase;
import com.nts.components.mail.bean.MailTemplate;
import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.MarginLevel;
import com.phn.mt.common.entity.UserRecord;


public class JmsContextSender implements IJmsContextSender {


	private static Logit LOG = Logit.getInstance(JmsContextSender.class);
	private IJmsSender jmsRealSender;
	private IJmsSender jmsDemoSender;

	private JmsContextSender() {
		
	}

	public void sendMarginRequest(MarginLevel marginLevel, boolean isMapMessage) {
		try {
			// convert margin level to map message					
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, marginLevel, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, e);
		}
	}	

	public void sendWithdrawRequest(FundRecord fundRecord, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, fundRecord, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, e);
		}
	}
	
	public void sendOpenAccountRequest(ArrayList<UserRecord> listRecords, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, listRecords, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, e);
		}
	}
	/**
	 * 　
	 * send request update account on mt4
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 13, 2012
	 * @MdDate
	 */
	public void sendEditAccountRequest(UserRecord userRecord, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, e);
		}
	}
	
	public void sendChangePasswordAccountRequest(UserRecord userRecord, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, e);
		}
	}
	
	public void sendMail(AmsMailTemplateInfo amsMailTemplateInfo, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, amsMailTemplateInfo, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, e);
		}
	}
	
	public void sendMail(AmsScMailTemplateInfo amsScMailTemplateInfo, boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, amsScMailTemplateInfo, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, e);
		}
	}
	
	public void sendMailTemplate(MailTemplate mailTemplateInfo) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, mailTemplateInfo, false);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, e);
		}
	}
	
	// Halh add
	public boolean sendMail(MailBase mail) {
		try {
			 jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, mail,false);
			 return true;
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.FE_CHANGE_PASSWORD_REQUEST, e);
			return false;
		}
	}
	
	public void sendMail(TrsMailTemplateInfo amsMailTemplateInfo , boolean isMapMessage) {
		try {
			jmsRealSender.sendQueue(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, amsMailTemplateInfo, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.SENDMAIL_REQUEST, e);
		}
	}
	
	public void sendOpenDemoAccountRequest(ArrayList<UserRecord> listRecords, boolean isMapMessage) {
		try {
			jmsDemoSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, listRecords, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST + " - " + e.getMessage(), e);
		}
	}
	public void sendEditDemoAccountRequest(UserRecord userRecord, boolean isMapMessage) {
		try {
			jmsDemoSender.sendQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord, isMapMessage);
		} catch (Exception e) {
			LOG.error(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST + " - " + e.getMessage(), e);
		}
	}
	public void sendBalanceUpdateTopic(BalanceUpdateInfo balanceUpdateInfo, boolean isMapMessage) {
		try {			
			jmsRealSender.sendTopic(IConstants.ACTIVEMQ.UPDATE_BALANCE_TOPIC, balanceUpdateInfo, isMapMessage);
		} catch(Exception ex) {
			LOG.error(IConstants.ACTIVEMQ.UPDATE_BALANCE_TOPIC + " - " + ex.getMessage(), ex);
		}
	}

	/**
	 * @return the jmsRealSender
	 */
	public IJmsSender getJmsRealSender() {
		return jmsRealSender;
	}

	/**
	 * @param jmsRealSender the jmsRealSender to set
	 */
	public void setJmsRealSender(IJmsSender jmsRealSender) {
		this.jmsRealSender = jmsRealSender;
	}

	/**
	 * @return the jmsDemoSender
	 */
	public IJmsSender getJmsDemoSender() {
		return jmsDemoSender;
	}

	/**
	 * @param jmsDemoSender the jmsDemoSender to set
	 */
	public void setJmsDemoSender(IJmsSender jmsDemoSender) {
		this.jmsDemoSender = jmsDemoSender;
	}
}
