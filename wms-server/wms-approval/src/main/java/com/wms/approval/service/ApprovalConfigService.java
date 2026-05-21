package com.wms.approval.service;

import com.wms.approval.domain.dto.ApprovalConfigDto;
import com.wms.approval.domain.vo.ApprovalConfigVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 审批配置服务接口
 * 提供审批配置的CRUD和分页查询功能
 */
public interface ApprovalConfigService {

    /**
     * 分页查询审批配置
     *
     * @param pageParam 分页参数
     * @param bizType   业务类型(可选)
     * @return 分页结果
     */
    PageResult<ApprovalConfigVo> pageConfigs(PageParam pageParam, Integer bizType);

    /**
     * 根据ID获取审批配置详情(含节点列表)
     *
     * @param id 审批配置ID
     * @return 审批配置详情VO
     */
    ApprovalConfigVo getConfigById(Long id);

    /**
     * 新增审批配置
     * 保存配置及节点列表
     *
     * @param dto 审批配置创建参数
     * @return 创建后的审批配置VO
     */
    ApprovalConfigVo createConfig(ApprovalConfigDto dto);

    /**
     * 更新审批配置
     * 删除原有节点后重新保存
     *
     * @param id  审批配置ID
     * @param dto 审批配置更新参数
     * @return 更新后的审批配置VO
     */
    ApprovalConfigVo updateConfig(Long id, ApprovalConfigDto dto);

    /**
     * 删除审批配置(逻辑删除)
     *
     * @param id 审批配置ID
     */
    void deleteConfig(Long id);
}
