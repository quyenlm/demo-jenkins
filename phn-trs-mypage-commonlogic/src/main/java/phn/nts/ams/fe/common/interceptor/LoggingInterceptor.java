package phn.nts.ams.fe.common.interceptor;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

import phn.com.nts.util.log.Logit;


public class LoggingInterceptor implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice {

	private static Logit logger = null;

	public void before(Method method, Object[] args, Object target)
			throws Throwable {
		logger = Logit.getInstance(target.getClass());
		if(logger.ison()) {
			logger.info(method.getName() + ":: BEGIN");
		}
	}

	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		logger = Logit.getInstance(target.getClass());
		if(logger.ison()) {
			logger.info(method.getName() + ":: END");
		}
	}

	public void afterThrowing(Method m, Object[] args, Object target,
			Throwable ex) {

	}
	
}