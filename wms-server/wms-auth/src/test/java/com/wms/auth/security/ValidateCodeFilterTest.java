package com.wms.auth.security;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.common.domain.R;
import com.wms.common.util.SpringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ValidateCodeFilter 单元测试
 * 验证验证码二次校验过滤器的拦截逻辑：
 * - 非 /auth/login 请求直接放行
 * - captchaEnabled=false 时跳过校验
 * - captchaVerification 为空时跳过 Filter 校验（由 Service 层兜底）
 * - verification 通过后放行
 * - verification 失败时返回错误响应
 */
@DisplayName("ValidateCodeFilter 测试")
class ValidateCodeFilterTest {

    private ValidateCodeFilter filter;
    private AuthProperties authProperties;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authProperties = new AuthProperties();
        authProperties.setCaptchaEnabled(true);
        objectMapper = new ObjectMapper();
        filter = new ValidateCodeFilter(authProperties, objectMapper);
    }

    @Test
    @DisplayName("非 /auth/login 请求应直接放行")
    void nonLoginRequest_shouldPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/other");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("GET /auth/login 请求应直接放行（仅拦截 POST）")
    void getLoginRequest_shouldPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("captchaEnabled=false 时应跳过校验直接放行")
    void captchaDisabled_shouldSkipValidation() throws Exception {
        authProperties.setCaptchaEnabled(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("captchaVerification 请求头为空时应跳过 Filter 校验放行（由 Service 层兜底）")
    void codeEmpty_shouldSkipFilterAndPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        // 不设置 captchaVerification 请求头
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        // 源码逻辑：请求头未携带 captchaVerification 时跳过 Filter 校验，由 AuthServiceImpl 兜底
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("captchaVerification 为空字符串时应跳过 Filter 校验放行")
    void codeBlank_shouldSkipFilterAndPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        request.addHeader("captchaVerification", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("verification 通过后应放行")
    void verificationSuccess_shouldPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        request.addHeader("captchaVerification", "valid-verification-code");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel successModel = ResponseModel.success();
        when(captchaService.verification(any(CaptchaVO.class))).thenReturn(successModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);
            filter.doFilterInternal(request, response, chain);
            verify(chain).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("verification 失败时应阻止登录并返回错误响应")
    void verificationFail_shouldBlockLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        request.addHeader("captchaVerification", "invalid-code");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel failModel = ResponseModel.errorMsg("验证未通过");
        when(captchaService.verification(any(CaptchaVO.class))).thenReturn(failModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);
            filter.doFilterInternal(request, response, chain);
            // 验证未通过，不应调用 filterChain.doFilter
            verify(chain, never()).doFilter(request, response);
            // 响应应包含错误信息
            assertEquals(200, response.getStatus());
            String responseBody = response.getContentAsString();
            assertNotNull(responseBody);
            assertTrue(responseBody.contains("\"code\":" + AuthErrorCode.CAPTCHA_MISMATCH.getCode()));
        }
    }

    @Test
    @DisplayName("verification 返回 6110（验证码过期）时应返回 CAPTCHA_EXPIRED 错误")
    void verificationExpired_shouldReturnCaptchaExpiredError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        request.setMethod("POST");
        request.addHeader("captchaVerification", "expired-code");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel expiredModel = new ResponseModel();
        expiredModel.setRepCode("6110");
        expiredModel.setRepMsg("验证码已失效");
        when(captchaService.verification(any(CaptchaVO.class))).thenReturn(expiredModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);
            filter.doFilterInternal(request, response, chain);
            verify(chain, never()).doFilter(request, response);
            assertEquals(200, response.getStatus());
            String responseBody = response.getContentAsString();
            assertNotNull(responseBody);
            assertTrue(responseBody.contains("\"code\":" + AuthErrorCode.CAPTCHA_EXPIRED.getCode()));
        }
    }

    @Test
    @DisplayName("非 /auth/login 路径即使有 captchaVerification 请求头也应放行")
    void nonLoginPathWithCaptchaHeader_shouldPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/refresh");
        request.setMethod("POST");
        request.addHeader("captchaVerification", "some-code");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }
}