package com.nts.ams.api.controller.service;

import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.ISystemPropertyManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.social.SCManager;
import com.nts.ams.api.controller.cache.DataCache;
import com.nts.ams.api.controller.common.AmsApiMode;
import com.nts.ams.api.controller.customer.jms.AmsCustomerResponsePublisher;
import com.nts.ams.api.controller.customer.mail.processor.AmsSocialSendMailProcessor;
import com.nts.ams.api.controller.customer.mail.processor.ScCustomerListener;
import com.nts.ams.api.controller.customer.mail.processor.ScFeedingListener;
import com.nts.ams.api.controller.customer.mail.processor.ScTradingListener;
import com.nts.ams.api.controller.customer.processor.AmsBoAdditionalInfoUpdateProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerAgreementNewsProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerBalanceProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerBoTestUpdateProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerCloseSocialProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerInfoProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerInfoUpdateProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerModifySocialProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerNewsProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerNewsUpdateProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerPaymentUpdateProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerRegisterSocialProcessor;
import com.nts.ams.api.controller.customer.processor.AmsCustomerReportsProcessor;
import com.nts.ams.api.controller.schedule.BjpLoadFileTimer;
import com.nts.ams.api.controller.setting.Configuration;
import com.nts.ams.api.controller.transfer.jms.AmsTransactionInfoPublisher;
import com.nts.ams.api.controller.transfer.processor.AmsCustomerPaymentInfoProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsDepositProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsDepositUpdateProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsTransferProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsTransferSocialAgentProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsWithdrawalCancelProcessor;
import com.nts.ams.api.controller.transfer.processor.AmsWithdrawalProcessor;
import com.nts.ams.api.controller.transfer.processor.manager.AmsSerializeTransactionMananger;

