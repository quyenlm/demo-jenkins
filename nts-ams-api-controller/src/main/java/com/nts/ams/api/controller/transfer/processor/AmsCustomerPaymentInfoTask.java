package com.nts.ams.api.controller.transfer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import phn.com.nts.db.domain.AmsPaymentgwWlInfo;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.SysProperty;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.TRS_CONSTANT;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CustomerBankInfo;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsCustomerPaymentInfoRequestWraper;
import com.nts.ams.api.controller.util.Converter;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerBankInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerPaymentInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.PaymentMethod;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.PaymentType;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.WithdrawalMethod;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsCustomerPaymentInfoResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 21, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsCustomerPaymentInfoTask implements Runnable {
	private static Logit log = Logit.getInstance(AmsCustomerPaymentInfoTask.class);
	
	private AmsCustomerPaymentInfoRequestWraper wraper;
    private IProfileManager profileManager = null;
    private IWithdrawalManager withdrawalManager;
    private String msgCode;
    
	public AmsCustomerPaymentInfoTask(AmsCustomerPaymentInfoRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsCustomerPaymentInfoRequest request = wraper.getRequest();
			log.info("[start] handle AmsCustomerPaymentInfoRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			String customerId = request.getCustomerId();
			String wlCode = request.getWlCode();
			
			PaymentType paymentType = request.hasPaymentType() ? request.getPaymentType() : null;
			List<AmsCustomerPaymentInfo> listPaymentInfo = null;
			
			if(paymentType == null) {
				//All_TYPE
				listPaymentInfo = getAllPaymentInfo(wlCode, customerId);
			} else if(paymentType == PaymentType.WITHDRAWAL_TYPE) {
				
				listPaymentInfo = getWithdrawalPaymentInfo(wlCode, customerId);
				
			} else if(paymentType == PaymentType.VIRTUAL_TYPE) {
				//VIRTUAL_TYPE
				AmsCustomerPaymentInfo amsCustomerPaymentInfo = getVirtualPaymentInfo(customerId);
				if(amsCustomerPaymentInfo != null) {
					listPaymentInfo = new ArrayList<AmsCustomerPaymentInfo>();
					listPaymentInfo.add(amsCustomerPaymentInfo);
				}
			}
						
			Result result = Result.FAILED;
			AmsCustomerPaymentInfoResponse paymentInfoResponse = null;
			if(listPaymentInfo != null && listPaymentInfo.size() > 0) {
				AmsCustomerPaymentInfoResponse.Builder paymentInfoResponseBuilder = AmsCustomerPaymentInfoResponse.newBuilder();
				paymentInfoResponseBuilder.addAllPaymentInfo(listPaymentInfo);
				paymentInfoResponse = paymentInfoResponseBuilder.build();
				result = Result.SUCCESS;
			}

			//Response to client
			RpcMessage response = createRpcMessage(paymentInfoResponse, result, msgCode);
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response);
			log.info("[end] handle AmsCustomerPaymentInfoRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsCustomerPaymentInfoProcessor().onComplete(wraper);
		}
	}
	
	/**
	 * Get All PaymentInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 21, 2015
	 * @MdDate
	 */
	public List<AmsCustomerPaymentInfo> getAllPaymentInfo(String wlCode, String customerId) {
		log.info("[start] get AllPaymentInfo, customerId: " + customerId + ", wlCode: " + wlCode);
		
		List<AmsCustomerPaymentInfo> listPaymentInfo = new ArrayList<AmsCustomerPaymentInfo>();
		
		List<SysProperty> listPaymentMethod = FrontEndContext.getInstance().getPaymentInformationMethod();
		log.info("Loaded ListPaymentMethod from SYS_PROPERTY: " + listPaymentMethod);
		
		if(listPaymentMethod != null && listPaymentMethod.size() > 0) {
			
			for (SysProperty methodInfo : listPaymentMethod) {
				
				if(methodInfo.getId().getPropType().equals(String.valueOf(PaymentType.WITHDRAWAL_TYPE_VALUE))) {
					
					List<AmsCustomerPaymentInfo> withdrawalPaymentMethod = getWithdrawalPaymentInfo(wlCode, customerId);
					if(withdrawalPaymentMethod != null && withdrawalPaymentMethod.size() > 0)
						listPaymentInfo.addAll(withdrawalPaymentMethod);
					
				} else if(methodInfo.getId().getPropType().equals(String.valueOf(PaymentType.VIRTUAL_TYPE_VALUE))) {
					
					AmsCustomerPaymentInfo amsCustomerPaymentInfo = getVirtualPaymentInfo(customerId);
					if(amsCustomerPaymentInfo != null)
						listPaymentInfo.add(amsCustomerPaymentInfo);
				}
			}
		} else
			log.info("MapPaymentMethod from SYS_PROPERTY is empty");
			
		
		log.info("[end] get AllPaymentInfo");
		return listPaymentInfo;
	}
	
	/**
	 * Get Withdrawal PaymentInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 21, 2015
	 * @MdDate
	 */
	private List<AmsCustomerPaymentInfo> getWithdrawalPaymentInfo(String wlCode, String customerId) {
		log.info("[start] get WithdrawalPaymentInfo, customerId: " + customerId + ", wlCode: " + wlCode);
		List<AmsCustomerPaymentInfo> listPaymentInfo = null;
		
		//WITHDRAWAL_TYPE
		List<AmsPaymentgwWlInfo> listPaymentGwInfo = FrontEndContext.getInstance().getListPaymentGwWlInfo(wlCode);
				
		if(listPaymentGwInfo != null && listPaymentGwInfo.size() > 0) {
			log.info("Loaded listPaymentGwInfo, size: " + listPaymentGwInfo.size());
			listPaymentInfo = new ArrayList<AmsCustomerPaymentInfo>();
			
			for(AmsPaymentgwWlInfo method : listPaymentGwInfo) {
				if(method.getPaymentMethod().equals(WithdrawalMethod.BANK_TRANFER.getNumber())) {
					log.info("[start] get CustomerBankInfo for paymentMethod: " + method.getPaymentMethod());
					
					List<CustomerBankInfo> listCustomerBankInfo = withdrawalManager.getListCustomerBankInfo(customerId);
					log.info("Loaded listCustomerBankInfo: " + listCustomerBankInfo);
					
					if(listCustomerBankInfo != null && listCustomerBankInfo.size() > 0){
						for(CustomerBankInfo bankInfo : listCustomerBankInfo){
							if(bankInfo.getBankAccClass().equals(1)){
								bankInfo.setAccountTypeStr(AmsApiControllerMng.getMsg("nts.socialtrading.moneytransaction.withdrawal.label.normal"));
							} else if(bankInfo.getBankAccClass().equals(2)){
								bankInfo.setAccountTypeStr(AmsApiControllerMng.getMsg("nts.socialtrading.moneytransaction.withdrawal.label.current"));
							} else if(bankInfo.getBankAccClass().equals(3)){
								bankInfo.setAccountTypeStr(AmsApiControllerMng.getMsg("nts.socialtrading.moneytransaction.withdrawal.label.savings"));
							}
							
							AmsCustomerPaymentInfo.Builder paymentInfoBuilder = AmsCustomerPaymentInfo.newBuilder();
							AmsCustomerBankInfo amsCustomerBankInfo  = Converter.convertAmsCustomerBankInfo(bankInfo);
							log.info("Converted AmsCustomerBankInfo: " + amsCustomerBankInfo);
							
							if(amsCustomerBankInfo != null) {
								paymentInfoBuilder.setCustomerId(customerId);
								paymentInfoBuilder.setPaymentType(PaymentType.WITHDRAWAL_TYPE);
								paymentInfoBuilder.setPaymentMethod(PaymentMethod.PAYMENT_BANK_TRANFER);
								paymentInfoBuilder.setCustomerBankInfo(amsCustomerBankInfo);
								listPaymentInfo.add(paymentInfoBuilder.build());
							}
						}
						
					}
				}
				
				log.info("[end] get CustomerBankInfo for paymentMethod: " + method.getPaymentMethod());
			}
		} else {
			log.info("AmsPaymentgwWl for " + wlCode + " is empty");
		}
		
		log.info("[end] get WithdrawalPaymentInfo");
		return listPaymentInfo;
	}
	
	/**
	 * Get Virtual PaymentInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 21, 2015
	 * @MdDate
	 */
	private AmsCustomerPaymentInfo getVirtualPaymentInfo(String customerId) {
		log.info("[start] get getVirtualPaymentInfo, customerId: " + customerId);
		
		AmsCustomerPaymentInfo result = null;
		
		//VIRTUAL_TYPE
		AmsCustomerBankInfo amsCustomerBankInfo = getAmsCustomerBankInfo(customerId);
		if(amsCustomerBankInfo != null) {
			AmsCustomerPaymentInfo.Builder paymentInfoBuilder = AmsCustomerPaymentInfo.newBuilder();
			
			paymentInfoBuilder.setCustomerId(customerId);
			paymentInfoBuilder.setPaymentType(PaymentType.VIRTUAL_TYPE);
			paymentInfoBuilder.setCustomerBankInfo(amsCustomerBankInfo);
			
			result = paymentInfoBuilder.build();
		}
		
		log.info("[end] get getVirtualPaymentInfo");
		return result;
	}

	/**
	 * Get AmsCustomer BankInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 21, 2015
	 * @MdDate
	 */
	private AmsCustomerBankInfo getAmsCustomerBankInfo(String customerId){
		log.info("[start] get AmsCustomerBankInfo, customerId: " + customerId);
		
		AmsCustomerBankInfo.Builder builder = AmsCustomerBankInfo.newBuilder();
		
		//Load customer
		AmsCustomer amsCustomer = getProfileManager().getAmsCustomer(customerId);
		log.info("Loaded AmsCustomer: " + amsCustomer);
		
		if(amsCustomer != null && amsCustomer.getAmsSysVirtualBank() != null) {
			builder.setAccountNo(amsCustomer.getAmsSysVirtualBank().getId().getVirtualBankAccNo());
			log.info("Loaded VirtualBankAccNo: " + builder.getAccountNo());
		}

		String bankName = "";
		String branchName = "";
		String bankAccountType = "";
		String bankAccountName = "";
		String bankAccountNameKana = "";

		if (checkVirtualBankAccNoExist(amsCustomer)) {
			//Load bank config
			Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + TRS_CONSTANT.TRS_WL_CODE);
			bankName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_BANK_NAME);
			branchName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_BRANCH_NAME);
			bankAccountType = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_VIRTUAL_ACC_TYPE);
			bankAccountName = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_ACCOUNT_NAME);
			bankAccountNameKana = mapConfiguration.get(ITrsConstants.AMS_WHITELABEL_CONFIG_KEY.JA_ACCOUNT_NAME_KANA);

			log.info("Loaded bankName: " + bankName + ", branchName: " + branchName + ", bankAccountType: " + bankAccountType
                    + ", bankAccountName: " + bankAccountName + ", bankAccountNameKana: " + bankAccountNameKana);
		}

		builder.setBankName(bankName);
		builder.setBranchName(branchName);
		builder.setBankAccClass(bankAccountType);
		builder.setAccountName(bankAccountName);
		builder.setAccountNameKana(bankAccountNameKana);
		
		log.info("[end] get AmsCustomerBankInfo");
		return builder.build();
	}

	private boolean checkVirtualBankAccNoExist(AmsCustomer amsCustomer) {
		return amsCustomer.getAmsSysVirtualBank() != null && StringUtils.isNotEmpty(amsCustomer.getAmsSysVirtualBank().getId().getVirtualBankAccNo());
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
	public RpcMessage createRpcMessage(AmsCustomerPaymentInfoResponse withdrawalInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_CUSTOMER_PAYMENT_INFO_RESPONSE);
		if(errCode != null)
			response.setMessageCode(errCode);
		
		if(result == Result.SUCCESS && withdrawalInfo != null) {
			response.setPayloadData(withdrawalInfo.toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(result);
		}
		
		return response.build();
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public IWithdrawalManager getWithdrawalManager() {
		return withdrawalManager;
	}

	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}

}