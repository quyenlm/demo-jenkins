package com.nts.ams.api.controller.customer.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import phn.com.nts.util.log.Logit;

import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

public class AmsCustomerResponsePublisher {
	private static Logit log = Logit.getInstance(AmsCustomerResponsePublisher.class);
	private JmsTemplate jmsTemplate;
	private String topicName;
	private Topic topic;
	
	public void publish(final RpcMessage responseMsg) {
		try {
			getJmsTemplate().send(topic, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					BytesMessage msgObj = null;
					msgObj = session.createBytesMessage();
					msgObj.writeBytes(responseMsg.toByteArray());
					log.info("Published to client:\n" + responseMsg);
					return msgObj;
				}
			});
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(final String topicName) {
		topic = new Topic() {					
			public String getTopicName() throws JMSException {
				return topicName;
			}
		};
		this.topicName = topicName;
	}
}