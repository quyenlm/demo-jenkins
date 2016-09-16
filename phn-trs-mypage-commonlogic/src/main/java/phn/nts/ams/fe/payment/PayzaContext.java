package phn.nts.ams.fe.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Map;

import phn.com.nts.util.common.Utilities;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.PayzaResponseInfo;

public class PayzaContext {
	private static Logit LOG = Logit.getInstance(PayzaContext.class);
	private static PayzaContext instance;
	public static PayzaContext getInstance() {
		if(instance == null) {
			instance = new PayzaContext();			
		}
		return instance;
	}
	/**
	 * 　
	 * get payza response from url
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 21, 2012
	 * @MdDate
	 */
	public PayzaResponseInfo getPayzaResponse(String url, String params) {
		PayzaResponseInfo payzalReponseInfo = new PayzaResponseInfo();
		try {
			HttpURLConnection conn = Utilities.doPost(url, params);
			InputStreamReader input = new InputStreamReader(conn.getInputStream());		
			BufferedReader in = new BufferedReader(input);
			String line = null;
		    while ( (line = in.readLine()) != null ) {
		    	line = URLDecoder.decode(line);
		    	break;
		    }
		    LOG.info("Response from payzal with url: " + url + ", param: " + params + ", " +  line);

		    
		    Map<String, String> mapResponse = Utilities.getQueryMap(line);
		    if(mapResponse != null) {
		    	String returnCode = mapResponse.get("RETURNCODE");
		    	String description = mapResponse.get("DESCRIPTION");
		    	String referenceNumber = mapResponse.get("REFERENCENUMBER");
		    	String testMode = mapResponse.get("TESTMODE");
		    	if(returnCode != null) {
		    		payzalReponseInfo.setReturnCode(Integer.parseInt(returnCode));
		    	}
		    	payzalReponseInfo.setDescription(description);		    			  
		    	payzalReponseInfo.setReferenceNumber(referenceNumber);		    
		    	payzalReponseInfo.setTestMode(testMode);			 
		    }		    
		    
		} catch(IOException e) {			
			LOG.error(e.getMessage(), e);
		} catch(IndexOutOfBoundsException f){
			LOG.error(f.getMessage(), f);
		} catch(Exception ef){
			LOG.error(ef.getMessage(), ef);
		}
		return payzalReponseInfo;
	}
}
