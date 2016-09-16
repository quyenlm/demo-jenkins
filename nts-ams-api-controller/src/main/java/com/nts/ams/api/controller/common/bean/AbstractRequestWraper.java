package com.nts.ams.api.controller.common.bean;

import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Builder;

/**
 * @description AbstractRequest Wraper
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 8, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AbstractRequestWraper<T> implements RequestWraper<T> {
	private String customerId;
	private T request;
	private Builder responseBuilder;
	
	public AbstractRequestWraper(T request, Builder responseBuilder, String customerId) {
		this.setRequest(request);
		this.setResponseBuilder(responseBuilder);
		this.setCustomerId(customerId);
	}

	public T getRequest() {
		return request;
	}

	public void setRequest(T request) {
		this.request = request;
	}

	public Builder getResponseBuilder() {
		return responseBuilder;
	}

	public void setResponseBuilder(Builder responseBuilder) {
		this.responseBuilder = responseBuilder;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}