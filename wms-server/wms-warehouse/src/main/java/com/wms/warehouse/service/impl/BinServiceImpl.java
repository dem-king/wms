package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.BinDto;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.BinVo;
import com.wms.warehouse.converter.BinConverter;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.service.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
    private final BinConverter binConverter;

    /**
     * 按存放柜ID分页查询库位列表
     *
     * @param pageParam  分页参数
     * @param cabinetId 存放柜ID
     * @return 库位分页结果
     */
    @Override
    public PageResult<BinVo> page(PageParam pageParam, Long cabinetId) {
        LambdaQueryWrapper<WmsBin> wrapper = new LambdaQueryWrapper<WmsBin>()
                .eq(WmsBin::getCabinetId, cabinetId)
                .orderByAsc(WmsBin::getRowNum)
                .orderByAsc(WmsBin::getColNum);
        Page<WmsBin> page = wmsBinMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        Set<Long> cabinetIds = page.getRecords().stream()
                .map(WmsBin::getCabinetId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsCabinet> cabinetMap = cabinetIds.isEmpty()
                ? Map.of()
                : wmsCabinetMapper.selectBatchIds(cabinetIds).stream()
                        .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));

        PageResult<BinVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(bin -> binConverter.toVo(bin, cabinetMap)).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 按存放柜ID查询库位列表
     *
     * @param cabinetId 存放柜ID
     * @return 库位VO列表
     */
    @Override
    public List<BinVo> listByCabinetId(Long cabinetId) {
        LambdaQueryWrapper<WmsBin> wrapper = new LambdaQueryWrapper<WmsBin>()
                .eq(WmsBin::getCabinetId, cabinetId)
                .orderByAsc(WmsBin::getRowNum)
                .orderByAsc(WmsBin::getColNum);
        List<WmsBin> list = wmsBinMapper.selectList(wrapper);
        // 批量查询存放柜构建Map，避免N+1查询
        Set<Long> cabinetIds = list.stream()
                .map(WmsBin::getCabinetId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsCabinet> cabinetMap = cabinetIds.isEmpty()
                ? Map.of()
                : wmsCabinetMapper.selectBatchIds(cabinetIds).stream()
                        .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));
        return list.stream().map(bin -> binConverter.toVo(bin, cabinetMap)).collect(Collectors.toList());
    }

    /**
     * 新增库位
     * 自动生成库位编码，默认状态为启用、空闲
     *
     * @param dto 库位新增参数
     * @return 新增后的库位VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BinVo create(BinDto dto) {
        // 校验存放柜存在且启用
        WmsCabinet cabinet = wmsCabinetMapper.selectById(dto.getCabinetId());
        if (cabinet == null) {
            throw new BizException("存放柜不存在");
        }
        if (cabinet.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        WmsBin bin = new WmsBin();
        copyDtoToEntity(dto, bin);
        // 自动生成库位编码: 存放柜编码-行号-列号
        if (bin.getBinCode() == null || bin.getBinCode().isBlank()) {
            bin.setBinCode(generateBinCode(cabinet.getCabinetCode(), bin.getRowNum(), bin.getColNum()));
        }
        // 默认状态为启用
        if (bin.getBinStatus() == null) {
            bin.setBinStatus(BizConstants.STATUS_ENABLED);
        }
        // 默认为空闲
        if (bin.getIsOccupied() == null) {
            bin.setIsOccupied(WarehouseConstants.IS_OCCUPIED_NO);
        }
        wmsBinMapper.insert(bin);
        return binConverter.toVo(bin, Map.of());
    }

    /**
     * 更新库位
     *
     * @param id  库位ID
     * @param dto 库位更新参数
     * @return 更新后的库位VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BinVo update(Long id, BinDto dto) {
        WmsBin existing = wmsBinMapper.selectById(id);
        if (existing == null) {
            throw new BizException("库位不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库位已删除");
        }
        // 校验存放柜存在
        WmsCabinet cabinet = wmsCabinetMapper.selectById(dto.getCabinetId());
        if (cabinet == null) {
            throw new BizException("存放柜不存在");
        }
        if (cabinet.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsBinMapper.updateById(existing);
        return binConverter.toVo(existing, Map.of());
    }

    /**
     * 删除库位(逻辑删除)
     *
     * @param id 库位ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsBin existing = wmsBinMapper.selectById(id);
        if (existing == null) {
            throw new BizException("库位不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库位已删除");
        }
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsBinMapper, WmsBin.class, id);
    }

    /**
     * 批量生成库位
     * 按行列数批量创建库位，同步更新存放柜行列数
     *
     * @param cabinetId 存放柜ID
     * @param rows     行数
     * @param cols     列数
     * @return 新增的库位VO列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BinVo> batchCreate(Long cabinetId, Integer rows, Integer cols) {
        // 校验存放柜存在且启用
        WmsCabinet cabinet = wmsCabinetMapper.selectById(cabinetId);
        if (cabinet == null) {
            throw new BizException("存放柜不存在");
        }
        if (cabinet.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
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
                bin.setIsOccupied(WarehouseConstants.IS_OCCUPIED_NO);
                bin.setBinStatus(BizConstants.STATUS_ENABLED);
                binList.add(bin);
            }
        }
        // 批量插入所有库位，避免逐条insert
        Db.saveBatch(binList);
        for (WmsBin bin : binList) {
            result.add(binConverter.toVo(bin, Map.of()));
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
        entity.setCapacity(dto.getCapacity());
        entity.setUsedCapacity(dto.getUsedCapacity());
        entity.setBinStatus(dto.getBinStatus());
    }

}
