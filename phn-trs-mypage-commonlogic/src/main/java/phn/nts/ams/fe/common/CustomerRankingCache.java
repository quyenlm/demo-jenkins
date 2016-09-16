package phn.nts.ams.fe.common;

import phn.com.nts.db.dao.IScSummaryTradingCustDAO;
import phn.com.nts.db.domain.CustomerFollowCopyInfo;
import phn.com.nts.db.entity.ScSummaryTradingCust;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.messages.SocialEventMessage;
import phn.nts.ams.fe.jms.IJmsSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 13/05/2014 10:50 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class CustomerRankingCache {
    private Logit logger = Logit.getInstance(CustomerRankingCache.class);
    private Map<String, Integer> followerData = new HashMap<String, Integer>();
    private Map<String, Integer> copierData = new HashMap<String, Integer>();
    private IScSummaryTradingCustDAO<ScSummaryTradingCust> scSummaryTradingCustDAO;
    private static CustomerRankingCache instance;
    interface MESSAGE_TYPE{
        final Integer INCREASE_FOLLOWER = 1;
        final Integer DECREASE_FOLLOWER = 2;
        final Integer INCREASE_COPIER = 3;
        final Integer DECREASE_COPIER = 4;
    }

    public static CustomerRankingCache getInstance(){
        return instance;
    }

    public static void ensureInitialized(CustomerRankingCache rankingCache){
        if(instance == null){
            instance = rankingCache;
            instance.loadAllData();
        }
    }

    public void onMessage(Object message){
        if(message instanceof HashMap){
            HashMap mapMessage = (HashMap)message;
            try {
                Integer msgType = (Integer)mapMessage.get("msgType");
                String customerId = mapMessage.get("customerId") == null ? null : mapMessage.get("customerId").toString();
                String accountId = mapMessage.get("accountId") == null ? null : mapMessage.get("accountId").toString();
                String brokerCd = mapMessage.get("brokerCd") == null ? null : mapMessage.get("brokerCd").toString();
                if(MESSAGE_TYPE.INCREASE_FOLLOWER.equals(msgType)){
                    increaseFollowerNo(customerId);
                } else if(MESSAGE_TYPE.DECREASE_FOLLOWER.equals(msgType)){
                    decreaseFollowerNo(customerId);
                } else if(MESSAGE_TYPE.INCREASE_COPIER.equals(msgType)){
                    increaseCopierNo(accountId, brokerCd);
                } else if(MESSAGE_TYPE.DECREASE_COPIER.equals(msgType)){
                    decreaseCopierNo(accountId, brokerCd);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    private void loadAllData() {
        List<CustomerFollowCopyInfo> data = scSummaryTradingCustDAO.getCustomerFollowCopyData();
        for(CustomerFollowCopyInfo item : data){
            if(item.getFollowCustomerId() != null){
                followerData.put(item.getFollowCustomerId(), item.getFollowerCopierNo());
            } else {
                copierData.put(getCombinedKey(item.getCopyAccountId(), item.getCopyBrokerCd()), item.getFollowerCopierNo());
            }
        }
    }

    public CustomerRankingCache(IScSummaryTradingCustDAO<ScSummaryTradingCust> scSummaryTradingCustDAO){
        this.scSummaryTradingCustDAO = scSummaryTradingCustDAO;
    }

    public void increaseFollowerNo(IJmsSender jmsSender, String customerId){
        SocialEventMessage message = new SocialEventMessage(customerId, MESSAGE_TYPE.INCREASE_FOLLOWER);
        jmsSender.sendMapMessage(ITrsConstants.ACTIVEMQ.TOPIC_AMS_FRONT_NOTIFY_CACHE_EVENTS, message);
    }

    public void decreaseFollowerNo(IJmsSender jmsSender, String customerId){
        SocialEventMessage message = new SocialEventMessage(customerId, MESSAGE_TYPE.DECREASE_FOLLOWER);
        jmsSender.sendMapMessage(ITrsConstants.ACTIVEMQ.TOPIC_AMS_FRONT_NOTIFY_CACHE_EVENTS, message);
    }

    public void increaseCopierNo(IJmsSender jmsSender, String accountId, String brokerCd){
        SocialEventMessage message = new SocialEventMessage(accountId, brokerCd, MESSAGE_TYPE.INCREASE_COPIER);
        jmsSender.sendMapMessage(ITrsConstants.ACTIVEMQ.TOPIC_AMS_FRONT_NOTIFY_CACHE_EVENTS, message);
    }

    public void decreaseCopierNo(IJmsSender jmsSender, String accountId, String brokerCd){
        SocialEventMessage message = new SocialEventMessage(accountId, brokerCd, MESSAGE_TYPE.DECREASE_COPIER);
        jmsSender.sendMapMessage(ITrsConstants.ACTIVEMQ.TOPIC_AMS_FRONT_NOTIFY_CACHE_EVENTS, message);
    }

    private synchronized void increaseFollowerNo(String customerId){
        if(followerData.containsKey(customerId)){
            Integer followerNo = followerData.get(customerId);
            followerData.put(customerId, followerNo == null ? 1 : followerNo + 1);
        } else {
            followerData.put(customerId, 1);
        }
    }

    private synchronized void decreaseFollowerNo(String customerId){
        if(followerData.containsKey(customerId)){
            Integer followerNo = followerData.get(customerId);
            followerData.put(customerId, followerNo == null ? 1 : (followerNo == null || followerNo <= 0) ? 0 : followerNo - 1);
        }
    }

    private synchronized void increaseCopierNo(String accountId, String brokerCd){
        if(copierData.containsKey(getCombinedKey(accountId, brokerCd))){
            Integer copierNo = copierData.get(getCombinedKey(accountId, brokerCd));
            copierData.put(getCombinedKey(accountId, brokerCd), copierNo == null ? 1 : copierNo + 1);
        } else {
            copierData.put(getCombinedKey(accountId, brokerCd), 1);
        }
    }

    private synchronized void decreaseCopierNo(String accountId, String brokerCd){
        if(copierData.containsKey(getCombinedKey(accountId, brokerCd))){
            Integer copierNo = copierData.get(getCombinedKey(accountId, brokerCd));
            copierData.put(getCombinedKey(accountId, brokerCd), (copierNo == null || copierNo <= 0) ? 0 : copierNo - 1);
        }
    }

    public Integer getFollowerNo(String customerId){
        if(followerData.containsKey(customerId)){
            Integer followerNo = followerData.get(customerId);
            return followerNo;
        }
        return 0;
    }

    public Integer getCopierNo(String accountId, String brokerCd){
        if(copierData.containsKey(getCombinedKey(accountId, brokerCd))){
            Integer copierNo = copierData.get(getCombinedKey(accountId, brokerCd));
            return copierNo;
        }
        return 0;
    }

    private String getCombinedKey(String accountId, String brokerCd){
        return accountId + "_" + brokerCd;
    }
}
