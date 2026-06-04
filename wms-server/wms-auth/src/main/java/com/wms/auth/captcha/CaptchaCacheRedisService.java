package com.wms.auth.captcha;

import com.anji.captcha.service.CaptchaCacheService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 验证码 Redis 缓存服务
 * 实现 anji-plus/captcha 的 CaptchaCacheService 接口，
 * 使用 StringRedisTemplate 操作 Redis，increment() 方法使用 Lua 脚本保证原子性
 */
public class CaptchaCacheRedisService implements CaptchaCacheService {

    /** Lua 脚本：key 存在则 INCRBY，不存在则直接返回传入值 */
    private static final String LUA_SCRIPT =
        "local key = KEYS[1] " +
        "local incrementValue = tonumber(ARGV[1]) " +
        "if redis.call('EXISTS', key) == 1 then " +
        "    return redis.call('INCRBY', key, incrementValue) " +
        "else " +
        "    return incrementValue " +
        "end";

    private StringRedisTemplate stringRedisTemplate;

    /**
     * 注入 StringRedisTemplate（由 CaptchaConfig 调用）
     *
     * @param stringRedisTemplate Redis 字符串操作模板
     */
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 返回缓存类型标识
     * 与 aj-captcha 配置项 aj.captcha.cache-type=redis 对应
     *
     * @return 缓存类型字符串 "redis"
     */
    @Override
    public String type() {
        return "redis";
    }

    /**
     * 设置缓存键值对并指定过期时间
     *
     * @param key             缓存键
     * @param value           缓存值
     * @param expiresInSeconds 过期时间（秒）
     */
    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 判断缓存键是否存在
     *
     * @param key 缓存键
     * @return 键是否存在
     */
    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 删除缓存键
     *
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，键不存在时返回 null
     */
    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 原子递增缓存值
     * 使用 Lua 脚本保证原子性：key 存在则 INCRBY，不存在则直接返回传入值
     *
     * @param key 缓存键
     * @param val 递增量
     * @return 递增后的值
     */
    @Override
    public Long increment(String key, long val) {
        RedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        return stringRedisTemplate.execute(script, Collections.singletonList(key), String.valueOf(val));
    }

    /**
     * 设置缓存键的过期时间
     *
     * @param key 缓存键
     * @param l   过期时间（秒）
     */
    public void setExpire(String key, long l) {
        stringRedisTemplate.expire(key, l, TimeUnit.SECONDS);
    }
}
