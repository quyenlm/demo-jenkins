package phn.nts.trader.fe.model;

import phn.com.nts.ams.web.condition.InviteCustomerSearchCondition;
import phn.com.nts.ams.web.condition.InviteKickbackHistoryCondition;
import phn.com.nts.db.domain.InviteCustomerInfo;
import phn.com.nts.db.domain.InviteKickbackHistoryInfo;
import phn.nts.ams.fe.model.BaseSocialModel;
import phn.nts.trader.fe.domain.IBInfo;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/11/13 9:31 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class IBModel extends BaseSocialModel {

    private IBInfo ibInfo;
    private InviteCustomerSearchCondition customerSearchCondition;
    private InviteKickbackHistoryCondition kickbackHistoryCondition;
    private List<InviteCustomerInfo> listInviteCustomerDetails;
    private List<InviteKickbackHistoryInfo> listInviteKickbackHistoryDetails;
    private Map<String, String> mapKickbackTypes = new LinkedHashMap<String, String>();
    private String pattern;
    private InputStream csvFile;
    private String csvFileName;

    public InviteCustomerSearchCondition getCustomerSearchCondition() {
        return customerSearchCondition;
    }

    public void setCustomerSearchCondition(InviteCustomerSearchCondition customerSearchCondition) {
        this.customerSearchCondition = customerSearchCondition;
    }

    public InviteKickbackHistoryCondition getKickbackHistoryCondition() {
        return kickbackHistoryCondition;
    }

    public void setKickbackHistoryCondition(InviteKickbackHistoryCondition kickbackHistoryCondition) {
        this.kickbackHistoryCondition = kickbackHistoryCondition;
    }

    public List<InviteCustomerInfo> getListInviteCustomerDetails() {
        return listInviteCustomerDetails;
    }

    public void setListInviteCustomerDetails(List<InviteCustomerInfo> listInviteCustomerDetails) {
        this.listInviteCustomerDetails = listInviteCustomerDetails;
    }

    public List<InviteKickbackHistoryInfo> getListInviteKickbackHistoryDetails() {
        return listInviteKickbackHistoryDetails;
    }

    public void setListInviteKickbackHistoryDetails(List<InviteKickbackHistoryInfo> listInviteKickbackHistoryDetails) {
        this.listInviteKickbackHistoryDetails = listInviteKickbackHistoryDetails;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Map<String, String> getMapKickbackTypes() {
        return mapKickbackTypes;
    }

    public void setMapKickbackTypes(Map<String, String> mapKickbackTypes) {
        this.mapKickbackTypes = mapKickbackTypes;
    }

    public InputStream getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(InputStream csvFile) {
        this.csvFile = csvFile;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public IBInfo getIbInfo() {
        return ibInfo;
    }

    public void setIbInfo(IBInfo ibInfo) {
        this.ibInfo = ibInfo;
    }
}
