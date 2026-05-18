package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.service.AuthorizeService;
import com.wms.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizeServiceImpl implements AuthorizeService {

    private final StringRedisTemplate stringRedisTemplate;
    private final SysPermissionService sysPermissionService;

    @Override
    public boolean hasPermission(Long userId, String permCode) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permCode);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        try {
            String permKey = AuthRedisKey.PERM_PREFIX + userId;
            List<String> cached = stringRedisTemplate.opsForList().range(permKey, 0, -1);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis不可用,跳过权限缓存: {}", e.getMessage());
        }

        try {
            List<String> permissions = sysPermissionService.getPermCodesByUserId(userId);
            try {
                if (!permissions.isEmpty()) {
                    String permKey = AuthRedisKey.PERM_PREFIX + userId;
                    stringRedisTemplate.opsForList().rightPushAll(permKey, permissions);
                    stringRedisTemplate.expire(permKey, 5, TimeUnit.MINUTES);
                }
            } catch (Exception e) {
                log.warn("写入权限缓存失败: {}", e.getMessage());
            }
            return permissions;
        } catch (Exception e) {
            log.error("获取用户权限失败,userId={}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void refreshPermissionCache(Long userId) {
        try {
            stringRedisTemplate.delete(AuthRedisKey.PERM_PREFIX + userId);
        } catch (Exception e) {
            log.warn("清除权限缓存失败: {}", e.getMessage());
        }
    }
}
