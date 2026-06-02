<script setup lang="ts">
import { computed } from 'vue'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'
import type { VisualViewMode } from '../visual-state'
import type { ElementType } from '../types/layout-element'
import { ELEMENT_TYPES } from '../types/layout-element'
import BackgroundImageUploader from './BackgroundImageUploader.vue'

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
  /** 缩放百分比 */
  scalePercent: string
  /** 是否有底图 */
  hasBackground: boolean
  /** 底图透明度(0~1) */
  backgroundOpacity: number
  /** 当前绘制模式 */
  drawingMode: ElementType | null
  /** 是否有可撤销操作 */
  canUndo: boolean
  /** 是否有可重做操作 */
  canRedo: boolean
  /** 是否有待保存的元素变更 */
  hasPendingElementChanges: boolean
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
  (e: 'zoom-in'): void
  (e: 'zoom-out'): void
  (e: 'zoom-reset'): void
  (e: 'zoom-fit'): void
  (e: 'background-uploaded'): void
  (e: 'background-deleted'): void
  (e: 'update:backgroundOpacity', value: number): void
  (e: 'set-drawing-mode', elementType: ElementType | null): void
  (e: 'undo'): void
  (e: 'redo'): void
  (e: 'save-elements'): void
}>()

const viewModeOptions = computed(() => [
  { label: '2D', value: '2d' },
  { label: '2.5D', value: '2.5d' },
])

/** 元素类型选项列表 */
const elementTypeOptions = computed(() =>
  Object.entries(ELEMENT_TYPES).map(([value, label]) => ({
    label,
    value: value as ElementType,
  })),
)

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

/**
 * 切换绘制模式
 */
function handleDrawingModeToggle(elementType: ElementType) {
  if (props.drawingMode === elementType) {
    emit('set-drawing-mode', null)
  } else {
    emit('set-drawing-mode', elementType)
  }
}

/**
 * 底图透明度百分比
 */
const backgroundOpacityPercent = computed({
  get: () => Math.round(props.backgroundOpacity * 100),
  set: (val: number) => emit('update:backgroundOpacity', val / 100),
})
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
          :disabled="props.pendingLayoutCount === 0 && !props.hasPendingElementChanges"
          @click="emit('save-layout')"
        >
          保存布局
        </el-button>
      </div>
    </div>

    <!-- 缩放控制区 -->
    <div class="toolbar-secondary">
      <div class="toolbar-group">
        <span class="toolbar-label">缩放</span>
        <el-button-group>
          <el-button size="small" @click="emit('zoom-out')" title="缩小">
            <el-icon><ZoomOut /></el-icon>
          </el-button>
          <el-button size="small" disabled class="scale-display">
            {{ scalePercent }}
          </el-button>
          <el-button size="small" @click="emit('zoom-in')" title="放大">
            <el-icon><ZoomIn /></el-icon>
          </el-button>
        </el-button-group>
        <el-button size="small" @click="emit('zoom-reset')" title="重置100%">100%</el-button>
        <el-button size="small" @click="emit('zoom-fit')" title="适应画布">适应</el-button>
      </div>

      <!-- 底图控制区 -->
      <div class="toolbar-group">
        <span class="toolbar-label">底图</span>
        <BackgroundImageUploader
          v-if="selectedWarehouseId"
          :warehouse-id="selectedWarehouseId"
          :has-background="hasBackground"
          @uploaded="emit('background-uploaded')"
          @deleted="emit('background-deleted')"
        />
        <el-slider
          v-if="hasBackground"
          v-model="backgroundOpacityPercent"
          :min="0"
          :max="100"
          :step="5"
          :show-tooltip="true"
          :format-tooltip="(val: number) => val + '%'"
          style="width: 120px; margin-left: 8px;"
        />
      </div>

      <!-- 元素编辑工具区（编辑模式下显示） -->
      <div v-if="isEditMode" class="toolbar-group">
        <span class="toolbar-label">绘制元素</span>
        <el-button-group>
          <el-button
            v-for="opt in elementTypeOptions"
            :key="opt.value"
            size="small"
            :type="drawingMode === opt.value ? 'primary' : 'default'"
            @click="handleDrawingModeToggle(opt.value)"
          >
            {{ opt.label }}
          </el-button>
        </el-button-group>
      </div>

      <!-- 撤销/重做 -->
      <div v-if="isEditMode" class="toolbar-group">
        <span class="toolbar-label">操作</span>
        <el-button size="small" :disabled="!canUndo" @click="emit('undo')" title="撤销(Ctrl+Z)">
          <el-icon><RefreshLeft /></el-icon>撤销
        </el-button>
        <el-button size="small" :disabled="!canRedo" @click="emit('redo')" title="重做(Ctrl+Y)">
          <el-icon><RefreshRight /></el-icon>重做
        </el-button>
        <el-button
          v-if="hasPendingElementChanges"
          type="primary"
          size="small"
          @click="emit('save-elements')"
        >
          保存元素
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

<script lang="ts">
import { ZoomIn, ZoomOut, RefreshLeft, RefreshRight } from '@element-plus/icons-vue'
export default {
  components: { ZoomIn, ZoomOut, RefreshLeft, RefreshRight },
}
</script>

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

.toolbar-secondary {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.scale-display {
  min-width: 60px;
  cursor: default !important;
}
</style>
