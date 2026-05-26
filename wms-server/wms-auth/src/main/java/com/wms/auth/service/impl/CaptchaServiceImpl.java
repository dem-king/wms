package com.wms.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.img.ImgUtil;
import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.auth.domain.vo.CaptchaResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.CaptchaService;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 处理验证码生成与校验，验证码存储在Redis中并设置过期时间
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    /**
     * 生成验证码
     * 生成线段干扰验证码图片，验证码文本存入Redis
     * 
     * @return 验证码响应(含key和Base64图片)
     */
    @Override
    public CaptchaResp generateCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(AuthConstants.CAPTCHA_WIDTH, AuthConstants.CAPTCHA_HEIGHT, AuthConstants.CAPTCHA_CHAR_COUNT, AuthConstants.CAPTCHA_LINE_COUNT);
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String captchaText = captcha.getCode();

        stringRedisTemplate.opsForValue().set(
                AuthRedisKey.CAPTCHA_PREFIX + captchaKey,
                captchaText.toLowerCase(),
                authProperties.getCaptchaExpire(), TimeUnit.SECONDS);

        CaptchaResp resp = new CaptchaResp();
        resp.setCaptchaKey(captchaKey);
        resp.setCaptchaImage(captcha.getImageBase64Data());
        return resp;
    }

    /**
     * 校验验证码
     * 从Redis取出并删除验证码，比较文本(忽略大小写)
     * 
     * @param captchaKey 验证码key
     * @param captchaText 用户输入的验证码文本
     */
    @Override
    public void validateCaptcha(String captchaKey, String captchaText) {
        String redisKey = AuthRedisKey.CAPTCHA_PREFIX + captchaKey;
        String storedText = stringRedisTemplate.opsForValue().getAndDelete(redisKey);
        if (storedText == null) {
            throw new BizException(AuthErrorCode.CAPTCHA_INVALID.getCode(), AuthErrorCode.CAPTCHA_INVALID.getMsg());
        }
        if (!storedText.equals(captchaText.toLowerCase())) {
            throw new BizException(AuthErrorCode.CAPTCHA_MISMATCH.getCode(), AuthErrorCode.CAPTCHA_MISMATCH.getMsg());
        }
    }
}
