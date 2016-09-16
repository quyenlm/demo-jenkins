/**
 * 
 */
package phn.nts.ams.fe.security;

import javax.servlet.http.HttpSessionEvent;

import phn.com.nts.util.log.Logit;

/**
 * @author tungpv
 *
 */
public class CountListener extends org.springframework.security.web.session.HttpSessionEventPublisher {
	private static Logit LOG = Logit.getInstance(CountListener.class);
//	@Override
//	public void sessionCreated(HttpSessionEvent arg0) {
//		LOG.info("#############sessionCreated count Session="+CountTask.getCount());
////		CountTask.setCount(CountTask.getCount()+1);
//	}
//	/* (non-Javadoc)
//	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
//	 */
//	@Override
//	public void sessionDestroyed(HttpSessionEvent arg0) {
//		LOG.info("#############before sessionDestroyed count Session="+CountTask.getCount());
//		if(CountTask.getCount()>0){
//			CountTask.setCount(CountTask.getCount()-1);
//		}
//		LOG.info("#############after sessionDestroyed count Session="+CountTask.getCount());
//	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.session.HttpSessionEventPublisher#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
//		super.sessionCreated(event);
		CountTask.count=CountTask.count+1;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.session.HttpSessionEventPublisher#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
//		super.sessionDestroyed(event);
		LOG.info("#############before sessionDestroyed count Session="+CountTask.count);
		if(CountTask.count>0){
			CountTask.count=CountTask.count-1;
		}
		LOG.info("#############after sessionDestroyed count Session="+CountTask.count);
	}

//	@Override
//	public void sessionDestroyed(HttpSessionEvent arg0) {
//		LOG.info("#############before sessionDestroyed count Session="+CountTask.getCount());
//		if(CountTask.getCount()>0){
//			CountTask.setCount(CountTask.getCount()-1);
//		}
//		LOG.info("#############after sessionDestroyed count Session="+CountTask.getCount());
//	}
}
