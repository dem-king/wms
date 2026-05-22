package com.wms.system.converter;

import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.domain.vo.SysOperLogVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志转换器
 * SysOperLog实体与SysOperLogVo之间的转换
 */
@Component
public class SysOperLogConverter {

    /**
     * 实体转VO
     *
     * @param entity 操作日志实体
     * @return 操作日志VO
     */
    public SysOperLogVo toVo(SysOperLog entity) {
        SysOperLogVo vo = new SysOperLogVo();
        vo.setId(entity.getId());
        vo.setModule(entity.getModule());
        vo.setType(entity.getType());
        vo.setDesc(entity.getDesc());
        vo.setOperatorId(entity.getOperatorId());
        vo.setOperatorName(entity.getOperatorName());
        vo.setRequestUrl(entity.getRequestUrl());
        vo.setRequestMethod(entity.getRequestMethod());
        vo.setRequestParams(entity.getRequestParams());
        vo.setResponseResult(entity.getResponseResult());
        vo.setOperIp(entity.getOperIp());
        vo.setStatus(entity.getStatus());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setCostTime(entity.getCostTime());
        vo.setOperTime(entity.getOperTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 操作日志实体列表
     * @return 操作日志VO列表
     */
    public List<SysOperLogVo> toVoList(List<SysOperLog> entities) {
        return entities.stream().map(this::toVo).collect(Collectors.toList());
    }
}
