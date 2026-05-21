package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 机器-备件关联新增/更新DTO
 */
@Data
@Schema(description = "机器-备件关联请求")
public class MachineSpareDto {

    /** 机器名称 */
    @NotBlank(message = "机器名称不能为空")
    @Schema(description = "机器名称")
    private String machineName;

    /** 机器编号 */
    @NotBlank(message = "机器编号不能为空")
    @Schema(description = "机器编号")
    private String machineCode;

    /** 备件物品ID */
    @NotNull(message = "备件物品ID不能为空")
    @Schema(description = "备件物品ID")
    private Long spareItemId;

    /** 数量 */
    @NotNull(message = "数量不能为空")
    @Schema(description = "数量")
    private Integer quantity;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
