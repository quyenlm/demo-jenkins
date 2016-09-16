package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.ams.api.controller.common.bean.RequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description CustomerInfoRequest Wraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentUpdateRequestWraper extends AbstractRequestWraper<AmsCustomerPaymentUpdateRequest> implements RequestWraper<AmsCustomerPaymentUpdateRequest> {
	public AmsCustomerPaymentUpdateRequestWraper(AmsCustomerPaymentUpdateRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}