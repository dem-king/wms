import { describe, expect, it } from 'vitest'
import {
  applySavedLayoutToVisualModel,
  buildLayoutSavePayload,
  canUndo,
  canRedo,
  createInitialLayoutEditorState,
  hasPendingElementChanges,
  reduceLayoutEditorState,
} from './layout-editor'
import type { LayoutElementDraft } from './layout-editor'
import type { WarehouseVisualModel } from './visual-layout'

function createModel(): WarehouseVisualModel {
  return {
    warehouseId: '1',
    warehouseName: '一号库房',
    viewport: {
      width: 960,
      height: 640,
      areaGap: 24,
      cabinetGap: 12,
      padding: 24,
    },
    empty: null,
    summary: {
      totalAreas: 1,
      totalCabinets: 2,
      totalBins: 0,
      disabledAreas: 0,
      disabledCabinets: 0,
      disabledBins: 0,
    },
    areas: [
      { id: '10', areaName: '西区', areaCode: 'QY-01', sortOrder: 10, status: 1, cabinetIds: ['101', '102'], x: 24, y: 24, width: 420, height: 280, shapeType: null, polygonPoints: null, labelX: null, labelY: null },
    ],
    cabinets: {
      101: {
        id: '101',
        areaId: '10',
        cabinetName: 'A 柜',
        cabinetCode: 'CG-01',
        sortOrder: 10,
        status: 1,
        rows: 2,
        cols: 2,
        x: 48,
        y: 72,
        width: 180,
        height: 140,
        gridRowCount: 2,
        gridColCount: 2,
        binIds: [],
        cells: [[], []],
      },
      102: {
        id: '102',
        areaId: '10',
        cabinetName: 'B 柜',
        cabinetCode: 'CG-02',
        sortOrder: 20,
        status: 1,
        rows: 2,
        cols: 2,
        x: 252,
        y: 72,
        width: 180,
        height: 140,
        gridRowCount: 2,
        gridColCount: 2,
        binIds: [],
        cells: [[], []],
      },
    },
    bins: {},
    layoutElements: [],
    layoutWidth: 0,
    layoutHeight: 0,
    layoutScale: null,
    layoutBackgroundVersion: null,
  }
}

describe('layout editor state', () => {
  it('tracks pending cabinet positions and builds save payload for one area', () => {
    const initial = reduceLayoutEditorState(createInitialLayoutEditorState(), {
      type: 'set-edit-mode',
      enabled: true,
    })

    const next = reduceLayoutEditorState(initial, {
      type: 'move-cabinet',
      cabinetId: '101',
      positionX: 220,
      positionY: 160,
    })

    expect(next.pendingCabinetIds).toEqual(['101'])
    expect(buildLayoutSavePayload(createModel(), next, '10')).toEqual({
      areaId: '10',
      cabinets: [
        { id: '102', positionX: 204, positionY: 0, sortOrder: 10 },
        { id: '101', positionX: 172, positionY: 88, sortOrder: 20 },
      ],
    })
  })

  it('clears pending changes after a successful save feedback', () => {
    const changed = reduceLayoutEditorState(
      reduceLayoutEditorState(createInitialLayoutEditorState(), {
        type: 'set-edit-mode',
        enabled: true,
      }),
      {
        type: 'move-cabinet',
        cabinetId: '101',
        positionX: 220,
        positionY: 160,
      },
    )

    expect(
      reduceLayoutEditorState(changed, {
        type: 'save-success',
        message: '布局已保存',
      }),
    ).toEqual(expect.objectContaining({
      isEditMode: true,
      pendingPositions: {},
      pendingCabinetIds: [],
      isSaving: false,
      feedbackType: 'success',
      feedbackMessage: '布局已保存',
    }))
  })

  it('applies saved layout positions back to the visual model', () => {
    const nextModel = applySavedLayoutToVisualModel(createModel(), {
      areaId: '10',
      cabinets: [
        { id: '101', areaId: '10', cabinetName: 'A 柜', cabinetCode: 'CG-01', rows: 2, cols: 2, sortOrder: 20, status: 1, positionX: 172, positionY: 88, createTime: '2026-05-21 09:00:00' },
        { id: '102', areaId: '10', cabinetName: 'B 柜', cabinetCode: 'CG-02', rows: 2, cols: 2, sortOrder: 10, status: 1, positionX: 204, positionY: 0, createTime: '2026-05-21 09:00:00' },
      ],
    })

    expect(nextModel.areas[0].cabinetIds).toEqual(['102', '101'])
    expect(nextModel.cabinets['101'].x).toBe(220)
    expect(nextModel.cabinets['101'].y).toBe(160)
    expect(nextModel.cabinets['102'].x).toBe(252)
    expect(nextModel.cabinets['102'].y).toBe(72)
  })
})

