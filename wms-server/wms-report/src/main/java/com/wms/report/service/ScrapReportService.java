package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.ScrapReportVo;

/**
 * 报废统计报表服务接口
 * 提供报废汇总、趋势、分类分布统计功能
 */
public interface ScrapReportService {

    /**
     * 报废汇总统计
     * 查询时间范围内报废总数量、总金额、按分类汇总
     *
     * @param queryDto 报表查询参数
     * @return 报废汇总统计VO
     */
    ScrapReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 报废趋势统计
     * 按日或按月返回时间序列数据
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 报废趋势统计VO
     */
    ScrapReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 报废分类分布统计
     * 按物品分类计算数量和占比
     *
     * @param queryDto 报表查询参数
     * @return 报废分类分布统计VO
     */
    ScrapReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
