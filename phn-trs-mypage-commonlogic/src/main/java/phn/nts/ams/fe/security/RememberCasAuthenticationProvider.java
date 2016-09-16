package phn.nts.ams.fe.security;

import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import phn.com.nts.util.common.StringUtil;

public class RememberCasAuthenticationProvider extends CasAuthenticationProvider {
	 
    UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    ServiceProperties serviceProperties;
    GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    String targetUrlParameter = "target-path";
 
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	if (!supports(authentication.getClass())) {
            return null;
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken
            && (!CasAuthenticationFilter.CAS_STATEFUL_IDENTIFIER.equals(authentication.getPrincipal().toString())
            && !CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER.equals(authentication.getPrincipal().toString()))) {
            // UsernamePasswordAuthenticationToken not CAS related
            return null;
        }

        // If an existing CasAuthenticationToken, just check we created it
        if (authentication instanceof CasAuthenticationToken) {
            if (this.getKey().hashCode() == ((CasAuthenticationToken) authentication).getKeyHash()) {
                return authentication;
            } else {
                throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.incorrectKey",
                        "The presented CasAuthenticationToken does not contain the expected key"));
            }
        }

        // Ensure credentials are presented
        if ((authentication.getCredentials() == null) || "".equals(authentication.getCredentials())) {
            throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.noServiceTicket",
                    "Failed to provide a CAS service ticket to validate"));
        }

        boolean stateless = false;

        if (authentication instanceof UsernamePasswordAuthenticationToken
            && CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER.equals(authentication.getPrincipal())) {
            stateless = true;
        }

        CasAuthenticationToken result = null;

        if (stateless) {
            // Try to obtain from cache
            result = getStatelessTicketCache().getByTicketId(authentication.getCredentials().toString());
        }

        if (result == null) {
            result = this.authenticateNow(authentication);
            result.setDetails(authentication.getDetails());
        }

        if (stateless) {
            // Add to cache
            getStatelessTicketCache().putTicketInCache(result);
        }

        return result;
    }
    
    protected CasAuthenticationToken authenticateNow(final Authentication authentication) throws AuthenticationException {
        try {
            String targetPath = this.getTargetPath(authentication.getDetails());
         
            String service = String.format("%s?%s", serviceProperties.getService(), targetPath);
             
            final Assertion assertion = this.getTicketValidator().validate(authentication.getCredentials().toString(), service);
            final UserDetails userDetails = loadUserByAssertion(assertion);
            userDetailsChecker.check(userDetails);
            return new CasAuthenticationToken(this.getKey(), userDetails, authentication.getCredentials(),
                    authoritiesMapper.mapAuthorities(userDetails.getAuthorities()), userDetails, assertion);
        } catch (final TicketValidationException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }
     
    
    protected String getTargetPath(Object authenticationDetails) {
        String targetPath = "";
         
        if (authenticationDetails instanceof RememberWebAuthenticationDetails) {
            RememberWebAuthenticationDetails details = (RememberWebAuthenticationDetails) authenticationDetails;
            String queryString = details.getQueryString();
             
            if (!StringUtil.isEmpty(queryString)) {
                int start = queryString.indexOf(this.targetUrlParameter);
                if (start >= 0) {
                    int end = queryString.indexOf("&", start);
                    if (end >= 0) {
                        targetPath = queryString.substring(start, end);
                    } else {
                        targetPath = queryString.substring(start);
                    }
                }
            }
        }
         
        return targetPath;
    }
	public void setServiceProperties(ServiceProperties serviceProperties) {
		this.serviceProperties = serviceProperties;
	}
}