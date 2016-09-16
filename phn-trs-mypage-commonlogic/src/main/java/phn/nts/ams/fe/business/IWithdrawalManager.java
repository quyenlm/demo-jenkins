package phn.nts.ams.fe.business;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCustomerEwallet;
import phn.com.nts.db.entity.AmsWithdrawal;
import phn.nts.ams.fe.domain.CustomerBankInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.LibertyReservedInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.WithdrawalInfo;
import phn.nts.ams.fe.model.WithdrawalModel;

public interface IWithdrawalManager {
	
	public LinkedHashMap<String, String> getListCountry();
	
	public List<WithdrawalInfo> getWithdrawlHistory(String customerId, PagingInfo pagingInfo, String status);
	
	public Integer withdrawalIBBankTransfer(WithdrawalInfo withdrawalInfo, String customerServiceId);
	
	public Double getCashBalance(String customerId, String currencyCode, Integer serviceType);
	
	public BigDecimal getTotalWithdrawalAmount(String customerId, String type);
	
	public Integer summaryOfWithDrawalPerday(String customerId, String acceptDate);
	
	public AmsWithdrawal getAmsWithdrawal(String withdrawalId);
	
	public void updateStatusofWithdrawal(AmsWithdrawal amsWithdrawal, String customerId);
	
	public Double getTotalWithdrawalAmount(String customerId, Integer status, Integer serviceType);
	
	public List<CustomerBankInfo> getListCustomerBankInfo(String customerId);
	
	public List<AmsCustomerEwallet> getListAmsCustomerEwallet(String customerId, Integer ewalletType) ;
	
	public List<CustomerEwalletInfo> getCustomerEwalletInfo(String customerId, Integer ewalletType);
	
	public CustomerInfo getCustomerInfo(String customerId);
	
	public Integer withdrawalBankTransfer(CustomerBankInfo bankwireInfo);
	
	public Integer withdrawalNeteller(NetellerInfo netellerInfo);
	
	public Integer withdrawalPayza(PayzaInfo payzaInfo);
	
	public CustomerBankInfo  getCustomerBankInfo(String customerBankId);
	
	public String getCountryName(Integer countryId);
	
	public Integer withdrawalExchanger(ExchangerInfo exchangerInfo);
	
	public Integer withdrawalCreditCard(WithdrawalInfo withdrawalInfo);
	
	public Double getWithdrawalFee(Double amount, String currencyCode, Integer paymentgwId, String wlCode);
	
	public Integer withdrawalLiberty(LibertyInfo libertyInfo);
	
	public CustomerEwalletInfo getLibertyInfo(String customerId, String accountId, String publicKey);

	public void checkWithdrawalRules(WithdrawalModel withdrawalModel);

	public void updateAmsCustomerSurvey(String currentCustomerId, BigDecimal withdrawalAmount);

	public void loadWithdrawalRuleInfo(WithdrawalModel withdrawalModel);

	public void withdrawMoney(WithdrawalModel withdrawalModel);
	
	public boolean checkWithDrawalLimitPerDay(WithdrawalModel withdrawalModel);
	
	public boolean checkWithDrawalLimitPerDay();
	
	public Integer getCountAkazan(String customerId);
	
	public Integer getCountAkazanNegativeBlance(String customerId);
}
