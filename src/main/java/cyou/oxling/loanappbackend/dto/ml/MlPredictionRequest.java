package cyou.oxling.loanappbackend.dto.ml;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ML预测请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionRequest {
    
    /**
     * 特征数组，按顺序包含以下10个特征：
     * 1. RevolvingUtilizationOfUnsecuredLines - 信用卡账面金额/信用卡总额度
     * 2. age - 贷款人年龄
     * 3. NumberOfTime30-59DaysPastDueNotWorse - 逾期30-59天的次数
     * 4. DebtRatio - 负债比率
     * 5. MonthlyIncome - 月收入
     * 6. NumberOfOpenCreditLinesAndLoans - 开放式信贷和贷款数量
     * 7. NumberOfTimes90DaysLate - 90天或更高逾期的次数
     * 8. NumberRealEstateLoansOrLines - 抵押贷款和房地产贷款数量
     * 9. NumberOfTime60-89DaysPastDueNotWorse - 逾期60-89天的次数
     * 10. NumberOfDependents - 家属数量
     */
    private List<Double> features;
}
