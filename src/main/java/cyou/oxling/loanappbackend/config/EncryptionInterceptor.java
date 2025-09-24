package cyou.oxling.loanappbackend.config;

import cyou.oxling.loanappbackend.annotation.Encrypted;
import cyou.oxling.loanappbackend.util.AESUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * 简单的加密拦截器
 * 进数据库：自动加密标记了@Encrypted的字段
 * 出数据库：自动解密标记了@Encrypted的字段
 */
@Component
@Intercepts({
    @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class),
    @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class)
})
public class EncryptionInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionInterceptor.class);

    @Autowired
    private AESUtil aesUtil;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        
        if (target instanceof ParameterHandler) {
            // 进数据库前：加密
            handleEncryption(invocation);
        } else if (target instanceof ResultSetHandler) {
            // 出数据库后：解密
            Object result = invocation.proceed();
            handleDecryption(result);
            return result;
        }
        
        return invocation.proceed();
    }

    /**
     * 进数据库前：加密标记了@Encrypted的字段
     */
    private void handleEncryption(Invocation invocation) throws Throwable {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        
        Field field = parameterHandler.getClass().getDeclaredField("parameterObject");
        field.setAccessible(true);
        Object parameterObject = field.get(parameterHandler);
        
        if (parameterObject != null) {
            processObject(parameterObject, true); // true表示加密
        }
    }

    /**
     * 出数据库后：解密标记了@Encrypted的字段
     */
    private void handleDecryption(Object result) {
        if (result instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) result;
            for (Object item : list) {
                processObject(item, false); // false表示解密
            }
        } else if (result != null) {
            processObject(result, false); // false表示解密
        }
    }

    /**
     * 处理对象的加密/解密
     * @param obj 要处理的对象
     * @param encrypt true=加密, false=解密
     */
    private void processObject(Object obj, boolean encrypt) {
        if (obj == null) return;
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 只处理标记了@Encrypted注解的String字段
            if (field.isAnnotationPresent(Encrypted.class) && field.getType() == String.class) {
                try {
                    field.setAccessible(true);
                    String value = (String) field.get(obj);
                    
                    if (value != null && !value.isEmpty()) {
                        String processedValue = encrypt ? aesUtil.encrypt(value) : aesUtil.decrypt(value);
                        field.set(obj, processedValue);
                        logger.debug("字段 {} {}", field.getName(), encrypt ? "已加密" : "已解密");
                    }
                } catch (Exception e) {
                    logger.error("处理字段 {} 失败", field.getName(), e);
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 无需额外配置
    }
}