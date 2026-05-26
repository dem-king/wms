package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.auth.service.LoginLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录锁定服务实现类
 * 处理登录失败计数、账号锁定状态判断、锁定剩余时间查询等逻辑
 */
@Service
@RequiredArgsConstructor
public class LoginLockServiceImpl implements LoginLockService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    /**
     * 记录登录失败
     * 失败次数达到阈值时锁定账号
     * 
     * @param username 用户名
     */
    @Override
    public void recordFailure(String username) {
        String failKey = AuthRedisKey.LOCK_FAIL_PREFIX + username;
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            stringRedisTemplate.expire(failKey, authProperties.getLockDuration(), TimeUnit.SECONDS);
        }
        if (count != null && count >= authProperties.getLoginFailThreshold()) {
            String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
            stringRedisTemplate.opsForValue().set(lockKey, AuthConstants.LOCK_FLAG, authProperties.getLockDuration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 判断账号是否被锁定
     * 
     * @param username 用户名
     * @return 是否锁定
     */
    @Override
    public boolean isLocked(String username) {
        String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(lockKey));
    }

    /**
     * 清除登录失败计数和锁定状态
     * 
     * @param username 用户名
     */
    @Override
    public void clearFailureCount(String username) {
        stringRedisTemplate.delete(AuthRedisKey.LOCK_FAIL_PREFIX + username);
        stringRedisTemplate.delete(AuthRedisKey.LOCK_STATE_PREFIX + username);
    }

    /**
     * 获取账号锁定剩余时间(分钟)
     * 
     * @param username 用户名
     * @return 剩余锁定分钟数
     */
    @Override
    public long getRemainingLockTime(String username) {
        String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
        Long ttl = stringRedisTemplate.getExpire(lockKey, TimeUnit.MINUTES);
        return ttl != null && ttl > 0 ? ttl : 0;
    }
}
