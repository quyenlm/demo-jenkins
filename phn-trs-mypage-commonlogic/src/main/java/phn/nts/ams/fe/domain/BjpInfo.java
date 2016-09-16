package phn.nts.ams.fe.domain;

import java.io.Serializable;

public class BjpInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bankCode;
	private Double amount;
	
	//POST
	public String POST_PORTAL_CODE	;			
	public String POST_SHOP_CODE		;		
	public String POST_BANK_CODE	;			
	public String POST_KESSAI_FLAG	;			
	public String POST_CTRL_NO	;			
	public Double POST_TRAN_AMOUNT	;			
	public String POST_VALID_RETURN_URL	;			
	public String POST_INVALID_RETURN_URL	;			
	public String POST_CUST_NAME_1	;			
	public String POST_CUST_NAME_2		;		
	public String POST_CUST_NAME		;		
	public String POST_CUST_LNAME	;			
	public String POST_CUST_FNAME	;			
	public String POST_GOODS_NAME	;			
	public String POST_GOODS_NAME_KANA	;			
	public String POST_REMARKS_1		;		
	public String POST_REMARKS_2		;		
	public String POST_REMARKS_3		;		
	public String POST_CUST_POSTCODE_1;			
	public String POST_CUST_POSTCODE_2;			
	public String POST_CUST_ADDRESS_1;				
	public String POST_CUST_ADDRESS_2;				
	public String POST_CUST_ADDRESS_3;			
	public String POST_CUS_TEL		;		
	public String POST_EMAIL_ADDRESS	;			
	public String POST_TRAN_TAX	;			
	public String POST_CONC_DAY	;			
	public String POST_DUE_DAY	;			
	public String POST_UN_KESSAI_FLAG;				
	public String POST_M_AMOUNT_1	;			
	public String POST_M_GOODS_NAME_1;				
	public String POST_M_REMARK_1	;			
	public String POST_M_AMOUNT_2	;			
	public String POST_M_GOODS_NAME_2;				
	public String POST_M_REMARK_2	;			
	public String POST_M_AMOUNT_3	;			
	public String POST_M_GOODS_NAME_3;				
	public String POST_M_REMARK_3	;			
	public String POST_M_AMOUNT_4	;			
	public String POST_M_GOODS_NAME_4;			
	public String POST_M_REMARK_4	;			
	public String POST_M_AMOUNT_5	;			
	public String POST_M_GOODS_NAME_5;				
	public String POST_M_REMARK_5	;			
	public String POST_M_AMOUNT_6	;			
	public String POST_M_GOODS_NAME_6;				
	public String POST_M_REMARK_6	;			
	public String POST_M_AMOUNT_7	;			
	public String POST_M_GOODS_NAME_7;				
	public String POST_M_REMARK_7	;			
	//GET
	public  String RECEIVED_PORTAL_CODE;				
	public  String RECEIVED_SHOP_CODE	;			
	public  String RECEIVED_BANK_CODE	;			
	public  String RECEIVED_KESSAI_FLAG;				
	public  String RECEIVED_CTRL_NO	;			
	public  String RECEIVED_TRAN_STAT	;			
	public  String RECEIVED_TRAN_REASON_CODE;				
	public  String RECEIVED_TRAN_RESULT_MSG;				
	public  String RECEIVED_TRAN_DATE	;			
	public  String RECEIVED_TRAN_TIME	;			
	public  String RECEIVED_CUST_NAME	;			
	public  String RECEIVED_CUST_LNAME;				
	public  String RECEIVED_CUST_FNAME;				
	public  String RECEIVED_TRAN_AMOUNT	;			
	public  String RECEIVED_TRAN_FEE	;			
	public  String RECEIVED_PAYMENT_DAY;				
	public  String RECEIVED_GOODS_NAME;				
	public  String RECEIVED_REMARKS_1	;			
	public  String RECEIVED_REMARKS_2	;			
	public  String RECEIVED_REMARKS_3	;			
	public  String RECEIVED_TRAN_ID	;			
	public  String RECEIVED_TRAN_DIGEST	;	
	public  String BJP_URL	;	
	public  int valid=0;	
	public String msg;	

	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getPOST_PORTAL_CODE() {
		return POST_PORTAL_CODE;
	}
	public void setPOST_PORTAL_CODE(String pOST_PORTAL_CODE) {
		POST_PORTAL_CODE = pOST_PORTAL_CODE;
	}
	public String getPOST_SHOP_CODE() {
		return POST_SHOP_CODE;
	}
	public void setPOST_SHOP_CODE(String pOST_SHOP_CODE) {
		POST_SHOP_CODE = pOST_SHOP_CODE;
	}
	public String getPOST_BANK_CODE() {
		return POST_BANK_CODE;
	}
	public void setPOST_BANK_CODE(String pOST_BANK_CODE) {
		POST_BANK_CODE = pOST_BANK_CODE;
	}
	public String getPOST_KESSAI_FLAG() {
		return POST_KESSAI_FLAG;
	}
	public void setPOST_KESSAI_FLAG(String pOST_KESSAI_FLAG) {
		POST_KESSAI_FLAG = pOST_KESSAI_FLAG;
	}
	public String getPOST_CTRL_NO() {
		return POST_CTRL_NO;
	}
	public void setPOST_CTRL_NO(String pOST_CTRL_NO) {
		POST_CTRL_NO = pOST_CTRL_NO;
	}
	public Double getPOST_TRAN_AMOUNT() {
		return POST_TRAN_AMOUNT;
	}
	public void setPOST_TRAN_AMOUNT(Double pOST_TRAN_AMOUNT) {
		POST_TRAN_AMOUNT = pOST_TRAN_AMOUNT;
	}
	public String getPOST_VALID_RETURN_URL() {
		return POST_VALID_RETURN_URL;
	}
	public void setPOST_VALID_RETURN_URL(String pOST_VALID_RETURN_URL) {
		POST_VALID_RETURN_URL = pOST_VALID_RETURN_URL;
	}
	public String getPOST_INVALID_RETURN_URL() {
		return POST_INVALID_RETURN_URL;
	}
	public void setPOST_INVALID_RETURN_URL(String pOST_INVALID_RETURN_URL) {
		POST_INVALID_RETURN_URL = pOST_INVALID_RETURN_URL;
	}
	public String getPOST_CUST_NAME_1() {
		return POST_CUST_NAME_1;
	}
	public void setPOST_CUST_NAME_1(String pOST_CUST_NAME_1) {
		POST_CUST_NAME_1 = pOST_CUST_NAME_1;
	}
	public String getPOST_CUST_NAME_2() {
		return POST_CUST_NAME_2;
	}
	public void setPOST_CUST_NAME_2(String pOST_CUST_NAME_2) {
		POST_CUST_NAME_2 = pOST_CUST_NAME_2;
	}
	public String getPOST_CUST_NAME() {
		return POST_CUST_NAME;
	}
	public void setPOST_CUST_NAME(String pOST_CUST_NAME) {
		POST_CUST_NAME = pOST_CUST_NAME;
	}
	public String getPOST_CUST_LNAME() {
		return POST_CUST_LNAME;
	}
	public void setPOST_CUST_LNAME(String pOST_CUST_LNAME) {
		POST_CUST_LNAME = pOST_CUST_LNAME;
	}
	public String getPOST_CUST_FNAME() {
		return POST_CUST_FNAME;
	}
	public void setPOST_CUST_FNAME(String pOST_CUST_FNAME) {
		POST_CUST_FNAME = pOST_CUST_FNAME;
	}
	public String getPOST_GOODS_NAME() {
		return POST_GOODS_NAME;
	}
	public void setPOST_GOODS_NAME(String pOST_GOODS_NAME) {
		POST_GOODS_NAME = pOST_GOODS_NAME;
	}
	public String getPOST_GOODS_NAME_KANA() {
		return POST_GOODS_NAME_KANA;
	}
	public void setPOST_GOODS_NAME_KANA(String pOST_GOODS_NAME_KANA) {
		POST_GOODS_NAME_KANA = pOST_GOODS_NAME_KANA;
	}
	public String getPOST_REMARKS_1() {
		return POST_REMARKS_1;
	}
	public void setPOST_REMARKS_1(String pOST_REMARKS_1) {
		POST_REMARKS_1 = pOST_REMARKS_1;
	}
	public String getPOST_REMARKS_2() {
		return POST_REMARKS_2;
	}
	public void setPOST_REMARKS_2(String pOST_REMARKS_2) {
		POST_REMARKS_2 = pOST_REMARKS_2;
	}
	public String getPOST_REMARKS_3() {
		return POST_REMARKS_3;
	}
	public void setPOST_REMARKS_3(String pOST_REMARKS_3) {
		POST_REMARKS_3 = pOST_REMARKS_3;
	}
	public String getPOST_CUST_POSTCODE_1() {
		return POST_CUST_POSTCODE_1;
	}
	public void setPOST_CUST_POSTCODE_1(String pOST_CUST_POSTCODE_1) {
		POST_CUST_POSTCODE_1 = pOST_CUST_POSTCODE_1;
	}
	public String getPOST_CUST_POSTCODE_2() {
		return POST_CUST_POSTCODE_2;
	}
	public void setPOST_CUST_POSTCODE_2(String pOST_CUST_POSTCODE_2) {
		POST_CUST_POSTCODE_2 = pOST_CUST_POSTCODE_2;
	}
	public String getPOST_CUST_ADDRESS_1() {
		return POST_CUST_ADDRESS_1;
	}
	public void setPOST_CUST_ADDRESS_1(String pOST_CUST_ADDRESS_1) {
		POST_CUST_ADDRESS_1 = pOST_CUST_ADDRESS_1;
	}
	public String getPOST_CUST_ADDRESS_2() {
		return POST_CUST_ADDRESS_2;
	}
	public void setPOST_CUST_ADDRESS_2(String pOST_CUST_ADDRESS_2) {
		POST_CUST_ADDRESS_2 = pOST_CUST_ADDRESS_2;
	}
	public String getPOST_CUST_ADDRESS_3() {
		return POST_CUST_ADDRESS_3;
	}
	public void setPOST_CUST_ADDRESS_3(String pOST_CUST_ADDRESS_3) {
		POST_CUST_ADDRESS_3 = pOST_CUST_ADDRESS_3;
	}
	public String getPOST_CUS_TEL() {
		return POST_CUS_TEL;
	}
	public void setPOST_CUS_TEL(String pOST_CUS_TEL) {
		POST_CUS_TEL = pOST_CUS_TEL;
	}
	public String getPOST_EMAIL_ADDRESS() {
		return POST_EMAIL_ADDRESS;
	}
	public void setPOST_EMAIL_ADDRESS(String pOST_EMAIL_ADDRESS) {
		POST_EMAIL_ADDRESS = pOST_EMAIL_ADDRESS;
	}
	public String getPOST_TRAN_TAX() {
		return POST_TRAN_TAX;
	}
	public void setPOST_TRAN_TAX(String pOST_TRAN_TAX) {
		POST_TRAN_TAX = pOST_TRAN_TAX;
	}
	public String getPOST_CONC_DAY() {
		return POST_CONC_DAY;
	}
	public void setPOST_CONC_DAY(String pOST_CONC_DAY) {
		POST_CONC_DAY = pOST_CONC_DAY;
	}
	public String getPOST_DUE_DAY() {
		return POST_DUE_DAY;
	}
	public void setPOST_DUE_DAY(String pOST_DUE_DAY) {
		POST_DUE_DAY = pOST_DUE_DAY;
	}
	public String getPOST_UN_KESSAI_FLAG() {
		return POST_UN_KESSAI_FLAG;
	}
	public void setPOST_UN_KESSAI_FLAG(String pOST_UN_KESSAI_FLAG) {
		POST_UN_KESSAI_FLAG = pOST_UN_KESSAI_FLAG;
	}
	public String getPOST_M_AMOUNT_1() {
		return POST_M_AMOUNT_1;
	}
	public void setPOST_M_AMOUNT_1(String pOST_M_AMOUNT_1) {
		POST_M_AMOUNT_1 = pOST_M_AMOUNT_1;
	}
	public String getPOST_M_GOODS_NAME_1() {
		return POST_M_GOODS_NAME_1;
	}
	public void setPOST_M_GOODS_NAME_1(String pOST_M_GOODS_NAME_1) {
		POST_M_GOODS_NAME_1 = pOST_M_GOODS_NAME_1;
	}
	public String getPOST_M_REMARK_1() {
		return POST_M_REMARK_1;
	}
	public void setPOST_M_REMARK_1(String pOST_M_REMARK_1) {
		POST_M_REMARK_1 = pOST_M_REMARK_1;
	}
	public String getPOST_M_AMOUNT_2() {
		return POST_M_AMOUNT_2;
	}
	public void setPOST_M_AMOUNT_2(String pOST_M_AMOUNT_2) {
		POST_M_AMOUNT_2 = pOST_M_AMOUNT_2;
	}
	public String getPOST_M_GOODS_NAME_2() {
		return POST_M_GOODS_NAME_2;
	}
	public void setPOST_M_GOODS_NAME_2(String pOST_M_GOODS_NAME_2) {
		POST_M_GOODS_NAME_2 = pOST_M_GOODS_NAME_2;
	}
	public String getPOST_M_REMARK_2() {
		return POST_M_REMARK_2;
	}
	public void setPOST_M_REMARK_2(String pOST_M_REMARK_2) {
		POST_M_REMARK_2 = pOST_M_REMARK_2;
	}
	public String getPOST_M_AMOUNT_3() {
		return POST_M_AMOUNT_3;
	}
	public void setPOST_M_AMOUNT_3(String pOST_M_AMOUNT_3) {
		POST_M_AMOUNT_3 = pOST_M_AMOUNT_3;
	}
	public String getPOST_M_GOODS_NAME_3() {
		return POST_M_GOODS_NAME_3;
	}
	public void setPOST_M_GOODS_NAME_3(String pOST_M_GOODS_NAME_3) {
		POST_M_GOODS_NAME_3 = pOST_M_GOODS_NAME_3;
	}
	public String getPOST_M_REMARK_3() {
		return POST_M_REMARK_3;
	}
	public void setPOST_M_REMARK_3(String pOST_M_REMARK_3) {
		POST_M_REMARK_3 = pOST_M_REMARK_3;
	}
	public String getPOST_M_AMOUNT_4() {
		return POST_M_AMOUNT_4;
	}
	public void setPOST_M_AMOUNT_4(String pOST_M_AMOUNT_4) {
		POST_M_AMOUNT_4 = pOST_M_AMOUNT_4;
	}
	public String getPOST_M_GOODS_NAME_4() {
		return POST_M_GOODS_NAME_4;
	}
	public void setPOST_M_GOODS_NAME_4(String pOST_M_GOODS_NAME_4) {
		POST_M_GOODS_NAME_4 = pOST_M_GOODS_NAME_4;
	}
	public String getPOST_M_REMARK_4() {
		return POST_M_REMARK_4;
	}
	public void setPOST_M_REMARK_4(String pOST_M_REMARK_4) {
		POST_M_REMARK_4 = pOST_M_REMARK_4;
	}
	public String getPOST_M_AMOUNT_5() {
		return POST_M_AMOUNT_5;
	}
	public void setPOST_M_AMOUNT_5(String pOST_M_AMOUNT_5) {
		POST_M_AMOUNT_5 = pOST_M_AMOUNT_5;
	}
	public String getPOST_M_GOODS_NAME_5() {
		return POST_M_GOODS_NAME_5;
	}
	public void setPOST_M_GOODS_NAME_5(String pOST_M_GOODS_NAME_5) {
		POST_M_GOODS_NAME_5 = pOST_M_GOODS_NAME_5;
	}
	public String getPOST_M_REMARK_5() {
		return POST_M_REMARK_5;
	}
	public void setPOST_M_REMARK_5(String pOST_M_REMARK_5) {
		POST_M_REMARK_5 = pOST_M_REMARK_5;
	}
	public String getPOST_M_AMOUNT_6() {
		return POST_M_AMOUNT_6;
	}
	public void setPOST_M_AMOUNT_6(String pOST_M_AMOUNT_6) {
		POST_M_AMOUNT_6 = pOST_M_AMOUNT_6;
	}
	public String getPOST_M_GOODS_NAME_6() {
		return POST_M_GOODS_NAME_6;
	}
	public void setPOST_M_GOODS_NAME_6(String pOST_M_GOODS_NAME_6) {
		POST_M_GOODS_NAME_6 = pOST_M_GOODS_NAME_6;
	}
	public String getPOST_M_REMARK_6() {
		return POST_M_REMARK_6;
	}
	public void setPOST_M_REMARK_6(String pOST_M_REMARK_6) {
		POST_M_REMARK_6 = pOST_M_REMARK_6;
	}
	public String getPOST_M_AMOUNT_7() {
		return POST_M_AMOUNT_7;
	}
	public void setPOST_M_AMOUNT_7(String pOST_M_AMOUNT_7) {
		POST_M_AMOUNT_7 = pOST_M_AMOUNT_7;
	}
	public String getPOST_M_GOODS_NAME_7() {
		return POST_M_GOODS_NAME_7;
	}
	public void setPOST_M_GOODS_NAME_7(String pOST_M_GOODS_NAME_7) {
		POST_M_GOODS_NAME_7 = pOST_M_GOODS_NAME_7;
	}
	public String getPOST_M_REMARK_7() {
		return POST_M_REMARK_7;
	}
	public void setPOST_M_REMARK_7(String pOST_M_REMARK_7) {
		POST_M_REMARK_7 = pOST_M_REMARK_7;
	}
	public String getRECEIVED_PORTAL_CODE() {
		return RECEIVED_PORTAL_CODE;
	}
	public void setRECEIVED_PORTAL_CODE(String rECEIVED_PORTAL_CODE) {
		RECEIVED_PORTAL_CODE = rECEIVED_PORTAL_CODE;
	}
	public String getRECEIVED_SHOP_CODE() {
		return RECEIVED_SHOP_CODE;
	}
	public void setRECEIVED_SHOP_CODE(String rECEIVED_SHOP_CODE) {
		RECEIVED_SHOP_CODE = rECEIVED_SHOP_CODE;
	}
	public String getRECEIVED_BANK_CODE() {
		return RECEIVED_BANK_CODE;
	}
	public void setRECEIVED_BANK_CODE(String rECEIVED_BANK_CODE) {
		RECEIVED_BANK_CODE = rECEIVED_BANK_CODE;
	}
	public String getRECEIVED_KESSAI_FLAG() {
		return RECEIVED_KESSAI_FLAG;
	}
	public void setRECEIVED_KESSAI_FLAG(String rECEIVED_KESSAI_FLAG) {
		RECEIVED_KESSAI_FLAG = rECEIVED_KESSAI_FLAG;
	}
	public String getRECEIVED_CTRL_NO() {
		return RECEIVED_CTRL_NO;
	}
	public void setRECEIVED_CTRL_NO(String rECEIVED_CTRL_NO) {
		RECEIVED_CTRL_NO = rECEIVED_CTRL_NO;
	}
	public String getRECEIVED_TRAN_STAT() {
		return RECEIVED_TRAN_STAT;
	}
	public void setRECEIVED_TRAN_STAT(String rECEIVED_TRAN_STAT) {
		RECEIVED_TRAN_STAT = rECEIVED_TRAN_STAT;
	}
	public String getRECEIVED_TRAN_REASON_CODE() {
		return RECEIVED_TRAN_REASON_CODE;
	}
	public void setRECEIVED_TRAN_REASON_CODE(String rECEIVED_TRAN_REASON_CODE) {
		RECEIVED_TRAN_REASON_CODE = rECEIVED_TRAN_REASON_CODE;
	}
	public String getRECEIVED_TRAN_RESULT_MSG() {
		return RECEIVED_TRAN_RESULT_MSG;
	}
	public void setRECEIVED_TRAN_RESULT_MSG(String rECEIVED_TRAN_RESULT_MSG) {
		RECEIVED_TRAN_RESULT_MSG = rECEIVED_TRAN_RESULT_MSG;
	}
	public String getRECEIVED_TRAN_DATE() {
		return RECEIVED_TRAN_DATE;
	}
	public void setRECEIVED_TRAN_DATE(String rECEIVED_TRAN_DATE) {
		RECEIVED_TRAN_DATE = rECEIVED_TRAN_DATE;
	}
	public String getRECEIVED_TRAN_TIME() {
		return RECEIVED_TRAN_TIME;
	}
	public void setRECEIVED_TRAN_TIME(String rECEIVED_TRAN_TIME) {
		RECEIVED_TRAN_TIME = rECEIVED_TRAN_TIME;
	}
	public String getRECEIVED_CUST_NAME() {
		return RECEIVED_CUST_NAME;
	}
	public void setRECEIVED_CUST_NAME(String rECEIVED_CUST_NAME) {
		RECEIVED_CUST_NAME = rECEIVED_CUST_NAME;
	}
	public String getRECEIVED_CUST_LNAME() {
		return RECEIVED_CUST_LNAME;
	}
	public void setRECEIVED_CUST_LNAME(String rECEIVED_CUST_LNAME) {
		RECEIVED_CUST_LNAME = rECEIVED_CUST_LNAME;
	}
	public String getRECEIVED_CUST_FNAME() {
		return RECEIVED_CUST_FNAME;
	}
	public void setRECEIVED_CUST_FNAME(String rECEIVED_CUST_FNAME) {
		RECEIVED_CUST_FNAME = rECEIVED_CUST_FNAME;
	}
	public String getRECEIVED_TRAN_AMOUNT() {
		return RECEIVED_TRAN_AMOUNT;
	}
	public void setRECEIVED_TRAN_AMOUNT(String rECEIVED_TRAN_AMOUNT) {
		RECEIVED_TRAN_AMOUNT = rECEIVED_TRAN_AMOUNT;
	}
	public String getRECEIVED_TRAN_FEE() {
		return RECEIVED_TRAN_FEE;
	}
	public void setRECEIVED_TRAN_FEE(String rECEIVED_TRAN_FEE) {
		RECEIVED_TRAN_FEE = rECEIVED_TRAN_FEE;
	}
	public String getRECEIVED_PAYMENT_DAY() {
		return RECEIVED_PAYMENT_DAY;
	}
	public void setRECEIVED_PAYMENT_DAY(String rECEIVED_PAYMENT_DAY) {
		RECEIVED_PAYMENT_DAY = rECEIVED_PAYMENT_DAY;
	}
	public String getRECEIVED_GOODS_NAME() {
		return RECEIVED_GOODS_NAME;
	}
	public void setRECEIVED_GOODS_NAME(String rECEIVED_GOODS_NAME) {
		RECEIVED_GOODS_NAME = rECEIVED_GOODS_NAME;
	}
	public String getRECEIVED_REMARKS_1() {
		return RECEIVED_REMARKS_1;
	}
	public void setRECEIVED_REMARKS_1(String rECEIVED_REMARKS_1) {
		RECEIVED_REMARKS_1 = rECEIVED_REMARKS_1;
	}
	public String getRECEIVED_REMARKS_2() {
		return RECEIVED_REMARKS_2;
	}
	public void setRECEIVED_REMARKS_2(String rECEIVED_REMARKS_2) {
		RECEIVED_REMARKS_2 = rECEIVED_REMARKS_2;
	}
	public String getRECEIVED_REMARKS_3() {
		return RECEIVED_REMARKS_3;
	}
	public void setRECEIVED_REMARKS_3(String rECEIVED_REMARKS_3) {
		RECEIVED_REMARKS_3 = rECEIVED_REMARKS_3;
	}
	public String getRECEIVED_TRAN_ID() {
		return RECEIVED_TRAN_ID;
	}
	public void setRECEIVED_TRAN_ID(String rECEIVED_TRAN_ID) {
		RECEIVED_TRAN_ID = rECEIVED_TRAN_ID;
	}
	public String getRECEIVED_TRAN_DIGEST() {
		return RECEIVED_TRAN_DIGEST;
	}
	public void setRECEIVED_TRAN_DIGEST(String rECEIVED_TRAN_DIGEST) {
		RECEIVED_TRAN_DIGEST = rECEIVED_TRAN_DIGEST;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the bJP_URL
	 */
	public String getBJP_URL() {
		return BJP_URL;
	}
	/**
	 * @param bJP_URL the bJP_URL to set
	 */
	public void setBJP_URL(String bJP_URL) {
		BJP_URL = bJP_URL;
	}
	
}
