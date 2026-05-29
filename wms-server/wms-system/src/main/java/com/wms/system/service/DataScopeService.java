package com.wms.system.service;

import com.wms.common.datascope.DataScopeCondition;

/**
 * 数据范围服务。
 */
public interface DataScopeService {

    /**
     * 获取当前登录用户的数据范围条件。
     *
     * @return 数据范围条件
     */
    DataScopeCondition getCurrentDataScope();
}
