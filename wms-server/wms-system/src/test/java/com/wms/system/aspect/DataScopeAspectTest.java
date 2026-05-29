package com.wms.system.aspect;

import com.wms.common.context.DataScopeContext;
import com.wms.common.datascope.DataScopeCondition;
import com.wms.system.service.DataScopeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 数据范围切面测试。
 */
@DisplayName("DataScopeAspect 测试")
class DataScopeAspectTest {

    @AfterEach
    void tearDown() {
        DataScopeContext.clear();
    }

    @Test
    @DisplayName("切面执行期间设置数据范围上下文并在结束后清理")
    void shouldSetAndClearDataScopeContextAroundQuery() throws Throwable {
        DataScopeService dataScopeService = mock(DataScopeService.class);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(10L), 7L, true);
        when(dataScopeService.getCurrentDataScope()).thenReturn(condition);
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            assertSame(condition, DataScopeContext.get());
            return "ok";
        });
        DataScopeAspect aspect = new DataScopeAspect(dataScopeService);

        Object result = aspect.around(joinPoint);

        verify(joinPoint).proceed();
        assertSame("ok", result);
        assertNull(DataScopeContext.get());
    }
}
