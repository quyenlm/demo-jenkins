
package phn.nts.ams.fe.web.action.deposit;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import phn.com.nts.util.common.FormatHelper;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.business.IExchangerManager;
import phn.nts.ams.fe.business.impl.MasterDataManagerImpl;
import phn.nts.ams.fe.domain.ExchangerInfo;
import phn.nts.ams.fe.domain.ExchangerSymbolInfo;
import phn.nts.ams.fe.model.ExchangerAjaxModel;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


public class ExchangerAJAXAction extends ActionSupport implements ModelDriven<ExchangerAjaxModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ExchangerAJAXAction.class);
	ExchangerAjaxModel model = new ExchangerAjaxModel();	
	private IDepositManager depositManager;
	private IExchangerManager exchangerManager;
	
	public ExchangerAjaxModel getModel() {
		return model;
	}
	public String executeGetRate() {
		try{
			if(!StringUtil.isEmpty(model.getExchangerId())) {
				ExchangerSymbolInfo info = depositManager.getExchangerSymbol(model.getExchangerId());
				if(info != null) {
					String symbol = info.getSymbolCd();
					String fSymbol = symbol.substring(0, 3);
					String tSymbol = symbol.substring(3, symbol.length());
					String toPattern = MasterDataManagerImpl.getInstance().getPattern(tSymbol);
					String fromPattern = MasterDataManagerImpl.getInstance().getPattern(fSymbol);
					
					model.setRate(fSymbol + " = " + FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(fSymbol,MathUtil.parseDouble(info.getSellRate())), toPattern) + " " +tSymbol);
					
					BigDecimal convertedRate = model.getAmount().multiply(MathUtil.parseBigDecimal(info.getSellRate()));
					model.setConvertedAmount(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(tSymbol,convertedRate.doubleValue()), toPattern) + " " + tSymbol);
					ExchangerInfo exchangerInfo = depositManager.getExchanger(model.getExchangerId());
					model.setBankInfo(exchangerInfo.getBankInfo());
					model.setAmountStr(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(tSymbol,model.getAmount().doubleValue()), fromPattern) );
				}			
			}		
		} catch (Exception ex){
			logger.error(ex.getMessage(), ex);
		}
		return SUCCESS;
	}
	
	public String executeGetRateFromWithdrawal() {
		try{
			List<ExchangerSymbolInfo> listInfo = exchangerManager.getExchangerSymbolByExchangerId(model.getExchangerId());
			ExchangerSymbolInfo info = null;
			String symbolCd = "";
			String firstMoney = "";
			String lastMoney = "";
			StringBuilder strBuilder = new StringBuilder();
			if (listInfo != null && listInfo.size() > 0) {
				info = listInfo.get(0);
				symbolCd = info.getSymbolCd();
				firstMoney = symbolCd.substring(0, 3);
				lastMoney = symbolCd.substring(3, symbolCd.length());
				String toPattern = MasterDataManagerImpl.getInstance().getPattern(lastMoney);
				//format rate
				strBuilder.append(firstMoney).append(" = ").append(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(lastMoney, MathUtil.parseDouble(info.getBuyRate())), toPattern)).append(" ").append(lastMoney);
				if (model.getAmount() != null) {
					model.setRate(strBuilder.toString());
					BigDecimal convertedAmount = model.getAmount().multiply(MathUtil.parseBigDecimal(info.getBuyRate()));
					model.setConvertedAmount(FormatHelper.formatString(MasterDataManagerImpl.getInstance().currencyRound(lastMoney, convertedAmount.doubleValue()), toPattern) + " " + lastMoney);
				}
			} 
		} catch (Exception ex){
			logger.error(ex.getMessage(), ex);
		}
		return SUCCESS;
		
	}
	
	
	public IDepositManager getDepositManager() {
		return depositManager;
	}
	public void setDepositManager(IDepositManager depositManager) {
		this.depositManager = depositManager;
	}
	/**
	 * @return the exchangerManager
	 */
	public IExchangerManager getExchangerManager() {
		return exchangerManager;
	}
	/**
	 * @param exchangerManager the exchangerManager to set
	 */
	public void setExchangerManager(IExchangerManager exchangerManager) {
		this.exchangerManager = exchangerManager;
	}
	
}