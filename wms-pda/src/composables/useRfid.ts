/**
 * WMS-PDA useRfid RFID读取组合函数
 * 封装RFID原生插件调用，提供初始化、批量读取、停止、释放等操作
 * 内置去重逻辑（Set<string>），每读取到新EPC码播放playBeep()
 * 原生插件不可用时提供降级方案，组件卸载时自动release
 */
import { ref, onBeforeUnmount } from 'vue'
import { useAppStore } from '@/store/app'
import { useSound } from '@/composables/useSound'
import { RFID_POWER_MIN, RFID_POWER_MAX } from '@/utils/constants'
import type { WmsRfidPlugin, RfidReadOptions } from '@/utils/constants'

/** startRead回调选项 */
interface RfidReadCallbacks {
  /** 实时读取回调：每读取到一个新EPC码触发 */
  onRead: (epc: string) => void
  /** 读取完成回调：停止读取后触发，传入所有去重后的EPC码列表 */
  onComplete: (tags: string[]) => void
}

/** useRfid返回类型 */
interface UseRfidReturn {
  /** 是否正在读取 */
  reading: Ref<boolean>
  /** 已读取标签数量 */
  readCount: Ref<number>
  /** RFID硬件是否可用 */
  hardwareAvailable: Ref<boolean>
  /** 已读取的EPC码集合（去重） */
  epcTags: Ref<Set<string>>
  /** 初始化RFID模块 */
  init: () => Promise<boolean>
  /** 开始批量读取 */
  startRead: (callbacks: RfidReadCallbacks) => void
  /** 停止批量读取 */
  stopRead: () => void
  /** 释放RFID资源 */
  release: () => void
}

import type { Ref } from 'vue'

/**
 * RFID读取组合函数
 * 封装原生插件wms-rfid的调用，提供初始化、批量读取、停止、释放等操作
 * 原生插件不可用时hardwareAvailable=false，页面可据此提供降级方案
 * 组件卸载时自动调用release释放RFID资源
 *
 * @returns RFID操作接口
 */
