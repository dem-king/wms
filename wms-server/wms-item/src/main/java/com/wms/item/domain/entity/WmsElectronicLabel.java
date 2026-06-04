package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 电子标签实体
 * 对应表 wms_electronic_label，存储电子标签的基础信息、绑定关系和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_electronic_label")
public class WmsElectronicLabel extends BaseEntity {

    /** 标签编号 */
    @Schema(description = "标签编号")
    private String labelNo;

    /** 标签类型(1-二维码 2-条形码 3-RFID) */
    @Schema(description = "标签类型(1-二维码 2-条形码 3-RFID)")
    private Integer labelType;

    /** 绑定物品ID */
    @Schema(description = "绑定物品ID")
    private Long itemId;

    /** 绑定库位ID */
    @Schema(description = "绑定库位ID")
    private Long binId;

    /** 批次号 */
    @Schema(description = "批次号")
    private String batchNo;

    /** RFID编码 */
    @Schema(description = "RFID编码")
    private String rfidCode;

    /** 二维码内容 */
    @Schema(description = "二维码内容")
    private String qrContent;

    /** 条形码内容 */
    @Schema(description = "条形码内容")
    private String barcodeContent;

    /** 标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置) */
    @Schema(description = "标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置)")
    private Integer labelStatus;

    /** 绑定类型(1-单品对应 2-批次对应) */
    @Schema(description = "绑定类型(1-单品对应 2-批次对应)")
    private Integer bindType;

    /** 当前领用人ID */
    @Schema(description = "当前领用人ID")
    private Long currentUserId;

    /** 当前领用部门ID */
    @Schema(description = "当前领用部门ID")
    private Long currentDeptId;

    /** 借出时间 */
    @Schema(description = "借出时间")
    private LocalDateTime borrowTime;

    /** 领用人姓名 */
    @Schema(description = "领用人姓名")
    private String borrowerName;

    /** 预计归还时间 */
    @Schema(description = "预计归还时间")
    private LocalDateTime expectedReturn;

    /** 归还人姓名 */
    @Schema(description = "归还人姓名")
    private String returnerName;

    /** 归还时间 */
    @Schema(description = "归还时间")
    private LocalDateTime returnTime;

    /** 打印状态(0-未打印 1-已打印) */
    @Schema(description = "打印状态(0-未打印 1-已打印)")
    private Integer printStatus;
}
