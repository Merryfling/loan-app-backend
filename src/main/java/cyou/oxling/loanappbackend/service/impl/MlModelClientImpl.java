package cyou.oxling.loanappbackend.service.impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cyou.oxling.loanappbackend.dto.ml.MlPredictionRequest;
import cyou.oxling.loanappbackend.dto.ml.MlPredictionResponse;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.user.UserReport;
import cyou.oxling.loanappbackend.service.MlModelClient;
import lombok.RequiredArgsConstructor;

/**
 * ML模型客户端实现类
 */
@Service
@RequiredArgsConstructor
public class MlModelClientImpl implements MlModelClient {
    
    private static final Logger logger = LoggerFactory.getLogger(MlModelClientImpl.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${ml.api.url:http://127.0.0.1:5000/predict}")
    private String mlApiUrl;
    
    @Override
    public MlPredictionResponse predict(List<Double> features) {
        if (features == null || features.size() != 10) {
            throw new BusinessException("特征数组必须包含10个元素");
        }
        
        try {
            MlPredictionRequest request = new MlPredictionRequest(features);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MlPredictionRequest> httpEntity = new HttpEntity<>(request, headers);
            
            logger.debug("调用ML API: {}", mlApiUrl);
            
            ResponseEntity<MlPredictionResponse> response = restTemplate.postForEntity(
                    mlApiUrl, httpEntity, MlPredictionResponse.class);
            
            MlPredictionResponse result = response.getBody();
            if (result == null) {
                throw new BusinessException("ML API返回结果为空");
            }
            
            logger.info("ML评估完成 - limit: {}", result.getLimit());
            return result;
            
        } catch (Exception e) {
            logger.error("调用ML API失败", e);
            throw new BusinessException("调用ML评估服务失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Double> buildFeatures(UserReport userReport) {
        return Arrays.asList(
            toDouble(userReport.getRevolvingUtilizationOfUnsecuredLines()),
            toDouble(userReport.getAge()),
            toDouble(userReport.getNumberOfTime30To59DaysPastDueNotWorse()),
            toDouble(userReport.getDebtRatio()),
            toDouble(userReport.getMonthlyIncome()),
            toDouble(userReport.getNumberOfOpenCreditLinesAndLoans()),
            toDouble(userReport.getNumberOfTimes90DaysLate()),
            toDouble(userReport.getNumberRealEstateLoansOrLines()),
            toDouble(userReport.getNumberOfTime60To89DaysPastDueNotWorse()),
            toDouble(userReport.getNumberOfDependents())
        );
    }
    
    private Double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }
}
