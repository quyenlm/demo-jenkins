package com.nts.ams.api.controller.service;

import com.nts.ams.api.controller.setting.Configuration;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class SecurityMananger extends Thread {
	private AtomicInteger currentProcess = new AtomicInteger(0);
	private Configuration configuration;
	private static SecurityMananger securityMananger;
	
	private SecurityMananger(Configuration configuration) {
		this.configuration = configuration;		
	}
	
	public static SecurityMananger init(Configuration configuration) {
		if(securityMananger == null) {
			securityMananger = new SecurityMananger(configuration);	
		}
		return securityMananger;
	}
	
	public static SecurityMananger getInstance() {
		return securityMananger;
	}
	
	public boolean isServiceAvaiable() {
		if(currentProcess.get() < configuration.getMaxRequestOnTime()) {
			currentProcess.incrementAndGet();
			return true;
		}
		
		return false;
	}
	
	public void freeHandle() {
		if(currentProcess.get() > 0)
			currentProcess.decrementAndGet();
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}