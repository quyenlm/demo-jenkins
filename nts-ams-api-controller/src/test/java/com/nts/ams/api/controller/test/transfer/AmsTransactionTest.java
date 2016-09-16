package com.nts.ams.api.controller.test.transfer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nts.ams.api.controller.test.ApiControllerTestImpl;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferResponse;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsWithdrawalResponse;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 23, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransactionTest {
	private final String customerId = "1069923";
	
	ApiControllerTestImpl apiControllerTestImpl;
	
    @Before
    public void setUp() throws Exception {
    	apiControllerTestImpl = new ApiControllerTestImpl(ApiControllerTestImpl.QUEUE_TRANSACTION_REQUEST, ApiControllerTestImpl.TOPIC_TRANSACTION_RESPONSE);
    	apiControllerTestImpl.startConnection();
    }
      
//    @Test
    public void testAmsTransfer() throws Exception {
    	AmsTransferResponse response = apiControllerTestImpl.requestAmsTransfer(customerId);
    	
        Assert.assertNotNull("AmsTransferResponse shouldn't be null", response);
        Assert.assertNotNull("TransferInfo shouldn't be null", response.getTransferInfo());
    }
    
//    @Test
    public void testAmsWithdrawal() throws Exception {
    	AmsWithdrawalResponse response = apiControllerTestImpl.requestAmsWithdrawal(customerId);
    	
        Assert.assertNotNull("AmsWithdrawalResponse shouldn't be null", response);
        Assert.assertNotNull("WithdrawalInfo shouldn't be null", response.getWithdrawalInfo());
    }
   
    @After
    public void tearDown() throws Exception {
    	apiControllerTestImpl.stopConnection();
    }
}