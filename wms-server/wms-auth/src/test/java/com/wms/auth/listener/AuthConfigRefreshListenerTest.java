package com.wms.auth.listener;

import com.wms.auth.config.AuthProperties;
import com.wms.common.event.ConfigChangeEvent;
import com.wms.system.manager.SysConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * AuthConfigRefreshListener 单元测试
 * 验证认证配置动态刷新监听器对各类配置变更的正确响应
 */
@DisplayName("AuthConfigRefreshListener 测试")
@ExtendWith(MockitoExtension.class)
class AuthConfigRefreshListenerTest {

    @Mock
    private AuthProperties authProperties;

    @Mock
    private SysConfigManager configManager;

    private AuthConfigRefreshListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuthConfigRefreshListener(authProperties, configManager);
    }

    // ==================== auth配置变更 ====================

    @Test
    @DisplayName("accessTokenExpire变更后刷新AuthProperties")
    void onConfigChange_accessTokenExpire_refreshesProperty() {
        when(configManager.getLongValue("wms.auth.access-token-expire", 7200)).thenReturn(3600L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.auth.access-token-expire", "7200", "3600");
        listener.onConfigChange(event);

        verify(authProperties).setAccessTokenExpire(3600L);
    }

    @Test
    @DisplayName("refreshTokenExpire变更后刷新AuthProperties")
    void onConfigChange_refreshTokenExpire_refreshesProperty() {
        when(configManager.getLongValue("wms.auth.refresh-token-expire", 604800)).thenReturn(1209600L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.auth.refresh-token-expire", "604800", "1209600");
        listener.onConfigChange(event);

        verify(authProperties).setRefreshTokenExpire(1209600L);
    }

    @Test
    @DisplayName("tokenRenewThreshold变更后刷新AuthProperties")
    void onConfigChange_tokenRenewThreshold_refreshesProperty() {
        when(configManager.getLongValue("wms.auth.token-renew-threshold", 1800)).thenReturn(900L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.auth.token-renew-threshold", "1800", "900");
        listener.onConfigChange(event);

        verify(authProperties).setTokenRenewThreshold(900L);
    }

    // ==================== security配置变更 ====================

    @Test
    @DisplayName("loginFailThreshold变更后刷新AuthProperties")
    void onConfigChange_loginFailThreshold_refreshesProperty() {
        when(configManager.getIntValue("wms.security.login-fail-threshold", 5)).thenReturn(10);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.security.login-fail-threshold", "5", "10");
        listener.onConfigChange(event);

        verify(authProperties).setLoginFailThreshold(10);
    }

    @Test
    @DisplayName("lockDuration变更后刷新AuthProperties")
    void onConfigChange_lockDuration_refreshesProperty() {
        when(configManager.getLongValue("wms.security.lock-duration-seconds", 1800)).thenReturn(3600L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.security.lock-duration-seconds", "1800", "3600");
        listener.onConfigChange(event);

        verify(authProperties).setLockDuration(3600L);
    }

    @Test
    @DisplayName("captchaEnabled变更后刷新AuthProperties")
    void onConfigChange_captchaEnabled_refreshesProperty() {
        when(configManager.getBooleanValue("wms.security.captcha-enabled", true)).thenReturn(false);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.security.captcha-enabled", "true", "false");
        listener.onConfigChange(event);

        verify(authProperties).setCaptchaEnabled(false);
    }

    @Test
    @DisplayName("captchaExpireSeconds变更后刷新AuthProperties")
    void onConfigChange_captchaExpireSeconds_refreshesProperty() {
        when(configManager.getLongValue("wms.security.captcha-expire-seconds", 120)).thenReturn(600L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.security.captcha-expire-seconds", "120", "600");
        listener.onConfigChange(event);

        verify(authProperties).setCaptchaExpire(600L);
    }

    // ==================== storage配置变更 ====================

    @Test
    @DisplayName("avatarMaxSizeMb变更后正确转换为Bytes并刷新AuthProperties")
    void onConfigChange_avatarMaxSizeMb_convertsToBytes() {
        // wms.storage.avatar-max-size-mb 变更后，MB值转换为Bytes设置到AuthProperties
        when(configManager.getLongValue("wms.storage.avatar-max-size-mb", 2)).thenReturn(5L);

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.storage.avatar-max-size-mb", "2", "5");
        listener.onConfigChange(event);

        verify(authProperties).setAvatarMaxSizeBytes(5L * 1024 * 1024);
    }

    // ==================== 无关配置变更 ====================

    @Test
    @DisplayName("无关配置变更不触发刷新")
    void onConfigChange_unrelatedKey_doesNotRefresh() {
        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.business.low-stock-threshold", "10", "20");
        listener.onConfigChange(event);

        verifyNoInteractions(authProperties);
    }

    @Test
    @DisplayName("wms.auth前缀但不在映射范围内的配置不刷新属性")
    void onConfigChange_authPrefixUnmappedKey_doesNotRefreshProperty() {
        // wms.auth.unknown-config 虽然以wms.auth.开头，但不在switch映射中
        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.auth.unknown-config", "old", "new");
        listener.onConfigChange(event);

        verify(authProperties, never()).setAccessTokenExpire(anyLong());
        verify(authProperties, never()).setRefreshTokenExpire(anyLong());
        verify(authProperties, never()).setTokenRenewThreshold(anyLong());
        verify(authProperties, never()).setLoginFailThreshold(anyInt());
        verify(authProperties, never()).setLockDuration(anyLong());
        verify(authProperties, never()).setCaptchaEnabled(anyBoolean());
        verify(authProperties, never()).setCaptchaExpire(anyLong());
        verify(authProperties, never()).setAvatarMaxSizeBytes(anyLong());
    }

    // ==================== 异常处理 ====================

    @Test
    @DisplayName("刷新属性异常时不抛出异常，不影响其他监听器")
    void onConfigChange_refreshException_doesNotThrow() {
        when(configManager.getLongValue("wms.auth.access-token-expire", 7200))
                .thenThrow(new RuntimeException("DB down"));

        ConfigChangeEvent event = new ConfigChangeEvent(
                this, "wms.auth.access-token-expire", "7200", "3600");

        // 不应抛出异常
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> listener.onConfigChange(event));
    }
}