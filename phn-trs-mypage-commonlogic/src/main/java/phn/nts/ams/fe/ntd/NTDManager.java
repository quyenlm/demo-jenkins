package phn.nts.ams.fe.ntd;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.Result;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.messages.NTDMessageSubcriber;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.jms.IJmsSender;
import phn.nts.ams.utils.Converter;

import com.google.protobuf.ByteString;
import com.nts.common.Constant;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.AccountInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.CustomerInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.CustomerType;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.TradingCashflowType;
import com.nts.common.exchange.proto.ams.AmsCustomerService.AccountInfoRequest;
import com.nts.common.exchange.proto.ams.AmsCustomerService.BalanceInfoRequest;
import com.nts.common.exchange.proto.ams.AmsCustomerService.CustomerReportsRequest;
import com.nts.common.exchange.proto.ams.AmsCustomerService.CustomerReportsResponse;
import com.nts.common.exchange.proto.ams.AmsCustomerService.RegisterCustomerRequest;
import com.nts.common.exchange.proto.ams.AmsCustomerService.UpdateBalanceRequest;
import com.nts.common.exchange.proto.ams.RpcAms;
import com.nts.common.exchange.proto.ams.RpcAms.RpcMessage;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;

/**
 * @description NTDManager
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class NTDManager {
	private static Logit LOG = Logit.getInstance(NTDManager.class);
	private static NTDManager instance;
	private IJmsSender jmsRealSender;
	
	private String ntdRequestQueue;
	private String protoVersion;
	private String amsService;
	
	public static NTDManager getInstance() {
		if(instance == null) {
			instance = new NTDManager();
		}
		return instance;
	}
	
    private String getRandomSequenceId(){
        Object clusterServerId = FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + ITrsConstants.CONFIG_KEY.CLUSTER_SERVER_ID);
        return (clusterServerId == null ? "" : clusterServerId.toString()) + MathUtil.generateRandomPassword(32);
    }

	/**
	 * NTD getBalanceInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
    public BalanceInfo getBalanceInfo(String ntdAccountId) {    	
		BalanceInfo balanceInfo = null;
		
		try {
			String sequenceID =  getRandomSequenceId();
			BalanceInfoRequest request = createBalanceInfoRequest(ntdAccountId);
			jmsRealSender.sendByteMessageToQueue(ntdRequestQueue, createRpcMessage(sequenceID, ProtoMsgConstant.AmsMsgType.BALANCE_INFO_REQUEST, request.toByteString()));
			
			balanceInfo = (BalanceInfo) NTDMessageSubcriber.getJmsResult(sequenceID);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			balanceInfo.setResult(AccountBalanceResult.INTERNAL_ERROR);
		}
		
		if(balanceInfo == null) {
			balanceInfo =  new BalanceInfo();
			balanceInfo.setResult(AccountBalanceResult.TIME_OUT);
		}
		return balanceInfo;
	}
	
    /**
	 * Create BalanceInfo Request to NTD
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
    private BalanceInfoRequest createBalanceInfoRequest(String ntdAccountId) {
    	BalanceInfoRequest.Builder builder = BalanceInfoRequest.newBuilder();
    	builder.setNtdAccountId(ntdAccountId);
    	return builder.build();
    }
    
    /**
	 * NTD getBalanceInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
    public int updateBalance(String ntdAccountId, String amount, TradingCashflowType tranferType) {    	
		int result = Constant.RESULT_UNKNOWN;
		
		try {
			String sequenceID =  getRandomSequenceId();
			
			LOG.info("updateBalance to NTD, sequenceID: " + sequenceID + ", ntdAccountId: " + ntdAccountId + ", amount: " + amount + ", tranferType: " + tranferType);
			
			UpdateBalanceRequest.Builder requestBuilder = UpdateBalanceRequest.newBuilder();
			requestBuilder.setAmount(amount);
			requestBuilder.setCashFlowType(tranferType);
			requestBuilder.setNtdAccountId(ntdAccountId);
			
			UpdateBalanceRequest request = requestBuilder.build();
			
			//send to AMSBridge
			jmsRealSender.sendByteMessageToQueue(ntdRequestQueue, createRpcMessage(sequenceID, ProtoMsgConstant.AmsMsgType.UPDATE_BALANCE_INFO_REQUEST, request.toByteString()));
			
			//Received result AMSBridge
			Integer ntdResult = (Integer) NTDMessageSubcriber.getJmsResult(sequenceID);
			if(ntdResult == null)
				result = Constant.RESULT_UNKNOWN; //Unknown
			else if(ntdResult.intValue() == Constant.RESULT_SUCCESS)
				result = Constant.RESULT_SUCCESS;
			else
				result = Constant.RESULT_FAIL;
			
			LOG.info("updateBalance to NTD, sequenceID: " + sequenceID + ", result: " + result + " - " + Result.valueOf(result));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
    
    /**
	 * Update account　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Apr 14, 2016
	 * @MdDate
	 */
    public int updateAccountInfo(AccountInfo ntdAccount) {    	
		int result = Constant.RESULT_UNKNOWN;
		
		try {
			String sequenceID = MathUtil.generateRandomPassword(32);
			
			LOG.info("updateAccountInfo to NTD, sequenceID: " + sequenceID + ", ntdAccount: " + ntdAccount);
			
			AccountInfoRequest.Builder requestBuilder = AccountInfoRequest.newBuilder();
			requestBuilder.setCrudType(CustomerType.UPDATE);
			requestBuilder.addAccount(ntdAccount);
			
			AccountInfoRequest request = requestBuilder.build();
			
			//send to AMSBridge
			jmsRealSender.sendByteMessageToQueue(ntdRequestQueue, createRpcMessage(sequenceID, ProtoMsgConstant.AmsMsgType.ACCOUNT_INFO_REQUEST, request.toByteString()));
			
			//Received result AMSBridge
			Integer ntdResult = (Integer) NTDMessageSubcriber.getJmsResult(sequenceID);
			if(ntdResult == null)
				result = Constant.RESULT_UNKNOWN; //Unknown
			else if(ntdResult.intValue() == Constant.RESULT_SUCCESS)
				result = Constant.RESULT_SUCCESS;
			else
				result = Constant.RESULT_FAIL;
			
			LOG.info("updateAccountInfo to NTD, sequenceID: " + sequenceID + ", result: " + result + " - " + Result.valueOf(result));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
    
    /**
	 * Update customerInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Apr 19, 2016
	 * @MdDate
	 */
    public int updateCustomerInfo(CustomerInfo ntdCustomerInfo) {    	
		int result = Constant.RESULT_UNKNOWN;
		
		try {
			String sequenceID = MathUtil.generateRandomPassword(32);
			
			LOG.info("updateCustomerInfo to NTD, sequenceID: " + sequenceID + ", " + ntdCustomerInfo);
			RegisterCustomerRequest.Builder requestBuilder = RegisterCustomerRequest.newBuilder();
			requestBuilder.setCustomerInfo(ntdCustomerInfo);
			
			RegisterCustomerRequest request = requestBuilder.build();
			
			//send to AMSBridge
			jmsRealSender.sendByteMessageToQueue(ntdRequestQueue, createRpcMessage(sequenceID, ProtoMsgConstant.AmsMsgType.REGISTER_CUSTOMER_REQUEST, request.toByteString()));
			
			//Received result AMSBridge
			Integer ntdResult = (Integer) NTDMessageSubcriber.getJmsResult(sequenceID);
			if(ntdResult == null)
				result = Constant.RESULT_UNKNOWN; //Unknown
			else if(ntdResult.intValue() == Constant.RESULT_SUCCESS)
				result = Constant.RESULT_SUCCESS;
			else
				result = Constant.RESULT_FAIL;
			
			LOG.info("updateCustomerInfo to NTD, sequenceID: " + sequenceID + ", result: " + result + " - " + Result.valueOf(result));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
    
    public AmsCustomerReportsResponse getCustomerReports(String ntdAccountId, AmsCustomerReportsRequest amsCustomerReportsRequest) {    	
    	AmsCustomerReportsResponse amsCustomerReportsResponse = null;
		
		try {
			String sequenceID = getRandomSequenceId();
			CustomerReportsRequest request = Converter.convertCustomerReportsRequest(amsCustomerReportsRequest, Long.valueOf(ntdAccountId));
			jmsRealSender.sendByteMessageToQueue(ntdRequestQueue, createRpcMessage(sequenceID, ProtoMsgConstant.AmsMsgType.CUSTOMER_REPORTS_REQUEST, request.toByteString()));
			
			CustomerReportsResponse customerReportsResponse = (CustomerReportsResponse) NTDMessageSubcriber.getJmsResult(sequenceID);
			if(customerReportsResponse != null)
				amsCustomerReportsResponse = Converter.convertAmsCustomerReportsResponse(customerReportsResponse);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		return amsCustomerReportsResponse;
	}
    
    /**
	 * Create RpcMessage to  NTD　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
    private RpcMessage createRpcMessage(String id, String clazz, ByteString payloadData) {
    	RpcAms.RpcMessage.Builder rpcResponseBuilder = RpcAms.RpcMessage.newBuilder();
		rpcResponseBuilder.setId(id);
		rpcResponseBuilder.setVersion(protoVersion);
		rpcResponseBuilder.setService(amsService);
		
		rpcResponseBuilder.setPayloadClass(clazz);
		rpcResponseBuilder.setPayloadData(payloadData);
		return rpcResponseBuilder.build();
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

	public static void setInstance(NTDManager instance) {
		NTDManager.instance = instance;
	}

	public String getNtdRequestQueue() {
		return ntdRequestQueue;
	}

	public void setNtdRequestQueue(String ntdRequestQueue) {
		this.ntdRequestQueue = ntdRequestQueue;
	}

	public String getProtoVersion() {
		return protoVersion;
	}

	public void setProtoVersion(String protoVersion) {
		this.protoVersion = protoVersion;
	}

	public String getService() {
		return amsService;
	}

	public void setService(String amsService) {
		this.amsService = amsService;
	}
}