package phn.nts.ams.fe.common.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import phn.com.nts.util.log.Logit;

import com.phn.mt.common.entity.UserRecord;

public class AccountChangePassword implements MessageListener, Serializable {
	private static final long serialVersionUID = 1L;
	private static AccountChangePassword accountChangePassword = null;
	private static Map<Integer, Boolean> list_account_changepass_flag = new HashMap<Integer, Boolean>();
	private static Map<Integer, UserRecord> listUserRecord = new HashMap<Integer, UserRecord>();
	
	private Logit logger = Logit.getInstance(AccountChangePassword.class);
	public static AccountChangePassword getInstance() throws NamingException, JMSException {
		if(accountChangePassword == null) {
			accountChangePassword = new AccountChangePassword();
		}
		return accountChangePassword;
	}	
	public static AccountChangePassword getInstance(Integer customerId) throws NamingException, JMSException {
		if(accountChangePassword == null) {
			accountChangePassword = new AccountChangePassword();
		}
		if(!list_account_changepass_flag.containsKey(customerId)) {
			list_account_changepass_flag.put(customerId, false);
		}
		return accountChangePassword;
	}	

	public void onMessage(Message message) {
		if(message != null) {		
			if(message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage)message;
				UserRecord userRecord = null;
				try {
					userRecord = (UserRecord) objMessage.getObject();
				} catch (JMSException e) {				
					logger.error(e.toString(), e);
				}
				if(userRecord != null ) {
					int id = userRecord.getLogin();
					Integer customerId = new Integer(id);
					if(list_account_changepass_flag.containsKey(customerId)) {
						list_account_changepass_flag.put(customerId, true);
						listUserRecord.put(customerId, userRecord);
					}
					
				}
			}
		}		
	}	
	public Boolean getFlag(Integer customerId) {
		Boolean b = false;
		if(list_account_changepass_flag.containsKey(customerId)) {
			b = list_account_changepass_flag.get(customerId);
		}
		return b;
	}
	public UserRecord getUserRecord(Integer customerId) {
		UserRecord ur = null;
		if(list_account_changepass_flag.containsKey(customerId)) {
			ur = listUserRecord.get(customerId);
		}
		return ur;
	}
	
	public void removeUserRecord(Integer customerId) {
		if(list_account_changepass_flag.containsKey(customerId)) {
			list_account_changepass_flag.put(customerId, false);
			listUserRecord.remove(customerId);
		}
	}
}
