/**
 * WMS-PDA useSound声音反馈组合函数
 * 基于uni.createInnerAudioContext实现成功/错误/提示音播放，播放失败时静默跳过
 */
import { computed } from 'vue'
import { useAppStore } from '@/store/app'

/** 提示音文件路径常量 */
const SOUND_PATHS = {
  /** 成功提示音 */
  SUCCESS: '/static/sounds/success.mp3',
  /** 错误提示音 */
  ERROR: '/static/sounds/error.mp3',
  /** 短提示音（滴声） */
  BEEP: '/static/sounds/beep.mp3'
} as const

/** useSound返回类型 */
interface UseSoundReturn {
  /** 播放成功提示音 */
  playSuccess: () => void
  /** 播放错误提示音 */
  playError: () => void
  /** 播放短提示音（滴声） */
  playBeep: () => void
  /** 声音开关是否启用 */
  enabled: computed<boolean>
  /** 设置声音开关 */
  setEnabled: (enabled: boolean) => void
}

/**
 * 声音反馈组合函数
 * 提供成功/错误/提示音播放，读取appStore.soundEnabled控制开关
 *
 * @returns 声音操作接口
 */
export function useSound(): UseSoundReturn {
  const appStore = useAppStore()

  /**
   * 播放指定提示音
   * 检查开关状态后创建音频上下文播放，播放完成自动销毁，失败时静默跳过
   *
   * @param src 音频文件路径
   */
  function play(src: string): void {
    // 开关关闭时不播放
    if (!appStore.soundEnabled) {
      return
    }

    try {
      const audioContext = uni.createInnerAudioContext()
      audioContext.src = src

      // 播放完成后自动销毁音频上下文，避免内存泄漏
      audioContext.onStop(() => {
        audioContext.destroy()
      })
      audioContext.onEnded(() => {
        audioContext.destroy()
      })
      audioContext.onError(() => {
        // 播放失败，静默跳过并销毁
        audioContext.destroy()
      })

      audioContext.play()
    } catch {
      // 创建音频上下文失败，静默跳过
    }
  }

  /**
   * 播放成功提示音
   * 用于扫码识别成功、单据提交成功等场景
   */
  function playSuccess(): void {
    play(SOUND_PATHS.SUCCESS)
  }

  /**
   * 播放错误提示音
   * 用于扫码识别失败、操作异常等场景
   */
  function playError(): void {
    play(SOUND_PATHS.ERROR)
  }

  /**
   * 播放短提示音（滴声）
   * 用于RFID读取到新标签等轻量提示场景
   */
  function playBeep(): void {
    play(SOUND_PATHS.BEEP)
  }

  /** 声音开关是否启用 */
  const enabled = computed(() => appStore.soundEnabled)

  /**
   * 设置声音开关
   *
   * @param value 是否启用声音
   */
  function setEnabled(value: boolean): void {
    appStore.setSoundEnabled(value)
  }

  return {
    playSuccess,
    playError,
    playBeep,
    enabled,
    setEnabled
  }
}