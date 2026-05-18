<template>
  <div class="sidebar-container" :class="{ 'semi-dark': preferences.theme.semiDarkSidebar || isDark }">
    <!-- 双栏布局的左侧（主菜单） -->
    <div v-if="isSidebarMixedNav || isHeaderMixedNav" class="sidebar-mixed-left" :style="{ width: preferences.sidebar.mixedWidth + 'px' }">
      <div v-if="!isHeaderMixedNav" class="logo-mixed">
        <h1>W</h1>
      </div>
      <el-scrollbar>
        <div class="mixed-menu-list">
          <div 
            class="mixed-menu-item" 
            :class="{ active: route.path === '/dashboard' }"
            @click="router.push('/dashboard')"
          >
            <el-icon><HomeFilled /></el-icon>
            <span class="title">首页</span>
          </div>
          <div 
            v-for="menu in permissionStore.menuTree" 
            :key="menu.id"
            class="mixed-menu-item"
            :class="{ active: activeRootMenu?.id === menu.id }"
            @click="handleRootMenuClick(menu)"
          >
            <el-icon v-if="menu.icon">
              <component :is="menu.icon" />
            </el-icon>
            <span class="title">{{ menu.menuName }}</span>
          </div>
        </div>
      </el-scrollbar>
    </div>

    <!-- 常规侧边栏 或 双栏布局的右侧（子菜单） -->
    <div class="sidebar-main" :style="mainSidebarStyle">
      <div v-if="!isSplitMenu && !isHeaderSidebarNav" class="logo">
        <h1 v-if="!preferences.sidebar.collapsed">WMS</h1>
        <h1 v-else>W</h1>
      </div>
      <el-scrollbar v-if="sidebarMenus.length > 0 || !isSplitMenu">
        <el-menu
          :default-active="route.path"
          :collapse="isSplitMenu ? false : preferences.sidebar.collapsed"
          router
        >
          <el-menu-item v-if="!isSplitMenu" index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <template v-for="menu in sidebarMenus" :key="menu.id">
            <sidebar-menu-item :menu="menu" :base-path="isSplitMenu ? sidebarBasePath : ''" />
          </template>
        </el-menu>
      </el-scrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuTreeNode } from '@/types/auth'
import { usePermissionStore } from '@/store/modules/permission'
import { preferences, usePreferences } from '@/utils/preferences'
import { useSplitMenu } from '@/layouts/composables/use-split-menu'
import SidebarMenuItem from './SidebarMenuItem.vue'

defineProps<{
  isMixed?: boolean
}>()

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const { isDark, layoutState } = usePreferences()

const isSidebarMixedNav = computed(() => layoutState.value.isSidebarMixedNav)
const isHeaderMixedNav = computed(() => layoutState.value.isHeaderMixedNav)
const isHeaderSidebarNav = computed(() => layoutState.value.isHeaderSidebarNav)
const isSplitMenu = computed(() => layoutState.value.isSplitMenu)

const menuTree = computed(() => permissionStore.menuTree)
const { activeRootMenu, navigateToMenu, sidebarBasePath, sidebarMenus: splitSidebarMenus } = useSplitMenu({
  layoutState: computed(() => layoutState.value),
  menuTree,
  routePath: computed(() => route.path),
})

const sidebarMenus = computed(() => {
  if (isSplitMenu.value) {
    return splitSidebarMenus.value
  }
  return menuTree.value
})

const mainSidebarStyle = computed(() => {
  let width = '100%'
  if (isSidebarMixedNav.value || isHeaderMixedNav.value) {
    width = `${preferences.sidebar.width}px`
  }

  const isHidden =
    (isSidebarMixedNav.value || isHeaderMixedNav.value) &&
    preferences.sidebar.collapsed &&
    !preferences.sidebar.expandOnHover
  
  return {
    width,
    flex: 1,
    display: isHidden ? 'none' : 'flex',
    flexDirection: 'column' as const,
  }
})

