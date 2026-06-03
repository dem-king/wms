<script setup lang="ts">
/**
 * 库房可视化主页面（重写版）
 * 使用Three.js 3D渲染替代Konva.js 2D渲染
 * 支持3D视图/2D俯视图/柜子详情视图三种模式切换
 */
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetList } from '@/api/warehouse/cabinet'
import { getBinList } from '@/api/warehouse/bin'
import { getLayoutElementList } from '@/api/warehouse/layout-element'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'
import {
  buildWarehouseVisualModel,
  findVisualLocationByCode,
  type WarehouseVisualBinNode,
  type WarehouseVisualEmptyState,
  type WarehouseVisualModel,
} from './visual-layout'
import {
  createInitialVisualSelection,
  reduceVisualSelection,
  type VisualSelectionState,
  type VisualViewMode,
} from './visual-state'
import type { LayoutElementVo, ElementType } from './types/layout-element'
import { ELEMENT_MAX_COUNT } from './types/layout-element'
import type { PresetView } from './types/three-visual'
import Warehouse3DViewer from './components/Warehouse3DViewer.vue'
import Warehouse2DMap from './components/Warehouse2DMap.vue'
import VisualToolbar from './components/VisualToolbar.vue'
import CabinetDetailPanel from './components/CabinetDetailPanel.vue'
import BinGridPanel from './components/BinGridPanel.vue'

const route = useRoute()

// ==================== 状态定义 ====================

const warehouseList = ref<WmsWarehouseVo[]>([])
const selectedWarehouseId = ref<EntityId>()
const loading = ref(false)
const visualModel = ref<WarehouseVisualModel | null>(null)
const visualError = ref('')
const visualSelection = ref<VisualSelectionState>({
  selectedAreaId: null,
  selectedCabinetId: null,
  selectedBinId: null,
  highlightedAreaId: null,
  highlightedCabinetId: null,
  highlightedBinId: null,
  viewMode: '3d',
  selectedElementId: null,
  detailCabinetId: null,
})
const quickLocateKeyword = ref('')
const quickLocateFeedback = ref('')
const layoutElements = ref<LayoutElementVo[]>([])
const layoutElementError = ref('')
const backgroundOpacity = ref(0.7)

/** 3D视图组件引用 */
const viewer3dRef = ref<InstanceType<typeof Warehouse3DViewer> | null>(null)

// ==================== 计算属性 ====================

const selectedArea = computed(() =>
  visualModel.value?.areas.find(area => area.id === visualSelection.value.selectedAreaId) ?? null,
)
const selectedCabinet = computed(() =>
  (visualSelection.value.selectedCabinetId && visualModel.value?.cabinets[visualSelection.value.selectedCabinetId]) || null,
)

const currentEmptyState = computed<WarehouseVisualEmptyState | null>(() => {
  if (visualError.value) {
    return null
  }
  if (!selectedWarehouseId.value) {
    return {
      title: '请先选择库房',
      description: '选择库房后即可查看区域、存放柜与库位的 3D 布局。',
    }
  }
  return visualModel.value?.empty ?? null
})
const viewMode = computed(() => visualSelection.value.viewMode)
const isEditMode = ref(false)
const selectedCabinetBins = computed<WarehouseVisualBinNode[]>(() => {
  if (!selectedCabinet.value) {
    return []
  }
  return selectedCabinet.value.binIds
    .map(binId => visualModel.value?.bins[binId] ?? null)
    .filter((bin): bin is WarehouseVisualBinNode => Boolean(bin))
})


/** 是否有底图 */
const hasBackground = computed(() => {
  return Boolean(visualModel.value?.layoutBackgroundVersion)
})

/** 缩放百分比（3D模式下由OrbitControls管理，此处提供占位） */
const scalePercent = computed(() => '100%')

// ==================== 数据加载 ====================

async function loadWarehouseOptions() {
  const response = await getWarehouseList()
  warehouseList.value = response.data

  const routeWarehouseId = typeof route.query.warehouseId === 'string' ? route.query.warehouseId : undefined
  const initialWarehouseId = routeWarehouseId || warehouseList.value[0]?.id

  if (initialWarehouseId) {
    selectedWarehouseId.value = initialWarehouseId
    await loadVisual()
  }
}

async function handleWarehouseChange() {
  quickLocateFeedback.value = ''
  await loadVisual()
}

async function retryLoad() {
  await loadVisual()
}

