package phn.nts.ams.fe.domain.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import phn.com.nts.db.entity.AmsExchanger;
import phn.com.nts.db.entity.AmsExchangerSymbol;
import phn.com.nts.db.entity.FxSymbol;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;

/**
 * @description ExchangerSymbolConverter
 * @version NTS1.0
 * @author Nguyen.Manh.Thang
 * @CrDate Sep 18, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class ExchangerSymbolConverter {
	
	public static ExchangerSymbolInfo toInfo(AmsExchangerSymbol entity) {
		ExchangerSymbolInfo info = new ExchangerSymbolInfo();
		AmsExchanger exchanger = entity.getAmsExchanger();
		FxSymbol symbol = entity.getFxSymbol();
		info.setExchangerSymbolId(entity.getExchangerSymbolId());
		info.setExchangerId(exchanger.getExchangerId());
		info.setSymbolCd(symbol.getSymbolCd());
		
		info.setSellRate(StringUtil.toString(entity.getSellRate()));
		
		info.setBuyRate(StringUtil.toString(entity.getBuyRate()));
		
		info.setOrginalSellRate(StringUtil.toString(entity.getSellRate()));
		info.setOrginalBuyRate(StringUtil.toString(entity.getBuyRate()));
		
//		info.setSellRate(formatNumberByPattern(MathUtil.parseBigDecimal(entity.getSellRate()), IConstants.NUMBER_FORMAT.FORMAT_DEFAULT));
//		info.setBuyRate(formatNumberByPattern(MathUtil.parseBigDecimal(entity.getBuyRate()), IConstants.NUMBER_FORMAT.FORMAT_DEFAULT));
		
//		info.setOrginalSellRate(formatNumberByPattern(MathUtil.parseBigDecimal(entity.getSellRate()), IConstants.NUMBER_FORMAT.FORMAT_DEFAULT));
//		info.setOrginalBuyRate(formatNumberByPattern(MathUtil.parseBigDecimal(entity.getBuyRate()), IConstants.NUMBER_FORMAT.FORMAT_DEFAULT));
		
		info.setUpdateDate(DateUtil.toString(entity.getUpdateDate(), DateUtil.PATTERN_MMDDYYYY_HHMMSS));
		return info;
	}
	public static String formatNumberByPattern(BigDecimal number, String pattern) {
		String result = "";
		if(number != null) {
			DecimalFormat formater = new DecimalFormat(pattern);
			result = formater.format(number);
		}
		return result;
	}
}
