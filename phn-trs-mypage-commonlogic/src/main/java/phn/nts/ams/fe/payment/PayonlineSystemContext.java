package phn.nts.ams.fe.payment;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Security;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.PayonlinePaymentDetail;

public class PayonlineSystemContext {
	private static Logit LOG = Logit.getInstance(PayonlineSystemContext.class);
	private static PayonlineSystemContext instance;
	public static PayonlineSystemContext getInstance() {
		if(instance == null) {
			instance = new PayonlineSystemContext();			
		}
		return instance;
	}
	public String getPayonlineSystem(String orderId, CreditCardInfo creditCardInfo, Map<String, String> mapPayonlineConfig, Map<String, String> mapFrontEnd) {
		String url = "";		
		try {
			url = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.PAYMENT_GATEWAY_PAYONLINE);			
			String returnUrl = mapFrontEnd.get(IConstants.FRONT_END_CONFIG.HOME) + mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.RETURN_URL) + "?payKey=" + orderId;
			String failUrl   = mapFrontEnd.get(IConstants.FRONT_END_CONFIG.HOME) + mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.FAIL_URL) +  "?payKey=" + orderId;		
			StringBuffer paramSB = new StringBuffer();		
			String merchantId = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.MERCHANT_ID);
			String privateSecurityKey = mapPayonlineConfig.get(IConstants.PAYONLINE_CONFIGURATION.PRIVATE_SECURITY_KEY);			
			LOG.info("Total of transactionId " + orderId + ": " + creditCardInfo.getAmount());
			paramSB.append("MerchantId="+merchantId.trim());
			paramSB.append("&OrderId="+orderId.trim());
			paramSB.append("&Amount="+ String.format("%.2f", creditCardInfo.getAmount().doubleValue()));
			paramSB.append("&Currency="+ IConstants.CURRENCY_CODE.USD);			
			String params = "MerchantId="+ merchantId.trim() +"&OrderId="+ orderId +"&Amount="+ String.format("%.2f", creditCardInfo.getAmount())+"&Currency="+IConstants.CURRENCY_CODE.USD+"&PrivateSecurityKey="+privateSecurityKey;			
			params = Security.MD5(params);
			paramSB.append("&SecurityKey=" + params);	
			paramSB.append("&ReturnUrl=" + URLEncoder.encode(returnUrl,"UTF-8"));
			paramSB.append("&FailUrl=" + URLEncoder.encode(failUrl, "UTF-8"));
			String parameters = paramSB.toString();
			// set url for payonline system
			url += parameters;
			LOG.info("==========PARAMETTER================");
			LOG.info("MerchantId=" + merchantId + ", OrderId= "  + orderId + ", Amount=" +  String.format("%.2f", creditCardInfo.getAmount().doubleValue()) + ",Currency= " + IConstants.CURRENCY_CODE.USD + ", privateSecurityKey= " + privateSecurityKey + ", SecurityKey= " +params);
			LOG.info("transactionId before encrypt= " + orderId);
			LOG.info("Transaction ID after encrypt = " + orderId);			
			LOG.info("ReturnURL=" + returnUrl + ", FailURL="+failUrl);	
			
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
			String id = rootNode.getChildText("id");
			String responseAmount = rootNode.getChildText("amount");
			String responseCurrency = rootNode.getChildText("currency");
			String responseOrderId = rootNode.getChildText("orderId");
			String responseDateTime = rootNode.getChildText("dateTime");
			String responseStatus = rootNode.getChildText("status");
			LOG.info("ResponseInfo of " + orderId + ":responseOrderId:" + responseOrderId + ", responseAmount: " + responseAmount 
					+ ", currency: " + responseCurrency + ", orderId: " + orderId + ", dateTime: " + responseDateTime + ", transactionId: " + id
					+ ", status: " + responseStatus);
			if(IConstants.RESPONSE_PAYONLINE_STATUS.PENDING.equalsIgnoreCase(responseStatus) || IConstants.RESPONSE_PAYONLINE_STATUS.SETTLE.equalsIgnoreCase(responseStatus)) {
				result = Boolean.TRUE;				
			}
			
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}			
		return result;
	}
	public PayonlinePaymentDetail getPayonlinePaymentDetail(String orderId, Map<String, String> mapPayonlineConfig, Map<String, String> mapFrontEnd) {
		PayonlinePaymentDetail payonlinePaymentDetail = new PayonlinePaymentDetail();
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
			String id = rootNode.getChildText("id");
			String responseAmount = rootNode.getChildText("amount");
			String responseCurrency = rootNode.getChildText("currency");
			String responseOrderId = rootNode.getChildText("orderId");
			String responseDateTime = rootNode.getChildText("dateTime");
			String responseStatus = rootNode.getChildText("status");
			LOG.info("ResponseInfo of " + orderId + ":responseOrderId:" + responseOrderId + ", responseAmount: " + responseAmount 
					+ ", currency: " + responseCurrency + ", orderId: " + orderId + ", dateTime: " + responseDateTime + ", transactionId: " + id
					+ ", status: " + responseStatus);
			payonlinePaymentDetail.setTransactionId(id);
			payonlinePaymentDetail.setAmount(responseAmount);
			payonlinePaymentDetail.setCurrency(responseCurrency);
			payonlinePaymentDetail.setDateTime(responseDateTime);
			payonlinePaymentDetail.setOrderId(responseOrderId);
			payonlinePaymentDetail.setStatus(responseStatus);						
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}			
		return payonlinePaymentDetail;
	}
}
