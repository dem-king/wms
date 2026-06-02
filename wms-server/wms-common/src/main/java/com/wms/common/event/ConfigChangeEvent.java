package com.wms.common.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 系统配置变更事件
 * 当sys_config表中的配置值被修改时发布此事件
 */
@Getter
public class ConfigChangeEvent extends ApplicationEvent {

    /** 变更的配置键 */
    @Schema(description = "变更的配置键")
    private final String configKey;

    /** 变更前的值 */
    @Schema(description = "变更前的值")
    private final String oldValue;

    /** 变更后的值 */
    @Schema(description = "变更后的值")
    private final String newValue;

    /**
     * 构造配置变更事件
     *
     * @param source    事件源
     * @param configKey 变更的配置键
     * @param oldValue  变更前的值
     * @param newValue  变更后的值
     */
    public ConfigChangeEvent(Object source, String configKey, String oldValue, String newValue) {
        super(source);
        this.configKey = configKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
