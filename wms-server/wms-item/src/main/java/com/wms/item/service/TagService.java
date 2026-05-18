package com.wms.item.service;

import com.wms.item.domain.dto.TagDto;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;

import java.util.List;

/**
 * 标签服务接口
 * 提供标签CRUD和按标签筛选物品功能
 */
public interface TagService {

    /**
     * 查询所有标签列表
     *
     * @return 标签VO列表
     */
    List<TagVo> listAll();

    /**
     * 新增标签
     *
     * @param dto 标签新增参数
     * @return 新增后的标签VO
     */
    TagVo create(TagDto dto);

    /**
     * 更新标签
     *
     * @param id  标签ID
     * @param dto 标签更新参数
     * @return 更新后的标签VO
     */
    TagVo update(Long id, TagDto dto);

    /**
     * 删除标签(逻辑删除)
     * 同时解除物品与该标签的关联
     *
     * @param id 标签ID
     */
    void delete(Long id);

    /**
     * 按标签筛选物品列表
     *
     * @param tagId 标签ID
     * @return 物品VO列表
     */
    List<ItemVo> getItemsByTag(Long tagId);
}
