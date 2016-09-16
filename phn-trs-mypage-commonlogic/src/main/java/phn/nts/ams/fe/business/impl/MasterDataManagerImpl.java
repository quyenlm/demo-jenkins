package phn.nts.ams.fe.business.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import phn.com.nts.db.dao.IAmsSysCountryDAO;
import phn.com.nts.db.dao.IAmsWhitelabelConfigDAO;
import phn.com.nts.db.dao.IAmsWhitelabelDAO;
import phn.com.nts.db.dao.IFxSymbolDAO;
import phn.com.nts.db.dao.ISysCurrencyDAO;
import phn.com.nts.db.dao.impl.AmsSysCountryDAO;
import phn.com.nts.db.entity.AmsSysCountry;
import phn.com.nts.db.entity.AmsWhitelabel;
import phn.com.nts.db.entity.AmsWhitelabelConfig;
import phn.com.nts.db.entity.AmsWhitelabelConfigId;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.db.entity.SysCurrency;
import phn.com.nts.util.common.FormatHelper;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.Utilities;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.immutable.ImmutableData;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;

public class MasterDataManagerImpl implements TextProvider, LocaleProvider {
	private static final String PATTERN_STRING = "#,###,##0";
	private static final String BASE_CURRENCY = "BASE_CURRENCY";
	
	private final transient TextProvider textProvider = new TextProviderFactory().createInstance(getClass(), this);
	private static IAmsSysCountryDAO<AmsSysCountry> countryDAO;
	private static ISysCurrencyDAO<SysCurrency> currencyDAO;
	private static IAmsWhitelabelDAO<AmsWhitelabel> whitelabelDAO;
	private static IFxSymbolDAO<FxSymbol> symbolCdDAO;
	private static IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> whitelabelConfigDAO;

	private static MasterDataManagerImpl instance;
	private Map<String, SysCurrency> mapCurrency;
	private Map<String, AmsWhitelabel> mapAmsWhitelabel;
	private Map<String, Set<String>> mapSymbolCd;
	// get all symbol information by symbol code
	private Map<String, FxSymbol> mapSymbolRecord;
	private Map<AmsWhitelabelConfigId, AmsWhitelabelConfig> mapWhiteLabelConfig;
	public MasterDataManagerImpl() {
	}
	public static MasterDataManagerImpl getInstance() {
		if (instance == null) {
			instance = new MasterDataManagerImpl();
		}
		
		return instance;
	}
	private Map<Integer, String> mapCountryList;
	private Map<Integer, String> mapCountryCode;
	private Map<Integer, AmsSysCountry> mapCountryRecord;
	private static ImmutableData immutableData;
	/**
	 * getListBySymbolCd　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 13, 2012
	 */
	public Set<String> getListBySymbolCd(String symbolCdSelected) {
		initMapSymbol();

		return mapSymbolCd.get(symbolCdSelected);
	}

	/**
	 * getPatternByCurrencyPair　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 20, 2012
	 */ 
	public String getPatternByCurrencyPair(String currency1, String currency2) {
		if (currency1 != null && currency2 != null && currency1.equals(currency2)) {
			return getPattern(currency1);
		}

		Integer decimal = new Integer(0);
//		Integer rounding = new Integer(0);
		StringBuffer symbolCode = new StringBuffer(currency1).append(currency2);
		FxSymbol fxSymbol = getMapSymbolRecord().get(symbolCode.toString());
		if (fxSymbol == null) {
			symbolCode = new StringBuffer(currency2).append(currency1);
			fxSymbol = getMapSymbolRecord().get(symbolCode.toString());
		}
		if (fxSymbol != null) {
			decimal = fxSymbol.getSymbolDecimal();
		} else {
			decimal = 2;
		}

		StringBuffer format = new StringBuffer(PATTERN_STRING);
		if (decimal > 0) {
			format.append(".");
			for (int i = 0; i < decimal; i++) {
				format.append("0");
			}
		}

		return format.toString();
	}

	/**
	 * getMapSymbol<String, String>　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 13, 2012
	 */
	public Map<String, String> getMapSymbol() {
		initMapSymbol();
		Set<String> set = mapSymbolCd.keySet();
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String s : set) {
			map.put(s, s);
		}
		
