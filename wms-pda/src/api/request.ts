/**
 * WMS-PDA 基础请求封装
 * 基于uni.request封装，支持泛型响应类型、Token自动注入、Token自动刷新、离线容错
 */
import type { ApiResponse } from '@/utils/constants'
import { RequestTimeout, TOKEN_EXPIRE_THRESHOLD } from '@/utils/constants'
import type { OfflineAction } from '@/store/offline'

/** 请求方法类型 */
type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'

/** 请求配置 */
interface RequestConfig {
  /** 请求URL（相对路径，如 /api/auth/login） */
  url: string
  /** HTTP方法 */
  method?: HttpMethod
  /** 请求数据 */
  data?: Record<string, unknown> | string
  /** 请求头 */
  header?: Record<string, string>
  /** 超时时间（毫秒） */
  timeout?: number
  /** 是否需要Token（默认true） */
  needToken?: boolean
}

/** Token刷新状态管理：单例刷新模式 */
let isRefreshing = false
/** 等待Token刷新的请求队列 */
let pendingRequests: Array<(token: string) => void> = []

/**
 * 获取存储的accessToken
 */
function getAccessToken(): string {
  try {
    return uni.getStorageSync('accessToken') || ''
  } catch {
    return ''
  }
}

/**
 * 获取存储的refreshToken
 */
function getRefreshToken(): string {
  try {
    return uni.getStorageSync('refreshToken') || ''
  } catch {
    return ''
  }
}

/**
 * 获取服务器地址
 */
function getServerUrl(): string {
  try {
    return uni.getStorageSync('serverUrl') || ''
  } catch {
    return ''
  }
}

/**
 * Token刷新请求（避免循环依赖，直接使用uni.request）
 */
