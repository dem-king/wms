package com.wms.system.converter;

import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.vo.SysLoginLogVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志转换器
 * SysLoginLog实体与SysLoginLogVo之间的转换
 */
@Component
public class SysLoginLogConverter {

    /**
     * 实体转VO
     *
     * @param entity 登录日志实体
     * @return 登录日志VO
     */
    public SysLoginLogVo toVo(SysLoginLog entity) {
        SysLoginLogVo vo = new SysLoginLogVo();
        vo.setId(entity.getId());
        vo.setUsername(entity.getUsername());
        vo.setLoginIp(entity.getLoginIp());
        vo.setLoginLocation(entity.getLoginLocation());
        vo.setBrowser(entity.getBrowser());
        vo.setOs(entity.getOs());
        vo.setStatus(entity.getStatus());
        vo.setFailReason(entity.getFailReason());
        vo.setLoginTime(entity.getLoginTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 登录日志实体列表
     * @return 登录日志VO列表
     */
    public List<SysLoginLogVo> toVoList(List<SysLoginLog> entities) {
        return entities.stream().map(this::toVo).collect(Collectors.toList());
    }
}
