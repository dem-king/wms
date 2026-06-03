package com.wms.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AuthProperties.class)
@EnableConfigurationProperties(AuthProperties.class)
@TestPropertySource(properties = {
        "wms.auth.captcha-expire=180",
        "wms.auth.captcha-rate-limit-threshold=30",
        "wms.auth.captcha-track-tolerance=8"
})
class AuthPropertiesTest {

    @Autowired
    private AuthProperties authProperties;

    @Test
    void shouldReadCaptchaExpire() {
        assertEquals(180L, authProperties.getCaptchaExpire());
    }

    @Test
    void shouldReadCaptchaRateLimitThreshold() {
        assertEquals(30, authProperties.getCaptchaRateLimitThreshold());
    }

    @Test
    void shouldReadCaptchaTrackTolerance() {
        assertEquals(8, authProperties.getCaptchaTrackTolerance());
    }

    @Test
    void shouldDefaultCaptchaEnabledTrue() {
        assertTrue(authProperties.isCaptchaEnabled());
    }
}
