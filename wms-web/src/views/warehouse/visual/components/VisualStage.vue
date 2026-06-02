<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import type { EntityId } from '@/types/warehouse'
import type {
  WarehouseVisualAreaNode,
  WarehouseVisualCabinetNode,
  WarehouseVisualEmptyState,
  WarehouseVisualModel,
} from '../visual-layout'
import { VISUAL_AREA_HEADER_HEIGHT, VISUAL_STATUS_DISABLED } from '../visual-layout'
import type { LayoutEditorPositionDraft } from '../layout-editor'
import type { VisualSelectionState, VisualViewMode } from '../visual-state'
import type { LayoutElementVo, RenderContext, ElementType } from '../types/layout-element'
import { renderLayoutElement } from '../renderers'
import { calculatePolygonBounds, resolveLabelPosition, detectAreaOverlap, getPolygonAreaConfig } from '../renderers/renderPolygonArea'
import { useCanvasViewport } from '../composables/useCanvasViewport'
import { useBackgroundImage } from '../composables/useBackgroundImage'

/** 缩放低于此比例时隐藏库位细节 */
const SCALE_HIDE_BIN_DETAIL = 0.2

interface CabinetGridCell {
  key: string
  binId: EntityId | null
  rectConfig: Record<string, number | string>
  labelConfig: Record<string, number | string> | null
}

interface DragTargetLike {
  target: {
    x: () => number
    y: () => number
  }
}

const props = defineProps<{
  visualModel: WarehouseVisualModel | null
  loading: boolean
  visualError: string
  emptyState: WarehouseVisualEmptyState | null
  selection: VisualSelectionState
  viewMode: VisualViewMode
  isEditMode: boolean
  pendingCabinetIds: EntityId[]
  draftPositions: Record<string, LayoutEditorPositionDraft>
  /** 布局辅助元素列表 */
  layoutElements: LayoutElementVo[]
  /** 当前绘制模式 */
  drawingMode: ElementType | null
}>()

const emit = defineEmits<{
  (e: 'select-area', areaId: EntityId): void
  (e: 'select-cabinet', cabinetId: EntityId): void
  (e: 'select-bin', payload: { cabinetId: EntityId; binId: EntityId }): void
  (e: 'open-cabinet-detail', cabinetId: EntityId): void
  (e: 'update-cabinet-position', payload: { cabinetId: EntityId; positionX: number; positionY: number }): void
  (e: 'retry'): void
  (e: 'select-element', elementId: EntityId): void
  (e: 'wheel-zoom', event: WheelEvent): void
}>()

// ==================== 画布视口 ====================
const stageContainerRef = ref<HTMLElement | null>(null)
const containerWidth = ref(960)
const containerHeight = ref(600)

const viewport = useCanvasViewport(containerWidth.value, containerHeight.value)
const backgroundImg = useBackgroundImage()

// 监听容器尺寸变化
let resizeObserver: ResizeObserver | null = null
onMounted(() => {
  if (stageContainerRef.value) {
    resizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        containerWidth.value = entry.contentRect.width
        containerHeight.value = entry.contentRect.height
      }
    })
    resizeObserver.observe(stageContainerRef.value)
  }
})
onUnmounted(() => {
  resizeObserver?.disconnect()
})

// 监听底图版本变化，自动加载底图
watch(
  () => props.visualModel,
  (model) => {
    if (model?.layoutBackgroundVersion) {
      backgroundImg.load(model.warehouseId, model.layoutBackgroundVersion)
    } else {
      backgroundImg.clear()
    }
  },
  { immediate: true },
)

// ==================== 计算属性 ====================
const visualAreas = computed(() => props.visualModel?.areas ?? [])
const cabinetList = computed(() =>
  Object.values(props.visualModel?.cabinets ?? {}).map((cabinet) => {
    const draft = props.draftPositions[cabinet.id]
    return {
      ...cabinet,
      x: draft?.positionX ?? cabinet.x,
      y: draft?.positionY ?? cabinet.y,
    }
  }),
)

/** 是否隐藏库位细节（缩放低于20%时） */
const hideBinDetail = computed(() => viewport.scale.value < SCALE_HIDE_BIN_DETAIL)

/** 渲染上下文 */
function createRenderContext(isSelected: boolean, isHighlighted: boolean): RenderContext {
  return {
    viewMode: props.viewMode,
    isSelected,
    isHighlighted,
    isEditMode: props.isEditMode,
    scale: viewport.scale.value,
  }
}

