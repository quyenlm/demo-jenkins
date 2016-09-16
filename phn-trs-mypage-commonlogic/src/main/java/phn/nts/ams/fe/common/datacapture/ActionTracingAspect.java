package phn.nts.ams.fe.common.datacapture;

import org.aspectj.lang.ProceedingJoinPoint;
import phn.com.nts.util.log.Logit;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 12/02/2014 1:21 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ActionTracingAspect {

    private static Logit LOG = Logit.getInstance(ActionTracingAspect.class);

    public String trace(ProceedingJoinPoint proceedingJP) throws Throwable {

        String methodInformation = proceedingJP.getStaticPart().getSignature().toString();
        LOG.info("Entering " + methodInformation);
        try {
            Object result = proceedingJP.proceed();
            return result == null ? null : result.toString();
        } catch (Throwable ex) {
            LOG.error("Exception in " + methodInformation, ex);
            throw ex;
        } finally {
            LOG.info("Exiting " + methodInformation);
        }
    }
}
