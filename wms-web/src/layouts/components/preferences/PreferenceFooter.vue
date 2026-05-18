<script setup lang="ts">
const footerEnable = defineModel<boolean>('footerEnable')
const footerFixed = defineModel<boolean>('footerFixed')
const footerHeight = defineModel<number>('footerHeight')
</script>

<template>
  <div class="preference-section">
    <div class="section-title">页脚</div>
    <div class="switch-group">
      <div class="switch-item">
        <span>显示页脚</span>
        <el-switch v-model="footerEnable" size="small" />
      </div>
      <div class="switch-item">
        <span>固定页脚</span>
        <el-switch v-model="footerFixed" size="small" />
      </div>
      <div class="number-item">
        <span>页脚高度</span>
        <el-input-number v-model="footerHeight" :min="20" :max="80" :step="4" size="small" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preference-section { margin-bottom: 20px; }
.section-title { font-size: 14px; font-weight: 500; margin-bottom: 12px; color: var(--text-foreground); }
.switch-group { display: flex; flex-direction: column; gap: 8px; }
.switch-item, .number-item { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
</style>