async function loadVisual() {
  if (!selectedWarehouseId.value) {
    visualModel.value = null
    visualError.value = ''
    visualSelection.value = reduceVisualSelection({} as WarehouseVisualModel, visualSelection.value, { type: 'reset' })
    quickLocateFeedback.value = ''
    layoutElements.value = []
    layoutElementError.value = ''
    return
  }

  const warehouse = warehouseList.value.find(item => item.id === selectedWarehouseId.value)
  if (!warehouse) {
    return
  }

  loading.value = true
  visualError.value = ''
  layoutElementError.value = ''

  try {
    // 并行请求区域和布局元素
    const [areaResponse, elementResponse] = await Promise.allSettled([
      getAreaList(selectedWarehouseId.value),
      getLayoutElementList(selectedWarehouseId.value),
    ])

    if (areaResponse.status === 'rejected') {
      throw areaResponse.reason
    }
    const areas = areaResponse.value.data

    // 布局元素加载失败时仅提示，不影响主画布
    let elements: LayoutElementVo[] = []
    if (elementResponse.status === 'fulfilled') {
      elements = elementResponse.value.data
      if (elements.length > ELEMENT_MAX_COUNT) {
        console.warn(`[loadVisual] 布局元素数量(${elements.length})超过上限(${ELEMENT_MAX_COUNT})，仅显示前${ELEMENT_MAX_COUNT}个`)
        elements = elements.slice(0, ELEMENT_MAX_COUNT)
      }
    } else {
      layoutElementError.value = '布局辅助元素加载失败'
    }
    layoutElements.value = elements

    // 请求存放柜和库位
    const cabinetResponses = await Promise.all(areas.map(area => getCabinetList(area.id)))
    const cabinets = cabinetResponses.flatMap(response => response.data)
    const binResponses = cabinets.length > 0
      ? await Promise.all(cabinets.map(cabinet => getBinList(cabinet.id)))
      : []
    const bins = binResponses.flatMap(response => response.data)

    const nextModel = buildWarehouseVisualModel({
      warehouse,
      areas,
      cabinets,
      bins,
      layoutElements: elements,
    })

    visualModel.value = nextModel
    visualSelection.value = createInitialVisualSelection(nextModel)
    quickLocateFeedback.value = ''
  } catch (error) {
    visualModel.value = null
    visualSelection.value = {
      selectedAreaId: null,
      selectedCabinetId: null,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: visualSelection.value.viewMode,
      selectedElementId: null,
      detailCabinetId: null,
    }
    visualError.value = resolveErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ==================== 事件处理 ====================

function handleSelectArea(areaId: EntityId) {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'select-area',
    areaId,
  })
}

function handleSelectCabinet(cabinetId: EntityId) {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'select-cabinet',
    cabinetId,
  })
}

function handleSelectBin(cabinetId: EntityId, binId: EntityId) {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'select-bin',
    cabinetId,
    binId,
  })
}

function handleEnterCabinetDetail(cabinetId: EntityId) {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'enter-detail',
    cabinetId,
  })
}

function handleExitCabinetDetail() {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'exit-detail',
  })
  // 退回全景
  viewer3dRef.value?.returnToOverview()
}

function handleChangeViewMode(nextViewMode: VisualViewMode) {
  if (!visualModel.value) {
    visualSelection.value = reduceVisualSelection({} as WarehouseVisualModel, visualSelection.value, {
      type: 'set-view-mode',
      viewMode: nextViewMode,
    })
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'set-view-mode',
    viewMode: nextViewMode,
  })
}

function handleQuickLocate() {
  if (!visualModel.value) {
    quickLocateFeedback.value = '请先选择并加载库房布局。'
    return
  }

  const match = findVisualLocationByCode(visualModel.value, quickLocateKeyword.value)
  if (!match) {
    quickLocateFeedback.value = '未找到匹配编码，请输入区域、存放柜或库位编码。'
    return
  }

  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'locate-match',
    match,
  })
  quickLocateFeedback.value = `已定位到${match.matchedCode}`
}

function handleToggleEditMode(enabled: boolean) {
  isEditMode.value = enabled
}

function handleCabinetPositionChange(payload: { cabinetId: EntityId; positionX: number; positionY: number }) {
  // 2D编辑模式下更新存放柜位置
  if (!visualModel.value) {
    return
  }
  const cabinet = visualModel.value.cabinets[payload.cabinetId]
  if (cabinet) {
    cabinet.x = payload.positionX
    cabinet.y = payload.positionY
  }
}

function handleSetPresetView(preset: PresetView) {
  viewer3dRef.value?.setPresetView(preset)
}

function handleBackgroundOpacityChange(value: number) {
  backgroundOpacity.value = value
}

function handleBackgroundUploaded() {
  loadVisual()
}

function handleBackgroundDeleted() {
  if (visualModel.value) {
    visualModel.value = { ...visualModel.value, layoutBackgroundVersion: null }
  }
}

function handleSaveLayout() {
  ElMessage.info('布局保存功能待实现')
}

function handleDiscardLayout() {
  ElMessage.info('放弃变更功能待实现')
}

function handleSaveElements() {
  ElMessage.info('元素保存功能待实现')
}

function handleSetDrawingMode(_elementType: ElementType | null) {
  // 绘制模式功能
}

function handleUndo() {
  // 撤销功能
}

function handleRedo() {
  // 重做功能
}

// ==================== 键盘快捷键 ====================

function handleKeyDown(event: KeyboardEvent) {
  // Esc 退出详情视图或编辑模式
  if (event.key === 'Escape') {
    if (visualSelection.value.viewMode === 'detail') {
      handleExitCabinetDetail()
      return
    }
    if (isEditMode.value) {
      isEditMode.value = false
      return
    }
  }
}

function resolveErrorMessage(error: unknown): string {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return '请检查区域、存放柜、库位接口或稍后重试。'
}

