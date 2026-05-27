package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 归还单视图对象
 * 包含关联出库单信息、明细列表等
 */
@Data
@Schema(description = "归还单信息")
public class ReturnOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 归还单号 */
    @Schema(description = "归还单号")
    private String orderNo;

    /** 关联出库单ID */
    @Schema(description = "关联出库单ID")
    private Long outboundOrderId;

    /** 关联出库单号 */
    @Schema(description = "关联出库单号")
    private String outboundOrderNo;

    /** 状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成) */
    @Schema(description = "状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)")
    private Integer status;

    /** 归还人 */
    @Schema(description = "归还人")
    private String receiver;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 归还明细列表 */
    @Schema(description = "归还明细列表")
    private List<ReturnDetailVo> details;

    /**
     * 归还明细视图对象
     */
    @Data
    @Schema(description = "归还明细信息")
    public static class ReturnDetailVo {

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

        /** 物品状态(1-正常 2-损坏 3-丢失 4-数量不符) */
        @Schema(description = "物品状态(1-正常 2-损坏 3-丢失 4-数量不符)")
        private Integer conditionStatus;

        /** 异常说明 */
        @Schema(description = "异常说明")
        private String abnormalRemark;

        /** 实际归还数量(数量不符时记录) */
        @Schema(description = "实际归还数量")
        private Integer actualQuantity;
    }
}
