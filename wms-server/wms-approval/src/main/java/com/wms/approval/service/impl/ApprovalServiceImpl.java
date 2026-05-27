package com.wms.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.approval.converter.ApprovalOrderConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.dto.ApprovalActionDto;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.mapper.WmsApprovalRecordMapper;
import com.wms.approval.service.ApprovalService;
import com.wms.approval.strategy.ApprovalContext;
import com.wms.approval.strategy.ApprovalStrategy;
import com.wms.approval.strategy.ApprovalStrategyFactory;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.event.ApprovalResultEvent;
import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 审批服务实现类
 * 处理审批流程的发起、审批通过、驳回、撤回等业务逻辑
 * 使用策略模式区分免审/单级/多级审批
 */
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final WmsApprovalOrderMapper wmsApprovalOrderMapper;
    private final WmsApprovalRecordMapper wmsApprovalRecordMapper;
    private final WmsApprovalConfigMapper wmsApprovalConfigMapper;
    private final WmsApprovalNodeMapper wmsApprovalNodeMapper;
    private final ApprovalOrderConverter approvalOrderConverter;
    private final ApplicationEventPublisher eventPublisher;
    private final ApprovalStrategyFactory approvalStrategyFactory;
    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 发起审批
     * 查询审批配置，通过策略工厂选择审批策略(免审/单级/多级)并执行
     *
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 审批单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalOrderVo startApproval(Long bizId, int bizType) {
        // 查询该业务类型的审批配置
        WmsApprovalConfig config = getEnabledConfig(bizType);
        if (config == null) {
            throw new BizException("未找到该业务类型的审批配置");
        }
        if (config.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批配置已删除");
        }

        // 查询审批节点列表
        List<WmsApprovalNode> nodes = wmsApprovalNodeMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalNode>()
                        .eq(WmsApprovalNode::getConfigId, config.getId())
                        .orderByAsc(WmsApprovalNode::getStepOrder));

        // 非免审时必须有审批节点
        if (!(config.getAutoApprove() != null && config.getAutoApprove() == BizConstants.STATUS_ENABLED)
                && nodes.isEmpty()) {
            throw new BizException("审批配置未设置审批节点");
        }

        // 构建审批上下文
        ApprovalContext context = ApprovalContext.builder()
                .bizId(bizId)
                .bizType(bizType)
                .applicantId(SecurityUtil.getCurrentUserId())
                .config(config)
                .nodes(nodes)
                .build();

        // 通过策略工厂选择并执行审批策略
        ApprovalStrategy strategy = approvalStrategyFactory.getStrategy(config, nodes.size());
        return strategy.execute(context);
    }

    /**
     * 审批通过
     * 校验审批单状态，记录审批结果，若为最后节点则标记审批通过
     *
     * @param approvalId 审批单ID
     * @param dto        审批操作参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long approvalId, ApprovalActionDto dto) {
        WmsApprovalOrder order = wmsApprovalOrderMapper.selectById(approvalId);
        if (order == null) {
            throw new BizException("审批单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批工单已删除");
        }
        if (order.getStatus() != ApprovalConstants.STATUS_APPROVING) {
            throw new BizException("审批单状态不是审批中，无法审批通过");
        }
        validateCurrentApprover(order);

        // 写入审批记录
        WmsApprovalRecord record = new WmsApprovalRecord();
        record.setApprovalId(approvalId);
        record.setStepOrder(order.getCurrentStep());
        record.setApproverId(SecurityUtil.getCurrentUserId());
        record.setApproverName(SecurityUtil.getCurrentUsername());
        record.setResult(ApprovalConstants.RESULT_APPROVED);
        record.setOpinion(dto != null ? dto.getOpinion() : "");
        record.setApproveTime(LocalDateTime.now());
        wmsApprovalRecordMapper.insert(record);

        // 若当前节点是最后一步则标记审批通过，否则推进到下一节点
        if (order.getCurrentStep() >= order.getTotalSteps()) {
            order.setStatus(ApprovalConstants.STATUS_APPROVED);
            wmsApprovalOrderMapper.updateById(order);
            // 审批通过后发布审批结果事件，通知业务模块执行后续逻辑
            eventPublisher.publishEvent(new ApprovalResultEvent(order.getBizId(), order.getBizType(), true));
        } else {
            order.setCurrentStep(order.getCurrentStep() + ApprovalConstants.STEP_INCREMENT);
            wmsApprovalOrderMapper.updateById(order);
        }
    }

    /**
     * 审批驳回
     * 校验审批单状态，记录审批结果，标记审批单为已驳回
     *
     * @param approvalId 审批单ID
     * @param dto        审批操作参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long approvalId, ApprovalActionDto dto) {
        WmsApprovalOrder order = wmsApprovalOrderMapper.selectById(approvalId);
        if (order == null) {
            throw new BizException("审批单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批工单已删除");
        }
        if (order.getStatus() != ApprovalConstants.STATUS_APPROVING) {
            throw new BizException("审批单状态不是审批中，无法驳回");
        }
        validateCurrentApprover(order);

        // 写入审批记录
        WmsApprovalRecord record = new WmsApprovalRecord();
        record.setApprovalId(approvalId);
        record.setStepOrder(order.getCurrentStep());
        record.setApproverId(SecurityUtil.getCurrentUserId());
        record.setApproverName(SecurityUtil.getCurrentUsername());
        record.setResult(ApprovalConstants.RESULT_REJECTED);
        record.setOpinion(dto != null ? dto.getOpinion() : "");
        record.setApproveTime(LocalDateTime.now());
        wmsApprovalRecordMapper.insert(record);

        // 标记审批单为已驳回
        order.setStatus(ApprovalConstants.STATUS_REJECTED);
        wmsApprovalOrderMapper.updateById(order);

        // 审批驳回后发布审批结果事件，通知业务模块回退状态
        eventPublisher.publishEvent(new ApprovalResultEvent(order.getBizId(), order.getBizType(), false));
    }

    /**
     * 撤回审批
     * 校验审批单状态为待审批或审批中时允许撤回，撤回后发布审批结果事件(驳回)通知业务回退
     *
     * @param approvalId 审批单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long approvalId) {
        WmsApprovalOrder order = wmsApprovalOrderMapper.selectById(approvalId);
        if (order == null) {
            throw new BizException("审批单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批工单已删除");
        }
        if (order.getStatus() != ApprovalConstants.STATUS_PENDING
                && order.getStatus() != ApprovalConstants.STATUS_APPROVING) {
            throw new BizException("仅待审批或审批中状态可以撤回");
        }
        if (!Objects.equals(order.getApplicantId(), SecurityUtil.getCurrentUserId())) {
            throw new BizException("仅申请人可以撤回审批");
        }

        // 标记审批单为已撤回
        order.setStatus(ApprovalConstants.STATUS_REVOKED);
        wmsApprovalOrderMapper.updateById(order);

        // 撤回后发布审批结果事件(驳回)，通知业务模块将单据状态回退为草稿
        eventPublisher.publishEvent(new ApprovalResultEvent(order.getBizId(), order.getBizType(), false));
    }

    /**
     * 根据业务单据查询审批单
     *
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 审批单VO
     */
    @Override
    public ApprovalOrderVo getByBiz(Long bizId, int bizType) {
        WmsApprovalOrder order = wmsApprovalOrderMapper.selectOne(
                new LambdaQueryWrapper<WmsApprovalOrder>()
                        .eq(WmsApprovalOrder::getBizId, bizId)
                        .eq(WmsApprovalOrder::getBizType, bizType));
        if (order == null) {
            throw new BizException("未找到该业务单据的审批记录");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批工单已删除");
        }
        return buildOrderVoWithRecords(order);
    }

    /**
     * 分页查询审批单
     *
     * @param pageParam 分页参数
     * @param bizType   业务类型(可选)
     * @param status    审批状态(可选)
     * @return 分页结果
     */
    @Override
    public PageResult<ApprovalOrderVo> pageApprovals(PageParam pageParam, Integer bizType, Integer status) {
        LambdaQueryWrapper<WmsApprovalOrder> wrapper = new LambdaQueryWrapper<>();
        if (bizType != null) {
            wrapper.eq(WmsApprovalOrder::getBizType, bizType);
        }
        if (status != null) {
            wrapper.eq(WmsApprovalOrder::getStatus, status);
        }
        wrapper.orderByDesc(WmsApprovalOrder::getCreateTime);

        Page<WmsApprovalOrder> page = wmsApprovalOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ApprovalOrderVo> result = new PageResult<>();
        List<WmsApprovalOrder> orders = page.getRecords();
        List<ApprovalOrderVo> records = orders.stream()
                .map(approvalOrderConverter::toVo)
                .toList();
        enrichApprovalOrderVos(orders, records);
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID获取审批单详情(含记录列表)
     *
     * @param id 审批单ID
     * @return 审批单详情VO
     */
    @Override
    public ApprovalOrderVo getApprovalById(Long id) {
        WmsApprovalOrder order = wmsApprovalOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("审批单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("审批工单已删除");
        }
        return buildOrderVoWithRecords(order);
    }

    /**
     * 构建审批单VO并填充审批记录列表
     *
     * @param order 审批单实体
     * @return 含审批记录的审批单VO
     */
    private ApprovalOrderVo buildOrderVoWithRecords(WmsApprovalOrder order) {
        ApprovalOrderVo vo = approvalOrderConverter.toVo(order);
        List<WmsApprovalRecord> records = wmsApprovalRecordMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalRecord>()
                        .eq(WmsApprovalRecord::getApprovalId, order.getId())
                        .orderByAsc(WmsApprovalRecord::getStepOrder));
        vo.setRecords(approvalOrderConverter.toRecordVoList(records));
        enrichApprovalOrderVos(List.of(order), List.of(vo));
        return vo;
    }

    /**
     * 批量补齐审批单展示字段，避免前端自行拼装审批单号、业务单号和节点名称
     *
     * @param orders 审批单实体列表
     * @param vos    审批单VO列表
     */
    private void enrichApprovalOrderVos(List<WmsApprovalOrder> orders, List<ApprovalOrderVo> vos) {
        if (orders.isEmpty() || orders.size() != vos.size()) {
            return;
        }
        Set<Integer> bizTypes = orders.stream()
                .map(WmsApprovalOrder::getBizType)
                .collect(Collectors.toSet());
        Map<Integer, WmsApprovalConfig> configMap = loadEnabledConfigMap(bizTypes);
        Map<Long, Map<Integer, String>> nodeNameMap = loadNodeNameMap(configMap.values());
        Map<Long, String> applicantNameMap = loadApplicantNameMap(orders);
        Map<String, String> bizNoMap = loadBizNoMap(orders);
        for (int index = 0; index < orders.size(); index++) {
            WmsApprovalOrder order = orders.get(index);
            ApprovalOrderVo vo = vos.get(index);
            vo.setApprovalNo(buildApprovalNo(order.getId()));
            vo.setBizNo(bizNoMap.getOrDefault(buildBizKey(order.getBizType(), order.getBizId()), String.valueOf(order.getBizId())));
            vo.setApplicantName(resolveApplicantName(order, applicantNameMap));
            vo.setCurrentNodeName(resolveCurrentNodeName(order, configMap, nodeNameMap));
            fillRecordNodeNames(order, vo, configMap, nodeNameMap);
        }
    }

    /**
     * 查询启用的审批配置
     *
     * @param bizType 业务类型
     * @return 审批配置
     */
    private WmsApprovalConfig getEnabledConfig(int bizType) {
        return wmsApprovalConfigMapper.selectOne(
                new LambdaQueryWrapper<WmsApprovalConfig>()
                        .eq(WmsApprovalConfig::getBizType, bizType)
                        .eq(WmsApprovalConfig::getEnabled, BizConstants.STATUS_ENABLED));
    }

    /**
     * 校验当前登录用户是否为当前步骤审批人
     *
     * @param order 审批单
     */
    private void validateCurrentApprover(WmsApprovalOrder order) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BizException("当前登录用户不存在");
        }
        WmsApprovalConfig config = getEnabledConfig(order.getBizType());
        if (config == null) {
            throw new BizException("审批配置不存在");
        }
        WmsApprovalNode currentNode = wmsApprovalNodeMapper.selectOne(
                new LambdaQueryWrapper<WmsApprovalNode>()
                        .eq(WmsApprovalNode::getConfigId, config.getId())
                        .eq(WmsApprovalNode::getStepOrder, order.getCurrentStep()));
        if (currentNode == null) {
            throw new BizException("当前审批步骤不存在");
        }
        if (Objects.equals(currentNode.getApproverType(), ApprovalConstants.APPROVER_TYPE_USER)) {
            if (!Objects.equals(currentNode.getApproverId(), currentUserId)) {
                throw new BizException("当前用户不是当前步骤审批人");
            }
            return;
        }
        if (Objects.equals(currentNode.getApproverType(), ApprovalConstants.APPROVER_TYPE_ROLE)) {
            if (!hasRoleApproval(currentNode.getApproverId())) {
                throw new BizException("当前用户不具备当前步骤审批角色");
            }
            return;
        }
        if (Objects.equals(currentNode.getApproverType(), ApprovalConstants.APPROVER_TYPE_WAREHOUSE_ADMIN)) {
            throw new BizException("库房管理员审批节点暂未支持，已拒绝当前审批请求");
        }
        throw new BizException("当前审批步骤审批人类型不受支持");
    }

    /**
     * 校验当前登录用户是否拥有审批节点指定角色
     *
     * @param roleId 审批角色ID
     * @return 是否匹配
     */
    private boolean hasRoleApproval(Long roleId) {
        if (roleId == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        String requiredRole = "ROLE_" + roleId;
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredRole::equals);
    }

    /**
     * 批量查询启用的审批配置
     *
     * @param bizTypes 业务类型集合
     * @return 业务类型到审批配置的映射
     */
    private Map<Integer, WmsApprovalConfig> loadEnabledConfigMap(Set<Integer> bizTypes) {
        if (bizTypes.isEmpty()) {
            return Map.of();
        }
        return wmsApprovalConfigMapper.selectList(
                        new LambdaQueryWrapper<WmsApprovalConfig>()
                                .in(WmsApprovalConfig::getBizType, bizTypes)
                                .eq(WmsApprovalConfig::getEnabled, BizConstants.STATUS_ENABLED))
                .stream()
                .collect(Collectors.toMap(WmsApprovalConfig::getBizType, Function.identity(), (left, right) -> left));
    }

    /**
     * 批量查询审批节点名称
     *
     * @param configs 审批配置集合
     * @return 配置ID到步骤名称映射
     */
    private Map<Long, Map<Integer, String>> loadNodeNameMap(Collection<WmsApprovalConfig> configs) {
        Set<Long> configIds = configs.stream()
                .map(WmsApprovalConfig::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (configIds.isEmpty()) {
            return Map.of();
        }
        return wmsApprovalNodeMapper.selectList(
                        new LambdaQueryWrapper<WmsApprovalNode>()
                                .in(WmsApprovalNode::getConfigId, configIds)
                                .orderByAsc(WmsApprovalNode::getStepOrder))
                .stream()
                .collect(Collectors.groupingBy(
                        WmsApprovalNode::getConfigId,
                        Collectors.toMap(WmsApprovalNode::getStepOrder, WmsApprovalNode::getNodeName, (left, right) -> left, LinkedHashMap::new)));
    }

    /**
     * 批量查询申请人姓名
     *
     * @param orders 审批单列表
     * @return 申请人ID到姓名映射
     */
    private Map<Long, String> loadApplicantNameMap(List<WmsApprovalOrder> orders) {
        Set<Long> applicantIds = orders.stream()
                .map(WmsApprovalOrder::getApplicantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (applicantIds.isEmpty()) {
            return Map.of();
        }
        return sysUserMapper.selectBatchIds(applicantIds).stream()
                .filter(SysUser.class::isInstance)
                .map(SysUser.class::cast)
                .collect(Collectors.toMap(SysUser::getId, this::resolveUserDisplayName, (left, right) -> left));
    }

    /**
     * 批量查询业务单号
     *
     * @param orders 审批单列表
     * @return 业务类型+业务ID到业务单号映射
     */
    private Map<String, String> loadBizNoMap(List<WmsApprovalOrder> orders) {
        Map<Integer, Set<Long>> bizIdsByType = orders.stream()
                .collect(Collectors.groupingBy(
                        WmsApprovalOrder::getBizType,
                        Collectors.mapping(WmsApprovalOrder::getBizId, Collectors.toSet())));
        Map<String, String> bizNoMap = new HashMap<>();
        appendOrderNoMap(bizNoMap, BizTypeEnum.INBOUND.getCode(), bizIdsByType.get(BizTypeEnum.INBOUND.getCode()),
                wmsInboundOrderMapper.selectBatchIds(defaultIds(bizIdsByType.get(BizTypeEnum.INBOUND.getCode()))).stream()
                        .map(WmsInboundOrder.class::cast)
                        .collect(Collectors.toMap(WmsInboundOrder::getId, WmsInboundOrder::getOrderNo, (left, right) -> left)));
        appendOrderNoMap(bizNoMap, BizTypeEnum.OUTBOUND.getCode(), bizIdsByType.get(BizTypeEnum.OUTBOUND.getCode()),
                wmsOutboundOrderMapper.selectBatchIds(defaultIds(bizIdsByType.get(BizTypeEnum.OUTBOUND.getCode()))).stream()
                        .map(WmsOutboundOrder.class::cast)
                        .collect(Collectors.toMap(WmsOutboundOrder::getId, WmsOutboundOrder::getOrderNo, (left, right) -> left)));
        appendOrderNoMap(bizNoMap, BizTypeEnum.SCRAP.getCode(), bizIdsByType.get(BizTypeEnum.SCRAP.getCode()),
                wmsScrapOrderMapper.selectBatchIds(defaultIds(bizIdsByType.get(BizTypeEnum.SCRAP.getCode()))).stream()
                        .map(WmsScrapOrder.class::cast)
                        .collect(Collectors.toMap(WmsScrapOrder::getId, WmsScrapOrder::getOrderNo, (left, right) -> left)));
        appendOrderNoMap(bizNoMap, BizTypeEnum.TRANSFER.getCode(), bizIdsByType.get(BizTypeEnum.TRANSFER.getCode()),
                wmsTransferOrderMapper.selectBatchIds(defaultIds(bizIdsByType.get(BizTypeEnum.TRANSFER.getCode()))).stream()
                        .map(WmsTransferOrder.class::cast)
                        .collect(Collectors.toMap(WmsTransferOrder::getId, WmsTransferOrder::getOrderNo, (left, right) -> left)));
        appendOrderNoMap(bizNoMap, BizTypeEnum.RETURN.getCode(), bizIdsByType.get(BizTypeEnum.RETURN.getCode()),
                wmsReturnOrderMapper.selectBatchIds(defaultIds(bizIdsByType.get(BizTypeEnum.RETURN.getCode()))).stream()
                        .map(WmsReturnOrder.class::cast)
                        .collect(Collectors.toMap(WmsReturnOrder::getId, WmsReturnOrder::getOrderNo, (left, right) -> left)));
        return bizNoMap;
    }

    /**
     * 为审批记录补齐节点名称
     *
     * @param order       审批单实体
     * @param vo          审批单VO
     * @param configMap   配置映射
     * @param nodeNameMap 节点名称映射
     */
    private void fillRecordNodeNames(WmsApprovalOrder order,
                                     ApprovalOrderVo vo,
                                     Map<Integer, WmsApprovalConfig> configMap,
                                     Map<Long, Map<Integer, String>> nodeNameMap) {
        if (vo.getRecords() == null || vo.getRecords().isEmpty()) {
            return;
        }
        WmsApprovalConfig config = configMap.get(order.getBizType());
        Map<Integer, String> stepNameMap = config == null
                ? Map.of()
                : nodeNameMap.getOrDefault(config.getId(), Map.of());
        vo.getRecords().forEach(recordVo ->
                recordVo.setNodeName(stepNameMap.getOrDefault(recordVo.getStepOrder(), "第" + recordVo.getStepOrder() + "步")));
    }

    /**
     * 解析审批单展示用申请人姓名
     *
     * @param order            审批单实体
     * @param applicantNameMap 申请人姓名映射
     * @return 申请人姓名
     */
    private String resolveApplicantName(WmsApprovalOrder order, Map<Long, String> applicantNameMap) {
        String applicantName = applicantNameMap.get(order.getApplicantId());
        return applicantName != null && !applicantName.isBlank() ? applicantName : order.getCreateBy();
    }

    /**
     * 解析当前审批节点名称
     *
     * @param order       审批单实体
     * @param configMap   审批配置映射
     * @param nodeNameMap 节点名称映射
     * @return 节点名称
     */
    private String resolveCurrentNodeName(WmsApprovalOrder order,
                                          Map<Integer, WmsApprovalConfig> configMap,
                                          Map<Long, Map<Integer, String>> nodeNameMap) {
        if (Objects.equals(order.getTotalSteps(), ApprovalConstants.EMPTY_STEP_COUNT)) {
            return ApprovalConstants.FREE_APPROVAL_NODE_NAME;
        }
        WmsApprovalConfig config = configMap.get(order.getBizType());
        if (config == null) {
            return "第" + order.getCurrentStep() + "步";
        }
        return nodeNameMap.getOrDefault(config.getId(), Map.of())
                .getOrDefault(order.getCurrentStep(), "第" + order.getCurrentStep() + "步");
    }

    /**
     * 构建审批单号
     *
     * @param approvalId 审批单ID
     * @return 审批单号
     */
    private String buildApprovalNo(Long approvalId) {
        return ApprovalConstants.APPROVAL_NO_PREFIX + approvalId;
    }

    /**
     * 构建业务映射主键
     *
     * @param bizType 业务类型
     * @param bizId   业务单据ID
     * @return 映射主键
     */
    private String buildBizKey(Integer bizType, Long bizId) {
        return bizType + ":" + bizId;
    }

    /**
     * 追加业务单号映射
     *
     * @param targetMap    目标映射
     * @param bizType      业务类型
     * @param bizIds       业务ID集合
     * @param orderNoMap   业务ID到单号映射
     */
    private void appendOrderNoMap(Map<String, String> targetMap,
                                  Integer bizType,
                                  Set<Long> bizIds,
                                  Map<Long, String> orderNoMap) {
        if (bizIds == null || bizIds.isEmpty()) {
            return;
        }
        bizIds.forEach(bizId -> targetMap.put(buildBizKey(bizType, bizId), orderNoMap.getOrDefault(bizId, String.valueOf(bizId))));
    }

    /**
     * 防御式处理空业务ID集合
     *
     * @param bizIds 业务ID集合
     * @return 非空集合
     */
    private Collection<Long> defaultIds(Set<Long> bizIds) {
        return bizIds == null ? Collections.emptyList() : bizIds;
    }

    /**
     * 解析用户展示名，优先真实姓名，其次用户名
     *
     * @param user 系统用户
     * @return 展示名称
     */
    private String resolveUserDisplayName(SysUser user) {
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        return user.getUsername();
    }
}
