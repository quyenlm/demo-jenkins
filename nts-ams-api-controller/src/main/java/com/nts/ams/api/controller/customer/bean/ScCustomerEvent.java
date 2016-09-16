package com.nts.ams.api.controller.customer.bean;

import java.io.Serializable;
import java.util.List;

import cn.nextop.social.api.admin.proxy.model.customer.CustomerConfig;

/**
 * @description
 * @version NTS
 * @author TheLN
 * @CrDate Mar 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ScCustomerEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private List<Integer> copierIds;
	private CustomerConfig customerConfig;
	
	public List<Integer> getCopierIds() {
		return copierIds;
	}
	public void setCopierIds(List<Integer> copierIds) {
		this.copierIds = copierIds;
	}
	public CustomerConfig getCustomerConfig() {
		return customerConfig;
	}
	public void setCustomerConfig(CustomerConfig customerConfig) {
		this.customerConfig = customerConfig;
	}
	
	@Override
	public String toString() {
		return "ScCustomerEvent [copierIds=" + copierIds + ", customerConfig="
				+ customerConfig + "]";
	}
	
}
