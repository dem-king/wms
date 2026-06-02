import type { EntityId, WmsCabinetLayoutBatchSaveDto, WmsCabinetLayoutSaveVo } from '@/types/warehouse'
import {
  applyCabinetLayoutSaveResult,
  VISUAL_AREA_HEADER_HEIGHT,
  VISUAL_PADDING,
  type WarehouseVisualModel,
} from './visual-layout'
import type { LayoutElementDto, LayoutElementUpdateItemDto, ElementType, ShapeType } from './types/layout-element'

const LAYOUT_SORT_STEP = 10
/** 撤销/重做栈最大深度 */
const UNDO_STACK_MAX_DEPTH = 50

export type LayoutFeedbackType = 'success' | 'error' | ''

export interface LayoutEditorPositionDraft {
  positionX: number
  positionY: number
}

/** 布局元素草稿（绘制中的临时元素） */
export interface LayoutElementDraft {
  /** 元素名称 */
  elementName: string
  /** 元素类型 */
  elementType: ElementType
  /** 形状类型 */
  shapeType: ShapeType
  /** X坐标 */
  positionX: number
  /** Y坐标 */
  positionY: number
  /** 宽度 */
  layoutWidth: number
  /** 高度 */
  layoutHeight: number
  /** 旋转角度 */
  rotation: number
  /** 点位数据JSON */
  pointData: string
  /** 样式数据JSON */
  styleData: string
  /** 展示文本 */
  labelText: string
}

/** 待保存的元素变更 */
export interface PendingElements {
  /** 新增元素列表 */
  created: LayoutElementDto[]
  /** 更新元素列表 */
  updated: LayoutElementUpdateItemDto[]
  /** 删除元素ID列表 */
  deletedIds: EntityId[]
}

/** 撤销/重做操作记录 */
export interface LayoutEditorUndoAction {
  /** 操作描述 */
  description: string
  /** 操作前的pendingElements快照 */
  before: PendingElements
  /** 操作后的pendingElements快照 */
  after: PendingElements
}

export interface LayoutEditorState {
  isEditMode: boolean
  pendingPositions: Record<string, LayoutEditorPositionDraft>
  pendingCabinetIds: EntityId[]
  isSaving: boolean
  feedbackType: LayoutFeedbackType
  feedbackMessage: string
  /** 当前绘制模式（null表示选择模式） */
  drawingMode: ElementType | null
  /** 绘制中的草稿元素 */
  drawingDraft: LayoutElementDraft | null
  /** 待保存的元素变更 */
  pendingElements: PendingElements
  /** 撤销栈 */
  undoStack: LayoutEditorUndoAction[]
  /** 重做栈 */
  redoStack: LayoutEditorUndoAction[]
  /** 当前选中的布局元素ID */
  selectedElementId: EntityId | null
}

export type LayoutEditorAction =
  | { type: 'set-edit-mode'; enabled: boolean }
  | { type: 'move-cabinet'; cabinetId: EntityId; positionX: number; positionY: number }
  | { type: 'discard-pending' }
  | { type: 'save-start' }
  | { type: 'save-success'; message: string }
  | { type: 'save-failure'; message: string }
  | { type: 'clear-feedback' }
  | { type: 'set-drawing-mode'; elementType: ElementType | null }
  | { type: 'set-drawing-draft'; draft: LayoutElementDraft | null }
  | { type: 'select-element'; elementId: EntityId | null }
  | { type: 'add-created-element'; element: LayoutElementDto }
  | { type: 'add-updated-element'; element: LayoutElementUpdateItemDto }
  | { type: 'add-deleted-element-id'; id: EntityId }
  | { type: 'clear-pending-elements' }
  | { type: 'undo' }
  | { type: 'redo' }

/**
 * 创建布局编辑器默认状态。
 */
