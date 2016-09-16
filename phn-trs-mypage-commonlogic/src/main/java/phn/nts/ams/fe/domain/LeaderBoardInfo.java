package phn.nts.ams.fe.domain;

import java.util.List;

import phn.com.nts.db.domain.LeaderBoardCustomer;

public class LeaderBoardInfo {

	 private boolean guestMode;
	 private String ajaxMsg;
	 private boolean ajaxSuccess;
	 private List<LeaderBoardCustomer> listLeaderBoardCustomer;
	
	 
	 
	public boolean isGuestMode() {
		return guestMode;
	}
	public void setGuestMode(boolean guestMode) {
		this.guestMode = guestMode;
	}
	public String getAjaxMsg() {
		return ajaxMsg;
	}
	public void setAjaxMsg(String ajaxMsg) {
		this.ajaxMsg = ajaxMsg;
	}
	public boolean isAjaxSuccess() {
		return ajaxSuccess;
	}
	public void setAjaxSuccess(boolean ajaxSuccess) {
		this.ajaxSuccess = ajaxSuccess;
	}
	public List<LeaderBoardCustomer> getListLeaderBoardCustomer() {
		return listLeaderBoardCustomer;
	}
	public void setListLeaderBoardCustomer(
			List<LeaderBoardCustomer> listLeaderBoardCustomer) {
		this.listLeaderBoardCustomer = listLeaderBoardCustomer;
	}
	 
	 
}
