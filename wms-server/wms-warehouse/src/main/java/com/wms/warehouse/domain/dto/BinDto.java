package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库位新增/编辑DTO
 */
@Data
@Schema(description = "库位新增/编辑请求")
public class BinDto {

    /** 所属存放柜ID */
    @NotNull(message = "所属存放柜ID不能为空")
    @Schema(description = "所属存放柜ID")
    private Long cabinetId;

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
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

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
