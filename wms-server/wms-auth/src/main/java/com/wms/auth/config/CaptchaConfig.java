package com.wms.auth.config;

import com.anji.captcha.properties.AjCaptchaProperties;
import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.wms.auth.captcha.CaptchaCacheRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 验证码配置类
 * 注册项目自定义的 Redis 缓存实现和验证码国际化 MessageSource。
 */
@Configuration
public class CaptchaConfig {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 注册 CaptchaCacheService Bean
     * 使用 @Primary 覆盖 AJ-Captcha 默认的 LocalCacheService，
     * 并将项目中的 StringRedisTemplate 注入给 CaptchaCacheRedisService
     *
     * @param config AJ-Captcha 配置属性
     * @return CaptchaCacheService 实例（Redis 实现）
     */
    @Bean(name = "AjCaptchaCacheService")
    @Primary
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties config) {
        // 1. 让工厂基于"配置项 cache-type"产出对应实例
        CaptchaCacheService ret = CaptchaServiceFactory.getCache(config.getCacheType().name());
        // 2. 如果是 Redis 实现，把项目里的 StringRedisTemplate 注入给它
        if (ret instanceof CaptchaCacheRedisService) {
            ((CaptchaCacheRedisService) ret).setStringRedisTemplate(redisTemplate);
        }
        return ret;
    }

    /**
     * 注册验证码国际化 MessageSource
     * 解析 captcha/messages 资源文件，支持中英文错误消息
     *
     * @return 验证码国际化消息源
     */
    @Bean
    @ConditionalOnMissingBean
    public org.springframework.context.MessageSource captchaMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("captcha/messages", "captcha/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
