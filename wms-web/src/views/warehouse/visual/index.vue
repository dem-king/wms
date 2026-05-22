<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetList, saveCabinetLayout } from '@/api/warehouse/cabinet'
import { getBinList } from '@/api/warehouse/bin'
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
} from './layout-editor'
import {
  createInitialVisualSelection,
  reduceVisualSelection,
  type VisualSelectionState,
  type VisualViewMode,
} from './visual-state'
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
})
const quickLocateKeyword = ref('')
const quickLocateFeedback = ref('')
const isCabinetDetailVisible = ref(false)
const layoutEditor = ref(createInitialLayoutEditorState())

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
    return
  }

  const warehouse = warehouseList.value.find(item => item.id === selectedWarehouseId.value)
  if (!warehouse) {
    return
  }

  loading.value = true
  visualError.value = ''

  try {
    const areaResponse = await getAreaList(selectedWarehouseId.value)
    const areas = areaResponse.data
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
  if (pendingLayoutCount.value === 0) {
    ElMessage.warning('当前没有待保存的布局变更')
    return
  }

  layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
    type: 'save-start',
  })

  try {
    const payload = buildLayoutSavePayload(visualModel.value, layoutEditor.value, selectedArea.value.id)
    const response = await saveCabinetLayout(payload)
    visualModel.value = applySavedLayoutToVisualModel(visualModel.value, response.data)
    layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
      type: 'save-success',
      message: `已保存 ${response.data.cabinets.length} 个存放柜布局`,
    })
    ElMessage.success('布局保存成功')
  } catch (error) {
    layoutEditor.value = reduceLayoutEditorState(layoutEditor.value, {
      type: 'save-failure',
      message: resolveErrorMessage(error),
    })
  }
}

function resolveErrorMessage(error: unknown): string {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return '请检查区域、存放柜、库位接口或稍后重试。'
}

onMounted(async () => {
  await loadWarehouseOptions()
})
</script>

<template>
  <div class="app-container warehouse-visual-page">
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
      @change-warehouse="selectedWarehouseId = $event; handleWarehouseChange()"
      @change-view-mode="handleChangeViewMode"
      @update:quick-locate-keyword="quickLocateKeyword = $event"
      @quick-locate="handleQuickLocate"
      @toggle-edit-mode="handleToggleEditMode"
      @save-layout="handleSaveLayout"
      @discard-layout="handleDiscardLayout"
      @refresh="retryLoad"
    />

    <el-row :gutter="16">
      <el-col :span="17">
        <VisualStage
          :visual-model="visualModel"
          :loading="loading"
          :visual-error="visualError"
          :empty-state="currentEmptyState"
          :selection="visualSelection"
          :view-mode="viewMode"
          :is-edit-mode="isEditMode"
          :pending-cabinet-ids="layoutEditor.pendingCabinetIds"
          :draft-positions="layoutEditor.pendingPositions"
          @select-area="handleSelectArea"
          @select-cabinet="handleSelectCabinet"
          @select-bin="handleSelectBin($event.cabinetId, $event.binId)"
          @open-cabinet-detail="handleOpenCabinetDetail"
          @update-cabinet-position="handleCabinetPositionChange"
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
          @open-cabinet-detail="handleOpenCabinetDetail()"
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
