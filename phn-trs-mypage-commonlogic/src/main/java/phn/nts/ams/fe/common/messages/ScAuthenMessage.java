package phn.nts.ams.fe.common.messages;

import com.nts.common.exchange.dealing.FxWsAuthInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.jms.IJmsSender;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.HashMap;
import java.util.Map;



public class ScAuthenMessage {
    /**
     *
     */
    private Logit logger = Logit.getInstance(ScAuthenMessage.class);
    private static final long serialVersionUID = 1L;
    private Map<String, FxWsAuthInfo> mapFxWsAuthInfo = new HashMap<String, FxWsAuthInfo>();
    private IJmsSender jmsRealSender;
    private IJmsSender jmsDemoSender;


    public void onMessage(Message message) {
        if(message != null) {
            if(message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage)message;
                FxWsAuthInfo fxWsAuthInfo = null;
                try {
                    fxWsAuthInfo = (FxWsAuthInfo) objMessage.getObject();
                } catch (JMSException e) {
                    logger.error(e.toString(), e);
                }
                if(fxWsAuthInfo != null && fxWsAuthInfo.getTicketId() != null) {

                    Object data = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + fxWsAuthInfo.getTicketId());
                    if(data != null) {
                        fxWsAuthInfo.setResult(IConstants.AUTHENTICATION_RESULT.AUTHENTICATION_RESULT_SUCCESS);
                        logger.info("[start] send authentication response with ticketId = " + fxWsAuthInfo.getTicketId());
                        jmsRealSender.sendTopic(IConstants.ACTIVEMQ.SC_FE_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
                        logger.info("[end] send authentication response with ticketId = " + fxWsAuthInfo.getTicketId());
                    } else {
                        fxWsAuthInfo.setResult(IConstants.AUTHENTICATION_RESULT.AUTHENTICATION_RESULT_FAILURE);
                    }

                    if(!mapFxWsAuthInfo.containsKey(fxWsAuthInfo.getTicketId())) {
                        mapFxWsAuthInfo.put(fxWsAuthInfo.getTicketId(), fxWsAuthInfo);
                    }

                }
            }
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