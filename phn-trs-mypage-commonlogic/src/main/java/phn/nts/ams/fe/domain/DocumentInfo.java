package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.net.URLEncoder;

import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.log.Logit;
import phn.com.nts.util.security.Cryptography;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

public class DocumentInfo implements Serializable {
	private static Logit LOG = Logit.getInstance(DocumentInfo.class);
	private static final long serialVersionUID = -3961883003410453914L;
	private Integer customerDocId;
	private String docUrl;
	private String docFileName;
	private Integer docFileType;
	
	// [start] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
	public String getEncryptedDocUrl() {
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			String publicKey = frontUserOnline.getPublicKey();
			String privateKey = (String) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CACHE + "secret.key");
			String encryted = Cryptography.encrypt(new String(docUrl), privateKey, publicKey);
			return URLEncoder.encode(encryted, "UTF-8");
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			return null;
		}
	}
	//[end] Fix bug: A4 Insecure Direct Object References - By:DuyenNT - Date:Apr 1, 2014 - JIRA refs: #TRSPT-971
	
	public Integer getCustomerDocId() {
		return customerDocId;
	}
	public void setCustomerDocId(Integer customerDocId) {
		this.customerDocId = customerDocId;
	}
	public String getDocUrl() {
		return docUrl;
	}
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	public String getDocFileName() {
		return docFileName;
	}
	public void setDocFileName(String docFileName) {
		this.docFileName = docFileName;
	}
	public Integer getDocFileType() {
		return docFileType;
	}
	public void setDocFileType(Integer docFileType) {
		this.docFileType = docFileType;
	}
}
