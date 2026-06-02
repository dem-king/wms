<template>
  <div class="navbar-shell" :class="{ 'has-header-menu': showHeaderMenu }">
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
      <div
        class="header-menu"
        :class="{
          'is-overflowing': isHeaderMenuOverflowing,
          'can-scroll-left': canScrollLeft,
          'can-scroll-right': canScrollRight,
        }"
      >
        <slot name="menu"></slot>
        <button
          v-if="isHeaderMenuOverflowing"
          class="header-scroll-button left"
          type="button"
          :disabled="!canScrollLeft"
          aria-label="向左滚动菜单"
          @click="scrollHeaderMenuBy(-headerMenuScrollStep)"
        >
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <div
          ref="headerMenuScrollRef"
          class="header-menu-scroll"
          @scroll="syncHeaderMenuScrollState"
          @wheel="handleHeaderMenuWheel"
        >
          <el-menu
            :default-active="route.path"
            mode="horizontal"
            :ellipsis="false"
            router
            class="header-horizontal-menu"
            :style="headerMenuStyle"
            @select="handleHeaderMenuSelect"
          >
            <el-menu-item
              index="/dashboard"
              :class="{ 'is-root-highlighted': isMixedNav && !activeRootMenuId && route.path === '/dashboard' }"
            >
              <el-icon><HomeFilled /></el-icon>
              <template #title>首页</template>
            </el-menu-item>
            <template v-for="menu in headerMenus" :key="menu.id">
              <NavbarMenuItem
                :menu="menu"
                :active-root-menu-id="activeRootMenuId"
                :current-path="route.path"
                :highlight-root="isMixedNav"
              />
            </template>
          </el-menu>
        </div>
        <button
          v-if="isHeaderMenuOverflowing"
          class="header-scroll-button right"
          type="button"
          :disabled="!canScrollRight"
          aria-label="向右滚动菜单"
          @click="scrollHeaderMenuBy(headerMenuScrollStep)"
        >
          <el-icon><ArrowRight /></el-icon>
        </button>
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
        <NotificationBell />
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
        :name="userStore.userInfo?.realName || userStore.userInfo?.username || '用户'"
        @profile="openProfile"
        @change-password="openPasswordDialog"
        @logout="handleLogout"
      />
    </div>

    <ChangePasswordDialog v-model:visible="passwordDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onBeforeUnmount, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, Crop, FullScreen, RefreshRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { preferences, updatePreferences, usePreferences } from '@/utils/preferences'
import { resolveHeaderMenuAlignment, resolveHorizontalScrollState } from '@/layouts/composables/menu-layout'
import { useSplitMenu } from '@/layouts/composables/use-split-menu'
import ChangePasswordDialog from '@/views/system/password/ChangePasswordDialog.vue'
import HeaderActionButton from './HeaderActionButton.vue'
import NotificationBell from './NotificationBell.vue'
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

const { activeRootMenuId, navigateToMenu } = useSplitMenu({
  layoutState: computed(() => layoutState.value),
  menuTree: computed(() => permissionStore.menuTree),
  routePath: computed(() => route.path),
})

const headerMenuScrollRef = ref<HTMLDivElement>()
const headerMenuScrollStep = 240
const isHeaderMenuOverflowing = ref(false)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

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

function syncHeaderMenuScrollState() {
  const el = headerMenuScrollRef.value
  if (!el) return

  const nextState = resolveHorizontalScrollState({
    clientWidth: el.clientWidth,
    scrollLeft: el.scrollLeft,
    scrollWidth: el.scrollWidth,
  })

  isHeaderMenuOverflowing.value = nextState.isOverflowing
  canScrollLeft.value = nextState.canScrollLeft
  canScrollRight.value = nextState.canScrollRight
}

function handleHeaderMenuWheel(event: WheelEvent) {
  const el = headerMenuScrollRef.value
  if (!el) return

  if (Math.abs(event.deltaY) <= Math.abs(event.deltaX)) {
    return
  }

  if (el.scrollWidth <= el.clientWidth) {
    return
  }

  event.preventDefault()
  el.scrollLeft += event.deltaY
  syncHeaderMenuScrollState()
}

function scrollHeaderMenuBy(distance: number) {
  const el = headerMenuScrollRef.value
  if (!el) return

  el.scrollBy({
    left: distance,
    behavior: 'smooth',
  })

  window.setTimeout(() => {
    syncHeaderMenuScrollState()
  }, 180)
}

function toggleSidebar() {
  updatePreferences({
    sidebar: { collapsed: !preferences.sidebar.collapsed },
  })
}

