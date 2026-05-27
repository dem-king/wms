import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/system/auth'
import {
  clearAuth,
  getStoredPermissions,
  getStoredRoles,
  getStoredUserInfo,
  getTokenExpiresAt,
  getToken,
  setRefreshToken,
  setStoredPermissions,
  setStoredRoles,
  setStoredUserInfo,
  setToken,
  setTokenExpiresAt,
} from '@/utils/auth'
import { scheduleTokenRefresh, stopRefreshScheduler } from '@/utils/auth-refresh'
import type { LoginReq, UserInfoVO } from '@/types/auth'
import { usePermissionStore } from './permission'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfoVO | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  /**
   * 用户登录
   * 登录成功后保存Token、用户信息、权限和菜单数据
   * 菜单数据传递给permission store用于动态路由生?   */
  async function login(req: LoginReq) {
    const res = await loginApi(req)
    const data = res.data
    token.value = data.accessToken
    setToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    setTokenExpiresAt(Date.now() + data.expiresIn * 1000)
    userInfo.value = data.userInfo
    permissions.value = data.permissions
    roles.value = []
    persistSessionState()
    scheduleTokenRefresh()
    // 将菜单树传递给permission store
    const permissionStore = usePermissionStore()
    permissionStore.setMenuTree(data.menus || [])
  }

  /**
   * 用户登出
   * 清除所有状态和权限，重定向到登录页
   */
  async function logout() {
    try {
      await logoutApi()
    } finally {
      stopRefreshScheduler()
      resetState()
      clearAuth()
      const permissionStore = usePermissionStore()
      permissionStore.resetPermission()
      router.push('/login')
    }
  }

  /**
   * 重置用户状态?   */
  function resetState() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
  }

  /**
   * 从本地存储恢复登录用户快照
   * 页面刷新后，Pinia内存状态会丢失，需要用Token存在性作为恢复前提
   */
  function initializeFromStorage() {
    const storedToken = getToken()
    token.value = storedToken || ''

    if (!storedToken) {
      stopRefreshScheduler()
      resetState()
      clearAuth()
      return
    }

    userInfo.value = getStoredUserInfo()
    permissions.value = getStoredPermissions()
    roles.value = getStoredRoles()
    if (getTokenExpiresAt()) {
      scheduleTokenRefresh()
    }
  }

  /**
   * 判断是否拥有指定权限
   * @param permCode 权限编码
   */
  function hasPermission(permCode: string): boolean {
    return permissions.value.includes(permCode)
  }

  /**
   * 判断是否已登�?   */
  function isLogin(): boolean {
    return !!getToken()
  }

  function syncUserInfo(nextUserInfo: UserInfoVO | null) {
    userInfo.value = nextUserInfo
    persistSessionState()
  }

  function persistSessionState() {
    setStoredUserInfo(userInfo.value as UserInfoVO | null)
    setStoredPermissions(permissions.value)
    setStoredRoles(roles.value)
  }

  return { token, userInfo, roles, permissions, login, logout, resetState, initializeFromStorage, hasPermission, isLogin, syncUserInfo }
})