		return map;
	}

	/**
	 * init mapSymbol (from database)　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 13, 2012
	 */
	private Map<String, Set<String>> initMapSymbol() {
		if (mapSymbolCd == null) {
			mapSymbolCd = new LinkedHashMap<String, Set<String>>();
			List<FxSymbol> listAll = symbolCdDAO.findAll();
			// [NTS1.0-le.xuan.tuong]Sep 17, 2012 - Start init map symbol record
			initMapSymbol(listAll);
			// [NTS1.0-le.xuan.tuong]Sep 17, 2012 - End init map symbol record
			for (FxSymbol symbol : listAll) {
				String symbolCd = symbol.getSymbolCd();
				
				// get list sub
				Set<String> listSub = null;
				String subSymbol = symbol.getOriginalSymbolCd();
				if (subSymbol == null || subSymbol.trim().length() == 0) {
					if (mapSymbolCd.containsKey(symbolCd)) {
						listSub = mapSymbolCd.get(symbolCd);
					}
					if (listSub == null) {
						listSub = new HashSet<String>();
						listSub.add(symbolCd);
					}
					mapSymbolCd.put(symbolCd, listSub);
				} else {
					listSub = mapSymbolCd.get(subSymbol);
					if (listSub == null) {
						listSub = new HashSet<String>();
						listSub.add(subSymbol);
					}
					listSub.add(symbolCd);
					mapSymbolCd.put(subSymbol, listSub);
				}
			}
		}
		
		return mapSymbolCd;
	}
	/**
	 *  currencyRound
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Aug 31, 2012
	 */
	public double currencyRound(String currencyCode, Double amount) {
		Integer decimal = new Integer(0);
		Integer rounding = new Integer(0);
		
		SysCurrency sysCurrency = getMapCurrency().get(currencyCode);
		if (sysCurrency != null) {
			rounding = sysCurrency.getCurrencyRound();
			decimal = sysCurrency.getCurrencyDecimal();
			amount = MathUtil.parseBigDecimal(amount).divide(MathUtil.parseBigDecimal(1), decimal, rounding).doubleValue();
		}
		return amount;
	}
	
	/**
	 * init mapSymbolRecord
	 * 
	 * @param symbols All symbols from DB
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Sep 17, 2012
	 */
	private void initMapSymbol(List<FxSymbol> symbols) {
		if (mapSymbolRecord == null) {
			mapSymbolRecord = new HashMap<String, FxSymbol>();
			if (!Utilities.isEmptyList(symbols)) {
				for (FxSymbol sym : symbols) {
					mapSymbolRecord.put(sym.getSymbolCd(), sym);
				}
			}
		}
	}
	
	/**
	 * get mapSymbolRecord
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Sep 17, 2012
	 */
	public Map<String, FxSymbol> getMapSymbolRecord() {
		initMapSymbol();
		return mapSymbolRecord;
	}
	
	/**
	 * get Rate format by Symbol code
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Sep 17, 2012
	 */
	public double getRateRound(String currency1, String currency2, Double amount) {
		Integer decimal = new Integer(0);
		Integer rounding = new Integer(0);
		StringBuffer symbolCode = new StringBuffer(currency1).append(currency2);
		FxSymbol fxSymbol = getMapSymbolRecord().get(symbolCode.toString());
		if (fxSymbol == null) {
			symbolCode = new StringBuffer(currency2).append(currency1);
			fxSymbol = getMapSymbolRecord().get(symbolCode.toString());
		}
		if (fxSymbol != null) {
			rounding = fxSymbol.getSymbolRound();
			decimal = fxSymbol.getSymbolDecimal();
			amount = MathUtil.parseBigDecimal(amount).divide(MathUtil.parseBigDecimal(1), decimal, rounding).doubleValue();
		}
		return amount;
	}

	/**
	 * getPattern by currencyCode
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 10, 2012
	 */
	public String getPattern(String currencyCode) {
		SysCurrency sysCurrency = getMapCurrency().get(currencyCode);
		Integer decimal = new Integer(0);
		StringBuffer format = new StringBuffer(PATTERN_STRING);
		if (sysCurrency != null) {
			decimal = sysCurrency.getCurrencyDecimal();
			if (decimal > 0) {
				format.append(".");
				for (int i = 0; i < decimal; i++) {
					format.append("0");
				}
			}
		}
		return format.toString();
	}

	/**
	 * check string isDoubleFormat by currencyCode
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 10, 2012
	 */
	public String isDoubleFormat(String currencyCode, String str) {
		SysCurrency sysCurrency = getMapCurrency().get(currencyCode);
		Integer decimal = new Integer(0);
		StringBuffer format = new StringBuffer("\\d{0,15}");
		str = str.replaceAll("\\,", "");
		if (sysCurrency != null) {
			decimal = sysCurrency.getCurrencyDecimal();
			if (decimal > 0) {
				format.append(".");
				format.append("\\d{0,");
				format.append(decimal);
				format.append("}");
			}

			Boolean isNumber = FormatHelper.isFormatNumber(str, format.toString());
			if (isNumber != null && isNumber.booleanValue()) {
				return null;
			} else {
				return format.toString();
			}
		}

		return format.toString();
	}

	/**
	 * getMapCurrency　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Aug 31, 2012
	 */
	private Map<String, SysCurrency> getMapCurrency() {
		if (mapCurrency == null) {
			mapCurrency = new TreeMap<String, SysCurrency>();
			List<SysCurrency> listCurrency = currencyDAO.findAll();
			for (SysCurrency ams : listCurrency) {
				mapCurrency.put(ams.getCurrencyCode(), ams);
			}
		}
		
		return mapCurrency;
	}

	/**
	 * getSysCurrency　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 11, 2012
	 */
	public SysCurrency getSysCurrency(String currencyCode) {
		return getMapCurrency().get(currencyCode);
	}
	/**
	 * get map Whitelabel　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Aug 31, 2012
	 */
	public Map<String, AmsWhitelabel> getMapAmsWhitelabel() {
		if (mapAmsWhitelabel == null) {
			mapAmsWhitelabel = new TreeMap<String, AmsWhitelabel>();
			List<AmsWhitelabel> listAms = whitelabelDAO.findAll();
			for (AmsWhitelabel ams : listAms) {
				mapAmsWhitelabel.put(ams.getWlCode(), ams);
			}
		}

		return mapAmsWhitelabel;
	}
	
	/**
	 * get map white label config
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Sep 14, 2012
	 */
	public Map<AmsWhitelabelConfigId, AmsWhitelabelConfig> getMapWhiteLabelConfig() {
		if (mapWhiteLabelConfig == null) {
			mapWhiteLabelConfig = new HashMap<AmsWhitelabelConfigId, AmsWhitelabelConfig>();
			List<AmsWhitelabelConfig> listAms = whitelabelConfigDAO.findAll();
			for (AmsWhitelabelConfig ams : listAms) {
				mapWhiteLabelConfig.put(ams.getId(), ams);
			}
		}

		return mapWhiteLabelConfig;
	}

	public void resetMapWhiteLabelConfig() {
		mapWhiteLabelConfig = null;
	}
	
	/**
	 * get base currency by wlCode　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Sep 14, 2012
	 */
	public Map<String, String> getListCurrency(boolean all) {
		String wlCode ="";
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {		
				wlCode = frontUserOnline.getWlCode();
			}
		}		
		
		List<AmsWhitelabelConfig> list = whitelabelConfigDAO.getListWlConfig(BASE_CURRENCY, wlCode);
		
		Map<String, String> mapCurrency = new LinkedHashMap<String, String>();
		if (all) {
			mapCurrency.put("-1", getText("display_object.all"));
		}

		for (AmsWhitelabelConfig config : list) {
			mapCurrency.put(config.getConfigValue(), config.getConfigValue());
		}
		return mapCurrency;
	}
	
	/**
	 * get Map all Country　
	 * 
	 * @param
	 * @return
	 * @throws
	 * @author Nguyen.Xuan.Bach
	 * @CrDate Aug 17, 2012
	 */
	public Map<Integer, String> getMapCountry() {
		if (mapCountryList == null) {
			mapCountryList = new TreeMap<Integer, String>();
			mapCountryRecord = new TreeMap<Integer, AmsSysCountry>(); 
			List< AmsSysCountry > amsSysCountries = countryDAO.findByActiveFlg(1);
//			Map<Integer, String> countryList = new TreeMap<Integer, String>();
			if (amsSysCountries != null && amsSysCountries.size() > 0) {
				for (AmsSysCountry amsSysCountry : amsSysCountries) {
					mapCountryList.put(amsSysCountry.getCountryId(),amsSysCountry.getCountryName());
					mapCountryCode.put(amsSysCountry.getCountryId(),amsSysCountry.getCountryCode());
					mapCountryRecord.put(amsSysCountry.getCountryId(),amsSysCountry);
				}
			}
		}

		return mapCountryList;
	}
	
	public Map<Integer, String> getMapCountryCode() {
		if (mapCountryCode == null) {
			mapCountryCode = new TreeMap<Integer, String>();
			List< AmsSysCountry > amsSysCountries = countryDAO.findByActiveFlg(1);
			if (amsSysCountries != null && amsSysCountries.size() > 0) {
				for (AmsSysCountry amsSysCountry : amsSysCountries) {
					mapCountryCode.put(amsSysCountry.getCountryId(),amsSysCountry.getCountryCode());
				}
			}
		}

		return mapCountryCode;
	}

	/**
	 * get all AmsSysCountry
	 *  
	 * @param
	 * @return mapCountryRecord
	 * @throws
	 * @author le.xuan.tuong
	 * @CrDate Sep 14, 2012
	 */
	public Map<Integer, AmsSysCountry> getMapCountryRecord() {
		if (mapCountryRecord == null) {
			mapCountryRecord = new TreeMap<Integer, AmsSysCountry>(); 
			List<AmsSysCountry> amsSysCountries = countryDAO.findByActiveFlg(1);
			if (amsSysCountries != null && amsSysCountries.size() > 0) {
				for (AmsSysCountry amsSysCountry : amsSysCountries) {
					mapCountryRecord.put(amsSysCountry.getCountryId(), amsSysCountry);
				}
			}
		}

		return mapCountryRecord;
	}

	/****************************************************************************************/
	public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        } else {
            return null;
        }
    }

	public boolean hasKey(String key) {
        return textProvider.hasKey(key);
    }

    public String getText(String aTextName) {
//    	TODO check getText(String aTextName)
//      return textProvider.getText(aTextName);
    	return String.valueOf(SystemPropertyConfig.getErrMsgs().get(aTextName));
    }

    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }

    public ResourceBundle getTexts(String aBundleName) {
        return textProvider.getTexts(aBundleName);
    }
    
    @Override
	public String getText(String arg0, List<?> arg1) {
		return textProvider.getText(arg0, arg1);
	}

	@Override
	public String getText(String arg0, String arg1, List<?> arg2) {
		return textProvider.getText(arg0, arg1, arg2);
	}

	@Override
	public String getText(String arg0, String arg1, List<?> arg2,
			ValueStack arg3) {
		return textProvider.getText(arg0, arg1, arg2, arg3);
	}

	public static void setCountryDAO(AmsSysCountryDAO sysCountryDAO) {
		countryDAO = sysCountryDAO;
	}
	public static void setCurrencyDAO(ISysCurrencyDAO<SysCurrency> sysCurrencyDAO) {
		currencyDAO = sysCurrencyDAO;
	}
	public static void setWhitelabelDAO(IAmsWhitelabelDAO<AmsWhitelabel> whitelabelDAO) {
		MasterDataManagerImpl.whitelabelDAO = whitelabelDAO;
	}
	public static IFxSymbolDAO<FxSymbol> getSymbolCdDAO() {
		return symbolCdDAO;
	}
	public static void setSymbolCdDAO(IFxSymbolDAO<FxSymbol> symbolCdDAO) {
		MasterDataManagerImpl.symbolCdDAO = symbolCdDAO;
	}
	public static IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> getWhitelabelConfigDAO() {
		return whitelabelConfigDAO;
	}
	public static void setWhitelabelConfigDAO(IAmsWhitelabelConfigDAO<AmsWhitelabelConfig> whitelabelConfigDAO) {
		MasterDataManagerImpl.whitelabelConfigDAO = whitelabelConfigDAO;
	}
	public ImmutableData getImmutableData() {
		return immutableData;
	}
	public void setImmutableData(ImmutableData immutableData) {
		MasterDataManagerImpl.immutableData = immutableData;
	}
	

}
