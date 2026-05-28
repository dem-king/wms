package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.common.util.SecurityUtil;
import com.wms.report.domain.dto.AlertReportQueryDto;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.dto.dashboard.DashboardConfigDto;
import com.wms.report.domain.entity.MonitorStockAlert;
import com.wms.report.domain.vo.AlertReportVo;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.domain.vo.OutboundReportVo;
import com.wms.report.domain.vo.StockReportVo;
import com.wms.report.domain.vo.dashboard.DashboardConfigVo;
import com.wms.report.domain.vo.dashboard.DashboardDataVo;
import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;
import com.wms.report.mapper.ReportMonitorStockAlertMapper;
import com.wms.report.service.AlertReportService;
import com.wms.report.service.DashboardLocationWeatherService;
import com.wms.report.service.DashboardService;
import com.wms.report.service.InboundReportService;
import com.wms.report.service.OutboundReportService;
import com.wms.report.service.StockReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StockReportService stockReportService;
    private final InboundReportService inboundReportService;
    private final OutboundReportService outboundReportService;
    private final AlertReportService alertReportService;
    private final DashboardLocationWeatherService dashboardLocationWeatherService;
    
    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsReturnOrderMapper returnOrderMapper;
    private final ReportMonitorStockAlertMapper stockAlertMapper;
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String CONFIG_KEY_PREFIX = "dashboard:config:";

    @Override
    public DashboardDataVo getDashboardData(String role) {
        DashboardDataVo dashboard = new DashboardDataVo();
        
        // 1. 构建 Metrics
        dashboard.setMetrics(buildMetrics());
        
        // 2. 构建 Tasks
        dashboard.setTasks(buildTasks(role));
        
        // 3. 构建 Alerts
        dashboard.setAlerts(buildAlerts(role));
        
        // 4. 构建 Charts
        dashboard.setCharts(buildCharts());
        
        // 5. 构建 QuickActions
        dashboard.setQuickActions(buildQuickActions());
        
        return dashboard;
    }

    @Override
    public DashboardLocationWeatherVo getLocationWeather(Double latitude, Double longitude) {
        return dashboardLocationWeatherService.getLocationWeather(latitude, longitude);
    }

    private List<DashboardDataVo.MetricItemVo> buildMetrics() {
        List<DashboardDataVo.MetricItemVo> metrics = new ArrayList<>();
        
        ReportQueryDto queryDto = new ReportQueryDto();
        queryDto.setStartDate(LocalDate.now().withDayOfMonth(1));
        queryDto.setEndDate(LocalDate.now());
        
        // 获取库存汇总
        StockReportVo.SummaryVo stockSummary = stockReportService.getSummary(queryDto);
        // 获取入库汇总
        InboundReportVo.SummaryVo inboundSummary = inboundReportService.getSummary(queryDto);
        // 获取出库汇总
        OutboundReportVo.SummaryVo outboundSummary = outboundReportService.getSummary(queryDto);
        
        AlertReportQueryDto alertQuery = new AlertReportQueryDto();
        alertQuery.setStartDate(LocalDate.now().withDayOfMonth(1));
        alertQuery.setEndDate(LocalDate.now());
        AlertReportVo.SummaryVo alertSummary = alertReportService.getSummary(alertQuery);

        metrics.add(createMetric("total_quantity", "库存总数量", new BigDecimal(stockSummary != null && stockSummary.getTotalQuantity() != null ? stockSummary.getTotalQuantity() : 0), "件", "up", "Box", "#409EFF"));
        metrics.add(createMetric("total_amount", "库存总金额", stockSummary != null && stockSummary.getTotalAmount() != null ? stockSummary.getTotalAmount() : BigDecimal.ZERO, "元", "up", "Money", "#67C23A"));
        metrics.add(createMetric("inbound_month", "本月入库单数", new BigDecimal(inboundSummary != null && inboundSummary.getOrderCount() != null ? inboundSummary.getOrderCount() : 0), "单", "up", "Download", "#E6A23C"));
        metrics.add(createMetric("outbound_month", "本月出库单数", new BigDecimal(outboundSummary != null && outboundSummary.getOrderCount() != null ? outboundSummary.getOrderCount() : 0), "单", "up", "Upload", "#F56C6C"));
        
        long pendingApprovalCount = inboundOrderMapper.selectCount(new LambdaQueryWrapper<WmsInboundOrder>().eq(WmsInboundOrder::getStatus, 1))
                + outboundOrderMapper.selectCount(new LambdaQueryWrapper<WmsOutboundOrder>().eq(WmsOutboundOrder::getStatus, 1));
        
        metrics.add(createMetric("pending_approval", "待审批单据", new BigDecimal(pendingApprovalCount), "单", "flat", "Document", "#909399"));
        metrics.add(createMetric("stock_warning", "预警触发次数", new BigDecimal(alertSummary != null && alertSummary.getTotalTriggerCount() != null ? alertSummary.getTotalTriggerCount() : 0), "次", "up", "Warning", "#F56C6C"));

        return metrics;
    }

    private DashboardDataVo.MetricItemVo createMetric(String key, String name, BigDecimal value, String unit, String trend, String icon, String color) {
        DashboardDataVo.MetricItemVo item = new DashboardDataVo.MetricItemVo();
        item.setKey(key);
        item.setName(name);
        item.setValue(value);
        item.setUnit(unit);
        item.setChangeRate(new BigDecimal("5.0")); // 模拟环比数据
        item.setTrend(trend);
        item.setIcon(icon);
        item.setColor(color);
        return item;
    }

    private List<DashboardDataVo.TaskItemVo> buildTasks(String role) {
        List<DashboardDataVo.TaskItemVo> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 待审核入库单
        List<WmsInboundOrder> inboundOrders = inboundOrderMapper.selectList(new LambdaQueryWrapper<WmsInboundOrder>()
                .eq(WmsInboundOrder::getStatus, 1).orderByDesc(WmsInboundOrder::getCreateTime).last("LIMIT 5"));
        for (WmsInboundOrder order : inboundOrders) {
            DashboardDataVo.TaskItemVo task = new DashboardDataVo.TaskItemVo();
            task.setId(order.getId().toString());
            task.setType("approval");
            task.setTitle("待审批入库单 - " + order.getOrderNo());
            task.setDescription("申请人：" + order.getCreateBy() + " | 金额：¥" + (order.getTotalAmount() != null ? order.getTotalAmount() : 0));
            task.setPriority("high");
            task.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(formatter) : "");
            task.setActionUrl("/business/inbound");
            tasks.add(task);
        }

        // 待审核出库单
        List<WmsOutboundOrder> outboundOrders = outboundOrderMapper.selectList(new LambdaQueryWrapper<WmsOutboundOrder>()
                .eq(WmsOutboundOrder::getStatus, 1).orderByDesc(WmsOutboundOrder::getCreateTime).last("LIMIT 5"));
        for (WmsOutboundOrder order : outboundOrders) {
            DashboardDataVo.TaskItemVo task = new DashboardDataVo.TaskItemVo();
            task.setId(order.getId().toString());
            task.setType("outbound");
            task.setTitle("待审批出库单 - " + order.getOrderNo());
            task.setDescription("申请人：" + order.getCreateBy() + " | 接收人：" + order.getReceiver());
            task.setPriority("medium");
            task.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(formatter) : "");
            task.setActionUrl("/business/outbound");
            tasks.add(task);
        }

        return tasks;
    }

    private List<DashboardDataVo.AlertItemVo> buildAlerts(String role) {
        List<DashboardDataVo.AlertItemVo> alerts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<MonitorStockAlert> dbAlerts = stockAlertMapper.selectList(new LambdaQueryWrapper<MonitorStockAlert>()
                .eq(MonitorStockAlert::getStatus, "0")
                .orderByDesc(MonitorStockAlert::getCreateTime)
                .last("LIMIT 5"));

        for (MonitorStockAlert alert : dbAlerts) {
            DashboardDataVo.AlertItemVo item = new DashboardDataVo.AlertItemVo();
            item.setId(alert.getId().toString());
            item.setType("stock_low");
            item.setTitle("库存不足预警");
            item.setContent("物品[" + alert.getItemName() + "]当前库存" + alert.getCurrentQuantity() + "，触发阈值" + alert.getThresholdValue());
            item.setLevel("STOCK_LOW".equals(alert.getAlertType()) ? "warning" : "danger");
            item.setCreateTime(alert.getCreateTime() != null ? alert.getCreateTime().format(formatter) : "");
            alerts.add(item);
        }

        return alerts;
    }

    private DashboardDataVo.ChartDataVo buildCharts() {
        DashboardDataVo.ChartDataVo charts = new DashboardDataVo.ChartDataVo();
        
        ReportQueryDto queryDto = new ReportQueryDto();
        queryDto.setStartDate(LocalDate.now().minusDays(30));
        queryDto.setEndDate(LocalDate.now());
        queryDto.setTrendType("DAILY");

        // 趋势
        StockReportVo.TrendVo stockTrend = stockReportService.getTrend(queryDto);
        List<DashboardDataVo.ChartDataVo.TrendData> trendList = new ArrayList<>();
        if (stockTrend != null && stockTrend.getTrendList() != null) {
            for (CommonReportVo.TrendItem item : stockTrend.getTrendList()) {
                DashboardDataVo.ChartDataVo.TrendData t = new DashboardDataVo.ChartDataVo.TrendData();
                t.setDate(item.getDate());
                t.setQuantity(new BigDecimal(item.getQuantity() != null ? item.getQuantity() : 0));
                t.setAmount(item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO);
                trendList.add(t);
            }
        }
        charts.setTrend(trendList);

        // 出入库对比
        InboundReportVo.TrendVo inboundTrend = inboundReportService.getTrend(queryDto);
        OutboundReportVo.TrendVo outboundTrend = outboundReportService.getTrend(queryDto);
        
        List<DashboardDataVo.ChartDataVo.CompareData> compareList = new ArrayList<>();
        if (inboundTrend != null && inboundTrend.getTrendList() != null) {
            for (CommonReportVo.TrendItem inItem : inboundTrend.getTrendList()) {
                DashboardDataVo.ChartDataVo.CompareData c = new DashboardDataVo.ChartDataVo.CompareData();
                c.setDate(inItem.getDate());
                c.setInbound(inItem.getQuantity());
                c.setOutbound(0);
                if (outboundTrend != null && outboundTrend.getTrendList() != null) {
                    outboundTrend.getTrendList().stream().filter(o -> o.getDate().equals(inItem.getDate())).findFirst().ifPresent(o -> c.setOutbound(o.getQuantity()));
                }
                compareList.add(c);
            }
        }
        charts.setCompare(compareList);

        // 分类分布
        StockReportVo.DistributionVo distribution = stockReportService.getDistribution(queryDto);
        List<DashboardDataVo.ChartDataVo.DistributionData> distList = new ArrayList<>();
        if (distribution != null && distribution.getDistributionList() != null) {
            for (CommonReportVo.DistributionItem item : distribution.getDistributionList()) {
                DashboardDataVo.ChartDataVo.DistributionData d = new DashboardDataVo.ChartDataVo.DistributionData();
                d.setName(item.getCategoryName());
                d.setValue(item.getQuantity());
                distList.add(d);
            }
        }
        charts.setDistribution(distList);

        return charts;
    }

    private List<DashboardDataVo.QuickActionItemVo> buildQuickActions() {
        List<DashboardDataVo.QuickActionItemVo> actions = new ArrayList<>();
        actions.add(createAction("1", "入库", "Download", "/business/inbound", "#409EFF"));
        actions.add(createAction("2", "出库", "Upload", "/business/outbound", "#67C23A"));
        actions.add(createAction("3", "归还", "RefreshLeft", "/business/return", "#E6A23C"));
        actions.add(createAction("4", "报废", "Delete", "/business/scrap", "#F56C6C"));
        actions.add(createAction("5", "调拨", "Switch", "/business/transfer", "#909399"));
        actions.add(createAction("6", "查询", "Search", "/stock", "#409EFF"));
        actions.add(createAction("7", "可视化", "DataBoard", "/warehouse/visual", "#67C23A"));
        actions.add(createAction("8", "标签", "PriceTag", "/label", "#E6A23C"));
        return actions;
    }

    private DashboardDataVo.QuickActionItemVo createAction(String id, String name, String icon, String url, String color) {
        DashboardDataVo.QuickActionItemVo action = new DashboardDataVo.QuickActionItemVo();
        action.setId(id);
        action.setName(name);
        action.setIcon(icon);
        action.setUrl(url);
        action.setColor(color);
        return action;
    }

    @Override
    public DashboardConfigVo getConfig() {
        String username = SecurityUtil.getCurrentUsername();
        Object cacheObj = redisTemplate.opsForValue().get(CONFIG_KEY_PREFIX + username);
        if (cacheObj instanceof DashboardConfigVo) {
            return (DashboardConfigVo) cacheObj;
        }
        
        // 默认配置
        DashboardConfigVo config = new DashboardConfigVo();
        config.setMetrics(Arrays.asList("total_quantity", "total_amount", "inbound_month", "outbound_month"));
        config.setQuickActions(Arrays.asList("1", "2", "3", "4", "6", "7"));
        config.setChartTimeRange("month");
        config.setLayoutRatio(Arrays.asList(6, 18));
        config.setTheme("light");
        return config;
    }

    @Override
    public void saveConfig(DashboardConfigDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        DashboardConfigVo config = new DashboardConfigVo();
        config.setMetrics(dto.getMetrics());
        config.setQuickActions(dto.getQuickActions());
        config.setChartTimeRange(dto.getChartTimeRange());
        config.setLayoutRatio(dto.getLayoutRatio());
        config.setTheme(dto.getTheme());
        
        redisTemplate.opsForValue().set(CONFIG_KEY_PREFIX + username, config);
    }
}
