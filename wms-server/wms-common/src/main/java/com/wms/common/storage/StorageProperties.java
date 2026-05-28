package com.wms.common.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置属性
 * 通过 wms.storage.type 切换本地存储与MinIO存储
 */
@Data
@Component
@ConfigurationProperties(prefix = "wms.storage")
public class StorageProperties {

    /** 存储类型: local=本地文件系统, minio=MinIO对象存储 */
    private String type = "local";

    /** 本地存储根目录 */
    private String localBaseDir = "storage";

    /** 本地存储URL前缀（通过Controller暴露） */
    private String localUrlPrefix = "/api/storage";

    /** MinIO端点 */
    private String minioEndpoint = "http://localhost:9000";

    /** MinIO Access Key */
    private String minioAccessKey = "dev_minio_access_key";

    /** MinIO Secret Key */
    private String minioSecretKey = "dev_minio_secret_key";

    /** MinIO默认桶名 */
    private String minioBucketName = "wms";

    /** MinIO文件访问域名（生产环境可为CDN域名，不填则使用endpoint） */
    private String minioPublicDomain;
}
