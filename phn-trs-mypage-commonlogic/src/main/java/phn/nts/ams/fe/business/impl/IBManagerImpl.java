package phn.nts.ams.fe.business.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.*;
import phn.com.nts.db.entity.*;
import phn.com.nts.util.common.*;
import phn.com.nts.util.file.FileLoaderUtil;
import phn.com.nts.util.file.FileUploadInfo;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Security;
import phn.com.nts.util.webcore.SystemProperty;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.common.messages.AccountCreateMessage;
import phn.nts.ams.fe.domain.*;
import phn.nts.ams.fe.model.IBModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.util.AppConfiguration;
import phn.nts.ams.fe.util.MailInfo;
import phn.nts.ams.fe.util.MailService;

import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.phn.mt.common.constant.IResultConstant;
import com.phn.mt.common.entity.UserRecord;

public class IBManagerImpl implements IIBManager {
	final Integer PASSWORD_DEFAULT_LENGTH = 8;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> iAmsWhitelabelConfigDAO;
	private IAmsIbKickbackDAO<AmsIbKickback> iAmsIbKickbackDAO;
	private IAmsIbDAO<AmsIb> iAmsIbDAO;
	private IAmsIbClientDAO<AmsIbClient> iAmsIbClientDAO;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO;
	private IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO;
	private static Logit log = Logit.getInstance(IBManagerImpl.class);
	private IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO;
	private IAmsWhitelabelDAO<AmsWhitelabel> iAmsWhitelabelDAO; 
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private ISysCurrencyDAO<SysCurrency> iSysCurrencyDAO;
	private IAmsGroupDAO<AmsGroup> iAmsGroupDAO;
	private IAmsSysVirtualBankDAO<AmsSysVirtualBank> iAmsSysVirtualBankDAO;
	private MailService mailService;
	//private IAmsSysSymbolDAO<AmsSysSymbol> iAmsSysSymbolDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerService;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IFxSymbolDAO<FxSymbol> iFxSymbolDAO;
    private IScCustomerDAO<ScCustomer> scCustomerDAO;
	private IScBrokerDAO<ScBroker> scBrokerDAO;
    private IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO;
    private IAmsSysCountryDAO<AmsSysCountry> amsSysCountryDAO;
    private IJmsContextSender jmsContextSender;
	private static final String CONFIGPATH = "configs.properties";
	private ITransferManager transferManager;
	private IBModel model = new IBModel();	
	
	private InputStream inputStream;
	private static final String URL_AVATAR_FOLDER = "url.avatar.folder";
	private static Properties propsConfig;
	private static final int IMG_WIDTH = 160;
	private static final int IMG_HEIGHT = 160;

	static {
        try {
            propsConfig = Helpers.getProperties(CONFIGPATH);                      
        } catch(Exception e) {
            log.warn("Could not load configuration file from: " + CONFIGPATH, e);
        }
    }
	/**
	 * @return the iAmsIbClientDAO
	 */
	public IAmsIbClientDAO<AmsIbClient> getiAmsIbClientDAO() {
		return iAmsIbClientDAO;
	}

	/**
	 * @param iAmsIbClientDAO the iAmsIbClientDAO to set
	 */
	public void setiAmsIbClientDAO(IAmsIbClientDAO<AmsIbClient> iAmsIbClientDAO) {
		this.iAmsIbClientDAO = iAmsIbClientDAO;
	}

	/**
	 * @return the iAmsIbDAO
	 */
	public IAmsIbDAO<AmsIb> getiAmsIbDAO() {
		return iAmsIbDAO;
	}

