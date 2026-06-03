package com.wms.auth.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.SliderImageCaptchaInfo;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.vo.CaptchaImageResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.common.exception.BizException;
import com.wms.common.util.UserAgentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CaptchaServiceImpl 滑块拼图测试")
class CaptchaServiceImplTest {

    private ImageCaptchaApplication application;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    private AuthProperties authProperties;
    private UserAgentParser userAgentParser;
    private ObjectMapper objectMapper;
    private CaptchaServiceImpl service;

    @BeforeEach
    void setUp() {
        application = mock(ImageCaptchaApplication.class);
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        authProperties = new AuthProperties();
        authProperties.setCaptchaExpire(120);

        userAgentParser = new UserAgentParser();
        objectMapper = new ObjectMapper();

        service = new CaptchaServiceImpl(application, redisTemplate, authProperties,
                userAgentParser, objectMapper);
    }

    @Test
    @DisplayName("生成滑块拼图应返回背景图/拼图块/Y 坐标与 Token")
    void generate_shouldReturnImageCaptchaResp() {
        ImageCaptchaVO vo = new ImageCaptchaVO();
        vo.setBackgroundImage("data:image/png;base64,xxx");
        vo.setTemplateImage("data:image/png;base64,yyy");
        SliderImageCaptchaInfo data = new SliderImageCaptchaInfo();
        data.setY(33);
        vo.setData(data);
        CaptchaResponse<ImageCaptchaVO> response = new CaptchaResponse<>();
        response.setId("tok-1");
        response.setCaptcha(vo);
        when(application.generateCaptcha(CaptchaTypeConstant.SLIDER)).thenReturn(response);

        CaptchaImageResp resp = service.generateCaptcha("127.0.0.1", "Mozilla/5.0");

        assertNotNull(resp);
        assertEquals("tok-1", resp.getCaptchaToken());
        assertEquals("data:image/png;base64,xxx", resp.getBackgroundImage());
        assertEquals("data:image/png;base64,yyy", resp.getBlockImage());
        assertEquals(33, resp.getBlockY());
        assertEquals(120, resp.getExpiresIn());
    }

    @Test
    @DisplayName("生成滑块后应将客户端指纹写入 Redis")
    void generate_shouldWriteFingerprintToRedis() {
        ImageCaptchaVO vo = new ImageCaptchaVO();
        vo.setBackgroundImage("bg");
        vo.setTemplateImage("block");
        SliderImageCaptchaInfo data = new SliderImageCaptchaInfo();
        data.setY(10);
        vo.setData(data);
        CaptchaResponse<ImageCaptchaVO> response = new CaptchaResponse<>();
        response.setId("tok-2");
        response.setCaptcha(vo);
        when(application.generateCaptcha(anyString())).thenReturn(response);

        service.generateCaptcha("10.0.0.1", "ua");

        verify(valueOps).set(eq("auth:captcha:fp:tok-2"), anyString(), eq(Duration.ofSeconds(120)));
    }

    @Test
    @DisplayName("校验时若指纹缺失应抛 CAPTCHA_INVALID")
    void validate_shouldThrowInvalidWhenFingerprintMissing() {
        when(valueOps.getAndDelete("auth:captcha:fp:tok-x")).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> service.validateCaptcha("tok-x", "{}", "127.0.0.1", "ua"));
        assertEquals(AuthErrorCode.CAPTCHA_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("校验时若指纹哈希不匹配应抛 CAPTCHA_INVALID（防 token 被其他 IP 截获重放）")
    void validate_shouldThrowInvalidWhenFingerprintMismatch() {
        // 存储的 fp 来自 IP=10.0.0.1, UA=ua（与当前请求 IP 不同）
        when(valueOps.getAndDelete("auth:captcha:fp:tok-mismatch"))
                .thenReturn("stored-fp-different-from-current");

        BizException ex = assertThrows(BizException.class,
                () -> service.validateCaptcha("tok-mismatch", "track", "192.168.1.1", "ua"));
        assertEquals(AuthErrorCode.CAPTCHA_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("tianai 校验失败时应抛 CAPTCHA_MISMATCH")
    void validate_shouldThrowMismatchWhenTianaiReturnsFail() {
        // 算出与 generate 相同的指纹值，让绑定校验通过
        String ip = "127.0.0.1";
        String ua = "ua";
        String expectedFp = DigestUtil.sha256Hex(ip + "|" + userAgentParser.truncate(ua));
        when(valueOps.getAndDelete("auth:captcha:fp:tok-y")).thenReturn(expectedFp);
        doReturn(ApiResponse.ofError("坐标错误")).when(application)
                .matching(eq("tok-y"), any(ImageCaptchaTrack.class));

        BizException ex = assertThrows(BizException.class,
                () -> service.validateCaptcha("tok-y", "{}", ip, ua));
        assertEquals(AuthErrorCode.CAPTCHA_MISMATCH.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("tianai 校验成功时不应抛异常")
    void validate_shouldPassWhenTianaiReturnsSuccess() {
        // 算出与 generate 相同的指纹值，让 stub 匹配
        String ip = "127.0.0.1";
        String ua = "ua";
        String expectedFp = DigestUtil.sha256Hex(ip + "|" + userAgentParser.truncate(ua));
        when(valueOps.getAndDelete("auth:captcha:fp:tok-z")).thenReturn(expectedFp);
        doReturn(ApiResponse.ofSuccess()).when(application)
                .matching(eq("tok-z"), any(ImageCaptchaTrack.class));

        assertDoesNotThrow(() -> service.validateCaptcha("tok-z", "{}", ip, ua));
    }
}
