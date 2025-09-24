package cyou.oxling.loanappbackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感数据加密注解
 * 标记需要进行AES加密的字段
 * 
 * 使用此注解的字段在存入数据库时会自动加密，
 * 从数据库读取时会自动解密
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypted {
    
    /**
     * 字段描述，用于日志记录
     */
    String value() default "";
    
}