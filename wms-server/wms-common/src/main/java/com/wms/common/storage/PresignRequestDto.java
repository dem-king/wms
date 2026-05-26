package com.wms.common.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 预签名上传请求DTO
 */
@Data
@Schema(description = "预签名上传请求")
public class PresignRequestDto {

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "MIME类型")
    private String contentType;
}
