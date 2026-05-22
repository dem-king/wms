package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.OutboundReportVo;

/**
 * 出库统计报表服务接口
 * 提供出库汇总、趋势、分类分布统计功能
 */
public interface OutboundReportService {

    /**
     * 出库汇总统计
     *
     * @param queryDto 报表查询参数
     * @return 出库汇总统计VO
     */
    OutboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 出库趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 出库趋势统计VO
     */
    OutboundReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 出库分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 出库分类分布统计VO
     */
    OutboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