/** 按层级分类布局元素 */
const auxiliaryElements = computed(() =>
  props.layoutElements.filter(e => e.elementType === 'wall' || e.elementType === 'aisle' || e.elementType === 'reserved'),
)
const annotationElements = computed(() =>
  props.layoutElements.filter(e => e.elementType === 'device' || e.elementType === 'text' || e.elementType === 'dimension'),
)

/** 区域重叠检测结果 */
const areaOverlaps = computed(() => {
  const areas = visualAreas.value
  const overlaps: Array<{ areaIdA: EntityId; areaIdB: EntityId }> = []
  for (let i = 0; i < areas.length; i++) {
    for (let j = i + 1; j < areas.length; j++) {
      if (detectAreaOverlap(areas[i], areas[j])) {
        overlaps.push({ areaIdA: areas[i].id, areaIdB: areas[j].id })
      }
    }
  }
  return overlaps
})

// ==================== Stage配置 ====================
const stageConfig = computed(() => ({
  width: containerWidth.value,
  height: containerHeight.value,
}))

const stageContentConfig = computed(() => ({
  scaleX: viewport.state.value.scaleX,
  scaleY: viewport.state.value.scaleY,
  x: viewport.state.value.offsetX,
  y: viewport.state.value.offsetY,
  draggable: !props.isEditMode || !props.drawingMode,
}))

// ==================== 区域渲染 ====================
function getAreaRectConfig(area: WarehouseVisualAreaNode) {
  const isSelected = area.id === props.selection.selectedAreaId
  const isHighlighted = area.id === props.selection.highlightedAreaId
  const isDisabled = area.status === VISUAL_STATUS_DISABLED

  return {
    x: area.x,
    y: area.y,
    width: area.width,
    height: area.height,
    fill: isDisabled ? '#f4f4f5' : isHighlighted ? '#d7ebff' : isSelected ? '#e8f3ff' : '#f7fbff',
    stroke: isHighlighted ? '#1d7df2' : isSelected ? '#409eff' : '#cdddf5',
    strokeWidth: isHighlighted ? 3 : isSelected ? 2 : 1,
    cornerRadius: 16,
  }
}

function getAreaTitleConfig(area: WarehouseVisualAreaNode) {
  // 多边形区域标题定位
  if (area.shapeType === 'polygon' && area.polygonPoints) {
    const bounds = calculatePolygonBounds(area.polygonPoints)
    const labelPos = resolveLabelPosition(bounds, area.labelX, area.labelY)
    return {
      x: labelPos.x,
      y: labelPos.y,
      text: `${area.areaName} (${area.areaCode})`,
      fontSize: 18,
      fontStyle: area.id === props.selection.selectedAreaId ? 'bold' : 'normal',
      fill: '#1f2d3d',
    }
  }

  return {
    x: area.x + 16,
    y: area.y + 14,
    text: `${area.areaName} (${area.areaCode})`,
    fontSize: 18,
    fontStyle: area.id === props.selection.selectedAreaId ? 'bold' : 'normal',
    fill: '#1f2d3d',
  }
}

function getAreaTopShadowConfig(area: WarehouseVisualAreaNode) {
  return {
    x: area.x + 12,
    y: area.y - 10,
    width: Math.max(area.width - 24, 40),
    height: 12,
    fill: '#ecf5ff',
    cornerRadius: 12,
    opacity: 0.9,
  }
}

function getAreaSideShadowConfig(area: WarehouseVisualAreaNode) {
  return {
    x: area.x + area.width - 10,
    y: area.y + 12,
    width: 12,
    height: Math.max(area.height - 24, 40),
    fill: '#d9ecff',
    cornerRadius: 12,
    opacity: 0.9,
  }
}

/** 多边形区域渲染配置 */
function getPolygonConfig(area: WarehouseVisualAreaNode) {
  if (area.shapeType !== 'polygon' || !area.polygonPoints) {
    return null
  }
  const isSelected = area.id === props.selection.selectedAreaId
  const isHighlighted = area.id === props.selection.highlightedAreaId
  const isDisabled = area.status === VISUAL_STATUS_DISABLED
  return getPolygonAreaConfig(area.polygonPoints, isSelected, isHighlighted, isDisabled)
}

// ==================== 存放柜渲染 ====================
function getCabinetGroupConfig(cabinet: WarehouseVisualCabinetNode) {
  return {
    x: cabinet.x,
    y: cabinet.y,
    draggable: props.isEditMode,
  }
}

