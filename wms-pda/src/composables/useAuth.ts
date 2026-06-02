/**
 * WMS-PDA useAuth组合函数
 * 封装认证操作，提供计算属性和操作方法
 */
import { computed } from 'vue'
import type { UserInfoVo } from '@/utils/constants'
import { useAuthStore } from '@/store/auth'
import { isValidServerUrl } from '@/utils/validate'

/** useAuth返回类型 */
interface UseAuthReturn {
  /** 登录操作 */
  login: (username: string, password: string) => Promise<void>
  /** 登出操作 */
  logout: () => Promise<void>
  /** 是否已登录 */
  isLoggedIn: computed<boolean>
  /** 当前用户信息 */
  userInfo: computed<UserInfoVo | null>
  /** 服务器地址 */
  serverUrl: computed<string>
  /** 设置服务器地址 */
  setServerUrl: (url: string) => boolean
  /** 获取用户信息 */
  fetchProfile: () => Promise<void>
  /** 检查Token是否即将过期 */
  isTokenExpiring: () => boolean
  /** 是否有指定权限 */
  hasPermission: (permission: string) => boolean
}

/**
 * 认证组合函数
 * 封装login/logout/setServerUrl操作，暴露计算属性
 *
 * @returns 认证操作接口
 */
export function useAuth(): UseAuthReturn {
  const authStore = useAuthStore()

  /**
   * 登录
   * 调用authStore.login，成功后跳转首页
   *
   * @param username 用户名
   * @param password 密码
   */
  async function login(username: string, password: string): Promise<void> {
    await authStore.login({ username, password })

    // 登录成功，跳转首页
    uni.reLaunch({
      url: '/pages/home/index'
    })
  }

  /**
   * 登出
   * 确认后调用authStore.logout，清除Token并跳转登录页
   */
  async function logout(): Promise<void> {
    // 显示确认弹窗
    return new Promise((resolve, reject) => {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        confirmText: '确定',
        cancelText: '取消',
        success: async (res) => {
          if (res.confirm) {
            try {
              await authStore.logout()
              // 跳转登录页
              uni.reLaunch({
                url: '/pages/login/index'
              })
              resolve()
            } catch (error) {
              reject(error)
            }
          } else {
            resolve()
          }
        },
        fail: () => resolve()
      })
    })
  }

  /**
   * 设置服务器地址
   * 校验URL格式后保存
   *
   * @param url 服务器地址
   * @returns 是否设置成功
   */
  function setServerUrl(url: string): boolean {
    // URL格式校验
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

  // 计算属性
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