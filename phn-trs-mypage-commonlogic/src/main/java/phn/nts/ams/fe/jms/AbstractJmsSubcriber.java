package phn.nts.ams.fe.jms;

import java.util.Properties;

import phn.com.nts.util.log.Logit;

import com.phn.nts.jms.receiver.JMSSubscriber;

/**
 * @description abstract jms subcriber
 * @version NTS1.0
 * @author QuyTM
 * @CrDate Mar 7, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class AbstractJmsSubcriber extends JMSSubscriber {
	private static final Logit LOG = Logit.getInstance(AbstractJmsSubcriber.class);
	public AbstractJmsSubcriber(String jmsFactory, String topicName) {
		super(jmsFactory, topicName);
	}
	public AbstractJmsSubcriber(String topicName, Properties props) {
		super(null, topicName);
		try {
			connectToJMSServer(props);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
}
