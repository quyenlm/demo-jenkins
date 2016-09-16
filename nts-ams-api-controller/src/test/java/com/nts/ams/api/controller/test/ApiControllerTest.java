package com.nts.ams.api.controller.test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
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

import com.google.protobuf.ByteString;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ActionType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoInvestmentPurpose;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeAmount;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ConfirmFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReadFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerAgreementNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsTranferMoneyInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsWithdrawalTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.PaymentType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoResponse;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositResponse;
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
 * @CrDate Jan 19, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ApiControllerTest implements Runnable {
	private static ConcurrentHashMap<String, String> hmRequest = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, Object> hmResponse = new ConcurrentHashMap<String, Object>();
	
	private String name = "";
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		
//	private static String QUEUE_AMSINFO = "queue.AmsCustomerInfoRequest";
//	private static String TOPIC_AMSINFO = "topic.AmsCustomerInfoResponse";
	
	private static String QUEUE_AMSINFO = "queue.AmsTransactionInfoRequest";
	private static String TOPIC_AMSINFO = "topic.AmsTransactionInfoResponse";
	
	public static void main(String[] args) throws JMSException, ParseException {
		new Thread(new ApiControllerTest("")).start();
	}
	
	public ApiControllerTest(String id) {
		this.setName(id);
	}
			
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
	
	private MessageProducer producer;
	private Session session;
	
	@Override
	public void run() {
		try {
			System.out.println("Producer STARTTING...");
			
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection = connectionFactory.createConnection();
			connection.start();

			System.out.println("Producer START");
			
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(session.createQueue(QUEUE_AMSINFO));
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			MessageConsumer consumer = session.createConsumer(session.createTopic(TOPIC_AMSINFO));
			new Receiver(consumer).start();
			
			printHelp();
			
			Scanner scaner = new Scanner(System.in);
			String[] arrParam;
			while(true) {
				arrParam = scaner.nextLine().split(" ", -1);
				if(arrParam[0].equals("help")) {
					printHelp();
					continue;
				} else if(arrParam[0].equals("exit")) {
					break;
				}
				
				if(arrParam.length > 1) {
					//Send request
					if(arrParam[0].equalsIgnoreCase("getcus")) {
						//get customer
						ByteString data = genAmsCustomerInfoRequest(arrParam[1], arrParam.length > 2 ? arrParam[2] : null).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("getbl")) {
						//Get Balance
						ByteString data = genAmsCustomerBalanceRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("upcus")) {
						//Update Customer
//						for(int i = 0; i< 1; i++) {
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//									try {
										String id = sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST, genAmsCustomerInfoRequest(arrParam[1], "TRS").toByteString());
										AmsCustomerInfo cusInfo = (AmsCustomerInfo)getResult(id);
										
										if(cusInfo != null) {
											//Clear pass before update
											ByteString data = genAmsCustomerInfoUpdateRequest(cusInfo.toBuilder().clearPassword().build()).toByteString();
											sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_REQUEST, data);
										}
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							}).start();
//						}
					}  else if(arrParam[0].equalsIgnoreCase("wd")) {
						//withDrawal
						ByteString data = genAmsWithdrawalRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDRAWAL_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("tf")) {
						//Transfer
						ByteString data = genAmsTransferRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("getpm")) {
						//Get payment method
						ByteString data = genAmsCustomerPaymentInfoRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("botest")) {
						//BoTestUpdate
						ByteString data = genAmsCustomerBoTestUpdateRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BO_TEST_UPDATE_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("gnew")) {
						//News Agreement
						ByteString data = genAmsCustomerNewsRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("gnew2")) {
						//News Agreement
						ByteString data = genAmsCustomerAgreementNewsRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_AGREEMENT_NEWS_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("addbo")) {
						//News Agreement
						ByteString data = genAmsBoAdditionalInfoUpdateRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_BO_ADDITIONAL_INFO_UPDATE_REQUEST, data);
					} else if(arrParam[0].equalsIgnoreCase("upAgree")) {
						//News Agreement
						ByteString data = genAmsCustomerNewsUpdateRequest(arrParam[1]).toByteString();
						sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_NEWS_UPDATE_REQUEST, data);
					} 
				} else
					System.out.println("Not valid param");
			}
			scaner.close();
			connection.close();
			System.out.println("Producer END");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
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
			System.out.println(getName() + "Request: " + message);
		} catch (Exception e) {
			throw e;
		}
		return id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	// gen AmsCustomerInfoRequest
	private AmsCustomerInfoRequest genAmsCustomerInfoRequest(String customerId, String wlCode) throws IOException {
		AmsCustomerInfoRequest.Builder requestBuilder = AmsCustomerInfoRequest.newBuilder();
		requestBuilder.setCustomerId(customerId);
		if(wlCode != null)
			requestBuilder.setWlCode(wlCode);
		return requestBuilder.build();
	}
	
	// gen AmsCustomerBalanceRequest
	private AmsCustomerBalanceRequest genAmsCustomerBalanceRequest(String customerId) throws IOException {
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
		
		return requestBuilder.build();
	}
