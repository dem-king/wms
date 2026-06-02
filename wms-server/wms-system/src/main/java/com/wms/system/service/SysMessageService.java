package com.wms.system.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.domain.dto.SysMessageCreateDto;
import com.wms.system.domain.dto.SysMessageQueryDto;
import com.wms.system.domain.vo.SysMessageVo;

/**
 * 站内信服务接口。
 */
public interface SysMessageService {

    /**
     * 查询当前用户站内信分页。
     *
     * @param pageParam 分页参数
     * @param query     查询参数
     * @return 当前用户站内信分页
     */
    PageResult<SysMessageVo> pageMessages(PageParam pageParam, SysMessageQueryDto query);

    /**
     * 统计当前用户未读站内信数量。
     *
     * @return 未读数量
     */
    Long countUnread();

    /**
     * 标记当前用户一条站内信为已读。
     *
     * @param id 站内信ID
     */
    void markRead(Long id);

    /**
     * 标记当前用户全部站内信为已读。
     */
    void markAllRead();

    /**
     * 幂等创建站内信。
     *
     * @param dto 站内信创建参数
     * @return 是否新创建
     */
    boolean createIfAbsent(SysMessageCreateDto dto);
}
