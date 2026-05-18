package com.wms.warehouse.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.warehouse.domain.dto.WarehouseDto;
import com.wms.warehouse.domain.vo.WarehouseVo;

import java.util.List;

/**
 * 库房服务接口
 * 提供库房CRUD、分页查询、列表查询等功能
 */
public interface WarehouseService {

    /**
     * 查询所有库房列表
     *
     * @return 库房VO列表
     */
    List<WarehouseVo> listAll();

    /**
     * 库房分页列表
     * 支持按状态、关键字筛选
     *
     * @param pageParam 分页参数
     * @param status    状态(可选)
     * @param keyword   搜索关键字(可选，匹配名称/编码)
     * @return 分页结果
     */
    PageResult<WarehouseVo> page(PageParam pageParam, Integer status, String keyword);

    /**
     * 新增库房
     * 自动生成库房编码
     *
     * @param dto 库房新增参数
     * @return 新增后的库房VO
     */
    WarehouseVo create(WarehouseDto dto);

    /**
     * 更新库房
     *
     * @param id  库房ID
     * @param dto 库房更新参数
     * @return 更新后的库房VO
     */
    WarehouseVo update(Long id, WarehouseDto dto);

    /**
     * 删除库房(逻辑删除)
     *
     * @param id 库房ID
     */
    void delete(Long id);
}
