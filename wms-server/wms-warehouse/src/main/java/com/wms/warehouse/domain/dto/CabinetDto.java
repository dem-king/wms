package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 存放柜新增/编辑DTO
 */
@Data
@Schema(description = "存放柜新增/编辑请求")
public class CabinetDto {

    /** 所属区域ID */
    @NotNull(message = "所属区域ID不能为空")
    @Schema(description = "所属区域ID")
    private Long areaId;

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 存放柜名称 */
    @NotBlank(message = "存放柜名称不能为空")
    @Schema(description = "存放柜名称")
    private String cabinetName;

    /** 存放柜编码 */
    @Schema(description = "存放柜编码")
    private String cabinetCode;

    /** X坐标(可视化位置) */
    @Schema(description = "X坐标(可视化位置)")
    private Integer positionX;

    /** Y坐标(可视化位置) */
    @Schema(description = "Y坐标(可视化位置)")
    private Integer positionY;

    /** 存放柜类型(1-货架 2-柜子 3-托盘架 4-冷藏柜) */
    @Schema(description = "存放柜类型(1-货架 2-柜子 3-托盘架 4-冷藏柜)")
    private Integer cabinetType;

    /** 行数 */
    @Schema(description = "行数")
    private Integer rows;

    /** 列数 */
    @Schema(description = "列数")
    private Integer cols;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
