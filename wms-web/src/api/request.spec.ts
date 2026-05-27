import { beforeEach, describe, expect, it, vi } from 'vitest'

const messageErrorMock = vi.hoisted(() => vi.fn())
const clearAuthMock = vi.hoisted(() => vi.fn())
const getTokenMock = vi.hoisted(() => vi.fn(() => 'access-token'))
const getRefreshTokenMock = vi.hoisted(() => vi.fn(() => 'refresh-token'))
const setTokenMock = vi.hoisted(() => vi.fn())
const setRefreshTokenMock = vi.hoisted(() => vi.fn())
const routerPushMock = vi.hoisted(() => vi.fn())

const mockService = vi.hoisted(() => {
  const requestHandlers: Array<(value: any) => any> = []
  const responseHandlers: Array<(value: any) => any> = []
  const responseErrorHandlers: Array<(error: any) => any> = []

  return {
    interceptors: {
      request: {
        use: vi.fn((fulfilled: (value: any) => any) => {
          requestHandlers.push(fulfilled)
        }),
      },
      response: {
        use: vi.fn((fulfilled: (value: any) => any, rejected: (error: any) => any) => {
          responseHandlers.push(fulfilled)
          responseErrorHandlers.push(rejected)
        }),
      },
    },
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    __requestHandlers: requestHandlers,
    __responseHandlers: responseHandlers,
    __responseErrorHandlers: responseErrorHandlers,
  }
})

const axiosPostMock = vi.hoisted(() => vi.fn())

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => mockService),
    post: axiosPostMock,
  },
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    error: messageErrorMock,
  },
}))

vi.mock('@/utils/auth', () => ({
  clearAuth: clearAuthMock,
  getToken: getTokenMock,
  getRefreshToken: getRefreshTokenMock,
  setToken: setTokenMock,
  setRefreshToken: setRefreshTokenMock,
}))

vi.mock('@/router', () => ({
  default: {
    push: routerPushMock,
  },
}))

async function loadRequestModule() {
  mockService.__requestHandlers.length = 0
  mockService.__responseHandlers.length = 0
  mockService.__responseErrorHandlers.length = 0
  vi.resetModules()
  await import('./request')
}

describe('request interceptors', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockService.get.mockReset()
    mockService.post.mockReset()
    mockService.put.mockReset()
    mockService.delete.mockReset()
    axiosPostMock.mockReset()
    getTokenMock.mockReturnValue('access-token')
    getRefreshTokenMock.mockReturnValue('refresh-token')
  })

  it('returns blob responses without applying the json code guard', async () => {
    await loadRequestModule()
    const blob = new Blob(['report'], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    })
    const onFulfilled = mockService.__responseHandlers[0]

    const result = onFulfilled({
      data: blob,
      config: { responseType: 'blob' },
    })

    await expect(Promise.resolve(result)).resolves.toBe(blob)
    expect(messageErrorMock).not.toHaveBeenCalled()
  })

  it('rejects queued requests when token refresh fails', async () => {
    await loadRequestModule()
    const onRejected = mockService.__responseErrorHandlers[0]
    const originalRequest = {
      headers: {},
      _retry: false,
    }
    let rejectRefreshRequest!: (error: Error) => void

    axiosPostMock.mockImplementation(
      () =>
        new Promise((_, reject) => {
          rejectRefreshRequest = reject
        }),
    )

    const firstRefresh = onRejected({
      config: originalRequest,
      response: { status: 401 },
      message: 'Unauthorized',
    })

    const queuedRequest = onRejected({
      config: {
        headers: {},
        _retry: false,
      },
      response: { status: 401 },
      message: 'Unauthorized',
    })

    await Promise.resolve()

    rejectRefreshRequest(new Error('refresh failed'))

    await expect(firstRefresh).rejects.toMatchObject({
      response: {
        status: 401,
      },
    })

    await expect(Promise.race([
      queuedRequest.then(() => 'resolved', () => 'rejected'),
      new Promise((resolve) => setTimeout(() => resolve('pending'), 0)),
    ])).resolves.toBe('rejected')

    expect(clearAuthMock).toHaveBeenCalled()
    expect(routerPushMock).toHaveBeenCalledWith('/login')
  })
})
