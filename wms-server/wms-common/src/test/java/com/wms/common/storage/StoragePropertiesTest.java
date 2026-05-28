package com.wms.common.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * StorageProperties 默认值测试
 * 验证敏感配置默认值不再使用公开弱口令。
 */
@DisplayName("StorageProperties 测试")
class StoragePropertiesTest {

    @Test
    @DisplayName("MinIO 默认凭证不应使用公开弱口令")
    void shouldNotUseWeakDefaultMinioCredentials() {
        StorageProperties properties = new StorageProperties();

        assertNotEquals("minioadmin", properties.getMinioAccessKey());
        assertNotEquals("minioadmin", properties.getMinioSecretKey());
    }
}
