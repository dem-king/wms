package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.converter.MachineSpareConverter;
import com.wms.item.domain.dto.MachineSpareDto;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsMachineSpare;
import com.wms.item.domain.vo.MachineSpareVo;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsMachineSpareMapper;
import com.wms.item.service.MachineSpareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器-备件关联服务实现类
 * 处理机器-备件关联的CRUD业务逻辑
 */
@Service
@RequiredArgsConstructor
public class MachineSpareServiceImpl implements MachineSpareService {

    private final WmsMachineSpareMapper wmsMachineSpareMapper;
    private final WmsItemMapper wmsItemMapper;
    private final MachineSpareConverter machineSpareConverter;

    /**
     * 分页查询机器-备件关联
     *
     * @param pageParam 分页参数
     * @param machineName 机器名称(可选，模糊匹配)
     * @param spareItemId 备件物品ID(可选)
     * @return 分页结果
     */
    @Override
    public PageResult<MachineSpareVo> pageList(PageParam pageParam, String machineName, Long spareItemId) {
        LambdaQueryWrapper<WmsMachineSpare> wrapper = new LambdaQueryWrapper<>();
        if (machineName != null && !machineName.isBlank()) {
            wrapper.like(WmsMachineSpare::getMachineName, machineName);
        }
        if (spareItemId != null) {
            wrapper.eq(WmsMachineSpare::getSpareItemId, spareItemId);
        }
        wrapper.orderByDesc(WmsMachineSpare::getCreateTime);

        Page<WmsMachineSpare> page = wmsMachineSpareMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<MachineSpareVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream()
                .map(machineSpareConverter::toVo)
                .collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID获取机器-备件关联详情
     *
     * @param id 主键
     * @return 机器-备件关联VO
     */
    @Override
    public MachineSpareVo getById(Long id) {
        WmsMachineSpare entity = wmsMachineSpareMapper.selectById(id);
        if (entity == null || entity.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("机器-备件关联不存在");
        }
        return machineSpareConverter.toVo(entity);
    }

    /**
     * 新增机器-备件关联
     * 校验备件物品存在后插入
     *
     * @param dto 新增参数
     * @return 新增后的VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MachineSpareVo create(MachineSpareDto dto) {
        // 校验备件物品存在
        WmsItem item = wmsItemMapper.selectById(dto.getSpareItemId());
        if (item == null || item.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("备件物品不存在: " + dto.getSpareItemId());
        }

        WmsMachineSpare entity = new WmsMachineSpare();
        entity.setMachineName(dto.getMachineName());
        entity.setMachineCode(dto.getMachineCode());
        entity.setSpareItemId(dto.getSpareItemId());
        entity.setQuantity(dto.getQuantity());
        entity.setRemark(dto.getRemark());
        wmsMachineSpareMapper.insert(entity);
        return machineSpareConverter.toVo(entity);
    }

    /**
     * 更新机器-备件关联
     * 校验存在性和备件物品后更新
     *
     * @param id 主键
     * @param dto 更新参数
     * @return 更新后的VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MachineSpareVo update(Long id, MachineSpareDto dto) {
        WmsMachineSpare entity = wmsMachineSpareMapper.selectById(id);
        if (entity == null || entity.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("机器-备件关联不存在");
        }
        // 校验备件物品存在
        WmsItem item = wmsItemMapper.selectById(dto.getSpareItemId());
        if (item == null || item.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("备件物品不存在: " + dto.getSpareItemId());
        }

        entity.setMachineName(dto.getMachineName());
        entity.setMachineCode(dto.getMachineCode());
        entity.setSpareItemId(dto.getSpareItemId());
        entity.setQuantity(dto.getQuantity());
        entity.setRemark(dto.getRemark());
        wmsMachineSpareMapper.updateById(entity);
        return machineSpareConverter.toVo(entity);
    }

    /**
     * 删除机器-备件关联(逻辑删除)
     *
     * @param id 主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsMachineSpare entity = wmsMachineSpareMapper.selectById(id);
        if (entity == null || entity.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("机器-备件关联不存在");
        }
        // 逻辑删除
        WmsMachineSpare updateEntity = new WmsMachineSpare();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        wmsMachineSpareMapper.updateById(updateEntity);
    }

    /**
     * 按机器编号查询备件列表
     *
     * @param machineCode 机器编号
     * @return 备件关联VO列表
     */
    @Override
    public List<MachineSpareVo> listByMachineCode(String machineCode) {
        List<WmsMachineSpare> list = wmsMachineSpareMapper.selectList(
                new LambdaQueryWrapper<WmsMachineSpare>()
                        .eq(WmsMachineSpare::getMachineCode, machineCode)
                        .orderByDesc(WmsMachineSpare::getCreateTime));
        return list.stream().map(machineSpareConverter::toVo).collect(Collectors.toList());
    }
}
