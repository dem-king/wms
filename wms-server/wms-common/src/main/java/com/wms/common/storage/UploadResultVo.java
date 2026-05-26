package com.wms.common.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传结果")
public class UploadResultVo {

    @Schema(description = "文件访问URL")
    private String url;

    @Schema(description = "对象存储路径")
    private String objectName;
}
