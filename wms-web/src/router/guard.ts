import type { Router } from 'vue-router'
import NProgress from 'nprogress'
import { clearAuth, getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { getUserMenuTree } from '@/api/system/menu'
import type { MenuTreeNode } from '@/types/auth'

const WHITE_LIST = ['/login', '/404', '/403']

/**
 * 路由守卫设置
 * 核心逻辑：
 * 1. 白名单路由直接放行
 * 2. 未登录重定向到登录页
 * 3. 已登录但动态路由未生成时，获取菜单树并生成动态路由
 * 4. 已登录且动态路由已生成时直接放行
 */
export function setupRouterGuard(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    NProgress.start()
    try {
      const token = getToken()

      if (WHITE_LIST.includes(to.path)) {
        // 已登录访问登录页则重定向到首页
        if (token && to.path === '/login') {
          next('/')
          return
        }
        next()
      } else if (!token) {
        // 未登录重定向到登录页
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      } else {
        // 已登录，检查动态路由是否已生成
        const permissionStore = usePermissionStore()
        if (!permissionStore.hasRoutes()) {
          try {
            // 刷新页面后需要重新获取菜单树并生成动态路由
            const menus = await fetchMenuTree()
            permissionStore.setMenuTree(menus)
            permissionStore.generateRoutes(menus)
            // 重新导航到目标路由(确保动态路由已注册)
            next({ ...to, replace: true })
          } catch (error) {
            // 获取菜单失败(如Token过期)，清除认证信息并重定向到登录页
            console.error('获取菜单树失败:', error)
            const userStore = useUserStore()
            userStore.resetState()
            clearAuth()
            next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
          }
        } else {
          next()
        }
      }
      document.title = `${to.meta.title || ''} - 备品备件库房管理平台`
    } catch (error) {
      console.error('路由守卫异常:', error)
      next()
    }
  })

  router.afterEach(() => {
    NProgress.done()
  })
}

/**
 * 获取菜单树
 * 优先从permission store中获取(登录时已保存)
 * 如果store中没有(页面刷新)，则调用API获取
 */
async function fetchMenuTree(): Promise<MenuTreeNode[]> {
  const permissionStore = usePermissionStore()

  // 如果store中已有菜单数据，直接返回
  if (permissionStore.menuTree.length > 0) {
    return permissionStore.menuTree
  }

  // 页面刷新后需要重新从API获取当前用户菜单树
  const res = await getUserMenuTree()
  return res.data
}
