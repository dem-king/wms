package com.wms.report.service;

import com.wms.report.domain.dto.CostAccountConfigDto;
import com.wms.report.domain.vo.CostAccountVo;

/**
 * 年度费用核算服务接口
 * 提供费用核算汇总查询、配置读取和更新功能
 */
public interface CostAccountService {

    /**
     * 获取年度费用核算汇总
     * 读取sys_config获取配置，校验enabled=true，从预聚合表汇总费用
     *
     * @param year 核算年度
     * @return 费用核算结果VO
     */
    CostAccountVo getSummary(Integer year);

    /**
     * 获取费用核算配置
     *
     * @return 费用核算配置VO
     */
    CostAccountVo getConfig();

    /**
     * 更新费用核算配置
     * 更新sys_config，清除Redis缓存
     *
     * @param dto 费用核算配置参数
     * @return 更新后的配置VO
     */
    CostAccountVo updateConfig(CostAccountConfigDto dto);
}
