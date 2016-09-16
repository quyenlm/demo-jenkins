package phn.nts.ams.fe.model;

import java.util.List;
import java.util.Map;

import phn.nts.ams.fe.domain.BoTestInfo;
import phn.nts.ams.fe.domain.BoTestResult;

public class TestBoModel extends BaseSocialModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6700761507077435721L;
	private List<BoTestInfo> listTest;
	private String customerServiceId;
	private String customerId;
	private Map<String,BoTestResult> testResult;
	private Map<String,String> textMap;
	private Map<Integer,String> mapYesNo;
	private int testToday = 0;
	private String warning;
	private String url;
	private boolean testAcceptFlag;
	private int testPoint;
	private int totalPoint;
	private int resultTest;
	private int customerServiceStatusBeforeChange;
	private int customerServiceStatusAfterChange;
	
	public boolean isTestAcceptFlag() {
		return testAcceptFlag;
	}
	public void setTestAcceptFlag(boolean testAcceptFlag) {
		this.testAcceptFlag = testAcceptFlag;
	}
	public List<BoTestInfo> getListTest() {
		return listTest;
	}
	public void setListTest(List<BoTestInfo> listTest) {
		this.listTest = listTest;
	}
	public String getCustomerServiceId() {
		return customerServiceId;
	}
	public void setCustomerServiceId(String customerServiceId) {
		this.customerServiceId = customerServiceId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Map<String, BoTestResult> getTestResult() {
		return testResult;
	}
	public void setTestResult(Map<String, BoTestResult> testResult) {
		this.testResult = testResult;
	}
	public Map<Integer, String> getMapYesNo() {
		return mapYesNo;
	}
	public void setMapYesNo(Map<Integer, String> mapYesNo) {
		this.mapYesNo = mapYesNo;
	}
	public int getTestToday() {
		return testToday;
	}
	public void setTestToday(int testToday) {
		this.testToday = testToday;
	}
	public String getWarning() {
		return warning;
	}
	public void setWarning(String warning) {
		this.warning = warning;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, String> getTextMap() {
		return textMap;
	}
	public void setTextMap(Map<String, String> textMap) {
		this.textMap = textMap;
	}
	public int getTestPoint() {
		return testPoint;
	}
	public void setTestPoint(int testPoint) {
		this.testPoint = testPoint;
	}
	public int getTotalPoint() {
		return totalPoint;
	}
	public void setTotalPoint(int totalPoint) {
		this.totalPoint = totalPoint;
	}
	public int getResultTest() {
		return resultTest;
	}
	public void setResultTest(int resultTest) {
		this.resultTest = resultTest;
	}
	public int getCustomerServiceStatusBeforeChange() {
		return customerServiceStatusBeforeChange;
	}
	public void setCustomerServiceStatusBeforeChange(int customerServiceStatusBeforeChange) {
		this.customerServiceStatusBeforeChange = customerServiceStatusBeforeChange;
	}
	public int getCustomerServiceStatusAfterChange() {
		return customerServiceStatusAfterChange;
	}
	public void setCustomerServiceStatusAfterChange(int customerServiceStatusAfterChange) {
		this.customerServiceStatusAfterChange = customerServiceStatusAfterChange;
	}
	
	
}
