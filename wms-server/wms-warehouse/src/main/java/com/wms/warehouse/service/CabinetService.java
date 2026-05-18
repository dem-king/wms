package com.wms.warehouse.service;

import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.vo.CabinetVo;

import java.util.List;

/**
 * 存放柜服务接口
 * 提供存放柜CRUD、按区域查询、详情查询、位置更新等功能
 */
public interface CabinetService {

    /**
     * 按区域ID查询存放柜列表
     *
     * @param areaId 区域ID
     * @return 存放柜VO列表
     */
    List<CabinetVo> listByAreaId(Long areaId);

    /**
     * 存放柜详情(含物品数量)
     *
     * @param id 存放柜ID
     * @return 存放柜详情VO
     */
    CabinetVo getById(Long id);

    /**
     * 新增存放柜
     * 自动生成存放柜编码
     *
     * @param dto 存放柜新增参数
     * @return 新增后的存放柜VO
     */
    CabinetVo create(CabinetDto dto);

    /**
     * 更新存放柜
     *
     * @param id  存放柜ID
     * @param dto 存放柜更新参数
     * @return 更新后的存放柜VO
     */
    CabinetVo update(Long id, CabinetDto dto);

    /**
     * 删除存放柜(逻辑删除)
     *
     * @param id 存放柜ID
     */
    void delete(Long id);

    /**
     * 更新存放柜位置坐标
     *
     * @param id 存放柜ID
     * @param x  X坐标
     * @param y  Y坐标
     */
    void updatePosition(Long id, Integer x, Integer y);
}
