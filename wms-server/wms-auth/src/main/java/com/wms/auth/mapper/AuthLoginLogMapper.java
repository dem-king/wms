package com.wms.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.auth.domain.entity.AuthLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthLoginLogMapper extends BaseMapper<AuthLoginLog> {
}
