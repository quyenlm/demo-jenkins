package phn.nts.ams.fe.business.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;
import phn.com.nts.db.dao.IAmsCashBalanceDAO;
import phn.com.nts.db.dao.IAmsCustomerDAO;
import phn.com.nts.db.dao.IAmsCustomerServiceDAO;
import phn.com.nts.db.dao.IAmsDepositDAO;
import phn.com.nts.db.dao.IAmsPromotionBaseCcyDAO;
import phn.com.nts.db.dao.IAmsPromotionCustomerDAO;
import phn.com.nts.db.dao.IAmsPromotionDAO;
import phn.com.nts.db.dao.IAmsTransferMoneyDAO;
import phn.com.nts.db.dao.IFxSymbolDAO;
import phn.com.nts.db.dao.IScCustomerServiceDAO;
import phn.com.nts.db.dao.ISysAppDateDAO;
import phn.com.nts.db.dao.ISysCurrencyDAO;
import phn.com.nts.db.dao.ISysUniqueidCounterDAO;
import phn.com.nts.db.dao.impl.BoCustomerDAO;
import phn.com.nts.db.entity.AmsCashBalance;
import phn.com.nts.db.entity.AmsCashBalanceId;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsCustomerService;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsPromotion;
import phn.com.nts.db.entity.AmsPromotionBaseCcy;
import phn.com.nts.db.entity.AmsPromotionCustomer;
import phn.com.nts.db.entity.AmsTransferMoney;
import phn.com.nts.db.entity.BoCustomer;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.ScCustomerService;
import phn.com.nts.db.entity.SysAppDate;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.db.entity.SysUniqueidCounter;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.IConstants.TRANSFER_STATUS;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.Result;
import phn.com.trs.util.enums.MT4UpdateBalanceStatus;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.common.AbstractManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.IJmsContextSender;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.domain.UpdateBoBalanceInfo;
import phn.nts.ams.fe.jms.managers.BoManager;
import phn.nts.ams.fe.model.TransferModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.ntd.NTDManager;
import phn.nts.ams.fe.promotion.IPromotionManager;
import phn.nts.ams.utils.Helper;
import phn.nts.ams.fe.social.SCManager;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferResponse;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferStatus;

import com.nts.common.Constant;
import com.nts.common.Constant.CashflowType;
import com.nts.common.exchange.bean.BalanceUpdateInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.AccountInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.TradingCashflowType;
import com.nts.common.exchange.proto.fx.FxAuthModel.TradingRestriction;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.phn.mt.common.constant.IResultConstant;
import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.FundResultRecord;
import com.phn.mt.common.entity.UserRecord;

public class TransferManagerImpl extends AbstractManager implements ITransferManager {
	private static final Logit log = Logit.getInstance(TransferManagerImpl.class);
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO;
	private ISysUniqueidCounterDAO<SysUniqueidCounter> iSysUniqueidCounterDAO;
	private IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO;
	private IAmsPromotionDAO<AmsPromotion> iAmsPromotionDAO;
	private IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> iAmsPromotionBaseCcyDAO;
	private IAmsPromotionCustomerDAO<AmsPromotionCustomer> iAmsPromotionCustomerDAO;
	private ISysAppDateDAO<SysAppDate> iSysAppDateDAO;
	private IPromotionManager iPromotionManager;
	private IDepositManager iDepositManager;
	private IFxSymbolDAO<FxSymbol> iFxSymbolDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> iAmsCashBalanceDAO;
	private IBalanceManager balanceManager;
	private ISysCurrencyDAO<SysCurrency> iSysCurrencyDAO;
	private IAmsDepositDAO<AmsDeposit> iAmsDepositDAO ;
	private IAccountManager iAccountManager;
	private IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO;
	private IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO;
	private IJmsContextSender jmsContextSender;
	private String AMS_DISSOLVE_AKAZAN_SUCCESS_JA = "AMS_DISSOLVE_AKAZAN_SUCCESS_JA";
	private BoManager boManager;
	private BoCustomerDAO boCustomerDAO;
	
