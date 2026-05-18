import { defineStore } from 'pinia'
import { computed } from 'vue'
import {
  preferences,
  updatePreferences,
  usePreferences,
} from '@/utils/preferences'

/**
 * 应用状态Store
 * 基于偏好设置系统提供应用级状态
 */
export const useAppStore = defineStore('app', () => {
  const { isDark, layout, isMobile, theme } = usePreferences()

  /** 侧边栏是否折叠 */
  const sidebarCollapsedState = computed(() => preferences.sidebar.collapsed)

  /** 切换侧边栏折叠状态 */
  function toggleSidebar() {
    updatePreferences({
      sidebar: { collapsed: !preferences.sidebar.collapsed },
    })
  }

  /** 设置侧边栏折叠状态 */
  function setSidebarCollapsed(collapsed: boolean) {
    updatePreferences({
      sidebar: { collapsed },
    })
  }

  /** 切换主题模式 */
  function toggleTheme() {
    updatePreferences({
      theme: { mode: isDark.value ? 'light' : 'dark' },
    })
  }

  /** 设置布局模式 */
  function setLayout(newLayout: string) {
    updatePreferences({ app: { layout: newLayout as any } })
  }

  return {
    sidebarCollapsed: sidebarCollapsedState,
    theme,
    isDark,
    layout,
    isMobile,
    toggleSidebar,
    setSidebarCollapsed,
    toggleTheme,
    setLayout,
  }
})
