package com.wms.monitor.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 逾期归还视图对象
 */
@Data
@Schema(description = "逾期归还信息")
public class OverdueReturnVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 借出单ID */
    @Schema(description = "借出单ID")
    private Long orderId;

    /** 借出单号 */
    @Schema(description = "借出单号")
    private String orderNo;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 物品编码 */
    @Schema(description = "物品编码")
    private String itemCode;

    /** 借出数量 */
    @Schema(description = "借出数量")
    private Integer borrowQuantity;

    /** 借用人姓名 */
    @Schema(description = "借用人姓名")
    private String borrowerName;

    /** 借出时间 */
    @Schema(description = "借出时间")
    private LocalDateTime borrowTime;

    /** 预计归还日期 */
    @Schema(description = "预计归还日期")
    private LocalDate expectedReturnDate;

    /** 逾期天数 */
    @Schema(description = "逾期天数")
    private Integer overdueDays;

    /** 提醒级别 */
    @Schema(description = "提醒级别")
    private String alertLevel;

    /** 处理状态 */
    @Schema(description = "处理状态")
    private String status;
}
