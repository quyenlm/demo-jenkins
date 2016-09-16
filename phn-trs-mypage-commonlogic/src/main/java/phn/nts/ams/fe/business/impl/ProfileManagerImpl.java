package phn.nts.ams.fe.business.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;
import phn.com.components.trs.api.CRMIntegrationAPI;
import phn.com.components.trs.business.impl.TrsPostDocumentManagerImpl;
import phn.com.nts.ams.web.condition.ReportHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.dao.IAmsAffiliateDAO;
import phn.com.nts.db.dao.IAmsCustomerBankDAO;
import phn.com.nts.db.dao.IAmsCustomerCreditcardDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerDocDAO;
import phn.com.nts.db.dao.IAmsCustomerEwalletDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsCustomerSurveyDAO;
import phn.com.nts.db.dao.IAmsCustomerTraceDAO;
import phn.com.nts.db.dao.IAmsGroupDAO;
import phn.com.nts.db.dao.IAmsIbDAO;
import phn.com.nts.db.dao.IAmsSubGroupDAO;
import phn.com.nts.db.dao.IAmsSysBankBranchDAO;
import phn.com.nts.db.dao.IAmsSysBankDAO;
import phn.com.nts.db.dao.IAmsSysCountryDAO;
import phn.com.nts.db.dao.IAmsSysZipcodeDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWhitelabelDAO;
import phn.com.nts.db.dao.IAmsWhitelabelReportDAO;
import phn.com.nts.db.dao.IAmsWithdrawalRefDAO;
import phn.com.nts.db.dao.IBoCustomerDAO;
import phn.com.nts.db.dao.IScBrokerDAO;
import phn.com.nts.db.dao.IScCustomerCopyDAO;
import phn.com.nts.db.dao.IScCustomerDAO;
import phn.com.nts.db.dao.IScCustomerServiceDAO;
import phn.com.nts.db.dao.IScOrderDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysPropertyDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.entity.AmsAffiliate;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerBank;
import phn.com.nts.db.entity.AmsCustomerCreditcard;
import phn.com.nts.db.entity.AmsCustomerDoc;
import phn.com.nts.db.entity.AmsCustomerEwallet;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsCustomerSurvey;
import phn.com.nts.db.entity.AmsCustomerTrace;
import phn.com.nts.db.entity.AmsGroup;
import phn.com.nts.db.entity.AmsIb;
import phn.com.nts.db.entity.AmsSubGroup;
import phn.com.nts.db.entity.AmsSysBank;
import phn.com.nts.db.entity.AmsSysBankBranch;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsSysVirtualBank;
import phn.com.nts.db.entity.AmsSysZipcode;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWhitelabelConfigId;
import phn.com.nts.db.entity.AmsWhitelabelReport;
import phn.com.nts.db.entity.AmsWithdrawalRef;
import phn.com.nts.db.entity.BoCustomer;
import phn.com.nts.db.entity.ScBroker;
import phn.com.nts.db.entity.ScBrokerSetting;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.db.entity.ScCustomerCopy;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.db.entity.ScOrder;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysProperty;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.*;
import phn.com.nts.util.file.FileLoaderUtil;
import phn.com.nts.util.file.FileUploadInfo;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Cryptography;
import phn.com.nts.util.security.Security;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.ENABLE_MT4_FX;
import phn.com.trs.util.common.ITrsConstants.TRS_CONSTANT;
import phn.com.trs.util.enums.Result;
import phn.com.trs.util.enums.SocialSignalFlg;
import phn.com.trs.util.enums.SocialSignalProvider;
import phn.com.trs.util.enums.SyncSaleForceResult;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BrokerSettingInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CustReportHistoryInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerScInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.DocumentInfo;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.ntd.NTDManager;
import phn.nts.ams.fe.social.SCManager;
import phn.nts.ams.fe.util.AppConfiguration;
import phn.nts.ams.fe.util.MailInfo;
import phn.nts.ams.fe.util.MailService;
import phn.nts.ams.utils.Helper;
import cn.nextop.social.api.admin.proxy.glossary.AccountStatus;
import cn.nextop.social.api.admin.proxy.glossary.AccountType;
import cn.nextop.social.api.admin.proxy.glossary.CloseAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.CurrencyCode;
import cn.nextop.social.api.admin.proxy.glossary.CustomerMailConfig;
import cn.nextop.social.api.admin.proxy.glossary.LoginRestriction;
import cn.nextop.social.api.admin.proxy.glossary.ModifyAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.OpenAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.PilotingStatus;
import cn.nextop.social.api.admin.proxy.glossary.TradingRestriction;
import cn.nextop.social.api.admin.proxy.glossary.TransferRestriction;
import cn.nextop.social.api.admin.proxy.model.customer.Customer;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccount;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccountModification;
import cn.nextop.social.api.admin.proxy.model.customer.impl.CustomerAccountImpl;
import cn.nextop.social.api.admin.proxy.model.customer.impl.CustomerAccountModificationImpl;
import cn.nextop.social.api.admin.proxy.model.customer.impl.CustomerImpl;

import com.nts.common.Constant;
import com.nts.common.exchange.proto.ams.AmsCustomerModel;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.CustomerType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerModifySocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerRegisterSocialRequest.CorporationType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerBankInfo;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.nts.components.mail.bean.AmsScMailTemplateInfo;
import com.phn.mt.common.constant.IConstant;
import com.phn.mt.common.entity.UserRecord;
import com.sforce.soap.AMS_Customer_Sync_A.CustomerInfor__c;

public class ProfileManagerImpl implements IProfileManager {
	private static final Logit log = Logit.getInstance(ProfileManagerImpl.class);
	private static final int IMG_WIDTH = 160;
	private static final int IMG_HEIGHT = 160;
	private static final int FULL_IMG_WIDTH = 260;
	private static final int FULL_IMG_HEIGHT = 260;
	private static final int TEMP_IMG_WIDTH = 800;
	private static final int TEMP_IMG_HEIGHT = 800;
	private static final String URL_AVATAR_FOLDER = "url.avatar.folder";
	private static Properties propsConfig;
	private static final String APP_PROPS_FILE = "configs.properties";
	private static final String PATH_JASPER_OUTPUT = "post.document.pathFileJasper.output";
	
	private IAmsCustomerEwalletDAO<AmsCustomerEwallet> iAmsCustomerEwalletDAO;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO;
	private IAmsGroupDAO<AmsGroup> iAmsGroupDAO;
	private IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> iAmsCustomerCreditcardDAO;
	private ISysPropertyDAO<SysProperty> iSysPropertyDAO;
	private IAmsCustomerBankDAO<AmsCustomerBank> iAmsCustomerBankDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO;
	private IAmsIbDAO<AmsIb> iAmsIbDAO;
	private IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO;
	private MailService mailService;
	private IAmsSysZipcodeDAO<AmsSysZipcode> amsSysZipcodeDAO;
	private IAmsAffiliateDAO<AmsAffiliate> amsAffiliateDAO;
	private TrsPostDocumentManagerImpl postDocumentManager;
	private IAccountManager accountManager;
	// [NTS1.0-anhndn]Jan 21, 2013A - Start
	private IAmsCustomerDocDAO<AmsCustomerDoc> iAmsCustomerDocDAO;
	private IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO;
	// [NTS1.0-anhndn]Jan 21, 2013A - End

	// [NTS1.0-anhndn]Jan 25, 2013A - Start
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	// [NTS1.0-anhndn]Jan 25, 2013A - End

	private IScCustomerDAO<ScCustomer> iScCustomerDAO;
	private IScCustomerServiceDAO<ScCustomerService> iScCustomerServiceDAO;
	private IScBrokerDAO<ScBroker> iScBrokerDAO;
	private ISysAppDateDAO<SysAppDate> appDateDAO;
	private IScCustomerCopyDAO<ScCustomerCopy> iScCustomerCopyDAO;
	private IScOrderDAO<ScOrder> iScOrderDAO;
	private IBalanceManager balanceManager;
	private IJmsContextSender jmsContextSender;
	private IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;
	private IAmsSysBankDAO<AmsSysBank> amsSysBankDAO;
	private IAmsSysBankBranchDAO<AmsSysBankBranch> amsSysBankBranchDAO;
	private IAmsWhitelabelReportDAO<AmsWhitelabelReport> amsWhitelabelReportDAO;
	private IAmsWhitelabelDAO<AmsWhitelabel> amsWhitelabelDAO;
	private IBoCustomerDAO<BoCustomer> boCustomerDAO;
	private IAmsWithdrawalRefDAO<AmsWithdrawalRef> iAmsWithdrawalRefDAO;
	
	public IAmsWithdrawalRefDAO<AmsWithdrawalRef> getiAmsWithdrawalRefDAO() {
		return iAmsWithdrawalRefDAO;
	}

	public void setiAmsWithdrawalRefDAO(IAmsWithdrawalRefDAO<AmsWithdrawalRef> iAmsWithdrawalRefDAO) {
		this.iAmsWithdrawalRefDAO = iAmsWithdrawalRefDAO;
	}

	public IAmsWhitelabelReportDAO<AmsWhitelabelReport> getAmsWhitelabelReportDAO() {
		return amsWhitelabelReportDAO;
	}

	public void setAmsWhitelabelReportDAO(IAmsWhitelabelReportDAO<AmsWhitelabelReport> amsWhitelabelReportDAO) {
		this.amsWhitelabelReportDAO = amsWhitelabelReportDAO;
	}

	public IAmsAffiliateDAO<AmsAffiliate> getAmsAffiliateDAO() {
		return amsAffiliateDAO;
	}

	public void setAmsAffiliateDAO(IAmsAffiliateDAO<AmsAffiliate> amsAffiliateDAO) {
		this.amsAffiliateDAO = amsAffiliateDAO;
	}

	public IAmsSysBankBranchDAO<AmsSysBankBranch> getAmsSysBankBranchDAO() {
		return amsSysBankBranchDAO;
	}

	public void setAmsSysBankBranchDAO(IAmsSysBankBranchDAO<AmsSysBankBranch> amsSysBankBranchDAO) {
		this.amsSysBankBranchDAO = amsSysBankBranchDAO;
	}

	public IAmsSysBankDAO<AmsSysBank> getAmsSysBankDAO() {
		return amsSysBankDAO;
	}

	public void setAmsSysBankDAO(IAmsSysBankDAO<AmsSysBank> amsSysBankDAO) {
		this.amsSysBankDAO = amsSysBankDAO;
	}

