package com.wms.auth.service.impl;

import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.dto.UpdateProfileDto;
import com.wms.auth.domain.vo.AuthProfileVo;
import com.wms.auth.domain.vo.LastLoginInfoVo;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.UploadAvatarVo;
import com.wms.auth.domain.vo.UserInfoVo;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.enums.AuthOperTypeEnum;
import com.wms.auth.service.*;
import com.wms.common.constant.BizConstants;
import com.wms.common.exception.BizException;
import com.wms.common.storage.StorageConstants;
import com.wms.common.storage.StorageStrategy;
import com.wms.common.util.SecurityUtil;
import com.wms.common.util.UserAgentParser;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.vo.MenuTreeVo;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.service.SysMenuService;
import com.wms.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 认证服务实现类
 * 处理登录、登出、令牌刷新、用户资料查询与更新、头像上传等业务逻辑
 */
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
    private final StorageStrategy storageStrategy;

    /**
     * 用户登录
     * 依次执行: 限流校验→验证码校验→锁定校验→密码解密→用户校验→权限加载→令牌生成→会话创建→登录记录
     * 
     * @param req 登录请求
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 登录响应(含令牌、权限、菜单)
     */
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

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        resp.setUserInfo(userInfo);

        return resp;
    }

    /**
     * 用户登出
     * 撤销令牌和所有关联令牌，记录操作日志
     * 
     * @param accessToken 访问令牌
     */
    @Override
    public void logout(String accessToken) {
        try {
            tokenService.revokeToken(accessToken);
            var claims = tokenService.parseToken(accessToken);
            Long userId = Long.valueOf(claims.getSubject());
            tokenService.revokeAllTokens(userId);

            SysOperLog operLog = new SysOperLog();
            operLog.setModule(AuthConstants.OPER_LOG_MODULE_AUTH);
            operLog.setType(AuthOperTypeEnum.LOGOUT.getDesc());
            operLog.setDesc(AuthConstants.OPER_LOG_DESC_LOGOUT);
            operLog.setOperatorId(userId);
            operLog.setOperatorName(claims.get("username", String.class));
            operLog.setStatus(SysLogConstants.OPER_STATUS_SUCCESS);
            operLog.setOperTime(java.time.LocalDateTime.now());
            authAuditService.recordOperLog(operLog);
        } catch (BizException e) {
            log.debug("登出时Token已失效(幂等): {}", e.getMessage());
        }
    }

    /**
     * 刷新令牌
     * 
     * @param req 刷新令牌请求
     * @return 新的令牌对
     */
    @Override
    public TokenResp refreshToken(RefreshTokenReq req) {
        return tokenService.refreshToken(req.getRefreshToken());
    }

    /**
     * 获取当前登录用户资料
     * 包括用户信息、权限、角色和最近登录信息
     * 
     * @return 用户资料VO
     */
    @Override
    public AuthProfileVo getCurrentProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BizException(AuthErrorCode.TOKEN_INVALID.getCode(), AuthErrorCode.TOKEN_INVALID.getMsg());
        }

        SysUserVo user = sysUserService.getById(userId);
        List<String> permissions = authorizeService.getUserPermissions(userId);
        List<String> roles = sysUserService.getUserRoles(userId).stream()
                .map(String::valueOf)
                .toList();
        SysLoginLog lastLoginLog = authAuditService.getLatestSuccessLoginLog(userId);

        AuthProfileVo profile = new AuthProfileVo();
        profile.setUserInfo(buildUserInfo(user));
        profile.setPermissions(permissions);
        profile.setRoles(roles);
        profile.setLastLoginInfo(buildLastLoginInfo(lastLoginLog));
        return profile;
    }

    /**
     * 更新当前登录用户资料
     * 
     * @param dto 资料更新参数
     * @return 更新后的用户资料VO
     */
    @Override
    public AuthProfileVo updateCurrentProfile(UpdateProfileDto dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BizException(AuthErrorCode.TOKEN_INVALID.getCode(), AuthErrorCode.TOKEN_INVALID.getMsg());
        }

        SysUserVo user = sysUserService.updateProfile(
                userId,
                dto.getRealName(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getAvatar()
        );
        List<String> permissions = authorizeService.getUserPermissions(userId);
        List<String> roles = sysUserService.getUserRoles(userId).stream()
                .map(String::valueOf)
                .toList();
        SysLoginLog lastLoginLog = authAuditService.getLatestSuccessLoginLog(userId);

        AuthProfileVo profile = new AuthProfileVo();
        profile.setUserInfo(buildUserInfo(user));
        profile.setPermissions(permissions);
        profile.setRoles(roles);
        profile.setLastLoginInfo(buildLastLoginInfo(lastLoginLog));
        return profile;
    }

    /**
     * 上传当前用户头像
     * 通过StorageStrategy统一上传
     * 
     * @param file 头像文件
     * @return 头像上传结果VO
     */
    @Override
    public UploadAvatarVo uploadCurrentUserAvatar(MultipartFile file) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BizException(AuthErrorCode.TOKEN_INVALID.getCode(), AuthErrorCode.TOKEN_INVALID.getMsg());
        }

        validateAvatarFile(file);
        String extension = resolveFileExtension(file.getOriginalFilename(), file.getContentType());
        String storedFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectName = userId + "/" + storedFileName;

        // 通过StorageStrategy统一上传，自动适配本地/MinIO存储
        String url;
        try {
            url = storageStrategy.upload(StorageConstants.BUCKET_AVATARS, objectName,
                    file.getInputStream(), file.getContentType(), file.getSize());
        } catch (Exception e) {
            throw new BizException("头像上传失败: " + e.getMessage());
        }

        UploadAvatarVo uploadAvatarVo = new UploadAvatarVo();
        uploadAvatarVo.setAvatarUrl(url);
        return uploadAvatarVo;
    }

    /**
     * 加载头像资源
     * 
     * @param userId 用户ID
     * @param fileName 文件名
     * @return 头像资源
     */
    @Override
    public Resource loadAvatarResource(Long userId, String fileName) {
        // 通过StorageStrategy统一读取，支持本地/MinIO存储
        String objectName = userId + "/" + fileName;
        InputStream is = storageStrategy.download(StorageConstants.BUCKET_AVATARS, objectName);
        return new InputStreamResource(is);
    }

    private void recordLoginSuccess(String username, Long userId, String ip, String ua) {
        UserAgentParser.ParsedUserAgent parsedUserAgent = UserAgentParser.parse(ua);
        authAuditService.recordLoginLog(
                userId,
                username,
                ip,
                parsedUserAgent.browser(),
                parsedUserAgent.os(),
                SysLogConstants.LOGIN_STATUS_SUCCESS,
                null
        );
    }

    private void recordLoginFail(String username, Long userId, String ip, String ua, String reason) {
        UserAgentParser.ParsedUserAgent parsedUserAgent = UserAgentParser.parse(ua);
        authAuditService.recordLoginLog(
                userId,
                username,
                ip,
                parsedUserAgent.browser(),
                parsedUserAgent.os(),
                SysLogConstants.LOGIN_STATUS_FAIL,
                reason
        );
    }

    private UserInfoVo buildUserInfo(SysUserVo user) {
        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        return userInfo;
    }

    private LastLoginInfoVo buildLastLoginInfo(SysLoginLog loginLog) {
        if (loginLog == null) {
            return null;
        }
        LastLoginInfoVo lastLoginInfo = new LastLoginInfoVo();
        lastLoginInfo.setLoginIp(loginLog.getLoginIp());
        lastLoginInfo.setLoginTime(loginLog.getLoginTime());
        return lastLoginInfo;
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("头像文件不能为空");
        }
        if (!isSupportedAvatarContentType(file.getContentType())) {
            throw new BizException("头像仅支持 PNG、JPG、JPEG、WEBP 图片");
        }
        if (file.getSize() > authProperties.getAvatarMaxSizeBytes()) {
            throw new BizException("头像大小不能超过" + authProperties.getAvatarMaxSizeBytes() + "字节");
        }
    }

    private boolean isSupportedAvatarContentType(String contentType) {
        return "image/png".equals(contentType)
                || "image/jpg".equals(contentType)
                || "image/jpeg".equals(contentType)
                || "image/webp".equals(contentType);
    }

    private String resolveFileExtension(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        return ".jpg";
    }
}
