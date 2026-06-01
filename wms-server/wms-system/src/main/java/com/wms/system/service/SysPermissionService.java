package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysPermissionDto;
import com.wms.system.domain.vo.SysPermissionVo;

import java.util.List;

/**
 * 权限服务接口
 * 提供权限CRUD、用户权限编码查询等功能
 */
public interface SysPermissionService {

    /**
     * 根据用户ID获取权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermCodesByUserId(Long userId);

    /**
     * 鍒嗛〉鏌ヨ鏉冮檺鍒楄〃
     *
     * @param pageParam 鍒嗛〉鍙傛暟
     * @param permName 鏉冮檺鍚嶇О
     * @param permCode 鏉冮檺缂栫爜
     * @return 鏉冮檺鍒嗛〉缁撴灉
     */
    PageResult<SysPermissionVo> page(PageParam pageParam, String permName, String permCode);

    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    List<SysPermissionVo> listAll();

    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限VO
     */
    SysPermissionVo getById(Long id);

    /**
     * 新增权限
     *
     * @param dto 权限新增参数
     * @return 新增后的权限VO
     */
    SysPermissionVo create(SysPermissionDto dto);

    /**
     * 更新权限
     *
     * @param id  权限ID
     * @param dto 权限更新参数
     * @return 更新后的权限VO
     */
    SysPermissionVo update(Long id, SysPermissionDto dto);

    /**
     * 删除权限(逻辑删除)
     *
     * @param id 权限ID
     */
    void delete(Long id);
}
