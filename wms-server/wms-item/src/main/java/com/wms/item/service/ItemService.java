package com.wms.item.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.dto.ItemImageDto;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * 物品服务接口
 * 提供物品CRUD、分页查询、图片管理、快速搜索和默认库位维护功能。
 */
public interface ItemService {

    /**
     * 查询物品分页列表。
     *
     * @param pageParam 分页参数
     * @param categoryId 主类目ID
     * @param subCategoryId 细分类目ID
     * @param tagId 标签ID
     * @param status 状态
     * @param keyword 搜索关键字
     * @return 分页结果
     */
    PageResult<ItemVo> page(PageParam pageParam, Long categoryId, Long subCategoryId,
                            Long tagId, Integer status, String keyword);

    /**
     * 根据ID获取物品详情。
     *
     * @param id 物品ID
     * @return 物品详情VO
     */
    ItemVo getById(Long id);

    /**
     * 新增物品。
     *
     * @param dto 物品新增参数
     * @return 新增后的物品VO
     */
    ItemVo create(ItemDto dto);

    /**
     * 更新物品。
     *
     * @param id 物品ID
     * @param dto 物品更新参数
     * @return 更新后的物品VO
     */
    ItemVo update(Long id, ItemDto dto);

    /**
     * 追加物品默认库位，只补充缺失库位并保留已有库位关系。
     *
     * @param itemId 物品ID
     * @param binIds 需要追加的库位ID集合
     * @return 更新后的物品VO
     */
    ItemVo appendDefaultBins(Long itemId, Collection<Long> binIds);

    /**
     * 删除物品。
     *
     * @param id 物品ID
     */
    void delete(Long id);

    /**
     * 上传物品图片。
     *
     * @param itemId 物品ID
     * @param file 图片文件
     * @return 图片VO
     */
    ItemImageVo uploadImage(Long itemId, MultipartFile file);

    /**
     * 关联已上传的物品图片。
     *
     * @param itemId 物品ID
     * @param dto 图片关联参数
     * @return 图片VO
     */
    ItemImageVo attachImage(Long itemId, ItemImageDto dto);

    /**
     * 删除物品图片。
     *
     * @param itemId 物品ID
     * @param imageId 图片ID
     */
    void deleteImage(Long itemId, Long imageId);

    /**
     * 快速搜索物品。
     *
     * @param keyword 搜索关键字
     * @return 物品VO列表
     */
    List<ItemVo> search(String keyword);
}
