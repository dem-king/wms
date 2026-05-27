import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { clearAuth, getRefreshToken, getToken, setRefreshToken, setToken } from '@/utils/auth'
import router from '@/router'

interface R<T = unknown> {
  code: number
  msg: string
  data: T
}

interface TokenResp {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

interface RetryableRequestConfig extends AxiosRequestConfig {
  _retry?: boolean
}

interface PendingRequest {
  config: RetryableRequestConfig
  resolve: (value: unknown) => void
  reject: (reason?: unknown) => void
}

let isRefreshing = false
let pendingRequests: PendingRequest[] = []

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  ((response: AxiosResponse<R>) => {
    if (response.config.responseType === 'blob') {
      return response.data as unknown as Blob
    }
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res as unknown as AxiosResponse<R>
  }) as (value: AxiosResponse<R>) => AxiosResponse<R> | Promise<AxiosResponse<R>>,
  async (error) => {
    const originalRequest = error.config as RetryableRequestConfig
    if (error.response?.status === 401 && !originalRequest._retry) {
      const refreshTokenValue = getRefreshToken()
      if (!refreshTokenValue) {
        clearAuthAndRedirect(error)
        return Promise.reject(error)
      }
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pendingRequests.push({
            config: originalRequest,
            resolve,
            reject,
          })
        })
      }
      isRefreshing = true
      originalRequest._retry = true
      try {
        const res = await axios.post<R<TokenResp>>('/api/auth/token/refresh', {
          refreshToken: refreshTokenValue
        })
        const resData = res.data
        if (resData.code === 200) {
          const { accessToken, refreshToken: newRefreshToken } = resData.data
          setToken(accessToken)
          setRefreshToken(newRefreshToken)
          flushPendingRequests(accessToken)
          originalRequest.headers = {
            ...originalRequest.headers,
            Authorization: `Bearer ${accessToken}`
          }
          originalRequest.headers['Authorization'] = `Bearer ${accessToken}`
          return service(originalRequest)
        } else {
          clearAuthAndRedirect(error)
          return Promise.reject(error)
        }
      } catch {
        clearAuthAndRedirect(error)
        return Promise.reject(error)
      } finally {
        isRefreshing = false
      }
    }
    const msg = error.response?.data?.msg || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

function flushPendingRequests(accessToken: string) {
  pendingRequests.forEach(({ config, resolve }) => {
    config._retry = true
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${accessToken}`
    }
    resolve(service(config))
  })
  pendingRequests = []
}

function rejectPendingRequests(reason: unknown) {
  pendingRequests.forEach(({ reject }) => reject(reason))
  pendingRequests = []
}

function clearAuthAndRedirect(reason?: unknown) {
  clearAuth()
  rejectPendingRequests(reason)
  router.push('/login')
}

export default service

export function get<T = unknown>(url: string, params?: Record<string, unknown> | object, config?: AxiosRequestConfig): Promise<R<T>> {
  return service.get(url, { params, ...config })
}

export function post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<R<T>> {
  return service.post(url, data, config)
}

export function put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<R<T>> {
  return service.put(url, data, config)
}

export function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<R<T>> {
  return service.delete(url, config)
}
