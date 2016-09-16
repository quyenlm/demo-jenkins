package phn.nts.ams.fe.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import phn.com.nts.db.dao.IAmsIbKickbackDAO;
import phn.com.nts.db.dao.IFxSummaryRateDAO;
import phn.com.nts.db.entity.AmsIbKickback;
import phn.com.nts.db.entity.FxSummaryRate;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.common.memcached.SocialMemcached;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.domain.SymbolInfo;

import com.nts.common.exchange.bean.RateBandInfo;
import com.nts.common.exchange.dealing.FxFrontRateInfo;

public class AbstractManager {
	private static Logit LOG = Logit.getInstance(AbstractManager.class);
	private IFxSummaryRateDAO<FxSummaryRate> fxSummaryRateDAO;
	private IAmsIbKickbackDAO<AmsIbKickback> iAmsIbKickbackDAO;
	
	/**
     * Get exchange rate between two currencies
     * @param fromCurrency The source currency
     * @param toCurrency The destination currency for converting to
     * @param toCurrencyScale The decimal section
     * @param toCurrencyRound The round type
     * @return The exchange rate
     */
	
	public BigDecimal getMidRate(String symbol, int scale, int rounding){
		try {
			FxFrontRateInfo fxFrontRateInfo = SocialMemcached.getInstance().getFrontRateInfo(symbol);
	        if(fxFrontRateInfo != null){
	        	List<RateBandInfo> bidBandInfoList = fxFrontRateInfo.getBidBandInfoList();
	        	List<RateBandInfo> askBandInfoList = fxFrontRateInfo.getAskBandInfoList();
	        	BigDecimal bid = null;
	        	BigDecimal ask = null;
	        	if(bidBandInfoList != null && bidBandInfoList.size() > 0) {
	        		RateBandInfo rateBandInfo = bidBandInfoList.get(0);
	        		if(rateBandInfo != null) {
	        			bid = rateBandInfo.getRate();
	        		}
	        	}
	        	if(askBandInfoList != null && askBandInfoList.size() > 0) {
	        		RateBandInfo rateBandInfo = askBandInfoList.get(0);
	        		if(rateBandInfo != null) {
	        			ask = rateBandInfo.getRate();
	        		}
	        	}
	        	if(bid == null) bid = new BigDecimal("0");
	        	if(ask == null) ask = new BigDecimal("0");
	        
	        	BigDecimal midRate =  ask.add(bid).divide(new BigDecimal("2"), scale, rounding);
	        	LOG.debug(fxFrontRateInfo.toString());
	        	LOG.debug(fxFrontRateInfo.getRateDate() + ", ###  Found rate of symbol " 
	                    +  fxFrontRateInfo.getCurrencyPair() + " bid price = " + fxFrontRateInfo.getBidBandInfoList().get(0).getRate() 
	                    + " ask price= " + fxFrontRateInfo.getAskBandInfoList().get(0).getRate() + " ### midRate = " + midRate);
	        	
	        	return midRate;
	        } else {
	        	BigDecimal frontRate = getSummaryRate(symbol);
	        	if(frontRate != null) {
	        		fxFrontRateInfo = new FxFrontRateInfo();
	        		List<RateBandInfo> bidBandInfoList = fxFrontRateInfo.getBidBandInfoList();
	            	List<RateBandInfo> askBandInfoList = fxFrontRateInfo.getAskBandInfoList();
	            	RateBandInfo rateBandInfo = new RateBandInfo();
	            	rateBandInfo.setRate(frontRate);
	            	bidBandInfoList.add(rateBandInfo);
	            	askBandInfoList.add(rateBandInfo);
	            	SocialMemcached.getInstance().saveFrontRateInfo(fxFrontRateInfo);
	            	return frontRate;
	        	}	        	
	        }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
    	
        return null;
	}
	
	public BigDecimal getFrontRate(String fromCurrencyCode, String toCurrencyCode) {
		BigDecimal frontRate = null;
		String symbolCd = fromCurrencyCode + toCurrencyCode;
		SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
		if(symbolInfo != null) {
			int scale = symbolInfo.getSymbolDecimal();
        	int rounding = symbolInfo.getSymbolRound();
        	frontRate = getMidRate(symbolCd, scale, rounding);
        	if(frontRate != null) {
        		return frontRate;
        	} 
        	symbolCd = toCurrencyCode + fromCurrencyCode;
        	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
        	if(symbolInfo != null) {
        		scale = symbolInfo.getSymbolDecimal();
            	rounding = symbolInfo.getSymbolRound();
            	frontRate = getMidRate(symbolCd, scale, rounding);
            	if(frontRate != null) {
            		return frontRate;
            	}
            	return null;
        	}
		} else {
			symbolCd = toCurrencyCode + fromCurrencyCode;
        	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
        	if(symbolInfo != null) {
        		int scale = symbolInfo.getSymbolDecimal();
            	int rounding = symbolInfo.getSymbolRound();
            	frontRate = getMidRate(symbolCd, scale, rounding);
            	if(frontRate != null) {
            		return frontRate;
            	}
            	symbolCd = fromCurrencyCode + toCurrencyCode;
            	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
            	if(symbolInfo != null) {
            		scale = symbolInfo.getSymbolDecimal();
                	rounding = symbolInfo.getSymbolRound();
                	frontRate = getMidRate(symbolCd, scale, rounding);
                	if(frontRate != null) {
                		return frontRate;
                	}
                	return null;
            	}
        	}
		}
		return frontRate;
	}
	
	public BigDecimal getSummaryRate(String symbolCd) {
		BigDecimal rate = null;
		FxSummaryRate fxSummaryRate = fxSummaryRateDAO.getFxSummaryRate(symbolCd);
		if(fxSummaryRate != null) {
			rate = fxSummaryRate.getClosePrice();
		}
		return rate;
	}
	
	public BigDecimal getConvertRate(String fromCurrency, String toCurrency){
		if(toCurrency == null || fromCurrency == null) return MathUtil.parseBigDecimal(0);
		String symbolCd = fromCurrency + toCurrency;
        SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
        if(symbolInfo != null) {
        	int scale = symbolInfo.getSymbolDecimal();
        	int rounding = symbolInfo.getSymbolRound();
        	BigDecimal convertRate = getMidRate(symbolCd, scale, rounding);
        	if(convertRate != null){
        		return convertRate;
        	}
        	
        	symbolCd = toCurrency + fromCurrency;
            convertRate = getMidRate(symbolCd, scale, rounding);
            if(convertRate != null){
            	return MathUtil.parseBigDecimal(1).divide(convertRate, scale, rounding);
            }
        }
        return null;
	}
	
	
    public BigDecimal getConvertRateOnFrontRate(String fromCurrency, String toCurrency, int defaultScale){
        try {
        	BigDecimal convertRate = BigDecimal.ZERO;
//        	int defaultScale = 5;
        	int roundingDefault = BigDecimal.ROUND_DOWN;
        	if(toCurrency == null || fromCurrency == null) return MathUtil.parseBigDecimal(0);
        	
            if(toCurrency.equals(fromCurrency)) return MathUtil.parseBigDecimal(1);
            
            String symbolCd = fromCurrency + toCurrency;
            SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
            if(symbolInfo != null) {
            	int scale = symbolInfo.getSymbolDecimal();
            	int rounding = symbolInfo.getSymbolRound();
            	BigDecimal rate = getMidRate(symbolCd, scale, rounding);
            	if(rate == null) {
            		symbolCd = toCurrency + fromCurrency;
            		symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
                    if(symbolInfo != null) {
                    	scale = symbolInfo.getSymbolDecimal();
                    	rounding = symbolInfo.getSymbolRound();
                    	rate = getMidRate(symbolCd, scale, rounding);
                    	if(rate != null) {
                    		convertRate = MathUtil.parseBigDecimal(1).divide(rate, defaultScale, roundingDefault);
                    	}
                    }
            	} else {
            		convertRate = rate;
            	}
            } else {
            	symbolCd = toCurrency + fromCurrency;
        		symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
                if(symbolInfo != null) {
                	int scale = symbolInfo.getSymbolDecimal();
                	int rounding = symbolInfo.getSymbolRound();
                	BigDecimal rate = getMidRate(symbolCd, scale, rounding);
                	if(rate != null) {
                		convertRate = MathUtil.parseBigDecimal(1).divide(rate, defaultScale, roundingDefault);
                	}
                	
                } else {
                	symbolCd = fromCurrency + toCurrency;
                	symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
                	if(symbolInfo != null) {
                		int scale = symbolInfo.getSymbolDecimal();
                		int rounding = symbolInfo.getSymbolRound();
                		convertRate = getMidRate(symbolCd, scale, rounding);
                	}
                }
            }
            if(BigDecimal.ZERO.equals(convertRate)) {
            	LOG.warn("cannot find convert rate in memcached with fromCurrency = " + fromCurrency + ", toCurrency = " + toCurrency);
            	LOG.info("[start] get rate from FX_SUMMARY_RATE with fromCurrency = " + fromCurrency + ", toCurrency = " + toCurrency);
            	convertRate = getConvertRateOnSummary(fromCurrency, toCurrency, defaultScale);            	
            	LOG.info("[end] get rate from FX_SUMMARY_RATE with fromCurrency = " + fromCurrency + ", toCurrency = " + toCurrency + ", convertRate = " + convertRate);
            }
            return convertRate;
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
        	return BigDecimal.ZERO;
        }
    }
    private void saveSummaryRate(String symbolCd, BigDecimal rate) {
    	FxFrontRateInfo fxFrontRateInfo = new FxFrontRateInfo();
    	fxFrontRateInfo.setCurrencyPair(symbolCd);
    	List<RateBandInfo> bidBandInfoList = new ArrayList<RateBandInfo>();
    	RateBandInfo rateBandInfo = new RateBandInfo();
    	rateBandInfo.setRate(rate);
    	bidBandInfoList.add(rateBandInfo);
    	List<RateBandInfo> askBandInfoList = new ArrayList<RateBandInfo>();
    	rateBandInfo = new RateBandInfo();
    	rateBandInfo.setRate(rate);
    	askBandInfoList.add(rateBandInfo);
    	fxFrontRateInfo.setAskBandInfoList(askBandInfoList);
    	fxFrontRateInfo.setBidBandInfoList(bidBandInfoList);
    	SocialMemcached.getInstance().saveFrontRateInfo(fxFrontRateInfo);
    }
    public BigDecimal getConvertRateOnSummary(String fromCurrency, String toCurrency, int defaultScale) {
    	BigDecimal convertRate = BigDecimal.ZERO;
    	int roundingDefault = BigDecimal.ROUND_DOWN;
        String symbolCd = fromCurrency + toCurrency;
        BigDecimal rate = getSummaryRate(symbolCd);
        if(rate != null && rate.compareTo(new BigDecimal("0")) > 0) {
        	LOG.info("[start] insert rate into memecached with symbolCd = " + symbolCd);
        	saveSummaryRate(symbolCd, rate);
        	LOG.info("[end] insert rate into memecached with symbolCd = " + symbolCd);
        	convertRate = rate;
        } else {
        	symbolCd = toCurrency + fromCurrency;
        	rate = getSummaryRate(symbolCd);
        	if(rate != null && rate.compareTo(new BigDecimal("0")) > 0) {
        		LOG.info("[start] insert rate into memecached with symbolCd = " + symbolCd);
            	saveSummaryRate(symbolCd, rate);
            	LOG.info("[end] insert rate into memecached with symbolCd = " + symbolCd);
        		convertRate =  MathUtil.parseBigDecimal(1).divide(rate, defaultScale, roundingDefault);
            }
        }
		return convertRate;
    }
	
    public BigDecimal getConvertRateOnFrontRate(String fromCurrency, String toCurrency){
        try {
            if(toCurrency.equals(fromCurrency)) return MathUtil.parseBigDecimal(1);
            
            BigDecimal convertRate = getConvertRate(fromCurrency, toCurrency);
            
            if(convertRate != null) return convertRate;
            
            convertRate = getConvertRate(toCurrency, fromCurrency);
            
            if(convertRate != null ){
            	String symbolCd = toCurrency + fromCurrency;
            	SymbolInfo symbolInfo = (SymbolInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.FX_SYMBOL + symbolCd);
                if(symbolInfo != null) {
                	return MathUtil.parseBigDecimal(1).divide(convertRate, symbolInfo.getSymbolDecimal(), symbolInfo.getSymbolRound());
            	}
            }
            
            return MathUtil.parseBigDecimal(0);
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
        	return MathUtil.parseBigDecimal(0);
        }
    }
    public BigDecimal rounding(BigDecimal amount, String currencyCode) {
    	try {
    		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
        	if(currencyInfo != null) {
        		amount = amount.setScale(currencyInfo.getCurrencyDecimal(), currencyInfo.getCurrencyRound());
        	}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
    	return amount;
    }
	/**
	 * @param fxSummaryRateDAO the fxSummaryRateDAO to set
	 */
	public void setFxSummaryRateDAO(
			IFxSummaryRateDAO<FxSummaryRate> fxSummaryRateDAO) {
		this.fxSummaryRateDAO = fxSummaryRateDAO;
	}

	public IAmsIbKickbackDAO<AmsIbKickback> getiAmsIbKickbackDAO() {
		return iAmsIbKickbackDAO;
	}

	public void setiAmsIbKickbackDAO(
			IAmsIbKickbackDAO<AmsIbKickback> iAmsIbKickbackDAO) {
		this.iAmsIbKickbackDAO = iAmsIbKickbackDAO;
	}
	
	
}
