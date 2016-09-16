package com.nts.ams.api.controller.customer.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.ITestBoManager;
import phn.nts.ams.fe.domain.BoTestInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.model.TestBoModel;

import com.nts.ams.api.controller.customer.bean.AmsCustomerBoTestUpdateRequestWraper;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.common.Constant;
import com.nts.common.Constant.BO_TEST_POINT;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsBoQuestionnaireInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerBoTestInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerBoTestUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 27, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerBoTestUpdateTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsCustomerBoTestUpdateTask.class);
	private AmsCustomerBoTestUpdateRequestWraper wraper;
	private IAccountManager accountManager = null;
	private ITestBoManager testBoManager = null;
	private ExecutorService executorService;
	
	private TestBoModel model = new TestBoModel();
	private HashMap<String, AmsBoQuestionnaireInfo.Builder> hmAnswers = new HashMap<String, AmsBoQuestionnaireInfo.Builder>(); 
    
	public AmsCustomerBoTestUpdateTask(AmsCustomerBoTestUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerBoTestUpdateRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerBoTestUpdateRequest, requestId: " + wraper.getResponseBuilder().getId() +  ", " + request);
			Result result = Result.FAILED;
			
			AmsCustomerBoTestInfo.Builder amsCustomerBoTestInfoBuilder = AmsCustomerBoTestInfo.newBuilder();
			amsCustomerBoTestInfoBuilder.setCustomerId(request.getCustomerId());
			amsCustomerBoTestInfoBuilder.setCustomerServiceId(request.getCustomerServiceId());
			
			if(request.getBoQuestionnaireInfoCount() <= 0) {
				log.info("BoQuestionnaireInfo is empty!");
				model.setErrorMessage("MSG_NAB010");
			} else {
				//Put answer to map for handle
				for (AmsBoQuestionnaireInfo answer : request.getBoQuestionnaireInfoList()) {
					hmAnswers.put(answer.getQuestionnaireId(), answer.toBuilder());
				}
				
				String customerId = request.getCustomerId();
				
				//Get customer info
				CustomerServicesInfo customerServiceInfoAccount = accountManager.getCustomerServiceInfo(customerId, IConstants.SERVICES_TYPE.BO);
				AmsCustomer amsCustomer = accountManager.getAmsCustomer(customerId);
				
				if(customerServiceInfoAccount != null && amsCustomer != null) {
					model.setCustomerId(customerId);
					model.setCustomerServiceStatusBeforeChange(customerServiceInfoAccount.getCustomerServiceStatus());

					String customerServiceId = customerServiceInfoAccount.getCustomerServiceId();
//					String frontDate = TrsStringUtil.toDateString(new Date(), ITrsConstants.DATE_PATTERN.YYYYMMDD);
//					String frontDate = testBoManager.getCurrentBizDate();
					String frontDate = request.getStartDateTest(); //TRSPT-7540
					
					String wlCode = customerServiceInfoAccount.getWlCode();
					String fullName = amsCustomer.getFullName();
					//get list BoTest of customer
					if(getListBoTest(frontDate, customerServiceId, wlCode)) {
						
						//Check test result
						result = checkBoTest(frontDate, customerServiceId, customerId, wlCode, fullName);
						
						//Set result
						amsCustomerBoTestInfoBuilder.setTotalPoint(String.valueOf(model.getTotalPoint()));
						amsCustomerBoTestInfoBuilder.setTestPoint(String.valueOf(model.getTestPoint()));
						amsCustomerBoTestInfoBuilder.setResultTest(String.valueOf(model.getResultTest()));
						
						if(result == Result.SUCCESS) {
							//Reset answer detail
							for (AmsBoQuestionnaireInfo.Builder answer : hmAnswers.values()) {
								amsCustomerBoTestInfoBuilder.addQuestionnaireInfo(answer);
							}
						}
					}
				} else {
					log.info("Not found BoCustomerService, customerId: " + customerId);
					model.setErrorMessage("MSG_NAB019");
				}
			}
			
			//Response to client
			RpcMessage response = createRpcMessage(amsCustomerBoTestInfoBuilder.build(), result, model.getErrorMessage());
			AmsApiControllerMng.getAmsCustomerResponsePublisher().publish(response);
			
			log.info("[end] handle AmsCustomerBoTestUpdateRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerBoTestUpdateProcessor().onComplete(wraper);
		}
	}
	
	/**
	 * Get BoTestList of CustomerServiceId
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 27, 2015
	 * @MdDate
	 */
	private boolean getListBoTest(String frontDate, String customerServiceId, String wlCode) {
		List<BoTestInfo> listTest = testBoManager.getListBoTest(frontDate, wlCode, customerServiceId);
		
		if (listTest == null || listTest.size() == 0) {
			log.info("ListBoTest is empty!");
			model.setErrorMessage("BO_TEST_EMPTY");
			return false;
		}
		
		log.info("Loaded ListBoTest size: " + listTest.size());
		model.setListTest(listTest);
		return true;
	}
	
	/**
	 * Check Bo test　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 27, 2015
	 * @MdDate
	 */
	public Result checkBoTest(String frontDate, String customerServiceId, String customerId, String wlCode, String customerFullName){
		try {
			log.info("[start] check Bo test, customerServiceId: " + customerServiceId + ", wlCode: " + wlCode);
			
			int minPoint = 0;
			int point = 0;
			int totalPoint = 0;
			
			//Get min point to pass boTest
			Map<String, String> mapConfig = SystemProperty.getInstance().getMap().get(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
			String minpoint = mapConfig != null ? mapConfig.get(ITrsConstants.WHITE_LABEL_CONFIG.BO_TEST_PERCENT_PASS) : "0";
			if(!StringUtil.isEmpty(minpoint)){
				minPoint = Integer.parseInt(minpoint);
			}
			
			//Check Bo test status
			Integer boTestStatus = testBoManager.getBoCustomerStatus(customerId);
			if(!checkBoTestStatus(boTestStatus, customerServiceId, frontDate)) {
				log.info("Customer already DO testing today");
				return Result.FAILED;
			}
			
			//Calculate Test Point
			List<BoTestInfo> listTestInfos = model.getListTest();
			totalPoint = listTestInfos.size();
			for(BoTestInfo info : listTestInfos) {
				AmsBoQuestionnaireInfo.Builder answer = hmAnswers.get(info.getQuestionId());
				if(answer == null) {
					log.warn("Not found Answer for QuestionId: " + info.getQuestionId());
					testBoManager.insertCustomerTest(customerServiceId, BO_TEST_POINT.FAIL, info);
					continue;
				}
					
				info.setCustomerAnswer(answer.getCustomerAnswer());
				if(info.getAnswer().equals(info.getCustomerAnswer())) {
					testBoManager.insertCustomerTest(customerServiceId, BO_TEST_POINT.SUCCESS, info);
					point += BO_TEST_POINT.SUCCESS;
					answer.setPoint(String.valueOf(BO_TEST_POINT.SUCCESS));
				} else {
					testBoManager.insertCustomerTest(customerServiceId, BO_TEST_POINT.FAIL, info);
					answer.setPoint(String.valueOf(BO_TEST_POINT.FAIL));
				}
			}					
			
			model.setTestPoint(point);
			model.setTotalPoint(totalPoint);
			
			//Check test pass
			float testPercent = 0;
			if(point > 0 && totalPoint > 0){
				testPercent = ((float)point/totalPoint) * 100;
			}
			
			if(testPercent >= minPoint){				
				log.info(String.format("PASS test package, point %d/%d, percent %f > %s", point, totalPoint, testPercent, minpoint));
				model.setResultTest(Constant.BO_TEST_RESULT_SUCCESS);
				
				//PASS test package
				String summaryFrontDate = testBoManager.updateAmsTestSummary(customerServiceId, frontDate, point, totalPoint, Constant.BO_TEST_RESULT_SUCCESS);
				
				try {
					////[TRSPT-7614-ThinhPH]Jan 27, 2016M - Start 
					//Update customerInfo
					testBoManager.updateCustomer(customerId, customerServiceId, wlCode, customerFullName, summaryFrontDate, getExecutorService());
					//[TRSPT-7614-ThinhPH]Jan 27, 2016M - End
				} catch (Exception e) {
					log.info("Fail to updateCustomer");
					log.error(e.getMessage(), e);
					return Result.FAILED;
				}
			} else {
				log.info(String.format("FAIL test package, point %d/%d, percent %f < %s", point, totalPoint, testPercent, minpoint));
				model.setResultTest(Constant.BO_TEST_RESULT_FAIL);
				
				//FAIL test package
				testBoManager.updateAmsTestSummary(customerServiceId, frontDate, point, totalPoint, Constant.BO_TEST_RESULT_FAIL);
			}
			
			//[TRSGAP-487-cuong.bui.manh]Jun 27, 2016A - Start 
			testBoManager.updateBoTestResultToAmsCustomerTrace(model);
			//[TRSGAP-487-cuong.bui.manh]Jun 27, 2016A - End

			return Result.SUCCESS;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			return Result.FAILED;
		} finally {
			log.info("[end] check Bo test, customerServiceId: " + customerServiceId + ", wlCode: " + wlCode);	
		}
	}
	
	/**
	 * Check Bo Test Status
	 * 
	 * @param
	 * @return
	 * @MdDate
	 */
	public boolean checkBoTestStatus(Integer boTestStatus, String customerId, String frontDate){
		log.info("[start] check BO test status, boTestStatus: " + boTestStatus + ", customerId: " + customerId + ", frontDate: " + frontDate);
		boolean flag = true;
		if(ITrsConstants.BO_TEST_STATUS.TEST_ALLOWED.equals(boTestStatus)){
			flag =  true;	
		}else if(ITrsConstants.BO_TEST_STATUS.TEST_DISABLE.equals(boTestStatus)){
			model.setTestToday(2);
			flag =  false;
		}
		
		if(testBoManager.checkBoTestToDay(customerId, frontDate)){
			model.setTestToday(1);
			model.setWarning("nts.ams.fe.testbo.warning");
			flag = false;
		} 
		
		log.info("[end] check BO test status");
		model.setTestAcceptFlag(false);
		return flag;
	}
	
	/**
	 * Create RpcMessage to response to client　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public RpcMessage createRpcMessage(AmsCustomerBoTestInfo boTestInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_BO_TEST_UPDATE_RESPONSE);
		if(errCode != null)
			response.setMessageCode(errCode);
		
		if(result == Result.SUCCESS && boTestInfo != null) {
			AmsCustomerBoTestUpdateResponse.Builder customerInfoResponse = AmsCustomerBoTestUpdateResponse.newBuilder();
			customerInfoResponse.setBoTestResultInfo(boTestInfo);
			response.setPayloadData(customerInfoResponse.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(result);
		}
		
		return response.build();
	}
	
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public ITestBoManager getTestBoManager() {
		return testBoManager;
	}

	public void setTestBoManager(ITestBoManager testBoManager) {
		this.testBoManager = testBoManager;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}