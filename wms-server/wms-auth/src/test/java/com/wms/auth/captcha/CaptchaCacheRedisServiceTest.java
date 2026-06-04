package com.wms.auth.captcha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CaptchaCacheRedisService 单元测试
 * 验证 Redis 缓存服务的 set/get/delete/exists/increment/setExpire/type 操作
 */
@DisplayName("CaptchaCacheRedisService 测试")
class CaptchaCacheRedisServiceTest {

    private CaptchaCacheRedisService service;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        service = new CaptchaCacheRedisService();
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        service.setStringRedisTemplate(redisTemplate);
    }

    @Test
    @DisplayName("type() 应返回 redis")
    void type_shouldReturnRedis() {
        assertEquals("redis", service.type());
    }

    @Test
    @DisplayName("set 应调用 redisTemplate.opsForValue().set() 并设置 TTL")
    void set_shouldCallRedisSetWithTTL() {
        service.set("test:key", "test:value", 120);
        verify(valueOps).set(eq("test:key"), eq("test:value"), eq(120L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("set 应支持不同的 TTL 值")
    void set_shouldSupportDifferentTTL() {
        service.set("captcha:token1", "value1", 180);
        verify(valueOps).set(eq("captcha:token1"), eq("value1"), eq(180L), eq(TimeUnit.SECONDS));

        service.set("captcha:token2", "value2", 300);
        verify(valueOps).set(eq("captcha:token2"), eq("value2"), eq(300L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("get 应调用 redisTemplate.opsForValue().get()")
    void get_shouldCallRedisGet() {
        when(valueOps.get("test:key")).thenReturn("test:value");
        assertEquals("test:value", service.get("test:key"));
    }

    @Test
    @DisplayName("get 键不存在时应返回 null")
    void get_keyNotExists_shouldReturnNull() {
        when(valueOps.get("nonexistent:key")).thenReturn(null);
        assertNull(service.get("nonexistent:key"));
    }

    @Test
    @DisplayName("delete 应调用 redisTemplate.delete()")
    void delete_shouldCallRedisDelete() {
        service.delete("test:key");
        verify(redisTemplate).delete("test:key");
    }

    @Test
    @DisplayName("exists 应调用 redisTemplate.hasKey()，键存在时返回 true")
    void exists_keyExists_shouldReturnTrue() {
        when(redisTemplate.hasKey("test:key")).thenReturn(true);
        assertTrue(service.exists("test:key"));
    }

    @Test
    @DisplayName("exists 应调用 redisTemplate.hasKey()，键不存在时返回 false")
    void exists_keyNotExists_shouldReturnFalse() {
        when(redisTemplate.hasKey("test:key")).thenReturn(false);
        assertFalse(service.exists("test:key"));
    }

    @Test
    @DisplayName("exists hasKey 返回 null 时应返回 false")
    void exists_hasKeyReturnsNull_shouldReturnFalse() {
        when(redisTemplate.hasKey("test:key")).thenReturn(null);
        assertFalse(service.exists("test:key"));
    }

    @Test
    @DisplayName("setExpire 应调用 redisTemplate.expire()")
    void setExpire_shouldCallRedisExpire() {
        service.setExpire("test:key", 60);
        verify(redisTemplate).expire(eq("test:key"), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("increment 应调用 Lua 脚本执行")
    void increment_shouldCallLuaScript() {
        when(redisTemplate.execute(any(), any(), any())).thenReturn(1L);
        Long result = service.increment("test:key", 1);
        verify(redisTemplate).execute(any(), any(), any());
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("increment 键不存在时应返回传入的递增值")
    void increment_keyNotExists_shouldReturnIncrementValue() {
        // Lua 脚本中 key 不存在时直接返回传入值
        when(redisTemplate.execute(any(), any(), any())).thenReturn(5L);
        Long result = service.increment("nonexistent:key", 5);
        assertEquals(5L, result);
    }

    @Test
    @DisplayName("increment 键存在时应返回递增后的值")
    void increment_keyExists_shouldReturnIncrementedValue() {
        when(redisTemplate.execute(any(), any(), any())).thenReturn(3L);
        Long result = service.increment("test:key", 1);
        assertEquals(3L, result);
    }
}