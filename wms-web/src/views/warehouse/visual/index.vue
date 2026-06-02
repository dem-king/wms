<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetList, saveCabinetLayout } from '@/api/warehouse/cabinet'
import { getBinList } from '@/api/warehouse/bin'
import { getLayoutElementList, batchSaveLayoutElements, updateAreaLayoutCoordinates } from '@/api/warehouse/layout-element'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'
import {
  buildWarehouseVisualModel,
  findVisualLocationByCode,
  type WarehouseVisualBinNode,
  type WarehouseVisualEmptyState,
  type WarehouseVisualModel,
} from './visual-layout'
import {
  applySavedLayoutToVisualModel,
  buildLayoutSavePayload,
  createInitialLayoutEditorState,
  reduceLayoutEditorState,
  canUndo,
  canRedo,
  hasPendingElementChanges,

} from './layout-editor'
import {
  createInitialVisualSelection,
  reduceVisualSelection,
  type VisualSelectionState,
  type VisualViewMode,
} from './visual-state'
import type { LayoutElementVo, ElementType } from './types/layout-element'
import { ELEMENT_MAX_COUNT } from './types/layout-element'
import VisualToolbar from './components/VisualToolbar.vue'
import VisualStage from './components/VisualStage.vue'
import VisualSummary from './components/VisualSummary.vue'
import CabinetDetailDialog from './components/CabinetDetailDialog.vue'

const route = useRoute()

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
  viewMode: '2d',
  selectedElementId: null,
})
const quickLocateKeyword = ref('')
const quickLocateFeedback = ref('')
const isCabinetDetailVisible = ref(false)
const layoutEditor = ref(createInitialLayoutEditorState())

/** 布局辅助元素列表 */
const layoutElements = ref<LayoutElementVo[]>([])
/** 布局元素加载错误 */
const layoutElementError = ref('')

/** VisualStage组件引用 */
const visualStageRef = ref<InstanceType<typeof VisualStage> | null>(null)

/** 底图透明度 */
const backgroundOpacity = ref(0.7)

const selectedArea = computed(() =>
  visualModel.value?.areas.find(area => area.id === visualSelection.value.selectedAreaId) ?? null,
)
const selectedCabinet = computed(() =>
  (visualSelection.value.selectedCabinetId && visualModel.value?.cabinets[visualSelection.value.selectedCabinetId]) || null,
)
const selectedBin = computed(() =>
  (visualSelection.value.selectedBinId && visualModel.value?.bins[visualSelection.value.selectedBinId]) || null,
)
const currentWarehouseName = computed(() => {
  const currentWarehouse = warehouseList.value.find(warehouse => warehouse.id === selectedWarehouseId.value)
  return currentWarehouse?.warehouseName ?? '未选择'
})
const currentEmptyState = computed<WarehouseVisualEmptyState | null>(() => {
  if (visualError.value) {
    return null
  }
  if (!selectedWarehouseId.value) {
    return {
      title: '请先选择库房',
      description: '选择库房后即可查看区域、存放柜与库位的 2D 布局。',
    }
  }
  return visualModel.value?.empty ?? null
})
const viewMode = computed(() => visualSelection.value.viewMode)
const isEditMode = computed(() => layoutEditor.value.isEditMode)
const pendingLayoutCount = computed(() => layoutEditor.value.pendingCabinetIds.length)
const layoutFeedbackType = computed(() => layoutEditor.value.feedbackType)
const layoutFeedbackMessage = computed(() => layoutEditor.value.feedbackMessage)
const selectedCabinetBins = computed<WarehouseVisualBinNode[]>(() => {
  if (!selectedCabinet.value) {
    return []
  }
  return selectedCabinet.value.binIds
    .map(binId => visualModel.value?.bins[binId] ?? null)
    .filter((bin): bin is WarehouseVisualBinNode => Boolean(bin))
})

/** 选中的布局元素 */
const selectedElement = computed(() => {
  if (!visualSelection.value.selectedElementId) {
    return null
  }
  return layoutElements.value.find(e => e.id === visualSelection.value.selectedElementId) ?? null
})

