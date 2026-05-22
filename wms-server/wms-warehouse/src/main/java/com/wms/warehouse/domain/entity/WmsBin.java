package com.wms.warehouse.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位实体
 * 对应表 wms_bin，存放柜下的具体库位，记录行列位置和占用状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_bin")
public class WmsBin extends BaseEntity {

    /** 所属存放柜ID */
    @Schema(description = "所属存放柜ID")
    private Long cabinetId;

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 库位编码 */
    @Schema(description = "库位编码")
    private String binCode;

    /** 行号 */
    @Schema(description = "行号")
    private Integer rowNum;

    /** 列号 */
    @Schema(description = "列号")
    private Integer colNum;

    /** 容量(0为不限) */
    @Schema(description = "容量(0为不限)")
    private Integer capacity;

    /** 已用容量 */
    @Schema(description = "已用容量")
    private Integer usedCapacity;

    /** 是否占用(0-空闲 1-占用) */
    @Schema(description = "是否占用(0-空闲 1-占用)")
    private Integer isOccupied;

    /** 状态(0-禁用 1-正常 2-满) */
    @Schema(description = "状态(0-禁用 1-正常 2-满)")
    private Integer binStatus;
}
