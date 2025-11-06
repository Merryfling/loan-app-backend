-- 更新user_report表结构，替换旧字段为新的ML模型参数
-- 执行前请备份数据库

-- 1. 重命名或删除旧字段（如果有数据需要保留，请先备份）
ALTER TABLE user_report 
DROP COLUMN IF EXISTS overdue_30p_cnt_2y,
DROP COLUMN IF EXISTS open_credit_lines_cnt,
DROP COLUMN IF EXISTS earliest_credit_open_date,
DROP COLUMN IF EXISTS derog_cnt,
DROP COLUMN IF EXISTS public_record_clean_cnt,
DROP COLUMN IF EXISTS housing_status,
DROP COLUMN IF EXISTS potential_loan_purpose,
DROP COLUMN IF EXISTS ext_early_amt_total,
DROP COLUMN IF EXISTS ext_early_cnt_total,
DROP COLUMN IF EXISTS ext_early_amt_3m;

-- 2. 添加新的ML模型参数字段
ALTER TABLE user_report
ADD COLUMN revolving_utilization_of_unsecured_lines DECIMAL(10, 6) COMMENT '除了房贷车贷之外的信用卡账面金额/信用卡总额度' AFTER status,
ADD COLUMN age INT COMMENT '贷款人年龄' AFTER revolving_utilization_of_unsecured_lines,
ADD COLUMN number_of_time_30_to_59_days_past_due_not_worse INT COMMENT '借款人逾期30-59天的次数' AFTER age,
ADD COLUMN debt_ratio DECIMAL(10, 6) COMMENT '负债比率，每月债务、赡养费、生活费/每月总收入' AFTER number_of_time_30_to_59_days_past_due_not_worse,
ADD COLUMN monthly_income DECIMAL(12, 2) COMMENT '月收入' AFTER debt_ratio,
ADD COLUMN number_of_open_credit_lines_and_loans INT COMMENT '开放式信贷和贷款数量' AFTER monthly_income,
ADD COLUMN number_of_times_90_days_late INT COMMENT '借款者有90天或更高逾期的次数' AFTER number_of_open_credit_lines_and_loans,
ADD COLUMN number_real_estate_loans_or_lines INT COMMENT '包括房屋净值信贷额度在内的抵押贷款和房地产贷款数量' AFTER number_of_times_90_days_late,
ADD COLUMN number_of_time_60_to_89_days_past_due_not_worse INT COMMENT '借款人逾期60-89天的次数' AFTER number_real_estate_loans_or_lines,
ADD COLUMN number_of_dependents INT COMMENT '不包括本人在内的家属数量' AFTER number_of_time_60_to_89_days_past_due_not_worse;

-- 3. 验证表结构
DESCRIBE user_report;
