package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库房布局元素视图对象
 * 包含布局元素的全部展示信息
 */
@Data
@Schema(description = "库房布局元素信息")
public class LayoutElementVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 关联区域ID */
    @Schema(description = "关联区域ID")
    private Long areaId;

    /** 元素编码 */
    @Schema(description = "元素编码")
    private String elementCode;

    /** 元素名称 */
    @Schema(description = "元素名称")
    private String elementName;

    /** 元素类型 */
    @Schema(description = "元素类型")
    private String elementType;

    /** 形状类型 */
    @Schema(description = "形状类型")
    private String shapeType;

    /** X坐标 */
    @Schema(description = "X坐标")
    private Integer positionX;

    /** Y坐标 */
    @Schema(description = "Y坐标")
    private Integer positionY;

    /** 宽度 */
    @Schema(description = "宽度")
    private Integer layoutWidth;

    /** 高度 */
    @Schema(description = "高度")
    private Integer layoutHeight;

    /** 旋转角度 */
    @Schema(description = "旋转角度")
    private Integer rotation;

    /** 点位数据JSON */
    @Schema(description = "点位数据JSON")
    private String pointData;

    /** 样式数据JSON */
    @Schema(description = "样式数据JSON")
    private String styleData;

    /** 展示文本 */
    @Schema(description = "展示文本")
    private String labelText;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

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