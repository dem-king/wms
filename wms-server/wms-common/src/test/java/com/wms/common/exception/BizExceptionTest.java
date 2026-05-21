package com.wms.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BizException 单元测试
 * 验证自定义业务异常的构造和行为
 */
@DisplayName("BizException 测试")
class BizExceptionTest {

    @Test
    @DisplayName("默认构造 - code应为400，message应正确")
    void shouldCreateWithDefaultCode() {
        BizException ex = new BizException("库存不足");

        assertEquals(400, ex.getCode());
        assertEquals("库存不足", ex.getMessage());
    }

    @Test
    @DisplayName("自定义code构造 - code和message均应正确")
    void shouldCreateWithCustomCode() {
        BizException ex = new BizException(403, "无操作权限");

        assertEquals(403, ex.getCode());
        assertEquals("无操作权限", ex.getMessage());
    }

    @Test
    @DisplayName("message包含参数 - 应支持格式化消息")
    void shouldSupportParameterizedMessage() {
        BizException ex = new BizException("物品不存在: itemId=123");

        assertEquals(400, ex.getCode());
        assertTrue(ex.getMessage().contains("123"));
    }

    @Test
    @DisplayName("BizException是RuntimeException子类")
    void shouldBeRuntimeExceptionSubclass() {
        BizException ex = new BizException("test");
        assertInstanceOf(RuntimeException.class, ex);
    }
}