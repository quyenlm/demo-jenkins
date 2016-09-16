package phn.nts.ams.fe.domain;

import java.sql.Timestamp;

import phn.com.nts.db.entity.ScCustomer;

public class FollowInfo {
	private Integer followId;
	private String customerId;
	private String followCustomerId;
	private Timestamp followDatetime;
	/**
	 * @return the followId
	 */
	public Integer getFollowId() {
		return followId;
	}
	/**
	 * @param followId the followId to set
	 */
	public void setFollowId(Integer followId) {
		this.followId = followId;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the followCustomerId
	 */
	public String getFollowCustomerId() {
		return followCustomerId;
	}
	/**
	 * @param followCustomerId the followCustomerId to set
	 */
	public void setFollowCustomerId(String followCustomerId) {
		this.followCustomerId = followCustomerId;
	}
	/**
	 * @return the followDatetime
	 */
	public Timestamp getFollowDatetime() {
		return followDatetime;
	}
	/**
	 * @param followDatetime the followDatetime to set
	 */
	public void setFollowDatetime(Timestamp followDatetime) {
		this.followDatetime = followDatetime;
	}
	
}
