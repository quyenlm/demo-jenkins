package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description CustomerInfoRequest Wraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class CustomerInfoUpdateRequestWraper extends AbstractRequestWraper<AmsCustomerInfoUpdateRequest> {
	public CustomerInfoUpdateRequestWraper(AmsCustomerInfoUpdateRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerInfo().getCustomerId());
	}
	
}