package com.wms.item.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.dto.ItemImageDto;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.ItemImageVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 物品服务接口
 * 提供物品CRUD、分页查询、图片管理、快速搜索等功能
 */
public interface ItemService {

    /**
     * 物品分页列表
     * 支持按类目、标签、状态、关键字筛选
     *
     * @param pageParam  分页参数
     * @param categoryId 主类目ID(可选)
     * @param subCategoryId 细分类目ID(可选)
     * @param tagId      标签ID(可选)
     * @param status     状态(可选)
     * @param keyword    搜索关键字(可选，匹配名称/编号/型号/拼音)
     * @return 分页结果
     */
    PageResult<ItemVo> page(PageParam pageParam, Long categoryId, Long subCategoryId,
                            Long tagId, Integer status, String keyword);

    /**
     * 根据ID获取物品详情(含标签、图片、库存信息)
     *
     * @param id 物品ID
     * @return 物品详情VO
     */
    ItemVo getById(Long id);

    /**
     * 新增物品
     * 自动生成物品编号和拼音，关联标签
     *
     * @param dto 物品新增参数
     * @return 新增后的物品VO
     */
    ItemVo create(ItemDto dto);

    /**
     * 更新物品
     * 同步更新标签关联关系
     *
     * @param id  物品ID
     * @param dto 物品更新参数
     * @return 更新后的物品VO
     */
    ItemVo update(Long id, ItemDto dto);

    /**
     * 删除物品(逻辑删除)
     * 同时逻辑删除关联的标签关联和图片
     *
     * @param id 物品ID
     */
    void delete(Long id);

    /**
     * 上传物品图片
     * 文件上传至MinIO后保存图片记录
     *
     * @param itemId 物品ID
     * @param file   图片文件
     * @return 图片VO
     */
    ItemImageVo uploadImage(Long itemId, MultipartFile file);

    /**
     * 关联已通过统一存储接口上传的物品图片。
     *
     * @param itemId 物品ID
     * @param dto    物品图片关联参数
     * @return 图片VO
     */
    ItemImageVo attachImage(Long itemId, ItemImageDto dto);

    /**
     * 删除物品图片(逻辑删除)
     *
     * @param itemId  物品ID
     * @param imageId 图片ID
     */
    void deleteImage(Long itemId, Long imageId);

    /**
     * 快速搜索物品
     * 按名称、拼音首字母、型号、编码模糊匹配
     *
     * @param keyword 搜索关键字
     * @return 物品VO列表
     */
    List<ItemVo> search(String keyword);
}
