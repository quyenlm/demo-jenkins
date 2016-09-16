package phn.nts.ams.fe.common.messages;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.command.ActiveMQBytesMessage;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nts.common.exchange.proto.ams.internal.RpcAms;

public class AmsApiMessageSubcriber {
	private static final Logit LOG = Logit.getInstance(AmsApiMessageSubcriber.class);
	private static volatile Cache<String, Object> mapObjects = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
	
	
	public void onMessage(Object message) {
		try {
			if(message != null) {
				 if(message instanceof ActiveMQBytesMessage){
                	ActiveMQBytesMessage byteMessages = (ActiveMQBytesMessage) message;
					byte [] data = byteMessages.getContent().getData();
					RpcAms.RpcMessage rpcMessage = RpcAms.RpcMessage.parseFrom(data);
					LOG.info("receive message with id="+rpcMessage.getId());
            		mapObjects.put(rpcMessage.getId(), rpcMessage);
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
			if(i >= counter) break;
			
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
