package phn.nts.social.fe.web.action;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import phn.com.nts.ams.web.condition.RankingSearchCondition;
import phn.com.nts.ams.web.condition.RankingTraderInfo;
import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.com.trs.util.common.ITrsConstants;
import phn.nts.ams.fe.model.RankingModel;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/7/13 10:21 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class RankingAction extends BaseSocialAction<RankingModel> {

    private RankingModel model = new RankingModel();
    private static Logit LOG = Logit.getInstance(RankingAction.class);
    private String dispatch;

    public RankingModel getModel() {
        return model;
    }

    public void setModel(RankingModel model) {
        this.model = model;
    }

    public String searchTrader(){
        try{
        	initMapServiceTypes();
            if(model.getCondition() == null){
             	
                RankingSearchCondition condition = new RankingSearchCondition();
                //condition.setRankingCriteria(RankingSearchCondition.RETURN_CRITERIA);
                model.setCondition(condition);
            }
            model.setMapCountry(socialManager.getListCountryInfo());
            PagingInfo pagingInfo = model.getPagingInfo();
            if(StringUtil.isEmpty(model.getMinTrades())) model.setMinTrades(RankingSearchCondition.MIN_TRADES.toString());
            if(StringUtil.isEmpty(model.getMinWinRatio())) model.setMinWinRatio(RankingSearchCondition.MIN_WIN_RATIO.toString());
            if(StringUtil.isEmpty(model.getMaxWinRatio())) model.setMaxWinRatio(RankingSearchCondition.MAX_WIN_RATIO.toString());
            if(StringUtil.isEmpty(model.getWinRatio())) model.setWinRatio(RankingSearchCondition.MIN_WIN_RATIO.toString() + "%-" + RankingSearchCondition.MAX_WIN_RATIO.toString() + "%");
            if(StringUtil.isEmpty(model.getReturnRatio())) model.setReturnRatio(RankingSearchCondition.MIN_RETURN.toString() + "%-" + RankingSearchCondition.MAX_RETURN.toString() + "%");
            if(StringUtil.isEmpty(model.getMinReturn())) model.setMinReturn(RankingSearchCondition.MIN_RETURN.toString());
            if(StringUtil.isEmpty(model.getMaxReturn())) model.setMaxReturn(RankingSearchCondition.MAX_RETURN.toString());
            if(StringUtil.isEmpty(model.getPips())) model.setPips(RankingSearchCondition.PIPS.toString());
            if(StringUtil.isEmpty(model.getAveragePips())) model.setAveragePips(RankingSearchCondition.AVG_PIPS.toString());
            if(StringUtil.isEmpty(model.getMaxDrawdown())) model.setMaxDrawdown(RankingSearchCondition.MIN_MAX_DRAWDOWN.toString() + "%-" + RankingSearchCondition.MAX_MAX_DRAWDOWN.toString() + "%");
            if(StringUtil.isEmpty(model.getMinMaxDrawdown())) model.setMinMaxDrawdown(RankingSearchCondition.MIN_MAX_DRAWDOWN.toString());
            if(StringUtil.isEmpty(model.getMaxMaxDrawdown())) model.setMaxMaxDrawdown(RankingSearchCondition.MAX_MAX_DRAWDOWN.toString());
            if(StringUtil.isEmpty(model.getAverageMargin())) model.setAverageMargin(RankingSearchCondition.MIN_AVG_MARGIN.toString() + "%-" + RankingSearchCondition.MAX_AVG_MARGIN.toString() + "%");
            if(StringUtil.isEmpty(model.getMinAvgMargin())) model.setMinAvgMargin(RankingSearchCondition.MIN_AVG_MARGIN.toString());
            if(StringUtil.isEmpty(model.getMaxAvgMargin())) model.setMaxAvgMargin(RankingSearchCondition.MAX_AVG_MARGIN.toString());
            if(StringUtil.isEmpty(model.getPeriod())) model.setPeriod(ITrsConstants.RANKING_PERIOD.PERIOD_1);
            
            String[] winRatio = model.getWinRatio().replace("%", "").split("-");
            if(winRatio != null && winRatio.length > 0)
                model.setMinWinRatio(winRatio[0]);
            if(winRatio != null && winRatio.length > 1)
                model.setMaxWinRatio(winRatio[1]);
            String[] returnRatio = model.getReturnRatio().replace("%", "").split("-");
            if(returnRatio != null && returnRatio.length > 0){
            	model.setMinReturn(returnRatio[0]);
            }
            if(returnRatio != null && returnRatio.length > 1){
                model.setMaxReturn(returnRatio[1]);
            }
            String[] maxDrawdown = model.getMaxDrawdown().replace("%", "").split("-");
            if(maxDrawdown != null && maxDrawdown.length > 0){
            	model.setMinMaxDrawdown(maxDrawdown[0]);
            }
            if(maxDrawdown != null && maxDrawdown.length > 1){
                model.setMaxMaxDrawdown(maxDrawdown[1]);
            }
            String[] avgMargin = model.getAverageMargin().replace("%", "").split("-");
            if(avgMargin != null && avgMargin.length > 0){
            	model.setMinAvgMargin(avgMargin[0]);
            }
            if(avgMargin != null && avgMargin.length > 1){
                model.setMaxAvgMargin(avgMargin[1]);
            }
            
//            if(!StringUtil.isEmpty(dispatch) && dispatch.equals("search") && !StringUtil.isEmpty(model.getUserName())){
            if(!StringUtil.isEmpty(model.getUserName()) && (StringUtil.isEmpty(dispatch) || (!StringUtil.isEmpty(dispatch) && dispatch.equals("search")))){
                model.getCondition().setUserName(model.getUserName());
                model.getCondition().setPeriod(model.getPeriod());
            } else {
            	model.getCondition().setPeriod(model.getPeriod());
            	model.getCondition().setMinReturn(MathUtil.parseBigDecimal(model.getMinReturn(),null));
            	model.getCondition().setMaxReturn(MathUtil.parseBigDecimal(model.getMaxReturn(),null));
            	model.getCondition().setPips(MathUtil.parseBigDecimal(model.getPips(),null));
            	model.getCondition().setAvgPips(MathUtil.parseBigDecimal(model.getAveragePips(),null));
            	model.getCondition().setMinMaxDrawdown(MathUtil.parseBigDecimal(model.getMinMaxDrawdown(),null));
            	model.getCondition().setMaxMaxDrawdown(MathUtil.parseBigDecimal(model.getMaxMaxDrawdown(),null));
            	model.getCondition().setMinAvgMargin(MathUtil.parseBigDecimal(model.getMinAvgMargin(),null));
            	model.getCondition().setMaxAvgMargin(MathUtil.parseBigDecimal(model.getMaxAvgMargin(),null));
                model.getCondition().setMinTrades(MathUtil.parseBigDecimal(model.getMinTrades(), null));
                model.getCondition().setMinWinRatio(MathUtil.parseBigDecimal(model.getMinWinRatio(), null));
                model.getCondition().setMaxWinRatio(MathUtil.parseBigDecimal(model.getMaxWinRatio(), null));
//                model.getCondition().setListServiceType(model.getListServiceType());
            }
            if(pagingInfo == null) {
                pagingInfo = new PagingInfo();
                pagingInfo.setOffset(ITrsConstants.PAGING.SC_RANKING_PAGE_SIZE_SCREEN);
                model.setPagingInfo(pagingInfo);
            }
            if(!StringUtil.isEmpty(dispatch) && (dispatch.equals("search") || dispatch.equals("advanced_search"))){
                model.getPagingInfo().setIndexPage(1);
                if(dispatch.equals("search") && !StringUtil.isEmpty(model.getUserName())){
                	model.getCondition().setPaging(true);
                }
            }
            // In case user click on another page
            if(!StringUtil.isEmpty(model.getUserName()) && (StringUtil.isEmpty(dispatch) || (!StringUtil.isEmpty(dispatch) && dispatch.equals("search")))){
            	model.getCondition().setPaging(true);
            } else {
            	model.getPagingInfo().setIndexPage(1);
            }
            
            List<RankingTraderInfo> listRanking = socialManager.getRankingData(model.getCondition(), model.getPagingInfo(), getWlCode());
           
            // only for TRS
            model.getCondition().setFrontDate(DateUtil.convertBetweenDateFormat(model.getCondition().getFrontDate(), DateUtil.PATTERN_YYYYMMDD_BLANK, getText("nts.ams.fe.label.date.pattern")));
            List<String> listDemoAcc = socialManager.getListGroupDemo(ITrsConstants.TRS_CONSTANT.TRS_WL_CODE);
            for (RankingTraderInfo rankingTraderInfo : listRanking) {
            	if(listDemoAcc.contains(rankingTraderInfo.getSubGroupCd())){
            		rankingTraderInfo.setAccountKind(0);
            	}else{
            		rankingTraderInfo.setAccountKind(1);
            	}
			}
            model.setRankingDetails(listRanking);
            model.setStartRankNumber((model.getPagingInfo().getIndexPage()-1) * model.getPagingInfo().getOffset());
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private void initMapServiceTypes() {
  		Map<String, String> map = new TreeMap<String, String>();

  		map.put(IConstants.SERVICES_TYPE.SOCIAL_FX.toString(), getText("nts.socialtrading.scfe023.label.service.type.social_trade"));
  		map.put(IConstants.SERVICES_TYPE.FX.toString(), getText("nts.socialtrading.scfe023.label.service.type.mt4_real"));
  		map.put(IConstants.SERVICES_TYPE.DEMO_FXCD.toString(), getText("nts.socialtrading.scfe023.label.service.type.mt4_demo"));
  		
  		model.setMapServiceTypes(map);
  		
  	}

  	public String getDispatch() {
          return dispatch;
      }

    public void setDispatch(String dispatch) {
        this.dispatch = dispatch;
    }
/*	public String getDefaultServices() {
		return "1,2";
	}*/
}
