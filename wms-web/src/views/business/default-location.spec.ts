import { describe, expect, it } from 'vitest'
import {
  getSelectableLocations,
  locationToPath,
  pathToBinId,
  pickDefaultLocation,
  resolveLocationPathByBinId,
} from './default-location'
import type { ItemLocationVo, WmsItemVo } from '@/types/item'

function createItem(locations: ItemLocationVo[]): WmsItemVo {
  return {
    id: '100',
    itemCode: 'WP001',
    itemName: 'Bearing',
    specModel: '6205',
    unit: 'pcs',
    categoryId: '1',
    categoryName: 'Spare',
    subCategoryId: '2',
    subCategoryName: 'Bearing',
    tagIds: [],
    tagNames: [],
    binIds: locations.map(location => location.binId),
    locations,
    supplierId: '1',
    supplierName: 'Default Supplier',
    stockLowerLimit: 0,
    stockUpperLimit: 0,
    replenishThreshold: 0,
    currentStock: 0,
    images: [],
    pinyinInitial: 'zc',
    status: 1,
    createTime: '2026-06-02T00:00:00',
  }
}

describe('default-location', () => {
  it('prefers the default location in the current warehouse', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(pickDefaultLocation(item, '2')?.binId).toBe('2222')
  })

  it('uses the first configured default location when no current warehouse matches', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(pickDefaultLocation(item, '3')?.binId).toBe('1111')
  })

  it('does not pick a default location from another warehouse when strict warehouse matching is required', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(pickDefaultLocation(item, '3', { strictWarehouse: true })).toBeUndefined()
  })

  it('returns all selectable locations in the current warehouse', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '1', areaId: '12', cabinetId: '122', binId: '1222' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(getSelectableLocations(item, '1', { strictWarehouse: true }).map(location => location.binId))
      .toEqual(['1111', '1222'])
  })

  it('returns no selectable locations from other warehouses when strict warehouse matching is required', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(getSelectableLocations(item, '3', { strictWarehouse: true })).toEqual([])
  })

  it('returns all selectable locations when no warehouse is selected', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(getSelectableLocations(item).map(location => location.binId)).toEqual(['1111', '2222'])
  })

  it('returns undefined when the item has no default locations', () => {
    expect(pickDefaultLocation(createItem([]), '1')).toBeUndefined()
  })

  it('uses the first configured default location when no current warehouse is selected', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(pickDefaultLocation(item)?.binId).toBe('1111')
  })

  it('maps default location paths to bin ids', () => {
    const path = locationToPath({ warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' })

    expect(path).toEqual(['1', '11', '111', '1111'])
    expect(pathToBinId(path)).toBe('1111')
  })

  it('resolves an existing detail bin id to a full location path', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
      { warehouseId: '2', areaId: '22', cabinetId: '222', binId: '2222' },
    ])

    expect(resolveLocationPathByBinId(item, '2222')).toEqual(['2', '22', '222', '2222'])
  })

  it('does not resolve an existing detail bin id when it belongs to another warehouse', () => {
    const item = createItem([
      { warehouseId: '1', areaId: '11', cabinetId: '111', binId: '1111' },
    ])

    expect(resolveLocationPathByBinId(item, '1111', '2')).toBeUndefined()
  })
})
