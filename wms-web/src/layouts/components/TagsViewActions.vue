<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue'

defineOptions({ name: 'TagsViewActions' })

const emit = defineEmits<{
  closeAll: []
  closeOthers: []
}>()
</script>

<template>
  <div class="tags-view-actions">
    <el-dropdown trigger="click" placement="bottom-end">
      <button class="action-button" type="button">
        更多
        <el-icon><ArrowDown /></el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="emit('closeOthers')">关闭其他</el-dropdown-item>
          <el-dropdown-item divided @click="emit('closeAll')">关闭全部</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style lang="scss" scoped>
.tags-view-actions {
  display: flex;
  align-items: flex-end;
  padding: 0;
  height: 100%;
}

.action-button {
  height: 32px;
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-muted-foreground);
  background-color: transparent;
  border: 1px solid transparent;
  border-radius: 8px 8px 0 0; /* 与下方内容区域吸附 */
  margin-bottom: -1px; /* 遮盖底边框，实现物理上的吸附 */
  cursor: pointer;
  transition: all 0.2s ease;
  outline: none;
  position: relative;

  .el-icon {
    font-size: 12px;
    margin-top: 1px;
    transition: transform 0.3s ease;
  }

  &:hover {
    color: var(--color-primary);
    background-color: transparent;
    border-color: transparent;
    border-bottom-color: transparent;
    z-index: 1;
  }
}
</style>
