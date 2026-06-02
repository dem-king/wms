package com.wms.system.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统配置统一管理器
 * 提供带Redis缓存的配置读取，替代@Value和直接查Mapper
 * 缓存key格式: sys:config:{configKey}
 * 缓存过期时间: 30分钟（配置修改时主动删除缓存）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysConfigManager {

    private final SysConfigMapper sysConfigMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /** Redis缓存key前缀 */
    private static final String CACHE_PREFIX = "sys:config:";
    /** 缓存过期时间（分钟） */
    private static final long CACHE_EXPIRE_MINUTES = 30;

    /**
     * 获取字符串配置值
     * 优先从Redis缓存读取，缓存未命中则查库并回填缓存
     *
     * @param key 配置键
     * @return 配置值，不存在返回null
     */
    public String getValue(String key) {
        // 优先从Redis缓存读取
        String cacheKey = CACHE_PREFIX + key;
        try {
            String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                return cachedValue;
            }
        } catch (Exception e) {
            // Redis异常时降级为直接查库，不影响业务
            log.warn("读取配置缓存失败, key={}, 降级查库", key, e);
        }

        // 缓存未命中，查询数据库
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config == null) {
            return null;
        }

        String value = config.getConfigValue();
        // 回填缓存，写入失败不影响业务
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入配置缓存失败, key={}", key, e);
        }
        return value;
    }

    /**
     * 获取配置值，带默认值
     * 配置项不存在时返回默认值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在则返回默认值
     */
    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数配置值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值的整数形式，转换失败返回默认值
     */
    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("配置值转换为整数失败, key={}, value={}, 使用默认值={}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取长整型配置值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值的长整型形式，转换失败返回默认值
     */
    public long getLongValue(String key, long defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("配置值转换为长整型失败, key={}, value={}, 使用默认值={}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取布尔配置值
     * 支持 "true"/"false" 字符串解析
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值的布尔形式
     */
    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * 清除指定配置的缓存
     * 配置修改时调用，保证下次读取获取最新值
     *
     * @param key 配置键
     */
    public void evictCache(String key) {
        try {
            stringRedisTemplate.delete(CACHE_PREFIX + key);
        } catch (Exception e) {
            log.warn("清除配置缓存失败, key={}", key, e);
        }
    }

    /**
     * 清除所有配置缓存
     * 批量修改配置时使用
     */
    public void evictAllCache() {
        try {
            var keys = stringRedisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("清除所有配置缓存失败", e);
        }
    }
}