	public BoManager getBoManager() {
		return boManager;
	}
	public void setBoManager(BoManager boManager) {
		this.boManager = boManager;
	}
	public BoCustomerDAO getBoCustomerDAO() {
		return boCustomerDAO;
	}
	public void setBoCustomerDAO(BoCustomerDAO boCustomerDAO) {
		this.boCustomerDAO = boCustomerDAO;
	}
	public IAmsCashBalanceDAO<AmsCashBalance> getAmsCashBalanceDAO() {
		return amsCashBalanceDAO;
	}
	public void setAmsCashBalanceDAO(
			IAmsCashBalanceDAO<AmsCashBalance> amsCashBalanceDAO) {
		this.amsCashBalanceDAO = amsCashBalanceDAO;
	}
	public IScCustomerServiceDAO<ScCustomerService> getScCustomerServiceDAO() {
		return scCustomerServiceDAO;
	}
	public void setScCustomerServiceDAO(
			IScCustomerServiceDAO<ScCustomerService> scCustomerServiceDAO) {
		this.scCustomerServiceDAO = scCustomerServiceDAO;
	}
	/**
	 * @param iAccountManager the iAccountManager to set
	 */
	public void setiAccountManager(IAccountManager iAccountManager) {
		this.iAccountManager = iAccountManager;
	}
	/**
	 * @param balanceManager the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
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
	 * @return the iPromotionManager
	 */
	public IPromotionManager getiPromotionManager() {
		return iPromotionManager;
	}
	/**
	 * @param iPromotionManager the iPromotionManager to set
	 */
	public void setiPromotionManager(IPromotionManager iPromotionManager) {
		this.iPromotionManager = iPromotionManager;
	}
	/**
	 * @return the iAmsCustomerDAO
	 */
	public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}
	/**
	 * @param iAmsCustomerDAO the iAmsCustomerDAO to set
	 */	
	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}
	
	
	/**
	 * @return the iAmsPromotionDAO
	 */
	public IAmsPromotionDAO<AmsPromotion> getiAmsPromotionDAO() {
		return iAmsPromotionDAO;
	}
	/**
	 * @param iAmsPromotionDAO the iAmsPromotionDAO to set
	 */
	public void setiAmsPromotionDAO(IAmsPromotionDAO<AmsPromotion> iAmsPromotionDAO) {
		this.iAmsPromotionDAO = iAmsPromotionDAO;
	}
	
	
	/**
	 * @return the iAmsPromotionBaseCcyDAO
	 */
	public IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> getiAmsPromotionBaseCcyDAO() {
		return iAmsPromotionBaseCcyDAO;
	}
	/**
	 * @param iAmsPromotionBaseCcyDAO the iAmsPromotionBaseCcyDAO to set
	 */
	public void setiAmsPromotionBaseCcyDAO(
			IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> iAmsPromotionBaseCcyDAO) {
		this.iAmsPromotionBaseCcyDAO = iAmsPromotionBaseCcyDAO;
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
	public void setiAmsTransferMoneyDAO(IAmsTransferMoneyDAO<AmsTransferMoney> iAmsTransferMoneyDAO) {
		this.iAmsTransferMoneyDAO = iAmsTransferMoneyDAO;
	}
	
	
	/**
	 * @return the iAmsPromotionCustomerDAO
	 */
	public IAmsPromotionCustomerDAO<AmsPromotionCustomer> getiAmsPromotionCustomerDAO() {
		return iAmsPromotionCustomerDAO;
	}
	/**
	 * @param iAmsPromotionCustomerDAO the iAmsPromotionCustomerDAO to set
	 */
	public void setiAmsPromotionCustomerDAO(IAmsPromotionCustomerDAO<AmsPromotionCustomer> iAmsPromotionCustomerDAO) {
		this.iAmsPromotionCustomerDAO = iAmsPromotionCustomerDAO;
	}
	/**
	 * @return the iDepositManager
	 */
	public IDepositManager getiDepositManager() {
		return iDepositManager;
	}
	/**
	 * @param iDepositManager the iDepositManager to set
	 */
	public void setiDepositManager(IDepositManager iDepositManager) {
		this.iDepositManager = iDepositManager;
	}
	
	public CustomerInfo getCustomerInfo(String customerId){
		CustomerInfo customerInfo = null;
		try {
			customerInfo = new CustomerInfo();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, customerId);
			BeanUtils.copyProperties(amsCustomer, customerInfo);
		} catch (BeansException e) {
			log.error(e.getMessage(), e);
		}
		
		return customerInfo;
	}
	
	/**
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jan 21, 2016
	 * @MdDate
	 * @deprecated Not safe transaction. AMS_TRANFER_MONEY record will loss if exception occur when commit transaction
	 */
	public Integer transferMoney(TransferMoneyInfo transferMoneyInfo, String currencyCode){		
		log.info("[start] transferMoney, TransferMoneyInfo: " + transferMoneyInfo + ", currencyCode: " + currencyCode);
		
		Integer result = IConstants.TRANSFER_STATUS.FAIL;
		AmsTransferMoney amsTransferMoney  = null;
		try {
			SysAppDate amsAppDate = null;
			List<SysAppDate> listAmsAppDate = getiSysAppDateDAO().findByProperty("id.dateKey", IConstants.APP_DATE.FRONT_DATE);
			if(listAmsAppDate != null && listAmsAppDate.size() > 0) {
				amsAppDate = listAmsAppDate.get(0);
				if(amsAppDate != null) {
					log.info("FRONT_DATE: " + amsAppDate.getId().getFrontDate());					
				}
			}
			
			log.info("[start] recalculate rate");
			
			BigDecimal convertRate = MathUtil.parseBigDecimal("1"); // default = 1
			BigDecimal convertedAmount = MathUtil.parseBigDecimal("0");
			
			//[NTS1.0-Mai.Thu.Huyen]Oct 5, 2012A - Start 
			CurrencyInfo fromCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + transferMoneyInfo.getFromCurrencyCode());
			CurrencyInfo toCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY +  transferMoneyInfo.getToCurrencyCode());
			
			if (fromCurrencyInfo == null || toCurrencyInfo == null) {
				log.info("Invalid CurrencyInfo, fromCurrencyInfo: " + fromCurrencyInfo + ", toCurrencyInfo: " + toCurrencyInfo);
				return IConstants.TRANSFER_STATUS.FAIL;
			}
			convertRate = getConvertRateOnFrontRate(fromCurrencyInfo.getCurrencyCode(), toCurrencyInfo.getCurrencyCode(), IConstants.FRONT_OTHER.SCALE_ALL);			
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 5, 2012A - Start format converted amount
			
			BigDecimal tranferAmount = MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney());
			transferMoneyInfo.setTransferMoney(tranferAmount.divide(MathUtil.parseBigDecimal(1), toCurrencyInfo.getCurrencyDecimal(), toCurrencyInfo.getCurrencyRound()).doubleValue());
			convertedAmount = tranferAmount.divide(convertRate, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 5, 2012A - End
			
			log.info("ConvertRate: " + convertRate.doubleValue() +  ", ConvertedAmount: " + convertedAmount.doubleValue());
			log.info("[end] recalculate rate");
			
			//Validate converted amount
			if(validateConvertedAmountToTransfer(transferMoneyInfo, convertedAmount, currencyCode)){
				log.info("Validate Converted Amount to Transfer: WITHDRAW_NOT_ENOUGH_MONEY");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}
			
			String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
			transferMoneyInfo.setTransferMoneyId(transferMoneyId);
			transferMoneyInfo.setRate(convertRate);
			
			log.info("[start] register transfer money of transferMoneyId: " + transferMoneyId + " for customerID: " + transferMoneyInfo.getCustomerId());		
			amsTransferMoney = new AmsTransferMoney(); 
			amsTransferMoney.setTransferMoneyId(transferMoneyId);
			amsTransferMoney.setTransferFrom(transferMoneyInfo.getTransferFrom()); 
			amsTransferMoney.setTransferTo(transferMoneyInfo.getTransferTo());
			amsTransferMoney.setTransferMoney(convertedAmount.doubleValue());			
			amsTransferMoney.setDestinationAmount(transferMoneyInfo.getTransferMoney());
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
			
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, transferMoneyInfo.getCustomerId());
			if(amsCustomer != null){
				amsTransferMoney.setAmsCustomer(amsCustomer); 
			}
			amsTransferMoney.setRate(convertRate.doubleValue());
			amsTransferMoney.setCurrencyCode(transferMoneyInfo.getFromCurrencyCode());
			amsTransferMoney.setDestinationCurrencyCode(transferMoneyInfo.getToCurrencyCode());
			if(amsAppDate != null){
				amsTransferMoney.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
			}
			amsTransferMoney.setTranferAcceptDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsTransferMoney.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsTransferMoney.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsTransferMoney.setWlRefId(null);
			amsTransferMoney.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			
			getiAmsTransferMoneyDAO().save(amsTransferMoney);   //register AmsTransferMoney with status is INPROGRESS
			getiAmsTransferMoneyDAO().flush();
			
			log.info("[end] register transfer money of transferMoneyId: " + transferMoneyId + " with status: " + IConstants.TRANSFER_STATUS.INPROGRESS);			
			
			result = transferMoney(transferMoneyInfo, amsTransferMoney, amsAppDate);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(amsTransferMoney != null){
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
			}
			result = IConstants.TRANSFER_STATUS.FAIL;
		}
		log.info("[end] transferMoney, TRANSFER_STATUS: " + result + " TransferMoneyInfo: " + transferMoneyInfo + ", currencyCode: " + currencyCode);
		return result;
	}
	
	public Integer registerTransferMoney(TransferMoneyInfo transferMoneyInfo, String currencyCode){		
		log.info("[start] registerTransferMoney, TransferMoneyInfo: " + transferMoneyInfo + ", currencyCode: " + currencyCode);
		
		Integer result = IConstants.TRANSFER_STATUS.FAIL;
		AmsTransferMoney amsTransferMoney  = null;
		try {
			SysAppDate amsAppDate = SystemPropertyConfig.getAmsAppDate();
			log.info("FRONT_DATE: " + amsAppDate);
			log.info("[start] recalculate rate");
			
			BigDecimal convertRate = MathUtil.parseBigDecimal("1"); // default = 1
			BigDecimal convertedAmount = MathUtil.parseBigDecimal("0");
			
			//[NTS1.0-Mai.Thu.Huyen]Oct 5, 2012A - Start 
			CurrencyInfo fromCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + transferMoneyInfo.getFromCurrencyCode());
			CurrencyInfo toCurrencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY +  transferMoneyInfo.getToCurrencyCode());
			
			if (fromCurrencyInfo == null || toCurrencyInfo == null) {
				log.info("Invalid CurrencyInfo, fromCurrencyInfo: " + fromCurrencyInfo + ", toCurrencyInfo: " + toCurrencyInfo);
				return IConstants.TRANSFER_STATUS.FAIL;
			}
			convertRate = getConvertRateOnFrontRate(fromCurrencyInfo.getCurrencyCode(), toCurrencyInfo.getCurrencyCode(), IConstants.FRONT_OTHER.SCALE_ALL);			
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 5, 2012A - Start format converted amount
			
			BigDecimal tranferAmount = MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney());
			transferMoneyInfo.setTransferMoney(tranferAmount.divide(MathUtil.parseBigDecimal(1), toCurrencyInfo.getCurrencyDecimal(), toCurrencyInfo.getCurrencyRound()).doubleValue());
			convertedAmount = tranferAmount.divide(convertRate, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
			//[NatureForex1.0-Mai.Thu.Huyen]Oct 5, 2012A - End
			
			log.info("ConvertRate: " + convertRate.doubleValue() +  ", ConvertedAmount: " + convertedAmount.doubleValue());
			log.info("[end] recalculate rate");
			
			//Validate converted amount
			if(validateConvertedAmountToTransfer(transferMoneyInfo, convertedAmount, currencyCode)){
				log.info("Validate Converted Amount to Transfer: WITHDRAW_NOT_ENOUGH_MONEY");
				return IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY;
			}
			
			String transferMoneyId = generateUniqueId(IConstants.UNIQUE_CONTEXT.TRANFER_MONEY_CONTEXT);
			transferMoneyInfo.setTransferMoneyId(transferMoneyId);
			transferMoneyInfo.setRate(convertRate);
			
			log.info("[start] register transfer money of transferMoneyId: " + transferMoneyId + " for customerID: " + transferMoneyInfo.getCustomerId());		
			amsTransferMoney = new AmsTransferMoney(); 
			amsTransferMoney.setTransferMoneyId(transferMoneyId);
			amsTransferMoney.setTransferFrom(transferMoneyInfo.getTransferFrom()); 
			amsTransferMoney.setTransferTo(transferMoneyInfo.getTransferTo());
			amsTransferMoney.setTransferMoney(convertedAmount.doubleValue());			
			amsTransferMoney.setDestinationAmount(transferMoneyInfo.getTransferMoney());
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
			
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, transferMoneyInfo.getCustomerId());
			if(amsCustomer != null){
				amsTransferMoney.setAmsCustomer(amsCustomer); 
			}
			amsTransferMoney.setRate(convertRate.doubleValue());
			amsTransferMoney.setCurrencyCode(transferMoneyInfo.getFromCurrencyCode());
			amsTransferMoney.setDestinationCurrencyCode(transferMoneyInfo.getToCurrencyCode());
			if(amsAppDate != null){
				amsTransferMoney.setTranferAcceptDate(amsAppDate.getId().getFrontDate());
			}
			amsTransferMoney.setTranferAcceptDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
			amsTransferMoney.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
			amsTransferMoney.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
			amsTransferMoney.setWlRefId(null);
			//[TRSGAP-1400-quyen.le.manh]Jul 28, 2016A - Start - Add Remark for Transfer flow 
			amsTransferMoney.setRemark(transferMoneyInfo.getRemark());
			//[TRSGAP-1400-quyen.le.manh]Jul 28, 2016A - End
			amsTransferMoney.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			result = IConstants.TRANSFER_STATUS.SUCCESS;
			getiAmsTransferMoneyDAO().save(amsTransferMoney);   //register AmsTransferMoney with status is INPROGRESS
			getiAmsTransferMoneyDAO().flush();
			
			log.info("[end] register transfer money of transferMoneyId: " + transferMoneyId + " with status: " + IConstants.TRANSFER_STATUS.INPROGRESS);			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(amsTransferMoney != null){
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
			}
			result = IConstants.TRANSFER_STATUS.FAIL;
		}
		log.info("[end] registerTransferMoney, TransferMoneyInfo: " + transferMoneyInfo + ", currencyCode: " + currencyCode);
		return result;
	}
	
	public Integer transferMoney(TransferMoneyInfo transferMoneyInfo) throws Exception{		
		log.info("[start] executeTransferMoney, TransferMoneyInfo: " + transferMoneyInfo);
		
		if(transferMoneyInfo.getTransferMoneyId() == null)
			throw new Exception("TransferMoneyId is empty! Must registerTransferMoney first!");
		
		Integer result = IConstants.TRANSFER_STATUS.FAIL;
		AmsTransferMoney amsTransferMoney  = null;
		try {
			SysAppDate amsAppDate = SystemPropertyConfig.getAmsAppDate();
			log.info("FRONT_DATE: " + amsAppDate);
			
			amsTransferMoney = getiAmsTransferMoneyDAO().findById(AmsTransferMoney.class, transferMoneyInfo.getTransferMoneyId());
			
			result = transferMoney(transferMoneyInfo, amsTransferMoney, amsAppDate);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(amsTransferMoney != null){
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
			}
			result = IConstants.TRANSFER_STATUS.FAIL;
		}
		log.info("[end] executeTransferMoney, TRANSFER_STATUS: " + result + " TransferMoneyInfo: " + transferMoneyInfo);
		return result;
	}
	
	/**
	 * 
	 * @param accountId
	 * @param akazanStatus
	 * @return
	 */
	public Integer changeDissolveStatusAkazan(String akazanStatus, String customerId) {
		boolean dissolveAkazanStatus = 	iAmsTransferMoneyDAO.checkDissolveStatusAkazan(customerId);
		if(!dissolveAkazanStatus)
			return IConstants.UPDATE_ACCOUNT_RESULT.FAILURE;
		
		log.info("[start] changeDissolveStatusAkazan, customerId: " + customerId + ", akazanStatus: " + akazanStatus);
		
		boolean ntdAkazanOk = true;
		AmsCustomerService ntdCustomerService = iAmsCustomerServiceDAO.getCustomerServicesInfo(customerId, ITrsConstants.SERVICES_TYPE.NTD_FX);

		boolean isEaAccount = iAccountManager.checkEaAccount(customerId);
		// Customer is EA no need call to NTD get balance check akazan
		if (!isEaAccount) {
			if(ntdCustomerService != null){
				//Check balance of NTD account
				ntdAkazanOk = false;

				if(ntdCustomerService.getNtdAccountId() != null) {
					log.info("Get balance of ntdAccountId: " + ntdCustomerService.getNtdAccountId());
					BalanceInfo balanceInfo = NTDManager.getInstance().getBalanceInfo(ntdCustomerService.getNtdAccountId());
					log.info("Get balance ntdAccountId: " + ntdCustomerService.getNtdAccountId() + ", balance: " + balanceInfo.getBalance());

					if(balanceInfo != null && balanceInfo.getBalance().compareTo(0D) >= 0){
						ntdAkazanOk = true;
					}
				} else
					log.warn("Invalid ntdAccountId: " + ntdCustomerService.getNtdAccountId() + ", customerServiceId: " + ntdCustomerService.getCustomerServiceId());
			}
		}

		Integer result = IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS;
		List<Integer> lstToSendMail = new ArrayList<Integer>();
		
		if(dissolveAkazanStatus && ntdAkazanOk) {
			//Update account status to RE
			List<AmsCustomerService> listCustomerServices = iAmsCustomerServiceDAO.getListCustomerServices(customerId);
			
			for (AmsCustomerService amsCustomerService : listCustomerServices) {
				if(!Helper.isNormalAccount(amsCustomerService.getCustomerServiceStatus())) {
					log.warn("NOT change status for uncomplete/cancel customerServiceId: " + amsCustomerService.getCustomerServiceId() + ", customerServiceStatus: " + amsCustomerService.getCustomerServiceStatus());
					continue;
				}
				
				if(ITrsConstants.SERVICES_TYPE.FX.equals(amsCustomerService.getServiceType())
						|| ITrsConstants.SERVICES_TYPE.SOCIAL_COPY_TRADE.equals(amsCustomerService.getServiceType())) {
					log.info(" [start] Dissolve status akazan of customerServiceId: " + amsCustomerService.getCustomerServiceId());
					
					UserRecord userRecordMt4 = new UserRecord();
					userRecordMt4.setLogin(MathUtil.parseInt(amsCustomerService.getCustomerServiceId()));
					userRecordMt4.setStatus(akazanStatus);
					
					Integer updateMt4Result = MT4Manager.getInstance().updateAccountMt4(userRecordMt4);
					result = IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(updateMt4Result) ? result : IConstants.UPDATE_ACCOUNT_RESULT.FAILURE;
					lstToSendMail.add(amsCustomerService.getServiceType());
					log.info(" [end] Dissolve status akazan of customerServiceId: " + amsCustomerService.getCustomerServiceId() + ", result: " + updateMt4Result + " - " + Result.valueOf(updateMt4Result));
				} else if (ITrsConstants.SERVICES_TYPE.NTD_FX.equals(amsCustomerService.getServiceType())) {
					// Customer is EA no need call to NTD dissolve akazan
					if (!isEaAccount) {
						String ntdAccountId = amsCustomerService.getNtdAccountId();
						int updateNtdResult = Constant.RESULT_UNKNOWN;

						if(ntdAccountId == null) {
							log.warn("Invalid ntdAccountId: " + ntdAccountId + ". Can not Dissolve status akazan for customerServiceId: " + amsCustomerService.getCustomerServiceId());
							updateNtdResult = Constant.RESULT_FAIL;
						} else {
							log.info(" [start] Dissolve status akazan of customerServiceId: " + amsCustomerService.getCustomerServiceId());

							AccountInfo.Builder accountBuilder = AccountInfo.newBuilder();
							accountBuilder.setTradingRestriction(TradingRestriction.TRADING_NORMAL_VALUE);
							accountBuilder.setNtdAccountId(ntdAccountId);

							updateNtdResult = NTDManager.getInstance().updateAccountInfo(accountBuilder.build());

							if (Constant.RESULT_SUCCESS == updateNtdResult) {
								lstToSendMail.add(amsCustomerService.getServiceType());
								try {
									log.info("update AllowTransactFlg = 1 to DB, customerServiceId: " + amsCustomerService.getCustomerServiceId());
									ntdCustomerService = iAmsCustomerServiceDAO.getCustomerServicesInfo(customerId, ITrsConstants.SERVICES_TYPE.NTD_FX);
									ntdCustomerService.setAllowTransactFlg(1);
									ntdCustomerService.setUpdateDate(new Timestamp(System.currentTimeMillis()));
									iAmsCustomerServiceDAO.merge(ntdCustomerService);
								} catch (Exception e) {
									log.error(e.getMessage(), e);
								}
							}

							log.info(" [end] Dissolve status akazan of customerServiceId: " + amsCustomerService.getCustomerServiceId() + ", result: " + updateNtdResult + " - " + Result.valueOf(updateNtdResult));
						}

						result = Constant.RESULT_SUCCESS == updateNtdResult ? result : IConstants.UPDATE_ACCOUNT_RESULT.FAILURE;
					}
				}
			}
			
			//Send mail to Customer
			if(IConstants.UPDATE_ACCOUNT_RESULT.SUCCESS.equals(result)) {
				log.info("Change Accounts.Status = 'RE' of customerId: "  + customerId + " is SUCCESS");
				for (Integer serviceType : lstToSendMail) {
					sendMailDissolveStatusAkazan(customerId, serviceType);
				}
			} else {
				log.info("Change Accounts.Status = 'RE' of customerId: "  + customerId + " is FAIL");
			}
		}
		
		log.info("[end] changeDissolveStatusAkazan, customerId: " + customerId + ", akazanStatus: " + akazanStatus);
		
		return result;
	}
	/**
	 * @param customerId
	 * @param mailSubject
	 */
	/*public void sendMailDissolveStatusAkazan(String customerId, String mailSubject){
		MailInfo mi = new MailInfo();
		AmsCustomer entity = iAmsCustomerDAO.getCustomerInfo(customerId);
		mi.setFullName(entity.getFullName());
		try {
			mailService.sendAppMail(mi,entity.getMailMain(),AppConfiguration.getMailAdminSender(),mailSubject,AMS_DISSOLVE_AKAZAN_SUCCESS_JA);
		} catch (Exception e) {
			log.info(" Send mail to customer when dissolve status akazan of customer was FAIL " + customerId);
			e.printStackTrace();
		}
	}*/
	
	public void sendMailDissolveStatusAkazan(String customerId, Integer serviceType){
		log.info("[start] send mail DissolveStatusAkazan to customerId " + customerId + ", serviceType: " + serviceType);
		AmsCustomer entity = iAmsCustomerDAO.getCustomerInfo(customerId);
		
		TrsMailTemplateInfo trsAmsMailTemplateInfo = new TrsMailTemplateInfo();
		trsAmsMailTemplateInfo.setFullName(entity.getFullName());
		trsAmsMailTemplateInfo.setCorporationType(String.valueOf(serviceType));
		trsAmsMailTemplateInfo.setLoginId(entity.getLoginId());
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(entity.getMailMain(), entity.getMailMain());
		trsAmsMailTemplateInfo.setTo(to);																												
		trsAmsMailTemplateInfo.setMailCode(AMS_DISSOLVE_AKAZAN_SUCCESS_JA);
		trsAmsMailTemplateInfo.setSubject(AMS_DISSOLVE_AKAZAN_SUCCESS_JA);
		trsAmsMailTemplateInfo.setWlCode(entity.getWlCode());
		jmsContextSender.sendMail(trsAmsMailTemplateInfo, false);
		log.info("[end] send mail DissolveStatusAkazan to customerId " + customerId);

	}

	private Integer transferMoney(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate) {
		Integer fromServiceType = transferMoneyInfo.getFromServicesInfo().getServiceType();
		Integer toServiceType = transferMoneyInfo.getToServicesInfo().getServiceType();
		
		String description = getFundBalanceDescription(fromServiceType, toServiceType);
		String creditDescription = getFundCreditDescription(fromServiceType, toServiceType);
		Double creditAmount = new Double("0");

		Integer transferResult = IConstants.TRANSFER_STATUS.INPROGRESS;
		
		//[TRSM1-2195-quyen.le.manh]Jan 20, 2016D - Start withdraw/deposit MT4/NTD/BO first then withdraw/deposit AMS to avoid loss money when MT4/NTD fail
		
		if(IConstants.SERVICES_TYPE.AMS.equals(toServiceType)) {
			//withdrawFromSourceAccount (MT4/NTD/BO) first
			transferResult = withdrawFromSourceAccount(fromServiceType, transferMoneyInfo, amsTransferMoney, 
					sysAppDate, creditAmount, description, creditDescription);
		
			//then depositToDestinationAccount (AMS)
			if(transferResult.equals(IConstants.TRANSFER_STATUS.SUCCESS)) {
				transferResult = depositToDestinationAccount(toServiceType, transferMoneyInfo, amsTransferMoney, 
						sysAppDate, creditAmount, description, creditDescription);
			}
		} else {
			//depositToDestinationAccount (MT4/NTD/BO) first
			transferResult = depositToDestinationAccount(toServiceType, transferMoneyInfo, amsTransferMoney, 
					sysAppDate, creditAmount, description, creditDescription);
			
			//then withdrawFromSourceAccount (AMS)
			if(transferResult.equals(IConstants.TRANSFER_STATUS.SUCCESS)) {
				transferResult = withdrawFromSourceAccount(fromServiceType, transferMoneyInfo, amsTransferMoney, 
							sysAppDate, creditAmount, description, creditDescription);
			}
		}
		
//		// Process from service 
//		if(IConstants.SERVICES_TYPE.AMS.equals(fromServiceType) || IConstants.SERVICES_TYPE.BO.equals(fromServiceType)) {
//			transferResult = withdrawFromPlainService(transferMoneyInfo, amsTransferMoney, sysAppDate);
//		} else if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromServiceType)) {
//			//MT4
//			transferResult = withdrawFromMT4Service(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount, description, creditDescription);	
//		} else {
//			//NTD
//			transferResult = withdrawDepositNTDService(transferMoneyInfo, amsTransferMoney, 
//					sysAppDate, creditAmount, description, creditDescription, CashflowType.WITHDRAW);
//		}
//		
//		if(transferResult.equals(IConstants.TRANSFER_STATUS.SUCCESS)) {
//			// Process to service
//			if(IConstants.SERVICES_TYPE.AMS.equals(toServiceType) || IConstants.SERVICES_TYPE.BO.equals(toServiceType)) {
//				transferResult = depositToPlainService(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount);
//			} else if(IConstants.SERVICES_TYPE.FX.equals(toServiceType) || IConstants.SERVICES_TYPE.COPY_TRADE.equals(toServiceType)) {
//				//MT4
//				transferResult = depositToMT4Service(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount, description, creditDescription);
//			} else {
//				//NTD
//				transferResult = withdrawDepositNTDService(transferMoneyInfo, amsTransferMoney, 
//						sysAppDate, creditAmount, description, creditDescription, CashflowType.DEPOSIT);
//			}
//		}
		//[TRSM1-2195-quyen.le.manh]Jan 20, 2016D - End
			
		//Update status of AmsTransferMoney Table
		if(!transferResult.equals(IConstants.TRANSFER_STATUS.INPROGRESS)) {		
			log.info("[start] update status of transfer money, transferResult: " + transferResult);
			amsTransferMoney.setStatus(transferResult);

			// [TRSGAP-1576-cuong.bui.manh]Aug 11, 2016A - Start
			// Only update tranferCompleteDate when transfer success
			if (transferResult.equals(TRANSFER_STATUS.SUCCESS)) {
				amsTransferMoney.setTranferCompleteDate(sysAppDate.getId().getFrontDate());
				amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
			}
			//[TRSGAP-1576-cuong.bui.manh]Aug 11, 2016A - End

			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("[end] update status of transfer money, transferResult: " + transferResult);
		}
		
		return transferResult;
	}
	
	private Integer withdrawFromSourceAccount(Integer fromServiceType, TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney,
			SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription) {
		Integer transferResult = IConstants.TRANSFER_STATUS.INPROGRESS;
		
		// Process from service 
		if(IConstants.SERVICES_TYPE.AMS.equals(fromServiceType) || IConstants.SERVICES_TYPE.BO.equals(fromServiceType)) {
			transferResult = withdrawFromPlainService(transferMoneyInfo, amsTransferMoney, sysAppDate);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromServiceType)) {
			//New Social system
			transferResult = depositWithdrawSocialSystem(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount, description, creditDescription, false);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType) /*|| IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromServiceType)*/) {
			//MT4
			transferResult = withdrawFromMT4Service(transferMoneyInfo, amsTransferMoney,
					sysAppDate, creditAmount, description, creditDescription);	
		} else {
			//NTD
			transferResult = withdrawDepositNTDService(transferMoneyInfo, amsTransferMoney, 
					sysAppDate, creditAmount, description, creditDescription, CashflowType.WITHDRAW);
		}
		
		return transferResult;
	}
	
	private Integer depositToDestinationAccount(Integer toServiceType, TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney,
			SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription) {
		Integer transferResult = IConstants.TRANSFER_STATUS.INPROGRESS;
		
		// Process to service
		if(IConstants.SERVICES_TYPE.AMS.equals(toServiceType) || IConstants.SERVICES_TYPE.BO.equals(toServiceType)) {
			transferResult = depositToPlainService(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(toServiceType)) {
			//New Social system
			transferResult = depositWithdrawSocialSystem(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount, description, creditDescription, true);
		} else if(IConstants.SERVICES_TYPE.FX.equals(toServiceType) /* || IConstants.SERVICES_TYPE.COPY_TRADE.equals(toServiceType)*/) {
			//MT4
			transferResult = depositToMT4Service(transferMoneyInfo, amsTransferMoney, sysAppDate, creditAmount, description, creditDescription);
		} else {
			//NTD
			transferResult = withdrawDepositNTDService(transferMoneyInfo, amsTransferMoney, 
					sysAppDate, creditAmount, description, creditDescription, CashflowType.DEPOSIT);
		}
		
		return transferResult;
	}
	private Integer withdrawFromPlainService(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate){
		String fromCurrencyCode = null;
		String fromCustomerId = null;
		String fromCustomerServiceId = null;
		CustomerServicesInfo fromCustomerServiceInfo = transferMoneyInfo.getFromServicesInfo();
		Integer fromServiceType = fromCustomerServiceInfo.getServiceType();
		
		boolean isBoService = IConstants.SERVICES_TYPE.BO.equals(fromServiceType);
		if(IConstants.SERVICES_TYPE.AMS.equals(fromServiceType)){
			fromCustomerId = transferMoneyInfo.getCustomerId();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, fromCustomerId);
			if(amsCustomer != null) {
				SysCurrency sysCurrency = amsCustomer.getSysCurrency();
				if(sysCurrency != null) {
					fromCurrencyCode = sysCurrency.getCurrencyCode();
				}
			}
			fromCustomerServiceId = null;
		}else{
			fromCurrencyCode = fromCustomerServiceInfo.getCurrencyCode();
			fromCustomerId = fromCustomerServiceInfo.getCustomerId();
			fromCustomerServiceId = fromCustomerServiceInfo.getCustomerServiceId();
		}
		
		if(StringUtil.isEmpty(fromCurrencyCode) || StringUtil.isEmpty(fromCustomerId)) {
			log.warn("from currency code and from customerId is null");
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setTranferCompleteDateTime(null);
			iAmsTransferMoneyDAO.merge(amsTransferMoney);
			return IConstants.COMMON_RESULT.FAIL; 
		}
		if(!isBoService){
			AmsCashBalance amsCashBalance = new AmsCashBalance();
			AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
			amsCashBalanceId.setCurrencyCode(fromCurrencyCode);
			amsCashBalanceId.setCustomerId(fromCustomerId);
			amsCashBalanceId.setServiceType(fromServiceType);
			amsCashBalance = iAmsCashBalanceDAO.findById(AmsCashBalance.class, amsCashBalanceId);
			if(amsCashBalance == null) {
				log.warn("Cannot find cashbalance for customerId = " + fromCustomerId + ", currencyCOde = " + fromCurrencyCode + ", serviceType = " + fromServiceType);
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
				return IConstants.COMMON_RESULT.FAIL; 
			}
			// update cashflow
			Double balance = amsCashBalance.getCashBalance();
			balance = balance - transferMoneyInfo.getConvertedAmount();
			
			log.info("[start] insert cashflow to " + fromServiceType + " (from service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			
			Double negateConvertedAmount = 0 - transferMoneyInfo.getConvertedAmount();
			iDepositManager.insertCashFlow(transferMoneyInfo.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), fromCustomerServiceId, sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER ,negateConvertedAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, fromServiceType, balance, transferMoneyInfo.getRate().doubleValue(), fromCurrencyCode);
			log.info("[end] insert cashflow to ams (from service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());			
			// update cashbalance
			log.info("deduct balance of " + fromServiceType + " with transferId = " + transferMoneyInfo.getTransferMoneyId());
			
			log.info("[start] update deduct cashbalance to ams (from service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			iDepositManager.updateAmsCashBalance(fromCustomerId, fromCurrencyCode, fromServiceType, transferMoneyInfo.getConvertedAmount().doubleValue(), new Double(0), true);
			log.info("[end] update cashbalance to ams (from service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
		} else {
			log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
			UpdateBoBalanceInfo updateBoBalanceInfo = boManager.withdraw(fromCustomerServiceId, MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), MathUtil.parseBigDecimal(String.valueOf(amsTransferMoney.getRate())),transferMoneyInfo.getTransferMoneyId(),IConstants.SOURCE_TYPE.TRANFER_MONEY,IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER);
        	Integer boResult = updateBoBalanceInfo.getResult();
        	Integer transferResult = getTransferResultFromCommonResult(boResult);
        	if(!IConstants.TRANSFER_STATUS.SUCCESS.equals(transferResult)){
        		return transferResult;
        	}
			log.info("======================================>[end] send topic for Refresh Balance Info of BO<======================================");
		}
		
		return IConstants.TRANSFER_STATUS.SUCCESS;
	}
	
	private Integer withdrawFromMT4Service(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription){
		log.info("check promotion for withdrawal from fx");
		CustomerServicesInfo fromCustomerServiceInfo = transferMoneyInfo.getFromServicesInfo();
		Integer fromServiceType = fromCustomerServiceInfo.getServiceType();
		
		int creditOut = ITrsConstants.FUND_CREDIT_MODE.CREDIT_OUT;
		Integer promotionKind = IConstants.PROMOTION_KIND.BASED_AMOUNT;
		AmsPromotion amsPromotion = iAmsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, fromServiceType, fromCustomerServiceInfo.getSubGroupId());
		if(amsPromotion != null) {
			promotionKind = amsPromotion.getKind();
		}
		if(promotionKind == null) promotionKind = IConstants.PROMOTION_KIND.BASED_AMOUNT;
		if(IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(promotionKind)) {
			creditOut = ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT;
		}
		
		FundRecord deductMt4 = new FundRecord();
		log.info("[start] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fromCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());		
		deductMt4 = MT4Manager.getInstance().withdrawBalance(fromCustomerServiceInfo.getCustomerServiceId(), transferMoneyInfo.getConvertedAmount().doubleValue(), FundRecord.BALANCE, description, creditOut);
		log.info("[end] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fromCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		if(deductMt4 != null) {
			if(IResultConstant.Withdraw.SUCCESSFUL == deductMt4.getResult()) {
				if(creditOut == ITrsConstants.FUND_CREDIT_MODE.CREDIT_OUT) {
					FundResultRecord fundResultRecord = deductMt4.getFundResultRecord();
					if(fundResultRecord != null) {
						creditAmount = fundResultRecord.getCreditDeduction();
					}
				} else {
					log.info("[start] calculate credit amount for customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fromCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
					log.info("getting net deposit of customerId = " + transferMoneyInfo.getCustomerId() + ", serviceType = " + fromCustomerServiceInfo.getServiceType());
					log.info("[start] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId());
					BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
					BigDecimal baseAmount = MathUtil.parseBigDecimal(0);
					AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
					amsCashBalanceFxId.setCurrencyCode(transferMoneyInfo.getFromCurrencyCode());
					amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
					amsCashBalanceFxId.setServiceType(fromCustomerServiceInfo.getServiceType());
					AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
					if(amsCashBalanceFx != null) {
						netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
						baseAmount = netDepositAmount.subtract(MathUtil.parseBigDecimal(transferMoneyInfo.getConvertedAmount()));
					}
					log.info("net deposit of customerId = " + transferMoneyInfo.getCustomerId() + ", serviceType = " + fromCustomerServiceInfo.getServiceType() + " = " + netDepositAmount);						
					log.info("[end] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId() + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
					BigDecimal deductCreditAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getFromCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, fromServiceType, fromCustomerServiceInfo.getSubGroupId(), netDepositAmount, fromCustomerServiceInfo.getCustomerId(), false); 
					creditAmount = deductCreditAmount == null ? new Double("0") : deductCreditAmount.abs().doubleValue();
					log.info("credit amount = " + creditAmount);						
					log.info("[end] calculate credit amount for customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fromCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
					if(creditAmount == null) creditAmount = new Double(0);						
					if(creditAmount.doubleValue() !=  0) {
						log.info("[start] withdraw credit with amount = " + creditAmount);
						FundRecord deductCreditMt4 = MT4Manager.getInstance().withdrawBalance(fromCustomerServiceInfo.getCustomerServiceId(), creditAmount, FundRecord.CREDIT, creditDescription, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
						if(deductCreditMt4 != null) {
							if(IResultConstant.Withdraw.SUCCESSFUL == deductCreditMt4.getResult()) {
								log.info("deduct credit amount successfull");
							}
						}
						log.info("[end] withdraw credit with amount = " + creditAmount);
					}
				}
				
				BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(fromCustomerServiceInfo.getCustomerServiceId());
				// insert cashflow for FX
				Double cashFlowAmount = 0 - transferMoneyInfo.getConvertedAmount();						
				log.info("FX start insert cash flow with money = "  + cashFlowAmount);
				iDepositManager.insertCashFlow(transferMoneyInfo.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), fromCustomerServiceInfo.getCustomerServiceId(), sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, fromServiceType, balanceInfo.getBalance(), transferMoneyInfo.getRate().doubleValue(), transferMoneyInfo.getFromCurrencyCode());
				log.info("FX  end insert cash flow with money = "  + cashFlowAmount);
				
				//[NTS1.0-quyen.le.manh]Oct 27, 2015D - Start - MT4 bridge will updated balance of MT4 account Ref https://nextop-asia.atlassian.net/browse/TRSBO-3402
				
//				log.info("[start] update ams cash balance info FX's Cash Balance");
//				// update balance for FX							
//				iDepositManager.updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), fromServiceType, transferMoneyInfo.getConvertedAmount(), creditAmount.doubleValue(), Boolean.TRUE);
//				log.info("[end] update ams cash balance info FX's Cash Balance");											
				
				//[NTS1.0-quyen.le.manh]Oct 27, 2015D - End
				
				if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType)){
					if(creditAmount > 0){
						creditAmount = 0-creditAmount;
					}
					log.info("[start] update amsMoneyTransfer when withdraw money from FX with credit amount: "+creditAmount);
					BigDecimal credit = roundAmount(creditAmount, fromCustomerServiceInfo.getCurrencyCode());
					
					amsTransferMoney.setCreditAmount(credit.doubleValue());
				}
				return IConstants.TRANSFER_STATUS.SUCCESS;
			} else { 
				log.info("cannot update deduct mt4 ");
				transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				transferMoneyInfo.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
				return IConstants.TRANSFER_STATUS.FAIL;
			}
		} else {
			log.info("fail in deductMt4");
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.INPROGRESS);
			amsTransferMoney.setTranferCompleteDateTime(null);
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("[end] transfer money from FX to AMS");
			return IConstants.TRANSFER_STATUS.INPROGRESS;			
		}
	}
	
	private Integer depositToMT4Service(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription){
		Integer result = IConstants.TRANSFER_STATUS.FAIL;
		CustomerServicesInfo toCustomerServiceInfo = transferMoneyInfo.getToServicesInfo();
		Integer toServiceType = toCustomerServiceInfo.getServiceType();
		
		BigDecimal bonusAmount = new BigDecimal("0");
		
		log.info("[start] plus mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + toCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		Integer plusMT4 = MT4Manager.getInstance().depositBalance(toCustomerServiceInfo.getCustomerServiceId(), transferMoneyInfo.getTransferMoney().doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
		log.info("[end] plus mt4, status: " + plusMT4  +  " customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + toCustomerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		
		if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(plusMT4)) {
			result = IConstants.TRANSFER_STATUS.SUCCESS;
			
			BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(toCustomerServiceInfo.getCustomerServiceId());
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, toCustomerServiceInfo.getCustomerId());
			String language = "";
			if(amsCustomer != null) {
				language = amsCustomer.getDisplayLanguage();
				if(language == null) language = IConstants.Language.ENGLISH;
			}
			
			log.info("[start] insert cashflow to " + toServiceType + " (to service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			getiDepositManager().insertCashFlow(transferMoneyInfo.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), toCustomerServiceInfo.getCustomerServiceId(), sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, transferMoneyInfo.getTransferMoney(), IConstants.SOURCE_TYPE.TRANFER_MONEY, toServiceType, balanceInfo.getBalance(), transferMoneyInfo.getRate().doubleValue(), transferMoneyInfo.getToCurrencyCode());
			log.info("[end] insert cashflow to " + toServiceType + " (to service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			
			log.info("[start] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId());
			BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
			AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
			amsCashBalanceFxId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
			amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
			amsCashBalanceFxId.setServiceType(toServiceType);
			AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
			if(amsCashBalanceFx != null) {
				netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
			}
			log.info("[end] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId() + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
			
			//[NTS1.0-quyen.le.manh]Oct 27, 2015D - Start - MT4 bridge will updated balance of MT4 account Ref https://nextop-asia.atlassian.net/browse/TRSBO-3402
//			log.info("[start] update cash balance into FX's Cash Balance");
//			iDepositManager.updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getToCurrencyCode(), toServiceType, transferMoneyInfo.getTransferMoney(), bonusAmount.doubleValue(), Boolean.FALSE); // deductFlag = TRUE
//			log.info("[end] update cash balance into FX's Cash Balance");
			//[NTS1.0-quyen.le.manh]Oct 27, 2015D - End 
			
			if(IConstants.SERVICES_TYPE.FX.equals(toServiceType)){
				log.info("[start] process promotion for fx with to service = " + toServiceType);
				try {
					// start promotion deposit bonus			
					
					BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()));
					Integer scale = new Integer(0);
					Integer rounding = new Integer(0);
					CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + transferMoneyInfo.getToCurrencyCode());
					if(currencyInfo != null) {
						scale = currencyInfo.getCurrencyDecimal();
						rounding = currencyInfo.getCurrencyRound();
					}	
					
					AmsPromotion amsPromotion = null;
					totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
					log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
					if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
						//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
						amsPromotion = iAmsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, toServiceType, toCustomerServiceInfo.getSubGroupId());
						
						if(amsPromotion != null){
							//bonusAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
							BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), netDepositAmount, amsPromotion.getKind());
							if(amsPromotion.getKind() == null) {
								bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, toServiceType, toCustomerServiceInfo.getSubGroupId());
							} else {
								if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())){
									bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, toServiceType, toCustomerServiceInfo.getSubGroupId());
									//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
								} else if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(amsPromotion.getKind())){
									bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, toServiceType, toCustomerServiceInfo.getSubGroupId(), netDepositAmount, toCustomerServiceInfo.getCustomerId(), true);
								}
							}
							if(bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
								log.info("Promotion: bonus = " + bonusAmount.doubleValue() + "for customerID = " + transferMoneyInfo.getCustomerId());
								Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(toCustomerServiceInfo.getCustomerServiceId(), bonusAmount.doubleValue(), FundRecord.CREDIT, creditDescription, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
								if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
									log.info("start save data to ams promotion customer. CustomerId  " +transferMoneyInfo.getCustomerId() + "promotion id" + amsPromotion.getPromotionId() + "bonus value " + bonusAmount.doubleValue() + "transferMoneyId" + amsTransferMoney.getTransferMoneyId()  + "currencyCode" + transferMoneyInfo.getToCurrencyCode() );
									getiPromotionManager().saveAmsPromotionCustomer(transferMoneyInfo.getCustomerId(), amsPromotion.getPromotionId(), bonusAmount.doubleValue(),amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getToCurrencyCode());
									log.info("end save data to ams promotion customer");
									sendmailDepositBonus(amsCustomer, transferMoneyInfo, language, bonusAmount);										
								}
								//update credit amount
								log.info("start update credit amount  = " + bonusAmount.doubleValue() + "and promotion id" + amsPromotion.getPromotionId() + "to AmsTransferMoney");
								
								BigDecimal bonus = roundAmount(bonusAmount.doubleValue(), toCustomerServiceInfo.getCurrencyCode());
								amsTransferMoney.setCreditAmount(bonus.doubleValue());
								amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
								//getiAmsTransferMoneyDAO().merge(amsTransferMoney);
								
								log.info("end update credit amount  = " + bonusAmount.doubleValue() + "and promotion id" + amsPromotion.getPromotionId() + "to AmsTransferMoney");
							}
						}
					}
					// end promotion deposit bonus
					checkingPromotionForLosscut(transferMoneyInfo, toCustomerServiceInfo, amsTransferMoney, amsCustomer, language);
					log.info("[end] process promotion for fx with to service = " + toServiceType);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT.equals(plusMT4)){
			 result = IConstants.TRANSFER_STATUS.INPROGRESS;
		}
		
		return result;
	}
	
	private Integer depositWithdrawSocialSystem(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription, boolean isDeposit){
		CustomerServicesInfo customerServiceInfo = isDeposit ? transferMoneyInfo.getToServicesInfo() : transferMoneyInfo.getFromServicesInfo();
		Integer serviceType = customerServiceInfo.getServiceType();
		String customerId = customerServiceInfo.getCustomerId();
		
		BigDecimal transferAmount = isDeposit ? BigDecimal.valueOf(transferMoneyInfo.getTransferMoney())
				: BigDecimal.ZERO.subtract(BigDecimal.valueOf(transferMoneyInfo.getTransferMoney()));
		//TODO hot fix hardcode , stupid code why transfer set type = cashback
		//int transferType = isDeposit ? ITrsConstants.TRANSACTION_TYPE.CASH_BACK_IN : ITrsConstants.TRANSACTION_TYPE.CASH_BACK_OUT;
		int transferType = IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER;
		Long sourceId = null;
		
		// 1. Register transfer to SocialApi
		log.info("[start] register transfer to SocialApi, transferMoneyId: " + transferMoneyInfo.getTransferMoneyId()
				+ ", amount: " + transferAmount + ", subGroupCode: " + customerServiceInfo.getSubGroupCode());
		boolean isEaAccount = iAccountManager.checkEaAccount(customerId);
		TransferResponse response = SCManager.getInstance().transfer(transferMoneyInfo.getTransferMoneyId(),
				transferType, transferAmount, Integer.valueOf(customerServiceInfo.getCustomerServiceId()), customerId, null, null, isEaAccount, sourceId);
		log.info("[end] register transfer to SocialApi, transferMoneyId: " + transferMoneyInfo.getTransferMoneyId()
				+ ", amount: " + transferAmount + ", transferResponse: " + response);
		
		//2.1 Not received response (Timed-out)
		if(response == null)
			return IConstants.TRANSFER_STATUS.INPROGRESS;
		
		//2.2 Success
		if(response.getResult() == TRANSFER_STATUS.SUCCESS) {
			
			Long cashflowId = null;
			//3. Update amsTransferMoney remark
			if(response.hasCashflowId()) {
				cashflowId = response.getCashflowId();
				String remark = (StringUtil.isEmpty(amsTransferMoney.getRemark()) 
											? "" : amsTransferMoney.getRemark() + ", ")
											+ "cashflowId: " + cashflowId;
				amsTransferMoney.setRemark(remark);
				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			}
			
			//4. Update balance MT4 social
			log.info("[start] update MT4 balance, customerServiceId: " + customerServiceInfo.getCustomerServiceId() + ", amount: " + transferAmount);
			FundRecord fundResult = MT4Manager.getInstance().updateBalance(customerServiceInfo.getCustomerServiceId(), 
					transferAmount.doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
			Integer plusMT4 = fundResult.getResult();
			sourceId = fundResult.getOrderTicket() != null ? (long)fundResult.getOrderTicket() : null;
			log.info("[end] update MT4 balance, status: " + MT4UpdateBalanceStatus.valueOf(plusMT4)  
					+ ", customerServiceId: " + customerServiceInfo.getCustomerServiceId() + ", amount: " + transferAmount);
			
			//4.1 Not received response (Timed-out)
			if(plusMT4 == IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_UPDATE_MT4_TIMEOUT)
				return IConstants.TRANSFER_STATUS.INPROGRESS;
			
			//4.2 Received response, update transfer status to SocialApi
			TransferStatus transferStatus = IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(plusMT4) ? TransferStatus.SUCCESS : TransferStatus.FAIL;
			log.info("[start] update transfer status to SocialApi, transferMoneyId: " + transferMoneyInfo.getTransferMoneyId() 
					+ ", transferStatus: " + transferStatus);
			response = SCManager.getInstance().transfer(transferMoneyInfo.getTransferMoneyId(), transferType, transferAmount,
					Integer.valueOf(customerServiceInfo.getCustomerServiceId()), customerId, transferStatus, cashflowId, isEaAccount, sourceId);
			log.info("[end] update transfer status to SocialApi, transferMoneyId: " + transferMoneyInfo.getTransferMoneyId() 
					+ ", transferStatus: " + transferStatus);
			
			//5.1 Not received response (Timed-out)
			if(response == null)
				return IConstants.TRANSFER_STATUS.INPROGRESS;
			
			//5.2 Received response: insertCashFlow
			if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(plusMT4)) {
				
				BalanceInfo balanceInfo = MT4Manager.getInstance().getBalanceInfo(customerServiceInfo.getCustomerServiceId());
				AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, customerServiceInfo.getCustomerId());
				String language = "";
				if(amsCustomer != null) {
					language = amsCustomer.getDisplayLanguage();
					if(language == null) language = IConstants.Language.ENGLISH;
				}
				
				//6. Insert cashflow
				log.info("[start] insert cashflow, serviceType: " + serviceType + ", transferMoneyId: " + transferMoneyInfo.getTransferMoneyId());
				getiDepositManager().insertCashFlow(transferMoneyInfo.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), 
						customerServiceInfo.getCustomerServiceId(), sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, 
						transferAmount.doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, serviceType, 
						balanceInfo.getBalance(), transferMoneyInfo.getRate().doubleValue(), transferMoneyInfo.getToCurrencyCode());
				log.info("[end] insert cashflow, serviceType: " + serviceType + ", transferMoneyId: " + transferMoneyInfo.getTransferMoneyId());
				
//				log.info("[start] checking NET_DEPOSIT, customerId: " + transferMoneyInfo.getCustomerId());
//				BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
//				AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
//				amsCashBalanceFxId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
//				amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
//				amsCashBalanceFxId.setServiceType(serviceType);
//				AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
//				if(amsCashBalanceFx != null) {
//					netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
//				}
//				log.info("[end] checking NET_DEPOSIT, customerId: " + transferMoneyInfo.getCustomerId() + ", NET_DEPOSIT_AMOUNT: " + netDepositAmount);
				
				return IConstants.TRANSFER_STATUS.SUCCESS;
			} else
				return IConstants.TRANSFER_STATUS.FAIL;
		} else {
			//fail
			return IConstants.TRANSFER_STATUS.FAIL;
		}
	}
	
	private Integer depositToPlainService(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate, Double creditAmount){
		AmsCashBalance amsCashBalance = new AmsCashBalance();
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		CustomerServicesInfo toCustomerServiceInfo = transferMoneyInfo.getToServicesInfo();
		Integer toServiceType = toCustomerServiceInfo.getServiceType();
		
		boolean isBoservice = IConstants.SERVICES_TYPE.BO.equals(toServiceType);
		String toCustomerId = null;
		String toCurrencyCode = null;
		String toCustomerServiceId = null;
		if(IConstants.SERVICES_TYPE.AMS.equals(toServiceType)){
//			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
//			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			
//			toCurrencyCode = frontUserOnline.getCurrencyCode();
//			toCustomerId = frontUserOnline.getUserId();
//			toCustomerId = transferMoneyInfo.getCustomerId();
//			toCustomerServiceId = null;
			
			toCustomerId = transferMoneyInfo.getCustomerId();
			AmsCustomer amsCustomer = getiAmsCustomerDAO().findById(AmsCustomer.class, toCustomerId);
			if(amsCustomer != null) {
				SysCurrency sysCurrency = amsCustomer.getSysCurrency();
				if(sysCurrency != null) {
					toCurrencyCode = sysCurrency.getCurrencyCode();
				}
			}
			toCustomerServiceId = null;
			
		}else{
			toCurrencyCode = toCustomerServiceInfo.getCurrencyCode();
			toCustomerId = toCustomerServiceInfo.getCustomerId();
			toCustomerServiceId = toCustomerServiceInfo.getCustomerServiceId();
		}
		if(!isBoservice){
			amsCashBalanceId.setCurrencyCode(toCurrencyCode);
			amsCashBalanceId.setCustomerId(toCustomerId);
			amsCashBalanceId.setServiceType(toServiceType);
			amsCashBalance = iAmsCashBalanceDAO.findById(AmsCashBalance.class, amsCashBalanceId);
			if(amsCashBalance == null) {
				log.warn("Cannot find cash balance for CustomerId = " + toCustomerId + ", currencyCode = " + toCurrencyCode + ", serviceType = " + toServiceType);
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				iAmsTransferMoneyDAO.merge(amsTransferMoney);
				return IConstants.COMMON_RESULT.FAIL;
			}
			Double balance = amsCashBalance.getCashBalance();
			balance = balance + transferMoneyInfo.getTransferMoney();
			// insert cashflow for to service (AMS or BO)
			log.info("[start] insert cashflow to " + toServiceType + " (to service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			getiDepositManager().insertCashFlow(transferMoneyInfo.getTransferMoneyId(), toCustomerId, toCustomerServiceId, sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, transferMoneyInfo.getTransferMoney(), IConstants.SOURCE_TYPE.TRANFER_MONEY, toServiceType, balance, transferMoneyInfo.getRate().doubleValue(), toCurrencyCode);
			log.info("[end] insert cashflow to " + toServiceType + " (to service) with transferMoneyId = " + transferMoneyInfo.getTransferMoneyId());
			
			// update cashbalance for to service (AMS or BO)
			log.info("[start] update ams cash balance info AMS/BO's Cash Balance");
			getiDepositManager().updateAmsCashBalance(toCustomerId, toCurrencyCode, toServiceType, transferMoneyInfo.getTransferMoney(), creditAmount.doubleValue(), Boolean.FALSE);
			log.info("[end] update ams cash balance info AMS/BO's Cash Balance");
		}else {
			log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
			UpdateBoBalanceInfo updateBoBalanceInfo = boManager.deposit(toCustomerServiceId, MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), MathUtil.parseBigDecimal(String.valueOf(amsTransferMoney.getRate())),transferMoneyInfo.getTransferMoneyId(),IConstants.SOURCE_TYPE.TRANFER_MONEY,IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER);
        	Integer boResult = updateBoBalanceInfo.getResult();
        	Integer transferResult = getTransferResultFromCommonResult(boResult);
        	if(!IConstants.TRANSFER_STATUS.SUCCESS.equals(transferResult)){
        		return transferResult;
        	}
			log.info("======================================>[end] send topic for Refresh Balance Info of BO<======================================");
		}
		
		 return IConstants.TRANSFER_STATUS.SUCCESS;
	}
	
	private Integer withdrawDepositNTDService(TransferMoneyInfo transferMoneyInfo, AmsTransferMoney amsTransferMoney, SysAppDate sysAppDate, Double creditAmount, String description, String creditDescription, CashflowType cashflowType){
		log.info("check promotion for withdrawal from fx");
		CustomerServicesInfo serviceInfo = null;
		double amount = 0;
		double cashFlowAmount = 0;
		boolean deductFlag = false;
		String currencyCode = null;
		
		if(cashflowType == CashflowType.DEPOSIT) {
			amount = transferMoneyInfo.getTransferMoney();
			cashFlowAmount = transferMoneyInfo.getConvertedAmount();
			serviceInfo = transferMoneyInfo.getToServicesInfo();
			currencyCode = transferMoneyInfo.getToCurrencyCode();
			deductFlag = false;
		} else {
			amount -= transferMoneyInfo.getTransferMoney();
			cashFlowAmount -= transferMoneyInfo.getConvertedAmount();
			serviceInfo = transferMoneyInfo.getFromServicesInfo();
			currencyCode = transferMoneyInfo.getFromCurrencyCode();
			deductFlag = true;
		}
		
		Integer serviceType = serviceInfo.getServiceType();
		
		int result = Constant.RESULT_UNKNOWN;
		log.info("[start] request update NTD Account Balance, customerId: " + transferMoneyInfo.getCustomerId() + ", NTDAccountId: " + serviceInfo.getNtdAccountId() +  ", amount: " + amount);
		//Request update balance to NTD
		result = NTDManager.getInstance().updateBalance(serviceInfo.getNtdAccountId(), amount + "", TradingCashflowType.TRANSFER);
		log.info("[end] request update NTD Account Balance, NTDAccountId: " + serviceInfo.getNtdAccountId() + ", result: " + result);
		
		//Check result
		if(Constant.RESULT_SUCCESS == result) {
			log.info("[start] calculate credit amount for customerId: " + transferMoneyInfo.getCustomerId() + ", customerServiceId: " + serviceInfo.getCustomerServiceId() + ", amount: " + amount);
			log.info("getting net deposit of customerId = " + transferMoneyInfo.getCustomerId() + ", serviceType = " + serviceInfo.getServiceType());
			log.info("[start] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId());
			
			BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
			BigDecimal baseAmount = MathUtil.parseBigDecimal(0);
			AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
			amsCashBalanceFxId.setCurrencyCode(currencyCode);
			amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
			amsCashBalanceFxId.setServiceType(serviceInfo.getServiceType());
			AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
			if(amsCashBalanceFx != null) {
				netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
				baseAmount = netDepositAmount.subtract(MathUtil.parseBigDecimal(transferMoneyInfo.getConvertedAmount()));
			}
			log.info("net deposit of customerId = " + transferMoneyInfo.getCustomerId() + ", serviceType = " + serviceInfo.getServiceType() + " = " + netDepositAmount);						
			log.info("[end] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId() + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
			BigDecimal deductCreditAmount = getiPromotionManager().getBonusAmount(baseAmount, currencyCode, 
					IConstants.PROMOTION_TYPE.DEPOSIT, serviceType, serviceInfo.getSubGroupId(), netDepositAmount, serviceInfo.getCustomerId(), false); 
			creditAmount = deductCreditAmount == null ? new Double("0") : deductCreditAmount.abs().doubleValue();
			log.info("credit amount = " + creditAmount);
			log.info("[end] calculate credit amount for customerId: " + transferMoneyInfo.getCustomerId() + ", customerServiceId = " + serviceInfo.getCustomerServiceId() + ", amount: " + amount);
			
			//Check balance
			BalanceInfo balanceInfo = NTDManager.getInstance().getBalanceInfo(serviceInfo.getNtdAccountId());
			
			// insert cashflow for FX
			log.info("[start] insert cash flow with money = "  + cashFlowAmount);
			iDepositManager.insertCashFlow(transferMoneyInfo.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), 
					serviceInfo.getCustomerServiceId(), sysAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, 
					IConstants.SOURCE_TYPE.TRANFER_MONEY, serviceType, balanceInfo.getBalance(), transferMoneyInfo.getRate().doubleValue(), currencyCode);
			log.info("[end] insert cash flow with money = "  + cashFlowAmount);
			
			//[TRSM1-3900-TheLN]May 17, 2016A - Start - Remove update ntd balance on AMS side when transfer
			
			/*log.info("[start] update ams cash balance info FX's Cash Balance");
			// update balance for NTD FX
			iDepositManager.updateAmsCashBalance(transferMoneyInfo.getCustomerId(), currencyCode, 
					serviceType, transferMoneyInfo.getConvertedAmount(), creditAmount.doubleValue(), deductFlag);
			log.info("[end] update ams cash balance info FX's Cash Balance");						*/					
			
			//[TRSM1-3900-TheLN]May 17, 2016A - End
			
			return IConstants.TRANSFER_STATUS.SUCCESS;
		} else if(Constant.RESULT_FAIL == result) {
			//If Fail update status
			log.info("Update NTD Account Balance FAIL, NTDAccountId: " + serviceInfo.getNtdAccountId());
			transferMoneyInfo.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			transferMoneyInfo.setTranferCompleteDateTime(null);
			iAmsTransferMoneyDAO.merge(amsTransferMoney);
			return IConstants.TRANSFER_STATUS.FAIL;
		} else {
			//Unknow do Nothings, status still INPROGRESS
			log.info("Timed-out for receiving response from NTD, NTDAccountId: " + serviceInfo.getNtdAccountId());
			return IConstants.TRANSFER_STATUS.INPROGRESS;
		}
	}
	
	private Integer getTransferResultFromCommonResult(Integer boResult) {
		if(IConstants.COMMON_RESULT.SUCCESS.equals(boResult)) return IConstants.TRANSFER_STATUS.SUCCESS;
		else if(IConstants.COMMON_RESULT.FAIL.equals(boResult)) return IConstants.TRANSFER_STATUS.FAIL;
		else return IConstants.TRANSFER_STATUS.INPROGRESS;
	}
	
	private BigDecimal roundAmount(Double bonusAmount, String currencyCode) {
		BigDecimal roundAmount = MathUtil.parseBigDecimal(bonusAmount);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		if(currencyInfo != null) {
			Integer scale = currencyInfo.getCurrencyDecimal();
			Integer rounding = currencyInfo.getCurrencyRound();
			roundAmount = roundAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		}
		
		return roundAmount;
	}
	private String getFundBalanceDescription(Integer fromService, Integer toService) {
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = "";
		if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.AMS.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_FX_AMS);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.BO.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_FX_BO);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_FX_COPYTRADE);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.AMS.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_COPYTRADE_AMS);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_COPYTRADE_FX);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.BO.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_COPYTRADE_BO);
		} else if(IConstants.SERVICES_TYPE.AMS.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_AMS_FX);
		} else if(IConstants.SERVICES_TYPE.AMS.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_AMS_COPYTRADE);
		} else if(IConstants.SERVICES_TYPE.BO.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_BO_FX);
		} else if(IConstants.SERVICES_TYPE.BO.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_BO_COPYTRADE);
		}
		return description;
	}
	
	private String getFundCreditDescription(Integer fromService, Integer toService) {
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = "";
		if(IConstants.SERVICES_TYPE.AMS.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_AMS_FX);
		} else if(IConstants.SERVICES_TYPE.AMS.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_AMS_COPYTRADE);
		} else if(IConstants.SERVICES_TYPE.BO.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_BO_FX);
		} else if(IConstants.SERVICES_TYPE.BO.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_BO_COPYTRADE);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.AMS.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_FX_AMS);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.COPY_TRADE.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_FX_COPYTRADE);
		} else if(IConstants.SERVICES_TYPE.FX.equals(fromService) && IConstants.SERVICES_TYPE.BO.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_FX_BO);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.AMS.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_COPYTRADE_AMS);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.BO.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_COPYTRADE_BO);
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromService) && IConstants.SERVICES_TYPE.FX.equals(toService)) {
			description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_COPYTRADE_FX);
		}
		return description;
	}
	
	
	/**
	 *  transfer money from AMS to fx　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyAmsFX(TransferMoneyInfo transferMoneyInfo, List<CustomerServicesInfo> listCustomerServiceInfo, AmsCashBalance amsCashBalanceAms, BigDecimal convertedAmount, BigDecimal convertRate, SysAppDate  amsAppDate, AmsTransferMoney amsTransferMoney, AmsCustomer amsCustomer, String currencyCode){
		log.info("[Start function] transferMoneyAmsFX  - TransferManagerImpl : "+System.currentTimeMillis());	
		String language = "";
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		if(amsCustomer != null){
			language = amsCustomer.getDisplayLanguage();			
		}
		if(language == null || StringUtils.isBlank(language)) {
			language = IConstants.Language.ENGLISH;
		}
		Double balance = new Double(0);
		Integer plusMT4 = new Integer(0);
		CustomerServicesInfo customerServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				if(IConstants.SERVICES_TYPE.FX.equals(serviceInfo.getServiceType())) {
					customerServiceInfo = serviceInfo;
				}
			}				
		}
		if(customerServiceInfo == null) {
			log.warn("Cannot find customer serviceId");
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_AMS_FX);
		//update balance		
		log.info("[start] plus mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + customerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		plusMT4 = MT4Manager.getInstance().depositBalance(customerServiceInfo.getCustomerServiceId(), transferMoneyInfo.getTransferMoney().doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
		log.info("[end] plus mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + customerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		if(plusMT4.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)){						
			balance = amsCashBalanceAms.getCashBalance() - convertedAmount.doubleValue();
			Double cashFlowAmount = 0 - convertedAmount.doubleValue(); // to insert negative value on ams cash flow of From Account (BO)
			log.info("AMS start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money = " + cashFlowAmount.doubleValue() +  " has balance = " + balance );
			getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
			log.info("AMS end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money = " + cashFlowAmount.doubleValue() +  " has  balance = " + balance );
			log.info("AMS start update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + convertedAmount.doubleValue());
			amsCashBalanceAms = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE); // deductFlag = true
			// insert cashflow for FX
			//balance = balanceInfo.getBalance() + convertedAmount.doubleValue();
			//BigDecimal bonusDeposit = MathUtil.parseBigDecimal(0);
			BalanceInfo balanceInfo  = balanceManager.getBalanceInfo(transferMoneyInfo.getCustomerId(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getToCurrencyCode());
			if(balanceInfo == null) {
				log.warn("Cannot find balance FX of customerID: " + transferMoneyInfo.getCustomerId());
				return IConstants.TRANSFER_STATUS.FAIL;
			}
			log.info("FX start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() +" after plus money=" + transferMoneyInfo.getTransferMoney() +  " has balance " + balanceInfo.getBalance());
			getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(),customerServiceInfo.getCustomerServiceId(), amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER,transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.FX, balanceInfo.getBalance(), convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
			log.info("FX end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() +" after plus money=" + transferMoneyInfo.getTransferMoney() +  " has balance " + balanceInfo.getBalance());
		
			// start promotion deposit bonus			
			log.info("[start] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId());
			BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
			AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
			amsCashBalanceFxId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
			amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
			amsCashBalanceFxId.setServiceType(IConstants.SERVICES_TYPE.FX);
			AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
			if(amsCashBalanceFx != null) {
				netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
			}
			log.info("[end] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId() + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
			BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()));
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + transferMoneyInfo.getToCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}	
			AmsPromotion amsPromotion = null;
			totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
			if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
				//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
				amsPromotion = iAmsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, customerServiceInfo.getSubGroupId());
				
				if(amsPromotion != null){
					//bonusAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
					//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
					BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), netDepositAmount, amsPromotion.getKind());
					bonusAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, customerServiceInfo.getSubGroupId());
					//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
					
					if(bonusAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0){		
						log.info("Promotion: bonus = " + bonusAmount.doubleValue() + "for customerID = " + transferMoneyInfo.getCustomerId());
						description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_AMS_FX);
						Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceInfo.getCustomerServiceId(), bonusAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
						if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
							log.info("start save data to ams promotion customer. CustomerId  " +transferMoneyInfo.getCustomerId() + "promotion id" + amsPromotion.getPromotionId() + "bonus value " + bonusAmount.doubleValue() + "transferMoneyId" + amsTransferMoney.getTransferMoneyId()  + "currencyCode" + transferMoneyInfo.getToCurrencyCode() );
							getiPromotionManager().saveAmsPromotionCustomer(transferMoneyInfo.getCustomerId(), amsPromotion.getPromotionId(), bonusAmount.doubleValue(),amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getToCurrencyCode());
							log.info("end save data to ams promotion customer");
//							DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
							sendmailDepositBonus(amsCustomer, transferMoneyInfo, language, bonusAmount);
						}
						//update credit amount
						log.info("start update credit amount  = " + bonusAmount.doubleValue() + "and promotion id" + amsPromotion.getPromotionId() + "to AmsTransferMoney");
						amsTransferMoney.setCreditAmount(bonusAmount.doubleValue());
						amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
						getiAmsTransferMoneyDAO().merge(amsTransferMoney);
						log.info("end update credit amount  = " + bonusAmount.doubleValue() + "and promotion id" + amsPromotion.getPromotionId() + "to AmsTransferMoney");
					}/*else{
						log.info("start update credit amount  =  0 and promotion id is NULL to AmsTransferMoney");
						amsTransferMoney.setCreditAmount(new Double(0));
						getiAmsTransferMoneyDAO().merge(amsTransferMoney);
						log.info("end update credit amount  =  0 and promotion id is NULL to AmsTransferMoney");
					}*/
				}
			}
			
			
			log.info("[start] update cash balance into FX's Cash Balance");
			getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.FX, convertedAmount.doubleValue(), bonusAmount.doubleValue(), Boolean.FALSE); // deductFlag = TRUE
			log.info("[end] update cash balance into FX's Cash Balance");

			// end promotion deposit bonuss
			checkingPromotionForLosscut(transferMoneyInfo, customerServiceInfo, amsTransferMoney, amsCustomer, language);
			log.info("start update status of transfer money success" + IConstants.TRANSFER_STATUS.SUCCESS);
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
			amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
			amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("end update status of transfer money");
			log.info("[Finish function] transferMoneyAmsFX  - TransferManagerImpl : "+System.currentTimeMillis());	
			return IConstants.TRANSFER_STATUS.SUCCESS;	
		}else{
			log.info("update balance mt4 error, set status of transfer money is fail " + IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setTranferCompleteDateTime(null);
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);	
			return IConstants.TRANSFER_STATUS.FAIL;
		}
	
	}

	/**
	 * sendmailDepositBonus　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 30, 2012
	 */
	private void sendmailDepositBonus(AmsCustomer amsCustomer, TransferMoneyInfo transferMoneyInfo, String language, BigDecimal bonusAmount) {
		log.info("[end]send mail deoposit bonus");
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_BONUS + "_" + language); //  waiting for BA
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_DEPOSIT_BONUS).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setBonusAmount(balanceManager.formatNumber(bonusAmount, transferMoneyInfo.getToCurrencyCode()));
		amsMailTemplateInfo.setBonusCurrency(balanceManager.getCurrencyCode(transferMoneyInfo.getToCurrencyCode(), language));
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);
															
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(transferMoneyInfo.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end]send mail deoposit bonus");
	}
	/**
	 *  transfer　money from AMS to BO
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyAmsBO(TransferMoneyInfo transferMoneyInfo, List<CustomerServicesInfo> listCustomerServiceInfo, AmsCashBalance amsCashBalanceAms,BigDecimal convertedAmount, SysAppDate amsAppDate, AmsTransferMoney amsTransferMoney, BigDecimal convertRate, String currencyCode, CurrencyInfo toCurrencyInfo){		
		log.info("Start transfer money from AMS to BO");		
		Double balance = new Double(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		AmsCashBalanceId boCashBalanceId = new AmsCashBalanceId();
		boCashBalanceId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
		boCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		boCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.BO);
		
		CustomerServicesInfo customerServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				if(IConstants.SERVICES_TYPE.BO.equals(serviceInfo.getServiceType())) {
					customerServiceInfo = serviceInfo;
				}
			}				
		}
		
		AmsCashBalance amsCashBalanceBo = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, boCashBalanceId);
		if(amsCashBalanceBo == null) {
			log.warn("Cannot find balance AMS of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}					
		balance = amsCashBalanceAms.getCashBalance() - convertedAmount.doubleValue();
		Double cashFlowAmount = 0 - convertedAmount.doubleValue(); // to insert negative value on ams cash flow of From Account (AMS)		
		log.info("AMS start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money = " + cashFlowAmount.doubleValue() +  " has  balance = " + balance );
		getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER,cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
		log.info("AMS end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money = " + cashFlowAmount.doubleValue() +  " has  balance = " + balance );
		log.info("AMS update cash balance ams for customer id "  + transferMoneyInfo.getCustomerId() + "transfer money info " +convertedAmount.doubleValue());
		amsCashBalanceAms = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.AMS, convertedAmount.doubleValue(), bonusAmsAmount.doubleValue(), Boolean.TRUE);
		
		balance = amsCashBalanceBo.getCashBalance() + transferMoneyInfo.getTransferMoney();
		log.info("BO update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money= " + transferMoneyInfo.getTransferMoney() + "has balance ="  + balance);
		getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), customerServiceInfo.getCustomerServiceId(), amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.BO, balance, convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
		log.info("BO end cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money= " + transferMoneyInfo.getTransferMoney() + "has balance ="  + balance);
		amsCashBalanceBo = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getToCurrencyCode(), IConstants.SERVICES_TYPE.BO,  transferMoneyInfo.getTransferMoney().doubleValue(), bonusAmount.doubleValue(), Boolean.FALSE);
	
		log.info("start update status of transfer money from AMS to BO is success and set completed date time");
		amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
		amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
		amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
		getiAmsTransferMoneyDAO().merge(amsTransferMoney);
		log.info("end update status of transfer money from AMS to BO is success and set completed date time");
		
		log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
		try {
			String customerServiceId = transferMoneyInfo.getCustomerId() + IConstants.SERVICES_TYPE.BO;
			BigDecimal sendingMoney = MathUtil.rounding(transferMoneyInfo.getTransferMoney(), toCurrencyInfo.getCurrencyDecimal(),  toCurrencyInfo.getCurrencyRound());
			log.info("sending BO customerServiceId = " + customerServiceId  + " with money = " + sendingMoney.doubleValue());
			BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, sendingMoney , 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
//			JMSSendClient.getInstance().sendBalanceUpdateTopic(balanceUpdateInfo);
			jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
			log.info("sending BO customerServiceId = " + customerServiceId  + " with money = " + sendingMoney.doubleValue() + "COMPLETED");
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}		
		log.info("======================================>[end] send topic for Refresh Balance Info of BO<======================================");
		
		return IConstants.TRANSFER_STATUS.SUCCESS;	
	}
	/**
	 *  transfer money from FX to AMS　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyFxAms(TransferMoneyInfo transferMoneyInfo, List<CustomerServicesInfo> listCustomerServiceInfo, BigDecimal convertedAmount, BigDecimal convertRate, SysAppDate amsAppDate, AmsTransferMoney amsTransferMoney , String currencyCode){
		log.info("[Start function] transferMoneyFxAms  - TransferManagerImpl : "+System.currentTimeMillis());	
		FundRecord deductMt4 = new FundRecord();
		Double balance = new Double(0);
		Double creditAmount = new Double(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
//		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		log.info("[start] transfer money from FX to AMS");
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
		amsCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.AMS);
		AmsCashBalance amsCashBalanceAms = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalanceAms == null) {
			log.warn("Cannot find balance AMS of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_FX_AMS);
		// get customer service info of FX
		CustomerServicesInfo customerServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				if(IConstants.SERVICES_TYPE.FX.equals(serviceInfo.getServiceType())) {
					customerServiceInfo = serviceInfo;
				}
			}				
		}
		if(customerServiceInfo == null) {
			log.warn("Cannot find customer serviceId");
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		log.info("[start] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + customerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());		
		deductMt4 = MT4Manager.getInstance().withdrawBalance(customerServiceInfo.getCustomerServiceId(), convertedAmount.doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
		log.info("[end] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + customerServiceInfo.getCustomerServiceId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		BalanceInfo balanceInfo  = balanceManager.getBalanceInfo(transferMoneyInfo.getCustomerId(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getFromCurrencyCode());
		if(balanceInfo == null) {
			log.warn("Cannot find balance FX of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		if(deductMt4 != null) {
			if(IResultConstant.Withdraw.SUCCESSFUL == deductMt4.getResult()) {		
				FundResultRecord fundResultRecord = deductMt4.getFundResultRecord();
				if(fundResultRecord != null) {
					creditAmount = fundResultRecord.getCreditDeduction();
				}
				//balance = balanceInfo.getBalance() - convertedAmount.doubleValue();
				// insert cashflow for FX
				Double cashFlowAmount = 0 - convertedAmount.doubleValue();						
				log.info("FX start insert cash flow with money = "  + cashFlowAmount);
				getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), customerServiceInfo.getCustomerServiceId(), amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER,cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.FX, balanceInfo.getBalance(), convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
				log.info("FX  end insert cash flow with money = "  + cashFlowAmount);
				log.info("[start] update ams cash balance info FX's Cash Balance");
				// update balance for FX							
				getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getTransferMoney(), creditAmount.doubleValue(), Boolean.TRUE);
				log.info("[end] update ams cash balance info FX's Cash Balance");											
				// insert cashflow for AMS
				balance = amsCashBalanceAms.getCashBalance() + transferMoneyInfo.getTransferMoney();
				log.info("AMS start insert cash flow with money" + transferMoneyInfo.getTransferMoney());
				getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
				log.info("AMS end insert cash flow with money" + transferMoneyInfo.getTransferMoney());
				// update balance for AMS
				log.info("AMS start update cash balance with money " + transferMoneyInfo.getTransferMoney() + " customerId = " + transferMoneyInfo.getCustomerId());
				amsCashBalanceAms = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getToCurrencyCode(), IConstants.SERVICES_TYPE.AMS, transferMoneyInfo.getTransferMoney().doubleValue(), bonusAmsAmount.doubleValue(), Boolean.FALSE);
				log.info("AMS end update cash balance with money " + transferMoneyInfo.getTransferMoney()  + " customerId = " + transferMoneyInfo.getCustomerId());

				if(creditAmount > 0){
					log.info("credit out" + creditAmount);								
					//update amstransfer money
					log.info("set credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY");
					amsTransferMoney.setCreditAmount(creditAmount.doubleValue());
//					getiAmsTransferMoneyDAO().merge(amsTransferMoney);
					log.info("end set credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY");
				} /*else{
					log.info("start set credit amount = 0 to AMS TRANSFER MONEY");
					amsTransferMoney.setCreditAmount(new Double(0));
//					getiAmsTransferMoneyDAO().merge(amsTransferMoney);
					log.info("end set credit amount = 0 to AMS TRANSFER MONEY");
				}*/
				
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
				amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
				amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
				log.info("start update credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY");
				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
				log.info("end update credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY");
				log.info("[Finish function] transferMoneyFxAms  - TransferManagerImpl : "+System.currentTimeMillis());	
				return IConstants.TRANSFER_STATUS.SUCCESS;	
			}else { 
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
				return IConstants.TRANSFER_STATUS.FAIL;
			}
		}else{
			log.info("fail in deductMt4");
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setTranferCompleteDateTime(null);
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("[end] transfer money from FX to AMS");
			return IConstants.TRANSFER_STATUS.FAIL;			
		}
		
	}
	/**
	 *  transfer money from FX to BO　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyFxBo(TransferMoneyInfo transferMoneyInfo, List<CustomerServicesInfo> listCustomerServiceInfo, BigDecimal convertedAmount, BigDecimal convertRate, SysAppDate amsAppDate, AmsTransferMoney amsTransferMoney, String currencyCode, CurrencyInfo toCurrencyInfo){
		FundRecord deductMt4 = new FundRecord();
		Double balance = new Double(0);
		Double creditAmount  = new Double(0);		
		BigDecimal bonusBoAmount = MathUtil.parseBigDecimal(0);
		//String fxCustomerServiceId = "";
		//String boCustomerServiceId = "";
		log.info("[start] transfer money from FX to BO");					
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
		amsCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.BO);
		AmsCashBalance amsCashBalanceBO = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalanceBO == null) {
			log.warn("Cannot find balance BO of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_FX_BO);
		
		String boCustomerServiceId = "";
		String fxCustomerServiceId = "";
		// get customer service info of FX
		CustomerServicesInfo customerServiceInfo = null;		
//		CustomerServicesInfo customerFxServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				customerServiceInfo = serviceInfo;
				if(IConstants.SERVICES_TYPE.FX.equals(serviceInfo.getServiceType())) {
					fxCustomerServiceId = customerServiceInfo.getCustomerServiceId();
//					customerFxServiceInfo = customerServiceInfo;
				}else if(IConstants.SERVICES_TYPE.BO.equals(serviceInfo.getServiceType())) {
					boCustomerServiceId = customerServiceInfo.getCustomerServiceId();
				}
				
			}				
		}
		if(customerServiceInfo == null) {
			log.warn("Cannot find customer serviceId");
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		log.info("[start] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + " customerServiceId= " + fxCustomerServiceId + ", money = " + convertedAmount.doubleValue());
		deductMt4 = MT4Manager.getInstance().withdrawBalance(fxCustomerServiceId, convertedAmount.doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
		log.info("[end] deduct mt4, customer id= " + transferMoneyInfo.getCustomerId() + " customerServiceId= " + fxCustomerServiceId + ", money = " + convertedAmount.doubleValue());
		BalanceInfo balanceInfo  = balanceManager.getBalanceInfo(transferMoneyInfo.getCustomerId(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getFromCurrencyCode());
		if(balanceInfo == null) {
			log.warn("Cannot find balance FX of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
//		creditAmount = getCreditOut(balanceInfo, transferMoneyInfo, convertedAmount.doubleValue());
		if (deductMt4 != null) {
			if(IResultConstant.Withdraw.SUCCESSFUL == deductMt4.getResult()) {		
				FundResultRecord fundResultRecord = deductMt4.getFundResultRecord();
				if(fundResultRecord != null) {
					creditAmount = fundResultRecord.getCreditDeduction();
				}
				
				//balance = balanceInfo.getBalance() - convertedAmount.doubleValue();
				// insert cashflow for FX
				Double cashFlowAmount = 0 - convertedAmount.doubleValue();
				log.info("FX start insert cash flow with money = "  + cashFlowAmount);
				getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), fxCustomerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.FX, balanceInfo.getBalance(), convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
				log.info("FX end insert cash flow with money = "  + cashFlowAmount);
				log.info("[start] update cash balance into FX's Cash Balance");
				// update balance for FX							
				getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getTransferMoney(), creditAmount.doubleValue(), Boolean.TRUE);
				log.info("[end] update cash balance into FX's Cash Balance");										
				// insert cashflow for BO
				balance = amsCashBalanceBO.getCashBalance() + transferMoneyInfo.getTransferMoney().doubleValue();
				log.info("BO start insert cash flow with money = "  + transferMoneyInfo.getTransferMoney().doubleValue());
				getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), boCustomerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.BO, balance, convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
				log.info("BO end insert cash flow with money = "  + transferMoneyInfo.getTransferMoney().doubleValue());
				// update balance for BO
				log.info("BO start update cash balance with money " + transferMoneyInfo.getTransferMoney() + " customerId = " + transferMoneyInfo.getCustomerId());
				amsCashBalanceBO = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getToCurrencyCode(), IConstants.SERVICES_TYPE.BO,transferMoneyInfo.getTransferMoney().doubleValue(), bonusBoAmount.doubleValue(), Boolean.FALSE);
				log.info("BO end update cash balance with money " + transferMoneyInfo.getTransferMoney() + " customerId = " + transferMoneyInfo.getCustomerId());
				if(creditAmount > 0){
					log.info("credit out" + creditAmount);							
					log.info("set credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY FX to BO");
					amsTransferMoney.setCreditAmount(creditAmount.doubleValue());
	//				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
					log.info("end set credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY FX to BO");
				} /*else{
					log.info("start set credit amount = 0 to AMS TRANSFER MONEY FX to BO");
					amsTransferMoney.setCreditAmount(new Double(0));
	//				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
					log.info("end set credit amount = 0 to AMS TRANSFER MONEY FX to BO");
				}
				*/
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
				amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
				amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
				log.info("start update credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY FX to BO");
				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
				log.info("end update credit amount = " + creditAmount.doubleValue() + "to AMS TRANSFER MONEY FX to BO");
				
				log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
				try {
					String customerServiceId = transferMoneyInfo.getCustomerId() + IConstants.SERVICES_TYPE.BO;
					BigDecimal sendingMoney = MathUtil.rounding(transferMoneyInfo.getTransferMoney(), toCurrencyInfo.getCurrencyDecimal(),  toCurrencyInfo.getCurrencyRound());
					log.info("sending BO customerServiceId = " + customerServiceId  + " with money = " + sendingMoney.doubleValue());
					BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, sendingMoney , 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
//					JMSSendClient.getInstance().sendBalanceUpdateTopic(balanceUpdateInfo);
					jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
					log.info("sending BO customerServiceId = " + customerServiceId  + " with money = " + sendingMoney.doubleValue() + "COMPLETED");
				} catch(Exception ex) {
					log.error(ex.getMessage(), ex);
				}		
				log.info("======================================>[end] send topic for Refresh Balance Info of BO<======================================");
				
				return IConstants.TRANSFER_STATUS.SUCCESS;	
			}else { 
				amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
				amsTransferMoney.setTranferCompleteDateTime(null);
				getiAmsTransferMoneyDAO().merge(amsTransferMoney);
				log.info("[end] transfer money from FX to BO");
				return IConstants.TRANSFER_STATUS.FAIL;
			}
		} else {
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setTranferCompleteDateTime(null);
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("fail in deductMT4");
			return IConstants.TRANSFER_STATUS.FAIL;
		}
	}
	/**
	 *  transfer money from BO to AMS　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyBoAms(TransferMoneyInfo transferMoneyInfo,List<CustomerServicesInfo> listCustomerServiceInfo,BigDecimal convertedAmount, BigDecimal convertRate, SysAppDate amsAppDate, AmsTransferMoney amsTransferMoney , String currencyCode, CurrencyInfo fromCurrencyInfo){
		Double balance = new Double(0);
		BigDecimal bonusBoAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusAmsAmount = MathUtil.parseBigDecimal(0);
		log.info("[start] transfer money from BO to AMS");
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
		amsCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.AMS);
		CustomerServicesInfo customerServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				if(IConstants.SERVICES_TYPE.BO.equals(serviceInfo.getServiceType())) {
					customerServiceInfo = serviceInfo;
				}
			}				
		}
		
		AmsCashBalance amsCashBalanceAms = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalanceAms == null) {
			log.warn("Cannot find balance AMS of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		AmsCashBalanceId boCashBalanceId = new AmsCashBalanceId();
		boCashBalanceId.setCurrencyCode(transferMoneyInfo.getFromCurrencyCode());
		boCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		boCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.BO);
		AmsCashBalance amsCashBalanceBO = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, boCashBalanceId);
		if(amsCashBalanceBO == null) {
			log.warn("Cannot find balance AMS of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}					
		balance = amsCashBalanceBO.getCashBalance() - convertedAmount.doubleValue();
		Double cashFlowAmount = 0 - convertedAmount.doubleValue();
		log.info("BO start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money= " + cashFlowAmount.doubleValue() + "balance " + balance);
		getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), customerServiceInfo.getCustomerServiceId(), amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.BO, balance, convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
		log.info("BO end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus money= " + cashFlowAmount.doubleValue() + "balance " + balance);
		log.info("BO start update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + convertedAmount.doubleValue());
		amsCashBalanceBO = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.BO, convertedAmount.doubleValue(), bonusBoAmount.doubleValue(), Boolean.TRUE);
		log.info("BO end update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + convertedAmount.doubleValue());
		//plus ams account with transfer money
		balance = amsCashBalanceAms.getCashBalance() + transferMoneyInfo.getTransferMoney().doubleValue();
		log.info("AMS start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money="  + transferMoneyInfo.getTransferMoney().doubleValue() + "balance " + balance);
		getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), null, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER,transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.AMS, balance, convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
		log.info("AMS end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money="  + transferMoneyInfo.getTransferMoney().doubleValue() + "balance " + balance);
		log.info("AMS start update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		amsCashBalanceAms = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getToCurrencyCode(), IConstants.SERVICES_TYPE.AMS, transferMoneyInfo.getTransferMoney().doubleValue(), bonusAmsAmount.doubleValue(), Boolean.FALSE);
		log.info("AMS start update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		log.info("[end] transfer money from BO to AMS");					
		log.info("update status of transfer money is success and set completed date time");
		amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
		amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
		amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
		getiAmsTransferMoneyDAO().merge(amsTransferMoney);
		
		log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
		try {			
			String customerServiceId = transferMoneyInfo.getCustomerId() + IConstants.SERVICES_TYPE.BO;
			Double sendBalance = 0 - convertedAmount.doubleValue();		
			BigDecimal sendingMoney = MathUtil.rounding(sendBalance, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
			log.info("sending BO  customerServiceId=" + customerServiceId + " with amount = " + sendingMoney.doubleValue());
			BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, sendingMoney, 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
//			JMSSendClient.getInstance().sendBalanceUpdateTopic(balanceUpdateInfo);
			jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
			log.info("sending BO  customerServiceId=" + customerServiceId + " with amount = " + sendingMoney + "COMPLETED");
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}		
		log.info("======================================>[end] send topic for Refresh Balance Info of BO<=======================================");
		
		
		return  IConstants.TRANSFER_STATUS.SUCCESS;	
	
	}
	/**
	 * 　transfer money from BO to fx
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 24, 2012
	 * @MdDate
	 */
	private Integer transferMoneyBoFx(TransferMoneyInfo transferMoneyInfo, List<CustomerServicesInfo> listCustomerServiceInfo,BigDecimal convertedAmount, BigDecimal convertRate, SysAppDate amsAppDate, AmsTransferMoney amsTransferMoney, AmsCustomer amsCustomer, String currencyCode, CurrencyInfo fromCurrencyInfo){
		String language = "";
		if(amsCustomer != null){
			language = amsCustomer.getDisplayLanguage();			
		}
		if(language == null || StringUtils.isBlank(language)) {
			language = IConstants.Language.ENGLISH;
		}
		BigDecimal bonusBoAmount = MathUtil.parseBigDecimal(0);
		BigDecimal bonusFxAmount = MathUtil.parseBigDecimal(0);
		Double balance = new Double(0);
		Integer plusMT4 = new Integer(0);
		log.info("[start] transfer money from BO to FX");				
		String boCustomerServiceId = "";
		String fxCustomerServiceId = "";
		// get customer service info of FX
		CustomerServicesInfo customerServiceInfo = null;		
		CustomerServicesInfo customerFxServiceInfo = null;
		if(listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
			for(CustomerServicesInfo serviceInfo : listCustomerServiceInfo) {
				customerServiceInfo = serviceInfo;
				if(IConstants.SERVICES_TYPE.FX.equals(serviceInfo.getServiceType())) {
					fxCustomerServiceId = customerServiceInfo.getCustomerServiceId();
					customerFxServiceInfo = customerServiceInfo;
				}else if(IConstants.SERVICES_TYPE.BO.equals(serviceInfo.getServiceType())) {
					boCustomerServiceId = customerServiceInfo.getCustomerServiceId();
				}
				
			}				
		}
		if(customerServiceInfo == null) {
			log.warn("Cannot find customer serviceId");
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		
		
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();					
		amsCashBalanceId.setCurrencyCode(transferMoneyInfo.getFromCurrencyCode());
		amsCashBalanceId.setCustomerId(transferMoneyInfo.getCustomerId());
		amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.BO);
		AmsCashBalance amsCashBalanceBo = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceId);
		if(amsCashBalanceBo == null) {
			log.warn("Cannot find balance AMS of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}	
		log.info("start transfer money to FX");
		BalanceInfo balanceInfo  = balanceManager.getBalanceInfo(transferMoneyInfo.getCustomerId(), IConstants.SERVICES_TYPE.FX, transferMoneyInfo.getToCurrencyCode());
		if(balanceInfo == null) {
			log.warn("Cannot find balance FX of customerID: " + transferMoneyInfo.getCustomerId());
			return IConstants.TRANSFER_STATUS.FAIL;
		}
		Map<String, String> mapDepositDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
		String description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_BALANCE_BO_FX);
		//update balance
		log.info("[start] plus mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fxCustomerServiceId + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		plusMT4 = MT4Manager.getInstance().depositBalance(fxCustomerServiceId, transferMoneyInfo.getTransferMoney().doubleValue(), FundRecord.BALANCE, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
		log.info("[end] plus mt4, customer id= " + transferMoneyInfo.getCustomerId() + ", customer service id = " + fxCustomerServiceId + ", money = " + transferMoneyInfo.getTransferMoney().doubleValue());
		if(plusMT4.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS)){			
			balance = amsCashBalanceBo.getCashBalance() - convertedAmount.doubleValue();
			// insert cashflow for BO
			Double cashFlowAmount = 0 - convertedAmount.doubleValue();
			log.info("BO start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus amount =" + cashFlowAmount.doubleValue() + "balance " + balance);
			getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), boCustomerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER, cashFlowAmount, IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.BO, balance, convertRate.doubleValue(), transferMoneyInfo.getFromCurrencyCode());
			log.info("BO start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after minus amount =" + cashFlowAmount.doubleValue() + "balance " + balance);
			// update balance for BO
			
			log.info("BO start update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + convertedAmount.doubleValue());
			amsCashBalanceBo = getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.BO, convertedAmount.doubleValue(), bonusBoAmount.doubleValue(), Boolean.TRUE); // deductFlag = true
			log.info("BO end update cash balance for customer id "  + transferMoneyInfo.getCustomerId() + ", money = " + convertedAmount.doubleValue());
			// insert cashflow for FX
			//balance = balanceInfo.getBalance() + convertedAmount.doubleValue();
			// QuyTM add balance of FX when insert into cashflow
			balanceInfo.setBalance(balanceInfo.getBalance() + transferMoneyInfo.getTransferMoney().doubleValue());
			log.info("FX start update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money =  " + transferMoneyInfo.getTransferMoney().doubleValue() + "balance " + balanceInfo.getBalance());
			getiDepositManager().insertCashFlow(amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getCustomerId(), fxCustomerServiceId, amsAppDate, IConstants.CASH_FLOW_TYPE.MONEY_TRANSFER,transferMoneyInfo.getTransferMoney().doubleValue(), IConstants.SOURCE_TYPE.TRANFER_MONEY, IConstants.SERVICES_TYPE.FX, balanceInfo.getBalance(), convertRate.doubleValue(), transferMoneyInfo.getToCurrencyCode());
			log.info("FX end update cash flow for customer id "  + transferMoneyInfo.getCustomerId() + " after plus money =  " + transferMoneyInfo.getTransferMoney().doubleValue() + "balance " + balanceInfo.getBalance());
			
			//start bonus	
			log.info("[start] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId());
			BigDecimal netDepositAmount = MathUtil.parseBigDecimal(0);
			AmsCashBalanceId amsCashBalanceFxId = new AmsCashBalanceId();
			amsCashBalanceFxId.setCurrencyCode(transferMoneyInfo.getToCurrencyCode());
			amsCashBalanceFxId.setCustomerId(transferMoneyInfo.getCustomerId());
			amsCashBalanceFxId.setServiceType(IConstants.SERVICES_TYPE.FX);
			AmsCashBalance amsCashBalanceFx = getiAmsCashBalanceDAO().findById(AmsCashBalance.class, amsCashBalanceFxId);
			if(amsCashBalanceFx != null) {
				netDepositAmount = MathUtil.parseBigDecimal(amsCashBalanceFx.getNetDepositAmount());
			}
			log.info("[end] checking NET_DEPOSIT of account " + transferMoneyInfo.getCustomerId() + " with NET_DEPOSIT_AMOUNT = " + netDepositAmount);
			BigDecimal totalNetDepositAmount = netDepositAmount.add(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()));
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + transferMoneyInfo.getToCurrencyCode());
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}	
			AmsPromotion amsPromotion = null;
			totalNetDepositAmount = totalNetDepositAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			log.info("after calculate total net deposit amount = " + totalNetDepositAmount);		
			if(totalNetDepositAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
				//amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
				amsPromotion = iAmsPromotionDAO.getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, customerFxServiceInfo.getSubGroupId());
				if(amsPromotion != null){
					//bonusFxAmount = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, transferMoneyInfo.getWlCode());
					//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - Start
					BigDecimal baseAmount = getBonusByNetDeposit(MathUtil.parseBigDecimal(transferMoneyInfo.getTransferMoney()), netDepositAmount, amsPromotion.getKind());
					bonusFxAmount = getiPromotionManager().getBonusAmount(baseAmount, transferMoneyInfo.getToCurrencyCode(), IConstants.PROMOTION_TYPE.DEPOSIT, IConstants.SERVICES_TYPE.FX, customerFxServiceInfo.getSubGroupId());
					//[NTS1.0-Quan.Le.Minh]Jan 18, 2013M - End
					
					if(bonusFxAmount.compareTo(MathUtil.parseBigDecimal(0)) > 0){		
						log.info("Promotion: bonus = " + bonusFxAmount.doubleValue());
						description = mapDepositDescription.get(IConstants.FUND_DESCRIPION.FE_TRANSFER_CREDIT_AMS_FX);
						Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(fxCustomerServiceId, bonusFxAmount.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
						if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
							log.info("start save data to ams promotion customer with promotionID= " + amsPromotion.getPromotionId());
							getiPromotionManager().saveAmsPromotionCustomer(transferMoneyInfo.getCustomerId(), amsPromotion.getPromotionId(), bonusFxAmount.doubleValue(),amsTransferMoney.getTransferMoneyId(), transferMoneyInfo.getToCurrencyCode());
							log.info("end save data to ams promotion customer with promotionID= " + amsPromotion.getPromotionId());
							log.info("[start] Send mail to customer about bonus amount " + amsTransferMoney.getTransferMoneyId());
//							DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
							
							sendmailDepositBonus(amsCustomer, transferMoneyInfo, language, bonusFxAmount);
						}
						log.info("start update cretdit amount = " + bonusFxAmount.doubleValue() + "and promotion ID = "  + amsPromotion.getPromotionId() + "to ams money transfer");
						amsTransferMoney.setPromotionId(amsPromotion.getPromotionId());
						amsTransferMoney.setCreditAmount(bonusFxAmount.doubleValue());
						getiAmsTransferMoneyDAO().merge(amsTransferMoney);
						log.info("end update cretdit amount = " + bonusFxAmount.doubleValue() + "and promotion ID = "  + amsPromotion.getPromotionId()+ "to ams money transfer");
					}
				}
			}

			//end bonus
			log.info("[start] update cash balance into FX for Apply NET_DEPOSIT");
			// update balance for FX
			getiDepositManager().updateAmsCashBalance(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getFromCurrencyCode(), IConstants.SERVICES_TYPE.FX, convertedAmount.doubleValue(), bonusFxAmount.doubleValue(), Boolean.FALSE); // deductFlag = TRUE
			log.info("[end] update cash balance into FX for Apply NET_DEPOSIT");
			
			checkingPromotionForLosscut(transferMoneyInfo, customerFxServiceInfo, amsTransferMoney, amsCustomer, language);
			log.info("start update status of transfer money success" + IConstants.TRANSFER_STATUS.SUCCESS);
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.SUCCESS);
			amsTransferMoney.setTranferCompleteDate(amsAppDate.getId().getFrontDate());
			amsTransferMoney.setTranferCompleteDateTime(new Timestamp(System.currentTimeMillis()));
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);
			log.info("end update status of transfer money");
			log.info("[end] transfer money from BO to CopyTrade");
			
			log.info("======================================>[start] send topic for Refresh Balance Info of BO<======================================");
			try {			
				String customerServiceId = transferMoneyInfo.getCustomerId() + IConstants.SERVICES_TYPE.BO;
				Double sendBalance = 0 - convertedAmount.doubleValue();		
				BigDecimal sendingMoney = MathUtil.rounding(sendBalance, fromCurrencyInfo.getCurrencyDecimal(), fromCurrencyInfo.getCurrencyRound());
				log.info("sending BO  customerServiceId=" + customerServiceId + " with amount = " + sendingMoney.doubleValue());
				BalanceUpdateInfo balanceUpdateInfo = new BalanceUpdateInfo(transferMoneyInfo.getCustomerId(), customerServiceId, sendingMoney, 0, new Timestamp(System.currentTimeMillis())); // fix type = 0
//				JMSSendClient.getInstance().sendBalanceUpdateTopic(balanceUpdateInfo);
				jmsContextSender.sendBalanceUpdateTopic(balanceUpdateInfo, false);
				log.info("sending BO  customerServiceId=" + customerServiceId + " with amount = " + sendingMoney + "COMPLETED");
			} catch(Exception ex) {
				log.error(ex.getMessage(), ex);
			}		
			log.info("======================================>[end] send topic for Refresh Balance Info of BO<=======================================");
			
			return  IConstants.TRANSFER_STATUS.SUCCESS;	
		}else{
			log.info("update balance mt4 error, set status of transfer money is fail " + IConstants.TRANSFER_STATUS.FAIL);
			amsTransferMoney.setStatus(IConstants.TRANSFER_STATUS.FAIL);
			getiAmsTransferMoneyDAO().merge(amsTransferMoney);	
			amsTransferMoney.setTranferCompleteDateTime(null);
			log.info("[end] transfer money from BO to CopyTrade");
			return  IConstants.TRANSFER_STATUS.FAIL;
		}
	
	}
	/**
	 *  checking promotion for loss cut　
	 * 
	 * @param
	 * @return
	 * @auth Mai.Thu.Huyen
	 * @CrDate Sep 28, 2012
	 * @MdDate
	 */
	private void checkingPromotionForLosscut(TransferMoneyInfo transferMoneyInfo, CustomerServicesInfo customerServiceInfo, AmsTransferMoney amsTransferMoney, AmsCustomer amsCustomer, String language){
		customerServiceInfo = iAccountManager.getCustomerServiceInfo(customerServiceInfo.getCustomerServiceId());
		if(customerServiceInfo != null) {
			log.info("[start] process losscut for customerid " + transferMoneyInfo.getCustomerId() + " with MT4ID " + customerServiceInfo.getCustomerServiceId() + ", losscutFlg: " + customerServiceInfo.getLosscutFlg() + ", losscutDateTime: " + customerServiceInfo.getLosscutDatetime());
			Map<String, String> mapFundDescription = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.FUND_DESCRIPTION);
			String description = "";
			//AmsPromotion amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, transferMoneyInfo.getWlCode());
			AmsPromotion amsPromotion = getiPromotionManager().getAmsPromotion(IConstants.PROMOTION_TYPE.LOSSCUT, customerServiceInfo.getServiceType(), customerServiceInfo.getSubGroupId());
			
			if(amsPromotion != null) {									
				log.info("Find promotion for losscut");
				log.info("[start] process promotion losscut for customerId: " + transferMoneyInfo.getCustomerId());
				log.info("Checking losscutflag of customer " + transferMoneyInfo.getCustomerId() + " and customer service id " + customerServiceInfo.getCustomerServiceId());
				if(customerServiceInfo != null && IConstants.ACTIVE_FLG.ACTIVE.equals(customerServiceInfo.getLosscutFlg())) {
					// get lastest deposit of this customer
					log.info("customerId " + customerServiceInfo.getCustomerServiceId() + " is losscut");
					log.info("[start] get deposit lastest for customerId " + transferMoneyInfo.getCustomerId());

					AmsDeposit amsDepositLastest = getiAmsDepositDAO().getLastestDeposit(transferMoneyInfo.getCustomerId(), customerServiceInfo.getLosscutDatetime());
					if(amsDepositLastest != null) {
						log.info("[start] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
						AmsPromotionCustomer amsPromotionCustomer = getiPromotionManager().getAmsPromotionCustomer(transferMoneyInfo.getCustomerId(), amsDepositLastest.getDepositId(), amsPromotion.getPromotionId());
						log.info("[end] Validate Promotion for depositId " + amsDepositLastest.getDepositId());
						if(amsPromotionCustomer != null) {
							log.info("CustomerID: " + transferMoneyInfo.getCustomerId() + " recieved promotion losscut");
							log.info("[start] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());
							customerServiceInfo.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
							AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO().findById(AmsCustomerService.class, customerServiceInfo.getCustomerServiceId());
							getiAmsCustomerServiceDAO().merge(amsCustomerService);
							log.info("[end] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());
						} else {
							log.info("Lastest deposit of customerServiceId = " + customerServiceInfo.getCustomerServiceId() + " is " + amsDepositLastest.getDepositAmount() + " and depositId " + amsDepositLastest.getDepositId());
							log.info("[start] get bonus amount for losscut of customerServiceId " + customerServiceInfo.getCustomerServiceId() );
							//BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, transferMoneyInfo.getWlCode());
							BigDecimal losscutAmountBonus = getiPromotionManager().getBonusAmount(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode(), IConstants.PROMOTION_TYPE.LOSSCUT, customerServiceInfo.getServiceType(), customerServiceInfo.getSubGroupId());
							
							log.info("customerServiceId " + customerServiceInfo.getCustomerServiceId() + " will be received losscutAmountBonus = " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for Promotion Losscut");
							log.info("[start] send request deposit to credit for customerServiceId " + customerServiceInfo.getCustomerServiceId());	
							description = mapFundDescription.get(IConstants.FUND_DESCRIPION.FE_PROMOTION_LOSSCUT_CREDIT);
							Integer resultUpdateCredit = MT4Manager.getInstance().depositBalance(customerServiceInfo.getCustomerServiceId(), losscutAmountBonus.doubleValue(), FundRecord.CREDIT, description, ITrsConstants.FUND_CREDIT_MODE.NOT_CREDIT_OUT);
							if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(resultUpdateCredit)) {
								log.info("customer service id " + customerServiceInfo.getCustomerServiceId() + " has been received " + losscutAmountBonus + " " + amsDepositLastest.getCurrencyCode() + " for losscut promotion");
								if(IConstants.CURRENCY_CODE.JPY.equals(amsDepositLastest.getCurrencyCode())) {
									log.info("Because currencyCode of customerId " + customerServiceInfo.getCustomerServiceId() + " is japan so system will not send mail to this account");
								} else {
									if(IConstants.Language.JAPANESE.equals(language)) {
										language = IConstants.Language.ENGLISH;
									}
//									DecimalFormat formater = new DecimalFormat(IConstants.NUMBER_FORMAT.CURRENCY_DECIMAL);
									sendmailLossCut(amsTransferMoney, amsCustomer, language, amsDepositLastest, losscutAmountBonus);
								}
								log.info("[start] Insert data into PROMOTION CUSTOMER");
								getiPromotionManager().saveAmsPromotionCustomer(transferMoneyInfo.getCustomerId(), amsPromotion.getPromotionId(), losscutAmountBonus.doubleValue(), amsDepositLastest.getDepositId(), transferMoneyInfo.getToCurrencyCode()); //check again HUYENMT													
								log.info("[end] Insert data into PROMOTION CUSTOMER");
							} else {
								log.warn("Cannot plus credit for customer service id " + customerServiceInfo.getCustomerServiceId() + " because returnCode = " + resultUpdateCredit);
							}
							log.info("[start] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());
							
							AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO().findById(AmsCustomerService.class, customerServiceInfo.getCustomerServiceId());
							if(amsCustomerService != null){
								amsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
								getiAmsCustomerServiceDAO().merge(amsCustomerService);							
							}
							log.info("[end] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());
							log.info("[end] send request deposit to credit for customerServiceId " + customerServiceInfo.getCustomerServiceId());
							log.info("[end] get bonus amount for losscut of customerServiceId " + customerServiceInfo.getCustomerServiceId() );
						}
						
						
					} else {
						log.info("customerId " + customerServiceInfo.getCustomerServiceId() + " no longer deposit into system");
						log.info("[start] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());						
						AmsCustomerService amsCustomerService = getiAmsCustomerServiceDAO().findById(AmsCustomerService.class, customerServiceInfo.getCustomerServiceId());
						amsCustomerService.setLosscutFlg(IConstants.ACTIVE_FLG.INACTIVE);
						getiAmsCustomerServiceDAO().merge(amsCustomerService);
						log.info("[end] update losscutFlag of customerServiceId " + customerServiceInfo.getCustomerServiceId());
					}
					log.info("[end] get deposit lastest for customerId " + transferMoneyInfo.getCustomerId());
				} else {
					log.info("CustomerID: " + transferMoneyInfo.getCustomerId() + " has losscutFlag = false");
				}
				
				log.info("[end] process promotion losscut for customerId: " + transferMoneyInfo.getCustomerId());
			} else {
				log.info("Cannot find promotion for losscut");
			}
			log.info("[end] process losscut for customerid " + transferMoneyInfo.getCustomerId() + " with MT4ID " + customerServiceInfo.getCustomerServiceId() + ", losscutFlg: " + customerServiceInfo.getLosscutFlg() + ", losscutDateTime: " + customerServiceInfo.getLosscutDatetime());
		}else{
			log.info("Can't get info of customer service");
		}
		
	}
	private void sendmailLossCut(AmsTransferMoney amsTransferMoney, AmsCustomer amsCustomer, String language, AmsDeposit amsDepositLastest, BigDecimal losscutAmountBonus) {
		log.info("[start] send mail to customerId " + amsCustomer.getCustomerId());
//		MailTemplateInfo mailTemplateInfo = (MailTemplateInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_MAIL_TEMPLATE + IConstants.MAIL_TEMPLATE.AMS_INSUARANCE_LOSSCUT + "_" + language);
		String mailCode = new StringBuffer(IConstants.MAIL_TEMPLATE.AMS_INSUARANCE_LOSSCUT).append("_").append(language).toString();
		AmsMailTemplateInfo amsMailTemplateInfo = new AmsMailTemplateInfo();
		amsMailTemplateInfo.setDepositAmount(balanceManager.formatNumber(MathUtil.parseBigDecimal(amsDepositLastest.getDepositAmount()), amsDepositLastest.getCurrencyCode()));
		amsMailTemplateInfo.setDepositCurrency(balanceManager.getCurrencyCode(amsDepositLastest.getCurrencyCode(), language));
		amsMailTemplateInfo.setDepositDate(DateUtil.toString(amsDepositLastest.getDepositCompletedDatetime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_DDMMYYYY));
//										amsMailTemplateInfo.setBonusAmount(formater.format(losscutAmountBonus));
		amsMailTemplateInfo.setBonusAmount(balanceManager.formatNumber(losscutAmountBonus, amsDepositLastest.getCurrencyCode()));
		amsMailTemplateInfo.setFullName(amsCustomer.getFullName());
		amsMailTemplateInfo.setDepositId(amsTransferMoney.getTransferMoneyId());
		Map<String, String> mapMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);							
		amsMailTemplateInfo.setDepositMethod(mapMethod.get(StringUtil.toString(IConstants.PAYMENT_METHOD.PAYZA)));
		amsMailTemplateInfo.setLoginId(amsCustomer.getLoginId());
		HashMap<String, String> to = new HashMap<String, String>();
		to.put(amsCustomer.getMailMain(), amsCustomer.getMailMain());
		amsMailTemplateInfo.setTo(to);																												
		amsMailTemplateInfo.setMailCode(mailCode);
		amsMailTemplateInfo.setSubject(mailCode);
		amsMailTemplateInfo.setWlCode(amsCustomer.getWlCode());
//		JMSSendClient.getInstance().sendMail(amsMailTemplateInfo);
		jmsContextSender.sendMail(amsMailTemplateInfo, false);
		log.info("[end] send mail to customerId " + amsCustomer.getCustomerId());
	}

	private synchronized String generateUniqueId(String contextID) {
		if (contextID == null || contextID.trim().equals("")) {
			return null;
		}		
		String uniqueId = getiSysUniqueidCounterDAO().generateUniqueId(contextID);		
		return uniqueId;
	}
	
	private boolean validateConvertedAmountToTransfer(TransferMoneyInfo transferMoneyInfo, BigDecimal convertedAmount, String currencyCode){	
		log.info("[start] validateConvertedAmountToTransfer, convertedAmount:" + convertedAmount + ", currencyCode: " + currencyCode);	
		boolean flag = false;
		
		BigDecimal amountAvailableTransfer = MathUtil.parseBigDecimal(0);
		BalanceInfo balanceInfo = null;
		try {
			//get balance of TransferFrom service
			balanceInfo = balanceManager.getBalanceInfo(transferMoneyInfo.getCustomerId(), transferMoneyInfo.getTransferFrom(), currencyCode);
			
			if(balanceInfo != null) {
				log.info("Amount available: " + balanceInfo.getAmountAvailable());
				CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + balanceInfo.getCurrencyCode());
				if(currencyInfo != null) {
					amountAvailableTransfer = MathUtil.rounding(balanceInfo.getAmountAvailable(), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				} else {
					amountAvailableTransfer = MathUtil.parseBigDecimal(balanceInfo.getAmountAvailable());
				}
				
				if(convertedAmount.compareTo(amountAvailableTransfer) > 0){
					log.info("Converted amount: " + convertedAmount  + " is larger than Amount available: " + amountAvailableTransfer);
					flag = true;
				}
			}					
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		log.info("[end] validateConvertedAmountToTransfer, convertedAmount:" + convertedAmount + ", amountAvailableTransfer" + amountAvailableTransfer);
		return flag;
	}
	
	public boolean validateBoCustomerStatus(String customerId){
		BoCustomer boCustomer = null;
		List<BoCustomer> list = boCustomerDAO.findByCustomerId(customerId);
		if(list!=null && list.size()>0){
			boCustomer = list.get(0);
			if(ITrsConstants.BO_TEST_STATUS.TEST_ALLOWED.equals(boCustomer.getBoTestStatus())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculate credit amount by net deposit
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 24, 2013
	 */
	private BigDecimal getBonusByNetDeposit(BigDecimal depositAmount, BigDecimal netDepositAmount, Integer promotionKind){
		BigDecimal baseAmount = MathUtil.parseBigDecimal(0);
		if(promotionKind == null){
			baseAmount = depositAmount;
		}else{
			if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(promotionKind)){
				baseAmount = depositAmount;
			}
			if (IConstants.PROMOTION_KIND.BASED_NET_DEPOSIT_AMOUNT.equals(promotionKind)){
				baseAmount = netDepositAmount.add(depositAmount);
			}
		}
		return baseAmount;
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
	
	public CurrencyInfo getCurrencyInfo(String currencyCode) {
		CurrencyInfo currencyInfo = new CurrencyInfo();
		SysCurrency sysCurrency  = getiSysCurrencyDAO().getCurrencyInfo(currencyCode);
		if(sysCurrency!=null){
			BeanUtils.copyProperties(sysCurrency, currencyInfo);
		}
		return currencyInfo;
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
	 * @return the iAmsDepositDAO
	 */
	public IAmsDepositDAO<AmsDeposit> getiAmsDepositDAO() {
		return iAmsDepositDAO;
	}
	/**
	 * @param iAmsDepositDAO the iAmsDepositDAO to set
	 */
	public void setiAmsDepositDAO(IAmsDepositDAO<AmsDeposit> iAmsDepositDAO) {
		this.iAmsDepositDAO = iAmsDepositDAO;
	}
	/**
	 * @return the iAmsCustomerServiceDAO
	 */
	public IAmsCustomerServiceDAO<AmsCustomerService> getiAmsCustomerServiceDAO() {
		return iAmsCustomerServiceDAO;
	}
	/**
	 * @param iAmsCustomerServiceDAO the iAmsCustomerServiceDAO to set
	 */
	public void setiAmsCustomerServiceDAO(IAmsCustomerServiceDAO<AmsCustomerService> iAmsCustomerServiceDAO) {
		this.iAmsCustomerServiceDAO = iAmsCustomerServiceDAO;
	}
	private void updateNetDeposit(Integer serviceType, String customerId, String currencyCode, Double amount) {
		try {
			AmsCashBalanceId id = new AmsCashBalanceId();
			id.setCurrencyCode(currencyCode);
			id.setCustomerId(customerId);
			id.setServiceType(serviceType);
			AmsCashBalance amsCashBalance = iAmsCashBalanceDAO.findById(AmsCashBalance.class, id);
			if(amsCashBalance != null) {
				amsCashBalance.setNetDepositAmount(amsCashBalance.getNetDepositAmount() + amount.doubleValue());
				amsCashBalance.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				iAmsCashBalanceDAO.merge(amsCashBalance);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	@Override
	public void loadAdditionalFxData(TransferModel transferModel) {
		TransferMoneyInfo transferMoneyInfo = transferModel.getTransferMoneyInfo();
		Integer fromServiceType = transferMoneyInfo.getTransferFrom();
		
		String customerId = transferMoneyInfo.getCustomerId();
		String currencyCode;
		BigDecimal transferAmount;
		if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType)){
			currencyCode = transferMoneyInfo.getFromCurrencyCode();
			transferAmount = MathUtil.parseBigDecimal(transferMoneyInfo.getConvertedAmount());
		}else{
			currencyCode = transferMoneyInfo.getToCurrencyCode();
			transferAmount = MathUtil.parseBigDecimal(transferModel.getAmount());
		}
		
		AmsCashBalanceId amsCashBalanceId = new AmsCashBalanceId();
		amsCashBalanceId.setCurrencyCode(currencyCode);
		amsCashBalanceId.setCustomerId(customerId);
		amsCashBalanceId.setServiceType(IConstants.SERVICES_TYPE.FX);
		
		AmsCashBalance amsCashBalance = amsCashBalanceDAO.findById(AmsCashBalance.class, amsCashBalanceId);
		
		if(amsCashBalance != null && IConstants.ACTIVE_FLG.ACTIVE.equals(amsCashBalance.getActiveFlg())){
			BigDecimal netDepositAmount = MathUtil.parseBigDecimal(amsCashBalance.getNetDepositAmount());
			
			if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType)){
				netDepositAmount = netDepositAmount.subtract(transferAmount);
			}else{
				netDepositAmount = netDepositAmount.add(transferAmount);
			}
			
			transferMoneyInfo.setNetDepositAmount(netDepositAmount);
		}
	}
	
	public boolean checkTransferFlagOnMasterData(String configKey, String wlCode) {
		String awc = getiAmsTransferMoneyDAO().getAllowTransferFlag(configKey, wlCode);
		if(awc.equals(IConstants.AMS_CUSTOMER_SERVICE.SUFFIX)){
			return true;
		}
		return false;
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
