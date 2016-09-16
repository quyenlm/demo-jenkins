package phn.nts.ams.fe.immutable;

import java.util.Map;

import com.google.common.collect.ImmutableMap;



public class ImmutableData {
	private ImmutableMap<String, String> mapPostDocumentLabel;
	private ImmutableMap<String, String> mapQuestionSubject;
	private ImmutableMap<Integer, String> mapPurposeBoHedgeType;
	private ImmutableMap<Integer, String> mapPurposeBoHedgeAmount;
	private ImmutableMap<Integer, String> mapPurposeBo;
	
	public ImmutableData(Map<String, String> mapQuestionSubject, Map<Integer, String> mapPurposeBoHedgeType, Map<Integer, String> mapPurposeBoHedgeAmount,Map<Integer, String> mapPurposeBo, 
			Map<String, String> mapPostDocumentLabel) {
		this.setMapQuestionSubject(new ImmutableMap.Builder<String, String>().putAll(mapQuestionSubject).build());
		this.mapPurposeBoHedgeType = new ImmutableMap.Builder<Integer, String>().putAll(mapPurposeBoHedgeType).build();
		this.mapPurposeBoHedgeAmount = new ImmutableMap.Builder<Integer, String>().putAll(mapPurposeBoHedgeAmount).build();
		this.mapPurposeBo = new ImmutableMap.Builder<Integer, String>().putAll(mapPurposeBo).build();
		this.setMapPostDocumentLabel(new ImmutableMap.Builder<String, String>().putAll(mapPostDocumentLabel).build());
	}

	public ImmutableMap<String, String> getMapPostDocumentLabel() {
		return mapPostDocumentLabel;
	}

	public void setMapPostDocumentLabel(ImmutableMap<String, String> mapPostDocumentLabel) {
		this.mapPostDocumentLabel = mapPostDocumentLabel;
	}
	
	public ImmutableMap<String, String> getMapQuestionSubject() {
		return mapQuestionSubject;
	}

	public void setMapQuestionSubject(ImmutableMap<String, String> mapQuestionSubject) {
		this.mapQuestionSubject = mapQuestionSubject;
	}

	public ImmutableMap<Integer, String> getMapPurposeBoHedgeType() {
		return mapPurposeBoHedgeType;
	}

	public void setMapPurposeBoHedgeType(
			ImmutableMap<Integer, String> mapPurposeBoHedgeType) {
		this.mapPurposeBoHedgeType = mapPurposeBoHedgeType;
	}

	public ImmutableMap<Integer, String> getMapPurposeBoHedgeAmount() {
		return mapPurposeBoHedgeAmount;
	}

	public void setMapPurposeBoHedgeAmount(
			ImmutableMap<Integer, String> mapPurposeBoHedgeAmount) {
		this.mapPurposeBoHedgeAmount = mapPurposeBoHedgeAmount;
	}

	public ImmutableMap<Integer, String> getMapPurposeBo() {
		return mapPurposeBo;
	}

	public void setMapPurposeBo(ImmutableMap<Integer, String> mapPurposeBo) {
		this.mapPurposeBo = mapPurposeBo;
	}
}
