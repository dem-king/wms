package com.wms.warehouse.service;

import com.wms.warehouse.domain.dto.LayoutElementBatchSaveDto;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.vo.LayoutElementBatchSaveVo;
import com.wms.warehouse.domain.vo.LayoutElementVo;

import java.util.List;

/**
 * 库房布局元素服务接口
 * 提供布局元素CRUD、按库房查询、批量保存等功能
 */
public interface LayoutElementService {

    /**
     * 按库房ID查询布局元素列表
     * 可选按区域ID筛选
     *
     * @param warehouseId 库房ID
     * @param areaId     区域ID（可选，为null时不筛选）
     * @return 布局元素VO列表
     */
    List<LayoutElementVo> listByWarehouseId(Long warehouseId, Long areaId);

    /**
     * 新增布局元素
     * 校验库房存在且启用，自动生成元素编码
     *
     * @param dto 布局元素创建参数
     * @return 新增后的布局元素VO
     */
    LayoutElementVo create(LayoutElementDto dto);

    /**
     * 更新布局元素
     *
     * @param id  布局元素ID
     * @param dto 布局元素更新参数
     * @return 更新后的布局元素VO
     */
    LayoutElementVo update(Long id, LayoutElementDto dto);

    /**
     * 删除布局元素(逻辑删除)
     *
     * @param id 布局元素ID
     */
    void delete(Long id);

    /**
     * 批量保存布局元素
     * 处理新增、更新、删除操作，统计成功和失败数量
     *
     * @param dto 批量保存参数
     * @return 批量保存结果
     */
    LayoutElementBatchSaveVo batchSave(LayoutElementBatchSaveDto dto);
}