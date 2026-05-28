package com.wms.system.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SysUserServiceImpl 配置约束测试
 * 验证默认密码只从配置键读取，不在 Java 注解中硬编码默认值。
 */
@DisplayName("SysUserServiceImpl 测试")
class SysUserServiceImplTest {

    @Test
    @DisplayName("默认密码配置应只引用属性键")
    void shouldReadDefaultPasswordWithoutInlineFallback() throws NoSuchFieldException {
        Field field = SysUserServiceImpl.class.getDeclaredField("defaultPassword");
        Value value = field.getAnnotation(Value.class);

        assertEquals("${wms.default-password}", value.value());
    }
}
