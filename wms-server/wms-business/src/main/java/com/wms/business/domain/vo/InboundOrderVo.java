package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库单视图对象
 * 包含库房名称、供应商名称、明细列表等关联信息
 */
@Data
@Schema(description = "入库单信息")
public class InboundOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 入库单号 */
    @Schema(description = "入库单号")
    private String orderNo;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 库房名称 */
    @Schema(description = "库房名称")
    private String warehouseName;

    /** 供应商ID */
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称 */
    @Schema(description = "供应商名称")
    private String supplierName;

    /** 入库类型(1-采购入库 2-归还入库 3-调拨入库) */
    @Schema(description = "入库类型(1-采购入库 2-归还入库 3-调拨入库)")
    private Integer orderType;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    private Integer status;

    /** 总金额 */
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 入库明细列表 */
    @Schema(description = "入库明细列表")
    private List<InboundDetailVo> details;

    /**
     * 入库明细视图对象
     */
    @Data
    @Schema(description = "入库明细信息")
    public static class InboundDetailVo {

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

        /** 单价 */
        @Schema(description = "单价")
        private BigDecimal unitPrice;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;

        /** 入库库位ID */
        @Schema(description = "入库库位ID")
        private Long binId;

        /** 库位编码 */
        @Schema(description = "库位编码")
        private String binCode;
    }
}