	/**
	 * @param iAmsIbDAO the iAmsIbDAO to set
	 */
	public void setiAmsIbDAO(IAmsIbDAO<AmsIb> iAmsIbDAO) {
		this.iAmsIbDAO = iAmsIbDAO;
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

	public IAmsIbKickbackDAO<AmsIbKickback> getiAmsIbKickbackDAO() {
		return iAmsIbKickbackDAO;
	}

	public void setiAmsIbKickbackDAO(
			IAmsIbKickbackDAO<AmsIbKickback> iAmsIbKickbackDAO) {
		this.iAmsIbKickbackDAO = iAmsIbKickbackDAO;
	}
		
	
	public WhiteLabelConfigInfo getWhiteLabelConfigInfo(String key, String wlCode) {
		WhiteLabelConfigInfo whiteLableConfigInfo = null;
		AmsWhitelabelConfig amsWhitelabelConfig= getiAmsWhitelabelConfigDAO().getAmsWhiteLabelConfig(key, wlCode);
		if(amsWhitelabelConfig != null) {
			whiteLableConfigInfo = new WhiteLabelConfigInfo();
			BeanUtils.copyProperties(amsWhitelabelConfig, whiteLableConfigInfo);
			AmsWhitelabelConfigId amsWhitelabelConfigId = amsWhitelabelConfig.getId();
			if(amsWhitelabelConfigId != null) {
				whiteLableConfigInfo.setWlCode(amsWhitelabelConfigId.getWlCode());
				whiteLableConfigInfo.setConfigKey(amsWhitelabelConfigId.getConfigKey());
			}
		}
		return whiteLableConfigInfo;
	}
	public List<String> getListClientCustomerInfo(String customerId) {
		List<String> listCustomerInfo = null;		
		List<AmsIbClient> listAmsIbClient = null;
		listAmsIbClient = getiAmsIbClientDAO().getListAmsIbClient(customerId);
		if(listAmsIbClient != null && listAmsIbClient.size() > 0) {
			listCustomerInfo = new ArrayList<String>();
			for(AmsIbClient amsIbClient : listAmsIbClient) {
				String clientCustomerId = amsIbClient.getId().getClientCustomerId();
				listCustomerInfo.add(clientCustomerId);
			}
		}
		return listCustomerInfo;
	}
	public boolean isIbClient(String customerId, String clientCustomerId) {
		boolean result = false;
		List<String> listClient = getListClientCustomerInfo(customerId);
		if(listClient != null && listClient.size() > 0) {
			return listClient.contains(clientCustomerId);
		}
		return result;
	}
	public List<IbClientCustomer> getListIbCustomer (String clientCustomerId,String customerId, String customerName, PagingInfo pagingInfo) {
		List<IbClientCustomer> listIbClientCustomer = null;
		List<AmsIbClient> listAmsIbClient = null;		
		AmsCustomer amsCustomer = null;
		String customerServiceId = "";
		SysCurrency amsSysCurrency = null;
		AmsCustomerService amsCustomerService = null;
		try {
			listAmsIbClient = getiAmsIbClientDAO().getAmsIbClientList(clientCustomerId, customerId, customerName, pagingInfo);
			if(listAmsIbClient != null && listAmsIbClient.size() > 0) {
				listIbClientCustomer = new ArrayList<IbClientCustomer>();
				for(AmsIbClient amsIbClient: listAmsIbClient) {
					// get fullName of customer
					String fullName = "";
					String currencyCode="";
					AmsIbClientId amsIbClientId = amsIbClient.getId();
					List<AmsCustomer> listCustomers = getiAmsCustomerDAO().getCustomerInfoList(amsIbClientId.getClientCustomerId());
					if(listCustomers !=null && listCustomers.size() > 0) {
						amsCustomer = listCustomers.get(0);
						if(amsCustomer != null) {
							fullName = amsCustomer.getFullName();
							amsSysCurrency = amsCustomer.getSysCurrency();
							if(amsSysCurrency != null) {
								currencyCode = amsSysCurrency.getCurrencyCode();
							}
						}
						
					}
					List<AmsCustomerService> listAmsCustomerServices = getiAmsCustomerServiceDAO().getListCustomerServicesInfo(amsIbClientId.getClientCustomerId(), IConstants.SERVICES_TYPE.FX);
					if(listAmsCustomerServices != null && listAmsCustomerServices.size() > 0) {
						amsCustomerService = listAmsCustomerServices.get(0);
						if(amsCustomerService != null) {
							customerServiceId = amsCustomerService.getCustomerServiceId();
						}
					}
					// get Total kickback Amount by OrderCustomer					
					Double total = getiAmsIbKickbackDAO().getKickbackTotalByOrderCustomerId(amsIbClientId.getCustomerId(),customerServiceId);
					// get kickback date by OrderCustomer
					Timestamp kickbackDate = getiAmsIbKickbackDAO().getKickbackDateByOrderCustomerId(amsIbClientId.getCustomerId(),customerServiceId);
					IbClientCustomer ibClientCustomer = new IbClientCustomer();
					ibClientCustomer.setCustomerId(amsIbClientId.getClientCustomerId());					
					ibClientCustomer.setFullName(fullName);					
					ibClientCustomer.setCurrencyCode(currencyCode);
					if(total !=null ) {
						ibClientCustomer.setTotal(total);
					}
					if(kickbackDate !=null ){
						ibClientCustomer.setKickbackDate(kickbackDate);
					}
					listIbClientCustomer.add(ibClientCustomer);	
				}
			}
		}catch(Exception ex) {
			log.error(ex.getLocalizedMessage(),ex);
		}
		return listIbClientCustomer;
	}
	/**
	 * 　
	 * get ib information
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 6, 2012
	 * @MdDate
	 */
	public IbInfo getIbInfo(String customerId) {
		IbInfo ibInfo = null;
		AmsIb amsIb = null;		
		try {
			amsIb = getiAmsIbDAO().findById(AmsIb.class, customerId);		
			if(amsIb != null) {
				ibInfo = new IbInfo();
				BeanUtils.copyProperties(amsIb, ibInfo);
			}
			if(ibInfo != null) {
				Long accountTotal = getIBAccountTotal(customerId);
				ibInfo.setAccountTotal(accountTotal);
				Double kickbackTotal = getKickbackTotal(customerId);
				ibInfo.setKickbackTotal(kickbackTotal);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage(), ex);
		}
	
		return ibInfo;
	}
	/**
	 * 　
	 * get Ib account total
	 * @param
	 * @return
	 * @auth QUyTM
	 * @CrDate Aug 6, 2012
	 * @MdDate
	 */
	public Long getIBAccountTotal(String customerId) {
		Long accountTotal = new Long(0);
		accountTotal = getiAmsIbClientDAO().getCountAmsIbClient(customerId);
		return accountTotal;
	}
	/**
	 * 　
	 * GET KICKBACK TOTAL
	 * @param
	 * @return
	 * @auth qUYtm
	 * @CrDate Aug 6, 2012
	 * @MdDate
	 */
	public Double getKickbackTotal(String customerId) {
		Double kickbackTotal = new Double(0);		
		kickbackTotal = getiAmsIbKickbackDAO().getKickbackTotal(customerId);
		return kickbackTotal;
	}
	public List<AmsIbKickback> searchIbKickBackHistory (String customerId, String orderCustomerId, String orderId, String orderSymbolCd, String fromDate, String toDate, PagingInfo pagingInfo){
		return getiAmsIbKickbackDAO().getIBKickback(customerId, orderCustomerId, orderId, orderSymbolCd, fromDate, toDate, pagingInfo);
	}

	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}

	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}
	/**
	 * 　
	 * Register IB customer 
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 7, 2012
	 * @MdDate
	 */

	public Integer registerIBCustomer(CustomerInfo customerInfo,String wlCode, String currentUserId,String rootPath) {		
		String registerReason = "Register customer";		
		String loginId = null;
		String loginPassword = null;
		String investorPassword = "";
		AmsCustomer amsCustomer = new AmsCustomer();
		SysCurrency sysCurrency = null;
		AmsWhitelabel amsWhitelabel = null;
		
		AmsIb amsIb = new AmsIb();
		AmsIbClient amsIbClient = new AmsIbClient();			
		AmsSubGroup amsSubGroup = null;
		AmsSubGroup amsSubGroupBo = null;
        AmsSubGroup amsSubGroupCopyTrade = null;
        AmsSubGroup amsSubGroupDemoFx = null;
		AmsGroup amsGroup = new AmsGroup();
		String md5LoginPassword = "";
		try{
			// generate loginID
			AmsWhitelabel amswl= getiAmsWhitelabelDAO().getAmsWhiteLabel(wlCode);
			
			String wlRefNo = StringUtil.toString(amswl.getWlRefNo());
			loginId = generateCustomerId(wlCode,wlRefNo);
			// generate loginPassword
			loginPassword = generateRandomPassword(PASSWORD_DEFAULT_LENGTH);
			investorPassword = generateRandomPassword(PASSWORD_DEFAULT_LENGTH);
			log.info("encode login pass by MD5");
			md5LoginPassword = Security.MD5(loginPassword);
			customerInfo.setLoginPass(md5LoginPassword);	
			customerInfo.setWlCode(wlCode);
			customerInfo.setCustomerId(loginId);
			log.info("[start] get country name on db with countryId =" + customerInfo.getCountryId());
			AmsSysCountry amsSysCountry = amsSysCountryDAO.findById(AmsSysCountry.class, customerInfo.getCountryId());
			if(amsSysCountry != null) {
				customerInfo.setCountryName(amsSysCountry.getCountryName());
				customerInfo.setCountryCode(amsSysCountry.getCountryCode());
			}
			log.info("[end] get country name on db with countryId =" + customerInfo.getCountryId());
		}catch(Exception ex) {		
			log.error(ex.getMessage(),ex);
			return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
		}
		Integer accountType = null;
		AmsCustomer amsCustomerCurrentUser = getiAmsCustomerDAO().findById(AmsCustomer.class, currentUserId);
		if(amsCustomerCurrentUser != null) {
			amsGroup = amsCustomerCurrentUser.getAmsGroup();
			sysCurrency = amsCustomerCurrentUser.getSysCurrency();
			accountType = amsCustomerCurrentUser.getAccountType();
		}
		if(accountType == null) accountType = IConstants.ACCOUNT_TYPE.STANDARD_ACCOUNT;
		
		String customerServiceId = loginId + IConstants.SERVICES_TYPE.FX;
		String agentServiceId = "";	
		Integer leverage = null;
//		List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDAO().getListCustomerServicesInfo(currentUserId, IConstants.SERVICES_TYPE.FX);
		List<AmsCustomerService> listAmsCustomerServices = getiAmsCustomerServiceDAO().getListCustomerServices(currentUserId);
		if(listAmsCustomerServices != null && listAmsCustomerServices.size() > 0) {
			for(AmsCustomerService amsCustomerServiceOfCurrent : listAmsCustomerServices) {
				if(IConstants.SERVICES_TYPE.FX.equals(amsCustomerServiceOfCurrent.getServiceType())) {
					amsSubGroup = amsCustomerServiceOfCurrent.getAmsSubGroup();
					agentServiceId = amsCustomerServiceOfCurrent.getCustomerServiceId();						
				} else if(IConstants.SERVICES_TYPE.BO.equals(amsCustomerServiceOfCurrent.getServiceType())) {
					amsSubGroupBo = amsCustomerServiceOfCurrent.getAmsSubGroup();
				} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(amsCustomerServiceOfCurrent.getServiceType())) {
                    amsSubGroupCopyTrade = amsCustomerServiceOfCurrent.getAmsSubGroup();
                } else if(IConstants.SERVICES_TYPE.DEMO_FXCD.equals(amsCustomerServiceOfCurrent.getServiceType())) {
                    amsSubGroupDemoFx = amsCustomerServiceOfCurrent.getAmsSubGroup();
                }
			}
		}
		String demoAccountId = generateDemoCustomerId(IConstants.UNIQUE_CONTEXT.CUSTOMER_CONTEXT_DEMO_FX, StringUtil.toString(IConstants.SERVICES_TYPE.DEMO_FXCD));
		
		String subGroupCode = amsSubGroup.getSubGroupCode();
		leverage = amsSubGroup.getLeverage();
		
		//[TDSBO1.0-Administrator]Aug 21, 2012A - End
        Integer registerMT4Result = MT4Manager.getInstance().registerMT4Account(customerInfo, customerServiceId, wlCode, agentServiceId, subGroupCode, leverage, investorPassword, loginPassword);
        //if(amsSubGroupCopyTrade != null && registerMT4Result == IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS){
        if(amsSubGroupCopyTrade != null) {
        	registerMT4Result =  MT4Manager.getInstance().registerMT4Account(customerInfo, loginId + IConstants.SERVICES_TYPE.COPY_TRADE, wlCode, "0", amsSubGroupCopyTrade.getSubGroupCode(), amsSubGroupCopyTrade.getLeverage(), investorPassword, loginPassword);
        }
        if(customerInfo.isDemoFxFlag() && registerMT4Result == IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS && amsSubGroupDemoFx != null)
            registerMT4DemoAccount(customerInfo, amsSubGroupDemoFx, investorPassword, loginPassword, demoAccountId);
        
		if(registerMT4Result == IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS) {
			log.info("Start saving account into DB");				
			try {				
				// Register AMS_CUSTOMER
				if(amsGroup == null) {
					log.warn("Cannot find Default group for WL: " + wlCode);					
					return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
				}		
				SysAppDate sysAppDate = null;
				List<SysAppDate> listSysAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
				if(listSysAppDate != null && listSysAppDate.size() > 0) {
					sysAppDate = listSysAppDate.get(0);
				}
				if(sysAppDate == null) {
					log.warn("Cannot find appdate for FrontDate");
					return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
				}
				
//				amsSysCurrency = new SysCurrency();
//				amsSysCurrency.setCurrencyCode(customerInfo.getCurrencyCode());
				log.info("Register AMS CUSTOMER ");
				log.info("LoginID " + loginId);
				log.info("LoginPassword " + loginPassword);
				log.info("FullName " + customerInfo.getFirstName() + " " + customerInfo.getLastName());
				log.info("FirstName " + customerInfo.getFirstName());
				log.info("LastName "+ customerInfo.getLastName());
				log.info("Mail" +customerInfo.getMailMain());
				log.info("CurrencyCode " + sysCurrency.getCurrencyCode());
				log.info("End register AMS CUSTOMER");
				//[TDSBO1.0-Administrator]Aug 11, 2012A - Start - add country id for ams customer
				if(customerInfo.getCountryId() != null) {
					AmsSysCountry amsSysCountry = amsSysCountryDAO.findById(AmsSysCountry.class, customerInfo.getCountryId());
					amsCustomer.setAmsSysCountry(amsSysCountry);
                    amsCustomer.setDisplayLanguage(amsSysCountry == null ? customerInfo.getDisplayLanguage() : amsSysCountry.getDefaultLanguage());
				}else {
                    amsCustomer.setDisplayLanguage(customerInfo.getDisplayLanguage());
                }
				
				//[TDSBO1.0-Administrator]Aug 11, 2012A - End
				amsCustomer.setCustomerId(loginId);
                amsCustomer.setPublicKey(Utilities.generateRandomPassword(16));
				amsCustomer.setLoginId(customerInfo.getMailMain());
				amsCustomer.setLoginPass(md5LoginPassword);
				amsCustomer.setFullName(customerInfo.getFirstName() + " " + customerInfo.getLastName());
				amsCustomer.setFirstName(customerInfo.getFirstName());
				amsCustomer.setLastName(customerInfo.getLastName());
				amsCustomer.setMailMain(customerInfo.getMailMain());
                amsCustomer.setTel1(customerInfo.getPhoneCode() + customerInfo.getTel1());
                amsCustomer.setPhoneType(customerInfo.getPhoneType());
                if(customerInfo.getBirthday() != null) {
                	String birthday = customerInfo.getBirthday();
                	birthday = birthday.replace("/", "");
                	amsCustomer.setBirthday(birthday);
                }
                
                amsCustomer.setAccountType(accountType);
                
                amsCustomer.setSex(customerInfo.getSex());
				amsCustomer.setSysCurrency(sysCurrency);				
				amsCustomer.setAmsGroup(amsGroup);
                amsCustomer.setAddress(customerInfo.getAddress());
                amsCustomer.setCity(customerInfo.getCity());
                amsCustomer.setPrefecture(customerInfo.getPrefecture());
                amsCustomer.setZipcode(customerInfo.getZipcode());
				//amsCustomer.setServiceTypeBO(IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setServiceTypeBO(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setServiceTypeDemoBO(IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setServiceTypeDemoFX(customerInfo.isDemoFxFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setServiceTypeFX(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setExchangerFlag(IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomer.setAllowChangePassFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setAllowNewOrderFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setWlCode(wlCode);
				amsCustomer.setConfirm1Flg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setConfirm2Flg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setConfirm3Flg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setConfirm4Flg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setAccountClass(customerInfo.getAccountClass());
				amsCustomer.setAllowWithdrawalFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setAllowLoginFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setAccountApplicationDate(sysAppDate.getId().getFrontDate());
				amsCustomer.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
                amsCustomer.setVerifySignatureStatus(IConstants.VERIFY_STATUS.VERIFIED);
                amsCustomer.setVerifyAddressStatus(IConstants.VERIFY_STATUS.VERIFIED);
                amsCustomer.setVerifyPassportStatus(IConstants.VERIFY_STATUS.VERIFIED);
                amsCustomer.setVerifyPhoneStatus(IConstants.VERIFY_STATUS.VERIFIED);
                amsCustomer.setInvestorPass(Security.MD5(investorPassword));
                amsCustomer.setFirstNameKana(customerInfo.getFirstName());
                amsCustomer.setLastNameKana(customerInfo.getLastName());
                amsCustomer.setDocumentPostDate(sysAppDate == null ? null : sysAppDate.getId().getFrontDate());
                amsCustomer.setDocumentAcceptDate(sysAppDate == null ? null : sysAppDate.getId().getFrontDate());
				
				
				amsCustomer.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCustomer.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				//[NTS1.0-Administrator]Aug 15, 2012A - Start - add open account status, application date follow new req  
				amsCustomer.setAccountOpenStatus(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomer.setAccountActiveDate(sysAppDate.getId().getFrontDate());
				//[NTS1.0-Administrator]Aug 15, 2012A - End
				getiAmsCustomerDAO().save(amsCustomer);

                //Register SC_CUSTOMER
                ScCustomer scCustomer = new ScCustomer();
                scCustomer.setCustomerId(amsCustomer.getCustomerId());
                scCustomer.setUserName(customerInfo.getUsername());                
                scCustomer.setUserType(IConstants.SC_USER_TYPE.ALL);
                scCustomer.setFollowerNo(0);
                scCustomer.setCopierNo(0);
                scCustomer.setSendMessageFlg(1);
                scCustomer.setWriteMyBoardFlg(1);
                scCustomer.setNotificationFlg(1);
                BigDecimal zero = new BigDecimal("0");
                scCustomer.setSignalTotalPips(zero);
                scCustomer.setSignalTotalReturn(zero);
                scCustomer.setSignalTotalTrade(zero);
                scCustomer.setSignalWinRatio(zero);
                scCustomer.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                scCustomer.setInputDate(timestamp);
                scCustomer.setUpdateDate(timestamp);
                scCustomerDAO.save(scCustomer);

                //upload avatar
                uploadAvatar(amsCustomer.getCustomerId(),rootPath);
                
                String brokerCd = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.BROKER_OWNER).get("1");
                List<ScBroker> scBrokerList = scBrokerDAO.findByBrokerCd(brokerCd);
                Integer brokerId = null;
                Integer demoBrokerId = null;
                for(ScBroker broker : scBrokerList){
                    if(IConstants.SERVER_ADDRESS.REAL.equals(broker.getServerAddress())){
                        brokerId = broker.getBrokerId();
                    } else  if(IConstants.SERVER_ADDRESS.DEMO.equals(broker.getServerAddress())){
                        demoBrokerId = broker.getBrokerId();
                    }
                }

                //Register ScCustomerService for FX/CFD
                registerScCustomerService(customerInfo, amsCustomer.getCustomerId(), amsSubGroup, brokerId, IConstants.SERVICES_TYPE.FX, loginId + IConstants.SERVICES_TYPE.FX,  IConstants.SC_ACCOUNT_TYPE.SIGNAL_PROVIDER, IConstants.SC_ACCOUNT_KIND.REAL);
                if(customerInfo.isDemoFxFlag() && amsSubGroupDemoFx != null){
                    //Register ScCustomerService for DEMO
                    registerScCustomerService(customerInfo, amsCustomer.getCustomerId(), amsSubGroupDemoFx, demoBrokerId, IConstants.SERVICES_TYPE.DEMO_FXCD, demoAccountId, IConstants.SC_ACCOUNT_TYPE.SIGNAL_PROVIDER, IConstants.SC_ACCOUNT_KIND.DEMO);
                }
                //Register ScCustomerService for COPY TRADE
                registerScCustomerService(customerInfo, amsCustomer.getCustomerId(), amsSubGroupCopyTrade, brokerId, IConstants.SERVICES_TYPE.SOCIAL_FX, loginId + IConstants.SERVICES_TYPE.COPY_TRADE, IConstants.SC_ACCOUNT_TYPE.COPY_TRADE, IConstants.SC_ACCOUNT_KIND.REAL);
			
				// insert cash balance for AMS
				AmsCashBalance amsCashBalance = new AmsCashBalance();
				AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
				amsCashBalanceId.setCurrencyCode(customerInfo.getCurrencyCode());
				amsCashBalanceId.setCustomerId(loginId);
				amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.AMS);
				amsCashBalance.setAmsCustomer(amsCustomer);
				amsCashBalance.setId(amsCashBalanceId);
				amsCashBalance.setCashBalance(new Double(0));
				amsCashBalance.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setPreviousBalance(new Double(0));
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCashBalance.setNetDepositAmount(new Double(0));
				amsCashBalance.setCreditAmount(new Double(0));
				getiAmsCashBalanceDAO().save(amsCashBalance);

				// insert cash balance for FX
				amsCashBalance = new AmsCashBalance();
				amsCashBalanceId = new AmsCashBalanceId();
				amsCashBalanceId.setCurrencyCode(amsSubGroup.getCurrencyCode());
				amsCashBalanceId.setCustomerId(loginId);
				amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.FX);
				amsCashBalance.setId(amsCashBalanceId);
				AmsCustomerService amsCustomerServiceForBalance = new AmsCustomerService();
				amsCustomerServiceForBalance.setCustomerServiceId(loginId + IConstants.SERVICES_TYPE.FX);

				amsCashBalance.setAmsCustomer(amsCustomer);
				amsCashBalance.setCashBalance(new Double(0));
				amsCashBalance.setAmsCustomerService(amsCustomerServiceForBalance);
				amsCashBalance.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setPreviousBalance(new Double(0));
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCashBalance.setNetDepositAmount(new Double(0));
				amsCashBalance.setCreditAmount(new Double(0));
				getiAmsCashBalanceDAO().save(amsCashBalance);
				
				