export function createInitialLayoutEditorState(): LayoutEditorState {
  return {
    isEditMode: false,
    pendingPositions: {},
    pendingCabinetIds: [],
    isSaving: false,
    feedbackType: '',
    feedbackMessage: '',
    drawingMode: null,
    drawingDraft: null,
    pendingElements: { created: [], updated: [], deletedIds: [] },
    undoStack: [],
    redoStack: [],
    selectedElementId: null,
  }
}

/**
 * 根据编辑动作收敛布局编辑状态。
 */
export function reduceLayoutEditorState(
  state: LayoutEditorState,
  action: LayoutEditorAction,
): LayoutEditorState {
  if (action.type === 'set-edit-mode') {
    return {
      ...state,
      isEditMode: action.enabled,
      // 退出编辑模式时清除绘制状态
      drawingMode: action.enabled ? state.drawingMode : null,
      drawingDraft: action.enabled ? state.drawingDraft : null,
    }
  }

  if (action.type === 'move-cabinet') {
    const pendingPositions = {
      ...state.pendingPositions,
      [action.cabinetId]: {
        positionX: action.positionX,
        positionY: action.positionY,
      },
    }

    return {
      ...state,
      pendingPositions,
      pendingCabinetIds: toPendingCabinetIds(pendingPositions),
      feedbackType: '',
      feedbackMessage: '',
    }
  }

  if (action.type === 'discard-pending') {
    return {
      ...state,
      pendingPositions: {},
      pendingCabinetIds: [],
      isSaving: false,
      feedbackType: '',
      feedbackMessage: '',
      pendingElements: { created: [], updated: [], deletedIds: [] },
      undoStack: [],
      redoStack: [],
    }
  }

  if (action.type === 'save-start') {
    return {
      ...state,
      isSaving: true,
      feedbackType: '',
      feedbackMessage: '',
    }
  }

  if (action.type === 'save-success') {
    return {
      ...state,
      pendingPositions: {},
      pendingCabinetIds: [],
      isSaving: false,
      feedbackType: 'success',
      feedbackMessage: action.message,
      pendingElements: { created: [], updated: [], deletedIds: [] },
      undoStack: [],
      redoStack: [],
    }
  }

  if (action.type === 'save-failure') {
    return {
      ...state,
      isSaving: false,
      feedbackType: 'error',
      feedbackMessage: action.message,
    }
  }

  // 设置绘制模式
  if (action.type === 'set-drawing-mode') {
    return {
      ...state,
      drawingMode: action.elementType,
      drawingDraft: null,
    }
  }

  // 设置绘制草稿
  if (action.type === 'set-drawing-draft') {
    return {
      ...state,
      drawingDraft: action.draft,
    }
  }

  // 选中布局元素
  if (action.type === 'select-element') {
    return {
      ...state,
      selectedElementId: action.elementId,
    }
  }

  // 新增待创建元素
  if (action.type === 'add-created-element') {
    const before = { ...state.pendingElements }
    const after: PendingElements = {
      ...state.pendingElements,
      created: [...state.pendingElements.created, action.element],
    }
    return {
      ...state,
      pendingElements: after,
      undoStack: pushUndoStack(state.undoStack, { description: '新增元素', before, after }),
      redoStack: [],
    }
  }

  // 新增待更新元素
  if (action.type === 'add-updated-element') {
    const before = { ...state.pendingElements }
    const after: PendingElements = {
      ...state.pendingElements,
      updated: [...state.pendingElements.updated, action.element],
    }
    return {
      ...state,
      pendingElements: after,
      undoStack: pushUndoStack(state.undoStack, { description: '更新元素', before, after }),
      redoStack: [],
    }
  }

  // 新增待删除元素ID
  if (action.type === 'add-deleted-element-id') {
    const before = { ...state.pendingElements }
    const after: PendingElements = {
      ...state.pendingElements,
      deletedIds: [...state.pendingElements.deletedIds, action.id],
    }
    return {
      ...state,
      pendingElements: after,
      undoStack: pushUndoStack(state.undoStack, { description: '删除元素', before, after }),
      redoStack: [],
    }
  }

  // 清除待保存元素
  if (action.type === 'clear-pending-elements') {
    return {
      ...state,
      pendingElements: { created: [], updated: [], deletedIds: [] },
    }
  }

  // 撤销
  if (action.type === 'undo') {
    if (state.undoStack.length === 0) {
      return state
    }
    const lastAction = state.undoStack[state.undoStack.length - 1]
    return {
      ...state,
      pendingElements: lastAction.before,
      undoStack: state.undoStack.slice(0, -1),
      redoStack: [...state.redoStack, lastAction],
    }
  }

  // 重做
  if (action.type === 'redo') {
    if (state.redoStack.length === 0) {
      return state
    }
    const lastAction = state.redoStack[state.redoStack.length - 1]
    return {
      ...state,
      pendingElements: lastAction.after,
      undoStack: [...state.undoStack, lastAction],
      redoStack: state.redoStack.slice(0, -1),
    }
  }

  return {
    ...state,
    feedbackType: '',
    feedbackMessage: '',
  }
}

