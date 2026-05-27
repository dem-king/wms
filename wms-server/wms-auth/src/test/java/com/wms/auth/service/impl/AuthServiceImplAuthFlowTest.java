package com.wms.auth.service.impl;

import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.service.AuthAuditService;
import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.CaptchaService;
import com.wms.auth.service.CryptoService;
import com.wms.auth.service.LoginLockService;
import com.wms.auth.service.RateLimiterService;
import com.wms.auth.service.TokenService;
import com.wms.common.exception.BizException;
import com.wms.common.util.IpRegionResolver;
import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.service.SysMenuService;
import com.wms.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AuthServiceImpl 认证链路测试")
@ExtendWith(MockitoExtension.class)
class AuthServiceImplAuthFlowTest {

    private static final String CHROME_ON_WINDOWS_UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private LoginLockService loginLockService;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private AuthAuditService authAuditService;

    @Mock
    private SysUserService sysUserService;

    @Mock
    private SysMenuService sysMenuService;

    @Mock
    private AuthProperties authProperties;

    @Mock
    private IpRegionResolver ipRegionResolver;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("登录成功时应记录解析后的浏览器和操作系统")
    void shouldRecordParsedBrowserAndOsWhenLoginSucceeds() {
        LoginReq req = new LoginReq();
        req.setUsername("admin");
        req.setEncryptedPassword("cipher-text");

        SysUser user = new SysUser();
        user.setId(1001L);
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setStatus(1);
        user.setRealName("系统管理员");

        TokenResp tokenResp = new TokenResp();
        tokenResp.setAccessToken("access-token");
        tokenResp.setRefreshToken("refresh-token");
        tokenResp.setExpiresIn(7200L);

        when(authProperties.isCaptchaEnabled()).thenReturn(false);
        when(loginLockService.isLocked("admin")).thenReturn(false);
        when(cryptoService.decryptPassword("cipher-text")).thenReturn("plain-password");
        when(sysUserService.getByUsername("admin")).thenReturn(user);
        when(cryptoService.verifyPassword("plain-password", "encoded-password")).thenReturn(true);
        when(authorizeService.getUserPermissions(1001L)).thenReturn(List.of("system:user:list"));
        when(sysMenuService.getUserMenuTree()).thenReturn(Collections.emptyList());
        when(tokenService.generateTokenPair(1001L, "admin", Collections.emptyList())).thenReturn(tokenResp);
        when(ipRegionResolver.resolve("127.0.0.1")).thenReturn("内网IP");

        LoginResp result = authService.login(req, "127.0.0.1", CHROME_ON_WINDOWS_UA);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        verify(authAuditService).recordLoginLog(
                1001L,
                "admin",
                "127.0.0.1",
                "内网IP",
                "Chrome 124",
                "Windows 10",
                SysLogConstants.LOGIN_STATUS_SUCCESS,
                null
        );
    }

    @Test
    @DisplayName("登录失败时应记录解析后的浏览器和操作系统")
    void shouldRecordParsedBrowserAndOsWhenLoginFails() {
        LoginReq req = new LoginReq();
        req.setUsername("ghost");
        req.setEncryptedPassword("cipher-text");

        when(authProperties.isCaptchaEnabled()).thenReturn(false);
        when(loginLockService.isLocked("ghost")).thenReturn(false);
        when(cryptoService.decryptPassword("cipher-text")).thenReturn("plain-password");
        when(sysUserService.getByUsername("ghost")).thenReturn(null);
        when(ipRegionResolver.resolve("127.0.0.1")).thenReturn("内网IP");

        assertThrows(BizException.class, () -> authService.login(req, "127.0.0.1", CHROME_ON_WINDOWS_UA));

        verify(authAuditService).recordLoginLog(
                null,
                "ghost",
                "127.0.0.1",
                "内网IP",
                "Chrome 124",
                "Windows 10",
                SysLogConstants.LOGIN_STATUS_FAIL,
                "用户不存在"
        );
    }

    @Test
    @DisplayName("登出时应构造 SysOperLog 并委托 AuthAuditService 统一记录")
    void shouldDelegateLogoutToAuthAuditService() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1001");
        when(claims.get("username", String.class)).thenReturn("admin");
        when(tokenService.parseToken("access-token")).thenReturn(claims);

        authService.logout("access-token");

        ArgumentCaptor<SysOperLog> operLogCaptor = ArgumentCaptor.forClass(SysOperLog.class);
        verify(authAuditService).recordOperLog(operLogCaptor.capture());
        SysOperLog operLog = operLogCaptor.getValue();
        assertEquals("auth", operLog.getModule());
        assertEquals("登出", operLog.getType());
        assertEquals("用户登出", operLog.getDesc());
        assertEquals(1001L, operLog.getOperatorId());
        assertEquals("admin", operLog.getOperatorName());
        assertEquals(SysLogConstants.OPER_STATUS_SUCCESS, operLog.getStatus());
        assertNotNull(operLog.getOperTime());
        verify(tokenService).revokeToken("access-token");
        verify(tokenService).revokeAllTokens(1001L);
    }
}
