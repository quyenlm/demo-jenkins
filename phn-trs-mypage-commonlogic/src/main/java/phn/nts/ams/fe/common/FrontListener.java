package phn.nts.ams.fe.common;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import phn.nts.ams.fe.business.ISystemPropertyManager;
import phn.nts.ams.fe.jms.IJmsSender;
import phn.nts.ams.fe.mt4.MT4Manager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;


public class FrontListener implements ServletContextListener {
	
	private static Logger logger = Logger.getLogger(FrontListener.class);	
	ISystemPropertyManager iSystemPropertyManager = null;
	public void contextInitialized(ServletContextEvent event) {
		try {		
			// init system properties
			ServletContext servletContext = event.getServletContext();
			if(servletContext != null) {								
				ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
				if(applicationContext != null) {
					iSystemPropertyManager = (ISystemPropertyManager) applicationContext.getBean("ISystemPropertyManager");
					iSystemPropertyManager.loadData();
					IJmsSender jmsRealSender = (IJmsSender) applicationContext.getBean("JmsRealSender");
					IJmsSender jmsDemoSender = (IJmsSender) applicationContext.getBean("JmsDemoSender");
                    //load customer ranking caching data
                    CustomerRankingCache rankingCache = (CustomerRankingCache)applicationContext.getBean("customerRankingCache");
                    CustomerRankingCache.ensureInitialized(rankingCache);
					MT4Manager.getInstance().setJmsDemoSender(jmsDemoSender);
					MT4Manager.getInstance().setJmsRealSender(jmsRealSender);
			
				}
			}									  			  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		try {
			logger.info("Context destroyed on " + new Date());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * @param iSystemPropertyManager the iSystemPropertyManager to set
	 */
	public void setiSystemPropertyManager(
			ISystemPropertyManager iSystemPropertyManager) {
		this.iSystemPropertyManager = iSystemPropertyManager;
	}

	/**
	 * @return the iSystemPropertyManager
	 */
	public ISystemPropertyManager getiSystemPropertyManager() {
		return iSystemPropertyManager;
	}

}
