package phn.nts.ams.fe.common.messages;

import java.util.HashMap;

import com.nts.components.mail.bean.AmsMailTemplateInfo;

/**
 * @description Mail to CS when change user information
 * @version NTS1.0
 * @author le.hong.ha
 * @CrDate Apr 23, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class MailToCSTemplate extends AmsMailTemplateInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String customerId;
	private String address;
	
	@Override
	public void mailContentProccessing() {
		super.mailContentProccessing();
		HashMap<String, Object> content = getContent();
		if(content == null){
			content = new HashMap<String, Object>();
		}
		content.put("CustomerName", getFullName());
		content.put("CustomerID", customerId);
		content.put("Address", address);
		content.put("mailInfo", this);
		
		setContent(content);
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
