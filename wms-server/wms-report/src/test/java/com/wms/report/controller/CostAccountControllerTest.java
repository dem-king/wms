package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CostAccountController 注解测试
 * 验证查询接口满足数据权限约束。
 */
@DisplayName("CostAccountController 测试")
class CostAccountControllerTest {

    @Test
    @DisplayName("获取费用核算配置接口应声明数据权限注解")
    void shouldDeclareDataScopeOnGetConfig() throws NoSuchMethodException {
        DataScope dataScope = CostAccountController.class
                .getMethod("getConfig")
                .getAnnotation(DataScope.class);

        assertNotNull(dataScope);
    }
}
