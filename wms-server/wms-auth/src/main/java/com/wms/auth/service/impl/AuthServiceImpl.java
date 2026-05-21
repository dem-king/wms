package com.wms.auth.service.impl;

import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.entity.AuthLoginLog;
import com.wms.auth.domain.entity.AuthOperLog;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.UserInfoVO;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.enums.AuthOperTypeEnum;
import com.wms.auth.enums.LoginResultEnum;
import com.wms.auth.service.*;
import com.wms.common.constant.BizConstants;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.common.exception.BizException;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.vo.MenuTreeVo;
import com.wms.system.service.SysMenuService;
import com.wms.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RateLimiterService rateLimiterService;
    private final CaptchaService captchaService;
    private final LoginLockService loginLockService;
    private final CryptoService cryptoService;
    private final TokenService tokenService;
    private final AuthorizeService authorizeService;
    private final AuthAuditService authAuditService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final AuthProperties authProperties;

    @Override
    public LoginResp login(LoginReq req, String clientIp, String userAgent) {
        rateLimiterService.tryAcquire(clientIp);

        if (authProperties.isCaptchaEnabled()
                && req.getCaptchaKey() != null
                && req.getCaptchaText() != null) {
            captchaService.validateCaptcha(req.getCaptchaKey(), req.getCaptchaText());
        }

        if (loginLockService.isLocked(req.getUsername())) {
            long remaining = loginLockService.getRemainingLockTime(req.getUsername());
            throw new BizException(AuthErrorCode.ACCOUNT_LOCKED.getCode(),
                    AuthErrorCode.ACCOUNT_LOCKED.getMsg() + "，剩余" + remaining + "分钟");
        }

        String rawPassword = cryptoService.decryptPassword(req.getEncryptedPassword());

        SysUser user = sysUserService.getByUsername(req.getUsername());
        if (user == null) {
            loginLockService.recordFailure(req.getUsername());
            recordLoginFail(req.getUsername(), null, clientIp, userAgent, "用户不存在");
            throw new BizException(AuthErrorCode.CREDENTIAL_INVALID.getCode(),
                    AuthErrorCode.CREDENTIAL_INVALID.getMsg());
        }

        if (user.getStatus() != null && user.getStatus() == BizConstants.STATUS_DISABLED) {
            recordLoginFail(user.getUsername(), user.getId(), clientIp, userAgent, "账号禁用");
            throw new BizException(AuthErrorCode.ACCOUNT_DISABLED.getCode(),
                    AuthErrorCode.ACCOUNT_DISABLED.getMsg());
        }

        if (!cryptoService.verifyPassword(rawPassword, user.getPassword())) {
            loginLockService.recordFailure(user.getUsername());
            recordLoginFail(user.getUsername(), user.getId(), clientIp, userAgent, "密码错误");
            throw new BizException(AuthErrorCode.CREDENTIAL_INVALID.getCode(),
                    AuthErrorCode.CREDENTIAL_INVALID.getMsg());
        }

        loginLockService.clearFailureCount(user.getUsername());

        List<String> roles = Collections.emptyList();
        List<String> permissions;
        try {
            permissions = authorizeService.getUserPermissions(user.getId());
        } catch (Exception e) {
            log.warn("获取用户权限降级,userId={}: {}", user.getId(), e.getMessage());
            permissions = Collections.emptyList();
        }

        // 查询用户菜单树(用于前端动态路由)
        List<MenuTreeVo> menus;
        try {
            menus = sysMenuService.getUserMenuTree();
        } catch (Exception e) {
            log.warn("获取用户菜单树降级,userId={}: {}", user.getId(), e.getMessage());
            menus = Collections.emptyList();
        }

        TokenResp tokenResp = tokenService.generateTokenPair(user.getId(), user.getUsername(), roles);

        tokenService.kickOutOldSession(user.getId());
        tokenService.createSession(user.getId(), tokenResp.getAccessToken(), clientIp, userAgent);

        recordLoginSuccess(user.getUsername(), user.getId(), clientIp, userAgent);

        LoginResp resp = new LoginResp();
        resp.setAccessToken(tokenResp.getAccessToken());
        resp.setRefreshToken(tokenResp.getRefreshToken());
        resp.setTokenType(tokenResp.getTokenType());
        resp.setExpiresIn(tokenResp.getExpiresIn());
        resp.setPermissions(permissions);
        resp.setMenus(menus != null ? menus.stream().map(m -> (Object) m).toList() : Collections.emptyList());

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        resp.setUserInfo(userInfo);

        return resp;
    }

    @Override
    public void logout(String accessToken) {
        try {
            tokenService.revokeToken(accessToken);
            var claims = tokenService.parseToken(accessToken);
            Long userId = Long.valueOf(claims.getSubject());
            tokenService.revokeAllTokens(userId);

            AuthOperLog operLog = new AuthOperLog();
            operLog.setUserId(userId);
            operLog.setUsername(claims.get("username", String.class));
            operLog.setOperType(AuthOperTypeEnum.LOGOUT.getCode());
            operLog.setOperResult(AuthConstants.OPER_RESULT_SUCCESS);
            operLog.setOperTime(LocalDateTime.now());
            authAuditService.recordOperLog(operLog);
        } catch (BizException e) {
            log.debug("登出时Token已失效(幂等): {}", e.getMessage());
        }
    }

    @Override
    public TokenResp refreshToken(RefreshTokenReq req) {
        return tokenService.refreshToken(req.getRefreshToken());
    }

    private void recordLoginSuccess(String username, Long userId, String ip, String ua) {
        AuthLoginLog loginLog = new AuthLoginLog();
        loginLog.setUsername(username);
        loginLog.setUserId(userId);
        loginLog.setLoginResult(LoginResultEnum.SUCCESS.getCode());
        loginLog.setLoginIp(ip);
        loginLog.setUserAgent(ua != null ? ua.substring(0, Math.min(AuthConstants.USER_AGENT_MAX_LENGTH, ua.length())) : null);
        loginLog.setLoginTime(LocalDateTime.now());
        authAuditService.recordLoginLog(loginLog);
    }

    private void recordLoginFail(String username, Long userId, String ip, String ua, String reason) {
        AuthLoginLog loginLog = new AuthLoginLog();
        loginLog.setUsername(username);
        loginLog.setUserId(userId);
        loginLog.setLoginResult(LoginResultEnum.FAIL.getCode());
        loginLog.setLoginIp(ip);
        loginLog.setUserAgent(ua != null ? ua.substring(0, Math.min(AuthConstants.USER_AGENT_MAX_LENGTH, ua.length())) : null);
        loginLog.setFailReason(reason);
        loginLog.setLoginTime(LocalDateTime.now());
        authAuditService.recordLoginLog(loginLog);
    }
}
