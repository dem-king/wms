package com.wms.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.system.domain.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 查询角色的全部权限关联，包含已逻辑删除记录。
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    @Select("SELECT id, role_id, perm_id, del_flag, create_time, create_by, update_time, update_by " +
            "FROM sys_role_permission WHERE role_id = #{roleId}")
    List<SysRolePermission> selectAllByRoleId(@Param("roleId") Long roleId);

    /**
     * 按主键显式更新逻辑删除标记，避免 @TableLogic 字段在通用更新链路中被忽略。
     *
     * @param id       关联ID
     * @param delFlag  逻辑删除标记
     * @param updateBy 更新人
     * @return 更新行数
     */
    @Update("UPDATE sys_role_permission SET del_flag = #{delFlag}, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE id = #{id}")
    int updateDelFlagById(@Param("id") Long id,
                          @Param("delFlag") Integer delFlag,
                          @Param("updateBy") String updateBy);
}
