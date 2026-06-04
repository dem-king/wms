package com.wms.auth.listener;

import com.wms.auth.config.AuthProperties;
import com.wms.common.event.ConfigChangeEvent;
import com.wms.system.manager.SysConfigManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 认证配置动态刷新监听器
 * 监听ConfigChangeEvent，当auth/security组配置变更时
 * 动态更新AuthProperties中的属性值，无需重启服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthConfigRefreshListener {

    private final AuthProperties authProperties;
    private final SysConfigManager configManager;

    /**
     * 监听配置变更事件，刷新认证相关属性
     * 仅处理wms.auth、wms.security和wms.storage前缀的配置项
     *
     * @param event 配置变更事件
     */
    @EventListener
    public void onConfigChange(ConfigChangeEvent event) {
        String key = event.getConfigKey();
        // 仅处理认证、安全和存储相关配置
        if (!key.startsWith("wms.auth.") && !key.startsWith("wms.security.") && !key.startsWith("wms.storage.")) {
            return;
        }

        log.info("收到配置变更事件, key={}, oldValue={}, newValue={}, 开始刷新认证属性",
                key, event.getOldValue(), event.getNewValue());

        try {
            refreshProperty(key);
            log.info("认证属性刷新成功, key={}", key);
        } catch (Exception e) {
            // 刷新失败不能抛异常影响其他监听器，仅记录错误日志
            log.error("认证属性刷新失败, key={}", key, e);
        }
    }

    /**
     * 根据配置键刷新对应的AuthProperties属性
     * 从SysConfigManager重新读取最新值并设置到AuthProperties
     *
     * @param key 配置键
     */
    private void refreshProperty(String key) {
        switch (key) {
            // 认证Token相关配置
            case "wms.auth.access-token-expire":
                authProperties.setAccessTokenExpire(
                        configManager.getLongValue(key, 7200));
                break;
            case "wms.auth.refresh-token-expire":
                authProperties.setRefreshTokenExpire(
                        configManager.getLongValue(key, 604800));
                break;
            case "wms.auth.token-renew-threshold":
                authProperties.setTokenRenewThreshold(
                        configManager.getLongValue(key, 1800));
                break;

            // 安全策略相关配置
            case "wms.security.login-fail-threshold":
                authProperties.setLoginFailThreshold(
                        configManager.getIntValue(key, 5));
                break;
            case "wms.security.lock-duration-seconds":
                authProperties.setLockDuration(
                        configManager.getLongValue(key, 1800));
                break;
            case "wms.security.captcha-enabled":
                authProperties.setCaptchaEnabled(
                        configManager.getBooleanValue(key, true));
                break;


            // 文件上传相关配置
            case "wms.storage.avatar-max-size-mb":
                long maxSizeMb = configManager.getLongValue(key, 2);
                // MB转换为Bytes
                authProperties.setAvatarMaxSizeBytes(maxSizeMb * 1024 * 1024);
                break;

            default:
                log.debug("配置项{}不在AuthProperties映射范围内，跳过刷新", key);
                break;
        }
    }
}