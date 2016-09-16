package phn.nts.ams.fe.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import phn.nts.ams.fe.domain.FrontUserOnline;

/**
 * Front User Detail
 * @description
 * @version TDSBO1.0
 * @CrBy QuyTM
 * @CrDate Jul 17, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class FrontUserDetails implements UserDetails {
	
	private static final long serialVersionUID = -6733554149597036064L;
	private interface KEYS {
		final String ATTR_KEY = "FrontUser_";
	}	
	private final Map<String, Serializable> attrs = new HashMap<String, Serializable>();
	protected Collection<GrantedAuthority> roles = null;
	
	//[NTS1.0-Quan.Le.Minh]Jan 23, 2013A - Start 
	private boolean fromSigninPage;
	//[NTS1.0-Quan.Le.Minh]Jan 23, 2013A - End
	
	public FrontUserDetails(FrontUserOnline frontUserOnline, String role) {	
		addAttr(KEYS.ATTR_KEY, frontUserOnline);
		 // +++ add roles
        if (StringUtils.isNotBlank(role)) {
        	GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role);
        	if(this.roles == null) {
        		this.roles = new ArrayList<GrantedAuthority>();
        	}        	
        	this.roles.add(grantedAuthority);            
        }    
	}
	public FrontUserOnline getFrontUserOnline() {
		return (FrontUserOnline) attrs.get(KEYS.ATTR_KEY);
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		FrontUserOnline frontUserOnline = (FrontUserOnline) getAttr(KEYS.ATTR_KEY);		
		return frontUserOnline == null ? null : frontUserOnline.getPassword();
	}

	@Override
	public String getUsername() {
		FrontUserOnline frontUserOnline = (FrontUserOnline) getAttr(KEYS.ATTR_KEY);
		return frontUserOnline == null ? null : frontUserOnline.getLoginId();
	}

	@Override
	public boolean isAccountNonExpired() {		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
	
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public final boolean addAttr(String key, Serializable value) {
		if(key != null && value != null) {
			attrs.put(key, value);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public final Serializable getAttr(String key) {
		if(key != null) {
			return attrs.get(key);
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public final Set<String> getAttrKeys() {
		return attrs.keySet();
	}
	public final void addRole(String role) {
		if (StringUtils.isNotBlank(role)) {
	       	GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role);
	       	if(this.roles == null) {
	       		this.roles = new ArrayList<GrantedAuthority>();
	       	}        	
	       	this.roles.add(grantedAuthority);            
	    }  
	}
	public boolean isFromSigninPage() {
		return fromSigninPage;
	}
	public void setFromSigninPage(boolean fromSigninPage) {
		this.fromSigninPage = fromSigninPage;
	}

}
