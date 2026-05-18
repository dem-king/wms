package com.wms.auth.service.impl;

import com.wms.auth.constant.AuthRedisKey;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.RateLimiterService;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;

    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local window = tonumber(ARGV[2]) " +
            "local current = tonumber(redis.call('get', key) or '0') " +
            "if current >= limit then return 0 end " +
            "redis.call('incr', key) " +
            "if current == 0 then redis.call('expire', key, window) end " +
            "return 1";

    @Override
    public void tryAcquire(String ip) {
        String key = AuthRedisKey.RATE_LIMIT_PREFIX + ip;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(authProperties.getIpRateLimitThreshold()),
                "60"
        );
        if (result == null || result == 0L) {
            throw new BizException(AuthErrorCode.IP_RATE_LIMITED.getCode(), AuthErrorCode.IP_RATE_LIMITED.getMsg());
        }
    }
}
