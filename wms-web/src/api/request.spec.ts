import { beforeEach, describe, expect, it, vi } from 'vitest'

const refreshAccessTokenMock = vi.hoisted(() => vi.fn())
const handleRefreshFailureMock = vi.hoisted(() => vi.fn())
const serviceMock = vi.hoisted(() =>
  Object.assign(vi.fn().mockResolvedValue({ data: { code: 200, data: { ok: true } } }), {
    interceptors: {
      request: {
        use: vi.fn(),
      },
      response: {
        use: vi.fn(),
      },
    },
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  }),
)

const axiosCreateMock = vi.hoisted(() => vi.fn(() => serviceMock))
const axiosPostMock = vi.hoisted(() => vi.fn())

vi.mock('axios', () => ({
  default: {
    create: axiosCreateMock,
    post: axiosPostMock,
  },
  create: axiosCreateMock,
  post: axiosPostMock,
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
  },
}))

vi.mock('@/router', () => ({
  default: {
    push: vi.fn(),
  },
}))

vi.mock('@/utils/auth', () => ({
  clearAuth: vi.fn(),
  getRefreshToken: vi.fn(() => 'refresh-token'),
  getToken: vi.fn(() => 'expired-access-token'),
  setRefreshToken: vi.fn(),
  setToken: vi.fn(),
}))

vi.mock('@/utils/auth-refresh', () => ({
  refreshAccessToken: refreshAccessTokenMock,
  handleRefreshFailure: handleRefreshFailureMock,
}))

import './request'

describe('request interceptor', () => {
  beforeEach(() => {
    refreshAccessTokenMock.mockReset()
    handleRefreshFailureMock.mockReset()
    serviceMock.mockClear()
  })

  it('uses the shared refresh helper when handling 401 responses', async () => {
    refreshAccessTokenMock.mockResolvedValue('new-access-token')
    const responseRejected = serviceMock.interceptors.response.use.mock.calls[0][1]
    const originalRequest = {
      headers: {} as Record<string, string>,
      _retry: false,
      url: '/system/user',
    }

    await responseRejected({
      response: { status: 401 },
      config: originalRequest,
      message: 'Unauthorized',
    })

    expect(refreshAccessTokenMock).toHaveBeenCalledTimes(1)
    expect(originalRequest.headers.Authorization).toBe('Bearer new-access-token')
    expect(serviceMock).toHaveBeenCalledWith(originalRequest)
  })
})
