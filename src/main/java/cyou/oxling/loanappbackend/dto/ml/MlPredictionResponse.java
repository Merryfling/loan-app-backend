package cyou.oxling.loanappbackend.dto.ml;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ML预测响应DTO
 */
@Data
public class MlPredictionResponse {
    
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
