package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.vo.SysRoleVo;
import com.wms.system.mapper.SysRoleMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysRoleServiceImpl 测试
 * 验证供下拉选择使用的全部角色查询只返回启用角色。
 */
@DisplayName("SysRoleServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class SysRoleServiceImplTest {

    @BeforeAll
    static void initTableInfo() {
        Configuration configuration = new Configuration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        assistant.setCurrentNamespace(SysRoleMapper.class.getName());
        TableInfoHelper.initTableInfo(assistant, SysRole.class);
    }

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

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
}
