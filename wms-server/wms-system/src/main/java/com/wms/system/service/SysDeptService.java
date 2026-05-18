package com.wms.system.service;

import com.wms.system.domain.dto.SysDeptDto;
import com.wms.system.domain.vo.SysDeptVo;

import java.util.List;

/**
 * 部门服务接口
 * 提供部门CRUD、部门树构建等功能
 */
public interface SysDeptService {

    /**
     * 获取部门列表(树形)
     *
     * @return 部门树
     */
    List<SysDeptVo> listTree();

    /**
     * 获取部门树(用于下拉选择)
     *
     * @return 部门树
     */
    List<SysDeptVo> tree();

    /**
     * 新增部门
     *
     * @param dto 部门新增参数
     * @return 新增后的部门VO
     */
    SysDeptVo create(SysDeptDto dto);

    /**
     * 更新部门
     *
     * @param id  部门ID
     * @param dto 部门更新参数
     * @return 更新后的部门VO
     */
    SysDeptVo update(Long id, SysDeptDto dto);

    /**
     * 删除部门(逻辑删除)
     * 校验是否存在子部门
     *
     * @param id 部门ID
     */
    void delete(Long id);
}
