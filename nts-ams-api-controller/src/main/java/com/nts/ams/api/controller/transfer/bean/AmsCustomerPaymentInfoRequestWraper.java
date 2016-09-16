package com.nts.ams.api.controller.transfer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description CustomerBalanceRequestWraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentInfoRequestWraper extends AbstractRequestWraper<AmsCustomerPaymentInfoRequest> {
	public AmsCustomerPaymentInfoRequestWraper(AmsCustomerPaymentInfoRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}