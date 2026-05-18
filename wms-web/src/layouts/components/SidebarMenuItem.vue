<template>
  <!-- 目录类型：渲染子菜单组 -->
  <el-sub-menu v-if="menu.menuType === 1 && menu.children && menu.children.length > 0" :index="resolvePath(menu)">
    <template #title>
      <el-icon v-if="menu.icon">
        <component :is="menu.icon" />
      </el-icon>
      <span>{{ menu.menuName }}</span>
    </template>
    <!-- 递归渲染子菜单，传递 basePath 用于拼接完整路径 -->
    <sidebar-menu-item
      v-for="child in menu.children"
      :key="child.id"
      :menu="child"
      :base-path="resolvePath(menu)"
    />
  </el-sub-menu>

  <!-- 菜单类型：渲染菜单项 -->
  <el-menu-item v-else-if="menu.menuType === 2" :index="resolvePath(menu)">
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

/**
 * 解析菜单完整路径
 * 目录类型：basePath 为空时返回 /${menu.path}，否则返回 ${basePath}/${menu.path}
 * 菜单类型：拼接 basePath + / + menu.path，生成与动态路由一致的完整路径
 */
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
