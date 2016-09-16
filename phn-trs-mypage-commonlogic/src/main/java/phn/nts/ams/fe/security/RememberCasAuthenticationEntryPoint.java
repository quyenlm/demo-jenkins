package phn.nts.ams.fe.security;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.CommonUtils;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;



import phn.com.nts.util.common.StringUtil;

public class RememberCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {
    String targetUrlParameter = "target-path";
     
    protected String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
        String service = this.getServiceProperties().getService();
        request.getPathInfo();
        String servletPath = request.getServletPath();        
        
        
        if (!StringUtil.isEmpty(servletPath)) {
        	Map<String, String []> mapParams = request.getParameterMap();
        	boolean isFirst = true;
            for(Entry<String, String[]> e : mapParams.entrySet()){
            	if(isFirst){
            		servletPath += String.format("?%s=%s", e.getKey(), e.getValue()[0]);
            	}else{
            		servletPath += String.format("&%s=%s", e.getKey(), e.getValue()[0]);
            	}
            }
            service += String.format("?%s=%s", this.targetUrlParameter, servletPath);
        }
         
        return CommonUtils.constructServiceUrl(null, response, service, null, this.getServiceProperties().getArtifactParameter(), this.getEncodeServiceUrlWithSessionId());
    }

	public String getTargetUrlParameter() {
		return targetUrlParameter;
	}

	public void setTargetUrlParameter(String targetUrlParameter) {
		this.targetUrlParameter = targetUrlParameter;
	}
    
    
}