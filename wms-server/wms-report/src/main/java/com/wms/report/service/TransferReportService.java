package com.wms.report.service;

import com.wms.report.domain.dto.TransferReportQueryDto;
import com.wms.report.domain.vo.TransferReportVo;

/**
 * 调拨统计报表服务接口
 * 提供调拨汇总、趋势、分类分布统计功能
 */
public interface TransferReportService {

    /**
     * 调拨汇总统计
     * 查询时间范围内调拨总数量、总金额、按分类汇总(含调出/调入库房维度)
     *
     * @param queryDto 调拨报表查询参数
     * @return 调拨汇总统计VO
     */
    TransferReportVo.SummaryVo getSummary(TransferReportQueryDto queryDto);

    /**
     * 调拨趋势统计
     * 按日或按月返回时间序列数据
     *
     * @param queryDto 调拨报表查询参数(含trendType)
     * @return 调拨趋势统计VO
     */
    TransferReportVo.TrendVo getTrend(TransferReportQueryDto queryDto);

    /**
     * 调拨分类分布统计
     * 按物品分类计算数量和占比
     *
     * @param queryDto 调拨报表查询参数
     * @return 调拨分类分布统计VO
     */
    TransferReportVo.DistributionVo getDistribution(TransferReportQueryDto queryDto);
}
