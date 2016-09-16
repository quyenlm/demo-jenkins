package phn.nts.ams.fe.web.action.report;

import phn.com.nts.util.common.IConstants;
import phn.nts.ams.fe.model.ReportModel;
import phn.nts.social.fe.web.action.BaseSocialAction;

public class ReportAction extends BaseSocialAction<ReportModel> {
	
	ReportModel model = new ReportModel();
	
	public ReportModel getModel() {
		return model;
	}
	public String index() {
		setRawUrl(IConstants.FrontEndActions.REPORT_INDEX);
		return SUCCESS;
	}
}