export function useRfid(): UseRfidReturn {
  const appStore = useAppStore()
  const { playBeep } = useSound()

  // ==================== 响应式状态 ====================

  /** 是否正在读取 */
  const reading = ref<boolean>(false)
  /** 已读取标签数量 */
  const readCount = ref<number>(0)
  /** RFID硬件是否可用 */
  const hardwareAvailable = ref<boolean>(false)
  /** 已读取的EPC码集合（去重） */
  const epcTags = ref<Set<string>>(new Set<string>())

  // ==================== 内部变量 ====================

  /** RFID原生插件实例 */
  let rfidPlugin: WmsRfidPlugin | null = null
  /** 是否已初始化 */
  let initialized = false
  /** 读取回调引用 */
  let readCallbacks: RfidReadCallbacks | null = null

  /**
   * 加载RFID原生插件
   * 使用条件编译，仅在APP-PLUS平台加载原生插件
   */
  function loadPlugin(): WmsRfidPlugin | null {
    // #ifdef APP-PLUS
    try {
      const plugin = uni.requireNativePlugin('wms-rfid') as WmsRfidPlugin | null
      if (plugin) {
        return plugin
      }
      console.warn('RFID原生插件wms-rfid加载失败：插件返回null')
      return null
    } catch (error) {
      console.error('RFID原生插件wms-rfid加载异常:', error)
      return null
    }
    // #endif

    // #ifndef APP-PLUS
    // 非APP平台，原生插件不可用
    return null
    // #endif
  }

  /**
   * 初始化RFID模块
   * 加载原生插件并调用init方法，检测硬件可用性
   *
   * @returns 是否初始化成功（硬件可用）
   */
  function init(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      // 加载原生插件
      rfidPlugin = loadPlugin()

      if (!rfidPlugin) {
        // 原生插件不可用，标记硬件不可用
        hardwareAvailable.value = false
        console.warn('RFID原生插件不可用，将使用降级方案')
        resolve(false)
        return
      }

      try {
        // 调用原生插件初始化
        rfidPlugin.init((result: { success: boolean; msg: string }) => {
          if (result.success) {
            initialized = true
            // 初始化成功后检测硬件可用性
            checkHardware().then((available) => {
              resolve(available)
            })
          } else {
            // 初始化失败
            initialized = false
            hardwareAvailable.value = false
            console.error('RFID模块初始化失败:', result.msg)
            uni.showToast({
              title: 'RFID模块异常，请检查设备',
              icon: 'none',
              duration: 2000
            })
            resolve(false)
          }
        })
      } catch (error) {
        // 调用init异常
        initialized = false
        hardwareAvailable.value = false
        console.error('RFID模块初始化异常:', error)
        uni.showToast({
          title: 'RFID模块异常，请检查设备',
          icon: 'none',
          duration: 2000
        })
        resolve(false)
      }
    })
  }

  /**
   * 检测RFID硬件是否可用
   *
   * @returns 硬件是否可用
   */
  function checkHardware(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      if (!rfidPlugin) {
        hardwareAvailable.value = false
        resolve(false)
        return
      }

      try {
        rfidPlugin.checkHardware((result: { available: boolean; msg: string }) => {
          hardwareAvailable.value = result.available
          if (!result.available) {
            console.warn('RFID硬件不可用:', result.msg)
          }
          resolve(result.available)
        })
      } catch (error) {
        hardwareAvailable.value = false
        console.error('RFID硬件检测异常:', error)
        resolve(false)
      }
    })
  }

  /**
   * 开始批量读取
   * 清空之前的读取结果，调用原生插件startBatchRead
   * 读取到新EPC码时去重并播放beep提示音
   *
   * @param callbacks 读取回调：onRead(每读到新EPC)、onComplete(停止后全量回调)
   */
  function startRead(callbacks: RfidReadCallbacks): void {
    // 保存回调引用
    readCallbacks = callbacks

    // 清空之前的读取结果
    epcTags.value = new Set<string>()
    readCount.value = 0
    reading.value = true

    if (!rfidPlugin || !initialized) {
      // 原生插件不可用，提示降级方案
      console.warn('RFID插件未初始化，无法开始读取')
      reading.value = false
      uni.showToast({
        title: 'RFID模块不可用，请使用手动输入',
        icon: 'none',
        duration: 2000
      })
      return
    }

    try {
      // 构建读取参数：使用appStore中配置的功率
      const power = Math.max(RFID_POWER_MIN, Math.min(RFID_POWER_MAX, appStore.rfidPower))
      const options: RfidReadOptions = {
        power,
        timeout: 0,   // 不限时，由用户手动停止
        repeat: false  // 不重复读取同一标签
      }

      // 设置读取功率
      rfidPlugin.setPower(power)

      // 开始批量读取
      rfidPlugin.startBatchRead(
        options,
        // onRead回调：每读取到一个EPC码
        (epcCode: string) => {
          if (!epcCode) {
            return
          }

          // 去重检查：Set中已存在则跳过
          if (epcTags.value.has(epcCode)) {
            return
          }

          // 新EPC码：加入集合、更新计数
          epcTags.value.add(epcCode)
          readCount.value = epcTags.value.size

          // 播放beep提示音
          playBeep()

          // 触发onRead回调
          if (readCallbacks?.onRead) {
            readCallbacks.onRead(epcCode)
          }
        },
        // onComplete回调：读取完成
        (_totalCount: number) => {
          // 原生插件自动停止时触发
          reading.value = false

          // 触发onComplete回调，传入所有去重后的EPC码
          if (readCallbacks?.onComplete) {
            const tags = Array.from(epcTags.value)
            readCallbacks.onComplete(tags)
          }
        }
      )
    } catch (error) {
      // 调用startBatchRead异常
      reading.value = false
      console.error('RFID开始读取异常:', error)
      uni.showToast({
        title: 'RFID模块异常，请检查设备',
        icon: 'none',
        duration: 2000
      })
    }
  }

  /**
   * 停止批量读取
   * 调用原生插件stopBatchRead，保留已读取数据
   * 停止后触发onComplete回调
   */
  function stopRead(): void {
    if (!reading.value) {
      return
    }

    reading.value = false

    if (rfidPlugin) {
      try {
        rfidPlugin.stopBatchRead()
      } catch (error) {
        console.error('RFID停止读取异常:', error)
      }
    }

    // 触发onComplete回调，传入所有去重后的EPC码
    if (readCallbacks?.onComplete) {
      const tags = Array.from(epcTags.value)
      readCallbacks.onComplete(tags)
    }
  }

  /**
   * 释放RFID资源
   * 调用原生插件release，重置内部状态
   */
  function release(): void {
    // 先停止读取
    if (reading.value) {
      stopRead()
    }

    if (rfidPlugin) {
      try {
        rfidPlugin.release()
      } catch (error) {
        console.error('RFID释放资源异常:', error)
      }
    }

    // 重置内部状态
    rfidPlugin = null
    initialized = false
    hardwareAvailable.value = false
    epcTags.value = new Set<string>()
    readCount.value = 0
    readCallbacks = null
  }

  // 组件卸载时自动释放RFID资源
  onBeforeUnmount(() => {
    release()
  })

  return {
    reading,
    readCount,
    hardwareAvailable,
    epcTags,
    init,
    startRead,
    stopRead,
    release
  }
}