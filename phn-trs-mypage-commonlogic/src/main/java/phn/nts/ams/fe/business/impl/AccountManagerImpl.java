package phn.nts.ams.fe.business.impl;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;
import phn.com.components.trs.api.CRMIntegrationAPI;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsCustomerTraceDAO;
import phn.com.nts.db.dao.IAmsDepositDAO;
import phn.com.nts.db.dao.IAmsDepositRefDAO;
import phn.com.nts.db.dao.IAmsExchangerDAO;
import phn.com.nts.db.dao.IAmsExchangerSymbolDAO;
import phn.com.nts.db.dao.IAmsIbClientDAO;
import phn.com.nts.db.dao.IAmsIbDAO;
import phn.com.nts.db.dao.IAmsMessageDAO;
import phn.com.nts.db.dao.IAmsMessageReadTraceDAO;
import phn.com.nts.db.dao.IAmsSubGroupDAO;
import phn.com.nts.db.dao.IAmsSysBankDAO;
import phn.com.nts.db.dao.IAmsTransferMoneyDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWithdrawalDAO;
import phn.com.nts.db.dao.IScBrokerDAO;
import phn.com.nts.db.dao.IScCustomerDAO;
import phn.com.nts.db.dao.IScCustomerServiceDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashBalanceId;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsCustomerTrace;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.db.entity.AmsExchanger;
import phn.com.nts.db.entity.AmsExchangerSymbol;
import phn.com.nts.db.entity.AmsGroup;
import phn.com.nts.db.entity.AmsIb;
import phn.com.nts.db.entity.AmsIbClient;
import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.AmsMessageReadTrace;
import phn.com.nts.db.entity.AmsSubGroup;
import phn.com.nts.db.entity.AmsSysBank;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsTransferMoney;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWhitelabelConfigId;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.db.entity.ScBroker;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.CommonUtil;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Security;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.AMS_WHITELABEL_CONFIG_KEY;
import phn.com.trs.util.common.ITrsConstants.TRS_CONSTANT;
import phn.com.trs.util.enums.ServiceTypeEnum;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BjpInfo;
import phn.nts.ams.fe.domain.BoRegisInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.MessageInfo;
import phn.nts.ams.fe.domain.ScCustomerServiceInfo;
import phn.nts.ams.fe.jms.managers.BoManager;
import phn.nts.ams.fe.model.AppWebUserDetails;
import phn.nts.ams.fe.model.WebUserDetails;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.MailService;

import com.nts.common.Constant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsDepositTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.DepositMethod;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.phn.bo.admin.message.AdminAccountDetailsUpdate;
import com.phn.bo.exchange.bean.AccountInfo;
import com.phn.mt.common.constant.IConstant;
import com.phn.mt.common.entity.UserRecord;
import phn.nts.ams.utils.Helper;

public class AccountManagerImpl implements IAccountManager {

	final int MAX_TIME = 30;
	final Integer PASSWORD_DEFAULT_LENGTH = 8;
	private static Logit log = Logit.getInstance(AccountManagerImpl.class);
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDao;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDao;
	private IAmsIbClientDAO<AmsIbClient> iAmsIbClientDao;
	private IAmsIbDAO<AmsIb> iAmsIbDao;
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO;
	private IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IAmsMessageDAO<AmsMessage> iAmsMessageDAO;
	private IAmsMessageReadTraceDAO<AmsMessageReadTrace> iAmsMessageReadTraceDAO;
	private IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO;
	private IAmsExchangerDAO<AmsExchanger> iAmsExchangerDAO;
	private IAmsExchangerSymbolDAO<AmsExchangerSymbol> iAmsExchangerSymbolDAO;
	private IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO;
	private IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO;
	private IScCustomerDAO<ScCustomer> scCustomerDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> uniqueidCounterDAO;
	private IScBrokerDAO<ScBroker> scBrokerDAO;
	private IAmsDepositDAO<AmsDeposit> iAmsDepositDAO;
	private IAmsDepositRefDAO<AmsDepositRef> iAmsDepositRefDAO;
	private IAmsSysBankDAO<AmsSysBank> iAmsSysBankDAO;
	private IAmsMessageDAO<AmsMessage> amsMessageDAO;
	
	private IJmsContextSender jmsContextSender;
	// send mail
	private MailService mailService;
	private BoManager boManager;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
	
	/**
	 * @return the iAmsIbClientDao
	 */
	public IAmsIbClientDAO<AmsIbClient> getiAmsIbClientDao() {
		return iAmsIbClientDao;
	}
	/**
	 * @param iAmsIbClientDao the iAmsIbClientDao to set
	 */
	public void setiAmsIbClientDao(IAmsIbClientDAO<AmsIbClient> iAmsIbClientDao) {
		this.iAmsIbClientDao = iAmsIbClientDao;
	}
	/**
	 * @return the iAmsCustomerServiceDao
	 */
	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDao() {
		return iAmsCustomerServiceDao;
	}
	/**
	 * @param iAmsCustomerServiceDao the iAmsCustomerServiceDao to set
	 */
	public void setiAmsCustomerServiceDao(
			IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDao) {
		this.iAmsCustomerServiceDao = iAmsCustomerServiceDao;
	}
	
	
	/**
	 * @return the iAmsCustomerDao
	 */
	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDao() {
		return iAmsCustomerDao;
	}


	/**
	 * @param iAmsCustomerDao the iAmsCustomerDao to set
	 */
	public void setiAmsCustomerDao(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDao) {
		this.iAmsCustomerDao = iAmsCustomerDao;
	}
	
	/**
	 * @return the iAmsIbDao
	 */
	public IAmsIbDAO<AmsIb> getiAmsIbDao() {
		return iAmsIbDao;
	}
	/**
	 * @param iAmsIbDao the iAmsIbDao to set
	 */
	public void setiAmsIbDao(IAmsIbDAO<AmsIb> iAmsIbDao) {
		this.iAmsIbDao = iAmsIbDao;
	}
	
	/**
	 * @return the mailService
	 */
	public MailService getMailService() {
		return mailService;
	}
	/**
	 * @param mailService the mailService to set
	 */
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	
	public IAmsCashBalanceDAO<AmsCashBalance> getiAmsCashBalanceDAO() {
		return iAmsCashBalanceDAO;
	}
	public void setiAmsCashBalanceDAO(
			IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO) {
		this.iAmsCashBalanceDAO = iAmsCashBalanceDAO;
	}
	
