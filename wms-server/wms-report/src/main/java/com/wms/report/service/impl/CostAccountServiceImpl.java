package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.CostAccountConfigDto;
import com.wms.report.domain.entity.ReportInboundDaily;
import com.wms.report.domain.entity.ReportOutboundDaily;
import com.wms.report.domain.entity.ReportScrapDaily;
import com.wms.report.domain.entity.ReportTransferDaily;
import com.wms.report.domain.vo.CostAccountVo;
import com.wms.report.mapper.ReportInboundDailyMapper;
import com.wms.report.mapper.ReportOutboundDailyMapper;
import com.wms.report.mapper.ReportScrapDailyMapper;
import com.wms.report.mapper.ReportTransferDailyMapper;
import com.wms.report.service.CostAccountService;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 年度费用核算服务实现类
 * 从sys_config读取配置，从预聚合表汇总各周期费用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CostAccountServiceImpl implements CostAccountService {

    private final SysConfigMapper sysConfigMapper;
    private final ReportInboundDailyMapper inboundMapper;
    private final ReportOutboundDailyMapper outboundMapper;
    private final ReportScrapDailyMapper scrapMapper;
    private final ReportTransferDailyMapper transferMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取年度费用核算汇总
     * 读取sys_config获取配置，校验enabled=true，从预聚合表汇总费用
     *
     * @param year 核算年度
     * @return 费用核算结果VO
     */
    @Override
    public CostAccountVo getSummary(Integer year) {
        // 校验是否启用
        boolean enabled = getBooleanConfig(ReportConstants.COST_CONFIG_ENABLED_KEY);
        if (!enabled) {
            throw new BizException("费用核算功能未启用");
        }

        String period = getConfigValue(ReportConstants.COST_CONFIG_PERIOD_KEY);
        if (period == null) {
            period = ReportConstants.COST_PERIOD_MONTHLY;
        }

        CostAccountVo vo = new CostAccountVo();
        vo.setYear(year);
        vo.setPeriod(period);

        List<CostAccountVo.CostPeriodItem> periodList = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        List<DateRange> ranges = calcDateRanges(year, period);
        for (DateRange range : ranges) {
            CostAccountVo.CostPeriodItem item = new CostAccountVo.CostPeriodItem();
            item.setPeriodLabel(range.label);

            // 采购入库费用
            BigDecimal purchaseCost = sumInboundAmount(range.start, range.end);
            item.setPurchaseCost(purchaseCost);

            // 消耗出库费用
            BigDecimal consumeCost = sumOutboundAmount(range.start, range.end);
            item.setConsumeCost(consumeCost);

            // 报废损失费用
            BigDecimal scrapCost = sumScrapAmount(range.start, range.end);
            item.setScrapCost(scrapCost);

            // 调拨费用
            BigDecimal transferCost = sumTransferAmount(range.start, range.end);
            item.setTransferCost(transferCost);

            // 周期合计
            BigDecimal periodTotal = purchaseCost.add(consumeCost).add(scrapCost).add(transferCost);
            item.setPeriodTotal(periodTotal);

            periodList.add(item);
            totalCost = totalCost.add(periodTotal);
        }

        vo.setPeriodList(periodList);
        vo.setTotalCost(totalCost);
        return vo;
    }

    /**
     * 获取费用核算配置
     *
     * @return 费用核算配置VO
     */
    @Override
    public CostAccountVo getConfig() {
        CostAccountVo vo = new CostAccountVo();
        Boolean enabled = Boolean.valueOf(getConfigValue(ReportConstants.COST_CONFIG_ENABLED_KEY));
        String yearStr = getConfigValue(ReportConstants.COST_CONFIG_YEAR_KEY);
        String period = getConfigValue(ReportConstants.COST_CONFIG_PERIOD_KEY);

        vo.setYear(yearStr != null ? Integer.parseInt(yearStr) : null);
        vo.setPeriod(period);
        return vo;
    }

    /**
     * 更新费用核算配置
     * 更新sys_config，清除Redis缓存
     *
     * @param dto 费用核算配置参数
     * @return 更新后的配置VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CostAccountVo updateConfig(CostAccountConfigDto dto) {
        updateConfigValue(ReportConstants.COST_CONFIG_ENABLED_KEY, dto.getEnabled().toString());
        updateConfigValue(ReportConstants.COST_CONFIG_YEAR_KEY, dto.getYear().toString());
        updateConfigValue(ReportConstants.COST_CONFIG_PERIOD_KEY, dto.getPeriod());

        // 清除Redis缓存
        try {
            redisTemplate.delete(ReportConstants.COST_CACHE_PREFIX + "*");
        } catch (Exception e) {
            log.warn("清除费用核算Redis缓存失败", e);
        }

        CostAccountVo vo = new CostAccountVo();
        vo.setYear(dto.getYear());
        vo.setPeriod(dto.getPeriod());
        return vo;
    }

    /**
     * 汇总入库费用
     */
    private BigDecimal sumInboundAmount(LocalDate start, LocalDate end) {
        List<ReportInboundDaily> list = inboundMapper.selectList(
                new LambdaQueryWrapper<ReportInboundDaily>()
                        .ge(ReportInboundDaily::getStatDate, start)
                        .le(ReportInboundDaily::getStatDate, end)
        );
        return list.stream().map(ReportInboundDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 汇总出库费用
     */
    private BigDecimal sumOutboundAmount(LocalDate start, LocalDate end) {
        List<ReportOutboundDaily> list = outboundMapper.selectList(
                new LambdaQueryWrapper<ReportOutboundDaily>()
                        .ge(ReportOutboundDaily::getStatDate, start)
                        .le(ReportOutboundDaily::getStatDate, end)
        );
        return list.stream().map(ReportOutboundDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 汇总报废费用
     */
    private BigDecimal sumScrapAmount(LocalDate start, LocalDate end) {
        List<ReportScrapDaily> list = scrapMapper.selectList(
                new LambdaQueryWrapper<ReportScrapDaily>()
                        .ge(ReportScrapDaily::getStatDate, start)
                        .le(ReportScrapDaily::getStatDate, end)
        );
        return list.stream().map(ReportScrapDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 汇总调拨费用
     */
    private BigDecimal sumTransferAmount(LocalDate start, LocalDate end) {
        List<ReportTransferDaily> list = transferMapper.selectList(
                new LambdaQueryWrapper<ReportTransferDaily>()
                        .ge(ReportTransferDaily::getStatDate, start)
                        .le(ReportTransferDaily::getStatDate, end)
        );
        return list.stream().map(ReportTransferDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 读取配置值
     */
    private String getConfigValue(String key) {
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key)
        );
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 读取布尔配置
     */
    private boolean getBooleanConfig(String key) {
        String value = getConfigValue(key);
        return Boolean.parseBoolean(value);
    }

    /**
     * 更新配置值
     */
    private void updateConfigValue(String key, String value) {
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key)
        );
        if (config == null) {
            throw new BizException("配置项不存在: " + key);
        }
        config.setConfigValue(value);
        sysConfigMapper.updateById(config);
    }

    /**
     * 根据周期计算日期范围列表
     */
    private List<DateRange> calcDateRanges(int year, String period) {
        List<DateRange> ranges = new ArrayList<>();
        if (ReportConstants.COST_PERIOD_MONTHLY.equals(period)) {
            for (int m = 1; m <= 12; m++) {
                LocalDate start = LocalDate.of(year, m, 1);
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                ranges.add(new DateRange(year + "年" + m + "月", start, end));
            }
        } else if (ReportConstants.COST_PERIOD_QUARTERLY.equals(period)) {
            for (int q = 1; q <= 4; q++) {
                int startMonth = (q - 1) * 3 + 1;
                int endMonth = q * 3;
                LocalDate start = LocalDate.of(year, startMonth, 1);
                LocalDate end = LocalDate.of(year, endMonth, 1).withDayOfMonth(
                        LocalDate.of(year, endMonth, 1).lengthOfMonth());
                ranges.add(new DateRange(year + "年第" + q + "季度", start, end));
            }
        } else if (ReportConstants.COST_PERIOD_YEARLY.equals(period)) {
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            ranges.add(new DateRange(year + "年度", start, end));
        } else {
            throw new BizException("不支持的核算周期: " + period);
        }
        return ranges;
    }

    /**
     * 日期范围内部类
     */
    private record DateRange(String label, LocalDate start, LocalDate end) {
    }
}
