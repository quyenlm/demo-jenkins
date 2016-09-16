package com.nts.ams.api.controller.test.customer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nts.ams.api.controller.test.ApiControllerTestImpl;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBalanceResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateResponse;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 23, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerTest {
	private final String wlCode = "TRS";
	private final String customerId = "1039802";
	
	ApiControllerTestImpl apiControllerTestImpl;
	
    @Before
    public void setUp() throws Exception {
    	apiControllerTestImpl = new ApiControllerTestImpl(ApiControllerTestImpl.QUEUE_CUSTOMER_REQUEST, ApiControllerTestImpl.TOPIC_CUSTOMER_RESPONSE);
    	apiControllerTestImpl.startConnection();
    }
      
//    @Test
    public void testGetAmsCustomerInfo() throws Exception {
    	AmsCustomerInfo response = apiControllerTestImpl.getAmsCustomerInfo(customerId, wlCode);
    	
        Assert.assertNotNull("AmsCustomerInfo shouldn't be null", response);
    }
    
//    @Test
    public void testGetAmsCustomerBalance() throws Exception {
    	AmsCustomerBalanceResponse response = apiControllerTestImpl.getAmsCustomerBalance(customerId);
    	
        Assert.assertNotNull("AmsCustomerBalance shouldn't be null", response);
        if(response != null)
        	Assert.assertTrue("AmsCustomerBalance shouldn't be empty", response.getBalanceInfoCount() > 0);
    }
    
//    @Test
    public void testUpdateAmsCustomerInfo() throws Exception {
    	AmsCustomerInfo amsCustomerInfo = apiControllerTestImpl.getAmsCustomerInfo(customerId, wlCode);
        Assert.assertNotNull("AmsCustomerInfo shouldn't be null", amsCustomerInfo);
        
    	AmsCustomerInfoUpdateResponse response = apiControllerTestImpl.updateAmsCustomerInfo(amsCustomerInfo);
    	
        Assert.assertNotNull("AmsCustomerInfoUpdateResponse shouldn't be null", response);
       	Assert.assertNotNull("AmsCustomerBalance shouldn't be empty", response.getCustomerInfo());
    }
   
    @After
    public void tearDown() throws Exception {
    	apiControllerTestImpl.stopConnection();
    }
}