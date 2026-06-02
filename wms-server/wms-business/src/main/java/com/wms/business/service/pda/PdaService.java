package com.wms.business.service.pda;

import com.wms.business.domain.dto.pda.RfidBatchReadDto;
import com.wms.business.domain.dto.pda.StockCheckDto;
import com.wms.business.domain.vo.pda.PdaTaskVo;
import com.wms.business.domain.vo.pda.RfidBatchReadResultVo;
import com.wms.business.domain.vo.pda.StockCheckResultVo;

/**
 * PDA专用服务接口
 * 提供RFID批量读取上报、盘点结果提交、待办任务统计等功能
 */
public interface PdaService {

    /**
     * RFID批量读取上报
     * 查询该库房下所有在库标签的rfidCode，与实际读取EPC对比，生成差异明细
     *
     * @param dto RFID批量读取上报请求，包含库房ID、EPC码列表
     * @return 对比结果，包含系统标签数、实际标签数、匹配数、盘盈/盘亏明细
     */
    RfidBatchReadResultVo rfidBatchRead(RfidBatchReadDto dto);

    /**
     * 盘点结果提交
     * 保存盘点记录和差异明细，生成盘点单号
     *
     * @param dto 盘点结果提交请求，包含库房ID、盘点类型、明细列表
     * @return 盘点结果，包含盘点单ID和状态
     */
    StockCheckResultVo stockCheck(StockCheckDto dto);

    /**
     * 查询当前用户的PDA待办任务统计
     * 统计待提交入库单、待提交出库单、待提交归还单、待审批单据、库存预警、超期归还数量
     *
     * @return 待办任务统计VO
     */
    PdaTaskVo getTasks();
}