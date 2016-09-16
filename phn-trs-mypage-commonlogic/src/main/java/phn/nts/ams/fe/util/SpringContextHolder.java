package phn.nts.ams.fe.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * 
 * @author Nguyen Xuan Bach
 *
 */
public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext appContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.appContext = applicationContext;
	}

	public static ApplicationContext getSpringApplicationContext() {
		return appContext;
	}
}
