package phn.nts.ams.fe.security;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import phn.com.nts.util.log.Logit;

public class FrontUserOnlineContext {
	private static final Logit LOG = Logit.getInstance(FrontUserOnlineContext.class);
	public static String getUserOnline() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
        	LOG.error("getCurrentLoginUserName() - " + e.getMessage(), e);
        }
        return null;
    }
	public static FrontUserDetails getFrontUserOnline() {
        try {
        	if(SecurityContextHolder.getContext().getAuthentication() == null) {
        		return null;
        	}
            Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (obj != null && obj instanceof FrontUserDetails) {
                return (FrontUserDetails) obj;
            }
        } catch (Exception e) {
            LOG.error("getWebUserDetails() - " + e.getMessage(), e);
        }
        return null;
    }
	public static String getCurrentLoginUserName() {
		
		try {
            return getFrontUserOnline().getUsername();
        } catch (Exception e) {
        	LOG.error("getCurrentLoginUserId() - " + e.getMessage(), e);
        }
        return "";
    }
	public static String getUserId() {
		try {
            return getFrontUserOnline().getFrontUserOnline().getUserId();
        } catch (Exception e) {
        	LOG.error("getCurrentLoginUserId() - " + e.getMessage(), e);
        }
        return "";
	}
	public static SecurityContext getSecurityContext() {
	    return SecurityContextHolder.getContext();
	}
	 /**
     * check current user is annonymous or not
     * 
     * @return
     */
    public static boolean isAnonymous() {
        AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            return resolver.isAnonymous(auth);
        }
        return true;
    }

}
