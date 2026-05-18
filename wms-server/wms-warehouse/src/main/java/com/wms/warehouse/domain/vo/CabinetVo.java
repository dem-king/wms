package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 存放柜视图对象
 * 包含存放柜基础信息、所属区域名称和物品数量
 */
@Data
@Schema(description = "存放柜信息")
public class CabinetVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 所属区域ID */
    @Schema(description = "所属区域ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String areaName;

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
    private Integer rows;

    /** 列数 */
    @Schema(description = "列数")
    private Integer cols;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 物品数量(占用库位数) */
    @Schema(description = "物品数量(占用库位数)")
    private Integer itemCount;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
