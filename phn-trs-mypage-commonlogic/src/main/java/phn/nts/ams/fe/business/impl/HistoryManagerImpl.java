package phn.nts.ams.fe.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import phn.com.nts.ams.web.condition.AmsFeHistorySearchCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.*;
import phn.com.nts.db.entity.*;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.IHistoryManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;

public class HistoryManagerImpl implements IHistoryManager{
	private static final Logit log = Logit.getInstance(HistoryManagerImpl.class);
	private IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> iAmsFeSearchHistoryDao;
	private IAmsDepositDAO<AmsDeposit> iAmsDepositDAO;
	private IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO;
	private IAmsIbClientDAO<AmsIbClient> iAmsIbClientDAO;
	private IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO;
	private IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO;

	public IAmsWithdrawalDAO<AmsWithdrawal> getiAmsWithdrawalDAO() {
		return iAmsWithdrawalDAO;
	}

	public void setiAmsWithdrawalDAO(
			IAmsWithdrawalDAO<AmsWithdrawal> iAmsWithdrawalDAO) {
		this.iAmsWithdrawalDAO = iAmsWithdrawalDAO;
	}

	public IAmsDepositDAO<AmsDeposit> getiAmsDepositDAO() {
		return iAmsDepositDAO;
	}

	public void setiAmsDepositDAO(IAmsDepositDAO<AmsDeposit> iAmsDepositDAO) {
		this.iAmsDepositDAO = iAmsDepositDAO;
	}
	
	public IAmsIbClientDAO<AmsIbClient> getiAmsIbClientDAO() {
		return iAmsIbClientDAO;
	}

