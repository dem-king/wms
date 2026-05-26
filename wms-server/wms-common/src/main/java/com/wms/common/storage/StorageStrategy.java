package com.wms.common.storage;

import java.io.InputStream;

/**
 * 文件存储策略接口
 * 所有文件上传/下载/删除操作统一通过此接口，由具体实现类决定存储方式
 */
public interface StorageStrategy {

    /**
     * 上传文件
     *
     * @param bucket      逻辑桶名（如 avatars、items）
     * @param objectName  对象路径（如 1/uuid.jpg）
     * @param inputStream 文件流
     * @param contentType MIME类型
     * @param fileSize    文件大小
     * @return 文件访问URL
     */
    String upload(String bucket, String objectName,
                  InputStream inputStream, String contentType, long fileSize);

    /**
     * 下载文件
     *
     * @param bucket     逻辑桶名
     * @param objectName 对象路径
     * @return 文件流（调用方负责关闭）
     */
    InputStream download(String bucket, String objectName);

    /**
     * 删除文件
     *
     * @param bucket     逻辑桶名
     * @param objectName 对象路径
     */
    void delete(String bucket, String objectName);

    /**
     * 获取文件访问URL
     *
     * @param bucket     逻辑桶名
     * @param objectName 对象路径
     * @return 文件访问URL
     */
    String getUrl(String bucket, String objectName);

    /**
     * 生成预签名上传URL（前端直传场景，本地存储返回null）
     *
     * @param bucket       逻辑桶名
     * @param objectName   对象路径
     * @param contentType  MIME类型
     * @param expireSeconds 过期时间（秒）
     * @return 预签名上传结果，本地存储返回null
     */
    PresignedUploadResult getPresignedUploadUrl(String bucket, String objectName,
                                                 String contentType, long expireSeconds);
}
