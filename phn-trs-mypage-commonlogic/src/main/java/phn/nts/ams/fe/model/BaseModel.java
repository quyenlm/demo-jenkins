package phn.nts.ams.fe.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.SymbolInfo;



public class BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> messages;
	private String successMessage;
	private String errorMessage;
	private String infoMessage;
	private PagingInfo pagingInfo;
	private String page; 
	private Integer mode = IConstants.SOCIAL_MODES.OWNER_MODE;
		
	/**
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	/**
	 * @return the successMessage
	 */
	public String getSuccessMessage() {
		return successMessage;
	}

	/**
	 * @param successMessage the successMessage to set
	 */
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the pagingInfo
	 */
	public PagingInfo getPagingInfo() {		
		return pagingInfo;
	}

	/**
	 * @param pagingInfo the pagingInfo to set
	 */
	public void setPagingInfo(PagingInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return the infoMessage
	 */
	public String getInfoMessage() {
		return infoMessage;
	}

	/**
	 * @param infoMessage the infoMessage to set
	 */
	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}
	public String formatPassword(String password) {
		String result = IConstants.FRONT_OTHER.PASSWORD_MARK_DEFAULT;
		if(password != null) {
			if(password.length() >= 3) {
				result = "";
				for(int i = 0;i < password.length() - 3; i ++) {
					result +=IConstants.FRONT_OTHER.PASSWORD_MARK_SYMBOL; 
				}
				result += password.substring(password.length() - 3, password.length());
			}
		}
		return result;
	}
	public String displayPassword(String password) {
		String result = "";
		if(password != null) {
			for(int i = 0; i < password.length(); i ++ ) {
				result +=IConstants.FRONT_OTHER.PASSWORD_MARK_SYMBOL;
			}
		}
		return result;
	}
	/**
	 * 　
	 * format number 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 11, 2012
	 * @MdDate
	 */
	public String formatNumber(BigDecimal number, String currencyCode) {
		String result = "";		
		if(number != null) {
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
		}
		
		
		return result;
	}	
	
	/**
	 * 　
	 * format number 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Sep 11, 2012
	 * @MdDate
	 */
	public String formatNumber(BigDecimal number, String currencyCode, String language) {
		String result = "";		
		if(number != null) {
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
				rounding = currencyInfo.getCurrencyRound();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
			if(IConstants.Language.JAPANESE.equals(language)) {
				
			} else {
				result += " " + currencyCode;
			}
		}
		
		
		return result;
	}	
	public String formatNumberBySymbol(BigDecimal number, String symbolName) {
		String result = "";
		if(number != null) {
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolName);
			if(symbolInfo != null) {
				scale = symbolInfo.getSymbolDecimal();
				rounding = symbolInfo.getSymbolRound();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.divide(MathUtil.parseBigDecimal(1), scale, rounding);
			
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
			
		}
		return result;
	}
	public String formatNumberByPattern(BigDecimal number, String pattern) {
		String result = "";
		if(number != null) {
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
		}
		return result;
	}
	public String formatNumberMT4(BigDecimal number, String currencyCode){
		String result = "";
		if(number != null){
			String pattern = IConstants.NUMBER_FORMAT.CURRENCY_NONE_DECIMAL;
			Integer scale = new Integer(0);
			Integer rounding = new Integer(0);
			String MT4Rounding = SystemPropertyConfig.getInstance().getMap(IConstants.SYSTEM_CONFIG_KEY.SYS_PROPERTY + ITrsConstants.SYS_PROPERTY.MT4_ROUNDING).get(ITrsConstants.SYS_PROPERTY.MT4_ROUNDING);
			if(!StringUtil.isEmpty(MT4Rounding)){
				rounding = Integer.valueOf(MT4Rounding);
			}
			CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
			if(currencyInfo != null) {
				scale = currencyInfo.getCurrencyDecimal();
			}
			if(scale.compareTo(new Integer(0)) > 0) {
				pattern += ".";
				for(int i = 1; i <= scale; i ++) {
					pattern += IConstants.NUMBER_FORMAT.CURRENCY_ZERO_DECIMAL;
				}
			}
			number = number.setScale(scale, rounding);
			
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
		}
		return result;
	}
	public String getCountryFlag(String countryCode) {
		String CountryName = "";
		return CountryName;
	}

	/**
	 * @return the mode
	 */
	public Integer getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Integer mode) {
		this.mode = mode;
	}
}
