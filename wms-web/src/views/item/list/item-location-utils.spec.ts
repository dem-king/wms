import { describe, expect, it } from 'vitest'
import {
  buildLocationText,
  extractBinIds,
  locationsToCascaderValue,
} from './item-location-utils'
import type { ItemLocationVo } from '@/types/item'

describe('item-location-utils', () => {
  it('builds readable location text from warehouse path and bin code', () => {
    const location: ItemLocationVo = {
      warehouseId: '1',
      warehouseName: '一号仓',
      areaId: '2',
      areaName: '一区',
      cabinetId: '3',
      cabinetName: 'A柜',
      binId: '4',
      binCode: 'BIN-04',
      locationText: '',
    }

    expect(buildLocationText(location)).toBe('一号仓/一区/A柜/BIN-04')
  })

  it('extracts leaf bin ids from cascader paths', () => {
    expect(extractBinIds([
      ['1', '2', '3', '4'],
      ['1', '2', '3', '5'],
    ])).toEqual(['4', '5'])
  })

  it('maps item locations to cascader paths for edit echo', () => {
    const locations: ItemLocationVo[] = [{
      warehouseId: '1',
      warehouseName: '一号仓',
      areaId: '2',
      areaName: '一区',
      cabinetId: '3',
      cabinetName: 'A柜',
      binId: '4',
      binCode: 'BIN-04',
      locationText: '一号仓/一区/A柜/BIN-04',
    }]

    expect(locationsToCascaderValue(locations)).toEqual([['1', '2', '3', '4']])
  })
})
