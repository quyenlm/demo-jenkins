package phn.nts.ams.fe.mt4;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import phn.com.nts.db.common.DBException;
import phn.com.nts.db.common.Database;
import phn.com.nts.db.common.DatabaseWithoutPool;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.messages.AmsFrontAdminMessageSubcriber;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.jms.IJmsSender;

import com.phn.mt.common.constant.IConstant;
import com.phn.mt.common.constant.IResultConstant;
import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.MarginLevel;
import com.phn.mt.common.entity.UserRecord;

public class MT4Manager {
	private static Logit LOG = Logit.getInstance(MT4Manager.class);
	private static String server = "";
	private static String driver = "";
	private static String username = "";
	private static String password = "";
	private static Database db = null;
	final int MAX_TIME = 30;
	private static MT4Manager instance;	
	public static MT4Manager getInstance() {
		if(instance == null) {
			instance = new MT4Manager();
		}
		return instance;
	}
	
	
	private IJmsSender jmsRealSender;
	private IJmsSender jmsDemoSender;
	
    private String getRandomSequenceId(){
        Object clusterServerId = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + ITrsConstants.CONFIG_KEY.CLUSTER_SERVER_ID);
        return (clusterServerId == null ? "" : clusterServerId.toString()) + MathUtil.generateRandomPassword(32);
    }

	/**
	 * 　
	 * Register MT4 account
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 7, 2012
	 * @MdDate
	 */
	
	public Integer registerMT4Account(CustomerInfo customerInfo,String loginId,String wlCode, String agentServiceId, String subGroupName, Integer leverage, String investorPassword, String masterPassword){	
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		Integer counter = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL));
		Integer interval = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.INTERVAL));
		
		int activeFlg = IConstants.ACTIVE_FLG.ACTIVE;
		String defaultCustomerName = "customer";
		Integer agentAccountId = MathUtil.parseInt(agentServiceId, 0);
		UserRecord userRecord = new UserRecord();				
		try{			
			Integer customerId = MathUtil.parseInteger(loginId);													
			// Add to userRecord
			LOG.info("Start Adding to userRecord");		
			userRecord.setLogin(MathUtil.parseInteger(loginId));
			userRecord.setEnable(activeFlg);
//			userRecord.setEnableChangePassword(IConstants.ACTIVE_FLG.INACTIVE);
			userRecord.setEnableReadOnly(IConstants.ACTIVE_FLG.INACTIVE);
			userRecord.setGroup(subGroupName);
			userRecord.setPassword(masterPassword);
			userRecord.setPasswordInvestor(investorPassword);
			userRecord.setSignalProvider(IConstants.ENABLE_FLG.ENABLE);
//			userRecord.setEnable(activeFlg);
//			userRecord.setEnableChangePassword(IConstants.ACTIVE_FLG.ACTIVE);
			//userRecord.setEnableReadOnly(1);
			userRecord.setCountry(customerInfo.getCountryName());
			if(leverage != null)
			userRecord.setLeverage(leverage);
			if(customerInfo.getFullName() !=null && !StringUtil.isEmpty(customerInfo.getFullName())) {
				userRecord.setName(customerInfo.getFullName());
			}else {
				userRecord.setName(defaultCustomerName);
			}
			userRecord.setEmail(customerInfo.getMailMain());
			userRecord.setSendReports(IConstants.ACTIVE_FLG.ACTIVE);
			userRecord.setAgent_account(agentAccountId);
			// End adding to userRecord
			
			LOG.info("Add information into userRecord "+userRecord.getName() +"LOGinId "+userRecord.getLogin());
			LOG.info("Password" +userRecord.getPassword());
			LOG.info("Enable" +userRecord.getEnableChangePassword());
			LOG.info("Leverage "+userRecord.getLeverage());
			LOG.info("Group " +userRecord.getGroup());
			LOG.info("AgentAccount " +userRecord.getAgent_account());
			LOG.info("End Adding to userRecord");
		    String sequenceId = getRandomSequenceId();
		    LOG.info("registerMT4Account to MT4, sequenceID: " + sequenceId);
			userRecord.setSequenceID(sequenceId);
			userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_REGIST);
			jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
			int result = -1;
			String passMt4 = null;
			try{
				Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
				if(obj != null){
					UserRecord ur = (UserRecord) obj;
					result = ur.getResult();
					passMt4 = ur.getPassword();
					investorPassword = ur.getPasswordInvestor();
					LOG.info("updateStatusForCreateNewMT4Account:" + result);
					LOG.debug("MT4 user record response: passMT4 is " + passMt4 + " and investor passwd is " + investorPassword);
				}
			}catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			if (IResultConstant.Register.SUCCESSFUL == result) {
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
			}
		}catch(Exception ex) {
			LOG.error(ex.getMessage(),ex);
		}
		return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
	}
	
	public Integer registerMT4DemoAccount(CustomerInfo customerInfo,String loginId,String wlCode, String subGroupName, Integer leverage, String investorPassword, String masterPassword, String balance){
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
				
		String defaultCustomerName = "customer";		
		UserRecord userRecord = new UserRecord();				
		try{			
			Integer customerId = MathUtil.parseInteger(loginId);													
			// Add to userRecord
			LOG.info("Start Adding to userRecord");		
			userRecord.setLogin(MathUtil.parseInteger(loginId));
			userRecord.setEnable(IConstants.ACTIVE_FLG.ACTIVE);//			
			userRecord.setEnableReadOnly(IConstants.ACTIVE_FLG.INACTIVE);
			userRecord.setGroup(subGroupName);
			userRecord.setPassword(masterPassword);
			userRecord.setPasswordInvestor(investorPassword);
			userRecord.setCountry(customerInfo.getCountryName());
			userRecord.setSignalProvider(IConstants.ENABLE_FLG.ENABLE);
			if(leverage != null)
				userRecord.setLeverage(leverage);
			if (balance != null) {
				LOG.info("Balance " + balance);
				Double deposit = MathUtil.parseDouble(balance);
				userRecord.setBalance(deposit);
			}
			
			if(customerInfo.getFullName() !=null && !StringUtil.isEmpty(customerInfo.getFullName())) {
				userRecord.setName(customerInfo.getFullName());
			}else {
				userRecord.setName(defaultCustomerName);
			}
			userRecord.setEmail(customerInfo.getMailMain());
			userRecord.setSendReports(IConstants.ACTIVE_FLG.ACTIVE);
			userRecord.setAgent_account(0);
			// End adding to userRecord
			
			LOG.info("Add information into userRecord for open demo account " + userRecord.getName() + " LOGinId " + userRecord.getLogin());
			LOG.info("Password " +userRecord.getPassword());
			LOG.info("Enable " + userRecord.getEnableChangePassword());
			LOG.info("Leverage "+userRecord.getLeverage());
			LOG.info("Group " +userRecord.getGroup());
			LOG.info("AgentAccount " +userRecord.getAgent_account());
			LOG.info("End Adding to userRecord");
			String sequenceId = getRandomSequenceId();
			LOG.info("registerMT4DemoAccount to MT4, sequenceID: " + sequenceId);
			userRecord.setSequenceID(sequenceId);
			userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_REGIST);
			jmsDemoSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_DEMO_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
			int result = -1;
			String passMt4 = null;
			try{
				Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
				if(obj != null){
					UserRecord ur = (UserRecord) obj;
					result = ur.getResult();
					passMt4 = ur.getPassword();
					investorPassword = ur.getPasswordInvestor();
					LOG.info("updateStatusForCreateNewMT4Account:" + result);
					LOG.debug("MT4 user record response: passMT4 is " + passMt4 + " and investor passwd is " + investorPassword);
				}
			}catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			if (result == IResultConstant.Register.SUCCESSFUL) {
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
			}
		}catch(Exception ex) {
			LOG.error(ex.getMessage(),ex);
		}
		return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
	}
	
	public FundRecord withdrawBalance(String customerId, Double amount, Integer fundType, String description, int fundCreditMode) {
		FundRecord fundRecordReceiver = null;
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		LOG.info("<><><><><><><><><> Start withdrawal Balance() function <><><><><><><><><>");								
		Double volumeMt4 = 0 - amount;
			
		// if [OK] then send FundRecord object to ActiveMQ
		java.util.Date date = Calendar.getInstance().getTime();
		
		String sequenceID = getRandomSequenceId();
		LOG.info("withdrawBalance to MT4, sequenceID: " + sequenceID);
		FundRecord fundRecord = new FundRecord();
		fundRecord.setActiveFlg(true);
		fundRecord.setCustomerId(MathUtil.parseInt(customerId, 0));		
		if(fundRecord.getCustomerId() == 0) {
			LOG.warn("Customer ID is null");
			
			return fundRecordReceiver; 
		}
		
		fundRecord.setRequestDate(date.getTime());
		fundRecord.setVolume(String.valueOf(volumeMt4));
		fundRecord.setSequenceID(sequenceID);
		fundRecord.setFundType(fundType);
		fundRecord.setDescription(description);
		
		fundRecord.setFundCreditMode(fundCreditMode);
		fundRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_BALANCE_UPDATE);
		jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, fundRecord);
		
		try{
			//Get result
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceID);
			if(obj != null){
				fundRecordReceiver = (FundRecord) obj;
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return fundRecordReceiver;
	}
	public Integer depositBalance(String customerId, Double amount, Integer fundType, String description, int fundCreditMode) {
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		
		try {								
			// if [OK] then send FundRecord object to ActiveMQ
			java.util.Date date = Calendar.getInstance().getTime();
			
			String sequenceID = getRandomSequenceId();
			LOG.info("depositBalance to MT4, sequenceID: " + sequenceID);
			FundRecord fundRecord = new FundRecord();
			fundRecord.setActiveFlg(true);
			fundRecord.setCustomerId(MathUtil.parseInt(customerId, 0));		
			if(fundRecord.getCustomerId() == 0) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_INVALID_CUSTOMER_ID; 
			}
			
			fundRecord.setRequestDate(date.getTime());
			fundRecord.setVolume(String.valueOf(amount));
			fundRecord.setSequenceID(sequenceID);
			fundRecord.setFundType(fundType);
			fundRecord.setDescription(description);
			// TODO set msg type for deposit or withdrawal
			fundRecord.setFundCreditMode(fundCreditMode);
			fundRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_BALANCE_UPDATE);
			
			jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, fundRecord);
			
			int result = Integer.MIN_VALUE;
			try{
				Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceID);
				if(obj != null){
					FundRecord fr = (FundRecord) obj;
					result = fr.getResult();
				}
			}catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		
			if(result == Integer.MIN_VALUE) {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT;
			} else if (result == IResultConstant.Withdraw.SUCCESSFUL) {								
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS;
			} else {
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_FAIL;
			}

		} catch (Exception e) {
			LOG.error(e.toString(), e);
			
			return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_EXCEPTION;
		}
	}
	
	public FundRecord updateBalance(String customerId, Double amount, Integer fundType, String description, int fundCreditMode) {
		FundRecord resultRecord = new FundRecord();
		resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT);
		try {
			// Send FundRecord object to ActiveMQ
			java.util.Date date = Calendar.getInstance().getTime();
			
			String sequenceID = getRandomSequenceId();
			LOG.info("[] updateBalance, sequenceID: " + sequenceID);
			
			FundRecord fundRecord = new FundRecord();
			fundRecord.setActiveFlg(true);
			fundRecord.setCustomerId(MathUtil.parseInt(customerId, 0));		
			if(fundRecord.getCustomerId() == 0) {
				resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_INVALID_CUSTOMER_ID);
				return resultRecord;
			}
			
			fundRecord.setRequestDate(date.getTime());
			fundRecord.setVolume(String.valueOf(amount));
			fundRecord.setSequenceID(sequenceID);
			fundRecord.setFundType(fundType);
			fundRecord.setDescription(description);
			fundRecord.setFundCreditMode(fundCreditMode);
			fundRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_BALANCE_UPDATE);
			
			jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, fundRecord);
			
			int result = Integer.MIN_VALUE;
			try{
				Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceID);
				if(obj != null){
					FundRecord fr = (FundRecord) obj;
					result = fr.getResult();
					resultRecord.setOrderTicket(fr.getOrderTicket());
				}
			}catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		
			if(result == Integer.MIN_VALUE) {
				resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT);
			} else if (result == IResultConstant.Withdraw.SUCCESSFUL) {								
				resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS);
			} else {
				resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_FAIL);
			}
		} catch (Exception e) {
			LOG.error(e.toString(), e);
			resultRecord.setResult(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_FAIL);
		}
		
		return resultRecord;
	}

	public BalanceInfo getBalanceInfo(String customerId) {
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		
		BalanceInfo balanceInfo = new BalanceInfo();
		/*try {*/
		String sequenceID =  getRandomSequenceId();
		LOG.info("getBalanceInfo from MT4, sequenceID: " + sequenceID);
		MarginLevel marginLevel = new MarginLevel();
		marginLevel.setLogin(Integer.parseInt(customerId));
		marginLevel.setSequenceID(sequenceID);
		
		marginLevel.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_MARGIN_LEVEL);
		
		jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, marginLevel);
		
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceID);
			if(obj != null){
				MarginLevel ml = (MarginLevel) obj;
				LOG.info("Response from MT4: "
						+ ", SequenceID: " + sequenceID
						+ ", Balance: " + ml.getBalance()
						+ ", Equity: " + ml.getEquity()
						+ ", Margin: " + ml.getMargin()
						+ ", FreeMargin: " + ml.getMarginFree()						
						+ ", Margin Shot: " + ml.getMarginShort()
						+ ", Margin Level: " + ml.getMarginLevel()
						+ ", Credit: " + ml.getCredit()
						+ ", MaxwithdrawBalance: " + ml.getMaxWithdrawBalance()
						+ ", UnrealizedPl: " + ml.getUnrealizedPl());
				
				int result = ml.getResult();
				if(result == IConstant.BALANCE_GET_SUCCESS) {
					balanceInfo.setBalance(ml.getBalance());
					balanceInfo.setEquity(ml.getEquity());
					balanceInfo.setMargin(ml.getMargin());
					balanceInfo.setFreemargin(ml.getMarginFree());
					balanceInfo.setMarginShort(ml.getMarginShort());
					balanceInfo.setMarginLevel(ml.getMarginLevel());						
					balanceInfo.setCredit(ml.getCredit());
					balanceInfo.setAmountAvailable(ml.getMaxWithdrawBalance());
					balanceInfo.setLeverage(ml.getLeverage());
					balanceInfo.setUnrealizedPl(ml.getUnrealizedPl());
					balanceInfo.setResult(AccountBalanceResult.SUCCESS);
				} else
					balanceInfo.setResult(AccountBalanceResult.INVALID_ACCOUNT_ID);
			} else
				balanceInfo.setResult(AccountBalanceResult.TIME_OUT);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			balanceInfo.setResult(AccountBalanceResult.INTERNAL_ERROR);
		}
			
		return balanceInfo;
	}
	/**
	 * 　
	 * change password on MT4
	 * @param login of customer
	 * @param password of customer
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 13, 2012
	 * @MdDate
	 */
	public Integer changePassword(int loginId, String password) {
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		Integer counter = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL));
		Integer interval = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.INTERVAL));
		
		Integer result = IConstant.UPDATE_ACCOUNT;
		UserRecord userRecord = new UserRecord();
		userRecord.setApiData(IConstants.FRONT_OTHER.API_DATA);
		userRecord.setLogin(loginId);
		userRecord.setPassword(password);
		userRecord.setPasswordInvestor(password);
		userRecord.setEnable(UserRecord.NO_UPDATE);
		userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
		userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
		String sequenceId = getRandomSequenceId();
		LOG.info("changePassword to MT4, sequenceID: " + sequenceId);
		userRecord.setSequenceID(sequenceId);
		userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_EDIT);
		jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
		String passMt4 = null;
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				UserRecord ur = (UserRecord) obj;
				result = ur.getResult();
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	public Integer changePassword(int loginId, String password,String investorPass) {
		Integer result = IConstant.UPDATE_ACCOUNT;
		UserRecord userRecord = new UserRecord();
		userRecord.setApiData(IConstants.FRONT_OTHER.API_DATA);
		userRecord.setLogin(loginId);
		userRecord.setPassword(password);
		userRecord.setPasswordInvestor(investorPass);
		userRecord.setEnable(UserRecord.NO_UPDATE);
		userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
		userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
		try {		
			result = updateAccountMt4(userRecord);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}			
		
		return result;
	}
	
	public Integer updateDemoAccountMt4(UserRecord userRecord) {		
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		Integer result = IConstant.UPDATE_ACCOUNT;
		
		String sequenceId = getRandomSequenceId();
		LOG.info("updateDemoAccountMt4 to MT4, sequenceID: " + sequenceId);
		userRecord.setSequenceID(sequenceId);
		userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_EDIT);
		jmsDemoSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_DEMO_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
		
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				UserRecord ur = (UserRecord) obj;
				result = ur.getResult();
				AmsFrontAdminMessageSubcriber.removeJmsResult(sequenceId);
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	} 
	
	public Integer updateAccountMt4(CustomerInfo customerInfo, CustomerServicesInfo customerServiceInfo) {
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		Integer counter = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL));
		Integer interval = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.INTERVAL));
		
		Integer loginId = MathUtil.parseInteger(customerServiceInfo.getCustomerServiceId());
		Integer result = IConstant.UPDATE_ACCOUNT;
		// update account information on MT4
		UserRecord userRecord = new UserRecord();
	    userRecord.setApiData(IConstants.FRONT_OTHER.API_DATA);
		
		userRecord.setEnable(UserRecord.NO_UPDATE);
		userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
		userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
		
		userRecord.setLogin(loginId);		       
