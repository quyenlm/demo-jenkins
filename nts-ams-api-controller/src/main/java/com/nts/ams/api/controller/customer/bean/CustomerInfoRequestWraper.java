package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.ams.api.controller.common.bean.RequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerInfoRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description CustomerInfoRequest Wraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class CustomerInfoRequestWraper extends AbstractRequestWraper<AmsCustomerInfoRequest> implements RequestWraper<AmsCustomerInfoRequest> {
	public CustomerInfoRequestWraper(AmsCustomerInfoRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}