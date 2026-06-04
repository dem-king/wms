package com.wms.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器工具类
 * 提供静态方法获取 ApplicationContext 中的 Bean 实例，
 * 用于在 Filter、工具类等无法直接注入的场景获取 Spring 管理的 Bean
 *
 * @author wms-team
 * @since 1.0
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置 Spring 应用上下文
     * 由 Spring 容器在启动时自动调用
     *
     * @param applicationContext Spring 应用上下文
     * @throws BeansException 上下文设置异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取 Spring 应用上下文
     *
     * @return ApplicationContext 实例
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据类型获取 Bean 实例
     *
     * @param clazz Bean 的类型
     * @param <T>   Bean 类型泛型
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在或有多个
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称获取 Bean 实例
     *
     * @param name Bean 的名称
     * @param <T>  Bean 类型泛型
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据名称和类型获取 Bean 实例
     *
     * @param name  Bean 的名称
     * @param clazz Bean 的类型
     * @param <T>   Bean 类型泛型
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}