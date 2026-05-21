package com.wms.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 入库扫码结果VO
 * 返回标签识别结果以及可直接回填到入库单的建议明细
 */
@Data
@Schema(description = "入库扫码结果")
public class InboundScanResultVo {

    /** 标签ID */
    @Schema(description = "标签ID")
    private Long labelId;

    /** 标签编号 */
    @Schema(description = "标签编号")
    private String labelNo;

    /** 标签状态 */
    @Schema(description = "标签状态")
    private Integer labelStatus;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 物品编码 */
    @Schema(description = "物品编码")
    private String itemCode;

    /** 建议回填的入库明细 */
    @Schema(description = "建议回填的入库明细")
    private InboundDetailScanVo detail;

    /**
     * 入库扫码建议明细
     */
    @Data
    @Schema(description = "入库扫码建议明细")
    public static class InboundDetailScanVo {

        /** 物品ID */
        @Schema(description = "物品ID")
        private Long itemId;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;
    }
}
