package com.nts.ams.api.controller.customer.base;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public abstract class BaseAmsCustomerInfoTest extends MessageConsumerService {
	protected MessageProducerService messageProducerService = new MessageProducerService();

	private static final String DESTINATION_TOPIC = "topic.AmsCustomerInfoResponse";
	private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;

	public BaseAmsCustomerInfoTest() throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(session.createTopic(DESTINATION_TOPIC));
		consumer.setMessageListener(this);
	}
}
