/**
 * WMS-PDA 认证状态管理
 * Pinia store，管理JWT双Token认证状态
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfoVo, LoginDto, LoginResultVo, AuthProfileVo, RefreshTokenResultVo } from '@/utils/constants'
import { TOKEN_EXPIRE_THRESHOLD } from '@/utils/constants'
import { post, get } from '@/api/request'

/** 认证状态Store */
export const useAuthStore = defineStore('auth', () => {
  // ==================== State ====================

  /** 访问令牌 */
  const accessToken = ref<string | null>(null)
  /** 刷新令牌 */
  const refreshToken = ref<string | null>(null)
  /** accessToken过期时间戳（毫秒） */
  const tokenExpireTime = ref<number | null>(null)
  /** 用户信息 */
  const userInfo = ref<UserInfoVo | null>(null)
  /** 权限列表 */
  const permissions = ref<string[]>([])
  /** 角色列表 */
  const roles = ref<string[]>([])
  /** 服务器地址配置 */
  const serverUrl = ref<string>('')

  // ==================== 初始化：从本地存储恢复状态 ====================

  /**
   * 从本地存储恢复认证状态
   * 应用启动时调用，恢复持久化的Token和用户信息
   */
  function initFromStorage(): void {
    try {
      accessToken.value = uni.getStorageSync('accessToken') || null
      refreshToken.value = uni.getStorageSync('refreshToken') || null
      tokenExpireTime.value = uni.getStorageSync('tokenExpireTime') || null
      serverUrl.value = uni.getStorageSync('serverUrl') || ''

      const storedUserInfo = uni.getStorageSync('userInfo')
      if (storedUserInfo) {
        userInfo.value = typeof storedUserInfo === 'string' ? JSON.parse(storedUserInfo) : storedUserInfo
      }

      const storedPermissions = uni.getStorageSync('permissions')
      if (storedPermissions) {
        permissions.value = typeof storedPermissions === 'string' ? JSON.parse(storedPermissions) : storedPermissions
      }

      const storedRoles = uni.getStorageSync('roles')
      if (storedRoles) {
        roles.value = typeof storedRoles === 'string' ? JSON.parse(storedRoles) : storedRoles
      }
    } catch (error) {
      console.error('恢复认证状态失败:', error)
    }
  }

  // ==================== Getters ====================

  /** 是否已登录 */
  const isLoggedIn = computed(() => !!accessToken.value && !!refreshToken.value)

  /** 是否有指定权限 */
  function hasPermission(permission: string): boolean {
    return permissions.value.includes(permission)
  }

  // ==================== Actions ====================

  /**
   * 登录
   * 调用登录接口，存储双Token和用户信息
   *
   * @param dto 登录请求参数
   */
  async function login(dto: LoginDto): Promise<void> {
    const result = await post<LoginResultVo>('/api/auth/login', {
      username: dto.username,
      password: dto.password,
      captchaToken: dto.captchaToken,
      captchaTrack: dto.captchaTrack,
    })

    // 存储Token
    accessToken.value = result.accessToken
    refreshToken.value = result.refreshToken

    // 计算Token过期时间戳
    const expireTimestamp = Date.now() + result.expiresIn * 1000
    tokenExpireTime.value = expireTimestamp

    // 持久化到本地存储
    uni.setStorageSync('accessToken', result.accessToken)
    uni.setStorageSync('refreshToken', result.refreshToken)
    uni.setStorageSync('tokenExpireTime', expireTimestamp)

    // 登录成功后获取用户信息
    await fetchProfile()
  }

  /**
   * 登出
   * 调用登出接口，清除本地认证数据
   */
  async function logout(): Promise<void> {
    try {
      // 尝试调用后端登出接口
      if (accessToken.value) {
        await post('/api/auth/logout')
      }
    } catch (error) {
      // 登出接口失败也继续清除本地数据
      console.warn('登出接口调用失败:', error)
    } finally {
      clearAuth()
    }
  }

  /**
   * 刷新Token
   * 使用refreshToken获取新的accessToken
   *
   * @returns 新的accessToken
   */
  async function refreshTokenAction(): Promise<string> {
    if (!refreshToken.value) {
      throw new Error('无refreshToken，无法刷新')
    }

    const result = await post<RefreshTokenResultVo>('/api/auth/token/refresh', {
      refreshToken: refreshToken.value
    })

    // 更新Token
    accessToken.value = result.accessToken
    const expireTimestamp = Date.now() + result.expiresIn * 1000
    tokenExpireTime.value = expireTimestamp

    // 持久化
    uni.setStorageSync('accessToken', result.accessToken)
    uni.setStorageSync('tokenExpireTime', expireTimestamp)

    return result.accessToken
  }

  /**
   * 获取用户信息
   * 调用用户档案接口，更新用户信息和权限
   */
  async function fetchProfile(): Promise<void> {
    const profile = await get<AuthProfileVo>('/api/auth/profile')

    // 更新用户信息
    userInfo.value = profile.userInfo
    permissions.value = profile.permissions
    roles.value = profile.roles

    // 持久化
    uni.setStorageSync('userInfo', JSON.stringify(profile.userInfo))
    uni.setStorageSync('permissions', JSON.stringify(profile.permissions))
    uni.setStorageSync('roles', JSON.stringify(profile.roles))
  }

  /**
   * 设置服务器地址
   * 配置后端API服务器地址
   *
   * @param url 服务器地址（如 http://192.168.1.100:8080）
   */
  function setServerUrl(url: string): void {
    serverUrl.value = url
    uni.setStorageSync('serverUrl', url)
  }

  /**
   * 检查Token是否即将过期
   * 在过期前5分钟返回true，触发主动刷新
   *
   * @returns Token是否即将过期
   */
  function isTokenExpiring(): boolean {
    if (!tokenExpireTime.value) {
      return true
    }

    const now = Date.now()
    const remaining = tokenExpireTime.value - now

    // 距离过期不足5分钟，视为即将过期
    return remaining < TOKEN_EXPIRE_THRESHOLD
  }

  /**
   * 清除认证数据
   * 清除所有Token和用户信息
   */
  function clearAuth(): void {
    accessToken.value = null
    refreshToken.value = null
    tokenExpireTime.value = null
    userInfo.value = null
    permissions.value = []
    roles.value = []

    // 清除本地存储
    uni.removeStorageSync('accessToken')
    uni.removeStorageSync('refreshToken')
    uni.removeStorageSync('tokenExpireTime')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('permissions')
    uni.removeStorageSync('roles')
  }

  // 初始化时恢复状态
  initFromStorage()

  return {
    // State
    accessToken,
    refreshToken,
    tokenExpireTime,
    userInfo,
    permissions,
    roles,
    serverUrl,
    // Getters
    isLoggedIn,
    hasPermission,
    // Actions
    login,
    logout,
    refreshTokenAction,
    fetchProfile,
    setServerUrl,
    isTokenExpiring,
    clearAuth
  }
})