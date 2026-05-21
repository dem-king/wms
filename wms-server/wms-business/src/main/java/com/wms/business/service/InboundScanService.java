package com.wms.business.service;

import com.wms.business.domain.dto.InboundScanDto;
import com.wms.business.domain.vo.InboundScanResultVo;

/**
 * 入库扫码服务接口
 * 提供标签扫码识别、重复扫描校验和入库建议明细组装能力
 */
public interface InboundScanService {

    /**
     * 解析入库扫码结果
     * 复用标签扫码能力并校验当前单据内重复扫描
     *
     * @param dto 入库扫码请求
     * @return 入库扫码结果
     */
    InboundScanResultVo scan(InboundScanDto dto);
}
