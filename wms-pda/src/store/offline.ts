/**
 * WMS-PDA 离线队列状态管理
 * Pinia store，管理离线操作队列的入队、同步、失败记录等状态
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { OfflineQueueItem, OfflineQueueStatusValue } from '@/utils/constants'
import { OFFLINE_QUEUE_MAX_SIZE, OfflineQueueStatus, RequestTimeout } from '@/utils/constants'
import {
  initDatabase,
  enqueue as dbEnqueue,
  getPendingRecords,
  updateQueueStatus,
  incrementRetryCount,
  getQueueSize as dbGetQueueSize,
  getFailedRecords as dbGetFailedRecords,
  clearSuccessRecords as dbClearSuccessRecords,
  getTotalQueueSize,
  addOperationLog
} from '@/utils/db'
import { useAuthStore } from '@/store/auth'
import { useAppStore } from '@/store/app'

/** 离线操作请求类型 */
export interface OfflineAction {
  /** 记录id（可选，入队时自动生成） */
  id?: number
  /** 请求URL */
  url: string
  /** HTTP方法 */
  method: string
  /** 请求体 */
  body?: unknown
  /** 入队时间戳（毫秒） */
  timestamp: number
  /** 重试次数 */
  retryCount: number
  /** 同步状态 */
  status: string
  /** 失败原因 */
  errorMsg?: string
}

