package com.wms.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.auth.domain.entity.AuthOperLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthOperLogMapper extends BaseMapper<AuthOperLog> {
}
