/**
 * 
 */
package com.nts.ams.api.controller.util;
import com.nts.ams.api.controller.service.AmsApiControllerMng;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 7, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class Helper {
	public static String getHeader(String id) {
		return "[RequestId=" + id + "] - ";
	}
	
	/**
	 * Check RpcProto version
	 * */
	public static boolean checkProtoVersion(String comVersion) {
		return AmsApiControllerMng.getConfiguration().getAmsApiProtoVersion().equals(comVersion);
	}
}