/** 缩放百分比 */
const scalePercent = computed(() => {
  return visualStageRef.value?.viewport.scalePercent.value ?? '100%'
})

/** 是否有底图 */
const hasBackground = computed(() => {
  return Boolean(visualModel.value?.layoutBackgroundVersion)
})

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
    layoutEditor.value = createInitialLayoutEditorState()
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
    // 并行请求区域、存放柜、库位和布局元素
    const [areaResponse, elementResponse] = await Promise.allSettled([
      getAreaList(selectedWarehouseId.value),
      getLayoutElementList(selectedWarehouseId.value),
    ])

    // 区域数据必须成功
    if (areaResponse.status === 'rejected') {
      throw areaResponse.reason
    }
    const areas = areaResponse.value.data

    // 布局元素加载失败时仅提示，不影响主画布
    let elements: LayoutElementVo[] = []
    if (elementResponse.status === 'fulfilled') {
      elements = elementResponse.value.data
      // 元素数量超过上限时分批加载（此处已一次性加载，后续可优化）
      if (elements.length > ELEMENT_MAX_COUNT) {
        console.warn(`[loadVisual] 布局元素数量(${elements.length})超过上限(${ELEMENT_MAX_COUNT})，仅显示前${ELEMENT_MAX_COUNT}个`)
        elements = elements.slice(0, ELEMENT_MAX_COUNT)
      }
    } else {
      layoutElementError.value = '布局辅助元素加载失败'
    }
    layoutElements.value = elements

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
    layoutEditor.value = createInitialLayoutEditorState()
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
    }
    visualError.value = resolveErrorMessage(error)
    layoutEditor.value = createInitialLayoutEditorState()
  } finally {
    loading.value = false
  }
}

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

function handleSelectElement(elementId: EntityId) {
  if (!visualModel.value) {
    return
  }
  visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
    type: 'select-element',
    elementId,
  })
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

function handleOpenCabinetDetail(cabinetId?: EntityId) {
  if (!visualModel.value) {
    return
  }

  if (typeof cabinetId === 'string' && cabinetId) {
    visualSelection.value = reduceVisualSelection(visualModel.value, visualSelection.value, {
      type: 'select-cabinet',
      cabinetId,
    })
  }

  if (!selectedCabinet.value && typeof cabinetId !== 'string') {
    return
  }
  isCabinetDetailVisible.value = true
}

function handleToggleEditMode(enabled: boolean) {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'set-edit-mode',
    enabled,
  })
}

function handleCabinetPositionChange(payload: { cabinetId: EntityId; positionX: number; positionY: number }) {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'move-cabinet',
    cabinetId: payload.cabinetId,
    positionX: payload.positionX,
    positionY: payload.positionY,
  })
}

function handleDiscardLayout() {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'discard-pending',
  })
}

async function handleSaveLayout() {
  if (!visualModel.value || !selectedArea.value) {
    ElMessage.warning('请先选择区域后再保存布局')
    return
  }
  if (pendingLayoutCount.value === 0 && !hasPendingElementChanges(layoutEditor.value)) {
    ElMessage.warning('当前没有待保存的布局变更')
    return
  }

  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'save-start',
  })

  try {
    // 保存存放柜布局
    if (pendingLayoutCount.value > 0) {
      const payload = buildLayoutSavePayload(visualModel.value, layoutEditor.value, selectedArea.value.id)
      const response = await saveCabinetLayout(payload)
      visualModel.value = applySavedLayoutToVisualModel(visualModel.value, response.data)
    }

    // 保存布局元素变更
    if (hasPendingElementChanges(layoutEditor.value)) {
      await handleSaveElements()
    }

    // 保存区域坐标变更
    await handleSaveAreaCoordinates()

    layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
      type: 'save-success',
      message: '布局保存成功',
    })
    ElMessage.success('布局保存成功')
  } catch (error) {
    layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
      type: 'save-failure',
      message: resolveErrorMessage(error),
    })
  }
}

/**
 * 批量保存布局元素
 */
