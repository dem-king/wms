<template>
  <div class="vben-layout">
    <!-- ==================== 侧边栏区域 ==================== -->
    <aside
      v-if="sidebarVisible"
      class="layout-sidebar"
      :class="{
        'semi-dark': preferences.theme.semiDarkSidebar || isDark,
        'sidebar-mixed': isSidebarMixedNav || isHeaderMixedNav,
      }"
      :style="sidebarStyle"
    >
      <Sidebar :is-mixed="isSidebarMixedNav || isHeaderMixedNav" />
    </aside>

    <!-- ==================== 顶栏区域 ==================== -->
    <header
      v-if="headerVisible"
      class="layout-header"
      :class="{ 'semi-dark': preferences.theme.semiDarkHeader || isDark }"
      :style="headerStyle"
    >
      <Navbar>
        <template #menu>
          <slot v-if="showHeaderMenu" name="header-menu"></slot>
        </template>
      </Navbar>
    </header>

    <!-- ==================== 右侧内容区域（Tabbar+内容+底栏） ==================== -->
    <div class="layout-main-wrapper" :style="mainWrapperStyle">
      <div class="layout-main-panel">
        <div
          v-if="preferences.tabbar.enable && !isFullContent"
          class="layout-tabbar"
        >
          <TagsView />
        </div>

        <!-- 主内容 -->
        <main class="layout-content" :style="contentStyle">
          <router-view v-slot="{ Component }">
            <transition :name="preferences.transition.name" mode="out-in">
              <keep-alive :include="cachedViews">
                <component :is="Component" />
              </keep-alive>
            </transition>
          </router-view>
        </main>

        <!-- 底栏 -->
        <footer
          v-if="preferences.footer.enable && !isFullContent"
          class="layout-footer"
          :style="footerStyle"
        >
          <span>WMS 备品备件库房管理平台</span>
        </footer>
      </div>
    </div>
  </div>

  <HeaderActionButton
    v-if="preferences.app.preferencesButtonPosition === 'fixed'"
    class="layout-floating-preferences"
    tooltip="偏好设置"
    @click="openPreferences"
  >
    <el-icon><Setting /></el-icon>
  </HeaderActionButton>

  <PreferencesDrawer />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import TagsView from './components/TagsView.vue'
import HeaderActionButton from './components/HeaderActionButton.vue'
import PreferencesDrawer from './components/preferences/PreferencesDrawer.vue'
import { preferences, updatePreferences, usePreferences } from '@/utils/preferences'
import { getLayoutMetrics } from '@/layouts/composables/menu-layout'

const { isDark, keepAlive, layoutState } = usePreferences()

const isFullContent = computed(() => layoutState.value.isFullContent)
const isSidebarMixedNav = computed(() => layoutState.value.isSidebarMixedNav)
const isHeaderNav = computed(() => layoutState.value.isHeaderNav)
const isMixedNav = computed(() => layoutState.value.isMixedNav)
const isHeaderSidebarNav = computed(() => layoutState.value.isHeaderSidebarNav)
const isHeaderMixedNav = computed(() => layoutState.value.isHeaderMixedNav)
const sidebarVisible = computed(() => layoutState.value.sidebarVisible)
const headerVisible = computed(() => layoutState.value.headerVisible)
const showHeaderMenu = computed(() => layoutState.value.showHeaderMenu)

const layoutMetrics = computed(() =>
  getLayoutMetrics(layoutState.value, {
    collapseWidth: preferences.sidebar.collapseWidth,
    collapsed: preferences.sidebar.collapsed,
    headerHeight: preferences.header.height,
    headerVisible: headerVisible.value,
    mixedWidth: preferences.sidebar.mixedWidth,
    sidebarVisible: sidebarVisible.value,
    sidebarWidth: preferences.sidebar.width,
    tabbarHeight: preferences.tabbar.height,
  }),
)

const sidebarStyle = computed(() => {
  return {
    width: `${layoutMetrics.value.sidebarWidth}px`,
    top: `${layoutMetrics.value.sidebarTop}px`,
    height: `calc(100% - ${layoutMetrics.value.sidebarTop}px)`,
    transition: 'width 0.3s, top 0.3s, height 0.3s',
    zIndex: 100,
  }
})

