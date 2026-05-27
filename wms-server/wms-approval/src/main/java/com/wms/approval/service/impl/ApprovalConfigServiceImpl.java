package com.wms.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.approval.converter.ApprovalConfigConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.dto.ApprovalConfigDto;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.vo.ApprovalConfigVo;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.approval.service.ApprovalConfigService;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 审批配置服务实现类
 * 处理审批配置的CRUD和分页查询业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

    private final WmsApprovalConfigMapper wmsApprovalConfigMapper;
    private final WmsApprovalNodeMapper wmsApprovalNodeMapper;
    private final ApprovalConfigConverter approvalConfigConverter;

    /**
     * 分页查询审批配置
     *
     * @param pageParam 分页参数
     * @param bizType   业务类型(可选)
     * @return 分页结果
     */
    @Override
    public PageResult<ApprovalConfigVo> pageConfigs(PageParam pageParam, Integer bizType) {
        LambdaQueryWrapper<WmsApprovalConfig> wrapper = new LambdaQueryWrapper<>();
        if (bizType != null) {
            wrapper.eq(WmsApprovalConfig::getBizType, bizType);
        }
        wrapper.orderByDesc(WmsApprovalConfig::getCreateTime);

        Page<WmsApprovalConfig> page = wmsApprovalConfigMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ApprovalConfigVo> result = new PageResult<>();
        List<WmsApprovalConfig> records = page.getRecords();
        // 批量查询所有配置的节点，避免N+1查询
        Set<Long> configIds = records.stream()
                .map(WmsApprovalConfig::getId).collect(Collectors.toSet());
        Map<Long, List<WmsApprovalNode>> nodesMap;
        if (configIds.isEmpty()) {
            nodesMap = Map.of();
        } else {
            List<WmsApprovalNode> allNodes = wmsApprovalNodeMapper.selectList(
                    new LambdaQueryWrapper<WmsApprovalNode>()
                            .in(WmsApprovalNode::getConfigId, configIds)
                            .orderByAsc(WmsApprovalNode::getStepOrder));
            nodesMap = allNodes.stream()
                    .collect(Collectors.groupingBy(WmsApprovalNode::getConfigId));
        }
        Map<Long, List<WmsApprovalNode>> finalNodesMap = nodesMap;
        result.setRecords(records.stream().map(entity -> {
            ApprovalConfigVo vo = approvalConfigConverter.toVo(entity);
            List<WmsApprovalNode> nodes = finalNodesMap.getOrDefault(entity.getId(), List.of());
            vo.setNodes(approvalConfigConverter.toNodeVoList(nodes));
            return vo;
        }).toList());
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID获取审批配置详情(含节点列表)
     *
     * @param id 审批配置ID
     * @return 审批配置详情VO
     */
    @Override
    public ApprovalConfigVo getConfigById(Long id) {
        WmsApprovalConfig config = wmsApprovalConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException("审批配置不存在");
        }
        if (config.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批配置已删除");
        }
        ApprovalConfigVo vo = approvalConfigConverter.toVo(config);
        // 查询节点列表并按顺序排列
        List<WmsApprovalNode> nodes = wmsApprovalNodeMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalNode>()
                        .eq(WmsApprovalNode::getConfigId, id)
                        .orderByAsc(WmsApprovalNode::getStepOrder));
        vo.setNodes(approvalConfigConverter.toNodeVoList(nodes));
        return vo;
    }

    /**
     * 新增审批配置
     * 保存配置及节点列表
     *
     * @param dto 审批配置创建参数
     * @return 创建后的审批配置VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalConfigVo createConfig(ApprovalConfigDto dto) {
        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setBizType(dto.getBizType());
        config.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : BizConstants.STATUS_ENABLED);
        config.setAutoApprove(dto.getAutoApprove() != null ? dto.getAutoApprove() : BizConstants.STATUS_DISABLED);
        config.setConfigName(dto.getConfigName());
        config.setRemark(dto.getRemark());
        config.setTimeoutHours(dto.getTimeoutHours() != null ? dto.getTimeoutHours() : ApprovalConstants.DEFAULT_TIMEOUT_HOURS);
        config.setTimeoutAction(dto.getTimeoutAction() != null ? dto.getTimeoutAction() : ApprovalConstants.TIMEOUT_ACTION_REMIND);
        wmsApprovalConfigMapper.insert(config);

        // 保存审批节点配置
        if (dto.getNodes() != null) {
            List<WmsApprovalNode> nodeList = new ArrayList<>();
            for (ApprovalConfigDto.ApprovalNodeDto nodeDto : dto.getNodes()) {
                WmsApprovalNode node = new WmsApprovalNode();
                node.setConfigId(config.getId());
                node.setStepOrder(nodeDto.getStepOrder());
                node.setNodeName(nodeDto.getNodeName());
                node.setApproverType(nodeDto.getApproverType());
                node.setApproverId(nodeDto.getApproverId());
                nodeList.add(node);
            }
            if (!nodeList.isEmpty()) {
                Db.saveBatch(nodeList);
            }
        }

        return getConfigById(config.getId());
    }

    /**
     * 更新审批配置
     * 逻辑删除原有节点后重新保存
     *
     * @param id  审批配置ID
     * @param dto 审批配置更新参数
     * @return 更新后的审批配置VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalConfigVo updateConfig(Long id, ApprovalConfigDto dto) {
        WmsApprovalConfig config = wmsApprovalConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException("审批配置不存在");
        }
        if (config.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批配置已删除");
        }

        config.setBizType(dto.getBizType());
        if (dto.getEnabled() != null) {
            config.setEnabled(dto.getEnabled());
        }
        if (dto.getAutoApprove() != null) {
            config.setAutoApprove(dto.getAutoApprove());
        }
        config.setConfigName(dto.getConfigName());
        config.setRemark(dto.getRemark());
        if (dto.getTimeoutHours() != null) {
            config.setTimeoutHours(dto.getTimeoutHours());
        }
        if (dto.getTimeoutAction() != null) {
            config.setTimeoutAction(dto.getTimeoutAction());
        }
        wmsApprovalConfigMapper.updateById(config);

        // 逻辑删除原有节点
        List<WmsApprovalNode> oldNodes = wmsApprovalNodeMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalNode>().eq(WmsApprovalNode::getConfigId, id));
        List<WmsApprovalNode> updateNodes = new ArrayList<>();
        for (WmsApprovalNode oldNode : oldNodes) {
            WmsApprovalNode updateNode = new WmsApprovalNode();
            updateNode.setId(oldNode.getId());
            updateNode.setDelFlag(DelFlagConstants.DELETED);
            updateNodes.add(updateNode);
        }
        if (!updateNodes.isEmpty()) {
            Db.updateBatchById(updateNodes);
        }

        // 保存新的审批节点配置
        if (dto.getNodes() != null) {
            List<WmsApprovalNode> newNodeList = new ArrayList<>();
            for (ApprovalConfigDto.ApprovalNodeDto nodeDto : dto.getNodes()) {
                WmsApprovalNode node = new WmsApprovalNode();
                node.setConfigId(id);
                node.setStepOrder(nodeDto.getStepOrder());
                node.setNodeName(nodeDto.getNodeName());
                node.setApproverType(nodeDto.getApproverType());
                node.setApproverId(nodeDto.getApproverId());
                newNodeList.add(node);
            }
            if (!newNodeList.isEmpty()) {
                Db.saveBatch(newNodeList);
            }
        }

        return getConfigById(id);
    }

    /**
     * 删除审批配置(逻辑删除)
     *
     * @param id 审批配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        WmsApprovalConfig config = wmsApprovalConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException("审批配置不存在");
        }
        if (config.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批配置已删除");
        }
        // 逻辑删除配置
        WmsApprovalConfig updateEntity = new WmsApprovalConfig();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        wmsApprovalConfigMapper.updateById(updateEntity);

        // 逻辑删除关联节点
        List<WmsApprovalNode> nodes = wmsApprovalNodeMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalNode>().eq(WmsApprovalNode::getConfigId, id));
        List<WmsApprovalNode> updateNodeList = new ArrayList<>();
        for (WmsApprovalNode node : nodes) {
            WmsApprovalNode updateNode = new WmsApprovalNode();
            updateNode.setId(node.getId());
            updateNode.setDelFlag(DelFlagConstants.DELETED);
            updateNodeList.add(updateNode);
        }
        if (!updateNodeList.isEmpty()) {
            Db.updateBatchById(updateNodeList);
        }
    }
}
