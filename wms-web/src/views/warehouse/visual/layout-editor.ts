import type { WmsCabinetLayoutBatchSaveDto, WmsCabinetLayoutSaveVo } from '@/types/warehouse'
import {
  applyCabinetLayoutSaveResult,
  VISUAL_AREA_HEADER_HEIGHT,
  VISUAL_PADDING,
  type WarehouseVisualModel,
} from './visual-layout'

const LAYOUT_SORT_STEP = 10

export type LayoutFeedbackType = 'success' | 'error' | ''

export interface LayoutEditorPositionDraft {
  positionX: number
  positionY: number
}

export interface LayoutEditorState {
  isEditMode: boolean
  pendingPositions: Record<number, LayoutEditorPositionDraft>
  pendingCabinetIds: number[]
  isSaving: boolean
  feedbackType: LayoutFeedbackType
  feedbackMessage: string
}

export type LayoutEditorAction =
  | { type: 'set-edit-mode'; enabled: boolean }
  | { type: 'move-cabinet'; cabinetId: number; positionX: number; positionY: number }
  | { type: 'discard-pending' }
  | { type: 'save-start' }
  | { type: 'save-success'; message: string }
  | { type: 'save-failure'; message: string }
  | { type: 'clear-feedback' }

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
  areaId: number,
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
    .sort((left, right) => left.positionY - right.positionY || left.positionX - right.positionX || left.id - right.id)
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

function toPendingCabinetIds(pendingPositions: Record<number, LayoutEditorPositionDraft>): number[] {
  return Object.keys(pendingPositions)
    .map(value => Number(value))
    .sort((left, right) => left - right)
}
