/**
 * 
 */
package phn.nts.ams.fe.domain;

import org.apache.log4j.Logger;

import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;

/**
 * @author tungpv
 * 
 */
public class BjpDepositInfo {
	//mobileCategory = 1：BJP Mobile・DoCoMo   2：BJP Mobile・EzWeb 3：BJP Mobile・Vodafone   4：Ngoài BJP Mobile 5：Không thông qua BJP
	/*checkAccountHolderResult =1：Khớp account holder・HOÀN THÀNH BJP   2：Khớp account holder・ĐANG Xử LÝ BJP   
	3：Tên account holder không khớp・HOÀN THÀNH BJP 4：Tên account holder không khớp・ĐANG Xử LÝ BJP    5：KHÔNG THÔNG QUA BJP・利息   6：KHÔNG THÔNG QUA BJP・利息以外
	*/
	//billCheckCategory = 1：check   2：bill obligatory   3：bill of exchange  (Trường hợp old UFJ, SMBC, có thể có space。 Trường hợp mizuho, có thể có 0)
	//tranferCategory= 10：cash 11：payment 14：transfer 18：others  19：amendment
	//depositWithdrawlCategory = 1：deposit   2：withdrawl
	private static Logger log = Logger.getLogger(BjpDepositInfo.class);
	private boolean dataRecordCheck = false;
	private int dataCategory;
	private String refCode;
	private String countDay;
	private String depositWithdrawlDay;
	private String DEPOSIT_ACCEPT_DATE;
	private String depositWithdrawlCategory;
	private String tranferCategory;
	private double tranferAmount;
	private double DEPOSIT_AMOUNT;
	private int bankCheckAmount;
	private String declareTranferDate;
	private String revertMoneyDate;
	private String billCheckCategory;
	private String billCheckNumber;
	private String branchCode;
	private String codeOfUserRequestTranfer;
	private String nameOfUserRequestTranfer;
	private String ACCOUNT_NAME_KANA;
	private String bankNameTranfer;
	private String BANK_NAME_KANA;
	private String branchNameTranfer;
	private String BRANCH_NAME_KANA;
	private String contentNote;
	private String REMARK;
	private String ediInformation;
	private String checkAccountHolderResult;
	private String CHECK_MEIGI_STAT;
	private String managerCode;
	private String DEPOSIT_ID;
	private String mobileCategory;
	private String dummy;

	public BjpDepositInfo() {
	}

	public BjpDepositInfo(String record) {
		try {
			String first = record.substring(0, 1);
			if (BJP_CONFIG.DATA_KESHIKOMI.equalsIgnoreCase(first)) {
				dataRecordCheck = true;
				dataCategory = Integer.parseInt(first);
				refCode = record.substring(1, 9).trim();
				while(refCode.startsWith("0")){
					refCode = refCode.substring(1);
				}
				countDay = record.substring(9, 15).trim();
				depositWithdrawlDay = record.substring(15, 21).trim();
				DEPOSIT_ACCEPT_DATE=depositWithdrawlDay;
				depositWithdrawlCategory = record.substring(21, 22).trim();
				tranferCategory = record.substring(22, 24).trim();
				tranferAmount = Double.parseDouble(record.substring(24, 36));
				DEPOSIT_AMOUNT=tranferAmount;
				bankCheckAmount = Integer.parseInt(record.substring(36, 48));
				declareTranferDate = record.substring(48, 54).trim();
				revertMoneyDate = record.substring(54, 60).trim();
				billCheckCategory = record.substring(60, 61).trim();
				billCheckNumber = record.substring(61, 68).trim();
				while(billCheckNumber.startsWith("0")){
					billCheckNumber = billCheckNumber.substring(1);
				}
				branchCode = record.substring(68, 71).trim();
				codeOfUserRequestTranfer = record.substring(71, 81).trim();
				while(codeOfUserRequestTranfer.startsWith("0")){
					codeOfUserRequestTranfer = codeOfUserRequestTranfer.substring(1);
				}
				
				nameOfUserRequestTranfer = record.substring(81, 129).trim();
				ACCOUNT_NAME_KANA = nameOfUserRequestTranfer;
				bankNameTranfer = record.substring(129, 144).trim();
				BANK_NAME_KANA=bankNameTranfer;
				branchNameTranfer = record.substring(144, 159).trim();
				BRANCH_NAME_KANA=branchNameTranfer;
				contentNote = record.substring(159, 179).trim();
				REMARK=contentNote;
				ediInformation = record.substring(179, 199).trim();
				checkAccountHolderResult = record.substring(199, 200).trim();
				CHECK_MEIGI_STAT=checkAccountHolderResult;
				managerCode = record.substring(200, 220).trim();
				DEPOSIT_ID=managerCode;
				mobileCategory = record.substring(220, 221).trim();
				dummy = record.substring(221, 250).trim().trim();
			}else{
//				log.info(record);
			}
		} catch (Exception e) {
			log.error("KESHIKOMI"+e.getMessage());
		}
	}