//				amsWhitelabel = new AmsWhitelabel();
//				amsWhitelabel.setWlCode(wlCode);
				// Register AMS_CUSTOMER_SERVICE
				AmsCustomerService amsCustomerService = new AmsCustomerService();
				amsCustomerService.setCustomerServiceId(customerServiceId);
				amsCustomerService.setAmsCustomer(amsCustomer);
				amsCustomerService.setServiceType(IConstants.SERVICES_TYPE.FX);
                AmsSubGroup tempSubGroup = new AmsSubGroup();
                tempSubGroup.setSubGroupId(amsSubGroup == null ? null : amsSubGroup.getDefaultSubGroupId());
				
				amsCustomerService.setAmsSubGroup(tempSubGroup);
				amsWhitelabel = amsSubGroup.getAmsWhitelabel();
				if (amsWhitelabel == null) {
					amsWhitelabel = new AmsWhitelabel();
					amsWhitelabel.setWlCode(wlCode);
				}
				amsCustomerService.setAmsWhitelabel(amsWhitelabel);
				// get leverage 
				amsCustomerService.setLeverage(leverage);
				amsCustomerService.setCustomerServiceStatus(customerInfo.isFxBoFlag() ? IConstants.AMS_OPEN_CUSTOMER_STATUS.OPEN_COMPLETED : IConstants.AMS_OPEN_CUSTOMER_STATUS.BEFORE_REGISTER);
				amsCustomerService.setAllowTransactFlg(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomerService.setAllowSendmoneyFlg(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomerService.setAllowLoginFlg(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
				amsCustomerService.setAgreementFlg(customerInfo.isFxBoFlag() ? IConstants.ACTIVE_FLG.ACTIVE : IConstants.ACTIVE_FLG.INACTIVE);
//				amsCustomerService.setAmsWhitelabel(amsWhitelabel);
				amsCustomerService.setAccountApplicationDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT));
				amsCustomerService.setAccountOpenDate(customerInfo.isFxBoFlag() ? DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT) : null);
				amsCustomerService.setAccountOpenFinishDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT));
				amsCustomerService.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerService.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);				
				
				
				getiAmsCustomerServiceDAO().save(amsCustomerService);    // save into database
				// end save into fx
				// save to fx demo
				// 
