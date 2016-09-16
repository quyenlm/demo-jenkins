
package phn.nts.ams.fe.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;




/**
 * @description Web User Detail for spring security config
 * @version TDSBO1.0
 * @CrBy QuyTM
 * @CrDate Jul 23, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class FrontUserDetailsService implements IFrontUserDetailsService, InitializingBean {
    Logit logger = Logit.getInstance(FrontUserDetailsService.class);

	private IAccountManager accountManager;

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}
	public IAccountManager getAccountManager() {
		return accountManager;
	}
	/**
	 * 　
	 * orveride load user by Username
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Jul 23, 2012
	 * @MdDate
	 */
	
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    	logger.info("[start]load user by user name : " + userName);
        long startTime = System.currentTimeMillis();
    	FrontUserDetails user = null;
    	try {
//    		synchronized (accountManager) {
			user = accountManager.getUserDetail(userName);
    		if (user == null) {
    			logger.info("User not found");
                throw new UsernameNotFoundException("User not found!!!");
            } else {
            	logger.info("found user in db");
            }
//			}
    		
        } catch (UsernameNotFoundException ufe) {
            throw ufe;
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found!!!", e);
        }
    	System.out.println("USER: "+user.getUsername());
    	System.out.println("SUCCESS ");
//    	CountTask.setCount(CountTask.getCount()+1);
//    	System.out.println(CountTask.getCount());
//        System.out.println( CountTask.getCount());
    	//[NTS1.0-Quan.Le.Minh]Jan 23, 2013A - Start 
    	user.setFromSigninPage(true);
    	//[NTS1.0-Quan.Le.Minh]Jan 23, 2013A - End

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("[loadUserByUsername] total time in milliseconds for processing login: " + totalTime);

        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see kr.ac.edu.lab.web.security.IWebUserDetailsService#reloadUserByUsername(java.lang.String)
     */
    public void reloadUserByUsername(String userName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        //  Auto-generated method stub
    }
}
