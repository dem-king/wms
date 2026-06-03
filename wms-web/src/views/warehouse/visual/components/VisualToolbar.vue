<script setup lang="ts">
/**
 * 库房可视化工具栏（重写版）
 * 新增：3D/2D视图切换、预设视角按钮、返回全景按钮、底图透明度
 * 保留：库房选择、编码搜索、编辑模式、保存/取消布局、缩放控制
 */
import { computed } from 'vue'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'
import type { VisualViewMode } from '../visual-state'
import type { PresetView } from '../types/three-visual'
import type { ElementType } from '../types/layout-element'
import { ELEMENT_TYPES } from '../types/layout-element'

const props = defineProps<{
  /** 库房列表 */
  warehouseList: WmsWarehouseVo[]
  /** 当前选中的库房ID */
  selectedWarehouseId?: EntityId
  /** 是否加载中 */
  loading: boolean
  /** 当前视图模式 */
  viewMode: VisualViewMode
  /** 快速定位关键词 */
  quickLocateKeyword: string
  /** 快速定位反馈信息 */
  quickLocateFeedback: string
  /** 是否编辑模式 */
  isEditMode: boolean
  /** 待保存布局数量 */
  pendingLayoutCount: number
  /** 是否正在保存布局 */
  isSavingLayout: boolean
  /** 布局反馈类型 */
  layoutFeedbackType: '' | 'success' | 'error'
  /** 布局反馈消息 */
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
  (e: 'set-preset-view', preset: PresetView): void
  (e: 'exit-detail'): void
}>()

/** 视图模式选项 */
const viewModeOptions = computed(() => [
  { label: '3D', value: '3d' as VisualViewMode },
  { label: '2D', value: '2d' as VisualViewMode },
])

/** 预设视角选项 */
const presetViewOptions: { label: string; value: PresetView }[] = [
  { label: '鸟瞰', value: 'bird-eye' },
  { label: '正面', value: 'front' },
  { label: '侧面', value: 'side' },
  { label: '等轴测', value: 'isometric' },
]

/** 是否为3D视图模式 */
const is3DMode = computed(() => props.viewMode === '3d')

/** 是否为柜子详情视图 */
const isDetailMode = computed(() => props.viewMode === 'detail')

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
  if (value === '3d' || value === '2d' || value === 'detail') {
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

function handleDrawingModeToggle(elementType: ElementType) {
  if (props.drawingMode === elementType) {
    emit('set-drawing-mode', null)
  } else {
    emit('set-drawing-mode', elementType)
  }
}

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
            :model-value="isDetailMode ? '3d' : props.viewMode"
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

        <!-- 预设视角按钮（仅3D模式下可用） -->
        <div v-if="is3DMode || isDetailMode" class="toolbar-group">
          <span class="toolbar-label">视角</span>
          <el-button-group>
            <el-button
              v-for="opt in presetViewOptions"
              :key="opt.value"
              size="small"
              @click="emit('set-preset-view', opt.value)"
            >
              {{ opt.label }}
            </el-button>
          </el-button-group>
        </div>

        <!-- 返回全景按钮（柜子详情视图时显示） -->
        <div v-if="isDetailMode" class="toolbar-group">
          <el-button type="warning" size="small" @click="emit('exit-detail')">
            返回全景
          </el-button>
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
        <el-button
          v-if="hasBackground"
          size="small"
          type="danger"
          plain
          @click="emit('background-deleted')"
        >
          删除底图
        </el-button>
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