// ==================== 生命周期 ====================

onMounted(async () => {
  document.addEventListener('keydown', handleKeyDown)
  await loadWarehouseOptions()
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
})
</script>

<template>
  <div class="app-container warehouse-visual-page">
    <!-- 布局元素加载失败提示 -->
    <el-alert
      v-if="layoutElementError"
      type="warning"
      :closable="true"
      :title="layoutElementError"
      show-icon
      style="margin-bottom: 12px;"
      @close="layoutElementError = ''"
    />

    <!-- 工具栏 -->
    <VisualToolbar
      :warehouse-list="warehouseList"
      :selected-warehouse-id="selectedWarehouseId"
      :loading="loading"
      :view-mode="viewMode"
      :quick-locate-keyword="quickLocateKeyword"
      :quick-locate-feedback="quickLocateFeedback"
      :is-edit-mode="isEditMode"
      :pending-layout-count="0"
      :is-saving-layout="false"
      layout-feedback-type=""
      layout-feedback-message=""
      :scale-percent="scalePercent"
      :has-background="hasBackground"
      :background-opacity="backgroundOpacity"
      :drawing-mode="null"
      :can-undo="false"
      :can-redo="false"
      :has-pending-element-changes="false"
      @change-warehouse="selectedWarehouseId = $event; handleWarehouseChange()"
      @change-view-mode="handleChangeViewMode"
      @update:quick-locate-keyword="quickLocateKeyword = $event"
      @quick-locate="handleQuickLocate"
      @toggle-edit-mode="handleToggleEditMode"
      @save-layout="handleSaveLayout"
      @discard-layout="handleDiscardLayout"
      @zoom-in="() => {}"
      @zoom-out="() => {}"
      @zoom-reset="() => {}"
      @zoom-fit="() => {}"
      @background-uploaded="handleBackgroundUploaded"
      @background-deleted="handleBackgroundDeleted"
      @update:background-opacity="handleBackgroundOpacityChange"
      @set-drawing-mode="handleSetDrawingMode"
      @undo="handleUndo"
      @redo="handleRedo"
      @save-elements="handleSaveElements"
      @set-preset-view="handleSetPresetView"
      @exit-detail="handleExitCabinetDetail"
    />

    <!-- 主内容区 -->
    <div class="visual-content">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <span>正在加载库房布局数据...</span>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="visualError" class="error-state">
        <el-empty :description="visualError">
          <el-button type="primary" @click="retryLoad">重新加载</el-button>
        </el-empty>
      </div>

      <!-- 空状态 -->
      <div v-else-if="currentEmptyState" class="empty-state">
        <el-empty :description="currentEmptyState.description">
          <template #title>
            <span>{{ currentEmptyState.title }}</span>
          </template>
        </el-empty>
      </div>

      <!-- 正常渲染 -->
      <template v-else>
        <div class="viewer-area">
          <!-- 3D视图 -->
          <Warehouse3DViewer
            v-if="viewMode === '3d' || viewMode === 'detail'"
            ref="viewer3dRef"
            :visual-model="visualModel"
            :selection="visualSelection"
            :is-edit-mode="isEditMode"
            :layout-elements="layoutElements"
            @select-area="handleSelectArea"
            @select-cabinet="handleSelectCabinet"
            @select-bin="handleSelectBin($event.cabinetId, $event.binId)"
            @enter-cabinet-detail="handleEnterCabinetDetail"
            @exit-cabinet-detail="handleExitCabinetDetail"
          />

          <!-- 2D俯视图 -->
          <Warehouse2DMap
            v-if="viewMode === '2d'"
            :visual-model="visualModel"
            :selection="visualSelection"
            :is-edit-mode="isEditMode"
            :layout-elements="layoutElements"
            @select-area="handleSelectArea"
            @select-cabinet="handleSelectCabinet"
            @update-cabinet-position="handleCabinetPositionChange"
          />
        </div>

        <!-- 右侧面板 -->
        <div class="detail-area">
          <!-- 柜子详情面板 -->
          <CabinetDetailPanel
            :cabinet="selectedCabinet"
            :bins="selectedCabinetBins"
            :area="selectedArea"
            :visual-model="visualModel"
            :visible="!!selectedCabinet"
            @close="visualSelection.selectedCabinetId = null"
          />

          <!-- 库位网格面板（详情视图时显示） -->
          <BinGridPanel
            v-if="viewMode === 'detail'"
            :cabinet="selectedCabinet"
            :bins="selectedCabinetBins"
            :visible="viewMode === 'detail'"
            @select-bin="handleSelectBin(selectedCabinet!.id, $event)"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { Loading } from '@element-plus/icons-vue'
export default {
  components: { Loading },
}
</script>

<style scoped lang="scss">
.warehouse-visual-page {
  padding: 20px;
}

.visual-content {
  display: flex;
  gap: 16px;
  min-height: 600px;
}

.viewer-area {
  flex: 1;
  min-width: 0;
}

.detail-area {
  width: 320px;
  flex-shrink: 0;
}

.loading-state,
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 600px;
  gap: 12px;
  color: #909399;
  font-size: 14px;
}
</style>
