package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报废单主表实体
 * 对应表 wms_scrap_order，存储报废单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_scrap_order")
public class WmsScrapOrder extends BaseEntity {

    /** 报废单号 */
    @Schema(description = "报废单号")
    private String orderNo;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    @TableField("order_status")
    private Integer status;

    /** 报废原因 */
    @Schema(description = "报废原因")
    private String scrapReason;

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;
}
