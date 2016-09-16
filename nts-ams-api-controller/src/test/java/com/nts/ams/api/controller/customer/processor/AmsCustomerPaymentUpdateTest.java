package com.nts.ams.api.controller.customer.processor;

import com.nts.ams.api.controller.customer.base.BaseAmsCustomerInfoTest;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms;

/**
 * Created by cuong.bui.manh on 8/4/2016.
 */
public class AmsCustomerPaymentUpdateTest extends BaseAmsCustomerInfoTest {

    public AmsCustomerPaymentUpdateTest() throws Exception {
        super();
    }

    @Override
    public void handleResponseMessage(RpcAms.RpcMessage rpcResponse) {

    }

    public void updateBankInfo() throws Exception {
        AmsCustomerPaymentUpdateRequest request = AmsCustomerPaymentUpdateRequest.newBuilder()
                .setCustomerId("1069923")
                .setPaymentInfo(AmsTransactionModel.AmsCustomerPaymentInfo.newBuilder()
                        .setCustomerBankInfo(AmsTransactionModel.AmsCustomerBankInfo.newBuilder()
                                .setCustomerId("1069923")
                                .setBankName("CuongBM_BankName")
                                .setBranchName("CuongBM_BranchName")
                                .setAccountNo("1111111")
                                .setBankCode("0038")
                                .setBranchCode("100")
                                .setCustomerBankId("19")))
                .setActionType(AmsCustomerinfoModel.ActionType.UPDATE)
                .build();

        messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_UPDATE_REQUEST, request.toByteString());
    }

    public static void main(String[] args){
		try {
			AmsCustomerPaymentUpdateTest amsCustomerPaymentUpdateTest;
			amsCustomerPaymentUpdateTest = new AmsCustomerPaymentUpdateTest();
			amsCustomerPaymentUpdateTest.updateBankInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
