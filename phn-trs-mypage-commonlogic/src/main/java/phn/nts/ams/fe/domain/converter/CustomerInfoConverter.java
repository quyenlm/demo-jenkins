package phn.nts.ams.fe.domain.converter;

import phn.com.nts.db.domain.LeaderBoardCustomer;
import phn.com.nts.db.entity.ScCustomer;
import phn.nts.ams.fe.domain.CustomerInfo;

public class CustomerInfoConverter {

	public static CustomerInfo toCustomerInfo(ScCustomer entity) {
		CustomerInfo customer = new CustomerInfo();
		customer.setCustomerId(entity.getCustomerId());
		customer.setUsername(entity.getUserName());
		customer.setFollowerNo(entity.getFollowerNo());
		//customer.setCopierNo(entity.getCopierNo());
		if(entity.getSignalTotalReturn() != null){
			customer.setReturnRate(entity.getSignalTotalReturn().toString());	
		}
		
		return customer;
	}
	
	
//	public static CustomerInfo toCustomerInfo(LeaderBoardCustomer entity){
//		CustomerInfo customer = new CustomerInfo();
//		customer.setCustomerId(entity.getCustomerId());
//		customer.setUsername(entity.getUsername());
//		customer.setFollowerNo(entity.getFollowerNo());
//		customer.setCopierNo(entity.getCopierNo());
//		customer.setBrokerCd(entity.getBrokerCd());
//		customer.setAccountId(entity.getAccountId());
//		return customer;
//		
//	}
	
}
