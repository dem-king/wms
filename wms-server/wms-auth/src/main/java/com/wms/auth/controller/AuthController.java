package com.wms.auth.controller;

import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.PasswordReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.TokenValidateResp;
import com.wms.auth.service.AuthService;
import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.PasswordService;
import com.wms.auth.service.TokenService;
import com.wms.common.domain.R;
import com.wms.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final AuthorizeService authorizeService;
    private final PasswordService passwordService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResp> login(@Valid @RequestBody LoginReq req, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return R.ok(authService.login(req, clientIp, userAgent));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return R.ok();
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/token/refresh")
    public R<TokenResp> refreshToken(@Valid @RequestBody RefreshTokenReq req) {
        return R.ok(authService.refreshToken(req));
    }

    @Operation(summary = "校验Token")
    @GetMapping("/token/validate")
    public R<TokenValidateResp> validateToken(HttpServletRequest request) {
        String token = extractToken(request);
        return R.ok(tokenService.validateTokenInfo(token));
    }

    @Operation(summary = "权限校验")
    @GetMapping("/authorize/check")
    public R<Boolean> checkPermission(@RequestParam String permCode) {
        Long userId = SecurityUtil.getCurrentUserId();
        return R.ok(authorizeService.hasPermission(userId, permCode));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public R<Void> changePassword(@Valid @RequestBody PasswordReq req) {
        Long userId = SecurityUtil.getCurrentUserId();
        passwordService.changePassword(userId, req);
        return R.ok();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
