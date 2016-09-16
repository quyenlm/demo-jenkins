package phn.nts.ams.fe.jms;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;

import phn.com.nts.util.log.Logit;

import com.phn.mt.common.entity.UserRecord;

public class AccountChangePasswordReceiver extends AbstractJmsReceiver implements IJmsReceiver {
	private static final Logit LOG = Logit.getInstance(AccountChangePasswordReceiver.class);
	public AccountChangePasswordReceiver(String queueName, Properties props) {
		super(queueName, props);
	}
	@Override
	protected void receiveMsg() throws JMSException {
		if(receiver != null) {
			Message message = receiver.receive();
			if(message != null) {
				
			}
		}
	}
	public boolean checkResult(String key) {
		boolean result = false;
		try {
			UserRecord userRecord = null;
			while(userRecord == null) {
				userRecord = (UserRecord) this.mapJmsObject.get(key);
				if(userRecord != null) {
					result = true;
					break;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
}