function handleRootMenuClick(menu: MenuTreeNode) {
  navigateToMenu(router, menu)
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;
  display: flex;
  background-color: var(--bg-sidebar);
  color: var(--text-card-foreground);
  backdrop-filter: blur(18px);
}

.sidebar-mixed-left {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--color-border);
  background-color: var(--bg-sidebar-deep);
  
  .mixed-menu-list {
    padding: 8px 0;
  }
  
  .mixed-menu-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    min-height: 72px;
    padding: 12px 6px;
    margin: 4px 8px;
    border: 1px solid transparent;
    border-radius: 16px;
    cursor: pointer;
    color: var(--text-muted-foreground);
    transition: all 0.2s ease;
    
    .el-icon {
      font-size: 20px;
    }
    
    .title {
      font-size: 12px;
      text-align: center;
      line-height: 1.2;
    }
    
    &:hover {
      color: var(--text-foreground);
      background-color: hsl(var(--accent));
      border-color: hsl(var(--border));
      box-shadow: 0 8px 18px hsl(var(--primary) / 0.08);
    }
    
    &.active {
      color: var(--color-primary);
      background: linear-gradient(180deg, hsl(var(--primary) / 0.16), hsl(var(--primary) / 0.08));
      border-color: hsl(var(--primary) / 0.22);
      box-shadow: 0 12px 24px hsl(var(--primary) / 0.12);
    }
  }
}

.sidebar-main {
  height: 100%;
  min-width: 0;
  padding: 12px 10px 14px;
  
  :deep(.el-menu) {
    background-color: transparent;
    border-right: none;
    --el-menu-text-color: var(--text-muted-foreground);
    --el-menu-active-color: var(--color-primary);
    --el-menu-hover-text-color: var(--color-primary);
    --el-menu-hover-bg-color: hsl(var(--accent));
    --el-menu-item-height: 46px;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    margin-bottom: 4px;
    border-radius: 14px;
  }

  :deep(.el-menu-item.is-active) {
    color: var(--color-primary) !important;
    background: linear-gradient(90deg, hsl(var(--primary) / 0.14), hsl(var(--primary) / 0.04)) !important;
    box-shadow: inset 0 0 0 1px hsl(var(--primary) / 0.1);
  }
}

.sidebar-container:hover .sidebar-main {
  display: flex !important;
}

.sidebar-container.semi-dark {
  background-color: hsl(var(--sidebar-deep));

  .logo,
  .logo-mixed {
    background-color: hsl(var(--sidebar-deep));
  }
  
  .sidebar-mixed-left {
    background-color: hsl(var(--sidebar-deep));
    border-right-color: hsl(var(--accent-darker));
    
    .mixed-menu-item {
      color: hsl(var(--foreground) / 70%);
      
      &:hover {
        color: hsl(var(--foreground));
        background-color: hsl(var(--accent) / 50%);
      }
      
      &.active {
        color: var(--color-primary);
        background: linear-gradient(180deg, hsl(var(--primary) / 0.18), hsl(var(--primary) / 0.08));
      }
    }
  }

  :deep(.el-menu) {
    --el-menu-text-color: hsl(var(--foreground) / 70%);
    --el-menu-hover-bg-color: hsl(var(--accent) / 50%);
    --el-menu-hover-text-color: hsl(var(--foreground));
  }

  :deep(.el-menu-item.is-active) {
    color: var(--color-primary) !important;
  }
}

.logo {
  height: v-bind('preferences.header.height + "px"');
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  background-color: var(--bg-sidebar-deep);
  h1 { margin: 0; font-size: 18px; font-weight: 700; }
}

.logo-mixed {
  height: v-bind('preferences.header.height + "px"');
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  background-color: var(--bg-sidebar-deep);
  h1 { margin: 0; font-size: 16px; font-weight: 700; }
}
</style>
