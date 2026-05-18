package com.wms.warehouse.service;

import com.wms.warehouse.domain.dto.BinDto;
import com.wms.warehouse.domain.vo.BinVo;

import java.util.List;

/**
 * 库位服务接口
 * 提供库位CRUD、按存放柜查询、批量生成库位等功能
 */
public interface BinService {

    /**
     * 按存放柜ID查询库位列表
     *
     * @param cabinetId 存放柜ID
     * @return 库位VO列表
     */
    List<BinVo> listByCabinetId(Long cabinetId);

    /**
     * 新增库位
     *
     * @param dto 库位新增参数
     * @return 新增后的库位VO
     */
    BinVo create(BinDto dto);

    /**
     * 更新库位
     *
     * @param id  库位ID
     * @param dto 库位更新参数
     * @return 更新后的库位VO
     */
    BinVo update(Long id, BinDto dto);

    /**
     * 删除库位(逻辑删除)
     *
     * @param id 库位ID
     */
    void delete(Long id);

    /**
     * 批量生成库位
     * 根据存放柜的行列数自动生成所有库位
     *
     * @param cabinetId 存放柜ID
     * @param rows     行数
     * @param cols     列数
     * @return 生成的库位VO列表
     */
    List<BinVo> batchCreate(Long cabinetId, Integer rows, Integer cols);
}