//	LAST_NAME, FIRST_NAME_KANA, LAST_NAME_KANA,
	// gen AmsCustomerInfoUpdateRequest
	private AmsCustomerInfoUpdateRequest genAmsCustomerInfoUpdateRequest(AmsCustomerInfo cusInfo){
		AmsCustomerInfoUpdateRequest.Builder requestBuilder = AmsCustomerInfoUpdateRequest.newBuilder();
		requestBuilder.setActionType(ActionType.UPDATE);
		//set requestBuilder
		requestBuilder.setCustomerInfo(cusInfo.toBuilder()
//				.setBeneficOwnerFlg("0")
//				.setPassword("a12345678")
//				.setNewPassword("a12345678")
				.setEmail("test_2008_1@nextop.asia")
//				.setSection("ｔｅｓｓｔ1")
//				.setFirstName("Mss")
//				.setPicBuildingName("Lanmark")
//				.setPicFirstName("PicFirst")
//				.setBeneficOwnerCity("HN")
//				.setBeneficOwnerFullname("Owner")
//				.setRepFirstName("RepFirt")
				.setBoPurposeHedgeAmount(BoPurposeHedgeAmount.HEDGE_AMOUNT_4)
				.setBoInvestmentPurpose(cusInfo.getBoInvestmentPurpose().toBuilder().setBoPurposeHedgeFlg("1"))
				);
		return requestBuilder.build();
	}
	
	// gen genAmsWithdrawalRequest
	private AmsWithdrawalRequest genAmsWithdrawalRequest(String customerId){
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
		return requestBuilder.build();
	}
	
	// gen genAmsTransferRequest
	private AmsTransferRequest genAmsTransferRequest(String customerId){
		AmsTransferRequest.Builder requestBuilder = AmsTransferRequest.newBuilder();
		//CustomerId: 1009464
		AmsTranferMoneyInfo.Builder tranBuilder = AmsTranferMoneyInfo.newBuilder();
		tranBuilder.setCustomerId(customerId);
		tranBuilder.setCurrencyCode("JPY");
		tranBuilder.setWlCode("TRS");
		tranBuilder.setDestinationAmount("1000");
		tranBuilder.setTranferMoney("1000");
		tranBuilder.setTranferFrom(ServiceType.AMS);
		tranBuilder.setTranferTo(ServiceType.NTD_FX);
		tranBuilder.setDestinationCurrencyCode("JPY");
		
		//set requestBuilder
		requestBuilder.setTransferInfo(tranBuilder);
		return requestBuilder.build();
	}
	
	// gen AmsCustomerPaymentInfoRequest
	private AmsCustomerPaymentInfoRequest genAmsCustomerPaymentInfoRequest(String customerId){
		AmsCustomerPaymentInfoRequest.Builder requestBuilder = AmsCustomerPaymentInfoRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setPaymentType(PaymentType.VIRTUAL_TYPE);
		requestBuilder.setWlCode("TRS");
		
		return requestBuilder.build();
	}
	
	//genAmsCustomerBoTestUpdateRequest
	private AmsCustomerBoTestUpdateRequest genAmsCustomerBoTestUpdateRequest(String customerId){
		AmsCustomerBoTestUpdateRequest.Builder requestBuilder = AmsCustomerBoTestUpdateRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setCustomerServiceId(customerId + "04");
		
		return requestBuilder.build();
	}
	
	//genAmsCustomerNewsRequest
	private AmsCustomerNewsRequest genAmsCustomerNewsRequest(String customerId){
		AmsCustomerNewsRequest.Builder requestBuilder = AmsCustomerNewsRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		requestBuilder.setMessageType(MessageType.RE_AGREEMENT);
		
		return requestBuilder.build();
	}
	
	private AmsCustomerAgreementNewsRequest genAmsCustomerAgreementNewsRequest(String customerId){
		AmsCustomerAgreementNewsRequest.Builder requestBuilder = AmsCustomerAgreementNewsRequest.newBuilder();
		
		requestBuilder.setCustomerId(customerId);
		
		return requestBuilder.build();
	}
	
	//genAmsCustomerNewsRequest
	private AmsBoAdditionalInfoUpdateRequest genAmsBoAdditionalInfoUpdateRequest(String customerId){
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
		
		return requestBuilder.build();
	}
	
	//genAmsCustomerBoTestUpdateRequest
	private AmsCustomerNewsUpdateRequest genAmsCustomerNewsUpdateRequest(String customerId){
		AmsCustomerNewsUpdateRequest.Builder requestBuilder = AmsCustomerNewsUpdateRequest.newBuilder();
		requestBuilder.setCustomerId(customerId);
		AmsNewsInfo.Builder newInfo = AmsNewsInfo.newBuilder();
		newInfo.setMessageId("163");
		newInfo.setMessageType(MessageType.RE_AGREEMENT);
		newInfo.setConfirmFlg(ConfirmFlag.CONFIRMED);
		requestBuilder.addNewsInfo(newInfo);
		return requestBuilder.build();
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
					
				    if(hmRequest.containsKey(rpcResponse.getId()))
				    	hmRequest.remove(rpcResponse.getId());
				    else
				    	return;
				    
					//Handle
					if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsCustomerInfoResponse amsCustomerInfoResponse = AmsCustomerInfoResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
							hmResponse.put(rpcResponse.getId(), amsCustomerInfoResponse.getCustomerInfo());
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BALANCE_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsCustomerBalanceResponse amsCustomerInfoResponse = AmsCustomerBalanceResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsCustomerInfoUpdateResponse amsCustomerInfoResponse = AmsCustomerInfoUpdateResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_WITHDAWAL_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsWithdrawalResponse amsCustomerInfoResponse = AmsWithdrawalResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsTransferResponse amsCustomerInfoResponse = AmsTransferResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsDepositResponse amsCustomerInfoResponse = AmsDepositResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + amsCustomerInfoResponse);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_RESPONSE)) {
						if(rpcResponse.getResult() == Result.SUCCESS) {
							AmsCustomerPaymentInfoResponse response = AmsCustomerPaymentInfoResponse.parseFrom(rpcResponse.getPayloadData());
							System.out.println(getHeader(rpcResponse.getId()) + response);
						}
						else
							System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					} else
						System.out.println(getHeader(rpcResponse.getId()) + rpcResponse);
					
				} catch (JMSException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				System.out.println(getName() + " Received: " + message);
			
			synchronized (ApiControllerTest.this) {
				ApiControllerTest.this.notifyAll();
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
		return "Responsed[" + id + "] - ";
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
	
	private static void printHelp() {
		System.out.println("getcus [cusId - 1005012] [wlId - TRS]: get customer");
		System.out.println("getbl [cusId - 1005012]: get balance");
		System.out.println("upcus [cusId - 1005012]: update customer");
		
		System.out.println("wd [cusId - 1005012]: withDrawal");
		System.out.println("tf [cusId - 1005012] [from - ServiceType(0,1,4,7,21,22,23)] [to - ServiceType(0,1,4,7,21,22,23)]: transfer");
		System.out.println("getpm [cusId - 1005012]: get payment method");
		
		System.out.println("botest [cusId - 1005012]: Bo test request");
		System.out.println("gnew [cusId - 1005012]: get news and agreement");
		System.out.println("gnew2 [cusId - 1005012]: get agreement news");
		System.out.println("addbo [cusId - 1005012]: add Bo account");
		System.out.println("upAgree [cusId - 1015713]: add Bo account");
	}
}