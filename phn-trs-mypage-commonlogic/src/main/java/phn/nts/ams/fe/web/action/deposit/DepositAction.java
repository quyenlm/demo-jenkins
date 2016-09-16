package phn.nts.ams.fe.web.action.deposit;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.netpay.NetPay;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.ObjectCopy;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.com.trs.util.common.ITrsConstants.AMS_WHITELABEL_CONFIG_KEY;
import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;
import phn.com.trs.util.common.ITrsConstants.TRS_CONSTANT;
import phn.com.trs.util.common.UAgentInfo;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IBalanceManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.business.IIBManager;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.AllChargeResponseInfo;
import phn.nts.ams.fe.domain.BalanceInfo;
import phn.nts.ams.fe.domain.BankTransferInfo;
import phn.nts.ams.fe.domain.BjpInfo;
import phn.nts.ams.fe.domain.CreditCardInfo;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.CustomerEwalletInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.domain.CustomerServicesInfo;
import phn.nts.ams.fe.domain.DepositInfo;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.domain.LibertyInfo;
import phn.nts.ams.fe.domain.NetellerInfo;
import phn.nts.ams.fe.domain.PayonlinePaymentDetail;
import phn.nts.ams.fe.domain.PayzaInfo;
import phn.nts.ams.fe.domain.RateInfo;
import phn.nts.ams.fe.model.DepositModel;
import phn.nts.ams.fe.payment.AllChargeContext;
import phn.nts.ams.fe.payment.LibertyContext;
import phn.nts.ams.fe.payment.PayonlineSystemContext;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;
import phn.nts.social.fe.web.action.BaseSocialAction;

public class DepositAction extends BaseSocialAction<DepositModel> {
	/**
	 * 
	 */
	private static Logit log = Logit.getInstance(DepositAction.class);
	private static final long serialVersionUID = 7115890598489635400L;
	private DepositModel depositModel = new DepositModel();
	private IDepositManager depositManager = null;
	private IAccountManager accountManager = null;
	// private IBalanceManager balanceManager = null;
	private IIBManager ibManager = null;
	private IExchangerManager exchangerManager = null;
	private String result;
	private String payKey;
	private Integer ErrorCode;
	private String msgCode;

	// [NTS1.0-anhndn]Feb 27, 2013A - Start
	private String lr_paidto;
	private String lr_paidby;
	private String lr_amnt;
	private String lr_fee_amnt;
	private String lr_currency;
	private String lr_transfer;
	private String lr_store;
	private String lr_timestamp;
	private String lr_merchant_ref;
	private String lr_encrypted;
	private String order_id;
	// [NTS1.0-anhndn]Feb 27, 2013A - End

	// [NTS1.0-Nguyen.Xuan.Bach]Mar 7, 2013A - Start for NETPAY
	private String replyCode;
	private String replyDesc;
	private String trans_id;
	private String trans_date;
	private String trans_amount;
	private String trans_currency;
	private String trans_installments;
	private String trans_refNum;
	private String client_id;
	private String storage_id;
	private String paymentDisplay;
	private String signature;
	private String client_fullName;
	private String client_phoneNum;
	private String recurringSeries_id;

