import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

const authApiMocks = vi.hoisted(() => ({
  login: vi.fn(),
  logout: vi.fn(),
}))

const routerPushMock = vi.hoisted(() => vi.fn())

vi.mock('@/api/system/auth', () => ({
  login: authApiMocks.login,
  logout: authApiMocks.logout,
}))

vi.mock('@/router', () => ({
  default: {
    push: routerPushMock,
  },
}))

import { useUserStore } from './user'

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

describe('user store', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    const localStorage = new MemoryStorage()
    const sessionStorage = new MemoryStorage()
    vi.stubGlobal('localStorage', localStorage)
    vi.stubGlobal('sessionStorage', sessionStorage)
    setActivePinia(createPinia())
  })

  it('restores persisted user info and permissions when token still exists after refresh', () => {
    localStorage.setItem('wms_token', 'access-token')
    localStorage.setItem(
      'wms_user_info',
      JSON.stringify({
        userId: 1,
        username: 'admin',
        realName: '管理员',
        avatar: 'avatar.png',
        deptId: 10,
      }),
    )
    localStorage.setItem('wms_permissions', JSON.stringify(['system:user:list']))
    localStorage.setItem('wms_roles', JSON.stringify(['admin']))

    const userStore = useUserStore()

    userStore.initializeFromStorage()

    expect(userStore.token).toBe('access-token')
    expect(userStore.userInfo?.realName).toBe('管理员')
    expect(userStore.permissions).toEqual(['system:user:list'])
    expect(userStore.roles).toEqual(['admin'])
  })

  it('persists user session data after login succeeds', async () => {
    authApiMocks.login.mockResolvedValue({
      data: {
        accessToken: 'access-token',
        refreshToken: 'refresh-token',
        tokenType: 'Bearer',
        expiresIn: 7200,
        userInfo: {
          userId: 2,
          username: 'zhangsan',
          realName: '张三',
          avatar: 'avatar.png',
          deptId: 20,
        },
        permissions: ['system:user:list', 'system:role:list'],
        menus: [],
      },
    })

    const userStore = useUserStore()

    await userStore.login({
      username: 'zhangsan',
      encryptedPassword: 'encrypted-password',
      captchaKey: 'captcha-key',
      captchaText: '1234',
    })

    expect(userStore.userInfo?.realName).toBe('张三')
    expect(JSON.parse(localStorage.getItem('wms_user_info') || 'null')).toEqual({
      userId: 2,
      username: 'zhangsan',
      realName: '张三',
      avatar: 'avatar.png',
      deptId: 20,
    })
    expect(JSON.parse(localStorage.getItem('wms_permissions') || 'null')).toEqual([
      'system:user:list',
      'system:role:list',
    ])
    expect(JSON.parse(localStorage.getItem('wms_roles') || 'null')).toEqual([])
    expect(Number(localStorage.getItem('wms_access_token_expires_at'))).toBeGreaterThan(Date.now())
  })
})