//				customerServiceId = loginId + IConstants.SERVICES_TYPE.DEMO_FXCD;
//				String subGroupDemoAccount = "";
//				customerServiceId = generateDemoCustomerId(IConstants.UNIQUE_CONTEXT.CUSTOMER_CONTEXT_DEMO_FX, StringUtil.toString(IConstants.SERVICES_TYPE.DEMO_FXCD));
//				if(amsSubGroupDemoFx != null) {
//					subGroupDemoAccount = amsSubGroupDemoFx.getSubGroupName();
//				}
				
//				int mt4DemoResult = MT4Manager.getInstance().registerMT4DemoAccount(customerInfo, customerServiceId, wlCode, subGroupDemoAccount, leverage, investorPassword, loginPassword, null);
//				if(mt4DemoResult == IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.intValue()) {
//					log.info("Open demo account success with serviceId = " + customerServiceId);
//				} else {
//					log.error("Cannot open demo account with serviceId = " + customerServiceId);
//				}				
//				amsCustomerService.setAllowTransactFlg(IConstants.ACTIVE_FLG.INACTIVE);
//				amsCustomerService.setAllowSendmoneyFlg(IConstants.ACTIVE_FLG.INACTIVE);
//				amsCustomerService.setAllowLoginFlg(IConstants.ACTIVE_FLG.INACTIVE);
//				amsCustomerService.setCustomerServiceId(customerServiceId);
//				amsCustomerService.setServiceType(IConstants.SERVICES_TYPE.DEMO_FXCD);
//				amsCustomerService.setAmsSubGroup(amsSubGroupDemoFx);
//				amsCustomerService.setCustomerServiceStatus(IConstants.AMS_OPEN_CUSTOMER_STATUS.BEFORE_REGISTER);
//				getiAmsCustomerServiceDAO().save(amsCustomerService);
				// end save into fx demo
				// save to bo
				customerServiceId = loginId + IConstants.SERVICES_TYPE.BO;
				amsCustomerService.setLeverage(null);
