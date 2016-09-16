package phn.nts.ams.fe.common.messages;

import com.phn.nts.jms.common.JMSConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import phn.com.nts.util.common.IConstants;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.Utilities;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 14/08/2013 10:16 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class MapMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
        if(o instanceof Message) return (Message)o;
        if(o instanceof Serializable)return JMSConverter.getInstance().convertToMapMessage((Serializable)o, session.createMapMessage());
        return null;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if(message instanceof MapMessage){
            MapMessage mapMessage = (MapMessage)message;
            Integer msgType = mapMessage.getInt("msgType");
            Object clusterServerId = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + ITrsConstants.CONFIG_KEY.CLUSTER_SERVER_ID);
            String sequenceID = mapMessage.getString("sequenceID");
            if(msgType != null && sequenceID != null && (clusterServerId == null || sequenceID.startsWith(clusterServerId.toString()))) {
                switch (msgType.intValue()) {
                    case IConstants.NTS_MSG_TYPE.NTS_MSG_USER_MARGIN_LEVEL:
                        return Utilities.convertMarginLevel(mapMessage);
                    case IConstants.NTS_MSG_TYPE.NTS_MSG_USER_REGIST:
                        return Utilities.convertUserRecord(mapMessage);
                    case IConstants.NTS_MSG_TYPE.NTS_MSG_USER_EDIT:
                        return Utilities.convertUserRecord(mapMessage);
                    case IConstants.NTS_MSG_TYPE.NTS_MSG_USER_BALANCE_UPDATE:
                        return Utilities.convertFundRecord(mapMessage);
                    default:
                        return message;
                }
            }
        }
        return message;
    }
}
