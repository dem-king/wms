package com.wms.auth.service.impl;

import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.common.exception.BizException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@DisplayName("TokenServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private Map<String, String> redisStore;

    @BeforeEach
    void setUp() {
        redisStore = new HashMap<>();
        lenient().when(stringRedisTemplate.hasKey(anyString())).thenAnswer(invocation -> redisStore.containsKey(invocation.getArgument(0)));
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(valueOperations.get(anyString())).thenAnswer(invocation -> redisStore.get(invocation.getArgument(0)));
        lenient().when(valueOperations.getAndDelete(anyString())).thenAnswer(invocation -> redisStore.remove(invocation.getArgument(0)));
        lenient().when(stringRedisTemplate.delete(anyString())).thenAnswer(invocation -> redisStore.remove(invocation.getArgument(0)) != null);
        lenient().doAnswer(invocation -> {
            redisStore.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("刷新 Token 后应保留 accessToken 的用户名和角色声明")
    void shouldKeepUsernameAndRolesClaimsWhenRefreshingToken() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtSecret("wms-secret-key-for-jwt-token-generation-2024");
        authProperties.setAccessTokenExpire(7200);
        authProperties.setRefreshTokenExpire(604800);

        TokenServiceImpl tokenService = new TokenServiceImpl(stringRedisTemplate, authProperties);
        TokenResp issuedTokens = tokenService.generateTokenPair(1L, "admin", List.of("ADMIN"));

        TokenResp refreshedTokens = tokenService.refreshToken(issuedTokens.getRefreshToken());
        Claims refreshedClaims = tokenService.parseToken(refreshedTokens.getAccessToken());

        assertEquals("admin", refreshedClaims.get("username", String.class));
        assertEquals(List.of("ADMIN"), refreshedClaims.get("roles", List.class));
    }

    @Test
    @DisplayName("撤销当前用户所有 Token 后 refreshToken 不应再可用")
    void shouldRejectRefreshTokenAfterRevokingAllTokens() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtSecret("wms-secret-key-for-jwt-token-generation-2024");
        authProperties.setAccessTokenExpire(7200);
        authProperties.setRefreshTokenExpire(604800);

        TokenServiceImpl tokenService = new TokenServiceImpl(stringRedisTemplate, authProperties);
        TokenResp issuedTokens = tokenService.generateTokenPair(1L, "admin", List.of("ADMIN"));

        tokenService.revokeAllTokens(1L);

        BizException exception = assertThrows(BizException.class, () -> tokenService.refreshToken(issuedTokens.getRefreshToken()));
        assertEquals(AuthErrorCode.TOKEN_REVOKED.getCode(), exception.getCode());
    }
}
