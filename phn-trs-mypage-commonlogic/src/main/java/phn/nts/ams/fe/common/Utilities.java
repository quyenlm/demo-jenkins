package phn.nts.ams.fe.common;

import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.FundResultRecord;
import com.phn.mt.common.entity.MarginLevel;
import com.phn.mt.common.entity.UserRecord;

import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;

import javax.jms.JMSException;
import javax.jms.MapMessage;

public class Utilities {
	private static final Logit LOG = Logit.getInstance(Utilities.class);
	public static UserRecord convertUserRecord(MapMessage mapMessage) throws JMSException{
		UserRecord userRecord = new UserRecord();
		userRecord.setAddress(mapMessage.getString("address"));
		userRecord.setAgent_account(mapMessage.getInt("agent_account"));
		userRecord.setApiData(mapMessage.getString("apiData"));
		userRecord.setBalance(MathUtil.parseDouble(mapMessage.getString("balance")));
		userRecord.setBalanceStatus(mapMessage.getInt("balanceStatus"));
		userRecord.setCity(mapMessage.getString("city"));
		userRecord.setComment(mapMessage.getString("comment"));
		userRecord.setCountry(mapMessage.getString("country"));
		userRecord.setCredit(MathUtil.parseDouble(mapMessage.getString("credit")));
		userRecord.setEmail(mapMessage.getString("email"));
		userRecord.setEnable(mapMessage.getInt("enable"));
		userRecord.setEnableChangePassword(mapMessage.getInt("enableChangePassword"));
		userRecord.setEnableReadOnly(mapMessage.getInt("enableReadOnly"));
		userRecord.setErrorMessage(mapMessage.getString("errorMessage"));
		userRecord.setErrorType(mapMessage.getInt("errorType"));
		userRecord.setGroup(mapMessage.getString("group"));
		userRecord.setId(mapMessage.getString("id"));
		userRecord.setInterestRate(MathUtil.parseDouble(mapMessage.getString("interestRate")));
		userRecord.setLastDate(mapMessage.getLong("lastDate"));
		userRecord.setLeverage(mapMessage.getInt("leverage"));
		userRecord.setLogin(mapMessage.getInt("login"));
		userRecord.setLoginAccount(mapMessage.getInt("loginAccount"));
		userRecord.setMsgObj(mapMessage.getObject("msgObj"));
		userRecord.setMsgType(mapMessage.getInt("msgType"));
		userRecord.setName(mapMessage.getString("name"));
		userRecord.setPassAccount(mapMessage.getString("passAccount"));
		userRecord.setPassword(mapMessage.getString("password"));
		userRecord.setPasswordInvestor(mapMessage.getString("passwordInvestor"));
		userRecord.setPasswordPhone(mapMessage.getString("passwordPhone"));
		userRecord.setPhone(mapMessage.getString("phone"));
		userRecord.setPrevBalance(MathUtil.parseDouble(mapMessage.getString("prevBalance")));
		userRecord.setPrevEquity(MathUtil.parseDouble(mapMessage.getString("prevEquity")));
		userRecord.setPrevMonthBalance(MathUtil.parseDouble(mapMessage.getString("prevMonthBalance")));
		userRecord.setPrevMonthEquity(MathUtil.parseDouble(mapMessage.getString("prevMonthEquity")));
		userRecord.setPublicKey(mapMessage.getString("publicKey"));
		userRecord.setRegDate(mapMessage.getLong("regDate"));
		userRecord.setReserved(mapMessage.getInt("reserved"));
		userRecord.setResult(mapMessage.getInt("resultCode"));
		userRecord.setSendReports(mapMessage.getInt("sendReports"));
		userRecord.setSequenceID(mapMessage.getString("sequenceID"));
		userRecord.setSignalProvider(mapMessage.getInt("signalProvider"));
		userRecord.setState(mapMessage.getString("state"));
		userRecord.setStatus(mapMessage.getString("status"));
		userRecord.setTaxes(MathUtil.parseDouble(mapMessage.getString("taxes")));
		userRecord.setTimestamp(mapMessage.getLong("timestamp"));
		userRecord.setUnused(mapMessage.getString("unused"));
		userRecord.setZipcode(mapMessage.getString("zipcode"));
		
		return userRecord;
	}
	
	
	public static MarginLevel convertMarginLevel(MapMessage mapMessage) throws JMSException{
		MarginLevel marginLevel = new MarginLevel();
		
		marginLevel.setBalance(MathUtil.parseDouble(mapMessage.getString("balance")));
		marginLevel.setCredit(MathUtil.parseDouble(mapMessage.getString("credit")));
		marginLevel.setDepositCurrency(mapMessage.getString("depositCurrency"));
		marginLevel.setEquity(MathUtil.parseDouble(mapMessage.getString("equity")));
		marginLevel.setErrorType(mapMessage.getInt("errorType"));
		marginLevel.setGroup(mapMessage.getString("group"));
		marginLevel.setLeverage(mapMessage.getInt("leverage"));
		marginLevel.setLogin(mapMessage.getInt("login"));
		marginLevel.setMargin(MathUtil.parseDouble(mapMessage.getString("margin")));
		marginLevel.setMarginCallLevel(mapMessage.getInt("marginCallLevel"));
		marginLevel.setMarginFree(MathUtil.parseDouble(mapMessage.getString("marginFree")));
		marginLevel.setMarginLevel(MathUtil.parseDouble(mapMessage.getString("marginLevel")));
		marginLevel.setMarginShort(MathUtil.parseDouble(mapMessage.getString("marginShort")));
		marginLevel.setMarginStatusId(mapMessage.getInt("marginStatusId"));
		marginLevel.setMarginTime(mapMessage.getLong("marginTime"));
		marginLevel.setMarginType(mapMessage.getInt("marginType"));
		marginLevel.setMaxWithdrawBalance(MathUtil.parseDouble(mapMessage.getString("maxWithdrawBalance")));
		marginLevel.setMsgType(mapMessage.getInt("msgType"));
		marginLevel.setResult(mapMessage.getInt("resultCode"));
		marginLevel.setRetValue(mapMessage.getInt("retValue"));
		marginLevel.setSequenceID(mapMessage.getString("sequenceID"));
		marginLevel.setStopOutLevel(mapMessage.getInt("stopOutLevel"));
		marginLevel.setUpdated(mapMessage.getInt("updated"));
		marginLevel.setVolume(mapMessage.getInt("volume"));
		marginLevel.setMsgObj(mapMessage.getObject("msgObj"));
		if(mapMessage.getString("unrealizedPl") != null)
			marginLevel.setUnrealizedPl(MathUtil.parseDouble(mapMessage.getString("unrealizedPl")));
		
		return marginLevel;
	}
	
	
	public static FundRecord convertFundRecord(MapMessage mapMessage){
		try {
			FundRecord fundRecord = new FundRecord();
			fundRecord.setActiveFlg(mapMessage.getBoolean("activeFlg"));
			fundRecord.setConfirmDate(mapMessage.getLong("confirmDate"));
			fundRecord.setCurrentDate(mapMessage.getLong("currentDate"));
			fundRecord.setCustomerId(mapMessage.getInt("customerId"));
			fundRecord.setDealerId(mapMessage.getInt("dealerId"));
			fundRecord.setDepositCurrency(mapMessage.getString("depositCurrency"));
			fundRecord.setDescription(mapMessage.getString("description"));
			fundRecord.setErrorType(mapMessage.getInt("errorType"));
			fundRecord.setFundType(mapMessage.getInt("fundType"));
			fundRecord.setMsgType(mapMessage.getInt("msgType"));
			fundRecord.setRequestDate(mapMessage.getLong("requestDate"));
			fundRecord.setResult(mapMessage.getInt("resultCode"));
			fundRecord.setSequenceID(mapMessage.getString("sequenceID"));
			fundRecord.setUpdateDate(mapMessage.getLong("updateDate"));
			fundRecord.setVolume(mapMessage.getString("volume"));
			fundRecord.setWithdrawStatus(mapMessage.getInt("withdrawStatus"));
			fundRecord.setFundCreditMode(mapMessage.getInt("fundCreditMode"));
			fundRecord.setOrderTicket(mapMessage.getInt("orderTicket"));
			
			FundResultRecord fundResultRecord = new FundResultRecord();
			fundResultRecord.setCreditDeduction(MathUtil.parseDouble(mapMessage.getString("creditDeduction")));
			fundRecord.setFundResultRecord(fundResultRecord);
			
			return fundRecord;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
}
