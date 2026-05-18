<script setup lang="ts">
const headerEnable = defineModel<boolean>('headerEnable')
const headerHidden = defineModel<boolean>('headerHidden')
const headerMode = defineModel<string>('headerMode')
const headerMenuAlign = defineModel<string>('headerMenuAlign')

defineProps<{ disabled: boolean }>()
</script>

<template>
  <div class="preference-section">
    <div class="section-title">顶栏</div>
    <div class="switch-group">
      <div class="switch-item">
        <span>显示顶栏</span>
        <el-switch v-model="headerEnable" :disabled="disabled" size="small" />
      </div>
      <div class="switch-item">
        <span>隐藏顶栏</span>
        <el-switch v-model="headerHidden" :disabled="disabled" size="small" />
      </div>
      <div class="switch-item">
        <span>顶栏模式</span>
        <el-select v-model="headerMode" size="small" style="width: 120px">
          <el-option label="固定" value="fixed" />
          <el-option label="静态" value="static" />
          <el-option label="自动" value="auto" />
          <el-option label="自动滚动" value="auto-scroll" />
        </el-select>
      </div>
      <div class="switch-item">
        <span>菜单对齐</span>
        <el-select v-model="headerMenuAlign" size="small" style="width: 120px">
          <el-option label="左对齐" value="start" />
          <el-option label="居中" value="center" />
          <el-option label="右对齐" value="end" />
        </el-select>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preference-section { margin-bottom: 20px; }
.section-title { font-size: 14px; font-weight: 500; margin-bottom: 12px; color: var(--text-foreground); }
.switch-group { display: flex; flex-direction: column; gap: 8px; }
.switch-item { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
</style>
