import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualLocationMatch, WarehouseVisualModel } from './visual-layout'

export type VisualViewMode = '2d' | '2.5d'

const DEFAULT_VIEW_MODE: VisualViewMode = '2d'

export interface VisualSelectionState {
  selectedAreaId: EntityId | null
  selectedCabinetId: EntityId | null
  selectedBinId: EntityId | null
  highlightedAreaId: EntityId | null
  highlightedCabinetId: EntityId | null
  highlightedBinId: EntityId | null
  viewMode: VisualViewMode
}

export type VisualSelectionAction =
  | { type: 'select-area'; areaId: EntityId }
  | { type: 'select-cabinet'; cabinetId: EntityId }
  | { type: 'select-bin'; cabinetId: EntityId; binId: EntityId }
  | { type: 'set-view-mode'; viewMode: VisualViewMode }
  | { type: 'locate-match'; match: WarehouseVisualLocationMatch }
  | { type: 'reset' }

/**
 * 基于布局模型生成默认选中态，优先选中首个区域和其下第一个存放柜。
 */
export function createInitialVisualSelection(model: WarehouseVisualModel): VisualSelectionState {
  const firstArea = model.areas[0]
  if (!firstArea) {
    return createEmptySelection()
  }

  return {
    selectedAreaId: firstArea.id,
    selectedCabinetId: firstArea.cabinetIds[0] ?? null,
    selectedBinId: null,
    highlightedAreaId: null,
    highlightedCabinetId: null,
    highlightedBinId: null,
    viewMode: DEFAULT_VIEW_MODE,
  }
}

/**
 * 根据点击动作收敛新的区域、存放柜、库位选中态。
 */
export function reduceVisualSelection(
  model: WarehouseVisualModel,
  state: VisualSelectionState,
  action: VisualSelectionAction,
): VisualSelectionState {
  if (action.type === 'reset') {
    return createEmptySelection(state.viewMode)
  }

  if (action.type === 'set-view-mode') {
    return {
      ...state,
      viewMode: action.viewMode,
    }
  }

  if (action.type === 'locate-match') {
    return {
      selectedAreaId: action.match.areaId,
      selectedCabinetId: action.match.cabinetId,
      selectedBinId: action.match.binId,
      highlightedAreaId: action.match.areaId,
      highlightedCabinetId: action.match.cabinetId,
      highlightedBinId: action.match.binId,
      viewMode: state.viewMode ?? DEFAULT_VIEW_MODE,
    }
  }

  if (action.type === 'select-area') {
    return {
      selectedAreaId: action.areaId,
      selectedCabinetId: null,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: state.viewMode ?? DEFAULT_VIEW_MODE,
    }
  }

  if (action.type === 'select-cabinet') {
    const cabinet = model.cabinets[action.cabinetId]
    if (!cabinet) {
      return state
    }

    return {
      selectedAreaId: cabinet.areaId,
      selectedCabinetId: action.cabinetId,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: state.viewMode ?? DEFAULT_VIEW_MODE,
    }
  }

  const cabinet = model.cabinets[action.cabinetId]
  const bin = model.bins[action.binId]
  if (!cabinet || !bin) {
    return state
  }

  return {
    selectedAreaId: cabinet.areaId,
    selectedCabinetId: action.cabinetId,
    selectedBinId: action.binId,
    highlightedAreaId: null,
    highlightedCabinetId: null,
    highlightedBinId: null,
    viewMode: state.viewMode ?? DEFAULT_VIEW_MODE,
  }
}

function createEmptySelection(viewMode: VisualViewMode = DEFAULT_VIEW_MODE): VisualSelectionState {
  return {
    selectedAreaId: null,
    selectedCabinetId: null,
    selectedBinId: null,
    highlightedAreaId: null,
    highlightedCabinetId: null,
    highlightedBinId: null,
    viewMode,
  }
}
