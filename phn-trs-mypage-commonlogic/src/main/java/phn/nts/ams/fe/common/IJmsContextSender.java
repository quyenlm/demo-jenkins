package phn.nts.ams.fe.common;

import java.util.ArrayList;

import phn.com.components.trs.ams.mail.TrsMailTemplateInfo;

import com.nts.common.exchange.bean.BalanceUpdateInfo;
import com.nts.components.mail.bean.AmsMailTemplateInfo;
import com.nts.components.mail.bean.AmsScMailTemplateInfo;
import com.nts.components.mail.bean.MailTemplate;
import com.phn.mt.common.entity.FundRecord;
import com.phn.mt.common.entity.MarginLevel;
import com.phn.mt.common.entity.UserRecord;

public interface IJmsContextSender {
	public void sendMarginRequest(MarginLevel marginLevel, boolean isMapMessage);
	public void sendWithdrawRequest(FundRecord fundRecord, boolean isMapMessage);
	public void sendOpenAccountRequest(ArrayList<UserRecord> listRecords, boolean isMapMessage);
	public void sendEditAccountRequest(UserRecord userRecord, boolean isMapMessage);
	public void sendChangePasswordAccountRequest(UserRecord userRecord, boolean isMapMessage);
	public void sendMail(AmsMailTemplateInfo amsMailTemplateInfo, boolean isMapMessage);
	public void sendMail(AmsScMailTemplateInfo amsScMailTemplateInfo, boolean isMapMessage);
	public void sendOpenDemoAccountRequest(ArrayList<UserRecord> listRecords, boolean isMapMessage);
	public void sendEditDemoAccountRequest(UserRecord userRecord, boolean isMapMessage);
	public void sendBalanceUpdateTopic(BalanceUpdateInfo balanceUpdateInfo, boolean isMapMessage);
	public void sendMail(TrsMailTemplateInfo amsMailTemplateInfo , boolean isMapMessage);
	public void sendMailTemplate(MailTemplate mailTemplateInfo);
}
