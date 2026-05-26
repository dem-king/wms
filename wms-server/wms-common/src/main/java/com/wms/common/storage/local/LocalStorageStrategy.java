package com.wms.common.storage.local;

import com.wms.common.exception.BizException;
import com.wms.common.storage.PresignedUploadResult;
import com.wms.common.storage.StorageProperties;
import com.wms.common.storage.StorageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件系统存储策略
 * 文件保存到本地磁盘，通过Controller接口暴露访问
 */
@Slf4j
@RequiredArgsConstructor
public class LocalStorageStrategy implements StorageStrategy {

    private final StorageProperties props;

    @Override
    public String upload(String bucket, String objectName,
                         InputStream inputStream, String contentType, long fileSize) {
        Path dirPath = Paths.get(props.getLocalBaseDir(), bucket);
        Path targetPath = dirPath.resolve(objectName);
        try {
            // 创建目录（支持多级，如 avatars/1/）
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BizException("文件上传失败: " + e.getMessage());
        }
        return getUrl(bucket, objectName);
    }

    @Override
    public InputStream download(String bucket, String objectName) {
        Path filePath = Paths.get(props.getLocalBaseDir(), bucket, objectName).normalize();
        // 防路径穿越：确保文件在存储根目录下
        Path basePath = Paths.get(props.getLocalBaseDir()).toAbsolutePath().normalize();
        if (!filePath.toAbsolutePath().normalize().startsWith(basePath)) {
            throw new BizException("非法文件路径");
        }
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new BizException("文件不存在或不可读");
        }
    }

    @Override
    public void delete(String bucket, String objectName) {
        Path filePath = Paths.get(props.getLocalBaseDir(), bucket, objectName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("文件删除失败: bucket={}, objectName={}, error={}", bucket, objectName, e.getMessage());
        }
    }

    @Override
    public String getUrl(String bucket, String objectName) {
        return props.getLocalUrlPrefix() + "/" + bucket + "/" + objectName;
    }

    @Override
    public PresignedUploadResult getPresignedUploadUrl(String bucket, String objectName,
                                                        String contentType, long expireSeconds) {
        // 本地存储不支持预签名URL，需通过服务端中转上传
        return null;
    }
}
