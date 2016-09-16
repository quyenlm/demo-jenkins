package phn.nts.ams.fe.common.messages;

import com.nts.common.exchange.dealing.FxWsAuthInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.jms.IJmsSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ScTradingAuthenMessage {
	/**
	 * 
	 */
	private Logit logger = Logit.getInstance(ScTradingAuthenMessage.class);
	private static final long serialVersionUID = 1L;
	private static ScTradingAuthenMessage instance;
	private Map<String, FxWsAuthInfo> mapFxWsAuthInfo = new HashMap<String, FxWsAuthInfo>();
	private IJmsSender jmsRealSender;
	private IJmsSender jmsDemoSender;

	public void onMessage(Object message) {
		try
		{
			if(message != null) {		
				if(message instanceof FxWsAuthInfo) {
					FxWsAuthInfo fxWsAuthInfo = (FxWsAuthInfo)message;
					/*if(fxWsAuthInfo != null && fxWsAuthInfo.getTicketId() != null) {
						
						String ticketId = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + fxWsAuthInfo.getTicketId());
						if(ticketId != null) {
							fxWsAuthInfo.setResult(IConstants.AUTHENTICATION_RESULT.AUTHENTICATION_RESULT_SUCCESS);
						} else {
							fxWsAuthInfo.setResult(IConstants.AUTHENTICATION_RESULT.AUTHENTICATION_RESULT_FAILURE);
						}
						
						logger.info("[start] send authentication response with ticketId = " + fxWsAuthInfo.getTicketId());
						//JMSContext.getInstance().sendJMSTopic(IConstants.ACTIVEMQ.SC_FE_TRADING_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
						jmsRealSender.sendTopic(IConstants.ACTIVEMQ.SC_FE_TRADING_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
						logger.info("[end] send authentication response with ticketId = " + fxWsAuthInfo.getTicketId());
						
						
						if(!mapFxWsAuthInfo.containsKey(ticketId)) {
							mapFxWsAuthInfo.put(ticketId, fxWsAuthInfo);
						}
						
					}*/
					
					//HungPV add					
					if(IConstants.WS_AUTHENTICATION_TYPE.LOGIN.equals(fxWsAuthInfo.getAuthenticationType())){
						
						logger.info("Check ticketId on SYSTEM CACHING");
						Object data = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + fxWsAuthInfo.getTicketId());
						if(data != null) {
							
							logger.info("TicketId: " + fxWsAuthInfo.getTicketId() + " is in SYSTEM CACHING");
							fxWsAuthInfo.setResult(IConstants.WS_AUTHENTICATION_RESULT.SUCCESS);
                            if(data instanceof List){
                                List<String> accounts = (List<String>)data;
                                logger.info("Send number of accounts: " + accounts.size());
                                fxWsAuthInfo.setListAccount(accounts);
                            }
							
                            logger.info("[start] sending Social-FrontEnd AUTHENTICATION to WS Server");						
    						jmsRealSender.sendTopic(IConstants.ACTIVEMQ.SC_FE_TRADING_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
    						logger.info("[end] sending Social-front AUTHENTICATION to WS Server");
						} else {
							
							logger.info("TicketId: " + fxWsAuthInfo.getTicketId() + " is not in SYSTEM CACHING");
							fxWsAuthInfo.setResult(IConstants.WS_AUTHENTICATION_RESULT.FAILURE);
						}		
						
						if(!mapFxWsAuthInfo.containsKey(fxWsAuthInfo.getTicketId())) {
							
							mapFxWsAuthInfo.put(fxWsAuthInfo.getTicketId(), fxWsAuthInfo);
						}
					}
					else if(IConstants.WS_AUTHENTICATION_TYPE.VERIFY_TICKET.equals(fxWsAuthInfo.getAuthenticationType())){
						
						logger.info("Check ticketId on SYSTEM CACHING");
						Object data = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + fxWsAuthInfo.getTicketId());
						if(data != null) {
							
							logger.info("TicketId: " + fxWsAuthInfo.getTicketId() + " is in SYSTEM CACHING");
							fxWsAuthInfo.setResult(IConstants.WS_AUTHENTICATION_RESULT.SUCCESS);
                            if(data instanceof List){
                                List<String> accounts = (List<String>)data;
                                logger.info("Send number of accounts: " + accounts.size());
                                fxWsAuthInfo.setListAccount(accounts);
                            }
						} else {
							
							logger.info("TicketId: " + fxWsAuthInfo.getTicketId() + " is not in SYSTEM CACHING");
							fxWsAuthInfo.setResult(IConstants.WS_AUTHENTICATION_RESULT.FAILURE);							
						}
						
						logger.info("[start] sending Social-FrontEnd AUTHENTICATION to WS Server");
						jmsRealSender.sendTopic(IConstants.ACTIVEMQ.SC_FE_TRADING_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
						logger.info("[end] sending Social-FrontEnd AUTHENTICATION to WS Server");
						
						if(!mapFxWsAuthInfo.containsKey(fxWsAuthInfo.getTicketId())) {
							
							mapFxWsAuthInfo.put(fxWsAuthInfo.getTicketId(), fxWsAuthInfo);
						}
					}
					else{
						
						logger.info("Lougout from web socket server successful");
					}
				}				
			}	
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
		}
	}


    public IJmsSender getJmsRealSender() {
		return jmsRealSender;
	}


	public void setJmsRealSender(IJmsSender jmsRealSender) {
		this.jmsRealSender = jmsRealSender;
	}


	public IJmsSender getJmsDemoSender() {
		return jmsDemoSender;
	}


	public void setJmsDemoSender(IJmsSender jmsDemoSender) {
		this.jmsDemoSender = jmsDemoSender;
	}


	public FxWsAuthInfo getFxWsAuthInfo(String ticketId) {
		FxWsAuthInfo fxWsAuthInfo = null;
		if(mapFxWsAuthInfo.containsKey(ticketId)) {
			fxWsAuthInfo = mapFxWsAuthInfo.get(ticketId);
		}
		return fxWsAuthInfo;
	}
	
	public void removeFxWsAuthInfo(String ticketId) {
		if(mapFxWsAuthInfo.containsKey(ticketId)) {
			mapFxWsAuthInfo.remove(ticketId);
		}
	}
}
