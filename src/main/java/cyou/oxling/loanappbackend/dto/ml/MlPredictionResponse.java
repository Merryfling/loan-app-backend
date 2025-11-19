package cyou.oxling.loanappbackend.dto.ml;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ML预测响应DTO
 * 
 * 适配嵌套响应结构：
 * {
 *   "credit_limit": 500,
 *   "details": { "pd": ..., "limit": 500, ... },
 *   "prob_default": 0.47
 * }
 */
@Data
public class MlPredictionResponse {
    
    /**
     * 顶层信用额度（兼容旧版，优先使用 details.limit）
     */
    @JsonProperty("credit_limit")
    private Integer creditLimit;
    
    /**
     * 顶层违约概率（兼容旧版，优先使用 details.pd）
     */
    @JsonProperty("prob_default")
    private BigDecimal probDefault;
    
    /**
     * 详细信息对象（包含所有计算细节）
     */
    private Details details;
    
    /**
     * 获取最终授信额度（优先从 details 取，降级到顶层 credit_limit）
     */
    public Integer getLimit() {
        if (details != null && details.getLimit() != null) {
            return details.getLimit();
        }
        return creditLimit;
    }
    
    /**
     * 获取违约概率（优先从 details 取，降级到顶层 prob_default）
     */
    public BigDecimal getPd() {
        if (details != null && details.getPd() != null) {
            return details.getPd();
        }
        return probDefault;
    }
    
    /**
     * 获取其他字段（从 details 中提取）
     */
    public BigDecimal getBudgetFraction() {
        return details != null ? details.getBudgetFraction() : null;
    }
    
    public BigDecimal getRiskFactor() {
        return details != null ? details.getRiskFactor() : null;
    }
    
    public BigDecimal getExposureEl() {
        return details != null ? details.getExposureEl() : null;
    }
    
    public BigDecimal getExposureAfford() {
        return details != null ? details.getExposureAfford() : null;
    }
    
    public BigDecimal getTierCap() {
        return details != null ? details.getTierCap() : null;
    }
    
    /**
     * 内嵌的详细信息类
     */
    @Data
    public static class Details {
        /**
         * 修正后的违约概率
         */
        private BigDecimal pd;
        
        /**
         * 收入中分配用于风险预算的比例
         */
        @JsonProperty("budget_fraction")
        private BigDecimal budgetFraction;
        
        /**
         * 风险惩罚系数
         */
        @JsonProperty("risk_factor")
        private BigDecimal riskFactor;
        
        /**
         * 基于经济资本（EL）计算的风险驱动额度
         */
        @JsonProperty("exposure_el")
        private BigDecimal exposureEl;
        
        /**
         * 基于可支配收入的偿付能力驱动额度
         */
        @JsonProperty("exposure_afford")
        private BigDecimal exposureAfford;
        
        /**
         * 分层策略给出的收入上限
         */
        @JsonProperty("tier_cap")
        private BigDecimal tierCap;
        
        /**
         * 最终建议授信额度（整数，向百位四舍五入后返回，最低为500）
         */
        private Integer limit;
    }
}
