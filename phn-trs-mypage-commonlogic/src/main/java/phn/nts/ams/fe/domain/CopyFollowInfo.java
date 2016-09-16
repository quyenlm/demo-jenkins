package phn.nts.ams.fe.domain;

import phn.com.nts.ams.web.condition.CopyTradeItemInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/5/13 4:54 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class CopyFollowInfo {
    private Integer followers;
    private Integer copiers;
    private BigDecimal investAmount;
    private BigDecimal equityPercentage;
    private String investAmountStr;
    private String equityPercentageStr;
    private String customerId;
    private Integer followFlg;
    private String ajaxMsg;
    private boolean ajaxSuccess;
    private boolean fullRender;
    private boolean showAccountBox;
    private String socialBaseCurrency;
    private String userName;
    private BigDecimal availableInvestAmount;

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getCopiers() {
        return copiers;
    }

    public void setCopiers(Integer copiers) {
        this.copiers = copiers;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public BigDecimal getEquityPercentage() {
        return equityPercentage;
    }

    public void setEquityPercentage(BigDecimal equityPercentage) {
        this.equityPercentage = equityPercentage;
    }

    public String getInvestAmountStr() {
        return investAmountStr;
    }

    public void setInvestAmountStr(String investAmountStr) {
        this.investAmountStr = investAmountStr;
    }

    public String getEquityPercentageStr() {
        return equityPercentageStr;
    }

    public void setEquityPercentageStr(String equityPercentageStr) {
        this.equityPercentageStr = equityPercentageStr;
    }

    public Integer getFollowFlg() {
        return followFlg;
    }

    public void setFollowFlg(Integer followFlg) {
        this.followFlg = followFlg;
    }

    public String getAjaxMsg() {
        return ajaxMsg;
    }

    public void setAjaxMsg(String ajaxMsg) {
        this.ajaxMsg = ajaxMsg;
    }

    public boolean isAjaxSuccess() {
        return ajaxSuccess;
    }

    public void setAjaxSuccess(boolean ajaxSuccess) {
        this.ajaxSuccess = ajaxSuccess;
    }

    public boolean isFullRender() {
        return fullRender;
    }

    public void setFullRender(boolean fullRender) {
        this.fullRender = fullRender;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isShowAccountBox() {
        return showAccountBox;
    }

    public void setShowAccountBox(boolean showAccountBox) {
        this.showAccountBox = showAccountBox;
    }

    public String getSocialBaseCurrency() {
        return socialBaseCurrency;
    }

    public void setSocialBaseCurrency(String socialBaseCurrency) {
        this.socialBaseCurrency = socialBaseCurrency;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getAvailableInvestAmount() {
        return availableInvestAmount;
    }

    public void setAvailableInvestAmount(BigDecimal availableInvestAmount) {
        this.availableInvestAmount = availableInvestAmount;
    }
}
