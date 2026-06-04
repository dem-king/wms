package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 电子标签视图对象
 * 包含关联的物品名称和编码信息
 */
@Data
@Schema(description = "电子标签信息")
public class ElectronicLabelVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

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

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 物品编码 */
    @Schema(description = "物品编码")
    private String itemCode;

    /** 主类目名称 */
    @Schema(description = "主类目名称")
    private String categoryName;

    /** 细分类目名称 */
    @Schema(description = "细分类目名称")
    private String subCategoryName;

    /** 库位编码 */
    @Schema(description = "库位编码")
    private String binCode;

    /** 存放位置 */
    @Schema(description = "存放位置")
    private String locationText;

    /** 库存数量 */
    @Schema(description = "库存数量")
    private Integer stockQuantity;

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

    /** 打印状态(0-未打印 1-已打印) */
    @Schema(description = "打印状态(0-未打印 1-已打印)")
    private Integer printStatus;

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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
