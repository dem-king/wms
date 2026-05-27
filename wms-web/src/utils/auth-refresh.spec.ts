import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest'

const axiosPostMock = vi.hoisted(() => vi.fn())
const routerPushMock = vi.hoisted(() => vi.fn())

vi.mock('axios', () => ({
  default: {
    post: axiosPostMock,
  },
  post: axiosPostMock,
}))

vi.mock('@/router', () => ({
  default: {
    push: routerPushMock,
  },
}))

import {
  getRefreshToken,
  getToken,
  getTokenExpiresAt,
  setRefreshToken,
  setToken,
  setTokenExpiresAt,
} from './auth'
import { refreshAccessToken, scheduleTokenRefresh, stopRefreshScheduler } from './auth-refresh'

class MemoryStorage implements Storage {
  private store = new Map<string, string>()

  get length() {
    return this.store.size
  }

  clear() {
    this.store.clear()
  }

  getItem(key: string) {
    return this.store.get(key) ?? null
  }

  key(index: number) {
    return Array.from(this.store.keys())[index] ?? null
  }

  removeItem(key: string) {
    this.store.delete(key)
  }

  setItem(key: string, value: string) {
    this.store.set(key, value)
  }
}

describe('auth refresh', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-05-22T10:00:00Z'))
    vi.clearAllMocks()
    vi.stubGlobal('localStorage', new MemoryStorage())
    vi.stubGlobal('sessionStorage', new MemoryStorage())
  })

  afterEach(() => {
    stopRefreshScheduler()
    vi.useRealTimers()
  })

  it('schedules one proactive refresh before token expiry', () => {
    setToken('access-token')
    setRefreshToken('refresh-token')
    setTokenExpiresAt(Date.now() + 5 * 60 * 1000)

    const timerSpy = vi.spyOn(globalThis, 'setTimeout')

    scheduleTokenRefresh()

    expect(timerSpy).toHaveBeenCalledTimes(1)
  })

  it('refreshes tokens and updates expiry time', async () => {
    setToken('old-access-token')
    setRefreshToken('old-refresh-token')
    setTokenExpiresAt(Date.now() + 5 * 60 * 1000)
    axiosPostMock.mockResolvedValue({
      data: {
        code: 200,
        data: {
          accessToken: 'new-access-token',
          refreshToken: 'new-refresh-token',
          tokenType: 'Bearer',
          expiresIn: 7200,
        },
      },
    })

    await refreshAccessToken()

    expect(axiosPostMock).toHaveBeenCalledWith('/api/auth/token/refresh', {
      refreshToken: 'old-refresh-token',
    })
    expect(getToken()).toBe('new-access-token')
    expect(getRefreshToken()).toBe('new-refresh-token')
    expect(getTokenExpiresAt()).toBeGreaterThan(Date.now())
  })
})
