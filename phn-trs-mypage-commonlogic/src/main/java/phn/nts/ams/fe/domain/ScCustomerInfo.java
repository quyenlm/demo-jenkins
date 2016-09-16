package phn.nts.ams.fe.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 15/11/2013 10:07 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScCustomerInfo {
    private String customerId;
    private String userName;
    private String description;
    private Integer copierNo;
    private Integer followerNo;
    private Integer userType;
    private Integer sendMessageFlg;
    private Integer writeMyBoardFlg;
    private Integer notificationFlg;
    private BigDecimal signalTotalReturn;
    private BigDecimal signalTotalPips;
    private BigDecimal signalTotalTrade;
    private BigDecimal signalWinRatio;
    private Integer activeFlg;
    private Timestamp inputDate;
    private Timestamp updateDate;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCopierNo() {
        return copierNo;
    }

    public void setCopierNo(Integer copierNo) {
        this.copierNo = copierNo;
    }

    public Integer getFollowerNo() {
        return followerNo;
    }

    public void setFollowerNo(Integer followerNo) {
        this.followerNo = followerNo;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSendMessageFlg() {
        return sendMessageFlg;
    }

    public void setSendMessageFlg(Integer sendMessageFlg) {
        this.sendMessageFlg = sendMessageFlg;
    }

    public Integer getWriteMyBoardFlg() {
        return writeMyBoardFlg;
    }

    public void setWriteMyBoardFlg(Integer writeMyBoardFlg) {
        this.writeMyBoardFlg = writeMyBoardFlg;
    }

    public Integer getNotificationFlg() {
        return notificationFlg;
    }

    public void setNotificationFlg(Integer notificationFlg) {
        this.notificationFlg = notificationFlg;
    }

    public BigDecimal getSignalTotalReturn() {
        return signalTotalReturn;
    }

    public void setSignalTotalReturn(BigDecimal signalTotalReturn) {
        this.signalTotalReturn = signalTotalReturn;
    }

    public BigDecimal getSignalTotalPips() {
        return signalTotalPips;
    }

    public void setSignalTotalPips(BigDecimal signalTotalPips) {
        this.signalTotalPips = signalTotalPips;
    }

    public BigDecimal getSignalTotalTrade() {
        return signalTotalTrade;
    }

    public void setSignalTotalTrade(BigDecimal signalTotalTrade) {
        this.signalTotalTrade = signalTotalTrade;
    }

    public BigDecimal getSignalWinRatio() {
        return signalWinRatio;
    }

    public void setSignalWinRatio(BigDecimal signalWinRatio) {
        this.signalWinRatio = signalWinRatio;
    }

    public Integer getActiveFlg() {
        return activeFlg;
    }

    public void setActiveFlg(Integer activeFlg) {
        this.activeFlg = activeFlg;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }
}
