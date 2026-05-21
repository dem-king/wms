package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.WarehouseDto;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.WarehouseVo;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房服务实现类
 * 处理库房CRUD、分页查询、列表查询等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SequenceGenerator sequenceGenerator;

    @Override
    public List<WarehouseVo> listAll() {
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>()
                .orderByDesc(WmsWarehouse::getCreateTime);
        List<WmsWarehouse> list = wmsWarehouseMapper.selectList(wrapper);
        return list.stream().map(this::toWarehouseVo).collect(Collectors.toList());
    }

    @Override
    public PageResult<WarehouseVo> page(PageParam pageParam, Integer status, String keyword) {
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsWarehouse::getStatus, status);
        }
        // 按关键字模糊搜索(名称/编码)
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(WmsWarehouse::getWarehouseName, keyword)
                    .or().like(WmsWarehouse::getWarehouseCode, keyword)
            );
        }
        wrapper.orderByDesc(WmsWarehouse::getCreateTime);

        Page<WmsWarehouse> page = wmsWarehouseMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<WarehouseVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toWarehouseVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseVo create(WarehouseDto dto) {
        WmsWarehouse warehouse = new WmsWarehouse();
        copyDtoToEntity(dto, warehouse);
        // 自动生成库房编码: KF + 年月日 + 4位流水号
        warehouse.setWarehouseCode(generateWarehouseCode());
        // 默认状态为启用
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(BizConstants.STATUS_ENABLED);
        }
        wmsWarehouseMapper.insert(warehouse);
        return toWarehouseVo(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseVo update(Long id, WarehouseDto dto) {
        WmsWarehouse existing = wmsWarehouseMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setWarehouseCode(null);
        wmsWarehouseMapper.updateById(existing);
        return toWarehouseVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsWarehouse existing = wmsWarehouseMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房不存在");
        }
        // 逻辑删除库房
        WmsWarehouse updateEntity = new WmsWarehouse();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsWarehouseMapper.updateById(updateEntity);
    }

    /**
     * 生成库房编码: KF + 年月日 + 4位流水号
     * 基于Redis INCR原子操作保证并发安全
     * 示例: KF202605140001
     */
    private String generateWarehouseCode() {
        return sequenceGenerator.next(WarehouseConstants.WAREHOUSE_CODE_PREFIX);
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(WarehouseDto dto, WmsWarehouse entity) {
        entity.setWarehouseName(dto.getWarehouseName());
        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setAddress(dto.getAddress());
        entity.setManager(dto.getManager());
        entity.setPhone(dto.getPhone());
        entity.setArea(dto.getArea());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * WmsWarehouse实体转WarehouseVo
     */
    private WarehouseVo toWarehouseVo(WmsWarehouse warehouse) {
        WarehouseVo vo = new WarehouseVo();
        vo.setId(warehouse.getId());
        vo.setWarehouseName(warehouse.getWarehouseName());
        vo.setWarehouseCode(warehouse.getWarehouseCode());
        vo.setAddress(warehouse.getAddress());
        vo.setManager(warehouse.getManager());
        vo.setPhone(warehouse.getPhone());
        vo.setArea(warehouse.getArea());
        vo.setStatus(warehouse.getStatus());
        vo.setRemark(warehouse.getRemark());
        vo.setCreateTime(warehouse.getCreateTime());
        vo.setUpdateTime(warehouse.getUpdateTime());
        return vo;
    }
}
