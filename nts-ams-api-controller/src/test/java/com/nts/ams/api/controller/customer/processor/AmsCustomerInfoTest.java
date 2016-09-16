package com.nts.ams.api.controller.customer.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nts.ams.api.controller.customer.base.BaseAmsCustomerInfoTest;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

public class AmsCustomerInfoTest extends BaseAmsCustomerInfoTest {
	private AmsCustomerInfoResponse response;
	
	public AmsCustomerInfoTest() throws Exception {
		super();
	}

	public AmsCustomerInfoResponse getAmsCustomerInfoCorporation() throws Exception {

		AmsCustomerInfoRequest request = AmsCustomerInfoRequest.newBuilder()
				.setCustomerId("1004836")
				.setWlCode("TRS")
				.build();

		messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST, request.toByteString());

		while (true) {

			// Wait until has response
			if (response != null) {
				return response;
			}

			Thread.sleep(1);
		}
	}

	public AmsCustomerInfoResponse getAmsCustomerInfoIndividual(String customerId) throws Exception {

		AmsCustomerInfoRequest request = AmsCustomerInfoRequest.newBuilder()
				 											   .setCustomerId(customerId)
				 											   .setWlCode("TRS")
				 											   .build();

		messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_REQUEST, request.toByteString());

		while (true) {

			// Wait until has response
			if (response != null) {
				return response;
			}

			Thread.sleep(1);
		}
	}

	@Override
	public void handleResponseMessage(RpcMessage rpcResponse) {
		try {
			if (rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_RESPONSE)) {
				if (rpcResponse.getResult() == com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result.SUCCESS) {
					response = AmsCustomerInfoResponse.parseFrom(rpcResponse.getPayloadData());
				}
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		AmsCustomerInfoTest amsCustomerBoTestUpdateTaskTest = new AmsCustomerInfoTest();
//		AmsCustomerInfoResponse response = amsCustomerBoTestUpdateTaskTest.getAmsCustomerInfoIndividual("1005000");
		AmsCustomerInfoResponse response = amsCustomerBoTestUpdateTaskTest.getAmsCustomerInfoCorporation();
		System.out.println(response);

	}

}