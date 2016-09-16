package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerNewsUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description CustomerBalanceRequestWraper
 * @version NTS
 * @author ThinhPH
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerNewsUpdateRequestWraper extends AbstractRequestWraper<AmsCustomerNewsUpdateRequest> {
	public AmsCustomerNewsUpdateRequestWraper(AmsCustomerNewsUpdateRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}