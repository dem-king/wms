package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 存放区域视图对象
 * 包含区域基础信息和所属库房名称
 */
@Data
@Schema(description = "存放区域信息")
public class AreaVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 所属库房ID */
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 库房名称 */
    @Schema(description = "库房名称")
    private String warehouseName;

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

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
