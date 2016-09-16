package com.nts.ams.api.controller.customer.base;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.nts.common.exchange.proto.ams.internal.RpcAms;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

public abstract class MessageConsumerService implements MessageListener {
	
	@Override
	public void onMessage(Message message) {
		try {

			if (message instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) message;
				byte[] data = new byte[(int) bytesMessage.getBodyLength()];
				bytesMessage.readBytes(data);
				RpcMessage rpcResponse = RpcAms.RpcMessage.parseFrom(data);
				handleResponseMessage(rpcResponse);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void handleResponseMessage(RpcMessage rpcResponse);	
	
}
