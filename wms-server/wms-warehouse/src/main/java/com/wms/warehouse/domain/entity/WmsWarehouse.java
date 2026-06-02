package com.wms.warehouse.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库房实体
 * 对应表 wms_warehouse，存储库房的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_warehouse")
public class WmsWarehouse extends BaseEntity {

    /** 库房名称 */
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

    /** 管理员用户ID */
    @Schema(description = "管理员用户ID")
    private Long managerId;

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
    @Schema(description = "布局画布宽度")
    private Integer layoutWidth;

    /** 布局画布高度 */
    @Schema(description = "布局画布高度")
    private Integer layoutHeight;

    /** 布局比例尺 */
    @Schema(description = "布局比例尺")
    private BigDecimal layoutScale;

    /** 底图版本号 */
    @Schema(description = "底图版本号")
    private String layoutBackgroundVersion;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
