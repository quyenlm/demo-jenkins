
package phn.nts.ams.fe.model;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.CountTask;




/**
 * @description
 * @version TDSBO1.0
 * @CrBy Nguyen.Xuan.Bach
 * @CrDate Feb 22, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
@SuppressWarnings("deprecation")
public class AppWebUserDetails extends WebUserDetails {

	private static final long serialVersionUID = 1L;
	public AppWebUserDetails(FrontUserOnline FrontUserOnline, String role) {
        if (FrontUserOnline != null) {
        	super.addAttr(Keys.USER_ATTR, FrontUserOnline);
            if (role != null && role.trim().length() != 0) {
//            	this.roles = new GrantedAuthorityImpl[1];
//                int index = 0;
//                roles[0] = new GrantedAuthorityImpl(role);
				GrantedAuthority firstRole = new GrantedAuthorityImpl(role);
            	roles = new ArrayList<GrantedAuthority>();
            	roles.add(firstRole);
            }
        }
    }

	public interface Keys {
		String USER_ATTR = "USER";
	}
    /**
     * 
     * @return
     */
    public FrontUserOnline getUser() {
    	try {
    		return (FrontUserOnline)this.getAttr(Keys.USER_ATTR);
    	} catch (Exception e) {
    		return null;
    	}
    }
    
	public String getPassword() {
		FrontUserOnline FrontUserOnline = this.getUser();
		return (FrontUserOnline != null ? FrontUserOnline.getPassword() : null);
	}

	public String getUsername() {
		FrontUserOnline FrontUserOnline = this.getUser();
		return (FrontUserOnline != null ? FrontUserOnline.getUserName() : null);
	}

	public boolean isAccountNonExpired() {
		boolean isNonExpired = true;

        return isNonExpired;
	}

	public boolean isAccountNonLocked() {
		
		boolean nonLocked = true;
		
        return nonLocked;
	}

	public boolean isCredentialsNonExpired() {
		 return true;
	}

	public boolean isEnabled() {
		
		boolean enable = true;
		
		return enable;
	}


	@Override
	public String getUserName() {
		//  Auto-generated method stub
		return null;
	}
}