//      userRecord.setName(customerInfo.getFirstName() + " " + customerInfo.getLastName());       
		if(customerInfo.getLeverage() != null && customerInfo.getLeverage().intValue() > 0) {
			userRecord.setLeverage(customerInfo.getLeverage());
		}
		userRecord.setCountry(customerInfo.getCountryName());		
	    userRecord.setPhone(customerInfo.getTel1());
	    userRecord.setEmail(customerInfo.getMailMain());
	    userRecord.setComment("");
	    	   
	    //[TRSM1-3755-quyen.le.manh]May 4, 2016A - Start not sync status, sendReports when update profile
	    userRecord.setStatus(null);
	    // send daily confirm
	    userRecord.setSendReports(UserRecord.NO_UPDATE);
	    //[TRSM1-3755-quyen.le.manh]May 4, 2016A - End
	    
	    String sequenceId = getRandomSequenceId();
	    LOG.info("updateAccountMt4 to MT4, sequenceID: " + sequenceId);
		userRecord.setSequenceID(sequenceId);
		userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_EDIT);
		jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
		
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				UserRecord ur = (UserRecord) obj;
				result = ur.getResult();
			} else
				LOG.error("Timed-out waiting for response from MT4, sequenceId: " + sequenceId);
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 　
	 * update account on MT4
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public Integer updateAccountMt4(UserRecord userRecord) {		
		Map<String, String> mapMt4Configuration = (Map<String, String>) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.MT4_CONFIGURATION);
		if(mapMt4Configuration == null) {
			mapMt4Configuration = new HashMap<String, String>();
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.COUNT_INTERVAL));
			mapMt4Configuration.put(IConstants.MT4_CONFIGURATION.INTERVAL, StringUtil.toString(IConstants.FRONT_END_CONFIG.INTERVAL));
		}
		
		Integer result = IConstant.UPDATE_ACCOUNT;
		
		String sequenceId = getRandomSequenceId();
		LOG.info("updateAccountMt4 to MT4, sequenceID: " + sequenceId);
		userRecord.setSequenceID(sequenceId);
		userRecord.setMsgType(IConstants.NTS_MSG_TYPE.NTS_MSG_USER_EDIT);
		jmsRealSender.sendMapMessageToQueue(IConstants.ACTIVEMQ.QUEUE_AMS_FRONT_ADMIN_MESSAGE_REQUEST, userRecord);
		
		try{
			Object obj = AmsFrontAdminMessageSubcriber.getJmsResult(sequenceId);
			if(obj != null){
				UserRecord ur = (UserRecord) obj;
				result = ur.getResult();
				AmsFrontAdminMessageSubcriber.removeJmsResult(sequenceId);
			} else
				LOG.error("Timed-out waiting for response from MT4, sequenceId: " + sequenceId);
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	} 
	/**
	 * 　
	 * get rate on MT4 Report DB
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 23, 2012
	 * @MdDate
	 */
	public RateInfo getRate1(String currencyCode, Map<String, String> mapConfig) {
		LOG.info("[start] open connection to db report");
		RateInfo rateInfo = null;
		if(db == null) {
			driver = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.DRIVER);
			server = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.SERVER);
			
			username = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.USERNAME);
			password = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.PASSWORD);
			try {
				db = new DatabaseWithoutPool(driver, server, username, password);
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		synchronized (db) {
			BigDecimal rate = MathUtil.parseBigDecimal(0);
			String sql = "SELECT * FROM MT4_PRICES WHERE SYMBOL= ?";
			
			try {
				//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - Start 
				PreparedStatement statement = db.getConnection().prepareStatement(sql);
				String symbolName = IConstants.CURRENCY_CODE.USD + currencyCode;
				statement.setString(1, symbolName);
				ResultSet rs = statement.executeQuery();		
				//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - End 
				while(rs.next()) {
					rateInfo = new RateInfo();
					rate = rs.getBigDecimal("ASK");
					rateInfo.setSymbolName(symbolName);
					rateInfo.setRateType(IConstants.RATE_TYPE.ASK);
					rateInfo.setRate(rate);					
					LOG.info("SYMBOL: " + symbolName + ", Bid:" + rs.getBigDecimal("BID") + ", Ask:" + rs.getBigDecimal("ASK") + ", Rate="+rate);
					break;
					
				}			
				if(rate.compareTo(MathUtil.parseBigDecimal(0)) == 0) {
					symbolName = currencyCode + IConstants.CURRENCY_CODE.USD;
					//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - Start
					statement.setString(1, symbolName);
					//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - End 
					rs = statement.executeQuery();			
					while(rs.next()) {
						rateInfo = new RateInfo();
						rate = rs.getBigDecimal("BID");											
						rateInfo.setSymbolName(symbolName);
						rateInfo.setRateType(IConstants.RATE_TYPE.BID);
						rateInfo.setRate(rate);											
						LOG.info("SYMBOL: " + symbolName + ", Bid:" + rs.getBigDecimal("BID") + ", Ask:" + rs.getBigDecimal("ASK") + ", Rate=" + rate);
					}			
				}
				if(rate.compareTo(MathUtil.parseBigDecimal(0)) == 0) {
					LOG.info("Cannot find any rate for currency: " + currencyCode);
				}
			} catch (SQLException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			try {
				if(db!=null){
					db.closeConnection();
					db = null;
				}
			} catch (DBException e) {
				LOG.info(e.getMessage(), e);
				e.printStackTrace();
			}
			LOG.info("[end] open connection to db report");
			return rateInfo;
		}
		
	}
	public RateInfo getRate1(String fromCurrencyCode, String toCurrencyCode, Map<String, String> mapConfig) {
		RateInfo rateInfo = null;
		if (toCurrencyCode.equalsIgnoreCase(fromCurrencyCode)) {
			rateInfo = new RateInfo();
			rateInfo.setRate(MathUtil.parseBigDecimal(1));
			rateInfo.setRateType(IConstants.RATE_TYPE.MID);
			return rateInfo;
		}
		LOG.info("[start] open connection to db report");
		if(db == null) {
			driver = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.DRIVER);
			server = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.SERVER);
			
			username = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.USERNAME);
			password = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.PASSWORD);
			try {
				db = new DatabaseWithoutPool(driver, server, username, password);
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		synchronized (db) {
			BigDecimal rate = MathUtil.parseBigDecimal(0);
			//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - Start 
			String sql = "SELECT * FROM MT4_PRICES WHERE SYMBOL= ? ";
			
			try {
				PreparedStatement statement = db.getConnection().prepareStatement(sql);
				String symbolName = fromCurrencyCode + toCurrencyCode;
				statement.setString(1, symbolName);
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					rateInfo = new RateInfo();
					/*rate = rs.getBigDecimal("ASK");*/
					rate = rs.getBigDecimal("BID");
					rateInfo.setSymbolName(symbolName);
					/*rateInfo.setRateType(IConstants.RATE_TYPE.ASK);*/
					rateInfo.setRateType(IConstants.RATE_TYPE.BID);
					rateInfo.setRate(rate);					
					LOG.info("SYMBOL: " + symbolName + ", Bid:" + rs.getBigDecimal("BID") + ", Ask:" + rs.getBigDecimal("ASK") + ", Rate="+rate);
					break;
					
				}			
				if(rate.compareTo(MathUtil.parseBigDecimal(0)) == 0) {
					symbolName = toCurrencyCode + fromCurrencyCode;
					statement.setString(1, symbolName);
					rs = statement.executeQuery();			
					while(rs.next()) {
						rateInfo = new RateInfo();
						/*rate = rs.getBigDecimal("BID");*/
						rate = rs.getBigDecimal("ASK");	
						rateInfo.setSymbolName(symbolName);
						/*rateInfo.setRateType(IConstants.RATE_TYPE.BID);*/
						rateInfo.setRateType(IConstants.RATE_TYPE.ASK);
						rateInfo.setRate(rate);											
						LOG.info("SYMBOL: " + symbolName + ", Bid:" + rs.getBigDecimal("BID") + ", Ask:" + rs.getBigDecimal("ASK") + ", Rate=" + rate);
						break;
					}			
				}
				if(rate.compareTo(MathUtil.parseBigDecimal(0)) == 0) {
					LOG.info("Cannot find any rate for baseCurrencyCode: " + toCurrencyCode + ", counterCurrencyCode " + fromCurrencyCode);
				}
			} catch (SQLException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			try {
				if(db!=null){
					db.closeConnection();
					db = null;
				}
			} catch (DBException e) {
				LOG.info(e.getMessage(), e);
				e.printStackTrace();
			}
			LOG.info("[end] open connection to db report");
			return rateInfo;
		}
		
	}
	public RateInfo getRateBySymbol1(String symbolName, Map<String, String> mapConfig) {
		LOG.info("[start] open connection to db report");
		RateInfo rateInfo = null;
		if(db == null) {
			driver = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.DRIVER);
			server = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.SERVER);
			
			username = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.USERNAME);
			password = mapConfig.get(IConstants.MT_REPORT_CONFIGURATION.PASSWORD);
			try {
				db = new DatabaseWithoutPool(driver, server, username, password);
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		synchronized (db) {
			BigDecimal bid = MathUtil.parseBigDecimal(0);
			BigDecimal ask = MathUtil.parseBigDecimal(0);
			//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - Start 
			String sql = "SELECT * FROM MT4_PRICES WHERE SYMBOL= ? ";			
			try {
				PreparedStatement statement = db.getConnection().prepareStatement(sql);
				statement.setString(1, symbolName);
				ResultSet rs = statement.executeQuery();	
				//[NTS1.0-dinh.nguyen.xuan]Apr 2, 2014 Fix SQL Injection - TRSPT-968 - End 
				while(rs.next()) {
					rateInfo = new RateInfo();					
					bid = rs.getBigDecimal("BID");
					ask = rs.getBigDecimal("ASK");
					rateInfo.setSymbolName(symbolName);					
					rateInfo.setBid(bid);
					rateInfo.setAsk(ask);
					LOG.info("SYMBOL: " + symbolName + ", Bid:" + rs.getBigDecimal("BID") + ", Ask:" + rs.getBigDecimal("ASK"));
					break;
					
				}											
			} catch (SQLException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (DBException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
			try {
				if(db!=null){
					db.closeConnection();
					db = null;
				}
			} catch (DBException e) {
				LOG.info(e.getMessage(), e);
				e.printStackTrace();
			}
			LOG.info("[end] open connection to db report");
			return rateInfo;
		}
		
	}

	/**
	 * @return the jmsRealSender
	 */
	public IJmsSender getJmsRealSender() {
		return jmsRealSender;
	}

	/**
	 * @param jmsRealSender the jmsRealSender to set
	 */
	public void setJmsRealSender(IJmsSender jmsRealSender) {
		this.jmsRealSender = jmsRealSender;
	}

	/**
	 * @return the jmsDemoSender
	 */
	public IJmsSender getJmsDemoSender() {
		return jmsDemoSender;
	}

	/**
	 * @param jmsDemoSender the jmsDemoSender to set
	 */
	public void setJmsDemoSender(IJmsSender jmsDemoSender) {
		this.jmsDemoSender = jmsDemoSender;
	}

	public static void setInstance(MT4Manager instance) {
		MT4Manager.instance = instance;
	}
}
