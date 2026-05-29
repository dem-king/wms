package com.wms.system.controller;

import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.system.domain.vo.SysRoleVo;
import com.wms.system.service.SysRoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SysRoleController 单元测试
 * 验证角色列表静态路由不会被角色详情动态路由误匹配。
 */
@DisplayName("SysRoleController 测试")
@ExtendWith(MockitoExtension.class)
class SysRoleControllerTest {

    @Mock
    private SysRoleService sysRoleService;

    @InjectMocks
    private SysRoleController sysRoleController;

    @Test
    @DisplayName("查询全部角色时应命中静态 all 路由且只查询启用角色")
    void shouldReturnAllRolesWhenRequestAllPath() throws Exception {
        SysRoleVo roleVo = new SysRoleVo();
        roleVo.setId(1L);
        roleVo.setRoleName("管理员");
        when(sysRoleService.listEnabled()).thenReturn(List.of(roleVo));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(sysRoleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/system/roles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].roleName").value("管理员"));

        verify(sysRoleService).listEnabled();
    }
}
