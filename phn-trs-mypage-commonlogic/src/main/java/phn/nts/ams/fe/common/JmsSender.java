package phn.nts.ams.fe.common;

import com.nts.common.exchange.proto.ams.RpcAms.RpcMessage;
import com.phn.nts.jms.common.JMSConverter;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.jms.IJmsSender;

import javax.jms.*;

import java.io.Serializable;

public class JmsSender implements IJmsSender {
	private static Logit LOG = Logit.getInstance(JmsSender.class);
	private JmsTemplate jmsTemplate;	
	/**
	 * @param jmsTemplate the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	/**
	 * send queue message 
	 * @param
	 * @return
	 * @throws
	 * @author QuyTM
	 * @CrDate Oct 19, 2012
	 */
	public void sendQueue(String queueKey, final Serializable object) {
		try {
			final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
			jmsTemplate.send(queueName, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage(object);
					return objectMessage;
				}
			});
			
			LOG.info("sent object message to queue OK: " + queueKey);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}	
	/**
	 * send topic message 
	 * @param
	 * @return
	 * @throws
	 * @author QuyTM
	 * @CrDate Oct 19, 2012
	 */
	public void sendTopic(String topicKey, final Serializable object) {
		try {
			//final String topicName = (String) SystemCaching.getInstance().getCache(IConstants.SYSTEM_CONFIG_KEY.JMS_CONTEXT + topicKey);
			final String topicName = FrontEndContext.getInstance().getJmsName(topicKey);
			Topic topic = new Topic() {					
				public String getTopicName() throws JMSException {
					return topicName;
				}
			};
			jmsTemplate.send(topic, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage(object);
					return objectMessage;
				}
			});
			
			LOG.info("sent object message to topic OK: " + topicKey);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

    @Override
    public void sendQueue(String queueKey, final String message) {
        try {
        	final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
            jmsTemplate.send(queueName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(message);
                    return textMessage;
                }
            });
            
            LOG.info("sent text message to queue OK: " + queueKey);
        } catch(Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
    public void sendMapMessage(String topicKey, final Serializable object) {
		try {
			final String topicName = FrontEndContext.getInstance().getJmsName(topicKey);
			Topic topic = new Topic() {					
				public String getTopicName() throws JMSException {
					return topicName;
				}
			};
			jmsTemplate.send(topic, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					MapMessage mapMessage = session.createMapMessage();
					mapMessage = JMSConverter.getInstance().convertToMapMessage(object, mapMessage);
					return mapMessage;
				}
			});
			
			LOG.info("sent map message to topic OK: " + topicKey);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
    }
    
	@Override
	public void sendMapMessageToQueue(String queueKey, final Serializable object) {
		try{
			final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
            jmsTemplate.send(queueName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                	MapMessage mapMessage = session.createMapMessage();
					mapMessage = JMSConverter.getInstance().convertToMapMessage(object, mapMessage);
                	return mapMessage;
                }
            });
            
            LOG.info("sent map message to queue OK: " + queueKey);
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	@Override
	public void sendQueue(String queueKey, final Serializable object, final boolean isMapMessage) {
		try {
			final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
			LOG.info("send message to queue name: " + queueName +" with queue key: " + queueKey);
			jmsTemplate.send(queueName, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					Message message = null;
					if(isMapMessage) {
						message = session.createMapMessage();
						MapMessage mapMessage = (MapMessage)message;
						JMSConverter.getInstance().convertToMapMessage(object, mapMessage);
					} else {
						message = session.createObjectMessage(object);
					}
					return message;
				}
			});	
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void sendTopic(String topicKey, final Serializable object, final boolean isMapMessage) {
		try {
			final String topicName = FrontEndContext.getInstance().getJmsName(topicKey);
			Topic topic = new Topic() {					
				public String getTopicName() throws JMSException {
					return topicName;
				}
			};
			jmsTemplate.send(topic, new MessageCreator() {					
				public Message createMessage(Session session) throws JMSException {
					Message message = null;
					if(isMapMessage) {
						message = session.createMapMessage();
						MapMessage mapMessage = (MapMessage)message;
						JMSConverter.getInstance().convertToMapMessage(object, mapMessage);
					} else {
						message = session.createObjectMessage(object);
					}
					return message;
				}
			});

			LOG.info("sent message to topic OK: " + topicKey);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		
	}
	public void sendByteMessagesToQueue(String queueKey, final byte[] messages) throws JmsException{
		final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
		jmsTemplate.send(queueName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				BytesMessage byteMessage = session.createBytesMessage();
				byteMessage.writeBytes(messages);
				return byteMessage;
			}
		});
		
		LOG.info("sent byte message to queue OK: " + queueKey);
	}

	@Override
	public void sendByteMessageToQueue(final String queueKey, final RpcMessage message) {
		try {
			final String queueName = FrontEndContext.getInstance().getJmsName(queueKey);
            jmsTemplate.send(queueName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                	BytesMessage bytesMessage = null;
					bytesMessage = session.createBytesMessage();
					bytesMessage.writeBytes(message.toByteArray());
					LOG.info("sent message to queue " + queueKey + ": " + message);
					return bytesMessage;
                }
            });
            
            LOG.info("sent rpc message to queue OK: " + queueKey);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
