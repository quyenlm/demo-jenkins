package phn.nts.ams.fe.promotion.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;

import phn.com.nts.db.dao.IAmsPromotionBaseCcyDAO;
import phn.com.nts.db.dao.IAmsPromotionCustomerDAO;
import phn.com.nts.db.dao.IAmsPromotionDAO;
import phn.com.nts.db.entity.AmsPromotion;
import phn.com.nts.db.entity.AmsPromotionBaseCcy;
import phn.com.nts.db.entity.AmsPromotionCustomer;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.common.FrontEndContext;
import phn.nts.ams.fe.domain.CurrencyInfo;
import phn.nts.ams.fe.promotion.IPromotionManager;

public class PromotionManagerImpl implements IPromotionManager {
	private static Logit LOG = Logit.getInstance(PromotionManagerImpl.class);
	private IAmsPromotionDAO<AmsPromotion> iAmsPromotionDAO;
	private IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> iAmsPromotionBaseCcyDAO;
	private IAmsPromotionCustomerDAO<AmsPromotionCustomer> iAmsPromotionCustomerDAO;
	
	public AmsPromotion getAmsPromotion(Integer promotionType, Integer serviceType, Integer subGroupId) {
		AmsPromotion amsPromotion = null;
		try {
			//amsPromotion = getiAmsPromotionDAO().getAmsPromotion(promotionType, wlCode);
			amsPromotion = getiAmsPromotionDAO().getAmsPromotion(promotionType, serviceType, subGroupId);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return amsPromotion;
	}
	public BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionType, Integer serviceType, Integer subGroupId) {
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		try{			
			LOG.info("Checking promotion " + ", currencyCode: " + currencyCode + ", amount: " + amount + ", promotionType: " + promotionType + ", subGroupId: " + subGroupId);
			//AmsPromotion amsPromotion  =  getiAmsPromotionDAO().getAmsPromotion(promotionType, wlCode);
			AmsPromotion amsPromotion  =  getiAmsPromotionDAO().getAmsPromotion(promotionType, serviceType, subGroupId);
			if(amsPromotion != null){																
				AmsPromotionBaseCcy amsPromotionBaseCcy = getiAmsPromotionBaseCcyDAO().getPromotionBaseCcy(amount, currencyCode, amsPromotion.getPromotionId());
				if(amsPromotionBaseCcy != null) {
					if(amsPromotionBaseCcy.getBonusPercent() != null) {
						bonusAmount = amount.multiply(amsPromotionBaseCcy.getBonusPercent().divide(MathUtil.parseBigDecimal(100)));
					} else if(amsPromotionBaseCcy.getBonusAmount() != null) {
						bonusAmount = amsPromotionBaseCcy.getBonusAmount();
					}
				} else {
					LOG.info("Can not get promotion " + amsPromotion.getPromotionId() + " for currency " + currencyCode + " with amount = " + amount);
				}
			} else {
				LOG.info("Can not get promotion ");
			}
			LOG.info("End Checking promotion " + ", currencyCode: " + currencyCode + ", amount: " + amount + ", promotionType: " + promotionType + ", subGroupId: " + subGroupId);
		} catch(Exception ex) {						  
			LOG.error(ex.toString(), ex);
		}
		LOG.info("bonus Deposit = " +bonusAmount);		
		LOG.info("[start] rounding of bonusAmount = " + bonusAmount);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		Integer scale = currencyInfo.getCurrencyDecimal();
		Integer rounding = currencyInfo.getCurrencyRound();
		bonusAmount = bonusAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		LOG.info("[end] rounding of bonusAmount => " + bonusAmount);
		return bonusAmount;
	}
	public void saveAmsPromotionCustomer(String customerId, Integer promotionId, Double amount, String sourceId, String currencyCode) {
		AmsPromotionCustomer amsPromotionCustomer = new AmsPromotionCustomer();
		amsPromotionCustomer.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsPromotionCustomer.setAmount(amount);
		amsPromotionCustomer.setSourceId(sourceId);
		amsPromotionCustomer.setInputDate(new Timestamp(System.currentTimeMillis()));
		amsPromotionCustomer.setCustomerId(customerId);
		amsPromotionCustomer.setPromotionId(promotionId);
		//[NatureForex1.0-HuyenMT]Sep 11, 2012A - Start 
		amsPromotionCustomer.setCurrencyCode(currencyCode);
		//[NatureForex1.0-HuyenMT]Sep 11, 2012A - End
		getiAmsPromotionCustomerDAO().save(amsPromotionCustomer);
	}
	public AmsPromotionCustomer getAmsPromotionCustomer(String customerId, String sourceId, Integer promotionId) {
		AmsPromotionCustomer amsPromotionCustomer = null;
		try {
			amsPromotionCustomer = getiAmsPromotionCustomerDAO().getAmsPromotionCustomer(customerId, sourceId, promotionId);
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
				
		return amsPromotionCustomer;
	}
	
	public BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionType, Integer serviceType, Integer subGroupId, BigDecimal netDeposit, String customerId, boolean isDeposit) {
		BigDecimal bonusAmount = MathUtil.parseBigDecimal(0);
		try{			
			LOG.info("Checking promotion " + ", currencyCode: " + currencyCode + ", amount: " + amount + ", promotionType: " + promotionType + ", subGroupId: " + subGroupId + ", customerId: " + customerId + ", isDeposit: " + isDeposit + ", netDeposit: " + netDeposit + ", serviceType: " + serviceType);
			AmsPromotion amsPromotion  =  getiAmsPromotionDAO().getAmsPromotion(promotionType, serviceType, subGroupId);
			if(amsPromotion != null){				
				if(IConstants.PROMOTION_KIND.BASED_AMOUNT.equals(amsPromotion.getKind())) {
					LOG.info("promotionId = " + amsPromotion.getPromotionId() + " will be calculated by BASED_AMOUNT");
					LOG.info("[start] calculate bonus amount with base amount = " + amount);
					AmsPromotionBaseCcy amsPromotionBaseCcy = getiAmsPromotionBaseCcyDAO().getPromotionBaseCcy(amount, currencyCode, amsPromotion.getPromotionId());
					if(amsPromotionBaseCcy != null) {
						if(amsPromotionBaseCcy.getBonusPercent() != null) {
							bonusAmount = amount.multiply(amsPromotionBaseCcy.getBonusPercent().divide(MathUtil.parseBigDecimal(100)));
						} else if(amsPromotionBaseCcy.getBonusAmount() != null) {
							bonusAmount = amsPromotionBaseCcy.getBonusAmount();
						}
					} else {
						LOG.info("Can not get promotion " + amsPromotion.getPromotionId() + " for currency " + currencyCode + " with amount = " + amount);
						return new BigDecimal("0");
					}
					LOG.info("[end] calculate bonus amount with base amount = " + amount);
				} else {
					LOG.info("promotionId = " + amsPromotion.getPromotionId() + " will be calculated by NET_DEPOSIT");
					AmsPromotionBaseCcy amsPromotionBaseCcy = getiAmsPromotionBaseCcyDAO().getPromotionBaseCcy(amount, currencyCode, amsPromotion.getPromotionId());
					if(amsPromotionBaseCcy != null) {
						LOG.info("check bonus amount with amount = " + amount + ", promotionId = " + amsPromotion.getPromotionId() + ", currencyCode = " + currencyCode);
						boolean isReceiveBonus = checkBonus(amount, currencyCode, amsPromotion.getPromotionId(), customerId, amsPromotionBaseCcy);
						if(!isDeposit) {
							isReceiveBonus = true;
						}
						LOG.info("isReceiveBonus = " + isReceiveBonus);
						if(isReceiveBonus) {
							LOG.info("[start] calculate bonus amount with net deposit = " + netDeposit + ", baseAmount = " + amount);
							BigDecimal baseBonusAmount = getBonusAmount(amount, currencyCode, amsPromotion.getPromotionId(), customerId, amsPromotionBaseCcy);
							BigDecimal netBonusAmount = new BigDecimal("0");
							if(netDeposit.compareTo(new BigDecimal("0")) != 0) {
								AmsPromotionBaseCcy amsPromotionBaseCcyForNet = getiAmsPromotionBaseCcyDAO().getPromotionBaseCcy(netDeposit, currencyCode, amsPromotion.getPromotionId());
								if(amsPromotionBaseCcyForNet != null) {
									LOG.info("PromotionBaseCcyId = " + amsPromotionBaseCcyForNet.getPromotionBaseCcyId());
									netBonusAmount = getBonusAmount(netDeposit, currencyCode, amsPromotion.getPromotionId(), customerId, amsPromotionBaseCcyForNet);
									LOG.info("totalBonusAmount = " + bonusAmount + ", netBonusAmount = " + netBonusAmount);
								}
							}
							
							bonusAmount = baseBonusAmount.subtract(netBonusAmount);
							LOG.info("[start] get bonus amount for validation");
							bonusAmount = getBonusAmount(baseBonusAmount, amsPromotion.getPromotionId(), customerId, currencyCode, bonusAmount);
							LOG.info("[end] get bonus amount for validation");
							LOG.info("bonusAmount = TotalBonusAmount - NetBonusAmount = " + bonusAmount);
							LOG.info("[end] calculate bonus amount with net deposit = " + netDeposit + ", baseAmount = " + amount);
						} else {
							LOG.info("currencyCode: " + currencyCode + ", amount: " + amount + ", promotionType: " + promotionType + ", subGroupId: " + subGroupId + " cannot receive amount");
							return new BigDecimal("0");
						}
					} else {
						LOG.info("Can not get promotion " + amsPromotion.getPromotionId() + " for currency " + currencyCode + " with amount = " + amount);
						BigDecimal baseBonusAmount = new BigDecimal("0");
						BigDecimal netBonusAmount = new BigDecimal("0");
						
						if(netDeposit.compareTo(new BigDecimal("0")) != 0) {
							AmsPromotionBaseCcy amsPromotionBaseCcyForNet = getiAmsPromotionBaseCcyDAO().getPromotionBaseCcy(netDeposit, currencyCode, amsPromotion.getPromotionId());
							if(amsPromotionBaseCcyForNet != null) {
								LOG.info("PromotionBaseCcyId = " + amsPromotionBaseCcyForNet.getPromotionBaseCcyId());
								netBonusAmount = getBonusAmount(netDeposit, currencyCode, amsPromotion.getPromotionId(), customerId, amsPromotionBaseCcyForNet);
								LOG.info("totalBonusAmount = " + bonusAmount + ", netBonusAmount = " + netBonusAmount);
							}
						}
						
						bonusAmount = baseBonusAmount.subtract(netBonusAmount);
						if(isDeposit) {
							LOG.info("[start] get bonus amount for validation");
							bonusAmount = getBonusAmount(baseBonusAmount, amsPromotion.getPromotionId(), customerId, currencyCode, bonusAmount);
							LOG.info("[end] get bonus amount for validation");
						}
						
						LOG.info("bonusAmount = TotalBonusAmount - NetBonusAmount = " + bonusAmount);
						
//						return new BigDecimal("0");
					}
				}
			} else {
				LOG.info("Can not get promotion ");
				return new BigDecimal("0");
			}
			LOG.info("End Checking promotion " + ", currencyCode: " + currencyCode + ", amount: " + amount + ", promotionType: " + promotionType + ", subGroupId: " + subGroupId);
		} catch(Exception ex) {						  
			LOG.error(ex.toString(), ex);
			return new BigDecimal("0");
		}
		LOG.info("bonus Deposit = " + bonusAmount);		
		LOG.info("[start] rounding of bonusAmount = " + bonusAmount);
		CurrencyInfo currencyInfo = (CurrencyInfo) FrontEndContext.getInstance().getMapConfiguration(IConstants.SYSTEM_CONFIG_KEY.SYS_CURRENCY + currencyCode);
		Integer scale = currencyInfo.getCurrencyDecimal();
		Integer rounding = currencyInfo.getCurrencyRound();
		bonusAmount = bonusAmount.divide(MathUtil.parseBigDecimal(1), scale, rounding);
		LOG.info("[end] rounding of bonusAmount => " + bonusAmount);
		return bonusAmount;
	}
	private BigDecimal getReceivedAmount(Integer promotionId, String customerId) {
		BigDecimal receiveAmount = new BigDecimal("0");
		receiveAmount = iAmsPromotionCustomerDAO.sumReceiveAmount(customerId, promotionId);
		return receiveAmount;
	}
	private BigDecimal getBonusAmount(BigDecimal bonusBaseAmount, Integer promotionId, String customerId, String currencyCode, BigDecimal bonusAmount) {
		BigDecimal totalReceivedAmount = getReceivedAmount(promotionId, customerId);
		BigDecimal maxBonusInLvOne = getMaxBonusInLvOne(promotionId, currencyCode);
		if(totalReceivedAmount.compareTo(maxBonusInLvOne) < 0 && bonusBaseAmount.compareTo(maxBonusInLvOne) <= 0) {
			BigDecimal tmp = maxBonusInLvOne.subtract(totalReceivedAmount);
			if(tmp.compareTo(bonusAmount) <= 0) {
				return tmp;
			}
			return bonusAmount;
		} 
		return bonusAmount;
	}
	private BigDecimal getBonusAmountInRange(BigDecimal amount, AmsPromotionBaseCcy amsPromotionBaseCcy) {
		BigDecimal bonusAmount = new BigDecimal("0");
		BigDecimal minAmount = amsPromotionBaseCcy.getMinAmount();
		BigDecimal amountInRange = amount.subtract(minAmount);
		if(amsPromotionBaseCcy.getBonusPercent() != null && amsPromotionBaseCcy.getBonusPercent().compareTo(new BigDecimal("0")) > 0) {
			bonusAmount = amountInRange.multiply(amsPromotionBaseCcy.getBonusPercent().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
		} else {
			LOG.warn("BONUS PERCENT is null or ZERO with PromotionId = " + amsPromotionBaseCcy.getPromotionId() + ", amount =  " + amount + ", currencyCode = " + amsPromotionBaseCcy.getCurrencyCode() + ", promotionBaseCcyId = " + amsPromotionBaseCcy.getPromotionBaseCcyId()) ;
			return new BigDecimal("0");
		}
		return bonusAmount;
	}
	private BigDecimal getPreviousAmount(BigDecimal amount, String currencyCode, Integer promotionId) {
		BigDecimal bonusAmount = new BigDecimal("0");
		bonusAmount = iAmsPromotionBaseCcyDAO.getBonusPreviousRange(amount, currencyCode, promotionId);
		return bonusAmount;
	}
	private BigDecimal getMaxBonusInLvOne(Integer promotionId, String currencyCode) {
		BigDecimal maxBonusAmount = new BigDecimal("0");
		maxBonusAmount = iAmsPromotionBaseCcyDAO.getMaxBonusInLvOne(currencyCode, promotionId);		
		return maxBonusAmount;
	}
	private BigDecimal getBonusAmount(BigDecimal amount, String currencyCode, Integer promotionId, String customerId, AmsPromotionBaseCcy amsPromotionBaseCcy) {
		BigDecimal bonusAmount = new BigDecimal("0");
		try {
			BigDecimal bonusAmountInRange = getBonusAmountInRange(amount, amsPromotionBaseCcy);
			BigDecimal previousBonus = getPreviousAmount(amount, currencyCode, promotionId);
			bonusAmount = bonusAmountInRange.add(previousBonus);
			LOG.info("bonusAmountInRange = " + bonusAmountInRange + ", previousBonus = " + previousBonus + ", bonusAmount = " + bonusAmount);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return bonusAmount;
	}
	private boolean checkBonus(BigDecimal amount, String currencyCode, Integer promotionId, String customerId, AmsPromotionBaseCcy amsPromotionBaseCcy) {
		BigDecimal totalReceivedAmount = getReceivedAmount(promotionId, customerId);
		BigDecimal maxBonusInLvOne = getMaxBonusInLvOne(promotionId, currencyCode);
		BigDecimal bonusAmount = getBonusAmount(amount, currencyCode, promotionId, customerId, amsPromotionBaseCcy);
		if(totalReceivedAmount.compareTo(maxBonusInLvOne) >= 0 && bonusAmount.compareTo(maxBonusInLvOne) <= 0) {
			return false;
		}
		return true;
	}
	/*private void registerPromotionCustomer(String customerId, String wlCode, String currencyCode, String transferMoneyId, Double creditAmount){
		AmsPromotionCustomer amsPromotionCustomer = new AmsPromotionCustomer();
		AmsPromotion amsPromotion = getiAmsPromotionDAO().getAmsPromotion(IConstants.PROMOTION_TYPE.DEPOSIT, wlCode);
		amsPromotionCustomer.setCustomerId(customerId);
		amsPromotionCustomer.setPromotionId(amsPromotion.getPromotionId());
		amsPromotionCustomer.setAmsPromotion(amsPromotion);
		amsPromotionCustomer.setSourceId(transferMoneyId);
		amsPromotionCustomer.setAmount(creditAmount);
		amsPromotionCustomer.setCurrencyCode(currencyCode);
		amsPromotionCustomer.setActiveFlg(IConstants.ACTIVE_FLG.ACTIVE);
		amsPromotionCustomer.setInputDate(new java.sql.Timestamp(System.currentTimeMillis()));
		amsPromotionCustomer.setUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
	}*/

	/**
	 * @return the iAmsPromotionDAO
	 */
	public IAmsPromotionDAO<AmsPromotion> getiAmsPromotionDAO() {
		return iAmsPromotionDAO;
	}


	/**
	 * @param iAmsPromotionDAO the iAmsPromotionDAO to set
	 */
	public void setiAmsPromotionDAO(IAmsPromotionDAO<AmsPromotion> iAmsPromotionDAO) {
		this.iAmsPromotionDAO = iAmsPromotionDAO;
	}


	/**
	 * @return the iAmsPromotionBaseCcyDAO
	 */
	public IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> getiAmsPromotionBaseCcyDAO() {
		return iAmsPromotionBaseCcyDAO;
	}


	/**
	 * @param iAmsPromotionBaseCcyDAO the iAmsPromotionBaseCcyDAO to set
	 */
	public void setiAmsPromotionBaseCcyDAO(
			IAmsPromotionBaseCcyDAO<AmsPromotionBaseCcy> iAmsPromotionBaseCcyDAO) {
		this.iAmsPromotionBaseCcyDAO = iAmsPromotionBaseCcyDAO;
	}


	/**
	 * @return the iAmsPromotionCustomerDAO
	 */
	public IAmsPromotionCustomerDAO<AmsPromotionCustomer> getiAmsPromotionCustomerDAO() {
		return iAmsPromotionCustomerDAO;
	}


	/**
	 * @param iAmsPromotionCustomerDAO the iAmsPromotionCustomerDAO to set
	 */
	public void setiAmsPromotionCustomerDAO(
			IAmsPromotionCustomerDAO<AmsPromotionCustomer> iAmsPromotionCustomerDAO) {
		this.iAmsPromotionCustomerDAO = iAmsPromotionCustomerDAO;
	}
}
