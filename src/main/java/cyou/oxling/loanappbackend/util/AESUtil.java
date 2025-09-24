package cyou.oxling.loanappbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密工具类
 * 用于保护身份证号、银行卡号等敏感信息
 * 使用AES-256-GCM模式，提供更好的安全性
 */
@Component
public class AESUtil {

    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 加密模式/填充方式
     */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /**
     * GCM模式下IV长度
     */
    private static final int GCM_IV_LENGTH = 12;

    /**
     * GCM模式下标签长度
     */
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * 从配置文件读取的加密密钥
     */
    @Value("${security.aes.secret-key}")
    private String secretKeyString;

    /**
     * 获取SecretKey对象
     * @return SecretKey
     */
    private SecretKey getSecretKey() {
        // 使用SHA-256将密钥字符串转换为256位密钥
        try {
            byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
            // 如果密钥长度不足32字节，则重复填充到32字节
            byte[] key = new byte[32];
            for (int i = 0; i < key.length; i++) {
                key[i] = keyBytes[i % keyBytes.length];
            }
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            logger.error("生成密钥失败", e);
            throw new RuntimeException("生成密钥失败", e);
        }
    }

    /**
     * 加密字符串
     * @param plainText 明文
     * @return 加密后的字符串（Base64编码）
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            
            // 创建Cipher对象
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            
            // 初始化GCM参数
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            
            // 执行加密
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据拼接
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
            
            // 返回Base64编码的结果
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            logger.error("加密失败，原文: [已隐藏]", e);
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * 解密字符串
     * @param encryptedText 加密后的字符串（Base64编码）
     * @return 解密后的明文
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            
            // Base64解码
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // 提取IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            
            // 提取加密数据
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            // 创建Cipher对象
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            
            // 执行解密
            byte[] decryptedData = cipher.doFinal(encryptedData);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("解密失败", e);
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 生成AES密钥（用于初始化配置）
     * @return Base64编码的密钥字符串
     */
    public static String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("生成密钥失败", e);
        }
    }
}