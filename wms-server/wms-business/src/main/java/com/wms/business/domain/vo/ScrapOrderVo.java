package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报废单视图对象
 * 包含明细列表等关联信息
 */
@Data
@Schema(description = "报废单信息")
public class ScrapOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 报废单号 */
    @Schema(description = "报废单号")
    private String orderNo;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    private Integer status;

    /** 报废原因 */
    @Schema(description = "报废原因")
    private String scrapReason;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 报废明细列表 */
    @Schema(description = "报废明细列表")
    private List<ScrapDetailVo> details;

    /**
     * 报废明细视图对象
     */
    @Data
    @Schema(description = "报废明细信息")
    public static class ScrapDetailVo {

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
