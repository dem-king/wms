/**
 * WMS-PDA 应用全局状态管理
 * Pinia store，管理网络状态、振动/声音开关、离线队列、RFID功率等全局配置
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { RFID_DEFAULT_POWER } from '@/utils/constants'

/** 本地存储Key常量 */
const STORAGE_KEYS = {
  /** 振动开关存储Key */
  VIBRATE_ENABLED: 'vibrateEnabled',
  /** 声音开关存储Key */
  SOUND_ENABLED: 'soundEnabled',
  /** RFID功率存储Key */
  RFID_POWER: 'rfidPower'
} as const

/** 应用全局状态Store */
export const useAppStore = defineStore('app', () => {
  // ==================== State ====================

  /** 是否在线 */
  const isOnline = ref<boolean>(true)
  /** 振动反馈开关 */
  const vibrateEnabled = ref<boolean>(true)
  /** 声音反馈开关 */
  const soundEnabled = ref<boolean>(true)
  /** 离线队列大小 */
  const offlineQueueSize = ref<number>(0)
  /** 是否正在同步离线队列 */
  const syncing = ref<boolean>(false)
  /** RFID读取功率（5-30） */
  const rfidPower = ref<number>(RFID_DEFAULT_POWER)

  // ==================== 初始化：从本地存储恢复开关状态 ====================

  /**
   * 从本地存储恢复振动/声音开关和RFID功率
   * 应用启动时调用
   */
  function initFromStorage(): void {
    try {
      const storedVibrate = uni.getStorageSync(STORAGE_KEYS.VIBRATE_ENABLED)
      if (storedVibrate !== '' && storedVibrate !== undefined) {
        vibrateEnabled.value = !!storedVibrate
      }

      const storedSound = uni.getStorageSync(STORAGE_KEYS.SOUND_ENABLED)
      if (storedSound !== '' && storedSound !== undefined) {
        soundEnabled.value = !!storedSound
      }

      const storedRfidPower = uni.getStorageSync(STORAGE_KEYS.RFID_POWER)
      if (storedRfidPower !== '' && storedRfidPower !== undefined) {
        const power = Number(storedRfidPower)
        if (!isNaN(power) && power >= 5 && power <= 30) {
          rfidPower.value = power
        }
      }
    } catch (error) {
      console.error('恢复应用配置失败:', error)
    }
  }

  // ==================== Actions ====================

  /**
   * 初始化网络状态监听
   * 基于uni.onNetworkStatusChange监听网络变化，更新isOnline状态
   */
  function initNetworkListener(): void {
    // 先获取当前网络状态
    uni.getNetworkType({
      success: (res) => {
        isOnline.value = res.networkType !== 'none'
      }
    })

    // 监听网络状态变化
    uni.onNetworkStatusChange((res) => {
      isOnline.value = res.isConnected
    })
  }

  /**
   * 设置振动反馈开关
   * 持久化至本地存储
   *
   * @param enabled 是否启用振动
   */
  function setVibrateEnabled(enabled: boolean): void {
    vibrateEnabled.value = enabled
    uni.setStorageSync(STORAGE_KEYS.VIBRATE_ENABLED, enabled)
  }

  /**
   * 设置声音反馈开关
   * 持久化至本地存储
   *
   * @param enabled 是否启用声音
   */
  function setSoundEnabled(enabled: boolean): void {
    soundEnabled.value = enabled
    uni.setStorageSync(STORAGE_KEYS.SOUND_ENABLED, enabled)
  }

  /**
   * 更新离线队列大小
   *
   * @param size 当前队列中的待同步记录数
   */
  function updateOfflineQueueSize(size: number): void {
    offlineQueueSize.value = size
  }

  /**
   * 设置RFID读取功率
   * 功率范围5-30，超出范围时自动修正
   * 持久化至本地存储
   *
   * @param power 功率值
   */
  function setRfidPower(power: number): void {
    // 功率范围修正
    const clampedPower = Math.max(5, Math.min(30, power))
    rfidPower.value = clampedPower
    uni.setStorageSync(STORAGE_KEYS.RFID_POWER, clampedPower)
  }

  // 初始化时恢复本地存储配置
  initFromStorage()

  return {
    // State
    isOnline,
    vibrateEnabled,
    soundEnabled,
    offlineQueueSize,
    syncing,
    rfidPower,
    // Actions
    initNetworkListener,
    setVibrateEnabled,
    setSoundEnabled,
    updateOfflineQueueSize,
    setRfidPower
  }
})