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
import com.wms.common.constant.PermissionConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
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
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

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
            vo.setNodes(toNodeVoList(nodes));
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
        vo.setNodes(toNodeVoList(nodes));
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
        Integer enabled = dto.getEnabled() != null ? dto.getEnabled() : BizConstants.STATUS_ENABLED;
        validateEnabledConfigUnique(dto.getBizType(), null, enabled);
        validateNodeApprovers(dto.getAutoApprove(), dto.getNodes());

        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setBizType(dto.getBizType());
        config.setEnabled(enabled);
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
        validateNodeApprovers(dto.getAutoApprove(), dto.getNodes());
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
        validateEnabledConfigUnique(config.getBizType(), id, config.getEnabled());
        wmsApprovalConfigMapper.updateById(config);

        // 显式更新 @TableLogic 字段，避免通用批量更新链路忽略 delFlag 导致旧节点累积。
        wmsApprovalNodeMapper.updateDelFlagByConfigId(id, DelFlagConstants.DELETED, SecurityUtil.getCurrentUsername());

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
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsApprovalConfigMapper, WmsApprovalConfig.class, id);

        // 显式逻辑删除关联节点，避免 @TableLogic 字段在通用更新链路中被忽略。
        wmsApprovalNodeMapper.updateDelFlagByConfigId(id, DelFlagConstants.DELETED, SecurityUtil.getCurrentUsername());
    }

    /**
     * 校验同一业务类型仅允许存在一个启用中的审批配置。
     *
     * @param bizType         业务类型
     * @param excludeConfigId 更新时需要排除的当前配置ID
     * @param enabled         当前配置启用状态
     */
    private List<ApprovalConfigVo.ApprovalNodeVo> toNodeVoList(List<WmsApprovalNode> nodes) {
        List<ApprovalConfigVo.ApprovalNodeVo> nodeVos = approvalConfigConverter.toNodeVoList(nodes);
        if (nodeVos.isEmpty()) {
            return nodeVos;
        }
        Set<Long> userIds = nodeVos.stream()
                .filter(node -> node.getApproverType() == ApprovalConstants.APPROVER_TYPE_USER)
                .map(ApprovalConfigVo.ApprovalNodeVo::getApproverId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = userIds.isEmpty()
                ? Map.of()
                : sysUserMapper.selectBatchIds(userIds).stream()
                        .filter(SysUser.class::isInstance)
                        .map(SysUser.class::cast)
                        .collect(Collectors.toMap(SysUser::getId, Function.identity(), (left, right) -> left));
        Set<Long> roleIds = nodeVos.stream()
                .filter(node -> node.getApproverType() == ApprovalConstants.APPROVER_TYPE_ROLE)
                .map(ApprovalConfigVo.ApprovalNodeVo::getApproverId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysRole> roleMap = roleIds.isEmpty()
                ? Map.of()
                : sysRoleMapper.selectBatchIds(roleIds).stream()
                        .filter(SysRole.class::isInstance)
                        .map(SysRole.class::cast)
                        .collect(Collectors.toMap(SysRole::getId, Function.identity(), (left, right) -> left));
        for (ApprovalConfigVo.ApprovalNodeVo nodeVo : nodeVos) {
            if (nodeVo.getApproverType() == ApprovalConstants.APPROVER_TYPE_USER) {
                SysUser user = userMap.get(nodeVo.getApproverId());
                if (user != null) {
                    nodeVo.setApproverUsername(user.getUsername());
                    nodeVo.setApproverName(resolveUserDisplayName(user));
                }
            }
            if (nodeVo.getApproverType() == ApprovalConstants.APPROVER_TYPE_ROLE) {
                SysRole role = roleMap.get(nodeVo.getApproverId());
                if (role != null) {
                    nodeVo.setApproverRoleName(role.getRoleName());
                }
            }
        }
        return nodeVos;
    }

    private String resolveUserDisplayName(SysUser user) {
        return user.getRealName() != null && !user.getRealName().isBlank()
                ? user.getRealName()
                : user.getUsername();
    }

    private void validateEnabledConfigUnique(Integer bizType, Long excludeConfigId, Integer enabled) {
        if (enabled == null || enabled != BizConstants.STATUS_ENABLED) {
            return;
        }
        LambdaQueryWrapper<WmsApprovalConfig> wrapper = new LambdaQueryWrapper<WmsApprovalConfig>()
                .eq(WmsApprovalConfig::getBizType, bizType)
                .eq(WmsApprovalConfig::getEnabled, BizConstants.STATUS_ENABLED);
        if (excludeConfigId != null) {
            wrapper.ne(WmsApprovalConfig::getId, excludeConfigId);
        }
        Long existingCount = wmsApprovalConfigMapper.selectCount(wrapper);
        if (existingCount != null && existingCount > 0) {
            throw new BizException("该业务类型已存在启用的审批配置");
        }
    }

    private void validateNodeApprovers(Integer autoApprove, List<ApprovalConfigDto.ApprovalNodeDto> nodes) {
        if (autoApprove != null && autoApprove == BizConstants.STATUS_ENABLED) {
            return;
        }
        if (nodes == null || nodes.isEmpty()) {
            throw new BizException("审批配置必须至少包含一个审批节点");
        }
        for (ApprovalConfigDto.ApprovalNodeDto node : nodes) {
            if (node.getApproverType() == null) {
                throw new BizException("审批人类型不能为空");
            }
            if (node.getApproverId() == null) {
                throw new BizException("审批人或角色不能为空");
            }
            if (node.getApproverType() == ApprovalConstants.APPROVER_TYPE_USER) {
                validateUserApprover(node.getApproverId());
                validateUserApprovalPermission(node.getApproverId());
            } else if (node.getApproverType() == ApprovalConstants.APPROVER_TYPE_ROLE) {
                validateRoleApprover(node.getApproverId());
            } else {
                throw new BizException("审批人类型仅支持指定角色或指定用户");
            }
        }
    }

    private void validateUserApprover(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != BizConstants.STATUS_ENABLED) {
            throw new BizException("审批用户已禁用");
        }
    }

    private void validateUserApprovalPermission(Long userId) {
        if (!hasApprovalPermission(userId)) {
            throw new BizException("审批用户缺少审批权限");
        }
    }

    private boolean hasApprovalPermission(Long userId) {
        List<Long> roleIds = sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return false;
        }
        Set<Long> enabledRoleIds = sysRoleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>()
                                .in(SysRole::getId, roleIds)
                                .eq(SysRole::getStatus, BizConstants.STATUS_ENABLED))
                .stream()
                .map(SysRole::getId)
                .collect(Collectors.toSet());
        if (enabledRoleIds.isEmpty()) {
            return false;
        }
        Set<Long> permIds = sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, enabledRoleIds))
                .stream()
                .map(SysRolePermission::getPermId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (permIds.isEmpty()) {
            return false;
        }
        return sysPermissionMapper.selectList(
                        new LambdaQueryWrapper<SysPermission>()
                                .in(SysPermission::getId, permIds)
                                .eq(SysPermission::getStatus, BizConstants.STATUS_ENABLED))
                .stream()
                .anyMatch(permission -> PermissionConstants.APPROVAL_PENDING_APPROVE.equals(permission.getPermCode()));
    }

    private void validateRoleApprover(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || role.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批角色不存在");
        }
        if (role.getStatus() == null || role.getStatus() != BizConstants.STATUS_ENABLED) {
            throw new BizException("审批角色已禁用");
        }
    }
}