	public void setiAmsIbClientDAO(IAmsIbClientDAO<AmsIbClient> iAmsIbClientDAO) {
		this.iAmsIbClientDAO = iAmsIbClientDAO;
	}
	public List<AmsFeHistorySearchCondition> getListAmsFeSearchHistory(AmsFeHistorySearchCondition condition, PagingInfo pagingInfo, String customerId) {
		List<AmsFeHistorySearchCondition> listForDisplay = null;
		try {
			List<AmsViewFeSearchHistory> listAmsFeSearchHistory = getiAmsFeSearchHistoryDao().getHistory(condition, pagingInfo);
			if(listAmsFeSearchHistory != null && listAmsFeSearchHistory.size() >0) {
				listForDisplay = new ArrayList<AmsFeHistorySearchCondition>();
				for (AmsViewFeSearchHistory amsFeSearchHistory : listAmsFeSearchHistory) {
					AmsFeHistorySearchCondition amsFeHistorySearchCondition = new AmsFeHistorySearchCondition();
					BeanUtils.copyProperties(amsFeHistorySearchCondition, amsFeSearchHistory);
					AmsCustomer amsCustomer =getiAmsCustomerDAO().findById(AmsCustomer.class,amsFeSearchHistory.getCustomerId());
					amsFeHistorySearchCondition.setCustomerName(amsCustomer == null ? "" : amsCustomer.getFullName());
					Map<String, String> mapPaymentMethod = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_METHOD);
					
					String type = amsFeHistorySearchCondition.getType();
					boolean isWithdrawal = ITrsConstants.TRANSACTION_TYPE_NAME.WITHDRAWAL.equalsIgnoreCase(type);
					Integer paymentMethod = amsFeHistorySearchCondition.getMethod();
					boolean isBankTransfer = Integer.valueOf(IConstants.PAYMENT_METHOD.BANK_TRANSFER).equals(paymentMethod);
					if (isWithdrawal || (!isWithdrawal && !isBankTransfer)){
						amsFeHistorySearchCondition.setMethodName(mapPaymentMethod.get(StringUtil.toString(paymentMethod)));
					}

					// Add source and destination for transaction
					
					if(ITrsConstants.TRANSACTION_TYPE_NAME.DEPOSIT.equalsIgnoreCase(type)) {
						String destination = getServiceType(amsFeSearchHistory.getDestination());
						amsFeHistorySearchCondition.setDestination(destination);
						amsFeHistorySearchCondition.setSource(IConstants.SYMBOL_TRANSFER.EMPTY_SYMBOL);
						
					} else if (ITrsConstants.TRANSACTION_TYPE_NAME.WITHDRAWAL.equalsIgnoreCase(type)) {
						String source = getServiceType(amsFeSearchHistory.getSource());
						amsFeHistorySearchCondition.setSource(source);
						amsFeHistorySearchCondition.setDestination(IConstants.SYMBOL_TRANSFER.EMPTY_SYMBOL);
					} else if (ITrsConstants.TRANSACTION_TYPE_NAME.TRANSFER.equalsIgnoreCase(type) 
							|| ITrsConstants.TRANSACTION_TYPE_NAME.CASHBACK.equalsIgnoreCase(type)
							|| ITrsConstants.TRANSACTION_TYPE_NAME.SOCIAL_FEE.equalsIgnoreCase(type)
							|| ITrsConstants.TRANSACTION_TYPE_NAME.SWAP.equalsIgnoreCase(type)
							|| ITrsConstants.TRANSACTION_TYPE_NAME.PL.equalsIgnoreCase(type)
							|| ITrsConstants.TRANSACTION_TYPE_NAME.INVITE_FEE.equalsIgnoreCase(type)) {
						
						String destination = getServiceType(amsFeSearchHistory.getDestination());
						String source = getServiceType(amsFeSearchHistory.getSource());
						if(destination == null) {
							destination = IConstants.SYMBOL_TRANSFER.EMPTY_SYMBOL;
						}
						if(source == null) {
							source = IConstants.SYMBOL_TRANSFER.EMPTY_SYMBOL;
						}
						amsFeHistorySearchCondition.setDestination(destination);
						amsFeHistorySearchCondition.setSource(source);
					} 
					
					if(amsFeSearchHistory.getMethod() == null) {
						amsFeHistorySearchCondition.setMethodName(IConstants.SYMBOL_TRANSFER.EMPTY_SYMBOL);
					}
					
					listForDisplay.add(amsFeHistorySearchCondition);
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return listForDisplay;
	}

	/**
	 * @return the iAmsFeSearchHistoryDao
	 */
	public IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> getiAmsFeSearchHistoryDao() {
		return iAmsFeSearchHistoryDao;
	}

	/**
	 * @param iAmsFeSearchHistoryDao the iAmsFeSearchHistoryDao to set
	 */
	public void setiAmsFeSearchHistoryDao(
			IAmsViewFeSearchHistoryDAO<AmsViewFeSearchHistory> iAmsFeSearchHistoryDao) {
		this.iAmsFeSearchHistoryDao = iAmsFeSearchHistoryDao;
	}
	
	/**
	 * get Deposit by depositId
	 */
	public AmsDeposit getDeposit(String depositId) {
		AmsDeposit amsDeposit = null;
		try{
			amsDeposit = getiAmsDepositDAO().findById(AmsDeposit.class, depositId);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return amsDeposit;
	}
	
	/**
	 * get Withdrawal by withdrawalID
	 */
	
	public AmsWithdrawal getAmsWithdrawal(String withdrawalId) {
		AmsWithdrawal amsWithdrawal = null;
		try {
			amsWithdrawal = getiAmsWithdrawalDAO().getAmsWithDrawalInfo(withdrawalId);
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return amsWithdrawal;
	}
	
	/**
	 * Update status of deposit in DB
	 */
	public void updateAmsDepositStatus(AmsDeposit amsDeposit){
		log.info("[Start] update status for cancel deposit transaction");
		try {
			getiAmsDepositDAO().attachDirty(amsDeposit);			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] update status for cancel deposit transaction");
	}
	
	/**
	 * Update status of withdrawal	 in DB
	 */
	public void updateAmsWithdrawalStatus(AmsWithdrawal amsWithdrawal){
		log.info("[Start] update status for cancel withdrawal transaction");
		try {
			getiAmsWithdrawalDAO().attachDirty(amsWithdrawal);			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("[End] update status for cancel withdrawal transaction");
	}
	/**
	 * check is IB client or not
	 */
	public boolean isIBClientUser(String customerId, String ibCustomerId) {
		try{
			AmsIbClient amsIbClient = getiAmsIbClientDAO().getAmsIbClient(customerId, ibCustomerId);
			if(amsIbClient == null) {
				return false;
			}
		}catch(Exception ex) {
			log.error(ex.getMessage(),ex);
		}
		return true;
	}

    @Override
    public void updateBackNetDepositCc(String customerId, Double withdrawalAmount) {
        AmsCustomerSurvey survey = amsCustomerSurveyDAO.findById(AmsCustomerSurvey.class, customerId);
        if(survey != null){
            survey.setNetDepositCc((survey.getNetDepositCc() == null ? 0 : survey.getNetDepositCc()) + withdrawalAmount);
            amsCustomerSurveyDAO.merge(survey);
        }
    }

    public IAmsCustomerDAO<AmsCustomer> getiAmsCustomerDAO() {
		return iAmsCustomerDAO;
	}

	public void setiAmsCustomerDAO(IAmsCustomerDAO<AmsCustomer> iAmsCustomerDAO) {
		this.iAmsCustomerDAO = iAmsCustomerDAO;
	}
	
	// get Service type
	
	public String getServiceType(Integer serviceType) {
		Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		String serviceTypeString = null;
		if(IConstants.SERVICES_TYPE.FX.equals(serviceType)) {
			serviceTypeString = mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX));
		} else if (IConstants.SERVICES_TYPE.AMS.equals(serviceType)) {
			serviceTypeString = mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS));	
		}  else if (IConstants.SERVICES_TYPE.BO.equals(serviceType)) {
			serviceTypeString = mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO));	
		} else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(serviceType)) {
			serviceTypeString = mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE));
		}
		return serviceTypeString;
	}

    public IAmsCustomerSurveyDAO<AmsCustomerSurvey> getAmsCustomerSurveyDAO() {
        return amsCustomerSurveyDAO;
    }

    public void setAmsCustomerSurveyDAO(IAmsCustomerSurveyDAO<AmsCustomerSurvey> amsCustomerSurveyDAO) {
        this.amsCustomerSurveyDAO = amsCustomerSurveyDAO;
    }
}
