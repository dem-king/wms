package com.wms.business.domain.vo.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 盘点结果提交响应VO
 * 返回盘点单ID和提交后的状态
 */
@Data
@Schema(description = "盘点结果提交响应")
public class StockCheckResultVo {

    /** 盘点单ID */
    @Schema(description = "盘点单ID")
    private Long checkId;

    /** 盘点状态(0-草稿 1-已提交 2-已确认) */
    @Schema(description = "盘点状态")
    private Integer status;
}