<script setup lang="ts">
import type { ThemeModeType } from '@/types/preferences'

import { Sunny, Moon, Sunrise } from '@element-plus/icons-vue'

const themeMode = defineModel<string>('themeMode', { default: 'auto' })
const themeSemiDarkSidebar = defineModel<boolean>('themeSemiDarkSidebar')
const themeSemiDarkHeader = defineModel<boolean>('themeSemiDarkHeader')

interface ThemeOption {
  icon: any
  name: ThemeModeType
  label: string
}

const THEME_PRESET: ThemeOption[] = [
  { icon: Sunny, name: 'light', label: '亮色' },
  { icon: Moon, name: 'dark', label: '暗黑' },
  { icon: Sunrise, name: 'auto', label: '跟随系统' },
]
</script>

<template>
  <div class="preference-section">
    <div class="section-title">主题模式</div>
    <div class="theme-mode-list">
      <div
        v-for="item in THEME_PRESET"
        :key="item.name"
        class="theme-mode-item"
        :class="{ active: themeMode === item.name }"
        @click="themeMode = item.name"
      >
        <div class="mode-icon-box">
          <el-icon :size="20"><component :is="item.icon" /></el-icon>
        </div>
        <div class="mode-label">{{ item.label }}</div>
      </div>
    </div>
    <div class="switch-group">
      <div class="switch-item">
        <span>暗色侧边栏</span>
        <el-switch
          v-model="themeSemiDarkSidebar"
          :disabled="themeMode === 'dark'"
          size="small"
        />
      </div>
      <div class="switch-item">
        <span>暗色顶栏</span>
        <el-switch
          v-model="themeSemiDarkHeader"
          :disabled="themeMode === 'dark'"
          size="small"
        />
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
.theme-mode-list {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.theme-mode-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  &.active .mode-icon-box {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px hsl(var(--primary) / 20%);
  }
}
.mode-icon-box {
  width: 56px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--color-border);
  border-radius: 8px;
  margin-bottom: 6px;
  transition: all 0.2s;
  &:hover {
    border-color: var(--color-primary);
  }
}
.mode-label {
  font-size: 12px;
  color: var(--text-muted-foreground);
}
.switch-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.switch-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}
</style>
