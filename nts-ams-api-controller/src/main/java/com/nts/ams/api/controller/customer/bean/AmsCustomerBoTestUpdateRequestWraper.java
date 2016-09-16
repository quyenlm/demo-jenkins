package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 27, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerBoTestUpdateRequestWraper extends AbstractRequestWraper<AmsCustomerBoTestUpdateRequest> {
	public AmsCustomerBoTestUpdateRequestWraper(AmsCustomerBoTestUpdateRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}