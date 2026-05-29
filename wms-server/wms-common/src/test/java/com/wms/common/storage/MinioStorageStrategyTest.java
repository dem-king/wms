package com.wms.common.storage;

import com.wms.common.storage.minio.MinioStorageStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MinIO存储策略测试。
 */
class MinioStorageStrategyTest {

    /**
     * 访问URL应使用配置的公开域名、物理桶和业务对象路径。
     */
    @Test
    void shouldBuildAccessUrlWithPublicDomainAndLogicalBucketPrefix() {
        StorageProperties properties = new StorageProperties();
        properties.setMinioEndpoint("http://internal-minio:9000");
        properties.setMinioAccessKey("access-key");
        properties.setMinioSecretKey("secret-key");
        properties.setMinioBucketName("wms");
        properties.setMinioPublicDomain("https://cdn.example.com");
        MinioStorageStrategy storageStrategy = new MinioStorageStrategy(properties);

        String url = storageStrategy.getUrl(StorageConstants.BUCKET_ITEMS, "100/demo.png");

        assertThat(url).isEqualTo("https://cdn.example.com/wms/items/100/demo.png");
    }
}
