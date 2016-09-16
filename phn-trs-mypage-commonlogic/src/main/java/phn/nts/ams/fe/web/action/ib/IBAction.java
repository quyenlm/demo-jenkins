package phn.nts.ams.fe.web.action.ib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.ams.web.condition.AmsFeIBKickBackHistoryCondition;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.dao.IScCustomerDAO;
import phn.com.nts.db.entity.AmsIbKickback;
import phn.com.nts.db.entity.ScCustomer;
import phn.com.nts.util.common.CommonUtil;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.FormatHelper;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.business.ITransferManager;
import phn.nts.ams.fe.business.IWithdrawalManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.DepositInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.IbClientCustomer;
import phn.nts.ams.fe.domain.IbInfo;
import phn.nts.ams.fe.domain.IbTransferMoneyInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.domain.TransferMoneyInfo;
import phn.nts.ams.fe.domain.WhiteLabelConfigInfo;
import phn.nts.ams.fe.domain.WithdrawalInfo;
import phn.nts.ams.fe.model.IBModel;
import phn.nts.ams.fe.mt4.MT4Manager;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.ams.fe.util.CsvWriter;
import phn.nts.social.fe.web.action.BaseSocialAction;

import com.opensymphony.xwork2.ActionContext;

public class IBAction extends BaseSocialAction<IBModel> {

	private static final long serialVersionUID = 1L;
	private static Logit log = Logit.getInstance(IBAction.class);
	private IIBManager ibManager;
	private IDepositManager depositManager;
	private IWithdrawalManager withdrawalManager;
	private IAccountManager accountManager;
//	private IBalanceManager balanceManager;
    private IScCustomerDAO<ScCustomer> scCustomerDAO;
	private String msgCode;
	private String result;
	
