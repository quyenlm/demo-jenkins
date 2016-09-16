package com.nts.ams.api.controller.customer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 27, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsBoAdditionalInfoUpdateRequestWraper extends AbstractRequestWraper<AmsBoAdditionalInfoUpdateRequest> {
	public AmsBoAdditionalInfoUpdateRequestWraper(AmsBoAdditionalInfoUpdateRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
}