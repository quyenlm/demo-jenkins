package phn.nts.ams.fe.business.impl;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsDemoContestDAO;
import phn.com.nts.db.dao.IAmsDmcCustomerContestDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDmc;
import phn.com.nts.db.entity.AmsDmcCustomerContest;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IDemoContestManager;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.DemoContestInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.converter.DemoContestAccountInfoConverter;
import phn.nts.ams.fe.domain.converter.DemoContestInfoConverter;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.nts.components.mail.bean.AmsMailTemplateInfo;

/**
 * @description DemoContestManagerImpl
 * @version NTS1.0
 * @author Quan.Le.Minh
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestManagerImpl implements IDemoContestManager{
	private static final Logit log = Logit.getInstance(DemoContestManagerImpl.class);
	private IAmsDemoContestDAO<AmsDmc> amsDemoContestDAO;
	private IAmsCustomerDAO<AmsCustomer> amsCustomerDAO;
	private IAmsDmcCustomerContestDAO<AmsDmcCustomerContest> amsDmcCustomerContestDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> uniqueidCounterDAO;
	private IJmsContextSender jmsContextSender;
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
		return uniqueidCounterDAO.generateId(id);
	}

	/**
	 * Get detail information of Contest　
	 * 
	 * @param Id of contest
	 * @return Information of contest
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	@Override
	public AmsDmc getDemoContestDetail(Integer contestId) {
		log.info("[start]DemoContestManagerImpl.getDemoContestDetail()");
		AmsDmc demoContest = amsDemoContestDAO.findById(AmsDmc.class, contestId);
		if (demoContest!= null) {
			log.info("[end]DemoContestManagerImpl.getDemoContestDetail()");
			return demoContest;
		}
		return null;
	}
	
	
	public SearchResult<DemoContestInfo> getContestList(String language, String wlCode, PagingInfo paging) {
		SearchResult<AmsDmc> resultDAO = amsDemoContestDAO.getContestList(language, wlCode, paging);
		if (resultDAO == null) {
			return null;
		}
		SearchResult<DemoContestInfo> result = new SearchResult<DemoContestInfo>();
		for (AmsDmc entity : resultDAO) {
			result.add(DemoContestInfoConverter.toInfo(entity));
		}
		result.setPagingInfo(resultDAO.getPagingInfo());
		return result;
	}
	
	/**
	 * Determine if joined contest or not　
	 * 
	 * @param
	 * @return Not null if joined
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	@Override
	public DemoContestAccountInfo joinedContest(String customerId, String contestCd, String currencyCode) {
		DemoContestAccountInfo resutl = null;
		List<AmsDmc> amsDmcs = amsDemoContestDAO.findByProperty("contestCd", contestCd);
		List<Integer> contestCds = new ArrayList<Integer>();
		for (AmsDmc amsDmc : amsDmcs) {
			contestCds.add(amsDmc.getContestId());
		}
		AmsDmcCustomerContest demoContest = amsDemoContestDAO.getCustomerByContestId(customerId, contestCds);
		if(demoContest != null){
			resutl = DemoContestAccountInfoConverter.toInfo(demoContest, currencyCode);
		}
		return resutl;
	}

	/**
	 * Get information for generating demo account　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	@Override
	public DemoContestAccountInfo getInfoForGenerateAccount(Integer contestId) {
		AmsDmc amsDmc = amsDemoContestDAO.getInfoForGenerateAccount(contestId);
		if(amsDmc != null){
			return DemoContestInfoConverter.toContestAccountInfo(amsDmc);
		}
		return null;
	}

	/**
	 * Check existing of nickname　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	@Override
	public boolean checkExistNickname(String nickname, Integer contestId) {
		boolean result = false;
		if (!StringUtil.isEmpty(nickname) && contestId != null) {
			if(amsDemoContestDAO.getCustomerByNickname(nickname, contestId) == null){
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Create and save Demo contest accont into Database　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	@Override
	public Integer saveDmcAccount(DemoContestAccountInfo dmcAccountInfo, Integer contestId, String contestCd) {
		log.info("[start]DemoContestManagerImpl.saveDmcAccount()");
		Integer result = new Integer(0);
		String password = MathUtil.generateRandomPassword(8);
		String loginId = generateKey("8", IConstants.UNIQUE_CONTEXT.CUSTOMER_CONTEXT_DEMO_FX, 8);
		
		/*Check registering or not*/
		if(joinedContest(dmcAccountInfo.getCustomerId(), contestCd, dmcAccountInfo.getCurrencyCode()) == null){
			dmcAccountInfo.setLoginId(loginId);
			dmcAccountInfo.setPassword(password);
			/*Register Demo FX account information to MT4 (demo)*/
			result = registerMT4DemoAccount(dmcAccountInfo);
		}else{
			result = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
		}
		log.info("[end]DemoContestManagerImpl.saveDmcAccount()");
		return result;
	}
	
	/**
	 * Register MT4 Demo Account　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 8, 2013
	 */
	private Integer registerMT4DemoAccount(DemoContestAccountInfo dmcAccountInfo) {
		String passwordInvester = "";
		CustomerInfo customerInfo = new CustomerInfo();
		try{
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			String customerId = null;
			String language = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					customerInfo.setCountryName(frontUserOnline.getCountryName());
					customerInfo.setAccountClass(frontUserOnline.getDeviceType());
					customerId = frontUserOnline.getUserId();
					language = frontUserOnline.getLanguage();
				} else {
					return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
				}
			}
			
			if (customerId == null) {
				log.warn("Not exist customer with customer id = " + customerId);
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL; 
			}

			if (language == null) {
				language = IConstants.Language.ENGLISH;
			}
			String wlCode = frontUserOnline.getWlCode();
			customerInfo.setWlCode(wlCode);
			customerInfo.setFullName(dmcAccountInfo.getNickname());
			customerInfo.setMailMain(getMailByCustomerId(customerId));
			dmcAccountInfo.setCustomerId(customerId);

			String balance = dmcAccountInfo.getDeposit();
			Integer registerAccountResult = MT4Manager.getInstance().registerMT4DemoAccount(customerInfo, dmcAccountInfo.getLoginId(), wlCode, dmcAccountInfo.getSubGroupName(), dmcAccountInfo.getLeverage(), passwordInvester, dmcAccountInfo.getPassword(), balance);
			
			registerAccountResult = IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
			if(!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
			}
			
			AmsDmcCustomerContest entity = DemoContestInfoConverter.toDmcCustomerContestEntity(dmcAccountInfo);
			amsDmcCustomerContestDAO.save(entity);
			sendmailOpenContestAccount(customerInfo, language, dmcAccountInfo.getLoginId(), dmcAccountInfo.getPassword());
		} catch(Exception ex) {			
			log.error(ex.getMessage(),ex);
			return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
		}
		
		return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
	}

	@Override
	public String getMailByCustomerId(String customerId) {
		AmsCustomer asmCustomer = amsCustomerDAO.findById(AmsCustomer.class, customerId);
		
		return asmCustomer.getMailMain();
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 7, 2013
	 */
	private void sendmailOpenContestAccount(CustomerInfo amsCustomer, String language, String loginId, String loginPassword) {
		log.info("[start] send mail about open contest account successful");
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_ACCOUNT_DEMOCONTEST).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setLoginId(loginId);
		amsMailTemplateInfo.setLoginPass(loginPassword);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);				
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about open contest account successful");
	}
	
	/**
	 * findDmcAccountByCondition　
	 * 
	 * @param Integer contestId, String participant, String currencyCode, PagingInfo pagingInfo
	 * @return SearchResult<DemoContestAccountInfo>
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 7, 2013
	 */
	public SearchResult<DemoContestAccountInfo> findDmcAccountByCondition(Integer contestId, String contestCd, String participant, String currencyCode, PagingInfo pagingInfo) {
		SearchResult<DemoContestAccountInfo> result = new SearchResult<DemoContestAccountInfo>();
		
		List<AmsDmc> dmcList = amsDemoContestDAO.findByContestCd(contestCd);
		List<Integer> dmcIdList = new ArrayList<Integer>();
		for (AmsDmc dmc : dmcList) {
			dmcIdList.add(dmc.getContestId());
		}
		SearchResult<AmsDmcCustomerContest> resultDAO = amsDmcCustomerContestDAO.findByCondition(contestId, dmcIdList, participant, pagingInfo);
		for (AmsDmcCustomerContest entity : resultDAO) {
			result.add(DemoContestAccountInfoConverter.toInfo(entity, currencyCode));
		}
		
		result.setPagingInfo(resultDAO.getPagingInfo());
		
		return result;
	}

	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 10, 2013
	 */
	@Override
	public boolean canViewContest(Integer contestId, String wlCode, String language) {
		boolean result = false;
		if(amsDemoContestDAO.getContestByCondition(contestId, wlCode, language) == null){
			result = false;
		} else {
			result = true;
		}
		return result;
	}
	
	/********************************** GETTER AND SETTER ***********************************/
	
	public IAmsDemoContestDAO<AmsDmc> getAmsDemoContestDAO() {
		return amsDemoContestDAO;
	}

	public void setAmsDemoContestDAO(IAmsDemoContestDAO<AmsDmc> amsDemoContestDAO) {
		this.amsDemoContestDAO = amsDemoContestDAO;
	}

	public IAmsCustomerDAO<AmsCustomer> getAmsCustomerDAO() {
		return amsCustomerDAO;
	}

	public void setAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> amsCustomerDAO) {
		this.amsCustomerDAO = amsCustomerDAO;
	}

	public ISysUniqueidCounterDAO<SysUniqueidCounter> getUniqueidCounterDAO() {
		return uniqueidCounterDAO;
	}

	public void setUniqueidCounterDAO(ISysUniqueidCounterDAO<SysUniqueidCounter> uniqueidCounterDAO) {
		this.uniqueidCounterDAO = uniqueidCounterDAO;
	}

	public IAmsDmcCustomerContestDAO<AmsDmcCustomerContest> getAmsDmcCustomerContestDAO() {
		return amsDmcCustomerContestDAO;
	}

	public void setAmsDmcCustomerContestDAO(
			IAmsDmcCustomerContestDAO<AmsDmcCustomerContest> amsDmcCustomerContestDAO) {
		this.amsDmcCustomerContestDAO = amsDmcCustomerContestDAO;
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
	
}
