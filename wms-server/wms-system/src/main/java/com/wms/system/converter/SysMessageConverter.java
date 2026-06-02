package com.wms.system.converter;

import com.wms.system.domain.entity.SysMessage;
import com.wms.system.domain.vo.SysMessageVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 站内信转换器。
 */
@Component
public class SysMessageConverter {

    /**
     * 将站内信实体转换为视图对象。
     *
     * @param entity 站内信实体
     * @return 站内信视图对象
     */
    public SysMessageVo toVo(SysMessage entity) {
        SysMessageVo vo = new SysMessageVo();
        vo.setId(entity.getId());
        vo.setReceiverId(entity.getReceiverId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setMessageType(entity.getMessageType());
        vo.setMessageLevel(entity.getMessageLevel());
        vo.setBusinessKey(entity.getBusinessKey());
        vo.setTargetUrl(entity.getTargetUrl());
        vo.setReadStatus(entity.getReadStatus());
        vo.setReadTime(entity.getReadTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 批量转换站内信实体。
     *
     * @param entities 站内信实体列表
     * @return 站内信视图对象列表
     */
    public List<SysMessageVo> toVoList(List<SysMessage> entities) {
        return entities.stream().map(this::toVo).toList();
    }
}
