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
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.event.ApprovalResultEvent;
import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        WmsApprovalConfig config = wmsApprovalConfigMapper.selectOne(
                new LambdaQueryWrapper<WmsApprovalConfig>()
                        .eq(WmsApprovalConfig::getBizType, bizType)
                        .eq(WmsApprovalConfig::getEnabled, ApprovalConstants.STATUS_APPROVED));
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
        if (!(config.getAutoApprove() != null && config.getAutoApprove() == ApprovalConstants.STATUS_APPROVED)
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
            order.setCurrentStep(order.getCurrentStep() + ApprovalConstants.RESULT_APPROVED);
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
        result.setRecords(page.getRecords().stream()
                .map(approvalOrderConverter::toVo)
                .toList());
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
        return vo;
    }
}
