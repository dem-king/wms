package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 出库扫码请求DTO
 * 用于在新增或编辑出库单时解析标签并校验重复扫描与可出库状态
 */
@Data
@Schema(description = "出库扫码请求")
public class OutboundScanDto {

    /** 扫码内容 */
    @NotBlank(message = "扫码内容不能为空")
    @Schema(description = "扫码内容")
    private String code;

    /** 当前单据中已扫描的标签ID列表 */
    @Schema(description = "当前单据中已扫描的标签ID列表")
    private List<Long> currentLabelIds;
}
