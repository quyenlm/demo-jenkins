package com.nts.ams.api.controller.transfer.processor;

import com.nts.ams.controller.transfer.base.BaseAmsCustomerTranferTest;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService;
import com.nts.common.exchange.proto.ams.internal.RpcAms;

/**
 * Created by cuong.bui.manh on 8/11/2016.
 */
public class AmsTransferTaskTest extends BaseAmsCustomerTranferTest{

    public AmsTransferTaskTest() throws Exception {
        super();
    }

    @Override
    public void handleResponseMessage(RpcAms.RpcMessage rpcResponse) {

    }

    private void transferMoney() throws Exception {
        AmsTransactionService.AmsTransferRequest.Builder request = AmsTransactionService.AmsTransferRequest.newBuilder();
        //CustomerId: 1009464
        AmsTransactionModel.AmsTranferMoneyInfo.Builder tranBuilder = AmsTransactionModel.AmsTranferMoneyInfo.newBuilder();
        tranBuilder.setCustomerId("1004836");
        tranBuilder.setCurrencyCode("JPY");
        tranBuilder.setWlCode("TRS");
        tranBuilder.setDestinationAmount("5000");
        tranBuilder.setTranferMoney("5000");
        tranBuilder.setTranferFrom(AmsCustomerinfoModel.ServiceType.SC);
        tranBuilder.setTranferTo(AmsCustomerinfoModel.ServiceType.NTD_FX);
        tranBuilder.setDestinationCurrencyCode("JPY");
        tranBuilder.setRemark("test remark");

        //set requestBuilder
        request.setTransferInfo(tranBuilder);

        //Send request
        messageProducerService.sendRequest(ProtoMsgConstant.AmsInternalMsgType.AMS_TRANSFER_REQUEST, request.build().toByteString());
    }

    public static void main(String[] args){
        try {
            AmsTransferTaskTest amsTransferTaskTest;
            amsTransferTaskTest = new AmsTransferTaskTest();
            amsTransferTaskTest.transferMoney();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
