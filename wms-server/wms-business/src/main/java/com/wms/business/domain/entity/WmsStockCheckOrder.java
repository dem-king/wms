package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点单主表实体
 * 对应表 wms_stock_check_order，存储盘点单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stock_check_order")
public class WmsStockCheckOrder extends BaseEntity {

    /** 盘点单号 */
    @Schema(description = "盘点单号")
    private String checkNo;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 区域ID */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 盘点类型(1-全盘 2-抽盘) */
    @Schema(description = "盘点类型(1-全盘 2-抽盘)")
    private Integer checkType;

    /** 盘点状态(0-草稿 1-已提交 2-已确认) */
    @Schema(description = "盘点状态(0-草稿 1-已提交 2-已确认)")
    private Integer status;

    /** 系统在库标签数 */
    @Schema(description = "系统在库标签数")
    private Integer systemCount;

    /** 实际读取标签数 */
    @Schema(description = "实际读取标签数")
    private Integer actualCount;

    /** 匹配标签数 */
    @Schema(description = "匹配标签数")
    private Integer matchCount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}