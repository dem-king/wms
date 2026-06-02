package com.wms.warehouse.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 存放区域实体
 * 对应表 wms_area，存储库房下的区域划分信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_area")
public class WmsArea extends BaseEntity {

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 区域名称 */
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
    @Schema(description = "标题X坐标")
    private Integer labelX;

    /** 标题Y坐标 */
    @Schema(description = "标题Y坐标")
    private Integer labelY;

    /** X坐标(画布自由定位) */
    @Schema(description = "X坐标(画布自由定位)")
    private Integer coordX;

    /** Y坐标(画布自由定位) */
    @Schema(description = "Y坐标(画布自由定位)")
    private Integer coordY;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
