<template>
  <el-sub-menu
    v-if="menu.menuType === 1 && menu.children && menu.children.length > 0"
    :index="resolvePath(menu)"
    :class="{ 'is-root-highlighted': rootHighlighted }"
  >
    <template #title>
      <el-icon v-if="menu.icon">
        <component :is="menu.icon" />
      </el-icon>
      <span>{{ menu.menuName }}</span>
    </template>
    <navbar-menu-item
      v-for="child in menu.children"
      :key="child.id"
      :menu="child"
      :base-path="resolvePath(menu)"
    />
  </el-sub-menu>

  <el-menu-item v-else :index="resolvePath(menu)" :class="{ 'is-root-highlighted': rootHighlighted }">
    <el-icon v-if="menu.icon">
      <component :is="menu.icon" />
    </el-icon>
    <template #title>{{ menu.menuName }}</template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuTreeNode } from '@/types/auth'
import { isRootMenuHighlighted } from '@/layouts/composables/menu-layout'

const props = withDefaults(
  defineProps<{
    menu: MenuTreeNode
    activeRootMenuId?: MenuTreeNode['id'] | null
    basePath?: string
    currentPath?: string
    highlightRoot?: boolean
  }>(),
  {
    activeRootMenuId: null,
    basePath: '',
    currentPath: '',
    highlightRoot: false,
  }
)

const rootHighlighted = computed(() => {
  if (!props.highlightRoot) {
    return false
  }

  return isRootMenuHighlighted(props.menu, props.currentPath, props.activeRootMenuId)
})

function resolvePath(menu: MenuTreeNode): string {
  if (menu.path.startsWith('/')) {
    return menu.path
  }
  if (props.basePath) {
    return `${props.basePath}/${menu.path}`
  }
  return `/${menu.path}`
}
</script>

<style lang="scss" scoped>
.is-root-highlighted {
  :deep(> .el-sub-menu__title),
  &.el-menu-item {
    color: var(--color-primary) !important;
    background: linear-gradient(180deg, hsl(var(--primary) / 0.16), hsl(var(--primary) / 0.08)) !important;
    box-shadow: inset 0 0 0 1px hsl(var(--primary) / 0.18);
  }
}
</style>