	/**
	 * @return the dataRecordCheck
	 */
	public boolean isDataRecordCheck() {
		return dataRecordCheck;
	}

	/**
	 * @param dataRecordCheck
	 *            the dataRecordCheck to set
	 */
	public void setDataRecordCheck(boolean dataRecordCheck) {
		this.dataRecordCheck = dataRecordCheck;
	}

	/**
	 * @return the dataCategory
	 */
	public int getDataCategory() {
		return dataCategory;
	}

	/**
	 * @param dataCategory
	 *            the dataCategory to set
	 */
	public void setDataCategory(int dataCategory) {
		this.dataCategory = dataCategory;
	}

	/**
	 * @return the refCode
	 */
	public String getRefCode() {
		return refCode;
	}

	/**
	 * @param refCode
	 *            the refCode to set
	 */
	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}

	/**
	 * @return the countDay
	 */
	public String getCountDay() {
		return countDay;
	}

	/**
	 * @param countDay
	 *            the countDay to set
	 */
	public void setCountDay(String countDay) {
		this.countDay = countDay;
	}

	/**
	 * @return the depositWithdrawlDay
	 */
	public String getDepositWithdrawlDay() {
		return depositWithdrawlDay;
	}

	/**
	 * @param depositWithdrawlDay
	 *            the depositWithdrawlDay to set
	 */
	public void setDepositWithdrawlDay(String depositWithdrawlDay) {
		this.depositWithdrawlDay = depositWithdrawlDay;
	}

	/**
	 * @return the depositWithdrawlCategory
	 */
	public String getDepositWithdrawlCategory() {
		return depositWithdrawlCategory;
	}

	/**
	 * @param depositWithdrawlCategory
	 *            the depositWithdrawlCategory to set
	 */
	public void setDepositWithdrawlCategory(String depositWithdrawlCategory) {
		this.depositWithdrawlCategory = depositWithdrawlCategory;
	}

	/**
	 * @return the tranferCategory
	 */
	public String getTranferCategory() {
		return tranferCategory;
	}

	/**
	 * @param tranferCategory
	 *            the tranferCategory to set
	 */
	public void setTranferCategory(String tranferCategory) {
		this.tranferCategory = tranferCategory;
	}

	/**
	 * @return the tranferAmount
	 */
	public double getTranferAmount() {
		return tranferAmount;
	}

	/**
	 * @param tranferAmount
	 *            the tranferAmount to set
	 */
	public void setTranferAmount(double tranferAmount) {
		this.tranferAmount = tranferAmount;
	}

	/**
	 * @return the bankCheckAmount
	 */
	public int getBankCheckAmount() {
		return bankCheckAmount;
	}

	/**
	 * @param bankCheckAmount
	 *            the bankCheckAmount to set
	 */
	public void setBankCheckAmount(int bankCheckAmount) {
		this.bankCheckAmount = bankCheckAmount;
	}

	/**
	 * @return the declareTranferDate
	 */
	public String getDeclareTranferDate() {
		return declareTranferDate;
	}

	/**
	 * @param declareTranferDate
	 *            the declareTranferDate to set
	 */
	public void setDeclareTranferDate(String declareTranferDate) {
		this.declareTranferDate = declareTranferDate;
	}

	/**
	 * @return the revertMoneyDate
	 */
	public String getRevertMoneyDate() {
		return revertMoneyDate;
	}

	/**
	 * @param revertMoneyDate
	 *            the revertMoneyDate to set
	 */
	public void setRevertMoneyDate(String revertMoneyDate) {
		this.revertMoneyDate = revertMoneyDate;
	}

	/**
	 * @return the billCheckCategory
	 */
	public String getBillCheckCategory() {
		return billCheckCategory;
	}

	/**
	 * @param billCheckCategory
	 *            the billCheckCategory to set
	 */
	public void setBillCheckCategory(String billCheckCategory) {
		this.billCheckCategory = billCheckCategory;
	}

	/**
	 * @return the billCheckNumber
	 */
	public String getBillCheckNumber() {
		return billCheckNumber;
	}

	/**
	 * @param billCheckNumber
	 *            the billCheckNumber to set
	 */
	public void setBillCheckNumber(String billCheckNumber) {
		this.billCheckNumber = billCheckNumber;
	}

	/**
	 * @return the branchCode
	 */
	public String getBranchCode() {
		return branchCode;
	}

	/**
	 * @param branchCode
	 *            the branchCode to set
	 */
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	/**
	 * @return the codeOfUserRequestTranfer
	 */
	public String getCodeOfUserRequestTranfer() {
		return codeOfUserRequestTranfer;
	}

	/**
	 * @param codeOfUserRequestTranfer
	 *            the codeOfUserRequestTranfer to set
	 */
	public void setCodeOfUserRequestTranfer(String codeOfUserRequestTranfer) {
		this.codeOfUserRequestTranfer = codeOfUserRequestTranfer;
	}

	/**
	 * @return the nameOfUserRequestTranfer
	 */
	public String getNameOfUserRequestTranfer() {
		return nameOfUserRequestTranfer;
	}

	/**
	 * @param nameOfUserRequestTranfer
	 *            the nameOfUserRequestTranfer to set
	 */
	public void setNameOfUserRequestTranfer(String nameOfUserRequestTranfer) {
		this.nameOfUserRequestTranfer = nameOfUserRequestTranfer;
	}

	/**
	 * @return the bankNameTranfer
	 */
	public String getBankNameTranfer() {
		return bankNameTranfer;
	}

	/**
	 * @param bankNameTranfer
	 *            the bankNameTranfer to set
	 */
	public void setBankNameTranfer(String bankNameTranfer) {
		this.bankNameTranfer = bankNameTranfer;
	}

	/**
	 * @return the branchNameTranfer
	 */
	public String getBranchNameTranfer() {
		return branchNameTranfer;
	}

	/**
	 * @param branchNameTranfer
	 *            the branchNameTranfer to set
	 */
	public void setBranchNameTranfer(String branchNameTranfer) {
		this.branchNameTranfer = branchNameTranfer;
	}

	/**
	 * @return the contentNote
	 */
	public String getContentNote() {
		return contentNote;
	}

	/**
	 * @param contentNote
	 *            the contentNote to set
	 */
	public void setContentNote(String contentNote) {
		this.contentNote = contentNote;
	}

	/**
	 * @return the ediInformation
	 */
	public String getEdiInformation() {
		return ediInformation;
	}

	/**
	 * @param ediInformation
	 *            the ediInformation to set
	 */
	public void setEdiInformation(String ediInformation) {
		this.ediInformation = ediInformation;
	}

	/**
	 * @return the checkAccountHolderResult
	 */
	public String getCheckAccountHolderResult() {
		return checkAccountHolderResult;
	}

	/**
	 * @param checkAccountHolderResult
	 *            the checkAccountHolderResult to set
	 */
	public void setCheckAccountHolderResult(String checkAccountHolderResult) {
		this.checkAccountHolderResult = checkAccountHolderResult;
	}

	/**
	 * @return the managerCode
	 */
	public String getManagerCode() {
		return managerCode;
	}

	/**
	 * @param managerCode
	 *            the managerCode to set
	 */
	public void setManagerCode(String managerCode) {
		this.managerCode = managerCode;
	}

	/**
	 * @return the mobileCategory
	 */
	public String getMobileCategory() {
		return mobileCategory;
	}

	/**
	 * @param mobileCategory
	 *            the mobileCategory to set
	 */
	public void setMobileCategory(String mobileCategory) {
		this.mobileCategory = mobileCategory;
	}

	/**
	 * @return the dummy
	 */
	public String getDummy() {
		return dummy;
	}

	/**
	 * @param dummy
	 *            the dummy to set
	 */
	public void setDummy(String dummy) {
		this.dummy = dummy;
	}

	/**
	 * @return the dEPOSIT_ACCEPT_DATE
	 */
	public String getDEPOSIT_ACCEPT_DATE() {
		return DEPOSIT_ACCEPT_DATE;
	}

	/**
	 * @param dEPOSIT_ACCEPT_DATE the dEPOSIT_ACCEPT_DATE to set
	 */
	public void setDEPOSIT_ACCEPT_DATE(String dEPOSIT_ACCEPT_DATE) {
		DEPOSIT_ACCEPT_DATE = dEPOSIT_ACCEPT_DATE;
	}

	/**
	 * @return the dEPOSIT_AMOUNT
	 */
	public double getDEPOSIT_AMOUNT() {
		return DEPOSIT_AMOUNT;
	}

	/**
	 * @param dEPOSIT_AMOUNT the dEPOSIT_AMOUNT to set
	 */
	public void setDEPOSIT_AMOUNT(double dEPOSIT_AMOUNT) {
		DEPOSIT_AMOUNT = dEPOSIT_AMOUNT;
	}

	/**
	 * @return the aCCOUNT_NAME_KANA
	 */
	public String getACCOUNT_NAME_KANA() {
		return ACCOUNT_NAME_KANA;
	}

	/**
	 * @param aCCOUNT_NAME_KANA the aCCOUNT_NAME_KANA to set
	 */
	public void setACCOUNT_NAME_KANA(String aCCOUNT_NAME_KANA) {
		ACCOUNT_NAME_KANA = aCCOUNT_NAME_KANA;
	}

	/**
	 * @return the bANK_NAME_KANA
	 */
	public String getBANK_NAME_KANA() {
		return BANK_NAME_KANA;
	}

	/**
	 * @param bANK_NAME_KANA the bANK_NAME_KANA to set
	 */
	public void setBANK_NAME_KANA(String bANK_NAME_KANA) {
		BANK_NAME_KANA = bANK_NAME_KANA;
	}

	/**
	 * @return the bRANCH_NAME_KANA
	 */
	public String getBRANCH_NAME_KANA() {
		return BRANCH_NAME_KANA;
	}

	/**
	 * @param bRANCH_NAME_KANA the bRANCH_NAME_KANA to set
	 */
	public void setBRANCH_NAME_KANA(String bRANCH_NAME_KANA) {
		BRANCH_NAME_KANA = bRANCH_NAME_KANA;
	}

	/**
	 * @return the rEMARK
	 */
	public String getREMARK() {
		return REMARK;
	}

	/**
	 * @param rEMARK the rEMARK to set
	 */
	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}

	/**
	 * @return the cHECK_MEIGI_STAT
	 */
	public String getCHECK_MEIGI_STAT() {
		return CHECK_MEIGI_STAT;
	}

	/**
	 * @param cHECK_MEIGI_STAT the cHECK_MEIGI_STAT to set
	 */
	public void setCHECK_MEIGI_STAT(String cHECK_MEIGI_STAT) {
		CHECK_MEIGI_STAT = cHECK_MEIGI_STAT;
	}

	/**
	 * @return the dEPOSIT_ID
	 */
	public String getDEPOSIT_ID() {
		return DEPOSIT_ID;
	}

	/**
	 * @param dEPOSIT_ID the dEPOSIT_ID to set
	 */
	public void setDEPOSIT_ID(String dEPOSIT_ID) {
		DEPOSIT_ID = dEPOSIT_ID;
	}

	@Override
	public String toString() {
		return "BjpDepositInfo [dataRecordCheck=" + dataRecordCheck
				+ ", dataCategory=" + dataCategory + ", refCode=" + refCode
				+ ", countDay=" + countDay + ", depositWithdrawlDay="
				+ depositWithdrawlDay + ", DEPOSIT_ACCEPT_DATE="
				+ DEPOSIT_ACCEPT_DATE + ", depositWithdrawlCategory="
				+ depositWithdrawlCategory + ", tranferCategory="
				+ tranferCategory + ", tranferAmount=" + tranferAmount
				+ ", DEPOSIT_AMOUNT=" + DEPOSIT_AMOUNT + ", bankCheckAmount="
				+ bankCheckAmount + ", declareTranferDate="
				+ declareTranferDate + ", revertMoneyDate=" + revertMoneyDate
				+ ", billCheckCategory=" + billCheckCategory
				+ ", billCheckNumber=" + billCheckNumber + ", branchCode="
				+ branchCode + ", codeOfUserRequestTranfer="
				+ codeOfUserRequestTranfer + ", nameOfUserRequestTranfer="
				+ nameOfUserRequestTranfer + ", ACCOUNT_NAME_KANA="
				+ ACCOUNT_NAME_KANA + ", bankNameTranfer=" + bankNameTranfer
				+ ", BANK_NAME_KANA=" + BANK_NAME_KANA + ", branchNameTranfer="
				+ branchNameTranfer + ", BRANCH_NAME_KANA=" + BRANCH_NAME_KANA
				+ ", contentNote=" + contentNote + ", REMARK=" + REMARK
				+ ", ediInformation=" + ediInformation
				+ ", checkAccountHolderResult=" + checkAccountHolderResult
				+ ", CHECK_MEIGI_STAT=" + CHECK_MEIGI_STAT + ", managerCode="
				+ managerCode + ", DEPOSIT_ID=" + DEPOSIT_ID
				+ ", mobileCategory=" + mobileCategory + ", dummy=" + dummy
				+ "]";
	}

}