//				amsCustomerService.setAllowTransactFlg(IConstants.ACTIVE_FLG.ACTIVE);
//				amsCustomerService.setAllowSendmoneyFlg(IConstants.ACTIVE_FLG.ACTIVE);
//				amsCustomerService.setAllowLoginFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerService.setCustomerServiceId(customerServiceId);
				amsCustomerService.setServiceType(IConstants.SERVICES_TYPE.BO);

				if(amsSubGroupBo == null)
				    amsSubGroupBo = iAmsSubGroupDAO.findBySubGroupCode(IConstants.NATUREBO_USD_CODE);
				if (amsSubGroupBo == null) {
					log.warn("Not exist SUBGROUP " + IConstants.NATUREBO_USD_CODE);
					return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
				}
                AmsSubGroup tempSubGroupBo = new AmsSubGroup();
                tempSubGroupBo.setSubGroupId(amsSubGroupBo == null ? null : amsSubGroupBo.getDefaultSubGroupId());
				amsCustomerService.setAmsSubGroup(tempSubGroupBo);
				AmsWhitelabel wlBo = new AmsWhitelabel();
				wlBo.setWlCode(amsSubGroupBo.getAmsWhitelabel().getWlCode());
				amsCustomerService.setAmsWhitelabel(wlBo);
				getiAmsCustomerServiceDAO().save(amsCustomerService);
				
				// insert cash balance for BO
				amsCashBalance = new AmsCashBalance();
				amsCashBalanceId = new AmsCashBalanceId();
				amsCashBalanceId.setCurrencyCode(amsSubGroupBo.getCurrencyCode());
				amsCashBalanceId.setCustomerId(loginId);
				amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.BO);
				amsCashBalance.setId(amsCashBalanceId);
				amsCustomerServiceForBalance = new AmsCustomerService();
				amsCustomerServiceForBalance.setCustomerServiceId(loginId + IConstants.SERVICES_TYPE.BO);
				amsCashBalance.setAmsCustomer(amsCustomer);
				amsCashBalance.setCashBalance(new Double(0));
				amsCashBalance.setAmsCustomerService(amsCustomerServiceForBalance);
				amsCashBalance.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setPreviousBalance(new Double(0));
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCashBalance.setNetDepositAmount(new Double(0));
				amsCashBalance.setCreditAmount(new Double(0));
				getiAmsCashBalanceDAO().save(amsCashBalance);
				
                //Copy Trade
                customerServiceId = loginId + IConstants.SERVICES_TYPE.COPY_TRADE;
                amsCustomerService.setLeverage(amsSubGroupCopyTrade.getLeverage());
                amsCustomerService.setCustomerServiceId(customerServiceId);
                amsCustomerService.setServiceType(IConstants.SERVICES_TYPE.COPY_TRADE);
                amsCustomerService.setAmsSubGroup(amsSubGroupCopyTrade);
                AmsWhitelabel wlCopyTrade = new AmsWhitelabel();
                wlCopyTrade.setWlCode(amsSubGroupCopyTrade.getAmsWhitelabel().getWlCode());
                amsCustomerService.setAmsWhitelabel(wlCopyTrade);
                getiAmsCustomerServiceDAO().save(amsCustomerService);
                
                amsCashBalance = new AmsCashBalance();
				amsCashBalanceId = new AmsCashBalanceId();
				amsCashBalanceId.setCurrencyCode(amsSubGroup.getCurrencyCode());
				amsCashBalanceId.setCustomerId(loginId);
				amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.COPY_TRADE);
				amsCashBalance.setId(amsCashBalanceId);
				amsCustomerServiceForBalance.setCustomerServiceId(loginId + IConstants.SERVICES_TYPE.COPY_TRADE);

				amsCashBalance.setAmsCustomer(amsCustomer);
				amsCashBalance.setCashBalance(new Double(0));
				amsCashBalance.setAmsCustomerService(amsCustomerServiceForBalance);
				amsCashBalance.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setPreviousBalance(new Double(0));
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCashBalance.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCashBalance.setNetDepositAmount(new Double(0));
				amsCashBalance.setCreditAmount(new Double(0));
				getiAmsCashBalanceDAO().save(amsCashBalance);
				
                //DemoFx
                if(customerInfo.isDemoFxFlag() && amsSubGroupDemoFx != null){
                    customerServiceId = demoAccountId;
                    amsCustomerService.setLeverage(amsSubGroupDemoFx.getLeverage());
                    amsCustomerService.setCustomerServiceId(customerServiceId);
                    amsCustomerService.setServiceType(IConstants.SERVICES_TYPE.DEMO_FXCD);
                    AmsSubGroup tempSubGroupDemoFx = new AmsSubGroup();
                    tempSubGroupDemoFx.setSubGroupId(amsSubGroupDemoFx == null ? null : amsSubGroupDemoFx.getDefaultSubGroupId());
                    amsCustomerService.setAmsSubGroup(tempSubGroupDemoFx);
                    AmsWhitelabel wlDemoFx = new AmsWhitelabel();
                    wlDemoFx.setWlCode(amsSubGroupDemoFx.getAmsWhitelabel().getWlCode());
                    amsCustomerService.setAmsWhitelabel(wlDemoFx);
                    amsCustomerService.setCustomerServiceStatus(IConstants.AMS_OPEN_CUSTOMER_STATUS.OPEN_COMPLETED);
                    amsCustomerService.setAllowTransactFlg(IConstants.ACTIVE_FLG.ACTIVE);
                    amsCustomerService.setAllowSendmoneyFlg(IConstants.ACTIVE_FLG.ACTIVE);
                    amsCustomerService.setAllowLoginFlg(IConstants.ACTIVE_FLG.ACTIVE);
                    amsCustomerService.setAgreementFlg(IConstants.ACTIVE_FLG.ACTIVE);
                    amsCustomerService.setAccountOpenDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT));
                    getiAmsCustomerServiceDAO().save(amsCustomerService);
                }
				
				
				
				// end save to BO
				log.info("Register AMS CUSTOMER SERVICE");
				log.info("CustomerServiceID " + amsCustomer.getCustomerId()+ IConstants.SERVICES_TYPE.FX);
				log.info("serviceType " + customerInfo.getServiceType());
				log.info("LeverageId" + amsSubGroup.getLeverage());
				log.info("End register AMS CUSTOMER SERVICE");

				// Register AMS_CUSTOMER_TRACE
				AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
				amsCustomerTrace.setServiceType(IConstants.SERVICES_TYPE.AMS);
				amsCustomerTrace.setAmsCustomer(amsCustomer);
				amsCustomerTrace.setReason(registerReason);
				amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);				
				amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
				amsCustomerTrace.setValue1("");
				amsCustomerTrace.setValue2("");
				getiAmsCustomerTraceDAO().save(amsCustomerTrace);
				log.info("Register AMS CUSTOMER TRACE");
				log.info("CustomerID " + amsCustomer.getCustomerId());
				log.info("Reason " + registerReason);
				log.info(" FullName " +amsCustomer.getFullName());
				log.info("End register AMS CUSTOMER TRACE");

				// Register AMS_IB
				amsIb.setCustomerId(loginId);		
				amsIb.setAmsCustomer(amsCustomer);
				amsIb.setIbType(IConstants.IB_TYPE.NORMAL_IB);
				amsIb.setIbLink("");
				amsIb.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsIb.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsIb.setUpdateDate(new Timestamp(System.currentTimeMillis()));								
				getiAmsIbDAO().save(amsIb); 				// save into Database
				log.info("Register AMS IB");
				log.info("CustomerID " + amsCustomer.getCustomerId());
//				log.info("IBlink  " + amsIb.getIbLink());
				log.info(" InputDate " +amsIb.getInputDate());
				log.info("End register AMS IB");
				// Register AMS_IB_CLIENT
				AmsIbClientId id = new AmsIbClientId();
				id.setCustomerId(currentUserId);
				id.setClientCustomerId(loginId);
				amsIbClient.setId(id);
				amsIbClient.setAmsCustomer(amsCustomer);
				amsIbClient.setClientOpenDate(sysAppDate.getId().getFrontDate());
				amsIbClient.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsIbClient.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsIbClient.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				log.info("Register AMSIB Client for AmsIB client "+amsIbClient.getId());
				log.info("OpenDate " +amsIbClient.getClientOpenDate() + ", ActiveFlg + " + amsIbClient);
				getiAmsIbClientDAO().save(amsIbClient);   	// save into Database
			} catch(Exception ex){
				log.error(ex.getMessage(),ex);
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
			}
			
			// send mail to open account
			String language = amsCustomer.getDisplayLanguage();
			if(language == null || StringUtils.isBlank(language)) {
				language = IConstants.Language.ENGLISH;
			}
			sendmailOpenAccount(amsCustomer, language, loginPassword);

            if(customerInfo.isFxBoFlag()){
                String mt4Id = loginId + IConstants.SERVICES_TYPE.FX;
                sendmailOpenFxBo(amsCustomer, language, mt4Id, loginPassword, investorPassword);

                sendmailOpenSocial(amsCustomer, language, loginId + IConstants.SERVICES_TYPE.COPY_TRADE, loginPassword, investorPassword);
            }
            if(customerInfo.isDemoFxFlag()){
                String mt4Id = loginId + IConstants.SERVICES_TYPE.DEMO_FXCD;
                sendmailOpenDemoFx(amsCustomer, language, loginPassword, wlCode, loginId + IConstants.SERVICES_TYPE.DEMO_FXCD);
            }
			
			return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;	
		} else {
			log.warn("Cannot open account + " + customerServiceId + " and status = " + registerMT4Result);
		}
		return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
	}

    private void registerScCustomerService(CustomerInfo customerInfo, String customerId, AmsSubGroup subGroup, Integer brokerId, Integer serviceType, String accountId, Integer accountType, Integer accountKind) {
        ScCustomerService customerService = new ScCustomerService();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String subGroupCd = null;
        if(subGroup != null && subGroup.getDefaultSubGroupId() != null){
            AmsSubGroup tempSubGroup = iAmsSubGroupDAO.findById(AmsSubGroup.class, subGroup.getDefaultSubGroupId());
            if(tempSubGroup != null) subGroupCd = tempSubGroup.getSubGroupCode();
        }
        customerService.setInputDate(timestamp);
        customerService.setUpdateDate(timestamp);
        customerService.setCustomerId(customerId);
        customerService.setSubGroupCd(subGroupCd);
        customerService.setBrokerId(brokerId);
        customerService.setAccountId(accountId);
        customerService.setServiceType(serviceType);
        customerService.setAccountType(accountType);
        customerService.setLeverage(new BigDecimal(subGroup.getLeverage()));
        customerService.setBaseCurrency(customerInfo.getCurrencyCode());
        customerService.setEnableFlg(IConstants.ENABLE_FLG.ENABLE);
        customerService.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
        customerService.setBrokerCd("NFX");
        customerService.setAccountKind(accountKind);
        scCustomerServiceDAO.save(customerService);
    }

    private void sendmailOpenDemoFx(AmsCustomer amsCustomer, String language, String loginPassword, String wlCode, String demoCustomerServiceId) {
        // send mail template
        log.info("[start] send mail about open account demo fx successful");
        String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_DEMOFX).append("_").append(language).toString();

        AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
        amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
        amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
        amsMailTemplateInfo.setCustomerServiceId(demoCustomerServiceId);
        amsMailTemplateInfo.setLoginPass(loginPassword);
        HashMap<String, String> to = new HashMap<String, String>();
        to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
        amsMailTemplateInfo.setTo(to);
        amsMailTemplateInfo.setMailCode(mailCode);
        amsMailTemplateInfo.setSubject(mailCode);
        amsMailTemplateInfo.setWlCode(wlCode);
