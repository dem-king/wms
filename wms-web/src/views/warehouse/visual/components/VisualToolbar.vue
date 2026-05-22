<script setup lang="ts">
import { computed } from 'vue'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'
import type { VisualViewMode } from '../visual-state'

const props = defineProps<{
  warehouseList: WmsWarehouseVo[]
  selectedWarehouseId?: EntityId
  loading: boolean
  viewMode: VisualViewMode
  quickLocateKeyword: string
  quickLocateFeedback: string
  isEditMode: boolean
  pendingLayoutCount: number
  isSavingLayout: boolean
  layoutFeedbackType: '' | 'success' | 'error'
  layoutFeedbackMessage: string
}>()

const emit = defineEmits<{
  (e: 'change-warehouse', value: EntityId): void
  (e: 'refresh'): void
  (e: 'change-view-mode', value: VisualViewMode): void
  (e: 'update:quickLocateKeyword', value: string): void
  (e: 'quick-locate'): void
  (e: 'toggle-edit-mode', value: boolean): void
  (e: 'save-layout'): void
  (e: 'discard-layout'): void
}>()

const viewModeOptions = computed(() => [
  { label: '2D', value: '2d' },
  { label: '2.5D', value: '2.5d' },
])

function handleWarehouseChange(value: EntityId | undefined) {
  if (typeof value === 'string' && value) {
    emit('change-warehouse', value)
  }
}

function handleViewModeChange(value: string | number | boolean | undefined) {
  if (value === '2d' || value === '2.5d') {
    emit('change-view-mode', value)
  }
}

function resolveLayoutFeedbackType() {
  if (props.layoutFeedbackType === 'success') {
    return 'success'
  }
  if (props.layoutFeedbackType === 'error') {
    return 'error'
  }
  return 'info'
}
</script>

<template>
  <el-card class="toolbar-card" shadow="never">
    <div class="toolbar">
      <div class="toolbar-main">
        <div class="toolbar-group">
          <span class="toolbar-label">库房选择</span>
          <el-select
            :model-value="props.selectedWarehouseId"
            placeholder="请选择库房"
            style="width: 280px"
            filterable
            @change="handleWarehouseChange"
          >
            <el-option
              v-for="warehouse in props.warehouseList"
              :key="warehouse.id"
              :label="warehouse.warehouseName"
              :value="warehouse.id"
            />
          </el-select>
        </div>

        <div class="toolbar-group">
          <span class="toolbar-label">视图模式</span>
          <el-radio-group
            :model-value="props.viewMode"
            @update:model-value="handleViewModeChange"
          >
            <el-radio-button
              v-for="option in viewModeOptions"
              :key="option.value"
              :label="option.value"
            >
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
        </div>

        <div class="toolbar-group toolbar-search">
          <span class="toolbar-label">快速定位</span>
          <el-input
            :model-value="props.quickLocateKeyword"
            placeholder="输入区域/柜/库位编码"
            clearable
            @update:model-value="emit('update:quickLocateKeyword', $event)"
            @keyup.enter="emit('quick-locate')"
          >
            <template #append>
              <el-button @click="emit('quick-locate')">定位</el-button>
            </template>
          </el-input>
        </div>

        <div class="toolbar-group">
          <span class="toolbar-label">编辑模式</span>
          <el-switch
            :model-value="props.isEditMode"
            active-text="开启"
            inactive-text="关闭"
            @update:model-value="emit('toggle-edit-mode', Boolean($event))"
          />
        </div>
      </div>

      <div class="toolbar-actions">
        <el-button :loading="props.loading" @click="emit('refresh')">刷新布局</el-button>
        <el-button
          v-if="props.pendingLayoutCount > 0"
          :disabled="props.isSavingLayout"
          @click="emit('discard-layout')"
        >
          放弃变更
        </el-button>
        <el-button
          type="primary"
          :loading="props.isSavingLayout"
          :disabled="props.pendingLayoutCount === 0"
          @click="emit('save-layout')"
        >
          保存布局
        </el-button>
      </div>
    </div>

    <div v-if="props.quickLocateFeedback" class="toolbar-feedback">
      <el-alert :title="props.quickLocateFeedback" type="info" :closable="false" show-icon />
    </div>
    <div class="toolbar-feedback">
      <el-alert
        :title="props.pendingLayoutCount > 0 ? `当前有 ${props.pendingLayoutCount} 个存放柜待保存` : '当前无待保存布局变更'"
        :type="props.pendingLayoutCount > 0 ? 'warning' : 'success'"
        :closable="false"
        show-icon
      />
    </div>
    <div v-if="props.layoutFeedbackMessage" class="toolbar-feedback">
      <el-alert
        :title="props.layoutFeedbackMessage"
        :type="resolveLayoutFeedbackType()"
        :closable="false"
        show-icon
      />
    </div>
  </el-card>
</template>

<style scoped lang="scss">
.toolbar-card {
  margin-bottom: 16px;
}

.toolbar,
.toolbar-main,
.toolbar-group {
  display: flex;
  align-items: center;
}

.toolbar {
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-main {
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-group {
  gap: 12px;
}

.toolbar-label {
  color: #909399;
  white-space: nowrap;
}

.toolbar-search {
  min-width: 320px;
}

.toolbar-feedback {
  margin-top: 12px;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
