import axios from 'axios'
import router from '@/router'
import {
  clearAuth,
  getRefreshToken,
  getTokenExpiresAt,
  setRefreshToken,
  setToken,
  setTokenExpiresAt,
} from './auth'

interface TokenResp {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

const REFRESH_AHEAD_MS = 60 * 1000

let refreshTimer: ReturnType<typeof setTimeout> | null = null
let refreshPromise: Promise<string> | null = null

export function scheduleTokenRefresh() {
  stopRefreshScheduler()

  const refreshToken = getRefreshToken()
  const expiresAt = getTokenExpiresAt()
  if (!refreshToken || !expiresAt) {
    return
  }

  const delay = Math.max(expiresAt - Date.now() - REFRESH_AHEAD_MS, 0)
  refreshTimer = setTimeout(() => {
    void refreshAccessToken().catch(() => undefined)
  }, delay)
}

export function stopRefreshScheduler() {
  if (refreshTimer) {
    clearTimeout(refreshTimer)
    refreshTimer = null
  }
}

export async function refreshAccessToken(): Promise<string> {
  if (refreshPromise) {
    return refreshPromise
  }

  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    handleRefreshFailure()
    throw new Error('刷新Token不存在')
  }

  refreshPromise = axios
    .post<ApiResponse<TokenResp>>('/api/auth/token/refresh', {
      refreshToken,
    })
    .then((response) => {
      if (response.data.code !== 200) {
        throw new Error(response.data.msg || '刷新Token失败')
      }

      const nextTokens = response.data.data
      setToken(nextTokens.accessToken)
      setRefreshToken(nextTokens.refreshToken)
      setTokenExpiresAt(Date.now() + nextTokens.expiresIn * 1000)
      scheduleTokenRefresh()
      return nextTokens.accessToken
    })
    .catch((error) => {
      handleRefreshFailure()
      throw error
    })
    .finally(() => {
      refreshPromise = null
    })

  return refreshPromise
}

export function handleRefreshFailure() {
  stopRefreshScheduler()
  clearAuth()
  router.push('/login')
}
