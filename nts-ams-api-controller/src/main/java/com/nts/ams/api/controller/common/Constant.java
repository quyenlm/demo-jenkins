package com.nts.ams.api.controller.common;

public class Constant {
	//Special regex for validate input data (name, address)
	public static final String REGEX_SPECIAL = "[~`!#$%\\^&*+=@\\-\\[\\]\\';,/{}|:\\?\"<>]";
	public static final Integer SERVICE_AMS = 0;
	public static final Integer ENABLE_MT4_FX = 1;
	public static String BJP_DEPOSIT = "BJP_DEPOSIT";
	public static final String ORDER_TYPE_BUY = "買";
	public static final String ORDER_TYPE_SELL = "売";
	
	public static final String TRADE_TYPE_OPEN = "新規";
	public static final String TRADE_TYPE_CLOSE = "決済";
	
	public static final String CURRENT_TIME_MONTH = "月";
	public static final String CURRENT_TIME_DAY = "日";
	
	public static final String NOT_SEND_MAIL = "0";
}