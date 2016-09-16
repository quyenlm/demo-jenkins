package phn.nts.ams.utils;

import java.util.List;
import java.util.ListIterator;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.business.impl.SystemPropertyManagerImpl;
import phn.nts.ams.fe.common.SystemPropertyConfig;

public class Helper {
//	public static boolean isEASocialAccount(final String group) {
//		if(group != null && group.contains("Demo"))
//			return true;
//		return false;
//	}
	
	public static boolean isTestInternalScCustomer(final String customerId) {
		if (customerId != null && SystemPropertyConfig.getListCustomerTestInternalSc().contains(customerId))
			return true;
		return false;
	}
	
	/**
	 * Check account is normal (serviceSts in (8,9,10])
	 * */
	public static boolean isNormalAccount(final Integer serviceSts) {
		if(serviceSts == null)
			return false;
		if(ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSIT_WAITING.equals(serviceSts)
				|| ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_DEPOSITED.equals(serviceSts)
				|| ITrsConstants.ACCOUNT_OPEN_STATUS.OPEN_COMPLETED_TRADED.equals(serviceSts))
			return true;
		return false;
	}
	
	public static boolean validateRequestToSC(final String customerId) {
		if (SystemPropertyConfig.getListCustomerTestInternalSc().isEmpty() || Helper.isTestInternalScCustomer(customerId))
			return true;
		
		return false;
	}
	
	public static boolean isEaGroupName(String groupName) {
		if (groupName != null && SystemPropertyManagerImpl.getEaGroupNameList().contains(groupName))
			return true;
		return false;
	}
	
	public static void listTrim(List<String> strings) {
		for (ListIterator<String> listIterator = strings.listIterator(); listIterator.hasNext();) {
			listIterator.set(listIterator.next().trim());
		}
	}
}