describe('layout editor - element editing extension', () => {
  it('should set drawing mode and clear draft', () => {
    const state = reduceLayoutEditorState(createInitialLayoutEditorState(), {
      type: 'set-drawing-mode',
      elementType: 'wall',
    })

    expect(state.drawingMode).toBe('wall')
    expect(state.drawingDraft).toBeNull()
  })

  it('should clear drawing mode and draft when exiting edit mode', () => {
    let state = reduceLayoutEditorState(createInitialLayoutEditorState(), {
      type: 'set-edit-mode',
      enabled: true,
    })
    state = reduceLayoutEditorState(state, {
      type: 'set-drawing-mode',
      elementType: 'aisle',
    })
    state = reduceLayoutEditorState(state, {
      type: 'set-edit-mode',
      enabled: false,
    })

    expect(state.isEditMode).toBe(false)
    expect(state.drawingMode).toBeNull()
    expect(state.drawingDraft).toBeNull()
  })

  it('should set drawing draft', () => {
    const draft: LayoutElementDraft = {
      elementName: '墙体草稿',
      elementType: 'wall',
      shapeType: 'rect',
      positionX: 100,
      positionY: 200,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
    }

    const state = reduceLayoutEditorState(createInitialLayoutEditorState(), {
      type: 'set-drawing-draft',
      draft,
    })

    expect(state.drawingDraft).toEqual(draft)
  })

  it('should select element', () => {
    const state = reduceLayoutEditorState(createInitialLayoutEditorState(), {
      type: 'select-element',
      elementId: 'elem1',
    })

    expect(state.selectedElementId).toBe('elem1')
  })

  it('should add created element to pending and record undo', () => {
    const element = {
      warehouseId: '1',
      elementName: '新墙体',
      elementType: 'wall' as const,
      shapeType: 'rect' as const,
      positionX: 10,
      positionY: 20,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }

    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-created-element', element })

    expect(state.pendingElements.created).toHaveLength(1)
    expect(state.pendingElements.created[0].elementName).toBe('新墙体')
    expect(canUndo(state)).toBe(true)
    expect(canRedo(state)).toBe(false)
  })

  it('should add updated element to pending', () => {
    const element = {
      id: 'elem1',
      warehouseId: '1',
      elementName: '更新墙体',
      elementType: 'wall' as const,
      shapeType: 'line' as const,
      positionX: 10,
      positionY: 20,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }

    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-updated-element', element })

    expect(state.pendingElements.updated).toHaveLength(1)
    expect(hasPendingElementChanges(state)).toBe(true)
  })

  it('should add deleted element id to pending', () => {
    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-deleted-element-id', id: 'elem1' })

    expect(state.pendingElements.deletedIds).toEqual(['elem1'])
    expect(hasPendingElementChanges(state)).toBe(true)
  })

  it('should clear pending elements', () => {
    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-deleted-element-id', id: 'elem1' })
    state = reduceLayoutEditorState(state, { type: 'clear-pending-elements' })

    expect(state.pendingElements.created).toEqual([])
    expect(state.pendingElements.updated).toEqual([])
    expect(state.pendingElements.deletedIds).toEqual([])
  })

  it('should support undo and redo for element changes', () => {
    const element = {
      warehouseId: '1',
      elementName: '墙体',
      elementType: 'wall' as const,
      shapeType: 'rect' as const,
      positionX: 10,
      positionY: 20,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }

    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-created-element', element })
    expect(state.pendingElements.created).toHaveLength(1)

    // Undo
    state = reduceLayoutEditorState(state, { type: 'undo' })
    expect(state.pendingElements.created).toHaveLength(0)
    expect(canUndo(state)).toBe(false)
    expect(canRedo(state)).toBe(true)

    // Redo
    state = reduceLayoutEditorState(state, { type: 'redo' })
    expect(state.pendingElements.created).toHaveLength(1)
    expect(canUndo(state)).toBe(true)
    expect(canRedo(state)).toBe(false)
  })

  it('should clear undo/redo on save success', () => {
    const element = {
      warehouseId: '1',
      elementName: '墙体',
      elementType: 'wall' as const,
      shapeType: 'rect' as const,
      positionX: 10,
      positionY: 20,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }

    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-created-element', element })
    state = reduceLayoutEditorState(state, { type: 'save-success', message: '保存成功' })

    expect(canUndo(state)).toBe(false)
    expect(canRedo(state)).toBe(false)
    expect(hasPendingElementChanges(state)).toBe(false)
  })

  it('should discard all pending changes including element edits', () => {
    const element = {
      warehouseId: '1',
      elementName: '墙体',
      elementType: 'wall' as const,
      shapeType: 'rect' as const,
      positionX: 10,
      positionY: 20,
      layoutWidth: 300,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }

    let state = createInitialLayoutEditorState()
    state = reduceLayoutEditorState(state, { type: 'add-created-element', element })
    state = reduceLayoutEditorState(state, { type: 'discard-pending' })

    expect(state.pendingElements.created).toEqual([])
    expect(state.undoStack).toEqual([])
    expect(state.redoStack).toEqual([])
  })
})
