package phn.nts.ams.fe.domain;

import phn.com.nts.util.common.IConstants;

/**
 * @description ExchangerSymbolInfo
 * @version NTS1.0
 * @author Nguyen.Manh.Thang
 * @CrDate Sep 18, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class ExchangerSymbolInfo {
	
	private Integer exchangerSymbolId;
	private String exchangerId;
	private String symbolCd;
	private String orginalSellRate;
	private String orginalBuyRate;
	private String sellRate;
	private String buyRate;
	private String updateDate;
	private Integer isUpdate = IConstants.ACTIVE_FLG.INACTIVE;
	
	public String getSymbolCd() {
		return symbolCd;
	}
	public void setSymbolCd(String symbolCd) {
		this.symbolCd = symbolCd;
	}
	public String getSellRate() {
		return sellRate;
	}
	public void setSellRate(String sellRate) {
		this.sellRate = sellRate;
	}
	public String getBuyRate() {
		return buyRate;
	}
	public void setBuyRate(String buyRate) {
		this.buyRate = buyRate;
	}
	public String getExchangerId() {
		return exchangerId;
	}
	public void setExchangerId(String exchangerId) {
		this.exchangerId = exchangerId;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getExchangerSymbolId() {
		return exchangerSymbolId;
	}
	public void setExchangerSymbolId(Integer exchangerSymbolId) {
		this.exchangerSymbolId = exchangerSymbolId;
	}
	public Integer getIsUpdate() {
		return isUpdate;
	}
	public void setIsUpdate(Integer isUpdate) {
		this.isUpdate = isUpdate;
	}
	public String getOrginalSellRate() {
		return orginalSellRate;
	}
	public void setOrginalSellRate(String orginalSellRate) {
		this.orginalSellRate = orginalSellRate;
	}
	public String getOrginalBuyRate() {
		return orginalBuyRate;
	}
	public void setOrginalBuyRate(String orginalBuyRate) {
		this.orginalBuyRate = orginalBuyRate;
	}
	
}
