package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.jfree.util.Log;

import phn.com.components.trs.api.CRMIntegrationAPI;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsCustomerTestDAO;
import phn.com.nts.db.dao.IAmsCustomerTraceDAO;
import phn.com.nts.db.dao.IAmsPackageTestDAO;
import phn.com.nts.db.dao.IAmsQuestionnaireDAO;
import phn.com.nts.db.dao.IAmsTestSummaryDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IBoCustomerDAO;
import phn.com.nts.db.dao.IRptUsedBoTestDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.domain.BoTestDetail;
import phn.com.nts.db.domain.QuestionDataInfo;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsCustomerTest;
import phn.com.nts.db.entity.AmsCustomerTestId;
import phn.com.nts.db.entity.AmsCustomerTrace;
import phn.com.nts.db.entity.AmsPackageTest;
import phn.com.nts.db.entity.AmsPackageTestId;
import phn.com.nts.db.entity.AmsQuestionnaire;
import phn.com.nts.db.entity.AmsSubGroup;
import phn.com.nts.db.entity.AmsTestSummary;
import phn.com.nts.db.entity.AmsTestSummaryId;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.BoCustomer;
import phn.com.nts.db.entity.RptUsedBoTest;
import phn.com.nts.db.entity.RptUsedBoTestId;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.TrsStringUtil;
import phn.com.trs.util.enums.BoServiceStatusEnum;
import phn.com.trs.util.enums.EnumResultTest;
import phn.nts.ams.fe.business.ITestBoManager;
import phn.nts.ams.fe.common.AbstractManager;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BoTestInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.jms.managers.BoManager;
import phn.nts.ams.fe.model.TestBoModel;

import com.nts.common.Constant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;
import com.nts.components.mail.bean.MailTemplate;
import com.phn.bo.admin.message.AdminAccountDetailsUpdate;
import com.phn.bo.exchange.bean.AccountInfo;

public class TestBoManagerImpl extends AbstractManager implements ITestBoManager {
	private static Logit log = Logit.getInstance(TestBoManagerImpl.class);
	private final String SEPARATOR_USED_BO_TEST = ",";
	
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> whiteLableConfig;
	private IBoCustomerDAO<BoCustomer> boCustomerDAO;
	private IAmsTestSummaryDAO<AmsTestSummary> testSummaryDAO;
	private IAmsQuestionnaireDAO<AmsQuestionnaire> amsQuestionnaireDAO;
	private IAmsCustomerTestDAO<AmsCustomerTest> amsCustomerTestDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> amsCustomerServiceDAO;
	private ISysAppDateDAO<SysAppDate> sysAppDateDAO;
	private IAmsPackageTestDAO<AmsPackageTest> amsPackageTestDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private IRptUsedBoTestDAO<RptUsedBoTest> rptUsedBoTestDAO;
	private BoManager boManager;
	private IJmsContextSender jmsContextSender;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
	private IAmsCustomerTraceDAO<AmsCustomerTrace> amsCustomerTraceDAO;


	public String getMinPointPass(String wlCode){
		AmsWhitelabelConfig config = whiteLableConfig.getAmsWhiteLabelConfig(ITrsConstants.WHITE_LABEL_CONFIG.BO_TEST_PERCENT_PASS, wlCode);
		if(config!=null){
			return config.getConfigValue();
		}
		return null;
	}
	
	public Integer getBoCustomerStatus(String customerId){
		List<BoCustomer> list = boCustomerDAO.findByCustomerId(customerId);
		BoCustomer boCustomer = null;
		if(list!=null && list.size() >0){
			boCustomer = list.get(0);
			return boCustomer.getBoTestStatus();
		}
		return null;
	}
	
	public boolean checkBoTestToDay(String customerServiceId,String frontDate){
		AmsTestSummaryId id = new AmsTestSummaryId();
		id.setCustomerServiceId(customerServiceId);
		id.setFrontDate(frontDate);
		AmsTestSummary amsTestSummary = testSummaryDAO.findById(AmsTestSummary.class, id);
		if(amsTestSummary!=null && amsTestSummary.getResultTest() >0){
			return true;
		}
		return false;
	}
	
	public boolean checkBoTestComplete(String customerServiceId){

		AmsTestSummary amsTestSummary = testSummaryDAO.checkCustomerCompleteTest(customerServiceId);
		if(amsTestSummary!=null){
			return true;
		}
		return false;
	}
	
	public boolean insertAmsTestSummary(CustomerServicesInfo info,String frontDate){
		AmsTestSummary amsTestSummary = null;
		AmsTestSummaryId id = new AmsTestSummaryId();
		id.setCustomerServiceId(info.getCustomerServiceId());
		id.setFrontDate(frontDate);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		amsTestSummary = testSummaryDAO.findById(AmsTestSummary.class, id);
		if(amsTestSummary == null){
			amsTestSummary = new AmsTestSummary();
			amsTestSummary.setCustomerId(info.getCustomerId());
			amsTestSummary.setId(id);
			amsTestSummary.setWlCode(info.getWlCode());
			amsTestSummary.setResultTest(0);
			amsTestSummary.setTestPoint(0);
			amsTestSummary.setTotalPoint(new BigDecimal(0));
			amsTestSummary.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsTestSummary.setInputDate(currentTime);
			amsTestSummary.setUpdateDate(currentTime);
			amsTestSummary.setStartExecutionDateTime(currentTime);
			testSummaryDAO.save(amsTestSummary);
		}
		return true;
	}
	