function openPasswordDialog() {
  passwordDialogVisible.value = true
}

function openProfile() {
  router.push('/profile')
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

function registerHeaderResizeObserver() {
  const el = headerMenuScrollRef.value
  if (!el || typeof ResizeObserver === 'undefined') {
    return () => undefined
  }

  const observer = new ResizeObserver(() => {
    syncHeaderMenuScrollState()
  })
  observer.observe(el)

  return () => observer.disconnect()
}

let cleanupResizeObserver: () => void = () => {}

onMounted(() => {
  nextTick(() => {
    syncHeaderMenuScrollState()
    cleanupResizeObserver = registerHeaderResizeObserver()
  })
})

onBeforeUnmount(() => {
  cleanupResizeObserver()
})

watch(
  [() => route.path, headerMenus, () => preferences.header.menuAlign],
  () => nextTick(() => syncHeaderMenuScrollState()),
)
</script>

<style lang="scss" scoped>
.navbar-shell {
  height: 100%;
  width: 100%;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 0 20px;
  background: transparent;
  min-width: 0;
}

.navbar-shell.has-header-menu {
  gap: 14px;
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
  display: flex;
  align-items: center;
  min-width: 0;
  align-self: stretch;
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
  align-items: center;
  padding: 0;
  min-width: 0;
  flex: 1;
  overflow: hidden;
  position: relative;

  &::before,
  &::after {
    content: '';
    position: absolute;
    top: 8px;
    bottom: 8px;
    width: 24px;
    pointer-events: none;
    opacity: 0;
    transition: opacity 0.2s ease;
    z-index: 1;
  }

  &::before {
    left: 0;
    background: linear-gradient(90deg, hsl(var(--header) / 0.95), transparent);
  }

  &::after {
    right: 0;
    background: linear-gradient(270deg, hsl(var(--header) / 0.95), transparent);
  }

  &.can-scroll-left::before {
    opacity: 1;
  }

  &.can-scroll-right::after {
    opacity: 1;
  }
}

.header-scroll-button {
  position: absolute;
  top: 50%;
  z-index: 2;
  width: 28px;
  height: 28px;
  border: 1px solid hsl(var(--border));
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: hsl(var(--card) / 0.94);
  color: var(--text-foreground);
  box-shadow: 0 8px 18px hsl(220 43% 11% / 0.08);
  transform: translateY(-50%);
  transition:
    opacity 0.2s ease,
    background-color 0.2s ease,
    color 0.2s ease,
    border-color 0.2s ease;

  &.left {
    left: 4px;
  }

  &.right {
    right: 4px;
  }

  &:hover:not(:disabled) {
    color: var(--color-primary);
    border-color: hsl(var(--primary) / 0.24);
    background: hsl(var(--card));
  }

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.header-menu-scroll {
  flex: 1;
  min-width: 0;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.header-menu.is-overflowing .header-menu-scroll {
  padding: 0 32px;
}

.header-horizontal-menu {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  height: 100%;
  border-bottom: none !important;
  background: transparent !important;
  --el-menu-text-color: var(--text-foreground);
  --el-menu-active-color: var(--color-primary);
  --el-menu-hover-text-color: var(--color-primary);
  --el-menu-bg-color: transparent;
  min-width: 0;
  white-space: nowrap;
  --el-menu-item-height: v-bind('Math.max(preferences.header.height - 14, 40) + "px"');
  --el-menu-horizontal-height: 100%;

  :deep(.el-menu-item),
  :deep(.el-sub-menu),
  :deep(.el-sub-menu__title) {
    min-width: 0;
    border-bottom: none !important;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: v-bind('Math.max(preferences.header.height - 14, 40) + "px"');
    display: inline-flex;
    align-items: center;
    line-height: 1;
    padding: 0 14px;
    border-radius: 12px;
    margin: 0 4px;
  }

  :deep(.el-sub-menu__icon-arrow) {
    position: static;
    margin-top: 0;
    margin-left: 6px;
    right: auto;
  }

  :deep(.el-menu-item .el-icon),
  :deep(.el-sub-menu__title .el-icon) {
    margin-right: 8px;
  }

  :deep(.el-menu-item .el-tooltip__trigger),
  :deep(.el-sub-menu__title .el-tooltip__trigger) {
    display: inline-flex;
    align-items: center;
    min-width: 0;
  }

  :deep(.el-menu-item span),
  :deep(.el-sub-menu__title span) {
    overflow: hidden;
    text-overflow: ellipsis;
  }

  :deep(.el-menu-item.is-active),
  :deep(.el-menu-item.is-root-highlighted),
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
