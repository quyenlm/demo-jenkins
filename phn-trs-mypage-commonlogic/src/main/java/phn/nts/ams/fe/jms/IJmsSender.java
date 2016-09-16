package phn.nts.ams.fe.jms;

import java.io.Serializable;

import org.springframework.jms.JmsException;

import com.nts.common.exchange.proto.ams.RpcAms.RpcMessage;


public interface IJmsSender {
	public void sendQueue(String queueKey, Serializable object, boolean isMapMessage);
	public void sendTopic(String topicKey, Serializable object, boolean isMapMessage);
	public void sendTopic(String topicKey, Serializable object);
	
    public void sendQueue(String queueKey, String message);
    public void sendMapMessage(String topicKey, final Serializable object);
    public void sendMapMessageToQueue(String queueKey, Serializable object);
    public void sendByteMessageToQueue(String queueKey, final RpcMessage responseMsg);
    public void sendByteMessagesToQueue(String queueKey, final byte[] messages) throws JmsException;
}
