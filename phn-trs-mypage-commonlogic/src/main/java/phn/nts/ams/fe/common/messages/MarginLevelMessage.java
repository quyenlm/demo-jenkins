package phn.nts.ams.fe.common.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import phn.nts.ams.fe.common.Utilities;
import phn.com.nts.util.log.Logit;



import com.phn.mt.common.entity.MarginLevel;



public class MarginLevelMessage implements MessageListener, Serializable{
	/**
	 * 
	 */
	private Logit logger = Logit.getInstance(MarginLevelMessage.class);
	private static final long serialVersionUID = 1L;
	private static MarginLevelMessage marginLevelMessage =  null;
	private static Map<String, MarginLevel> listMarginLevel = new HashMap<String, MarginLevel>();
	
	public static MarginLevelMessage getInstance() throws NamingException, JMSException {
		if(marginLevelMessage == null) {
			marginLevelMessage = new MarginLevelMessage();
		}
		return marginLevelMessage;
	}	
	public static MarginLevelMessage getInstance(String sequenceID) throws NamingException, JMSException {
		if(marginLevelMessage == null) {
			marginLevelMessage = new MarginLevelMessage();
		}
		
		return marginLevelMessage;
	}	

	public void onMessage(Message message) {
		//System.out.println("MarginLevel receive success");
		if(message != null) {		
			if(message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage)message;
				MarginLevel marginLevel = null;
				try {
					marginLevel = (MarginLevel) objMessage.getObject();
				} catch (JMSException e) {				
					logger.error(e.toString(), e);
				}
				if(marginLevel != null) {
					String sequenceID = marginLevel.getSequenceID();
					if(!listMarginLevel.containsKey(sequenceID)) {
						listMarginLevel.put(sequenceID, marginLevel);
						logger.info("onMessage marginLevel.getSequenceID(): " + sequenceID);
					}
					
				}
			}else if(message instanceof MapMessage) {
				MapMessage mapMessage = (MapMessage)message;
				MarginLevel marginLevel = null;
//				try{
//					marginLevel = Utilities.convertMarginLevel(mapMessage);					
//				}catch(JMSException e){
//					logger.error(e.toString(), e);
//				}	
				if(marginLevel != null) {
					String sequenceID = marginLevel.getSequenceID();
					if(!listMarginLevel.containsKey(sequenceID)) {
						listMarginLevel.put(sequenceID, marginLevel);
						logger.info("onMessage marginLevel.getSequenceID(): " + sequenceID);
					}
					
				}
			}
		}		
	}
	
	public Boolean getFlag(String sequenceID) {
		if(listMarginLevel.containsKey(sequenceID)) {
			return true;
		}
		return false;
	}
	
	public MarginLevel getMarginLevel(String sequenceID) {
		MarginLevel fr = null;
		if(listMarginLevel.containsKey(sequenceID)) {
			fr = listMarginLevel.get(sequenceID);
		}
		return fr;
	}
	
	public void removeMarginLevel(String sequenceID) {
		if(listMarginLevel.containsKey(sequenceID)) {
			listMarginLevel.remove(sequenceID);
		}
	}
	
}
