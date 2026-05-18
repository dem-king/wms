package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.exception.BizException;
import com.wms.warehouse.domain.dto.BinDto;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.BinVo;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.service.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库位服务实现类
 * 处理库位CRUD、按存放柜查询、批量生成库位等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class BinServiceImpl implements BinService {

    private final WmsBinMapper wmsBinMapper;
    private final WmsCabinetMapper wmsCabinetMapper;

    @Override
    public List<BinVo> listByCabinetId(Long cabinetId) {
        LambdaQueryWrapper<WmsBin> wrapper = new LambdaQueryWrapper<WmsBin>()
                .eq(WmsBin::getCabinetId, cabinetId)
                .orderByAsc(WmsBin::getRowNum)
                .orderByAsc(WmsBin::getColNum);
        List<WmsBin> list = wmsBinMapper.selectList(wrapper);
        return list.stream().map(this::toBinVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BinVo create(BinDto dto) {
        // 校验存放柜存在且启用
        WmsCabinet cabinet = wmsCabinetMapper.selectById(dto.getCabinetId());
        if (cabinet == null || cabinet.getDelFlag() == 1) {
            throw new BizException("存放柜不存在");
        }
        WmsBin bin = new WmsBin();
        copyDtoToEntity(dto, bin);
        // 自动生成库位编码: 存放柜编码-行号-列号
        if (bin.getBinCode() == null || bin.getBinCode().isBlank()) {
            bin.setBinCode(generateBinCode(cabinet.getCabinetCode(), bin.getRowNum(), bin.getColNum()));
        }
        // 默认状态为启用
        if (bin.getStatus() == null) {
            bin.setStatus(1);
        }
        // 默认为空闲
        if (bin.getIsOccupied() == null) {
            bin.setIsOccupied(0);
        }
        wmsBinMapper.insert(bin);
        return toBinVo(bin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BinVo update(Long id, BinDto dto) {
        WmsBin existing = wmsBinMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("库位不存在");
        }
        // 校验存放柜存在
        WmsCabinet cabinet = wmsCabinetMapper.selectById(dto.getCabinetId());
        if (cabinet == null || cabinet.getDelFlag() == 1) {
            throw new BizException("存放柜不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsBinMapper.updateById(existing);
        return toBinVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsBin existing = wmsBinMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("库位不存在");
        }
        // 逻辑删除库位
        WmsBin updateEntity = new WmsBin();
        updateEntity.setId(id);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsBinMapper.updateById(updateEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BinVo> batchCreate(Long cabinetId, Integer rows, Integer cols) {
        // 校验存放柜存在且启用
        WmsCabinet cabinet = wmsCabinetMapper.selectById(cabinetId);
        if (cabinet == null || cabinet.getDelFlag() == 1) {
            throw new BizException("存放柜不存在");
        }
        if (rows == null || rows <= 0 || cols == null || cols <= 0) {
            throw new BizException("行列数必须大于0");
        }
        // 检查是否已有库位，避免重复生成
        Long existCount = wmsBinMapper.selectCount(
                new LambdaQueryWrapper<WmsBin>()
                        .eq(WmsBin::getCabinetId, cabinetId)
        );
        if (existCount > 0) {
            throw new BizException("该存放柜已存在库位，请先删除后再批量生成");
        }

        List<BinVo> result = new ArrayList<>();
        List<WmsBin> binList = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                WmsBin bin = new WmsBin();
                bin.setCabinetId(cabinetId);
                bin.setWarehouseId(cabinet.getWarehouseId());
                bin.setBinCode(generateBinCode(cabinet.getCabinetCode(), row, col));
                bin.setRowNum(row);
                bin.setColNum(col);
                bin.setIsOccupied(0);
                bin.setStatus(1);
                binList.add(bin);
            }
        }
        // 批量插入所有库位，避免逐条insert
        Db.saveBatch(binList);
        for (WmsBin bin : binList) {
            result.add(toBinVo(bin));
        }
        // 同步更新存放柜行列数
        WmsCabinet updateCabinet = new WmsCabinet();
        updateCabinet.setId(cabinetId);
        updateCabinet.setRows(rows);
        updateCabinet.setCols(cols);
        wmsCabinetMapper.updateById(updateCabinet);

        return result;
    }

    /**
     * 生成库位编码: 存放柜编码-行号-列号
     * 示例: CG202605140001-01-03
     */
    private String generateBinCode(String cabinetCode, Integer rowNum, Integer colNum) {
        String cabinetPart = (cabinetCode != null) ? cabinetCode : "BIN";
        return cabinetPart + "-" + String.format("%02d", rowNum) + "-" + String.format("%02d", colNum);
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(BinDto dto, WmsBin entity) {
        entity.setCabinetId(dto.getCabinetId());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setBinCode(dto.getBinCode());
        entity.setRowNum(dto.getRowNum());
        entity.setColNum(dto.getColNum());
        entity.setIsOccupied(dto.getIsOccupied());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * WmsBin实体转BinVo(填充存放柜名称和占用状态描述)
     */
    private BinVo toBinVo(WmsBin bin) {
        BinVo vo = new BinVo();
        vo.setId(bin.getId());
        vo.setCabinetId(bin.getCabinetId());
        vo.setWarehouseId(bin.getWarehouseId());
        vo.setBinCode(bin.getBinCode());
        vo.setRowNum(bin.getRowNum());
        vo.setColNum(bin.getColNum());
        vo.setIsOccupied(bin.getIsOccupied());
        // 占用状态描述
        vo.setOccupiedDesc(bin.getIsOccupied() != null && bin.getIsOccupied() == 1 ? "占用" : "空闲");
        vo.setStatus(bin.getStatus());
        vo.setRemark(bin.getRemark());
        vo.setCreateTime(bin.getCreateTime());
        // 填充存放柜名称
        if (bin.getCabinetId() != null) {
            WmsCabinet cabinet = wmsCabinetMapper.selectById(bin.getCabinetId());
            if (cabinet != null) {
                vo.setCabinetName(cabinet.getCabinetName());
            }
        }
        return vo;
    }
}
