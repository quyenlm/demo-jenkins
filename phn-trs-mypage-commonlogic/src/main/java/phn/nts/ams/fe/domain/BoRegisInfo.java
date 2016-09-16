package phn.nts.ams.fe.domain;

import java.util.List;

public class BoRegisInfo {
	
	private List<Integer> listPurposeBo ;
	private Integer purposeBoHedgeType;
	private Integer purposeBoHedgeAmount;
	private String maxLossAmountBo;
	
	
	public List<Integer> getListPurposeBo() {
		return listPurposeBo;
	}
	public void setListPurposeBo(List<Integer> listPurposeBo) {
		this.listPurposeBo = listPurposeBo;
	}
	public Integer getPurposeBoHedgeType() {
		return purposeBoHedgeType;
	}
	public void setPurposeBoHedgeType(Integer purposeBoHedgeType) {
		this.purposeBoHedgeType = purposeBoHedgeType;
	}
	public Integer getPurposeBoHedgeAmount() {
		return purposeBoHedgeAmount;
	}
	public void setPurposeBoHedgeAmount(Integer purposeBoHedgeAmount) {
		this.purposeBoHedgeAmount = purposeBoHedgeAmount;
	}
	public String getMaxLossAmountBo() {
		return maxLossAmountBo;
	}
	public void setMaxLossAmountBo(String maxLossAmountBo) {
		this.maxLossAmountBo = maxLossAmountBo;
	}
	
	

}