/**
 * @description AmsApiControllerMng
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsApiControllerMng {
	private static Logit log = Logit.getInstance(AmsApiControllerMng.class);
	private static ApplicationContext appContext;
	private static Configuration configuration;
	private static boolean started = false;
	
	private static DataCache dataCache;
	//Manager
	private static AmsSerializeTransactionMananger amsSerializeTransactionMananger;
	
	//Customer
	private static AmsCustomerInfoProcessor amsCustomerInfoProcessor;
	private static AmsCustomerBalanceProcessor amsCustomerBalanceProcessor;
	private static AmsCustomerInfoUpdateProcessor amsCustomerInfoUpdateProcessor;
	private static AmsCustomerPaymentUpdateProcessor amsCustomerPaymentUpdateProcessor;
	private static AmsCustomerBoTestUpdateProcessor amsCustomerBoTestUpdateProcessor;
	private static AmsBoAdditionalInfoUpdateProcessor amsBoAdditionalInfoUpdateProcessor;
	private static AmsCustomerReportsProcessor amsCustomerReportsProcessor;
	private static AmsCustomerRegisterSocialProcessor amsCustomerRegisterSocialProcessor;
	private static AmsCustomerModifySocialProcessor amsCustomerModifySocialProcessor;
	private static AmsCustomerCloseSocialProcessor amsCustomerCloseSocialProcessor;
	private static AmsSocialSendMailProcessor amsSocialSendMailProcessor;
	
	//Transfer
	private static AmsWithdrawalProcessor amsWithdrawalProcessor;
	private static AmsTransferProcessor amsTransferProcessor;
	private static AmsTransferSocialAgentProcessor amsTransferSocialAgentProcessor;
	private static AmsDepositUpdateProcessor amsDepositUpdateProcessor;
	private static AmsWithdrawalCancelProcessor amsWithdrawalCancelProcessor;
	private static AmsDepositProcessor amsDepositProcessor;
	
	private static AmsCustomerPaymentInfoProcessor amsCustomerPaymentInfoProcessor;
	
	//News
	private static AmsCustomerNewsProcessor amsCustomerNewsProcessor;
	private static AmsCustomerAgreementNewsProcessor amsCustomerAgreementNewsProcessor;
	private static AmsCustomerNewsUpdateProcessor amsCustomerNewsUpdateProcessor;
	private static BjpLoadFileTimer bjpLoadFileTimer;
	
	//Publisher
	private static AmsCustomerResponsePublisher amsCustomerResponsePublisher;
	private static AmsCustomerResponsePublisher amsCustomerReportResponsePublisher;
	private static AmsTransactionInfoPublisher amsTransactionInfoPublisher;
	
	//Social JMS
	private static AmsCustomerResponsePublisher amsCustomerBEResponsePublisher;
	
	private static ServerSocketThread serverSocketCtrl;
	
	private static Properties errMsgs;
	
	public static void start(ApplicationContext appContext) {
		try {
			AmsApiControllerMng.appContext = appContext;
			configuration = (Configuration) appContext.getBean("amsConfiguration");
			log.info("Loaded " + configuration);
			
			errMsgs = (Properties) appContext.getBean("errMsgs");
			SystemPropertyConfig.setErrMsgs(errMsgs);
			SystemPropertyConfig.setConfigMode(SystemPropertyConfig.CONFIG_MODE_AMS_API);
			log.info("Loaded Error Message: " + errMsgs);
			
			SystemPropertyConfig.loadListCustomerTestInternalSc();
			log.info("Loaded list customerTestInternalSc: " + SystemPropertyConfig.getListCustomerTestInternalSc());
			
			//Init security
			SecurityMananger.init(configuration);
			
			ISystemPropertyManager systemPropertyManager = (ISystemPropertyManager) appContext.getBean("ISystemPropertyManager");
			systemPropertyManager.loadData();
			systemPropertyManager.loadProperties();
			
			dataCache = (DataCache) appContext.getBean("dataCache");
			dataCache.init();
			
			//Init API Publisher
			amsCustomerResponsePublisher = (AmsCustomerResponsePublisher) appContext.getBean("amsCustomerResponsePublisher");
			amsCustomerReportResponsePublisher = (AmsCustomerResponsePublisher) appContext.getBean("amsCustomerReportResponsePublisher");
			amsCustomerBEResponsePublisher = (AmsCustomerResponsePublisher) appContext.getBean("amsCustomerBEResponsePublisher");
			amsTransactionInfoPublisher = (AmsTransactionInfoPublisher) appContext.getBean("amsTransactionInfoPublisher");
			
			//Init amsSerializeTransactionMananger
			amsSerializeTransactionMananger = (AmsSerializeTransactionMananger) appContext.getBean("amsSerializeTransactionMananger");
			amsSerializeTransactionMananger.start();
			
			//Init API Processor
			//Customer
			amsCustomerInfoProcessor = (AmsCustomerInfoProcessor) appContext.getBean("amsCustomerInfoProcessor");
			amsCustomerBalanceProcessor = (AmsCustomerBalanceProcessor) appContext.getBean("amsCustomerBalanceProcessor");
			amsCustomerInfoUpdateProcessor = (AmsCustomerInfoUpdateProcessor) appContext.getBean("amsCustomerInfoUpdateProcessor");
			amsCustomerPaymentUpdateProcessor = (AmsCustomerPaymentUpdateProcessor) appContext.getBean("amsCustomerPaymentUpdateProcessor");
			amsCustomerBoTestUpdateProcessor = (AmsCustomerBoTestUpdateProcessor) appContext.getBean("amsCustomerBoTestUpdateProcessor");
			amsBoAdditionalInfoUpdateProcessor = (AmsBoAdditionalInfoUpdateProcessor) appContext.getBean("amsBoAdditionalInfoUpdateProcessor");
			amsCustomerReportsProcessor = (AmsCustomerReportsProcessor) appContext.getBean("amsCustomerReportsProcessor");
			amsCustomerRegisterSocialProcessor = (AmsCustomerRegisterSocialProcessor) appContext.getBean("amsCustomerRegisterSocialProcessor");
			amsCustomerModifySocialProcessor  = (AmsCustomerModifySocialProcessor) appContext.getBean("amsCustomerModifySocialProcessor");
			amsCustomerCloseSocialProcessor = (AmsCustomerCloseSocialProcessor) appContext.getBean("amsCustomerCloseSocialProcessor");
			amsSocialSendMailProcessor = (AmsSocialSendMailProcessor) appContext.getBean("amsSocialSendMailProcessor");
			
			//Transfer
			amsWithdrawalProcessor = (AmsWithdrawalProcessor) appContext.getBean("amsWithdrawalProcessor");
			amsWithdrawalCancelProcessor = (AmsWithdrawalCancelProcessor) appContext.getBean("amsWithdrawalCancelProcessor");
			amsTransferProcessor = (AmsTransferProcessor) appContext.getBean("amsTransferProcessor");
			amsTransferSocialAgentProcessor = (AmsTransferSocialAgentProcessor) appContext.getBean("amsTransferSocialAgentProcessor");
			amsDepositProcessor = (AmsDepositProcessor) appContext.getBean("amsDepositProcessor");
			amsDepositUpdateProcessor = (AmsDepositUpdateProcessor) appContext.getBean("amsDepositUpdateProcessor");
			amsCustomerPaymentInfoProcessor = (AmsCustomerPaymentInfoProcessor) appContext.getBean("amsCustomerPaymentInfoProcessor");
			
			//News
			amsCustomerNewsProcessor = ((AmsCustomerNewsProcessor) appContext.getBean("amsCustomerNewsProcessor"));
			amsCustomerAgreementNewsProcessor = ((AmsCustomerAgreementNewsProcessor) appContext.getBean("amsCustomerAgreementNewsProcessor"));
			amsCustomerNewsUpdateProcessor = (AmsCustomerNewsUpdateProcessor) appContext.getBean("amsCustomerNewsUpdateProcessor");
			
			log.info("AmsApiMode: " + configuration.getAmsApiMode());
			log.info("---------------------------");
			
			//Start Processor
			//Mode customer info (getCustomerInfo, balance, update customer, news...)
			if(configuration.hasAmsApiMode(AmsApiMode.CUSTOMER_INFO)) {
				log.info("Start service for mode: " + AmsApiMode.CUSTOMER_INFO);
				
				amsCustomerInfoProcessor.start("AmsCustomerInfo", configuration.getProfileUpdatePoolSize());
				amsCustomerBalanceProcessor.start("AmsCustomerBalance", configuration.getProfileUpdatePoolSize());
				amsCustomerInfoUpdateProcessor.start("AmsCustomerInfoUpdate", configuration.getProfileUpdatePoolSize());
				amsCustomerPaymentUpdateProcessor.start("AmsCustomerPaymentUpdate", configuration.getProfileUpdatePoolSize());
				amsCustomerBoTestUpdateProcessor.start("AmsCustomerBoTestUpdate", configuration.getAgreementPoolSize());
				amsBoAdditionalInfoUpdateProcessor.start("AmsBoAdditionalInfoUpdate", configuration.getProfileUpdatePoolSize());
				
				//start news
				amsCustomerNewsUpdateProcessor.start("AmsCustomerNewsUpdate", configuration.getAgreementPoolSize());
				amsCustomerNewsProcessor.start("AmsCustomerNews", configuration.getAgreementPoolSize());
				amsCustomerAgreementNewsProcessor.start("AmsCustomerAgreementNews", configuration.getAgreementPoolSize());
				
				//start customer report
				amsCustomerReportsProcessor.start("AmsCustomerReports", configuration.getAgreementPoolSize());
				amsCustomerRegisterSocialProcessor.start("AmsCustomerRegisterSocial", configuration.getProfileUpdatePoolSize());
				amsCustomerModifySocialProcessor.start("AmsCustomerModifySocial", configuration.getProfileUpdatePoolSize());
				amsCustomerCloseSocialProcessor.start("AmsCustomerCloseSocial", configuration.getProfileUpdatePoolSize());
				amsSocialSendMailProcessor.start("SocialSendMail", configuration.getProfileUpdatePoolSize());
				log.info("---------------------------");
			}
			
			if(configuration.hasAmsApiMode(AmsApiMode.TRANSFER)) {
				log.info("Start service for mode: " + AmsApiMode.TRANSFER);
				
				amsWithdrawalProcessor.start("AmsWithdrawal", configuration.getWithdrawalPoolSize());
				amsWithdrawalCancelProcessor.start("AmsWithdrawalCancel", configuration.getWithdrawalPoolSize());
				amsTransferProcessor.start("AmsTransfer", configuration.getTransferPoolSize());
				amsTransferSocialAgentProcessor.start("AmsTransferSocial", configuration.getTransferPoolSize());
				amsDepositProcessor.start("AmsDeposit", configuration.getDepositPoolSize());
				amsDepositUpdateProcessor.start("AmsDepositUpdate", configuration.getDepositPoolSize());
				amsCustomerPaymentInfoProcessor.start("AmsCustomerPaymentInfo", configuration.getAgreementPoolSize());
				log.info("---------------------------");
				
				//Start Timer
				bjpLoadFileTimer = ((BjpLoadFileTimer) appContext.getBean("bjpLoadFileTimer"));
				bjpLoadFileTimer.bjpInitialized();
			}
			
			started = true;
			
			//Connect to social
			SCManager.getInstance().initConnectionToServer();
			SCManager.getInstance().addListener(new ScCustomerListener());
			SCManager.getInstance().addListener(new ScFeedingListener());
			SCManager.getInstance().addListener(new ScTradingListener());
			SCManager.getInstance().start();
						
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			//Start Service Controller (Stop/Start service)
			serverSocketCtrl = (ServerSocketThread) appContext.getBean("serverSocketCtrl");
			serverSocketCtrl.start();
		}

	}
	
	public static void stop() {
		try {
			amsCustomerInfoProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerBalanceProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerInfoUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerNewsUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			amsBoAdditionalInfoUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerBoTestUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerNewsProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerAgreementNewsProcessor.stop(configuration.getStopServiceTimeOut());
			amsWithdrawalCancelProcessor.stop(configuration.getStopServiceTimeOut());
			amsTransferProcessor.stop(configuration.getStopServiceTimeOut());
			amsTransferSocialAgentProcessor.stop(configuration.getStopServiceTimeOut());
			amsDepositProcessor.stop(configuration.getStopServiceTimeOut());
			amsDepositUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerPaymentInfoProcessor.stop(configuration.getStopServiceTimeOut());
			amsCustomerPaymentUpdateProcessor.stop(configuration.getStopServiceTimeOut());
			serverSocketCtrl.stopProcessing();
			((ClassPathXmlApplicationContext)appContext).close();
		} catch (Exception e) {
			
		}		
	}
	
	public static AmsCustomerInfoProcessor getAmsProfileProcessor() {
		return amsCustomerInfoProcessor;
	}

	public static void setAmsProfileProcessor(AmsCustomerInfoProcessor amsProfileProcessor) {
		AmsApiControllerMng.amsCustomerInfoProcessor = amsProfileProcessor;
	}

	public static AmsCustomerBalanceProcessor getAmsCustomerBalanceProcessor() {
		return amsCustomerBalanceProcessor;
	}

	public static void setAmsCustomerBalanceProcessor(
			AmsCustomerBalanceProcessor amsCustomerBalanceProcessor) {
		AmsApiControllerMng.amsCustomerBalanceProcessor = amsCustomerBalanceProcessor;
	}

	public static AmsCustomerResponsePublisher getAmsCustomerResponsePublisher() {
		return amsCustomerResponsePublisher;
	}

	public static AmsCustomerResponsePublisher getAmsCustomerReportResponsePublisher() {
		return amsCustomerReportResponsePublisher;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		AmsApiControllerMng.configuration = configuration;
	}

	public static AmsCustomerInfoUpdateProcessor getAmsCustomerInfoUpdateProcessor() {
		return amsCustomerInfoUpdateProcessor;
	}

	public static void setAmsCustomerInfoUpdateProcessor(
			AmsCustomerInfoUpdateProcessor amsCustomerInfoUpdateProcessor) {
		AmsApiControllerMng.amsCustomerInfoUpdateProcessor = amsCustomerInfoUpdateProcessor;
	}

	public static AmsWithdrawalProcessor getAmsWithdrawalProcessor() {
		return amsWithdrawalProcessor;
	}

	public static void setAmsWithdrawalProcessor(AmsWithdrawalProcessor amsWithdrawalProcessor) {
		AmsApiControllerMng.amsWithdrawalProcessor = amsWithdrawalProcessor;
	}

	public static AmsTransferProcessor getAmsTransferProcessor() {
		return amsTransferProcessor;
	}

	public static void setAmsTransferProcessor(AmsTransferProcessor amsTransferProcessor) {
		AmsApiControllerMng.amsTransferProcessor = amsTransferProcessor;
	}

	public static AmsTransferSocialAgentProcessor getAmsTransferSocialAgentProcessor() {
		return amsTransferSocialAgentProcessor;
	}

	public static void setAmsTransferProcessor(AmsTransferSocialAgentProcessor amsTransferSocialAgentProcessor) {
		AmsApiControllerMng.amsTransferSocialAgentProcessor = amsTransferSocialAgentProcessor;
	}
	
	
	public static AmsDepositProcessor getAmsDepositProcessor() {
		return amsDepositProcessor;
	}

	public static void setAmsDepositProcessor(AmsDepositProcessor amsDepositProcessor) {
		AmsApiControllerMng.amsDepositProcessor = amsDepositProcessor;
	}

	public static AmsDepositUpdateProcessor getAmsDepositUpdateProcessor() {
		return amsDepositUpdateProcessor;
	}

	public static void setAmsDepositUpdateProcessor(
			AmsDepositUpdateProcessor amsDepositUpdateProcessor) {
		AmsApiControllerMng.amsDepositUpdateProcessor = amsDepositUpdateProcessor;
	}

	public static AmsTransactionInfoPublisher getAmsTransactionInfoPublisher() {
		return amsTransactionInfoPublisher;
	}

	public static AmsCustomerNewsUpdateProcessor getAmsCustomerNewsUpdateProcessor() {
		return amsCustomerNewsUpdateProcessor;
	}

	public static void setAmsCustomerNewsUpdateProcessor(
			AmsCustomerNewsUpdateProcessor amsCustomerNewsUpdateProcessor) {
		AmsApiControllerMng.amsCustomerNewsUpdateProcessor = amsCustomerNewsUpdateProcessor;
	}

	public static AmsCustomerAgreementNewsProcessor getAmsCustomerAgreementNewsProcessor() {
		return amsCustomerAgreementNewsProcessor;
	}

	public static void setAmsTransactionInfoPublisher(
			AmsTransactionInfoPublisher amsTransactionInfoPublisher) {
		AmsApiControllerMng.amsTransactionInfoPublisher = amsTransactionInfoPublisher;
	}

	public static AmsCustomerNewsProcessor getAmsCustomerNewsProcessor() {
		return amsCustomerNewsProcessor;
	}

	public static void setAmsCustomerNewsProcessor(
			AmsCustomerNewsProcessor amsCustomerNewsProcessor) {
		AmsApiControllerMng.amsCustomerNewsProcessor = amsCustomerNewsProcessor;
	}

	public static AmsWithdrawalCancelProcessor getAmsWithdrawalCancelProcessor() {
		return amsWithdrawalCancelProcessor;
	}

	public static BjpLoadFileTimer getBjpLoadFileTimer() {
		return bjpLoadFileTimer;
	}

	public static void setBjpLoadFileTimer(BjpLoadFileTimer bjpLoadFileTimer) {
		AmsApiControllerMng.bjpLoadFileTimer = bjpLoadFileTimer;
	}

	public static void setAmsWithdrawalCancelProcessor(
			AmsWithdrawalCancelProcessor amsWithdrawalCancelProcessor) {
		AmsApiControllerMng.amsWithdrawalCancelProcessor = amsWithdrawalCancelProcessor;
	}
	
	public static String getMsg(String msgCode) {
		if(msgCode == null)
			return msgCode;
		return errMsgs.getProperty(msgCode, msgCode);
	}

	public static boolean isStarted() {
		return started;
	}

	public static void setStarted(boolean started) {
		AmsApiControllerMng.started = started;
	}

	public static AmsCustomerPaymentInfoProcessor getAmsCustomerPaymentInfoProcessor() {
		return amsCustomerPaymentInfoProcessor;
	}

	public static void setAmsCustomerPaymentInfoProcessor(
			AmsCustomerPaymentInfoProcessor amsCustomerPaymentInfoProcessor) {
		AmsApiControllerMng.amsCustomerPaymentInfoProcessor = amsCustomerPaymentInfoProcessor;
	}

	public static AmsCustomerInfoProcessor getAmsCustomerInfoProcessor() {
		return amsCustomerInfoProcessor;
	}

	public static void setAmsCustomerInfoProcessor(
			AmsCustomerInfoProcessor amsCustomerInfoProcessor) {
		AmsApiControllerMng.amsCustomerInfoProcessor = amsCustomerInfoProcessor;
	}

	public static AmsCustomerPaymentUpdateProcessor getAmsCustomerPaymentUpdateProcessor() {
		return amsCustomerPaymentUpdateProcessor;
	}

	public static void setAmsCustomerPaymentUpdateProcessor(
			AmsCustomerPaymentUpdateProcessor amsCustomerPaymentUpdateProcessor) {
		AmsApiControllerMng.amsCustomerPaymentUpdateProcessor = amsCustomerPaymentUpdateProcessor;
	}

	public static AmsCustomerBoTestUpdateProcessor getAmsCustomerBoTestUpdateProcessor() {
		return amsCustomerBoTestUpdateProcessor;
	}

	public static AmsBoAdditionalInfoUpdateProcessor getAmsBoAdditionalInfoUpdateProcessor() {
		return amsBoAdditionalInfoUpdateProcessor;
	}
	
	public static void setAmsCustomerBoTestUpdateProcessor(
			AmsCustomerBoTestUpdateProcessor amsCustomerBoTestUpdateProcessor) {
		AmsApiControllerMng.amsCustomerBoTestUpdateProcessor = amsCustomerBoTestUpdateProcessor;
	}

	public static AmsSerializeTransactionMananger getAmsSerializeTransactionMananger() {
		return amsSerializeTransactionMananger;
	}

	public static void setAmsSerializeTransactionMananger(
			AmsSerializeTransactionMananger amsSerializeTransactionMananger) {
		AmsApiControllerMng.amsSerializeTransactionMananger = amsSerializeTransactionMananger;
	}

	public static AmsCustomerReportsProcessor getAmsCustomerReportsProcessor() {
		return amsCustomerReportsProcessor;
	}

	public static void setAmsCustomerReportsProcessor(
			AmsCustomerReportsProcessor amsCustomerReportsProcessor) {
		AmsApiControllerMng.amsCustomerReportsProcessor = amsCustomerReportsProcessor;
	}

	public static DataCache getDataCache() {
		return dataCache;
	}
	public static AmsCustomerRegisterSocialProcessor getAmsCustomerRegisterSocialProcessor() {
		return amsCustomerRegisterSocialProcessor;
	}

	public static void setAmsCustomerRegisterSocialProcessor(
			AmsCustomerRegisterSocialProcessor amsCustomerRegisterSocialProcessor) {
		AmsApiControllerMng.amsCustomerRegisterSocialProcessor = amsCustomerRegisterSocialProcessor;
	}

	public static AmsCustomerModifySocialProcessor getAmsCustomerModifySocialProcessor() {
		return amsCustomerModifySocialProcessor;
	}

	public static void setAmsCustomerModifySocialProcessor(
			AmsCustomerModifySocialProcessor amsCustomerModifySocialProcessor) {
		AmsApiControllerMng.amsCustomerModifySocialProcessor = amsCustomerModifySocialProcessor;
	}

	public static AmsCustomerResponsePublisher getAmsCustomerBEResponsePublisher() {
		return amsCustomerBEResponsePublisher;
	}

	public static AmsCustomerCloseSocialProcessor getAmsCustomerCloseSocialProcessor() {
		return amsCustomerCloseSocialProcessor;
	}

	public static void setAmsCustomerCloseSocialProcessor(
			AmsCustomerCloseSocialProcessor amsCustomerCloseSocialProcessor) {
		AmsApiControllerMng.amsCustomerCloseSocialProcessor = amsCustomerCloseSocialProcessor;
	}

	public static AmsSocialSendMailProcessor getAmsSocialSendMailProcessor() {
		return amsSocialSendMailProcessor;
	}

	public static void setAmsSocialSendMailProcessor(
			AmsSocialSendMailProcessor amsSocialSendMailProcessor) {
		AmsApiControllerMng.amsSocialSendMailProcessor = amsSocialSendMailProcessor;
	}
}