function getCabinetRectConfig(cabinet: WarehouseVisualCabinetNode) {
  const isSelected = cabinet.id === props.selection.selectedCabinetId
  const isHighlighted = cabinet.id === props.selection.highlightedCabinetId
  const isDisabled = cabinet.status === VISUAL_STATUS_DISABLED
  const isPending = props.pendingCabinetIds.includes(cabinet.id)

  return {
    x: 0,
    y: 0,
    width: cabinet.width,
    height: cabinet.height,
    fill: isDisabled ? '#f5f7fa' : isHighlighted ? '#ffefcf' : isSelected ? '#fff7e6' : '#ffffff',
    stroke: isPending ? '#67c23a' : isHighlighted ? '#d48806' : isSelected ? '#e6a23c' : '#dcdfe6',
    strokeWidth: isPending ? 3 : isHighlighted ? 3 : isSelected ? 2 : 1,
    cornerRadius: 12,
    shadowBlur: isSelected || isHighlighted || isPending ? 8 : 0,
    shadowColor: isPending ? '#67c23a' : '#d9a441',
    dash: isPending ? [8, 4] : undefined,
  }
}

function getCabinetTitleConfig(cabinet: WarehouseVisualCabinetNode) {
  const isPending = props.pendingCabinetIds.includes(cabinet.id)

  return {
    x: 12,
    y: 10,
    text: isPending ? `${cabinet.cabinetName} *` : cabinet.cabinetName,
    fontSize: 14,
    fontStyle: cabinet.id === props.selection.selectedCabinetId ? 'bold' : 'normal',
    fill: '#303133',
  }
}

function getCabinetTopShadowConfig(cabinet: WarehouseVisualCabinetNode) {
  return {
    x: 10,
    y: -8,
    width: Math.max(cabinet.width - 20, 40),
    height: 10,
    fill: '#fff8e8',
    cornerRadius: 8,
    opacity: 0.95,
  }
}

function getCabinetSideShadowConfig(cabinet: WarehouseVisualCabinetNode) {
  return {
    x: cabinet.width - 8,
    y: 10,
    width: 10,
    height: Math.max(cabinet.height - 20, 40),
    fill: '#ffe7ba',
    cornerRadius: 8,
    opacity: 0.95,
  }
}

function getCabinetGridCells(cabinet: WarehouseVisualCabinetNode): CabinetGridCell[] {
  const cellWidth = (cabinet.width - 24) / cabinet.gridColCount
  const cellHeight = (cabinet.height - 48) / cabinet.gridRowCount

  return cabinet.cells.flatMap((row, rowIndex) =>
    row.map((cell, colIndex) => {
      const isSelected = cell?.id === props.selection.selectedBinId
      const isHighlighted = cell?.id === props.selection.highlightedBinId
      const isDisabled = cell?.status === VISUAL_STATUS_DISABLED
      const x = 12 + colIndex * cellWidth
      const y = 34 + rowIndex * cellHeight

      return {
        key: `${cabinet.id}-${rowIndex + 1}-${colIndex + 1}`,
        binId: cell?.id ?? null,
        rectConfig: {
          x,
          y,
          width: Math.max(cellWidth - 6, 24),
          height: Math.max(cellHeight - 6, 18),
          fill: cell
            ? isDisabled
              ? '#ebeef5'
              : isHighlighted
                ? '#ffd591'
                : isSelected
                  ? '#fde2c0'
                  : '#ecf5ff'
            : '#fafafa',
          stroke: isHighlighted ? '#d46b08' : isSelected ? '#e6a23c' : '#dcdfe6',
          strokeWidth: isHighlighted ? 3 : isSelected ? 2 : 1,
          cornerRadius: 6,
        },
        labelConfig: cell && !hideBinDetail.value
          ? {
              x: x + 4,
              y: y + 6,
              text: cell.binCode,
              fontSize: 10,
              fill: '#606266',
              width: Math.max(cellWidth - 14, 12),
            }
          : null,
      }
    }),
  )
}

// ==================== 事件处理 ====================
function handleCabinetClick(cabinetId: EntityId) {
  emit('select-cabinet', cabinetId)
}

function handleCabinetDblclick(cabinetId: EntityId) {
  emit('open-cabinet-detail', cabinetId)
}

