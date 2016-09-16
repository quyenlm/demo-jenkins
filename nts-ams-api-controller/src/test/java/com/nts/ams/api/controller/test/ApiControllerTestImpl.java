package com.nts.ams.api.controller.test;

import java.util.concurrent.ConcurrentHashMap;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import phn.com.nts.util.log.Logit;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ActionType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoInvestmentPurpose;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeAmount;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ConfirmFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.FileType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsTranferMoneyInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsWithdrawalTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.PaymentType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoResponse;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferResponse;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 23, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ApiControllerTestImpl {
	private static Logit log = Logit.getInstance(ApiControllerTestImpl.class);
	private static ConcurrentHashMap<String, String> hmRequest = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, Object> hmResponse = new ConcurrentHashMap<String, Object>();
	
	public static final String QUEUE_CUSTOMER_REQUEST = "queue.AmsCustomerInfoRequest";
	public static final String TOPIC_CUSTOMER_RESPONSE = "topic.AmsCustomerInfoResponse";
	
	public static final String QUEUE_TRANSACTION_REQUEST = "queue.AmsTransactionInfoRequest";
	public static final String TOPIC_TRANSACTION_RESPONSE = "topic.AmsTransactionInfoResponse";
	
	public static final String QUEUE_BE_REQUEST = "queue.AmsCustomerInfoBERequest";
	public static final String TOPIC_BE_RESPONSE = "topic.AmsCustomerInfoBEResponse";
	
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private String queueRequest;
	private String topicReponse;
	
	public ApiControllerTestImpl(String queueRequest, String topicReponse) {
		this(ActiveMQConnection.DEFAULT_BROKER_URL, queueRequest, topicReponse);
	}
	
	public ApiControllerTestImpl(String amqUrl, String queueRequest, String topicReponse) {
		this.queueRequest = queueRequest;
		this.topicReponse = topicReponse;
		this.url = amqUrl;
	}	
	
	private Connection connection;
	private MessageProducer producer;
	private Session session;
	private MessageConsumer consumer;
	private Receiver receiver;
	
	public void startConnection() {
		try {
			log.info("CONNECTING to AMQ...");
			
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			connection = connectionFactory.createConnection();
			connection.start();

			log.info("start Producer...");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(session.createQueue(queueRequest));
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			log.info("started Producer");
			
			log.info("start Consumer...");
			consumer = session.createConsumer(session.createTopic(topicReponse));
			receiver = new Receiver(consumer);
			receiver.start();
			log.info("started Consumer");
			
			log.info("CONNECTED to AMQ: DONE");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	public void stopConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String sendRequest(String payloadClass, ByteString payloadData) throws Exception {
		String id = System.currentTimeMillis() + "";
		try {
			RpcAms.RpcMessage.Builder requestBuilder = RpcAms.RpcMessage.newBuilder();
			requestBuilder.setId(id);
			requestBuilder.setVersion("123456");
			requestBuilder.setPayloadClass(payloadClass);
			requestBuilder.setPayloadData(payloadData);
			
			hmRequest.put(requestBuilder.getId(), requestBuilder.getId() + "");
			
			BytesMessage message = session.createBytesMessage();
			message.writeBytes(requestBuilder.build().toByteArray());
			producer.send(message);
			
			log.info("Request[" + id + "]:\n" + requestBuilder.build());
		} catch (Exception e) {
			throw e;
		}
		return id;
	}
	
	// gen AmsCustomerInfoRequest
	public AmsCustomerInfo getAmsCustomerInfo(String customerId, String wlCode) throws Exception {
		//Create request
		AmsCustomerInfoRequest.Builder requestBuilder = AmsCustomerInfoRequest.newBuilder();
		requestBuilder.setCustomerId(customerId);
		if(wlCode != null)
			requestBuilder.setWlCode(wlCode);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerInfo response  = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_RESPONSE)) {
			
			AmsCustomerInfoResponse amsCustomerInfoResponse = AmsCustomerInfoResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + amsCustomerInfoResponse);
			response = amsCustomerInfoResponse.getCustomerInfo();
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	// gen AmsCustomerBalanceRequest
	public AmsCustomerBalanceResponse getAmsCustomerBalance(String customerId) throws Exception {
		//Create request
		
		AmsCustomerBalanceRequest.Builder requestBuilder = AmsCustomerBalanceRequest.newBuilder();
		requestBuilder.setCustomerId(customerId);
		
		ServiceTypeInfo.Builder serviceAMS = ServiceTypeInfo.newBuilder();
		serviceAMS.setCurrencyCode("JPY");
		serviceAMS.setServiceType(ServiceType.AMS);
		serviceAMS.setCustomerServiceId(customerId + "0" + ServiceType.AMS.getNumber());
		requestBuilder.addServiceTypeInfo(serviceAMS);
		
		ServiceTypeInfo.Builder serviceSC = ServiceTypeInfo.newBuilder();
		serviceSC.setCurrencyCode("JPY");
		serviceSC.setServiceType(ServiceType.SC);
		serviceSC.setCustomerServiceId(customerId + "0" + ServiceType.SC.getNumber());
		requestBuilder.addServiceTypeInfo(serviceSC);
		
		ServiceTypeInfo.Builder serviceNTD = ServiceTypeInfo.newBuilder();
		serviceNTD.setCurrencyCode("JPY");
		serviceNTD.setServiceType(ServiceType.NTD_FX);
		serviceNTD.setCustomerServiceId(customerId + "0" + ServiceType.NTD_CFD.getNumber());
		requestBuilder.addServiceTypeInfo(serviceNTD);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerBalanceResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_RESPONSE)) {
			response = AmsCustomerBalanceResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	// gen AmsCustomerInfoUpdateRequest
	public AmsCustomerInfoUpdateResponse updateAmsCustomerInfo(AmsCustomerInfo cusInfo) throws Exception{
		AmsCustomerInfoUpdateRequest.Builder requestBuilder = AmsCustomerInfoUpdateRequest.newBuilder();
		requestBuilder.setActionType(ActionType.UPDATE);
		//set requestBuilder
		requestBuilder.setCustomerInfo(cusInfo.toBuilder()
//				.setBeneficOwnerFlg("0")
//				.setPassword("a123456788")
//				.setNewPassword("a12345678")
//				.setEmail("test101_99@test.com")
//				.setSection("ｔｅｓｓｔ1")
//				.setFirstName("Mss")
//				.setPicBuildingName("Lanmark")
//				.setPicFirstName("PicFirst")
//				.setBeneficOwnerCity("HN")
//				.setBeneficOwnerFullname("Owner")
//				.setRepFirstName("RepFirt")
//				.setBoPurposeHedgeAmount(BoPurposeHedgeAmount.HEDGE_AMOUNT_4)
				.setBoInvestmentPurpose(cusInfo.getBoInvestmentPurpose().toBuilder().setBoPurposeHedgeFlg("1"))
				);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerInfoUpdateResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_RESPONSE)) {
			response = AmsCustomerInfoUpdateResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	// gen genAmsWithdrawalRequest
	public AmsWithdrawalResponse requestAmsWithdrawal(String customerId) throws Exception{
		AmsWithdrawalRequest.Builder requestBuilder = AmsWithdrawalRequest.newBuilder();
		
		AmsWithdrawalTransactionInfo.Builder tranBuilder = AmsWithdrawalTransactionInfo.newBuilder();
		tranBuilder.setCurrencyCode("JPY");
		tranBuilder.setWlCode("TRS");
		tranBuilder.setCustomerId(customerId);
		tranBuilder.setWithdrawalAmount("2000");
		tranBuilder.setServiceType(ServiceType.AMS);
		tranBuilder.setWithdrawalFee("300");
		tranBuilder.setCustomerBankId("40961");
		
		//set requestBuilder
		requestBuilder.setWithdrawalInfo(tranBuilder);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDRAWAL_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsWithdrawalResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDAWAL_RESPONSE)) {
			
			response = AmsWithdrawalResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	// gen genAmsTransferRequest
	public AmsTransferResponse requestAmsTransfer(String customerId) throws Exception{
		AmsTransferRequest.Builder requestBuilder = AmsTransferRequest.newBuilder();
		//CustomerId: 1009464
		AmsTranferMoneyInfo.Builder tranBuilder = AmsTranferMoneyInfo.newBuilder();
		tranBuilder.setCustomerId(customerId);
		tranBuilder.setCurrencyCode("JPY");
		tranBuilder.setWlCode("TRS");
		tranBuilder.setDestinationAmount("5000");
		tranBuilder.setTranferMoney("5000");
		tranBuilder.setTranferFrom(ServiceType.SC);
		tranBuilder.setTranferTo(ServiceType.NTD_FX);
		tranBuilder.setDestinationCurrencyCode("JPY");
		tranBuilder.setRemark("test remark");
		
		//set requestBuilder
		requestBuilder.setTransferInfo(tranBuilder);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsTransferResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_RESPONSE)) {
			
			response = AmsTransferResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	// gen AmsCustomerPaymentInfoRequest
	public AmsCustomerPaymentInfoResponse getAmsCustomerPaymentInfo(String customerId) throws Exception{
		AmsCustomerPaymentInfoRequest.Builder requestBuilder = AmsCustomerPaymentInfoRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setPaymentType(PaymentType.VIRTUAL_TYPE);
		requestBuilder.setWlCode("TRS");
				
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerPaymentInfoResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_RESPONSE)) {
			
			response = AmsCustomerPaymentInfoResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//genAmsCustomerBoTestUpdateRequest
	public AmsCustomerBoTestUpdateResponse updateAmsCustomerBoTest(String customerId) throws Exception{
		AmsCustomerBoTestUpdateRequest.Builder requestBuilder = AmsCustomerBoTestUpdateRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setCustomerServiceId(customerId + "04");
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BO_TEST_UPDATE_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerBoTestUpdateResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BO_TEST_UPDATE_RESPONSE)) {
			
			response = AmsCustomerBoTestUpdateResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//genAmsCustomerNewsRequest
	public AmsCustomerNewsResponse getAmsCustomerNews(String customerId) throws Exception{
		AmsCustomerNewsRequest.Builder requestBuilder = AmsCustomerNewsRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setMessageType(MessageType.RE_AGREEMENT);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerNewsResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_RESPONSE)) {
			
			response = AmsCustomerNewsResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//genAmsCustomerNewsRequest
	public AmsBoAdditionalInfoUpdateResponse updateAmsBoAdditionalInfo(String customerId) throws Exception{
		AmsBoAdditionalInfoUpdateRequest.Builder requestBuilder = AmsBoAdditionalInfoUpdateRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		
		BoInvestmentPurpose.Builder boInvestmentPurposeBuilder = BoInvestmentPurpose.newBuilder();
		boInvestmentPurposeBuilder.setBoPurposeDispAssetMngFlg("1");
		boInvestmentPurposeBuilder.setBoPurposeHedgeFlg("1");
		boInvestmentPurposeBuilder.setBoPurposeShortTermFlg("1");	
		requestBuilder.setBoInvestmentPurpose(boInvestmentPurposeBuilder);
		
		requestBuilder.setBoPurposeHedgeType(BoPurposeHedgeType.HEDGE_FX);
		requestBuilder.setBoPurposeHedgeAmount(BoPurposeHedgeAmount.HEDGE_AMOUNT_5);
		requestBuilder.setBoLossMaxAmount("100000");
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_BO_ADDITIONAL_INFO_UPDATE_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsBoAdditionalInfoUpdateResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_BO_ADDITIONAL_INFO_UPDATE_RESPONSE)) {
			
			response = AmsBoAdditionalInfoUpdateResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//genAmsCustomerBoTestUpdateRequest
	public AmsCustomerNewsUpdateResponse updateAmsCustomerNews(String customerId) throws Exception{
		AmsCustomerNewsUpdateRequest.Builder requestBuilder = AmsCustomerNewsUpdateRequest.newBuilder();
		requestBuilder.setCustomerId(customerId);
		AmsNewsInfo.Builder newInfo = AmsNewsInfo.newBuilder();
		newInfo.setMessageId("163");
		newInfo.setMessageType(MessageType.RE_AGREEMENT);
		newInfo.setConfirmFlg(ConfirmFlag.CONFIRMED);
		requestBuilder.addNewsInfo(newInfo);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_UPDATE_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerNewsUpdateResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_UPDATE_RESPONSE)) {
			
			response = AmsCustomerNewsUpdateResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//AmsCustomerRegisterSocialRequest
	public AmsCustomerRegisterSocialResponse registerAmsCustomerSocial(String customerId) throws Exception{
		AmsCustomerRegisterSocialRequest.Builder requestBuilder = AmsCustomerRegisterSocialRequest.newBuilder();
		requestBuilder.setCustomerId("1039810");		
		requestBuilder.setLoginId("corp_user1@nextop.asia");
		requestBuilder.setZipCode("12345678");
		requestBuilder.setAddress1("HN");
		requestBuilder.setFirstName("ｃｏｒｐ");
		requestBuilder.setLastName("ｕｓｅｒ１");
		
		//Add service type
		ServiceTypeInfo.Builder sc = ServiceTypeInfo.newBuilder();
		sc.setCustomerServiceId("103981007");
		sc.setSubGroupId("4");
		sc.setSubGroupCode("ST_Corp");		
		sc.setLeverage("100");
		sc.setSignalFlg(0);
		sc.setDepositCurrency("JPY");
		sc.setCurrencyCode("JPY");
		sc.setServiceType(ServiceType.SC);
		requestBuilder.addServiceTypeInfo(sc);
		
		//Add service type
		ServiceTypeInfo.Builder ntdFx = ServiceTypeInfo.newBuilder();
		ntdFx.setCustomerServiceId("103981021");
		ntdFx.setSubGroupId("15");
		ntdFx.setSubGroupCode("NTDFX_Indiv");		
		ntdFx.setLeverage("100");
		ntdFx.setSignalFlg(0);
		ntdFx.setDepositCurrency("JPY");
		ntdFx.setCurrencyCode("JPY");
		ntdFx.setServiceType(ServiceType.NTD_FX);
		requestBuilder.addServiceTypeInfo(ntdFx);
		
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REGISTER_SOCIAL_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerRegisterSocialResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REGISTER_SOCIAL_RESPONSE)) {
			
			response = AmsCustomerRegisterSocialResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//AmsCustomerRegisterSocialRequest
	public AmsCustomerModifySocialResponse modifyAmsCustomerSocial(String customerId) throws Exception{
		AmsCustomerModifySocialRequest.Builder requestBuilder = AmsCustomerModifySocialRequest.newBuilder();
		requestBuilder.setCustomerId("1039811");
		requestBuilder.setCustomerServiceId("103981107");
		requestBuilder.setZipCode("123456789");
		requestBuilder.setAddress1("HN");
		requestBuilder.setFirstName("ｃｏｒｐ");
		requestBuilder.setLastName("ｕｓｅｒ１");
		requestBuilder.setLoginId("1039811@gmail.com");
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_MODIFY_SOCIAL_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerModifySocialResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_MODIFY_SOCIAL_RESPONSE)) {
			
			response = AmsCustomerModifySocialResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	//AmsCustomerRegisterSocialRequest
	public AmsCustomerCloseSocialResponse closeAmsCustomerSocial(String customerId) throws Exception{
		AmsCustomerCloseSocialRequest.Builder requestBuilder = AmsCustomerCloseSocialRequest.newBuilder();
		requestBuilder.setCustomerId("1039811");
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_CLOSE_SOCIAL_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerCloseSocialResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_CLOSE_SOCIAL_RESPONSE)) {
			
			response = AmsCustomerCloseSocialResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
	}
	
	public AmsCustomerReportsResponse getReportBo() throws Exception {
		AmsCustomerReportsRequest.Builder requestBuilder = AmsCustomerReportsRequest.newBuilder();
		
		requestBuilder.setFromDate("20160201");
		requestBuilder.setToDate("20160225");
		requestBuilder.setServiceType(ServiceType.BO);
		requestBuilder.setReportType(ReportType.REPORT_DAILY);
		requestBuilder.setFileType(FileType.FILE_PDF);
		requestBuilder.setWlCode("TRS");
		requestBuilder.setCustomerId("1060883");
		requestBuilder.setPageNumber(1);
		requestBuilder.setPageSize(1);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerReportsResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_RESPONSE)) {
			
			response = AmsCustomerReportsResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
		
	}
	
	public AmsCustomerReportsResponse getReportSc() throws Exception {
		AmsCustomerReportsRequest.Builder requestBuilder = AmsCustomerReportsRequest.newBuilder();
		
		requestBuilder.setFromDate("20150201");
		requestBuilder.setToDate("20170525");
		requestBuilder.setServiceType(ServiceType.SC);
		requestBuilder.setReportType(ReportType.REPORT_MONTHLY);
		requestBuilder.setFileType(FileType.FILE_PDF);
		requestBuilder.setWlCode("TRS");
		requestBuilder.setCustomerId("1055022");
		requestBuilder.setPageNumber(1);
		requestBuilder.setPageSize(1);
		
		//Send request
		String requestId = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_REQUEST, requestBuilder.build().toByteString());
		
		//Received result
		AmsCustomerReportsResponse response = null;
		RpcMessage rpcResponse = (RpcMessage)getResult(requestId);
		
		//Handle
		if(rpcResponse != null && rpcResponse.getResult() == Result.SUCCESS
				&& rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_REPORTS_RESPONSE)) {
			
			response = AmsCustomerReportsResponse.parseFrom(rpcResponse.getPayloadData());
			log.info(getHeader(requestId) + response);
		} else
			log.warn(getHeader(requestId) + rpcResponse);
		
		return response;
		
	}
	
	private class Receiver extends Thread implements MessageListener {
		private MessageConsumer consumer;
		
		public Receiver(MessageConsumer consumer) {
			this.consumer = consumer;
		}
		
		@Override
		public void onMessage(Message message) {
			if(message instanceof BytesMessage) {
				try {
				    BytesMessage bytesMessage = (BytesMessage) message;
				    byte[] data = new byte[(int) bytesMessage.getBodyLength()];
				    bytesMessage.readBytes(data);
				    RpcMessage rpcResponse = RpcAms.RpcMessage.parseFrom(data);
					
				    if(hmRequest.containsKey(rpcResponse.getId())) {
				    	hmRequest.remove(rpcResponse.getId());
				    	hmResponse.put(rpcResponse.getId(), rpcResponse);
				    }
				} catch (JMSException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				log.info(getName() + " Received: " + message);
			
			synchronized (ApiControllerTestImpl.this) {
				ApiControllerTestImpl.this.notifyAll();
			}
		}

		@Override
		public void run() {
			try {
				consumer.setMessageListener(this);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
	
	private  static String getHeader(String id) {
		return "Responsed[" + id + "]:\n";
	}
	
	private static Object getResult(String id) {
		Object result = null;
		int i = 0;
		while(i++ < 50) {
			result = hmResponse.get(id);
			if(result == null)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	public static void printHelp() {
		log.info("getcus [cusId - 1005012] [wlId - TRS]: get customer");
		log.info("getbl [cusId - 1005012]: get balance");
		log.info("upcus [cusId - 1005012]: update customer");
		
		log.info("wd [cusId - 1005012]: withDrawal");
		log.info("tf [cusId - 1005012] [from - ServiceType(0,1,4,7,21,22,23)] [to - ServiceType(0,1,4,7,21,22,23)]: transfer");
		log.info("getpm [cusId - 1005012]: get payment method");
		
		log.info("botest [cusId - 1005012]: Bo test request");
		log.info("gnew [cusId - 1005012]: get news and agreement");
		log.info("addbo [cusId - 1005012]: add Bo account");
		log.info("upAgree [cusId - 1015713]: add Bo account");
		log.info("reportbo: get report bo");
		log.info("reportsc: get report sc");
		log.info("msc [cusId - 1015713]: modify social account from BE");
	}

	
}