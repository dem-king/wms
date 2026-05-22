package com.wms.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.annotation.OperLog;
import com.wms.common.util.IpUtil;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP切面
 * 拦截@OperLog注解标注的方法，自动记录操作日志并异步持久化
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final SysOperLogService sysOperLogService;
    private final ObjectMapper objectMapper;

    /**
     * 定义切入点：所有标注了@OperLog注解的方法
     */
    @Pointcut("@annotation(com.wms.common.annotation.OperLog)")
    public void operLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     * 捕获方法执行前后的信息，包括请求参数、响应结果、耗时、异常等
     *
     * @param joinPoint 切入点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("operLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog operLogAnnotation = method.getAnnotation(OperLog.class);

        SysOperLog operLog = new SysOperLog();
        operLog.setModule(operLogAnnotation.module());
        operLog.setType(operLogAnnotation.type());
        operLog.setDesc(operLogAnnotation.desc());

        // 获取当前用户信息
        Long userId = SecurityUtil.getCurrentUserId();
        String username = SecurityUtil.getCurrentUsername();
        operLog.setOperatorId(userId);
        operLog.setOperatorName(username);

        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operLog.setRequestUrl(request.getRequestURI());
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperIp(IpUtil.getIpAddr(request));
        }

        // 获取请求参数并脱敏
        operLog.setRequestParams(getDesensitizedParams(joinPoint));

        Object result = null;
        try {
            // 执行业务方法
            result = joinPoint.proceed();
            operLog.setStatus(SysLogConstants.OPER_STATUS_SUCCESS);
            // 截断响应结果避免过长
            operLog.setResponseResult(truncateToJson(result, SysLogConstants.MAX_PARAM_LENGTH));
        } catch (Throwable e) {
            operLog.setStatus(SysLogConstants.OPER_STATUS_FAIL);
            // 截断异常信息
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            operLog.setErrorMsg(
                    errorMsg.length() > SysLogConstants.MAX_ERROR_MSG_LENGTH
                            ? errorMsg.substring(0, SysLogConstants.MAX_ERROR_MSG_LENGTH)
                            : errorMsg);
            throw e;
        } finally {
            // 计算耗时
            operLog.setCostTime(System.currentTimeMillis() - startTime);
            operLog.setOperTime(java.time.LocalDateTime.now());
            // 异步保存日志
            sysOperLogService.asyncSave(operLog);
        }

        return result;
    }

    /**
     * 获取脱敏后的请求参数
     * 将方法参数名和参数值映射后，对敏感字段进行脱敏处理
     *
     * @param joinPoint 切入点
     * @return 脱敏后的参数JSON字符串
     */
    private String getDesensitizedParams(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            if (paramNames == null || paramNames.length == 0) {
                return null;
            }
            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                // 跳过HttpServletRequest等Servlet对象
                if (args[i] instanceof HttpServletRequest) {
                    continue;
                }
                paramMap.put(paramNames[i], args[i]);
            }
            String json = objectMapper.writeValueAsString(paramMap);
            // 对敏感字段进行脱敏
            return desensitize(json);
        } catch (Exception e) {
            log.debug("获取请求参数失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对JSON字符串中的敏感字段值进行脱敏替换
     *
     * @param json 原始JSON字符串
     * @return 脱敏后的JSON字符串
     */
    private String desensitize(String json) {
        if (json == null) {
            return null;
        }
        for (String field : SysLogConstants.SENSITIVE_FIELDS) {
            // 替换 "password":"xxx" 为 "password":"******"
            json = json.replaceAll(
                    "(\"" + field + "\"\\s*:\\s*)\"[^\"]*\"",
                    "$1\"" + SysLogConstants.DESENSITIZE_MASK + "\"");
        }
        // 截断过长参数
        if (json.length() > SysLogConstants.MAX_PARAM_LENGTH) {
            json = json.substring(0, SysLogConstants.MAX_PARAM_LENGTH);
        }
        return json;
    }

    /**
     * 将对象转为JSON字符串并截断
     *
     * @param obj      对象
     * @param maxLen   最大长度
     * @return 截断后的JSON字符串
     */
    private String truncateToJson(Object obj, int maxLen) {
        if (obj == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(obj);
            if (json.length() > maxLen) {
                return json.substring(0, maxLen);
            }
            return json;
        } catch (Exception e) {
            return obj.toString();
        }
    }
}
