/**
 * WMS-PDA useScan扫码作业组合函数
 * 封装扫码操作，集成振动/声音反馈
 * 扫码成功：shortVibrate + playSuccess
 * 扫码失败：longVibrate + playError
 */
import { computed } from 'vue'
import type { ScanJobType, ScanDetailItem } from '@/utils/constants'
import { useScanStore } from '@/store/scan'
import { useVibrate } from '@/composables/useVibrate'
import { useSound } from '@/composables/useSound'

/** useScan返回类型 */
interface UseScanReturn {
  /** 当前作业类型 */
  jobType: computed<ScanJobType | null>
  /** 扫码明细列表 */
  details: computed<ScanDetailItem[]>
  /** 是否正在扫码中 */
  scanning: computed<boolean>
  /** 是否可提交 */
  canSubmit: computed<boolean>
  /** 设置作业类型 */
  setJobType: (type: ScanJobType) => void
  /** 设置当前库房 */
  setWarehouse: (id: number) => void
  /** 设置目标库房 */
  setToWarehouse: (id: number) => void
  /** 扫码操作（含反馈） */
  scanCode: (code: string) => Promise<ScanDetailItem | null>
  /** 删除明细项 */
  removeDetail: (index: number) => void
  /** 提交单据（含反馈） */
  submitOrder: () => Promise<number | null>
  /** 重置扫码状态 */
  reset: () => void
}

/**
 * 扫码作业组合函数
 * 封装scanCode/removeDetail/submitOrder/reset操作，集成振动/声音反馈
 *
 * @returns 扫码操作接口
 */
export function useScan(): UseScanReturn {
  const scanStore = useScanStore()
  const { shortVibrate, longVibrate } = useVibrate()
  const { playSuccess, playError } = useSound()

  /**
   * 扫码操作
   * 调用scanStore.scanCode，根据结果触发振动/声音反馈
   *
   * @param code 扫码内容
   * @returns 扫码结果明细项，失败返回null
   */
  async function scanCode(code: string): Promise<ScanDetailItem | null> {
    try {
      const result = await scanStore.scanCode(code)

      if (result) {
        // 扫码成功：短振动 + 成功提示音
        shortVibrate()
        playSuccess()
      }

      return result
    } catch (error: unknown) {
      // 扫码失败：长振动 + 错误提示音
      longVibrate()
      playError()

      // 显示错误提示
      const errMsg = error instanceof Error ? error.message : '扫码识别失败'
      uni.showToast({
        title: errMsg,
        icon: 'none',
        duration: 2000
      })

      return null
    }
  }

  /**
   * 删除明细项
   * 委托给scanStore.removeDetail
   *
   * @param index 明细索引
   */
  function removeDetail(index: number): void {
    scanStore.removeDetail(index)
  }

  /**
   * 提交单据
   * 调用scanStore.submitOrder，成功触发反馈，失败提示错误
   *
   * @returns 提交后的单据ID，失败返回null
   */
  async function submitOrder(): Promise<number | null> {
    try {
      const orderId = await scanStore.submitOrder()

      if (orderId) {
        // 提交成功：短振动 + 成功提示音
        shortVibrate()
        playSuccess()

        uni.showToast({
          title: '提交成功',
          icon: 'success',
          duration: 1500
        })
      }

      return orderId
    } catch (error: unknown) {
      // 提交失败：长振动 + 错误提示音
      longVibrate()
      playError()

      const errMsg = error instanceof Error ? error.message : '提交失败'
      uni.showToast({
        title: errMsg,
        icon: 'none',
        duration: 2000
      })

      return null
    }
  }

  /**
   * 重置扫码状态
   * 委托给scanStore.reset
   */
  function reset(): void {
    scanStore.reset()
  }

  // 计算属性
  const jobType = computed(() => scanStore.jobType)
  const details = computed(() => scanStore.details)
  const scanning = computed(() => scanStore.scanning)
  const canSubmit = computed(() => scanStore.canSubmit)

  return {
    jobType,
    details,
    scanning,
    canSubmit,
    setJobType: scanStore.setJobType,
    setWarehouse: scanStore.setWarehouse,
    setToWarehouse: scanStore.setToWarehouse,
    scanCode,
    removeDetail,
    submitOrder,
    reset
  }
}