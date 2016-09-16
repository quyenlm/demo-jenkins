package com.nts.ams.api.controller.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phn.com.nts.util.log.Logit;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsApiControllerStartup {
	private static Logit log = Logit.getInstance(AmsApiControllerStartup.class);
	
	public static void main(String[] args){
		log.info("Starting ams-api-controller");
		
		String springConfig = "spring-ams-api-controller.xml";
        ApplicationContext appContext = new ClassPathXmlApplicationContext(springConfig);
        AmsApiControllerMng.start(appContext);
        
		log.info("Started ams-api-controller successfully");
	}
	
	public static void stop() {
		try {
			AmsApiControllerMng.stop();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}