	/**
	 * @author tungpv
	 * @return
	 */
	public String index() {
		try{
			log.info("[start] deposit index");
			if (result != null) {
				getMsgCode(result);
			}
			setRawUrl(IConstants.FrontEndActions.DEPOSIT_INDEX);
			readInformationUserOnline();
			readEwalletInformation();
			initRate();
			initRateCny();

			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					AmsWhitelabelConfig amsWlConfig = depositManager.getAmsWhitelabelConfig(AMS_WHITELABEL_CONFIG_KEY.DIRECT_DEPOSIT_BANK, frontUserOnline.getWlCode());
					if (amsWlConfig != null) {
						String cfgValue = amsWlConfig.getConfigValue();
						String[] cfgVlArr = cfgValue.split(",");
						HashMap<String, String> m = new HashMap<String, String>();
						for (int i = 0; i < cfgVlArr.length; i++) {
							m.put(cfgVlArr[i], getText(cfgVlArr[i]));
						}
						LinkedHashMap<String, String> mapBank = new LinkedHashMap<String, String>();
						if (m.get(BJP_CONFIG.BANK_CODE_UFJ) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE_UFJ, m.get(BJP_CONFIG.BANK_CODE_UFJ));
						if (m.get(BJP_CONFIG.BANK_CODE0009) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE0009, m.get(BJP_CONFIG.BANK_CODE0009));
						if (m.get(BJP_CONFIG.BANK_CODE0003) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE0003, m.get(BJP_CONFIG.BANK_CODE0003));
						if (m.get(BJP_CONFIG.BANK_CODE0036) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE0036, m.get(BJP_CONFIG.BANK_CODE0036));
						if (m.get(BJP_CONFIG.BANK_CODE_SBI) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE_SBI, m.get(BJP_CONFIG.BANK_CODE_SBI));
						if (m.get(BJP_CONFIG.BANK_CODE0033) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE0033, m.get(BJP_CONFIG.BANK_CODE0033));
						if (m.get(BJP_CONFIG.BANK_CODE9900) != null)
							mapBank.put(BJP_CONFIG.BANK_CODE9900, m.get(BJP_CONFIG.BANK_CODE9900));
						depositModel.setMapBjpBank(mapBank);
					}
					amsWlConfig = depositManager.getAmsWhitelabelConfig(depositModel.getBaseCurrencyCode() + AMS_WHITELABEL_CONFIG_KEY.MIN_DEPOSIT_AMOUNT_TAIL, frontUserOnline.getWlCode());
					if (amsWlConfig != null) {
						Double min = Double.parseDouble(amsWlConfig.getConfigValue());
						depositModel.setBjpMinAmount(min.intValue());
					}
					amsWlConfig = depositManager.getAmsWhitelabelConfig(depositModel.getBaseCurrencyCode() + AMS_WHITELABEL_CONFIG_KEY.MAX_DEPOSIT_AMOUNT_TAIL, frontUserOnline.getWlCode());
					if (amsWlConfig != null) {
						Double max = Double.parseDouble(amsWlConfig.getConfigValue());
						depositModel.setBjpMaxAmount(max.intValue());
					}

				}
			}
			String isback = httpRequest.getParameter("back");
			if (isback != null) {
				Object amt = httpRequest.getSession().getAttribute("bjpAmt");
				if (amt != null && amt != "") {
					DecimalFormat df = new DecimalFormat("###.###");
					depositModel.setBjpBankAmount(df.format(Double.parseDouble(amt + "")));
				}
				Object bcode = httpRequest.getSession().getAttribute("bjpBankCode");
				if (bcode != null && bcode != "") {
					depositModel.setBjpBankCode(bcode + "");
				}
			}
			log.info("[end] deposit index");
		} catch (Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return INPUT;
	}

	private void readEwalletInformation() {
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					String customerId = frontUserOnline.getUserId();
					String publicKey = frontUserOnline.getPublicKey();
					Map<Integer, List<CustomerEwalletInfo>> mapListCustomerEwalletInfo = depositManager.getListCustomerEwalletInfo(customerId, publicKey);
					if (mapListCustomerEwalletInfo != null && mapListCustomerEwalletInfo.size() > 0) {
						// get list neteller on db
						List<CustomerEwalletInfo> listCustomerEwalletInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.NETELLER);
						depositModel.setListNeteller(listCustomerEwalletInfo);
						// get list liberty on db
						List<CustomerEwalletInfo> listLibertyInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.LIBERTY);
						depositModel.setListLiberty(listLibertyInfo);
						// get list payza on db
						listCustomerEwalletInfo = mapListCustomerEwalletInfo.get(IConstants.EWALLET_TYPE.PAYZA);
						depositModel.setListPayza(listCustomerEwalletInfo);
					}
					List<CreditCardInfo> listCreditCardInfo = depositManager.getListCustomerCreditCardInfo(customerId, publicKey);
					depositModel.setListCreditInfo(listCreditCardInfo);
					// get list country
					Map<String, String> mapCountry = depositManager.getListCountry();
					depositModel.setMapCountry(mapCountry);
					// [NTS1.0-Administrator]Apr 1, 2013A - Start - add cardtype
					// into combobox
					Map<String, String> mapCardType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CARD_TYPE);
					depositModel.setMapCardType(mapCardType);
					// [NTS1.0-Administrator]Apr 1, 2013A - End

				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

	}

	public void readExchangerInfo() {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				// get map exchanger
				Map<String, String> mapExchanger = exchangerManager.getMapExchanger(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
				if (mapExchanger == null) {
					mapExchanger = new TreeMap<String, String>();
				}
				depositModel.setMapExchanger(mapExchanger);
			} else {
				depositModel.setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
			}
		}
	}

	public void getListExchanger() {
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				List<ExchangerInfo> ls = depositManager.getListExchangers(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
				if (ls == null) {
					ls = new ArrayList<ExchangerInfo>();
				}
				depositModel.setListExchanger(ls);
			}
		}

	}

	private void initRateCny() {
		String currencyCode = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				currencyCode = frontUserOnline.getCurrencyCode();
			}
		}
		if (currencyCode == null) {
			currencyCode = IConstants.CURRENCY_CODE.USD;
		}

		RateInfo rateInfoCny = depositManager.getLastestRate(currencyCode + IConstants.CURRENCY_CODE.CNY);
		depositModel.setRateInfoCny(rateInfoCny);
	}

	/**
	 * 　 init rate for payonline system
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 23, 2012
	 * @MdDate
	 */
	public void initRate() {
		// init rate for payonline gateway
		try {
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					// Map<String, String> mapRateConfig =
					// SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY
					// + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
					// BigDecimal midRate =
					// depositManager.getFrontRate(frontUserOnline.getCurrencyCode(),
					// IConstants.CURRENCY_CODE.USD);
					// BigDecimal convertRate =
					// depositManager.getConvertRate(frontUserOnline.getCurrencyCode(),
					// IConstants.CURRENCY_CODE.USD);
					RateInfo rateInfo = depositManager.getRateInfo(frontUserOnline.getCurrencyCode(), IConstants.CURRENCY_CODE.USD);
					// RateInfo rateInfo =
					// MT4Manager.getInstance().getRate(frontUserOnline.getCurrencyCode(),
					// mapRateConfig);
					if (rateInfo != null) {
						List<Object> listParams = new ArrayList<Object>();
						listParams.add(rateInfo.getSymbolName());
						rateInfo.setExchangeRate(getText("nts.ams.fe.label.deposit.payonline.rateof", listParams));
						depositModel.setRateInfo(rateInfo);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

	}

	/**
	 * @author tungpv
	 * @return
	 */
	public String depositConfirmed() {
		httpRequest.getSession().setAttribute("bjpAmt", depositModel.getBjpBankAmount());
		httpRequest.getSession().setAttribute("bjpBankCode", depositModel.getBjpBankCode());
		// export file language start
//		try {
//			StringBuffer sb = new StringBuffer();
//			StringBuffer sbNotJa = new StringBuffer();
//			Properties pen = new Properties();
//			pen.load(new FileInputStream("D:\\Trs Project Ws\\FrontEnd\\nts-sc-front\\src\\resources\\front.properties"));
//			Enumeration<Object> ken = pen.keys();
//			Properties p = new Properties();
//			p.load(new FileInputStream("D:\\Trs Project Ws\\FrontEnd\\nts-sc-front\\src\\resources\\front_ja.properties"));
//
//			while (ken.hasMoreElements()) {
//				Object keyEn = ken.nextElement();
//				if(p.get(keyEn)!=null&&p.get(keyEn)!=null){
//					String v = p.get(keyEn).toString();
//					sb.append(keyEn.toString() + "=" + v + "\n");
//				}else{
//					String v = pen.get(keyEn).toString();
//					sbNotJa.append(keyEn.toString() + "=" + v + "\n");
//				}
//			}
//			httpResponse.setContentType("application/x-csv");
//			httpResponse.setHeader("Content-Disposition", "attachment; filename=Excel.csv");
//			sb.append("\n######PROPERTIES NOT IN JA###### \n");
//			sb.append(sbNotJa);
//			httpResponse.getWriter().write(sb.toString());
//			httpResponse.getWriter().close();
//			return SUCCESS;
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// export file language end
		try {
			String bamt = depositModel.getBjpBankAmount();
			bamt = bamt.replaceAll(",", "");
			Double amt;
			try {
				amt = Double.parseDouble(bamt);
			} catch (NumberFormatException e) {
				log.error("Deposit with Amount not correct");
				index();
				depositModel.setErrorMessage(getText(ITrsConstants.MSG_TRS_NAF_0036));
				return ERROR;
			}

			String bjpBankCode = depositModel.getBjpBankCode();
			if (bjpBankCode == null || bjpBankCode.trim() == "") {
				log.error("Deposit with bjp bank code not correct");
				index();
				// setMsgCode(ITrsConstants.MSG_NAB001);
				depositModel.setErrorMessage(getText(ITrsConstants.MSG_NAB001));
				return ERROR;
			}
			Integer min = depositModel.getBjpMinAmount();
			if (amt < min) {
				log.error("Deposit with amount less than min amount");
				index();
				// setMsgCode(ITrsConstants.MSG_NAB024);
				depositModel.setErrorMessage(getText("MSG_NAB024", (min + "").split(",")));
				// depositModel.setErrorMessage(getText("MSG_NAB024"));
				return ERROR;
			}
			Integer max = depositModel.getBjpMaxAmount();
			if (amt > max) {
				log.error("Deposit with amount less than max amount");
				index();
				// setMsgCode(ITrsConstants.MSG_NAB023);
				depositModel.setErrorMessage(getText("MSG_NAB023", (max + "").split(",")));
				return ERROR;
			}
			CustomerInfo customerInfo = accountManager.getCustomerInfo(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
			if (customerInfo == null) {
				log.error("Account service status not in = 0 inactive");
				index();
				// setMsgCode(ITrsConstants.MSG_NAB091);
				depositModel.setErrorMessage(getText(ITrsConstants.MSG_NAB091, getText("service_type.asm").split(",")));
				return ERROR;
			}
			if (customerInfo.getAccountOpenStatus().intValue() == 0) {
				log.error("Account service status not in = 0 inactive");
				index();
				// setMsgCode(ITrsConstants.MSG_NAB091);
				depositModel.setErrorMessage(getText(ITrsConstants.MSG_NAB091, getText("service_type.asm").split(",")));
				return ERROR;
			}
			index();
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			index();
			return ERROR;
		}
	}

	/**
	 * validate Deposit
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */
	/*public String depositConfirmCheck() {
		try {
			BjpInfo bi = new BjpInfo();
			String bankCode = httpRequest.getParameter("bankCode");
			String amount = httpRequest.getParameter("amount");
			Double amt;
			try {
				amount = amount.replaceAll(",", "");
				amt = Double.parseDouble(amount);
			} catch (NumberFormatException e) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_TRS_NAF_0036"));
				setMsgCode(ITrsConstants.MSG_TRS_NAF_0036);
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			if (bankCode == null || bankCode.trim() == "") {
				bi.setValid(0);
				setMsgCode(ITrsConstants.MSG_NAB001);
				bi.setMsg(getText("MSG_NAB001", getText("payment_method.direct.deposit").split(",")));
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			AmsWhitelabelConfig amsWlConfig = depositManager.getAmsWhitelabelConfig(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getCurrencyCode()
					+ AMS_WHITELABEL_CONFIG_KEY.MIN_DEPOSIT_AMOUNT_TAIL, FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getWlCode());
			if (amsWlConfig != null) {
				Double min = Double.parseDouble(amsWlConfig.getConfigValue());
				depositModel.setBjpMinAmount(min.intValue());
			}
			Integer min = depositModel.getBjpMinAmount();
			if (amt < min) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB024"));
				setMsgCode(ITrsConstants.MSG_NAB024);
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			amsWlConfig = depositManager.getAmsWhitelabelConfig(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getCurrencyCode() + AMS_WHITELABEL_CONFIG_KEY.MAX_DEPOSIT_AMOUNT_TAIL,
					FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getWlCode());
			if (amsWlConfig != null) {
				Double max = Double.parseDouble(amsWlConfig.getConfigValue());
				depositModel.setBjpMaxAmount(max.intValue());
			}
			Integer max = depositModel.getBjpMaxAmount();
			if (amt > max) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB023", (max + "").split(",")));
				setMsgCode(ITrsConstants.MSG_NAB023);
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			CustomerInfo customerInfo = accountManager.getCustomerInfo(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
			if (customerInfo == null) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB091", getText("service_type.asm").split(",")));
				setMsgCode(ITrsConstants.MSG_NAB091);
				depositModel.setBjpInfo(bi);
				log.error("Account not active");
				return SUCCESS;
			}
			if (customerInfo.getAccountOpenStatus().intValue() == 0) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB091", getText("service_type.asm").split(",")));
				setMsgCode(ITrsConstants.MSG_NAB091);
				log.error("Account not active");
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			AmsDeposit amsDeposit = new AmsDeposit();
			AmsDepositRef amsDepositref = new AmsDepositRef();
			AmsCustomer amsCustomer = new AmsCustomer();
			amsCustomer.setCustomerId(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
			amsDeposit.setAmsCustomer(amsCustomer);
			amsDeposit.setDepositType(1);
			amsDeposit.setServiceType(0);
			amsDeposit.setCurrencyCode(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getCurrencyCode());
			amsDeposit.setStatus(ITrsConstants.BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS);
			amsDeposit.setDepositMethod(ITrsConstants.BJP_CONFIG.BJP_METHOD);
			amsDeposit.setDepositGateway(AMS_WHITELABEL_CONFIG_KEY.BJP);
			amsDeposit.setDepositAmount(amt);
			amsDeposit.setDepositFee(0d);
			amsDeposit.setRate(1d);
			Integer depositRoute = 1;
			String userAgent = httpRequest.getHeader("User-Agent");
			String httpAccept = httpRequest.getHeader("Accept");
			UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);
			if (detector.detectMobileQuick() || detector.isTierTablet) {
				depositRoute = 2;
			}
			amsDeposit.setDepositRoute(depositRoute);
			amsDepositref.setEwalletType(ITrsConstants.BJP_CONFIG.BJP_METHOD);
			amsDepositref.setEwalletEmail(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getLoginId());
			amsDepositref.setCountryId(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getCountryId());
			amsDepositref.setBeneficiaryBankName(null);
			amsDepositref.setInputDate(new Timestamp(System.currentTimeMillis()));
			// using instead bankcode
			amsDepositref.setCcNo(bankCode);
			amsDeposit.setDepositAcceptDatetime(new Timestamp(System.currentTimeMillis()));
			// TODO
			String rs = accountManager.saveBjpDeposit(amsDeposit, amsDepositref);
			if (rs.isEmpty()) {
				log.error("Can not save to AMS_DEPOSIT");
				index();
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB068"));
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			bi.setValid(1);
			bi.setMsg("");
			bi.setAmount(amt);
			bi.setBankCode(bankCode);
			bi.setPOST_BANK_CODE(bankCode);
			// bi.setPOST_REMARKS_3(rs);
			bi.setPOST_TRAN_AMOUNT(amt);

			bi = accountManager.updateBjpInfo(bi);
			if (bi.getValid() == 0) {
				bi.setValid(0);
				bi.setMsg(getText("MSG_NAB068"));
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				depositModel.setBjpInfo(bi);
				return SUCCESS;
			}
			bi.setPOST_VALID_RETURN_URL(bi.getPOST_VALID_RETURN_URL());
			bi.setPOST_INVALID_RETURN_URL(bi.getPOST_INVALID_RETURN_URL());
			bi.setPOST_CTRL_NO(rs);
			
			Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + TRS_CONSTANT.TRS_WL_CODE);
			bi.setBJP_URL(mapConfiguration.get(ITrsConstants.BJP_CONFIG.BJP_CONNECTION_URL));
			// convert half full end:
			StringBuilder msg = new StringBuilder();
			msg.append("BANK_CODE=" + bi.getPOST_BANK_CODE() + "|");
			msg.append("|CONC_DAY=" + bi.getPOST_CONC_DAY() + "|");
			msg.append("|CTRL_NO=" + bi.getPOST_CTRL_NO() + "|");
			msg.append("|PORTAL_CODE=" + bi.getPOST_PORTAL_CODE() + "|");
			msg.append("|CUS_TEL=" + bi.getPOST_CUS_TEL() + "|");
			msg.append("|CUST_ADDRESS_1=" + bi.getPOST_CUST_ADDRESS_1() + "|");
			msg.append("|CUST_ADDRESS_2=" + bi.getPOST_CUST_ADDRESS_2() + "|");
			msg.append("|CUST_ADDRESS_3=" + bi.getPOST_CUST_ADDRESS_3() + "|");
			msg.append("|CUST_FNAME=" + bi.getPOST_CUST_FNAME() + "|");
			msg.append("|CUST_LNAME=" + bi.getPOST_CUST_LNAME() + "|");
			msg.append("|CUST_NAME=" + bi.getPOST_CUST_NAME() + "|");
			msg.append("|CUST_NAME_1=" + bi.getPOST_CUST_NAME_1() + "|");
			msg.append("|CUST_NAME_2=" + bi.getPOST_CUST_NAME_2() + "|");
			msg.append("|CUST_POSTCODE_1=" + bi.getPOST_CUST_POSTCODE_1() + "|");
			msg.append("|CUST_POSTCODE_2=" + bi.getPOST_CUST_POSTCODE_2() + "|");
			msg.append("|DUE_DAY=" + bi.getPOST_DUE_DAY() + "|");
			msg.append("|EMAIL_ADDRESS=" + bi.getPOST_EMAIL_ADDRESS() + "|");
			msg.append("|GOODS_NAME=" + bi.getPOST_GOODS_NAME() + "|");
			msg.append("|GOODS_NAME_KANA=" + bi.getPOST_GOODS_NAME_KANA() + "|");
			msg.append("|INVALID_RETURN_URL=" + bi.getPOST_INVALID_RETURN_URL() + "|");
			msg.append("|KESSAI_FLAG=" + bi.getPOST_KESSAI_FLAG() + "|");
			msg.append("|M_AMOUNT_1=" + bi.getPOST_M_AMOUNT_1() + "|");
			msg.append("|M_AMOUNT_2=" + bi.getPOST_M_AMOUNT_2() + "|");
			msg.append("|M_AMOUNT_3=" + bi.getPOST_M_AMOUNT_3() + "|");
			msg.append("|M_AMOUNT_4=" + bi.getPOST_M_AMOUNT_4() + "|");
			msg.append("|M_AMOUNT_5=" + bi.getPOST_M_AMOUNT_5() + "|");
			msg.append("|M_AMOUNT_6=" + bi.getPOST_M_AMOUNT_6() + "|");
			msg.append("|M_AMOUNT_7=" + bi.getPOST_M_AMOUNT_7() + "|");
			msg.append("|M_GOODS_NAME_1=" + bi.getPOST_M_GOODS_NAME_1() + "|");
			msg.append("|M_GOODS_NAME_2=" + bi.getPOST_M_GOODS_NAME_2() + "|");
			msg.append("|M_GOODS_NAME_3=" + bi.getPOST_M_GOODS_NAME_3() + "|");
			msg.append("|M_GOODS_NAME_4=" + bi.getPOST_M_GOODS_NAME_4() + "|");
			msg.append("|M_GOODS_NAME_5=" + bi.getPOST_M_GOODS_NAME_5() + "|");
			msg.append("|M_GOODS_NAME_6=" + bi.getPOST_M_GOODS_NAME_6() + "|");
			msg.append("|M_GOODS_NAME_7=" + bi.getPOST_M_GOODS_NAME_7() + "|");
			msg.append("|M_REMARK_1=" + bi.getPOST_M_REMARK_1() + "|");
			msg.append("|M_REMARK_2=" + bi.getPOST_M_REMARK_2() + "|");
			msg.append("|M_REMARK_3=" + bi.getPOST_M_REMARK_3() + "|");
			msg.append("|M_REMARK_4=" + bi.getPOST_M_REMARK_4() + "|");
			msg.append("|M_REMARK_5=" + bi.getPOST_M_REMARK_5() + "|");
			msg.append("|M_REMARK_6=" + bi.getPOST_M_REMARK_6() + "|");
			msg.append("|M_REMARK_7=" + bi.getPOST_M_REMARK_7() + "|");
			msg.append("|REMARKS_1=" + bi.getPOST_REMARKS_1() + "|");
			msg.append("|REMARKS_2=" + bi.getPOST_REMARKS_2() + "|");
			msg.append("|REMARKS_3=" + bi.getPOST_REMARKS_3() + "|");
			msg.append("|SHOP_CODE=" + bi.getPOST_SHOP_CODE() + "|");
			msg.append("|TRAN_TAX=" + bi.getPOST_TRAN_TAX() + "|");
			msg.append("|UN_KESSAI_FLAG=" + bi.getPOST_UN_KESSAI_FLAG() + "|");
			msg.append("|VALID_RETURN_URL=" + bi.getPOST_VALID_RETURN_URL() + "|");
			msg.append("|TRAN_AMOUNT=" + bi.getPOST_TRAN_AMOUNT() + "|");
			msg.append("|Amount=" + bi.getAmount() + "|");
			msg.append("|valid=" + bi.getValid() + "|");
			msg.append("|bankCode=" + bi.getBankCode());
			msg.append("|BJP_URL=" + bi.getBJP_URL());
			log.info("message send to bjp " + msg.toString());
			depositModel.setBjpInfo(bi);
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		BjpInfo bi = new BjpInfo();
		bi.setValid(0);
		bi.setMsg(getText("MSG_NAB068"));
		depositModel.setBjpInfo(bi);
		httpRequest.getSession().setAttribute("bjpAmt", null);
		httpRequest.getSession().setAttribute("bjpBankCode", null);
		httpRequest.getSession().removeAttribute("bjpAmt");
		httpRequest.getSession().removeAttribute("bjpBankCode");
		return SUCCESS;
	}*/

	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */
	public String depositBjpError() {
		try {
			try {
				log.info("BJP response CharacterEncoding = " + httpRequest.getCharacterEncoding());
				httpRequest.setCharacterEncoding("Shift-JIS");
			} catch (UnsupportedEncodingException e) {
			}
			String PORTAL_CODE = httpRequest.getParameter("PORTAL_CODE");
			String SHOP_CODE = httpRequest.getParameter("SHOP_CODE");
			String BANK_CODE = httpRequest.getParameter("BANK_CODE");
			String KESSAI_FLAG = httpRequest.getParameter("KESSAI_FLAG");
			String CTRL_NO = httpRequest.getParameter("CTRL_NO");
			String TRAN_STAT = httpRequest.getParameter("TRAN_STAT");
			String TRAN_REASON_CODE = httpRequest.getParameter("TRAN_REASON_CODE");
			String TRAN_RESULT_MSG = httpRequest.getParameter("TRAN_RESULT_MSG");
			String TRAN_DATE = httpRequest.getParameter("TRAN_DATE");
			String TRAN_TIME = httpRequest.getParameter("TRAN_TIME");
			String CUST_NAME = httpRequest.getParameter("CUST_NAME");
			String CUST_NAME_1 = httpRequest.getParameter("CUST_NAME_1");
			String CUST_NAME_2 = httpRequest.getParameter("CUST_NAME_2");
			String CUST_LNAME = httpRequest.getParameter("CUST_LNAME");
			String CUST_FNAME = httpRequest.getParameter("CUST_FNAME");
			String TRAN_AMOUNT = httpRequest.getParameter("TRAN_AMOUNT");
			String TRAN_FEE = httpRequest.getParameter("TRAN_FEE");
			String PAYMENT_DAY = httpRequest.getParameter("PAYMENT_DAY");
			String GOODS_NAME = httpRequest.getParameter("GOODS_NAME");
			String REMARKS_1 = httpRequest.getParameter("REMARKS_1");
			String REMARKS_2 = httpRequest.getParameter("REMARKS_2");
			String REMARKS_3 = httpRequest.getParameter("REMARKS_3");
			String TRAN_ID = httpRequest.getParameter("TRAN_ID");
			String TRAN_DIGEST = httpRequest.getParameter("TRAN_DIGEST");
			String BANK_CUST_NAME = httpRequest.getParameter("BANK_CUST_NAME");
			String MEIGI_STAT = httpRequest.getParameter("MEIGI_STAT");
			String CONFIRM_TIME = httpRequest.getParameter("CONFIRM_TIME");
			String OPTRAN_DIGEST = httpRequest.getParameter("OPTRAN_DIGEST");
			String depositId = httpRequest.getParameter("CTRL_NO");

			StringBuilder contentResponse = new StringBuilder();
			contentResponse.append("PORTAL_CODE=" + PORTAL_CODE);
			contentResponse.append("|SHOP_CODE=" + SHOP_CODE);
			contentResponse.append("|BANK_CODE=" + BANK_CODE);
			contentResponse.append("|KESSAI_FLAG=" + KESSAI_FLAG);
			contentResponse.append("|CTRL_NO=" + CTRL_NO);
			contentResponse.append("|TRAN_STAT=" + TRAN_STAT);
			contentResponse.append("|TRAN_REASON_CODE=" + TRAN_REASON_CODE);
			contentResponse.append("|TRAN_RESULT_MSG=" + TRAN_RESULT_MSG);
			contentResponse.append("|TRAN_DATE=" + TRAN_DATE);
			contentResponse.append("|TRAN_TIME=" + TRAN_TIME);
			contentResponse.append("|CUST_NAME=" + CUST_NAME);
			contentResponse.append("|CUST_NAME_1=" + CUST_NAME_1);
			contentResponse.append("|CUST_NAME_2=" + CUST_NAME_2);
			contentResponse.append("|CUST_LNAME=" + CUST_LNAME);
			contentResponse.append("|CUST_FNAME=" + CUST_FNAME);
			contentResponse.append("|TRAN_AMOUNT=" + TRAN_AMOUNT);
			contentResponse.append("|TRAN_FEE=" + TRAN_FEE);
			contentResponse.append("|PAYMENT_DAY=" + PAYMENT_DAY);
			contentResponse.append("|GOODS_NAME=" + GOODS_NAME);
			contentResponse.append("|REMARKS_1=" + REMARKS_1);
			contentResponse.append("|REMARKS_2=" + REMARKS_2);
			contentResponse.append("|REMARKS_3=" + REMARKS_3);
			contentResponse.append("|TRAN_ID=" + TRAN_ID);
			contentResponse.append("|TRAN_DIGEST=" + TRAN_DIGEST);
			contentResponse.append("|BANK_CUST_NAME=" + BANK_CUST_NAME);
			contentResponse.append("|MEIGI_STAT=" + MEIGI_STAT);
			contentResponse.append("|CONFIRM_TIME=" + CONFIRM_TIME);
			contentResponse.append("|OPTRAN_DIGEST=" + OPTRAN_DIGEST);
			log.debug("BJP FAIL RESPONSE =" + contentResponse.toString());
			System.out.println(contentResponse);
			index();
			String BJP_KEY = accountManager.getBjpCertificationKey(TRS_CONSTANT.TRS_WL_CODE);
			// String certificationKey =
			// (PORTAL_CODE==null?"":PORTAL_CODE) +
			// (SHOP_CODE==null?"":SHOP_CODE) + (BANK_CODE==null?"":BANK_CODE) +
			// (KESSAI_FLAG==null?"":KESSAI_FLAG) + (CTRL_NO==null?"":CTRL_NO)
			// + (TRAN_STAT==null?"":TRAN_STAT) +
			// (TRAN_REASON_CODE==null?"":TRAN_REASON_CODE) +
			// (TRAN_RESULT_MSG==null?"":TRAN_RESULT_MSG)
			// + (TRAN_DATE==null?"":TRAN_DATE) + (TRAN_TIME==null?"":TRAN_TIME)
			// + (CUST_NAME==null?"":CUST_NAME) +
			// (CUST_LNAME==null?"":CUST_LNAME)
			// + (CUST_FNAME==null?"":CUST_FNAME) +
			// (TRAN_AMOUNT==null?"":TRAN_AMOUNT) + (TRAN_FEE==null?"":TRAN_FEE)
			// + (PAYMENT_DAY==null?"":PAYMENT_DAY)
			// + (GOODS_NAME==null?"":GOODS_NAME) +
			// (REMARKS_1==null?"":REMARKS_1) + (REMARKS_2==null?"":REMARKS_2) +
			// (REMARKS_3==null?"":REMARKS_3) + (TRAN_ID==null?"":TRAN_ID)
			// + BJP_KEY;
			// certificationKey = makeMessageDigest( certificationKey);

			StringBuilder key = new StringBuilder();
			if (PORTAL_CODE != null) {
				key.append(PORTAL_CODE);
			}
			if (SHOP_CODE != null) {
				key.append(SHOP_CODE);
			}
			if (BANK_CODE != null) {
				key.append(BANK_CODE);
			}
			if (KESSAI_FLAG != null) {
				key.append(KESSAI_FLAG);
			}
			if (CTRL_NO != null) {
				key.append(CTRL_NO);
			}
			if (TRAN_STAT != null) {
				key.append(TRAN_STAT);
			}
			if (TRAN_REASON_CODE != null) {
				key.append(TRAN_REASON_CODE);
			}
			if (TRAN_RESULT_MSG != null) {
				key.append(TRAN_RESULT_MSG);
			}
			if (TRAN_DATE != null) {
				key.append(TRAN_DATE);
			}
			if (TRAN_TIME != null) {
				key.append(TRAN_TIME);
			}
			if (CUST_NAME != null) {
				key.append(CUST_NAME);
			}
			if (CUST_LNAME != null) {
				key.append(CUST_LNAME);
			}
			if (CUST_FNAME != null) {
				key.append(CUST_FNAME);
			}
			if (TRAN_AMOUNT != null) {
				key.append(TRAN_AMOUNT);
			}
			if (TRAN_FEE != null) {
				key.append(TRAN_FEE);
			}
			if (PAYMENT_DAY != null) {
				key.append(PAYMENT_DAY);
			}
			if (GOODS_NAME != null) {
				key.append(GOODS_NAME);
			}
			if (REMARKS_1 != null) {
				key.append(REMARKS_1);
			}
			if (REMARKS_2 != null) {
				key.append(REMARKS_2);
			}
			if (REMARKS_3 != null) {
				key.append(REMARKS_3);
			}
			if (TRAN_ID != null) {
				key.append(TRAN_ID);
			}
			key.append(BJP_KEY);
			log.info("BJP TRAN_DIGEST=" + TRAN_DIGEST);
			log.info("BJP key=" + key.toString());
			String certificationKey = makeMessageDigest(key.toString());
			log.info("BJP keymd5=" + certificationKey);
			AmsDeposit dep = depositManager.getBjpDeposit(depositId);
			
			if (!certificationKey.equals(TRAN_DIGEST)) {
				log.error("TRAN_DIGEST = " + TRAN_DIGEST + "not equal KEY CERTIFICATE=" + certificationKey);
				
				if (dep == null) {
					setMsgCode(ITrsConstants.MSG_NAB073);
					return SUCCESS;
				} else {
					if (6 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_TRS_NAF_0002);
						return SUCCESS;
					} else if (1 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB067);
						return SUCCESS;
					} else if (2 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB068);
						return SUCCESS;
					} else if (7 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB073);
						return SUCCESS;
					}
				}
				setMsgCode(ITrsConstants.MSG_NAB073);
				return SUCCESS;
			}
			
			Integer receivedDepositStatus = getReceivedDepositStatusWhenBjpReturnError(TRAN_REASON_CODE);
			
			if(dep.getStatus() != null && dep.getStatus().intValue() != ITrsConstants.BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS){
				FrontUserOnline frontUserOnline = FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline();
				depositManager.sendMailAbnormalUpdateDepositStatus(frontUserOnline.getUserId(), frontUserOnline.getFullName(), depositId, getDepositStatusText(dep.getStatus()), getDepositStatusText(receivedDepositStatus));
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				return SUCCESS;
			}
			
			if(BJP_CONFIG.DEPOSIT_STATUS_CANCEL == receivedDepositStatus.intValue()){
				setMsgCode(ITrsConstants.MSG_NAB068);
			}else{
				setMsgCode(ITrsConstants.MSG_NAB073);
			}
			
			depositManager.updateBjpDepositFail(dep, receivedDepositStatus, TRAN_REASON_CODE);
			depositManager.sendMailBjpDeposit(dep, receivedDepositStatus, getText("bjp.deposit.method.name"));
			
//			dep = depositManager.getBjpDeposit(depositId);
			
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return SUCCESS;
		}
	}

	private String getDepositStatusText(Integer status) {
		return getText("nts.ams.fe.label.deposit.bjp.status."+status);
	}

	private Integer getReceivedDepositStatusWhenBjpReturnError(String transactionReasonCode) {
		if (BJP_CONFIG.TRAN_REASON_CODE_15.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_16.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_21.equals(transactionReasonCode)
				|| BJP_CONFIG.TRAN_REASON_CODE_29.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_32.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_39.equals(transactionReasonCode)
				|| BJP_CONFIG.TRAN_REASON_CODE_83.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_92.equals(transactionReasonCode) || BJP_CONFIG.TRAN_REASON_CODE_93.equals(transactionReasonCode)
				|| BJP_CONFIG.TRAN_REASON_CODE_99.equals(transactionReasonCode)) {
			return BJP_CONFIG.DEPOSIT_STATUS_CANCEL;
		} else {
			return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
		}
	}

	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */
	public String depositBjpSuccessFull() {
		try {
			try {
				log.info("BJP RESPONE CharacterEncoding =" + httpRequest.getCharacterEncoding());
				httpRequest.setCharacterEncoding("Shift-JIS");
			} catch (Exception e1) {
			}
			String PORTAL_CODE = httpRequest.getParameter("PORTAL_CODE");
			String SHOP_CODE = httpRequest.getParameter("SHOP_CODE");
			String BANK_CODE = httpRequest.getParameter("BANK_CODE");
			String KESSAI_FLAG = httpRequest.getParameter("KESSAI_FLAG");
			String CTRL_NO = httpRequest.getParameter("CTRL_NO");
			String TRAN_STAT = httpRequest.getParameter("TRAN_STAT");
			String TRAN_REASON_CODE = httpRequest.getParameter("TRAN_REASON_CODE");
			String TRAN_RESULT_MSG = httpRequest.getParameter("TRAN_RESULT_MSG");

			String TRAN_DATE = httpRequest.getParameter("TRAN_DATE");
			String TRAN_TIME = httpRequest.getParameter("TRAN_TIME");
			String CUST_NAME = httpRequest.getParameter("CUST_NAME");
			String CUST_NAME_1 = httpRequest.getParameter("CUST_NAME_1");
			String CUST_NAME_2 = httpRequest.getParameter("CUST_NAME_2");
			String CUST_LNAME = httpRequest.getParameter("CUST_LNAME");
			String CUST_FNAME = httpRequest.getParameter("CUST_FNAME");
			String TRAN_AMOUNT = httpRequest.getParameter("TRAN_AMOUNT");
			String TRAN_FEE = httpRequest.getParameter("TRAN_FEE");
			String PAYMENT_DAY = httpRequest.getParameter("PAYMENT_DAY");
			String GOODS_NAME = httpRequest.getParameter("GOODS_NAME");
			String REMARKS_1 = httpRequest.getParameter("REMARKS_1");
			String REMARKS_2 = httpRequest.getParameter("REMARKS_2");
			String REMARKS_3 = httpRequest.getParameter("REMARKS_3");
			String TRAN_ID = httpRequest.getParameter("TRAN_ID");
			String TRAN_DIGEST = httpRequest.getParameter("TRAN_DIGEST");
			String BANK_CUST_NAME = httpRequest.getParameter("BANK_CUST_NAME");
			String MEIGI_STAT = httpRequest.getParameter("MEIGI_STAT");
			String CONFIRM_TIME = httpRequest.getParameter("CONFIRM_TIME");
			String OPTRAN_DIGEST = httpRequest.getParameter("OPTRAN_DIGEST");
			String depositId = httpRequest.getParameter("CTRL_NO");

			StringBuilder contentResponse = new StringBuilder();
			contentResponse.append("PORTAL_CODE=" + PORTAL_CODE);
			contentResponse.append("|SHOP_CODE=" + SHOP_CODE);
			contentResponse.append("|BANK_CODE=" + BANK_CODE);
			contentResponse.append("|KESSAI_FLAG=" + KESSAI_FLAG);
			contentResponse.append("|CTRL_NO=" + CTRL_NO);
			contentResponse.append("|TRAN_STAT=" + TRAN_STAT);
			contentResponse.append("|TRAN_REASON_CODE=" + TRAN_REASON_CODE);
			contentResponse.append("|TRAN_RESULT_MSG=" + TRAN_RESULT_MSG);
			contentResponse.append("|TRAN_DATE=" + TRAN_DATE);
			contentResponse.append("|TRAN_TIME=" + TRAN_TIME);
			contentResponse.append("|CUST_NAME=" + CUST_NAME);
			contentResponse.append("|CUST_NAME_1=" + CUST_NAME_1);
			contentResponse.append("|CUST_NAME_2=" + CUST_NAME_2);
			contentResponse.append("|CUST_LNAME=" + CUST_LNAME);
			contentResponse.append("|CUST_FNAME=" + CUST_FNAME);
			contentResponse.append("|TRAN_AMOUNT=" + TRAN_AMOUNT);
			contentResponse.append("|TRAN_FEE=" + TRAN_FEE);
			contentResponse.append("|PAYMENT_DAY=" + PAYMENT_DAY);
			contentResponse.append("|GOODS_NAME=" + GOODS_NAME);
			contentResponse.append("|REMARKS_1=" + REMARKS_1);
			contentResponse.append("|REMARKS_2=" + REMARKS_2);
			contentResponse.append("|REMARKS_3=" + REMARKS_3);
			contentResponse.append("|TRAN_ID=" + TRAN_ID);
			contentResponse.append("|TRAN_DIGEST=" + TRAN_DIGEST);
			contentResponse.append("|BANK_CUST_NAME=" + BANK_CUST_NAME);
			contentResponse.append("|MEIGI_STAT=" + MEIGI_STAT);
			contentResponse.append("|CONFIRM_TIME=" + CONFIRM_TIME);
			contentResponse.append("|OPTRAN_DIGEST=" + OPTRAN_DIGEST);
			log.debug("BJP SUCCCESS RESPONSE =" + contentResponse.toString());
			System.out.println(contentResponse);
			index();
			String BJP_KEY = accountManager.getBjpCertificationKey(TRS_CONSTANT.TRS_WL_CODE);
			StringBuilder key = new StringBuilder();
			if (PORTAL_CODE != null) {
				key.append(PORTAL_CODE);
			}
			if (SHOP_CODE != null) {
				key.append(SHOP_CODE);
			}
			if (BANK_CODE != null) {
				key.append(BANK_CODE);
			}
			if (KESSAI_FLAG != null) {
				key.append(KESSAI_FLAG);
			}
			if (CTRL_NO != null) {
				key.append(CTRL_NO);
			}
			if (TRAN_STAT != null) {
				key.append(TRAN_STAT);
			}
			if (TRAN_REASON_CODE != null) {
				key.append(TRAN_REASON_CODE);
			}
			if (TRAN_RESULT_MSG != null) {
				key.append(TRAN_RESULT_MSG);
			}
			if (TRAN_DATE != null) {
				key.append(TRAN_DATE);
			}
			if (TRAN_TIME != null) {
				key.append(TRAN_TIME);
			}
			if (CUST_NAME != null) {
				key.append(CUST_NAME);
			}
			if (CUST_LNAME != null) {
				key.append(CUST_LNAME);
			}
			if (CUST_FNAME != null) {
				key.append(CUST_FNAME);
			}
			if (TRAN_AMOUNT != null) {
				key.append(TRAN_AMOUNT);
			}
			if (TRAN_FEE != null) {
				key.append(TRAN_FEE);
			}
			if (PAYMENT_DAY != null) {
				key.append(PAYMENT_DAY);
			}
			if (GOODS_NAME != null) {
				key.append(GOODS_NAME);
			}
			if (REMARKS_1 != null) {
				key.append(REMARKS_1);
			}
			if (REMARKS_2 != null) {
				key.append(REMARKS_2);
			}
			if (REMARKS_3 != null) {
				key.append(REMARKS_3);
			}
			if (TRAN_ID != null) {
				key.append(TRAN_ID);
			}
			key.append(BJP_KEY);

			log.info("BJP TRAN_DIGEST=" + TRAN_DIGEST);
			log.info("BJP key=" + key.toString());
			String certificationKey = makeMessageDigest(key.toString());
			log.info("BJP keymd5=" + certificationKey);

			AmsDeposit dep = depositManager.getBjpDeposit(depositId);
			
			if (!certificationKey.equals(TRAN_DIGEST)) {
				log.error("TRAN_DIGEST = " + TRAN_DIGEST + "not equal KEY CERTIFICATE=" + certificationKey);				
				if (dep == null) {
					setMsgCode(ITrsConstants.MSG_NAB073);
					return SUCCESS;
				} else {
					if (6 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_TRS_NAF_0002);
						return SUCCESS;
					} else if (1 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB067);
						return SUCCESS;
					} else if (2 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB068);
						return SUCCESS;
					} else if (7 == dep.getStatus().intValue()) {
						setMsgCode(ITrsConstants.MSG_NAB073);
						return SUCCESS;
					}
				}
				setMsgCode(ITrsConstants.MSG_NAB073);
				return SUCCESS;
			}
			
			if(dep.getStatus() != null && dep.getStatus().intValue() != ITrsConstants.BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS){
				FrontUserOnline frontUserOnline = FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline();
				Integer receivedDepositStatus =  getReceivedDepositStatusWhenBjpReturnSuccess( TRAN_STAT, TRAN_REASON_CODE, MEIGI_STAT, BANK_CODE);
				depositManager.sendMailAbnormalUpdateDepositStatus(frontUserOnline.getUserId(), frontUserOnline.getFullName(), depositId, getDepositStatusText(dep.getStatus()), getDepositStatusText(receivedDepositStatus));
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				return SUCCESS;
			}
			
			// check same start
			// CustomerInfo cusInf =
			// accountManager.getCustomerInfo(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
			// String CUST_NAME_CK= getCookie("CUST_NAME_CK");
			// String CUST_LNAME_CK= getCookie("CUST_LNAME_CK");
			// String CUST_FNAME_CK= getCookie("CUST_FNAME_CK");
			String PORTAL_CODE_CK = getCookie("PORTAL_CODE_CK");
			String SHOP_CODE_CK = getCookie("SHOP_CODE_CK");
			String BANK_CODE_CK = getCookie("BANK_CODE_CK");
			String KESSAI_FLAG_CK = getCookie("KESSAI_FLAG_CK");
			String CTRL_NO_CK = getCookie("CTRL_NO_CK");
			String TRAN_AMOUNT_CK = getCookie("TRAN_AMOUNT_CK");
			String CUST_NAME_CK = "";
			LOG.info(new StringBuilder("Data from cookie for similarity checking - ").append("PORTAL_CODE_CK: " ).append(PORTAL_CODE_CK).append(", SHOP_CODE_CK: ").append(SHOP_CODE_CK).append(", BANK_CODE_CK: ")
						.append(BANK_CODE_CK).append(", KESSAI_FLAG_CK: ").append(KESSAI_FLAG_CK).append(", CTRL_NO_CK: ").append(CTRL_NO_CK).append(", TRAN_AMOUNT_CK: ").append(TRAN_AMOUNT_CK).toString());
			try {
				CustomerInfo custInfo = accountManager.getCustomerInfo(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
				if (custInfo.getCorporationType() == 1) {
					CUST_NAME_CK = custInfo.getCorpFullnameKana();
				} else {
					CUST_NAME_CK = custInfo.getFirstNameKana() + " " + custInfo.getLastNameKana();
				}
				log.info("CUST_NAME=" + CUST_NAME);
				log.info("CUST_NAME_CK=" + CUST_NAME_CK);
				if (CUST_NAME.equalsIgnoreCase(CUST_NAME_CK)) {
					log.info("same name");
				} else {

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			boolean same = true;
			if (!PORTAL_CODE.equals(PORTAL_CODE_CK)) {
				same = false;
			}
			if (!BANK_CODE.equals(BANK_CODE_CK)) {
				same = false;
			}
			if (!SHOP_CODE.equals(SHOP_CODE_CK)) {
				same = false;
			}
			if (!KESSAI_FLAG.equals(KESSAI_FLAG_CK)) {
				same = false;
			}
			if (!CTRL_NO.equals(CTRL_NO_CK)) {
				same = false;
			}
			if (MathUtil.parseBigDecimal(TRAN_AMOUNT).compareTo(MathUtil.parseBigDecimal(TRAN_AMOUNT_CK)) != 0) {
				same = false;
			}
			// if(!CUST_NAME.equalsIgnoreCase(CUST_NAME_CK)){
			// same=false;
			// }
			// if(!CUST_LNAME.equalsIgnoreCase(CUST_LNAME_CK)){
			// same=false;
			// }
			// if(!CUST_FNAME.equalsIgnoreCase(CUST_FNAME_CK)){
			// same=false;
			// }
			if (same == false) {
				log.error("BJP RETURN NOT SAME VALUE");
				setMsgCode(ITrsConstants.MSG_NAB073);
				return SUCCESS;
			}
			// check same end
			Double amt = 0d;
			try {
				amt = Double.parseDouble(TRAN_AMOUNT);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Double fee = 0d;
			try {
				fee = Double.parseDouble(TRAN_FEE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dep = depositManager.getBjpDeposit(depositId);
			if (dep == null) {
				setMsgCode(ITrsConstants.MSG_NAB073);
				return SUCCESS;
			} else {
				if (!dep.getAmsCustomer().getCustomerId().equalsIgnoreCase(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId())) {
					setMsgCode(ITrsConstants.MSG_NAB073);
					log.warn("Error in bjp  deposit customer not exist ");
					return SUCCESS;
				} else {
					if (BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS != dep.getStatus().intValue()) {
						log.warn("Error in bjp  deposit reponse with depositIdstatus " + dep.getStatus());
						setMsgCode(ITrsConstants.MSG_TRS_NAF_0058);
//						depositManager.sendMailBjpDeposit(dep, dep.getStatus().intValue() , getText("bjp.deposit.method.name"));
						return SUCCESS;
					}
				}
			}
			int step = 0;
			if (BJP_CONFIG.DEPOSIT_TRAN_STAT_SUCCESS.equalsIgnoreCase(TRAN_STAT) && BJP_CONFIG.TRAN_REASON_CODE_00.equalsIgnoreCase(TRAN_REASON_CODE)) {
				if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equals(MEIGI_STAT)) {
					// run step sucess
					step = 1;
				} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equals(MEIGI_STAT)) {
					// update equal 2 and show msg fail
					step = 2;

				} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equals(MEIGI_STAT)) {
					if ("0033".equals(BANK_CODE) || "0036".equals(BANK_CODE) || "0038".equals(BANK_CODE) || "0003".equals(BANK_CODE)) {
						setMsgCode(ITrsConstants.MSG_TRS_NAF_0002);
						return SUCCESS;
					} else {
						// run step sucess
						step = 1;
					}
				}
			} else {
				// fail
				step = 3;
			}

			if (step == 3) {
				if (BJP_CONFIG.TRAN_REASON_CODE_15.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_16.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_21.equals(TRAN_REASON_CODE)
						|| BJP_CONFIG.TRAN_REASON_CODE_29.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_32.equals(TRAN_REASON_CODE)
						|| BJP_CONFIG.TRAN_REASON_CODE_39.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_83.equals(TRAN_REASON_CODE)
						|| BJP_CONFIG.TRAN_REASON_CODE_92.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_93.equals(TRAN_REASON_CODE)
						|| BJP_CONFIG.TRAN_REASON_CODE_99.equals(TRAN_REASON_CODE)) {
					depositManager.updateBjpDepositFail(dep, BJP_CONFIG.DEPOSIT_STATUS_CANCEL, TRAN_REASON_CODE);
					setMsgCode(ITrsConstants.MSG_NAB068);
				} else {
					depositManager.updateBjpDepositFail(dep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, TRAN_REASON_CODE);
					setMsgCode(ITrsConstants.MSG_NAB073);
				}
				dep = depositManager.getBjpDeposit(depositId);
				if (dep != null) {
					if (BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS == dep.getStatus().intValue()) {
						// (Inprogress)Display alert message: MSG_TRS_NAF_0002
						setMsgCode(ITrsConstants.MSG_TRS_NAF_0002);
						return SUCCESS;
					} else if (BJP_CONFIG.DEPOSIT_STATUS_SUCCESS == dep.getStatus().intValue()) {
						// If AMS_DEPOSIT.STATUS is 1 (Finish) Display alert
						// message : MSG_NAB067
						setMsgCode(ITrsConstants.MSG_NAB067);
						try {
							depositManager.sendMailBjpDeposit(dep, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, getText("bjp.deposit.method.name"));
						} catch (Exception e) {
							log.error(e.getMessage());
						}
						return SUCCESS;
					} else if (BJP_CONFIG.DEPOSIT_STATUS_FAIL == dep.getStatus().intValue()) {
						// 2 fail
						try {
							depositManager.sendMailBjpDeposit(dep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, getText("bjp.deposit.method.name"));
						} catch (Exception e) {
							log.error(e.getMessage());
						}
						return SUCCESS;
					} else if (BJP_CONFIG.DEPOSIT_STATUS_CANCEL == dep.getStatus().intValue()) {
						// 7 cancel
						try {
							depositManager.sendMailBjpDeposit(dep, BJP_CONFIG.DEPOSIT_STATUS_CANCEL, getText("bjp.deposit.method.name"));
						} catch (Exception e) {
							log.error(e.getMessage());
						}
						return SUCCESS;
					}
				} else {
					// not exist deposit set fail
					setMsgCode(ITrsConstants.MSG_NAB073);
				}
				return SUCCESS;
			} else if (step == 1) {
				AmsCashflow cashflow = new AmsCashflow();
				AmsCustomer cus = new AmsCustomer();
				cus.setCustomerId(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId());
				cashflow.setAmsCustomer(cus);
				cashflow.setCashflowType(1);
				cashflow.setCashflowAmount(amt);
				cashflow.setCurrencyCode(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getCurrencyCode());
				cashflow.setRate(1d);
				cashflow.setSourceType(1);
				if (depositId.isEmpty()) {
					depositId = REMARKS_3;
				}
				cashflow.setSourceId(depositId);
				cashflow.setActiveFlg(1);
				cashflow.setServiceType(0);
				depositManager.insertBjpCashFlow(cashflow);
				depositManager.updateBjpCashBalance(FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline().getUserId(), FrontUserOnlineContext.getFrontUserOnline().getFrontUserOnline()
						.getCurrencyCode(), 0, amt, fee);
				depositManager.updateBjpDeposit(depositId, TRAN_REASON_CODE, Math.abs(fee));
				depositManager.updateBjpDepositRef(depositId, TRAN_ID, null);
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
				try {
					depositManager.sendMailBjpDeposit(dep, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, getText("bjp.deposit.method.name"));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return SUCCESS;
			} else if (step == 2) {
				depositManager.updateBjpDepositFail(dep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, TRAN_REASON_CODE);
				setMsgCode(ITrsConstants.MSG_NAB073);
				try {
					depositManager.sendMailBjpDeposit(dep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, getText("bjp.deposit.method.name"));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return SUCCESS;
			}
			return SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			return SUCCESS;
		}
	}

	private Integer getReceivedDepositStatusWhenBjpReturnSuccess(String TRAN_STAT, String TRAN_REASON_CODE, String MEIGI_STAT, String BANK_CODE){
		if (BJP_CONFIG.DEPOSIT_TRAN_STAT_SUCCESS.equalsIgnoreCase(TRAN_STAT) && BJP_CONFIG.TRAN_REASON_CODE_00.equalsIgnoreCase(TRAN_REASON_CODE)) {
		if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equals(MEIGI_STAT)) {
			return BJP_CONFIG.DEPOSIT_STATUS_SUCCESS;
		} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equals(MEIGI_STAT)) {
			return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
		} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equals(MEIGI_STAT)) {
			if ("0033".equals(BANK_CODE) || "0036".equals(BANK_CODE) || "0038".equals(BANK_CODE) || "0003".equals(BANK_CODE)) {
                    return BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS;
                } else {
                    // run step success
                    return BJP_CONFIG.DEPOSIT_STATUS_SUCCESS;
                }
            }
        } else {
            if (BJP_CONFIG.TRAN_REASON_CODE_15.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_16.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_21.equals(TRAN_REASON_CODE)
                    || BJP_CONFIG.TRAN_REASON_CODE_29.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_32.equals(TRAN_REASON_CODE)
                    || BJP_CONFIG.TRAN_REASON_CODE_39.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_83.equals(TRAN_REASON_CODE)
                    || BJP_CONFIG.TRAN_REASON_CODE_92.equals(TRAN_REASON_CODE) || BJP_CONFIG.TRAN_REASON_CODE_93.equals(TRAN_REASON_CODE)
                    || BJP_CONFIG.TRAN_REASON_CODE_99.equals(TRAN_REASON_CODE)) {
                return BJP_CONFIG.DEPOSIT_STATUS_CANCEL;
            } else {
                return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
            }
        }
        return BJP_CONFIG.DEPOSIT_STATUS_FAIL;
    }

	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */
	public String makeMessageDigest(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		byte[] bytes;
		bytes = data.getBytes("Shift_Jis");
		messagedigest.update(bytes);
		bytes = messagedigest.digest();
		String HexVal = "";
		String messageDigest = "";
		for (int i = 0; i < bytes.length; i++) {
			HexVal = Integer.toHexString(0xff & bytes[i]);
			if (HexVal.length() == 1) {
				messageDigest += "0";
				messageDigest += HexVal;
			} else {
				messageDigest += HexVal;
			}
		}

		return messageDigest;
	}

	/**
	 * TODO　
	 * 
	 * @version TRS1.0
	 * @param
	 * @return
	 * @throws
	 * @author tungpv
	 * @CrDate May 23, 2013
	 */

	public String getCookie(String name) {
		HttpServletRequest request = httpRequest;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String value = null;
		try {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals(name)) {
					value = c.getValue();
				}
			}
		} catch (Exception e) {
			LOG.info("cant get cookie");
			log.error(e.getMessage(), e);
		}
		return value;
	}

	public String depositSubmit() {
		log.info("[start] depositSubmit");
		try {
			readInformationUserOnline();
			getDepositHisory();
			DepositInfo depositInfo = depositModel.getDepositInfo();
			String customerId = "";
			String wlCode = "";
			String currencyCode = "";
			String publicKey = "";
			Integer deviceType = new Integer(0);
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					depositInfo.setDepositRoute(frontUserOnline.getDeviceType());
					customerId = frontUserOnline.getUserId();
					wlCode = frontUserOnline.getWlCode();
					currencyCode = frontUserOnline.getCurrencyCode();
					deviceType = frontUserOnline.getDeviceType();
					publicKey = frontUserOnline.getPublicKey();
				}
			}
			// get sub group of FX
			Integer subGroupId = accountManager.getSubGroupId(customerId, depositInfo.getServiceType());
			if (IConstants.PAYMENT_METHOD.NETELLER == depositInfo.getMethod()) {
				log.info("[NETELLER] = depositInfo.getMethod()");
				// if method is neteller
				// if customer input new account
				NetellerInfo netellerInfo = depositModel.getNetellerInfo();
				if (netellerInfo != null) {
					netellerInfo.setWlCode(wlCode);
					netellerInfo.setCustomerId(customerId);
					netellerInfo.setBaseCurrency(currencyCode);
					netellerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					netellerInfo.setServiceType(depositInfo.getServiceType());
					netellerInfo.setDeviceType(deviceType);
					netellerInfo.setSubGroupId(subGroupId);
					// deposit via neteller
					log.info("[start] deposit via neteller");
					Integer result = depositManager.depositNeteller(netellerInfo, publicKey);
					if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
						// if deposit neteller is failure
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
						// if deposit neteller is success
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
						// if deposit is error on mt4
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_MT4_ERROR);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.CANCEL.equals(result)) {
						// if deposit is error on mt4
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_CANCEL);
					}
					log.info("[end] deposit via neteller");
				}

			} else if (IConstants.PAYMENT_METHOD.PAYZA == depositInfo.getMethod()) {
				log.info("[PAYZA] = depositInfo.getMethod()");
				// if method is payza
				PayzaInfo payzaInfo = depositModel.getPayzaInfo();
				if (payzaInfo != null) {
					payzaInfo.setWlCode(wlCode);
					payzaInfo.setCustomerId(customerId);
					payzaInfo.setBaseCurrency(currencyCode);
					payzaInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.PAYZA);
					payzaInfo.setServiceType(depositInfo.getServiceType());
					payzaInfo.setDeviceType(deviceType);
					payzaInfo.setSubGroupId(subGroupId);
					// deposit via payza
					log.info("[start] deposit via payza");
					Integer result = depositManager.depositPayza(payzaInfo);
					if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
						// if deposit payza is failure
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
						// if deposit payza is success
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
						// if deposit is error on mt4
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_MT4_ERROR);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.CANCEL.equals(result)) {
						// if deposit is error on mt4
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_CANCEL);
					}
					log.info("[end] deposit via payza");
				}
			} else if (IConstants.PAYMENT_METHOD.BANK_TRANSFER == depositInfo.getMethod()) {
				log.info("[BANK_TRANSFER] = depositInfo.getMethod()");
				BankTransferInfo bankTransferInfo = depositModel.getBankTransferInfo();
				if (bankTransferInfo == null) {
					bankTransferInfo = new BankTransferInfo();
				}

				bankTransferInfo.setWlCode(wlCode);
				bankTransferInfo.setCustomerId(customerId);
				bankTransferInfo.setBaseCurrency(currencyCode);
				bankTransferInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
				bankTransferInfo.setServiceType(depositInfo.getServiceType());
				bankTransferInfo.setDeviceType(deviceType);
				bankTransferInfo.setSubGroupId(subGroupId);
				Integer result = depositManager.depositBankTransfer(bankTransferInfo);
				if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// if deposit banktransfer is failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// if deposit banktransfer is success
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_REQUESTING_SUCCESS);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE.equals(result)) {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE);
				}

			} else if (IConstants.PAYMENT_METHOD.EXCHANGER == depositInfo.getMethod()) {
				log.info("[EXCHANGER] = depositInfo.getMethod()");
				ExchangerInfo exchangerInfo = depositModel.getExchangerInfo();
				if (exchangerInfo == null) {
					exchangerInfo = new ExchangerInfo();
				}

				exchangerInfo.setWlCode(wlCode);
				exchangerInfo.setCustomerId(customerId);
				exchangerInfo.setCurrencyCode(currencyCode);
				exchangerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.EXCHANGER);
				exchangerInfo.setServiceType(depositInfo.getServiceType());
				exchangerInfo.setDeviceType(deviceType);
				exchangerInfo.setSubGroupId(subGroupId);

				Integer result = depositManager.depositExchanger(exchangerInfo);
				if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// if deposit banktransfer is failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// if deposit banktransfer is success
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_REQUESTING_SUCCESS);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.NO_CONVERT_RATE.equals(result)) {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE);
				}
			} else if (IConstants.PAYMENT_METHOD.LIBERTY == depositInfo.getMethod()) {
				log.info("[LIBERTY] = depositInfo.getMethod()");

				try {
					LibertyInfo libertyInfo = depositModel.getLibertyInfo();
					if (libertyInfo == null) {
						libertyInfo = new LibertyInfo();
					}
					libertyInfo.setWlCode(wlCode);
					libertyInfo.setCustomerId(customerId);
					libertyInfo.setBaseCurrency(currencyCode);
					libertyInfo.setCurrencyCode(currencyCode);
					libertyInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.LIBERTY);
					libertyInfo.setSubGroupId(subGroupId);
					libertyInfo.setServiceType(depositInfo.getServiceType());
					libertyInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));

					// get and set parameters for liberty url to redirect
					String url = depositManager.depositLibertySCI(libertyInfo);
					setRawUrl(url);
					return IConstants.LIBERTY_PAYMENT.SCI_URL;

				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			} else if (IConstants.PAYMENT_METHOD.CREDIT_CARD == depositInfo.getMethod()) {
				log.info("[NETELLER] = depositInfo.getMethod()");
				Integer paymentGateway = depositModel.getPaymentGateway();
				if (paymentGateway == null) {
					paymentGateway = IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.PAYONLINE;
				}

				if (IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY.equals(paymentGateway)) {
					CreditCardInfo creditCardInfo = depositModel.getCreditCardInfo();
					Integer cardType = creditCardInfo.getCcType();

					String amount = depositInfo.getAmount();
					creditCardInfo.setAmount(MathUtil.parseBigDecimal(amount));
					creditCardInfo.setCustomerId(customerId);
					creditCardInfo.setServiceType(depositInfo.getServiceType());
					creditCardInfo.setWlCode(wlCode);

					String depositId = depositManager.depositNetpay(creditCardInfo);

					if (cardType != null && cardType.intValue() != IConstants.CREDIT_CARD_PAYMENT.CARD_TYPE.CUP) {
						Integer rs = 1;
						if (depositId != null) {
							rs = depositManager.depositNetpaySilentPost(depositId, customerId, creditCardInfo);
						} else {
							setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE);
							return IConstants.CREDIT_CARD_PAYMENT_URL.NETPAY;
						}

						if (rs == 0) {
							setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
							Integer result = depositManager.depositNetpaySuccess("", depositId, customerId, wlCode, currencyCode, IConstants.DEPOSIT_STATUS.FINISHED);

							if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
								// if update successful
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
							} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
								// if update deposit success but don't success
								// on mt4
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
							} else if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
								// if update failure
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
							} else if (IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED.equals(result)) {
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
							} else {
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
							}
							return SUCCESS;
						} else {
							setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
							return SUCCESS;
						}
					} else {
						Map<String, String> mapPayonlineConfig = SystemPropertyConfig.getInstance().getMap(
								IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + creditCardInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.NETPAY_CONFIG);
						String redirectUrl = mapPayonlineConfig.get(IConstants.NETPAY.CONFIG.PATH) + mapPayonlineConfig.get(IConstants.NETPAY.CONFIG.SUCCESS_URL);

						RateInfo rateInfoCny = depositModel.getRateInfoCny();
						if (rateInfoCny == null) {
							rateInfoCny = depositManager.getLastestRate(currencyCode + IConstants.CURRENCY_CODE.CNY);
							if (rateInfoCny == null) {
								setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE);
								return SUCCESS;
							}
						}

						BigDecimal fromAmount = MathUtil.parseBigDecimal(amount);
						BigDecimal toAmount = fromAmount.multiply(rateInfoCny.getRate());

						Integer scale = new Integer(0);
						Integer rounding = new Integer(0);
						CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + IConstants.CURRENCY_CODE.CNY);
						if (currencyInfo != null) {
							scale = currencyInfo.getCurrencyDecimal();
							rounding = currencyInfo.getCurrencyRound();
						} else {
							scale = 2;
							rounding = 2;
						}

						String strToAmount = String.valueOf(MathUtil.parseBigDecimal(String.valueOf(toAmount)).divide(MathUtil.parseBigDecimal(1), scale, rounding));

						Map<String, String> mapNetpayConfig = SystemPropertyConfig.getInstance().getMap(
								IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + creditCardInfo.getWlCode() + "_" + IConstants.SYS_PROPERTY.NETPAY_CONFIG);

						String url = NetPay.getInstance().hostedLink(mapNetpayConfig, strToAmount, IConstants.CURRENCY_CODE.CNY, redirectUrl, depositId, depositId);
						log.info(url);

						Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
						if (mapDeposit == null) {
							mapDeposit = new HashMap<String, String>();
							FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
						}

						synchronized (mapDeposit) {
							String transaction_encrypted_code = mapDeposit.get(depositId);
							if (transaction_encrypted_code == null) {
								mapDeposit.put(depositId, depositId);
							}
						}

						setRawUrl(url);
						return IConstants.CREDIT_CARD_PAYMENT_URL.NETPAY;
					}
					// return IConstants.CREDIT_CARD_PAYMENT_URL.NETPAY;
				} else {
					// if customer choose payonline
					CreditCardInfo creditCardInfo = depositModel.getCreditCardInfo();
					// in nfx because payonline don't accept other symbol except
					// USD so system will convert to USD
					// depositInfo.amount: amount will insert to db
					// creditCardInfo.amount: amount will pay to payonline
					if (creditCardInfo != null) {
						creditCardInfo.setWlCode(wlCode);
						creditCardInfo.setCustomerId(customerId);
						creditCardInfo.setBaseCurrency(currencyCode);
						creditCardInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
						creditCardInfo.setServiceType(depositInfo.getServiceType());
						BigDecimal amount = MathUtil.parseBigDecimal(depositInfo.getAmount());
						// [TDSBO1.0-Administrator]Oct 26, 2012A - Start -
						// Calculate Converted Amount
						// Map<String, String> mapRateConfig =
						// SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY
						// + IConstants.SYS_PROPERTY.MT_REPORT_CONFIG);
						// RateInfo rateInfo =
						// MT4Manager.getInstance().getRate(currencyCode,
						// mapRateConfig);
						RateInfo rateInfo = depositManager.getRateInfo(currencyCode, IConstants.CURRENCY_CODE.USD);
						if (rateInfo != null) {
							if (IConstants.RATE_TYPE.ASK.equals(rateInfo.getRateType())) {
								BigDecimal creditAmount = amount.divide(rateInfo.getRate(), 2, BigDecimal.ROUND_HALF_UP);
								creditCardInfo.setAmount(creditAmount);
							} else {
								BigDecimal creditAmount = amount.multiply(rateInfo.getRate());
								creditCardInfo.setAmount(depositManager.rounding(creditAmount, IConstants.CURRENCY_CODE.USD));
							}
						}
						log.info("Amount = " + amount + ", Converted Amount = " + creditCardInfo.getAmount());

						// [TDSBO1.0-Administrator]Oct 26, 2012A - End
						creditCardInfo.setDeviceType(deviceType);
						creditCardInfo.setSubGroupId(subGroupId);

						String url = depositManager.depositPayonlineSystem(creditCardInfo, amount, publicKey);
						if (IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE.equals(url)) {
							setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE);
							return ERROR;
						} else {
							setRawUrl(url);
						}

						return IConstants.CREDIT_CARD_PAYMENT_URL.PAYONLINE;
					}
				}
			}
		} catch (Exception e) {
			setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			log.error(e.getMessage(), e);
			result = ERROR;
		}

		log.info("[start] depositSubmit");
		return SUCCESS;

	}

	private void readPaymentInformation() {

		DepositInfo depositInfo = depositModel.getDepositInfo();
		String customerId = "";
		String wlCode = "";
		String currencyCode = "";
		String publicKey = "";
		Integer deviceType = IConstants.DEVICE_TYPE.PC;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				depositInfo.setDepositRoute(frontUserOnline.getDeviceType());
				customerId = frontUserOnline.getUserId();
				wlCode = frontUserOnline.getWlCode();
				currencyCode = frontUserOnline.getCurrencyCode();
				deviceType = frontUserOnline.getDeviceType();
				publicKey = frontUserOnline.getPublicKey();
			}
		}
		if (IConstants.PAYMENT_METHOD.NETELLER == depositInfo.getMethod()) {
			// if method is neteller
			String rndNeteller = depositModel.getRdNetteller();
			if (IConstants.DEPOSIT_CHOOSE_RADIO.NETELLER.equals(rndNeteller)) {
				// if customer input new account
				NetellerInfo netellerInfo = depositModel.getNetellerInfo();
				if (netellerInfo != null) {
					netellerInfo.setWlCode(wlCode);
					netellerInfo.setCustomerId(customerId);
					netellerInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
					netellerInfo.setCurrencyCode(depositInfo.getCurrencyCode());
					netellerInfo.setBaseCurrency(currencyCode);
					netellerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					netellerInfo.setServiceType(depositInfo.getServiceType());

				}
			} else {
				// if customer choose on payment information
				String ewalletId = rndNeteller;
				CustomerEwalletInfo customerEwalletInfo = depositManager.getNettellerInfo(MathUtil.parseInteger(ewalletId), publicKey);
				if (customerEwalletInfo != null) {
					NetellerInfo netellerInfo = new NetellerInfo();
					netellerInfo.setAccountId(customerEwalletInfo.getEwalletAccNo());
					netellerInfo.setSecureId(customerEwalletInfo.getEwalletSecureId());
					netellerInfo.setWlCode(wlCode);
					netellerInfo.setCustomerId(customerId);
					netellerInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
					netellerInfo.setCurrencyCode(depositInfo.getCurrencyCode());
					netellerInfo.setBaseCurrency(currencyCode);
					netellerInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					netellerInfo.setServiceType(depositInfo.getServiceType());
					depositModel.setNetellerInfo(netellerInfo);
				}
			}
		} else if (IConstants.PAYMENT_METHOD.PAYZA == depositInfo.getMethod()) {
			// if method is payza
			String rndPayza = depositModel.getRdPayza();
			if (IConstants.DEPOSIT_CHOOSE_RADIO.PAYZA.equals(rndPayza)) {
				// if customer input new email and api password
				PayzaInfo payzaInfo = depositModel.getPayzaInfo();
				if (payzaInfo != null) {
					payzaInfo.setWlCode(wlCode);
					payzaInfo.setCustomerId(customerId);
					payzaInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
					payzaInfo.setCurrencyCode(depositInfo.getCurrencyCode());
					payzaInfo.setBaseCurrency(currencyCode);
					payzaInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.PAYZA);
					payzaInfo.setServiceType(depositInfo.getServiceType());
				}
			} else {
				String email = rndPayza;
				CustomerEwalletInfo customerEwalletInfo = depositManager.getPayzaInfo(customerId, email, publicKey);
				if (customerEwalletInfo != null) {
					PayzaInfo payzaInfo = new PayzaInfo();
					payzaInfo.setEmailAddress(email);
					payzaInfo.setApiPassword(customerEwalletInfo.getEwalletApiPassword());
					payzaInfo.setWlCode(wlCode);
					payzaInfo.setCustomerId(customerId);
					payzaInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
					payzaInfo.setCurrencyCode(depositInfo.getCurrencyCode());
					payzaInfo.setBaseCurrency(currencyCode);
					payzaInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.NETELLER);
					payzaInfo.setServiceType(depositInfo.getServiceType());
					depositModel.setPayzaInfo(payzaInfo);
					depositModel.setRdPayza(email);
				}
			}

		} else if (IConstants.PAYMENT_METHOD.BANK_TRANSFER == depositInfo.getMethod()) {
			// if deposit on bank transfer
			BankTransferInfo bankTransferInfo = new BankTransferInfo();
			bankTransferInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
			bankTransferInfo.setWlCode(wlCode);
			bankTransferInfo.setCustomerId(customerId);
			bankTransferInfo.setCurrencyCode(depositInfo.getCurrencyCode());
			bankTransferInfo.setBaseCurrency(currencyCode);
			bankTransferInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.BANK_TRANSFER);
			bankTransferInfo.setServiceType(depositInfo.getServiceType());
			depositModel.setBankTransferInfo(bankTransferInfo);

		} else if (IConstants.PAYMENT_METHOD.CREDIT_CARD == depositInfo.getMethod()) {
			if (IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.PAYONLINE.equals(depositModel.getPaymentGateway())) {
				// if customer choose pay via payonline
				CreditCardInfo creditCardInfo = depositModel.getCreditCardInfo();
				if (creditCardInfo == null) {
					creditCardInfo = new CreditCardInfo();
				}
				creditCardInfo.setDeviceType(deviceType);
				creditCardInfo.setWlCode(wlCode);
				creditCardInfo.setCustomerId(customerId);
				creditCardInfo.setCurrencyCode(depositInfo.getCurrencyCode());
				creditCardInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
				creditCardInfo.setServiceType(depositInfo.getServiceType());
				creditCardInfo.setPaymentGateway(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.PAYONLINE);
				Map<String, String> mapPaymentConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CREDIT_CARD_PAYMENT);
				creditCardInfo.setPaymentGatewayName(mapPaymentConfig.get(StringUtil.toString(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.PAYONLINE)));
				depositInfo.setPaymentGatewayName(creditCardInfo.getPaymentGatewayName());
				if (IConstants.CURRENCY_CODE.USD.equals(depositInfo.getCurrencyCode())) {
					creditCardInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
				} else {
					RateInfo rateInfo = depositModel.getRateInfo();
					if (rateInfo != null) {
						List<Object> listParams = new ArrayList<Object>();
						listParams.add(rateInfo.getSymbolName());
						rateInfo.setExchangeRate(getText("nts.ams.fe.label.deposit.payonline.rateof", listParams));
						creditCardInfo.setAmount(rateInfo.getTotalAfterConvert());
					}
					depositModel.setRateInfo(rateInfo);
				}
				depositModel.setCreditCardInfo(creditCardInfo);
			} else if (IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY.equals(depositModel.getPaymentGateway())) {
				CreditCardInfo creditCardInfo = depositModel.getCreditCardInfo();
				if (creditCardInfo == null) {
					creditCardInfo = new CreditCardInfo();
				}
				creditCardInfo.setWlCode(wlCode);
				creditCardInfo.setDeviceType(deviceType);
				creditCardInfo.setCustomerId(customerId);
				creditCardInfo.setCurrencyCode(depositInfo.getCurrencyCode());
				creditCardInfo.setBaseCurrency(currencyCode);
				creditCardInfo.setPaymentMethod(IConstants.PAYMENT_METHOD.CREDIT_CARD);
				creditCardInfo.setServiceType(depositInfo.getServiceType());
				creditCardInfo.setPaymentGateway(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY);
				creditCardInfo.setAmount(MathUtil.parseBigDecimal(depositInfo.getAmount()));
				Map<String, String> mapPaymentConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CREDIT_CARD_PAYMENT);
				creditCardInfo.setPaymentGatewayName(mapPaymentConfig.get(StringUtil.toString(IConstants.CREDIT_CARD_PAYMENT.PAYMENT_METHOD.NETPAY)));
				depositInfo.setPaymentGatewayName(creditCardInfo.getPaymentGatewayName());
				// depositManager.depositNetpay(creditCardInfo);

				depositModel.setCreditCardInfo(creditCardInfo);
			}
		}
	}

	/**
	 * 　 read current user is being online
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 28, 2012
	 * @MdDate
	 */
	@SuppressWarnings("unchecked")
	private void readInformationUserOnline() {
		try {
			String currency = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			Map<Integer, Boolean> mapCustomerService = null;
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					currency = frontUserOnline.getCurrencyCode();
					mapCustomerService = frontUserOnline.getMapCustomerService();
				}
			}

			log.info("[Start] info of customer deposit: ");
			log.info("[start]get list payment method from system property");

			Map<String, String> mapDepositMethod = (Map<String, String>) ObjectCopy.copy(SystemPropertyConfig.getInstance().getMap(
					IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_METHOD));

			log.info("[end]get list payment method from system property");
			log.info("[start]get list servicetype from system property");

			Map<String, String> mapServiceType = (Map<String, String>) ObjectCopy.copy(SystemPropertyConfig.getInstance().getMap(
					IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE));
			List<String> listServicesForRemove = new ArrayList<String>();
			if (mapCustomerService != null && mapCustomerService.size() > 0) {
				for (Entry<String, String> entry : mapServiceType.entrySet()) {
					Integer key = MathUtil.parseInteger(entry.getKey());
					if (IConstants.SERVICES_TYPE.AMS.equals(key))
						continue;
					if (!mapCustomerService.containsKey(key)) {
						listServicesForRemove.add(StringUtil.toString(key));
					}
				}
			}
			for (String item : listServicesForRemove) {
				mapServiceType.remove(item);
			}

			depositModel.setMapServiceType(mapServiceType);

			log.info("[end]get list payment method from system property");
			log.info("[start]get list credit card payment from system property");
			Map<String, String> mapCreditCardPayment = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.CREDIT_CARD_PAYMENT);
			depositModel.setMapCreditCardPayment(mapCreditCardPayment);
			log.info("[end]get list credit card payment from system property");
			// [NTS1.0-Quan.Le.Minh]Feb 20, 2013A - Start
			log.info("[start]get list payment system from system property");

			Map<String, String> mapPaymentSystem = null;
			if (IConstants.CURRENCY_CODE.JPY.equalsIgnoreCase(currency)) {
				mapPaymentSystem = new TreeMap<String, String>();
				mapPaymentSystem.put("3", getText("payment_system.netpay"));
			} else {
				mapPaymentSystem = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.PAYMENT_SYSTEM);
			}

			depositModel.setMapPaymentSystem(mapPaymentSystem);
			log.info("[end]get list payment system from system property");
			// [NTS1.0-Quan.Le.Minh]Feb 20, 2013A - End

			// [NTS1.0-anhndn]Feb 27, 2013A - Start
			Map<String, String> mapLibertyAccess = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.LIBERTY_ACCESS);
			depositModel.setMapLibertyAccess(mapLibertyAccess);
			// [NTS1.0-anhndn]Feb 27, 2013A - End

			log.info("[End] info of customer deposit");

			DepositInfo depositInfo = depositModel.getDepositInfo();
			String maxDeposit = "";
			String minDeposit = "";
			if (depositInfo == null) {

			} else {
				depositInfo.setMethodName(mapDepositMethod.get(StringUtil.toString(depositInfo.getMethod())));
				depositInfo.setServiceTypeName(mapServiceType.get(StringUtil.toString(depositInfo.getServiceType())));
				//
				// [NTS1.0-Quan.Le.Minh]Feb 20, 2013A - Start
				if (depositModel.getPaymentGateway() != null) {
					depositInfo.setPaymentGatewayName(mapPaymentSystem.get(depositModel.getPaymentGateway().toString()));
				}
				// [NTS1.0-Quan.Le.Minh]Feb 20, 2013A - End
				depositModel.setDepositInfo(depositInfo);
			}

			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					if (depositInfo == null) {
						depositInfo = new DepositInfo();
						depositInfo.setCurrencyCode(frontUserOnline.getCurrencyCode());
						depositInfo.setServiceType(IConstants.SERVICES_TYPE.AMS);
					}

					log.info("[start] get balance of all customer service");
					Map<String, BalanceInfo> mapBalanceInfo = new HashMap<String, BalanceInfo>();

					// CustomerServicesInfo customerServiceInfo = null;
					List<CustomerServicesInfo> listCustomerServiceInfo = frontUserOnline.getListCustomerServiceInfo();
					if (listCustomerServiceInfo != null && listCustomerServiceInfo.size() > 0) {
						for (CustomerServicesInfo item : listCustomerServiceInfo) {
							if (mapServiceType.containsKey(StringUtil.toString(item.getServiceType()))) {
								if (IConstants.SERVICES_TYPE.FX.equals(item.getServiceType())) {
									// customerServiceInfo = item;
									BalanceInfo balanceInfo = depositModel.getBalanceFxInfo();
									mapBalanceInfo.put(StringUtil.toString(item.getServiceType()), balanceInfo);
								} else if (IConstants.SERVICES_TYPE.BO.equals(item.getServiceType())) {
									BalanceInfo balanceInfo = depositModel.getBalanceBoInfo();
									mapBalanceInfo.put(StringUtil.toString(item.getServiceType()), balanceInfo);
								} else if (IConstants.SERVICES_TYPE.AMS.equals(item.getServiceType())) {
									BalanceInfo balanceInfo = depositModel.getBalanceAmsInfo();
									mapBalanceInfo.put(StringUtil.toString(item.getServiceType()), balanceInfo);
								} else if (IConstants.SERVICES_TYPE.COPY_TRADE.equals(item.getServiceType())) {
									BalanceInfo balanceInfo = depositModel.getBalanceScInfo();
									mapBalanceInfo.put(StringUtil.toString(item.getServiceType()), balanceInfo);
								}
								// BalanceInfo balanceInfo =
								// balanceManager.getBalanceInfo(item.getCustomerId(),
								// item.getCustomerServiceId(),
								// item.getServiceType(),
								// item.getCurrencyCode());
								// mapBalanceInfo.put(StringUtil.toString(item.getServiceType()),
								// balanceInfo);
							}
						}
					}
					log.info("[end] get balance of all customer service");

					// get list exchanger
					Map<String, String> mapExchanger = exchangerManager.getMapExchanger(frontUserOnline.getWlCode(), frontUserOnline.getCurrencyCode(), frontUserOnline.getUserId());
					if (mapExchanger == null || mapExchanger.size() <= 0) {
						mapExchanger = new HashMap<String, String>();
						depositModel.setMapExchanger(mapExchanger);
						mapDepositMethod.remove(String.valueOf(IConstants.PAYMENT_METHOD.EXCHANGER));
					} else {
						depositModel.setMapExchanger(mapExchanger);
					}

					if (!frontUserOnline.getCurrencyCode().equals(IConstants.CURRENCY_CODE.USD) && !frontUserOnline.getCurrencyCode().equals(IConstants.CURRENCY_CODE.EUR)) {
						mapDepositMethod.remove(String.valueOf(IConstants.PAYMENT_METHOD.LIBERTY));
					}

					// set deposit method
					depositModel.setMapDepositMethod(mapDepositMethod);
					// get base currency of ams
					depositModel.setBaseCurrencyCode(frontUserOnline.getCurrencyCode());
					log.info("customerID" + frontUserOnline.getUserId());
					log.info("login ID " + frontUserOnline.getLoginId());
					log.info("currency Code " + frontUserOnline.getCurrencyCode());
					String key = frontUserOnline.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT;
					// CustomerServicesInfo customerServiceInfo =
					// accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(),
					// IConstants.SERVICES_TYPE.FX);
					// if(customerServiceInfo != null) {

					depositInfo.setLoginId(frontUserOnline.getLoginId());
					depositInfo.setCustomerId(frontUserOnline.getUserId());
					depositInfo.setDepositRoute(frontUserOnline.getDeviceType());
					String bankInfo = depositManager.getBankInfo(frontUserOnline.getWlCode());
					depositInfo.setBankInfo(bankInfo);
					Map<String, String> mapConfiguration = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + frontUserOnline.getWlCode());
					maxDeposit = mapConfiguration.get(key);
					key = frontUserOnline.getCurrencyCode() + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT;
					minDeposit = mapConfiguration.get(key);
					depositInfo.setMaxAmount(MathUtil.parseBigDecimal(maxDeposit));
					depositInfo.setMinAmount(MathUtil.parseBigDecimal(minDeposit));

					// }
					BalanceInfo balanceInfo = balanceManager.getBalanceInfo(frontUserOnline.getUserId(), IConstants.SERVICES_TYPE.AMS, frontUserOnline.getCurrencyCode());
					mapBalanceInfo.put(StringUtil.toString(IConstants.SERVICES_TYPE.AMS), balanceInfo);
					depositModel.setMapBalanceInfo(mapBalanceInfo);

					depositModel.setBalanceInfo(balanceInfo);
				}
			}
			log.info("deposit limitation: max = " + maxDeposit + "and min = " + minDeposit);

			BigDecimal validMaxAmount = MathUtil.parseBigDecimal(maxDeposit);
			BigDecimal validMinAmount = MathUtil.parseBigDecimal(minDeposit);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + depositInfo.getCurrencyCode());
			if (currencyInfo != null) {
				validMaxAmount = validMaxAmount.divide(MathUtil.parseBigDecimal(1), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
				validMinAmount = validMinAmount.divide(MathUtil.parseBigDecimal(1), currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
			}

			depositInfo.setMaxAmount(validMaxAmount);
			depositInfo.setMinAmount(validMinAmount);
			depositModel.setDepositInfo(depositInfo);

		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 　 validate deposit form
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 28, 2012
	 * @MdDate
	 */
	private void validateForm() {
		clearFieldErrors();
		String wlCode = "";
		DepositInfo depositInfo = depositModel.getDepositInfo();
		String currencyCode = depositInfo.getCurrencyCode();
		String baseCurrency = "";
		BigDecimal amount = MathUtil.parseBigDecimal(depositInfo.getAmount(), null);
		BigDecimal amountDepositMax = MathUtil.parseBigDecimal(0);
		BigDecimal amountDepositMin = MathUtil.parseBigDecimal(0);
		if (IConstants.FRONT_OTHER.COMBO_TOP.equals(depositInfo.getMethod())) {
			List<String> listContent = new ArrayList<String>();
			listContent.add(getText("nts.ams.fe.label.deposit.method"));
			depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
			addFieldError("errorMessage", getText("MSG_NAF001", listContent));
			return;
		}

		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				Map<Integer, Boolean> mapCustomerServiceType = frontUserOnline.getMapCustomerService();
				if (!IConstants.SERVICES_TYPE.AMS.equals(depositInfo.getServiceType())) {
					if (!mapCustomerServiceType.containsKey(depositInfo.getServiceType())) {
						Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);

						List<Object> listContent = new ArrayList<Object>();
						listContent.add(mapServiceType.get(StringUtil.toString(depositInfo.getServiceType())));
						depositModel.setErrorMessage(getText("nts.ams.fe.error.message.NAB091", listContent));
						addFieldError("errorMessage", getText("nts.ams.fe.error.message.NAB091", listContent));
						return;
					}
				}

				String configValue = "";
				wlCode = frontUserOnline.getWlCode();
				// currencyCode = frontUserOnline.getCurrencyCode();
				Map<String, String> mapWlConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.WL_CONFIG + wlCode);
				configValue = mapWlConfig.get(currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT);
				amountDepositMax = MathUtil.parseBigDecimal(configValue);

				configValue = mapWlConfig.get(currencyCode + "_" + IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT);
				amountDepositMin = MathUtil.parseBigDecimal(configValue);

				// String key = currencyCode + "_" +
				// IConstants.WHITE_LABEL_CONFIG.MAX_DEPOSIT_AMOUNT;
				// WhiteLabelConfigInfo whiteLabelConfigInfo =
				// ibManager.getWhiteLabelConfigInfo(key, wlCode);
				// if(whiteLabelConfigInfo != null) {
				// amountDepositMax =
				// MathUtil.parseBigDecimal(whiteLabelConfigInfo.getConfigValue());
				// }
				//
				// key = currencyCode + "_" +
				// IConstants.WHITE_LABEL_CONFIG.MIN_DEPOSIT_AMOUNT;
				// whiteLabelConfigInfo = ibManager.getWhiteLabelConfigInfo(key,
				// wlCode);
				// if(whiteLabelConfigInfo != null) {
				// amountDepositMin =
				// MathUtil.parseBigDecimal(whiteLabelConfigInfo.getConfigValue());
				// }
				baseCurrency = frontUserOnline.getCurrencyCode();
			}
		}
		// check base currency and currency code for deposit
		if (!baseCurrency.equals(currencyCode)) {
			Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.DEPOSIT_SERVICE_TYPE);

			List<Object> listContent = new ArrayList<Object>();
			listContent.add(mapServiceType.get(StringUtil.toString(IConstants.SERVICES_TYPE.BO)));
			depositModel.setErrorMessage(getText("nts.ams.fe.message.cannot.deposit.NAB076", listContent));
			addFieldError("errorMessage", getText("nts.ams.fe.message.cannot.deposit.NAB076", listContent));
			return;
		}
		if (depositInfo != null) {
			if (depositInfo.getAmount() == null || StringUtils.isBlank(depositInfo.getAmount())) {
				List<String> listContent = new ArrayList<String>();
				listContent.add(getText("nts.ams.fe.label.deposit.amount"));
				depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
				addFieldError("errorMessage", getText("MSG_NAF001", listContent));
				return;
			}

			if (amount == null) {
				List<String> listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.deposit.amount"));
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContents));
				depositModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContents));
				return;
			} else if (amount.compareTo(MathUtil.parseBigDecimal(0)) < 0) {
				List<String> listContents = new ArrayList<String>();
				listContents.add(getText("nts.ams.fe.label.deposit.amount"));
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContents));
				depositModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB013", listContents));
				return;
			} else if ((amount.compareTo(amountDepositMax) > 0)) {
				List<String> listContents = new ArrayList<String>();
				listContents.add(StringUtil.toString(amountDepositMax));
				listContents.add(currencyCode);
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB024", listContents));
				depositModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB024", listContents));
				return;

			} else if (amount.compareTo(amountDepositMin) < 0) {
				List<String> listContents = new ArrayList<String>();
				listContents.add(StringUtil.toString(amountDepositMin));
				listContents.add(currencyCode);
				addFieldError("errorMessage", getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB023", listContents));
				depositModel.setErrorMessage(getText("nts.ams.fe.message.ibmanager.deposit.withdrawal.NAB023", listContents));
				return;
			}
			String rdNeteller = depositModel.getRdNetteller();
			String rdPayza = depositModel.getRdPayza();
			String rdLiberty = depositModel.getRdLiberty();
			// [NTS1.0-anhndn]Feb 27, 2013A - Start
			String libertyAccessMethod = depositModel.getLibertyAccessMethod();
			// [NTS1.0-anhndn]Feb 27, 2013A - End

			if (IConstants.PAYMENT_METHOD.NETELLER == depositInfo.getMethod()) {
				if (rdNeteller == null) {
					// do late
				} else {
					if (IConstants.DEPOSIT_CHOOSE_RADIO.NETELLER.equals(rdNeteller)) {
						NetellerInfo netellerInfo = depositModel.getNetellerInfo();
						if (netellerInfo != null) {
							if (netellerInfo.getAccountId() == null || StringUtils.isBlank(netellerInfo.getAccountId())) {
								// if customer don't input email account
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
								depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return;
							}
							if (netellerInfo.getSecureId() == null || StringUtils.isBlank(netellerInfo.getSecureId())) {
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.neteller.securityId"));
								depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));
								return;
							}
						} else {
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.neteller.accountId"));
							depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));
							return;

						}
					}
				}
			} else if (IConstants.PAYMENT_METHOD.PAYZA == depositInfo.getMethod()) {
				if (rdPayza == null) {
					// do late
				} else {
					if (IConstants.DEPOSIT_CHOOSE_RADIO.PAYZA.equals(rdPayza)) {
						PayzaInfo payzaInfo = depositModel.getPayzaInfo();
						if (payzaInfo != null) {
							if (payzaInfo.getEmailAddress() == null || StringUtils.isBlank(payzaInfo.getEmailAddress())) {
								// if customer don't input email account
								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
								depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));

								return;
							} else {
								if (!StringUtil.isEmail(payzaInfo.getEmailAddress())) {
									List<Object> listMsg = new ArrayList<Object>();
									listMsg.add(getText("nts.ams.fe.label.deposit.payza.email"));
									listMsg.add(getText("global.message.NAB007_2"));
									depositModel.setErrorMessage(getText("global.message.NAB007", listMsg));
									addFieldError("errorMessage", getText("global.message.NAB007", listMsg));
									return;
								}
							}
							if (payzaInfo.getApiPassword() == null || StringUtils.isBlank(payzaInfo.getApiPassword())) {

								List<String> listContent = new ArrayList<String>();
								listContent.add(getText("nts.ams.fe.label.deposit.payza.apipassword"));
								depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
								addFieldError("errorMessage", getText("MSG_NAF001", listContent));

								return;
							}
						} else {
							// if customer don't input email account
							List<String> listContent = new ArrayList<String>();
							listContent.add(getText("nts.ams.fe.label.deposit.payza.email"));
							depositModel.setErrorMessage(getText("MSG_NAF001", listContent));
							addFieldError("errorMessage", getText("MSG_NAF001", listContent));

							return;
						}
					}
				}
			} else if (IConstants.PAYMENT_METHOD.EXCHANGER == depositInfo.getMethod()) {
				if (depositInfo.getServiceType().intValue() != IConstants.SERVICES_TYPE.AMS.intValue()) {
					addFieldError("errorMessage", getText("MSG_NAB092"));
					depositModel.setErrorMessage(getText("MSG_NAB092"));
					return;
				} else if (StringUtil.isEmpty(depositModel.getExchangerInfo().getExchangerId())) {
					String msg = getText("MSG_NAF001", new String[] { getText("nts.ams.fe.label.exchanger") });
					depositModel.setErrorMessage(msg);
					addFieldError("errorMessage", msg);
				}
			}
			if (IConstants.PAYMENT_METHOD.LIBERTY == depositInfo.getMethod()) {
				if (IConstants.LIBERTY_PAYMENT.API.equals(libertyAccessMethod)) {
					if (!IConstants.DEPOSIT_CHOOSE_RADIO.LIBERTY.equals(rdLiberty)) {
						LibertyInfo libertyInfo = depositModel.getLibertyInfo();
						libertyInfo.setAccountNumber(rdLiberty);
						readEwalletInformation();
						List<CustomerEwalletInfo> listLibertyInfos = depositModel.getListLiberty();
						for (CustomerEwalletInfo liberty : listLibertyInfos) {
							if (liberty.getEwalletAccNo().equals(rdLiberty)) {
								libertyInfo.setApiName(liberty.getEwalletApiName());
								libertyInfo.setSecurityWord(liberty.getEwalletSecureWord());
								break;
							}
						}

					}
					if (StringUtil.isEmpty(depositModel.getLibertyInfo().getAccountNumber())) {
						String msg = getText("MSG_NAF001", new String[] { getText("nts.ams.fe.label.deposit.liberty.account") });
						depositModel.setErrorMessage(msg);
						addFieldError("errorMessage", msg);
					} else if (StringUtil.isEmpty(depositModel.getLibertyInfo().getApiName())) {
						String msg = getText("MSG_NAF001", new String[] { getText("nts.ams.fe.label.deposit.liberty.apiname") });
						depositModel.setErrorMessage(msg);
						addFieldError("errorMessage", msg);
					} else if (StringUtil.isEmpty(depositModel.getLibertyInfo().getSecurityWord())) {
						String msg = getText("MSG_NAF001", new String[] { getText("nts.ams.fe.label.deposit.liberty.secureword") });
						depositModel.setErrorMessage(msg);
						addFieldError("errorMessage", msg);
					}
				}
			} else if (IConstants.PAYMENT_METHOD.CREDIT_CARD == depositInfo.getMethod()) {
				if (depositModel.getPaymentGateway() == null) {
					List<Object> listContent = new ArrayList<Object>();
					listContent.add(getText("nts.ams.fe.label.deposit.payment.gateway"));
					String msg = getText("MSG_SC_013", listContent);
					depositModel.setErrorMessage(msg);
					addFieldError("errorMessage", msg);
				}
			}

			// if(IConstants.CURRENCY_CODE.JPY.equals(currencyCode)) {
			// amount = amount.divide(MathUtil.parseBigDecimal(1), 0,
			// BigDecimal.ROUND_DOWN);
			// depositModel.getDepositInfo().setAmount(StringUtil.toString(amount));
			// } else {
			// amount = amount.divide(MathUtil.parseBigDecimal(1), 2,
			// BigDecimal.ROUND_DOWN);
			// depositModel.getDepositInfo().setAmount(StringUtil.toString(amount));
			// }
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if (currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			amount = amount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			depositModel.getDepositInfo().setAmount(StringUtil.toString(amount));

		}
	}

	private void getDepositHisory() {
		log.info("[start] get history deposit of:");

		try {
			PagingInfo pagingInfo = depositModel.getPagingInfo();
			if (pagingInfo == null) {
				pagingInfo = new PagingInfo();
			}
			depositModel.setPagingInfo(pagingInfo);
			List<AmsDeposit> listAmsDeposit = null;
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					log.info("CustomerId = " + frontUserOnline.getUserId());
					listAmsDeposit = depositManager.getListDepositHistory(frontUserOnline.getUserId(), pagingInfo);
				} else {
					depositModel.setErrorMessage(getText("nts.ams.fe.message.history.session_timeout"));
				}
			}

			if (listAmsDeposit != null) {
				depositModel.setListAmsDeposit(listAmsDeposit);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

	}

	/**
	 * 　 get msg code for deposit
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 28, 2012
	 * @MdDate
	 */
	private void getMsgCode(String msgCode) {
		if (msgCode != null) {
			if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS)) {
				// depositModel.setSuccessMessage(getText("nts.ams.fe.info.message.success"));
				depositModel.setSuccessMessage(getText("MSG_NAB067"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE)) {
				// depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
				// depositModel.setErrorMessage(getText("MSG_NAB073"));
				depositModel.setErrorMessage(getText("MSG_NAB068"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE)) {
				depositModel.setInfoMessage(getText("MSG_NAB068"));
				// depositModel.setInfoMessage(getText("nts.ams.fe.error.message.not.available"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_MT4_ERROR)) {
				depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PAYONLINE_ERROR)) {
				depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_ALLCHARGE_ERROR)) {
				depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_ALLCHARGE_CANCEL)) {
				depositModel.setInfoMessage(getText("nts.ams.fe.error.message.allcharge.payment_cancel"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED)) {
				depositModel.setInfoMessage(getText("nts.ams.fe.label.deposit.processed"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING)) {
				depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_NO_CONVERT_RATE)) {
				depositModel.setErrorMessage(getText("nts.ams.fe.error.message.NAB066"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_CANCEL)) {
				depositModel.setErrorMessage(getText("MSG_NAB068"));
			} else if (msgCode.equals(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_REQUESTING_SUCCESS)) {
				depositModel.setSuccessMessage(getText("MSG_NAB106"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB073)) {
				depositModel.setErrorMessage(getText("MSG_NAB073"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB067)) {
				depositModel.setSuccessMessage(getText("MSG_NAB067"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB068)) {
				depositModel.setErrorMessage(getText("MSG_NAB068"));
			} else if (msgCode.equals(ITrsConstants.MSG_TRS_NAF_0002)) {
				depositModel.setErrorMessage(getText("MSG_TRS_NAF_0002"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB023)) {
				depositModel.setErrorMessage(getText("MSG_NAB023"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB024)) {
				depositModel.setErrorMessage(getText("MSG_NAB024"));
			} else if (msgCode.equals(ITrsConstants.MSG_TRS_NAF_0036)) {
				depositModel.setErrorMessage(getText("MSG_TRS_NAF_0036"));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB091)) {
				depositModel.setErrorMessage(getText("MSG_NAB091", getText("service_type.asm").split(",")));
			} else if (msgCode.equals(ITrsConstants.MSG_NAB001)) {
				depositModel.setErrorMessage(getText("MSG_NAB001", getText("payment_method.direct.deposit").split(",")));
			} else if (msgCode.equals(ITrsConstants.MSG_TRS_NAF_0058)) {
				depositModel.setErrorMessage(getText("MSG_TRS_NAF_0058", getText("")));
			}
		}
	}

	/**
	 * 　 action for payonline incase success
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 28, 2012
	 * @MdDate
	 */
	public String payonlineSuccess() {
		try {
			// get TransactionID from ReturnURL
			String payKey = this.getPayKey();
			log.info("[start] payonline successfully process deposit on payonline status success with paykey=" + payKey);
			String depositId = payKey;
			// get wl Code from Current User
			String wlCode = "";
			String customerId = "";
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					wlCode = frontUserOnline.getWlCode();
					customerId = frontUserOnline.getUserId();
					currencyCode = frontUserOnline.getCurrencyCode();
				}
			}
			Map<String, String> mapPayonlineConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.PAY_ONLINE_CONFIG);
			Map<String, String> mapFrontEnd = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.FRONT_END);
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			if (mapDeposit == null) {
				mapDeposit = new HashMap<String, String>();
				FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
			}
			synchronized (mapDeposit) {
				String transactionValue = mapDeposit.get(depositId);
				if (transactionValue == null) {
					mapDeposit.put(depositId, depositId);
				} else {
					depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING);
					return SUCCESS;
				}
			}
			log.info("payonline successfully TransactionID is " + depositId);
			// [NatureForex1.0-Administrator]Jun 26, 2012A - Start - add
			// checking for status transactionId
			log.info("[Start] payonline successfully Checking status for transactionId: " + depositId);

			// Boolean checkStatus =
			// PayonlineSystemContext.getInstance().checkStatusOnPayonline(transactionId,
			// mapPayonlineConfig, mapFrontEnd);
			PayonlinePaymentDetail payonlinePaymentDetail = PayonlineSystemContext.getInstance().getPayonlinePaymentDetail(depositId, mapPayonlineConfig, mapFrontEnd);
			log.info("payonline successfully Checking status of " + depositId + " is " + payonlinePaymentDetail.getStatus());
			log.info("[End] payonline successfully Checking status for transactionId: " + depositId);
			// [NatureForex1.0-Administrator]Jun 26, 2012A - End
			if (IConstants.RESPONSE_PAYONLINE_STATUS.PENDING.equalsIgnoreCase(payonlinePaymentDetail.getStatus())
					|| IConstants.RESPONSE_PAYONLINE_STATUS.SETTLE.equalsIgnoreCase(payonlinePaymentDetail.getStatus())) {
				String transactionId = payonlinePaymentDetail.getTransactionId();
				log.info("payonline successfully Checking status for payonline system is settled or Pending");
				// Integer result =
				// depositManager.depositPayonlineSuccess(transactionId,
				// depositId, customerId, wlCode, currencyCode,
				// IConstants.DEPOSIT_STATUS.FINISHED);
				Integer result = depositManager.depositPayonlineSuccess(transactionId, depositId, customerId, wlCode, currencyCode, IConstants.DEPOSIT_STATUS.FINISHED);
				if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// if update successful
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
					// if update deposit success but don't success on mt4
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// if update failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED.equals(result)) {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
				} else {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				}
				synchronized (mapDeposit) {
					mapDeposit.remove(transactionId);
				}
				log.info("[end] payonline successfully process deposit success with paykey=" + payKey);
			} else {
				synchronized (mapDeposit) {
					mapDeposit.remove(depositId);
				}
				// depositManager.depositUpdateStatus(depositId, customerId,
				// currencyCode, null, IConstants.DEPOSIT_STATUS.FAILURE, null,
				// wlCode,
				// StringUtil.toString(IConstants.PAYONLINE_ERROR_CODE.UNDEFINDED_ERROR),
				// null, null);
				depositManager.depositUpdateStatus(depositId, customerId, currencyCode, null, IConstants.DEPOSIT_STATUS.FAILURE, null, wlCode,
						StringUtil.toString(IConstants.PAYONLINE_ERROR_CODE.UNDEFINDED_ERROR), null, null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				log.info("payonline failure transactionId: " + depositId + " don't success in payonlineSystem");
				log.info("payonline failure End process with transactionId: " + depositId);
			}

			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				mapDeposit.remove(this.getPayKey());
			}
			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);
			return INPUT;
		}
	}

	/**
	 * get sub group id of fx account
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Nov 14, 2012
	 */
	private Integer getSubGroupId() {
		String customerId = null;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				customerId = frontUserOnline.getUserId();
			}
		}

		return accountManager.getSubGroupIdFX(customerId);
	}

	/**
	 * Process success response from liberty SCI　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 27, 2013
	 */
	public String libertySuccess() {
		log.info("[start] libertySuccess");
		try {
			// get response information from return URL
			String lr_paidto = this.getLr_paidto();
			String lr_paidby = this.getLr_paidby();
			String lr_amnt = this.getLr_amnt();
			String lr_fee_amnt = this.getLr_fee_amnt();
			String lr_currency = this.getLr_currency();
			String lr_transfer = this.getLr_transfer();
			String lr_store = this.getLr_store();
			String lr_timestamp = this.getLr_timestamp();
			String lr_merchant_ref = this.getLr_merchant_ref();

			String lr_encrypted = this.getLr_encrypted();

			// get TransactionID from ReturnURL
			String transactionId = this.getOrder_id();

			log.info("[Start] liberty deposit response succes for transactionId: " + transactionId + "; lr_paidto: " + lr_paidto + "; lr_paidby:" + lr_paidby + "; lr_amnt:" + lr_amnt
					+ "; lr_fee_amnt: " + lr_fee_amnt + "; lr_currency: " + lr_currency + "; lr_transfer: " + lr_transfer + "; lr_store: " + lr_store + "; lr_timestamp: " + lr_timestamp
					+ "; lr_merchant_ref: " + lr_merchant_ref + "; lr_encrypted:" + lr_encrypted);

			// get wl Code from Current User
			String wlCode = "";
			String customerId = "";
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					wlCode = frontUserOnline.getWlCode();
					customerId = frontUserOnline.getUserId();
					currencyCode = frontUserOnline.getCurrencyCode();
				}
			}
			Map<String, String> mapLibertyConfig = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + wlCode + "_" + IConstants.SYS_PROPERTY.LIBERTY_CONFIG);
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);

			String transaction_encrypted_code = null;
			if (mapDeposit != null) {
				transaction_encrypted_code = mapDeposit.get(transactionId);
			}

			if (transaction_encrypted_code == null) {
				transaction_encrypted_code = lr_encrypted;
			}
			String encoded_lr = LibertyContext.getInstance().encodeLibertyInfo(lr_paidto, lr_paidby, lr_store, lr_amnt, lr_transfer, lr_currency, mapLibertyConfig);

			log.info("[Start] liberty sci deposit check status for transactionId: " + transactionId + "transaction_encrypted_code: " + transaction_encrypted_code + "; encode_lr: " + encoded_lr);
			if (encoded_lr.equalsIgnoreCase(transaction_encrypted_code)) {
				log.info("[Start] liberty sci deposit successfully for transactionId: " + transactionId + "transaction_encrypted_code: " + transaction_encrypted_code);
				Integer result = depositManager.depositUpdateStatus(transactionId, customerId, currencyCode, currencyCode, IConstants.DEPOSIT_STATUS.FINISHED, null, wlCode, null,
						IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, IConstants.PAYMENT_METHOD.LIBERTY);
				depositManager.updateDepositRef(transactionId, lr_paidby);
				if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// if update successful
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
					// if update deposit success but don't success on mt4
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// if update failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED.equals(result)) {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
				} else {
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				}
				if (mapDeposit != null) {
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				}
				log.info("[end] liberty sci successfully process deposit success with transactionId: " + transactionId);
			} else {
				log.info("[Start] liberty sci deposit fail (NO VERIFICATION) for transactionId: " + transactionId + "transaction_encrypted_code: " + transaction_encrypted_code);
				if (mapDeposit != null) {
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				}

				String reason = IConstants.LIBERTY_ERROR_REASON.VERIFICATION_SERVER_NOT_OK;
				ErrorCode = IConstants.LIBERTY_ERROR_CODE.VERIFICATION_SERVER_NOT_OK;

				log.info("[START] liberty sci failure transactionId: " + transactionId + " " + reason + ErrorCode.toString());
				depositManager.depositUpdateStatus(transactionId, customerId, currencyCode, currencyCode, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode, StringUtil.toString(ErrorCode), null,
						null, IConstants.PAYMENT_METHOD.LIBERTY);
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);

				log.info("[END] liberty sci failure transactionId: " + transactionId + " " + reason + ErrorCode.toString());
			}

			log.info("[end] libertySuccess");
			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			if (mapDeposit != null) {
				synchronized (mapDeposit) {
					mapDeposit.remove(this.getPayKey());
				}
			}

			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);
			return INPUT;
		}
	}

	/**
	 * Check liberty payment status　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 27, 2013
	 */
	public String checkLibertyPaymentStatus() {
		// get TransactionID from ReturnURL
		String transactionId = this.getOrder_id();
		String lr_encrypted = this.getLr_encrypted();

		log.info("[start] check liberty response status success with transactionId: " + transactionId + "; lr_encrypted: " + lr_encrypted);
		Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
		if (mapDeposit == null) {
			mapDeposit = new HashMap<String, String>();
			FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
		}
		synchronized (mapDeposit) {
			String transaction_encrypted_code = mapDeposit.get(transactionId);
			if (transaction_encrypted_code == null) {
				transaction_encrypted_code = lr_encrypted;
				mapDeposit.put(transactionId, transaction_encrypted_code);
			}
		}
		return SUCCESS;
	}

	/**
	 * Process when liberty gw return fail response　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author anh.nguyen.ngoc
	 * @CrDate Feb 27, 2013
	 */
	public String libertyFail() {
		try {
			// get TransactionID from ReturnURL
			String transactionId = this.getOrder_id();
			if (!StringUtil.isEmpty(transactionId)) {
				String reason = null;
				log.info("[start] process deposit fail with transactionId: " + transactionId);

				String customerId = "";
				String currencyCode = "";
				String wlCode = "";
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					if (frontUserOnline != null) {
						customerId = frontUserOnline.getUserId();
						currencyCode = frontUserOnline.getCurrencyCode();
						wlCode = frontUserOnline.getWlCode();
					}
				}

				Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);

				ErrorCode = IConstants.LIBERTY_ERROR_CODE.UNDEFINDED_ERROR;
				reason = IConstants.LIBERTY_ERROR_REASON.UNDEFINDED_ERROR;

				Integer result = depositManager.depositUpdateStatus(transactionId, customerId, currencyCode, currencyCode, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
						StringUtil.toString(ErrorCode), null, null, IConstants.PAYMENT_METHOD.LIBERTY);
				if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// if update successful
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// update failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// update successful
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PAYONLINE_ERROR);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
					// this transaction is processed
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
				} else {
					// not available
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				}
				if (mapDeposit != null) {
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				}

				log.info("[end] process deposit fail with transactionId: " + transactionId);
				return SUCCESS;
			}
			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				if (mapDeposit != null) {
					mapDeposit.remove(this.getPayKey());
				}
			}
			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);
			return SUCCESS;
		}
	}

	public String payonlineCancel() {
		try {
			// get TransactionID from ReturnURL
			String transactionId = this.getPayKey();
			String reason = null;
			log.info("[start] process deposit fail with paykey=" + payKey);
			log.info("TransactionID is " + transactionId);
			log.info("ErrorCode is " + ErrorCode);
			String customerId = "";
			String currencyCode = "";
			String wlCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					currencyCode = frontUserOnline.getCurrencyCode();
					wlCode = frontUserOnline.getWlCode();
				}
			}

			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			if (mapDeposit == null) {
				mapDeposit = new HashMap<String, String>();
				FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
			}
			synchronized (mapDeposit) {
				String transactionValue = mapDeposit.get(transactionId);
				if (transactionValue == null) {
					mapDeposit.put(transactionId, transactionId);
				} else {
					depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING);
					return SUCCESS;
				}
			}
			if (ErrorCode == IConstants.PAYONLINE_ERROR_CODE.TECHNICAL_ERROR) {
				reason = getText("message.deposit.payonline.technical_error");
			} else if (ErrorCode == IConstants.PAYONLINE_ERROR_CODE.NOTACCEPTED_ERROR) {
				reason = getText("message.deposit.payonline.noaccepted_error");
			} else {
				reason = getText("message.deposit.payonline.declined_error");
			}
			// Integer result =
			// depositManager.depositUpdateStatus(transactionId, customerId,
			// currencyCode, null, IConstants.DEPOSIT_STATUS.CANCEL, reason,
			// wlCode, StringUtil.toString(ErrorCode), null, null);
			Integer result = depositManager.depositUpdateStatus(transactionId, customerId, currencyCode, null, IConstants.DEPOSIT_STATUS.CANCEL, reason, wlCode, StringUtil.toString(ErrorCode), null,
					null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
			if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
				// update failure
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
				// update successful
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PAYONLINE_ERROR);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
				// this transaction is processed
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
			} else {
				// not available
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
			}
			synchronized (mapDeposit) {
				mapDeposit.remove(transactionId);
			}

			log.info("[end] process deposit fail with paykey=" + payKey);
			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				mapDeposit.remove(this.getPayKey());
			}
			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);
			return INPUT;
		}
	}

	/**
	 * 　 action allcharge success
	 * 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Aug 22, 2012
	 * @MdDate
	 */
	public String allChargeSuccess() {
		// get information form URL
		log.info("[Start] getting information of allcharge method from url");
		// String merchantId = "";
		// String merchantData = "";
		BigDecimal amount;
		String currency = "";
		String transactionId = "";
		String customerId = "";
		String wlCode = "";
		String baseCurrency = "";
		try {
			log.info("MerchantId" + httpRequest.getParameter("MerchantID"));
			log.info("Amount" + httpRequest.getParameter("Amount"));
			log.info("Currency" + httpRequest.getParameter("Currency"));
			log.info("transactionId" + httpRequest.getParameter("TransactionID"));
			log.info("merchantData" + httpRequest.getParameter("MerchantData"));
			log.info("[End] get some parametter from allcharge");

			// merchantId = httpRequest.getParameter("MerchantID");
			// merchantData = httpRequest.getParameter("MerchantData");
			amount = MathUtil.parseBigDecimal(httpRequest.getParameter("Amount"));
			currency = httpRequest.getParameter("Currency");
			transactionId = httpRequest.getParameter("TransactionID");

			if (amount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
				log.info("[start] process with transaction " + transactionId);

				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					if (frontUserOnline != null) {
						customerId = frontUserOnline.getUserId();
						wlCode = frontUserOnline.getWlCode();
						baseCurrency = frontUserOnline.getCurrencyCode();
					}
				}
				Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
				if (mapDeposit == null) {
					mapDeposit = new HashMap<String, String>();
					FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
				}
				synchronized (mapDeposit) {
					String transactionValue = mapDeposit.get(transactionId);
					if (transactionValue == null) {
						mapDeposit.put(transactionId, transactionId);
					} else {
						depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING);
						return SUCCESS;
					}
				}
				AllChargeResponseInfo allChargeReponseInfo = AllChargeContext.getInstance().getAllChargeResponseInfo(transactionId, amount, currency, wlCode);
				log.info("[Start] Checking status for transactionId: " + transactionId);
				Integer returnCode = allChargeReponseInfo.getReturnCode();
				log.info("Checking status of " + transactionId + " is " + returnCode);
				log.info("[End] Checking status for transactionId: " + transactionId);
				if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.SUCCESS.equals(returnCode)) {
					// if return code is successful
					String reason = "";
					// Integer result =
					// depositManager.depositUpdateStatus(transactionId,
					// customerId, baseCurrency, currency,
					// IConstants.DEPOSIT_STATUS.FINISHED, reason, wlCode, null,
					// IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_ALLCHARGE,
					// IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_ALLCHARGE);
					Integer result = depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FINISHED, reason, wlCode, null,
							IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_ALLCHARGE, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_ALLCHARGE, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
						// update failure
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
						// update successful
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
						// this transaction is processed
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
					} else {
						// not available
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
					}
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
					log.info("[end] process deposit fail with paykey=" + payKey);
				} else if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.CANCELLED.equals(returnCode)) {
					// if this transaction is cancelled
					log.warn("Transaction " + transactionId + " is cancelled");
					String reason = getText("nts.ams.fe.error.message.allcharge.payment_cancel");
					Integer result = depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.CANCEL, reason, wlCode,
							StringUtil.toString(returnCode), null, null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
						// update failure
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
						// update successful
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
					} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
						// this transaction is processed
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
					} else {
						// not available
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
					}
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				} else if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.UNKNOWN.equals(returnCode)) {
					String reason = getText("nts.ams.fe.error.message.allcharge.payment_fail");
					log.info("transactionId " + transactionId + " is unknown on allcharge");
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					// depositManager.depositUpdateStatus(transactionId,
					// customerId, baseCurrency, currency,
					// IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
					// StringUtil.toString(returnCode), null, null);
					depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode, StringUtil.toString(returnCode), null,
							null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					depositModel.setErrorMessage(getText("nts.ams.fe.error.message.allcharge.payment_fail"));
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				} else if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.NO_RECORD.equals(returnCode)) {
					String reason = getText("nts.ams.fe.error.message.not.available");
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
					log.warn("Status of transaction " + transactionId + " is [No matching Record]");
					depositModel.setErrorMessage(getText("nts.ams.fe.error.message.not.available"));
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					// depositManager.depositUpdateStatus(transactionId,
					// customerId, baseCurrency, currency,
					// IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
					// StringUtil.toString(returnCode), null, null);
					depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode, StringUtil.toString(returnCode), null,
							null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
					// if return status is [Record already utilized]
				} else if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.UTILIZED_RECORD.equals(returnCode)) {
					String reason = getText("nts.ams.fe.error.message.processed");
					log.warn("Status of transaction " + transactionId + " is [Record already utilized]");
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
					depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processed"));
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					// depositManager.depositUpdateStatus(transactionId,
					// customerId, baseCurrency, currency,
					// IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
					// StringUtil.toString(returnCode), null, null);
					depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode, StringUtil.toString(returnCode), null,
							null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
					// if return status is [Record too long]
				} else if (IConstants.ALLCHARGE_SYNC_RETURN_CODE.RECORD_TOO_LONG.equals(returnCode)) {
					String reason = getText("nts.ams.fe.error.message.allcharge.payment_fail");
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
					log.warn("Status of transaction " + transactionId + " is [Record too long]");
					depositModel.setErrorMessage(getText("nts.ams.fe.error.message.allcharge.payment_fail"));
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					// depositManager.depositUpdateStatus(transactionId,
					// customerId, baseCurrency, currency,
					// IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
					// StringUtil.toString(returnCode), null, null);
					depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode, StringUtil.toString(returnCode), null,
							null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
					log.info("[start] update allcharge deposit id " + transactionId + " to failure");
					synchronized (mapDeposit) {
						mapDeposit.remove(transactionId);
					}
				}
			}

		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				mapDeposit.remove(this.getPayKey());
			}
			String reason = "";
			log.info("[start] update allcharge deposit id " + transactionId + " to failure");
			// depositManager.depositUpdateStatus(transactionId, customerId,
			// baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE,
			// reason, wlCode,
			// StringUtil.toString(IConstants.ALLCHARGE_SYNC_RETURN_CODE.UNDEFINDED_ERROR),
			// null, null);
			depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.FAILURE, reason, wlCode,
					StringUtil.toString(IConstants.ALLCHARGE_SYNC_RETURN_CODE.UNDEFINDED_ERROR), null, null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
			log.info("[start] update allcharge deposit id " + transactionId + " to failure");
			setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.allcharge.payment_fail"));
			log.error(ex.toString(), ex);
			return SUCCESS;
		}

		return SUCCESS;
	}

	public String allChargeCancel() {
		log.info("[Start] getting information of allcharge method from url");
		// String merchantId = "";
		BigDecimal amount;
		String currency = "";
		String transactionId = "";
		// String merchantData = "";
		try {
			// [NatureForex1.0-HuyenMT]Jul 26, 2012A - Start
			log.info("MerchantId" + httpRequest.getParameter("MerchantID"));
			log.info("Amount" + httpRequest.getParameter("Amount"));
			log.info("Currency" + httpRequest.getParameter("Currency"));
			log.info("transactionId" + httpRequest.getParameter("TransactionID"));
			log.info("merchantData" + httpRequest.getParameter("MerchantData"));
			log.info("[End] get some parametter from allcharge");
			// [NatureForex1.0-HuyenMT]Jul 26, 2012A - End

			// merchantId = httpRequest.getParameter("MerchantID");
			amount = MathUtil.parseBigDecimal(httpRequest.getParameter("Amount"));
			currency = httpRequest.getParameter("Currency");
			transactionId = httpRequest.getParameter("TransactionID");
			// merchantData = httpRequest.getParameter("MerchantData");
			if (amount.compareTo(MathUtil.parseBigDecimal(0)) > 0) {
				String customerId = "";
				String wlCode = "";
				String baseCurrency = "";
				FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
				if (frontUserDetails != null) {
					FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
					if (frontUserOnline != null) {
						customerId = frontUserOnline.getUserId();
						wlCode = frontUserOnline.getWlCode();
						baseCurrency = frontUserOnline.getCurrencyCode();
					}
				}
				Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
				if (mapDeposit == null) {
					mapDeposit = new HashMap<String, String>();
					FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
				}
				synchronized (mapDeposit) {
					String transactionValue = mapDeposit.get(transactionId);
					if (transactionValue == null) {
						mapDeposit.put(transactionId, transactionId);
					} else {
						depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
						setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING);
						return SUCCESS;
					}
				}
				log.info("[start] process with transaction" + transactionId);
				log.info("Status of " + transactionId + " is cancelled");
				log.info("[End] process with transaction " + transactionId);
				String reason = getText("nts.ams.fe.error.message.allcharge.payment_cancel");
				// update deposit status
				// Integer result =
				// depositManager.depositUpdateStatus(transactionId, customerId,
				// baseCurrency, currency, IConstants.DEPOSIT_STATUS.CANCEL,
				// reason, wlCode, null, null, null);
				Integer result = depositManager.depositUpdateStatus(transactionId, customerId, baseCurrency, currency, IConstants.DEPOSIT_STATUS.CANCEL, reason, wlCode, null, null, null,
						IConstants.PAYMENT_METHOD.CREDIT_CARD);
				if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
					// update failure
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
					// update successful
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_ALLCHARGE_CANCEL);
				} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
					// this transaction is processed
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
				} else {
					// not available
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
				}
				synchronized (mapDeposit) {
					mapDeposit.remove(transactionId);
				}
			}
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				mapDeposit.remove(this.getPayKey());
			}
			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.allcharge.payment_fail"));
			setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			log.error(ex.toString(), ex);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * Check account status　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Quan.Le.Minh
	 * @CrDate Jan 25, 2013
	 */
	private boolean isAllowDeposit() {
		boolean flg = false;
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if (frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if (frontUserOnline != null) {
				DepositInfo depositInfo = depositModel.getDepositInfo();
				if (!IConstants.SERVICES_TYPE.AMS.equals(depositInfo.getServiceType())) {
					CustomerServicesInfo customerServiceInfoToAccount = accountManager.getCustomerServiceInfo(frontUserOnline.getUserId(), depositInfo.getServiceType());
					if (customerServiceInfoToAccount != null) {
						Integer serviceStatus = customerServiceInfoToAccount.getCustomerServiceStatus();
						if (!IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED.equals(serviceStatus) && !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_DEPOSITED.equals(serviceStatus)
								&& !IConstants.CUSTOMER_SERVIVES_STATUS.OPEN_COMPLETED_TRADED.equals(serviceStatus)) {

							Map<String, String> mapServiceType = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + IConstants.SYS_PROPERTY.SERVICE_TYPE);
							List<Object> listContent = new ArrayList<Object>();
							listContent.add(mapServiceType.get(StringUtil.toString(depositInfo.getServiceType())));
							depositModel.setErrorMessage(getText("nts.ams.fe.error.message.NAB091", listContent));
							addFieldError("errorMessage", getText("nts.ams.fe.error.message.NAB091", listContent));
							flg = false;
						} else {
							flg = true;
						}
					}
				} else {
					flg = true;
				}
			}
		}
		return flg;
	}

	/**
	 * netpayCancel　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Mar 7, 2013
	 */
	public String netpayCancel() {
		try {
			// get TransactionID from ReturnURL
			String transactionId = this.getPayKey();
			String reason = null;
			log.info("[start] process deposit fail with paykey=" + payKey);
			log.info("TransactionID is " + transactionId);
			log.info("ErrorCode is " + ErrorCode);
			String customerId = "";
			String currencyCode = "";
			String wlCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					customerId = frontUserOnline.getUserId();
					currencyCode = frontUserOnline.getCurrencyCode();
					wlCode = frontUserOnline.getWlCode();
				}
			}

			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			if (mapDeposit == null) {
				mapDeposit = new HashMap<String, String>();
				FrontEndContext.getInstance().putContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT, mapDeposit);
			}

			synchronized (mapDeposit) {
				String transactionValue = mapDeposit.get(transactionId);
				if (transactionValue == null) {
					mapDeposit.put(transactionId, transactionId);
				} else {
					depositModel.setInfoMessage(getText("nts.ams.fe.error.message.processing"));
					setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSING);
					return SUCCESS;
				}
			}

			if (ErrorCode == IConstants.PAYONLINE_ERROR_CODE.TECHNICAL_ERROR) {
				reason = getText("message.deposit.netpay.technical_error");
			} else if (ErrorCode == IConstants.PAYONLINE_ERROR_CODE.NOTACCEPTED_ERROR) {
				reason = getText("message.deposit.netpay.noaccepted_error");
			} else {
				reason = getText("message.deposit.netpay.declined_error");
			}

			// Integer result =
			// depositManager.depositUpdateStatus(transactionId, customerId,
			// currencyCode, null, IConstants.DEPOSIT_STATUS.CANCEL, reason,
			// wlCode, StringUtil.toString(ErrorCode), null, null);
			Integer result = depositManager.depositUpdateStatus(transactionId, customerId, currencyCode, null, IConstants.DEPOSIT_STATUS.CANCEL, reason, wlCode, StringUtil.toString(ErrorCode), null,
					null, IConstants.PAYMENT_METHOD.CREDIT_CARD);
			if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
				// update failure
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
				// update successful
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PAYONLINE_ERROR);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.PROCESSED.equals(result)) {
				// this transaction is processed
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
			} else {
				// not available
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
			}

			synchronized (mapDeposit) {
				mapDeposit.remove(transactionId);
			}

			log.info("[end] process deposit fail with paykey=" + payKey);
			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			synchronized (mapDeposit) {
				mapDeposit.remove(this.getPayKey());
			}

			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);

			return INPUT;
		}
	}

	public String netpaySuccess() {
		log.info("[start] netpaySuccess");
		String depositId = null;
		try {
			// get response information from return URL
			String replyCode = getReplyCode();
			String replyDesc = getReplyDesc();
			String trans_id = getTrans_id();
			String client_id = getClient_id();
			String recurringSeries_id = getRecurringSeries_id();

			log.info("[Start] netpay deposit response succes for replyCode: " + replyCode + "; replyDesc: " + replyDesc + "; trans_id:" + trans_id + "; recurringSeries_id:" + recurringSeries_id);
			// get wl Code from Current User
			String wlCode = "";
			String customerId = "";
			String currencyCode = "";
			FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
			if (frontUserDetails != null) {
				FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
				if (frontUserOnline != null) {
					wlCode = frontUserOnline.getWlCode();
					customerId = frontUserOnline.getUserId();
					currencyCode = frontUserOnline.getCurrencyCode();
				}
			}

			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);

			if (mapDeposit != null) {
				String clientId = client_id.replaceAll(",", "").trim();
				depositId = mapDeposit.get(clientId);
			}

			if (StringUtil.isEmpty(depositId)) {
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				return SUCCESS;
			}

			if (mapDeposit != null && !StringUtil.isEmpty(depositId)) {
				synchronized (mapDeposit) {
					mapDeposit.remove(depositId);
				}
			}

			Integer result = 1;
			if (IConstants.NETPAY.ERROR_CODE.SUCCESS.equals(replyCode)) {
				result = depositManager.depositUpdateStatus(depositId, customerId, currencyCode, currencyCode, IConstants.DEPOSIT_STATUS.FINISHED, null, wlCode, null,
						IConstants.FUND_DESCRIPION.FE_DEPOSIT_BALANCE_LIBERTY, IConstants.FUND_DESCRIPION.FE_DEPOSIT_CREDIT_LIBERTY, IConstants.PAYMENT_METHOD.LIBERTY);
			} else {
				depositManager.depositUpdateStatus(depositId, customerId, currencyCode, null, IConstants.DEPOSIT_STATUS.CANCEL, replyDesc, wlCode, StringUtil.toString(ErrorCode), null, null,
						IConstants.PAYMENT_METHOD.CREDIT_CARD);
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
				return SUCCESS;
			}

			if (IConstants.DEPOSIT_UPDATE_RESULT.SUCCESS.equals(result)) {
				// if update successful
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_SUCCESS);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.MT4_ERROR.equals(result)) {
				// if update deposit success but don't success on mt4
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			} else if (IConstants.DEPOSIT_UPDATE_RESULT.FAILURE.equals(result)) {
				// if update failure
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_FAILURE);
			} else if (IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED.equals(result)) {
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_PROCESSED);
			} else {
				setMsgCode(IConstants.DEPOSIT_MSG_CODE.MSG_DEPOSIT_NOT_AVAILABLE);
			}

			log.info("[end] netpay successfully process deposit success with paykey=" + depositId);

			return SUCCESS;
		} catch (Exception ex) {
			Map<String, String> mapDeposit = FrontEndContext.getInstance().getContext(IConstants.FRONT_END_CONFIG.SUFFIX_DEPOSIT);
			if (mapDeposit != null) {
				synchronized (mapDeposit) {
					mapDeposit.remove(depositId);
				}
			}

			depositModel.setErrorMessage(getText("nts.ams.fe.error.message.failure"));
			log.error(ex.toString(), ex);
			return INPUT;
		}
	}

	/**
	 * @param balanceManager
	 *            the balanceManager to set
	 */
	public void setBalanceManager(IBalanceManager balanceManager) {
		this.balanceManager = balanceManager;
	}

	public IExchangerManager getExchangerManager() {
		return exchangerManager;
	}

	public void setExchangerManager(IExchangerManager exchangerManager) {
		this.exchangerManager = exchangerManager;
	}

	@Override
	public DepositModel getModel() {
		return depositModel;
	}

	public IDepositManager getDepositManager() {
		return depositManager;
	}

	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the msgCode
	 */
	public String getMsgCode() {
		return msgCode;
	}

	/**
	 * @param msgCode
	 *            the msgCode to set
	 */
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 * @return the ibManager
	 */
	public IIBManager getIbManager() {
		return ibManager;
	}

	/**
	 * @param ibManager
	 *            the ibManager to set
	 */
	public void setIbManager(IIBManager ibManager) {
		this.ibManager = ibManager;
	}

	/**
	 * @return the payKey
	 */
	public String getPayKey() {
		return payKey;
	}

	/**
	 * @param payKey
	 *            the payKey to set
	 */
	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public void setServletRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;

	}

	public void setServletResponse(HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;

	}

	/**
	 * @return the errorCode
	 */
	public Integer getErrorCode() {
		return ErrorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(Integer errorCode) {
		ErrorCode = errorCode;
	}

	public String getLr_encrypted() {
		return lr_encrypted;
	}

	public void setLr_encrypted(String lr_encrypted) {
		this.lr_encrypted = lr_encrypted;
	}

	public String getLr_paidto() {
		return lr_paidto;
	}

	public void setLr_paidto(String lr_paidto) {
		this.lr_paidto = lr_paidto;
	}

	public String getLr_paidby() {
		return lr_paidby;
	}

	public void setLr_paidby(String lr_paidby) {
		this.lr_paidby = lr_paidby;
	}

	public String getLr_amnt() {
		return lr_amnt;
	}

	public void setLr_amnt(String lr_amnt) {
		this.lr_amnt = lr_amnt;
	}

	public String getLr_fee_amnt() {
		return lr_fee_amnt;
	}

	public void setLr_fee_amnt(String lr_fee_amnt) {
		this.lr_fee_amnt = lr_fee_amnt;
	}

	public String getLr_currency() {
		return lr_currency;
	}

	public void setLr_currency(String lr_currency) {
		this.lr_currency = lr_currency;
	}

	public String getLr_transfer() {
		return lr_transfer;
	}

	public void setLr_transfer(String lr_transfer) {
		this.lr_transfer = lr_transfer;
	}

	public String getLr_store() {
		return lr_store;
	}

	public void setLr_store(String lr_store) {
		this.lr_store = lr_store;
	}

	public String getLr_timestamp() {
		return lr_timestamp;
	}

	public void setLr_timestamp(String lr_timestamp) {
		this.lr_timestamp = lr_timestamp;
	}

	public String getLr_merchant_ref() {
		return lr_merchant_ref;
	}

	public void setLr_merchant_ref(String lr_merchant_ref) {
		this.lr_merchant_ref = lr_merchant_ref;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}

	public String getReplyDesc() {
		return replyDesc;
	}

	public void setReplyDesc(String replyDesc) {
		this.replyDesc = replyDesc;
	}

	public String getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}

	public String getTrans_date() {
		return trans_date;
	}

	public void setTrans_date(String trans_date) {
		this.trans_date = trans_date;
	}

	public String getTrans_amount() {
		return trans_amount;
	}

	public void setTrans_amount(String trans_amount) {
		this.trans_amount = trans_amount;
	}

	public String getTrans_currency() {
		return trans_currency;
	}

	public void setTrans_currency(String trans_currency) {
		this.trans_currency = trans_currency;
	}

	public String getTrans_installments() {
		return trans_installments;
	}

	public void setTrans_installments(String trans_installments) {
		this.trans_installments = trans_installments;
	}

	public String getTrans_refNum() {
		return trans_refNum;
	}

	public void setTrans_refNum(String trans_refNum) {
		this.trans_refNum = trans_refNum;
	}

	public String getClient_id() {
		return client_id == null ? "" : client_id.trim();
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getStorage_id() {
		return storage_id;
	}

	public void setStorage_id(String storage_id) {
		this.storage_id = storage_id;
	}

	public String getPaymentDisplay() {
		return paymentDisplay;
	}

	public void setPaymentDisplay(String paymentDisplay) {
		this.paymentDisplay = paymentDisplay;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getClient_fullName() {
		return client_fullName;
	}

	public void setClient_fullName(String client_fullName) {
		this.client_fullName = client_fullName;
	}

	public String getClient_phoneNum() {
		return client_phoneNum;
	}

	public void setClient_phoneNum(String client_phoneNum) {
		this.client_phoneNum = client_phoneNum;
	}

	public String getRecurringSeries_id() {
		return recurringSeries_id;
	}

	public void setRecurringSeries_id(String recurringSeries_id) {
		this.recurringSeries_id = recurringSeries_id;
	}

	public String getPaymentGateway() {
		String cardType = httpRequest.getParameter("cardType");
		if (!StringUtil.isEmpty(cardType)) {
			Map<String, String> mapPayment = depositManager.getMapPaymentGW(MathUtil.parseInteger(cardType));
			depositModel.setMapPaymentSystem(mapPayment);
		}
		return SUCCESS;
	}
}
