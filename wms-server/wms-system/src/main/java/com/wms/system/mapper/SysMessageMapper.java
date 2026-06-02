package com.wms.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.system.domain.entity.SysMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内信Mapper。
 */
@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {
}
