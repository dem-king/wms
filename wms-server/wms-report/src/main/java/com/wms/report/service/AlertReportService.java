package com.wms.report.service;

import com.wms.report.domain.dto.AlertReportQueryDto;
import com.wms.report.domain.vo.AlertReportVo;

/**
 * 预警统计报表服务接口
 * 提供预警汇总、趋势、类型分布统计功能
 */
public interface AlertReportService {

    /**
     * 预警汇总统计
     * 查询时间范围内预警总触发次数、按预警类型汇总
     *
     * @param queryDto 预警报表查询参数
     * @return 预警汇总统计VO
     */
    AlertReportVo.SummaryVo getSummary(AlertReportQueryDto queryDto);

    /**
     * 预警趋势统计
     * 按日或按月返回时间序列数据
     *
     * @param queryDto 预警报表查询参数(含trendType)
     * @return 预警趋势统计VO
     */
    AlertReportVo.TrendVo getTrend(AlertReportQueryDto queryDto);

    /**
     * 预警类型分布统计
     * 按预警类型计算触发次数和占比
     *
     * @param queryDto 预警报表查询参数
     * @return 预警类型分布统计VO
     */
    AlertReportVo.DistributionVo getDistribution(AlertReportQueryDto queryDto);
}
