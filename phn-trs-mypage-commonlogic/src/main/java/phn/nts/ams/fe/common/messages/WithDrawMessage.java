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


import com.phn.mt.common.entity.FundRecord;



public class WithDrawMessage implements MessageListener, Serializable{
	/**
	 * 
	 */
	private Logit logger = Logit.getInstance(WithDrawMessage.class);
	private static final long serialVersionUID = 1L;
	private static WithDrawMessage withDrawMessage =  new WithDrawMessage();
	private static Map<String, FundRecord> listFundRecord = new HashMap<String, FundRecord>();
	
	public static WithDrawMessage getInstance() throws NamingException, JMSException {
		if(withDrawMessage == null) {
			withDrawMessage = new WithDrawMessage();
		}
		return withDrawMessage;
	}	
	public static WithDrawMessage getInstance(String sequenceID) throws NamingException, JMSException {
		if(withDrawMessage == null) {
			withDrawMessage = new WithDrawMessage();
		}
		return withDrawMessage;
	}	

	public void onMessage(Message message) {
		//System.out.println("WithDraw receive success");
		if(message != null) {		
			if(message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage)message;
				FundRecord fundRecord = null;
				try {
					fundRecord = (FundRecord) objMessage.getObject();
				} catch (JMSException e) {				
					logger.error(e.toString(), e);
				}
				if(fundRecord != null) {
					//write log fund record
					String sequenceID = fundRecord.getSequenceID();
					if(!listFundRecord.containsKey(sequenceID)) {
						listFundRecord.put(sequenceID, fundRecord);
						logger.info("onMessage fundRecord.getSequenceID(): " + fundRecord.getSequenceID());
					}
					
				}
			}
		}		
	}	
	
	public boolean getFlag(String sequenceID) {
		if(listFundRecord.containsKey(sequenceID)) {
			return true;
		}
		return false;
	}
	
	public FundRecord getFundRecord(String sequenceID) {
		FundRecord fr = null;
		if(listFundRecord.containsKey(sequenceID)) {
			fr = listFundRecord.get(sequenceID);
		}
		return fr;
	}
	
	public void removeFundRecord(String sequenceID) {
		if(listFundRecord.containsKey(sequenceID)) {
			listFundRecord.remove(sequenceID);
		}
	}
}
