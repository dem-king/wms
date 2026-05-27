package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库房新增/编辑DTO
 */
@Data
@Schema(description = "库房新增/编辑请求")
public class WarehouseDto {

    /** 库房名称 */
    @NotBlank(message = "库房名称不能为空")
    @Schema(description = "库房名称")
    private String warehouseName;

    /** 库房编码 */
    @Schema(description = "库房编码")
    private String warehouseCode;

    /** 地址 */
    @Schema(description = "地址")
    private String address;

    /** 负责人 */
    @Schema(description = "负责人")
    private String manager;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String phone;

    /** 面积(平方米) */
    @Schema(description = "面积(平方米)")
    private BigDecimal area;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 布局画布宽度 */
    @PositiveOrZero(message = "布局画布宽度不能小于0")
    @Schema(description = "布局画布宽度")
    private Integer layoutWidth;

    /** 布局画布高度 */
    @PositiveOrZero(message = "布局画布高度不能小于0")
    @Schema(description = "布局画布高度")
    private Integer layoutHeight;

    /** 布局比例尺 */
    @DecimalMin(value = "0", message = "布局比例尺不能小于0")
    @Schema(description = "布局比例尺")
    private BigDecimal layoutScale;

    /** 底图版本号 */
    @Schema(description = "底图版本号")
    private String layoutBackgroundVersion;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
