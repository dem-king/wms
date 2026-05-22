package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysOperLogQueryDto;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.domain.vo.SysOperLogVo;

/**
 * 操作日志服务接口
 * 提供操作日志分页查询和异步保存功能
 */
public interface SysOperLogService {

    /**
     * 分页查询操作日志
     *
     * @param pageParam 分页参数
     * @param queryDto  查询条件
     * @return 分页结果
     */
    PageResult<SysOperLogVo> page(PageParam pageParam, SysOperLogQueryDto queryDto);

    /**
     * 异步保存操作日志
     * 通过@Async实现异步写入，不阻塞业务线程
     *
     * @param operLog 操作日志实体
     */
    void asyncSave(SysOperLog operLog);
}
