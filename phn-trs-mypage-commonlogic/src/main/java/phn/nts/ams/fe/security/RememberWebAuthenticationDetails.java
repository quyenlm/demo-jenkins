package phn.nts.ams.fe.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class RememberWebAuthenticationDetails extends WebAuthenticationDetails {
    private final String queryString;
 
    public RememberWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
         
        this.queryString = request.getQueryString();
    }
     
    public String getQueryString() {
        return this.queryString;
    }
}