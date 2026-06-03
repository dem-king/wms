package com.wms.auth.service.impl;

import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.dto.UpdateProfileDto;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.auth.domain.vo.AuthProfileVo;
import com.wms.auth.domain.vo.UploadAvatarVo;
import com.wms.auth.service.AuthAuditService;
import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.CaptchaService;
import com.wms.auth.service.CryptoService;
import com.wms.auth.service.LoginLockService;
import com.wms.auth.service.RateLimiterService;
import com.wms.auth.service.TokenService;
import com.wms.common.storage.StorageConstants;
import com.wms.common.storage.StorageStrategy;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.service.SysMenuService;
import com.wms.system.service.SysOperLogService;
import com.wms.system.service.SysRoleService;
import com.wms.system.service.SysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("AuthServiceImpl 个人中心测试")
@ExtendWith(MockitoExtension.class)
class AuthServiceImplProfileTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private LoginLockService loginLockService;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private AuthAuditService authAuditService;

    @Mock
    private SysUserService sysUserService;

    @Mock
    private SysMenuService sysMenuService;

    @Mock
    private SysOperLogService sysOperLogService;

    @Mock
    private AuthProperties authProperties;

    @Mock
    private StorageStrategy storageStrategy;

    @Mock
    private SysRoleService sysRoleService;

    @InjectMocks
    private AuthServiceImpl authService;

    @TempDir
    Path tempDir;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("应返回当前登录用户资料和最近一次成功登录信息")
    void shouldReturnCurrentUserProfileWithLatestSuccessLoginInfo() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1001L, null)
        );
        SysUserVo userVo = new SysUserVo();
        userVo.setId(1001L);
        userVo.setUsername("admin");
        userVo.setRealName("系统管理员");
        userVo.setAvatar("https://example.com/avatar.png");
        userVo.setDeptId(2001L);
        when(sysUserService.getById(1001L)).thenReturn(userVo);
        when(sysRoleService.getRoleCodesByUserId(1001L)).thenReturn(List.of("admin", "user"));
        when(authorizeService.getUserPermissions(1001L)).thenReturn(List.of("system:user:list", "system:role:list"));

        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setLoginIp("127.0.0.1");
        loginLog.setLoginTime(LocalDateTime.of(2026, 5, 21, 16, 30, 0));
        when(authAuditService.getLatestSuccessLoginLog(1001L)).thenReturn(loginLog);

        AuthProfileVo result = authService.getCurrentProfile();

        assertNotNull(result);
        assertEquals("admin", result.getUserInfo().getUsername());
        assertEquals("系统管理员", result.getUserInfo().getRealName());
        assertEquals(List.of("system:user:list", "system:role:list"), result.getPermissions());
        assertEquals(List.of("admin", "user"), result.getRoles());
        assertNotNull(result.getLastLoginInfo());
        assertEquals("127.0.0.1", result.getLastLoginInfo().getLoginIp());
        assertEquals(LocalDateTime.of(2026, 5, 21, 16, 30, 0), result.getLastLoginInfo().getLoginTime());
    }

    @Test
    @DisplayName("无成功登录日志时应返回空的最近登录信息")
    void shouldReturnNullLastLoginInfoWhenNoSuccessLoginLogExists() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1002L, null)
        );
        SysUserVo userVo = new SysUserVo();
        userVo.setId(1002L);
        userVo.setUsername("zhangsan");
        userVo.setRealName("张三");
        userVo.setDeptId(2002L);
        when(sysUserService.getById(1002L)).thenReturn(userVo);
        when(sysRoleService.getRoleCodesByUserId(1002L)).thenReturn(List.of());
        when(authorizeService.getUserPermissions(1002L)).thenReturn(List.of());
        when(authAuditService.getLatestSuccessLoginLog(1002L)).thenReturn(null);

        AuthProfileVo result = authService.getCurrentProfile();

        assertNotNull(result);
        assertEquals("zhangsan", result.getUserInfo().getUsername());
        assertNull(result.getLastLoginInfo());
        assertEquals(List.of(), result.getPermissions());
        assertEquals(List.of(), result.getRoles());
    }

    @Test
    @DisplayName("应只更新当前登录用户的个人资料并返回最新结果")
    void shouldUpdateCurrentUserProfileAndReturnLatestProfile() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1003L, null)
        );
        UpdateProfileDto dto = new UpdateProfileDto();
        dto.setRealName("李四");
        dto.setPhone("13800138001");
        dto.setEmail("lisi@example.com");
        dto.setAvatar("/wms/avatar/1003/avatar.png");

        SysUserVo updatedUser = new SysUserVo();
        updatedUser.setId(1003L);
        updatedUser.setUsername("lisi");
        updatedUser.setRealName("李四");
        updatedUser.setDeptId(2003L);
        updatedUser.setPhone("13800138001");
        updatedUser.setEmail("lisi@example.com");
        updatedUser.setAvatar("/wms/avatar/1003/avatar.png");

        when(sysUserService.updateProfile(1003L, "李四", "13800138001", "lisi@example.com", "/wms/avatar/1003/avatar.png"))
                .thenReturn(updatedUser);
        when(sysRoleService.getRoleCodesByUserId(1003L)).thenReturn(List.of("user"));
        when(authorizeService.getUserPermissions(1003L)).thenReturn(List.of("system:profile:update"));
        when(authAuditService.getLatestSuccessLoginLog(1003L)).thenReturn(null);

        AuthProfileVo result = authService.updateCurrentProfile(dto);

        assertNotNull(result);
        assertEquals("李四", result.getUserInfo().getRealName());
        assertEquals("13800138001", result.getUserInfo().getPhone());
        assertEquals("lisi@example.com", result.getUserInfo().getEmail());
        assertEquals("/wms/avatar/1003/avatar.png", result.getUserInfo().getAvatar());
        assertEquals(List.of("system:profile:update"), result.getPermissions());
        assertEquals(List.of("user"), result.getRoles());
    }

    @Test
    @DisplayName("头像上传成功时应返回可访问的头像地址")
    void shouldUploadAvatarAndReturnAvatarUrl() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1004L, null)
        );
        when(authProperties.getAvatarMaxSizeBytes()).thenReturn(1024 * 1024L);
        when(storageStrategy.upload(eq(StorageConstants.BUCKET_AVATARS), any(), any(), eq("image/png"), eq(6L)))
                .thenReturn("/api/storage/avatars/1004/avatar.png");
        MultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "avatar".getBytes());

        UploadAvatarVo result = authService.uploadCurrentUserAvatar(file);

        assertNotNull(result);
        assertEquals("/api/storage/avatars/1004/avatar.png", result.getAvatarUrl());
        verify(storageStrategy).upload(eq(StorageConstants.BUCKET_AVATARS), any(), any(), eq("image/png"), eq(6L));
    }

    @Test
    @DisplayName("读取头像内容时应通过统一存储策略下载")
    void shouldLoadAvatarResourceFromStorageStrategy() throws IOException {
        byte[] content = "avatar".getBytes();
        when(storageStrategy.download(StorageConstants.BUCKET_AVATARS, "1004/avatar.png"))
                .thenReturn(new ByteArrayInputStream(content));

        Resource result = authService.loadAvatarResource(1004L, "avatar.png");

        assertNotNull(result);
        assertEquals("avatar", new String(result.getInputStream().readAllBytes()));
        verify(storageStrategy).download(StorageConstants.BUCKET_AVATARS, "1004/avatar.png");
    }

    @Test
    @DisplayName("头像上传时应拒绝非图片类型文件")
    void shouldRejectUnsupportedAvatarContentType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1005L, null)
        );
        MultipartFile file = new MockMultipartFile("file", "avatar.txt", "text/plain", "avatar".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.uploadCurrentUserAvatar(file));

        assertEquals("头像仅支持 PNG、JPG、JPEG、WEBP 图片", exception.getMessage());
    }

    @Test
    @DisplayName("头像上传时应拒绝超出大小限制的文件")
    void shouldRejectOversizedAvatarFile() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1006L, null)
        );
        when(authProperties.getAvatarMaxSizeBytes()).thenReturn(4L);
        MultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "avatar".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.uploadCurrentUserAvatar(file));

        assertEquals("头像大小不能超过4字节", exception.getMessage());
    }
}
