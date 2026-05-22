package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 归还日聚合实体
 * 对应表 report_return_daily，存储按日+库房+分类聚合的归还统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_return_daily")
public class ReportReturnDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 归还总数量 */
    @Schema(description = "归还总数量")
    private Integer totalQuantity;

    /** 正常归还数量 */
    @Schema(description = "正常归还数量")
    private Integer normalQuantity;

    /** 损坏归还数量 */
    @Schema(description = "损坏归还数量")
    private Integer damagedQuantity;

    /** 归还单据数 */
    @Schema(description = "归还单据数")
    private Integer orderCount;
}
