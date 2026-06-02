package com.wms.business.domain.dto.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 盘点结果提交请求DTO
 * PDA端确认盘点差异后，提交盘点结果至后端保存
 */
@Data
@Schema(description = "盘点结果提交请求")
public class StockCheckDto {

    /** 库房ID */
    @NotNull(message = "库房ID不能为空")
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 区域ID(可选，用于局部盘点) */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 盘点类型(1-全盘 2-抽盘) */
    @NotNull(message = "盘点类型不能为空")
    @Schema(description = "盘点类型(1-全盘 2-抽盘)")
    private Integer checkType;

    /** 盘点明细列表 */
    @NotEmpty(message = "盘点明细不能为空")
    @Schema(description = "盘点明细列表")
    private List<StockCheckDetailDto> items;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}