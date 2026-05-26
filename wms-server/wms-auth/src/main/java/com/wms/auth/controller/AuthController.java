package com.wms.auth.controller;

import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.PasswordReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.dto.UpdateProfileDto;
import com.wms.auth.domain.vo.AuthProfileVo;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.TokenValidateResp;
import com.wms.auth.domain.vo.UploadAvatarVo;
import com.wms.auth.service.AuthService;
import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.PasswordService;
import com.wms.auth.service.TokenService;
import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public R<LoginResp> login(@Valid @RequestBody LoginReq req, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return R.ok(authService.login(req, clientIp, userAgent));
    }

    @Operation(summary = "用户登出")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return R.ok();
    }

    @Operation(summary = "刷新Token")
    @PreAuthorize("permitAll()")
    @PostMapping("/token/refresh")
    public R<TokenResp> refreshToken(@Valid @RequestBody RefreshTokenReq req) {
        return R.ok(authService.refreshToken(req));
    }

    @Operation(summary = "校验Token")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/token/validate")
    public R<TokenValidateResp> validateToken(HttpServletRequest request) {
        String token = extractToken(request);
        return R.ok(tokenService.validateTokenInfo(token));
    }

    @DataScope
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户个人中心信息")
    @GetMapping("/profile")
    public R<AuthProfileVo> getProfile() {
        return R.ok(authService.getCurrentProfile());
    }

    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "auth", type = "修改", desc = "更新当前用户个人资料")
    @Operation(summary = "更新当前用户个人资料")
    @PutMapping("/profile")
    public R<AuthProfileVo> updateProfile(@Valid @RequestBody UpdateProfileDto dto) {
        return R.ok(authService.updateCurrentProfile(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "auth", type = "上传", desc = "上传当前用户头像")
    @Operation(summary = "上传当前用户头像")
    @PostMapping("/profile/avatar")
    public R<UploadAvatarVo> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return R.ok(authService.uploadCurrentUserAvatar(file));
    }

    @DataScope
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取头像内容")
    @GetMapping("/profile/avatar/content/{userId}/{fileName:.+}")
    public ResponseEntity<Resource> getAvatarContent(@PathVariable Long userId, @PathVariable String fileName) {
        Resource resource = authService.loadAvatarResource(userId, fileName);
        return ResponseEntity.ok()
                .contentType(resolveAvatarMediaType(fileName))
                .body(resource);
    }

    @DataScope
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "权限校验")
    @GetMapping("/authorize/check")
    public R<Boolean> checkPermission(@RequestParam String permCode) {
        Long userId = SecurityUtil.getCurrentUserId();
        return R.ok(authorizeService.hasPermission(userId, permCode));
    }

    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "auth", type = "修改", desc = "修改密码")
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

    private MediaType resolveAvatarMediaType(String fileName) {
        String lowercaseFileName = fileName.toLowerCase();
        if (lowercaseFileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lowercaseFileName.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }
}
