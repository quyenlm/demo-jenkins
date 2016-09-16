package phn.nts.ams.fe.social;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.util.Log;

import phn.com.nts.util.common.IConstants.TRANSFER_STATUS;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.enums.AccountBalanceResult;
import phn.com.trs.util.enums.SocialTransferFlg;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.utils.Converter;
import phn.nts.ams.utils.Helper;
import cn.nextop.social.api.admin.proto.CustomerModelProto.CustomerReport;
import cn.nextop.social.api.admin.proto.CustomerModelProto.FileType;
import cn.nextop.social.api.admin.proto.CustomerModelProto.ReportType;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferResponse;
import cn.nextop.social.api.admin.proto.TradingServiceProto.TransferStatus;
import cn.nextop.social.api.admin.proxy.glossary.AccountStatementResult;
import cn.nextop.social.api.admin.proxy.glossary.CloseAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.ModifyAccountResult;
import cn.nextop.social.api.admin.proxy.glossary.OpenAccountResult;
import cn.nextop.social.api.admin.proxy.model.customer.Customer;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccount;
import cn.nextop.social.api.admin.proxy.model.customer.CustomerAccountModification;
import cn.nextop.social.api.admin.proxy.model.trading.AccountStatement;
import cn.nextop.social.api.admin.proxy.rpc.RpcAdminContext;
import cn.nextop.social.api.admin.proxy.rpc.RpcAdminExporter;
import cn.nextop.social.api.admin.proxy.rpc.RpcAdminSession;
import cn.nextop.social.api.admin.proxy.service.customer.CustomerListener;
import cn.nextop.social.api.admin.proxy.service.customer.impl.CustomerAdministratorImpl;
import cn.nextop.social.api.admin.proxy.service.feeding.FeedingListener;
import cn.nextop.social.api.admin.proxy.service.feeding.impl.FeedingAdministratorImpl;
import cn.nextop.social.api.admin.proxy.service.trading.TradingListener;
import cn.nextop.social.api.admin.proxy.service.trading.impl.TradingAdministratorImpl;
import cn.nextop.social.api.common.proto.Rpc;
import cn.nextop.social.api.common.proto.util.RpcMessageMarshaller;
import cn.nextop.social.api.common.rpc.remoting.exporter.RpcService;
import cn.nextop.social.api.common.rpc.transport.impl.oio.RpcOioTransport;

