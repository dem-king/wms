package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库位视图对象
 * 包含库位基础信息、所属存放柜名称和占用状态
 */
@Data
@Schema(description = "库位信息")
public class BinVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 所属存放柜ID */
    @Schema(description = "所属存放柜ID")
    private Long cabinetId;

    /** 存放柜名称 */
    @Schema(description = "存放柜名称")
    private String cabinetName;

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

    /** 是否占用(0-空闲 1-占用) */
    @Schema(description = "是否占用(0-空闲 1-占用)")
    private Integer isOccupied;

    /** 占用状态描述 */
    @Schema(description = "占用状态描述")
    private String occupiedDesc;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
