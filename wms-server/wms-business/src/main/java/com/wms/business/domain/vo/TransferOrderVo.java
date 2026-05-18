package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调拨单视图对象
 * 包含调出/调入库房名称、明细列表等关联信息
 */
@Data
@Schema(description = "调拨单信息")
public class TransferOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 调拨单号 */
    @Schema(description = "调拨单号")
    private String orderNo;

    /** 调出库房ID */
    @Schema(description = "调出库房ID")
    private Long fromWarehouseId;

    /** 调出库房名称 */
    @Schema(description = "调出库房名称")
    private String fromWarehouseName;

    /** 调入库房ID */
    @Schema(description = "调入库房ID")
    private Long toWarehouseId;

    /** 调入库房名称 */
    @Schema(description = "调入库房名称")
    private String toWarehouseName;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 调拨明细列表 */
    @Schema(description = "调拨明细列表")
    private List<TransferDetailVo> details;

    /**
     * 调拨明细视图对象
     */
    @Data
    @Schema(description = "调拨明细信息")
    public static class TransferDetailVo {

        /** 主键 */
        @Schema(description = "主键")
        private Long id;

        /** 物品ID */
        @Schema(description = "物品ID")
        private Long itemId;

        /** 物品名称 */
        @Schema(description = "物品名称")
        private String itemName;

        /** 物品编号 */
        @Schema(description = "物品编号")
        private String itemCode;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;
    }
}
