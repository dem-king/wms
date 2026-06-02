package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 布局元素保存失败项视图对象
 * 记录批量保存中失败的元素名称和原因
 */
@Data
@Schema(description = "布局元素保存失败项")
public class LayoutElementFailedItemVo {

    /** 元素名称 */
    @Schema(description = "元素名称")
    private String elementName;

    /** 失败原因 */
    @Schema(description = "失败原因")
    private String reason;
}