package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.dto.SysUserDto;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.manager.SysConfigManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysUserServiceImpl 配置约束测试
 * 验证默认密码只从配置键读取，不在 Java 注解中硬编码默认值。
 */
@DisplayName("SysUserServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private SysConfigManager configManager;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @Test
    @DisplayName("默认密码配置应只引用属性键")
    void shouldReadDefaultPasswordWithoutInlineFallback() throws NoSuchFieldException {
        Field field = SysUserServiceImpl.class.getDeclaredField("defaultPassword");
        Value value = field.getAnnotation(Value.class);

        assertEquals("${wms.default-password}", value.value());
    }

    @Test
    @DisplayName("更新用户且角色未变化时不应重复插入用户角色关联")
    void shouldSkipRoleReinsertWhenAssignedRolesUnchanged() {
        Long userId = 1001L;
        Long roleId = 2L;
        SysUser existingUser = buildUser(userId, "zhangsan");
        SysUserRole existingRole = buildUserRole(2001L, userId, roleId);
        SysUserDto dto = buildUpdateDto("zhangsan", List.of(roleId));

        when(sysUserMapper.selectById(userId)).thenReturn(existingUser);
        when(sysUserMapper.selectCount(any())).thenReturn(0L);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(sysUserRoleMapper.selectList(any())).thenReturn(List.of(existingRole));
        when(sysRoleMapper.selectBatchIds(any())).thenReturn(List.of(role(roleId, "role-2")));

        SysUserVo result = sysUserService.update(userId, dto);

        assertEquals(userId, result.getId());
        assertEquals("张三", result.getRealName());
        verify(sysUserRoleMapper, never()).updateById(any(SysUserRole.class));
        verify(sysUserRoleMapper, never()).insert(any(SysUserRole.class));
    }

    @Test
    @DisplayName("更新用户且角色变化时应只逻辑删除旧角色并新增新角色")
    void shouldReplaceRemovedRolesAndInsertAddedRoles() {
        Long userId = 1002L;
        SysUser existingUser = buildUser(userId, "lisi");
        SysUserRole oldRole = buildUserRole(2002L, userId, 2L);
        SysUserDto dto = buildUpdateDto("lisi", List.of(3L));

        when(sysUserMapper.selectById(userId)).thenReturn(existingUser);
        when(sysUserMapper.selectCount(any())).thenReturn(0L);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(sysUserRoleMapper.selectList(any())).thenReturn(List.of(oldRole));
        when(sysUserRoleMapper.update(any(SysUserRole.class), any(UpdateWrapper.class))).thenReturn(1);
        when(sysUserRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);
        when(sysRoleMapper.selectBatchIds(any())).thenReturn(List.of(role(2L, "role-2"), role(3L, "role-3")));

        sysUserService.update(userId, dto);

        ArgumentCaptor<UpdateWrapper<SysUserRole>> wrapperCaptor = ArgumentCaptor.forClass(UpdateWrapper.class);
        verify(sysUserRoleMapper).update(any(SysUserRole.class), wrapperCaptor.capture());
        assertEquals(DelFlagConstants.DELETED, wrapperCaptor.getValue().getParamNameValuePairs().get("MPGENVAL1"));
        verify(sysUserRoleMapper).insert(any(SysUserRole.class));
    }

    @Test
    @DisplayName("delete user should explicitly set del_flag with update wrapper")
    void shouldSetDelFlagWithUpdateWrapperWhenDeletingUser() {
        Long userId = 1003L;
        SysUser existingUser = buildUser(userId, "wangwu");
        when(sysUserMapper.selectById(userId)).thenReturn(existingUser);

        sysUserService.delete(userId);

        ArgumentCaptor<SysUser> entityCaptor = ArgumentCaptor.forClass(SysUser.class);
        ArgumentCaptor<UpdateWrapper<SysUser>> wrapperCaptor = ArgumentCaptor.forClass(UpdateWrapper.class);
        verify(sysUserMapper).update(entityCaptor.capture(), wrapperCaptor.capture());
        verify(sysUserMapper, never()).updateById(any(SysUser.class));
        assertEquals(userId, entityCaptor.getValue().getId());
        assertEquals(DelFlagConstants.DELETED, wrapperCaptor.getValue().getParamNameValuePairs().get("MPGENVAL1"));
    }

    private SysUser buildUser(Long userId, String username) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setUsername(username);
        user.setRealName("张三");
        user.setDeptId(3001L);
        user.setPhone("13900139000");
        user.setEmail("demo@wms.com");
        user.setStatus(BizConstants.STATUS_ENABLED);
        user.setDelFlag(DelFlagConstants.NORMAL);
        return user;
    }

    private SysUserRole buildUserRole(Long id, Long userId, Long roleId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setId(id);
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setDelFlag(DelFlagConstants.NORMAL);
        return userRole;
    }

    private SysRole role(Long id, String code) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(code);
        role.setStatus(BizConstants.STATUS_ENABLED);
        role.setDelFlag(DelFlagConstants.NORMAL);
        return role;
    }

    private SysUserDto buildUpdateDto(String username, List<Long> roleIds) {
        SysUserDto dto = new SysUserDto();
        dto.setUsername(username);
        dto.setRealName("张三");
        dto.setDeptId(3001L);
        dto.setPhone("13900139000");
        dto.setEmail("demo@wms.com");
        dto.setStatus(BizConstants.STATUS_ENABLED);
        dto.setRoleIds(roleIds);
        return dto;
    }
}
