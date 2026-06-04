/**
 * WMS-PDA 认证组合函数。
 */
import { computed } from 'vue'
import type { ComputedRef } from 'vue'
import type { UserInfoVo } from '@/utils/constants'
import { useAuthStore } from '@/store/auth'
import { isValidServerUrl } from '@/utils/validate'

interface UseAuthReturn {
  login: (username: string, encryptedPassword: string, code: string, randomStr: string) => Promise<void>
  logout: () => Promise<void>
  isLoggedIn: ComputedRef<boolean>
  userInfo: ComputedRef<UserInfoVo | null>
  serverUrl: ComputedRef<string>
  setServerUrl: (url: string) => boolean
  fetchProfile: () => Promise<void>
  isTokenExpiring: () => boolean
  hasPermission: (permission: string) => boolean
}

export function useAuth(): UseAuthReturn {
  const authStore = useAuthStore()

  async function login(
    username: string,
    encryptedPassword: string,
    code: string,
    randomStr: string
  ): Promise<void> {
    await authStore.login({ username, encryptedPassword, code, randomStr })
    uni.reLaunch({ url: '/pages/home/index' })
  }

  async function logout(): Promise<void> {
    return new Promise((resolve, reject) => {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗?',
        confirmText: '确定',
        cancelText: '取消',
        success: async (res) => {
          if (!res.confirm) {
            resolve()
            return
          }
          try {
            await authStore.logout()
            uni.reLaunch({ url: '/pages/login/index' })
            resolve()
          } catch (error) {
            reject(error)
          }
        },
        fail: () => resolve()
      })
    })
  }

  function setServerUrl(url: string): boolean {
    if (!isValidServerUrl(url)) {
      uni.showToast({
        title: '服务器地址格式错误',
        icon: 'none',
        duration: 2000
      })
      return false
    }

    authStore.setServerUrl(url)
    return true
  }

  const isLoggedIn = computed(() => authStore.isLoggedIn)
  const userInfo = computed(() => authStore.userInfo)
  const serverUrl = computed(() => authStore.serverUrl)

  return {
    login,
    logout,
    isLoggedIn,
    userInfo,
    serverUrl,
    setServerUrl,
    fetchProfile: authStore.fetchProfile,
    isTokenExpiring: authStore.isTokenExpiring,
    hasPermission: authStore.hasPermission
  }
}
