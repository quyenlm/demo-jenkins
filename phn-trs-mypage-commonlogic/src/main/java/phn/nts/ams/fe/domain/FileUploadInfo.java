package phn.nts.ams.fe.domain;

import java.io.File;
import java.io.Serializable;

/**
 * @description uploaded file info
 * @version NTS1.0
 * @author anh.nguyen.ngoc
 * @CrDate Jan 21, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class FileUploadInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer customerDocId;
	
	private File file;
	private String fileName;
	
	private String customerId;
	private Integer docType;
	private Integer docKind;
	private Integer docFileType;
	private String wlCode;
	
	private String rootPath;
	
	public Integer getCustomerDocId() {
		return customerDocId;
	}
	public void setCustomerDocId(Integer customerDocId) {
		this.customerDocId = customerDocId;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getDocType() {
		return docType;
	}
	public void setDocType(Integer docType) {
		this.docType = docType;
	}
	public Integer getDocKind() {
		return docKind;
	}
	public void setDocKind(Integer docKind) {
		this.docKind = docKind;
	}
	public Integer getDocFileType() {
		return docFileType;
	}
	public void setDocFileType(Integer docFileType) {
		this.docFileType = docFileType;
	}
	public String getWlCode() {
		return wlCode;
	}
	public void setWlCode(String wlCode) {
		this.wlCode = wlCode;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}
