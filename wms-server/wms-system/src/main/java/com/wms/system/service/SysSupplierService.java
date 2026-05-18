package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysSupplierDto;
import com.wms.system.domain.vo.SysSupplierVo;

/**
 * 供应商服务接口
 * 提供供应商CRUD功能
 */
public interface SysSupplierService {

    /**
     * 分页查询供应商列表
     *
     * @param pageParam    分页参数
     * @param supplierName 供应商名称(模糊查询)
     * @param supplierCode 供应商编码(模糊查询)
     * @return 分页结果
     */
    PageResult<SysSupplierVo> page(PageParam pageParam, String supplierName, String supplierCode);

    /**
     * 根据ID获取供应商详情
     *
     * @param id 供应商ID
     * @return 供应商VO
     */
    SysSupplierVo getById(Long id);

    /**
     * 新增供应商
     *
     * @param dto 供应商新增参数
     * @return 新增后的供应商VO
     */
    SysSupplierVo create(SysSupplierDto dto);

    /**
     * 更新供应商
     *
     * @param id  供应商ID
     * @param dto 供应商更新参数
     * @return 更新后的供应商VO
     */
    SysSupplierVo update(Long id, SysSupplierDto dto);

    /**
     * 删除供应商(逻辑删除)
     *
     * @param id 供应商ID
     */
    void delete(Long id);
}
