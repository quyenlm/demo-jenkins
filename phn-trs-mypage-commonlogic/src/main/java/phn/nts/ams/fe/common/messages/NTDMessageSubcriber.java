package phn.nts.ams.fe.common.messages;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jms.BytesMessage;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.nts.ams.utils.Converter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nts.common.Constant;
import com.nts.common.ProtoMsgConstant;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.BalanceInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.Status;
import com.nts.common.exchange.proto.ams.AmsCustomerService.AccountInfoResponse;
import com.nts.common.exchange.proto.ams.AmsCustomerService.BalanceInfoResponse;
import com.nts.common.exchange.proto.ams.AmsCustomerService.CustomerReportsResponse;
import com.nts.common.exchange.proto.ams.AmsCustomerService.RegisterCustomerResponse;
import com.nts.common.exchange.proto.ams.AmsCustomerService.UpdateBalanceResponse;
import com.nts.common.exchange.proto.ams.RpcAms;
import com.nts.common.exchange.proto.ams.RpcAms.RpcMessage;

/**
 * @description Subcribe message from NTD AMS Bridge
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 13, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class NTDMessageSubcriber {
	private static final Logit LOG = Logit.getInstance(NTDMessageSubcriber.class);
	private static volatile Cache<String, Object> mapObjects = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
	
	private byte[] data;
	private BytesMessage bytesMessage;
	private RpcMessage rpcResponse ;
	public void onMessage(Object message) {
		try {
			if(message != null) {
				if(message instanceof BytesMessage) {
				    bytesMessage = (BytesMessage) message;
				    data = new byte[(int) bytesMessage.getBodyLength()];
				    bytesMessage.readBytes(data);
				    rpcResponse = RpcAms.RpcMessage.parseFrom(data);
				    
				    LOG.info("Received Response from NTDAmsBridge:\n" + rpcResponse);
				    
				    if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsMsgType.BALANCE_INFO_RESPONSE)) {
				    	//BALANCE_INFO_RESPONSE
						BalanceInfoResponse ntdBalanceResponse = BalanceInfoResponse.parseFrom(rpcResponse.getPayloadData());
						BalanceInfo ntdBalanceInfo = ntdBalanceResponse.getBalanceInfo();
						LOG.info("Received BalanceInfoResponse from NTDAmsBridge: " + ntdBalanceInfo);
						
						phn.nts.ams.fe.domain.BalanceInfo balanceInfo = null;
						
						if(ntdBalanceInfo != null && ntdBalanceInfo.hasStatus() && ntdBalanceInfo.getStatus() == Status.SUCCESS) {
							balanceInfo = Converter.convertBalanceInfo(ntdBalanceInfo);
							LOG.info("Converted BalanceInfo: " + balanceInfo);
						} else {
							balanceInfo = new phn.nts.ams.fe.domain.BalanceInfo();
							balanceInfo.setResult(AccountBalanceResult.INTERNAL_ERROR);
						}
						
						if(balanceInfo != null) {
							mapObjects.put(rpcResponse.getId(), balanceInfo);
						}
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsMsgType.UPDATE_BALANCE_INFO_RESPONSE)) {
						//UPDATE_BALANCE_INFO_RESPONSE
						UpdateBalanceResponse ntdUpdateBalanceResponse = UpdateBalanceResponse.parseFrom(rpcResponse.getPayloadData());
						BalanceInfo ntdUpdateBalanceInfo = ntdUpdateBalanceResponse.getBalanceInfo();
						LOG.info("Received UpdateBalanceResponse from NTDAmsBridge: " + ntdUpdateBalanceInfo);
						
						phn.nts.ams.fe.domain.BalanceInfo balanceInfo = null;
						if(ntdUpdateBalanceInfo != null && ntdUpdateBalanceInfo.hasStatus() && ntdUpdateBalanceInfo.getStatus() == Status.SUCCESS) {
							balanceInfo = Converter.convertBalanceInfo(ntdUpdateBalanceInfo);
							LOG.info("Converted BalanceInfo: " + balanceInfo);
						}
						
						if(balanceInfo != null) {
							mapObjects.put(rpcResponse.getId(), Constant.RESULT_SUCCESS);
						} else
							mapObjects.put(rpcResponse.getId(), Constant.RESULT_FAIL);
					}  else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsMsgType.CUSTOMER_REPORTS_RESPONSE)) {
						//CUSTOMER_REPORTS_RESPONSE
						CustomerReportsResponse customerReportsResponse = CustomerReportsResponse.parseFrom(rpcResponse.getPayloadData());
						LOG.info("Received CustomerReportsResponse from NTDAmsBridge: " + customerReportsResponse);
						mapObjects.put(rpcResponse.getId(), customerReportsResponse);
					} else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsMsgType.ACCOUNT_INFO_RESPONSE)) {
						
						//ACCOUNT_INFO_RESPONSE
				    	AccountInfoResponse accountInfoResponse = AccountInfoResponse.parseFrom(rpcResponse.getPayloadData());
				    	LOG.info("Received AccountInfoResponse from NTDAmsBridge: " + accountInfoResponse);
				    	if(accountInfoResponse.getAccountCount() > 0 && accountInfoResponse.getAccount(0).hasStatus()
				    			&& Status.SUCCESS.equals(accountInfoResponse.getAccount(0).getStatus()))
				    		mapObjects.put(rpcResponse.getId(), Constant.RESULT_SUCCESS);
				    	else
				    		mapObjects.put(rpcResponse.getId(), Constant.RESULT_FAIL);
				    } else if(rpcResponse.getPayloadClass().equals(ProtoMsgConstant.AmsMsgType.REGISTER_CUSTOMER_RESPONSE)) {
						
						//ACCOUNT_INFO_RESPONSE
				    	RegisterCustomerResponse accountInfoResponse = RegisterCustomerResponse.parseFrom(rpcResponse.getPayloadData());
				    	LOG.info("Received RegisterCustomerResponse from NTDAmsBridge: " + accountInfoResponse);
				    	if(accountInfoResponse.hasCustomerInfo() && accountInfoResponse.getCustomerInfo().hasStatus()
				    			&& Status.SUCCESS.equals(accountInfoResponse.getCustomerInfo().getStatus()))
				    		mapObjects.put(rpcResponse.getId(), Constant.RESULT_SUCCESS);
				    	else
				    		mapObjects.put(rpcResponse.getId(), Constant.RESULT_FAIL);
				    } else
						LOG.info("Request is not NTDAmsBridge Type");
				} else {
					LOG.info("Request has invalid ProtoVersion: " + rpcResponse.getVersion());
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public static Object getJmsResult(String key) throws InterruptedException{
		Map<String, String> mapMt4Configuration = SystemProperty.getInstance().getMapByKey(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT4_CONFIGURATION);
		Integer counter = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.COUNT_INTERVAL));
		Integer interval = MathUtil.parseInteger(mapMt4Configuration.get(IConstants.MT4_CONFIGURATION.INTERVAL));
		
		Object obj = mapObjects.getIfPresent(key);
		int i = 0;
		while(obj == null){
			if(i >= counter) {
				LOG.error("Timed out waiting for response from NTDAmsBridge, requestId: " + key);
				break;
			}
			
			obj = mapObjects.getIfPresent(key);
			Thread.sleep(interval);
			i++;
		}
		
		mapObjects.invalidate(key);
		
		return obj;
	}
	
	public static void removeJmsResult(String key){
		mapObjects.invalidate(key);
	}
}