package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.service.LoginLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginLockServiceImpl implements LoginLockService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    @Override
    public void recordFailure(String username) {
        String failKey = AuthRedisKey.LOCK_FAIL_PREFIX + username;
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            stringRedisTemplate.expire(failKey, authProperties.getLockDuration(), TimeUnit.SECONDS);
        }
        if (count != null && count >= authProperties.getLoginFailThreshold()) {
            String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
            stringRedisTemplate.opsForValue().set(lockKey, "1", authProperties.getLockDuration(), TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isLocked(String username) {
        String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(lockKey));
    }

    @Override
    public void clearFailureCount(String username) {
        stringRedisTemplate.delete(AuthRedisKey.LOCK_FAIL_PREFIX + username);
        stringRedisTemplate.delete(AuthRedisKey.LOCK_STATE_PREFIX + username);
    }

    @Override
    public long getRemainingLockTime(String username) {
        String lockKey = AuthRedisKey.LOCK_STATE_PREFIX + username;
        Long ttl = stringRedisTemplate.getExpire(lockKey, TimeUnit.MINUTES);
        return ttl != null && ttl > 0 ? ttl : 0;
    }
}