	private String filePath;
	private String fileName;
	private static final String CONFIGPATH = "configs.properties";
	private ITransferManager transferManager;
	private IBModel model = new IBModel();	
	/**
	 * index
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String index() {
		try {
//			String wlCode = "";
			String customerId = "";
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
			}
			if(frontUserOnline!=null){
				customerId = frontUserOnline.getUserId();
				currencyCode = frontUserOnline.getCurrencyCode();
				getModel().setCurrencyCode(frontUserOnline.getCurrencyCode());	
//				wlCode = frontUserOnline.getWlCode();
			}			
			IbInfo ibInfo = ibManager.getIbInfo(customerId);
			ibInfo.setCurrencyCode(currencyCode);
			getModel().setIbInfo(ibInfo);
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			return INPUT;
		}
		
		return SUCCESS;
	}

	/**
	 * getCustomerInformation
	 * action get customer information
	 * @param
	 * @return String (SUCCESS, INPUT)
	 * @auth LongND
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String getCustomerInformation() {
		List<IbClientCustomer> listIbClientCustomer = null;		
		String customerId = null;
		String customerName = null;
		String currencyCode = null;
		try{
			PagingInfo pagingInfo = getModel().getPagingInfo();
			if(pagingInfo == null) {
				pagingInfo = new PagingInfo();			
			}	
			getModel().setPagingInfo(pagingInfo);
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					currencyCode = frontUserOnline.getCurrencyCode();
				}
			}
			getModel().setPattern(getText("nts.ams.fe.label.date.full.pattern"));
			CustomerInfo customerInfo = getModel().getCustomerInfo();
			if (customerInfo != null) {
				//validateCustomerInfo(customerInfo);
				String message = getModel().getErrorMessage();
				if (message != null && !isEmptyString(message)) {
					return INPUT;
				}
				
				customerId = customerInfo.getCustomerId();
				customerName = customerInfo.getFullName();
					if(frontUserOnline!=null){
						//model.setCurrencyCode(currencyCode);
						listIbClientCustomer = getIbManager().getListIbCustomer(customerId, frontUserOnline.getUserId(), customerName,pagingInfo);			
						
					} else {
						getModel().setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
						return INPUT;
					}		
			} else {
				if(frontUserOnline!=null){
					getModel().setCurrencyCode(frontUserOnline.getCurrencyCode());
					listIbClientCustomer = getIbManager().getListIbCustomer(customerId,frontUserOnline.getUserId(),customerName,pagingInfo);						
				} else {
					getModel().setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
					return INPUT;
				}	
			}
			if(listIbClientCustomer!=null && listIbClientCustomer.size() >0) {
				for(IbClientCustomer ibClientCustomer:listIbClientCustomer){
					ibClientCustomer.setSuperCurrencyCode(currencyCode);
				}
				getModel().setListIbClientCustomers(listIbClientCustomer);
			}
			else 
				getModel().setInfoMessage(getText("nts.ams.fe.label.ibManagement.ibCustomer.searchResult.MSG_NAB010"));
		}catch(Exception ex) {
			log.error(ex.toString(),ex);
		}	
		return SUCCESS;
	}
	
	/**
	 * exportCsv
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String exportCsv() {
		//getListforExchangerScreen();
		try {
			generateFileName();
//			String userId = "";
			CsvWriter csv = new CsvWriter(getFilePath(), ',',Charset.forName("UTF-8"));
			writeHeaders(csv);

			int pageIndex = PagingInfo.DEFAULT_INDEX;
			
			
			while (true) {
				// Paging
				PagingInfo pagingInfo = getModel().getPagingInfo();
				if (pagingInfo == null) {
					pagingInfo = new PagingInfo();
				}
				pagingInfo.setIndexPage(pageIndex);
				@SuppressWarnings("rawtypes")
				Map session = ActionContext.getContext().getSession();
				AmsFeIBKickBackHistoryCondition condition = (AmsFeIBKickBackHistoryCondition)session.get("ibCondition");
				// Search results
				List<AmsIbKickback> listAmsIbKickbacks = ibManager.searchIbKickBackHistory(condition.getCustomerId(), condition.getOrderCustomerId(), condition.getOrderId(), condition.getOrderSymbolCd(), condition.getFromDate(), condition.getToDate(), pagingInfo);

				String currencyCode = "";
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				FrontUserOnline frontUserOnline = null;
				if(frontUserDetails != null) {
					frontUserOnline = frontUserDetails.getFrontUserOnline();
					if(frontUserOnline!=null){
						currencyCode = frontUserOnline.getCurrencyCode();
					} 
				}
				String pattern = MasterDataManagerImpl.getInstance().getPattern(currencyCode);
				// Data
				if (!CommonUtil.isEmpty(listAmsIbKickbacks)) {
					for (AmsIbKickback kickback : listAmsIbKickbacks) {
						writeRecord(csv, kickback, currencyCode, pattern);
					}
				}

				// Check next page
				if (pagingInfo.getIndexPage() < pagingInfo.getTotalPage()) {
					pageIndex = pagingInfo.getIndexPage() + 1;
				} else {
					break;
				}
			}
			csv.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	/**
	 * getCsvFile
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public InputStream getCsvFile() {
		try {
			InputStream is = new FileInputStream(new File(getFilePath()));
			return is;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * writeRecord
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	private void writeRecord(CsvWriter csv, AmsIbKickback k, String currencyCode, String pattern) throws IOException {
		//[NTS1.0-Quan.Le.Minh]Dec 18, 2012M - Start 
		String dateTimeFormatted = DateUtil.toString(k.getOrderDatetime(), getText("nts.ams.fe.label.date.full.pattern"));
		dateTimeFormatted = CsvWriter.getTextFormat(dateTimeFormatted);
		csv.write(dateTimeFormatted == null ? "" : dateTimeFormatted);
		//[NTS1.0-Quan.Le.Minh]Dec 18, 2012M - End
		csv.write(StringUtil.isEmpty(k.getOrderCustomerId()) ? "" : k.getOrderCustomerId());
		csv.write(StringUtil.isEmpty(k.getOrderId()) ? "" : k.getOrderId());
		csv.write(StringUtil.isEmpty(k.getOrderSymbolCd()) ? "" : k.getOrderSymbolCd());
		csv.write(k.getOrderVolume() == null ? "" : String.valueOf(k.getOrderVolume()));
		//[NTS1.0-Quan.Le.Minh]Dec 17, 2012D - Start
		/*csv.write(StringUtil.isEmpty(k.getOrderSymbolCd()) ? "" : k.getOrderSymbolCd());*/
		//[NTS1.0-Quan.Le.Minh]Dec 17, 2012D - End
		String amount = FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(currencyCode , k.getKickbackAmount()), pattern);
		csv.write(amount + "" +  currencyCode);
		// End record
		csv.endRecord();
	}

	/**
	 * generateFileName
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	private void generateFileName() throws FileNotFoundException {
		Calendar c = Calendar.getInstance();
		String timetamp = DateUtil.toString(c.getTime(), IConstants.DATE_TIME_FORMAT.DATE_TIME_EVENT);
		String timetampSecond = String.format("%02d%02d%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		setFileName("IBKickBackHistory_" + timetamp + "_" + timetampSecond + ".csv");
		Properties properties = Helpers.getProperties(CONFIGPATH);
		setFilePath(properties.getProperty("pathFileCsvOutput") + "/" + getFileName());
	}

	/**
	 * writeHeaders
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 29, 2012
	 */
	private void writeHeaders(CsvWriter csv) throws IOException {
		// ID
		csv.write("Date time");		
		csv.write("Order CustomerID");
		csv.write("OrderID");
		csv.write("Symbol");
		csv.write("Size(lot)");
		csv.write("Kickback Amount");
		csv.write("Remark");
		// End record
		csv.endRecord();
	}

	/**
	 * getDepositWithdrawal
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String getDepositWithdrawal() {
		try {
			initComboBox();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if(result != null) {
			getMsgCode(result);
		}
		return SUCCESS;
	}

	/**
	 * readMapServiceType
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	private void initComboBox() {
		//[NTS1.0-Quan.Le.Minh]Dec 19, 2012A - Start 
		Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		//[NTS1.0-Quan.Le.Minh]Dec 19, 2012A - End
		Map<Integer, String> mapAllServiceType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		if (mapAllServiceType == null || mapAllServiceType.size() == 0) {
			log.error("[mapAllServiceType] is null");
		}
		model.setMapAllServiceType(mapAllServiceType);
		
		Map<Integer, String> mapServiceType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.IB_SERVICE_TYPE);
		if (mapServiceType == null || mapServiceType.size() == 0) {
			log.error("[mapServiceType] is null");
		}
		Map<Integer, String> mapTemp = new TreeMap<Integer, String>();
		if (getModel().getType() == null) {
			getModel().setType(IConstants.TRANSACTION_TYPE.DEPOSIT);
		}

		// init map service type deposit
		Map<String, String> mapServiceDeposit = new TreeMap<String, String>();
		mapServiceDeposit.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
		mapServiceDeposit.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
        mapServiceDeposit.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		getModel().setMapServiceDeposit(mapServiceDeposit);

		// init map service type withdrawal
		Map<String, String> mapServiceWithdrawal = new TreeMap<String, String>();
		mapServiceWithdrawal.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
		getModel().setMapServiceWithdrawal(mapServiceWithdrawal);
		
		if (getModel().getType().intValue() == IConstants.TRANSACTION_TYPE.DEPOSIT.intValue()) {
			getModel().setMapServiceType(mapServiceType);
		} else { //2 withdrawal
			mapTemp.put(IConstants.SERVICES_TYPE.AMS, getText("service_type.asm"));
			getModel().setMapServiceType(mapTemp);
		}

		// map transaction type
		Map<Integer, String> mapType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.TRANSACTION_TYPE);
		getModel().setMapMethod(mapType);

		// map transfer from
		Map<String, String> mapTransferFrom = new TreeMap<String, String>();
		mapTransferFrom.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
		mapTransferFrom.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
		mapTransferFrom.put(IConstants.SERVICES_TYPE.BO.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
        mapTransferFrom.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		getModel().setMapTransferFrom(mapTransferFrom);

		// map transfer to
		Integer serviceFrom = IConstants.SERVICES_TYPE.AMS;
		IbTransferMoneyInfo info = model.getTransferInfo();
		if (info != null && info.getServiceTypeFrom() != null) {
			serviceFrom = info.getServiceTypeFrom();
		}
		Map<String, String> mapTransferTo = getMapTransferTo(serviceFrom.intValue());
		getModel().setMapTransferTo(mapTransferTo);
	}

	/**
	 * getMapTransferTo 
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Nov 29, 2012
	 */
	public Map<String, String> getMapTransferTo(int serviceType) {
		//[NTS1.0-Quan.Le.Minh]Dec 19, 2012M - Start
		Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		Map<String, String> mapTransferTo = new TreeMap<String, String>();
		if (serviceType == IConstants.SERVICES_TYPE.AMS.intValue()) {
			mapTransferTo.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
			mapTransferTo.put(IConstants.SERVICES_TYPE.BO.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		} else if (serviceType == IConstants.SERVICES_TYPE.FX.intValue()) {
			mapTransferTo.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
			mapTransferTo.put(IConstants.SERVICES_TYPE.BO.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		} else if (serviceType == IConstants.SERVICES_TYPE.BO.intValue()) {
            mapTransferTo.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
        } else if (serviceType == IConstants.SERVICES_TYPE.COPY_TRADE.intValue()) {
            mapTransferTo.put(IConstants.SERVICES_TYPE.AMS.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.BO.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
        } else {
			mapTransferTo.put(IConstants.SERVICES_TYPE.FX.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
			mapTransferTo.put(IConstants.SERVICES_TYPE.BO.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
            mapTransferTo.put(IConstants.SERVICES_TYPE.COPY_TRADE.toString(), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		}
		//[NTS1.0-Quan.Le.Minh]Dec 19, 2012M - End

		return mapTransferTo;
	}
	
	/**
	 * reloadMapServiceType
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String reloadMapServiceType() {
		Map<Integer, String> mapServiceType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.IB_SERVICE_TYPE);
		Map<Integer, String> mapTemp = new TreeMap<Integer, String>();
		String method = httpRequest.getParameter("method");
		Integer medthodSelected = Integer.parseInt(method);
		if (medthodSelected != null) {
			//1 deposit
			if (medthodSelected == 1) {
				getModel().setMapServiceType(mapServiceType);
			} else { //2 withdrawal
				mapTemp.put(IConstants.SERVICES_TYPE.AMS, getText("service_type.asm"));
				getModel().setMapServiceType(mapTemp);
			}
		}
		return SUCCESS;
	}

	/**
	 * getKickbackHistory
	 * get kickback history from DB (Config struts action)
	 * @param
	 * @return
	 * @auth HuyenMTT
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String getKickbackHistory() {
		try {
			getModel().setListSymbol(getListSymbol());
			AmsFeIBKickBackHistoryCondition amsFeIBKickBackHistoryCondition = getModel().getAmsFeIBKickBackHistoryCondition();
			if(amsFeIBKickBackHistoryCondition == null) {
				amsFeIBKickBackHistoryCondition = new AmsFeIBKickBackHistoryCondition();
				amsFeIBKickBackHistoryCondition.setToDate(DateUtil.toString(new Date(), IConstants.DATE_TIME_FORMAT.DATE_TIME_PICKER));
				getModel().setAmsFeIBKickBackHistoryCondition(amsFeIBKickBackHistoryCondition);
			}			
			int result = searchKickbackHistory(); 
			if(result == 0){
				getModel().setInfoMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
				return INPUT;
			}							
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}

	/**
	 * validateIBHistoryForm
	 * validate IB History form (Item must be inputted valid)
	 * @param
	 * @return true if valid, false if invalid
	 * @auth HuyenMTT
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public boolean validateIBHistoryForm(AmsFeIBKickBackHistoryCondition amsFeIBKickBackHistoryCondition){
		if (amsFeIBKickBackHistoryCondition == null) {
			return false;
		}
		
		String message = getModel().getErrorMessage();
		if (message == null) {
			message = new String("");
		}
		
		String fromDate =null;
		String toDate = null;
		String customerId = null;
		String orderId = null;
		try{
			log.info("Display date get from transaction history search");
			log.info("CustomerID = " + amsFeIBKickBackHistoryCondition.getCustomerId());
			customerId = amsFeIBKickBackHistoryCondition.getCustomerId();
			orderId = amsFeIBKickBackHistoryCondition.getOrderId();
			fromDate = amsFeIBKickBackHistoryCondition.getFromDate();
			toDate = amsFeIBKickBackHistoryCondition.getToDate();
			log.info("FromDate is " + fromDate);
			log.info("ToDate is " + toDate);
			log.info("End display date");
		}catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		
		if(fromDate == null || fromDate.equals("")) {
			message = getText("nts.ams.fe.message.history.require_fromtDate");
		} else if (toDate == null || toDate.equals("")) {
			message = getText("nts.ams.fe.message.history.require_toDate");
		} else {
				Date fromDate_date = DateUtil.toDate(fromDate,IConstants.DATE_TIME_FORMAT.DATE_TIME_PICKER);
				Date toDate_date = DateUtil.toDate(toDate,IConstants.DATE_TIME_FORMAT.DATE_TIME_PICKER);
				if(fromDate_date !=null && toDate_date !=null) {
					if(fromDate_date.after(toDate_date)) {
						message =getText("nts.ams.fe.message.history.invalidDate");
					}
				}
		}
		if (customerId == null || customerId.equals("")) {
			message = getText("required");
		}
		if(orderId == null || orderId.equals("")){
			message = getText("required");
		}
		getModel().setErrorMessage(message);
		if (message != ""){
			return false;
		}
		return true;
	}

	/**
	 * search kick back history
	 * 
	 * @param
	 * @return
	 * @auth HuyenMT
	 * @CrDate Aug 13, 2012
	 * @MdDate
	 */
	@SuppressWarnings("unchecked")
	private int searchKickbackHistory(){
		int totalRecord = 0;
		log.info("[Start] search kickback history");
		try {
			PagingInfo pagingInfo = getModel().getPagingInfo();
			if(pagingInfo == null) {
				pagingInfo = new PagingInfo();			
			}	
			getModel().setPagingInfo(pagingInfo);
			AmsFeIBKickBackHistoryCondition condition = getModel().getAmsFeIBKickBackHistoryCondition();
			if (condition == null) {
				condition = new AmsFeIBKickBackHistoryCondition();
			}
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline!=null){
					condition.setCustomerId(frontUserOnline.getUserId());	
					getModel().setCurrencyCode(frontUserOnline.getCurrencyCode());
					
				} 
			}
			getModel().setListSymbol(getListSymbol());
			
			String dSymbolName = condition.getOrderSymbolCd();
			if(dSymbolName != null){
				if(!IConstants.FRONT_OTHER.COMBO_INDEX.equals(dSymbolName)){
					condition.setOrderSymbolCd(dSymbolName);
				}else{
					dSymbolName = "";
					condition.setOrderSymbolCd(dSymbolName);
				}
				
			}
			log.info("customerId"  + condition.getCustomerId());
			log.info("order customer id" + condition.getOrderCustomerId());
			log.info("order id" + condition.getOrderId());
			log.info("from date"  + condition.getFromDate());
			log.info("to date" + condition.getToDate());
			@SuppressWarnings("rawtypes")
			Map session = ActionContext.getContext().getSession();
			session.put("ibCondition", condition);
			List<AmsIbKickback> listAmsIbKickbacks = (List<AmsIbKickback> ) ibManager.searchIbKickBackHistory(condition.getCustomerId(), condition.getOrderCustomerId(), condition.getOrderId(), condition.getOrderSymbolCd(), condition.getFromDate(), condition.getToDate(), pagingInfo);
			if(listAmsIbKickbacks != null && listAmsIbKickbacks.size() > 0){
				BigDecimal totalKickback = MathUtil.parseBigDecimal(0);	
				BigDecimal totalVolumn = MathUtil.parseBigDecimal(0);
				for(AmsIbKickback amsIbKickback : listAmsIbKickbacks) {
					if(amsIbKickback != null) {
						totalKickback = totalKickback.add(MathUtil.parseBigDecimal(amsIbKickback.getKickbackAmount().toString()));					
						totalVolumn   = totalVolumn.add(MathUtil.parseBigDecimal(amsIbKickback.getOrderVolume().toString()));						
					}
				}
				getModel().setTotalKickBack(totalKickback);
				getModel().setTotalVolumn(totalVolumn);
				getModel().setKickbackList(listAmsIbKickbacks);
				totalRecord = listAmsIbKickbacks.size();
			}
			
			getModel().setPattern(getText("nts.ams.fe.label.date.full.pattern"));
		} catch (Exception e) {
			getModel().setListSymbol(getListSymbol());
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
		log.info("[End] search kickback history");
		return totalRecord;
	}

	/**
	 * @param ibManager the ibManager to set
	 */
	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}

	/**
	 * validateDepositWithdrawal
	 * validate deposit history of IB
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	private boolean validateDepositWithdrawal() {
		
		BigDecimal amount = MathUtil.parseBigDecimal(0);
		if(getModel().getClientCustomerId() == null && StringUtils.isBlank(getModel().getClientCustomerId())) {
			String message = getText("nts.ams.fe.message.bank_information.neteller.accountId.require");
			addFieldError("errorMessage", message);
			getModel().setErrorMessage(message);
			return false;
		}		
		
		if(getModel().getAmount() == null || StringUtils.isBlank(getModel().getAmount())) {
			addFieldError("errorMessage", getText("nts.ams.fe.label.ibmanager.deposit.withdrawal.amount") +" "+ getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.is.require"));
			getModel().setErrorMessage(getText("nts.ams.fe.label.ibmanager.deposit.withdrawal.amount") +" "+ getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.is.require"));
			return false;
		}
		amount = MathUtil.parseBigDecimal(getModel().getAmount());
		if(amount.compareTo(MathUtil.parseBigDecimal(0)) == 0) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.ibmanager.deposit.withdrawal.amount"));
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContent));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContent));
			return false;
		}
		
		// check exist client customer id
		String customerId = null;
		String wlCode = "";
		String clientCustomerId = getModel().getClientCustomerId();
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = null;
		if(frontUserDetails != null) {
			frontUserOnline = frontUserDetails.getFrontUserOnline();	
			if (frontUserOnline!= null) {
				customerId = frontUserOnline.getUserId();
				wlCode = frontUserOnline.getWlCode();
			}
		}
		Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
		CustomerInfo customerInfo = accountManager.getCustomerInfo(customerId, clientCustomerId);
		if(customerInfo == null) {		
			// if client customer id not found
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
			return false;
		}
		
		boolean isValid = true;
		if(IConstants.TRANSACTION_TYPE.DEPOSIT.equals(getModel().getType())) {			
			isValid = validateDeposit(amount, mapConfiguration, customerInfo);
		} else if(IConstants.TRANSACTION_TYPE.WITHDRAWAL.equals(getModel().getType())) {
			isValid = validateWithdrawal(amount, wlCode, clientCustomerId, mapConfiguration, customerInfo);
		}
		
		if (!isValid) {
			return false;
		}

		String currencyCode = getModel().getCurrencyCode();
		if(currencyCode == null || StringUtils.isBlank(currencyCode)) {
			customerInfo = accountManager.getCustomerInfo(clientCustomerId);
			if(customerInfo == null) {
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
				return false;
			} else {
				getModel().setCurrencyCode(customerInfo.getCurrencyCode());
				getModel().setCustomerName(customerInfo.getFullName());
				getModel().setEmailAddress(customerInfo.getMailMain());
			}
		}

		Integer serviceType = IConstants.SERVICES_TYPE.AMS;
		if (IConstants.TRANSACTION_TYPE.WITHDRAWAL.equals(getModel().getType())) {
			serviceType = model.getServiceTypeWithdrawal();
		} else if (IConstants.TRANSACTION_TYPE.DEPOSIT.equals(getModel().getType())) {
			serviceType = model.getServiceTypeDeposit();
		}
		model.setServiceType(serviceType);

		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + getModel().getCurrencyCode());
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		getModel().setAmount(StringUtil.toString(amount));
		
		Map<Integer, String> mapServiceType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.IB_SERVICE_TYPE);

		if (!IConstants.SERVICES_TYPE.AMS.equals(serviceType)) {
			CustomerServicesInfo customerServicesInfo = accountManager.getCustomerServiceInfo(getModel().getClientCustomerId(), serviceType);
			if (customerServicesInfo == null ) {
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(mapServiceType.get(serviceType));
				getModel().setErrorMessage(getText("nts.ams.fe.error.message.NAB091", listContent));
				addFieldError("errorMessage", getText("nts.ams.fe.error.message.NAB091", listContent));
				return false;
			}
			
			if (customerServicesInfo != null && customerServicesInfo.getCustomerServiceStatus().intValue() == IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER && customerServicesInfo.getCustomerServiceStatus().intValue() == IConstants.CUSTOMER_SERVIVES_STATUS.CANCEL) {
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(mapServiceType.get(serviceType));
				getModel().setErrorMessage(getText("nts.ams.fe.error.message.NAB091", listContent));
				addFieldError("errorMessage", getText("nts.ams.fe.error.message.NAB091", listContent));
				return false;
			}
		}
		
		if(IConstants.TRANSACTION_TYPE.TRANSFER_MONEY.equals(getModel().getType())) {
			return validateTransfer(customerInfo);
		}
		return true;
	}

	/**
	 * reloadMapToServiceType
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 10, 2012
	 */
	public String reloadMapTransferTo() {
		String method = httpRequest.getParameter("from");
		Integer medthodSelected = MathUtil.parseInteger(method);
		if (medthodSelected == null) {
			medthodSelected = 1;
		}
		model.setMapTransferTo(getMapToServiceType(medthodSelected));

		return SUCCESS;
	}

	/**
	 * getMapToServiceType
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 10, 2012
	 */
	private Map<String, String> getMapToServiceType(Integer fromServiceType){		
		Map<String, String> mapServiceTypeName = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
		Map<String, String> mapServiceType = new TreeMap<String, String>();	
	
		if(IConstants.SERVICES_TYPE.AMS.equals(fromServiceType)){		
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));				
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		}else if(IConstants.SERVICES_TYPE.FX.equals(fromServiceType)) {
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		}else if(IConstants.SERVICES_TYPE.BO.equals(fromServiceType)){
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
			mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.COPY_TRADE)));
		}else if(IConstants.SERVICES_TYPE.COPY_TRADE.equals(fromServiceType)){
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.AMS)));
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.FX), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.FX)));
            mapServiceType.put(StringUtil.toString(IConstants.SERVICES_TYPE.BO), mapServiceTypeName.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
        }
		return mapServiceType;
	}

	private boolean validateTransfer(CustomerInfo customerInfo) {
		IbTransferMoneyInfo info = model.getTransferInfo();
		
		if (info == null) {
			String message = getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		String currencyFrom = info.getCurrencyFrom();
		if (StringUtil.isEmpty(currencyFrom)) {
			String message = getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.currencyFrom");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		String currencyTo = info.getCurrencyTo();
		if (StringUtil.isEmpty(currencyTo)) {
			String message = getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.currencyTo");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}

		String amount = model.getAmount();
		Double transferAmount = MathUtil.parseDouble(amount);
		if (transferAmount == null) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.transfer.label.amount"));
			model.setErrorMessage(getText("MSG_NAF001", listContent));
			addFieldError("errorMessage", getText("MSG_NAF001", listContent));
			
			return false;
		}

		if (transferAmount.doubleValue() <= 0) {
			List<String> listContent = new ArrayList<String>();
			listContent.add("0");
			model.setErrorMessage(getText("MSG_NAB095", listContent));
			addFieldError("errorMessage", getText("MSG_NAB095", listContent));
			return false;
		}
		String wlCode = customerInfo.getWlCode();
		Map<String, String> mapWlConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
		String minValue = mapWlConfig.get(currencyFrom + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_TRANSFER_AMOUNT);
		Double minTransfer = MathUtil.parseDouble(minValue);

		if (minTransfer == null || minTransfer < 0) {
			log.error("Config error minValue = " + minValue);
			String message = getText("MSG_NAB073");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}

//		Double convertedAmount = info.getConvertedAmount();
		Double convertedAmount = null;
		if (currencyFrom.equalsIgnoreCase(currencyTo)) {
			convertedAmount = transferAmount;
		} else {
			convertedAmount = MathUtil.parseDouble(info.getConvertedAmount());
		}

		if (convertedAmount == null || convertedAmount <= 0) {
			String message = getText("nts.ams.fe.label.deposit.converted.amount.invalid");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;	
		}

		if (convertedAmount.doubleValue() < minTransfer.doubleValue()) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(StringUtil.toString(minTransfer));
			model.setErrorMessage(getText("MSG_NAB095", listContent));
			addFieldError("errorMessage", getText("MSG_NAB095", listContent));
			log.info("amount = " + convertedAmount.doubleValue()+" is smaller than min transfer amount=" + minTransfer.doubleValue());
			return false;
		}
		info.setAmountTransfer(transferAmount.toString());

		String customerId = customerInfo.getCustomerId();
		if (StringUtil.isEmpty(customerId)) {
			String message = getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		String rate = info.getConvertedRate();
		if (StringUtil.isEmpty(rate)) {
			String message = getText("info.getConvertedRate()");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		if (StringUtil.isEmpty(info.getAmountTransfer())) {
			String message = getText("nts.ams.fe.error.message.amount.required.MSG_NAB057");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}

		Integer serviceTypeFrom = info.getServiceTypeFrom();
		Integer serviceTypeTo = info.getServiceTypeTo();
		if (serviceTypeFrom == null) {
			String message = getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.servicetype.from.invalid");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		if (serviceTypeTo == null) {
			String message = getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.servicetype.to.invalid");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		if (serviceTypeFrom.intValue() == serviceTypeTo.intValue()) {
			String message = getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.servicetype.invalid");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;
		}
		
		BalanceInfo balanceInfo = balanceManager.getBalanceInfo(customerId, serviceTypeFrom, currencyFrom);
		if (balanceInfo == null) {
			String message = getText("nts.ams.fe.transfer.message.cannot.get.balance");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;	
		}
		
		Double amountAvailable = balanceInfo.getAmountAvailable();
		if (amountAvailable == null || amountAvailable.doubleValue() < convertedAmount.doubleValue()) {
			String message = getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029");
			addFieldError("errorMessage", message);
			model.setErrorMessage(message);
			return false;	
		}
		
		//[NTS1.0-Quan.Le.Minh]Dec 22, 2012A - Start
		Map<Integer, String> mapServiceType = SystemPropertyConfig.getInstance().getMapContent(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.IB_SERVICE_TYPE);
		if (!IConstants.SERVICES_TYPE.AMS.equals(serviceTypeFrom)) {
			CustomerServicesInfo customerServicesInfo = accountManager.getCustomerServiceInfo(getModel().getClientCustomerId(), serviceTypeFrom);
			if (customerServicesInfo != null && (customerServicesInfo.getCustomerServiceStatus().intValue() == IConstants.CUSTOMER_SERVIVES_STATUS.BEFORE_REGISTER || customerServicesInfo.getCustomerServiceStatus().intValue() == IConstants.CUSTOMER_SERVIVES_STATUS.CANCEL)) {
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(mapServiceType.get(serviceTypeTo));
				getModel().setErrorMessage(getText("nts.ams.fe.error.message.NAB091", listContent));
				addFieldError("errorMessage", getText("nts.ams.fe.error.message.NAB091", listContent));
				return false;
			}
			
			if (customerServicesInfo != null && customerServicesInfo.getAllowSendmoneyFlg().intValue() == IConstants.ALLOW_FLG.INALLOW) {
				List<Object> listContent = new ArrayList<Object>();
				listContent.add(mapServiceType.get(serviceTypeTo));
				getModel().setErrorMessage(getText("MSG_NAB020"));
				addFieldError("errorMessage", getText("MSG_NAB020"));
				return false;
			}
		}
		FrontUserDetails frontUserDetail = FrontUserOnlineContext.getFrontUserOnline();
		FrontUserOnline frontUserOnline = frontUserDetail.getFrontUserOnline();
		if(frontUserOnline != null) {
			String userId = frontUserOnline.getUserId();
			boolean isIbClient = ibManager.isIbClient(userId, model.getClientCustomerId());
			if(!isIbClient) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
				return false;
			}
		}
		
		//[NTS1.0-Quan.Le.Minh]Dec 22, 2012A - End
		return true;
	}
	
	/**
	 * validateWithdrawal
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 4, 2012
	 */
	private boolean validateWithdrawal(BigDecimal amount, String wlCode, String clientCustomerId, Map<String, String> mapConfiguration, CustomerInfo customerInfo) {
		BigDecimal withdrawalAmountMax = MathUtil.parseBigDecimal(0);
		BigDecimal withdrawalAmountMin = MathUtil.parseBigDecimal(0); 
		BigDecimal withdrawalAmountPerday = MathUtil.parseBigDecimal(0); 
		Integer withdrawalAmountNumber = new Integer(0); 
		try {
			String key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_WITHDRAWAL_AMOUNT;		
			
			WhiteLabelConfigInfo whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, wlCode);
			if(whiteLabelConfigInfo != null) {
				withdrawalAmountMax = MathUtil.parseBigDecimal(whiteLabelConfigInfo.getConfigValue());
			}
			key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_WITHDRAWAL_AMOUNT;
//				withdrawalAmountMin = MathUtil.parseBigDecimal(mapConfiguration.get(key));
			whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key, wlCode);
			if(whiteLabelConfigInfo != null) {
				withdrawalAmountMin = MathUtil.parseBigDecimal(whiteLabelConfigInfo.getConfigValue());
			}

			key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.WITHDRAWAL_AMOUNT_PERDAY;
			withdrawalAmountPerday = MathUtil.parseBigDecimal(mapConfiguration.get(key));
			key = IConstants.WHITE_LABEL_CONFIG.WITHDRAWAL_NUMBER_PERDAY;
			withdrawalAmountNumber = MathUtil.parseInteger(mapConfiguration.get(key));
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			return false;
		}

		if(amount.compareTo(withdrawalAmountMax) > 0) {
			// display MSG_NAB026				
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));
			return false;
		}

		if(amount.compareTo(withdrawalAmountMin) < 0) {
			// display MSG_NAB027				
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));
			return false;
		}

		// check withdrawal amount perday
		BigDecimal totalAmountWithdrawal = withdrawalManager.getTotalWithdrawalAmount(clientCustomerId,  IConstants.APP_DATE.FRONT_DATE);
		if(withdrawalAmountPerday.compareTo(totalAmountWithdrawal) <= 0) {
			//display MSG_NAB028				
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB028"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB028"));
			return false;
		}

		// check withdrawal number perday
		Integer countWithdrawal = withdrawalManager.summaryOfWithDrawalPerday(clientCustomerId, IConstants.APP_DATE.FRONT_DATE);
		if(withdrawalAmountNumber.compareTo(countWithdrawal) <= 0) {
			//display MSG_NAB029				
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));
			return false;
		}

		BalanceInfo balanceInfo = balanceManager.getBalanceInfo(getModel().getClientCustomerId(), getModel().getServiceTypeWithdrawal(), customerInfo.getCurrencyCode());
		if(balanceInfo != null) {
			if(amount.compareTo(MathUtil.parseBigDecimal(balanceInfo.getAmountAvailable())) > 0) {
				// withdrawal Amount > free margin
				// display MSG_NAB022
				addFieldError("errorMessage", getText("MSG_NAB022"));
				getModel().setErrorMessage(getText("MSG_NAB022"));
				return false;						
			}	
		} else {
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB041"));
			return false;
		}
		
		return true;
	}

	private boolean validateDeposit(BigDecimal amount,
			Map<String, String> mapConfiguration, CustomerInfo customerInfo) {
		BigDecimal depositAmountMax = MathUtil.parseBigDecimal(0);
		BigDecimal depositAmountMin = MathUtil.parseBigDecimal(0);			
		String key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT;
		
		depositAmountMax = MathUtil.parseBigDecimal(mapConfiguration.get(key));
		key = customerInfo.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT;
		depositAmountMin = MathUtil.parseBigDecimal(mapConfiguration.get(key));

		if(amount.compareTo(depositAmountMax) > 0) {
			// show message NAB024
			List<String> listContents = new ArrayList<String>();
			listContents.add(StringUtil.toString(depositAmountMax));
			listContents.add(customerInfo.getCurrencyCode());
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB024", listContents));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB024", listContents));
			return false;
		}

		if(amount.compareTo(depositAmountMin) < 0) {
			// show message NAB024
			List<String> listContents = new ArrayList<String>();
			listContents.add(StringUtil.toString(depositAmountMin));
			listContents.add(customerInfo.getCurrencyCode());
			addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB023", listContents));
			getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB023", listContents));
			return false;
		}

		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + getModel().getCurrencyCode());
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		getModel().setAmount(StringUtil.toString(amount));
		return true;
	}

	/**
	 * getDepositWithdrawalConfirm
	 * Struts action confirmation for deposit and withdrawal IB
	 * @param
	 * @return
	 * @auth Administrator
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String getDepositWithdrawalConfirm() {
		try {
			initComboBox();
			validateDepositWithdrawal();		
			if(hasErrors()) {
				return INPUT;
			}		
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	/**
	 * transactionSubmit
	 * execute deposit and withdrawal for IB
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String transactionSubmit() {
		try {
			// if not validate
			String customerId = "";
			Integer deviceType = IConstants.DEVICE_TYPE.PC;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {
					customerId = frontUserOnline.getUserId();
					deviceType = frontUserOnline.getDeviceType();
				}
			}
			if (getModel().getType().equals(IConstants.TRANSACTION_TYPE.DEPOSIT)) {
				return ibDeposit(customerId, deviceType);
			} else if(getModel().getType().equals(IConstants.TRANSACTION_TYPE.WITHDRAWAL)) {
				return ibWithdrawal(customerId);
			} else if(getModel().getType().equals(IConstants.TRANSACTION_TYPE.TRANSFER_MONEY)) {
				return ibTransfer();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	/**
	 * get RateInfo
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 5, 2012
	 */
	public String getRate() {
		try{
			double convertAmount = 0;
			String amountStr = httpRequest.getParameter("amount");
			double amount = 0;
			String rate = "";
			String fromCurrency = httpRequest.getParameter("fromCurrency");
			String toCurrency =  httpRequest.getParameter("toCurrency");
			
			//Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
			//RateInfo raInfo = MT4Manager.getInstance().getRate(fromCurrency, toCurrency, mapConfig);
            BigDecimal convertRate = transferManager.getConvertRateOnFrontRate(fromCurrency, toCurrency);
			try {
				amount = Double.parseDouble(amountStr);
			} catch (Exception e) {
				log.error("Can not get rate" + e);
			}

			//String pattern = MasterDataManagerImpl.getInstance().getPatternByCurrencyPair(fromCurrency, toCurrency);
			//if (raInfo.getSymbolName().equals(fromCurrency + toCurrency)) {
            if (convertRate != null) {
				convertAmount = amount / convertRate.doubleValue();
				//rate = FormatHelper.formatString(convertRate, pattern);
			}
//            else {
//				convertAmount = amount * raInfo.getRate().doubleValue();
//				rate = FormatHelper.formatString(raInfo.getRate(), pattern);
//			}
			String patternConvert = MasterDataManagerImpl.getInstance().getPattern(fromCurrency);
			String convertedAmount = FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(fromCurrency,convertAmount), patternConvert);
			//String rateStr = "|" + rate + "|" + convertedAmount + "|" + raInfo.getSymbolName();
            //String rateStr = "|" + rate + "|" + convertedAmount + "|" + fromCurrency + toCurrency;
            String rateStr = "|" + String.valueOf(convertRate) + "|" + convertedAmount + "|" + fromCurrency + toCurrency;
			return rateStr;
		} catch (Exception e){
			String msg = getText("MSG_NAB066");
			model.setErrorMessage(msg);
			return "";
		}
	}

	/**
	 * ibTransfer
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 4, 2012
	 */
	private String ibTransfer() {
		IbTransferMoneyInfo info = model.getTransferInfo();
		String customerId = model.getClientCustomerId();
		String fromCurrencyCode = info.getCurrencyFrom();
		String toCurrencyCode = info.getCurrencyTo();
		//Map<String, String> mapConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
		//RateInfo rate = MT4Manager.getInstance().getRate(fromCurrencyCode, toCurrencyCode, mapConfig);
        BigDecimal convertRate = transferManager.getConvertRateOnFrontRate(fromCurrencyCode, toCurrencyCode, IConstants.FRONT_OTHER.SCALE_ALL);
		CustomerInfo customerInfo = accountManager.getCustomerInfo(customerId);

		TransferMoneyInfo transferMoneyInfo = new TransferMoneyInfo();
		String currencyCodeAms = customerInfo.getCurrencyCode();
		transferMoneyInfo.setConvertedAmount(MathUtil.parseDouble(info.getConvertedAmount()));
		transferMoneyInfo.setCurrencyCode(currencyCodeAms);
		transferMoneyInfo.setCustomerId(customerId);
		transferMoneyInfo.setDestinationAmount(MathUtil.parseDouble(info.getAmountTransfer()));
		transferMoneyInfo.setDestinationCurrencyCode(toCurrencyCode);
		transferMoneyInfo.setToCurrencyCode(toCurrencyCode);
		transferMoneyInfo.setFromCurrencyCode(fromCurrencyCode);
		//transferMoneyInfo.setRate(rate.getRate());
        transferMoneyInfo.setRate(convertRate);
		transferMoneyInfo.setTransferMoney(MathUtil.parseDouble(info.getAmountTransfer()));
		transferMoneyInfo.setTransferFrom(info.getServiceTypeFrom());
		transferMoneyInfo.setTransferTo(info.getServiceTypeTo());
		boolean isValid = validateTransfer(customerInfo);
		model.setType(IConstants.TRANSACTION_TYPE.DEPOSIT);
		
		if (isValid) {
			List<CustomerServicesInfo> listCustomerServiceInfo = accountManager.getListCustomerServiceInfo(customerId);
			for(CustomerServicesInfo customerServicesInfo : listCustomerServiceInfo) {
				if(customerServicesInfo.getServiceType() == transferMoneyInfo.getTransferFrom())
					transferMoneyInfo.setFromServicesInfo(customerServicesInfo);
				else if(customerServicesInfo.getServiceType() == transferMoneyInfo.getTransferTo())
					transferMoneyInfo.setToServicesInfo(customerServicesInfo);
			}
			
			Integer status  = getTransferManager().transferMoney(transferMoneyInfo, currencyCodeAms);
			if (status.intValue() == IConstants.TRANSFER_STATUS.SUCCESS.intValue()) {
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_TRANSFER_SUCCESS);
				return SUCCESS;
			} else {
				setMsgCode("nts.ams.fe.error.message.failure");
				return ERROR;
			}
		} else {
			setMsgCode("nts.ams.fe.error.message.failure");
			return ERROR;
		}
	}

	/**
	 * ibWithdrawal
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 4, 2012
	 */
	private String ibWithdrawal(String customerId) {
		WithdrawalInfo withdrawalInfo = getModel().getWithdrawalInfo();
		if(withdrawalInfo == null) {
			withdrawalInfo = new WithdrawalInfo();
		}
		if(!IConstants.SERVICES_TYPE.AMS.equals(getModel().getServiceType())) {
			CustomerServicesInfo customerServiceInfo = accountManager.getCustomerServiceInfo(getModel().getClientCustomerId(), getModel().getServiceTypeWithdrawal());
			if(customerServiceInfo == null) {
				getModel().setErrorMessage("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg");
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;			
			} 	
		}
		
		withdrawalInfo.setCustomerId(getModel().getClientCustomerId());
		CustomerInfo customerInfo = accountManager.getCustomerInfo(getModel().getClientCustomerId());
		if(customerInfo != null) {
			withdrawalInfo.setPassword(customerInfo.getLoginPass());
		}	
		
		BigDecimal amount =  MathUtil.parseBigDecimal(getModel().getAmount());
		Integer scale = new Integer(0);
		Integer rounding = new Integer(0);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + getModel().getCurrencyCode());
		if(currencyInfo != null) {
			scale = currencyInfo.getCurrencyDecimal();
			rounding = currencyInfo.getCurrencyRound();
		}
		amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		
		withdrawalInfo.setWithdrawalMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
		withdrawalInfo.setActiveFlag(IConstants.ACTIVE_FLG.ACTIVE); 
		withdrawalInfo.setStatus(IConstants.STATUS_WITHDRAW.REQUESTING);
		withdrawalInfo.setCurrencyCode(getModel().getCurrencyCode());
		withdrawalInfo.setServiceType(getModel().getServiceType());
		withdrawalInfo.setWithdrawalAmount(amount.doubleValue());
		withdrawalInfo.setRemark(getModel().getRemark());
		//[NTS1.0-Administrator]Aug 14, 2012A - Start - add RegCustomerID follow BA 
		withdrawalInfo.setRegCustomerId(customerId);
		//[NTS1.0-Administrator]Aug 14, 2012A - End
		getModel().setWithdrawalInfo(withdrawalInfo);
		if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == withdrawalInfo.getWithdrawalMethod()) {		
			log.info("[start] process withdrawal ib via bank transfer");
			Integer result = withdrawalManager.withdrawalIBBankTransfer(withdrawalInfo, getModel().getClientCustomerId());
			if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_SUCCESS.equals(result)) {
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_WITHDRAWAL_SUCCESS);
				return SUCCESS;
			} else if(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY.equals(result)) {
				getModel().setErrorMessage("MSG_NAB022");
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022);
				return ERROR;
			}else {
				getModel().setErrorMessage("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg");
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				return ERROR;
			}
			
		} else {
			log.warn("Cannot support this method: " + withdrawalInfo.getServiceType());
			getModel().setErrorMessage("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg");
			setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
			return ERROR;
		}
	}

	/**
	 * ibDeposit
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Dec 4, 2012
	 */
	private String ibDeposit(String customerId, Integer deviceType) {
		log.info("[start] process deposit Ib");
		DepositInfo depositInfo = getModel().getDepositInfo();
		if(depositInfo == null) {
			depositInfo = new DepositInfo();
		}
		depositInfo.setCurrencyCode(getModel().getCurrencyCode());
		depositInfo.setCustomerId(getModel().getClientCustomerId());
		depositInfo.setAmount(getModel().getAmount());		
		depositInfo.setMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
		depositInfo.setServiceType(getModel().getServiceType());
		depositInfo.setRemark(getModel().getRemark());
		//[NTS1.0-QuyTM]Aug 14, 2012A - Start - add regCustomerId for new requirement  
		depositInfo.setRegCustomerId(customerId);
		//[NTS1.0-QuyTM]Aug 14, 2012A - End
		depositInfo.setDepositRoute(deviceType);
		getModel().setDepositInfo(depositInfo);
		if(IConstants.PAYMENT_METHOD.BANK_TRANSFER == depositInfo.getMethod()) {			
			String result = depositManager.depositBankTransfer(depositInfo);
			if(result == "banktranser_completed") {		
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
				return SUCCESS;
			} else {
				setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
				getModel().setErrorMessage("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg");
				return ERROR;
			}
		} else {
			setMsgCode(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR);
			log.warn("Cannot support this method: " + depositInfo.getServiceType());
			getModel().setErrorMessage("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg");
			return ERROR;
		}
	}

	
	/**
	 * validateCustomerInfo
	 * validate Inputed CustomerInfo
	 * @param
	 * @return
	 * @auth longnd
	 * @CrDate Aug 4, 2012
	 * @MdDate
	 */
