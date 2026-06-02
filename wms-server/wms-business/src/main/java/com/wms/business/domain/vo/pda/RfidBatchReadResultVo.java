package com.wms.business.domain.vo.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * RFID批量读取上报结果VO
 * 返回系统在库标签列表以及盘盈/盘亏差异明细
 */
@Data
@Schema(description = "RFID批量读取上报结果")
public class RfidBatchReadResultVo {

    /** 系统在库标签数 */
    @Schema(description = "系统在库标签数")
    private Integer systemCount;

    /** 实际读取标签数 */
    @Schema(description = "实际读取标签数")
    private Integer actualCount;

    /** 匹配标签数 */
    @Schema(description = "匹配标签数")
    private Integer matchCount;

    /** 盘盈明细(实际有但系统无的EPC) */
    @Schema(description = "盘盈明细")
    private List<StockCheckDiffDetailVo> surplusDetails;

    /** 盘亏明细(系统有但实际无的标签) */
    @Schema(description = "盘亏明细")
    private List<StockCheckDiffDetailVo> deficitDetails;
}