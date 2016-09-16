package phn.nts.ams.fe.business.impl;

import java.util.List;

import phn.com.nts.db.dao.IAmsWhitelabelReportDAO;
import phn.com.nts.db.entity.AmsWhitelabelReport;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IProfileManager;
import phn.nts.ams.fe.business.IReportManager;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.social.SCManager;
import phn.nts.ams.utils.Converter;
import cn.nextop.social.api.admin.proto.CustomerModelProto.CustomerReport;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;

public class ReportManagerImpl implements IReportManager {
	private static final Logit log = Logit.getInstance(IReportManager.class);

	private IProfileManager profileManager;
	private IAmsWhitelabelReportDAO<AmsWhitelabelReport> amsWhitelabelReportDAO;
	
	@Override
	public AmsCustomerReportsResponse getCustomerReportBoOrAms(AmsCustomerReportsRequest request, Integer tradingType) {
		String fromDate = request.hasFromDate() ? request.getFromDate() : "";
		String toDate = request.hasToDate() ? request.getToDate() : "";
		String reportType = request.hasReportType() ? Converter.convertToReportTypeDb(request.getReportType()) : "";
		Integer reportFileType = request.hasFileType() ? Converter.convertToReportFileTypeDb(request.getFileType()) : null;
		String wlCode = request.hasWlCode() ? request.getWlCode() : "";
		String customerId = request.hasCustomerId() ? request.getCustomerId() : "";
		
		Integer pageNumber = null;
		if (request.hasPageNumber() && request.getPageNumber() > 0)
			pageNumber = request.getPageNumber();
		
		Integer pageSize = null;
		if (request.hasPageSize() && request.getPageSize() > 0)
			pageSize = request.getPageSize();

		List<AmsWhitelabelReport> amsWhitelabeReports = amsWhitelabelReportDAO.getCustomerReportBoOrAms(fromDate, toDate,
				reportType, reportFileType, wlCode, customerId, pageNumber, pageSize, tradingType);
		
		return Converter.convertCustomerReportsBoOrAms(amsWhitelabeReports);
	}
	
	@Override
	public AmsCustomerReportsResponse getCustomerReportSc(AmsCustomerReportsRequest request) {

		cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType reportTypeSc 
			= request.hasReportType() ? Converter.convertReportType(request.getReportType()) : null;
			
		cn.nextop.social.api.admin.proto.CustomerModelProto.FileType fileTypeSc 
			= request.hasFileType() ? Converter.convertFileTypeSc(request.getFileType()) : null;

		String wlCode = request.hasWlCode() ? request.getWlCode() : "";
		Integer pageNumber = request.hasPageNumber() ? request.getPageNumber() : null;
		Integer pageSize = request.hasPageSize() ? request.getPageSize() : null;
		String fromDate = request.hasFromDate() ? request.getFromDate() : "";
		String toDate = request.hasToDate() ? request.getToDate() : "";
		
		CustomerServicesInfo serviceInfo = profileManager.getCustomerService(request.getCustomerId(), request.getServiceType().getNumber());
		String customerServiceId;
		if (serviceInfo == null) {
			log.info("getCustomerReportSc customer [" + request.getCustomerId() + "] does not exist!");
			return null;
		} else {
			customerServiceId = serviceInfo.getCustomerServiceId();
		}

		List<CustomerReport> customerReports = SCManager.getInstance().getCustomerReportSc(fromDate, toDate,
				reportTypeSc, fileTypeSc, wlCode, customerServiceId, pageNumber, pageSize);

		return Converter.convertCustomerReportsSc(customerReports);
	}

	public IAmsWhitelabelReportDAO<AmsWhitelabelReport> getAmsWhitelabelReportDAO() {
		return amsWhitelabelReportDAO;
	}

	public void setAmsWhitelabelReportDAO(IAmsWhitelabelReportDAO<AmsWhitelabelReport> amsWhitelabelReportDAO) {
		this.amsWhitelabelReportDAO = amsWhitelabelReportDAO;
	}

	public IProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(IProfileManager profileManager) {
		this.profileManager = profileManager;
	}

}
