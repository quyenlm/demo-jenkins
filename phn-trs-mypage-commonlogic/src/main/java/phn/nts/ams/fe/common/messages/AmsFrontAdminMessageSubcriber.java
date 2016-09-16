package phn.nts.ams.fe.common.messages;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.command.ActiveMQObjectMessage;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.webcore.SystemProperty;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.phn.bo.admin.message.AdminMessageImpl;
import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.MarginLevel;
import com.phn.mt.common.entity.UserRecord;

public class AmsFrontAdminMessageSubcriber {
	private static final Logit LOG = Logit.getInstance(AmsFrontAdminMessageSubcriber.class);
	private static volatile Cache<String, Object> mapObjects = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
	
	
	public void onMessage(Object message) {
		try {
			if(message != null) {
				UserRecord userRecord = null;
				MarginLevel marginLevel = null;
				FundRecord fundRecord = null;
				if(message instanceof MarginLevel){
					marginLevel = (MarginLevel)message;
					mapObjects.put(marginLevel.getSequenceID(), marginLevel);
                } else if(message instanceof UserRecord){
                    userRecord = (UserRecord)message;
                    mapObjects.put(userRecord.getSequenceID(), userRecord);
                    LOG.debug("PUT USER EDIT RESULT");
                } else if(message instanceof FundRecord){
                    fundRecord = (FundRecord)message;
                    mapObjects.put(fundRecord.getSequenceID(), fundRecord);
                } else if(message instanceof ActiveMQObjectMessage){
                	Object msg = ((ActiveMQObjectMessage) message).getObject();
                	if (msg instanceof AdminMessageImpl){
                    	AdminMessageImpl adminMessage = (AdminMessageImpl) msg;
                    	if(!StringUtil.isEmpty(adminMessage.getM_Id())){
                    		mapObjects.put(adminMessage.getM_Id(), adminMessage);
                    	}
                    }
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
