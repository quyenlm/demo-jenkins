package phn.nts.ams.fe.business;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoService.AmsBoAdditionalInfoUpdateRequest;

import phn.com.nts.db.entity.AmsCustomerService;
import phn.nts.ams.fe.domain.BoTestInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.model.TestBoModel;

public interface ITestBoManager {
	public String getMinPointPass(String wlCode);
	public Integer getBoCustomerStatus(String customerId);
	public boolean checkBoTestToDay(String customerServiceId,String frontDate);
	public boolean insertAmsTestSummary(CustomerServicesInfo info,String frontDate);
	public List<BoTestInfo> getListBoTest(String frontDate,String wlCode,String customerServiceId);
	public void insertCustomerTest(String customerServiceId,int point,BoTestInfo info);
	public String updateAmsTestSummary(String customerServiceId,String frontDate,int testPoint,int totalPoint,int resultTest);
	public void updateCustomer(String customerId,String customerServiceId,String wlCode,String fullname, String frontDate, ExecutorService executorService) throws Exception;
	boolean generatePackageTest(String customerServiceId, String wlCode);
	public boolean checkBoTestComplete(String customerServiceId);
	public AmsCustomerService updateAmsBoAdditionalInfo(AmsBoAdditionalInfoUpdateRequest request);
	public String getCurrentBizDate();
	public void updateBoTestResultToAmsCustomerTrace(TestBoModel model);
}
