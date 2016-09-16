package com.nts.ams.api.controller.transfer.processor;

import com.nts.ams.controller.transfer.base.BaseAmsCustomerTranferTest;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService;
import com.nts.common.exchange.proto.ams.internal.RpcAms;

/**
 * Created by cuong on 9/9/2016.
 */
public class AmsCustomerPaymentInfoTest extends BaseAmsCustomerTranferTest {

    public AmsCustomerPaymentInfoTest() throws Exception {
        super();
    }

    @Override
    public void handleResponseMessage(RpcAms.RpcMessage rpcResponse) {

    }

    private void getPaymentInfo() throws Exception {
        AmsTransactionService.AmsCustomerPaymentInfoRequest.Builder request = AmsTransactionService.AmsCustomerPaymentInfoRequest.newBuilder();

        request.setCustomerId("1004836");
        request.setPaymentType(AmsTransactionModel.PaymentType.VIRTUAL_TYPE);
        request.setWlCode("TRS");

        //Send request
        messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_REQUEST, request.build().toByteString());

    }


    public static void main(String[] args){
        try {
            AmsCustomerPaymentInfoTest amsCustomerPaymentInfoTest = new AmsCustomerPaymentInfoTest();
            amsCustomerPaymentInfoTest.getPaymentInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
