import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteComponent, RouteRecordRaw } from 'vue-router'
import type { MenuTreeNode } from '@/types/auth'
import router from '@/router'
import Layout from '@/layouts/DefaultLayout.vue'

/** 动态路由是否已生成 */
const isRoutesAdded = ref(false)

export const usePermissionStore = defineStore('permission', () => {
  /** 用户菜单树 */
  const menuTree = ref<MenuTreeNode[]>([])

  /** 动态路由列表 */
  const dynamicRoutes = ref<RouteRecordRaw[]>([])

  /**
   * 设置菜单树数据
   * @param menus 登录时或刷新时获取的菜单树
   */
  function setMenuTree(menus: MenuTreeNode[]) {
    menuTree.value = menus
  }

  /**
   * 根据菜单树生成动态路由并添加到路由实例
   * 核心逻辑：遍历菜单树，将目录和菜单类型的节点转换为 Vue Router 路由配置
   */
  function generateRoutes(menus: MenuTreeNode[]) {
    const routes: RouteRecordRaw[] = []
    for (const menu of menus) {
      const route = transformMenuToRoute(menu)
      if (route) {
        routes.push(route)
      }
    }
    dynamicRoutes.value = routes

    // 将动态路由添加到路由实例
    for (const route of routes) {
      router.addRoute(route)
    }
    // 添加兜底404路由(放在最后)
    router.addRoute({
      path: '/:pathMatch(.*)*',
      redirect: '/404',
      meta: { hidden: true }
    })
    isRoutesAdded.value = true
  }

  /**
   * 重置权限状态(登出时调用)
   */
  function resetPermission() {
    menuTree.value = []
    dynamicRoutes.value = []
    isRoutesAdded.value = false
  }

  /**
   * 判断动态路由是否已生成
   */
  function hasRoutes(): boolean {
    return isRoutesAdded.value
  }

  return {
    menuTree,
    dynamicRoutes,
    setMenuTree,
    generateRoutes,
    resetPermission,
    hasRoutes
  }
})

/**
 * 将单个菜单节点转换为 Vue Router 路由配置
 * 目录(menuType=1)作为布局的子路由容器
 * 菜单(menuType=2)作为具体页面路由
 *
 * @param menu 菜单树节点
 * @returns Vue Router 路由配置，或null(如果无法转换)
 */
function transformMenuToRoute(menu: MenuTreeNode): RouteRecordRaw | null {
  // 目录类型：创建一个带布局的父路由
  if (menu.menuType === 1) {
    const route = {
      path: `/${menu.path}`,
      name: menu.menuCode,
      component: Layout,
      meta: {
        title: menu.menuName,
        icon: menu.icon || '',
        hidden: menu.visible === 0,
        alwaysShow: true
      }
    } as RouteRecordRaw
    const redirect = getFirstChildPath(menu)
    if (redirect) {
      ;(route as any).redirect = redirect
    }
    // 递归处理子菜单
    if (menu.children && menu.children.length > 0) {
      const children: RouteRecordRaw[] = []
      for (const child of menu.children) {
        const childRoute = transformMenuToRoute(child)
        if (childRoute) {
          children.push(childRoute)
        }
      }
      ;(route as any).children = children
    }
    return route
  }

  // 菜单类型：创建具体页面路由
  if (menu.menuType === 2) {
    const route = {
      path: menu.path,
      name: menu.menuCode,
      component: loadView(menu.component),
      meta: {
        title: menu.menuName,
        icon: menu.icon || '',
        hidden: menu.visible === 0,
        noCache: menu.isCache === 0,
        affix: false
      }
    } as RouteRecordRaw
    // 如果有重定向
    if (menu.redirect) {
      ;(route as any).redirect = menu.redirect
    }
    // 处理外链
    if (menu.isExternal === 1) {
      route.meta = { ...route.meta, link: menu.path, isExternal: true }
      ;(route as any).path = `/external/${menu.menuCode}`
      delete (route as any).component
    }
    // 递归处理子路由(如果有)
    if (menu.children && menu.children.length > 0) {
      const children: RouteRecordRaw[] = []
      for (const child of menu.children) {
        const childRoute = transformMenuToRoute(child)
        if (childRoute) {
          children.push(childRoute)
        }
      }
      ;(route as any).children = children
    }
    return route
  }

  // 按钮/操作类型不生成路由
  return null
}

/**
 * 获取目录下第一个子菜单的路径(用于目录的重定向)
 */
function getFirstChildPath(menu: MenuTreeNode): string | undefined {
  if (!menu.children || menu.children.length === 0) {
    return undefined
  }
  // 找到第一个可见的子菜单
  const firstVisible = menu.children.find(child => child.visible === 1 && child.menuType === 2)
  if (firstVisible) {
    return firstVisible.path
  }
  return undefined
}

/**
 * 动态加载视图组件
 * 使用 Vite 的 import.meta.glob 实现动态导入
 * 组件路径格式: src/views/{component}/index.vue
 *
 * @param component 后端配置的组件路径，如 system/user
 * @returns 组件导入函数
 */
function loadView(component: string): RouteComponent {
  if (!component) {
    return () => import('@/views/error/404.vue')
  }
  // 使用 Vite glob 导入
  const modules = import.meta.glob('/src/views/**/*.vue')
  const viewPath = `/src/views/${component}/index.vue`
  const moduleKey = Object.keys(modules).find(key => key.includes(viewPath))
  if (moduleKey) {
    return modules[moduleKey] as RouteComponent
  }
  // 尝试直接路径
  const directPath = `/src/views/${component}.vue`
  const directKey = Object.keys(modules).find(key => key.includes(directPath))
  if (directKey) {
    return modules[directKey] as RouteComponent
  }
  // 找不到组件则返回404
  console.warn(`[动态路由] 找不到组件: ${component}`)
  return () => import('@/views/error/404.vue')
}
