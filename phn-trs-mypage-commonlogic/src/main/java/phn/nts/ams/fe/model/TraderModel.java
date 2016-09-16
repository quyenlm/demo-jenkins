package phn.nts.ams.fe.model;

import java.util.List;

import com.nts.common.exchange.social.ScCustomerInfo;



import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.common.SearchResult;
import phn.com.nts.db.domain.CopierCustomerInfo;
import phn.com.nts.db.domain.FollowerCustomerInfo;
import phn.com.nts.db.domain.TraderServiceInfo;
import phn.nts.ams.fe.domain.CopyFollowInfo;
import phn.nts.ams.fe.domain.CopyListModel;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.DemoContestAccountInfo;
import phn.nts.ams.fe.domain.FollowListModel;


public class TraderModel extends BaseSocialModel {
	private CopyListModel copyListModel = new CopyListModel();
    private FollowListModel followListModel = new FollowListModel();
	private ChangeFundsModel changeFundModel = new ChangeFundsModel();
    private CustomerInfo customerInfo;
    private List<CustomerInfo> copierList;
    private PagingInfo pagingInfo;
    private List <TraderServiceInfo> listAccountOfTrader;
    private Integer copierNo;
    private List<CopierCustomerInfo> listAccountInfo;
	private String accountOfTraderSelected;
	private Integer followerNo;
	private SearchResult<CustomerInfo> followerList;
	
	public void setFollowerList(SearchResult<CustomerInfo> followerList) {
		this.followerList = followerList;
	}
	
	
	
    
	public List<CustomerInfo> getFollowerList() {
		return followerList;
	}



	public String getAccountOfTraderSelected() {
		return accountOfTraderSelected;
	}

	public void setAccountOfTraderSelected(String accountOfTraderSelected) {
		this.accountOfTraderSelected = accountOfTraderSelected;
	}

	public PagingInfo getPagingInfo() {
		return pagingInfo;
	}

	public void setPagingInfo(PagingInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	public ChangeFundsModel getChangeFundModel() {
		return changeFundModel;
	}

	public void setChangeFundModel(ChangeFundsModel changeFundModel) {
		this.changeFundModel = changeFundModel;
	}


	public CopyListModel getCopyListModel() {
		return copyListModel;
	}

	public void setCopyListModel(CopyListModel copyListModel) {
		this.copyListModel = copyListModel;
	}

	/**
	 * @return the customerInfo
	 */
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	/**
	 * @return the copierList
	 */
	public List<CustomerInfo> getCopierList() {
		return copierList;
	}

	/**
	 * @param copierList the copierList to set
	 */
	public void setCopierList(List<CustomerInfo> copierList) {
		this.copierList = copierList;
	}

	/**
	 * @return the listAccountOfTrader
	 */
	public List <TraderServiceInfo> getListAccountOfTrader() {
		return listAccountOfTrader;
	}

	/**
	 * @param listAccountOfTrader the listAccountOfTrader to set
	 */
	public void setListAccountOfTrader(List <TraderServiceInfo> listAccountOfTrader) {
		this.listAccountOfTrader = listAccountOfTrader;
	}

	/**
	 * @return the copierNo
	 */
	public Integer getCopierNo() {
		return copierNo;
	}

	/**
	 * @param copierNo the copierNo to set
	 */
	public void setCopierNo(Integer copierNo) {
		this.copierNo = copierNo;
	}

	public List<CopierCustomerInfo> getListAccountInfo() {
		return listAccountInfo;
	}

	public void setListAccountInfo(List<CopierCustomerInfo> listAccountInfo) {
		this.listAccountInfo = listAccountInfo;
	}

	   

    public FollowListModel getFollowListModel() {
        return followListModel;
    }

    public void setFollowListModel(FollowListModel followListModel) {
        this.followListModel = followListModel;
    }

	public Integer getFollowerNo() {
		return followerNo;
	}

	public void setFollowerNo(Integer followerNo) {
		this.followerNo = followerNo;
	}



}
