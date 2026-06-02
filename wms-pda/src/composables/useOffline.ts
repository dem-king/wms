/**
 * WMS-PDA useOffline离线容错组合函数
 * 监听网络变化，离线时禁止扫码，网络恢复时自动同步队列
 */
import { computed, onUnmounted } from 'vue'
import { useAppStore } from '@/store/app'
import { useOfflineStore } from '@/store/offline'
import type { OfflineAction } from '@/store/offline'

/** useOffline返回类型 */
interface UseOfflineReturn {
  /** 是否在线 */
  isOnline: computed<boolean>
  /** 队列中pending记录数 */
  queueSize: computed<number>
  /** 是否正在同步 */
  syncing: computed<boolean>
  /** 入队操作请求 */
  enqueue: (action: OfflineAction) => Promise<boolean>
  /** 同步离线队列 */
  syncQueue: () => Promise<{ successCount: number; failCount: number }>
  /** 初始化网络监听 */
  initListener: () => void
  /** 销毁网络监听 */
  destroyListener: () => void
  /** 检查是否允许扫码（离线时禁止） */
  canScan: () => boolean
}

/** 网络状态变化回调引用（用于销毁时移除） */
let networkChangeCallback: ((result: { isConnected: boolean; networkType: string }) => void) | null = null

/**
 * 离线容错组合函数
 * 监听网络变化，离线时更新状态并禁止扫码，网络恢复时自动同步队列
 *
 * @returns 离线容错操作接口
 */
export function useOffline(): UseOfflineReturn {
  const appStore = useAppStore()
  const offlineStore = useOfflineStore()

  /** 是否在线 */
  const isOnline = computed(() => appStore.isOnline)

  /** 队列中pending记录数 */
  const queueSize = computed(() => offlineStore.queueSize)

  /** 是否正在同步 */
  const syncing = computed(() => offlineStore.syncing)

  /**
   * 初始化网络监听
   * 监听网络状态变化：离线时更新状态，恢复时触发自动同步
   */
  function initListener(): void {
    // 先获取当前网络状态
    uni.getNetworkType({
      success: (res) => {
        const online = res.networkType !== 'none'
        appStore.isOnline = online
      }
    })

    // 定义网络状态变化回调
    networkChangeCallback = async (result: { isConnected: boolean; networkType: string }) => {
      const wasOnline = appStore.isOnline
      const nowOnline = result.isConnected

      // 更新在线状态
      appStore.isOnline = nowOnline

      if (!nowOnline) {
        // 网络离线：提示用户
        uni.showToast({
          title: '网络已断开，进入离线模式',
          icon: 'none',
          duration: 2000
        })
      } else if (!wasOnline && nowOnline) {
        // 网络恢复：从离线变为在线，触发自动同步
        uni.showToast({
          title: '网络已恢复',
          icon: 'success',
          duration: 1500
        })

        // 延迟1秒后触发同步，等待网络稳定
        setTimeout(async () => {
          await autoSync()
        }, 1000)
      }
    }

    // 注册网络状态变化监听
    uni.onNetworkStatusChange(networkChangeCallback)
  }

  /**
   * 自动同步离线队列
   * 网络恢复后自动触发，同步完成后通知结果
   */
  async function autoSync(): Promise<void> {
    // 检查是否有待同步记录
    if (offlineStore.queueSize <= 0) {
      return
    }

    try {
      const result = await offlineStore.syncQueue()

      // 同步完成后通知结果
      if (result.successCount > 0 || result.failCount > 0) {
        uni.showToast({
          title: `已同步${result.successCount}条成功，${result.failCount}条失败`,
          icon: result.failCount > 0 ? 'none' : 'success',
          duration: 3000
        })
      }
    } catch (error) {
      console.error('自动同步失败:', error)
    }
  }

  /**
   * 销毁网络监听
   * 组件卸载时调用，清理监听回调
   */
  function destroyListener(): void {
    // uni.onNetworkStatusChange的回调无法直接移除
    // 将回调置空，避免重复触发
    networkChangeCallback = null
  }

  /**
   * 检查是否允许扫码
   * 离线时禁止扫码识别，提示用户
   *
   * @returns 是否允许扫码
   */
  function canScan(): boolean {
    if (!appStore.isOnline) {
      uni.showToast({
        title: '当前离线，无法执行扫码识别',
        icon: 'none',
        duration: 2000
      })
      return false
    }
    return true
  }

  /**
   * 入队操作请求
   * 离线时允许单据提交入队
   *
   * @param action 离线操作请求
   * @returns 是否入队成功
   */
  async function enqueue(action: OfflineAction): Promise<boolean> {
    const success = await offlineStore.enqueue(action)
    if (success) {
      uni.showToast({
        title: '已加入离线队列，网络恢复后自动同步',
        icon: 'none',
        duration: 2000
      })
    }
    return success
  }

  /**
   * 同步离线队列
   * 手动触发同步
   *
   * @returns 同步结果
   */
  async function syncQueue(): Promise<{ successCount: number; failCount: number }> {
    if (!appStore.isOnline) {
      uni.showToast({
        title: '当前离线，无法同步',
        icon: 'none',
        duration: 2000
      })
      return { successCount: 0, failCount: 0 }
    }

    const result = await offlineStore.syncQueue()

    // 通知同步结果
    if (result.successCount > 0 || result.failCount > 0) {
      uni.showToast({
        title: `已同步${result.successCount}条成功，${result.failCount}条失败`,
        icon: result.failCount > 0 ? 'none' : 'success',
        duration: 3000
      })
    } else {
      uni.showToast({
        title: '没有待同步的记录',
        icon: 'none',
        duration: 1500
      })
    }

    return result
  }

  // 组件卸载时自动销毁监听
  onUnmounted(() => {
    destroyListener()
  })

  return {
    isOnline,
    queueSize,
    syncing,
    enqueue,
    syncQueue,
    initListener,
    destroyListener,
    canScan
  }
}