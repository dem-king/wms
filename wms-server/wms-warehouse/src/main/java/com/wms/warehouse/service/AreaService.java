package com.wms.warehouse.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.warehouse.domain.dto.AreaDto;
import com.wms.warehouse.domain.dto.AreaLayoutItemDto;
import com.wms.warehouse.domain.vo.AreaVo;

import java.util.List;

/**
 * 存放区域服务接口
 * 提供区域CRUD、按库房查询等功能
 */
public interface AreaService {

    /**
     * 按库房ID分页查询区域列表
     *
     * @param pageParam    分页参数
     * @param warehouseId 库房ID
     * @return 区域分页结果
     */
    PageResult<AreaVo> page(PageParam pageParam, Long warehouseId);

    /**
     * 按库房ID查询区域列表
     *
     * @param warehouseId 库房ID
     * @return 区域VO列表
     */
    List<AreaVo> listByWarehouseId(Long warehouseId);

    /**
     * 新增区域
     * 自动生成区域编码
     *
     * @param dto 区域新增参数
     * @return 新增后的区域VO
     */
    AreaVo create(AreaDto dto);

    /**
     * 更新区域
     *
     * @param id  区域ID
     * @param dto 区域更新参数
     * @return 更新后的区域VO
     */
    AreaVo update(Long id, AreaDto dto);

    /**
     * 删除区域(逻辑删除)
     *
     * @param id 区域ID
     */
    void delete(Long id);

    /**
     * 批量更新区域坐标
     * 更新区域的coordX/coordY字段，用于画布自由定位
     *
     * @param items 区域坐标更新项列表
     * @return 更新成功的数量
     */
    int updateLayoutCoordinates(List<AreaLayoutItemDto> items);
}
