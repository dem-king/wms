import { describe, expect, it } from 'vitest'
import { buildWarehouseVisualModel, findVisualLocationByCode, VISUAL_STATUS_DISABLED } from './visual-layout'
import type { WmsAreaVo, WmsBinVo, WmsCabinetVo, WmsWarehouseVo } from '@/types/warehouse'

function createWarehouse(overrides: Partial<WmsWarehouseVo> = {}): WmsWarehouseVo {
  return {
    id: '1',
    warehouseName: '一号库房',
    warehouseCode: 'KF-001',
    address: 'A 区',
    manager: '管理员',
    phone: '13800000000',
    area: 100,
    status: 1,
    remark: '',
    createTime: '2026-05-21 09:00:00',
    ...overrides,
  }
}

function createArea(overrides: Partial<WmsAreaVo> = {}): WmsAreaVo {
  return {
    id: '10',
    warehouseId: '1',
    areaName: '西区',
    areaCode: 'QY-01',
    sortOrder: 10,
    status: 1,
    createTime: '2026-05-21 09:00:00',
    ...overrides,
  }
}

function createCabinet(overrides: Partial<WmsCabinetVo> = {}): WmsCabinetVo {
  return {
    id: '100',
    areaId: '10',
    cabinetName: 'A 柜',
    cabinetCode: 'CG-01',
    rows: 2,
    cols: 3,
    sortOrder: 10,
    status: 1,
    createTime: '2026-05-21 09:00:00',
    ...overrides,
  }
}

function createBin(overrides: Partial<WmsBinVo> = {}): WmsBinVo {
  return {
    id: '1000',
    cabinetId: '100',
    binCode: 'KW-01',
    rowNum: 1,
    colNum: 1,
    binStatus: 1,
    createTime: '2026-05-21 09:00:00',
    ...overrides,
  }
}

describe('warehouse visual layout builder', () => {
  it('groups cabinets by area and derives a stable bin grid', () => {
    const warehouse = createWarehouse()
    const areas = [
      createArea({ id: '20', areaName: '东区', sortOrder: 20 }),
      createArea({ id: '10', areaName: '西区', sortOrder: 10 }),
    ]
    const cabinets = [
      createCabinet({ id: '101', areaId: '10', cabinetName: 'A 柜', sortOrder: 20 }),
      createCabinet({ id: '102', areaId: '10', cabinetName: 'B 柜', sortOrder: 10, rows: 2, cols: 2 }),
      createCabinet({ id: '201', areaId: '20', cabinetName: 'C 柜', sortOrder: 10, rows: 1, cols: 2 }),
    ]
    const bins = [
      createBin({ id: '1', cabinetId: '102', rowNum: 2, colNum: 1, binCode: 'B-2-1' }),
      createBin({ id: '2', cabinetId: '102', rowNum: 1, colNum: 1, binCode: 'B-1-1' }),
      createBin({ id: '3', cabinetId: '102', rowNum: 1, colNum: 2, binCode: 'B-1-2', binStatus: VISUAL_STATUS_DISABLED }),
      createBin({ id: '4', cabinetId: '201', rowNum: 1, colNum: 2, binCode: 'C-1-2' }),
    ]

    const model = buildWarehouseVisualModel({ warehouse, areas, cabinets, bins })

    expect(model.empty).toBeNull()
    expect(model.areas.map(area => area.id)).toEqual(['10', '20'])
    expect(model.areas[0].cabinetIds).toEqual(['102', '101'])
    expect(model.cabinets['102'].gridRowCount).toBe(2)
    expect(model.cabinets['102'].gridColCount).toBe(2)
    expect(model.cabinets['102'].cells[0][0]?.binCode).toBe('B-1-1')
    expect(model.cabinets['102'].cells[0][1]?.status).toBe(VISUAL_STATUS_DISABLED)
    expect(model.summary.totalAreas).toBe(2)
    expect(model.summary.totalCabinets).toBe(3)
    expect(model.summary.totalBins).toBe(4)
  })

  it('returns an explicit empty state when the warehouse has no areas', () => {
    const model = buildWarehouseVisualModel({
      warehouse: createWarehouse(),
      areas: [],
      cabinets: [],
      bins: [],
    })

    expect(model.empty).toEqual({
      title: '当前库房暂无区域数据',
      description: '请先在区域管理中维护区域，再查看 2D 布局。',
    })
  })

  it('throws a descriptive error when a cabinet references a missing area', () => {
    expect(() =>
      buildWarehouseVisualModel({
        warehouse: createWarehouse(),
        areas: [createArea({ id: '10' })],
        cabinets: [createCabinet({ id: '999', areaId: '88' })],
        bins: [],
      }),
    ).toThrow('存放柜 999 缺少所属区域')
  })

  it('locates target nodes by area, cabinet and bin code', () => {
    const model = buildWarehouseVisualModel({
      warehouse: createWarehouse(),
      areas: [createArea({ id: '10', areaCode: 'QY-WEST' })],
      cabinets: [createCabinet({ id: '100', areaId: '10', cabinetCode: 'CG-A01' })],
      bins: [createBin({ id: '1001', cabinetId: '100', binCode: 'KW-A-01' })],
    })

    expect(findVisualLocationByCode(model, 'qY-west')).toEqual({
      matchedType: 'area',
      areaId: '10',
      cabinetId: null,
      binId: null,
      matchedCode: 'QY-WEST',
    })
    expect(findVisualLocationByCode(model, 'CG-A01')).toEqual({
      matchedType: 'cabinet',
      areaId: '10',
      cabinetId: '100',
      binId: null,
      matchedCode: 'CG-A01',
    })
    expect(findVisualLocationByCode(model, 'KW-A-01')).toEqual({
      matchedType: 'bin',
      areaId: '10',
      cabinetId: '100',
      binId: '1001',
      matchedCode: 'KW-A-01',
    })
  })

  it('prefers persisted relative cabinet positions when building the visual model', () => {
    const model = buildWarehouseVisualModel({
      warehouse: createWarehouse(),
      areas: [createArea({ id: '10' })],
      cabinets: [
        createCabinet({ id: '100', areaId: '10', cabinetCode: 'CG-A01', positionX: 36, positionY: 18 }),
        createCabinet({ id: '101', areaId: '10', cabinetCode: 'CG-A02', positionX: 220, positionY: 96 }),
      ],
      bins: [],
    })

    expect(model.cabinets['100'].x).toBe(84)
    expect(model.cabinets['100'].y).toBe(90)
    expect(model.cabinets['101'].x).toBe(268)
    expect(model.cabinets['101'].y).toBe(168)
    expect(model.areas[0].height).toBeGreaterThanOrEqual(308)
  })
})
