package phn.nts.ams.fe.payment;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Security;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.LibertyInfo;

public class LibertyContext {
	private static Logit LOG = Logit.getInstance(LibertyContext.class);
	private static LibertyContext instance;
	public static LibertyContext getInstance() {
		if(instance == null) {
			instance = new LibertyContext();			
		}
		return instance;
	}
	public String getLibertySystem(String orderId, LibertyInfo libertyInfo, Map<String, String> mapLibertyConfig, Map<String, String> mapFrontEnd, String currencyCode) {
		String url = "";		
		try {
			url = mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.PAYMENT_GATEWAY_LIBERTY);			
			String successUrl = mapFrontEnd.get(IConstants.FRONT_END_CONFIG.HOME) + mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.SUCCESS_URL);	
			String failUrl = mapFrontEnd.get(IConstants.FRONT_END_CONFIG.HOME) + mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.FAIL_URL);		
			String statusUrl = mapFrontEnd.get(IConstants.FRONT_END_CONFIG.HOME) + mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.STATUS_URL);
			StringBuffer paramSB = new StringBuffer();		
			String merchantId = mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.MERCHANT_ID);
			String storeName = mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.STORE_NAME);
//			String privateSecurityKey = mapLibertyConfig.get(IConstants.PAYONLINE_CONFIGURATION.PRIVATE_SECURITY_KEY);			
			LOG.info("Total of transactionId " + orderId + ": " + libertyInfo.getAmount());
			paramSB.append("lr_acc=" + merchantId.trim());
//			paramSB.append("&lr_acc_from=" +libertyInfo.getAccountNumber().trim());
			paramSB.append("&lr_store=" + storeName.trim());
			paramSB.append("&lr_amnt=" + String.format("%.2f", libertyInfo.getAmount().doubleValue()));
			paramSB.append("&lr_currency=LR"+ currencyCode);			
			paramSB.append("&lr_success_url=" + URLEncoder.encode(successUrl,"UTF-8"));
			paramSB.append("&lr_success_url_method=POST");
			paramSB.append("&lr_fail_url=" + URLEncoder.encode(failUrl, "UTF-8"));
			paramSB.append("&lr_fail_url_method=POST");
			paramSB.append("&lr_status_url=" + URLEncoder.encode(statusUrl, "UTF-8"));
			paramSB.append("&lr_status_url_method=POST");
			paramSB.append("&order_id=" + orderId);
			String parameters = paramSB.toString();
			// set url for liberty
			url += parameters;
			LOG.info("==========PARAMETTER================");
			LOG.info("MerchantId=" + merchantId + ", OrderId= "  + orderId + ", Amount=" +  String.format("%.2f", libertyInfo.getAmount().doubleValue()) + ",Currency= " + IConstants.CURRENCY_CODE.USD);
			LOG.info("transactionId before encrypt= " + orderId);
			LOG.info("Transaction ID after encrypt = " + orderId);			
			LOG.info("ReturnURL=" + successUrl + ", FailURL="+failUrl);	
			
			LOG.info("url sending = " + url);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		
		return url;
	}
	public Boolean checkStatusOnPayonline(String orderId, Map<String, String> mapPayonlineConfig, Map<String, String> mapFrontEnd) {
		Boolean result = Boolean.FALSE;
		try {
			String merchantId = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.MERCHANT_ID);
			String privateSecurityKey = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.PRIVATE_SECURITY_KEY);			
			String checkingUrl = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.PAYMENT_GATEWAY_CHECKING);
			
			String contentType = "xml";
			
			String securityKey = "MerchantId=" + merchantId + "&OrderId=" + orderId + "&PrivateSecurityKey=" + privateSecurityKey;
			LOG.info("security of " + orderId + " before: " + securityKey);
			securityKey = Security.MD5(securityKey);
			LOG.info("security of " + orderId + " after: " + securityKey);
			String token = "MerchantId=" + merchantId + "&OrderId=" + orderId + "&SecurityKey=" + securityKey + "&ContentType=" + contentType;
			LOG.info("of " + orderId + " url: " + checkingUrl + ", " + token);
			HttpURLConnection conn = Utilities.doPost("https://secure.payonlinesystem.com/payment/search/", token);							
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			org.jdom.Document document = (org.jdom.Document) builder.build(conn.getInputStream());
			org.jdom.Element rootNode = document.getRootElement();
			String responseAmount = rootNode.getChildText("amount");
			String responseCurrency = rootNode.getChildText("currency");
			String responseOrderId = rootNode.getChildText("orderId");
			String responseDateTime = rootNode.getChildText("dateTime");
			String responseStatus = rootNode.getChildText("status");
			LOG.info("ResponseInfo of " + orderId + ":responseOrderId:" + responseOrderId + ", responseAmount: " + responseAmount 
					+ ", currency: " + responseCurrency + ", orderId: " + orderId + ", dateTime: " + responseDateTime
					+ ", status: " + responseStatus);
			if(IConstants.RESPONSE_PAYONLINE_STATUS.PENDING.equalsIgnoreCase(responseStatus) || IConstants.RESPONSE_PAYONLINE_STATUS.SETTLE.equalsIgnoreCase(responseStatus)) {
				result = Boolean.TRUE;				
			}
			
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}			
		return result;
	}
	
	/**
	 * encode liberty information to check status　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 27, 2013
	 */
	public String encodeLibertyInfo(String lr_paidto, String lr_paidby, String lr_store, String lr_amnt, String lr_transfer, String lr_currency, Map<String, String> mapLibertyConfig) {
		String secureWord = mapLibertyConfig.get(IConstants.LIBERTY_CONFIGURATION.STORE_SECURITY);
		StringBuffer encodeString = new StringBuffer();
		try {
			encodeString.append(lr_paidto).append(":");
			encodeString.append(lr_paidby).append(":");
			encodeString.append(lr_store).append(":");
			encodeString.append(lr_amnt).append(":");
			encodeString.append(lr_transfer).append(":");
			encodeString.append(lr_currency).append(":");
			encodeString.append(secureWord);
			LOG.info("[END] encode liberty info: " + encodeString.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return StringUtil.sha256Hex(encodeString.toString());
	}
}
