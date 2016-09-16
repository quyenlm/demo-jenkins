package com.nts.ams.api.controller.transfer.bean;

import com.nts.ams.api.controller.common.bean.AbstractRequestWraper;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsTransferSocialRequest;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Mar 8, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsTransferSocialRequestWraper extends AbstractRequestWraper<AmsTransferSocialRequest> {
	public AmsTransferSocialRequestWraper(AmsTransferSocialRequest request, Builder responseBuilder) {
		super(request, responseBuilder, request.getCustomerId());
	}
	
}