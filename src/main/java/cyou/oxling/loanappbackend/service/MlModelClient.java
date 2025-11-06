package cyou.oxling.loanappbackend.service;

import java.util.List;

import cyou.oxling.loanappbackend.dto.ml.MlPredictionResponse;
import cyou.oxling.loanappbackend.model.user.UserReport;

/**
 * ML模型客户端接口
 */
public interface MlModelClient {
    
    /**
     * 调用ML模型进行信用评估
     * 
     * @param features 特征数组，按顺序包含10个特征值
     * @return ML预测响应，包含信用额度等信息
     */
    MlPredictionResponse predict(List<Double> features);
    
    /**
     * 从用户自报信息构建特征数组
     * 
     * @param userReport 用户自报信息
     * @return 特征数组
     */
    List<Double> buildFeatures(UserReport userReport);
}
