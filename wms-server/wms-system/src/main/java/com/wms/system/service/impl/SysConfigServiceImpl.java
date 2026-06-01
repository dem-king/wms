package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.system.converter.SysConfigConverter;
import com.wms.system.domain.dto.SysConfigDto;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.domain.vo.SysConfigVo;
import com.wms.system.mapper.SysConfigMapper;
import com.wms.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务实现类
 * 处理配置分页查询、按key查询、新增、更新、删除等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;
    private final SysConfigConverter sysConfigConverter;

    /**
     * 分页查询配置列表
     * 支持按configKey模糊匹配、configGroup精确筛选
     *
     * @param pageParam   分页参数
     * @param configKey   配置键（模糊匹配，可为空）
     * @param configGroup 配置分组（精确匹配，可为空）
     * @return 分页结果
     */
    @Override
    public PageResult<SysConfigVo> page(PageParam pageParam, String configKey, String configGroup) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (configKey != null && !configKey.isBlank()) {
            wrapper.like(SysConfig::getConfigKey, configKey);
        }
        if (configGroup != null && !configGroup.isBlank()) {
            wrapper.eq(SysConfig::getConfigGroup, configGroup);
        }
        wrapper.orderByDesc(SysConfig::getCreateTime);

        Page<SysConfig> page = sysConfigMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<SysConfigVo> result = new PageResult<>();
        result.setRecords(sysConfigConverter.toVoList(page.getRecords()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
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
        return sysConfigConverter.toVo(config);
    }

    /**
     * 新增配置项
     * 校验configKey唯一性，设置默认分组
     *
     * @param dto 配置新增参数
     * @return 新增后的配置项VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysConfigVo create(SysConfigDto dto) {
        // 校验configKey唯一性
        Long count = sysConfigMapper.selectCount(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, dto.getConfigKey())
        );
        if (count > 0) {
            throw new BizException("配置键已存在: " + dto.getConfigKey());
        }

        SysConfig entity = new SysConfig();
        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigValue(dto.getConfigValue());
        entity.setConfigName(dto.getConfigName());
        // 默认分组为default
        entity.setConfigGroup(dto.getConfigGroup() != null ? dto.getConfigGroup() : "default");
        entity.setConfigDesc(dto.getConfigDesc());
        sysConfigMapper.insert(entity);
        return sysConfigConverter.toVo(entity);
    }

    /**
     * 更新配置项
     * 编辑时configKey不可修改
     *
     * @param id  配置项ID
     * @param dto 配置项更新参数
     * @return 更新后的配置项VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysConfigVo update(Long id, SysConfigDto dto) {
        SysConfig existing = sysConfigMapper.selectById(id);
        if (existing == null) {
            throw new BizException("配置项不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("配置项已删除");
        }
        // 编辑时不修改configKey
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
        return sysConfigConverter.toVo(existing);
    }

    /**
     * 逻辑删除配置项
     *
     * @param id 配置项ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysConfig existing = sysConfigMapper.selectById(id);
        if (existing == null) {
            throw new BizException("配置项不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("配置项已删除");
        }
        // 逻辑删除
        SysConfig updateEntity = new SysConfig();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        sysConfigMapper.updateById(updateEntity);
    }
}
