package com.wms.business.service;

import com.wms.business.domain.dto.OutboundScanDto;
import com.wms.business.domain.vo.OutboundScanResultVo;

/**
 * 出库扫码服务接口
 * 提供标签扫码识别、重复扫描校验和出库前状态校验能力
 */
public interface OutboundScanService {

    /**
     * 解析出库扫码结果
     * 复用标签扫码能力并校验当前单据内重复扫描和标签状态
     *
     * @param dto 出库扫码请求
     * @return 出库扫码结果
     */
    OutboundScanResultVo scan(OutboundScanDto dto);
}
