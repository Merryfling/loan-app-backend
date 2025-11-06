package cyou.oxling.loanappbackend.dto.ml;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 用户自报信息DTO
 */
@Data
public class UserReportDTO {
    
    private Long userId;
    
    // 新的ML模型参数
    private BigDecimal revolvingUtilizationOfUnsecuredLines;  // 除了房贷车贷之外的信用卡账面金额/信用卡总额度
    private Integer age;  // 贷款人年龄
    private Integer numberOfTime30To59DaysPastDueNotWorse;  // 借款人逾期30-59天的次数
    private BigDecimal debtRatio;  // 负债比率，每月债务、赡养费、生活费/每月总收入
    private BigDecimal monthlyIncome;  // 月收入
    private Integer numberOfOpenCreditLinesAndLoans;  // 开放式信贷和贷款数量
    private Integer numberOfTimes90DaysLate;  // 借款者有90天或更高逾期的次数
    private Integer numberRealEstateLoansOrLines;  // 包括房屋净值信贷额度在内的抵押贷款和房地产贷款数量
    private Integer numberOfTime60To89DaysPastDueNotWorse;  // 借款人逾期60-89天的次数
    private Integer numberOfDependents;  // 不包括本人在内的家属数量
} 