async function handleSaveElements() {
  if (!selectedWarehouseId.value) {
    return
  }
  const { pendingElements } = layoutEditor.value
  if (pendingElements.created.length === 0 && pendingElements.updated.length === 0 && pendingElements.deletedIds.length === 0) {
    return
  }

  const result = await batchSaveLayoutElements({
    warehouseId: selectedWarehouseId.value,
    ...pendingElements,
  })

  // 处理失败项
  if (result.data.failedItems.length > 0) {
    const failedNames = result.data.failedItems.map(item => `${item.elementName}: ${item.reason}`).join('\n')
    ElMessage.warning(`部分元素保存失败:\n${failedNames}`)
  }

  // 清除已保存的变更
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'clear-pending-elements',
  })

  // 刷新布局元素列表
  await loadLayoutElements()
}

/**
 * 保存区域坐标变更
 */
async function handleSaveAreaCoordinates() {
  if (!visualModel.value) {
    return
  }
  // 收集有coordX/coordY的区域
  const items = visualModel.value.areas
    .filter(area => area.x !== undefined && area.y !== undefined)
    .map(area => ({
      id: area.id,
      coordX: area.x,
      coordY: area.y,
    }))

  if (items.length === 0) {
    return
  }

  await updateAreaLayoutCoordinates({ items })
}

/**
 * 加载布局元素列表
 */
async function loadLayoutElements() {
  if (!selectedWarehouseId.value) {
    return
  }
  try {
    const response = await getLayoutElementList(selectedWarehouseId.value)
    layoutElements.value = response.data
  } catch {
    layoutElementError.value = '布局辅助元素加载失败'
  }
}

/**
 * 设置绘制模式
 */
function handleSetDrawingMode(elementType: ElementType | null) {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'set-drawing-mode',
    elementType,
  })
}

/**
 * 撤销
 */
function handleUndo() {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'undo',
  })
}

/**
 * 重做
 */
function handleRedo() {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'redo',
  })
}

/**
 * 缩放操作
 */
function handleZoomIn() {
  visualStageRef.value?.zoomIn()
}
function handleZoomOut() {
  visualStageRef.value?.zoomOut()
}
function handleZoomReset() {
  visualStageRef.value?.resetZoom()
}
function handleZoomFit() {
  visualStageRef.value?.fitToContent()
}

/**
 * 底图上传/删除回调
 */
function handleBackgroundUploaded() {
  // 重新加载以获取新的layoutBackgroundVersion
  loadVisual()
}
function handleBackgroundDeleted() {
  if (visualModel.value) {
    visualModel.value = { ...visualModel.value, layoutBackgroundVersion: null }
  }
}

/**
 * 更新底图透明度
 */
function handleBackgroundOpacityChange(value: number) {
  backgroundOpacity.value = value
  visualStageRef.value?.backgroundImg.setOpacity(value)
}

/**
 * 更新元素属性
 */
function handleUpdateElementProperty(payload: { field: string; value: unknown }) {
  // TODO: 实现元素属性更新逻辑
  console.log('[handleUpdateElementProperty]', payload)
}

/**
 * 更新元素样式
 */
function handleUpdateElementStyle(payload: Record<string, unknown>) {
  // TODO: 实现元素样式更新逻辑
  console.log('[handleUpdateElementStyle]', payload)
}

/**
 * 删除元素
 */
function handleDeleteElement(elementId: string) {
  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'add-deleted-element-id',
    id: elementId,
  })
  // 取消选中
  visualSelection.value = { ...visualSelection.value, selectedElementId: null }
}

// ==================== 键盘快捷键 ====================
function handleKeyDown(event: KeyboardEvent) {
  // Ctrl+Z 撤销
  if (event.ctrlKey && event.key === 'z' && canUndo(layoutEditor.value)) {
    event.preventDefault()
    handleUndo()
    return
  }
  // Ctrl+Y 重做
  if (event.ctrlKey && event.key === 'y' && canRedo(layoutEditor.value)) {
    event.preventDefault()
    handleRedo()
    return
  }
  // Esc 退出绘制模式
  if (event.key === 'Escape' && layoutEditor.value.drawingMode) {
    handleSetDrawingMode(null)
    return
  }
  // Delete 删除选中元素
  if (event.key === 'Delete' && visualSelection.value.selectedElementId && isEditMode.value) {
    handleDeleteElement(visualSelection.value.selectedElementId)
  }
}

