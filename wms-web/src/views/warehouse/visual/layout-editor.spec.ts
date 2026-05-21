import { describe, expect, it } from 'vitest'
import {
  applySavedLayoutToVisualModel,
  buildLayoutSavePayload,
  createInitialLayoutEditorState,
  reduceLayoutEditorState,
} from './layout-editor'
import type { LayoutEditorState } from './layout-editor'
import type { WarehouseVisualModel } from './visual-layout'

function createModel(): WarehouseVisualModel {
  return {
    warehouseId: 1,
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
      { id: 10, areaName: '西区', areaCode: 'QY-01', sortOrder: 10, status: 1, cabinetIds: [101, 102], x: 24, y: 24, width: 420, height: 280 },
    ],
    cabinets: {
      101: {
        id: 101,
        areaId: 10,
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
        id: 102,
        areaId: 10,
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
      cabinetId: 101,
      positionX: 220,
      positionY: 160,
    })

    expect(next.pendingCabinetIds).toEqual([101])
    expect(buildLayoutSavePayload(createModel(), next, 10)).toEqual({
      areaId: 10,
      cabinets: [
        { id: 102, positionX: 204, positionY: 0, sortOrder: 10 },
        { id: 101, positionX: 172, positionY: 88, sortOrder: 20 },
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
        cabinetId: 101,
        positionX: 220,
        positionY: 160,
      },
    )

    expect(
      reduceLayoutEditorState(changed, {
        type: 'save-success',
        message: '布局已保存',
      }),
    ).toEqual<LayoutEditorState>({
      isEditMode: true,
      pendingPositions: {},
      pendingCabinetIds: [],
      isSaving: false,
      feedbackType: 'success',
      feedbackMessage: '布局已保存',
    })
  })

  it('applies saved layout positions back to the visual model', () => {
    const nextModel = applySavedLayoutToVisualModel(createModel(), {
      areaId: 10,
      cabinets: [
        { id: 101, areaId: 10, cabinetName: 'A 柜', cabinetCode: 'CG-01', rows: 2, cols: 2, sortOrder: 20, status: 1, positionX: 172, positionY: 88, createTime: '2026-05-21 09:00:00' },
        { id: 102, areaId: 10, cabinetName: 'B 柜', cabinetCode: 'CG-02', rows: 2, cols: 2, sortOrder: 10, status: 1, positionX: 204, positionY: 0, createTime: '2026-05-21 09:00:00' },
      ],
    })

    expect(nextModel.areas[0].cabinetIds).toEqual([102, 101])
    expect(nextModel.cabinets[101].x).toBe(220)
    expect(nextModel.cabinets[101].y).toBe(160)
    expect(nextModel.cabinets[102].x).toBe(252)
    expect(nextModel.cabinets[102].y).toBe(72)
  })
})
