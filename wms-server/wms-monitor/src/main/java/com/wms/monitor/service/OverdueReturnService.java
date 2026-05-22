package com.wms.monitor.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.monitor.domain.vo.OverdueReturnVo;

/**
 * 逾期归还服务接口
 * 提供逾期归还记录分页查询操作
 */
public interface OverdueReturnService {

    /**
     * 分页查询逾期归还记录
     *
     * @param pageParam   分页参数
     * @param alertLevel  提醒级别(可选)
     * @param status      处理状态(可选)
     * @return 分页结果
     */
    PageResult<OverdueReturnVo> page(PageParam pageParam, String alertLevel, String status);
}
