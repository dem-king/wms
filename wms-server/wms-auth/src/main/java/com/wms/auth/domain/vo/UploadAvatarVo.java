package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 头像上传结果
 */
@Data
@Schema(description = "头像上传结果")
public class UploadAvatarVo {

    /** 头像访问地址 */
    @Schema(description = "头像访问地址")
    private String avatarUrl;
}
