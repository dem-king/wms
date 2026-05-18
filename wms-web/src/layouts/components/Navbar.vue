<template>
  <div class="navbar-shell">
    <div class="navbar-left">
      <div v-if="showHeaderLogo" class="navbar-logo">
        <div class="logo-mark">W</div>
        <div class="logo-text">
          <span class="logo-title">WMS</span>
          <span class="logo-subtitle">Warehouse Console</span>
        </div>
      </div>

      <HeaderActionButton
        v-if="showSidebarToggle && preferences.widget.sidebarToggle"
        :tooltip="preferences.sidebar.collapsed ? '展开侧边栏' : '收起侧边栏'"
        @click="toggleSidebar"
      >
        <el-icon>
          <Fold v-if="!preferences.sidebar.collapsed" />
          <Expand v-else />
        </el-icon>
      </HeaderActionButton>

      <div class="breadcrumb-panel" v-if="preferences.breadcrumb.enable && !showHeaderMenu">
        <div class="breadcrumb-label">当前位置</div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div v-if="showHeaderMenu" class="navbar-center">
      <div class="header-menu">
        <slot name="menu"></slot>
        <el-menu
          :default-active="route.path"
          mode="horizontal"
          router
          class="header-horizontal-menu"
          :style="headerMenuStyle"
          @select="handleHeaderMenuSelect"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <template v-for="menu in headerMenus" :key="menu.id">
            <NavbarMenuItem :menu="menu" />
          </template>
        </el-menu>
      </div>
    </div>

    <div class="navbar-right">
      <div class="navbar-actions">
        <HeaderActionButton
          v-if="preferences.widget.refresh"
          tooltip="刷新页面"
          @click="refreshPage"
        >
          <el-icon><RefreshRight /></el-icon>
        </HeaderActionButton>
        <ThemeToggle v-if="preferences.widget.themeToggle" />
        <HeaderActionButton
          v-if="preferences.widget.fullscreen"
          :tooltip="isFullscreen ? '退出全屏' : '全屏模式'"
          :active="isFullscreen"
          @click="toggleFullscreen"
        >
          <el-icon><component :is="isFullscreen ? Crop : FullScreen" /></el-icon>
        </HeaderActionButton>
        <HeaderActionButton
          v-if="preferences.app.preferencesButtonPosition !== 'fixed'"
          tooltip="偏好设置"
          @click="openPreferences"
        >
          <el-icon><Setting /></el-icon>
        </HeaderActionButton>
      </div>

      <UserDropdown
        :avatar="userStore.userInfo?.avatar"
        :description="preferences.app.name"
        :name="userStore.userInfo?.realName || '用户'"
        @change-password="openPasswordDialog"
        @logout="handleLogout"
      />
    </div>

    <ChangePasswordDialog v-model:visible="passwordDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Crop, FullScreen, RefreshRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { preferences, updatePreferences, usePreferences } from '@/utils/preferences'
import { resolveHeaderMenuAlignment } from '@/layouts/composables/menu-layout'
import { useSplitMenu } from '@/layouts/composables/use-split-menu'
import ChangePasswordDialog from '@/views/system/password/ChangePasswordDialog.vue'
import HeaderActionButton from './HeaderActionButton.vue'
import ThemeToggle from './ThemeToggle.vue'
import UserDropdown from './UserDropdown.vue'
import NavbarMenuItem from './NavbarMenuItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const passwordDialogVisible = ref(false)
const isFullscreen = ref(false)

const { isMobile, layoutState } = usePreferences()

const isHeaderNav = computed(() => layoutState.value.isHeaderNav)
const isMixedNav = computed(() => layoutState.value.isMixedNav)
const isHeaderSidebarNav = computed(() => layoutState.value.isHeaderSidebarNav)
const isHeaderMixedNav = computed(() => layoutState.value.isHeaderMixedNav)

const { navigateToMenu } = useSplitMenu({
  layoutState: computed(() => layoutState.value),
  menuTree: computed(() => permissionStore.menuTree),
  routePath: computed(() => route.path),
})

const showHeaderLogo = computed(() => {
  if (
    isHeaderNav.value ||
    isMixedNav.value ||
    isHeaderSidebarNav.value ||
    isHeaderMixedNav.value ||
    isMobile.value
  ) {
    return true
  }
  return false
})

const showHeaderMenu = computed(() => layoutState.value.showHeaderMenu)

const headerMenus = computed(() => {
  if (isMixedNav.value) {
    return permissionStore.menuTree.map((item: any) => ({ ...item, children: undefined }))
  }
  return permissionStore.menuTree
})

const showSidebarToggle = computed(() => {
  if (isMobile.value) return true
  return layoutState.value.sidebarVisible
})

const headerMenuStyle = computed(() => ({
  justifyContent: resolveHeaderMenuAlignment(preferences.header.menuAlign),
}))

function toggleSidebar() {
  updatePreferences({
    sidebar: { collapsed: !preferences.sidebar.collapsed },
  })
}

function openPasswordDialog() {
  passwordDialogVisible.value = true
}

function handleLogout() {
  userStore.logout()
}

function openPreferences() {
  updatePreferences({ app: { enablePreferences: true } })
}

function refreshPage() {
  window.location.reload()
}

async function toggleFullscreen() {
  if (document.fullscreenElement) {
    await document.exitFullscreen()
    isFullscreen.value = false
    return
  }

  await document.documentElement.requestFullscreen()
  isFullscreen.value = true
}

function handleHeaderMenuSelect(index: string) {
  const target = permissionStore.menuTree.find((menu) => index === `/${menu.path}` || index === menu.path)
  if (target && isMixedNav.value) {
    navigateToMenu(router, target)
  }
}
</script>

<style lang="scss" scoped>
.navbar-shell {
  height: 100%;
  width: 100%;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 0 20px;
  background: transparent;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 6px;
}

.logo-mark {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 95% 62%));
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 12px 24px hsl(var(--primary) / 0.2);
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  color: var(--text-foreground);
  font-size: 15px;
  font-weight: 700;
  line-height: 1.1;
}

.logo-subtitle {
  color: var(--text-muted-foreground);
  font-size: 11px;
  line-height: 1.1;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 0 0 auto;
  min-width: 0;
}

.navbar-center {
  flex: 1;
  min-width: 0;
}

.breadcrumb-panel {
  min-width: 0;
  padding-left: 4px;
}

.breadcrumb-label {
  margin-bottom: 2px;
  color: var(--text-muted-foreground);
  font-size: 11px;
  line-height: 1;
}

.header-menu {
  height: 100%;
  display: flex;
  align-items: stretch;
  padding: 6px;
  border: 1px solid hsl(var(--border));
  border-radius: 18px;
  background: hsl(var(--card) / 0.86);
  box-shadow: 0 14px 36px hsl(220 43% 11% / 0.08);
  backdrop-filter: blur(18px);
  min-width: 0;
}

.header-horizontal-menu {
  display: flex;
  flex: 1;
  border-bottom: none !important;
  background: transparent !important;
  --el-menu-text-color: var(--text-foreground);
  --el-menu-active-color: var(--color-primary);
  --el-menu-hover-text-color: var(--color-primary);
  --el-menu-bg-color: transparent;
  min-width: 0;
  --el-menu-item-height: 42px;
  --el-menu-active-color: var(--color-primary);

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    border-radius: 12px;
    margin: 0 4px;
  }

  :deep(.el-menu-item.is-active),
  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    background: hsl(var(--primary) / 0.12);
    box-shadow: inset 0 0 0 1px hsl(var(--primary) / 0.12);
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