	/**
	 * Get List BoTest　of customerServiceId
	 * 
	 */
	public List<BoTestInfo> getListBoTest(String frontDate, String wlCode, String customerServiceId){
		List<BoTestInfo> listBoTest = new ArrayList<BoTestInfo>();
		List<BoTestDetail> listQues = amsQuestionnaireDAO.getQuestionForTest(frontDate,customerServiceId);
		Map<String,String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
		String questionSort = ITrsConstants.DEFAULT_VALUE.QUESTION_SUBJECT_SORT;
		String typeSort = ITrsConstants.DEFAULT_VALUE.QUESTION_TYPE_SORT;
		
		if(mapConfig!=null && mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_SUBJECT_SORT) != null){
			questionSort = mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_SUBJECT_SORT);
		}else{
			log.info("can not get config question subject sort");
		}
		
		if(mapConfig!=null && mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_TYPE_SORT) != null){
			typeSort = mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.QUESTION_TYPE_SORT);
		}else{
			log.info("can not get config question type sort");
		}
		
		String[] subjects = questionSort.split(",");
		String[] types = typeSort.split(",");
		Map<String,Integer> mapposition = new LinkedHashMap<String, Integer>();
		int pos = 0;
		for(String subject : subjects){
			for(String type : types){
				mapposition.put(subject+type, pos);
				pos++;
			}
		}
		
		if(listQues!=null){
			for(BoTestDetail ques : listQues){
				BoTestInfo info = new BoTestInfo();
				info.setQuestionId(String.valueOf(ques.getQuestionnaireId()));
				info.setQuestion(ques.getQuestion());
				info.setHintLink(ques.getHintLink());
				info.setAnswerMethod(String.valueOf(ques.getAnswerMethod()));
				info.setAnswer(String.valueOf(ques.getAnswer()));
				info.setPackageTestId(String.valueOf(ques.getPackageTestId()));
				info.setSubject(ques.getSubject());
				info.setType(String.valueOf(ques.getType()));
				if(mapposition.get(ques.getSubject()+ques.getType())!=null){
					info.setPos(mapposition.get(ques.getSubject()+ques.getType()));
				}
				listBoTest.add(info);
			}
		}
		
		Collections.sort(listBoTest);
		return listBoTest;
	}
	
	public void insertCustomerTest(String customerServiceId,int point,BoTestInfo info){
		AmsCustomerTest amsCustomerTest = new AmsCustomerTest();
		AmsCustomerTestId id = new AmsCustomerTestId();
		id.setCustomerServiceId(customerServiceId);
		id.setPackageTestId(Integer.parseInt(info.getPackageTestId()));
		id.setQuestionnaireId(Integer.parseInt(info.getQuestionId()));
		amsCustomerTest.setId(id);
		if(!StringUtil.isEmpty(info.getCustomerAnswer())){
			amsCustomerTest.setCustomerAnswer(Integer.parseInt(info.getCustomerAnswer()));
		}else{
			amsCustomerTest.setCustomerAnswer(-1);
		}
		amsCustomerTest.setPoint(point);
		amsCustomerTest.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCustomerTest.setExecutionDatetime(new Timestamp(System.currentTimeMillis()));
		amsCustomerTest.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsCustomerTest.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		amsCustomerTestDAO.save(amsCustomerTest);
	}
	
	public String updateAmsTestSummary(String customerServiceId, String frontDate, int testPoint, int totalPoint, int resultTest){
		//[TRSPT-7540-quyen.le.manh]Jan 21, 2016A - Start - Update AmsTestSummary with frontDate at real time
		
		AmsTestSummaryId id = new AmsTestSummaryId();
		id.setCustomerServiceId(customerServiceId);
		id.setFrontDate(frontDate);
		AmsTestSummary amsTestSummary = testSummaryDAO.findById(AmsTestSummary.class, id);
		
		if(amsTestSummary!=null) {
			amsTestSummary.setResultTest(resultTest);
			amsTestSummary.setTestPoint(testPoint);
			amsTestSummary.setTotalPoint(new BigDecimal(totalPoint));
			amsTestSummary.setEndExecutionDateTime(new Timestamp(System.currentTimeMillis()));
			amsTestSummary.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			
			String curFrontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
			if (frontDate.equals(curFrontDate)) {
				testSummaryDAO.merge(amsTestSummary);
				return frontDate;
			} else {
				AmsTestSummary curTestSummary = amsTestSummary.clone();
				AmsTestSummaryId curId = new AmsTestSummaryId();
				curId.setCustomerServiceId(customerServiceId);
				curId.setFrontDate(curFrontDate);
				curTestSummary.setId(curId);
				
				testSummaryDAO.merge(curTestSummary);
				testSummaryDAO.delete(amsTestSummary);
				return curFrontDate;
			}
		}
		
		//[TRSPT-7540-quyen.le.manh]Jan 21, 2016A - End - Update AmsTestSummary with frontDate at real time
		
		return null;
	}
	
	public void updateCustomer(String customerId, String customerServiceId, String wlCode, String fullname, String summaryFrontDate, ExecutorService executorService) throws Exception {
		
		AmsCustomerSurvey survey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
		if(survey != null){
			//[TRSPT-7251-quyen.le.manh]Dec 29, 2015M - Start - Save BO_TEST_STATUS
			survey.setBoTestStatus(ITrsConstants.BO_TEST_STATUS.TEST_FINISHED);
			survey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			amsCustomerSurveyDAO.merge(survey);
			//[TRSPT-7251-quyen.le.manh]Dec 29, 2015M - End
		}else{
			log.warn("cannot get amscustomer survey of customerid: " + customerId);
		}
		
		final AmsCustomerService customerService = amsCustomerServiceDAO.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.BO);
		if(customerService!=null){
			
			String fdate = sysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate();
			customerService.setCustomerServiceStatus(ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSIT_WAITING);
			customerService.setAccountStatusChangeDate(TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD));
			customerService.setAccountStatusChangeDatetime(new Timestamp(System.currentTimeMillis()));
			customerService.setAccountOpenFinishDate(fdate);
			customerService.setAllowLoginFlg(IConstants.ACTIVE_FLG.ACTIVE);
			customerService.setAllowSendmoneyFlg(IConstants.ACTIVE_FLG.ACTIVE);
			customerService.setAllowTransactFlg(IConstants.ACTIVE_FLG.INACTIVE);
			customerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			amsCustomerServiceDAO.merge(customerService);
			
			// Update REDIS
			boolean resultUpdateBo = updateBoCustomer(customerService, ITrsConstants.BO_TEST_STATUS.TEST_FINISHED);
			if (!resultUpdateBo) {
				throw new Exception("Fail in update BoCustomer");
			}
			
			//[TRSSC-1077-quyen.le.manh]Jul 22, 2016A - Start - optimize BO status sync SalesForce flow
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					//update sale force
					log.info("[start] sync bo test status SalesForce");
					
					boolean flag = CRMIntegrationAPI.syncBoTestStatusSF(customerService, ITrsConstants.BO_TEST_STATUS.TEST_FINISHED);
					if(!flag)
						log.warn("Sync to SalesForce FAIL");
					else
						log.info("Sync to SalesForce SUCCESS");
					log.info("[end] sync bo test status SalesForce");
				}
			});
			//[TRSSC-1077-quyen.le.manh]Jul 22, 2016A - End
			
			AmsTestSummaryId id = new AmsTestSummaryId();
			id.setCustomerServiceId(customerServiceId);
			id.setFrontDate(summaryFrontDate);
			AmsTestSummary summary = testSummaryDAO.findById(AmsTestSummary.class, id);
			sendMailTestComplete(wlCode, customerId, fullname, summary);
			
			//Ref https://nextop-asia.atlassian.net/browse/TRSBO-3149
			//sendMailCustomerTestComplete(survey.getAmsCustomer().getMailMain(), customerServiceId, wlCode, fullname);
			
			//[TRSGAP-487-cuong.bui.manh]Jun 27, 2016D - Start 
