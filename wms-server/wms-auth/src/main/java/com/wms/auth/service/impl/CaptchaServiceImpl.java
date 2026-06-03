package com.wms.auth.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.SliderImageCaptchaInfo;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.vo.CaptchaImageResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.CaptchaService;
import com.wms.common.exception.BizException;
import com.wms.common.util.UserAgentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 滑块拼图验证码服务实现
 * 基于 tianai-captcha SDK 实现生成/校验，并通过 Redis 存储客户端指纹防止重放
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    /** 项目附加的客户端指纹存储 key 前缀（与 tianai 内部 key 隔离） */
    private static final String FINGERPRINT_KEY_PREFIX = "auth:captcha:fp:";

    private final ImageCaptchaApplication imageCaptchaApplication;
    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;
    private final UserAgentParser userAgentParser;
    private final ObjectMapper objectMapper;

    /**
     * 生成滑块拼图验证码
     * 调用 tianai SDK 生成 SLIDER 拼图，将客户端指纹写入 Redis 用于校验时防重放
     *
     * @param clientIp  客户端 IP
     * @param userAgent User-Agent 头
     * @return 验证码响应（token + 背景图 + 拼图块 + 缺口 Y）
     */
    @Override
    public CaptchaImageResp generateCaptcha(String clientIp, String userAgent) {
        // 调用 tianai SDK 生成 SLIDER 类型拼图
        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(
                CaptchaTypeConstant.SLIDER);
        if (response == null || response.getCaptcha() == null) {
            log.error("tianai 生成滑块拼图失败: {}", response == null ? "null" : "captcha is null");
            throw new BizException(AuthErrorCode.CAPTCHA_INVALID.getCode(),
                    AuthErrorCode.CAPTCHA_INVALID.getMsg());
        }
        ImageCaptchaVO vo = response.getCaptcha();
        String token = response.getId();

        // 写入客户端指纹到独立 Redis key，与 tianai 内部 captcha 缓存解耦
        // 仅做防重放绑定，校验时取出后立即删除实现一次性消费
        String fingerprint = DigestUtil.sha256Hex(
                clientIp + "|" + userAgentParser.truncate(userAgent));
        stringRedisTemplate.opsForValue().set(
                FINGERPRINT_KEY_PREFIX + token,
                fingerprint,
                Duration.ofSeconds(authProperties.getCaptchaExpire()));

        // 转换为项目内 VO
        CaptchaImageResp resp = new CaptchaImageResp();
        resp.setCaptchaToken(token);
        resp.setBackgroundImage(vo.getBackgroundImage());
        resp.setBlockImage(vo.getTemplateImage());
        // 滑块类型下 data 为 SliderImageCaptchaInfo，从中提取缺口 Y
        Integer blockY = null;
        if (vo.getData() instanceof SliderImageCaptchaInfo sliderInfo) {
            blockY = sliderInfo.getY();
        }
        resp.setBlockY(blockY);
        resp.setExpiresIn((int) authProperties.getCaptchaExpire());
        return resp;
    }

    /**
     * 校验滑块拼图
     * 1) 校验 Redis 中的客户端指纹（一次性消费：getAndDelete）
     * 2) 解析前端轨迹 JSON 为 ImageCaptchaTrack，调用 tianai SDK 做采样点/坐标校验
     *
     * @param captchaToken 验证码 Token
     * @param captchaTrack 前端采集的拖动轨迹数据（JSON 字符串）
     * @throws BizException 校验失败时抛出 CAPTCHA_INVALID 或 CAPTCHA_MISMATCH
     */
    @Override
    public void validateCaptcha(String captchaToken, String captchaTrack) {
        // 1. 校验客户端指纹（一次性消费：取出后立即删除）
        String fingerprintKey = FINGERPRINT_KEY_PREFIX + captchaToken;
        String storedFingerprint = stringRedisTemplate.opsForValue().getAndDelete(fingerprintKey);
        if (storedFingerprint == null) {
            throw new BizException(AuthErrorCode.CAPTCHA_INVALID.getCode(),
                    AuthErrorCode.CAPTCHA_INVALID.getMsg());
        }

        // 2. 解析前端轨迹 JSON 为 ImageCaptchaTrack，调用 tianai SDK 校验
        ImageCaptchaTrack track;
        try {
            track = objectMapper.readValue(captchaTrack, ImageCaptchaTrack.class);
        } catch (JsonProcessingException e) {
            log.warn("滑块拼图轨迹解析失败: token={}", captchaToken, e);
            throw new BizException(AuthErrorCode.CAPTCHA_MISMATCH.getCode(),
                    AuthErrorCode.CAPTCHA_MISMATCH.getMsg());
        }
        ApiResponse<?> response = imageCaptchaApplication.matching(captchaToken, track);
        if (response == null || !response.isSuccess()) {
            log.warn("滑块拼图校验失败: token={}, msg={}", captchaToken,
                    response == null ? "null" : response.getMsg());
            throw new BizException(AuthErrorCode.CAPTCHA_MISMATCH.getCode(),
                    AuthErrorCode.CAPTCHA_MISMATCH.getMsg());
        }
    }
}
