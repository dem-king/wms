package com.wms.system.converter;

import com.wms.system.domain.entity.SysConfig;
import com.wms.system.domain.vo.SysConfigVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置转换器
 * SysConfig实体与SysConfigVo之间的转换逻辑
 */
@Component
public class SysConfigConverter {

    /**
     * SysConfig实体转SysConfigVo
     *
     * @param entity 系统配置实体
     * @return 系统配置VO
     */
    public SysConfigVo toVo(SysConfig entity) {
        SysConfigVo vo = new SysConfigVo();
        vo.setId(entity.getId());
        vo.setConfigKey(entity.getConfigKey());
        vo.setConfigValue(entity.getConfigValue());
        vo.setConfigName(entity.getConfigName());
        vo.setConfigGroup(entity.getConfigGroup());
        vo.setConfigDesc(entity.getConfigDesc());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 批量转换系统配置实体列表
     *
     * @param entities 系统配置实体列表
     * @return 系统配置VO列表
     */
    public List<SysConfigVo> toVoList(List<SysConfig> entities) {
        return entities.stream().map(this::toVo).collect(Collectors.toList());
    }
}