//        JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
        jmsContextSender.sendMail(amsMailTemplateInfo, false);
        log.info("[end] send mail about open account demo fx successful");
    }

    public Integer registerMT4DemoAccount(CustomerInfo customerInfo, AmsSubGroup amsSubGroup, String investorPassword, String masterPassword, String demoAccountId) {
        try {
            String wlCode = customerInfo.getWlCode();
            Integer serviceType = IConstants.SERVICES_TYPE.DEMO_FXCD;
            // find subgroup
            String currency = customerInfo.getCurrencyAms();
            if (StringUtil.isEmpty(currency)) {
                currency = IConstants.CURRENCY_CODE.USD;
            }
            
//            String configKey = currency + IConstants.WHITE_LABEL_CONFIG.SUFFIX_DEMO_FX;
//            AmsWhitelabelConfigId configId = new AmsWhitelabelConfigId(configKey, wlCode);
//            AmsWhitelabelConfig amsConfig = iAmsWhitelabelConfigDAO.findById(AmsWhitelabelConfig.class, configId);
//            String subGroupCode = null;
//            if (amsConfig == null) {
//                log.error("can not find whitelabel config with wlCode = " + wlCode + " ad config key = " + configKey);
//                return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
//            } else {
//                subGroupCode = amsConfig.getConfigValue();
//            }
//            AmsSubGroup subGroupDemo = iAmsSubGroupDAO.findBySubGroupCode(subGroupCode);
//            if (subGroupDemo == null) {
//                log.error("Can not find subgroup with serviceType = " + serviceType + ", wlCode = " + wlCode + ", currency = " + currency);
//                return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
//            }
            
            
            String customerId = customerInfo.getCustomerId();
            Integer registerAccountResult = MT4Manager.getInstance().registerMT4DemoAccount(customerInfo, demoAccountId, wlCode, amsSubGroup.getSubGroupCode(), amsSubGroup.getLeverage(), investorPassword, masterPassword, null);
            if(!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
                log.error("Create Demo FX fail: customerId = " + customerId + " wlCode = " + wlCode + " SubGroupCode = " + amsSubGroup.getSubGroupCode());
                return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
            }

        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            ex.printStackTrace();
            return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
        }
        return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
    }

	/**
	 * sendmailOpenFxBo　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailOpenFxBo(AmsCustomer amsCustomer, String language, String mt4Id, String passwordLogin, String investorPass) {
		log.info("[start] send mail about open Fx account successful with mt4Id = " + mt4Id + " passwordLogin = " + passwordLogin + " investorPass = " + investorPass);
//		mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_OPEN_FXBO + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_FXBO).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setCustomerServiceId(mt4Id);
		amsMailTemplateInfo.setLoginPass(passwordLogin);
		amsMailTemplateInfo.setInvestorPass(investorPass);
		
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		 jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about open Fx account successful with mt4Id = " + mt4Id + " passwordLogin = " + passwordLogin + " investorPass = " + investorPass);
	}

    private void sendmailOpenSocial(AmsCustomer amsCustomer, String language, String mt4Id, String passwordLogin, String investorPass) {
        log.info("[start] send mail about open social account successful with mt4Id = " + mt4Id + " passwordLogin = " + passwordLogin + " investorPass = " + investorPass);
//		mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_OPEN_FXBO + "_" + language);
        String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_SOCIAL).append("_").append(language).toString();
        AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
        amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
        amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
        amsMailTemplateInfo.setCustomerServiceId(mt4Id);
        amsMailTemplateInfo.setLoginPass(passwordLogin);
        amsMailTemplateInfo.setInvestorPass(investorPass);

        HashMap<String, String> to = new HashMap<String, String>();
        to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
        amsMailTemplateInfo.setTo(to);
        amsMailTemplateInfo.setMailCode(mailCode);
        amsMailTemplateInfo.setSubject(mailCode);
        amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//        JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
        jmsContextSender.sendMail(amsMailTemplateInfo, false);
        log.info("[end] send mail about open Fx account successful with mt4Id = " + mt4Id + " passwordLogin = " + passwordLogin + " investorPass = " + investorPass);
    }

	/**
	 * sendmailOpenAccount　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailOpenAccount(AmsCustomer amsCustomer, String language, String loginPassword) {
		log.info("[start] send mail about open account successful");
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_OPEN_ACCOUNT + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_OPEN_ACCOUNT).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setLoginPass(loginPassword);
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);				
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		 jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail about open account successful");
	}

	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDAO() {
		return iAmsCustomerServiceDAO;
	}

	public void setiAmsCustomerServiceDAO(
			IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO) {
		this.iAmsCustomerServiceDAO = iAmsCustomerServiceDAO;
	}

	public IAmsCustomerTraceDAO<AmsCustomerTrace> getiAmsCustomerTraceDAO() {
		return iAmsCustomerTraceDAO;
	}

	public void setiAmsCustomerTraceDAO(
			IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO) {
		this.iAmsCustomerTraceDAO = iAmsCustomerTraceDAO;
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
	
	public Integer registerMT4Account(CustomerInfo customerInfo,String loginId,String wlCode, String agentServiceId){
		int activeFlg = IConstants.ACTIVE_FLG.ACTIVE;
		String defaultCustomerName = "customer";
		Integer agentAccountId = MathUtil.parseInt(agentServiceId, 0);
		UserRecord userRecord = new UserRecord();
		String subGroupName = "";
		AmsSubGroup amsSubGroup = null;
		try{		
			Integer leverage = null;
			Integer customerId = MathUtil.parseInteger(loginId);
			String fullName = customerInfo.getFirstName() + " " + customerInfo.getLastName();			
			String currencyCode  = customerInfo.getCurrencyCode();		
			//AmsSysCurrency amsSysCurrency = getiAmsSysCurrencyDAO().getCurrencyInfo(currencyCode);
			String configKey =currencyCode + IConstants.WHITE_LABEL_CONFIG.SUFFIX_FX;
			Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
			String subGroupId = mapConfiguration.get(configKey);
			amsSubGroup = getiAmsSubGroupDAO().findBySubGroupCode(subGroupId);
			
			// get user session from customerInfo
//			AmsWhitelabelConfig amsWhitelabelConfig = getiAmsWhitelabelConfigDAO().getAmsWhiteLabelConfig(configKey, wlCode);
//			Integer leverage = null;
//			if(amsWhitelabelConfig != null){
//				String subGroupId = amsWhitelabelConfig.getConfigValue();
//				amsSubGroup = getiAmsSubGroupDAO().findBySubGroupCode(subGroupId);
//				
//			} 	
			if(amsSubGroup == null) {
				log.warn("Cannot find subgroup for configKey = " + configKey);
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
			}
			subGroupName = amsSubGroup.getSubGroupName();
			leverage = amsSubGroup.getLeverage();
			// Add to userRecord
			log.info("Start Adding to userRecord");		
			userRecord.setLogin(MathUtil.parseInteger(loginId));
//			userRecord.setEnable(activeFlg);
//			userRecord.setEnableChangePassword(IConstants.ACTIVE_FLG.ACTIVE);
//			userRecord.setEnableReadOnly(enableReadonly);
			userRecord.setGroup(subGroupName);
			userRecord.setPassword(customerInfo.getLoginPass());
			userRecord.setEnable(activeFlg);
			userRecord.setEnableChangePassword(IConstants.ACTIVE_FLG.ACTIVE);
			//userRecord.setEnableReadOnly(1);
			userRecord.setCountry(customerInfo.getCountryName());
			if(leverage != null)
			userRecord.setLeverage(leverage);
			if(fullName !=null && !StringUtil.isEmpty(fullName)) {
				userRecord.setName(fullName);
			}else {
				userRecord.setName(defaultCustomerName);
			}
			userRecord.setEmail(customerInfo.getMailMain());
			userRecord.setSendReports(IConstants.ACTIVE_FLG.ACTIVE);
			userRecord.setAgent_account(agentAccountId);
			// End adding to userRecord
			
			log.info("Add information into userRecord "+userRecord.getName() +"loginId "+userRecord.getLogin());
			log.info("Password" +userRecord.getPassword());
			log.info("Enable" +userRecord.getEnableChangePassword());
			log.info("Leverage "+userRecord.getLeverage());
			log.info("Group " +userRecord.getGroup());
			log.info("AgentAccount " +userRecord.getAgent_account());
			log.info("End Adding to userRecord");
			ArrayList<UserRecord> listUserRecords = new ArrayList<UserRecord>();
			listUserRecords.add(userRecord);
			AccountCreateMessage createAccountMessage = AccountCreateMessage.getInstance(customerId);
//			JMSSendClient.getInstance().sendOpenAccountRequest(listUserRecords);
			jmsContextSender.sendOpenAccountRequest(listUserRecords, true);
			int res = -1; 
			String passMt4 = "";
			String investorPassword = "";
			UserRecord ur = null;
			
			/**
			 * wait for response of MT4
			 */
			final int MAXIMUM_TRY_NUMBER = 30;
			int tryAgain = 0;
			while (true) {
				if (tryAgain >= MAXIMUM_TRY_NUMBER) {
					break;
				}

				if (createAccountMessage.getFlag(customerId)) {
					ur = createAccountMessage.getUserRecord(customerId);
					if (ur != null) {
						res = ur.getResult();
						passMt4 = ur.getPassword();
						investorPassword = ur.getPasswordInvestor();
						log.info("updateStatusForCreateNewMT4Account:" + res);
						log.debug("MT4 user record response: passMT4 is "
								+ passMt4
								+ " and investor passwd is "
								+ investorPassword);
						break;
					}

				}
				Thread.sleep(1000);
				tryAgain++;
			}
			createAccountMessage.removeUserRecord(customerId);
			if (res == IResultConstant.Register.SUCCESSFUL) {
				return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS;
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_FAIL;
	}

	public IAmsSubGroupDAO<AmsSubGroup> getiAmsSubGroupDAO() {
		return iAmsSubGroupDAO;
	}

	public void setiAmsSubGroupDAO(IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO) {
		this.iAmsSubGroupDAO = iAmsSubGroupDAO;
	}
	public synchronized static String getValueConfig(String input) {
		String FILE_NAME = "configs.properties";
		String result = null;
		try {
			Properties pros = Helpers.getProperties(FILE_NAME);
			result = pros.getProperty(input);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public IAmsWhitelabelDAO<AmsWhitelabel> getiAmsWhitelabelDAO() {
		return iAmsWhitelabelDAO;
	}

	public void setiAmsWhitelabelDAO(
			IAmsWhitelabelDAO<AmsWhitelabel> iAmsWhitelabelDAO) {
		this.iAmsWhitelabelDAO = iAmsWhitelabelDAO;
	}
	public String generateRandomPassword(int len) {
		final String RANDOM_STR = "0123456789abcdefghijklmnopqrstuvwxyz";
		final String RANDOM_NUMBER = "0123456789";
		Random rand = new Random();
		StringBuffer sb = new StringBuffer(len);
		char str;
		boolean checkOneNumber = false;
		for (int i = 0; i < len; i++) {
			str = RANDOM_STR.charAt(rand.nextInt(RANDOM_STR.length()));
			if (isNumericString(str + "")) {
				checkOneNumber = true;
			}
			sb.append(str);
		}
		
		// set one up case character
		int position1 = rand.nextInt(len);
		char str_check = sb.charAt(position1);
		while (isNumericString(str_check + "")) {
			position1 = rand.nextInt(len);
			str_check = sb.charAt(position1);
		}
		
		sb.setCharAt(position1, Character.toUpperCase(str_check));

		// check at least one number
		if (!checkOneNumber) {
			int position3 = rand.nextInt(len);
			while (position3 == position1) {
				position3 = rand.nextInt(len);
			}
			sb.setCharAt(position3,
					RANDOM_NUMBER.charAt(rand.nextInt(RANDOM_NUMBER.length())));
		}

		return sb.toString();
	}

	private boolean isNumericString(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}

		return true;
	}
	private synchronized String generateCustomerId(String contextID, String transactionId) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}

		Long counter = getiSysUniqueidCounterDAO().generateId(contextID);
		String countString = counter.toString();		
		String[] magicZeros = { "", // 0
				"0", // 1
				"00", // 2
				"000", // 3
				"0000", // 4
				"00000", // 5
				"000000", // 5
		};
		transactionId += magicZeros[6 - countString.length()];
		transactionId += countString;

		return transactionId;
	}
	
	private synchronized String generateDemoCustomerId(String contextID, String transactionId) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}

		Long counter = getiSysUniqueidCounterDAO().generateId(contextID);
		String countString = counter.toString();		
		String[] magicZeros = { "", // 0
				"0", // 1
				"00", // 2
				"000", // 3
				"0000", // 4
				"00000", // 5
				"000000", // 6
				"0000000", // 7
				"00000000", // 8
		};
		transactionId += magicZeros[8 - countString.length()];
		transactionId += countString;

		return transactionId;
	}
	
	private synchronized String generateDemoAccount(String contextID, String transactionId) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}

		Long counter = getiSysUniqueidCounterDAO().generateId(contextID);
		String countString = counter.toString();		
		String[] magicZeros = { "", // 0
				"0", // 1
				"00", // 2
				"000", // 3
				"0000", // 4
				"00000", // 5
				"000000", // 6
				"0000000", // 7
				"00000000", // 8
		};
		transactionId += magicZeros[8 - countString.length()];
		transactionId += countString;

		return transactionId;
	}
	public IAmsGroupDAO<AmsGroup> getiAmsGroupDAO() {
		return iAmsGroupDAO;
	}

	public void setiAmsGroupDAO(IAmsGroupDAO<AmsGroup> iAmsGroupDAO) {
		this.iAmsGroupDAO = iAmsGroupDAO;
	}

	public IAmsSysVirtualBankDAO<AmsSysVirtualBank> getiAmsSysVirtualBankDAO() {
		return iAmsSysVirtualBankDAO;
	}

	public void setiAmsSysVirtualBankDAO(
			IAmsSysVirtualBankDAO<AmsSysVirtualBank> iAmsSysVirtualBankDAO) {
		this.iAmsSysVirtualBankDAO = iAmsSysVirtualBankDAO;
	}

	public void sendMail(String mt4Id,String mt4Password,String email,String subject,String template) {
		MailInfo mail = new MailInfo();
		mail.setMt4Id(mt4Id);
		mail.setMt4Password(mt4Password);
		mail.setEmailAddress(email);
		try {
			mailService.sendAppMail(mail, email, AppConfiguration.getMailAdminSender(),subject, template);
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
		}
	}
	
	/**
	 * 　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author HuyenMT
	 * @CrDate Aug 20, 2012
	 */
	public List<AmsCustomerService> getListAmsCustomerService(String customerId){
		List<AmsCustomerService> listAmsCustomerService = null;
		try {
			listAmsCustomerService = new ArrayList<AmsCustomerService>();
			listAmsCustomerService = getiAmsCustomerServiceDAO().getListCustomerServices(customerId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return listAmsCustomerService;
	}
	public MailService getMailService() {
		return mailService;
	}
	/*
	*//**
	 * @return the iAmsSysSymbolDAO
	 *//*
	public IAmsSysSymbolDAO<AmsSysSymbol> getiAmsSysSymbolDAO() {
		return iAmsSysSymbolDAO;
	}

	*//**
	 * @param iAmsSysSymbolDAO the iAmsSysSymbolDAO to set
	 *//*
	public void setiAmsSysSymbolDAO(IAmsSysSymbolDAO<AmsSysSymbol> iAmsSysSymbolDAO) {
		this.iAmsSysSymbolDAO = iAmsSysSymbolDAO;
	}
*/

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	
	/**
	 * @return the iAmsTransferMoneyDAO
	 */
	public IAmsTransferMoneyDAO<AmsTransferMoney> getiAmsTransferMoneyDAO() {
		return iAmsTransferMoneyDAO;
	}

	/**
	 * @param iAmsTransferMoneyDAO the iAmsTransferMoneyDAO to set
	 */
	public void setiAmsTransferMoneyDAO(
			IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO) {
		this.iAmsTransferMoneyDAO = iAmsTransferMoneyDAO;
	}
	
	public Double getTotalAmountTransferMoney(Integer transferFrom, Integer status) {
		Double total = new Double(0);
		total = getiAmsTransferMoneyDAO().getTotalAmount(status, transferFrom);
		return total;
	}

	
	/**
	 * @return the iAmsCashBalanceDAO
	 */
	public IAmsCashBalanceDAO<AmsCashBalance> getiAmsCashBalanceDAO() {
		return iAmsCashBalanceDAO;
	}

	/**
	 * @param iAmsCashBalanceDAO the iAmsCashBalanceDAO to set
	 */
	public void setiAmsCashBalanceDAO(
			IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO) {
		this.iAmsCashBalanceDAO = iAmsCashBalanceDAO;
	}

	/**
	 * @return the iAmsCustomerService
	 */
	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerService() {
		return iAmsCustomerService;
	}

	/**
	 * @param iAmsCustomerService the iAmsCustomerService to set
	 */
	public void setiAmsCustomerService(IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerService) {
		this.iAmsCustomerService = iAmsCustomerService;
	}

	/**
	 * @return the iSysUniqueidCounterDAO
	 */
	public ISysUniqueidCounterDAO<SysUniqueidCounter> getiSysUniqueidCounterDAO() {
		return iSysUniqueidCounterDAO;
	}

	/**
	 * @param iSysUniqueidCounterDAO the iSysUniqueidCounterDAO to set
	 */
	public void setiSysUniqueidCounterDAO(
			ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO) {
		this.iSysUniqueidCounterDAO = iSysUniqueidCounterDAO;
	}

	/**
	 * @return the iSysCurrencyDAO
	 */
	public ISysCurrencyDAO<SysCurrency> getiSysCurrencyDAO() {
		return iSysCurrencyDAO;
	}

	/**
	 * @param iSysCurrencyDAO the iSysCurrencyDAO to set
	 */
	public void setiSysCurrencyDAO(ISysCurrencyDAO<SysCurrency> iSysCurrencyDAO) {
		this.iSysCurrencyDAO = iSysCurrencyDAO;
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
	 * @return the iFxSymbolDAO
	 */
	public IFxSymbolDAO<FxSymbol> getiFxSymbolDAO() {
		return iFxSymbolDAO;
	}

	/**
	 * @param iFxSymbolDAO the iFxSymbolDAO to set
	 */
	public void setiFxSymbolDAO(IFxSymbolDAO<FxSymbol> iFxSymbolDAO) {
		this.iFxSymbolDAO = iFxSymbolDAO;
	}

	public List<String> getListSymbol() {
		List<String> listSymbol = new ArrayList<String>();
		List<FxSymbol> listFxSymbol = null;
		listFxSymbol = getiFxSymbolDAO().getListOriginalFxSymbol();
		FxSymbol fxSymbol = null;
		if(listFxSymbol != null && listFxSymbol.size() > 0) {
			for(int i = 0; i < listFxSymbol.size(); i ++ ) {
				fxSymbol = listFxSymbol.get(i);
				listSymbol.add(fxSymbol.getSymbolCd());
			}
		}
		return listSymbol;
	}
	/**
	 * 　
	 * get list whitelablel config by ConfigType
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Oct 19, 2012
	 * @MdDate
	 */
	public List<String> getListWhiteLabelConfigInfo(String configType, String wlCode) {
		List<String> listWhiteLabelConfigInfo = null;
		WhiteLabelConfigInfo whiteLabelConfigInfo = null;
		List<AmsWhitelabelConfig> listAmsWhiteLabelConfig = getiAmsWhitelabelConfigDAO().getListWlConfig(configType, wlCode);
		if(listAmsWhiteLabelConfig != null && listAmsWhiteLabelConfig.size() > 0) {
			listWhiteLabelConfigInfo = new ArrayList<String>();
			for(int i = 0; i< listAmsWhiteLabelConfig.size(); i ++ ) {
				AmsWhitelabelConfig amsWhitelabelConfig= listAmsWhiteLabelConfig.get(i);
				if(amsWhitelabelConfig != null) {
					whiteLabelConfigInfo = new WhiteLabelConfigInfo();
					BeanUtils.copyProperties(amsWhitelabelConfig, whiteLabelConfigInfo);
					AmsWhitelabelConfigId amsWhitelabelConfigId = amsWhitelabelConfig.getId();
					if(amsWhitelabelConfigId != null) {
						whiteLabelConfigInfo.setWlCode(amsWhitelabelConfigId.getWlCode());
						whiteLabelConfigInfo.setConfigKey(amsWhitelabelConfigId.getConfigKey());
					}
					listWhiteLabelConfigInfo.add(whiteLabelConfigInfo.getConfigValue());
				}
			}			
		}
		return listWhiteLabelConfigInfo; 
		
	}

    public IScCustomerDAO<ScCustomer> getScCustomerDAO() {
        return scCustomerDAO;
    }

    public void setScCustomerDAO(IScCustomerDAO<ScCustomer> scCustomerDAO) {
        this.scCustomerDAO = scCustomerDAO;
    }

    public IScBrokerDAO<ScBroker> getScBrokerDAO() {
        return scBrokerDAO;
    }

    public void setScBrokerDAO(IScBrokerDAO<ScBroker> scBrokerDAO) {
        this.scBrokerDAO = scBrokerDAO;
    }

    public IScCustomerServiceDAO<ScCustomerService> getScCustomerServiceDAO() {
        return scCustomerServiceDAO;
    }

    public void setScCustomerServiceDAO(IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO) {
        this.scCustomerServiceDAO = scCustomerServiceDAO;
    }

	/**
	 * @param amsSysCountryDAO the amsSysCountryDAO to set
	 */
	public void setAmsSysCountryDAO(
			IAmsSysCountryDAO<AmsSysCountry> amsSysCountryDAO) {
		this.amsSysCountryDAO = amsSysCountryDAO;
	}
	
	/**
	 * upload avatar
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 28, 2013
	 */
	private boolean uploadAvatar(String customerId, String rootPath){
		try {
			FileUploadInfo fileUploadInfo = new FileUploadInfo();
			inputStream = new BufferedInputStream(new FileInputStream(rootPath));
			fileUploadInfo.setFile(new File(rootPath));
			fileUploadInfo.setCustomerId(customerId);
//			fileUploadInfo.setFileName(model.getUploadedAvatarFileName());
			fileUploadInfo.setRootPath(rootPath);
			File srcFile = fileUploadInfo.getFile();

			renameFileUpload(fileUploadInfo);
			BufferedImage originalImage = ImageIO.read(srcFile);
			BufferedImage resizedImage = resizeImage(originalImage,BufferedImage.TYPE_INT_RGB);
			ImageIO.write(resizedImage, "jpg", srcFile);
			fileUploadInfo.setFile(srcFile);
			
			// TODO: Fix to test
			String destPath = propsConfig.getProperty(URL_AVATAR_FOLDER);

			Integer copyResult = FileLoaderUtil.copyFile(fileUploadInfo,destPath);
			if (copyResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return false;
			}

			return true;
		}catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}
	}
	
	private void renameFileUpload(FileUploadInfo fileUploadInfo) {
		String fileExtension;
		StringBuffer newFileName;
		fileExtension = IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.JPG;
		newFileName = new StringBuffer(fileUploadInfo.getCustomerId());
		newFileName.append(fileExtension);
		fileUploadInfo.setFileName(newFileName.toString());
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
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

