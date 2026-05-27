package com.wms.common.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SecurityUtil 测试")
class SecurityUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("应优先返回认证详情中的用户名")
    void shouldReturnUsernameFromAuthenticationDetails() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(1L, null);
        authentication.setDetails("admin");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals("admin", SecurityUtil.getCurrentUsername());
    }
}
