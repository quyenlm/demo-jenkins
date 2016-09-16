package com.nts.ams.api.controller.common.bean;

import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description Wrape Request from client
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public interface RequestWraper<T> {
	public String getCustomerId();
	public T getRequest();
	public Builder getResponseBuilder();
}