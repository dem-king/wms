<script setup lang="ts">
const tabbarEnable = defineModel<boolean>('tabbarEnable')
const tabbarHeight = defineModel<number>('tabbarHeight')
const tabbarStyleType = defineModel<string>('tabbarStyleType')
const tabbarShowMore = defineModel<boolean>('tabbarShowMore')
const tabbarShowMaximize = defineModel<boolean>('tabbarShowMaximize')
const tabbarDraggable = defineModel<boolean>('tabbarDraggable')
</script>

<template>
  <div class="preference-section">
    <div class="section-title">标签页</div>
    <div class="control-list">
      <div class="control-item">
        <span>显示标签页</span>
        <el-switch v-model="tabbarEnable" size="small" />
      </div>
      <div class="control-item">
        <span>样式类型</span>
        <el-segmented
          v-model="tabbarStyleType"
          :options="[
            { label: 'Chrome', value: 'chrome' },
            { label: '卡片', value: 'card' },
            { label: '简洁', value: 'plain' },
          ]"
          size="small"
        />
      </div>
      <div class="control-item">
        <span>右侧更多</span>
        <el-switch v-model="tabbarShowMore" size="small" />
      </div>
      <!-- <div class="control-item">
        <span>最大化按钮</span>
        <el-switch v-model="tabbarShowMaximize" size="small" />
      </div> -->
      <div class="control-item">
        <span>拖拽排序</span>
        <el-switch v-model="tabbarDraggable" size="small" />
      </div>
      <div class="slider-item">
        <span>标签页高度</span>
        <el-slider
          v-model="tabbarHeight"
          :min="34"
          :max="54"
          :step="2"
          :show-input="true"
          input-size="small"
          style="width: 180px"
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

.control-item,
.slider-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--text-muted-foreground);
  font-size: 13px;
}
</style>
