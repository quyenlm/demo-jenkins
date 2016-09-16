package phn.nts.ams.fe.jms;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import phn.com.nts.util.log.Logit;

import com.phn.nts.jms.sender.JMSPublisher;

public abstract class AbstractJmsPublisher extends JMSPublisher {
	private static final Logit LOG = Logit.getInstance(AbstractJmsPublisher.class);
	public AbstractJmsPublisher(String jmsTopicFactory, String topicName) {
		super(jmsTopicFactory, topicName);
	}
	
	public void sendTextMessage(String message) {
		try {
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText(message);
			publisher.send(textMessage);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
	
	public void sendObjectMessage(Serializable objMsg) {
		try {
			ObjectMessage objMessage = session.createObjectMessage();
			objMessage.setObject(objMsg);
			publisher.send(objMessage);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

}
