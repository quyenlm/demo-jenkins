package com.nts.ams.controller.transfer.base;

import com.google.protobuf.ByteString;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class MessageProducerService {

	private static final String DESTINATION_QUEUE = "queue.AmsTransactionInfoRequest";

	public void sendRequest(String payloadClass, ByteString payloadData) throws Exception {
		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(session.createQueue(DESTINATION_QUEUE));
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		RpcAms.RpcMessage.Builder requestBuilder = RpcAms.RpcMessage.newBuilder();
		requestBuilder.setId(System.currentTimeMillis() + "");
		requestBuilder.setVersion("123456");
		requestBuilder.setPayloadClass(payloadClass);
		requestBuilder.setPayloadData(payloadData);

		BytesMessage message = session.createBytesMessage();
		message.writeBytes(requestBuilder.build().toByteArray());
		producer.send(message);

		connection.close();
	}

}
