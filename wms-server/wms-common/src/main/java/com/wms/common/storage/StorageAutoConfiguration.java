package com.wms.common.storage;

import com.wms.common.storage.local.LocalStorageStrategy;
import com.wms.common.storage.minio.MinioConfig;
import com.wms.common.storage.minio.MinioStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 文件存储自动装配配置
 * 根据 wms.storage.type 配置选择本地存储或MinIO存储实现
 */
@Slf4j
@Configuration
public class StorageAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "wms.storage.type", havingValue = "local", matchIfMissing = true)
    public StorageStrategy localStorageStrategy(StorageProperties props) {
        log.info("文件存储策略: 本地文件系统, baseDir={}", props.getLocalBaseDir());
        return new LocalStorageStrategy(props);
    }

    @Bean
    @ConditionalOnProperty(name = "wms.storage.type", havingValue = "minio")
    @Import(MinioConfig.class)
    public StorageStrategy minioStorageStrategy(StorageProperties props,
                                                io.minio.MinioClient minioClient) {
        log.info("文件存储策略: MinIO对象存储, endpoint={}, bucket={}",
                props.getMinioEndpoint(), props.getMinioBucketName());
        return new MinioStorageStrategy(props, minioClient);
    }
}
