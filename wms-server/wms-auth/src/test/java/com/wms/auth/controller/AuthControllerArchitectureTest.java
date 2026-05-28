package com.wms.auth.controller;

import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AuthController 分层架构测试
 * 验证 Controller 不再直接依赖 SecurityUtil，而是通过 Service 获取当前用户上下文。
 */
@DisplayName("AuthController 分层测试")
class AuthControllerArchitectureTest {

    @Test
    @DisplayName("AuthController 源码中不应直接使用 SecurityUtil")
    void shouldNotUseSecurityUtilDirectlyInController() throws IOException {
        Path sourcePath = Path.of("src/main/java/com/wms/auth/controller/AuthController.java");
        String sourceCode = Files.readString(sourcePath);

        assertFalse(sourceCode.contains("SecurityUtil"));
    }

    @Test
    @DisplayName("AuthorizeService 应提供当前用户权限校验入口")
    void shouldExposeCurrentUserPermissionMethod() {
        boolean exists = Arrays.stream(AuthorizeService.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("hasCurrentUserPermission")
                        && Arrays.equals(method.getParameterTypes(), new Class<?>[]{String.class}));

        assertTrue(exists);
    }

    @Test
    @DisplayName("PasswordService 应提供当前用户修改密码入口")
    void shouldExposeCurrentUserPasswordChangeMethod() {
        boolean exists = Arrays.stream(PasswordService.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("changeCurrentUserPassword")
                        && Arrays.equals(method.getParameterTypes(),
                        new Class<?>[]{com.wms.auth.domain.dto.PasswordReq.class}));

        assertTrue(exists);
    }
}
