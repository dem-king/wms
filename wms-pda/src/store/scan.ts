/**
 * WMS-PDA 扫码作业状态管理
 * Pinia Composition API风格
 * 管理扫码作业类型、库房选择、扫码明细、提交操作等状态
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ScanJobType, ScanDetailItem, LabelStatusValue } from '@/utils/constants'
import { LabelStatus, OrderStatus } from '@/utils/constants'
import { inboundScanApi, outboundScanApi, labelScanApi } from '@/api/scan'
import {
  createInboundApi, submitInboundApi,
  createOutboundApi, submitOutboundApi,
  createReturnApi, submitReturnApi,
  createTransferApi, submitTransferApi,
  createScrapApi, submitScrapApi
} from '@/api/order'

/** 扫码作业状态Store */
export const useScanStore = defineStore('scan', () => {
  // ==================== State ====================

  /** 当前作业类型 */
  const jobType = ref<ScanJobType | null>(null)
  /** 当前库房ID */
  const warehouseId = ref<number | null>(null)
  /** 目标库房ID（调拨使用） */
  const toWarehouseId = ref<number | null>(null)
  /** 扫码明细列表 */
  const details = ref<ScanDetailItem[]>([])
  /** 是否正在扫码中 */
  const scanning = ref<boolean>(false)
  /** 当前已添加的标签ID列表（用于重复检测） */
  const currentLabelIds = ref<number[]>([])

  // ==================== Getters ====================

  /** 是否可提交（明细非空且不在扫码中） */
  const canSubmit = computed(() => details.value.length > 0 && !scanning.value)

  // ==================== Actions ====================

  /**
   * 设置作业类型
   *
   * @param type 扫码作业类型
   */
  function setJobType(type: ScanJobType): void {
    jobType.value = type
  }

  /**
   * 设置当前库房
   *
   * @param id 库房ID
   */
  function setWarehouse(id: number): void {
    warehouseId.value = id
  }

  /**
   * 设置目标库房（调拨使用）
   *
   * @param id 目标库房ID
   */
  function setToWarehouse(id: number): void {
    toWarehouseId.value = id
  }

  /**
   * 扫码操作
   * 根据jobType调用不同扫码接口，含重复检测+状态校验
   *
   * @param code 扫码内容
   * @returns 扫码结果明细项，失败返回null
   */
  async function scanCode(code: string): Promise<ScanDetailItem | null> {
    if (!jobType.value || scanning.value) {
      return null
    }

    scanning.value = true
    try {
      let detailItem: ScanDetailItem | null = null

      switch (jobType.value) {
        case 'inbound': {
          // 入库扫码：调用入库扫码识别接口
          const result = await inboundScanApi({
            code,
            currentLabelIds: currentLabelIds.value
          })
          // 重复检测
          if (currentLabelIds.value.includes(result.labelId)) {
            throw new Error('该标签已在明细列表中')
          }
          detailItem = {
            labelId: result.labelId,
            labelNo: result.labelNo,
            labelStatus: result.labelStatus,
            itemId: result.itemId,
            itemName: result.itemName,
            itemCode: result.itemCode,
            quantity: result.detail.quantity
          }
          break
        }

        case 'outbound': {
          // 出库扫码：调用出库扫码识别接口
          const result = await outboundScanApi({
            code,
            currentLabelIds: currentLabelIds.value
          })
          // 重复检测
          if (currentLabelIds.value.includes(result.labelId)) {
            throw new Error('该标签已在明细列表中')
          }
          detailItem = {
            labelId: result.labelId,
            labelNo: result.labelNo,
            labelStatus: result.labelStatus,
            itemId: result.itemId,
            itemName: result.itemName,
            itemCode: result.itemCode,
            quantity: result.detail.quantity
          }
          break
        }

        case 'return': {
          // 归还扫码：调用标签查询接口，校验labelStatus=2(正在使用)
          const result = await labelScanApi(code)
          // 重复检测
          if (currentLabelIds.value.includes(result.id)) {
            throw new Error('该标签已在明细列表中')
          }
          // 状态校验：只有正在使用的标签才可归还
          if (result.labelStatus !== LabelStatus.IN_USE) {
            throw new Error('该标签当前状态不可归还')
          }
          detailItem = {
            labelId: result.id,
            labelNo: result.labelNo,
            labelStatus: result.labelStatus,
            itemId: result.itemId,
            itemName: result.itemName,
            itemCode: result.itemCode,
            quantity: 1
          }
          break
        }

        case 'transfer': {
          // 调拨扫码：调用标签查询接口，校验labelStatus=1(在库)
          const result = await labelScanApi(code)
          // 重复检测
          if (currentLabelIds.value.includes(result.id)) {
            throw new Error('该标签已在明细列表中')
          }
          // 状态校验：只有在库的标签才可调拨
          if (result.labelStatus !== LabelStatus.IN_STOCK) {
            throw new Error('该标签当前状态不可调拨')
          }
          detailItem = {
            labelId: result.id,
            labelNo: result.labelNo,
            labelStatus: result.labelStatus,
            itemId: result.itemId,
            itemName: result.itemName,
            itemCode: result.itemCode,
            quantity: 1
          }
          break
        }

        case 'scrap': {
          // 报废扫码：调用标签查询接口，校验labelStatus=1(在库)或5(闲置)
          const result = await labelScanApi(code)
          // 重复检测
          if (currentLabelIds.value.includes(result.id)) {
            throw new Error('该标签已在明细列表中')
          }
          // 状态校验：只有在库或闲置的标签才可报废
          if (result.labelStatus !== LabelStatus.IN_STOCK && result.labelStatus !== LabelStatus.IDLE) {
            throw new Error('该标签当前状态不可报废')
          }
          detailItem = {
            labelId: result.id,
            labelNo: result.labelNo,
            labelStatus: result.labelStatus,
            itemId: result.itemId,
            itemName: result.itemName,
            itemCode: result.itemCode,
            quantity: 1
          }
          break
        }

        case 'query': {
          // 查询扫码：仅查询标签信息，不添加到明细列表
          // 查询扫码在页面层直接处理，此处不操作
          return null
        }

        default:
          return null
      }

      // 添加到明细列表
      if (detailItem) {
        details.value.push(detailItem)
        currentLabelIds.value.push(detailItem.labelId)
      }

      return detailItem
    } finally {
      scanning.value = false
    }
  }

  /**
   * 删除明细项
   * 同时从currentLabelIds中移除对应标签ID
   *
   * @param index 明细索引
   */
  function removeDetail(index: number): void {
    if (index >= 0 && index < details.value.length) {
      const removed = details.value.splice(index, 1)[0]
      // 从currentLabelIds中移除
      const labelIdx = currentLabelIds.value.indexOf(removed.labelId)
      if (labelIdx > -1) {
        currentLabelIds.value.splice(labelIdx, 1)
      }
    }
  }

  /**
   * 清空所有明细
   */
  function clearDetails(): void {
    details.value = []
    currentLabelIds.value = []
  }

  /**
   * 提交单据
   * 根据jobType调用不同单据创建+提交接口
   *
   * @returns 提交后的单据ID，失败返回null
   */
  async function submitOrder(): Promise<number | null> {
    if (!jobType.value || details.value.length === 0) {
      return null
    }

    // 按物品ID聚合明细（同一物品合并数量）
    const aggregatedMap = new Map<number, { itemId: number; quantity: number }>()
    for (const detail of details.value) {
      const existing = aggregatedMap.get(detail.itemId)
      if (existing) {
        existing.quantity += detail.quantity
      } else {
        aggregatedMap.set(detail.itemId, { itemId: detail.itemId, quantity: detail.quantity })
      }
    }
    const aggregatedDetails = Array.from(aggregatedMap.values())

    try {
      switch (jobType.value) {
        case 'inbound': {
          // 创建入库单并提交
          if (!warehouseId.value) throw new Error('请选择库房')
          const order = await createInboundApi({
            warehouseId: warehouseId.value,
            orderType: OrderStatus.DRAFT,
            details: aggregatedDetails
          })
          await submitInboundApi(order.id)
          return order.id
        }

        case 'outbound': {
          // 创建出库单并提交
          if (!warehouseId.value) throw new Error('请选择库房')
          const order = await createOutboundApi({
            warehouseId: warehouseId.value,
            orderType: OrderStatus.DRAFT,
            details: aggregatedDetails
          })
          await submitOutboundApi(order.id)
          return order.id
        }

        case 'return': {
          // 创建归还单并提交
          const order = await createReturnApi({
            details: aggregatedDetails
          })
          await submitReturnApi(order.id)
          return order.id
        }

        case 'transfer': {
          // 创建调拨单并提交
          if (!warehouseId.value) throw new Error('请选择源库房')
          if (!toWarehouseId.value) throw new Error('请选择目标库房')
          const order = await createTransferApi({
            fromWarehouseId: warehouseId.value,
            toWarehouseId: toWarehouseId.value,
            details: aggregatedDetails
          })
          await submitTransferApi(order.id)
          return order.id
        }

        case 'scrap': {
          // 创建报废单并提交
          if (!warehouseId.value) throw new Error('请选择库房')
          const order = await createScrapApi({
            warehouseId: warehouseId.value,
            scrapReason: 'PDA扫码报废',
            details: aggregatedDetails
          })
          await submitScrapApi(order.id)
          return order.id
        }

        default:
          return null
      }
    } catch (error) {
      // 重新抛出错误，由上层处理提示
      throw error
    }
  }

  /**
   * 重置扫码状态
   * 清空所有状态，用于退出扫码页面时调用
   */
  function reset(): void {
    jobType.value = null
    warehouseId.value = null
    toWarehouseId.value = null
    details.value = []
    scanning.value = false
    currentLabelIds.value = []
  }

  return {
    // State
    jobType,
    warehouseId,
    toWarehouseId,
    details,
    scanning,
    currentLabelIds,
    // Getters
    canSubmit,
    // Actions
    setJobType,
    setWarehouse,
    setToWarehouse,
    scanCode,
    removeDetail,
    clearDetails,
    submitOrder,
    reset
  }
})