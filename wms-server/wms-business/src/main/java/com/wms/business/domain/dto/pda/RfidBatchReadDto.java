package com.wms.business.domain.dto.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * RFID批量读取上报请求DTO
 * PDA端完成RFID批量读取后，将EPC码列表上报至后端进行差异对比
 */
@Data
@Schema(description = "RFID批量读取上报请求")
public class RfidBatchReadDto {

    /** 库房ID */
    @NotNull(message = "库房ID不能为空")
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 区域ID(可选，用于局部盘点) */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 实际读取到的EPC码列表(已去重) */
    @NotEmpty(message = "EPC码列表不能为空")
    @Schema(description = "实际读取到的EPC码列表")
    private List<String> epcCodes;

    /** 读取持续时长(秒) */
    @Schema(description = "读取持续时长(秒)")
    private Integer readDuration;
}