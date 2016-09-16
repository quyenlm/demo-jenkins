package phn.nts.ams.fe.common.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import phn.com.nts.util.log.Logit;

/**
 * @description
 * @CrBy dai.nguyen.van
 * @CrDate 12/02/2014 11:36 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class DebugInterceptor extends SimpleTraceInterceptor {

    private volatile long count;
    private static Logit LOG = Logit.getInstance(DebugInterceptor.class);


    /**
     * Create a new DebugInterceptor with a static logger.
     */
    public DebugInterceptor() {
    }

    /**
     * Create a new DebugInterceptor with dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public DebugInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return true;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        synchronized (this) {
            this.count++;
        }
        return super.invoke(invocation);
    }

    @Override
    protected String getInvocationDescription(MethodInvocation invocation) {
        return invocation + "; count=" + this.count;
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String invocationDescription = getInvocationDescription(invocation);
        LOG.info("Entering " + invocationDescription);
        try {
            Object rval = invocation.proceed();
            LOG.info("Exiting " + invocationDescription);
            return rval;
        }
        catch (Throwable ex) {
            LOG.info("Exception thrown in " + invocationDescription, ex);
            throw ex;
        }
    }

    /**
     * Return the number of times this interceptor has been invoked.
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Reset the invocation count to zero.
     */
    public synchronized void resetCount() {
        this.count = 0;
    }

}
