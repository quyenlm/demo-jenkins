package phn.nts.ams.fe.business;

import phn.nts.ams.fe.domain.MessageInfo;

public interface IMessageManager {
	public MessageInfo getMessageInfo(String customerId, Integer messageId);
	public void insertAmsMessageReadTrace(String customerId, Integer messageId);
	public MessageInfo getMessageInfo(Integer messageId);
	public Boolean deleteMessage(String customerId, Integer messageId);
}