//	private void validateCustomerInfo(CustomerInfo customerInfo) {
//		String message = getModel().getErrorMessage();
//		if (message == null) {
//			message = new String("");
//		}
//		String customerId = customerInfo.getCustomerId();
//		String customerName = customerInfo.getFullName();
//		if ((customerName==null && customerId ==null)||(isEmptyString(customerName) && isEmptyString(customerId))) {
//			message= getText("nts.ams.fe.message.ibmanagement.ibcustomer.customerinfo.inputinvalid");
//		} 
//		getModel().setErrorMessage(message);
//	} 

	/**
	 * getMsgCode
	 * get Message Code 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	private void getMsgCode(String msgCode) {
		if(msgCode != null) {
			if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS)) {
				getModel().setSuccessMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.deposit.success"));
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_WITHDRAWAL_SUCCESS)) {
				getModel().setSuccessMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.withdrawal.success"));
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_TRANSFER_SUCCESS)) {
				getModel().setSuccessMessage(getText("nts.ams.fe.transfer.message.MSG_NAB014"));
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB026)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB026"));				
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB027)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB027"));				
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB028)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB028"));				
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB029)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB029"));				
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_ERROR)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.fail.msg"));
			} else if(msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_CREATE_SUCESS)) {
				getModel().setSuccessMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.createsuccess"));
			} else if(msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_CREATE_FAIL)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.createfail"));
			} else if (msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_TIME_OUT)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
			} else if (msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NOT_FOUND)) {
				getModel().setSuccessMessage(getText("nts.ams.fe.message.ibmanager.kickback.history.notFound"));
			} else if(msgCode.equals(IConstants.IB_DEPOSIT_MSG_CODE.MSG_NAB022)) {
				getModel().setErrorMessage(getText("MSG_NAB022"));	
			} else if (msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_EMAIL_EXIST)) {
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.NAB033"));
			} if(msgCode.equals(IConstants.WITHDRAW_MT4_STATUS.WITHDRAW_NOT_ENOUGH_MONEY)){
				getModel().setErrorMessage(getText("MSG_NAB022"));
			} if(msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_ACCOUNT_PROCESSING)){
				getModel().setErrorMessage(getText("nts.ams.fe.error.message.account.processing"));
            } if(msgCode.equals(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_USER_EXIST)){
                getModel().setErrorMessage(getText("MSG_SC_066"));
			} if(msgCode.equals("nts.ams.fe.error.message.failure")) {
				getModel().setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			}
		}
	}

	/**
	 * registerCustomerInformation
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String registerCustomerInformation() {
		try {
			if(result != null) {
				getMsgCode(result);
			}
			//[NTS1.0-Administrator]Oct 19, 2012A - Start - get list currencyCode from WL CONFIG
//		String wlCode = "";
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {				
//				wlCode = frontUserOnline.getWlCode();
					currencyCode = frontUserOnline.getCurrencyCode();
                    if(model.getCustomerInfo() == null){
                        model.setCustomerInfo(new CustomerInfo());
                        model.getCustomerInfo().setCountryId(frontUserOnline.getCountryId());
                    }
				}
			}
//		List<String> listWhiteLabelConfig = ibManager.getListWhiteLabelConfigInfo(IConstants.WHITE_LABEL_CONFIG.BASE_CURRENCY, wlCode);
//		model.setListWhiteLabelConfig(listWhiteLabelConfig);
			List<String> listCurrencyCode = new ArrayList<String>();
			listCurrencyCode.add(currencyCode);
			getModel().setListCurrencyCode(listCurrencyCode);
			//[NTS1.0-Administrator]Oct 19, 2012A - End
            model.setMapCountry(depositManager.getListCountryInfo());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	/**
	 * confirmCustomerInformation
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author 
	 * @CrDate Nov 29, 2012
	 */
	public String confirmCustomerInformation() {
		try {
//		String wlCode = "";		
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			FrontUserOnline frontUserOnline = null;
			if(frontUserDetails != null) {
				frontUserOnline = frontUserDetails.getFrontUserOnline();	
				if (frontUserOnline!= null) {				
//				wlCode = frontUserOnline.getWlCode();
					currencyCode = frontUserOnline.getCurrencyCode();
				}
			}
			CustomerInfo customerInfo = getModel().getCustomerInfo();
            model.setMapCountry(depositManager.getListCountryInfo());
			if(customerInfo != null) 
				validateAddCustomer(true);
			if(hasFieldErrors()) {
//			List<String> listWhiteLabelConfig = ibManager.getListWhiteLabelConfigInfo(IConstants.WHITE_LABEL_CONFIG.BASE_CURRENCY, wlCode);
//			model.setListWhiteLabelConfig(listWhiteLabelConfig);
				List<String> listCurrencyCode = new ArrayList<String>();
				listCurrencyCode.add(currencyCode);
				getModel().setListCurrencyCode(listCurrencyCode);
				return INPUT;
			}	
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	/**
	 * validateAddCustomer
	 * validate add customer for IB Manager
	 *
     * @param validateEmailMatch@return
	 * @auth LongND
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	private void validateAddCustomer(boolean validateEmailMatch) {
		CustomerInfo customerInfo = getModel().getCustomerInfo();
		List<String> listContents = null;
		if(customerInfo != null) {
            String username = customerInfo.getUsername();
			if(username == null || StringUtils.isBlank(username)){
				listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.username"));
				addFieldError("errorMessage", getText("MSG_NAF001", listContents));
				getModel().setErrorMessage(getText("MSG_NAF001", listContents));
				return;
			}
            String[] notAllowChars = new String[]{" ", "@", "!", "<", "#", "$", "%", "&", "*"};
            for (String noChar : notAllowChars){
                if(username.contains(noChar)){
                    listContents = new ArrayList<String>();
                    //listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.username"));
                    addFieldError("errorMessage", getText("MSG_SC_065", listContents));
                    getModel().setErrorMessage(getText("MSG_SC_065", listContents));
                    return;
                }
            }
            List<ScCustomer> scCustomerList = scCustomerDAO.findByUserNameAccountOpenStatus(username, IConstants.ACCOUNT_OPEN_STATUS.BEFORE_REGISTER);
            if(scCustomerList.size() > 0){
                listContents = new ArrayList<String>();
                //listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.username"));
                addFieldError("errorMessage", getText("MSG_SC_065", listContents));
                getModel().setErrorMessage(getText("MSG_SC_066", listContents));
                return;
            }
			if(customerInfo.getFirstName() == null || StringUtils.isBlank(customerInfo.getFirstName())) {
				listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.firstname"));
				addFieldError("errorMessage", getText("MSG_NAF001", listContents));
				getModel().setErrorMessage(getText("MSG_NAF001", listContents));
				return;
			}

            if(new Integer(0).equals(customerInfo.getPhoneType())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phonetype"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }
			
			if(customerInfo.getLastName() == null || StringUtils.isBlank(customerInfo.getLastName())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.lastname"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }

            if(StringUtil.isEmpty(customerInfo.getPhoneCode())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }
            
            if(StringUtil.isEmpty(customerInfo.getTel1())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone_number"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }
            
            try{
                new BigInteger(customerInfo.getPhoneCode());
            }catch(Exception e){
                addFieldError("errorMessage", getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone_digit_only"));
                getModel().setErrorMessage(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone_digit_only"));
                return;
            }
            
            try{
                new BigInteger(customerInfo.getTel1());
            }catch(Exception e){
                addFieldError("errorMessage", getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone_digit_only"));
                getModel().setErrorMessage(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.phone_digit_only"));
                return;
            }
            
            /*if(StringUtil.isEmpty(customerInfo.getAddress())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.address"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }
            if(StringUtil.isEmpty(customerInfo.getCity())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.city"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }
            if(StringUtil.isEmpty(customerInfo.getPrefecture())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.state"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }

            if(StringUtil.isEmpty(customerInfo.getBirthday())) {
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.customer_information.birthday"));
                addFieldError("errorMessage", getText("MSG_NAF001", listContents));
                getModel().setErrorMessage(getText("MSG_NAF001", listContents));
                return;
            }*/
            if(!StringUtil.isEmpty(customerInfo.getBirthday()) && !DateUtil.isValidDate(customerInfo.getBirthday(), DateUtil.PATTERN_YYMMDD)){
                listContents = new ArrayList<String>();
                listContents.add(getText("nts.ams.fe.label.customer_information.birthday"));
                addFieldError("errorMessage", getText("MSG_NAB053", listContents));
                getModel().setErrorMessage(getText("MSG_NAB053", listContents));
                return;
            }

            if(customerInfo.getMailMain() == null || StringUtils.isBlank(customerInfo.getMailMain())) {
				listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.email"));
				addFieldError("errorMessage", getText("MSG_NAF001", listContents));
				getModel().setErrorMessage(getText("MSG_NAF001", listContents));
				return;
			}
			if(!FormatHelper.checkValidEmail(customerInfo.getMailMain())) {
				listContents = new ArrayList<String>();			
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.MSG_NAB007"));
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.MSG_NAB007"));
				return;
			}
            if(validateEmailMatch && !customerInfo.getMailMain().equals(customerInfo.getConfirmMailMain())){
                listContents = new ArrayList<String>();
                addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.MSG_NAB108"));
                getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.MSG_NAB108"));
                return;
            }
			if(accountManager.isExistMail(customerInfo.getMailMain())) {
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.NAB033"));
				getModel().setErrorMessage(getText("nts.ams.fe.message.ibmanagement.ibcustomer.addcustomer.NAB033"));
				return;
			}

            if(accountManager.isExistPhone(customerInfo.getPhoneCode() + customerInfo.getTel1())){
                addFieldError("errorMessage", getText("MSG_SC_020", new String[]{getText("nts.socialtrading.naf603.label.phone")}));
                getModel().setErrorMessage(getText("MSG_SC_020", new String[]{getText("nts.socialtrading.naf603.label.phone")}));
                return;
            }

			if(!customerInfo.isFxBoFlag() && !customerInfo.isDemoFxFlag()) {
				listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.serviceType"));	
				addFieldError("errorMessage", getText("MSG_NAF001", listContents));
				getModel().setErrorMessage(getText("MSG_NAF001", listContents));
				return;
			}
			if(customerInfo.getCurrencyCode() == null || StringUtil.isEmpty(customerInfo.getCurrencyCode())) {
				listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.ibmanagement.ibcustomer.addcustomer.currencycode"));	
				addFieldError("errorMessage", getText("MSG_NAF001", listContents));
				getModel().setErrorMessage(getText("MSG_NAF001", listContents));
				return;
			}
		}
	}

	/**
	 * registerCustomerSubmit
	 * execute register customer 
	 * @param
	 * @return
	 * @auth LongND
	 * @CrDate Aug 14, 2012
	 * @MdDate
	 */
	public String registerCustomerSubmit() {
		String currentUserWLcode = null;
		String currentUserId = null;
		String language = "";
		Map<String, String> mapOpenAccount = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_ACCOUNT);
		CustomerInfo customerInfo = getModel().getCustomerInfo();
		try{
			
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if(frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if(frontUserOnline != null) {
					currentUserWLcode = frontUserOnline.getWlCode();
					currentUserId = frontUserOnline.getUserId();
					//customerInfo.setCountryId(frontUserOnline.getCountryId());
					//customerInfo.setCountryName(frontUserOnline.getCountryName());
					customerInfo.setAccountClass(frontUserOnline.getDeviceType());
					language = frontUserOnline.getLanguage();
					customerInfo.setDisplayLanguage(language);
				} else {
					setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_TIME_OUT);
					return ERROR;
				}
			}
			
			validateAddCustomer(false);
			if(hasFieldErrors()) {
				setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_USER_EXIST);
				return ERROR;
			}	
			
			if(mapOpenAccount == null) {
				mapOpenAccount = new HashMap<String, String>();
				FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_ACCOUNT, mapOpenAccount);
			}
			String mailMain = customerInfo.getMailMain();
			synchronized (mapOpenAccount) {
				String transactionValue = mapOpenAccount.get(mailMain);
				if(transactionValue == null) {
					mapOpenAccount.put(mailMain, mailMain);
				} else {
					getModel().setInfoMessage(getText("nts.ams.fe.error.message.account.processing"));
					setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_ACCOUNT_PROCESSING);
					return SUCCESS;
				} 
			}
			
			customerInfo.setFullName(customerInfo.getFirstName() + " " + customerInfo.getLastName());
			//[NTS1.0-Nguyen.Manh.Thang]Oct 24, 2012M - Start 
			//check email is exists or not
			if(accountManager.getCustomerInfoByEmail(mailMain) != null) {
				synchronized (mapOpenAccount) {
					mapOpenAccount.remove(mailMain);
				}
				setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_EMAIL_EXIST);
				return ERROR;
			}
			
			String rootPath = null;
			if(customerInfo.getSex() != null){
				if(customerInfo.getSex().equals(IConstants.GENDER.FEMALE)){
					rootPath = httpRequest.getSession().getServletContext().getRealPath("/images/user-pict-female.png");
				} else {
					rootPath = httpRequest.getSession().getServletContext().getRealPath("/images/user-pict-male.png");
				}
			}else{
				rootPath = httpRequest.getSession().getServletContext().getRealPath("/images/user-pict-male.png");
			}
			
			//[NTS1.0-Nguyen.Manh.Thang]Oct 24, 2012M - End
			Integer registerAccountResult = getIbManager().registerIBCustomer(customerInfo,currentUserWLcode,currentUserId,rootPath);
			
			if(!IConstants.OPEN_ACCOUNT_STATUS.ACCOUNT_CREATE_SUCCESS.equals(registerAccountResult)) {
				synchronized (mapOpenAccount) {
					mapOpenAccount.remove(mailMain);
				}
				setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_CREATE_FAIL);
				return ERROR;
			}
		} catch(Exception ex) {			
			synchronized (mapOpenAccount) {
				mapOpenAccount.remove(customerInfo.getMailMain());
			}
			log.error(ex.getMessage(),ex);
			setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_CREATE_FAIL);
			return ERROR;
		}
		setMsgCode(IConstants.AMS_OPEN_ACCOUNT_MSG_CODE.MSG_CREATE_SUCESS);
		synchronized (mapOpenAccount) {
			mapOpenAccount.remove(customerInfo.getMailMain());
		}
		return SUCCESS;
	}

	/**
	 * @return the listSymbol
	 */
	public List<String> getListSymbol() {
		log.info("[Start] get list symbol");			
		List<String> listSymbol = new ArrayList<String>();
		listSymbol = ibManager.getListSymbol();
		log.info("[End] get list symbol");
		return listSymbol;
		
	}
	/************************************** GET SET *************************************************/
	private boolean isEmptyString(String str) {
		if (str == null)
			return true;
		if (str.trim().length() == 0)
			return true;
		return false;
	}

	public IIBManager getIbManager() {
		return ibManager;
	}

	/**
	 * @return the msgCode
	 */
	public String getMsgCode() {
		return msgCode;
	}

	/**
	 * @param msgCode the msgCode to set
	 */
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @param depositManager the depositManager to set
	 */
	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}

	/**
	 * @param accountManager the accountManager to set
	 */
	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	/**
	 * @param withdrawalManager the withdrawalManager to set
	 */
	public void setWithdrawalManager(IWithdrawalManager withdrawalManager) {
		this.withdrawalManager = withdrawalManager;
	}

	public IBalanceManager getBalanceManager() {
		return balanceManager;
	}

	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void getListCurrency() {
		
	}

	public IBModel getModel() {
		return model;
	}

	public void setModel(IBModel model) {
		this.model = model;
	}

	public ITransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(ITransferManager transferManager) {
		this.transferManager = transferManager;
	}

    public IScCustomerDAO<ScCustomer> getScCustomerDAO() {
        return scCustomerDAO;
    }

    public void setScCustomerDAO(IScCustomerDAO<ScCustomer> scCustomerDAO) {
        this.scCustomerDAO = scCustomerDAO;
    }
}


