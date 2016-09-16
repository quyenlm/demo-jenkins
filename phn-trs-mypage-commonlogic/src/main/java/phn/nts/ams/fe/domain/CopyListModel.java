package phn.nts.ams.fe.domain;

import java.util.ArrayList;
import java.util.List;

import phn.com.nts.db.domain.CopiedScCustomer;
import phn.com.nts.util.common.IConstants;

public class CopyListModel {
	private String customerId;
	private List<CopiedScCustomer> copiedCustomersInChart = new ArrayList<CopiedScCustomer>();
	private List<CopiedScCustomer> copiedCustomers = new ArrayList<CopiedScCustomer>();
	private CopyChartInfo chartInfo = new CopyChartInfo();
	private Integer numberOfGuru;
	private String currentUserName;
	private String stopCopyCustomerId;
	private String stopCopyAccountId;
	private String stopCopyBrokerCd;
	private Integer stopCopyResult;
	private String message;
	private Integer copyListCount;
	
	public String getStopCopyAccountId() {
		return stopCopyAccountId;
	}

	public void setStopCopyAccountId(String stopCopyAccountId) {
		this.stopCopyAccountId = stopCopyAccountId;
	}

	public String getStopCopyBrokerCd() {
		return stopCopyBrokerCd;
	}

	public void setStopCopyBrokerCd(String stopCopyBrokerCd) {
		this.stopCopyBrokerCd = stopCopyBrokerCd;
	}

	public List<CopiedScCustomer> getCopiedCustomersInChart() {
		return copiedCustomersInChart;
	}

	public void setCopiedCustomersInChart(
			List<CopiedScCustomer> copiedCustomersInChart) {
		this.copiedCustomersInChart = copiedCustomersInChart;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Integer getCopyListCount() {
		return copyListCount;
	}

	public void setCopyListCount(Integer copyListCount) {
		this.copyListCount = copyListCount;
	}

	public Integer getStopCopyResult() {
		return stopCopyResult;
	}

	public void setStopCopyResult(Integer stopCopyResult) {
		this.stopCopyResult = stopCopyResult;
	}

	public String getStopCopyCustomerId() {
		return stopCopyCustomerId;
	}

	public void setStopCopyCustomerId(String stopCopyCustomerId) {
		this.stopCopyCustomerId = stopCopyCustomerId;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public Integer getNumberOfGuru() {
		return numberOfGuru;
	}

	public void setNumberOfGuru(Integer numberOfGuru) {
		this.numberOfGuru = numberOfGuru;
	}

	public CopyChartInfo getChartInfo() {
		return chartInfo;
	}

	public void setChartInfo(CopyChartInfo chartInfo) {
		this.chartInfo = chartInfo;
	}

	public List<CopiedScCustomer> getCopiedCustomers() {
		return copiedCustomers;
	}

	public void setCopiedCustomers(List<CopiedScCustomer> copiedCustomers) {
		this.copiedCustomers = copiedCustomers;
	}
	
}
