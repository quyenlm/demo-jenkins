package phn.nts.ams.fe.business;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsRequest;
import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsCustomerReportsResponse;

public interface IReportManager {

	AmsCustomerReportsResponse getCustomerReportBoOrAms(AmsCustomerReportsRequest request, Integer tradingType);

	AmsCustomerReportsResponse getCustomerReportSc(AmsCustomerReportsRequest request);

}
