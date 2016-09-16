package phn.nts.ams.fe.payment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Map;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.AllChargeResponseInfo;

public class AllChargeContext {
	private static Logit LOG = Logit.getInstance(AllChargeContext.class);
	private static AllChargeContext instance;
	public static AllChargeContext getInstance() {
		if(instance == null) {
			instance = new AllChargeContext();
		}
		return instance;
	}
	public AllChargeResponseInfo getAllChargeResponseInfo(String transactionId, BigDecimal amount, String currencyCode, String wlCode) {		
		AllChargeResponseInfo allChargeResponseInfo = new AllChargeResponseInfo();
		try {			
			Map<String, String> mapAllChargeConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.ALLCHARGE_CONFIG);				
			String merchantId = mapAllChargeConfig.get(IConstants.ALLCHARGE_CONFIGURATION.ALLCHARGE_DCPID);
			String syncUrl = mapAllChargeConfig.get(IConstants.ALLCHARGE_CONFIGURATION.ALLCHARGE_PAYMENT_SYNC_GATEWAY);
			String password = mapAllChargeConfig.get(IConstants.ALLCHARGE_CONFIGURATION.ALLCHARGE_PASSWORD);
			String params = "MerchantID=" + merchantId + "&Password=" + password + "&TransactionID=" + transactionId + "&Amount=" + amount + "&currency=" + currencyCode;
			HttpURLConnection conn = Utilities.doPost(syncUrl, params);
			InputStreamReader input = new InputStreamReader(conn.getInputStream());		
			BufferedReader in = new BufferedReader(input);
			String line = null;
		    while ( (line = in.readLine()) != null ) {
		    	line = URLDecoder.decode(line, IConstants.UTF8);
		    	break;
		    }
		    LOG.info("Response from: " + syncUrl + "is " + line);
		    Map<String, String> mapResponse = Utilities.getQueryMap(line);
		    allChargeResponseInfo.setAmount(MathUtil.parseBigDecimal(mapResponse.get(AllChargeResponseInfo.AMOUNT)));
		    allChargeResponseInfo.setCity(mapResponse.get(AllChargeResponseInfo.CITY));
		    allChargeResponseInfo.setEmail(mapResponse.get(AllChargeResponseInfo.EMAIL));
		    allChargeResponseInfo.setFirstName(mapResponse.get(AllChargeResponseInfo.FIRSTNAME));
		    allChargeResponseInfo.setLastName(mapResponse.get(AllChargeResponseInfo.LASTNAME));
		    allChargeResponseInfo.setInstallments(mapResponse.get(AllChargeResponseInfo.INSTALLMENTS));
		    allChargeResponseInfo.setMerchantData(mapResponse.get(AllChargeResponseInfo.MERCHANTDATA));
		    allChargeResponseInfo.setPhone(mapResponse.get(AllChargeResponseInfo.PHONE));
		    allChargeResponseInfo.setReturnCode(new Integer(mapResponse.get(AllChargeResponseInfo.RET_CODE)));
		    allChargeResponseInfo.setShippingAddress(mapResponse.get(AllChargeResponseInfo.SHIPPING_ADDRESS));
		    allChargeResponseInfo.setSyncType(mapResponse.get(AllChargeResponseInfo.TYPE));
		    allChargeResponseInfo.setTransactionId(mapResponse.get(AllChargeResponseInfo.TRANSACTION_ID));
		    allChargeResponseInfo.setZip(mapResponse.get(AllChargeResponseInfo.ZIP));
		    allChargeResponseInfo.writeLog();
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		
	    
		return allChargeResponseInfo;
	}
}
