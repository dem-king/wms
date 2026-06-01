package com.wms.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.system.domain.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 角色-菜单关联Mapper接口
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 查询角色的全部菜单关联，包含已逻辑删除记录。
     *
     * @param roleId 角色ID
     * @return 角色菜单关联列表
     */
    @Select("SELECT id, role_id, menu_id, del_flag, create_time, create_by, update_time, update_by " +
            "FROM sys_role_menu WHERE role_id = #{roleId}")
    List<SysRoleMenu> selectAllByRoleId(@Param("roleId") Long roleId);

    /**
     * 按主键显式更新逻辑删除标记。
     *
     * @param id       关联ID
     * @param delFlag  逻辑删除标记
     * @param updateBy 更新人
     * @return 更新行数
     */
    @Update("UPDATE sys_role_menu SET del_flag = #{delFlag}, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE id = #{id}")
    int updateDelFlagById(@Param("id") Long id,
                          @Param("delFlag") Integer delFlag,
                          @Param("updateBy") String updateBy);
}
