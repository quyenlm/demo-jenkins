package com.nts.ams.api.controller.common;

public enum AmsApiMode {
	CUSTOMER_INFO(1),
	TRANSFER(2);
	
	private int number;
	
	AmsApiMode(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public static AmsApiMode valueOf(int mode) {
		if(mode == 1)
			return CUSTOMER_INFO;
		if(mode == 2)
			return TRANSFER;
		return null;
	}
}