/**
 * 基于当前编辑状态生成布局保存请求。
 */
export function buildLayoutSavePayload(
  model: WarehouseVisualModel,
  state: LayoutEditorState,
  areaId: EntityId,
): WmsCabinetLayoutBatchSaveDto {
  const area = model.areas.find(item => item.id === areaId)
  if (!area) {
    throw new Error('当前区域不存在，无法保存布局')
  }

  const cabinets = area.cabinetIds
    .map(cabinetId => model.cabinets[cabinetId])
    .filter((cabinet): cabinet is NonNullable<typeof cabinet> => Boolean(cabinet))
    .map((cabinet) => {
      const draft = state.pendingPositions[cabinet.id]

      return {
        id: cabinet.id,
        positionX: draft?.positionX ?? cabinet.x,
        positionY: draft?.positionY ?? cabinet.y,
      }
    })
    .sort((left, right) => left.positionY - right.positionY || left.positionX - right.positionX || left.id.localeCompare(right.id))
    .map((cabinet, index) => ({
      id: cabinet.id,
      positionX: Math.max(cabinet.positionX - area.x - VISUAL_PADDING, 0),
      positionY: Math.max(cabinet.positionY - area.y - VISUAL_AREA_HEADER_HEIGHT, 0),
      sortOrder: (index + 1) * LAYOUT_SORT_STEP,
    }))

  return {
    areaId,
    cabinets,
  }
}

/**
 * 将保存结果回写到当前布局模型。
 */
export function applySavedLayoutToVisualModel(
  model: WarehouseVisualModel,
  result: WmsCabinetLayoutSaveVo,
): WarehouseVisualModel {
  return applyCabinetLayoutSaveResult(model, result)
}

/**
 * 是否有可撤销的操作
 */
export function canUndo(state: LayoutEditorState): boolean {
  return state.undoStack.length > 0
}

/**
 * 是否有可重做的操作
 */
export function canRedo(state: LayoutEditorState): boolean {
  return state.redoStack.length > 0
}

/**
 * 是否有待保存的布局元素变更
 */
export function hasPendingElementChanges(state: LayoutEditorState): boolean {
  const { created, updated, deletedIds } = state.pendingElements
  return created.length > 0 || updated.length > 0 || deletedIds.length > 0
}

/** 压入撤销栈（限制最大深度） */
function pushUndoStack(
  stack: LayoutEditorUndoAction[],
  action: LayoutEditorUndoAction,
): LayoutEditorUndoAction[] {
  const newStack = [...stack, action]
  // 超过最大深度时移除最早的记录
  if (newStack.length > UNDO_STACK_MAX_DEPTH) {
    return newStack.slice(newStack.length - UNDO_STACK_MAX_DEPTH)
  }
  return newStack
}

function toPendingCabinetIds(pendingPositions: Record<string, LayoutEditorPositionDraft>): EntityId[] {
  return Object.keys(pendingPositions)
    .sort((left, right) => left.localeCompare(right))
}
