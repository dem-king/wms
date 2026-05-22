package com.wms.report.service.impl;

import com.wms.common.exception.BizException;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.converter.ChartOptionConverter;
import com.wms.report.domain.dto.ChartQueryDto;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.dto.TransferReportQueryDto;
import com.wms.report.domain.vo.*;
import com.wms.report.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 图表服务实现类
 * 根据reportType路由到对应报表服务，再按chartType转换为ECharts option
 */
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final InboundReportService inboundReportService;
    private final OutboundReportService outboundReportService;
    private final StockReportService stockReportService;
    private final ReturnReportService returnReportService;
    private final ScrapReportService scrapReportService;
    private final TransferReportService transferReportService;
    private final AlertReportService alertReportService;
    private final ChartOptionConverter chartOptionConverter;

    /**
     * 获取图表选项数据
     * 根据reportType路由到对应报表服务，再按chartType转换为ECharts option
     *
     * @param queryDto 图表查询参数
     * @return 图表选项VO
     */
    @Override
    public ChartOptionVo getChartOption(ChartQueryDto queryDto) {
        String reportType = queryDto.getReportType();
        String chartType = queryDto.getChartType();

        ChartOptionVo vo = new ChartOptionVo();
        vo.setReportType(reportType);
        vo.setChartType(chartType);

        // 饼图走分布数据
        if (ReportConstants.CHART_TYPE_PIE.equals(chartType)) {
            List<CommonReportVo.DistributionItem> distributionList = getDistributionData(reportType, queryDto);
            Map<String, Object> option = chartOptionConverter.toDistributionOption(distributionList);
            vo.setOption(option);
            return vo;
        }

        // 折线图/柱状图走趋势数据
        if (ReportConstants.CHART_TYPE_LINE.equals(chartType) || ReportConstants.CHART_TYPE_BAR.equals(chartType)) {
            List<CommonReportVo.TrendItem> trendList = getTrendData(reportType, queryDto);
            String yName = getYName(reportType);
            String y2Name = getY2Name(reportType);
            Map<String, Object> option = chartOptionConverter.toTrendOption(trendList, chartType, yName, y2Name);
            vo.setOption(option);
            return vo;
        }

        throw new BizException("不支持的图表类型: " + chartType);
    }

    /**
     * 获取趋势数据
     */
    private List<CommonReportVo.TrendItem> getTrendData(String reportType, ChartQueryDto queryDto) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> inboundReportService.getTrend(toReportQueryDto(queryDto)).getTrendList();
            case ReportConstants.REPORT_TYPE_OUTBOUND -> outboundReportService.getTrend(toReportQueryDto(queryDto)).getTrendList();
            case ReportConstants.REPORT_TYPE_STOCK -> stockReportService.getTrend(toReportQueryDto(queryDto)).getTrendList();
            case ReportConstants.REPORT_TYPE_RETURN -> returnReportService.getTrend(toReportQueryDto(queryDto)).getTrendList();
            case ReportConstants.REPORT_TYPE_SCRAP -> scrapReportService.getTrend(toReportQueryDto(queryDto)).getTrendList();
            case ReportConstants.REPORT_TYPE_TRANSFER -> transferReportService.getTrend(toTransferQueryDto(queryDto)).getTrendList();
            default -> throw new BizException("不支持的报表类型: " + reportType);
        };
    }

    /**
     * 获取分布数据
     */
    private List<CommonReportVo.DistributionItem> getDistributionData(String reportType, ChartQueryDto queryDto) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> inboundReportService.getDistribution(toReportQueryDto(queryDto)).getDistributionList();
            case ReportConstants.REPORT_TYPE_OUTBOUND -> outboundReportService.getDistribution(toReportQueryDto(queryDto)).getDistributionList();
            case ReportConstants.REPORT_TYPE_STOCK -> stockReportService.getDistribution(toReportQueryDto(queryDto)).getDistributionList();
            case ReportConstants.REPORT_TYPE_RETURN -> returnReportService.getDistribution(toReportQueryDto(queryDto)).getDistributionList();
            case ReportConstants.REPORT_TYPE_SCRAP -> scrapReportService.getDistribution(toReportQueryDto(queryDto)).getDistributionList();
            case ReportConstants.REPORT_TYPE_TRANSFER -> transferReportService.getDistribution(toTransferQueryDto(queryDto)).getDistributionList();
            default -> throw new BizException("不支持的报表类型: " + reportType);
        };
    }

    /**
     * ChartQueryDto转ReportQueryDto
     */
    private ReportQueryDto toReportQueryDto(ChartQueryDto dto) {
        ReportQueryDto queryDto = new ReportQueryDto();
        queryDto.setStartDate(dto.getStartDate());
        queryDto.setEndDate(dto.getEndDate());
        queryDto.setWarehouseId(dto.getWarehouseId());
        queryDto.setCategoryId(dto.getCategoryId());
        queryDto.setTrendType(dto.getTrendType());
        return queryDto;
    }

    /**
     * ChartQueryDto转TransferReportQueryDto
     */
    private TransferReportQueryDto toTransferQueryDto(ChartQueryDto dto) {
        TransferReportQueryDto queryDto = new TransferReportQueryDto();
        queryDto.setStartDate(dto.getStartDate());
        queryDto.setEndDate(dto.getEndDate());
        queryDto.setCategoryId(dto.getCategoryId());
        queryDto.setTrendType(dto.getTrendType());
        return queryDto;
    }

    /**
     * 获取Y轴名称(数量)
     */
    private String getYName(String reportType) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> "入库数量";
            case ReportConstants.REPORT_TYPE_OUTBOUND -> "出库数量";
            case ReportConstants.REPORT_TYPE_STOCK -> "库存数量";
            case ReportConstants.REPORT_TYPE_RETURN -> "归还数量";
            case ReportConstants.REPORT_TYPE_SCRAP -> "报废数量";
            case ReportConstants.REPORT_TYPE_TRANSFER -> "调拨数量";
            default -> "数量";
        };
    }

    /**
     * 获取第二Y轴名称(金额)
     */
    private String getY2Name(String reportType) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> "入库金额";
            case ReportConstants.REPORT_TYPE_OUTBOUND -> "出库金额";
            case ReportConstants.REPORT_TYPE_STOCK -> "库存金额";
            case ReportConstants.REPORT_TYPE_RETURN -> "归还金额";
            case ReportConstants.REPORT_TYPE_SCRAP -> "报废金额";
            case ReportConstants.REPORT_TYPE_TRANSFER -> "调拨金额";
            default -> "金额";
        };
    }
}
