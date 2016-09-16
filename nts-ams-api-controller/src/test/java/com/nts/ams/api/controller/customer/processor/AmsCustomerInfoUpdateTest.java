package com.nts.ams.api.controller.customer.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nts.ams.api.controller.customer.base.BaseAmsCustomerInfoTest;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ActionType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;

public class 	AmsCustomerInfoUpdateTest extends BaseAmsCustomerInfoTest {
	AmsCustomerInfoUpdateResponse response;
	
	public AmsCustomerInfoUpdateTest() throws Exception {
		super();
	}

	@Override
	public void handleResponseMessage(RpcMessage rpcResponse) {
		try {
			if (rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_RESPONSE)) {
				if (rpcResponse.getResult() == com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result.SUCCESS) {
					response = AmsCustomerInfoUpdateResponse.parseFrom(rpcResponse.getPayloadData());
				}
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCustomer() throws Exception {
		// Get customer
		String customerId = "1004836";
		AmsCustomerInfoTest amsCustomerInfoTest = new AmsCustomerInfoTest();
		AmsCustomerInfoResponse response = amsCustomerInfoTest.getAmsCustomerInfoIndividual(customerId);
		AmsCustomerInfo amsCustomerInfo = response.getCustomerInfo();
		AmsCustomerInfo.Builder amsCustomerInfoBuilder = amsCustomerInfo.toBuilder()
																	    .setFullName("cuongTest");
		
		// Update customer
		
		AmsCustomerInfoUpdateRequest request = AmsCustomerInfoUpdateRequest.newBuilder()
																		   .setCustomerInfo(amsCustomerInfoBuilder)
																		   .setActionType(ActionType.UPDATE)
																		   .build();
		
		messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_REQUEST, request.toByteString());
	}

	public void updateBeneficOwner() throws Exception {
		// Get customer
		String customerId = "1004836";
		AmsCustomerInfoTest amsCustomerBoTestUpdateTaskTest = new AmsCustomerInfoTest();
		AmsCustomerInfoResponse response = amsCustomerBoTestUpdateTaskTest.getAmsCustomerInfoIndividual(customerId);
		AmsCustomerInfo amsCustomerInfo = response.getCustomerInfo();
		AmsCustomerInfo.Builder amsCustomerInfoBuilder = amsCustomerInfo.toBuilder()
				.setBeneficOwnerFlg("1")
				.setBeneficOwnerFullname("cuong_fullname")
				.setBeneficOwnerFullnameKana("cuong_fullname_kana")
				.setBeneficOwnerEstablishDate("20150101")
				.setBeneficOwnerZipcode("1000222")
				.setBeneficOwnerPrefecture("cuong_preficture")
				.setBeneficOwnerCity("cuong_city")
				.setBeneficOwnerSection("cuong_section")
				.setBeneficOwnerBuildingName("cuong_building_name")
				.setBeneficOwnerBuildingName("cuong_building_name")
				.setBeneficOwnerTel("cuong_tell")

				.setBeneficOwnerFlg2("1")
				.setBeneficOwnerFullname2("cuong_fullname_2")
				.setBeneficOwnerFullnameKana2("cuong_fullname_kana_2")
				.setBeneficOwnerEstablishDate2("20150101")
				.setBeneficOwnerZipcode2("1000222")
				.setBeneficOwnerPrefecture2("cuong_preficture2")
				.setBeneficOwnerCity2("cuong_city2")
				.setBeneficOwnerSection2("cuong_section2")
				.setBeneficOwnerBuildingName2("cuong_building_name2")
				.setBeneficOwnerBuildingName2("cuong_building_name2")
				.setBeneficOwnerTel2("cuong_tell_2")

				.setBeneficOwnerFlg3("1")
				.setBeneficOwnerFullname3("cuong_fullname_3")
				.setBeneficOwnerFullnameKana3("cuong_fullname_kana_3")
				.setBeneficOwnerEstablishDate3("20150101")
				.setBeneficOwnerZipcode3("1000222")
				.setBeneficOwnerPrefecture3("cuong_preficture3")
				.setBeneficOwnerCity3("cuong_city3")
				.setBeneficOwnerSection3("cuong_section3")
				.setBeneficOwnerBuildingName3("cuong_building_name3")
				.setBeneficOwnerBuildingName3("cuong_building_name3")
				.setBeneficOwnerTel3("cuong_tell_3");



		// Update customer

		AmsCustomerInfoUpdateRequest request = AmsCustomerInfoUpdateRequest.newBuilder()
				.setCustomerInfo(amsCustomerInfoBuilder)
				.setActionType(ActionType.UPDATE)
				.build();

		messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_INFO_UPDATE_REQUEST, request.toByteString());
	}
	
	public static void main(String[] args) throws Exception {
		AmsCustomerInfoUpdateTest amsCustomerInfoUpdateTest = new AmsCustomerInfoUpdateTest();
//		amsCustomerInfoUpdateTest.updateCustomer();
		amsCustomerInfoUpdateTest.updateBeneficOwner();
	}

}