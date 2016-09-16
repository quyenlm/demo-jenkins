package phn.nts.ams.fe.promotion;

import java.math.BigDecimal;

import phn.com.nts.db.entity.AmsPromotion;
import phn.com.nts.db.entity.AmsPromotionCustomer;

public interface IPromotionManager {
	//public BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionType, String wlCode);
	public BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionType, Integer serviceType, Integer subGroupId);
	//public AmsPromotion getAmsPromotion(Integer promotionType, String wlCode);
	public AmsPromotion getAmsPromotion(Integer promotionType, Integer serviceType, Integer subGroupId);
	public void saveAmsPromotionCustomer(String customerId, Integer promotionId, Double amount, String refId, String currencyCode);
	public AmsPromotionCustomer getAmsPromotionCustomer(String customerId, String sourceId, Integer promotionId);
	public BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionType, Integer serviceType, Integer subGroupId, BigDecimal netDeposit, String customerId, boolean isDeposit);
}
