package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsItemBin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 物品默认库位Mapper接口
 */
@Mapper
public interface WmsItemBinMapper extends BaseMapper<WmsItemBin> {

    /**
     * 查询物品所有默认库位关联，包含已逻辑删除记录，用于恢复历史关联。
     *
     * @param itemId 物品ID
     * @return 默认库位关联列表
     */
    @Select("SELECT id, item_id, bin_id, sort_order, del_flag, create_time, create_by, update_time, update_by " +
            "FROM wms_item_bin WHERE item_id = #{itemId}")
    List<WmsItemBin> selectAllByItemId(@Param("itemId") Long itemId);

    /**
     * 恢复已逻辑删除的默认库位关联。
     *
     * @param id        关联ID
     * @param sortOrder 排序号
     * @return 更新行数
     */
    @Update("UPDATE wms_item_bin SET del_flag = #{delFlag}, sort_order = #{sortOrder}, update_time = NOW() WHERE id = #{id}")
    int restoreById(@Param("id") Long id, @Param("sortOrder") Integer sortOrder, @Param("delFlag") Integer delFlag);
}
