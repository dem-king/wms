package com.wms.common.storage;

import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.common.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 通用文件存储接口
 * 提供统一的上传、下载、预签名URL和文件访问代理功能
 */
@Slf4j
@Tag(name = "文件存储")
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageStrategy storageStrategy;
    private final StorageProperties storageProperties;

    /**
     * 上传文件到指定业务桶
     *
     * @param bucket 逻辑桶名（avatars/items等）
     * @param file   上传的文件
     * @return 上传结果，包含文件访问URL和对象路径
     */
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "storage", type = "上传", desc = "上传文件")
    @Operation(summary = "上传文件")
    @PostMapping("/upload/{bucket}")
    public R<UploadResultVo> upload(@PathVariable String bucket,
                                    @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        String objectName = generateObjectName(file.getOriginalFilename());
        try {
            String url = storageStrategy.upload(bucket, objectName,
                    file.getInputStream(), file.getContentType(), file.getSize());
            return R.ok(new UploadResultVo(url, objectName));
        } catch (Exception e) {
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取预签名上传URL（前端直传MinIO场景）
     * 本地存储模式不支持预签名，返回null
     *
     * @param bucket 逻辑桶名
     * @param dto    预签名请求参数
     * @return 预签名上传结果
     */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取预签名上传URL")
    @PostMapping("/presign/{bucket}")
    public R<PresignedUploadResult> presign(@PathVariable String bucket,
                                             @Valid @RequestBody PresignRequestDto dto) {
        String objectName = generateObjectName(dto.getFileName());
        PresignedUploadResult result = storageStrategy.getPresignedUploadUrl(
                bucket, objectName, dto.getContentType(), StorageConstants.PRESIGN_EXPIRE_SECONDS);
        if (result == null) {
            return R.fail("当前存储模式不支持预签名上传");
        }
        return R.ok(result);
    }

    /**
     * 文件访问代理（本地存储模式）
     * MinIO模式下文件通过MinIO域名直接访问，无需此接口
     * 本地存储模式下通过此接口读取文件并返回
     *
     * @param bucket  逻辑桶名
     * @param request HTTP请求（用于提取完整对象路径）
     * @return 文件内容
     */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "文件访问代理")
    @GetMapping("/{bucket}/**")
    public ResponseEntity<Resource> serveFile(@PathVariable String bucket,
                                              HttpServletRequest request) {
        String objectName = extractObjectName(request, bucket);
        try {
            InputStream is = storageStrategy.download(bucket, objectName);
            Resource resource = new InputStreamResource(is);
            // 从objectName提取文件名用于Content-Disposition
            String fileName = objectName.contains("/")
                    ? objectName.substring(objectName.lastIndexOf('/') + 1)
                    : objectName;
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (BizException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     *
     * @param bucket     逻辑桶名
     * @param objectName 对象路径
     */
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "storage", type = "删除", desc = "删除文件")
    @Operation(summary = "删除文件")
    @DeleteMapping("/{bucket}/{objectName:.+}")
    public R<Void> delete(@PathVariable String bucket, @PathVariable String objectName) {
        storageStrategy.delete(bucket, objectName);
        return R.ok();
    }

    /**
     * 生成对象存储路径名（UUID + 原始扩展名）
     */
    private String generateObjectName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 从请求URI中提取对象路径
     * URI格式: /api/storage/{bucket}/path/to/file.jpg
     */
    private String extractObjectName(HttpServletRequest request, String bucket) {
        String requestURI = request.getRequestURI();
        // 去掉context-path前缀 /api
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            requestURI = requestURI.substring(contextPath.length());
        }
        // 去掉 /storage/{bucket}/ 前缀
        String prefix = "/storage/" + bucket + "/";
        int idx = requestURI.indexOf(prefix);
        if (idx >= 0) {
            return requestURI.substring(idx + prefix.length());
        }
        return requestURI;
    }
}
