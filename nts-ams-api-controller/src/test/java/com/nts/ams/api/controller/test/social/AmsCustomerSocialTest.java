package com.nts.ams.api.controller.test.social;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nts.ams.api.controller.test.ApiControllerTestImpl;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerCloseSocialResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialResponse;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 23, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerSocialTest {
	private final String customerId = "1039811";
	
	ApiControllerTestImpl apiControllerTestImpl;
	
    @Before
    public void setUp() throws Exception {
    	apiControllerTestImpl = new ApiControllerTestImpl(ApiControllerTestImpl.QUEUE_BE_REQUEST, ApiControllerTestImpl.TOPIC_BE_RESPONSE);
    	apiControllerTestImpl.startConnection();
    }
      
//    @Test
    public void testRegisterAmsCustomerSocial() throws Exception {
    	AmsCustomerRegisterSocialResponse response = apiControllerTestImpl.registerAmsCustomerSocial(customerId);
    	
        Assert.assertNotNull("AmsCustomerRegisterSocialResponse shouldn't be null", response);
    }
    
//    @Test
    public void testModifyAmsCustomerSocial() throws Exception {
    	AmsCustomerModifySocialResponse response = apiControllerTestImpl.modifyAmsCustomerSocial(customerId);
    	
        Assert.assertNotNull("AmsCustomerModifySocialResponse shouldn't be null", response);
    }
    
//    @Test
    public void testCloseAmsCustomerSocial() throws Exception {
    	AmsCustomerCloseSocialResponse amsCustomerInfo = apiControllerTestImpl.closeAmsCustomerSocial(customerId);
        Assert.assertNotNull("AmsCustomerInfo shouldn't be null", amsCustomerInfo);
    }
   
    @After
    public void tearDown() throws Exception {
    	apiControllerTestImpl.stopConnection();
    }
}