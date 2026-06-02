package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.system.domain.dto.SysSupplierDto;
import com.wms.system.domain.entity.SysSupplier;
import com.wms.system.domain.vo.SysSupplierVo;
import com.wms.system.mapper.SysSupplierMapper;
import com.wms.system.service.SysSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商服务实现类
 * 处理供应商CRUD业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysSupplierServiceImpl implements SysSupplierService {

    private final SysSupplierMapper sysSupplierMapper;

    /**
     * 分页查询供应商
     * 
     * @param pageParam 分页参数
     * @param supplierName 供应商名称(可选，模糊匹配)
     * @param supplierCode 供应商编码(可选，模糊匹配)
     * @return 供应商分页结果
     */
    @Override
    public PageResult<SysSupplierVo> page(PageParam pageParam, String supplierName, String supplierCode) {
        LambdaQueryWrapper<SysSupplier> wrapper = new LambdaQueryWrapper<SysSupplier>();
        // 模糊查询供应商名称
        if (supplierName != null && !supplierName.isBlank()) {
            wrapper.like(SysSupplier::getSupplierName, supplierName);
        }
        // 模糊查询供应商编码
        if (supplierCode != null && !supplierCode.isBlank()) {
            wrapper.like(SysSupplier::getSupplierCode, supplierCode);
        }
        wrapper.orderByDesc(SysSupplier::getCreateTime);

        Page<SysSupplier> page = sysSupplierMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<SysSupplierVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询供应商详情
     * 
     * @param id 供应商ID
     * @return 供应商VO
     */
    @Override
    public SysSupplierVo getById(Long id) {
        SysSupplier supplier = sysSupplierMapper.selectById(id);
        if (supplier == null || supplier.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("供应商不存在");
        }
        return toVo(supplier);
    }

    /**
     * 新增供应商
     * 校验编码唯一性
     * 
     * @param dto 供应商新增参数
     * @return 新增后的供应商VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysSupplierVo create(SysSupplierDto dto) {
        // 校验供应商编码唯一性
        checkSupplierCodeUnique(dto.getSupplierCode(), null);
        SysSupplier supplier = new SysSupplier();
        copyDtoToEntity(dto, supplier);
        sysSupplierMapper.insert(supplier);
        return toVo(supplier);
    }

    /**
     * 更新供应商
     * 
     * @param id 供应商ID
     * @param dto 供应商更新参数
     * @return 更新后的供应商VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysSupplierVo update(Long id, SysSupplierDto dto) {
        SysSupplier existing = sysSupplierMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("供应商不存在");
        }
        // 校验供应商编码唯一性(排除自身)
        checkSupplierCodeUnique(dto.getSupplierCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysSupplierMapper.updateById(existing);
        return toVo(existing);
    }

    /**
     * 删除供应商(逻辑删除)
     * 
     * @param id 供应商ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysSupplier existing = sysSupplierMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("供应商不存在");
        }
        // 逻辑删除供应商
        LogicDeleteHelper.markDeleted(sysSupplierMapper, SysSupplier.class, id);
    }

    /**
     * 校验供应商编码唯一性
     *
     * @param supplierCode 供应商编码
     * @param excludeId    排除的供应商ID(更新时排除自身)
     */
    private void checkSupplierCodeUnique(String supplierCode, Long excludeId) {
        LambdaQueryWrapper<SysSupplier> wrapper = new LambdaQueryWrapper<SysSupplier>()
                .eq(SysSupplier::getSupplierCode, supplierCode);
        if (excludeId != null) {
            wrapper.ne(SysSupplier::getId, excludeId);
        }
        Long count = sysSupplierMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("供应商编码已存在: " + supplierCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysSupplierDto dto, SysSupplier entity) {
        entity.setSupplierName(dto.getSupplierName());
        entity.setSupplierCode(dto.getSupplierCode());
        entity.setContactPerson(dto.getContactPerson());
        entity.setContactPhone(dto.getContactPhone());
        entity.setAddress(dto.getAddress());
        entity.setAnnualAmount(dto.getAnnualAmount());
        entity.setRemark(dto.getRemark());
    }

    /**
     * SysSupplier实体转SysSupplierVo
     */
    private SysSupplierVo toVo(SysSupplier supplier) {
        SysSupplierVo vo = new SysSupplierVo();
        vo.setId(supplier.getId());
        vo.setSupplierName(supplier.getSupplierName());
        vo.setSupplierCode(supplier.getSupplierCode());
        vo.setContactPerson(supplier.getContactPerson());
        vo.setContactPhone(supplier.getContactPhone());
        vo.setAddress(supplier.getAddress());
        vo.setAnnualAmount(supplier.getAnnualAmount());
        vo.setRemark(supplier.getRemark());
        vo.setCreateTime(supplier.getCreateTime());
        return vo;
    }
}
