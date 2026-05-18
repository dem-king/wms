<template>
  <el-sub-menu v-if="menu.menuType === 1 && menu.children && menu.children.length > 0" :index="resolvePath(menu)">
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

  <el-menu-item v-else :index="resolvePath(menu)">
    <el-icon v-if="menu.icon">
      <component :is="menu.icon" />
    </el-icon>
    <template #title>{{ menu.menuName }}</template>
  </el-menu-item>
</template>

<script setup lang="ts">
import type { MenuTreeNode } from '@/types/auth'

const props = withDefaults(
  defineProps<{
    menu: MenuTreeNode
    basePath?: string
  }>(),
  { basePath: '' }
)

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
