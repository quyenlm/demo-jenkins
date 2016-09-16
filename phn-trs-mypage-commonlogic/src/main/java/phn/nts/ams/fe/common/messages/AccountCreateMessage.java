package phn.nts.ams.fe.common.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import phn.com.nts.util.log.Logit;


import com.phn.mt.common.entity.UserRecord;

public class AccountCreateMessage implements MessageListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static AccountCreateMessage accountCreateMessage =  new AccountCreateMessage();
	private static Map<Integer, Boolean> list_account_create_flag = new HashMap<Integer, Boolean>();
	private static Map<Integer, List<UserRecord>> mapListUserRecord = new HashMap<Integer, List<UserRecord>>();
	
	private Logit logger = Logit.getInstance(AccountCreateMessage.class);
	public static AccountCreateMessage getInstance() throws NamingException, JMSException {
		if(accountCreateMessage == null) {
			accountCreateMessage = new AccountCreateMessage();
		}
		return accountCreateMessage;
	}	
	public static AccountCreateMessage getInstance(Integer customerId) throws NamingException, JMSException {
		if(accountCreateMessage == null) {
			accountCreateMessage = new AccountCreateMessage();
		}
		if(!list_account_create_flag.containsKey(customerId)) {
			list_account_create_flag.put(customerId, false);
		}
		return accountCreateMessage;
	}	
	
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
		if(message != null) {		
			if(message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage)message;
				List<UserRecord> lRecords = null;
				try {
					lRecords = (List<UserRecord>) objMessage.getObject();
				} catch (JMSException e) {				
					logger.error(e.toString(), e);
				}
				if(lRecords != null && lRecords.size() > 0) {
					UserRecord userRecord = lRecords.get(0);
					Integer customerId = userRecord.getLogin();
					if(list_account_create_flag.containsKey(customerId)) {						
						list_account_create_flag.put(customerId, true);
						mapListUserRecord.put(customerId, lRecords);
					}					
				}
			}
		}		
	}	
	public Boolean getFlag(Integer customerId) {
		Boolean b = false;
		if(list_account_create_flag.containsKey(customerId)) {
			b = list_account_create_flag.get(customerId);
		}
		return b;
	}
	public UserRecord getUserRecord(Integer customerId) {
		UserRecord ur = null;
		if(list_account_create_flag.containsKey(customerId)) {
			List<UserRecord> lRecords = mapListUserRecord.get(customerId);
			if(lRecords!= null && lRecords.size() > 0) {
				ur = lRecords.get(0);
			}
		}
		return ur;
	}
	
	public void removeUserRecord(Integer customerId) {
		if(list_account_create_flag.containsKey(customerId)) {
			list_account_create_flag.remove(customerId);
			mapListUserRecord.remove(customerId);
		}
	}
}
