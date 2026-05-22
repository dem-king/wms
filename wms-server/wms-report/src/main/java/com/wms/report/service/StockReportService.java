package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.StockReportVo;

/**
 * 库存统计报表服务接口
 * 提供库存汇总、趋势、分类分布统计功能
 */
public interface StockReportService {

    /**
     * 库存汇总统计
     * 取时间范围内最新一天的快照数据
     *
     * @param queryDto 报表查询参数
     * @return 库存汇总统计VO
     */
    StockReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 库存趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 库存趋势统计VO
     */
    StockReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 库存分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 库存分类分布统计VO
     */
    StockReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
