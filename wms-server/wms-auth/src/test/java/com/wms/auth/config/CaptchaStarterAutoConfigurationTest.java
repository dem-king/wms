package com.wms.auth.config;

import com.anji.captcha.properties.AjCaptchaProperties;
import com.anji.captcha.service.CaptchaCacheService;
import com.wms.auth.captcha.CaptchaCacheRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {
        CaptchaStarterAutoConfigurationTest.TestApplication.class,
        CaptchaConfig.class,
        CaptchaStarterAutoConfigurationTest.RedisTemplateTestConfig.class
})
@TestPropertySource(properties = {
        "aj.captcha.cache-type=redis"
})
class CaptchaStarterAutoConfigurationTest {

    @Autowired
    private AjCaptchaProperties captchaProperties;

    @Autowired
    private CaptchaCacheService captchaCacheService;

    @Test
    void shouldRegisterAjCaptchaProperties() {
        assertNotNull(captchaProperties);
    }

    @Test
    void shouldRegisterCaptchaCacheService() {
        assertNotNull(captchaCacheService);
        assertTrue(captchaCacheService instanceof CaptchaCacheRedisService);
    }

    @Test
    void shouldExposeBoot3AutoConfigurationImportResource() throws IOException {
        var resources = Thread.currentThread().getContextClassLoader().getResources(
                "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");

        boolean hasAjCaptchaAutoConfiguration = Collections.list(resources).stream()
                .map(resource -> {
                    try (var reader = new BufferedReader(new InputStreamReader(
                            resource.openStream(), StandardCharsets.UTF_8))) {
                        return reader.lines().collect(Collectors.joining("\n"));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .anyMatch(content -> content.contains("com.anji.captcha.config.AjCaptchaAutoConfiguration"));

        assertTrue(hasAjCaptchaAutoConfiguration);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class TestApplication {
    }

    @TestConfiguration
    static class RedisTemplateTestConfig {

        @Bean
        StringRedisTemplate stringRedisTemplate() {
            return mock(StringRedisTemplate.class);
        }
    }
}
