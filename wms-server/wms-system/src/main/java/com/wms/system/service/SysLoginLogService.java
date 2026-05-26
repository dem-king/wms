package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysLoginLogQueryDto;
import com.wms.system.domain.vo.SysLoginLogVo;
import com.wms.system.domain.entity.SysLoginLog;

/**
 * 登录日志服务接口
 * 提供登录日志分页查询和记录登录日志功能
 */
public interface SysLoginLogService {

    /**
     * 分页查询登录日志
     *
     * @param pageParam 分页参数
     * @param queryDto  查询条件
     * @return 分页结果
     */
    PageResult<SysLoginLogVo> page(PageParam pageParam, SysLoginLogQueryDto queryDto);

    /**
     * 异步记录登录日志
     *
     * @param userId        用户ID
     * @param username      用户名
     * @param loginIp       登录IP
     * @param loginLocation 登录地点
     * @param browser       浏览器
     * @param os            操作系统
     * @param status        登录状态(SUCCESS/FAIL/LOGOUT)
     * @param failReason    失败原因(可为null)
     */
    void recordLoginLog(Long userId, String username, String loginIp, String loginLocation,
                        String browser, String os, String status, String failReason);

    /**
     * 查询用户最近一次成功登录日志
     *
     * @param userId 用户ID
     * @return 最近一次成功登录日志，不存在时返回null
     */
    SysLoginLog getLatestSuccessLoginLog(Long userId);
}
