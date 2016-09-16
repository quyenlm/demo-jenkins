package com.nts.ams.api.controller.transfer.processor;


import java.sql.Timestamp;

import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.util.common.IConstants.CORPORATION_TYPE;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants.AMS_WHITELABEL_CONFIG_KEY;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.domain.CustomerInfo;

import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsDepositRequestWraper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsDepositTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsDeposit Task
 * @version NTS
 * @author THINHPH
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsDepositTask implements Runnable {
	private Logit log = Logit.getInstance(AmsDepositTask.class);
	private AmsDepositRequestWraper wraper;
	private IDepositManager depositManager = null;
	private IAccountManager accountManager = null;
	
	public AmsDepositTask(AmsDepositRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsDepositRequest request = wraper.getRequest();
			log.info("[start] handle AmsDepositRequest, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			log.info(getDepositManager());
			
			RpcMessage.Builder response = registerDeposit(request.toBuilder(), wraper.getResponseBuilder());;
			
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response.build());
			
			log.info("[end] handle AmsDepositRequest, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsDepositProcessor().onComplete(wraper);
		}
	}

	public RpcMessage.Builder registerDeposit(AmsDepositRequest.Builder amsDepositRequest, RpcMessage.Builder rpcBuilder) {
		AmsDepositResponse.Builder amsDepositResponse = AmsDepositResponse.newBuilder();
		AmsDepositTransactionInfo.Builder amsDepositTransactionInfo = amsDepositRequest.getDepositInfo().toBuilder();
		
		try {
			String bankCode = amsDepositTransactionInfo.getBankCode();
			String amount = amsDepositTransactionInfo.getDepositAmount();
			Double amt;
			try {
				amount = amount.replaceAll(",", "");
				amt = Double.valueOf(amount);
			} catch (NumberFormatException e) {
				rpcBuilder.setMessageCode("MSG_TRS_NAF_0036");
				return resultFail(rpcBuilder);
			}
			if (bankCode == null || bankCode.trim() == "") {
				rpcBuilder.setMessageCode("MSG_NAB001");
				return resultFail(rpcBuilder);
			}
			AmsWhitelabelConfig amsWlConfig = depositManager.getAmsWhitelabelConfig(amsDepositTransactionInfo.getCurrencyCode()
					+ AMS_WHITELABEL_CONFIG_KEY.MIN_DEPOSIT_AMOUNT_TAIL, amsDepositTransactionInfo.getWlCode());
			Double min = 0.0;
			Double max = 0.0;
			if (amsWlConfig != null) {
				min = Double.parseDouble(amsWlConfig.getConfigValue());
			}
			if (amt < min) {
				log.info("Amout great than min limit " + min);
				rpcBuilder.setMessageCode("MSG_NAB024");
				return resultFail(rpcBuilder);
			}
			
			amsWlConfig = depositManager.getAmsWhitelabelConfig(amsDepositTransactionInfo.getCurrencyCode() + AMS_WHITELABEL_CONFIG_KEY.MAX_DEPOSIT_AMOUNT_TAIL,
					amsDepositTransactionInfo.getWlCode());
			if (amsWlConfig != null) {
				max = Double.parseDouble(amsWlConfig.getConfigValue());
			}
			if (amt > max) {
				log.info("Amout less than min limit " + max);
				rpcBuilder.setMessageCode("MSG_NAB023");
				return resultFail(rpcBuilder);
			}
			CustomerInfo customerInfo = accountManager.getCustomerInfo(amsDepositTransactionInfo.getCustomerId());
			if (customerInfo == null) {
				log.error("Account not active");
				rpcBuilder.setMessageCode("MSG_NAB091");
				return resultFail(rpcBuilder);
			}
			if (customerInfo.getAccountOpenStatus().intValue() == 0) {
				log.error("Account not active");
				rpcBuilder.setMessageCode("MSG_NAB091");
				return resultFail(rpcBuilder);
			}
			AmsDeposit amsDeposit = new AmsDeposit();
			AmsDepositRef amsDepositref = new AmsDepositRef();
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(amsDepositTransactionInfo.getCustomerId());
			amsDeposit.setAmsCustomer(amsCustomer);
			amsDeposit.setDepositType(amsDepositTransactionInfo.getDepositType().getNumber());
			amsDeposit.setServiceType(amsDepositTransactionInfo.getServiceType().getNumber());
			amsDeposit.setCurrencyCode(amsDepositTransactionInfo.getCurrencyCode());
			amsDeposit.setStatus(amsDepositTransactionInfo.getStatus().getNumber());
			amsDeposit.setDepositMethod(amsDepositTransactionInfo.getDepositMethod().getNumber());
			amsDeposit.setDepositGateway(amsDepositTransactionInfo.getDepositGateway());
			amsDeposit.setDepositAmount(amt);
			if(!StringUtil.isEmpty(amsDepositTransactionInfo.getDepositFee())){
				amsDeposit.setDepositFee(Double.valueOf(amsDepositTransactionInfo.getDepositFee()));
			}
			if(!StringUtil.isEmpty(amsDepositTransactionInfo.getRate())){
				amsDeposit.setRate(Double.valueOf(amsDepositTransactionInfo.getRate()));
			}
			amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
			amsDeposit.setDepositRoute(amsDepositTransactionInfo.getDepositRoute().getNumber());
			if(!StringUtil.isEmpty(amsDepositTransactionInfo.getEwalletType())){
				amsDepositref.setEwalletType(amsDepositTransactionInfo.getDepositMethod().getNumber());
			}
			
			if(!StringUtil.isEmpty(amsDepositTransactionInfo.getCountryId())){
				amsDepositref.setCountryId(Integer.valueOf(amsDepositTransactionInfo.getCountryId()));
			}
			
			if(CORPORATION_TYPE.INDIVIDUAL.equals(customerInfo.getCorporationType())){
				amsDepositref.setEwalletEmail(customerInfo.getMailMain());
			}else if(CORPORATION_TYPE.CORPORATION.equals(customerInfo.getCorporationType())){
				amsDepositref.setEwalletEmail(customerInfo.getCorpPicMailPc());
			}
			amsDepositref.setCountryId(customerInfo.getCountryId());
			amsDepositref.setInputDate(new Timestamp(System.currentTimeMillis()));
			// using instead bankcode
			amsDepositref.setCcNo(bankCode);
			String depositId = accountManager.saveBjpDeposit(amsDeposit, amsDepositref, amsDepositRequest.getDepositInfo());
			log.info("Register success with deposit key = " + depositId);
			if (depositId.isEmpty()) {
				log.error("Can not save to AMS_DEPOSIT");
				rpcBuilder.setMessageCode("MSG_NAB068");
				return resultFail(rpcBuilder);
			}
			
			amsDepositTransactionInfo.setDepositId(depositId);
			if(!StringUtil.isEmpty(amsDepositref.getGwRefId()))
				amsDepositTransactionInfo.setGwRefId(amsDepositref.getGwRefId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rpcBuilder.setMessageCode("MSG_NAB068");
			return resultFail(rpcBuilder);
		}
		amsDepositResponse.setDepositInfo(amsDepositTransactionInfo);
		rpcBuilder.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_RESPONSE);
		rpcBuilder.setPayloadData(amsDepositResponse.build().toByteString());
		rpcBuilder.setResult(Result.SUCCESS);
		
		log.info("Reponse - " + amsDepositTransactionInfo.build().toString());
		return rpcBuilder;
	}
	
	public RpcMessage.Builder resultFail(RpcMessage.Builder rpcBuilder){
		rpcBuilder.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_RESPONSE);
		rpcBuilder.setResult(Result.FAILED);
		return rpcBuilder;
	}
	
	public IDepositManager getDepositManager() {
		return depositManager;
	}

	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
}