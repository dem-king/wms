package com.wms.item.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.item.domain.dto.LabelBindDto;
import com.wms.item.domain.dto.LabelGenerateDto;
import com.wms.item.domain.dto.LabelStatusDto;
import com.wms.item.domain.vo.ElectronicLabelVo;

import java.util.List;

/**
 * 电子标签服务接口
 * 提供标签生成、绑定、状态管理、扫码查询、闲置检测等功能
 */
public interface ElectronicLabelService {

    /**
     * 标签分页列表
     * 支持按物品ID、标签类型、标签状态筛选，关联查询物品名称编码
     *
     * @param page        分页对象
     * @param itemId      物品ID(可选)
     * @param labelType   标签类型(可选, 1-二维码 2-条形码 3-RFID)
     * @param labelStatus 标签状态(可选, 1-在库 2-正在使用 3-已归还 4-报废 5-闲置)
     * @return 分页结果
     */
    Page<ElectronicLabelVo> page(Page<ElectronicLabelVo> page, Long itemId, Integer labelType, Integer labelStatus);

    /**
     * 标签详情(含物品信息)
     *
     * @param id 标签ID
     * @return 标签详情VO
     */
    ElectronicLabelVo getById(Long id);

    /**
     * 批量生成标签
     * 生成标签编号(格式: BQ+年月日+4位流水号)，自动生成qrContent和barcodeContent，
     * 如果labelType=3(RFID)还需生成rfidCode
     *
     * @param dto 批量生成参数，包含itemId、count、labelType、bindType
     * @return 生成的标签VO列表
     */
    List<ElectronicLabelVo> generate(LabelGenerateDto dto);

    /**
     * 标签绑定物品
     * 校验标签存在且状态为闲置(5)，校验物品存在，更新itemId和bindType
     *
     * @param id  标签ID
     * @param dto 绑定参数，包含itemId、bindType
     * @return 绑定后的标签VO
     */
    ElectronicLabelVo bind(Long id, LabelBindDto dto);

    /**
     * 更新标签状态
     * 校验状态流转合法性(在库→正在使用→已归还/报废)
     *
     * @param id  标签ID
     * @param dto 状态更新参数，包含labelStatus
     * @return 更新后的标签VO
     */
    ElectronicLabelVo updateStatus(Long id, LabelStatusDto dto);

    /**
     * 批量打印标签(更新打印状态为已打印)
     *
     * @param labelIds 标签ID列表
     */
    void batchPrint(List<Long> labelIds);

    /**
     * 扫码查询标签信息
     * 根据code在labelNo/rfidCode/qrContent/barcodeContent中模糊匹配
     *
     * @param code 扫码内容
     * @return 匹配的标签VO
     */
    ElectronicLabelVo scan(String code);

    /**
     * 查询长期闲置标签
     * 条件: label_status=5 或 (label_status=1 且 update_time < 90天前)
     *
     * @return 闲置标签VO列表
     */
    List<ElectronicLabelVo> listIdle();
}
