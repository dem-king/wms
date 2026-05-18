<script setup lang="ts">
import type { LayoutType } from '@/types/preferences'

const appLayout = defineModel<LayoutType>('appLayout', { default: 'sidebar-nav' })

interface LayoutOption {
  name: string
  tip: string
  type: LayoutType
}

const PRESET: LayoutOption[] = [
  { name: '侧边导航', tip: '经典侧边栏布局', type: 'sidebar-nav' },
  { name: '双栏', tip: '左根菜单 + 右子菜单', type: 'sidebar-mixed-nav' },
  { name: '顶部导航', tip: '水平导航菜单', type: 'header-nav' },
  { name: '顶部+侧边', tip: '顶部操作区 + 左完整菜单', type: 'header-sidebar-nav' },
  { name: '混合菜单', tip: '顶部根菜单 + 左子菜单', type: 'mixed-nav' },
  { name: '顶部双栏', tip: '顶部操作区 + 双栏菜单', type: 'header-mixed-nav' },
  { name: '全屏内容', tip: '隐藏所有导航', type: 'full-content' },
]

/** 布局缩略图的SVG图标模拟 */
function getLayoutIcon(type: LayoutType): string {
  const icons: Record<string, string> = {
    'sidebar-nav': 'sidebar',
    'sidebar-mixed-nav': 'sidebar-mixed',
    'header-nav': 'header',
    'header-sidebar-nav': 'header-sidebar',
    'mixed-nav': 'mixed',
    'header-mixed-nav': 'header-mixed',
    'full-content': 'full',
  }
  return icons[type] || 'sidebar'
}
</script>

<template>
  <div class="preference-section">
    <div class="section-title">布局模式</div>
    <div class="layout-list">
      <div
        v-for="item in PRESET"
        :key="item.type"
        class="layout-item"
        @click="appLayout = item.type"
      >
        <div
          class="layout-icon-box"
          :class="{ active: appLayout === item.type }"
          :title="item.tip"
        >
          <div class="layout-preview" :class="getLayoutIcon(item.type)">
            <div v-if="item.type !== 'full-content'" class="preview-sidebar"></div>
            <div class="preview-main">
              <div v-if="item.type.includes('header') || item.type === 'mixed-nav'" class="preview-header"></div>
              <div class="preview-content"></div>
            </div>
          </div>
        </div>
        <div class="layout-label">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preference-section {
  margin-bottom: 20px;
}
.section-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
  color: var(--text-foreground);
}
.layout-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.layout-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.layout-icon-box {
  width: 72px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--color-border);
  border-radius: 6px;
  margin-bottom: 4px;
  transition: all 0.2s;
  &.active {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px hsl(var(--primary) / 20%);
  }
  &:hover {
    border-color: var(--color-primary);
  }
}
.layout-preview {
  width: 56px;
  height: 32px;
  display: flex;
  border-radius: 2px;
  overflow: hidden;
  &.full .preview-content { width: 100%; }
}
.preview-sidebar {
  width: 16px;
  background: hsl(var(--muted-foreground) / 30%);
  flex-shrink: 0;
}
.preview-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.preview-header {
  height: 6px;
  background: hsl(var(--muted-foreground) / 20%);
  flex-shrink: 0;
}
.preview-content {
  flex: 1;
  background: hsl(var(--accent));
}
.layout-label {
  font-size: 11px;
  color: var(--text-muted-foreground);
  text-align: center;
}
</style>
