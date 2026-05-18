package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库房视图对象
 * 包含库房的基础信息
 */
@Data
@Schema(description = "库房信息")
public class WarehouseVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

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

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String phone;

    /** 面积(平方米) */
    @Schema(description = "面积(平方米)")
    private BigDecimal area;

    /** 状态(1-启用 0-禁用) */
    @Schema(description = "状态(1-启用 0-禁用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