function handleCabinetDragEnd(cabinet: WarehouseVisualCabinetNode, event: DragTargetLike) {
  if (!props.visualModel || !props.isEditMode) {
    return
  }

  const area = props.visualModel.areas.find(item => item.id === cabinet.areaId)
  if (!area) {
    return
  }

  const minX = area.x + props.visualModel.viewport.padding
  const maxX = area.x + area.width - cabinet.width - props.visualModel.viewport.padding
  const minY = area.y + VISUAL_AREA_HEADER_HEIGHT
  const maxY = area.y + area.height - cabinet.height - props.visualModel.viewport.padding
  const positionX = clamp(event.target.x(), minX, maxX)
  const positionY = clamp(event.target.y(), minY, maxY)

  emit('update-cabinet-position', {
    cabinetId: cabinet.id,
    positionX,
    positionY,
  })
}

function handleElementClick(elementId: EntityId) {
  emit('select-element', elementId)
}

/** 鼠标滚轮缩放 */
function handleStageWheel(event: WheelEvent) {
  viewport.handleWheelZoom(event)
}

/** 画布拖拽平移 */
const isPanning = ref(false)
const lastPointerPos = ref({ x: 0, y: 0 })

function handleStageMouseDown(event: MouseEvent) {
  // 编辑模式绘制时不触发平移
  if (props.isEditMode && props.drawingMode) {
    return
  }
  isPanning.value = true
  lastPointerPos.value = { x: event.clientX, y: event.clientY }
}

function handleStageMouseMove(event: MouseEvent) {
  if (!isPanning.value) {
    return
  }
  const dx = event.clientX - lastPointerPos.value.x
  const dy = event.clientY - lastPointerPos.value.y
  lastPointerPos.value = { x: event.clientX, y: event.clientY }

  const contentWidth = props.visualModel?.viewport.width ?? 960
  const contentHeight = props.visualModel?.viewport.height ?? 600
  viewport.pan(dx, dy, contentWidth, contentHeight)
}

function handleStageMouseUp() {
  isPanning.value = false
}

function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max)
}

// 暴露给父组件的方法
defineExpose({
  viewport,
  backgroundImg,
  fitToContent: () => {
    const contentWidth = props.visualModel?.viewport.width ?? 960
    const contentHeight = props.visualModel?.viewport.height ?? 600
    viewport.fitToContent(contentWidth, contentHeight)
  },
  resetZoom: viewport.resetZoom,
  zoomIn: viewport.zoomIn,
  zoomOut: viewport.zoomOut,
})
</script>

