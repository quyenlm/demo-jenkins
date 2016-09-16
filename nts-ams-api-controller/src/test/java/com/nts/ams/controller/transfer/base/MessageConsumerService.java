package com.nts.ams.controller.transfer.base;

import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public abstract class MessageConsumerService implements MessageListener {
	
	@Override
	public void onMessage(Message message) {
		try {

			if (message instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) message;
				byte[] data = new byte[(int) bytesMessage.getBodyLength()];
				bytesMessage.readBytes(data);
				RpcMessage rpcResponse = RpcMessage.parseFrom(data);
				handleResponseMessage(rpcResponse);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void handleResponseMessage(RpcMessage rpcResponse);	
	
}
