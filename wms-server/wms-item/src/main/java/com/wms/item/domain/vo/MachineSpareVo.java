package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机器-备件关联视图对象
 */
@Data
@Schema(description = "机器-备件关联信息")
public class MachineSpareVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 机器名称 */
    @Schema(description = "机器名称")
    private String machineName;

    /** 机器编号 */
    @Schema(description = "机器编号")
    private String machineCode;

    /** 备件物品ID */
    @Schema(description = "备件物品ID")
    private Long spareItemId;

    /** 备件物品名称 */
    @Schema(description = "备件物品名称")
    private String spareItemName;

    /** 备件物品编号 */
    @Schema(description = "备件物品编号")
    private String spareItemCode;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;
}
