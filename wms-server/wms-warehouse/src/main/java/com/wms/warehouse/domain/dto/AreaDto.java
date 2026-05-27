package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * 存放区域新增/编辑DTO
 */
@Data
@Schema(description = "存放区域新增/编辑请求")
public class AreaDto {

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 区域名称 */
    @NotBlank(message = "区域名称不能为空")
    @Schema(description = "区域名称")
    private String areaName;

    /** 区域编码 */
    @Schema(description = "区域编码")
    private String areaCode;

    /** 区域类型(1-存储区 2-暂存区 3-操作区 4-退货区) */
    @Schema(description = "区域类型(1-存储区 2-暂存区 3-操作区 4-退货区)")
    private Integer areaType;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 区域形状 */
    @Schema(description = "区域形状")
    private String shapeType;

    /** 多边形点位JSON */
    @Schema(description = "多边形点位JSON")
    private String polygonPoints;

    /** 标题X坐标 */
    @PositiveOrZero(message = "标题X坐标不能小于0")
    @Schema(description = "标题X坐标")
    private Integer labelX;

    /** 标题Y坐标 */
    @PositiveOrZero(message = "标题Y坐标不能小于0")
    @Schema(description = "标题Y坐标")
    private Integer labelY;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
