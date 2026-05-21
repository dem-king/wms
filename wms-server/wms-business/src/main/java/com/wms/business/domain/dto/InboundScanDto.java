package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 入库扫码请求DTO
 * 用于在新增或编辑入库单时解析标签并校验当前单据内是否重复扫描
 */
@Data
@Schema(description = "入库扫码请求")
public class InboundScanDto {

    /** 扫码内容 */
    @NotBlank(message = "扫码内容不能为空")
    @Schema(description = "扫码内容")
    private String code;

    /** 当前单据中已扫描的标签ID列表 */
    @Schema(description = "当前单据中已扫描的标签ID列表")
    private List<Long> currentLabelIds;
}
