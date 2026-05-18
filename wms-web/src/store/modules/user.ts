import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/system/auth'
import { setToken, setRefreshToken, getToken, clearAuth } from '@/utils/auth'
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
   * 菜单数据传递给permission store用于动态路由生�?   */
  async function login(req: LoginReq) {
    const res = await loginApi(req)
    const data = res.data
    token.value = data.accessToken
    setToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    userInfo.value = data.userInfo
    permissions.value = data.permissions
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
      resetState()
      clearAuth()
      const permissionStore = usePermissionStore()
      permissionStore.resetPermission()
      router.push('/login')
    }
  }

  /**
   * 重置用户状�?   */
  function resetState() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
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

  return { token, userInfo, roles, permissions, login, logout, resetState, hasPermission, isLogin }
})

