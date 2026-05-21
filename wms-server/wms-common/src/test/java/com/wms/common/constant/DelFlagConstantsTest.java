package com.wms.common.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DelFlagConstants 单元测试
 * 验证逻辑删除常量的正确性
 */
@DisplayName("DelFlagConstants 测试")
class DelFlagConstantsTest {

    @Test
    @DisplayName("NORMAL常量值应为0")
    void shouldHaveCorrectNormalValue() {
        assertEquals(0, DelFlagConstants.NORMAL);
    }

    @Test
    @DisplayName("DELETED常量值应为1")
    void shouldHaveCorrectDeletedValue() {
        assertEquals(1, DelFlagConstants.DELETED);
    }

    @Test
    @DisplayName("NORMAL和DELETED值互斥")
    void shouldBeMutuallyExclusive() {
        assertNotEquals(DelFlagConstants.NORMAL, DelFlagConstants.DELETED);
    }

    @Test
    @DisplayName("类不可实例化 - 私有构造函数")
    void shouldNotBeInstantiable() {
        assertDoesNotThrow(() -> {
            var constructor = DelFlagConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            var instance = constructor.newInstance();
            assertNotNull(instance);
        });
    }
}