const mainWrapperStyle = computed(() => {
  return {
    paddingLeft: `${layoutMetrics.value.mainPaddingLeft}px`,
    paddingTop: `${layoutMetrics.value.mainPaddingTop}px`,
    transition: 'padding-left 0.3s, padding-top 0.3s',
  }
})

const headerStyle = computed(() => {
  const height = `${preferences.header.height}px`
  const isFullWidth = isHeaderNav.value || isMixedNav.value || isHeaderMixedNav.value || isHeaderSidebarNav.value
  
  let left = '0'
  let width = '100%'
  
  if (!isFullWidth && sidebarVisible.value) {
    left = `${layoutMetrics.value.sidebarWidth}px`
    width = `calc(100% - ${layoutMetrics.value.sidebarWidth}px)`
  }

  return {
    height,
    position: preferences.header.mode === 'fixed' ? 'fixed' as const : 'sticky' as const,
    top: 0,
    left,
    width,
    zIndex: isFullWidth ? 101 : 99,
    transition: 'width 0.3s, left 0.3s',
  }
})

const contentStyle = computed(() => {
  const padding = preferences.app.contentPadding
  return {
    padding: `${padding}px`,
  }
})

const footerStyle = computed(() => ({
  height: `${preferences.footer.height}px`,
  position: preferences.footer.fixed ? 'sticky' as const : 'relative' as const,
  bottom: 0,
}))

const cachedViews = computed(() => (keepAlive.value ? undefined : []))

function openPreferences() {
  updatePreferences({ app: { enablePreferences: true } })
}
</script>

<style lang="scss" scoped>
.vben-layout {
  display: flex;
  height: 100vh;
  width: 100%;
  position: relative;
  background:
    radial-gradient(circle at top left, hsl(var(--primary) / 0.08), transparent 28%),
    var(--bg-background-deep);
}

.layout-sidebar {
  position: fixed;
  left: 0;
  overflow: hidden;
  background-color: var(--bg-sidebar);
  border-right: 1px solid var(--color-border);
  box-shadow: 10px 0 30px hsl(220 43% 11% / 0.05);

  &.semi-dark {
    background-color: hsl(var(--sidebar-deep));
  }

  &.sidebar-mixed {
    :deep(.el-menu) {
      --el-menu-item-height: 48px;
    }
  }
}

.layout-main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding-right: 16px;
  padding-bottom: 16px;
  gap: 12px;
}

.layout-header {
  display: flex;
  align-items: center;
  padding: 0;
  border: 1px solid hsl(var(--border));
  border-radius: 0 0 22px 22px;
  background: hsl(var(--header) / 0.84);
  box-shadow: 0 18px 36px hsl(220 43% 11% / 0.08);
  backdrop-filter: blur(18px);

  &.semi-dark {
    background-color: hsl(var(--sidebar-deep));
    border-bottom-color: hsl(var(--accent-darker));
  }
}

.layout-tabbar {
  flex-shrink: 0;
  background: hsl(var(--card) / 0.9);
}

.layout-main-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  border: 1px solid hsl(var(--border));
  border-radius: 24px;
  background: hsl(var(--card) / 0.92);
  box-shadow: 0 20px 50px hsl(220 43% 11% / 0.08);
  overflow: hidden;
}

.layout-content {
  flex: 1;
  background:
    linear-gradient(180deg, hsl(var(--card)) 0%, hsl(var(--background)) 100%);
  overflow: auto;
}

.layout-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid hsl(var(--border));
  background-color: hsl(var(--card));
  color: var(--text-muted-foreground);
  font-size: 12px;
  flex-shrink: 0;
}

.layout-floating-preferences {
  position: fixed;
  right: 18px;
  bottom: 22px;
  z-index: 120;
  width: 44px;
  height: 44px;
  border-radius: 16px;
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 95% 62%));
  color: #fff;
  border-color: transparent;
  box-shadow: 0 16px 36px hsl(var(--primary) / 0.28);

  &:hover {
    color: #fff;
    border-color: transparent;
    background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 95% 62%));
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
