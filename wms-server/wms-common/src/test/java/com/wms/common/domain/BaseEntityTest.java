package com.wms.common.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * BaseEntity 回归测试。
 * 校验逻辑删除仅依赖 delFlag，不再保留最后操作类型等冗余字段。
 */
class BaseEntityTest {

    @Test
    void shouldNotContainLastOperTypeField() {
        Set<String> fieldNames = Arrays.stream(BaseEntity.class.getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toSet());

        assertFalse(fieldNames.contains("lastOperType"));
    }
}