//			updateAmsCustomerTrace(summary, fullname);
			//[TRSGAP-487-cuong.bui.manh]Jun 27, 2016D - End
		}
		
//		List<BoCustomer> list = boCustomerDAO.findByCustomerId(customerId);
//		BoCustomer boCustomer = null;
//		if(list!=null && list.size() >0){
//			boCustomer = list.get(0);
//			boCustomer.setBoTestStatus(ITrsConstants.BO_TEST_STATUS.TEST_FINISHED);
//			boCustomer.setCustomerServiceStatus(IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED);
//			boCustomer.setAllowSendMoneyFlg(IConstants.ACTIVE_FLG.INACTIVE);
//			boCustomer.setAllowTransactFlg(IConstants.ACTIVE_FLG.INACTIVE);
//			boCustomerDAO.merge(boCustomer);
//		}
		
	}

	@Override
	public boolean generatePackageTest(String customerServiceId, String wlCode) {
		long t1 = System.currentTimeMillis();
		// 1. Get TEST_DATE 
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String testDate = DateUtil.toString(currentTime, DateUtil.PATTERN_YYMMDD_BLANK);
		// 2. get config for one BO package test
		String key = ITrsConstants.WHITE_LABEL_CONFIG.AMS_PACKAGE_TEST;
		Map<QuestionDataInfo, Integer> mapDefaultPackageTestInfo = getPackageTestInfo(key, wlCode);
		if(mapDefaultPackageTestInfo == null || mapDefaultPackageTestInfo.size() == 0) {
			log.warn("mapDefaultPackageTestInfo is empty");
			return false;
		}
		
		for (QuestionDataInfo each : mapDefaultPackageTestInfo.keySet()) {
			Integer numberDefaultQuest = mapDefaultPackageTestInfo.get(each);
			// 3. Get question  was used package test (trsreport)
			RptUsedBoTestId rptUsedBoTestId = new RptUsedBoTestId(each.getType(), each.getSubject());
			RptUsedBoTest rptUsedBoTest = rptUsedBoTestDAO.findById(RptUsedBoTest.class, rptUsedBoTestId);
			List<Integer> listUsedQuest = new ArrayList<Integer>();
			if (rptUsedBoTest == null || StringUtil.isEmpty(rptUsedBoTest.getQuestionnaireList())) {
				log.info("RptUsedBoTest QuestionnaireList is empty with Type = " + each.getType() + " and Subject = " + each.getSubject());
			} else {
				String[] arrayUsedQuest = rptUsedBoTest.getQuestionnaireList().split(SEPARATOR_USED_BO_TEST);
				for (String eachUsedQuest : arrayUsedQuest) {
					if (!StringUtil.isEmpty(eachUsedQuest)) {
						Integer usedQuest = MathUtil.parseInteger(eachUsedQuest);
						if (usedQuest == null) {
							log.warn("QuestionnaireList has invalid value = " + rptUsedBoTest.getQuestionnaireList());
							return false;
						}
						listUsedQuest.add(usedQuest); 
					}
				}
			}
			listUsedQuest = amsQuestionnaireDAO.getUsedQuestion(listUsedQuest, each.getSubject(), each.getType());
			// 3. Get question for BO pakage Test
			List<Integer> listNewQuest = new ArrayList<Integer>();
			// 3.1 get question for each subject, type
			List<Integer> listUnusedQuest = amsQuestionnaireDAO.getUnUsedQuestion(listUsedQuest, each.getSubject(), each.getType());
			// if total records ( get from step 3.1) >=  [quantity question]
			if (listUnusedQuest != null && listUnusedQuest.size() >= numberDefaultQuest) {
				// add QUESTIONNAIRE_ID on  [list QUESTIONNAIRE_ID] to USED_QUESTION ( sparate by comma)
				int size = listUnusedQuest.size();
				while (size >= 1) {
					int index = (int)(Math.random() * size);
					listNewQuest.add(listUnusedQuest.remove(index));
					size--;
					if (listNewQuest.size() == numberDefaultQuest) {
						break;
					}
				}
			} else {
				if (listUsedQuest.size() + listUnusedQuest.size() < numberDefaultQuest) {
					log.warn("Number of used question and un-used question is less than default number question = " + numberDefaultQuest);
					return false;
				}
				
				listNewQuest.addAll(listUnusedQuest);
				int size = listUsedQuest.size();
				while (size >= 1) {
					int index = (int)(Math.random() * size);
					listNewQuest.add(listUsedQuest.remove(index));
					size--;
					if (listNewQuest.size() == numberDefaultQuest) {
						break;
					}
				}
			}
			// 3.3 insert on AMS_PACKAGE_TEST
			for (Integer eachQuestId : listNewQuest) {
				AmsPackageTest packageTest = new AmsPackageTest();
				Long packageTestId = generateUniqueId(ITrsConstants.UNIQUE_CONTEXT.PACKAGE_TEST);
				AmsPackageTestId id = new AmsPackageTestId(packageTestId, eachQuestId);
				packageTest.setId(id);
				packageTest.setSubject(each.getSubject());
				packageTest.setType(each.getType());
				packageTest.setPublicDate(testDate);
				packageTest.setInputDate(currentTime);
				packageTest.setUpdateDate(currentTime);
				packageTest.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				packageTest.setCustomerServiceId(customerServiceId);
				amsPackageTestDAO.save(packageTest);
			}
			//update RPT_BAT_BO_TEST
			StringBuffer newUsedBoTest = new StringBuffer();
			for (Integer eachQuestId : listUsedQuest) {
				newUsedBoTest.append(SEPARATOR_USED_BO_TEST).append(eachQuestId);
			}
			for (Integer eachQuestId : listNewQuest) {
				newUsedBoTest.append(SEPARATOR_USED_BO_TEST).append(eachQuestId);
			}
			newUsedBoTest.deleteCharAt(0);
			if (rptUsedBoTest == null) {
				rptUsedBoTest = new RptUsedBoTest();
				rptUsedBoTest.setId(rptUsedBoTestId);
				rptUsedBoTest.setQuestionnaireList(newUsedBoTest.toString());
				rptUsedBoTest.setInputDate(currentTime);
				rptUsedBoTest.setUpdateDate(currentTime);
				rptUsedBoTest.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				rptUsedBoTestDAO.save(rptUsedBoTest);
			} else {
				rptUsedBoTest.setQuestionnaireList(newUsedBoTest.toString());
				rptUsedBoTest.setUpdateDate(currentTime);
				rptUsedBoTestDAO.merge(rptUsedBoTest);
			}
		}
		long t2 = System.currentTimeMillis();
		log.info("TestBoManagerImpl.generatePackageTest() Generate Question = " + (t2 - t1));
		return true;
	}
	
	private Map<QuestionDataInfo, Integer> getPackageTestInfo(String key, String wlCode) {
		List<AmsWhitelabelConfig> listWlConfig= whiteLableConfig.getLikeConfigKey(key, wlCode);
		Map<QuestionDataInfo, Integer> multimap = new HashMap<QuestionDataInfo, Integer>();
		if(listWlConfig != null) {
			for (AmsWhitelabelConfig each : listWlConfig) {
				String subject = each.getId().getConfigKey().substring(17);
				Integer type = MathUtil.parseInteger(each.getConfigType());
				if (type == null) {
					Log.warn("ConfigType is invalid = " + each.getConfigType() + " of ConfigKey = " + key);
					return null;
				}
				QuestionDataInfo questionInfo = new QuestionDataInfo(subject, type);
				Integer value = MathUtil.parseInteger(each.getConfigValue());
				multimap.put(questionInfo, value);
			}
		}
		return multimap;
	}
	
	private synchronized Long generateUniqueId(String contextID) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}		
		Long uniqueId = iSysUniqueidCounterDAO.generateId(contextID);
		return uniqueId;
	}
	
	private boolean updateBoCustomer(AmsCustomerService service, Integer boTestStatus) {
		log.info("[start] CustomerServiceManagerImpl.updateBoCustomer()");
		if (service == null) {
			log.warn("AmsCustomerService is null");
			return false;
		}
		AmsSubGroup newAmsSubGroup = service.getAmsSubGroup();
		if (newAmsSubGroup == null) {
			log.warn("AmsSubGroup is null");
			return false;
		}
		Integer newServiceStatus = service.getCustomerServiceStatus();
		String wlCode = null;
		AmsWhitelabel amsWl = service.getAmsWhitelabel();
		if (amsWl != null) {
			wlCode = amsWl.getWlCode();
		}
		if (StringUtil.isEmpty(wlCode)) {
			log.warn("Cannot get WlCode of CustomerServiceId = " + service.getCustomerServiceId());
			return false;
		}
		AmsCustomer amsCustomer = service.getAmsCustomer();
		if(amsCustomer ==null){
			log.warn("AmsCustomer is null");
		}
		AccountInfo jmsUpdateAccount = new AccountInfo();
		jmsUpdateAccount.setAllowTransactFlg(service.getAllowTransactFlg());
		jmsUpdateAccount.setAllowLoginFlg(service.getAllowLoginFlg());
		jmsUpdateAccount.setCustomserServiceStatus(service.getCustomerServiceStatus());
		jmsUpdateAccount.setCustomerServiceId(service.getCustomerServiceId());
		jmsUpdateAccount.setSubGroupCode(newAmsSubGroup.getSubGroupCode());
		jmsUpdateAccount.setSubGroupName(newAmsSubGroup.getSubGroupName());
		jmsUpdateAccount.setSubGroupId(StringUtil.toString(newAmsSubGroup.getSubGroupId()));
		jmsUpdateAccount.setWlCode(wlCode);
		jmsUpdateAccount.setCustomserServiceStatus(newServiceStatus);
		jmsUpdateAccount.setAccountOpenDate(service.getAccountOpenDate());
		jmsUpdateAccount.setAccountCancelDate(service.getAccountCancelDate());
		jmsUpdateAccount.setAccountStatusChangeDate(service.getAccountStatusChangeDate());
		jmsUpdateAccount.setAccountStatusChangeDateTime(service.getAccountStatusChangeDatetime());
		jmsUpdateAccount.setAccountOpenFinishDate(service.getAccountOpenFinishDate());
		jmsUpdateAccount.setAllowSendMoneyFlg(service.getAllowSendmoneyFlg());
		jmsUpdateAccount.setBoTestStatus(boTestStatus);
		jmsUpdateAccount.setAddress(amsCustomer.getAddress());
		jmsUpdateAccount.setFullName(amsCustomer.getFullName());
		jmsUpdateAccount.setLoginId(amsCustomer.getLoginId());
		jmsUpdateAccount.setMailMain(amsCustomer.getMailMain());
		jmsUpdateAccount.setBirthday(amsCustomer.getBirthday());
		try {
			AdminAccountDetailsUpdate receivedUpdateBo = boManager.updateBoDetail(jmsUpdateAccount);
			if (receivedUpdateBo == null || receivedUpdateBo.getResult() == Constant.ADMIN_MSG_RESULT_FAIL) {
				log.warn("Can not update Bo detail by JMS");
				return false;
			}
		} catch (Exception e) {
			log.error("ERROR", e);
			return false;
		}
		log.info("[end] CustomerServiceManagerImpl.updateBoCustomer()");
		return true;
	}
	
	private void sendMailTestComplete(String wlCode, String customerId, String fullName, AmsTestSummary amsTestSummary) {
		log.info("[start] send mail about complete test successful");
		String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_OPEN_BO_TRADE_CS;
		AmsWhitelabelConfig config = MasterDataManagerImpl.getWhitelabelConfigDAO().getAmsWhiteLabelConfig("MAIL_CS", wlCode);
		MailTemplate amsMailTemplateInfo = new MailTemplate();
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(SystemPropertyConfig.getInstance().getText("mail.test.subject"));
		amsMailTemplateInfo.setWlCode(wlCode);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(config.getConfigValue(), config.getConfigValue());
		amsMailTemplateInfo.setTo(to);
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("customerId", customerId);
		content.put("finishedDate", amsTestSummary.getEndExecutionDateTime());
		content.put("result", MasterDataManagerImpl.getInstance().getText(EnumResultTest.getValueById(amsTestSummary.getResultTest())));
		content.put("point", amsTestSummary.getTestPoint());
		content.put("testCount", getTestCount(amsTestSummary.getId().getCustomerServiceId()));
		content.put("customerServiceId", amsTestSummary.getId().getCustomerServiceId());
		content.put("fullName", fullName);
		HashMap<String, Object> content2 = new HashMap<String, Object>();
		content2.put("mailInfo", content);
		amsMailTemplateInfo.setContent(content2);
		
		log.info("Mail Content: " + amsMailTemplateInfo);
		
		jmsContextSender.sendMailTemplate(amsMailTemplateInfo);
		log.info("[end] send mail about complete test successful");
	}
	
	private void sendMailCustomerTestComplete(String customerMail, String customerServiceId, String wlCode, String fullName) {
		log.info("[start] send mail customer about complete test successful");
		String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_OPEN_BO_TRADE;
		MailTemplate amsMailTemplateInfo = new MailTemplate();
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(SystemPropertyConfig.getInstance().getText("mail.test.customer.subject"));
		amsMailTemplateInfo.setWlCode(wlCode);	
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(customerMail, customerMail);
		amsMailTemplateInfo.setTo(to);
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("fullName", fullName);
		HashMap<String, Object> content2 = new HashMap<String, Object>();
		content2.put("mailInfo", content);
		amsMailTemplateInfo.setContent(content2);
		
		log.info("Mail Content: " + amsMailTemplateInfo);
		
		jmsContextSender.sendMailTemplate(amsMailTemplateInfo);
		log.info("[end] send mail about complete test successful");
	}
	
	public Long getTestCount(String customerServiceId){
		return testSummaryDAO.countTestForCustomer(customerServiceId);
	}
	
	public void updateAmsCustomerTrace(AmsTestSummary amsTestSummary,String fullName){
		AmsCustomerTrace trace = new AmsCustomerTrace();
		trace.setServiceType(ITrsConstants.SERVICES_TYPE.BO);
		AmsCustomer customer = new AmsCustomer(amsTestSummary.getCustomerId());
		trace.setAmsCustomer(customer);
		trace.setReason("Test allowed to open completed.");
		trace.setNote1("");
		trace.setNote2("");
		trace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		trace.setValue1("Data before change: Finished Date:" +amsTestSummary.getEndExecutionDateTime() + ",Result:"+amsTestSummary.getResultTest()+",Point:"+amsTestSummary.getTestPoint()+",Test count"+getTestCount(amsTestSummary.getId().getCustomerServiceId()));
		trace.setValue2("Data after change: Finished Date:" +amsTestSummary.getEndExecutionDateTime() + ",Result:"+amsTestSummary.getResultTest()+",Point:"+amsTestSummary.getTestPoint()+",Test count"+getTestCount(amsTestSummary.getId().getCustomerServiceId()));
//		SysOperation operation = new SysOperation();
//		operation.setOperationId(Integer.parseInt(amsTestSummary.getCustomerId()));
//		trace.setSysOperation(operation);
//		trace.setOperationFullname(fullName);
		trace.setChangeTime(new Timestamp(System.currentTimeMillis()));
		amsCustomerTraceDAO.save(trace);
	}

	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getWhiteLableConfig() {
		return whiteLableConfig;
	}

	public void setWhiteLableConfig(
			IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> whiteLableConfig) {
		this.whiteLableConfig = whiteLableConfig;
	}

	public IBoCustomerDAO<BoCustomer> getBoCustomerDAO() {
		return boCustomerDAO;
	}

	public void setBoCustomerDAO(IBoCustomerDAO<BoCustomer> boCustomerDAO) {
		this.boCustomerDAO = boCustomerDAO;
	}

	public IAmsTestSummaryDAO<AmsTestSummary> getTestSummaryDAO() {
		return testSummaryDAO;
	}

	public void setTestSummaryDAO(IAmsTestSummaryDAO<AmsTestSummary> testSummaryDAO) {
		this.testSummaryDAO = testSummaryDAO;
	}

	public IAmsQuestionnaireDAO<AmsQuestionnaire> getAmsQuestionnaireDAO() {
		return amsQuestionnaireDAO;
	}

	public void setAmsQuestionnaireDAO(
			IAmsQuestionnaireDAO<AmsQuestionnaire> amsQuestionnaireDAO) {
		this.amsQuestionnaireDAO = amsQuestionnaireDAO;
	}

	public IAmsCustomerTestDAO<AmsCustomerTest> getAmsCustomerTestDAO() {
		return amsCustomerTestDAO;
	}

	public void setAmsCustomerTestDAO(
			IAmsCustomerTestDAO<AmsCustomerTest> amsCustomerTestDAO) {
		this.amsCustomerTestDAO = amsCustomerTestDAO;
	}

	public IAmsCustomerServiceDAO<AmsCustomerService> getAmsCustomerServiceDAO() {
		return amsCustomerServiceDAO;
	}

	public void setAmsCustomerServiceDAO(
			IAmsCustomerServiceDAO<AmsCustomerService> amsCustomerServiceDAO) {
		this.amsCustomerServiceDAO = amsCustomerServiceDAO;
	}

	public ISysAppDateDAO<SysAppDate> getSysAppDateDAO() {
		return sysAppDateDAO;
	}

	public void setSysAppDateDAO(ISysAppDateDAO<SysAppDate> sysAppDateDAO) {
		this.sysAppDateDAO = sysAppDateDAO;
	}

	public IAmsPackageTestDAO<AmsPackageTest> getAmsPackageTestDAO() {
		return amsPackageTestDAO;
	}

	public void setAmsPackageTestDAO(
			IAmsPackageTestDAO<AmsPackageTest> amsPackageTestDAO) {
		this.amsPackageTestDAO = amsPackageTestDAO;
	}

	public ISysUniqueidCounterDAO<SysUniqueidCounter> getiSysUniqueidCounterDAO() {
		return iSysUniqueidCounterDAO;
	}

	public void setiSysUniqueidCounterDAO(ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO) {
		this.iSysUniqueidCounterDAO = iSysUniqueidCounterDAO;
	}

	public IRptUsedBoTestDAO<RptUsedBoTest> getRptUsedBoTestDAO() {
		return rptUsedBoTestDAO;
	}

	public void setRptUsedBoTestDAO(IRptUsedBoTestDAO<RptUsedBoTest> rptUsedBoTestDAO) {
		this.rptUsedBoTestDAO = rptUsedBoTestDAO;
	}

	public BoManager getBoManager() {
		return boManager;
	}

	public void setBoManager(BoManager boManager) {
		this.boManager = boManager;
	}

	public IJmsContextSender getJmsContextSender() {
		return jmsContextSender;
	}

	public void setJmsContextSender(IJmsContextSender jmsContextSender) {
		this.jmsContextSender = jmsContextSender;
	}

	public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}

	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}

	public IAmsCustomerTraceDAO<AmsCustomerTrace> getAmsCustomerTraceDAO() {
		return amsCustomerTraceDAO;
	}

	public void setAmsCustomerTraceDAO(
			IAmsCustomerTraceDAO<AmsCustomerTrace> amsCustomerTraceDAO) {
		this.amsCustomerTraceDAO = amsCustomerTraceDAO;
	}
	
	/**
	 * Update AmsBoAdditionalInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Oct 16, 2015
	 * @MdDate
	 */
	public AmsCustomerService updateAmsBoAdditionalInfo(AmsBoAdditionalInfoUpdateRequest request) {
		List<String> listBefore = new ArrayList<String>();
		List<String> listAfter = new ArrayList<String>();
		
		AmsCustomerService amsCustomerService = null;
		
		String customerId = request.getCustomerId();
		
		AmsCustomerSurvey amsCustomerSurvey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
		if(amsCustomerSurvey != null) {
			//BoInvestmentPurpose
			if(request.hasBoInvestmentPurpose() && request.getBoInvestmentPurpose().hasBoPurposeShortTermFlg()) {
				Integer boPurposeShortTermFlg = Integer.valueOf(request.getBoInvestmentPurpose().getBoPurposeShortTermFlg());
				if (!boPurposeShortTermFlg.equals(amsCustomerSurvey.getBoPurposeShortTermFlg())) {
					listBefore.add("BoPurposeShortTermFlg=" + amsCustomerSurvey.getBoPurposeShortTermFlg());
					listAfter.add("BoPurposeShortTermFlg=" + boPurposeShortTermFlg);
					amsCustomerSurvey.setBoPurposeShortTermFlg(boPurposeShortTermFlg);
				}
			}
			
			if(request.hasBoInvestmentPurpose() && request.getBoInvestmentPurpose().hasBoPurposeDispAssetMngFlg()) {
				Integer boPurposeDispAssetMngFlg = Integer.valueOf(request.getBoInvestmentPurpose().getBoPurposeDispAssetMngFlg());
				if (!boPurposeDispAssetMngFlg.equals(amsCustomerSurvey.getBoPurposeDispAssetMngFlg())) {
					listBefore.add("BoPurposeDispAssetMngFlg=" + amsCustomerSurvey.getBoPurposeDispAssetMngFlg());
					listAfter.add("BoPurposeDispAssetMngFlg=" + boPurposeDispAssetMngFlg);
					amsCustomerSurvey.setBoPurposeDispAssetMngFlg(boPurposeDispAssetMngFlg);
				}
			}
			
			if(request.hasBoInvestmentPurpose() && request.getBoInvestmentPurpose().hasBoPurposeHedgeFlg()) {
				Integer boPurposeHedgeFlg = Integer.valueOf(request.getBoInvestmentPurpose().getBoPurposeHedgeFlg());
				if (!boPurposeHedgeFlg.equals(amsCustomerSurvey.getBoPurposeHedgeFlg())) {
					listBefore.add("BoPurposeHedgeFlg=" + amsCustomerSurvey.getBoPurposeHedgeFlg());
					listAfter.add("BoPurposeHedgeFlg=" + boPurposeHedgeFlg);
					amsCustomerSurvey.setBoPurposeHedgeFlg(boPurposeHedgeFlg);
				}
			}
			
			if(request.hasBoPurposeHedgeType()) {
				Integer boPurposeHedgeType = request.getBoPurposeHedgeType().getNumber();
				if (boPurposeHedgeType != null && !boPurposeHedgeType.equals(amsCustomerSurvey.getBoPurposeHedgeType())) {
					listBefore.add("BoPurposeHedgeType=" + amsCustomerSurvey.getBoPurposeHedgeType());
					listAfter.add("BoPurposeHedgeType=" + boPurposeHedgeType);
					amsCustomerSurvey.setBoPurposeHedgeType(boPurposeHedgeType);
				}
			}
			
			if(request.hasBoPurposeHedgeAmount()) {
				Integer boPurposeHedgeAmount = request.getBoPurposeHedgeAmount().getNumber();
				if (boPurposeHedgeAmount != null && !boPurposeHedgeAmount.equals(amsCustomerSurvey.getBoPurposeHedgeAmount())) {
					listBefore.add("BoPurposeHedgeAmount=" + amsCustomerSurvey.getBoPurposeHedgeAmount());
					listAfter.add("BoPurposeHedgeAmount=" + boPurposeHedgeAmount);
					amsCustomerSurvey.setBoPurposeHedgeAmount(boPurposeHedgeAmount);
				}
			}
			
			if(request.hasBoLossMaxAmount()) {
				BigDecimal boMaxLossAmount = new BigDecimal(request.getBoLossMaxAmount());
				BigDecimal tempMaxLossAmount = amsCustomerSurvey.getBoMaxLossAmount() != null ? amsCustomerSurvey.getBoMaxLossAmount() : BigDecimal.ZERO;
				
				if (boMaxLossAmount != null && boMaxLossAmount.compareTo(tempMaxLossAmount) != 0) {
					listBefore.add("BoMaxLossAmount=" + amsCustomerSurvey.getBoMaxLossAmount());
					listAfter.add("BoMaxLossAmount=" + boMaxLossAmount);
					amsCustomerSurvey.setBoMaxLossAmount(boMaxLossAmount);
				}
			}
			
			//Calculate boTestStatus
			Integer boTestStatus = ITrsConstants.BO_TEST_STATUS.TEST_WAITING;
			if(isNotSetValue(amsCustomerSurvey.getInvestExpFx())
					|| isNotSetValue(amsCustomerSurvey.getInvestExpStockTrust())
				    || isNotSetValue(amsCustomerSurvey.getInvestExpOption())
				    || isNotSetValue(amsCustomerSurvey.getInvestExpFutureTrading()))
				boTestStatus = ITrsConstants.BO_TEST_STATUS.TEST_WAITING;
			else {
				int investExp = isNotSetValue(amsCustomerSurvey.getInvestExpFx()) ? 0 : amsCustomerSurvey.getInvestExpFx();
				investExp = isNotSetValue(amsCustomerSurvey.getInvestExpStockTrust()) ? 0 : Math.max(amsCustomerSurvey.getInvestExpStockTrust(), investExp);
				investExp = isNotSetValue(amsCustomerSurvey.getInvestExpOption()) ? 0 : Math.max(amsCustomerSurvey.getInvestExpOption(), investExp);
				investExp = isNotSetValue(amsCustomerSurvey.getInvestExpFutureTrading()) ? 0 : Math.max(amsCustomerSurvey.getInvestExpFutureTrading(), investExp);
				
				if(investExp <= 1 || (amsCustomerSurvey.getFinancialAssets() == null || amsCustomerSurvey.getFinancialAssets() == 0))
					boTestStatus = ITrsConstants.BO_TEST_STATUS.TEST_DISABLE;
				else
					boTestStatus = ITrsConstants.BO_TEST_STATUS.TEST_ALLOWED;
			}
			
			amsCustomerSurvey.setBoTestStatus(boTestStatus);
			amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			
			//Save to DB amsCustomerSurvey
			amsCustomerSurveyDAO.merge(amsCustomerSurvey);
			log.info("Updated AmsBoAdditionalInfo to AmsCustomerSurvey, boTestStatus: " + boTestStatus);
			
			AmsCustomerService tempAmsCustomerService = amsCustomerService = amsCustomerServiceDAO.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.BO);

			//Save to AmsCustomerService
			if(amsCustomerService != null) {
				 amsCustomerService.setTestStatus(boTestStatus);
				 amsCustomerService.setCustomerServiceStatus(ITrsConstants.BO_TEST_STATUS.WAITING_ADD_ACCOUNT);
				 amsCustomerService.setAccountStatusChangeDate(DateUtil.getCurrentDateTime(DateUtil.PATTERN_YYYYMMDD_BLANK));
				 amsCustomerService.setAccountStatusChangeDatetime(new Timestamp(System.currentTimeMillis()));
				 amsCustomerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				 
				 amsCustomerServiceDAO.merge(amsCustomerService);
				 log.info("Updated AmsBoAdditionalInfo to AmsCustomerService, CustomerServiceStatus: " + amsCustomerService.getCustomerServiceStatus());
				 
				 listBefore.add("CustomerServiceBoStatus=" + amsCustomerService.getCustomerServiceStatus());
				 listAfter.add("CustomerServiceBoStatus=" + ITrsConstants.BO_TEST_STATUS.WAITING_ADD_ACCOUNT 
						 + "+" + boTestStatus + "(" + BoServiceStatusEnum.getInstanceByIntValue(boTestStatus.intValue()) + ")");
				 
				// Update to REDIS (BoBusinessController)
				boolean resultUpdateBo = updateBoCustomer(amsCustomerService, amsCustomerService.getTestStatus());
				if (!resultUpdateBo) {
					log.warn("Sync AmsBoAdditionalInfo to BoBusinessController: FAIL");
					amsCustomerService = null;
				} else
					log.info("Sync AmsBoAdditionalInfo to BoBusinessController: SUCCESS");
			} else {
				log.warn("Not found customerServiceId: " + customerId + IConstants.SERVICES_TYPE.BO);
			}
			
			// Insert into AMS_CUSTOMER_TRACE
			if (listAfter.size() > 0 && tempAmsCustomerService != null) {
				try {
					StringBuilder beforeValue = new StringBuilder();
					StringBuilder afterValue = new StringBuilder();
					
					boolean isFirst = true;
					for(String item : listBefore) {
						if(isFirst) isFirst = false;
						else
							beforeValue.append(", ");
						beforeValue.append(item);
					}
					
					isFirst = true;
					for(String item : listAfter) {
						if(isFirst) isFirst = false;
						else
							afterValue.append(", ");
						afterValue.append(item);
					}
					
					String strBefore = beforeValue.toString();
					String strAfter = afterValue.toString();
					log.info("[start] insert to AmsCustomerTrace, Before: " + strBefore + " => After: " + strAfter);
					
					AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
					amsCustomerTrace.setServiceType(IConstants.SERVICES_TYPE.AMS);
					amsCustomerTrace.setAmsCustomer(tempAmsCustomerService.getAmsCustomer());
					amsCustomerTrace.setReason("Add BO additional registration info");
					amsCustomerTrace.setNote1("");
					amsCustomerTrace.setNote2("");
					amsCustomerTrace.setValue1(strBefore);
					amsCustomerTrace.setValue2(strAfter);
					amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
					amsCustomerTrace.setOperationFullname(tempAmsCustomerService.getAmsCustomer().getFullName());
					amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
					amsCustomerTraceDAO.save(amsCustomerTrace);
					
					log.info("[end] insert to AmsCustomerTrace");
				} catch (Exception e) {
					log.error(e.getLocalizedMessage(), e);
				}
			}
		} else
			log.warn("Not found amsCustomerSurvey for customerId: " + customerId);
		
		return amsCustomerService;
	}
	
	private static boolean isNotSetValue(Integer value) {
		return value == null || value.intValue() == -1;
	}

	@Override
	public String getCurrentBizDate() {
		return sysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate();
	}

	@Override
	public void updateBoTestResultToAmsCustomerTrace(TestBoModel model) {
		log.info("[Start] Update BO test result to AMS_CUSTOMER_TRACE for customerId [" + model.getCustomerId() + "]");

		AmsCustomerService customerService = amsCustomerServiceDAO.findByCustomerIdServiceType(model.getCustomerId(), IConstants.SERVICES_TYPE.BO);
		if (customerService != null) {
			int customerServiceStatusAfterChange = customerService.getCustomerServiceStatus();

			AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
			amsCustomerTrace.setServiceType(IConstants.SERVICES_TYPE.BO);
			amsCustomerTrace.setAmsCustomer(customerService.getAmsCustomer());
			amsCustomerTrace.setReason(model.getResultTest() == Constant.BO_TEST_RESULT_SUCCESS ? "Test Passed" : "Test Failed");
			amsCustomerTrace.setNote1(model.getTestPoint() + "/" + model.getTotalPoint());
			amsCustomerTrace.setNote2("");
			amsCustomerTrace.setValue1(model.getCustomerServiceStatusBeforeChange() + "");
			amsCustomerTrace.setValue2(customerServiceStatusAfterChange + "");
			amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsCustomerTrace.setSysOperation(null);
			amsCustomerTrace.setOperationFullname("");
			amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
			amsCustomerTraceDAO.save(amsCustomerTrace);

			log.info("[End] Update BO test result to AMS_CUSTOMER_TRACE for customerId [" + model.getCustomerId() + "]");
		}
	}
}
