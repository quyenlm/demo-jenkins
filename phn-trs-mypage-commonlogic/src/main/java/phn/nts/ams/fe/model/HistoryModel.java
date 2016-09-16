package phn.nts.ams.fe.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import phn.com.nts.ams.web.condition.AmsFeHistorySearchCondition;
import phn.com.nts.db.entity.AmsViewFeSearchHistory;

public class HistoryModel extends BaseSocialModel {
	AmsFeHistorySearchCondition amsFeHistorySearchCondition;
	List<AmsFeHistorySearchCondition> listAmsTransactionHistory;
	String transactionId = null;
	String transactionType = null;
	Map<String, String> mapPaymentMethod;
	Map<String, String> mapTransactionMethod;
	Map<String, String> mapDepositStatus;
	Map<String, String> mapCustomerType;
	Map<String, String> mapCashBackStatus;
	private String pattern;
	
	/**
	 * @return the condition to get list of history
	 */
	public AmsFeHistorySearchCondition getAmsFeHistorySearchCondition() {
		return amsFeHistorySearchCondition;
	}

	/**
	 * @param amsFeHistorySearchCondition set list of history
	 */
	public void setAmsFeHistorySearchCondition(
			AmsFeHistorySearchCondition amsFeHistorySearchCondition) {
		this.amsFeHistorySearchCondition = amsFeHistorySearchCondition;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Map<String, String> getMapPaymentMethod() {
		return mapPaymentMethod;
	}

	public void setMapPaymentMethod(Map<String, String> mapPaymentMethod) {
		this.mapPaymentMethod = mapPaymentMethod;
	}

	public Map<String, String> getMapTransactionMethod() {
		return mapTransactionMethod;
	}

	public void setMapTransactionMethod(Map<String, String> mapTransactionMethod) {
		this.mapTransactionMethod = mapTransactionMethod;
	}

	public Map<String, String> getMapDepositStatus() {
		return mapDepositStatus;
	}

	public void setMapDepositStatus(Map<String, String> mapDepositStatus) {
		this.mapDepositStatus = mapDepositStatus;
	}

	public Map<String, String> getMapCustomerType() {
		return mapCustomerType;
	}

	public void setMapCustomerType(Map<String, String> mapCustomerType) {
		this.mapCustomerType = mapCustomerType;
	}

	public Map<String, String> getMapCashBackStatus() {
		return mapCashBackStatus;
	}

	public void setMapCashBackStatus(Map<String, String> mapCashBackStatus) {
		this.mapCashBackStatus = mapCashBackStatus;
	}

	public List<AmsFeHistorySearchCondition> getListAmsTransactionHistory() {
		return listAmsTransactionHistory;
	}

	public void setListAmsTransactionHistory(
			List<AmsFeHistorySearchCondition> listAmsTransactionHistory) {
		this.listAmsTransactionHistory = listAmsTransactionHistory;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
