package com.wms.warehouse.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 存放柜实体
 * 对应表 wms_cabinet，存储区域下的存放柜信息，含位置坐标和行列配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_cabinet")
public class WmsCabinet extends BaseEntity {

    /** 所属区域ID */
    @Schema(description = "所属区域ID")
    private Long areaId;

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 存放柜名称 */
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
    @TableField("`rows`")
    private Integer rows;

    /** 列数 */
    @Schema(description = "列数")
    @TableField("`cols`")
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
