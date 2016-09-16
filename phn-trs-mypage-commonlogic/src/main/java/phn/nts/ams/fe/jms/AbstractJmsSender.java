package phn.nts.ams.fe.jms;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.Message;

import phn.com.nts.util.log.Logit;

import com.phn.nts.jms.sender.JMSSender;

public class AbstractJmsSender extends JMSSender {
private static final Logit LOG = Logit.getInstance(AbstractJmsSender.class);
	
	public AbstractJmsSender(String jmsFactory, String queueName) {
		super(jmsFactory, queueName);
	}
	public AbstractJmsSender(String queueName, Properties props) {
		super(null, queueName);
		try {
			connectToJMSServer(props);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
	public void sendMsg(String message) {
		try {
			Message textMessage = session.createTextMessage(message);
			sender.send(textMessage);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
	public void sendMsg(Serializable message) {
		try {
			Message objMessage = session.createObjectMessage(message);
			sender.send(objMessage);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
}
