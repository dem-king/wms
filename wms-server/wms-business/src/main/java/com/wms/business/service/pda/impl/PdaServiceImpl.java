package com.wms.business.service.pda.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.StockCheckConverter;
import com.wms.business.domain.constant.PdaConstants;
import com.wms.business.domain.dto.pda.RfidBatchReadDto;
import com.wms.business.domain.dto.pda.StockCheckDetailDto;
import com.wms.business.domain.dto.pda.StockCheckDto;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.entity.WmsStockCheckDetail;
import com.wms.business.domain.entity.WmsStockCheckOrder;
import com.wms.business.domain.vo.pda.PdaTaskVo;
import com.wms.business.domain.vo.pda.RfidBatchReadResultVo;
import com.wms.business.domain.vo.pda.StockCheckDiffDetailVo;
import com.wms.business.domain.vo.pda.StockCheckResultVo;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsStockCheckDetailMapper;
import com.wms.business.mapper.WmsStockCheckOrderMapper;
import com.wms.business.service.pda.PdaService;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.exception.BizException;

import com.wms.common.util.SequenceGenerator;
import com.wms.item.domain.entity.WmsElectronicLabel;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsElectronicLabelMapper;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PDA专用服务实现类
 * 处理RFID批量读取上报、盘点结果提交、待办任务统计等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdaServiceImpl implements PdaService {

    private final WmsElectronicLabelMapper wmsElectronicLabelMapper;
    private final WmsStockCheckOrderMapper wmsStockCheckOrderMapper;
    private final WmsStockCheckDetailMapper wmsStockCheckDetailMapper;
    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsReturnOrderMapper wmsReturnOrderMapper;

    private final WmsItemMapper wmsItemMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SequenceGenerator sequenceGenerator;
    private final StockCheckConverter stockCheckConverter;
    private final JdbcTemplate jdbcTemplate;

    /**
     * RFID批量读取上报
     * 查询该库房下所有在库标签(labelStatus=1)的rfidCode，
     * 与实际读取EPC对比，生成盘盈(实际有系统无)和盘亏(系统有实际无)差异明细
     *
     * @param dto RFID批量读取上报请求
     * @return 对比结果
     */
    @Override
    public RfidBatchReadResultVo rfidBatchRead(RfidBatchReadDto dto) {
        // 校验库房是否存在
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        // 查询该库房下所有在库标签(labelStatus=在库)的rfidCode
        // 注：MyBatis-Plus自动拼接del_flag=0，无需手动添加
        LambdaQueryWrapper<WmsElectronicLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IN_STOCK.getCode());
        List<WmsElectronicLabel> systemLabels = wmsElectronicLabelMapper.selectList(labelWrapper);

        // 构建系统rfidCode到标签的映射
        Map<String, WmsElectronicLabel> systemRfidMap = systemLabels.stream()
                .filter(label -> label.getRfidCode() != null && !label.getRfidCode().isBlank())
                .collect(Collectors.toMap(WmsElectronicLabel::getRfidCode, Function.identity(), (a, b) -> a));

        // 实际读取的EPC码去重
        Set<String> actualEpcSet = new HashSet<>(dto.getEpcCodes());

        // 计算匹配的EPC码：系统有且实际也有
        Set<String> matchedEpcSet = new HashSet<>(systemRfidMap.keySet());
        matchedEpcSet.retainAll(actualEpcSet);

        // 盘盈明细：实际有但系统无的EPC
        List<StockCheckDiffDetailVo> surplusDetails = new ArrayList<>();
        for (String epc : actualEpcSet) {
            if (!systemRfidMap.containsKey(epc)) {
                StockCheckDiffDetailVo detailVo = new StockCheckDiffDetailVo();
                detailVo.setLabelNo(epc);
                detailVo.setDiffType(PdaConstants.DIFF_TYPE_SURPLUS);
                detailVo.setSystemQty(0);
                detailVo.setActualQty(1);
                surplusDetails.add(detailVo);
            }
        }

        // 盘亏明细：系统有但实际无的标签
        List<StockCheckDiffDetailVo> deficitDetails = new ArrayList<>();
        // 批量查询物品信息，避免N+1
        Set<Long> itemIds = systemLabels.stream()
                .map(WmsElectronicLabel::getItemId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsItem> itemMap = itemIds.isEmpty() ? Map.of()
                : wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, Function.identity()));

        for (Map.Entry<String, WmsElectronicLabel> entry : systemRfidMap.entrySet()) {
            if (!actualEpcSet.contains(entry.getKey())) {
                WmsElectronicLabel label = entry.getValue();
                StockCheckDiffDetailVo detailVo = new StockCheckDiffDetailVo();
                detailVo.setLabelNo(label.getLabelNo());
                detailVo.setItemId(label.getItemId());
                detailVo.setDiffType(PdaConstants.DIFF_TYPE_DEFICIT);
                detailVo.setSystemQty(1);
                detailVo.setActualQty(0);
                // 填充物品信息
                if (label.getItemId() != null) {
                    WmsItem item = itemMap.get(label.getItemId());
                    if (item != null) {
                        detailVo.setItemName(item.getItemName());
                        detailVo.setItemCode(item.getItemCode());
                    }
                }
                deficitDetails.add(detailVo);
            }
        }

        // 组装结果
        RfidBatchReadResultVo result = new RfidBatchReadResultVo();
        result.setSystemCount(systemRfidMap.size());
        result.setActualCount(actualEpcSet.size());
        result.setMatchCount(matchedEpcSet.size());
        result.setSurplusDetails(surplusDetails);
        result.setDeficitDetails(deficitDetails);
        return result;
    }

    /**
     * 盘点结果提交
     * 生成盘点单号，保存盘点记录和差异明细
     *
     * @param dto 盘点结果提交请求
     * @return 盘点结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockCheckResultVo stockCheck(StockCheckDto dto) {
        // 校验库房是否存在
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        // 校验盘点类型
        if (dto.getCheckType() == null
                || (dto.getCheckType() != PdaConstants.CHECK_TYPE_FULL
                && dto.getCheckType() != PdaConstants.CHECK_TYPE_PARTIAL)) {
            throw new BizException("盘点类型无效，仅支持1(全盘)或2(抽盘)");
        }

        // 生成盘点单号：PD + 年月日 + 4位流水号
        String checkNo = sequenceGenerator.next(PdaConstants.STOCK_CHECK_NO_PREFIX);

        // 计算差异统计
        int surplusCount = 0;
        int deficitCount = 0;
        for (StockCheckDetailDto item : dto.getItems()) {
            if (item.getActualQty() > item.getSystemQty()) {
                surplusCount++;
            } else if (item.getActualQty() < item.getSystemQty()) {
                deficitCount++;
            }
        }

        // 保存盘点单主表
        WmsStockCheckOrder order = new WmsStockCheckOrder();
        order.setCheckNo(checkNo);
        order.setWarehouseId(dto.getWarehouseId());
        order.setAreaId(dto.getAreaId());
        order.setCheckType(dto.getCheckType());
        order.setStatus(PdaConstants.CHECK_STATUS_SUBMITTED);
        order.setSystemCount(surplusCount + deficitCount);
        order.setActualCount(dto.getItems().size());
        order.setMatchCount(dto.getItems().size() - surplusCount - deficitCount);
        order.setRemark(dto.getRemark());
        wmsStockCheckOrderMapper.insert(order);

        // 保存盘点差异明细
        List<WmsStockCheckDetail> detailList = new ArrayList<>();
        for (StockCheckDetailDto itemDto : dto.getItems()) {
            // 校验物品是否存在
            WmsItem item = wmsItemMapper.selectById(itemDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + itemDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + itemDto.getItemId());
            }

            // 仅保存有差异的明细(实际数量≠系统数量)
            if (!itemDto.getActualQty().equals(itemDto.getSystemQty())) {
                WmsStockCheckDetail detail = new WmsStockCheckDetail();
                detail.setCheckOrderId(order.getId());
                detail.setItemId(itemDto.getItemId());
                detail.setSystemQty(itemDto.getSystemQty());
                detail.setActualQty(itemDto.getActualQty());
                detail.setBinId(itemDto.getBinId());
                // 判断差异类型
                if (itemDto.getActualQty() > itemDto.getSystemQty()) {
                    detail.setDiffType(PdaConstants.DIFF_TYPE_SURPLUS);
                } else {
                    detail.setDiffType(PdaConstants.DIFF_TYPE_DEFICIT);
                }
                detailList.add(detail);
            }
        }
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
        }

        return stockCheckConverter.toResultVo(order);
    }

    /**
     * 查询当前用户的PDA待办任务统计
     * 统计各类待处理任务数量
     *
     * @return 待办任务统计VO
     */
    @Override
    public PdaTaskVo getTasks() {
        PdaTaskVo vo = new PdaTaskVo();

        // 统计待提交入库单数(草稿状态)
        Long inboundPending = wmsInboundOrderMapper.selectCount(
                new LambdaQueryWrapper<WmsInboundOrder>()
                        .eq(WmsInboundOrder::getStatus, OrderStatusEnum.DRAFT.getCode()));
        vo.setInboundPendingCount(inboundPending.intValue());

        // 统计待提交出库单数(草稿状态)
        Long outboundPending = wmsOutboundOrderMapper.selectCount(
                new LambdaQueryWrapper<WmsOutboundOrder>()
                        .eq(WmsOutboundOrder::getStatus, OrderStatusEnum.DRAFT.getCode()));
        vo.setOutboundPendingCount(outboundPending.intValue());

        // 统计待提交归还单数(草稿状态)
        Long returnPending = wmsReturnOrderMapper.selectCount(
                new LambdaQueryWrapper<WmsReturnOrder>()
                        .eq(WmsReturnOrder::getStatus, OrderStatusEnum.DRAFT.getCode()));
        vo.setReturnPendingCount(returnPending.intValue());

        // 统计待审批单据数(待审批0+审批中1)
        // 注：审批模块在wms-approval中，为避免循环依赖，使用JdbcTemplate直接查询
        Integer approvalPending = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wms_approval_order WHERE del_flag = 0 AND status IN (0, 1)",
                Integer.class);
        vo.setApprovalPendingCount(approvalPending != null ? approvalPending : 0);

        // 统计库存预警数(库存数量低于安全库存)
        // 使用SQL JOIN在数据库层面完成筛选，避免全表查询+内存过滤
        Integer alertCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wms_stock s INNER JOIN wms_item i ON s.item_id = i.id "
                        + "WHERE s.del_flag = 0 AND i.del_flag = 0 "
                        + "AND i.stock_lower_limit IS NOT NULL AND s.quantity < i.stock_lower_limit",
                Integer.class);
        vo.setStockAlertCount(alertCount != null ? alertCount : 0);

        // 统计超期归还数(标签状态为正在使用且预计归还时间已过)
        LocalDateTime now = LocalDateTime.now();
        Long overdueReturn = wmsElectronicLabelMapper.selectCount(
                new LambdaQueryWrapper<WmsElectronicLabel>()
                        .eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IN_USE.getCode())
                        .lt(WmsElectronicLabel::getExpectedReturn, now));
        vo.setOverdueReturnCount(overdueReturn.intValue());

        return vo;
    }
}
