package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 布局元素更新项DTO
 * 继承LayoutElementDto的字段并新增id字段，用于批量保存中的更新场景
 */
@Data
@Schema(description = "布局元素更新项")
public class LayoutElementUpdateItemDto {

    /** 元素ID */
    @NotNull(message = "元素ID不能为空")
    @Schema(description = "元素ID")
    private Long id;

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 关联区域ID */
    @Schema(description = "关联区域ID")
    private Long areaId;

    /** 元素名称 */
    @NotBlank(message = "元素名称不能为空")
    @Size(max = 100, message = "元素名称长度不能超过100")
    @Schema(description = "元素名称")
    private String elementName;

    /** 元素类型 */
    @NotBlank(message = "元素类型不能为空")
    @Schema(description = "元素类型")
    private String elementType;

    /** 形状类型 */
    @NotBlank(message = "形状类型不能为空")
    @Schema(description = "形状类型")
    private String shapeType;

    /** X坐标 */
    @Min(0)
    @Max(10000)
    @Schema(description = "X坐标")
    private Integer positionX;

    /** Y坐标 */
    @Min(0)
    @Max(10000)
    @Schema(description = "Y坐标")
    private Integer positionY;

    /** 宽度 */
    @Min(1)
    @Max(5000)
    @Schema(description = "宽度")
    private Integer layoutWidth;

    /** 高度 */
    @Min(1)
    @Max(5000)
    @Schema(description = "高度")
    private Integer layoutHeight;

    /** 旋转角度 */
    @Min(0)
    @Max(360)
    @Schema(description = "旋转角度")
    private Integer rotation;

    /** 点位数据JSON */
    @Size(max = 4000)
    @Schema(description = "点位数据JSON")
    private String pointData;

    /** 样式数据JSON */
    @Size(max = 2000)
    @Schema(description = "样式数据JSON")
    private String styleData;

    /** 展示文本 */
    @Size(max = 200)
    @Schema(description = "展示文本")
    private String labelText;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}