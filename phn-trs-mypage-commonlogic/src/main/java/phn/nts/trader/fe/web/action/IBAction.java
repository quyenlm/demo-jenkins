package phn.nts.trader.fe.web.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import phn.com.nts.ams.web.condition.InviteCustomerSearchCondition;
import phn.com.nts.ams.web.condition.InviteKickbackHistoryCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.domain.InviteCustomerInfo;
import phn.com.nts.db.domain.InviteKickbackHistoryInfo;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;
import phn.nts.trader.fe.business.IIBManager;
import phn.nts.trader.fe.domain.IBInfo;
import phn.nts.trader.fe.model.IBModel;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/11/13 9:30 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class IBAction extends BaseSocialAction<IBModel> {

    private IBModel model = new IBModel();
    private IIBManager ibManager;
    private static Logit LOG = Logit.getInstance(IBAction.class);
    private String dispatch;
    private static final String SEARCH_ACTION = "search";
    private static final String CSV_ACTION = "csv";
    private static final String EXPORT_RESULT = "export";

    public String inviteManagement() {
        try {
            String currencyCode = "";
            String wlCode = getWlCode();
            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            FrontUserOnline frontUserOnline = null;
            if(frontUserDetails != null) {
                frontUserOnline = frontUserDetails.getFrontUserOnline();
            }
            if(frontUserOnline!=null){
                currencyCode = frontUserOnline.getCurrencyCode();
                getModel().setCurrencyCode(frontUserOnline.getCurrencyCode());
            }
            IBInfo ibInfo = ibManager.getIbMgmtInfo(getCurrentCustomerId());
            ibInfo.setCurrencyCode(currencyCode);

            Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
//            ibInfo.setFbShareUrl(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_URL));
//            ibInfo.setFbShareSummary(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_SUMMARY));
//            ibInfo.setFbShareThumbnail(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_THUMBNAIL));
//            ibInfo.setFbShareTitle(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_TITLE));
//            ibInfo.setTwShareContent(mapConfiguration.get(ITrsConstants.TWITTER_API.TW_SHARE_CONTENT));
//            ibInfo.setGpShareContent(mapConfiguration.get(ITrsConstants.GOOGLE_PLUS_API.GP_SHARE_CONTENT));
            ibInfo.setFbShareUrl(ibInfo.getIbLink());
            ibInfo.setFbShareSummary(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_SUMMARY));
            ibInfo.setFbShareThumbnail(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_THUMBNAIL));
            ibInfo.setFbShareTitle(mapConfiguration.get(ITrsConstants.FACEBOOK_API.FB_SHARE_TITLE));
            ibInfo.setTwShareContent(ibInfo.getIbLink());
            ibInfo.setTwShareSummary(mapConfiguration.get(ITrsConstants.TWITTER_API.TW_SHARE_SUMMARY));
            ibInfo.setTwShareThumbnail(mapConfiguration.get(ITrsConstants.TWITTER_API.TW_SHARE_THUMBNAIL));
            ibInfo.setTwShareTitle(mapConfiguration.get(ITrsConstants.TWITTER_API.TW_SHARE_TITLE));
            
            ibInfo.setGpShareContent(ibInfo.getIbLink());
            ibInfo.setGpShareSummary(mapConfiguration.get(ITrsConstants.GOOGLE_PLUS_API.GP_SHARE_SUMMARY));
            ibInfo.setGpShareThumbnail(mapConfiguration.get(ITrsConstants.GOOGLE_PLUS_API.GP_SHARE_THUMBNAIL));
            ibInfo.setGpShareTitle(mapConfiguration.get(ITrsConstants.GOOGLE_PLUS_API.GP_SHARE_TITLE));
            getModel().setIbInfo(ibInfo);
        } catch(Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return INPUT;
        }

        return SUCCESS;
    }

    public String kickbackHistoryIndex(){
        return SUCCESS;
    }

    public String inviteCustomerSearchIndex(){
        return SUCCESS;
    }

    public String inviteCustomerSearch(){
        try{
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));
            //if(!StringUtil.isEmpty(dispatch)) {
                InviteCustomerSearchCondition condition = model.getCustomerSearchCondition();
                if(condition == null){
                    condition = new InviteCustomerSearchCondition();
                    model.setCustomerSearchCondition(condition);
                }
                //if(condition != null){
                    condition.setCurrentCustomerId(getCurrentCustomerId());
                    PagingInfo pagingInfo = model.getPagingInfo();
                    if(pagingInfo == null) {
                        pagingInfo = new PagingInfo();
                        pagingInfo.setOffset(20);
                        model.setPagingInfo(pagingInfo);
                    }
                    List<InviteCustomerInfo> searchResult = ibManager.getInviteCustomerDetails(model.getCustomerSearchCondition(), model.getPagingInfo());
                    if(CSV_ACTION.equalsIgnoreCase(dispatch)){
                        pagingInfo.setOffset(IConstants.CSV.MAX_RECORDS_EXPORT);
                        return exportCsvInviteCustomer(searchResult);
                    } else {
                        model.setListInviteCustomerDetails(searchResult);
                    }
                //}
            //}
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private String exportCsvInviteCustomer(List<InviteCustomerInfo> searchResult) {
        try{
            StringBuffer content = new StringBuffer();
            String currentDate = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME_EXPORT_CSV);
            String CSVFileName = "InviteCustomer_" + currentDate + ".csv";
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.no")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.customerId")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.customerName")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.currencycode")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.kickbackAmount")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.KickbackDate"));


            for(int i = 0; i < searchResult.size(); i++){
                InviteCustomerInfo info = searchResult.get(i);
                content.append("\n");
                content.append(i + 1).append(",");
                content.append(info.getCustomerId()).append(",");
                content.append(info.getCustomerName()).append(",");
                content.append(info.getBaseCurrency()).append(",");
                content.append(info.getKickbackAmount() == null ? IConstants.DEALING_CONSTANTS.IS_NULL_DATA_CSV : info.getKickbackAmount().toString()).append(" " + info.getBaseCurrency()).append(",");
                content.append(info.getKickbackDate());
            }

            InputStream fileContent = new ByteArrayInputStream(content.toString().getBytes("Shift_JIS"));
            model.setCsvFileName(CSVFileName);
            model.setCsvFile(fileContent);
        } catch (Exception e){
            return INPUT;
        }
        return EXPORT_RESULT;
    }

    public String kickbackHistorySearch(){
        try{
            model.setPattern(getText("nts.ams.fe.label.date.full.pattern"));
            Map<String, String> mapKickbackTypes = model.getMapKickbackTypes();
            
            mapKickbackTypes.put(IConstants.KICKBACK_TYPE.TRADED_COMPLETE.toString(), getText("nts.ams.fe.label.ibManagement.kickback.history.traded.complete"));
            
            InviteKickbackHistoryCondition condition = model.getKickbackHistoryCondition();
            if(condition == null){
                condition = new InviteKickbackHistoryCondition();
                String currentDate = DateUtil.toString(new Timestamp(System.currentTimeMillis()), DateUtil.PATTERN_YYMMDD);
                condition.setFromDate(currentDate);
                condition.setToDate(currentDate);
                condition.setKickBackType(IConstants.KICKBACK_TYPE.TRADED_COMPLETE);
                model.setKickbackHistoryCondition(condition);
            }

                condition.setCurrentCustomerId(getCurrentCustomerId());
                PagingInfo pagingInfo = model.getPagingInfo();
                if(pagingInfo == null) {
                    pagingInfo = new PagingInfo();
                    pagingInfo.setOffset(10);
                    model.setPagingInfo(pagingInfo);
                }
                List<InviteKickbackHistoryInfo> searchResult = ibManager.getInviteKickbackHistoryDetails(model.getKickbackHistoryCondition(), model.getPagingInfo());
                if(CSV_ACTION.equals(dispatch)){
                    pagingInfo.setOffset(IConstants.CSV.MAX_RECORDS_EXPORT);
                    return exportCsvKickbackHistory(searchResult, mapKickbackTypes);
                } else {  
                    model.setListInviteKickbackHistoryDetails(searchResult);
                }
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    private String exportCsvKickbackHistory(List<InviteKickbackHistoryInfo> searchResult, Map<String, String> mapKickbackTypes) {
        try{
            StringBuffer content = new StringBuffer();
            String currentDate = DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.ALL_DATE_TIME_EXPORT_CSV);
            String CSVFileName = "KickbackHistory_" + currentDate + ".csv";
            content.append(getText("nts.ams.fe.label.ibManagement.kickback.history.searchResult.date")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.kickback.history.searchResult.no.person")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.kickback.history.searchResult.kickback.amount")).append(",");
            content.append(getText("nts.ams.fe.label.ibManagement.kickback.history.searchResult.type"));

            for(InviteKickbackHistoryInfo info : searchResult){
                content.append("\n");
                content.append(info.getDate()).append(",");
                content.append(info.getNoPeople()).append(",");
                content.append(info.getKickbackAmount() == null ? IConstants.DEALING_CONSTANTS.IS_NULL_DATA_CSV : info.getKickbackAmount().toString()).append(info.getKickbackCurrency() == null ? IConstants.DEALING_CONSTANTS.IS_NULL_DATA_CSV : info.getKickbackCurrency()).append(",");
                content.append(info.getKickbackType() == null ? IConstants.DEALING_CONSTANTS.IS_NULL_DATA_CSV : mapKickbackTypes.get(info.getKickbackType().toString()));
            }

            InputStream fileContent = new ByteArrayInputStream(content.toString().getBytes("Shift_JIS"));
            model.setCsvFileName(CSVFileName);
            model.setCsvFile(fileContent);
        } catch (Exception e){
            return INPUT;
        }
        return EXPORT_RESULT;
    }

    public IBModel getModel() {
        return model;
    }

    public void setModel(IBModel model) {
        this.model = model;
    }

    public IIBManager getIbManager() {
        return ibManager;
    }

    public void setIbManager(IIBManager ibManager) {
        this.ibManager = ibManager;
    }

    public String getDispatch() {
        return dispatch;
    }

    public void setDispatch(String dispatch) {
        this.dispatch = dispatch;
    }
}
