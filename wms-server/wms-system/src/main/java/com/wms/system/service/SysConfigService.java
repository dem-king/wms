package com.wms.system.service;

import com.wms.system.domain.dto.SysConfigDto;
import com.wms.system.domain.vo.SysConfigVo;

import java.util.List;

/**
 * 系统配置服务接口
 * 提供配置列表查询、按key查询、更新配置等功能
 */
public interface SysConfigService {

    /**
     * 获取配置列表
     *
     * @return 配置VO列表
     */
    List<SysConfigVo> listAll();

    /**
     * 根据配置key获取配置值
     *
     * @param key 配置key
     * @return 配置VO
     */
    SysConfigVo getByKey(String key);

    /**
     * 更新配置
     *
     * @param id  配置ID
     * @param dto 配置更新参数
     * @return 更新后的配置VO
     */
    SysConfigVo update(Long id, SysConfigDto dto);
}
