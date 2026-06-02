package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DataScopeConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.dto.SysRoleDto;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRoleDept;
import com.wms.system.domain.entity.SysRoleMenu;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysRoleVo;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRoleDeptMapper;
import com.wms.system.mapper.SysRoleMenuMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysRoleServiceImpl 测试
 * 验证供下拉选择使用的全部角色查询只返回启用角色。
 */
@DisplayName("SysRoleServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SysRoleServiceImplTest {

    @BeforeAll
    static void initTableInfo() {
        Configuration configuration = new Configuration();
        MapperBuilderAssistant roleAssistant = new MapperBuilderAssistant(configuration, "");
        roleAssistant.setCurrentNamespace(SysRoleMapper.class.getName());
        TableInfoHelper.initTableInfo(roleAssistant, SysRole.class);
        MapperBuilderAssistant roleDeptAssistant = new MapperBuilderAssistant(configuration, "");
        roleDeptAssistant.setCurrentNamespace(SysRoleDeptMapper.class.getName());
        TableInfoHelper.initTableInfo(roleDeptAssistant, SysRoleDept.class);
        MapperBuilderAssistant rolePermissionAssistant = new MapperBuilderAssistant(configuration, "");
        rolePermissionAssistant.setCurrentNamespace(SysRolePermissionMapper.class.getName());
        TableInfoHelper.initTableInfo(rolePermissionAssistant, SysRolePermission.class);
        MapperBuilderAssistant userRoleAssistant = new MapperBuilderAssistant(configuration, "");
        userRoleAssistant.setCurrentNamespace(SysUserRoleMapper.class.getName());
        TableInfoHelper.initTableInfo(userRoleAssistant, SysUserRole.class);
    }

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    @Test
    @DisplayName("查询启用角色列表时应按启用状态过滤")
    void shouldFilterDisabledRolesWhenListEnabled() {
        SysRole enabledRole = new SysRole();
        enabledRole.setId(1L);
        enabledRole.setRoleName("管理员");
        enabledRole.setRoleCode("admin");
        enabledRole.setStatus(BizConstants.STATUS_ENABLED);
        enabledRole.setDelFlag(DelFlagConstants.NORMAL);
        when(sysRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(enabledRole));

        List<SysRoleVo> result = sysRoleService.listEnabled();

        ArgumentCaptor<LambdaQueryWrapper<SysRole>> wrapperCaptor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(sysRoleMapper).selectList(wrapperCaptor.capture());
        assertTrue(wrapperCaptor.getValue().getSqlSegment().contains("status"));
        assertEquals(1, result.size());
        assertEquals(BizConstants.STATUS_ENABLED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("查询自定义数据范围角色详情时应返回部门ID列表")
    void shouldReturnDeptIdsWhenGetCustomDataScopeRole() {
        SysRole role = new SysRole();
        role.setId(2L);
        role.setRoleName("仓库主管");
        role.setRoleCode("warehouse_manager");
        role.setDataScope(DataScopeConstants.SCOPE_CUSTOM);
        role.setStatus(BizConstants.STATUS_ENABLED);
        role.setDelFlag(DelFlagConstants.NORMAL);
        SysRoleDept first = new SysRoleDept();
        first.setRoleId(2L);
        first.setDeptId(10L);
        SysRoleDept second = new SysRoleDept();
        second.setRoleId(2L);
        second.setDeptId(11L);
        when(sysRoleMapper.selectById(2L)).thenReturn(role);
        when(sysRoleDeptMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(first, second));

        SysRoleVo result = sysRoleService.getById(2L);

        assertEquals(List.of(10L, 11L), result.getDeptIds());
    }

    @Test
    @DisplayName("分配角色权限时应差量同步并优先恢复已删除关联")
    void shouldSyncRolePermissionsDifferentially() {
        SysRolePermission keptPermission = new SysRolePermission();
        keptPermission.setId(101L);
        keptPermission.setRoleId(1L);
        keptPermission.setPermId(28L);
        keptPermission.setDelFlag(DelFlagConstants.NORMAL);

        SysRolePermission removedPermission = new SysRolePermission();
        removedPermission.setId(102L);
        removedPermission.setRoleId(1L);
        removedPermission.setPermId(29L);
        removedPermission.setDelFlag(DelFlagConstants.NORMAL);

        SysRolePermission restorablePermission = new SysRolePermission();
        restorablePermission.setId(103L);
        restorablePermission.setRoleId(1L);
        restorablePermission.setPermId(30L);
        restorablePermission.setDelFlag(DelFlagConstants.DELETED);

        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(sysRolePermissionMapper.selectAllByRoleId(1L))
                .thenReturn(List.of(keptPermission, removedPermission, restorablePermission));

        sysRoleService.assignRolePermissions(1L, List.of(28L, 30L, 30L, 31L));

        verify(sysRolePermissionMapper, times(1))
                .updateDelFlagById(eq(102L), eq(DelFlagConstants.DELETED), anyString());
        verify(sysRolePermissionMapper, times(1))
                .updateDelFlagById(eq(103L), eq(DelFlagConstants.NORMAL), anyString());
        verify(sysRolePermissionMapper, times(1)).insert(argThat(permission ->
                permission != null
                        && Long.valueOf(1L).equals(permission.getRoleId())
                        && Long.valueOf(31L).equals(permission.getPermId())));
    }

    @Test
    @DisplayName("分配角色菜单时应差量同步并优先恢复已删除关联")
    void shouldSyncRoleMenusDifferentially() {
        SysRoleMenu keptMenu = new SysRoleMenu();
        keptMenu.setId(201L);
        keptMenu.setRoleId(1L);
        keptMenu.setMenuId(100L);
        keptMenu.setDelFlag(DelFlagConstants.NORMAL);

        SysRoleMenu removedMenu = new SysRoleMenu();
        removedMenu.setId(202L);
        removedMenu.setRoleId(1L);
        removedMenu.setMenuId(101L);
        removedMenu.setDelFlag(DelFlagConstants.NORMAL);

        SysRoleMenu restorableMenu = new SysRoleMenu();
        restorableMenu.setId(203L);
        restorableMenu.setRoleId(1L);
        restorableMenu.setMenuId(102L);
        restorableMenu.setDelFlag(DelFlagConstants.DELETED);

        when(sysRoleMenuMapper.selectAllByRoleId(1L))
                .thenReturn(List.of(keptMenu, removedMenu, restorableMenu));

        sysRoleService.assignRoleMenus(1L, List.of(100L, 102L, 102L, 103L));

        verify(sysRoleMenuMapper, times(1))
                .updateDelFlagById(eq(202L), eq(DelFlagConstants.DELETED), anyString());
        verify(sysRoleMenuMapper, times(1))
                .updateDelFlagById(eq(203L), eq(DelFlagConstants.NORMAL), anyString());
        verify(sysRoleMenuMapper, times(1)).insert(argThat(menu ->
                menu != null
                        && Long.valueOf(1L).equals(menu.getRoleId())
                        && Long.valueOf(103L).equals(menu.getMenuId())));
    }

    @Test
    @DisplayName("保存角色自定义部门范围时应差量同步并优先恢复已删除关联")
    void shouldSyncRoleDeptScopeDifferentially() {
        SysRoleDept keptDept = new SysRoleDept();
        keptDept.setId(301L);
        keptDept.setRoleId(9L);
        keptDept.setDeptId(10L);
        keptDept.setDelFlag(DelFlagConstants.NORMAL);

        SysRoleDept removedDept = new SysRoleDept();
        removedDept.setId(302L);
        removedDept.setRoleId(9L);
        removedDept.setDeptId(11L);
        removedDept.setDelFlag(DelFlagConstants.NORMAL);

        SysRoleDept restorableDept = new SysRoleDept();
        restorableDept.setId(303L);
        restorableDept.setRoleId(9L);
        restorableDept.setDeptId(12L);
        restorableDept.setDelFlag(DelFlagConstants.DELETED);

        SysRoleDto dto = new SysRoleDto();
        dto.setDataScope(DataScopeConstants.SCOPE_CUSTOM);
        dto.setDeptIds(List.of(10L, 12L, 12L, 13L));

        when(sysRoleDeptMapper.selectAllByRoleId(9L))
                .thenReturn(List.of(keptDept, removedDept, restorableDept));

        sysRoleService.update(9L, buildRoleDtoForUpdate(dto));

        verify(sysRoleDeptMapper, times(1))
                .updateDelFlagById(eq(302L), eq(DelFlagConstants.DELETED), anyString());
        verify(sysRoleDeptMapper, times(1))
                .updateDelFlagById(eq(303L), eq(DelFlagConstants.NORMAL), anyString());
        verify(sysRoleDeptMapper, times(1)).insert(argThat(roleDept ->
                roleDept != null
                        && Long.valueOf(9L).equals(roleDept.getRoleId())
                        && Long.valueOf(13L).equals(roleDept.getDeptId())));
    }

    private SysRoleDto buildRoleDtoForUpdate(SysRoleDto dto) {
        SysRole role = new SysRole();
        role.setId(9L);
        role.setRoleName("测试角色");
        role.setRoleCode("test_role");
        role.setDataScope(DataScopeConstants.SCOPE_CUSTOM);
        role.setStatus(BizConstants.STATUS_ENABLED);
        role.setDelFlag(DelFlagConstants.NORMAL);
        when(sysRoleMapper.selectById(9L)).thenReturn(role);
        when(sysRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        dto.setRoleName("测试角色");
        dto.setRoleCode("test_role");
        dto.setStatus(BizConstants.STATUS_ENABLED);
        return dto;
    }
}