/** 离线队列Store */
export const useOfflineStore = defineStore('offline', () => {
  // ==================== State ====================

  /** 队列中pending记录数 */
  const queueSize = ref<number>(0)
  /** 是否正在同步 */
  const syncing = ref<boolean>(false)
  /** 失败记录列表 */
  const failedRecords = ref<OfflineQueueItem[]>([])

  // ==================== Actions ====================

  /**
   * 初始化离线存储
   * 初始化数据库并从存储恢复队列状态
   */
  async function init(): Promise<void> {
    try {
      await initDatabase()
      // 恢复队列大小
      await refreshQueueSize()
      // 恢复失败记录
      await refreshFailedRecords()
    } catch (error) {
      console.error('离线存储初始化失败:', error)
    }
  }

  /**
   * 刷新队列大小
   * 从数据库/Storage读取当前pending记录数
   */
  async function refreshQueueSize(): Promise<void> {
    try {
      const size = await dbGetQueueSize()
      queueSize.value = size
      // 同步更新appStore中的队列大小
      const appStore = useAppStore()
      appStore.updateOfflineQueueSize(size)
    } catch (error) {
      console.error('获取队列大小失败:', error)
    }
  }

  /**
   * 刷新失败记录
   * 从数据库/Storage读取failed记录
   */
  async function refreshFailedRecords(): Promise<void> {
    try {
      failedRecords.value = await dbGetFailedRecords()
    } catch (error) {
      console.error('获取失败记录失败:', error)
    }
  }

  /**
   * 入队操作请求
   * 将离线操作请求加入队列，容量上限100条
   *
   * @param action 离线操作请求
   * @returns 是否入队成功
   */
  async function enqueue(action: OfflineAction): Promise<boolean> {
    try {
      // 检查队列容量上限
      const totalSize = await getTotalQueueSize()
      if (totalSize >= OFFLINE_QUEUE_MAX_SIZE) {
        uni.showToast({
          title: `离线队列已满（上限${OFFLINE_QUEUE_MAX_SIZE}条）`,
          icon: 'none',
          duration: 2000
        })
        return false
      }

      // 序列化请求体
      const bodyStr = action.body ? JSON.stringify(action.body) : ''

      // 插入数据库
      await dbEnqueue({
        url: action.url,
        method: action.method as 'POST' | 'PUT' | 'PATCH',
        body: bodyStr,
        timestamp: action.timestamp || Date.now(),
        retryCount: action.retryCount || 0,
        status: OfflineQueueStatus.PENDING,
        errorMsg: ''
      })

      // 刷新队列大小
      await refreshQueueSize()

      // 记录操作日志
      await addOperationLog({
        action: 'enqueue',
        module: 'offline',
        detail: `${action.method} ${action.url}`,
        timestamp: Date.now(),
        success: true
      })

      return true
    } catch (error) {
      console.error('入队失败:', error)
      return false
    }
  }

  /**
   * 同步离线队列
   * FIFO顺序逐条同步：先刷新Token（如需），再发送请求
   * 成功标记success，失败标记failed+errorMsg
   */
  async function syncQueue(): Promise<{ successCount: number; failCount: number }> {
    // 防止重复同步
    if (syncing.value) {
      return { successCount: 0, failCount: 0 }
    }

    syncing.value = true
    const appStore = useAppStore()
    appStore.syncing = true

    let successCount = 0
    let failCount = 0

    try {
      // 先刷新Token，避免同步时Token过期
      const authStore = useAuthStore()
      if (authStore.isLoggedIn && authStore.isTokenExpiring()) {
        try {
          await authStore.refreshTokenAction()
        } catch (error) {
          // Token刷新失败，中止同步
          console.error('同步前Token刷新失败，中止同步:', error)
          uni.showToast({
            title: 'Token刷新失败，请重新登录',
            icon: 'none',
            duration: 2000
          })
          return { successCount: 0, failCount: 0 }
        }
      }

      // 获取所有pending记录
      const pendingRecords = await getPendingRecords()

      if (pendingRecords.length === 0) {
        return { successCount: 0, failCount: 0 }
      }

      // 逐条同步
      for (const record of pendingRecords) {
        try {
          // 发送请求
          await sendOfflineRequest(record)
          // 成功：标记为success
          await updateQueueStatus(record.id, OfflineQueueStatus.SUCCESS)
          successCount++
        } catch (error) {
          // 失败：标记为failed，记录errorMsg
          const errorMsg = error instanceof Error ? error.message : '同步失败'
          await updateQueueStatus(record.id, OfflineQueueStatus.FAILED, errorMsg)
          await incrementRetryCount(record.id)
          failCount++
        }
      }

      // 刷新队列状态
      await refreshQueueSize()
      await refreshFailedRecords()

      // 清理已成功记录
      await dbClearSuccessRecords()

      return { successCount, failCount }
    } catch (error) {
      console.error('同步队列异常:', error)
      return { successCount, failCount }
    } finally {
      syncing.value = false
      appStore.syncing = false
    }
  }

  /**
   * 发送离线请求
   * 使用uni.request重新发送队列中的请求
   *
   * @param record 离线队列记录
   */
  function sendOfflineRequest(record: OfflineQueueItem): Promise<unknown> {
    return new Promise((resolve, reject) => {
      // 获取服务器地址和Token
      const serverUrl = uni.getStorageSync('serverUrl') || ''
      const accessToken = uni.getStorageSync('accessToken') || ''
      const fullUrl = record.url.startsWith('http') ? record.url : `${serverUrl}${record.url}`

      // 解析请求体
      let requestData: unknown = {}
      try {
        requestData = record.body ? JSON.parse(record.body) : {}
      } catch {
        requestData = record.body
      }

      uni.request({
        url: fullUrl,
        method: record.method as 'POST' | 'PUT' | 'PATCH',
        data: requestData,
        header: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        },
        timeout: RequestTimeout.DEFAULT,
        success: (res) => {
          if (res.statusCode === 200) {
            const responseData = res.data as { code?: number; msg?: string }
            if (responseData.code === 200) {
              resolve(responseData)
            } else {
              reject(new Error(responseData.msg || '业务异常'))
            }
          } else if (res.statusCode === 401) {
            reject(new Error('Token已过期'))
          } else {
            reject(new Error(`HTTP ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '网络请求失败'))
        }
      })
    })
  }

  /**
   * 获取队列大小
   *
   * @returns pending记录数
   */
  async function getQueueSizeAction(): Promise<number> {
    return dbGetQueueSize()
  }

  /**
   * 获取失败记录
   *
   * @returns failed记录列表
   */
  async function getFailedRecordsAction(): Promise<OfflineQueueItem[]> {
    return dbGetFailedRecords()
  }

  /**
   * 清除已同步成功的记录
   */
  async function clearSuccessRecordsAction(): Promise<void> {
    await dbClearSuccessRecords()
    await refreshQueueSize()
  }

  return {
    // State
    queueSize,
    syncing,
    failedRecords,
    // Actions
    init,
    enqueue,
    syncQueue,
    refreshQueueSize,
    refreshFailedRecords,
    getQueueSizeAction,
    getFailedRecordsAction,
    clearSuccessRecordsAction
  }
})