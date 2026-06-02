package com.wms.system.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.mapper.SysConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysConfigManager 单元测试
 * 验证带Redis缓存的配置读取、类型转换、缓存清除等逻辑
 */
@DisplayName("SysConfigManager 测试")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SysConfigManagerTest {

    @Mock
    private SysConfigMapper sysConfigMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private SysConfigManager configManager;

    @BeforeEach
    void setUp() {
        // 默认行为：stringRedisTemplate.opsForValue() 返回 mock的valueOperations
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        configManager = new SysConfigManager(sysConfigMapper, stringRedisTemplate);
    }

    // ==================== getValue ====================

    @Test
    @DisplayName("getValue - 缓存命中时直接返回，不查库")
    void getValue_cacheHit_returnsCachedValue() {
        when(valueOperations.get("sys:config:test.key")).thenReturn("cachedValue");

        String result = configManager.getValue("test.key");

        assertEquals("cachedValue", result);
        verify(sysConfigMapper, never()).selectOne(any());
    }

    @Test
    @DisplayName("getValue - 缓存未命中时查库并回填缓存")
    void getValue_cacheMiss_queriesDbAndFillsCache() {
        when(valueOperations.get("sys:config:test.key")).thenReturn(null);
        SysConfig config = new SysConfig();
        config.setConfigKey("test.key");
        config.setConfigValue("dbValue");
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

        String result = configManager.getValue("test.key");

        assertEquals("dbValue", result);
        verify(valueOperations).set(eq("sys:config:test.key"), eq("dbValue"), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("getValue - 数据库中也无此配置时返回null")
    void getValue_configNotExists_returnsNull() {
        when(valueOperations.get("sys:config:nonexistent")).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        String result = configManager.getValue("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("getValue - Redis异常时降级查库")
    void getValue_redisException_fallsBackToDb() {
        when(valueOperations.get("sys:config:test.key")).thenThrow(new RuntimeException("Redis down"));
        SysConfig config = new SysConfig();
        config.setConfigValue("fallbackValue");
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

        String result = configManager.getValue("test.key");

        assertEquals("fallbackValue", result);
    }

    @Test
    @DisplayName("getValue带默认值 - 配置不存在时返回默认值")
    void getValue_withDefaultValue_returnsDefaultWhenNull() {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        String result = configManager.getValue("nonexistent", "defaultValue");

        assertEquals("defaultValue", result);
    }

    @Test
    @DisplayName("getValue带默认值 - 配置存在时返回配置值")
    void getValue_withDefaultValue_returnsConfigValueWhenExists() {
        when(valueOperations.get("sys:config:exist.key")).thenReturn("realValue");

        String result = configManager.getValue("exist.key", "defaultValue");

        assertEquals("realValue", result);
    }

    // ==================== getIntValue ====================

    @Test
    @DisplayName("getIntValue - 正常转换整数值")
    void getIntValue_normalConversion() {
        when(valueOperations.get("sys:config:int.key")).thenReturn("42");

        int result = configManager.getIntValue("int.key", 0);

        assertEquals(42, result);
    }

    @Test
    @DisplayName("getIntValue - 格式无效时返回默认值")
    void getIntValue_invalidFormat_returnsDefault() {
        when(valueOperations.get("sys:config:invalid.key")).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(createConfig("invalid.key", "notANumber"));

        int result = configManager.getIntValue("invalid.key", 99);

        assertEquals(99, result);
    }

    @Test
    @DisplayName("getIntValue - 配置不存在时返回默认值")
    void getIntValue_configNotExists_returnsDefault() {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        int result = configManager.getIntValue("nonexistent", 99);

        assertEquals(99, result);
    }

    // ==================== getLongValue ====================

    @Test
    @DisplayName("getLongValue - 正常转换长整数值")
    void getLongValue_normalConversion() {
        when(valueOperations.get("sys:config:long.key")).thenReturn("7200");

        long result = configManager.getLongValue("long.key", 0L);

        assertEquals(7200L, result);
    }

    @Test
    @DisplayName("getLongValue - 格式无效时返回默认值")
    void getLongValue_invalidFormat_returnsDefault() {
        when(valueOperations.get("sys:config:invalid.key")).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(createConfig("invalid.key", "notANumber"));

        long result = configManager.getLongValue("invalid.key", 100L);

        assertEquals(100L, result);
    }

    // ==================== getBooleanValue ====================

    @Test
    @DisplayName("getBooleanValue - true字符串解析")
    void getBooleanValue_trueString() {
        when(valueOperations.get("sys:config:bool.key")).thenReturn("true");

        boolean result = configManager.getBooleanValue("bool.key", false);

        assertTrue(result);
    }

    @Test
    @DisplayName("getBooleanValue - false字符串解析")
    void getBooleanValue_falseString() {
        when(valueOperations.get("sys:config:bool.key")).thenReturn("false");

        boolean result = configManager.getBooleanValue("bool.key", true);

        assertFalse(result);
    }

    @Test
    @DisplayName("getBooleanValue - 配置不存在时返回默认值")
    void getBooleanValue_configNotExists_returnsDefault() {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(sysConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        boolean result = configManager.getBooleanValue("nonexistent", true);

        assertTrue(result);
    }

    @Test
    @DisplayName("getBooleanValue - 非true/false字符串解析为false")
    void getBooleanValue_nonBooleanString_returnsFalse() {
        when(valueOperations.get("sys:config:bool.key")).thenReturn("yes");

        // Boolean.parseBoolean("yes") 返回false
        boolean result = configManager.getBooleanValue("bool.key", true);

        assertFalse(result);
    }

    // ==================== evictCache ====================

    @Test
    @DisplayName("evictCache - 清除指定key的缓存")
    void evictCache_deletesKey() {
        configManager.evictCache("test.key");

        verify(stringRedisTemplate).delete("sys:config:test.key");
    }

    @Test
    @DisplayName("evictCache - Redis异常时不抛出异常")
    void evictCache_redisException_doesNotThrow() {
        when(stringRedisTemplate.delete(anyString())).thenThrow(new RuntimeException("Redis down"));

        assertDoesNotThrow(() -> configManager.evictCache("test.key"));
    }

    // ==================== evictAllCache ====================

    @Test
    @DisplayName("evictAllCache - 清除所有配置缓存")
    void evictAllCache_deletesAllKeys() {
        when(stringRedisTemplate.keys("sys:config:*")).thenReturn(Set.of("sys:config:a", "sys:config:b"));

        configManager.evictAllCache();

        verify(stringRedisTemplate).delete(Set.of("sys:config:a", "sys:config:b"));
    }

    @Test
    @DisplayName("evictAllCache - 无缓存key时不执行删除")
    void evictAllCache_noKeys_doesNotDelete() {
        when(stringRedisTemplate.keys("sys:config:*")).thenReturn(Set.of());

        configManager.evictAllCache();

        verify(stringRedisTemplate, never()).delete(any(Set.class));
    }

    @Test
    @DisplayName("evictAllCache - keys返回null时不执行删除")
    void evictAllCache_nullKeys_doesNotDelete() {
        when(stringRedisTemplate.keys("sys:config:*")).thenReturn(null);

        configManager.evictAllCache();

        verify(stringRedisTemplate, never()).delete(any(Set.class));
    }

    @Test
    @DisplayName("evictAllCache - Redis异常时不抛出异常")
    void evictAllCache_redisException_doesNotThrow() {
        when(stringRedisTemplate.keys("sys:config:*")).thenThrow(new RuntimeException("Redis down"));

        assertDoesNotThrow(() -> configManager.evictAllCache());
    }

    // ==================== 辅助方法 ====================

    private SysConfig createConfig(String key, String value) {
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        return config;
    }
}