import com.google.common.collect.Lists;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Feb 16, 2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class SCManager {
	private static Logit LOG = Logit.getInstance(SCManager.class);
	private static SCManager instance;

	private int serverPort;
	private String serverHost;
	private boolean verbose = false;
	private long heartBeatInteval = 5000L; //miliseconds
	private long requestTimedOut = 1000L; //miliseconds
	
	private static Map<Class<?>, RpcService<RpcAdminContext, RpcAdminSession>> services;
    private static RpcAdminExporter exporter;
    
	public static SCManager getInstance() {
		if(instance == null) {
			instance = new SCManager();
		}
		return instance;
	}
	
	public void initConnectionToServer() {
		if(services == null) {
			LOG.info("Init Connection to SocialApi...");
			services = new HashMap<Class<?>, RpcService<RpcAdminContext, RpcAdminSession>>();
	
	        // Transport
	        RpcOioTransport<Rpc.RpcMessage> transport = new RpcOioTransport<Rpc.RpcMessage>();
	        transport.setMarshaller(new RpcMessageMarshaller());
	        transport.setPort(serverPort);
	        transport.setVerbose(verbose);
	        transport.setHost(serverHost);
	        
	        //System
//	        final SystemAdministratorImpl systemAdministrator = new SystemAdministratorImpl();
//	        services.put(SystemAdministratorImpl.class, systemAdministrator);
	
	        //Customer service
	        CustomerAdministratorImpl customerAdministrator = new CustomerAdministratorImpl();
	        customerAdministrator.setInvocationTimeout(requestTimedOut);
	        services.put(CustomerAdministratorImpl.class, customerAdministrator);
	        
	        //TradingAdministratorImpl
	        TradingAdministratorImpl tradingAdministratorImpl = new TradingAdministratorImpl();
	        tradingAdministratorImpl.setInvocationTimeout(requestTimedOut);
	        services.put(TradingAdministratorImpl.class, tradingAdministratorImpl);
	        
	        //FeedingAdministratorImpl
	        FeedingAdministratorImpl feedingAdministratorImpl = new FeedingAdministratorImpl();
	        feedingAdministratorImpl.setInvocationTimeout(requestTimedOut);
	        services.put(FeedingAdministratorImpl.class, feedingAdministratorImpl);
	        
	        // Exporter
	        exporter = new RpcAdminExporter();
	        exporter.setTransport(transport);
	        exporter.setServices(Lists.newArrayList(services.values()));
	        exporter.setVerbose(verbose);
	        exporter.setHeartbeatInterval(heartBeatInteval);
	        
	        LOG.info("Init Connection to SocialApi: DONE");
		}
	}
	
	public boolean addListener(Object listener) {
		if(exporter == null || services == null || listener == null)
			return false;
		if(listener instanceof CustomerListener && services.get(CustomerAdministratorImpl.class) != null) {
			((CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class)).addCustomerListener((CustomerListener)listener);
		} else if(listener instanceof TradingListener && services.get(TradingAdministratorImpl.class) != null) {
			((TradingAdministratorImpl)services.get(TradingAdministratorImpl.class)).addTradingListener((TradingListener)listener);
		} else if(listener instanceof FeedingListener && services.get(FeedingAdministratorImpl.class) != null) {
			((FeedingAdministratorImpl)services.get(FeedingAdministratorImpl.class)).addFeedingListener((FeedingListener)listener);
		}
		
		return true;
	}

	public void start() {
		if(exporter != null && exporter.getSession() == null)
			exporter.start();
	}
	
	/**
	 * Social getBalanceInfo　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
    public BalanceInfo getBalanceInfo(Integer acountId) {    	
		BalanceInfo balanceInfo = null;
		
		TradingAdministratorImpl tradingAdministratorImpl = (TradingAdministratorImpl)services.get(TradingAdministratorImpl.class);
		List<Integer> accounts = new ArrayList<Integer>();
		accounts.add(acountId);
		
		LOG.info("getBalanceInfo, acountId: " + acountId);
		List<AccountStatement> accountStatements = tradingAdministratorImpl.getAccountStatements(accounts);
		LOG.info("getBalanceInfo, acountId: " + acountId + ", accountStatements: " + accountStatements);
		
		if(accountStatements != null && accountStatements.size() > 0){
			AccountStatement account = accountStatements.get(0);
			if(AccountStatementResult.INTERNAL_ERROR.equals(account.getAccountStatementResult()))
				LOG.error("SocialApi has INTERNAL_ERROR. Please check!");
				
			balanceInfo = Converter.convertBalanceInfo(account);
			LOG.info("balanceInfo of acountId "+ acountId + ": " + balanceInfo);
		} else {
			balanceInfo =  new BalanceInfo();
			balanceInfo.setResult(AccountBalanceResult.INTERNAL_ERROR);
		}
			
		return balanceInfo;
	}
    
    /**
	 * Open social Account　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
    public OpenAccountResult openAccount(Customer customer, List<CustomerAccount> accounts) {
    	CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
    	LOG.info("openAccount, customer: " + customer + ", accounts: " + accounts);
    	OpenAccountResult result = customerAdministratorImpl.openAccount(customer, accounts);
    	LOG.info("openAccount, result: " + result);
    	return result;
    }
    
    public CustomerAccount getAccount(Integer accountId) {
    	CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
    	LOG.info("getAccount, accountId: " + accountId);
    	
    	List<Integer> accountIds = new ArrayList<Integer>();
    	accountIds.add(accountId);
    	List<CustomerAccount> accounts = customerAdministratorImpl.getAccounts(accountIds);
    	
    	LOG.info("getAccount, result: " + accounts);
    	if(accounts != null && accounts.size() > 0)
    		return accounts.get(0);
    	return null;
    }
    
    public List<CustomerAccount> getListAccount(List<Integer> accountIds) {
    	CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
    	LOG.info("getListAccount, accountIds: " + accountIds);
    	List<CustomerAccount> accounts = customerAdministratorImpl.getAccounts(accountIds);
    	LOG.info("getListAccount, result: " + accounts);
    	if(accounts != null && accounts.size() > 0)
    		return accounts;
    	return null;
    }
    
    /**
	 * Modify social Account　
	 * 
	 * @param
	 * @return
	 * @author quyen.le.manh
	 * @CrDate Feb 18, 2016
	 * @MdDate
	 */
    public ModifyAccountResult modifyAccount(CustomerAccountModification modification) {
    	CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
    	LOG.info("modifyAccount, modification: " + modification);
    	ModifyAccountResult result = customerAdministratorImpl.modifyAccount(modification);
    	LOG.info("modifyAccount, result: " + result);
    	return result;
    }

    /**
   	 * Close social Account　
   	 * 
   	 * @param
   	 * @return
   	 * @author quyen.le.manh
   	 * @CrDate Feb 18, 2016
   	 * @MdDate
   	 */
	public CloseAccountResult closeAccount(int customerId) {
		CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl) services.get(CustomerAdministratorImpl.class);
		
		LOG.info("closeAccount, customerId: " + customerId);
		CloseAccountResult result = customerAdministratorImpl.closeAccount(customerId);
		LOG.info("closeAccount, result: " + result);
		return result;
	}

	public Customer getCustomer(Integer customerId) {
    	CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
    	LOG.info("getCustomer, customerId: " + customerId);
    	 
    	List<Integer> customerIds = new ArrayList<Integer>();
    	customerIds.add(customerId);
    	List<Customer> accounts = customerAdministratorImpl.getCustomers(customerIds);
    	
    	LOG.info("getCustomer, result: " + accounts);
    	if(accounts != null && accounts.size() > 0)
    		return accounts.get(0);
    	return null;
    }
	 
	/**
   	 * Transfer social money　
   	 * 
   	 * @param
   	 * @return
   	 * @author quyen.le.manh
   	 * @CrDate Feb 18, 2016
   	 * @MdDate
   	 */
	public TransferResponse transfer(String id, Integer cashflowType, BigDecimal amount, Integer accountId, String customerId, TransferStatus status, Long cashflowId, boolean isEaAccount, Long sourceId) {
		TransferResponse result = null;
		
		if (isEaAccount) {
			LOG.warn("customerServiceId: " + accountId + " is EA, NOT need send transfer to SocialApi");
			result = TransferResponse.newBuilder().setResult(TRANSFER_STATUS.SUCCESS).build();
		} else {
			if (Helper.validateRequestToSC(customerId)) {
				try {
					TradingAdministratorImpl tradingAdministratorImpl = (TradingAdministratorImpl) services.get(TradingAdministratorImpl.class);
					
					LOG.info("transfer, id: " + id + ", accountId: " + accountId + ", customerId: " + customerId + ", cashflowType: " + cashflowType + ", amount: " + amount + ", status: " + status + ", cashflowId: " + cashflowId + ", sourceId: " + sourceId);
					result = tradingAdministratorImpl.transfer(id , cashflowType, amount, accountId, status, cashflowId, sourceId);
					LOG.info("transfer, result: " + result + ", " + SocialTransferFlg.valueOf(result != null ? result.getResult() : SocialTransferFlg.INTERNAL_ERROR.getNumber()));
				} catch (Exception e) {
					Log.error(e.getMessage(), e);
				}
			} else {
				LOG.warn("CustomerId: " + customerId + " not in list test account, response transfer result = SUCCESS");
				result = TransferResponse.newBuilder().setResult(TRANSFER_STATUS.SUCCESS).build();
			}
			
		}
		return result;
	}
	
	public List<CustomerReport> getCustomerReportSc(String fromDate, String toDate, ReportType reportTypeSc, FileType reportFileTypeSc, String wlCode, String customerServiceId, Integer pageNumber, Integer pageSize) {
		LOG.info("[Start] getCustomerReportSc with: customerServiceId=[" + customerServiceId + "]" +  "fromDate=[" + fromDate + "]" + "toDate=[" + toDate + "]");

		CustomerAdministratorImpl customerAdministratorImpl = (CustomerAdministratorImpl)services.get(CustomerAdministratorImpl.class);
		List<CustomerReport> customerReports = customerAdministratorImpl.getCustomerReportSc(fromDate, toDate, reportTypeSc, reportFileTypeSc, wlCode, customerServiceId, pageNumber, pageSize);
		
		LOG.info("[End] getCustomerReportSc with: customerServiceId=[" + customerServiceId + "]" +  "fromDate=[" + fromDate + "]" + "toDate=[" + toDate + "]");
    	return customerReports;
	}
	
	public static void setInstance(SCManager instance) {
		SCManager.instance = instance;
	}
	
	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public long getHeartBeatInteval() {
		return heartBeatInteval;
	}

	public void setHeartBeatInteval(long heartBeatInteval) {
		this.heartBeatInteval = heartBeatInteval;
	}
	
	public long getRequestTimedOut() {
		return requestTimedOut;
	}

	public void setRequestTimedOut(long requestTimedOut) {
		this.requestTimedOut = requestTimedOut;
	}
	
}