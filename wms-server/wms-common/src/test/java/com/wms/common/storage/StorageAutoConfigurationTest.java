package com.wms.common.storage;

import com.wms.common.storage.local.LocalStorageStrategy;
import com.wms.common.storage.minio.MinioStorageStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StorageAutoConfiguration 测试
 * 验证默认本地存储场景下可创建本地存储策略。
 */
class StorageAutoConfigurationTest {

    /**
     * 默认配置下应返回本地存储策略。
     */
    @Test
    void shouldCreateLocalStorageStrategy() {
        StorageProperties properties = new StorageProperties();
        StorageAutoConfiguration configuration = new StorageAutoConfiguration();

        StorageStrategy storageStrategy = configuration.localStorageStrategy(properties);

        assertThat(storageStrategy).isInstanceOf(LocalStorageStrategy.class);
    }

    /**
     * MinIO配置下应返回MinIO存储策略。
     */
    @Test
    void shouldCreateMinioStorageStrategy() {
        StorageProperties properties = new StorageProperties();
        properties.setType("minio");
        properties.setMinioEndpoint("http://localhost:9000");
        properties.setMinioAccessKey("access-key");
        properties.setMinioSecretKey("secret-key");
        properties.setMinioBucketName("wms");
        StorageAutoConfiguration configuration = new StorageAutoConfiguration();

        StorageStrategy storageStrategy = configuration.minioStorageStrategy(properties);

        assertThat(storageStrategy).isInstanceOf(MinioStorageStrategy.class);
    }
}
