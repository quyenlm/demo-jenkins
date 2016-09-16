package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description AmsCustomerReportsRequestWraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jan 6, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerReportsRequestWraper extends AbstractRequestWraper<AmsCustomerReportsRequest> {
	public AmsCustomerReportsRequestWraper(AmsCustomerReportsRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}