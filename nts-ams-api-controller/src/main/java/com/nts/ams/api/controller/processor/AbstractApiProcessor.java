package com.nts.ams.api.controller.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.nts.ams.api.controller.service.SecurityMananger;

import phn.com.nts.util.log.Logit;

/**
 * @description AbstractApiProcessor
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public abstract class AbstractApiProcessor<T> implements ApiProcessor<T> {
	private static Logit log = Logit.getInstance(AbstractApiProcessor.class);
	private ExecutorService executorService;
	// Create a factory that produces daemon threads with a naming pattern and a priority
	private BasicThreadFactory factory = null;
	
	
	/**
	 * Start　service
	 * 
	 * @param serviceName - serviceName
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public void start(String serviceName, int poolSize) {
		if(executorService == null) {
			log.info("Starting service: " + serviceName);
			
			factory = new BasicThreadFactory.Builder()
					.namingPattern(serviceName + "-%d").daemon(true)
					.priority(Thread.MAX_PRIORITY).build();
			
			executorService = Executors.newCachedThreadPool(factory);
			log.info("Started service: " + serviceName);
		} else
			log.warn("Service: " + serviceName + " already STARTED");
	}
	
	/**
	 * Stop service in timeOut MILLISECONDS
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 20, 2015
	 * @MdDate
	 */
	public void stop(Long timeOut) {
		if(getExecutorService() != null)
			try {
				getExecutorService().awaitTermination(timeOut, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
	}
	
	public abstract void onMessage(T wraper) throws Exception;
	
	public void onComplete(T wraper) {
		//count down security
		SecurityMananger.getInstance().freeHandle();
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}