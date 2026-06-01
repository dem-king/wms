package com.wms.auth.security;

import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.TokenService;
import com.wms.system.service.SysRoleService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JWT认证过滤器权限装载测试
 */
@DisplayName("JwtAuthenticationFilter 测试")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private SysRoleService sysRoleService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("认证成功后应写入角色和权限编码到Spring Security上下文")
    void shouldSetRoleAndPermissionAuthorities() throws ServletException, IOException {
        Claims claims = mock(Claims.class);
        when(tokenService.validateToken("access-token")).thenReturn(true);
        when(tokenService.parseToken("access-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("1001");
        when(claims.get("username", String.class)).thenReturn("admin");
        when(sysRoleService.getRoleCodesByUserId(1001L)).thenReturn(List.of("admin"));
        when(authorizeService.getUserPermissions(1001L)).thenReturn(List.of("system:perm:list"));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService, authorizeService, sysRoleService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer access-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
        assertEquals(1001L, authentication.getPrincipal());
        assertTrue(authorities.contains("ROLE_admin"));
        assertTrue(authorities.contains("system:perm:list"));
    }
}
