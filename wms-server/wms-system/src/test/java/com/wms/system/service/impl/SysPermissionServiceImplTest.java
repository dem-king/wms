package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.event.PermissionCacheEvictEvent;
import com.wms.system.converter.SysPermissionConverter;
import com.wms.system.domain.entity.SysMenu;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysPermissionVo;
import com.wms.system.mapper.SysMenuMapper;
import com.wms.system.mapper.SysPermissionMapper;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysPermissionServiceImpl 测试
 */
@DisplayName("SysPermissionServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SysPermissionServiceImplTest {

    @BeforeAll
    static void initTableInfo() {
        Configuration configuration = new Configuration();
        MapperBuilderAssistant permissionAssistant = new MapperBuilderAssistant(configuration, "");
        permissionAssistant.setCurrentNamespace(SysPermissionMapper.class.getName());
        TableInfoHelper.initTableInfo(permissionAssistant, SysPermission.class);
    }

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Mock
    private SysMenuMapper sysMenuMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Spy
    private SysPermissionConverter sysPermissionConverter;

    @InjectMocks
    private SysPermissionServiceImpl sysPermissionService;

    @Test
    @DisplayName("分页查询权限时应按菜单ID批量回填菜单名称")
    void shouldPagePermissionsWithMenuName() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(20);
        SysPermission permission = new SysPermission();
        permission.setId(1L);
        permission.setPermName("权限查询");
        permission.setPermCode("system:perm:list");
        permission.setMenuId(1044L);
        permission.setStatus(1);
        Page<SysPermission> page = new Page<>(1, 20);
        page.setRecords(List.of(permission));
        page.setTotal(1L);
        SysMenu menu = new SysMenu();
        menu.setId(1044L);
        menu.setMenuName("权限查询");

        when(sysPermissionMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(sysMenuMapper.selectBatchIds(List.of(1044L))).thenReturn(List.of(menu));

        PageResult<SysPermissionVo> result = sysPermissionService.page(pageParam, "权限", "system:perm");

        assertEquals(1L, result.getTotal());
        assertEquals("权限查询", result.getRecords().get(0).getMenuName());
    }

    @Test
    @DisplayName("更新权限后应发布受影响用户的权限缓存失效事件")
    void shouldPublishCacheEvictEventWhenPermissionUpdated() {
        SysPermission permission = new SysPermission();
        permission.setId(8L);
        permission.setPermName("权限编辑");
        permission.setPermCode("system:perm:edit");
        permission.setMenuId(1042L);
        permission.setStatus(1);
        permission.setDelFlag(0);
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(2L);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(1001L);

        when(sysPermissionMapper.selectById(8L)).thenReturn(permission);
        when(sysPermissionMapper.selectCount(any())).thenReturn(0L);
        when(sysPermissionMapper.updateById(any(SysPermission.class))).thenReturn(1);
        when(sysRolePermissionMapper.selectList(any())).thenReturn(List.of(rolePermission));
        when(sysUserRoleMapper.selectList(any())).thenReturn(List.of(userRole));

        sysPermissionService.update(8L, dto());

        ArgumentCaptor<PermissionCacheEvictEvent> eventCaptor = ArgumentCaptor.forClass(PermissionCacheEvictEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(List.of(1001L), eventCaptor.getValue().userIds().stream().toList());
    }

    private com.wms.system.domain.dto.SysPermissionDto dto() {
        com.wms.system.domain.dto.SysPermissionDto dto = new com.wms.system.domain.dto.SysPermissionDto();
        dto.setPermName("权限编辑");
        dto.setPermCode("system:perm:edit");
        dto.setPermType(2);
        dto.setMenuId(1042L);
        dto.setStatus(1);
        return dto;
    }
}
