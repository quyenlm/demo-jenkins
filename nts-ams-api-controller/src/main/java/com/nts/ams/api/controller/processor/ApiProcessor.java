package com.nts.ams.api.controller.processor;


/**
 * @description ApiProcessor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public interface ApiProcessor<T> {
	public void start(String serviceName, int poolSize);
	public void stop(Long timeOut);
	public void onMessage(T wraper) throws Exception;
	public void onComplete(T wraper) throws Exception;
}