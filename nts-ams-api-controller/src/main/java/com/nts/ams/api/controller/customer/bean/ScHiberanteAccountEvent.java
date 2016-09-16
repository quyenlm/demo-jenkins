package com.nts.ams.api.controller.customer.bean;

import java.io.Serializable;
/**
 * @description
 * @version NTS
 * @author TheLN
 * @CrDate Mar 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */

public class ScHiberanteAccountEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer accountId;
	private Integer guruAccountId;
	private Long lastTradeTime;
	
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Integer getGuruAccountId() {
		return guruAccountId;
	}
	public void setGuruAccountId(Integer guruAccountId) {
		this.guruAccountId = guruAccountId;
	}
	public Long getLastTradeTime() {
		return lastTradeTime;
	}
	public void setLastTradeTime(Long lastTradeTime) {
		this.lastTradeTime = lastTradeTime;
	}

}
