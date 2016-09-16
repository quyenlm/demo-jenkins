package phn.nts.social.fe.web.action;

import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.messages.AmsApiMessageSubcriber;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.jms.IJmsSender;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ConfirmFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReadFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.SourceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class CustomInterceptor implements Interceptor {
	private static Logit log = Logit.getInstance(CustomInterceptor.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IJmsSender jmsRealSender;
	
	public IJmsSender getJmsRealSender() {
		return jmsRealSender;
	}
	public void setJmsRealSender(IJmsSender jmsRealSender) {
		this.jmsRealSender = jmsRealSender;
	}
	@Override
	public void destroy() {
		log.info("detroy custom interceptor");
	}

	@Override
	public void init() {
		log.info("init custom interceptor");
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		log.info("[start] interceptor check news agree");
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline!=null){
				// 2.1. Request to check and display Agreement News (if have)
				if(frontUserOnline.getHaveAgreementFlg()){
					AmsCustomerNewsResponse response = getCustomerNews(frontUserOnline.getUserId());
					
					// 2.2. Check to display Agreement from response: AmsCustomerNewsResponse
					if (response != null) {
						if(response.getTotalRecords() > 0) {
							frontUserOnline.setHaveAgreementFlg(true);
						} else {
							frontUserOnline.setHaveAgreementFlg(false);
						}
						frontUserOnline.setMyPageUrl(response.getMypageUrl());
					} else {
						log.warn("can not get AmsCustomerNewsResponse for customer: "+ frontUserOnline.getLoginId());
						frontUserOnline.setHaveAgreementFlg(false);
					}
				}
				if(frontUserOnline.getHaveAgreementFlg()){
					return "agree";
				}
				if(frontUserOnline.getNormalNewsMessage() != null){
					return "normal_news_message";
				}
			}
			
		}else{
			log.warn("Can not get user online information");
		}
		log.info("[end] interceptor check news agree");
		return invocation.invoke();
	}
	
	private AmsCustomerNewsResponse getCustomerNews(String customerId) throws InvalidProtocolBufferException, InterruptedException {
		String sequenceId = Utilities.generateRandomPassword(32);
		AmsCustomerNewsRequest.Builder amsCustomerNewsRequest = AmsCustomerNewsRequest.newBuilder();
		amsCustomerNewsRequest.setCustomerId(customerId);
		amsCustomerNewsRequest.setSourceType(SourceType.SOCIAL_WEB);
		amsCustomerNewsRequest.setMessageType(MessageType.RE_AGREEMENT_AND_AGREEMENT_NEWS);
		amsCustomerNewsRequest.setReadFlg(ReadFlag.UNREAD);
		amsCustomerNewsRequest.setConfirmFlg(ConfirmFlag.UNCONFIRMED);
		log.info("[start]Call API AmsCustomerNewsRequest - sequenceId: " + sequenceId + " - customerId: " + customerId);
		RpcAms.RpcMessage.Builder rpcMessageBuilder = RpcAms.RpcMessage.newBuilder();
		rpcMessageBuilder.setPayloadClass(ITrsConstants.PROTO_PAYLOAD.AMS_CUSTOMER_NEWS_REQUEST);
		rpcMessageBuilder.setPayloadData(amsCustomerNewsRequest.build().toByteString());
		rpcMessageBuilder.setVersion("123456");
		rpcMessageBuilder.setId(sequenceId);
		jmsRealSender.sendByteMessagesToQueue(ITrsConstants.ACTIVEMQ.QUEUE_AMS_CUSTOMER_INFO_REQUEST, rpcMessageBuilder.build().toByteArray());
		log.info("[end]Call API AmsCustomerNewsRequest - sequenceId: " + sequenceId + " - customerId: " + customerId);
		AmsCustomerNewsResponse response = null;
		RpcAms.RpcMessage receiveMsg = (RpcMessage) AmsApiMessageSubcriber.getJmsResult(sequenceId);
		if(receiveMsg != null){
			response = AmsCustomerNewsResponse.parseFrom(receiveMsg.getPayloadData());
		}
		log.info("RECEIVE AmsCustomerNewsResponse with Data= "+ response);
		return response;
	}

}
