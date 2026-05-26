package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.dto.SysConfigDto;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.domain.vo.SysConfigVo;
import com.wms.system.mapper.SysConfigMapper;
import com.wms.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 * 处理配置列表查询、按key查询、更新配置等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    /**
     * 查询所有配置项列表
     * 
     * @return 配置项VO列表
     */
    @Override
    public List<SysConfigVo> listAll() {
        List<SysConfig> configs = sysConfigMapper.selectList(
                new LambdaQueryWrapper<SysConfig>()
        );
        return configs.stream().map(this::toVo).collect(Collectors.toList());
    }

    /**
     * 根据配置键查询配置项
     * 
     * @param key 配置键
     * @return 配置项VO
     */
    @Override
    public SysConfigVo getByKey(String key) {
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config == null) {
            throw new BizException("配置项不存在: " + key);
        }
        return toVo(config);
    }

    /**
     * 更新配置项
     * 仅更新非空字段
     * 
     * @param id 配置项ID
     * @param dto 配置项更新参数
     * @return 更新后的配置项VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysConfigVo update(Long id, SysConfigDto dto) {
        SysConfig existing = sysConfigMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("配置项不存在");
        }
        // 仅更新非空字段
        if (dto.getConfigValue() != null) {
            existing.setConfigValue(dto.getConfigValue());
        }
        if (dto.getConfigName() != null) {
            existing.setConfigName(dto.getConfigName());
        }
        if (dto.getConfigGroup() != null) {
            existing.setConfigGroup(dto.getConfigGroup());
        }
        if (dto.getConfigDesc() != null) {
            existing.setConfigDesc(dto.getConfigDesc());
        }
        existing.setId(id);
        sysConfigMapper.updateById(existing);
        return toVo(existing);
    }

    /**
     * SysConfig实体转SysConfigVo
     *
     * @param config 系统配置实体
     * @return 系统配置VO
     */
    private SysConfigVo toVo(SysConfig config) {
        SysConfigVo vo = new SysConfigVo();
        vo.setId(config.getId());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setDescription(config.getConfigDesc());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }
}