function refreshAccessToken(): Promise<string> {
  const refreshToken = getRefreshToken()
  const serverUrl = getServerUrl()

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${serverUrl}/api/auth/token/refresh`,
      method: 'POST',
      data: { refreshToken },
      header: { 'Content-Type': 'application/json' },
      timeout: RequestTimeout.DEFAULT,
      success: (res) => {
        const responseData = res.data as ApiResponse<{ accessToken: string; expiresIn: number }>
        if (responseData.code === 200 && responseData.data) {
          const { accessToken, expiresIn } = responseData.data
          // 存储新Token
          uni.setStorageSync('accessToken', accessToken)
          // 计算过期时间戳
          const expireTimestamp = Date.now() + expiresIn * 1000
          uni.setStorageSync('tokenExpireTime', expireTimestamp)
          resolve(accessToken)
        } else {
          // refreshToken也过期，清除认证信息，跳转登录页
          clearAuthAndRedirect()
          reject(new Error('Token刷新失败'))
        }
      },
      fail: () => {
        clearAuthAndRedirect()
        reject(new Error('Token刷新请求失败'))
      }
    })
  })
}

/**
 * 清除认证信息并跳转登录页
 */
function clearAuthAndRedirect(): void {
  uni.removeStorageSync('accessToken')
  uni.removeStorageSync('refreshToken')
  uni.removeStorageSync('tokenExpireTime')
  uni.removeStorageSync('userInfo')
  uni.removeStorageSync('permissions')
  uni.removeStorageSync('roles')

  // 跳转登录页
  uni.reLaunch({
    url: '/pages/login/index'
  })
}

/**
 * 处理Token刷新：单例刷新模式
 * 多个并发请求仅发送一次刷新请求，其余请求排队等待结果
 */
async function handleTokenRefresh(): Promise<string> {
  if (isRefreshing) {
    // 已有刷新请求进行中，排队等待
    return new Promise((resolve) => {
      pendingRequests.push((token: string) => {
        resolve(token)
      })
    })
  }

  isRefreshing = true
  try {
    const newToken = await refreshAccessToken()
    // 通知所有等待中的请求
    pendingRequests.forEach((callback) => callback(newToken))
    pendingRequests = []
    return newToken
  } catch (error) {
    // 刷新失败，拒绝所有等待中的请求
    pendingRequests = []
    throw error
  } finally {
    isRefreshing = false
  }
}

/**
 * 检查Token是否即将过期（过期前5分钟）
 * 直接读取本地存储，避免循环依赖authStore
 */
function isTokenExpiringCheck(): boolean {
  try {
    const expireTime = uni.getStorageSync('tokenExpireTime')
    if (!expireTime) {
      return true
    }
    const remaining = expireTime - Date.now()
    return remaining < TOKEN_EXPIRE_THRESHOLD
  } catch {
    return true
  }
}

/**
 * 统一错误提示
 */
function showErrorToast(message: string): void {
  uni.showToast({
    title: message,
    icon: 'none',
    duration: 2000
  })
}

/**
 * 基础请求方法
 * 支持泛型响应类型、Token自动注入、Token自动刷新
 *
 * @param config 请求配置
 * @returns Promise<T> 响应数据中的data字段
 */
export function request<T = unknown>(config: RequestConfig): Promise<T> {
  const {
    url,
    method = 'GET',
    data,
    header = {},
    timeout = RequestTimeout.DEFAULT,
    needToken = true
  } = config

  const serverUrl = getServerUrl()
  const fullUrl = url.startsWith('http') ? url : `${serverUrl}${url}`

  // 请求拦截：Token自动注入
  const requestHeader: Record<string, string> = {
    'Content-Type': 'application/json',
    ...header
  }

  if (needToken) {
    const token = getAccessToken()
    if (token) {
      requestHeader['Authorization'] = `Bearer ${token}`

      // 主动Token刷新：检查Token是否即将过期（过期前5分钟）
      // 仅在非刷新请求本身时触发，避免循环
      if (!url.includes('/auth/token/refresh') && isTokenExpiringCheck()) {
        // 异步刷新，不阻塞当前请求（当前请求仍用旧Token，若401再触发同步刷新）
        handleTokenRefresh().catch(() => {
          // 静默处理，401时会再次触发
        })
      }
    }
  }

  return new Promise<T>((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method,
      data,
      header: requestHeader,
      timeout,
      success: async (res) => {
        const statusCode = res.statusCode

        // 响应拦截：处理HTTP状态码
        if (statusCode === 401) {
          // Token过期，尝试刷新
          try {
            const newToken = await handleTokenRefresh()
            // 使用新Token重试原请求
            requestHeader['Authorization'] = `Bearer ${newToken}`
            uni.request({
              url: fullUrl,
              method,
              data,
              header: requestHeader,
              timeout,
              success: (retryRes) => {
                const retryData = retryRes.data as ApiResponse<T>
                if (retryData.code === 200) {
                  resolve(retryData.data)
                } else {
                  showErrorToast(retryData.msg || '请求失败')
                  reject(new Error(retryData.msg || '请求失败'))
                }
              },
              fail: (err) => {
                showErrorToast('请求超时，请重试')
                reject(err)
              }
            })
          } catch {
            // Token刷新失败，已在handleTokenRefresh中处理跳转
            reject(new Error('登录已过期，请重新登录'))
          }
          return
        }

        if (statusCode === 403) {
          showErrorToast('无操作权限')
          reject(new Error('无操作权限'))
          return
        }

        if (statusCode === 404) {
          showErrorToast('数据不存在')
          reject(new Error('数据不存在'))
          return
        }

        if (statusCode === 500) {
          showErrorToast('服务器异常，请稍后重试')
          reject(new Error('服务器异常'))
          return
        }

        // 正常响应：统一解析{code, msg, data}格式
        const responseData = res.data as ApiResponse<T>
        if (responseData.code === 200) {
          resolve(responseData.data)
        } else if (responseData.code === 401) {
          // 业务层401：Token无效
          try {
            const newToken = await handleTokenRefresh()
            requestHeader['Authorization'] = `Bearer ${newToken}`
            uni.request({
              url: fullUrl,
              method,
              data,
              header: requestHeader,
              timeout,
              success: (retryRes) => {
                const retryData = retryRes.data as ApiResponse<T>
                if (retryData.code === 200) {
                  resolve(retryData.data)
                } else {
                  showErrorToast(retryData.msg || '请求失败')
                  reject(new Error(retryData.msg || '请求失败'))
                }
              },
              fail: (err) => {
                showErrorToast('请求超时，请重试')
                reject(err)
              }
            })
          } catch {
            reject(new Error('登录已过期，请重新登录'))
          }
        } else {
          // 其他业务异常
          showErrorToast(responseData.msg || '请求失败')
          reject(new Error(responseData.msg || '请求失败'))
        }
      },
      fail: (err) => {
        // 网络请求失败
        const errMsg = err.errMsg || ''
        if (errMsg.includes('timeout')) {
          showErrorToast('请求超时，请重试')
          reject(err)
        } else if (errMsg.includes('network') || errMsg.includes('connect')) {
          // 网络不可达：离线容错处理
          handleOfflineRequest(url, method, data).then((handled) => {
            if (handled) {
              // 已入队离线队列，resolve让调用方不报错
              resolve(undefined as T)
            } else {
              showErrorToast('网络不可用')
              reject(err)
            }
          })
        } else {
          showErrorToast('网络请求失败')
          reject(err)
        }
      }
    })
  })
}

/**
 * GET请求快捷方法
 */
export function get<T = unknown>(url: string, data?: Record<string, unknown>, timeout?: number): Promise<T> {
  return request<T>({ url, method: 'GET', data, timeout })
}

/**
 * POST请求快捷方法
 */
export function post<T = unknown>(url: string, data?: Record<string, unknown> | string, timeout?: number): Promise<T> {
  return request<T>({ url, method: 'POST', data, timeout })
}

/**
 * PUT请求快捷方法
 */
export function put<T = unknown>(url: string, data?: Record<string, unknown>, timeout?: number): Promise<T> {
  return request<T>({ url, method: 'PUT', data, timeout })
}

/**
 * DELETE请求快捷方法
 */
export function del<T = unknown>(url: string, data?: Record<string, unknown>, timeout?: number): Promise<T> {
  return request<T>({ url, method: 'DELETE', data, timeout })
}

/**
 * PATCH请求快捷方法
 */
export function patch<T = unknown>(url: string, data?: Record<string, unknown>, timeout?: number): Promise<T> {
  return request<T>({ url, method: 'PATCH', data, timeout })
}

/**
 * 扫码专用请求（超时5秒）
 */
export function scanRequest<T = unknown>(config: Omit<RequestConfig, 'timeout'>): Promise<T> {
  return request<T>({ ...config, timeout: RequestTimeout.SCAN })
}

// ==================== 离线容错集成 ====================

/** 可入队的写操作方法 */
const OFFLINE_METHODS: HttpMethod[] = ['POST', 'PUT', 'PATCH']

/**
 * 离线容错请求处理
 * 网络不可达时判断请求类型：
 * - POST/PUT/PATCH操作：自动入队离线队列
 * - GET/DELETE请求：直接提示"网络不可用"
 *
 * @param url 请求URL
 * @param method HTTP方法
 * @param data 请求数据
 * @returns 是否已入队离线队列
 */
async function handleOfflineRequest(
  url: string,
  method: HttpMethod,
  data?: Record<string, unknown> | string
): Promise<boolean> {
  // GET/DELETE请求不可入队，直接返回false
  if (!OFFLINE_METHODS.includes(method)) {
    return false
  }

  // 认证相关请求不入队（登录/登出/刷新Token）
  if (url.includes('/auth/')) {
    return false
  }

  try {
    // 动态导入offlineStore，避免循环依赖
    const { useOfflineStore } = await import('@/store/offline')
    const offlineStore = useOfflineStore()

    const action: OfflineAction = {
      url,
      method,
      body: data,
      timestamp: Date.now(),
      retryCount: 0,
      status: 'pending'
    }

    const success = await offlineStore.enqueue(action)
    if (success) {
      // 入队成功，提示用户
      uni.showToast({
        title: '已加入离线队列',
        icon: 'none',
        duration: 1500
      })
      return true
    }
  } catch (error) {
    console.error('离线入队失败:', error)
  }

  return false
}