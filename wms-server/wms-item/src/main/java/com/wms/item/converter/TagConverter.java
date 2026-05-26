package com.wms.item.converter;

import com.wms.item.domain.entity.WmsTag;
import com.wms.item.domain.vo.TagVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签转换器
 * WmsTag实体与TagVo之间的转换逻辑
 */
@Component
public class TagConverter {

    /**
     * WmsTag实体转TagVo
     *
     * @param tag 标签实体
     * @return 标签VO
     */
    public TagVo toVo(WmsTag tag) {
        TagVo vo = new TagVo();
        vo.setId(tag.getId());
        vo.setTagName(tag.getTagName());
        vo.setTagColor(tag.getTagColor());
        vo.setTagDesc(tag.getTagDesc());
        vo.setScopeType(tag.getScopeType());
        vo.setScopeId(tag.getScopeId());
        vo.setCreateTime(tag.getCreateTime());
        return vo;
    }

    /**
     * 批量转换标签实体列表
     *
     * @param tags 标签实体列表
     * @return 标签VO列表
     */
    public List<TagVo> toVoList(List<WmsTag> tags) {
        return tags.stream().map(this::toVo).collect(Collectors.toList());
    }
}
