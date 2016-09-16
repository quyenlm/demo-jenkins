package phn.nts.ams.fe.web.action.withdrawal;

import org.apache.log4j.Logger;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.model.WithdrawalAjaxModel;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

import java.util.ArrayList;
import java.util.List;


public class WithdrawalAJAXAction extends BaseSocialAction<WithdrawalAjaxModel> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(WithdrawalAJAXAction.class);
    private WithdrawalAjaxModel model = new WithdrawalAjaxModel();
    private IWithdrawalManager iWithdrawalManager;
    private final String UNDEFINED = "undefined";


    public WithdrawalAjaxModel getModel() {
        return model;
    }

    /**
     *
     *
     * @param
     * @return
     * @auth Mai.Thu.Huyen
     * @CrDate Oct 15, 2012
     * @MdDate
     */
    public String executeGetWithdrawalFee() {
        try {
            String gwIdParams = httpRequest.getParameter("gwId");
            String amount = httpRequest.getParameter("amount");

            FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
            FrontUserOnline frontUserOnline = null;
            String currencyCode = null;
            String wlCode = null;
            if (frontUserDetails != null) {
                frontUserOnline = frontUserDetails.getFrontUserOnline();
                if (frontUserOnline != null) {
                    currencyCode = frontUserOnline.getCurrencyCode();
                    wlCode = frontUserOnline.getWlCode();
                }
            }
            if (StringUtil.isEmpty(gwIdParams) || StringUtil.isEmpty(amount)) {

            } else {
                Integer gwId = null;
                Double amountRequest = MathUtil.parseDouble(amount);
                if (!UNDEFINED.equals(gwIdParams)) {
                    gwId = MathUtil.parseInteger(gwIdParams);
                }

                Double fee = iWithdrawalManager.getWithdrawalFee(amountRequest, currencyCode, gwId, wlCode);
                //Double receivedAmount = MathUtil.parseDouble(amount) - fee;
                Double receivedAmount = 0D;
                if (amountRequest > fee) {
                    receivedAmount = amountRequest - fee;
                    model.setMessage(null);
                } else {
                    List<String> listContent = new ArrayList<String>();
                    listContent.add(StringUtil.toString(fee));
                    listContent.add(currencyCode);
                    model.setMessage(getText("MSG_NAB101", listContent));
                }
                model.setWithdrawalFee(StringUtil.toString(fee));
//                model.setWithdrawalFee(model.formatNumberByPattern(MathUtil.parseBigDecimal(fee), IConstants.NUMBER_FORMAT.FORMAT_DEFAULT));
                model.setFormatWithdrawlFee(model.formatNumber(MathUtil.parseBigDecimal(fee), currencyCode));

                model.setFormatReceivedAmount(model.formatNumber(MathUtil.parseBigDecimal(receivedAmount), currencyCode));
                model.setReceivedAmount(StringUtil.toString(receivedAmount));
                model.setCurrencyCode(currencyCode);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return SUCCESS;
    }

    /**
     * @param iWithdrawalManager the iWithdrawalManager to set
     */
    public void setiWithdrawalManager(IWithdrawalManager iWithdrawalManager) {
        this.iWithdrawalManager = iWithdrawalManager;
    }

}