package com.wms.system.aspect;

import com.wms.common.context.DataScopeContext;
import com.wms.common.datascope.DataScopeCondition;
import com.wms.system.service.DataScopeService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据范围切面。
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final DataScopeService dataScopeService;

    /**
     * 在标记@DataScope的查询执行期间设置数据范围上下文。
     *
     * @param joinPoint 切点
     * @return 原方法返回值
     * @throws Throwable 原方法异常
     */
    @Around("@annotation(com.wms.common.annotation.DataScope)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        DataScopeCondition condition = dataScopeService.getCurrentDataScope();
        DataScopeContext.set(condition);
        try {
            return joinPoint.proceed();
        } finally {
            DataScopeContext.clear();
        }
    }
}
