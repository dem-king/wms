import { computed } from 'vue'

import { resolveLayoutFlags } from '@/layouts/composables/menu-layout'

import { diff } from './diff'
import { preferencesManager } from './preferences'
import { isDarkTheme } from './css-variables-updater'

/**
 * 偏好设置组合式函数
 * 提供计算属性和便捷方法，用于在组件中访问和操作偏好设置
 */
export function usePreferences() {
  const preferences = preferencesManager.getPreferences()
  const initialPreferences = preferencesManager.getInitialPreferences()

  /** 与初始值有差异的偏好设置 */
  const diffPreference = computed(() => {
    return diff(initialPreferences, preferences)
  })

  const appPreferences = computed(() => preferences.app)
  const isDark = computed(() => isDarkTheme(preferences.theme.mode))
  const locale = computed(() => preferences.app.locale)
  const isMobile = computed(() => appPreferences.value.isMobile)
  const theme = computed(() => (isDark.value ? 'dark' : 'light'))
  const layoutFlags = computed(() =>
    resolveLayoutFlags(appPreferences.value.layout, isMobile.value),
  )

  /** 当前布局模式（移动端强制sidebar-nav） */
  const layout = computed(() => layoutFlags.value.layout)

  const layoutState = computed(() => ({
    ...layoutFlags.value,
    headerVisible:
      layoutFlags.value.showHeader &&
      preferences.header.enable &&
      !preferences.header.hidden,
    isMobile: isMobile.value,
    showHeaderMenu: layoutFlags.value.showHeaderMenu,
    sidebarVisible:
      layoutFlags.value.showSidebar &&
      preferences.sidebar.enable &&
      !preferences.sidebar.hidden,
  }))

  const isShowHeaderNav = computed(() => layoutState.value.headerVisible)
  const isFullContent = computed(() => layoutState.value.isFullContent)
  const isSideNav = computed(() => layoutState.value.isSidebarNav)
  const isHeaderNav = computed(() => layoutState.value.isHeaderNav)
  const isMixedNav = computed(() => layoutState.value.isMixedNav)
  const isSideMode = computed(() =>
    isMixedNav.value ||
    isSideNav.value ||
    layoutState.value.isSidebarMixedNav ||
    layoutState.value.isHeaderMixedNav ||
    layoutState.value.isHeaderSidebarNav,
  )
  const sidebarCollapsed = computed(() => preferences.sidebar.collapsed)
  const keepAlive = computed(() => preferences.tabbar.enable && preferences.tabbar.keepAlive)

  return {
    diffPreference,
    appPreferences,
    isDark,
    locale,
    isMobile,
    theme,
    layout,
    layoutState,
    isShowHeaderNav,
    isFullContent,
    isSideNav,
    isHeaderNav,
    isMixedNav,
    isSideMode,
    sidebarCollapsed,
    keepAlive,
  }
}
