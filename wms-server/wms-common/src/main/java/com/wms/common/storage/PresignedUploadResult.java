package com.wms.common.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预签名上传结果
 * 用于前端直传MinIO场景
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预签名上传结果")
public class PresignedUploadResult {

    @Schema(description = "预签名上传URL，前端直接PUT上传到此地址")
    private String uploadUrl;

    @Schema(description = "对象存储路径")
    private String objectName;

    @Schema(description = "上传完成后的文件访问URL")
    private String accessUrl;
}
