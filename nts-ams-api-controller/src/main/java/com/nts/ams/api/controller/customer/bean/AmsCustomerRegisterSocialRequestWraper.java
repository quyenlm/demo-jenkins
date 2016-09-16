package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerRegisterSocialRequestWraper extends AbstractRequestWraper<AmsCustomerRegisterSocialRequest> {
	public AmsCustomerRegisterSocialRequestWraper(AmsCustomerRegisterSocialRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
	
}