package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.InboundReportVo;

/**
 * 入库统计报表服务接口
 * 提供入库汇总、趋势、分类分布统计功能
 */
public interface InboundReportService {

    /**
     * 入库汇总统计
     * 查询时间范围内入库总数量、总金额、按分类汇总
     *
     * @param queryDto 报表查询参数
     * @return 入库汇总统计VO
     */
    InboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 入库趋势统计
     * 按日或按月返回时间序列数据
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 入库趋势统计VO
     */
    InboundReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 入库分类分布统计
     * 按物品分类计算数量和占比
     *
     * @param queryDto 报表查询参数
     * @return 入库分类分布统计VO
     */
    InboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
