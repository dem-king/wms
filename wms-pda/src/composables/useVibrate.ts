/**
 * WMS-PDA useVibrate振动反馈组合函数
 * 基于plus.device.vibrate实现短/长振动，设备不支持时静默跳过
 */
import { computed } from 'vue'
import { useAppStore } from '@/store/app'
import { VIBRATE_SHORT_DURATION, VIBRATE_LONG_DURATION } from '@/utils/constants'

/** useVibrate返回类型 */
interface UseVibrateReturn {
  /** 短振动（200ms，成功反馈） */
  shortVibrate: () => void
  /** 长振动（500ms，错误反馈） */
  longVibrate: () => void
  /** 振动开关是否启用 */
  enabled: computed<boolean>
  /** 设置振动开关 */
  setEnabled: (enabled: boolean) => void
}

/**
 * 振动反馈组合函数
 * 提供短振动/长振动操作，读取appStore.vibrateEnabled控制开关
 *
 * @returns 振动操作接口
 */
export function useVibrate(): UseVibrateReturn {
  const appStore = useAppStore()

  /**
   * 执行振动
   * 检查开关状态和设备支持后触发振动，失败时静默跳过
   *
   * @param duration 振动时长（毫秒）
   */
  function vibrate(duration: number): void {
    // 开关关闭时不振动
    if (!appStore.vibrateEnabled) {
      return
    }

    try {
      // 优先使用5+ API（Android原生振动）
      if (typeof plus !== 'undefined' && plus.device && plus.device.vibrate) {
        plus.device.vibrate(duration)
      } else {
        // 降级使用uni API（部分平台支持）
        uni.vibrateShort({
          success: () => {},
          fail: () => {}
        })
      }
    } catch {
      // 设备不支持振动，静默跳过
    }
  }

  /**
   * 短振动（200ms）
   * 用于成功反馈场景：扫码识别成功、单据提交成功
   */
  function shortVibrate(): void {
    vibrate(VIBRATE_SHORT_DURATION)
  }

  /**
   * 长振动（500ms）
   * 用于错误反馈场景：扫码识别失败、操作异常
   */
  function longVibrate(): void {
    vibrate(VIBRATE_LONG_DURATION)
  }

  /** 振动开关是否启用 */
  const enabled = computed(() => appStore.vibrateEnabled)

  /**
   * 设置振动开关
   *
   * @param value 是否启用振动
   */
  function setEnabled(value: boolean): void {
    appStore.setVibrateEnabled(value)
  }

  return {
    shortVibrate,
    longVibrate,
    enabled,
    setEnabled
  }
}