	public IAmsTransferMoneyDAO<AmsTransferMoney> getiAmsTransferMoneyDAO() {
		return iAmsTransferMoneyDAO;
	}
	public void setiAmsTransferMoneyDAO(
			IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO) {
		this.iAmsTransferMoneyDAO = iAmsTransferMoneyDAO;
	}
	public IAmsWithdrawalDAO<AmsWithdrawal> getiAmsWithdrawalDAO() {
		return iAmsWithdrawalDAO;
	}
	public void setiAmsWithdrawalDAO(
			IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO) {
		this.iAmsWithdrawalDAO = iAmsWithdrawalDAO;
	}
	public IAmsMessageDAO<AmsMessage> getAmsMessageDAO() {
		return amsMessageDAO;
	}
	public void setAmsMessageDAO(IAmsMessageDAO<AmsMessage> amsMessageDAO) {
		this.amsMessageDAO = amsMessageDAO;
	}
	public BoManager getBoManager() {
		return boManager;
	}
	public void setBoManager(BoManager boManager) {
		this.boManager = boManager;
	}
	public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}
	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}
	/**
	 * 　
	 * get front user online 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Jul 24, 2012
	 * @MdDate
	 */
	public FrontUserOnline getFrontUserOnline(String loginId) {
		FrontUserOnline frontUserOnline = new FrontUserOnline();
		List<AmsCustomer> listAmsCustomer = null; 
		AmsCustomer amsCustomer = null;
		listAmsCustomer = getiAmsCustomerDao().findByLoginId(loginId);		
		if(listAmsCustomer != null && listAmsCustomer.size() > 0) {			
			amsCustomer = listAmsCustomer.get(0);
		}
		if(amsCustomer != null) {			
			
			//[TDSBO1.0-Administrator]Aug 14, 2012A - Start - add information of customer 
			try {
				if(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomer.getAllowLoginFlg())) {
					log.info("set information for user");
					log.info("LoginId " + amsCustomer.getLoginId());
					log.info("CustomerId" + amsCustomer.getCustomerId());
					log.info("UserRole " + IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
					log.info("CurrencyCode " + amsCustomer.getSysCurrency().getCurrencyCode());
					log.info("End setting information for user");
					frontUserOnline.setLoginId(amsCustomer.getLoginId());
					frontUserOnline.setPassword(amsCustomer.getLoginPass());
					frontUserOnline.setUserId(amsCustomer.getCustomerId());
					frontUserOnline.setFullName(amsCustomer.getFullName());
					frontUserOnline.setUserRole(IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
					frontUserOnline.setUserGroup(amsCustomer.getAmsGroup());		
					frontUserOnline.setWlCode(amsCustomer.getWlCode());
					frontUserOnline.setPublicKey(amsCustomer.getPublicKey());
					// get list customer service info			
					List<CustomerServicesInfo> listCustomerServiceInfo = getListCustomerServiceInfo(amsCustomer.getCustomerId());			
					frontUserOnline.setListCustomerServiceInfo(listCustomerServiceInfo);
					Map<Integer, Boolean> mapCustomerService = new HashMap<Integer, Boolean>();
                    List<String> serviceIdList = new ArrayList<String>();
					for(CustomerServicesInfo customerServiceInfo : listCustomerServiceInfo) {
						serviceIdList.add(customerServiceInfo.getCustomerServiceId());
						if(IConstants.SERVICES_TYPE.BO.equals(customerServiceInfo.getServiceType()) && (IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(customerServiceInfo.getCustomerServiceStatus()) 
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.CERTIFICATED_DOCSWAITING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.ACCOUNT_OPEN_REQUESTING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.BACK_TO_INSPECT.equals(customerServiceInfo.getCustomerServiceStatus()))) {
							mapCustomerService.put(IConstants.SERVICES_TYPE.BO, Boolean.TRUE);
						}
						if(IConstants.SERVICES_TYPE.DEMO_FXCD.equals(customerServiceInfo.getServiceType())&& (IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(customerServiceInfo.getCustomerServiceStatus()) 
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(customerServiceInfo.getCustomerServiceStatus()))) {
							mapCustomerService.put(IConstants.SERVICES_TYPE.DEMO_FXCD, Boolean.TRUE);
						}
						if(IConstants.SERVICES_TYPE.FX.equals(customerServiceInfo.getServiceType()) && (IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(customerServiceInfo.getCustomerServiceStatus()) 
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.CERTIFICATED_DOCSWAITING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.ACCOUNT_OPEN_REQUESTING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.BACK_TO_INSPECT.equals(customerServiceInfo.getCustomerServiceStatus()))) {
							mapCustomerService.put(IConstants.SERVICES_TYPE.FX, Boolean.TRUE);
							
							log.info("[start] get mt4 id of login " + amsCustomer.getLoginId());
							/*String mt4Id = customerServiceInfo.getCustomerServiceId();
							frontUserOnline.setMt4Id(mt4Id);*/
							log.info("[end] get mt4 id of login " + amsCustomer.getLoginId());
						}
						if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(customerServiceInfo.getServiceType()) && (IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(customerServiceInfo.getCustomerServiceStatus()) 
								|| IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.CERTIFICATED_DOCSWAITING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.ACCOUNT_OPEN_REQUESTING.equals(customerServiceInfo.getCustomerServiceStatus())
								|| IConstants.CUSTOMER_SERVIVES_STATUS.BACK_TO_INSPECT.equals(customerServiceInfo.getCustomerServiceStatus()))) {
							mapCustomerService.put(IConstants.SERVICES_TYPE.COPY_TRADE, Boolean.TRUE);
							String mt4Id = customerServiceInfo.getCustomerServiceId();
							frontUserOnline.setMt4Id(mt4Id);
							
						}
						if(IConstants.SERVICES_TYPE.BO.equals(customerServiceInfo.getServiceType())){
							frontUserOnline.setServiceBo(customerServiceInfo.getCustomerServiceStatus());
						}
						
						if(ServiceTypeEnum.NTD_FX.getIntValue() ==  customerServiceInfo.getServiceType().intValue()){
							mapCustomerService.put(Integer.valueOf(ServiceTypeEnum.NTD_FX.getIntValue()), Boolean.TRUE);
							frontUserOnline.setNtdAccountId(customerServiceInfo.getNtdAccountId());
						}
					}
					// add avatar mode
					frontUserOnline.setAvatarMode(FrontEndContext.getInstance().getAvatarMode());
				
					frontUserOnline.setMapCustomerService(mapCustomerService);
					//huyenmt added
					frontUserOnline.setCurrencyCode(amsCustomer.getSysCurrency().getCurrencyCode());			
					//end huyenmt added
					AmsIb amsIb = getiAmsIbDao().findById(AmsIb.class, amsCustomer.getCustomerId());
					if(amsIb != null) {
						frontUserOnline.setIbType(amsIb.getIbType());
						frontUserOnline.setIbLink(amsIb.getIbLink());
					}
		    		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		    		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		    		if(request != null) {
		    			frontUserOnline.setIpAddress(request.getRemoteAddr());
		    			String userAgent = request.getHeader("user-agent");
		    			if(userAgent != null) {
		    				frontUserOnline.setUserAgent(userAgent);
		    				if(userAgent.contains(IConstants.FRONT_OTHER.IPHONE)) {
		    					frontUserOnline.setUseDevice(IConstants.FRONT_OTHER.IPHONE);
		    					frontUserOnline.setDeviceType(IConstants.DEVICE_TYPE.SMARTPHONE);
		    				} else if(userAgent.contains(IConstants.FRONT_OTHER.ANDROID)) {
		    					frontUserOnline.setUseDevice(IConstants.FRONT_OTHER.ANDROID);
		    					frontUserOnline.setDeviceType(IConstants.DEVICE_TYPE.SMARTPHONE);
		    				} else {
		    					frontUserOnline.setUseDevice(IConstants.FRONT_OTHER.PC);
		    					frontUserOnline.setDeviceType(IConstants.DEVICE_TYPE.PC);
		    				}
		    			}
		    		}
		    		//[AMS1.0-Administrator]Sep 18, 2012A - Start - get exchanger information
		    		if(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomer.getExchangerFlag())) {
		    			AmsExchanger amsExchanger = getiAmsExchangerDAO().getAmsExchanger(amsCustomer.getCustomerId());
			    		if(amsExchanger != null) {
			    			ExchangerInfo exchangerInfo = new ExchangerInfo();
			    			BeanUtils.copyProperties(amsExchanger, exchangerInfo);
			    			exchangerInfo.setCustomerId(amsCustomer.getCustomerId());
			    			frontUserOnline.setExchangerInfo(exchangerInfo);
//			    			frontUserOnline.setIsExchanger(true);
			    		} else {
//			    			frontUserOnline.setIsExchanger(false);
			    		}
			    		
		    		} else {
//		    			frontUserOnline.setIsExchanger(false);
		    		}
		    		
		    		if(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomer.getExchangerFlag())) {
		    			frontUserOnline.setIsExchanger(true);
		    		} else {
		    			frontUserOnline.setIsExchanger(false);
		    		}
		    		
		    		frontUserOnline.setLanguage(amsCustomer.getDisplayLanguage());
					//[AMS1.0-Administrator]Sep 18, 2012A - End
		    		AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
					if(amsSysCountry != null && amsSysCountry.getCountryId() != null) {
						frontUserOnline.setCountryId(amsSysCountry.getCountryId());
						frontUserOnline.setCountryName(amsSysCountry.getCountryName());
						frontUserOnline.setCountryCode(amsSysCountry.getCountryCode());
					}			
					log.info("[start] get list social customer service with customerId = " + amsCustomer.getLoginId());
					List<ScCustomerServiceInfo> listScCustomerServiceInfo = new ArrayList<ScCustomerServiceInfo>();
					List<ScCustomerService> listScCustomerService = scCustomerServiceDAO.getListScCustomerService(amsCustomer.getCustomerId());
					if(listScCustomerService != null && listScCustomerService.size() > 0) {
						for(ScCustomerService item : listScCustomerService) {
							ScCustomerServiceInfo scCustomerServiceInfo = new ScCustomerServiceInfo();
							BeanUtils.copyProperties(item, scCustomerServiceInfo);
							listScCustomerServiceInfo.add(scCustomerServiceInfo);
						}
					}
					frontUserOnline.setListScCustomerServiceInfo(listScCustomerServiceInfo);
					log.info("[end] get list social customer service with customerId = " + amsCustomer.getLoginId());
					log.info("[start] generate ticketId for login " + amsCustomer.getLoginId());
					String randomText = Utilities.generateRandomPassword(8);
					String ticketId = amsCustomer.getLoginId() + "_" + randomText;
					log.info("ticketId of login " + amsCustomer.getLoginId() + " = " + ticketId);
					frontUserOnline.setTicketId(ticketId);
					FrontEndContext.getInstance().putMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + ticketId, serviceIdList);
					log.info("[end] generate ticketId for login " + amsCustomer.getLoginId());
					ScCustomer scCustomer = getScCustomerDAO().findById(ScCustomer.class, amsCustomer.getCustomerId());
					if(scCustomer != null) {
						frontUserOnline.setDescription(scCustomer.getDescription());
						frontUserOnline.setUserName(scCustomer.getUserName());
					}
					frontUserOnline.setHaveAgreementFlg(true);
					if(amsCustomer.getAmsCustomerSurvey()!=null){
						frontUserOnline.setFinalcialSelfAssets(amsCustomer.getAmsCustomerSurvey().getFinancialAssets());
					}else{
						log.warn("can not get survey for customer:"+loginId);
					}
					
				} else {
					log.info("Customer " + amsCustomer.getLoginId() + " cannot login because loginFlag = false");
                    return null;
				}								    	
	    	} catch(Exception ex) {
	    		log.error(ex.getMessage(), ex);
                return null;
	    	}
			//[TDSBO1.0-Administrator]Aug 14, 2012A - End
		}
		
		return frontUserOnline;
	}

    public WebUserDetails getWebUserDetails(String loginId) {
		FrontUserOnline frontUserOnline = new FrontUserOnline();
		AppWebUserDetails webUserDetails = null;
		AmsCustomer amsCustomer = null; 
		amsCustomer = (AmsCustomer) getiAmsCustomerDao().findByLoginId(loginId);		
		if(amsCustomer != null) {			
			frontUserOnline.setLoginId(amsCustomer.getLoginId());
			frontUserOnline.setPassword(amsCustomer.getLoginPass());
			frontUserOnline.setUserId(amsCustomer.getCustomerId());
			frontUserOnline.setUserRole(IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
			frontUserOnline.setUserGroup(amsCustomer.getAmsGroup());
			//huyenmt added
			frontUserOnline.setCurrencyCode(amsCustomer.getSysCurrency().getCurrencyCode());
			//end huyenmt added
			webUserDetails = new AppWebUserDetails(frontUserOnline, IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
		}
		return webUserDetails;
	}
	/**
	 * 　
	 * get user detail 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Jul 24, 2012
	 * @MdDate
	 */
	public FrontUserDetails getUserDetail(String loginId) {
		FrontUserDetails frontUserDetail = null;
		FrontUserOnline frontUserOnline = getFrontUserOnline(loginId);
		if(frontUserOnline != null) {
			frontUserDetail = new FrontUserDetails(frontUserOnline, IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
			if(IConstants.IB_TYPE.NORMAL_IB.equals(frontUserOnline.getIbType())) {
				frontUserDetail = new FrontUserDetails(frontUserOnline, IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
			} else if(IConstants.IB_TYPE.CONTRACT_IB.equals(frontUserOnline.getIbType())) {
				frontUserDetail = new FrontUserDetails(frontUserOnline, IConstants.FRONT_USER_ROLE.ROLE_CONTRACT_IB);
			} else {
				frontUserDetail = new FrontUserDetails(frontUserOnline, IConstants.FRONT_USER_ROLE.ROLE_NORMAL_IB);
			}
//			ExchangerInfo exchangerInfo = frontUserOnline.getExchangerInfo();
//			if(exchangerInfo != null) {
//				// if this customer is exchanger
//				frontUserDetail.addRole(IConstants.FRONT_USER_ROLE.ROLE_EXCHANGER);
//			}
			if(frontUserOnline.getIsExchanger()) {
				// if this customer is exchanger
				frontUserDetail.addRole(IConstants.FRONT_USER_ROLE.ROLE_EXCHANGER);
			}
		}
		
		return frontUserDetail;
	}
	
	public CustomerInfo getCustomerInfoByEmail(String email) {
		CustomerInfo customerInfo = null;
		AmsCustomer amsCustomer = null;
		List<AmsCustomer> listAmsCustomer = getiAmsCustomerDao().findByMailMain(email);
		if(listAmsCustomer != null && listAmsCustomer.size() > 0) {
			amsCustomer = listAmsCustomer.get(0);
		}
		if(amsCustomer != null) {
			customerInfo = new CustomerInfo();
			BeanUtils.copyProperties(amsCustomer, customerInfo);
		}
		
		return customerInfo;
	}
//	public synchronized BalanceInfo getBalanceInfo(String loginId, String password) {
//		BalanceInfo balanceInfo = new BalanceInfo();
//		JfxBalanceInfo jfxBalanceInfo = JfxReceiverProxy.getInstance().getJfxBalanceInfo(loginId, password);
//		if(jfxBalanceInfo != null) {
//			BeanUtils.copyProperties(jfxBalanceInfo, balanceInfo);
//		}		
//		return balanceInfo;
//	}
	/**
	 * 　
	 * get customer services Info
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 4, 2012
	 * @MdDate
	 */
	public CustomerServicesInfo getCustomerServiceInfo(String customerServiceId) {
		CustomerServicesInfo customerServicesInfo = null;
		AmsCustomerService amsCustomerServices = null;
		try {
			
			List<AmsCustomerService> listAmsCustomerServices = (List<AmsCustomerService>) getiAmsCustomerServiceDao().findByCustomerServiceId(customerServiceId);
			if(listAmsCustomerServices != null && listAmsCustomerServices.size() > 0) {
				amsCustomerServices = listAmsCustomerServices.get(0);
			}
			if(amsCustomerServices != null) {
				customerServicesInfo = new CustomerServicesInfo();
				BeanUtils.copyProperties(amsCustomerServices, customerServicesInfo);
				AmsSubGroup amsSubGroup = amsCustomerServices.getAmsSubGroup();
				if(amsSubGroup != null) {
					customerServicesInfo.setSubGroupCode(amsSubGroup.getSubGroupCode());
					customerServicesInfo.setSubGroupId(amsSubGroup.getSubGroupId());
				}
				AmsCustomer amsCustomer = amsCustomerServices.getAmsCustomer();
				if(amsCustomer != null) {
					customerServicesInfo.setCustomerId(amsCustomer.getCustomerId());

					AmsGroup amsGroup = amsCustomer.getAmsGroup();
					if (amsGroup != null) {
						customerServicesInfo.setGroupId(amsGroup.getGroupId());
						customerServicesInfo.setGroupName(amsGroup.getGroupName());
					}

				}
			}
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		return customerServicesInfo;
	}
	public List<CustomerServicesInfo> getListCustomerServiceInfo(String customerId) {
		List<CustomerServicesInfo> listCustomerServiceInfo = new ArrayList<CustomerServicesInfo>();
		CustomerServicesInfo customerServicesInfo = null;		
		try {
			
			List<AmsCustomerService> listAmsCustomerServices = (List<AmsCustomerService>) getiAmsCustomerServiceDao().findByCustomerServiceByCusId(customerId);
			if(listAmsCustomerServices != null && listAmsCustomerServices.size() > 0) {
				listCustomerServiceInfo = new ArrayList<CustomerServicesInfo>();
				for(AmsCustomerService amsCustomerService : listAmsCustomerServices) {
					customerServicesInfo = new CustomerServicesInfo();
					BeanUtils.copyProperties(amsCustomerService, customerServicesInfo);
					AmsSubGroup amsSubGroup = amsCustomerService.getAmsSubGroup();
					if(amsSubGroup != null) {
						customerServicesInfo.setSubGroupId(amsSubGroup.getSubGroupId());
						customerServicesInfo.setCurrencyCode(amsSubGroup.getCurrencyCode());
					}
					AmsCustomer amsCustomer = amsCustomerService.getAmsCustomer();
					if(amsCustomer != null) {
						customerServicesInfo.setCustomerId(amsCustomer.getCustomerId());
					}
					listCustomerServiceInfo.add(customerServicesInfo);
				}
			}			
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		return listCustomerServiceInfo;
	}

	/**
	 *  get list of customer service which has CustomerServiceStatus is 1 or 12　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 22, 2012
	 * @MdDate
	 */
	public List<CustomerServicesInfo> getListCustomerServiceByServiceTypes(String customerId, List<Integer> serviceTypes) {
		List<CustomerServicesInfo> listCustomerServiceInfo = null;
		CustomerServicesInfo customerServicesInfo = null;		
		try {
			
			SearchResult<AmsCustomerService> listAmsCustomerServices = getiAmsCustomerServiceDao().getListCustomerServicesByServiceTypes(customerId, serviceTypes);
			if(listAmsCustomerServices != null && listAmsCustomerServices.size() > 0) {
				listCustomerServiceInfo = new ArrayList<CustomerServicesInfo>();
				for(AmsCustomerService amsCustomerService : listAmsCustomerServices) {
					customerServicesInfo = new CustomerServicesInfo();
					BeanUtils.copyProperties(amsCustomerService, customerServicesInfo);
					AmsSubGroup amsSubGroup = amsCustomerService.getAmsSubGroup();
					if(amsSubGroup != null) {
						customerServicesInfo.setSubGroupId(amsSubGroup.getSubGroupId());
						customerServicesInfo.setCurrencyCode(amsSubGroup.getCurrencyCode());
					}
					AmsCustomer amsCustomer = amsCustomerService.getAmsCustomer();
					if(amsCustomer != null) {
						customerServicesInfo.setCustomerId(amsCustomer.getCustomerId());
					}
					//check status of customerserviceinfo account
					if(IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER.equals(amsCustomerService.getCustomerServiceStatus()) || IConstants.CUSTOMER_SERVIVES_STATUS.CANCEL.equals(amsCustomerService.getCustomerServiceStatus())){
						listCustomerServiceInfo.add(customerServicesInfo);
					}
				}
			}			
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		return listCustomerServiceInfo;
	}

    @Override
    public boolean isExistMail(String email) {
        try {
            List<AmsCustomer> listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
            if(listAmsCustomer.size() > 0) return true;
            listAmsCustomer = iAmsCustomerDao.findByMailAddtional(email);
            if(listAmsCustomer.size() > 0) return true;
            listAmsCustomer = iAmsCustomerDao.findByMailMobile(email);
            if(listAmsCustomer.size() > 0) return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean isExistPhone(String phoneNumber) {
        try {
            List<AmsCustomer> listAmsCustomer = getiAmsCustomerDao().findByTel1(phoneNumber);
            if(listAmsCustomer.size() > 0) return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
	 *  get list customer service info　
	 * 
	 * @param String customerId, Integer serviceType
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 22, 2012
	 * @MdDate
	 */
	public CustomerServicesInfo getCustomerServiceInfo(String customerId, Integer serviceType) {
		CustomerServicesInfo customerServiceInfo = null;
		AmsCustomerService amsCustomerServices = null;
		List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDao().getListCustomerServicesInfo(customerId, serviceType);
		if(listAmsCustomerService != null && listAmsCustomerService.size() > 0) {
			amsCustomerServices = listAmsCustomerService.get(0);
		
		}
		if(amsCustomerServices != null) {
			customerServiceInfo = new CustomerServicesInfo();
			BeanUtils.copyProperties(amsCustomerServices, customerServiceInfo);
			AmsSubGroup amsSubGroup = amsCustomerServices.getAmsSubGroup();
			if(amsSubGroup != null) {
				customerServiceInfo.setSubGroupCode(amsSubGroup.getSubGroupCode());
				customerServiceInfo.setSubGroupId(amsSubGroup.getSubGroupId());
				customerServiceInfo.setCurrencyCode(amsSubGroup.getCurrencyCode());
			}
			AmsCustomer amsCustomer = amsCustomerServices.getAmsCustomer();
			if(amsCustomer != null) {
				customerServiceInfo.setCustomerId(amsCustomer.getCustomerId());

				AmsGroup amsGroup = amsCustomer.getAmsGroup();
				if (amsGroup != null) {
					customerServiceInfo.setGroupId(amsGroup.getGroupId());
					customerServiceInfo.setGroupName(amsGroup.getGroupName());
				}
			}
			AmsWhitelabel amsWhiteLabel = amsCustomerServices.getAmsWhitelabel();			
			if(amsWhiteLabel != null) {
				customerServiceInfo.setWlCode(amsWhiteLabel.getWlCode());
			}
			
		}
		return customerServiceInfo;
	}
	
	
	
	
	public CustomerInfo getCustomerInfo(String customerId, String clientCustomerId) {
		CustomerInfo customerInfo = null;
		AmsCustomer amsCustomer = null;
		AmsIbClient amsIbClient = getiAmsIbClientDao().getAmsIbClient(customerId, clientCustomerId);
		if(amsIbClient != null) {
			amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(clientCustomerId);
			amsCustomer = getiAmsCustomerDao().findById(AmsCustomer.class, clientCustomerId);			
			if(amsCustomer != null) {
				customerInfo = new CustomerInfo();
				BeanUtils.copyProperties(amsCustomer, customerInfo);
				SysCurrency amsSysCurrency = amsCustomer.getSysCurrency();
				if(amsSysCurrency != null) {
					customerInfo.setCurrencyCode(amsSysCurrency.getCurrencyCode());
					customerInfo.setCurrencyAms(amsSysCurrency.getCurrencyCode());
				}
				AmsGroup amsGroup = amsCustomer.getAmsGroup();
				if(amsGroup != null) {
					customerInfo.setGroupId(amsGroup.getGroupId());
					customerInfo.setGroupName(amsGroup.getGroupName());
				}
				AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
				if(amsSysCountry != null) {
					customerInfo.setCountryId(amsSysCountry.getCountryId());
				}
				// don't show password on jquery json
				customerInfo.setLoginPass(IConstants.FRONT_OTHER.PASSWORD_MARK_DEFAULT);
				
				// set currency
				Set<AmsCustomerService> services = amsCustomer.getAmsCustomerServices();
				if (services == null) {
					log.warn("AccountManagerImpl.getCustomerInfo() not exist customer service customerId = " + clientCustomerId);
				} else {
					for (AmsCustomerService s : services) {
						String currency = null;
						AmsSubGroup subGroup = s.getAmsSubGroup();
						if (subGroup != null) {
							currency = s.getAmsSubGroup().getCurrencyCode();
						} else {
							log.warn("Not exist subgroup of customer service, customerId = " + clientCustomerId);
						}

						if (s.getServiceType() != null && s.getServiceType().intValue() == IConstants.SERVICES_TYPE.BO.intValue()) {
							customerInfo.setCurrencyBo(currency);
						} else if (s.getServiceType() != null && s.getServiceType().intValue() == IConstants.SERVICES_TYPE.FX.intValue()) {
							customerInfo.setCurrencyFx(currency);
						} else if (s.getServiceType() != null && s.getServiceType().intValue() == IConstants.SERVICES_TYPE.COPY_TRADE.intValue()) {
                            customerInfo.setCurrencyCopyTrade(currency);
                        }
					}
				}
			}
		}
		return customerInfo;
	}
	
	public CustomerInfo getCustomerInfo(String customerId) {
		CustomerInfo customerInfo = null;
		AmsCustomer amsCustomer = getiAmsCustomerDao().findById(AmsCustomer.class, customerId);
		if(amsCustomer != null) {			
			customerInfo = new CustomerInfo();
			BeanUtils.copyProperties(amsCustomer, customerInfo);
			SysCurrency amsSysCurrency = amsCustomer.getSysCurrency();
			if(amsSysCurrency != null) {
				customerInfo.setCurrencyCode(amsSysCurrency.getCurrencyCode());					
			}
			AmsGroup amsGroup = amsCustomer.getAmsGroup();
			if(amsGroup != null) {
				customerInfo.setGroupId(amsGroup.getGroupId());
				customerInfo.setGroupName(amsGroup.getGroupName());
			}
			AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
			if(amsSysCountry != null) {
				customerInfo.setCountryId(amsSysCountry.getCountryId());
			}
											
		}
		return customerInfo;
	}

	
	
	public BalanceInfo getCashBalanceInfo(String customerId,String currencyCode, Integer serviceType) {
		BalanceInfo balanceInfo = null;
		try{
			AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
			amsCashBalanceId.setCurrencyCode(currencyCode);
			amsCashBalanceId.setCustomerId(customerId);
			amsCashBalanceId.setServiceType(serviceType);
			AmsCashBalance amsCashBalance = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
			if( amsCashBalance != null ) {
				balanceInfo = new BalanceInfo();
				balanceInfo.setBalance(amsCashBalance.getCashBalance());
				
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return balanceInfo;
	}
	
	/**
	 * getAppDate　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Feb 19, 2013
	 */
	private SysAppDate getAppDate() {
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		SysAppDate amsAppDate = null;
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		if(amsAppDate == null) {
			log.warn("Cannot find front date ");
			return null;
		}
		
		return amsAppDate;
	}

	public void updateAmsCustomerServices(CustomerServicesInfo customerServiceInfo) throws Exception {
		AmsCustomerService amsCustomerService = new AmsCustomerService();		
		BeanUtils.copyProperties(customerServiceInfo, amsCustomerService);
		SysAppDate amsAppDate = null;
		List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
		if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
			amsAppDate = listAmsAppDate.get(0);
		}
		if(amsAppDate == null) {
			log.warn("Cannot find front date ");
			return;
		}
		amsCustomerService.setAccountOpenDate(amsAppDate.getId().getFrontDate());
		amsCustomerService.setAccountStatusChangeDate(amsAppDate.getId().getFrontDate());
		amsCustomerService.setAccountStatusChangeDatetime(new Timestamp(System.currentTimeMillis()));
		AmsCustomer amsCustomer = new AmsCustomer();
		amsCustomer.setCustomerId(customerServiceInfo.getCustomerId());
		amsCustomerService.setAmsCustomer(amsCustomer);
		AmsWhitelabel amsWhitelabel = new AmsWhitelabel();
		amsWhitelabel.setWlCode(customerServiceInfo.getWlCode());
		amsCustomerService.setAmsWhitelabel(amsWhitelabel);
		AmsSubGroup amsSubGroup = new AmsSubGroup();
		amsSubGroup.setSubGroupId(customerServiceInfo.getSubGroupId());
		amsCustomerService.setAmsSubGroup(amsSubGroup);
		getiAmsCustomerServiceDao().merge(amsCustomerService);				
	}
	
	/**
	 * @return the iSysAppDateDAO
	 */
	public ISysAppDateDAO<SysAppDate> getiSysAppDateDAO() {
		return iSysAppDateDAO;
	}
	/**
	 * @param iSysAppDateDAO the iSysAppDateDAO to set
	 */
	public void setiSysAppDateDAO(ISysAppDateDAO<SysAppDate> iSysAppDateDAO) {
		this.iSysAppDateDAO = iSysAppDateDAO;
	}
	
	/**
	 * get message from DB
	 */
	public List<MessageInfo> getMessageList(String customerId) {
		List<MessageInfo> listMessageInfo = new ArrayList<MessageInfo>();
		MessageInfo messageInfo = null;
		try{
			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
			
			List<AmsMessage> listAmsMessage = getiAmsMessageDAO().searchMessagebyCondition(customerId);
			if(listAmsMessage != null && listAmsMessage.size()>0) {
				for(AmsMessage amsMessage : listAmsMessage) {
					messageInfo = new MessageInfo();
					BeanUtils.copyProperties(amsMessage, messageInfo);					
					AmsMessageReadTrace amsMessageReadTrace = getiAmsMessageReadTraceDAO().getAmsMessageReadTrace(customerId, messageInfo.getMessageId());
					if(amsMessageReadTrace == null) {
						messageInfo.setReadFlg(false);
					} else {
						messageInfo.setReadFlg(true);
					}
					String serviceName = mapServiceType.get(StringUtil.toString(amsMessage.getServiceType()));
					messageInfo.setServiceName(serviceName);
					if(IConstants.DISPLAY_OBJECT.ACCOUNT_NUMBER.equals(amsMessage.getDisplayObjectType())) {
						if(IConstants.ACTIVE_FLG.ACTIVE.equals(amsMessage.getCustomerDeleteFlg())) {
							messageInfo.setAllowDeleteFlag(true);
						}
						
					}
					listMessageInfo.add(messageInfo);				
				}
			}			
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return listMessageInfo;
	}
	
	public List<MessageInfo> getMessageList(String customerId, PagingInfo pagingInfo) {
		List<MessageInfo> listMessageInfo = new ArrayList<MessageInfo>();
		MessageInfo messageInfo = null;
		try{
			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
			
			List<AmsMessage> listAmsMessage = iAmsMessageDAO.searchMessagebyCondition(customerId, pagingInfo);
			if(listAmsMessage != null && listAmsMessage.size()>0) {
				for(AmsMessage amsMessage : listAmsMessage) {
					messageInfo = new MessageInfo();
					BeanUtils.copyProperties(amsMessage, messageInfo);					
					AmsMessageReadTrace amsMessageReadTrace = getiAmsMessageReadTraceDAO().getAmsMessageReadTrace(customerId, messageInfo.getMessageId());
					if(amsMessageReadTrace == null) {
						messageInfo.setReadFlg(false);
					} else {
						messageInfo.setReadFlg(true);
					}
					String serviceName = mapServiceType.get(StringUtil.toString(amsMessage.getServiceType()));
					messageInfo.setServiceName(serviceName);
					if(IConstants.DISPLAY_OBJECT.ACCOUNT_NUMBER.equals(amsMessage.getDisplayObjectType())) {
						if(IConstants.ACTIVE_FLG.ACTIVE.equals(amsMessage.getCustomerDeleteFlg())) {
							messageInfo.setAllowDeleteFlag(true);
						}
						
					}
					listMessageInfo.add(messageInfo);				
				}
			}			
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return listMessageInfo;
	}
	
	public IAmsMessageDAO<AmsMessage> getiAmsMessageDAO() {
		return iAmsMessageDAO;
	}
	public void setiAmsMessageDAO(IAmsMessageDAO<AmsMessage> iAmsMessageDAO) {
		this.iAmsMessageDAO = iAmsMessageDAO;
	}
	/**
	 * @return the iAmsMessageReadTraceDAO
	 */
	public IAmsMessageReadTraceDAO<AmsMessageReadTrace> getiAmsMessageReadTraceDAO() {
		return iAmsMessageReadTraceDAO;
	}
	/**
	 * @param iAmsMessageReadTraceDAO the iAmsMessageReadTraceDAO to set
	 */
	public void setiAmsMessageReadTraceDAO(
			IAmsMessageReadTraceDAO<AmsMessageReadTrace> iAmsMessageReadTraceDAO) {
		this.iAmsMessageReadTraceDAO = iAmsMessageReadTraceDAO;
	}
	
	/**
	 * @return the iAmsSubGroupDAO
	 */
	public IAmsSubGroupDAO<AmsSubGroup> getiAmsSubGroupDAO() {
		return iAmsSubGroupDAO;
	}
	/**
	 * @param iAmsSubGroupDAO the iAmsSubGroupDAO to set
	 */
	public void setiAmsSubGroupDAO(IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO) {
		this.iAmsSubGroupDAO = iAmsSubGroupDAO;
	}
	
	public Boolean registerCustomerService(CustomerInfo customerInfo, Integer serviceType) {
		try {			
			String wlCode = customerInfo.getWlCode();
			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
			
			AmsCustomer amsCustomer = getiAmsCustomerDao().findById(AmsCustomer.class, customerInfo.getCustomerId());
			if (amsCustomer == null) {
				log.warn("Can not find customer with customer id = " + customerInfo.getCustomerId());
				return false;
			}
			String customerId = customerInfo.getCustomerId();

			if(IConstants.FRONT_OTHER.FX_BO_SERVICE.equals(serviceType)) {
				log.info("Regist FX-BO service");
				SysAppDate sysAppDate = null;
				List<SysAppDate> listSysAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
				if(listSysAppDate != null && listSysAppDate.size() > 0) {
					sysAppDate = listSysAppDate.get(0);
				} else {
					log.info("Cannot find AppDate with Date Key = Front Date");
					return Boolean.FALSE;
				}

				if(sysAppDate == null) {
					log.info("Cannot find AppDate with Date Key = Front Date");
					return Boolean.FALSE;
				}

				List<AmsCustomerService> customerServices = iAmsCustomerServiceDao.findByCustomerId(customerId);

				AmsCustomerService serviceFx = null;
				AmsCustomerService serviceBo = null;

				// find services
				for (AmsCustomerService service : customerServices) {
					if (service != null && service.getServiceType() != null && service.getServiceType().intValue() == IConstants.SERVICES_TYPE.FX.intValue()) {
						serviceFx = service;
					} else if (service != null && service.getServiceType() != null && service.getServiceType().intValue() == IConstants.SERVICES_TYPE.BO.intValue()) {
						serviceBo = service;
					}
				}
				
				if (serviceFx == null) {
					log.warn("Can not find CUSTOMER SERVICE FX");
					return false;
				}
				if (serviceBo == null) {
					log.warn("Can not find CUSTOMER SERVICE BO");
					return false;
				}

				// generate pass
				String masterPassword = Utilities.generateRandomPassword(IConstants.MAX_LENGTH.ACCOUNT_PASSWORD_DEFAULT_LENGTH);
				String investorPass = Utilities.generateRandomPassword(IConstants.MAX_LENGTH.ACCOUNT_PASSWORD_DEFAULT_LENGTH);
				String mt4Account = serviceFx.getCustomerServiceId();
				log.info("[start] update fx account on MT4 " + mt4Account);
	
				UserRecord userRecord = new UserRecord();
				userRecord.setLogin(MathUtil.parseInt(mt4Account));
				userRecord.setEnable(IConstants.ACTIVE_FLG.ACTIVE);
				userRecord.setEnableReadOnly(IConstants.ACTIVE_FLG.INACTIVE);
				userRecord.setPassword(masterPassword);
				userRecord.setPasswordInvestor(investorPass);

				Integer result = MT4Manager.getInstance().updateAccountMt4(userRecord);
//				Integer result = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
				log.info("[end] update fx account on MT4 " + mt4Account);

				if(IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(result)) {
					updateStatusCustomerService(serviceFx, sysAppDate, IConstants.ACCOUNT_OPEN_STATUS.VERIFY_DOCS_WAITING);
					updateStatusCustomerService(serviceBo, sysAppDate, IConstants.ACCOUNT_OPEN_STATUS.VERIFY_DOCS_WAITING);

					registCustomerTrace(customerId, IConstants.SERVICES_TYPE.FX, mapServiceType, amsCustomer, serviceFx.getCustomerServiceId());
					registCustomerTrace(customerId, IConstants.SERVICES_TYPE.BO, mapServiceType, amsCustomer, serviceBo.getCustomerServiceId());

					// update customer
					log.info("[start]Update ServiceTypeFX of AMS_CUSTOMER");
					amsCustomer.setServiceTypeFX(IConstants.ACTIVE_FLG.ACTIVE);
					amsCustomer.setServiceTypeBO(IConstants.ACTIVE_FLG.ACTIVE);
					amsCustomer.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					Md5PasswordEncoder e = new Md5PasswordEncoder();
					String encryptPass = e.encodePassword(masterPassword, null);
					String encryptInvestorPass = e.encodePassword(investorPass, null);
					amsCustomer.setLoginPass(encryptPass);
					amsCustomer.setInvestorPass(encryptInvestorPass);
					getiAmsCustomerDao().merge(amsCustomer);
					log.info("[end]Update ServiceTypeFX of AMS_CUSTOMER");
					
					sendmailUpdateSuccess(wlCode, amsCustomer, mt4Account, masterPassword, investorPass);	
				} else {
					log.warn("register with MT4 failure");
					return false;
				}
			} else if(IConstants.SERVICES_TYPE.DEMO_FXCD.equals(serviceType)) { 
				log.info("Regist DEMO FX");
//				CustomerServicesInfo customerServiceInfo = getCustomerServiceInfo(customerInfo.getCustomerId(), serviceType);
				AmsCustomerService serviceDemo = iAmsCustomerServiceDao.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.DEMO_FXCD);
				String masterPassword = "";
				if (serviceDemo == null) {
					// find subgroup
					String currency = customerInfo.getCurrencyAms();
					if (StringUtil.isEmpty(currency)) {
						currency = IConstants.CURRENCY_CODE.USD;
					}

					// generate Id Customer  Service Demo
					String loginId = generateKey("8", IConstants.UNIQUE_CONTEXT.CUSTOMER_CONTEXT_DEMO_FX, 8);

					String configKey = currency + IConstants.WHITE_LABEL_CONFIG.SUFFIX_DEMO_FX;
					AmsWhitelabelConfigId configId = new AmsWhitelabelConfigId(configKey, wlCode);
					AmsWhitelabelConfig amsConfig = iAmsWhitelabelConfigDAO.findById(AmsWhitelabelConfig.class, configId);
					Integer subGroupDemoFx = null;
					String subGroupCode = null;
					if (amsConfig == null) {
						log.warn("can not find whitelabel config with wlCode = " + wlCode + " ad config key = " + configKey);
						return Boolean.FALSE;
					} else {
						subGroupCode = amsConfig.getConfigValue();
					}

					AmsSubGroup subGroupDemo = iAmsSubGroupDAO.findBySubGroupCode(subGroupCode);
					if (subGroupDemo == null) {
						log.warn("Can not find subgroup with serviceType = " + serviceType + ", wlCode = " + wlCode + ", currency = " + currency);
						return Boolean.FALSE;
					}

					subGroupDemoFx = subGroupDemo.getSubGroupId();
					String investorPassword = Utilities.generateRandomPassword(IConstants.MAX_LENGTH.ACCOUNT_PASSWORD_DEFAULT_LENGTH);
					masterPassword = Utilities.generateRandomPassword(IConstants.MAX_LENGTH.ACCOUNT_PASSWORD_DEFAULT_LENGTH);
					Integer registerAccountResult = MT4Manager.getInstance().registerMT4DemoAccount(customerInfo, "" + loginId, wlCode, subGroupDemo.getSubGroupCode(), subGroupDemo.getLeverage(), investorPassword, masterPassword, null);
//					Integer registerAccountResult = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
					if(!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
						log.warn("Create Demo FX fail: customerId = " + customerId + " wlCode = " + wlCode + " SubGroupCode = " + subGroupDemo.getSubGroupCode());
						return Boolean.FALSE;
					}

					log.info("Create Customer Demo Fx");
					CustomerServicesInfo customerServiceInfo = new CustomerServicesInfo();
					customerServiceInfo.setCustomerId(customerId);
					customerServiceInfo.setCustomerServiceId(loginId);
					customerServiceInfo.setWlCode(wlCode);
					customerServiceInfo.setSubGroupId(subGroupDemoFx);
					customerServiceInfo.setServiceType(IConstants.SERVICES_TYPE.DEMO_FXCD);

					boolean isValid = saveCustomerService(customerServiceInfo);
					if (isValid) {
					} else {
						log.warn("Create Customer Demo Fx false");
						return Boolean.FALSE;
					}
					
					// insert ams customer trace
					registCustomerTrace(amsCustomer, customerServiceInfo, customerInfo, mapServiceType, serviceType);
					
					// insert sc demo customer service
					
					insertScCustomerService(amsCustomer, wlCode, loginId, IConstants.SERVICES_TYPE.DEMO_FXCD, subGroupDemo, IConstants.SC_ACCOUNT_KIND.DEMO,IConstants.SC_ACCOUNT_TYPE.SIGNAL_PROVIDER);
					
					// send mail template
					sendMailOpenDemoFx(wlCode, amsCustomer, customerServiceInfo.getCustomerServiceId(), masterPassword);
				} else {
					Integer customerServiceId = MathUtil.parseInteger(serviceDemo.getCustomerServiceId());
					
					if (customerServiceId == null) {
						log.warn("Error customer service id " + customerServiceId);
						return false;
					}
					String masterPasswordDemo = Utilities.generateRandomPassword(IConstants.MAX_LENGTH.ACCOUNT_PASSWORD_DEFAULT_LENGTH);

					Integer registerAccountResult = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
					if(!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
						log.warn("Create Demo FX fail: customerServiceId = " + customerServiceId);
						return Boolean.FALSE;
					} else {
						serviceDemo.setCustomerServiceStatus(IConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED);
						serviceDemo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
						iAmsCustomerServiceDao.merge(serviceDemo);

						registCustomerTrace(customerId, IConstants.SERVICES_TYPE.DEMO_FXCD, mapServiceType, amsCustomer, String.valueOf(customerServiceId));

						sendMailOpenDemoFx(wlCode, amsCustomer, String.valueOf(customerServiceId), masterPasswordDemo);
					}
				}
			}
			
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 1, 2013
	 */
	private void sendMailOpenDemoFx(String wlCode, AmsCustomer amsCustomer, String customerServiceId, String masterPassword) {
		log.info("[start] send mail about open account demo fx successful");
		String language = amsCustomer.getDisplayLanguage();
		if(language == null || StringUtils.isBlank(language)) {
			language = IConstants.Language.ENGLISH;
		}
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_DEMOFX).append("_").append(language).toString();

		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setCustomerServiceId(customerServiceId);
		amsMailTemplateInfo.setLoginPass(masterPassword);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(wlCode);					

		log.info("Mail Content: " + amsMailTemplateInfo);
		
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about open account demo fx successful");
	}
	
	private void registCustomerTrace(String customerId, Integer serviceType, Map<String, String> mapServiceType, AmsCustomer amsCustomer, String mt4Account) {
		log.info("[start] insert customer trace for servicetype = " + serviceType + " and customerId = " + customerId);
		AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
		amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));					
		amsCustomerTrace.setValue1("");
		amsCustomerTrace.setValue2("");		
		amsCustomerTrace.setServiceType(serviceType);
		amsCustomer.setCustomerId(customerId);
		amsCustomerTrace.setAmsCustomer(amsCustomer);
		String serviceName = mapServiceType.get(StringUtil.toString(serviceType));
		amsCustomerTrace.setReason("Register " + serviceName + " account :" + mt4Account);
		getiAmsCustomerTraceDAO().save(amsCustomerTrace);
		log.info("[end] insert customer trace for servicetype = " + serviceType + " and customerId = " + customerId);
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 1, 2013
	 */
	private void sendmailUpdateSuccess(String wlCode, AmsCustomer amsCustomer, String mt4Account, String password, String investorPass) {
		log.info("[start] send mail about open account bo successful");
		String language = amsCustomer.getDisplayLanguage();
		if(language == null || StringUtils.isBlank(language)) {
			language = IConstants.Language.ENGLISH;
		}
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_FXBO).append("_").append(language).toString();
			
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setCustomerServiceId(mt4Account);
		amsMailTemplateInfo.setLoginPass(password);
		amsMailTemplateInfo.setInvestorPass(investorPass);

		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(wlCode);

		log.info("Mail Content: " + amsMailTemplateInfo);
		
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about open account bo successful");
	}
	
	/**
	 * updateStatusCustomerService　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 1, 2013
	 */
	private void updateStatusCustomerService(AmsCustomerService service, SysAppDate sysAppDate, int verifyDocsWaiting) {
		log.info("[START]Regis customer service: CUSTOMER_SERVICE_ID = " + service.getCustomerServiceId() + " SERVICE_TYPE = " + service.getServiceType());
		Timestamp current = new Timestamp(System.currentTimeMillis());

//		service.setAllowTransactFlg(IConstants.ACTIVE_FLG.ACTIVE);
//		service.setAllowSendmoneyFlg(IConstants.ACTIVE_FLG.ACTIVE);
//		service.setAllowLoginFlg(IConstants.ACTIVE_FLG.ACTIVE);
		service.setCustomerServiceStatus(verifyDocsWaiting);
		service.setAccountOpenDate(sysAppDate.getId().getFrontDate());
		service.setAccountStatusChangeDate(sysAppDate.getId().getFrontDate());
		service.setAccountStatusChangeDatetime(current);
		service.setUpdateDate(current);
		
		iAmsCustomerServiceDao.merge(service);
		log.info("[START]Regis customer service: CUSTOMER_SERVICE_ID = " + service.getCustomerServiceId() + " SERVICE_TYPE = " + service.getServiceType());
	}
	
	private boolean saveCustomerService(CustomerServicesInfo info) {
		log.info("[start]AccountManagerImpl.saveCustomerService()");
		int serviceType = info.getServiceType();
		AmsCustomerService service = new AmsCustomerService();
		AmsCustomer amsCustomer = new AmsCustomer(info.getCustomerId());
		service.setAmsCustomer(amsCustomer);
		service.setServiceType(serviceType);
		Integer subGroupId = info.getSubGroupId();
		String customerServiceId = info.getCustomerServiceId();

		// ID
		AmsSubGroup subGroup = new AmsSubGroup();
		subGroup.setSubGroupId(subGroupId);
		service.setAmsSubGroup(subGroup);

		service.setCustomerServiceId(customerServiceId);
		service.setCustomerServiceStatus(IConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED);
//		service.setAllowTransactFlg(IConstants.ALLOW_FLG.ALLOW);
//		service.setAllowSendmoneyFlg(IConstants.ALLOW_FLG.ALLOW);
//		service.setAllowLoginFlg(IConstants.ALLOW_FLG.ALLOW);
//		service.setAgreementFlg(IConstants.AGREEMENT_FLG.AGREE);

		SysAppDate appDate = getAppDate();
		if (appDate != null) {
			service.setAccountOpenDate(appDate.getId().getFrontDate());
		} else {
			service.setAccountOpenDate(DateUtil.getCurrentDateTime(DateUtil.PATTERN_YYMMDD_BLANK));
		}

		AmsWhitelabel amsWhitelabel = new AmsWhitelabel();
		amsWhitelabel.setWlCode(info.getWlCode());

		service.setAmsWhitelabel(amsWhitelabel);
		service.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		Timestamp current = new Timestamp(System.currentTimeMillis());
		service.setInputDate(current);
		service.setUpdateDate(current);

		// save to DB
		try {
			iAmsCustomerServiceDao.save(service);
		} catch (Exception e) {
			return false;
		}
		//write log
		log.info("[end]AccountManagerImpl.saveCustomerService()");
		return true;
	}

	private void registCustomerTrace(AmsCustomer amsCustomer, CustomerServicesInfo customerServiceInfo, CustomerInfo customerInfo, Map<String, String> mapServiceType, Integer serviceType) {
		log.info("[start] insert customer trace for servicetype = " + serviceType + " and customerId = " + customerInfo.getCustomerId());
		AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
		amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
		amsCustomerTrace.setServiceType(serviceType);
		amsCustomerTrace.setValue1("");
		amsCustomerTrace.setValue2("");				
		amsCustomer.setCustomerId(customerInfo.getCustomerId());
		amsCustomerTrace.setAmsCustomer(amsCustomer);
		String serviceName = mapServiceType.get(StringUtil.toString(serviceType));
		amsCustomerTrace.setReason("Register " + serviceName + " account :" + customerServiceInfo.getCustomerServiceId());
		getiAmsCustomerTraceDAO().save(amsCustomerTrace);
		log.info("[end] insert customer trace for servicetype = " + serviceType + " and customerId = " + customerInfo.getCustomerId());
	}
	public ScCustomerService insertScCustomerService(AmsCustomer amsCustomer, String wlCode, String customerServiceId, Integer serviceType, AmsSubGroup amsSubGroup, Integer accountKind,Integer accountType) {
		ScCustomerService scCustomerService = new ScCustomerService();
//		try{
			// Register AMS_CUSTOMER_SERVICE
			
//			scCustomerService.setScCustServiceId(scCustServiceId);
			scCustomerService.setCustomerId(amsCustomer.getCustomerId());
			
//			AmsSubGroup amsSubGroup = null;
//			String configKey = amsCustomer.getSysCurrency().getCurrencyCode() + suffix;		
//			log.info("whitelabel config for configKey: " + configKey + ", wlCode: " + amsCustomer.getWlCode());
//			log.info("[start] get white label config with WL_CODE = " + amsCustomer.getWlCode());
//			AmsWhitelabel amsWhitelabel = getiAmsWhitelabelDAO().getAmsWhiteLabel(amsCustomer.getWlCode());
//			log.info("[end] get white label config with WL_CODE = " + amsCustomer.getWlCode());
//			log.info("[start] get leverage and groupname of subgroup base on IB CUSTOMER ID");
//			Integer leverage = new Integer(500); // 500: Default
//			if(ibCustomerId != null && !StringUtil.isEmpty(ibCustomerId)) {
//				AmsCustomerService amsCustomerIbService = getiAmsCustomerServiceDAO().getCustomerServicesInfo(ibCustomerId, serviceType);
//				if(amsCustomerIbService != null) {
//					amsSubGroup = amsCustomerIbService.getAmsSubGroup();
//				}						
//			}
//			if(amsSubGroup == null) {
//				Map<String, String> mapWhiteLabelConfig = (Map<String, String>) SystemCaching.getInstance().getCache(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + amsCustomer.getWlCode());
//				String subGroupCode = mapWhiteLabelConfig.get(configKey);				
//				amsSubGroup = getiAmsSubGroupDAO().findBySubGroupCode(subGroupCode);
//			}
			Integer leverage = new Integer(500); // 500: Default
			if(amsSubGroup != null) {			
				leverage = amsSubGroup.getLeverage();
			} else {
				log.warn("ams sub group is null");
			}
			log.info("[end] get liverage and groupname of subgroup base on IB CUSTOMER ID");
								
			scCustomerService.setSubGroupCd(amsSubGroup.getSubGroupCode());
			scCustomerService.setServiceType(serviceType);
			
//			Map<String,String> mapBroker = (Map<String, String>) SystemCaching.getInstance().getCache(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.BROKER_OWNER);
			scCustomerService.setBrokerCd(wlCode);
			List<ScBroker> listScBrokers = new ArrayList<ScBroker>();
			String serverAddress = IConstants.SERVER_ADDRESS.DEMO;
				
			listScBrokers = getScBrokerDAO().findByServerAddress(serverAddress);
			if(listScBrokers != null && listScBrokers.size() != 0){
				Integer brokerId = listScBrokers.get(0).getBrokerId();
				scCustomerService.setBrokerId(brokerId);
			}
			
			scCustomerService.setAccountId(customerServiceId);
			scCustomerService.setAccountType(accountType);
			scCustomerService.setAccountKind(accountKind);
			// get leverage 
			scCustomerService.setLeverage(BigDecimal.valueOf(leverage));
			scCustomerService.setBaseCurrency(amsCustomer.getSysCurrency().getCurrencyCode());
			scCustomerService.setEnableFlg(IConstants.ENABLE_FLG.ENABLE);
			scCustomerService.setInputDate(new Timestamp(System.currentTimeMillis()));
			scCustomerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			scCustomerService.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);

			scCustomerServiceDAO.save(scCustomerService);    // save into database
			log.info("Register SC CUSTOMER SERVICE");
			log.info("CustomerID:" + amsCustomer.getCustomerId());
			log.info("serviceType " + serviceType);
			log.info("LeverageId" + amsSubGroup.getLeverage());
			log.info("End register SC CUSTOMER SERVICE");	
		return scCustomerService;
	}

	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Sep 21, 2012
	 * @MdDate
	 */
	public Boolean isEmailExisting(String email){
		List<AmsCustomer> listAmsCustomer = null;
//		AmsCustomer amsCustomer = null;
		Boolean flg = false;
		try {
			listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
//			amsCustomer = new AmsCustomer();
			if(listAmsCustomer !=null && listAmsCustomer.size() > 0){
//				amsCustomer = listAmsCustomer.get(0);
				flg = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return flg;
	}
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Sep 20, 2012
	 * @MdDate
	 */
	public Boolean resetPassword(String email){
		Boolean flg = true;
		try {
			List<AmsCustomer> listAmsCustomer = null;
			AmsCustomer amsCustomer = null;
			listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
			amsCustomer = new AmsCustomer();
			if(listAmsCustomer !=null && listAmsCustomer.size() > 0){
				amsCustomer = listAmsCustomer.get(0);
			}
			//generate reset password code
			String resetPasswordCode = "";			
			resetPasswordCode = phn.com.nts.util.common.Utilities.generateRandomPassword(PASSWORD_DEFAULT_LENGTH);
			String md5ResetPasswordCode = Security.MD5(resetPasswordCode);
			String wlCode = "";
			String loginId = "";
			String fullName = "";
			String customerId = "";
			String displayLanguage  = "";
			if(amsCustomer !=null){
				wlCode = amsCustomer.getWlCode();
				loginId = amsCustomer.getLoginId();
				fullName = amsCustomer.getFullName();
				customerId = amsCustomer.getCustomerId();
				displayLanguage = amsCustomer.getDisplayLanguage();				
			}
			if(displayLanguage == null || StringUtils.isBlank(displayLanguage)) {
				displayLanguage = IConstants.Language.ENGLISH;
			}
			
//			MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_FORGOT_PASS + "_" + displayLanguage)  ;
			String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_FORGOT_PASS).append("_").append(displayLanguage).toString();
//			if(mailTemplateInfo != null) {
				AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
				amsMailTemplateInfo.setFullName(fullName);
				amsMailTemplateInfo.setActiveCode(md5ResetPasswordCode);
				amsMailTemplateInfo.setActiveMail(md5ResetPasswordCode);
				amsMailTemplateInfo.setEmailAddress(loginId);
				amsMailTemplateInfo.setWlCode(wlCode);
				HashMap<String, String> to = new HashMap<String, String>();				
				to.put(loginId, loginId);
				amsMailTemplateInfo.setTo(to);
				amsMailTemplateInfo.setMailCode(mailCode);
				amsMailTemplateInfo.setSubject(mailCode);				
//				amsMailTemplateInfo.setTemplateId(mailTemplateInfo.getMailTemplateId());
				
				log.info("mail template info: , subject=" + mailCode);
//				JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
				jmsContextSender.sendMail(amsMailTemplateInfo, false);
				//update ams customer
				log.info("[Start] update mail active code on ams customer with customerID= " + customerId);
				amsCustomer.setResetPasswordCode(md5ResetPasswordCode);
				getiAmsCustomerDao().merge(amsCustomer);
				
				log.info("[end] update mail active code on ams customer with customerID= " + customerId);
//			} else {
//				flg = false;
//				log.info("cannot find mail template for mail code " + IConstants.MAIL_TEMPLATE.AMS_FORGOT_PASS + "send mail error!");
//				
//			}
			
			log.info("[end] send mail forgot password");
			
		} catch (Exception e) {
			flg = false;
			log.error(e.getMessage(), e);
		}
		
		return flg;
	}
	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 17, 2013
	 */
	public Boolean resetPasswordExtend(String email){
		Boolean flg = true;
		try {
			List<AmsCustomer> listAmsCustomer = null;
			AmsCustomer amsCustomer = null;
			listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
			amsCustomer = new AmsCustomer();
			if(listAmsCustomer !=null && listAmsCustomer.size() > 0){
				amsCustomer = listAmsCustomer.get(0);
			}
			String wlCode = "";
			String loginId = "";
			String fullName = "";
			String displayLanguage  = "";
			if(amsCustomer !=null){
				wlCode = amsCustomer.getWlCode();
				loginId = amsCustomer.getLoginId();
				fullName = amsCustomer.getFullName();
				displayLanguage = amsCustomer.getDisplayLanguage();				
			}
			if(displayLanguage == null || StringUtils.isBlank(displayLanguage)) {
				displayLanguage = IConstants.Language.JAPANESE;
			}
			String activeCode = Utilities.generateRandomPassword(PASSWORD_DEFAULT_LENGTH);
			log.debug("start get iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.FORGOT_PASS_UR");
			AmsWhitelabelConfig wlConfig = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.FORGOT_PASS_URL, wlCode);
			String urlReset = wlConfig.getConfigValue();
			log.debug("urlReset="+urlReset);
			String verifylink ="";
			if(amsCustomer.getCorporationType().intValue()==0){
				 verifylink  = urlReset  + "verifyLink?mailactivecode="+activeCode+"&emailaddr="+loginId+"&birthday="+ amsCustomer.getBirthday();
			}else{
				 verifylink  = urlReset  + "verifyLink?mailactivecode="+activeCode+"&emailaddr="+loginId+"&birthday="+ amsCustomer.getCorpEstablishDate();
			}
			log.debug("verifylink="+verifylink);
			amsCustomer.setResetPasswordCode(activeCode);
			amsCustomer.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			iAmsCustomerDao.merge(amsCustomer);
			String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_RESET_PASS_JA;
			TrsMailTemplateInfo trsMail= new TrsMailTemplateInfo();
			trsMail.setFullName(fullName);
			trsMail.setMail(loginId);
			trsMail.setWlCode(wlCode);
			trsMail.setMailCode(mailCode);
			trsMail.setSubject(mailCode);
			trsMail.setLogFileURL(verifylink);
			HashMap<String, String> to = new HashMap<String, String>();	
			to.put(loginId, amsCustomer.getMailMain());
			trsMail.setTo(to);
			HashMap<String, String> from = new HashMap<String, String>();
			
			AmsWhitelabelConfig amsWhitelabelConfig1 = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, amsCustomer.getWlCode());
			String mailFrom = "";
			if(amsWhitelabelConfig1 != null){
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			trsMail.setFrom(from);
			
			HashMap<String, Object> content = new HashMap<String, Object>();
			content.put(ITrsConstants.RESET_PASSWORD_MAIL_CONTENT_FULL_NAME, fullName);
			content.put(ITrsConstants.RESET_PASSWORD_MAIL_CONTENT_FORGOT_PASS_URL, verifylink);
			trsMail.setContent(content);
//			JMSSendClient.getInstance().sendMail(trsMail);
			jmsContextSender.sendMail(trsMail, false);
			log.info("[end] send mail forgot password");
		} catch (Exception e) {
			flg = false;
			log.error(e.getMessage(), e);
		}
		return flg;
	}
	
	/**
	 * 　verify password
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Sep 20, 2012
	 * @MdDate
	 */
	public Boolean verifyPassword(String mailActiveCode, String email){
		Boolean flg = true;
		try {			
			AmsCustomer amsCustomer = null;		
			amsCustomer = getiAmsCustomerDao().findByEmailAndActiveCode(email, mailActiveCode);
			String fullName = "";
			String customerId = "";
			String displayLanguage = "";
			String wlCode  = "";
			if(amsCustomer != null){
				fullName = amsCustomer.getFullName();
				wlCode = amsCustomer.getWlCode();  
				customerId = amsCustomer.getCustomerId();
				displayLanguage = amsCustomer.getDisplayLanguage();
			} else {
				return false;
			}

			if(displayLanguage == null || StringUtils.isBlank(displayLanguage)) {
				displayLanguage = IConstants.Language.ENGLISH;
			}
			String newPassword = "";
			String investorPassword = "";
			newPassword = phn.com.nts.util.common.Utilities.generateRandomPassword(PASSWORD_DEFAULT_LENGTH);
			investorPassword = phn.com.nts.util.common.Utilities.generateRandomPassword(PASSWORD_DEFAULT_LENGTH);

			String md5Password = Security.MD5(newPassword);
//			String md5InvestorPassword = Security.MD5(investorPassword);
			//update ams customer
			log.info("[Start] update new password= " + newPassword + " with md5= "+ md5Password + "on ams customer with customerID= " + customerId);
			amsCustomer.setLoginPass(md5Password);
			getiAmsCustomerDao().merge(amsCustomer);
			log.info("[end] update new password= " + newPassword + " with md5= "+ md5Password +  "on ams customer with customerID= " + customerId);
			log.info("[start] get customer service with customerId = " + customerId);
			String customerServiceId = "";
			List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDao().getListCustomerServices(customerId);
			if(listAmsCustomerService != null && listAmsCustomerService.size() > 0) {
				for(AmsCustomerService amsCustomerService : listAmsCustomerService) {
					if(IConstants.SERVICES_TYPE.FX.equals(amsCustomerService.getServiceType())) {
						customerServiceId = amsCustomerService.getCustomerServiceId();
					}
				}
			}
			if(StringUtil.isEmpty(customerServiceId)) {
				log.warn("Cannot find customer service id with customerId = " + customerId);
				return false;
			}
			log.info("Find CustomerServiceId = " + customerServiceId + " with customerId = " + customerId);
			log.info("[end] get customer service with customerId = " + customerId);
			log.info("[Start] update mt4 account");
			UserRecord userRecord = new UserRecord();
			userRecord.setLogin(MathUtil.parseInt(customerServiceId));
			userRecord.setPassword(newPassword);
			userRecord.setPasswordInvestor(investorPassword);
			
			userRecord.setEnable(UserRecord.NO_UPDATE);
			userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
			userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
			
			Integer result = MT4Manager.getInstance().updateAccountMt4(userRecord);
			if(IConstant.ACCOUNT_UPDATE_SUCCESS != result){
				flg = false;
				log.info("update mt4 fail!");
			}else if(IConstant.ACCOUNT_UPDATE_SUCCESS == result){
				log.info("[start] send mail reset password");
//				MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_RESET_PASS + "_" + displayLanguage);
				String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_RESET_PASS).append("_").append(displayLanguage).toString();
	//			if(mailTemplateInfo != null) {
				AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
				amsMailTemplateInfo.setFullName(fullName);	
				amsMailTemplateInfo.setLoginId(email);
				amsMailTemplateInfo.setLoginPass(newPassword);
				amsMailTemplateInfo.setWlCode(wlCode);
				HashMap<String, String> to = new HashMap<String, String>();				
				to.put(email, email);
				amsMailTemplateInfo.setTo(to);
				amsMailTemplateInfo.setMailCode(mailCode);
				amsMailTemplateInfo.setSubject(mailCode);
//				amsMailTemplateInfo.setTemplateId(mailTemplateInfo.getMailTemplateId());
				
				log.info("mail template info:  subject=" + mailCode + "");
//				JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
				jmsContextSender.sendMail(amsMailTemplateInfo, false);
//				} else {
//					log.info("cannot find mail template for mail code " + IConstants.MAIL_TEMPLATE.AMS_RESET_PASS + "send mail error!");
//				}			
				log.info("[end] send mail reset password");
			}
			log.info("[End] update mt4 account");
		} catch (Exception e) {
			flg = false;
			log.error(e.getMessage(), e);
		}
		return flg;
	}
	/**
	 * @return the iAmsExchangerDAO
	 */
	public IAmsExchangerDAO<AmsExchanger> getiAmsExchangerDAO() {
		return iAmsExchangerDAO;
	}
	/**
	 * @param iAmsExchangerDAO the iAmsExchangerDAO to set
	 */
	public void setiAmsExchangerDAO(IAmsExchangerDAO<AmsExchanger> iAmsExchangerDAO) {
		this.iAmsExchangerDAO = iAmsExchangerDAO;
	}
	/**
	 * @return the iAmsExchangerSymbolDAO
	 */
	public IAmsExchangerSymbolDAO<AmsExchangerSymbol> getiAmsExchangerSymbolDAO() {
		return iAmsExchangerSymbolDAO;
	}
	/**
	 * @param iAmsExchangerSymbolDAO the iAmsExchangerSymbolDAO to set
	 */
	public void setiAmsExchangerSymbolDAO(
			IAmsExchangerSymbolDAO<AmsExchangerSymbol> iAmsExchangerSymbolDAO) {
		this.iAmsExchangerSymbolDAO = iAmsExchangerSymbolDAO;
	}
	/**
	 * @return the iAmsCustomerTraceDAO
	 */
	public IAmsCustomerTraceDAO<AmsCustomerTrace> getiAmsCustomerTraceDAO() {
		return iAmsCustomerTraceDAO;
	}
	/**
	 * @param iAmsCustomerTraceDAO the iAmsCustomerTraceDAO to set
	 */
	public void setiAmsCustomerTraceDAO(
			IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO) {
		this.iAmsCustomerTraceDAO = iAmsCustomerTraceDAO;
	}
	/**
	 * @return the iAmsWhitelabelConfigDAO
	 */
	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getiAmsWhitelabelConfigDAO() {
		return iAmsWhitelabelConfigDAO;
	}
	/**
	 * @param iAmsWhitelabelConfigDAO the iAmsWhitelabelConfigDAO to set
	 */
	public void setiAmsWhitelabelConfigDAO(
			IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO) {
		this.iAmsWhitelabelConfigDAO = iAmsWhitelabelConfigDAO;
	}

	/**
	 * get sub group id of fx account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 14, 2012
	 */
	public Integer getSubGroupId(String customerId, int serviceType) {
		List<CustomerServicesInfo> amsCustomerServices = getListCustomerServiceInfo(customerId);
		Integer subGroupId = null;
		if (!CommonUtil.isEmpty(amsCustomerServices)) {
			for (CustomerServicesInfo s : amsCustomerServices) {				
				if (s.getServiceType().intValue() == serviceType) {
					subGroupId = s.getSubGroupId();
					break;
				}
			}
		}
		return subGroupId;
	}
	/**
	 * get sub group id of fx account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 14, 2012
	 */
	public Integer getSubGroupIdFX(String customerId) {
		List<CustomerServicesInfo> amsCustomerServices = getListCustomerServiceInfo(customerId);
		Integer subGroupId = null;
		if (!CommonUtil.isEmpty(amsCustomerServices)) {
			for (CustomerServicesInfo s : amsCustomerServices) {
				if (s.getServiceType().intValue() == IConstants.SERVICES_TYPE.FX.intValue()) {
					subGroupId = s.getSubGroupId();
					break;
				}
			}
		}
		return subGroupId;
	}
	
	public String generateKey(String prefix, String contextId, int leng) {
		Long number = getNumber(contextId);
		if (leng < 3)
			return String.valueOf(number);

		int subLength = leng - prefix.length();
		StringBuffer fb = new StringBuffer();
		for (int i = 0; i < subLength; i++) {
			fb.append("0");
		}

		NumberFormat formatter = new DecimalFormat(fb.toString());
		String key = formatter.format(number);

		return new StringBuffer(prefix).append(key).toString();
	}

	private Long getNumber(String id) {
		return getUniqueidCounterDAO().generateId(id);
	}
	
	/**
	 * @return the scCustomerServiceDAO
	 */
	public IScCustomerServiceDAO<ScCustomerService> getScCustomerServiceDAO() {
		return scCustomerServiceDAO;
	}
	/**
	 * @param scCustomerServiceDAO the scCustomerServiceDAO to set
	 */
	public void setScCustomerServiceDAO(
			IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO) {
		this.scCustomerServiceDAO = scCustomerServiceDAO;
	}
	/**
	 * @return the scCustomerDAO
	 */
	public IScCustomerDAO<ScCustomer> getScCustomerDAO() {
		return scCustomerDAO;
	}
	/**
	 * @param scCustomerDAO the scCustomerDAO to set
	 */
	public void setScCustomerDAO(IScCustomerDAO<ScCustomer> scCustomerDAO) {
		this.scCustomerDAO = scCustomerDAO;
	}
	public ISysUniqueidCounterDAO<SysUniqueidCounter> getUniqueidCounterDAO() {
		return uniqueidCounterDAO;
	}
	public void setUniqueidCounterDAO(ISysUniqueidCounterDAO<SysUniqueidCounter> uniqueidCounterDAO) {
		this.uniqueidCounterDAO = uniqueidCounterDAO;
	}
	/**
	 * @return the scBrokerDAO
	 */
	public IScBrokerDAO<ScBroker> getScBrokerDAO() {
		return scBrokerDAO;
	}
	/**
	 * @param scBrokerDAO the scBrokerDAO to set
	 */
	public void setScBrokerDAO(IScBrokerDAO<ScBroker> scBrokerDAO) {
		this.scBrokerDAO = scBrokerDAO;
	}
	public IAmsDepositDAO<AmsDeposit> getiAmsDepositDAO() {
		return iAmsDepositDAO;
	}
	public void setiAmsDepositDAO(IAmsDepositDAO<AmsDeposit> iAmsDepositDAO) {
		this.iAmsDepositDAO = iAmsDepositDAO;
	}
	
	@Override
	public String saveBjpDeposit(AmsDeposit amsDeposit, AmsDepositRef amsDepositRef, AmsDepositTransactionInfo amsDepositTransactionInfo) {
		try {
			String key = uniqueidCounterDAO.generateUniqueId(AMS_WHITELABEL_CONFIG_KEY.UNI_DEP_KEY);
			String fdate = iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate();
			amsDeposit.setDepositId(key);
			amsDeposit.setDepositAcceptDate(fdate);
			amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
			amsDeposit.setActiveFlg(1);
			amsDeposit.setInputDate(new Timestamp(System.currentTimeMillis()));
			amsDeposit.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			iAmsDepositDAO.save(amsDeposit);
			
			AmsDeposit rs = iAmsDepositDAO.findById(AmsDeposit.class, key);
			amsDepositRef.setAmsDeposit(rs);
			String bankCode  = amsDepositRef.getCcNo();
			amsDepositRef.setCcNo(null);
			
			//[TRSM1-2389-quyen.le.manh]Jan 28, 2016M - Start - add bank name with Veritrans deposit method
			if(amsDepositTransactionInfo.getDepositMethod() == DepositMethod.VERITRANS_PAYMENT) {
				amsDepositRef.setBeneficiaryBankName(amsDepositTransactionInfo.getBeneficiaryBankName());
				amsDepositRef.setBeneficiaryBankAddress(amsDepositTransactionInfo.getBeneficiaryBankAddress());
				amsDepositRef.setBeneficiarySwiftCode(amsDepositTransactionInfo.getBeneficiarySwiftCode());
				amsDepositRef.setBeneficiaryBankNameKana(amsDepositTransactionInfo.getBeneficiaryBankNameKana());
			} else {
				AmsSysBank bank = iAmsSysBankDAO.getBankByBjpBankCode(bankCode); //findById(AmsSysBank.class, bankCode);
				if(bank!=null){
					amsDepositRef.setBeneficiaryBankName(bank.getBankName());
					amsDepositRef.setBeneficiaryBankAddress(bank.getBankAddress());
					amsDepositRef.setBeneficiarySwiftCode(bank.getSwiftCode());
					amsDepositRef.setBeneficiaryBankNameKana(bank.getBankNameKana());
				} else
					log.warn("Not found bank with bankCode: " + bankCode);
			}
			//[TRSM1-2389-quyen.le.manh]Jan 28, 2016M - End
			
			AmsCustomer cus = rs.getAmsCustomer();
			String cusId = cus.getCustomerId();
			AmsCustomer customer = iAmsCustomerDao.findById(AmsCustomer.class, cusId);
			if(customer!=null&&customer.getCorporationType()==1){
				amsDepositRef.setBeneficiaryAccountNameKana(customer.getCorpFullnameKana());
				amsDepositRef.setBeneficiaryAccountName(customer.getCorpPicFirstname()+customer.getCorpPicLastname());
			}else{
				amsDepositRef.setBeneficiaryAccountNameKana(customer.getFirstNameKana()+" "+customer.getLastNameKana());
				amsDepositRef.setBeneficiaryAccountName(customer.getFullName());
			}
			amsDepositRef.setDepositId(key);
			amsDepositRef.setActiveFlg(1);
			
			if(amsDeposit.getDepositMethod() != null && amsDeposit.getDepositMethod().equals(IConstants.DEPOSIT_METHOD.VERITRANS_PAYMENT)) {
				//VERITRANS_PAYMENT GW_REF_ID =	[DEPOSIT_ID] with replaced "DEP" by "000"
				amsDepositRef.setGwRefId(key.replace("DEP", "000"));
			}
			
			iAmsDepositRefDAO.save(amsDepositRef);		
			return key;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "";
	}
	
	@Override
	public BjpInfo updateBjpInfo(BjpInfo bjpInfo) {
		if(bjpInfo==null){
			bjpInfo= new BjpInfo();
		}
		AmsWhitelabelConfig wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.POST_CODE, TRS_CONSTANT.TRS_WL_CODE);
		if(wlcfg==null){
			bjpInfo.setValid(0);
			log.warn("Can not get AMS_WHITELABEL_CONFIG_KEY.POST_CODE");
			return bjpInfo;
		}else{
			bjpInfo.setPOST_PORTAL_CODE(wlcfg.getConfigValue());
		}
		wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.SHOP_CODE, TRS_CONSTANT.TRS_WL_CODE);
		if(wlcfg==null){
			bjpInfo.setValid(0);
			log.warn("Can not get AMS_WHITELABEL_CONFIG_KEY.SHOP_CODE");
			return bjpInfo;
		}else{
			bjpInfo.setPOST_SHOP_CODE(wlcfg.getConfigValue());
		}
		bjpInfo.setPOST_KESSAI_FLAG(AMS_WHITELABEL_CONFIG_KEY.KESSAI_FLAG);
//		wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.CTRL_NO, TRS_CONSTANT.TRS_WL_CODE);
//		if(wlcfg==null){
//			bjpInfo.setValid(0);
//			log.error("Can not get AMS_WHITELABEL_CONFIG_KEY.CTRL_NO");
//			return bjpInfo;
//		}else{
//			bjpInfo.setPOST_CTRL_NO(wlcfg.getConfigValue());
//		}
		wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.VALID_RETURN_URL, TRS_CONSTANT.TRS_WL_CODE);
		if(wlcfg==null){
			bjpInfo.setValid(0);
			log.warn("Can not get AMS_WHITELABEL_CONFIG_KEY.VALID_RETURN_URL");
			return bjpInfo;
		}else{
			bjpInfo.setPOST_VALID_RETURN_URL(wlcfg.getConfigValue());
		}
		
		wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.INVALID_RETURN_URL, TRS_CONSTANT.TRS_WL_CODE);
		if(wlcfg==null){
			bjpInfo.setValid(0);
			log.warn("Can not get AMS_WHITELABEL_CONFIG_KEY.INVALID_RETURN_URL");
			return bjpInfo;
		}else{
			bjpInfo.setPOST_INVALID_RETURN_URL(wlcfg.getConfigValue());
		}
		wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.INVALID_RETURN_URL, TRS_CONSTANT.TRS_WL_CODE);
		if(wlcfg==null){
			bjpInfo.setValid(0);
			log.warn("Can not get AMS_WHITELABEL_CONFIG_KEY.INVALID_RETURN_URL");
			return bjpInfo;
		}else{
			bjpInfo.setPOST_INVALID_RETURN_URL(wlcfg.getConfigValue());
		}
		String cusId = FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId();
		CustomerInfo cusInfo = getCustomerInfo(cusId);
		if(cusInfo.getCorporationType()==1){
//			bjpInfo.setPOST_CUST_NAME_1("");
//			bjpInfo.setPOST_CUST_NAME_2(cusInfo.getCorpFullname());
		
			bjpInfo.setPOST_CUST_NAME(cusInfo.getCorpFullnameKana());
			bjpInfo.setPOST_CUST_LNAME("");
			bjpInfo.setPOST_CUST_FNAME(cusInfo.getCorpFullnameKana());
			try {
				bjpInfo.setPOST_CUST_POSTCODE_1(cusInfo.getZipcode().substring(0, 3));
				bjpInfo.setPOST_CUST_POSTCODE_2(cusInfo.getZipcode().substring(3, 7));
			} catch (Exception e) {
				bjpInfo.setPOST_CUST_POSTCODE_1(cusInfo.getZipcode());
				bjpInfo.setPOST_CUST_POSTCODE_2(cusInfo.getZipcode());
				log.error(e.getMessage());
			}
			bjpInfo.setPOST_CUST_ADDRESS_1(cusInfo.getCorpPicPrefecture());
			bjpInfo.setPOST_CUST_ADDRESS_2(cusInfo.getCorpPicCity()+cusInfo.getCorpPicSection());
//			bjpInfo.setPOST_CUST_ADDRESS_3(cusInfo.getCorpPicBuildingName()+" "+cusInfo.getHouseNumber());
			bjpInfo.setPOST_CUS_TEL(cusInfo.getCorpPicTel());
			bjpInfo.setPOST_EMAIL_ADDRESS(cusInfo.getCorpPicMailPc());
			String fdate = iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate();
			bjpInfo.setPOST_CONC_DAY(fdate);
			bjpInfo.setPOST_DUE_DAY(fdate);
		}else{
//			bjpInfo.setPOST_CUST_NAME_1(cusInfo.getFirstName());
//			bjpInfo.setPOST_CUST_NAME_2(cusInfo.getLastName());
			bjpInfo.setPOST_CUST_NAME(cusInfo.getFirstNameKana()+" "+cusInfo.getLastNameKana());
			bjpInfo.setPOST_CUST_LNAME(cusInfo.getFirstNameKana());
			bjpInfo.setPOST_CUST_FNAME(cusInfo.getLastNameKana());
			try {
				bjpInfo.setPOST_CUST_POSTCODE_1(cusInfo.getZipcode().substring(0, 3));
				bjpInfo.setPOST_CUST_POSTCODE_2(cusInfo.getZipcode().substring(3, 7));
			} catch (Exception e) {
				bjpInfo.setPOST_CUST_POSTCODE_1(cusInfo.getZipcode());
				bjpInfo.setPOST_CUST_POSTCODE_2(cusInfo.getZipcode());
				log.error(e.getMessage());
			}
			bjpInfo.setPOST_CUST_ADDRESS_1(cusInfo.getPrefecture());
			bjpInfo.setPOST_CUST_ADDRESS_2(cusInfo.getCity()+cusInfo.getSection());
//			bjpInfo.setPOST_CUST_ADDRESS_3(cusInfo.getBuildingName()+" "+cusInfo.getHouseNumber());
			bjpInfo.setPOST_CUS_TEL(cusInfo.getTel1());
			bjpInfo.setPOST_EMAIL_ADDRESS(cusInfo.getMailMain());
			String fdate = iSysAppDateDAO.getCurrentBusinessDay().getId().getFrontDate();
			bjpInfo.setPOST_CONC_DAY(fdate);
			bjpInfo.setPOST_DUE_DAY(fdate);
		}
		return bjpInfo;
	}
	/**
	 * @return the iAmsDepositRefDAO
	 */
	public IAmsDepositRefDAO<AmsDepositRef> getiAmsDepositRefDAO() {
		return iAmsDepositRefDAO;
	}
	/**
	 * @param iAmsDepositRefDAO the iAmsDepositRefDAO to set
	 */
	public void setiAmsDepositRefDAO(
			IAmsDepositRefDAO<AmsDepositRef> iAmsDepositRefDAO) {
		this.iAmsDepositRefDAO = iAmsDepositRefDAO;
	}
	/**
	 * @return the iAmsSysBankDAO
	 */
	public IAmsSysBankDAO<AmsSysBank> getiAmsSysBankDAO() {
		return iAmsSysBankDAO;
	}
	/**
	 * @param iAmsSysBankDAO the iAmsSysBankDAO to set
	 */
	public void setiAmsSysBankDAO(IAmsSysBankDAO<AmsSysBank> iAmsSysBankDAO) {
		this.iAmsSysBankDAO = iAmsSysBankDAO;
	}
	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IAccountManager#checkUserExisting(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean checkUserExisting(String email, String birthday, Integer corpType) {
		List<AmsCustomer> listAmsCustomer = null;
		try {
			listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
			if(listAmsCustomer !=null && listAmsCustomer.size() > 0){
				AmsCustomer cust = listAmsCustomer.get(0);
				List<AmsCustomerService> listCustomerService = iAmsCustomerServiceDao.findByCustomerId(cust.getCustomerId());
//				if(listCustomerService==null||listCustomerService.size()==0||listCustomerService.size()==1){
//					return false;
//				}
                boolean isValid = false;
				for(int i=0;i<listCustomerService.size();i++){
					Integer serviceStatus = listCustomerService.get(i).getCustomerServiceStatus();
					if(serviceStatus!=null &&(serviceStatus.intValue()==IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED||serviceStatus.intValue()==IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED||serviceStatus.intValue()==IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED)){
                        isValid = true;
                        break;
					}
				}
                if(!isValid) return false; //If there is no account with status in (8,9,10) return false
				String birth="";
				if(cust.getCorporationType().intValue()!=corpType.intValue()){
					return false;
				}
				if(1==corpType.intValue()){
					birth=cust.getCorpEstablishDate();
					Date date = DateUtil.toDate(birth,"yyyyMM");
					birth = DateUtil.toString(date,"yyyy/MM");
					if(birth.equalsIgnoreCase(birthday)){
						return true;
					}else{
						return false;
					}
				}else{
					birth=cust.getBirthday();
					Date date = DateUtil.toDate(birth,DateUtil.PATTERN_YYMMDD_BLANK);
					birth = DateUtil.toString(date,DateUtil.PATTERN_YYMMDD);
					if(birth.equalsIgnoreCase(birthday)){
						return true;
					}else{
						return false;
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 17, 2013
	 */
	@Override
	public boolean checkVerifyUser(String email, String birthday,String mailActiveCode) {
		List<AmsCustomer> listAmsCustomer = null;
		try {
			listAmsCustomer = getiAmsCustomerDao().findByLoginId(email);
			if(listAmsCustomer !=null && listAmsCustomer.size() > 0){
				AmsCustomer cust = listAmsCustomer.get(0);
				if(cust.getResetPasswordCode()==null){
					return false;
				}
				if(cust.getResetPasswordCode().length()!=PASSWORD_DEFAULT_LENGTH){
					return false;
				}
				if(!mailActiveCode.equalsIgnoreCase(cust.getResetPasswordCode())){
					return false;
				}
				String birth="";
				Integer corpType = cust.getCorporationType();
				if(1==corpType.intValue()){
					birth=cust.getCorpEstablishDate();
					if(birth.equalsIgnoreCase(birthday)){
						return true;
					}else{
						return false;
					}
				}else{
					birth=cust.getBirthday();
					if(birth.equalsIgnoreCase(birthday)){
						return true;
					}else{
						return false;
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IAccountManager#updatePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updatePassword(String email, String newPassword,String mailActiveCode) {
		Boolean flg = true;
		try {			
			AmsCustomer amsCustomer = null;		
			amsCustomer = getiAmsCustomerDao().findByEmailAndActiveCode(email, mailActiveCode);
			String customerId = "";
			String displayLanguage = "";
			if(amsCustomer != null){
				customerId = amsCustomer.getCustomerId();
				displayLanguage = amsCustomer.getDisplayLanguage();
			} else {
				return false;
			}

			if(displayLanguage == null || StringUtils.isBlank(displayLanguage)) {
				displayLanguage = IConstants.Language.ENGLISH;
			}
			String md5Password = Security.MD5(newPassword);
//			String md5InvestorPassword = Security.MD5(investorPassword);
			//update ams customer
			log.info("[Start] update new password= " + newPassword + " with md5= "+ md5Password + "on ams customer with customerID= " + customerId);
			amsCustomer.setLoginPass(md5Password);
			amsCustomer.setResetPasswordCode(null);
			iAmsCustomerDao.merge(amsCustomer);
			log.info("[end] update new password= " + newPassword + " with md5= "+ md5Password +  "on ams customer with customerID= " + customerId);
			log.info("[start] get customer service with customerId = " + customerId);
			String customerServiceId = "";
			List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDao().getListCustomerServices(customerId);
			if(listAmsCustomerService != null && listAmsCustomerService.size() > 0) {
				for(AmsCustomerService amsCustomerService : listAmsCustomerService) {
					if(IConstants.SERVICES_TYPE.FX.equals(amsCustomerService.getServiceType())||amsCustomerService.getServiceType().intValue()==ITrsConstants.SOCIAL_SERVICE_TYPE){
						customerServiceId = amsCustomerService.getCustomerServiceId();
						if(StringUtil.isEmpty(customerServiceId)) {
							log.warn("Cannot find customer service id with customerId = " + customerId);
							return false;
						}
						log.info("Find CustomerServiceId = " + customerServiceId + " with customerId = " + customerId);
						UserRecord userRecord = new UserRecord();
						userRecord.setLogin(MathUtil.parseInt(customerServiceId));
						userRecord.setPassword(newPassword);
						userRecord.setPasswordInvestor(newPassword);
						userRecord.setEnable(UserRecord.NO_UPDATE);
						userRecord.setEnableReadOnly(UserRecord.NO_UPDATE);
						userRecord.setEnableChangePassword(UserRecord.NO_UPDATE);
						log.info("[Start] update mt4 account" + customerServiceId);
						Integer result = MT4Manager.getInstance().updateAccountMt4(userRecord);
						log.info("[End] update mt4 account"+customerServiceId);
						if(IConstant.ACCOUNT_UPDATE_SUCCESS != result){
							flg = false;
							log.info("update mt4 fail!" +customerServiceId);
							return flg;
						}
				}
				}
			}
			log.info("[end] get customer service with customerId = " + customerId);
			log.info("[start] send mail reset password");
			sendmailChangePass(amsCustomer, IConstants.Language.JAPANESE, customerServiceId, newPassword);
			log.info("[end] send mail reset password");
		} catch (Exception e) {
			flg = false;
			log.error(e.getMessage(), e);
		}
		return flg;
	}
	private void sendmailChangePass(AmsCustomer amsCustomer, String language,
			String customerServiceId, String newPassword) {
		log.info("[start] send mail to customer about change password successful");
		// MailTemplateInfo mailTemplateInfo = (MailTemplateInfo)
		// FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE
		// + IConstants.MAIL_TEMPLATE.AMS_PASS_CHANGED + "_" + language);
		String mailCode = new StringBuffer(
				IConstants.MAIL_TEMPLATE.AMS_PASS_CHANGED).append("_")
				.append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setCustomerServiceId(customerServiceId);
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		amsMailTemplateInfo.setLoginPass(newPassword);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		// amsMailTemplateInfo.setTemplateId(mailTemplateInfo.getMailTemplateId());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail to customer about change password successful");
	}
	/* (non-Javadoc)
	 * @see phn.nts.ams.fe.business.IAccountManager#getBjpCertificationKey(java.lang.String)
	 */
	@Override
	public String getBjpCertificationKey(String wlcode) {
		AmsWhitelabelConfig wlcfg = iAmsWhitelabelConfigDAO.getAmsWhiteLabelConfig(AMS_WHITELABEL_CONFIG_KEY.BJP_CERTIFICATION_KEY, wlcode);
		if(wlcfg==null){
			log.warn("Can not get AMS_WHITELABEL_CONFIG.BJP_CERTIFICATION_KEY");
			return "";
		}else{
			return wlcfg.getConfigValue();
		}
	}
	/**
	 * @return the jmsContextSender
	 */
	public IJmsContextSender getJmsContextSender() {
		return jmsContextSender;
	}
	/**
	 * @param jmsContextSender the jmsContextSender to set
	 */
	public void setJmsContextSender(IJmsContextSender jmsContextSender) {
		this.jmsContextSender = jmsContextSender;
	}
	@Override
	public List<Integer> getListServiceTypeStatusCancel(String customerId) {
		List<Integer> listServiceType = new ArrayList<Integer>();
		List<AmsCustomerService> listCustomerService = getiAmsCustomerServiceDao().getListCustomerServices(customerId);
		for(AmsCustomerService customerService:listCustomerService){
			if(customerService.getCustomerServiceStatus() < IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED 
					|| customerService.getCustomerServiceStatus() > IConstants.CUSTOMER_SERVIVES_STATUS.CANCELCONTRACT){
				listServiceType.add(customerService.getServiceType());
			}
		}
		return listServiceType;
	}
	
	/**
	 * Sync CustomerInfo to Bo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 25, 2015
	 * @MdDate
	 */
	public boolean syncCustomerInfoToBo(String customerId) {
		boolean flag = false;
		log.info("[start] sync CustomerInfo to BoCustomer");
		
		AmsCustomerService amsCustomerService = iAmsCustomerServiceDao.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.BO);
		if(amsCustomerService != null) {
			
			//[TRSPT-7251-quyen.le.manh]Dec 29, 2015M - Start - keep BoTestStatus like AmsCustomerSurvey.BoTestStatus
			int boTestStatus = ITrsConstants.BO_TEST_STATUS.DEFAULT;
			
			if(amsCustomerService.getAmsCustomer() != null && amsCustomerService.getAmsCustomer().getAmsCustomerSurvey() != null
					&& amsCustomerService.getAmsCustomer().getAmsCustomerSurvey().getBoTestStatus() != null)
				boTestStatus = amsCustomerService.getAmsCustomer().getAmsCustomerSurvey().getBoTestStatus();
						
			flag = updateBoCustomer(amsCustomerService, boTestStatus);
			//[TRSPT-7251-quyen.le.manh]Dec 29, 2015M - End
		} else
			log.warn("Can not get ams customer service with service type = BO for customerid: " + customerId);
		
		log.info("[start] sync CustomerInfo to BoCustomer");
		return flag;
	}
	
	public boolean updateCustomer(String customerId,BoRegisInfo info){
		boolean flag = false;
		AmsCustomer amsCustomer = iAmsCustomerDao.findById(AmsCustomer.class, customerId);
		if(amsCustomer!=null){
			AmsCustomerSurvey amsSurvey = amsCustomer.getAmsCustomerSurvey();
			if(amsSurvey!=null){
				amsSurvey.setBoPurposeShortTermFlg(IConstants.ACTIVE_FLG.INACTIVE);
				amsSurvey.setBoPurposeDispAssetMngFlg(IConstants.ACTIVE_FLG.INACTIVE);
				amsSurvey.setBoPurposeHedgeFlg(IConstants.ACTIVE_FLG.INACTIVE);
				for(int i = 0;i<info.getListPurposeBo().size();i++){
					if(ITrsConstants.REGISTER_CUSTOMER.PURPOSE_BO.SHORT_TERM_RETURN.equals(info.getListPurposeBo().get(i))){
						amsSurvey.setBoPurposeShortTermFlg(IConstants.ACTIVE_FLG.ACTIVE);
					}
					if(ITrsConstants.REGISTER_CUSTOMER.PURPOSE_BO.DISPERSION_ASSET_MANAGEMENT.equals(info.getListPurposeBo().get(i))){
						amsSurvey.setBoPurposeDispAssetMngFlg(IConstants.ACTIVE_FLG.ACTIVE);
					}
					if(ITrsConstants.REGISTER_CUSTOMER.PURPOSE_BO.HEDGE.equals(info.getListPurposeBo().get(i))){
						amsSurvey.setBoPurposeHedgeFlg(IConstants.ACTIVE_FLG.ACTIVE);
					}
				}
				amsSurvey.setBoPurposeHedgeType(info.getPurposeBoHedgeType());
				amsSurvey.setBoPurposeHedgeAmount(info.getPurposeBoHedgeAmount());
				amsSurvey.setBoMaxLossAmount(MathUtil.parseBigDecimal(info.getMaxLossAmountBo()));
				amsSurvey.setBoTestStatus(ITrsConstants.BO_TEST_STATUS.TEST_WAITING);
				amsSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerSurveyDAO.merge(amsSurvey);
			} else {
				log.warn("can not get ams customer survey for customerid:"+customerId);
				return flag;
			}
			AmsCustomerService amsCustomerService = iAmsCustomerServiceDao.findByCustomerIdServiceType(customerId, IConstants.SERVICES_TYPE.BO);
			if(amsCustomerService!=null){
				amsCustomerService.setCustomerServiceStatus(ITrsConstants.ACCOUNT_OPEN_STATUS.WAITING_ADD_ACCOUNT);
			} else {
				log.warn("can not get ams customer service with service type = BO for customerid:"+customerId);
				return flag;
			}
			flag = updateBoCustomer(amsCustomerService, ITrsConstants.BO_TEST_STATUS.TEST_WAITING);
			if(flag){
				CRMIntegrationAPI.syncBoTestStatusSF(amsCustomerService, ITrsConstants.BO_TEST_STATUS.TEST_WAITING);
			}
		} else {
			return false;
		}
		return flag;
	}
	
	private boolean updateBoCustomer(AmsCustomerService service, Integer boTestStatus) {
		log.info("[start]CustomerServiceManagerImpl.updateBoCustomer()");
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
		
		//[TRSPT-7692-quyen.le.manh]Feb 3, 2016M - Start only update AccountOpenFinishDate when OPEN_COMPLETED
		if(IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(service.getCustomerServiceStatus()) 
				&& StringUtil.isEmpty(service.getAccountOpenFinishDate()))
			jmsUpdateAccount.setAccountOpenFinishDate(service.getAccountStatusChangeDate());
		else
			jmsUpdateAccount.setAccountOpenFinishDate(service.getAccountOpenFinishDate());
		//[TRSPT-7692-quyen.le.manh]Feb 3, 2016M - End

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
		log.info("[end]CustomerServiceManagerImpl.updateBoCustomer()");
		return true;
	}

	public AmsCustomer getAmsCustomer(String customerId) {
		AmsCustomer amsCustomer = null;
		List<AmsCustomer> listAmsCustomer = getiAmsCustomerDao().findByCustomerId(customerId);
		if(listAmsCustomer != null && listAmsCustomer.size() > 0) {			
			amsCustomer = listAmsCustomer.get(0);
		}
		return amsCustomer;
	}
	
	public AmsCustomer getAmsCustomerByCustomerService(String customerServiceId) {
		List<AmsCustomerService> listService = getiAmsCustomerServiceDao().findByCustomerServiceId(customerServiceId);
		
		if(listService != null && listService.size() > 0) {
			return getAmsCustomer(listService.get(0).getAmsCustomer().getCustomerId());
		}
		
		return null;
	}
	
	public String getCustomerIdByCustomerService(String customerServiceId) {
		List<AmsCustomerService> listService = getiAmsCustomerServiceDao().findByCustomerServiceId(customerServiceId);
		
		if(listService != null && listService.size() > 0) {
			return listService.get(0).getAmsCustomer().getCustomerId();
		}
		
		return null;
	}

	@Override
	public boolean checkEaAccount(String customerId) {
		log.info("[Start] check customer is Ea for customerId [" + customerId + "]");

		AmsCustomer amsCustomer = getAmsCustomer(customerId);
		if (amsCustomer == null) {
			log.error("Cannot found ams customer object with customerID [" + customerId + "]");
			return false;
		}

		AmsGroup amsGroup = amsCustomer.getAmsGroup();
		if (amsGroup == null) {
			log.error("Cannot found ams group object with customerID [" + customerId + "]");
			return false;
		}

		boolean result = Helper.isEaGroupName(amsGroup.getGroupName());
		log.info("[End] check customer is Ea for customerId [" + customerId + "]" + " --> result true");
		return result;
	}
}