package com.wms.auth.service.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.domain.vo.RsaKeyPairResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.service.CryptoService;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 加密服务实现类
 * 处理RSA密钥对生成、密码RSA解密、BCrypt哈希与校验
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    /**
     * 生成RSA密钥对
     * 公钥和私钥存入Redis并设置过期时间
     * 
     * @return RSA密钥对响应(含公钥和keyId)
     */
    @Override
    public RsaKeyPairResp generateRsaKeyPair() {
        RSA rsa = new RSA();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String keyId = UUID.randomUUID().toString().replace("-", "");

        stringRedisTemplate.opsForValue().set(
                AuthRedisKey.RSA_PUBLIC_KEY + ":" + keyId, publicKeyBase64,
                authProperties.getRsaKeyExpire(), TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(
                AuthRedisKey.RSA_PRIVATE_KEY + ":" + keyId, privateKeyBase64,
                authProperties.getRsaKeyExpire(), TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(
                AuthRedisKey.RSA_KEY_ID, keyId,
                authProperties.getRsaKeyExpire(), TimeUnit.SECONDS);

        RsaKeyPairResp resp = new RsaKeyPairResp();
        resp.setPublicKey(publicKeyBase64);
        resp.setKeyId(keyId);
        return resp;
    }

    /**
     * 使用当前RSA私钥解密密码
     * 
     * @param encryptedPassword RSA加密后的密码
     * @return 解密后的明文密码
     */
    @Override
    public String decryptPassword(String encryptedPassword) {
        String keyId = stringRedisTemplate.opsForValue().get(AuthRedisKey.RSA_KEY_ID);
        if (keyId == null) {
            throw new BizException(AuthErrorCode.RSA_KEY_ERROR.getCode(), AuthErrorCode.RSA_KEY_ERROR.getMsg());
        }
        String privateKeyBase64 = stringRedisTemplate.opsForValue()
                .get(AuthRedisKey.RSA_PRIVATE_KEY + ":" + keyId);
        if (privateKeyBase64 == null) {
            throw new BizException(AuthErrorCode.RSA_KEY_ERROR.getCode(), AuthErrorCode.RSA_KEY_ERROR.getMsg());
        }
        try {
            RSA rsa = new RSA(privateKeyBase64, null);
            byte[] decrypted = rsa.decrypt(encryptedPassword, KeyType.PrivateKey);
            return new String(decrypted, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("RSA密码解密失败: {}", e.getMessage());
            throw new BizException(AuthErrorCode.DECRYPT_ERROR.getCode(), AuthErrorCode.DECRYPT_ERROR.getMsg());
        }
    }

    public String decryptPasswordWithKeyId(String encryptedPassword, String keyId) {
        String privateKeyBase64 = stringRedisTemplate.opsForValue()
                .get(AuthRedisKey.RSA_PRIVATE_KEY + ":" + keyId);
        if (privateKeyBase64 == null) {
            throw new BizException(AuthErrorCode.RSA_KEY_ERROR.getCode(), AuthErrorCode.RSA_KEY_ERROR.getMsg());
        }
        try {
            RSA rsa = new RSA(privateKeyBase64, null);
            byte[] decrypted = rsa.decrypt(encryptedPassword, KeyType.PrivateKey);
            return new String(decrypted, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("RSA密码解密失败: {}", e.getMessage());
            throw new BizException(AuthErrorCode.DECRYPT_ERROR.getCode(), AuthErrorCode.DECRYPT_ERROR.getMsg());
        }
    }

    /**
     * 对明文密码进行BCrypt哈希
     * 
     * @param rawPassword 明文密码
     * @return BCrypt哈希值
     */
    @Override
    public String hashPassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
     * 校验明文密码与哈希值是否匹配
     * 
     * @param rawPassword 明文密码
     * @param hashedPassword BCrypt哈希值
     * @return 是否匹配
     */
    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, hashedPassword);
    }
}
