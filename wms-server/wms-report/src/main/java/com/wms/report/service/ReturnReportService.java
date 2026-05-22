package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.ReturnReportVo;

/**
 * 借还统计报表服务接口
 * 提供借还汇总、趋势、分类分布统计功能
 */
public interface ReturnReportService {

    /**
     * 借还汇总统计
     * 额外包含正常/损坏归还数量和正常归还率
     *
     * @param queryDto 报表查询参数
     * @return 借还汇总统计VO
     */
    ReturnReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 借还趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 借还趋势统计VO
     */
    ReturnReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 借还分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 借还分类分布统计VO
     */
    ReturnReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
