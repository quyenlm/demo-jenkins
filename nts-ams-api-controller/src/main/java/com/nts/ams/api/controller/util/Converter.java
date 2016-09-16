package com.nts.ams.api.controller.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import phn.com.nts.db.entity.AmsMessage;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.com.nts.util.common.StringUtil;
import com.nts.ams.api.controller.common.Common;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AllowedFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsBalanceInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsNewsInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoInvestmentPurpose;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeAmount;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.BoPurposeHedgeType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ConfirmFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.CorporationType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.CustomerServiceStatus;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.FinancialAssets;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.InvestmentPurpose;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageCategory;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageKind;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.MessageType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReadFlag;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceTypeInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsCustomerBankInfo;
import com.nts.common.exchange.proto.ams.internal.AmsTransactionModel.AmsWithdrawalTransactionInfo;

/**
 * @description Convert bean to bean
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class Converter {
	/**
	 * Convert AmsCustomerInfo to CustomerInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static AmsCustomerInfo convertCustomerInfo(CustomerInfo customerInfo) {
		if(customerInfo == null)
			return null;
		
		AmsCustomerInfo.Builder builder = AmsCustomerInfo.newBuilder();
		
		// Common
		if (customerInfo.getCustomerId() != null)
			builder.setCustomerId(customerInfo.getCustomerId());
		if (customerInfo.getNtdCustomerId() != null)
			builder.setNtdCustomerId(customerInfo.getNtdCustomerId());
		if (customerInfo.getZipcode() != null)
			builder.setZipCode(customerInfo.getZipcode());
		if (customerInfo.getPrefecture() != null)
			builder.setPrefecture(customerInfo.getPrefecture());
		if (customerInfo.getCity() != null)
			builder.setCity(customerInfo.getCity());
		if (customerInfo.getSection() != null)
			builder.setSection(customerInfo.getSection());
		if (customerInfo.getAddress() != null)
			builder.setAddress(customerInfo.getAddress());
		if (customerInfo.getBuildingName() != null)
			builder.setBuildingName(customerInfo.getBuildingName());
		if (customerInfo.getTel1() != null)
			builder.setPhone(customerInfo.getTel1());
		if (customerInfo.getTel2() != null)
			builder.setPhone2(customerInfo.getTel2());
		if (customerInfo.getMailMain() != null)
			builder.setEmail(customerInfo.getMailMain());
		builder.setIsEaAccount(customerInfo.isEaAccount() ? 1 : 0);
		//[TRSM1-2175-quyen.le.manh]Feb 3, 2016M - Start - get additional mail for 2 case Individual = AMS_CUTOMER.MAIL_ADDTIONAL, Corporation = AMS_CUTOMER.CORP_PIC_MAIL_MOBILE
		if(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER == customerInfo.getCorporationType()) {
			if (customerInfo.getMailAddtional() != null)
				builder.setAdditionalEmail(customerInfo.getMailAddtional());
		} else {
			if (customerInfo.getCorpPicMailMobile() != null)
				builder.setAdditionalEmail(customerInfo.getCorpPicMailMobile());
		}
		//[TRSM1-2175-quyen.le.manh]Feb 3, 2016M - End
		
		if (customerInfo.getWlCode() != null)
			builder.setWlCode(customerInfo.getWlCode());
		if (customerInfo.getDescription() != null)
			builder.setDescription(customerInfo.getDescription());
		if (customerInfo.getAccountApplicationDate() != null)
			builder.setApplicationDate(customerInfo.getAccountApplicationDate());
		
		if (customerInfo.getLoginId() != null)
			builder.setLoginId(customerInfo.getLoginId());
		// Pass
		if (customerInfo.getLoginPass() != null)
			builder.setPassword(customerInfo.getLoginPass());
		
		// Survey/InvestmentPurpose
		if(customerInfo.getFinancilAssets() != null)
			builder.setFinancialAssets(convertFinancialAssets(customerInfo.getFinancilAssets()));
		InvestmentPurpose.Builder purposeBuilder = InvestmentPurpose.newBuilder();
		purposeBuilder.setPurposeShortTermFlg(convertFlg(customerInfo.isPurposeShortTermFlg()) + "");
		purposeBuilder.setPurposeLongTermFlg(convertFlg(customerInfo.isPurposeLongTermFlg()) + "");
		purposeBuilder.setPurposeExchangeFlg(convertFlg(customerInfo.isPurposeExchangeFlg()) + "");
		purposeBuilder.setPurposeSwapFlg(convertFlg(customerInfo.isPurposeSwapFlg()) + "");
		purposeBuilder.setPurposeHedgeAssetFlg(convertFlg(customerInfo.isPurposeHedgeAssetFlg()) + "");
		purposeBuilder.setPurposeHighIntFlg(convertFlg(customerInfo.isPurposeHighIntFlg()) + "");
		purposeBuilder.setPurposeEconomicFlg(convertFlg(customerInfo.isPurposeEconomicFlg()) + "");
		builder.setInvestment(purposeBuilder);
		
		//BoInvestmentPurpose
		BoInvestmentPurpose.Builder boPurpose = BoInvestmentPurpose.newBuilder();
		boPurpose.setBoPurposeShortTermFlg(String.valueOf(convertFlg(customerInfo.isBoPurposeShortTermFlg())));
		boPurpose.setBoPurposeDispAssetMngFlg(String.valueOf(convertFlg(customerInfo.isBoPurposeDispAssetMngFlg())));
		boPurpose.setBoPurposeHedgeFlg(String.valueOf(convertFlg(customerInfo.isBoPurposeHedgeFlg())));
		builder.setBoInvestmentPurpose(boPurpose);
		
		if(customerInfo.getBoPurposeHedgeType() != null && customerInfo.getBoPurposeHedgeType().intValue() != -1)
			builder.setBoPurposeHedgeType(BoPurposeHedgeType.valueOf(customerInfo.getBoPurposeHedgeType()));
		if(customerInfo.getBoPurposeHedgeAmount() != null && customerInfo.getBoPurposeHedgeAmount().intValue() != -1)
			builder.setBoPurposeHedgeAmount(BoPurposeHedgeAmount.valueOf(customerInfo.getBoPurposeHedgeAmount()));
		if(customerInfo.getBoMaxLossAmount() != null)
			builder.setBoLossMaxAmount(String.valueOf(customerInfo.getBoMaxLossAmount()));
		
		// CorporationType
		builder.setCorporationType(CorporationType.valueOf(customerInfo.getCorporationType()));
		
		if(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER == customerInfo.getCorporationType()) {
			// Individual customer
			if (customerInfo.getFullName() != null)
				builder.setFullName(customerInfo.getFullName());
			if (customerInfo.getFirstName() != null)
				builder.setFirstName(customerInfo.getFirstName());
			if (customerInfo.getLastName() != null)
				builder.setLastName(customerInfo.getLastName());
			if (customerInfo.getFirstNameKana() != null)
				builder.setFirstNameKana(customerInfo.getFirstNameKana());
			if (customerInfo.getLastNameKana() != null)
				builder.setLastNameKana(customerInfo.getLastNameKana());
			if (customerInfo.getSex() != null)
				builder.setGender(customerInfo.getSex() + "");
			if (customerInfo.getBirthday() != null)
				builder.setBirthday(customerInfo.getBirthday());
		} else {
			// CorporationType
			//CorpRepName
			if (customerInfo.getCorpRepFirstname() != null)
				builder.setRepFirstName(customerInfo.getCorpRepFirstname());
			if (customerInfo.getCorpRepLastname() != null)
				builder.setRepLastName(customerInfo.getCorpRepLastname());
			if (customerInfo.getCorpRepFirstnameKana() != null)
				builder.setRepFirstNameKana(customerInfo.getCorpRepFirstnameKana());
			if (customerInfo.getCorpRepLastnameKana() != null)
				builder.setRepLastNameKana(customerInfo.getCorpRepLastnameKana());			
			
			if (customerInfo.getCorpRepFirstname() != null || customerInfo.getCorpRepLastname() != null)
				builder.setRepresentative(customerInfo.getCorpRepFirstname() + " " + customerInfo.getCorpRepLastname());
			if(customerInfo.getCorpRepFullname() != null)
				builder.setRepFullName(customerInfo.getCorpRepFullname());
			if(customerInfo.getCorpRepFullnameKana() != null)
				builder.setRepFullNameKana(customerInfo.getCorpRepFullnameKana());
			
			//CorpName
			if (customerInfo.getCorpFullname() != null)
				builder.setCorporationName(customerInfo.getCorpFullname());
			if (customerInfo.getCorpFullnameKana() != null)
				builder.setCorporationNameKana(customerInfo.getCorpFullnameKana());

			// BeneficOwner1
			if (customerInfo.getBeneficOwnerFlg() != null)
				builder.setBeneficOwnerFlg(String.valueOf(customerInfo.getBeneficOwnerFlg()));
			
			if(customerInfo.getBeneficOwnerFullname() != null)
				builder.setBeneficOwnerFullname(customerInfo.getBeneficOwnerFullname());
			if(customerInfo.getBeneficOwnerFullnameKana() != null)
				builder.setBeneficOwnerFullnameKana(customerInfo.getBeneficOwnerFullnameKana());
			
			if(customerInfo.getBeneficOwnerEstablishDate() != null)
				builder.setBeneficOwnerEstablishDate(customerInfo.getBeneficOwnerEstablishDate());
			if(customerInfo.getBeneficOwnerZipcode() != null)
				builder.setBeneficOwnerZipcode(customerInfo.getBeneficOwnerZipcode());
			
			if(customerInfo.getBeneficOwnerPrefecture() != null)
				builder.setBeneficOwnerPrefecture(customerInfo.getBeneficOwnerPrefecture());
			if(customerInfo.getBeneficOwnerCity() != null)
				builder.setBeneficOwnerCity(customerInfo.getBeneficOwnerCity());
			if(customerInfo.getBeneficOwnerSection() != null)
				builder.setBeneficOwnerSection(customerInfo.getBeneficOwnerSection());
			
			if(customerInfo.getBeneficOwnerBuildingName() != null)
				builder.setBeneficOwnerBuildingName(customerInfo.getBeneficOwnerBuildingName());
			if(customerInfo.getBeneficOwnerTel() != null)
				builder.setBeneficOwnerTel(customerInfo.getBeneficOwnerTel());

			// BeneficOwner2
			if (customerInfo.getBeneficOwnerFlg2() != null)
				builder.setBeneficOwnerFlg2(String.valueOf(customerInfo.getBeneficOwnerFlg2()));

			if(customerInfo.getBeneficOwnerFullname2() != null)
				builder.setBeneficOwnerFullname2(customerInfo.getBeneficOwnerFullname2());
			if(customerInfo.getBeneficOwnerFullnameKana2() != null)
				builder.setBeneficOwnerFullnameKana2(customerInfo.getBeneficOwnerFullnameKana2());

			if(customerInfo.getBeneficOwnerEstablishDate2() != null)
				builder.setBeneficOwnerEstablishDate2(customerInfo.getBeneficOwnerEstablishDate2());
			if(customerInfo.getBeneficOwnerZipcode2() != null)
				builder.setBeneficOwnerZipcode2(customerInfo.getBeneficOwnerZipcode2());

			if(customerInfo.getBeneficOwnerPrefecture2() != null)
				builder.setBeneficOwnerPrefecture2(customerInfo.getBeneficOwnerPrefecture2());
			if(customerInfo.getBeneficOwnerCity2() != null)
				builder.setBeneficOwnerCity2(customerInfo.getBeneficOwnerCity2());
			if(customerInfo.getBeneficOwnerSection2() != null)
				builder.setBeneficOwnerSection2(customerInfo.getBeneficOwnerSection2());

			if(customerInfo.getBeneficOwnerBuildingName2() != null)
				builder.setBeneficOwnerBuildingName2(customerInfo.getBeneficOwnerBuildingName2());
			if(customerInfo.getBeneficOwnerTel2() != null)
				builder.setBeneficOwnerTel2(customerInfo.getBeneficOwnerTel2());

			// BeneficOwner3
			if (customerInfo.getBeneficOwnerFlg3() != null)
				builder.setBeneficOwnerFlg3(String.valueOf(customerInfo.getBeneficOwnerFlg3()));

			if(customerInfo.getBeneficOwnerFullname3() != null)
				builder.setBeneficOwnerFullname3(customerInfo.getBeneficOwnerFullname3());
			if(customerInfo.getBeneficOwnerFullnameKana3() != null)
				builder.setBeneficOwnerFullnameKana3(customerInfo.getBeneficOwnerFullnameKana3());

			if(customerInfo.getBeneficOwnerEstablishDate3() != null)
				builder.setBeneficOwnerEstablishDate3(customerInfo.getBeneficOwnerEstablishDate3());
			if(customerInfo.getBeneficOwnerZipcode3() != null)
				builder.setBeneficOwnerZipcode3(customerInfo.getBeneficOwnerZipcode3());

			if(customerInfo.getBeneficOwnerPrefecture3() != null)
				builder.setBeneficOwnerPrefecture3(customerInfo.getBeneficOwnerPrefecture3());
			if(customerInfo.getBeneficOwnerCity3() != null)
				builder.setBeneficOwnerCity3(customerInfo.getBeneficOwnerCity3());
			if(customerInfo.getBeneficOwnerSection3() != null)
				builder.setBeneficOwnerSection3(customerInfo.getBeneficOwnerSection3());

			if(customerInfo.getBeneficOwnerBuildingName3() != null)
				builder.setBeneficOwnerBuildingName3(customerInfo.getBeneficOwnerBuildingName3());
			if(customerInfo.getBeneficOwnerTel3() != null)
				builder.setBeneficOwnerTel3(customerInfo.getBeneficOwnerTel3());
			
			//Pic
			if (customerInfo.getCorpPicFirstname() != null)
				builder.setPicFirstName(customerInfo.getCorpPicFirstname());
			if (customerInfo.getCorpPicLastname() != null)
				builder.setPicLastName(customerInfo.getCorpPicLastname());
			if (customerInfo.getCorpPicFirstnameKana() != null)
				builder.setPicFirstNameKana(customerInfo.getCorpPicFirstnameKana());
			if (customerInfo.getCorpPicLastnameKana() != null)
				builder.setPicLastNameKana(customerInfo.getCorpPicLastnameKana());
			
			if (customerInfo.getCorpPicZipcode() != null)
				builder.setPicZipCode(customerInfo.getCorpPicZipcode());
			if (customerInfo.getCorpPicPrefecture() != null)
				builder.setPicPerfecture(customerInfo.getCorpPicPrefecture());
			if (customerInfo.getCorpPicCity() != null)
				builder.setPicCity(customerInfo.getCorpPicCity());
			if(customerInfo.getCorpPicSection() != null )
				builder.setPicSection(customerInfo.getCorpPicSection());
			if (customerInfo.getCorpPicAddress() != null)
				builder.setPicAddress(customerInfo.getCorpPicAddress());
			if (customerInfo.getCorpPicBuildingName() != null)
				builder.setPicBuildingName(customerInfo.getCorpPicBuildingName());
			if (customerInfo.getCorpPicTel() != null)
				builder.setPicPhone(customerInfo.getCorpPicTel());
		}

		return builder.build();
	}

	/**
	 * Mere AmsCustomerInfo to CustomerInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static CustomerInfo convertCustomerInfo(AmsCustomerInfo amsCustomerInfo, CustomerInfo customerInfo) throws UnsupportedEncodingException {
		if(amsCustomerInfo == null)
			return null;
		//EnableMt4FX
		if(amsCustomerInfo.hasEnableMt4Fx()){
			customerInfo.setEnableMt4Fx(amsCustomerInfo.getEnableMt4Fx());
		}
		if(amsCustomerInfo.hasBeneficOwnerFlg())
			customerInfo.setBeneficOwnerFlg(Integer.valueOf(amsCustomerInfo.getBeneficOwnerFlg()));
		// Common
		if (amsCustomerInfo.hasCustomerId())
			customerInfo.setCustomerId(amsCustomerInfo.getCustomerId());
		
		//Address
		if (amsCustomerInfo.hasZipCode())
			customerInfo.setZipcode(amsCustomerInfo.getZipCode());
		if (amsCustomerInfo.hasPrefecture())
			customerInfo.setPrefecture(amsCustomerInfo.getPrefecture());
		if (amsCustomerInfo.hasCity())
			customerInfo.setCity(amsCustomerInfo.getCity());
		if (amsCustomerInfo.hasSection())
			customerInfo.setSection(amsCustomerInfo.getSection());
		if (amsCustomerInfo.hasAddress())
			customerInfo.setAddress(amsCustomerInfo.getAddress());
		if (amsCustomerInfo.hasBuildingName())
			customerInfo.setBuildingName(amsCustomerInfo.getBuildingName());
		
		//Orther
		if (amsCustomerInfo.hasPhone())
			customerInfo.setTel1(amsCustomerInfo.getPhone());
		if (amsCustomerInfo.hasPhone2())
			customerInfo.setTel2(amsCustomerInfo.getPhone2());
		if (amsCustomerInfo.hasEmail())
			customerInfo.setMailMain(amsCustomerInfo.getEmail());
		
		//[TRSM1-2175-quyen.le.manh]Feb 3, 2016M - Start - get additional mail for 2 case Individual = AMS_CUTOMER.MAIL_ADDTIONAL, Corporation = AMS_CUTOMER.CORP_PIC_MAIL_MOBILE
		if(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER == customerInfo.getCorporationType()) {
			if (amsCustomerInfo.hasAdditionalEmail())
				customerInfo.setMailAddtional(amsCustomerInfo.getAdditionalEmail());
		} else {
			if (amsCustomerInfo.hasAdditionalEmail())
				customerInfo.setCorpPicMailMobile(amsCustomerInfo.getAdditionalEmail());
		}
		//[TRSM1-2175-quyen.le.manh]Feb 3, 2016M - End
				
		if (amsCustomerInfo.hasWlCode()) {
			customerInfo.setWlCode(amsCustomerInfo.getWlCode());
		}
		if(amsCustomerInfo.hasDocumentUploadFlg())
			customerInfo.setHasDocumentUpload(convertFlg(amsCustomerInfo.getDocumentUploadFlg()));
		
		if (amsCustomerInfo.hasLoginId())
			customerInfo.setLoginId(amsCustomerInfo.getLoginId());
		// Password
		if(amsCustomerInfo.hasNewPassword()) {
			customerInfo.setNewPassword(amsCustomerInfo.getNewPassword());
			customerInfo.setComfirmedPassword(amsCustomerInfo.getNewPassword());
		}
		if(amsCustomerInfo.hasPassword()) {
			customerInfo.setLoginPass(amsCustomerInfo.getPassword());
			customerInfo.setIdentifyPassword(amsCustomerInfo.getPassword());
		}
				
		// Survey
		if (amsCustomerInfo.hasFinancialAssets())
			customerInfo.setFinancilAssets(amsCustomerInfo.getFinancialAssets().getNumber());
		if(amsCustomerInfo.hasInvestment()) {
			if(amsCustomerInfo.getInvestment().hasPurposeShortTermFlg())
				customerInfo.setPurposeShortTermFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeShortTermFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeLongTermFlg())
				customerInfo.setPurposeLongTermFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeLongTermFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeExchangeFlg())
				customerInfo.setPurposeExchangeFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeExchangeFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeSwapFlg())
				customerInfo.setPurposeSwapFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeSwapFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeHedgeAssetFlg())
				customerInfo.setPurposeHedgeAssetFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeHedgeAssetFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeHighIntFlg())
				customerInfo.setPurposeHighIntFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeHighIntFlg()));
			if(amsCustomerInfo.getInvestment().hasPurposeEconomicFlg())
				customerInfo.setPurposeEconomicFlg(convertFlg(amsCustomerInfo.getInvestment().getPurposeEconomicFlg()));
		}
		
		//BoInvestmentPurpose
		if(amsCustomerInfo.hasBoInvestmentPurpose()) {
			BoInvestmentPurpose boPurpose = amsCustomerInfo.getBoInvestmentPurpose();
			if(boPurpose.hasBoPurposeShortTermFlg())
				customerInfo.setBoPurposeShortTermFlg(convertFlg(boPurpose.getBoPurposeShortTermFlg()));
			if(boPurpose.hasBoPurposeDispAssetMngFlg())
				customerInfo.setBoPurposeDispAssetMngFlg(convertFlg(boPurpose.getBoPurposeDispAssetMngFlg()));
			if(boPurpose.hasBoPurposeHedgeFlg())
				customerInfo.setBoPurposeHedgeFlg(convertFlg(boPurpose.getBoPurposeHedgeFlg()));
		}
		if (customerInfo.isBoPurposeHedgeFlg()) {
			if (amsCustomerInfo.hasBoPurposeHedgeType())
				customerInfo.setBoPurposeHedgeType(amsCustomerInfo.getBoPurposeHedgeType().getNumber());
			if (amsCustomerInfo.hasBoPurposeHedgeAmount())
				customerInfo.setBoPurposeHedgeAmount(amsCustomerInfo.getBoPurposeHedgeAmount().getNumber());
		} else {
			customerInfo.setBoPurposeHedgeType(-1);  // -1 -> web display "please select value"
			customerInfo.setBoPurposeHedgeAmount(-1);
		}
		
		if(amsCustomerInfo.hasBoLossMaxAmount())
			customerInfo.setBoMaxLossAmount(new BigDecimal(amsCustomerInfo.getBoLossMaxAmount()));
		
		//Name, Address
		if(ITrsConstants.TRS_CONSTANT.INDIVIDUAL_CUSTOMER == customerInfo.getCorporationType()) {
			// Individual customer
			
			//Customer Name
			if (amsCustomerInfo.hasFullName()) {
				customerInfo.setFullName(amsCustomerInfo.getFullName());
			}
			if (amsCustomerInfo.hasFirstName()) {
				customerInfo.setFirstName(amsCustomerInfo.getFirstName());
			}
			if (amsCustomerInfo.hasLastName()) {
				customerInfo.setLastName(amsCustomerInfo.getLastName());
			}
			if (amsCustomerInfo.hasFirstNameKana()) {
				customerInfo.setFirstNameKana(amsCustomerInfo.getFirstNameKana());
			}
			if (amsCustomerInfo.hasLastNameKana()) {
				customerInfo.setLastNameKana(amsCustomerInfo.getLastNameKana());
			}
			
			//Orther Info
			if (amsCustomerInfo.hasBirthday()) {
				customerInfo.setBirthday(amsCustomerInfo.getBirthday());
			}
			if (amsCustomerInfo.hasGender()) {
				customerInfo.setSex(Integer.parseInt(amsCustomerInfo.getGender()));
			}			
			
			if (amsCustomerInfo.hasApplicationDate()) {
				customerInfo.setAccountApplicationDate(amsCustomerInfo.getApplicationDate());
			}
			
			if (amsCustomerInfo.hasDescription()) {
				customerInfo.setDescription(amsCustomerInfo.getDescription());
			}
		} else {
			// Corporation
			
			//CorpRepName
			if (amsCustomerInfo.hasRepFirstName())
				customerInfo.setCorpRepFirstname(amsCustomerInfo.getRepFirstName());
			if (amsCustomerInfo.hasRepLastName())
				customerInfo.setCorpRepLastname(amsCustomerInfo.getRepLastName());
			
			if (amsCustomerInfo.hasRepFirstNameKana()) {
				customerInfo.setCorpRepFirstnameKana(amsCustomerInfo.getRepFirstNameKana());
			}
			if (amsCustomerInfo.hasRepLastNameKana()) {
				customerInfo.setCorpRepLastnameKana(amsCustomerInfo.getRepLastNameKana()); 
			}
			
			String corpRepFullnameKana = "";
			if (!StringUtil.isEmpty(customerInfo.getCorpRepFirstnameKana())) {
				corpRepFullnameKana = corpRepFullnameKana + customerInfo.getCorpRepFirstnameKana();
			}
			
			if (!StringUtil.isEmpty(customerInfo.getCorpRepLastnameKana())) {
				corpRepFullnameKana = corpRepFullnameKana + " " + customerInfo.getCorpRepLastnameKana();
			}
			
			if (!StringUtil.isEmpty(corpRepFullnameKana))
				customerInfo.setCorpRepFullnameKana(corpRepFullnameKana);
			
			// CorpName
			if (amsCustomerInfo.hasCorporationName())
				customerInfo.setCorpFullname(amsCustomerInfo.getCorporationName());
				
			if (amsCustomerInfo.hasCorporationNameKana())
				customerInfo.setCorpFullnameKana(amsCustomerInfo.getCorporationNameKana());
			
			if (amsCustomerInfo.hasRepresentative())
				customerInfo.setCorpRep(amsCustomerInfo.getRepresentative());
			
			//BeneficOwner
			if (amsCustomerInfo.hasBeneficOwnerFlg()) {
				customerInfo.setBeneficOwnerFlg(Integer.parseInt(amsCustomerInfo.getBeneficOwnerFlg()));
			}

			if(amsCustomerInfo.hasBeneficOwnerFullname())
				customerInfo.setBeneficOwnerFullname(amsCustomerInfo.getBeneficOwnerFullname());
			if(amsCustomerInfo.hasBeneficOwnerFullnameKana())
				customerInfo.setBeneficOwnerFullnameKana(amsCustomerInfo.getBeneficOwnerFullnameKana());

			if(amsCustomerInfo.hasBeneficOwnerEstablishDate())
				customerInfo.setBeneficOwnerEstablishDate(amsCustomerInfo.getBeneficOwnerEstablishDate());
			if(amsCustomerInfo.hasBeneficOwnerZipcode())
				customerInfo.setBeneficOwnerZipcode(amsCustomerInfo.getBeneficOwnerZipcode());

			if(amsCustomerInfo.hasBeneficOwnerPrefecture())
				customerInfo.setBeneficOwnerPrefecture(amsCustomerInfo.getBeneficOwnerPrefecture());
			if(amsCustomerInfo.hasBeneficOwnerCity())
				customerInfo.setBeneficOwnerCity(amsCustomerInfo.getBeneficOwnerCity());
			if(amsCustomerInfo.hasBeneficOwnerSection())
				customerInfo.setBeneficOwnerSection(amsCustomerInfo.getBeneficOwnerSection());

			if(amsCustomerInfo.hasBeneficOwnerBuildingName())
				customerInfo.setBeneficOwnerBuildingName(amsCustomerInfo.getBeneficOwnerBuildingName());
			if(amsCustomerInfo.hasBeneficOwnerTel())
				customerInfo.setBeneficOwnerTel(amsCustomerInfo.getBeneficOwnerTel());

			// BeneficOwner2
			if (amsCustomerInfo.hasBeneficOwnerFlg2())
				customerInfo.setBeneficOwnerFlg2(Integer.parseInt(amsCustomerInfo.getBeneficOwnerFlg2()));

			if(amsCustomerInfo.hasBeneficOwnerFullname2())
				customerInfo.setBeneficOwnerFullname2(amsCustomerInfo.getBeneficOwnerFullname2());

			if(amsCustomerInfo.hasBeneficOwnerFullnameKana2())
				customerInfo.setBeneficOwnerFullnameKana2(amsCustomerInfo.getBeneficOwnerFullnameKana2());

			if(amsCustomerInfo.hasBeneficOwnerEstablishDate2())
				customerInfo.setBeneficOwnerEstablishDate2(amsCustomerInfo.getBeneficOwnerEstablishDate2());

			if(amsCustomerInfo.hasBeneficOwnerZipcode2())
				customerInfo.setBeneficOwnerZipcode2(amsCustomerInfo.getBeneficOwnerZipcode2());

			if(amsCustomerInfo.hasBeneficOwnerPrefecture2())
				customerInfo.setBeneficOwnerPrefecture2(amsCustomerInfo.getBeneficOwnerPrefecture2());

			if(amsCustomerInfo.hasBeneficOwnerCity2())
				customerInfo.setBeneficOwnerCity2(amsCustomerInfo.getBeneficOwnerCity2());

			if(amsCustomerInfo.hasBeneficOwnerSection2())
				customerInfo.setBeneficOwnerSection2(amsCustomerInfo.getBeneficOwnerSection2());

			if(amsCustomerInfo.hasBeneficOwnerBuildingName2())
				customerInfo.setBeneficOwnerBuildingName2(amsCustomerInfo.getBeneficOwnerBuildingName2());

			if(amsCustomerInfo.hasBeneficOwnerTel2())
				customerInfo.setBeneficOwnerTel2(amsCustomerInfo.getBeneficOwnerTel2());

			// BeneficOwner3
			if (amsCustomerInfo.hasBeneficOwnerFlg3())
				customerInfo.setBeneficOwnerFlg3(Integer.parseInt(amsCustomerInfo.getBeneficOwnerFlg3()));

			if(amsCustomerInfo.hasBeneficOwnerFullname3())
				customerInfo.setBeneficOwnerFullname3(amsCustomerInfo.getBeneficOwnerFullname3());

			if(amsCustomerInfo.hasBeneficOwnerFullnameKana3())
				customerInfo.setBeneficOwnerFullnameKana3(amsCustomerInfo.getBeneficOwnerFullnameKana3());

			if(amsCustomerInfo.hasBeneficOwnerEstablishDate3())
				customerInfo.setBeneficOwnerEstablishDate3(amsCustomerInfo.getBeneficOwnerEstablishDate3());

			if(amsCustomerInfo.hasBeneficOwnerZipcode3())
				customerInfo.setBeneficOwnerZipcode3(amsCustomerInfo.getBeneficOwnerZipcode3());

			if(amsCustomerInfo.hasBeneficOwnerPrefecture3())
				customerInfo.setBeneficOwnerPrefecture3(amsCustomerInfo.getBeneficOwnerPrefecture3());

			if(amsCustomerInfo.hasBeneficOwnerCity3())
				customerInfo.setBeneficOwnerCity3(amsCustomerInfo.getBeneficOwnerCity3());

			if(amsCustomerInfo.hasBeneficOwnerSection3())
				customerInfo.setBeneficOwnerSection3(amsCustomerInfo.getBeneficOwnerSection3());

			if(amsCustomerInfo.hasBeneficOwnerBuildingName3())
				customerInfo.setBeneficOwnerBuildingName3(amsCustomerInfo.getBeneficOwnerBuildingName3());

			if(amsCustomerInfo.hasBeneficOwnerTel3())
				customerInfo.setBeneficOwnerTel3(amsCustomerInfo.getBeneficOwnerTel3());
			
			//isChangeCorpRefName
			if (amsCustomerInfo.hasPicFirstName()) {
				customerInfo.setCorpPicFirstname(amsCustomerInfo.getPicFirstName());
			}
			if (amsCustomerInfo.hasPicLastName()) {
				customerInfo.setCorpPicLastname(amsCustomerInfo.getPicLastName());
			}
			if (amsCustomerInfo.hasPicFirstNameKana()) {
				customerInfo.setCorpPicFirstnameKana(amsCustomerInfo.getPicFirstNameKana());
			}
			if (amsCustomerInfo.hasPicLastNameKana()) {
				customerInfo.setCorpPicLastnameKana(amsCustomerInfo.getPicLastNameKana());
			}
			
			//isChangeCorpRefAddress
			if (amsCustomerInfo.hasPicZipCode())
				customerInfo.setCorpPicZipcode(amsCustomerInfo.getPicZipCode());
			if (amsCustomerInfo.hasPicPerfecture())
				customerInfo.setCorpPicPrefecture(amsCustomerInfo.getPicPerfecture());
			if (amsCustomerInfo.hasPicCity())
				customerInfo.setCorpPicCity(amsCustomerInfo.getPicCity());
			if(amsCustomerInfo.hasPicSection() )
				customerInfo.setCorpPicSection(amsCustomerInfo.getPicSection());
			if (amsCustomerInfo.hasPicAddress())
				customerInfo.setCorpPicAddress(amsCustomerInfo.getPicAddress());
			if (amsCustomerInfo.hasPicBuildingName())
				customerInfo.setCorpPicBuildingName(amsCustomerInfo.getPicBuildingName());
			
			if (amsCustomerInfo.hasPicPhone())
				customerInfo.setCorpPicTel(amsCustomerInfo.getPicPhone());
		}
		
		return customerInfo;
	}
	
	/**
	 * Convert BalanceInfo to AmsBalanceInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static AmsBalanceInfo convertBalanceInfo(BalanceInfo balanceInfo, int type) {
		if(balanceInfo == null)
			return null;
		
		AmsBalanceInfo.Builder builder = AmsBalanceInfo.newBuilder();
		
		builder.setServiceType(type + "");
		
		if(balanceInfo.getAccountId() != null)
			builder.setCustomerId(balanceInfo.getAccountId());
		
		if(balanceInfo.getServiceType() != null)
			builder.setServiceType(balanceInfo.getServiceType().toString());
		
		if(balanceInfo.getResult() != null)
			builder.setResult(AmsBalanceInfo.Result.valueOf(balanceInfo.getResult().getValue()));
		
		//in case fail
		if(!AmsBalanceInfo.Result.SUCCESS.equals(builder.getResult()))
				return builder.build();
		
		if(balanceInfo.getBalance() != null)
			builder.setCashBalance(balanceInfo.getBalance().toString());
		
		if(balanceInfo.getCurrencyCode() != null)
			builder.setCurrenyCode(balanceInfo.getCurrencyCode());
		
		if(balanceInfo.getAmountAvailable() != null)
			builder.setAvailableAmount(balanceInfo.getAmountAvailable().toString());
		
		if(balanceInfo.getCredit() != null)
			builder.setCredit(balanceInfo.getCredit().toString());
		
		if(balanceInfo.getEquity() != null)
			builder.setEquity(balanceInfo.getEquity().toString());
		
		if(balanceInfo.getUnrealizedPl() != null)
			builder.setUnrealizedPl(balanceInfo.getUnrealizedPl().toString());
		
		if(balanceInfo.getRequestingAmount() != null)
			builder.setReservedAmount(Double.toString(balanceInfo.getRequestingAmount()));
		
		if(balanceInfo.getMargin() != null)
			builder.setMargin(Double.toString(balanceInfo.getMargin()));
		
		if(balanceInfo.getFreemargin() != null)
			builder.setFreeMargin(Double.toString(balanceInfo.getFreemargin()));
		
		if(balanceInfo.getRequiredMargin() != null)
			builder.setRequiredMargin(balanceInfo.getRequiredMargin().toString());
		
		if(balanceInfo.getMarginLevel() != null)
			builder.setMarginLevel(Double.toString(balanceInfo.getMarginLevel()));
		
		return builder.build();
	}

	/**
	 * Convert CustomerServicesInfo to ServiceTypeInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static ServiceTypeInfo convertCustomerServicesInfo(CustomerServicesInfo customerServicesInfo) {
		ServiceTypeInfo.Builder builder = ServiceTypeInfo.newBuilder();
		
		if(customerServicesInfo.getNtdAccountId() != null)
			builder.setNtdAccountId(customerServicesInfo.getNtdAccountId());
		
		if(customerServicesInfo.getServiceType() != null)
			builder.setServiceType(ServiceType.valueOf(customerServicesInfo.getServiceType()));
		if(customerServicesInfo.getCustomerServiceId() != null)
			builder.setCustomerServiceId(customerServicesInfo.getCustomerServiceId());
		if(customerServicesInfo.getLeverage() != null)
			builder.setLeverage(String.valueOf(customerServicesInfo.getLeverage()));
		if(customerServicesInfo.getAccountOpenDate() != null)
			builder.setAccountOpenDate(customerServicesInfo.getAccountOpenDate());
		if(customerServicesInfo.getSubGroupId() != null)
			builder.setSubGroupId(String.valueOf(customerServicesInfo.getSubGroupId()));
		
		if(customerServicesInfo.getAllowTransactFlg() != null)
			builder.setAllowOrderFlg(Integer.valueOf(customerServicesInfo.getAllowTransactFlg()));
		else
			builder.setAllowOrderFlg(IConstants.ALLOW_FLG.INALLOW);
		
		if(customerServicesInfo.getAllowLoginFlg() != null)
			builder.setAllowLoginFlg(String.valueOf(customerServicesInfo.getAllowLoginFlg()));
		else
			builder.setAllowLoginFlg(String.valueOf(IConstants.ALLOW_FLG.INALLOW));
		
		if(customerServicesInfo.getCustomerServiceStatus() != null)
			builder.setCustomerServiceStatus(CustomerServiceStatus.valueOf(customerServicesInfo.getCustomerServiceStatus()));
		else
			builder.setCustomerServiceStatus(CustomerServiceStatus.BEFORE_REGISTER);
		
		return builder.build();
	}
	
	/**
	 * Convert AmsWithdrawal to AmsWithdrawalTransactionInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static AmsWithdrawalTransactionInfo convertWithdrawalInfo(AmsWithdrawal amsWithdrawal) {
		AmsWithdrawalTransactionInfo.Builder builder = AmsWithdrawalTransactionInfo.newBuilder();
		builder.setCustomerId(amsWithdrawal.getRegCustomerId());
		builder.setWithdrawalId(amsWithdrawal.getWithdrawalId());
		return builder.build();
	}

	/**
	 * Convert financialAssetsValue to FinancialAssets
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static FinancialAssets convertFinancialAssets(int financialAssetsValue) {
		switch (financialAssetsValue) {
		case 0:
			return FinancialAssets.FINANCIAL_ASSETS_0;
		case 1:
			return FinancialAssets.FINANCIAL_ASSETS_1;
		case 2:
			return FinancialAssets.FINANCIAL_ASSETS_2;
		case 3:
			return FinancialAssets.FINANCIAL_ASSETS_3;
		case 4:
			return FinancialAssets.FINANCIAL_ASSETS_4;
		case 5:
			return FinancialAssets.FINANCIAL_ASSETS_5;
		case 6:
			return FinancialAssets.FINANCIAL_ASSETS_6;
		case 7:
			return FinancialAssets.FINANCIAL_ASSETS_7;
		case 8:
			return FinancialAssets.FINANCIAL_ASSETS_8;
		default:
			return FinancialAssets.valueOf(financialAssetsValue);
		}
	}
	
	/**
	 * Convert CustomerBankInfo to AmsCustomerBankInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 9, 2015
	 * @MdDate
	 */
	public static AmsCustomerBankInfo convertAmsCustomerBankInfo(CustomerBankInfo customerBankInfo) {
		if(customerBankInfo == null)
			return null;
		
		AmsCustomerBankInfo.Builder builder = AmsCustomerBankInfo.newBuilder();
		
		if (customerBankInfo.getCustomerId() != null)
			builder.setCustomerId(customerBankInfo.getCustomerId());
		if (customerBankInfo.getCustomerBankId() != null)
			builder.setCustomerBankId(customerBankInfo.getCustomerBankId() + "");
		if (customerBankInfo.getBankCode() != null)
			builder.setBankCode(customerBankInfo.getBankCode());
		
		if (customerBankInfo.getBankName() != null)
			builder.setBankName(customerBankInfo.getBankName());
		if (customerBankInfo.getBankNameKana() != null)
			builder.setBankNameKana(customerBankInfo.getBankNameKana());
		
		if (customerBankInfo.getBranchCode() != null)
			builder.setBranchCode(customerBankInfo.getBranchCode());
		
		if (customerBankInfo.getBranchName() != null)
			builder.setBranchName(customerBankInfo.getBranchName());
		if (customerBankInfo.getBranchNameKana() != null)
			builder.setBranchNameKana(customerBankInfo.getBranchNameKana());
		if (customerBankInfo.getBankAccClass() != null)
			builder.setBankAccClass(customerBankInfo.getBankAccClass() + "");
		
		if (customerBankInfo.getAccountNo() != null)
			builder.setAccountNo(customerBankInfo.getAccountNo());
		if (customerBankInfo.getAccountName() != null)
			builder.setAccountName(customerBankInfo.getAccountName());
		if (customerBankInfo.getAccountNameKana() != null)
			builder.setAccountNameKana(customerBankInfo.getAccountNameKana());
		
		if (customerBankInfo.getSwiftCode() != null)
			builder.setSwiftCode(customerBankInfo.getSwiftCode());
		
		if (customerBankInfo.getBankAddress() != null)
			builder.setBankAddress(customerBankInfo.getBankAddress());
		if (customerBankInfo.getCountryId() != null)
			builder.setCountryId(customerBankInfo.getCountryId() + "");
		if (customerBankInfo.getCountryName() != null)
			builder.setCountryName(customerBankInfo.getCountryName());
		
		if (customerBankInfo.getUpdateDate() != null)
			builder.setUpdateDate(DateUtil.toString(customerBankInfo.getUpdateDate(), DateUtil.PATTERN_YYMMDD_HH_MM) );
		
		return builder.build();
	}
	
	public static boolean convertFlg(String flag) {
		if(flag == null)
			return false;
		if(IConstants.ACTIVE_FLG.ACTIVE == Integer.parseInt(flag))
			return true;
		return false;
	}
	
	public static int convertFlg(boolean flag) {
		if(flag)
			return IConstants.ACTIVE_FLG.ACTIVE;
		return IConstants.ACTIVE_FLG.INACTIVE;
	}
	
	public static boolean convertFlg(int flag) {
		return IConstants.ACTIVE_FLG.ACTIVE == flag;
	}
	
	public static AmsNewsInfo convertAmsMessage(AmsMessage amsMessage) {
		AmsNewsInfo.Builder amsNewsInfo = AmsNewsInfo.newBuilder();
		if(amsMessage.getCustomerDeleteFlg()!=null){
			amsNewsInfo.setCustomerDeleteFlg(AllowedFlag.valueOf(amsMessage.getCustomerDeleteFlg()));
		}
		if(amsMessage.getEndDate()!=null){
			amsNewsInfo.setEndDate(Common.formatTimeStampToString(amsMessage.getEndDate()));
		}
		if(amsMessage.getInformation() != null)
			amsNewsInfo.setInformation(amsMessage.getInformation());
		if(amsMessage.getMessageCategory()!= null){
			amsNewsInfo.setMessageCategory(MessageCategory.valueOf(amsMessage.getMessageCategory()));
		}
		if(amsMessage.getMessageId()!= null){
			amsNewsInfo.setMessageId(Common.formatObjToString(amsMessage.getMessageId()));
		}
		if(amsMessage.getMessageTitle() != null)
			amsNewsInfo.setMessageTitle(amsMessage.getMessageTitle());
		if(amsMessage.getMessageType()!= null){
			amsNewsInfo.setMessageType(MessageType.valueOf(amsMessage.getMessageType()));
		}
		if(amsMessage.getReadFlg()!=null){
			amsNewsInfo.setReadFlg(ReadFlag.valueOf(amsMessage.getReadFlg()));
		}
		if(amsMessage.getReadingManageFlg()!=null){
			amsNewsInfo.setReadingManageFlg(AllowedFlag.valueOf(amsMessage.getReadingManageFlg()));
		}
		if(amsMessage.getServiceType()!=null){
			amsNewsInfo.setServiceType(ServiceType.valueOf(amsMessage.getServiceType()));
		}
		if(amsMessage.getStartDate()!=null){
			amsNewsInfo.setStartDate(Common.formatTimeStampToString(amsMessage.getStartDate()));
		}
		if(amsMessage.getMessageKind()!=null){
			amsNewsInfo.setMessageKind(MessageKind.valueOf(amsMessage.getMessageKind()));
		}
		if (amsMessage.getConfirmFlg() != null)
			amsNewsInfo.setConfirmFlg(ConfirmFlag.valueOf(amsMessage.getConfirmFlg()));
		
		if(amsMessage.getAcceptanceDeadline() != null)
			amsNewsInfo.setAcceptanceDeadline(Common.formatTimeStampToString(amsMessage.getAcceptanceDeadline()));
		return amsNewsInfo.build();
	}
}