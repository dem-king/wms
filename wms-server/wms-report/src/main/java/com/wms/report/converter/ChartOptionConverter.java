package com.wms.report.converter;

import com.wms.report.domain.vo.CommonReportVo;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表选项转换器
 * 将报表数据转换为ECharts option规范结构
 */
@Component
public class ChartOptionConverter {

    /**
     * 将趋势数据转换为折线图/柱状图option
     *
     * @param trendList 趋势数据列表
     * @param chartType line或bar
     * @param yName Y轴名称(数量)
     * @param y2Name 第二Y轴名称(金额)
     * @return ECharts option Map
     */
    public Map<String, Object> toTrendOption(List<CommonReportVo.TrendItem> trendList,
                                              String chartType, String yName, String y2Name) {
        Map<String, Object> option = new LinkedHashMap<>();

        // legend
        Map<String, Object> legend = new LinkedHashMap<>();
        legend.put("data", List.of(yName, y2Name));
        option.put("legend", legend);

        // xAxis
        List<String> xData = trendList.stream().map(CommonReportVo.TrendItem::getDate).collect(Collectors.toList());
        Map<String, Object> xAxis = new LinkedHashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", xData);
        option.put("xAxis", xAxis);

        // yAxis
        Map<String, Object> y1 = new LinkedHashMap<>();
        y1.put("type", "value");
        y1.put("name", yName);
        Map<String, Object> y2 = new LinkedHashMap<>();
        y2.put("type", "value");
        y2.put("name", y2Name);
        option.put("yAxis", List.of(y1, y2));

        // series
        List<Integer> quantityData = trendList.stream().map(CommonReportVo.TrendItem::getQuantity).collect(Collectors.toList());
        List<Object> amountData = trendList.stream().map(CommonReportVo.TrendItem::getAmount).collect(Collectors.toList());

        Map<String, Object> series1 = new LinkedHashMap<>();
        series1.put("name", yName);
        series1.put("type", chartType);
        series1.put("data", quantityData);

        Map<String, Object> series2 = new LinkedHashMap<>();
        series2.put("name", y2Name);
        series2.put("type", chartType);
        series2.put("yAxisIndex", 1);
        series2.put("data", amountData);

        option.put("series", List.of(series1, series2));
        return option;
    }

    /**
     * 将分布数据转换为饼图option
     *
     * @param distributionList 分布数据列表
     * @return ECharts option Map
     */
    public Map<String, Object> toDistributionOption(List<CommonReportVo.DistributionItem> distributionList) {
        Map<String, Object> option = new LinkedHashMap<>();

        // legend
        List<String> legendData = distributionList.stream()
                .map(CommonReportVo.DistributionItem::getCategoryName).collect(Collectors.toList());
        Map<String, Object> legend = new LinkedHashMap<>();
        legend.put("data", legendData);
        option.put("legend", legend);

        // series
        List<Map<String, Object>> pieData = distributionList.stream().map(item -> {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("name", item.getCategoryName());
            d.put("value", item.getQuantity());
            return d;
        }).collect(Collectors.toList());

        Map<String, Object> series = new LinkedHashMap<>();
        series.put("type", "pie");
        series.put("data", pieData);
        option.put("series", List.of(series));

        return option;
    }
}
