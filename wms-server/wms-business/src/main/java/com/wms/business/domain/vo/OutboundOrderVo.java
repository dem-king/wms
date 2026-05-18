package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单视图对象
 * 包含库房名称、明细列表等关联信息
 */
@Data
@Schema(description = "出库单信息")
public class OutboundOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 出库单号 */
    @Schema(description = "出库单号")
    private String orderNo;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 库房名称 */
    @Schema(description = "库房名称")
    private String warehouseName;

    /** 出库类型(1-领用出库 2-调拨出库 3-报废出库) */
    @Schema(description = "出库类型(1-领用出库 2-调拨出库 3-报废出库)")
    private Integer orderType;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    private Integer status;

    /** 领用人 */
    @Schema(description = "领用人")
    private String receiver;

    /** 用途 */
    @Schema(description = "用途")
    private String purpose;

    /** 预计归还日期 */
    @Schema(description = "预计归还日期")
    private LocalDateTime expectedReturnDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 出库明细列表 */
    @Schema(description = "出库明细列表")
    private List<OutboundDetailVo> details;

    /**
     * 出库明细视图对象
     */
    @Data
    @Schema(description = "出库明细信息")
    public static class OutboundDetailVo {

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

        /** 库位ID */
        @Schema(description = "库位ID")
        private Long binId;

        /** 库位编码 */
        @Schema(description = "库位编码")
        private String binCode;
    }
}
