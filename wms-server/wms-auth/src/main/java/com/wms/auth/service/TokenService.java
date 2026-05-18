package com.wms.auth.service;

import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.TokenValidateResp;
import io.jsonwebtoken.Claims;

import java.util.List;

public interface TokenService {

    TokenResp generateTokenPair(Long userId, String username, List<String> roles);

    Claims parseToken(String token);

    boolean validateToken(String token);

    boolean isTokenRevoked(String tokenId);

    void revokeToken(String accessToken);

    void revokeAllTokens(Long userId);

    void createSession(Long userId, String accessToken, String clientIp, String userAgent);

    void kickOutOldSession(Long userId);

    TokenResp refreshToken(String refreshToken);

    boolean renewTokenIfNeeded(String accessToken);

    /**
     * 校验Token并返回Token信息
     * 如果Token有效，解析Claims并构建TokenValidateResp；否则返回valid=false
     *
     * @param token JWT Token字符串
     * @return Token校验结果
     */
    TokenValidateResp validateTokenInfo(String token);
}