<template>
  <el-card class="visual-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <div class="header-title">库房可视化工作台</div>
          <div class="header-subtitle">支持 2D / 2.5D 等轴测投影、快速定位、缩放平移和高亮联动</div>
        </div>
        <div class="header-badges">
          <el-tag type="info">{{ viewMode === '2.5d' ? '2.5D' : '2D' }}</el-tag>
          <el-tag type="success">{{ viewport.scalePercent.value }}</el-tag>
        </div>
      </div>
    </template>

    <div v-if="visualError" class="visual-error-state">
      <el-result icon="error" title="布局加载失败" :sub-title="visualError">
        <template #extra>
          <el-button type="primary" @click="emit('retry')">重试加载</el-button>
        </template>
      </el-result>
    </div>

    <div v-else-if="emptyState" class="visual-empty-state">
      <el-empty :description="emptyState.description">
        <template #image>
          <div class="empty-title">{{ emptyState.title }}</div>
        </template>
      </el-empty>
    </div>

    <div v-else class="stage-shell">
      <div v-loading="loading" class="stage-scroll" ref="stageContainerRef">
        <!-- 区域重叠警告 -->
        <el-alert
          v-if="areaOverlaps.length > 0"
          type="warning"
          :closable="false"
          title="区域存在重叠"
          class="overlap-alert"
        />

        <!-- 底图加载状态 -->
        <div v-if="backgroundImg.loading.value" class="background-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>底图加载中...</span>
        </div>
        <div v-if="backgroundImg.error.value" class="background-error">
          <el-alert type="error" :closable="false" :title="backgroundImg.error.value">
            <template #default>
              <el-button size="small" @click="backgroundImg.retry()">重试</el-button>
            </template>
          </el-alert>
        </div>

        <v-stage :config="stageConfig" @wheel="handleStageWheel" @mousedown="handleStageMouseDown" @mousemove="handleStageMouseMove" @mouseup="handleStageMouseUp" @mouseleave="handleStageMouseUp">
          <v-layer :config="stageContentConfig">
            <!-- L0: 底图层 -->
            <v-layer>
              <v-image
                v-if="backgroundImg.backgroundImage.value && visualModel?.layoutBackgroundVersion"
                :config="{
                  image: backgroundImg.backgroundImage.value,
                  x: 0,
                  y: 0,
                  width: visualModel?.layoutWidth || visualModel?.viewport.width || 960,
                  height: visualModel?.layoutHeight || visualModel?.viewport.height || 600,
                  opacity: backgroundImg.opacity.value,
                }"
              />
            </v-layer>

            <!-- L1: 辅助结构层（墙体/通道/预留区） -->
            <v-layer>
              <template v-for="element in auxiliaryElements" :key="`aux-${element.id}`">
                <v-rect
                  v-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'rect'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-line
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'line'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-text
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'text'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-group
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'group'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
              </template>
            </v-layer>

            <!-- L2: 区域层 -->
            <v-layer>
              <template v-for="area in visualAreas" :key="`area-${area.id}`">
                <!-- 2.5D阴影 -->
                <v-rect
                  v-if="viewMode === '2.5d'"
                  :config="getAreaTopShadowConfig(area)"
                />
                <v-rect
                  v-if="viewMode === '2.5d'"
                  :config="getAreaSideShadowConfig(area)"
                />
                <!-- 多边形区域 -->
                <v-line
                  v-if="area.shapeType === 'polygon' && area.polygonPoints"
                  :config="getPolygonConfig(area)"
                  @click="emit('select-area', area.id)"
                />
                <!-- 矩形区域 -->
                <v-rect
                  v-else
                  :config="getAreaRectConfig(area)"
                  @click="emit('select-area', area.id)"
                />
                <!-- 区域标题 -->
                <v-text :config="getAreaTitleConfig(area)" @click="emit('select-area', area.id)" />
              </template>
            </v-layer>

            <!-- L3: 存放柜/库位层 -->
            <v-layer>
              <template v-for="cabinet in cabinetList" :key="`cabinet-${cabinet.id}`">
                <v-group
                  :config="getCabinetGroupConfig(cabinet)"
                  @dragend="handleCabinetDragEnd(cabinet, $event)"
                >
                  <v-rect
                    v-if="viewMode === '2.5d'"
                    :config="getCabinetTopShadowConfig(cabinet)"
                  />
                  <v-rect
                    v-if="viewMode === '2.5d'"
                    :config="getCabinetSideShadowConfig(cabinet)"
                  />
                  <v-rect
                    :config="getCabinetRectConfig(cabinet)"
                    @click="handleCabinetClick(cabinet.id)"
                    @dblclick="handleCabinetDblclick(cabinet.id)"
                  />
                  <v-text
                    :config="getCabinetTitleConfig(cabinet)"
                    @click="handleCabinetClick(cabinet.id)"
                    @dblclick="handleCabinetDblclick(cabinet.id)"
                  />

                  <template v-if="!hideBinDetail" v-for="cell in getCabinetGridCells(cabinet)" :key="cell.key">
                    <v-rect
                      :config="cell.rectConfig"
                      @click="cell.binId ? emit('select-bin', { cabinetId: cabinet.id, binId: cell.binId }) : handleCabinetClick(cabinet.id)"
                    />
                    <v-text
                      v-if="cell.labelConfig"
                      :config="cell.labelConfig"
                      @click="cell.binId ? emit('select-bin', { cabinetId: cabinet.id, binId: cell.binId }) : handleCabinetClick(cabinet.id)"
                    />
                  </template>
                </v-group>
              </template>
            </v-layer>

            <!-- L4: 标注层（设备/文字/尺寸标注） -->
            <v-layer>
              <template v-for="element in annotationElements" :key="`ann-${element.id}`">
                <v-rect
                  v-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'rect'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-line
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'line'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-text
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'text'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
                <v-group
                  v-else-if="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.type === 'group'"
                  :config="renderLayoutElement(element, createRenderContext(element.id === selection.selectedElementId, false))?.config"
                  @click="handleElementClick(element.id)"
                />
              </template>
            </v-layer>
          </v-layer>
        </v-stage>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts">
import { Loading } from '@element-plus/icons-vue'
export default {
  components: { Loading },
}
</script>

<style scoped lang="scss">
.visual-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title,
.empty-title {
  color: #303133;
  font-weight: 600;
}

.header-subtitle {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.header-badges {
  display: flex;
  gap: 8px;
}

.stage-shell {
  min-height: 420px;
}

.stage-scroll {
  overflow: hidden;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: linear-gradient(180deg, #fcfdff 0%, #f6faff 100%);
  min-height: 420px;
}

.visual-empty-state,
.visual-error-state {
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlap-alert {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
}

.background-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  z-index: 5;
}

.background-error {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
  max-width: 300px;
}
</style>
