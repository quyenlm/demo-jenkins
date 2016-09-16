package com.nts.ams.api.controller.transfer.processor;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;
import phn.com.trs.util.common.ITrsConstants.TRS_CONSTANT;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;

import com.nts.ams.api.controller.common.Constant;
import com.nts.ams.api.controller.service.AmsApiControllerMng;
import com.nts.ams.api.controller.transfer.bean.AmsDepositUpdateRequestWraper;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsBillingDepositResult;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsDepositTransactionInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsVeriTransDepositResult;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.DepositMethod;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositUpdateRequest;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionService.AmsDepositUpdateResponse;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.RpcAms.RpcMessage.Result;

/**
 * @description AmsDepositUpdate Task
 * @version NTS
 * @author THINHPH
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class AmsDepositUpdateTask extends Thread {
	private Logit log = Logit.getInstance(AmsDepositUpdateTask.class);
	private AmsDepositUpdateRequestWraper wraper;
	private IDepositManager depositManager = null;
	private IAccountManager accountManager = null;
//	private final String depositMethod = "ダイレクト入金";
	private String depositMethod = "";
	public AmsDepositUpdateTask(AmsDepositUpdateRequestWraper wraper) {
		this.wraper = wraper;
	}

	@Override
	public void run() {
		try {
			AmsDepositUpdateRequest request = wraper.getRequest();
			log.info("[start] handle AmsDepositUpdateTask, requestId: " + wraper.getResponseBuilder().getId() + ", " + request);
			
			RpcMessage.Builder response = handleDepositBjp(request.toBuilder(), wraper.getResponseBuilder());
			
			AmsApiControllerMng.getAmsTransactionInfoPublisher().publish(response.build());
			
			log.info("[end] handle AmsDepositUpdateTask, requestId: " + wraper.getResponseBuilder().getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			AmsApiControllerMng.getAmsDepositUpdateProcessor().onComplete(wraper);
		}
	}

	public RpcMessage.Builder resultFail(RpcMessage.Builder rpcBuilder){
		rpcBuilder.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_UPDATE_REPONSE);
		rpcBuilder.setResult(Result.FAILED);
		return rpcBuilder;
	}
	
	public RpcMessage.Builder createRpcMessage(AmsDepositTransactionInfo transactionInfo, Result result, String errCode) {
		RpcMessage.Builder response = wraper.getResponseBuilder();
		response.setPayloadClass(ProtoMsgConstant.AmsInternalMsgType.AMS_DEPOSIT_UPDATE_REPONSE);
		if(errCode != null)
			response.setMessageCode(errCode);
		
		if(result == Result.SUCCESS && transactionInfo != null) {
			AmsDepositUpdateResponse.Builder depositUpdate = AmsDepositUpdateResponse.newBuilder();
			depositUpdate.setDepositInfo(transactionInfo);
			response.setPayloadData(depositUpdate.build().toByteString());
			response.setResult(Result.SUCCESS);
		} else {
			response.setResult(result);
		}
		
		return response;
	}
	
	public StringBuilder buildCertificationKey(AmsBillingDepositResult amsDirectDepositResult){
		return new StringBuilder()
		.append(amsDirectDepositResult.getPortalCode())
		.append(amsDirectDepositResult.getShopCode())
		.append(amsDirectDepositResult.getBankCode())
		.append(amsDirectDepositResult.getKessaiFlag())
		.append(amsDirectDepositResult.getCtrlNo())
		.append(amsDirectDepositResult.getTranStat())
		.append(amsDirectDepositResult.getTranReasonCode())
		.append(amsDirectDepositResult.getTranResultMsg())
		.append(amsDirectDepositResult.getTranDate())
		.append(amsDirectDepositResult.getTranTime())
		.append(amsDirectDepositResult.getCustName())
		.append(amsDirectDepositResult.getCustLname())
		.append(amsDirectDepositResult.getCustFname())
		.append(amsDirectDepositResult.getTranAmount())
		.append(amsDirectDepositResult.getTranFee())
		.append(amsDirectDepositResult.getPaymentDay())
		.append(amsDirectDepositResult.getGoodsName())
		.append(amsDirectDepositResult.getRemarks1())
		.append(amsDirectDepositResult.getRemarks2())
		.append(amsDirectDepositResult.getRemarks3())
		.append(amsDirectDepositResult.getTranId());
	}

	/**
	 * err-fe.properties
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author THINHPH
	 * @CrDate May 23, 2013
	 */
	public RpcMessage.Builder handleDepositBjp(AmsDepositUpdateRequest.Builder amsDepositUpdateRequest,  RpcMessage.Builder rpcBuilder) {
		AmsBillingDepositResult.Builder amsDirectDepositResult = amsDepositUpdateRequest.getPaymentResult().getBillingDepositResult().toBuilder();
		AmsDepositTransactionInfo.Builder amsDepositTransactionInfo = amsDepositUpdateRequest.getDepositInfoBuilder();
		depositMethod = SystemPropertyConfig.getInstance().getText(Constant.BJP_DEPOSIT);
		try {
			AmsDeposit amsDeposit = depositManager.getBjpDeposit(amsDepositTransactionInfo.getDepositId());
					
			//Check exist amsDeposit
			if (amsDeposit == null) {
				log.warn("Not found AmsDepositId: " + amsDepositTransactionInfo.getDepositId());
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
				return resultFail(rpcBuilder);
			} 
			
			if (StringUtil.isEmpty(amsDepositTransactionInfo.getCustomerId()) || amsDeposit.getAmsCustomer() == null || !amsDepositTransactionInfo.getCustomerId().equals(amsDeposit.getAmsCustomer().getCustomerId())) {
				log.warn("CustomerId: " + amsDepositTransactionInfo.getCustomerId() + " NOT have AmsDepositId: " + amsDepositTransactionInfo.getDepositId());
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
				return resultFail(rpcBuilder);
			} 
			
			//Check status of amsDeposit, if amsDepositStatus is INPROGRESS then handle
			if(amsDeposit.getStatus() != null && amsDeposit.getStatus().intValue() != ITrsConstants.BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS){
				log.warn("Error in bjp  deposit reponse with depositIdstatus " + amsDeposit.getStatus());
				Integer receivedDepositStatus =  getReceivedDepositStatusWhenBjpReturnSuccess( amsDirectDepositResult.getTranStat(), 
						amsDirectDepositResult.getTranReasonCode(), amsDirectDepositResult.getMeigiStat(), amsDirectDepositResult.getBankCode());
				depositManager.sendMailAbnormalUpdateDepositStatus(amsDepositTransactionInfo.getCustomerId(), amsDepositTransactionInfo.getCustomerName(), 
						amsDepositTransactionInfo.getDepositId(), getDepositStatusText(amsDeposit.getStatus()), getDepositStatusText(receivedDepositStatus));
				rpcBuilder.setMessageCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				return resultFail(rpcBuilder);
			}
			
			//Check customerId
			if (!amsDeposit.getAmsCustomer().getCustomerId().equalsIgnoreCase(amsDepositTransactionInfo.getCustomerId())) {
				log.warn("Error in bjp  deposit customer not exist ");
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
				return resultFail(rpcBuilder);
			}
			
			//Start handle
			if(DepositMethod.BILLING_PAYMENT == amsDepositTransactionInfo.getDepositMethod())
				return handleBillingPayment(amsDepositUpdateRequest, rpcBuilder, amsDeposit); //Handle BILLING_PAYMENT
			else if(DepositMethod.VERITRANS_PAYMENT == amsDepositTransactionInfo.getDepositMethod())
				return handleVeriTransPayment(amsDepositUpdateRequest, rpcBuilder, amsDeposit); //Handle VERITRANS_PAYMENT
			else {
				log.warn("Not support DepositMethod: " + amsDepositTransactionInfo.getDepositMethod());
				return resultFail(rpcBuilder);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rpcBuilder.setMessageCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			return resultFail(rpcBuilder);
		}
	}

	/**
	 * Handle BillingPayment result　
	 * 
	 * @param
	 * @return
	 * @author THINHPH
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
	private RpcMessage.Builder handleBillingPayment(AmsDepositUpdateRequest.Builder amsDepositUpdateRequest,  RpcMessage.Builder rpcBuilder, AmsDeposit amsDeposit) throws Exception {
		AmsBillingDepositResult amsDirectDepositResult = amsDepositUpdateRequest.getPaymentResult().getBillingDepositResult();
		AmsDepositTransactionInfo.Builder amsDepositTransactionInfo = amsDepositUpdateRequest.getDepositInfoBuilder();
		
		//Check Certificate
		if(!checkCertificate(amsDepositUpdateRequest, rpcBuilder, amsDeposit)) {
			log.warn("Certificate is not valid");
			return resultFail(rpcBuilder);
		}
		
		//Process
		Double amount = MathUtil.parseDouble(amsDirectDepositResult.getTranAmount(), 0D);
		Double fee = MathUtil.parseDouble(amsDirectDepositResult.getTranFee(), 0D);
		if(amount <= 0) {
			log.warn("Amount is not valid: " + amount);
			rpcBuilder.setMessageCode("MSG_NAB210");
			return resultFail(rpcBuilder);
		}
		
		int step = 0;
		if (BJP_CONFIG.DEPOSIT_TRAN_STAT_SUCCESS.equalsIgnoreCase(amsDirectDepositResult.getTranStat()) 
				&& BJP_CONFIG.TRAN_REASON_CODE_00.equalsIgnoreCase(amsDirectDepositResult.getTranReasonCode())) {
			if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equals(amsDirectDepositResult.getMeigiStat())) {
				// run step sucess
				step = 1;
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equals(amsDirectDepositResult.getMeigiStat())) {
				// update equal 2 and show msg fail
				step = 2;
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equals(amsDirectDepositResult.getMeigiStat())) {
				if ("0033".equals(amsDepositTransactionInfo.getBankCode()) 
						|| "0036".equals(amsDepositTransactionInfo.getBankCode()) 
						|| "0038".equals(amsDepositTransactionInfo.getBankCode()) 
						|| "0003".equals(amsDepositTransactionInfo.getBankCode())) {
					rpcBuilder.setMessageCode(ITrsConstants.MSG_TRS_NAF_0002);
					return resultFail(rpcBuilder);
				} else {
					// run step sucess
					step = 1;
				}
			}
		} else {
			// fail
			step = 3;
		}

		if (step == 3) {
			//d).   When Direct  Deposit is FAIL (TRAN_STAT = 2)
			if (BJP_CONFIG.TRAN_REASON_CODE_15.equals(amsDirectDepositResult.getTranReasonCode()) 
					|| BJP_CONFIG.TRAN_REASON_CODE_16.equals(amsDirectDepositResult.getTranReasonCode()) 
					|| BJP_CONFIG.TRAN_REASON_CODE_21.equals(amsDirectDepositResult.getTranReasonCode())
					|| BJP_CONFIG.TRAN_REASON_CODE_29.equals(amsDirectDepositResult.getTranReasonCode()) 
					|| BJP_CONFIG.TRAN_REASON_CODE_32.equals(amsDirectDepositResult.getTranReasonCode())
					|| BJP_CONFIG.TRAN_REASON_CODE_39.equals(amsDirectDepositResult.getTranReasonCode()) 
					|| BJP_CONFIG.TRAN_REASON_CODE_83.equals(amsDirectDepositResult.getTranReasonCode())
					|| BJP_CONFIG.TRAN_REASON_CODE_92.equals(amsDirectDepositResult.getTranReasonCode()) 
					|| BJP_CONFIG.TRAN_REASON_CODE_93.equals(amsDirectDepositResult.getTranReasonCode())
					|| BJP_CONFIG.TRAN_REASON_CODE_99.equals(amsDirectDepositResult.getTranReasonCode())) {
				depositManager.updateBjpDepositFail(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_CANCEL, amsDirectDepositResult.getTranReasonCode());
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB068);
			} else {
				depositManager.updateBjpDepositFail(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, amsDirectDepositResult.getTranReasonCode());
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
			}
			
			if (BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS == amsDeposit.getStatus().intValue()) {
				// (Inprogress)Display alert message: MSG_TRS_NAF_0002
				rpcBuilder.setMessageCode(ITrsConstants.MSG_TRS_NAF_0002);
				return resultFail(rpcBuilder);
			} else if (BJP_CONFIG.DEPOSIT_STATUS_SUCCESS == amsDeposit.getStatus().intValue()) {
				// If AMS_DEPOSIT.STATUS is 1 (Finish) Display alert
				// message : MSG_NAB067
				try {
					depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, depositMethod);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB067);
				return resultFail(rpcBuilder);
			} else if (BJP_CONFIG.DEPOSIT_STATUS_FAIL == amsDeposit.getStatus().intValue()) {
				// 2 fail
				try {
					depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, depositMethod);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
				return resultFail(rpcBuilder);
			} else if (BJP_CONFIG.DEPOSIT_STATUS_CANCEL == amsDeposit.getStatus().intValue()) {
				// 7 cancel
				try {
					depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_CANCEL, depositMethod);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB068);
				return resultFail(rpcBuilder);
			}
			
			return resultFail(rpcBuilder);
		} else if (step == 1) {
			//c).  When Direct  Deposit is SUCCESS update cash flow, cash balance for customer																					

			AmsCashflow cashflow = new AmsCashflow();
			AmsCustomer cus = new AmsCustomer();
			AmsDepositTransactionInfo.Builder transactionInfo = AmsDepositTransactionInfo.newBuilder();
			cus.setCustomerId(amsDepositTransactionInfo.getCustomerId());
			cashflow.setAmsCustomer(cus);
			cashflow.setCashflowType(1);
			cashflow.setCashflowAmount(amount);
			cashflow.setCurrencyCode(amsDeposit.getCurrencyCode());
			cashflow.setRate(1d);
			cashflow.setSourceType(1);
			if (amsDepositTransactionInfo.getDepositId().isEmpty()) {
				amsDepositTransactionInfo.setDepositId(amsDirectDepositResult.getRemarks3());
			}
			cashflow.setSourceId(amsDepositTransactionInfo.getDepositId());
			cashflow.setActiveFlg(1);
			cashflow.setServiceType(0);
			depositManager.insertBjpCashFlow(cashflow);
			depositManager.updateBjpCashBalance(amsDepositTransactionInfo.getCustomerId(), amsDepositTransactionInfo.getCurrencyCode(), IConstants.SERVICES_TYPE.AMS, amount, fee);
			depositManager.updateBjpDeposit(amsDepositTransactionInfo.getDepositId(), amsDirectDepositResult.getTranReasonCode(), Math.abs(fee));
			depositManager.updateBjpDepositRef(amsDepositTransactionInfo.getDepositId(), amsDirectDepositResult.getTranId(), null);
			depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, depositMethod);
			
			rpcBuilder.setMessageCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
			transactionInfo.setDepositId(amsDepositTransactionInfo.getDepositId());
			transactionInfo.setCustomerId(amsDepositTransactionInfo.getCustomerId());
			return createRpcMessage(transactionInfo.build(), Result.SUCCESS, IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
		} else if (step == 2) {
			depositManager.updateBjpDepositFail(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, amsDirectDepositResult.getTranReasonCode());
			try {
				depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, depositMethod);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
			return resultFail(rpcBuilder);
		}
		
		return resultFail(rpcBuilder);
	}
	
	private boolean checkCertificate(AmsDepositUpdateRequest.Builder amsDepositUpdateRequest,  RpcMessage.Builder rpcBuilder, AmsDeposit amsDeposit) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		AmsBillingDepositResult amsDirectDepositResult = amsDepositUpdateRequest.getPaymentResult().getBillingDepositResult();
		
		String BJP_KEY = accountManager.getBjpCertificationKey(TRS_CONSTANT.TRS_WL_CODE);
		StringBuilder key = buildCertificationKey(amsDirectDepositResult);
		key.append(BJP_KEY);

		log.info("BJP key=" + key.toString());
		String certificationKey = makeMessageDigest(key.toString());
		log.info("BJP keymd5=" + certificationKey);
		
		boolean flag = false;
		
		if (!certificationKey.equals(amsDirectDepositResult.getTranDigest())) {
			log.error("TRAN_DIGEST=" + amsDirectDepositResult.getTranDigest() + " not equal KEY CERTIFICATE=" + certificationKey);				
			if (amsDeposit == null) {
				rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
			} else {
				if (6 == amsDeposit.getStatus().intValue()) {
					rpcBuilder.setMessageCode(ITrsConstants.MSG_TRS_NAF_0002);
				} else if (1 == amsDeposit.getStatus().intValue()) {
					rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB067);
				} else if (2 == amsDeposit.getStatus().intValue()) {
					rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB068);
				} else if (7 == amsDeposit.getStatus().intValue()) {
					rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
				}
			}
			rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
		} else
			flag = true;
		
		return flag;
	}
	
	/**
	 * Handle VeriTrans Payment result　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Aug 11, 2015
	 * @MdDate
	 */
	private RpcMessage.Builder handleVeriTransPayment(AmsDepositUpdateRequest.Builder amsDepositUpdateRequest,  RpcMessage.Builder rpcBuilder, AmsDeposit amsDeposit) throws Exception {
		AmsVeriTransDepositResult amsVeriTransDepositResult = amsDepositUpdateRequest.getPaymentResult().getVeritransDepositResult();
		
		String seikyuNo = amsVeriTransDepositResult.getSeikyuNo();
		AmsDepositRef amsDepositRef = depositManager.getDepositRefById(amsDepositUpdateRequest.getDepositInfo().getDepositId());
		
		//Check valid seikyuNo
		if(amsDepositRef == null) {
			log.warn("Not found AmsDepositRef with DepositId: " + seikyuNo);
			rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
			return resultFail(rpcBuilder);
		}
		
		if(StringUtil.isEmpty(seikyuNo) || !seikyuNo.equals(amsDepositRef.getGwRefId())) {
			// Fail  for not found deposit Id
			log.warn(String.format("SeikyuNo: %s not match GwRefId: %s ", seikyuNo, amsDepositRef.getGwRefId()));
			
			depositManager.updateBjpDepositFail(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, "");
			try {
				depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, depositMethod);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			rpcBuilder.setMessageCode(ITrsConstants.MSG_NAB073);
			return resultFail(rpcBuilder);
		}
		
		//In case Success
		Double amout = MathUtil.parseDouble(amsVeriTransDepositResult.getTotal(), 0D);
		Double fee = MathUtil.parseDouble(amsVeriTransDepositResult.getInshizei(), 0D);
		if(amout <= 0 || !amout.equals(amsDeposit.getDepositAmount())) {
			//Check amount
			log.warn("Amount is not valid: " + amout);
			
			//[TRSM1-2417-quyen.le.manh]Feb 2, 2016A - Start update status in AMS_DEPOSIT in case result is Fail (Amount not match)
			depositManager.updateBjpDepositFail(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, "Amount invalid");
			try {
				depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_FAIL, depositMethod);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			//[TRSM1-2417-quyen.le.manh]Feb 2, 2016A - End
			
			rpcBuilder.setMessageCode("MSG_NAB210");
			return resultFail(rpcBuilder);
		}
		
		AmsDepositTransactionInfo.Builder amsDepositTransactionInfo = amsDepositUpdateRequest.getDepositInfoBuilder();
		AmsCustomer cus = new AmsCustomer();
		cus.setCustomerId(amsDepositTransactionInfo.getCustomerId());
		
		//Insert AMS_CASHFLOW
		AmsCashflow cashflow = new AmsCashflow();
		cashflow.setAmsCustomer(cus);
		cashflow.setCashflowType(1); // (Withdraw/deposit)
		cashflow.setCashflowAmount(amout);
		cashflow.setCurrencyCode(amsDeposit.getCurrencyCode());
		cashflow.setRate(1d);
		cashflow.setSourceType(1); // (DEPOSIT_ID)
		
		if (amsDepositTransactionInfo.getDepositId().isEmpty()) {
			amsDepositTransactionInfo.setDepositId(amsDepositRef.getDepositId());
		}
		
		cashflow.setSourceId(amsDepositTransactionInfo.getDepositId());
		cashflow.setActiveFlg(1);
		cashflow.setServiceType(0);
		depositManager.insertBjpCashFlow(cashflow);
		
		//UPDATE TABLE: AMS_CASH_BALANCE
		depositManager.updateBjpCashBalance(amsDepositTransactionInfo.getCustomerId(), amsDepositTransactionInfo.getCurrencyCode(), IConstants.SERVICES_TYPE.AMS, amout, fee);
		
		//UPDATE TABLE: AMS_DEPOSIT
		depositManager.updateBjpDeposit(amsDepositTransactionInfo.getDepositId(), null, Math.abs(fee));
		
		//2.3.  Send mail to customer
		depositManager.sendMailBjpDeposit(amsDeposit, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, depositMethod);
		
		//2.2. Create object AmsDepositUpdateResponse
		rpcBuilder.setMessageCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
		AmsDepositTransactionInfo.Builder transactionInfo = AmsDepositTransactionInfo.newBuilder();
		transactionInfo.setDepositId(amsDepositTransactionInfo.getDepositId());
		transactionInfo.setCustomerId(amsDepositTransactionInfo.getCustomerId());
		
		return createRpcMessage(transactionInfo.build(), Result.SUCCESS, IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
	}
	
	/**
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */
	private String makeMessageDigest(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		byte[] bytes;
		bytes = data.getBytes("Shift_Jis");
		messagedigest.update(bytes);
		bytes = messagedigest.digest();
		String HexVal = "";
		String messageDigest = "";
		for (int i = 0; i < bytes.length; i++) {
			HexVal = Integer.toHexString(0xff & bytes[i]);
			if (HexVal.length() == 1) {
				messageDigest += "0";
				messageDigest += HexVal;
			} else {
				messageDigest += HexVal;
			}
		}

		return messageDigest;
	}
	
	private Integer getReceivedDepositStatusWhenBjpReturnSuccess(
			String tranStart, String tranReasonCode, String meigiStart,
			String bankCoode) {
		if (BJP_CONFIG.DEPOSIT_TRAN_STAT_SUCCESS.equalsIgnoreCase(tranStart)
				&& BJP_CONFIG.TRAN_REASON_CODE_00.equalsIgnoreCase(tranReasonCode)) {
			if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equals(meigiStart)) {
				return BJP_CONFIG.DEPOSIT_STATUS_SUCCESS;
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equals(meigiStart)) {
				return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equals(meigiStart)) {
				if ("0033".equals(bankCoode) 
						|| "0036".equals(bankCoode)
						|| "0038".equals(bankCoode) 
						|| "0003".equals(bankCoode)) {
					return BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS;
				} else {
					// run step success
					return BJP_CONFIG.DEPOSIT_STATUS_SUCCESS;
				}
			}
		} else {
			if (BJP_CONFIG.TRAN_REASON_CODE_15.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_16.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_21.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_29.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_32.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_39.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_83.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_92.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_93.equals(tranReasonCode)
					|| BJP_CONFIG.TRAN_REASON_CODE_99.equals(tranReasonCode)) {
				return BJP_CONFIG.DEPOSIT_STATUS_CANCEL;
			} else {
				return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
			}
		}
		return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
	}
	
	private String getDepositStatusText(Integer status) {
		return "nts.ams.fe.label.deposit.bjp.status."+status;
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