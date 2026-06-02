package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import com.wms.system.converter.SysMessageConverter;
import com.wms.system.domain.constant.SysMessageConstants;
import com.wms.system.domain.dto.SysMessageCreateDto;
import com.wms.system.domain.dto.SysMessageQueryDto;
import com.wms.system.domain.entity.SysMessage;
import com.wms.system.domain.vo.SysMessageVo;
import com.wms.system.mapper.SysMessageMapper;
import com.wms.system.service.SysMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 站内信服务实现类。
 * 提供当前用户消息查询、已读标记和系统内部幂等发送能力。
 */
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl implements SysMessageService {

    private final SysMessageMapper sysMessageMapper;
    private final SysMessageConverter sysMessageConverter;

    /**
     * 查询当前用户站内信分页。
     *
     * @param pageParam 分页参数
     * @param query     查询参数
     * @return 当前用户站内信分页
     */
    @Override
    public PageResult<SysMessageVo> pageMessages(PageParam pageParam, SysMessageQueryDto query) {
        Long currentUserId = requireCurrentUserId();
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, currentUserId);
        if (query != null && query.getReadStatus() != null) {
            wrapper.eq(SysMessage::getReadStatus, query.getReadStatus());
        }
        wrapper.orderByDesc(SysMessage::getCreateTime);

        Page<SysMessage> page = sysMessageMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<SysMessageVo> result = new PageResult<>();
        result.setRecords(sysMessageConverter.toVoList(page.getRecords()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 统计当前用户未读站内信数量。
     *
     * @return 未读数量
     */
    @Override
    public Long countUnread() {
        Long currentUserId = requireCurrentUserId();
        return sysMessageMapper.selectCount(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, currentUserId)
                .eq(SysMessage::getReadStatus, SysMessageConstants.READ_STATUS_UNREAD));
    }

    /**
     * 标记当前用户一条站内信为已读。
     *
     * @param id 站内信ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        Long currentUserId = requireCurrentUserId();
        SysMessage message = sysMessageMapper.selectById(id);
        if (message == null) {
            throw new BizException("站内信不存在");
        }
        if (!currentUserId.equals(message.getReceiverId())) {
            throw new BizException("无权操作该站内信");
        }
        if (SysMessageConstants.READ_STATUS_READ == message.getReadStatus()) {
            return;
        }
        SysMessage updateEntity = new SysMessage();
        updateEntity.setId(id);
        updateEntity.setReadStatus(SysMessageConstants.READ_STATUS_READ);
        updateEntity.setReadTime(LocalDateTime.now());
        sysMessageMapper.updateById(updateEntity);
    }

    /**
     * 标记当前用户全部站内信为已读。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead() {
        Long currentUserId = requireCurrentUserId();
        SysMessage updateEntity = new SysMessage();
        updateEntity.setReadStatus(SysMessageConstants.READ_STATUS_READ);
        updateEntity.setReadTime(LocalDateTime.now());
        sysMessageMapper.update(updateEntity, new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, currentUserId)
                .eq(SysMessage::getReadStatus, SysMessageConstants.READ_STATUS_UNREAD));
    }

    /**
     * 幂等创建站内信。
     *
     * @param dto 站内信创建参数
     * @return 是否新创建
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createIfAbsent(SysMessageCreateDto dto) {
        Long existingCount = sysMessageMapper.selectCount(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, dto.getReceiverId())
                .eq(SysMessage::getMessageType, dto.getMessageType())
                .eq(SysMessage::getBusinessKey, dto.getBusinessKey()));
        if (existingCount != null && existingCount > 0) {
            return false;
        }

        SysMessage message = new SysMessage();
        message.setReceiverId(dto.getReceiverId());
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setMessageType(dto.getMessageType());
        message.setMessageLevel(dto.getMessageLevel());
        message.setBusinessKey(dto.getBusinessKey());
        message.setTargetUrl(dto.getTargetUrl());
        message.setReadStatus(SysMessageConstants.READ_STATUS_UNREAD);
        try {
            sysMessageMapper.insert(message);
            return true;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    /**
     * 获取当前登录用户ID。
     *
     * @return 当前登录用户ID
     */
    private Long requireCurrentUserId() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BizException("当前登录用户不存在");
        }
        return currentUserId;
    }
}
