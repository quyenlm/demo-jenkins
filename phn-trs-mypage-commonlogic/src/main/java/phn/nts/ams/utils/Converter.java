package phn.nts.ams.utils;


import java.util.List;

import phn.com.nts.db.entity.AmsWhitelabelReport;
import phn.com.nts.util.common.StringUtil;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.nts.ams.fe.domain.BalanceInfo;
import cn.nextop.social.api.admin.proto.CustomerModelProto.CustomerReport;
import cn.nextop.social.api.admin.proxy.glossary.AccountStatementResult;

import com.nts.common.exchange.proto.ams.AmsCustomerModel.CustomerReportInfo;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.ReportType;
import com.nts.common.exchange.proto.ams.AmsCustomerModel.ServiceType;
import com.nts.common.exchange.proto.ams.AmsCustomerService.CustomerReportsRequest;
import com.nts.common.exchange.proto.ams.AmsCustomerService.CustomerReportsResponse;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerReportInfo;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.FileType;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;

/**
 * @description Convert bean to bean
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jul 9, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class Converter {
	/**
	 * Convert NTDBalanceInfo to BalanceInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
	public static BalanceInfo convertBalanceInfo(com.nts.common.exchange.proto.ams.AmsCustomerModel.BalanceInfo ntdBalanceInfo) {
		if(ntdBalanceInfo == null)
			return null;
		
		BalanceInfo balanceInfo = new BalanceInfo();
		balanceInfo.setResult(AccountBalanceResult.valueOf(ntdBalanceInfo.getStatus().getNumber()));
				
		balanceInfo.setBalance(StringUtil.isEmpty(ntdBalanceInfo.getBalance()) ?
				new Double("0") : Double.parseDouble(ntdBalanceInfo.getBalance()));
		
		balanceInfo.setEquity(StringUtil.isEmpty(ntdBalanceInfo.getEquity()) ?
				new Double("0") : Double.parseDouble(ntdBalanceInfo.getEquity()));
		
		balanceInfo.setMargin(StringUtil.isEmpty(ntdBalanceInfo.getMargin()) ?
				new Double("0") :  Double.parseDouble(ntdBalanceInfo.getMargin()));
		
		balanceInfo.setFreemargin(StringUtil.isEmpty(ntdBalanceInfo.getFreeMargin()) ? 
				new Double("0") : Double.parseDouble(ntdBalanceInfo.getFreeMargin()));
		
		balanceInfo.setMarginLevel(StringUtil.isEmpty(ntdBalanceInfo.getMarginLevel()) ?
				 new Double("0") : Double.parseDouble(ntdBalanceInfo.getMarginLevel()));
		
		balanceInfo.setCredit(StringUtil.isEmpty(ntdBalanceInfo.getCredit())
				? new Double("0") : Double.parseDouble(ntdBalanceInfo.getCredit()));
		
		balanceInfo.setUnrealizedPl(StringUtil.isContainSpecialChars(ntdBalanceInfo.getUnrealizedPl())
				? new Double("0") : Double.parseDouble(ntdBalanceInfo.getUnrealizedPl()));
		
		//Calculate availabelAmount
		Double availabelAmount = balanceInfo.getFreemargin() - balanceInfo.getCredit();
		if(balanceInfo.getBalance() <= 0 || availabelAmount <= 0)
			balanceInfo.setAmountAvailable(new Double("0"));
		else
			balanceInfo.setAmountAvailable(Math.min(balanceInfo.getBalance(), availabelAmount));
		return balanceInfo;
	}
	
	/**
	 * Convert NTDBalanceInfo to BalanceInfo
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Jul 13, 2015
	 * @MdDate
	 */
	public static BalanceInfo convertBalanceInfo(cn.nextop.social.api.admin.proxy.model.trading.AccountStatement scAccountStatement) {
		if(scAccountStatement == null)
			return null;
		
		BalanceInfo balanceInfo = new BalanceInfo();
		balanceInfo.setResult(convertAccountBalanceResult(scAccountStatement.getAccountStatementResult()));
		if(!AccountStatementResult.SUCCESS.equals(scAccountStatement.getAccountStatementResult()))
			return balanceInfo;
		
		balanceInfo.setBalance(scAccountStatement.getBalance() == null ?
				new Double("0") : scAccountStatement.getBalance().doubleValue());
		
		balanceInfo.setEquity(scAccountStatement.getEquity() == null ?
				new Double("0") : scAccountStatement.getEquity().doubleValue());
		
		balanceInfo.setFreemargin(scAccountStatement.getFreeMargin() == null ?
				new Double("0") : scAccountStatement.getFreeMargin().doubleValue());
		
		balanceInfo.setMarginLevel(scAccountStatement.getMarginRatio() == null ?
				 new Double("0") : scAccountStatement.getMarginRatio().doubleValue());
		
		balanceInfo.setCredit(new Double("0"));
		balanceInfo.setUnrealizedPl(scAccountStatement.getOpenPl() == null ? new Double("0") : scAccountStatement.getOpenPl().doubleValue());
		balanceInfo.setMargin(scAccountStatement.getRequiredMargin() == null ? new Double("0") : scAccountStatement.getRequiredMargin().doubleValue());
		balanceInfo.setRequiredMargin(scAccountStatement.getRequiredMargin() == null ? new Double("0") : scAccountStatement.getRequiredMargin().doubleValue());
		
		//Calculate availabelAmount
		balanceInfo.setAmountAvailable(scAccountStatement.getWithdrawableAmount() == null ? new Double("0") : scAccountStatement.getWithdrawableAmount().doubleValue());
		//ReservedAmount = balance - withdrawable_amount						
		balanceInfo.setRequestingAmount(balanceInfo.getBalance() - balanceInfo.getAmountAvailable());
		
		return balanceInfo;
	}
	
	public static AccountBalanceResult convertAccountBalanceResult(AccountStatementResult statementResult) {
		if (AccountStatementResult.INTERNAL_ERROR.equals(statementResult))
			return AccountBalanceResult.INTERNAL_ERROR;
		
		return AccountBalanceResult.valueOf(Integer.valueOf(statementResult.getValue()));
	}
	
	public static CustomerReportsRequest convertCustomerReportsRequest(com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest amsCustomerReportsRequest, Long ntdAccountId) {
		CustomerReportsRequest.Builder builder = CustomerReportsRequest.newBuilder();
		
		builder.setStep(amsCustomerReportsRequest.getPageSize());
		if(amsCustomerReportsRequest.hasReportId())
			builder.setReportId(amsCustomerReportsRequest.getReportId());
		builder.setInclusive(amsCustomerReportsRequest.getInclusive());
		if(amsCustomerReportsRequest.hasServiceType())
			builder.setServiceType(ServiceType.valueOf(amsCustomerReportsRequest.getServiceType().getNumber()));
		if(amsCustomerReportsRequest.hasReportType())
			builder.setReportType(ReportType.valueOf(amsCustomerReportsRequest.getReportType().getNumber()));
		builder.setAccountId(ntdAccountId);
		builder.setReportDate1(amsCustomerReportsRequest.getFromDate());
		builder.setReportDate2(amsCustomerReportsRequest.getToDate());		
		
		return builder.build();
	}
	
	public static AmsCustomerReportsResponse convertAmsCustomerReportsResponse(CustomerReportsResponse customerReportsResponse) {
		AmsCustomerReportsResponse.Builder builder = AmsCustomerReportsResponse.newBuilder();
	
		for (CustomerReportInfo customerReportInfo : customerReportsResponse.getCustomerReportsList()) {
			builder.addCustomerReports(convertAmsCustomerReportInfo(customerReportInfo));
		}
		
		return builder.build();
	}
	
	public static AmsCustomerReportInfo convertAmsCustomerReportInfo(CustomerReportInfo customerReportInfo) {
		AmsCustomerReportInfo.Builder builder = AmsCustomerReportInfo.newBuilder();
		builder.setTitle(customerReportInfo.getTitle());
		builder.setReportId(customerReportInfo.getReportId());
		builder.setReportDate(customerReportInfo.getIssueDate());
		builder.setReportType(com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.valueOf(
				customerReportInfo.getReportType().getNumber()));
		builder.setFileType(com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.FileType.valueOf(
				customerReportInfo.getFileType().getNumber()));
		builder.setServiceType(com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ServiceType.valueOf(
				customerReportInfo.getServiceType().getNumber()));
		builder.setReportUrl(customerReportInfo.getReportUrl());
		return builder.build();
	}

	public static AmsCustomerReportsResponse convertCustomerReportsBoOrAms(List<AmsWhitelabelReport> amsWhitelabeReports) {
		AmsCustomerReportsResponse.Builder builder = AmsCustomerReportsResponse.newBuilder();
		
		for (AmsWhitelabelReport amsWhitelabelReport : amsWhitelabeReports) {
			builder.addCustomerReports(convertAmsCustomerReportBoOrAms(amsWhitelabelReport));
		}
		
		return builder.build();
	}

	private static AmsCustomerReportInfo convertAmsCustomerReportBoOrAms(AmsWhitelabelReport amsWhitelabelReport) {
		AmsCustomerReportInfo.Builder builder = AmsCustomerReportInfo.newBuilder();

		builder.setTitle(amsWhitelabelReport.getReportTitle());
		builder.setReportId(amsWhitelabelReport.getReportId());
		builder.setReportDate(amsWhitelabelReport.getReportDate());
		builder.setReportUrl(amsWhitelabelReport.getLink());
		builder.setReportType(converToReportTypeProto(amsWhitelabelReport.getReportType()));
		
		return builder.build();
	}

	private static com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType converToReportTypeProto(
			String reportTypeDb) {
		if (ITrsConstants.REPORT_TYPE_DB.DAILY.equalsIgnoreCase(reportTypeDb)) {
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_DAILY;
		} else if (ITrsConstants.REPORT_TYPE_DB.MONTHLY.equalsIgnoreCase(reportTypeDb)) {
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_MONTHLY;
		} else if (ITrsConstants.REPORT_TYPE_DB.YEARLY.equalsIgnoreCase(reportTypeDb)) {
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_YEARLY;
		}
		return null;
	}

	public static String convertToReportTypeDb(com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType reportTypeProto) {
		switch (reportTypeProto) {
		case REPORT_DAILY:
			return ITrsConstants.REPORT_TYPE_DB.DAILY;
		case REPORT_MONTHLY:
			return ITrsConstants.REPORT_TYPE_DB.MONTHLY;
		case REPORT_YEARLY:
			return ITrsConstants.REPORT_TYPE_DB.YEARLY;
		default:
			return null;
		}
	}

	public static Integer convertToReportFileTypeDb(FileType fileType) {
		switch (fileType) {
		case FILE_CSV:
			return ITrsConstants.REPORT_FILE_TYPE_DB.CSV;
		case FILE_PDF:
			return ITrsConstants.REPORT_FILE_TYPE_DB.PDF;
		default:
			return null;
		}
	}

	public static AmsCustomerReportsResponse convertCustomerReportsSc(List<cn.nextop.social.api.admin.proto.CustomerModelProto.CustomerReport> customerReports) {
		AmsCustomerReportsResponse.Builder builder = AmsCustomerReportsResponse.newBuilder();
		
		for (CustomerReport customerReport : customerReports) {
			builder.addCustomerReports(convertAmsCustomerReportSc(customerReport));
		}
		
		return builder.build();
	}

	private static AmsCustomerReportInfo convertAmsCustomerReportSc(CustomerReport customerReport) {
		AmsCustomerReportInfo.Builder builder = AmsCustomerReportInfo.newBuilder();

		builder.setTitle(customerReport.getReportTitle());
		builder.setReportId(customerReport.getReportId());
		builder.setReportDate(customerReport.getReportDate());
		builder.setReportUrl(customerReport.getDownloadUrl());
		builder.setReportType(converToReportTypeBoProto(customerReport.getReportType()));
		
		return builder.build();
	}

	private static com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType converToReportTypeBoProto(
			cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType reportType) {
		switch (reportType) {
		case DAILY:
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_DAILY;
		case MONTHLY:
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_MONTHLY;
		case YEARLY:
			return com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType.REPORT_YEARLY;
		default:
			return null;
		}
	}

	public static cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType convertReportType(
			com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.ReportType reportTypeMyPage) {
		switch (reportTypeMyPage) {
		case REPORT_DAILY:
			return cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType.DAILY;
		case REPORT_MONTHLY:
			return cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType.MONTHLY;
		case REPORT_YEARLY:
			return cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType.YEARLY;
		default:
			return null;
		}		
	}

	public static cn.nextop.social.api.admin.proto.CustomerModelProto.FileType convertFileTypeSc(FileType fileTypeMyPage) {
		switch (fileTypeMyPage) {
		case FILE_CSV:
			return cn.nextop.social.api.admin.proto.CustomerModelProto.FileType.CSV;
		case FILE_PDF:
			return cn.nextop.social.api.admin.proto.CustomerModelProto.FileType.PDF;
		default:
			return null;
		}
	}
}