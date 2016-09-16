package phn.nts.ams.fe.jms;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;

import com.phn.mt.common.entity.UserRecord;
import com.phn.nts.jms.receiver.JMSReceiver;

public class AbstractJmsReceiver extends JMSReceiver {
	protected ConcurrentMap<String, Object> mapJmsObject = new ConcurrentHashMap<String, Object>();
	
	private static final Logit LOG = Logit.getInstance(AbstractJmsReceiver.class);
	public AbstractJmsReceiver(String jmsFactory, String queueName) {
		super(jmsFactory, queueName);
	}
	public AbstractJmsReceiver(String queueName, Properties props) {
		super(null, queueName);
		try {
			que_mode = QUE_MODE_RECEIVER;
			connectToJMSServer(props);
			
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
	public Object getObject(String key) {
		return this.mapJmsObject.get(key);
	}
	public void putObject(String key, Object value) {
		this.mapJmsObject.put(key, value);
	}
	@Override
	protected void receiveMsg() throws JMSException {
		if(receiver != null) {
			Message message = receiver.receive();
			if(message != null) {
				if(message instanceof TextMessage) {
					
				} else if(message instanceof ObjectMessage) {
					ObjectMessage objMsg = (ObjectMessage) message;
					Object obj = objMsg.getObject();
					if(obj instanceof UserRecord) {
						UserRecord userRecord = (UserRecord) obj;
						String key = StringUtil.toString(userRecord.getLogin());
						if(mapJmsObject.containsKey(key)) {
							mapJmsObject.put(key, userRecord);
						}
					} else if(obj instanceof List<?>) {
						try {
							List<UserRecord> listUserRecord = null;
							listUserRecord = (List<UserRecord>) obj;
							if(listUserRecord != null && listUserRecord.size() > 0) {
								UserRecord userRecord = listUserRecord.get(0);
								String key = StringUtil.toString(userRecord.getLogin());
								if(mapJmsObject.containsKey(key)) {						
									mapJmsObject.put(key, listUserRecord);
								}					
							}
						} catch(Exception ex) {
							LOG.error(ex.getMessage(), ex);
						}
					}
				}
			}
		}
	}
}
