package com.wms.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.img.ImgUtil;
import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.vo.CaptchaResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.CaptchaService;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    @Override
    public CaptchaResp generateCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 6);
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
