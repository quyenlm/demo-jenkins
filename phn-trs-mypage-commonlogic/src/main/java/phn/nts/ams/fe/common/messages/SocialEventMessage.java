package phn.nts.ams.fe.common.messages;

import java.io.Serializable;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 14/05/2014 10:18 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class SocialEventMessage implements Serializable {
    private String customerId;
    private String accountId;
    private String brokerCd;
    private Integer msgType;

    public SocialEventMessage(String customerId, Integer msgType) {
        this.customerId = customerId;
        this.msgType = msgType;
    }

    public SocialEventMessage(String accountId, String brokerCd, Integer msgType) {
        this.accountId = accountId;
        this.brokerCd = brokerCd;
        this.msgType = msgType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBrokerCd() {
        return brokerCd;
    }

    public void setBrokerCd(String brokerCd) {
        this.brokerCd = brokerCd;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
