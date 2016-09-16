package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerAgreementNewsRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Apr 12, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerAgreementNewsWraper extends AbstractRequestWraper<AmsCustomerAgreementNewsRequest> {
	public AmsCustomerAgreementNewsWraper(AmsCustomerAgreementNewsRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}