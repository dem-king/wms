package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 布局元素批量保存结果视图对象
 * 返回批量保存的统计信息和失败项列表
 */
@Data
@Schema(description = "布局元素批量保存结果")
public class LayoutElementBatchSaveVo {

    /** 新增成功数量 */
    @Schema(description = "新增成功数量")
    private Integer createdCount;

    /** 更新成功数量 */
    @Schema(description = "更新成功数量")
    private Integer updatedCount;

    /** 删除成功数量 */
    @Schema(description = "删除成功数量")
    private Integer deletedCount;

    /** 失败项列表 */
    @Schema(description = "失败项列表")
    private List<LayoutElementFailedItemVo> failedItems;
}