	public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
		return amsCustomerSurveyDAO;
	}

	public void setAmsCustomerSurveyDAO(
			IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
		this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
	}

	public IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getAmsWhitelabelConfigDAO() {
		return amsWhitelabelConfigDAO;
	}

	public void setAmsWhitelabelConfigDAO(
			IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> amsWhitelabelConfigDAO) {
		this.amsWhitelabelConfigDAO = amsWhitelabelConfigDAO;
	}

	public IAmsWhitelabelDAO<AmsWhitelabel> getAmsWhitelabelDAO() {
		return amsWhitelabelDAO;
	}

	public void setAmsWhitelabelDAO(IAmsWhitelabelDAO<AmsWhitelabel> amsWhitelabelDAO) {
		this.amsWhitelabelDAO = amsWhitelabelDAO;
	}

	public IAmsSysZipcodeDAO<AmsSysZipcode> getAmsSysZipcodeDAO() {
		return amsSysZipcodeDAO;
	}

	public void setAmsSysZipcodeDAO(
			IAmsSysZipcodeDAO<AmsSysZipcode> amsSysZipcodeDAO) {
		this.amsSysZipcodeDAO = amsSysZipcodeDAO;
	}

	
	public TrsPostDocumentManagerImpl getPostDocumentManager() {
		return postDocumentManager;
	}

	public void setPostDocumentManager(
			TrsPostDocumentManagerImpl postDocumentManager) {
		this.postDocumentManager = postDocumentManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	static {
		try {
			propsConfig = Helpers.getProperties(APP_PROPS_FILE);
		} catch (Exception e) {
			log.warn("Could not load configuration file from: "	+ APP_PROPS_FILE, e);
		}
	}

	/**
	 * @return IAmsCustomerEwalletDAO
	 */
	public IAmsCustomerEwalletDAO<AmsCustomerEwallet> getiAmsCustomerEwalletDAO() {
		return iAmsCustomerEwalletDAO;
	}

	/**
	 * @param iAmsCustomerEwalletDAO
	 *            setiAmsCustomerEwalletDAO
	 */
	public void setiAmsCustomerEwalletDAO(
			IAmsCustomerEwalletDAO<AmsCustomerEwallet> iAmsCustomerEwalletDAO) {
		this.iAmsCustomerEwalletDAO = iAmsCustomerEwalletDAO;
	}

	/**
	 * @return IAmsCustomerDAO
	 */
	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}

	/**
	 * @param iAmsCustomerDAO
	 *            setiAmsCustomerDAO
	 */
	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}

	public IAmsSysCountryDAO<AmsSysCountry> getiAmsSysCountryDAO() {
		return iAmsSysCountryDAO;
	}

	public IAmsCustomerBankDAO<AmsCustomerBank> getiAmsCustomerBankDAO() {
		return iAmsCustomerBankDAO;
	}

	public void setiAmsCustomerBankDAO(
			IAmsCustomerBankDAO<AmsCustomerBank> iAmsCustomerBankDAO) {
		this.iAmsCustomerBankDAO = iAmsCustomerBankDAO;
	}

	public void setiAmsSysCountryDAO(
			IAmsSysCountryDAO<AmsSysCountry> iAmsSysCountryDAO) {
		this.iAmsSysCountryDAO = iAmsSysCountryDAO;
	}

	/**
	 * @return the iAmsGroupDAO
	 */
	public IAmsGroupDAO<AmsGroup> getiAmsGroupDAO() {
		return iAmsGroupDAO;
	}

	/**
	 * @param iAmsGroupDAO
	 *            the iAmsGroupDAO to set
	 */
	public void setiAmsGroupDAO(IAmsGroupDAO<AmsGroup> iAmsGroupDAO) {
		this.iAmsGroupDAO = iAmsGroupDAO;
	}

	// [NTS1.0-anhndn]Jan 21, 2013A - Start
	public IAmsCustomerDocDAO<AmsCustomerDoc> getiAmsCustomerDocDAO() {
		return iAmsCustomerDocDAO;
	}

	public void setiAmsCustomerDocDAO(
			IAmsCustomerDocDAO<AmsCustomerDoc> iAmsCustomerDocDAO) {
		this.iAmsCustomerDocDAO = iAmsCustomerDocDAO;
	}

	public IAmsSubGroupDAO<AmsSubGroup> getiAmsSubGroupDAO() {
		return iAmsSubGroupDAO;
	}

	public void setiAmsSubGroupDAO(IAmsSubGroupDAO<AmsSubGroup> iAmsSubGroupDAO) {
		this.iAmsSubGroupDAO = iAmsSubGroupDAO;
	}

	// [NTS1.0-anhndn]Jan 21, 2013A - End

	/**
	 * @return the iScCustomerServiceDAO
	 */
	public IScCustomerServiceDAO<ScCustomerService> getiScCustomerServiceDAO() {
		return iScCustomerServiceDAO;
	}

	/**
	 * @param iScCustomerServiceDAO
	 *            the iScCustomerServiceDAO to set
	 */
	public void setiScCustomerServiceDAO(
			IScCustomerServiceDAO<ScCustomerService> iScCustomerServiceDAO) {
		this.iScCustomerServiceDAO = iScCustomerServiceDAO;
	}

	/**
	 * @return the iScBrokerDAO
	 */
	public IScBrokerDAO<ScBroker> getiScBrokerDAO() {
		return iScBrokerDAO;
	}

	/**
	 * @param iScBrokerDAO
	 *            the iScBrokerDAO to set
	 */
	public void setiScBrokerDAO(IScBrokerDAO<ScBroker> iScBrokerDAO) {
		this.iScBrokerDAO = iScBrokerDAO;
	}

	/**
	 * @return the appDateDAO
	 */
	public ISysAppDateDAO<SysAppDate> getAppDateDAO() {
		return appDateDAO;
	}

	/**
	 * @param appDateDAO
	 *            the appDateDAO to set
	 */
	public void setAppDateDAO(ISysAppDateDAO<SysAppDate> appDateDAO) {
		this.appDateDAO = appDateDAO;
	}

	/**
	 * @return the iScCustomerCopyDAO
	 */
	public IScCustomerCopyDAO<ScCustomerCopy> getiScCustomerCopyDAO() {
		return iScCustomerCopyDAO;
	}

	/**
	 * @param iScCustomerCopyDAO
	 *            the iScCustomerCopyDAO to set
	 */
	public void setiScCustomerCopyDAO(
			IScCustomerCopyDAO<ScCustomerCopy> iScCustomerCopyDAO) {
		this.iScCustomerCopyDAO = iScCustomerCopyDAO;
	}

	/**
	 * @return the iScOrderDAO
	 */
	public IScOrderDAO<ScOrder> getiScOrderDAO() {
		return iScOrderDAO;
	}

	/**
	 * @param iScOrderDAO
	 *            the iScOrderDAO to set
	 */
	public void setiScOrderDAO(IScOrderDAO<ScOrder> iScOrderDAO) {
		this.iScOrderDAO = iScOrderDAO;
	}

	public IBoCustomerDAO<BoCustomer> getBoCustomerDAO() {
		return boCustomerDAO;
	}

	public void setBoCustomerDAO(IBoCustomerDAO<BoCustomer> boCustomerDAO) {
		this.boCustomerDAO = boCustomerDAO;
	}

	/**
	 * Add EWallet Account
	 */
	public String addEwallet(CustomerEwalletInfo customerEwalletInfo,Integer eWalletType, List<FileUploadInfo> listFileUploadInfo,
			String wlCode, Integer subGroupId, String publicKey) {
		AmsCustomerEwallet amsCustomerEwallet = new AmsCustomerEwallet();
		// Set information for customerEWallet
		try {
			if (customerEwalletInfo != null) {
//				AmsCustomer amsCustomer = new AmsCustomer();
				String customerId = customerEwalletInfo.getCustomerId();
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				if (amsCustomer == null) {
					log.warn("Can not find CUSTOMER with id = " + customerId);
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
				}
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				amsCustomer.setCustomerId(customerId);
				amsCustomerEwallet.setAmsCustomer(amsCustomer);
				// If type of ewallet is neteller
				if (eWalletType == IConstants.EWALLET_TYPE.NETELLER) {
					amsCustomerEwallet.setEwalletType(IConstants.PAYMENT_METHOD.NETELLER);
					amsCustomerEwallet.setEwalletAccNo(customerEwalletInfo.getEwalletAccNo());
					String encryptedSecureId = Cryptography.encrypt(customerEwalletInfo.getEwalletSecureId(), privateKey, publicKey);
					amsCustomerEwallet.setEwalletSecureId(encryptedSecureId);
					// If type of ewallet is payza
				} else if (eWalletType == IConstants.EWALLET_TYPE.PAYZA) {
					amsCustomerEwallet
							.setEwalletType(IConstants.PAYMENT_METHOD.PAYZA);
					amsCustomerEwallet.setEwalletEmail(customerEwalletInfo.getEwalletEmail());
					String encryptedApiPassword = Cryptography.encrypt(customerEwalletInfo.getEwalletApiPassword(), privateKey, publicKey);
					amsCustomerEwallet.setEwalletApiPassword(encryptedApiPassword);
				} else if (eWalletType == IConstants.EWALLET_TYPE.LIBERTY) {
					// in-case ewallet is liberty
					amsCustomerEwallet.setEwalletType(IConstants.PAYMENT_METHOD.LIBERTY);
					amsCustomerEwallet.setEwalletAccNo(customerEwalletInfo.getEwalletAccNo());
					amsCustomerEwallet.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.NOT_VERIFY);

					// [NTS1.0-anhndn]Jan 22, 2013A - Start : upload
					// verification documents
					List<AmsCustomerDoc> listAmsCustomerDoc = new ArrayList<AmsCustomerDoc>();
					if (listFileUploadInfo != null
							&& !listFileUploadInfo.isEmpty()) {
						Integer uploadDoc = uploadVerifycationDocument(listFileUploadInfo,customerEwalletInfo.getCustomerId(), wlCode,
								subGroupId);
						if (IConstants.UPLOAD_DOCUMENT.FAIL.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
						}
						if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED;
						}
						if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED;
						}
						for (FileUploadInfo f : listFileUploadInfo) {
							AmsCustomerDoc amsCustomerDoc = new AmsCustomerDoc();
							amsCustomerDoc.setCustomerDocId(f.getCustomerDocId());
							listAmsCustomerDoc.add(amsCustomerDoc);
						}
						amsCustomerEwallet.setAmsCustomerDoc1(listAmsCustomerDoc.get(0));
						amsCustomerEwallet.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
					}
					// [NTS1.0-anhndn]Jan 22, 2013A - End
				}
				amsCustomerEwallet.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerEwallet.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerEwallet.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				log.info("CustomerEwallet customer"	+ amsCustomerEwallet.getEwalletAccNo());
				log.info("Ewallet type is "	+ amsCustomerEwallet.getEwalletType());
				log.info("Ewallet ID " + amsCustomerEwallet.getEwalletId());
				log.info("Payment method " + amsCustomerEwallet.getEwalletType());
				log.info("AccountId " + amsCustomerEwallet.getEwalletEmail());
				log.info("Input date " + amsCustomerEwallet.getInputDate());
				// Check existing of this ewallet account
				if (getiAmsCustomerEwalletDAO().isCustomerEwalletExists(amsCustomerEwallet)) {
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
				} else {
					getiAmsCustomerEwalletDAO().save(amsCustomerEwallet);
					//[NTS1.0-Nguyen.Xuan.Bach]Mar 15, 2013A - Start 
					// send mail to CS team
					if (eWalletType == IConstants.EWALLET_TYPE.LIBERTY) {
						sendMailToCsTeam(customerId, amsCustomer.getFullName(), IConstants.UPLOAD_DOCUMENT.DOC_TYPE_DISP.LIBERTY_RESERVE, wlCode);
					}
					//[NTS1.0-Nguyen.Xuan.Bach]Mar 15, 2013A - End
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
	}

    @Override
    public void ensureAvatarCreated(String customerId, String defaultImagePath) {
        try{
            String destPath = propsConfig.getProperty(URL_AVATAR_FOLDER);
            String filePath = destPath + File.separator + customerId + ".jpg";
            if(!new File(filePath).exists()){
                BufferedImage currAvatarImage = ImageIO.read(new File(defaultImagePath));
//                ImageIO.write(currAvatarImage, "jpg", new File(filePath));

                //Save data for temp file
                filePath = destPath + File.separator + customerId + "_temp.jpg";
                if(!new File(filePath).exists()){
                    ImageIO.write(currAvatarImage, "jpg", new File(filePath));
                }
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

	/**
	 * Update EWallet account
	 */
	public String updateEwallet(String customerId,
			CustomerEwalletInfo newCustomerEwalletInfo,
			CustomerEwalletInfo customerEwalletInfo, Integer ewalletType,
			List<FileUploadInfo> listFileUploadInfo, String wlCode,
			Integer subGroupId, String publicKey) {
		AmsCustomerEwallet amsCustomerEwallet = null;
		try {
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key"); 
			AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
			if (ewalletType.equals(IConstants.PAYMENT_METHOD.NETELLER)) {
				String oldEWalletAccountId = customerEwalletInfo.getEwalletAccNo();
				if(!StringUtil.isEmpty(oldEWalletAccountId)){
//					String encryptedOldEWalletAccountId = Cryptography.encrypt(oldEWalletAccountId, privateKey, publicKey);
					amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(customerId, oldEWalletAccountId,ewalletType);
				}
				if (amsCustomerEwallet != null) {
					String eWalletAccountNo = newCustomerEwalletInfo.getEwalletAccNo();
					String eWalletSecure = newCustomerEwalletInfo.getEwalletSecureId();
					if (!StringUtils.isBlank(eWalletAccountNo) && eWalletAccountNo != null	&& !eWalletAccountNo.equals(oldEWalletAccountId)) {
//						amsCustomerEwallet.setEwalletAccNo(Cryptography.encrypt(eWalletAccountNo, privateKey,publicKey));
						amsCustomerEwallet.setEwalletAccNo(eWalletAccountNo);
					}
					if (!StringUtils.isBlank(eWalletSecure)
							&& eWalletSecure != null && !eWalletSecure.equals(customerEwalletInfo.getEwalletSecureId())) {
						amsCustomerEwallet.setEwalletSecureId(Cryptography.encrypt(eWalletSecure, privateKey,publicKey));
					}
					if (!oldEWalletAccountId.equals(eWalletAccountNo)) {
						if (getiAmsCustomerEwalletDAO().isCustomerEwalletExists(amsCustomerEwallet))
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
					}
					getiAmsCustomerEwalletDAO().attachDirty(amsCustomerEwallet);
				}

			} else if (ewalletType.equals(IConstants.PAYMENT_METHOD.PAYZA)) {
				String oldEwalletEmail = customerEwalletInfo.getEwalletEmail();
				amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(customerId, oldEwalletEmail,ewalletType);
				if (amsCustomerEwallet != null) {
					String ewalletEmail = newCustomerEwalletInfo.getEwalletEmail();
					String ewalletPasword = newCustomerEwalletInfo.getEwalletApiPassword();
					if (!StringUtils.isBlank(ewalletEmail)
							&& ewalletEmail != null	&& !ewalletEmail.equals(customerEwalletInfo.getEwalletEmail())) {
						amsCustomerEwallet.setEwalletEmail(ewalletEmail);
					}
					if (!StringUtils.isBlank(ewalletPasword)
							&& ewalletPasword != null
							&& !ewalletPasword.equals(customerEwalletInfo.getEwalletApiPassword())) {
						amsCustomerEwallet.setEwalletApiPassword(Cryptography.encrypt(ewalletPasword,privateKey,publicKey));
					}
					if (getiAmsCustomerEwalletDAO().isCustomerEwalletExists(amsCustomerEwallet))
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
					else
						getiAmsCustomerEwalletDAO().attachDirty(amsCustomerEwallet);
				}
			} else if (ewalletType.equals(IConstants.PAYMENT_METHOD.LIBERTY)) {
				boolean uploadFlag = false;
				// [NTS1.0-Nguyen.Manh.Thang]Oct 26, 2012A - Start
				String oldEWalletAccountId = customerEwalletInfo.getEwalletAccNo();
				amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(customerId, oldEWalletAccountId,ewalletType);
				if (amsCustomerEwallet != null) {
					String eWalletAccountId = newCustomerEwalletInfo.getEwalletAccNo();
					if (amsCustomer == null) {
						log.warn("Can not find CUSTOMER with id = " + customerId);
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
					}
					if (!StringUtils.isBlank(eWalletAccountId)
							&& eWalletAccountId != null
							&& !eWalletAccountId.equals(customerEwalletInfo.getEwalletAccNo())) {
						amsCustomerEwallet.setEwalletAccNo(eWalletAccountId);
//						amsCustomerEwallet.setEwalletAccNo(eWalletAccountId);
					}
					if (!oldEWalletAccountId.equals(eWalletAccountId)) {
						if (getiAmsCustomerEwalletDAO().isCustomerEwalletExists(amsCustomerEwallet))
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
					}

					// [NTS1.0-anhndn]Jan 22, 2013A - Start : upload
					// verification documents
					List<AmsCustomerDoc> listAmsCustomerDoc = new ArrayList<AmsCustomerDoc>();
					if (listFileUploadInfo != null
							&& !listFileUploadInfo.isEmpty()) {
						Integer uploadDoc = uploadVerifycationDocument(listFileUploadInfo,customerEwalletInfo.getCustomerId(), wlCode,subGroupId);
						if (IConstants.UPLOAD_DOCUMENT.FAIL.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
						}
						if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED;
						}
						if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED.equals(uploadDoc)) {
							return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED;
						}
						for (FileUploadInfo f : listFileUploadInfo) {
							AmsCustomerDoc amsCustomerDoc = new AmsCustomerDoc();
							amsCustomerDoc.setCustomerDocId(f.getCustomerDocId());
							listAmsCustomerDoc.add(amsCustomerDoc);
						}
						amsCustomerEwallet.setAmsCustomerDoc1(listAmsCustomerDoc.get(0));
						amsCustomerEwallet.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
						uploadFlag = true;
					}
					// [NTS1.0-anhndn]Jan 22, 2013A - End
					getiAmsCustomerEwalletDAO().attachDirty(amsCustomerEwallet);
					if (uploadFlag) {
						sendMailToCsTeam(customerId, amsCustomer.getFullName(), IConstants.UPLOAD_DOCUMENT.DOC_TYPE_DISP.LIBERTY_RESERVE, wlCode);
					}
				}
				// [NTS1.0-Nguyen.Manh.Thang]Oct 26, 2012A - End
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	/**
	 * Delete EWallet Account
	 */
	public String deleteEwallet(String customerId, String ewalletAccNo,
			Integer ewalletType) {
		AmsCustomerEwallet amsCustomerEwallet = null;
		try {
//			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
//			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, customerId);
//			ewalletAccNo = Cryptography.encrypt(ewalletAccNo, privateKey, publicKey);
			amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(customerId, ewalletAccNo, ewalletType);
			/*
			 * Check existing of ewallet account if exist remove
			 */
			if (amsCustomerEwallet != null) {
				amsCustomerEwallet.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
				getiAmsCustomerEwalletDAO().attachDirty(amsCustomerEwallet);
			} else
				return IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	public String addBankTransfer(AmsCustomerBankInfo.Builder bankTransferInfo) {
		AmsCustomerBank newAmsCustomerBank = new AmsCustomerBank();
		AmsWithdrawalRef amsWithdrawalRef = new AmsWithdrawalRef();
		try {
			log.info("[start] Add BankInfo");
			if (bankTransferInfo != null) {
				AmsSysBank amsSysBank = amsSysBankDAO.getBankByBankCode(bankTransferInfo.getBankCode());
				AmsSysBankBranch amsSysBankBranch = amsSysBankBranchDAO.getBankByBankCode(bankTransferInfo.getBankCode(), bankTransferInfo.getBranchCode());
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, bankTransferInfo.getCustomerId());
				
				if(!StringUtils.isBlank(bankTransferInfo.getCustomerBankId())){
					newAmsCustomerBank.setCustomerBankId(Integer.valueOf(bankTransferInfo.getCustomerBankId()));
					amsWithdrawalRef.setCustomerBankId(Integer.valueOf(bankTransferInfo.getCustomerBankId()));
				}
				
				newAmsCustomerBank.setCustomerId(bankTransferInfo.getCustomerId());
				newAmsCustomerBank.setBankCode(bankTransferInfo.getBankCode());
				newAmsCustomerBank.setBankName(bankTransferInfo.getBankName());
				
				amsWithdrawalRef.setBeneficiaryBankName(bankTransferInfo.getBankName());
				amsWithdrawalRef.setBeneficiaryBankCode(bankTransferInfo.getBankCode());
				
				
				if(amsSysBank != null){
					newAmsCustomerBank.setBankNameKana(amsSysBank.getBankNameKana());
					if(amsSysBank.getAmsSysCountry() != null){
						newAmsCustomerBank.setCountryId(amsSysBank.getAmsSysCountry().getCountryId());
						amsWithdrawalRef.setCountryId(amsSysBank.getAmsSysCountry().getCountryId());
					}
					newAmsCustomerBank.setBankAddress(amsSysBank.getBankAddress());
					newAmsCustomerBank.setSwiftCode(amsSysBank.getSwiftCode());
					
					amsWithdrawalRef.setBeneficiaryBankNameKana(amsSysBank.getBankNameKana());
					amsWithdrawalRef.setBeneficiaryBankAddress(amsSysBank.getBankAddress());
					amsWithdrawalRef.setBeneficiarySwiftCode(amsSysBank.getSwiftCode());
					
				}
				newAmsCustomerBank.setAccountNo(bankTransferInfo.getAccountNo());
				amsWithdrawalRef.setBeneficiaryAccountNo(bankTransferInfo.getAccountNo());
				
				if(amsSysBankBranch != null){
					newAmsCustomerBank.setBranchNameKana(amsSysBankBranch.getBranchNameKana());
					newAmsCustomerBank.setBranchCode(amsSysBankBranch.getId().getBranchCode());
					newAmsCustomerBank.setBranchName(amsSysBankBranch.getBranchName());
					
					amsWithdrawalRef.setBeneficiaryBranchNameKana(amsSysBankBranch.getBranchNameKana());
					amsWithdrawalRef.setBeneficiaryBranchCode(amsSysBankBranch.getId().getBranchCode());
					amsWithdrawalRef.setBeneficiaryBranchName(amsSysBankBranch.getBranchName());
				}else{
					return "ERR_MSG_BRANCH_NOT_EXISTS";
				}
				
				if(amsCustomer != null){
					if(amsCustomer.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
						String firstName = amsCustomer.getFirstName() != null ? amsCustomer.getFirstName() : "";
						String lastName = amsCustomer.getLastName() != null ? amsCustomer.getLastName() : "";
						String firstNameKana = amsCustomer.getFirstNameKana() != null ? amsCustomer.getFirstNameKana() : "";
						String lastNameKana = amsCustomer.getLastNameKana() != null ? amsCustomer.getLastNameKana() : "";
						newAmsCustomerBank.setAccountName(firstName + " " + lastName);
						newAmsCustomerBank.setAccountNameKana(firstNameKana + " " + lastNameKana);
					}else{
						newAmsCustomerBank.setAccountName(amsCustomer.getCorpFullname());
						newAmsCustomerBank.setAccountNameKana(amsCustomer.getCorpFullnameKana());
					}
				}
				if(!StringUtil.isEmpty(bankTransferInfo.getBankAccClass())){
					newAmsCustomerBank.setBankAccClass(Integer.valueOf(bankTransferInfo.getBankAccClass()));
					amsWithdrawalRef.setBeneficiaryBankAccClass(Integer.valueOf(bankTransferInfo.getBankAccClass()));
				}
				newAmsCustomerBank.setInputDate(new Timestamp(System.currentTimeMillis()));
				newAmsCustomerBank.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				newAmsCustomerBank.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				
				amsWithdrawalRef.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				log.info("Bank Name " + newAmsCustomerBank.getBankName());
				log.info("Bank Address " + newAmsCustomerBank.getBankAddress());
				log.info("Account Number " + newAmsCustomerBank.getAccountNo());
				log.info("Account Name  " + newAmsCustomerBank.getAccountName());
				log.info("SwiftCode  " + newAmsCustomerBank.getSwiftCode());
				log.info("Branch Name " + newAmsCustomerBank.getBranchName());
				log.info("[End] Add BankInfo");
				// Check existing of this credit account
				AmsCustomerBank currentAmsCustomerBank =  getiAmsCustomerBankDAO().getBankInfo(String.valueOf(newAmsCustomerBank.getCustomerBankId()));

				if (currentAmsCustomerBank != null){

					if(!StringUtils.isBlank(bankTransferInfo.getCustomerBankId())){
						AmsCustomerBank currentAmsCustomerBankClone = (AmsCustomerBank) ObjectCopy.copy(currentAmsCustomerBank);
						getiAmsCustomerBankDAO().merge(newAmsCustomerBank);
						updateTraceWhenUpdateBankInfo(amsCustomer, currentAmsCustomerBankClone, newAmsCustomerBank);
						getiAmsWithdrawalRefDAO().updateAmsWithdrawalRef(amsWithdrawalRef);
					}

					log.info("Update BankInfo SUCCESS " + newAmsCustomerBank.toString());
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;

				} else {					
					if (getiAmsCustomerBankDAO().isCustomerBankExisted(bankTransferInfo.getCustomerId(), bankTransferInfo.getAccountNo())) {
						return "MSG_NAB011";
					}
					getiAmsCustomerBankDAO().save(newAmsCustomerBank);
					updateTraceWhenInsertBankInfo(amsCustomer, newAmsCustomerBank);
					log.info("Insert BankInfo SUCCESS " + newAmsCustomerBank.toString());
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		log.info("InsertOrUpdate BankInfo FAIL " + newAmsCustomerBank.toString());
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
	}
	
	public String deleteBankTransfer(String customerBankId) {
		try {
			AmsCustomerBank amsCustomerBank = getiAmsCustomerBankDAO().getBankInfo(customerBankId);
			if (amsCustomerBank != null) {
				amsCustomerBank.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
				getiAmsCustomerBankDAO().merge(amsCustomerBank);
				updateTraceWhenDeleteBankInfo(amsCustomerBank);
			} else {
				return IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	private void updateTraceWhenInsertBankInfo(AmsCustomer amsCustomer, AmsCustomerBank newAmsCustomerBank) {

		log.info("[Start] Update trace when insert bank for customerId [" + amsCustomer.getCustomerId() + "]");
		AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
		amsCustomerTrace.setServiceType(ITrsConstants.SERVICES_TYPE.AMS);
		amsCustomerTrace.setAmsCustomer(amsCustomer);
		amsCustomerTrace.setReason("Change Payment Information");
		amsCustomerTrace.setNote1("");
		amsCustomerTrace.setNote2("");
		amsCustomerTrace.setValue1("");
		amsCustomerTrace.setValue2("Bank Name: " + newAmsCustomerBank.getBankName() + ", Branch Name: " + newAmsCustomerBank.getBranchName());
		amsCustomerTrace.setActiveFlg(ITrsConstants.ACTIVE_FLG.ACTIVE);
		amsCustomerTrace.setSysOperation(null);
		amsCustomerTrace.setOperationFullname("");
		amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
		getiAmsCustomerTraceDAO().save(amsCustomerTrace);
		log.info("[End] Update trace when insert bank for customerId [" + amsCustomer.getCustomerId() + "]");

	}

	private void updateTraceWhenUpdateBankInfo(AmsCustomer amsCustomer, AmsCustomerBank currentAmsCustomerBank, AmsCustomerBank newAmsCustomerBank) {

		boolean isChangeBankName = !currentAmsCustomerBank.getBankName().equals(newAmsCustomerBank.getBankName());
		boolean isChangeBranchName = !currentAmsCustomerBank.getBranchName().equals(newAmsCustomerBank.getBranchName());
		boolean needUpdateTrace =  isChangeBankName || isChangeBranchName;
		
		if (needUpdateTrace) {
			log.info("[Start] Update trace when update bank for customerId [" + amsCustomer.getCustomerId() + "]");
			AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
			amsCustomerTrace.setServiceType(ITrsConstants.SERVICES_TYPE.AMS);
			amsCustomerTrace.setAmsCustomer(amsCustomer);
			amsCustomerTrace.setReason("Change Payment Information");
			amsCustomerTrace.setNote1("");
			amsCustomerTrace.setNote2("");
			amsCustomerTrace.setValue1("Bank Name: " + currentAmsCustomerBank.getBankName() + ", Branch Name: " + currentAmsCustomerBank.getBranchName());
			amsCustomerTrace.setValue2("Bank Name: " + newAmsCustomerBank.getBankName() + ", Branch Name: " + newAmsCustomerBank.getBranchName());
			amsCustomerTrace.setActiveFlg(ITrsConstants.ACTIVE_FLG.ACTIVE);
			amsCustomerTrace.setSysOperation(null);
			amsCustomerTrace.setOperationFullname("");
			amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
			getiAmsCustomerTraceDAO().save(amsCustomerTrace);
			log.info("[End] Update trace when update bank for customerId [" + amsCustomer.getCustomerId() + "]");
		} else {
			log.info("Don't need update AmsCustomerTrace when update for customerId [" + amsCustomer.getCustomerId() + "]");
		}		

	}

	private void updateTraceWhenDeleteBankInfo(AmsCustomerBank amsCustomerBank) {
		
		log.info("[Start] Update trace when delete bank for customerId [" + amsCustomerBank.getCustomerId() + "]");
		AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
		amsCustomerTrace.setServiceType(ITrsConstants.SERVICES_TYPE.AMS);
		
		AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, amsCustomerBank.getCustomerId());
		amsCustomerTrace.setAmsCustomer(amsCustomer);
		amsCustomerTrace.setReason("Change Payment Information");
		amsCustomerTrace.setNote1("");
		amsCustomerTrace.setNote2("");
		amsCustomerTrace.setValue1("Bank Name: " + amsCustomerBank.getBankName() + ", Branch Name: " + amsCustomerBank.getBranchName());
		amsCustomerTrace.setValue2("");
		amsCustomerTrace.setActiveFlg(ITrsConstants.ACTIVE_FLG.ACTIVE);
		amsCustomerTrace.setSysOperation(null);
		amsCustomerTrace.setOperationFullname("");
		amsCustomerTrace.setChangeTime(new Timestamp(System.currentTimeMillis()));
		getiAmsCustomerTraceDAO().save(amsCustomerTrace);
		log.info("[End] Update trace when delete bank for customerId [" + amsCustomerBank.getCustomerId() + "]");
		
	}

	public String updateBankTransfer(BankTransferInfo newBankTransferInfo, BankTransferInfo oldBankTransferInfo) {
		try {
			String oldAccNo = oldBankTransferInfo.getAccountNumber();
			String customerId = newBankTransferInfo.getCustomerId();
			String newBankCode = newBankTransferInfo.getBankCode();
			String newBranchCode = newBankTransferInfo.getBranchCode();
			String newAccountNo = newBankTransferInfo.getAccountNumber();
//			if (!oldAccNo.equals(newAccountNo) && getiAmsCustomerBankDAO().isCustomerBankExisted(customerId, newAccountNo, newBankCode)){
//				return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
//			}
			
			AmsCustomerBank amsCustomerBank = null;
			if (amsCustomerBank != null) {
				String bankName = newBankTransferInfo.getBankName();
				String branch = newBankTransferInfo.getBranchName();
				String branchCode = newBankTransferInfo.getBranchCode();
				String bankCode = newBankTransferInfo.getBankCode();
				amsCustomerBank.setAccountNo(newAccountNo);
//				String benificiary = oldBankTransferInfo.getBeneficiaryName();
				Integer accountType = newBankTransferInfo.getBankAccClass();
				amsCustomerBank.setBankName(bankName);
				
				AmsSysBank newAmsSysBank = amsSysBankDAO.getBankByBankCode(newBankCode);
				if(newAmsSysBank != null){
					amsCustomerBank.setBankNameKana(newAmsSysBank.getBankNameKana());
					if(newAmsSysBank.getAmsSysCountry() != null){
						amsCustomerBank.setCountryId(newAmsSysBank.getAmsSysCountry().getCountryId());
					}
					amsCustomerBank.setBankAddress(newAmsSysBank.getBankAddress());
					amsCustomerBank.setSwiftCode(newAmsSysBank.getSwiftCode());
				}
				
				amsCustomerBank.setBranchName(branch);
				AmsSysBankBranch newAmsSysBankBranch = amsSysBankBranchDAO.getBankByBankCode(newBankCode, newBranchCode);
				if(newAmsSysBankBranch != null){
					amsCustomerBank.setBranchNameKana(newAmsSysBankBranch.getBranchNameKana());
				}
				amsCustomerBank.setBranchCode(branchCode);
				amsCustomerBank.setBankCode(bankCode);
//				amsCustomerBank.setAccountName(benificiary);
				
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				if(amsCustomer != null){
					if(amsCustomer.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
						String firstName = amsCustomer.getFirstNameKana() != null ? amsCustomer.getFirstNameKana() : "";
						String lastName = amsCustomer.getLastNameKana() != null ? amsCustomer.getLastNameKana() : "";
						amsCustomerBank.setAccountNameKana(firstName + " " + lastName);
						amsCustomerBank.setAccountName(amsCustomer.getFullName());
					}else{
						amsCustomerBank.setAccountNameKana(amsCustomer.getCorpFullnameKana());
						amsCustomerBank.setAccountName(amsCustomer.getCorpFullname());
					}
				}
				amsCustomerBank.setBankAccClass(accountType);
				amsCustomerBank.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				getiAmsCustomerBankDAO().merge(amsCustomerBank);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	public String addCreditCard(CreditCardInfo creditCardInfo,
			List<FileUploadInfo> listFileUploadInfo, String wlCode,
			Integer subGroupId, String publicKey) {
		AmsCustomerCreditcard amsCustomerCreditcard = new AmsCustomerCreditcard();
		String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
		try {
			if (creditCardInfo != null) {
				String customerId = creditCardInfo.getCustomerId();
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				if (amsCustomer == null) {
					log.warn("Can not find CUSTOMER with id = " + customerId);
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
				}
				AmsSysCountry amsSysCountry = new AmsSysCountry();
				amsCustomer.setCustomerId(creditCardInfo.getCustomerId());
				Integer countryId = creditCardInfo.getCountryId();
				// if (countryId.equals(arg0))
				amsSysCountry.setCountryId(countryId);
				amsCustomerCreditcard.setAmsCustomer(amsCustomer);
				amsCustomerCreditcard.setCcNo(Cryptography.encrypt(creditCardInfo.getCcNo(),privateKey,publicKey));
				amsCustomerCreditcard.setExpiredDate(creditCardInfo.getExpiredDate().replaceAll("/", ""));
				amsCustomerCreditcard.setCcCvv(Cryptography.encrypt(creditCardInfo.getCcCvv(),privateKey,publicKey));
				if (!countryId.equals(-1)) {
					amsCustomerCreditcard.setAmsSysCountry(amsSysCountry);
				}
				amsCustomerCreditcard.setCcFirstName(creditCardInfo.getCcFirstName());
				amsCustomerCreditcard.setCcLastName(creditCardInfo.getCcLastName());
				amsCustomerCreditcard.setCcHolderName(creditCardInfo.getCcFirstName() + " "	+ creditCardInfo.getCcLastName());
				amsCustomerCreditcard.setCcType(creditCardInfo.getCcType());
				amsCustomerCreditcard.setZipCode(creditCardInfo.getZipCode());
				amsCustomerCreditcard.setCity(creditCardInfo.getCity());
				amsCustomerCreditcard.setAddress(creditCardInfo.getAddress());
				amsCustomerCreditcard.setState(creditCardInfo.getState());
				amsCustomerCreditcard.setEmail(creditCardInfo.getEmail());
				amsCustomerCreditcard.setPhone(creditCardInfo.getPhone());
				amsCustomerCreditcard.setCcDriverNo(creditCardInfo.getCcDriverNo());
				amsCustomerCreditcard.setInputDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerCreditcard.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				amsCustomerCreditcard.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerCreditcard.setCcNoLastDigit(creditCardInfo.getCcNoLastDigit());
				// [NTS1.0-anhndn]Jan 21, 2013A - Start
				amsCustomerCreditcard.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.NOT_VERIFY);
				// [NTS1.0-anhndn]Jan 21, 2013A - End

				log.info("Credit card type is "
						+ amsCustomerCreditcard.getCcType());
				log.info("Credit ID " + amsCustomerCreditcard.getCcNo());
				log.info("Holder Name  "
						+ amsCustomerCreditcard.getCcHolderName());
				log.info("Input date  " + amsCustomerCreditcard.getInputDate());

				// [NTS1.0-anhndn]Jan 22, 2013A - Start : upload verification
				// documents
				List<AmsCustomerDoc> listAmsCustomerDoc = new ArrayList<AmsCustomerDoc>();
				if (listFileUploadInfo != null && !listFileUploadInfo.isEmpty()) {
					Integer uploadDoc = uploadVerifycationDocument(
							listFileUploadInfo, creditCardInfo.getCustomerId(),
							wlCode, subGroupId);
					if (IConstants.UPLOAD_DOCUMENT.FAIL.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
					}
					if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED
							.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED;
					}
					if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED
							.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED;
					}
					for (FileUploadInfo f : listFileUploadInfo) {
						AmsCustomerDoc amsCustomerDoc = new AmsCustomerDoc();
						amsCustomerDoc.setCustomerDocId(f.getCustomerDocId());
						listAmsCustomerDoc.add(amsCustomerDoc);
					}
					int size = listAmsCustomerDoc.size();
					amsCustomerCreditcard.setAmsCustomerDoc1(listAmsCustomerDoc
							.get(0));
					if (size > 1) {
						amsCustomerCreditcard
								.setAmsCustomerDoc2(listAmsCustomerDoc.get(1));
					}
//					if (size > 2) {
//						amsCustomerCreditcard
//								.setAmsCustomerDoc3(listAmsCustomerDoc.get(2));
//					}
					amsCustomerCreditcard
							.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
				}
				// [NTS1.0-anhndn]Jan 22, 2013A - End

				// Check existing of this credit account
				if (getiAmsCustomerCreditcardDAO().isCustomerCreditExists(amsCustomerCreditcard)) {
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.EXISTED;
				} else {
					getiAmsCustomerCreditcardDAO().save(amsCustomerCreditcard);
					// send mail to cs team
					sendMailToCsTeam(customerId, amsCustomer.getFullName(), IConstants.UPLOAD_DOCUMENT.DOC_TYPE_DISP.CREDIT_CARD, wlCode);
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
	}

	public String updateCreditCard(CreditCardInfo newCreditCardInfo,
			List<FileUploadInfo> listFileUploadInfo, String wlCode,
			Integer subGroupId, String publicKey) {
		boolean uploadFlag = false;
		try {
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			AmsCustomerCreditcard amsCustomerCreditcard = getiAmsCustomerCreditcardDAO().getCreditInfobyId(newCreditCardInfo.getCustomerCcId());
			if (amsCustomerCreditcard != null) {
				AmsCustomer amsCustomer = amsCustomerCreditcard.getAmsCustomer();
				String customerId = null;
				if (amsCustomer != null) {
					customerId = amsCustomer.getCustomerId();
					amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				}
				if (amsCustomer == null) {
					log.warn("Can not find CUSTOMER with id = " + customerId);
					return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
				}
				Integer cardType = newCreditCardInfo.getCcType();
				// [NTS1.0-anhndn]Jan 22, 2013A - Start
//				String ccNo = newCreditCardInfo.getCcNo();
				String expiredDate = newCreditCardInfo.getExpiredDate();
				String ccCvv = newCreditCardInfo.getCcCvv();
				// [NTS1.0-anhndn]Jan 22, 2013A - End
				Integer countryId = newCreditCardInfo.getCountryId();
				String firstName = newCreditCardInfo.getCcFirstName();
				String lastName = newCreditCardInfo.getCcLastName();
				String cardHolderName = firstName + " " + lastName;
				String zipcode = newCreditCardInfo.getZipCode();
				String city = newCreditCardInfo.getCity();
				String state = newCreditCardInfo.getState();
				String email = newCreditCardInfo.getEmail();
				String phone = newCreditCardInfo.getPhone();
				String personalNum = newCreditCardInfo.getCcDriverNo();
				String address = newCreditCardInfo.getAddress();
				AmsSysCountry amsSysCountry = new AmsSysCountry();
				if (cardType != null
						&& !cardType.equals(amsCustomerCreditcard.getCcType())) {
					amsCustomerCreditcard.setCcType(cardType);
				}
				// [NTS1.0-anhndn]Jan 22, 2013A - Start
//				if (ccNo != null && !ccNo.equals(amsCustomerCreditcard.getCcNo())) {
//					amsCustomerCreditcard.setCcNo(Cryptography.encrypt(ccNo, privateKey, publicKey));
//				}
				if (expiredDate != null	&& !expiredDate.equals(amsCustomerCreditcard.getExpiredDate())) {
					amsCustomerCreditcard.setExpiredDate(expiredDate);
				}
				if (ccCvv != null
						&& !ccCvv.equals(amsCustomerCreditcard.getCcCvv())) {
					amsCustomerCreditcard.setCcCvv(Cryptography.encrypt(ccCvv, privateKey, publicKey));
				}
				// [NTS1.0-anhndn]Jan 22, 2013A - End
				if (address != null
						&& !address.equals(amsCustomerCreditcard.getAddress())) {
					amsCustomerCreditcard.setAddress(address);
				}
				if (countryId != null && !countryId.equals(-1)) {
					AmsSysCountry oldAmsSysCountry = amsCustomerCreditcard
							.getAmsSysCountry();
					Integer oldCountryId = null;
					if (oldAmsSysCountry != null) {
						oldCountryId = oldAmsSysCountry.getCountryId();
					}
					if (!countryId.equals(oldCountryId)) {
						amsSysCountry.setCountryId(countryId);
						amsCustomerCreditcard.setAmsSysCountry(amsSysCountry);
					}
				}
				if (firstName != null
						&& !firstName.equals(amsCustomerCreditcard
								.getCcFirstName())) {
					amsCustomerCreditcard.setCcFirstName(firstName);
				}
				if (lastName != null
						&& !lastName.equals(amsCustomerCreditcard
								.getCcLastName())) {
					amsCustomerCreditcard.setCcLastName(lastName);
				}
				if (!StringUtils.isBlank(cardHolderName)
						&& cardHolderName != null
						&& !cardHolderName.equals(amsCustomerCreditcard
								.getCcHolderName())) {
					amsCustomerCreditcard.setCcHolderName(cardHolderName);
				}
				if (!StringUtils.isBlank(zipcode) && zipcode != null
						&& !zipcode.equals(amsCustomerCreditcard.getZipCode())) {
					amsCustomerCreditcard.setZipCode(zipcode);
				}
				if (!StringUtils.isBlank(city) && city != null
						&& !city.equals(amsCustomerCreditcard.getCity())) {
					amsCustomerCreditcard.setCity(city);
				}
				if (!StringUtils.isBlank(state) && state != null
						&& !state.equals(amsCustomerCreditcard.getState())) {
					amsCustomerCreditcard.setState(state);
				}
				if (!StringUtils.isBlank(email) && email != null
						&& !email.equals(amsCustomerCreditcard.getEmail())) {
					amsCustomerCreditcard.setEmail(email);
				}
				if (!StringUtils.isBlank(phone) && phone != null
						&& !phone.equals(amsCustomerCreditcard.getPhone())) {
					amsCustomerCreditcard.setPhone(phone);
				}
				if (!StringUtils.isBlank(personalNum) && personalNum != null && !personalNum.equals(amsCustomerCreditcard.getCcDriverNo())) {
					amsCustomerCreditcard.setCcDriverNo(personalNum);
				}
				// [NTS1.0-anhndn]Jan 22, 2013A - Start : upload verification
				// documents
				List<AmsCustomerDoc> listAmsCustomerDoc = new ArrayList<AmsCustomerDoc>();
				if (listFileUploadInfo != null && !listFileUploadInfo.isEmpty()) {
					Integer uploadDoc = uploadVerifycationDocument(listFileUploadInfo, amsCustomerCreditcard.getAmsCustomer().getCustomerId(), wlCode,
							subGroupId);
					if (IConstants.UPLOAD_DOCUMENT.FAIL.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
					}
					if (IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED
							.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_NOT_ALLOWED;
					}
					if (IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED
							.equals(uploadDoc)) {
						return IConstants.PROCESS_PAYMENT_METHOD_STATUS.DOC_UPLOAD_STATUS.FILE_SIZE_EXCEEDED;
					}
					for (FileUploadInfo f : listFileUploadInfo) {
						AmsCustomerDoc amsCustomerDoc = new AmsCustomerDoc();
						amsCustomerDoc.setCustomerDocId(f.getCustomerDocId());
						listAmsCustomerDoc.add(amsCustomerDoc);
					}
					int size = listAmsCustomerDoc.size();
					amsCustomerCreditcard.setAmsCustomerDoc1(listAmsCustomerDoc
							.get(0));
					if (size > 1) {
						amsCustomerCreditcard.setAmsCustomerDoc2(listAmsCustomerDoc.get(1));
					}
					amsCustomerCreditcard.setDocVerifyStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
					uploadFlag = true;
				}
				// [NTS1.0-anhndn]Jan 22, 2013A - End
				getiAmsCustomerCreditcardDAO().attachDirty(amsCustomerCreditcard);
				if (uploadFlag) {
					sendMailToCsTeam(customerId, amsCustomer.getFullName(), IConstants.UPLOAD_DOCUMENT.DOC_TYPE_DISP.CREDIT_CARD, wlCode);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	/**
	 * Delete creditCard
	 */
	public String deleteCreditcard(Integer creditId) {
		AmsCustomerCreditcard amsCustomerCreditcard = null;
		try {
			amsCustomerCreditcard = getiAmsCustomerCreditcardDAO().getCreditInfobyId(creditId);
			if (amsCustomerCreditcard != null) {
				amsCustomerCreditcard.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
				getiAmsCustomerCreditcardDAO().attachDirty(
						amsCustomerCreditcard);
			} else
				return IConstants.PROCESS_PAYMENT_METHOD_STATUS.NOT_EXIST;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.PROCESS_PAYMENT_METHOD_STATUS.FAIL;
		}
		return IConstants.PROCESS_PAYMENT_METHOD_STATUS.SUCCESS;
	}

	/**
	 * upload verification documents
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 21, 2013
	 */
	private Integer uploadVerifycationDocument(
			List<FileUploadInfo> listFileUploadInfo, String customerId,
			String wlCode, Integer subGroupId) {

		// validate file
		Integer validate = FileLoaderUtil.validateFiles(listFileUploadInfo);
		if (IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.EXTENSION_NOT_ALLOWED
				.equals(validate)) {
			return IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED;
		}
		if (IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.SIZE_LIMIT_EXCEEDED
				.equals(validate)) {
			return IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED;
		}

		FileLoaderUtil.renameFileUpload(listFileUploadInfo);

		for (FileUploadInfo f : listFileUploadInfo) {
			Integer uploadResult = uploadDocument(f);
			if (uploadResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}
		}

		String destPath = getDestinationFolderUpload(customerId, wlCode,
				subGroupId);
		Integer insertDocResult = insertAmsCustomerDoc(listFileUploadInfo,
				destPath);
		if (insertDocResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
			return insertDocResult;
		}

		for (FileUploadInfo f : listFileUploadInfo) {
			Integer copyResult = FileLoaderUtil.copyFile(f, destPath);
			if (copyResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}
		}

		return IConstants.UPLOAD_DOCUMENT.SUCCESS;
	}

	/**
	 * upload verification documents for liberty reverse　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 21, 2013
	 */
	public Integer uploadLibertyDocument(
			List<FileUploadInfo> listFileUploadInfo, String customerId,
			String ewalletAccountId, String wlCode, Integer subGroupId) {

		String destFolder = getDestinationFolderUpload(customerId, wlCode,
				subGroupId);

		for (FileUploadInfo f : listFileUploadInfo) {
			Integer uploadResult = uploadDocument(f);
			if (uploadResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}
		}

		Integer insertDocResult = insertAmsCustomerDoc(listFileUploadInfo,
				destFolder);
		if (insertDocResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
			return insertDocResult;
		}
		Integer updateCustomerLiberty = updateCustomerLibertyDoc(
				listFileUploadInfo, customerId, ewalletAccountId);
		if (updateCustomerLiberty.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
			return updateCustomerLiberty;
		}
		return IConstants.UPLOAD_DOCUMENT.SUCCESS;
	}

	/**
	 * get destination folder for upload documents　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 21, 2013
	 */
	private String getDestinationFolderUpload(String customerId, String wlCode,
			Integer subGroupId) {
		AmsWhitelabelConfigId id = new AmsWhitelabelConfigId(
				IConstants.WHITE_LABEL_CONFIG.ATTACH_FILE_PATH, wlCode);
		String originalPath = MasterDataManagerImpl.getWhitelabelConfigDAO()
				.findById(AmsWhitelabelConfig.class, id).getConfigValue();
		// String subGroupCode =
		// getiAmsSubGroupDAO().findById(AmsSubGroup.class,
		// subGroupId).getSubGroupCode();
		StringBuffer destinationFolder = new StringBuffer(originalPath);
		// destinationFolder.append("/").append(subGroupCode).append("/").append(customerId);
		// destinationFolder.append("/");
		log.info("FILE UPLOADED ADDR: " + destinationFolder.toString());
		return destinationFolder.toString();
	}

	/**
	 * upload document　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 21, 2013
	 */
	private Integer uploadDocument(FileUploadInfo fileUpload) {
		log.info("uploadDocument");
		try {
			StringBuffer rootPath = new StringBuffer(fileUpload.getRootPath());
			String path = rootPath.append(
					IConstants.UPLOAD_DOCUMENT.UPLOAD_TEMP_FOLDER).toString();
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File destFile = null;
			String fileName = fileUpload.getFileName();
			destFile = new File(dir, fileName);
			/*
			 * if (!destFile.exists()) { destFile.createNewFile(); }
			 */
			log.info("TOMCAT TEMP PATH DIR: " + dir.getPath());
			log.info("TOMCAT TEMP FILE: " + destFile.getPath());
			FileUtils.copyFile(fileUpload.getFile(), destFile);
			fileUpload.setFile(destFile);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		return IConstants.UPLOAD_DOCUMENT.SUCCESS;
	}

	/**
	 * insert one record to AmsCustomerDoc　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 21, 2013
	 */
	private Integer insertAmsCustomerDoc(
			List<FileUploadInfo> listFileUploadInfo, String destFolder) {
		try {
			AmsCustomerDoc amsCustomerDoc = null;
			AmsCustomer amsCustomer = null;
			for (FileUploadInfo f : listFileUploadInfo) {
				amsCustomerDoc = new AmsCustomerDoc();
				amsCustomer = new AmsCustomer();
				amsCustomer.setCustomerId(f.getCustomerId());
				amsCustomerDoc.setAmsCustomer(amsCustomer);
				amsCustomerDoc.setDocType(f.getDocType());
				amsCustomerDoc.setDocKind(f.getDocKind());
				amsCustomerDoc.setDocUrl(destFolder + "/" + f.getFileName());
				amsCustomerDoc.setDocFileName(f.getFileName());
				amsCustomerDoc.setDocFileType(f.getDocFileType());
				amsCustomerDoc.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerDoc.setUploadDateTime(new Timestamp(System
						.currentTimeMillis()));
				amsCustomerDoc.setInputDate(new Timestamp(System
						.currentTimeMillis()));
				amsCustomerDoc.setUpdateDate(new Timestamp(System
						.currentTimeMillis()));
				getiAmsCustomerDocDAO().save(amsCustomerDoc);
				f.setCustomerDocId(amsCustomerDoc.getCustomerDocId());
			}
			return IConstants.UPLOAD_DOCUMENT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
	}

	/**
	 * update AmsCustomerEWallet when document uploaded　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Jan 22, 2013
	 */
	private Integer updateCustomerLibertyDoc(
			List<FileUploadInfo> listFileUploadInfo, String customerId,
			String ewalletAccountId) {
		AmsCustomerEwallet amsCustomerEwallet = null;
		try {
			List<AmsCustomerDoc> listAmsCustomerDoc = new ArrayList<AmsCustomerDoc>();
			AmsCustomerDoc amsCustomerDoc = null;
			for (FileUploadInfo f : listFileUploadInfo) {
				amsCustomerDoc = new AmsCustomerDoc();
				amsCustomerDoc.setCustomerDocId(f.getCustomerDocId());
				listAmsCustomerDoc.add(amsCustomerDoc);
			}
			Integer ewalletType = IConstants.EWALLET_TYPE.LIBERTY;
			amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(
					customerId, ewalletAccountId, ewalletType);
			if (amsCustomerEwallet != null) {
				amsCustomerEwallet
						.setAmsCustomerDoc1(listAmsCustomerDoc.get(0));
				getiAmsCustomerEwalletDAO().attachDirty(amsCustomerEwallet);
				return IConstants.UPLOAD_DOCUMENT.SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		return IConstants.UPLOAD_DOCUMENT.FAIL;
	}

	/**
	 * sendMailToCsTeam　about upload document
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 15, 2013
	 */
	private void sendMailToCsTeam(String loginId, String fullName, String remark, String wlCode) {
		log.info("[start] send mail to CS about upload document successful loginId=" + loginId);
		try{
			Map<String, String> mapListMail = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CS_TEAM_MAIL);
			String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_NOTIFY_DOCS).append("_").append(IConstants.Language.ENGLISH).toString();
			AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
	
			amsMailTemplateInfo.setLoginId(loginId);
			amsMailTemplateInfo.setFullName(fullName);
			amsMailTemplateInfo.setRemark(remark);
	
			HashMap<String, String> to = new HashMap<String, String>();
			for (String id : mapListMail.keySet()) {
				to.put(mapListMail.get(id), mapListMail.get(id));
			}
	
			amsMailTemplateInfo.setTo(to);
			amsMailTemplateInfo.setWlCode(wlCode);
			amsMailTemplateInfo.setMailCode(mailCode);
			amsMailTemplateInfo.setSubject(mailCode);	
			jmsContextSender.sendMail(amsMailTemplateInfo, false);
		}catch (Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		log.info("[end] send mail to CS about upload document successful loginId=" + loginId);
	}
	
	/**
	 * Get ewallet list
	 * 
	 * @param customerId
	 * @param ewalletType
	 * @return
	 */
	public List<CustomerEwalletInfo> getEwalletList(String customerId,Integer paymentType, String publicKey) {
		List<AmsCustomerEwallet> listAmsCustomerEwallet = null;
		ArrayList<CustomerEwalletInfo> listCustomerEwalletInfos = null;
		try {
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
			if(amsCustomer != null){
				if (paymentType == IConstants.EWALLET_TYPE.NETELLER) {
					listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId,IConstants.EWALLET_TYPE.NETELLER);
				} else if (paymentType == IConstants.EWALLET_TYPE.PAYZA) {
					listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId,IConstants.EWALLET_TYPE.PAYZA);
				} else if (paymentType == IConstants.EWALLET_TYPE.LIBERTY) {
					// [NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - Start
					listAmsCustomerEwallet = getiAmsCustomerEwalletDAO().getHistoryEwallet(customerId,IConstants.EWALLET_TYPE.LIBERTY);
					// [NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - End
				}
	
				if (listAmsCustomerEwallet != null
						&& listAmsCustomerEwallet.size() > 0) {
					listCustomerEwalletInfos = new ArrayList<CustomerEwalletInfo>();
					for (AmsCustomerEwallet amsEwallet : listAmsCustomerEwallet) {
						CustomerEwalletInfo customerEwalletInfo = new CustomerEwalletInfo();
						if (paymentType == IConstants.EWALLET_TYPE.NETELLER) {
							customerEwalletInfo.setEwalletAccNo(amsEwallet.getEwalletAccNo());
							if(!StringUtil.isEmpty(amsEwallet.getEwalletSecureId())){
								customerEwalletInfo.setEwalletSecureId(Cryptography.decrypt(amsEwallet.getEwalletSecureId(),privateKey,publicKey));
							}
						} else if (paymentType == IConstants.EWALLET_TYPE.PAYZA) {
							customerEwalletInfo.setEwalletEmail(amsEwallet.getEwalletEmail());
							if(!StringUtil.isEmpty(amsEwallet.getEwalletApiPassword())){
								customerEwalletInfo.setEwalletApiPassword(Cryptography.decrypt(amsEwallet.getEwalletApiPassword(),privateKey,publicKey));
							}
						} else if (paymentType == IConstants.EWALLET_TYPE.LIBERTY) {
							// [NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - Start
							customerEwalletInfo.setEwalletAccNo(amsEwallet.getEwalletAccNo());
							customerEwalletInfo.setEwalletApiName(amsEwallet.getEwalletApiName());
							if(!StringUtil.isEmpty(amsEwallet.getEwalletSecureWord())){
								customerEwalletInfo.setEwalletSecureWord(Cryptography.decrypt(amsEwallet.getEwalletSecureWord(),privateKey,publicKey));
							}
							customerEwalletInfo.setDocVerifyStatus(amsEwallet.getDocVerifyStatus());
							// [NTS1.0-Nguyen.Manh.Thang]Oct 25, 2012A - End
						}
						customerEwalletInfo.setEwalletId(amsEwallet.getEwalletId());
						listCustomerEwalletInfos.add(customerEwalletInfo);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listCustomerEwalletInfos;
	}

	/**
	 * Get Customer Information by loginId
	 * 
	 * @param loginId
	 * @return
	 */
	public CustomerInfo getCustomerInfoByLoginId(String loginId) {
		log.info("getCustomerInfoByLoginId, loginId: " + loginId);
		
		CustomerInfo customerInfo = null;
		try {
			AmsCustomer amsCustomer = null;
			amsCustomer = getiAmsCustomerDAO().getAmsCustomer(loginId);
			if(amsCustomer == null)
				return null;
			
			customerInfo = getCustomerInfo(amsCustomer);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerInfo;
	}
	
	public CustomerInfo getCustomerInfoByNtdCustomerId(String ntdCustomerId) {
		CustomerInfo customerInfo = null;
		try {
			AmsCustomer amsCustomer = null;
			amsCustomer = getiAmsCustomerDAO().getAmsCustomerByNtdCustomerId(ntdCustomerId);
			if(amsCustomer == null)
				return null;
			
			customerInfo = getCustomerInfo(amsCustomer);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerInfo;
	}
	
	/**
	 * Get Customer Information by customerID
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerInfo getCustomerInfo(String customerId) {
		log.info("getCustomerInfo, customerId: " + customerId);
		CustomerInfo customerInfo = null;
		try {
			AmsCustomer amsCustomer = null;
			amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
			if(amsCustomer == null)
				return null;
			
			customerInfo = getCustomerInfo(amsCustomer);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerInfo;
	}
	
	private CustomerInfo getCustomerInfo(AmsCustomer amsCustomer) throws IllegalAccessException, InvocationTargetException {
		CustomerInfo customerInfo =  new CustomerInfo();
		
		log.info("[start] convert data to CustomerInfo object");
		if (amsCustomer != null) {
			BeanUtils.copyProperties(customerInfo, amsCustomer);
			customerInfo.setOldLoginId(amsCustomer.getLoginId());
			
			//[NTS1.0-le.hong.ha]Apr 26, 2013A - Start 
			if(amsCustomer.getCorporationType() != 0){
				customerInfo.setSex(amsCustomer.getCorpPicSex());
				customerInfo.setBirthday(amsCustomer.getCorpEstablishDate());
			}
			//[NTS1.0-le.hong.ha]Apr 26, 2013A - End
			
			AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
			if (amsSysCountry != null) {
				customerInfo.setCountryId(amsSysCountry.getCountryId());
				customerInfo.setCountryName(amsSysCountry.getCountryName());
				customerInfo.setCountryCode(amsSysCountry.getCountryCode());
			}

			AmsGroup amsGroup = amsCustomer.getAmsGroup();
            if (amsGroup != null) {
                customerInfo.setGroupName(amsGroup.getGroupName());
                customerInfo.setGroupId(amsGroup.getGroupId());
                customerInfo.setEaAccount(Helper.isEaGroupName(amsGroup.getGroupName()));
            } else {
                customerInfo.setEaAccount(false);
            }

            log.info("get amsCustomerSurvey info");
			//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
			AmsCustomerSurvey amsCustomerSurvey = amsCustomer.getAmsCustomerSurvey();
			if(amsCustomerSurvey != null){
				customerInfo.setFinancilAssets(amsCustomerSurvey.getFinancialAssets());
				customerInfo.setPurposeShortTermFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeShortTermFlg()));
				customerInfo.setPurposeLongTermFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeLongTermFlg()));
				customerInfo.setPurposeExchangeFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeExchangeFlg()));
				customerInfo.setPurposeSwapFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeSwapFlg()));
				customerInfo.setPurposeHedgeAssetFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeHedgeAssetFlg()));
				customerInfo.setPurposeHighIntFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeHighIntFlg()));
				customerInfo.setPurposeEconomicFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeEconomicFlg()));
//				customerInfo.setPurposeOther(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getPurposeOther()));
//				customerInfo.setPurposeOtherComment(amsCustomerSurvey.getPurposeOtherComment());
				setDataForBeneficOwner(customerInfo, amsCustomerSurvey);
				
				//Bo InvestmentPurpose
				customerInfo.setBoPurposeShortTermFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getBoPurposeShortTermFlg()));
				customerInfo.setBoPurposeDispAssetMngFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getBoPurposeDispAssetMngFlg()));
				customerInfo.setBoPurposeHedgeFlg(IConstants.ACTIVE_FLG.ACTIVE.equals(amsCustomerSurvey.getBoPurposeHedgeFlg()));
				
				customerInfo.setBoPurposeHedgeType(amsCustomerSurvey.getBoPurposeHedgeType());
				customerInfo.setBoPurposeHedgeAmount(amsCustomerSurvey.getBoPurposeHedgeAmount());
				customerInfo.setBoMaxLossAmount(amsCustomerSurvey.getBoMaxLossAmount());
				
				if(!StringUtil.isEmpty(customerInfo.getBeneficOwnerEstablishDate())){
					Calendar cal = Calendar.getInstance();
					try {
						if(!StringUtil.isEmpty(customerInfo.getBeneficOwnerEstablishDate())){
							cal.setTime(DateUtil.toDate(customerInfo.getBeneficOwnerEstablishDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
							int year = cal.get(Calendar.YEAR);
						    int month = cal.get(Calendar.MONTH);
						    int day = cal.get(Calendar.DATE);
						    String monthStr;
						    String dayStr;
						    if(month < 10){
						    	monthStr = "0" + (month + 1);
						    }else{
						    	monthStr = (month + 1) + "";
						    }
						    if(day < 10){
						    	dayStr = "0" + day;
						    }else{
						    	dayStr = day+"";
						    }
							customerInfo.setBeneficOwnerEstablishDateYear(year+"");
							customerInfo.setBeneficOwnerEstablishDateMonth(monthStr);
							customerInfo.setBeneficOwnerEstablishDateDay(dayStr);
						}

                        if(!StringUtil.isEmpty(customerInfo.getBeneficOwnerEstablishDate2())){
                            cal.setTime(DateUtil.toDate(customerInfo.getBeneficOwnerEstablishDate2(), DateUtil.PATTERN_YYYYMMDD_BLANK));
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            int day = cal.get(Calendar.DATE);
                            String monthStr;
                            String dayStr;
                            if(month < 10){
                                monthStr = "0" + (month + 1);
                            }else{
                                monthStr = (month + 1) + "";
                            }
                            if(day < 10){
                                dayStr = "0" + day;
                            }else{
                                dayStr = day+"";
                            }
                            customerInfo.setBeneficOwnerEstablishDateYear2(year+"");
                            customerInfo.setBeneficOwnerEstablishDateMonth2(monthStr);
                            customerInfo.setBeneficOwnerEstablishDateDay2(dayStr);
                        }

                        if(!StringUtil.isEmpty(customerInfo.getBeneficOwnerEstablishDate3())){
                            cal.setTime(DateUtil.toDate(customerInfo.getBeneficOwnerEstablishDate3(), DateUtil.PATTERN_YYYYMMDD_BLANK));
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            int day = cal.get(Calendar.DATE);
                            String monthStr;
                            String dayStr;
                            if(month < 10){
                                monthStr = "0" + (month + 1);
                            }else{
                                monthStr = (month + 1) + "";
                            }
                            if(day < 10){
                                dayStr = "0" + day;
                            }else{
                                dayStr = day+"";
                            }
                            customerInfo.setBeneficOwnerEstablishDateYear3(year+"");
                            customerInfo.setBeneficOwnerEstablishDateMonth3(monthStr);
                            customerInfo.setBeneficOwnerEstablishDateDay3(dayStr);
                        }
					} catch (Exception e) {
					}
				}
			}
			
			log.info("get amsSysVirtualBank info");
			AmsSysVirtualBank amsSysVirtualBank = amsCustomer.getAmsSysVirtualBank();
			if(amsSysVirtualBank != null){
				customerInfo.setVirtualBankAccNo(amsSysVirtualBank.getId().getVirtualBankAccNo());
			}
			
			Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + TRS_CONSTANT.TRS_WL_CODE);
			String bankName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_BANK_NAME);
			
			String branchName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_BRANCH_NAME);
			
			String bankAccountType = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_ACC_TYPE);
			
			String bankAccountName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_ACCOUNT_NAME);
			String bankAccountNameKana = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_ACCOUNT_NAME_KANA);
			
			customerInfo.setVirtualBankName(bankName);
			customerInfo.setVirtualBranchName(branchName);
			customerInfo.setVirtualAccType(bankAccountType);
			customerInfo.setVirtualAccName(bankAccountName);
			customerInfo.setVirtualAccNameKana(bankAccountNameKana);
		}
		log.info("get scCustomer info");
		ScCustomer scCustomer = null;
		scCustomer = getiScCustomerDAO().getScCustomer(customerInfo.getCustomerId());
		if(scCustomer !=null){
			customerInfo.setUsername(scCustomer.getUserName());
			customerInfo.setDescription(scCustomer.getDescription());
		}
		log.info("[end] convert data to CustomerInfo object");
		
		return customerInfo;
	}

	private void setDataForBeneficOwner(CustomerInfo customerInfo, AmsCustomerSurvey amsCustomerSurvey) {
		customerInfo.setBeneficOwnerFirstname(amsCustomerSurvey.getBeneficOwnerFirstname());
		customerInfo.setBeneficOwnerLastname(amsCustomerSurvey.getBeneficOwnerLastname());
		customerInfo.setBeneficOwnerFirstnameKana(amsCustomerSurvey.getBeneficOwnerFirstnameKana());
		customerInfo.setBeneficOwnerLastnameKana(amsCustomerSurvey.getBeneficOwnerLastnameKana());
		customerInfo.setBeneficOwnerFullname(amsCustomerSurvey.getBeneficOwnerFullname());
		customerInfo.setBeneficOwnerFullnameKana(amsCustomerSurvey.getBeneficOwnerFullnameKana());
		customerInfo.setBeneficOwnerFlg(amsCustomerSurvey.getBeneficOwnerFlg());
		customerInfo.setBeneficOwnerEstablishDate(amsCustomerSurvey.getBeneficOwnerEstablishDate());
		customerInfo.setBeneficOwnerZipcode(amsCustomerSurvey.getBeneficOwnerZipcode());
		customerInfo.setBeneficOwnerPrefecture(amsCustomerSurvey.getBeneficOwnerPrefecture());
		customerInfo.setBeneficOwnerCity(amsCustomerSurvey.getBeneficOwnerCity());
		customerInfo.setBeneficOwnerSection(amsCustomerSurvey.getBeneficOwnerSection());
		customerInfo.setBeneficOwnerBuildingName(amsCustomerSurvey.getBeneficOwnerBuildingName());
		customerInfo.setBeneficOwnerTel(amsCustomerSurvey.getBeneficOwnerTel());

		customerInfo.setBeneficOwnerFirstname2(amsCustomerSurvey.getBeneficOwnerFirstname2());
		customerInfo.setBeneficOwnerLastname2(amsCustomerSurvey.getBeneficOwnerLastname2());
		customerInfo.setBeneficOwnerFirstnameKana2(amsCustomerSurvey.getBeneficOwnerFirstnameKana2());
		customerInfo.setBeneficOwnerLastnameKana2(amsCustomerSurvey.getBeneficOwnerLastnameKana2());
		customerInfo.setBeneficOwnerFullname2(amsCustomerSurvey.getBeneficOwnerFullname2());
		customerInfo.setBeneficOwnerFullnameKana2(amsCustomerSurvey.getBeneficOwnerFullnameKana2());
		customerInfo.setBeneficOwnerFlg2(amsCustomerSurvey.getBeneficOwnerFlg2());
		customerInfo.setBeneficOwnerEstablishDate2(amsCustomerSurvey.getBeneficOwnerEstablishDate2());
		customerInfo.setBeneficOwnerZipcode2(amsCustomerSurvey.getBeneficOwnerZipcode2());
		customerInfo.setBeneficOwnerPrefecture2(amsCustomerSurvey.getBeneficOwnerPrefecture2());
		customerInfo.setBeneficOwnerCity2(amsCustomerSurvey.getBeneficOwnerCity2());
		customerInfo.setBeneficOwnerSection2(amsCustomerSurvey.getBeneficOwnerSection2());
		customerInfo.setBeneficOwnerBuildingName2(amsCustomerSurvey.getBeneficOwnerBuildingName2());
		customerInfo.setBeneficOwnerTel2(amsCustomerSurvey.getBeneficOwnerTel2());

		customerInfo.setBeneficOwnerFirstname3(amsCustomerSurvey.getBeneficOwnerFirstname3());
		customerInfo.setBeneficOwnerLastname3(amsCustomerSurvey.getBeneficOwnerLastname3());
		customerInfo.setBeneficOwnerFirstnameKana3(amsCustomerSurvey.getBeneficOwnerFirstnameKana3());
		customerInfo.setBeneficOwnerLastnameKana3(amsCustomerSurvey.getBeneficOwnerLastnameKana3());
		customerInfo.setBeneficOwnerFullname3(amsCustomerSurvey.getBeneficOwnerFullname3());
		customerInfo.setBeneficOwnerFullnameKana3(amsCustomerSurvey.getBeneficOwnerFullnameKana3());
		customerInfo.setBeneficOwnerFlg3(amsCustomerSurvey.getBeneficOwnerFlg3());
		customerInfo.setBeneficOwnerEstablishDate3(amsCustomerSurvey.getBeneficOwnerEstablishDate3());
		customerInfo.setBeneficOwnerZipcode3(amsCustomerSurvey.getBeneficOwnerZipcode3());
		customerInfo.setBeneficOwnerPrefecture3(amsCustomerSurvey.getBeneficOwnerPrefecture3());
		customerInfo.setBeneficOwnerCity3(amsCustomerSurvey.getBeneficOwnerCity3());
		customerInfo.setBeneficOwnerSection3(amsCustomerSurvey.getBeneficOwnerSection3());
		customerInfo.setBeneficOwnerBuildingName3(amsCustomerSurvey.getBeneficOwnerBuildingName3());
		customerInfo.setBeneficOwnerTel3(amsCustomerSurvey.getBeneficOwnerTel3());
	}

	/**
	 * Get AmsCustomer Information by customerID
	 * 
	 * @param customerId
	 * @return
	 */
	public AmsCustomer getAmsCustomer(String customerId) {
		AmsCustomer amsCustomer = null;
		amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
		return amsCustomer;
	}
	
	/**
	 * Get AmsCustomer Information by customerID
	 * 
	 * @param loginId
	 * @return
	 */
	public AmsCustomer getAmsCustomerByLoginId(String loginId) {
		return getiAmsCustomerDAO().getAmsCustomer(loginId);
	}
	
	public String getCustomerIdByCustomerServiceId(String customerServiceId) {
		List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDAO().findByCustomerServiceId(customerServiceId);
		
		if(listAmsCustomerService != null && listAmsCustomerService.size() > 0) {
			return listAmsCustomerService.get(0).getAmsCustomer().getCustomerId();
		}
		
		return null;
	}
	
	public Integer updateProfile(CustomerInfo customerInfo) {
		Integer result = IConstant.UPDATE_ACCOUNT;
		
		log.info("[start] get ams sys country with countryId = " + customerInfo.getCountryId());
		if(customerInfo.getCountryId() != null) {
			AmsSysCountry amsSysCountry = iAmsSysCountryDAO.findById(AmsSysCountry.class, customerInfo.getCountryId());
			if(amsSysCountry != null) {
				customerInfo.setCountryName(amsSysCountry.getCountryName());
			}
		}
		log.info("[end] get ams sys country with countryId = " + customerInfo.getCountryId());
		
		//Sync copy trade account
		CustomerServicesInfo customerServiceCopyTradeInfo = getCustomerService(customerInfo.getCustomerId(), IConstants.SERVICES_TYPE.COPY_TRADE);
		if(customerServiceCopyTradeInfo != null) {
			
			//[TRSGAP-873-quyen.le.manh]Jul 13, 2016A - Start - Update send Login_ID to SC when change account Information
			if (Helper.validateRequestToSC(customerInfo.getCustomerId()) && customerInfo.isChangeMailMain()){
				log.info("Customer changed loginId: " + customerInfo.getOldLoginId()
						+ " -> " + customerInfo.getMailMain() + ", must modify SocialAccount to SocialApi");
				Result updateScResult = modifySocialAccount(customerInfo, customerServiceCopyTradeInfo);
				if(Result.SUCCESS != updateScResult)
						return Constant.RESULT_FAIL;
			}
			//[TRSGAP-873-quyen.le.manh]Jul 13, 2016A - End

//			// update account information on MT4
			log.info("[start] update account MT4 of copy trade");
			result = MT4Manager.getInstance().updateAccountMt4(customerInfo, customerServiceCopyTradeInfo);
			
			if (IConstant.ACCOUNT_UPDATE_SUCCESS == result)
				log.info("Update Social account in MT4 Successful");
			else
				log.warn("Cannot update Social account in MT4, please contact to administrator");
			
			log.info("[end] update account MT4 of copy trade");
		} else
			log.info("CustomerId: " + customerInfo.getCustomerId() + " has NO copy trade account");
		
		//[TRSM1-1682-quyen.le.manh]Dec 23, 2015D - Start Merge source AMS API from BO to TRS M1 branch
		//Sync Fx account to MT4 (if enable Mt4Fx==1)
		if(customerInfo.getEnableMt4Fx() == 1){
			if(IConstant.ACCOUNT_UPDATE_SUCCESS == result) {
			log.info("[start] update account MT4 of FX");
			
			//Sync FX account
			CustomerServicesInfo customerServiceFxInfo = getCustomerService(customerInfo.getCustomerId(), IConstants.SERVICES_TYPE.FX);
			if(customerServiceFxInfo != null) {
				result = MT4Manager.getInstance().updateAccountMt4(customerInfo, customerServiceFxInfo);
				
				if (IConstant.ACCOUNT_UPDATE_SUCCESS == result)
					log.info("Update FX account in MT4 Successful");
				else
					log.warn("Cannot update FX account in MT4, please contact to administrator");
			} else
				log.info("CustomerId: " + customerInfo.getCustomerId() + " has NO FX account");
			log.info("[end] update account MT4 of FX");
			}
			//[TRSM1-1682-quyen.le.manh]Dec 23, 2015D - End
		} else if(IConstant.ACCOUNT_UPDATE_SUCCESS == result && customerInfo.isChangeMailMain() && !Helper.isEaGroupName(customerInfo.getGroupName())) { //TRSGAP-1936
			log.info("Customer changes MailMain, have to sync to NTD");
			
			//Sync mail main to NTD
			AmsCustomerModel.CustomerInfo.Builder builder = getNTDCustomerInfo(customerInfo.getCustomerId(), ITrsConstants.SERVICES_TYPE.NTD_FX);
			builder.setMailMain(customerInfo.getMailMain());
			
			int updateNtdResult = NTDManager.getInstance().updateCustomerInfo(builder.build());
			
			if(Constant.RESULT_SUCCESS != updateNtdResult)
				result = Constant.RESULT_FAIL;
		}

		if(IConstant.ACCOUNT_UPDATE_SUCCESS == result) {
			Boolean updateProfileResult = updateProfileInfo(customerInfo);
			
			if (updateProfileResult) {
				log.info("Update customer success " + updateProfileResult);
				
				AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerInfo.getCustomerId());
				
				if (customerInfo.getChangePasswordFlag()) {
					String newLoginPassword = customerInfo.getNewPassword();
					if (newLoginPassword != null && !StringUtils.isBlank(newLoginPassword)) {
						
						Boolean resetPasswordResult = updatePassword(customerInfo);
						if (resetPasswordResult) {
							// if change password successful
							log.info("Update password of customerId: "	+ customerInfo.getCustomerId() + " successful");
							
							log.info("[start] send mail to customer about change password successful");
							String language = amsCustomer.getDisplayLanguage();
							if (language == null || StringUtils.isBlank(language)) {
								language = IConstants.Language.ENGLISH;
							}
							
							String customerServiceId = customerServiceCopyTradeInfo != null ? customerServiceCopyTradeInfo.getCustomerServiceId() : "";
							sendmailChangePass(amsCustomer, language, customerServiceId, customerInfo.getNewPassword());
						} else
							log.info("Update password of customerId: "	+ customerInfo.getCustomerId() + " FAIL");
					}
				}
				result = IConstant.ACCOUNT_UPDATE_SUCCESS;
			} else
				log.info("Cannot update customerInfo, customerId: " + customerInfo.getCustomerId());
		}
		return result;
	}

	/**
	 * sendmailChangePass　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
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
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail to customer about change password successful");
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 22, 2013
	 */
	public void sendmailChangeMt4Pass(String customerId,String customerServiceId, String newMasterPassword,String newInvestorPassword) {
		AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
		log.info("[start] send mail to customer about change password successful");
		String mailCode = new StringBuffer(ITrsConstants.MAIL_TEMPLATE.AMS_PASS_CHANGED).append("_").append(amsCustomer.getDisplayLanguage()).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setEmailAddress(amsCustomer.getMailMain());
		amsMailTemplateInfo.setCustomerServiceId(customerServiceId);
		amsMailTemplateInfo.setNewMasterPassword(newMasterPassword);
		amsMailTemplateInfo.setNewInvestorPassword(newInvestorPassword);
		
		//[NTS1.0-le.hong.ha]May 15, 2013A - Start 
		HashMap<String, String> from = new HashMap<String, String>();
		AmsWhitelabelConfig amsWhitelabelConfig1 = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, amsCustomer.getWlCode());
		String mailFrom = "";
		if(amsWhitelabelConfig1 != null){
			mailFrom = amsWhitelabelConfig1.getConfigValue();
		}
		from.put("TRS", mailFrom);
		
		amsMailTemplateInfo.setFrom(from);
		//[NTS1.0-le.hong.ha]May 15, 2013A - End
		
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		// amsMailTemplateInfo.setTemplateId(mailTemplateInfo.getMailTemplateId());
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail to customer about change password successful");
	}
	
//	/**
//	 * Send mail change user information to inform to CS
//	 * 
//	 * @param
//	 * @return
//	 * @throws
//	 * @author le.hong.ha
//	 * @CrDate Apr 23, 2013
//	 */
//	public void sendmailChangeInfoToCS(CustomerInfo customerInfo) {
//		try {
//			String customerId = customerInfo.getCustomerId();
//			AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
//			log.info("[start] send mail to CS about change user information");
//			
//			String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_RQ_INFO_CHANGED_JA;
//			
//			TrsMailTemplateInfo mail = new TrsMailTemplateInfo();
//			HashMap<String, Object> content = new HashMap<String, Object>();
//			if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER){
//				//New Value
//				mail.setCustomerName(customerInfo.getFullName());
//				mail.setCustomerId(customerId);
//				mail.setCustomerAddress(customerInfo.getAddress());
//				mail.setCorporationType(customerInfo.getCorporationType()+"");
//				
//				//Previous value
//				mail.setPreCustomerName(amsCustomer.getFullName());
//				mail.setPreCustomerAddress(amsCustomer.getAddress());
//			}else{
//				//New Value
//				mail.setCustomerName(customerInfo.getCorpFullname());
//				mail.setCustomerAddress(customerInfo.getAddress());
//				mail.setCustomerId(customerId);
//				
//				String picFirstName = !StringUtil.isEmpty(customerInfo.getCorpPicFirstname()) ? customerInfo.getCorpPicFirstname() : "";
//				String picLastame = !StringUtil.isEmpty(customerInfo.getCorpPicLastname()) ? customerInfo.getCorpPicLastname() : "";
//				mail.setPicName(picFirstName + " " + picLastame);
//				mail.setPicAddress(customerInfo.getCorpPicAddress());
//				mail.setBeneficName(customerInfo.getBeneficOwnerFullname());
//				
//				//New BeneficOwner
//				String bePrefecture = customerInfo.getBeneficOwnerPrefecture() != null ? customerInfo.getBeneficOwnerPrefecture(): "";
//				String beCity = customerInfo.getBeneficOwnerCity() != null ? customerInfo.getBeneficOwnerCity():"";
//				String beSection = customerInfo.getBeneficOwnerSection() != null ? customerInfo.getBeneficOwnerSection() : "";
//				String beBuildingName = customerInfo.getBeneficOwnerBuildingName() != null ? customerInfo.getBeneficOwnerBuildingName():"";
//				mail.setBeneficAddress(bePrefecture + beCity + beSection + beBuildingName);
//				
//				mail.setReqName(customerInfo.getCorpRepFullname());
//				mail.setCorporationType(customerInfo.getCorporationType()+"");
//				
//				//Previous value
//				mail.setPreCustomerName(amsCustomer.getFullName());
//				mail.setPreCustomerAddress(amsCustomer.getAddress());
//				
//				String oldPicFirstName = !StringUtil.isEmpty(amsCustomer.getCorpPicFirstname()) ? amsCustomer.getCorpPicFirstname() : "";
//				String oldPicLastName = !StringUtil.isEmpty(amsCustomer.getCorpPicLastname()) ? amsCustomer.getCorpPicLastname() : "";
//				mail.setPrePicName(oldPicFirstName + " " + oldPicLastName);
//				mail.setPrePicAddress(amsCustomer.getCorpPicAddress());
//				
//				AmsCustomerSurvey servey = amsCustomer.getAmsCustomerSurvey();
//				if(servey != null){
//					//Previous BeneficOwner
//					
//					mail.setPreBeneficName(servey.getBeneficOwnerFullname());
//					
//					bePrefecture = servey.getBeneficOwnerPrefecture() != null ? servey.getBeneficOwnerPrefecture(): "";
//					beCity = servey.getBeneficOwnerCity() != null ? servey.getBeneficOwnerCity():"";
//					beSection = servey.getBeneficOwnerSection() != null ? servey.getBeneficOwnerSection() : "";
//					beBuildingName = servey.getBeneficOwnerBuildingName() != null ? servey.getBeneficOwnerBuildingName():"";
//					mail.setPreBeneficAddress(bePrefecture + beCity + beSection + beBuildingName);
//				}
//				
//				mail.setPreReqName(amsCustomer.getCorpRepFullname());
//			}
//			
//			content.put("mailInfo", mail);
//			mail.setContent(content);
//			
//			HashMap<String, String> to = new HashMap<String, String>();
//			HashMap<String, String> from = new HashMap<String, String>();
//			
//			AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_CS, amsCustomer.getWlCode());
//			String mailCS = "";
//			if(amsWhitelabelConfig != null){
//				mailCS = amsWhitelabelConfig.getConfigValue();
//			}
//			to.put("CS", mailCS);
//			
//			AmsWhitelabelConfig amsWhitelabelConfig1 = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, amsCustomer.getWlCode());
//			String mailFrom = "";
//			if(amsWhitelabelConfig1 != null){
//				mailFrom = amsWhitelabelConfig1.getConfigValue();
//			}
//			from.put("TRS", mailFrom);
//			
//			mail.setFrom(from);
//			mail.setTo(to);
//			mail.setWlCode(amsCustomer.getWlCode());
//			mail.setMailCode(mailCode);
//			mail.setSubject(mailCode);
//			
//			log.info("Mail Content: " + mail);
//			
//			jmsContextSender.sendMail(mail, false);
//			log.info("[end] send mail to CS about change user information successfull");
//		} catch (Exception e) {
//			log.error("[end] send mail to CS about change user information failed");
//			log.error(e.getMessage(), e);
//		}
//	}
	
	/**
	 * Send mail change user information to inform to CS
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Apr 23, 2013
	 */
	public void sendmailChangeInfoToCS(CustomerInfo customerAfterChange) {
		String customerId = customerAfterChange.getCustomerId();
		try {
			AmsCustomer customerBeforeChange = getiAmsCustomerDAO().getCustomerInfo(customerId);
			log.info("[Start] Send mail to CS about change user information, customerId [" + customerId + "]");
			
			TrsMailTemplateInfo mail = new TrsMailTemplateInfo();
			if (customerAfterChange.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER) {

				mail.setCustomerId(customerId);
				mail.setCorporationType(String.valueOf(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER));

				// Previous value
				mail.setPreCustomerName(customerBeforeChange.getFullName());
				//[TRSGAP-1469-theln]Aug 2, 2016A - Start 
				String preCustomerNameKana = "";
				if (!StringUtil.isEmpty(customerBeforeChange.getFirstNameKana())) {
					preCustomerNameKana = customerBeforeChange.getFirstNameKana();
				}
				if (!StringUtil.isEmpty(customerBeforeChange.getLastNameKana())) {
					preCustomerNameKana = preCustomerNameKana + " " + customerBeforeChange.getLastNameKana();
				}
				mail.setPreCustomerNameKana(preCustomerNameKana);
				if (customerBeforeChange.getZipcode() != null)
					mail.setPreZipcode(customerBeforeChange.getZipcode());
				else
					mail.setPreZipcode("");
				mail.setPreCustomerAddress(customerBeforeChange.getAddress());

				// New Value
				mail.setCustomerName(customerAfterChange.getFullName());
				
				String customerNameKana = "";
				if (!StringUtil.isEmpty(customerAfterChange.getFirstNameKana())) {
					customerNameKana = customerAfterChange.getFirstNameKana();
				}
				if (!StringUtil.isEmpty(customerAfterChange.getLastNameKana())) {
					customerNameKana = customerNameKana + " " + customerAfterChange.getLastNameKana();
				}
				mail.setCustomerNameKana(customerNameKana);
				
				if (customerAfterChange.getZipcode() != null)
					mail.setZipcode(customerAfterChange.getZipcode());
				else
					mail.setZipcode("");
				//[TRSGAP-1469-theln]Aug 2, 2016A - End
				mail.setCustomerAddress(customerAfterChange.getAddress());

			} else if (customerAfterChange.getCorporationType() == ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER) {

				mail.setCustomerId(customerId);
				mail.setCorporationType(String.valueOf(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER));
				
				// Previous value
				mail.setPreCustomerName(customerBeforeChange.getFullName());
				mail.setPreCustomerNameKana(customerBeforeChange.getCorpFullnameKana());
				mail.setPreZipcode(customerBeforeChange.getZipcode());
				mail.setPreCustomerAddress(customerBeforeChange.getAddress());
				mail.setPreReqName(customerBeforeChange.getCorpRepFullname());
				//[TRSGAP-1223-theln]Jul 24, 2016A - Start - Fix preCorpRepFullnameKana has been null
				String preCorpRepFullnameKana = "";
				if (!StringUtil.isEmpty(customerBeforeChange.getCorpRepFirstnameKana()))
					preCorpRepFullnameKana = customerBeforeChange.getCorpRepFirstnameKana();
				if (!StringUtil.isEmpty(customerBeforeChange.getCorpRepLastnameKana()))
					preCorpRepFullnameKana = preCorpRepFullnameKana + " " + customerBeforeChange.getCorpRepLastnameKana();
				mail.setPreReNameKana(preCorpRepFullnameKana);
				//[TRSGAP-1223-theln]Jul 24, 2016A - End - Fix preCorpRepFullnameKana has been null
				mail.setPrePicName(customerBeforeChange.getCorpPicFirstname() + " " + customerBeforeChange.getCorpPicLastname());
				mail.setPrePicNameKana(customerBeforeChange.getCorpPicFirstnameKana() + " " + customerBeforeChange.getCorpPicLastnameKana());
				mail.setPrePicZipcode(customerBeforeChange.getCorpPicZipcode());
				mail.setPrePicAddress(customerBeforeChange.getCorpPicAddress());
				
//				if(customerBeforeChange.getAmsCustomerSurvey() != null){
//					AmsCustomerSurvey servey = customerBeforeChange.getAmsCustomerSurvey();
//					mail.setPreBeneficName(servey.getBeneficOwnerFullname());
//					mail.setPreBeneficNameKana(servey.getBeneficOwnerFullnameKana());
//					mail.setPreBeneficZipcode(servey.getBeneficOwnerZipcode());
//
//					String prePrefecture = servey.getBeneficOwnerPrefecture() != null ? servey.getBeneficOwnerPrefecture(): "";
//					String preCity = servey.getBeneficOwnerCity() != null ? servey.getBeneficOwnerCity():"";
//					String preSection = servey.getBeneficOwnerSection() != null ? servey.getBeneficOwnerSection() : "";
//					String preBuildingName = servey.getBeneficOwnerBuildingName() != null ? servey.getBeneficOwnerBuildingName():"";
//					mail.setPreBeneficAddress(prePrefecture + preCity + preSection + preBuildingName);
//				}
				
				// New Value
				mail.setCustomerName(customerAfterChange.getCorpFullname());
				mail.setCustomerNameKana(customerAfterChange.getCorpFullnameKana());
				mail.setZipcode(customerAfterChange.getZipcode());
				mail.setCustomerAddress(customerAfterChange.getAddress());
				mail.setReqName(customerAfterChange.getCorpRepFullname());
				//[TRSGAP-1223-theln]Jul 24, 2016A - Start - Fix corpRepFullnameKana has been null
				String corpRepFullnameKana = "";
				if (!StringUtil.isEmpty(customerAfterChange.getCorpRepFirstnameKana()))
					corpRepFullnameKana = customerAfterChange.getCorpRepFirstnameKana();
				if (!StringUtil.isEmpty(customerAfterChange.getCorpRepLastnameKana()))
					corpRepFullnameKana = corpRepFullnameKana + " " + customerAfterChange.getCorpRepLastnameKana();
				mail.setReNameKana(corpRepFullnameKana);
				//[TRSGAP-1223-theln]Jul 24, 2016A - End - Fix corpRepFullnameKana has been null
				mail.setPicName(customerAfterChange.getCorpPicFirstname() + " " + customerAfterChange.getCorpPicLastname());
				mail.setPicNameKana(customerAfterChange.getCorpPicFirstnameKana() + " " + customerAfterChange.getCorpPicLastnameKana());
				mail.setPicZipcode(customerAfterChange.getCorpPicZipcode());
				mail.setPicAddress(customerAfterChange.getCorpPicAddress());
//				mail.setBeneficName(customerAfterChange.getBeneficOwnerFullname());
//				mail.setBeneficNameKana(customerAfterChange.getBeneficOwnerFullnameKana());
//				mail.setBeneficZipcode(customerAfterChange.getBeneficOwnerZipcode());
				
//				String prefecture = customerAfterChange.getBeneficOwnerPrefecture() != null ? customerAfterChange.getBeneficOwnerPrefecture(): "";
//				String city = customerAfterChange.getBeneficOwnerCity() != null ? customerAfterChange.getBeneficOwnerCity():"";
//				String section = customerAfterChange.getBeneficOwnerSection() != null ? customerAfterChange.getBeneficOwnerSection() : "";
//				String buildingName = customerAfterChange.getBeneficOwnerBuildingName() != null ? customerAfterChange.getBeneficOwnerBuildingName():"";
//				mail.setBeneficAddress(prefecture + city + section + buildingName);
				
			} else {
				log.error("Don't support for this type customer, customerId [" + customerId + "], type [" + customerAfterChange.getCorporationType() + "]");
			}
			
			HashMap<String, String> to = new HashMap<String, String>();
			AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_CS, customerBeforeChange.getWlCode());
			String mailCS = "";
			if(amsWhitelabelConfig != null){
				mailCS = amsWhitelabelConfig.getConfigValue();
			}
			to.put("CS", mailCS);
			
			HashMap<String, String> from = new HashMap<String, String>();
			AmsWhitelabelConfig amsWhitelabelConfig1 = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, customerBeforeChange.getWlCode());
			String mailFrom = "";
			if(amsWhitelabelConfig1 != null){
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			
			mail.setFrom(from);
			mail.setTo(to);
			mail.setWlCode(customerBeforeChange.getWlCode());
			mail.setMailCode(ITrsConstants.MAIL_TEMPLATE.AMS_RQ_INFO_CHANGED_JA);
			mail.setSubject(ITrsConstants.MAIL_TEMPLATE.AMS_RQ_INFO_CHANGED_JA);
			
			HashMap<String, Object> content = new HashMap<String, Object>();
			content.put("mailInfo", mail);
			mail.setContent(content);
			
			log.info("Mail Content: " + mail);
			jmsContextSender.sendMail(mail, false);
			log.info("[End] Send mail to CS about change user information, customerId [" + customerId + "]");
		} catch (Exception e) {
			log.error("Send mail to CS about change user information failed, customerId [" + customerId + "]");
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Send mail change user information to inform to Customer
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Apr 23, 2013
	 */
	public void sendmailChangeInfoToCustomer(CustomerInfo customerInfo) {
		String customerId = customerInfo.getCustomerId();
		try {
			log.info("[Start] Send mail to customer about change user information, customerId [" + customerId + "]");
			
			TrsMailTemplateInfo mail = new TrsMailTemplateInfo();
			String wlCode = customerInfo.getWlCode();

			AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.UPLOAD_DOC_URL_KEY, wlCode);
			String logFileUrl = "";
			if (amsWhitelabelConfig != null) {
				logFileUrl = amsWhitelabelConfig.getConfigValue();
			}

			if (customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER) {

				mail.setCorporationType(String.valueOf(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER));
				mail.setCustomerId(customerId);
				mail.setCustomerName(customerInfo.getFullName());
				//[TRSGAP-1469-theln]Aug 2, 2016A - Start 
				String customerNameKana = "";
				if (!StringUtil.isEmpty(customerInfo.getFirstNameKana())) {
					customerNameKana = customerInfo.getFirstNameKana();
				}
				if (!StringUtil.isEmpty(customerInfo.getLastNameKana())) {
					customerNameKana = customerNameKana + " " + customerInfo.getLastNameKana();
				}
				mail.setCustomerNameKana(customerNameKana);
				//[TRSGAP-1469-theln]Aug 2, 2016A - End
				if (customerInfo.getZipcode() != null)
					mail.setZipcode(customerInfo.getZipcode());
				else
					mail.setZipcode("");
				mail.setCustomerAddress(customerInfo.getAddress());
				mail.setLogFileURL(logFileUrl);

			} else if (customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER) {

				mail.setCorporationType(String.valueOf(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER));
				mail.setCustomerId(customerId);
				mail.setCustomerName(customerInfo.getCorpFullname());
				mail.setCustomerNameKana(customerInfo.getCorpFullnameKana());
				mail.setZipcode(customerInfo.getZipcode());
				mail.setCustomerAddress(customerInfo.getAddress());
				mail.setReqName(customerInfo.getCorpRepFullname());
				//[TRSGAP-1223-theln]Jul 24, 2016A - Start - Fix corpRepFullnameKana has been null
				String corpRepFullnameKana = "";
				if (!StringUtil.isEmpty(customerInfo.getCorpRepFirstnameKana()))
					corpRepFullnameKana = customerInfo.getCorpRepFirstnameKana();
				if (!StringUtil.isEmpty(customerInfo.getCorpRepLastnameKana()))
					corpRepFullnameKana = corpRepFullnameKana + " " + customerInfo.getCorpRepLastnameKana();
				mail.setReNameKana(corpRepFullnameKana);
				//[TRSGAP-1223-theln]Jul 24, 2016A - End - Fix corpRepFullnameKana has been null
				mail.setPicName(customerInfo.getCorpPicFirstname() + " " + customerInfo.getCorpPicLastname());
				mail.setPicNameKana(customerInfo.getCorpPicFirstnameKana() + " " + customerInfo.getCorpPicLastnameKana());
				mail.setPicZipcode(customerInfo.getCorpPicZipcode());
				mail.setPicAddress(customerInfo.getCorpPicAddress());
//				mail.setBeneficName(customerInfo.getBeneficOwnerFullname());
//				mail.setBeneficNameKana(customerInfo.getBeneficOwnerFullnameKana());
//				mail.setBeneficZipcode(customerInfo.getBeneficOwnerZipcode());
//
//				String bePrefecture = customerInfo.getBeneficOwnerPrefecture() != null ? customerInfo.getBeneficOwnerPrefecture(): "";
//				String beCity = customerInfo.getBeneficOwnerCity() != null ? customerInfo.getBeneficOwnerCity():"";
//				String beSection = customerInfo.getBeneficOwnerSection() != null ? customerInfo.getBeneficOwnerSection() : "";
//				String beBuildingName = customerInfo.getBeneficOwnerBuildingName() != null ? customerInfo.getBeneficOwnerBuildingName():"";
//				mail.setBeneficAddress(bePrefecture + beCity + beSection + beBuildingName);
				mail.setLogFileURL(logFileUrl);

			} else {
				log.error("Don't support for this type customer, customerId [" + customerId + "], type [" + customerInfo.getCorporationType() + "]");
			}

			HashMap<String, Object> content = new HashMap<String, Object>();
			content.put("mailInfo", mail);
			mail.setContent(content);

			HashMap<String, String> from = new HashMap<String, String>();
			AmsWhitelabelConfig amsWhitelabelConfig1 = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, wlCode);
			String mailFrom = "";
			if (amsWhitelabelConfig1 != null) {
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			mail.setFrom(from);

			HashMap<String, String> to = new HashMap<String, String>();
			to.put("CustomerMail", customerInfo.getMailMain());
			mail.setTo(to);

			mail.setWlCode(wlCode);
			mail.setMailCode(ITrsConstants.MAIL_TEMPLATE.AMS_INFO_CHANGED_JA);
			mail.setSubject(ITrsConstants.MAIL_TEMPLATE.AMS_INFO_CHANGED_JA);

			log.info("Mail Content: " + mail);
			jmsContextSender.sendMail(mail, false);
			log.info("[End] Send mail to customer about change user information, customerId [" + customerId + "]");
		} catch (Exception e) {
			log.info("Send mail to customer about change user information fail, customerId [" + customerId + "]");
			log.error(e.getMessage(),e);
		}
	}
	
	public void sendmailNotifyDocToCS(CustomerInfo customerInfo) {
		try {
			String customerId = customerInfo.getCustomerId();
			log.info("[start] send mail to CS about upload file successful");
			String mailCode = ITrsConstants.MAIL_TEMPLATE.AMS_NOTIFY_DOCS_JA;
			String wlCode = customerInfo.getWlCode();
			
			TrsMailTemplateInfo mail = new TrsMailTemplateInfo();
			HashMap<String, Object> content = new HashMap<String, Object>();
			
			mail.setCustomerId(customerId);
			if(customerInfo.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER)
				mail.setFullName(customerInfo.getFullName());
			else
				mail.setFullName(customerInfo.getCorpFullname());
			mail.setMail(customerInfo.getMailMain());
			
			content.put("mailInfo", mail);
			mail.setContent(content);
			
			HashMap<String, String> to = new HashMap<String, String>();
			HashMap<String, String> from = new HashMap<String, String>();
			
			AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_CS, wlCode);
			String mailCS = "";
			if(amsWhitelabelConfig != null){
				mailCS = amsWhitelabelConfig.getConfigValue();
			}
			to.put("CS", mailCS);
			
			AmsWhitelabelConfig amsWhitelabelConfig1 = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, wlCode);
			String mailFrom = "";
			if(amsWhitelabelConfig1 != null){
				mailFrom = amsWhitelabelConfig1.getConfigValue();
			}
			from.put("TRS", mailFrom);
			
			mail.setFrom(from);
			mail.setTo(to);
			mail.setWlCode(wlCode);
			mail.setMailCode(mailCode);
			mail.setSubject(mailCode);
			
			log.info("Mail Content: " + mail);
			
			jmsContextSender.sendMail(mail, false);
			
			log.info("[end] send mail to Customer upload file successfull");
		} catch (Exception e) {
			log.error("[end] send mail to Customer upload file failed");
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * @description
	 * @version NTS
	 * @author TheLN
	 * @CrDate Mar 16, 2016
	 * @Copyright Nextop Asia Limited. All rights reserved.
	 */
	public void sendmailSocial(CustomerInfo customerInfo, String mailCode) {
		HashMap<String, String> to = new HashMap<String, String>();
		HashMap<String, String> from = new HashMap<String, String>();
		
		try {
			log.info("[start] send mail social to customer about " + mailCode);
			String wlCode="";
			if (!StringUtil.isEmpty(customerInfo.getWlCode()))
				wlCode = customerInfo.getWlCode();
			else
				wlCode = "TRS";
			AmsScMailTemplateInfo mail = new AmsScMailTemplateInfo();
			HashMap<String, Object> content = new HashMap<String, Object>();
			
			if (!StringUtil.isEmpty(customerInfo.getGuruNickName()))
				mail.setCustomerName(customerInfo.getGuruNickName());
			if (!StringUtil.isEmpty(customerInfo.getGuruCustomerId()))
				mail.setGuruCustomerId(customerInfo.getGuruCustomerId());
			if (!StringUtil.isEmpty(customerInfo.getCustomerServiceId()))
				mail.setCustomerServiceId(customerInfo.getCustomerServiceId());
			if (!StringUtil.isEmpty(customerInfo.getFullName()))
				mail.setFullName(customerInfo.getFullName());
			if (!StringUtil.isEmpty(customerInfo.getEventDateTime()))
				mail.setEventDateTime(customerInfo.getEventDateTime());
			if (!StringUtil.isEmpty(customerInfo.getSymbol()))
				mail.setSymbol(customerInfo.getSymbol());
			if (!StringUtil.isEmpty(customerInfo.getVolume()))
				mail.setVolume(customerInfo.getVolume());
			if (!StringUtil.isEmpty(customerInfo.getOrderType()))
				mail.setOrderType(customerInfo.getOrderType());
			if (!StringUtil.isEmpty(customerInfo.getOrderDate()))
				mail.setOrderDate(customerInfo.getOrderDate());
			if (!StringUtil.isEmpty(customerInfo.getTradeType()))
				mail.setTradeType(customerInfo.getTradeType());
			if (!StringUtil.isEmpty(customerInfo.getLastTradeTime()))
				mail.setLastTradeTime(customerInfo.getLastTradeTime());
			if (!StringUtil.isEmpty(customerInfo.getOrderId()))
				mail.setOrderId(customerInfo.getOrderId());
			if (!StringUtil.isEmpty(customerInfo.getExecutionPrice()))
				mail.setExecutionPrice(customerInfo.getExecutionPrice());
			
			content.put("mailInfo", mail);
			mail.setContent(content);
			
			if (!StringUtil.isEmpty(customerInfo.getMailMain())){
				to.put("copierEmail", customerInfo.getMailMain());
			}
			if (!StringUtil.isEmpty(customerInfo.getMailMobile())){
				to.put("copierMobileEmail", customerInfo.getMailMobile());
			}
			
			AmsWhitelabelConfig amsWhitelabelConfig = amsWhitelabelConfigDAO.getAmsWhiteLabelConfig(ITrsConstants.TRS_CONSTANT.MAIL_SERVER_BUSINESS, wlCode);
			String mailFrom = "";
			if (!StringUtil.isEmpty(amsWhitelabelConfig.getConfigValue())) {
				mailFrom = amsWhitelabelConfig.getConfigValue();
			} else {
				mailFrom = "no-reply@min";
			}
			from.put("TRS", mailFrom);
	
			mail.setFrom(from);
			mail.setTo(to);
			mail.setWlCode(wlCode);
			mail.setMailCode(mailCode);
			mail.setSubject(mailCode);
			
			log.info("Mail Content: " + mail);
			
			if (to.size() > 0) {
				jmsContextSender.sendMail(mail, false);
				log.info("[end] send mail social to "+ to +" about " + mailCode + " is SUCCESS");
			} else {
				log.warn("Mail To is empty..");
			}
			
		} catch (Exception e) {
			log.error("[end] send mail social to " + to + " about " + mailCode + "is FAILED");
			log.error(e.getMessage(),e);
		}
	}

	
	/**
	 * 　 get Information about ams customer to compare for updating
	 * 
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 11, 2012
	 * @MdDate
	 */

	public Boolean updateProfileInfo(CustomerInfo customerInfo) {
		try {
			List<String> listBefore = new ArrayList<String>();
			List<String> listAfter = new ArrayList<String>();
			
			//publicMode = true: NTD FX, publicMode = false: MT4 FX
			boolean isPublicMode = customerInfo.getEnableMt4Fx() == 1 ? false : true;
			
			String customerId = customerInfo.getCustomerId();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
			AmsCustomerService serviceCopyTrade = getiAmsCustomerServiceDAO().getCustomerServicesInfo(customerId, ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
			
			int enableServiceType = isPublicMode ? ITrsConstants.SERVICES_TYPE.NTD_FX : ITrsConstants.SERVICES_TYPE.MT4_FX;
			
			AmsCustomerService serviceFx = getiAmsCustomerServiceDAO().getCustomerServicesInfo(customerId, enableServiceType);
			if (serviceFx == null) {
				log.warn("Update FAIL. NOT FOUND customerService, serviceType: " + enableServiceType);
				return false;
			}
			
			if (serviceCopyTrade == null) {
				log.warn("Update FAIL. NOT FOUND customerService, serviceType: " + ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE);
				return false;
			}
			
			String newPasswod = customerInfo.getNewPassword();
			if (!StringUtils.isBlank(newPasswod) && newPasswod != null && !newPasswod.equals(amsCustomer.getLoginPass())) {
				listBefore.add("LoginPassword");
				listAfter.add("LoginPassword");
			}

			//[TRSBO-3615-quyen.le.manh]Nov 3, 2015D - Start - done allow customer to update leverage
//			Integer leverage = customerInfo.getLeverage();
//			if (serviceCopyTrade != null) {
//				if (leverage != null && !leverage.equals(serviceCopyTrade.getLeverage())) {
//					listBefore.add("Leverage = " + serviceCopyTrade.getLeverage());
//					log.info("[start] update leverage on social customer services to " + leverage);
//					
//					serviceCopyTrade.setLeverage(leverage);
//					getiAmsCustomerServiceDAO().merge(serviceCopyTrade);
//					listAfter.add("Leverage = " + leverage);
//					log.info("[end] update leverage on social customer services to " + leverage);
//				}
//			}
//			
//			if (serviceFx != null) {
//				if (leverage != null && !leverage.equals(serviceFx.getLeverage())) {
//					listBefore.add("Leverage = " + serviceFx.getLeverage());
//					log.info("[start] update leverage on mt4 customer services to " + leverage);
//					
//					serviceFx.setLeverage(leverage);
//					getiAmsCustomerServiceDAO().merge(serviceFx);
//					listAfter.add("Leverage = " + leverage);
//					log.info("[end] update leverage on mt4 customer services to " + leverage);
//				}
//			}
			//[TRSBO-3615-quyen.le.manh]Nov 3, 2015D - End
			
			String tel1 = customerInfo.getTel1();
			if (tel1 != null && !tel1.equals(amsCustomer.getTel1())) {
				listBefore.add("Phone Number = " + amsCustomer.getTel1());
				listAfter.add("Phone Number = " + tel1);
				amsCustomer.setTel1(tel1);
			}

			Map<String, String> mapLanguage = SystemPropertyConfig
					.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY
									+ customerInfo.getWlCode() + "_"
									+ IConstants.SYS_PROPERTY.LANGUAGE);

			String displayLanguage = customerInfo.getDisplayLanguage();
			if (displayLanguage != null && mapLanguage.containsKey(StringUtil.toUpperCase(displayLanguage))) {
				if (!displayLanguage.equals(amsCustomer.getDisplayLanguage())) {
					listBefore.add("Language = " + amsCustomer.getDisplayLanguage());
					listAfter.add("Language = " + displayLanguage);
					amsCustomer.setDisplayLanguage(displayLanguage);
				}
			}

			Integer countryId = customerInfo.getCountryId();
			if (countryId != null && !countryId.equals(amsCustomer.getAmsSysCountry().getCountryId())) {
				listBefore.add("CountryId = " + amsCustomer.getAmsSysCountry().getCountryId());
				listAfter.add("CountryId = " + countryId);
				AmsSysCountry amsSysCountry = new AmsSysCountry();
				amsSysCountry.setCountryId(countryId);
				
				amsCustomer.setAmsSysCountry(amsSysCountry);
			}
			
			//[NTS1.0-le.hong.ha]Apr 17, 2013A - Start 
			String tel2 = customerInfo.getTel2();
			String mailMain = customerInfo.getMailMain();
			String additionalMail = customerInfo.getMailAddtional();
			String mobileMail = customerInfo.getMailMobile();
			String corpPicMailMobile = customerInfo.getCorpPicMailMobile();
			Integer finacialAssests = customerInfo.getFinancilAssets();
			Integer purposeInvestFlg = customerInfo.isPurposeShortTermFlg() == true ? 1 : 0;
			Integer purposeLongTermFlg = customerInfo.isPurposeLongTermFlg() == true ? 1 : 0;
			Integer purposeExchangeFlg = customerInfo.isPurposeExchangeFlg() == true ? 1 : 0;
			Integer purposeSwapFlg = customerInfo.isPurposeSwapFlg() == true ? 1 : 0;
			Integer purposeHedgeAssetFlg = customerInfo.isPurposeHedgeAssetFlg() == true ? 1 : 0;
			Integer purposeHighIntFlg = customerInfo.isPurposeHighIntFlg() == true ? 1 : 0;
			Integer purposeEconomicFlg = customerInfo.isPurposeEconomicFlg() == true ? 1 : 0;
			
			AmsCustomerSurvey amsCustomerSurvey = amsCustomer.getAmsCustomerSurvey();
			
			//check mail
			boolean isChangeMail = false;
			boolean isChangeLoginId = false;
			if (tel2 != null && !tel2.equals(amsCustomer.getTel2())){
				listBefore.add("Tel2 = " + amsCustomer.getTel2());
				listAfter.add("Tel2 = " + tel2);
				amsCustomer.setTel2(tel2);
			}
			if (additionalMail != null && !additionalMail.equals(amsCustomer.getMailAddtional())) {
				listBefore.add("MailAdditional = " + amsCustomer.getMailAddtional());
				listAfter.add("MailAdditional = " + additionalMail);
				amsCustomer.setMailAddtional(additionalMail);
			}
			if (corpPicMailMobile != null && !corpPicMailMobile.equals(amsCustomer.getCorpPicMailMobile())){
				listBefore.add("corpPicMailMobile = " + amsCustomer.getCorpPicMailMobile());
				listAfter.add("corpPicMailMobile = " + corpPicMailMobile);
				amsCustomer.setCorpPicMailMobile(corpPicMailMobile);
			}
			if (mobileMail != null && !mobileMail.equals(amsCustomer.getMailMobile())){
				listBefore.add("MailMobile = " + amsCustomer.getMailMobile());				
				listAfter.add("MailMobile = " + mobileMail);
				amsCustomer.setMailMobile(mobileMail);
			}
			
			//Purpose
			if (finacialAssests != null && !finacialAssests.equals(amsCustomerSurvey.getFinancialAssets())){				
				listBefore.add("FinancialAssets = "+ amsCustomerSurvey.getFinancialAssets());
				listAfter.add("FinancialAssets = " + finacialAssests);
				amsCustomerSurvey.setFinancialAssets(finacialAssests);
			}
			if (purposeInvestFlg != null
					&& !purposeInvestFlg.equals(amsCustomerSurvey.getPurposeShortTermFlg())){
				listBefore.add("PurposeShortTermFlg = " + amsCustomerSurvey.getPurposeShortTermFlg());
				listAfter.add("PurposeShortTermFlg = " + purposeInvestFlg);
				amsCustomerSurvey.setPurposeShortTermFlg(purposeInvestFlg);
			}
			
			if (purposeLongTermFlg != null
					&& !purposeLongTermFlg.equals(amsCustomerSurvey.getPurposeLongTermFlg())){
				listBefore.add("purposeLongTermFlg = " + amsCustomerSurvey.getPurposeLongTermFlg());
				
				listAfter.add("purposeLongTermFlg = " + purposeLongTermFlg);
				amsCustomerSurvey.setPurposeLongTermFlg(purposeLongTermFlg);
			}
			if (purposeExchangeFlg != null
					&& !purposeExchangeFlg.equals(amsCustomerSurvey.getPurposeExchangeFlg())){
				listBefore.add("purposeExchangeFlg = " + amsCustomerSurvey.getPurposeExchangeFlg());
				
				listAfter.add("purposeExchangeFlg = " + purposeExchangeFlg);
				amsCustomerSurvey.setPurposeExchangeFlg(purposeExchangeFlg);
			}
			if (purposeSwapFlg != null
					&& !purposeSwapFlg.equals(amsCustomerSurvey.getPurposeSwapFlg())){
				listBefore.add("purposeSwapFlg = " + amsCustomerSurvey.getPurposeSwapFlg());

				listAfter.add("purposeSwapFlg = " + purposeSwapFlg);
				amsCustomerSurvey.setPurposeSwapFlg(purposeSwapFlg);
			}
			if (purposeHedgeAssetFlg != null
					&& !purposeHedgeAssetFlg.equals(amsCustomerSurvey.getPurposeHedgeAssetFlg())){
				listBefore.add("purposeHedgeAssetFlg = " + amsCustomerSurvey.getPurposeHedgeAssetFlg());

				listAfter.add("purposeHedgeAssetFlg = " + purposeHedgeAssetFlg);
				amsCustomerSurvey.setPurposeHedgeAssetFlg(purposeHedgeAssetFlg);
			}
			if (purposeHighIntFlg != null
					&& !purposeHighIntFlg.equals(amsCustomerSurvey.getPurposeHighIntFlg())){
				listBefore.add("purposeHighIntFlg = " + amsCustomerSurvey.getPurposeHighIntFlg());

				listAfter.add("purposeHighIntFlg = " + purposeHighIntFlg);
				amsCustomerSurvey.setPurposeHighIntFlg(purposeHighIntFlg);
			}
			if (purposeEconomicFlg != null
					&& !purposeEconomicFlg.equals(amsCustomerSurvey.getPurposeEconomicFlg())){
				listBefore.add("purposeEconomicFlg = " + amsCustomerSurvey.getPurposeEconomicFlg());
				listAfter.add("purposeEconomicFlg = " + purposeEconomicFlg);
				amsCustomerSurvey.setPurposeEconomicFlg(purposeEconomicFlg);
			}
			
			//BoInvestmentPurpose
			Integer boPurposeShortTermFlg = Converter.convertFlg(customerInfo.isBoPurposeShortTermFlg());
			if (!boPurposeShortTermFlg.equals(amsCustomerSurvey.getBoPurposeShortTermFlg())) {
				listBefore.add("BoPurposeShortTermFlg = " + amsCustomerSurvey.getBoPurposeShortTermFlg());
				listAfter.add("BoPurposeShortTermFlg = " + boPurposeShortTermFlg);
				amsCustomerSurvey.setBoPurposeShortTermFlg(boPurposeShortTermFlg);
			}
			
			Integer boPurposeDispAssetMngFlg = Converter.convertFlg(customerInfo.isBoPurposeDispAssetMngFlg());
			if (!boPurposeDispAssetMngFlg.equals(amsCustomerSurvey.getBoPurposeDispAssetMngFlg())) {
				listBefore.add("BoPurposeDispAssetMngFlg = " + amsCustomerSurvey.getBoPurposeDispAssetMngFlg());
				listAfter.add("BoPurposeDispAssetMngFlg = " + boPurposeDispAssetMngFlg);
				amsCustomerSurvey.setBoPurposeDispAssetMngFlg(boPurposeDispAssetMngFlg);
			}
			
			Integer boPurposeHedgeFlg = Converter.convertFlg(customerInfo.isBoPurposeHedgeFlg());
			if (!boPurposeHedgeFlg.equals(amsCustomerSurvey.getBoPurposeHedgeFlg())) {
				listBefore.add("BoPurposeHedgeFlg = " + amsCustomerSurvey.getBoPurposeHedgeFlg());
				listAfter.add("BoPurposeHedgeFlg = " + boPurposeHedgeFlg);
				amsCustomerSurvey.setBoPurposeHedgeFlg(boPurposeHedgeFlg);
			}
			
			//[TRSGAP-1277-theln]Jul 25, 2016A - Start 
			Integer boPurposeHedgeType = customerInfo.getBoPurposeHedgeType();
			if (boPurposeHedgeType != null && !boPurposeHedgeType.equals(amsCustomerSurvey.getBoPurposeHedgeType())) {
				if (!(boPurposeHedgeType == -1 && amsCustomerSurvey.getBoPurposeHedgeType() == null)) { //Don't insert trace if boPurposeHedgeType == -1 && amsCustomerSurvey.getBoPurposeHedgeType() == null 
					listBefore.add("BoPurposeHedgeType = " + amsCustomerSurvey.getBoPurposeHedgeType());
					listAfter.add("BoPurposeHedgeType = " + boPurposeHedgeType);
					amsCustomerSurvey.setBoPurposeHedgeType(boPurposeHedgeType);
				}
			}
			
			Integer boPurposeHedgeAmount = customerInfo.getBoPurposeHedgeAmount();
			if (boPurposeHedgeAmount != null && !boPurposeHedgeAmount.equals(amsCustomerSurvey.getBoPurposeHedgeAmount())) {
				if (!(boPurposeHedgeAmount == -1 && amsCustomerSurvey.getBoPurposeHedgeAmount() == null)) { //Don't insert trace if boPurposeHedgeAmount == -1 && amsCustomerSurvey.getBoPurposeHedgeAmount() == null
					listBefore.add("BoPurposeHedgeAmount = " + amsCustomerSurvey.getBoPurposeHedgeAmount());
					listAfter.add("BoPurposeHedgeAmount = " + boPurposeHedgeAmount);
					amsCustomerSurvey.setBoPurposeHedgeAmount(boPurposeHedgeAmount);
				}
			}
			//[TRSGAP-1277-theln]Jul 25, 2016A - End
			
			BigDecimal boMaxLossAmount = customerInfo.getBoMaxLossAmount();
			if (boMaxLossAmount != null && !boMaxLossAmount.equals(amsCustomerSurvey.getBoMaxLossAmount())) {
				listBefore.add("BoMaxLossAmount = " + amsCustomerSurvey.getBoMaxLossAmount());
				listAfter.add("BoMaxLossAmount = " + boMaxLossAmount);
				amsCustomerSurvey.setBoMaxLossAmount(boMaxLossAmount);
			}
			
			// For individual customer
			if(customerInfo.getCorporationType() == 0){
				// Now updated by CS
				// Mail main
				if (mailMain != null
						&& !mailMain.equals(amsCustomer.getLoginId())){
					isChangeLoginId = true;
					customerInfo.setOldLoginId(amsCustomer.getLoginId());
					listBefore.add("LoginId = " + amsCustomer.getLoginId());
					listAfter.add("LoginId = " + mailMain);
					amsCustomer.setLoginId(mailMain);
				}
				
				if (mailMain != null && !mailMain.equals(amsCustomer.getMailMain())){
					isChangeMail = true;
					listBefore.add("MailMain = " + amsCustomer.getMailMain());
					listAfter.add("MailMain = " + mailMain);
					amsCustomer.setMailMain(mailMain);
				}
			}else{
				// For corporation customer
				// Mail main
				if (mailMain != null && !mailMain.equals(amsCustomer.getLoginId())){
					isChangeLoginId = true;
					customerInfo.setOldLoginId(amsCustomer.getLoginId());
					listBefore.add("LoginId = " + amsCustomer.getLoginId());
					
					listAfter.add("LoginId = " + mailMain);
					amsCustomer.setLoginId(mailMain);
				}
				
				if (mailMain != null && !mailMain.equals(amsCustomer.getMailMain())){
					isChangeMail = true;
					listBefore.add("MailMain = " + amsCustomer.getMailMain());
					
					listAfter.add("MailMain = " + mailMain);
					amsCustomer.setMailMain(mailMain);
				}
				
				//PIC Contact
				if (mailMain != null && !mailMain.equals(amsCustomer.getCorpPicMailPc())){					
					listBefore.add("CorpPicMailPC = " + amsCustomer.getCorpPicMailPc());
					
					listAfter.add("CorpPicMailPC = " + mailMain);
					amsCustomer.setCorpPicMailPc(mailMain);
				}
				
				String corpPicTel = customerInfo.getCorpPicTel();
				if (!StringUtils.isBlank(corpPicTel) && corpPicTel != null && !corpPicTel.equals(amsCustomer.getCorpPicTel())){
					listBefore.add("corpPicTel = " + amsCustomer.getCorpPicTel());
				
					listAfter.add("corpPicTel = " + corpPicTel);
					amsCustomer.setCorpPicTel(corpPicTel);
				}
				
				String corppictel = customerInfo.getCorpPicTel();
				if (corppictel != null && !corppictel.equals(amsCustomer.getCorpPicTel())) {
					listBefore.add("CorpPicTel = " + amsCustomer.getCorpPicTel());
					listAfter.add("CorpPicTel = " + corppictel);
					amsCustomer.setCorpPicTel(corppictel);
				}
				
				buildBeneficOwnerTraceInfo(customerInfo, amsCustomerSurvey, listBefore, listAfter);
				
				//Importan: this function must below buildBeneficOwnerTraceInfo()
                buildBeneficOwner(customerInfo, amsCustomerSurvey);

			}
            if (listAfter.size() > 0)
                amsCustomerSurvey.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			amsCustomer.setAmsCustomerSurvey(amsCustomerSurvey);
			amsCustomer.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			
			amsCustomerSurveyDAO.merge(amsCustomerSurvey);
			//[NTS1.0-le.hong.ha]Apr 17, 2013A - End
			
			iAmsCustomerDAO.merge(amsCustomer);
			
			// Insert into AMS_CUSTOMER_TRACE
			if (listAfter.size() > 0) {
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
					
				AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
				AmsCustomer customer = new AmsCustomer();
				customer.setCustomerId(customerInfo.getCustomerId());
				amsCustomerTrace.setServiceType(IConstants.SERVICES_TYPE.AMS);
				amsCustomerTrace.setAmsCustomer(customer);
				amsCustomerTrace
						.setReason("Update Customer Information,CustomerId "
								+ customerInfo.getCustomerId());
				amsCustomerTrace.setNote1("");
				amsCustomerTrace.setNote2("");
				amsCustomerTrace.setValue1(beforeValue.toString());
				amsCustomerTrace.setValue2(afterValue.toString());
				amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerTrace.setOperationFullname(amsCustomer.getFullName());
				amsCustomerTrace.setChangeTime(new Timestamp(System
						.currentTimeMillis()));
				getiAmsCustomerTraceDAO().save(amsCustomerTrace);
			}
			
			//Need reload CustomerAuthen to Redis
			if(isChangeLoginId)
				customerInfo.setNeedReloadCache(true);
			
			if (isChangeMail){
				log.info("Mail main is changed, newMailMain: " + mailMain);
				
				if(serviceFx.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSIT_WAITING
						|| serviceFx.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSITED
						|| serviceFx.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_TRADED
						|| serviceCopyTrade.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSIT_WAITING
						|| serviceCopyTrade.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSITED
						|| serviceCopyTrade.getCustomerServiceStatus().intValue() == ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_TRADED){
					
					String pwLabel = MasterDataManagerImpl.getInstance().getText("post_document.label.login_pass");
					Map<String, Object> map = new HashMap<String, Object>();
					map.putAll(SystemProperty.getInstance().getMap());
					LinkedHashMap<String, String> mapLabel = getTextStringMap(MasterDataManagerImpl.getInstance().getImmutableData().getMapPostDocumentLabel());
					map.put(ITrsConstants.SYSTEM_CONFIG_KEY.IMMUTABLE_DATA + ITrsConstants.JASPER_TEMPLATE.LABEL_MAP, mapLabel);
					postDocumentManager.setCache(map);
					String filePath = propsConfig.getProperty(PATH_JASPER_OUTPUT);
					String picFullNameEtx = MasterDataManagerImpl.getInstance().getText(ITrsConstants.PIC_FULLNAME_EXTEND);
					
					postDocumentManager.updatePostDocument(amsCustomer, serviceFx.getCustomerServiceId(), 
							serviceCopyTrade.getCustomerServiceId(), pwLabel, isChangeMail, filePath, picFullNameEtx, isPublicMode);
				} else
					log.info("service Copy Trade is NOT OPEN_COMPLETED. Don't have to update postDocumentManager");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}

		return true;
	}

    private void buildBeneficOwner(CustomerInfo customerInfo, AmsCustomerSurvey amsCustomerSurvey) {
        // BeneficOwner 1
        amsCustomerSurvey.setBeneficOwnerFlg(customerInfo.getBeneficOwnerFlg());
        amsCustomerSurvey.setBeneficOwnerFullname(customerInfo.getBeneficOwnerFullname());
        amsCustomerSurvey.setBeneficOwnerFullnameKana(customerInfo.getBeneficOwnerFullnameKana());
        amsCustomerSurvey.setBeneficOwnerEstablishDate(customerInfo.getBeneficOwnerEstablishDate());
        amsCustomerSurvey.setBeneficOwnerZipcode(customerInfo.getBeneficOwnerZipcode());
        amsCustomerSurvey.setBeneficOwnerPrefecture(customerInfo.getBeneficOwnerPrefecture());
        amsCustomerSurvey.setBeneficOwnerSection(customerInfo.getBeneficOwnerSection());
        amsCustomerSurvey.setBeneficOwnerCity(customerInfo.getBeneficOwnerCity());
        amsCustomerSurvey.setBeneficOwnerBuildingName(customerInfo.getBeneficOwnerBuildingName());
        amsCustomerSurvey.setBeneficOwnerTel(customerInfo.getBeneficOwnerTel());
        amsCustomerSurvey.setBeneficOwnerFirstname(customerInfo.getBeneficOwnerFirstname());
        amsCustomerSurvey.setBeneficOwnerLastname(customerInfo.getBeneficOwnerLastname());
        amsCustomerSurvey.setBeneficOwnerFirstnameKana(customerInfo.getBeneficOwnerFirstnameKana());
        amsCustomerSurvey.setBeneficOwnerLastnameKana(customerInfo.getBeneficOwnerLastnameKana());

        // BeneficOwner 2
        amsCustomerSurvey.setBeneficOwnerFlg2(customerInfo.getBeneficOwnerFlg2());
        amsCustomerSurvey.setBeneficOwnerFullname2(customerInfo.getBeneficOwnerFullname2());
        amsCustomerSurvey.setBeneficOwnerFullnameKana2(customerInfo.getBeneficOwnerFullnameKana2());
        amsCustomerSurvey.setBeneficOwnerEstablishDate2(customerInfo.getBeneficOwnerEstablishDate2());
        amsCustomerSurvey.setBeneficOwnerZipcode2(customerInfo.getBeneficOwnerZipcode2());
        amsCustomerSurvey.setBeneficOwnerPrefecture2(customerInfo.getBeneficOwnerPrefecture2());
        amsCustomerSurvey.setBeneficOwnerSection2(customerInfo.getBeneficOwnerSection2());
        amsCustomerSurvey.setBeneficOwnerCity2(customerInfo.getBeneficOwnerCity2());
        amsCustomerSurvey.setBeneficOwnerBuildingName2(customerInfo.getBeneficOwnerBuildingName2());
        amsCustomerSurvey.setBeneficOwnerTel2(customerInfo.getBeneficOwnerTel2());
        amsCustomerSurvey.setBeneficOwnerFirstname2(customerInfo.getBeneficOwnerFirstname2());
        amsCustomerSurvey.setBeneficOwnerLastname2(customerInfo.getBeneficOwnerLastname2());
        amsCustomerSurvey.setBeneficOwnerFirstnameKana2(customerInfo.getBeneficOwnerFirstnameKana2());
        amsCustomerSurvey.setBeneficOwnerLastnameKana2(customerInfo.getBeneficOwnerLastnameKana2());

        // BeneficOwner 3
        amsCustomerSurvey.setBeneficOwnerFlg3(customerInfo.getBeneficOwnerFlg3());
        amsCustomerSurvey.setBeneficOwnerFullname3(customerInfo.getBeneficOwnerFullname3());
        amsCustomerSurvey.setBeneficOwnerFullnameKana3(customerInfo.getBeneficOwnerFullnameKana3());
        amsCustomerSurvey.setBeneficOwnerEstablishDate3(customerInfo.getBeneficOwnerEstablishDate3());
        amsCustomerSurvey.setBeneficOwnerZipcode3(customerInfo.getBeneficOwnerZipcode3());
        amsCustomerSurvey.setBeneficOwnerPrefecture3(customerInfo.getBeneficOwnerPrefecture3());
        amsCustomerSurvey.setBeneficOwnerSection3(customerInfo.getBeneficOwnerSection3());
        amsCustomerSurvey.setBeneficOwnerCity3(customerInfo.getBeneficOwnerCity3());
        amsCustomerSurvey.setBeneficOwnerBuildingName3(customerInfo.getBeneficOwnerBuildingName3());
        amsCustomerSurvey.setBeneficOwnerTel3(customerInfo.getBeneficOwnerTel3());
        amsCustomerSurvey.setBeneficOwnerFirstname3(customerInfo.getBeneficOwnerFirstname3());
        amsCustomerSurvey.setBeneficOwnerLastname3(customerInfo.getBeneficOwnerLastname3());
        amsCustomerSurvey.setBeneficOwnerFirstnameKana3(customerInfo.getBeneficOwnerFirstnameKana3());
        amsCustomerSurvey.setBeneficOwnerLastnameKana3(customerInfo.getBeneficOwnerLastnameKana3());
    }
    
    private void buildBeneficOwnerTraceInfo(CustomerInfo customerInfo, AmsCustomerSurvey amsCustomerSurvey, List<String> listBefore, List<String> listAfter) {
    	//Benefic 1
		Integer beneficOwnerFlg = customerInfo.getBeneficOwnerFlg();
		String beneficOwnerFullname = customerInfo.getBeneficOwnerFullname();
		String beneficOwnerFullnameKana = customerInfo.getBeneficOwnerFullnameKana();
		String beneficOwnerEstablishDate = customerInfo.getBeneficOwnerEstablishDate();
		String beneficOwnerZipcode = customerInfo.getBeneficOwnerZipcode();
		String beneficOwnerPrefecture = customerInfo.getBeneficOwnerPrefecture();
		String beneficOwnerCity = customerInfo.getBeneficOwnerCity();
		String beneficOwnerSection = customerInfo.getBeneficOwnerSection();
		String beneficOwnerBuildingName = customerInfo.getBeneficOwnerBuildingName();
		String beneficOwnerTel = customerInfo.getBeneficOwnerTel();
		String beneficOwnerFirstname = customerInfo.getBeneficOwnerFirstname();
		String beneficOwnerLastname = customerInfo.getBeneficOwnerLastname();
		String beneficOwnerFirstnameKana = customerInfo.getBeneficOwnerFirstnameKana();
		String beneficOwnerLastnameKana = customerInfo.getBeneficOwnerLastnameKana();
		//Benefic 2
		Integer beneficOwnerFlg2 = customerInfo.getBeneficOwnerFlg2();
		String beneficOwnerFullname2 = customerInfo.getBeneficOwnerFullname2();
		String beneficOwnerFullnameKana2 = customerInfo.getBeneficOwnerFullnameKana2();
		String beneficOwnerEstablishDate2 = customerInfo.getBeneficOwnerEstablishDate2();
		String beneficOwnerZipcode2 = customerInfo.getBeneficOwnerZipcode2();
		String beneficOwnerPrefecture2 = customerInfo.getBeneficOwnerPrefecture2();
		String beneficOwnerCity2 = customerInfo.getBeneficOwnerCity2();
		String beneficOwnerSection2 = customerInfo.getBeneficOwnerSection2();
		String beneficOwnerBuildingName2 = customerInfo.getBeneficOwnerBuildingName2();
		//Benefic 3
		Integer beneficOwnerFlg3 = customerInfo.getBeneficOwnerFlg3();
		String beneficOwnerFullname3 = customerInfo.getBeneficOwnerFullname3();
		String beneficOwnerFullnameKana3 = customerInfo.getBeneficOwnerFullnameKana3();
		String beneficOwnerEstablishDate3 = customerInfo.getBeneficOwnerEstablishDate3();
		String beneficOwnerZipcode3 = customerInfo.getBeneficOwnerZipcode3();
		String beneficOwnerPrefecture3 = customerInfo.getBeneficOwnerPrefecture3();
		String beneficOwnerCity3 = customerInfo.getBeneficOwnerCity3();
		String beneficOwnerSection3 = customerInfo.getBeneficOwnerSection3();
		String beneficOwnerBuildingName3 = customerInfo.getBeneficOwnerBuildingName3();
		
		//Benefic 1
		if (beneficOwnerFlg != null && !beneficOwnerFlg.equals(amsCustomerSurvey.getBeneficOwnerFlg())) {
			listBefore.add("beneficOwnerFlg = " + amsCustomerSurvey.getBeneficOwnerFlg());
			listAfter.add("beneficOwnerFlg = " + beneficOwnerFlg);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullname) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullname()))
				&& !beneficOwnerFullname.equals(amsCustomerSurvey.getBeneficOwnerFullname())){
			listBefore.add("beneficOwnerFullname = " + amsCustomerSurvey.getBeneficOwnerFullname());
			listAfter.add("beneficOwnerFullname = " + beneficOwnerFullname);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullnameKana) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullnameKana()))
				&& !beneficOwnerFullnameKana.equals(amsCustomerSurvey.getBeneficOwnerFullnameKana())){
			listBefore.add("beneficOwnerFullnameKana = " + amsCustomerSurvey.getBeneficOwnerFullnameKana());
			listAfter.add("beneficOwnerFullnameKana = " + beneficOwnerFullnameKana);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerEstablishDate) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerEstablishDate()))
				&& !beneficOwnerEstablishDate.equals(amsCustomerSurvey.getBeneficOwnerEstablishDate())){
			listBefore.add("beneficOwnerEstablishDate = " + amsCustomerSurvey.getBeneficOwnerEstablishDate());
			listAfter.add("beneficOwnerEstablishDate = " + beneficOwnerEstablishDate);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerZipcode) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerZipcode()))
				&& !beneficOwnerZipcode.equals(amsCustomerSurvey.getBeneficOwnerZipcode())){
			listBefore.add("beneficOwnerZipcode = " + amsCustomerSurvey.getBeneficOwnerZipcode());
			listAfter.add("beneficOwnerZipcode = " + beneficOwnerZipcode);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerPrefecture) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerPrefecture()))
				&& !beneficOwnerPrefecture.equals(amsCustomerSurvey.getBeneficOwnerPrefecture())){
			listBefore.add("beneficOwnerPrefecture = " + amsCustomerSurvey.getBeneficOwnerPrefecture());
			listAfter.add("beneficOwnerPrefecture = " + beneficOwnerPrefecture);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerCity) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerCity()))
				&& !beneficOwnerCity.equals(amsCustomerSurvey.getBeneficOwnerCity())){
			listBefore.add("beneficOwnerCity = " + amsCustomerSurvey.getBeneficOwnerCity());
			listAfter.add("beneficOwnerCity = " + beneficOwnerCity);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerSection) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerSection()))
				&& !beneficOwnerSection.equals(amsCustomerSurvey.getBeneficOwnerSection())){
			listBefore.add("beneficOwnerSection = " + amsCustomerSurvey.getBeneficOwnerSection());
			listAfter.add("beneficOwnerSection = " + beneficOwnerSection);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerBuildingName) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerBuildingName()))
				&& !beneficOwnerBuildingName.equals(amsCustomerSurvey.getBeneficOwnerBuildingName())){
			listBefore.add("beneficOwnerBuildingName = " + amsCustomerSurvey.getBeneficOwnerBuildingName());
			listAfter.add("beneficOwnerBuildingName = " + beneficOwnerBuildingName);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerTel) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerTel()))
				&& !beneficOwnerTel.equals(amsCustomerSurvey.getBeneficOwnerTel())){
			listBefore.add("beneficOwnerTel = " + amsCustomerSurvey.getBeneficOwnerTel());
			listAfter.add("beneficOwnerTel = " + beneficOwnerTel);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFirstname) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFirstname()))
				&& !beneficOwnerFirstname.equals(amsCustomerSurvey.getBeneficOwnerFirstname())){
			listBefore.add("beneficOwnerFirstname = " + amsCustomerSurvey.getBeneficOwnerFirstname());
			listAfter.add("beneficOwnerFirstname = " + beneficOwnerFirstname);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerLastname) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerLastname()))
				&& !beneficOwnerLastname.equals(amsCustomerSurvey.getBeneficOwnerLastname())){
			listBefore.add("beneficOwnerLastname = " + amsCustomerSurvey.getBeneficOwnerLastname());
			listAfter.add("beneficOwnerLastname = " + beneficOwnerLastname);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFirstnameKana) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFirstnameKana()))
				&& !beneficOwnerFirstnameKana.equals(amsCustomerSurvey.getBeneficOwnerFirstnameKana())){
			listBefore.add("beneficOwnerFirstnameKana = " + amsCustomerSurvey.getBeneficOwnerFirstnameKana());
			listAfter.add("beneficOwnerFirstnameKana = " + beneficOwnerFirstnameKana);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerLastnameKana) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerLastnameKana()))
				&& !beneficOwnerLastnameKana.equals(amsCustomerSurvey.getBeneficOwnerLastnameKana())){
			listBefore.add("beneficOwnerLastnameKana = " + amsCustomerSurvey.getBeneficOwnerLastnameKana());
			listAfter.add("beneficOwnerLastnameKana = " + beneficOwnerLastnameKana);
		}
		
		//Benefic 2
		if (beneficOwnerFlg2 != null && !beneficOwnerFlg2.equals(amsCustomerSurvey.getBeneficOwnerFlg2())){
			listBefore.add("beneficOwnerFlg2 = " + amsCustomerSurvey.getBeneficOwnerFlg2());
			listAfter.add("beneficOwnerFlg2 = " + beneficOwnerFlg2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullname2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullname2()))
				&& !beneficOwnerFullname2.equals(amsCustomerSurvey.getBeneficOwnerFullname2())){
			listBefore.add("beneficOwnerFullname2 = " + amsCustomerSurvey.getBeneficOwnerFullname2());
			listAfter.add("beneficOwnerFullname2 = " + beneficOwnerFullname2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullnameKana2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullnameKana2()))
				&& !beneficOwnerFullnameKana2.equals(amsCustomerSurvey.getBeneficOwnerFullnameKana2())){
			listBefore.add("beneficOwnerFullnameKana2 = " + amsCustomerSurvey.getBeneficOwnerFullnameKana2());
			listAfter.add("beneficOwnerFullnameKana2 = " + beneficOwnerFullnameKana2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerEstablishDate2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerEstablishDate2()))
				&& !beneficOwnerEstablishDate2.equals(amsCustomerSurvey.getBeneficOwnerEstablishDate2())){
			listBefore.add("beneficOwnerEstablishDate2 = " + amsCustomerSurvey.getBeneficOwnerEstablishDate2());
			listAfter.add("beneficOwnerEstablishDate2 = " + beneficOwnerEstablishDate2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerZipcode2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerZipcode2()))
				&& !beneficOwnerZipcode2.equals(amsCustomerSurvey.getBeneficOwnerZipcode2())){
			listBefore.add("beneficOwnerZipcode2 = " + amsCustomerSurvey.getBeneficOwnerZipcode2());
			listAfter.add("beneficOwnerZipcode2 = " + beneficOwnerZipcode2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerPrefecture2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerPrefecture2()))
				&& !beneficOwnerPrefecture2.equals(amsCustomerSurvey.getBeneficOwnerPrefecture2())){
			listBefore.add("beneficOwnerPrefecture2 = " + amsCustomerSurvey.getBeneficOwnerPrefecture2());
			listAfter.add("beneficOwnerPrefecture2 = " + beneficOwnerPrefecture2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerCity2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerCity2()))
				&& !beneficOwnerCity2.equals(amsCustomerSurvey.getBeneficOwnerCity2())){
			listBefore.add("beneficOwnerCity2 = " + amsCustomerSurvey.getBeneficOwnerCity2());
			listAfter.add("beneficOwnerCity2 = " + beneficOwnerCity2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerSection2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerSection2()))
				&& !beneficOwnerSection2.equals(amsCustomerSurvey.getBeneficOwnerSection2())){
			listBefore.add("beneficOwnerSection2 = " + amsCustomerSurvey.getBeneficOwnerSection2());
			listAfter.add("beneficOwnerSection2 = " + beneficOwnerSection2);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerBuildingName2) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerBuildingName2()))
				&& !beneficOwnerBuildingName2.equals(amsCustomerSurvey.getBeneficOwnerBuildingName2())){
			listBefore.add("beneficOwnerBuildingName2 = " + amsCustomerSurvey.getBeneficOwnerBuildingName2());
			listAfter.add("beneficOwnerBuildingName2 = " + beneficOwnerBuildingName2);
		}
		
		//Benefic 3
		if (beneficOwnerFlg3 != null && !beneficOwnerFlg3.equals(amsCustomerSurvey.getBeneficOwnerFlg3())){
			listBefore.add("beneficOwnerFlg3 = " + amsCustomerSurvey.getBeneficOwnerFlg3());
			listAfter.add("beneficOwnerFlg3 = " + beneficOwnerFlg3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullname3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullname3()))
				&& !beneficOwnerFullname3.equals(amsCustomerSurvey.getBeneficOwnerFullname3())){
			listBefore.add("beneficOwnerFullname3 = " + amsCustomerSurvey.getBeneficOwnerFullname3());
			listAfter.add("beneficOwnerFullname3 = " + beneficOwnerFullname3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerFullnameKana3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerFullnameKana3()))
				&& !beneficOwnerFullnameKana3.equals(amsCustomerSurvey.getBeneficOwnerFullnameKana3())){
			listBefore.add("beneficOwnerFullnameKana3 = " + amsCustomerSurvey.getBeneficOwnerFullnameKana3());
			listAfter.add("beneficOwnerFullnameKana3 = " + beneficOwnerFullnameKana3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerEstablishDate3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerEstablishDate3()))
				&& !beneficOwnerEstablishDate3.equals(amsCustomerSurvey.getBeneficOwnerEstablishDate3())){
			listBefore.add("beneficOwnerEstablishDate3 = " + amsCustomerSurvey.getBeneficOwnerEstablishDate3());
			listAfter.add("beneficOwnerEstablishDate3 = " + beneficOwnerEstablishDate3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerZipcode3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerZipcode3()))
				&& !beneficOwnerZipcode3.equals(amsCustomerSurvey.getBeneficOwnerZipcode3())){
			listBefore.add("beneficOwnerZipcode3 = " + amsCustomerSurvey.getBeneficOwnerZipcode3());
			listAfter.add("beneficOwnerZipcode3 = " + beneficOwnerZipcode3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerPrefecture3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerPrefecture3()))
				&& !beneficOwnerPrefecture3.equals(amsCustomerSurvey.getBeneficOwnerPrefecture3())){
			listBefore.add("beneficOwnerPrefecture3 = " + amsCustomerSurvey.getBeneficOwnerPrefecture3());
			listAfter.add("beneficOwnerPrefecture3 = " + beneficOwnerPrefecture3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerCity3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerCity3()))
				&& !beneficOwnerCity3.equals(amsCustomerSurvey.getBeneficOwnerCity3())){
			listBefore.add("beneficOwnerCity3 = " + amsCustomerSurvey.getBeneficOwnerCity3());
			listAfter.add("beneficOwnerCity3 = " + beneficOwnerCity3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerSection3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerSection3()))
				&& !beneficOwnerSection3.equals(amsCustomerSurvey.getBeneficOwnerSection3())){
			listBefore.add("beneficOwnerSection3 = " + amsCustomerSurvey.getBeneficOwnerSection3());
			listAfter.add("beneficOwnerSection3 = " + beneficOwnerSection3);
		}
		
		if (!(Utilities.isEmpty(beneficOwnerBuildingName3) && Utilities.isEmpty(amsCustomerSurvey.getBeneficOwnerBuildingName3()))
				&& !beneficOwnerBuildingName3.equals(amsCustomerSurvey.getBeneficOwnerBuildingName3())){
			listBefore.add("beneficOwnerBuildingName3 = " + amsCustomerSurvey.getBeneficOwnerBuildingName3());
			listAfter.add("beneficOwnerBuildingName3 = " + beneficOwnerBuildingName3);
		}
    }

    /**
	 * 　 update password on db
	 * 
	 * @param CustomerInfo
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 13, 2012
	 * @MdDate
	 */
	public Boolean updatePassword(CustomerInfo customerInfo) {
		try {
			String customerId = customerInfo.getCustomerId();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(customerId);
			String newLoginPassword = customerInfo.getNewPassword();
			if (newLoginPassword != null && !StringUtils.isBlank(newLoginPassword)) {
				amsCustomer.setLoginPass(newLoginPassword);
				String md5NewPassword = Security.MD5(newLoginPassword);
				if (md5NewPassword != null && !StringUtils.isBlank(md5NewPassword)) {
					amsCustomer.setLoginPass(md5NewPassword);
				}
				getiAmsCustomerDAO().merge(amsCustomer);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}

		return true;
	}

	@Override
	public LinkedHashMap<String, String> getListCountry() {
		log.info("[Start] get list country in system");
		LinkedHashMap<String, String> listCountry = null;
		try {
			List<AmsSysCountry> listCountries = new ArrayList<AmsSysCountry>();
			listCountries = getiAmsSysCountryDAO().findByActiveFlg(
					IConstants.ACTIVE_FLG.ACTIVE, "countryName");
			listCountry = new LinkedHashMap<String, String>();
			if (listCountries != null && listCountries.size() > 0) {
				for (AmsSysCountry amsSysCountry : listCountries) {
					listCountry.put(amsSysCountry.getCountryId().toString(),
							amsSysCountry.getCountryName());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] get list country in system");
		return listCountry;
	}

	public void sendMailResetPassword(String mt4Id, String mt4Password,
			String fullName, String email, String subject, String template) {
		MailInfo mail = new MailInfo();
		mail.setMt4Id(mt4Id);
		mail.setMt4Password(mt4Password);
		if (fullName == null) {
			fullName = "Customer";
		}
		mail.setEmailAddress(email);
		mail.setFullName(fullName);
		try {
			mailService.sendAppMail(mail, email,
					AppConfiguration.getMailAdminSender(), subject, template);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * @param mailService
	 *            the mailService to set
	 */
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> getiAmsCustomerCreditcardDAO() {
		return iAmsCustomerCreditcardDAO;
	}

	public void setiAmsCustomerCreditcardDAO(
			IAmsCustomerCreditcardDAO<AmsCustomerCreditcard> iAmsCustomerCreditcardDAO) {
		this.iAmsCustomerCreditcardDAO = iAmsCustomerCreditcardDAO;
	}

	/**
	 * Get creditCustomer list
	 * 
	 * @param customerId
	 * @param ewalletType
	 * @return
	 */
	public List<CreditCardInfo> getCreditCardList(String customerId, String publicKey) {
		List<AmsCustomerCreditcard> listAmsCustomerCreditcards = null;
		ArrayList<CreditCardInfo> listCreditCards = null;
		try {
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			listAmsCustomerCreditcards = getiAmsCustomerCreditcardDAO().getCreditCustomerHistory(customerId);
			if (listAmsCustomerCreditcards != null
					&& listAmsCustomerCreditcards.size() > 0) {
				listCreditCards = new ArrayList<CreditCardInfo>();
				for (AmsCustomerCreditcard amsCreditcard : listAmsCustomerCreditcards) {
					CreditCardInfo creditCardInfo = new CreditCardInfo();
					BeanUtils.copyProperties(creditCardInfo, amsCreditcard);
					if(!StringUtil.isEmpty(creditCardInfo.getCcNo())){
						creditCardInfo.setCcNo(Cryptography.decrypt(creditCardInfo.getCcNo(), privateKey, publicKey));
					}
					if(!StringUtil.isEmpty(creditCardInfo.getCcCvv())){
						creditCardInfo.setCcCvv(Cryptography.decrypt(creditCardInfo.getCcCvv(), privateKey, publicKey));
					}
						// [NTS1.0-Mai.Thu.Huyen]Oct 6, 2012M - Start checking
					// display card type is null
					String sCardType = "";
					// get CreditCardType
					if (amsCreditcard.getCcType() != null) {
						SysProperty amsSysProperty = getiSysPropertyDAO().getSysProperty(IConstants.SYS_PROPERTY.CARD_TYPE,amsCreditcard.getCcType());
						sCardType = amsSysProperty.getPropertyValue();
					}
					// [NTS1.0-Mai.Thu.Huyen]Oct 6, 2012M - End
					creditCardInfo.setCcTypeName(sCardType);
					listCreditCards.add(creditCardInfo);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listCreditCards;
	}

	/**
	 * Get creditCustomer
	 * 
	 * @param customerId
	 * @param ewalletType
	 * @return
	 */

	/**
	 * Get bankInfo list
	 * 
	 * @param customerId
	 * @return
	 */
	public List<BankTransferInfo> getBankInfo(String customerId) {
		List<AmsCustomerBank> listAmsCustomerBanks = null;
		ArrayList<BankTransferInfo> listBankTransferInfos = null;
		try {
			listAmsCustomerBanks = null;
			if (listAmsCustomerBanks != null && listAmsCustomerBanks.size() > 0) {
				listBankTransferInfos = new ArrayList<BankTransferInfo>();
				for (AmsCustomerBank amsCustomerBank : listAmsCustomerBanks) {
					BankTransferInfo bankTransferInfo = new BankTransferInfo();
					bankTransferInfo.setBankName(amsCustomerBank.getBankName());
					bankTransferInfo.setAccountNumber(amsCustomerBank.getAccountNo());
					bankTransferInfo.setBeneficiaryName(amsCustomerBank.getAccountNameKana());
					
					bankTransferInfo.setBankCode(amsCustomerBank.getBankCode());
					bankTransferInfo.setBranchCode(amsCustomerBank.getBranchCode());
					bankTransferInfo.setBranchName(amsCustomerBank.getBranchName());
					bankTransferInfo.setBankAccClass(amsCustomerBank.getBankAccClass());
					
					listBankTransferInfos.add(bankTransferInfo);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listBankTransferInfos;
	}

	/**
	 * Get bankInfo
	 * 
	 * @param customerId
	 * @return
	 */
	public BankTransferInfo getBankInfo(String customerId, String bankName,	String accNo) {
		AmsCustomerBank amsCustomerBank = null;
		BankTransferInfo bankTransferInfo = null;
		try {
			amsCustomerBank = null;
			if (amsCustomerBank != null) {
				bankTransferInfo = new BankTransferInfo();
				bankTransferInfo.setBankName(amsCustomerBank.getBankName());
				bankTransferInfo.setBranchName(amsCustomerBank.getBranchName());
				bankTransferInfo.setAccountNumber(accNo);
				//[NTS1.0-le.hong.ha]May 14, 2013A - Start 
				bankTransferInfo.setBankAccClass(amsCustomerBank.getBankAccClass());
				bankTransferInfo.setBankCode(amsCustomerBank.getBankCode());
				bankTransferInfo.setBranchCode(amsCustomerBank.getBranchCode());
				//[NTS1.0-le.hong.ha]May 14, 2013A - End
				bankTransferInfo.setBeneficiaryName(amsCustomerBank.getAccountNameKana());
				bankTransferInfo.setSwiftCode(amsCustomerBank.getSwiftCode());
				bankTransferInfo.setBankAddress(amsCustomerBank.getBankAddress());
				bankTransferInfo.setCountryId(amsCustomerBank.getCountryId().toString());
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return bankTransferInfo;
	}

	/**
	 * get Ewallet
	 */

	public CustomerEwalletInfo getEwalletInfo(String customerId,
			String ewalletID, Integer ewalletType, String publicKey) {
		CustomerEwalletInfo customerEwalletInfo = null;
		try {
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			AmsCustomerEwallet amsCustomerEwallet = getiAmsCustomerEwalletDAO().getEwalletInfo(customerId, ewalletID, ewalletType);
			if (amsCustomerEwallet != null) {
				customerEwalletInfo = new CustomerEwalletInfo();
				// [NTS1.0-Quan.Le.Minh]Feb 19, 2013A - Start
				customerEwalletInfo.setEwalletId(amsCustomerEwallet.getEwalletId());
				// [NTS1.0-Quan.Le.Minh]Feb 19, 2013A - End
				if (ewalletType == IConstants.EWALLET_TYPE.NETELLER) {
					customerEwalletInfo.setEwalletAccNo(amsCustomerEwallet.getEwalletAccNo());
					if(!StringUtil.isEmpty(amsCustomerEwallet.getEwalletSecureId())){
						String secureId = Cryptography.decrypt(amsCustomerEwallet.getEwalletSecureId(), privateKey, publicKey);
						customerEwalletInfo.setEwalletSecureId(secureId);
					}
				} else if (ewalletType == IConstants.EWALLET_TYPE.PAYZA) {
					customerEwalletInfo.setEwalletEmail(amsCustomerEwallet.getEwalletEmail());
					String password = Cryptography.decrypt(amsCustomerEwallet.getEwalletApiPassword(), privateKey, publicKey);
					customerEwalletInfo.setEwalletApiPassword(password);
				} else if (ewalletType == IConstants.EWALLET_TYPE.LIBERTY) {
					// [NTS1.0-Nguyen.Manh.Thang]Oct 26, 2012A - Start
					customerEwalletInfo.setEwalletAccNo(amsCustomerEwallet.getEwalletAccNo());
					customerEwalletInfo.setDocVerifyStatus(amsCustomerEwallet.getDocVerifyStatus());
					// [NTS1.0-Nguyen.Manh.Thang]Oct 26, 2012A - End
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerEwalletInfo;
	}

	/**
	 * get CreditCard by customerCCID
	 */
	public CreditCardInfo getCreditCardbyID(Integer customerCCID, String publicKey) {
		AmsCustomerCreditcard amsCustomerCreditcard = null;
		CreditCardInfo creditCardInfo = null;
		try {
			amsCustomerCreditcard = getiAmsCustomerCreditcardDAO().getCreditInfobyId(customerCCID);
			AmsCustomer amsCustomer = getiAmsCustomerDAO().getCustomerInfo(amsCustomerCreditcard.getAmsCustomer().getCustomerId());
			if(amsCustomer != null){
				String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
				if (amsCustomerCreditcard != null) {
					creditCardInfo = new CreditCardInfo();
					BeanUtils.copyProperties(creditCardInfo, amsCustomerCreditcard);
					if(!StringUtil.isEmpty(creditCardInfo.getCcNo())){
						creditCardInfo.setCcNo(Cryptography.decrypt(creditCardInfo.getCcNo(),privateKey, publicKey));
					}
					if(!StringUtil.isEmpty(creditCardInfo.getCcCvv())){
						creditCardInfo.setCcCvv(Cryptography.decrypt(creditCardInfo.getCcCvv(), privateKey, publicKey));
					}
					AmsSysCountry country = amsCustomerCreditcard.getAmsSysCountry();
					if (country != null) {
						creditCardInfo.setCountryId(country.getCountryId());
					}
					String sCardType = "";
					if(amsCustomerCreditcard.getCcType() != null){
						SysProperty amsSysProperty =getiSysPropertyDAO().getSysProperty(IConstants.SYS_PROPERTY.CARD_TYPE, amsCustomerCreditcard.getCcType());
						sCardType = amsSysProperty.getPropertyValue();
					}
					creditCardInfo.setCcTypeName(sCardType);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return creditCardInfo;
	}

	/**
	 * Get countryName from DB
	 */

	public String getCountryName(Integer countryId) {
		String countryName = null;
		try {
			AmsSysCountry amsSysCountry = getiAmsSysCountryDAO().findById(
					AmsSysCountry.class, countryId);
			if (amsSysCountry != null) {
				countryName = amsSysCountry.getCountryName();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return countryName;
	}

	/**
	 * @return the iSysPropertyDAO
	 */
	public ISysPropertyDAO<SysProperty> getiSysPropertyDAO() {
		return iSysPropertyDAO;
	}

	/**
	 * @param iSysPropertyDAO
	 *            the iSysPropertyDAO to set
	 */
	public void setiSysPropertyDAO(ISysPropertyDAO<SysProperty> iSysPropertyDAO) {
		this.iSysPropertyDAO = iSysPropertyDAO;
	}

	public CustomerServicesInfo getCustomerService(String customerId, Integer serviceType) {
		CustomerServicesInfo customerService = null;
		try {
			AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO()
					.findByCustomerIdServiceType(customerId, serviceType);
			
			if(amsCustomerService != null){
				customerService = new CustomerServicesInfo();
				customerService.setCustomerServiceId(amsCustomerService.getCustomerServiceId());
				customerService.setLeverage(amsCustomerService.getLeverage());
				customerService.setSubGroupCode(amsCustomerService.getAmsSubGroup().getSubGroupCode());
				customerService.setCurrencyCode(amsCustomerService.getAmsSubGroup().getCurrencyCode());
				customerService.setSubGroupId(amsCustomerService.getAmsSubGroup().getSubGroupId());
				customerService.setServiceType(amsCustomerService.getServiceType());
				customerService.setAccountOpenDate(amsCustomerService.getAccountOpenDate());
				customerService.setAllowTransactFlg(amsCustomerService.getAllowTransactFlg());
				customerService.setAllowLoginFlg(amsCustomerService.getAllowLoginFlg());
				customerService.setCustomerServiceStatus(amsCustomerService.getCustomerServiceStatus());
				customerService.setNtdAccountId(amsCustomerService.getNtdAccountId());
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerService;
	}
	
	public List<CustomerServicesInfo> getCustomerService (String customerId){
		List<CustomerServicesInfo> listCustomerService = new ArrayList<CustomerServicesInfo>();
		try {
			List<AmsCustomerService> listAmsCustomerService = getiAmsCustomerServiceDAO().getListCustomerServices(customerId);
			for(AmsCustomerService amsCustomerService : listAmsCustomerService){
				CustomerServicesInfo customerService = new CustomerServicesInfo();
				customerService.setCustomerServiceId(amsCustomerService.getCustomerServiceId());
				customerService.setLeverage(amsCustomerService.getLeverage());
				customerService.setSubGroupCode(amsCustomerService.getAmsSubGroup().getSubGroupCode());
				customerService.setCurrencyCode(amsCustomerService.getAmsSubGroup().getCurrencyCode());
				customerService.setSubGroupId(amsCustomerService.getAmsSubGroup().getSubGroupId());
				customerService.setServiceType(amsCustomerService.getServiceType());
				customerService.setAccountOpenDate(amsCustomerService.getAccountOpenDate());
				customerService.setAllowTransactFlg(amsCustomerService.getAllowTransactFlg());
				customerService.setAllowLoginFlg(amsCustomerService.getAllowLoginFlg());
				customerService.setCustomerServiceStatus(amsCustomerService.getCustomerServiceStatus());
				customerService.setNtdAccountId(amsCustomerService.getNtdAccountId());
				
				listCustomerService.add(customerService);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return listCustomerService;
	}

	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDAO() {
		return iAmsCustomerServiceDAO;
	}

	private AmsCustomerModel.CustomerInfo.Builder getNTDCustomerInfo(String customerId, Integer serviceType) {
		AmsCustomer amsCustomer = getAmsCustomer(customerId);
		AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO().findByCustomerIdServiceType(customerId, serviceType);
		
		AmsWhitelabel amsWhitelabel = amsWhitelabelDAO.findById(AmsWhitelabel.class, amsCustomer.getWlCode());
		Integer wlId = amsWhitelabel.getWlID();
		
		AmsSysCountry amsSysCountry = amsCustomer.getAmsSysCountry();
		if (amsSysCountry == null) {
			log.warn("Can not find AmsSysCountry for customerId: " + customerId);
			return null;
		}
		
		boolean isCorp = IConstants.CORPORATION_TYPE.CORPORATION.equals(amsCustomer.getCorporationType());
		
		String language = amsSysCountry.getDefaultLanguage();
		String zipCode = amsCustomer.getZipcode();
		String address = amsCustomer.getPrefecture() + amsCustomer.getCity(); // newAmsCustomer.getAddress();
		String firtName = isCorp ? amsCustomer.getCorpFullname() : amsCustomer.getFirstName();
		String lastName = amsCustomer.getLastName();
		String section = amsCustomer.getSection();
		String buildingName = amsCustomer.getBuildingName();
		
		String newMailmain = amsCustomer.getMailMain();
		String baseCurrency = amsCustomer.getSysCurrency().getCurrencyCode();
		AmsSubGroup subGroup = amsCustomerService.getAmsSubGroup();
		if (subGroup == null) {
			log.warn("Can not get AmsSubGroup for customerId: " + customerId + ", serviceType: " + amsCustomerService.getServiceType());
			return null;
		}
		
		Integer mappingGroupId = subGroup.getMappingGroupId();
		if (mappingGroupId == null) {
			log.warn("MappingGroupId of AmsSubGroup with SubGroupId: " + subGroup.getSubGroupId() + " is null");
			return null;
		}
		
		String ntdCustomerId = amsCustomer.getNtdCustomerId();
		String ntdAccountId = amsCustomerService.getNtdAccountId();
		
		AmsCustomerModel.CustomerInfo.Builder customerInfoBuilder = AmsCustomerModel.CustomerInfo.newBuilder();
		customerInfoBuilder.setLoginId(newMailmain);
		customerInfoBuilder.setMailMain(newMailmain);
		customerInfoBuilder.setNtdCustomerId(ntdCustomerId);
		customerInfoBuilder.setWlId(wlId);
		customerInfoBuilder.setLanguage(language);
		customerInfoBuilder.setZipCode(zipCode);
		customerInfoBuilder.setAddress(address);
		
		if (!isCorp)
			customerInfoBuilder.setLastName(lastName);

		customerInfoBuilder.setFirstName(firtName);
		customerInfoBuilder.setSection(section);
		customerInfoBuilder.setBuilding(buildingName);
		
		customerInfoBuilder.setAccountOpenDate(amsCustomerService.getAccountOpenDate() != null ? amsCustomerService.getAccountOpenDate() : "");
	    customerInfoBuilder.setAccountOpenFinishDate(amsCustomerService.getAccountOpenFinishDate() != null ? amsCustomerService.getAccountOpenFinishDate() : "");
	    customerInfoBuilder.setAgreementFlg(IConstants.AGREEMENT_FLG.AGREE);
		customerInfoBuilder.setNtdAccountId(ntdAccountId);
		customerInfoBuilder.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		customerInfoBuilder.setMappingGroupId(mappingGroupId);
		customerInfoBuilder.setBaseCurrencyCode(baseCurrency);
		customerInfoBuilder.setCustomerType(CustomerType.UPDATE);

		return customerInfoBuilder;
	}

	public void setiAmsCustomerServiceDAO(
			IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO) {
		this.iAmsCustomerServiceDAO = iAmsCustomerServiceDAO;
	}

	public String getAmsGroup(Integer groupId) {
		AmsGroup amsGroup = null;
		String groupName = null;
		try {
			amsGroup = getiAmsGroupDAO().findByAmsGroupId(groupId);
			if (amsGroup != null) {
				groupName = amsGroup.getGroupName();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return groupName;
	}

	public String getIbLink(String customerId) {
		AmsIb amsIb = null;
		String ibLink = null;
		try {
			amsIb = getiAmsIbDAO().getAmsIbByCustomerId(customerId);
			if (amsIb != null)
				ibLink = amsIb.getIbLink();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return ibLink;
	}

	public IAmsIbDAO<AmsIb> getiAmsIbDAO() {
		return iAmsIbDAO;
	}

	public void setiAmsIbDAO(IAmsIbDAO<AmsIb> iAmsIbDAO) {
		this.iAmsIbDAO = iAmsIbDAO;
	}

	public IAmsCustomerTraceDAO<AmsCustomerTrace> getiAmsCustomerTraceDAO() {
		return iAmsCustomerTraceDAO;
	}

	public void setiAmsCustomerTraceDAO(
			IAmsCustomerTraceDAO<AmsCustomerTrace> iAmsCustomerTraceDAO) {
		this.iAmsCustomerTraceDAO = iAmsCustomerTraceDAO;
	}

	public IScCustomerDAO<ScCustomer> getiScCustomerDAO() {
		return iScCustomerDAO;
	}

	public void setiScCustomerDAO(IScCustomerDAO<ScCustomer> iScCustomerDAO) {
		this.iScCustomerDAO = iScCustomerDAO;
	}

	/**
	 * Update Customer Service Status to ACCOUNT_OPEN_REQUESTING
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 21, 2013
	 */
	@Override
	public boolean updateCustomerServiceStatus(String customerId) {
		List<AmsCustomerService> customerServices = iAmsCustomerServiceDAO
				.findByCustomerId(customerId);
		if (customerServices != null) {
			for (AmsCustomerService amsCustomerService : customerServices) {
				amsCustomerService.setCustomerServiceStatus(IConstants.ACCOUNT_OPEN_STATUS.ACCOUNT_OPEN_REQUESTING);
				iAmsCustomerServiceDAO.merge(amsCustomerService);
			}
			return true;
		}
		return false;
	}

	/**
	 * Upload files to server　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 21, 2013
	 */
	@Override
	public Integer uploadFiles(List<FileUploadInfo> filesInfo,
			String customerId, String wlCode, Integer subGroupId) {
		Integer result = FileLoaderUtil.validateFiles(filesInfo);
		String destFolder = getDestinationFolderUpload(customerId, wlCode,
				subGroupId);

		// renameFileUpload(filesInfo);

		if (result.equals(IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.ALLOWED)) {
			try {

				FileLoaderUtil.renameFileUpload(filesInfo);

				for (int i = 0; i < filesInfo.size(); i++) {
//					FileUploadInfo f = filesInfo.get(i);
					// String docId =
					// getiSysUniqueidCounterDAO().generateUniqueId(IConstants.UNIQUE_CONTEXT.FILE_CONTEXT);
					// f.setCustomerDocId(docId);
					/* Upload File */
					result = uploadDocument(filesInfo.get(i));
					if (result.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
						return result;
					}
				}

				/* Insert new Doc info to DB */
				result = insertAmsCustomerDoc(filesInfo, destFolder);

				for (FileUploadInfo f : filesInfo) {
					FileLoaderUtil.copyFile(f, destFolder);
				}
				AmsCustomer amsCustomer = iAmsCustomerDAO.findById(AmsCustomer.class, customerId);
				if (amsCustomer != null) {
					sendMailToCsTeam(customerId, amsCustomer.getFullName(), IConstants.UPLOAD_DOCUMENT.DOC_TYPE_DISP.DOCUMENT, wlCode);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				result = IConstants.UPLOAD_DOCUMENT.FAIL;
			}
		}

		return result;
	}

	/**
	 * Change verify doc status when uploaded
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 24, 2013
	 */
	@Override
	public boolean updateCustomerDocStatus(String customerId, Integer docType) {
		AmsCustomer customer = iAmsCustomerDAO.findById(AmsCustomer.class,
				customerId);
		if (customer != null) {
			if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.ADDRESS.equals(docType)) {
				customer.setVerifyAddressStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
			} else if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.PASSPORT
					.equals(docType)) {
				customer.setVerifyPassportStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
			} else if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.SIGNATURE
					.equals(docType)) {
				customer.setVerifySignatureStatus(IConstants.DOC_VERIFY_STATUS.VERIFYING);
			}

			iAmsCustomerDAO.merge(customer);
			return true;
		}
		return false;
	}

	public ISysUniqueidCounterDAO<SysUniqueidCounter> getiSysUniqueidCounterDAO() {
		return iSysUniqueidCounterDAO;
	}

	public void setiSysUniqueidCounterDAO(
			ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO) {
		this.iSysUniqueidCounterDAO = iSysUniqueidCounterDAO;
	}

	/**
	 * Get Doc Url by DocId
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 18, 2013
	 */
	@Override
	public List<DocumentInfo> getEwalletDocUrl(Integer ewalletId) {
		List<DocumentInfo> result = new ArrayList<DocumentInfo>();
		Integer docId1 = null;
		Integer docId2 = null;
		Integer docId3 = null;

		AmsCustomerEwallet customerEwallet = iAmsCustomerEwalletDAO.findById(
				AmsCustomerEwallet.class, ewalletId);
		if (customerEwallet != null) {
			AmsCustomerDoc customerDoc = null;

			/* DOC 1 */
			if (customerEwallet.getAmsCustomerDoc1() != null) {
				docId1 = customerEwallet.getAmsCustomerDoc1()
						.getCustomerDocId();
				if (docId1 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(
							AmsCustomerDoc.class, docId1);

					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
			/* DOC 2 */
			if (customerEwallet.getAmsCustomerDoc2() != null) {
				docId2 = customerEwallet.getAmsCustomerDoc2()
						.getCustomerDocId();
				if (docId2 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(
							AmsCustomerDoc.class, docId2);

					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
			/* DOC 3 */
			if (customerEwallet.getAmsCustomerDoc3() != null) {
				docId3 = customerEwallet.getAmsCustomerDoc3().getCustomerDocId();
				if (docId3 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(
							AmsCustomerDoc.class, docId3);

					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get Doc Url by DocId
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	@Override
	public List<DocumentInfo> getCcDocUrl(Integer ccId) {
		List<DocumentInfo> result = new ArrayList<DocumentInfo>();
		Integer docId1 = null;
		Integer docId2 = null;
		Integer docId3 = null;

		AmsCustomerCreditcard customerCc = iAmsCustomerCreditcardDAO.findById(
				AmsCustomerCreditcard.class, ccId);
		if (customerCc != null) {
			AmsCustomerDoc customerDoc = null;

			/* DOC 1 */
			if (customerCc.getAmsCustomerDoc1() != null) {
				docId1 = customerCc.getAmsCustomerDoc1().getCustomerDocId();
				if (docId1 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(AmsCustomerDoc.class, docId1);
					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
			/* DOC 2 */
			if (customerCc.getAmsCustomerDoc2() != null) {
				docId2 = customerCc.getAmsCustomerDoc2().getCustomerDocId();
				if (docId2 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(AmsCustomerDoc.class, docId2);
					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
			/* DOC 3 */
			if (customerCc.getAmsCustomerDoc3() != null) {
				docId3 = customerCc.getAmsCustomerDoc3().getCustomerDocId();
				if (docId3 != null) {
					customerDoc = iAmsCustomerDocDAO.findById(AmsCustomerDoc.class, docId3);
					if (customerDoc != null) {
						result.add(toDocumentInfo(customerDoc));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get Docs URL
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	@Override
	public CustomerInfo getDocUrls(String customerId) {
		log.info("getInfo()");
		CustomerInfo info = new CustomerInfo();
		AmsCustomer customer = iAmsCustomerDAO.findById(AmsCustomer.class,
				customerId);

		if (customer == null) {
			return null;
		}

		List<AmsCustomerDoc> listDocs = iAmsCustomerDocDAO.findByCustomerId(customerId);

		List<DocumentInfo> listPassport = new ArrayList<DocumentInfo>();
		List<DocumentInfo> listAddress = new ArrayList<DocumentInfo>();
		List<DocumentInfo> listSignature = new ArrayList<DocumentInfo>();

		// add list docs
		if (listDocs != null) {
			for (AmsCustomerDoc doc : listDocs) {
				if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.PASSPORT.equals(doc
						.getDocType())) {
					listPassport.add(toDocumentInfo(doc));
				} else if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.ADDRESS
						.equals(doc.getDocType())) {
					listAddress.add(toDocumentInfo(doc));
				} else if (IConstants.UPLOAD_DOCUMENT.DOC_TYPE.SIGNATURE
						.equals(doc.getDocType())) {
					listSignature.add(toDocumentInfo(doc));
				}
			}
		}

		// set list docs
		info.setPassportDocs(listPassport);
		info.setAddressDocs(listAddress);
		info.setSignatureDocs(listSignature);

		return info;
	}

	/**
	 * Convert document entity to info
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Feb 19, 2013
	 */
	private DocumentInfo toDocumentInfo(AmsCustomerDoc amsCustomerDoc) {
		DocumentInfo docInfo = new DocumentInfo();
		if (amsCustomerDoc == null) {
			return null;
		}
		docInfo.setCustomerDocId(amsCustomerDoc.getCustomerDocId());
		docInfo.setDocFileName(amsCustomerDoc.getDocFileName());
		docInfo.setDocFileType(amsCustomerDoc.getDocFileType());
		docInfo.setDocUrl(amsCustomerDoc.getDocUrl());

		return docInfo;
	}

	/**
	 * get Social Customer Info by Customer ID
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Feb 28, 2013
	 */
	public CustomerScInfo getCustomerScInfo(String customerId) {
		CustomerScInfo customerScInfo = null;
		ScCustomer scCustomer = null;
		try {
			scCustomer = getiScCustomerDAO().getScCustomer(customerId);
			if (scCustomer != null) {
				customerScInfo = new CustomerScInfo();
				if (scCustomer.getSignalTotalReturn() == null)
					scCustomer.setSignalTotalReturn(new BigDecimal(0));
				if (scCustomer.getSignalTotalPips() == null)
					scCustomer.setSignalTotalPips(new BigDecimal(0));
				if (scCustomer.getSignalTotalTrade() == null)
					scCustomer.setSignalTotalTrade(new BigDecimal(0));
				if (scCustomer.getSignalWinRatio() == null)
					scCustomer.setSignalWinRatio(new BigDecimal(0));
				BeanUtils.copyProperties(customerScInfo, scCustomer);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return customerScInfo;
	}

	/**
	 * upload Avatar
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 1, 2013
	 */
	public Integer uploadAvatar(FileUploadInfo fileUploadInfo, String wlCode) {
		try {
			Integer validate = new Integer(0);
			File srcFile = fileUploadInfo.getFile();
			String fileName = fileUploadInfo.getFileName();
			/* Validate Upload File */
			List<String> fileAllows = new ArrayList<String>();
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.JPG);
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.PNG);
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.GIF);
			validate = FileLoaderUtil.validateFileExtension(fileName, fileAllows);
			if (!validate
					.equals(IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.EXTENSION_NOT_ALLOWED)) {
				if (srcFile.length() > ITrsConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_5M) {
					return IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED;
				}
			} else {
				return IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED;
			}

			renameFileUpload(fileUploadInfo);
			BufferedImage originalImage = ImageIO.read(srcFile);
			BufferedImage resizedImage = resizeImage(originalImage,
					BufferedImage.TYPE_INT_RGB);
			ImageIO.write(resizedImage, "jpg", srcFile);
			fileUploadInfo.setFile(srcFile);

			Integer uploadResult = uploadDocument(fileUploadInfo);
			if (uploadResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}

			// TODO: Fix to test
			// String destPath = getDestinationFolderUpload(customerId, wlCode,
			// subGroupId);
			String destPath = propsConfig.getProperty(URL_AVATAR_FOLDER);
			/*
			 * Integer updateAvatarResult =
			 * updateAvatarIntoScCustomer(fileUploadInfo, destPath); if
			 * (updateAvatarResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
			 * return updateAvatarResult; }
			 */

			Integer copyResult = FileLoaderUtil.copyFile(fileUploadInfo,destPath);
			if (copyResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}

			return IConstants.UPLOAD_DOCUMENT.SUCCESS;
		} catch (Exception ex) {
			ex.printStackTrace();
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
	}

	public Integer uploadCropAvatar(FileUploadInfo fileUploadInfo, String wlCode) {
		try {
			Integer validate = new Integer(0);
			File srcFile = fileUploadInfo.getFile();
			String fileName = fileUploadInfo.getFileTempName();
			/* Validate Upload File */
			List<String> fileAllows = new ArrayList<String>();
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.JPG);
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.PNG);
			fileAllows.add(IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.GIF);
			validate = FileLoaderUtil.validateFileExtension(fileName, fileAllows);
			if (!validate
					.equals(IConstants.UPLOAD_DOCUMENT.DOC_FILE_STATUS.EXTENSION_NOT_ALLOWED)) {
				if (srcFile.length() > IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT) {
					return IConstants.UPLOAD_DOCUMENT.SIZE_LIMIT_EXCEEDED;
				}
			} else {
				return IConstants.UPLOAD_DOCUMENT.EXTENSION_NOT_ALLOWED;
			}

			renameFileTemp(fileUploadInfo);
			BufferedImage originalImage = ImageIO.read(srcFile);
			int width = originalImage.getWidth() <= 700 ? originalImage.getWidth() : 700;
			int height = (originalImage.getHeight() * width) / originalImage.getWidth();
			BufferedImage resizedTempImage = resizeTempImage(originalImage, width, height, BufferedImage.TYPE_INT_RGB);
			ImageIO.write(resizedTempImage, "jpg", srcFile);
			fileUploadInfo.setFile(srcFile);
			fileUploadInfo.setW(width);
			fileUploadInfo.setH(height);
			Integer uploadResult = uploadTempFile(fileUploadInfo);
			if (uploadResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}
			String destPath = propsConfig.getProperty(URL_AVATAR_FOLDER);
			Integer copyResult = copyFileTemp(fileUploadInfo,destPath);
			if (copyResult.equals(IConstants.UPLOAD_DOCUMENT.FAIL)) {
				return IConstants.UPLOAD_DOCUMENT.FAIL;
			}

			return IConstants.UPLOAD_DOCUMENT.SUCCESS;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
	}

    public Integer copyFileTemp(FileUploadInfo fileUpload, String destPath) {
        log.info("copyFile");
        try {
            File tempFile = fileUpload.getFile();
            File dir = new File(destPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File destFile = null;
            String fileName = fileUpload.getFileTempName();
            destFile = new File(dir, fileName);
            log.info("PATH: " + dir.getPath());
            log.info("PATH DEST: " + destFile.getPath());
            FileUtils.copyFile(tempFile, destFile);
        } catch (Exception e) {
            log.error(e.getMessage());
            return IConstants.UPLOAD_DOCUMENT.FAIL;
        }
        return IConstants.UPLOAD_DOCUMENT.SUCCESS;
    }
	
	public Integer uploadCropAvatarSubmit(FileUploadInfo fileUploadInfo, String wlCode) {
		try {			
			renameFileUpload(fileUploadInfo);
			renameFileTemp(fileUploadInfo);
			String destPath = propsConfig.getProperty(URL_AVATAR_FOLDER);
			String srcAvatar = destPath + "/" +fileUploadInfo.getFileTempName();
			String srcCurrAvatar = destPath + "/" +fileUploadInfo.getFileName();
			File srcFile = new File(srcAvatar);
			File srcCurrFile = new File(srcCurrAvatar);
			BufferedImage currAvatarImage = ImageIO.read(srcFile);
			if(fileUploadInfo.getX() + fileUploadInfo.getW() > currAvatarImage.getWidth()){
				fileUploadInfo.setW(currAvatarImage.getWidth() - fileUploadInfo.getX());
			}
			if(fileUploadInfo.getY() + fileUploadInfo.getH() > currAvatarImage.getHeight()){
				fileUploadInfo.setH(currAvatarImage.getHeight() - fileUploadInfo.getY());
			}
			BufferedImage subImgage = currAvatarImage.getSubimage(fileUploadInfo.getX(), fileUploadInfo.getY(), fileUploadInfo.getW(), fileUploadInfo.getH());
			srcFile.delete();
			srcCurrFile.delete();
			ImageIO.write(subImgage, "jpg", srcCurrFile);
			return IConstants.UPLOAD_DOCUMENT.SUCCESS;
		} 
		catch (Exception e){
			log.error(e.getMessage(), e);
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
	}
	
	private void renameFileTemp(FileUploadInfo fileUploadInfo) {
		String fileExtension;
		StringBuffer newFileName;
		fileExtension = IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.JPG;
		newFileName = new StringBuffer(fileUploadInfo.getCustomerId());
		newFileName.append(ITrsConstants.TEMPLATE_FILE_SUFFIX.IMG_TEMP_FILE);
		newFileName.append(fileExtension);
		fileUploadInfo.setFileTempName(newFileName.toString());
	}
	

	
	private void renameFileUpload(FileUploadInfo fileUploadInfo) {
		// String oldFileName;
		String fileExtension;
		StringBuffer newFileName;
		// oldFileName = fileUploadInfo.getFileName();
		// fileExtension =
		// oldFileName.substring(oldFileName.lastIndexOf(IConstants.UPLOAD_DOCUMENT.EXTENSTION_SEPARATOR));
		fileExtension = IConstants.UPLOAD_DOCUMENT.DOC_FILE_EXTENSION.JPG;
//		newFileName = new StringBuffer(fileUploadInfo.getUsername());
		newFileName = new StringBuffer(fileUploadInfo.getCustomerId());
		newFileName.append(fileExtension);
		fileUploadInfo.setFileName(newFileName.toString());
	}

	private Integer uploadTempFile(FileUploadInfo fileUpload) {
		log.info("uploadDocument");
		try {
			StringBuffer rootPath = new StringBuffer(fileUpload.getRootPath());
			String path = rootPath.append(
					IConstants.UPLOAD_DOCUMENT.UPLOAD_TEMP_FOLDER).toString();
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File destFile = null;
			String fileName = fileUpload.getFileTempName();
			destFile = new File(dir, fileName);
			log.info("TOMCAT TEMP PATH DIR: " + dir.getPath());
			log.info("TOMCAT TEMP FILE: " + destFile.getPath());
			FileUtils.copyFile(fileUpload.getFile(), destFile);
			fileUpload.setFile(destFile);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return IConstants.UPLOAD_DOCUMENT.FAIL;
		}
		return IConstants.UPLOAD_DOCUMENT.SUCCESS;
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
	}
	private BufferedImage resizeTempImage(BufferedImage originalImage, int width, int height, int type) {
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}
	/**
	 * update basic info
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 5, 2013
	 */
	@SuppressWarnings("deprecation")
	public boolean updateBasicInfoOfScCustomer(CustomerInfo customerInfo,CustomerScInfo customerScInfo) {
		try {
			String customerId = customerInfo.getCustomerId();
			if (customerScInfo != null) {
				StringBuilder beforeValue = new StringBuilder();
				StringBuilder afterValue = new StringBuilder();
				//String username = customerScInfo.getUserName();
				String desc = customerScInfo.getDescription();
				
				ScCustomer scCustomer = getiScCustomerDAO().getScCustomer(customerId);
				String description = scCustomer.getDescription();
				if (scCustomer != null && !StringUtils.isBlank(desc) && desc != null
						&& !desc.equals(description)) {

					if (!StringUtils.isEmpty(beforeValue))
						beforeValue.append(",");
					beforeValue.append("Description = " + description);

					if (!StringUtils.isEmpty(afterValue))
						afterValue.append(",");
					afterValue.append("Description = " + desc);
				}
				
				if (getiScCustomerDAO().updateScCustomer(desc, customerId).equals(IConstants.UPDATE_STATUS.FAIL)) {
					return false;
				}
				
				//Halh
				// Insert into AMS_CUSTOMER_TRACE
				AmsCustomerTrace amsCustomerTrace = new AmsCustomerTrace();
				AmsCustomer customer = new AmsCustomer();
				customer.setCustomerId(customerInfo.getCustomerId());
				amsCustomerTrace.setServiceType(IConstants.SERVICES_TYPE.AMS);
				amsCustomerTrace.setAmsCustomer(customer);
				amsCustomerTrace.setReason("Update Customer Information,CustomerId "
								+ customerInfo.getCustomerId());
				amsCustomerTrace.setNote1("");
				amsCustomerTrace.setNote2("");
				amsCustomerTrace.setValue1(beforeValue.toString());
				amsCustomerTrace.setValue2(afterValue.toString());
				amsCustomerTrace.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				amsCustomerTrace.setOperationFullname(customerInfo.getFullName());
				amsCustomerTrace.setChangeTime(new Timestamp(System
						.currentTimeMillis()));
				getiAmsCustomerTraceDAO().save(amsCustomerTrace);
			}
			if (customerInfo != null) {
				Integer gender = customerInfo.getSex();
				Date date = new Date(
						Integer.parseInt(customerInfo.getYear()) - 1900,
						Integer.parseInt(customerInfo.getMonth()) - 1,
						Integer.parseInt(customerInfo.getDay()));
				String dob = DateUtil.toString(date,
						DateUtil.PATTERN_YYYYMMDD_BLANK);
				if (getiAmsCustomerDAO().updateAmsCustomer(gender, dob,
						customerId).equals(IConstants.UPDATE_STATUS.FAIL)) {
					return false;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
			return false;
		}
		return true;
	}

	/**
	 * get social customer Service
	 * 
	 * @param customerId
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 6, 2013
	 */
	public List<BrokerSettingInfo> getBrokerSettingInfo(String customerId) {
		List<BrokerSettingInfo> lisBrokerSettingInfos = new ArrayList<BrokerSettingInfo>();
		try {
			List<ScBrokerSetting> listScBrokerSettings;
			listScBrokerSettings = getiScCustomerServiceDAO().getListBrokerSetting(customerId);
			for (ScBrokerSetting brokerSetting : listScBrokerSettings) {
				BrokerSettingInfo brokerSettingInfo = new BrokerSettingInfo();
				// BeanUtils.copyProperties(brokerSettingInfo, brokerSetting);
				brokerSettingInfo.setScCustServiceId(brokerSetting.getScCustServiceId());
				brokerSettingInfo.setServiceType(StringUtil.toString(brokerSetting.getServiceType()));
				brokerSettingInfo.setBrokerCd(brokerSetting.getBrokerCd());
				brokerSettingInfo.setBrokerName(brokerSetting.getBrokerName());
				brokerSettingInfo.setServerAddress(brokerSetting.getServerAddress());
				brokerSettingInfo.setAccountId(brokerSetting.getAccountId());
				brokerSettingInfo.setAccountPassword(brokerSetting.getAccountPassword());
				brokerSettingInfo.setAccountType(brokerSetting.getAccountType());
				brokerSettingInfo.setAccountKind(brokerSetting.getAccountKind());
				brokerSettingInfo.setEnableFlg(brokerSetting.getEnableFlg());
				brokerSettingInfo.setSignalExpiredDatetime(DateUtil.toString(brokerSetting.getSignalExpiredDatetime(), DateUtil.PATTERN_YYMMDD));
				brokerSettingInfo.setBaseCurrency(brokerSetting.getBaseCurrency());
				lisBrokerSettingInfos.add(brokerSettingInfo);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
		}
		return lisBrokerSettingInfos;
	}

	/**
	 * get map server Address by Broker name
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 8, 2013
	 */
	public Map<Integer, String> getServerAddressByBrokerCd(String brokerCd) {
		List<ScBroker> scBrokerList = new ArrayList<ScBroker>();
		Map<Integer, String> mapServerAddr = new HashMap<Integer, String>();
		try {
			if (brokerCd != null
					&& !brokerCd.equals(IConstants.FRONT_OTHER.COMBO_INDEX)) {
				// scBrokerList = getiScBrokerDAO().findAll();
				// } else {
				// String brokerName = getMapBrokerName().get(brokerId);
				scBrokerList = getiScBrokerDAO().findByBrokerCd(brokerCd);
				if (scBrokerList != null && scBrokerList.size() != 0) {
					for (ScBroker scBroker : scBrokerList) {
						mapServerAddr.put(scBroker.getBrokerId(),scBroker.getServerAddress());
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return mapServerAddr;
	}

	/**
	 * update privacy Setting
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 7, 2013
	 */
	public boolean updatePrivacySetting(Integer writeMyBoardFlg,
			String customerId) {
		try {
			if (customerId != null) {
				if (getiScCustomerDAO().updatePrivacySetting(writeMyBoardFlg,
						customerId).equals(IConstants.UPDATE_STATUS.FAIL)) {
					return false;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
			return false;
		}
		return true;
	}

	/**
	 * get Map brokerName
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 7, 2013
	 */
	public Map<String, String> getMapBrokerCd() {
		Map<String, String> mapBrokerName = new HashMap<String, String>();
		try {
			List<Object> listScBroker = iScBrokerDAO.getListOtherBrokerCd();
			if (listScBroker != null) {
				for (Object obj : listScBroker) {
					// mapBrokerName.put(StringUtil.toString(scBroker.getBrokerId()),
					// scBroker.getBrokerName());
					mapBrokerName.put(StringUtil.toString(obj), StringUtil.toString(obj));
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
		}
		return mapBrokerName;
	}

	/**
	 * get map the number of Enabled account by broker cd
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 8, 2013
	 */
	public Integer getNumberOfEnabledAccount(String customerId) {
		Integer numberNFX = 0;
		try {
			getiScCustomerServiceDAO().getNumberOfEnabledAccount(customerId);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return numberNFX;
	}

	public Integer getNumberOfEnabledOtherBroker(String customerId) {
		Integer number = 0;
		try {
			getiScCustomerServiceDAO().getNumberOfEnabledOtherBroker(customerId);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return number;
	}

	/**
	 * update Enable of Broker
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 8, 2013
	 */
	public boolean updateEnableFlgOfBroker(Integer scCustServiceId,	Integer enableFlg) {
		try {
			if (scCustServiceId != null) {
				ScCustomerService customerService = iScCustomerServiceDAO.findById(ScCustomerService.class, scCustServiceId);
				if (customerService != null) {
					customerService.setEnableFlg(enableFlg);
					customerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					Double equity = getEquity(customerService.getAccountId());
					customerService.setEquity(MathUtil.parseBigDecimal(equity));
					iScCustomerServiceDAO.merge(customerService);
					log.info("[start] update enable signal provider for scCustomerServiceId = " + customerService.getAccountId());
					boolean isReal = true;
					if(IConstants.SERVICES_TYPE.DEMO_FXCD.equals(customerService.getServiceType())) {
						isReal = false;
					}
							
					Integer result = updateMT4SignalProvider(customerService.getAccountId(), enableFlg, isReal);
					log.info("[end] update enable signal provider for scCustomerServiceId = " + customerService.getAccountId() + " with result = " + result);
					if(IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(result)) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				log.warn("Cannot find customer service id with id = " + scCustServiceId);
				return false;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
			return false;
		}
		return true;
	}
	private Double getEquity(String customerServiceId) {
		Double equity = new Double(0);
		BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceId);
		if(balanceInfo != null) {
			equity = balanceInfo.getEquity();
		}
		if(equity == null || equity.compareTo(new Double(0)) <= 0) {
			AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO().findById(AmsCustomerService.class, customerServiceId);
			if(amsCustomerService != null) {
				AmsSubGroup amsSubGroup = amsCustomerService.getAmsSubGroup();
				if(amsSubGroup != null) {
					String currencyCode = amsSubGroup.getCurrencyCode();
					if(IConstants.CURRENCY_CODE.JPY.equals(currencyCode)) {
						return new Double(500000); // hardcode 500000 with JPY
					} else {
						return new Double(500000); // hardcode 50000 with other currency
					}
				}
			}
					
		}
		return equity;
	}
	
	private Integer updateMT4SignalProvider(String accountId, Integer enableFlg, boolean isReal) {
		Integer result = IConstants.UPDATE_ACCOUNT_RESULT.FAILURE;
		UserRecord userRecord = new UserRecord();
		userRecord.setLogin(MathUtil.parseInt(accountId));
		userRecord.setSignalProvider(enableFlg);
		if(isReal) {
			result = MT4Manager.getInstance().updateAccountMt4(userRecord);
		} else {
			result = MT4Manager.getInstance().updateDemoAccountMt4(userRecord);
		}
		
		return result;
	}

	/**
	 * update Broker Info
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 8, 2013
	 */
	public boolean updateBrokerInfo(Integer scCustServiceId,
			String expiredDate, String password) {
		try {
			if (scCustServiceId != null && expiredDate != null) {
				if (getiScCustomerServiceDAO().updateBrokerInfo(
						scCustServiceId, expiredDate, password).equals(
						IConstants.UPDATE_STATUS.FAIL)) {
					return false;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
			return false;
		}
		return true;
	}

	/**
	 * get Current Business Date
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 9, 2013
	 */
	public String getCurrentBusinessDay() {
		SysAppDate appDate = appDateDAO.getCurrentBusinessDay();
		if (appDate != null) {
			return appDate.getId().getFrontDate();
		} else {
			return null;
		}
	}

	/**
	 * check account id and broker name is already existed
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 9, 2013
	 */
	public boolean checkExistAccountIdAndBrokerId(String customerId,
			BrokerSettingInfo brokerSettingInfo) {
		try {
			List<Object> numberList = getiScCustomerServiceDAO()
					.checkExistAccountIdAndBrokerId(customerId,
							brokerSettingInfo.getAccountId(),
							brokerSettingInfo.getBrokerId());
			Integer number = (Integer) numberList.get(0);
			if (number.equals(0)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}
	}

	/**
	 * create new Account Broker
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 9, 2013
	 */
	public boolean insertNewAccountBroker(String customerId,
			BrokerSettingInfo brokerSettingInfo) {
		try {
			ScCustomerService scCustomerService = new ScCustomerService();
			if (brokerSettingInfo != null && customerId != null) {
				scCustomerService.setCustomerId(customerId);
				scCustomerService.setServiceType(IConstants.SERVICES_TYPE.OTHER_BROKER);
				scCustomerService.setBaseCurrency(brokerSettingInfo.getBaseCurrency());
				scCustomerService.setBrokerId(MathUtil.parseInteger(brokerSettingInfo.getBrokerId(), null));
				scCustomerService.setBrokerCd(brokerSettingInfo.getBrokerCd());
				scCustomerService.setAccountId(brokerSettingInfo.getAccountId());
				scCustomerService.setAccountPassword(brokerSettingInfo.getAccountPassword());
				scCustomerService.setAccountKind(brokerSettingInfo.getAccountKind());
				scCustomerService.setEnableFlg(IConstants.ENABLE_FLG.DISABLE);
				long expiredDate = DateUtil.toDate(brokerSettingInfo.getSignalExpiredDatetime(),
						DateUtil.PATTERN_YYMMDD).getTime();
				scCustomerService.setSignalExpiredDatetime(new Timestamp(expiredDate));
				scCustomerService.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
				scCustomerService.setInputDate(new Timestamp(new Date().getTime()));
				scCustomerService.setUpdateDate(new Timestamp(new Date().getTime()));
			}

			if (brokerSettingInfo != null) {
				getiScCustomerServiceDAO().save(scCustomerService);
				return true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}
		return false;
	}

	public String getCountryCodeFromCountryId(Integer countryId) {
		String countryCode = null;
		try {
			if (countryId != null) {
				countryCode = getiAmsSysCountryDAO().findById(
						AmsSysCountry.class, countryId).getCountryCode();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return countryCode;
	}

	/**
	 * delete a broker account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 12, 2013
	 */
	public boolean deleteBroker(Integer scCustServiceId) {
		try {
			ScCustomerService customerService = iScCustomerServiceDAO.findById(
					ScCustomerService.class, scCustServiceId);
			if (customerService != null) {
				customerService.setActiveFlg(IConstants.ACTIVE_FLG.INACTIVE);
				customerService.setUpdateDate(new Timestamp(System
						.currentTimeMillis()));
				iScCustomerServiceDAO.merge(customerService);
				return true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return false;
	}

	/**
	 * get list copy order that copy an account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author DuyenNT
	 * @CrDate Mar 12, 2013
	 */
	public Integer getNumberOfScOrder(String customerServiceId) {
		Integer numberCopyOrder = IConstants.FRONT_OTHER.NOT_FOUND_NUMBER;
		try {
			numberCopyOrder = getiScOrderDAO().getNumberOfCopyAccount(customerServiceId);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return numberCopyOrder;
	}

	/**
	 * Get a map of prefecture
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Apr 18, 2013
	 */
	@Override
	public Map<String, String> getMapPrefecture() {
		Map<String, String> mapPrefecture = new LinkedHashMap<String, String>();
//		List<String> listPrefecture = amsSysZipcodeDAO.getUniquePrefectures();
//		for (String prefecture : listPrefecture) {
//			mapPrefecture.put(prefecture, prefecture);
//		}
		
		// Now fix this information to customize order
//		mapPrefecture.put("-1", "選択して下さい");
//		mapPrefecture.put("北海道"     ,  " 北海道"        )  ;
//		mapPrefecture.put("青森県"     ,  " 青森県   "     )  ;
//		mapPrefecture.put("岩手県"     ,  " 岩手県   "     )  ;
//		mapPrefecture.put("宮城県"     , "  宮城県  "     )  ;
//		mapPrefecture.put("秋田県"     , "  秋田県  "     )  ;
//		mapPrefecture.put("山形県"     , "  山形県  "     )  ;
//		mapPrefecture.put("福島県"     , "  福島県  "     )  ;
//		mapPrefecture.put("茨城県"     , "  茨城県  "     )  ;
//		mapPrefecture.put("栃木県"     , "  栃木県  "     )  ;
//		mapPrefecture.put("群馬県"     , "  群馬県  "     )  ;
//		mapPrefecture.put("埼玉県"     , "  埼玉県  "     )  ;
//		mapPrefecture.put("千葉県"     , "  千葉県  "     )  ;
//		mapPrefecture.put("東京都"     , "  東京都  "     )  ;
//		mapPrefecture.put("神奈川県"    , "  神奈川県"     )  ;
//		mapPrefecture.put("新潟県"     , "  新潟県  "     )  ;
//		mapPrefecture.put("富山県"     , "  富山県  "     )  ;
//		mapPrefecture.put("石川県"     , "  石川県  "     )  ;
//		mapPrefecture.put("福井県"     , "  福井県  "     )  ;
//		mapPrefecture.put("山梨県"     , "  山梨県  "     )  ;
//		mapPrefecture.put("長野県"     , "  長野県  "     )  ;
//		mapPrefecture.put("岐阜県"     , "  岐阜県  "     )  ;
//		mapPrefecture.put("静岡県"     , "  静岡県  "     )  ;
//		mapPrefecture.put("愛知県"     , "  愛知県  "     )  ;
//		mapPrefecture.put("三重県"     , "  三重県  "     )  ;
//		mapPrefecture.put("滋賀県"     , "  滋賀県  "     )  ;
//		mapPrefecture.put("京都府"     , "  京都府  "     )  ;
//		mapPrefecture.put("大阪府"     , "  大阪府  "     )  ;
//		mapPrefecture.put("兵庫県"     , "  兵庫県  "     )  ;
//		mapPrefecture.put("奈良県"     , "  奈良県  "     )  ;
//		mapPrefecture.put("和歌山県"     , "  和歌山県"     )  ;
//		mapPrefecture.put("鳥取県"     , "  鳥取県  "     )  ;
//		mapPrefecture.put("島根県"     , "  島根県  "     )  ;
//		mapPrefecture.put("岡山県"     , "  岡山県  "     )  ;
//		mapPrefecture.put("広島県"     , "  広島県  "     )  ;
//		mapPrefecture.put("山口県"     , "  山口県  "     )  ;
//		mapPrefecture.put("徳島県"     , "  徳島県  "     )  ;
//		mapPrefecture.put("香川県"     , "  香川県  "     )  ;
//		mapPrefecture.put("愛媛県"     , "  愛媛県  "     )  ;
//		mapPrefecture.put("高知県"     , "  高知県  "     )  ;
//		mapPrefecture.put("福岡県"     , "  福岡県  "     )  ;
//		mapPrefecture.put("佐賀県"     , "  佐賀県  "     )  ;
//		mapPrefecture.put("長崎県"     , "  長崎県  "     )  ;
//		mapPrefecture.put("熊本県"     , "  熊本県  "     )  ;
//		mapPrefecture.put("大分県"     , "  大分県  "     )  ;
//		mapPrefecture.put("宮崎県"     , "  宮崎県  "     )  ;
//		mapPrefecture.put("鹿児島県"     , "  鹿児島県"     )  ;
//		mapPrefecture.put("沖縄県"     , "  沖縄県  "     )  ;
		
		// return empty map, change to use get perfectures in the action class
		return mapPrefecture;
	}

	/**
	 * Get zipCode
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Apr 18, 2013
	 */
	@Override
	public AmsSysZipcode getAddressByZipCode(String zipCode) {
		AmsSysZipcode amsSysZipcode = amsSysZipcodeDAO.getZipCode(zipCode);
		return amsSysZipcode;
	}

	/**
	 * Get bank name
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Apr 26, 2013
	 */
	@Override
	public String getBankNameByVirtualBank(String virtualBankAccNo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Result syncTradingInfoToSalesForce(String customerId) {
		Result result = Result.UNKNOWN;
		log.info("[start] sync trading info of customerId: " + customerId + " to SalesForce");
		try {
			AmsCustomerService amsCustomerService = iAmsCustomerServiceDAO.getCustomerServicesInfo(customerId, ITrsConstants.SERVICES_TYPE.NTD_FX);
			
			CRMIntegrationAPI crmAPIs = CRMIntegrationAPI.getInstance();
			CustomerInfor__c sFCustomer = crmAPIs.getCustomer(customerId);
			
			if (sFCustomer != null) {
				sFCustomer.setALLOW_TRANSACT_FLG__c(String.valueOf(amsCustomerService.getAllowTransactFlg()));
				int updateStatus = crmAPIs.updateCustomer(sFCustomer);
				log.info("sync SalesForce customerId: " + customerId + ": " + SyncSaleForceResult.valueOf(updateStatus));
				result = updateStatus == SyncSaleForceResult.SUCCESS.getNumber() ? Result.SUCCESS : Result.FAILED;
			} else{
				log.error("Can not find customerId: " + customerId + " on SalesForce");
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = Result.FAILED;
		}
		
		log.info("[end] sync trading info of customerId: " + customerId + " to SalesForce: " + result);
		
		return result;
	}
	
	/**
	 * @description Sync customer info to SalesForce
	 * @version NTS1.0
	 * @author le.hong.ha
	 * @CrDate May 10, 2013
	 * @Copyright Posismo Hanoi Limited. All rights reserved.
	 */
	@Override
	public void syncCustomerInfoToSaleFace(final CustomerInfo customerInfo) {
		try {
			AmsCustomer amsCustomer = iAmsCustomerDAO.getCustomerInfo(customerInfo.getCustomerId());
			AmsCustomerSurvey amsCustomerSurvey = amsCustomer.getAmsCustomerSurvey();
			AmsCustomerService amsCustomerService = iAmsCustomerServiceDAO.getCustomerServicesInfo(customerInfo.getCustomerId(),
					ENABLE_MT4_FX.ENABLE.equals(String.valueOf(customerInfo.getEnableMt4Fx())) ? ITrsConstants.SERVICES_TYPE.FX : ITrsConstants.SERVICES_TYPE.NTD_FX);
			
			CRMIntegrationAPI crmAPI = CRMIntegrationAPI.getInstance();
			CustomerInfor__c sFCustomer = crmAPI.getCustomer(customerInfo.getCustomerId());
			
			if(sFCustomer != null){
				updateCustomerInfoToSf(customerInfo, amsCustomer, amsCustomerSurvey, amsCustomerService, crmAPI, sFCustomer);
			} else {
				createCustomerInfoToSf(customerInfo, amsCustomer, amsCustomerSurvey, amsCustomerService, crmAPI);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void updateCustomerInfoToSf(final CustomerInfo customerInfo, AmsCustomer amsCustomer, AmsCustomerSurvey amsCustomerSurvey, AmsCustomerService amsCustomerService,
			CRMIntegrationAPI crmAPI, CustomerInfor__c sFCustomer) {
		if (amsCustomer.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER) {

			sFCustomer.setCORPORATION_TYPE__c(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER + "");
			sFCustomer.setLAST_NAME__c(amsCustomer.getFirstName());
			sFCustomer.setFIRST_NAME__c(amsCustomer.getLastName());
			sFCustomer.setLAST_NAME_KANA__c(amsCustomer.getLastNameKana());
			sFCustomer.setFIRST_NAME_KANA__c(amsCustomer.getFirstNameKana());
			sFCustomer.setBIRTHDAY__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getBirthday(), DateUtil.PATTERN_YYYYMMDD_BLANK));

			sFCustomer.setSEX__c(amsCustomer.getSex() + "");
			sFCustomer.setMAIL_ADDTIONAL__c(amsCustomer.getMailAddtional());

			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - Start
			sFCustomer.setCONFIRM7_FLG__c(toBoolean(amsCustomer.getConfirm7Flg()));
			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - End

		} else {

			// CORPORATION CUSTOMER
			sFCustomer.setCORPORATION_TYPE__c(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER + "");
			sFCustomer.setCORP_FULLNAME__c(amsCustomer.getCorpFullname());
			sFCustomer.setCORP_FULLNAME_KANA__c(amsCustomer.getCorpFullnameKana());
//			sFCustomer.setBIRTHDAY__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getCorpEstablishDate(), DateUtil.PATTERN_YYYYMM_BLANK)); //TRSGAP-877
			sFCustomer.setCORP_ESTABLISH_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getCorpEstablishDate(),DateUtil.PATTERN_YYYYMM_BLANK));
			sFCustomer.setMAIL_ADDTIONAL__c(amsCustomer.getCorpPicMailMobile());

			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - Start
			sFCustomer.setCONFIRM7_FLG__c(toBoolean(amsCustomer.getConfirm8Flg()));
			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - End

		}

		sFCustomer.setMAIL_MAIN__c(amsCustomer.getMailMain());
		if (!StringUtil.isEmpty(amsCustomer.getAccountApplicationDate())) {
			sFCustomer.setACCOUNT_APPLICATION_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getAccountApplicationDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
		}
		if (!StringUtil.isEmpty(amsCustomer.getAccountActiveDate())) {
			sFCustomer.setACCOUNT_ACTIVE_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getAccountActiveDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
		}
		sFCustomer.setZIPCODE__c(amsCustomer.getZipcode());
		sFCustomer.setCITY__c(amsCustomer.getCity());
		sFCustomer.setPREFECTURE__c(amsCustomer.getPrefecture());
		sFCustomer.setSECTION__c(amsCustomer.getSection());
		sFCustomer.setTEL1__c(amsCustomer.getTel1());
		sFCustomer.setTEL2__c(amsCustomer.getTel2());
		sFCustomer.setALLOW_LOGIN_FLG__c(toBoolean(amsCustomer.getAllowLoginFlg()));
		sFCustomer.setALLOW_NEW_ORDER_FLG__c(toBoolean(amsCustomer.getAllowNewOrderFlg()));
		sFCustomer.setALLOW_WITHDRAWAL_FLG__c(toBoolean(amsCustomer.getAllowWithdrawalFlg()));
		sFCustomer.setCORP_REP_FIRSTNAME__c(amsCustomer.getCorpRepFirstname());
		sFCustomer.setCORP_REP_LASTNAME__c(amsCustomer.getCorpRepLastname());
		sFCustomer.setCORP_REP_FIRSTNAME_KANA__c(amsCustomer.getCorpRepFirstnameKana());
		sFCustomer.setCORP_REP_LASTNAME_KANA__c(amsCustomer.getCorpRepLastnameKana());
		sFCustomer.setCORP_PIC_FIRSTNAME__c(amsCustomer.getCorpPicFirstname());
		sFCustomer.setCORP_PIC_LASTNAME__c(amsCustomer.getCorpPicLastname());
		sFCustomer.setCORP_PIC_FIRSTNAME_KANA__c(amsCustomer.getCorpPicFirstnameKana());
		sFCustomer.setCORP_PIC_LASTNAME_KANA__c(amsCustomer.getCorpPicLastnameKana());
		sFCustomer.setCORP_PIC_ZIPCODE__c(amsCustomer.getCorpPicZipcode());
		sFCustomer.setCORP_PIC_PREFECTURE__c(amsCustomer.getCorpPicPrefecture());
		sFCustomer.setDISCOVER_SOURCE__c(amsCustomerSurvey.getDiscoverSource() + "");
		sFCustomer.setCORP_PIC_SECTION__c(amsCustomer.getCorpPicSection());
		sFCustomer.setCORP_PIC_SEX__c(StringUtil.toString(amsCustomer.getCorpPicSex()));
		sFCustomer.setCORP_PIC_MAIL_PC__c(amsCustomer.getCorpPicMailPc());
		sFCustomer.setCORP_PIC_MAIL_MOBILE__c(amsCustomer.getCorpPicMailMobile());
		sFCustomer.setCORP_PIC_TEL__c(amsCustomer.getCorpPicTel());
		sFCustomer.setCORP_PIC_MOBILE__c(amsCustomer.getCorpPicMobile());

		if (amsCustomerService != null) {
			if (!StringUtil.isEmpty(amsCustomerService.getAccountCancelDate())) {
				sFCustomer.setACCOUNT_CANCEL_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomerService.getAccountCancelDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
			}
			if (!StringUtil.isEmpty(amsCustomerService.getAccountOpenFinishDate())) {
				sFCustomer
						.setACCOUNT_OPEN_FINISH_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomerService.getAccountOpenFinishDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
			}
			sFCustomer.setCUSTOMER_SERVICE_STATUS__c(StringUtil.toString(amsCustomerService.getCustomerServiceStatus()));
			sFCustomer.setLOSSCUT_FLG__c(toBoolean(amsCustomerService.getLosscutFlg()));
		}
		sFCustomer.setALLOW_NEW_ORDER_FLG__c(amsCustomer.getAllowNewOrderFlg() == 1 ? true : false);
		if (amsCustomerSurvey != null) {
			sFCustomer.setDISCOVER_SOURCE__c(StringUtil.toString(amsCustomerSurvey.getDiscoverSource()));
			sFCustomer.setBENEFIC_OWNER_FULLNAME__c(amsCustomerSurvey.getBeneficOwnerFullname());
			sFCustomer.setBENEFIC_OWNER_FULLNAME_KANA__c(amsCustomerSurvey.getBeneficOwnerFullnameKana());
			sFCustomer.setMY_NUMBER_STATUS__c(String.valueOf(amsCustomerSurvey.getMyNumberStatus() == null ? 0 : amsCustomerSurvey.getMyNumberStatus()));
		}

		// Set Affiliate information
		// Get Affiliate name
		String affName = getAffName(customerInfo.getCustomerId());
		sFCustomer.setAFF_NAME__c(affName);

		int updateStatus = crmAPI.updateCustomer(sFCustomer);

		log.info("Update customer to SalesForce status: " + updateStatus);
		switch (updateStatus) {
		case 1:
			log.info("Update customer to SalesForce status SUCCESS");
			break;
		case 2:
			log.info("Update customer to SalesForce status FAILED");
		case 0:
			log.info("Update customer to SalesForce status UNKNOW ERROR");
		default:
			break;
		}
	}
	
	private void createCustomerInfoToSf(final CustomerInfo customerInfo, AmsCustomer amsCustomer, AmsCustomerSurvey amsCustomerSurvey, AmsCustomerService amsCustomerService,
			CRMIntegrationAPI crmAPI) {
		CustomerInfor__c sFCustomer;
		log.warn("Can not find customer Id: " + customerInfo.getAccountId() + " on SalesForce");
		log.info("Insert to SalesForce...");

		sFCustomer = new CustomerInfor__c();
		sFCustomer.setAMS_Customer_Id__c(amsCustomer.getCustomerId());
		if (amsCustomer.getCorporationType() == ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER) {
			
			sFCustomer.setCORPORATION_TYPE__c(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER + "");
			sFCustomer.setLAST_NAME__c(amsCustomer.getLastName());
			sFCustomer.setFIRST_NAME__c(amsCustomer.getFirstName());
			sFCustomer.setLAST_NAME_KANA__c(amsCustomer.getLastNameKana());
			sFCustomer.setFIRST_NAME_KANA__c(amsCustomer.getFirstNameKana());
			sFCustomer.setBIRTHDAY__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getBirthday(), DateUtil.PATTERN_YYYYMMDD_BLANK));
			sFCustomer.setSEX__c(amsCustomer.getSex() + "");

			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - Start
			sFCustomer.setCONFIRM7_FLG__c(toBoolean(amsCustomer.getConfirm7Flg()));
			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - End
			
		} else {
			
			// CORPORATION CUSTOMER
			sFCustomer.setCORPORATION_TYPE__c(ITrsConstants.TRS_CONSTANT.CORPORATION_CUSTOMER + "");
			sFCustomer.setCORP_FULLNAME__c(amsCustomer.getCorpFullname());
			sFCustomer.setCORP_FULLNAME_KANA__c(amsCustomer.getCorpFullnameKana());
//			sFCustomer.setBIRTHDAY__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getCorpEstablishDate(), DateUtil.PATTERN_YYYYMM_BLANK)); //TRSGAP-877
			sFCustomer.setCORP_ESTABLISH_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getCorpEstablishDate(),DateUtil.PATTERN_YYYYMM_BLANK));

			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - Start
			sFCustomer.setCONFIRM7_FLG__c(toBoolean(amsCustomer.getConfirm8Flg()));
			// [TRSGAP-487-cuong.bui.manh]Jun 28, 2016M - End
			
		}

		sFCustomer.setMAIL_MAIN__c(amsCustomer.getMailMain());
		sFCustomer.setMAIL_ADDTIONAL__c(amsCustomer.getMailAddtional());
		if (!StringUtil.isEmpty(amsCustomer.getAccountApplicationDate())) {
			sFCustomer.setACCOUNT_APPLICATION_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getAccountApplicationDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
		}
		if (!StringUtil.isEmpty(amsCustomer.getAccountActiveDate())) {
			sFCustomer.setACCOUNT_ACTIVE_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomer.getAccountActiveDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
		}
		sFCustomer.setZIPCODE__c(amsCustomer.getZipcode());
		sFCustomer.setPREFECTURE__c(amsCustomer.getPrefecture());
		sFCustomer.setCITY__c(amsCustomer.getCity());
		sFCustomer.setSECTION__c(amsCustomer.getSection());
		sFCustomer.setTEL1__c(amsCustomer.getTel1());
		sFCustomer.setTEL2__c(amsCustomer.getTel2());
		sFCustomer.setALLOW_LOGIN_FLG__c(toBoolean(amsCustomer.getAllowLoginFlg()));
		sFCustomer.setALLOW_NEW_ORDER_FLG__c(toBoolean(amsCustomer.getAllowNewOrderFlg()));
		sFCustomer.setALLOW_WITHDRAWAL_FLG__c(toBoolean(amsCustomer.getAllowWithdrawalFlg()));
		sFCustomer.setCORP_REP_FIRSTNAME__c(amsCustomer.getCorpRepFirstname());
		sFCustomer.setCORP_REP_LASTNAME__c(amsCustomer.getCorpRepLastname());
		sFCustomer.setCORP_REP_FIRSTNAME_KANA__c(amsCustomer.getCorpRepFirstnameKana());
		sFCustomer.setCORP_REP_LASTNAME_KANA__c(amsCustomer.getCorpRepLastnameKana());
		sFCustomer.setCORP_PIC_FIRSTNAME__c(amsCustomer.getCorpPicFirstname());
		sFCustomer.setCORP_PIC_LASTNAME__c(amsCustomer.getCorpPicLastname());
		sFCustomer.setCORP_PIC_FIRSTNAME_KANA__c(amsCustomer.getCorpPicFirstnameKana());
		sFCustomer.setCORP_PIC_LASTNAME_KANA__c(amsCustomer.getCorpPicLastnameKana());
		sFCustomer.setCORP_PIC_ZIPCODE__c(amsCustomer.getCorpPicZipcode());
		sFCustomer.setCORP_PIC_PREFECTURE__c(amsCustomer.getCorpPicPrefecture());
		sFCustomer.setCORP_PIC_SECTION__c(amsCustomer.getCorpPicSection());
		sFCustomer.setCORP_PIC_SEX__c(StringUtil.toString(amsCustomer.getCorpPicSex()));
		sFCustomer.setCORP_PIC_MAIL_PC__c(amsCustomer.getCorpPicMailPc());
		sFCustomer.setCORP_PIC_MAIL_MOBILE__c(amsCustomer.getCorpPicMailMobile());
		sFCustomer.setCORP_PIC_TEL__c(amsCustomer.getCorpPicTel());
		sFCustomer.setCORP_PIC_MOBILE__c(amsCustomer.getCorpPicMobile());
		sFCustomer.setDISCOVER_SOURCE__c(amsCustomerSurvey.getDiscoverSource() + "");
		if (amsCustomerService != null) {
			if (!StringUtil.isEmpty(amsCustomerService.getAccountCancelDate())) {
				sFCustomer.setACCOUNT_CANCEL_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomerService.getAccountCancelDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
			}
			if (!StringUtil.isEmpty(amsCustomerService.getAccountOpenFinishDate())) {
				sFCustomer
						.setACCOUNT_OPEN_FINISH_DATE__c(CRMIntegrationAPI.getCalendarWithRawOffset(amsCustomerService.getAccountOpenFinishDate(), DateUtil.PATTERN_YYYYMMDD_BLANK));
			}
			sFCustomer.setCUSTOMER_SERVICE_STATUS__c(StringUtil.toString(amsCustomerService.getCustomerServiceStatus()));
			sFCustomer.setLOSSCUT_FLG__c(toBoolean(amsCustomerService.getLosscutFlg()));
		}
		sFCustomer.setALLOW_NEW_ORDER_FLG__c(amsCustomer.getAllowNewOrderFlg() == 1 ? true : false);
		if (amsCustomerSurvey != null) {
			sFCustomer.setDISCOVER_SOURCE__c(StringUtil.toString(amsCustomerSurvey.getDiscoverSource()));
			sFCustomer.setBENEFIC_OWNER_FULLNAME__c(amsCustomerSurvey.getBeneficOwnerFullname());
			sFCustomer.setBENEFIC_OWNER_FULLNAME_KANA__c(amsCustomerSurvey.getBeneficOwnerFullnameKana());
			sFCustomer.setMY_NUMBER_STATUS__c(String.valueOf(amsCustomerSurvey.getMyNumberStatus() == null ? 0 : amsCustomerSurvey.getMyNumberStatus()));
		}

		// Set Affiliate information
		// Get Affiliate name
		String affName = getAffName(customerInfo.getCustomerId());
		if (affName == null) {
			affName = "";
		}
		sFCustomer.setAFF_NAME__c(affName);

		int insertStatus = crmAPI.insertCustomer(sFCustomer);

		log.info("Insert customer to SalesForce status: " + insertStatus);
		switch (insertStatus) {
		case 1:
			log.info("Insert customer to SalesForce status SUCCESS");
			break;
		case 2:
			log.info("Insert customer to SalesForce status FAILED");
		case 0:
			log.info("Insert customer to SalesForce status UNKNOW ERROR");
		default:
			break;
		}
	}

    private String getAffName(String customerId) {
		List<AmsAffiliate> affiliates = amsAffiliateDAO.getAmsAffiliateByCustomerId(customerId);
		if(affiliates != null && affiliates.size() > 0 && affiliates.get(0) != null){
			return affiliates.get(0).getAffName();
		}
		return null;
	}

	private Boolean toBoolean(Integer a){
		if(a != null && a == 1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Check bank acc number existed
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate May 13, 2013
	 */
	@Override
	public boolean checkExistedBankAccNumber(BankTransferInfo bankTransferInfo) {
		/*AmsCustomerBank amsCustomerBank = iAmsCustomerBankDAO.getBankInfo(bankTransferInfo.getCustomerId(), bankTransferInfo.getAccountNumber());
		if(amsCustomerBank != null){
			return true;
		}*/
		return false;
	}

	/**
	 * Search bank　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate May 13, 2013
	 */
	@Override
	public SearchResult<AmsSysBank> findListBank(String bankName, String bankNameFullSize, String bankNameHalfSize, PagingInfo paging) {
		return  amsSysBankDAO.searchByBankName(bankName, bankNameFullSize, bankNameHalfSize, paging);
	}

	/**
	 * Search bank branch　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.hong.ha
	 * @CrDate May 13, 2013
	 */
	@Override
	public SearchResult<AmsSysBankBranch> findListBankBranch(String bankCode, String branchName, String branchNameFullSize, String branchNameHalfSize, PagingInfo paging) {
		return amsSysBankBranchDAO.searchByBankCodeBranchName(bankCode, branchName, branchNameFullSize, branchNameHalfSize, paging);
	}

	/**
	 * Search customer report history
	 * 
	 * @param
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws
	 * @author le.hong.ha
	 * @CrDate Jul 29, 2013
	 */
	@Override
	public SearchResult<CustReportHistoryInfo> searchCustReportHistory(ReportHistorySearchCondition condition, PagingInfo paging, String privateKey, String publicKey) throws UnsupportedEncodingException {
		SearchResult<AmsWhitelabelReport> listReport = amsWhitelabelReportDAO.searchCustReportHistory(condition, paging);
		SearchResult<CustReportHistoryInfo> reportInfo = new SearchResult<CustReportHistoryInfo>();
		reportInfo.setPagingInfo(listReport.getPagingInfo());
		for (AmsWhitelabelReport amsWhitelabelReport : listReport) {
			CustReportHistoryInfo info = new CustReportHistoryInfo();
			Date reportDate = DateUtil.toDate(amsWhitelabelReport.getReportDate(), DateUtil.PATTERN_YYMMDD_BLANK);
			info.setReportDate(DateUtil.toString(reportDate, DateUtil.PATTERN_YYMMDD));
			info.setReportType(amsWhitelabelReport.getReportType().toUpperCase());
			info.setReportTitle(amsWhitelabelReport.getReportTitle());
			info.setReportFileName(amsWhitelabelReport.getReportFileName());
			info.setReportId(amsWhitelabelReport.getReportId());
			if(!StringUtil.isEmpty(amsWhitelabelReport.getLink())) {
				// [start] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
				String encryptedLink = encrypt(amsWhitelabelReport.getLink(),privateKey,publicKey);
				info.setLink(URLEncoder.encode(encryptedLink, "UTF-8"));
				// [end]Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
			}
			if(amsWhitelabelReport.getReadFlg()!=null){
				info.setUnread(amsWhitelabelReport.getReadFlg());
			}else{
				info.setUnread(ITrsConstants.REPORT_HISTORY.RADIO.YES);
			}
			reportInfo.add(info);
		}
		return reportInfo;
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
	public boolean mailIndivExisted(String customerId, String mailMain) {
		List<AmsCustomer> customers = iAmsCustomerDAO.getSameMailInvi(customerId, mailMain);
		if(customers == null || customers.size() <= 0){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean mailCorpExisted(String customerId, String mailMain) {
		List<AmsCustomer> customers = iAmsCustomerDAO.getSameMailCorp(customerId, mailMain);
		if(customers == null || customers.size() <= 0){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean additionalMailExisted(String customerId, String mailMain) {
		List<AmsCustomer> customers = iAmsCustomerDAO.getSameAdditionalMail(customerId, mailMain);
		if(customers == null || customers.size() <= 0){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean mailExisted(String customerId, String mail) {
		List<AmsCustomer> customers = iAmsCustomerDAO.getSameMail(customerId, mail);
		if(customers == null || customers.size() <= 0){
			return false;
		}
		return true;
	}
	
	private String encrypt(String str, String privateKey, String publicKey) {
		try {
			return Cryptography.encrypt(str, privateKey, publicKey);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	public void updateWhiteLabelReport(String reportId){
		if(!StringUtil.isEmpty(reportId)){
			AmsWhitelabelReport report = amsWhitelabelReportDAO.findById(AmsWhitelabelReport.class, new Integer(reportId));
			if(report!=null){
				if(report.getReadFlg()!=null && !ITrsConstants.REPORT_HISTORY.RADIO.YES.equals(report.getReadFlg())){
					report.setReadFlg(ITrsConstants.REPORT_HISTORY.RADIO.YES);
					amsWhitelabelReportDAO.merge(report);
				}
			}
		}
	}
	private LinkedHashMap<String,String> getTextStringMap(Map<String, String> map) {
		LinkedHashMap<String, String> textMap = new LinkedHashMap<String, String>();
    	if(map!= null && map.size() > 0) {
    		for(String key : map.keySet()) {
    			textMap.put(key, MasterDataManagerImpl.getInstance().getText(map.get(key)));
    		}
    	}
    	return textMap;
    }

	public BoCustomer getBoCustomer(String customerId) {
		List<BoCustomer> findByCustomerIds = boCustomerDAO.findByCustomerId(customerId);
		if(findByCustomerIds != null && findByCustomerIds.size() > 0)
			return findByCustomerIds.get(0);
		return null;
	}

	private Result modifySocialAccount(CustomerInfo newCus, CustomerServicesInfo scService) {
		AmsCustomerModifySocialRequest request = AmsCustomerModifySocialRequest.newBuilder()
														.setCustomerId(newCus.getCustomerId())
														.setCustomerServiceId(scService.getCustomerServiceId())
														.setLoginId(newCus.getMailMain())
														.build();
		
		ModifyAccountResult modifyAccountResult = modifySocialAccount(request);
		log.info("modify SocialAccount Result: " + modifyAccountResult);
		
		if (modifyAccountResult == ModifyAccountResult.SUCCESS)
			return Result.SUCCESS;
		return Result.FAILED;
	}
	
	/**
	 * Modify Social Account to social system
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
	public ModifyAccountResult modifySocialAccount(AmsCustomerModifySocialRequest request) {
		
		final CustomerAccountModification modification = new CustomerAccountModificationImpl();
		modification.setAccountId(Integer.valueOf(request.getCustomerServiceId()));
		if(request.hasZipCode())
			modification.setZipCode(request.getZipCode());
		if(request.hasAddress1())
			modification.setAddress1(request.getAddress1());
		if(request.hasAddress2())
			modification.setAddress2(request.getAddress2());
		if(request.hasAddress3())
			modification.setAddress3(request.getAddress3());
		if(request.hasFirstName())
			modification.setFirstName(request.getFirstName());
		if(request.hasLastName())
			modification.setFamilyName(request.getLastName());
		
		if(request.hasDescription())
			modification.setDescription(request.getDescription());
		if(request.hasLoginRestriction())
			modification.setLoginRestriction(LoginRestriction.parse(Converter.nextopToSophiaEnumValue((byte)request.getLoginRestriction()))); //1 (ON), 9 (OFF)
		if(request.hasTradingRestriction()) {
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(request.getCustomerServiceId());
			boolean isEaAccount = Helper.isEaGroupName(customerServiceInfo.getGroupName());
			modification.setTradingRestriction(TradingRestriction.parse(Converter.nextopToSophiaTradingRestrictionEnumValue((byte)request.getTradingRestriction(), isEaAccount))); //1 (ON), 9 (OFF)
		}
		if(request.hasTransferRestriction())
			modification.setTransferRestriction(TransferRestriction.parse(Converter.nextopToSophiaEnumValue((byte)(request.getTransferRestriction())))); //1 (ON), 9 (OFF)
		if(request.hasPilotingStatus())
			modification.setPilotingStatus(PilotingStatus.parse(Converter.nextopToSophiaEnumValue((byte)(request.getPilotingStatus())))); //1 (ON), 9 (OFF)
		if(request.hasLeverage())
			modification.setLeverage(request.getLeverage());
		if(request.hasLoginId())
			modification.setLoginId(request.getLoginId());
		
		return SCManager.getInstance().modifyAccount(modification);
	}
	
	/**
	 * Close Social Account to social system
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
	public CloseAccountResult closeSocialAccount(String customerId) {
		return SCManager.getInstance().closeAccount(Integer.valueOf(customerId));
	}
	
	/**
	 * Open Social Account to social system
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
	public OpenAccountResult openSocialAccount(AmsCustomerRegisterSocialRequest request) {
		final Customer customer = openCustomer(request);
		final List<CustomerAccount> accounts = openAccounts(customer, request.getServiceTypeInfoList());
		return SCManager.getInstance().openAccount(customer, accounts);
	}
	
	protected Customer openCustomer(AmsCustomerRegisterSocialRequest request) {
		final CustomerImpl r = new CustomerImpl();
		r.setId(Integer.valueOf(request.getCustomerId()));
		r.setLoginId(request.getLoginId());
		r.setZipCode(request.getZipCode());
		r.setAddress1(request.getAddress1());
		if(request.hasAddress2())
			r.setAddress2(request.getAddress2());
		if(request.hasAddress3())
			r.setAddress3(request.getAddress3());
		r.setFirstName(request.getFirstName());
		r.setFamilyName(request.getLastName());
		r.setCorporationType(request.getCorpType() == CorporationType.INDIVIDUAL
				? cn.nextop.social.api.admin.proxy.glossary.CorporationType.INDIVIDUAL : cn.nextop.social.api.admin.proxy.glossary.CorporationType.CORPORATION );
		return r;
	}
    
    protected List<CustomerAccount> openAccounts(Customer customer, List<ServiceTypeInfo> serviceTypeInfos) {
    	final List<CustomerAccount> r = new ArrayList<CustomerAccount>();
    	
		//MailConfig
		final CustomerMailConfig mc = new CustomerMailConfig();
		mc.setTradePcMailEnabled(true);
		mc.setTradeMobileMailEnabled(false);
		mc.setSignalOffPcMailEnabled(false);
		mc.setSignalOffMobileMailEnabled(false);
		boolean isEaAccount = accountManager.checkEaAccount(customer.getId() + "");

		for (ServiceTypeInfo serviceTypeInfo : serviceTypeInfos) {
			//
	        CustomerAccount social = new CustomerAccountImpl();
	        social.setAccountId(Integer.valueOf(serviceTypeInfo.getCustomerServiceId()));
	        social.setMailConfig(mc);
	        social.setGroup(serviceTypeInfo.getSubGroupCode());
	        social.setCompany("TRS");
	        social.setLeverage(Short.valueOf(serviceTypeInfo.getLeverage()));
	        
	        //SignalProvider
	        if(serviceTypeInfo.getServiceType() == ServiceType.NTD_FX)
	        	social.setSignalProvider(SocialSignalProvider.NTD_FX.getValue());
	        else if(serviceTypeInfo.getServiceType() == ServiceType.SC) {
	        	if(isEaAccount)
	        		social.setSignalProvider(SocialSignalProvider.TRS_EA.getValue());
	        	else
	        		social.setSignalProvider(SocialSignalProvider.SOCIAL.getValue());	
	        }
	        
	        //Check is EA account && is social
	        if(serviceTypeInfo.getServiceType() == ServiceType.SC && !isEaAccount)
	        	social.setAccountType(AccountType.SOCIAL);
	        else
	        	social.setAccountType(AccountType.SIGNAL_PROVIDER);
	        	
	        
	        social.setOpenDatetime(System.currentTimeMillis());
	        social.setDepositCurrency(CurrencyCode.valueOfName(serviceTypeInfo.getDepositCurrency()));
	        social.setPilotingStatus(SocialSignalFlg.ON.getNumber() == serviceTypeInfo.getSignalFlg()
	        		? PilotingStatus.ON : PilotingStatus.OFF);
	        social.setAccountStatus(AccountStatus.ACTIVATING);
	        
	        social.setLoginRestriction(LoginRestriction.NORMAL);
	        social.setTradingRestriction(TradingRestriction.NORMAL);
	        social.setTransferRestriction(TransferRestriction.NORMAL);
	        r.add(social);
		}
		
        return r;
	}
}