/**
 * 底图Composable
 * 管理底图加载与透明度
 */

import { ref } from 'vue'
import { getWarehouseBackgroundUrl } from '@/api/warehouse/layout-element'
import type { EntityId } from '@/types/warehouse'

/** 底图加载超时时间(ms) */
const BACKGROUND_LOAD_TIMEOUT = 10000
/** 底图默认透明度 */
const DEFAULT_BACKGROUND_OPACITY = 0.7

/**
 * 底图Composable
 * 提供底图加载、透明度调节、重试等功能
 * @returns 底图状态和操作方法
 */
export function useBackgroundImage() {
  /** 底图Image对象（Konva可用的HTMLImageElement） */
  const backgroundImage = ref<HTMLImageElement | null>(null)
  /** 底图加载状态 */
  const loading = ref(false)
  /** 底图加载错误 */
  const error = ref('')
  /** 底图透明度（0~1，默认0.7） */
  const opacity = ref(DEFAULT_BACKGROUND_OPACITY)

  /** 当前加载的库房ID（用于重试） */
  let currentWarehouseId: EntityId | null = null
  let currentVersion: string | null = null
  /** 超时定时器 */
  let timeoutTimer: ReturnType<typeof setTimeout> | null = null

  /**
   * 加载底图
   * @param warehouseId 库房ID
   * @param version 底图版本号
   */
  async function load(warehouseId: EntityId, version?: string | null) {
    // 清理之前的加载
    cleanup()

    currentWarehouseId = warehouseId
    currentVersion = version ?? null

    if (!version) {
      // 无版本号时不渲染底图
      backgroundImage.value = null
      loading.value = false
      error.value = ''
      return
    }

    loading.value = true
    error.value = ''

    return new Promise<void>((resolve) => {
      const img = new Image()
      img.crossOrigin = 'anonymous'

      // 超时处理
      timeoutTimer = setTimeout(() => {
        img.src = ''
        loading.value = false
        error.value = '底图加载超时，请点击重试'
        resolve()
      }, BACKGROUND_LOAD_TIMEOUT)

      img.onload = () => {
        clearTimeoutTimer()
        backgroundImage.value = img
        loading.value = false
        error.value = ''
        resolve()
      }

      img.onerror = () => {
        clearTimeoutTimer()
        backgroundImage.value = null
        loading.value = false
        error.value = '底图文件无法解析，请检查文件格式'
        resolve()
      }

      // 通过后端代理接口获取底图
      img.src = getWarehouseBackgroundUrl(warehouseId, version)
    })
  }

  /**
   * 重试加载底图
   */
  async function retry() {
    if (currentWarehouseId) {
      await load(currentWarehouseId, currentVersion)
    }
  }

  /**
   * 设置底图透明度
   * @param value 透明度值(0~1)
   */
  function setOpacity(value: number) {
    opacity.value = Math.min(Math.max(value, 0), 1)
  }

  /**
   * 清除底图
   */
  function clear() {
    cleanup()
    backgroundImage.value = null
    loading.value = false
    error.value = ''
  }

  /** 清理超时定时器 */
  function clearTimeoutTimer() {
    if (timeoutTimer !== null) {
      window.clearTimeout(timeoutTimer)
      timeoutTimer = null
    }
  }

  /** 清理资源 */
  function cleanup() {
    clearTimeoutTimer()
  }

  return {
    backgroundImage,
    loading,
    error,
    opacity,
    load,
    retry,
    setOpacity,
    clear,
  }
}