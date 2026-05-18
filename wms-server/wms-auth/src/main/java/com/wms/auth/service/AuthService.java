package com.wms.auth.service;

import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;

public interface AuthService {

    LoginResp login(LoginReq req, String clientIp, String userAgent);

    void logout(String accessToken);

    TokenResp refreshToken(RefreshTokenReq req);
}
