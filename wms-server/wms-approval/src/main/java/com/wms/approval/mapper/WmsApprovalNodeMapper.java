package com.wms.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.approval.domain.entity.WmsApprovalNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 审批节点配置Mapper
 */
@Mapper
public interface WmsApprovalNodeMapper extends BaseMapper<WmsApprovalNode> {

    /**
     * 按审批配置ID显式更新节点逻辑删除标记。
     *
     * @param configId 配置ID
     * @param delFlag  逻辑删除标记
     * @param updateBy 更新人
     * @return 更新行数
     */
    @Update("UPDATE wms_approval_node SET del_flag = #{delFlag}, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE config_id = #{configId}")
    int updateDelFlagByConfigId(@Param("configId") Long configId,
                                @Param("delFlag") Integer delFlag,
                                @Param("updateBy") String updateBy);
}
