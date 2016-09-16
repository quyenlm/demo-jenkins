package phn.nts.ams.fe.web.action.account;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.TrsStringUtil;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ITestBoManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BoTestInfo;
import phn.nts.ams.fe.domain.BoTestResult;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.TestBoModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

public class TestBoAction extends BaseSocialAction<TestBoModel>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3210082816681562656L;
	private static Logit log = Logit.getInstance(TestBoAction.class);
    private TestBoModel model = new TestBoModel();
    private ITestBoManager testBoManager = null;
    private IAccountManager accountManager;
    private static Properties propsConfig;
    private static final String CONFIGPATH = "configs.properties";
    private static final String WEBTRADE_URL = "webtrade.url";
    
	
	public String initBoTest(){
		try{
			log.info("begin init test bo");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			String frontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
			if(frontUserDetails!=null){
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
					int status = customerServiceInfoAccount.getCustomerServiceStatus();
					if(status == ITrsConstants.ACCOUNT_OPEN_STATUS.WAITING_ADD_ACCOUNT){
						Integer boTestStatus = testBoManager.getBoCustomerStatus(frontUserOnline.getUserId());
						if(checkBoTestStatus(boTestStatus, customerServiceInfoAccount.getCustomerServiceId(), frontDate)){
							return INPUT;
						}else{
							return ERROR;
						}
					}else if (testBoManager.checkBoTestComplete(customerServiceInfoAccount.getCustomerServiceId())) {
						return "home";
					}
				}
			}else{
				return ERROR;
			}
			
			return INPUT;
		}catch(Exception ex){
			log.error(ex,ex);
			return ERROR;
		}
	}
	
	public boolean checkBoTestStatus(Integer boTestStatus,String customerId,String frontDate){
		boolean flag = true;
		if(ITrsConstants.BO_TEST_STATUS.TEST_ALLOWED.equals(boTestStatus)){
			flag =  true;	
		}else if(ITrsConstants.BO_TEST_STATUS.TEST_DISABLE.equals(boTestStatus)){
			model.setTestToday(2);
			flag =  false;
		}
		
		if(testBoManager.checkBoTestToDay(customerId,frontDate)){
			model.setTestToday(1);
			model.setWarning(getText("nts.ams.fe.testbo.warning",new String[]{DateUtil.convertBetweenDateFormat(frontDate, DateUtil.PATTERN_YYMMDD_BLANK, DateUtil.PATTERN_YYMMDD)}));
			flag = false;
		} 
		model.setTestAcceptFlag(false);
		return flag;
	}
	
	public String agreeBoTest(){
		try{
			log.info("begin agree test bo");
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			String frontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
			if(frontUserDetails!=null){
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
					Integer boTestStatus = testBoManager.getBoCustomerStatus(frontUserOnline.getUserId());
					if(checkBoTestStatus(boTestStatus, customerServiceInfoAccount.getCustomerServiceId(), frontDate)){
						boolean flag = testBoManager.insertAmsTestSummary(customerServiceInfoAccount,frontDate);
						String wlCode = frontUserOnline.getWlCode();
						String customerServiceId = customerServiceInfoAccount.getCustomerServiceId();
						if(flag){
							if(model.getListTest()==null){
								List<BoTestInfo> listTest = testBoManager.getListBoTest(frontDate,wlCode,customerServiceId);
								if (listTest == null || listTest.size() == 0) {
									boolean statusGenerate = testBoManager.generatePackageTest(customerServiceId, customerServiceInfoAccount.getWlCode());
									if(statusGenerate){
										listTest = testBoManager.getListBoTest(frontDate,wlCode,customerServiceId);
									} else {
										return ERROR;
									}
								}
								getModel().setListTest(listTest);
							}
						}
					} else {
						return ERROR;
					}
				}
			}
			model.setTestAcceptFlag(true);
			return SUCCESS;
		}catch(Exception ex){
			log.error(ex, ex);
			return ERROR;
		}
	}
	
	public String confirmTest(){
		try{
			log.info("begin confirm test bo");
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			String frontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
			if(frontUserDetails!=null){
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
					Integer boTestStatus = testBoManager.getBoCustomerStatus(frontUserOnline.getUserId());
					if(checkBoTestStatus(boTestStatus, customerServiceInfoAccount.getCustomerServiceId(), frontDate)){
						List<BoTestInfo> listTestInfos = getModel().getListTest();
						if(listTestInfos!=null){
							int count = 0;
							for(BoTestInfo boTest : listTestInfos){
								if(boTest.getCustomerAnswer()==null){
									count += 1;
								}
							}
							if(count > 0){
								model.setErrorMessage(getText("MSG_TRS_NAF_0004"));
								return ERROR;
							}
							model.setListTest(listTestInfos);
						}else{
							return ERROR;
						}
					}
					else
						return ERROR;
				}
			}
			
			return SUCCESS;
		}catch(Exception ex){
			log.error(ex,ex);
			return ERROR;
		}
	}
	
	public String submitTest(){
		try{
			log.info("begin submit test bo");
			try {
				propsConfig = Helpers.getProperties(CONFIGPATH);
			} catch (FileNotFoundException e) {
				log.error("cannot read config.properties");
			} 
			String webTradeUrl = propsConfig.getProperty(WEBTRADE_URL);
	 		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails!=null){
				frontUserOnline = frontUserDetails.getFrontUserOnline();
			}
			Integer boTestStatus = testBoManager.getBoCustomerStatus(frontUserOnline.getUserId());
			List<BoTestInfo> listTestInfos = getModel().getListTest();
			String minpoint = testBoManager.getMinPointPass(frontUserOnline.getWlCode());
			CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.BO);
			String customerId = frontUserOnline.getUserId();
			String customerServiceId = customerServiceInfoAccount.getCustomerServiceId();
			String frontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
			Map<String,BoTestResult> mapResult = new LinkedHashMap<String, BoTestResult>();
			Map<String, String> mapQuestionSubject = getTextMap(MasterDataManagerImpl.getInstance().getImmutableData().getMapQuestionSubject());
			Map<String, String> textMapQuestionSubject = new LinkedHashMap<String, String>();
			Map<String,String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + frontUserOnline.getWlCode());
			String questionSort = ITrsConstants.DEFAULT_VALUE.QUESTION_SUBJECT_SORT;
			if(mapConfig!=null && mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_SUBJECT_SORT) != null){
				questionSort = mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_SUBJECT_SORT);
			}else{
				log.info("can not get config question subject sort");
			}
	    	if(mapQuestionSubject!= null && mapQuestionSubject.size() > 0) {
	    		String[] subjectSort = questionSort.split(",");
	    		if(subjectSort!=null && subjectSort.length > 0){
		    		for(String key : subjectSort) {
		    			textMapQuestionSubject.put(getText(key), getText(mapQuestionSubject.get(key)));
		    		}
	    		}
	    	}
	    	model.setTextMap(textMapQuestionSubject);
			int min=0;
			int point = 0;
			int totalPoint = 0;
			if(!StringUtil.isEmpty(minpoint)){
				min = Integer.parseInt(minpoint);
			}
			if(!checkBoTestStatus(boTestStatus, customerServiceId, frontDate)){	
				return ERROR;
			}else {
				if(listTestInfos!=null){
					for(BoTestInfo info : listTestInfos){
						if(info.getAnswer().equals(info.getCustomerAnswer())){
							testBoManager.insertCustomerTest(customerServiceId, 1, info);
							if(mapResult.get(info.getSubject())!=null){
								BoTestResult boTest = mapResult.get(info.getSubject());
								boTest.setCorrectAnswer(boTest.getCorrectAnswer()+1);
								boTest.setTotalAnswer(boTest.getTotalAnswer()+1);
							}else{
								String subject = info.getSubject();
								mapResult.put(subject, new BoTestResult(1,1));
							}
							point += 1;
						} else {
							testBoManager.insertCustomerTest(customerServiceId, 0, info);
							if(mapResult.get(info.getSubject())!=null){
								BoTestResult boTest = mapResult.get(info.getSubject());
								boTest.setTotalAnswer(boTest.getTotalAnswer()+1);
							}else{
								String subject = info.getSubject();
								mapResult.put(subject, new BoTestResult(0,1));
							}
						}
						totalPoint += 1;
					}
					float testPercent = 0;
					if(point > 0 && totalPoint > 0){
						testPercent = ((float)point/totalPoint) * 100;
					}
					if(testPercent >= min){
						testBoManager.updateAmsTestSummary(customerServiceId, frontDate, point, listTestInfos.size(), 2);
						try {
//							testBoManager.updateCustomer(customerId,customerServiceId,frontUserOnline.getWlCode(),frontUserOnline.getFullName());
						} catch (Exception e) {
							log.error("ERROR", e);
							model.setTestResult(mapResult);
							return "fail";
						}
						model.setUrl(webTradeUrl);
						model.setTestResult(mapResult);
						return SUCCESS;
					}else{
						testBoManager.updateAmsTestSummary(customerServiceId, frontDate, point, listTestInfos.size(), 1);
						model.setTestResult(mapResult);
						return "fail";
					}
				}else{
					return INPUT;
				}
			}
		}catch(Exception ex){
			log.error(ex, ex);
			return ERROR;
		}
	}
	
	public ITestBoManager getTestBoManager() {
		return testBoManager;
	}

	public void setTestBoManager(ITestBoManager testBoManager) {
		this.testBoManager = testBoManager;
	}

	public void setModel(TestBoModel model) {
		this.model = model;
	}

	public TestBoModel getModel() {
		return model;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

}