function resolveErrorMessage(error: unknown): string {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return '请检查区域、存放柜、库位接口或稍后重试。'
}

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

    <VisualToolbar
      :warehouse-list="warehouseList"
      :selected-warehouse-id="selectedWarehouseId"
      :loading="loading"
      :view-mode="viewMode"
      :quick-locate-keyword="quickLocateKeyword"
      :quick-locate-feedback="quickLocateFeedback"
      :is-edit-mode="isEditMode"
      :pending-layout-count="pendingLayoutCount"
      :is-saving-layout="layoutEditor.isSaving"
      :layout-feedback-type="layoutFeedbackType"
      :layout-feedback-message="layoutFeedbackMessage"
      :scale-percent="scalePercent"
      :has-background="hasBackground"
      :background-opacity="backgroundOpacity"
      :drawing-mode="layoutEditor.drawingMode"
      :can-undo="canUndo(layoutEditor)"
      :can-redo="canRedo(layoutEditor)"
      :has-pending-element-changes="hasPendingElementChanges(layoutEditor)"
      @change-warehouse="selectedWarehouseId = $event; handleWarehouseChange()"
      @change-view-mode="handleChangeViewMode"
      @update:quick-locate-keyword="quickLocateKeyword = $event"
      @quick-locate="handleQuickLocate"
      @toggle-edit-mode="handleToggleEditMode"
      @save-layout="handleSaveLayout"
      @discard-layout="handleDiscardLayout"
      @zoom-in="handleZoomIn"
      @zoom-out="handleZoomOut"
      @zoom-reset="handleZoomReset"
      @zoom-fit="handleZoomFit"
      @background-uploaded="handleBackgroundUploaded"
      @background-deleted="handleBackgroundDeleted"
      @update:background-opacity="handleBackgroundOpacityChange"
      @set-drawing-mode="handleSetDrawingMode"
      @undo="handleUndo"
      @redo="handleRedo"
      @save-elements="handleSaveElements"
    />

    <el-row :gutter="16">
      <el-col :span="17">
        <VisualStage
          ref="visualStageRef"
          :visual-model="visualModel"
          :loading="loading"
          :visual-error="visualError"
          :empty-state="currentEmptyState"
          :selection="visualSelection"
          :view-mode="viewMode"
          :is-edit-mode="isEditMode"
          :pending-cabinet-ids="layoutEditor.pendingCabinetIds"
          :draft-positions="layoutEditor.pendingPositions"
          :layout-elements="layoutElements"
          :drawing-mode="layoutEditor.drawingMode"
          @select-area="handleSelectArea"
          @select-cabinet="handleSelectCabinet"
          @select-bin="handleSelectBin($event.cabinetId, $event.binId)"
          @open-cabinet-detail="handleOpenCabinetDetail"
          @update-cabinet-position="handleCabinetPositionChange"
          @select-element="handleSelectElement"
          @retry="retryLoad"
        />
      </el-col>

      <el-col :span="7">
        <VisualSummary
          :warehouse-name="currentWarehouseName"
          :selected-area="selectedArea"
          :selected-cabinet="selectedCabinet"
          :selected-bin="selectedBin"
          :visual-model="visualModel"
          :pending-layout-count="pendingLayoutCount"
          :is-edit-mode="isEditMode"
          :selected-element="selectedElement"
          @open-cabinet-detail="handleOpenCabinetDetail()"
          @update-element-property="handleUpdateElementProperty"
          @update-element-style="handleUpdateElementStyle"
          @delete-element="handleDeleteElement"
        />
      </el-col>
    </el-row>

    <CabinetDetailDialog
      v-model="isCabinetDetailVisible"
      :selected-area="selectedArea"
      :selected-cabinet="selectedCabinet"
      :selected-bins="selectedCabinetBins"
    />
  </div>
</template>

<style scoped lang="scss">
.warehouse-visual-page {
  padding: 20px;
}
</style>
