import { describe, expect, it } from 'vitest'
import {
  createInitialVisualSelection,
  reduceVisualSelection,
  type VisualSelectionState,
} from './visual-state'
import type { WarehouseVisualLocationMatch, WarehouseVisualModel } from './visual-layout'

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
      totalAreas: 2,
      totalCabinets: 2,
      totalBins: 3,
      disabledAreas: 0,
      disabledCabinets: 0,
      disabledBins: 1,
    },
    areas: [
      { id: 10, areaName: '西区', areaCode: 'QY-01', sortOrder: 10, status: 1, cabinetIds: [101], x: 24, y: 24, width: 420, height: 280 },
      { id: 20, areaName: '东区', areaCode: 'QY-02', sortOrder: 20, status: 1, cabinetIds: [201], x: 468, y: 24, width: 420, height: 280 },
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
        x: 40,
        y: 72,
        width: 180,
        height: 140,
        gridRowCount: 2,
        gridColCount: 2,
        binIds: [1001, 1002],
        cells: [[{ id: 1001, cabinetId: 101, binCode: 'A-1-1', row: 1, col: 1, status: 1 } as any]],
      },
      201: {
        id: 201,
        areaId: 20,
        cabinetName: 'B 柜',
        cabinetCode: 'CG-02',
        sortOrder: 20,
        status: 1,
        rows: 1,
        cols: 1,
        x: 490,
        y: 72,
        width: 180,
        height: 140,
        gridRowCount: 1,
        gridColCount: 1,
        binIds: [2001],
        cells: [[{ id: 2001, cabinetId: 201, binCode: 'B-1-1', row: 1, col: 1, status: 1 } as any]],
      },
    },
    bins: {
      1001: { id: 1001, cabinetId: 101, binCode: 'A-1-1', row: 1, col: 1, status: 1 },
      1002: { id: 1002, cabinetId: 101, binCode: 'A-1-2', row: 1, col: 2, status: 0 },
      2001: { id: 2001, cabinetId: 201, binCode: 'B-1-1', row: 1, col: 1, status: 1 },
    },
  }
}

describe('warehouse visual selection reducer', () => {
  it('creates a default selection from the first area and cabinet', () => {
    const selection = createInitialVisualSelection(createModel())

    expect(selection).toEqual({
      selectedAreaId: 10,
      selectedCabinetId: 101,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    })
  })

  it('keeps selected area in sync when cabinet and bin are clicked', () => {
    const initial: VisualSelectionState = {
      selectedAreaId: 10,
      selectedCabinetId: 101,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    }

    const afterCabinetClick = reduceVisualSelection(createModel(), initial, {
      type: 'select-cabinet',
      cabinetId: 201,
    })
    const afterBinClick = reduceVisualSelection(createModel(), afterCabinetClick, {
      type: 'select-bin',
      cabinetId: 201,
      binId: 2001,
    })

    expect(afterCabinetClick).toEqual({
      selectedAreaId: 20,
      selectedCabinetId: 201,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    })
    expect(afterBinClick).toEqual({
      selectedAreaId: 20,
      selectedCabinetId: 201,
      selectedBinId: 2001,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    })
  })

  it('clears cabinet and bin selection when the user switches area', () => {
    const initial: VisualSelectionState = {
      selectedAreaId: 20,
      selectedCabinetId: 201,
      selectedBinId: 2001,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    }

    expect(
      reduceVisualSelection(createModel(), initial, {
        type: 'select-area',
        areaId: 10,
      }),
    ).toEqual({
      selectedAreaId: 10,
      selectedCabinetId: null,
      selectedBinId: null,
      highlightedAreaId: null,
      highlightedCabinetId: null,
      highlightedBinId: null,
      viewMode: '2d',
    })
  })

  it('stores the selected workbench view mode', () => {
    expect(
      reduceVisualSelection(createModel(), createInitialVisualSelection(createModel()), {
        type: 'set-view-mode',
        viewMode: '2.5d',
      }),
    ).toMatchObject({
      viewMode: '2.5d',
      selectedAreaId: 10,
      selectedCabinetId: 101,
    })
  })

  it('applies quick locate result as both selection and highlight chain', () => {
    const match: WarehouseVisualLocationMatch = {
      matchedType: 'bin',
      areaId: 10,
      cabinetId: 101,
      binId: 1002,
      matchedCode: 'A-1-2',
    }

    expect(
      reduceVisualSelection(createModel(), createInitialVisualSelection(createModel()), {
        type: 'locate-match',
        match,
      }),
    ).toEqual({
      selectedAreaId: 10,
      selectedCabinetId: 101,
      selectedBinId: 1002,
      highlightedAreaId: 10,
      highlightedCabinetId: 101,
      highlightedBinId: 1002,
      viewMode: '2d',
    })
  })
})
