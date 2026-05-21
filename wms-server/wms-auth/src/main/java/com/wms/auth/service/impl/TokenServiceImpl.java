package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.constant.TokenConstants;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.TokenValidateResp;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.TokenService;
import com.wms.common.exception.BizException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = authProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(Arrays.copyOf(keyBytes, TokenConstants.HMAC_KEY_LENGTH));
    }

    @Override
    public TokenResp generateTokenPair(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString().replace("-", "");

        String accessToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(authProperties.getAccessTokenExpire())))
                .signWith(getSigningKey())
                .compact();

        String refreshJti = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .id(refreshJti)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(authProperties.getRefreshTokenExpire())))
                .signWith(getSigningKey())
                .compact();

        TokenResp resp = new TokenResp();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setTokenType(TokenConstants.TOKEN_TYPE_BEARER);
        resp.setExpiresIn(authProperties.getAccessTokenExpire());
        return resp;
    }

    @Override
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BizException(AuthErrorCode.TOKEN_EXPIRED.getCode(), AuthErrorCode.TOKEN_EXPIRED.getMsg());
        } catch (JwtException e) {
            throw new BizException(AuthErrorCode.TOKEN_INVALID.getCode(), AuthErrorCode.TOKEN_INVALID.getMsg());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            String jti = claims.getId();
            if (isTokenRevoked(jti)) {
                return false;
            }
            return true;
        } catch (BizException e) {
            return false;
        }
    }

    @Override
    public boolean isTokenRevoked(String tokenId) {
        try {
            return Boolean.TRUE.equals(
                    stringRedisTemplate.hasKey(AuthRedisKey.TOKEN_PREFIX + tokenId));
        } catch (Exception e) {
            log.warn("Redis不可用,降级为仅签名校验: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void revokeToken(String accessToken) {
        try {
            Claims claims = parseToken(accessToken);
            String jti = claims.getId();
            long remainingSeconds = claims.getExpiration().getTime() / 1000
                    - System.currentTimeMillis() / 1000;
            if (remainingSeconds > 0) {
                stringRedisTemplate.opsForValue().set(
                        AuthRedisKey.TOKEN_PREFIX + jti, TokenConstants.REVOKE_FLAG,
                        remainingSeconds, TimeUnit.SECONDS);
            }
        } catch (BizException e) {
            log.debug("撤销Token时解析失败,Token可能已失效: {}", e.getMessage());
        }
    }

    @Override
    public void revokeAllTokens(Long userId) {
        String sessionKey = AuthRedisKey.SESSION_PREFIX + userId;
        String sessionData = stringRedisTemplate.opsForValue().get(sessionKey);
        if (sessionData != null) {
            stringRedisTemplate.delete(sessionKey);
        }
    }

    @Override
    public void createSession(Long userId, String accessToken, String clientIp, String userAgent) {
        String sessionKey = AuthRedisKey.SESSION_PREFIX + userId;
        String sessionValue = String.join("|",
                String.valueOf(userId),
                accessToken.substring(0, Math.min(TokenConstants.TOKEN_PREFIX_LENGTH, accessToken.length())),
                clientIp,
                userAgent != null ? userAgent.substring(0, Math.min(TokenConstants.SESSION_UA_MAX_LENGTH, userAgent.length())) : "",
                String.valueOf(System.currentTimeMillis()));
        stringRedisTemplate.opsForValue().set(sessionKey, sessionValue,
                authProperties.getAccessTokenExpire(), TimeUnit.SECONDS);
    }

    @Override
    public void kickOutOldSession(Long userId) {
        String sessionKey = AuthRedisKey.SESSION_PREFIX + userId;
        String oldSession = stringRedisTemplate.opsForValue().getAndDelete(sessionKey);
        if (oldSession != null) {
            log.info("用户{}旧会话已踢出", userId);
        }
    }

    @Override
    public TokenResp refreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        if (!TokenConstants.TOKEN_TYPE_REFRESH.equals(claims.get("type", String.class))) {
            throw new BizException(AuthErrorCode.TOKEN_INVALID.getCode(), "非刷新Token");
        }
        String refreshJti = claims.getId();
        if (isTokenRevoked(refreshJti)) {
            throw new BizException(AuthErrorCode.TOKEN_REVOKED.getCode(), AuthErrorCode.TOKEN_REVOKED.getMsg());
        }

        long remainingSeconds = claims.getExpiration().getTime() / 1000
                - System.currentTimeMillis() / 1000;
        if (remainingSeconds > 0) {
            stringRedisTemplate.opsForValue().set(
                    AuthRedisKey.TOKEN_PREFIX + refreshJti, TokenConstants.REVOKE_FLAG,
                    remainingSeconds, TimeUnit.SECONDS);
        }

        Long userId = Long.valueOf(claims.getSubject());
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.emptyList();
        }
        String username = claims.get("username", String.class);

        return generateTokenPair(userId, username != null ? username : "", roles);
    }

    @Override
    public boolean renewTokenIfNeeded(String accessToken) {
        try {
            Claims claims = parseToken(accessToken);
            long remainingSeconds = claims.getExpiration().getTime() / 1000
                    - System.currentTimeMillis() / 1000;
            return remainingSeconds > 0
                    && remainingSeconds < authProperties.getTokenRenewThreshold();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TokenValidateResp validateTokenInfo(String token) {
        TokenValidateResp resp = new TokenValidateResp();
        if (token != null && validateToken(token)) {
            Claims claims = parseToken(token);
            resp.setValid(true);
            resp.setUserId(Long.valueOf(claims.getSubject()));
            resp.setUsername(claims.get("username", String.class));
        } else {
            resp.setValid(false);
        }
        return resp;
    }
}
