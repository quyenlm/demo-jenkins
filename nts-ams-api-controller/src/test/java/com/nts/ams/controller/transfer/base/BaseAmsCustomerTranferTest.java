package com.nts.ams.controller.transfer.base;

import com.google.protobuf.ByteString;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by cuong.bui.manh on 8/11/2016.
 */
public abstract class BaseAmsCustomerTranferTest extends MessageConsumerService {

    protected MessageProducerService messageProducerService = new MessageProducerService();

    private static final String DESTINATION_TOPIC = "topic.AmsTransactionInfoResponse";
    private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public BaseAmsCustomerTranferTest() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createTopic(DESTINATION_TOPIC));
        consumer.setMessageListener(this);
    }

}
