package com.wms.report.service;

import com.wms.report.domain.dto.ChartQueryDto;
import com.wms.report.domain.vo.ChartOptionVo;

/**
 * 图表服务接口
 * 根据报表类型和图表类型，返回ECharts option配置
 */
public interface ChartService {

    /**
     * 获取图表选项数据
     * 根据reportType路由到对应报表服务，再按chartType转换为ECharts option
     *
     * @param queryDto 图表查询参数
     * @return 图表选项VO
     */
    ChartOptionVo getChartOption(ChartQueryDto queryDto);
}
