package phn.nts.ams.fe.domain;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 15/11/2013 10:06 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScSummaryTradingCustIdInfo {
    private String frontDate;
    private String accountId;
    private String brokerCd;

    public String getFrontDate() {
        return frontDate;
    }

    public void setFrontDate(String frontDate) {
        this.frontDate = frontDate;
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
}
