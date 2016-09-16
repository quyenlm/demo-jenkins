package com.nts.ams.api.controller.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Common {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	
	public static String formatTimeStampToString(Timestamp timestamp){
		return dateFormat.format(timestamp);
	}
	
	public static String formatObjToString(Object obj){
		return obj == null ? "" : obj.toString();
	}
}
