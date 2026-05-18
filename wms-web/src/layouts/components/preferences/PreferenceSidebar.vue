<script setup lang="ts">
import type { LayoutType } from '@/types/preferences'

defineProps<{
  currentLayout?: LayoutType
  disabled: boolean
  sidebarPreferenceState: {
    allowHoverExpand: boolean
    showMixedWidth: boolean
  }
}>()

const sidebarEnable = defineModel<boolean>('sidebarEnable')
const sidebarWidth = defineModel<number>('sidebarWidth')
const sidebarMixedWidth = defineModel<number>('sidebarMixedWidth')
const sidebarCollapsedShowTitle = defineModel<boolean>('sidebarCollapsedShowTitle')
const sidebarCollapsed = defineModel<boolean>('sidebarCollapsed')
const sidebarExpandOnHover = defineModel<boolean>('sidebarExpandOnHover')
const sidebarCollapsedButton = defineModel<boolean>('sidebarCollapsedButton')
const sidebarFixedButton = defineModel<boolean>('sidebarFixedButton')
</script>

<template>
  <div class="preference-section">
    <div class="section-title">侧边栏</div>
    <div class="switch-group">
      <div class="switch-item">
        <span>显示侧边栏</span>
        <el-switch v-model="sidebarEnable" :disabled="disabled" size="small" />
      </div>
      <div class="switch-item">
        <span>折叠侧边栏</span>
        <el-switch v-model="sidebarCollapsed" :disabled="!sidebarEnable || disabled" size="small" />
      </div>
      <div class="switch-item">
        <span>悬停展开</span>
        <el-switch
          v-model="sidebarExpandOnHover"
          :disabled="disabled || !sidebarPreferenceState.allowHoverExpand"
          size="small"
        />
      </div>
      <div class="switch-item">
        <span>折叠时显示标题</span>
        <el-switch
          v-model="sidebarCollapsedShowTitle"
          :disabled="!sidebarEnable || disabled || !sidebarCollapsed"
          size="small"
        />
      </div>
      <div class="switch-item">
        <span>折叠按钮</span>
        <el-switch v-model="sidebarCollapsedButton" :disabled="!sidebarEnable || disabled" size="small" />
      </div>
      <div class="switch-item">
        <span>固定按钮</span>
        <el-switch v-model="sidebarFixedButton" :disabled="!sidebarEnable || disabled" size="small" />
      </div>
      <div class="number-item">
        <span>侧边栏宽度</span>
        <el-slider
          v-model="sidebarWidth"
          :disabled="!sidebarEnable || disabled"
          :min="160"
          :max="320"
          :step="10"
          :show-input="true"
          input-size="small"
          style="width: 180px"
        />
      </div>
      <div v-if="sidebarPreferenceState.showMixedWidth" class="number-item">
        <span>双栏主菜单宽度</span>
        <el-slider
          v-model="sidebarMixedWidth"
          :disabled="!sidebarEnable || disabled"
          :min="64"
          :max="140"
          :step="4"
          :show-input="true"
          input-size="small"
          style="width: 180px"
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preference-section { margin-bottom: 20px; }
.section-title { font-size: 14px; font-weight: 500; margin-bottom: 12px; color: var(--text-foreground); }
.switch-group { display: flex; flex-direction: column; gap: 8px; }
.switch-item, .number-item {
  display: flex; justify-content: space-between; align-items: center; font-size: 13px;
}
</style>
