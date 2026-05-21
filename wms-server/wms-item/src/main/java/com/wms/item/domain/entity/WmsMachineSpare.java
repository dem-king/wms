package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器-备件关联实体
 * 对应表 wms_machine_spare，记录机器与备件物品的关联关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_machine_spare")
public class WmsMachineSpare extends BaseEntity {

    /** 机器名称 */
    @Schema(description = "机器名称")
    private String machineName;

    /** 机器编号 */
    @Schema(description = "机器编号")
    private String machineCode;

    /** 备件物品ID */
    @Schema(description = "备件物品ID")
    private Long spareItemId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
