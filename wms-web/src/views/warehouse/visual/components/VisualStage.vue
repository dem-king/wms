<script setup lang="ts">
import { computed } from 'vue'
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
}>()

const emit = defineEmits<{
  (e: 'select-area', areaId: EntityId): void
  (e: 'select-cabinet', cabinetId: EntityId): void
  (e: 'select-bin', payload: { cabinetId: EntityId; binId: EntityId }): void
  (e: 'open-cabinet-detail', cabinetId: EntityId): void
  (e: 'update-cabinet-position', payload: { cabinetId: EntityId; positionX: number; positionY: number }): void
  (e: 'retry'): void
}>()

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
const stageConfig = computed(() => ({
  width: props.visualModel?.viewport.width ?? 960,
  height: props.visualModel?.viewport.height ?? 420,
}))

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
        labelConfig: cell
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

function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max)
}
</script>

<template>
  <el-card class="visual-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <div class="header-title">库房可视化工作台</div>
          <div class="header-subtitle">支持 2D / 2.5D 轻量投影、快速定位和高亮联动</div>
        </div>
        <el-tag type="info">{{ viewMode === '2.5d' ? '2.5D' : '2D' }}</el-tag>
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
      <div v-loading="loading" class="stage-scroll">
        <v-stage :config="stageConfig">
          <v-layer>
            <template v-for="area in visualAreas" :key="`area-${area.id}`">
              <v-rect
                v-if="viewMode === '2.5d'"
                :config="getAreaTopShadowConfig(area)"
              />
              <v-rect
                v-if="viewMode === '2.5d'"
                :config="getAreaSideShadowConfig(area)"
              />
              <v-rect :config="getAreaRectConfig(area)" @click="emit('select-area', area.id)" />
              <v-text :config="getAreaTitleConfig(area)" @click="emit('select-area', area.id)" />
            </template>

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

                <template v-for="cell in getCabinetGridCells(cabinet)" :key="cell.key">
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
        </v-stage>
      </div>
    </div>
  </el-card>
</template>

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

.stage-shell {
  min-height: 420px;
}

.stage-scroll {
  overflow: auto;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: linear-gradient(180deg, #fcfdff 0%, #f6faff 100%);
}

.visual-empty-state,
.visual-error-state {
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
