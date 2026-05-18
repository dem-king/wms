<script setup lang="ts">
const widgetThemeToggle = defineModel<boolean>('widgetThemeToggle')
const widgetFullscreen = defineModel<boolean>('widgetFullscreen')
const widgetRefresh = defineModel<boolean>('widgetRefresh')
const widgetSidebarToggle = defineModel<boolean>('widgetSidebarToggle')
const appPreferencesButtonPosition = defineModel<string>('appPreferencesButtonPosition')
</script>

<template>
  <div class="preference-section">
    <div class="section-title">顶部工具</div>
    <div class="control-list">
      <div class="control-item">
        <span>主题切换按钮</span>
        <el-switch v-model="widgetThemeToggle" size="small" />
      </div>
      <div class="control-item">
        <span>侧栏折叠按钮</span>
        <el-switch v-model="widgetSidebarToggle" size="small" />
      </div>
      <div class="control-item">
        <span>全屏按钮</span>
        <el-switch v-model="widgetFullscreen" size="small" />
      </div>
      <div class="control-item">
        <span>刷新按钮</span>
        <el-switch v-model="widgetRefresh" size="small" />
      </div>
      <div class="control-item">
        <span>偏好按钮位置</span>
        <el-segmented
          v-model="appPreferencesButtonPosition"
          :options="[
            { label: '自动', value: 'auto' },
            { label: '顶栏', value: 'header' },
            { label: '固定', value: 'fixed' },
          ]"
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
  margin-bottom: 12px;
  color: var(--text-foreground);
  font-size: 14px;
  font-weight: 600;
}

.control-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.control-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--text-muted-foreground);
  font-size: 13px;
}
</style>
