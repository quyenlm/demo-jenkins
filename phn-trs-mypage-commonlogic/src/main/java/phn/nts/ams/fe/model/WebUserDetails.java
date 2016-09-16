package phn.nts.ams.fe.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @description
 * @version TDSBO1.0
 * @CrBy Nguyen.Xuan.Bach
 * @CrDate Feb 21, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public abstract class WebUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;
	private final Map<String, Serializable> attrs = new HashMap<String, Serializable>();
	protected Collection<GrantedAuthority> roles = null;
	
	private String wlName;
	private Integer pattern;
	private String wlCode;
	private Integer userRole;
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
	
	/**
	 * 
	 * @return
	 */
	public abstract String getUserName();
	
    public Collection<GrantedAuthority> getAuthorities() {
        return roles;
    }

	public String getWlName() {
		return wlName;
	}

	public void setWlName(String wlName) {
		this.wlName = wlName;
	}

	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

	public String getWlCode() {
		return wlCode;
	}

	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}
}
