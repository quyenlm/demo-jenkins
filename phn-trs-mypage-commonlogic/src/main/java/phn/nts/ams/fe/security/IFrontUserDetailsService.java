
package phn.nts.ams.fe.security;

import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * 　
 * Interface for Front User Detail Services
 * @param
 * @return
 * @auth QuyTM
 * @CrDate Jul 24, 2012
 * @MdDate
 */
public interface IFrontUserDetailsService extends UserDetailsService {
    /**
     * 
     * @param userName
     */
    public void reloadUserByUsername(String userName);
}
