package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.domain.constant.AuthConstants;
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

/**
 * 授权服务实现类
 * 处理用户权限判断、权限编码查询(含Redis缓存)、权限缓存刷新
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizeServiceImpl implements AuthorizeService {

    private final StringRedisTemplate stringRedisTemplate;
    private final SysPermissionService sysPermissionService;

    /**
     * 判断用户是否拥有指定权限
     * 
     * @param userId 用户ID
     * @param permCode 权限编码
     * @return 是否拥有权限
     */
    @Override
    public boolean hasPermission(Long userId, String permCode) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permCode);
    }

    /**
     * 获取用户权限编码列表
     * 优先从Redis缓存读取，缓存未命中时查询数据库并写入缓存
     * 
     * @param userId 用户ID
     * @return 权限编码列表
     */
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
                    stringRedisTemplate.expire(permKey, AuthConstants.PERM_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
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

    /**
     * 刷新用户权限缓存
     * 删除Redis中的权限缓存
     * 
     * @param userId 用户ID
     */
    @Override
    public void refreshPermissionCache(Long userId) {
        try {
            stringRedisTemplate.delete(AuthRedisKey.PERM_PREFIX + userId);
        } catch (Exception e) {
            log.warn("清除权限缓存失败: {}", e.getMessage());
        }
    }
}
