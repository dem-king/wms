/**
 * 库房可视化选中态管理
 * 管理区域、存放柜、库位的选中/高亮状态，以及视图模式切换
 */

import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualLocationMatch, WarehouseVisualModel } from './visual-layout'

/** 视图模式：3D视图 / 2D俯视图 / 柜子详情视图 */
export type VisualViewMode = '3d' | '2d' | 'detail'

/** 默认视图模式 */
const DEFAULT_VIEW_MODE: VisualViewMode = '3d'

/** 可视化选中状态 */
export interface VisualSelectionState {
  /** 选中的区域ID */
  selectedAreaId: EntityId | null
  /** 选中的存放柜ID */
  selectedCabinetId: EntityId | null
  /** 选中的库位ID */
  selectedBinId: EntityId | null
  /** 高亮的区域ID（编码搜索定位时使用） */
  highlightedAreaId: EntityId | null
  /** 高亮的存放柜ID（编码搜索定位时使用） */
  highlightedCabinetId: EntityId | null
  /** 高亮的库位ID（编码搜索定位时使用） */
  highlightedBinId: EntityId | null
  /** 当前视图模式 */
  viewMode: VisualViewMode
  /** 当前选中的布局元素ID */
  selectedElementId: EntityId | null
  /** 柜子详情视图中的柜子ID */
  detailCabinetId: EntityId | null
}

/** 可视化选中动作类型 */
export type VisualSelectionAction =
  | { type: 'select-area'; areaId: EntityId }
  | { type: 'select-cabinet'; cabinetId: EntityId }
  | { type: 'select-bin'; cabinetId: EntityId; binId: EntityId }
  | { type: 'set-view-mode'; viewMode: VisualViewMode }
  | { type: 'locate-match'; match: WarehouseVisualLocationMatch }
  | { type: 'select-element'; elementId: EntityId | null }
  | { type: 'enter-detail'; cabinetId: EntityId }
  | { type: 'exit-detail' }
  | { type: 'reset' }

/**
 * 基于布局模型生成默认选中态，优先选中首个区域和其下第一个存放柜。
 * @param model 库房可视化模型
 * @returns 初始选中状态
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
    selectedElementId: null,
    detailCabinetId: null,
  }
}

/**
 * 根据动作收敛新的区域、存放柜、库位选中态。
 * @param model 库房可视化模型
 * @param state 当前选中状态
 * @param action 触发的动作
 * @returns 新的选中状态
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
      selectedElementId: null,
      detailCabinetId: null,
    }
  }

  // 选中布局元素
  if (action.type === 'select-element') {
    return {
      ...state,
      selectedElementId: action.elementId,
      // 选中元素时清除区域/存放柜/库位选中
      ...(action.elementId ? {
        selectedAreaId: null,
        selectedCabinetId: null,
        selectedBinId: null,
      } : {}),
    }
  }

  // 进入柜子详情视图
  if (action.type === 'enter-detail') {
    const cabinet = model.cabinets[action.cabinetId]
    if (!cabinet) {
      return state
    }
    return {
      ...state,
      selectedCabinetId: action.cabinetId,
      selectedAreaId: cabinet.areaId,
      viewMode: 'detail',
      detailCabinetId: action.cabinetId,
      selectedElementId: null,
    }
  }

  // 退出柜子详情视图
  if (action.type === 'exit-detail') {
    return {
      ...state,
      viewMode: '3d',
      detailCabinetId: null,
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
      selectedElementId: null,
      detailCabinetId: null,
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
      selectedElementId: null,
      detailCabinetId: null,
    }
  }

  // select-bin
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
    selectedElementId: null,
    detailCabinetId: null,
  }
}

/**
 * 创建空选中状态
 * @param viewMode 视图模式
 * @returns 空选中状态
 */
function createEmptySelection(viewMode: VisualViewMode = DEFAULT_VIEW_MODE): VisualSelectionState {
  return {
    selectedAreaId: null,
    selectedCabinetId: null,
    selectedBinId: null,
    highlightedAreaId: null,
    highlightedCabinetId: null,
    highlightedBinId: null,
    viewMode,
    selectedElementId: null,
    detailCabinetId: null,
  }
}
