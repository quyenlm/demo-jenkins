package phn.nts.ams.fe.payment;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.NetellerResponseInfo;

public class NetellerContext {	
	private static Logit LOG = Logit.getInstance(NetellerContext.class);
	private static NetellerContext instance;
	public static NetellerContext getInstance() {
		if(instance == null) {
			instance = new NetellerContext();			
		}
		return instance;
	}
	/**
	 * 　
	 * get neteller response info
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public NetellerResponseInfo getNetellerResponseInfo(Map<String, String> netellerConfig, NetellerInfo netellerInfo) {
		NetellerResponseInfo netellerResponseInfo = new NetellerResponseInfo();
		try {
			String url = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_API_URL);
			String version = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_API_VERSION);
			
			String charset = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_ENCODING_CHARSET);

			String merchantId = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_MERCHANT_ID);
			
			String merchantKey = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_MERCHANT_KEY);
			String merchantName = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_MERCHANT_NAME);
			String merchantAccount = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_MERCHANT_ACCOUNT);

			LOG.info("merchantId: " + merchantId + ", merchantKey: " + merchantKey + ", merchantName: " + merchantName + ", merchantAccount: " + merchantAccount);
			// sender			
			String currencyCode = netellerInfo.getCurrencyCode().trim();
			String transactionId = MathUtil.generateRandomPassword(15).toUpperCase();
			netellerResponseInfo.setTransactionId(transactionId);
			String languageCode = netellerConfig.get(IConstants.NETELLER_CONFIGURATION.NETELLER_LANGUAGE_CODE);

			BigDecimal amount = netellerInfo.getAmount();
			String netAccount = netellerInfo.getAccountId() == null ? "": netellerInfo.getAccountId().trim();
			String netSecureId = netellerInfo.getSecureId() == null ? "": netellerInfo.getSecureId().trim();

			String parameters = "version="
					+ URLEncoder.encode(version, charset) + "&amount="
					+ URLEncoder.encode(amount.toEngineeringString(), charset)
					+ "&currency=" + URLEncoder.encode(currencyCode, charset)
					+ "&net_account=" + URLEncoder.encode(netAccount, charset)
					+ "&secure_id=" + URLEncoder.encode(netSecureId, charset)
					+ "&merchant_id=" + URLEncoder.encode(merchantId, charset)
					+ "&merch_key=" + URLEncoder.encode(merchantKey, charset)
					+ "&language_code="
					+ URLEncoder.encode(languageCode, charset) + "&merch_name="
					+ URLEncoder.encode(merchantName, charset)
					+ "&merch_account="
					+ URLEncoder.encode(merchantAccount, charset)
					+ "&merch_transid="
					+ URLEncoder.encode(transactionId, charset);
			LOG.info("PARAMETERS: " + parameters);
			HttpURLConnection conn = Utilities.doPost(url,parameters);

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			org.jdom.Document document = (org.jdom.Document) builder.build(conn.getInputStream());
			org.jdom.Element rootNode = document.getRootElement();

			String approval = rootNode.getChildText(IConstants.NETELLER_RESPONSE.PARAM_APPROVAL);
			// String urlDecline = Utilities.getValueByName(stream,
			// Constants.NETELLER_RESPONE_PARA_URL);
			String errorMessage = rootNode.getChildText(IConstants.NETELLER_RESPONSE.PARAM_ERROR_MESSAGE);
			String errorCode = rootNode.getChildText(IConstants.NETELLER_RESPONSE.PARAM_ERROR_CODE);
			LOG.info("Approval: " + approval + ", urlMessage: " + errorMessage + ", errorCode: " + errorCode);
			netellerResponseInfo.setApproval(approval);
			netellerResponseInfo.setErrorCode(errorCode);
			netellerResponseInfo.setErrorMessage(errorMessage);
			
			conn.disconnect();
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}		
		return netellerResponseInfo;
	}
}
