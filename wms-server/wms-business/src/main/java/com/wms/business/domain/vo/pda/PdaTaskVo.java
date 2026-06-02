package com.wms.business.domain.vo.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PDA待办任务统计VO
 * 返回当前用户各类型待处理任务的数量统计
 */
@Data
@Schema(description = "PDA待办任务统计")
public class PdaTaskVo {

    /** 待提交入库单数 */
    @Schema(description = "待提交入库单数")
    private Integer inboundPendingCount;

    /** 待提交出库单数 */
    @Schema(description = "待提交出库单数")
    private Integer outboundPendingCount;

    /** 待提交归还单数 */
    @Schema(description = "待提交归还单数")
    private Integer returnPendingCount;

    /** 待审批单据数 */
    @Schema(description = "待审批单据数")
    private Integer approvalPendingCount;

    /** 库存预警数 */
    @Schema(description = "库存预警数")
    private Integer stockAlertCount;

    /** 超期归还数 */
    @Schema(description = "超期归还数")
    private Integer overdueReturnCount;
}