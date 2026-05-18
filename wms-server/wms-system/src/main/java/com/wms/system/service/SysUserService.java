package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysUserDto;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.vo.SysUserVo;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户CRUD、密码重置、状态切换、角色分配等功能
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 更新用户密码
     *
     * @param userId          用户ID
     * @param newPasswordHash 新密码哈希值
     * @return 是否更新成功
     */
    boolean updatePassword(Long userId, String newPasswordHash);

    /**
     * 分页查询用户列表
     *
     * @param pageParam 分页参数
     * @param username  用户名(模糊查询)
     * @param realName  真实姓名(模糊查询)
     * @param status    状态
     * @return 分页结果
     */
    PageResult<SysUserVo> page(PageParam pageParam, String username, String realName, Integer status);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户VO
     */
    SysUserVo getById(Long id);

    /**
     * 新增用户
     * 密码使用BCrypt加密存储，并关联角色
     *
     * @param dto 用户新增参数
     * @return 新增后的用户VO
     */
    SysUserVo create(SysUserDto dto);

    /**
     * 更新用户
     * 如果dto中包含roleIds则同步更新用户角色关联
     *
     * @param id  用户ID
     * @param dto 用户更新参数
     * @return 更新后的用户VO
     */
    SysUserVo update(Long id, SysUserDto dto);

    /**
     * 删除用户(逻辑删除)
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 重置用户密码为默认密码
     *
     * @param id 用户ID
     */
    void resetPwd(Long id);

    /**
     * 切换用户状态(启用/禁用)
     *
     * @param id 用户ID
     */
    void changeStatus(Long id);

    /**
     * 查询用户关联的角色ID列表
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoles(Long id);

    /**
     * 分配用户角色
     * 先删除旧关联再批量插入新关联
     *
     * @param id      用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long id, List<Long> roleIds);
}
