package phn.nts.ams.fe.security;

import com.nts.common.exchange.dealing.FxWsAuthInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.jms.IJmsSender;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 22/08/2013 10:01 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private static Logit LOG = Logit.getInstance(LogoutSuccessHandler.class);
    private IJmsSender jmsSender;
    private String logoutUrl;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            String ticketId = "";
            if(authentication != null) {
                FrontUserDetails userDetail  = (FrontUserDetails) authentication.getPrincipal();
                if(userDetail != null) {
                    FrontUserOnline userOnline = userDetail.getFrontUserOnline();
                    if(userOnline != null) {
                        ticketId = userOnline.getTicketId();
                    }
                }
                response.sendRedirect(logoutUrl);

                // handle logout success
                FxWsAuthInfo fxWsAuthInfo = new FxWsAuthInfo();
                fxWsAuthInfo.setResult(IConstants.WS_AUTHENTICATION_RESULT.SUCCESS);
                fxWsAuthInfo.setTicketId(ticketId);
                fxWsAuthInfo.setAuthenticationType(IConstants.WS_AUTHENTICATION_TYPE.LOGOUT);

                StringBuilder logInfo = new StringBuilder("[start] send request logout with ticketId " + ticketId);
                logInfo.append(" ,result: "+fxWsAuthInfo.getResult());
                logInfo.append(" ,authentication type: "+fxWsAuthInfo.getAuthenticationType());
                LOG.info(logInfo.toString());
                
//                jmsSender.sendTopic(IConstants.ACTIVEMQ.SC_AUTHENTICATION_RESPONSE, fxWsAuthInfo);
                jmsSender.sendTopic(IConstants.ACTIVEMQ.SC_FE_AUTHENTICATION_RESPONSE, fxWsAuthInfo);

                LOG.info("[end] send request logout with ticketId " + ticketId);
            } else {
                response.sendRedirect(logoutUrl);
            }
        } catch(Exception ex) {
            super.onLogoutSuccess(request, response, authentication);
            LOG.error(ex.getMessage(), ex);
        }
        super.onLogoutSuccess(request, response, authentication);
    }

    public IJmsSender getJmsSender() {
        return jmsSender;
    }

    public void setJmsSender(IJmsSender jmsSender) {
        this.jmsSender = jmsSender;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
}
