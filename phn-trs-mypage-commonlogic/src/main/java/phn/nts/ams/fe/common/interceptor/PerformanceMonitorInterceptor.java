package phn.nts.ams.fe.common.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.StopWatch;
import phn.com.nts.util.log.Logit;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 12/02/2014 11:43 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

    private static Logit LOG = Logit.getInstance(PerformanceMonitorInterceptor.class);

    /**
     * Create a new PerformanceMonitorInterceptor with a static logger.
     */
    public PerformanceMonitorInterceptor() {
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return true;
    }

    /**
     * Create a new PerformanceMonitorInterceptor with a dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }


    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            stopWatch.stop();
            LOG.info(stopWatch.shortSummary());
        }
    }

}
