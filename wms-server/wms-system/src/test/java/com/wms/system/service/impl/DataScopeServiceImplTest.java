package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DataScopeConstants;
import com.wms.common.datascope.DataScopeCondition;
import com.wms.system.domain.entity.SysDepartment;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRoleDept;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysDepartmentMapper;
import com.wms.system.mapper.SysRoleDeptMapper;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 数据范围运行时服务测试。
 */
@DisplayName("DataScopeServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class DataScopeServiceImplTest {

    @BeforeAll
    static void initTableInfo() {
        Configuration configuration = new Configuration();
        initTableInfo(configuration, SysUserMapper.class.getName(), SysUser.class);
        initTableInfo(configuration, SysUserRoleMapper.class.getName(), SysUserRole.class);
        initTableInfo(configuration, SysRoleMapper.class.getName(), SysRole.class);
        initTableInfo(configuration, SysRoleDeptMapper.class.getName(), SysRoleDept.class);
        initTableInfo(configuration, SysDepartmentMapper.class.getName(), SysDepartment.class);
    }

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Mock
    private SysDepartmentMapper sysDepartmentMapper;

    @InjectMocks
    private DataScopeServiceImpl dataScopeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("任一角色拥有全部数据时最终范围为全部数据")
    void shouldReturnAllWhenAnyRoleHasAllDataScope() {
        setCurrentUser(7L);
        when(sysUserMapper.selectById(7L)).thenReturn(user(7L, 10L));
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(userRole(1L), userRole(2L)));
        when(sysRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                role(1L, DataScopeConstants.SCOPE_SELF),
                role(2L, DataScopeConstants.SCOPE_ALL)
        ));

        DataScopeCondition result = dataScopeService.getCurrentDataScope();

        assertTrue(result.isAllData());
    }

    @Test
    @DisplayName("自定义部门和仅本人角色应合并为部门集合加本人条件")
    void shouldMergeCustomDepartmentAndSelfScopes() {
        setCurrentUser(7L);
        when(sysUserMapper.selectById(7L)).thenReturn(user(7L, 10L));
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(userRole(1L), userRole(2L)));
        when(sysRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                role(1L, DataScopeConstants.SCOPE_CUSTOM),
                role(2L, DataScopeConstants.SCOPE_SELF)
        ));
        when(sysRoleDeptMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(roleDept(1L, 20L), roleDept(1L, 21L)));

        DataScopeCondition result = dataScopeService.getCurrentDataScope();

        assertFalse(result.isAllData());
        assertEquals(Set.of(20L, 21L), result.getDeptIds());
        assertEquals(7L, result.getUserId());
        assertTrue(result.isSelfScope());
    }

    @Test
    @DisplayName("本部门及以下范围应包含当前部门和所有子部门")
    void shouldIncludeCurrentAndChildDepartments() {
        setCurrentUser(7L);
        when(sysUserMapper.selectById(7L)).thenReturn(user(7L, 10L));
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(userRole(1L)));
        when(sysRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(role(1L, DataScopeConstants.SCOPE_DEPT_AND_CHILD)));
        when(sysDepartmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                dept(10L, 0L),
                dept(11L, 10L),
                dept(12L, 11L),
                dept(20L, 0L)
        ));

        DataScopeCondition result = dataScopeService.getCurrentDataScope();

        assertEquals(Set.of(10L, 11L, 12L), result.getDeptIds());
    }

    private static void initTableInfo(Configuration configuration, String namespace, Class<?> entityClass) {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        assistant.setCurrentNamespace(namespace);
        TableInfoHelper.initTableInfo(assistant, entityClass);
    }

    private void setCurrentUser(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId, null));
    }

    private SysUser user(Long id, Long deptId) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setDeptId(deptId);
        return user;
    }

    private SysUserRole userRole(Long roleId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(roleId);
        return userRole;
    }

    private SysRole role(Long id, Integer dataScope) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setDataScope(dataScope);
        role.setStatus(BizConstants.STATUS_ENABLED);
        return role;
    }

    private SysRoleDept roleDept(Long roleId, Long deptId) {
        SysRoleDept roleDept = new SysRoleDept();
        roleDept.setRoleId(roleId);
        roleDept.setDeptId(deptId);
        return roleDept;
    }

    private SysDepartment dept(Long id, Long parentId) {
        SysDepartment dept = new SysDepartment();
        dept.setId(id);
        dept.setParentId(parentId);
        return dept;
    }
}
