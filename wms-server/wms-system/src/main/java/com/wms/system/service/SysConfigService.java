package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysConfigDto;
import com.wms.system.domain.vo.SysConfigVo;

/**
 * 系统配置服务接口
 * 提供配置分页查询、按key查询、新增、更新、删除等功能
 */
public interface SysConfigService {

    /**
     * 分页查询配置列表
     *
     * @param pageParam   分页参数
     * @param configKey   配置键（模糊匹配，可为空）
     * @param configGroup 配置分组（精确匹配，可为空）
     * @return 分页结果
     */
    PageResult<SysConfigVo> page(PageParam pageParam, String configKey, String configGroup);

    /**
     * 根据配置键查询配置
     *
     * @param key 配置键
     * @return 配置VO
     */
    SysConfigVo getByKey(String key);

    /**
     * 新增配置
     *
     * @param dto 配置新增参数
     * @return 新增后的配置VO
     */
    SysConfigVo create(SysConfigDto dto);

    /**
     * 更新配置
     *
     * @param id  配置ID
     * @param dto 配置更新参数
     * @return 更新后的配置VO
     */
    SysConfigVo update(Long id, SysConfigDto dto);

    /**
     * 删除配置（逻辑删除）
     *
     * @param id 配置ID
     */
    void delete(Long id);
}
